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
package it.eldasoft.sil.pg.db.dao;

/**
 * Interfaccia DAO per l'accesso ai dati della tabella CATSCA
 * 
 * @author Stefano.Sabbadin
 */
public interface ScadenzeDao {
  
  /**
   * Calcola la scadenza per diverse tipologie di date, a seconda dei parametri in input 
   * @param tipoCalcolo tipologia del calcolo in funzione della data:
   * <ul>
   * <li>1 = da trasmissione bando (DIBAND.TORN) a ricezione offerte (DTEOFF.TORN)</li>
   * <li>2 = da trasmissione bando (DIBAND.TORN) a ricezione domande (DTEPAR.TORN)</li>
   * <li>3 = da invio inviti (DINVIT.TORN) a ricezione offerte (DTEOFF.TORN)</li>
   * <li>4 = da inizio pubblicazione bando (DPUBAV.TORN) a fine pubblicazione bando (DFPUBA.TORN) (ex parametro A20s8)</li>
   * <li>5 = da richiesta documenti controllo requisiti (DRIDOC.TORN) a termine invio (DINDOC.TORN) (ex parametro A1012)</li>
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
  public Integer getGiorniScadenza(Integer tipoCalcolo, Integer tipoAppalto, Double importoGara, Integer tipoProcedura, String proceduraUrgente, String termineRidotto, String bandoWeb, String docWeb, Integer oggettoContratto);

}
