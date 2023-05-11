package it.eldasoft.sil.pg.db.domain.nso;

import java.io.Serializable;
import java.util.Date;

/**
 * This class represent an Order
 * @author gabriele.nencini
 *
 */
public class Ordine implements Serializable, Comparable<Ordine> {
  private static final long serialVersionUID = 1L;
  
  private Long id;
  private Long id_padre;
  private Long id_originario;
  private Long id_versione;
  private Date data_inizio_val;
  private Date data_fine_val;
  private String codord;  
  private String codord_padre;  
  private String codord_originario;  
  private String ngara;
  private String oggetto;
  private String rif_offerta;
  private Date data_ordine;
  private Integer stato_ordine;
  private Integer azione_ordine;
  private String referente;
  private String centro_costo;
  private Date data_scadenza;
  private Date data_limite_mod;
  private String cig;
  private String esenzione_cig;
  private String cup;
  private String nrepat;
  private String note;
  private String is_div_benef;
  private Date data_inizio_forn;
  private Date data_fine_forn;
  private String codein_fattura;
  private String ufficio_fattura;
  private String condizioni_pagamento;
  private Double arrotondamento;
  private String codein; //reference to uffint
  private String nomein; //field from uffint by direct join
  private String piva; //field from uffint by direct join
  private String codnaz; //field from uffint by direct join with tab2
  private Double importo_totale;//field to be transfer to WS
  
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public Long getId_padre() {
    return id_padre;
  }
  
  public void setId_padre(Long id_padre) {
    this.id_padre = id_padre;
  }
  
  public Long getId_versione() {
    return id_versione;
  }
  
  public void setId_versione(Long id_versione) {
    this.id_versione = id_versione;
  }
  
  public Date getData_inizio_val() {
    return data_inizio_val;
  }
  
  public void setData_inizio_val(Date data_inizio_val) {
    this.data_inizio_val = data_inizio_val;
  }
  
  public Date getData_fine_val() {
    return data_fine_val;
  }
  
  public void setData_fine_val(Date data_fine_val) {
    this.data_fine_val = data_fine_val;
  }
  
  public String getCodord() {
    return codord;
  }
  
  public void setCodord(String codord) {
    this.codord = codord;
  }
  
  public String getNgara() {
    return ngara;
  }
  
  public void setNgara(String ngara) {
    this.ngara = ngara;
  }
  
  public String getOggetto() {
    return oggetto;
  }
  
  public void setOggetto(String oggetto) {
    this.oggetto = oggetto;
  }
  
  public String getRif_offerta() {
    return rif_offerta;
  }
  
  public void setRif_offerta(String rif_offerta) {
    this.rif_offerta = rif_offerta;
  }
  
  public Date getData_ordine() {
    return data_ordine;
  }
  
  public void setData_ordine(Date data_ordine) {
    this.data_ordine = data_ordine;
  }
  
  public Integer getStato_ordine() {
    return stato_ordine;
  }
  
  public void setStato_ordine(Integer stato_ordine) {
    this.stato_ordine = stato_ordine;
  }
  
  public Integer getAzione_ordine() {
    return azione_ordine;
  }
  
  public void setAzione_ordine(Integer azione_ordine) {
    this.azione_ordine = azione_ordine;
  }
  
  public String getReferente() {
    return referente;
  }
  
  public void setReferente(String referente) {
    this.referente = referente;
  }
  
  public String getCentro_costo() {
    return centro_costo;
  }
  
  public void setCentro_costo(String centro_costo) {
    this.centro_costo = centro_costo;
  }
  
  public Date getData_scadenza() {
    return data_scadenza;
  }
  
  public void setData_scadenza(Date data_scadenza) {
    this.data_scadenza = data_scadenza;
  }
  
  public Date getData_limite_mod() {
    return data_limite_mod;
  }
  
  public void setData_limite_mod(Date data_limite_mod) {
    this.data_limite_mod = data_limite_mod;
  }
  
  public String getCig() {
    return cig;
  }
  
  public void setCig(String cig) {
    this.cig = cig;
  }
  
  public String getEsenzione_cig() {
    return esenzione_cig;
  }
  
  public void setEsenzione_cig(String esenzione_cig) {
    this.esenzione_cig = esenzione_cig;
  }
  
  public String getCup() {
    return cup;
  }
  
  public void setCup(String cup) {
    this.cup = cup;
  }
  
  public String getNrepat() {
    return nrepat;
  }
  
  public void setNrepat(String nrepat) {
    this.nrepat = nrepat;
  }
  
  public String getNote() {
    return note;
  }
  
  public void setNote(String note) {
    this.note = note;
  }
  
  public String getIs_div_benef() {
    return is_div_benef;
  }
  
  public void setIs_div_benef(String is_div_benef) {
    this.is_div_benef = is_div_benef;
  }
  
  public Date getData_inizio_forn() {
    return data_inizio_forn;
  }
  
  public void setData_inizio_forn(Date data_inizio_forn) {
    this.data_inizio_forn = data_inizio_forn;
  }
  
  public Date getData_fine_forn() {
    return data_fine_forn;
  }
  
  public void setData_fine_forn(Date data_fine_forn) {
    this.data_fine_forn = data_fine_forn;
  }
  
  public String getCodein_fattura() {
    return codein_fattura;
  }
  
  public void setCodein_fattura(String codein_fattura) {
    this.codein_fattura = codein_fattura;
  }
  
  public String getUfficio_fattura() {
    return ufficio_fattura;
  }
  
  public void setUfficio_fattura(String ufficio_fattura) {
    this.ufficio_fattura = ufficio_fattura;
  }
  
  public String getCondizioni_pagamento() {
    return condizioni_pagamento;
  }
  
  public void setCondizioni_pagamento(String condizioni_pagamento) {
    this.condizioni_pagamento = condizioni_pagamento;
  }
  
  public Double getArrotondamento() {
    return arrotondamento;
  }
  
  public void setArrotondamento(Double arrotondamento) {
    this.arrotondamento = arrotondamento;
  }

  public String getCodein() {
    return codein;
  }
  
  public void setCodein(String codein) {
    this.codein = codein;
  }
  
  public String getNomein() {
    return nomein;
  }
  
  public void setNomein(String nomein) {
    this.nomein = nomein;
  }

  public String getPiva() {
    return piva;
  }

  public void setPiva(String piva) {
    this.piva = piva;
  }
  
  public String getCodnaz() {
    return codnaz;
  }

  public void setCodnaz(String codnaz) {
    this.codnaz = codnaz;
  }

  
  public Long getId_originario() {
    return id_originario;
  }

  
  public void setId_originario(Long id_originario) {
    this.id_originario = id_originario;
  }

  
  public String getCodord_padre() {
    return codord_padre;
  }

  
  public void setCodord_padre(String codord_padre) {
    this.codord_padre = codord_padre;
  }

  
  public String getCodord_originario() {
    return codord_originario;
  }

  
  public void setCodord_originario(String codord_originario) {
    this.codord_originario = codord_originario;
  }

  
  public Double getImporto_totale() {
    return importo_totale;
  }

  
  public void setImporto_totale(Double importo_totale) {
    this.importo_totale = importo_totale;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((codord == null) ? 0 : codord.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((ngara == null) ? 0 : ngara.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Ordine other = (Ordine) obj;
    if (codord == null) {
      if (other.codord != null) return false;
    } else if (!codord.equals(other.codord)) return false;
    if (id == null) {
      if (other.id != null) return false;
    } else if (!id.equals(other.id)) return false;
    if (ngara == null) {
      if (other.ngara != null) return false;
    } else if (!ngara.equals(other.ngara)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Ordine ["
        + (id != null ? "id=" + id + ", " : "")
        + (id_padre != null ? "id_padre=" + id_padre + ", " : "")
        + (id_versione != null ? "id_versione=" + id_versione + ", " : "")
        + (codord != null ? "codord=" + codord + ", " : "")
        + (ngara != null ? "ngara=" + ngara : "")
        + "]";
  }

  @Override
  public int compareTo(Ordine o) {
    if(o==null) return 1;
    if( this.id == o.getId()) return 0;
    if(this.id_padre == o.getId()) return 1;
    if (this.id_versione != null) {
      return this.id_versione.compareTo(o.id_versione);
    }
    return -1;
  }

}
