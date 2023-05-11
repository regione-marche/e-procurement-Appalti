/*
 * Created on 16-Feb-2022
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
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AtacManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Vector;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standart per la pagina "Dettaglio offerta prezzi della ditta"
 *
 * @author Cristian Febas
 */
public class GestoreV_GCAP_DPRE_OFFERTA extends AbstractGestoreEntita {


	@Override
  public String getEntita() {
		return "V_GCAP_DPRE";
	}

	public GestoreV_GCAP_DPRE_OFFERTA() {
	    super(false);
	  }


	  public GestoreV_GCAP_DPRE_OFFERTA(boolean isGestoreStandard) {
	    super(isGestoreStandard);
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
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
		/*
		//Si cancellano le occorrenze in GCAP e DPRE, GCAP_EST, GCAP_SAN e DPRE_SAN
		String ngara = datiForm.getString("V_GCAP_DPRE.NGARA");
	  Long contaf = datiForm.getLong("V_GCAP_DPRE.CONTAF");
	  String dittao = datiForm.getString("V_GCAP_DPRE.COD_DITTA");
	 

		try {
			//Poiche' il gestore non e' standard si deve gestire l'eliminazione
			// delle occorrenze del generatore attributi, dei documenti associati e
			// delle note
			this.geneManager.eliminaOccorrenzeGeneratoreAttributi(
					"V_GCAP_DPRE","NGARA = ? and CONTAF = ? and COD_DITTA =?",
					new Object[]{ngara, contaf,dittao});
			this.geneManager.eliminaOccorrenzeOggettiAssociati(
					"V_GCAP_DPRE","NGARA = ? and CONTAF = ? and COD_DITTA =?",
					new Object[]{ngara, contaf,dittao});
			this.geneManager.eliminaOccorrenzeNoteAvvisi(
					"V_GCAP_DPRE","NGARA = ? and CONTAF = ? and COD_DITTA =?",
					new Object[]{ngara, contaf,dittao});

			this.getSqlManager().update(
					"delete from GCAP where NGARA = ? and CONTAF = ? and DITTAO =?",
					new Object[] { ngara, contaf,dittao});

			this.getSqlManager().update(
					"delete from GCAP_EST where NGARA = ? and CONTAF = ?",
					new Object[] { ngara, contaf});

			this.getSqlManager().update(
					"delete from GCAP_SAN where NGARA = ? and CONTAF = ?",
          new Object[] { ngara, contaf});

			this.getSqlManager().update(
					"delete from DPRE_SAN where NGARA = ? and CONTAF = ?",
					new Object[] { ngara, contaf});

			this.getSqlManager().update(
					"delete from DPRE where NGARA = ? and CONTAF = ? and DITTAO =?",
					new Object[] { ngara, contaf,dittao});
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nella cancellazione delle occorrenze di GACP e DPRE con chiave "
					+ "NGARA = " + ngara + ", CONTAF = " + contaf.toString()
					+ ", DITTAO = " + dittao, null, e);
		}
		*/
	}

	//Gestione dell'inserimento nelle entita' GCAP e DPRE
	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
		/*

		try {
			//Si deve calcolare il valore della chiave numerica CONTAF
			String select="select max(CONTAF) from GCAP where ngara = ?";
			String ngara = datiForm.getColumn("GCAP.NGARA").getValue().stringValue();
			Vector ret = this.getSqlManager().getVector(select.toString(), new Object[]{ngara});
			Long max = SqlManager.getValueFromVectorParam(ret, 0).longValue();
			if (max == null) max = new Long(0);

			//Si impostano le chiavi di GCAP
			datiForm.getColumn("GCAP.NGARA").setChiave(true);
			datiForm.getColumn("GCAP.CONTAF").setChiave(true);
			datiForm.setValue("GCAP.CONTAF", new Long(max.longValue() + 1));
			datiForm.setValue("V_GCAP_DPRE.CONTAF", new Long(max.longValue() + 1));

			//Si devono settare a null i valori originali dei campi
			// NORVOC,DITTAO,CLASI1,SOLSIC e SOGRIB, poichè i loro valori
			//vengono impostato nella pagina. Se non si fa ciò non vengono
			//visti come modificati e non vengono salvati
			//lo stesso discorso vale per NGARA
			datiForm.getColumn("GCAP.NGARA").setObjectOriginalValue(null);
			datiForm.getColumn("GCAP.NORVOC").setObjectOriginalValue(null);
			datiForm.getColumn("GCAP.DITTAO").setObjectOriginalValue(null);

			if(datiForm.isColumn("GCAP.CLASI1"))
			  datiForm.getColumn("GCAP.CLASI1").setObjectOriginalValue(null);
			if(datiForm.isColumn("GCAP.SOLSIC"))
			  datiForm.getColumn("GCAP.SOLSIC").setObjectOriginalValue(null);
			if(datiForm.isColumn("GCAP.SOGRIB"))
			  datiForm.getColumn("GCAP.SOGRIB").setObjectOriginalValue(null);

			//Il valore del campo fittizzio QUANTI_F va riportata in
			//QUANTI.GCAP
			boolean quantitaModificata = false;
			if (datiForm.isColumn("V_GCAP_DPRE.QUANTI_F") && datiForm.isModifiedColumn("V_GCAP_DPRE.QUANTI_F")){
				quantitaModificata = true;
				datiForm.setValue("GCAP.QUANTI", datiForm.getColumn("V_GCAP_DPRE.QUANTI_F").getValue().doubleValue());
			}

            //Il valore del campo fittizzio PERCIVA_F va riportata in PERCIVA.GCAP
            boolean ivaModificata = false;
            if (datiForm.isColumn("V_GCAP_DPRE.PERCIVA_F") && datiForm.isModifiedColumn("V_GCAP_DPRE.PERCIVA_F")){
                ivaModificata = true;
                datiForm.setValue("GCAP.PERCIVA", datiForm.getColumn("V_GCAP_DPRE.PERCIVA_F").getValue().doubleValue());
            }

            datiForm.insert("GCAP", this.geneManager.getSql());

			PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
					this.getServletContext(), PgManager.class);

			Long tipforn = pgManager.prelevaTIPFORN(ngara);

			//Aggiornamento di GCAP_EST
			 if (tipforn== null || tipforn.longValue()==3 || tipforn.longValue()==98)
			   pgManager.aggiornaGCAP_EST("V_GCAP_DPRE", datiForm);

			//Inserimento dei dati in DPRE
			if (datiForm.isModifiedTable("DPRE") ||(quantitaModificata && datiForm.getColumn("DPRE.IMPOFF").getValue().doubleValue() != null) || ivaModificata){
				datiForm.getColumn("DPRE.NGARA").setChiave(true);
				datiForm.getColumn("DPRE.CONTAF").setChiave(true);
				datiForm.getColumn("DPRE.DITTAO").setChiave(true);


				datiForm.setValue("DPRE.CONTAF", new Long(max.longValue() + 1));
				//Se non metto a null l'OriginalValue, vengono scartati nell'insert
				//i valori dei due campi
				datiForm.getColumn("DPRE.NGARA").setObjectOriginalValue(null);
				datiForm.getColumn("DPRE.DITTAO").setObjectOriginalValue(null);
				datiForm.insert("DPRE", this.geneManager.getSql());
			}

			//Aggiornamento di GCAP_SAN e DPRE_SAN
			if (tipforn!= null && (tipforn.longValue() == 1 || tipforn.longValue()==2 || tipforn.longValue()==98)){
			  pgManager.aggiornaGCAP_SAN("V_GCAP_DPRE", datiForm);
			  pgManager.aggiornaDPRE_SAN("V_GCAP_DPRE", datiForm);
			}
		} catch (SQLException e) {
			  throw new GestoreException("Errore nel salvataggio dei dati in GCAP e DPRE",
			          null, e);
		}
		*/
	}


	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm )
			throws GestoreException {


	   //Si controlla se l'aggiornamento viene chiamato dalla lista
		//o dalla scheda
		String updateDaLista = null;
		String ngara = null;
        Long contaf = null;
		String isprodneg = null;

		updateDaLista = UtilityStruts.getParametroString(this.getRequest(), "AGGIORNA_DA_LISTA");
		ngara = UtilityStruts.getParametroString(this.getRequest(), "ngara");
		contaf = Long.parseLong(UtilityStruts.getParametroString(this.getRequest(), "contaf"));
		isprodneg = UtilityStruts.getParametroString(this.getRequest(), "isprodneg");
		
		String aggiudicatario = UtilityStruts.getParametroString(this.getRequest(), "aggiudicatario");
		aggiudicatario = UtilityStringhe.convertiNullInStringaVuota(aggiudicatario);

		//Aggiornamento dalla scheda
		if (updateDaLista == null){
			boolean quantitaModificata = false;
			boolean ivaModificata = false;
			boolean esisteDPRE = false;
			if (datiForm.isModifiedColumn("V_GCAP_DPRE.QUANTI_F")){
				quantitaModificata = true;
				datiForm.setValue("DPRE.QUANTI", datiForm.getColumn("V_GCAP_DPRE.QUANTI_F").getValue().doubleValue());
			}


            if (datiForm.isModifiedColumn("V_GCAP_DPRE.PERCIVA_F")){
                ivaModificata = true;
                datiForm.setValue("DPRE.PERCIVA", datiForm.getColumn("V_GCAP_DPRE.PERCIVA_F").getValue().doubleValue());
            }

            //Verifica se esiste l'occorrenza in DPRE
			String select="select count(NGARA) from DPRE where ngara = ? and contaf = ? and dittao = ?";
			ngara = datiForm.getColumn("GCAP.NGARA").getValue().stringValue();
			contaf = datiForm.getColumn("GCAP.CONTAF").getValue().longValue();
			String cod_ditta = datiForm.getColumn("V_GCAP_DPRE.COD_DITTA").getValue().stringValue();
			Vector ret;
			try {
				ret = this.getSqlManager().getVector(select.toString(), new Object[]{ngara, contaf,cod_ditta });
				Long max = SqlManager.getValueFromVectorParam(ret, 0).longValue();
				if (max != null && max.longValue()>0)
					esisteDPRE = true;

				if (datiForm.isModifiedTable("DPRE") || quantitaModificata || ivaModificata){
					if (esisteDPRE)
						datiForm.update("DPRE", this.geneManager.getSql());
					else{
						datiForm.getColumn("DPRE.NGARA").setChiave(true);
						datiForm.getColumn("DPRE.CONTAF").setChiave(true);
						datiForm.getColumn("DPRE.DITTAO").setChiave(true);

						datiForm.setValue("DPRE.CONTAF", contaf);
						//Se non metto a null l'OriginalValue, vengono scartati nell'insert
						//i valori dei due campi
						datiForm.getColumn("DPRE.NGARA").setObjectOriginalValue(null);
						datiForm.getColumn("DPRE.DITTAO").setObjectOriginalValue(null);

						datiForm.insert("DPRE", this.geneManager.getSql());
					}
				}

				if (datiForm.isModifiedTable("GCAP"))
					datiForm.update("GCAP", this.geneManager.getSql());

				PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
						this.getServletContext(), PgManager.class);

				Long tipforn = pgManager.prelevaTIPFORN(ngara);

				//Aggiornamento di GCAP_EST
				if (tipforn== null || tipforn.longValue()==3 || tipforn.longValue()==98)
				  pgManager.aggiornaGCAP_EST("V_GCAP_DPRE", datiForm);

				//Aggiornamento GCAP_SAN e DPRE_SAN
				 if (tipforn!= null && (tipforn.longValue() == 1 || tipforn.longValue()==2 || tipforn.longValue()==98)){
                  pgManager.aggiornaGCAP_SAN("V_GCAP_DPRE", datiForm);
                  pgManager.aggiornaDPRE_SAN("V_GCAP_DPRE", datiForm);

                  String acquisito = null;
                }



			} catch (SQLException e) {
				throw new GestoreException("Errore nel salvataggio dei dati in DPRE",
				          null, e);
			}
		} else if("2".equals(updateDaLista)){

		  //Mettere un controllo su :..costruzione del DCC solo se e' cambiato qualcosa
			int numeroProdotti = 0;
		    String numProdotti = this.getRequest().getParameter("numeroProdotti");
		    if(numProdotti != null && numProdotti.length() > 0) {
		    	numeroProdotti =  UtilityNumeri.convertiIntero(numProdotti).intValue();
		    }
		      
		    
		    String cod_ditta = null;
			  for (int i = 1; i <= numeroProdotti; i++) {
			      //if (!"".equals(aggiudicatario)){
		            cod_ditta = datiForm.getColumn("V_GCAP_DPRE.COD_DITTA_" + i).getValue().stringValue();
		            Double qtaOrdinabile = datiForm.getColumn("V_GCAP_DPRE.QTAORDINABILE_" + i).getValue().doubleValue();
		            Double qtaOrdinata = datiForm.getColumn("V_GCAP_DPRE.QTAORDINATA_" + i).getValue().doubleValue();
		            if(cod_ditta.equals(aggiudicatario) || qtaOrdinata!= null ) {
		            	//verifico che non avesse già aggiudicato qualcosa
		            	String isProdottiAggiudicatiDitta = datiForm.getColumn("IS_PRODOTTI_AGGIUDICATI_DITTA_" + i).getValue().stringValue();
		            	Double originalQtaOrdinata = datiForm.getColumn("V_GCAP_DPRE.QTAORDINATA_" + i).getOriginalValue().doubleValue();
		            	if(originalQtaOrdinata== null || Double.valueOf(0).equals(originalQtaOrdinata)) {
		            		if("true".equals(isProdottiAggiudicatiDitta)) {
		            			throw new GestoreException("Non risulta possibile assegnare ad un offerente che ha già prodotti assegnati","prodotti.assegnati",new Exception());	
		            		}
		            	}
		                Vector elencoCampi = new Vector();
			            elencoCampi.add(new DataColumn("DPRE.NGARA",
			                new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
			            elencoCampi.add(new DataColumn("DPRE.CONTAF",
			                new JdbcParametro(JdbcParametro.TIPO_NUMERICO, contaf)));
			            elencoCampi.add(new DataColumn("DPRE.DITTAO",
			                new JdbcParametro(JdbcParametro.TIPO_TESTO, cod_ditta)));
			            if(cod_ditta.equals(aggiudicatario)) {
				            elencoCampi.add(new DataColumn("DPRE.QTAORDINATA",
					                new JdbcParametro(JdbcParametro.TIPO_TESTO, qtaOrdinabile)));
			            }else {
				            elencoCampi.add(new DataColumn("DPRE.QTAORDINATA",
					                new JdbcParametro(JdbcParametro.TIPO_TESTO, null)));
			            	
			            }
	
				        DataColumnContainer containerDPRE = new DataColumnContainer(elencoCampi);
	
				        containerDPRE.getColumn("DPRE.NGARA").setChiave(true);
		                containerDPRE.getColumn("DPRE.CONTAF").setChiave(true);
		                containerDPRE.getColumn("DPRE.DITTAO").setChiave(true);
	
		                //Verifica se esiste l'occorrenza in DPRE
		                String select="select count(NGARA) from DPRE where ngara = ? and contaf = ? and dittao = ?";
		                boolean esisteDPRE = false;
		                Vector ret;
		                try {
		                    ret = this.getSqlManager().getVector(select.toString(), new Object[]{ngara, contaf,cod_ditta });
		                    Long max = SqlManager.getValueFromVectorParam(ret, 0).longValue();
		                    if (max != null && max.longValue()>0)
		                        esisteDPRE = true;
		                    if (esisteDPRE){
		                      containerDPRE.getColumn("DPRE.NGARA").setObjectOriginalValue(ngara);
		                      containerDPRE.getColumn("DPRE.CONTAF").setObjectOriginalValue(contaf);
		                      containerDPRE.getColumn("DPRE.DITTAO").setObjectOriginalValue(cod_ditta);
		                      containerDPRE.getColumn("DPRE.QTAORDINATA").setObjectOriginalValue("-1");
		                      containerDPRE.update("DPRE", sqlManager);
		                    }
		                    else{
		                      //containerDPRE.insert("DPRE", sqlManager);
		                    }
		    		      } catch (SQLException e) {
		    		           throw new GestoreException("Errore nell'aggiornamento dei dati in DPRE",null, e);
		    		      }
		            }
	
	              //}
			  }
			  if(ngara != null && contaf != null && isprodneg != null && "1".equals(isprodneg)) {
			    try {
                  sqlManager.update("update GCAP set isprodneg = ? where ngara = ? and contaf = ?",new Object[]{null, ngara, contaf});
                } catch (SQLException e) {
                  throw new GestoreException("Errore nell'aggiornamento del dato isprodneg in GCAP",null, e);
                }
			  }
			  
			  try {
			    AtacManager atacManager = (AtacManager)UtilitySpring.getBean("atacManager", this.getServletContext(), AtacManager.class);
	            atacManager.controllaStatoRicercaDiMercato(ngara);
              } catch (SQLException e) {
                throw new GestoreException("Errore nell'aggiornamento dello stato della Gara",null, e);
              }
			  
		}

	}






}