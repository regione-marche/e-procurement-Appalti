/*
 * Created on 06/09/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard che si occupa di preparare i dati prelevati dalle
 * viste V_DITTE_ELECAT e V_DITTE_ELESUM per potere eseguire l'inserimento
 * delle ditte in gara sfruttando il gestore GestoreFasiRicezione
 *
 * @author Marcello Caminiti
 */
public class GestorePopupSelDitteRilancio extends GestoreFasiRicezione {

  static Logger               logger         = Logger.getLogger(GestorePopupSelDitteRilancio.class);

  @Override
  public String getEntita() {
    return "DITG";
  }

  public GestorePopupSelDitteRilancio() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupSelDitteRilancio(boolean isGestoreStandard) {
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
  public void preInsert(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {


  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer dataColumnContainer)
      throws GestoreException {

    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);


    //Anche se la pagina è aperta in modifica, vi è la necessità di eseguire le operazioni
    //legate all'inserimento di una ditta, operazioni si trovano nel preInsert GestoreFasiRicezione,
    //quindi anche se sono nel preUpdate si richiama super.preInsert.
    String[] listaDitteSelezionate = this.getRequest().getParameterValues("keys");
    String ngara=UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String codgar="$" + ngara;
    String dittao=null;

    try{
      int numeroOperatoriDaSelezionare=listaDitteSelezionate.length;
      String select="select nomimp from impr where codimp = ?";
      String ragsoc = null;
      for (int i = 0; i < numeroOperatoriDaSelezionare; i++) {
        dittao = listaDitteSelezionate[i];
                try {
          ragsoc = (String) sqlManager.getObject(
              select, new Object[] { dittao });

        } catch (SQLException e) {
          throw new GestoreException("Errore nella lettura della ragione sociale della ditta ", null, e);
        }

        //campi chiave di ditg
        Vector elencoCampi = new Vector();
        elencoCampi.add(new DataColumn("DITG.NGARA5",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
        elencoCampi.add(new DataColumn("DITG.CODGAR5",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar)));
        elencoCampi.add(new DataColumn("DITG.DITTAO",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, dittao)));

        //campi che si devono inserire perchè adoperati nel gestore
        //GestoreFasiRicezione
        elencoCampi.add(new DataColumn("DITG.NOMIMO",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, ragsoc)));
        elencoCampi.add(new DataColumn("DITG.NPROGG",
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
        elencoCampi.add(new DataColumn("DITG.NUMORDPL",
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
        elencoCampi.add(new DataColumn("GARARILANCIO",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, "1")));

        DataColumnContainer containerDITG = new DataColumnContainer(elencoCampi);

        super.preInsert(status, containerDITG);

      }

      //best case
      this.getRequest().setAttribute("RISULTATO", "OK");
    }catch(GestoreException e){
      this.getRequest().setAttribute("RISULTATO", "NOK");
      throw e;

    }


  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

}