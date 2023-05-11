/*
 * Created on 01/12/22
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneProgrammazioneManager;
import it.eldasoft.utils.spring.UtilitySpring;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 * @author Manuel Bridda
 */
public class GetRDARDIFunction extends AbstractFunzioneTag {

  public GetRDARDIFunction(){
    super(5, new Class[]{PageContext.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    
    String type = (String) params[1];
    String codgar = (String) params[2];
    String ngara = (String) params[3];
    String getLottiXRda = (String) params[4];
    
    GestioneProgrammazioneManager gestioneProgrammazioneManager = (GestioneProgrammazioneManager) UtilitySpring.getBean("gestioneProgrammazioneManager",
        pageContext, GestioneProgrammazioneManager.class);
    
    Logger logger = Logger.getLogger(GetRDARDIFunction.class);
      
    JSONObject resp = null;
    
    try {
      ProfiloUtente profiloUtente = (ProfiloUtente) pageContext.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE, PageContext.SESSION_SCOPE);
      Long syscon = new Long(profiloUtente.getId());
      String uffint = (String) pageContext.getSession().getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
      
      if(GestioneProgrammazioneManager.RDA_COLLEGATE.equals(type) && StringUtils.isNotEmpty(codgar)) {
        resp = gestioneProgrammazioneManager.recuperaRdaCollegate(codgar,ngara,false,"true".equals(type),"true".equals(getLottiXRda));
      }else {
        resp = gestioneProgrammazioneManager.consultaRda(syscon,uffint,type,null,null,null,null);
        if (logger.isDebugEnabled()) logger.debug("Chiamata al servizio di consultazione delle RdA eseguita");
        }
      if(resp!=null && resp.getBoolean("esito")) {
        pageContext.getRequest().setAttribute("listaRDARDI",  resp.getJSONArray("listaRdA"));
        return resp.getJSONArray("listaRdA").size()+"";
      }else {
        pageContext.getRequest().setAttribute("listaRDARDI", new JSONArray());
        if(resp!=null && !resp.getBoolean("esito") && resp.get("messaggio")!=null && !"Nessun intervento/fabbisogno trovato".equals(resp.get("messaggio"))) {
            logger.error("Esito negativo per la consultazione delle RdA: "+resp.get("messaggio"));
          pageContext.getRequest().setAttribute("errorMsgRda",  resp.get("messaggio"));
          return "-1";
        }
       }
    }catch (GestoreException e) {
      e.printStackTrace();
     return "-1";
    } 
    catch (Exception e) {
      e.printStackTrace();
    }
    
    return "0";
  }
}