/*
 * Created on 07/nov/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import it.eldasoft.sil.pg.db.dao.ScadenzeDao;


/**
 * Manager che si occupa di gestire tutte le operazioni di business logic sulla
 * parte delle scadenze delle gare d'appalto
 * 
 * @author Stefano.Sabbadin
 */
public class ScadenzeManager {
  
  
  /** DAO per l'accesso alle scadenze */
  private ScadenzeDao scadenzeDao;
  
  /**
   * @param scadenzeDao scadenzeDao da settare internamente alla classe.
   */
  public void setScadenzeDao(ScadenzeDao scadenzeDao) {
    this.scadenzeDao = scadenzeDao;
  }

  /**
   * Calcola la scadenza per diverse tipologie di date, a seconda dei parametri in input 
   * @param tipoCalcolo tipologia del calcolo in funzione della data:
   * <ul>
   * <li>1 = da DTEOFF.TORN nella pagina 'Dati generali' della gara</li>
   * <li>2 = da DTEPAR.TORN nella pagina 'Dati generali' della gara</li>
   * <li>3 = da DTEOFF.TORN dalla fase 'Inviti' della pagina 'Ricezione domande e offerte'</li>
   * <li>4 = per il calcolo della data termine risposta chiarimenti</li>
   * </ul>
   * @param tipoAppalto
   * @param importoGara
   * @param tipoProcedura
   * @param proceduraUrgente
   * @param termineRidotto
   * @param bandoWeb
   * @param docWeb
   * @param oggettoContratto
   * @return numero di giorni prima della scadenza
   */
  public Integer getGiorniScadenza(Integer tipoCalcolo, Integer tipoAppalto, Double importoGara, Integer tipoProcedura, String proceduraUrgente, String termineRidotto, String bandoWeb, String docWeb, Integer oggettoContratto) {
    Integer scadenza = null;
    
    // il calcolo va effettuato solo se i dati di 1° livello della CATSCA sono valorizzati
    if (tipoAppalto != null && importoGara != null && tipoProcedura != null) {
      // si impostano dei valori di default per i dati di 2° livello della CATSCA
      if (proceduraUrgente == null) proceduraUrgente = "2";
      if (termineRidotto == null) termineRidotto = "2";
      if (bandoWeb == null) bandoWeb = "2";
      if (docWeb == null) docWeb = "2";
      if (oggettoContratto == null) oggettoContratto = new Integer(1);
      if(oggettoContratto.longValue()==4)
        oggettoContratto = new Integer(1);
      // si estrae il numero di giorni della scadenza
      scadenza = this.scadenzeDao.getGiorniScadenza(tipoCalcolo, tipoAppalto, importoGara, tipoProcedura, proceduraUrgente, termineRidotto, bandoWeb, docWeb, oggettoContratto);
    }
    
    return scadenza;
  }

}
