/*
 * Created on 16/nov/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.bl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.domain.BlobFile;


public class ScaricaDocumentiManager {

  static Logger logger = Logger.getLogger(ScaricaDocumentiManager.class);
  
  private SqlManager          sqlManager;

  private FileAllegatoManager fileAllegatoManager;
  
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }
  
  public void creaArchivio(final ActionMapping mapping,
      final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception{

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String pathArchivioDocumenti = System.getProperty("java.io.tmpdir");

    String nomeArchivio = request.getParameter("path") + ".zip";
    String archivioCreato = request.getParameter("archivioCreato");
    String stringIdddocdg = request.getParameter("idddocdg");
    String idprg = request.getParameter("idprg");

    logger.debug("ScaricaTuttiDocumentiBustaAction: download documento con identificativo: "
        + idprg + ", " + stringIdddocdg + "; creazione archivio: " + nomeArchivio);

    Long iddocdg = new Long(stringIdddocdg);

    ZipOutputStream zipOut = null;
    String nomeFile = (String)sqlManager.getObject("select dignomdoc from w_docdig where idprg = ? and iddocdig = ?", new Object[] {idprg,iddocdg});
    if("false".equals(archivioCreato)){
      zipOut = new ZipOutputStream(new FileOutputStream(pathArchivioDocumenti + "/" + nomeArchivio));
      zipOut.setMethod(ZipOutputStream.DEFLATED);
      zipOut.setLevel(Deflater.DEFAULT_COMPRESSION);
      this.addFileToArchive(nomeArchivio,pathArchivioDocumenti,idprg,iddocdg,nomeFile,zipOut);
      zipOut.closeEntry();
      zipOut.flush();
      zipOut.close();
    }else{
      addFilesToZip(pathArchivioDocumenti + "/" + nomeArchivio,idprg, iddocdg);
    }

    JSONObject result = new JSONObject();

    out.print(result);
    out.flush();

  }
  
  public void addFileToArchive(String nomeArchivio, String pathArchivioDocumenti, String idprg, Long iddocdig, String nome, ZipOutputStream zipOut) throws Exception {
    BlobFile file = this.fileAllegatoManager.getFileAllegato(idprg,iddocdig);
    OutputStream out = new FileOutputStream(pathArchivioDocumenti + "/" + nomeArchivio);
    out.write(file.getStream());
    out.close();
    //Aggiungo il file allo zip
    try {
        zipOut.putNextEntry(new ZipEntry(nome));
        zipOut.write(file.getStream(), 0, file.getStream().length);
    } catch (ZipException e) {
        // il file è già presente nello zip .., lo ignoro
        ;
    }
  }

  public void addFilesToZip(String sourcePath, String idprg, Long iddocdig){
    try{
        File source = new File(sourcePath);
        BlobFile file = this.fileAllegatoManager.getFileAllegato(idprg,iddocdig);
        File tmpZip = File.createTempFile(source.getName(), null);
        tmpZip.delete();
        if(!source.renameTo(tmpZip)){
            throw new Exception("Could not make temp file (" + source.getName() + ")");
        }
        byte[] buffer = new byte[4096];
        ZipInputStream zin = new ZipInputStream(new FileInputStream(tmpZip));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(source));

        out.putNextEntry(new ZipEntry(file.getNome()));
        out.write(file.getStream(), 0, file.getStream().length);
        out.closeEntry();

        for(ZipEntry ze = zin.getNextEntry(); ze != null; ze = zin.getNextEntry()){
            if(!zipEntryMatch(ze.getName(), file)){
                out.putNextEntry(ze);
                for(int read = zin.read(buffer); read > -1; read = zin.read(buffer)){
                    out.write(buffer, 0, read);
                }
                out.closeEntry();
            }
        }
        out.close();
        zin.close();
        tmpZip.delete();
    }catch(Exception e){
        e.printStackTrace();
    }
  }

  public static String replaceInvalidChar(String filename){
    String res = filename.replaceAll("[\\\\/:\"*?<>| ]", "_");
    return res;
  }

  public boolean zipEntryMatch(String zeName, BlobFile file){
        if((file.getNome()).equals(zeName)){
            return true;
        }
      return false;
  }
  
}
