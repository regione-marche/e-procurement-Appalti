/*
 * Created on Aprile 2022
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
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityMath;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore per importare le lavorazioni dalle rda
 *
 *
 */
public class GestoreImportaLavorazioniRda extends AbstractGestoreChiaveNumerica {

  static Logger               logger                    = Logger.getLogger(GestoreImportaLavorazioniRda.class);

  @Override
  public String[] getAltriCampiChiave() {
    return null;
  }

  @Override
  public String getCampoNumericoChiave() {
    return "ID";
  }

  @Override
  public String getEntita() {
    return "GARE";
  }

  public GestoreImportaLavorazioniRda() {
    super(false);
  }

  public GestoreImportaLavorazioniRda(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }


  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {

  }


  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("preInsert: inizio metodo");

    	GestioneWSERPManager gestioneWSERPManager =
    			(GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager", this.getServletContext(), GestioneWSERPManager.class);

	    String tipoWSERP =null;
	    WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
	    if(configurazione.isEsito()){
	      tipoWSERP = configurazione.getRemotewserp();
	    }
	    
	    ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
	    Long syscon = new Long(profilo.getId());
	    String[] credenziali = gestioneWSERPManager.wserpGetLogin(syscon, "WSERP");
	    String username = credenziali[0];
	    String password = credenziali[1];
	
	    WSERPRdaType erpSearch = new WSERPRdaType();

        String strSqlInsert="insert into GCAP (NGARA,CONTAF,NORVOC,CODVOC,QUANTI,PREZUN,CLASI1," +
      	      "SOLSIC,SOGRIB,VOCE,IDVOC,UNIMIS,CODCARR,CODRDA,POSRDA,CODCAT,PERCIVA,DATACONS,LUOGOCONS)" +
      	      " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String strSqlInsertEst = "insert into GCAP_EST (DESEST,NGARA,CONTAF) values(?,?,?)";

	    String codiceAppalto = datiForm.getString("CODAPPALTO");
	    String ngara = datiForm.getString("GARE.NGARA");

	    WSERPRdaType[] rdaArray = null;
		if("ATAC".equals(tipoWSERP)){
			try {
				
				
			    //Verifiche preliminari
			    codiceAppalto = StringUtils.stripToEmpty(codiceAppalto);
			    ngara = StringUtils.stripToEmpty(ngara);
			    
			    if(!"".equals(codiceAppalto) && !"".equals(ngara)){
		   			Long countAppalto = (Long) this.sqlManager.getObject("select count(gcap.contaf) from GCAP,GARE"
		   					+ " where gcap.ngara= gare.ngara and gare.ngara <> ? and gcap.codcarr = ? and gare.esineg is null and gare.seguen is null",
								    new Object[] { ngara,codiceAppalto });
		   			if(countAppalto>0) {
			   			String garaAssociata = (String) this.sqlManager.getObject("select distinct(gcap.ngara) from GCAP,GARE"
			   					+ " where gcap.ngara= gare.ngara and gcap.codcarr = ? and gare.esineg is null and gare.seguen is null", new Object[] { codiceAppalto });
			   			garaAssociata = StringUtils.stripToEmpty(garaAssociata);
		   				this.getRequest().setAttribute("importRdaEseguito", "2");
		   				throw new GestoreException("Non risulta possibile associare il codice appalto indicato,in quanto gi\\u00e0 associato alla gara "+ garaAssociata, "codiceAppalto.assegnato", new Object[]{garaAssociata}, new Exception());
		   			}
			    }
				
				erpSearch.setCodiceCarrello(codiceAppalto);
				WSERPRdaResType wserpRdaRes = gestioneWSERPManager.wserpListaRda(username, password, "WSERP", erpSearch );
		        if(wserpRdaRes.isEsito()) {
			        rdaArray  = wserpRdaRes.getRdaArray();
			        int numRdaWS = rdaArray.length;
			        int numRdaCaricabili = 0;
			        Double importoTotalePosizioni = Double.valueOf(0);
	                if(rdaArray != null && rdaArray.length > 0){
	                	//faccio qui l'eliminazione delle righe preesistenti se ci sono:
               			Long countRda = (Long) this.sqlManager.getObject("select count(contaf) from GCAP where ngara = ? and codcarr is not null",
	    							    new Object[] { ngara });
               			if(countRda>0) {
               				//elimino
               				if(!"".equals(ngara)) {
                   				this.sqlManager.update("delete from gcap where ngara = ?", new Object[] {ngara});
                   				this.sqlManager.update("delete from gcap_est where ngara = ?", new Object[] {ngara});
               				}
               			}
	                	
	                	for (int h = 0; h < rdaArray.length; h++) {
	                		WSERPRdaType hRda = rdaArray[h];
	                		String codiceArticolo= hRda.getCodiceMateriale();
	                		Double quantita=null;
	                		if(hRda.getQuantita()!=null) {
	                			quantita = hRda.getQuantita();	
	                		}
	                		Double prezzo=null;
	                		if(hRda.getValoreStimato()!=null) {
	                			prezzo = hRda.getValoreStimato();	
	                		}
	                		
	                		Double importoTotalePosizione=null;
	                		//in via provvisoria, per esigenze SAP, viene utilizzato l'importo rinnovi per 
	                		// passare l'importo totale posizione
	                		if(hRda.getImportoRinnovi()!=null) {
	                			importoTotalePosizione = hRda.getImportoRinnovi();
	                		}

	                		Calendar calConsegna = hRda.getDataConsegna();
	                        Date dataConsegna = null;
	                        if(calConsegna != null){
	                          dataConsegna = calConsegna.getTime();
	                        }
	                		
	                        String unitaMisura = hRda.getUm();
	                        unitaMisura = UtilityStringhe.convertiNullInStringaVuota(unitaMisura);
	                        //verifica ed eventuale inserimento unita misura
				
							Long ret = (Long) this.sqlManager.getObject(
							    "select count(*) from UNIMIS where CONTA = ? and TIPO = ? ",
							    new Object[] { new Long(-1), unitaMisura });
							
							
	                        if(new Long(0).equals(ret) && !"".equals(unitaMisura)){
	                            this.sqlManager.update("insert into UNIMIS (CONTA, TIPO, DESUNI, NUMDEC) values (?, ?, ?, ?)",
	                                new Object[] { new Long(-1), unitaMisura, unitaMisura, new Long(0) });
	                          }

	                  		
	                  		String oggetto = hRda.getDescrizione();
	                  		String descrizione = null;
	                  		if(oggetto!= null && oggetto.length()>2000) {
	                  			String descrizioneOggetto=oggetto;
	                  			oggetto = descrizioneOggetto.substring(0,1999);
	                  			descrizione = descrizioneOggetto.substring(1999);
	                  		}
	                  		
	                  		String codiceCarrello= hRda.getIdLotto();
	                  		String codiceRda=hRda.getCodiceRda();
	                  		String posizioneRda = hRda.getPosizioneRda();
	                  		
	                  		//verifico se la riga e' caricabile
	                  		Boolean rigaCaricabile= true;
	                        Long countRigaPresente = (Long) sqlManager.getObject(
	                                  "select count(contaf) from GCAP where ngara = ? and codcarr = ? and codrda = ? and posrda = ?",
	                                  new Object[] { ngara,codiceCarrello,codiceRda,posizioneRda });
	                        if(countRigaPresente > Long.valueOf(0)) {
	                        	rigaCaricabile= false;
	                        }

	                  		
		                  	if(rigaCaricabile) {
		                  		  numRdaCaricabili = numRdaCaricabili+1;
		                  		  importoTotalePosizioni = importoTotalePosizioni + importoTotalePosizione;
		                          Long maxContafEsistente = (Long) sqlManager.getObject(
		                                  "select coalesce(max(contaf),0) from GCAP where ngara = ?", new Object[] { ngara });
	                              Long contaf = new Long(maxContafEsistente+1);
	                              this.sqlManager.update(strSqlInsert, new Object[]{ngara, contaf,
		                                  new Long(maxContafEsistente+1),codiceArticolo,quantita,prezzo,new Long(3),"2","2",oggetto,
		                                  null,unitaMisura,codiceCarrello,codiceRda,posizioneRda,null,null,dataConsegna,null});
		                              //descrizione estesa : solo se supera i 2000 caratteri
	                              this.sqlManager.update(strSqlInsertEst,
		                                  new Object[] { descrizione,ngara, new Long(maxContafEsistente+1)});
		                              maxContafEsistente++;
		                  		
		                  	}	
	                		
	                	}
	                	
	                }

	                PgManager pgManager = (PgManager)UtilitySpring.getBean("pgManager", this.getServletContext(),PgManager.class);
           		    importoTotalePosizioni = UtilityMath.round(importoTotalePosizioni, 2);
	                pgManager.aggiornamentoImportiDaLavorazioni(ngara,false);
	                
	                if(numRdaCaricabili==0) {
	                	//gestisci un messaggio
	                }
		        
		        	this.getRequest().setAttribute("importRdaEseguito", "1");
		        }else {
		        	this.getRequest().setAttribute("importRdaEseguito", "2");  	
		        	if("NoAppalto".equals(wserpRdaRes.getMessaggio())) {
		        		throw new GestoreException("L'appalto indicato ("+codiceAppalto+") non esiste", "codiceAppalto.inesistente", new Object[]{codiceAppalto}, new Exception());

		        	}
		        	
		        }//esito
				
				
			} catch (SQLException e) {
				throw new GestoreException("Errore in fase di import delle rda in gara", null, e);
			}

	        
	        	
	        	
	            	

	    }//ATAC
        



        	
        	
        	
        	

        

     


      

    

    if (logger.isDebugEnabled()) logger.debug("preInsert: fine metodo");

  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {

  }




}