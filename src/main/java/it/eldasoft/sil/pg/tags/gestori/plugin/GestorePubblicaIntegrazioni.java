/*
 * Created on 22/gen/2019
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore che effettua i controlli preliminari e popola la popup per
 * la rettifica dei termini di gara
 *
 * @author Marcello Caminiti
 */
public class GestorePubblicaIntegrazioni extends AbstractGestorePreload {

  private SqlManager sqlManager = null;
  private PgManagerEst1 pgManagerEst1 = null;
  private TabellatiManager tabellatiManager = null;
  
  public GestorePubblicaIntegrazioni(BodyTagSupportGene tag) {
    super(tag);
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }

  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {
    /*
    String queryDocumentiDaPubblicare = "SELECT * FROM DOCUMGARA WHERE CODGAR = ? AND STATODOC IS NULL AND ((GRUPPO = 1 AND EXISTS (SELECT * FROM PUBBLI WHERE CODGAR9 = DOCUMGARA.CODGAR AND TIPPUB = 11))" +
    "OR (GRUPPO = 6 AND EXISTS (SELECT * FROM PUBBLI WHERE CODGAR9 = DOCUMGARA.CODGAR AND TIPPUB = 13)) OR (GRUPPO = 3 AND EXISTS (SELECT * FROM PUBBLI WHERE CODGAR9 = DOCUMGARA.CODGAR AND (TIPPUB = 13 OR TIPPUB = 11)))"+
    "OR  (GRUPPO = 2 AND EXISTS (SELECT * FROM PUBBLI WHERE CODGAR9 = DOCUMGARA.CODGAR AND (TIPPUB = 13 OR TIPPUB = 11))) OR  (GRUPPO = 4 AND EXISTS (SELECT * FROM PUBG WHERE NGARA = DOCUMGARA.CODGAR AND TIPPUBG = 12)))";
    */
    String queryDocumentiDaPubblicare = "SELECT GRUPPO, BUSTA, IDDOCDG, URLDOC, NORDDOCG, IDPRG, DATAPROV FROM DOCUMGARA WHERE CODGAR = ? AND STATODOC IS NULL";

    String titoloMessaggio = "<b>Non è possibile procedere con la pubblicazione dei documenti sul portale Appalti.</b><br>";
    String messaggio = "";
    String messaggioWarning = "";
    String controlloSuperato="SI";
    String warning="NO";

    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);
    pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        page, PgManagerEst1.class);
    tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        page, TabellatiManager.class);

    // lettura dei parametri di input
    String codgar = page.getRequest().getParameter("codgar");
    String ngara = page.getRequest().getParameter("ngara");
    String telematica = page.getRequest().getParameter("isProceduraTelematica");
    String pubblicaTrasparenza = "NO";
    try {
      boolean documentoDaPubblicare = false;
      Long iterga = (Long) sqlManager.getObject("SELECT ITERGA FROM TORN WHERE CODGAR = ?", new Object[]{codgar});

      List listaDatiGara= this.sqlManager.getListVector(queryDocumentiDaPubblicare, new Object[]{codgar});
      if (listaDatiGara != null && listaDatiGara.size() > 0) {
        Long tippub11 = (Long) sqlManager.getObject("SELECT COUNT(TIPPUB) FROM PUBBLI WHERE CODGAR9 = ? AND TIPPUB = ?", new Object[]{codgar,11});
        Long tippub13 = (Long) sqlManager.getObject("SELECT COUNT(TIPPUB) FROM PUBBLI WHERE CODGAR9 = ? AND (TIPPUB = ? OR TIPPUB = ?)", new Object[]{codgar,13,23});
        Long pubg14 = (Long) sqlManager.getObject("SELECT COUNT(TIPPUBG) FROM PUBG, GARE WHERE PUBG.NGARA = GARE.NGARA AND GARE.CODGAR1 = ? AND TIPPUBG = ?", new Object[]{codgar,14});
        Long pubg12 = (Long) sqlManager.getObject("SELECT COUNT(TIPPUBG) FROM PUBG, GARE WHERE PUBG.NGARA = GARE.NGARA AND GARE.CODGAR1 = ? AND TIPPUBG = ?", new Object[]{codgar,12});
        for(int i=listaDatiGara.size()-1;i>=0;i--){
          Long gruppo = SqlManager.getValueFromVectorParam(
              listaDatiGara.get(i), 0).longValue();
          documentoDaPubblicare = false;
          if(gruppo == 1){
            if(tippub11 > 0){documentoDaPubblicare = true;}
          }
          if(gruppo == 6){
            if(tippub13 > 0){documentoDaPubblicare = true;}
          }
          if(gruppo == 3){
            if(tippub11 > 0 && tippub13 == 0 && (new Long(2).equals(iterga) || new Long(4).equals(iterga))){
              Long busta = SqlManager.getValueFromVectorParam(listaDatiGara.get(i), 1).longValue();
              if(new Long(4).equals(busta)){
                //ristretta e tippub = 11 e busta = 4
                documentoDaPubblicare = true;
              }
            }else{
              if(tippub11 > 0 || tippub13 > 0){
                documentoDaPubblicare = true;
              }
            }
          }
          if(gruppo == 2){
            if(tippub11 > 0 || tippub13 > 0){documentoDaPubblicare = true;}
          }
          if(gruppo == 4){
            if(pubg12 > 0){documentoDaPubblicare = true;}
          }
          if(gruppo == 10){
            if(tippub11 > 0 || tippub13 > 0 || pubg12 > 0){documentoDaPubblicare = true;}
          }
          if(gruppo == 15){
            documentoDaPubblicare = true;
          }
          if(gruppo == 5){
            pubblicaTrasparenza = "SI";
            if(pubg14 > 0){documentoDaPubblicare = true;}
          }
          if(!documentoDaPubblicare){
            listaDatiGara.remove(i);
          }
        }
      }
      if(listaDatiGara.size()<= 0){
        controlloSuperato = "NO";
        messaggio += "<br>Non è stato inserito nessun documento da pubblicare.";
      }else{
        boolean controlloFirma = true;
        String richiestaFirma = ConfigManager.getValore(CostantiAppalti.PROP_RICHIESTA_FIRMA);
        if(!"1".equals(richiestaFirma)){
          controlloFirma = false;
        }
        boolean controlloAllegato = true;
        boolean controlloAllegatoUrl = true;
        boolean gruppo3 = false;
        boolean buste4 = false;
        boolean controlloDataprov = true;
        Timestamp dataprov = null;
        String dataOggi = null;
        Date dataOdierna = null;
        
        String desc = tabellatiManager.getDescrTabellato("A1108", "1");
        if(desc!=null && !"".equals(desc))
          desc = desc.substring(0,1);
        boolean gestioneUrl=false;
        if("1".equals(desc))
          gestioneUrl=true;
        
        for(int i=listaDatiGara.size()-1;i>=0 && (controlloAllegato || controlloAllegatoUrl || controlloFirma || controlloDataprov);i--){
          Long iddocdig = (Long) SqlManager.getValueFromVectorParam(listaDatiGara.get(i), 2).getValue();
          String idprg = SqlManager.getValueFromVectorParam(listaDatiGara.get(i), 5).getStringValue();
          String allegato = (String) sqlManager.getObject("SELECT DIGNOMDOC FROM W_DOCDIG WHERE IDDOCDIG = ? and IDPRG = ? and DIGKEY1 = ?", new Object[]{iddocdig,idprg,codgar});
          Long gruppo = (Long) SqlManager.getValueFromVectorParam(listaDatiGara.get(i), 0).getValue();
          Long buste = (Long) SqlManager.getValueFromVectorParam(listaDatiGara.get(i), 1).getValue();
          if(gruppo != null && gruppo.intValue() == 3){
            gruppo3 = true;
          };
          if(buste != null && buste.intValue() == 4){
            buste4 = true;
          };
          String URLdoc = SqlManager.getValueFromVectorParam(listaDatiGara.get(i), 3).getStringValue();
          if(controlloAllegato && (gruppo.intValue() != 2 && gruppo.intValue() != 3 && (allegato == null || "".equals(allegato)))){
            if(!gestioneUrl || "".equals(URLdoc) || URLdoc == null){
              controlloSuperato = "NO";
              controlloAllegato = false;
              messaggio = messaggio + "<br>Per tutti i documenti di gara da pubblicare deve essere specificato l'allegato";
              if(gestioneUrl){
                messaggio = messaggio + " o l'url di pubblicazione";
              }
              messaggio = messaggio + ".";
            }
          }if(controlloAllegatoUrl && (gestioneUrl && !"".equals(URLdoc) && URLdoc != null)){
            if(!PgManager.validazioneURL(URLdoc)){
              controlloSuperato = "NO";
              controlloAllegatoUrl = false;
              messaggio += "<br>Per tutti i documenti di gara da pubblicare le url di pubblicazione specificate devono essere valide.";
            }
          }
          if(controlloFirma){
            //Controllo sulla richiesta di firma degli allrgati
            String select="select count(*) from w_docdig where idprg = ? and iddocdig = ? and digfirma ='1'";
            Long numOccorrenze = (Long) sqlManager.getObject(select, new Object[]{idprg,iddocdig});
            if(numOccorrenze!= null && numOccorrenze.longValue()>0){
              controlloSuperato = "NO";
              controlloFirma = false;
              messaggio += "<br>Ci sono dei documenti da pubblicare in attesa di firma.";

            }
          }
          dataprov = SqlManager.getValueFromVectorParam(listaDatiGara.get(i), 6).dataValue();
          if(dataprov!=null){
            dataOggi = UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA);
            dataOdierna = UtilityDate.convertiData(dataOggi, UtilityDate.FORMATO_GG_MM_AAAA);
            if(dataOdierna.compareTo(new Date(dataprov.getTime()))<0){
              controlloSuperato = "NO";
              controlloDataprov = false;
              messaggio += "<br>Ci sono dei documenti da pubblicare con data provvedimento successiva alla data corrente.";
            }
          }

        }//fine controlli su ogni documento rimasto
        if(new Long(2).equals(iterga) || new Long(4).equals(iterga)){
          Long documPreq = (Long) sqlManager.getObject("SELECT COUNT(*) FROM DOCUMGARA, PUBBLI WHERE CODGAR = ? AND DOCUMGARA.CODGAR = PUBBLI.CODGAR9 AND DOCUMGARA.BUSTA = 4 AND PUBBLI.TIPPUB = 13 AND DOCUMGARA.STATODOC IS NULL", new Object[]{codgar});
          if(documPreq > 0){
            controlloSuperato = "NO";
            messaggio = messaggio + "<br>Non è possibile integrare documenti relativi alla busta di prequalifica perchè la fase di prequalifica è conclusa";
          }
        }

        if("true".equals(telematica)){
          if(ngara == null || "".equals(ngara)){
            ngara= codgar;
          }
          if(gruppo3){
              if(pgManagerEst1.esistonoComunicazioni(ngara, "FS11")){
              warning = "SI";
              messaggioWarning = messaggioWarning + "<br>Ci sono presentazioni di offerta, in fase di composizione o già completate, da parte degli operatori.";
              }
          }
          if(buste4){
            if(pgManagerEst1.esistonoComunicazioni(ngara, "FS10")){
            warning = "SI";
            messaggioWarning = messaggioWarning + "<br>Ci sono domande di partecipazione, in fase di composizione o già completate, da parte degli operatori.";
            }
          }
          messaggioWarning += "<br>Pertanto l'integrazione dei documenti richiesti ai concorrenti potrebbe non essere recepita da tali operatori.";
        }
      }//fine controlli da eseguire se ci sono documenti da pubblicare

      if("NO".equals(controlloSuperato)){
        page.setAttribute("titoloMsg", titoloMessaggio, PageContext.REQUEST_SCOPE);
        page.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);
      }else{
        if("SI".equals(warning)){
          page.setAttribute("msgWarning", messaggioWarning, PageContext.REQUEST_SCOPE);
        }
        ArrayList<Long> norddocgList = new ArrayList();
        for(int i=0;i<listaDatiGara.size();i++){
          norddocgList.add(SqlManager.getValueFromVectorParam(listaDatiGara.get(i), 4).longValue());
        }
        page.setAttribute("pubblicaTrasparenza", pubblicaTrasparenza, PageContext.REQUEST_SCOPE);
        page.setAttribute("listaDocumenti", norddocgList, PageContext.REQUEST_SCOPE);
      }
      page.setAttribute("controlloSuperato", controlloSuperato, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante l'interazione con la base dati durante i controlli preliminari all'integrazione documentazione", e);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore durante l'interazione con la base dati durante i controlli preliminari all'integrazione documentazione", e);
    }
  }
}