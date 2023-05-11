/*
 * Created on 27/01/2017
 *
 * Copyright (c) Maggioli S.p.A. - Divisione EldaSoft
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per il recupero dell'importo IVR per una ditta.
 *
 * @author A. Mancini
 */
public class GetIVRFunction extends AbstractFunzioneTag {

  public GetIVRFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String ditta = (String) params[1];
    Date dataOdierna = new Date(UtilityDate.getDataOdiernaAsDate().getTime());
    
    String selectIVR = "SELECT IVR "
        + "FROM IMPRVR "
        + "WHERE CODIMP = ? "
        + "AND ? BETWEEN IVR_DATA_INIZIO and IVR_DATA_FINE ";
    
    String sospenzioneDataInizio = sqlManager.getDBFunction("datetimetostring",  new String[] {"SOSPENSIONE_DATA_INIZIO"});
    String sospenzioneDataFine = sqlManager.getDBFunction("datetimetostring",  new String[] {"SOSPENSIONE_DATA_FINE"});
    String selectIsSospesa = "SELECT "
        + sqlManager.getDBFunction("concat",  new String[] {
            sqlManager.getDBFunction("concat",  new String[] {
                sospenzioneDataInizio, "' - '" 
                }),
            sospenzioneDataFine
            })
        + ", SOSPENSIONE_REVOCATA "
        + "FROM IMPRVR "
        + "WHERE CODIMP = ? "
        + "AND ? BETWEEN SOSPENSIONE_DATA_INIZIO and SOSPENSIONE_DATA_FINE ";
    
    Double ivr = null;
    Vector isSospesa = null;

    String dataSospensione = "";
    String isSospesaRevocata = "";
    
    String res = "NC";
    
    try {
      List<Vector> listaIVR = sqlManager.getListVector(selectIVR, new Object[]{ditta,dataOdierna});
      
      if(listaIVR.isEmpty()) {
        res = "";
      } else {
        ivr = (Double)SqlManager.getValueFromVectorParam(listaIVR.get(0), 0).getValue();
        if(ivr != null) {
          res = ivr.toString();
        }
      }
      isSospesa = sqlManager.getVector(selectIsSospesa, new Object[]{ditta,dataOdierna});
      
      if (isSospesa != null && !isSospesa.isEmpty()) {
        dataSospensione = (String) SqlManager.getValueFromVectorParam(isSospesa, 0).getValue();
        dataSospensione  = UtilityStringhe.convertiNullInStringaVuota(dataSospensione);
        if(!dataSospensione.isEmpty()) {
          String[] dateSplited = dataSospensione.split(" - ");
          String dataSospensioneInizio = dateSplited[0].split(" ")[0];
          String dataSospensioneFine = dateSplited[1].split(" ")[0];
          dataSospensione = dataSospensioneInizio.concat(" - ").concat(dataSospensioneFine);
        }
        isSospesaRevocata = (String) SqlManager.getValueFromVectorParam(isSospesa, 1).getValue();
        isSospesaRevocata  = UtilityStringhe.convertiNullInStringaVuota(isSospesaRevocata);
      }
    } catch (SQLException e) {
      throw new JspException("Errore nel recupero dell'importo IVR per la ditta " + ditta, e);
    }
    
    pageContext.setAttribute("ivr",res,PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("dataSospensione",dataSospensione,PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("isSospesaRevocata",isSospesaRevocata,PageContext.REQUEST_SCOPE);
    
    return null;
  }

}
