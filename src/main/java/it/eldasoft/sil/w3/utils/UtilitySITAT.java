package it.eldasoft.sil.w3.utils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class UtilitySITAT {

	  /**
	   * Ritorna il codice della categoria usato in SIMOG a partire dal codice usato da Sitat.
	   * 
	   * @param sqlManager
	   * @param categSimog
	   * @return Ritorna il codice della categoria usato in Sitat a partire dal codice usato da SIMOG.
	   * @throws SQLException
	   */
	  public static String getCategoriaSIMOG(final SqlManager sqlManager, final String categSitat) throws SQLException {
	    return (String) sqlManager.getObject("select CODAVCP from W9CODICI_AVCP where TABCOD='W3z03' and CODSITAT=?", 
	        new Object[] { categSitat } );
	  }
	  
	  /**
	   * Ritorna il codice della categoria usato in Sitat a partire dal codice usato da SIMOG.
	   * 
	   * @param sqlManager
	   * @param categSimog
	   * @return Ritorna il codice della categoria usato in Sitat a partire dal codice usato da SIMOG.
	   * @throws SQLException
	   */
	  public static String getCategoriaSITAT(final SqlManager sqlManager, final String categSimog) throws SQLException {
	    return (String) sqlManager.getObject("select CODSITAT from W9CODICI_AVCP where TABCOD='W3z03' and CODAVCP=?", 
	        new Object[] { categSimog } );
	  }
	  
	  /**
       * Prelevo la data di attuazione di una versione SIMOG,
       * dal tabellato W9023 in TAB1, in base al TAB1TIP
       * 
       * @return
       * @throws SQLException
       */
      public static Date getDataAttuazioneSimog(final SqlManager sqlManager,Long tab1tip) throws SQLException {
          String strDataAttuazioneSimog = (String) sqlManager.getObject(
            "select TAB1DESC from TAB1 where TAB1COD='W9023' and TAB1TIP=?", new Object[] {tab1tip});
        
          Date dataAttuazioneSimog = null;
          if (StringUtils.isNotEmpty(strDataAttuazioneSimog)) {
              dataAttuazioneSimog = UtilityDate.convertiData(strDataAttuazioneSimog.substring(0, 10), 
              UtilityDate.FORMATO_GG_MM_AAAA);
          }
          return dataAttuazioneSimog;
      }
      
      public static boolean isVerAttiva(final SqlManager sqlManager, Long tab1tip) {
              Date dataAttivazione=null;
              try {
                dataAttivazione = getDataAttuazioneSimog(sqlManager,tab1tip);
                if(dataAttivazione==null) {
                  return true;
                }
                return new Date().compareTo(dataAttivazione)>0;
              } catch (SQLException e) {
                e.printStackTrace();
                return false;
              }
      }
}
