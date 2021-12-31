/*
 * Created on 26/nov/09
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
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Inizializzazione delle pagine a lista per le gare a lotti con offerta unica
 * per visualizzare e inserire le offerte tecniche/economiche di una ditta per
 * i diversi lotti
 *
 * Questa classe si ispira alla classe GestioneFasiGaraFunction
 *
 * @author Luca.Giacomazzo
 */
public class GestioneFaseAstaElettronicaFunction extends AbstractFunzioneTag {

	public GestioneFaseAstaElettronicaFunction(){
	  super(3, new Class[] { PageContext.class,String.class, String.class });
	}


	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

	  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
	        pageContext, SqlManager.class);

	  String paginaAsta = UtilityTags.getParametro(pageContext,"pgAsta");
	  String ngara = (String)params[1];
	  String bustalotti="";
	  if(params[2]!=null)
	    bustalotti=(String)params[2];

	  Long fasgar = null;
	  try {
        fasgar = (Long)sqlManager.getObject("select fasgar from gare where ngara=?", new Object[]{ngara});
      } catch (SQLException e) {
        throw new JspException("Errore nella letura del fasgar della gara " + ngara, e);
      }

	  if((paginaAsta == null || "".equals(paginaAsta)) && new Long(7).equals(fasgar)){
	    pageContext.setAttribute("pgAsta", new Long(3), PageContext.REQUEST_SCOPE);
	  }else if(paginaAsta == null || "".equals(paginaAsta)){
	    HttpSession session = pageContext.getSession();

	    String profiloAttivo = (String) session.getAttribute("profiloAttivo");

	    GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
	        pageContext, GeneManager.class);
	    Long pgAsta = null;
	    String pagina="GARE-scheda.FASIGARA";
	    if("1".equals(bustalotti))
	      pagina="GARE-APERTURAOFFERTEAGGIUDPROV";
	    if(geneManager.getProfili().checkProtec(profiloAttivo,"FUNZ","VIS","VIS.GARE." + pagina + ".AMMISSIONE"))
	        pgAsta = new Long(1);
	    else if(geneManager.getProfili().checkProtec(profiloAttivo,"FUNZ","VIS","VIS.GARE." + pagina + ".INVITO"))
	      pgAsta = new Long(2);
	    else
	      pgAsta = new Long(3);
	    pageContext.setAttribute("pgAsta",  pgAsta, PageContext.REQUEST_SCOPE);
	    paginaAsta = pgAsta.toString();
	  }else if("1".equals(paginaAsta)){
	    pageContext.setAttribute("pgAsta", new Long(1), PageContext.REQUEST_SCOPE);
	  }else if("2".equals(paginaAsta)){
        pageContext.setAttribute("pgAsta", new Long(2), PageContext.REQUEST_SCOPE);
      }else if("3".equals(paginaAsta)){
        pageContext.setAttribute("pgAsta", new Long(3), PageContext.REQUEST_SCOPE);
      }

	  if("2".equals(paginaAsta)){
  	    //Caricamento dati da AEFASI
  	    try {
          String dataorainiString = sqlManager.getDBFunction("DATETIMETOSTRING",
              new String[] { "aefasi.dataoraini" });
          String dataorafineString = sqlManager.getDBFunction("DATETIMETOSTRING",
              new String[] { "aefasi.dataorafine" });
          List datiAefasi = sqlManager.getListVector("select ngara, id, datini, oraini, durmin, durmax, tbase, " + dataorainiString + "," + dataorafineString +" from aefasi where ngara=? order by numfase", new Object[]{ngara});
          pageContext.setAttribute("faseAsta", datiAefasi, PageContext.REQUEST_SCOPE);
        } catch (SQLException e) {
          throw new JspException("Errore nell'estrarre le fasi dell'asta elettronica  della gara " + ngara, e);
        }
	  }
	  return null;
  }

}