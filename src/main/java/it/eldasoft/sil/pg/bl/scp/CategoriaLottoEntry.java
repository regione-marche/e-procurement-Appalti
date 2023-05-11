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
public class CategoriaLottoEntry implements Serializable {
  /**
   * UID.
   */
  private static final long serialVersionUID = -4433185026855332865L;

  private String categoria;
  
  private String classe;
  
  private String scorporabile;

  private String subappaltabile;
  
  public void setCategoria(String categoria) {
      this.categoria = categoria;
  }
  public String getCategoria() {
      return categoria;
  }
  public void setClasse(String classe) {
      this.classe = classe;
  }
  public String getClasse() {
      return classe;
  }
  public void setScorporabile(String scorporabile) {
      this.scorporabile = scorporabile;
  }
  public String getScorporabile() {
      return scorporabile;
  }
  public void setSubappaltabile(String subappaltabile) {
      this.subappaltabile = subappaltabile;
  }
  public String getSubappaltabile() {
      return subappaltabile;
  }

}
