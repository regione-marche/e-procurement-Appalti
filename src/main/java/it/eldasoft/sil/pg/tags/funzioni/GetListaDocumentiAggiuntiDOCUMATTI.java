package it.eldasoft.sil.pg.tags.funzioni;
import org.apache.commons.lang.StringEscapeUtils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GetListaDocumentiAggiuntiDOCUMATTI extends AbstractFunzioneTag {
	public GetListaDocumentiAggiuntiDOCUMATTI() {
		super(-1, null);
	}

	@Override
	public String function(PageContext pageContext, Object[] params) throws JspException {

		SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
		String ngara = params[1].toString();
		String dittao = params[2].toString();

		try {
			String dittaoPrimoGrado = null;
			Vector<JdbcParametro> datiSecondoGrado = sqlManager.getVector("select codgar1,ngara from gare where ngarapa=?", new Object[] {ngara});
			if (datiSecondoGrado != null && datiSecondoGrado.size() > 0) {
				String codgarSecondoGrado = (String) datiSecondoGrado.get(0).getValue();
				String ngaraSecondoGrado = (String) datiSecondoGrado.get(1).getValue();
				if (StringUtils.isNotEmpty(codgarSecondoGrado)) {
					this.getRequest().setAttribute("codgarSecondoGrado", codgarSecondoGrado);
					dittaoPrimoGrado = (String) sqlManager.getObject("select dittainv from ditg where dittao=? and ngara5=? and codgar5=?", new Object[] {dittao, ngaraSecondoGrado, codgarSecondoGrado});
					if (StringUtils.isNotEmpty(dittaoPrimoGrado)) {
						this.getRequest().setAttribute("dittaoPrimoGrado", dittaoPrimoGrado);
					}
				}
			}
			Object[] parametri = null;
			String select = "select idorigine from g1documatti where idorigine is not null and ngara=? and (dittao";
			if (StringUtils.isNotEmpty(dittao)) {
				select += "=?";
				parametri = new Object[] {ngara, dittao};

				String codice = null;
				Long tipimp = (Long) sqlManager.getObject("select tipimp from impr where codimp = ? ",
						new Object[] { dittao });
				if (Long.valueOf(3).equals(tipimp) || Long.valueOf(10).equals(tipimp)) {
					codice = (String) sqlManager.getObject("select coddic from ragimp where codime9 = ? and impman = '1'", new Object[] { dittao });
					if (StringUtils.isEmpty(codice)) {
						codice = "";
					}
				} else {
					codice = dittao;
				}
				String nomimoDittaPrincipale = (String) sqlManager.getObject("select nomimp from impr where codimp=?", new Object[] {codice});
				String escapedNomimo = StringEscapeUtils.escapeJavaScript(nomimoDittaPrincipale);
				this.getRequest().setAttribute("nomimoDittaPrincipale", escapedNomimo);

				if (StringUtils.isNotEmpty(dittaoPrimoGrado)) {
					select += " or dittao=?";
					parametri = new Object[] {ngara, dittao, dittaoPrimoGrado};
				}
			} else {
				select += " is null";
				parametri = new Object[] {ngara};
			}
			select += ")";
			List<?> listaDocumentiAggiunti = sqlManager.getListVector(select, parametri);
			List<String> listaOutput = new ArrayList<String>();
			listaDocumentiAggiunti.forEach((documento) -> {
				try {
					String idprgIddocdig = ((JdbcParametro) ((Vector<?>) documento).get(0)).stringValue();
					idprgIddocdig = idprgIddocdig.substring(0,idprgIddocdig.indexOf("_"));
					listaOutput.add(idprgIddocdig);
				} catch (GestoreException e) {
					e.printStackTrace();
				}
			});
			if (listaDocumentiAggiunti != null && listaDocumentiAggiunti.size() > 0) {
				this.getRequest().setAttribute("listaDocumentiAggiunti", listaOutput);
			}
		} catch (Throwable e) {
			throw new JspException("Errore nella lettura dei documenti dalla tabella G1DOCUMATTI" ,e);
		}
		return null;
	}
}
