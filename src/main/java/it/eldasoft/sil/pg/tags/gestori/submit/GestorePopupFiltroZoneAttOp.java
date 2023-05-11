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

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard che si occupa di preparare la condizione di filtro
 * su zone attivita da aggiungere alla pagina gare-popup-selOpEconomici.jsp
 *
 * @author Cristian.Febas
 */
public class GestorePopupFiltroZoneAttOp extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "G1FILTRIELE";
  }

  public GestorePopupFiltroZoneAttOp() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupFiltroZoneAttOp(boolean isGestoreStandard) {
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

    String filtroZoneAttComplessivo = "";
    String[] arrayZoneAttScelte = new String[1];
    String[] arrayAllReg = new String[1];
    String elencoIdZoneAttivita = "";       //Contiene tutti gli ID delle zone di attivita

    arrayZoneAttScelte = this.getRequest().getParameterValues("regioni");
    arrayAllReg = this.getRequest().getParameterValues("allreg");
    if(arrayAllReg != null){
      //eventualmente un ulteriore controllo sul primo elemento dell'array
      filtroZoneAttComplessivo = " and (exists(select * from impr where codice = impr.codimp and impr.zoneat = '11111111111111111111' and (tipimp not in (3,10) or tipimp is null))"
          + " or (exists(select * from impr where codice = codimp  and tipimp in (3,10) and "
          + "exists (select impr.codimp from impr where impr.codimp in (select ragimp.coddic from ragimp where ragimp.codime9=codice) and impr.zoneat = '11111111111111111111'))))";
      elencoIdZoneAttivita = "ALL";
    }else{
      if (arrayZoneAttScelte != null && arrayZoneAttScelte.length > 0) {
        String filtroZoneAtt = "exists(select * from impr where codice = codimp and (tipimp not in (3,10) or tipimp is null) ";
        //Nel caso di RT si devono cercare le zone attive fra le imprese componenti
        String filtroZoneAttRT = "(exists(select * from impr where codice = codimp  and tipimp in (3,10) " +
            " and exists (select impr.codimp from impr where impr.codimp in (select ragimp.coddic from ragimp where ragimp.codime9=codice)";
        String filtro="";
        for (int i = 0; i < arrayZoneAttScelte.length; i++) {

          String i_pos = arrayZoneAttScelte[i];
          i_pos = UtilityStringhe.convertiNullInStringaVuota(i_pos);
          if(!"".equals(i_pos)){
            int pos = Integer.parseInt(i_pos);
            pos++;

            if(!"".equals(elencoIdZoneAttivita)){
              elencoIdZoneAttivita+=",";
            }
            elencoIdZoneAttivita+=i_pos;

            //poi differenziare il SUBSTRING per i vari DB
            //filtroZoneAtt =filtroZoneAtt + " and substr(zoneat," + String.valueOf(pos) + ",1)='1'";
            filtro = this.sqlManager.getDBFunction("substr", new String[] { "impr.zoneat", String.valueOf(pos), "1" }) + "='1'";
            filtroZoneAtt += " and " + filtro;
            filtroZoneAttRT += " and " + filtro;
          }


        }
        filtroZoneAtt += ")";
        filtroZoneAttRT += ")))";
        filtroZoneAttComplessivo =" and (" + filtroZoneAtt + " or " + filtroZoneAttRT + ")";
      }
    }

    HttpSession sessione = this.getRequest().getSession();
    sessione.setAttribute("filtroZoneAtt", filtroZoneAttComplessivo);
    sessione.setAttribute("elencoIdZoneAttivita", elencoIdZoneAttivita);

    this.getRequest().setAttribute("RISULTATO", "OK");


  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub

  }

}