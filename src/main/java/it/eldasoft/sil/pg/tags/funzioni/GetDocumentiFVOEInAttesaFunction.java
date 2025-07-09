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

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

public class GetDocumentiFVOEInAttesaFunction extends AbstractFunzioneTag {

  public GetDocumentiFVOEInAttesaFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    String codgar = (String) params[1];
    String codimp = (String) params[2];
    String listaString="";

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    try {
      List<Vector> lista = sqlManager.getListVector("select distinct codice from g1fvoedoc where statorichiesta in ('1','2','3') and codgar=? and codimp=? order by codice",
              new Object[] {codgar,codimp});
      if(lista!=null && !lista.isEmpty()){
        for(int i=0;i<lista.size();i++){
          listaString+=SqlManager.getValueFromVectorParam(lista.get(i), 0).getStringValue()+";";
        }
      }

    } catch (SQLException e) {
      throw new JspException("Errore durante il prelievo delle tipologie di documento FVOE in attesa", e);
    }
    return listaString;
  }

}