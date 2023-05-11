/*
 * Created on 10/05/21
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;


import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

/**
 * Gestore che effettua il controllo della valorizzazione di alcuni
 * campi. Se anche uno non risulta valorizzato, allora si deve riportare
 * un messaggio opportuno alla finestra g1stipula-PubblicaDocumenti.jsp
 *
 * @authorCristian Febas
 */
public class GestoreInitAlboFornitori extends AbstractGestorePreload {

  SqlManager sqlManager = null;

  
  public GestoreInitAlboFornitori(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }

  public void inizializzaManager(PageContext page){
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);

  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {
	  
	  this.inizializzaManager(page);
	  // lettura dei parametri di input
	  String codgar = page.getRequest().getParameter("codgar");
	  String ngara = page.getRequest().getParameter("ngara");
	  Long initTipgen = null;
   
      try {
    	  
    	  Long tipgen = (Long) sqlManager.getObject("select tipgen from torn where codgar = ? ", new Object[]{codgar});
    	  if(tipgen != null && Long.valueOf(3).equals(tipgen)) {
    		  initTipgen = Long.valueOf(2);
    		  page.setAttribute("initTipgen", initTipgen, PageContext.REQUEST_SCOPE);
    	  }else {
    		  page.setAttribute("initTipgen", tipgen, PageContext.REQUEST_SCOPE);
    	  }

          String selectFiltroAlbo="select catiga,descat,tiplavg from catg,cais where catg.catiga = cais.caisim and ngara = ?";
          Vector datiFiltroAlbo = sqlManager.getVector(selectFiltroAlbo, new Object[]{ngara});
          

          if(datiFiltroAlbo!=null){
            String catiga = SqlManager.getValueFromVectorParam(datiFiltroAlbo,0).getStringValue();
            catiga = StringUtils.stripToEmpty(catiga);
            page.setAttribute("initCatiga", catiga, PageContext.REQUEST_SCOPE);
            String descat = SqlManager.getValueFromVectorParam(datiFiltroAlbo,1).getStringValue();
            descat = StringUtils.stripToEmpty(descat);
            page.setAttribute("initDescat", descat, PageContext.REQUEST_SCOPE);
            Long tiplavg = (Long) SqlManager.getValueFromVectorParam(datiFiltroAlbo,2).getValue();
            page.setAttribute("initTiplavg", tiplavg, PageContext.REQUEST_SCOPE);
          }
          
    

      } catch (SQLException e) {
    	  throw new JspException("Errore nella lettura dati per inizializzare la ricerca su albo fornitori", e);
      }
  }
}