package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.QuartzConfigManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestionePATManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class GestorePubblicaSuPortalePAT extends AbstractGestoreEntita {

	static Logger logger = Logger.getLogger(GestorePubblicaSuPortalePAT.class);

	private GestionePATManager gestionePATManager;
	private PgManagerEst1 pgManagerEst1;

	@Override
	public void setRequest(HttpServletRequest request) {
		super.setRequest(request);
		gestionePATManager = (GestionePATManager) UtilitySpring.getBean("gestionePATManager", this.getServletContext(), GestionePATManager.class);
		pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1", this.getServletContext(), PgManagerEst1.class);
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
		HashMap<String,String> ufficiIntestatariConfigurati = gestionePATManager.getUfficiIntestatariConfigurati(CostantiAppalti.PROP_WSBANDI_TIPO);
		// Selezione pubblicazioni bando da processare
		String[] tippubgDaPubblicare = new String[]{"11", "13", "12"};
		boolean isAttivaConfigurazioneStandard = GestionePATManager.isConfiguratoPerPAT(null);
		ArrayList<String> listaUfficiConfigurazioneErrata = new ArrayList<>();

		String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
		String jobClass = org.apache.commons.lang.StringUtils.substringBefore(GestorePubblicaSuPortalePAT.class.getName(), "$$");

		String msgErrori = "";
		for (String tippub : tippubgDaPubblicare) {
			List<Vector> listaLottiDaPubblicare = null;
			try {
				listaLottiDaPubblicare = this.sqlManager.getListVector(getQueryPubblicazioni(tippub), new Object[] {GestionePATManager.TIPRIC});
			} catch (SQLException e) {
				throw new GestoreException("Errore nella lettura dei bandi pubblicati non presenti in GARUUID", null, e);
			}
			if (listaLottiDaPubblicare != null && !listaLottiDaPubblicare.isEmpty()) {
                for (Vector<?> datiLotto : listaLottiDaPubblicare) {
                    String codgar = ((JdbcParametro) datiLotto.get(0)).stringValue();
                    Timestamp datpub = ((JdbcParametro) datiLotto.get(1)).dataValue();
                    String ngara = ((JdbcParametro) datiLotto.get(2)).getStringValue();
                    Long genereGara = ((JdbcParametro) datiLotto.get(3)).longValue();
                    String codcig = ((JdbcParametro) datiLotto.get(4)).stringValue();
                    Long iterga = ((JdbcParametro) datiLotto.get(5)).longValue();
                    String cenint = ((JdbcParametro) datiLotto.get(6)).stringValue();

                    String tipoConfigPerUfficio = ufficiIntestatariConfigurati.get(cenint);
                    if ((StringUtils.isEmpty(tipoConfigPerUfficio) && !isAttivaConfigurazioneStandard) || (!("2".equals(tipoConfigPerUfficio)) && StringUtils.isNotEmpty(tipoConfigPerUfficio))) {
                        continue;
                    }
					try {
						if (pgManagerEst1.isAffidamentoDiretto(codgar) && "13".equals(tippub)) {
							continue; //non pubblico gli inviti per gli affidamenti diretti
						}
					} catch (SQLException e) {
						throw new GestoreException("Errore nella lettura del tipo di gara", null, e);
					}
                    if (genereGara != null && genereGara > 100) {
                        genereGara = genereGara / 100;
                    }
					String dicituraGaraLotto = ((Long) 2L).equals(genereGara) ? "della gara ":"del lotto ";
					boolean commitTransaction = false;
					TransactionStatus status = null;
					boolean locked = false;
					String jobMethod = "PubblicazionePAT." + codcig;
					String job = jobClass + "." + jobMethod;
					QuartzConfigManager manager = null;
                    try {
                        if (listaUfficiConfigurazioneErrata.contains(cenint) || Arrays.asList(GestionePATManager.controlloConfigurazionePAT(cenint)).contains(false)) {
                            if (!listaUfficiConfigurazioneErrata.contains(cenint))
                                listaUfficiConfigurazioneErrata.add(cenint);
                            throw new Exception("L'ufficio intestatario con codice interno: " + cenint + " è configurato incorrettamente");
                        }
						if (StringUtils.isEmpty(codcig)) {
							throw new Exception("Non è stato inserito il codice CIG");
						}
						status = sqlManager.startTransaction();

                        if (!gestionePATManager.trovatoLottoPubblicatoManualmente(codgar, ngara, codcig, this.getRequest())) {
							try {
								manager = (QuartzConfigManager) UtilitySpring.getBean("quartzConfigManager", SpringAppContext.getServletContext(), QuartzConfigManager.class);
							}catch (IllegalStateException e){
								//errore dovuto al riavvio della webapp mentre un job è schedulato
								throw new Exception("Errore durante l'apertura della transazione");
							}
							try {
								String server = InetAddress.getLocalHost().getHostName();
								String node = System.getProperty("jvmRoute");
								Long countQuartzLock = (Long) sqlManager.getObject("select count(*) from w_quartzlock where job=? and codapp=?", new Object[] {job,codapp});
								if (countQuartzLock != null && countQuartzLock > 0) {
									throw new Exception("La pubblicazione è già in corso");
								}
								// tentare inserimento lock esclusivo
								locked = manager.insertQuartzLock(codapp, jobClass, jobMethod, server, node);
								if (!locked) {
									throw new Exception("Errore durante l'apertura della transazione");
								}
								gestionePATManager.pubblicaLotto(this.getRequest(), codgar, ngara, genereGara, datpub);
							} catch (UnknownHostException e) {
								throw new Exception ("Problemi di reperimento del nome server");
							}
                        }
						commitTransaction = true;
                    } catch (Exception e) {
						if (!msgErrori.contains(e.getMessage())) msgErrori += "Impossibile procedere all'esecuzione della pubblicazione " + dicituraGaraLotto + ngara + ": " + e.getMessage() + "\r\n";
					} finally {
						if (status != null) {
							try {
								if (commitTransaction) {
									sqlManager.commitTransaction(status);
								} else {
									sqlManager.rollbackTransaction(status);
								}
							} catch (SQLException e) {
								logger.error("Errore durante la chiusura della transazione", e);
							}
						}
						if (locked) {
							manager.deleteQuartzLock(codapp, jobClass, jobMethod);
						}
					}
                }
			}
		}
		this.getRequest().setAttribute("pubblicazioneEseguita", "1");
		if (StringUtils.isEmpty(msgErrori))
			msgErrori = null;
		this.getRequest().setAttribute("messaggi", msgErrori);
	}

	@Override
	public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
	}

	@Override
	public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

	}

	@Override
	public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
	}

	private String getQueryPubblicazioni(String tippub) {
		if ("11".equals(tippub) || "13".equals(tippub)) {
			return "select pubbli.codgar9, pubbli.datpub, gare.ngara, v_gare_genere.genere, gare.codcig, torn.iterga, torn.cenint" +
					" from pubbli inner join torn on pubbli.codgar9=torn.codgar and torn.vernpa is not null" +
					" inner join gare on gare.codgar1=pubbli.codgar9 and gare.ngara != gare.codgar1 and gare.preced is null" +
					" inner join v_gare_genere on v_gare_genere.codice=gare.ngara" +
					" where (pubbli.tippub=" + tippub + " and not exists (select garuuid.id from garuuid where garuuid.ngara=gare.ngara and garuuid.tipric=?))" +
					" and exists (select codcig from gare where codgar1=pubbli.codgar9 and codgar1 != ngara and codcig is not null)";
		} else if ("12".equals(tippub)) {
			return "select torn.codgar, pubg.dinpubg, pubg.ngara, v_gare_genere.genere, gare.codcig, torn.iterga, torn.cenint" +
					" from pubg inner join gare on pubg.ngara=gare.ngara and gare.ngara != gare.codgar1 and gare.preced is null inner join torn on gare.codgar1=torn.codgar and torn.vernpa is not null" +
					" inner join v_gare_genere on v_gare_genere.codice=gare.ngara" +
					" where (pubg.tippubg=" + tippub + " and not exists (select garuuid.id from garuuid where garuuid.ngara=gare.ngara and garuuid.tipric=?))" +
					" and exists (select codcig from gare where gare.codgar1=torn.codgar and codgar1 != ngara and codcig is not null)";
		}
		return null;
	}

}
