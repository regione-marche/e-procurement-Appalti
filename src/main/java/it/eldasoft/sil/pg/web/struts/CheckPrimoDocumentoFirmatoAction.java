package it.eldasoft.sil.pg.web.struts;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.web.struts.VerificaFirmaServizioEsterno;
import it.eldasoft.gene.web.struts.model.FirmaCheck;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.maggioli.eldasoft.digital.signature.DigitalSignatureCheckClient;
import it.maggioli.eldasoft.digital.signature.ProviderEnum;
import it.maggioli.eldasoft.digital.signature.model.ResponseCheckSignature;
import it.maggioli.eldasoft.digital.signature.providers.Provider;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import net.sf.json.JSONObject;

/**
 * Azione per controllo che il primo file sia firmato: personalizzazione LAPISOPERA
 *
 * @author Manuel Bridda
 */
public class CheckPrimoDocumentoFirmatoAction extends Action {

  private GestioneWSDMManager gestioneWSDMManager;
  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }
  
  private FileAllegatoManager fileAllegatoManager;
  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    try {
      String idprg = request.getParameter("idprg");
      String iddocdig = request.getParameter("iddocdig");
      String idconfi = request.getParameter("idconfi");
      //setta i parameti del json "primoAllegatoFirmato" e "messaggioLapis"
      
      if(StringUtils.isNotBlank(idprg) && StringUtils.isNotBlank(iddocdig))
        check(result,gestioneWSDMManager,fileAllegatoManager,idprg,Long.parseLong(iddocdig),idconfi,null);
      else {//controllo se siamo nel caso in cui sto caricando un file contestualmente alla submit
        UploadFileForm uploadForm = (UploadFileForm) form;
        FormFile file = (FormFile) uploadForm.getMultipartRequestHandler().getFileElements().get("file");
        if(file!=null)
          check(result,gestioneWSDMManager,fileAllegatoManager,null,null,idconfi,file);
      }

    } catch (Exception e) {
      throw new JspException("Errore durante la lettura della comunicazione", e);
    }

    out.println(result);
    out.flush();

    return null;

  }
  
  public void check(JSONObject result, GestioneWSDMManager gestioneWSDMManager,FileAllegatoManager fileAllegatoManager, String idprg, Long iddocdig, String idconfi, FormFile file) throws GestoreException, SQLException {
    
    boolean primoAllegatoFirmato=false;
    String messaggioLapis=null;
    boolean integrazioneWSDMAttiva = gestioneWSDMManager.isIntegrazioneWSDMAttivaValida(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO,idconfi);
    String tipoWSDM = null;
    if(integrazioneWSDMAttiva){
      WSDMConfigurazioneOutType configurazione = gestioneWSDMManager.wsdmConfigurazioneLeggi(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO,idconfi);
      if(configurazione.isEsito())
        tipoWSDM = configurazione.getRemotewsdm();
     
      if("LAPISOPERA".equals(tipoWSDM)) {
        String providerConfig = ConfigManager.getValore("digital-signature-provider");
        if ("2".equals(providerConfig)) {
          try {
            byte[] stream = null;
            String nomeFile="";
            if(iddocdig!=null) {
              BlobFile f = fileAllegatoManager.getFileAllegato(idprg, iddocdig);
              stream = f.getStream();
              nomeFile=f.getNome();
            }else {
              stream=file.getFileData();
              nomeFile = file.getFileName();
            }
            String urlInfocertVerify = ConfigManager.getValore("digital-signature-check-url");
            Provider provider = DigitalSignatureCheckClient.getInstance(ProviderEnum.INFOCERT)
                .withConfiguration(urlInfocertVerify);
            
            ResponseCheckSignature rcs = provider.checkDigitalSignature(new ByteArrayInputStream(stream), nomeFile);
            
            VerificaFirmaServizioEsterno vfse = new VerificaFirmaServizioEsterno();
            FirmaCheck  fc = vfse.getObjectsByXML(rcs.getXmlbody());         
            if(fc.getSignatures()!=null && fc.getSignatures().size()>0) {
              primoAllegatoFirmato=true;
            }
            
          }catch(Exception e) {
            messaggioLapis = "Firma remota: il servizio di verifica INFOCERT non e' configurato correttamente";
          }
        }else {
          messaggioLapis="Firma remota: non e' configurato il servizio di verifica INFOCERT";
        }
      }
    }
    result.put("primoAllegatoFirmato", primoAllegatoFirmato);
    result.put("messaggioLapis", messaggioLapis);
  }

}
