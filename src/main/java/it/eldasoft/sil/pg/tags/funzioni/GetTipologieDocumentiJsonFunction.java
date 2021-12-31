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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;


public class GetTipologieDocumentiJsonFunction extends AbstractFunzioneTag {

  public GetTipologieDocumentiJsonFunction() {
    super(3, new Class[] { PageContext.class,String.class,String.class });
    // TODO Auto-generated constructor stub
  }
  
  private String bando = "Bando o avviso";
  private String esito = "Esito";
  private String invito = "Invito";
  private String atto = "Atto";
  private String trasparenza = "Trasparenza";
  private String attoContrarre = "Atto a contrarre";
  
  private String sqlw9cfPubb = "select TORN.CODGAR,tipgar,tipgarg from TORN left outer join GARE on TORN.CODGAR=GARE.CODGAR1 where TORN.CODGAR=?";
  private String sqlCount = "select count(*) from DOCUMGARA D where D.CODGAR=? and D.TIPOLOGIA=?";
  private String sqlCountPubb = "select count(*) from DOCUMGARA D where D.CODGAR=? and D.TIPOLOGIA=? and D.statodoc = 5 and D.ISARCHI is null";
  private String sqlCountAttesaPubb = "select count(*) from DOCUMGARA D where D.CODGAR=? and D.TIPOLOGIA=? and D.statodoc is null and D.ISARCHI is null";
  private String sqlCountAttesaFirma = "select count(*) from DOCUMGARA D, W_DOCDIG W where D.CODGAR=? and D.TIPOLOGIA=? and W.IDDOCDIG=D.IDDOCDG and W.IDPRG = D.IDPRG and W.DIGFIRMA = '1' and D.ISARCHI is null";
  private String sqlCountArchi = "select count(*) from DOCUMGARA D where D.CODGAR=? and D.TIPOLOGIA=? and D.ISARCHI is NOT null";
  
  @Override 
  public String function(PageContext pageContext, Object[] params) 
  throws JspException {
    
    String codiceGara = (String) params[1];
    String ngara = (String) params[2];
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);
    JSONArray json =  new JSONArray();
    Long tipoPubblicazione, nrPubblicazioniTotali,gruppo;
    String nome, clausolaWhereVis,clausolaWhereUlt;
    int indice = 0;
    String contextPath = (String) ((HttpServletRequest) pageContext.getRequest()).getContextPath();
    
    try { 
        List<?> w9cfPubb = sqlManager.getListVector("select ID, NOME, CL_WHERE_VIS, CL_WHERE_ULT, GRUPPO from G1CF_PUBB order by NUMORD", new Object[] {});
        if (w9cfPubb != null && w9cfPubb.size() > 0) {
          for (int i = 0; i < w9cfPubb.size(); i++) {
              tipoPubblicazione = SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 0).longValue();
              nome = (String) SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 1).getValue();
              if(nome != null && nome.length() > 140){nome = nome.substring(0,140) + "...";}
              clausolaWhereVis = (String) SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 2).getValue();
              clausolaWhereUlt = (String) SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 3).getValue();
              gruppo = SqlManager.getValueFromVectorParam(w9cfPubb.get(i), 4).longValue();
              if(!new Long(5).equals(gruppo) || GeneManager.checkOP(pageContext.getServletContext(), "OP129")){
              
                if (clausolaWhereVis != null && !clausolaWhereVis.equals("")) {
                    clausolaWhereVis = " and (" + clausolaWhereVis + ")";
                } 
                if (clausolaWhereUlt != null && !clausolaWhereUlt.equals("")) {
                  clausolaWhereVis = clausolaWhereVis + " and (" + clausolaWhereUlt + ")";
              } 
                List<?> w9cfPubbVisible = null;
                w9cfPubbVisible = sqlManager.getListVector(sqlw9cfPubb + clausolaWhereVis, new Object[] {codiceGara});
                
                if (w9cfPubbVisible!= null && w9cfPubbVisible.size() > 0) {
                    indice++;
                    JSONObject jsonItem =  new JSONObject();
                    JSONObject jsonItemLi =  new JSONObject();
                    jsonItem.put("id", tipoPubblicazione.toString());
                    jsonItem.put("parent", "#");
                    nrPubblicazioniTotali = (Long)sqlManager.getObject(sqlCount, new Object[] {codiceGara, tipoPubblicazione});
                    Long numeroPubb = null;
                    Long numeroAttesaPubb = null;
                    Long numeroAttesaFirma = null;
                    Long numeroArchiviati = null;
                    if (nrPubblicazioniTotali > 0) {
                        numeroPubb = (Long)sqlManager.getObject(sqlCountPubb, new Object[] {codiceGara, tipoPubblicazione});
                        numeroAttesaPubb = (Long)sqlManager.getObject(sqlCountAttesaPubb, new Object[] {codiceGara, tipoPubblicazione});
                        String richiestaFirma = ConfigManager.getValore(CostantiAppalti.PROP_RICHIESTA_FIRMA);
                        String riepilogoPubb = "";
                        String gruppoDescr = "(" + gruppoToNome(gruppo) + ") ";
                        String iconaGruppiPreferiti = "";
                        if(gruppo.intValue() == 15 || gruppo.intValue() == 1 || gruppo.intValue() == 6 || gruppo.intValue() == 4 ){
                          iconaGruppiPreferiti = "<img title='"+gruppoToNome(gruppo)+"' style='float:left;padding:2px' src='/Appalti/img/documentiPreferiti.png' width='12' height='12'  >";
                        }
                        riepilogoPubb += "Da pubblicare: " + numeroAttesaPubb; 
                        numeroAttesaFirma = (Long)sqlManager.getObject(sqlCountAttesaFirma, new Object[] {codiceGara, tipoPubblicazione});
                        if("1".equals(richiestaFirma) && numeroAttesaFirma.intValue() > 0){
                          riepilogoPubb = riepilogoPubb + ", in attesa di firma: "+ numeroAttesaFirma;
                        }
                        riepilogoPubb = riepilogoPubb + ", pubblicati: " + numeroPubb;
                        numeroArchiviati = (Long)sqlManager.getObject(sqlCountArchi, new Object[] {codiceGara, tipoPubblicazione});
                        if(numeroArchiviati != null && numeroArchiviati.intValue() != 0){
                          riepilogoPubb = riepilogoPubb + ", archiviati: "+ numeroArchiviati;
                        }
                        String icon = "";
                        if(numeroPubb > 0){icon = "<img src=\"" + contextPath + "/img/documento_pubblicato.png\" title=\""+gruppoDescr + riepilogoPubb+"\" style=\" float:right;\" width=\"16\" height=\"16\" />";}
                        if(ngara == null){
                          jsonItem.put("text", "<b>" + nome + "</b>" + icon +  "<br><i>" + gruppoDescr + iconaGruppiPreferiti + riepilogoPubb + "</i><span style=\"display:none;\">" + codiceGara + "</span>");
                        }else{
                          jsonItem.put("text", "<b>" + nome + "</b>" + icon +  "<br><i>" + gruppoDescr + iconaGruppiPreferiti + riepilogoPubb + "</i><span style=\"display:none;\">" + ngara + "</span>");
                        }
                    } else {
                      String gruppoDescr = "";
                        if(gruppo.intValue() == 15 || gruppo.intValue() == 1 || gruppo.intValue() == 6 || gruppo.intValue() == 4 ){
                          gruppoDescr = "<br><i>(" + gruppoToNome(gruppo) + ")</i>";
                          gruppoDescr += "<img title='"+gruppoToNome(gruppo)+"' style='float:left;padding:2px' src='/Appalti/img/documentiPreferiti.png' width='12' height='12'  >";
                        }
                        if(ngara == null){
                          jsonItem.put("text", nome + gruppoDescr + "<span style=\"display:none;\">" + codiceGara + "</span>");
                        }else{
                          jsonItem.put("text", nome + gruppoDescr + "<span style=\"display:none;\">" + ngara + "</span>");
                        }
                    }
                    JSONObject jsonItemA =  new JSONObject();
                    if(ngara == null){
                      jsonItemA.put("href", "javascript:visualizzaDocumenti('" + codiceGara + "'," + tipoPubblicazione + "," + gruppo + ");");
                    }else{
                      jsonItemA.put("href", "javascript:visualizzaDocumenti('" + ngara + "'," + tipoPubblicazione + "," + gruppo + ");");
                    }
                    jsonItem.put("a_attr", jsonItemA);
                    
                    //
                    if (indice % 2 == 0) {
                        jsonItemLi.put("CLASS", "even");
                    } else {
                        jsonItemLi.put("CLASS", "odd");
                    }
                    jsonItem.put("li_attr", jsonItemLi);
                    //Aggiungo tipologia documento 
                    json.add(jsonItem);
                    
                }
              }
           }
        }
      
    } catch (Exception ex) {
      throw new JspException("Errore nel caricamento della lista: " + ex.getMessage());
    }
  
  String result = json.toString();
  result = result.replaceAll("\"CLASS\"", "\"class\"");
  return result;

  }
  
  private String gruppoToNome(Long gruppo){
    String nome;
    switch(gruppo.intValue()) {
    case 1:
      nome = bando;
    break;
    case 4:
      nome = esito;
    break;
    case 5:
      nome = trasparenza;
    break;
    case 6:
      nome = invito;
    break;
    case 10:
      nome = atto;
    break;
    case 15:
      nome = attoContrarre;
    break;
    default:
      nome = atto;
    }
    return nome;
  }
  
}
