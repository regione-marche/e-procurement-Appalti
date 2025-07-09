package it.eldasoft.sil.pg.tags.funzioni.npa;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.tags.gestori.submit.npa.GestoreG1NPAFORM;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetAnacFormAggiungibiliFunction extends AbstractFunzioneTag {

	public GetAnacFormAggiungibiliFunction() {
		super(-1, null);
	}

	public static final String selectFiltro = "select TORN.CODGAR,tipgar,tipgarg from TORN left outer join GARE on TORN.CODGAR=GARE.CODGAR1 left outer join GAREAVVISI on TORN.CODGAR=GAREAVVISI.CODGAR where TORN.CODGAR=?";
	private static final String andStatiCancellati = " and g1npascheda.statonpa not in " + GestoreG1NPAFORM.elencoStatiCancellati +" and (g1npascheda.isann is null or g1npascheda.isann = '2') and not (g1npascheda.statonpa = 30 and g1cf_npa.nome_scheda='ANN')";

	public static Vector<?> getDatiUltimoFormAggiunto(String codiceGara, SqlManager sqlManager) throws SQLException {
		return sqlManager.getVector(
				"select G1NPAFORM.id, idcfnpa, nome_scheda, schede_succ, isiniz, statonpa"
						+ " from G1NPAFORM"
						+ " left join G1CF_NPA on G1NPAFORM.IDCFNPA = G1CF_NPA.ID"
						+ " left join G1NPASCHEDA on G1NPAFORM.ID = G1NPASCHEDA.IDFORM"
						+ " where codgar=?" + andStatiCancellati + " and modpres <> 3 and nome_scheda <> 'S3' and G1NPASCHEDA.IDSCHEDANPARET is null order by G1NPAFORM.ID desc", new Object[] {codiceGara});
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		String codiceGara = (String) params[1];
		String ngara = (String) params[2];

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

		TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", pageContext, TabellatiManager.class);

		List ret = new Vector();

		try {
			Vector<?> ultimoFormAggiunto = getDatiUltimoFormAggiunto(codiceGara, sqlManager);
			Vector<?> primoFormAggiunto = sqlManager.getVector(
					"select G1NPAFORM.id, idcfnpa, nome_scheda, schede_succ, flusso"
					+ " from G1NPAFORM"
					+ " left join G1CF_NPA on G1NPAFORM.IDCFNPA = G1CF_NPA.ID"
					+ " left join G1NPASCHEDA on G1NPAFORM.ID = G1NPASCHEDA.IDFORM"
					+ " where codgar=? and isiniz='1' and G1NPASCHEDA.IDSCHEDANPARET is null " + andStatiCancellati, new Object[] {codiceGara});
			Long genereGara = (Long) sqlManager.getObject("select genere from v_gare_genere where codgar=? and genere<100", new Object[] {codiceGara});
			List<?> formsAggiungibili = null;
			String codiceFlusso = null;
			if (ultimoFormAggiunto != null && ultimoFormAggiunto.size() > 0) {
				Long idForm = SqlManager.getValueFromVectorParam(ultimoFormAggiunto, 0).longValue();
				String schedeSucc = SqlManager.getValueFromVectorParam(ultimoFormAggiunto, 3).stringValue();
				Long maxStatoNPA = (Long) sqlManager.getObject(
						"select statonpa from g1npascheda left join g1npaform on G1NPAFORM.ID = G1NPASCHEDA.IDFORM left join G1CF_NPA on G1NPAFORM.IDCFNPA = G1CF_NPA.ID where idform=? and statonpa is not null" + andStatiCancellati + " order by statonpa desc",
						new Object[] { idForm });
				if (StringUtils.isBlank(schedeSucc)) {
					this.getRequest().setAttribute("messaggioNonAggiungibili", "Non ci sono schede ulteriori da produrre.");
					return null;
				}
				if (maxStatoNPA == null) {
					this.getRequest().setAttribute("messaggioNonAggiungibili", "L'ultima scheda prodotta è in uno stato non valido.");
					return null;
				}
				if (maxStatoNPA == 1) {
					String descrizioneStato = tabellatiManager.getDescrTabellato("A1291", "1");
					this.getRequest().setAttribute("messaggioNonAggiungibili", "Ci sono schede non ancora completate. L'ultima scheda aggiunta è nello stato \"" + descrizioneStato + "\"");
					return null;
				}
				if (primoFormAggiunto != null && primoFormAggiunto.size() > 0) {
					codiceFlusso = SqlManager.getValueFromVectorParam(primoFormAggiunto, 4).stringValue();
					schedeSucc = "'" + schedeSucc.replaceAll(",", "','") + "'";
					formsAggiungibili = sqlManager.getListVector("select ID, nome_scheda, oggetto, descrizione, modpres, CL_WHERE_VIS, CL_WHERE_ULT from g1cf_npa"
									+ " where nome_scheda in (" + schedeSucc + ") and (flusso like ? or flusso like ? or flusso like ? or flusso=? or flusso=?) order by numord asc",
							new Object[]{"%," + codiceFlusso + ",%", codiceFlusso + ",%", "%," + codiceFlusso, codiceFlusso, "ALL"});
				}
			} else {
				formsAggiungibili = sqlManager.getListVector("select ID, nome_scheda, oggetto, descrizione, modpres, CL_WHERE_VIS, CL_WHERE_ULT from g1cf_npa"
						+ " where isiniz = '1' order by numord asc", null);
			}

			if (formsAggiungibili == null || formsAggiungibili.size() == 0)
				return null;

			Long id, modpres;
			String nomeScheda, oggetto, descrizione, clausulaWhereVis, clausulaWhereUlt;
			HashMap<String,Integer> hm = new HashMap<String,Integer>();
			int countAggiungibili = 0;
			for (int i = 0; i < formsAggiungibili.size(); i++) {
				Vector<?> datiAnacForm = (Vector<?>) formsAggiungibili.get(i);
				int contatoreCampiEstratti = 0;
				id = SqlManager.getValueFromVectorParam(datiAnacForm, contatoreCampiEstratti++).longValue();
				nomeScheda = SqlManager.getValueFromVectorParam(datiAnacForm, contatoreCampiEstratti++).stringValue();

				oggetto = SqlManager.getValueFromVectorParam(datiAnacForm, contatoreCampiEstratti++).stringValue();
				descrizione = SqlManager.getValueFromVectorParam(datiAnacForm, contatoreCampiEstratti++).stringValue();
				modpres = SqlManager.getValueFromVectorParam(datiAnacForm, contatoreCampiEstratti++).longValue();
				clausulaWhereVis = SqlManager.getValueFromVectorParam(datiAnacForm, contatoreCampiEstratti++).stringValue();
				clausulaWhereUlt = SqlManager.getValueFromVectorParam(datiAnacForm, contatoreCampiEstratti++).stringValue();

				descrizione = descrizione.replaceAll("(\r\n|\n\r|\r|\n)", "<br />");

				String titoloFormulario = "[" + nomeScheda + "] " + oggetto;

				if (modpres != null && modpres == 3) {
					String querySelect = "select count(*) from g1npaform,g1cf_npa,g1npascheda"
							+ " where g1npaform.idcfnpa = g1cf_npa.id and g1npaform.id=g1npascheda.idform"
							+ " and codgar=?";
					Object[] parametriSelect = null;
					if (genereGara != null && (((Long) 1L).equals(genereGara) || ((Long) 3L).equals(genereGara))) {
						querySelect += " and nome_scheda=? and statonpa=1";
						parametriSelect = new Object[] {codiceGara,nomeScheda};
					} else {
						querySelect += " and g1cf_npa.modpres = 3 and statonpa not in " + GestoreG1NPAFORM.elencoStatiCancellati;
						parametriSelect = new Object[] {codiceGara};
					}
					Long countSchedeGiaPresenti = (Long) sqlManager.getObject(querySelect, parametriSelect);
					if (countSchedeGiaPresenti > 0L) {
						continue;
					}
				} else {
					if("S2R".equals(nomeScheda)) {
						//verifico ci siano ancora lotti non inviati ad ANAC
						Long countLottiRettificabili = (Long) sqlManager.getObject("select count(*) from gare  where ngara <> codgar1 and codgar1=? " +
								"and ngara not in "+GestoreG1NPAFORM.WHERE_LOTTI_SENZA_SCHEDA, new Object[]  {codiceGara, codiceGara});
						if (countLottiRettificabili == 0L) {
							continue;
						}
					}else {
						Long countSchedaGiaAggiunta = (Long) sqlManager.getObject(
								"select count(*) from g1npaform,g1cf_npa,g1npascheda"
										+ " where g1npaform.idcfnpa = g1cf_npa.id and g1npaform.id=g1npascheda.idform"
										+ " and codgar=? and nome_scheda=?" + andStatiCancellati,
								new Object[]{codiceGara, nomeScheda});
						if (countSchedaGiaAggiunta > 0L) {
							continue;
						}
					}
				}
				String clausuleExtra = "";
				if (StringUtils.isNotBlank(clausulaWhereVis))
					clausuleExtra = " and " + "(" + clausulaWhereVis + ")";
				if (StringUtils.isNotBlank(clausulaWhereUlt))
					clausuleExtra += " and " + "(" + clausulaWhereUlt + ")";
				List<?> countVisibili = sqlManager.getListVector(selectFiltro + clausuleExtra, new Object[] { codiceGara });

				if (countVisibili != null && countVisibili.size() > 0) {
					ret.add(new Object[] { id, titoloFormulario,descrizione });
					hm.put(nomeScheda,countAggiungibili++);
				}
			}
			if (Arrays.asList("5","40").contains(codiceFlusso)) { //Se P5 o AD4 bisogna togliere le S1 e S2 che non sono previste in questi flussi ma sono schede successive della CM1
				Integer s1Index = hm.get("S1");
                Integer s2Index = hm.get("S2");
                if (s1Index != null && s2Index != null) { //Rimuovere prima quella con index più alto per non dover aggiornare i valori nella hm
                    if (s1Index > s2Index) {
                        ret.remove(s1Index.intValue());
                        ret.remove(s2Index.intValue());
                    } else {
                        ret.remove(s2Index.intValue());
                        ret.remove(s1Index.intValue());
                    }
                } else if (s1Index != null) {
                    ret.remove(s1Index.intValue());
                } else if (s2Index != null) {
                    ret.remove(s2Index.intValue());
                }
			}else if (hm.containsKey("S1") && hm.containsKey("S2"))
				ret.remove(hm.get("S2").intValue());
		} catch (Exception ex) {
			throw new JspException("Errore nel caricamento della lista: " + ex.getMessage());
		}
		if (ret.size() == 0) {
			this.getRequest().setAttribute("messaggioNonAggiungibili", "Non ci sono schede ulteriori da produrre.");
			return null;
		}
		this.getRequest().setAttribute("listaFormulariAggiungibili", ret);
		return null;
	}
}