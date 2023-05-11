/*
 * Created on 26-04-2016
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
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che determina se la gara è stata pubblicata dopo il 19/04/2016 e se
 * è attiva la gestione manuale del criterio di calcolo della soglia di anomalia
 * Inoltre viene verificato se il numero di ditte in gara è superiore rispetto alla
 * soglie specificate nei tabellati A1135 e A2063
 *
 * @author M.C.
 */
public class IsGaraDopoDLGS2016ManualeFunction extends AbstractFunzioneTag {

  public IsGaraDopoDLGS2016ManualeFunction() {
    super(5, new Class[] { PageContext.class, String.class,String.class, String.class, String.class });
  }

  /**
   * Valori restituiti dalla funzione:
   * 0 gara pre DLgs.50/2016 o post 19/04/2019
   * 1 gara DLgs.50/2016 fino al 19/04/2019  manuale
   * 2 gara DLgs.50/2016 fino al 19/04/2019  autometica
   *
   */
  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    AggiudicazioneManager aggiudicazioneManager = (AggiudicazioneManager) UtilitySpring.getBean("aggiudicazioneManager",
        pageContext, AggiudicazioneManager.class);

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        pageContext, TabellatiManager.class);
    String result = "0";

    String ngara=(String)params[1];
    String eseguireControlloDitteNuovaNormativa =(String)params[2];
    String eseguireControlloDitteNormativaPrecedente =(String)params[3];
    String controlloSoloDitteInvOff =(String)params[4];
    int esitoControllo = 0;
    Boolean esitoControlloNumDitte= new Boolean(true);
    try {

        Vector datiGara = sqlManager.getVector("select t.iterga, t.dpubav, t.dinvit, g.calcsoang, g.modlicg, t.imptor, g.impapp, g.codgar1, t.tipgen," +
        		" g.modastg, t.calcsome from gare g, torn t" +
        		" where g.ngara=? and g.codgar1=t.codgar", new Object[] {ngara});

        if (datiGara != null && datiGara.size() > 0) {
          Long iterga =  SqlManager.getValueFromVectorParam(datiGara, 0).longValue();
          Timestamp dpubavg = SqlManager.getValueFromVectorParam(datiGara, 1).dataValue();
          Timestamp dinvit = SqlManager.getValueFromVectorParam(datiGara, 2).dataValue();
          String calcsoang = SqlManager.getValueFromVectorParam(datiGara, 3).stringValue();
          Long modlicg = SqlManager.getValueFromVectorParam(datiGara, 4).longValue();
          Double importoGara = SqlManager.getValueFromVectorParam(datiGara, 5).doubleValue();
          Double impapp = SqlManager.getValueFromVectorParam(datiGara, 6).doubleValue();
          String codgar1 = SqlManager.getValueFromVectorParam(datiGara, 7).stringValue();
          Long tipgen = SqlManager.getValueFromVectorParam(datiGara, 8).longValue();
          Long modastg = SqlManager.getValueFromVectorParam(datiGara, 9).longValue();
          String calcsome = SqlManager.getValueFromVectorParam(datiGara, 10).stringValue();
          if (("$" + ngara).equals(codgar1)){
            importoGara = impapp;
          }
          if (importoGara==null){
            importoGara=new Double(0);
          }

          Date datpub = (Date)sqlManager.getObject("select datpub from pubbli where codgar9=? and tippub=?", new Object[]{codgar1, new Long(11)});
          esitoControllo = aggiudicazioneManager.getLeggeCalcoloSoglia(iterga, dinvit, dpubavg, datpub, calcsome);

          if(esitoControllo > 0){
            if(esitoControllo != 3)
              result=aggiudicazioneManager.isModalitaManuale();

            if("true".equals(eseguireControlloDitteNuovaNormativa) && !new Long(6).equals(modlicg)){
              //Controllo che il numero di ditte ammesse in gara sia superiore al valore specificato nel tabellato A1135
              Object[] soglia = aggiudicazioneManager.getImportoSogliaPerGara(tipgen,dpubavg,dinvit,iterga.intValue(),1,codgar1);
              Double importoSogliaPerGara = (Double) soglia[0];
              int numeroVoceParametro=1;
              if (importoGara < importoSogliaPerGara.doubleValue())
                numeroVoceParametro=2;
              //Object[] risultato = aggiudicazioneManager.controlloNumDitteAmmesseSopraSoglia(ngara,"A1135",numeroVoceParametro);
              Object[] risultato = null;
              if("true".equals(controlloSoloDitteInvOff))
                risultato = aggiudicazioneManager.controlloNumDitteInvoffSopraSoglia(ngara,"A1135",numeroVoceParametro,calcsome);
              else
                risultato = aggiudicazioneManager.controlloNumDitteAmmesseSopraSoglia(ngara,"A1135",numeroVoceParametro,calcsome);
              esitoControlloNumDitte = (Boolean)risultato[0];
              String descTabellatoValoreConfronto = (String)risultato[1];
              pageContext.setAttribute("sogliaNumDitte",descTabellatoValoreConfronto, PageContext.REQUEST_SCOPE);

              String ditteInGara = (String)risultato[2];
              pageContext.setAttribute("ditteInGara",ditteInGara, PageContext.REQUEST_SCOPE);

              if("1".equals(calcsome) && new Long(1).equals(modastg)) {
                pageContext.setAttribute("initEscauto","5", PageContext.REQUEST_SCOPE);
              }else if("1".equals(calcsoang) && new Long(1).equals(modastg) && esitoControlloNumDitte){
                String initEscauto = null;
                //Gestione per campo ESCAUTO
                Long ditteAmmesse = aggiudicazioneManager.conteggioDitte(ngara,"(AMMGAR <> '2' or AMMGAR is null) and (MOTIES < 99 or MOTIES is null)");
                if (importoGara < importoSogliaPerGara.doubleValue()){
                  if(ditteAmmesse!=null && ditteAmmesse.longValue() < 10)
                    initEscauto = "1/3";
                  else if(ditteAmmesse!=null && ditteAmmesse.longValue() >= 10)
                    initEscauto = "2";
                }else if(importoGara >= importoSogliaPerGara.doubleValue())
                  initEscauto = "4";
                pageContext.setAttribute("initEscauto",initEscauto, PageContext.REQUEST_SCOPE);
              }
            }
          }
          pageContext.setAttribute("isGaraDLGS2016",new Boolean(esitoControllo== 1), PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("isGaraDLGS2017",new Boolean(esitoControllo== 2), PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("isGaraDL2019",new Boolean(esitoControllo== 3), PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("calcsoang",calcsoang, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("esitoControlloDitteDLGS2016",esitoControlloNumDitte, PageContext.REQUEST_SCOPE);
        }
        if("true".equals(eseguireControlloDitteNormativaPrecedente)){
          Boolean risultato = (Boolean)aggiudicazioneManager.controlloNumDitteAmmesseSopraSoglia(ngara,"A2063",1,"0")[0];
          pageContext.setAttribute("controlloDitteNormativaPrecedente",risultato, PageContext.REQUEST_SCOPE);
        }
      } catch (SQLException e) {
        throw new JspException(
            "Errore durante la lettura dei dati della gara", e);
      } catch (GestoreException e) {
        throw new JspException(
            "Errore durante la lettura dei dati della gara", e);
      }

    return result;
  }

}
