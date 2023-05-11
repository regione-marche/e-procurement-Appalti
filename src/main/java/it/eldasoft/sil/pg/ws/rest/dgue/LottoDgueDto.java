package it.eldasoft.sil.pg.ws.rest.dgue;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public class LottoDgueDto {
  private String cig;
  private String numLotto;
  
  /**
   * @return the numLotto
   */
  public String getNumLotto() {
    return numLotto;
  }
  
  /**
   * @param numLotto the numLotto to set
   */
  public void setNumLotto(String numLotto) {
    this.numLotto = numLotto;
  }

  /**
   * @return the cig
   */
  public String getCig() {
    return cig;
  }
  
  /**
   * @param cig the cig to set
   */
  public void setCig(String cig) {
    this.cig = cig;
  }

}
