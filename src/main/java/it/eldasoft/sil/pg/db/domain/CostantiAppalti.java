/*
 * Created on 20/mar/2018
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.db.domain;


public class CostantiAppalti {
  public static final String PROP_RICHIESTA_FIRMA = "documentiDb.richiestaFirma";

  public static final String FORMATO_ALLEGATI = "allegatiComunicazione.formato";

  public static final String PROP_WSBANDI_TIPO                              = "sitoIstituzionale.ws.tipo";
  public static final String PROP_WSBANDI_URL                               = "sitoIstituzionale.ws.url";
  public static final String PROP_WSBANDI_TOKEN                             = "sitoIstituzionale.ws.token";
  public static final String PROP_WSBANDI_USER                              = "sitoIstituzionale.ws.user";
  public static final String PROP_WS_PORTALEAPPALTI_BANDI_URL               = "sitoIstituzionale.ws.urlBandiPortaleAppalti";
  public static final String PROP_WS_PORTALEAPPALTI_ESITI_URL               = "sitoIstituzionale.ws.urlEsitiPortaleAppalti";
  public static final String PROP_WS_PORTALEAPPALTI_AVVISI_URL              = "sitoIstituzionale.ws.urlAvvisiPortaleAppalti";

  public static final String PROP_INTEGRAZIONE_MDGUE_URL                    = "integrazioneMDgue.url";

  public static final String nomeFileXML_Aggiornamento                      = "dati_aggisc.xml";
  public static final String nomeFileXML_Iscrizione                         = "dati_iscele.xml";
  public static final String nomeFileXML_AggiornamentoAnagrafica            = "dati_agganag.xml";
  public static final String nomeFileQestionario                            = "questionario.json";
  public static final String nomeFileDatiPartecipazione                     = "dati_partrti.xml";
  public static final String nomeFileXML_IscrizioneImpresa                  = "dati_reg.xml";
  public static final String NOME_FILE_RINNOVO_ISCRIZIONE                   = "dati_rin.xml";

  public static final String importoOffertoQuestionario                     = "bidValueTotal";
  public static final String ribassoOffertoQuestionario                     = "bidDiscountValueTotal";
  public static final String sezioneDatiQuestionario                        = "survey";
  public static final String sezioneFileCancellatiQuestionario              = "deletedFiles";

  //Valori dello stato di DITGQFORM
  public static final int statoDITGQFORMDaAttivare                          = 1;
  public static final int statoDITGQFORMAttivo                              = 2;
  public static final int statoDITGQFORMErrori                              = 3;
  public static final int statoDITGQFORMRifiutato                           = 4;
  public static final int statoDITGQFORMSuperato                            = 5;

  public static final String PROP_APPALTI_MS_URL                            = "appalti-ms.ws.url";

  public static final String FORMATO_ALLEGATI_FIRMATI                       = "PDF.P7M";

}
