/*
 * Created on 03-12-2013
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione adoperata nelle fasi di gara per controllare se nelle imprese partecipanti vi sono note e avvisi e
 * nel caso di consorzi e RT si verifica che esistano almeno due componenti, e nel solo caso di RT
 * si controlla che esista la mandataria
 * Esito note\avvisi:
 * 0        non vi sono note
 * 1        vi sono note/avvisi per l'impresa
 * 2        vi sono note/avvisi per RT
 * 3        vi sono note/avvisi per il consorzio
 * Esito componenti
 * 0        tutti i controlli rispettati
 * 1        RT in cui non sono presenti almeno due componenti
 * Esito controllo mandataria
 * 0        Mandataria presente
 * 1        Mandataria non presente
 *
 * @author Cristian Febas
 */
public class CheckNoteAvvisiImpresaPartecipantiFunction extends AbstractFunzioneTag {

  public CheckNoteAvvisiImpresaPartecipantiFunction() {
    super(3, new Class[] {PageContext.class, String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String dittao = (String) params[1];
    dittao = UtilityStringhe.convertiNullInStringaVuota(dittao);
    String ngara = (String) params[2];
    String flagNoteAvvisi = "0";
    String flagComponenti = "0";
    String contolloMandataria = "0";
    Long numNoteAvvisi = new Long(0);
    if ("".equals(dittao)) {
      pageContext.setAttribute("controlloNoteAvvisi",flagNoteAvvisi,PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("controlloComponenti",flagComponenti,PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("contolloMandataria",contolloMandataria,PageContext.REQUEST_SCOPE);
      return null;
    }

    String selectTipologiaImpr = "select tipimp from impr where codimp = ? ";
    String selectRaggruppamento = "select coddic from ragimp where codime9 = ? ";
    String selectRagdet = "select coddic from ragdet where codimP = ? and ngara=?";
    List listaImprRaggruppamento = null;
    List listaDitteConosorzio = null;
    String selectNoteAvvisi = "select count(*) from g_noteavvisi where noteprg='PG' and noteent='IMPR'" +
    		" and statonota = 1 and notekey1 = ? ";
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    String selectRagimp ="select count(codime9) from ragimp where codime9=?";
    String selectImpman ="select count(impman) from ragimp where codime9=? and impman='1'";

    try {
      Long tipologiaImpr = (Long) sqlManager.getObject(selectTipologiaImpr, new Object[] {dittao});
      numNoteAvvisi = (Long) sqlManager.getObject(selectNoteAvvisi, new Object[] {dittao});
      if (tipologiaImpr == null || (!new Long(3).equals(tipologiaImpr) && !new Long(10).equals(tipologiaImpr)
          && !new Long(2).equals(tipologiaImpr) && !new Long(11).equals(tipologiaImpr))) {
        if (numNoteAvvisi > 0) {
          flagNoteAvvisi = "1";
        }
      }else if(new Long(3).equals(tipologiaImpr) || new Long(10).equals(tipologiaImpr)){
        //RT
        if (numNoteAvvisi > 0) {
          //Sto considerando anche la nota della RT stessa
          flagNoteAvvisi = "2";
        }
        listaImprRaggruppamento = sqlManager.getListVector(selectRaggruppamento, new Object[] {dittao});
        for (int i = 0; i < listaImprRaggruppamento.size(); i++) {
          Vector tmp = (Vector) listaImprRaggruppamento.get(i);
          String tmpDittao = ((JdbcParametro) tmp.get(0)).getStringValue();
          numNoteAvvisi = (Long) sqlManager.getObject(selectNoteAvvisi, new Object[] {tmpDittao});
          if (numNoteAvvisi > 0) {
            flagNoteAvvisi = "2";
            break;
          }
        }
        //Si deve controllare che vi siano almeno due componenti del raggruppamento e che sia presente la mandataria
        Long conteggioComponenti=(Long)sqlManager.getObject(selectRagimp, new Object[]{dittao});
        if(conteggioComponenti==null || (conteggioComponenti!=null && conteggioComponenti.longValue()<2) ){
          flagComponenti="1";
        }
        if((conteggioComponenti!=null && conteggioComponenti.longValue()>0)){
          Long contaggioManadataria=(Long)sqlManager.getObject(selectImpman, new Object[]{dittao});
          if(contaggioManadataria==null || (contaggioManadataria!=null && contaggioManadataria.longValue()<1)){
            contolloMandataria="1";
          }
        }

      }else{
        //Consorzi
        if (numNoteAvvisi > 0) {
          flagNoteAvvisi = "3";
        }
        listaDitteConosorzio = sqlManager.getListVector(selectRagdet, new Object[] {dittao,ngara});
        for (int i = 0; i < listaDitteConosorzio.size(); i++) {
          Vector tmp = (Vector) listaDitteConosorzio.get(i);
          String tmpDittao = ((JdbcParametro) tmp.get(0)).getStringValue();
          numNoteAvvisi = (Long) sqlManager.getObject(selectNoteAvvisi, new Object[] {tmpDittao});
          if (numNoteAvvisi > 0) {
            flagNoteAvvisi = "3";
            break;
          }
        }
      }

    } catch (SQLException e) {
      throw new JspException("Errore nella lettura di G_NOTEAVVISI)", e);
    }

    pageContext.setAttribute("controlloNoteAvvisi",flagNoteAvvisi,PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("controlloComponenti",flagComponenti,PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("contolloMandataria",contolloMandataria,PageContext.REQUEST_SCOPE);
    return null;
  }

}
