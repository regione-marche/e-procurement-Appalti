package it.eldasoft.sil.pg.db.domain.nso;

import java.io.Serializable;
import java.util.Date;

public class LineaOrdine implements Serializable, Comparable<LineaOrdine> {
  private static final long serialVersionUID = 1L;
  
  private Integer id;
  private Integer nso_ordini_id;
  private Integer id_linea; 
  private String codice;
  private String descrizione;
  private Double quantita;
  private String unimis;
  private Double prezzo_unitario;
  private Double iva;
  private String codice_esenzione;
  private String centro_costo;
  private String cons_parziale;
  private Date data_inizio_cons;
  private Date data_fine_cons;
  private String codcpv;
  private String note;
  private String codein_rich;
  private String codein_rich_cf;
  private String codein_rich_nome;
  
  public Integer getId() {
    return id;
  }
  
  public void setId(Integer id) {
    this.id = id;
  }
  
  public Integer getNso_ordini_id() {
    return nso_ordini_id;
  }
  
  public void setNso_ordini_id(Integer nso_ordini_id) {
    this.nso_ordini_id = nso_ordini_id;
  }
  
  public Integer getId_linea() {
    return id_linea;
  }
  
  public void setId_linea(Integer id_linea) {
    this.id_linea = id_linea;
  }
  
  public String getCodice() {
    return codice;
  }
  
  public void setCodice(String codice) {
    this.codice = codice;
  }
  
  public String getDescrizione() {
    return descrizione;
  }
  
  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }
  
  public Double getQuantita() {
    return quantita;
  }
  
  public void setQuantita(Double quantita) {
    this.quantita = quantita;
  }
  
  public String getUnimis() {
    return unimis;
  }
  
  public void setUnimis(String unimis) {
    this.unimis = unimis;
  }
  
  public Double getPrezzo_unitario() {
    return prezzo_unitario;
  }
  
  public void setPrezzo_unitario(Double prezzo_unitario) {
    this.prezzo_unitario = prezzo_unitario;
  }
  
  public Double getIva() {
    return iva;
  }
  
  public void setIva(Double iva) {
    this.iva = iva;
  }
  
  public String getCodice_esenzione() {
    return codice_esenzione;
  }
  
  public void setCodice_esenzione(String codice_esenzione) {
    this.codice_esenzione = codice_esenzione;
  }
  
  public String getCentro_costo() {
    return centro_costo;
  }
  
  public void setCentro_costo(String centro_costo) {
    this.centro_costo = centro_costo;
  }
  
  public String getCons_parziale() {
    return cons_parziale;
  }
  
  public void setCons_parziale(String cons_parziale) {
    this.cons_parziale = cons_parziale;
  }
  
  public Date getData_inizio_cons() {
    return data_inizio_cons;
  }
  
  public void setData_inizio_cons(Date data_inizio_cons) {
    this.data_inizio_cons = data_inizio_cons;
  }
  
  public Date getData_fine_cons() {
    return data_fine_cons;
  }
  
  public void setData_fine_cons(Date data_fine_cons) {
    this.data_fine_cons = data_fine_cons;
  }
  
  public String getCodcpv() {
    return codcpv;
  }
  
  public void setCodcpv(String codcpv) {
    this.codcpv = codcpv;
  }
  
  public String getNote() {
    return note;
  }
  
  public void setNote(String note) {
    this.note = note;
  }
  
  public String getCodein_rich() {
    return codein_rich;
  }
  
  public void setCodein_rich(String codein_rich) {
    this.codein_rich = codein_rich;
  }
  
  public String getCodein_rich_cf() {
    return codein_rich_cf;
  }
  
  public void setCodein_rich_cf(String codein_rich_cf) {
    this.codein_rich_cf = codein_rich_cf;
  }
  
  public String getCodein_rich_nome() {
    return codein_rich_nome;
  }
  
  public void setCodein_rich_nome(String codein_rich_nome) {
    this.codein_rich_nome = codein_rich_nome;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id_linea == null) ? 0 : id_linea.hashCode());
    result = prime * result + ((nso_ordini_id == null) ? 0 : nso_ordini_id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    LineaOrdine other = (LineaOrdine) obj;
    if (id_linea == null) {
      if (other.id_linea != null) return false;
    } else if (!id_linea.equals(other.id_linea)) return false;
    if (nso_ordini_id == null) {
      if (other.nso_ordini_id != null) return false;
    } else if (!nso_ordini_id.equals(other.nso_ordini_id)) return false;
    return true;
  }

  @Override
  public int compareTo(LineaOrdine arg0) {
    if(arg0==null) return 1;
    return this.getId_linea().compareTo(arg0.getId_linea());
  }

  @Override
  public String toString() {
    return "LineaOrdine ["
        + (id != null ? "id=" + id + ", " : "")
        + (nso_ordini_id != null ? "nso_ordini_id=" + nso_ordini_id + ", " : "")
        + (id_linea != null ? "id_linea=" + id_linea + ", " : "")
        + (codice != null ? "codice=" + codice : "")
        + "]";
  }
  
  
}
