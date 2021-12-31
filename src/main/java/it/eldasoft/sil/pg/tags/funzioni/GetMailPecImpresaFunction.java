/*
 * Created on 11-12-2013
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
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione adoperata per recuperare mail /PEC
 * da utilizzzare per comunicare l'abilitazione agli operatori economici
 * Return:
 * 0 = niente 1=email 2 = PEC 3= entrambe
 *
 * @author Cristian Febas
 */
public class GetMailPecImpresaFunction extends AbstractFunzioneTag {

  public GetMailPecImpresaFunction() {
    super(2, new Class[] {PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String dittao = (String) params[1];
    dittao = UtilityStringhe.convertiNullInStringaVuota(dittao);
    String codimp = "";
    String coddic = "";
    String resMailPec = "0";
    Vector valoriMailPec = new Vector(2);

    if ("".equals(dittao)) {
      return resMailPec;
    }

    String selectTipologiaImpr = "select tipimp from impr where codimp = ? ";
    String selectRaggruppamento = "select coddic from ragimp where codime9 = ? and impman ='1' ";
    String selectMailPec = "select emaiip,emai2ip from impr where codimp = ? ";
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    try {
      Long tipologiaImpr = (Long) sqlManager.getObject(selectTipologiaImpr, new Object[] {dittao });
      if (tipologiaImpr == null || new Long(1).equals(tipologiaImpr)) {
        codimp = dittao;
      } else {
        codimp = dittao;
        if (new Long(3).equals(tipologiaImpr) || new Long(10).equals(tipologiaImpr)) {
          coddic = (String) sqlManager.getObject(selectRaggruppamento, new Object[] {dittao });
          coddic = UtilityStringhe.convertiNullInStringaVuota(coddic);
          if(!"".equals(coddic)){
            codimp = coddic;
          }else{
            resMailPec = "0";
            pageContext.setAttribute("email", null, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute("emailPec", null, PageContext.REQUEST_SCOPE);
            return resMailPec;
          }
        }
      }
      valoriMailPec = sqlManager.getVector(selectMailPec, new Object[] {codimp });
      String email = sqlManager.getValueFromVectorParam(valoriMailPec, 0).getStringValue();
      String emailPec = sqlManager.getValueFromVectorParam(valoriMailPec, 1).getStringValue();
      email = UtilityStringhe.convertiNullInStringaVuota(email);
      emailPec = UtilityStringhe.convertiNullInStringaVuota(emailPec);
      if ("".equals(email) && "".equals(emailPec)) {
        resMailPec = "0";
      }
      if (!"".equals(email) && "".equals(emailPec)) {
        resMailPec = "1";
      }
      if ("".equals(email) && !"".equals(emailPec)) {
        resMailPec = "2";
      }
      if (!"".equals(email) && !"".equals(emailPec)) {
        resMailPec = "3";
      }

      pageContext.setAttribute("email", email, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("emailPec", emailPec, PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      throw new JspException("Errore nella lettura di mail e pec)", e);
    }

    return resMailPec;
  }

}
