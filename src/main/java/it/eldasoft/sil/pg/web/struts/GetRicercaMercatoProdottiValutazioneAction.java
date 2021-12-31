package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetRicercaMercatoProdottiValutazioneAction extends Action {

  /**
   * Manager per la gestione delle interrogazioni di database.
   */
  private SqlManager sqlManager;

  /**
   * @param sqlManager
   *        the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);
    
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONArray jsonCarrelloArray = new JSONArray();

    Long meric_id = new Long(request.getParameter("meric_id"));

    try {

      String selectMERICART = "select mericart.id, " // 0
          + " mericart.idartcat, " // 1
          + " mericart.quanti, " // 2
          + " meartcat.descr, " // 3
          + " meartcat.colore, " // 4
          + " mericart.ngara " // 5
          + " from mericart, meartcat "
          + " where mericart.idric = ? "
          + " and mericart.idartcat = meartcat.id "
          + " order by meartcat.descr ";

      String selectMERICPROD = "select mericprod.id, " // 0
          + " mericprod.idprod, " // 1
          + " mericprod.codimp, " // 2
          + " mericprod.preoff, " // 3
          + " mericprod.acquista, " // 4
          + " mericprod.perciva " // 5
          + " from mericprod "
          + " where mericprod.idricart = ? "
          + " order by mericprod.preoff, mericprod.codimp";

      List<?> datiMEARICART = sqlManager.getListVector(selectMERICART, new Object[] { meric_id });

      if (datiMEARICART != null && datiMEARICART.size() > 0) {
        for (int iART = 0; iART < datiMEARICART.size(); iART++) {
          Long mericart_id = (Long) SqlManager.getValueFromVectorParam(datiMEARICART.get(iART), 0).getValue();
          Long mericart_idartcat = (Long) SqlManager.getValueFromVectorParam(datiMEARICART.get(iART), 1).getValue();
          String meartcat_descr = (String) SqlManager.getValueFromVectorParam(datiMEARICART.get(iART), 3).getValue();
          String meartcat_colore = (String) SqlManager.getValueFromVectorParam(datiMEARICART.get(iART), 4).getValue();
          String meartcat_ngara = (String) SqlManager.getValueFromVectorParam(datiMEARICART.get(iART), 5).getValue();

          Long mericprod_id_acquistato = null;
          String mericprod_codimp_acquistato = null;
          String impr_nomest_acquistato = null;
          Double mericprod_prezzo_acquistato = null;
          String descrizione_perciva_acquistato = null;

          Double mericprod_prezzo_migliore = null;

          List<?> datiMERICPROD = sqlManager.getListVector(selectMERICPROD, new Object[] { mericart_id });
          List<Object> prodotti = new Vector<Object>();

          if (datiMERICPROD != null && datiMERICPROD.size() > 0) {
            for (int iPROD = 0; iPROD < datiMERICPROD.size(); iPROD++) {
              Long mericprod_id = (Long) SqlManager.getValueFromVectorParam(datiMERICPROD.get(iPROD), 0).getValue();
              Long mericprod_idprod = (Long) SqlManager.getValueFromVectorParam(datiMERICPROD.get(iPROD), 1).getValue();

              String mericprod_codimp = (String) SqlManager.getValueFromVectorParam(datiMERICPROD.get(iPROD), 2).getValue();
              String impr_nomest = null;
              if (mericprod_codimp != null) {
                impr_nomest = (String) sqlManager.getObject("select nomest from impr where codimp = ?", new Object[] { mericprod_codimp });
              }
              Double mericprod_prezzo = (Double) SqlManager.getValueFromVectorParam(datiMERICPROD.get(iPROD), 3).getValue();
              String mericprod_acquista = (String) SqlManager.getValueFromVectorParam(datiMERICPROD.get(iPROD), 4).getValue();

              // Gestione percentuale IVA
              Long mericprod_perciva = (Long) SqlManager.getValueFromVectorParam(datiMERICPROD.get(iPROD), 5).getValue();
              String descrizione_perciva = null;
              if (mericprod_perciva != null) {
                descrizione_perciva = (String) sqlManager.getObject("select tab1desc from tab1 where tab1cod = ? and tab1tip = ?",
                    new Object[] { "G_055", mericprod_perciva });
              }

              // PRODOTTI
              // 4.0 - Identificativo prodotto ricerca di mercato (MERICPROD.ID)
              // 4.1 - Identificativo articolo ricerca di mercato
              // (MERICPROD.IDRICART)
              // 4.2 - Identificativo prodotto (MERICPROD.IDPROD)
              // 4.3 - Codice impresa offerente (MERICPROD.CODIMP)
              // 4.4 - Denominazione impresa offerente (IMPR.NOMEST)
              // 4.5 - Prezzo offerto (MERICPROD.PREZZO)
              // 4.6 - Prodotto acquistato ? (MERICPROD.ACQUISTA)
              // 4.7 - Percentuali IVA (TAB1DESC del tabellato G_055 con TAB1TIP
              // = MERICPROD.PERCIVA)
              Object[] rowProdotto = new Object[8];
              rowProdotto[0] = mericprod_id;
              rowProdotto[1] = mericart_id;
              rowProdotto[2] = mericprod_idprod;
              rowProdotto[3] = mericprod_codimp;
              rowProdotto[4] = impr_nomest;
              rowProdotto[5] = mericprod_prezzo;
              rowProdotto[6] = mericprod_acquista;
              rowProdotto[7] = descrizione_perciva;
              prodotti.add(rowProdotto);

              // Miglior prezzo
              if (iPROD == 0) mericprod_prezzo_migliore = mericprod_prezzo;

              // Prodotto acquistato
              if (mericprod_acquista != null && mericprod_acquista.equals("1")) {
                mericprod_id_acquistato = mericprod_id;
                mericprod_codimp_acquistato = mericprod_codimp;
                impr_nomest_acquistato = impr_nomest;
                mericprod_prezzo_acquistato = mericprod_prezzo;
                descrizione_perciva_acquistato = descrizione_perciva;
              }

            }
          }

          // Differenza tra prodotto acquistato e miglior prezzo
          Double mericprod_prezzo_differenza = null;
          if (mericprod_prezzo_migliore != null && mericprod_prezzo_acquistato != null) {
            mericprod_prezzo_differenza = new Double(mericprod_prezzo_acquistato.doubleValue() - mericprod_prezzo_migliore.doubleValue());
          }

          // ARTICOLI ED INFORMAZIONI GENERALI
          // 0 - Identificativo articolo ricerca di mercato (MERICART.ID)
          // 1 - Identificativo articolo (MEARTCAT.ID - MERICART.IDARTICOLO)
          // 2 - Descrizione articolo (MEARTCAT.DESCR)
          // 3 - Colore articolo (MEARTCAT.COLORE)
          // 4 - Lista dei prodotti offerti (vedi struttura PRODOTTI)
          // 5 - Identificativo del prodotto acquistato (MERICPROD.ID per
          // MERICPROD.ACQUISTA = '1')
          // 6 - Codice impresa fornitore (IMPR.CODIMP associata a
          // MERICPROD.CODIMP per MERICPROD.ACQUISTA = '1')
          // 7 - Nome dell'impresa selezionata per l'acquisto (IMPR.NOMEST
          // associata a MERICPROD.CODIMP per MERICPROD.ACQUISTA = '1')
          // 8 - Prezzo selezionato per l'acquisto (MERICPROD.PREZZO per
          // MERICPROD.ACQUISTA = '1')
          // 9 - Differenza tra il prezzo migliore ed il prezzo selezionato per
          // l'acquisto
          // 10 - Differenza calcolata sul prezzo
          // 11 - Descrizione IVA
          // 12 - Numero gara dell'articolo nell'ordine
          Object[] rowArticolo = new Object[13];
          rowArticolo[0] = mericart_id;
          rowArticolo[1] = mericart_idartcat;
          rowArticolo[2] = meartcat_descr;
          rowArticolo[3] = meartcat_colore;
          rowArticolo[4] = prodotti;
          rowArticolo[5] = mericprod_id_acquistato;
          rowArticolo[6] = mericprod_codimp_acquistato;
          rowArticolo[7] = impr_nomest_acquistato;
          rowArticolo[8] = mericprod_prezzo_acquistato;
          rowArticolo[9] = mericprod_prezzo_migliore;
          rowArticolo[10] = mericprod_prezzo_differenza;
          rowArticolo[11] = descrizione_perciva_acquistato;
          rowArticolo[12] = meartcat_ngara;

          jsonCarrelloArray.add(rowArticolo);

        }
      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura del carrello", e);
    }

    out.println(jsonCarrelloArray);
    out.flush();

    return null;

  }

}
