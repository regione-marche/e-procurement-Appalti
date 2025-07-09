/*
 * Created on 19/03/2024
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.bl.erp.SaceERPManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.SpringAppContext;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class AcquisisciAggiornamentiERPAnagraficaManager {

  static Logger           logger = Logger.getLogger(AcquisisciAggiornamentiERPAnagraficaManager.class);

  private SqlManager      sqlManager;
  private GenChiaviManager    genChiaviManager;
  private SaceERPManager    saceERPManager;
  private GestioneWSERPManager gestioneWSERPManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  public SqlManager getSqlManager() {
    return this.sqlManager;
  }
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }
  public void setSaceERPManager(SaceERPManager saceERPManager) {
    this.saceERPManager = saceERPManager;
  }
  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }
  
  public static final String APPALTI_MS_SACE_WS_BASEURL = "sace.ws.baseUrl";
  
  public void acquisisciAggiornamentiERPAnagrafica() throws GestoreException {

    if (WebUtilities.isAppNotReady()) return;
    ServletContext context = SpringAppContext.getServletContext();

    if (logger.isDebugEnabled()){
      logger.debug("acquisisciAggiornamentiERPAnagrafica: inizio metodo");
    }
    
    String url = ConfigManager.getValore(APPALTI_MS_SACE_WS_BASEURL);
    if (url != null && !"".equals(url)) {
      try {
        /**
         * tabella G1MSFLUSSI:
         *    CHIAVE_INT = CODICE DELL'IMPRESA
         *    CHIAVE_EXT:
         *        SACE = CODICE MICHELANGELO
         *    TIPO_FLUSSO:
         *        1 = INVIO A SERVIZIO ESTERNO NEL NOSTRO CASO SACE IL NOSTRO CODICE IMPRESA
         *        2 = RISCHIESTA DI AGGIORNAMENTO DA PARTE DI SACE DI UNA NOSTRA ANAGRAFICA
         */
        String entita = "IMPR";
        
        String queryFlussoUno = "select g.chiave_int, i.nazimp, i.nomimp, i.indimp, i.capimp, i.cfimp, g.id, g.stato "
            + " from G1MSFLUSSI g "
            + " inner join IMPR i on i.codimp = g.chiave_int "
            + " where g.fonte = ? "
            + "   and g.entita = ? "
            + "   and g.stato in (1,3,10) "
            + "   and g.tipo_flusso = 1 ";
        List<String> datiFlussoUno = this.sqlManager.getListVector(queryFlussoUno, new Object[]{ "MAG", entita });
        if(datiFlussoUno!=null && !datiFlussoUno.isEmpty()){
          for (int i = 0; i < datiFlussoUno.size(); i++) {
            String codiceImpresa = (String) SqlManager.getValueFromVectorParam(datiFlussoUno.get(i), 0).getValue();
            Long nazioneImpresa = (Long) SqlManager.getValueFromVectorParam(datiFlussoUno.get(i), 1).getValue();
            if(nazioneImpresa == null) {
              nazioneImpresa = Long.valueOf(1);
            }
            String ragSociale = (String) SqlManager.getValueFromVectorParam(datiFlussoUno.get(i), 2).getValue();
            String indirizzoImpresa = (String) SqlManager.getValueFromVectorParam(datiFlussoUno.get(i), 3).getValue();
            String capImpresa = (String) SqlManager.getValueFromVectorParam(datiFlussoUno.get(i), 4).getValue();
            capImpresa = StringUtils.stripToEmpty(capImpresa);
            String codiceFiscaleImpresa = (String) SqlManager.getValueFromVectorParam(datiFlussoUno.get(i), 5).getValue();
            Long idFlusso = (Long) SqlManager.getValueFromVectorParam(datiFlussoUno.get(i), 6).getValue();
            Long statoFlusso = (Long) SqlManager.getValueFromVectorParam(datiFlussoUno.get(i), 7).getValue();
            
            TransactionStatus status = null;
            boolean doCommit = true;
            try {
                status = this.sqlManager.startTransaction();
                saceERPManager.invioCodiceImpresa(codiceImpresa,codiceFiscaleImpresa,nazioneImpresa,ragSociale,indirizzoImpresa,capImpresa,idFlusso,statoFlusso);
            }catch (Exception e) {
                doCommit = false;
                logger.error("Errore in invio codice impresa: "+e.getMessage());
            } finally {
                if (status != null) {
                  try {
                    if (doCommit) {
                      this.sqlManager.commitTransaction(status);
                    } else {
                      this.sqlManager.rollbackTransaction(status);
                    }
                  } catch (SQLException ex) {
                    throw new GestoreException("", "", ex);
                  }
                }
            }

          }// for flusso uno
        }//if flusso uno
        
        String queryFlussoDue = "select g.chiave_ext, g.id "
            + " from G1MSFLUSSI g "
            + " inner join IMPR i on i.codimp = g.chiave_int and i.cgenimp = g.chiave_ext "
            + " where (g.fonte = ? or g.fonte = ?) "
            + "   and g.entita = ? "
            + "   and g.stato in (1,3) "
            + "   and g.tipo_flusso = 2 "
            + "   and i.cgenimp is not null ";
        List<String> datiFlussoDue = this.sqlManager.getListVector(queryFlussoDue, new Object[]{ "SACE", "MAG", entita });
        if(datiFlussoDue!=null && !datiFlussoDue.isEmpty()){
          for (int i = 0; i < datiFlussoDue.size(); i++) {
            String codiceEsterno = (String) SqlManager.getValueFromVectorParam(datiFlussoDue.get(i), 0).getValue();
            Long idFlusso = (Long) SqlManager.getValueFromVectorParam(datiFlussoDue.get(i), 1).getValue();
            
            TransactionStatus status = null;
            boolean doCommit = true;
            try {
                status = this.sqlManager.startTransaction();
                saceERPManager.aggiornamentoDatiImpresa(codiceEsterno,idFlusso);
            }catch (Exception e) {
                doCommit = false;
                logger.error("Errore in aggiornamento dati impresa: "+e.getMessage());
            } finally {
                if (status != null) {
                  try {
                    if (doCommit) {
                      this.sqlManager.commitTransaction(status);
                    } else {
                      this.sqlManager.rollbackTransaction(status);
                    }
                  } catch (SQLException ex) {
                    throw new GestoreException("", "", ex);
                  }
                }
            }
            
          }//for flusso due
        }//if flusso due
        
        if (logger.isDebugEnabled()){
          logger.debug("acquisisciAggiornamentiERPAnagrafica: fine metodo");
        }
      } catch (SQLException ex) {
      	throw new GestoreException("", "", ex);
      }        
      
    }
  }

  private String getDatiDaResponse(InputStream input) throws IOException {
    StringBuilder sb = new StringBuilder();
    if(input != null) {
      BufferedReader br = new BufferedReader(new InputStreamReader(input));
      String strLine;
      while ((strLine = br.readLine()) != null) {
        sb.append(strLine);
      }
      br.close();
    }
    return sb.toString();
  }

  private String recuperoErroreDaResponse(JSONObject response) throws GestoreException {
    String errorMessage = "";
    if(response.has("Esito") && response.has("Messaggio")) {
      errorMessage = response.getString("Messaggio");
    }
    return errorMessage;
  }

}
