/*
 * Created on 21/ago/2020
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
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per inserire le
 * pubblicazioni predefinite per il bando e per l'esito
 *
 * @author Cristian Febas
 */
public class GestoreWSERPInsPosRda extends
    AbstractGestoreEntita {

  public GestoreWSERPInsPosRda() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

  private GestioneWSERPManager gestioneWSERPManager = null;

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);

    gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
        this.getServletContext(), GestioneWSERPManager.class);

  }

  @Override
  public String getEntita() {
    return "GARE";
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
    String codgar = datiForm.getString("CODGAR");
    String ngara = datiForm.getString("NGARA");

    String servizio = "WSERP";

    ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long syscon = new Long(profilo.getId());
    String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);
    String username = credenziali[0];
    String password = credenziali[1];

    //recupero i valori su APPARDA in lavo in base a clavor/numera
    List<?> datiGareLavo;
    try {
      datiGareLavo = this.sqlManager.getVector("select clavor, numera from gare" +
      		" where codgar1 = ? and ngara = ?", new Object[] { codgar,ngara });
      if (datiGareLavo != null && datiGareLavo.size() > 0) {
        String clavor = (String) SqlManager.getValueFromVectorParam(datiGareLavo, 0).getValue();
        Long numera = (Long) SqlManager.getValueFromVectorParam(datiGareLavo, 1).getValue();
        List<?> datiAPPARDA = this.sqlManager.getListVector("select codicerda, tiporda" +
            " from apparda where codlav = ? and nappal = ? and nproat = ?", new Object[] { clavor,numera,new Long(1) });
        if (datiAPPARDA != null && datiAPPARDA.size() > 0) {
          for (int i = 0; i < datiAPPARDA.size(); i++) {
            String codicerda= (String) SqlManager.getValueFromVectorParam(datiAPPARDA.get(i), 0).getValue();
            String tiporda= (String) SqlManager.getValueFromVectorParam(datiAPPARDA.get(i), 1).getValue();

            WSERPRdaType erpSearch= new WSERPRdaType();
            erpSearch.setCodiceRda(codicerda);
            erpSearch.setTipoRdaErp(tiporda);
            WSERPRdaResType wserpRdaRes = new WSERPRdaResType();
            wserpRdaRes = this.gestioneWSERPManager.wserpDettaglioRda(username, password, servizio, erpSearch);
            wserpRdaRes.setEsito(true);

            if(!wserpRdaRes.isEsito()){
              throw new GestoreException("Si e' verificato un errore durante la associazione delle RdA: " + wserpRdaRes.getMessaggio(),
                  "wserp.erp.dettagliorda.remote.error", null);
            }else{
              WSERPRdaType[] rdaArray = wserpRdaRes.getRdaArray();

              this.gestioneWSERPManager.insCollegamentoRda(this.getRequest(), username, password, servizio, codgar, ngara, rdaArray, "2", null);
            }

          }
        }
      }

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'operazione di caricamento delle lavorazioni da ERP",
          "insPosRdaGara", null);
    }
    // setta l'operazione a completata, in modo da scatenare il reload della
    // pagina principale
    this.getRequest().setAttribute("lavErpIns", "1");
  }



  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
