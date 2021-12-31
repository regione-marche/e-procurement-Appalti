/*
 * Created on 16/12/11
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
 * Controlla se è valorizzata la tabella W_CONFCOM
 *
 * @author Marcello Caminiti
 */
public class IsW_CONFCOMPopolataFunction extends AbstractFunzioneTag {

  public IsW_CONFCOMPopolataFunction(){
    super(2, new Class[]{PageContext.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String tipo = (String) params[1];
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ret = "false";

    try {
      String select="select count(numpro) from w_confcom where genere = 1 or genere = ?";
      if("56".equals(tipo)){
        select="select numpro from w_confcom where genere = ?";
        Long numpro = (Long)sqlManager.getObject(select, new Object[]{new Long(tipo)});
        if(numpro!=null){
          ret = "true";
          pageContext.setAttribute("numeroModello", numpro, PageContext.REQUEST_SCOPE);
        }
      }else{
        if("10".equals(tipo) || "20".equals(tipo) || "4".equals(tipo) || "5".equals(tipo) || "6".equals(tipo) ||"7".equals(tipo) || "11".equals(tipo))
          select="select count(numpro) from w_confcom where genere = ?";
        Long numOccorrenze = (Long)sqlManager.getObject(select, new Object[]{new Long(tipo)});
        if(numOccorrenze!=null && numOccorrenze.longValue()>0)
          ret = "true";
        }
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura dei dati della tabella W_CONFCOM",e);
    }

    return ret;
  }

}