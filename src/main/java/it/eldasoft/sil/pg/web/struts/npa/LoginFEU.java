package it.eldasoft.sil.pg.web.struts.npa;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.web.struts.AccediAltroApplicativoAction;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;

public class LoginFEU extends ActionBaseNoOpzioni {

	static Logger logger = Logger.getLogger(AccediAltroApplicativoAction.class);

	private static String PROP_URL_FEU = "feu.url";

	protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		if (logger.isDebugEnabled())
			logger.debug("runAction: inizio metodo");

		String url = ConfigManager.getValore(PROP_URL_FEU);
		if (StringUtils.isNotEmpty(url) && url.endsWith("/")) {
			url = url.substring(0,url.length()-1);
		}

		String credenziali = request.getParameter("credenziali");
		String altroUsername = request.getParameter("username");
		String altraPassword = request.getParameter("password");
		String username = "";
		String password = "";

		if ("CORRENTI".equals(credenziali)) {
			ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);

			username = profiloUtente.getLogin();
			String passwordCifrata = profiloUtente.getPwd();
			password = "";
			if (passwordCifrata != null) {
				ICriptazioneByte decriptatore;
				try {
					decriptatore = FactoryCriptazioneByte.getInstance(ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), passwordCifrata.getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
					password = new String(decriptatore.getDatoNonCifrato());
				} catch (CriptazioneException e) {
					logger.error(this.resBundleGenerale.getString(e.getChiaveResourceBundle()), e);
				}
			}
		} else if ("ALTRE".equals(credenziali)) {
			username = altroUsername;
			password = altraPassword;
		}

		StringBuffer html = new StringBuffer();
		html.append("<html>");
		html.append("<head>");
		html.append("<META http-equiv=\"Pragma\" content=\"no-cache\">");
		html.append("<META http-equiv=\"Expires\" content=\"-1\">");
		html.append("</head>");
		html.append("<body onload=\"javascript:document.forms[0].submit();\">");
		html.append("  <form action=\"").append(url).append("/LoginExt.do\" method=\"post\" >");
		html.append("  <input type=\"hidden\" name=\"username\" value=\"").append(username).append("\"/>");
		html.append("  <input type=\"hidden\" name=\"password\" value=\"").append(password).append("\"/>");
		html.append("  </form>");
		html.append("</body>");
		html.append("</html>");

		response.getWriter().write(html.toString());

		if (logger.isDebugEnabled())
			logger.debug("runAction: fine metodo");
		return mapping.findForward(null);
	}

}
