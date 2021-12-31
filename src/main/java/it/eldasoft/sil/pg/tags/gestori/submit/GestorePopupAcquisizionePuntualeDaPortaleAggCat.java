/*
 * Created on 27/01/17
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoIscrizioneImpresaElencoOperatoriDocument;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per l'acquisizione degli aggiornamenti delle categorie di un singolo
 * aggiornamento FS4 proveniente da Portale ALice
 *
 * @author Marcello Caminiti
 */
public class GestorePopupAcquisizionePuntualeDaPortaleAggCat extends AbstractGestoreEntita {

  /** Logger */
  static Logger            logger           = Logger.getLogger(GestorePopupAcquisizionePuntualeDaPortaleAggCat.class);

  static String     nomeFileXML_Aggiornamento = "dati_aggisc.xml";

  @Override
  public String getEntita() {
    return "IMPR";
  }

  public GestorePopupAcquisizionePuntualeDaPortaleAggCat() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupAcquisizionePuntualeDaPortaleAggCat(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer
      dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {

    FileAllegatoManager fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager",
        this.getServletContext(), FileAllegatoManager.class);

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);

    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);

    String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String codiceDitta = UtilityStruts.getParametroString(this.getRequest(),"codiceDitta");
    String opScelta = UtilityStruts.getParametroString(this.getRequest(),"opScelta");
    String identificativo = UtilityStruts.getParametroString(this.getRequest(),"identificativo");
    Long id = null;
    if(identificativo!=null && !"".equals(identificativo))
      id= new Long(identificativo);

    ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    Long syscon = new Long(profilo.getId());

    String select=null;
    boolean errori=false;

    if("2".equals(opScelta)){
      this.aggiornaStatoGaracquisiz(id, new Long(4),syscon);
      this.getRequest().setAttribute("acquisizioneEseguita", "1");
    }else{
      //Vengono letti i documenti associati ad ogni occorrenza di di W_INVCOM
      select="select w.idprg,w.iddocdig from w_docdig w, garacquisiz g where w.idprg = g.idprg and g.ngara=? and g.codimp=? "
          + "and w.digkey1 = " + this.sqlManager.getDBFunction("inttostr",  new String[] {"g.idcom"}) + " and w.DIGENT = ? and stato=? and dignomdoc = ? ";
      String digent="W_INVCOM";

      Vector datiW_DOCDIG = null;
      try {
          datiW_DOCDIG = sqlManager.getVector(select,
              new Object[]{ngara,codiceDitta,digent, new Long(1), nomeFileXML_Aggiornamento});
      } catch (SQLException e) {
        this.getRequest().setAttribute("erroreAcquisizione", "1");
        throw new GestoreException("Errore nella lettura della tabella W_DOCDIG ",null, e);
      }
      String idprgW_INVCOM = null;
      Long iddocdig = null;

      GestorePopupAcquisisciDaPortale gacqport = new GestorePopupAcquisisciDaPortale();
      gacqport.setRequest(this.getRequest());
      if(datiW_DOCDIG != null ){
        if(((JdbcParametro)datiW_DOCDIG.get(0)).getValue() != null)
          idprgW_INVCOM = ((JdbcParametro) datiW_DOCDIG.get(0)).getStringValue();

        if(((JdbcParametro)datiW_DOCDIG.get(1)).getValue() != null)
        try {
          iddocdig = ((JdbcParametro) datiW_DOCDIG.get(1)).longValue();
        } catch (GestoreException e2) {
          this.getRequest().setAttribute("erroreAcquisizione", "1");
          throw new GestoreException("Errore nella lettura della tabella W_DOCDIG ",null, e2);
        }

        //Lettura del file xml immagazzinato nella tabella W_DOCDIG
        BlobFile fileAllegato = null;
        try {
          fileAllegato = fileAllegatoManager.getFileAllegato(idprgW_INVCOM,iddocdig);
        } catch (Exception e) {
          this.getRequest().setAttribute("erroreAcquisizione", "1");
          throw new GestoreException("Errore nella lettura del file allegato presente nella tabella W_DOCDIG",null, e);
        }
        String xml=null;
        if(fileAllegato!=null && fileAllegato.getStream()!=null){
          xml = new String(fileAllegato.getStream());

          try{
            AggiornamentoIscrizioneImpresaElencoOperatoriDocument document = AggiornamentoIscrizioneImpresaElencoOperatoriDocument.Factory.parse(xml);
            gacqport.gestioneCategorie(document, ngara, codiceDitta, pgManager, status);
            this.aggiornaStatoGaracquisiz(id, new Long(2),syscon);
          }catch (XmlException e) {
            this.getRequest().setAttribute("erroreAcquisizione", "1");
            this.aggiornaStatoGaracquisiz(id, new Long(3),syscon);
            throw new GestoreException("Errore nella lettura del file XML", null, e);
          }

        }else{
          //Aggiornamento dello stato a errore
          this.aggiornaStatoGaracquisiz(id, new Long(3),syscon);
          errori=true;
        }
      }else{
        //Aggiornamento dello stato a errore
        this.aggiornaStatoGaracquisiz(id, new Long(3),syscon);
        errori=true;
      }

      if(errori)
        this.getRequest().setAttribute("erroreAcquisizione", "1");
      else
        this.getRequest().setAttribute("acquisizioneEseguita", "1");

    }
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer dataColumnContainer)
      throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }




  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {


  }

  private void aggiornaStatoGaracquisiz(Long id, Long stato, Long syscon) throws GestoreException{
    //Aggiornamento dello stato delle occorrenze per le quali in Garacquisiz
    try {
      this.getSqlManager().update(
          "update GARACQUISIZ set STATO = ?, DATAORACQ = ?, SYSCON = ? where id=?",
          new Object[] { stato,new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),syscon, id});
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreAcquisizione", "1");
      throw new GestoreException("Errore nell'aggiornamento dell'entità GARACQUISIZ", null, e);
    }
  }

}