/*
 * Created on 19/12/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPLiquidatoResType;
import it.maggioli.eldasoft.ws.erp.WSERPLiquidatoType;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per l'import degli affidamenti da dati esterni
 *
 * @author Cristian.Febas
 */
public class GestorePopupImportaDatiL190 extends AbstractGestoreEntita {

  static Logger           logger = Logger.getLogger(GestorePopupImportaDatiL190.class);




  private GestioneWSERPManager gestioneWSERPManager;

  @Override
  public String getEntita() {
    return "TORN";
  }

  public GestorePopupImportaDatiL190() {
    super(false);
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    long numeroCigImportati=0;
    long numeroCigAggiornati=0;
    long numeroCigConErrori=0;

    Timestamp datpub = datiForm.getData("DATPUB");
    Date dataRiferimento = new Date(datpub.getTime());
    Calendar dataRif = Calendar.getInstance();
    dataRif.setTimeInMillis(datpub.getTime());


    String ufficioIntestatario=null;
    HttpSession session = this.getRequest().getSession();
    if ( session != null) {
      ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
    }



    gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
        this.getServletContext(), GestioneWSERPManager.class);



    //Si determina se è attiva l'integrazione con WSERP
    String urlWSERP = ConfigManager.getValore("wserp.erp.url");
    if(urlWSERP != null && !"".equals(urlWSERP)){
      WSERPConfigurazioneOutType configurazione = this.gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
      if(configurazione.isEsito()){
        String tipoWSERP = configurazione.getRemotewserp();
        if (logger.isInfoEnabled()) {
          logger.info("Avvio della procedura di acquisizione dei dati di L190");
        }

          if("ANTHEA".equals(tipoWSERP)){
            Long i_syscon = new Long(-1);
            String servizio ="WSERP";
            String[] credenziali = gestioneWSERPManager.wserpGetLogin(i_syscon, servizio);
            String username = credenziali[0];
            String password = credenziali[1];
            WSERPLiquidatoResType liqRes = gestioneWSERPManager.wserpDatiL190(username, password, servizio, null, dataRif, null);

            if(liqRes.isEsito()){
              WSERPLiquidatoType[] liqArray = liqRes.getLiquidatoArray();
              if(liqArray != null){
                int countCigIns = 0;
                int countCigAgg = 0;
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
                          countCigAgg = countCigAgg+1;
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
                        countCigIns = countCigIns+1;
                        String uffint = null;
                        Long tipoAppalto = null;
                        Long tipgar = null;
                        gestioneWSERPManager.insAffidamento(codcig,liquidatoType,tipoAppalto, tipgar, ufficioIntestatario);

                      }

                    } catch (SQLException e) {
                      this.getRequest().setAttribute("esito", "ERRORE");
                      throw new GestoreException("Errore nell' aggiornamento dei dati L.190 ", null, e);

                    }

                  }

                }
                //CASO BUONO
                this.getRequest().setAttribute("esito", "OK");
                this.getRequest().setAttribute("msg", "Importazione completata:");
                this.getRequest().setAttribute("numeroCigImportati", new Long(countCigIns));
                this.getRequest().setAttribute("numeroCigAggiornati", new Long(countCigAgg));

              }else{

                this.getRequest().setAttribute("esito", "OK");
                this.getRequest().setAttribute("msg", "Non ci sono cig da importare.");

              }

            }else{
              this.getRequest().setAttribute("esito", "ERRORE");
              this.getRequest().setAttribute("msg", "Importazione non completata a causa di errori.");
            }

          }//if ANTHEA

          if (logger.isInfoEnabled()) {
            logger.info("Fine della procedura di acquisizione dei dati di L190");
          }



      }

    }




    this.getRequest().setAttribute("numeroCigConErrori", new Long(numeroCigConErrori));
    //this.getRequest().setAttribute("messaggiErrore", messaggiErrore.toString());
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

  }

}
