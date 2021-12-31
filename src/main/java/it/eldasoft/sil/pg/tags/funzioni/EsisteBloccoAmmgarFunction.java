/*
 * Created on 27-06-2012
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
 * Funzione adoperata nelle fasi di gara per Offerta Unica(gare.ngara=gare.codgar1)
 * per controllare se una ditta non è ammessa alla gara.
 *
 * @author Marcello Caminiti
 */
public class EsisteBloccoAmmgarFunction extends AbstractFunzioneTag {

  public EsisteBloccoAmmgarFunction() {
    super(4 , new Class[] { PageContext.class, String.class,String.class,String.class});
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String ngara = (String) params[1];
    String dittao = (String) params[2];
    String step = (String) params[3];


    String select="select ammgar from v_ditgammis where codgar=? and ngara=? and dittao=? and fasgar=?";
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      Boolean bloccoAmmgar= new Boolean(false);
      long stepAttivo=0;
      if(step!=null && !"".equals(step))
        stepAttivo = new Long(step).longValue()/10;
      Long ammgar = (Long)sqlManager.getObject(select,new Object[]{ngara,ngara,dittao,new Long(stepAttivo)});
      if(ammgar!= null && (ammgar.longValue() == 2 || ammgar.longValue() == 6))
        bloccoAmmgar = new  Boolean(true);
      pageContext.setAttribute("bloccoAmmgar", bloccoAmmgar,
          PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      throw new JspException("Errore nella lettura di V_DITGAMMIS.AMMGAR)",e);
    }
    return null;
  }

}
