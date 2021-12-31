/*
 * Created on 11/06/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per popolare la dropdown per selezionare una categoria padre di una categoria foglia
 *
 * @author Marcello Caminiti
 */
public class GetDatoCODLIVAction extends Action {

  Logger             logger = Logger.getLogger(GetDatoCODLIVAction.class);

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

    String codice = StringUtils.stripToNull(request.getParameter("codice"));
    String codliv1 = request.getParameter("codliv1");
    String codliv2 = request.getParameter("codliv2");
    String codliv3 = request.getParameter("codliv3");
    String livello = request.getParameter("livello");
    Long tipo = new Long(request.getParameter("tipo"));
    String isarchi = StringUtils.stripToNull(request.getParameter("isarchi"));
    //String select="select caisim, descat from cais where tiplavg=?";
    String select="select caisim, descat";
    if("1".equals(livello))
        select += ",titcat";
    select +=" from cais where tiplavg=?";
    if(codice!=null)
      select+= " and caisim<>'" + codice +  "'";

    if("1".equals(livello)){
      select+="  and codliv1 is null ";
    }else if("2".equals(livello)){
      select+=" and codliv1='" + codliv1 + "' and codliv2 is null ";
    }else if("3".equals(livello)){
      select+=" and codliv1='" + codliv1 + "' and codliv2='" + codliv2 + "' and codliv3 is null ";
    }else {
      select+=" and codliv1='" + codliv1 + "' and codliv2='" + codliv2 + "' and codliv3='" + codliv3 + "' and codliv4 is null ";
    }
    if(isarchi==null || !"1".equals(isarchi))
      select+= " and (isarchi is null or isarchi<>'1') ";

    select+= " order by caisim";
    try{
      @SuppressWarnings("unchecked")
      List<Vector<JdbcParametro>> listadatiCAIS = sqlManager.getListVector(select.toString(), new Object[] {tipo});
      List<Map<String, Object>> risultato = new ArrayList<Map<String, Object>>();

      if (listadatiCAIS != null && listadatiCAIS.size() > 0) {
        for (Vector<JdbcParametro> riga : listadatiCAIS) {
          String caisim = SqlManager.getValueFromVectorParam(riga, 0).stringValue();
          String desc = SqlManager.getValueFromVectorParam(riga, 1).stringValue();
          Map<String, Object> mappaRiga = new HashMap<String, Object>();
          mappaRiga.put("caisim", caisim);
          mappaRiga.put("desc", desc);
          if("1".equals(livello)){
            String titcat = SqlManager.getValueFromVectorParam(riga, 2).stringValue();
            mappaRiga.put("titcat", titcat);
          }
          risultato.add(mappaRiga);
        }

      }
      // si popola il risultato in formato JSON
      JSONArray jsonArray = JSONArray.fromObject(risultato.toArray());
      out.println(jsonArray);
      out.flush();
     } catch (GestoreException e) {
       logger.error("Errore inaspettato durante la lettura dei dati della CAIS", e);
       throw new RuntimeException("Errore inaspettato durante la lettura dei dati della CAIS", e);
     } catch (SQLException e) {
       logger.error("Errore durante l'estrazione dei dati per popolare i campi CODLIV di CAIS", e);
       throw new RuntimeException("Errore durante l'estrazione dei dati per popolare i campi CODLIV di CAIS", e);
     }
    return null;
  }

}
