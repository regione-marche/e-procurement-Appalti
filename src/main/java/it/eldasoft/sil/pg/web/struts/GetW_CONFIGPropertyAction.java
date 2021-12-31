package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;

import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetW_CONFIGPropertyAction extends Action {

  /**
   * Manager per la gestione delle interrogazioni di database.
   */
  private SqlManager sqlManager;

  /**
   * @param sqlManager
   *        the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    try {
      String codapp = request.getParameter("codapp");
      String chiave = request.getParameter("chiave");
      String criptato = request.getParameter("criptato");
      
      String valore= null;
      if (codapp != null && chiave != null) {
        String selectW_INVCOM = "select valore from w_config where codapp = ? and chiave = ?";
        valore = (String)sqlManager.getObject(selectW_INVCOM, new Object[]{codapp,chiave});
      }
      
      if ("1".equals(criptato) && valore != null) {
        ICriptazioneByte valoreICriptazioneByte = null;
        valoreICriptazioneByte = FactoryCriptazioneByte.getInstance(ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
            valore.getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
        valore = new String(valoreICriptazioneByte.getDatoNonCifrato());
      }
      
      result.put("propertyW_CONFIG", valore);
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura della property in W_CONFIG", e);
    }

    out.println(result);
    out.flush();

    return null;

  }
}
