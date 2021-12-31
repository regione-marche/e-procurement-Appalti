package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author Stefano.Cestaro
 *
 */
public class GetArticoliAlberoAction extends Action {

  private static Long STATO_ARTICOLO_ATTIVO = new Long(2);

  /**
   * Manager per la gestione delle interrogazioni di database.
   */
  private SqlManager  sqlManager;

  /**
   * @param sqlManager
   *        the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * Restituisce JSONArray strutturato nel seguente modo:
   *
   * <ul>
   * <li>0 - Livello (-2, -1, 0, 1 ...)</li>
   * <li>1 - Tipo di categoria (CAIS.TIPLAVG)</li>
   * <li>2 - Eventuale titolo per raggruppamento categorie (CAIS.TITCAT)</li>
   * <li>3 - Codice categoria (CAIS.CAISIM)</li>
   * <li>4 - Codice categoria livello 1</li>
   * <li>5 - Codice categoria livello 2</li>
   * <li>6 - Codice categoria livello 3</li>
   * <li>7 - Codice categoria livello 4</li>
   * <li>8 - Descrizione del tipo categoria, del titolo o della categoria</li>
   * <li>9 - Numero ARTICOLI annidati ASSOCIATI (default 0)</li>
   * <li>10 - Identificativo dell'articolo (MEARTCAT.ID)</li>
   * <li>11 - Articolo inserito nel carrello articoli</li>
   * <li>12 - Quantita' dell'articolo nel carrello articoli</li>
   * <li>13 - Importo minimo ordine</li>
   * </ul>
   */
  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String operation = request.getParameter("operation");
    String ngara = request.getParameter("ngara");

    JSONArray jsonArray = new JSONArray();

    if ("load".equals(operation)) {
      // Livello del nodo
      Long livello = null;
      if (request.getParameter("livello") != "") livello = new Long(request.getParameter("livello"));

      // Tipologia categorie
      Long tiplavg = null;
      if (request.getParameter("tiplavg") != "") tiplavg = new Long(request.getParameter("tiplavg"));

      // Ordinamento
      boolean orderByCodice = true;
      String tab1desc = (String) sqlManager.getObject("select tab1desc from tab1 where tab1cod = ? and tab1tip = ?", new Object[] {
          "G_057", new Long(1) });
      if (tab1desc != null && "2".equals(tab1desc.trim().substring(0, 1))) {
        orderByCodice = false;
      }

      // Identificativo ricerca di mercato
      Long meric_id = null;
      if (request.getParameter("meric_id") != "") meric_id = new Long(request.getParameter("meric_id"));

      String titcat = request.getParameter("titcat");
      String caisim = request.getParameter("caisim");
      String caisim_livello1 = request.getParameter("caisim_livello1");
      String caisim_livello2 = request.getParameter("caisim_livello2");
      String caisim_livello3 = request.getParameter("caisim_livello3");
      String caisim_livello4 = request.getParameter("caisim_livello4");

      switch (livello.intValue()) {
      case -2:
        this.popolaRoot(ngara, jsonArray);
        break;

      case -1:
        this.popolaTitoli(tiplavg, ngara, jsonArray);
        this.popolaLivello0(tiplavg, null, ngara, orderByCodice, jsonArray);
        break;

      case 0:
        this.popolaLivello0(tiplavg, titcat, ngara, orderByCodice, jsonArray);
        break;

      case 1:
        this.popolaArticoli(caisim, ngara, meric_id, jsonArray);
        this.popolaLivello1(tiplavg, titcat, caisim_livello1, ngara, orderByCodice, jsonArray);
        break;

      case 2:
        this.popolaArticoli(caisim, ngara, meric_id, jsonArray);
        this.popolaLivello2(tiplavg, titcat, caisim_livello1, caisim_livello2, ngara, orderByCodice, jsonArray);
        break;

      case 3:
        this.popolaArticoli(caisim, ngara, meric_id, jsonArray);
        this.popolaLivello3(tiplavg, titcat, caisim_livello1, caisim_livello2, caisim_livello3, ngara, orderByCodice, jsonArray);
        break;

      case 4:
        this.popolaArticoli(caisim, ngara, meric_id, jsonArray);
        this.popolaLivello4(tiplavg, titcat, caisim_livello1, caisim_livello2, caisim_livello3, caisim_livello4, ngara, orderByCodice,
            jsonArray);
        break;

      case 5:
      case 50:
        this.popolaArticoli(caisim, ngara, meric_id, jsonArray);

      default:
        break;
      }

    } else if ("search".equals(operation)) {
      String textsearch = request.getParameter("textsearch");
      this.searchCategoria(ngara, textsearch, jsonArray);
    }

    out.println(jsonArray);
    out.flush();

    return null;
  }

  /**
   * Restituisce il percorso con i nodi da aprire automaticamente in funzione
   * della ricerca. Questo metodo permette la ricerca in OR su vari termini di
   * ricerca separati da "spazio".
   *
   * @param ngara
   * @param textsearch
   * @param jsonArray
   * @throws SQLException
   */
  private void searchCategoria(String ngara, String textsearch_any, JSONArray jsonArray) throws SQLException {

    String[] words = textsearch_any.split(" ");

    for (int ia = 0; ia < words.length; ia++) {
      String textsearch = words[ia];
      if (textsearch != null && textsearch.trim().length() >= 3 && !"".equals(textsearch.trim())) {

        // Caratteri di escape
        textsearch = StringUtils.replace(textsearch, "_", "#_");
        textsearch = StringUtils.replace(textsearch, "%", "#%");
        textsearch = "%" + textsearch.toUpperCase() + "%";

        String selectCAIS = "select cais.tiplavg, cais.titcat, cais.codliv1, cais.codliv2, cais.codliv3, cais.codliv4, cais.caisim "
            + " from cais "
            + " where (upper(cais.caisim) like ? escape '#' "
            + " or upper(cais.descat) like ? escape '#' "
            + " or exists (select tab5.tab5tip from tab5 where tab5.tab5cod = 'G_j05' and cais.titcat = tab5.tab5tip and upper(tab5.tab5desc) like ? escape '#')) "
            + " or exists (select meartcat.id from meartcat, opes "
            + " where cais.caisim = opes.catoff and opes.ngara3 = ? "
            + " and meartcat.ngara = opes.ngara3 "
            + " and meartcat.nopega = opes.nopega "
            + " and meartcat.stato = ? "
            + " and ((upper(meartcat.cod) like ? escape '#') "
            + " or (upper(meartcat.descr) like ? escape '#') or (upper(meartcat.colore) like ? escape '#')))";

        List<?> datiCAIS = this.sqlManager.getListVector(selectCAIS, new Object[] { textsearch, textsearch, textsearch, ngara,
            STATO_ARTICOLO_ATTIVO, textsearch, textsearch, textsearch });
        if (datiCAIS != null && datiCAIS.size() > 0) {
          for (int i = 0; i < datiCAIS.size(); i++) {
            Long tiplavg = (Long) SqlManager.getValueFromVectorParam(datiCAIS.get(i), 0).getValue();
            String titcat = (String) SqlManager.getValueFromVectorParam(datiCAIS.get(i), 1).getValue();
            String codliv1 = (String) SqlManager.getValueFromVectorParam(datiCAIS.get(i), 2).getValue();
            String codliv2 = (String) SqlManager.getValueFromVectorParam(datiCAIS.get(i), 3).getValue();
            String codliv3 = (String) SqlManager.getValueFromVectorParam(datiCAIS.get(i), 4).getValue();
            String codliv4 = (String) SqlManager.getValueFromVectorParam(datiCAIS.get(i), 5).getValue();
            String caisim = (String) SqlManager.getValueFromVectorParam(datiCAIS.get(i), 6).getValue();

            if (tiplavg != null && caisim != null) {
              jsonArray.add(new Object[] { tiplavg.toString(), titcat, caisim });

              jsonArray.add(new Object[] { tiplavg.toString(), "", "" });
              if (titcat != null) {
                jsonArray.add(new Object[] { tiplavg.toString(), titcat, "" });
              }
              if (codliv1 != null) {
                jsonArray.add(new Object[] { tiplavg.toString(), titcat, codliv1 });
              }
              if (codliv2 != null) {
                jsonArray.add(new Object[] { tiplavg.toString(), titcat, codliv2 });
              }
              if (codliv3 != null) {
                jsonArray.add(new Object[] { tiplavg.toString(), titcat, codliv3 });
              }
              if (codliv4 != null) {
                jsonArray.add(new Object[] { tiplavg.toString(), titcat, codliv4 });
              }
            }
          }
        }
      }
    }
  }

  /**
   * Popola l'elemento root dell'albero (corrisponde al tabellato con la lista
   * delle tipologie di categorie).
   *
   * @param ngara
   * @param jsonArray
   * @throws SQLException
   */
  private void popolaRoot(String ngara, JSONArray jsonArray) throws SQLException {

    String selectCategorie = "select tab1tip, tab1desc from tab1 where tab1cod = ? order by tab1nord, tab1tip";
    List<?> datiCategorie = this.sqlManager.getListVector(selectCategorie, new Object[] { "G_038" });
    if (datiCategorie != null && datiCategorie.size() > 0) {
      for (int i = 0; i < datiCategorie.size(); i++) {
        Long tiplavg = (Long) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 0).getValue();
        String titcat = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 1).getValue();

        int numeroTitoliAssociati = this.numeroTitoliAssociati(tiplavg, ngara);

        if (numeroTitoliAssociati > 0) {
          int numArticoliAssociati = this.numeroArticoliDaTitolo(tiplavg, ngara);
          numArticoliAssociati += this.numeroArticoliDaLivello0(tiplavg, null, ngara);
          if (numArticoliAssociati > 0) {
            Object[] row = new Object[13];
            row[0] = "-1";
            row[1] = tiplavg;
            row[2] = "";
            row[3] = "";
            row[4] = "";
            row[5] = "";
            row[6] = "";
            row[7] = "";
            row[8] = titcat;
            row[9] = numArticoliAssociati;
            row[10] = null;
            row[11] = false;
            row[12] = null;
            jsonArray.add(row);
          }

        } else {
          int numArticoliAssociati = this.numeroArticoliDaLivello0(tiplavg, null, ngara);
          if (numArticoliAssociati > 0) {
            Object[] row = new Object[13];
            row[0] = "0";
            row[1] = tiplavg;
            row[2] = "";
            row[3] = "";
            row[4] = "";
            row[5] = "";
            row[6] = "";
            row[7] = "";
            row[8] = titcat;
            row[9] = numArticoliAssociati;
            row[10] = null;
            row[11] = false;
            row[12] = null;
            jsonArray.add(row);
          }
        }
      }
    }
  }

  /**
   * Popola il livello dei Titoli. Non sempre una particolare tipologia
   * (TIPLAVG) ha dei titoli. Tuttavia se una particolare tipologia ha dei
   * titoli è necessario aggiungere, all'albero, questo livello intermedio prima
   * di caricare la categorie vere e proprie (dal livello 0 in poi).
   *
   * @param tiplavg
   * @param ngara
   * @param jsonArray
   * @throws SQLException
   */
  private void popolaTitoli(Long tiplavg, String ngara, JSONArray jsonArray) throws SQLException {

    String selectTITCAT = "select distinct cais.titcat, tab5.tab5desc "
        + " from cais, tab5 "
        + " where cais.tiplavg = ? "
        + " and cais.titcat = tab5.tab5tip "
        + " and tab5.tab5cod = 'G_j05' "
        + " order by tab5.tab5desc";

    List<?> datiTITCAT = this.sqlManager.getListVector(selectTITCAT, new Object[] { tiplavg });

    if (datiTITCAT != null && datiTITCAT.size() > 0) {
      for (int i = 0; i < datiTITCAT.size(); i++) {
        String titcat = (String) SqlManager.getValueFromVectorParam(datiTITCAT.get(i), 0).getValue();
        String tab5desc = (String) SqlManager.getValueFromVectorParam(datiTITCAT.get(i), 1).getValue();

        int numArticoliAssociati = this.numeroArticoliDaLivello0(tiplavg, titcat, ngara);

        if (numArticoliAssociati > 0) {
          Object[] row = new Object[13];
          row[0] = "0";
          row[1] = tiplavg;
          row[2] = titcat;
          row[3] = "";
          row[4] = "";
          row[5] = "";
          row[6] = "";
          row[7] = "";
          row[8] = tab5desc;
          row[9] = numArticoliAssociati;
          row[10] = null;
          row[11] = false;
          row[12] = null;
          jsonArray.add(row);
        }
      }
    }
  }

  /**
   * Popola la lista delle categorie di livello 0
   *
   * @param tiplavg
   * @param titcat
   * @param ngara
   * @param jsonArray
   * @throws SQLException
   */
  private void popolaLivello0(Long tiplavg, String titcat, String ngara, boolean orderByCodice, JSONArray jsonArray) throws SQLException {

    List<?> datiCategorie = null;

    String orderBy = "order by caisord, caisim";
    if (orderByCodice == false) orderBy = "order by caisord, descat";

    if (titcat == null || (titcat != null && "".equals(titcat.trim()))) {
      String selectCategorie = "select caisim, descat from cais "
          + " where tiplavg = ? "
          + " and titcat is null "
          + " and codliv1 is null "
          + " and codliv2 is null "
          + " and codliv3 is null "
          + " and codliv4 is null "
          + orderBy;
      datiCategorie = this.sqlManager.getListVector(selectCategorie, new Object[] { tiplavg });
    } else {
      String selectCategorie = "select caisim, descat from cais "
          + " where tiplavg = ? "
          + " and titcat = ? "
          + " and codliv1 is null "
          + " and codliv2 is null "
          + " and codliv3 is null "
          + " and codliv4 is null "
          + orderBy;
      datiCategorie = this.sqlManager.getListVector(selectCategorie, new Object[] { tiplavg, titcat });
    }

    if (datiCategorie != null && datiCategorie.size() > 0) {
      for (int i = 0; i < datiCategorie.size(); i++) {
        String caisim = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 0).getValue();
        String descat = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 1).getValue();

        int numArticoliAssociatiCategoria = this.numeroArticoli(caisim, ngara);
        int numArticoliAssociati = this.numeroArticoliDaLivello1(tiplavg, caisim, ngara);
        numArticoliAssociati += numArticoliAssociatiCategoria;
        String livello = "1";

        if (numArticoliAssociati > 0) {
          Object[] row = new Object[14];
          row[0] = livello;
          row[1] = tiplavg;
          row[2] = titcat;
          row[3] = caisim;
          row[4] = caisim;
          row[5] = "";
          row[6] = "";
          row[7] = "";
          row[8] = descat;
          row[9] = numArticoliAssociati;
          row[10] = null;
          row[11] = false;
          row[12] = null;
          row[13] = this.getOPES_ORDMIN(caisim, ngara);
          jsonArray.add(row);
        }
      }
    }
  }

  /**
   * Popola la lista delle categorie di livello 1
   *
   * @param tiplavg
   * @param caisim_livello1
   * @param ngara
   * @param jsonArray
   * @param addChild
   * @throws SQLException
   */
  private void popolaLivello1(Long tiplavg, String titcat, String caisim_livello1, String ngara, boolean orderByCodice, JSONArray jsonArray)
      throws SQLException {

    String orderBy = "order by caisord, caisim";
    if (orderByCodice == false) orderBy = "order by caisord, descat";

    String selectCategorie = "select caisim, descat from cais "
        + " where tiplavg = ? "
        + " and codliv1 = ? "
        + " and codliv2 is null "
        + " and codliv3 is null "
        + " and codliv4 is null "
        + orderBy;
    List<?> datiCategorie = this.sqlManager.getListVector(selectCategorie, new Object[] { tiplavg, caisim_livello1 });

    if (datiCategorie != null && datiCategorie.size() > 0) {
      for (int i = 0; i < datiCategorie.size(); i++) {
        String caisim = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 0).getValue();
        String descat = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 1).getValue();

        int numArticoliAssociatiCategoria = this.numeroArticoli(caisim, ngara);
        int numArticoliAssociati = this.numeroArticoliDaLivello2(tiplavg, caisim_livello1, caisim, ngara) + numArticoliAssociatiCategoria;
        String livello = "2";

        if (numArticoliAssociati > 0) {
          Object[] row = new Object[14];
          row[0] = livello;
          row[1] = tiplavg;
          row[2] = titcat;
          row[3] = caisim;
          row[4] = caisim_livello1;
          row[5] = caisim;
          row[6] = "";
          row[7] = "";
          row[8] = descat;
          row[9] = numArticoliAssociati;
          row[10] = null;
          row[11] = false;
          row[12] = null;
          row[13] = this.getOPES_ORDMIN(caisim, ngara);
          jsonArray.add(row);
        }
      }
    }
  }

  /**
   * Popola la lista delle categorie di livello 2.
   *
   * @param tiplavg
   * @param titcat
   * @param caisim_livello1
   * @param caisim_livello2
   * @param ngara
   * @param jsonArray
   * @throws SQLException
   */
  private void popolaLivello2(Long tiplavg, String titcat, String caisim_livello1, String caisim_livello2, String ngara,
      boolean orderByCodice, JSONArray jsonArray) throws SQLException {

    String orderBy = "order by caisord, caisim";
    if (orderByCodice == false) orderBy = "order by caisord, descat";

    String selectCategorie = "select caisim, descat from cais "
        + " where tiplavg = ? "
        + " and codliv1 = ? "
        + " and codliv2 = ? "
        + " and codliv3 is null "
        + " and codliv4 is null "
        + orderBy;
    List<?> datiCategorie = this.sqlManager.getListVector(selectCategorie, new Object[] { tiplavg, caisim_livello1, caisim_livello2 });
    if (datiCategorie != null && datiCategorie.size() > 0) {
      for (int i = 0; i < datiCategorie.size(); i++) {
        String caisim = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 0).getValue();
        String descat = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 1).getValue();

        int numArticoliAssociatiCategoria = this.numeroArticoli(caisim, ngara);
        int numArticoliAssociati = this.numeroArticoliDaLivello3(tiplavg, caisim_livello1, caisim_livello2, caisim, ngara)
            + numArticoliAssociatiCategoria;
        String livello = "3";

        if (numArticoliAssociati > 0) {
          Object[] row = new Object[14];
          row[0] = livello;
          row[1] = tiplavg;
          row[2] = titcat;
          row[3] = caisim;
          row[4] = caisim_livello1;
          row[5] = caisim_livello2;
          row[6] = caisim;
          row[7] = "";
          row[8] = descat;
          row[9] = numArticoliAssociati;
          row[10] = null;
          row[11] = false;
          row[12] = null;
          row[13] = this.getOPES_ORDMIN(caisim, ngara);
          jsonArray.add(row);
        }

      }
    }
  }

  /**
   * Popola la lista delle categorie di livello 3.
   *
   * @param tiplavg
   * @param titcat
   * @param caisim_livello1
   * @param caisim_livello2
   * @param caisim_livello3
   * @param ngara
   * @param jsonArray
   * @throws SQLException
   */
  private void popolaLivello3(Long tiplavg, String titcat, String caisim_livello1, String caisim_livello2, String caisim_livello3,
      String ngara, boolean orderByCodice, JSONArray jsonArray) throws SQLException {

    String orderBy = "order by caisord, caisim";
    if (orderByCodice == false) orderBy = "order by caisord, descat";

    String selectCategorie = "select caisim, descat from cais "
        + " where tiplavg = ? "
        + " and codliv1 = ? "
        + " and codliv2 = ? "
        + " and codliv3 = ? "
        + " and codliv4 is null "
        + orderBy;
    List<?> datiCategorie = this.sqlManager.getListVector(selectCategorie, new Object[] { tiplavg, caisim_livello1, caisim_livello2,
        caisim_livello3 });

    if (datiCategorie != null && datiCategorie.size() > 0) {
      for (int i = 0; i < datiCategorie.size(); i++) {
        String caisim = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 0).getValue();
        String descat = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 1).getValue();

        int numArticoliAssociatiCategoria = this.numeroArticoli(caisim, ngara);
        int numArticoliAssociati = this.numeroArticoliDaLivello4(tiplavg, caisim_livello1, caisim_livello2, caisim_livello3, caisim, ngara)
            + numArticoliAssociatiCategoria;
        String livello = "4";

        if (numArticoliAssociati > 0) {
          Object[] row = new Object[14];
          row[0] = livello;
          row[1] = tiplavg;
          row[2] = titcat;
          row[3] = caisim;
          row[4] = caisim_livello1;
          row[5] = caisim_livello2;
          row[6] = caisim_livello3;
          row[7] = caisim;
          row[8] = descat;
          row[9] = numArticoliAssociati;
          row[10] = null;
          row[11] = false;
          row[12] = null;
          row[13] = this.getOPES_ORDMIN(caisim, ngara);
          jsonArray.add(row);
        }

      }
    }
  }

  /**
   * Popola la lista delle categorie di livello 4.
   *
   * @param tiplavg
   * @param titcat
   * @param caisim_livello1
   * @param caisim_livello2
   * @param caisim_livello3
   * @param caisim_livello4
   * @param ngara
   * @param jsonArray
   * @return
   * @throws SQLException
   */
  private int popolaLivello4(Long tiplavg, String titcat, String caisim_livello1, String caisim_livello2, String caisim_livello3,
      String caisim_livello4, String ngara, boolean orderByCodice, JSONArray jsonArray) throws SQLException {

    String orderBy = "order by caisord, caisim";
    if (orderByCodice == false) orderBy = "order by caisord, descat";

    int numero = 0;
    String selectCategorie = "select caisim, descat from cais "
        + " where tiplavg = ? "
        + " and codliv1 = ? "
        + " and codliv2 = ? "
        + " and codliv3 = ? "
        + " and codliv4 = ? "
        + orderBy;
    List<?> datiCategorie = this.sqlManager.getListVector(selectCategorie, new Object[] { tiplavg, caisim_livello1, caisim_livello2,
        caisim_livello3, caisim_livello4 });
    if (datiCategorie != null && datiCategorie.size() > 0) {
      numero = datiCategorie.size();
      for (int i = 0; i < datiCategorie.size(); i++) {
        String caisim = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 0).getValue();
        String descat = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 1).getValue();

        int numArticoliAssociati = this.numeroArticoli(caisim, ngara);
        String livello = "5";

        if (numArticoliAssociati > 0) {
          Object[] row = new Object[143];
          row[0] = livello;
          row[1] = tiplavg;
          row[2] = titcat;
          row[3] = caisim;
          row[4] = caisim_livello1;
          row[5] = caisim_livello2;
          row[6] = caisim_livello3;
          row[7] = caisim_livello4;
          row[8] = descat;
          row[9] = numArticoliAssociati;
          row[10] = null;
          row[11] = false;
          row[12] = null;
          row[13] = this.getOPES_ORDMIN(caisim, ngara);
          jsonArray.add(row);
        }
      }
    }
    return numero;
  }

  /**
   * Popola la lista degli articoli collegate ad una determinata categoria di
   * OPES.
   *
   * @param catoff
   * @param ngara
   * @param jsonArray
   * @return
   * @throws SQLException
   */
  private int popolaArticoli(String catoff, String ngara, Long meric_id, JSONArray jsonArray) throws SQLException {
    int numero = 0;
    String selectMEARTCAT = "select meartcat.id, meartcat.cod, meartcat.descr, meartcat.colore "
        + " from meartcat, opes "
        + " where meartcat.ngara = ? "
        + " and meartcat.ngara = opes.ngara3 "
        + " and meartcat.nopega = opes.nopega "
        + " and opes.catoff = ? "
        + " and meartcat.stato = ? "
        + " order by meartcat.descr, meartcat.cod";
    List<?> datiMEARTCAT = this.sqlManager.getListVector(selectMEARTCAT, new Object[] { ngara, catoff, STATO_ARTICOLO_ATTIVO });
    if (datiMEARTCAT != null && datiMEARTCAT.size() > 0) {
      numero = datiMEARTCAT.size();
      for (int i = 0; i < datiMEARTCAT.size(); i++) {
        Long id = (Long) SqlManager.getValueFromVectorParam(datiMEARTCAT.get(i), 0).getValue();
        String cod = (String) SqlManager.getValueFromVectorParam(datiMEARTCAT.get(i), 1).getValue();
        String descr = (String) SqlManager.getValueFromVectorParam(datiMEARTCAT.get(i), 2).getValue();
        String colore = (String) SqlManager.getValueFromVectorParam(datiMEARTCAT.get(i), 3).getValue();

        String descrizione = descr;
        if (colore != null && !"".equals(colore.trim())) {
          descrizione += " (" + colore + ")";
        }

        Object[] row = new Object[13];
        row[0] = "51";
        row[1] = "";
        row[2] = "";
        row[3] = cod;
        row[4] = "";
        row[5] = "";
        row[6] = "";
        row[7] = "";
        row[8] = descrizione;
        row[9] = 0;
        row[10] = id;
        row[11] = this.isArticoloInCarrello(meric_id, id);
        row[12] = this.quantitaArticoloInCarrello(meric_id, id);

        jsonArray.add(row);

      }
    }

    return numero;
  }

  /**
   * Numero titoli (in funzione della tipologia) con categorie associate
   *
   * @param tiplav
   * @param ngara
   * @return
   * @throws SQLException
   */
  private int numeroTitoliAssociati(Long tiplavg, String ngara) throws SQLException {
    String selectCAIS = "select count(*) from cais, opes where cais.tiplavg = ? and cais.titcat is not null and cais.caisim = opes.catoff and opes.ngara3 = ?";
    Long numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, ngara });
    return (numero != null) ? numero.intValue() : 0;
  }

  /**
   * Numero titoli contenenti categoria associate ad articoli.
   *
   * @param tiplavg
   * @param ngara
   * @return
   * @throws SQLException
   */
  private int numeroArticoliDaTitolo(Long tiplavg, String ngara) throws SQLException {
    String selectCAIS = "select count(*) from cais, opes, meartcat "
        + " where cais.tiplavg = ? "
        + " and cais.titcat is not null "
        + " and cais.caisim = opes.catoff "
        + " and opes.ngara3 = ?"
        + " and meartcat.ngara = opes.ngara3 "
        + " and meartcat.nopega = opes.nopega "
        + " and meartcat.stato = ?";
    Long numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, ngara, STATO_ARTICOLO_ATTIVO });
    return (numero != null) ? numero.intValue() : 0;
  }

  /**
   * Numero di categorie (da livello 0) contenenti articoli.
   *
   * @param tiplavg
   * @param titcat
   * @param ngara
   * @return
   * @throws SQLException
   */
  private int numeroArticoliDaLivello0(Long tiplavg, String titcat, String ngara) throws SQLException {
    Long numero = null;
    if (titcat == null) {
      String selectCAIS = "select count(*) from cais, opes, meartcat"
          + " where cais.tiplavg = ? "
          + " and cais.titcat is null "
          + " and cais.caisim = opes.catoff "
          + " and opes.ngara3 = ? "
          + " and meartcat.ngara = opes.ngara3 "
          + " and meartcat.nopega = opes.nopega "
          + " and meartcat.stato = ?";
      numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, ngara, STATO_ARTICOLO_ATTIVO });
    } else {
      String selectCAIS = "select count(*) from cais, opes, meartcat"
          + " where cais.tiplavg = ? "
          + " and cais.titcat = ? "
          + " and cais.caisim = opes.catoff "
          + " and opes.ngara3 = ? "
          + " and meartcat.ngara = opes.ngara3 "
          + " and meartcat.nopega = opes.nopega "
          + " and meartcat.stato = ?";
      numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, titcat, ngara, STATO_ARTICOLO_ATTIVO });
    }
    return (numero != null) ? numero.intValue() : 0;
  }

  /**
   * Numero di categorie (da livello 1) contenenti articoli.
   *
   * @param tiplavg
   * @param caisim_livello1
   * @param ngara
   * @return
   * @throws SQLException
   */
  private int numeroArticoliDaLivello1(Long tiplavg, String caisim_livello1, String ngara) throws SQLException {
    String selectCAIS = "select count(*) from cais, opes, meartcat "
        + " where cais.tiplavg = ? "
        + " and cais.codliv1 = ? "
        + " and cais.caisim = opes.catoff "
        + " and opes.ngara3 = ? "
        + " and meartcat.ngara = opes.ngara3 "
        + " and meartcat.nopega = opes.nopega "
        + " and meartcat.stato = ?";
    Long numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, caisim_livello1, ngara, STATO_ARTICOLO_ATTIVO });
    return (numero != null) ? numero.intValue() : 0;
  }

  /**
   * Numero di categorie (da livello 2) contenenti articoli.
   *
   * @param tiplavg
   * @param caisim_livello1
   * @param caisim_livello2
   * @param ngara
   * @return
   * @throws SQLException
   */
  private int numeroArticoliDaLivello2(Long tiplavg, String caisim_livello1, String caisim_livello2, String ngara) throws SQLException {
    String selectCAIS = "select count(*) from cais, opes, meartcat "
        + " where cais.tiplavg = ? "
        + " and cais.codliv1 = ? "
        + " and cais.codliv2 = ? "
        + " and cais.caisim = opes.catoff "
        + " and opes.ngara3 = ? "
        + " and meartcat.ngara = opes.ngara3 "
        + " and meartcat.nopega = opes.nopega "
        + " and meartcat.stato = ?";
    Long numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, caisim_livello1, caisim_livello2, ngara,
        STATO_ARTICOLO_ATTIVO });
    return (numero != null) ? numero.intValue() : 0;
  }

  /**
   * Numero di categorie (da livello 3) contenenti articoli.
   *
   * @param tiplavg
   * @param caisim_livello1
   * @param caisim_livello2
   * @param caisim_livello3
   * @param ngara
   * @return
   * @throws SQLException
   */
  private int numeroArticoliDaLivello3(Long tiplavg, String caisim_livello1, String caisim_livello2, String caisim_livello3, String ngara)
      throws SQLException {
    String selectCAIS = "select count(*) from cais, opes, meartcat "
        + " where cais.tiplavg = ? "
        + " and cais.codliv1 = ? "
        + " and cais.codliv2 = ? "
        + " and cais.codliv3 = ? "
        + " and cais.caisim = opes.catoff "
        + " and opes.ngara3 = ? "
        + " and meartcat.ngara = opes.ngara3 "
        + " and meartcat.nopega = opes.nopega "
        + " and meartcat.stato = ?";
    Long numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, caisim_livello1, caisim_livello2, caisim_livello3,
        ngara, STATO_ARTICOLO_ATTIVO });
    return (numero != null) ? numero.intValue() : 0;
  }

  /**
   * Numero di categorie (da livello 4) contenenti articoli.
   *
   * @param tiplavg
   * @param caisim_livello1
   * @param caisim_livello2
   * @param caisim_livello3
   * @param caisim_livello4
   * @param ngara
   * @return
   * @throws SQLException
   */
  private int numeroArticoliDaLivello4(Long tiplavg, String caisim_livello1, String caisim_livello2, String caisim_livello3,
      String caisim_livello4, String ngara) throws SQLException {
    String selectCAIS = "select count(*) from cais, opes, meartcat "
        + " where cais.tiplavg = ? "
        + " and cais.codliv1 = ? "
        + " and cais.codliv2 = ? "
        + " and cais.codliv3 = ? "
        + " and cais.codliv4 = ? "
        + " and cais.caisim = opes.catoff "
        + " and opes.ngara3 = ? "
        + " and meartcat.ngara = opes.ngara3 "
        + " and meartcat.nopega = opes.nopega "
        + " and meartcat.stato = ? ";
    Long numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, caisim_livello1, caisim_livello2, caisim_livello3,
        caisim_livello4, ngara, STATO_ARTICOLO_ATTIVO });
    return (numero != null) ? numero.intValue() : 0;
  }

  /**
   * Numero articoli (MEARTCAT) collegati alla categoria (OPES.CATOFF).
   *
   * @param caisim
   * @param ngara
   * @return
   * @throws SQLException
   */
  private int numeroArticoli(String caisim, String ngara) throws SQLException {
    String selectMEARTCAT = "select count(*) from meartcat, opes where opes.ngara3 = ? and opes.catoff = ? and meartcat.ngara = opes.ngara3 and meartcat.nopega = opes.nopega";
    Long numero = (Long) this.sqlManager.getObject(selectMEARTCAT, new Object[] { ngara, caisim });
    return (numero != null) ? numero.intValue() : 0;
  }

  /**
   * Verifica se l'articolo e' presente nel carrello articoli della ricerca di
   * mercato.
   *
   * @param meric_id
   * @param meartcat_id
   * @return
   * @throws SQLException
   */
  private boolean isArticoloInCarrello(Long meric_id, Long meartcat_id) throws SQLException {
    String selectMERICART = "select count(*) from mericart where idric = ? and idartcat = ?";
    Long numero = (Long) this.sqlManager.getObject(selectMERICART, new Object[] { meric_id, meartcat_id });
    return (numero != null && numero.longValue() > 0) ? true : false;
  }

  /**
   * Quantita' dell'articolo in carrello.
   *
   * @param meric_id
   * @param meartcat_id
   * @return
   * @throws SQLException
   */
  private Double quantitaArticoloInCarrello(Long meric_id, Long meartcat_id) throws SQLException {
    String selectMERICART = "select quanti from mericart where idric = ? and idartcat = ?";
    return (Double) this.sqlManager.getObject(selectMERICART, new Object[] { meric_id, meartcat_id });

  }
  /**
   * Ricava l'importo minimo dell'ordine assegnato alla categoria.
   *
   * @param caisim
   * @param ngara
   * @return
   * @throws SQLException
   */
  private Double getOPES_ORDMIN(String caisim, String ngara) throws SQLException {
    String selectOPES = "select ordmin from opes where ngara3 = ? and catoff = ?";
    return (Double) this.sqlManager.getObject(selectOPES, new Object[] { ngara, caisim });
  }

}
