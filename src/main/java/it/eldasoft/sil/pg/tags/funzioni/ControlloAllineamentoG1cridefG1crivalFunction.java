/*
 * Created on 10/07/17
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
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class ControlloAllineamentoG1cridefG1crivalFunction extends AbstractFunzioneTag {

  public ControlloAllineamentoG1cridefG1crivalFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];
    String nomimo = null;
    String esitoControllo = "ok";
    String tipo = (String) params[2];
    String sezione = (String) params[3];
    String selectNonCorrispondenzaG1CridefG1Crival="select count(c.id) from g1cridef c, goev g where c.ngara=? and g.ngara = c.ngara and g.necvan = c.necvan and g.tippar = ? and c.maxpun >0 ";
    if (!"".equals(sezione))
      selectNonCorrispondenzaG1CridefG1Crival += " and g.seztec = 1 ";
    selectNonCorrispondenzaG1CridefG1Crival += "and not exists (select v.id from g1crival v where v.idcridef=c.id and v.ngara=? and v.dittao=?)";
    String selectEsistenzaDettaglioManuale="select count(c.id) from g1cridef c, goev g where c.ngara=? and g.ngara = c.ngara and g.necvan = c.necvan and g.tippar = ? and c.maxpun >0 and (c.modpunti=1 or c.modpunti=3)";
    if (!"".equals(sezione))
      selectEsistenzaDettaglioManuale +=  " and g.seztec = 1 ";
    String selectEsistenzaDettaglioManualeG1Crival="select count(c.id) from g1cridef c, goev g where c.ngara=? and g.ngara = c.ngara and g.necvan = c.necvan and g.tippar = ? and c.maxpun >0 and (c.modpunti=1 or c.modpunti=3) ";
    if (!"".equals(sezione))
      selectEsistenzaDettaglioManualeG1Crival += " and g.seztec = 1 ";
    selectEsistenzaDettaglioManualeG1Crival += " and exists (select v.id from g1crival v where v.idcridef=c.id and v.ngara=? and v.dittao=? and v.punteg is null)";
    String selectEsistenzaDettaglioManualeCoeffNonValidi="select count(c.id) from g1cridef c, goev g where c.ngara=? and g.ngara = c.ngara and g.necvan = c.necvan and g.tippar = ? and c.maxpun >0 and (c.modpunti=1 or c.modpunti=3) ";
    if (!"".equals(sezione))
      selectEsistenzaDettaglioManualeCoeffNonValidi += " and g.seztec = 1 ";
    selectEsistenzaDettaglioManualeCoeffNonValidi += "and exists (select v.id from g1crival v where v.idcridef=c.id and v.ngara=? and v.dittao=? and (v.coeffi is not null and (v.coeffi>1 or v.coeffi<0)))";

    if (ngara != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      String select="select dittao,nomimo from ditg where ngara5=? and (fasgar is null or fasgar > ?) order by NUMORDPL asc,NOMIMO  asc";
      Object par[] = new Object[2];
      par[0]=ngara;
      if("1".equals(tipo)){
        par[1]=new Long(5);
      }else{
        par[1]=new Long(6);
      }
      try {
        //Si devono prendere in considerazione per i controlli solo le ditte non escluse nella fase corrente
        List listaDitteGara=sqlManager.getListVector(select, par);
        if(listaDitteGara!=null && listaDitteGara.size()>0){
          String ditta = null;

          Long conteggio=null;
          Long conteggioCriteriManuali=null;
          Long conteggioCorrispondenzaCriteriManuali=null;
          Long conteggioCoeffNonValidi=null;
          for(int i=0;i<listaDitteGara.size();i++){
            ditta = SqlManager.getValueFromVectorParam(listaDitteGara.get(i), 0).getStringValue();
            nomimo = SqlManager.getValueFromVectorParam(listaDitteGara.get(i), 1).getStringValue();
            //Si controlla inizialmente se vi sono delle occorrenze in G1CRIDEF con MAXPUN >0 per le quali non esiste
            //corrispondenza in G1CRIVAL
            conteggio = (Long) sqlManager.getObject(selectNonCorrispondenzaG1CridefG1Crival, new Object[] {ngara, new Long(tipo),ngara,ditta});
            if (conteggio != null && conteggio.longValue() > 0) {
              esitoControllo = "nok";
              break;
            }else{
              //Si controlla che nel caso di dettaglio manuale devono essere valorizzati coefficente e punteggio(basta controllare il punteggio)
              conteggioCriteriManuali = (Long) sqlManager.getObject(selectEsistenzaDettaglioManuale, new Object[] {ngara, new Long(tipo)});
              if(conteggioCriteriManuali != null && conteggioCriteriManuali.longValue() > 0){
                conteggioCorrispondenzaCriteriManuali = (Long) sqlManager.getObject(selectEsistenzaDettaglioManualeG1Crival, new Object[] {ngara, new Long(tipo), ngara, ditta});
                if (conteggioCorrispondenzaCriteriManuali != null && conteggioCorrispondenzaCriteriManuali.longValue() > 0){
                  esitoControllo = "nok";
                  break;
                }
                //si controlla che il coefficente ricada nell'intervallo [0;1]
                conteggioCoeffNonValidi=(Long) sqlManager.getObject(selectEsistenzaDettaglioManualeCoeffNonValidi, new Object[] {ngara, new Long(tipo), ngara, ditta});
                if (conteggioCoeffNonValidi != null && conteggioCoeffNonValidi.longValue() > 0){
                  esitoControllo = "nok-coeffNonValidi";
                  break;
                }
              }
            }
          }
        }
      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo sull'esistenza di occorrenze di G1CRIVAL associate a G1CRIDEF", e);
      }
    }
    pageContext.setAttribute("ragioneSocDitta",nomimo);
    return esitoControllo;
  }

}