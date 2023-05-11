/*
 * Created on 26/03/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoType;

public class VerificaFirmaWSDMTask {

  static Logger           logger = Logger.getLogger(VerificaFirmaWSDMTask.class);

  private SqlManager      sqlManager;

  private GestioneWSDMManager      gestioneWSDMManager;

  /**
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
  *
  * @param sqlManager
  */
 public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
   this.gestioneWSDMManager = gestioneWSDMManager;
 }

  /**
   *
   * @throws GestoreException
   */
  public void verificaFirma() throws GestoreException {

    if (WebUtilities.isAppNotReady()) return;

    Long id=null;
    String idprg=null;
    Long iddocdig=null;
    String numerodoc=null;
    Long idconfi=null;
    String username = null;
    String password = null;
    String passwordDecoded = null;
    String idconfiString = null;
    String nomeAllegato = null;
    byte[] contenutoAllegato = null;
    Long annoProtocollo = null;
    Calendar dataProtocollo = null;
    String numeroProtocollo = null;
    TransactionStatus status = null;
    boolean commitTransaction=true;
    LobHandler lobHandler = null;
    String tipoWSDM = null;
    String firmaDocumentiAttiva = null;
    String digkey1 = null;

    if (logger.isDebugEnabled())
      logger.debug("verificaFirma: inizio metodo");

    String select="select f.id, f.idprg,f.iddocdig, f.numerodoc, f.idconfiwsdm, l.username, l.password, d.digkey1 from WSRICFIRMA f, w_docdig d, wslogin l where f.idprg = d.idprg "
        + "and f.iddocdig = d.iddocdig and l.syscon=f.syscon and l.servizio='FASCICOLOPROTOCOLLO' and l.idconfiwsdm = f.idconfiwsdm  and d.digfirma='1' and "
        + "(f.numeroprot is null or f.numeroprot ='') order by f.id";

    List<?> listaRichiesteFirma = null;
    try {
      listaRichiesteFirma = sqlManager.getListVector(select,new Object[] {});
    }catch (SQLException e) {
      throw new GestoreException("Errore nella lettura delle richiesta di firma da processare", null, e);
    }
    if (listaRichiesteFirma != null && listaRichiesteFirma.size() > 0) {
      for (int i = 0; i < listaRichiesteFirma.size(); i++) {
        try {
          id = SqlManager.getValueFromVectorParam(listaRichiesteFirma.get(i), 0).longValue();
          idprg = SqlManager.getValueFromVectorParam(listaRichiesteFirma.get(i), 1).getStringValue();
          iddocdig = SqlManager.getValueFromVectorParam(listaRichiesteFirma.get(i), 2).longValue();
          numerodoc = SqlManager.getValueFromVectorParam(listaRichiesteFirma.get(i), 3).getStringValue();
          idconfi = SqlManager.getValueFromVectorParam(listaRichiesteFirma.get(i), 4).longValue();
          username = SqlManager.getValueFromVectorParam(listaRichiesteFirma.get(i), 5).getStringValue();
          password = SqlManager.getValueFromVectorParam(listaRichiesteFirma.get(i), 6).getStringValue();
          digkey1 = SqlManager.getValueFromVectorParam(listaRichiesteFirma.get(i), 7).getStringValue();

          if(idconfi!=null)
            idconfiString = idconfi.toString();
          else
            idconfiString = "";

          boolean integrazioneWSDMAttiva = this.gestioneWSDMManager.isIntegrazioneWSDMAttivaValida(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO,idconfiString);
          if(integrazioneWSDMAttiva) {
            tipoWSDM="";
            WSDMConfigurazioneOutType configurazione = this.gestioneWSDMManager.wsdmConfigurazioneLeggi(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO,idconfiString);
            if(configurazione.isEsito())
              tipoWSDM = configurazione.getRemotewsdm();
            if("ITALPROT".equals(tipoWSDM)) {
              firmaDocumentiAttiva = ConfigManager.getValore("wsdm.firmaDocumenti."+idconfiString);
              if("1".equals(firmaDocumentiAttiva)) {
                passwordDecoded = GestioneWSDMManager.decodificaPassword(password);


                WSDMProtocolloDocumentoResType res = this.gestioneWSDMManager.wsdmFirmaVerifica(username, passwordDecoded, numerodoc, idconfiString);
                if(res.isEsito()) {
                  WSDMProtocolloDocumentoType docType = res.getProtocolloDocumento();
                  annoProtocollo = docType.getAnnoProtocollo();
                  dataProtocollo = docType.getDataProtocollo();
                  numeroProtocollo = docType.getNumeroProtocollo();
                  WSDMProtocolloAllegatoType  allegatoType[] = docType.getAllegati();
                  if(allegatoType!=null && allegatoType.length>0) {
                    for(int j=0; j < allegatoType.length; j++) {
                      nomeAllegato = allegatoType[j].getNome();
                      contenutoAllegato = allegatoType[j].getContenuto();
                      lobHandler = new DefaultLobHandler();
                      try {
                        status = this.sqlManager.startTransaction();
                        this.sqlManager.update("update w_docdig set dignomdoc=?, digogg=?, digfirma=null where idprg=? and iddocdig=?", new Object[] {nomeAllegato, new SqlLobValue(contenutoAllegato, lobHandler), idprg, iddocdig });
                        //Se l'allegato è relativo a C0OGGASS si deve aggiornare il nome del file in C0ANOMOGG
                        if(StringUtils.isNumeric(digkey1)) {
                          this.sqlManager.update("update C0OGGASS set C0ANOMOGG=? where C0ACOD=?", new Object[] {nomeAllegato,new Long(digkey1) });
                        }
                        this.sqlManager.update("update wsricfirma set annoprot=?, numeroprot=?, datprot=? where id=?", new Object[] {annoProtocollo, numeroProtocollo,  new Timestamp(dataProtocollo.getTime().getTime()), id});
                        commitTransaction=true;
                      }catch (Exception e) {
                        commitTransaction = false;
                        logger.error("Errore nella verifica della richiesta di firma del documento:" + numerodoc, e);
                      } finally {
                        if (status != null) {
                          if (commitTransaction) {
                            this.sqlManager.commitTransaction(status);
                          } else {
                            this.sqlManager.rollbackTransaction(status);
                          }
                        }
                      }
                    }
                  }
                }else {
                  logger.error("Verifica della richiesta di firma del documento:" + numerodoc + " - " + res.getMessaggio());
                }
              }
            }
          }
        }catch (Exception e) {
          logger.error("Errore nella verifica della richiesta di firma del documento:" + numerodoc, e);
        }

       }
     }

    if (logger.isDebugEnabled())
      logger.debug("verificaFirma: fine metodo");
  }
}
