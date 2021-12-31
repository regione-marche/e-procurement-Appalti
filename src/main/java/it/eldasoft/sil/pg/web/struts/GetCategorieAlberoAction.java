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
public class GetCategorieAlberoAction extends Action {

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
   * <li>9 - Numero CATEGORIE annidate NON ARCHIVIATE (default 0)</li>
   * <li>10 - Numero CATEGORIE annidate ARCHIVIATE (default 0)</li>
   * <li>11 - Numero CATEGORIE annidate ASSOCIATE (default 0)</li>
   * <li>12 - Numero ARTICOLI annidati ASSOCIATI (default 0)</li>
   * <li>13 - Categoria associata ? (boolean, default FALSE)</li>
   * <li>14 - Categoria archiviata ? (boolean, default FALSE)</li>
   * <li>15 - Chiave (OPES.NOPEGA) della categoria associata o identificativo
   * dell'articolo (MEARTCAT.ID)</li>
   * <li>16 - Importo minimo ordine</li>
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
    String genere = request.getParameter("genere");

    JSONArray jsonArray = new JSONArray();

    if ("load".equals(operation)) {
      String modoapertura = request.getParameter("modoapertura");

      // Livello del nodo
      Long livello = null;
      if (request.getParameter("livello") != "") livello = new Long(request.getParameter("livello"));

      // Tipologia categorie
      Long tiplavg = null;
      if (request.getParameter("tiplavg") != "") tiplavg = new Long(request.getParameter("tiplavg"));

      // Ordinamento
      boolean orderByCodice = true;
      String tab1desc = (String) sqlManager.getObject("select tab1desc from tab1 where tab1cod = ? and tab1tip = ?", new Object[] {"G_057", new Long(1)});
      if (tab1desc != null && "2".equals(tab1desc.trim().substring(0,1))) {
        orderByCodice = false;
      }

      String titcat = request.getParameter("titcat");
      String caisim = request.getParameter("caisim");
      String caisim_livello1 = request.getParameter("caisim_livello1");
      String caisim_livello2 = request.getParameter("caisim_livello2");
      String caisim_livello3 = request.getParameter("caisim_livello3");
      String caisim_livello4 = request.getParameter("caisim_livello4");
      String tipologie = request.getParameter("tipologie");

      switch (livello.intValue()) {
      case -2:
        this.popolaRoot(ngara, tipologie, jsonArray,genere);
        break;

      case -1:
        this.popolaTitoli(tiplavg, ngara, jsonArray, genere);
        this.popolaLivello0(tiplavg, null, ngara, orderByCodice, jsonArray, genere);
        break;

      case 0:
        this.popolaLivello0(tiplavg, titcat, ngara, orderByCodice, jsonArray, genere);
        break;

      case 1:
        this.popolaArticoli(caisim, ngara, jsonArray, genere);
        this.popolaLivello1(tiplavg, titcat, caisim_livello1, ngara, orderByCodice, jsonArray, genere);
        break;

      case 2:
        this.popolaArticoli(caisim, ngara, jsonArray, genere);
        this.popolaLivello2(tiplavg, titcat, caisim_livello1, caisim_livello2, ngara, orderByCodice, jsonArray, genere);
        break;

      case 3:
        this.popolaArticoli(caisim, ngara, jsonArray, genere);
        this.popolaLivello3(tiplavg, titcat, caisim_livello1, caisim_livello2, caisim_livello3, ngara, orderByCodice, jsonArray, genere);
        break;

      case 4:
        this.popolaArticoli(caisim, ngara, jsonArray, genere);
        this.popolaLivello4(tiplavg, titcat, caisim_livello1, caisim_livello2, caisim_livello3, caisim_livello4, ngara, orderByCodice, jsonArray, genere);
        break;

      case 5:
      case 50:
        this.popolaArticoli(caisim, ngara, jsonArray, genere);

      default:
        break;
      }

      // Se in visualizzazione escludo dalla lista le categorie non associate
      // alla OPES. In questo modo, solo in visualizzazione, riduco i dati
      // trasferiti alla
      // pagina e di conseguenza le elaborazioni javascript.
      if ("VISUALIZZA".equals(modoapertura) && livello.intValue() != 50) {
        JSONArray jsonArrayVISUALIZZA = new JSONArray();
        if (jsonArray != null && jsonArray.size() > 0) {
          for (int i = 0; i < jsonArray.size(); i++) {
            int numCategorieAssociate = jsonArray.getJSONArray(i).getInt(11);
            boolean categoriaassociata = jsonArray.getJSONArray(i).getBoolean(13);
            String _livello = jsonArray.getJSONArray(i).getString(0);
            if (categoriaassociata || numCategorieAssociate > 0 || (_livello != null && _livello.equals("51")))
              jsonArrayVISUALIZZA.add(jsonArray.getJSONArray(i));
          }
        }
        jsonArray = new JSONArray();
        jsonArray.addAll(jsonArrayVISUALIZZA);
      }
    } else if ("search".equals(operation)) {
      String textsearch = request.getParameter("textsearch");
      String modoapertura = request.getParameter("modoapertura");
      this.searchCategoria(ngara, textsearch, jsonArray, modoapertura,genere);
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
  private void searchCategoria(String ngara, String textsearch_any, JSONArray jsonArray, String modoapertura, String genere) throws SQLException {

    String[] words = textsearch_any.split(" ");

    for (int ia = 0; ia < words.length; ia++) {
      String textsearch = words[ia];
      if (textsearch != null && textsearch.trim().length() >= 3 && !"".equals(textsearch.trim())) {

        // Caratteri di escape
        textsearch = StringUtils.replace(textsearch, "_____", "#_____");
        textsearch = StringUtils.replace(textsearch, "%", "#%");
        textsearch = "%" + textsearch.toUpperCase() + "%";

        String selectCAIS = null;
        List<?> datiCAIS = null;
        if("20".equals(genere)){
         selectCAIS = "select cais.tiplavg, cais.titcat, cais.codliv1, cais.codliv2, cais.codliv3, cais.codliv4, cais.caisim "
            + " from cais "
            + " where upper(cais.caisim) like ? escape '#' "
            + " or upper(cais.descat) like ? escape '#' "
            + " or exists (select tab5.tab5tip from tab5 where tab5.tab5cod = 'G_j05' and cais.titcat = tab5.tab5tip and upper(tab5.tab5desc) like ? escape '#') "
            + " or exists (select meartcat.id from meartcat, opes "
            + " where cais.caisim = opes.catoff and opes.ngara3 = ? "
            + " and meartcat.ngara = opes.ngara3 "
            + " and meartcat.nopega = opes.nopega "
            + " and ((upper(meartcat.cod) like ? escape '#') "
            + " or (upper(meartcat.descr) like ? escape '#') or (upper(meartcat.colore) like ? escape '#')))";
        datiCAIS = this.sqlManager.getListVector(selectCAIS, new Object[] { textsearch, textsearch, textsearch, ngara, textsearch,
            textsearch, textsearch });
        }else{
          selectCAIS = "select cais.tiplavg, cais.titcat, cais.codliv1, cais.codliv2, cais.codliv3, cais.codliv4, cais.caisim "
            + " from cais "
            + " where upper(cais.caisim) like ? escape '#' "
            + " or upper(cais.descat) like ? escape '#' "
            + " or exists (select tab5.tab5tip from tab5 where tab5.tab5cod = 'G_j05' and cais.titcat = tab5.tab5tip and upper(tab5.tab5desc) like ? escape '#') ";
          datiCAIS = this.sqlManager.getListVector(selectCAIS, new Object[] { textsearch, textsearch, textsearch, });
        }

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
              Long nopega = this.nopegaCategoriaAssociata(caisim, ngara);
              boolean categoriaAssociata = (nopega != null) ? true : false;
              if (("VISUALIZZA".equals(modoapertura) && categoriaAssociata) || !"VISUALIZZA".equals(modoapertura)) {
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
  }

  /**
   * Popola l'elemento root dell'albero (corrisponde al tabellato con la lista
   * delle tipologie di categorie).
   *
   * @param ngara
   * @param tipologie
   * @param jsonArray
   * @param genere
   * @throws SQLException
   */
  private void popolaRoot(String ngara, String tipologie, JSONArray jsonArray, String genere) throws SQLException {

    tipologie = StringUtils.leftPad(tipologie, 3, "0");

    String selectCategorie = "select tab1tip, tab1desc from tab1 where tab1cod = ? order by tab1nord, tab1tip";
    List<?> datiCategorie = this.sqlManager.getListVector(selectCategorie, new Object[] { "G_038" });
    if (datiCategorie != null && datiCategorie.size() > 0) {
      for (int i = 0; i < datiCategorie.size(); i++) {
        Long tiplavg = (Long) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 0).getValue();
        String titcat = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 1).getValue();

        // Per ogni CAIS.TIPLAVG controllo in funzione della tipologia
        // di elenco (TIPOLOGIE) se deve essere caricato il nodo ROOT
        boolean tipologia_abilitata = false;
        switch (tiplavg.intValue()) {
        case 1:
          if ("1".equals(tipologie.substring(0, 1))) tipologia_abilitata = true;
          break;

        case 2:
          if ("1".equals(tipologie.substring(1, 2))) tipologia_abilitata = true;
          break;

        case 3:
          if ("1".equals(tipologie.substring(2))) tipologia_abilitata = true;
          break;

        case 4:
          if ("1".equals(tipologie.substring(0, 1))) tipologia_abilitata = true;
          break;

        case 5:
          if ("1".equals(tipologie.substring(2))) tipologia_abilitata = true;
          break;
        }

        if (tipologia_abilitata) {

          int numTitoli[] = this.numeroTitoli(tiplavg);

          if (numTitoli[0] > 0 || numTitoli[1] > 0) {
            int[] numCategorie = this.numeroCategorieDaLivello0(tiplavg, null);

            int numCategorieNonArchiviate = numTitoli[0] + numCategorie[0];
            int numCategorieArchiviate = numTitoli[1] + numCategorie[1];

            int numCategorieAssociate = this.numeroTitoliAssociati(tiplavg, ngara);
            numCategorieAssociate += this.numeroCategorieDaLivello0Associate(tiplavg, null, ngara);

            int numArticoliAssociati = this.numeroArticoliDaTitolo(tiplavg, ngara, genere);
            numArticoliAssociati += this.numeroArticoliDaLivello0(tiplavg, null, ngara, genere);

            Object[] row = new Object[17];
            row[0] = "-1";
            row[1] = tiplavg;
            row[2] = "";
            row[3] = "";
            row[4] = "";
            row[5] = "";
            row[6] = "";
            row[7] = "";
            row[8] = titcat;
            row[9] = numCategorieNonArchiviate;
            row[10] = numCategorieArchiviate;
            row[11] = numCategorieAssociate;
            row[12] = numArticoliAssociati;
            row[13] = false;
            row[14] = false;
            row[15] = null;
            row[16] = null;

            jsonArray.add(row);

          } else {
            int num[] = this.numeroCategorieDaLivello0(tiplavg, null);
            int numCategorieNonArchiviate = num[0];
            int numCategorieArchiviate = num[1];
            int numCategorieAssociate = this.numeroCategorieDaLivello0Associate(tiplavg, null, ngara);
            int numArticoliAssociati = this.numeroArticoliDaLivello0(tiplavg, null, ngara, genere);

            Object[] row = new Object[17];
            row[0] = "0";
            row[1] = tiplavg;
            row[2] = "";
            row[3] = "";
            row[4] = "";
            row[5] = "";
            row[6] = "";
            row[7] = "";
            row[8] = titcat;
            row[9] = numCategorieNonArchiviate;
            row[10] = numCategorieArchiviate;
            row[11] = numCategorieAssociate;
            row[12] = numArticoliAssociati;
            row[13] = false;
            row[14] = false;
            row[15] = null;
            row[16] = null;

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
   * @param genere
   * @throws SQLException
   */
  private void popolaTitoli(Long tiplavg, String ngara, JSONArray jsonArray, String genere) throws SQLException {

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

        int num[] = this.numeroCategorieDaLivello0(tiplavg, titcat);
        int numCategorieNonArchiviate = num[0];
        int numCategorieArchiviate = num[1];
        int numCategorieAssociate = this.numeroCategorieDaLivello0Associate(tiplavg, titcat, ngara);
        int numArticoliAssociati = this.numeroArticoliDaLivello0(tiplavg, titcat, ngara, genere);

        Object[] row = new Object[17];
        row[0] = "0";
        row[1] = tiplavg;
        row[2] = titcat;
        row[3] = "";
        row[4] = "";
        row[5] = "";
        row[6] = "";
        row[7] = "";
        row[8] = tab5desc;
        row[9] = numCategorieNonArchiviate;
        row[10] = numCategorieArchiviate;
        row[11] = numCategorieAssociate;
        row[12] = numArticoliAssociati;
        row[13] = false;
        row[14] = false;
        row[15] = null;
        row[16] = null;

        jsonArray.add(row);
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
   * @param genere
   * @throws SQLException
   */
  private void popolaLivello0(Long tiplavg, String titcat, String ngara, boolean orderByCodice, JSONArray jsonArray, String genere) throws SQLException {

    List<?> datiCategorie = null;

    String orderBy = "order by caisord, caisim";
    if (orderByCodice == false) orderBy = "order by caisord, descat";

    if (titcat == null || (titcat != null && "".equals(titcat.trim()))) {
      String selectCategorie = "select caisim, descat, isarchi from cais "
          + " where tiplavg = ? "
          + " and titcat is null "
          + " and codliv1 is null "
          + " and codliv2 is null "
          + " and codliv3 is null "
          + " and codliv4 is null "
          + orderBy;
      datiCategorie = this.sqlManager.getListVector(selectCategorie, new Object[] { tiplavg });
    } else {
      String selectCategorie = "select caisim, descat, isarchi from cais "
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
        String isarchi = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 2).getValue();

        int num[] = this.numeroCategorieDaLivello1(tiplavg, caisim);
        int numCategorieNonArchiviate = num[0];
        int numCategorieArchiviate = num[1];
        int numCategorieAssociate = this.numeroCategorieDaLivello1Associate(tiplavg, caisim, ngara);

        int numArticoliAssociatiCategoria = this.numeroArticoli(caisim, ngara, genere);
        int numArticoliAssociati = this.numeroArticoliDaLivello1(tiplavg, caisim, ngara, genere);
        numArticoliAssociati += numArticoliAssociatiCategoria;
        // String livello = (numArticoliAssociatiCategoria > 0) ? "50" : "1";
        String livello = "1";

        Long nopega = this.nopegaCategoriaAssociata(caisim, ngara);
        boolean categoriaAssociata = (nopega != null) ? true : false;
        boolean categoriaArchiviata = (isarchi != null && "1".equals(isarchi)) ? true : false;

        Object[] row = new Object[17];
        row[0] = livello;
        row[1] = tiplavg;
        row[2] = titcat;
        row[3] = caisim;
        row[4] = caisim;
        row[5] = "";
        row[6] = "";
        row[7] = "";
        row[8] = descat;
        row[9] = numCategorieNonArchiviate;
        row[10] = numCategorieArchiviate;
        row[11] = numCategorieAssociate;
        row[12] = numArticoliAssociati;
        row[13] = categoriaAssociata;
        row[14] = categoriaArchiviata;
        row[15] = nopega;
        row[16] = this.getOPES_ORDMIN(caisim, ngara);

        jsonArray.add(row);

      }
    }
  }

  /**
   * Popola la lista delle categorie di livello 1
   *
   * @param tiplavg
   * @param titcat
   * @param caisim_livello1
   * @param ngara
   * @param orderByCodice
   * @param jsonArray
   * @param genere
   * @throws SQLException
   */
  private void popolaLivello1(Long tiplavg, String titcat, String caisim_livello1, String ngara, boolean orderByCodice, JSONArray jsonArray, String genere) throws SQLException {

    String orderBy = "order by caisord, caisim";
    if (orderByCodice == false) orderBy = "order by caisord, descat";

    String selectCategorie = "select caisim, descat, isarchi from cais "
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
        String isarchi = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 2).getValue();

        int num[] = this.numeroCategorieDaLivello2(tiplavg, caisim_livello1, caisim);
        int numCategorieNonArchiviate = num[0];
        int numCategorieArchiviate = num[1];
        int numCategorieAssociate = this.numeroCategorieDaLivello2Associate(tiplavg, caisim_livello1, caisim, ngara);

        int numArticoliAssociatiCategoria = this.numeroArticoli(caisim, ngara, genere);
        int numArticoliAssociati = this.numeroArticoliDaLivello2(tiplavg, caisim_livello1, caisim, ngara, genere) + numArticoliAssociatiCategoria;
        // String livello = (numArticoliAssociatiCategoria > 0) ? "50" : "2";
        String livello = "2";

        Long nopega = this.nopegaCategoriaAssociata(caisim, ngara);
        boolean categoriaAssociata = (nopega != null) ? true : false;
        boolean categoriaArchiviata = (isarchi != null && "1".equals(isarchi)) ? true : false;

        Object[] row = new Object[17];
        row[0] = livello;
        row[1] = tiplavg;
        row[2] = titcat;
        row[3] = caisim;
        row[4] = caisim_livello1;
        row[5] = caisim;
        row[6] = "";
        row[7] = "";
        row[8] = descat;
        row[9] = numCategorieNonArchiviate;
        row[10] = numCategorieArchiviate;
        row[11] = numCategorieAssociate;
        row[12] = numArticoliAssociati;
        row[13] = categoriaAssociata;
        row[14] = categoriaArchiviata;
        row[15] = nopega;
        row[16] = this.getOPES_ORDMIN(caisim, ngara);

        jsonArray.add(row);

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
   * @param orderByCodice
   * @param jsonArray
   * @param genere
   * @throws SQLException
   */
  private void popolaLivello2(Long tiplavg, String titcat, String caisim_livello1, String caisim_livello2, String ngara, boolean orderByCodice, JSONArray jsonArray, String genere)
      throws SQLException {

    String orderBy = "order by caisord, caisim";
    if (orderByCodice == false) orderBy = "order by caisord, descat";

    String selectCategorie = "select caisim, descat, isarchi from cais "
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
        String isarchi = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 2).getValue();

        int num[] = this.numeroCategorieDaLivello3(tiplavg, caisim_livello1, caisim_livello2, caisim);
        int numCategorieNonArchiviate = num[0];
        int numCategorieArchiviate = num[1];
        int numCategorieAssociate = this.numeroCategorieDaLivello3Associate(tiplavg, caisim_livello1, caisim_livello2, caisim, ngara);

        int numArticoliAssociatiCategoria = this.numeroArticoli(caisim, ngara,genere);
        int numArticoliAssociati = this.numeroArticoliDaLivello3(tiplavg, caisim_livello1, caisim_livello2, caisim, ngara,genere)
            + numArticoliAssociatiCategoria;
        // String livello = (numArticoliAssociatiCategoria > 0) ? "50" : "3";
        String livello = "3";

        Long nopega = this.nopegaCategoriaAssociata(caisim, ngara);
        boolean categoriaAssociata = (nopega != null) ? true : false;
        boolean categoriaArchiviata = (isarchi != null && "1".equals(isarchi)) ? true : false;

        Object[] row = new Object[17];
        row[0] = livello;
        row[1] = tiplavg;
        row[2] = titcat;
        row[3] = caisim;
        row[4] = caisim_livello1;
        row[5] = caisim_livello2;
        row[6] = caisim;
        row[7] = "";
        row[8] = descat;
        row[9] = numCategorieNonArchiviate;
        row[10] = numCategorieArchiviate;
        row[11] = numCategorieAssociate;
        row[12] = numArticoliAssociati;
        row[13] = categoriaAssociata;
        row[14] = categoriaArchiviata;
        row[15] = nopega;
        row[16] = this.getOPES_ORDMIN(caisim, ngara);

        jsonArray.add(row);

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
   * @param orderByCodice
   * @param jsonArray
   * @param genere
   * @throws SQLException
   */
  private void popolaLivello3(Long tiplavg, String titcat, String caisim_livello1, String caisim_livello2, String caisim_livello3,
      String ngara, boolean orderByCodice, JSONArray jsonArray, String genere) throws SQLException {

    String orderBy = "order by caisord, caisim";
    if (orderByCodice == false) orderBy = "order by caisord, descat";

    String selectCategorie = "select caisim, descat, isarchi from cais "
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
        String isarchi = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 2).getValue();

        int num[] = this.numeroCategorieDaLivello4(tiplavg, caisim_livello1, caisim_livello2, caisim_livello3, caisim);

        int numCategorieNonArchiviate = num[0];
        int numCategorieArchiviate = num[1];
        int numCategorieAssociate = this.numeroCategorieDaLivello4Associate(tiplavg, caisim_livello1, caisim_livello2, caisim_livello3,
            caisim, ngara);

        int numArticoliAssociatiCategoria = this.numeroArticoli(caisim, ngara, genere);
        int numArticoliAssociati = this.numeroArticoliDaLivello4(tiplavg, caisim_livello1, caisim_livello2, caisim_livello3, caisim, ngara,genere)
            + numArticoliAssociatiCategoria;
        // String livello = (numArticoliAssociatiCategoria > 0) ? "50" : "4";
        String livello = "4";

        Long nopega = this.nopegaCategoriaAssociata(caisim, ngara);
        boolean categoriaAssociata = (nopega != null) ? true : false;
        boolean categoriaArchiviata = (isarchi != null && "1".equals(isarchi)) ? true : false;

        Object[] row = new Object[17];
        row[0] = livello;
        row[1] = tiplavg;
        row[2] = titcat;
        row[3] = caisim;
        row[4] = caisim_livello1;
        row[5] = caisim_livello2;
        row[6] = caisim_livello3;
        row[7] = caisim;
        row[8] = descat;
        row[9] = numCategorieNonArchiviate;
        row[10] = numCategorieArchiviate;
        row[11] = numCategorieAssociate;
        row[12] = numArticoliAssociati;
        row[13] = categoriaAssociata;
        row[14] = categoriaArchiviata;
        row[15] = nopega;
        row[16] = this.getOPES_ORDMIN(caisim, ngara);

        jsonArray.add(row);

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
   * @param orderByCodice
   * @param jsonArray
   * @param genere
   * @return
   * @throws SQLException
   */
  private int popolaLivello4(Long tiplavg, String titcat, String caisim_livello1, String caisim_livello2, String caisim_livello3,
      String caisim_livello4, String ngara, boolean orderByCodice, JSONArray jsonArray, String genere) throws SQLException {

    String orderBy = "order by caisord, caisim";
    if (orderByCodice == false) orderBy = "order by caisord, descat";

    int numero = 0;
    String selectCategorie = "select caisim, descat, isarchi from cais "
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
        String isarchi = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 2).getValue();

        int numCategorieNonArchiviate = 0;
        int numCategorieArchiviate = 0;
        int numCategorieAssociate = 0;

        int numArticoliAssociati = this.numeroArticoli(caisim, ngara,genere);
        // String livello = (numArticoliAssociati > 0) ? "50" : "5";
        String livello = "5";

        Long nopega = this.nopegaCategoriaAssociata(caisim, ngara);
        boolean categoriaAssociata = (nopega != null) ? true : false;
        boolean categoriaArchiviata = (isarchi != null && "1".equals(isarchi)) ? true : false;

        Object[] row = new Object[17];
        row[0] = livello;
        row[1] = tiplavg;
        row[2] = titcat;
        row[3] = caisim;
        row[4] = caisim_livello1;
        row[5] = caisim_livello2;
        row[6] = caisim_livello3;
        row[7] = caisim_livello4;
        row[8] = descat;
        row[9] = numCategorieNonArchiviate;
        row[10] = numCategorieArchiviate;
        row[11] = numCategorieAssociate;
        row[12] = numArticoliAssociati;
        row[13] = categoriaAssociata;
        row[14] = categoriaArchiviata;
        row[15] = nopega;
        row[16] = this.getOPES_ORDMIN(caisim, ngara);

        jsonArray.add(row);

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
   * @param orderByCodice
   * @param jsonArray
   * @param  genere
   * @return
   * @throws SQLException
   */
  private int popolaArticoli(String catoff, String ngara, JSONArray jsonArray,String genere) throws SQLException {
    int numero = 0;
    if("20".equals(genere)){
      String selectMEARTCAT = "select meartcat.id, meartcat.cod, meartcat.descr, meartcat.colore "
          + " from meartcat, opes "
          + " where meartcat.ngara = ? "
          + " and meartcat.ngara = opes.ngara3 "
          + " and meartcat.nopega = opes.nopega "
          + " and opes.catoff = ? order by meartcat.descr, meartcat.cod";
      List<?> datiMEARTCAT = this.sqlManager.getListVector(selectMEARTCAT, new Object[] { ngara, catoff });
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

          Object[] row = new Object[17];
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
          row[10] = 0;
          row[11] = 0;
          row[12] = 0;
          row[13] = false;
          row[14] = false;
          row[15] = id;
          row[16] = null;

          jsonArray.add(row);

        }
      }
    }
    return numero;
  }

  /**
   * Numero categorie associate ai titoli.
   *
   * Il metodo restituisce i conteggi delle categorie non archiviate ed
   * archiviate
   *
   * <ul>
   * <li>Posizione 0 - Numero categorie non archiviate</li>
   * <li>Posizione 1 - Numero categorie archiviate</li>
   * </ul>
   *
   * @param tiplavg
   * @return
   * @throws SQLException
   */
  private int[] numeroTitoli(Long tiplavg) throws SQLException {
    int[] numero = new int[2];
    numero[0] = 0;
    numero[1] = 0;

    // Conteggio delle categorie non archiviate associate ai titoli
    String selectCAIS_NonArchiviate = "select count(*) from cais where tiplavg = ? and titcat is not null and (isarchi is null or isarchi <> '1')";
    Long numero_NonArchiviate = (Long) this.sqlManager.getObject(selectCAIS_NonArchiviate, new Object[] { tiplavg });
    numero[0] = (numero_NonArchiviate != null) ? numero_NonArchiviate.intValue() : 0;

    // Conteggio delle categorie archiviate associate ai titoli
    String selectCAIS_Archiviate = "select count(*) from cais where tiplavg = ? and titcat is not null and isarchi is not null and isarchi = '1'";
    Long numero_Archiviate = (Long) this.sqlManager.getObject(selectCAIS_Archiviate, new Object[] { tiplavg });
    numero[1] = (numero_Archiviate != null) ? numero_Archiviate.intValue() : 0;

    return numero;

  }

  /**
   * Numero titoli (in funzione della tipologia) che contengono almeno una
   * categoria (di qualsiasi livello) associata.
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
   * Numero categorie annidate dal livello 0.
   *
   * Il metodo restituisce i conteggi delle categorie non archiviate ed
   * archiviate
   *
   * <ul>
   * <li>Posizione 0 - Numero categorie non archiviate</li>
   * <li>Posizione 1 - Numero categorie archiviate</li>
   * </ul>
   *
   * @param tiplavg
   * @param titcat
   * @return
   * @throws SQLException
   */
  private int[] numeroCategorieDaLivello0(Long tiplavg, String titcat) throws SQLException {
    int[] numero = new int[2];
    numero[0] = 0;
    numero[1] = 0;

    if (titcat == null || (titcat != null && "".equals(titcat.trim()))) {
      // Conteggio delle categoria non associate ad alcun titolo e non
      // archiviate
      String selectCAIS_NonArchiviate = "select count(*) from cais where tiplavg = ? and titcat is null and (isarchi is null or isarchi <> '1')";
      Long numero_NonArchiviate = (Long) this.sqlManager.getObject(selectCAIS_NonArchiviate, new Object[] { tiplavg });
      numero[0] = (numero_NonArchiviate != null) ? numero_NonArchiviate.intValue() : 0;

      // Conteggio delle categorie non associate ad alcun titolo e archiviate
      String selectCAIS_Archiviate = "select count(*) from cais where tiplavg = ? and titcat is null and isarchi is not null and isarchi = '1'";
      Long numero_Archiviate = (Long) this.sqlManager.getObject(selectCAIS_Archiviate, new Object[] { tiplavg });
      numero[1] = (numero_Archiviate != null) ? numero_Archiviate.intValue() : 0;

    } else {
      // Conteggio delle categorie associate ad un titolo e non archiviate
      String selectCAIS_NonArchiviate = "select count(*) from cais where tiplavg = ? and titcat = ? and (isarchi is null or isarchi <> '1')";
      Long numero_NonArchiviate = (Long) this.sqlManager.getObject(selectCAIS_NonArchiviate, new Object[] { tiplavg, titcat });
      numero[0] = (numero_NonArchiviate != null) ? numero_NonArchiviate.intValue() : 0;

      // Conteggio delle categorie associate ad un titolo e archiviate
      String selectCAIS_Archiviate = "select count(*) from cais where tiplavg = ? and titcat = ? and isarchi is not null and isarchi = '1'";
      Long numero_Archiviate = (Long) this.sqlManager.getObject(selectCAIS_Archiviate, new Object[] { tiplavg, titcat });
      numero[1] = (numero_Archiviate != null) ? numero_Archiviate.intValue() : 0;

    }
    return numero;
  }

  /**
   * Numero categorie annidate dal livello 1. Il metodo restituisce i conteggi
   * delle categorie non archiviate ed archiviate
   *
   * <ul>
   * <li>Posizione 0 - Numero categorie non archiviate</li>
   * <li>Posizione 1 - Numero categorie archiviate</li>
   * </ul>
   *
   * @param tiplavg
   * @param caisim_livello1
   * @return
   * @throws SQLException
   */
  private int[] numeroCategorieDaLivello1(Long tiplavg, String caisim_livello1) throws SQLException {
    int[] numero = new int[2];
    numero[0] = 0;
    numero[1] = 0;

    // Conteggio delle categorie non archiviate
    String selectCAIS_NonArchiviate = "select count(*) from cais where tiplavg = ? and codliv1 = ? and (isarchi is null or isarchi <> '1')";
    Long numero_NonArchiviate = (Long) this.sqlManager.getObject(selectCAIS_NonArchiviate, new Object[] { tiplavg, caisim_livello1 });
    numero[0] = (numero_NonArchiviate != null) ? numero_NonArchiviate.intValue() : 0;

    // Conteggio delle categorie archiviate
    String selectCAIS_Archiviate = "select count(*) from cais where tiplavg = ? and codliv1 = ? and isarchi is not null and isarchi = '1'";
    Long numero_Archiviate = (Long) this.sqlManager.getObject(selectCAIS_Archiviate, new Object[] { tiplavg, caisim_livello1 });
    numero[1] = (numero_Archiviate != null) ? numero_Archiviate.intValue() : 0;

    return numero;
  }

  /**
   * Numero categorie annidate dal livello 2.
   *
   * Il metodo restituisce i conteggi delle categorie non archiviate ed
   * archiviate
   *
   * <ul>
   * <li>Posizione 0 - Numero categorie non archiviate</li>
   * <li>Posizione 1 - Numero categorie archiviate</li>
   * </ul>
   *
   * @param tiplavg
   * @param caisim_livello1
   * @param caisim_livello2
   * @return
   * @throws SQLException
   */
  private int[] numeroCategorieDaLivello2(Long tiplavg, String caisim_livello1, String caisim_livello2) throws SQLException {
    int[] numero = new int[2];
    numero[0] = 0;
    numero[1] = 0;

    // Conteggio delle categorie non archiviate
    String selectCAIS_NonArchiviate = "select count(*) from cais where tiplavg = ? and codliv1 = ? and codliv2 = ? and (isarchi is null or isarchi <> '1')";
    Long numero_NonArchiviate = (Long) this.sqlManager.getObject(selectCAIS_NonArchiviate, new Object[] { tiplavg, caisim_livello1,
        caisim_livello2 });
    numero[0] = (numero_NonArchiviate != null) ? numero_NonArchiviate.intValue() : 0;

    // Conteggio delle categorie archiviate
    String selectCAIS_Archiviate = "select count(*) from cais where tiplavg = ? and codliv1 = ? and codliv2 = ? and isarchi is not null and isarchi = '1'";
    Long numero_Archiviate = (Long) this.sqlManager.getObject(selectCAIS_Archiviate, new Object[] { tiplavg, caisim_livello1,
        caisim_livello2 });
    numero[1] = (numero_Archiviate != null) ? numero_Archiviate.intValue() : 0;

    return numero;
  }

  /**
   * Numero categorie annidate dal livello 3.
   *
   * Il metodo restituisce i conteggi delle categorie non archiviate ed
   * archiviate
   *
   * <ul>
   * <li>Posizione 0 - Numero categorie non archiviate</li>
   * <li>Posizione 1 - Numero categorie archiviate</li>
   * </ul>
   *
   * @param tiplavg
   * @param caisim_livello1
   * @param caisim_livello2
   * @param caisim_livello3
   * @return
   * @throws SQLException
   */
  private int[] numeroCategorieDaLivello3(Long tiplavg, String caisim_livello1, String caisim_livello2, String caisim_livello3)
      throws SQLException {
    int[] numero = new int[2];
    numero[0] = 0;
    numero[1] = 0;

    // Conteggio delle categorie non archiviate
    String selectCAIS_NonArchiviate = "select count(*) from cais where tiplavg = ? and codliv1 = ? and codliv2 = ? and codliv3 = ? and (isarchi is null or isarchi <> '1')";
    Long numero_NonArchiviate = (Long) this.sqlManager.getObject(selectCAIS_NonArchiviate, new Object[] { tiplavg, caisim_livello1,
        caisim_livello2, caisim_livello3 });
    numero[0] = (numero_NonArchiviate != null) ? numero_NonArchiviate.intValue() : 0;

    // Conteggio delle categorie archiviate
    String selectCAIS_Archiviate = "select count(*) from cais where tiplavg = ? and codliv1 = ? and codliv2 = ? and codliv3 = ? and isarchi is not null and isarchi = '1'";
    Long numero_Archiviate = (Long) this.sqlManager.getObject(selectCAIS_Archiviate, new Object[] { tiplavg, caisim_livello1,
        caisim_livello2, caisim_livello3 });
    numero[1] = (numero_Archiviate != null) ? numero_Archiviate.intValue() : 0;

    return numero;
  }

  /**
   * Numero categorie annidate dal livello 4.
   *
   * Il metodo restituisce i conteggi delle categorie non archiviate ed
   * archiviate
   *
   * <ul>
   * <li>Posizione 0 - Numero categorie non archiviate</li>
   * <li>Posizione 1 - Numero categorie archiviate</li>
   * </ul>
   *
   * @param tiplavg
   * @param caisim_livello1
   * @param caisim_livello2
   * @param caisim_livello3
   * @param caisim_livello4
   * @return
   * @throws SQLException
   */
  private int[] numeroCategorieDaLivello4(Long tiplavg, String caisim_livello1, String caisim_livello2, String caisim_livello3,
      String caisim_livello4) throws SQLException {

    int[] numero = new int[2];
    numero[0] = 0;
    numero[1] = 0;

    // Conteggio delle categorie non archiviate
    String selectCAIS_NonArchiviate = "select count(*) from cais where tiplavg = ? and codliv1 = ? and codliv2 = ? and codliv3 = ? and codliv4 = ? and (isarchi is null or isarchi <> '1')";
    Long numero_NonArchiviate = (Long) this.sqlManager.getObject(selectCAIS_NonArchiviate, new Object[] { tiplavg, caisim_livello1,
        caisim_livello2, caisim_livello3, caisim_livello4 });
    numero[0] = (numero_NonArchiviate != null) ? numero_NonArchiviate.intValue() : 0;

    // Conteggio delle categorie archiviate
    String selectCAIS_Archiviate = "select count(*) from cais where tiplavg = ? and codliv1 = ? and codliv2 = ? and codliv3 = ? and codliv4 = ? and isarchi is not null and isarchi = '1'";
    Long numero_Archiviate = (Long) this.sqlManager.getObject(selectCAIS_Archiviate, new Object[] { tiplavg, caisim_livello1,
        caisim_livello2, caisim_livello3, caisim_livello4 });
    numero[1] = (numero_Archiviate != null) ? numero_Archiviate.intValue() : 0;

    return numero;
  }

  /**
   * Numero categorie annidate dal livello 0 ed associate.
   *
   * @param tiplavg
   * @param titcat
   * @param ngara
   * @return
   * @throws SQLException
   */
  private int numeroCategorieDaLivello0Associate(Long tiplavg, String titcat, String ngara) throws SQLException {
    Long numero = null;
    if (titcat == null) {
      String selectCAIS = "select count(*) from cais, opes where cais.tiplavg = ? and cais.titcat is null and cais.caisim = opes.catoff and opes.ngara3 = ?";
      numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, ngara });
    } else {
      String selectCAIS = "select count(*) from cais, opes where cais.tiplavg = ? and cais.titcat = ? and cais.caisim = opes.catoff and opes.ngara3 = ?";
      numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, titcat, ngara });
    }
    return (numero != null) ? numero.intValue() : 0;
  }

  /**
   * Numero categorie annidate dal livello 1 ed associate.
   *
   * @param tiplavg
   * @param caisim_livello1
   * @param ngara
   * @return
   * @throws SQLException
   */
  private int numeroCategorieDaLivello1Associate(Long tiplavg, String caisim_livello1, String ngara) throws SQLException {
    String selectCAIS = "select count(*) from cais, opes "
        + " where cais.tiplavg = ? "
        + " and cais.codliv1 = ? "
        + " and cais.caisim = opes.catoff "
        + " and opes.ngara3 = ?";
    Long numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, caisim_livello1, ngara });
    return (numero != null) ? numero.intValue() : 0;
  }

  /**
   * Numero categorie annidate dal livello 2 ed associate.
   *
   * @param tiplavg
   * @param caisim_livello1
   * @param caisim_livello2
   * @param ngara
   * @return
   * @throws SQLException
   */
  private int numeroCategorieDaLivello2Associate(Long tiplavg, String caisim_livello1, String caisim_livello2, String ngara)
      throws SQLException {
    String selectCAIS = "select count(*) from cais, opes "
        + " where cais.tiplavg = ? "
        + " and cais.codliv1 = ? "
        + " and cais.codliv2 = ? "
        + " and cais.caisim = opes.catoff "
        + " and opes.ngara3 = ?";
    Long numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, caisim_livello1, caisim_livello2, ngara });
    return (numero != null) ? numero.intValue() : 0;
  }

  /**
   * Numero categorie annidate dal livello 3 ed associate.
   *
   * @param tiplavg
   * @param caisim_livello1
   * @param caisim_livello2
   * @param caisim_livello3
   * @param ngara
   * @return
   * @throws SQLException
   */
  private int numeroCategorieDaLivello3Associate(Long tiplavg, String caisim_livello1, String caisim_livello2, String caisim_livello3,
      String ngara) throws SQLException {
    String selectCAIS = "select count(*) from cais, opes "
        + " where cais.tiplavg = ? "
        + " and cais.codliv1 = ? "
        + " and cais.codliv2 = ? "
        + " and cais.codliv3 = ? "
        + " and cais.caisim = opes.catoff "
        + " and opes.ngara3 = ?";
    Long numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, caisim_livello1, caisim_livello2, caisim_livello3,
        ngara });
    return (numero != null) ? numero.intValue() : 0;
  }

  /**
   * Numero categorie annidate dal livello 4 ed associate.
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
  private int numeroCategorieDaLivello4Associate(Long tiplavg, String caisim_livello1, String caisim_livello2, String caisim_livello3,
      String caisim_livello4, String ngara) throws SQLException {
    String selectCAIS = "select count(*) from cais, opes "
        + " where cais.tiplavg = ? "
        + " and cais.codliv1 = ? "
        + " and cais.codliv2 = ? "
        + " and cais.codliv3 = ? "
        + " and cais.codliv4 = ? "
        + " and cais.caisim = opes.catoff "
        + " and opes.ngara3 = ?";
    Long numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, caisim_livello1, caisim_livello2, caisim_livello3,
        caisim_livello4, ngara });
    return (numero != null) ? numero.intValue() : 0;
  }

  /**
   * Ricava la chiave (OPES.NOPEGA) della categoria associata.
   *
   * @param caisim
   * @param ngara
   * @return
   * @throws SQLException
   */
  private Long nopegaCategoriaAssociata(String caisim, String ngara) throws SQLException {
    String selectOPES = "select nopega from opes where ngara3 = ? and catoff = ?";
    return (Long) this.sqlManager.getObject(selectOPES, new Object[] { ngara, caisim });
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

  /**
   * Numero titoli contenenti categoria associate ad articoli.
   *
   * @param tiplavg
   * @param ngara
   * @param genere
   * @return
   * @throws SQLException
   */
  private int numeroArticoliDaTitolo(Long tiplavg, String ngara, String genere) throws SQLException {
    Long numero = null;
    if("20".equals(genere)){
      String selectCAIS = "select count(*) from cais, opes, meartcat "
          + " where cais.tiplavg = ? "
          + " and cais.titcat is not null "
          + " and cais.caisim = opes.catoff "
          + " and opes.ngara3 = ?"
          + " and meartcat.ngara = opes.ngara3 "
          + " and meartcat.nopega = opes.nopega";
      numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, ngara });
    }
    return (numero != null) ? numero.intValue() : 0;
  }

  /**
   * Numero di categorie (da livello 0) contenenti articoli.
   *
   * @param tiplavg
   * @param titcat
   * @param ngara
   * @param genere
   * @return
   * @throws SQLException
   */
  private int numeroArticoliDaLivello0(Long tiplavg, String titcat, String ngara, String genere) throws SQLException {
    Long numero = null;
    if("20".equals(genere)){
      if (titcat == null) {
        String selectCAIS = "select count(*) from cais, opes, meartcat"
            + " where cais.tiplavg = ? "
            + " and cais.titcat is null "
            + " and cais.caisim = opes.catoff "
            + " and opes.ngara3 = ? "
            + " and meartcat.ngara = opes.ngara3 "
            + " and meartcat.nopega = opes.nopega";
        numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, ngara });
      } else {
        String selectCAIS = "select count(*) from cais, opes, meartcat"
            + " where cais.tiplavg = ? "
            + " and cais.titcat = ? "
            + " and cais.caisim = opes.catoff "
            + " and opes.ngara3 = ? "
            + " and meartcat.ngara = opes.ngara3 "
            + " and meartcat.nopega = opes.nopega";
        numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, titcat, ngara });
      }
    }
    return (numero != null) ? numero.intValue() : 0;
  }

  /**
   * Numero di categorie (da livello 1) contenenti articoli.
   *
   * @param tiplavg
   * @param caisim_livello1
   * @param ngara
   * @param genere
   * @return
   * @throws SQLException
   */
  private int numeroArticoliDaLivello1(Long tiplavg, String caisim_livello1, String ngara, String genere) throws SQLException {
    Long numero = null;
    if("20".equals(genere)){
      String selectCAIS = "select count(*) from cais, opes, meartcat "
          + " where cais.tiplavg = ? "
          + " and cais.codliv1 = ? "
          + " and cais.caisim = opes.catoff "
          + " and opes.ngara3 = ? "
          + " and meartcat.ngara = opes.ngara3 "
          + " and meartcat.nopega = opes.nopega";
      numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, caisim_livello1, ngara });
    }
    return (numero != null) ? numero.intValue() : 0;
  }

  /**
   * Numero di categorie (da livello 2) contenenti articoli.
   *
   * @param tiplavg
   * @param caisim_livello1
   * @param caisim_livello2
   * @param ngara
   * @param genere
   * @return
   * @throws SQLException
   */
  private int numeroArticoliDaLivello2(Long tiplavg, String caisim_livello1, String caisim_livello2, String ngara, String genere) throws SQLException {
    Long numero = null;
    if("20".equals(genere)){
      String selectCAIS = "select count(*) from cais, opes, meartcat "
          + " where cais.tiplavg = ? "
          + " and cais.codliv1 = ? "
          + " and cais.codliv2 = ? "
          + " and cais.caisim = opes.catoff "
          + " and opes.ngara3 = ? "
          + " and meartcat.ngara = opes.ngara3 "
          + " and meartcat.nopega = opes.nopega";
      numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, caisim_livello1, caisim_livello2, ngara });
    }
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
   * @param genere
   * @return
   * @throws SQLException
   */
  private int numeroArticoliDaLivello3(Long tiplavg, String caisim_livello1, String caisim_livello2, String caisim_livello3, String ngara, String genere)
      throws SQLException {
    Long numero = null;
    if("20".equals(genere)){
      String selectCAIS = "select count(*) from cais, opes, meartcat "
          + " where cais.tiplavg = ? "
          + " and cais.codliv1 = ? "
          + " and cais.codliv2 = ? "
          + " and cais.codliv3 = ? "
          + " and cais.caisim = opes.catoff "
          + " and opes.ngara3 = ? "
          + " and meartcat.ngara = opes.ngara3 "
          + " and meartcat.nopega = opes.nopega";
      numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, caisim_livello1, caisim_livello2, caisim_livello3,
          ngara });
    }
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
   * @param genere
   * @return
   * @throws SQLException
   */
  private int numeroArticoliDaLivello4(Long tiplavg, String caisim_livello1, String caisim_livello2, String caisim_livello3,
      String caisim_livello4, String ngara, String genere) throws SQLException {
    Long numero = null;
    if("20".equals(genere)){
      String selectCAIS = "select count(*) from cais, opes, meartcat "
          + " where cais.tiplavg = ? "
          + " and cais.codliv1 = ? "
          + " and cais.codliv2 = ? "
          + " and cais.codliv3 = ? "
          + " and cais.codliv4 = ? "
          + " and cais.caisim = opes.catoff "
          + " and opes.ngara3 = ? "
          + " and meartcat.ngara = opes.ngara3 "
          + " and meartcat.nopega = opes.nopega";
      numero = (Long) this.sqlManager.getObject(selectCAIS, new Object[] { tiplavg, caisim_livello1, caisim_livello2, caisim_livello3,
          caisim_livello4, ngara });
    }
    return (numero != null) ? numero.intValue() : 0;
  }

  /**
   * Numero articoli (MEARTCAT) collegati alla categoria (OPES.CATOFF).
   *
   * @param caisim
   * @param ngara
   * @param genere
   * @return
   * @throws SQLException
   */
  private int numeroArticoli(String caisim, String ngara, String genere) throws SQLException {
    Long numero = null;
    if("20".equals(genere)){
      String selectMEARTCAT = "select count(*) from meartcat, opes where opes.ngara3 = ? and opes.catoff = ? and meartcat.ngara = opes.ngara3 and meartcat.nopega = opes.nopega";
      numero = (Long) this.sqlManager.getObject(selectMEARTCAT, new Object[] { ngara, caisim });
    }
    return (numero != null) ? numero.intValue() : 0;
  }

}
