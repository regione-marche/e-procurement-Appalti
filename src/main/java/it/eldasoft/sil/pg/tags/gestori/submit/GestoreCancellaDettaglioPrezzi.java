/*
 * Created on 25/set/09
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

import java.sql.SQLException;
import java.util.List;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per cancellare le
 * occorrenze di DPRE relative ad una ditta e le occorrenze di GCAP introdotte
 * dalla stessa ditta
 * 
 * @author Marcello.Caminiti
 */
public class GestoreCancellaDettaglioPrezzi extends AbstractGestoreEntita {

  public GestoreCancellaDettaglioPrezzi() {
    super(false);
  }

  public String getEntita() {
    return "DPRE";
  }

  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  // Vengono eliminate le occorrenze di DPRE e GCAP inserite da una ditta
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    // lettura dei parametri di input
    String ngara = datiForm.getString("NGARA");
    String ditta = datiForm.getString("DITTAO");
    String codgar = datiForm.getString("CODGARA");
    String garaOffertaUnica = datiForm.getString("GARA_OFFERTA_SINGOLA");

    try {
      if ("true".equals(garaOffertaUnica)) {
        // nel caso di gare divise a lotti con offerta unica si devono andare a
        // cancellare le
        // occorrenze di GCAP e DPRE dei lotti
        String select = "select ngara from gare where codgar1 = ? and (genere <> 3 or genere is null)";

        List ret = sqlManager.getListVector(select, new Object[] { codgar });

        if (ret != null && ret.size() > 0) {
          for (int i = 0; i < ret.size(); i++) {
            String ngaraLotto = SqlManager.getValueFromVectorParam(ret.get(i),
                0).stringValue();
            if (ngaraLotto != null && !"".equals(ngaraLotto)) {
              // Cancellazione estensione DPRE_SAN
              this.getSqlManager().update(
                  "delete from DPRE_SAN where NGARA = ? and DITTAO =?",
                  new Object[] { ngaraLotto, ditta });

              this.getSqlManager().update(
                  "delete from DPRE where NGARA = ? and DITTAO =?",
                  new Object[] { ngaraLotto, ditta });

              // Cancellazione della tabella di estensione GCAP_EST
              this.getSqlManager().update(
                  "delete from GCAP_EST where NGARA  = ? and CONTAF in (select contaf from gcap where ngara=? and DITTAO =?)",
                  new Object[] { ngaraLotto, ngaraLotto, ditta });
              
              // Cancellazione GCAP_SAN
              this.getSqlManager().update(
                  "delete from GCAP_SAN where NGARA  = ? and CONTAF in (select contaf from gcap where ngara=? and DITTAO =?)",
                  new Object[] { ngaraLotto, ngaraLotto, ditta });

              this.getSqlManager().update(
                  "delete from GCAP where NGARA = ? and DITTAO =?",
                  new Object[] { ngaraLotto, ditta });

            }

          }
        }

      } else {
        // Cancellazione estensione DPRE_SAN
        this.getSqlManager().update(
            "delete from DPRE_SAN where NGARA = ? and DITTAO =?",
            new Object[] { ngara, ditta });
        
        this.getSqlManager().update(
            "delete from DPRE where NGARA = ? and DITTAO =?",
            new Object[] { ngara, ditta });

        // Cancellazione della tabella di estensione GCAP_EST
        this.getSqlManager().update(
            "delete from GCAP_EST where NGARA  = ? and CONTAF in (select contaf from gcap where ngara=? and DITTAO =?)",
            new Object[] { ngara, ngara, ditta });
        
        // Cancellazione estensione GCAP_SAN
        this.getSqlManager().update(
            "delete from GCAP_SAN where NGARA  = ? and CONTAF in (select contaf from gcap where ngara=? and DITTAO =?)",
            new Object[] { ngara, ngara, ditta });
        
        this.getSqlManager().update(
            "delete from GCAP where NGARA = ? and DITTAO =?",
            new Object[] { ngara, ditta });

      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante la cancellazione del dettaglio prezzi", "null", e);
    }

    // setta l'operazione a completata, in modo da scatenare il reload della
    // pagina principale
    this.getRequest().setAttribute("cancellazioneEffettuata", "1");
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
