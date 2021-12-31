/*
 * Created on 02/12/11
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
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;
/**
 * Gestore non standard che si occupa di gestire la funzionalità
 * per riassegnare il numero d'ordine delle ditte
 *
 * @author Marcello Caminiti
 */
public class GestoreRiassegnaNumOrdine extends AbstractGestoreEntita {


  @Override
  public String getEntita() {
    return "DITG";
  }

  public GestoreRiassegnaNumOrdine() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestoreRiassegnaNumOrdine(boolean isGestoreStandard) {
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

    String  ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String  modalitaRiass = UtilityStruts.getParametroString(this.getRequest(),"modalitaRiass");
    String paginaAttiva = UtilityStruts.getParametroString(this.getRequest(),"WIZARD_PAGINA_ATTIVA");
    String isProceduraAggiudicazioneAperta = UtilityStruts.getParametroString(this.getRequest(),"isProceduraAggiudicazioneAperta");
    String isGaraLottiConOffertaUnica = UtilityStruts.getParametroString(this.getRequest(),"isGaraLottiConOffertaUnica");
    String isDitteConcorrenti = UtilityStruts.getParametroString(this.getRequest(),"isDitteConcorrenti");
    int modalitaRiassegnamento=0;

    if(modalitaRiass!=null && !"".equals(modalitaRiass))
      modalitaRiassegnamento = Integer.parseInt(modalitaRiass);

    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        this.getServletContext(), PgManagerEst1.class);

    try {
      String codgar = (String) this.sqlManager.getObject("select codgar1 from gare where ngara=?", new Object[]{ngara});
      //la variabile modalitaRiassegnamento può assumere i seguenti valori:
      //1 -> Ragione sociale
      //2 -> Data e ora presentazione domanda di partecipazione
      //3 -> Numero protocollo presentazione domanda di partecipazione
      //4 -> Data e ora presentazione offerta
      //5 -> Numero protocollo presentazione offerta

      /*
      String isGaraLottoUnico ="false";
      if (codgar.startsWith("$"))
        isGaraLottoUnico = "true";

      String campoChiave="ngara5";
      if("true".equals(isGaraLottiConOffertaUnica))
        campoChiave="codgar5";


      switch(modalitaRiassegnamento){
        case 1:
          select="select dittao,invoff from ditg,impr where " + campoChiave + " = ? and codimp = dittao ";
          ordinamento=" order by IMPR.NOMEST, DITG.NPROGG";
          break;
        case 2:
          select="select dittao,invoff,coalesce(oradom,'24:00') as oradom_1 from ditg where " + campoChiave + " = ? ";
          ordinamento=" order by DRICIND,oradom_1, NPROGG";
          break;
        case 3:
          if("ORA".equals(db)){
            select="select dittao,invoff,to_number(nvl2(LENGTH (TRIM (TRANSLATE (nprdom, ' +-.0123456789', ' ') ) ) ,null,nprdom)) as nprdom_numerico from ditg where " + campoChiave + " = ? ";
            ordinamento = " order by nprdom_numerico, NPROGG";
          }else if("MSQ".equals(db)){
            select="select dittao,invoff, CONVERT( numeric ,CASE when isnumeric(nprdom)=1 then nprdom else null end) as nprdom_numerico from ditg where " + campoChiave + " = ? ";
            ordinamento=" order by nprdom_numerico, NPROGG";
          }else if("POS".equals(db)){
            select="select dittao,invoff, CAST(nullif(nprdom, '') AS integer) as nprdom_numerico from ditg where " + campoChiave + " = ? ";
            ordinamento=" order by nprdom_numerico, NPROGG";
          }
          break;
        case 4:
          select="select dittao,invoff,coalesce(oraoff,'24:00') as oraoff_1 from ditg where " + campoChiave + " = ? ";
          ordinamento="order by DATOFF,oraoff_1, NUMORDPL";
          break;
        case 5:
          if("ORA".equals(db)){
            select="select dittao,invoff,to_number(nvl2(LENGTH (TRIM (TRANSLATE (NPROFF, ' +-.0123456789', ' ') ) ) ,null,NPROFF)) as nproff_numerico from ditg where " + campoChiave + " = ? ";
            ordinamento = " order by nproff_numerico, NUMORDPL";
          }
          else if("MSQ".equals(db)){
            select="select dittao,invoff, CONVERT( numeric ,CASE when isnumeric(NPROFF)=1 then nproff else null end) as nproff_numerico from ditg where " + campoChiave + " = ? ";
            ordinamento=" order by nproff_numerico, NPROGG";
          }else if("POS".equals(db)){
            select="select dittao,invoff, CAST(nullif(NPROFF, '') AS integer) as nproff_numerico from ditg where " + campoChiave + " = ? ";
            ordinamento=" order by nproff_numerico, NPROGG";
          }
          break;

      }

      if("true".equals(isGaraLottiConOffertaUnica))
        select+= " and ngara5=codgar5 ";

      if((paginaAttiva!=null && !"".equals(paginaAttiva) && Integer.parseInt(paginaAttiva)==GestioneFasiRicezioneFunction.FASE_RICEZIONE_PLICHI && (!"true".equals(isProceduraAggiudicazioneAperta))))
        select+= " and (invoff is null or not invoff = '2') ";

      select+= ordinamento;

      List listaCodiciDitte = this.sqlManager.getListVector(select, new Object[]{ngara});
      if(listaCodiciDitte!= null && listaCodiciDitte.size()>0){
        String update="";
        for(int i=0;i<listaCodiciDitte.size();i++){
          String codiceDitta = SqlManager.getValueFromVectorParam(listaCodiciDitte.get(i), 0).getStringValue();
          String invoff = SqlManager.getValueFromVectorParam(listaCodiciDitte.get(i), 1).getStringValue();
          //update="update ditg set numordpl = " + Integer.toString(i + 1);
          boolean daAggiornare = false;
          update="update ditg set ";
          if(invoff==null || !"2".equals(invoff)){
            update+= "numordpl =" + Integer.toString(i + 1);
            daAggiornare=true;
          }

          if("true".equals(isDitteConcorrenti) || (paginaAttiva!=null && !"".equals(paginaAttiva) && (Integer.parseInt(paginaAttiva)==GestioneFasiRicezioneFunction.FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE ||
              Integer.parseInt(paginaAttiva)==GestioneFasiRicezioneFunction.FASE_ELENCO_DITTE_INVITATE) || "true".equals(isProceduraAggiudicazioneAperta))){
            //update+= ", nprogg = " + Integer.toString(i + 1);
            if(daAggiornare)
              update+=",";
            update+= " nprogg = " + Integer.toString(i + 1);
            daAggiornare=true;

            if("true".equals(isGaraLottiConOffertaUnica) || "true".equals(isGaraLottoUnico)){
              this.sqlManager.update("update edit set nprogt = ? where codgar4 = ? and codime = ? ", new Object[]{new Long(i+1),codgar,codiceDitta});
            }
          }

          if(daAggiornare){
            update += " where " + campoChiave + "=? and dittao = ?";
            this.sqlManager.update(update, new Object[]{ngara,codiceDitta});
          }

        }
      }
      */
      pgManagerEst1.setNumeroOrdine(ngara, codgar, isGaraLottiConOffertaUnica, isProceduraAggiudicazioneAperta, isDitteConcorrenti, paginaAttiva, modalitaRiassegnamento);

    } catch (SQLException e) {
      throw new GestoreException("Errore nella riassegnazione del numero d'ordine della gara " + ngara, null, e);
    }


    //Se tutto è andato bene setto nel request il parametro riassegnamento = 1
    this.getRequest().setAttribute("riassegnamentoEseguito", "1");
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer dataColumnContainer)
      throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }




  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {


  }




}