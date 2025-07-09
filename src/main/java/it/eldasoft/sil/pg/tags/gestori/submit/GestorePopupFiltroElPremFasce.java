package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import org.springframework.transaction.TransactionStatus;

import javax.servlet.http.HttpSession;

public class GestorePopupFiltroElPremFasce extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "GARE";
  }

  public GestorePopupFiltroElPremFasce() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupFiltroElPremFasce(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer
      dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer dataColumnContainer) throws GestoreException {
        String filtroElPremFasce = this.getRequest().getParameter("filtroElPremFasce");
        //Carico in sessione il filtro.
        //I valori in sessione vengono sbiancati in GestioneFasiRicezioneFunction.java e GestioneDitteConcorrentiFunction.java
        //cioè ogni volta che si accede alla pagina delle fasi ricezione e ditte concorrenti
        HttpSession sessione = this.getRequest().getSession();
        sessione.setAttribute("filtroElPremFasce", filtroElPremFasce);
        this.getRequest().setAttribute("RISULTATO", "OK");
  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

}
