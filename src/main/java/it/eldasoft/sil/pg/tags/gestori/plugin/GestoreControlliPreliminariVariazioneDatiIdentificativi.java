/*
 * Created on 21/06/11
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
import it.eldasoft.sil.portgare.datatypes.RichiestaGenericaType;
import it.eldasoft.sil.portgare.datatypes.RichiestaVariazioneDocument;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.xmlbeans.XmlException;

/**
 * Gestore che effettua i controlli preliminari per l'acquisizione delle
 * variazioni dei dati identificativi impresa provenienti da portale
 *
 * @author Cristian Febas
 */
public class GestoreControlliPreliminariVariazioneDatiIdentificativi extends AbstractGestorePreload {

  static String     nomeFileXML_VariazioneDatiIdentificativi = "dati_domvar.xml";

  public GestoreControlliPreliminariVariazioneDatiIdentificativi(BodyTagSupportGene tag) {
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

    FileAllegatoManager fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager",
        page, FileAllegatoManager.class);

    //String ngara = page.getRequest().getParameter("ngara");
    String idocmString = page.getRequest().getParameter("idcom");
    Long idcom =  new Long(idocmString);

    try {
      String committ= (String)sqlManager.getObject("select committ from w_invcom where idprg=? and idcom=?", new Object[]{"PA",idcom});
      page.setAttribute("committ", committ);
    } catch (SQLException e) {
      page.setAttribute("erroreAcquisizione", "1");
      throw new JspException("Errore nella lettura della tabella W_INVCOM ", e);
    }

    String select=null;
    boolean errori=false;

        //Vengono letti i documenti associati ad ogni occorrenza di di W_INVCOM
        select="select idprg,iddocdig from w_docdig where DIGENT = ? and digkey1 = ? and idprg = ? and dignomdoc = ? ";
        String digent="W_INVCOM";
        String idprgW_DOCDIG="PA";

        Vector datiW_DOCDIG = null;
        try {

            datiW_DOCDIG = sqlManager.getVector(select,
                new Object[]{digent, idcom.toString(), idprgW_DOCDIG, nomeFileXML_VariazioneDatiIdentificativi});

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
              RichiestaVariazioneDocument document = RichiestaVariazioneDocument.Factory.parse(xml);
              RichiestaGenericaType richiestaVarDatiId = null;
              String testoRichiesta;
              if(document instanceof RichiestaVariazioneDocument){
                richiestaVarDatiId = (document).getRichiestaVariazione();
                testoRichiesta =  richiestaVarDatiId.getDescrizione();
                String msg1="\n "+testoRichiesta ;
                String msg = msg1;

                page.setAttribute("messaggi", msg,PageContext.REQUEST_SCOPE);
              }
            } catch (XmlException e) {
              page.setAttribute("erroreAcquisizione", "1");
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