/*
 * Created on 31/03/16
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
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Controllo sulla valorizzazione dell'importo e del ribasso offerto
 *
 * @author Marcello Caminiti
 */
public class ControlloImportiEconomiciFunction extends AbstractFunzioneTag {

  public ControlloImportiEconomiciFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String chiave = (String) params[1];
    String offertaUnica = (String) params[2];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String message=null;
    try {
      String selectModlic =null;
      String selectDatiEconomici = null;
      if("false".equals(offertaUnica)){
        selectModlic ="select modlicg from gare where ngara=?";
        Long modlic = (Long)sqlManager.getObject(selectModlic, new Object[]{chiave});
        selectDatiEconomici = "select impoff, ribauo from ditg where ngara5=? and (fasgar > 6 or fasgar = 0 or fasgar is null)";
        List datiEconomici = sqlManager.getListVector(selectDatiEconomici, new Object[]{chiave});

        if(datiEconomici!=null && datiEconomici.size()>0){
          for (int i = 0; i < datiEconomici.size(); i++) {
            Double impoff = SqlManager.getValueFromVectorParam(datiEconomici.get(i), 0).doubleValue();
            Double ribauo = SqlManager.getValueFromVectorParam(datiEconomici.get(i), 1).doubleValue();
            if(modlic!=null){
              Long ribcal = (Long)sqlManager.getObject("select ribcal from gare where ngara=?", new Object[]{chiave});
              if(modlic.longValue()==6){
                if(impoff==null){
                  message="l'importo offerto";
                  break;
                }
              }else if(modlic.longValue()==5 || modlic.longValue()==14 || ((modlic.longValue()==1 || modlic.longValue()==13) && (new Long(2)).equals(ribcal))){
                if(impoff==null || ribauo==null){
                  message="il ribasso o l'importo offerto";
                  break;
                }
              }else if(modlic.longValue()==1 || modlic.longValue()==13){
                if(ribauo==null){
                  message="il ribasso offerto";
                  break;
                }
              }
            }
          }
        }
      }else{
        selectDatiEconomici = "select impoff, ribauo from ditg where codgar5=? and (fasgar > 6 or fasgar = 0 or fasgar is null) and ngara5=?";
        List<?> listaLotti = sqlManager.getListVector("select g.ngara, modlicg, ribcal from gare g,gare1 g1 where g.codgar1=? and "
            + "g.ngara<>g.codgar1 and g.ngara=g1.ngara and (costofisso is null or costofisso <> '1') order by codiga", new Object[] { chiave });
        if (listaLotti != null && listaLotti.size() > 0) {
          String ngaraLotto = null;
          Long modlicg = null;
          Long ribcal = null;
          for(int j=0; j < listaLotti.size() && message == null; j++){
            ngaraLotto = SqlManager.getValueFromVectorParam(listaLotti.get(j), 0).getStringValue();
            modlicg= SqlManager.getValueFromVectorParam(listaLotti.get(j), 1).longValue();
            ribcal = SqlManager.getValueFromVectorParam(listaLotti.get(j), 2).longValue();
            List datiEconomici = sqlManager.getListVector(selectDatiEconomici, new Object[]{chiave,ngaraLotto});

            if(datiEconomici!=null && datiEconomici.size()>0){
              for (int i = 0; i < datiEconomici.size(); i++) {
                Double impoff = SqlManager.getValueFromVectorParam(datiEconomici.get(i), 0).doubleValue();
                Double ribauo = SqlManager.getValueFromVectorParam(datiEconomici.get(i), 1).doubleValue();
                if(modlicg!=null){
                  if(modlicg.longValue()==6){
                    if(impoff==null){
                      message="l'importo offerto";
                      break;
                    }
                  }else if(modlicg.longValue()==5 || modlicg.longValue()==14 || ((modlicg.longValue()==1 || modlicg.longValue()==13) && (new Long(2)).equals(ribcal))){
                    if(impoff==null || ribauo==null){
                      message="il ribasso o l'importo offerto";
                      break;
                    }
                  }else if(modlicg.longValue()==1 || modlicg.longValue()==13){
                    if(ribauo==null){
                      message="il ribasso offerto";
                      break;
                    }
                  }
                }
              }
            }
          }
        }
      }
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura dei dati economici della gara " +  chiave, e);
    } catch (GestoreException e) {
      throw new JspException("Errore nella lettura dei dati economici della gara " +  chiave, e);
    }
    return message;
  }

}