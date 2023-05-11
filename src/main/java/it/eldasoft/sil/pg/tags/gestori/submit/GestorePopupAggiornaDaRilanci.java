/*
 * Created on 12/12/18
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard che si occupa della gestione dell'aggiornamento dell'offerta
 * della gara a partire dal rilancio selezionato
 *
 * @author Marcello Caminiti
 */
public class GestorePopupAggiornaDaRilanci extends GestoreFasiRicezione {

  static Logger               logger         = Logger.getLogger(GestorePopupAggiornaDaRilanci.class);

  @Override
  public String getEntita() {
    return "GARE";
  }

  public GestorePopupAggiornaDaRilanci() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupAggiornaDaRilanci(boolean isGestoreStandard) {
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

    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);

    String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String ngaraRilancio=UtilityStruts.getParametroString(this.getRequest(),"garaRilancioSelezionata");
    String modlicg=UtilityStruts.getParametroString(this.getRequest(),"modlicg");


    //variabili per tracciatura eventi
    //String oggEvento = ngara;
    String oggEvento = ngara;
    String codEvento = "GA_AGGIORNA_RILANCIO";
    //String descrEvento = "Inserimento ditte in gara mediante selezione da elenco operatori o catalogo (" + ngaraElenco + ")";
    String descrEvento = "Aggiorna offerte economiche in seguito a rilancio (cod.gara rilancio " +  ngaraRilancio + ")";
    String messageKey = null;
    int livEvento = 3;
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");

    try{
      //Controlli preliminari

      //Verificare se il rilancio è già stato selezionato precedentemente
      String select="select numril from garilanci where ngara=? and ngararil=?";
      Long numeroRilanci = (Long)this.sqlManager.getObject(select, new Object[]{ngara, ngaraRilancio});
      if(numeroRilanci!=null && numeroRilanci.longValue()>0){
        errMsgEvento = "La gara di rilancio selezionata risulta già elaborata.";
        this.getRequest().setAttribute("RISULTATO", "NOK");
        this.getRequest().setAttribute("mgsErr", errMsgEvento);
        return;
      }

      //Verifica che tutte le ditte del rilancio siano presenti in gara
      select="select count(d.dittao) from ditg d where d.ngara5=? and d.ammgar='1' and d.dittao not in(select d1.dittao from ditg d1 where d1.ngara5=?)";
      Long numDitteNonCorrispondenti = (Long)this.sqlManager.getObject(select, new Object[]{ngaraRilancio,ngara});
      if(numDitteNonCorrispondenti!=null && numDitteNonCorrispondenti.longValue()>0){
        errMsgEvento = "Nella gara di rilancio selezionata risultano partecipare ditte non presenti nella gara corrente.";
        this.getRequest().setAttribute("RISULTATO", "NOK");
        this.getRequest().setAttribute("mgsErr", errMsgEvento);
        return;
      }

      //Verifica che non vi siano buste dell'offerta economica ancora da elaborare
      select="select count(idcom) from w_invcom where idprg='PA' and comkey2=? and comtipo='FS11C' and comstato in (5,7)";
      Long numeroBusteEconomiche=(Long)this.sqlManager.getObject(select, new Object[]{ngaraRilancio});
      if(numeroBusteEconomiche!=null && numeroBusteEconomiche.longValue()>0){
        errMsgEvento = "Nella gara di rilancio selezionata deve essere completata l'acquisizione delle buste economiche.";
        this.getRequest().setAttribute("RISULTATO", "NOK");
        this.getRequest().setAttribute("mgsErr", errMsgEvento);
        return;
      }

      //Verifica che non vi siano ditte con ammgar non valorizzato
      select="select count(d.dittao) from ditg d where d.ngara5=? and (ammgar is null or ammgar='')";
      Long numDitteAmmgarNonValorizzato = (Long)this.sqlManager.getObject(select, new Object[]{ngaraRilancio});
      if(numDitteAmmgarNonValorizzato!=null && numDitteAmmgarNonValorizzato.longValue()>0){
        errMsgEvento = "Nella gara di rilancio selezionata non è stato valorizzato lo stato di ammissione per tutte le ditte in gara.";
        this.getRequest().setAttribute("RISULTATO", "NOK");
        this.getRequest().setAttribute("mgsErr", errMsgEvento);
        return;
      }


      String insert="insert into garilanci(id, ngara, ngararil, numril, dittao, ribauo, impoff, dataoraagg) values(?,?,?,?,?,?,?,?)";

      //Ciclo sulle ditte
      List<?> listaDitte=this.sqlManager.getListVector("SELECT dittao, ribauo, impoff FROM ditg where ngara5=?", new Object[]{ngara});
      if(listaDitte!=null && listaDitte.size()>0){
        Long numril = null;
        String dittao=null;
        Double ribauo=null;
        Double impoff=null;
        Long idG1cridef = null;
        Long idRilancio = null;
        Long formato = null;
        Double valnum=null;
        Double ribauoIniziale=null;

        if("6".equals(modlicg)){
          idG1cridef = (Long)this.sqlManager.getObject("select id from g1cridef where ngara=? and formato=51", new Object[]{ngara});
          if(idG1cridef==null){
            idG1cridef = (Long)this.sqlManager.getObject("select id from g1cridef where ngara=? and formato=50", new Object[]{ngara});
            if(idG1cridef==null){
              idG1cridef = (Long)this.sqlManager.getObject("select id from g1cridef where ngara=? and formato=52", new Object[]{ngara});
              formato=new Long(52);
            }else
              formato=new Long(50);
          }else
            formato=new Long(51);
        }

        for(int i=0;i<listaDitte.size();i++){
          dittao = SqlManager.getValueFromVectorParam(listaDitte.get(i), 0).stringValue();

          Vector datiRilancio = this.sqlManager.getVector("SELECT ribauo, impoff FROM ditg where ngara5=? and dittao=? and ammgar='1'", new Object[]{ngaraRilancio,dittao});
          if(datiRilancio!=null && datiRilancio.size()>0){
            Long numrilIniziale=(Long)this.sqlManager.getObject("select max(numril) from garilanci where ngara=? and dittao=?", new Object[]{ngara, dittao});
            ribauo=null;
            if(numrilIniziale==null){
              numrilIniziale= new Long(-1);
              numril = new Long(1);
            }else{
              numril = new Long(numrilIniziale.longValue() + 1);
            }

            //Storicizzazione offerta iniziale
            if(numrilIniziale.longValue()==-1){
              if("6".equals(modlicg) && formato.longValue()==51){
                ribauo = (Double)this.sqlManager.getObject("select valnum from g1crival where idcridef=? and ngara=? and dittao=?", new Object[]{idG1cridef,ngara,dittao});
                ribauoIniziale = new Double(ribauo.doubleValue() * -1);
              }else if(!"6".equals(modlicg)){
                ribauo = SqlManager.getValueFromVectorParam(listaDitte.get(i), 1).doubleValue();
                ribauoIniziale = new Double(ribauo.doubleValue());
              }
              impoff = SqlManager.getValueFromVectorParam(listaDitte.get(i), 2).doubleValue();
              idRilancio = new Long(genChiaviManager.getNextId("GARILANCI"));
              this.sqlManager.update(insert, new Object[]{idRilancio,ngara,null,numrilIniziale,dittao,ribauoIniziale,impoff,null});
            }

            //Storicizzazione rilancio
            ribauo = SqlManager.getValueFromVectorParam(datiRilancio, 0).doubleValue();
            impoff = SqlManager.getValueFromVectorParam(datiRilancio, 1).doubleValue();
            idRilancio = new Long(genChiaviManager.getNextId("GARILANCI"));
            this.sqlManager.update(insert, new Object[]{idRilancio,ngara,ngaraRilancio,numril,dittao,ribauo,impoff,new Timestamp(System.currentTimeMillis())});

            Double riboepv=null;
            //Aggiornamento offerta
            if("6".equals(modlicg)){
              if(formato!=null && (formato.longValue()==50 || (formato.longValue()==52)))
                valnum = impoff;
              else if(formato!=null && formato.longValue()==51){
                valnum = ribauo;
                valnum = new Double (valnum.doubleValue() * -1);
                riboepv=ribauo;
              }
              this.sqlManager.update("update g1crival set valnum=?, coeffi=null, punteg=null  where idcridef=? and ngara=? and dittao=?", new Object[]{valnum,idG1cridef,ngara,dittao});
              ribauo=null;
            }
            this.sqlManager.update("update ditg set ribauo=?, impoff=?, isrilancio=?, riboepv = ?  where ngara5=? and dittao=?", new Object[]{ribauo,impoff,"1",riboepv, ngara,dittao});

          }
        }
      }

      //best case
      livEvento = 1;
      errMsgEvento="";
      this.getRequest().setAttribute("RISULTATO", "OK");
    }catch(SQLException e){
      errMsgEvento = e.getMessage();
      throw new GestoreException("Errore nell'aggiornamento della gara " + ngara + " dal rilancio " + ngaraRilancio,null, e);

    }finally{
      //Tracciatura eventi
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(oggEvento);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descrEvento);
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
      }
    }


  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

}