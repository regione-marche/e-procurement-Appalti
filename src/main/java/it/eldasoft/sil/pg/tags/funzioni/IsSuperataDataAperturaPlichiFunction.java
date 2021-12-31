/*
 * Created on 12/06/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class IsSuperataDataAperturaPlichiFunction extends AbstractFunzioneTag {

  public IsSuperataDataAperturaPlichiFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];

    /*
    String isSuperataDataAperturaPlichi = "false";

    if (ngara != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try {
        String selectTORN = "select torn.desoff, torn.oesoff from torn, gare where torn.codgar = gare.codgar1 and gare.ngara = ?";

        List<?> datiTORN = sqlManager.getVector(selectTORN, new Object[] { ngara });
        if (datiTORN != null && datiTORN.size() > 0) {
          Date desoff = (Date) SqlManager.getValueFromVectorParam(datiTORN, 0).getValue();
          String oesoff = (String) SqlManager.getValueFromVectorParam(datiTORN, 1).getValue();
          if (oesoff == null) {
        	  oesoff = "00:00";
          }

          if (desoff != null) {
            Date dataOdierna = new Date();

            String hrs = oesoff.substring(0, 2);
            String min = oesoff.substring(3);
            Long lhrs = new Long(hrs);
            Long lmin = new Long(min);

            Date dataTerminePresentazioneOfferte = new Date(desoff.getYear(), desoff.getMonth(), desoff.getDate(), lhrs.intValue(),
                lmin.intValue());

            if (dataTerminePresentazioneOfferte != null) {
              if (dataOdierna.after(dataTerminePresentazioneOfferte)) {
                isSuperataDataAperturaPlichi = "true";
              }
            }

            pageContext.setAttribute("torn_desoff", UtilityDate.convertiData(desoff, UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);
            pageContext.setAttribute("torn_oesoff", oesoff, PageContext.REQUEST_SCOPE);

          } else {
        	  isSuperataDataAperturaPlichi = "true";
          }
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo se l'acquisizione delle offerte dal portale Appalti e' abilitata", e);
      }
    }
     */
    String isSuperataDataAperturaPlichi = "false";
    MEPAManager mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager", pageContext, MEPAManager.class);
    try {
      String esitoControllo [] = mepaManager.controlloDataConDataAttuale(ngara, "desoff", "oesoff");
      isSuperataDataAperturaPlichi =  esitoControllo[0];
      if("false".equals(isSuperataDataAperturaPlichi)){
        pageContext.setAttribute("torn_desoff", esitoControllo [1], PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("torn_oesoff", esitoControllo [2], PageContext.REQUEST_SCOPE);
      }
    } catch (SQLException e) {
      throw new JspException("Errore durante il controllo se l'acquisizione delle offerte dal portale Appalti e' abilitata", e);
    }
    return isSuperataDataAperturaPlichi;
  }

}