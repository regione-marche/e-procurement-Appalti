/*
 * Created on 22/01/19
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard che si occupa di preparare la condizione di filtro
 * aggiuntivo/specifico da aggiungere alla pagina gare-popup-selOpEconomici.jsp
 *
 * @author Cristian.Febas
 */
public class GestorePopupFiltroSpecificoOp extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "G1FILTRIELE";
  }

  public GestorePopupFiltroSpecificoOp() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupFiltroSpecificoOp(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer
      dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {


  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer dataColumnContainer)
      throws GestoreException {

      try{
        String filtroSpecifico = null;
        String[] listaFiltriSpecificiSelezionati = this.getRequest().getParameterValues("keys");
        String elencoIdFiltriSpecifici=null;       //Contiene tutti gli ID dei filtri specifici
        String elencoMsgFiltriSpecifici=null;       //Contiene i messaggi di riferimento per i filtri specifici

        if(listaFiltriSpecificiSelezionati!= null && listaFiltriSpecificiSelezionati.length>0){
          filtroSpecifico="";
          elencoIdFiltriSpecifici = "";
          elencoMsgFiltriSpecifici = "";

          for (int i = 0; i < listaFiltriSpecificiSelezionati.length; i++) {

            String datiFiltriSpecifici[]= listaFiltriSpecificiSelezionati[i].split(";");
            String idFiltroSpecificoStr = datiFiltriSpecifici[0];
            Long idFiltroSpecifico = new Long(idFiltroSpecificoStr);

            String selectG1FILTRIELE="select tipoele,applicaele,queryfiltro,msgfiltro from g1filtriele where id=?";
            Vector datiFiltroSpecifico = sqlManager.getVector(selectG1FILTRIELE, new Object[]{idFiltroSpecifico});
              if (datiFiltroSpecifico != null && datiFiltroSpecifico.size() > 0) {
                String tipoele = (String) SqlManager.getValueFromVectorParam(datiFiltroSpecifico, 0).getValue();
                String applicaele = (String) SqlManager.getValueFromVectorParam(datiFiltroSpecifico, 1).getValue();
                String queryfiltro = (String) SqlManager.getValueFromVectorParam(datiFiltroSpecifico, 2).getValue();
                String msgfiltro = (String) SqlManager.getValueFromVectorParam(datiFiltroSpecifico, 3).getValue();
                filtroSpecifico+= " and " + queryfiltro;

                if(!"".equals(elencoIdFiltriSpecifici)){
                  elencoIdFiltriSpecifici+=",";
                  elencoMsgFiltriSpecifici+=",";
                }

                elencoIdFiltriSpecifici+=idFiltroSpecifico;
                elencoMsgFiltriSpecifici+=msgfiltro;
              }

          }//for

        }//if

        //Carico in sessione il filtro e altre informazioni.
        //I valori in sessione vengono sbiancati in GestioneFasiRicezioneFunction.java
        //cioè ogni volta che si accede alla pagina delle fasi ricezione
        HttpSession sessione = this.getRequest().getSession();
        sessione.setAttribute("filtroSpecifico", filtroSpecifico);
        sessione.setAttribute("elencoIdFiltriSpecifici", elencoIdFiltriSpecifici);
        sessione.setAttribute("elencoMsgFiltriSpecifici", elencoMsgFiltriSpecifici);
        this.getRequest().setAttribute("RISULTATO", "OK");
      }catch (SQLException e){
        HttpSession sessione = this.getRequest().getSession();
        sessione.setAttribute("filtro", null);
        sessione.setAttribute("filtroSpecifico", null);
        sessione.setAttribute("elencoUlterioriCategorie", null);
        sessione.setAttribute("elencoIdFiltriSpecifici", null);
        sessione.setAttribute("elencoMsgFiltriSpecifici", null);
        sessione.setAttribute("elencoNumcla", null);
        sessione.setAttribute("elencoTiplavgUltCategorie", null);
        sessione.setAttribute("prevalenteSelezionata", null);
      }
  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub

  }

}