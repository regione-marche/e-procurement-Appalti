/*
 * Created on 15-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pl.struts.gestori;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreIMPDTE;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreIMPLEG;
import it.eldasoft.sil.pl.bl.PlManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore che implementa le funzionalita di aggiornamento dati della tabella
 * APPA e sottotabelle derivate
 *
 * @author Marco.Franceschin
 */
public class GestoreAPPA extends AbstractGestoreChiaveNumerica {

  /** Logger */
  static Logger logger = Logger.getLogger(GestoreAPPA.class);

  @Override
  public String[] getAltriCampiChiave() {
    return new String[] { "CODLAV" };
  }

  @Override
  public String getCampoNumericoChiave() {
    return "NAPPAL";
  }

  @Override
  public String getEntita() {
    return "APPA";
  }

  private void aggiornaImportiSulContratto(DataColumnContainer impl) throws GestoreException {
    String campiContratto[] = { "CONT.IMPLORC", "CONT.IMPLORM", "CONT.IMPLORE" };
    String campiAppalto[] = { "APPA.IMPLAC", "APPA.IMPLAM", "APPA.IMPLAE" };
    for (int i = 0; i < campiContratto.length; i++) {
      if (impl.isColumn(campiContratto[i]) && impl.isColumn(campiAppalto[i])) {
        if (impl.isModifiedColumn(campiAppalto[i])) {
          impl.setValue(campiContratto[i], impl.getObject(campiAppalto[i]));
        }
      }
    }
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
    // Questo metodo viene richiamato da due punti diversi:
    // 1. creazione di un nuovo lavoro, con creazione del primo appalto ed
    // entita' figlie;
    // 2. creazione di un appalto successivo al primo
    // Il discriminante tra le due e' la presenza o meno nell'oggetto impl del
    // campo APPA.NAPPAL (il quale e' presente solante nel secondo caso)
    boolean isInserimentoNuovoAppalto = impl.isColumn("APPA.NAPPAL");
    if (isInserimentoNuovoAppalto) {
      // inserimento di un nuovo appalto dalla lista degli appalti o dalla
      // scheda di un appalto al quale si è acceduto dalla lista degli appalti
      impl.getColumn("APPA.CODLAV").setChiave(true);
      impl.getColumn("APPA.NAPPAL").setChiave(true);
      try {
        Vector resultSelect = this.sqlManager.getVector(
            "select CODPROE, INTERV, CATINT1, CLAINT, SETINT, TIPOPR from PERI where CODLAV = ? ",
            new Object[] { impl.getString("APPA.CODLAV") });
        if (resultSelect != null) {
          impl.addColumn("APPA.IVALAV", ((JdbcParametro) resultSelect.get(0)).doubleValue());
          impl.addColumn("APPA.IVARP", ((JdbcParametro) resultSelect.get(0)).doubleValue());

          PlManager plManager = (PlManager) UtilitySpring.getBean("plManager", this.getServletContext(), PlManager.class);
          String categoriaIntervento = plManager.calcoloCategoriaIntervento(((JdbcParametro) resultSelect.get(1)).getStringValue(),
              ((JdbcParametro) resultSelect.get(2)).getStringValue(), ((JdbcParametro) resultSelect.get(3)).getStringValue(),
              ((JdbcParametro) resultSelect.get(4)).getStringValue(), ((JdbcParametro) resultSelect.get(5)).getStringValue());
          impl.addColumn("APPA.CATINT", categoriaIntervento);
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nel determinare CODPROE e gli "
            + "importi del lavoro (INTERV, CATINT1, CLAINT, SETINT, TIPOPR) "
            + "per inizializzare i campi IVALAV, IVARP e CATINT", null, e);
      }
    } else {
      // inserimento del primo appalto in fase di creazione di un nuovo lavoro
      impl.addColumn("APPA.CODLAV", impl.getColumn("PERI.CODLAV"));
      impl.addColumn("APPA.CAPPAL", impl.getColumn("PERI.CODLAV"));
      impl.addColumn("APPA.NAPPAL", new Long(1));
      // titolo dell'appalto=titolo del lavoro
      impl.addColumn("APPA.NOTAPP", impl.getColumn("PERI.TITSIL"));
      if (!impl.isColumn("APPA.TIPLAVG")) {
        impl.addColumn("APPA.TIPLAVG", new Long(1));
      }
      Double iva = impl.getDouble("PERI.CODPROE");
      impl.addColumn("APPA.IVALAV", iva);
      impl.addColumn("APPA.IVARP", iva);
      impl.addColumn("APPA.TIAPPA", new Long(1));
      // verifico se il codice istat associato all'appalto è valorizzato. Se si
      // valorizzo la provincia e il comune
      if (impl.isColumn("APPA.LOCINT") && impl.getString("APPA.LOCINT") != null && !impl.getString("APPA.LOCINT").equals("")) {
        try {
          String queryProvincia = "select tabcod3 from tabsche where tabcod='S2003' and tabcod1='07' and tabcod2="
              + sqlManager.getDBFunction(
                  "concat",
                  new String[] { "'0'",
                      sqlManager.getDBFunction("substr", new String[] { "'" + impl.getString("APPA.LOCINT") + "'", "4", "3" }) });
          String queryComune = "select tabdesc from tabsche where tabcod='S2003' and tabcod1='09' and tabcod3='"
              + impl.getString("APPA.LOCINT")
              + "'";
          String provincia = (String) this.sqlManager.getObject(queryProvincia, new Object[] {});
          String comune = (String) this.sqlManager.getObject(queryComune, new Object[] {});
          impl.addColumn("APPA.PROLAV", provincia);
          impl.addColumn("APPA.COMLAV", comune);
        } catch (SQLException e) {
          throw new GestoreException("Errore durante l'inserimento del codice ISTAT in APPA", "insAPPA", e);
        }
      }
    }

    // Inizializzazioni comune di campi di APPA a prescindere se primo
    // appalto del lavoro o appalto successivo
    impl.addColumn("APPA.RITRP", new Long(15));
    impl.addColumn("APPA.ALEARP", new Long(10));
    impl.addColumn("APPA.RITINF", new Double(0.5));
    impl.addColumn("APPA.PERREA", new Long(10));

    if (impl.isColumn("APPA.CODCIG")
        && impl.isModifiedColumn("APPA.CODCIG")
        && impl.getColumn("APPA.CODCIG").getValue().stringValue() != null
        && !"".equals(impl.getColumn("APPA.CODCIG").getValue().stringValue())
        && impl.getColumn("APPA.CODCIG").getValue().stringValue().length() != 10)
      throw new GestoreException("Il codice CIG specificato non è valido", "controlloCodiceCIG");

    // Valorizzazione del campo NAPPAL con il max(NAPPAL) a parita' di CODLAV
    super.preInsert(status, impl);

    // Inserimento di APPA1
    impl.addColumn("APPA1.CODLAV", impl.getColumn("APPA.CODLAV"));
    impl.addColumn("APPA1.NAPPAL", impl.getColumn("APPA.NAPPAL"));
    try {
      impl.insert("APPA1", this.getSqlManager());
    } catch (SQLException e) {
      throw new GestoreException("Errore durante l'inserimento dei dati in APPA1", "insAPPA1", e);
    }

    if (isInserimentoNuovoAppalto) {
      // Valorizzazione del secondo campo chiave dell'entita CONT
      impl.setValue("CONT.CODLAV", impl.getString("APPA.CODLAV"));
      impl.getColumn("CONT.CODLAV").setChiave(true);
      impl.setValue("CONT.NAPPAL", impl.getLong("APPA.NAPPAL"));
      impl.getColumn("CONT.NAPPAL").setChiave(true);
    } else {
      // Inserimento del contratto
      impl.addColumn("CONT.CODLAV", impl.getColumn("PERI.CODLAV"));
      impl.addColumn("CONT.NAPPAL", new Long(1));
      impl.addColumn("CONT.NPROAT", new Long(1));
      impl.addColumn("CONT.CODAPP", new Long(1));
      Double iva = impl.getDouble("PERI.CODPROE");
      impl.addColumn("CONT.NESECO", iva);
    }
    // I campi CODAPP, NESECO sono stati inizializzati in fase di apertura della
    // scheda dell'appalto-contratto in inserimento
    aggiornaImportiSulContratto(impl);
    this.inserisci(status, impl, new GestoreCONT());

    // Salvataggio del riferimento dell'appalto ad una gara
    PlManager plManager = (PlManager) UtilitySpring.getBean("plManager", this.getServletContext(), PlManager.class);
    plManager.gestioneCollegamentoGara(impl);

    GestoreCATAPP.gestisciEntitaDaAppa(this.getRequest(), status, impl);
    GestoreULTAPPMultiplo.gestisciEntitaDaAppa(this.getRequest(), status, impl);

    gestioneDiretTecnicoLegRapp(status, impl);

    this.gestioneULTAPP_L(status, impl);

  }

  /**
   * Controllo se la somma degli importi delle categorie coincide con l'importo
   * appalto diminuito dell'importo degli oneri progettuali. Se no, viene
   * visualizzato un messaggio di warning ritornando alla scheda in
   * visualizzazione
   *
   * @param datiForm
   *        container di dati ricevuti dalla pagina di partenza
   * @param schema
   *        schema concettuale di riferimento per le categorie
   * @param campoImportoAppalto
   *        entita + "." + campo del campo importo appalto
   * @param campoImportoOneriProgettuali
   *        entita + "." + campo del campo importo oneri progettuali
   * @param nomeTabellaCategoria
   *        nome della tabella categoria prevalente dello schema concettuale
   * @param nomeTabellaUlterioriCategorie
   *        nome della tabella categorie ulteriori dello schema concettuale
   * @param geneManager
   *        manager di gene
   * @param request
   *        request http
   * @param OneriProgettualiVisibili
   *        booleano che indica se gli oneri di progettazione sono visibili nela
   *        pagina indipendentemente dalla gestione via profilo
   * @param categoriePresenti
   *        booleano che indica se nei messaggi si deve indicare
   *        "categorie"(true) oppure "prestazioni" (false)
   * @throws GestoreException
   */
  public static void checkSommaImportiCategorie(DataColumnContainer datiForm, String schema, String campoImportoAppalto,
      String campoImportoOneriProgettuali, String nomeTabellaCategoria, String nomeTabellaUlterioriCategorie, GeneManager geneManager,
      HttpServletRequest request, boolean OneriProgettualiVisibili, boolean categoriePresenti) throws GestoreException {

    if (geneManager.getProfili().checkProtec((String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "COLS", "VIS",
        schema + "." + campoImportoAppalto)
        && geneManager.getProfili().checkProtec((String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "COLS", "VIS",
            schema + "." + nomeTabellaCategoria + ".IMPBASG")) {

      double importoAppalto = datiForm.getDouble(campoImportoAppalto) != null ? datiForm.getDouble(campoImportoAppalto).doubleValue() : 0;
      double importoOneriProgettuali = datiForm.getDouble(campoImportoOneriProgettuali) != null ? datiForm.getDouble(
          campoImportoOneriProgettuali).doubleValue() : 0;
      double importoCategoriaPrevalente = datiForm.getDouble(nomeTabellaCategoria + ".IMPBASG") != null ? datiForm.getDouble(
          nomeTabellaCategoria + ".IMPBASG").doubleValue() : 0;
      double sommaImportiCategorieUlteriori = 0;

      int numeroCategorieUlteriori = datiForm.getLong("NUMERO_CATEGORIE").intValue();

      for (int i = 1; i <= numeroCategorieUlteriori; i++) {
        if (datiForm.isColumn(nomeTabellaUlterioriCategorie + ".DEL_ULTERIORE_CATEGORIA_" + i)
            && !"1".equals(datiForm.getString(nomeTabellaUlterioriCategorie + ".DEL_ULTERIORE_CATEGORIA_" + i))) {
          double importoUltCategoria = datiForm.getDouble(nomeTabellaUlterioriCategorie + ".IMPAPO_" + i) != null ? datiForm.getDouble(
              nomeTabellaUlterioriCategorie + ".IMPAPO_" + i).doubleValue() : 0;
          sommaImportiCategorieUlteriori += importoUltCategoria;
        }
      }

      double importoTotCategorie = sommaImportiCategorieUlteriori + importoCategoriaPrevalente;
      double importoTot = importoAppalto - importoOneriProgettuali;
      if (importoTotCategorie != 0
          && UtilityNumeri.confrontaDouble(importoTot, importoTotCategorie, 2)!=0) {
        String messageKey = null;
        if (geneManager.getProfili().checkProtec((String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "COLS",
            "VIS", schema + "." + campoImportoOneriProgettuali)
            && OneriProgettualiVisibili) {
          if (categoriePresenti)
            messageKey = "warnings.appalto.verificaImportoCategorie";
          else
            messageKey = "warnings.appalto.verificaImportoPrestazioni";
        } else {
          if (categoriePresenti)
            messageKey = "warnings.appalto.verificaImportoCategorie.campoONPRGE_NonVisibile";
          else
            messageKey = "warnings.appalto.verificaImportoPrestazioni.campoONPRGE_NonVisibile";
        }

        UtilityStruts.addMessage(request, "warning", messageKey,
            new Object[] { UtilityNumeri.convertiImporto(new Double(importoTotCategorie), 2),
                UtilityNumeri.convertiImporto(new Double(importoTot), 2) });
      }
    }
  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {
    // In inserimento la scheda è APPA-scheda
    preCheckSommaImportiCategorie(impl, "APPA");
  }

  /**
   * @param impl
   * @param nomeScheda
   * @throws GestoreException
   */
  private void preCheckSommaImportiCategorie(DataColumnContainer impl, String nomeScheda) throws GestoreException {
    // Se le sezioni 'Categoria prevalente' e 'Categoria ulteriore' sono
    // visualizzabili da profilo si esegue il controllo della somma degli
    // importi delle categorie prevalenti con l'importo netto contrattuale
    if (this.getGeneManager().getProfili().checkProtec(
        (String) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "SEZ", "VIS",
        "LAVO." + nomeScheda + "-scheda.APPACONTR.CATAPP")
        && this.getGeneManager().getProfili().checkProtec(
            (String) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "SEZ", "VIS",
            "LAVO." + nomeScheda + "-scheda.APPACONTR.ULTAPP")) {
      // Il controllo della somma degli importi delle categorie deve essere
      // effettuato solo se si e' modificata la pagina appalto-contratto e
      // non altre
      if (impl.isColumn("APPA.IMPLAV"))
        GestoreAPPA.checkSommaImportiCategorie(impl, "LAVO", "APPA.IMPLAV", "APPA.ONPRGE", "CATAPP", "ULTAPP", this.getGeneManager(),
            this.getRequest(), true, true);
    }
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
    PlManager manager = (PlManager) UtilitySpring.getBean("plManager", this.getServletContext(), PlManager.class);

    if (impl.isColumn("APPA.CODCIG")
        && impl.isModifiedColumn("APPA.CODCIG")
        && impl.getColumn("APPA.CODCIG").getValue().stringValue() != null
        && !"".equals(impl.getColumn("APPA.CODCIG").getValue().stringValue())
        && impl.getColumn("APPA.CODCIG").getValue().stringValue().length() != 10)
      throw new GestoreException("Il codice CIG specificato non è valido", "controlloCodiceCIG");

    // verifico se il codice istat associato all'appalto è valorizzato. Se si
    // valorizzo la provincia e il comune
    if (impl.isColumn("APPA.LOCINT") && impl.getString("APPA.LOCINT") != null && !impl.getString("APPA.LOCINT").equals("")) {
      try {
        String queryProvincia = "select tabcod3 from tabsche where tabcod='S2003' and tabcod1='07' and tabcod2="
            + sqlManager.getDBFunction(
                "concat",
                new String[] { "'0'",
                    sqlManager.getDBFunction("substr", new String[] { "'" + impl.getString("APPA.LOCINT") + "'", "4", "3" }) });
        String queryComune = "select tabdesc from tabsche where tabcod='S2003' and tabcod1='09' and tabcod3='"
            + impl.getString("APPA.LOCINT")
            + "'";
        String provincia = (String) this.sqlManager.getObject(queryProvincia, new Object[] {});
        String comune = (String) this.sqlManager.getObject(queryComune, new Object[] {});
        impl.addColumn("APPA.PROLAV", provincia);
        impl.addColumn("APPA.COMLAV", comune);
      } catch (SQLException e) {
        throw new GestoreException("Errore durante l'inserimento del codice ISTAT in APPA", "insAPPA", e);
      }
    }

    // Eseguo l'update del contratto attaccato
    if (impl.isColumn("CONT.CODLAV")) {
      aggiornaImportiSulContratto(impl);
      update(status, impl, new GestoreCONT());
    }

    // Se esiste il codice del lavoro per APPA1 allora eseguo l'update
    if (impl.isColumn("APPA1.CODLAV")) {
      try {
        if (impl.getString("APPA1.CODLAV") == null) {
          impl.setValue("APPA1.CODLAV", impl.getString("APPA.CODLAV"));
          impl.setValue("APPA1.NAPPAL", impl.getLong("APPA.NAPPAL"));

          impl.insert("APPA1", this.getSqlManager());

        } else {
          impl.update("APPA1", this.getSqlManager());
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'update/inserimento di APPA1", "updateAPPA1", e);
      }
    }
    // Se viene modificato implav allora lancio l'aggiornamento della categoria
    // prevalente
    gestioneCampoIMPLAV(impl, manager);

    // Verifica dell'unicita' del codice CUA
    verificaUnicitaCodCUA(impl);

    // Aggiornamento del campo CODINT dell'entita' LAVSCHE
    if (impl.isColumn("APPA.CODINT") && impl.isModifiedColumn("APPA.CODINT")) {
      try {
        getSqlManager().update("update LAVSCHE set CODINT = ? where CODLAV = ? and NAPPAL = ?",
            new Object[] { impl.getString("APPA.CODINT"), impl.getString("APPA.CODLAV"), impl.getLong("APPA.NAPPAL") });
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'update del CODINT in lavsche!", "updateLavscheCODINT");
      }
    }
    // Salvataggio del riferimento dell'appalto ad una gara
    PlManager plManager = (PlManager) UtilitySpring.getBean("plManager", this.getServletContext(), PlManager.class);
    plManager.gestioneCollegamentoGara(impl);

    GestoreCATAPP.gestisciEntitaDaAppa(this.getRequest(), status, impl);
    GestoreULTAPPMultiplo.gestisciEntitaDaAppa(this.getRequest(), status, impl);

    AbstractGestoreChiaveNumerica gestoreTEMP1 = new DefaultGestoreEntitaChiaveNumerica("TEMP1", "NPROVT", new String[] { "CODLAV",
        "NAPPAL" }, this.getRequest());

    // poichè per TEMP1 quando TIPVAR=3,4 si deve riportare su
    // GIORSO il valore di DURPRO, non si può adoperare la
    // gestisciAggiornamentiRecordSchedaMultipla.
    // Quindi il metodo che segue è una copia di
    // gestisciAggiornamentiRecordSchedaMultipla con in più
    // i controlli suddetti
    this.gestisciAggiornamentiRecordSchedaMultiplaTEMP1(status, impl, gestoreTEMP1, "VAR", new DataColumn[] {
        impl.getColumn("APPA.CODLAV"), impl.getColumn("APPA.NAPPAL") }, null);

    gestioneDiretTecnicoLegRapp(status, impl);

    // Si devono cancellare tutte le occorrenze della ragdet
    // che non sono legate alla ditta esecutrice
    if (impl.isColumn("APPA.NCODIM") && impl.isModifiedColumn("APPA.NCODIM")) {
      String ncodim = impl.getString("APPA.NCODIM");

      try {
        String cimpag = (String) sqlManager.getObject("select cimpag from appa where CODLAV = ? and NAPPAL = ?",
            new Object[] { impl.getString("APPA.CODLAV"), impl.getLong("APPA.NAPPAL") });

        String delete = "delete from RAGDET where CODLAV = ? and NAPPAL = ?";

        if (ncodim != null) {
          delete += " and CODIMP <> ?";

          if (cimpag != null) delete += " and CODIMP <> '" + cimpag + "'";
          getSqlManager().update(delete, new Object[] { impl.getString("APPA.CODLAV"), impl.getLong("APPA.NAPPAL"), ncodim });
        } else {
          if (cimpag != null) delete += " and CODIMP <> '" + cimpag + "'";
          getSqlManager().update(delete, new Object[] { impl.getString("APPA.CODLAV"), impl.getLong("APPA.NAPPAL") });
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'aggiornamento della RAGDET!", "deleteRAGDET");
      }
    }

    this.gestioneULTAPP_L(status, impl);


  }

  /**
   * @param impl
   * @param manager
   * @throws GestoreException
   */
  private void gestioneCampoIMPLAV(DataColumnContainer impl, PlManager manager) throws GestoreException {
    if (impl.isColumn("APPA.IMPLAV") && impl.getColumn("APPA.IMPLAV").isModified()) {
      manager.updateCategorieIscrizione(impl, impl.getString("APPA.CODLAV"), impl.getLong("APPA.NAPPAL"));
    }
  }

  /**
   * @param impl
   * @throws GestoreException
   */
  private void verificaUnicitaCodCUA(DataColumnContainer impl) throws GestoreException {
    if (impl.isColumn("APPA.CODCUA") && impl.isModifiedColumn("APPA.CODCUA")) {
      // Verifico che il codice cua sia univoco
      try {
        List ret = getSqlManager().getVector("select codlav, nappal from appa where ( codlav <> ? or nappal <> ? ) and codcua = ?",
            new Object[] { impl.getString("APPA.CODLAV"), impl.getLong("APPA.NAPPAL"), impl.getString("APPA.CODCUA") });
        if (ret != null && ret.size() > 0) {
          throw new GestoreException("Errore nella verifica univocità del codice CUA!", "verifyUnicitaCODCUA", new Object[] {
              ret.get(0).toString(), ret.get(1).toString() }, null);
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nella verifica univocità del codice CUA!", "verifyCODCUA", e);
      }
      try {
        getSqlManager().update("Update lavsche Set codcua = ? Where codlav = ? And nappal = ?",
            new Object[] { impl.getString("APPA.CODCUA"), impl.getString("APPA.CODLAV"), impl.getLong("APPA.NAPPAL") });
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'update del CODCUA in lavsche!", "updateLavscheCODCUA");
      }
    }
  }

  /**
   * @param status
   * @param impl
   * @throws GestoreException
   */
  private void gestioneDiretTecnicoLegRapp(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
    if (impl.isColumn("APPA.NCODIM")) {
      if (impl.getString("APPA.NCODIM") != null && !impl.getString("APPA.NCODIM").equalsIgnoreCase("")) {

        if (impl.getString("APPA.APCLEG") != null
            && !impl.getString("APPA.APCLEG").equalsIgnoreCase("")
            && impl.getString("APPA.APNLEG") != null
            && !impl.getString("APPA.APNLEG").equalsIgnoreCase("")) {
          try {
            // controllo l'esistenza del tecnico selezionato in archivio (con
            // la gestione dell'archivio si rischia che se si digita un filtro
            // quando c'è già qualcosa nei campi, cambiando tipo di filtro
            // sulla popup e chiudendola c'è rischio dati sporchi)
            String codtim = (String) getSqlManager().getObject("select codtim from teim where codtim = ? and nomtim = ?",
                new Object[] { impl.getString("APPA.APCLEG"), impl.getString("APPA.APNLEG") });
            if (codtim != null && !codtim.equalsIgnoreCase("")) {
              // controllo l'esistenza in db dell'occorrenza tra i legali
              // rappresentanti del tecnico selezionato
              String codleg = (String) getSqlManager().getObject("select codleg from impleg where codimp2 = ? and codleg = ?",
                  new Object[] { impl.getString("APPA.NCODIM"), codtim });
              if (codleg == null || codleg.equalsIgnoreCase(""))
              // se non esiste la inserisco
                GestoreIMPLEG.gestisciEntitaDaAppa(this.getRequest(), status, impl);
            } else {
              // se il codice e il nome del tecnico non ha riscontro
              // nell'archivio non devo salvare dati nemmeno su appa
              impl.getColumn("APPA.APCLEG").setValue(null);
              impl.getColumn("APPA.APNLEG").setValue(null);
            }
          } catch (SQLException e) {
            throw new GestoreException("Errore nella selezione da IMPLEG", "selectIMPLEG");
          }
        }
        if (impl.getString("APPA.APCDTE") != null
            && !impl.getString("APPA.APCDTE").equalsIgnoreCase("")
            && impl.getString("APPA.APNDTE") != null
            && !impl.getString("APPA.APNDTE").equalsIgnoreCase("")) {
          try {

            // controllo l'esistenza del tecnico selezionato in archivio (con
            // la gestione dell'archivio si rischia che se si digita un filtro
            // quando c'è già qualcosa nei campi, cambiando tipo di filtro
            // sulla popup e chiudendola c'è rischio dati sporchi)
            String codtim = (String) getSqlManager().getObject("select codtim from teim where codtim = ? and nomtim = ?",
                new Object[] { impl.getString("APPA.APCDTE"), impl.getString("APPA.APNDTE") });
            if (codtim != null && !codtim.equalsIgnoreCase("")) {
              // controllo l'esistenza in db dell'occorrenza tra i legali
              // rappresentanti del tecnico selezionato
              String coddte = (String) getSqlManager().getObject("select coddte from impdte where codimp3 = ? and coddte = ?",
                  new Object[] { impl.getString("APPA.NCODIM"), codtim });
              if (coddte == null || coddte.equalsIgnoreCase(""))
              // se non esiste la inserisco
                GestoreIMPDTE.gestisciEntitaDaAppa(this.getRequest(), status, impl);
            } else {
              // se il codice e il nome del tecnico non ha riscontro
              // nell'archivio
              // non devo salvare dati nemmeno su appa
              impl.getColumn("APPA.APCDTE").setValue(null);
              impl.getColumn("APPA.APNDTE").setValue(null);
            }
          } catch (SQLException e) {
            throw new GestoreException("Errore nella selezione da IMPDTE", "selectIMPDTE");
          }
        }
      } else {
        // se non è selezionata nessuna impresa sbianco tutti i campi di
        // direttori
        // tecnici e legali rappresentanti

        impl.getColumn("APPA.APCLEG").setValue(null);
        impl.getColumn("APPA.APNLEG").setValue(null);
        impl.getColumn("APPA.APCDTE").setValue(null);
        impl.getColumn("APPA.APNDTE").setValue(null);
      }
    }
  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postUpdate(it.eldasoft.gene.db.sql.sqlparser.DataColumnContainer)
   */
  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
    // In modifica la scheda è APPA-scheda se ...., altrimenti e ....
    String jspPath = UtilityStruts.getParametroString(this.getRequest(), UtilityTags.DEFAULT_HIDDEN_FORM_JSP_PATH);
    if (jspPath.indexOf("peri-scheda.jsp") >= 0)
      preCheckSommaImportiCategorie(impl, "PERI");
    else
      preCheckSommaImportiCategorie(impl, "APPA");
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
    PlManager plManager = (PlManager) UtilitySpring.getBean("plManager", this.getServletContext(), PlManager.class);
    String codiceLavoro = impl.getString("APPA.CODLAV");
    Long numeroAppalto = impl.getLong("APPA.NAPPAL");

    plManager.deleteAppalto(codiceLavoro, numeroAppalto);
  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }


  /**
   * Gestione categorie di iscrizione.
   * @param status
   * @param datiForm
   * @throws GestoreException
   */
  private void gestioneULTAPP_L(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    AbstractGestoreChiaveNumerica gestoreMultiploULTAPP_L = new DefaultGestoreEntitaChiaveNumerica("ULTAPP_L", "NOPEGA", new String[] {
        "CODLAV", "NAPPAL" }, this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm, gestoreMultiploULTAPP_L, "ULTAPP_L",
        new DataColumn[] { datiForm.getColumn("APPA.CODLAV"), datiForm.getColumn("APPA.NAPPAL") }, null);
  }


  // poichè per TEMP1 quando TIPVAR=3,4 si deve riportare su
  // GIORSO il valore di DURPRO, non si può adoperare la
  // gestisciAggiornamentiRecordSchedaMultipla.
  // Quindi il metodo che segue è una copia di
  // gestisciAggiornamentiRecordSchedaMultipla con in più
  // controlli suddetti
  public void gestisciAggiornamentiRecordSchedaMultiplaTEMP1(TransactionStatus status, DataColumnContainer dataColumnContainer,
      AbstractGestoreChiaveNumerica gestore, String suffisso, DataColumn[] valoreChiave, String[] campiDaNonAggiornare)
      throws GestoreException {

    String nomeCampoNumeroRecord = "NUMERO_VAR";
    String nomeCampoDelete = "DEL_VAR";
    String nomeCampoMod = "MOD_VAR";

    String isIntegrazioneContratti = ConfigManager.getValore("it.eldasoft.sil.pl.swcontratti.ws.url");
    isIntegrazioneContratti = UtilityStringhe.convertiNullInStringaVuota(isIntegrazioneContratti);


    // Gestione delle pubblicazioni bando solo se esiste la colonna con il
    // numero di occorrenze
    if (dataColumnContainer.isColumn(nomeCampoNumeroRecord)) {

      // Estraggo dal dataColumnContainer tutte le occorrenze dei campi
      // dell'entità definita per il gestore
      DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(dataColumnContainer.getColumns(gestore.getEntita(), 0));

      int numeroRecord = dataColumnContainer.getLong(nomeCampoNumeroRecord).intValue();

      for (int i = 1; i <= numeroRecord; i++) {
        DataColumnContainer newDataColumnContainer = new DataColumnContainer(tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

        boolean deleteOccorrenza = newDataColumnContainer.isColumn(nomeCampoDelete)
            && "1".equals(newDataColumnContainer.getString(nomeCampoDelete));
        boolean updateOccorrenza = newDataColumnContainer.isColumn(nomeCampoMod)
            && "1".equals(newDataColumnContainer.getString(nomeCampoMod));

        // Rimozione dei campi fittizi (il campo per la marcatura della delete e
        // tutti gli eventuali campi passati come argomento)
        newDataColumnContainer.removeColumns(new String[] { gestore.getEntita() + "." + nomeCampoDelete,
            gestore.getEntita() + "." + nomeCampoMod });

        if (campiDaNonAggiornare != null) {
          for (int j = 0; j < campiDaNonAggiornare.length; j++)
            campiDaNonAggiornare[j] = gestore.getEntita() + "." + campiDaNonAggiornare[j];
          newDataColumnContainer.removeColumns(campiDaNonAggiornare);
        }

        if (deleteOccorrenza) {
          // Se è stata richiesta l'eliminazione e il campo chiave numerica e'
          // diverso da null eseguo l'effettiva eliminazione del record
          if (newDataColumnContainer.getLong(gestore.getCampoNumericoChiave()) != null){
            gestore.elimina(status, newDataColumnContainer);
          }
          // altrimenti e' stato eliminato un nuovo record non ancora inserito
          // ma predisposto nel form per l'inserimento
        } else {
          if (updateOccorrenza) {
            // si settano tutti i campi chiave con i valori ereditati dal
            // chiamante
            for (int z = 0; z < gestore.getAltriCampiChiave().length; z++) {
              if (newDataColumnContainer.getColumn(gestore.getAltriCampiChiave()[z]).getValue().getValue() == null)
                newDataColumnContainer.getColumn(gestore.getAltriCampiChiave()[z]).setValue(valoreChiave[z].getValue());
            }
            if (newDataColumnContainer.isColumn("TEMP1.GIORSO")
                && newDataColumnContainer.isColumn("TEMP1.TIPVAR")
                && newDataColumnContainer.isColumn("TEMP1.DURPRO")) {
              Long tipvar = newDataColumnContainer.getLong("TEMP1.TIPVAR");
              if (tipvar != null && (tipvar.longValue() == 3 || tipvar.longValue() == 4)) {
                newDataColumnContainer.setValue("TEMP1.GIORSO", newDataColumnContainer.getObject("TEMP1.DURPRO"));
              }
            }
            if (newDataColumnContainer.getLong(gestore.getCampoNumericoChiave()) == null){
              gestore.inserisci(status, newDataColumnContainer);
            }else{
              gestore.update(status, newDataColumnContainer);
            }
          }
        }
      }
    }
  }
}
