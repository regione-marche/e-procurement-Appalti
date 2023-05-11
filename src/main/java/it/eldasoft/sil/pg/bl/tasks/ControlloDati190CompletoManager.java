/*
 * Created on 16/apr/2018
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.bl.tasks;

import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.sil.pg.bl.ControlloDati190Manager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.utility.UtilityDate;


public class ControlloDati190CompletoManager {
  
  static Logger      logger = Logger.getLogger(ControlloDati190Manager.class);
  
  private SqlManager sqlManager;
  private GenChiaviManager genChiaviManager;
  private PgManager pgManager;
  private ControlloDati190Manager controlloDati190Manager;
  
  /**
   * Set SqlManager 
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
      this.sqlManager = sqlManager;
  }
  
  /**
   * Set SqlManager 
   *
   * @param sqlManager
   */
  public void setControlloDati190Manager(ControlloDati190Manager controlloDati190Manager) {
      this.controlloDati190Manager = controlloDati190Manager;
  }
  
  public void controllaDati190Completo() throws SQLException{
    
    if (logger.isDebugEnabled())
        logger.debug("controllaDati190Completo: inizio metodo");
    
    String querySelectConFiltro = "select distinct gare.codgar1 from gare, torn, garecont where " +
    		"((garecont.codimp=gare.ditta or garecont.codimp is null) and " +
    		"((garecont.ngara=gare.ngara and garecont.ncont=1) or " +
    		"(garecont.ngara=gare.codgar1 and (garecont.ngaral is null or garecont.ngaral=gare.ngara))))and " +
    		"gare.codgar1 = torn.codgar and (genere is null or genere not in (4,10,11,20)) and " +
    		"((torn.dpubav >= ?) or " +
    		"(torn.dpubav is null and torn.dinvit >= ?) or " +
    		"(torn.dpubav is null and torn.dinvit is null and gare.dattoa >= ?) or" +
    		"(torn.dpubav is null and torn.dinvit is null and gare.dattoa is null)) and " +
    		"(gare.datneg is null or gare.datneg >= ? )  and " +
    		"(garecont.dcertu is null or (gare.dattoa is not null and garecont.dcertu>= ?))";
    
    java.util.Date dataInizio = null;
    dataInizio = UtilityDate.convertiData("01/12/2012",
        UtilityDate.FORMATO_GG_MM_AAAA);
    int year = Calendar.getInstance().get(Calendar.YEAR);
    java.util.Date utilToday = (java.util.Date) new GregorianCalendar(year - 1,0,1).getTime();
    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    String StringaAnnoScorso = df.format(utilToday);
    java.util.Date annoScorso = UtilityDate.convertiData(StringaAnnoScorso,
        UtilityDate.FORMATO_GG_MM_AAAA);
    
    List<?> gareDaControllare = this.sqlManager.getListVector(querySelectConFiltro, new Object[] {dataInizio,dataInizio,dataInizio,annoScorso,annoScorso});
    ArrayList<String> codgarDaControllare = new ArrayList<String>();
    
    HashMap<String,Object>  response = new HashMap<String,Object>();
    for(int i = 0; i < gareDaControllare.size(); i++ ){
      codgarDaControllare.add((String) SqlManager.getValueFromVectorParam(gareDaControllare.get(i), 0).getValue());
    }
    for(int n = 0; n < codgarDaControllare.size(); n++ ){
      response = controlloDati190Manager.controllaDati(codgarDaControllare.get(n));
    }
    if (logger.isDebugEnabled())
      logger.debug("controllaDati190Completo: fine metodo");
  }
  
  
}
