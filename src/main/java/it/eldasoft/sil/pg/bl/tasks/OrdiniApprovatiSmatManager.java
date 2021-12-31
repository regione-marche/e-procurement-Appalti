/*
 * Created on 10/09/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.SmatManager;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

public class OrdiniApprovatiSmatManager {

  static Logger           logger = Logger.getLogger(OrdiniApprovatiSmatManager.class);

  private SqlManager      sqlManager;

  private TabellatiManager      tabellatiManager;

  private SmatManager smatManager;


  /**
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
  *
  * @param tabellatiManager
  */
 public void setTabellatiManager(TabellatiManager tabellatiManager) {
   this.tabellatiManager = tabellatiManager;
 }

  /**
 *
 * @param smatManager
 */
  public void setSmatManager(SmatManager smatManager) {
    this.smatManager = smatManager;
  }


  /**
   *Gestione degli ordini approvati in SMAT
   *
   */
  public void acquisisciOrdiniApprovati() throws GestoreException{

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato ed inoltre deve esistere l'integrazione con il Portale Alice (OP114)
    if (WebUtilities.isAppNotReady()) return;
    ServletContext context = SpringAppContext.getServletContext();
    if(!GeneManager.checkOP(context, "OP114")) return;

       this.acquisisciAnagrafiche();

    if (logger.isDebugEnabled())
      logger.debug("acquisisciOrdiniApprovati: inizio metodo");

    Long idOrdine= null;
    Double importoOrdinato = null; //verificare che sia effettivamente un double
    String codiceFiscale = null;
    String partitaIVA = null;
    String ragioneSociale = null;
    String descrizioneTestata = null;
    String cig = null;
    Date dataApprovazione = null;
    Long tipologiaAffidamento = null;
    Long idEvento= null;


    String select="select id_ordine, ordine, importo_ordinato, cfis, piva, rag_soc, descrizione_testata," +
    		" cig, data_approvazione, tipologia_affidamento, annullato, lotto_pubblicabile, event_id " +
    		"  from v_smat_ordini_pubesiti" +
    		"  where processed_flag in ('N','M') order by id_ordine,event_id ";

    //String flag_elaborazione = "N";
    String flag_annullamento = "N";
    String flagPubblicabile = "N";

    List listaOrdiniDaElaborare = null;
    try {
      listaOrdiniDaElaborare = sqlManager.getListVector(select,
            new Object[] { });

    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della view V_SMAT_ORDINI_PUBESITI ", null, e);
    }
    if (listaOrdiniDaElaborare != null && listaOrdiniDaElaborare.size() > 0) {

      for (int i = 0; i < listaOrdiniDaElaborare.size(); i++) {
        idOrdine = SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 0).longValue();
        importoOrdinato = SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 2).doubleValue();
        codiceFiscale = SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 3).getStringValue();
        partitaIVA = SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 4).getStringValue();
        ragioneSociale = SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 5).getStringValue();
        descrizioneTestata = SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 6).getStringValue();
        cig = SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 7).getStringValue();
        dataApprovazione = (Date) SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 8).getValue();
        tipologiaAffidamento = (Long) SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 9).getValue();
        flag_annullamento = SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 10).getStringValue();
        flag_annullamento = UtilityStringhe.convertiNullInStringaVuota(flag_annullamento);
        flagPubblicabile = SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 11).getStringValue();
        flagPubblicabile = UtilityStringhe.convertiNullInStringaVuota(flagPubblicabile);
        idEvento = SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 12).longValue();
        HashMap datiOrdine = new HashMap();
        datiOrdine.put("idOrdine", idOrdine);
        datiOrdine.put("importoOrdinato", importoOrdinato);
        datiOrdine.put("codiceFiscale", codiceFiscale);
        datiOrdine.put("partitaIVA", partitaIVA);
        datiOrdine.put("ragioneSociale", ragioneSociale);
        datiOrdine.put("descrizioneTestata", descrizioneTestata);
        datiOrdine.put("cig", cig);
        datiOrdine.put("dataApprovazione", dataApprovazione);
        datiOrdine.put("tipologiaAffidamento",tipologiaAffidamento);
        datiOrdine.put("flagPubblicabile",flagPubblicabile);
        datiOrdine.put("idEvento",idEvento);


        try {
            if ("".equals(flag_annullamento)) {
              flag_annullamento = "N";
            }
            int res = smatManager.updDaOrdineApprovato(datiOrdine, flag_annullamento, "", "", "Y");
            if(res<0){
              logger.error("Errore durante la elaborazione degli ordini approvati! ");
            }else{
              if(res != 1){
                res = smatManager.updOrdineElaborato(idOrdine, idEvento, res);
                if (res < 0) {
                  logger.error("Errore durante la elaborazione degli ordini approvati! ");
                }
              }
            }
        } catch (GestoreException e) {
          logger.error("Errore durante la elaborazione degli ordini approvati! " + ", "  + e.getMessage(),e );
        }
      }
    }

    if (logger.isDebugEnabled())
      logger.debug("acquisisciOrdiniApprovati: fine metodo");

  }

  /**
   *Gestione delle anagrafiche in SMAT
   *
   */
  private void acquisisciAnagrafiche() throws GestoreException{

    if (logger.isDebugEnabled())
      logger.debug("acquisisciAnagrafiche: inizio metodo");

    Long idSupplierInterface = null;
    String codimp = null;
    String nomest = null;
    String cfimp = null;
    String pivimp = null;
    String indimp = null;
    String nciimp = null;
    String capimp = null;
    String locimp = null;
    String proimp = null;
    String telimp = null;
    String faximp = null;
    String emaiip = null;
    String emai2ip = null;

    String ebs_supplier_num = null;
    String ebs_processed_status = null;
    Date ebs_processed_date = null;
    String ebs_msg_error = null;

    String alice_processed_status = null;
    Date alice_processed_date = null;
    String alice_msg_error = null;

    String selectSupplierInterface = "select supplier_interface_id, codimp, nomest, cfimp, pivimp, indimp, nciimp, capimp," +
            " locimp, proimp, telimp, faximp, emaiip, emai2ip, " +
            " ebs_supplier_num, ebs_processed_status, ebs_processed_date, ebs_msg_error, " +
            " alice_processed_status, alice_processed_date, alice_msg_error " +
            "  from SMAT_AP210_SUPPLIERS_INTERFACE " +
            "  where alice_processed_status = 'NEW' and ebs_processed_status = 'PROCESSED_INSERT'" +
            "  order by supplier_interface_id ";

    //String flag_elaborazione = "N";

    List listaAnagraficheDaElaborare = null;
    try {
      listaAnagraficheDaElaborare = sqlManager.getListVector(selectSupplierInterface, new Object[] { });

    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della tabella SMAT_AP210_SUPPLIERS_INTERFACE ", null, e);
    }
    if (listaAnagraficheDaElaborare != null && listaAnagraficheDaElaborare.size() > 0) {

      for (int i = 0; i < listaAnagraficheDaElaborare.size(); i++) {
        idSupplierInterface = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 0).longValue();
        codimp = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 1).getStringValue();
        nomest = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 2).getStringValue();
        cfimp = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 3).getStringValue();
        pivimp = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 4).getStringValue();
        indimp = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 5).getStringValue();
        nciimp = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 6).getStringValue();
        capimp = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 7).getStringValue();
        locimp = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 8).getStringValue();
        proimp = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 9).getStringValue();
        telimp = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 10).getStringValue();
        faximp = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 11).getStringValue();
        emaiip = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 12).getStringValue();
        emai2ip = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 13).getStringValue();
        ebs_supplier_num = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 14).getStringValue();
        ebs_processed_status = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 15).getStringValue();
        ebs_processed_date = (Date) SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 16).getValue();
        ebs_msg_error = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 17).getStringValue();

        alice_processed_status = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 18).getStringValue();
        alice_processed_date = (Date) SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 19).getValue();
        alice_msg_error = SqlManager.getValueFromVectorParam(listaAnagraficheDaElaborare.get(i), 20).getStringValue();

        HashMap datiAnagrafica = new HashMap();
        datiAnagrafica.put("idSupplierInterface", idSupplierInterface);
        datiAnagrafica.put("codimp", codimp);
        datiAnagrafica.put("ebs_supplier_num", ebs_supplier_num);
        try {
            int res = smatManager.updAnagraficaElaborata(datiAnagrafica, idSupplierInterface);
            if(res<0){
              logger.error("Errore durante la elaborazione delle anagrafiche! ");
            }else{
              ;
            }
        } catch (GestoreException e) {
          logger.error("Errore durante la elaborazione delle anagrafiche! " + ", "  + e.getMessage(),e );
        }
      }
    }

    if (logger.isDebugEnabled())
      logger.debug("acquisisciAnagrafiche: fine metodo");

  }

}


