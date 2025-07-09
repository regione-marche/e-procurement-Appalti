package it.eldasoft.sil.pg.bl.nadc.model;


import java.util.Date;
import java.util.List;

public class Contratto {
  private List<Rda> rda;
  private String id;
  private String n_doc;
  private Integer num_var;
  private String cig;
  private String rilascio;
  private String classe;
  private Date data_creazione;
  private String id_creatore;
  private String cogn_creatore;
  private String nome_creatore;
  private Double imp_var00;
  private Double imp_valuta00;
  private String valuta;
  private Date dt_firma00;
  private String cod_proc00;
  private String cognome_proc;
  private String nome_proc;
  private String oggetto;
  private Double imp_contratto;
  private String area_acq;
  private String rup;
  private Date data_nom_rup;
  private String rec;
  private String gruppo_rilascio;
  private String strategia_rilascio;
  private String descr_strategia;
  private String trattativa;
  private String decod_tratt;
  private String tipologia;
  private String cat_merc;
  private String decod_classe;
  private String fornitore;
  private String cod_id;
  private String piva;
  private String piva_est;
  private String rag_soc;
  private String nazione;
  private String tp_doc;
  private Date dt_firma;
  private String proc_firma;
  private String cognome;
  private String nome;
  private Date dt_inizio_val;
  private Date dt_fine_val;
  private String indirizzo;
  private String citta;
  private String cap;
  private String provincia;

  public List<Rda> getRda() {
    return rda;
  }

  public void setRda(List<Rda> rda) {
    this.rda = rda;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getN_doc() {
    return n_doc;
  }

  public void setN_doc(String n_doc) {
    this.n_doc = n_doc;
  }

  public int getNum_var() {
    return num_var;
  }

  public void setNum_var(int num_var) {
    this.num_var = num_var;
  }

  public String getCig() {
    return cig;
  }

  public void setCig(String cig) {
    this.cig = cig;
  }

  public String getRilascio() {
    return rilascio;
  }

  public void setRilascio(String rilascio) {
    this.rilascio = rilascio;
  }

  public String getClasse() {
    return classe;
  }

  public void setClasse(String classe) {
    this.classe = classe;
  }

  public Date getData_creazione() {
    return data_creazione;
  }

  public void setData_creazione(Date data_creazione) {
    this.data_creazione = data_creazione;
  }

  public String getId_creatore() {
    return id_creatore;
  }

  public void setId_creatore(String id_creatore) {
    this.id_creatore = id_creatore;
  }

  public String getCogn_creatore() {
    return cogn_creatore;
  }

  public void setCogn_creatore(String cogn_creatore) {
    this.cogn_creatore = cogn_creatore;
  }

  public String getNome_creatore() {
    return nome_creatore;
  }

  public void setNome_creatore(String nome_creatore) {
    this.nome_creatore = nome_creatore;
  }

  public Double getImp_var00() {
    return imp_var00;
  }

  public void setImp_var00(Double imp_var00) {
    this.imp_var00 = imp_var00;
  }

  public Double getImp_valuta00() {
    return imp_valuta00;
  }

  public void setImp_valuta00(Double imp_valuta00) {
    this.imp_valuta00 = imp_valuta00;
  }

  public String getValuta() {
    return valuta;
  }

  public void setValuta(String valuta) {
    this.valuta = valuta;
  }

  public Date getDt_firma00() {
    return dt_firma00;
  }

  public void setDt_firma00(Date dt_firma00) {
    this.dt_firma00 = dt_firma00;
  }

  public String getCod_proc00() {
    return cod_proc00;
  }

  public void setCod_proc00(String cod_proc00) {
    this.cod_proc00 = cod_proc00;
  }

  public String getCognome_proc() {
    return cognome_proc;
  }

  public void setCognome_proc(String cognome_proc) {
    this.cognome_proc = cognome_proc;
  }

  public String getNome_proc() {
    return nome_proc;
  }

  public void setNome_proc(String nome_proc) {
    this.nome_proc = nome_proc;
  }

  public String getOggetto() {
    return oggetto;
  }

  public void setOggetto(String oggetto) {
    this.oggetto = oggetto;
  }

  public Double getImp_contratto() {
    return imp_contratto;
  }

  public void setImp_contratto(Double imp_contratto) {
    this.imp_contratto = imp_contratto;
  }

  public String getArea_acq() {
    return area_acq;
  }

  public void setArea_acq(String area_acq) {
    this.area_acq = area_acq;
  }

  public String getRup() {
    return rup;
  }

  public void setRup(String rup) {
    this.rup = rup;
  }

  public Date getData_nom_rup() {
    return data_nom_rup;
  }

  public void setData_nom_rup(Date data_nom_rup) {
    this.data_nom_rup = data_nom_rup;
  }

  public String getRec() {
    return rec;
  }

  public void setRec(String rec) {
    this.rec = rec;
  }

  public String getGruppo_rilascio() {
    return gruppo_rilascio;
  }

  public void setGruppo_rilascio(String gruppo_rilascio) {
    this.gruppo_rilascio = gruppo_rilascio;
  }

  public String getStrategia_rilascio() {
    return strategia_rilascio;
  }

  public void setStrategia_rilascio(String strategia_rilascio) {
    this.strategia_rilascio = strategia_rilascio;
  }

  public String getDescr_strategia() {
    return descr_strategia;
  }

  public void setDescr_strategia(String descr_strategia) {
    this.descr_strategia = descr_strategia;
  }

  public String getTrattativa() {
    return trattativa;
  }

  public void setTrattativa(String trattativa) {
    this.trattativa = trattativa;
  }

  public String getDecod_tratt() {
    return decod_tratt;
  }

  public void setDecod_tratt(String decod_tratt) {
    this.decod_tratt = decod_tratt;
  }

  public String getTipologia() {
    return tipologia;
  }

  public void setTipologia(String tipologia) {
    this.tipologia = tipologia;
  }

  public String getCat_merc() {
    return cat_merc;
  }

  public void setCat_merc(String cat_merc) {
    this.cat_merc = cat_merc;
  }

  public String getDecod_classe() {
    return decod_classe;
  }

  public void setDecod_classe(String decod_classe) {
    this.decod_classe = decod_classe;
  }

  public String getFornitore() {
    return fornitore;
  }

  public void setFornitore(String fornitore) {
    this.fornitore = fornitore;
  }

  public String getCod_id() {
    return cod_id;
  }

  public void setCod_id(String cod_id) {
    this.cod_id = cod_id;
  }

  public String getPiva() {
    return piva;
  }

  public void setPiva(String piva) {
    this.piva = piva;
  }

  public String getPiva_est() {
    return piva_est;
  }

  public void setPiva_est(String piva_est) {
    this.piva_est = piva_est;
  }

  public String getRag_soc() {
    return rag_soc;
  }

  public void setRag_soc(String rag_soc) {
    this.rag_soc = rag_soc;
  }

  public String getNazione() {
    return nazione;
  }

  public void setNazione(String nazione) {
    this.nazione = nazione;
  }

  public String getTp_doc() {
    return tp_doc;
  }

  public void setTp_doc(String tp_doc) {
    this.tp_doc = tp_doc;
  }

  public Date getDt_firma() {
    return dt_firma;
  }

  public void setDt_firma(Date dt_firma) {
    this.dt_firma = dt_firma;
  }

  public String getProc_firma() {
    return proc_firma;
  }

  public void setProc_firma(String proc_firma) {
    this.proc_firma = proc_firma;
  }

  public String getCognome() {
    return cognome;
  }

  public void setCognome(String cognome) {
    this.cognome = cognome;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public Date getDt_inizio_val() {
    return dt_inizio_val;
  }

  public void setDt_inizio_val(Date dt_inizio_val) {
    this.dt_inizio_val = dt_inizio_val;
  }

  public Date getDt_fine_val() {
    return dt_fine_val;
  }

  public void setDt_fine_val(Date dt_fine_val) {
    this.dt_fine_val = dt_fine_val;
  }

  public String getIndirizzo() {
    return indirizzo;
  }

  public void setIndirizzo(String indirizzo) {
    this.indirizzo = indirizzo;
  }

  public String getCitta() {
    return citta;
  }

  public void setCitta(String citta) {
    this.citta = citta;
  }

  public String getCap() {
    return cap;
  }

  public void setCap(String cap) {
    this.cap = cap;
  }

  public String getProvincia() {
    return provincia;
  }

  public void setProvincia(String provincia) {
    this.provincia = provincia;
  }

  public Contratto() {
    // TODO document why this constructor is empty
  }


}
