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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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


public class GetBusteDocumentiConcorrentiJsonFunction extends AbstractFunzioneTag {

  public GetBusteDocumentiConcorrentiJsonFunction() {
    super(3, new Class[] { PageContext.class,String.class,String.class });
    // TODO Auto-generated constructor stub
  }

  static Integer     PREQUALIFICA = 1;
  static Integer     AMMINISTRATIVA = 2;
  static Integer     TECNICA = 3;
  static Integer     ECONOMICA = 4;
  
  static String      TITOLOAMMINISTRATIVA = "Busta documentazione amministrativa";
  static String      TITOLOTECNICA = "Busta offerta tecnica";
  static String      TITOLOECONOMICA = "Busta offerta economica";
  static String      TITOLOPREQUALIFICA = "Busta prequalifica";
  
  private String sqlCount = "select count(*) from DOCUMGARA D where D.CODGAR=? and D.BUSTA=?";
  private String sqlCountPubb = "select count(*) from DOCUMGARA D where D.CODGAR=? and D.BUSTA=? and D.statodoc = 5 and D.ISARCHI is null";
  private String sqlCountAttesaPubb = "select count(*) from DOCUMGARA D where D.CODGAR=? and D.BUSTA=? and D.statodoc is null and D.ISARCHI is null";
  private String sqlCountArchi = "select count(*) from DOCUMGARA D where D.CODGAR=? and D.BUSTA=? and D.ISARCHI is NOT null";
  
  SqlManager sqlManager;
  
  @Override 
  public String function(PageContext pageContext, Object[] params) 
  throws JspException {
    
    String codiceGara = (String) params[1];
    String ngara = (String) params[2];
    
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);
    JSONArray json =  new JSONArray();
    Long nrPubblicazioni;
    String nomeBusta = "";
    Integer busta = 0;
    boolean bustaAbilitata = false;
    HashMap<Integer,String> clausoleList = new HashMap<Integer,String>();
    clausoleList.put(AMMINISTRATIVA, "");
    clausoleList.put(TECNICA, "SELECT COUNT(*) FROM GARE,GARE1 WHERE GARE1.NGARA=GARE.NGARA AND (GARE.MODLICG = 6 OR GARE1.VALTEC = 1) AND GARE.CODGAR1 = ?");
    clausoleList.put(ECONOMICA, "SELECT COUNT(*) FROM GARE, GARE1 WHERE GARE1.NGARA=GARE.NGARA AND (GARE.MODLICG != 6 OR  (GARE.MODLICG = 6 AND (COSTOFISSO IS NULL OR GARE1.COSTOFISSO != 1))) AND GARE1.CODGAR1 = ?");
    clausoleList.put(PREQUALIFICA, "SELECT COUNT(*) FROM TORN WHERE (ITERGA = 2 OR ITERGA = 4) AND CODGAR = ?");
    
    int indice = 0;
    try { //scrivere query con condizioni di visualizzazione per ogni busta
          for (int i = 1; i < 5; i++) {
            bustaAbilitata = false;
            nrPubblicazioni = new Long(0);
            
            if(i == PREQUALIFICA){
              nomeBusta = TITOLOPREQUALIFICA;
              busta = 4;
              nrPubblicazioni = (Long)sqlManager.getObject(clausoleList.get(i), new Object[] {codiceGara});
              if(nrPubblicazioni.intValue() > 0){
                bustaAbilitata = true;
                indice++;
              }
            }
            if(i == AMMINISTRATIVA){
              nomeBusta = TITOLOAMMINISTRATIVA;
              busta = 1;
              bustaAbilitata = true;
              indice++;
            }
            if(i == TECNICA){
              nomeBusta = TITOLOTECNICA;
              busta = 2;
              nrPubblicazioni = (Long)sqlManager.getObject(clausoleList.get(i), new Object[] {codiceGara});
              if(nrPubblicazioni.intValue() > 0){
                bustaAbilitata = true;
                indice++;
              }
            }
            if(i == ECONOMICA){
              nomeBusta = TITOLOECONOMICA;
              busta = 3;
              nrPubblicazioni = (Long)sqlManager.getObject(clausoleList.get(i), new Object[] {codiceGara});
              if(nrPubblicazioni.intValue() > 0){
                bustaAbilitata = true;
                indice++;
              }
            }
            
            if(bustaAbilitata){
              JSONObject jsonItem = this.composeJson(codiceGara, ngara,indice,busta,nomeBusta,pageContext);
              //Aggiungo tipologia documento 
              json.add(jsonItem);
            }
           }
      
    } catch (Exception ex) {
        ;
    }
  
  String result = json.toString();
  result = result.replaceAll("\"CLASS\"", "\"class\"");
  return result;

  }
  
  private JSONObject composeJson(String codiceGara, String ngara, int indice,Integer busta,String nome,PageContext pageContext) throws SQLException{
    JSONObject jsonItem =  new JSONObject();
    JSONObject jsonItemLi =  new JSONObject();
    Long nrPubblicazioniTotali = new Long(0);
    jsonItem.put("id", busta.toString());
    jsonItem.put("parent", "#");
    nrPubblicazioniTotali = (Long)sqlManager.getObject(sqlCount, new Object[] {codiceGara, busta});
    Long numeroPubb = null;
    Long numeroAttesaPubb = null;
    Long numeroArchiviati = null;
    String contextPath = (String) ((HttpServletRequest) pageContext.getRequest()).getContextPath();
    
    if (nrPubblicazioniTotali > 0) {
        numeroPubb = (Long)sqlManager.getObject(sqlCountPubb, new Object[] {codiceGara, busta});
        numeroAttesaPubb = (Long)sqlManager.getObject(sqlCountAttesaPubb, new Object[] {codiceGara, busta});
        String riepilogoPubb = "Da pubblicare: " + numeroAttesaPubb + ", pubblicati: " + numeroPubb; 
        numeroArchiviati = (Long)sqlManager.getObject(sqlCountArchi, new Object[] {codiceGara, busta});
        if(!new Long(0).equals(numeroArchiviati)){
          riepilogoPubb = riepilogoPubb + ", archiviati: "+ numeroArchiviati;
        }
        String icon = "";
        if(numeroPubb > 0){icon = "<img src=\""+ contextPath +"/img/documento_pubblicato.png\" title=\""+riepilogoPubb+"\" style=\" float:right;\" width=\"16\" height=\"16\" />";}
        if(ngara == null){
          jsonItem.put("text", "<b>" + nome + "</b>" + icon +  "<br><i>" + riepilogoPubb + "</i><span style=\"display:none;\">" + codiceGara + "</span>");
        }else{
          jsonItem.put("text", "<b>" + nome + "</b>" + icon +  "<br><i>" + riepilogoPubb + "</i><span style=\"display:none;\">" + ngara + "</span>");
        }
    } else {
        if(ngara == null){
          jsonItem.put("text", nome + "<span style=\"display:none;\">" + codiceGara + "</span>");
        }else{
          jsonItem.put("text", nome + "<span style=\"display:none;\">" + ngara + "</span>");
        }
    }
    JSONObject jsonItemA =  new JSONObject();
    if(ngara == null){
      jsonItemA.put("href", "javascript:visualizzaDocumentiConcorrenti('" + codiceGara + "'," + busta +",'"+ nome + "')");
    }else{
      jsonItemA.put("href", "javascript:visualizzaDocumentiConcorrenti('" + ngara + "'," + busta +",'"+ nome + "')");
    }
    jsonItem.put("a_attr", jsonItemA);
    
    //
    if (indice % 2 == 0) {
        jsonItemLi.put("CLASS", "even");
    } else {
        jsonItemLi.put("CLASS", "odd");
    }
    jsonItem.put("li_attr", jsonItemLi);
    return jsonItem;
  }
  
}
