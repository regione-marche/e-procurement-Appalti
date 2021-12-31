/*
 * Created on 29-08-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per la pagina di dettaglio dell'adempimento (ANTICOR).
 * Si adopera un gestore non standard poichè sulle tabelle figli di ANTICOR sono
 * impostati dei vincoli di integrità referenziale per cui non si possono
 * inserire le figlie se non è stato inserita l'occorrenza in ANTICOR
 *
 * @author Marcello Caminiti
 */
public class GestoreANTICOR extends AbstractGestoreChiaveIDAutoincrementante {



  @Override
  public String getCampoNumericoChiave() {
    return "ID";
  }

  @Override
  public String getEntita() {
    return "ANTICOR";
  }



  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    Long annorif = null;

    super.preInsert(status, datiForm);

    String ufficioIntestatario=null;
    HttpSession session = this.getRequest().getSession();
    if ( session != null) {
      ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
    }


      annorif = datiForm.getLong("ANTICOR.ANNORIF");
      try {
        // Si deve controllare l'unicità del valore di ANNORIF
        if(ufficioIntestatario==null ||"".equals(ufficioIntestatario))
          ufficioIntestatario="*";

        String select = "select count(id) from anticor where annorif=? and codein=?";
        Long count = (Long) this.sqlManager.getObject(select,
            new Object[] { annorif, ufficioIntestatario});

        if (count != null && count.longValue() > 0)
          throw new GestoreException(
              "Esiste già una pubblicazione adempimento per l'anno di riferimento inserito nella scheda",
              "annorif.dupl", new Exception());

        /*
        // Se se in urlsito la url non termina con / la inserisco
        String urlsito = datiForm.getString("ANTICOR.URLSITO");
        if (!urlsito.endsWith("/")) {
          urlsito += "/";
          datiForm.setValue("ANTICOR.URLSITO", urlsito);
        }
        */

        //Gestione ufficio intestatario
        datiForm.setValue("ANTICOR.CODEIN", ufficioIntestatario);

      } catch (SQLException e) {
        throw new GestoreException("Errore nell'inserimento in ANTICOR", null,
            e);
      }

      if(GeneManager.checkOP(this.getServletContext(), CostantiGenerali.OPZIONE_GESTIONE_PORTALE)){
        String urlPortaleAlice = ConfigManager.getValore("it.eldasoft.sil.pg.avcp.urlPortaleAlice");
        if(urlPortaleAlice==null || "".equals(urlPortaleAlice)){
          throw new GestoreException(
              "Non è valorizzato nel file di configurazione la url del portale Appalti. Contattare l'amministratore",
              "urlPortale.nulla", new Exception());
        }
        if(!urlPortaleAlice.endsWith("/"))
            urlPortaleAlice+="/";

        String urlsito = urlPortaleAlice;

        if(ufficioIntestatario!=null && !"*".equals(ufficioIntestatario)){

          try {
            String codfisUffInt = (String)this.sqlManager.getObject("select cfein from uffint where codein=?", new Object[]{ufficioIntestatario});
            if(codfisUffInt==null || "".equals(codfisUffInt)){
              throw new GestoreException(
                "Codice fiscale Ufficio Intestatario " + ufficioIntestatario + " non valorizzato" , "UfficioInt.cfNull", new Exception());
            }
            urlsito += codfisUffInt + "/";
          } catch (SQLException e) {
            this.getRequest().setAttribute("erroreOperazione", "1");
            throw new GestoreException(
                "Errore durante la lettura del codice fiscale dell'ufficio intestatario ", "cfAnticorlotti", e);
          }
        }
        urlsito += annorif.toString();
        datiForm.setValue("ANTICOR.URLSITO", urlsito);
      }

  }

  @Override
  public void afterInsertEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);

    Long idAnticor = datiForm.getLong("ANTICOR.ID");
    Long annorif = datiForm.getLong("ANTICOR.ANNORIF");
    String lottoInBo = "1";
    boolean msgWarningAnnoPrec = false;

    String ufficioIntestatario=null;
    HttpSession session = this.getRequest().getSession();
    if ( session != null) {
      ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
      if (ufficioIntestatario == null)
        ufficioIntestatario="*";
    }

    try{
      HashMap<String, Object> ret= pgManager.insertLottiAdempimento(annorif, null, idAnticor, null, null, ufficioIntestatario, lottoInBo,null,true,null);

      String msg = "";
      try {
        //Nel caso di gestione uffici intestatari abilitata, si deve filtrare per il codice
        //dell'ufficio intestatario
        String select="select upper(cig) from v_dati_lotti where cig is not null group by upper(cig) having(count(*)>1)";
        if (!"*".equals(ufficioIntestatario))
          select="select upper(cig) from v_dati_lotti where codiceprop='" + ufficioIntestatario + "' and cig is not null group by upper(cig) having(count(*)>1)";

        List<?> listaCigDuplicati = this.sqlManager.getListVector(select, null);

        if (listaCigDuplicati != null && listaCigDuplicati.size() > 0) {
          for (int i = 0; i < listaCigDuplicati.size(); i++) {
            String cig = SqlManager.getValueFromVectorParam(
                listaCigDuplicati.get(i), 0).getStringValue();
            if (i > 0) msg += ", ";
            msg += cig;
          }
        }
      } catch (SQLException e) {
        this.getRequest().setAttribute("erroreOperazione", "1");
        throw new GestoreException(
            "Errore nella valutazione dell'esistenza CIG duplicati nel DB ",
            null, e);
      }


      if (ret != null) {
        String codiciCigDuplicati = (String)ret.get("cigDuplicati");
        if (codiciCigDuplicati != null && !"".equals(codiciCigDuplicati) && !"".equals(msg)) {
          UtilityStruts.addMessage(this.getRequest(), "warning",
              "warnings.adempimenti.CIG.duplicazioneDB_Lotti",
              new Object[] { msg,codiciCigDuplicati });
        } else if (!"".equals(msg) && (codiciCigDuplicati==null || "".equals(codiciCigDuplicati))) {
          UtilityStruts.addMessage(this.getRequest(), "warning",
              "warnings.adempimenti.CIG.duplicazioneDB",
              new Object[] { msg });
        }
      } else if(!"".equals(msg)) {
        UtilityStruts.addMessage(this.getRequest(), "warning",
            "warnings.adempimenti.CIG.duplicazioneDB",
            new Object[] { msg });
      }
    } catch(GestoreException e ) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw e;
    }

    //Import lotti adempimento lotti anno precedente
    try {
      msgWarningAnnoPrec = pgManager.insertLottiAdempimentoAnnoPrecedente(annorif, ufficioIntestatario, idAnticor);
      /*
      HashMap ret= pgManager.insertLottiAdempimentoAnnoPrecedente(annorif, ufficioIntestatario, idAnticor,false);
      if(ret!=null){
        msgWarningAnnoPrec = ((Boolean)ret.get("msgWarningAnnoPrec")).booleanValue();
      }
      */
    }  catch(GestoreException e ){
        this.getRequest().setAttribute("erroreOperazione", "1");
        throw e;
    }


    if (msgWarningAnnoPrec)
      UtilityStruts.addMessage(this.getRequest(), "warning",
          "warnings.adempimenti.import", null);
    this.getRequest().setAttribute("operazioneEseguita", "1");
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {


  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

}
