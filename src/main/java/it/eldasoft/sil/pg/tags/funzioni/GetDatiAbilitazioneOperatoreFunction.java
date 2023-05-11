/*
 * Created on 16-010-2018
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
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Carica i dati ABILITAZ e DABILITAZ di DITG per una ditta
 *
 *
 * @author Marcello Caminiti
 */
public class GetDatiAbilitazioneOperatoreFunction extends AbstractFunzioneTag {

  public GetDatiAbilitazioneOperatoreFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String select = "select abilitaz,dabilitaz from ditg where ngara5=? and dittao=?";
    String codiceGara = (String)params[1];
    String codiceOperatore= (String)params[2];

    if (codiceGara != null && codiceGara.length() > 0) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);


      try {
        String abilitaz="";
        Timestamp dabilitaz=null;
        String dabilitazString=null;
        Vector<?> datiDitg = sqlManager.getVector(select, new Object[]{codiceGara,codiceOperatore});
        if(datiDitg!=null && datiDitg.size()>0){
          abilitaz=SqlManager.getValueFromVectorParam(datiDitg, 0).getStringValue();
          dabilitaz = SqlManager.getValueFromVectorParam(datiDitg, 1).dataValue();
          if(dabilitaz!=null){
            dabilitazString = UtilityDate.convertiData(new Date(dabilitaz.getTime()), UtilityDate.FORMATO_GG_MM_AAAA);
            dabilitazString = sqlManager.getDBFunction("stringtodate",
              new String[] { dabilitazString });
          }
        }
        pageContext.getRequest().setAttribute("abilitaz", abilitaz);
        pageContext.getRequest().setAttribute("dabilitaz", dabilitaz);
        pageContext.getRequest().setAttribute("dabilitazString", dabilitazString);

      } catch (SQLException e) {
        throw new JspException("Errore nella dei dati di abilitazione dell'operatore " + codiceOperatore + " dell'elenco "
            + codiceGara, e);
      } catch (GestoreException e) {
        throw new JspException("Errore nella dei dati di abilitazione dell'operatore " + codiceOperatore + " dell'elenco "
            + codiceGara, e);
      }
    }
    return null;
  }

}