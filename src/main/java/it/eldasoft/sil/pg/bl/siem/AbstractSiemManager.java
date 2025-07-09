package it.eldasoft.sil.pg.bl.siem;

import java.time.LocalDate;
import java.util.List;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import org.apache.log4j.Logger;
import org.apache.tika.utils.StringUtils;

import it.eldasoft.utils.properties.ConfigManager;
import net.sf.json.JSONObject;

/**
 * Classe astratta da cui derivare le specializzazioni dei sistemi SIEM gestiti
 * da Appalti.
 *
 * @author Alvise Gorinati
 */
public abstract class AbstractSiemManager {

  // === Costanti ==============================================================
  protected final Logger LOGGER;

  /**
   * Query utilizzata per estrarre il tipo di SIEM Manager dalla tabella w_config.
   */
  private static final String REGISTRO_DI_SISTEMA_CHIAVE_CONFIG =
    "it.eldasoft.sil.pg.registroDiSistema";

  /**
   * Query utilizzata per estrarre il nome utente del SIEM Manager dalla tabella
   * w_config.
   */
  private static final String REGISTRO_DI_SISTEMA_USER_CHIAVE_CONFIG =
    "it.eldasoft.sil.pg.registroDiSistema.username";

  /**
   * Query utilizzata per estrarre la password dell'utente del SIEM Manager 
   * dalla tabella w_config.
   */
  private static final String REGISTRO_DI_SISTEMA_PASSWORD_CHIVE_CONFIG =
    "it.eldasoft.sil.pg.registroDiSistema.password";

  /**
   * Query utilizzata per estrarre l'URL di Cloud Logger dalla tabella w_config.
   */
  private static final String REGISTRO_DI_SISTEMA_URL_CHIAVE_CONFIG =
    "it.eldasoft.sil.pg.registroDiSistema.url";

  /**
   * Query utilizzata per estrarre la versione del SIEM Manager dalla tabella 
   * w_config.
   */
  private static final String REGISTRO_DI_SISTEMA_VERSIONE_CHIAVE_CONFIG =
    "it.eldasoft.sil.pg.registroDiSistema.versione";


  private static final String REGISTRO_DI_SISTEMA_AGENTNAME_CHIAVE_CONFIG =
          "it.eldasoft.sil.pg.registroDiSistema.agentName";
  /**
   * Messaggio d'errore utilizzato durante l'inizializzazione della classe.
   */
  private static final String INIT_ERROR_MSG =
    "Errore durante l'inizializzazione del SIEM Manager";

  /**
   * Messaggio d'errore utilizzato quando una proprietà necessaria per il corretto
   * funzionamento dell'implementazione è stata trovata, ma non è valida o è 
   * nulla/vuota.
   */
  private static final String EMPTY_PROPERTY_ERROR_MSG =
    "La proprietà con chiave \"%s\" ha valore nullo non è stata impostata";

  // === Variabili =============================================================
  /**
   * Tipo di SIEM.
   */
  protected String siemType;

  /**
   * Nome utente SIEM.
   */
  protected String siemUsername;

  /**
   * Password dell'utente SIEM.
   */
  protected String siemPassword;

  /**
   * URL del SIEM.
   */
  protected String siemUrl;

  /**
   * Versione del SIEM.
   */
  protected String siemVersion;

  /**
   * Agent name del SIEM.
   */
  protected String siemAgentName;
  
  // === Costruttori ===========================================================
  /**
   * Costruttore di default.
   *
   * @param logger
   *    logger della clase figlia.
   */
  public AbstractSiemManager(final Logger logger) throws SiemException {
    LOGGER = logger;
    siemType = ConfigManager.getValore(REGISTRO_DI_SISTEMA_CHIAVE_CONFIG);
    siemUsername = ConfigManager.getValore(REGISTRO_DI_SISTEMA_USER_CHIAVE_CONFIG);
    siemPassword = ConfigManager.getValore(REGISTRO_DI_SISTEMA_PASSWORD_CHIVE_CONFIG);
    siemUrl = ConfigManager.getValore(REGISTRO_DI_SISTEMA_URL_CHIAVE_CONFIG);
    siemVersion = ConfigManager.getValore(REGISTRO_DI_SISTEMA_VERSIONE_CHIAVE_CONFIG);
    siemAgentName = ConfigManager.getValore(REGISTRO_DI_SISTEMA_AGENTNAME_CHIAVE_CONFIG);
  }

  // === Metodi ================================================================
  /**
   * Esegue l'estrazione dei log dato un determinato target.
   *
   * @param targets
   *    Target dei log da estrarre
   *
   * @return  Oggetto JSON della risposta di Cloud Logger
   */
  public abstract JSONObject extract(List<String> targets);

  /**
   * Esegue l'estrazione di tutti i log nel lasso di tempo specificato.
   *
   * @param startDate
   *    Data inizio estrazione
   * @param endDate
   *    Data fine estrazione
   *
   * @return  Oggetto JSON della risposta di Cloud Logger
   */
  public abstract JSONObject extract(LocalDate startDate, LocalDate endDate);
}

