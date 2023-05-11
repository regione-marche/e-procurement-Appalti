package it.eldasoft.sil.pg.web.struts.permessi;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetListaPermessiUtentiGaraTelematicaAction extends Action {

	/**
	 * Manager per la gestione delle interrogazioni di database.
	 */
	private SqlManager sqlManager;

	private GeneManager geneManager;

	/**
	 * @param sqlManager
	 *        the sqlManager to set
	 */
	public void setSqlManager(SqlManager sqlManager) {
	  this.sqlManager = sqlManager;
	}

	public void setGeneManager(GeneManager geneManager) {
	    this.geneManager = geneManager;
	  }

	@Override
  public final ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		DataSourceTransactionManagerBase.setRequest(request);

	    response.setHeader("cache-control", "no-cache");
	    response.setContentType("text/text;charset=utf-8");
	    PrintWriter out = response.getWriter();

	    JSONObject result = new JSONObject();
	    int total = 0;
	    int totalAfterFilter = 0;

	    String operation = request.getParameter("operation");
	    String codgar = request.getParameter("codgar");

	    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
	    		CostantiGenerali.PROFILO_UTENTE_SESSIONE);
	    String ufficioAppartenza = null;
	    if (StringUtils.isNotEmpty(profiloUtente.getUfficioAppartenenza())) {
	    	ufficioAppartenza = profiloUtente.getUfficioAppartenenza();
	    }

	    boolean abilitazioneFiltroProfilo = this.geneManager.getProfili().checkProtec(
	        (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS",
	        "ALT.GENE.associazioneUffintAbilitata");
	    boolean isAssocUffintAbilitata = false;
	    String codein1 = null;
	    if ("1".equals(ConfigManager.getValore("it.eldasoft.associazioneUffintAbilitata")) && abilitazioneFiltroProfilo) {
	    	isAssocUffintAbilitata = true;
	    	codein1 = (String) request.getSession().getAttribute(
	    			CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
	    }

	    try {
	        List<Object> parameters = new ArrayList<Object>();
	    	String selectUSR = "select v_usrsys_matricolario.syslogin, "
				+ " v_usrsys_matricolario.sysute, "
				+ " v_usrsys_matricolario.email, "
				+ " v_usrsys_matricolario.syscon, "
				+ " v_usrsys_matricolario.area, "
				+ " v_usrsys_matricolario.direzione, "
				+ " v_usrsys_matricolario.settore, "
				+ " v_usrsys_matricolario.municipio, "
				+ " v_usrsys_matricolario.ufficio, "
				+ " g_permessi.numper, "
				+ " g_permessi.autori, "
				+ " g_permessi.propri, "
				+ " g_permessi.codgar, "
				+ " g_permessi.meruolo as meruolog, "
				+ " v_usrsys_matricolario.meruolo as meruolou, "
				+ " v_usrsys_matricolario.sysdisab ";

	    	String selectUSR_mod = "select v_usrsys_matricolario.syslogin, "
				+ " v_usrsys_matricolario.sysute, "
				+ " v_usrsys_matricolario.email, "
				+ " v_usrsys_matricolario.syscon, "
				+ " v_usrsys_matricolario.area, "
				+ " v_usrsys_matricolario.direzione, "
				+ " v_usrsys_matricolario.settore, "
				+ " v_usrsys_matricolario.municipio, "
				+ " v_usrsys_matricolario.ufficio, "
				+ " null as numper, "
				+ " null as autori, "
				+ " null as propri, "
				+ " null as codgar, "
				+ " null as meruolog, "
				+ " v_usrsys_matricolario.meruolo as meruolou, "
				+ " v_usrsys_matricolario.sysdisab ";

	    	if ("VISUALIZZA".equals(operation)) {
	    		if (isAssocUffintAbilitata) {
	    			selectUSR = selectUSR
						+ " from v_usrsys_matricolario, g_permessi, usr_ein "
						+ " where g_permessi.syscon = v_usrsys_matricolario.syscon "
						+ " and usr_ein.syscon = v_usrsys_matricolario.syscon "
						+ " and usr_ein.codein = ? "
						+ " and g_permessi.codgar = ? "
						+ " union "
			            + selectUSR
			            + " from v_usrsys_matricolario, g_permessi "
			            + " where g_permessi.syscon = v_usrsys_matricolario.syscon  "
			            + " and g_permessi.codgar = ? "
			            + " and syspwbou LIKE '%ou89%'";
	    			parameters.add(codein1);
	    			parameters.add(codgar);
	    			parameters.add(codgar);
	    		} else {
					selectUSR = selectUSR
						+ " from v_usrsys_matricolario, g_permessi "
						+ " where g_permessi.syscon = v_usrsys_matricolario.syscon "
						+ " and g_permessi.codgar = ? ";
					parameters.add(codgar);
    			}

	    	} else if ("MODIFICA".equals(operation)) {
	    		if (isAssocUffintAbilitata) {
	    			selectUSR = selectUSR
	    				+ " from v_usrsys_matricolario, g_permessi, usr_ein "
	    				+ " where v_usrsys_matricolario.syscon = g_permessi.syscon "
	    				+ " and usr_ein.syscon = v_usrsys_matricolario.syscon "
	    				+ " and g_permessi.codgar = ? "
	    				+ " and usr_ein.codein = ? "
	    				+ " union "
                        + selectUSR
                        + " from v_usrsys_matricolario, g_permessi "
                        + " where g_permessi.syscon = v_usrsys_matricolario.syscon  "
                        + " and g_permessi.codgar = ? "
                        + " and syspwbou LIKE '%ou89%'"
	    				+ " union " + selectUSR_mod
	    				+ " from v_usrsys_matricolario, usr_ein "
	    				+ " where usr_ein.syscon = v_usrsys_matricolario.syscon "
	    				+ " and usr_ein.codein = ? "
	    				+ " and v_usrsys_matricolario.syscon not in ("
		    					+ " select v_usrsys_matricolario.syscon "
		    					+ " from v_usrsys_matricolario, g_permessi, usr_ein "
		    					+ " where v_usrsys_matricolario.syscon = g_permessi.syscon "
		    					+ " and usr_ein.syscon = v_usrsys_matricolario.syscon "
		    					+ " and g_permessi.codgar = ? "
		    					+ " and usr_ein.codein = ? "
	    				+ ") and (v_usrsys_matricolario.sysdisab is null or v_usrsys_matricolario.sysdisab = '0') ";
	    			parameters.add(codgar);
	    			parameters.add(codein1);
	    			parameters.add(codgar);
	    			parameters.add(codein1);
	    			parameters.add(codgar);
	    			parameters.add(codein1);

	    			if (ufficioAppartenza != null) {
			        	  selectUSR = selectUSR
			        	  	+ " and (v_usrsys_matricolario.sysuffapp = ? or v_usrsys_matricolario.sysuffapp is null)" ;
			        	  parameters.add(ufficioAppartenza);
			        }

	    			selectUSR += " union "
	    	              + selectUSR_mod
	    	              + " from v_usrsys_matricolario "
	    	              + " where  v_usrsys_matricolario.syspwbou LIKE '%ou89%' and v_usrsys_matricolario.syscon not in"
	    	              + " (select v_usrsys_matricolario.syscon from v_usrsys_matricolario, g_permessi"
	    	              + " where g_permessi.syscon = v_usrsys_matricolario.syscon  and g_permessi.codgar = ? "
	    	              + " and syspwbou LIKE '%ou89%') and (v_usrsys_matricolario.sysdisab is null or v_usrsys_matricolario.sysdisab = '0')";
	    			parameters.add(codgar);

	    	          if (ufficioAppartenza != null) {
	    	            selectUSR = selectUSR
	    	                + " and (v_usrsys_matricolario.sysuffapp = ? or v_usrsys_matricolario.sysuffapp is null)" ;
	    	            parameters.add(ufficioAppartenza);
	    	          }

		    	} else {
		    		selectUSR = selectUSR
		    			+ " from v_usrsys_matricolario, g_permessi "
		    			+ " where v_usrsys_matricolario.syscon = g_permessi.syscon "
		    			+ " and g_permessi.codgar = ? "
		    			+ " union " + selectUSR_mod
		    			+ " from v_usrsys_matricolario "
	    				+ " where v_usrsys_matricolario.syscon not in ("
		    					+ " select v_usrsys_matricolario.syscon "
		    					+ " from v_usrsys_matricolario, g_permessi "
		    					+ " where v_usrsys_matricolario.syscon = g_permessi.syscon "
		    					+ " and g_permessi.codgar = ? "
	    				+ ") and (v_usrsys_matricolario.sysdisab is null or v_usrsys_matricolario.sysdisab = '0') ";
		    		parameters.add(codgar);
		    		parameters.add(codgar);

		    		if (ufficioAppartenza != null) {
	                  selectUSR = selectUSR
	                    + " and (v_usrsys_matricolario.sysuffapp = ? or v_usrsys_matricolario.sysuffapp is null)" ;
	                  parameters.add(ufficioAppartenza);
	                }
		    	}

	    	}

	    	List<?> hmUSR = this.sqlManager.getListHashMap(selectUSR, parameters.toArray());
	    	if (hmUSR != null && hmUSR.size() > 0) {

	      	  for (int i=0; i < hmUSR.size(); i++) {
	    		  HashMap<String,Object> h1 = (HashMap<String,Object>) hmUSR.get(i);
	    		  JdbcParametro jdbcPar = (JdbcParametro) h1.get("SYSDISAB");
	    		  if ("1".equals(jdbcPar.getStringValue())) {
	    			  JdbcParametro jdbcPar1 = (JdbcParametro) h1.get("SYSUTE");
	    			  String syslogin = jdbcPar1.getStringValue();
	    			  jdbcPar1.set(new JdbcParametro(JdbcParametro.TIPO_TESTO, syslogin.concat(" (utente disabilitato)")));
	    		  }
	    	  }

	    		total = hmUSR.size();
	    		totalAfterFilter = hmUSR.size();
	    	}

			result.put("iTotalRecords", total);
			result.put("iTotalDisplayRecords", totalAfterFilter);
			result.put("data", hmUSR);

	    } catch (SQLException e) {
	      throw new JspException("Errore durante la lettura dei permessi degli utenti", e);
	    }

	    out.println(result);
	    out.flush();
		return null;
	}
}
