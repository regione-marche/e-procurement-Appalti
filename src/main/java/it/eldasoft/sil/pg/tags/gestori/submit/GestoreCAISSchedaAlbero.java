/*
 * Created on 18/dic/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import java.sql.SQLException;
import java.util.List;

import org.springframework.transaction.TransactionStatus;

public class GestoreCAISSchedaAlbero extends AbstractGestoreEntita {

  public String getEntita() {
    return "CAIS";
  }

  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    // E' necessario cancellare anche tutte le categorie figlie
    String caisim = datiForm.getString("CAIS.CAISIM");
    try {
      String deleteCAIS = "delete from cais where codliv1 = ? or codliv2 = ? or codliv3 = ? or codliv4 = ?";
      this.sqlManager.update(deleteCAIS, new String[] { caisim, caisim, caisim, caisim });
    } catch (SQLException e) {
      throw new GestoreException("Errore in fase di eliminazione categorie figlie di " + caisim, "cais.deleteNodo", e);
    }

  }

  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    String caisim = datiForm.getString("CAIS.CAISIM");

    // Gestione campo CAISORD. Il valore deve essere calcolato in relazione alla
    // tipologia.
    Long tiplavg = datiForm.getLong("CAIS.TIPLAVG");
    String selectCAIS = "select max(caisord) from cais where tiplavg=?";
    try {
      Long caisord = (Long) sqlManager.getObject(selectCAIS, new Object[] { tiplavg });
      if (caisord == null) {
        caisord = new Long(0);
      }
      caisord = new Long(caisord.longValue() + 1);
      datiForm.setValue("CAIS.CAISORD", caisord);
    } catch (SQLException e) {
      throw new GestoreException("Errore nella valorizzazione del campo CAISORD per la categoria:" + caisim, null, e);
    }

    this.controlloUnivocitaCategoria(caisim);

  }

  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    String caisim = datiForm.getString("CAIS.CAISIM");

    // Se si tenta di modificare il campo ISARCHI per rendere la categoria
    // "non archiviata". Tuttavia se la categoria corrente
    // è figlia di una categoria archiviata non è possibile modificarla.
    if (datiForm.isModifiedColumn("CAIS.ISARCHI") && !"1".equals(datiForm.getString("CAIS.ISARCHI"))) {
      try {
        if (this.isCategoriaPadreArchiviata(caisim)) {
          Exception e = new Exception();
          throw new GestoreException(
              "Non è possibile modificare la categoria a 'non archiviata' perche' alcune categorie padre della categoria"
                  + caisim
                  + " risultano ancora archiviate", "categorie.eliminaArchiviazioneFiglia", e);
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nella lettura del campo isarchi delle categorie padre della categoria: " + caisim, null, e);
      }
    }

    // Ogni modifica di ISARCHI deve essere propagata a tutte le categorie
    // figlie
    if (datiForm.isModifiedColumn("CAIS.ISARCHI")) {
      String isarchi = datiForm.getString("CAIS.ISARCHI");
      try {
        String update = "update cais set isarchi = ? where (caisim <> ?) and (codliv1 = ? or codliv2 = ? or codliv3 = ? or codliv4 = ?)";
        this.sqlManager.update(update, new Object[] { isarchi, caisim, caisim, caisim, caisim, caisim });
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'aggiornamento del campo ISARCHI delle categorie figlie della categoria: " + caisim, null, e);
      }
    }

    // Se si modifica il titolo della categoria padre bisogna propagarlo anche a tutte le figlie
    if (datiForm.isModifiedColumn("CAIS.TITCAT")) {
      String titcat = datiForm.getString("CAIS.TITCAT");
      try {
        String update = "update cais set titcat = ? where (caisim <> ?) and (codliv1 = ? or codliv2 = ? or codliv3 = ? or codliv4 = ?)";
        this.sqlManager.update(update, new Object[] { titcat, caisim, caisim, caisim, caisim, caisim });
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'aggiornamento del campo TITCAT delle categorie figlie della categoria: " + caisim, null, e);
      }
    }
    
    // La modifica al codice categoria deve essere propagata anche a tutte le
    // tabelle figlie
    if (datiForm.isModifiedColumn("CAIS.CAISIM")) {

      this.controlloUnivocitaCategoria(caisim);

      String caisim_originale = datiForm.getColumn("CAIS.CAISIM").getOriginalValue().getStringValue();

      // Aggiornamento del codice categoria di tutte le categorie figlie
      String livello = "";
      try {
        for (int il = 0; il < 4; il++) {
          livello = String.valueOf(il + 1);
          this.sqlManager.update("update CAIS set CODLIV" + livello + " = ? where CODLIV" + livello + " = ?", new Object[] { caisim,
              caisim_originale });
        }
      } catch (SQLException el) {
        throw new GestoreException("Errore durante la modifica del codice categoria del campo CODLIV" + livello + " della tabella CAIS",
            "categorie.rinominaCodliv", new Object[] { livello }, el);
      }

      // Aggiornamento del codice categoria di tutte le tabelle associate
      String tabella[] = { "CATE", "ARCHDOCG", "OPES", "CATG", "ISCRIZCAT", "ISCRIZCLASSI", "CATAPP", "ULTAPP", "SUBA", "CPRIGHE",
          "CNRIGHE" };
      String fktabella[] = { "CATISC", "CATEGORIA", "CATOFF", "CATIGA", "CODCAT", "CODCAT", "CATIGA", "CATOFF", "CATLAV", "CATIGA", "CATEG" };

      for (int it = 0; it < tabella.length; it++) {
        if (this.sqlManager.isTable(tabella[it])) {
          try {
            this.sqlManager.update("update " + tabella[it] + " set " + fktabella[it] + " = ? where " + fktabella[it] + " = ?",
                new Object[] { caisim, caisim_originale });
          } catch (SQLException et) {
            throw new GestoreException("Errore durante la modifica del codice categoria del campo "
                + fktabella[it]
                + " della tabella "
                + tabella[it], "categorie.rinomina", new Object[] { fktabella[it], tabella[it] }, et);
          }
        }
      }

    }

  }

  /**
   * Controllo univocita' del codice categoria indicato.
   * 
   * @param caisim
   * @throws GestoreException
   */
  private void controlloUnivocitaCategoria(String caisim) throws GestoreException {

    try {
      Long tiplavg = (Long) this.sqlManager.getObject("select tiplavg from cais where caisim = ?", new Object[] { caisim });
      if (tiplavg != null && tiplavg.longValue() > 0) {
        String tab1desc = (String) this.sqlManager.getObject("select tab1desc from tab1 where tab1cod = ? and tab1tip = ?", new Object[] {
            "G_038", tiplavg });
        Exception e = new Exception();
        throw new GestoreException("Il codice categoria indicato e' gia' utilizzato nella tipologia '" + tab1desc + "'",
            "categorie.codiceDuplicato", new Object[] { tab1desc, caisim }, e);
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nel controllo sull'univocità del codice categoria:" + caisim, null, e);
    }
  }

  /**
   * Verifica l'esistenza di una categoria padre archiviata.
   * 
   * @param caisim
   * @return
   * @throws SQLException
   */
  private boolean isCategoriaPadreArchiviata(String caisim) throws SQLException {
    boolean isCategoriaPadreArchiviata = false;

    List<?> datiCAIS_P = this.sqlManager.getVector("select codliv1, codliv2, codliv3, codliv4 from cais where caisim = ?",
        new Object[] { caisim });
    if (datiCAIS_P != null && datiCAIS_P.size() > 0) {
      for (int liv = 0; liv < 4; liv++) {
        String caisim_liv = (String) SqlManager.getValueFromVectorParam(datiCAIS_P, liv).getValue();
        String isarchi_p = (String) sqlManager.getObject("select isarchi from cais where caisim = ?", new Object[] { caisim_liv });
        if (isarchi_p != null && "1".equals(isarchi_p)) isCategoriaPadreArchiviata = true;
      }
    }
    return isCategoriaPadreArchiviata;
  }
}
