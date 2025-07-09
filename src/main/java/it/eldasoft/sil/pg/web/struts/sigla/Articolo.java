/*
 * Created on 24-ago-2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts.sigla;

import java.io.Serializable;

/**
 * Bean per l'interfacciamento 
 *
 * @author Alex.Mancini
 */
public class Articolo implements Serializable {

  /**
   * UID.
   */
  private static final long serialVersionUID = 2572089103460119720L;


  private int                id;
  private String             cd_bene_servizio;
  private String             ds_bene_servizio;
  private String             unita_misura;
  private Boolean            fl_gestione_inventario;
  private Boolean            fl_gestione_magazzino;
  private String             cd_voce_iva;
  private String             tipoGestione;
  
  public int getId() {
    return id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public String getCd_bene_servizio() {
    return cd_bene_servizio;
  }
  
  public void setCd_bene_servizio(String cd_bene_servizio) {
    this.cd_bene_servizio = cd_bene_servizio;
  }
  
  public String getDs_bene_servizio() {
    return ds_bene_servizio;
  }
  
  public void setDs_bene_servizio(String ds_bene_servizio) {
    this.ds_bene_servizio = ds_bene_servizio;
  }
  
  public String getUnita_misura() {
    return unita_misura;
  }
  
  public void setUnita_misura(String unita_misura) {
    this.unita_misura = unita_misura;
  }
  
  public Boolean getFl_gestione_inventario() {
    return fl_gestione_inventario;
  }
  
  public void setFl_gestione_inventario(Boolean fl_gestione_inventario) {
    this.fl_gestione_inventario = fl_gestione_inventario;
  }
  
  public Boolean getFl_gestione_magazzino() {
    return fl_gestione_magazzino;
  }
  
  public void setFl_gestione_magazzino(Boolean fl_gestione_magazzino) {
    this.fl_gestione_magazzino = fl_gestione_magazzino;
  }
  
  public String getCd_voce_iva() {
    return cd_voce_iva;
  }
  
  public void setCd_voce_iva(String cd_voce_iva) {
    this.cd_voce_iva = cd_voce_iva;
  }
  
  public String getTipoGestione() {
    return tipoGestione;
  }
  
  public void setTipoGestione(String tipoGestione) {
    this.tipoGestione = tipoGestione;
  }
  
  

}