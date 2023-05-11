/*
 * Created on 29/09/15
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

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 *
 *
 * @author M. Caminiti
 */
public class GestioneGaraltsogFunction extends AbstractFunzioneTag {

  public GestioneGaraltsogFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String chiave = (String) params[1];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    //String select="select cenint,nomein,codrup from garaltsog, uffint where ngara=? and cenint=codein";
    String select="select cenint,codrup from garaltsog where ngara=? ";

    try {
      Vector datiGaraltsog = sqlManager.getVector(select, new Object[]{chiave});

      if (datiGaraltsog != null && datiGaraltsog.size() > 0){
        String cenint = SqlManager.getValueFromVectorParam(datiGaraltsog, 0).stringValue();
        String codrup = SqlManager.getValueFromVectorParam(datiGaraltsog, 1).stringValue();

        pageContext.setAttribute("initCenintGaraltsog", cenint,
            PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("initCodrupGaraltsog", codrup,
            PageContext.REQUEST_SCOPE);

        if(cenint!=null && !"".equals(cenint)){
          String nomein = (String)sqlManager.getObject("select nomein from uffint where codein=?", new Object[]{cenint});
          pageContext.setAttribute("initNomeinGaraltsog", nomein,
              PageContext.REQUEST_SCOPE);
        }
        if(codrup!=null && !"".equals(codrup)){
          String nomtec = (String)sqlManager.getObject("select nomtec from tecni where codtec=?", new Object[]{codrup});
          pageContext.setAttribute("initNomtecGaraltsog", nomtec,
              PageContext.REQUEST_SCOPE);
        }
      }

    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre i dati "
          + "della tabella GARALTSOG con chiave "
          + chiave, e);
    } catch (GestoreException e) {
      throw new JspException("Errore nell'estrarre i dati "
          + "della tabella GARALTSOG con chiave "
          + chiave, e);
    }

    return null;
  }

}
