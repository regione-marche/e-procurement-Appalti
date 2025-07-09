package it.eldasoft.sil.pg.tags.funzioni.npa;

import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetListaAnacFormPCPFunction extends AbstractFunzioneTag {

	public GetListaAnacFormPCPFunction() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		String codiceGara = (String) params[1];
		String ngara = (String) params[2];

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", pageContext, TabellatiManager.class);
		JSONArray json = new JSONArray();
		Long idForm,modpres,numord;
		String nomeScheda, oggetto, descrizione, clausulaWhereVis, islotti;
		int indice = 0;

		try {
			List<?> listaAnacForms = sqlManager.getListVector(
			"select distinct G1NPAFORM.ID, nome_scheda, oggetto, descrizione, islotti, modpres, numord from G1NPAFORM"
			+ " left join G1CF_NPA on idcfnpa = G1CF_NPA.ID"
			+ " where codgar=?"
			+ " order by id asc, numord asc, nome_scheda asc",
			new Object[] {codiceGara});

			Long countLotti = (Long) sqlManager.getObject("select count(ngara) from gare where codgar1=? and ngara <> codgar1", new Object[] {codiceGara});

			if (listaAnacForms == null || listaAnacForms.size() == 0)
				return json.toString();
			
			int counterSchedaRettifica = 0;
			for (int i = 0; i < listaAnacForms.size(); i++) {
				Vector<?> datiAnacForm = (Vector<?>) listaAnacForms.get(i);
				int contatoreCampiEstratti = 0;
				idForm = SqlManager.getValueFromVectorParam(datiAnacForm, contatoreCampiEstratti++).longValue();
				nomeScheda = SqlManager.getValueFromVectorParam(datiAnacForm, contatoreCampiEstratti++).stringValue();
				oggetto = SqlManager.getValueFromVectorParam(datiAnacForm, contatoreCampiEstratti++).stringValue();
				descrizione = SqlManager.getValueFromVectorParam(datiAnacForm, contatoreCampiEstratti++).stringValue();
				islotti = (String) SqlManager.getValueFromVectorParam(datiAnacForm, contatoreCampiEstratti++).stringValue();
				modpres = SqlManager.getValueFromVectorParam(datiAnacForm, contatoreCampiEstratti++).longValue();
				numord = SqlManager.getValueFromVectorParam(datiAnacForm, contatoreCampiEstratti++).longValue();
				
				Vector datiG1npascheda = sqlManager.getVector("select statonpa,idschedanparet,isann,ngara from g1npascheda where idform=? and statonpa is not null order by statonpa desc",new Object[] { idForm });
				Long maxStatoNPA = SqlManager.getValueFromVectorParam(datiG1npascheda, 0).longValue();
				Long idSchedaNpaRet = SqlManager.getValueFromVectorParam(datiG1npascheda, 1).longValue();
				String isann = SqlManager.getValueFromVectorParam(datiG1npascheda, 2).stringValue();
				String ngaraScheda = SqlManager.getValueFromVectorParam(datiG1npascheda, 3).stringValue();
				String statoNPACalcolato = "Stato non valido";
				if (maxStatoNPA != null)
					statoNPACalcolato = tabellatiManager.getDescrTabellato("A1291", maxStatoNPA.toString());
				if(idSchedaNpaRet!=null) {
				  counterSchedaRettifica++;
				  statoNPACalcolato = "Rettifica #"+counterSchedaRettifica+" - "+statoNPACalcolato;
				}
				if("1".equals(isann))
					statoNPACalcolato += " (scheda annullata)";

				JSONObject jsonItemA = new JSONObject();
				jsonItemA.put("href", "javascript:visualizzaFormNPA('" + idForm + "','" + islotti + "','" + modpres + "')");

				String lottiInteressati = "";
				if (((Long) 3L).equals(modpres)) {
					String listaCIG = "Lotti collegati a questa scheda: \n";
					List<?> cigCollegati = sqlManager.getListVector("select codcig from gare where ngara in ("
							+ "select ngara from g1npalottischeda where idscheda=("
							+ "select id from g1npascheda where idform=?))", new Object[] {idForm});
					if (cigCollegati != null && cigCollegati.size() > 0) {
						lottiInteressati += cigCollegati.size();
						for (int j = 0; j < cigCollegati.size(); j++)
							listaCIG += cigCollegati.get(j).toString() + ", ";

						listaCIG = listaCIG.substring(0, listaCIG.length() - 2);
					}
					else {
						lottiInteressati += 0;
						listaCIG = "Nessun CIG collegato";
					}
					jsonItemA.put("title",listaCIG);
				}
				lottiInteressati += "/" + countLotti;

				indice++;
				JSONObject jsonItem = new JSONObject();
				JSONObject jsonItemLi = new JSONObject();
				String testoRiga = "<b>[" + nomeScheda + "] " + oggetto + "</b><br>";

				if (countLotti != null && countLotti > 1 && ((Long) 3L).equals(modpres))
					testoRiga += "<i>Lotti interessati: " + lottiInteressati + "</i> &nbsp ";

				if (!codiceGara.startsWith("$") && ((Long) 4L).equals(modpres)) {
					String codiga = (String) sqlManager.getObject("select codiga from gare where ngara=?", new Object[] {ngaraScheda});
					testoRiga += "<i>Lotto " + codiga + "</i> - ";
				}

				testoRiga += "<i>" + statoNPACalcolato + "</i><span style=\"display:none;\">";
				if (ngara == null) {
					testoRiga += codiceGara;
				} else {
					testoRiga += ngara;
				}
				testoRiga += "</span>";
				jsonItem.put("text", testoRiga);
				jsonItem.put("a_attr", jsonItemA);

				if (indice % 2 == 0) {
					jsonItemLi.put("CLASS", "even");
				} else {
					jsonItemLi.put("CLASS", "odd");
				}
				jsonItem.put("li_attr", jsonItemLi);
				// Aggiungo tipologia documento
				json.add(jsonItem);
			}
		} catch (Exception ex) {
			throw new JspException("Errore nel caricamento della lista: " + ex.getMessage());
		}
		String result = json.toString();
		result = result.replaceAll("\"CLASS\"", "\"class\"");
		return result;
	}
}