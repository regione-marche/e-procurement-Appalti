package it.eldasoft.sil.w3.bl;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.w3.db.dao.DocumentoAllegatoDao;
import it.eldasoft.utils.utility.UtilityWeb;

import org.apache.log4j.Logger;

public class DocumentoAllegatoManager {

  static Logger           logger = Logger.getLogger(DocumentoAllegatoManager.class);

  private DocumentoAllegatoDao documentoAllegatoDao;


  /**
   * 
   * @param documentoAllegatoDao
   */
  public void setDocumentoAllegatoDao(DocumentoAllegatoDao documentoAllegatoDao) {
    this.documentoAllegatoDao = documentoAllegatoDao;
  }

  /**
   * 
   * @param params
   * @param response
   * @throws IOException
   * @throws GestoreException
   */
  public void downloadDocumentoAllegato(String dignomdoc, HashMap params, HttpServletResponse response)
      throws IOException, GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("downloadDocumentoAllegato: inizio metodo");

    BlobFile documentoAllegatoBlob = this.documentoAllegatoDao.getDocumentoAllegato(params);
    UtilityWeb.download(dignomdoc, documentoAllegatoBlob.getStream(), response);

    if (logger.isDebugEnabled())
      logger.debug("downloadDocumentoAllegato: fine metodo");
  }

}
