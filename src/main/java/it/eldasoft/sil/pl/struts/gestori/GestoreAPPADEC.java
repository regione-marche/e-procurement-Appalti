package it.eldasoft.sil.pl.struts.gestori;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.sil.pl.bl.PlManager;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

public class GestoreAPPADEC extends AbstractGestoreEntita {

  static Logger            logger           = Logger.getLogger(GestoreAPPADEC.class);

  private PlManager        plmanager        = null;

  private TabellatiManager tabellatiManager = null;

  // Gestore fittizio. Le operazioni di insert e delete devono essere eseguite
  // esplicitamente in questo gestore.
  public GestoreAPPADEC() {
    super(false);
  }

  @Override
  public String getEntita() {
    return "APPA";
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    plmanager = (PlManager) UtilitySpring.getBean("plManager", this.getServletContext(), PlManager.class);
    tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", this.getServletContext(), TabellatiManager.class);
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

    String lavoro_esistente = impl.getColumn("LAVORO_ESISTENTE").getValue().getStringValue();
    if (lavoro_esistente != null && "2".equals(lavoro_esistente)) {
      // Se la decisione è quella di creare un nuovo contratto non collegato ad
      // un lavoro esistente è necessario creare anche una nuova occorrenza di
      // PERI. Di conseguenza, se la codifica non è automatica, è necessario
      // copiare il codice inserito dall'utente al campo APPA.CODLAV nel campo
      // PERI.CODLAV. Analogamente deve essere copiata la descrizione del
      // contratto APPA.NOTAPP nel campo PERI.TITSIL.
      if (!geneManager.isCodificaAutomatica("PERI", "CODLAV")) {
        impl.setValue("PERI.CODLAV", impl.getString("APPA.CODLAV"));
        //Si deve controllare se il codice specificato corrisponde al lavoro già esistente
        try {
          Long conteggio = (Long)this.sqlManager.getObject("select count(codlav) from peri where codlav=?", new Object[]{impl.getString("APPA.CODLAV")});
          if(conteggio!=null && conteggio.longValue()>0)
            throw new GestoreException("Il codice del contratto specificato è già esistente", "errors.gestoreException.*.dec.lavoroDuplicato", new Exception());
        } catch (SQLException e) {
          throw new GestoreException("Errore nella lettura della tabella PERI", null, e);
        }
      }
      impl.setValue("PERI.TITSIL", impl.getString("APPA.NOTAPP"));
      this.inserisciNuovoContratto(status, impl);
    } else {
      this.inserisciContrattoSuLavoroEsistente(status, impl);
    }

    // Rilettura delle chiavi
    if (impl.isColumn("GARE.NGARA")) {
      String ngara = impl.getString("GARE.NGARA");
      if (ngara != null && !"".equals(ngara)) {
        String codlav = impl.getString("APPA.CODLAV");
        Long nappal = impl.getLong("APPA.NAPPAL");
        Long ncont = impl.getLong("NCONT");
        this.aggiornaIntegrazione(ngara, codlav, nappal, "INS", ncont);
      }
    }

  }

  /**
   * Inserimento di un nuovo contratto ASSOCIATO ad un lavoro esistente. In
   * questo caso di deve aggiungere una nuova occorrenza di APPA. E' come
   * aggiungere un nuovo appalto/contratto dalla lista degli appalti del lavoro.
   * Non deve essere effettuata alcuna operazione sulla tabella PERI perche' il
   * lavoro esiste gia' in banca dati.
   *
   * @param status
   * @param impl
   * @throws GestoreException
   */
  private void inserisciContrattoSuLavoroEsistente(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

    impl.addColumn("APPA.CODLAV", impl.getColumn("PERI.CODLAV"));
    impl.addColumn("CONT.CODAPP", JdbcParametro.TIPO_NUMERICO, new Long(1));
    impl.addColumn("CONT.CODLAV", JdbcParametro.TIPO_TESTO, null);
    impl.addColumn("CONT.NAPPAL", JdbcParametro.TIPO_NUMERICO, null);
    impl.addColumn("CONT.NPROAT", JdbcParametro.TIPO_NUMERICO, null);

    try {
      Double iva = (Double) this.sqlManager.getObject("select codproe from peri where codlav = ?",
          new Object[] { impl.getColumn("PERI.CODLAV").toString() });
      impl.addColumn("CONT.NESECO", iva);
    } catch (SQLException e) {
      // Nessuna operazione...
    }
    Long tiplavg = impl.getLong("APPA.TIPLAVG");
    if (new Long(1).equals(tiplavg)) {
      impl.addColumn("APPA.ISABSUBA", "1");
      impl.addColumn("APPA.ISABATTI", "1");
      impl.addColumn("APPA.ISABCOLL", "1");
      impl.addColumn("APPA.ISABRIVA", "2");
      impl.addColumn("APPA.ISABQUEC", "1");
      impl.addColumn("APPA.TIPOCONT", new Long(1));
    } else {
      impl.addColumn("APPA.ISABSUBA", "2");
      impl.addColumn("APPA.ISABATTI", "2");
      impl.addColumn("APPA.ISABCOLL", "2");
      impl.addColumn("APPA.ISABRIVA", "2");
      impl.addColumn("APPA.ISABQUEC", "2");
      impl.addColumn("APPA.TIPOCONT", new Long(2));
    }

    this.inserisci(status, impl, new GestoreAPPA());
  }

  /**
   * Inserimento di un nuovo contratto NON ASSOCIATO ad un lavoro esistente. In
   * questo caso oltre al contratto (occorrenza di APPA) deve essere creata
   * anche l'occorrenza di PERI. Questa occorrenza di PERI diventa "fittizia"
   * (PERI.FORNSERV = '1') e non visibile da lavori se il contratto è un puro
   * contratto di forniture o servizi (APPA.TIPLAVG = 2 o 3). E' invece
   * effettivamente un lavoro (PERI.FORNSERV = '2') se il contratto è per lavori
   * (APPA.TIPLAVG = 1). In quest'ultimo caso l'occorrenza di PERI continuera'
   * ad essere visibile anche da lavori.
   *
   * @param status
   * @param impl
   * @throws GestoreException
   */
  private void inserisciNuovoContratto(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

    if (geneManager.isCodificaAutomatica("PERI", "CODLAV")) {
      impl.getColumn("PERI.CODLAV").setChiave(true);
      impl.setValue("PERI.CODLAV", geneManager.calcolaCodificaAutomatica("PERI", "CODLAV"));
    }

    impl.addColumn("PERI.LIVPRO", JdbcParametro.TIPO_NUMERICO, new Long(3));
    impl.addColumn("PERI.RIGIVA", JdbcParametro.TIPO_NUMERICO, new Long(12));
    impl.addColumn("PERI.RIGESP", JdbcParametro.TIPO_NUMERICO, new Long(5));
    impl.addColumn("PERI.RIGIMP", JdbcParametro.TIPO_NUMERICO, new Long(4));
    impl.addColumn("PERI.RIGRPZ", JdbcParametro.TIPO_NUMERICO, new Long(6));
    impl.addColumn("PERI.CODPROE", JdbcParametro.TIPO_DECIMALE, this.plmanager.getTabellatoIVA());

    Long tiplavg = impl.getLong("APPA.TIPLAVG");
    if (new Long(1).equals(tiplavg)) {
      impl.addColumn("PERI.FORNSERV", JdbcParametro.TIPO_TESTO, "2");
    } else {
      impl.addColumn("PERI.FORNSERV", JdbcParametro.TIPO_TESTO, "1");
    }

    try {
      List<?> datiTAB1 = this.getSqlManager().getListVector("Select tab1desc From tab1 Where tab1cod = ? order by tab1tip",
          new Object[] { "A2032" });
      String campo = null;
      for (int i = 0; i < datiTAB1.size(); i++) {
        campo = "PERI.DSOMC" + String.valueOf(i + 1);
        impl.addColumn(campo, JdbcParametro.TIPO_TESTO, ((Vector<?>) datiTAB1.get(i)).get(0));
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore durante l'inizializzazione delle somme a disposizione", "initLavoro.somme", e);
    }
    AbstractGestoreEntita gestorePERI = new DefaultGestoreEntita("PERI", this.getRequest());
    this.inserisci(status, impl, gestorePERI);

    // Preparazione dati ed inserimento di APPR
    impl.addColumn("APPR.CODLAV", impl.getColumn("PERI.CODLAV"));
    impl.addColumn("APPR.NAPRPR", new Long(1));
    impl.addColumn("APPR.TIPPER", new Long(1));
    this.inserisci(status, impl, new GestoreAPPR());

    // Preparazione dati ed inserimento di APPA. Per forzare il gestore di APPA
    // all'inserimento di una nuova (e prima) occorrenza è necessario rimuovere
    // il campo APPA.NAPPAL. Il gestore di APPA, nell'inserimento del primo
    // appalto, non si aspetta questo campo ma lo crea con l'istruzione
    // addColumn
    impl.removeColumns(new String[] { "APPA.NAPPAL" });

    if (new Long(1).equals(tiplavg)) {
      impl.addColumn("APPA.ISABSUBA", "1");
      impl.addColumn("APPA.ISABATTI", "1");
      impl.addColumn("APPA.ISABCOLL", "1");
      impl.addColumn("APPA.ISABRIVA", "2");
      impl.addColumn("APPA.ISABQUEC", "1");
      impl.addColumn("APPA.TIPOCONT", new Long(1));
    } else {
      impl.addColumn("APPA.ISABSUBA", "2");
      impl.addColumn("APPA.ISABATTI", "2");
      impl.addColumn("APPA.ISABCOLL", "2");
      impl.addColumn("APPA.ISABRIVA", "2");
      impl.addColumn("APPA.ISABQUEC", "2");
      impl.addColumn("APPA.TIPOCONT", new Long(2));
    }
    this.inserisci(status, impl, new GestoreAPPA());

    // Gestione permessi e proprietà del nuovo lavoro
    this.inserisciPermessi(impl, "CODLAV", new Integer(1));
  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

    String ngara = impl.getString("GARE.NGARA");
    if (ngara != null && !"".equals(ngara)) {
      String codlav = impl.getString("APPA.CODLAV");
      Long nappal = impl.getLong("APPA.NAPPAL");
      Long ncont = impl.getLong("NCONT");
      this.aggiornaIntegrazione(ngara, codlav, nappal, "UPD",ncont);
    }
  }

  /**
   * Inserimento/aggiornamento dei campi coinvolti nell'integrazione
   * gare/lavori/dec.
   *
   * @param ngara
   * @param codlav
   * @param nappal
   * @param tipo
   *        (INS: inserimento, UPD: aggiornamento)
   * @throws GestoreException
   */
  private void aggiornaIntegrazione(String ngara, String codlav, Long nappal, String tipo, Long ncont) throws GestoreException {
    try {

      // Aggiornamento di PERI
      DataColumnContainer dccPERI = new DataColumnContainer(this.sqlManager, "PERI",
          "select codlav, livpro, cupprg, cupmst, cenint, cuiint, lavprogtri  from peri where codlav = ?", new Object[] { codlav });
      dccPERI.getColumn("PERI.CODLAV").setChiave(true);
      dccPERI.setValue("PERI.CODLAV", codlav);
      this.dccPERI_AltriDati(dccPERI, ngara, nappal);
      dccPERI.update("PERI", this.sqlManager);

      // Aggiornamento di APPA
      DataColumnContainer dccAPPA = new DataColumnContainer(this.sqlManager, "APPA", "select * from appa where codlav = ? and nappal = ?",
          new Object[] { codlav, nappal });
      dccAPPA.getColumn("APPA.CODLAV").setChiave(true);
      dccAPPA.getColumn("APPA.NAPPAL").setChiave(true);
      dccAPPA.setValue("APPA.CODLAV", codlav);
      dccAPPA.setValue("APPA.NAPPAL", nappal);
      dccAPPA.setValue("APPA.NAPPAL", nappal);
      dccAPPA.setValue("APPA.FASEAPPALTO", "E");
      this.dccAPPA_AltriDati(dccAPPA, ngara, ncont);
      dccAPPA.update("APPA", this.sqlManager);

      // Gestione categoria di iscrizione principale
      boolean isAggiornamentoCATAPP = this.richiestoAggiornamentoCATAPP(ngara, codlav, nappal);
      if (isAggiornamentoCATAPP == true) {
        this.gestioneCATAPP(ngara, codlav, nappal);
      }

      // Gestione categorie di iscrizione ulteriori
      boolean isAggiornamentoULTAPP = this.richiestoAggiornamentoULTAPP(ngara, codlav, nappal);
      if (isAggiornamentoULTAPP == true) {
        this.gestioneULTAPP(ngara, codlav, nappal);
      }

      // Gestione dei CIG
      boolean isAggiornamentoAPPACIG = this.richiestoAggiornamentoAPPACIG(ngara, codlav, nappal);
      if (isAggiornamentoAPPACIG == true) {
        //Viene richiamata solo nel caso di modcont=2, poichè nel metodo richiestoAggiornamentoAPPACIG i controlli
        //scattano solo per modcont=2
        this.gestioneAPPACIG(ngara, codlav, nappal, ncont);
      }

      // Gestione ditte consorziate
      this.gestioneRAGDET(ngara, codlav, nappal);

      // Aggiornamento di APPA1
      //Si deve controllare se esiste l'occorrenza in APPA1, se non esiste la si crea
      Long conteggio = (Long)this.sqlManager.getObject("select count(codlav) from appa1 where codlav = ? and nappal = ?", new Object[] { codlav, nappal });
      if(conteggio==null || (conteggio!=null && conteggio.longValue()==0))
        this.sqlManager.update("insert into appa1(codlav, nappal) values(?,?)", new Object[] { codlav, nappal });
      DataColumnContainer dccAPPA1 = new DataColumnContainer(this.sqlManager, "APPA1",
          "select codlav, nappal, navvigg from appa1 where codlav = ? and nappal = ?", new Object[] { codlav, nappal });
      dccAPPA1.getColumn("APPA1.CODLAV").setChiave(true);
      dccAPPA1.getColumn("APPA1.NAPPAL").setChiave(true);
      dccAPPA1.setValue("APPA1.CODLAV", codlav);
      dccAPPA1.setValue("APPA1.NAPPAL", nappal);
      this.dccAPPA1_AltriDati(dccAPPA1, ngara);
      dccAPPA1.update("APPA1", this.sqlManager);

      // Aggiornamento di CONT
      DataColumnContainer dccCONT = new DataColumnContainer(this.sqlManager, "CONT",
          "select * from cont where codlav = ? and nappal = ? and nproat = ?", new Object[] { codlav, nappal, new Long(1) });
      dccCONT.getColumn("CONT.CODLAV").setChiave(true);
      dccCONT.getColumn("CONT.NAPPAL").setChiave(true);
      dccCONT.getColumn("CONT.NPROAT").setChiave(true);
      dccCONT.setValue("CONT.CODLAV", codlav);
      dccCONT.setValue("CONT.NAPPAL", nappal);
      dccCONT.setValue("CONT.NPROAT", new Long(1));
      if(this.dccCONT_AltriDati(dccCONT, ngara, ncont, dccAPPA))
        dccAPPA.update("APPA", this.sqlManager);
      dccCONT.update("CONT", this.sqlManager);

      // Controllo aggiornamento
      if (dccPERI.isModifiedTable("PERI")
          || dccAPPA.isModifiedTable("APPA")
          || dccAPPA1.isModifiedTable("APPA1")
          || dccCONT.isModifiedTable("CONT")
          || isAggiornamentoCATAPP == true
          || isAggiornamentoULTAPP == true
          || isAggiornamentoAPPACIG == true) {

        // Ricavo lista dei campi modificati
        String listaCampiModificati = "";
        listaCampiModificati = this.getListaCampiModificati(dccPERI, "PERI", listaCampiModificati);
        listaCampiModificati = this.getListaCampiModificati(dccAPPA, "APPA", listaCampiModificati);
        listaCampiModificati = this.getListaCampiModificati(dccAPPA1, "APPA1", listaCampiModificati);
        listaCampiModificati = this.getListaCampiModificati(dccCONT, "CONT", listaCampiModificati);

        if (isAggiornamentoCATAPP == true) {
          if (!"".equals(listaCampiModificati)) {
            listaCampiModificati += ",\n";
          }
          listaCampiModificati += "Categoria principale";
        }

        if (isAggiornamentoULTAPP == true) {
          if (!"".equals(listaCampiModificati)) {
            listaCampiModificati += ",\n";
          }
          listaCampiModificati += "Lista delle ulteriori categorie";
        }

        if (isAggiornamentoAPPACIG == true) {
          if (!"".equals(listaCampiModificati)) {
            listaCampiModificati += ",\n";
          }
          listaCampiModificati += "Lista dei CIG";
        }

        Long notecod = (Long) this.sqlManager.getObject("select max(notecod) from g_noteavvisi", new Object[] {});
        if (notecod == null) {
          notecod = new Long(0);
        }
        notecod = new Long(notecod.longValue() + 1);

        DataColumnContainer dccG_NOTEAVVISI = new DataColumnContainer(new DataColumn[] { new DataColumn("G_NOTEAVVISI.NOTECOD",
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, notecod)) });
        dccG_NOTEAVVISI.addColumn("G_NOTEAVVISI.NOTEPRG", JdbcParametro.TIPO_TESTO, "PL");
        dccG_NOTEAVVISI.addColumn("G_NOTEAVVISI.NOTEENT", JdbcParametro.TIPO_TESTO, "APPA");
        dccG_NOTEAVVISI.addColumn("G_NOTEAVVISI.NOTEKEY1", JdbcParametro.TIPO_TESTO, codlav);
        dccG_NOTEAVVISI.addColumn("G_NOTEAVVISI.NOTEKEY2", JdbcParametro.TIPO_TESTO, nappal.toString());
        ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        dccG_NOTEAVVISI.addColumn("G_NOTEAVVISI.AUTORENOTA", JdbcParametro.TIPO_NUMERICO, new Long(profilo.getId()));
        dccG_NOTEAVVISI.addColumn("G_NOTEAVVISI.STATONOTA", JdbcParametro.TIPO_NUMERICO, new Long(1));
        dccG_NOTEAVVISI.addColumn("G_NOTEAVVISI.TIPONOTA", JdbcParametro.TIPO_NUMERICO, new Long(3));
        dccG_NOTEAVVISI.addColumn("G_NOTEAVVISI.DATANOTA", JdbcParametro.TIPO_DATA, new Timestamp(
            UtilityDate.getDataOdiernaAsDate().getTime()));

        String titolonota = null;
        String testonota = null;
        if ("INS".equals(tipo)) {
          titolonota = "Inserimento nuovo contratto";
          testonota = "E' stato inserito il nuovo contratto con codice " + codlav + "/" + nappal.toString();
        } else if ("UPD".equals(tipo)) {
          titolonota = "Aggiornamento contratto " + codlav + "/" + nappal.toString();
          if (listaCampiModificati != null && listaCampiModificati.length() >= 2000) {
            listaCampiModificati = listaCampiModificati.substring(0, 1960) + "...";
          }
          testonota = "Informazioni aggiornate: " + listaCampiModificati;
        }
        dccG_NOTEAVVISI.addColumn("G_NOTEAVVISI.TITOLONOTA", JdbcParametro.TIPO_TESTO, titolonota);
        dccG_NOTEAVVISI.addColumn("G_NOTEAVVISI.TESTONOTA", JdbcParametro.TIPO_TESTO, testonota);
        dccG_NOTEAVVISI.insert("G_NOTEAVVISI", this.sqlManager);
      }

    } catch (SQLException e) {
      throw new GestoreException("Errore durante l'aggiornamento dei dati del contratto", null, e);
    }
  }

  /**
   * Restituisce il valore di NGARA effettivo. Se la gara è una gara a lotto
   * singolo o una gara suddivisa il lotti con offerte distinte restituisce il
   * valore di NGARA passato come parametro. Se la gara è suddivisa il lotti ma
   * con offerta unica il valore di NGARA viene utilizzato per ricavare il
   * codice NGARA della riga fittizia.
   *
   * @param ngara
   * @return
   * @throws SQLException
   */
  private String getNGARA(String ngara) throws SQLException {
    String ngaraTmp = ngara;
    String selectGARA = "select gare.codgar1, "
        + "v_gare_torn.genere "
        + "from gare, v_gare_torn "
        + "where gare.codgar1 = v_gare_torn.codgar "
        + "and gare.ngara = ?";
    List<?> datiGARA = this.sqlManager.getVector(selectGARA, new Object[] { ngara });
    if (datiGARA != null && datiGARA.size() > 0) {
      String codgar1 = (String) SqlManager.getValueFromVectorParam(datiGARA, 0).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(datiGARA, 1).getValue();
      if (genere != null && new Long(3).equals(genere)) {
        ngaraTmp = codgar1;
      }
    }
    return ngaraTmp;
  }

  /**
   * Gestione della categoria di iscrizione principale.
   *
   * @param ngara
   * @param codlav
   * @param nappal
   * @throws SQLException
   * @throws GestoreException
   */
  private void gestioneCATAPP(String ngara, String codlav, Long nappal) throws SQLException, GestoreException {

    // Calcolo NGARA
    ngara = this.getNGARA(ngara);

    // Cancellazione preliminare
    this.sqlManager.update("delete from catapp where codlav = ? and nappal = ?", new Object[] { codlav, nappal });

    // Inserimento della categoria
    String selectCATG = "select catiga, " // 0
        + "impiga, " // 1
        + "impbasg, " // 2
        + "numcla " // 3
        + "from catg where ngara = ?";
    List<?> datiCATG = this.sqlManager.getVector(selectCATG, new Object[] { ngara });
    if (datiCATG != null && datiCATG.size() > 0) {
      DataColumnContainer dccCATAPP = new DataColumnContainer(new DataColumn[] {
          new DataColumn("CATAPP.CODLAV", new JdbcParametro(JdbcParametro.TIPO_TESTO, codlav)),
          new DataColumn("CATAPP.NAPPAL", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, nappal)),
          new DataColumn("CATAPP.NCATG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1))) });

      dccCATAPP.getColumn("CATAPP.CODLAV").setChiave(true);
      dccCATAPP.getColumn("CATAPP.NAPPAL").setChiave(true);
      dccCATAPP.getColumn("CATAPP.NCATG").setChiave(true);

      dccCATAPP.addColumn("CATAPP.CATIGA", JdbcParametro.TIPO_TESTO, SqlManager.getValueFromVectorParam(datiCATG, 0).getValue());
      dccCATAPP.addColumn("CATAPP.IMPIGA", JdbcParametro.TIPO_DECIMALE, SqlManager.getValueFromVectorParam(datiCATG, 1).getValue());
      dccCATAPP.addColumn("CATAPP.IMPBASG", JdbcParametro.TIPO_DECIMALE,
          SqlManager.getValueFromVectorParam(datiCATG, 2).getValue());
      dccCATAPP.addColumn("CATAPP.NUMCLA", JdbcParametro.TIPO_NUMERICO, SqlManager.getValueFromVectorParam(datiCATG, 3).getValue());
      dccCATAPP.insert("CATAPP", this.sqlManager);
    }
  }

  /**
   * Ricava la lista (con descrizione) dei campi modificati.
   *
   * @param dcc
   * @param entita
   * @return
   */
  private String getListaCampiModificati(DataColumnContainer dcc, String entita, String listaCampiModificati) {
    DataColumn cols[] = dcc.getColumns(entita, 2);
    for (int i = 0; i < cols.length; i++) {
      if (cols[i].isModified()) {
        String descrizioneCampo = this.getDescrizioneCampo(entita, cols[i].getName());
        if (!"".equals(listaCampiModificati)) {
          listaCampiModificati += ", ";
        }
        String separatore = "\n";
        listaCampiModificati += separatore + descrizioneCampo;
      }
    }
    return listaCampiModificati;
  }

  /**
   * Ricava gli importi totali nel caso di una gara divisa in lotti ma con
   * offerta unica. Gli importi sono determinati dalla somma di tutti i lotti
   * vinti dalla ditta considerata nel caso di modcont=2, se invece modcont=1
   * si considerano gli importi del singolo lotto
   *
   * @param codgar1
   * @param ditta
   * @param ngara
   * @param modcont
   * @return
   * @throws SQLException
   * @throws GestoreException
   */
  private HashMap<String, Double> getImportiOffertaUnica(String codgar1, String ditta, String ngara, Long modcont) throws SQLException, GestoreException {
    HashMap<String, Double> hMapImporti = new HashMap<String, Double>();

    double impapp = 0;
    double impnrl = 0;
    double impsic = 0;
    double iaggiu = 0;
    double impgar = 0;
    double impsmi = 0;
    double impsco = 0;
    double impcor = 0;
    double impmis = 0;
    double impnrm = 0;
    double impnrc = 0;
    double onprge = 0;

    Object parametri[]=null;
    String selectGARE = "select impapp, impnrl, impsic, iaggiu, impgar, impsmi, impsco, impcor, impmis, impnrm, impnrc, onprge from gare";
    if(new Long(1).equals(modcont)){
      selectGARE += " where ngara = ?";
      parametri = new Object[] { ngara };
    }else{
      selectGARE += " where codgar1 = ? and ditta = ? and genere is null";
      parametri = new Object[] { codgar1, ditta };
    }

    List<?> datiGARE = this.sqlManager.getListVector(selectGARE, parametri);
    if (datiGARE != null && datiGARE.size() > 0) {
      for (int i = 0; i < datiGARE.size(); i++) {
        Double impapp_tmp = (Double) SqlManager.getValueFromVectorParam(datiGARE.get(i), 0).getValue();
        Double impnrl_tmp = (Double) SqlManager.getValueFromVectorParam(datiGARE.get(i), 1).getValue();
        Double impsic_tmp = (Double) SqlManager.getValueFromVectorParam(datiGARE.get(i), 2).getValue();
        Double iaggiu_tmp = (Double) SqlManager.getValueFromVectorParam(datiGARE.get(i), 3).getValue();
        Double impgar_tmp = (Double) SqlManager.getValueFromVectorParam(datiGARE.get(i), 4).getValue();

        Double impsmi_tmp = (Double) SqlManager.getValueFromVectorParam(datiGARE.get(i), 5).getValue();
        Double impsco_tmp = (Double) SqlManager.getValueFromVectorParam(datiGARE.get(i), 6).getValue();
        Double impcor_tmp = (Double) SqlManager.getValueFromVectorParam(datiGARE.get(i), 7).getValue();
        Double impmis_tmp = (Double) SqlManager.getValueFromVectorParam(datiGARE.get(i), 8).getValue();
        Double impnrm_tmp = (Double) SqlManager.getValueFromVectorParam(datiGARE.get(i), 9).getValue();
        Double impnrc_tmp = (Double) SqlManager.getValueFromVectorParam(datiGARE.get(i), 10).getValue();
        Double onprge_tmp = (Double) SqlManager.getValueFromVectorParam(datiGARE.get(i), 11).getValue();

        if (impapp_tmp != null) {
          impapp += impapp_tmp.doubleValue();
        }
        if (impnrl_tmp != null) {
          impnrl += impnrl_tmp.doubleValue();
        }
        if (impsic_tmp != null) {
          impsic += impsic_tmp.doubleValue();
        }
        if (iaggiu_tmp != null) {
          iaggiu += iaggiu_tmp.doubleValue();
        }
        if (impgar_tmp != null) {
          impgar += impgar_tmp.doubleValue();
        }
        if (impsmi_tmp != null) {
          impsmi += impsmi_tmp.doubleValue();
        }
        if (impsco_tmp != null) {
          impsco += impsco_tmp.doubleValue();
        }
        if (impcor_tmp != null) {
          impcor += impcor_tmp.doubleValue();
        }
        if (impmis_tmp != null) {
          impmis += impmis_tmp.doubleValue();
        }
        if (impnrm_tmp != null) {
          impnrm += impnrm_tmp.doubleValue();
        }
        if (impnrc_tmp != null) {
          impnrc += impnrc_tmp.doubleValue();
        }
        if (onprge_tmp != null) {
          onprge += onprge_tmp.doubleValue();
        }

      }
    }

    hMapImporti.put("IMPAPP", new Double(impapp));
    hMapImporti.put("IMPNRL", new Double(impnrl));
    hMapImporti.put("IMPSIC", new Double(impsic));
    hMapImporti.put("IAGGIU", new Double(iaggiu));
    hMapImporti.put("IMPGAR", new Double(impgar));

    if(impsmi!=0)
      hMapImporti.put("IMPSMI", new Double(impsmi));
    else
      hMapImporti.put("IMPSMI", null);

    if(impsco!=0)
      hMapImporti.put("IMPSCO", new Double(impsco));
    else
      hMapImporti.put("IMPSCO", null);

    if(impcor!=0)
      hMapImporti.put("IMPCOR", new Double(impcor));
    else
      hMapImporti.put("IMPCOR", null);

    if(impmis!=0)
      hMapImporti.put("IMPMIS", new Double(impmis));
    else
      hMapImporti.put("IMPMIS", null);

    if(impnrm!=0)
      hMapImporti.put("IMPNRM", new Double(impnrm));
    else
      hMapImporti.put("IMPNRM", null);

    if(impnrc!=0)
      hMapImporti.put("IMPNRC", new Double(impnrc));
    else
      hMapImporti.put("IMPNRC", null);

    if(onprge!=0)
      hMapImporti.put("ONPRGE", new Double(onprge));
    else
      hMapImporti.put("ONPRGE", null);

    return hMapImporti;

  }

  /**
   * Gestione dei CIG associati al contratto nel caso di gara divisa in lotti ma
   * con offerta unica.
   *
   * @param ngara
   * @param codlav
   * @param nappal
   * @throws SQLException
   * @throws GestoreException
   */
  private void gestioneAPPACIG(String ngara, String codlav, Long nappal, Long ncont) throws SQLException, GestoreException {

    // Cancellazione preliminare
    this.sqlManager.update("delete from appacig where codlav = ? and nappal = ?", new Object[] { codlav, nappal });

    // Inserimento in APPACIG
    String selectGARA = "select gare.codgar1, v_gare_torn.genere, gare.ditta from gare, v_gare_torn where gare.codgar1 = v_gare_torn.codgar and gare.ngara = ?";
    List<?> datiGARA = this.sqlManager.getVector(selectGARA, new Object[] { ngara });
    if (datiGARA != null && datiGARA.size() > 0) {
      String codgar1 = (String) SqlManager.getValueFromVectorParam(datiGARA, 0).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(datiGARA, 1).getValue();
      String ditta = (String) SqlManager.getValueFromVectorParam(datiGARA, 2).getValue();

      if (genere != null && new Long(3).equals(genere)) {

        String selectCIG = "select codcig, not_gar from gare where codgar1 = ? and ditta = ? and genere is null";
        List<?> datiCIG = this.sqlManager.getListVector(selectCIG, new Object[] { codgar1, ditta });
        if (datiCIG != null && datiCIG.size() > 0) {
          for (int i = 0; i < datiCIG.size(); i++) {
            DataColumnContainer dccAPPACIG = new DataColumnContainer(new DataColumn[] {
                new DataColumn("APPACIG.CODLAV", new JdbcParametro(JdbcParametro.TIPO_TESTO, codlav)),
                new DataColumn("APPACIG.NAPPAL", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, nappal)),
                new DataColumn("APPACIG.NUM", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(i + 1))) });

            dccAPPACIG.getColumn("APPACIG.CODLAV").setChiave(true);
            dccAPPACIG.getColumn("APPACIG.NAPPAL").setChiave(true);
            dccAPPACIG.getColumn("APPACIG.NUM").setChiave(true);
            dccAPPACIG.addColumn("APPACIG.CIG", JdbcParametro.TIPO_TESTO,
                SqlManager.getValueFromVectorParam(datiCIG.get(i), 0).getValue());
            dccAPPACIG.addColumn("APPACIG.DESCRIZIONE", JdbcParametro.TIPO_TESTO,
                SqlManager.getValueFromVectorParam(datiCIG.get(i), 1).getValue());

            dccAPPACIG.insert("APPACIG", this.sqlManager);

          }

        }
      }
    }
  }

  /**
   * Controllo preliminare delle ulteriori categorie.
   *
   * @param ngara
   * @param codlav
   * @param nappal
   * @return boolean
   *         <ul>
   *         <li>false - non è necessario alcun aggiornamento</li>
   *         <li>true - necessario aggiornamento</li>
   *         </ul>
   * @throws SQLException
   * @throws GestoreException
   */
  private boolean richiestoAggiornamentoULTAPP(String ngara, String codlav, Long nappal) throws SQLException, GestoreException {

    ngara = this.getNGARA(ngara);

    boolean aggiorna = false;

    String selectCountOPES = "select count(*) from opes where ngara3 = ?";
    String selectCountULTAPP = "select count(*) from ultapp where codlav = ? and nappal = ?";
    String selectCountOPES_ULTAPP = "select count(*) from opes, ultapp "
        + "where (opes.catoff = ultapp.catoff or (opes.catoff is null and ultapp.catoff is null)) "
        + "and (opes.iscoff = ultapp.iscoff or (opes.iscoff is null and ultapp.iscoff is null)) "
        + "and (opes.impapo = ultapp.impapo or (opes.impapo is null and ultapp.impapo is null)) "
        + "and (opes.descop = ultapp.descop or (opes.descop is null and ultapp.descop is null)) "
        + "and (opes.numclu = ultapp.numclu or (opes.numclu is null and ultapp.numclu is null)) "
        + "and (opes.quaobb = ultapp.quaobb or (opes.quaobb is null and ultapp.quaobb is null)) "
        + "and (opes.acontec = ultapp.acontec or (opes.acontec is null and ultapp.acontec is null)) "
        + "and opes.ngara3 = ? and ultapp.codlav = ? and ultapp.nappal = ?";

    Long countOPES = (Long) this.sqlManager.getObject(selectCountOPES, new Object[] { ngara });
    Long countULTAPP = (Long) this.sqlManager.getObject(selectCountULTAPP, new Object[] { codlav, nappal });
    Long countOPES_ULTAPP = (Long) this.sqlManager.getObject(selectCountOPES_ULTAPP, new Object[] { ngara, codlav, nappal });

    if (countOPES == null) countOPES = new Long(0);
    if (countULTAPP == null) countULTAPP = new Long(0);
    if (countOPES_ULTAPP == null) countOPES_ULTAPP = new Long(0);

    if ((countOPES.longValue() != countULTAPP.longValue())
        || (countOPES.longValue() != countOPES_ULTAPP.longValue())
        || (countULTAPP.longValue() != countOPES_ULTAPP.longValue())) {
      aggiorna = true;
    }

    return aggiorna;
  }

  /**
   * Controllo preliminare della categoria principale.
   *
   * @param ngara
   * @param codlav
   * @param nappal
   * @return boolean
   *         <ul>
   *         <li>false - non è necessario alcun aggiornamento</li>
   *         <li>true - necessario aggiornamento</li>
   *         </ul>
   * @throws SQLException
   * @throws GestoreException
   */
  private boolean richiestoAggiornamentoCATAPP(String ngara, String codlav, Long nappal) throws SQLException, GestoreException {

    ngara = this.getNGARA(ngara);

    boolean aggiorna = false;

    String selectCountCATG = "select count(*) from catg where ngara = ?";
    String selectCountCATAPP = "select count(*) from catapp where codlav = ? and nappal = ?";
    String selectCountCATG_CATAPP = "select count(*) from catg, catapp "
        + "where (catg.catiga = catapp.catiga or (catg.catiga is null and catapp.catiga is null)) "
        + "and (catg.impiga = catapp.impiga or (catg.impiga is null and catapp.impiga is null)) "
        + "and (catg.impbasg = catapp.impbasg or (catg.impbasg is null and catapp.impbasg is null)) "
        + "and (catg.numcla = catapp.numcla or (catg.numcla is null and catapp.numcla is null)) "
        + "and catg.ngara = ? and catapp.codlav = ? and catapp.nappal = ?";

    Long countCATG = (Long) this.sqlManager.getObject(selectCountCATG, new Object[] { ngara });
    Long countCATAPP = (Long) this.sqlManager.getObject(selectCountCATAPP, new Object[] { codlav, nappal });
    Long countCATG_CATAPP = (Long) this.sqlManager.getObject(selectCountCATG_CATAPP, new Object[] { ngara, codlav, nappal });

    if (countCATG == null) countCATG = new Long(0);
    if (countCATAPP == null) countCATAPP = new Long(0);
    if (countCATG_CATAPP == null) countCATG_CATAPP = new Long(0);

    if ((countCATG.longValue() != countCATAPP.longValue())
        || (countCATG.longValue() != countCATG_CATAPP.longValue())
        || (countCATAPP.longValue() != countCATG_CATAPP.longValue())) {
      aggiorna = true;
    }

    return aggiorna;
  }

  /**
   * Controllo preliminare dei CIG nel caso di gare in lotto con offerta unica.
   *
   * @param ngara
   * @param codlav
   * @param nappal
   * @return boolean
   *         <ul>
   *         <li>false - non è necessario alcun aggiornamento</li>
   *         <li>true - necessario aggiornamento</li>
   *         </ul>
   * @throws SQLException
   * @throws GestoreException
   */
  private boolean richiestoAggiornamentoAPPACIG(String ngara, String codlav, Long nappal) throws SQLException, GestoreException {

    boolean aggiorna = false;

    String selectGARA = "select gare.codgar1, v_gare_torn.genere, gare.ditta, torn.modcont from gare, v_gare_torn, torn where gare.codgar1 = v_gare_torn.codgar and gare.ngara = ? and torn.codgar= v_gare_torn.codgar";
    List<?> datiGARA = this.sqlManager.getVector(selectGARA, new Object[] { ngara });
    if (datiGARA != null && datiGARA.size() > 0) {
      String codgar1 = (String) SqlManager.getValueFromVectorParam(datiGARA, 0).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(datiGARA, 1).getValue();
      String ditta = (String) SqlManager.getValueFromVectorParam(datiGARA, 2).getValue();
      Long modcont = (Long) SqlManager.getValueFromVectorParam(datiGARA, 3).getValue();

      if (genere != null && new Long(3).equals(genere) && new Long(2).equals(modcont)) {
        String selectCountGARE = "select count(*) from gare where codgar1 = ? and ditta = ? and genere is null";
        String selectCountAPPACIG = "select count(*) from appacig where codlav = ? and nappal = ?";
        String selectCountGARE_APPACIG = "select count(*) from gare, appacig "
            + "where (gare.codcig = appacig.cig or (gare.codcig is null and appacig.cig is null)) "
            + "and (gare.not_gar = appacig.descrizione or (gare.not_gar is null and appacig.descrizione is null)) "
            + "and gare.codgar1 = ? and gare.ditta = ? and gare.genere is null "
            + "and appacig.codlav = ? and appacig.nappal = ?";

        Long countGARE = (Long) this.sqlManager.getObject(selectCountGARE, new Object[] { codgar1, ditta });
        Long countAPPACIG = (Long) this.sqlManager.getObject(selectCountAPPACIG, new Object[] { codlav, nappal });
        Long countGARE_APPACIG = (Long) this.sqlManager.getObject(selectCountGARE_APPACIG, new Object[] { codgar1, ditta, codlav, nappal });

        if (countGARE == null) countGARE = new Long(0);
        if (countAPPACIG == null) countAPPACIG = new Long(0);
        if (countGARE_APPACIG == null) countGARE_APPACIG = new Long(0);

        if ((countGARE.longValue() != countAPPACIG.longValue())
            || (countGARE.longValue() != countGARE_APPACIG.longValue())
            || (countAPPACIG.longValue() != countGARE_APPACIG.longValue())) {
          aggiorna = true;
        }
      }
    }

    return aggiorna;

  }

  /**
   * Gestione delle categorie di iscrizione secondarie.
   *
   * @param ngara
   * @param codlav
   * @param nappal
   * @throws SQLException
   * @throws GestoreException
   */
  private void gestioneULTAPP(String ngara, String codlav, Long nappal) throws SQLException, GestoreException {

    // Calcolo NGARA
    ngara = this.getNGARA(ngara);

    // Cancellazione preliminare
    this.sqlManager.update("delete from ultapp where codlav = ? and nappal = ?", new Object[] { codlav, nappal });

    // Inserimento della categoria
    String selectOPES = "select nopega, " // 0
        + "catoff, " // 1
        + "iscoff, " // 2
        + "impapo, " // 3
        + "descop, " // 4
        + "numclu, " // 5
        + "quaobb, " // 6
        + "acontec " // 7
        + "from opes where ngara3 = ? order by nopega";

    List<?> datiOPES = this.sqlManager.getListVector(selectOPES, new Object[] { ngara });
    if (datiOPES != null && datiOPES.size() > 0) {
      for (int i = 0; i < datiOPES.size(); i++) {
        DataColumnContainer dccULTAPP = new DataColumnContainer(new DataColumn[] {
            new DataColumn("ULTAPP.CODLAV", new JdbcParametro(JdbcParametro.TIPO_TESTO, codlav)),
            new DataColumn("ULTAPP.NAPPAL", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, nappal)),
            new DataColumn("ULTAPP.NOPEGA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(i + 1))) });

        dccULTAPP.getColumn("ULTAPP.CODLAV").setChiave(true);
        dccULTAPP.getColumn("ULTAPP.NAPPAL").setChiave(true);
        dccULTAPP.getColumn("ULTAPP.NOPEGA").setChiave(true);

        dccULTAPP.addColumn("ULTAPP.CATOFF", JdbcParametro.TIPO_TESTO,
            SqlManager.getValueFromVectorParam(datiOPES.get(i), 1).getValue());
        dccULTAPP.addColumn("ULTAPP.ISCOFF", JdbcParametro.TIPO_DECIMALE,
            SqlManager.getValueFromVectorParam(datiOPES.get(i), 2).getValue());
        dccULTAPP.addColumn("ULTAPP.IMPAPO", JdbcParametro.TIPO_DECIMALE,
            SqlManager.getValueFromVectorParam(datiOPES.get(i), 3).getValue());
        dccULTAPP.addColumn("ULTAPP.DESCOP", JdbcParametro.TIPO_TESTO,
            SqlManager.getValueFromVectorParam(datiOPES.get(i), 4).getValue());
        dccULTAPP.addColumn("ULTAPP.NUMCLU", JdbcParametro.TIPO_NUMERICO,
            SqlManager.getValueFromVectorParam(datiOPES.get(i), 5).getValue());
        dccULTAPP.addColumn("ULTAPP.QUAOBB", JdbcParametro.TIPO_TESTO,
            SqlManager.getValueFromVectorParam(datiOPES.get(i), 6).getValue());
        dccULTAPP.addColumn("ULTAPP.ACONTEC", JdbcParametro.TIPO_TESTO,
            SqlManager.getValueFromVectorParam(datiOPES.get(i), 7).getValue());

        dccULTAPP.insert("ULTAPP", this.sqlManager);

      }

    }
  }

  /**
   * Gestione ditte consorziate
   *
   * @param ngara
   * @param codlav
   * @param nappal
   * @throws SQLException
   * @throws GestoreException
   */
  private void gestioneRAGDET(String ngara, String codlav, Long nappal) throws SQLException, GestoreException {

    // Cancellazione preliminare
    this.sqlManager.update("delete from ragdet where codlav = ? and nappal = ?", new Object[] { codlav, nappal });

    // Inserimento ditte consorziate
    String ditta = (String) this.sqlManager.getObject("select ditta from gare where ngara = ?", new Object[] { ngara });
    if (ditta != null && !"".equals(ditta)) {
      String selectRAGDET = "select codimp, coddic from ragdet where ngara = ? and codimp = ?";

      List<?> datiRAGDET = this.sqlManager.getListVector(selectRAGDET, new Object[] { ngara, ditta });
      if (datiRAGDET != null && datiRAGDET.size() > 0) {
        for (int i = 0; i < datiRAGDET.size(); i++) {
          String codimp = (String) SqlManager.getValueFromVectorParam(datiRAGDET.get(i), 0).getValue();
          String coddic = (String) SqlManager.getValueFromVectorParam(datiRAGDET.get(i), 1).getValue();

          Long maxNUMDIC = (Long) this.sqlManager.getObject("select max(numdic) from ragdet where codimp = ? and coddic = ?", new Object[] {
              codimp, coddic });
          if (maxNUMDIC == null) {
            maxNUMDIC = new Long(0);
          }
          maxNUMDIC = new Long(maxNUMDIC.longValue() + 1);

          DataColumnContainer dccRAGDET = new DataColumnContainer(new DataColumn[] {
              new DataColumn("RAGDET.CODIMP", new JdbcParametro(JdbcParametro.TIPO_TESTO, codimp)),
              new DataColumn("RAGDET.CODDIC", new JdbcParametro(JdbcParametro.TIPO_TESTO, coddic)),
              new DataColumn("RAGDET.NUMDIC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, maxNUMDIC)),
              new DataColumn("RAGDET.CODLAV", new JdbcParametro(JdbcParametro.TIPO_TESTO, codlav)),
              new DataColumn("RAGDET.NAPPAL", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, nappal)) });

          dccRAGDET.getColumn("RAGDET.CODIMP").setChiave(true);
          dccRAGDET.getColumn("RAGDET.CODDIC").setChiave(true);
          dccRAGDET.getColumn("RAGDET.NUMDIC").setChiave(true);

          dccRAGDET.insert("RAGDET", this.sqlManager);
        }
      }
    }
  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

    PlManager plManager = (PlManager) UtilitySpring.getBean("plManager", this.getServletContext(), PlManager.class);

    try {
      if (impl.isColumn("PERI.CODLAV")) {
        // Richiesta cancellazione dell'intero lavoro perche' è stato
        // selezionato il primo appalto di un lavoro
        String codlav = impl.getString("PERI.CODLAV");

        plManager.deleteLavoro(codlav);
        geneManager.deleteTabelle(new String[] { "PERI" }, "codlav = ?", new Object[] { codlav });

      } else if (impl.isColumn("APPA.CODLAV") && impl.isColumn("APPA.NAPPAL")) {
        // Richiesta cancellazione del singolo contratto. Tuttavia se l'appalto
        // è l'unico (come nel caso delle forniture o servizi puri) si deve
        // provvedere alla cancellazione anche del lavoro "fittizio" collegato.
        String codlav = impl.getString("APPA.CODLAV");
        Long nappal = impl.getLong("APPA.NAPPAL");

        Long numeroAppalti = (Long) this.sqlManager.getObject("select count(*) from appa where codlav = ?", new Object[] { codlav });
        if (numeroAppalti != null && numeroAppalti.longValue() > 1) {
          // In questo caso esiste piu' di un appalto.
          // E' possibile cancellare solamente l'appalto indicato.
          plManager.deleteAppalto(codlav, nappal);
          geneManager.deleteTabelle(new String[] { "APPA" }, "codlav = ? and nappal = ?", new Object[] { codlav, nappal });
        } else {
          // In questo caso o non esiste alcun appalto oppure è l'unico.
          // E' possibile cancellare l'intero lavoro.
          plManager.deleteLavoro(codlav);
          geneManager.deleteTabelle(new String[] { "PERI" }, "codlav = ?", new Object[] { codlav });
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore durante la cancellazione del lavoro/contratto", "", e);
    }
  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {

  }

  /**
   * Aggiunge le colonne degli altri dati di APPA.
   *
   * @param dccAPPA
   * @param ngara
   * @param ncont
   *
   * @throws GestoreException
   * @throws SQLException
   */
  private void dccAPPA_AltriDati(DataColumnContainer dccAPPA, String ngara, Long ncont) throws GestoreException, SQLException {

    if (logger.isDebugEnabled()) logger.debug("dccAPPA_AltriDati: inizio metodo");

    Long genere = null;
    Long modcont  = null;
    String codgar1 = null;
    Long aqoper = null;
    String esecscig = "";
    String selectGARA = "select gare.codgar1, " // 0
        + "v_gare_torn.genere, " // 1
        + "gare.codcig, " // 2
        + "gare.dacqcig, " // 3
        + "torn.navvig, " // 4
        + "torn.desoff, " // 5
        + "gare.desoff, " // 6
        + "gare.ditta, " // 7
        + "gare.nomima, " // 8
        + "gare.tipgarg, " // 9
        + "gare.modlicg, " // 10
        + "gare.dverag, " // 11
        + "gare.tattoa, " // 12
        + "gare.nattoa, " // 13
        + "gare.dattoa, " // 14
        + "gare.dcomag, " // 15
        + "gare.ncomag, " // 16
        + "gare.ribagg, " // 17
        + "gare.impapp, " // 18
        + "gare.impsic, " // 19
        + "gare.impsmi, " // 20
        + "gare.impsco, " // 21
        + "gare.impcor, " // 22
        + "gare.impmis, " // 23
        + "gare.impnrl, " // 24
        + "gare.impnrm, " // 25
        + "gare.impnrc, " // 26
        + "gare.onprge, " // 27
        + "gare.onsogrib, " // 28
        + "gare.numssl, " // 29
        + "gare.nomssl, " // 30
        + "gare.prosla, " // 31
        + "gare.loclav, " // 32
        + "gare.teutil, " // 33
        + "torn.davvig, " // 34
        + "gare.temesi, " // 35
        + "torn.altrisog, "// 36
        + "torn.accqua, "  // 37
        + "torn.isadesione, " //38
        + "torn.codcigaq, " //39
        + "gare.locint, " // 40
        + "torn.numavcp, " //41
        + "gare.tattog, " //42
        + "gare.nattog, " //43
        + "gare.dattog, "  //44
        + "torn.sommaur, " //45
        + "gare.riboepv " // 46
        + "from gare, v_gare_torn, torn "
        + "where gare.codgar1 = v_gare_torn.codgar "
        + "and gare.codgar1 = torn.codgar and "
        + "ngara = ?";

    String selectGAREGenere3 = "select gare.tattoa, " // 0
        + "gare.nattoa, " // 1
        + "gare.dattoa, " // 2
        + "gare.dacqcig, " // 3
        + "gare.dcomag, " // 4
        + "gare.ncomag, " // 5
        + "gare.numssl, " // 6
        + "gare.nomssl, " // 7
        + "gare.prosla, " // 8
        + "gare.loclav, " // 9
        + "gare.teutil, " // 10
        + "gare.temesi, " // 11
        + "gare.locint, " // 12
        + "torn.numavcp, " //13
        + "torn.modcont, "  //14
        + "torn.tattot, "   //15
        + "torn.nattot, "   //16
        + "torn.dattot, "    //17
        + "torn.sommaur, "  //18
        + "torn.accqua "  // 19
        + "from gare,torn where "
        + "gare.codgar1 = torn.codgar and "
        + "ngara = ?";


    List<?> datiGARA = this.sqlManager.getVector(selectGARA, new Object[] { ngara });
    if (datiGARA != null && datiGARA.size() > 0) {

      codgar1 = (String) SqlManager.getValueFromVectorParam(datiGARA, 0).getValue();

      genere = (Long) SqlManager.getValueFromVectorParam(datiGARA, 1).getValue();

      List<?> datiGARAGenere3 = null;
      if (genere != null && new Long(3).equals(genere)) {
        datiGARAGenere3 = this.sqlManager.getVector(selectGAREGenere3, new Object[] { codgar1 });
        if (datiGARAGenere3 != null && datiGARAGenere3.size() > 0) {
          modcont = (Long)SqlManager.getValueFromVectorParam(datiGARAGenere3, 14).getValue();
        }
      }
      String accqua = null;

      //Recupero inizialmente i dati di GARECONT e GARE1 che mi servono per stabilire il campo CIG
      Object param[] = null;
      String selectGarecont = "select coorba,banapp,codbic,esecscig from garecont where ngara=? and ncont=?";
      String selectGare1 = "select aqoper from gare1 where ngara=?";
      if (genere != null && new Long(3).equals(genere)) {
        if(new Long(1).equals(modcont)){
          selectGarecont = "select coorba,banapp,codbic,esecscig from garecont where ngara=? and ngaral=?";
          param= new Object[]{codgar1,ngara};
        }else
          param=new Object[]{codgar1,ncont};
      }else
        param=new Object[]{ngara,ncont};

      Vector datiGarecont = this.sqlManager.getVector(selectGarecont, param);
      if(datiGarecont!=null && datiGarecont.size()>0){
        String coorba = (String) SqlManager.getValueFromVectorParam(datiGarecont, 0).getValue();
        String banapp = (String) SqlManager.getValueFromVectorParam(datiGarecont, 1).getValue();
        String codbic = (String) SqlManager.getValueFromVectorParam(datiGarecont, 2).getValue();
        esecscig = (String) SqlManager.getValueFromVectorParam(datiGarecont, 3).getValue();
        esecscig = UtilityStringhe.convertiNullInStringaVuota(esecscig);
        dccAPPA.setValue("APPA.IBAN", coorba);
        dccAPPA.setValue("APPA.BANAPP", banapp);
        dccAPPA.setValue("APPA.BIC", codbic);
      }

      aqoper = (Long) this.sqlManager.getObject(selectGare1, new Object[]{ngara});

      String codcig = null;
      Date dacqcig = null;
      String numavcp = null;
      if (genere != null && new Long(3).equals(genere)) {
        if (datiGARAGenere3 != null && datiGARAGenere3.size() > 0) {
          dacqcig = (Date) SqlManager.getValueFromVectorParam(datiGARAGenere3, 3).getValue();
          numavcp = (String) SqlManager.getValueFromVectorParam(datiGARAGenere3, 13).getValue();
          accqua = (String) SqlManager.getValueFromVectorParam(datiGARAGenere3, 19).getValue();
        }
      } else {
        dacqcig = (Date) SqlManager.getValueFromVectorParam(datiGARA, 3).getValue();
        numavcp = (String) SqlManager.getValueFromVectorParam(datiGARA, 41).getValue();
        accqua = (String) SqlManager.getValueFromVectorParam(datiGARA, 37).getValue();
      }
      dccAPPA.setValue("APPA.IDGARA", numavcp);

      //Nella gestione del codice CIG, il caso di gare ad offerta unica con modcont=1 deve
      //essere gestito come lotto unico
      if((new Long(3).equals(genere) && new Long(1).equals(modcont)) || (genere!=null && genere.longValue()!=3)){
        String isadesione = (String) SqlManager.getValueFromVectorParam(datiGARA, 38).getValue();
        String codcigaq = (String) SqlManager.getValueFromVectorParam(datiGARA, 39).getValue();

      	if ("1".equals(isadesione)) {
      	dccAPPA.setValue("APPA.FLAG_CM", "1");
      	dccAPPA.setValue("APPA.CIGMASTER", codcigaq);
		}

		codcig = (String) SqlManager.getValueFromVectorParam(datiGARA, 2).getValue();
      }

      dccAPPA.setValue("APPA.CODCIG", codcig);
      dccAPPA.setValue("APPA.DACQCIG", dacqcig);
      dccAPPA.setValue("APPA.FLAG_ACQ", accqua);

      dccAPPA.setValue("APPA.DAVVIS", SqlManager.getValueFromVectorParam(datiGARA, 34).getValue());
      dccAPPA.setValue("APPA.DPROFF", SqlManager.getValueFromVectorParam(datiGARA, 5).getValue());

      String ditta = (String) SqlManager.getValueFromVectorParam(datiGARA, 7).getValue();
      String dittaAggiud = ditta;
      String ricsub = (String) this.sqlManager.getObject("select ricsub from ditg where codgar5 = ? and ngara5 = ? and dittao = ? ", new Object[]{codgar1,ngara,ditta});
      dccAPPA.setValue("APPA.iSABSUBA",ricsub);
      String ragioneSociale = (String)SqlManager.getValueFromVectorParam(datiGARA, 8).getValue();
      //Nel caso di personalizzazione SMAT, se ditta è una RT si deve riportare in APPA la mandataria
      if(this.getGeneManager().getProfili().checkProtec(
          (String) this.getRequest().getSession().getAttribute(
              CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS",
          "ALT.GARE.inserimentoDitteSMAT")){
        Long tipimp = (Long)this.sqlManager.getObject("select tipimp from impr where codimp=?", new Object[]{dittaAggiud});
        if(tipimp!=null && (tipimp.longValue()==3 || tipimp.longValue()==10)){
          String newDitta= (String)this.sqlManager.getObject("select coddic from ragimp where codime9=? and impman='1'", new Object[]{dittaAggiud});
          if(newDitta!=null && !"".equals(newDitta)){
            dittaAggiud=newDitta;
            String nomimp = (String)this.sqlManager.getObject("select nomimp from impr where codimp=?", new Object[]{dittaAggiud});
            ragioneSociale = nomimp;
          }
        }
      }

      dccAPPA.setValue("APPA.CIMPAG", dittaAggiud);
      dccAPPA.setValue("APPA.NCODIM", dittaAggiud);

      dccAPPA.setValue("APPA.APCLEG", null);
      dccAPPA.setValue("APPA.APNLEG", null);
      dccAPPA.setValue("APPA.APCDTE", null);
      dccAPPA.setValue("APPA.APNDTE", null);

      dccAPPA.setValue("APPA.IMPAGG", ragioneSociale);
      dccAPPA.setValue("APPA.IMPRESE", ragioneSociale);
      dccAPPA.setValue("APPA.TIPAGG", SqlManager.getValueFromVectorParam(datiGARA, 9).getValue());

      Long modlicg = (Long) SqlManager.getValueFromVectorParam(datiGARA, 10).getValue();
      String A2047_tab1desc = (String) sqlManager.getObject("select tab1desc from tab1 where tab1cod = ? and tab1tip = ?", new Object[] {
          "A2047", modlicg });
      dccAPPA.setValue("APPA.CODAGG", A2047_tab1desc);

      dccAPPA.setValue("APPA.DAGG", SqlManager.getValueFromVectorParam(datiGARA, 11).getValue());

      Long tattoa = (Long) SqlManager.getValueFromVectorParam(datiGARA, 12).getValue();
      String nattoa = (String) SqlManager.getValueFromVectorParam(datiGARA, 13).getValue();
      Date dattoa = (Date) SqlManager.getValueFromVectorParam(datiGARA, 14).getValue();
      Date dcomag = null;
      String ncomag = null;

      if (genere != null && new Long(3).equals(genere)) {
        if (datiGARAGenere3 != null && datiGARAGenere3.size() > 0) {
          dcomag = (Date) SqlManager.getValueFromVectorParam(datiGARAGenere3, 4).getValue();
          ncomag = (String) SqlManager.getValueFromVectorParam(datiGARAGenere3, 5).getValue();
        }
      } else {
        dcomag = (Date) SqlManager.getValueFromVectorParam(datiGARA, 15).getValue();
        ncomag = (String) SqlManager.getValueFromVectorParam(datiGARA, 16).getValue();
      }

      dccAPPA.setValue("APPA.TVOAGG", this.getTAB1TIP_A2040(tattoa));
      dccAPPA.setValue("APPA.NVOAGG", nattoa);
      dccAPPA.setValue("APPA.DVOAGG", dattoa);
      dccAPPA.setValue("APPA.DCOAGG", dcomag);
      dccAPPA.setValue("APPA.NCOAGG", ncomag);

      if (genere != null && new Long(3).equals(genere) && new Long(2).equals(modcont)) {
        dccAPPA.setValue("APPA.RIBAGG", null);
        dccAPPA.setValue("APPA.RIBSUB", null);
      } else {
        if (modlicg != null && new Long(6).equals(modlicg)) {
          dccAPPA.setValue("APPA.RIBAGG", SqlManager.getValueFromVectorParam(datiGARA, 46).getValue());
          dccAPPA.setValue("APPA.RIBSUB", SqlManager.getValueFromVectorParam(datiGARA, 46).getValue());
        } else {
          dccAPPA.setValue("APPA.RIBAGG", SqlManager.getValueFromVectorParam(datiGARA, 17).getValue());
          dccAPPA.setValue("APPA.RIBSUB", SqlManager.getValueFromVectorParam(datiGARA, 17).getValue());
        }
      }

      if (genere != null && new Long(3).equals(genere)) {
        HashMap<String, Double> hMapImporti = new HashMap<String, Double>();
        hMapImporti = this.getImportiOffertaUnica(codgar1, ditta, ngara, modcont);
        dccAPPA.setValue("APPA.IMPLAV", hMapImporti.get("IMPAPP"));
        dccAPPA.setValue("APPA.IMPLAS", hMapImporti.get("IMPSIC"));
        dccAPPA.setValue("APPA.IMPLAT1", hMapImporti.get("IMPSMI"));
        dccAPPA.setValue("APPA.IMPLASE", null);
        dccAPPA.setValue("APPA.IMPLASC", hMapImporti.get("IMPSCO"));
        dccAPPA.setValue("APPA.IMPLAC", hMapImporti.get("IMPCOR"));
        dccAPPA.setValue("APPA.IMPLAM", hMapImporti.get("IMPMIS"));
        dccAPPA.setValue("APPA.IMPLAE", null);
        dccAPPA.setValue("APPA.IMPNRL", hMapImporti.get("IMPNRL"));
        dccAPPA.setValue("APPA.IMPNRM", hMapImporti.get("IMPNRM"));
        dccAPPA.setValue("APPA.IMPNRE", null);
        dccAPPA.setValue("APPA.IMPNRC", hMapImporti.get("IMPNRC"));
        dccAPPA.setValue("APPA.ONPRGE", hMapImporti.get("ONPRGE"));
        if(new Long(1).equals(modcont))
          dccAPPA.setValue("APPA.ONSOGRIB", SqlManager.getValueFromVectorParam(datiGARA, 28).getValue());
        else
          dccAPPA.setValue("APPA.ONSOGRIB", null);
      } else {
        dccAPPA.setValue("APPA.IMPLAV", SqlManager.getValueFromVectorParam(datiGARA, 18).getValue());
        dccAPPA.setValue("APPA.IMPLAS", SqlManager.getValueFromVectorParam(datiGARA, 19).getValue());
        dccAPPA.setValue("APPA.IMPLAT1", SqlManager.getValueFromVectorParam(datiGARA, 20).getValue());
        dccAPPA.setValue("APPA.IMPLASE", null);
        dccAPPA.setValue("APPA.IMPLASC", SqlManager.getValueFromVectorParam(datiGARA, 21).getValue());
        dccAPPA.setValue("APPA.IMPLAC", SqlManager.getValueFromVectorParam(datiGARA, 22).getValue());
        dccAPPA.setValue("APPA.IMPLAM", SqlManager.getValueFromVectorParam(datiGARA, 23).getValue());
        dccAPPA.setValue("APPA.IMPLAE", null);
        dccAPPA.setValue("APPA.IMPNRL", SqlManager.getValueFromVectorParam(datiGARA, 24).getValue());
        dccAPPA.setValue("APPA.IMPNRM", SqlManager.getValueFromVectorParam(datiGARA, 25).getValue());
        dccAPPA.setValue("APPA.IMPNRE", null);
        dccAPPA.setValue("APPA.IMPNRC", SqlManager.getValueFromVectorParam(datiGARA, 26).getValue());
        dccAPPA.setValue("APPA.ONPRGE", SqlManager.getValueFromVectorParam(datiGARA, 27).getValue());
        dccAPPA.setValue("APPA.ONSOGRIB", SqlManager.getValueFromVectorParam(datiGARA, 28).getValue());
      }

      String codcpv = null;
      if (genere != null && new Long(3).equals(genere) && new Long(2).equals(modcont)) {
        codcpv = (String) this.sqlManager.getObject("select gc.codcpv from garcpv gc,gare g where gc.ngara = g.ngara and g.codgar1=? and gc.tipcpv = ? "
            + "and gc.codcpv is not null order by codiga", new Object[] { codgar1,new Long(1) });
      }else{
        codcpv = (String) this.sqlManager.getObject("select codcpv from garcpv where ngara = ? and tipcpv = ?", new Object[] { ngara,
            new Long(1) });
      }
      dccAPPA.setValue("APPA.ALTCAT", codcpv);

      String numssl = null;
      String nomssl = null;
      String prosla = null;
      String loclav = null;
      String locint = null;
      Long teutil = null;
      Long temesi = null;

      if (genere != null && new Long(3).equals(genere)) {
        if (datiGARAGenere3 != null && datiGARAGenere3.size() > 0) {
          numssl = (String) SqlManager.getValueFromVectorParam(datiGARAGenere3, 6).getValue();
          nomssl = (String) SqlManager.getValueFromVectorParam(datiGARAGenere3, 7).getValue();
          prosla = (String) SqlManager.getValueFromVectorParam(datiGARAGenere3, 8).getValue();
          loclav = (String) SqlManager.getValueFromVectorParam(datiGARAGenere3, 9).getValue();
          teutil = (Long) SqlManager.getValueFromVectorParam(datiGARAGenere3, 10).getValue();
          temesi = (Long) SqlManager.getValueFromVectorParam(datiGARAGenere3, 11).getValue();
          locint = (String) SqlManager.getValueFromVectorParam(datiGARAGenere3, 12).getValue();
        }
      } else {
        numssl = (String) SqlManager.getValueFromVectorParam(datiGARA, 29).getValue();
        nomssl = (String) SqlManager.getValueFromVectorParam(datiGARA, 30).getValue();
        prosla = (String) SqlManager.getValueFromVectorParam(datiGARA, 31).getValue();
        loclav = (String) SqlManager.getValueFromVectorParam(datiGARA, 32).getValue();
        teutil = (Long) SqlManager.getValueFromVectorParam(datiGARA, 33).getValue();
        temesi = (Long) SqlManager.getValueFromVectorParam(datiGARA, 35).getValue();
        locint = (String) SqlManager.getValueFromVectorParam(datiGARA, 40).getValue();
      }

      dccAPPA.setValue("APPA.ANSS", numssl);
      dccAPPA.setValue("APPA.ANOMSS", nomssl);
      dccAPPA.setValue("APPA.PROLAV", prosla);
      dccAPPA.setValue("APPA.COMLAV", loclav);
      dccAPPA.setValue("APPA.LOCINT", locint);
      if(teutil!=null){
        dccAPPA.setValue("APPA.TUTULT", teutil);

        if (temesi == null) temesi = new Long(1);
        dccAPPA.setValue("APPA.TUTULTUM", temesi);
      }
      // Ricalcolo DATULT (Data prevista ultimazione lavori)
      Date dinlav = dccAPPA.getData("APPA.DINLAV");
      if (teutil != null && dinlav != null) {

        Date datult = null;
        if (new Long(1).equals(temesi)) {
          // Giorni
          datult = DateUtils.addDays(dinlav, teutil.intValue() - 1);
        } else if (new Long(2).equals(temesi)) {
          // Mesi
          datult = DateUtils.addMonths(dinlav, teutil.intValue());
        }
        dccAPPA.setValue("APPA.DATULT", datult);

        // Ricalcolo DNULT (Data scadenza ultimazione lavori)
        if (datult != null) {
          Long ntotso = dccAPPA.getLong("APPA.NTOTSO");
          Long ntotpr = dccAPPA.getLong("APPA.NTOTPR");
          if (ntotso == null) ntotso = new Long(0);
          if (ntotpr == null) ntotpr = new Long(0);
          Date dnult = DateUtils.addDays(datult, ntotso.intValue() + ntotpr.intValue());
          dccAPPA.setValue("APPA.DNULT", dnult);
        }

      }



      //Atto autorizzativo
      Long tipoAtto = dccAPPA.getLong("APPA.ATTAUTTIPO");
      String numeroAtto = dccAPPA.getString("APPA.ATTAUTNUMERO");
      Timestamp dataAttoTimestamp = dccAPPA.getData("APPA.ATTAUTDATA");
      Date dataAtto = null;
      if(tipoAtto==null && dataAttoTimestamp==null && (numeroAtto==null || "".equals(numeroAtto))){
        if (genere != null && new Long(3).equals(genere)) {
          if (datiGARAGenere3 != null && datiGARAGenere3.size() > 0) {
            tipoAtto = (Long) SqlManager.getValueFromVectorParam(datiGARAGenere3, 15).getValue();
            numeroAtto = (String) SqlManager.getValueFromVectorParam(datiGARAGenere3, 16).getValue();
            dataAtto = (Date) SqlManager.getValueFromVectorParam(datiGARAGenere3, 17).getValue();
          }
        } else {
          tipoAtto = (Long) SqlManager.getValueFromVectorParam(datiGARA, 42).getValue();
          numeroAtto = (String) SqlManager.getValueFromVectorParam(datiGARA, 43).getValue();
          dataAtto = (Date) SqlManager.getValueFromVectorParam(datiGARA, 44).getValue();
        }

        dccAPPA.setValue("APPA.ATTAUTTIPO", this.getTAB1TIP_A2040(tipoAtto));
        dccAPPA.setValue("APPA.ATTAUTNUMERO", numeroAtto);
        dccAPPA.setValue("APPA.ATTAUTDATA", dataAtto);

      }

      String sommeurg=null;
      if (genere != null && new Long(3).equals(genere)) {
        if (datiGARAGenere3 != null && datiGARAGenere3.size() > 0) {
          sommeurg = (String) SqlManager.getValueFromVectorParam(datiGARAGenere3, 18).getValue();

        }
      } else {
        sommeurg = (String) SqlManager.getValueFromVectorParam(datiGARA, 45).getValue();

      }
      dccAPPA.setValue("APPA.SOMMEURG", sommeurg);
      dccAPPA.setValue("APPA.ULTDAT9", null);
    }

    /*
    Object param[] = null;
    String selectGarecont = "select coorba,banapp,codbic from garecont where ngara=? and ncont=?";
    if (genere != null && new Long(3).equals(genere)) {
      if(new Long(1).equals(modcont)){
        selectGarecont = "select coorba,banapp,codbic from garecont where ngara=? and ngaral=?";
        param= new Object[]{codgar1,ngara};
      }else
        param=new Object[]{codgar1,ncont};
    }else
      param=new Object[]{ngara,ncont};

    Vector datiGarecont = this.sqlManager.getVector(selectGarecont, param);
    if(datiGarecont!=null && datiGarecont.size()>0){
      String coorba = (String) SqlManager.getValueFromVectorParam(datiGarecont, 0).getValue();
      String banapp = (String) SqlManager.getValueFromVectorParam(datiGarecont, 1).getValue();
      String codbic = (String) SqlManager.getValueFromVectorParam(datiGarecont, 2).getValue();
      dccAPPA.setValue("APPA.IBAN", coorba);
      dccAPPA.setValue("APPA.BANAPP", banapp);
      dccAPPA.setValue("APPA.BIC", codbic);
    }
    */

    if (logger.isDebugEnabled()) logger.debug("dccAPPA_AltriDati: fine metodo");

  }

  /**
   * Aggiunge le colonne degli altri dati di APPA1.
   *
   * @param dccAPPA1
   * @param ngara
   * @throws GestoreException
   * @throws SQLException
   */
  private void dccAPPA1_AltriDati(DataColumnContainer dccAPPA1, String ngara) throws GestoreException, SQLException {

    if (logger.isDebugEnabled()) logger.debug("dccAPPA1_AltriDati: inizio metodo");

    String selectGARA = "select torn.navvig from gare, torn where gare.codgar1 = torn.codgar and ngara = ?";

    List<?> datiGARA = this.sqlManager.getVector(selectGARA, new Object[] { ngara });
    if (datiGARA != null && datiGARA.size() > 0) {
      dccAPPA1.setValue("APPA1.NAVVIGG", SqlManager.getValueFromVectorParam(datiGARA, 0).getValue());
    }

    if (logger.isDebugEnabled()) logger.debug("dccAPPA1_AltriDati: fine metodo");

  }

  /**
   * Aggiunge le colonne degli altri dati di PERI.
   *
   * @param dccPERI
   * @param ngara
   * @throws GestoreException
   * @throws SQLException
   */
  private void dccPERI_AltriDati(DataColumnContainer dccPERI, String ngara, Long nappal) throws GestoreException, SQLException {

    if (logger.isDebugEnabled()) logger.debug("dccPERI_AltriDati: inizio metodo");

    String selectGARA = "select gare.cupprg, " // 0
        + "v_gare_torn.genere, " // 1
        + "gare1.codcui " // 2
        + "from gare, v_gare_torn, torn, gare1 "
        + "where gare.codgar1 = v_gare_torn.codgar "
        + "and gare.codgar1 = torn.codgar and "
        + "gare.ngara = gare1.ngara and "
        + "gare.ngara = ?";

    List<?> datiGARA = this.sqlManager.getVector(selectGARA, new Object[] { ngara });

    if (datiGARA != null && datiGARA.size() > 0) {
      Long genere = (Long) SqlManager.getValueFromVectorParam(datiGARA, 1).getValue();

      if (! new Long(3).equals(genere) && (nappal!=null && nappal.intValue()==1)) {
        String cupprg = (String) SqlManager.getValueFromVectorParam(datiGARA, 0).getValue();
        dccPERI.setValue("PERI.CUPPRG", cupprg);
        String codcui = (String) SqlManager.getValueFromVectorParam(datiGARA, 2).getValue();
        if(codcui!=null && !"".equals(codcui)){
          dccPERI.setValue("PERI.CUIINT", codcui);
          dccPERI.setValue("PERI.LAVPROGTRI", "1");
        }
      }
    }

    if (logger.isDebugEnabled()) logger.debug("dccPERI_AltriDati: fine metodo");

  }

  /**
   * Aggiunge le colonne degli altri dati di CONT.
   *
   * @param dccCONT
   * @param ngara
   * @param ncont
   * @param dccAPPA
   * @throws GestoreException
   * @throws SQLException
   */
  private boolean dccCONT_AltriDati(DataColumnContainer dccCONT, String ngara, Long ncont, DataColumnContainer dccAPPA) throws GestoreException, SQLException {

    if (logger.isDebugEnabled()) logger.debug("dccCONT_AltriDati: inizio metodo");

    boolean aggAppa=false;
    String selectGARA = "select gare.modlicg, " // 0
        + "gare.tiatto, " // 1
        + "gare.nrepat, " // 2
        + "gare.daatto, " // 3
        + "gare.iaggiu, " // 4
        + "gare.ridiso, " // 5
        + "gare.impgar, " // 6
        + "gare.nquiet, " // 7
        + "gare.dquiet, " // 8
        + "gare.istcre, " // 9
        + "gare.indist, " // 10
        + "gare.ribagg, " // 11
        + "gare.impapp, " // 12
        + "gare.impsic, " // 13
        + "gare.onprge, " // 14
        + "gare.impnrl, " // 15
        + "gare.ditta, " // 16
        + "gare.codgar1, " // 17
        + "v_gare_torn.genere, " // 18
        + "torn.accqua, "  //19
        + "torn.modcont, " //20
        + "gare.riboepv "  //21
        + "from gare, v_gare_torn, torn where ngara = ? and gare.codgar1 = v_gare_torn.codgar and gare.codgar1 = torn.codgar";

    String selectGARECONT = "select lregco, nregco, dregco, numcont, nproat, dproat, dscapo, dconsd, numcont from garecont where ngara = ? and ncont = ? ";

    List<?> datiGARA = this.sqlManager.getVector(selectGARA, new Object[] { ngara });
    if (datiGARA != null && datiGARA.size() > 0) {

      Long modlicg = (Long) SqlManager.getValueFromVectorParam(datiGARA, 0).getValue();

      String ditta = (String) SqlManager.getValueFromVectorParam(datiGARA, 16).getValue();
      String codgar1 = (String) SqlManager.getValueFromVectorParam(datiGARA, 17).getValue();
      Long genere = (Long) SqlManager.getValueFromVectorParam(datiGARA, 18).getValue();
      Long modcont = (Long) SqlManager.getValueFromVectorParam(datiGARA, 20).getValue();

      Double impapp = null;
      Double impsic = null;
      Double onprge = null;
      Double impnrl = null;
      Double iaggiu = null;
      Double impgar = null;
      String accqua = (String)SqlManager.getValueFromVectorParam(datiGARA, 19).getValue();
      if (genere != null && (new Long(3)).equals(genere)) {
        HashMap<String, Double> hMapImporti = new HashMap<String, Double>();
        hMapImporti = this.getImportiOffertaUnica(codgar1, ditta, ngara, modcont);
        if("1".equals(accqua))
          iaggiu = (Double) this.sqlManager.getObject("select impqua from garecont where ngara=? and ncont=?", new Object[]{codgar1, ncont});
        else
          iaggiu = hMapImporti.get("IAGGIU");
        impgar = hMapImporti.get("IMPGAR");
        impapp = hMapImporti.get("IMPAPP");
        impsic = hMapImporti.get("IMPSIC");
        impnrl = hMapImporti.get("IMPNRL");
        onprge = hMapImporti.get("ONPRGE");
      } else {
        if("1".equals(accqua))
          iaggiu = (Double) this.sqlManager.getObject("select impqua from garecont where ngara=? and ncont=?", new Object[]{ngara, ncont});
        else
         iaggiu = (Double) SqlManager.getValueFromVectorParam(datiGARA, 4).getValue();
        impgar = (Double) SqlManager.getValueFromVectorParam(datiGARA, 6).getValue();
        impapp = (Double) SqlManager.getValueFromVectorParam(datiGARA, 12).getValue();
        impsic = (Double) SqlManager.getValueFromVectorParam(datiGARA, 13).getValue();
        onprge = (Double) SqlManager.getValueFromVectorParam(datiGARA, 14).getValue();
        impnrl = (Double) SqlManager.getValueFromVectorParam(datiGARA, 15).getValue();
      }
      if(SqlManager.getValueFromVectorParam(datiGARA, 3).getValue()!=null){
        dccCONT.setValue("CONT.TIATTO", SqlManager.getValueFromVectorParam(datiGARA, 1).getValue());
        dccCONT.setValue("CONT.NREPAT", SqlManager.getValueFromVectorParam(datiGARA, 2).getValue());
        dccCONT.setValue("CONT.DAATTO", SqlManager.getValueFromVectorParam(datiGARA, 3).getValue());
      }
      dccCONT.setValue("CONT.NIMPCO", iaggiu);


      String codlav = dccCONT.getString("CONT.CODLAV");
      Long nappal = dccCONT.getLong("CONT.NAPPAL");
      Long nproat = dccCONT.getLong("CONT.NPROAT");
      Long pcprev = null;
      Long neseco = null;
      List<?> datiCONT = this.sqlManager.getVector("select pcprev,neseco from cont where codlav = ? and nappal = ? and nproat = ?",
              new Object[] { codlav, nappal, nproat });
      if (datiCONT != null && datiCONT.size() > 0) {
    	  pcprev = (Long) SqlManager.getValueFromVectorParam(datiCONT, 0).getValue();
    	  neseco = (Long) SqlManager.getValueFromVectorParam(datiCONT, 1).getValue();
      }

      Double icprev = null;
      if (pcprev != null && iaggiu != null) {
    	  icprev = new Double(pcprev.doubleValue() * iaggiu.doubleValue() / 100.0);
    	  icprev = new Double((double) Math.round(icprev.doubleValue() * 100) / 100);
      }
      dccCONT.setValue("CONT.ICPREV", icprev);

      // Calcolo dell'IVA
      Double impiva = null;
		if (neseco != null && iaggiu != null) {
			impiva = new Double(iaggiu.doubleValue() * neseco.doubleValue() / 100.0);
			if (icprev != null) {
				impiva = new Double(impiva + (icprev.doubleValue() * neseco.doubleValue() / 100.0) + icprev);
			}
			impiva = new Double((double) Math.round(impiva.doubleValue() * 100) / 100);
		}
      dccCONT.setValue("CONT.IMPIVA", impiva);

      Double itotcont = null;
      if (iaggiu != null) {
    	itotcont = new Double(iaggiu.doubleValue());
        if (impiva != null) {
      	  itotcont=new Double(itotcont + impiva.doubleValue());
        }
      }else {
    	  if (impiva != null) {
    		  itotcont=new Double(impiva.doubleValue());
    	  }
      }
      if (itotcont != null) {
      	  itotcont = new Double((double) Math.round(itotcont.doubleValue() * 100) / 100);
      }
      dccCONT.setValue("CONT.ITOTCONT", itotcont);

      Double ribagg = (Double) SqlManager.getValueFromVectorParam(datiGARA, 11).getValue();
      Double riboepv = (Double) SqlManager.getValueFromVectorParam(datiGARA, 21).getValue();

      if (modlicg != null && new Long(6).equals(modlicg)) {
        dccCONT.setValue("CONT.RIAUMI", riboepv);
      } else {
        dccCONT.setValue("CONT.RIAUMI", ribagg);
      }

      dccCONT.setValue("CONT.RIDISO", SqlManager.getValueFromVectorParam(datiGARA, 5).getValue());
      dccCONT.setValue("CONT.IMCADE", impgar);

      // Calcolo della percentuale di cauzione
      Double ribpercauc = null;
      if (modlicg != null && new Long(6).equals(modlicg)) {
  	    if (riboepv != null) {
	 	  ribpercauc = riboepv;
   	    }
      } else {
        if (ribagg != null) {
          ribpercauc = ribagg;
        }
      }
      if(ribpercauc != null) {
    	  double percentualeCauzione = AggiudicazioneManager.calcoloPercentualeCauzione(ribpercauc.doubleValue());
    	  dccCONT.setValue("CONT.PERCAUC", new Double(percentualeCauzione));
      }else {
    	  dccCONT.setValue("CONT.PERCAUC", null);
      }

      dccCONT.setValue("CONT.NPOFID", SqlManager.getValueFromVectorParam(datiGARA, 7).getValue());
      dccCONT.setValue("CONT.DPOFID", SqlManager.getValueFromVectorParam(datiGARA, 8).getValue());

      String istcre = (String) SqlManager.getValueFromVectorParam(datiGARA, 9).getValue();
      String indist = (String) SqlManager.getValueFromVectorParam(datiGARA, 10).getValue();
      String iscred = null;
      if (istcre != null && !"".equals(istcre)) {
        iscred = istcre;
      }
      if (indist != null && !"".equals(indist)) {
        if (iscred != null) {
          iscred += " - " + indist;
        } else {
          iscred = indist;
        }
      }
      dccCONT.setValue("CONT.ISCRED", iscred);

      Double implatc = new Double(0);
      if (impapp != null) {
        implatc = new Double(implatc.doubleValue() + impapp.doubleValue());
      }
      if (impsic != null) {
        implatc = new Double(implatc.doubleValue() - impsic.doubleValue());
      }
      if (onprge != null) {
        implatc = new Double(implatc.doubleValue() - onprge.doubleValue());
      }
      if (impnrl != null) {
        implatc = new Double(implatc.doubleValue() - impnrl.doubleValue());
      }

      if (implatc != null) {
        implatc = new Double((double) Math.round(implatc.doubleValue() * 100) / 100);
      }

      dccCONT.setValue("CONT.IMPLOR", impapp);
      dccCONT.setValue("CONT.IMPLATC", implatc);
      dccCONT.setValue("CONT.IMPLASC", impsic);
      dccCONT.setValue("CONT.IMPLANR", impnrl);

      //Nel caso di genere=3 nel campo ngara di GARECONT è presente il valore della gara complementare
      //e non quello del lotto.
      String valoreChiaveGarecont= ngara;
      if (genere != null && (new Long(3)).equals(genere))
        valoreChiaveGarecont =codgar1;
      List<?> datiGARECONT = this.sqlManager.getVector(selectGARECONT, new Object[] { valoreChiaveGarecont, ncont});
      if (datiGARECONT != null && datiGARECONT.size() > 0) {
        if(SqlManager.getValueFromVectorParam(datiGARECONT, 2).getValue()!=null){
          dccCONT.setValue("CONT.LREGCO", SqlManager.getValueFromVectorParam(datiGARECONT, 0).getValue());
          dccCONT.setValue("CONT.NREGCO", SqlManager.getValueFromVectorParam(datiGARECONT, 1).getValue());
          dccCONT.setValue("CONT.DREGCO", SqlManager.getValueFromVectorParam(datiGARECONT, 2).getValue());
          dccCONT.setValue("CONT.NUMEROCONTRATTO", SqlManager.getValueFromVectorParam(datiGARECONT, 3).getValue());
        }
        if(SqlManager.getValueFromVectorParam(datiGARECONT, 4).getValue()!=null){
            dccCONT.setValue("CONT.NUMEROPROTOCOLLO", SqlManager.getValueFromVectorParam(datiGARECONT, 4).getValue());
          }
        if(SqlManager.getValueFromVectorParam(datiGARECONT, 5).getValue()!=null){
            dccCONT.setValue("CONT.DATAPROTOCOLLO", SqlManager.getValueFromVectorParam(datiGARECONT, 5).getValue());
          }
        if(SqlManager.getValueFromVectorParam(datiGARECONT, 6).getValue()!=null){
          dccCONT.setValue("CONT.DSCADCADE", SqlManager.getValueFromVectorParam(datiGARECONT, 6).getValue());
        }
        if(SqlManager.getValueFromVectorParam(datiGARECONT, 7).getValue()!=null){
          dccAPPA.setValue("APPA.DCONSD", SqlManager.getValueFromVectorParam(datiGARECONT, 7).getValue());
          dccAPPA.setValue("APPA.DINLAV", SqlManager.getValueFromVectorParam(datiGARECONT, 7).getValue());
          aggAppa =true;
        }
        if(SqlManager.getValueFromVectorParam(datiGARECONT, 8).getValue()!=null){
          dccCONT.setValue("CONT.NUMEROCONTRATTO", SqlManager.getValueFromVectorParam(datiGARECONT, 8).getValue());
        }
      }

    }

    if (logger.isDebugEnabled()) logger.debug("dccCONT_AltriDati: fine metodo");

    return aggAppa;

  }

  /**
   * Metodo per determinare il valore del TAB1TIP relativo al tabellato A2040
   * sulla base della descrizione del tabellato A2045.
   *
   * @param tattoa
   * @return
   * @throws GestoreException
   */
  private Long getTAB1TIP_A2040(Long tab1tip_A2045) throws GestoreException {
    Long result = null;

    if (tab1tip_A2045 != null) {
      String descrTabA2045Originale = tabellatiManager.getDescrTabellato("A2045", tab1tip_A2045.toString()).trim();
      String descrTabA2045 = descrTabA2045Originale.toUpperCase();

      List<?> listaTabellatoA2040 = tabellatiManager.getTabellato("A2040");

      boolean trovataDescrNelTabellatoA2040 = false;
      for (int i = 0; i < listaTabellatoA2040.size() && !trovataDescrNelTabellatoA2040; i++) {
        Tabellato tabellato = (Tabellato) listaTabellatoA2040.get(i);
        if (descrTabA2045.equals(tabellato.getDescTabellato().trim().toUpperCase())) {
          result = new Long(tabellato.getTipoTabellato());
          trovataDescrNelTabellatoA2040 = true;
        }
      }
      if (!trovataDescrNelTabellatoA2040) {
        // Nel tabellato A2040 non e' stata trovata nessuna occorrenza con
        // descrizione uguale ad una delle descrizioni del tabellato A2045
        // (dopo aver fatto trim e upperCase di entrambi i campi).
        // Si prosegue con l'inserimento della occorrenza del tabellato A2045
        // nel tabellato A2040
        try {
          Long tab1tip = (Long) this.sqlManager.getObject("select max(TAB1TIP) from TAB1 where tab1cod = ?", new Object[] { "A2040" });
          tab1tip = new Long(tab1tip.longValue() + 1);
          this.sqlManager.update("insert into TAB1 (TAB1COD, TAB1TIP, TAB1DESC, TAB1NORD) values (?, ?, ?, ?)", new Object[] { "A2040",
              tab1tip, descrTabA2045Originale, new Double(0) });
          result = tab1tip;

        } catch (SQLException sqle) {
          throw new GestoreException("Errore nell'inserimento di una nuova occorrenza per il tabellato A2040", null, sqle);
        }
      }
    }
    return result;
  }

  /**
   * Ricava la descrizione del campo di una entità.
   *
   * @param entita
   * @param campo
   * @return
   */
  private String getDescrizioneCampo(String entita, String campo) {
    String descrizione = "";
    try {
      Campo c = DizionarioCampi.getInstance().getCampoByNomeFisico(entita + "." + campo);
      descrizione = c.getDescrizioneWEB();
    } catch (Throwable t) {

    }

    return descrizione;
  }

}
