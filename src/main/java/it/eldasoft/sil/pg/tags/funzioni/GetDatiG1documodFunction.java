/*
 * Created on 24/09/2021
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
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che recupera i parametri dalla G1documod
 *
 * @author Riccardo.Peruzzo
 */
public class GetDatiG1documodFunction extends AbstractFunzioneTag {

  public GetDatiG1documodFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String idDocumod = (String) params[1];
    idDocumod = UtilityStringhe.convertiNullInStringaVuota(idDocumod);
    if(!"".equals(idDocumod)){
      Long idS = Long.valueOf(idDocumod);

      String selectDocumod = "select gruppo,busta from G1documod where id = ?";

      try {

        Vector<?> datiDocumod = sqlManager.getVector(selectDocumod,new Object[] { idS });
        if (datiDocumod != null && datiDocumod.size() > 0){
          Long gruppo = SqlManager.getValueFromVectorParam(datiDocumod, 0).longValue();
          String sGruppo = String.valueOf(gruppo);
          pageContext.setAttribute("gruppo",sGruppo,PageContext.REQUEST_SCOPE);
          Long busta = SqlManager.getValueFromVectorParam(datiDocumod, 1).longValue();
          if(!"".equals(busta)){
        	  String sBusta = String.valueOf(busta);
        	  pageContext.setAttribute("busta",sBusta,PageContext.REQUEST_SCOPE);
          }
          else {
        	  pageContext.setAttribute("busta",busta,PageContext.REQUEST_SCOPE);
          }
        }

      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre i dati dalla G1documod", e);
      } catch (GestoreException ge) {
        throw new JspException("Errore nell'estrarre i dati dalla G1documod", ge);
      } catch (Exception ex) {
        throw new JspException("Errore nell'estrarre i dati dalla G1documod", ex);
      }

    }

    return null;
  }

}
