package it.eldasoft.sil.pg.db.domain;

/**
 * Questa classe mappa la tabella DGUE_ELABORAZIONI
 * @author gabriele.nencini
 *
 */
public class DgueElaborazione {
//chiave possibilmente autogenerato
  public static final String TABELLA = "DGUE_ELABORAZIONI";
  private Long id;
  private String codgar;
  private String ngara;
  private String codimp;
  private String dignomdoc;
  private String nomeoe;
  private String piva;
  private String cf;
  private String ruolo;
  private String esclusione;
  private String interno;
  private String isgruppo;
  private String componenti;
  private String nomegruppo;
  private String isconsorzio;
  private String consorziate;
  private DgueElaborazioneStatus stato;
  private DgueBatch dgueBatch;//n:1 relationship
  private DgueBatchDoc dgueBatchDoc;//n:1 relationship
  private String errorMsg;
  
  
  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  
  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  
  /**
   * @return the codgar
   */
  public String getCodgar() {
    return codgar;
  }

  
  /**
   * @param codgar the codgar to set
   */
  public void setCodgar(String codgar) {
    this.codgar = codgar;
  }

  
  /**
   * @return the ngara
   */
  public String getNgara() {
    return ngara;
  }

  
  /**
   * @param ngara the ngara to set
   */
  public void setNgara(String ngara) {
    this.ngara = ngara;
  }

  
  /**
   * @return the codimp
   */
  public String getCodimp() {
    return codimp;
  }

  
  /**
   * @param codimp the codimp to set
   */
  public void setCodimp(String codimp) {
    this.codimp = codimp;
  }

  
  /**
   * @return the dignomdoc
   */
  public String getDignomdoc() {
    return dignomdoc;
  }

  
  /**
   * @param dignomdoc the dignomdoc to set
   */
  public void setDignomdoc(String dignomdoc) {
    this.dignomdoc = dignomdoc;
  }

  
  /**
   * @return the nomeoe
   */
  public String getNomeoe() {
    return nomeoe;
  }

  
  /**
   * @param nomeoe the nomeoe to set
   */
  public void setNomeoe(String nomeoe) {
    this.nomeoe = nomeoe;
  }

  
  /**
   * @return the piva
   */
  public String getPiva() {
    return piva;
  }

  
  /**
   * @param piva the piva to set
   */
  public void setPiva(String piva) {
    this.piva = piva;
  }

  
  /**
   * @return the cf
   */
  public String getCf() {
    return cf;
  }

  
  /**
   * @param cf the cf to set
   */
  public void setCf(String cf) {
    this.cf = cf;
  }

  
  /**
   * @return the ruolo
   */
  public String getRuolo() {
    return ruolo;
  }

  
  /**
   * @param ruolo the ruolo to set
   */
  public void setRuolo(String ruolo) {
    this.ruolo = ruolo;
  }

  
  /**
   * @return the esclusione
   */
  public String getEsclusione() {
    return esclusione;
  }

  
  /**
   * @param esclusione the esclusione to set
   */
  public void setEsclusione(String esclusione) {
    this.esclusione = esclusione;
  }

  
  /**
   * @return the interno
   */
  public String getInterno() {
    return interno;
  }

  
  /**
   * @param interno the interno to set
   */
  public void setInterno(String interno) {
    this.interno = interno;
  }

  
  /**
   * @return the isgruppo
   */
  public String getIsgruppo() {
    return isgruppo;
  }

  
  /**
   * @param isgruppo the isgruppo to set
   */
  public void setIsgruppo(String isgruppo) {
    this.isgruppo = isgruppo;
  }

  
  /**
   * @return the componenti
   */
  public String getComponenti() {
    return componenti;
  }

  
  /**
   * @param componenti the componenti to set
   */
  public void setComponenti(String componenti) {
    this.componenti = componenti;
  }

  
  /**
   * @return the nomegruppo
   */
  public String getNomegruppo() {
    return nomegruppo;
  }

  
  /**
   * @param nomegruppo the nomegruppo to set
   */
  public void setNomegruppo(String nomegruppo) {
    this.nomegruppo = nomegruppo;
  }

  
  /**
   * @return the isconsorzio
   */
  public String getIsconsorzio() {
    return isconsorzio;
  }

  
  /**
   * @param isconsorzio the isconsorzio to set
   */
  public void setIsconsorzio(String isconsorzio) {
    this.isconsorzio = isconsorzio;
  }

  
  /**
   * @return the consorziate
   */
  public String getConsorziate() {
    return consorziate;
  }

  
  /**
   * @param consorziate the consorziate to set
   */
  public void setConsorziate(String consorziate) {
    this.consorziate = consorziate;
  }

  
  /**
   * @return the stato
   */
  public DgueElaborazioneStatus getStato() {
    return stato;
  }

  
  /**
   * @param stato the stato to set
   */
  public void setStato(DgueElaborazioneStatus stato) {
    this.stato = stato;
  }

  
  /**
   * @return the dgueBatch
   */
  public DgueBatch getDgueBatch() {
    return dgueBatch;
  }

  
  /**
   * @param dgueBatch the dgueBatch to set
   */
  public void setDgueBatch(DgueBatch dgueBatch) {
    this.dgueBatch = dgueBatch;
  }

  
  /**
   * @return the dgueBatchDoc
   */
  public DgueBatchDoc getDgueBatchDoc() {
    return dgueBatchDoc;
  }

  
  /**
   * @param dgueBatchDoc the dgueBatchDoc to set
   */
  public void setDgueBatchDoc(DgueBatchDoc dgueBatchDoc) {
    this.dgueBatchDoc = dgueBatchDoc;
  }
  
  /**
   * @return the errorMsg
   */
  public String getErrorMsg() {
    return errorMsg;
  }
  
  /**
   * @param errorMsg the errorMsg to set
   */
  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }


  @Override
  public String toString() {
    return "DgueElaborazione ["
        + (id != null ? "id=" + id + ", " : "")
        + (codgar != null ? "codgar=" + codgar + ", " : "")
        + (ngara != null ? "ngara=" + ngara + ", " : "")
        + (codimp != null ? "codimp=" + codimp + ", " : "")
        + (stato != null ? "stato=" + stato + ", " : "")
        + (dgueBatch != null ? "dgueBatch.id=" + dgueBatch.getId() + ", " : "")
        + (dgueBatchDoc != null ? "dgueBatchDoc.id=" + dgueBatchDoc.getId() : "")
        + "]";
  }
  
  
}
