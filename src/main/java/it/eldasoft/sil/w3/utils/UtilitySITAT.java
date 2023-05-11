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
	   * Determina se il codice CIG passato come argomento e' formalmente corretto, cioe' se
	   * - ha lunghezza di 10 caratteri
	   * - i primi 7 caratteri sono numerici
	   * - gli ultimi tre caratteri sono il resto in esadecimale di questa operazione:
	   *   (primi 7 caratteri del CIG) * 211 /4091 
	   *   eventualmente completato a tre caratteri con degli zeri.
	   * 
	   * @param codiceCIG codice CIG
	   * @return Ritorna true se il codice CIG passato come argomento e' formalmente corretto.
	   */
	  public static boolean isCigValido(final String codiceCIG) {
	    boolean result = false;
	    
	    if (StringUtils.isNotEmpty(codiceCIG)) {
	      
	      String codCig = new String(codiceCIG.trim());
	      if (codCig.length() == 10) {
	        if (!"0000000000".equals(codCig)) {
	          String primi7CaratteriCig = StringUtils.substring(codCig.trim(), 0, 7);
	          String ultimi3CaratteriCig = StringUtils.substring(codCig.trim(), 7);
	          
	          if (StringUtils.isNumeric(primi7CaratteriCig)
	              && StringUtils.isAlphanumeric(ultimi3CaratteriCig)) {
	            while (primi7CaratteriCig.indexOf('0') == 0) {
	              primi7CaratteriCig = primi7CaratteriCig.substring(1);
	            }
	            Long numeroCIG =  new Long(primi7CaratteriCig);
	            long oper1 = numeroCIG.longValue();
	            long resto = (oper1 * 211) % 4091;
	            
	            String strResto = StringUtils.leftPad(Long.toHexString(resto), 3, '0').toUpperCase();
	            
	            if (ultimi3CaratteriCig.equalsIgnoreCase(strResto)) {
	              result = true;
	            }
	          }
	        }
	      }
	    }
	    return result;
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
