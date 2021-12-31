/*
 * Created on 28/01/2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Classe per l'inizializzazione della pagina lista lavorazioni e forniture
 *
 * @author Luca.Giacomazzo
 */
public class GestioneListaLavorazioniFornitureFunction extends
		AbstractFunzioneTag {

	public GestioneListaLavorazioniFornitureFunction(){
		super(1, new Class[]{String.class});
	}

	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

    GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
        pageContext, GeneManager.class);

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        pageContext, PgManagerEst1.class);

    String codiceTornata = ((String) params[0]);
    codiceTornata = codiceTornata.substring(codiceTornata.indexOf(":") + 1);

    // Determino se la la codifica automatica e' attiva
    boolean isCodificaAutomatica = geneManager.isCodificaAutomatica("GARE", "NGARA");
    if(isCodificaAutomatica)
    	pageContext.setAttribute("isCodificaAutomatica", "true", PageContext.REQUEST_SCOPE);
    else
    	pageContext.setAttribute("isCodificaAutomatica", "false", PageContext.REQUEST_SCOPE);

    boolean garaLottiConOffertaUnica = false;
    String temp = null;
    if(pageContext.getRequest().getParameter("tipologiaGara") != null)
    	temp = pageContext.getRequest().getParameter("tipologiaGara");
    else
    	temp = (String) pageContext.getAttribute("tipologiaGara", PageContext.REQUEST_SCOPE);
    if(temp != null && "3".equals(temp))
    	garaLottiConOffertaUnica = true;

    boolean esistonoLotti = true;
    boolean garaAggiudicata=false;

    if(garaLottiConOffertaUnica){
      try {
		List listaLotti = sqlManager.getListVector(
		          "select NGARA, MODLICG, DITTA from GARE where CODGAR1 = ?  and (GENERE is null or GENERE <> 3)",
					  new Object[]{codiceTornata});

	    // Per gare a lotti con offerta unica, senza codifica automatica,
	    // nel caso non sia stato definito alcun lotto, sia avvisa l'utente che non
	    // e' possibile aggiungere lavorazioni/forniture o importare senza aver
	    // definito almeno un lotto
    	if(listaLotti == null || (listaLotti != null && listaLotti.size() == 0))
    		esistonoLotti = false;
    	else{
    	  //Si deve controllare se esistono lotti con delle lavorazioni (MODLICG.GARE = 5,14,6 & occ. in GCAP)
          //e aggiudicati (DITTA.GARE valorizzato)
    	  String codiceLotto=null;
          Long conteggio=null;
          Long modlicg=null;
          String ditta=null;
          for(int i=0;i<listaLotti.size();i++){
            codiceLotto = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).stringValue();
            modlicg = SqlManager.getValueFromVectorParam(listaLotti.get(i), 1).longValue();
            ditta = SqlManager.getValueFromVectorParam(listaLotti.get(i), 2).stringValue();
            if(ditta!= null && !"".equals(ditta) && modlicg!=null && (modlicg.longValue()==5 || modlicg.longValue()==6 || modlicg.longValue()==14)){
              conteggio = (Long)sqlManager.getObject("select count(ngara) from gcap where ngara=?", new Object[]{codiceLotto});
              if(conteggio!=null && conteggio.longValue()>0){
                garaAggiudicata=true;
                break;
              }
            }
          }
    	}
      } catch (SQLException e) {
			throw new JspException(
	          "Errore durante la lettura dei lotti della gara", e);
      }catch (GestoreException e) {
        throw new JspException(
            "Errore durante la lettura dei lotti della gara" , e);
      }
    }

    if(esistonoLotti)
    	pageContext.setAttribute("esistonoLotti", "true", PageContext.REQUEST_SCOPE);
    else
    	pageContext.setAttribute("esistonoLotti", "false", PageContext.REQUEST_SCOPE);


    //Determino il valore del campo TIPFORN.TORN e FASGAR.GARE
    Long tipforn=null;
    Long fasgar=null;
    String ditta=null;
    Long ribcal = null;

    try {
      Vector datiGara = sqlManager.getVector("select tipforn,fasgar, ditta, ribcal from torn,gare where ngara=? and codgar1 = codgar", new Object[]{codiceTornata});
      if(datiGara!=null && datiGara.size()>0){
        tipforn = SqlManager.getValueFromVectorParam(datiGara, 0).longValue();
        fasgar = SqlManager.getValueFromVectorParam(datiGara, 1).longValue();
        ditta = SqlManager.getValueFromVectorParam(datiGara, 2).stringValue();
        ribcal = SqlManager.getValueFromVectorParam(datiGara, 3).longValue();
        if(ditta!= null && !"".equals(ditta))
          garaAggiudicata=true;
      }

      pageContext.setAttribute("tipoForniture", tipforn, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("fasgar", fasgar, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("garaAggiudicata", new Boolean(garaAggiudicata), PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("ribcal", ribcal, PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la valutazione del campo Tipo forniture(TORN.TIPFORN) e della fase della gara (GARE.FASGAR) e della ditta aggiudicataria (GARE.DITTA) " , e);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore durante la valutazione del campo Tipo forniture(TORN.TIPFORN) e della fase della gara (GARE.FASGAR) e della ditta aggiudicataria (GARE.DITTA)" , e);
    }

    //Si determina se è attiva l'integrazione AUR
    String integrazioneAUR="0";
    String urlWSAUR = ConfigManager.getValore("it.eldasoft.sil.pg.integrazioneAUR.ws.url");
    if(urlWSAUR != null && !"".equals(urlWSAUR)){
      integrazioneAUR ="1";
    }
    pageContext.setAttribute("integrazioneAUR", integrazioneAUR, PageContext.REQUEST_SCOPE);


    //Si determina se è attiva l'integrazione con WSERP
    String integrazioneWSERP="0";
    String urlWSERP = ConfigManager.getValore("wserp.erp.url");
    if(urlWSERP != null && !"".equals(urlWSERP)){
      integrazioneWSERP ="1";
      GestioneWSERPManager gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
          pageContext, GestioneWSERPManager.class);

      try {
        WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
        if (configurazione.isEsito()) {
          String tipoWSERP = configurazione.getRemotewserp();
          pageContext.setAttribute("tipoWSERP", tipoWSERP, PageContext.REQUEST_SCOPE);
        }
      } catch (GestoreException e) {
        UtilityStruts.addMessage(this.getRequest(), "error",
            "wserpconfigurazione.erp.configurazioneleggi.remote.error",new Object[]{":\r\n Configurazione integrazione con sistema ERP non corretta"});
      }

    }
    pageContext.setAttribute("integrazioneWSERP", integrazioneWSERP, PageContext.REQUEST_SCOPE);

    try {
      if(!garaLottiConOffertaUnica){
        boolean importiDifferenti=false;
        Object importoSoggRibassoObj = sqlManager.getObject("select sum(coalesce(quanti,0) * coalesce(prezun,0)) from gcap where ngara=? and dittao is null and sogrib='2'", new Object[]{codiceTornata});

        //Importo soggetto a ribasso
        Double importoSoggRibasso = new Double(0);
        if(importoSoggRibassoObj!=null)
          importoSoggRibasso= pgManagerEst1.getImportoDaObject(importoSoggRibassoObj);

        String importoFormattato = PgManagerEst1.convertiMoney5(UtilityNumeri.convertiImporto(
            importoSoggRibasso, 5));
        pageContext.setAttribute("importoSoggRibasso", importoFormattato, PageContext.REQUEST_SCOPE);

        //Importo non soggetto a ribasso
        Double importoNoRibasso = new Double(0);
        Long numLavorazioniNoRibasso = (Long)sqlManager.getObject("select count(ngara) from gcap where ngara=? and dittao is null and sogrib='1' and solsic='2'", new Object[]{codiceTornata});
        if(numLavorazioniNoRibasso!=null && numLavorazioniNoRibasso.longValue()>0){
          Object importoNoRibassoObj =  sqlManager.getObject("select sum(coalesce(quanti,0) * coalesce(prezun,0)) from gcap where ngara=? and dittao is null and sogrib='1' and solsic='2'", new Object[]{codiceTornata});

          if(importoNoRibassoObj!=null)
            importoNoRibasso= pgManagerEst1.getImportoDaObject(importoNoRibassoObj);

        }
        importoFormattato = PgManagerEst1.convertiMoney5(UtilityNumeri.convertiImporto(
            importoNoRibasso, 5));
        pageContext.setAttribute("importoNoRibasso", importoFormattato, PageContext.REQUEST_SCOPE);

        //Importo sicurezza
        Double importoSicurezza = new Double(0);
        Long numLavorazioniSicurezza = (Long)sqlManager.getObject("select count(ngara) from gcap where ngara=? and dittao is null and solsic='1'", new Object[]{codiceTornata});
        if(numLavorazioniSicurezza != null && numLavorazioniSicurezza.longValue() >0 ){
          Object importoSicurezzaObj =  sqlManager.getObject("select sum(coalesce(quanti,0) * coalesce(prezun,0)) from gcap where ngara=? and dittao is null and solsic='1'", new Object[]{codiceTornata});

          if(importoSicurezzaObj!=null)
            importoSicurezza= pgManagerEst1.getImportoDaObject(importoSicurezzaObj);


        }
        importoFormattato = PgManagerEst1.convertiMoney5(UtilityNumeri.convertiImporto(
            importoSicurezza, 5));
        pageContext.setAttribute("importoSicurezza", importoFormattato, PageContext.REQUEST_SCOPE);

        //Importo totale
        Double totale = new Double(importoSoggRibasso.doubleValue() + importoNoRibasso.doubleValue() + importoSicurezza.doubleValue());
        importoFormattato =UtilityNumeri.convertiImporto(totale, 2);
        pageContext.setAttribute("totale", importoFormattato, PageContext.REQUEST_SCOPE);

        if((new Long(3)).equals(ribcal)){
          //Totale pesi
          Object pesoObj = sqlManager.getObject("select sum(coalesce(peso,0)) from gcap where ngara=? and dittao is null", new Object[]{codiceTornata});
          Double peso = new Double(0);
          if(pesoObj!=null)
            peso= pgManagerEst1.getImportoDaObject(pesoObj);
          String pesoFormattato = UtilityNumeri.convertiImporto(peso, 3);
          //Si tolgono gli eventuali zeri finali della parte decimale
          char separatore='#';
          int posSeparatore=0;
          if(pesoFormattato.indexOf(",")>0){
            separatore = ',';
            posSeparatore = pesoFormattato.indexOf(",");
          }else if(pesoFormattato.indexOf(".")>0){
            separatore = '.';
            posSeparatore = pesoFormattato.indexOf(".");
          }
          if(separatore!='#'){
            while((pesoFormattato.charAt(pesoFormattato.length()-1) == '0' || pesoFormattato.charAt(pesoFormattato.length()-1) == separatore) && posSeparatore<pesoFormattato.length()){
              pesoFormattato = pesoFormattato.substring(0, pesoFormattato.length()-1);

            }
          }
          pageContext.setAttribute("totalePesi", pesoFormattato, PageContext.REQUEST_SCOPE);
        }

        //Importi di gara
        Double importoGara = new Double(0);
        Double importoGaraNoRibasso = new Double(0);
        Double importoGaraRibasso = new Double(0);
        Double importoGaraSicurezza = new Double(0);
        String onsogrib =null;
        //String selectGare="select impapp ,impapp - coalesce(impnrl,0) - coalesce(impsic,0), impnrl,impsic from gare where ngara=?";
        String selectGare="select impapp , impnrl,impsic, impapp - coalesce(impnrl,0) - coalesce(impsic,0) -  coalesce(onprge,0),"
            + "impapp - coalesce(impnrl,0) - coalesce(impsic,0) , onsogrib, coalesce(impnrl,0) +  coalesce(onprge,0)  from gare where ngara=?";
        Vector datiGara = sqlManager.getVector(selectGare, new Object[]{codiceTornata});
        if(datiGara!=null && datiGara.size()>0){
          importoGara = SqlManager.getValueFromVectorParam(datiGara, 0).doubleValue();

          importoGaraSicurezza  = SqlManager.getValueFromVectorParam(datiGara, 2).doubleValue();
          onsogrib = SqlManager.getValueFromVectorParam(datiGara, 5).getStringValue();
          if("1".equals(onsogrib)){
            importoGaraRibasso  = SqlManager.getValueFromVectorParam(datiGara, 4).doubleValue();
            importoGaraNoRibasso  = SqlManager.getValueFromVectorParam(datiGara, 1).doubleValue();
          }else{
            importoGaraRibasso = SqlManager.getValueFromVectorParam(datiGara, 3).doubleValue();
            importoGaraNoRibasso  = SqlManager.getValueFromVectorParam(datiGara, 6).doubleValue();
          }

        }
        
        if(importoGaraRibasso == null){
          importoGaraRibasso = new Double(0);
        }
        
        //Importo totale
        importoFormattato = PgManagerEst1.convertiMoney5(UtilityNumeri.convertiImporto(
            importoGara, 5));
        pageContext.setAttribute("totaleGara", importoFormattato, PageContext.REQUEST_SCOPE);

        //Importo soggetto a ribasso
        importoFormattato = PgManagerEst1.convertiMoney5(UtilityNumeri.convertiImporto(
            importoGaraRibasso, 5));
        pageContext.setAttribute("importoGaraSoggRibasso", importoFormattato, PageContext.REQUEST_SCOPE);

        //Importo non soggetto a ribasso
        importoFormattato = PgManagerEst1.convertiMoney5(UtilityNumeri.convertiImporto(
            importoGaraNoRibasso, 5));
        pageContext.setAttribute("importoGaraNoRibasso", importoFormattato, PageContext.REQUEST_SCOPE);

        //Importo sicurezza
        importoFormattato = PgManagerEst1.convertiMoney5(UtilityNumeri.convertiImporto(
            importoGaraSicurezza, 5));
        pageContext.setAttribute("importoGaraSicurezza", importoFormattato, PageContext.REQUEST_SCOPE);

        Long occorrenze = (Long)sqlManager.getObject("select count(ngara) from gcap where ngara=? and dittao is null",new Object[]{codiceTornata});
        if(occorrenze!=null && occorrenze.longValue() >0 &&  UtilityNumeri.confrontaDouble(importoGaraRibasso.doubleValue(), importoSoggRibasso.doubleValue(), 2) != 0 )
          importiDifferenti =true;

        pageContext.setAttribute("importiDifferenti", new Boolean(importiDifferenti), PageContext.REQUEST_SCOPE);
      }

    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura degli importi di riepilogo", e);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore durante la lettura degli importi di riepilogo", e);
    }
    return null;
	}

}