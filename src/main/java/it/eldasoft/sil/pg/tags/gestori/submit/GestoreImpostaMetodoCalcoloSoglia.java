/*
 * Created on 19/02/19
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per la pagina che imposta il metodo di calcolo soglia anomalia
 * gare-popup-metodoCalcoloAnomalia.jsp
 *
 *
 *
 * @author Marcello Caminiti
 */
public class GestoreImpostaMetodoCalcoloSoglia extends AbstractGestoreEntita {

  static Logger logger = Logger.getLogger(GestoreImpostaMetodoCalcoloSoglia.class);

  @Override
  public String getEntita() {
    return "GARE1";
  }

  public GestoreImpostaMetodoCalcoloSoglia() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestoreImpostaMetodoCalcoloSoglia(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

  	int numeroLottiPagina = 0;
    String numeroLottiPaginaString = this.getRequest().getParameter("numeroLottiPagina");
    if(numeroLottiPaginaString != null && numeroLottiPaginaString.length() > 0)
      numeroLottiPagina =  UtilityNumeri.convertiIntero(numeroLottiPaginaString).intValue();

    //variabili per tracciatura eventi
    String messageKey = null;
    int livEvento = 1;
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
    String descr="";
    String isGaraDLGS2017 = this.getRequest().getParameter("isGaraDLGS2017");

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        this.getServletContext(), TabellatiManager.class);

    Long metsoglia=null;
    Double metcoeff = null;
    String lotto=null;
    for (int i = 1; i <= numeroLottiPagina; i++) {
      DataColumnContainer dataColumnContainerDiRiga = new DataColumnContainer(
      		impl.getColumnsBySuffix("_" + i, false));

      try {
        metsoglia=null;
        metcoeff=null;
        lotto =null;
        if(dataColumnContainerDiRiga.isModifiedColumn("GARE1.METSOGLIA") || dataColumnContainerDiRiga.isModifiedColumn("GARE1.METCOEFF")){
          metsoglia = dataColumnContainerDiRiga.getLong("GARE1.METSOGLIA");
          metcoeff = dataColumnContainerDiRiga.getDouble("GARE1.METCOEFF");
          lotto = dataColumnContainerDiRiga.getString("GARE1.NGARA");
          dataColumnContainerDiRiga.update("GARE1", sqlManager);
          livEvento = 1;
          errMsgEvento = "";
        }
      } catch (SQLException e) {
        livEvento = 3;
        messageKey = "errors.gestoreException.update.setMetodoCalcoloSoglia";
        errMsgEvento = this.resBundleGenerale.getString(messageKey);
      }finally{
      //Tracciatura eventi
        try {
          if(metsoglia!=null){
            descr="Assegnazione metodo calcolo anomalia ";
            String descrizioneMetsoglia = "";
            descrizioneMetsoglia = tabellatiManager.getDescrTabellato("A1126", metsoglia.toString());
            descr+="(" + descrizioneMetsoglia;
            if(metcoeff!=null){
              String metcoeffString = Double.toString(metcoeff.doubleValue());
              if(metcoeffString.indexOf(".")>0)
                metcoeffString=metcoeffString.replace(".", ",");
              descr += " - " + metcoeffString;
            }
            if("true".equals(isGaraDLGS2017))
              descr+=" - adeguato DLgs.56/2017";
            descr+=")";
            LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
            logEvento.setLivEvento(livEvento);
            logEvento.setOggEvento(lotto);
            logEvento.setCodEvento("GA_METODO_ANOMALIA");
            logEvento.setDescr(descr);
            logEvento.setErrmsg(errMsgEvento);
            LogEventiUtils.insertLogEventi(logEvento);
          }


        } catch (Exception le) {
          messageKey = "errors.logEventi.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), le);
        }
      }

    }

  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

}