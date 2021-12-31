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

public class GetRicercaMercatoArticoloAction extends Action {

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

    Long mericart_id = new Long(request.getParameter("mericart_id"));

    try {

      // 0 - Identificato articolo nell'archivio degli articoli
      // 1 - Tipo (valore numerico)
      // 2 - Descrizione
      // 3 - Descrizione tecnica
      // 4 - Colore
      // 5 - Modalita' di acquisto (valore numerico)
      // 6 - Quantita' in carrello
      // 7 - Unita' di misura del tempo di consegna (valore numerico)
      // 8 - dettaglio quantita1
      // 9 - dettaglio quantita2
      //10 - quantita1
      //11 - quantita2

      String selectMERICART = "select mericart.idartcat, " // 0
          + " meartcat.tipo, " // 1
          + " meartcat.descr, " // 2
          + " meartcat.descrtecn, " // 3
          + " meartcat.colore, " // 4
          + " meartcat.przunitper, " // 5
          + " mericart.quanti, " // 6
          + " meartcat.unimistempocons, " // 7
          + " mericart.desdet1, " // 8
          + " mericart.desdet2, " // 9
          + " mericart.quadet1, " // 10
          + " mericart.quadet2 " // 11
          + " from mericart, meartcat "
          + " where mericart.id = ? "
          + " and mericart.idartcat = meartcat.id ";

      List<?> datiMERICART = sqlManager.getListVector(selectMERICART, new Object[] { mericart_id });
      if (datiMERICART != null && datiMERICART.size() > 0) {
        for (int i = 0; i < datiMERICART.size(); i++) {
          Vector<Object> vect = (Vector) datiMERICART.get(i);

          // 12 - Tipo (descrizione) // 8 - Tipo (descrizione)
          Long meartcat_tipo = (Long) SqlManager.getValueFromVectorParam(datiMERICART.get(i), 1).getValue();
          vect.add(new JdbcParametro(JdbcParametro.TIPO_TESTO, this.getDescrizione("ME001", meartcat_tipo)));

          // 13 - Modalita' di acquisto (descrizione)// 9 - Modalita' di acquisto (descrizione)
          Long meartcat_przunitper = (Long) SqlManager.getValueFromVectorParam(datiMERICART.get(i), 5).getValue();
          vect.add(new JdbcParametro(JdbcParametro.TIPO_TESTO, this.getDescrizione("ME003", meartcat_przunitper)));

          // 14 - Unita' di misura del tempo di consegna (descrizione) // 10 - Unita' di misura del tempo di consegna (descrizione)
          Long meartcat_unimistempocons = (Long) SqlManager.getValueFromVectorParam(datiMERICART.get(i), 7).getValue();
          vect.add(new JdbcParametro(JdbcParametro.TIPO_TESTO, this.getDescrizione("ME004", meartcat_unimistempocons)));

        }

        jsonArray = JSONArray.fromObject(datiMERICART.toArray());

      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura delle informazioni relative all'articolo.", e);
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
