package it.eldasoft.sil.pg.nadc.model;


public class DocumentoDaInviare {
  private String cig;
  private String titolo;
  private String tipo;
  private String urlDocumento;
  private Long idDocumento;
  private String codiceGaraEproc;
  private String docRifLotto;
  private Long numOrdine;
  private Long tipologia;
  private String titoloDocumento;
  private String descTipologia;
  private Long idFile;
  private byte[] file;
  private String nomeFile;


  public String getCig() {
      return cig;
  }

  public void setCig(String cig) {
      this.cig = cig;
  }

  public String getTitolo() {
      return titolo;
  }

  public void setTitolo(String titolo) {
      this.titolo = titolo;
  }

  public String getTipo() {
      return tipo;
  }

  public void setTipo(String tipo) {
      this.tipo = tipo;
  }

  public String getUrlDocumento() {
      return urlDocumento;
  }

  public void setUrlDocumento(String urlDocumento) {
      this.urlDocumento = urlDocumento;
  }

  public Long getIdDocumento() {
      return idDocumento;
  }

  public void setIdDocumento(Long idDocumento) {
      this.idDocumento = idDocumento;
  }

  public String getCodiceGaraEproc() {
      return codiceGaraEproc;
  }

  public void setCodiceGaraEproc(String codiceGaraEproc) {
      this.codiceGaraEproc = codiceGaraEproc;
  }

  public String getDocRifLotto() {
      return docRifLotto;
  }

  public void setDocRifLotto(String docRifLotto) {
      this.docRifLotto = docRifLotto;
  }

  public Long getNumOrdine() {
      return numOrdine;
  }

  public void setNumOrdine(Long numOrdine) {
      this.numOrdine = numOrdine;
  }

  public Long getTipologia() {
      return tipologia;
  }

  public void setTipologia(Long tipologia) {
      this.tipologia = tipologia;
  }

  public String getTitoloDocumento() {
      return titoloDocumento;
  }

  public void setTitoloDocumento(String titoloDocumento) {
      this.titoloDocumento = titoloDocumento;
  }

  public String getDescTipologia() {
      return descTipologia;
  }

  public void setDescTipologia(String descTipologia) {
      this.descTipologia = descTipologia;
  }

  public Long getIdFile() {
      return idFile;
  }

  public void setIdFile(Long idFile) {
      this.idFile = idFile;
  }

  public byte[] getFile() {
      return file;
  }

  public void setFile(byte[] file) {
      this.file = file;
  }

  public String getNomeFile() {
      return nomeFile;
  }

  public void setNomeFile(String nomeFile) {
      this.nomeFile = nomeFile;
  }

  public DocumentoDaInviare() {
    
  }
  
  //Costruttore di copia
  public DocumentoDaInviare(DocumentoDaInviare other) {
      this.cig = other.cig;
      this.titolo = other.titolo;
      this.tipo = other.tipo;
      this.urlDocumento = other.urlDocumento;
      this.idDocumento = other.idDocumento;
      this.codiceGaraEproc = other.codiceGaraEproc;
      this.docRifLotto = other.docRifLotto;
      this.numOrdine = other.numOrdine;
      this.tipologia = other.tipologia;
      this.titoloDocumento = other.titoloDocumento;
      this.descTipologia = other.descTipologia;
      this.idFile = other.idFile;
      this.file = other.file;
      this.nomeFile = other.nomeFile;
  }
  
  
}
