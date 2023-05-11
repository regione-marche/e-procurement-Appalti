/*
 * Created on 07/05/2018
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.integrazioni.Art80Manager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore non standard per la richiesta massiva della verifica art.80 di tutti gli operatori
 *
 * @author M. Caminiti
 */
public class GestorePopupVerificaOperatoriArt80 extends AbstractGestoreEntita {

  public GestorePopupVerificaOperatoriArt80() {
    super(false);
  }

  @Override
  public String getEntita() {
    return "GARE";
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String statoAbilitazione = datiForm.getString("ABILITAZ");
    String status_service = UtilityStruts.getParametroString(this.getRequest(),"status_service");
    String esitoFunzione="OK";
    Art80Manager art80Manager = (Art80Manager) UtilitySpring.getBean("art80Manager", this.getServletContext(), Art80Manager.class);
    StringBuffer msg = new StringBuffer("Non e' possibile procedere perchè i seguenti operatori non hanno valorizzato il codice fiscale o la ragione sociale:<br><ul>");
    List<String> listaOperatoriArt80=new ArrayList<String>();
    int numVerificheEsitoPositivo=0;
    int numVerificheErrore=0;
    int numVerificheGiaInviate=0;
    long numTotaleOperatoriRichiesta=0;
    try {
      String codein=(String) this.getRequest().getSession().getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
      boolean art80Gateway=false;
      if(codein!=null && !"".equals(codein)  && "1".equals(ConfigManager.getValore("art80.ws.url.gateway")) && "1".equals(ConfigManager.getValore("art80.gateway.multiuffint")))
        art80Gateway=true;
      String selectStato="select dittao from ditg,impr where ngara5=? and dittao=codimp and abilitaz=? and art80_stato is null";
      Object par[] = null;
      if(art80Gateway){
        selectStato="select dittao from ditg where ngara5=? and abilitaz=? and dittao not in (select codimp  from art80 where codein=? and stato is not null)";
        par= new Object[]{ngara,new Long(statoAbilitazione),codein};
      }else{
        par= new Object[]{ngara,new Long(statoAbilitazione)};
      }

      List listaOperatori=this.sqlManager.getListVector(selectStato, par);
      if(listaOperatori!=null && listaOperatori.size()>0){
        String codiceDitta=null;

        int responseCode=0;
        List <Object> listaEsito=null;
        for (int i=0;i<listaOperatori.size();i++){
          codiceDitta=SqlManager.getValueFromVectorParam(listaOperatori.get(i), 0).stringValue();

          listaEsito = art80Manager.art80CreaOE(codiceDitta,codein,status_service);
          if(listaEsito!=null && listaEsito.size()>0){
            for(int j=0; j< listaEsito.size();j++){
              Object esito[] = (Object[])listaEsito.get(j);
              responseCode = ((Integer)esito[1]).intValue();
              if(responseCode==201)
                numVerificheEsitoPositivo++;
              else if(responseCode==409)
                numVerificheGiaInviate++;
              else
                numVerificheErrore++;
              numTotaleOperatoriRichiesta++;
            }
          }

        }
        this.getRequest().setAttribute("numVerificheEsitoPositivo", new Long(numVerificheEsitoPositivo));
        this.getRequest().setAttribute("numVerificheErrore", new Long(numVerificheErrore));
        this.getRequest().setAttribute("numVerificheGiaInviate", new Long(numVerificheGiaInviate));
      }else{
        esitoFunzione="NoOperatori";
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella richiesta di verifica operatori articolo 80", null,  e);
    }
    this.getRequest().setAttribute("esito", esitoFunzione);
    this.getRequest().setAttribute("numOperatoriRichiesta", numTotaleOperatoriRichiesta);

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
