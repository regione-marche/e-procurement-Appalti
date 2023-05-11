/*
 * Created on 13/10/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire la l'inserimento
 * del documeto MDGUE
 *
 * @author Marcello Caminiti
 */
public class GestorePopupCreaDocumentoMDGUE extends
    AbstractGestoreEntita {

  public GestorePopupCreaDocumentoMDGUE() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

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
    String ngara = datiForm.getString("NGARA");
    String codgar = datiForm.getString("CODGAR");
    String gruppoString = datiForm.getString("GRUPPO");
    Long gruppo = new Long(gruppoString);
    String gartel = datiForm.getString("GARTEL");
    String tipologiaString = datiForm.getString("TIPOLOGIA");
    Long tipologia = null;
    if(tipologiaString != null && !"".equals(tipologiaString))
      tipologia = new Long(tipologiaString);
    String allmail=null;
    String inizializzaAllmail = datiForm.getString("inizializzaAllmail");



    String insert="insert into documgara(codgar, ngara, norddocg,gruppo, numord, descrizione, idstampa, allmail, valenza, tipologia, idprg) values(?,?,?,?,?,?,?,?,?,?,?)";

    try {

      if("6".equals(gruppoString)) {
        Long count = (Long) sqlManager.getObject("select count(*) from pubbli where (tippub = 13 or tippub = 23) and codgar9 = ?", new Object[]{codgar});
        if( "true".equals(gartel) && (  count == null || new Long(0).equals(count)) && !"true".equals(inizializzaAllmail))
          allmail="1";
        else
          allmail="2";
      }

      Object params[]= new Object[11];
      params[0]=  codgar;
      params[1]=  ngara;

      Long norddocg = (Long)this.sqlManager.getObject("select max(norddocg) from documgara where codgar=?", new Object[] {codgar});
      if(norddocg == null)
        norddocg = new Long(0);
      norddocg = new Long(norddocg.longValue() + 1);
      params[2] =  norddocg;
      params[3] = gruppo;
      params[4] = null;
      params[5] = "Documento di gara unico europeo (DGUE)";
      params[6] = "DGUE";
      params[7] = allmail;
      params[8] = new Long(0);
      params[9] = tipologia;
      params[10] = "PG";

      this.sqlManager.update(insert, params);
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'inserimento del documento DGUE ", null,  e);
    }

    //Ricalcolo NUMORD.DOCUMGARA
    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        this.getServletContext(), PgManagerEst1.class);

    pgManagerEst1.ricalcNumordDocGara(codgar, gruppo);

    // setta l'operazione a completata, in modo da scatenare il reload della
    // pagina principale
    this.getRequest().setAttribute("inserimentoEseguito", "1");
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
