package it.eldasoft.sil.pg.tags.gestori.submit;



import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;


public class GestoreW_INVCOMInvia extends AbstractGestoreChiaveNumerica {

  /** Logger */
  static Logger logger = Logger.getLogger(GestoreW_INVCOMInvia.class);

  @Override
  public String[] getAltriCampiChiave() {
    return new String[] { "IDPRG" };
  }

  @Override
  public String getCampoNumericoChiave() {
    return "IDCOM";
  }

  @Override
  public String getEntita() {
    return "W_INVCOM";
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {


  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    String idprg = datiForm.getString("IDPRG");
    Long idcom = datiForm.getLong("IDCOM");
    Long compub = datiForm.getLong("COMPUB");
    String cenint = datiForm.getString("IDCFG_NEW");
    
    Long idcomris = datiForm.getLong("IDCOMRIS");
    String idprgris = datiForm.getString("IDPRGRIS");

    this.getRequest().setAttribute("IDPRG", idprg);
    this.getRequest().setAttribute("IDCOM", idcom);
    this.getRequest().setAttribute("COMPUB", compub);
    this.getRequest().setAttribute("IDCFG", cenint);

    if (compub != null && compub.intValue() == 1){
      datiForm.setValue("W_INVCOM.COMSTATO", "3");
      //////
      //Comunicazioni pubbliche, valorizzata la data pubblicazione (COMDATAPUB.W_INVCOM) con la data corrente
      // Data pubblicazione
      datiForm.setValue("W_INVCOM.COMDATAPUB", new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()));
      //////
      //Aggiornamento della data ultimo aggiornamento
      PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
          this.getServletContext(), PgManagerEst1.class);

      String comkey1 = datiForm.getString("COMKEY1");
      pgManagerEst1.aggiornamentoDataAggiornamentoPortaleComunicazione(comkey1, idcom);
   }else
      datiForm.setValue("W_INVCOM.COMSTATO", "2");
      datiForm.setValue("W_INVCOM.IDCFG", cenint);
     
      if(idcomris != null && idprgris !=null && !"".equals(idprgris)){
        try {
          Date comdatalet = (Date) sqlManager.getObject("select COMDATLET from w_invcom where IDPRG = ? and IDCOM = ?", new Object[] { idprgris, idcomris });
          if(comdatalet == null){
            this.sqlManager.update("update W_INVCOM set COMDATLET = ? where IDPRG = ? and IDCOM=?", new Object[] {
                new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), idprgris, idcomris });
          }
        } catch (SQLException e) {
          logger.error("Si è verificato un errore nel salvataggio dei dati dalla comunicazione originale", e);
        }
      }  
    this.getRequest().setAttribute("RISULTATO", "INVIOESEGUITO");

  }

}
