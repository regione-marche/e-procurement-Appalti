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
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore che effettua i controlli preliminari e popola la popup per
 * la rettifica dei termini di gara
 *
 * @author Marcello Caminiti
 */
public class GestorePubblicaDelibere extends GestorePubblicaSuPortale {

  PgManagerEst1 pgManagerEst1 = null;

  public GestorePubblicaDelibere(BodyTagSupportGene tag) {
    super(tag);
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }

  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1", page, PgManagerEst1.class);

    /*
    String queryDocumentiDaPubblicare = "SELECT * FROM DOCUMGARA WHERE CODGAR = ? AND STATODOC IS NULL AND ((GRUPPO = 1 AND EXISTS (SELECT * FROM PUBBLI WHERE CODGAR9 = DOCUMGARA.CODGAR AND TIPPUB = 11))" +
    "OR (GRUPPO = 6 AND EXISTS (SELECT * FROM PUBBLI WHERE CODGAR9 = DOCUMGARA.CODGAR AND TIPPUB = 13)) OR (GRUPPO = 3 AND EXISTS (SELECT * FROM PUBBLI WHERE CODGAR9 = DOCUMGARA.CODGAR AND (TIPPUB = 13 OR TIPPUB = 11)))"+
    "OR  (GRUPPO = 2 AND EXISTS (SELECT * FROM PUBBLI WHERE CODGAR9 = DOCUMGARA.CODGAR AND (TIPPUB = 13 OR TIPPUB = 11))) OR  (GRUPPO = 4 AND EXISTS (SELECT * FROM PUBG WHERE NGARA = DOCUMGARA.CODGAR AND TIPPUBG = 12)))";
    */
    String queryDocumentiDaPubblicare = "SELECT IDDOCDG, URLDOC, NORDDOCG, IDPRG FROM DOCUMGARA WHERE CODGAR = ? AND GRUPPO = 15 AND STATODOC IS NULL";
    String queryGARE = "select TIPGARG, NOT_GAR from GARE where CODGAR1 = ?";
    String queryTORN = "select DESTOR, CENINT, CODRUP, TIPGAR from TORN where codgar = ?";
    String titoloMessaggio = "<b>Non è possibile procedere con la pubblicazione della delibera a contrarre sul portale Appalti.</b><br>";
    String messaggio = "";
    String controlloSuperato = "SI";

    this.inizializzaManager(page);

    // lettura dei parametri di input
    String codgar = page.getRequest().getParameter("codgar");
    String ngara = page.getRequest().getParameter("ngara");

    try {
      Long gentip = (Long) sqlManager.getObject("select genere from V_GARE_TORN where codgar = ?", new Object[]{codgar});

      boolean garaLotti = false;
      if(new Long(1).equals(gentip) || new Long(3).equals(gentip)){
        garaLotti = true;
      }
      //Controlli livello gara
      //Controlli gara a Lotti
      if(garaLotti){
        Long nLotti = new Long(0);
        nLotti = (Long) sqlManager.getObject("select count(ngara) from GARE where codgar1 = ? and codgar1 <> ngara" , new Object[]{codgar});
        if(new Long(0).equals(nLotti)){
          controlloSuperato = "NO";
          messaggio += "<br>Non sono stati definiti i lotti della gara.";
        }
      }

      String cenint=null;
      String codrup = null;

      /*
      List listaDatiTorn= this.sqlManager.getListVector(queryTORN, new Object[]{codgar});
      if (listaDatiTorn != null && listaDatiTorn.size() > 0) {
        for(int i=listaDatiTorn.size()-1;i>=0;i--){
          Long tipgar = SqlManager.getValueFromVectorParam(
              listaDatiTorn.get(i), 3).longValue();
          if(garaLotti && tipgar == null){
            controlloSuperato = "NO";
            messaggio += "<br>Non è stato inserito il tipo procedura della gara.";
          }
          String destor = SqlManager.getValueFromVectorParam(
              listaDatiTorn.get(i), 0).getStringValue();
          if(garaLotti && (destor == null || "".equals(destor))){
            controlloSuperato = "NO";
            messaggio += "<br>Non è stato inserito l'oggetto della gara.";
          }
          cenint = SqlManager.getValueFromVectorParam(
              listaDatiTorn.get(i), 1).getStringValue();
          if(cenint == null || "".equals(cenint)){
            controlloSuperato = "NO";
            messaggio += "<br>Non è stata inserita la stazione appaltante.";
          }
          codrup = SqlManager.getValueFromVectorParam(
              listaDatiTorn.get(i), 2).getStringValue();
          if(codrup == null || "".equals(codrup)){
            controlloSuperato = "NO";
            messaggio += "<br>Non è stato inserito il responsabile unico procedimento.";
          }
        }
      }
      */
      Vector datiTorn= this.sqlManager.getVector(queryTORN, new Object[]{codgar});
      if (datiTorn != null && datiTorn.size() > 0) {

          Long tipgar = SqlManager.getValueFromVectorParam(
              datiTorn, 3).longValue();
          if(garaLotti && tipgar == null){
            controlloSuperato = "NO";
            messaggio += "<br>Non è stato inserito il tipo procedura della gara.";
          }
          String destor = SqlManager.getValueFromVectorParam(
              datiTorn, 0).getStringValue();
          if(garaLotti && (destor == null || "".equals(destor))){
            controlloSuperato = "NO";
            messaggio += "<br>Non è stato inserito l'oggetto della gara.";
          }
          cenint = SqlManager.getValueFromVectorParam(
              datiTorn, 1).getStringValue();
          if(cenint == null || "".equals(cenint)){
            controlloSuperato = "NO";
            messaggio += "<br>Non è stata inserita la stazione appaltante.";
          }
          codrup = SqlManager.getValueFromVectorParam(
              datiTorn, 2).getStringValue();
          if(codrup == null || "".equals(codrup)){
            controlloSuperato = "NO";
            messaggio += "<br>Non è stato inserito il responsabile unico procedimento.";
          }

      }
      if(garaLotti){queryGARE = queryGARE + " and NGARA <> CODGAR1";}
      List listaDatiGare= this.sqlManager.getListVector(queryGARE, new Object[]{codgar});
      if (listaDatiGare != null && listaDatiGare.size() > 0) {
        for(int i=listaDatiGare.size()-1;i>=0;i--){
          Long tipgarg = SqlManager.getValueFromVectorParam(
              listaDatiGare.get(i), 0).longValue();
          if(tipgarg == null || "".equals(tipgarg)){
            controlloSuperato = "NO";
            if(garaLotti){
              messaggio += "<br>Non è stato inserito il tipo procedura del lotto " + ngara + ".";
            }else{
              messaggio += "<br>Non è stato inserito il tipo procedura della gara.";
            }
          }
          String notgar = SqlManager.getValueFromVectorParam(
              listaDatiGare.get(i), 1).getStringValue();
          if(notgar == null || "".equals(notgar)){
            controlloSuperato = "NO";
            if(garaLotti){
              messaggio += "<br>Non è stato inserito l'oggetto del lotto " + ngara + ".";
            }else{
              messaggio += "<br>Non è stato inserito l'oggetto della gara.";
            }
          }
        }
      }

      String profilo = (String) page.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);
      HashMap<String,String> risposta=pgManagerEst1.controlloDatiBloccantiInvioSCP(ngara, codgar, cenint, codrup, "DELIBERE", gentip, profilo);
      if(risposta!=null){
        if("SI".equals(risposta.get("erroriBloccanti"))){
            controlloSuperato = "NO";
            messaggio += risposta.get("msgErroriBloccanti");
        }
      }

      //iddocdig urldoc norddoc
      List listaDatiDocumenti= this.sqlManager.getListVector(queryDocumentiDaPubblicare, new Object[]{codgar});
      if (listaDatiDocumenti != null && listaDatiDocumenti.size() > 0) {

        String desc = tabellatiManager.getDescrTabellato("A1108", "1");
        if(desc!=null && !"".equals(desc))
          desc = desc.substring(0,1);
        boolean gestioneUrl=false;
        if("1".equals(desc))
          gestioneUrl=true;
        String[] arrayTemp = new String[2];
        arrayTemp = this.controlloAllegatoUrl(ngara, codgar, new Long(3), "GRUPPO = 15", gestioneUrl, "0");
        if("NO".equals(arrayTemp[0])){
          controlloSuperato = "NO";
          messaggio += arrayTemp[1];
        }

        String richiestaFirma = ConfigManager.getValore(CostantiAppalti.PROP_RICHIESTA_FIRMA);
        if("1".equals(richiestaFirma)){
          //Controllo sulla richiesta di firma degli allrgati
          String select="select count(codgar) from documgara, w_docdig where codgar= ? and documgara.idprg=w_docdig.idprg and documgara.iddocdg=w_docdig.iddocdig and digfirma ='1' and gruppo = 15";
          Long numOccorrenze = (Long) sqlManager.getObject(select, new Object[]{codgar});
          if(numOccorrenze!= null && numOccorrenze.longValue()>0){
            controlloSuperato = "NO";
            messaggio += "<br>Ci sono dei documenti da pubblicare in attesa di firma.";
          }
        }

        //Controllo sulla data provvedimento
        String dataOggi = UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA);
        Date dataOdierna = UtilityDate.convertiData(dataOggi, UtilityDate.FORMATO_GG_MM_AAAA);
        String select="select count(codgar) from documgara where codgar= ? and dataprov is not null and dataprov > ?";
        Long numOccorrenze = (Long) sqlManager.getObject(select, new Object[]{codgar, dataOdierna});
        if(numOccorrenze!= null && numOccorrenze.longValue()>0){
          controlloSuperato = "NO";
          messaggio += "<br>Ci sono dei documenti da pubblicare con data provvedimento successiva alla data corrente";
        }

      }else{
        controlloSuperato = "NO";
        messaggio += "<br>Non è stato inserito nessun documento relativo a delibera a contrarre.";
      }

      if("NO".equals(controlloSuperato)){
        page.setAttribute("titoloMsg", titoloMessaggio, PageContext.REQUEST_SCOPE);
        page.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);
      }else{
        ArrayList<Long> norddocgList = new ArrayList<Long>();
        for(int i=0;i<listaDatiDocumenti.size();i++){
          norddocgList.add(SqlManager.getValueFromVectorParam(listaDatiDocumenti.get(i), 2).longValue());
        }
        page.setAttribute("listaDocumenti", norddocgList, PageContext.REQUEST_SCOPE);
      }
      page.setAttribute("controlloSuperato", controlloSuperato, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante l'interazione con la base dati durante i controlli preliminari alla pubblicazione delibera a contrarre", e);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore durante l'interazione con la base dati durante i controlli preliminari alla pubblicazione delibera a contrarre", e);
    }
  }
}