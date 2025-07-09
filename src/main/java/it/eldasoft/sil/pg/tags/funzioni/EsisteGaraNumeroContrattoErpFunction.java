/*
 * Created on 26-07-2012
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
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che controlla se alla gara è stata associata il numero contratto Erp
 *
 * @author 
 */
public class EsisteGaraNumeroContrattoErpFunction extends AbstractFunzioneTag {

  public EsisteGaraNumeroContrattoErpFunction() {
    super(3 , new Class[] { PageContext.class, String.class, String.class});
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
	  
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);	  

    String esisteNumeroContrattoErp= "false";
    String ngara  = (String) params[1];
    ngara+="%";
    String codiga = (String) params[2]; // e' il numero lotto

    String select="select nrepat, daatto from gare where ngara like ? ";
    Object[] selectParam; 
    if ( codiga==null) {
    	select += " and codiga is null";
    	selectParam = new Object[]{ngara};
    } else {
        select += " and codiga=?";
        selectParam = new Object[]{ngara, codiga};
    }

    try {
    	
      List<?> datiContrattoErp = sqlManager.getListVector(select,selectParam);

      if (datiContrattoErp.size()>0) {
	      String nrepat = (String) sqlManager.getValueFromVectorParam(datiContrattoErp.get(0), 0).getValue();
	      Date daatto = (Date) sqlManager.getValueFromVectorParam(datiContrattoErp.get(0), 1).dataValue();
	      
	      String daattoStr = "";
	      if (daatto!=null) {
	    	  daattoStr = UtilityDate.convertiData(daatto, UtilityDate.FORMATO_GG_MM_AAAA);
	      }
	      
	      pageContext.setAttribute("numeroContrattoErp",nrepat,PageContext.REQUEST_SCOPE);
	      pageContext.setAttribute("dataContrattoErp",daattoStr,PageContext.REQUEST_SCOPE);
	      
	      esisteNumeroContrattoErp = "true";
      }
    	  
    } catch (Exception e) {
      throw new JspException("Errore nella lettura del campo GARE.nrepat o GARE.daatto)",e);
    }
    return esisteNumeroContrattoErp;
  }

}
