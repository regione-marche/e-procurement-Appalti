package it.eldasoft.sil.pg.web.struts.permessi;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import net.sf.json.JSONObject;

public class GetListaPermessiUtentiCommissioneAction extends Action {

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
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();
    int total = 0;
    int totalAfterFilter = 0;

    String operation = request.getParameter("operation");
    String ngara = request.getParameter("ngara");
    String codgar = request.getParameter("codgar");
    //String codein = request.getParameter("codein");

    //String ngaraParameter = "'" + ngara + "'";
    String ngaraParameter = ngara ;

    try {
      List<Object> parameters = new ArrayList<Object>();
      String selectUSR = "select distinct usrsys.syslogin, "
          + " usrsys.sysute, "
          + " usrsys.email, "
          + " usrsys.syscon, "
          + " g_permessi.numper, "
          + " g_permessi.autori, "
          + " g_permessi.propri, "
          + " g_permessi.codgar, "
          + " g_permessi.meruolo as meruolog, "
          + " usrsys.meruolo as meruolou, "
          + " usrsys.sysdisab, "
          + " tecni.codtec, "
          + " tecni.nomtec ";

      String selectUSR_mod = "select distinct usrsys.syslogin, "
          + " usrsys.sysute, "
          + " usrsys.email, "
          + " usrsys.syscon, "
          + " cast (null as numeric),"
          + " cast (null as numeric),"
          + " null, "
          + " null, "
          + " cast (null as numeric) as meruolog, "
          + " usrsys.meruolo as meruolou, "
          + " usrsys.sysdisab, "
          + " tecni.codtec, "
          + " tecni.nomtec ";

      String selectTECNI = "select distinct null, "
        + " null, "
        + " null, "
        + " cast (null as numeric),"
        + " cast (null as numeric),"
        + " cast (null as numeric),"
        + " null, "
        + " null, "
        + " cast (null as numeric) as meruolog, "
        + " cast (null as numeric) as meruolou,"
        + " null, "
        + " tecni.codtec, "
        + " tecni.nomtec ";

      if ("VISUALIZZA".equals(operation)) {
            selectUSR = selectUSR
            + " from usrsys, g_permessi, gfof, tecni "
            + " where g_permessi.syscon = usrsys.syscon "
            + " and g_permessi.codgar = ? "
            + " and gfof.codfof = tecni.codtec"
            + " and upper(tecni.cftec) = upper(usrsys.syscf)"
            + " and (gfof.indisponibilita is null or gfof.indisponibilita != '1')"
            + " and gfof.ngara2 = ? ";
            parameters.add(codgar);
            parameters.add(ngaraParameter);

              selectUSR = selectUSR + " union "
                  + selectUSR_mod
                  + " from usrsys, gfof, tecni "
                  + " where not exists ("
                        + "select * from g_permessi "
                        + " where usrsys.syscon = g_permessi.syscon "
                        + " and g_permessi.codgar = ? "
                  + ") and gfof.codfof = tecni.codtec"
                  + " and upper(tecni.cftec) = upper(usrsys.syscf)"
                  + " and (gfof.indisponibilita is null or gfof.indisponibilita != '1')"
                  + " and gfof.ngara2 = ? "
                  + " and (usrsys.sysdisab is null or usrsys.sysdisab = '0')";
              parameters.add(codgar);
              parameters.add(ngaraParameter);

              selectUSR = selectUSR + " union "
              + selectTECNI
              + " from tecni, gfof "
              + " where gfof.codfof = tecni.codtec"
              + " and not exists (select usrsys.syscf from usrsys where upper(usrsys.syscf) = upper(tecni.cftec)"
              + " and (usrsys.sysdisab is null or usrsys.sysdisab = '0')) "
              + " and not exists (select * from usrsys, g_permessi where upper(usrsys.syscf) = upper(tecni.cftec)"
              + " and g_permessi.syscon = usrsys.syscon "
              + " and g_permessi.codgar = ? ) "
              + " and (gfof.indisponibilita is null or gfof.indisponibilita != '1')"
              + " and gfof.ngara2 = ? ";
              parameters.add(codgar);
              parameters.add(ngaraParameter);

      } else if ("MODIFICA".equals(operation)) {

    	    selectUSR = selectUSR
            + " from usrsys, g_permessi, gfof, tecni "
            + " where g_permessi.syscon = usrsys.syscon "
            + " and g_permessi.codgar = ? "
            + " and gfof.codfof = tecni.codtec"
            + " and upper(tecni.cftec) = upper(usrsys.syscf)"
            + " and (gfof.indisponibilita is null or gfof.indisponibilita != '1')"
            + " and gfof.ngara2 = ? ";
    	    parameters.add(codgar);
    	    parameters.add(ngaraParameter);

              selectUSR = selectUSR + " union "
                  + selectUSR_mod
                  + " from usrsys, gfof, tecni "
                  + " where not exists ("
                        + "select * from g_permessi "
                        + " where usrsys.syscon = g_permessi.syscon "
                        + " and g_permessi.codgar = ? "
                  + ") and gfof.codfof = tecni.codtec"
                  + " and upper(tecni.cftec) = upper(usrsys.syscf)"
                  + " and (gfof.indisponibilita is null or gfof.indisponibilita != '1')"
                  + " and gfof.ngara2 = ? "
                  + " and (usrsys.sysdisab is null or usrsys.sysdisab = '0')";
              parameters.add(codgar);
              parameters.add(ngaraParameter);
              
              selectUSR = selectUSR + " union "
                + selectTECNI
                + " from tecni, gfof "
                + " where gfof.codfof = tecni.codtec"
                + " and not exists (select usrsys.syscf from usrsys where upper(usrsys.syscf) = upper(tecni.cftec)"
                + " and (usrsys.sysdisab is null or usrsys.sysdisab = '0')) "
                + " and not exists (select * from usrsys, g_permessi where upper(usrsys.syscf) = upper(tecni.cftec)"
                + " and g_permessi.syscon = usrsys.syscon "
                + " and g_permessi.codgar = ? ) "
                + " and (gfof.indisponibilita is null or gfof.indisponibilita != '1')"
                + " and gfof.ngara2 = ? ";
              parameters.add(codgar);
              parameters.add(ngaraParameter);

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
