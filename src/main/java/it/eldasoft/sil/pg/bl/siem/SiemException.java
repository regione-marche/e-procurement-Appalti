package it.eldasoft.sil.pg.bl.siem;

/**
 * Eccezione relativa alla classe CloudLoggerManager.
 *
 * @author Alvise Gorinati
 */
public class SiemException extends Exception {

  // === Costruttori ===========================================================
  /**
   * Costruttore di default con messaggio d'erroe.
   */
  public SiemException(final String message) {
    super(message);
  }

}

