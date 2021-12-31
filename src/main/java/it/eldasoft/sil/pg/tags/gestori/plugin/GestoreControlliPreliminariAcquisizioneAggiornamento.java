/*
 * Created on 12/10/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoAnagraficaImpresaDocument;
import it.eldasoft.sil.portgare.datatypes.RecapitiType;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.xmlbeans.XmlException;

/**
 * Gestore che effettua i controlli preliminari per l'acquisizione degli
 * aggiornamenti di anagrafica provenienti da portale
 *
 * @author Marcello Caminiti
 */
public class GestoreControlliPreliminariAcquisizioneAggiornamento extends AbstractGestorePreload {

  static String     nomeFileXML_Aggiornamento = "dati_agganag.xml";

  public GestoreControlliPreliminariAcquisizioneAggiornamento(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);

    //TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
    //    page, TabellatiManager.class);

    FileAllegatoManager fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager",
        page, FileAllegatoManager.class);

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        page, PgManager.class);

    //String ngara = page.getRequest().getParameter("ngara");
    String idocmString = page.getRequest().getParameter("idcom");
    Long idcom =  new Long(idocmString);
    String user = (String)page.getAttribute("comkey1", PageContext.REQUEST_SCOPE);

    String select=null;
    boolean errori=false;


        //Determinazione del codice dell'impresa
        select="select USERKEY1 from w_puser where USERNOME = ? ";
        String codiceDitta;
        try {
          codiceDitta = (String)sqlManager.getObject(select, new Object[]{user});
        } catch (SQLException e2) {
          page.setAttribute("erroreAcquisizione", "1");
          throw new JspException("Errore nella lettura della tabella W_PUSER ", e2);
        }

        //Vengono letti i documenti associati ad ogni occorrenza di di W_INVCOM
        select="select idprg,iddocdig from w_docdig where DIGENT = ? and digkey1 = ? and idprg = ? and dignomdoc = ? ";
        String digent="W_INVCOM";
        String idprgW_DOCDIG="PA";

        Vector datiW_DOCDIG = null;
        try {

            datiW_DOCDIG = sqlManager.getVector(select,
                new Object[]{digent, idcom.toString(), idprgW_DOCDIG, nomeFileXML_Aggiornamento});

        } catch (SQLException e) {
          page.setAttribute("erroreAcquisizione", "1");
          throw new JspException("Errore nella lettura della tabella W_DOCDIG ", e);
        }
        String idprgW_INVCOM = null;
        Long iddocdig = null;
        if(datiW_DOCDIG != null ){
          if(((JdbcParametro)datiW_DOCDIG.get(0)).getValue() != null)
            idprgW_INVCOM = ((JdbcParametro) datiW_DOCDIG.get(0)).getStringValue();

          if(((JdbcParametro)datiW_DOCDIG.get(1)).getValue() != null)
          try {
            iddocdig = ((JdbcParametro) datiW_DOCDIG.get(1)).longValue();
          } catch (GestoreException e2) {
            page.setAttribute("erroreAcquisizione", "1");
            throw new JspException("Errore nella lettura della tabella W_DOCDIG ", e2);
          }


          //Lettura del file xml immagazzinato nella tabella W_DOCDIG
          BlobFile fileAllegato = null;
          try {
            fileAllegato = fileAllegatoManager.getFileAllegato(idprgW_INVCOM,iddocdig);
          } catch (Exception e) {
            page.setAttribute("erroreAcquisizione", "1");
            throw new JspException("Errore nella lettura del file allegato presente nella tabella W_DOCDIG", e);
          }
          String xml=null;
          if(fileAllegato!=null && fileAllegato.getStream()!=null){
            xml = new String(fileAllegato.getStream());

            try {
              AggiornamentoAnagraficaImpresaDocument document = AggiornamentoAnagraficaImpresaDocument.Factory.parse(xml);
              //Controllo dei dati anagrafici di IMPR

              String msgImpresa=pgManager.controlloDatiImpresa(document, codiceDitta);

              //Controllo Legale Rappresentante
              String msgLegale = pgManager.controlloDatiReferenti( document, codiceDitta, "LEGALE");

              //Controllo Direttore Tecnico
              String msgDirettore = pgManager.controlloDatiReferenti( document, codiceDitta, "DIRETTORE");

              //Controllo Azionista
              //String msgAzionista = pgManager.controlloDatiReferenti( document, codiceDitta, "AZIONISTA");

              //Controllo Soggetti con altre cariche o qualifiche
              String msgAltreCariche = pgManager.controlloDatiReferenti( document, codiceDitta, "ALTRECARICHE");

              //Controllo Collaboratore
              String msgCollaboratore = pgManager.controlloDatiReferenti( document, codiceDitta, "COLLABORATORE");

              String msg = msgImpresa + msgLegale + msgDirettore + msgAltreCariche + msgCollaboratore;

              //Si controlla se è variata la pec o ci sono modifiche ai referenti
              //Informazione che serve per impostare lo stato della nota ad 'aperto'
              RecapitiType recapiti = document.getAggiornamentoAnagraficaImpresa().getDatiImpresa().getImpresa().getRecapiti();
              String pecMsg = "";
              String pecDb = "";
              if(recapiti!=null){
                pecMsg = recapiti.getPec();
                try {
                  pecDb = (String)sqlManager.getObject("select emai2ip from impr where codimp = ?", new Object[]{codiceDitta});
                } catch (SQLException e) {
                  throw new GestoreException("Errore nella lettura del campo IMPR.EMAI2IP",null, e);
                }
                if(pecDb==null)
                  pecDb="";
                if(pecMsg==null)
                  pecMsg="";
              }
              boolean impostareStatoNota = false;
              if(!pecMsg.equals(pecDb) || !"".equals(msgLegale) || !"".equals(msgDirettore) || !"".equals(msgAltreCariche) || !"".equals(msgCollaboratore))
                impostareStatoNota = true;
              page.setAttribute("impostareStatoNota", impostareStatoNota,PageContext.REQUEST_SCOPE);
              page.setAttribute("messaggi", msg,PageContext.REQUEST_SCOPE);
            } catch (XmlException e) {
              page.setAttribute("erroreAcquisizione", "1");
              throw new JspException("Errore nella lettura del file XML", e);
            } catch (GestoreException e) {
              throw new JspException("Errore nella lettura del file XML", e);
            }



          }else{

            errori=true;

          }


        }else{

          errori=true;

        }

        if(errori){
          page.setAttribute("erroreAcquisizione", "1");
        }

  }

}