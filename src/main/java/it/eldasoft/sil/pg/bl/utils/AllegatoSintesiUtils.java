/*
 * Created on 4 giu 2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.utils;

import it.eldasoft.gene.bl.SqlManager;

public class AllegatoSintesiUtils {

  public static final String NomefileAllegatoPdf = "'_comunicazione.pdf'";
  public static final String NomefileAllegatoTsd = "'_comunicazione.pdf.tsd'";

  /**
   * Viene creato il filtro per la ricerca nel db dell'occorrenza relativa al file di sintesi.
   * Con il parametro tsd viene attivata la ricerca del file con formato tsd
   * @param tsd
   * @return String
   */
  public static String creazioneFiltroNomeFileSintesi(boolean tsd, SqlManager sqlManager) {
    String conversioneStringa = sqlManager.getDBFunction("inttostr",  new String[] {"IDDOCDIG"});
    String concatenazione  = sqlManager.getDBFunction("concat",  new String[] {"IDPRG" , conversioneStringa });
    if(tsd)
      concatenazione = sqlManager.getDBFunction("concat",  new String[] {concatenazione , NomefileAllegatoTsd });
    else
      concatenazione = sqlManager.getDBFunction("concat",  new String[] {concatenazione , NomefileAllegatoPdf });
    concatenazione = "(" + concatenazione + ")";
    return concatenazione;
  }
}
