/*
 * Created on 10/10/2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class ControlliAbilitazioneValutazioneMEvalFunction extends AbstractFunzioneTag {

  public ControlliAbilitazioneValutazioneMEvalFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ret="OK";

    String codgar = (String) params[1];

    try {
      Long conteggio=(Long)sqlManager.getObject("select count(*) from gare1 where codgar1=? and statocg=?", new String[] {codgar, "2"});
      if(conteggio != null && conteggio.longValue()>0) {
        ret="NOK-1";
      }else {
        conteggio=(Long)sqlManager.getObject("select count(*) from gare where codgar1=? and fasgar > ? and modlicg=?", new Object[] {codgar, new Long(5), new Long(6)});
        if(conteggio != null && conteggio.longValue()>0) {
          ret="NOK-2";
        }else {
          List<?> lotti = sqlManager.getListVector("select ngara from torn,gare where codgar=? and codgar=codgar1 and ngara!=codgar and modlicg=6", new Object[] {codgar});
          if(lotti!=null && lotti.size()>0) {
            String lotto=null;
            long numLottiNok=0;
            for(int i=0;i<lotti.size();i++) {
              lotto=SqlManager.getValueFromVectorParam(lotti.get(i), 0).getStringValue();
              conteggio=(Long)sqlManager.getObject("select count(g1.ngara) from g1cridef g1, goev g where g1.ngara=? and g.ngara=g1.ngara and g.necvan=g1.necvan and (modpunti=1 or modpunti=3) and tippar=1", new Object[] {lotto});
              if(conteggio==null || new Long(0).equals(conteggio)) {
                numLottiNok++;
              }
            }
            if(numLottiNok == lotti.size())
              ret="NOK-3";
          }
        }
      }
    } catch (Exception e) {
      throw new JspException("Errore nei controlli preliminari della funzione di abilitazione valutazione M-Eval", e);
    }

    return ret;

  }

}
