package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.springframework.transaction.TransactionStatus;

public class GestoreMERIC extends AbstractGestoreChiaveIDAutoincrementante {

  @Override
  public String getCampoNumericoChiave() {
    return "ID";
  }

  @Override
  public String getEntita() {
    return "MERIC";
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    super.preInsert(status, datiForm);


    if(geneManager.isCodificaAutomatica("MERIC", "CODRIC")){
      String codric = geneManager.calcolaCodificaAutomatica("MERIC", "CODRIC");
      datiForm.setValue("MERIC.CODRIC", codric);
    } else {
      String codric = datiForm.getString("MERIC.CODRIC");
      try {
        List ret = this.sqlManager.getVector(
            "select 1 from meric where codric = ?",
            new Object[] { codric });
        if (ret != null && ret.size() > 0)
          throw new GestoreException(
              "Il codice della ricerca di mercato è già esistente",
              "verificaRicercaMercato.duplicato");
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella verifica dell'unicità del codice della ricerca di mercato",
            "verificaRicercaMercato", e);
      }
    }

    //Inserimento in G_PERMESSI solo per l'utente corrente
    ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long id =datiForm.getLong("MERIC.ID");
    Long idUtente = new Long(profilo.getId());
    String ruoloME = profilo.getRuoloUtenteMercatoElettronico();
    try {
      // si inserisce l'utente solo se non esiste l'associazione nella
      // G_PERMESSI con l'entita'
      Vector ret = this.sqlManager.getVector(
          "select count(numper) from g_permessi where idmeric"
              + " = ? and syscon = ?", new Object[] { id, idUtente });
      if (ret.size() > 0) {
        Long count = SqlManager.getValueFromVectorParam(ret, 0).longValue();
        if (count != null && count.longValue() == 0) {
          // non esiste, quindi tento l'inserimento
          long maxNumper = this.getMaxIdGPermessi() + 1;
          String sql = "insert into g_permessi (numper, syscon, idmeric"
              + ", autori, propri, meruolo) values (?, ?, ?, ?, ?, ?)";
          //Per adesso metto come valore fisso di meruolo il valore 2
          Long meruolo=new Long(2);
          if(ruoloME!=null && !"".equals(ruoloME))
            meruolo= new Long(ruoloME);
          this.sqlManager.update(sql, new Object[] { new Long(maxNumper), idUtente,
              id, new Long(1), "1",meruolo }, 1);
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'inserimento di un'occorrenza nella G_PERMESSI",
          "insertPermesso", e);
    }

  }

  @Override
  public void afterInsertEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    // Gestione delle sezioni 'Atti autorizzativi'
    //Si deve valorizzare il campo MERICATT.IDRIC dei record delle sezioni multiple
    Long id =datiForm.getLong("MERIC.ID");
    int numeroRecord = datiForm.getLong("NUMERO_ATAU").intValue();
    for (int i = 1; i <= numeroRecord; i++) {
      if(datiForm.isColumn("MERICATT.MOD_ATAU_" + i) && "1".equals(datiForm.getString("MERICATT.MOD_ATAU_" + i))){
        datiForm.addColumn("MERICATT.IDRIC_" + i, JdbcParametro.TIPO_NUMERICO, id);
      }
    }
    AbstractGestoreChiaveIDAutoincrementante gestoreMERICATT = new DefaultGestoreEntitaChiaveIDAutoincrementante(
        "MERICATT", "ID", this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
        gestoreMERICATT, "ATAU",
        new DataColumn[] { datiForm.getColumn("MERIC.ID") }, null);
  }


  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    Long id =datiForm.getLong("MERIC.ID");

    //Se per la ricerca di mercato sono stati generati degli ordini non si può cancellare l'occorrenza
    try {
      Long numOrdini = (Long)sqlManager.getObject("select count(ngara) from gare where idric=? and genere=4", new Object[]{id});
      if(numOrdini!=null && numOrdini.longValue()>0){
        throw new GestoreException(
            "Non è possibile cancellare la ricerca di mercato, poichè vi sono degli ordini associati",
            "bloccoCancellazione");
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella verifica delle presenza degli ordini della ricerca di mercato con id=" + id.toString(),
          null, e);
    }

    //Cancellazione della G_PERMESSI
    try {
      sqlManager.update("delete from G_PERMESSI where idmeric = ?", new Object[] { id });
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella cancellazione della G_PERMESSI della ricerca di mercato con id=" + id.toString(),
          null, e);
    }

  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    Long id =datiForm.getLong("MERIC.ID");
    //Non è possibile modificare il catalogo elettronico se ci sono degli articoli in carrello
    if(datiForm.isModifiedColumn("MERIC.CODCATA")){
      long occorrenzeMericart = geneManager.countOccorrenze("MERICART", "idric=?", new Object[]{id});
      if(occorrenzeMericart>0){
        throw new GestoreException(
            "Non è possibile modificare il catalogo poichè vi sono articoli in carrello",
            "modificaCatalogo.articoliInCarrello");
      }
    }

    // Gestione delle sezioni 'Atti autorizzativi'
    AbstractGestoreChiaveIDAutoincrementante gestoreMERICATT = new DefaultGestoreEntitaChiaveIDAutoincrementante(
        "MERICATT", "ID", this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
        gestoreMERICATT, "ATAU",
        new DataColumn[] { datiForm.getColumn("MERIC.ID") }, null);

    //Se si modifica la stazione appaltante si deve allineare il valore degli ordini di acquisto generati
    if(datiForm.isColumn("MERIC.CENINT") && datiForm.isModifiedColumn("MERIC.CENINT")){
      String select="select ngara from v_oda where ngara is not null and idric =?";
      String cenint = datiForm.getString("MERIC.CENINT");
      try {
        List listaOrdini = sqlManager.getListVector(select, new Object[]{id});
        if(listaOrdini!=null && listaOrdini.size()>0){
          for(int i=0; i<listaOrdini.size(); i++){
            String ngara = SqlManager.getValueFromVectorParam(listaOrdini.get(i), 0).stringValue();
            sqlManager.update("update torn set cenint=? where codgar=?", new Object[]{cenint, "$" + ngara});
          }
        }
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nell'aggiornamento della stazione appaltante dei prodotti",
            null, e);
      }
    }


  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

  /**
   * Ritorna l'ultimo id generato per la tabella G_PERMESSI
   *
   * @return ultimo id generato, 0 altrimenti
   * @throws GestoreException
   */
  private long getMaxIdGPermessi() throws GestoreException {
    long id = 0;
    try {
      Vector ret = this.sqlManager.getVector(
          "select max(numper) from g_permessi", new Object[] {});
      if (ret.size() > 0) {
        Long count = SqlManager.getValueFromVectorParam(ret, 0).longValue();
        if (count != null && count.longValue() > 0) {
          id = count.longValue();
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'estrazione dell'ultimo id utilizzato nella G_PERMESSI",
          "getMaxIdPermessi", e);
    }
    return id;
  }


}
