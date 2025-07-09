package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class GestorePubblicaAttiSuPortale extends AbstractGestorePreload {

	SqlManager sqlManager = null;
	GestioneWSDMManager gestioneWSDMManager = null;

	public GestorePubblicaAttiSuPortale(BodyTagSupportGene tag) {
		super(tag);
	}

	@Override
	public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {
	}

	public void inizializzaManager(PageContext page) {
		sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", page, SqlManager.class);
		gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager", page, GestioneWSDMManager.class);
	}

	@Override
	public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda) throws JspException {

		inizializzaManager(page);

		String codgar = page.getRequest().getParameter("codgar");
		String ngara = page.getRequest().getParameter("ngara");
		String idconfi = page.getRequest().getParameter("idconfi");

		String messaggio = "";
	    String controlloSuperato="SI";
	    String msgConferma = "";
		String profilo = (String) page.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);
		String integrazioneWSDM = "0";
		boolean abilitatoInvioMailDocumentale = false;

		try {
			Date dpubatti = (Date) sqlManager.getObject("select dpubatti from gare1 where ngara=?", new Object[] {ngara});
			if (dpubatti != null) {
				page.setAttribute("dpubatti", dpubatti, PageContext.REQUEST_SCOPE);
				Long countDocumentiDaPubblicare = (Long) sqlManager.getObject("select count(*) from g1documatti where ngara=? and statodoc is null and isarchi is null or isarchi='2'", new Object[] {ngara});
				if (countDocumentiDaPubblicare == null || countDocumentiDaPubblicare.equals(0L)) {
					controlloSuperato = "NO";
					messaggio += "<br> Non ci sono documenti da pubblicare";
				}

				if ("NO".equals(controlloSuperato)) {
					page.setAttribute("controlloSuperato", controlloSuperato, PageContext.REQUEST_SCOPE);
					page.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);
				}
				return;
			}

			boolean isIntegrazioneWSDMAttivaValida = gestioneWSDMManager.isIntegrazioneWSDMAttivaValida(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO, idconfi);
			if (isIntegrazioneWSDMAttivaValida) {
				integrazioneWSDM = "1";
				abilitatoInvioMailDocumentale = gestioneWSDMManager.abilitatoInvioMailDocumentale("FASCICOLOPROTOCOLLO", idconfi);
			}

			String tipoWSDM = null;

			if ("1".equals(integrazioneWSDM)) {
				WSDMConfigurazioneOutType configurazione = gestioneWSDMManager.wsdmConfigurazioneLeggi("FASCICOLOPROTOCOLLO", idconfi);
				if (configurazione.isEsito()) {
					tipoWSDM = configurazione.getRemotewsdm();
					page.setAttribute("tipoWSDM", tipoWSDM, PageContext.REQUEST_SCOPE);
				}
			}
		} catch (GestoreException | SQLException e) {
			throw new JspException("Errore nella verifica dell'integrazione WSDM Protocollo", e);
		}
		try {
			String ngarapa = (String) sqlManager.getObject("select ngara from gare,torn where ngarapa=? and codgar=codgar1 and torn.iterga=10", new Object[] {ngara});

			String controlloExtraConcorsiSecondoGrado = " and dg.dittao not in (select ditg.dittao from ditg where ngara5=? and (ditg.ammgar = '2' or ditg.amminversa = '2'))" +
														" and dg.dittainv not in (select dittao from ditg where (ammgar = '2' or amminversa = '2') and ngara5=?)";

			String selectControlli = "select count(*), 1 ordine from ditgpub where ditgpub.ngara=?"
			+ " union all"
			+ " select count(*), 2 ordine from ditgpub dgp, ditg dg where dgp.ngara=? and dgp.ngara=dg.ngara5 and dgp.dittao=dg.dittao and (dg.ammgar = '2' or dg.amminversa = '2')"
			+ " union all"
			+ " select count(*), 3 ordine from ditgpub where ditgpub.ngara=? and ditgpub.dittao not in (select distinct g1documatti.dittao from g1documatti where g1documatti.dittao is not null and g1documatti.ngara=?)"
			+ " union all"
			+ " select count(*), 4 ordine from g1documatti where g1documatti.ngara=? and g1documatti.dittao is null"
			+ " union all"
			+ " select count(*), 5 ordine from gare where gare.ngara=? and gare.ditta not in (select ditgpub.dittao from ditgpub where ditgpub.ngara=?)"
			+ " union all"
			+ " select count(*), 6 ordine from ditgaq where ditgaq.ngara=? and ditgaq.dittao not in (select ditgpub.dittao from ditgpub where ditgpub.ngara=?)"
			+ " union all"
			+ " select count(*), 7 ordine from ditg where ngara5=? and livpubatti is null and dittao in ( select dittao from v_ditgpub where ngara = ? and dittao is not null)"
			+ " union all"
			+ " select count(*), 8 ordine from ditg dg where (dg.ngara5=? and (dg.ammgar is null or dg.ammgar != '2') and (dg.amminversa is null or dg.amminversa != '2'))"
			+ (StringUtils.isNotEmpty(ngarapa) ? controlloExtraConcorsiSecondoGrado
			+ " union all"
			+ " select count(*), 9 ordine from ditgpub dgp, ditg dg where dgp.dittao=dg.dittao and dg.ngara5=? and dg.dittainv in (select dittao from ditg where (ammgar = '2' or amminversa = '2') and ngara5=?)"
			+ " or (dg.ngara5=? and dgp.ngara=dg.ngara5 and dgp.dittao=dg.dittao and (dg.ammgar = '2' or dg.amminversa = '2'))" : "")
			+ " order by ordine";

			String dittaGare = (String) sqlManager.getObject("select ditta from gare where ngara=?", new Object[] {ngara});
			if (StringUtils.isEmpty(dittaGare)) {
				controlloSuperato = "NO";
				messaggio += "<br> La gara non è stata aggiudicata";
			}
			Object[] parametriSelect = null;
			if (StringUtils.isNotEmpty(ngarapa)) {
				parametriSelect = new Object[] {ngara, ngarapa, ngara, ngara, ngara, ngara, ngara, ngara, ngara,
						ngarapa, ngarapa,
						ngarapa, ngara, ngara,
						ngarapa, ngara, ngara};
			} else {
				parametriSelect = new Object[] {ngara, ngara, ngara, ngara, ngara, ngara, ngara, ngara, ngara, ngara, ngara, ngara};
			}
			List<Vector<?>> datiControlli = sqlManager.getListVector(selectControlli, parametriSelect);
			Long countDitte = ((JdbcParametro) datiControlli.get(0).get(0)).longValue();
			if (countDitte == null || countDitte.equals(0L)) {
				controlloSuperato = "NO";
				messaggio += "<br> Non è stata impostata la condivisione dei documenti per nessuna ditta nella lista";
			} else {
				Long countDitteNonAmmesse = ((JdbcParametro) datiControlli.get(1).get(0)).longValue();
				if (countDitteNonAmmesse != null && countDitteNonAmmesse > 0L) {
					controlloSuperato = "NO";
					messaggio += "<br> Non è possibile impostare la condivisione dei documenti per le ditte escluse dalla gara";
				}
				if (StringUtils.isNotEmpty(ngarapa)) {
					Long countDitteNonIdonee = ((JdbcParametro) datiControlli.get(8).get(0)).longValue();
					if (countDitteNonIdonee != null && countDitteNonIdonee > 0L) {
						controlloSuperato = "NO";
						messaggio += "<br> Non è possibile impostare la condivisione dei documenti per le ditte non idonee al primo grado";
					}
				}

				Long countDittaAggiudicataria = ((JdbcParametro) datiControlli.get(4).get(0)).longValue();
				if (countDittaAggiudicataria != null && countDittaAggiudicataria > 0L) {
					controlloSuperato = "NO";
					messaggio += "<br> Non è stata impostata la condivisione dei documenti per la ditta aggiudicataria";
				}
				Long countDitteAQ = ((JdbcParametro) datiControlli.get(5).get(0)).longValue();
				if (countDitteAQ != null && countDitteAQ > 0L) {
					controlloSuperato = "NO";
					messaggio += "<br> Non è stata impostata la condivisione dei documenti per tutte le ditte aggiudicatarie";
				}
				Long countDitteAmmesse = ((JdbcParametro) datiControlli.get(7).get(0)).longValue();
				if (countDitteAmmesse >= 5) {
					if (countDitte != null && countDitte < 5) {
						if (("SI").equals(controlloSuperato)) controlloSuperato = "WARNING";
						if (StringUtils.isNotBlank(msgConferma)) msgConferma += "<br>";
						msgConferma += "Le ditte impostate con condivisione dei documenti sono in numero inferiore a 5. Vuoi procedere ugualmente?";
					}
				} else {
					if (countDitte != null && countDitteAmmesse != null && countDitte < countDitteAmmesse) {
						if (("SI").equals(controlloSuperato)) controlloSuperato = "WARNING";
						if (StringUtils.isNotBlank(msgConferma)) msgConferma += "<br>";
						msgConferma += "Le ditte impostate con condivisione non sono tutte quelle in gara. Vuoi procedere ugualmente?";
					}
				}
				Long countDitteSenzaDocumenti = ((JdbcParametro) datiControlli.get(2).get(0)).longValue();
				if (countDitteSenzaDocumenti != null && countDitteSenzaDocumenti > 0L) {
					controlloSuperato = "NO";
					messaggio += "<br> Non sono stati inseriti i documenti da pubblicare per alcune delle ditte nella lista";
				}

				Long countDitteLIVPUBATTInull = ((JdbcParametro) datiControlli.get(6).get(0)).longValue();
				if (countDitteLIVPUBATTInull != null && countDitteLIVPUBATTInull > 0L) {
					controlloSuperato = "NO";
					messaggio += "<br> Non è stato impostato il livello di accesso agli atti per alcune delle ditte nella lista";
				}

				String messaggioControlloEmail = null;
				String andControlloEmail = "";
				if (abilitatoInvioMailDocumentale) {
					messaggioControlloEmail = "<br>Le seguenti ditte non hanno un indirizzo PEC specificato in anagrafica:";
				} else {
					andControlloEmail = " and emaiip is null";
					messaggioControlloEmail = "<br>Le seguenti ditte non hanno un indirizzo PEC o E-mail specificato in anagrafica:";
				}

				String selectDitteRTSenzaMail = "select codimp, nomimp, tipimp from impr where codimp in"
						+ " (select coddic from ragimp where impman = '1' and codime9 in (select codimp from impr where codimp in (select dittao from ditg dg where dg.ngara5=? and ((dg.ammgar is null or dg.ammgar != '2') and (dg.amminversa is null or dg.amminversa != '2'))) and tipimp in (3,10)))"
						+ " and (emai2ip is null" + andControlloEmail + ")";

				String selectDitteSenzaMail = "select codimp, nomimp, tipimp from impr where codimp in"
						+ " (select dittao from ditg dg where dg.ngara5=? and ((dg.ammgar is null or dg.ammgar != '2') and (dg.amminversa is null or dg.amminversa != '2')))"
						+ " and (emai2ip is null and tipimp not in (3,10)" + andControlloEmail + ")";

				List<Vector<?>> ditteRTSenzaMail = sqlManager.getListVector(selectDitteRTSenzaMail, new Object[] {StringUtils.isEmpty(ngarapa) ? ngarapa : ngara});
				List<Vector<?>> ditteSenzaMail = sqlManager.getListVector(selectDitteSenzaMail, new Object[] {StringUtils.isEmpty(ngarapa) ? ngarapa : ngara});
				if ((ditteSenzaMail != null && ditteSenzaMail.size() > 0) || (ditteRTSenzaMail != null && ditteRTSenzaMail.size() > 0)) {
					String elementoHTMLDitteSenzaMail = "<br><ul>";
					for (int i = 0; i < ditteSenzaMail.size(); i++) {
						Vector<?> ditta = ditteSenzaMail.get(i);
						elementoHTMLDitteSenzaMail += "<li>" + ((JdbcParametro) ditta.get(0)).stringValue() + " - " + ((JdbcParametro) ditta.get(1)).stringValue() + "</li>";
					}
					for (int i = 0; i < ditteRTSenzaMail.size(); i++) {
						Vector<?> ditta = ditteRTSenzaMail.get(i);
						elementoHTMLDitteSenzaMail += "<li>" + ((JdbcParametro) ditta.get(0)).stringValue() + " - " + ((JdbcParametro) ditta.get(1)).stringValue() + "</li>";
					}
					elementoHTMLDitteSenzaMail += "</ul>";
					controlloSuperato = "NO";
					messaggio += messaggioControlloEmail + elementoHTMLDitteSenzaMail;
				}
			}
			Long countDocumentiDellaGara = ((JdbcParametro) datiControlli.get(3).get(0)).longValue();
			if (countDocumentiDellaGara == null || countDocumentiDellaGara.equals(0L)) {
				controlloSuperato = "NO";
				messaggio += "<br> Non sono stati inseriti i documenti da pubblicare per la gara";
			}
		} catch (GestoreException | SQLException e) {
			throw new JspException("Errore durante i controlli", e);
		}

		if ("NO".equals(controlloSuperato)) {
			page.setAttribute("controlloSuperato", controlloSuperato, PageContext.REQUEST_SCOPE);
			page.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);
		} else if ("WARNING".equals(controlloSuperato)) {
			page.setAttribute("controlloSuperato", controlloSuperato, PageContext.REQUEST_SCOPE);
			page.setAttribute("msg", msgConferma, PageContext.REQUEST_SCOPE);
		}
	}
}
