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
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.DatoBase64;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import java.awt.image.BufferedImage;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringEscapeUtils;

import org.krysalis.barcode4j.BarcodeException;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.krysalis.barcode4j.BarcodeUtil;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.MimeTypes;
import org.xml.sax.SAXException;

/**
 * Action per la stampa di un ordine
 *
 * @author Marco.Perazzetta
 */
public class StampaRiepilogoPswBusteAction extends ActionBaseNoOpzioni {

    /**
     * Logger
     */
    static Logger logger = Logger.getLogger(StampaRiepilogoPswBusteAction.class);


    private static final String PROP_JRREPORT_SOURCE_DIR = "/WEB-INF/jrReport/";
    private static final String PROP_JRREPORT_NAME = "password_buste";

    int livEvento = 1;
    String errMsgEvento = "";
    
    private HashMap<String, String> filenameBarcodeBuste = new HashMap<String, String>();

    
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
        JRExporter jrExporter = null;
        
        String genericMsgErr = "Errore inaspettato durante la tracciatura su w_logeventi";
       
        String codgar = request.getParameter("codgar");
        String url = request.getParameter("url");
        String username = request.getParameter("username");
        String usernameId = request.getParameter("usernameId");
        String nomeTecnico = request.getParameter("nomeTecnico");
        String oggettoGara = request.getParameter("oggettoGara");
        String PWD_A0 = request.getParameter("PWD_A0");
        String PWD_A = request.getParameter("PWD_A");
        String PWD_B = request.getParameter("PWD_B");
        String PWD_C = request.getParameter("PWD_C");

        try {

            // Input stream del file sorgente
            InputStream inputStream = request.getSession().getServletContext().getResourceAsStream(
                            PROP_JRREPORT_SOURCE_DIR + PROP_JRREPORT_NAME + ".jasper");
            
            StringBuilder xml = genXml(request,codgar,url,username,usernameId,nomeTecnico,oggettoGara,PWD_A0,PWD_A,PWD_B,PWD_C);
            
            JRXmlDataSource dataSource = new JRXmlDataSource(
                new ByteArrayInputStream(xml.toString().getBytes("UTF-8")),"/password_busta/busta");
            
            
            // Stampa del formulario
            JasperPrint print = JasperFillManager.fillReport(inputStream, new HashMap<String,Object>(), dataSource);

            // Output stream del risultato
            ByteArrayOutputStream baosJrReport = new ByteArrayOutputStream();
            
            jrExporter = new JRPdfExporter();
            jrExporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
            jrExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baosJrReport);
            jrExporter.exportReport();
            inputStream.close();
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
            
            try {
              LogEvento logEvento = LogEventiUtils.createLogEvento(request);
              logEvento.setLivEvento(livEvento);
              logEvento.setOggEvento(codgar);
              logEvento.setCodEvento("GA_DOWNLOAD_PDF_RIEPILOGO");
              String descr = "Download pdf di riepilogo delle password per la cifratura delle buste.";
              if(PWD_A0 != null && PWD_A0.length() > 0){
                descr = descr + " Hash pwd busta prequalifica = " + DigestUtils.md5(PWD_A0) + ".";
              }
              if(PWD_A != null && PWD_A.length() > 0){
                descr = descr + " Hash pwd busta amministrativa = " + DigestUtils.md5(PWD_A) + ".";
              }
              if(PWD_B != null && PWD_B.length() > 0){
                descr = descr + " Hash pwd busta tecnica = " + DigestUtils.md5(PWD_B) + ".";
              }
              if(PWD_C != null && PWD_C.length() > 0){
                descr = descr + " Hash pwd busta economica = " + DigestUtils.md5(PWD_C) + ".";
              }
              logEvento.setDescr(descr);
              logEvento.setErrmsg(errMsgEvento);
              LogEventiUtils.insertLogEventi(logEvento);
            } catch (Exception le) {
              livEvento = 3;
              logger.error(genericMsgErr, le);
            }

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
    
    private StringBuilder genXml(HttpServletRequest request,String codgar,String url,String username,String usernameId,String nomeTecnico,String oggettoGara,String PWD_A0,String PWD_A,String PWD_B,String PWD_C) throws ConfigurationException, SAXException, IOException, BarcodeException, CriptazioneException {
    StringBuilder xml = new StringBuilder();
    xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    xml.append("<password_busta>\n");
    if(PWD_A0 != null && PWD_A0.length() > 0){
      String chiaveCifrata = genTextForBarcode("A0_" + PWD_A0 + "_" + codgar);
      this.genBarcodeImage(request,"A0", chiaveCifrata);
      xml.append("    <busta>\n");
      xml.append("        <codiceGara>"+ codgar +"</codiceGara>\n");
      xml.append("       <oggettoGara><![CDATA["+ StringEscapeUtils.escapeXml(oggettoGara) +"]]></oggettoGara>\n");
      xml.append("       <rup><![CDATA["+ StringEscapeUtils.escapeXml(nomeTecnico) +"]]></rup>\n");
      xml.append("       <utente><![CDATA["+ StringEscapeUtils.escapeXml(username) +" (id:" + usernameId + ")]]></utente>\n");
      xml.append("        <url><![CDATA["+ StringEscapeUtils.escapeXml(url) +"]]></url>\n");
      xml.append("        <barcode_file>"+ filenameBarcodeBuste.get("A0") +"</barcode_file>\n");
      //xml.append("        <barcode_file>"+ "barcodeA.gif" +"</barcode_file>");
      xml.append("        <tipoBusta>"+ "Password busta di prequalifica:" +"</tipoBusta>\n");
      xml.append("        <chiavebusta>"+ PWD_A0 +"</chiavebusta>\n");
      xml.append("        <chiavecifrata><![CDATA["+ StringEscapeUtils.escapeXml(chiaveCifrata) +"]]></chiavecifrata>\n");
      xml.append("    </busta>\n");
    }
    if(PWD_A != null && PWD_A.length() > 0){
      String chiaveCifrata = genTextForBarcode("A_" + PWD_A + "_" + codgar);
      this.genBarcodeImage(request,"A", chiaveCifrata);
      xml.append("    <busta>\n");
      xml.append("        <codiceGara>"+ codgar +"</codiceGara>\n");
      xml.append("       <oggettoGara><![CDATA["+ StringEscapeUtils.escapeXml(oggettoGara) +"]]></oggettoGara>\n");
      xml.append("       <rup><![CDATA["+ StringEscapeUtils.escapeXml(nomeTecnico) +"]]></rup>\n");
      xml.append("       <utente><![CDATA["+ StringEscapeUtils.escapeXml(username) +" (id:" + usernameId + ")]]></utente>\n");
      xml.append("        <url><![CDATA["+ StringEscapeUtils.escapeXml(url) +"]]></url>\n");
      xml.append("        <barcode_file>"+ filenameBarcodeBuste.get("A") +"</barcode_file>\n");
      //xml.append("        <barcode_file>"+ "barcodeA.gif" +"</barcode_file>");
      xml.append("        <tipoBusta>"+ "Password busta amministrativa:" +"</tipoBusta>\n");
      xml.append("        <chiavebusta>"+ PWD_A +"</chiavebusta>\n");
      xml.append("        <chiavecifrata><![CDATA["+ StringEscapeUtils.escapeXml(chiaveCifrata) +"]]></chiavecifrata>\n");
      xml.append("    </busta>\n");
    }
    if(PWD_B != null && PWD_B.length() > 0){
      String chiaveCifrata = genTextForBarcode("B_" + PWD_B + "_" + codgar);
      this.genBarcodeImage(request,"B", chiaveCifrata);
      xml.append("    <busta>\n");
      xml.append("        <codiceGara>"+ codgar +"</codiceGara>\n");
      xml.append("       <oggettoGara><![CDATA["+ StringEscapeUtils.escapeXml(oggettoGara) +"]]></oggettoGara>\n");
      xml.append("        <rup><![CDATA["+ StringEscapeUtils.escapeXml(nomeTecnico) +"]]></rup>\n");
      xml.append("        <utente><![CDATA["+ StringEscapeUtils.escapeXml(username) +" (id:" + usernameId + ")]]></utente>\n");
      xml.append("        <url><![CDATA["+ StringEscapeUtils.escapeXml(url) +"]]></url>\n");
      xml.append("        <barcode_file>"+ filenameBarcodeBuste.get("B") +"</barcode_file>\n");
      //xml.append("        <barcode_file>"+ "barcodeB.gif" +"</barcode_file>");
      xml.append("        <tipoBusta>"+ "Password busta tecnica: " +"</tipoBusta>\n");
      xml.append("        <chiavebusta>"+ PWD_B +"</chiavebusta>\n");
      xml.append("        <chiavecifrata><![CDATA["+ StringEscapeUtils.escapeXml(chiaveCifrata) +"]]></chiavecifrata>\n");
      xml.append("    </busta>\n");
    }
    if(PWD_C != null && PWD_C.length() > 0){
      String chiaveCifrata = genTextForBarcode("C_" + PWD_C + "_" + codgar);
      this.genBarcodeImage(request,"C", chiaveCifrata);
      xml.append("    <busta>\n");
      xml.append("        <codiceGara>"+ codgar +"</codiceGara>\n");
      xml.append("       <oggettoGara><![CDATA["+ StringEscapeUtils.escapeXml(oggettoGara) +"]]></oggettoGara>\n");
      xml.append("        <rup><![CDATA["+ StringEscapeUtils.escapeXml(nomeTecnico) +"]]></rup>\n");
      xml.append("        <utente><![CDATA["+ StringEscapeUtils.escapeXml(username) +" (id:" + usernameId + ")]]></utente>\n");
      xml.append("        <url><![CDATA["+ StringEscapeUtils.escapeXml(url) +"]]></url>\n");
      xml.append("        <chiavebusta>"+ PWD_C +"</chiavebusta>\n");
      xml.append("        <tipoBusta>"+ "Password busta economica: " +"</tipoBusta>\n");
      //xml.append("        <barcode_file>"+ "barcodeC.gif" +"</barcode_file>");
      xml.append("        <barcode_file>"+ filenameBarcodeBuste.get("C") +"</barcode_file>\n");
      xml.append("        <chiavecifrata><![CDATA["+ StringEscapeUtils.escapeXml(chiaveCifrata) +"]]></chiavecifrata>\n");
      xml.append("    </busta>\n");
    }
    
    xml.append("</password_busta>\n");
    return xml;
    }
    
    
    private String genTextForBarcode(String chiaveDaCriptare) throws CriptazioneException {
    String risultato = null;
    try {
        ICriptazioneByte criptatore = FactoryCriptazioneByte.getInstance(
            FactoryCriptazioneByte.CODICE_CRIPTAZIONE_ADVANCED, chiaveDaCriptare.getBytes(),
            ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
        DatoBase64 base64 = new DatoBase64(criptatore.getDatoCifrato(),
            DatoBase64.FORMATO_ASCII);
        risultato = base64.getDatoBase64();
    } catch (CriptazioneException e) {
        livEvento = 3;
        errMsgEvento = "Errore inaspettato durante la criptazione dei dati da includere nel timbro digitale";
        throw new CriptazioneException(
            "Errore inaspettato durante la criptazione dei dati da includere nel timbro digitale",
            e);
    }
    return risultato;
    }
    
    private void genBarcodeImage(HttpServletRequest request, String index,String barcodeText)
        throws ConfigurationException, SAXException, IOException, BarcodeException {
    String nomeImmagine = "barcode"+ index +".gif";
    File f = TempFileUtilities.getTempFileSenzaNumeoRandom(nomeImmagine,request.getSession());
    InputStream configFile = request.getSession().getServletContext().getResourceAsStream(PROP_JRREPORT_SOURCE_DIR + "barcode-config.xml");
    FileOutputStream fos = new FileOutputStream(f,false);
    this.generateBarcode(configFile,fos, barcodeText);
    this.filenameBarcodeBuste.put(index, f.getAbsolutePath());
    }
    
    public void generateBarcode(InputStream configFile, OutputStream out,
        String message) throws ConfigurationException, SAXException, IOException, BarcodeException{
    final int dpi = 120;
    DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        Configuration cfg = builder.build(configFile);
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(out,
            MimeTypes.MIME_GIF, dpi, BufferedImage.TYPE_BYTE_BINARY,
            false, 0);
        BarcodeGenerator gen = BarcodeUtil.getInstance()
            .createBarcodeGenerator(cfg);
        gen.generateBarcode(canvas, message);
        canvas.finish();
    }
    
}
