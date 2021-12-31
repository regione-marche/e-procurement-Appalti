/*
 * Created on 09/03/15
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
import it.eldasoft.gene.web.struts.SetProfiloAction;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ScpManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire la storicizzazione
 * della rettifica
 *
 * @author Marcello Caminiti
 */
public class GestorePopupModificaPuntoConsegna extends
    AbstractGestoreEntita {

  public GestorePopupModificaPuntoConsegna() {
    super(false);
  }

  /** Logger Log4J di classe */
  static Logger          logger = Logger.getLogger(SetProfiloAction.class);

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

  private ScpManager scpManager;

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
  }

  @Override
  public String getEntita() {
    return "TORN";
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

    // lettura dei parametri di input
    String idOrdine = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
      "idOrdine"));

    String codCons = datiForm.getString("COD_PUNTO_CONS");
    codCons = UtilityStringhe.convertiNullInStringaVuota(codCons);
    String indirizzo = datiForm.getString("INDIRIZZO");
    String localita = datiForm.getString("LOCALITA");
    String cap = datiForm.getString("CAP");
    String citta = datiForm.getString("CITTA");
    String codNazStr = datiForm.getString("CODNAZ");
    codNazStr = UtilityStringhe.convertiNullInStringaVuota(codNazStr);
    Long codNaz= null;
    if(!"".equals(codNazStr)){
      codNaz = new Long(codNazStr);
    }
    String consDom = datiForm.getString("CONS_DOMICILIO");
    String altreIndic = datiForm.getString("ALTRE_INDIC");
    altreIndic = UtilityStringhe.convertiNullInStringaVuota(altreIndic);
    try {

      //controlli

      String msgObbl = "";
      if("2".equals(consDom) && "".equals(codCons)){
        msgObbl = msgObbl + " - Codice Punto di consegna";
      }
      if(!"".equals(codCons) && "".equals(codNaz)){
        msgObbl = msgObbl + " - Nazione";
      }
      if("1".equals(consDom) && "".equals(altreIndic)){
        msgObbl = msgObbl + " - Altre indicazioni";
      }

      if(!"".equals(msgObbl)){
        this.getRequest().setAttribute("modificaEseguita", "0");
        throw new GestoreException("Valorizzare i dati obbligatori per le scelte operate", "nso.noDatiObbl.error", new Object[]{msgObbl}, new Exception());
      }


      this.sqlManager.update("update NSO_PUNTICONS set" +
      		" COD_PUNTO_CONS = ?,INDIRIZZO = ?,LOCALITA= ? ,CAP = ?,CITTA = ? ,CODNAZ = ?,ALTRE_INDIC = ?, CONS_DOMICILIO= ?" +
      		" where NSO_ORDINI_ID = ? ", new Object[] {codCons,indirizzo,localita,cap,citta,codNaz,altreIndic,consDom,idOrdine});
    } catch (SQLException e) {
      throw new GestoreException("L'aggiornamento ... non e' andato a buon fine", "modificaPuntoConsegna.esitoNOK", new Object[]{""}, new Exception());
    }
    // setta l'operazione a completata, in modo da scatenare il reload della
    // pagina principale
    this.getRequest().setAttribute("modificaEseguita", "1");
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }


}
