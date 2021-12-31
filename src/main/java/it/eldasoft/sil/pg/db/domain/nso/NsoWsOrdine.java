package it.eldasoft.sil.pg.db.domain.nso;

import java.io.Serializable;

/**
 * This class represent an NsoWsOrdine
 * @author gabriele.nencini
 *
 */
public class NsoWsOrdine implements Serializable, Comparable<NsoWsOrdine> {
  private static final long serialVersionUID = 1L;
  
  private Long id;
  private String fileName;
  private byte[] fileXml;
  
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public String getFileName() {
    return fileName;
  }
  
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
  
  public byte[] getFileXml() {
    return fileXml;
  }
  
  public void setFileXml(byte[] fileXml) {
    this.fileXml = fileXml;
  }

  @Override
  public int compareTo(NsoWsOrdine arg0) {
    if(arg0==null) return 1;
    return this.id.compareTo(arg0.getId());
  }

}
