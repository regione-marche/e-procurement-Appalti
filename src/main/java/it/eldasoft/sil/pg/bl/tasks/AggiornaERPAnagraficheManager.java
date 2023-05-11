/*
 * Created on 04/11/2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.bl.erp.CavWSERPManager;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoAnagraficaImpresaDocument;
import it.eldasoft.sil.portgare.datatypes.RegistrazioneImpresaDocument;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPAnagraficaType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreResType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreType;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

public class AggiornaERPAnagraficheManager {

  static Logger           logger = Logger.getLogger(AggiornaERPAnagraficheManager.class);
  
  static String     nomeFileXML_Aggiornamento = "dati_reg.xml";

  private GestioneWSERPManager gestioneWSERPManager;
  
  private CavWSERPManager cavWSERPManager;
  
  private FileAllegatoManager fileAllegatoManager;

  private SqlManager      sqlManager;

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }
  
  public void setCavWSERPManager(CavWSERPManager cavWSERPManager) {
	    this.cavWSERPManager = cavWSERPManager;
  }
	  
  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
	   this.fileAllegatoManager = fileAllegatoManager;
  }
  
  public void setSqlManager(SqlManager sqlManager) {
	    this.sqlManager = sqlManager;
  }
  
  /**
   *Aggiornamento delle anagrafiche
   * @throws JspException 
   *
   */
  public void setDatiAnagrafiche() throws GestoreException, JspException{

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato
    if (WebUtilities.isAppNotReady()) return;
    
    //variabili per tracciatura eventi
    int livEvento = 1;
    String codEvento = "GA_AGGANAG_ERP";
    String descrEvento = "Aggiornamento anagrafico ERP";
    String errMsgEvento = null;


    if (logger.isDebugEnabled())
      logger.debug("setDatiAnagrafiche: inizio metodo");
    
    //Si determina se è attiva l'integrazione con WSERP
    String urlWSERP = ConfigManager.getValore("wserp.erp.url");
    if(urlWSERP != null && !"".equals(urlWSERP)){
        WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
        if(configurazione.isEsito()){
            String tipoWSERP = configurazione.getRemotewserp();
            if (logger.isInfoEnabled()) {
              logger.info("Avvio della procedura di aggiornamento anagrafiche ERP");
            }
            
            if("RAI".equals(tipoWSERP)){

            	Date date = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                // data ieri
                Date filterDate = cal.getTime();
                
                Boolean acquisito=false;
                
                //selezione FS15 con stato 5 del giorno prima e non solo        
                String selMarcatoriDaEseguire ="select idprg,idcom,comkey1"
                		+ " from w_invcom"
                		+ " where comtipo = ? and comstato = ? ";

                //seleziono gli aggiornamenti del giorno prima (o data simile)
                String selAggiornamentiDaEseguire = "select stab_val1,stab_operation from stab_trg"
                		+ " where stab_table = ? and stab_key1 = ? and (stab_operation = ? or stab_operation = ?)"
                		+ " and stab_date >= ?"
                		+ " order by stab_seq";
                	
        			try {
        				//considero i marcatori ancora da elaborare,indipendentemente dalla data
         			   List marcatoriDaEseguire = sqlManager.getListVector(selMarcatoriDaEseguire,  new Object[]{"FS15","5"});
        			   if(marcatoriDaEseguire != null && marcatoriDaEseguire.size()>0){
        				   for(int i = 0; i < marcatoriDaEseguire.size(); i++ ){
        					   	livEvento = 1;
        					    errMsgEvento = null;
        						String idprg = (String) SqlManager.getValueFromVectorParam(marcatoriDaEseguire.get(i), 0).getValue();
        						Long idcom =    (Long) SqlManager.getValueFromVectorParam(marcatoriDaEseguire.get(i), 1).getValue();
        						String comkey1 = (String) SqlManager.getValueFromVectorParam(marcatoriDaEseguire.get(i), 2).getValue();
        						String codiceImpresa = (String) sqlManager.getObject("select userkey1 from w_puser where usernome=?",new Object[] { comkey1 });
        						codiceImpresa =StringUtils.stripToEmpty(codiceImpresa);
        						 try {
        								if(!"".equals(codiceImpresa)) {
        									String soggettoRichiedente = getSoggettoRichiedentePortale(comkey1,"I");
        									soggettoRichiedente = StringUtils.stripToEmpty(soggettoRichiedente);
        									if(!"".equals(soggettoRichiedente)) {
        										String[] resAggErp= this.setAggiornamentoERP(codiceImpresa, soggettoRichiedente);
        										if(resAggErp[0]=="true") {
        											acquisito = true;
        										}else {
        											livEvento = 3;
        											errMsgEvento= resAggErp[1];
        											acquisito =false;
        										}
        									}else {
        										errMsgEvento = "Il soggetto richiedente risulta assente:manca la FS1 associata";
        										livEvento = 3;
        										acquisito =false;
        									}
        								}else {
        									errMsgEvento = "Il collegamento con il portale risulta assente";
        									livEvento = 3;
        									acquisito =false;
        								}
        								
        			                }catch(Exception e){
    									livEvento = 3;
        			                	errMsgEvento = "Errore inaspettato durante aggiornamento anagrafiche ERP (marcatori FS15)";
        			                	logger.error(errMsgEvento,e);
    									acquisito =false;
        							}finally{
        								//Alla fine aggiorno lo stato della FS15
        								if (acquisito) {
        									this.gestioneWSERPManager.updComunicazione(idprg, idcom, "6");
        				                } else {
        				                	this.gestioneWSERPManager.updComunicazione(idprg, idcom, "7");
        				                }
    							      try {
    							        LogEvento logEvento = new LogEvento();
    							        logEvento.setCodApplicazione("PG");
    							        logEvento.setLivEvento(livEvento);
    							        logEvento.setOggEvento(codiceImpresa);
    							        logEvento.setCodEvento(codEvento);
    							        logEvento.setDescr(descrEvento + " (cod.fornitore: " + codiceImpresa + ")");
    							        logEvento.setErrmsg(errMsgEvento);
    							        LogEventiUtils.insertLogEventi(logEvento);
    							      } catch (Exception le) {
    							        logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
    							      }
        							  
        							}
        						 
 								acquisito =false;
        						
        				   }//for
        			   }//if macatori
        				
        	        	//considero quelle inserite ieri
        				List aggiornamentiDaEseguire = sqlManager.getListVector(selAggiornamentiDaEseguire,  new Object[]{"IMPR","CODIMP","INS","UPD",filterDate});
        				if(aggiornamentiDaEseguire != null && aggiornamentiDaEseguire.size()>0){
        					for(int i = 0; i < aggiornamentiDaEseguire.size(); i++ ){
        						livEvento = 1;
        						errMsgEvento = null;
        						String codiceImpresa = (String) SqlManager.getValueFromVectorParam(aggiornamentiDaEseguire.get(i), 0).getValue();
        						String tipoOperazione = (String) SqlManager.getValueFromVectorParam(aggiornamentiDaEseguire.get(i), 1).getValue();
        				        try {
        							//devo trovare una FS15
        						      String selFS15="select IDCOM,USERNOME from"
        						      		+ " w_puser,W_INVCOM"
        						      		+ " where  IDPRG = ? and COMTIPO = ?"
        						      		+ " and COMSTATO = ?"
        						      		+ " and COMKEY1=USERNOME and USERKEY1 = ? ";
        						      
        						      Vector<?> datiFS15 = this.sqlManager.getVector(selFS15, new Object[] { "PA","FS15","6",codiceImpresa } );
        						      if(datiFS15!=null) {
        						       	String comkey1=SqlManager.getValueFromVectorParam(datiFS15, 1).stringValue();
        						       	String soggettoRichiedente = getSoggettoRichiedentePortale(comkey1,tipoOperazione);
        						       	soggettoRichiedente = StringUtils.stripToEmpty(soggettoRichiedente);
        						       	if(!"".equals(soggettoRichiedente)) {
        									//invoco la setAggiornamentiERP
        									String[] resAggErp= this.setAggiornamentoERP(codiceImpresa, soggettoRichiedente);
        									if(resAggErp[0]=="true") {
        										acquisito = true;//non devo aggiornare nulla
        									}else {
        										livEvento = 3;
        										errMsgEvento= resAggErp[1];
        									}
        						       	}else {
        						       		errMsgEvento = "Il soggetto richiedente risulta assente:manca la FS1 associata";
        									livEvento = 3;
        						       	}
        						      }else{
        						       		errMsgEvento = "Il marcatore risulta assente:manca la FS15 (stato 6) associata ";
        									livEvento = 1;
        						      }//if datiFS15
    			                }catch(Exception e){
									livEvento = 3;
    			                	errMsgEvento = "Errore inaspettato durante aggiornamento anagrafiche ERP (aggiornamenti STAB_TRG)";
    			                	logger.error(errMsgEvento,e);
        						}finally{
        						      try {
        						        LogEvento logEvento = new LogEvento();
        						        logEvento.setCodApplicazione("PG");
        						        logEvento.setLivEvento(livEvento);
        						        logEvento.setOggEvento(codiceImpresa);
        						        logEvento.setCodEvento(codEvento);
        						        logEvento.setDescr(descrEvento + " (cod.fornitore: " + codiceImpresa + ")");
        						        logEvento.setErrmsg(errMsgEvento);
        						        LogEventiUtils.insertLogEventi(logEvento);
        						      } catch (Exception le) {
        						        logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
        						      }
        						}
        			       
        					}//for
        				}//if 
        				
        			} catch (SQLException sqle) {
        	            errMsgEvento = sqle.getMessage();
        	            livEvento = 3;
        	            throw new GestoreException("Errore nella lettura delle comunicazioni per l'aggiornamento anagrafico ERP (albo fornitori)  ",null, sqle);
        			}
            	
            }//RAI
            
            if("CAV".equals(tipoWSERP)){
            	//stab_trg
            	Date date = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DAY_OF_MONTH, -3);
                // data di una settimana fa
                Date filterDate = cal.getTime();
                
                //seleziono gli aggiornamenti a partire da una certa data
                String selAggiornamentiDaEseguire = "select stab_val1,stab_operation from stab_trg"
                		+ " where stab_table = ? and stab_key1 = ? and (stab_operation = ? or stab_operation = ?)"
                		+ " and stab_date >= ?"
                		+ " order by stab_seq";
				try {
					
	                //credenziali per il ws
	                //recupero delle credenziali univoche
	                Long i_syscon = new Long(-1);
	                String servizio ="WSERP";
	                String[] credenziali = gestioneWSERPManager.wserpGetLogin(i_syscon, servizio);
	                String username = credenziali[0];
	                String password = credenziali[1];
					
					
					List aggiornamentiDaEseguire = sqlManager.getListVector(selAggiornamentiDaEseguire,  new Object[]{"IMPR","CODIMP","INS","UPD",filterDate});
					if(aggiornamentiDaEseguire != null && aggiornamentiDaEseguire.size()>0){
						for(int i = 0; i < aggiornamentiDaEseguire.size(); i++ ){
    						livEvento = 1;
    						errMsgEvento = null;
    						String codiceImpresa = (String) SqlManager.getValueFromVectorParam(aggiornamentiDaEseguire.get(i), 0).getValue();
    						String tipoOperazione = (String) SqlManager.getValueFromVectorParam(aggiornamentiDaEseguire.get(i), 1).getValue();
    						String idFornitore = null;
    						Vector<?> datiFornitore = this.sqlManager.getVector("select cgenimp from IMPR where codimp=?", 
    								new Object[] { codiceImpresa });
					          if (datiFornitore != null && datiFornitore.size() > 0) {
					        	  idFornitore = (String) SqlManager.getValueFromVectorParam(datiFornitore, 0).getValue();
					        	  idFornitore = StringUtils.stripToEmpty(idFornitore);
					        	  
					        	  if(!"".equals(idFornitore)) {
						        	try {					        	  
					        		  
				    						HashMap datiMask = new HashMap();
				    						datiMask.put("idFornitore", idFornitore);
				    						datiMask.put("ditta", codiceImpresa);
											WSERPFornitoreResType wserpFornitoreRes = cavWSERPManager.inviaDatiFornitore(username,password,datiMask);
											if(wserpFornitoreRes.isEsito()) {
												;//trattare per la tracciatura 
											}else {
		    									livEvento = 3;
		        			                	errMsgEvento = wserpFornitoreRes.getMessaggio();
		        			                	logger.error(errMsgEvento);
											}
										
		        			              }catch(Exception e){
		    									livEvento = 3;
		        			                	errMsgEvento = "Errore inaspettato durante aggiornamento anagrafiche ERP";
		        			                	logger.error(errMsgEvento,e);
		    	
		        			              }finally{
		    							    try {
		    							        LogEvento logEvento = new LogEvento();
		    							        logEvento.setCodApplicazione("PG");
		    							        logEvento.setLivEvento(livEvento);
		    							        logEvento.setOggEvento(codiceImpresa);
		    							        logEvento.setCodEvento(codEvento);
		    							        logEvento.setDescr(descrEvento + " (cod.fornitore: " + codiceImpresa + ")");
		    							        logEvento.setErrmsg(errMsgEvento);
		    							        LogEventiUtils.insertLogEventi(logEvento);
		    							    } catch (Exception le) {
		    							        logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
		    							    }
		        			              }
										
					        	  }else {
					        		  ;//se non hio cgenimp non serve tracciare nulla
					        	  }
					        	  
					        	  
					          }//verificato cgenimp

						}
						
					}
					
				} catch (SQLException sqlecav) {
    	            errMsgEvento = sqlecav.getMessage();
    	            livEvento = 3;
    	            throw new GestoreException("Errore nella lettura degli aggiornamenti anagrafici che richiedono riallineamento ERP",null, sqlecav);
				}
                
                
            	
            	
            }//CAV
            
            
            
            if (logger.isInfoEnabled()) {
                logger.info("Fine della procedura di aggiornamento anagrafiche ERP");
            }

        }
    }

    if (logger.isDebugEnabled())
      logger.debug("setDatiAnagrafiche: fine metodo");
  }
  
  
  private String getSoggettoRichiedentePortale(String comkey1, String tipoOperazione) throws GestoreException{
	  String soggettoRichiedente = null;
	  String res=null;
	  
      //variabili per tracciatura eventi
      int livEvento = 1;
      String codEvento = "GA_AGGANAG_ERP";
      String oggEvento = null;
      String descrEvento = "Aggiornamento anagrafico ERP (albo fornitori)";
      String errMsgEvento = "";
	  
	  
      //selezione delle FS SAP di interesse
      String idprg="PA";
      Long idcom = null;

      try {
    	  
              if(comkey1!=null) {

                //esamino le FS1 collegate con stato 6/19
                String selComunicazioniERP = "select IDCOM from W_INVCOM where IDPRG = ? AND COMKEY1= ? "
                		+ "AND COMTIPO = ? AND (COMSTATO = ? OR COMSTATO = ?) order by IDCOM desc";
                List ComunicazioniERP = sqlManager.getListVector(selComunicazioniERP,  new Object[]{idprg,comkey1,"FS1","6","19"});
      	        if(ComunicazioniERP != null && ComunicazioniERP.size()>0){
      	        	//prendo il primo
      	        	idcom = (Long) SqlManager.getValueFromVectorParam(ComunicazioniERP.get(0), 0).longValue();
      	        	//faro' in seguito la verifica che stia sulle tabelle di collegamento ad IMPR (LEGRAP,IMPDTE,IMPCOL,IMPAZI)
      	        	//anzi no secondo P.U.
                      //Vengono letti i documenti associati ad ogni occorrenza di di W_INVCOM
                      String selDocAllegato="select idprg,iddocdig from w_docdig where DIGENT = ? and digkey1 = ? and idprg = ? and dignomdoc = ? ";
                      String digent="W_INVCOM";
                      String idprgW_DOCDIG="PA";
                      Vector datiW_DOCDIG = null;
                      try {
                          datiW_DOCDIG = sqlManager.getVector(selDocAllegato,
                              new Object[]{digent, idcom.toString(), idprgW_DOCDIG, nomeFileXML_Aggiornamento});
                          
                          String idprgW_INVCOM = null;
                          Long iddocdig = null;
                          if(datiW_DOCDIG != null ){
                              if(((JdbcParametro)datiW_DOCDIG.get(0)).getValue() != null)
                                  idprgW_INVCOM = ((JdbcParametro) datiW_DOCDIG.get(0)).getStringValue();

                                if(((JdbcParametro)datiW_DOCDIG.get(1)).getValue() != null)
                                try {
                                  iddocdig = ((JdbcParametro) datiW_DOCDIG.get(1)).longValue();
                                } catch (GestoreException e2) {
                                  livEvento = 3;
                                  errMsgEvento = e2.getMessage();
                                  throw new GestoreException("Errore nella lettura della tabella W_DOCDIG ",null, e2);
                                }
                                
                                
                                //Lettura del file xml immagazzinato nella tabella W_DOCDIG
                                BlobFile fileAllegato = null;
                                try {
                                  fileAllegato = fileAllegatoManager.getFileAllegato(idprgW_INVCOM,iddocdig);
                                } catch (Exception e) {
                                  livEvento = 3;
                                  errMsgEvento = e.getMessage();
                                  throw new GestoreException("Errore nella lettura del file allegato presente nella tabella W_DOCDIG",null, e);
                                }
                                String xml=null;
                                if(fileAllegato!=null && fileAllegato.getStream()!=null){
                              	  xml = new String(fileAllegato.getStream());  
                              	   RegistrazioneImpresaDocument document;

                                       document = RegistrazioneImpresaDocument.Factory.parse(xml);
                                       soggettoRichiedente = document.getRegistrazioneImpresa().getSoggettoRichiedente();

                                }
                          }//if dati w_docdig
                          

                      } catch (SQLException e) {
                        livEvento = 3;
                        errMsgEvento = e.getMessage();
                        throw new GestoreException("Errore nella lettura della tabella W_DOCDIG ",null, e);
                      } catch (XmlException e) {
                          errMsgEvento = e.getMessage();
                          livEvento = 3;
                          throw new GestoreException("Errore nella lettura del file XML ",null, e);
                      }catch(Exception e){
                    	  livEvento = 3;
                    	  errMsgEvento = "Errore inaspettato durante la lettura del soggetto richiedente";
                    	  throw new GestoreException(errMsgEvento,null, e);
                      }finally {
                          //Tracciatura eventi
                    	  /*
                          try {
                            LogEvento logEvento = new LogEvento();
                            logEvento.setLivEvento(livEvento);
                            logEvento.setOggEvento(oggEvento);
                            logEvento.setCodEvento(codEvento);
                            logEvento.setDescr(descrEvento + " (usernome: " + comkey1 + ")");
                            logEvento.setErrmsg(errMsgEvento);
                            LogEventiUtils.insertLogEventi(logEvento);
                          } catch (Exception le) {
                            String messageKey = "errors.logEventi.inaspettataException";
                            logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
                          }
                          */
                        }
      	        }
                              
            	
                res = soggettoRichiedente;
            	
            }//comkey1 != null
              
        oggEvento = comkey1;
      } catch (SQLException e) {
        errMsgEvento = e.getMessage();
        livEvento = 3;
        throw new GestoreException("Errore nella lettura della tabella W_PUSER ",null, e);
      }

    		 
	  
	  
	  return res;
  }
  
  
  private String[] setAggiornamentoERP(String codiceImpresa,String soggettoRichiedente) throws GestoreException{
	  
	  String[] res = new String[2];
	  String msgError="";
	  String resVal="true";
	  String resMsg="";
	  Boolean ctrlInvio = true;
	  Boolean isUpdFornitore = false;
	  
      //credenziali per il ws
      //recupero delle credenziali univoche
      Long i_syscon = new Long(-1);
      String servizio ="WSERP";
      String[] credenziali = gestioneWSERPManager.wserpGetLogin(i_syscon, servizio);
      String username = credenziali[0];
      String password = credenziali[1];

      
      String selezioneImpreseDaAggiornare = "select i.NOMIMP,i.LOCIMP,i.NAZIMP,i.INDIMP,i.NCIIMP,"
      		+ "i.CAPIMP,i.PROIMP,i.NOMEST,i.EMAIIP,i.EMAI2IP,i.FAXIMP,i.TELIMP,i.INDWEB,i.DOFIMP,i.PIVIMP,"
      		+ "i.CGENIMP,i.NATGIUI,i.TIPIMP,i.DELAREG"
      		+ " from impr i"
      		+ " where i.codimp = ? ";
      String selezioneTecnicoDaAggiornare = "select cogtim,nometim,nomtim"
      		+ " from TEIM t"
      		+ " where cftim=?";			
     String selezioneTecnicoDaImpr = "select cognome,nome"
        		+ " from IMPR"
        		+ " where codimp = ? and tipimp = ?";			
      
      int apPar = soggettoRichiedente.indexOf("(");
      int chPar = soggettoRichiedente.indexOf(")");
      String cognomeTecnico = null;
      String nomeTecnico = null;
      String denominazioneTecnico = null;
      String cfSoggettoRichiedente = null;
      if(apPar>=0 && chPar>0) {
    	  cfSoggettoRichiedente = soggettoRichiedente.substring(apPar+1,chPar);  
      }
      
      //spezzare con TEIM
      
		try {
			List<?> listaImpreseDaAgg = sqlManager.getListVector(selezioneImpreseDaAggiornare, new Object[] {codiceImpresa});
			if(listaImpreseDaAgg.size() > 0){
				Vector<?> datiTecnicoDaAgg = sqlManager.getVector(selezioneTecnicoDaAggiornare, new Object[] {cfSoggettoRichiedente});
				if(datiTecnicoDaAgg!=null && datiTecnicoDaAgg.size()>0){
					cognomeTecnico =  SqlManager.getValueFromVectorParam(datiTecnicoDaAgg, 0).getStringValue();
					nomeTecnico =  SqlManager.getValueFromVectorParam(datiTecnicoDaAgg, 1).getStringValue();
					denominazioneTecnico =  SqlManager.getValueFromVectorParam(datiTecnicoDaAgg, 2).getStringValue();
				}else {
					datiTecnicoDaAgg = sqlManager.getVector(selezioneTecnicoDaImpr, new Object[] {codiceImpresa,Long.valueOf(6)});
					if(datiTecnicoDaAgg!=null && datiTecnicoDaAgg.size()>0){
						cognomeTecnico =  SqlManager.getValueFromVectorParam(datiTecnicoDaAgg, 0).getStringValue();
						nomeTecnico =  SqlManager.getValueFromVectorParam(datiTecnicoDaAgg, 1).getStringValue();
						denominazioneTecnico = cognomeTecnico + " " +  nomeTecnico;
					}
				}			        
				
				for (int g = 0; g < listaImpreseDaAgg.size(); g++){
					String oiNomeAzienda = SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 0).stringValue();
					String oiSedeLegale = SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 1).stringValue();
					Long nazL = (Long) SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 2).getValue();
					String oiSiglaNazione = null;
					if(nazL!= null) {
						String naz= nazL.toString();
						oiSiglaNazione = (String) sqlManager.getObject("select tab2tip from tab2" +
		                          " where tab2cod = ? and tab2d1 = ? ", new Object[] {"G_z27",naz});
						oiSiglaNazione = oiSiglaNazione.replace(oiSiglaNazione.substring(1,1),oiSiglaNazione.substring(1,1).toLowerCase());
					}else {
						oiSiglaNazione = "It";
					}
					String oiIndirizzo = SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 3).stringValue();
					String civico = SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 4).stringValue();
					civico = StringUtils.stripToEmpty(civico);
					oiIndirizzo = oiIndirizzo + " N. "+ civico;
					String oiCAP = SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 5).stringValue();
					String oiProvincia = SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 6).stringValue();
					String oiRagioneSociale = SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 7).stringValue();
					String oiUniqueName = StringUtils.stripToEmpty(oiNomeAzienda);
					oiUniqueName = oiUniqueName + "_addr";
					String oiEmailAzienda = SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 8).stringValue();
					String oiPecAzienda = SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 9).stringValue();
					String oiFaxAzienda = SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 10).stringValue();
					String oiTelefonoAzienda = SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 11).stringValue();
					String oiURL= SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 12).stringValue();
					String systemId = SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 13).stringValue();
					systemId = StringUtils.stripToEmpty(systemId);
					//SUCCESSIVAMENTE CONTROLLARE CON TIPO OPERAZIONE INS
					if("".equals(systemId)) {
						systemId="MAG_"+codiceImpresa;
						isUpdFornitore = true;
					}
					String oiPartitaIVA = SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 14).stringValue();
					
					String orgId= SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 15).stringValue();
					orgId = StringUtils.stripToEmpty(orgId);
					//SUCCESSIVAMENTE CONTROLLARE CON TIPO OPERAZIONE INS
					if("".equals(orgId)) {
						orgId="MAG_"+codiceImpresa;
						isUpdFornitore = true;
					}
					Long formaGiuridica = (Long) SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 16).getValue();
					String descFormaGiuridica = null;
					if(formaGiuridica != null) {
						descFormaGiuridica = (String) sqlManager.getObject("select tab1desc from tab1" +
		                          " where tab1cod = ? and tab1tip = ? ", new Object[] {"G_043",formaGiuridica});
						descFormaGiuridica = formaGiuridica.toString()+" - "+descFormaGiuridica;
					}
					Long tipoImpresa = (Long) SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 17).getValue();
					String descTipoImpresa = null;
					if(tipoImpresa != null) {
						descTipoImpresa = (String) sqlManager.getObject("select tab1desc from tab1" +
		                          " where tab1cod = ? and tab1tip = ? ", new Object[] {"Ag008",tipoImpresa});
						descTipoImpresa = tipoImpresa.toString()+" - "+descTipoImpresa;
					}
					Date dataCreazione = (Date) SqlManager.getValueFromVectorParam(listaImpreseDaAgg.get(g), 18).getValue();
					
					WSERPFornitoreType fornitore = new WSERPFornitoreType();
					fornitore.setLocalita(oiSedeLegale);
					fornitore.setNazionalita(oiSiglaNazione);
					fornitore.setIndirizzo(oiIndirizzo);
					fornitore.setCap(oiCAP);
					fornitore.setProvincia(oiProvincia);
					fornitore.setIban(oiUniqueName);
					fornitore.setRagioneSociale(oiNomeAzienda);
					fornitore.setEmail(oiEmailAzienda);
					fornitore.setPec(oiPecAzienda);
					if(oiFaxAzienda!=null){
						oiFaxAzienda = oiFaxAzienda.replaceAll(" ", "");
						if(StringUtils.isNumeric(oiFaxAzienda)){
							fornitore.setFax(oiFaxAzienda);
						}else {
							//throw new GestoreException("Il valore specificato per il fax deve essere numerico","AttributoNoNumerico");
							ctrlInvio=false;
							resVal="false";
							if("".equals(resMsg)) {
								resMsg = "Il valore specificato per il fax deve essere numerico";
							}else {
								resMsg = resMsg+"\nIl valore specificato per il fax deve essere numerico";	
							}
						}
					}
							
					if(oiTelefonoAzienda!=null) {
						oiTelefonoAzienda = oiTelefonoAzienda.replaceAll(" ", "");
						if(StringUtils.isNumeric(oiTelefonoAzienda)){
							fornitore.setTelefono(oiTelefonoAzienda);
						}else {
							//throw new GestoreException("Il valore specificato per il telefono deve essere numerico","AttributoNoNumerico");
							ctrlInvio=false;
							resVal="false";
							if("".equals(resMsg)) {
								resMsg = "Il valore specificato per il telefono deve essere numerico";
							}else {
								resMsg = resMsg+"\nIl valore specificato per il telefono deve essere numerico";	
							}
						}
					}

					fornitore.setUrl(oiURL);
					fornitore.setSystemIdFornitore(systemId);
					oiPartitaIVA=oiSiglaNazione+oiPartitaIVA;
					fornitore.setPartitaIva(oiPartitaIVA);
			        Calendar calDataCreazione = Calendar.getInstance();
			        if(dataCreazione!=null) {
			        	calDataCreazione.setTime(dataCreazione);
			        	fornitore.setDataCreazione(calDataCreazione);
			        }
					
					WSERPAnagraficaType[] tecniciArray = new WSERPAnagraficaType[1];
					WSERPAnagraficaType tecnico = new WSERPAnagraficaType();
					tecnico.setCognome(cognomeTecnico);
					tecnico.setNome(nomeTecnico);
					tecnico.setDenominazione(denominazioneTecnico);
					tecniciArray[0] = tecnico;
					fornitore.setTecniciArray(tecniciArray);
					
					//OrganizationImport
					if(ctrlInvio) {
						WSERPFornitoreResType wserpOiFornitoreRes = gestioneWSERPManager.wserpCreaFornitore(username, password, servizio, fornitore );
						Long statoOiId = wserpOiFornitoreRes.getStato();
						boolean esito = wserpOiFornitoreRes.isEsito();
						String oiMsg = wserpOiFornitoreRes.getMessaggio();
						oiMsg = StringUtils.stripToEmpty(oiMsg);
						if(Long.valueOf(0).equals(statoOiId) && esito==true) {//se e' andata a buon fine la prima operazione  
							//Aggiorno cgenimp e dofimp con i dati passati:
							if(isUpdFornitore) {
								this.gestioneWSERPManager.updFornitore("RAI",codiceImpresa,orgId,systemId);	
							}
						}else {
							resVal="false";
							resMsg = "Errore nell'invocazione del ws di aggiornamento anagrafiche erp ";
							if(!"".equals(oiMsg)) {
								resMsg +=":\n"+oiMsg;
							}
							
						}
						
					}
					
				}
			}
			
		} catch (SQLException e) {
			
			throw new GestoreException("Errore nella lettura delle imprese da aggiornare ", null, e);
		}
      
	  
	  res[0]=resVal;
	  res[1]=resMsg;
	
	  
	  return res;
	  
  }

}


