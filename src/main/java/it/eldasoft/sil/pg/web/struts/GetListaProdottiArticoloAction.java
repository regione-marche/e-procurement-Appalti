package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetListaProdottiArticoloAction extends Action {

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

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONArray jsonArray = new JSONArray();

    Long idricart = new Long(request.getParameter("idricart"));

    try {
      // 0 - Identificativo prodotto nella ricerca di mercato
      // 1 - Identificativo del prodotto
      // 2 - Codice prodotto
      // 3 - Nome commerciale
      // 4 - Codice operatore
      // 5 - Nome operatore
      // 6 - Unita' di misura prezzo (valore numerico)
      // 7 - Prezzo unitario riferito a unita' di misura prezzo
      // 8 - Quantita' di unita' di misura prezzo
      // 9 - Unita' di misura acquisto (valore numerico)
      // 10 - Prezzo unitario del prodotto riferito a unita' di misura acquisto
      // 11 - Lotto minimo per unita' di misura
      // 12 - Quantita'
      // 13 - Prezzo totale offerto
      // 14 - Aliquota IVA (valore numerico)
      // 15 - Tempo consegna
      // 16 - Marca

      // Per la selezione dei prodotti si utilizza V_MEPRODOTTI e non
      // direttamente la tabella MEISCRIZPROD

      String selectMERICPROD = "select mericprod.id, " // 0
          + " mericprod.idprod, " // 1
          + " v_meprodotti.codoe, " // 2
          + " v_meprodotti.nome, " // 3
          + " mericprod.codimp, " // 4
          + " impr.nomest, " // 5
          + " meartcat.unimisprz, " // 6
          + " v_meprodotti.przunit, " // 7
          + " v_meprodotti.qunimisprz, " // 8
          + " meartcat.unimisacq, " // 9
          + " v_meprodotti.przunitprod, " // 10
          + " v_meprodotti.qunimisacq, " // 11
          + " mericprod.quanti, " // 12
          + " mericprod.preoff, " // 13
          + " mericprod.perciva," // 14
          + " v_meprodotti.tempocons," // 15
          + " v_meprodotti.marcaprodut" // 16
          + " from mericprod, v_meprodotti, meartcat, impr"
          + " where mericprod.idprod = v_meprodotti.id "
          + " and v_meprodotti.idartcat = meartcat.id "
          + " and mericprod.codimp = impr.codimp "
          + " and mericprod.idricart = ? "
          + " order by mericprod.preoff, mericprod.quanti, impr.nomest ";

      List<?> datiMERICPROD = sqlManager.getListVector(selectMERICPROD, new Object[] { idricart });
      if (datiMERICPROD != null && datiMERICPROD.size() > 0) {
        for (int i = 0; i < datiMERICPROD.size(); i++) {
          Vector<Object> vect = (Vector) datiMERICPROD.get(i);

          // 17 - Unita' di misura prezzo (descrizione)
          Long meartcat_unimisprz = (Long) SqlManager.getValueFromVectorParam(datiMERICPROD.get(i), 6).getValue();
          vect.add(new JdbcParametro(JdbcParametro.TIPO_TESTO, this.getDescrizione("ME007", meartcat_unimisprz)));

          // 18 - Unita' di misura acquisto (descrizione)
          Long meartcat_unimisacq = (Long) SqlManager.getValueFromVectorParam(datiMERICPROD.get(i), 9).getValue();
          vect.add(new JdbcParametro(JdbcParametro.TIPO_TESTO, this.getDescrizione("ME007", meartcat_unimisacq)));

          // 19 - Aliquota IVA (descrizione)
          Long mericprod_perciva = (Long) SqlManager.getValueFromVectorParam(datiMERICPROD.get(i), 14).getValue();
          vect.add(new JdbcParametro(JdbcParametro.TIPO_TESTO, this.getDescrizione("G_055", mericprod_perciva)));

        }

        jsonArray = JSONArray.fromObject(datiMERICPROD.toArray());

      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura delle informazioni relativi ai prodotti dell'articolo", e);
    }
    out.println(jsonArray);
    out.flush();

    return null;

  }

  /**
   * Ricava la descrizione.
   *
   * @param tab1cod
   * @param tab1tip
   * @return
   * @throws Exception
   */
  private String getDescrizione(String tab1cod, Long tab1tip) throws Exception {
    String descrizione = null;
    if (tab1tip != null) {
      descrizione = (String) sqlManager.getObject("select tab1desc from tab1 where tab1cod = ? and tab1tip = ?", new Object[] { tab1cod,
          tab1tip });
    }
    return descrizione;
  }

}
