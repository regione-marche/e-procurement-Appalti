/*
 * Created on 28/04/11
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
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard che effettua la copia dei criteri
 * dal lotto sorgente ai lotti selezionati nella popup
 * gare-popup-copia-criteriValutazione.jsp
 *
 * @author Marcello Caminiti
 */
public class GestorePopupCopiaAttributiAggiuntiviXDPRE extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "GARE";
  }

  public GestorePopupCopiaAttributiAggiuntiviXDPRE() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupCopiaAttributiAggiuntiviXDPRE(boolean isGestoreStandard) {
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

    String  lottoSorgente = UtilityStruts.getParametroString(this.getRequest(),"lottoSorgente");
    String  codiceGara = UtilityStruts.getParametroString(this.getRequest(),"codiceGara");
    String[] listaLottiSelezionati = this.getRequest().getParameterValues("keys");

    this.getRequest().setAttribute("codiceGara", codiceGara);
    this.getRequest().setAttribute("lottoSorgente", lottoSorgente);

    try {

      //Lista degli attributi aggiuntivi da copiare
      List listaAttributi = sqlManager.getListVector(
          "select campo, formato, numord, obbligatorio from garconfdati where ngara=? and entita=?",new Object[] { lottoSorgente, "XDPRE" });

      for (int i = 0; i < listaLottiSelezionati.length; i++) {
        int pos = listaLottiSelezionati[i].indexOf(':') + 1;
        String codiceLottoSelezionato = listaLottiSelezionati[i].substring(pos);

        //Cancellazione degli eventuali attributi associati al lotto
        this.getSqlManager().update(
            "delete from garconfdati where NGARA = ? and entita=?",
            new Object[] { codiceLottoSelezionato, "XDPRE" });

        if (listaAttributi != null && listaAttributi.size() > 0) {
          String campo=null;
          Long formato=null;
          Long numord=null;
          String obbligatorio=null;
          Long id=null;

          String insert="insert into garconfdati (ID,NGARA,ENTITA,CAMPO,FORMATO,NUMORD,OBBLIGATORIO) values(?,?,?,?,?,?,?)";
          for (Iterator iterator = listaAttributi.iterator(); iterator.hasNext();) {
            Vector attributo = (Vector) iterator.next();
            id = new Long(genChiaviManager.getNextId("GARCONFDATI"));

            campo = null;
            if(attributo.get(0)!=null)
              campo = ((JdbcParametro) attributo.get(0)).stringValue();

            formato = null;
            if(attributo.get(1)!=null)
              formato = ((JdbcParametro) attributo.get(1)).longValue();

            numord = null;
            if(attributo.get(2)!=null)
              numord = ((JdbcParametro) attributo.get(2)).longValue();

            obbligatorio=null;
            if(attributo.get(3)!= null)
              obbligatorio = ((JdbcParametro) attributo.get(3)).stringValue();


            this.getSqlManager().update(insert, new Object[] { id,codiceLottoSelezionato, "XDPRE", campo,
                formato, numord, obbligatorio});

          }
        }
      }

      this.getRequest().setAttribute("RISULTATO", "COPIAESEGUITA");
    }  catch (SQLException e) {
      this.getRequest().setAttribute("RISULTATO", "ERRORI");
      throw new GestoreException("Errore nella copia degli attributi aggiuntivi in XDPRE", null, e);
    }
  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

   @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

}