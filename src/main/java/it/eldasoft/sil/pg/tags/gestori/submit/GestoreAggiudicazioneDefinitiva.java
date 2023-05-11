/*
 * Created on 27/09/12
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
import it.eldasoft.gene.bl.integrazioni.CinecaWSManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.sil.pg.bl.GestioneProgrammazioneManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestoreAggiudicazioneDefinitiva extends AbstractGestoreEntita {

	/** Logger */
	static Logger            logger           = Logger.getLogger(GestoreAggiudicazioneDefinitiva.class);

	/** Manager Integrazione Cineca */
	private CinecaWSManager cinecaWSManager;

	private PgManager pgManager;
	
	private GestioneProgrammazioneManager gestioneProgrammazioneManager;

	@Override
  public String getEntita() {
		return "GARE";
	}

	@Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per Cineca
    cinecaWSManager = (CinecaWSManager) UtilitySpring.getBean("cinecaWSManager",
        this.getServletContext(), CinecaWSManager.class);

    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);
    
    gestioneProgrammazioneManager = (GestioneProgrammazioneManager) UtilitySpring.getBean("gestioneProgrammazioneManager",
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
	    this.gestioneProgrammazioneManager.aggiornaRdaGara(datiForm.getString("GARE.CODGAR1"),datiForm.getString("GARE.NGARA"),null);
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

	  boolean isGaraLottiConOffertaUnica = false;
	    String tmp = this.getRequest().getParameter("isGaraLottiConOffertaUnica");
	    if(tmp == null)
	        tmp = (String) this.getRequest().getAttribute("isGaraLottiConOffertaUnica");

	    if("true".equals(tmp))
	        isGaraLottiConOffertaUnica = true;

	    String ditta = datiForm.getString("GARE.DITTA");
	    String numeroElenco = datiForm.getString("GARE.ELENCOE");
	    String ngara = datiForm.getString("GARE.NGARA");
	    if(isGaraLottiConOffertaUnica){
	      String codgar = datiForm.getString("GARE.CODGAR1");


	      //Se è stato modificato il campo RIDISO si devono aggiornare gli importi dei campi IMPGAR
	      //dei lotti aggiudicati
	      if (datiForm.getColumn("GARE.RIDISO").isModified()) {
	          String ridiso = datiForm.getString("GARE.RIDISO");
	          String oldRidiso = datiForm.getColumn("GARE.RIDISO").getOriginalValue().getStringValue();

	          if (ridiso==null || "".equals(ridiso)) ridiso="2";
	          if (oldRidiso==null || "".equals(oldRidiso)) oldRidiso="2";

	          String update="";
	          if(!ridiso.equals(oldRidiso)){
	              if ("1".equals(ridiso)) update="update gare set impgar = impgar /2, ridiso=? where CODGAR1 = ? and DITTA = ? and NGARA <>? and genere is null";
	              else update="update gare set impgar = impgar * 2, ridiso=?  where CODGAR1 = ? and DITTA = ? and NGARA <>? and genere is null";

	              try {
	                this.sqlManager.update(update, new Object[]{ridiso,codgar,ditta, ngara });
	              } catch (SQLException e) {
	                throw new GestoreException(
	                    "Errore nell'allineamento dei dati dei lotti aggiudicati dalla ditta " + ditta, null,e);
	              }
	          }

	      }
	    }else{
	      try {
            String coorba = null;
            String codbic = null;
            Vector datiBancari = this.sqlManager.getVector("select coorba,codbic from impr where codimp=?", new Object[]{ditta});
            if(datiBancari!=null && datiBancari.size()>0){
              coorba=SqlManager.getValueFromVectorParam(datiBancari, 0).stringValue();
              codbic=SqlManager.getValueFromVectorParam(datiBancari, 1).stringValue();
            }
            Double impqua =  datiForm.getDouble("IMPQUA");
            if(impqua!= null){
              String update="update GARECONT set CODIMP = ?, COORBA = ?, CODBIC = ?, IMPQUA = ? ";
              if(datiForm.isModifiedColumn("GARE.DITTA") && datiForm.getString("GARE.DITTA")!=null && !"".equals(datiForm.getString("GARE.DITTA")))
                update +=", STATO=2 ";
              update +="where NGARA = ? and NCONT = ?";
              this.sqlManager.update(
                  update, new Object[]{ditta, coorba, codbic, impqua, ngara, new Long(1) });
            }else{
              String update="update GARECONT set CODIMP = ?, COORBA = ?, CODBIC = ? ";
              if(datiForm.isModifiedColumn("GARE.DITTA") && datiForm.getString("GARE.DITTA")!=null && !"".equals(datiForm.getString("GARE.DITTA")))
                update +=", STATO=2 ";
              update +="where NGARA = ? and NCONT = ?";
              this.sqlManager.update(
                  update, new Object[]{ditta, coorba, codbic, ngara, new Long(1) });
            }
          } catch (SQLException e) {
            throw new GestoreException(
                "Errore nell'aggiornamento di GARECONT " + ditta, null,e);
          }

	      String profiloSemplificato = this.getRequest().getParameter("profiloSemplificato");
	      String offaum = datiForm.getString("TORN.OFFAUM");
	      Double iaggiu = datiForm.getDouble("GARE.IAGGIU");
          Double impapp = datiForm.getDouble("GARE.IMPAPP");

	      //Controlli per il profilo semplificato
	      if(("1".equals(profiloSemplificato)) && datiForm.getString("GARE.DITTA")!=null && !"".equals(datiForm.getString("GARE.DITTA"))){
	        Double ribagg = datiForm.getDouble("GARE.RIBAGG");
	        if(iaggiu==null ||  ribagg==null)
	          throw new GestoreException("L'importo ed il ribasso di aggiudicazione devono essere valorizzati per la ditta " + ditta, "gestoreAggiudicazione.offertaNulla", new Exception());

	        if((!"1".equals(offaum)) && iaggiu != null && impapp != null && iaggiu.doubleValue()> impapp.doubleValue())
    	        UtilityStruts.addMessage(this.getRequest(), "warning",
                  "warnings.gestoreAggiudicazione.importoAggiudicazioneSuperioreBaseGara",
                  new Object[] {UtilityNumeri.convertiImporto(impapp.doubleValue(), 2)});
          }

  	      //Gestione per iterga=6
  	      if(datiForm.isModifiedColumn("GARE.DITTA") && datiForm.getString("GARE.DITTA")!=null && !"".equals(datiForm.getString("GARE.DITTA"))){
  	        try {
  	          String codgar = datiForm.getString("GARE.CODGAR1");






  	          Long iterga = (Long)this.sqlManager.getObject("select iterga from torn where codgar=?", new Object[]{codgar});
  	          if(iterga != null && iterga.longValue()==6){
  	            datiForm.addColumn("GARE.TIATTO", new Long(8));

  	            AggiudicazioneManager aggiudicazioneManager = (AggiudicazioneManager) UtilitySpring.getBean(
  	                "aggiudicazioneManager", this.getServletContext(), AggiudicazioneManager.class);

  	            aggiudicazioneManager.calcoImportoIva(ngara, iaggiu);
  	          }

  	          //inizio integrazione Cineca
  	          //VALUTARE se deve risultare ancora cosi la verifica dell'INTEGRAZIONE///////////////////////////////////////
  	          String integrazioneCineca = ConfigManager.getValore("integrazioneAnagraficheUGOV");
  	          integrazioneCineca = UtilityStringhe.convertiNullInStringaVuota(integrazioneCineca);
              String dittaCineca = datiForm.getString("GARE.DITTA");
  	          if("1".equals(integrazioneCineca) && dittaCineca != null ){
                int msgCeneca = 0;
  	            //verifica che non si tratti un RTI o ....
  	            try {
  	              Vector<?> naturaImprVect = this.sqlManager.getVector("select TIPIMP,NATGIUI from IMPR where CODIMP = ?", new Object[] { dittaCineca });
  	              Long tipimp= null;
  	              Long natgiui = null;
  	              if (naturaImprVect != null && naturaImprVect.size() > 0) {
  	                tipimp = (Long) SqlManager.getValueFromVectorParam(naturaImprVect, 0).getValue();
  	                natgiui = (Long) SqlManager.getValueFromVectorParam(naturaImprVect, 1).getValue();
  	              }
  	              if(tipimp != null && (new Long(3).equals(tipimp) || new Long(2).equals(tipimp) || new Long(10).equals(tipimp) || new Long(11).equals(tipimp))){
  	                List ditteComponentiCineca  = this.sqlManager.getListVector(
  	                    "select CODDIC,NOMDIC from RAGIMP where CODIME9 = ? ", new Object[] { dittaCineca });
  	                if (ditteComponentiCineca != null && ditteComponentiCineca.size() > 0) {
  	                  for (int j = 0; j < ditteComponentiCineca.size(); j++) {
  	                    Vector tmpCineca = (Vector) ditteComponentiCineca.get(j);
  	                    String dittaComponenteCineca = ((JdbcParametro) tmpCineca.get(0)).getStringValue();
  	                    String[] resMsg = cinecaWSManager.gestioneDittaCineca(this.getRequest(), dittaComponenteCineca, tipimp, natgiui);
  	                    if(resMsg[0]!= null){
                          int res = Integer.parseInt(resMsg[0]);
                          if(res <0){
                            String msg = "";
                            if(res == -2){
                              msg = " - Codice Fiscale o Partita IVA non validi!";
                            }else if(res == -19){
                              msg = " - Non risultano valorizzati i seguenti dati obbligatori: \n" + resMsg[1];
                            }else{
                              msg = resMsg[1];
                            }
                            msgCeneca = 1;
                            UtilityStruts.addMessage(this.getRequest(), "warning",
                                "warnings.cineca.mancataIntegrazione",
                                new Object[] { dittaCineca, msg });
                          }
  	                    }
  	                  }
  	                }
  	              }
  	              if(!(tipimp != null && (new Long(3).equals(tipimp) || new Long(10).equals(tipimp)))){
  	                String[] resMsg = cinecaWSManager.gestioneDittaCineca(this.getRequest(), dittaCineca,tipimp, natgiui);
  	                if(resMsg[0]!= null){
                      int res = Integer.parseInt(resMsg[0]);
                      if(res <0){
                        String msg = "";
                        if(res == -2){
                          msg = " - Codice Fiscale o Partita IVA non validi!";
                        }else if(res == -19){
                          msg = " - Non risultano valorizzati i seguenti dati obbligatori: \n" + resMsg[1];
                        }else{
                          msg = resMsg[1];
                        }
                        msgCeneca = 1;
                        UtilityStruts.addMessage(this.getRequest(), "warning",
                            "warnings.cineca.mancataIntegrazione",
                            new Object[] { dittaCineca, msg });
                      }
  	                }
  	              }
  	            } catch (SQLException e) {
  	              throw new GestoreException(
  	                  "Errore nell'integrazione con Cineca ",null, e);
  	            }
  	          }//fine integrazione Cineca


            } catch (SQLException e) {
              throw new GestoreException(
                  "Errore negli aggiornamenti degli importi IVA della gara " + ngara, null,e);
            }

  	        //Sbiancamento campi di GARE1

  	        try {
              this.sqlManager.update("update gare1 set ribaggini=null, iaggiuini=null where ngara=?", new Object[]{ngara});
            } catch (SQLException e) {
              throw new GestoreException(
                  "Errore nell'aggiornamento di GARE1 per la gara " + ngara, null,e);
            }
  	      }


  	      if((datiForm.isColumn("GARESTATI.NPLETTCOMESCLOFEC") && datiForm.isModifiedColumn("GARESTATI.NPLETTCOMESCLOFEC")) || (datiForm.isColumn("GARESTATI.DPLETTCOMESCLOFEC") && datiForm.isModifiedColumn("GARESTATI.DPLETTCOMESCLOFEC"))){

              // Aggiornamento dei campi di GARESTATI
              DefaultGestoreEntita gestoreGARESTATI = new DefaultGestoreEntita(
                  "GARESTATI", this.getRequest());

              String codiceTornata = datiForm.getString("GARE.CODGAR1");
              String numeroGara = datiForm.getString("GARE.NGARA");

              //Nel caso siano stati modificati NPLETTCOMESCLOFTE e DPLETTCOMESCLOFTE fasgar=5 e stepgar=5
              //Nel caso siano stati modificati NPLETTCOMESCLOFEC e DPLETTCOMESCLOFEC fasgar=6 e stepgar=6
              Long faseGaraEc = new Long(GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE/10);
              Long stepGaraEc = new Long(GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE);


              DataColumn dataColumnGareStati[] = new DataColumn[6];
              dataColumnGareStati[0] = new DataColumn("GARESTATI.CODGAR",new JdbcParametro(JdbcParametro.TIPO_TESTO,null));
              dataColumnGareStati[1] = new DataColumn("GARESTATI.NGARA",new JdbcParametro(JdbcParametro.TIPO_TESTO,null));
              dataColumnGareStati[2] = new DataColumn("GARESTATI.FASGAR",new JdbcParametro(JdbcParametro.TIPO_NUMERICO,null));
              dataColumnGareStati[3] = new DataColumn("GARESTATI.STEPGAR",new JdbcParametro(JdbcParametro.TIPO_NUMERICO,null));
              dataColumnGareStati[4] = new DataColumn("GARESTATI.NPLETTCOMESCL",new JdbcParametro(JdbcParametro.TIPO_TESTO,null));
              dataColumnGareStati[5] = new DataColumn("GARESTATI.DPLETTCOMESCL",new JdbcParametro(JdbcParametro.TIPO_DATA,null));
              DataColumnContainer dataColumnContainerGARESTATI = new DataColumnContainer(dataColumnGareStati);

              dataColumnContainerGARESTATI.setValue("GARESTATI.CODGAR", codiceTornata);
              dataColumnContainerGARESTATI.getColumn("GARESTATI.CODGAR").setObjectOriginalValue(codiceTornata);
              dataColumnContainerGARESTATI.getColumn("GARESTATI.CODGAR").setChiave(true);

              dataColumnContainerGARESTATI.setValue("GARESTATI.NGARA", numeroGara);
              dataColumnContainerGARESTATI.getColumn("GARESTATI.NGARA").setObjectOriginalValue(numeroGara);
              dataColumnContainerGARESTATI.getColumn("GARESTATI.NGARA").setChiave(true);

              Date datamtp = new Date(1);
              datamtp.setTime(1);


              dataColumnContainerGARESTATI.setValue("GARESTATI.FASGAR", faseGaraEc);
              dataColumnContainerGARESTATI.getColumn("GARESTATI.FASGAR").setObjectOriginalValue(faseGaraEc);
              dataColumnContainerGARESTATI.getColumn("GARESTATI.FASGAR").setChiave(true);

              dataColumnContainerGARESTATI.setValue("GARESTATI.STEPGAR", stepGaraEc);
              dataColumnContainerGARESTATI.getColumn("GARESTATI.STEPGAR").setObjectOriginalValue(stepGaraEc);
              dataColumnContainerGARESTATI.getColumn("GARESTATI.STEPGAR").setChiave(false);

              String NPLETTCOMESCLOFEC = StringUtils.stripToNull(datiForm.getString("GARESTATI.NPLETTCOMESCLOFEC"));
              dataColumnContainerGARESTATI.setValue("GARESTATI.NPLETTCOMESCL", NPLETTCOMESCLOFEC);
              dataColumnContainerGARESTATI.getColumn("GARESTATI.NPLETTCOMESCL").setObjectOriginalValue(" ");
              dataColumnContainerGARESTATI.getColumn("GARESTATI.NPLETTCOMESCL").setChiave(false);

              Timestamp DPLETTCOMESCLOFEC = datiForm.getData("GARESTATI.DPLETTCOMESCLOFEC");
              dataColumnContainerGARESTATI.setValue("GARESTATI.DPLETTCOMESCL", DPLETTCOMESCLOFEC);
              dataColumnContainerGARESTATI.getColumn("GARESTATI.DPLETTCOMESCL").setObjectOriginalValue(datamtp);
              dataColumnContainerGARESTATI.getColumn("GARESTATI.DPLETTCOMESCL").setChiave(false);


              if(this.geneManager.countOccorrenze("GARESTATI", "CODGAR=? and NGARA=? and FASGAR=?",
                      new Object[]{codiceTornata, numeroGara, faseGaraEc}) > 0)
                  gestoreGARESTATI.update(status, dataColumnContainerGARESTATI);
              else
                  gestoreGARESTATI.inserisci(status, dataColumnContainerGARESTATI);



          }


	    }

	    if(numeroElenco!=null && !"".equals(numeroElenco)){
	      try{
	      String codiceElenco = "$" + numeroElenco;
	      Long tipoalgo = (Long)sqlManager.getObject("select tipoalgo from garealbo where ngara=? and codgar= ?", new Object[]{numeroElenco, codiceElenco});
	      String select="select catiga, numcla from catg where ngara=? and ncatg=1";
          String catiga=null;
          Long numcla=null;
          Long tipgen = datiForm.getLong("TORN.TIPGEN");
          String codiceStazApp = datiForm.getString("TORN.CENINT");
          Vector datiCatg = this.geneManager.getSql().getVector(select, new Object[] { ngara });
          if(datiCatg!=null && datiCatg.size()>0){
            catiga = (String)((JdbcParametro) datiCatg.get(0)).getValue();
            numcla = (Long)((JdbcParametro) datiCatg.get(1)).getValue();
          }

	      String dittaOrig= datiForm.getColumn("GARE.DITTA").getOriginalValue().stringValue();
	      Timestamp dattoa = datiForm.getData("GARE.DATTOA");
	      Timestamp dattoaOrig = datiForm.getColumn("GARE.DATTOA").getOriginalValue().dataValue();
	      ditta=StringUtils.stripToNull(ditta);
	      dittaOrig=StringUtils.stripToNull(dittaOrig);
	      //Long acquisizione=null;
	      //Long acquisizioneOrig=null;
	      String dittaConteggio=null;
	      String dittaConteggioOrig=null;
	      if(ditta!=null){
	        dittaConteggio = this.pgManager.getDittaSelezionataDaElenco(ditta,ngara);
	        //acquisizione=(Long)this.sqlManager.getObject("select acquisizione from ditg where ngara5=? and dittao=?", new Object[]{ngara,dittaConteggio});
	      }
	      if(dittaOrig!=null){
	        dittaConteggioOrig = this.pgManager.getDittaSelezionataDaElenco(dittaOrig,ngara);
	        //acquisizioneOrig=(Long)this.sqlManager.getObject("select acquisizione from ditg where ngara5=? and dittao=?", new Object[]{ngara,dittaConteggioOrig});
	      }

	      if((ditta!=null && dittaOrig==null && dattoa!=null && dattoaOrig==null) || (ditta!=null && dittaOrig!=null && dattoaOrig==null && dattoa!=null) ||
	          (ditta!=null && dittaOrig==null && dattoa!=null && dattoaOrig!=null)){
	        //Si incrementa il conteggio per ditta se è stata selezionata da elenco
	        if(dittaConteggio!=null)
	          this.pgManager.aggiornaNumAggiudicazioni(codiceElenco, numeroElenco, dittaConteggio, catiga, tipgen, numcla, codiceStazApp, tipoalgo, "INS");
	      }else if((ditta==null && dittaOrig!=null && ((dattoa==null && dattoaOrig!=null) || (dattoa!=null && dattoaOrig!=null))) ||
	          (ditta!=null && dittaOrig!=null && !ditta.equals(dattoaOrig) && dattoa==null && dattoaOrig!=null) ||
	          (dattoa==null && dattoaOrig!=null && ditta!=null && dittaOrig!=null && ditta.equals(dattoaOrig))){
	        //Si decrementa il conteggio per ditta originale se è stata selezionata da elenco
	        if(dittaConteggioOrig!=null)
	          this.pgManager.aggiornaNumAggiudicazioni(codiceElenco, numeroElenco, dittaConteggioOrig, catiga, tipgen, numcla, codiceStazApp, tipoalgo, "DEL");
	      }else if(ditta!=null && dittaOrig!=null && !ditta.equals(dattoaOrig) && dattoa!=null && dattoaOrig!=null){
	        //Si decrementa il conteggio per ditta originale se è stata selezionata da elenco e poi si incrementa il conteggio per ditta se è stata selezionata da elenco
	        if(dittaConteggioOrig!=null)
	          this.pgManager.aggiornaNumAggiudicazioni(codiceElenco, numeroElenco, dittaConteggioOrig, catiga, tipgen, numcla, codiceStazApp, tipoalgo, "DEL");
	        if(dittaConteggio!=null)
	          this.pgManager.aggiornaNumAggiudicazioni(codiceElenco, numeroElenco, dittaConteggio, catiga, tipgen, numcla, codiceStazApp, tipoalgo, "INS");
	      }
	      }catch(SQLException e) {
            throw new GestoreException(
                "Errore nell'aggiornamento del numero aggiudicazioni della ditta "+ ditta + "  della gara " + ngara, null,e);
	      }
	    }


	  if(datiForm.isColumn("GARE1.NOTDEFI")){

	    AbstractGestoreEntita gestoreGARE1 = new DefaultGestoreEntita("GARE1", this.getRequest());
	    gestoreGARE1.update(status, datiForm);
	  }

      //Gestione personalizzata delle sezioni dinamiche per gli atti di gara (aggiudicazione)
      AbstractGestoreChiaveNumerica gestoreGAREATTI = new DefaultGestoreEntitaChiaveNumerica(
          "GAREATTI", "ID", new String[] {}, this.getRequest());
      this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
          gestoreGAREATTI, "GAREATTIAGG",
          new DataColumn[] { datiForm.getColumn("GARE.NGARA") }, null);


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
