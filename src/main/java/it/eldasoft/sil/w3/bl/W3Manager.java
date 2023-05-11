/*
 * Created on 13/nov/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.w3.bl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.LoginManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.system.LdapManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.sicurezza.CriptazioneException;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.CommunicationException;

public class W3Manager {

  /** Logger */
  static Logger              logger = Logger.getLogger(W3Manager.class);

  private SqlManager         sqlManager;

  protected GenChiaviManager genChiaviManager;

  protected LoginManager     loginManager;

  private LdapManager        ldapManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  public void setLoginManager(LoginManager loginManager) {
    this.loginManager = loginManager;
  }

  public void setLdapManager(LdapManager ldapManager) {
    this.ldapManager = ldapManager;
  }


  /**
   * Verifica l'esistenza di una riga in W3GARA in funzione del numero gara
   * rilasciato da SIMOG
   * 
   * @param idgara
   * @return
   * @throws GestoreException
   */
  public boolean esisteW3GARA_IDGARA(String idgara) throws GestoreException {

    boolean esiste = false;
    try {
      Long conteggio = (Long) sqlManager.getObject(
          "select count(*) from w3gara where id_gara = ?",
          new Object[] { idgara });
      if (conteggio != null && conteggio.longValue() > 0) esiste = true;
    } catch (SQLException e) {
      throw new GestoreException("Errore durante la lettura della gara",
          "controlloW3GARA", e);
    }

    return esiste;
  }

  /**
   * Verifica l'esistenza di una riga in W3SMARTCIG in funzione del smartcig
   * rilasciato da SIMOG
   * 
   * @param idgara
   * @return
   * @throws GestoreException
   */
  public boolean esisteW3SMARTCIG(String smartcig) throws GestoreException {

    boolean esiste = false;
    try {
      Long conteggio = (Long) sqlManager.getObject(
          "select count(*) from W3SMARTCIG where UPPER(CIG) = ?",
          new Object[] { smartcig.toUpperCase() });
      if (conteggio != null && conteggio.longValue() > 0) esiste = true;
    } catch (SQLException e) {
      throw new GestoreException("Errore durante la lettura dello smartcig",
          "controlloW3GARA", e);
    }

    return esiste;
  }
  /**
   * Verifica l'esistenza di una riga in W3GARA in funzione del identificativo
   * univoco universale UUID
   * 
   * @param uuid
   * @return
   * @throws GestoreException
   */
  public boolean esisteW3GARA_UUID(String uuid) throws GestoreException {

    boolean esiste = false;
    try {
      Long conteggio = (Long) sqlManager.getObject(
          "select count(*) from w3gara where gara_uuid = ?",
          new Object[] { uuid });
      if (conteggio != null && conteggio.longValue() > 0) esiste = true;
    } catch (SQLException e) {
      throw new GestoreException("Errore durante la lettura della gara",
          "controlloW3GARA", e);
    }

    return esiste;
  }

  /**
   * Restituisce la chiave NUMGARA della tabella W3GARA per la riga individuata
   * mediante il codice identificativo universale UUID
   * 
   * @param uuid
   * @return
   * @throws GestoreException
   */
  public Long getNUMGARA_UUID(String uuid) throws GestoreException {

    Long numgara = null;
    try {
      numgara = (Long) sqlManager.getObject(
          "select numgara from w3gara where gara_uuid = ?",
          new Object[] { uuid });
    } catch (SQLException e) {
      throw new GestoreException("Errore durante la lettura della gara",
          "controlloW3GARA", e);
    }

    return numgara;
  }

  /**
   * Restituisce la chiave NUMGARA della tabella W3GARA per la riga individuata
   * mediante il numero gara IDGARA rilasciato da SIMOG
   * 
   * @param uuid
   * @return
   * @throws GestoreException
   */
  public Long getNUMGARA_IDGARA(String idgara) throws GestoreException {

    Long numgara = null;
    try {
      numgara = (Long) sqlManager.getObject(
          "select numgara from w3gara where id_gara = ?",
          new Object[] { idgara });
    } catch (SQLException e) {
      throw new GestoreException("Errore durante la lettura della gara",
          "controlloW3GARA", e);
    }

    return numgara;
  }

  /**
   * Restituisce la coppia di chiavi NUMGARA/NUMLOTT della tabella W3LOTT per la
   * riga identificata dall'identificativo univoco universale UUID
   * 
   * @param uuid
   * @return
   * @throws GestoreException
   */
  public HashMap<String, Long> getNUMGARA_NUMLOTT_UUID(String uuid)
      throws GestoreException {

    HashMap<String, Long> hMap = new HashMap<String, Long>();

    try {
      List<?> datiW3LOTT = this.sqlManager.getVector(
          "select numgara, numlott from w3lott where lotto_uuid = ?",
          new Object[] { uuid });
      if (datiW3LOTT != null && datiW3LOTT.size() > 0) {
        Long numgara = (Long) SqlManager.getValueFromVectorParam(datiW3LOTT, 0).getValue();
        Long numlott = (Long) SqlManager.getValueFromVectorParam(datiW3LOTT, 1).getValue();

        hMap.put("numgara", numgara);
        hMap.put("numlott", numlott);

      }

    } catch (SQLException e) {
      throw new GestoreException("Errore durante la lettura del lotto",
          "controlloW3LOTT", e);
    }

    return hMap;
  }

  /**
   * Verifica l'esistenza di una riga in W3LOTT in funzione del CIG rilasciato
   * da SIMOG
   * 
   * @param cig
   * @return
   * @throws GestoreException
   */
  public boolean esisteW3LOTT_CIG(String cig) throws GestoreException {

    boolean esiste = false;
    try {
      Long conteggio = (Long) sqlManager.getObject(
          "select count(*) from w3lott where UPPER(cig) = ?", new Object[] { cig.toUpperCase() });
      if (conteggio != null && conteggio.longValue() > 0) esiste = true;
    } catch (SQLException e) {
      throw new GestoreException("Errore durante la lettura del lotto",
          "controlloW3LOTT", e);
    }

    return esiste;
  }

  /**
   * Verifica l'esistenza di una riga in W3LOTT in funzione del identificativo
   * univoco universale UUID
   * 
   * @param uuid
   * @return
   * @throws GestoreException
   */
  public boolean esisteW3LOTT_UUID(String uuid) throws GestoreException {

    boolean esiste = false;
    try {
      Long conteggio = (Long) sqlManager.getObject(
          "select count(*) from w3lott where lotto_uuid = ?",
          new Object[] { uuid });
      if (conteggio != null && conteggio.longValue() > 0) esiste = true;
    } catch (SQLException e) {
      throw new GestoreException("Errore durante la lettura del lotto",
          "controlloW3LOTT", e);
    }

    return esiste;
  }

  /**
   * Verifica se la gara individuata dal codice identificativo UUID è
   * modificabile
   * 
   * @param uuid
   * @return
   * @throws GestoreException
   */
  public boolean isW3GARA_UUIDModificabile(String uuid) throws GestoreException {

    boolean isModificabile = false;
    try {
      Long stato_simog = (Long) sqlManager.getObject(
          "select stato_simog from w3gara where gara_uuid = ?",
          new Object[] { uuid });
      isModificabile = this.isSTATO_SIMOGModificabile(stato_simog);

    } catch (SQLException e) {
      throw new GestoreException("Errore durante la lettura della gara",
          "controlloW3GARA", e);
    }

    return isModificabile;
  }

  /**
   * Test sullo stato della gara/lotto (STATO_SIMOG) per verificare se si tratta
   * di una gara o un lotto modificabile
   * 
   * Una gara o un lotto sono modificabili se in uno dei seguenti stati:
   * <ul>
   * <li>1 - Dati da inviare a SIMOG</li>
   * <li>2 - Dati inviati a SIMOG</li>
   * <li>3 - Dati modificati, richiesta di modifica da inoltrare a SIMOG</li>
   * <li>4 - Dati inviati a SIMOG (dopo richiesta di modifica)</li>
   * </ul>
   * 
   * Gli altri stati:
   * <ul>
   * <li>5 - Dati in richiesta di cancellazione</li>
   * <li>6 - Dati cancellati</li>
   * <li>7 - Dati pubblicati</li>
   * <li>99 - Dati recuperati da SIMOG</li>
   * </ul>
   * 
   * rendono la gara o il lotto non modificabili
   * 
   * 
   * @param stato_simog
   * @return
   */
  private boolean isSTATO_SIMOGModificabile(Long stato_simog) {

    boolean isModificabile = false;

    if (stato_simog != null) {
      switch (stato_simog.intValue()) {
      case 1:
      case 2:
      case 3:
      case 4:
        isModificabile = true;
        break;

      case 5:
      case 6:
      case 7:
      case 99:
        isModificabile = false;

      default:
        break;
      }
    }
    return isModificabile;
  }

  /**
   * Verifica se la gara individuata dal numero gara IDGARA rilasciato da SIMOG
   * è modificabile
   * 
   * @param uuid
   * @return
   * @throws GestoreException
   */
  public boolean isW3GARA_IDGARAModificabile(String idgara)
      throws GestoreException {

    boolean isModificabile = false;
    try {
      Long stato_simog = (Long) sqlManager.getObject(
          "select stato_simog from w3gara where id_gara = ?",
          new Object[] { idgara });

      isModificabile = isSTATO_SIMOGModificabile(stato_simog);

    } catch (SQLException e) {
      throw new GestoreException("Errore durante la lettura della gara",
          "controlloW3GARA", e);
    }

    return isModificabile;
  }

  /**
   * Verifica se il lotto individuata dal codice identificativo UUID è
   * modificabile
   * 
   * @param uuid
   * @return
   * @throws GestoreException
   */
  public boolean isW3LOTT_UUIDModificabile(String uuid) throws GestoreException {

    boolean isModificabile = false;
    try {
      Long stato_simog = (Long) sqlManager.getObject(
          "select stato_simog from w3lott where lotto_uuid = ?",
          new Object[] { uuid });
      isModificabile = isSTATO_SIMOGModificabile(stato_simog);

    } catch (SQLException e) {
      throw new GestoreException("Errore durante la lettura del lotto",
          "controlloW3LOTT", e);
    }

    return isModificabile;
  }

  /**
   * Verifica se il lotto individuato dal CIG rilasciato da SIMOG è modificabile
   * 
   * @param uuid
   * @return
   * @throws GestoreException
   */
  public boolean isW3LOTT_CIGModificabile(String cig) throws GestoreException {

    boolean isModificabile = false;
    try {
      Long stato_simog = (Long) sqlManager.getObject(
          "select stato_simog from w3lott where cig = ?", new Object[] { cig });

      isModificabile = isSTATO_SIMOGModificabile(stato_simog);

    } catch (SQLException e) {
      throw new GestoreException("Errore durante la lettura del lotto",
          "controlloW3LOTT", e);
    }

    return isModificabile;
  }

  /**
   * Protezione archivi
   * 
   * @param idAccount
   * @param archivio
   * @param codice
   * @throws GestoreException
   * @throws SQLException
   */
  public void gestioneProtezioneArchivi(Long idAccount, String archivio,
      String codice) throws GestoreException, SQLException {

    Long numper = new Long(this.genChiaviManager.getMaxId("W3PERMESSI",
        "NUMPER") + 1);
    DataColumn[] dcW3PERMESSI = new DataColumn[] { new DataColumn(
        "W3PERMESSI.NUMPER", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
            numper)) };
    DataColumnContainer dccW3PERMESSI = new DataColumnContainer(dcW3PERMESSI);
    dccW3PERMESSI.addColumn("W3PERMESSI.SYSCON", JdbcParametro.TIPO_NUMERICO,
        idAccount);

    if ("IMPR".equals(archivio)) {
      dccW3PERMESSI.addColumn("W3PERMESSI.CODIMP", JdbcParametro.TIPO_TESTO,
          codice);
    } else if ("TECNI".equals(archivio)) {
      dccW3PERMESSI.addColumn("W3PERMESSI.CODTEC", JdbcParametro.TIPO_TESTO,
          codice);
    } else if ("UFFINT".equals(archivio)) {
      dccW3PERMESSI.addColumn("W3PERMESSI.CODEIN", JdbcParametro.TIPO_TESTO,
          codice);
    } else if ("W3AMMI".equals(archivio)) {
      dccW3PERMESSI.addColumn("W3PERMESSI.CODAMM", JdbcParametro.TIPO_TESTO,
          codice);
    }
    dccW3PERMESSI.insert("W3PERMESSI", this.sqlManager);
  }

  /**
   * Procedura di autenticazione. Restituisce l'idAccount. Serve per la
   * memorizzazione del proprietario della gara inserita
   * 
   * @param login
   * @param password
   * @return
   */
  public int getIdAccount(String login, String password) throws Exception {

    if (logger.isDebugEnabled())
      logger.debug("EldasoftSIMAPWSManager.getIdAccount: inizio metodo");

    if ("".equals(password)) password = null;
    Account account = null;

    try {
      if (login == null && password == null) {
        throw new Exception(UtilityTags.getResource(
            "errors.login.mancanoCredenziali", null, false));
      }

      account = this.loginManager.getAccountByLoginEPassword(login, password);
      if (account != null) {
        if (account.getFlagLdap().intValue() == 1) {
          if (password == null && account.getPassword() == null) {
          } else {
            ldapManager.verificaAccount(account.getDn(), password);
          }
        }
      } else {
        throw new Exception(UtilityTags.getResource("errors.login.unknown",
            null, false));
      }

    } catch (CriptazioneException cr) {
      throw new Exception("Errore di autenticazione", cr);
    } catch (DataAccessException d) {
      throw new Exception(UtilityTags.getResource(
          "errors.database.dataAccessException", null, false), d);
    } catch (AuthenticationException a) {
      throw new Exception(UtilityTags.getResource(
          "errors.ldap.autenticazioneFallita", null, false), a);
    } catch (CommunicationException c) {
      throw new Exception(UtilityTags.getResource(
          "errors.ldap.autenticazioneFallita", null, false), c);
    } catch (RuntimeException r) {
      throw new Exception(UtilityTags.getResource("errors.login.loginDoppia",
          null, false), r);
    } catch (Exception e) {
      throw e;
    } catch (Throwable t) {
      throw new Exception(UtilityTags.getResource(
          "errors.applicazione.inaspettataException", null, false), t);
    }

    if (logger.isDebugEnabled())
      logger.debug("EldasoftSIMAPWSManager.getIdAccount: fine metodo");

    return account.getIdAccount();

  }

}
