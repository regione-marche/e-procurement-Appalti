/*
 * Created on 06/09/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ImportExportDitteManager;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiRicezioneFunction;
import it.eldasoft.utils.spring.UtilitySpring;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard che si occupa di preparare i dati prelevati dalle
 * viste V_DITTE_ELECAT e V_DITTE_ELESUM per potere eseguire l'inserimento
 * delle ditte in gara sfruttando il gestore GestoreFasiRicezione
 *
 * @author Marcello Caminiti
 */
public class GestorePopupImportaDitteDaFile extends GestoreFasiRicezione {

  static Logger               logger         = Logger.getLogger(GestorePopupImportaDitteDaFile.class);

  @Override
  public String getEntita() {
    return "DITG";
  }

  public GestorePopupImportaDitteDaFile() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupImportaDitteDaFile(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer
      dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {


  }


  @SuppressWarnings({"rawtypes", "unchecked" })
  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer dataColumnContainer)
      throws GestoreException {

    ImportExportDitteManager importExportDitteManager = (ImportExportDitteManager) UtilitySpring.getBean("importExportDitteManager",
        this.getServletContext(), ImportExportDitteManager.class);
    MEPAManager mepamanager = (MEPAManager) UtilitySpring.getBean("mepaManager",
        this.getServletContext(), MEPAManager.class);

    //Anche se la pagina è aperta in modifica, vi è la necessità di eseguire le operazioni
    //legate all'inserimento di una ditta, operazioni si trovano nel preInsert GestoreFasiRicezione,
    //quindi anche se sono nel preUpdate si richiama super.preInsert.

    //String file=UtilityStruts.getParametroString(this.getRequest(),"file");
    String codgar=UtilityStruts.getParametroString(this.getRequest(),"codgar");
    String plicoUnico = UtilityStruts.getParametroString(this.getRequest(),"isGaraLottiConOffertaUnica");
    String ngara=UtilityStruts.getParametroString(this.getRequest(),"ngara");

    int livEvento = 1;
    String oggEvento = ngara;
    String codEvento = "GA_IMPORTA_DITTE_JSON";
    String descrEvento = "";
    String errMsgEvento = "";

    int countInsAnagrafica = 0;
    int countImport = 0;
    int countFailure = 0;

    String messageError = "";
    JSONObject jsonObject = null;
    UploadFileForm mf = this.getForm();
    FormFile file = mf.getSelezioneFile();
    try {
      InputStream inputStream = file.getInputStream();
      InputStreamReader reader = new InputStreamReader(inputStream);
      //Reader reader = new FileReader();

      JSONParser parser = new JSONParser();

      jsonObject = (JSONObject) parser.parse(reader);
      JSONObject datiGara = (JSONObject) jsonObject.get(ImportExportDitteManager.DATIGARA);
      JSONArray ar = (JSONArray) datiGara.get("operatori");
      String codgarOrigine = (String) datiGara.get(ImportExportDitteManager.CODICEGARA);
      for(int i=0;i<ar.size();i++){

          String codimp = null;
          //Per ogni controllo se è presente in anagrafica, in caso contrario la aggiungo.
          JSONObject obj = (JSONObject) ar.get(i);
          Long tipimp = (Long) obj.get(ImportExportDitteManager.TIPOIMPRESACOD);
          String codice = (String) obj.get(ImportExportDitteManager.CODICE);
          String cfimp = (String) obj.get(ImportExportDitteManager.CODICEFISCALE);
          String pivimp = (String) obj.get(ImportExportDitteManager.PARTITAIVA);
          String nomest = (String) obj.get(ImportExportDitteManager.RAGIONESOCIALE);
          String nomimp;
          if(nomest!= null && nomest.length()>60){
            nomimp = nomest.substring(0,60);
          }else{
            nomimp = nomest;
          }
          String emaiip = (String) obj.get(ImportExportDitteManager.EMAIL);
          String emai2ip = (String) obj.get(ImportExportDitteManager.PEC);

          try{
          //Se RT, lo inserisco in anagrafica, e poi controllo uno ad uno i componenti del raggruppamento
          if(tipimp != null && (tipimp.intValue()==3 || tipimp.intValue()==10)){
            codimp = importExportDitteManager.insertDittaAnagrafica(tipimp, nomest, nomimp, cfimp, pivimp, emaiip, emai2ip);
            JSONArray raggr = (JSONArray) obj.get("raggruppamento");
            //String[] codimpRagArray = new String[raggr.size()];
            for(int j=0;j<raggr.size();j++){
              JSONObject opRag = (JSONObject) raggr.get(j);
              Long tipimpRag = (Long) opRag.get(ImportExportDitteManager.TIPOIMPRESACOD);
              String codiceRag = (String) opRag.get(ImportExportDitteManager.CODICE);
              String cfimpRag = (String) opRag.get(ImportExportDitteManager.CODICEFISCALE);
              String pivimpRag = (String) opRag.get(ImportExportDitteManager.PARTITAIVA);
              String nomestRag = (String) opRag.get(ImportExportDitteManager.RAGIONESOCIALE);
              String nomimpRag;
              if(nomestRag!= null && nomestRag.length()>60){
                nomimpRag = nomestRag.substring(0,60);
              }else{
                nomimpRag = nomestRag;
              }
              String emaiipRag = (String) opRag.get(ImportExportDitteManager.EMAIL);
              String emai2ipRag = (String) opRag.get(ImportExportDitteManager.PEC);
              Boolean mandatariaBool = (Boolean) opRag.get(ImportExportDitteManager.MANDATARIA);
              Double quodic = (Double) opRag.get(ImportExportDitteManager.QUODIC);
              String codimpRag = importExportDitteManager.isOperatoreRegistrato(codiceRag, cfimpRag, pivimpRag);
              if(codimpRag == null || "".equals(codimpRag)){
                countInsAnagrafica++;
                codimpRag = importExportDitteManager.insertDittaAnagrafica(tipimpRag, nomestRag, nomimpRag, cfimpRag, pivimpRag, emaiipRag, emai2ipRag);
              }
              String mandataria;
              if(mandatariaBool != null && mandatariaBool){
                mandataria="1";
              }else{
                mandataria=null;
              }
              sqlManager.update("insert into RAGIMP (IMPMAN,CODDIC,NOMDIC,CODIME9,QUODIC) values(?,?,?,?,?)", new Object[] { mandataria,codiceRag, nomimpRag, codimp, quodic });
              //codimpRagArray[j]=codimpRag;
            }
          }else{
            codimp = importExportDitteManager.isOperatoreRegistrato(codice, cfimp, pivimp);
            if(codimp == null || "".equals(codimp)){
              codimp = importExportDitteManager.insertDittaAnagrafica(tipimp, nomest, nomimp, cfimp, pivimp, emaiip, emai2ip);
              countInsAnagrafica++;
            }
          }
          /*
          ArrayList<String> listaLotti = new ArrayList<String>();
          if("true".equals(plicoUnico)){
            String select = "select ngara from gare where codgar1 = ? and ngara != codgar1";
            List lottiPlicoUnico = sqlManager.getListVector(select, new Object[]{codgar});
            if(lottiPlicoUnico!=null && lottiPlicoUnico.size()>0){
              for(int j=0;j<lottiPlicoUnico.size();j++){
                String codiceLotto = SqlManager.getValueFromVectorParam(lottiPlicoUnico.get(j), 0).stringValue();
                listaLotti.add(codiceLotto);
              }
            }
          }else{
            String ngara=UtilityStruts.getParametroString(this.getRequest(),"ngara");
            listaLotti.add(ngara);
          }
          if(listaLotti!=null && listaLotti.size()>0){
            for(int j=0;j<listaLotti.size();j++){
              String ngara = listaLotti.get(j);
              */
              //Dopo aver aggiunto la ditta in anagrafica (se non era già presente). La aggiungo alla gara corrente
              Vector elencoCampi = new Vector();
              elencoCampi.add(new DataColumn("DITG.NGARA5",
                  new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
              elencoCampi.add(new DataColumn("DITG.CODGAR5",
                  new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar)));
              elencoCampi.add(new DataColumn("DITG.DITTAO",
                  new JdbcParametro(JdbcParametro.TIPO_TESTO, codimp)));

              //campi che si devono inserire perchè adoperati nel gestore
              //GestoreFasiRicezione
              elencoCampi.add(new DataColumn("DITG.NOMIMO",
                  new JdbcParametro(JdbcParametro.TIPO_TESTO, nomimp)));
              elencoCampi.add(new DataColumn("DITG.NPROGG",
                  new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
              elencoCampi.add(new DataColumn("DITG.NUMORDPL",
                  new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
              elencoCampi.add(new DataColumn("DITG.ACQUISIZIONE",
                  new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(7))));
              elencoCampi.add(new DataColumn("DITG.NGARAEXP",
                  new JdbcParametro(JdbcParametro.TIPO_TESTO, codgarOrigine)));

              DataColumnContainer containerDITG = new DataColumnContainer(elencoCampi);

              GestoreDITG gestoreDITG = new GestoreDITG();
              gestoreDITG.setRequest(this.getRequest());
              gestoreDITG.inserisci(status, containerDITG);
              // Inizializzazione documenti della ditta
              pgManager.inserimentoDocumentazioneDitta(codgar, ngara, codimp);

              Double faseGara = new Double(Math.floor(GestioneFasiRicezioneFunction.FASE_RICEZIONE_PLICHI / 10));
              Long faseGaraLong = new Long(faseGara.longValue());
              if("true".equals(plicoUnico)){
                String select = "select ngara from gare where codgar1 = ? and ngara != codgar1";
                List lottiPlicoUnico = sqlManager.getListVector(select, new Object[]{codgar});
                if(lottiPlicoUnico!=null && lottiPlicoUnico.size()>0){
                  mepamanager.gestioneLottiOffertaUnica(ngara, codimp, containerDITG, gestoreDITG,null,faseGaraLong,status,"INS",false,null,null);
                }
              }
              countImport++;
          /*
            }
          }
          */
          //super.preInsert(status, containerDITG);
        }catch (SQLException e) {
          livEvento = 3;
          countFailure++;
          messageError+= "<li>"+nomest+"</li>";
          errMsgEvento+= e.getMessage();
        }catch (GestoreException e) {
          livEvento = 3;
          countFailure++;
          messageError+= "<li>"+nomest+"</li>";
          errMsgEvento+= e.getMessage();
        }
      }

      sqlManager.update("update GARE set FASGAR = -3, STEPGAR = -30 where ngara = ?", new Object[] { ngara });

      this.getRequest().setAttribute("countAnagrafica", countInsAnagrafica);
      this.getRequest().setAttribute("countImport", countImport);
      if(!"".equals(messageError)){
        messageError = "<span>L'importazione delle seguenti ditte ha dato esito negativo:</span><ul>"+messageError+"</ul>";
        this.getRequest().setAttribute("messageError", messageError);
      }
      this.getRequest().setAttribute("esito", "ok");

    } catch (ParseException e) {
      livEvento = 3;
      errMsgEvento = e.getMessage();
      throw new GestoreException("Errore nell'importazione delle ditte in gara.", null, e);
    } catch (FileNotFoundException e) {
      livEvento = 3;
      errMsgEvento = e.getMessage();
      throw new GestoreException("Errore nell'importazione delle ditte in gara.", null, e);
    } catch (IOException e) {
      livEvento = 3;
      errMsgEvento = e.getMessage();
      throw new GestoreException("Errore nell'importazione delle ditte in gara.", null, e);
    } catch (SQLException e) {
      livEvento = 3;
      errMsgEvento = e.getMessage();
      throw new GestoreException("Errore nell'update della fase di gara.", null, e);
    }finally{

      descrEvento= "Importazione ditte da formato M-Appalti (estensione JSON) (n.ditte inserite in gara "+countImport+", n.ditte inserite in anagrafica "+countInsAnagrafica+", n.ditte non inserite in seguito a errori: "+countFailure+").";
      if(jsonObject!=null){
        errMsgEvento+=jsonObject.toString();
      }
      LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
      logEvento.setLivEvento(livEvento);
      logEvento.setOggEvento(oggEvento);
      logEvento.setCodEvento(codEvento);
      logEvento.setDescr(descrEvento);
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);
    }


  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

}