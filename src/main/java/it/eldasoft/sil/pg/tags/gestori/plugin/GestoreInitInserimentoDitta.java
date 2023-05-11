/*
 * Created on 29-02-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore preload per la pagina ditg-schedaPopup-insert.jsp.
 *
 * @author Marcello Caminiti
 */

public class GestoreInitInserimentoDitta extends AbstractGestorePreload {

  public GestoreInitInserimentoDitta(BodyTagSupportGene tag) {
    super(tag);
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {

  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",page, SqlManager.class);

    if (!"NUOVO".equals(modoAperturaScheda)) {
      HashMap key = UtilityTags.stringParamsToHashMap(
          (String) page.getAttribute(
              UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, PageContext.REQUEST_SCOPE),
          null);

      String codiceImpresa = ((JdbcParametro) key.get("DITG.DITTAO")).getStringValue();
      try {
        Long tipimp = (Long)sqlManager.getObject("select tipimp  from impr where codimp=?", new Object[]{codiceImpresa});
        if(tipimp != null && (tipimp.longValue()==3 || tipimp.longValue()==10)){
          page.setAttribute("isRTI", new Long(1), PageContext.REQUEST_SCOPE);
          page.setAttribute("tipoRTI", tipimp, PageContext.REQUEST_SCOPE);
          this.getDatiRaggruppamento(codiceImpresa, page, sqlManager);
        }else{
          page.setAttribute("isRTI", new Long(0), PageContext.REQUEST_SCOPE);
        }
      } catch (SQLException e) {
        throw new JspException("Errore durante l'estrazione dei dati della ditta", e);
      }

    }else{
      String isRTI = UtilityTags.getParametro(page,"isRTI");
      String tipoRTI = UtilityTags.getParametro(page,"tipoRTI");
      if(tipoRTI== null || "".equals(tipoRTI))
        tipoRTI ="0";

      String codiceRaggruppamento =   UtilityTags.getParametro(page,"codiceRaggruppamento");

      if(isRTI== null || "".equals(isRTI))
        isRTI ="0";
      else
        page.setAttribute("tipoRTI", Long.valueOf(tipoRTI), PageContext.REQUEST_SCOPE);

      if(codiceRaggruppamento!=null && !"".equals(codiceRaggruppamento)){
        //if(raggruppamentoSelezionato!=null && "SI".equals(raggruppamentoSelezionato)){
        this.getDatiRaggruppamento(codiceRaggruppamento, page, sqlManager);
      }

      String offertaRT = UtilityTags.getParametro(page,"offertaRT");
      if("1".equals(offertaRT)){
        String codiceDitta = UtilityTags.getParametro(page,"codiceDitta");
        String numeroGara = UtilityTags.getParametro(page,"numeroGara");
        String codiceGara = UtilityTags.getParametro(page,"codiceGara");
        try {
          //La gara è un lotto di uan gara ad offerte distinte?
          Long genere = (Long) sqlManager.getObject("select genere from v_gare_torn where codgar=?", new Object[]{codiceGara});
          boolean isOfferteDistinte= false;
          if(genere!= null && genere.longValue()==1)
            isOfferteDistinte= true;

          Vector<JdbcParametro> datiImpr = sqlManager.getVector("select nomest, cfimp, pivimp from impr where codimp=?", new Object[]{codiceDitta});
          if(datiImpr!=null && datiImpr.size()>0){
            String nomest = datiImpr.get(0).getStringValue();
            String cf = datiImpr.get(1).getStringValue();
            String piva = datiImpr.get(2).getStringValue();
            page.setAttribute("nomestMandataria", nomest, PageContext.REQUEST_SCOPE);
            page.setAttribute("CfMandataria", cf, PageContext.REQUEST_SCOPE);
            page.setAttribute("PivaMandataria", piva, PageContext.REQUEST_SCOPE);
          }
          @SuppressWarnings("unchecked")
          String dbFunctionDateToString = sqlManager.getDBFunction("DATETIMETOSTRING",
              new String[] { "dproff" });
          Vector<JdbcParametro> datiDitg = sqlManager.getVector("select nproff, datoff, oraoff, mezoff, plioff, notpoff, nprogg, " + dbFunctionDateToString + " from ditg where dittao=? and ngara5=?", new Object[]{codiceDitta,numeroGara});
          if(datiDitg!=null && datiDitg.size()>0){
            String nproff = datiDitg.get(0).getStringValue();
            Timestamp datoff = datiDitg.get(1).dataValue();
            String dataOfferta = null;
            if(datoff!=null){
              dataOfferta = UtilityDate.convertiData(new Date(datoff.getTime()), UtilityDate.FORMATO_GG_MM_AAAA);
            }
            String oraff = datiDitg.get(2).getStringValue();
            Long mezoff = datiDitg.get(3).longValue();
            Long plioff = datiDitg.get(4).longValue();
            String notpoff = datiDitg.get(5).getStringValue();
            Long nprogg = datiDitg.get(6).longValue();
            String dproff = datiDitg.get(7).stringValue();
            if(dproff!=null && !"".equals(dproff) && dproff.length()>=15){
              String data = dproff.substring(0, 10);
              String ora = dproff.substring(11, 16);
              page.setAttribute("dproffData", data, PageContext.REQUEST_SCOPE);
              page.setAttribute("dproffOra", ora, PageContext.REQUEST_SCOPE);
            }
            page.setAttribute("nproff", nproff, PageContext.REQUEST_SCOPE);
            page.setAttribute("datoff", dataOfferta, PageContext.REQUEST_SCOPE);
            page.setAttribute("oraff", oraff, PageContext.REQUEST_SCOPE);

            page.setAttribute("mezoff", mezoff, PageContext.REQUEST_SCOPE);
            page.setAttribute("plioff", plioff, PageContext.REQUEST_SCOPE);
            page.setAttribute("notpoff", notpoff, PageContext.REQUEST_SCOPE);
            page.setAttribute("nprogg", nprogg, PageContext.REQUEST_SCOPE);
            page.setAttribute("isOfferteDistinte", new Boolean(isOfferteDistinte), PageContext.REQUEST_SCOPE);
          }
        } catch (SQLException e) {
          throw new JspException("Errore durante l'estrazione dei dati della ditta", e);
        }catch (GestoreException e) {
          throw new JspException("Errore durante l'estrazione dei dati della ditta", e);
        }
      }

      page.setAttribute("isRTI", Long.valueOf(isRTI), PageContext.REQUEST_SCOPE);
    }



  }

  private void getDatiRaggruppamento(String codiceRaggruppamento,PageContext page, SqlManager sqlManager) throws JspException{
    try {
      //Dati della mandataria del raggruppamento
      String nomest=(String)sqlManager.getObject("select nomest from impr where codimp=?", new Object[]{codiceRaggruppamento});
      String   select= "select CODDIC, NOMDIC, CFIMP, PIVIMP, QUODIC  from RAGIMP,IMPR where CODIME9 = ? and IMPMAN = '1' and CODDIC=CODIMP";
      Vector datiMandataria = sqlManager.getVector(select, new Object[]{codiceRaggruppamento});
      if(datiMandataria!=null && datiMandataria.size()>0){
        Object obj = null;
        obj = ((JdbcParametro) datiMandataria.get(0)).getValue();
        if(obj != null)
          page.setAttribute("codiceMandataria", obj, PageContext.REQUEST_SCOPE);

        obj = ((JdbcParametro) datiMandataria.get(1)).getValue();
        if(obj != null)
          page.setAttribute("ragSocMandataria", obj, PageContext.REQUEST_SCOPE);

        obj = ((JdbcParametro) datiMandataria.get(2)).getValue();
        if(obj != null)
          page.setAttribute("codfiscMandataria", obj, PageContext.REQUEST_SCOPE);

        obj = ((JdbcParametro) datiMandataria.get(3)).getValue();
        if(obj != null)
          page.setAttribute("pivaMandataria", obj, PageContext.REQUEST_SCOPE);

        obj = ((JdbcParametro) datiMandataria.get(4)).getValue();
        if(obj != null){
          try {
            Double quodic = ((JdbcParametro) datiMandataria.get(4)).doubleValue();
            page.setAttribute("partecipazioneMandataria", quodic, PageContext.REQUEST_SCOPE);
          } catch (GestoreException e) {
            throw new JspException("Errore durante l'estrazione dei dati delle ditte del raggruppamento", e);
          }

        }
      }
      //

      //Dati delle imprese componenti del raggruppamento
      select= "select CODIME9, CODDIC, NOMDIC, QUODIC, IMPMAN, CGENIMP, CFIMP, PIVIMP  from RAGIMP,IMPR where CODIME9 = ? and " +
        "(IMPMAN <> '1' or IMPMAN is null) and coddic=codimp ";


      List listaRaggruppamenti = sqlManager.getListVector(
          select,new Object[]{codiceRaggruppamento});

      page.setAttribute("codiceRaggruppamento", codiceRaggruppamento, PageContext.REQUEST_SCOPE);
      page.setAttribute("ragSocRaggruppamento", nomest, PageContext.REQUEST_SCOPE);

      if (listaRaggruppamenti != null && listaRaggruppamenti.size() > 0)
          page.setAttribute("listaRaggruppamenti", listaRaggruppamenti,PageContext.REQUEST_SCOPE);

      page.setAttribute("raggruppamentoSelezionato", "SI", PageContext.REQUEST_SCOPE);
    }catch (SQLException e) {
        throw new JspException("Errore durante l'estrazione dei dati delle ditte del raggruppamento", e);
   }
  }
}
