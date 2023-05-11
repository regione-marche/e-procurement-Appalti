/*
 * Created on 29-04-2013
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
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Si determina quante imprese iscritte all'elenco non sono registrate su portale.
 *
 * @author Marcello Caminiti
 */
public class ControlliImpreseRegistrateElencoFunction extends AbstractFunzioneTag {

  public ControlliImpreseRegistrateElencoFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String ngara = (String) params[1];
    String ret="NO";
    String msg="";
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    long numImpreseNonRegistrate=0;
    long numTotaleImprese=0;
    try {
      List listaDitte =  sqlManager.getListVector("select dittao,tipimp from ditg,impr where ngara5=? and codgar5=? and dittao=codimp",
          new Object[] { ngara, "$" + ngara });
      if(listaDitte!=null && listaDitte.size()>0){
        numTotaleImprese= new Long(listaDitte.size());
        for(int i=0;i<listaDitte.size();i++){
          String dittao = (String)SqlManager.getValueFromVectorParam(listaDitte.get(i), 0).getValue();
          Long tipimp = (Long)SqlManager.getValueFromVectorParam(listaDitte.get(i), 1).getValue();
          Long iduser = (Long)sqlManager.getObject("select iduser from w_puser where userent=? and userkey1=?", new Object[]{"IMPR",dittao});
          if(iduser== null && (tipimp==null || (tipimp!=null && tipimp.longValue()!=3 && tipimp.longValue()!=10)))
            numImpreseNonRegistrate++;

        }
        if(numImpreseNonRegistrate>0){
          msg="Sono presenti <b>" + Long.toString(numImpreseNonRegistrate)+"</b> imprese non registrate sul portale su un totale di <b>" + Long.toString(numTotaleImprese) + "</b> imprese iscritte all'elenco. Vuoi proseguire con la procedura di registrazione massiva?";
          ret="SI";
        }else{
          msg="Non esistono imprese iscritte all'elenco da registrare nel portale";
        }
        pageContext.setAttribute("msg", msg, PageContext.REQUEST_SCOPE);
      }
    } catch (SQLException s) {
      throw new JspException("Errore durante la lettura delle imprese iscritte all'elenco:" + ngara,
          s);
    }



    return ret;
  }

}
