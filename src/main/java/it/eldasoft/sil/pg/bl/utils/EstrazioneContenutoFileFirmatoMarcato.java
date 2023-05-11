package it.eldasoft.sil.pg.bl.utils;

import org.apache.log4j.Logger;

import it.eldasoft.utils.sign.DigitalSignatureChecker;

/**
 * Questa classe riprende e rifattorizza il metodo della classe {@link it.eldasoft.gene.web.struts.DownloadDocumentoFirmatoAction DownloadDocumentoFirmatoAction}
 * di GeneWeb per poterlo usare in molteplici punti.
 * <br>
 * <br>Da rivedere quando disponibile un nuovo servizio esterno di estrazione contenuto file firmati / marcati.
 * @author gabriele.nencini
 *
 */
public class EstrazioneContenutoFileFirmatoMarcato {
  private static final Logger logger = Logger.getLogger(EstrazioneContenutoFileFirmatoMarcato.class);
  /*
   * prese da DownloadDocumentoFirmatoAction 
   */
  private static final String DOCUMENTO_MARCATO           = "tsd";
  private static final String DOCUMENTO_FIRMATO           = "p7m";
  
  /**
   * Estrae il contenuto del file firmato o marcato utilizzando {@link DigitalSignatureChecker}
   * @param doc il documento
   * @param fileName il nome del documento
   * @return ultima operazione sul contenuto del file oppure <code>null</code>
   */
  public static byte[] estraiContenutoFile(byte[] doc, String fileName) {
    try {
      if(doc != null) {
        String[] fileNameSplit = fileName.split("\\.");
        String ext = fileNameSplit[fileNameSplit.length - 1].toLowerCase();
        DigitalSignatureChecker digitalSignatureChecker = new DigitalSignatureChecker();
        while (true) {// ciclo infinito
          if (DOCUMENTO_MARCATO.equals(ext)) {
            byte[] innerDoc = digitalSignatureChecker.getContentTimeStamp(doc);
            if (DOCUMENTO_FIRMATO.equals(fileNameSplit[fileNameSplit.length - 2])) {
              doc = digitalSignatureChecker.getContent(innerDoc);
            } else {
              // caso di file non firmato ma solamente marcato temporalmente
              doc = innerDoc;
            }
  
          } else {
            doc = digitalSignatureChecker.getContent(doc);
          }
        }
      }
    } catch (Exception e) {
      logger.debug("Errore nello sbustamento, restituisco ultima operazione elaborata sul file. ["+e.getMessage()+"]");
      if(logger.isDebugEnabled()) {
        logger.debug("Trace full error.",e);
      }
    }
    return doc;
  }

}
