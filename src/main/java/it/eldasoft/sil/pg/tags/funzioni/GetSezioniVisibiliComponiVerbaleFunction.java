package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetSezioniVisibiliComponiVerbaleFunction extends AbstractFunzioneTag {
	public GetSezioniVisibiliComponiVerbaleFunction() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		String profiloAttivo = (String) pageContext.getSession().getAttribute("profiloAttivo");
		String ngara = params[1].toString();
		Long numeroSeduta = Long.parseLong(params[2].toString());
		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager", pageContext, PgManager.class);

		String ret = "";
		try {

			String selectGARE = "select codgar1,fasgar,tipgarg from gare where ngara=?";
			Vector<?> datiGare = sqlManager.getVector(selectGARE, new Object[] { ngara });

			int contatore = 0;
			String codgar = SqlManager.getValueFromVectorParam(datiGare, contatore).getStringValue();
			Long fasgar = (Long) SqlManager.getValueFromVectorParam(datiGare, ++contatore).getValue();
			Long tipoProcedura = (Long) SqlManager.getValueFromVectorParam(datiGare, ++contatore).getValue();

			if (StringUtils.isEmpty(codgar)) {
				throw new SQLException("La gara " + ngara + " non esiste nel database");
			}
			if (fasgar == null) {
				return "2,2,2,2,2";
			}
			Long iterga = pgManager.getITERGA(tipoProcedura);

			Long garsedFase = (Long) sqlManager.getObject("select fase from garsed where ngara=? and numsed=?", new Object[] { ngara,numeroSeduta });
			if (garsedFase == null) {
				return "2,2,2,2,2";
			}

			String selectTORN = "select dtepar, otepar, dteoff, oteoff, compreq, nobustamm from torn where codgar=?";
			Vector<?> datiTORN = sqlManager.getVector(selectTORN, new Object[] { codgar });

			String selectDITG = "select count(ngara5) from ditg where ngara5=?";
			Long countDITG = (Long) sqlManager.getObject(selectDITG, new Object[] { ngara });

			Long countTecn = (Long) sqlManager.getObject("select count(ngara) as total_count from gare1 where codgar1=? and valtec='1'", new Object[] { codgar})
				+ (Long) sqlManager.getObject("select count(ngara) from gare where codgar1=? and critlicg=2", new Object[] { codgar});

			String selectCostoNonFisso = "select count(ngara) from gare1 where codgar1=? and codgar1 <> ngara and (costofisso <> '1' or costofisso is null)";
			Long countCostoNonFisso = (Long) sqlManager.getObject(selectCostoNonFisso, new Object[] {codgar});

			String selectCostoFisso = "select count(ngara) from gare1 where codgar1=? and codgar1 <> ngara and costofisso = '1'";
			Long countCostoFisso = (Long) sqlManager.getObject(selectCostoFisso, new Object[] {codgar});

			contatore = 0;
			Date dtepar = (Date) SqlManager.getValueFromVectorParam(datiTORN, contatore).getValue();
			String otepar = SqlManager.getValueFromVectorParam(datiTORN, ++contatore).getStringValue();
			Date dteoff = (Date) SqlManager.getValueFromVectorParam(datiTORN, ++contatore).getValue();
			String oteoff = SqlManager.getValueFromVectorParam(datiTORN, ++contatore).getStringValue();
			String compreq = SqlManager.getValueFromVectorParam(datiTORN, ++contatore).getStringValue();
			String noBustaAmm = SqlManager.getValueFromVectorParam(datiTORN, ++contatore).getStringValue();

			Boolean isScadutiTerminiDomande = false;
			Boolean isScadutiTerminiOfferte = false;

			if ( dtepar != null ) {
				Calendar dataScadenzaDomande = calcoloData(dtepar, otepar);
				isScadutiTerminiDomande = ((Calendar.getInstance()).compareTo(dataScadenzaDomande)) > 0;
			}
			if ( dteoff != null ) {
				Calendar dataScadenzaOfferte = calcoloData(dteoff, oteoff);
				isScadutiTerminiOfferte = ((Calendar.getInstance()).compareTo(dataScadenzaOfferte)) > 0;
			}

			String parteSezioni = "";
			String parteScelte = "";

//			SEZIONE DOMANDE
			String scelteSezione = "1";
			if (isScadutiTerminiDomande && Arrays.asList(2L, 4L, 7L).contains(iterga) && 1L == garsedFase) {
				parteSezioni += "1";

				if (countDITG > 0 && fasgar >= -4) {
					scelteSezione += ",1,1";
					if (fasgar >= -3)
						scelteSezione += ",1";
					else
						scelteSezione += ",2";
				} else {
					scelteSezione += ",2,2,2";
				}
				parteScelte += scelteSezione;
			}
			else {
				parteSezioni += "2";
				parteScelte += "2,2,2,2";
			}

//			SEZIONE OFFERTE
			if (isScadutiTerminiOfferte && garsedFase >= 2L && fasgar >= 1L && iterga != 7L) {
				parteSezioni += ",1";
				scelteSezione = "1";

				if (!"1".equals(noBustaAmm) && countDITG > 0L && fasgar >= 2) {
					if ("1".equals(compreq)) 
						scelteSezione += ",1";
					else
						scelteSezione +=",2";

					scelteSezione += ",1,1";
				} else {
					scelteSezione += ",2,2,2";
				}
				parteScelte += "," + scelteSezione;
			} else {
				parteSezioni += ",2";
				parteScelte += ",2,2,2,2";
			}

//			SEZIONE VALUTAZIONE TECNICA
			if (isScadutiTerminiOfferte && countTecn > 0 && garsedFase >= 5 && fasgar >= 5 && iterga != 7L) {
				parteSezioni += ",1";
				parteScelte += ",1,1";
			} else {
				parteSezioni += ",2";
				parteScelte += ",2,2";
			}

//			SEZIONE APERTURA OFFERTA ECONOMICA
			if (isScadutiTerminiOfferte && countCostoNonFisso > 0 && garsedFase >= 6 && fasgar >= 6 && 7L != iterga) {
				parteSezioni += ",1";
				parteScelte += ",1,1";
			} else {
				parteSezioni += ",2";
				parteScelte += ",2,2";
			}

//			SEZIONE PROPOSTA DI AGGIUDICAZIONE
			if (isScadutiTerminiOfferte && iterga != 7 && (garsedFase >= 6 || (garsedFase==5 && countCostoFisso > 0)) && fasgar >= 7) {
				parteSezioni += ",1";
				parteScelte += ",1";
			} else {
				parteSezioni += ",2";
				parteScelte += ",2";
			}

			ret = parteSezioni + "," + parteScelte;

		} catch (SQLException e) {
			throw new JspException("Errore nella lettura dei dati della gara " + e.getMessage(), e);
		}
		return ret;
	}

	private Calendar calcoloData(Date data, String ora) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		if (StringUtils.isNotBlank(ora)) {
			String oraScadenza = ora.substring(0, 2);
			String minutiScadenza = ora.substring(3);
			cal.add(Calendar.HOUR, Integer.parseInt(oraScadenza));
			cal.add(Calendar.MINUTE, Integer.parseInt(minutiScadenza));
		} else {
			cal.add(Calendar.HOUR, 23);
			cal.add(Calendar.MINUTE, 59);
		}
		return cal;
	}
}