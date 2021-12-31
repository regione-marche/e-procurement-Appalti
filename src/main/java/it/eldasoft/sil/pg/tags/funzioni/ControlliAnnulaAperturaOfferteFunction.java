/*
 * Created on 07-05-2019
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
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che controlla se vi sono dei lotti con fase maggiore di quella indicata
 * @author Marcello Caminiti
 */
public class ControlliAnnulaAperturaOfferteFunction extends AbstractFunzioneTag {

  public ControlliAnnulaAperturaOfferteFunction() {
    super(4, new Class[] { PageContext.class,String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ngara = (String) GeneralTagsFunction.cast("string", params[1]);
    String fase= (String) GeneralTagsFunction.cast("string", params[2]);
    String bustalotti = (String) GeneralTagsFunction.cast("string", params[3]);
    String controlloLottiFase="false";
    String controlloBusteTecniche = "false";

    if (ngara != null && ngara.length()>0 && "1".equals(bustalotti) && fase !=null && !"".equals(fase)){
      try {
        Long numLotti = (Long)sqlManager.getObject("select count(ngara) from gare where codgar1=? and ngara<>codgar1 and fasgar > ?", new Object[]{ngara, new Long(fase)});
        if(numLotti!= null && numLotti.longValue()>0)
          controlloLottiFase = "true";
      } catch (SQLException e) {
          throw new JspException(
              "Errore durante il conteggio di lotti con fase >" + fase + " per la gara " + ngara, e);
      }
    }
    if (ngara != null && ngara.length()>0 ){
      Long busteParziali = null;
      try {
        if("1".equals(bustalotti)) {
          String codgar = (String) sqlManager.getObject("select codgar1 from gare where ngara=?", new Object[]{ngara});
          List<?> lotti = sqlManager.getListVector("select ngara from gare where codgar1=? and ngara<>codgar1", new Object[]{codgar});
          if(lotti !=null && lotti.size() >0 ) {
            String lotto = null;

            for (int i=0;i<lotti.size();i++) {
              lotto = SqlManager.getValueFromVectorParam(lotti.get(i), 0).getStringValue();
              busteParziali = (Long)sqlManager.getObject("select count(*) from W_INVCOM where COMTIPO='FS11B' and COMSTATO in ('16','17') and COMKEY2=?", new Object[]{lotto});
              if(busteParziali!=null && busteParziali.longValue()>0) {
                controlloBusteTecniche = "true";
                break;
              }
            }
          }
        }else {
          busteParziali = (Long)sqlManager.getObject("select count(*) from W_INVCOM where COMTIPO='FS11B' and COMSTATO in ('16','17') and COMKEY2=?", new Object[]{ngara});
          if(busteParziali!=null && busteParziali.longValue()>0) {
            controlloBusteTecniche = "true";
          }
        }
      } catch (SQLException e) {
        throw new JspException(
            "Errore durante il controllo sulle buste tecniche della gara " + ngara, e);
      }

    }
    pageContext.setAttribute("esistonoLottiInFase7", controlloLottiFase, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("esistonoBusteTecParziali", controlloBusteTecniche, PageContext.REQUEST_SCOPE);
    return null;
  }
}
