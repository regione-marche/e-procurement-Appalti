/*
 * Created on 10/01/13
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneProgrammazioneManager;
import it.eldasoft.sil.pg.bl.SmatManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestoreContratto extends AbstractGestoreEntita {

	/** Logger */
	static Logger            logger           = Logger.getLogger(GestoreContratto.class);

    /** Manager di SMAT */
  private SmatManager  smatManager ;
  
  private GestioneProgrammazioneManager gestioneProgrammazioneManager;


	@Override
  public String getEntita() {
		return "GARECONT";
	}

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per SMAT
    this.smatManager = (SmatManager) UtilitySpring.getBean("smatManager", this.getServletContext(), SmatManager.class);

    this.gestioneProgrammazioneManager = (GestioneProgrammazioneManager) UtilitySpring.getBean("gestioneProgrammazioneManager",
        this.getServletContext(), GestioneProgrammazioneManager.class);
   }


	@Override
  public void postDelete(DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void postInsert(DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void postUpdate(DataColumnContainer datiForm)
      throws GestoreException {
	}
	
	@Override
  public void afterUpdateEntita(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
	 //Integrazione programmazione
	 if(gestioneProgrammazioneManager.isAttivaIntegrazioneProgrammazione())
	   this.gestioneProgrammazioneManager.aggiornaRdaGara(datiForm.getString("GARE.CODGAR1"),null,null);
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

	  Long aqoper=null;
	  if (datiForm.isColumn("GARE1.AQOPER"))
	      aqoper = datiForm.getLong("GARE1.AQOPER");
	  if((aqoper==null || (aqoper!=null && aqoper.longValue()!=2)) && datiForm.isModifiedTable("DITG")){
	    AbstractGestoreEntita DITG = new DefaultGestoreEntita("DITG", this.getRequest());
        DITG.update(status, datiForm);
      }

	  /*
	  if(datiForm.isModifiedTable("GARECONT")){
	    AbstractGestoreEntita gestoreGARECONT = new DefaultGestoreEntita("GARECONT", this.getRequest());
	    gestoreGARECONT.update(status, datiForm);
	  }

	  if(datiForm.isModifiedTable("XGARECONT")){
        datiForm.addColumn("XGARECONT.NGARA", datiForm.getString("GARE.NGARA"));
	    AbstractGestoreEntita gestoreXGARECONT = new DefaultGestoreEntita("XGARECONT", this.getRequest());
        gestoreXGARECONT.update(status, datiForm);
      }
      */
	  if(datiForm.isModifiedTable("GARE")){
        // Nel caso di personalizzazione SMAT, controllo comunque se rda arriva dalla integrazione
        if (this.getGeneManager().getProfili().checkProtec(
            (String) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS",
            "ALT.GARE.inserimentoRdaSMAT")) {
          if (datiForm.isColumn("GARE.NMAXIMO")
              && datiForm.isModifiedColumn("GARE.NMAXIMO")
              && datiForm.getColumn("GARE.NMAXIMO").getValue().stringValue() != null
              && !"".equals(datiForm.getColumn("GARE.NMAXIMO").getValue().stringValue())) {
            String numeroOrdine = datiForm.getColumn("GARE.NMAXIMO").getValue().stringValue();
            String codiceGara = datiForm.getColumn("GARE.CODGAR1").getValue().stringValue();
            String numeroGara = datiForm.getColumn("GARE.NGARA").getValue().stringValue();

            int res = smatManager.updOrdineDaImputazioneManuale(numeroOrdine, codiceGara, numeroGara);
            if (res < 0) {
              throw new GestoreException("Errore in aggiornamento ordine SMAT", null);
            }
          }
	    }


        AbstractGestoreEntita gestoreGARE = new DefaultGestoreEntita("GARE", this.getRequest());
        gestoreGARE.update(status, datiForm);
      }

	  AbstractGestoreChiaveIDAutoincrementante gestoreGARCOMPREQ = new DefaultGestoreEntitaChiaveIDAutoincrementante(
          "GARCOMPREQ", "ID", this.getRequest());
      this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
          gestoreGARCOMPREQ, "GARCOMPREQ",
          new DataColumn[] {datiForm.getColumn("GARE.NGARA")}, null);

      AbstractGestoreChiaveIDAutoincrementante gestoreMultiploGARATTIAGG = new DefaultGestoreEntitaChiaveIDAutoincrementante(
          "GARATTIAGG", "ID", this.getRequest());
      this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
          gestoreMultiploGARATTIAGG, "GARATTIAGG",
          new DataColumn[] {datiForm.getColumn("GARE.NGARA")}, null);

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

        for (int i = 1; i <= numeroRecord; i++) {
          DataColumnContainer newDataColumnContainer = new DataColumnContainer(
              tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

          // Rimozione dei campi fittizi (il campo per la marcatura della
          // delete e
          // tutti gli eventuali campi passati come argomento)
          newDataColumnContainer.removeColumns(new String[] {
              gestoreDITGAQ.getEntita() + "." + nomeCampoDelete,
              gestoreDITGAQ.getEntita() + "." + nomeCampoMod });

          gestoreDITGAQ.update(status, newDataColumnContainer);

          //Se è stata modoficata l'entità DITG si deve forzare l'aggiornamento
          if (datiForm.isModifiedColumn("DITG.RICSUB_" + i)) {
            String ricsub = datiForm.getString("DITG.RICSUB_" + i);
            String dittao = newDataColumnContainer.getString("DITGAQ.DITTAO");
            try {
              this.sqlManager.update("update ditg set ricsub=? where ngara5=? and dittao=? and codgar5=?",
                  new Object[]{ricsub,datiForm.getString("GARE.NGARA"),dittao,datiForm.getString("GARE.CODGAR1")});
            } catch (SQLException e) {
              throw new GestoreException(
                  "Errore nell'aggiornamento del campo DITG.RICSUB per la ditta:" + dittao + " della gara:" + datiForm.getString("GARE.NGARA"), null, e);
            }
          }
        }
      }

	}

}
