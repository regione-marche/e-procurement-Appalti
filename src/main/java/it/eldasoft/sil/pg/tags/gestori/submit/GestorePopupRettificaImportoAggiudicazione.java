/*
 * Created on 19/10/18
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.SetProfiloAction;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire la storicizzazione
 * della rettifica
 *
 * @author Marcello Caminiti
 */
public class GestorePopupRettificaImportoAggiudicazione extends
    AbstractGestoreEntita {

  public GestorePopupRettificaImportoAggiudicazione() {
    super(false);
  }

  /** Logger Log4J di classe */
  static Logger          logger = Logger.getLogger(SetProfiloAction.class);

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

  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    String ngara = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
        "ngara"));
    String codgar = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
        "codgar"));

    String aqoper = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
        "aqoper"));

    String modlicgString = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
        "modlicg"));

    //variabili per tracciatura eventi
    int livEvento = 1;
    String codEvento = "GA_RETTIFICA_IMPORTO_AGG";
    String oggEvento = ngara;
    String descrEvento = "Rettifica importo aggiudicazione";
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");

    try{
      Long modlicg = null;
      if(modlicgString!=null && !"".equals(modlicgString))
        modlicg = new Long(modlicgString);

      if(!"2".equals(aqoper)){
        Double ribaggini = datiForm.getDouble("GARE1.RIBAGGINI");
        Double iaggiuini = datiForm.getDouble("GARE1.IAGGIUINI");
        Double ribagginiIniziale = datiForm.getColumn("GARE1.RIBAGGINI").getOriginalValue().doubleValue();
        Double iaggiuiniIniziale = datiForm.getColumn("GARE1.IAGGIUINI").getOriginalValue().doubleValue();
        if((ribagginiIniziale==null && ribaggini!=null)  || (iaggiuiniIniziale==null && iaggiuini!=null)){
          Object par1[]=null;
          String updateGare1="update gare1 set iaggiuini=?";
          updateGare1+=", ribaggini=?";
          par1 = new Object[]{iaggiuini,ribaggini,ngara};
          updateGare1+=" where ngara=?";

          try {
            this.sqlManager.update(updateGare1, par1);
          } catch (SQLException e) {
            this.getRequest().setAttribute("rettificaEseguita", "2");
            livEvento = 3;
            errMsgEvento="Errore nell'aggiornamento di GARE1 per la gara" + ngara;
            throw new GestoreException("Errore nell'aggiornamento di GARE1 per la gara" + ngara, null, e);
          }
        }

        Double ribagg=datiForm.getDouble("GARE.RIBAGG");
        Double riboepv=datiForm.getDouble("GARE.RIBOEPV");
        Double iaggiu=datiForm.getDouble("GARE.IAGGIU");
        Double impgar=datiForm.getDouble("GARE.IMPGAR");
        Object par[]=null;
        String updateGare="update gare set iaggiu=?, impgar=?";
        if(modlicg==null || (modlicg!=null && modlicg.longValue()!=6)){
          updateGare+=",ribagg=?";
          par = new Object[]{iaggiu,impgar,ribagg,ngara};
        }else{
          updateGare+=",riboepv=?";
          par = new Object[]{iaggiu,impgar,riboepv,ngara};
        }
        updateGare+=" where ngara=?";
        try {
          this.sqlManager.update(updateGare, par);
        } catch (SQLException e) {
          this.getRequest().setAttribute("rettificaEseguita", "2");
          livEvento = 3;
          errMsgEvento="Errore nell'aggiornamento di GARE per la gara" + ngara;
          throw new GestoreException("Errore nell'aggiornamento di GARE per la gara" + ngara, null, e);
        }

        Long iterga;
        try {
          iterga = (Long)this.sqlManager.getObject("select iterga from torn where codgar=?", new Object[]{codgar});
          if(iterga != null && iterga.longValue()==6){
            datiForm.addColumn("GARE.TIATTO", new Long(8));

            AggiudicazioneManager aggiudicazioneManager = (AggiudicazioneManager) UtilitySpring.getBean(
                "aggiudicazioneManager", this.getServletContext(), AggiudicazioneManager.class);

            aggiudicazioneManager.calcoImportoIva(ngara, iaggiu);
          }
        } catch (SQLException e) {
          livEvento = 3;
          errMsgEvento="Errore nell'aggiornamento di GAREIVA per la gara" + ngara;
          throw new GestoreException("Errore nell'aggiornamento di GAREIVA per la gara" + ngara, null, e);
        }

        descrEvento+=" (nuovo importo " + UtilityNumeri.convertiImporto(iaggiu, 2)+ " €)";

      }else{

        //Gestione sezioni dinamiche DITGAQ
        AbstractGestoreChiaveNumerica gestoreDITGAQ = new DefaultGestoreEntitaChiaveNumerica(
            "DITGAQ", "ID", new String[] {}, this.getRequest());

        String nomeCampoNumeroRecord = "NUMERO_DITGAQ" ;
        String nomeCampoDelete = "DEL_DITGAQ" ;
        String nomeCampoMod = "MOD_DITGAQ" ;

        if (datiForm.isColumn(nomeCampoNumeroRecord)) {

          // Estraggo dal dataColumnContainer tutte le occorrenze dei campi
          // dell'entità definita per il gestore
          DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(
              datiForm.getColumns(gestoreDITGAQ.getEntita(), 0));

          int numeroRecord = datiForm.getLong(nomeCampoNumeroRecord).intValue();

          String ditta=null;
          Double iaggiu=null;
          String msgImporti="";
          Long id=null;
          for (int i = 1; i <= numeroRecord; i++) {
            DataColumnContainer newDataColumnContainer = new DataColumnContainer(
                tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

            id=newDataColumnContainer.getLong("DITGAQ.ID");
            if(id!=null){
              // Rimozione dei campi fittizi (il campo per la marcatura della
              // delete e
              // tutti gli eventuali campi passati come argomento)
              newDataColumnContainer.removeColumns(new String[] {
                  gestoreDITGAQ.getEntita() + "." + nomeCampoDelete,
                  gestoreDITGAQ.getEntita() + "." + nomeCampoMod });

              ditta=newDataColumnContainer.getString("DITGAQ.DITTAO");
              iaggiu=newDataColumnContainer.getDouble("DITGAQ.IAGGIU");

              if(newDataColumnContainer.isModifiedColumn("DITGAQ.IAGGIU")){
                if("1".equals(datiForm.getString("INIZ_IMP_" + i)))
                  newDataColumnContainer.setOriginalValue("DITGAQ.IAGGIUINI", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, null));
                if("1".equals(datiForm.getString("INIZ_RIB_" + i)))
                  newDataColumnContainer.setOriginalValue("DITGAQ.RIBAGGINI", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, null));
                if(msgImporti.length()>0)
                  msgImporti+=", ";
                msgImporti+="ditta " + ditta + ": nuovo importo " + UtilityNumeri.convertiImporto(iaggiu, 2) + " €";
              }
              gestoreDITGAQ.update(status, newDataColumnContainer);

            }
          }
          descrEvento+=" (" + msgImporti + ")";
        }
      }






      errMsgEvento = "";
      this.getRequest().setAttribute("rettificaEseguita", "1");
    }catch (GestoreException e) {
      livEvento = 3;
      errMsgEvento=e.getMessage();
      throw e;
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
      }

    }
  }


}
