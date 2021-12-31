/*
 * Created on 01/giu/2017
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.bl.scp;

import java.io.Serializable;

/**
 * Dati Categoria del lotto.
 *
 * @author Mirco.Franzoni
 */
public class CpvLottoEntry implements Serializable {
  /**
   * UID.
   */
  private static final long serialVersionUID = -4433185026855332865L;
  
  private String cpv;
  
  public void setCpv(String cpv) {
      this.cpv = cpv;
  }
  public String getCpv() {
      return cpv;
  }
  

}
