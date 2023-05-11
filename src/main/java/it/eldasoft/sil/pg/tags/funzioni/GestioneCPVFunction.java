/*
 * Created on 27/08/22
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione per leggere i valori di cpv
 *
 *
 */
public class GestioneCPVFunction extends
    AbstractFunzioneTag {

  public GestioneCPVFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String nGara = (String) params[0];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      List CPV = null;
      List<?> cpv = sqlManager.getListVector(
          "select NGARA, NUMCPV, CODCPV, TIPCPV "
              + "from GARCPV "
              + "where NGARA = ? "
              + "order by NUMCPV asc", new Object[] { nGara });

      if (cpv != null && cpv.size() > 0) {
        boolean cpvPrincipalePresente=false;
        for(int i=0; i<cpv.size(); i++) {
          String TIPCPV = SqlManager.getValueFromVectorParam(cpv.get(i), 3).getStringValue();
          if("1".equals(TIPCPV)) {
            cpvPrincipalePresente=true;
            break;
          }
        }
        //Per visualizzare la riga del cpv principale anche se non valorizzata
        if(!cpvPrincipalePresente) {
          CPV = new ArrayList();
          Vector<JdbcParametro> cpvPrincipale = new Vector<JdbcParametro>();
          cpvPrincipale.add(new JdbcParametro(JdbcParametro.TIPO_TESTO,nGara));
          cpvPrincipale.add(new JdbcParametro(JdbcParametro.TIPO_NUMERICO,new Long(0)));
          cpvPrincipale.add(new JdbcParametro(JdbcParametro.TIPO_TESTO,""));
          cpvPrincipale.add(new JdbcParametro(JdbcParametro.TIPO_TESTO,"1"));
          CPV.add(cpvPrincipale);
          for(int i=0; i<cpv.size(); i++) {
            CPV.add(cpv.get(i));
          }

        }else {
          CPV = cpv;
        }
        pageContext.setAttribute("cpv", CPV, PageContext.REQUEST_SCOPE);
      }
    } catch (SQLException e) {
      throw new JspException(
          "Errore nell'estrarre i CPV oggetto complementare "
              + "della gara "
              + nGara, e);
    }

    return null;
  }

}
