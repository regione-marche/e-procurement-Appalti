/*
 * Created on 13/mag/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts.oda;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.type.PdfaConformanceEnum;

/**
 * Action per la stampa di un ordine
 *
 * @author Marco.Perazzetta
 */
public class StampaOrdineAcquisto extends ActionBaseNoOpzioni {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(StampaOrdineAcquisto.class);

	private DataSource dataSource;

	private Connection jrConnection;

	private static final String PROP_JRREPORT_SOURCE_DIR = "/WEB-INF/jrReport/";
	private static final String PROP_JRREPORT_NAME = "OrdineDiAcquisto";
	private static final String PROP_JRREPORT_IMAGES_FOLDER = "/WEB-INF/jrReport/images/";
	private static final String PROP_JRREPORT_SUBREPORT_FOLDER = "/WEB-INF/jrReport/subreports/";

	/**
	 *
	 * @param dataSource
	 * @throws SQLException
	 */
	public void setDataSource(DataSource dataSource) throws SQLException {
		this.dataSource = dataSource;
		this.jrConnection = this.dataSource.getConnection();
	}

	/**
	 * Metodo per la composizione della lista definitiva dei plichi ritirati
	 *
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	public ActionForward runAction(ActionMapping mapping, ActionForm form,
					HttpServletRequest request, HttpServletResponse response)
					throws IOException, ServletException {

		if (logger.isDebugEnabled()) {
			logger.debug("Genera pdf ordine di acquisto: inizio metodo");
		}

		String target = null;
		String messageKey = null;
		JRPdfExporter jrExporter;

		try {
			// Lettura dal request della condizione di where con cui e'
			// stata aperta la lista a video
			String numeroGara = request.getParameter("ngara");
			String codiceImpresa = request.getParameter("codimp");

			String jrSubReportSourceDir = request.getSession().getServletContext().getRealPath(PROP_JRREPORT_SUBREPORT_FOLDER) + "/";
			String jrImagesReportSourceDir = request.getSession().getServletContext().getRealPath(PROP_JRREPORT_IMAGES_FOLDER) + "/";

			// Input stream del file sorgente
			InputStream inputStreamJrReportSimap = request.getSession().getServletContext().getResourceAsStream(
							PROP_JRREPORT_SOURCE_DIR + PROP_JRREPORT_NAME + ".jasper");

			// Parametri
			HashMap<String, Object> jrParameters = new HashMap<String, Object>();
			jrParameters.put("SUBREPORT_DIR", jrSubReportSourceDir);
			jrParameters.put("IMAGES_DIR", jrImagesReportSourceDir);
			jrParameters.put("NUMERO_GARA", numeroGara);
			jrParameters.put("CODICE_IMPRESA", codiceImpresa);
            jrParameters.put(JRParameter.REPORT_LOCALE, Locale.ITALIAN);

			// Stampa del formulario
			JasperPrint jrPrint = JasperFillManager.fillReport(inputStreamJrReportSimap, jrParameters, jrConnection);

			// Output stream del risultato
			ByteArrayOutputStream baosJrReport = new ByteArrayOutputStream();

			jrExporter = new JRPdfExporter();
			JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
            jasperReportsContext.setProperty("net.sf.jasperreports.default.font.name", "Arial");
            jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.font.name", "Arial");
            jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            configuration.setPdfaConformance(PdfaConformanceEnum.PDFA_1A);
//            String pathToICC = ctx.getRealPath(PDF_A_ICC_PATH);
            configuration.setIccProfilePath(request.getSession().getServletContext().getRealPath("/WEB-INF/jrReport/sRGB_v4_ICC_preference.icc"));
            jrExporter.setConfiguration(configuration);
			
			
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
            jasperPrintList.add(jrPrint);
            jrExporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
			jrExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baosJrReport));
			jrExporter.exportReport();
			inputStreamJrReportSimap.close();
			baosJrReport.close();

			// Restituisco il documento
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=\""
							+ PROP_JRREPORT_NAME
							+ ".pdf"
							+ "\"");
			ServletOutputStream output = response.getOutputStream();
			baosJrReport.writeTo(output);
			output.flush();

		} catch (Throwable e) {
			target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
			messageKey = "errors.applicazione.inaspettataException";
			logger.error(this.resBundleGenerale.getString(messageKey), e);
			this.aggiungiMessaggio(request, messageKey);
		}
		if (messageKey != null) {
			response.reset();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Genera pdf ordine di acquisto: fine metodo");
		}
		return mapping.findForward(target);
	}
}
