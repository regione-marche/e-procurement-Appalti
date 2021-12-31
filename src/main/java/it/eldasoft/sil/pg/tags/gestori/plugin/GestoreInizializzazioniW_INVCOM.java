/*
 * Created on 09/mar/2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.dm.WSDMVerificaMailResType;


public class GestoreInizializzazioniW_INVCOM extends AbstractGestorePreload {

  public GestoreInizializzazioniW_INVCOM(BodyTagSupportGene tag) {
    super(tag);
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {

  }

  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
        "tabellatiManager", page, TabellatiManager.class);
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);
    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        page, PgManager.class);
    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        page, PgManagerEst1.class);
    GestioneWSDMManager gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager", page, GestioneWSDMManager.class);



    HashMap key = UtilityTags.stringParamsToHashMap(
        (String) page.getAttribute(
            UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, PageContext.REQUEST_SCOPE), null);

    String tipo=  page.getRequest().getParameter("tipo");
    String idconfi = page.getRequest().getParameter("idconfi");

    // Inizializza l'operatore con l'utente di USRSYS che ha avuto accesso all'applicativo
    ProfiloUtente profilo = (ProfiloUtente) page.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    page.setAttribute("inizializzazioneOperatore", profilo.getNome(), PageContext.REQUEST_SCOPE);

    // Inizializza il mittente con il nome dell'utente o con l'ufficio di appartenenza a seconda dell'impostazione del parametro
    String descTab = tabellatiManager.getDescrTabellato("G_032", "1");
    boolean result = false;
    if(descTab != null)
      result = descTab.startsWith("2");
    if (result){
      String numUfficioAppartenenza = profilo.getUfficioAppartenenza();
      String descUfficioAppartenenza = "";
      if (numUfficioAppartenenza != null && !"".equals(numUfficioAppartenenza))
        descUfficioAppartenenza = tabellatiManager.getDescrTabellato("G_022", numUfficioAppartenenza);
      page.setAttribute("inizializzazioneMittente", descUfficioAppartenenza, PageContext.REQUEST_SCOPE);
    } else
      page.setAttribute("inizializzazioneMittente", profilo.getNome(), PageContext.REQUEST_SCOPE);


    String modo = (String) page.getAttribute(
        UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO, PageContext.REQUEST_SCOPE);

    // Inizializza le informazioni sensibili al protocollo
    if(!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modo)){
      String idprg = "";
      String idcom = "";
      if (key.get("W_INVCOM.IDPRG") != null)
        idprg = ((JdbcParametro) key.get("W_INVCOM.IDPRG")).getStringValue();
      if (key.get("W_INVCOM.IDCOM") != null)
        idcom = ((JdbcParametro) key.get("W_INVCOM.IDCOM")).getStringValue();
      String sql = "select comnumprot from w_invcom where idprg = ? and idcom = ?";
      try {
        String numProt =  (String) sqlManager.getObject(sql,new Object[] { idprg, idcom });
        if(numProt!=null){
          page.setAttribute("inizializzazioneProtocollo", "true", PageContext.REQUEST_SCOPE);
          sql = "select count(idprg) from w_invcomdes where idprg = ? and idcom = ? and desstato='5'";
          Long conteggio = (Long)sqlManager.getObject(sql,new Object[] { idprg, idcom });
          if(conteggio!=null && conteggio.longValue() >0)
            page.setAttribute("destinatariErrore", "true", PageContext.REQUEST_SCOPE);
        }else{
          page.setAttribute("inizializzazioneProtocollo", "false", PageContext.REQUEST_SCOPE);
        }



      }catch(SQLException e) {
        throw new JspException(
            "Errore durante la selezione dei dati della comunicazione", e);
      }
    }else{
      page.setAttribute("inizializzazioneProtocollo", "false", PageContext.REQUEST_SCOPE);
    }

    try {
      if (UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modo)) {

        String rispondi = page.getRequest().getParameter("rispondi");

        if (rispondi == null) {
          // nuovo da modulo invia comunicazioni

          String numModello = page.getRequest().getParameter("numModello");
          if(numModello == null || "".equals(numModello) || "0".equals(numModello))
            return;
          else{
            String keyAdd = page.getRequest().getParameter("keyAdd");
            String gara=null;
            String codiceGara=null;
            String codiga =null;
            String g1codcig=null;
            String oggetta=null;
            String g1destor=null;
            String oggettoga=null;
            Timestamp dtepar = null;
            String otepar = null;
            Timestamp dteoff = null;
            String oteoff = null;
            Timestamp desoff = null;
            String oesoff = null;

            if(keyAdd!=null && !"".equals(keyAdd)){
              //Estrazione dati della gara
              gara=keyAdd.substring(keyAdd.indexOf(":")+1);
              String selectGare="select g.codgar1,g.codiga,g.codcig,g.not_gar,t.destor,t.dtepar,t.otepar,t.dteoff,t.oteoff,t.desoff,t.oesoff from gare g,torn t where g.ngara=? and g.codgar1=t.codgar";
              Vector datiGara= sqlManager.getVector(selectGare, new Object[]{gara});
              if(datiGara!=null && datiGara.size()>0){
                codiceGara = SqlManager.getValueFromVectorParam(datiGara, 0).stringValue();
                codiga = SqlManager.getValueFromVectorParam(datiGara, 1).stringValue();
                g1codcig = SqlManager.getValueFromVectorParam(datiGara, 2).stringValue();
                oggetta = SqlManager.getValueFromVectorParam(datiGara, 3).stringValue();
                g1destor = SqlManager.getValueFromVectorParam(datiGara, 4).stringValue();
                dtepar = SqlManager.getValueFromVectorParam(datiGara, 5).dataValue();
                otepar = SqlManager.getValueFromVectorParam(datiGara, 6).stringValue();
                dteoff = SqlManager.getValueFromVectorParam(datiGara, 7).dataValue();
                oteoff = SqlManager.getValueFromVectorParam(datiGara, 8).stringValue();
                desoff = SqlManager.getValueFromVectorParam(datiGara, 9).dataValue();
                oesoff = SqlManager.getValueFromVectorParam(datiGara, 10).stringValue();
              }
              oggettoga=(String)sqlManager.getObject("select oggetto from garealbo where ngara=?", new Object[]{gara});
            }


            String select="select commsgogg, comintest, commsgtes, crittes, genere,commodello from w_confcom where numpro = ?";
            Vector datiW_confcom = sqlManager.getVector(select, new Object[]{new Long(numModello)});

            String ditta = page.getRequest().getParameter("ditta");
            String g1nprogg = null;
            String g1numordpl = null;
            String dittao = null;
            String nomimp = null;
            String cfimp = null;
            String pivimp = null;
            Date g_dtrisoa = null;
            Date g_dscanc = null;

            String nomtecrup = null;
            String cftecrup = null;
            Long inctecLong = null;
            String inctec = null;

            Vector datiRup=  sqlManager.getVector("select nomtec, cftec, inctec from tecni,torn where codgar=? and codrup =codtec",new Object[] { codiceGara });
            if(datiRup!=null ){
              nomtecrup = ((JdbcParametro) datiRup.get(0)).getStringValue();
              cftecrup = ((JdbcParametro) datiRup.get(1)).getStringValue();
              inctecLong = (Long) ((JdbcParametro) datiRup.get(2)).getValue();
            }

            if(inctecLong != null){
              inctec = inctecLong.toString();
            }

            boolean singolaDitta = false;
            Long crittes = null;
            if(datiW_confcom.get(3)!=null){
              crittes = ((JdbcParametro) datiW_confcom.get(3)).longValue();
            }

            Long genere = null;
            if(datiW_confcom.get(4)!=null){
              genere = ((JdbcParametro) datiW_confcom.get(4)).longValue();
            }

            Long commodello = ((JdbcParametro) datiW_confcom.get(5)).longValue();
            if(new Long(56).equals(genere)){
              page.setAttribute("initCOMMODELLO",commodello, PageContext.REQUEST_SCOPE);
            }

            if(crittes==null || crittes.longValue()!=2){
              if(genere != null && (genere.longValue()==4 || genere.longValue()==5 || genere.longValue()==6)){
                singolaDitta = true;
                String selectDitta = "select ditg.nprogg, ditg.numordpl, ditg.dittao, impr.nomest, impr.cfimp, impr.pivimp, impr.dtrisoa, impr.DSCANC from impr,ditg where impr.codimp = ditg.dittao and impr.codimp = ? and ditg.ngara5 = ?";
                Vector datiDitta =  sqlManager.getVector(selectDitta,new Object[] { ditta, gara });
                g1nprogg = ((JdbcParametro) datiDitta.get(0)).getStringValue();
                g1numordpl =  ((JdbcParametro) datiDitta.get(1)).getStringValue();
                dittao =  ((JdbcParametro) datiDitta.get(2)).getStringValue();
                nomimp =  ((JdbcParametro) datiDitta.get(3)).getStringValue();
                cfimp =  ((JdbcParametro) datiDitta.get(4)).getStringValue();
                pivimp =  ((JdbcParametro) datiDitta.get(5)).getStringValue();
                g_dtrisoa =  ((JdbcParametro) datiDitta.get(6)).dataValue();
                g_dscanc =  ((JdbcParametro) datiDitta.get(7)).dataValue();
                }
            }

            if(datiW_confcom.get(0)!=null){

              String commsgogg = null;
              if(((JdbcParametro) datiW_confcom.get(0)).getValue()!=null){
                commsgogg = ((JdbcParametro) datiW_confcom.get(0)).getValue().toString();

                if(singolaDitta){
                  HashMap<String, Object> parametri = new HashMap<String, Object>();
                  parametri.put("oggetto", commsgogg);
                  parametri.put("g1nprogg", g1nprogg);
                  parametri.put("g1numordpl", g1numordpl);
                  parametri.put("dittao", dittao);
                  parametri.put("nomimp", nomimp);
                  parametri.put("cfimp", cfimp);
                  parametri.put("pivimp", pivimp);
                  parametri.put("g_dtrisoa", g_dtrisoa);
                  parametri.put("g_dscanc", g_dscanc);
                  commsgogg = pgManager.sostituzioneMnemoniciDittaSingola(parametri);
                }

                HashMap<String, Object> parametri = new HashMap<String, Object>();
                parametri.put("oggetto", commsgogg);
                parametri.put("ngara", gara);
                parametri.put("codgar", codiceGara);
                parametri.put("codiga", codiga);
                parametri.put("g1codcig", g1codcig);
                parametri.put("oggetta", oggetta);
                parametri.put("g1destor", g1destor);
                parametri.put("oggettoga", oggettoga);
                parametri.put("dtepar", dtepar);
                parametri.put("otepar", otepar);
                parametri.put("dteoff", dteoff);
                parametri.put("oteoff", oteoff);
                parametri.put("desoff", desoff);
                parametri.put("oesoff", oesoff);
                parametri.put("nomtecrup", nomtecrup);
                parametri.put("cftecrup", cftecrup);
                parametri.put("inctec", inctec);
                commsgogg = pgManager.sostituzioneMnemonici(parametri);
                if(commsgogg.length()>300)
                  commsgogg=commsgogg.substring(0, 300);
              }
              page.setAttribute("initCOMMSGOGG",commsgogg, PageContext.REQUEST_SCOPE);
            }

            if(datiW_confcom.get(1)!=null)
              page.setAttribute("initCOMINTEST",
                  ((JdbcParametro) datiW_confcom.get(1)).getValue(), PageContext.REQUEST_SCOPE);

            String whereBusteAttiveWizard = page.getRequest().getParameter("whereBusteAttiveWizard");
            String whereBusteAttiveWizardImprdocg =  "";
            if(whereBusteAttiveWizard!=null && !"".equals(whereBusteAttiveWizard)){
              whereBusteAttiveWizardImprdocg = whereBusteAttiveWizard.replace("DOCUMGARA", "IMPRDOCG");
            }

            if(datiW_confcom.get(2)!=null){

              if(((JdbcParametro) datiW_confcom.get(2)).getValue()!=null){

                String commsgtes = null;
                commsgtes = ((JdbcParametro) datiW_confcom.get(2)).getValue().toString();

                if(ditta!=null && !"".equals(ditta)){
                  if(crittes!=null && crittes.longValue()==1){
                    page.setAttribute("initCOMMSGTES",commsgtes, PageContext.REQUEST_SCOPE);
                  }else if(new Long(1).equals(commodello)){
                    genere = ((JdbcParametro) datiW_confcom.get(4)).longValue();
                    if(keyAdd!=null && !"".equals(keyAdd)){
                      String testoMail;
                      try {
                        testoMail = pgManagerEst1.sostituzioneMnemonicoDocumentiMancanti(codiceGara, gara, ditta, genere, whereBusteAttiveWizard, whereBusteAttiveWizardImprdocg, commsgtes);
                        page.setAttribute("initCOMMSGTES",testoMail, PageContext.REQUEST_SCOPE);
                      } catch (Exception e) {
                        throw new JspException(
                            "Errore durante la selezione dei dati per la creazione dei documenti mancanti", e);
                      }
                    }
                  }
                }

                if(!new Long(1).equals(commodello)) {
                  if(singolaDitta){
                    HashMap<String, Object>  parametri = new HashMap<String, Object>() ;
                    parametri.put("oggetto", commsgtes);
                    parametri.put("g1nprogg", g1nprogg);
                    parametri.put("g1numordpl", g1numordpl);
                    parametri.put("dittao", dittao);
                    parametri.put("nomimp", nomimp);
                    parametri.put("cfimp", cfimp);
                    parametri.put("pivimp", pivimp);
                    parametri.put("g_dtrisoa", g_dtrisoa);
                    parametri.put("g_dscanc", g_dscanc);
                    commsgtes = pgManager.sostituzioneMnemoniciDittaSingola(parametri);
                  }
                  HashMap<String, Object> parametri = new HashMap<String, Object>();
                  parametri.put("oggetto", commsgtes);
                  parametri.put("ngara", gara);
                  parametri.put("codgar", codiceGara);
                  parametri.put("codiga", codiga);
                  parametri.put("g1codcig", g1codcig);
                  parametri.put("oggetta", oggetta);
                  parametri.put("g1destor", g1destor);
                  parametri.put("oggettoga", oggettoga);
                  parametri.put("dtepar", dtepar);
                  parametri.put("otepar", otepar);
                  parametri.put("dteoff", dteoff);
                  parametri.put("oteoff", oteoff);
                  parametri.put("desoff", desoff);
                  parametri.put("oesoff", oesoff);
                  parametri.put("nomtecrup", nomtecrup);
                  parametri.put("cftecrup", cftecrup);
                  parametri.put("inctec", inctec);
                  commsgtes = pgManager.sostituzioneMnemonici(parametri);
                  page.setAttribute("initCOMMSGTES",commsgtes, PageContext.REQUEST_SCOPE);
                }
              }
            }
          }
        } else {
          // nuovo da modulo comunicazioni ricevute, usando la funzione rispondi

          String idprg = "";
          String idcom = "";
          if (key.get("W_INVCOM.IDPRG") != null)
            idprg = ((JdbcParametro) key.get("W_INVCOM.IDPRG")).getStringValue();
          if (key.get("W_INVCOM.IDCOM") != null)
            idcom = ((JdbcParametro) key.get("W_INVCOM.IDCOM")).getStringValue();
          String sql = "select commsgogg from w_invcom where idprg = ? and idcom = ?";
          try {
            String commsgogg =  (String) sqlManager.getObject(sql,new Object[] { idprg, idcom });
            commsgogg = "R: " + commsgogg;
            if(commsgogg.length() >300)
              commsgogg = commsgogg.substring(0, 300);
            page.setAttribute("initCOMMSGOGG",commsgogg, PageContext.REQUEST_SCOPE);
            page.setAttribute("idprgComunicazPadre",idprg, PageContext.REQUEST_SCOPE);
            page.setAttribute("idcomComunicazPadre",idcom, PageContext.REQUEST_SCOPE);
          }catch(SQLException e) {
            throw new JspException(
                "Errore durante la selezione dei dati della comunicazione a cui rispondere", e);
          }

          sql = "select userkey1 from w_puser inner join w_invcom on w_puser.usernome=w_invcom.comkey1 and w_puser.userent = ? where idprg = ? and idcom = ?";
          try {
            String codimp =  (String) sqlManager.getObject(sql,new Object[] { "IMPR", idprg, idcom });
            page.setAttribute("initDESCODSOG", codimp, PageContext.REQUEST_SCOPE);
          }catch(SQLException e) {
            throw new JspException(
                "Errore durante la selezione dei dati della comunicazione a cui rispondere", e);
          }

          String commodello = page.getRequest().getParameter("commodello");
          if("1".equals(commodello)) {
            String comtipma = page.getRequest().getParameter("comtipma");
            page.setAttribute("initCOMMODELLO",commodello, PageContext.REQUEST_SCOPE);
            page.setAttribute("initCOMTIPMA",comtipma, PageContext.REQUEST_SCOPE);
          }
        }

      }else{
        String isWSwsdm = ConfigManager.getValore("wsdmconfigurazione.fascicoloprotocollo.url."+idconfi);
        isWSwsdm = UtilityStringhe.convertiNullInStringaVuota(isWSwsdm);
        if(!"".equals(isWSwsdm)){
          WSDMConfigurazioneOutType config = gestioneWSDMManager.wsdmConfigurazioneLeggi("FASCICOLOPROTOCOLLO",idconfi);
          if (config.isEsito()){
            String tipoWSDM = config.getRemotewsdm();
            if("JIRIDE".equals(tipoWSDM)){
              String idprg = "";
              String idcom = "";
              if (key.get("W_INVCOM.IDPRG") != null)
                idprg = ((JdbcParametro) key.get("W_INVCOM.IDPRG")).getStringValue();
              if (key.get("W_INVCOM.IDCOM") != null)
                idcom = ((JdbcParametro) key.get("W_INVCOM.IDCOM")).getStringValue();

              //Si deve controlloare se è abilitato il login comune
              Long syscon = new Long(profilo.getId());
              String wsdmLoginComune = ConfigManager.getValore(GestioneWSDMManager.PROP_WSDM_LOGIN_COMUNE+idconfi);
              if (wsdmLoginComune != null && "1".equals(wsdmLoginComune))
                syscon = new Long(-1);

              WSDMVerificaMailResType wsdmVerificaMailRes = gestioneWSDMManager.wsdmVerificaMail(syscon, idcom, idprg, idconfi);
              if(wsdmVerificaMailRes!= null){
                Long numAccettazioni = wsdmVerificaMailRes.getNumeroAccettazioni();
                Long numConsegne = wsdmVerificaMailRes.getNumeroConsegne();
                page.setAttribute("initNUMRICEVUTE", numAccettazioni + " ricevute di accettazione, " + numConsegne +" ricevute di consegna", PageContext.REQUEST_SCOPE);
              }else{
                page.setAttribute("initNUMRICEVUTE", "Dato non disponibile" , PageContext.REQUEST_SCOPE);
              }
            }
          }
        }


      }
    } catch (SQLException e) {
      throw new JspException(
          "Errore in fase di esecuzione delle select di inizializzazione", e);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore in fase di esecuzione delle select di inizializzazione", e);
    }

  }

}
