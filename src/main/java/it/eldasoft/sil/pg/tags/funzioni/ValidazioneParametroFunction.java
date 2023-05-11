/*
  * Created on: 25/02/2022
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;

/**
 * Funzione per la validazione di parametri che devono essere inseriti direttamente in un sql
 * Attualmente vengono presi in considerazione 2 soli tipi di parametri:
 * Numerici
 * Stringhe, ma limitatamente al caso di codice campo chiave e l'unico controllo fatto è sulla lunghezza
 *          massima di caratteri previsti per il codice
 * @author marcello.caminiti
 *
 */
public class ValidazioneParametroFunction extends AbstractFunzioneTag {

  static Logger                 logger = Logger.getLogger(ValidazioneParametroFunction.class);
  private final static String   messaggio="Errore nella validazione del parametro";

  public ValidazioneParametroFunction() {
    super(4, new Class[] {PageContext.class,String.class,String.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String valore = (String) params[1];
    String tipoParametro = (String) params[2];
    String condizioneValutazione = (String) params[3];

    if(valore==null)
      return null;

    if("N".equals(tipoParametro)) {
      if (!StringUtils.isNumeric("" + valore)) {
        logger.error(messaggio + ": " + valore + ", formato non valido");
        throw new JspException(messaggio);
      }
    }else if("SC".equals(tipoParametro)) {
      //Nel caso di stringa per ora esamino il solo caso di un codice chiave, quindi suppongo ci sia un numero massimo di caratteri ammissibili
      //Stabilire in seguito se fare controlli più evoluti
      if (!StringUtils.isNumeric("" + condizioneValutazione))
        throw new JspException("Errore nella validazione del parametro " + valore + ". Il numero massimo di caratteri è stato inserito in formato non valido.");
      int lunghezzaMassima=new Long(condizioneValutazione).intValue();
      if(valore.length()> lunghezzaMassima) {
        logger.error(messaggio + ": " + valore + ", numero di caratteri maggiore del consentito");
        throw new JspException(messaggio);
      }
      //if(valore.indexOf(";DROP ")>0)
      //  throw new JspException("Errore nella validazione del parametro " + valore + ", formato non valido");
    }


    return null;
  }



}
