package it.eldasoft.sil.pg.bl.siem;

import java.util.Map;
import java.util.HashMap;

public class SiemManagerResolver {

  // Costanti ==================================================================
  private static final String NO_SIEM_SET_ERROR_MSG =
    "Sistema SIEM non impostato";
  
  private static final String UNHANDLED_SIEM_ERROR_MSG =
    "Sistema SIEM non gestito o invalido: %s";

  private static final String NO_SIEM_SET       = "0";
  private static final String SIEM_MAGGIOLI     = "1";
  private static final String SIEM_REG_MARCHE   = "2";

  private final String SIEM_TYPE;
  private final Map<String, AbstractSiemManager> MANAGERS;

  // Costruttori ===============================================================
  /**
   *
   */
  public SiemManagerResolver(String siemType) {
    SIEM_TYPE = siemType;

    MANAGERS = new HashMap<String, AbstractSiemManager>();
    MANAGERS.put("1", null);
    MANAGERS.put("2", null);
  }

  // Metodi ====================================================================
  /**
   *
   */
  public AbstractSiemManager get(final Long exportMethod) throws SiemException {
    if (NO_SIEM_SET.equals(SIEM_TYPE))
      throw new SiemException(NO_SIEM_SET_ERROR_MSG);

    switch(SIEM_TYPE) {
      case SIEM_MAGGIOLI:
        return new CloudLoggerSiemManager(exportMethod);
      case SIEM_REG_MARCHE:
        return new RegMarcheSiemManager();
      default:
        throw new SiemException(String.format(UNHANDLED_SIEM_ERROR_MSG, SIEM_TYPE));
    }
  }

}

