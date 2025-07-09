package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.ActionAjaxLogged;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class AggiungiElementiPremialiAJAX extends ActionAjaxLogged {

	static Logger logger = Logger.getLogger(AggiungiElementiPremialiAJAX.class);

	private SqlManager sqlManager;

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

	private GenChiaviManager genChiaviManager;

	public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
		this.genChiaviManager = genChiaviManager;
	}

	private GeneManager geneManager;

	public void setGeneManager(GeneManager geneManager) {
		this.geneManager = geneManager;
	}

	@Override
	public final ActionForward runAction(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response) throws IOException {

		if (logger.isDebugEnabled()) {
			logger.debug("AggiungiElementiPremialiAJAX: inizio metodo");
		}
		response.setHeader("cache-control", "no-cache");
		response.setContentType("text/text;charset=utf-8");
		JSONObject jsonReturn = new JSONObject();
		PrintWriter out = response.getWriter();

		try {
			String[] idElementi = (String[]) request.getParameterMap().get("elementiSelezionati[]");
			String[] classiSelezionate = (String[]) request.getParameterMap().get("classiSelezionate[]");
			String ngara = request.getParameter("ngara");

			TransactionStatus transazioneUpdate = this.sqlManager.startTransaction();
			boolean commitTransaction = false;
			try {
				String elencoe = (String) sqlManager.getObject("select elencoe from gare where ngara=?", new Object[] {ngara});
				String stringSelectElementi = "select tipoquesito,idquesito,idopzione" +
						" from elpremalbo where ngara=? and idquesito=? and (idopzione is null or idopzione <> 0) and isarchi is null order by id asc";

				HashMap<Long, Long> idMap = new HashMap<>();
				List<Vector<?>> listaElementiDaAggiungere = new ArrayList<>();
				for (int i=0; i<idElementi.length; i++) {
					String idElemento = idElementi[i];
					Long idQuesito = Long.parseLong(idElemento);
					listaElementiDaAggiungere.addAll(sqlManager.getListVector(stringSelectElementi, new Object[]{elencoe,idQuesito}));
					if (classiSelezionate != null && classiSelezionate.length > i && StringUtils.isNotEmpty(classiSelezionate[i])) {
						idMap.put(idQuesito, Long.parseLong(classiSelezionate[i]));
					}
				}
                for (Vector<?> vettoreDatiDocumento : listaElementiDaAggiungere) {
                    String tipoquesito = ((JdbcParametro) vettoreDatiDocumento.get(0)).getStringValue();
                    Long idquesito = ((JdbcParametro) vettoreDatiDocumento.get(1)).longValue();
                    Long idopzione = ((JdbcParametro) vettoreDatiDocumento.get(2)).longValue();

                    if ("premyesno".equals(tipoquesito) || "premrating".equals(tipoquesito) || (StringUtils.isEmpty(tipoquesito) && idopzione <= idMap.get(idquesito))) {
                        int newIdELPREMGARE = genChiaviManager.getNextId("ELPREMGARE");

                        DataColumnContainer containerELPREMGARE = new DataColumnContainer(new DataColumn[]{
                                new DataColumn("ELPREMGARE.ID", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(newIdELPREMGARE)))
                                , new DataColumn("ELPREMGARE.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara))
                                , new DataColumn("ELPREMGARE.IDQUESITO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idquesito))
                                , new DataColumn("ELPREMGARE.IDOPZIONE", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idopzione))
                        });
                        containerELPREMGARE.insert("ELPREMGARE", this.geneManager.getSql());
                    }
                }
				String elpremval = (String) sqlManager.getObject("select elpremval from gare1 where ngara=?", new Object[] {ngara});
				if (StringUtils.isEmpty(elpremval)) {
					sqlManager.update("update gare1 set elpremval='1' where ngara=?", new Object[] {ngara});
				}
				commitTransaction = true;
			} catch (SQLException | GestoreException e) {
				throw e;
			} finally {
				if (transazioneUpdate != null) {
					try {
						if (commitTransaction) {
							this.sqlManager.commitTransaction(transazioneUpdate);
						} else {
							this.sqlManager.rollbackTransaction(transazioneUpdate);
						}
					} catch (SQLException e) {
						throw e;
					}
				}
			}
		} catch (Throwable e) {
			String messaggioPerLog = e.getMessage();
			logger.error(messaggioPerLog, e);
			jsonReturn.put("Esito", "Errore");
			jsonReturn.put("MsgErrore", messaggioPerLog);
		}
		out.print(jsonReturn);
		out.flush();
		if (logger.isDebugEnabled()) {
			logger.debug("AggiungiElementiPremialiAJAX: fine metodo");
		}
		return null;
	}
}
