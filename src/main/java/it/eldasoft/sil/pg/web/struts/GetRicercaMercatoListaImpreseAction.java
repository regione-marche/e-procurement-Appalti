package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetRicercaMercatoListaImpreseAction extends Action {

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

    JSONArray jsonArray = new JSONArray();

    Long meric_id = new Long(request.getParameter("meric_id"));

    try {

      // 0 - Codice dell'impresa
      // 1 - Nome dell'impresa
      // 2 - Conteggio numero di articoli offerti dall'impresa
      // 3 - Prezzo offerto totale

      String selectIMPR = "select distinct mericprod.codimp, impr.nomest "
          + " from mericprod, impr"
          + " where mericprod.codimp = impr.codimp "
          + " and mericprod.idricart in (select id from mericart where idric = ?)";

      List<?> datiIMPR = this.sqlManager.getListVector(selectIMPR, new Object[] { meric_id });

      if (datiIMPR != null && datiIMPR.size() > 0) {
        for (int iIMPR = 0; iIMPR < datiIMPR.size(); iIMPR++) {
          String codimp = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(iIMPR), 0).getValue();
          String nomest = (String) SqlManager.getValueFromVectorParam(datiIMPR.get(iIMPR), 1).getValue();

          Object[] row = new Object[4];
          row[0] = codimp;
          row[1] = nomest;

          // Conteggio del numero di articoli per i quali e' stato inserito
          // almeno un prodotto
          String selectMERICART = "select count(*) from mericart where idric = ? and id in (select idricart from mericprod where codimp = ?)";
          Long conteggioMERICART = (Long) this.sqlManager.getObject(selectMERICART, new Object[] { meric_id, codimp });
          row[2] = conteggioMERICART;

          // Somma degli importi per i prodotti offerti: se per uno stesso
          // articolo e' stato offerto piu' di un
          // prodotto si deve considerare, nella somma, il prodotto con il
          // prezzo inferiore.
          double prezzoOffertoTotale = 0;
          String selectMERICPROD = "select min(mericprod.preoff), mericprod.idricart "
              + " from mericprod "
              + " where mericprod.idricart in (select id from mericart where idric = ?) "
              + " and mericprod.codimp = ? group by mericprod.idricart";
          List<?> datiMERICPROD = this.sqlManager.getListVector(selectMERICPROD, new Object[] { meric_id, codimp });
          if (datiMERICPROD != null && datiMERICPROD.size() > 0) {
            for (int iMERICPROD = 0; iMERICPROD < datiMERICPROD.size(); iMERICPROD++) {
              Object objPreoff = SqlManager.getValueFromVectorParam(datiMERICPROD.get(iMERICPROD), 0).getValue();
              Double preoff = null;
              if (objPreoff instanceof Double) {
                preoff = (Double) objPreoff;
              } else if (objPreoff instanceof Long) {
                preoff = new Double(((Long) objPreoff).longValue());
              }
              if (preoff != null) prezzoOffertoTotale += preoff.doubleValue();
            }
          }
          row[3] = new Double(prezzoOffertoTotale);
          jsonArray.add(row);
        }
      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura della lista delle imprese offerenti.", e);
    }

    // Ordinamento
    List<JSONArray> jsonValues = new ArrayList<JSONArray>();
    
    for (int i = 0; i < jsonArray.size(); i++) {
      jsonValues.add(jsonArray.getJSONArray(i));
    }
    
    Collections.sort(jsonValues, new Comparator<JSONArray>() {
      public int compare(JSONArray a, JSONArray b) {
        
        Long numeroArticoliA = (Long) a.getLong(2);
        Long numeroArticoliB = (Long) b.getLong(2);
        if (numeroArticoliA == null) numeroArticoliA = new Long(0);
        if (numeroArticoliB == null) numeroArticoliB = new Long(0);
        
        Double importoTotaleA = (Double) a.getDouble(3);
        Double importoTotaleB = (Double) b.getDouble(3);
        if (importoTotaleA == null) importoTotaleA = new Double(0);
        if (importoTotaleB == null) importoTotaleB = new Double(0);
       
        int ret = 0;
        if (numeroArticoliA.longValue() > numeroArticoliB.longValue()) {
          ret = -1; // Decrescente sul numero di articoli
        } else if (numeroArticoliA.longValue() < numeroArticoliB.longValue()) {
          ret = 1; // Crescente sul numero di articoli
        } else {
          // Parimerito sul numero di articoli con prodotti offerti
          if (importoTotaleA.doubleValue() > importoTotaleB.doubleValue()) {
            ret = 1; // Crescente sull'importo totale
          } else if (importoTotaleA.doubleValue() < importoTotaleB.doubleValue()){
            ret = -1; // Decrescente sull'importo totale
          }
        }
        return ret;
      }
    });
    
    out.println(jsonValues);
    out.flush();

    return null;

  }
}
