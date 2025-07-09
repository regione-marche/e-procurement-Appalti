package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class GestoreELPREMPUN extends AbstractGestoreEntita {

	static Logger logger = Logger.getLogger(GestoreELPREMPUN.class);
	private GenChiaviManager genChiaviManager;

	public GestoreELPREMPUN() {
		super(false);
	}

	@Override
	public void setRequest(HttpServletRequest request) {
		super.setRequest(request);
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
	public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
	}

	@Override
	public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

	  	String messageKey = null;
	  	String errMsgEvento = "";
	  	Integer livEvento = 1;
	  	String descrLog = "Calcolo punteggi ditte elenco operatori/catalogo con elementi premiali";
	  	String ngara = datiForm.getString("NGARA");
	  	String oggEvento = ngara;
	  	String codEvento = "GA_ELPREM_CALCOLA_PUN";

		try {
			Vector<JdbcParametro> datiGara = (Vector<JdbcParametro>) sqlManager.getVector("select garealbo.codgar,garealbo.ngara,gare1.delpremcont,gare1.elpremval from gare,gare1,garealbo" +
					" where gare.ngara=? and gare.ngara=gare1.ngara and gare.elencoe=garealbo.ngara", new Object[] {ngara});
			String codgarElenco = datiGara.get(0).getStringValue();
			String elencoe = datiGara.get(1).getStringValue();
			Date delpremcont = (Date) datiGara.get(2).getValue();
			String elpremval = datiGara.get(3).getStringValue();

			descrLog += " (codice elenco: "+elencoe+")";

			if (delpremcont != null) {
			  	throw new GestoreException ("Calcolo punteggi già eseguito",null);
			}

			List<Vector<JdbcParametro>> elementiPremialiGara = sqlManager.getListVector(GestoreELPREMGARE.queryQuesitiPunteggiELPREMGARE, new Object[] {ngara, ngara});
			HashMap<Long,HashMap<Long,Double>> mappaQuesiti = new HashMap<>();
			for(Vector<JdbcParametro> elementoPremialiGara : elementiPremialiGara) {
				Long idquesito = elementoPremialiGara.get(1).longValue();
				Long idopzione = elementoPremialiGara.get(2).longValue();
				Double pesoponr = ((Number)elementoPremialiGara.get(6).getValue()).doubleValue();

				if (mappaQuesiti.get(idquesito) == null) {
					mappaQuesiti.put(idquesito, new HashMap<>());
				}
				mappaQuesiti.get(idquesito).put(idopzione, pesoponr);
			}

			Timestamp dataAttuale = new Timestamp(Calendar.getInstance().getTimeInMillis());

			errMsgEvento = "Data conteggio:\r\n" + dataAttuale;
			errMsgEvento += "\r\n" + "Considera validità elementi premiali:";
			errMsgEvento += "\r\n" + ("1".equals(elpremval) ? "Si" : "No");
			errMsgEvento += "\r\n" + "Elenco operatori:";

			HashMap<String,Double> mappaPunteggiDitte = new HashMap<>();

			List<Vector<JdbcParametro>> listaOperatoriInElenco = sqlManager.getListVector("select dittao from ditg where ngara5=?", new Object[] {elencoe});
			for (Vector<JdbcParametro> datiOperatore : listaOperatoriInElenco) {
				String dittao = datiOperatore.get(0).getStringValue();
				mappaPunteggiDitte.put(dittao, new Double(0));
			}

			List<Vector<JdbcParametro>> elementiPremialiDitte = sqlManager.getListVector("select codimp,idquesito,idopzione,datafinval" +
					" from elpremditg where ngara=? and idquesito in (select idquesito from elpremgare where ngara=?)", new Object[] {elencoe, ngara});
			for (Vector<JdbcParametro> elementoPremialeDitta : elementiPremialiDitte) {
				String codimp = elementoPremialeDitta.get(0).stringValue();
				Long idquesito = elementoPremialeDitta.get(1).longValue();
				Long idopzione = elementoPremialeDitta.get(2).longValue();
				Timestamp dataFineValidita = elementoPremialeDitta.get(3).dataValue();

				if (!"1".equals(elpremval) || dataFineValidita == null || dataAttuale.before(dataFineValidita)) {
					Double punteggioQuesito = 0D;
					if (mappaQuesiti.get(idquesito).containsKey(idopzione)) {
						punteggioQuesito =  mappaQuesiti.get(idquesito).get(idopzione);
					} else {
						Long opzioneMassima = Collections.max(mappaQuesiti.get(idquesito).keySet());
						if (opzioneMassima < idopzione) {
							punteggioQuesito =  mappaQuesiti.get(idquesito).get(opzioneMassima);
						} else {
							throw new GestoreException("Opzione non trovata", null);
						}
					}
					mappaPunteggiDitte.put(codimp, mappaPunteggiDitte.get(codimp) + punteggioQuesito);
				}
			}

			genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager", this.getServletContext(), GenChiaviManager.class);
			for (String dittao : mappaPunteggiDitte.keySet()) {
				Long id = Long.valueOf(genChiaviManager.getNextId("ELPREMPUN"));
				Double puntot = mappaPunteggiDitte.get(dittao);
				if (puntot != null) {
					puntot = Math.round(puntot * 100.0) / 100.0;
				}

				errMsgEvento += "\r\n" + dittao + " " + puntot;

				DataColumnContainer containerELPREMPUN = new DataColumnContainer(new DataColumn[] {
						new DataColumn("ELPREMPUN.ID", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, id)),
						new DataColumn("ELPREMPUN.CODGAR", new JdbcParametro(JdbcParametro.TIPO_TESTO, codgarElenco)),
						new DataColumn("ELPREMPUN.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, elencoe)),
						new DataColumn("ELPREMPUN.CODIMP", new JdbcParametro(JdbcParametro.TIPO_TESTO, dittao)),
						new DataColumn("ELPREMPUN.NGARAG", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)),
						new DataColumn("ELPREMPUN.PUNTOT", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, puntot))
				});
				containerELPREMPUN.insert("ELPREMPUN", this.sqlManager);
			}
			sqlManager.update("update gare1 set delpremcont=? where ngara=?", new Object[] {dataAttuale, ngara});
			this.getRequest().setAttribute("calcoloEseguito","1");
		} catch (GestoreException | SQLException e) {
			livEvento = 3;
			errMsgEvento = e.getMessage();
			throw new GestoreException("Errore nella conferma dati e calcolo punteggi ditte", null, e);
		} finally {
			try {
				LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
				logEvento.setLivEvento(livEvento);
				logEvento.setOggEvento(oggEvento);
				logEvento.setCodEvento(codEvento);
				logEvento.setDescr(descrLog);
				logEvento.setErrmsg(errMsgEvento);
				LogEventiUtils.insertLogEventi(logEvento);
			} catch (Exception le) {
				messageKey = "errors.logEventi.inaspettataException";
				logger.error(this.resBundleGenerale.getString(messageKey), le);
			}
		}
	}

	@Override
	public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
		String messageKey = null;
		String errMsgEvento = "";
		Integer livEvento = 1;
		String descrLog = "Annulla calcolo punteggi ditte elenco operatori/catalogo con elementi premiali";
		String ngara = null;
		if (datiForm.isColumn("NGARA")) {
			ngara = datiForm.getColumn("NGARA").getValue().toString();
		}
		String oggEvento = ngara;
		String codEvento = "GA_ELPREM_ANNULLA_PUN";
		try {
			String elencoe = (String) sqlManager.getObject("select elencoe from gare where ngara=?", new Object[] { ngara });
			descrLog += " (codice elenco: "+elencoe+")";
			geneManager.deleteTabelle(new String[] {"ELPREMPUN"}, "ngarag=?", new Object[] { ngara });
			sqlManager.update("update gare1 set delpremcont=null where ngara=?", new Object[] {ngara});
			this.getRequest().setAttribute("annullaCalcoloEseguito","1");
		} catch (GestoreException | SQLException e) {
			livEvento = 3;
			errMsgEvento = e.getMessage();
			throw new GestoreException("Errore nell'annullamento calcolo punteggi ditte", null, e);
		} finally {
			try {
				LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
				logEvento.setLivEvento(livEvento);
				logEvento.setOggEvento(oggEvento);
				logEvento.setCodEvento(codEvento);
				logEvento.setDescr(descrLog);
				if (livEvento.equals(3)) {
					logEvento.setErrmsg(errMsgEvento);
				}
				LogEventiUtils.insertLogEventi(logEvento);
			} catch (Exception le) {
				messageKey = "errors.logEventi.inaspettataException";
				logger.error(this.resBundleGenerale.getString(messageKey), le);
			}
		}
	}

}
