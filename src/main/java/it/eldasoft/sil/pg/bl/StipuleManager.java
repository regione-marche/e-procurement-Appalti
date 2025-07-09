package it.eldasoft.sil.pg.bl;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.gene.ws.objectstorage.ObjectStorageUtils;
import it.eldasoft.gene.ws.objectstorage.UploadInfo;
import it.maggioli.eldasoft.ws.erp.WSERPAllegatoType;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;


/**
 *
 * @author cristian.febas
 *
 */
public class StipuleManager {
  private static final Logger logger = Logger.getLogger(StipuleManager.class);

  private SqlManager          sqlManager;

  private TabellatiManager    tabellatiManager;
  
  private GenChiaviManager    genChiaviManager;

  private FileAllegatoManager fileAllegatoManager;
  
  private GestioneWSERPManager gestioneWSERPManager;


  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }
  
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
	    this.genChiaviManager = genChiaviManager;
  }

  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
	    this.fileAllegatoManager = fileAllegatoManager;
  }
  
  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }

  /*
   *  Metodo per la gestione del file allegato in inserimento e aggiornamento
   */

    public int setDocumentoAllegato(HttpServletRequest request, String operazione, Long idDocStipula,
        Long iddocdg, DataColumnContainer impl, UploadFileForm uploadFileForm)
    throws GestoreException {

      boolean isFileDaAllegare = (impl.isColumn("FILEDAALLEGARE") && impl.getString("FILEDAALLEGARE") != null && !impl.getString("FILEDAALLEGARE").trim().equals(""));
      boolean isRichiestaFirma = impl.isModifiedColumn("W_DOCDIG.DIGFIRMA");
      
      boolean isFileCancellato = (iddocdg!=null && impl.isColumn("FILECANCELLATO") && impl.getString("FILECANCELLATO") != null && !impl.getString("FILECANCELLATO").trim().equals(""));
      
      if(isFileCancellato && !isFileDaAllegare) {
          try {
                  //Caso da cancellare
                  impl.addColumn("W_DOCDIG.IDPRG", JdbcParametro.TIPO_TESTO,"PG");
                  impl.addColumn("W_DOCDIG.IDDOCDIG", JdbcParametro.TIPO_NUMERICO,iddocdg);
                  impl.getColumn("W_DOCDIG.IDPRG").setChiave(true);
                  impl.getColumn("W_DOCDIG.IDDOCDIG").setChiave(true);
                  UploadInfo oldBucketInfo = ObjectStorageUtils.getUploadInfo("select bucket_ref, bucket_uuid from w_docdig where idprg=? and iddocdig=?",new Object[] {"PG", iddocdg});
                  ObjectStorageUtils.deleteFile(oldBucketInfo.bucket_ref(),oldBucketInfo.uuid());
                  impl.delete("W_DOCDIG", sqlManager);
                  //iddocdg = null;
          }catch (SQLException e) {
              throw new GestoreException("Errore eliminazione record W_DOCDIG", null, e);
          }
      }
      
      if (isFileDaAllegare || (isRichiestaFirma && !isFileCancellato)) {

          ByteArrayOutputStream baos = null;
          Cipher cipher = null;

          try {

            String dimMassimaTabellatoStringa = tabellatiManager.getDescrTabellato("A1072", "1");
            if(dimMassimaTabellatoStringa==null || "".equals(dimMassimaTabellatoStringa)){
              throw new GestoreException("Non è presente il tabellato A1072 per determinare la dimensione "
                  + "massima dell'upload del file", "upload.noTabellato", null);
            }
            int pos = dimMassimaTabellatoStringa.indexOf("(");
            if (pos<1){
              throw new GestoreException("Non è possibile determinare dal tabellato A1072 la dimensione "
                  + "massima dell'upload del file", "upload.noValore", null);
            }
            dimMassimaTabellatoStringa = dimMassimaTabellatoStringa.substring(0, pos-1);
            dimMassimaTabellatoStringa = dimMassimaTabellatoStringa.trim();
            double dimMassimaTabellatoByte = Math.pow(2, 20) * Double.parseDouble(dimMassimaTabellatoStringa);

            boolean estensioneAmmessa = FileAllegatoManager.isEstensioneFileAmmessa(uploadFileForm.getSelezioneFile().getFileName());
            boolean checkFileContent = FileAllegatoManager.checkFileContent(uploadFileForm.getSelezioneFile().getFileData(),uploadFileForm.getSelezioneFile().getFileName());
            boolean isMaliciousPDF = uploadFileForm.getSelezioneFile().getFileName().toLowerCase().endsWith(".pdf") && FileAllegatoManager.isMaliciousPdf(uploadFileForm.getSelezioneFile().getFileData(), uploadFileForm.getSelezioneFile().getFileName());

            boolean isPresenteFile = (uploadFileForm.getSelezioneFile().getFileSize() != 0 
                                      && uploadFileForm.getSelezioneFile().getFileSize() <= dimMassimaTabellatoByte
                                      && estensioneAmmessa && checkFileContent && !isMaliciousPDF);
            
            String nomeallegatoWSERP = request.getParameter("getdocumentoallegatoWSERP_nomeallegato");
            //String codiceOda = request.getParameter("getdocumentoallegatoWSERP_nomeallegato");
            boolean isAllegatoWSERP = nomeallegatoWSERP != null && nomeallegatoWSERP.length()>0;
            
            if(isPresenteFile || isRichiestaFirma || isAllegatoWSERP){
              HttpSession session = request.getSession();
              if (session != null) {
                if(isPresenteFile) {
                  baos = new ByteArrayOutputStream();
                  byte[] ff = uploadFileForm.getSelezioneFile().getFileData();
                  baos.write(ff);
                  //solo nel best case memorizzo in fb
                  String nomeFile="";
                  int len = impl.getString("FILEDAALLEGARE").length();
                  int posizioneBarra = impl.getString("FILEDAALLEGARE").lastIndexOf("\\");
                  nomeFile=impl.getString("FILEDAALLEGARE").substring(posizioneBarra+1,len).toUpperCase();
                  
                  impl.addColumn("W_DOCDIG.DIGNOMDOC", JdbcParametro.TIPO_TESTO,nomeFile);
                  impl.addColumn("W_DOCDIG.DIGOGG", JdbcParametro.TIPO_BINARIO,baos);
                } else if(isAllegatoWSERP) {
                  baos = new ByteArrayOutputStream();
                  String servizioWSERP ="WSERP";
                  ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
                          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
                      Long syscon = new Long(profilo.getId());
                      String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizioWSERP);

                      String usernameWSERP = credenziali[0];
                      String passwordWSERP = credenziali[1];

                  String codiceOda = request.getParameter("getdocumentoallegatoWSERP_codiceOda");
                  String idAllegatoWSERP = request.getParameter("getdocumentoallegatoWSERP_idFile");
                  
                  WSERPAllegatoType allegato = gestioneWSERPManager.wserpLeggiAllegato(usernameWSERP, passwordWSERP, nomeallegatoWSERP, servizioWSERP, codiceOda+"-"+idAllegatoWSERP, "ODA");
                  
                  baos.write(allegato.getContenuto());
                  impl.addColumn("W_DOCDIG.DIGNOMDOC", JdbcParametro.TIPO_TESTO,allegato.getNome());
                  impl.addColumn("W_DOCDIG.DIGOGG", JdbcParametro.TIPO_BINARIO,baos);
                  
                }
                if(iddocdg!=null){
                  operazione = "UPDATE";
                  impl.addColumn("W_DOCDIG.IDPRG", JdbcParametro.TIPO_TESTO,"PG");
                  impl.addColumn("W_DOCDIG.IDDOCDIG", JdbcParametro.TIPO_NUMERICO,iddocdg);
                  impl.getColumn("W_DOCDIG.IDPRG").setObjectOriginalValue("PG");
                  impl.getColumn("W_DOCDIG.IDDOCDIG").setObjectOriginalValue(iddocdg);
                }else{
                  operazione = "INSERT";
                  //Si deve calcolare il valore di IDDOCDIG
                  Long maxIDDOCDIG = (Long) this.sqlManager.getObject(
                            "select max(IDDOCDIG) from W_DOCDIG where IDPRG= ?",
                            new Object[] {"PG"} );
                  if (maxIDDOCDIG != null && maxIDDOCDIG.longValue()>0){
                    iddocdg  = maxIDDOCDIG.longValue() + 1;
                  }
                  impl.addColumn("W_DOCDIG.IDPRG", JdbcParametro.TIPO_TESTO,"PG");
                  impl.addColumn("W_DOCDIG.IDDOCDIG", JdbcParametro.TIPO_NUMERICO,iddocdg);
                }
                impl.getColumn("W_DOCDIG.IDPRG").setChiave(true);
                impl.getColumn("W_DOCDIG.IDDOCDIG").setChiave(true);
                impl.addColumn("W_DOCDIG.DIGENT", JdbcParametro.TIPO_TESTO,"G1DOCSTIPULA");
                impl.addColumn("W_DOCDIG.DIGKEY1", JdbcParametro.TIPO_TESTO, String.valueOf(idDocStipula));
                
                
                if("INSERT".equals(operazione)){
                  impl.insert("W_DOCDIG", sqlManager);
                }else{
                  if("UPDATE".equals(operazione)){
                    impl.update("W_DOCDIG", sqlManager);
                  }
                }
                ObjectStorageUtils.addFile(impl.getColumn("W_DOCDIG.IDPRG").getValue().getStringValue(), impl.getColumn("W_DOCDIG.IDDOCDIG").getValue().longValue());
              }
              
            } else {
              if(uploadFileForm.getSelezioneFile().getFileSize() == 0 ){
                throw new GestoreException("Il file specificato è vuoto. Per continuare specificare un altro file",
                    "upload.fileVuoto", null, null);
              }else if(uploadFileForm.getSelezioneFile().getFileSize()> dimMassimaTabellatoByte){
                throw new GestoreException("Il file selezionato ha una dimensione "
                    + "superiore al massimo consentito (" + dimMassimaTabellatoStringa + " MB)" , "upload.overflow", new String[] { dimMassimaTabellatoStringa + " MB" },null);
              }else {
                String fileName = uploadFileForm.getSelezioneFile().getFileName();
                if(!estensioneAmmessa){
                  throw new GestoreException("Il file selezionato da caricare ha un'estensione non accettata",
                      "upload.estensioneNonAmmessa", new String[]{fileName}, null);
                }else if(!checkFileContent){
                throw new GestoreException("Il file selezionato da caricare viene scartato in quanto ha un contenuto in formato non valido secondo le verifiche mediante la libreria Apache Tika",
                    "upload.erroreVerificaContenuto", new String[]{fileName}, null);
                } else if(isMaliciousPDF) {
                  throw new GestoreException("Il file selezionato da caricare viene scartato in quanto sembra codificare dei contenuti non sicuri (malware)",
                          "upload.erroreContenutoMalevolo", new String[]{fileName}, null);
                }
              }
            }

          } catch (SQLException e) {
            throw new GestoreException("Errore nel caricamento in W_DOCDIG", null, e);
          } catch (FileNotFoundException e) {
            throw new GestoreException("Si è verificato un problema durante il caricamento del file", null, e);
          } catch (IOException e) {
            throw new GestoreException("Si è verificato un problema durante il caricamento del file", null, e);
          }
      }//file da allegare
      
      return 0;

    }

    public void copiaDocumentiPredefiniti(HttpServletRequest request, List listaDocumenti, Long idStipula) throws SQLException, GestoreException{
        Long maxNumOrd = (Long) this.sqlManager.getObject(
                "select max(coalesce(numord,0)) from G1DOCSTIPULA where idstipula= ?",
                new Object[] {idStipula});
        Long newNumOrd = Long.valueOf(0);
        if (maxNumOrd != null && maxNumOrd.longValue()>0){
        	newNumOrd  = maxNumOrd.longValue();
        }
        
    	if (listaDocumenti != null && listaDocumenti.size() > 0) {
            String insertG1DOCSTIPULA="insert into g1docstipula"
            		+ " (ID,IDSTIPULA,NUMORD,FASE,TITOLO,DESCRIZIONE,VISIBILITA,OBBLIGATORIO,STATODOC,FORMATO) values(?,?,?,?,?,?,?,?,?,?)";

          	Long fase = null;
          	Long visibilita= null;
          	String titolo= null;
          	String descrizione= null;
          	String obbligatorio= null;
          	String idprg = null;
          	Long iddocdig = null;
          	Long formato = null;
			//a.NUMORD,a.FASE,a.VISIBILITA,a.DESCRIZIONE,a.ULTDESC,a.OBBLIGATORIO,a.IDPRG,a.IDDOCDIG,a.MODFIRMA
            for (Iterator iterator = listaDocumenti.iterator(); iterator.hasNext();) {
                Vector docpredef = (Vector) iterator.next();
                
                fase = null;
                if(docpredef.get(1)!=null) {
                	fase = ((JdbcParametro) docpredef.get(1)).longValue();
                }
                
                visibilita = null;
                if(docpredef.get(2)!=null) {
                	visibilita = ((JdbcParametro) docpredef.get(2)).longValue();
                }
                  
                titolo = null;
                if(docpredef.get(3)!=null) {
                	titolo = ((JdbcParametro) docpredef.get(3)).stringValue();
                }
                
                descrizione = null;
                if(docpredef.get(4)!=null) {
                	descrizione = ((JdbcParametro) docpredef.get(4)).stringValue();
                }

                obbligatorio = null;
                if(docpredef.get(5)!=null) {
                	obbligatorio = ((JdbcParametro) docpredef.get(5)).stringValue();
                }
                
                idprg = null;
                if(docpredef.get(6)!=null) {
                	idprg = ((JdbcParametro) docpredef.get(6)).stringValue();
                }
                
                iddocdig = null;
                if(docpredef.get(7)!=null) {
                	iddocdig = ((JdbcParametro) docpredef.get(7)).longValue();
                }
                
                formato = null;
                if(docpredef.get(8)!=null) {
                	formato = ((JdbcParametro) docpredef.get(8)).longValue();
                }
                
                Long statodoc= Long.valueOf(1);
                //calcolo numord
                newNumOrd  = newNumOrd.longValue() + 1;
                
                int nextId = this.genChiaviManager.getNextId("G1DOCSTIPULA");
                this.sqlManager.update(insertG1DOCSTIPULA,
                		new Object[] { nextId,idStipula,newNumOrd,fase,titolo,descrizione,visibilita,obbligatorio,statodoc,formato});
                
                //verifica esistenza allegato ed inserimento dello stesso:
                if("PG".equals(idprg) && iddocdig != null){
                	String selectW_DOCDIG = "select IDPRG,IDDOCDIG,DIGENT,DIGKEY1,DIGKEY2,DIGTIPDOC,DIGNOMDOC,DIGDESDOC from W_DOCDIG where IDPRG = ? and IDDOCDIG = ?";
                    try {
                	
	                    HashMap occorrenzaW_DOCDIGDaCopiare = this.sqlManager.getHashMap( selectW_DOCDIG, new Object[] { idprg, iddocdig });
	                    if (occorrenzaW_DOCDIGDaCopiare != null && occorrenzaW_DOCDIGDaCopiare.size() > 0) {
	                        DataColumnContainer campiDaCopiareW_DOCDIG = new DataColumnContainer( this.sqlManager, "W_DOCDIG", selectW_DOCDIG, new Object[] { idprg, iddocdig });
	                        campiDaCopiareW_DOCDIG.setValoriFromMap( occorrenzaW_DOCDIGDaCopiare, true);
	                        campiDaCopiareW_DOCDIG.getColumn("IDPRG").setChiave(true);
	                        campiDaCopiareW_DOCDIG.getColumn("IDDOCDIG").setChiave(true);
	                        // Si deve calcolare il valore di IDDOCDIG
	                        Long maxIDDOCDIG = (Long) this.sqlManager.getObject( "select max(IDDOCDIG) from W_DOCDIG where IDPRG= ?", new Object[] { "PG" });
	                        long newIDDOCDIG = 1;
	                        if (maxIDDOCDIG != null && maxIDDOCDIG.longValue() > 0){
	                          newIDDOCDIG = maxIDDOCDIG.longValue() + 1;
	                        }
                            campiDaCopiareW_DOCDIG.setValue("IDPRG", "PG");
	                        campiDaCopiareW_DOCDIG.setValue("IDDOCDIG", Long.valueOf( newIDDOCDIG));
	                        campiDaCopiareW_DOCDIG.setValue("DIGENT", "G1DOCSTIPULA");
	                        Long idDocStipula =Long.valueOf(nextId);
	                        String digkey1 =  idDocStipula.toString();
	                        campiDaCopiareW_DOCDIG.setValue("DIGKEY1", digkey1);
	                        
	
	                        BlobFile fileAllegato = null;
	                        fileAllegato = fileAllegatoManager.getFileAllegato(idprg, iddocdig);
	                        ByteArrayOutputStream baos = null;
	                        if (fileAllegato != null && fileAllegato.getStream() != null) {
	                          baos = new ByteArrayOutputStream();
	                          baos.write(fileAllegato.getStream());
	                        }
	                        campiDaCopiareW_DOCDIG.addColumn("W_DOCDIG.DIGOGG", JdbcParametro.TIPO_BINARIO, baos);
	                        // Inserimento del nuovo record su w_docdig
	                        campiDaCopiareW_DOCDIG.insert("W_DOCDIG", this.sqlManager);
                            ObjectStorageUtils.addFile("PG", newIDDOCDIG);
	                    	
	                    }
	                } catch (IOException e) {
	                    throw new GestoreException(
	                        "Errore nella lettura del campo BLOB DIGOGG della W_DOCDIG", "insertElencoDocumentazionePredefinita", e);
	                } catch (SQLException e) {
	                    throw new GestoreException(
	                        "Errore nella copia dell'allegato per entita' G1DOCSTIPULA", "insertElencoDocumentazionePredefinita", e);
	                }
                    

                }

            }

        }
    	
    }

          

}
