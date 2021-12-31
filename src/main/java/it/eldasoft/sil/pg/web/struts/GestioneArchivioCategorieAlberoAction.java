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
import org.springframework.transaction.TransactionStatus;

/**
 * @author Stefano.Cestaro
 * 
 */
public class GestioneArchivioCategorieAlberoAction extends Action {

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
   * Operazioni disponibili:
   * <ul>
   * <li>load - caricamento dei dati</li>
   * <li>search - ricerca dei dati</li>
   * <li>delete - cancellazione di una categoria</li>
   * </ul>
   * 
   * L'operazione load restituisce un oggetto JSONArray strutturato nel seguente
   * modo:
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
   * <li>11 - Categoria archiviata ? (boolean, default FALSE)</li>
   * <li>12 - Categoria utilizzata ? (boolean, default FALSE)</li>
   * </ul>
   */
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String operation = request.getParameter("operation");

    JSONArray jsonArray = new JSONArray();

    if ("load".equals(operation)) {
      // Caricamento dei nodi dell'albero
      Long livello = null;
      if (request.getParameter("livello") != "") livello = new Long(request.getParameter("livello"));

      Long tiplavg = null;
      if (request.getParameter("tiplavg") != "") tiplavg = new Long(request.getParameter("tiplavg"));

      String titcat = request.getParameter("titcat");
      String caisim_livello1 = request.getParameter("caisim_livello1");
      String caisim_livello2 = request.getParameter("caisim_livello2");
      String caisim_livello3 = request.getParameter("caisim_livello3");
      String caisim_livello4 = request.getParameter("caisim_livello4");

      switch (livello.intValue()) {
      case -2:
        this.popolaRoot(jsonArray);
        break;

      case -1:
        this.popolaTitoli(tiplavg, jsonArray);
        this.popolaLivello0(tiplavg, null, jsonArray);
        break;

      case 0:
        this.popolaLivello0(tiplavg, titcat, jsonArray);
        break;

      case 1:
        this.popolaLivello1(tiplavg, titcat, caisim_livello1, jsonArray);
        break;

      case 2:
        this.popolaLivello2(tiplavg, titcat, caisim_livello1, caisim_livello2, jsonArray);
        break;

      case 3:
        this.popolaLivello3(tiplavg, titcat, caisim_livello1, caisim_livello2, caisim_livello3, jsonArray);
        break;

      case 4:
        this.popolaLivello4(tiplavg, titcat, caisim_livello1, caisim_livello2, caisim_livello3, caisim_livello4, jsonArray);
        break;

      default:
        break;
      }

    } else if ("search".equals(operation)) {
      // Ricerca
      String textsearch = request.getParameter("textsearch");
      this.searchCategoria(textsearch, jsonArray);
    } else if ("delete".equals(operation)) {
      // Cancellazione di una categoria
      String caisim = request.getParameter("caisim");
      this.deleteCategoria(caisim);
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
  private void searchCategoria(String textsearch_any, JSONArray jsonArray) throws SQLException {

    String[] words = textsearch_any.split(" ");

    for (int ia = 0; ia < words.length; ia++) {
      String textsearch = words[ia];
      if (textsearch != null && !"".equals(textsearch.trim())) {

        // Caratteri di escape
        textsearch = StringUtils.replace(textsearch, "_", "#_");
        textsearch = StringUtils.replace(textsearch, "%", "#%");
        textsearch = "%" + textsearch.toUpperCase() + "%";

        String selectCAIS = "select cais.tiplavg, cais.titcat, cais.codliv1, cais.codliv2, cais.codliv3, cais.codliv4, cais.caisim "
            + " from cais "
            + " where upper(cais.caisim) like ? escape '#' "
            + " or upper(cais.descat) like ? escape '#' "
            + " or exists (select tab5.tab5tip from tab5 where tab5.tab5cod = 'G_j05' and cais.titcat = tab5.tab5tip and upper(tab5.tab5desc) like ? escape '#') ";

        List<?> datiCAIS = this.sqlManager.getListVector(selectCAIS, new Object[] { textsearch, textsearch, textsearch });
        if (datiCAIS != null && datiCAIS.size() > 0) {
          for (int i = 0; i < datiCAIS.size(); i++) {
            Long tiplavg = (Long) SqlManager.getValueFromVectorParam(datiCAIS.get(i), 0).getValue();
            String titcat = (String) SqlManager.getValueFromVectorParam(datiCAIS.get(i), 1).getValue();
            String codliv1 = (String) SqlManager.getValueFromVectorParam(datiCAIS.get(i), 2).getValue();
            String codliv2 = (String) SqlManager.getValueFromVectorParam(datiCAIS.get(i), 3).getValue();
            String codliv3 = (String) SqlManager.getValueFromVectorParam(datiCAIS.get(i), 4).getValue();
            String codliv4 = (String) SqlManager.getValueFromVectorParam(datiCAIS.get(i), 5).getValue();
            String caisim = (String) SqlManager.getValueFromVectorParam(datiCAIS.get(i), 6).getValue();

            if (tiplavg != null) {
              jsonArray.add(new Object[] { tiplavg.toString(), "", "" });
              if (titcat != null) jsonArray.add(new Object[] { tiplavg.toString(), titcat, "" });
              if (codliv1 != null) jsonArray.add(new Object[] { tiplavg.toString(), titcat, codliv1 });
              if (codliv2 != null) jsonArray.add(new Object[] { tiplavg.toString(), titcat, codliv2 });
              if (codliv3 != null) jsonArray.add(new Object[] { tiplavg.toString(), titcat, codliv3 });
              if (codliv4 != null) jsonArray.add(new Object[] { tiplavg.toString(), titcat, codliv4 });
              if (caisim != null) jsonArray.add(new Object[] { tiplavg.toString(), titcat, caisim });
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
   * @throws SQLException
   */
  private void popolaRoot(JSONArray jsonArray) throws SQLException {

    String selectCategorie = "select tab1tip, tab1desc from tab1 where tab1cod = ? order by tab1nord, tab1tip";
    List<?> datiCategorie = this.sqlManager.getListVector(selectCategorie, new Object[] { "G_038" });
    if (datiCategorie != null && datiCategorie.size() > 0) {
      for (int i = 0; i < datiCategorie.size(); i++) {
        Long tiplavg = (Long) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 0).getValue();
        String titcat = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 1).getValue();

        int numTitoli[] = this.numeroTitoli(tiplavg);

        if (numTitoli[0] > 0 || numTitoli[1] > 0) {
          int[] numCategorie = this.numeroCategorieDaLivello0(tiplavg, null);

          int numCategorieNonArchiviate = numTitoli[0] + numCategorie[0];
          int numCategorieArchiviate = numTitoli[1] + numCategorie[1];

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
          row[9] = numCategorieNonArchiviate;
          row[10] = numCategorieArchiviate;
          row[11] = false;
          row[12] = false;

          jsonArray.add(row);

        } else {
          int num[] = this.numeroCategorieDaLivello0(tiplavg, null);
          int numCategorieNonArchiviate = num[0];
          int numCategorieArchiviate = num[1];

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
          row[9] = numCategorieNonArchiviate;
          row[10] = numCategorieArchiviate;
          row[11] = false;
          row[12] = false;

          jsonArray.add(row);

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
  private void popolaTitoli(Long tiplavg, JSONArray jsonArray) throws SQLException {

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
        row[9] = numCategorieNonArchiviate;
        row[10] = numCategorieArchiviate;
        row[11] = false;
        row[12] = false;

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
   * @throws SQLException
   */
  private void popolaLivello0(Long tiplavg, String titcat, JSONArray jsonArray) throws SQLException {

    List<?> datiCategorie = null;

    if (titcat == null || (titcat != null && "".equals(titcat.trim()))) {
      String selectCategorie = "select caisim, descat, isarchi from cais "
          + " where tiplavg = ? "
          + " and titcat is null "
          + " and codliv1 is null "
          + " and codliv2 is null "
          + " and codliv3 is null "
          + " and codliv4 is null "
          + " order by caisim";
      datiCategorie = this.sqlManager.getListVector(selectCategorie, new Object[] { tiplavg });
    } else {
      String selectCategorie = "select caisim, descat, isarchi from cais "
          + " where tiplavg = ? "
          + " and titcat = ? "
          + " and codliv1 is null "
          + " and codliv2 is null "
          + " and codliv3 is null "
          + " and codliv4 is null "
          + " order by caisim";
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

        Object[] row = new Object[13];
        row[0] = "1";
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
        row[11] = (isarchi != null && "1".equals(isarchi)) ? true : false;
        row[12] = this.isCategoriaUtilizzata(caisim);

        jsonArray.add(row);

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
  private void popolaLivello1(Long tiplavg, String titcat, String caisim_livello1, JSONArray jsonArray) throws SQLException {

    String selectCategorie = "select caisim, descat, isarchi from cais "
        + " where tiplavg = ? "
        + " and codliv1 = ? "
        + " and codliv2 is null "
        + " and codliv3 is null "
        + " and codliv4 is null "
        + " order by caisim";
    List<?> datiCategorie = this.sqlManager.getListVector(selectCategorie, new Object[] { tiplavg, caisim_livello1 });

    if (datiCategorie != null && datiCategorie.size() > 0) {
      for (int i = 0; i < datiCategorie.size(); i++) {
        String caisim = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 0).getValue();
        String descat = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 1).getValue();
        String isarchi = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 2).getValue();

        int num[] = this.numeroCategorieDaLivello2(tiplavg, caisim_livello1, caisim);
        int numCategorieNonArchiviate = num[0];
        int numCategorieArchiviate = num[1];

        Object[] row = new Object[13];
        row[0] = "2";
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
        row[11] = (isarchi != null && "1".equals(isarchi)) ? true : false;
        row[12] = this.isCategoriaUtilizzata(caisim);

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
   * @param jsonArray
   * @throws SQLException
   */
  private void popolaLivello2(Long tiplavg, String titcat, String caisim_livello1, String caisim_livello2, JSONArray jsonArray)
      throws SQLException {

    String selectCategorie = "select caisim, descat, isarchi from cais "
        + " where tiplavg = ? "
        + " and codliv1 = ? "
        + " and codliv2 = ? "
        + " and codliv3 is null "
        + " and codliv4 is null "
        + " order by caisim";
    List<?> datiCategorie = this.sqlManager.getListVector(selectCategorie, new Object[] { tiplavg, caisim_livello1, caisim_livello2 });
    if (datiCategorie != null && datiCategorie.size() > 0) {
      for (int i = 0; i < datiCategorie.size(); i++) {
        String caisim = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 0).getValue();
        String descat = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 1).getValue();
        String isarchi = (String) SqlManager.getValueFromVectorParam(datiCategorie.get(i), 2).getValue();

        int num[] = this.numeroCategorieDaLivello3(tiplavg, caisim_livello1, caisim_livello2, caisim);
        int numCategorieNonArchiviate = num[0];
        int numCategorieArchiviate = num[1];

        Object[] row = new Object[13];
        row[0] = "3";
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
        row[11] = (isarchi != null && "1".equals(isarchi)) ? true : false;
        row[12] = this.isCategoriaUtilizzata(caisim);

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
   * @param jsonArray
   * @throws SQLException
   */
  private void popolaLivello3(Long tiplavg, String titcat, String caisim_livello1, String caisim_livello2, String caisim_livello3,
      JSONArray jsonArray) throws SQLException {
    String selectCategorie = "select caisim, descat, isarchi from cais "
        + " where tiplavg = ? "
        + " and codliv1 = ? "
        + " and codliv2 = ? "
        + " and codliv3 = ? "
        + " and codliv4 is null "
        + " order by caisim";
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

        Object[] row = new Object[13];
        row[0] = "4";
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
        row[11] = (isarchi != null && "1".equals(isarchi)) ? true : false;
        row[12] = this.isCategoriaUtilizzata(caisim);

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
   * @param jsonArray
   * @return
   * @throws SQLException
   */
  private int popolaLivello4(Long tiplavg, String titcat, String caisim_livello1, String caisim_livello2, String caisim_livello3,
      String caisim_livello4, JSONArray jsonArray) throws SQLException {
    int numero = 0;
    String selectCategorie = "select caisim, descat, isarchi from cais "
        + " where tiplavg = ? "
        + " and codliv1 = ? "
        + " and codliv2 = ? "
        + " and codliv3 = ? "
        + " and codliv4 = ? "
        + " order by caisim";
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

        Object[] row = new Object[13];
        row[0] = "5";
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
        row[11] = (isarchi != null && "1".equals(isarchi)) ? true : false;
        row[12] = this.isCategoriaUtilizzata(caisim);

        jsonArray.add(row);

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
   * Verifica se la categoria e' utilizzata in qualche tabella figlia.
   * 
   * @param caisim
   * @return
   * @throws SQLException
   */
  private boolean isCategoriaUtilizzata(String caisim) throws SQLException {
    boolean isCategoriaUtilizzata = false;
    String tabella[] = { "CATE", "ARCHDOCG", "OPES", "CATG", "ISCRIZCAT", "ISCRIZCLASSI", "CATAPP", "ULTAPP", "SUBA", "CPRIGHE", "CNRIGHE" };
    String fktabella[] = { "CATISC", "CATEGORIA", "CATOFF", "CATIGA", "CODCAT", "CODCAT", "CATIGA", "CATOFF", "CATLAV", "CATIGA", "CATEG" };

    // Selezione di tutte le categorie collegate
    String selectCAIS = "select caisim from cais where caisim = ? or codliv1 = ? or codliv2 = ? or codliv3 = ? or codliv4 = ?";
    List<?> datiCAIS = this.sqlManager.getListVector(selectCAIS, new Object[] { caisim, caisim, caisim, caisim, caisim });

    if (datiCAIS != null && datiCAIS.size() > 0) {

      for (int c = 0; c < datiCAIS.size() && !isCategoriaUtilizzata; c++) {
        String codicecategoria = (String) SqlManager.getValueFromVectorParam(datiCAIS.get(c), 0).getValue();

        for (int t = 0; t < tabella.length && !isCategoriaUtilizzata; t++) {
          if (this.sqlManager.isTable(tabella[t])) {
            String sql = "select count(*) from " + tabella[t] + " where " + fktabella[t] + " = ?";
            Long conta = (Long) this.sqlManager.getObject(sql, new Object[] { codicecategoria });
            if (conta != null && conta.longValue() > 0) isCategoriaUtilizzata = true;
          }
        }

      }
    }
    return isCategoriaUtilizzata;
  }

  /**
   * Cancellazione della categoria e di tutte le categoria figlie.
   * 
   * @param caisim
   * @throws SQLException
   */
  private void deleteCategoria(String caisim) throws SQLException {
    if (!this.isCategoriaUtilizzata(caisim)) {
      TransactionStatus status = null;
      boolean commitTransaction = false;
      try {
        status = this.sqlManager.startTransaction();
        this.sqlManager.update("delete from cais where caisim = ? or codliv1 = ? or codliv2 = ? or codliv3 = ? or codliv4 = ?",
            new Object[] { caisim, caisim, caisim, caisim, caisim });
        commitTransaction = true;
      } catch (Exception e) {
        commitTransaction = false;
      } finally {
        if (status != null) {
          if (commitTransaction) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        }
      }
    }
  }
}
