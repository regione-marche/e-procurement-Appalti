/*
 * Created on 03-12-2013
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
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;
import java.util.List;

public class GetDocumentiRiconciliazionePECFunction extends AbstractFunzioneTag {

  public GetDocumentiRiconciliazionePECFunction() {
    super(4, new Class[] {PageContext.class, String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String idprg = (String) params[1];
    String idcom = (String) params[2];
    String idcomdes = (String) params[3];
    String a = null;
    String b = null;
    try {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      List files = sqlManager.getListVector("select dignomdoc, iddocdig from w_docdig where digent='W_INVCOMDES' and digkey1=? and digkey2=? and digkey3=? order by dignomdoc asc",
              new Object[] {idprg,idcom,idcomdes});
      if(files!=null && !files.isEmpty()) {
        for (Object o : files) {
          if("accettazione.eml".equals(SqlManager.getValueFromVectorParam(o, 0).stringValue()))
            a= StringUtils.stripToNull(String.valueOf((SqlManager.getValueFromVectorParam(o, 1).longValue())));
          if("consegna.eml".equals(SqlManager.getValueFromVectorParam(o, 0).stringValue()))
            b= StringUtils.stripToNull(String.valueOf((SqlManager.getValueFromVectorParam(o, 1).longValue())));
        }
      }
    } catch (SQLException|GestoreException e ) {
      throw new JspException("Errore nella lettura delle ricevute PEC)", e);
    }
    pageContext.setAttribute("idDocumentoAccettazionePEC", a,PageContext.PAGE_SCOPE);
    pageContext.setAttribute("idDocumentoRicevutaPEC", b,PageContext.PAGE_SCOPE);
    return null;
  }

}
