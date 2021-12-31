/*
 * Created on 22-05-201
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
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che, se la gara è OEPV, ed è impostato il calcoloSoglia e
 * si rientra nel calcolo normativa DL.32/2019, effettua il controllo
 * sul numero di ditte ammesse rispetto al tabelatto  "A1156", e determina
 * se applicare il calcolo graduatoria anche se se previsto il calcolo
 * della soglia di anomalia (CALCSOANG.GARE = 1).
 *
 * @author M.C.
 */
public class ControlliDL_32_2019OEPVFunction extends AbstractFunzioneTag {

  public ControlliDL_32_2019OEPVFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }


  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    AggiudicazioneManager aggiudicazioneManager = (AggiudicazioneManager) UtilitySpring.getBean("aggiudicazioneManager",
        pageContext, AggiudicazioneManager.class);


    String result = "";

    String ngara=(String)params[1];
    int normativa = 0;
    try {

        Vector datiGara = sqlManager.getVector("select t.iterga, t.dpubav, t.dinvit, g.calcsoang, g.modlicg, t.imptor, g.impapp, g.codgar1, t.tipgen" +
        		" from gare g, torn t" +
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
          if (("$" + ngara).equals(codgar1)){
            importoGara = impapp;
          }
          if (importoGara==null){
            importoGara=new Double(0);
          }

          if(new Long(6).equals(modlicg) && "1".equals(calcsoang)){
            Date datpub = (Date)sqlManager.getObject("select datpub from pubbli where codgar9=? and tippub=?", new Object[]{codgar1, new Long(11)});
            normativa = aggiudicazioneManager.getLeggeCalcoloSoglia(iterga, dinvit, dpubavg, datpub);
            if(normativa  == 3){

                //Controllo che il numero di ditte ammesse in gara sia superiore al valore specificato nel tabellato A1135
                Object[] soglia = aggiudicazioneManager.getImportoSogliaPerGara(tipgen,dpubavg,dinvit,iterga.intValue(),1,codgar1);
                Double importoSogliaPerGara = (Double) soglia[0];
                int numeroVoceParametro=1;
                if (importoGara < importoSogliaPerGara.doubleValue())
                  numeroVoceParametro=2;
                Object[] risultato = aggiudicazioneManager.controlloNumDitteAmmesseSopraSoglia(ngara,"A1156",numeroVoceParametro);
                Boolean esitoControlloNumDitte = (Boolean)risultato[0];
                if(!esitoControlloNumDitte){
                  result="graduatoria";
                  pageContext.setAttribute("sogliaNumDitteOEPV",risultato[1], PageContext.REQUEST_SCOPE);
                }

            }
          }
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
