/*
 * Created on 25/03/2015
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
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetValoriDettaglioRaggruppanentoFunction extends AbstractFunzioneTag {

  public GetValoriDettaglioRaggruppanentoFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    String ngara = (String) params[1];
    String ditta = (String) params[2];
    String contatore = (String) params[3];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    List<?> datiRagdet = null;

    try {
      if(ngara!=null){
        String selectRagdet = "select r.codimp, r.coddic, r.numdic, r.ngara, i.nomest from ragdet r,impr i where  r.coddic=i.codimp and r.codimp=? and r.ngara=?" +
                  " order by r.coddic";

        datiRagdet = sqlManager.getListVector(selectRagdet, new Object[] {ditta,ngara });
      }
      if(datiRagdet!=null && datiRagdet.size()>0){
        String label = "datiRagdet";
        if(contatore!=null && !"".equals(contatore))
          label += contatore;
        pageContext.setAttribute(label, datiRagdet, PageContext.REQUEST_SCOPE);
      }
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura della tabella RAGDET", e);
    }

    return null;
  }

}
