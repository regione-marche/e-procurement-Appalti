/*
 * Created on 21/apr/2020
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
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityMath;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;


public class GestioneListaControlloSpeseFunction extends AbstractFunzioneTag {

  /**
   * La funzione prevede come secondo parametro una stringa così formattata:
   * ngara:ncont:ngaral:modcont:ditta
   */
  public GestioneListaControlloSpeseFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }


  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        pageContext, PgManagerEst1.class);

    String parametro = ((String) params[1]);
    if(parametro==null || "".equals(parametro))
      return null;

    if(parametro.indexOf(":") <= 0)
      return null;

    String vetPar[] = parametro.split(":");
    String ngara = vetPar[0];
    String ncont = vetPar[1];
    String ngaral = null;
    String modcont = null;
    String ditta = null;
    if(vetPar.length>2){
      ngaral = vetPar[2];
      modcont = vetPar[3];
      if(vetPar.length==5)
        ditta = vetPar[4];
    }

    try {
        Object impqua = sqlManager.getObject("select impqua from garecont where ngara=? and ncont=?", new Object[]{ngara, new Long(ncont)});

        //Disponibilita
        Double importoDisponibilita = new Double(0);
        if(impqua!=null)
          importoDisponibilita= pgManagerEst1.getImportoDaObject(impqua);

        String importoFormattato = pgManagerEst1.convertiMoney5(UtilityNumeri.convertiImporto(
            importoDisponibilita, 5));

        pageContext.setAttribute("disponibilita", importoFormattato, PageContext.REQUEST_SCOPE);

        //Prenotato autorizzato
        Double importoPrenotato = new Double(0);
        Object importoPrenotatoObj =  sqlManager.getObject("select sum(coalesce(impaut,0)) from g1aqspesa where ngara=? and ncont=?", new Object[]{ngara, new Long(ncont)});

        if(importoPrenotatoObj!=null)
          importoPrenotato= pgManagerEst1.getImportoDaObject(importoPrenotatoObj);

        importoFormattato = pgManagerEst1.convertiMoney5(UtilityNumeri.convertiImporto(
            importoPrenotato, 5));
        pageContext.setAttribute("importoPrenotato", importoFormattato, PageContext.REQUEST_SCOPE);

        //Residuo da autorizzare
        Double importoResiduo = new Double(importoDisponibilita.doubleValue() - importoPrenotato.doubleValue());
        importoFormattato = pgManagerEst1.convertiMoney5(UtilityNumeri.convertiImporto(
            importoResiduo, 5));
        pageContext.setAttribute("importoResiduo", importoFormattato, PageContext.REQUEST_SCOPE);

        //Importo impegnato effettivo
        String selectAdesioni="select sum(coalesce(iaggiu,0)) from v_gare_adesioni where ngaraaq";
        String chiaveAdesioni ="";
        if("2".equals(modcont)){
          chiaveAdesioni = pgManagerEst1.getElencoLottiAggiudicati(ngara, ditta);
          selectAdesioni+= " in (" + chiaveAdesioni + ")";
        }else{
          chiaveAdesioni = ngara;
          if("1".equals(modcont))
            chiaveAdesioni = ngaral;
          selectAdesioni+="='"+ chiaveAdesioni + "'";
        }
        Double importoImpegnato = new Double(0);
        Object importoImpegnatoObj =  sqlManager.getObject(selectAdesioni,null);

        if(importoImpegnatoObj!=null)
          importoImpegnato= pgManagerEst1.getImportoDaObject(importoImpegnatoObj);

        importoFormattato = pgManagerEst1.convertiMoney5(UtilityNumeri.convertiImporto(
            importoImpegnato, 5));
        pageContext.setAttribute("importoImpegnato", importoFormattato, PageContext.REQUEST_SCOPE);

        //Residuo da impegnare
        Double importoResiduoDaImpegnare = new Double(importoPrenotato.doubleValue() - importoImpegnato.doubleValue());
        importoFormattato = pgManagerEst1.convertiMoney5(UtilityNumeri.convertiImporto(
            importoResiduoDaImpegnare, 5));
        pageContext.setAttribute("importoResiduoDaImpegnare", importoFormattato, PageContext.REQUEST_SCOPE);

        //Percentuali impiego
        double percentualePrenotato=0;
        double percentualeResiduo=0;
        double percentualeImpegnato=0;
        String valoreStrig="0";
        if(importoDisponibilita.doubleValue()!=0){
          BigDecimal bdImportoPrenotato=new BigDecimal(importoPrenotato.doubleValue());
          BigDecimal bdImportoDisponibilita=new BigDecimal(importoDisponibilita.doubleValue());
          bdImportoPrenotato = bdImportoPrenotato.divide(bdImportoDisponibilita,4,RoundingMode.CEILING);
          bdImportoPrenotato = bdImportoPrenotato.multiply(new BigDecimal(100));
          //bdImportoPrenotato = bdImportoPrenotato.setScale(2, RoundingMode.CEILING);
          percentualePrenotato = UtilityMath.round(bdImportoPrenotato.doubleValue(), 2);
          valoreStrig = UtilityNumeri.convertiDouble(percentualePrenotato, UtilityNumeri.FORMATO_DOUBLE_CON_VIRGOLA_DECIMALE, 2);
          pageContext.setAttribute("percentualePrenotato", valoreStrig, PageContext.REQUEST_SCOPE);

          percentualeResiduo = UtilityMath.round(100 - percentualePrenotato,2);
          valoreStrig =UtilityNumeri.convertiDouble(percentualeResiduo, UtilityNumeri.FORMATO_DOUBLE_CON_VIRGOLA_DECIMALE, 2);
          pageContext.setAttribute("percentualeResiduo", valoreStrig, PageContext.REQUEST_SCOPE);


        }else{
          pageContext.setAttribute("percentualePrenotato", valoreStrig, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("percentualeResiduo", valoreStrig, PageContext.REQUEST_SCOPE);

        }


        valoreStrig=null;
        if(importoPrenotato!=null && importoPrenotato.doubleValue()!=0){
          BigDecimal bdImportoPrenotato=new BigDecimal(importoPrenotato.doubleValue());
          BigDecimal bdImportoImpegnato=new BigDecimal(importoImpegnato.doubleValue());
          bdImportoImpegnato = bdImportoImpegnato.divide(bdImportoPrenotato,4,RoundingMode.CEILING);
          bdImportoImpegnato = bdImportoImpegnato.multiply(new BigDecimal(100));
          percentualeImpegnato = UtilityMath.round(bdImportoImpegnato.doubleValue(),2);
          valoreStrig = UtilityNumeri.convertiDouble(percentualeImpegnato, UtilityNumeri.FORMATO_DOUBLE_CON_VIRGOLA_DECIMALE, 2);
          pageContext.setAttribute("percentualeImpegnato", valoreStrig, PageContext.REQUEST_SCOPE);

          double percentualeResiduoDaImpegnare = UtilityMath.round(100 - percentualeImpegnato,2);
          valoreStrig =UtilityNumeri.convertiDouble(percentualeResiduoDaImpegnare, UtilityNumeri.FORMATO_DOUBLE_CON_VIRGOLA_DECIMALE, 2);
          pageContext.setAttribute("percentualeResiduoDaImpegnare", valoreStrig, PageContext.REQUEST_SCOPE);
        }else{
          pageContext.setAttribute("percentualeImpegnato", valoreStrig, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("percentualeResiduoDaImpegnare", valoreStrig, PageContext.REQUEST_SCOPE);
        }



    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura degli importi di riepilogo", e);
    }
    return null;

  }


}
