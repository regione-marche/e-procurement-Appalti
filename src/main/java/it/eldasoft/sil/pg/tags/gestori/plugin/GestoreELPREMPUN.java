package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.tags.gestori.submit.GestoreELPREMGARE;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class GestoreELPREMPUN extends AbstractGestorePreload {

	private SqlManager sqlManager = null;

	public GestoreELPREMPUN(BodyTagSupportGene tag) {
		super(tag);
	}

	@Override
	public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {
	}

	public void inizializzaManager(PageContext page) {
		sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", page, SqlManager.class);
	}

	@Override
	public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda) throws JspException {

		inizializzaManager(page);

		String ngara = page.getRequest().getParameter("ngara");

		String annullaCalcolo = page.getRequest().getParameter("annullaCalcolo");
		if ("VERO".equals(annullaCalcolo)) {
			return;
		}

		String messaggio = "";
	    String controlloSuperato="SI";
	    String msgConferma = "";


		try {
			Date delpremcont = (Date) sqlManager.getObject("select delpremcont from gare1 where ngara=?", new Object[] {ngara});
			if (delpremcont != null) {
				page.setAttribute("delpremcont", delpremcont, PageContext.REQUEST_SCOPE);
				return;
			}
		} catch (SQLException e) {
			throw new JspException("Errore nella lettura dalla base dati: " + (e != null ? e.getMessage() : ""), e);
		}
		try {
			String selectControlli = "select count(*), 1 ordine from elpremgare where ngara=?" //countElementiGara
			+ " union all"
			+ " select count(*), 2 ordine from elpremgare where ngara=? and idopzione is not null and (pesopon is null or pesoponr is null)" //countElementiSenzaPeso
			+ " order by ordine";
			Object[] parametriSelect = new Object[] {ngara, ngara};

			List<Vector<?>> datiControlli = sqlManager.getListVector(selectControlli, parametriSelect);
			Long countElementiGara = ((JdbcParametro) datiControlli.get(0).get(0)).longValue();
			if (countElementiGara == null || countElementiGara.equals(0L)) {
				controlloSuperato = "NO";
				messaggio += "<br> Non è stato inserito nessun elemento premiale nella lista";
			} else {
				Long countElementiSenzaPeso = ((JdbcParametro) datiControlli.get(1).get(0)).longValue();
				if (countElementiSenzaPeso != null && countElementiSenzaPeso > 0L) {
					controlloSuperato = "NO";
					messaggio += "<br> Non è stato valorizzato il peso ponderale di tutti gli elementi inseriti nella lista";
				} else {
					List<Vector<JdbcParametro>> elementiPremialiGara = sqlManager.getListVector(GestoreELPREMGARE.queryQuesitiPunteggiELPREMGARE, new Object[] {ngara, ngara});
					HashMap<Long,Object[]> pesiPonderaliRiparametratiClasseMassima = new HashMap<>();
					for(Vector<JdbcParametro> elementoPremialiGara : elementiPremialiGara) {
						Long idquesito = elementoPremialiGara.get(1).longValue();
						Long idopzione = elementoPremialiGara.get(2).longValue();
						Long pesoponmin = elementoPremialiGara.get(3).longValue();
						Long pesoponmax = elementoPremialiGara.get(4).longValue();
						Double pesopon = ((Number)elementoPremialiGara.get(5).getValue()).doubleValue();
						Double pesoponr = ((Number)elementoPremialiGara.get(6).getValue()).doubleValue();

						if (pesoponmin != null && pesoponmax != null && (pesoponmin > pesopon || pesoponmax < pesopon)) {
							controlloSuperato = "NO";
							messaggio += "<br> Degli elementi della lista hanno un peso ponderale non compreso nella forcella";
							break;
						}
						Object[] idOpzioneConPeso = pesiPonderaliRiparametratiClasseMassima.get(idquesito);
						if (idOpzioneConPeso == null || (Double) idOpzioneConPeso[1] < pesoponr) {
							pesiPonderaliRiparametratiClasseMassima.put(idquesito, new Object[] {idopzione, pesoponr});
						}
					}
					if ("SI".equals(controlloSuperato)) {
						Double sommaPesiRiparametrati = 0D;
						for (Object[] idOpzioneConPeso : pesiPonderaliRiparametratiClasseMassima.values()) {
							sommaPesiRiparametrati += (Double) idOpzioneConPeso[1];
						}
						sommaPesiRiparametrati = Math.round(sommaPesiRiparametrati * 100.0) / 100.0;
						if (sommaPesiRiparametrati != 100) {
							controlloSuperato = "NO";
							messaggio += "<br> La somma dei pesi riparametrati deve essere pari a 100";
						}
					}
				}
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
