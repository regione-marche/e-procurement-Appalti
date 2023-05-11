/*
 * Created on 19/04/10
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

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class ControlliComponentiCommissioneFunction extends AbstractFunzioneTag {

	public ControlliComponentiCommissioneFunction() {
		super(5, new Class[] { PageContext.class, String.class, String.class, String.class, String.class });
	}

	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

	    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
	            pageContext, SqlManager.class);

	    GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
            pageContext, GeneManager.class);

	    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
            pageContext, TabellatiManager.class);

		String ngara = new String((String) params[1]);
		String controlloAggiud = new String((String) params[2]);
		String ngaraLotto = new String((String) params[3]);
		String garaOffertaUnica = new String((String) params[4]);
		String esitoControllo = "OK";

		HttpSession session = pageContext.getSession();

        String profiloAttivo = (String) session.getAttribute(CostantiGenerali.PROFILO_ATTIVO);
        String tipoMsg="";
        Long modlic = null;
        try {
          String selectModlic="select modlicg from gare where ngara = ?";
          String par[] = new String[] {ngara};
          if("true".equals(garaOffertaUnica) || "bustalotti=1".equals(garaOffertaUnica)) {
            if("true".equals(controlloAggiud) || "bustalotti=1".equals(garaOffertaUnica)) {
              par[0]=ngaraLotto;
            }else {
              selectModlic="select modlic from torn where codgar = ?";
              par[0]=ngara;
            }
          }
          modlic= (Long) sqlManager.getObject(selectModlic,par);
        } catch (SQLException e) {
          throw new JspException("Errore nella lettura del campo modlicg per il conteggio dei componenti della commissione",e);
        }

		if (new Long(6).equals(modlic) && geneManager.getProfili().checkProtec(profiloAttivo, "FUNZ", "VIS", "ALT.GARE.ControlliComponentiCommissione")) {
		  String descrTab=tabellatiManager.getDescrSupplementare("A1z15", "1");
		  if(descrTab!=null && !"".equals(descrTab)) {
		    Long numAtteso = null;
		    try {
		      numAtteso = new Long(descrTab);
  		    } catch (Exception e) {
                throw new JspException("Il numero di componenti della commissione specificato nel parametro di controllo non ha un formato valido",e);
            }

		    boolean eseguireControllo=true;
		    if("true".equals(controlloAggiud)) {
		      descrTab=tabellatiManager.getDescrSupplementare("A1z15", "2");
		      if("0".equals(descrTab))
		        eseguireControllo = false;

		    }
		    if(numAtteso!=null && numAtteso.longValue()>0 && eseguireControllo) {
		      try {
                Long conteggio = (Long) sqlManager.getObject("select count(*) from gfof where ngara2 = ?",new Object[] { ngara });
                if(conteggio== null)
                  conteggio = new Long(0);
                if(conteggio.longValue() < numAtteso.longValue()) {
                  esitoControllo = "NOK";
                  String msgResource = this.resBundleGenerale.getString("label.controlliCommissione");
                  msgResource = msgResource.replace("{0}",numAtteso.toString());
                  String messaggio = "<br><br><font color='#0000FF'><b>ATTENZIONE:</b>" +  msgResource + "</font>";

                  if("true".equals(controlloAggiud)) {
                    if("1".equals(descrTab) )
                      tipoMsg="B";
                    else if("2".equals(descrTab) )
                      tipoMsg="NB";
                    pageContext.setAttribute("tipoMsgCommissione", tipoMsg, PageContext.REQUEST_SCOPE);
                  }
                  if("true".equals(controlloAggiud) && tipoMsg=="B")
                    messaggio =  msgResource ;
                  pageContext.setAttribute("msgCommissione",messaggio, PageContext.REQUEST_SCOPE);
                }
              } catch (SQLException e) {
                  throw new JspException("Errore nel conteggio dei componenti della commissione",e);
              }
		    }

		  }

		}

	    return esitoControllo;

	}

}
