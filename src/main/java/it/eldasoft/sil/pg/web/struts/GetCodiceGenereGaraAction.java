package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetCodiceGenereGaraAction extends Action {

  /**
   * Manager per la gestione dell'integrazione con WSDM.
   */
  private GestioneWSDMManager gestioneWSDMManager;

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
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
      String chiave1 = request.getParameter("chiave1");
      Long genereGara = null;
      String codgar = null;
      HashMap<String, Object> dati = gestioneWSDMManager.getGenereCodiceGara(chiave1);
      if(dati!=null){
        codgar = (String)dati.get("codgar");
        genereGara = (Long)dati.get("genereGara");
        result.put("codiceGara", codgar);
        result.put("genereGara", genereGara);
      }
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura del codice e del genere della gara", e);
    }

    out.println(result);
    out.flush();

    return null;

  }
}
