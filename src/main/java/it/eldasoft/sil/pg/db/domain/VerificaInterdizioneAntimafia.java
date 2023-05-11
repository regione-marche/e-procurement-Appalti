/*
 * Created on 22/feb/2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.db.domain;


/**
 * @author Sara.Santi
 *
 */
public class VerificaInterdizioneAntimafia {
  
  private String            nomimp;
  private String            nomimp_db;
  private String            cfimp;
  private String            pivimp;
  private String            locimp;
  private int               in_archivio;
  private Boolean           interdetta;

  public VerificaInterdizioneAntimafia() {
    this.nomimp = null;
    this.nomimp_db = null;
    this.cfimp = null;
    this.pivimp = null;
    this.locimp = null;
    this.in_archivio = 0;
    this.interdetta = null;
  }

  
  /**
   * @return Ritorna nomimp.
   */
  public String getNomimp() {
    return nomimp;
  }
  
  /**
   * @param nomimp nomimp da settare internamente alla classe.
   */
  public void setNomimp(String nomimp) {
    this.nomimp = nomimp;
  }
  
  /**
   * @return Ritorna nomimp_import.
   */
  public String getNomimp_db() {
    return nomimp_db;
  }


  
  /**
   * @param nomimp_db
   *        nomimp_db da settare internamente alla classe.
   */
  public void setNomimp_db(String nomimp_db) {
    this.nomimp_db = nomimp_db;
  }

  /**
   * @return Ritorna cfimp.
   */
  public String getCfimp() {
    return cfimp;
  }
  
  /**
   * @param cfimp cfimp da settare internamente alla classe.
   */
  public void setCfimp(String cfimp) {
    this.cfimp = cfimp;
  }
  
  /**
   * @return Ritorna pivimp.
   */
  public String getPivimp() {
    return pivimp;
  }
  
  /**
   * @param pivimp pivimp da settare internamente alla classe.
   */
  public void setPivimp(String pivimp) {
    this.pivimp = pivimp;
  }
  
  /**
   * @return Ritorna locimp.
   */
  public String getLocimp() {
    return locimp;
  }
  
  /**
   * @param locimp locimp da settare internamente alla classe.
   */
  public void setLocimp(String locimp) {
    this.locimp = locimp;
  }
  
  /**
   * @return Ritorna in_archivio.
   */
  public int getIn_archivio() {
    return in_archivio;
  }
  
  /**
   * @param in_archivio in_archivio da settare internamente alla classe.
   */
  public void setIn_archivio(int in_archivio) {
    this.in_archivio = in_archivio;
  }
  
  /**
   * @return Ritorna interdetta.
   */
  public Boolean getInterdetta() {
    return interdetta;
  }
  
  /**
   * @param interdetta interdetta da settare internamente alla classe.
   */
  public void setInterdetta(Boolean interdetta) {
    this.interdetta = interdetta;
  }

  
  

}
