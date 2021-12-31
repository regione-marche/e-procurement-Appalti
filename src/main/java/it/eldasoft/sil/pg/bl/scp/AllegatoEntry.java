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
 * Dati pubblicazione.
 *
 * @author Mirco.Franzoni
 */

public class AllegatoEntry implements Serializable {
  /**
   * UID.
   */
  private static final long serialVersionUID = -4433185026855332865L;

  private String titolo;

  private String url;
 
  private byte[] file;
  
  public void setTitolo(String titolo) {
      this.titolo = titolo;
  }
  public String getTitolo() {
      return titolo;
  }
  public void setUrl(String url) {
      this.url = url;
  }
  public String getUrl() {
      return url;
  }
  
  public void setFile(byte[] file) {
      this.file = file;
  }
  public byte[] getFile() {
      return file;
  }

}

