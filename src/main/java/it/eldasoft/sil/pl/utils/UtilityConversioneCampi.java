/*
 * Created on 6-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pl.utils;

/**
 * Classe di supporto per utilità presenti in PL e relative alle conversioni di
 * dati
 * 
 * @author Stefano.Sabbadin
 */
public class UtilityConversioneCampi {

  /**
   * Estrae il nome del campo derivante da livpro
   * 
   * @param livpro
   *        livello di progettazione
   * @return nome del campo presente nella tabella PERI
   */
  public static String getCampoDerivanteDaLivpro(int livpro) {
    String campoDerivante = "";
    switch (livpro) {
    case 0:
      campoDerivante = "codstf";
      break;
    case 1:
      campoDerivante = "codprg";
      break;
    case 2:
      campoDerivante = "pcodpr";
      break;
    }
    return campoDerivante;
  }

  /**
   * Converte livpro in prglav
   * 
   * @param newLivpro
   *        livello di progettazione
   * @return prglav
   */
  public static int livpro2prglav(int newLivpro) {
    int prglav = 2;
    switch (newLivpro) {
    case 3: // Lavoro Esecutivo
      prglav = 1;
      break;
    case 2: // Lavoro Definitivo
      prglav = 2;
      break;
    case 1: // Lavoro preliminare
      prglav = 3;
      break;
    case 0: // Studio di fattibilità
      prglav = 4;
      break;
    }
    return prglav;
  }

  /**
   * Converte livpro in prglav
   * 
   * @param newLivpro
   *        livello di progettazione
   * @return prglav
   */
  public static int livpro2tipese(int newLivpro) {
    int tipese = 5;
    switch (newLivpro) {
    case 3: // Lavoro Esecutivo
      tipese = 2;
      break;
    case 2: // Lavoro Definitivo
      tipese = 4;
      break;
    case 1: // Lavoro preliminare
      tipese = 3;
      break;
    case 0: // Studio di fattibilità
      tipese = 5;
      break;
    }
    return tipese;
  }

}
