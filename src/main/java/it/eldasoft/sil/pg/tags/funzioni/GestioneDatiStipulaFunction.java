/*
 * Created on 09/04/2021
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
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Funzione che recupera i dati della stipula
 *
 * @author Cristian.Febas
 */
public class GestioneDatiStipulaFunction extends AbstractFunzioneTag {

  public GestioneDatiStipulaFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String idStipula = (String) params[1];
    idStipula = UtilityStringhe.convertiNullInStringaVuota(idStipula);

    String whereLottiAggiudicati="";

    if(!"".equals(idStipula)){
      Long idS = new Long(idStipula);

      String selDatiStipula = "select vgs.codgar,vgs.ngara,vgs.ncont,vgs.codstipula,vgs.codimp,vgs.modcont,vgs.cenint" +
      		" from v_gare_stipula vgs" +
      		" join garecont gc on vgs.ngara=gc.ngara and vgs.ncont=gc.ncont" +
      		" where vgs.id = ?";

      try {

        Vector<?> datiStipula = sqlManager.getVector(selDatiStipula,new Object[] { new Long(idS) });
        if (datiStipula != null && datiStipula.size() > 0){
          String codgar = SqlManager.getValueFromVectorParam(datiStipula, 0).stringValue();
          pageContext.setAttribute("codgar",codgar,PageContext.REQUEST_SCOPE);
          String ngara = SqlManager.getValueFromVectorParam(datiStipula, 1).stringValue();
          pageContext.setAttribute("ngara",ngara,PageContext.REQUEST_SCOPE);
          Long ncont = SqlManager.getValueFromVectorParam(datiStipula, 2).longValue();
          pageContext.setAttribute("ncont",ncont,PageContext.REQUEST_SCOPE);
          String codstipula = SqlManager.getValueFromVectorParam(datiStipula, 3).stringValue();
          pageContext.setAttribute("codstipula",codstipula,PageContext.REQUEST_SCOPE);
          String codimp = SqlManager.getValueFromVectorParam(datiStipula, 4).stringValue();
          pageContext.setAttribute("codimp",codimp,PageContext.REQUEST_SCOPE);
          Long modcont = SqlManager.getValueFromVectorParam(datiStipula, 5).longValue();
          String cenint =  SqlManager.getValueFromVectorParam(datiStipula, 6).stringValue();
          pageContext.setAttribute("uffintStipula",cenint,PageContext.REQUEST_SCOPE);
          /*
          if(Long.valueOf(1).equals(modcont)){
            whereLottiAggiudicati ="GARE.CODGAR1 = '" + codgar + "' and GARE.NGARA = '" + ngara + "' and GARE.DITTA = '"+ codimp + "' and (GARE.GENERE is null)";
          }else{
            whereLottiAggiudicati ="GARE.CODGAR1 = '" + codgar + "' and GARE.DITTA = '"+ codimp + "' and (GARE.GENERE is null)";
          }
          */
          whereLottiAggiudicati ="GARECONT.NGARA = '" + ngara + "' and GARECONT.NCONT = "+ ncont + " AND (GENERE IS NULL OR GENERE<>3)";

          pageContext.setAttribute("whereLottiAggiudicati",whereLottiAggiudicati,PageContext.REQUEST_SCOPE);

        }

      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre i dati della gara/lotto collegata alla stipula", e);
      } catch (GestoreException ge) {
        throw new JspException("Errore nell'estrarre i dati della gara/lotto collegata alla stipula", ge);
      }

    }

    return null;
  }

}
