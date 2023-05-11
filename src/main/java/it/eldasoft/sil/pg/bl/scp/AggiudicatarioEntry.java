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

import it.eldasoft.sil.pg.bl.scp.ImpresaEntry;

import java.io.Serializable;

/**
 * Dati aggiudicatario.
 *
 * @author Mirco.Franzoni
 */

public class AggiudicatarioEntry implements Serializable {
  /**
   * UID.
   */
  private static final long serialVersionUID = -4433185026855332865L;

  private Long tipoAggiudicatario;  
  
  private Long ruolo; 

  private ImpresaEntry impresa;  
  
  private Long idGruppo; 

  public void setTipoAggiudicatario(Long tipoAggiudicatario) {
      this.tipoAggiudicatario = tipoAggiudicatario;
  }
  public Long getTipoAggiudicatario() {
      return tipoAggiudicatario;
  }
  public void setRuolo(Long ruolo) {
      this.ruolo = ruolo;
  }
  public Long getRuolo() {
      return ruolo;
  }
  public void setIdGruppo(Long idGruppo) {
      this.idGruppo = idGruppo;
  }
  public Long getIdGruppo() {
      return idGruppo;
  }
  public void setImpresa(ImpresaEntry impresa) {
      this.impresa = impresa;
  }
  public ImpresaEntry getImpresa() {
      return impresa;
  }
}