/*
 * Created on 07-07-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.TempFileUtilities;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.web.struts.UploadMultiploForm;
import it.eldasoft.utils.spring.UtilitySpring;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore per update per Popup-ModificaOrdinamentoDocumentazione
 *
 * @author Francesco Di Mattei
 */
public class GestorePopupModificaOrdinamentoDocumentazione extends AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestorePopupModificaOrdinamentoDocumentazione.class);

  @Override
  public String getEntita() {
    return "DOCUMGARA";
  }

  public GestorePopupModificaOrdinamentoDocumentazione() {
    super(false);
  }

  public GestorePopupModificaOrdinamentoDocumentazione(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    if (logger.isDebugEnabled()) {
      logger.debug("GestorePopupModificaOrdinamentoDocumentazione: preUpdate: inizio metodo");
    }

    String codgar = UtilityStruts.getParametroString(this.getRequest(),
    "codgar1");
    
    String tipoDoc = UtilityStruts.getParametroString(this.getRequest(),
    "tipoDoc");
    
    
    Long gruppo = new Long(tipoDoc);
    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        this.getServletContext(), PgManagerEst1.class);
    ////////////////////////////////////////////////////////////////////////////////
    //Ricalcolo NUMORD.DOCUMGARA
    pgManagerEst1.ricalcNumordDocGara(codgar, gruppo);
    ////////////////////////////////////////////////////////////////////////////////
    this.getRequest().setAttribute("esitoRicalcNumordDocGara", "1");
    if (logger.isDebugEnabled()) {
      logger.debug("GestorePopupModificaOrdinamentoDocumentazione: preUpdate: fine metodo");
    }
  }

  
  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
   
  }

}