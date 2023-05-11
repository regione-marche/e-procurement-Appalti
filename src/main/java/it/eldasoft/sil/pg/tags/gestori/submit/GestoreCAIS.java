/*
 * Created on 12/apr/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Vector;

import org.springframework.transaction.TransactionStatus;


/**
 * Gestore di eliminazione dei dati da CAIS.
 *
 * @author Stefano.Sabbadin
 */
public class GestoreCAIS extends AbstractGestoreEntita {

  /** Manager Integrazione WSERP */
  private GestioneWSERPManager gestioneWSERPManager;

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#getEntita()
   */
  @Override
  public String getEntita() {
    return "CAIS";
  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postDelete(it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postInsert(it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postUpdate(it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  /**
   * Si eliminano in cascata anche tutte le occorrenze di categorie figlie della categoria corrente.
   *
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#preDelete(org.springframework.transaction.TransactionStatus, it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {


    String pk = datiForm.getString("CAIS.CAISIM");
    try {
      this.sqlManager.update("DELETE FROM CAIS WHERE CODLIV1=? OR CODLIV2=? OR CODLIV3=? OR CODLIV4=?", new String[] {pk, pk, pk, pk});
    } catch (SQLException e) {
      throw new GestoreException("Errore in fase di eliminazione categorie figlie di " + pk, "cais.deleteNodo", e);
    }



  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#preInsert(org.springframework.transaction.TransactionStatus, it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    /*
    Una nuova categoria non può essere figlia di una categoria archiviata, quindi il problema non si pone
    //Se la categoria è una categoria figlia ed è stato impostato il campo isarchi a non archiviato, allora si deve controllare se vi sono delle categorie padre con isarchi=1 e se ciò
    //accade si deve bloccare il salvataggio
    String codiceCategoria = datiForm.getString("CAIS.CAISIM");
    boolean isEsisteCategoriaPadreArchiviata=false;
    try {
      isEsisteCategoriaPadreArchiviata = this.checkIsCategoriaPadreArchiviata(codiceCategoria);
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura del campo isarchi delle categorie padre della categoria:" + codiceCategoria, null, e);
    }
    if(isEsisteCategoriaPadreArchiviata){
      Exception e = new Exception();
      throw new GestoreException("Non è possibile togliere il segno di spunta dal flag 'Categoria archiviata' perchè alcune categorie padre della categoria " + codiceCategoria + " risultano archiviate", "categorie.eliminaArchiviazioneFiglia", e);
    }
    */
    String codiceCategoria = datiForm.getString("CAIS.CAISIM");

    //Valorizzazione di CAISORD
    Long tiplavg=datiForm.getLong("CAIS.TIPLAVG");
    String select="select max(caisord) from cais where tiplavg=?";
    try {
      Long caisordMax = (Long)sqlManager.getObject(select, new Object[]{tiplavg});
      //long maxCaisord=1;
      if(caisordMax!=null && caisordMax.longValue()>0)
        caisordMax=new Long(caisordMax.longValue()+1);

      datiForm.setValue("CAIS.CAISORD", caisordMax);
    } catch (SQLException e) {
      throw new GestoreException("Errore nella valorizzazione del campo CAISORD per la categoria:" + codiceCategoria, null, e);
    }

    //Il controllo sull'univocità del codice categoria viene fatto pure considerando i valori su DB di CAISIM che presenta
    //spazi a destra fino al raggiungimento del max caratteri (5) (Alcuni clienti hanno fatto ciò)
    select="select tiplavg from cais where caisim=? or caisim=?";
    //String spazi = UtilityStringhe.fillRight(codiceCategoria, ' ', 5 - codiceCategoria.length());
    String spazi ="";
    if(5 - codiceCategoria.length()>0){
      char[] vettoreCaratteri = new char[5 - codiceCategoria.length()];
      Arrays.fill(vettoreCaratteri, ' ');
      spazi = new String(vettoreCaratteri);
    }
    String codiceCategoriaConSpazi= codiceCategoria + spazi;
    try {
      tiplavg = (Long) this.sqlManager.getObject(select, new Object[]{codiceCategoria,codiceCategoriaConSpazi});
      if(tiplavg!= null && tiplavg.longValue()>0){
        String tipologia="";
        switch(tiplavg.intValue()){
        case 1:
          tipologia="per lavori";
          break;

        case 2:
          tipologia="per forniture";
          break;

        case 3:
          tipologia="per servizi";
          break;

        case 4:
          tipologia="per lavori fino a 150.000 euro";
          break;
        case 5:
          tipologia="per servizi professionali";
          break;
        }
        Exception e = new Exception();
        throw new GestoreException("Codice Duplicato. Esiste già una categoria " + tipologia + " con uguale codice " + codiceCategoria ,
            "categorie.codiceDuplicato",new Object[]{tipologia,codiceCategoria}, e);
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nel controllo sull'univocità del codice categoria:" + codiceCategoria, null, e);
    }

    //Se la categoria è una categoria figlia, allora si imposta come titolo quello della categoria padre di primo livello
    if(datiForm.isColumn("CAIS.CODLIV1")){
      String codiceCategoriaPrimoLivello = datiForm.getString("CAIS.CODLIV1");
      if(codiceCategoriaPrimoLivello!=null && !"".equals(codiceCategoriaPrimoLivello)){
        try {
          String titcat = (String)sqlManager.getObject("select titcat from cais where caisim=?", new Object[]{codiceCategoriaPrimoLivello});
          //sqlManager.update("update cais set titcat=? where caisim=?", new Object[]{titcat,codiceCategoria});
          datiForm.setValue("CAIS.TITCAT", titcat);
        } catch (SQLException e) {
          throw new GestoreException("Errore nella lettura del campo titcat della categoria:" + codiceCategoriaPrimoLivello, null, e);
        }
      }
    }
  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#preUpdate(org.springframework.transaction.TransactionStatus, it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    //Se la categoria è una categoria figlia ed è stato impostato il campo isarchi a non archiviato, allora si deve controllare se vi sono delle categorie padre con isarchi=1 e se ciò
    //accade si deve bloccare il salvataggio
    String codiceCategoria = datiForm.getString("CAIS.CAISIM");
    boolean isEsisteCategoriaPadreArchiviata=false;
    try {
      isEsisteCategoriaPadreArchiviata = this.checkIsCategoriaPadreArchiviata(codiceCategoria);
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura del campo isarchi delle categorie padre della categoria:" + codiceCategoria, null, e);
    }
    if(isEsisteCategoriaPadreArchiviata){
      Exception e = new Exception();
      throw new GestoreException("Non è possibile togliere il segno di spunta dal flag 'Categoria archiviata' perchè alcune categorie padre della categoria" + codiceCategoria + " risultano archiviate", "categorie.eliminaArchiviazioneFiglia", e);
    }

    String isPadre=this.getRequest().getParameter("isPadre");
    //Se la categoria ha delle categorie figlie, si deve impedire si salvare se è stato modificato il livello
    if(isPadre!= null && "true".equals(isPadre) && (datiForm.isModifiedColumn("CAIS.CODLIV1") || datiForm.isModifiedColumn("CAIS.CODLIV2") || datiForm.isModifiedColumn("CAIS.CODLIV3"))){
      Exception e = new Exception();
      throw new GestoreException("Non è possibile modificare il livello della categoria" + codiceCategoria + " in quanto padre di altre categorie", "categorie.modificaLivelloPadre", e);
    }

    //Se la categoria è una categoria padre ed è stato modificato isarchi, allora si deve aggiornare il valore di isarchi di tutte le categorie figlie
    String isarchi = datiForm.getString("CAIS.ISARCHI");
    if(isPadre!= null && "true".equals(isPadre) && datiForm.isModifiedColumn("CAIS.ISARCHI")){
      String update="update cais set isarchi=? where (caisim<>?) and (codliv1=? or codliv2=? or codliv3=? or codliv4=?)";
      try {
        sqlManager.update(update, new Object[]{isarchi,codiceCategoria,codiceCategoria,codiceCategoria,codiceCategoria,codiceCategoria});
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'aggiornamento del campo isarchi delle categorie figlie della categoria:" + codiceCategoria, null, e);
      }

    }

    //Se la categoria è una categoria padre ed è stato modificato il titolo, allora si deve aggiornare il valore del titolo di tutte le categorie figlie
    String titcat = datiForm.getString("CAIS.TITCAT");
    if(isPadre!= null && "true".equals(isPadre) && datiForm.isModifiedColumn("CAIS.TITCAT")){
      String update="update cais set titcat=? where (caisim<>?) and (codliv1=? or codliv2=? or codliv3=? or codliv4=?)";
      try {
        sqlManager.update(update, new Object[]{titcat,codiceCategoria,codiceCategoria,codiceCategoria,codiceCategoria,codiceCategoria});
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'aggiornamento del campo titcat delle categorie figlie della categoria:" + codiceCategoria, null, e);
      }

    }

    /*
    //Se la categoria è una categoria figlia, allora si imposta come titolo quello della categoria padre di primo livello
    if(datiForm.isColumn("CAIS.CODLIV1")){
      String codiceCategoriaPrimoLivello = datiForm.getString("CAIS.CODLIV1");
      if(codiceCategoriaPrimoLivello!=null && !"".equals(codiceCategoriaPrimoLivello)){
        try {
          titcat = (String)sqlManager.getObject("select titcat from cais where caisim=?", new Object[]{codiceCategoriaPrimoLivello});
          //sqlManager.update("update cais set titcat=? where caisim=?", new Object[]{titcat,codiceCategoria});
          datiForm.setValue("CAIS.TITCAT", titcat);
        } catch (SQLException e) {
          throw new GestoreException("Errore nella lettura del campo titcat della categoria:" + codiceCategoriaPrimoLivello, null, e);
        }
      }
    }
  */

    //Integrazione con WSERP
    String urlWSERP = ConfigManager.getValore("wserp.erp.url");
    urlWSERP = UtilityStringhe.convertiNullInStringaVuota(urlWSERP);
    if(!"".equals(urlWSERP)){
      gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
          this.getServletContext(), GestioneWSERPManager.class);
      //integrazione ERP : per SmeUp controllo su  dei beni e servizi
      WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
      if(configurazione.isEsito()){
        String tipoWSERP = configurazione.getRemotewserp();
        tipoWSERP = UtilityStringhe.convertiNullInStringaVuota(tipoWSERP);
        if("SMEUP".equals(tipoWSERP)){
          AbstractGestoreChiaveNumerica gestoreBeniServizi = new DefaultGestoreEntitaChiaveNumerica(
              "T_UBUY_BENISERVIZI", "NUM_BS", new String[] { "CODCAT" }, this.getRequest());
          this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm, gestoreBeniServizi, "BENISERVIZI",
              new DataColumn[] { datiForm.getColumn("CAIS.CAISIM") }, null);
        }
      }
    }//if integrazione WSERP

  }

  private boolean checkIsCategoriaPadreArchiviata(String codiceCategoria) throws SQLException {
    boolean ret=false;

    @SuppressWarnings("unchecked")
    Vector<JdbcParametro> datiPadriCategoria = sqlManager.getVector("SELECT codliv1,codliv2,codliv3,codliv4 FROM CAIS WHERE CAISIM=?",
        new String[] {codiceCategoria});

    if(datiPadriCategoria!=null && datiPadriCategoria.size()>0){
      String isarchiPadre="";
      for(int i=0; i<4;i++){
        String codliv = datiPadriCategoria.get(i).getStringValue();
        isarchiPadre = (String)sqlManager.getObject("select isarchi from cais where caisim=?", new Object[]{codliv});
        if(isarchiPadre!=null && "1".equals(isarchiPadre))
          return true;
      }

    }
    return ret;
  }
}
