/*
 * Created on 7-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pl.bl;

import java.util.Arrays;
import java.util.Vector;

/**
 * Classe che ritorna l'elenco delle tabelle da utilizzare in una delete in un
 * gestore entità. Fornisce l'elenco nel formato previsto per il GeneManager.<br>
 * Design Pattern: Singleton
 * 
 * @author Stefano.Sabbadin
 * 
 */
public class TabelleDelete {

  // ELENCO DELLE TIPOLOGIE DI DELETE POSSIBILI
  public static final short          DELETE_RAPPORTINI_ITC = 10;
  public static final short          DELETE_ITC            = 20;
  public static final short          DELETE_APPR           = 30;
  public static final short          DELETE_CONT           = 40;
  public static final short          DELETE_PSAL           = 50;
  public static final short          DELETE_APPA           = 60;
  public static final short          DELETE_PERI           = 70;
  public static final short          DELETE_SUBA           = 80;

  private TabelleDeleteRapportiniITC deleteRapportiniITC;
  private TabelleDeleteITC           deleteITC;
  private TabelleDeleteAPPR          deleteAPPR;
  private TabelleDeleteCONT          deleteCONT;
  private TabelleDeletePSAL          deletePSAL;
  private TabelleDeleteAPPA          deleteAPPA;
  private TabelleDeletePERI          deletePERI;
  private TabelleDeleteSUBA          deleteSUBA;

  /** Istanza unica e condivisa della classe */
  private static TabelleDelete       instance;

  /**
   * Costruttore privato degli elenchi tabelle contenuti nella classe
   */
  private TabelleDelete() {
    this.deleteRapportiniITC = new TabelleDeleteRapportiniITC();
    this.deleteITC = new TabelleDeleteITC();
    this.deleteAPPR = new TabelleDeleteAPPR();
    this.deleteCONT = new TabelleDeleteCONT();
    this.deletePSAL = new TabelleDeletePSAL();
    this.deleteAPPA = new TabelleDeleteAPPA();
    this.deletePERI = new TabelleDeletePERI();
    this.deleteSUBA = new TabelleDeleteSUBA();
  }

  /**
   * Metodo statico per ottenere l'unica referenza al dizionario. Viene creato
   * l'oggetto solo la prima volta, le altre volte l'oggetto viene semplicemente
   * restituito
   * 
   * @return dizionario con l'elenco delle tabelle da eliminare per ogni
   *         tipologia di delete
   */
  public static TabelleDelete getInstance() {
    if (instance != null) return instance;

    synchronized (TabelleDelete.class) {
      if (instance == null) {
        instance = new TabelleDelete();
      }
    }
    return instance;
  }

  /**
   * Ritorna per la tipologia di cancellazione in input l'elenco delle tabelle
   * da cancellare
   * 
   * @param tipologiaDiCancellazione
   *        cancellazione da eseguire, a partire dall'elenco di costanti
   *        DELETE_XXX della classe
   * @return string array con i nomi delle tabelle da usare nella delete
   */
  public String[] getElencoTabelle(short tipologiaDiCancellazione) {
    String[] elenco = null;
    Vector tabelle = null;

    switch (tipologiaDiCancellazione) {
    case DELETE_RAPPORTINI_ITC:
      tabelle = this.deleteRapportiniITC;
      break;
    case DELETE_ITC:
      tabelle = this.deleteITC;
      break;
    case DELETE_APPR:
      tabelle = this.deleteAPPR;
      break;
    case DELETE_CONT:
      tabelle = this.deleteCONT;
      break;
    case DELETE_PSAL:
      tabelle = this.deletePSAL;
      break;
    case DELETE_APPA:
      tabelle = this.deleteAPPA;
      break;
    case DELETE_PERI:
      tabelle = this.deletePERI;
      break;
    case DELETE_SUBA:
      tabelle = this.deleteSUBA;
    default:
      break;
    }

    // se è stato selezionato un elenco di tabelle, allora si converte l'elenco
    // in string array
    if (tabelle != null) elenco = (String[]) tabelle.toArray(new String[0]);

    return elenco;
  }

  // INNER CLASS PER OGNI TIPOLOGIA DI DELETE

  /**
   * Elenco delle tabelle da utilizzare per l'eliminazione dei rapportini di un
   * intervento
   */
  private class TabelleDeleteRapportiniITC extends Vector {

    /** UID */
    private static final long serialVersionUID = -5474392763769356970L;

    public TabelleDeleteRapportiniITC() {
      super();
      this.addAll(Arrays.asList(new String[] { "MATRAP", "OPERAP" }));
    }
  }

  /**
   * Elenco delle tabelle da utilizzare per l'eliminazione di un intervento
   * (tabella ITC degli interventi esclusa)
   */
  private class TabelleDeleteITC extends Vector {

    /** UID */
    private static final long serialVersionUID = -598494668258193833L;

    public TabelleDeleteITC() {
      super();
      this.addAll(Arrays.asList(new String[] { "RAPINT", "SEGINT", "ITCELE" }));
    }
  }

  /**
   * Elenco delle tabelle da utilizzare per l'eliminazione di
   * un'approvazione/perizia (tabella APPR esclusa)
   */
  private class TabelleDeleteAPPR extends Vector {

    /** UID */
    private static final long serialVersionUID = -1212470879985307626L;

    public TabelleDeleteAPPR() {
      super();
      this.addAll(Arrays.asList(new String[] { "AGGI", "G2TECN", "DFINA", "R2IMPF", "R2VERI", "DELI", "SOMM", "FINA", "G2PARE", "G2CDSP", "ULTAPP_P" }));
    }
  }

  /**
   * Elenco delle tabelle da utilizzare per l'eliminazione di un contratto
   * (tabella CONT esclusa)
   */
  private class TabelleDeleteCONT extends Vector {

    /** UID */
    private static final long serialVersionUID = -7247297625442234031L;

    public TabelleDeleteCONT() {
      super();
      this.addAll(Arrays.asList(new String[] { "G2CONPEG", "CESSCRED" }));
    }
  }

  /**
   * Elenco delle tabelle da utilizzare per l'eliminazione di un SAL (tabella
   * PSAL esclusa)
   */
  private class TabelleDeletePSAL extends Vector {

    /** UID */
    private static final long serialVersionUID = -6284567456682475315L;

    public TabelleDeletePSAL() {
      super();
      this.addAll(Arrays.asList(new String[] { "BGLPAG" }));
    }
  }

  /**
   * Elenco delle tabelle da utilizzare per l'eliminazione di un appalto
   * (tabella APPA esclusa)
   */
  private class TabelleDeleteAPPA extends Vector {

    /** UID */
    private static final long serialVersionUID = -6485210238887916311L;

    public TabelleDeleteAPPA() {
      super();
      this.addAll(Arrays.asList(new String[] { "CONT", "TEMP1", "PSAL", "CONTAB", "SUBA", "RISERVE", "ORDSERV", "NUOPRZ", "VERPRZ",
          "APPA1", "ITCORD", "CATAPP", "ULTAPP", "RAGDET", "APPACIG", "RIVAPREZ", "FIDEJUSSIONE", "CERTIFICATO", "CERT_DETRA", "CERT_SOMMA", "ULTAPP_L" }));
      this.addAll(new TabelleDeleteCONT());
      this.addAll(new TabelleDeletePSAL());
      this.addAll(new TabelleDeleteSUBA());
    }
  }

  /**
   * Elenco delle tabelle da utilizzare per l'eliminazione di un lavoro (tabella
   * PERI dei lavori esclusa)
   */
  private class TabelleDeletePERI extends Vector {

    /** UID */
    private static final long serialVersionUID = -3580667887449402234L;

    public TabelleDeletePERI() {
      // {MF061207} Aggiunta la tabella FASI
      super();
      this.addAll(Arrays.asList(new String[] { "APPR", "APPA", "ESPR", "COLLAUD", "TECCOLL", "CDCO", "ISCS", "ORDI", "G2ORDICDC", "PESP",
          "G2ELAB", "DATISTA", "G2FONTE", "G2OPPU", "OPELAV", "DATICUP", "G2DURC", "G_PERMESSI", "CRONO", "FASI" }));
      this.addAll(new TabelleDeleteAPPR());
      this.addAll(new TabelleDeleteAPPA());
    }
  }

  /**
   * Elenco delle tabelle da utilizzare per l'eliminazione del subappalto
   * 
   */
  private class TabelleDeleteSUBA extends Vector {

    /** UID */
    private static final long serialVersionUID = -1586751449845025877L;

    public TabelleDeleteSUBA() {
      super();
      this.addAll(Arrays.asList(new String[] { "SUBA_ER" }));
    }
  }

}
