package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoMoney;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ImportExportDitteManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

public class ImportDitteInGaraAction extends ActionBaseNoOpzioni {


  static Logger                 logger          = Logger.getLogger(ExportDitteInGaraAction.class);
  
  /**
   * Reference al manager per la gestione delle operazioni di download e upload
   * di documenti associati
   */
  
  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  private ImportExportDitteManager importExportDitteManager;

  /**
   * @param sqlManager
   *        sqlManager da settare internamente alla classe.
   */
  public void setImportExportDitteManager(
      ImportExportDitteManager importExportDitteManager) {
    this.importExportDitteManager = importExportDitteManager;
  }

  @SuppressWarnings("unchecked")
  public final ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();
    
    String esito = "ok";
    
    String target = "success";

    JSONObject result = new JSONObject();
    
    FormFile file = null;
    try {
      UploadFileForm uploadForm = (UploadFileForm) form;
      String[] codgars =  (String[]) uploadForm.getMultipartRequestHandler().getTextElements().get("codgar");
      String codgar= codgars[0];
      if (uploadForm.getMultipartRequestHandler().getFileElements().get("file") == null) {
        throw new GestoreException("Non è stato selezionato alcun file da importare", "importaesportaexcel.nofile");
      }
      file = (FormFile) uploadForm.getMultipartRequestHandler().getFileElements().get("file");
      InputStream inputStream = file.getInputStream();
      InputStreamReader reader = new InputStreamReader(inputStream);
      //Reader reader = new FileReader();
      JSONParser parser = new JSONParser();
      JSONObject jsonObject = (JSONObject) parser.parse(reader);
      JSONObject datiGara = (JSONObject) jsonObject.get(ImportExportDitteManager.DATIGARA);
      String codiceGara = (String) datiGara.get(ImportExportDitteManager.CODICEGARA);
      String stazioneAppaltanteDesc = (String) datiGara.get(ImportExportDitteManager.STAZIONEAPPALTANTEDESC);
      String stazioneAppaltanteCF = (String) datiGara.get(ImportExportDitteManager.STAZIONEAPPALTANTECF);
      String oggetto = (String) datiGara.get(ImportExportDitteManager.OGGETTO);
      String tipoAppaltoDesc = (String) datiGara.get(ImportExportDitteManager.TIPOAPPALTODESC);
      String tipoProceduraDesc = (String) datiGara.get(ImportExportDitteManager.TIPOPROCEDURADESC);
      Long tipoAppaltoCod = (Long) datiGara.get(ImportExportDitteManager.TIPOAPPALTOCOD);
      Long tipoProceduraCod = (Long) datiGara.get(ImportExportDitteManager.TIPOPROCEDURACOD);
      String categoriaCod = (String) datiGara.get(ImportExportDitteManager.CATEGORIACOD);
      String categoriaDesc = (String) datiGara.get(ImportExportDitteManager.CATEGORIADESC);
      String classeCategoriaDesc = (String) datiGara.get(ImportExportDitteManager.CLASSECATEGORIADESC);
      Long classeCategoriaCod = (Long) datiGara.get(ImportExportDitteManager.CLASSECATEGORIACOD);
      Double importo = (Double) datiGara.get(ImportExportDitteManager.IMPORTO);
      
      /*
      request.setAttribute(ImportExportDitteManager.STAZIONEAPPALTANTEDESC, stazioneAppaltanteDesc);
      request.setAttribute(ImportExportDitteManager.STAZIONEAPPALTANTECF, stazioneAppaltanteCF);
      request.setAttribute(ImportExportDitteManager.OGGETTO, oggetto);
      request.setAttribute(ImportExportDitteManager.TIPOAPPALTODESC, tipoAppaltoDesc);
      request.setAttribute(ImportExportDitteManager.TIPOPROCEDURADESC, tipoProceduraDesc);
      request.setAttribute(ImportExportDitteManager.CATEGORIACOD, categoriaCod);
      request.setAttribute(ImportExportDitteManager.CATEGORIADESC, categoriaDesc);
      request.setAttribute(ImportExportDitteManager.CLASSECATEGORIADESC, classeCategoriaDesc);
      request.setAttribute(ImportExportDitteManager.IMPORTO, importo);

      request.setAttribute("file", jsonObject);
      */
      
      if(importExportDitteManager.fileGiaImportato(codiceGara)){
        esito="ko";
        result.put("messaggio", "Non è possibile procedere all'importazione: il file selezionato risulta essere stato già importato in altre gare.");
      }else{
        result.put(ImportExportDitteManager.STAZIONEAPPALTANTEDESC, stazioneAppaltanteDesc);
        result.put(ImportExportDitteManager.STAZIONEAPPALTANTECF, stazioneAppaltanteCF);
        result.put(ImportExportDitteManager.OGGETTO, oggetto);
        result.put(ImportExportDitteManager.TIPOAPPALTODESC, tipoAppaltoDesc);
        result.put(ImportExportDitteManager.TIPOPROCEDURADESC, tipoProceduraDesc);
        result.put(ImportExportDitteManager.CATEGORIACOD, categoriaCod);
        result.put(ImportExportDitteManager.CATEGORIADESC, categoriaDesc);
        result.put(ImportExportDitteManager.CLASSECATEGORIADESC, classeCategoriaDesc);
        if(importo!=null){
          String importoVis = new GestoreCampoMoney().getValorePerVisualizzazione(importo.toString());
          result.put(ImportExportDitteManager.IMPORTO, importoVis);
        }
        
        JSONObject obj = importExportDitteManager.getDatiGara(codgar);
        //String stazioneAppaltanteDescCurrent = (String) obj.get(ImportExportDitteManager.STAZIONEAPPALTANTEDESC);
        String stazioneAppaltanteCFCurrent = (String) obj.get(ImportExportDitteManager.STAZIONEAPPALTANTECF);
        Long tipoAppaltoCodCurrent = (Long) obj.get(ImportExportDitteManager.TIPOAPPALTOCOD);
        Long tipoProceduraCodCurrent = (Long) obj.get(ImportExportDitteManager.TIPOPROCEDURACOD);
        String categoriaCodCurrent = (String) obj.get(ImportExportDitteManager.CATEGORIACOD);
        //String categoriaDescCurrent = (String) obj.get(ImportExportDitteManager.CATEGORIADESC);
        Long classeCategoriaCodCurrent = (Long) obj.get(ImportExportDitteManager.CLASSECATEGORIACOD);
        Double importoCurrent = (Double) obj.get(ImportExportDitteManager.IMPORTO);
        
        String messaggio = "";
        if((stazioneAppaltanteCF==null && stazioneAppaltanteCFCurrent!=null) || (stazioneAppaltanteCFCurrent==null && stazioneAppaltanteCF!=null) || (stazioneAppaltanteCF!=null && !stazioneAppaltanteCF.equals(stazioneAppaltanteCFCurrent))){
          messaggio = componiMessaggio(messaggio,"codice fiscale stazione appaltante");
        }if((tipoAppaltoCodCurrent==null && tipoAppaltoCod!=null) || (tipoAppaltoCod==null && tipoAppaltoCodCurrent!=null) || (tipoAppaltoCod!=null && tipoAppaltoCodCurrent!=null && tipoAppaltoCod.intValue() != tipoAppaltoCodCurrent.intValue())){
          messaggio = componiMessaggio(messaggio,"Tipo di appalto");
        }if((tipoProceduraCod!=null && tipoProceduraCodCurrent==null) || (tipoProceduraCod==null && tipoProceduraCodCurrent!=null) || (tipoProceduraCod!=null && tipoProceduraCodCurrent!=null && tipoProceduraCod.intValue() != tipoProceduraCodCurrent.intValue())){
          messaggio = componiMessaggio(messaggio,"Tipo di procedura");
        }if((categoriaCodCurrent==null && categoriaCod!=null ) || (categoriaCod==null && categoriaCodCurrent!=null ) || (categoriaCod!=null && !categoriaCod.equals(categoriaCodCurrent))){
          messaggio = componiMessaggio(messaggio,"Categoria prevalente");
        }if((classeCategoriaCodCurrent!=null && classeCategoriaCod==null ) || (classeCategoriaCod!=null && classeCategoriaCodCurrent==null ) || (classeCategoriaCod!=null && classeCategoriaCodCurrent!=null && classeCategoriaCod.intValue() != classeCategoriaCodCurrent.intValue())){
          messaggio = componiMessaggio(messaggio,"Classifica categoria");
        }if((importoCurrent!=null && importo==null) || (importo!=null && importoCurrent==null) || (importo!=null && importoCurrent!=null && !importo.equals(importoCurrent))){
          messaggio = componiMessaggio(messaggio,"Importo a base di gara");
        }
        if(!"".equals(messaggio)){
          messaggio ="<b>ATTENZIONE:</b> Alcuni dati della gara corrente sono disallineati rispetto a quelli della gara da cui è stata prodotta l'esportazione. Verificare i seguenti dati:<ul><li>" + messaggio + "</ul>";
          result.put("messaggio", messaggio);
        }
      }
    } catch (GestoreException e) {
      esito="ko";
      result.put("messaggio", "Si è verificato un errore.");
    } catch (ParseException e) {
      esito="ko";
      result.put("messaggio", "Non è possibile procedere all'importazione: il file selezionato non è conforme al formato M-Appalti.");
    } catch (SQLException e) {
      esito="ko";
      result.put("messaggio", "Si è verificato un errore nella lettura dei dati della gara.");
    }
    
    result.put("esito", esito);
    out.print(result);
    out.flush();
    
    return null;
    
  }
  
  private String componiMessaggio(String messaggio, String campo){
    if(!"".equals(messaggio)){
      messaggio+= "<br><li>";
    }
    messaggio+=campo;
    return messaggio;
  }
  
}
