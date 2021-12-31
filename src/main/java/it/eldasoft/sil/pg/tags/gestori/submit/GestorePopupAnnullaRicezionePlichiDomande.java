/*
 * Created on 13/07/17
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
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire l'annullamento
 * della ricezione plichi o domande di partecipazione
 *
 * @author Marcello Caminiti
 */
public class GestorePopupAnnullaRicezionePlichiDomande extends
    AbstractGestoreEntita {

  static Logger               logger         = Logger.getLogger(GestorePopupAnnullaRicezionePlichiDomande.class);

  public GestorePopupAnnullaRicezionePlichiDomande() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;


  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
  }

  @Override
  public String getEntita() {
    return "GARE";
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
    String tipo= UtilityStruts.getParametroString(this.getRequest(),"tipo");
    String iterga = UtilityStruts.getParametroString(this.getRequest(),"iterga");
    String genere = UtilityStruts.getParametroString(this.getRequest(),"genere");
    String codgar = UtilityStruts.getParametroString(this.getRequest(),"codgar");

    //variabili per tracciatura eventi
    int livEvento = 3;
    String codEvento = "GA_ANNULLA_RICEZIONE_PLICHI";
    String oggEvento = ngara;

    String descrEvento = "Annullamento ricezione plichi";
    if("2".equals(tipo)){
      codEvento = "GA_ANNULLA_RICEZIONE_DOMANDE";
      descrEvento = "Annullamento ricezione domande di partecipazione";
    }
    String errMsgEvento = "";





    try{


        if("1".equals(iterga) || "2".equals(tipo)){
          //Cancellazione occorrenze W_DOCDIG
          String sqlW_DOCDIG = "select w.idprg, iddocdig from w_docdig w,imprdocg i where codgar=? and w.idprg = i.idprg and iddocdig = iddocdg";
          errMsgEvento = this.deleteW_DOCDIG(sqlW_DOCDIG, codgar, errMsgEvento);

          //Cancellazione IMPRDOCG
          String sqlIMPRDOCG = "select ngara, codimp, norddoci, proveni from imprdocg" ;
          String whereIMPRDOCG = "codgar=?";
          errMsgEvento = this.deleteImprdocg(sqlIMPRDOCG, whereIMPRDOCG, codgar, errMsgEvento,true);

          //Cancellazione DITGAMMIS
          String sqlDITGAMMIS = "select ngara, dittao, fasgar from ditgammis";
          String whereDITGAMMIS = "codgar=?";
          errMsgEvento = this.deleteDitgammis(sqlDITGAMMIS, whereDITGAMMIS, codgar, errMsgEvento);

          //Cancellazione DITGSTATI
          String sqlDITGSTATI = "select ngara, dittao, fasgar, stepgar from ditgstati";
          String whereDITGSTATI = "codgar=?";
          errMsgEvento = this.deleteDitgstati(sqlDITGSTATI, whereDITGSTATI, codgar, errMsgEvento);

          //Cancellazione GARESTATI
          String sqlGARESTATI = "select ngara, fasgar, stepgar from garestati";
          String whereGARESTATI = "codgar=?";
          errMsgEvento = this.deleteGarestati(sqlGARESTATI, whereGARESTATI, codgar, errMsgEvento);

          //Cancellazione occorrenze DITGAVVAL
          String sqlDITGAVVAL = "select id from ditgavval where ngara=?";
          List<?> datiDITGAVVAL = this.sqlManager.getListVector(sqlDITGAVVAL, new Object[]{ngara});
          if(datiDITGAVVAL!=null && datiDITGAVVAL.size()>0){
            errMsgEvento +="Eliminate occ. in DITGAVVAL (id: ";
            Long id=null;
            for(int i=0;i<datiDITGAVVAL.size();i++){
              id = SqlManager.getValueFromVectorParam(datiDITGAVVAL.get(i), 0).longValue();
              errMsgEvento += id.toString();
              if(i < datiDITGAVVAL.size() - 1)
                errMsgEvento += ", ";

            }
            this.getGeneManager().deleteTabelle(new String[]{"DITGAVVAL"},"ngara = ?", new Object[]{ngara});
            errMsgEvento+=") \r\n";

          }

          //Cancellazione RAGDET
          String sqlRAGDET = "select codimp, coddic, numdic from ragdet";
          String whereRAGDET = "ngara=?";
          errMsgEvento = this.deleteRagdet(sqlRAGDET, whereRAGDET, ngara, errMsgEvento);

          //Cancellazione EDIT
          String sqlEDIT = "select codime from edit";
          String whereEDIT = "codgar4=?";
          errMsgEvento = this.deleteEdit(sqlEDIT, whereEDIT, codgar, errMsgEvento);

          //Cancellazione DITG
          String sqlDITG = "select ngara5,dittao from ditg";
          String whereDITG = "codgar5=?";
          errMsgEvento = this.deleteDITG(sqlDITG, whereDITG, codgar, errMsgEvento,true);

          //Aggiornamento fase gara
          errMsgEvento+=" Aggiornato FASGAR-STEPGAR in GARE (ngara = '" + ngara+ "')\r\n";
          if("1".equals(tipo))
            this.sqlManager.update("update gare set fasgar=?, stepgar=? where ngara=?", new Object[]{new Long(1), new Long(10), ngara});
          else
            this.sqlManager.update("update gare set fasgar=?, stepgar=? where ngara=?", new Object[]{new Long(-5), new Long(-50),ngara });

          //Aggiornamento stato comunicazioni
          errMsgEvento= this.upadteComunicazioni(codgar, ngara, errMsgEvento,tipo);

        }else{
          //Cancellazione occorrenze W_DOCDIG
          String sqlW_DOCDIG = "select w.idprg, iddocdig from w_docdig w,v_gare_docditta v where codgar=? and w.idprg = v.idprg and iddocdig = iddocdg and (v.busta <> 4 or v.busta is null)";
          errMsgEvento = this.deleteW_DOCDIG(sqlW_DOCDIG, codgar, errMsgEvento);

          //Cancellazione IMPRDOCG
          String sqlIMPRDOCG = "select i.ngara, i.codimp, i.norddoci, i.proveni from imprdocg i, v_gare_docditta v";
          String whereIMPRDOCG = "i.codgar=? and i.proveni=2  and i.codgar=v.codgar and i.ngara=v.ngara and i.codimp=v.codimp and i.norddoci=v.norddoci and i.proveni=v.proveni and (v.busta <> 4 or v.busta is null)";
          errMsgEvento = this.deleteImprdocg(sqlIMPRDOCG, whereIMPRDOCG, codgar, errMsgEvento,true);

          //Aggiornamento IMPRDOCG
          whereIMPRDOCG = "i.codgar=? and i.iddocdg is not null and i.proveni=1 and i.codgar=v.codgar and i.ngara=v.ngara and i.codimp=v.codimp and i.norddoci=v.norddoci and i.proveni=v.proveni and (v.busta <> 4 or v.busta is null)";
          errMsgEvento = this.deleteImprdocg(sqlIMPRDOCG, whereIMPRDOCG, codgar, errMsgEvento,false);
          this.sqlManager.update("update imprdocg set idprg=null, iddocdg = null, datarilascio = null, orarilascio = null, datalettura = null, "
              + "sysconlet = null, situazdoci = null where codgar=? and iddocdg is not null and proveni=1 and exists ( select * from v_gare_docditta v "
              + " where v.codgar= imprdocg.codgar and v.ngara=imprdocg.ngara and v.codimp=imprdocg.codimp and v.norddoci=imprdocg.norddoci and v.proveni=imprdocg.proveni and (v.busta <> 4 or v.busta is null))", new Object[]{codgar});

          //Cancellazione DITGAMMIS
          String sqlDITGAMMIS = "select ngara, dittao, fasgar from ditgammis";
          String whereDITGAMMIS = "codgar=? and fasgar >= 1";
          errMsgEvento = this.deleteDitgammis(sqlDITGAMMIS, whereDITGAMMIS, codgar, errMsgEvento);

          //Cancellazione DITGSTATI
          String sqlDITGSTATI = "select ngara, dittao, fasgar, stepgar from ditgstati";
          String whereDITGSTATI = "codgar=? and fasgar >= 1";
          errMsgEvento = this.deleteDitgstati(sqlDITGSTATI, whereDITGSTATI, codgar, errMsgEvento);

          //Cancellazione GARESTATI
          String sqlGARESTATI = "select ngara, fasgar, stepgar from garestati";
          String whereGARESTATI = "codgar=? and fasgar >= 1";
          errMsgEvento = this.deleteGarestati(sqlGARESTATI, whereGARESTATI, codgar, errMsgEvento);

          //Cancellazione RAGDET
          String sqlRAGDET = "select codimp, coddic, numdic from ragdet";
          String whereRAGDET = "ngara=?";
          errMsgEvento = this.deleteRagdet(sqlRAGDET, whereRAGDET, ngara, errMsgEvento);

          // cancellazione presentazione offerta mediante RT
          String sqlDITG = "select ngara5,dittao from ditg where codgar5=? and acquisizione = 5";
          List<?> datiDITG = this.sqlManager.getListVector(sqlDITG, new Object[]{codgar});
          if(datiDITG!=null && datiDITG.size()>0){
            String ngaraE=null;
            String dittao=null;
            errMsgEvento+="+Inizio eliminazione RT definite in presentazione offerta:\r\n";
            String sqlEDIT = "select codime from edit";
            String whereEDIT = "";
            for(int i=0;i<datiDITG.size();i++){
              ngaraE = SqlManager.getValueFromVectorParam(datiDITG.get(i), 0).getStringValue();
              dittao = SqlManager.getValueFromVectorParam(datiDITG.get(i), 1).getStringValue();

              //Cancellazione DITGAMMIS
              whereDITGAMMIS = "codgar=? and dittao='"+ dittao + "' and ngara = '" + ngaraE + "'";
              errMsgEvento = this.deleteDitgammis(sqlDITGAMMIS, whereDITGAMMIS, codgar, errMsgEvento);

              //Cancellazione DITGSTATI
              whereDITGSTATI = "codgar=? and dittao='"+ dittao + "' and ngara = '" + ngaraE + "'";
              errMsgEvento = this.deleteDitgstati(sqlDITGSTATI, whereDITGSTATI, codgar, errMsgEvento);

              //Cancellazione EDIT
              whereEDIT = "codgar4=? and codime = '"+ dittao + "'";
              errMsgEvento = this.deleteEdit(sqlEDIT, whereEDIT, codgar, errMsgEvento);

              //Cancellazione IMPRDOCG
              sqlIMPRDOCG = "select ngara, codimp, norddoci, proveni from imprdocg";
              whereIMPRDOCG = "codgar=? and codimp='"+ dittao + "' and ngara = '" + ngaraE + "'";
              errMsgEvento = this.deleteImprdocg(sqlIMPRDOCG, whereIMPRDOCG, codgar, errMsgEvento,true);

              //Cancellazione DITG
              errMsgEvento +="Eliminate occ. in DITG (codgar5-ngara5-dittao: " + codgar + "-" + ngaraE + "-" + dittao +") \r\n";
              this.getGeneManager().deleteTabelle(new String[]{"DITG"}," CODGAR5 = ?  and NGARA5=? and DITTAO = ?" , new Object[]{codgar,ngaraE,dittao});


            }
            errMsgEvento+="+Fine eliminazione RT definite in presentazione offerta\r\n";
          }

          //Aggiornamento DITG
          sqlDITG = "select ngara5,dittao from ditg";
          String whereDITG = "codgar5=? and invgar='1'";
          errMsgEvento = this.deleteDITG(sqlDITG, whereDITG, codgar, errMsgEvento,false);
          this.sqlManager.update("update ditg set RTOFFERTA=null, INVOFF = null, NPROFF = null, DPROFF = null, DATOFF = null, "
              + "ORAOFF = null, MEZOFF = null, MOTIES = null, ESTIMP = null, AMMINVERSA = null, FASGAR  = null, NUMORDPL = NPROGG  where codgar5=? and invgar = ?", new Object[]{codgar,"1"});
          if("3".equals(genere)){
            this.sqlManager.update("update ditg set AMMGAR =null where codgar5=? and invgar = ? and ngara5 != codgar5", new Object[]{codgar,"1"});
            if("2".equals(iterga) || "4".equals(iterga) )
              this.sqlManager.update("update ditg set AMMGAR =? where codgar5=? and invgar = ? and ngara5 = codgar5", new Object[]{"1",codgar,"1"});
            else
              this.sqlManager.update("update ditg set AMMGAR =null where codgar5=? and invgar = ? and ngara5 = codgar5", new Object[]{codgar,"1"});
          }else{
            if("2".equals(iterga) || "4".equals(iterga) )
              this.sqlManager.update("update ditg set AMMGAR =? where codgar5=? and invgar = ? ", new Object[]{"1",codgar,"1"});
            else
              this.sqlManager.update("update ditg set AMMGAR =null where codgar5=? and invgar = ? ", new Object[]{codgar,"1"});
          }

          //Aggiornamento fase gara
          errMsgEvento+=" Aggiornato FASGAR-STEPGAR in GARE (ngara = '" + ngara+ "')\r\n";
          this.sqlManager.update("update gare set fasgar=?, stepgar=? where ngara=?", new Object[]{new Long(1), new Long(10),ngara});

          //Aggironamento comunicazioni
          errMsgEvento= this.upadteComunicazioni(codgar, ngara, errMsgEvento,tipo);
        }



      livEvento = 1;
      this.getRequest().setAttribute("operazioneEseguita", "1");



    } catch (Exception e) {
        livEvento = 3;
        this.getRequest().setAttribute("operazioneEseguita", "2");
        errMsgEvento = e.getMessage();
        throw new GestoreException("Si sono presentati degli errori durante le operazioni di annullamento", "annullaRicezionePlichiDomandePartecipazione",e);
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
          String messageKey = "errors.logEventi.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), le);
          this.getRequest().setAttribute("calcoloEseguito", "2");
        }
    }
}


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }


  /**
   * Viene effettuata la cancellazione delle occorrenze della Tabella W_DOCDIG
   * @param select
   * @param codgar
   * @param msg
   * @return
   * @throws SQLException
   * @throws GestoreException
   */
  private String deleteW_DOCDIG(String select, String codgar, String msg) throws SQLException, GestoreException{
    String msgRet=msg;
    List<?> datiW_DOCDIG = this.sqlManager.getListVector(select, new Object[]{codgar});
    if(datiW_DOCDIG!=null && datiW_DOCDIG.size()>0){
      msgRet +="Eliminato occ. in W_DOCDIG (idprg-iddocdig: ";
      String idprg=null;
      Long iddocdig=null;
      for(int i=0;i<datiW_DOCDIG.size();i++){
        idprg = SqlManager.getValueFromVectorParam(datiW_DOCDIG.get(i), 0).getStringValue();
        iddocdig = SqlManager.getValueFromVectorParam(datiW_DOCDIG.get(i), 1).longValue();
        msgRet += idprg + "-" + iddocdig.toString();
        if(i < datiW_DOCDIG.size() - 1)
          msgRet += ", ";
        this.getGeneManager().deleteTabelle(new String[]{"W_DOCDIG"},"IDPRG = ? and IDDOCDIG = ?", new Object[]{idprg, iddocdig});
      }
      msgRet+=") \r\n";

    }
    return msgRet;
  }

  /**
   *
   * @param select
   * @param where
   * @param codgar
   * @param msg
   * @param cancellazione
   * @return
   * @throws SQLException
   * @throws GestoreException
   */
  private String deleteImprdocg(String select, String where, String codgar, String msg, boolean cancellazione) throws SQLException, GestoreException{
    String msgRet=msg;
    List<?> datiIMPRDOCG = this.sqlManager.getListVector(select + " where " + where, new Object[]{codgar});
    if(datiIMPRDOCG!=null && datiIMPRDOCG.size()>0){
      if(cancellazione)
        msgRet +="Eliminato occ. in IMPRDOCG (codgar-ngara-codimp-norddoci-proveni: ";
      else
        msgRet +="Aggiornato occ. in IMPRDOCG (codgar-ngara-codimp-norddoci-proveni: ";
      String ngaraE=null;
      String codimp=null;
      Long norddoci = null;
      Long proveni = null;
      for(int i=0;i<datiIMPRDOCG.size();i++){
        ngaraE = SqlManager.getValueFromVectorParam(datiIMPRDOCG.get(i), 0).getStringValue();
        codimp= SqlManager.getValueFromVectorParam(datiIMPRDOCG.get(i), 1).getStringValue();
        norddoci = SqlManager.getValueFromVectorParam(datiIMPRDOCG.get(i), 2).longValue();
        proveni = SqlManager.getValueFromVectorParam(datiIMPRDOCG.get(i), 3).longValue();
        msgRet += codgar + "-" + ngaraE + "-" + codimp + "-" + norddoci.toString() + "-" + proveni.toString();
        if(i < datiIMPRDOCG.size() - 1)
          msgRet += ", ";
        if(cancellazione)
          this.getGeneManager().deleteTabelle(new String[]{"IMPRDOCG"},"codgar=? and ngara=? and codimp=? and norddoci=? and proveni=?", new Object[]{codgar,ngaraE,codimp,norddoci,proveni});
      }
      msgRet+=") \r\n";

    }
    return msgRet;
  }

  /**
   *
   * @param select
   * @param where
   * @param codgar
   * @param msg
   * @return
   * @throws SQLException
   * @throws GestoreException
   */
  private String deleteDitgammis(String select, String where, String codgar, String msg) throws SQLException, GestoreException{
    String msgRet=msg;
    List<?> datiDITGAMMIS = this.sqlManager.getListVector(select + " where " + where, new Object[]{codgar});
    if(datiDITGAMMIS!=null && datiDITGAMMIS.size()>0){
      msgRet +="Eliminato occ. in DITGAMMIS (codgar-ngara-dittao-fasgar: ";
      String ngaraE=null;
      String codimp=null;
      Long fasgar = null;
      for(int i=0;i<datiDITGAMMIS.size();i++){
        ngaraE = SqlManager.getValueFromVectorParam(datiDITGAMMIS.get(i), 0).getStringValue();
        codimp= SqlManager.getValueFromVectorParam(datiDITGAMMIS.get(i), 1).getStringValue();
        fasgar = SqlManager.getValueFromVectorParam(datiDITGAMMIS.get(i), 2).longValue();
        msgRet += codgar + "-" + ngaraE + "-" + codimp + "-" + fasgar.toString();
        if(i < datiDITGAMMIS.size() - 1)
          msgRet += ", ";
      }
      msgRet+=") \r\n";
      this.getGeneManager().deleteTabelle(new String[]{"DITGAMMIS"},where, new Object[]{codgar});
    }
    return msgRet;

  }

  /**
   *
   * @param select
   * @param where
   * @param codgar
   * @param msg
   * @return
   * @throws SQLException
   * @throws GestoreException
   */
  private String deleteDitgstati(String select, String where, String codgar, String msg) throws SQLException, GestoreException{
    String msgRet=msg;
    List<?> datiDITGSTATI = this.sqlManager.getListVector(select + " where " + where, new Object[]{codgar});
    if(datiDITGSTATI!=null && datiDITGSTATI.size()>0){
      msgRet +="Eliminato occ. in DITGSTATI (codgar-ngara-dittao-fasgar: ";
      String ngaraE=null;
      String dittao = null;
      Long fasgar = null;
      for(int i=0;i<datiDITGSTATI.size();i++){
        ngaraE = SqlManager.getValueFromVectorParam(datiDITGSTATI.get(i), 0).getStringValue();
        dittao = SqlManager.getValueFromVectorParam(datiDITGSTATI.get(i), 1).getStringValue();
        fasgar = SqlManager.getValueFromVectorParam(datiDITGSTATI.get(i), 2).longValue();
        msgRet += codgar + "-" + ngaraE + "-" + dittao + "-" + fasgar.toString();
        if(i < datiDITGSTATI.size() - 1)
          msgRet += ", ";
      }
      msgRet+=") \r\n";
      this.getGeneManager().deleteTabelle(new String[]{"DITGSTATI"},where, new Object[]{codgar});
    }
    return msgRet;

  }

  /**
 *
 * @param select
 * @param where
 * @param codgar
 * @param msg
 * @return
 * @throws SQLException
 * @throws GestoreException
 */
  private String deleteGarestati(String select, String where, String codgar, String msg) throws SQLException, GestoreException{
    String msgRet=msg;
    List<?> datiGARESTATI = this.sqlManager.getListVector(select + " where " + where, new Object[]{codgar});
    if(datiGARESTATI!=null && datiGARESTATI.size()>0){
      msgRet +="Eliminato occ. in GARESTATI (codgar-ngara-fasgar-stepgar: ";
      String ngaraE=null;
      Long fasgar = null;
      Long stepgar = null;
      for(int i=0;i<datiGARESTATI.size();i++){
        ngaraE = SqlManager.getValueFromVectorParam(datiGARESTATI.get(i), 0).getStringValue();
        fasgar = SqlManager.getValueFromVectorParam(datiGARESTATI.get(i), 1).longValue();
        stepgar = SqlManager.getValueFromVectorParam(datiGARESTATI.get(i), 2).longValue();
        msgRet += codgar + "-" + ngaraE + "-" + fasgar.toString() + "-" + stepgar.toString();
        if(i < datiGARESTATI.size() - 1)
          msgRet += ", ";
      }
      msgRet+=") \r\n";
      this.getGeneManager().deleteTabelle(new String[]{"GARESTATI"},where, new Object[]{codgar});
    }

    return msgRet;
  }

  /**
   *
   * @param select
   * @param where
   * @param codgar
   * @param msg
   * @return
   * @throws SQLException
   * @throws GestoreException
   */
  private String deleteEdit(String select, String where, String codgar, String msg) throws SQLException, GestoreException{
    String msgRet=msg;
    List<?> datiEDIT = this.sqlManager.getListVector(select + " where " + where, new Object[]{codgar});
    if(datiEDIT!=null && datiEDIT.size()>0){
      msgRet +="Eliminato occ. in EDIT (codgar4-codime: ";
      String codime=null;
      for(int i=0;i<datiEDIT.size();i++){
        codime = SqlManager.getValueFromVectorParam(datiEDIT.get(i), 0).getStringValue();
        msgRet += codgar + "-" + codime  ;
        if(i < datiEDIT.size() - 1)
          msgRet += ", ";
      }
      msgRet+=") \r\n";
      this.getGeneManager().deleteTabelle(new String[]{"EDIT"},where, new Object[]{codgar});
    }

    return msgRet;
  }

  /**
   *
   * @param select
   * @param where
   * @param codgar
   * @param msg
   * @param cancellazione
   * @return
   * @throws SQLException
   * @throws GestoreException
   */
  private String deleteDITG(String select, String where, String codgar, String msg,boolean cancellazione) throws SQLException, GestoreException{
    String msgRet=msg;
    List<?> datiDITG = this.sqlManager.getListVector(select + " where " + where, new Object[]{codgar});
    if(datiDITG!=null && datiDITG.size()>0){
      if(cancellazione)
        msgRet +="Eliminato occ. in DITG (codgar5-ngara5-dittao: ";
      else
        msgRet +="Aggiornato occ. in DITG (codgar5-ngara5-dittao: ";
      String ngaraE=null;
      String dittao=null;
      for(int i=0;i<datiDITG.size();i++){
        ngaraE = SqlManager.getValueFromVectorParam(datiDITG.get(i), 0).getStringValue();
        dittao = SqlManager.getValueFromVectorParam(datiDITG.get(i), 1).getStringValue();
        msgRet += codgar + "-" + ngaraE + "-" + dittao  ;
        if(i < datiDITG.size() - 1)
          msgRet += ", ";
      }
      msgRet+=") \r\n";
      if(cancellazione)
        this.getGeneManager().deleteTabelle(new String[]{"DITG"},where, new Object[]{codgar});
    }

    return msgRet;
  }


  /**
   *
   * @param select
   * @param where
   * @param codgar
   * @param msg
   * @return
   * @throws SQLException
   * @throws GestoreException
   */
  private String deleteRagdet(String select, String where, String codgar, String msg) throws SQLException, GestoreException{
    String msgRet=msg;
    List<?> datiRAGDET = this.sqlManager.getListVector(select + " where " + where, new Object[]{codgar});
    if(datiRAGDET!=null && datiRAGDET.size()>0){
      msgRet +="Eliminate occ. in RAGDET (codimp-coddic-numdic: ";
      String codimp=null;
      String coddic = null;
      Long numdic = null;
      for(int i=0;i<datiRAGDET.size();i++){
        codimp = SqlManager.getValueFromVectorParam(datiRAGDET.get(i), 0).getStringValue();
        coddic = SqlManager.getValueFromVectorParam(datiRAGDET.get(i), 1).getStringValue();
        numdic = SqlManager.getValueFromVectorParam(datiRAGDET.get(i), 2).longValue();
        msgRet += codimp + "-" + coddic + "-" + numdic.toString() ;
        if(i < datiRAGDET.size() - 1)
          msgRet += ", ";
      }
      msgRet+=") \r\n";
      this.getGeneManager().deleteTabelle(new String[]{"RAGDET"},where, new Object[]{codgar});
    }

    return msgRet;
  }


  /**
   *
   * @param codgar
   * @param ngara
   * @param msg
   * @param tipo
   * @return
   * @throws SQLException
   * @throws GestoreException
   */
  private String upadteComunicazioni(String codgar, String ngara, String msg,String tipo) throws SQLException, GestoreException{
    String msgRet=msg;
    String buste = "('FS11','FS11A' )";
    if("2".equals(tipo))
      buste = "('FS10','FS10A' )";
    String selectW_INVCOM = "select idprg, idcom from w_invcom where comkey2=? and comtipo in " + buste + " and comstato=6";

    List<?> datiW_INVCOM = this.sqlManager.getListVector(selectW_INVCOM, new Object[]{ngara});
    if(datiW_INVCOM!=null && datiW_INVCOM.size()>0){
      msgRet+="Aggiornato COMSTATO in W_INVCOM (idprg-idcom: ";
      String idprg=null;
      Long idcom=null;
      for(int i=0;i<datiW_INVCOM.size();i++){
        idprg = SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 0).getStringValue();
        idcom = SqlManager.getValueFromVectorParam(datiW_INVCOM.get(i), 1).longValue();
        msgRet += idprg + "-" + idcom.toString()  ;
        if(i < datiW_INVCOM.size() - 1)
          msgRet += ", ";
      }
      msgRet+=") \r\n";
    }
    String update = "update w_invcom set comstato=? where comkey2=? and comtipo in " + buste + " and (comstato=6 or comstato=7";
    if("1".equals(tipo))
      update+=" or comstato=8 or comstato=13";
    update+=")";
    this.sqlManager.update(update, new Object[]{new Long(5),ngara});

    return msgRet;
  }

}
