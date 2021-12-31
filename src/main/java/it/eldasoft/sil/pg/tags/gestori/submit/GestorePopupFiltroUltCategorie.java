/*
 * Created on 22/11/11
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

import javax.servlet.http.HttpSession;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard che si occupa di preparare la condizione di filtro
 * da aggiungere alla pagina gare-popup-selOpEconomici.jsp
 *
 * @author Marcello Caminiti
 */
public class GestorePopupFiltroUltCategorie extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "V_GARE_CATEGORIE";
  }

  public GestorePopupFiltroUltCategorie() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupFiltroUltCategorie(boolean isGestoreStandard) {
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
    String filtro = null;
    String[] listaCategorieSelezionate = this.getRequest().getParameterValues("keys");
    String elencoUlterioriCategorie=null;       //Contiene tutti i codici delle ulteriori categorie
    String numclaCatPrevalente = this.getRequest().getParameter("numclaCatPrev");
    String elencoNumcla="";                     //Contiene la classisifica di tutte le categorie, inclusa la prevalente
    String elencoTiplavgUltCategorie=null;      //Contiene il tiplavg di tutte le ulteriori categorie
    String categoriaPrev = this.getRequest().getParameter("categoriaPrev");
    String prevalenteSelezionata="no";
    String garaElenco = this.getRequest().getParameter("garaElenco");
    String criterioRotazione = this.getRequest().getParameter("criterioRotazione");
    String stazioneAppaltante= this.getRequest().getParameter("stazioneAppaltante");
    String entita="V_DITTE_ELECAT";
    if("8".equals(criterioRotazione) || "9".equals(criterioRotazione))
      entita="V_DITTE_ELECAT_SA";

    elencoNumcla+= numclaCatPrevalente;
    if(listaCategorieSelezionate!= null && listaCategorieSelezionate.length>0){

      String classificaString=null;
      String codiceCategoria=null;
      String indiceRigaString=null;
      Long tiplavg=null;

      int indiceRiga=0;
      Long classifica=null;
      boolean inseritoFiltroCatPrevDaUlt= false;

      filtro="";
      elencoUlterioriCategorie="";
      elencoTiplavgUltCategorie="";
      for (int i = 0; i < listaCategorieSelezionate.length; i++) {
        classifica=null;
        classificaString="";
        tiplavg=null;

        String datiCategoria[]= listaCategorieSelezionate[i].split(";");
        codiceCategoria = datiCategoria[0];
        indiceRigaString = datiCategoria[1];

        indiceRiga = Integer.parseInt(indiceRigaString) + 1;
        DataColumnContainer dataColumnContainerDiRiga = new DataColumnContainer(
            dataColumnContainer.getColumnsBySuffix("_" + indiceRiga, false));
        if(dataColumnContainerDiRiga.isColumn("V_GARE_CATEGORIE.NUMCLA")){
          classifica = dataColumnContainerDiRiga.getLong("V_GARE_CATEGORIE.NUMCLA");
          if(classifica!=null)
            classificaString = classifica.toString();
        }

        if(codiceCategoria!= null && categoriaPrev!= null && categoriaPrev.equals(codiceCategoria)){
          prevalenteSelezionata="si";
          /*
          filtro=" and V_DITTE_ELECAT.CODCAT = '" + categoriaPrev + "'";
          if(classificaString != null && !"".equals(classificaString)){
            filtro += " and (V_DITTE_ELECAT.NUMCLASS = " + classificaString + " or V_DITTE_ELECAT.NUMCLASS is null)";
          }else{
            filtro += " and (V_DITTE_ELECAT.NUMCLASS = (select min(a.NUMCLASS) from V_DITTE_ELECAT a where a.GARA = '" + garaElenco + "' and a.CODCAT = '" + categoriaPrev + "'";
            filtro += " and a.codice = V_DITTE_ELECAT.codice) or V_DITTE_ELECAT.NUMCLASS is null)";
          }
          */
          continue;
        }


        if(dataColumnContainerDiRiga.isColumn("V_GARE_CATEGORIE.TIPLAVG")){
          tiplavg = dataColumnContainerDiRiga.getLong("V_GARE_CATEGORIE.TIPLAVG");
        }

        if("no".equals(prevalenteSelezionata) && !inseritoFiltroCatPrevDaUlt){
          filtro=" " + entita + ".GARA = '" + garaElenco + "' and " + entita + ".CODCAT = '" + codiceCategoria + "'";
          if("V_DITTE_ELECAT_SA".equals(entita))
            filtro += " and " + entita + ".CENINT = '" + stazioneAppaltante + "'";
          if(classificaString != null && !"".equals(classificaString)){
            filtro += " and (" + entita + ".NUMCLASS = " + classificaString + " or " +entita + ".NUMCLASS is null)";
          }else{
            filtro += " and (" +entita + ".NUMCLASS = (select min(a.NUMCLASS) from " + entita + " a where a.GARA = '" + garaElenco + "' and a.CODCAT = '" + codiceCategoria + "'";
            if("V_DITTE_ELECAT_SA".equals(entita))
              filtro += " and a.CENINT = '" + stazioneAppaltante + "'";
            filtro += " and a.codice = " + entita + ".codice) or " + entita +".NUMCLASS is null)";
            classificaString=" ";
          }
          inseritoFiltroCatPrevDaUlt = true;
        }else{
          filtro+= " and exists(select * from " + entita + " a where a.codcat='" + codiceCategoria + "' and a.codice=" + entita + ".codice " +
              "and a.gara=" + entita + ".gara";
          if("V_DITTE_ELECAT_SA".equals(entita))
            filtro += " and a.CENINT = '" + stazioneAppaltante + "'";
          if(classificaString != null && !"".equals(classificaString)){
            filtro+= " and ((a.INFNUMCLASS <= " + classificaString + " or a.INFNUMCLASS is null) and (a.SUPNUMCLASS >= " + classificaString + " or a.SUPNUMCLASS is null))";
          }else
            classificaString=" ";
          filtro+=")";
        }

        elencoNumcla+="," + classificaString;

        if(!"".equals(elencoUlterioriCategorie))
          elencoUlterioriCategorie+=",";
        elencoUlterioriCategorie+=codiceCategoria;

        if(!"".equals(elencoTiplavgUltCategorie))
          elencoTiplavgUltCategorie+=",";
        elencoTiplavgUltCategorie+=tiplavg.toString();
      }


    }

    //Carico in sessione il filtro e altre informazioni.
    //I valori in sessione vengono sbiancati in GestioneFasiRicezioneFunction.java
    //cioè ogni volta che si accede alla pagina delle fasi ricezione
    HttpSession sessione = this.getRequest().getSession();
    sessione.setAttribute("filtro", filtro);
    sessione.setAttribute("elencoUlterioriCategorie", elencoUlterioriCategorie);
    sessione.setAttribute("elencoNumcla", elencoNumcla);
    sessione.setAttribute("elencoTiplavgUltCategorie", elencoTiplavgUltCategorie);
    sessione.setAttribute("prevalenteSelezionata",prevalenteSelezionata);
    this.getRequest().setAttribute("RISULTATO", "OK");
    }catch (GestoreException e){
      HttpSession sessione = this.getRequest().getSession();
      sessione.setAttribute("filtro", null);
      sessione.setAttribute("filtroSpecifico", null);
      sessione.setAttribute("elencoUlterioriCategorie", null);
      sessione.setAttribute("elencoIdFiltriSpecifici", null);
      sessione.setAttribute("elencoMsgFiltriSpecifici", null);
      sessione.setAttribute("elencoNumcla", null);
      sessione.setAttribute("elencoTiplavgUltCategorie", null);
      sessione.setAttribute("prevalenteSelezionata", null);
      throw e;
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