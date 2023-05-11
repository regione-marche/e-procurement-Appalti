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
package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.TempFileUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliJReports;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.type.PdfaConformanceEnum;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per la generazione automatica di un documento pdf da modello da
 * allegare alla gara
 *
 * @author Marco.Perazzetta
 */
public class GeneraAllegaPdf extends Action {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(GeneraAllegaPdf.class);

	private DataSource dataSource;

	private Connection jrConnection;

	private static final String JREPORT_NAME = "Documento";

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
	 * Metodo per la generazione di un documento di invio da assegnare ad una gara
	 *
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	@Override
	public final ActionForward execute(final ActionMapping mapping,
					final ActionForm form, final HttpServletRequest request,
					final HttpServletResponse response) throws Exception {

		DataSourceTransactionManagerBase.setRequest(request);

		response.setHeader("cache-control", "no-cache");
		response.setContentType("text/text;charset=utf-8");
		PrintWriter out = response.getWriter();

		if (logger.isDebugEnabled()) {
			logger.debug("Genera pdf da modello: inizio metodo");
		}

		String messageKey = null;
		String nomeFileGenerato = null;

		try {
			// Lettura dal request della condizione di where con cui e'
			// stata aperta la lista a video
			String numeroGara = request.getParameter("ngara");
			String indice = request.getParameter("indice");
			String idstampa = request.getParameter("idstampa");

			if (StringUtils.isNotBlank(idstampa)) {

				String[] idStampaComponents = idstampa.split("_", 2);
				InputStream inputStreamJrReport;
				String jReportsSubReportFolder;
				String jReportsImageFolder;
				String jReportsCustomSubReportFolder;

				jReportsSubReportFolder = request.getSession().getServletContext().getRealPath(
								CostantiGeneraliJReports.JR_REPORTS_SUBREPORTS_FOLDER);
				jReportsImageFolder = request.getSession().getServletContext().getRealPath(
								CostantiGeneraliJReports.JR_REPORTS_IMAGES_FOLDER);

				if (idStampaComponents.length == 2
								&& StringUtils.isNotBlank(idStampaComponents[0])
								&& StringUtils.isNotBlank(idStampaComponents[1])) {

					inputStreamJrReport = request.getSession().getServletContext().getResourceAsStream(
									CostantiGeneraliJReports.JR_REPORTS_SOURCE_FOLDER + idStampaComponents[0] + "/"
									+ idStampaComponents[1] + "/" + JREPORT_NAME + ".jasper");
					jReportsCustomSubReportFolder = request.getSession().getServletContext().getRealPath(
									CostantiGeneraliJReports.JR_REPORTS_SOURCE_FOLDER + idStampaComponents[0] + "/"
									+ CostantiGeneraliJReports.JR_REPORTS_SUBREPORTS);

					// Parametri
					HashMap<String, Object> jrParameters = new HashMap<String, Object>();
					jrParameters.put("SUBREPORT_DIR", jReportsSubReportFolder);
					jrParameters.put("IMAGES_DIR", jReportsImageFolder);
					jrParameters.put("CUSTOM_SUBREPORT_DIR", jReportsCustomSubReportFolder);
					jrParameters.put("NUMERO_GARA", numeroGara);
					jrParameters.put("IDSTAMPA", idstampa);
					jrParameters.put(JRParameter.REPORT_LOCALE, Locale.ITALIAN);
					
					if (jrConnection.isClosed()) {
						this.jrConnection = this.dataSource.getConnection();
					}

					// Stampa del formulario
					JasperPrint jrPrint = JasperFillManager.fillReport(inputStreamJrReport, jrParameters, jrConnection);

					// Output stream del risultato
					ByteArrayOutputStream baosJrReport = new ByteArrayOutputStream();

					JRPdfExporter jrExporter = new JRPdfExporter();
					
					
					JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
		            jasperReportsContext.setProperty("net.sf.jasperreports.default.font.name", "Arial");
		            jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.font.name", "Arial");
		            jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
		            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
		            configuration.setPdfaConformance(PdfaConformanceEnum.PDFA_1A);
//		            String pathToICC = ctx.getRealPath(PDF_A_ICC_PATH);
		            configuration.setIccProfilePath(request.getSession().getServletContext().getRealPath("/WEB-INF/jrReport/sRGB_v4_ICC_preference.icc"));
		            jrExporter.setConfiguration(configuration);
					
//					jrExporter.setParameter(JRExporterParameter.JASPER_PRINT, jrPrint);
//					jrExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baosJrReport);
					List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		            jasperPrintList.add(jrPrint);
		            jrExporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
		            jrExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baosJrReport));
					jrExporter.exportReport();
					inputStreamJrReport.close();
					baosJrReport.close();

					File tempFile = TempFileUtilities.getTempFile(JREPORT_NAME + ".pdf", request.getSession());

					//compongo il nome del file pdf
					String prefissoNome = JREPORT_NAME;
					if ("INV01".equals(idStampaComponents[1])) {
						prefissoNome = "InvitoRdO";
					}
					nomeFileGenerato = prefissoNome + "_"
									+ StringUtils.remove(StringUtils.remove(
																	DateFormatUtils.ISO_DATETIME_FORMAT.format(
																					Calendar.getInstance()), ':'), '-')
									+ ".pdf";

					// Scrittura dell'oggetto workBook nel file temporaneo
					FileOutputStream fos = new FileOutputStream(tempFile);
					baosJrReport.writeTo(fos);
					fos.close();

					tempFile.renameTo(new File(tempFile.getParent() + File.separatorChar + nomeFileGenerato));

				} else {
					throw new JspException("Errore durante la generazione del documento pdf da modello: "
									+ "Non e' stato possibile recuperare il codice del modello (IDSTAMPA)");
				}
			}
		} catch (JRException e) {
			throw new JspException("Errore durante la generazione del documento pdf da modello", e);
		} catch (IOException e) {
			throw new JspException("Errore durante la generazione del documento pdf da modello", e);
		}
		if (messageKey != null) {
			response.reset();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Genera pdf da modello: fine metodo");
		}
		out.print(nomeFileGenerato);
		out.flush();

		return null;
	}
}
