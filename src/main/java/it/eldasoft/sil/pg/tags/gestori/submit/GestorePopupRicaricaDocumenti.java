/*
 * Created on 13/10/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire la ricarica
 * della documentazione di una ditta
 *
 * @author Marcello Caminiti
 */
public class GestorePopupRicaricaDocumenti extends
    AbstractGestoreEntita {

  public GestorePopupRicaricaDocumenti() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
  }

  @Override
  public String getEntita() {
    return "TORN";
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
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    // lettura dei parametri di input
    String codgar = datiForm.getString("CODGAR");
    String ngara = datiForm.getString("NGARA");
    String codiceDitta = datiForm.getString("CODICEDITTA");
    String garaOffertaUnica = datiForm.getString("GARAOFFERTAUNICA");
    String tutteDitte = datiForm.getString("TUTTEDITTE");
    String delete="";

    //Per prima cosa si devono cancellare tutte le occorrenze di IMPRDOCG e W_DOCDIG relative alla ditta
    if("SI".equals(garaOffertaUnica)){

      try {
        if("1".equals(tutteDitte)){
          delete="delete from w_docdig where IDPRG in(select IMPRDOCG.IDPRG from IMPRDOCG where CODGAR = ? ) and IDDOCDIG in (select IMPRDOCG.IDDOCDG from IMPRDOCG where CODGAR = ? )";
          this.sqlManager.update(delete, new Object[] { codgar,codgar});
          this.sqlManager.update("delete from IMPRDOCG where CODGAR = ?", new Object[] { codgar});
        }else{
          delete="delete from w_docdig where IDPRG in(select IMPRDOCG.IDPRG from IMPRDOCG where CODGAR = ? and CODIMP = ?) and IDDOCDIG in (select IMPRDOCG.IDDOCDG from IMPRDOCG where CODGAR = ? and CODIMP = ?)";
          this.sqlManager.update(delete, new Object[] { codgar,codiceDitta,codgar,codiceDitta});
          this.sqlManager.update("delete from IMPRDOCG where CODGAR = ? and CODIMP = ?", new Object[] { codgar, codiceDitta});
        }
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore durante l'eliminazione dele righe delle tabelle IMPRDOCG e W_DOCDIG", null,  e);
      }
    }else{
      try {
        if("1".equals(tutteDitte)){
          delete="delete from w_docdig where IDPRG in(select IMPRDOCG.IDPRG from IMPRDOCG where CODGAR = ? and NGARA=? ) and IDDOCDIG in (select IMPRDOCG.IDDOCDG from IMPRDOCG where CODGAR = ? and NGARA=?)";
          this.sqlManager.update(delete, new Object[] { codgar, ngara,codgar, ngara});
          this.sqlManager.update("delete from IMPRDOCG where CODGAR = ? and NGARA=?", new Object[] { codgar, ngara});
        }else{
          delete="delete from w_docdig where IDPRG in(select IMPRDOCG.IDPRG from IMPRDOCG where CODGAR = ? and NGARA=? and CODIMP = ?) and IDDOCDIG in (select IMPRDOCG.IDDOCDG from IMPRDOCG where CODGAR = ? and NGARA=? and CODIMP = ?)";
          this.sqlManager.update(delete, new Object[] { codgar, ngara, codiceDitta,codgar,ngara,codiceDitta});
          this.sqlManager.update("delete from IMPRDOCG where CODGAR = ? and CODIMP = ? and NGARA=?", new Object[] { codgar, codiceDitta,ngara});
        }


      } catch (SQLException e) {
        throw new GestoreException(
            "Errore durante l'eliminazione dele righe delle tabelle IMPRDOCG e W_DOCDIG", null,  e);
      }
    }

    //Inserimento delle nuove occorrenze di IMPRDOCG
    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);

    if("1".equals(tutteDitte)){
      Object params[];
      String selectDitte="";
      if("SI".equals(garaOffertaUnica)){
        selectDitte="select dittao from ditg where codgar5=? and codgar5 = ngara5";
        params = new Object[1];
        params[0] = codgar;
      }else{
        selectDitte="select dittao from ditg where codgar5=? and ngara5=?";
        params = new Object[2];
        params[0] = codgar;
        params[1] = ngara;
      }
      try {
        List listaDitte= this.sqlManager.getListVector(selectDitte, params);
        if(listaDitte!=null && listaDitte.size()>0){
          for(int i=0;i<listaDitte.size();i++){
            String dittao=(String)SqlManager.getValueFromVectorParam(listaDitte.get(i), 0).getValue();
            pgManager.inserimentoDocumentazioneDitta(codgar, ngara, dittao);
          }
        }
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore durante la lettura dei codici delle ditte ", null,  e);
      }
    }else
      pgManager.inserimentoDocumentazioneDitta(codgar, ngara, codiceDitta);





    // setta l'operazione a completata, in modo da scatenare il reload della
    // pagina principale
    this.getRequest().setAttribute("ricaricaEseguita", "1");
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
