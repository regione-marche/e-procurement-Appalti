/*
 * Created on 19/ott/2018
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;


public class GetJsonTipologieDaInviareSCPFunction extends AbstractFunzioneTag {

  public GetJsonTipologieDaInviareSCPFunction() {
    super(3, new Class[] { PageContext.class,String.class,String.class });
    // TODO Auto-generated constructor stub
  }
  
  private boolean lottiDaPubblicare = false; 
  
  @Override 
  public String function(PageContext pageContext, Object[] params) 
  throws JspException {
    
    String codiceGara = (String) params[1];
    String genereGara = (String) params[2];
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);
    JSONArray json =  new JSONArray();
    Long tipologia, nrPubblicazioni,countVisibile;
    String nome,clausolaWhereVis,clausolaWhereUlt;
    String contextPath = (String) ((HttpServletRequest) pageContext.getRequest()).getContextPath();
    try { 
        List<?> w9cfPubb = sqlManager.getListVector("select ID, NOME, CL_WHERE_VIS, CL_WHERE_ULT from G1CF_PUBB where INVIOSCP = '1' order by NUMORD", new Object[] {});
        if (w9cfPubb != null && w9cfPubb.size() > 0) {
          int colorIndex = 0;
          for (int i = 0; i < w9cfPubb.size(); i++) {
            tipologia = SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 0).longValue();
            nome = (String) SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 1).getValue();
            lottiDaPubblicare = false;
            clausolaWhereVis = (String) SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 2).getValue();
            clausolaWhereUlt = (String) SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 3).getValue();
            if (clausolaWhereVis != null && !clausolaWhereVis.equals("")) {
              clausolaWhereVis = " and (" + clausolaWhereVis + ")";
            }
            if (clausolaWhereUlt != null && !clausolaWhereUlt.equals("")) {
              clausolaWhereVis = clausolaWhereVis + " and (" + clausolaWhereUlt + ")";
            }
            if(nome != null && nome.length() > 140){nome = nome.substring(0,140) + "...";}
            String selectVisibile = "select count(*) from TORN left outer join GARE on TORN.CODGAR=GARE.CODGAR1 where TORN.CODGAR=? ";
            countVisibile = (Long) sqlManager.getObject(selectVisibile + clausolaWhereVis, new Object[] {codiceGara});
            if(countVisibile.intValue()>0){
              colorIndex++;
              JSONObject jsonItem =  new JSONObject();
              JSONObject jsonItemLi =  new JSONObject();
              JSONObject jsonItemA =  new JSONObject();
              jsonItem.put("id", tipologia.toString());
              jsonItem.put("parent", "#");
              nrPubblicazioni = (Long)sqlManager.getObject("select count(*) from documgara where tipologia = ? and codgar = ? and statodoc = 5 and (isarchi is null or isarchi != '1')", new Object[] {tipologia, codiceGara});
  
              if (nrPubblicazioni == 0) {
                jsonItem.put("text", nome );
              }else {
                String icon = "";
                icon = "<img src=\""+ contextPath +"/img/documento_pubblicato_scp.png\" title=\"Pubblicato\" style=\" float:right;\" width=\"16\" height=\"16\" />";
                if((genereGara != null && ("1".equals(genereGara) || "3".equals(genereGara))) && (tipologia.intValue() == 17 || tipologia.intValue() == 19 || tipologia.intValue() == 20)){
                  
                  List<?> lotti = sqlManager.getListVector("select ngara, not_gar, ditta, esineg from GARE where codgar1 = ? and ngara != codgar1 and (ditta is not null or esineg is not null)", new Object[] {codiceGara});
                  boolean lottiPresenti = false;
                  for (int n = 0; n < lotti.size(); n++) {
                    String ngara = SqlManager.getValueFromVectorParam(lotti.get(n), 0).stringValue();
                    String not_gar = SqlManager.getValueFromVectorParam(lotti.get(n), 1).stringValue();
                    String ditta = SqlManager.getValueFromVectorParam(lotti.get(n), 2).stringValue();
                    Long esineg = (Long) SqlManager.getValueFromVectorParam(lotti.get(n), 3).getValue();
                    if((tipologia.intValue() == 17 && esineg != null) || 
                        ((tipologia.intValue() == 19 || tipologia.intValue() == 20) && ditta != null && !"".equals(ditta))){
                    JSONObject child =  createChild(sqlManager,tipologia.toString(),not_gar,ngara,icon);
                    json.add(child);
                    lottiPresenti = true;
                    }
                  }
                  if(lottiPresenti){
                    if(lottiDaPubblicare){
                      jsonItem.put("text",  nome + "<br><b>Atto da inviare</b>" );
                    }else{
                      jsonItem.put("text",  nome + "<br><b>Atti inviati</b>" );
                    }
                  }else{
                    jsonItem.put("text",  nome + "<br><b>Atto da inviare</b>" );
                  }
                }else{
                  String select = "select datpub from garattiscp where tipologia = ? and codgar = ?";
                  Date datpub  = (Date) sqlManager.getObject(select, new Object[] {tipologia,codiceGara});
                  if(datpub != null){
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
                    String tempDate = formatter.format(datpub);  
                    jsonItem.put("text", nome + icon +  "<br><b>Atto inviato in data: " + tempDate.toString() + "</b>");
                  }else{
                   jsonItem.put("text",  nome + "<br><b>Atto da inviare</b>");
                  }
                }
              }
             
              if (colorIndex % 2 == 0) {
                  jsonItemLi.put("CLASS", "even");
              } else {
                  jsonItemLi.put("CLASS", "odd");
              }
              jsonItem.put("li_attr", jsonItemLi);
              
              jsonItemA.put("href", "javascript:doNothing();");
              jsonItem.put("a_attr", jsonItemA);
              
              //Aggiungo tipologia documento 
              json.add(jsonItem);
            }
          }
       }
      
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  
  String result = json.toString();
  result = result.replaceAll("\"CLASS\"", "\"class\"");
  return result;

  }
  
  private JSONObject createChild(SqlManager sqlManager, String parent, String nomeLotto, String ngara, String icon) throws SQLException{
    
    JSONObject jsonItem =  new JSONObject();
    jsonItem.put("parent", parent);
    jsonItem.put("id", ngara+parent);

    String select = "select datpub from garattiscp where tipologia = ? and ngara = ?";
    Date datpub  = (Date) sqlManager.getObject(select, new Object[] {parent,ngara});
    if(datpub != null){
      SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
      String tempDate = formatter.format(datpub);  
      jsonItem.put("text",  nomeLotto  + icon+ "<br><b>Atto inviato in data: " + tempDate + "</b>");
    }else{
     jsonItem.put("text",  nomeLotto + "<br><b>Atto da inviare</b>");
     lottiDaPubblicare = true;
    }
    JSONObject jsonItemLi =  new JSONObject();
    jsonItemLi.put("CLASS", "child");
    jsonItem.put("li_attr", jsonItemLi);
    JSONObject jsonItemA =  new JSONObject();
    jsonItemA.put("href", "javascript:doNothing();");
    jsonItem.put("a_attr", jsonItemA);
    return jsonItem;
  }
  
}
