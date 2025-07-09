package it.eldasoft.sil.pg.web.struts.npa;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.struts.ActionAjaxLogged;
import it.eldasoft.sil.pg.bl.npa.GestisciInvioFEUManager;
import net.sf.json.JSONObject;

public class GestisciInvioFEUAJAX extends ActionAjaxLogged {

	static Logger logger = Logger.getLogger(GestisciInvioFEUAJAX.class);

	private GestisciInvioFEUManager gestisciInvioFEUManager;

	public void setGestisciInvioFEUManager(GestisciInvioFEUManager gestisciInvioFEUManager) {
		this.gestisciInvioFEUManager = gestisciInvioFEUManager;
	}

	@Override
	public final ActionForward runAction(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response) throws IOException {

		if (logger.isDebugEnabled()) {
			logger.debug("RecuperaEstremiPubblicazioneAction: inizio metodo");
		}

		response.setHeader("cache-control", "no-cache");
		response.setContentType("text/text;charset=utf-8");
		JSONObject jsonReturn = new JSONObject();
		PrintWriter out = response.getWriter();

		try {
			Long idForm = Long.parseLong(request.getParameter("idForm"));
			String metodo = request.getParameter("metodo");

			Object[] resFEU = gestisciInvioFEUManager.gestisciInvio(request, idForm, metodo);
			boolean esito = (Boolean) resFEU[0];
			if (!esito) {
				String messaggio = (String) resFEU[1];
				jsonReturn.put("Esito", "Errore");
				jsonReturn.put("MsgErrore", messaggio);
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
			logger.debug("RecuperaEstremiPubblicazioneAction: fine metodo");
		}
		return null;
	}
}
