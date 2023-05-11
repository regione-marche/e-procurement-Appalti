/*
 * Created on 07-06-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Function che verifica se la categoria viene usata in entità di GENE, GARE e PL.
 *
 * @author Marcello Caminiti
 */
public class CheckIsCategoriaAdoperataInPGPLFunction extends AbstractFunzioneTag {

  private final String entita[] = {"CATE","ARCHDOCG","OPES","CATG","ISCRIZCAT","ISCRIZCLASSI","CATAPP","ULTAPP","SUBA","CPRIGHE","CNRIGHE"};

  private final String campiEntita[] = {"CATISC","CATEGORIA","CATOFF","CATIGA","CODCAT","CODCAT","CATIGA","CATOFF","CATLAV","CATIGA","CATEG"};

  public CheckIsCategoriaAdoperataInPGPLFunction() {
    super(3, new Class[] {PageContext.class, String.class,String.class  });
  }

  /**
   * Si controlla se la categoria viene usata
   *
   * @return true se la categoria &agrave; usata , false altrimenti
   */
  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    boolean categoriaUsata = false;

    SqlManager manager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    String key = (String) params[1];
    String controlloFiglie = (String) params[2];
    StringBuffer html= new StringBuffer("La categoria è referenziata nelle seguenti tabelle:<ul>\n");
    try {
      if("Si".equals(controlloFiglie)){
        //Nel caso della cais devo riportare il numero di figlie
        String selectFiglie = "select count(1) from CAIS where codliv1=? or codliv2=? or codliv3=? or codliv4=? ";
        Long figlieCategoria = (Long) manager.getObject(selectFiglie, new String[] {key,key,key,key});
        if(figlieCategoria!=null && figlieCategoria.longValue()>0){
          categoriaUsata=true;
          html.append("<li>");
          html.append("CAIS").append("( ");
          html.append(figlieCategoria.toString());
          html.append(" figlie)");
          html.append("</li>\n");
        }
      }
      for(int i=0;i<entita.length;i++){
        if(manager.isTable(entita[i])){
          String sql = "SELECT COUNT(1) FROM " + entita[i] + " WHERE " + campiEntita[i] + " = ?";
          Long occorrenze = (Long) manager.getObject(sql, new String[] {key});
          if(occorrenze!=null && occorrenze.longValue()> 0){
            categoriaUsata=true;
            html.append("<li>");
            html.append(entita[i]).append("( ");
            html.append(occorrenze.toString());
            html.append(" volte)");
            html.append("</li>\n");
          }
        }

      }

    } catch (SQLException e) {
      throw new JspException("Errore nel conteggio dei riferimenti della categoria " + key,e);
    }

    if(categoriaUsata){
      html.append("</ul>\n");
      pageContext.setAttribute("messaggio", html, PageContext.REQUEST_SCOPE);
    }

    return String.valueOf(categoriaUsata);
  }

}
