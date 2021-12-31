/*
 * Created on 02-09-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per controllare che i dati obbligatori richiesti dalla
 * integrazione WSERP
 *
 * @author Cristian Febas
 */
public class GetWSERPDatiObbligatoriFunction extends AbstractFunzioneTag {



  public GetWSERPDatiObbligatoriFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String ngara = (String) params[1];
    Boolean controlliSuperati=null;
    String tipoWSERP = (String) params[2];

    GestioneWSERPManager gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager", pageContext, GestioneWSERPManager.class);

    HashMap<String, Object> esitoControlli = null;
    try {
      esitoControlli = gestioneWSERPManager.controlloDatiObbligatori(ngara,tipoWSERP);
    } catch (NumberFormatException e) {
      pageContext.setAttribute("erroreOperazione","1",PageContext.REQUEST_SCOPE);
      throw new JspException("Errore nella verifica dei dati", e);
    } catch (SQLException e) {
      pageContext.setAttribute("erroreOperazione","1",PageContext.REQUEST_SCOPE);
      throw new JspException("Errore nella verifica dei dati", e);
    } catch (GestoreException e) {
      pageContext.setAttribute("erroreOperazione","1",PageContext.REQUEST_SCOPE);
      throw new JspException("Errore nella verifica dei dati", e);
    }

    if(esitoControlli!=null){
      pageContext.setAttribute("messaggi",esitoControlli.get("msg"), PageContext.REQUEST_SCOPE);
      controlliSuperati = (Boolean)esitoControlli.get("esito");
    }

    return controlliSuperati.toString();
  }





}