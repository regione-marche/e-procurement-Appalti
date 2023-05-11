package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.tools.ant.util.DateUtils;
import org.springframework.transaction.TransactionStatus;

public class SetDatiAttoAutorizzativoAction extends Action {

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
  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String ngara = request.getParameter("ngara");
    String codgar = request.getParameter("codgar");
    String genere = request.getParameter("genere");
    
    Date dataProv = null;
    String numprov = null; 
    String descrizione = null;
    String tip = null;
    List datiAtto = null;
    JSONObject jsonObject = new JSONObject();
    
    if("2".equals(genere)){
      datiAtto = this.sqlManager.getListVector("select DATTOG, NATTOG, TATTOG from gare where ngara = ?", new Object[]{ngara});
    }else{
      datiAtto = this.sqlManager.getListVector("select DATTOT, NATTOT, TATTOT from torn where codgar = ?", new Object[]{codgar});
    }
    
    if(datiAtto!=null && datiAtto.size()>0){
      jsonObject.put("esito", "OK");
      dataProv=(Date) SqlManager.getValueFromVectorParam(datiAtto.get(0), 0).getValue();
      numprov=SqlManager.getValueFromVectorParam(datiAtto.get(0), 1).getStringValue();
      tip=SqlManager.getValueFromVectorParam(datiAtto.get(0), 2).getStringValue();
      if(tip != null && !"".equals(tip)){
        descrizione = (String) sqlManager.getObject("select tab1desc from tab1 where  tab1cod = ? AND tab1tip = ?", new Object[] {"A2045",tip});}
      if(dataProv!=null){
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        jsonObject.put("dataprov", df.format(dataProv));}
      if(!"".equals(numprov) && numprov != null){
        jsonObject.put("numprov", numprov);}
      if(!"".equals(descrizione) && descrizione!=null){
        jsonObject.put("descrizione", descrizione);}
      
      if(dataProv == null && ("".equals(numprov) || numprov == null)){
        jsonObject.put("esito", "KO");
        jsonObject.put("errore", "Nessun atto autorizzativo specificato per la gara.");
      }
      /*
      sqlManager.update("update DOCUMGARA set DATAPROV = ?, NUMPROV = ?, DESCRIZIONE = ? where codgar=? and gruppo = 15 and norddocg = ?",
          new Object[] { dataProv, numprov, descrizione, codgar, norddocg });
      this.sqlManager.commitTransaction(status);
      */
    }

    out.println(jsonObject);

    out.flush();
    return null;
  }

}
