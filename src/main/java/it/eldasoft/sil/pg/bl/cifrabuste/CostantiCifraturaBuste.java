/*
 * Created on 08/ott/2015
 *
 * Copyright (c) Maggioli S.p.A. - Divisione EldaSoft 
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione EldaSoft
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.bl.cifrabuste;

/**
 * Classe provvisoria con la chiave privata e pubblica da utilizzare per la cifratura della chiave di sessione.
 * 
 * @author Stefano.Sabbadin
 */
public class CostantiCifraturaBuste {

  /** Chiave pubblica. */
  public static final String PUBLIC_KEY  = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC5RFMvQD+1Ac0Qi+57nIBktwn9FuxtXVRyR0gg\n"
                                             + "NAvtVn9r2ctPHFabi8eC4oaR0NW8KnsVhUhuFNwFnVIQyNy0fq3UpfaKZoRVJlGdVrmwxBvjkArH\n"
                                             + "dQKzUjW6eS26KnhDanOhgB0Ln8rjbqh3zAm6TrZIhQSni2bpuxsGFpGO7wIDAQAB";

  /** Chiave privata. */
  public static final String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALlEUy9AP7UBzRCL7nucgGS3Cf0W\n"
                                             + "7G1dVHJHSCA0C+1Wf2vZy08cVpuLx4LihpHQ1bwqexWFSG4U3AWdUhDI3LR+rdSl9opmhFUmUZ1W\n"
                                             + "ubDEG+OQCsd1ArNSNbp5LboqeENqc6GAHQufyuNuqHfMCbpOtkiFBKeLZum7GwYWkY7vAgMBAAEC\n"
                                             + "gYA/wg3GoxvJlTcXTLDkBXYCMyPS38K52HapZXKi8oZwRWZQnYFkVmJP4YjluEOLhw0nVo9JVrcY\n"
                                             + "e0FFBWEquZWKSnBHUZMRSCD/zxAq37JR5FB46O4bNJhr63fsH6yqnrVY5CGqZqRF8NVaK1+jsXoA\n"
                                             + "NVetgzkTiHDMIcK9h94/UQJBANrX3FxVDgk2zSuIvsXlFwyyUtDgeQnxUU081V4yRXCXwtYKiYts\n"
                                             + "QHDCD/Oqja6RDtFIznrh9qu/xYzlpk9uO4MCQQDYuQ0snUyQrBTz3HA9dWWWbG8/KkJmwklYOAgA\n"
                                             + "Y+xG60oxob/ZduBrW/f/i6usrFdSY0BkLxmNxDE0X4+OhiclAkB48LZNKIwbN3fnSSj0wIgeciYm\n"
                                             + "XQdHIV+m6amY5vtNH/GCzEv7CxYJupWKOYUXJf8kVbIWYu4pOE/6b6ebWfX1AkAckJd57+mtj+Db\n"
                                             + "G4z3rMeNAhHPo4RJAwajyA7V7lWwK0cGL6mXwiFmRSL03bXs9nIz+7z693JIyzkpDAdh47C5AkA8\n"
                                             + "alNPetLomxmMhniqPqxQSPkcxIhAQRO66IGrmdLTYSovAI+hurjTqwCYLZLvDkjYXeVz7ASZX7jX\n"
                                             + "yWz62AHX";
}
