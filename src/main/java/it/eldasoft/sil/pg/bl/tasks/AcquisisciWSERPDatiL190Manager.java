/*
 * Created on 13/06/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPLiquidatoResType;
import it.maggioli.eldasoft.ws.erp.WSERPLiquidatoType;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

public class AcquisisciWSERPDatiL190Manager {

  static Logger           logger = Logger.getLogger(AcquisisciWSERPDatiL190Manager.class);

  private SqlManager      sqlManager;

  private GestioneWSERPManager gestioneWSERPManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }

public void acquisisciDatiL190() throws GestoreException {

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato
    if (WebUtilities.isAppNotReady()) return;
    ServletContext context = SpringAppContext.getServletContext();

    if (logger.isDebugEnabled()){
      logger.debug("acquisisciDatiL190: inizio metodo");
    }

    //Si determina se è attiva l'integrazione con WSERP
    String urlWSERP = ConfigManager.getValore("wserp.erp.url");
    if(urlWSERP != null && !"".equals(urlWSERP)){
      WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
      if(configurazione.isEsito()){
        String tipoWSERP = configurazione.getRemotewserp();
        if (logger.isInfoEnabled()) {
          logger.info("Avvio della procedura di acquisizione dei dati di L190");
        }

        if("AVM".equals(tipoWSERP) || "UGOV".equals(tipoWSERP) || "UGOVPA".equals(tipoWSERP) || "SMEUP".equals(tipoWSERP) || "TPER".equals(tipoWSERP) || "ENEA".equals(tipoWSERP) || "ATAC".equals(tipoWSERP)){

          Calendar cal = Calendar.getInstance();
          Calendar newCal = Calendar.getInstance();

          int year = cal.get(Calendar.YEAR);
          year = year-2;

          newCal.set(Calendar.YEAR,year);
          newCal.set(Calendar.MONTH,0);
          newCal.set(Calendar.DAY_OF_MONTH,1);
          newCal.set(Calendar.HOUR_OF_DAY, 0);
          newCal.set(Calendar.MINUTE, 0);
          newCal.set(Calendar.SECOND, 0);

          Date filterDate = newCal.getTime();

          String selezioneCIG = "select ga.codcig, gc.ngara, gc.ncont, gc.impliq, gc.dverbc, gc.dcertu" +
          		" from gare ga, garecont gc" +
          		" where gc.codimp = ga.ditta" +
          		" and ((gc.ngara=ga.ngara and gc.ncont=1) or (gc.ngara=ga.codgar1 and (gc.ngaral is null or gc.ngaral=ga.ngara)))" +
          		" and ga.ditta is not null and ga.clavor is null and ga.codcig is not null and (gc.dcertu is null or gc.dcertu >  ?)" +
          		" order by ga.codcig";
          
          if("TPER".equals(tipoWSERP)) {
              selezioneCIG = "select ga.codcig, gc.ngara, gc.ncont, gc.impliq, gc.dverbc, gc.dcertu" +
            	" from gare ga, garecont gc, torn t" +
           		" where gc.codimp = ga.ditta"+
            	" and ga.codgar1=t.codgar and t.cenint = '000001'" +
            	" and ((gc.ngara=ga.ngara and gc.ncont=1) or (gc.ngara=ga.codgar1 and (gc.ngaral is null or gc.ngaral=ga.ngara)))" +
               	" and ga.ditta is not null and ga.clavor is null and ga.codcig is not null and (gc.dcertu is null or gc.dcertu >  ?)" +
               	" order by ga.codcig";
          }

          try {

            List<?> listaCIG = sqlManager.getListVector(selezioneCIG, new Object[] {filterDate});
            if(listaCIG.size() > 0){
              Long i_syscon = new Long(-1);
              String servizio ="WSERP_L190";
              String[] credenziali = gestioneWSERPManager.wserpGetLogin(i_syscon, servizio);
              String username = credenziali[0];
              String password = credenziali[1];
              servizio ="WSERP";

              boolean acqOK = true;

              if("ENEA".equals(tipoWSERP)){ //MULTICIG-ARRAY
                WSERPLiquidatoResType liqRes = gestioneWSERPManager.wserpDatiL190(username, password, servizio, null, null, null);
                if(liqRes.isEsito()){
                  WSERPLiquidatoType[] liqArray = liqRes.getLiquidatoArray();
                  if(liqArray != null){
                    for (int l = 0; l < liqArray.length; l++){
                      WSERPLiquidatoType liquidatoType = liqArray[l];
                      String codcig = liquidatoType.getCig();
                      codcig = UtilityStringhe.convertiNullInStringaVuota(codcig);
                      Calendar calUltimazione = liquidatoType.getDataUltimazione();
                      Date dataUltimazione = null;
                      if(calUltimazione != null){
                        dataUltimazione = calUltimazione.getTime();
                      }
                      Calendar calInizio = liquidatoType.getDataInizio();
                      Date dataInizio = null;
                      if(calInizio != null){
                        dataInizio = calInizio.getTime();
                      }

                      Double impLiquidato = liquidatoType.getImpLiquidato();
                      if(!"".equals(codcig) && impLiquidato != null && impLiquidato != new Double(0)){
                        for (int c = 0; c < listaCIG.size(); c++){
                          String c_cig = SqlManager.getValueFromVectorParam(listaCIG.get(c), 0).stringValue();
                          c_cig = UtilityStringhe.convertiNullInStringaVuota(c_cig);
                          if(!"".equals(c_cig) && codcig.equals(c_cig)){
                            String c_ngara = SqlManager.getValueFromVectorParam(listaCIG.get(c), 1).stringValue();
                            Long c_ncont = (Long) SqlManager.getValueFromVectorParam(listaCIG.get(c), 2).getValue();
                            Double c_impliq = SqlManager.getValueFromVectorParam(listaCIG.get(c), 3).doubleValue();
                            Date c_dverbc = (Date) SqlManager.getValueFromVectorParam(listaCIG.get(c), 4).getValue();
                            Date c_dcertu = (Date) SqlManager.getValueFromVectorParam(listaCIG.get(c), 5).getValue();
                            gestioneWSERPManager.updDatiL190Cig(tipoWSERP, codcig, impLiquidato, dataInizio, dataUltimazione, c_ngara, c_ncont, c_impliq, c_dverbc, c_dcertu);
                          }
                        }//for
                      }
                    }//for

                  }else{
                    if (logger.isInfoEnabled()) {
                      logger.info("Aggiornamento CIG: \r\n" +
                      " Non risulta possibile recuperare i dati da ENEA");
                    }
                  }
                }else{
                  acqOK = false;
                }
              }else{ //UNICIG-ARRAY
                for (int c = 0; c < listaCIG.size(); c++){
                  String c_cig = SqlManager.getValueFromVectorParam(listaCIG.get(c), 0).stringValue();
                  String c_ngara = SqlManager.getValueFromVectorParam(listaCIG.get(c), 1).stringValue();
                  Long c_ncont = (Long) SqlManager.getValueFromVectorParam(listaCIG.get(c), 2).getValue();
                  Double c_impliq = SqlManager.getValueFromVectorParam(listaCIG.get(c), 3).doubleValue();
                  Date c_dverbc = (Date) SqlManager.getValueFromVectorParam(listaCIG.get(c), 4).getValue();
                  Date c_dcertu = (Date) SqlManager.getValueFromVectorParam(listaCIG.get(c), 5).getValue();


                  String[] cigUniArray = new String[1];
                  cigUniArray[0] = c_cig;
                  WSERPLiquidatoResType liqRes = gestioneWSERPManager.wserpDatiL190(username, password, servizio, cigUniArray, null, null);
                  if(liqRes.isEsito()){
                    WSERPLiquidatoType[] liqArray = liqRes.getLiquidatoArray();
                    if(liqArray != null){
                      WSERPLiquidatoType liquidatoType = liqArray[0];
                      String codcig = liquidatoType.getCig();
                      codcig = UtilityStringhe.convertiNullInStringaVuota(codcig);
                      Calendar calUltimazione = liquidatoType.getDataUltimazione();
                      Date dataUltimazione = null;
                      if(calUltimazione != null){
                        dataUltimazione = calUltimazione.getTime();
                      }
                      Calendar calInizio = liquidatoType.getDataInizio();
                      Date dataInizio = null;
                      if(calInizio != null){
                        dataInizio = calInizio.getTime();
                      }
                      Double impLiquidato = liquidatoType.getImpLiquidato();
                      if("AVM".equals(tipoWSERP) || "UGOV".equals(tipoWSERP) || "UGOVPA".equals(tipoWSERP) || "SMEUP".equals(tipoWSERP) || "TPER".equals(tipoWSERP) || "ATAC".equals(tipoWSERP)){
                        if(!"".equals(codcig) && impLiquidato != null && impLiquidato != new Double(0)){
                          gestioneWSERPManager.updDatiL190Cig(tipoWSERP, codcig, impLiquidato, dataInizio, dataUltimazione, c_ngara, c_ncont, c_impliq, c_dverbc, c_dcertu);
                        }
                      }
                    }else{
                      if (logger.isInfoEnabled()) {
                        if("UGOV".equals(tipoWSERP) || "UGOVPA".equals(tipoWSERP) || "SMEUP".equals(tipoWSERP)){
                          logger.info("Aggiornamento CIG: " + c_cig +"\r\n" +
                          " Non risulta possibile recuperare i dati da U-GOV");
                        }else{
                          if("AVM".equals(tipoWSERP) || "TPER".equals(tipoWSERP)){
                            logger.info("Aggiornamento CIG: " + c_cig +"\r\n" +
                            " Non risulta possibile recuperare i dati da SAP");
                          }
                        }
                      }
                    }
                  }else{
                    acqOK = false;
                  }

                }//for

              }


              //inserisco il messaggio di procedura terminata
              if(acqOK == true){
                String msg = "E' stata effettuata correttamente la acquisizione dei dati della L.190 " ;
                gestioneWSERPManager.insMsgDatiL190(msg);
              }else{
                String msg = "La procedura di acquisizione dei dati della L.190 ha rilevato degli errori." +
                "\r\n" + "Per consultarne il dettaglio accedere ai log applicativi";
                gestioneWSERPManager.insMsgDatiL190(msg);
              }

            }//if lista cig popolata

          } catch (SQLException e) {
            throw new GestoreException("Errore nella lettura dei CIG da passare a WSERP per la lettura dei dati L.190 ", null, e);
          }

        }else{

          if("ANTEA".equals(tipoWSERP)){
            Long i_syscon = new Long(-1);
            String servizio ="WSERP_L190";
            String[] credenziali = gestioneWSERPManager.wserpGetLogin(i_syscon, servizio);
            String username = credenziali[0];
            String password = credenziali[1];
            WSERPLiquidatoResType liqRes = gestioneWSERPManager.wserpDatiL190(username, password, servizio, null, null, null);
            servizio ="WSERP";

            if(liqRes.isEsito()){
              WSERPLiquidatoType[] liqArray = liqRes.getLiquidatoArray();
              if(liqArray != null){
                for (int l = 0; l < liqArray.length; l++){
                  WSERPLiquidatoType liquidatoType = liqArray[l];
                  String codcig = liquidatoType.getCig();
                  codcig = UtilityStringhe.convertiNullInStringaVuota(codcig);
                  if(!"".equals(codcig)){
                    //QUI COMINCIO a TRATTARE i CIG
                    try {
                      String ngara = (String) sqlManager.getObject("select ngara from gare where codcig =  ?", new Object[] {codcig});
                      ngara = UtilityStringhe.convertiNullInStringaVuota(ngara);
                      if(!"".equals(ngara)){
                        //AGGIORNO

                        String selCIGSpecifica = "select ga.codcig, gc.ngara, gc.ncont, gc.impliq, gc.dverbc, gc.dcertu" +
                        " from gare ga, garecont gc" +
                        " where gc.codimp = ga.ditta" +
                        " and ((gc.ngara=ga.ngara and gc.ncont=1) or (gc.ngara=ga.codgar1 and (gc.ngaral is null or gc.ngaral=ga.ngara)))" +
                        " and ga.ditta is not null and ga.clavor is null and ga.codcig is not null " +
                        " and ga.codcig = ? ";
                        String c_ngara = null;
                        Long c_ncont = null;
                        Double c_impliq = null;
                        Date c_dverbc = null;
                        Date c_dcertu = null;
                        Vector<?> datiCIG = sqlManager.getVector(selCIGSpecifica, new Object[] {codcig});
                        if (datiCIG != null && datiCIG.size() > 0) {
                          // Ragione sociale, partita IVA, codice fiscale
                          c_ngara = (String) SqlManager.getValueFromVectorParam(datiCIG, 1).getValue();
                          c_ncont = (Long) SqlManager.getValueFromVectorParam(datiCIG, 2).getValue();
                          c_impliq= (Double) SqlManager.getValueFromVectorParam(datiCIG, 3).getValue();
                          c_dverbc = (Date) SqlManager.getValueFromVectorParam(datiCIG, 4).getValue();
                          c_dcertu = (Date) SqlManager.getValueFromVectorParam(datiCIG, 5).getValue();
                        }

                        Double impLiquidato = liquidatoType.getImpLiquidato();
                        Calendar calUltimazione = liquidatoType.getDataUltimazione();
                        Date dataUltimazione = null;
                        if(calUltimazione != null){
                          dataUltimazione = calUltimazione.getTime();
                        }
                        Calendar calInizio = liquidatoType.getDataInizio();
                        Date dataInizio = null;
                        if(calInizio != null){
                          dataInizio = calInizio.getTime();
                        }

                        gestioneWSERPManager.updDatiL190Cig(tipoWSERP, codcig, impLiquidato, dataInizio, dataUltimazione, c_ngara, c_ncont, c_impliq, c_dverbc, c_dcertu);
                      }else{
                        //INSERISCO - NON SERVE l'AGGIORNAMENTO ma il MSG

                        String uffint = null;
                        Long tipoAppalto = null;
                        Long tipgar = null;
                        gestioneWSERPManager.insAffidamento(codcig,liquidatoType,tipoAppalto, tipgar, uffint);

                      }

                    } catch (SQLException e) {

                      throw new GestoreException("Errore nell' aggiornamento dei dati L.190 ", null, e);

                    }

                  }




                }




              }
            }
          }
        }

        if (logger.isInfoEnabled()) {
          logger.info("Fine della procedura di acquisizione dei dati di L190");
        }

      }

    }else{
      return;
    }

    if (logger.isDebugEnabled()){
      logger.debug("acquisisciDatiL190: fine metodo");
    }

  }

}
