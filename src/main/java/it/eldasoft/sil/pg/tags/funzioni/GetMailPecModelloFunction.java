/*
 * Created on 09-12-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Funzione adoperata per recuperare il modello di mail da inviare per l'abilitazione degli operatori
 *
 * @author Cristian Febas
 */
public class GetMailPecModelloFunction extends AbstractFunzioneTag {

  public GetMailPecModelloFunction() {
    super(4, new Class[] {PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String genereModelloComunicazione = (String) params[1];

    Vector valoriModello = new Vector(2);
    genereModelloComunicazione = UtilityStringhe.convertiNullInStringaVuota(genereModelloComunicazione);
    if ("".equals(genereModelloComunicazione)) {
      return "0";
    }

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", pageContext, TabellatiManager.class);
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager", pageContext, PgManager.class);
    ProfiloUtente profilo = (ProfiloUtente) pageContext.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);


    String selectModello = "select commsgogg,commsgtes,comintest from w_confcom where genere = ? ";

    try {

      valoriModello = sqlManager.getVector(selectModello, new Object[] {genereModelloComunicazione });
      if (valoriModello != null) {

        String oggettoMail = sqlManager.getValueFromVectorParam(valoriModello, 0).getStringValue();
        String testoMail = sqlManager.getValueFromVectorParam(valoriModello, 1).getStringValue();
		String abilitaIntestazioneVariabile = sqlManager.getValueFromVectorParam(valoriModello, 2).getStringValue();
		String ngara=(String) params[3];

		if (genereModelloComunicazione.equals("52") && testoMail!=null && !"".equals(testoMail)) {
		  //Sostituzione mnemonici
		  String isOda = (String) params[2];
		  String nrepat = "";
		  String daatto= "";
		  Vector datiOrdine = sqlManager.getVector("select nrepat,daatto from gare where ngara=?", new Object[]{ngara});
		  if(datiOrdine!=null){
		    nrepat = sqlManager.getValueFromVectorParam(datiOrdine, 0).getStringValue();
		    Timestamp dataTimestamp = sqlManager.getValueFromVectorParam(datiOrdine, 1).dataValue();
		    if(dataTimestamp!=null){
		      Date data = new Date(dataTimestamp.getTime());
		      daatto = UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA);
		    }

		  }
		  String REPLACEMENT_NUMERO_ORDINE = "#G1NREPAT#";
		  String REPLACEMENT_TESTO_DATA_ORDINE = "#G1DAATTO#";
		  String REPLACEMENT_TIPOLOGIA_ORDINE = "#alla richiesta di offerta|all'ordine di acquisto#";
		  String REPLACEMENT_GARA = "#G1NGARACO#";
		  testoMail = StringUtils.replace(testoMail, REPLACEMENT_NUMERO_ORDINE, nrepat);
		  testoMail = StringUtils.replace(testoMail, REPLACEMENT_TESTO_DATA_ORDINE, daatto);
		  String[] tipologie = StringUtils.replace(REPLACEMENT_TIPOLOGIA_ORDINE, "#", "").split("\\|");
		  testoMail = StringUtils.replace(testoMail, REPLACEMENT_TIPOLOGIA_ORDINE, ("true".equals(isOda) ? tipologie[1] : tipologie[0]));
		  testoMail = StringUtils.replace(testoMail, REPLACEMENT_GARA, ngara);
		}

		if (genereModelloComunicazione.equals("51")) {
		  String dataOdiernaStr = UtilityDate.convertiData(UtilityDate.getDataOdiernaAsDate(), UtilityDate.FORMATO_GG_MM_AAAA);
		  testoMail = testoMail.replace("dd/mm/yyyy", dataOdiernaStr);
		}

		if(ngara!=null){
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
          String selectGare="select g.codgar1,g.codiga,g.codcig,g.not_gar,t.destor,t.dtepar,t.otepar,t.dteoff,t.oteoff,t.desoff,t.oesoff from gare g,torn t where g.ngara=? and g.codgar1=t.codgar";
          Vector datiGara= sqlManager.getVector(selectGare, new Object[]{ngara});
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
          oggettoga=(String)sqlManager.getObject("select oggetto from garealbo where ngara=?", new Object[]{ngara});

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


          HashMap<String,Object> parametri = new HashMap<String,Object>();
          parametri.put("ngara", ngara);
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

          if(oggettoMail!=null){
            parametri.put("oggetto", oggettoMail);
            oggettoMail = pgManager.sostituzioneMnemonici(parametri);
            if(oggettoMail.length()>300)
              oggettoMail=oggettoMail.substring(0, 300);
          }
          if(testoMail!=null){
            parametri.put("oggetto", testoMail);
            testoMail = pgManager.sostituzioneMnemonici(parametri);
          }
		}

        pageContext.setAttribute("oggettoMail", oggettoMail, PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("testoMail", testoMail, PageContext.REQUEST_SCOPE);
				pageContext.setAttribute("abilitaIntestazioneVariabile", abilitaIntestazioneVariabile, PageContext.REQUEST_SCOPE);

        String descTab = tabellatiManager.getDescrTabellato("G_032", "1");
        boolean result = false;
        if(descTab != null)
          result = descTab.startsWith("2");
        if (result){
          String numUfficioAppartenenza = profilo.getUfficioAppartenenza();
          String descUfficioAppartenenza = "";
          if (numUfficioAppartenenza != null && !"".equals(numUfficioAppartenenza))
            descUfficioAppartenenza = tabellatiManager.getDescrTabellato("G_022", numUfficioAppartenenza);
          pageContext.setAttribute("mittenteMail", descUfficioAppartenenza, PageContext.REQUEST_SCOPE);
        } else
          pageContext.setAttribute("mittenteMail", profilo.getNome(), PageContext.REQUEST_SCOPE);
      }

    } catch (SQLException e) {
      throw new JspException("Errore nella lettura della mail/PEC dell'impresa)", e);
    } catch (GestoreException e) {
      throw new JspException("Errore nella lettura dei dati del modello da applicare alla comunicazione)", e);
    }

    return "1";
  }

}
