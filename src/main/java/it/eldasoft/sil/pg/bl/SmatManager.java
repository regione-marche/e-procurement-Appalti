/*
 * Created on 12/12/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.tags.gestori.submit.GestoreDITG;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Manager che raccoglie alcune funzionalità per SMAT
 *
 * @author Cristian.Febas
 */
public class SmatManager {

  /** Logger */
  static Logger       logger = Logger.getLogger(SmatManager.class);

  /** Manager di PG */
  private PgManager  pgManager ;

  /** Manager SQL per le operazioni su database */
  private SqlManager  sqlManager;

  /** Manager per le transazioni e selezioni nel database */
  private GeneManager         geneManager;

  /** Manager per l'interrogazione dei tabellati */
  private TabellatiManager    tabellatiManager;


  private GestoreDITG gestoreDITG;

  /**
   * Set SqlManager
   *
   * @param sqlManager
   */
  public void setPgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }

  /**
   * Set SqlManager
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * @param geneManager
   *        geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  /**
   * @return Ritorna geneManager.
   */
  public GeneManager getGeneManager() {
    return geneManager;
  }

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  public void setGestoreDITG (GestoreDITG gestoreDITG) {
    this.gestoreDITG = gestoreDITG;
  }


  /**
   * Funzione che esegue il calcolo delle codifica automatica specifica per le
   * gare ad offerta unica. Questo metodo e' stato sviluppato a partire dal metodo
   * GeneManager.calcolaCodificaAutomatica del progetto Gene e rappresenta una
   * customizzazione
   *
   * @param entita
   *        Entita = GARE o TORN
   * @param isGaraLottoUnico
   * @param codiceGaraLotti
   *        Codice della gara a lotti alla quale si sta creando un nuovo lotto
   *
   * @return HashMap contenente due oggetti associati alle chiavi 'codiceGara' e
   *         'numeroGara'
   * @throws GestoreException
   */
  public HashMap calcolaCodificaAutomaticaLotto(String codiceGaraLotti, Long progressivoLotto)
      throws GestoreException {
    // HashMap per contenere due stringhe che rappresentano CODGAR e NGARA
    // con chiave 'codiceGara' e 'numeroGara' rispettivamente
    HashMap result = new HashMap();

    String codiceGara = null;
    String numeroGara = null;
    if (codiceGaraLotti != null && codiceGaraLotti.length() > 0) {
              // Generare NGARA per il lotto di una gara divisa in lotti, dove
              // NGARA = CODGAR + suffisso generato usando il record di
              // G_CONFCOD relativo all'entita' GARE
      numeroGara = this.getNumeroGaraCodificaAutomatica(codiceGaraLotti, progressivoLotto);
      codiceGara = new String(codiceGaraLotti);
     } else{
            throw new GestoreException(
                  "Errore nel calcolo del numero della "
                      + "gara (GARE.NGARA) per un nuovo lotto della gara a partire "
                      + "dal codice della gara a lotti di appartenenza: '"
                      + codiceGaraLotti
                      + "'", null);
     }

    result.put("codiceGara", codiceGara);
    result.put("numeroGara", numeroGara);
    return result;
  }

  private String getNumeroGaraCodificaAutomatica(String codiceGara,
      Long progressivoLotto) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("getNumeroGaraCodificaAutomatica: inizio metodo");

    // Numero massimo di tentativi per generare il numero della gara
    int NUMERO_TENTATIVI_NGARA = 25;

    String result = null;
    String entita = "GARE";
    String campo = "NGARA";
    String tmpCodiceGara = new String(codiceGara);

    int maxLungCodiceGara = 14; // Costante, a meno della valorizzazione del tabellato A1050
    String strMaxLunghezzaCodiceGara = tabellatiManager.getDescrTabellato( "A1050", "1");
    if (strMaxLunghezzaCodiceGara != null
        && strMaxLunghezzaCodiceGara.length() > 0) {
      Integer tmp1 = UtilityNumeri.convertiIntero(strMaxLunghezzaCodiceGara.substring(
          0, strMaxLunghezzaCodiceGara.indexOf(" ")));
      if (tmp1 != null) {
        if (tmp1.intValue() > 0 && tmp1.intValue() < 20)
          maxLungCodiceGara = tmp1.intValue();
      }
    }

    if (codiceGara.length() > maxLungCodiceGara)
      tmpCodiceGara = tmpCodiceGara.substring(0, maxLungCodiceGara);

    JdbcParametro parametroCriterioNGARA = null;
    Long contatore = null;

    try {
      HashMap parametri = this.geneManager.getParametriCodificaAutomatica(
          entita, campo);

      if (parametri != null) {
        contatore = (Long) parametri.get("contatore");
        parametroCriterioNGARA = (JdbcParametro) parametri.get("parametro");
      }

      if (progressivoLotto == null) {
        // Per capire da che numero far partire il contatore, si determina il
        // numero di lotti esistenti e il contatore lo si incrementa di uno

        // oppure lo si fa partire da zero
        // MOD SS101109 - Non considera nel conteggio dei lotti l'eventuale
        // occ. in GARE complem.a TORN per le gare a offerta unica

        Long numeroLottiEsistenti = (Long) sqlManager.getObject(
            "select count(*) from GARE where CODGAR1 = ? and "
                + "(GENERE is null or GENERE<>3)", new Object[] { codiceGara });

        if (numeroLottiEsistenti != null
            && numeroLottiEsistenti.longValue() > 0)
          contatore = new Long(numeroLottiEsistenti.longValue());
        else
          contatore = new Long(0);
      } else
        // Decremento il progressivo del lotto, perche' nel calcolo vero e
        // proprio del codice lotto viene subito incrementato
        contatore = new Long(progressivoLotto.longValue() - 1);

      if (parametroCriterioNGARA != null) {
        String criterioNGARA = parametroCriterioNGARA.stringValue();
        long tmpContatore = contatore.longValue();
        boolean codiceUnivoco = false;
        int numeroTentativi = 0;
        StringBuffer strBuffer = null;

        while (!codiceUnivoco && numeroTentativi < NUMERO_TENTATIVI_NGARA) {

          strBuffer = new StringBuffer("");
          tmpContatore++;

          strBuffer = this.geneManager.calcoloCodiceAutomatico(tmpContatore,
              criterioNGARA);
          result = tmpCodiceGara + new String(strBuffer);

          try {
            this.verificaPreliminare(codiceGara, result, true);
            codiceUnivoco = true;
          } catch (GestoreException g) {
            if ("verificaPreCopiaGara.codiceGaraDestinazioneEsistente".equals(g.getCodice())) {
              numeroTentativi++;
              // Ripristino il valore del criterio con cui calcolare codice
              // della gara
              criterioNGARA = parametroCriterioNGARA.stringValue();
            }
          }
        }

        if (!codiceUnivoco && numeroTentativi >= NUMERO_TENTATIVI_NGARA) {
          throw new GestoreException("Codifica automatica non riuscita dopo "
              + NUMERO_TENTATIVI_NGARA
              + " tentativi a causa del disallineamento del contatore è "
              + "rispetto ai codici inseriti", "erroreCodiceCodificaAutomatica");
        }

        if (logger.isDebugEnabled())
          logger.debug("getNumeroGaraCodificaAutomatica: fine metodo");

        return result;
      } else {
        throw new GestoreException("Codice di codifica automatica sbagliato",
            "erroreCodiceCodificaAutomatica");
      }
    } catch (GestoreException e) {
      throw e;
    } catch (Throwable t) {
      throw new GestoreException(
          "Errore nel calcolo della codifica automatica per "
              + entita
              + "."
              + campo, "getNumeroGaraCodificaAutomatica", t);
    }
  }

  private void verificaPreliminare(String codiceGaraDestinazione, String nGaraDestinazione, boolean copiaComeLotto)

  throws GestoreException {

    String tmpCodGarDestinazione = new String(codiceGaraDestinazione);

    if (nGaraDestinazione != null && nGaraDestinazione.length() > 0) {
      // Verifico la non esistenza del numero di gara destinazione
      try {
        List ret = this.sqlManager.getVector(
            "select 1 from TORN where CODGAR = ? or CODGAR = ?", new Object[] {
                nGaraDestinazione, "$".concat(nGaraDestinazione) });

        List ret1 = this.sqlManager.getVector(
            "select 1 from GARE where NGARA = ?",
            new Object[] { nGaraDestinazione });

        if ((ret != null && ret.size() > 0)
            || (ret1 != null && ret1.size() > 0))
          throw new GestoreException(
              "Il numero della gara di destinazione è già esistente",
              "verificaPreCopiaGara.nGaraDestinazioneEsistente");
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella selezione del numero della gara di destinazione",
            "verificaPreCopiaGara", e);
      }
    }
  }

  /**
   * Funzione che esegue l'inserimento automatico del primo lotto in
   * gare ad offerta unica.
   *
   * @param codiceGara
   *        Codice della gara a lotti alla quale si sta creando un nuovo lotto
   * @param codiceLotto
   *        Codice del lotto appena calcolato con lam codifica automatica
   * @param numeroRda
   *        numero rda presente all'atto della creazione della gara
   * @param datiForm
   *        dati presenti nella maschera
   * @throws GestoreException
   */

  public int insPrimoLotto(String codiceGara, String codiceLotto, String numeroRda, DataColumnContainer datiForm)
    throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("insPrimoLotto: inizio metodo");
    String oggetto = null;
    String precut = tabellatiManager.getDescrTabellato("A1018", "1");
    String codiga =  "1";
    int tipgen = datiForm.getLong("TORN.TIPGEN").intValue();
    Long tipgar = datiForm.getLong("TORN.TIPGAR");
    Long modastg = datiForm.getLong("GARE.MODASTG");
    Long ribcal = datiForm.getLong("GARE.RIBCAL");
    String sicinc = datiForm.getString("GARE.SICINC");
    Long modlic = datiForm.getLong("TORN.MODLIC");
    Long critlic = datiForm.getLong("TORN.CRITLIC");
    Long detlic = datiForm.getLong("TORN.DETLIC");
    String calcsoan = datiForm.getString("TORN.CALCSOAN");
    Long applegreg = datiForm.getLong("TORN.APPLEGREG");
    String valtec = datiForm.getString("TORN.VALTEC");
    Long ultdetlic = datiForm.getLong("TORN.ULTDETLIC");
    if(critlic==null || ("1".equals(critlic) && detlic== null && (calcsoan==null || "".equals(calcsoan)))){
        throw new GestoreException("I dati per determinare il criterio di aggiudicazione non sono completi","criterioAggiudicazionegNoDati");
     }
     if(modlic== null){
       modlic = pgManager.getMODLICG(critlic, detlic, calcsoan, applegreg);
     }

     String tabellato = PgManager.getTabellatoPercCauzioneProvvisoria(tipgen);
     String descrPercentuale = tabellatiManager.getDescrTabellato(tabellato, "1");
     Double percentuale = UtilityNumeri.convertiDouble(descrPercentuale,
         UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE);

    String selectRda="select data_approvazione,descrizione,valore from v_smat_rda where numero_rda = ?";


    try {
      Vector datiRda = sqlManager.getVector(selectRda, new Object[] { numeroRda });
      if (datiRda != null && datiRda.size()>0){
        oggetto = (String) ((JdbcParametro) datiRda.get(1)).getValue();
      }

      this.sqlManager.update("insert into GARE (ngara, codgar1, codiga, not_gar, precut, tipgarg, modlicg," +
      		" critlicg, detlicg, calcsoang, applegregg, estimp, pgarof, modastg, ribcal, sicinc, onsogrib)" +
      		" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
          new Object[]{codiceLotto, datiForm.getString("TORN.CODGAR"),codiga, oggetto, precut, tipgar, modlic ,
          critlic, detlic, calcsoan, applegreg, "1", percentuale, modastg, ribcal, sicinc, "1"});

      this.sqlManager.update("insert into GARE1 (ngara,codgar1,valtec,ultdetlic) values(?,?,?,?)",
          new Object[]{codiceLotto, datiForm.getString("TORN.CODGAR"),valtec,ultdetlic});

      this.sqlManager.update("insert into GARECONT (ngara,ncont) values(?,?)",
          new Object[]{codiceLotto , new Long(1)});

    } catch (SQLException e) {
      throw new GestoreException("Errore nell'inizializzazione delle lavorazioni " +
          " della gara  (" + codiceLotto +")", null, e);
    }

    if (logger.isDebugEnabled()) logger.debug("insPrimoLotto: fine metodo");

    return 0;
  }

  public int insLavorazioniLotto(String codiceGara, String codiceLotto, String numeroRda)
    throws GestoreException {

    logger.debug("insLavorazioniLotto: inizio metodo");
    //e carico anche le lavorazioni
    String selectLineeRda="select codice_articolo,descrizione,quantita,prezzo,id_linea,unita_misura" +
    " from v_smat_rda_linee where numero_rda = ?";
    try {
      Long maxContafEsistente = (Long) sqlManager.getObject(
          "select coalesce(max(contaf),0) from GCAP where ngara = ?", new Object[] { codiceLotto });
      List datiLineeRda = sqlManager.getListVector(selectLineeRda, new Object[] { numeroRda });
      if (datiLineeRda != null && datiLineeRda.size()>0){
        Double prezzoTotale = 0.0;
        for (int i = 0; i < datiLineeRda.size(); i++) {
          Double prezzo = null;
          Double quantita = null;
          Double idLinea = null;
          String codice_articolo = (String) SqlManager.getValueFromVectorParam( datiLineeRda.get(i), 0).getValue();
          String descrizione = (String) SqlManager.getValueFromVectorParam( datiLineeRda.get(i), 1).getValue();
          Object objQuantita = SqlManager.getValueFromVectorParam( datiLineeRda.get(i), 2).getValue();
          if (objQuantita instanceof Long){
            quantita = ((Long) objQuantita).doubleValue();
          }else{
            if(objQuantita instanceof Double){
              quantita = (Double) objQuantita;
            }
          }
          Object objPrezzo = SqlManager.getValueFromVectorParam( datiLineeRda.get(i), 3).getValue();
          if (objPrezzo instanceof Long){
            prezzo = ((Long) objPrezzo).doubleValue();
          }else{
            if(objPrezzo instanceof Double){
              prezzo = (Double) objPrezzo;
            }
          }
          Object objIdLinea = SqlManager.getValueFromVectorParam( datiLineeRda.get(i), 4).getValue();
          if (objIdLinea instanceof Long){
            idLinea = ((Long) objIdLinea).doubleValue();
          }else{
            if(objIdLinea instanceof Double){
              idLinea = (Double) objIdLinea;
            }
          }
          String unitaMisura = (String) SqlManager.getValueFromVectorParam( datiLineeRda.get(i), 5).getValue();
          String strSqlInsert="insert into GCAP (NGARA,CONTAF,NORVOC,CODVOC,QUANTI,PREZUN,CLASI1," +
          "SOLSIC,SOGRIB,VOCE,IDVOC,UNIMIS) values (?,?,?,?,?,?,?,?,?,?,?,?)";
          this.sqlManager.update(strSqlInsert, new Object[]{codiceLotto, new Long(maxContafEsistente+i+1),
            new Long(maxContafEsistente+i+1),codice_articolo,quantita,prezzo,new Long(3),"2","2",descrizione,idLinea,unitaMisura});
        }


      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nell'inizializzazione delle lavorazioni " +
          " della gara  (" + codiceLotto +")", null, e);
    }

    if (logger.isDebugEnabled()) logger.debug("insLavorazioniLotto: fine metodo");

    return 0;


  }
  public int delLavorazioniLotto(String codiceGara, String codiceLotto, String numeroRda)
  throws GestoreException {
    if (logger.isDebugEnabled()) logger.debug("delLavorazioniLotto: inizio metodo");

    try {

      this.sqlManager.update("delete from GCAP where ngara = ? and idvoc in (select id_linea from " +
      		"v_smat_rda_linee where numero_rda = ? )", new Object[]{ codiceLotto, numeroRda});

    } catch (SQLException e) {
      throw new GestoreException("Errore nell'inizializzazione delle lavorazioni " +
          " della gara  (" + codiceGara +")", null, e);
    }

    if (logger.isDebugEnabled()) logger.debug("delLavorazioniLotto: fine metodo");

    return 0;

  }

  public int delOffertaDitta(String codiceGara, String codiceLotto, String codiceDitta)
  throws GestoreException {
    if (logger.isDebugEnabled()) logger.debug("delOffertaDitta: inizio metodo");

    try {

      this.sqlManager.update("delete from DPRE where ngara = ? and dittao = ? ",
          new Object[]{ codiceLotto, codiceDitta});

    } catch (SQLException e) {
      throw new GestoreException("Errore nella eliminazione della offerta " +
          " della ditta  (" + codiceDitta +")", null, e);
    }

    if (logger.isDebugEnabled()) logger.debug("delOffertaDitta: fine metodo");

    return 0;

  }

  public int updLotto(String codiceGara, String codiceLotto)
    throws GestoreException {
    if (logger.isDebugEnabled()) logger.debug("updLotto: inizio metodo");

    String selectSumGcap="select sum(coalesce(prezun*quanti,0)) from gcap where ngara = ?";
    Object objImpSumGcap = null;
    Double impSumGcap = null;

    try {
      objImpSumGcap = sqlManager.getObject(selectSumGcap, new Object[] { codiceLotto });
      if (objImpSumGcap instanceof Long){
        impSumGcap = ((Long) objImpSumGcap).doubleValue();
      }else{
        if(objImpSumGcap instanceof Double){
          impSumGcap = (Double) objImpSumGcap;
        }
      }

      //Aggiorno alcuni dati del lotto
      this.sqlManager.update("update GARE set IMPAPP = ? where codgar1 = ? and ngara = ? ",
          new Object[]{impSumGcap, codiceGara, codiceLotto});


    } catch (SQLException e) {
      throw new GestoreException("Errore nell'inizializzazione delle lavorazioni " +
          " della gara  (" + codiceGara +")", null, e);
    }

    if (logger.isDebugEnabled()) logger.debug("updLotto: fine metodo");

    return 0;

  }

  public Double updImportoTotaleTorn(String codiceGara) throws GestoreException{
    if (logger.isDebugEnabled()) logger.debug("updImportoTotaleTorn: inizio metodo");
    String select = "select sum(coalesce(impapp,0)) from gare where codgar1=? and ngara <> ?";
    try {
      Double importoTotale = new Double(0);
      Object importoTemp = sqlManager.getObject(select, new Object[] {codiceGara, codiceGara });
      if (importoTemp != null) {
        if (importoTemp instanceof Long) {
          importoTotale = new Double(((Long) importoTemp));
        } else if (importoTemp instanceof Double) {
          importoTotale = new Double((Double) importoTemp);
        }
      }
      importoTotale = new Double(importoTotale.doubleValue());
      if (logger.isDebugEnabled()) logger.debug("updImportoTotaleTorn: fine metodo");
      return importoTotale;
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante la lettura degli importi dei lotti di gara", null, e);
    }


  }

  public int verificaNumeroRdaLibero(String numeroRda) throws GestoreException{

    String select = "select count(*) from garatt where nattot = ? and tattot =4 ";
    Long countNumeroRdaEsistente;
    try {
      countNumeroRdaEsistente = (Long) sqlManager.getObject(
          select, new Object[] { numeroRda });
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante il conteggio delle presenze del numero RdA", null, e);
    }
    int countNRE = countNumeroRdaEsistente.intValue();
    if(countNRE > 0){
      return -1;
    }

    return 0;
  }



  public int insOfferteLotto(String codiceGara, String numeroGara, Long idOrdine, String codiceDitta)
    throws GestoreException {

  logger.debug("insOfferteLotto: inizio metodo");

  this.delOffertaDitta(codiceGara, numeroGara, codiceDitta);
  //e carico anche le offerte
  String selectGCAP="select contaf, idvoc, norvoc, codvoc, unimis, voce, numpar" +
  " from gcap where ngara = ?";
  List datiGCAP;
  try {
      datiGCAP = sqlManager.getListVector(selectGCAP, new Object[] { numeroGara });
      if (datiGCAP != null && datiGCAP.size() > 0) {
        for (int i = 0; i < datiGCAP.size(); i++) {
          Long contaf = (Long) SqlManager.getValueFromVectorParam(datiGCAP.get(i), 0).getValue();
          Double idvoc = (Double)SqlManager.getValueFromVectorParam(datiGCAP.get(i), 1).getValue();
          Double norvoc = (Double)SqlManager.getValueFromVectorParam(datiGCAP.get(i), 2).getValue();
          String codvoc = (String)SqlManager.getValueFromVectorParam(datiGCAP.get(i), 3).getValue();
          String unitaMisura = (String)SqlManager.getValueFromVectorParam(datiGCAP.get(i), 4).getValue();
          String voce = (String)SqlManager.getValueFromVectorParam(datiGCAP.get(i), 5).getValue();
          Long numpar = (Long) SqlManager.getValueFromVectorParam(datiGCAP.get(i), 6).getValue();

          String selectLineeOrdini = "select quantita,prezzo_unitario,descrizione from v_smat_ordini_linee " +
          		" where id_linea = ? and id_ordine = ?";
          List datiLineeOrdini;
          try {
            datiLineeOrdini = sqlManager.getListVector(selectLineeOrdini, new Object[] {idvoc,idOrdine });
            if (datiLineeOrdini != null && datiLineeOrdini.size() > 0) {
              //dovrei trovare una sola riga
              Double prezzoTotale = 0.0;
              Double quantita = 0.0;
              Double prezzoUnitario = 0.0;
              for (int j = 0; j < datiLineeOrdini.size(); j++) {
                Object objQuantita = SqlManager.getValueFromVectorParam(datiLineeOrdini.get(j), 0).getValue();
                if (objQuantita instanceof Long){
                  quantita = ((Long) objQuantita).doubleValue();
                }else{
                  if(objQuantita instanceof Double){
                    quantita = (Double) objQuantita;
                  }
                }
                Object objPrezzoUnitario = SqlManager.getValueFromVectorParam(datiLineeOrdini.get(j), 1).getValue();
                if (objPrezzoUnitario instanceof Long){
                  prezzoUnitario = ((Long) objPrezzoUnitario).doubleValue();
                }else{
                  if(objPrezzoUnitario instanceof Double){
                    prezzoUnitario = (Double) objPrezzoUnitario;
                  }
                }
                String descrizione = (String) SqlManager.getValueFromVectorParam(datiLineeOrdini.get(j), 2).getValue();

                String strSqlInsert="insert into DPRE (NGARA, CONTAF, DITTAO, PREOFF, IMPOFF," +
                		" NORVOC, CODVOC, UNIMIS, QUANTI, VOCE, NUMPAR)" +
                		" values (?,?,?,?,?,?,?,?,?,?,?)";

                this.sqlManager.update(strSqlInsert, new Object[]{numeroGara, contaf, codiceDitta, prezzoUnitario,
                    quantita*prezzoUnitario, norvoc, codvoc, unitaMisura, quantita, descrizione, numpar});



              }
            }
          } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }

      }

  } catch (SQLException e1) {
    // TODO Auto-generated catch block
    e1.printStackTrace();
  }




  //vado sulle linee ordine

  return 0;
  }


  /**
   * Funzione che aggiorna i dati di alice con i dati dell'ordine approvato
   *
   *
   * @param
   * @param
   * @param
   *
   * @return int -  esito dell'aggiornamento (<0 negativo, >=0 positivo)
   *
   * @throws GestoreException
   */




  public int updDaOrdineApprovato(HashMap datiOrdine, String flag_annullamento,
      String codiceGara, String numeroGara, String task )
      throws GestoreException {


    if (logger.isDebugEnabled()) logger.debug("updDaOrdineApprovato: inizio metodo");
    String tipoProcedura = "";
    String numeroRda = "";
    String numeroOrdine = "";
    int res = 0 ;

    if (datiOrdine != null) {
      Long idOrdine = (Long) datiOrdine.get("idOrdine");
      numeroOrdine = (String) datiOrdine.get("numeroOrdine");
      numeroOrdine = UtilityStringhe.convertiNullInStringaVuota(numeroOrdine);
      Double importoOrdinato = (Double) datiOrdine.get("importoOrdinato");
      String codiceFiscale = (String) datiOrdine.get("codiceFiscale");
      codiceFiscale = UtilityStringhe.convertiNullInStringaVuota(codiceFiscale);
      String partitaIVA = (String) datiOrdine.get("partitaIVA");
      partitaIVA = UtilityStringhe.convertiNullInStringaVuota(partitaIVA);
      String ragioneSociale = (String) datiOrdine.get("ragioneSociale");
      ragioneSociale = UtilityStringhe.convertiNullInStringaVuota(ragioneSociale);
      String descrizioneTestata = (String) datiOrdine.get("descrizioneTestata");
      String cig = (String) datiOrdine.get("cig");
      Date dataApprovazione = (Date) datiOrdine.get("dataApprovazione");
      Long tipologiaAffidamento = (Long) datiOrdine.get("tipologiaAffidamento");
      String flagPubblicabile = (String) datiOrdine.get("flagPubblicabile");
      flagPubblicabile = UtilityStringhe.convertiNullInStringaVuota(flagPubblicabile);

      String codimp = "";
      String nomimp = "";
      Long tipimp = new Long(3);
      boolean isFoundDitta = false;

      // controllo della esistenza della impresa
      // diamo per scontato (conferma da Danilo) che il cf appartiene a una ditta presente almeno nella view V_IMPR_SMAT
      if (!"".equals(codiceFiscale)) {
        String selectV_IMPR_SMAT = "select count(*) from V_IMPR_SMAT where cfimp = ? and id_sede is not null and id_sede!=0";
        Long numImpr;
          try {
            numImpr = (Long) sqlManager.getObject(selectV_IMPR_SMAT, new Object[] {codiceFiscale });
          } catch (SQLException e) {
            throw new GestoreException("Errore nella verifica dell'impresa con CF " + codiceFiscale, null, e);
          }
          DataColumnContainer dccV_IMPR_SMAT = null;
          if (!new Long(0).equals(numImpr)) {
            isFoundDitta = true;
            //ricerca solo per codice fiscale?
            selectV_IMPR_SMAT = "select * from V_IMPR_SMAT where cfimp = ? and id_sede is not null and id_sede!=0";
            dccV_IMPR_SMAT = new DataColumnContainer(sqlManager, "V_IMPR_SMAT", selectV_IMPR_SMAT, new Object[] {codiceFiscale});
            //funzione utilizzata anche dal gestore di ditg
            res = this.gestioneSMAT(dccV_IMPR_SMAT, true);
            if(res<0){
              logger.error("Errore durante la gestione della ditta " + nomimp + " in SMAT! ");
            }
            if (dccV_IMPR_SMAT.isColumn("V_IMPR_SMAT.CODIMP")) {
              codimp = dccV_IMPR_SMAT.getColumn("V_IMPR_SMAT.CODIMP").getValue().getStringValue();
              nomimp = dccV_IMPR_SMAT.getColumn("V_IMPR_SMAT.NOMIMP").getValue().getStringValue();
              nomimp = UtilityStringhe.convertiNullInStringaVuota(nomimp);
              if (!"".equals(nomimp) && nomimp.length() > 61) {
                nomimp = nomimp.substring(0, 60);
              }
            }
          }
      }

      try {
        //se la richiesta arriva dal task OrdiniApprovati occorre determinare le gare da aggiornare
        if ("Y".equals(task)) {
          String selectCodgar = "select codgar1, ngara, tipgarg, numero_rda, num_ordine from v_smat_rda_linee , gare, torn"
              + " where id_ordine = ? and nattog = numero_rda and codgar1 = codgar"
              + " and tattog = 4 and (gartel is null or gartel != '1') ";
          List GareDaAggiornare = sqlManager.getListVector(selectCodgar, new Object[] {idOrdine });
          if (GareDaAggiornare != null && GareDaAggiornare.size() > 0) {
            // La situazione corretta deve risultare una sola riga, in ogni caso prendo la prima
            codiceGara = SqlManager.getValueFromVectorParam(GareDaAggiornare.get(0), 0).getStringValue();
            codiceGara = UtilityStringhe.convertiNullInStringaVuota(codiceGara);
            numeroGara = SqlManager.getValueFromVectorParam(GareDaAggiornare.get(0), 1).getStringValue();
            //tipoProcedura = SqlManager.getValueFromVectorParam(GareDaAggiornare.get(0), 2).getStringValue();
            numeroGara = UtilityStringhe.convertiNullInStringaVuota(numeroGara);
            numeroRda = SqlManager.getValueFromVectorParam(GareDaAggiornare.get(0), 3).getStringValue();
            numeroOrdine = SqlManager.getValueFromVectorParam(GareDaAggiornare.get(0), 4).getStringValue();
          }
        }

        // verifica che si tratti di una procedura relazionata al profilo Affidamenti Diretti SMAT
/*
        String profiloAttivo = "PG_GARE_ADSMAT";
        String descTab = pgManager.getFiltroTipoGara(profiloAttivo);
        String[] arrayValoriProcedura = UtilityStringhe.deserializza(descTab, ',');
        String singleValProc = "";
*/
        String tipologiaAffidamentoStr = "";
        if (tipologiaAffidamento != null) {
          tipologiaAffidamentoStr = tipologiaAffidamento.toString();
        }
        String tipoIter = "";
/*
        boolean isFoundProcedura = false;

        if (arrayValoriProcedura != null && arrayValoriProcedura.length > 0) {
          for (int i = 0; i < arrayValoriProcedura.length; i++) {
            singleValProc = arrayValoriProcedura[i];
            if (singleValProc.equals(tipologiaAffidamentoStr)) {
              isFoundProcedura = true;
              break;
            }
          }
        }
 */
        boolean isFoundProcedura = true;
        //nei requisiti si dice di effettuare l'aggiornamento solo per le procedure relazionate a tale profilo
        if (isFoundProcedura) {

          // verifico anche il tipoIter

          boolean isFoundIter = false;
          List listaValoriA1z04 = sqlManager.getListVector("select tab2tip,tab2d2 from tab2"
              + " where tab2cod='A1z04' order by tab2tip", new Object[] {});
          if (listaValoriA1z04 != null && listaValoriA1z04.size() > 0) {
            for (int i = 0; i < listaValoriA1z04.size(); i++) {
              String tab2tip = SqlManager.getValueFromVectorParam(listaValoriA1z04.get(i), 0).getStringValue();
              String desc = SqlManager.getValueFromVectorParam(listaValoriA1z04.get(i), 1).getStringValue();
              String[] arrayValoriIter = UtilityStringhe.deserializza(desc, ',');
              String singleValIter = "";

              if (arrayValoriIter != null && arrayValoriIter.length > 0) {
                for (int k = 0; k < arrayValoriIter.length; k++) {
                  singleValIter = arrayValoriIter[k];
                  if (singleValIter.equals(tipologiaAffidamentoStr)) {
                    isFoundIter = true;
                    tipoIter = tab2tip;
                    break;
                  }
                }
              }
            }
          }
          if (isFoundDitta && isFoundIter && !"".equals(codiceGara) && !"".equals(numeroGara)) {
            //Verifico che si tratti di una gara a lotto unico (revisione ottobre 2014)
            if ('$' == codiceGara.charAt(0)) {
              // Aggiorno alcuni dati della gara/lotti
              if ("N".equals(flag_annullamento)) {
                this.sqlManager.update(
                    "update GARE set NMAXIMO = ?, NATTOA = ?, IAGGIU= ?, CODCIG = ?, DATTOA = ?,DITTA = ?, NOMIMA =?, TIPGARG = ? "
                        + "where codgar1 = ? and ngara = ? ", new Object[] {numeroOrdine, numeroOrdine, importoOrdinato, cig,
                        dataApprovazione, codimp, nomimp, tipologiaAffidamento, codiceGara, numeroGara });
                this.sqlManager.update("update TORN set TIPGAR = ?, ITERGA= ? " + "where codgar = ? ", new Object[] {tipologiaAffidamento,
                    tipoIter, codiceGara });
                // occorre gestire l'inserimento in ditg che negli altri casi(fasi di gara) viene fatto dal gestore di ditg
                HashMap datiDitta = new HashMap();
                datiDitta.put("codiceFiscale", codiceFiscale);
                datiDitta.put("codiceDitta", codimp);
                datiDitta.put("nomimp", nomimp);
                if(!"".equals(codimp)){
                  this.insDitteLotto( datiDitta, codiceGara, numeroGara );
                }
                // solo nel caso di corrispondenza... aggiorno l'offerta
                if ("Y".equals(task)) {
                  this.insOfferteLotto(codiceGara, numeroGara, idOrdine, codimp);
                }

              } else if ("Y".equals(flag_annullamento)) {
                this.sqlManager.update("update GARE set CATIGA = ? , ESINEG = ? " + "where codgar1 = ? and ngara = ? ",
                    new Object[] {"6", "1", codiceGara, numeroGara });
              }
            }
            if ("Y".equals(flagPubblicabile)) {
              res = this.insPubblicazioneLotto(codiceGara, numeroGara);
              if(res == 2){
                return res;
              }
            }
          }else{
            return 1;
          }
        }else{
          return 1;
        }

      } catch (SQLException e) {
        throw new GestoreException("Errore nell' aggiornamento dei dati della gara " + numeroGara, null, e);
      }
    }
    if (logger.isDebugEnabled()) logger.debug("updDaOrdineApprovato: fine metodo");

    return 0;

  }


  public int updOrdineElaborato(Long idOrdine, Long idEvento, int flag_elaborazione )
    throws GestoreException {
    if (logger.isDebugEnabled()) logger.debug("updOrdineElaborato: inizio metodo");
    String processed_flag = "Y";
    if(flag_elaborazione == 2){
      processed_flag = "M";
    }

    Date dataOdierna = UtilityDate.getDataOdiernaAsDate();
    // Aggiorno l'informazione di eleaborazione avvenuta
    // Eventualmente occorre inserire un trg di tipo instead of se si vuole aggiornare la view...
    try {
        this.sqlManager.update("update V_SMAT_ORDINI_PUBESITI set PROCESSED_FLAG = ? , PROCESSED_DATE = ? "
            + " where id_ordine = ? and event_id = ? ", new Object[] {processed_flag, dataOdierna, idOrdine, idEvento });
    } catch (SQLException e) {
      throw new GestoreException("Errore nell' aggiornamento dello stato elaborazione dell'ordine " , null, e);
    }

    if (logger.isDebugEnabled()) logger.debug("updOrdineElaborato: fine metodo");

    return 0;

  }

  public int updRdaAnnullabile(String entita,String numeroRda, String chiaveAttuale)
    throws GestoreException {
    if (logger.isDebugEnabled()) logger.debug("updRdaAnnullabile: inizio metodo");
    try {
      if(!"".equals(numeroRda)){
        if("TORN".equals(entita)){
          this.sqlManager.update("update torn set nattot  = null where tattot = 4 and nattot = ? and codgar <> ?" , new Object[] {numeroRda,chiaveAttuale});
          this.sqlManager.update("update gare set nattog  = null where tattog = 4 and nattog = ? and codgar1 <> ?" , new Object[] {numeroRda,chiaveAttuale});
          this.sqlManager.update("update garatt set nattot  = null where tattot = 4 and nattot = ?" , new Object[] {numeroRda});
        }
        if("GARE".equals(entita)){
          this.sqlManager.update("update torn set nattot  = null where tattot = 4 and nattot = ?" , new Object[] {numeroRda});
          this.sqlManager.update("update gare set nattog  = null where tattog = 4 and nattog = ? and ngara <> ?" , new Object[] {numeroRda,chiaveAttuale});
          this.sqlManager.update("update garatt set nattot  = null where tattot = 4 and nattot = ?" , new Object[] {numeroRda});
        }
        if("GARATT".equals(entita)){
          this.sqlManager.update("update torn set nattot  = null where tattot = 4 and nattot = ?" , new Object[] {numeroRda});
          this.sqlManager.update("update gare set nattog  = null where tattog = 4 and nattog = ?" , new Object[] {numeroRda});
          this.sqlManager.update("update garatt set nattot  = null where tattot = 4 and nattot = ? and codgar <> ?" , new Object[] {numeroRda,chiaveAttuale});
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nell' aggiornamento di una RdA annullabile  " , null, e);
    }

    if (logger.isDebugEnabled()) logger.debug("updRdaAnnullabile: fine metodo");

    return 0;

}


  /**
   * Per le imprese SMAT viene popolata la IMPR ed eventualmente la IMPIND
   *
   * @param dataColumnContainer
   * @param aggiornaDittao
   *
   * @return    1 ok
   *            <0 errore
   * @throws GestoreException
   */
  public int gestioneSMAT(DataColumnContainer dataColumnContainer, boolean aggiornaDittao) throws GestoreException{
    int ret=1;
    String select=null;
    GeneManager gene = this.getGeneManager();

    //Se si tratta di un impresa proveniente da OA, la inserisce prima in IMPR
    //Long is_impresa_oa = dataColumnContainer.getLong("IS_IMPRESA_OA");
    Long is_impresa_oa = null;
    if(dataColumnContainer.isColumn("IS_IMPRESA_OA"))
      is_impresa_oa = dataColumnContainer.getLong("IS_IMPRESA_OA");
    else
      is_impresa_oa = dataColumnContainer.getLong("V_IMPR_SMAT.IS_IMPRESA_OA");
    if(is_impresa_oa != null && is_impresa_oa.longValue()>0){

      String id_fornitore = null;
      if(dataColumnContainer.isColumn("ID_FORNITORE"))
        id_fornitore = dataColumnContainer.getString("ID_FORNITORE");
      else
        id_fornitore = dataColumnContainer.getString("V_IMPR_SMAT.ID_FORNITORE");

      Long id_sede = null;
      if(dataColumnContainer.isColumn("ID_SEDE"))
        id_sede = dataColumnContainer.getLong("ID_SEDE");
      else
        id_sede = dataColumnContainer.getLong("V_IMPR_SMAT.ID_SEDE");

      if(id_sede!=null && id_sede.longValue()==0){
        ret=-1;
      }else{
        //Si controlla se il codice smat è già presente in impr
        String codimp = id_fornitore;
        //if (idFornitore.length()>10)
        select="select count(codimp) from impr where codimp=?";
        try {
          Long count = (Long) sqlManager.getObject(select, new Object[]{codimp});
          if(count!= null && count.longValue()>0){
            codimp = gene.calcolaCodificaAutomatica("IMPR","CODIMP");
            if(aggiornaDittao){
              if(dataColumnContainer.isColumn("DITG.DITTAO"))
                dataColumnContainer.setValue("DITG.DITTAO", codimp);
              if(dataColumnContainer.isColumn("RAGIMP.CODDIC"))
                dataColumnContainer.setValue("RAGIMP.CODDIC", codimp);
            }
          }

          select="select NOME_FORNITORE, PARTITA_IVA,CODICE_FISCALE,INDIRIZZO1," +
            "CITTA,CAP,PROVINCIA,STATO,T_PREFISSO,T_NUMERO,F_PREFISSO,F_NUMERO,"+
            "INDIRIZZO2 ,INDIRIZZO3 from SMAT_VENDORS_ALL where CODICE_FORNITORE = ? "+
            "and ID_SEDE = ?";
          Vector datiSMAT = sqlManager.getVector(select, new Object[]{id_fornitore,id_sede});
          if(datiSMAT!=null && datiSMAT.size()>0){
            String nome_fornitore= SqlManager.getValueFromVectorParam(datiSMAT, 0).getStringValue();
            String nomimp=null;
            if (nome_fornitore!=null && nome_fornitore.length()>61)
              nomimp= nome_fornitore.substring(0, 61);
            else
              nomimp = nome_fornitore;

            String partita_iva= SqlManager.getValueFromVectorParam(datiSMAT, 1).getStringValue();
            String codice_fiscale= SqlManager.getValueFromVectorParam(datiSMAT, 2).getStringValue();
            String indirizzo1= SqlManager.getValueFromVectorParam(datiSMAT, 3).getStringValue();
            if(indirizzo1!= null && indirizzo1.length()>60)
              indirizzo1 = indirizzo1.substring(0, 60);
            String citta= SqlManager.getValueFromVectorParam(datiSMAT, 4).getStringValue();
            String cap= SqlManager.getValueFromVectorParam(datiSMAT, 5).getStringValue();
            if (cap!=null && cap.length()>5)
              cap = cap.substring(0, 5);
            String provincia= SqlManager.getValueFromVectorParam(datiSMAT, 6).getStringValue();
            if (provincia!=null && provincia.length()>2)
              provincia = provincia.substring(0, 2);
            String stato= SqlManager.getValueFromVectorParam(datiSMAT, 7).getStringValue();
            Long nazimp=null;
            if (stato!=null && !"".equals(stato)){
              select="select tab1tip from tab1 where tab1cod='Ag010' and upper(tab1desc) = ?";
              nazimp = (Long)sqlManager.getObject(select, new Object[]{stato.toUpperCase()});
            }

            String t_prefisso= SqlManager.getValueFromVectorParam(datiSMAT, 8).getStringValue();
            String t_numero= SqlManager.getValueFromVectorParam(datiSMAT, 9).getStringValue();
            String tel = this.componiStringhe(t_prefisso, t_numero, null);

            String f_prefisso= SqlManager.getValueFromVectorParam(datiSMAT, 10).getStringValue();
            String f_numero= SqlManager.getValueFromVectorParam(datiSMAT, 11).getStringValue();
            String fax = this.componiStringhe(f_prefisso, f_numero, null);
            if (fax.length()>20)
              fax= fax.substring(0, 20);

            String indirizzo2= SqlManager.getValueFromVectorParam(datiSMAT, 12).getStringValue();
            String indirizzo3= SqlManager.getValueFromVectorParam(datiSMAT, 13).getStringValue();
            String note = this.componiStringhe(indirizzo2, indirizzo3, " - ");

            String codiceIstat=null;
            if (provincia!= null && citta!= null){
              codiceIstat = pgManager.getCodiceISTAT(citta.toUpperCase(), provincia.toUpperCase());
            }

            select="insert into impr(CODIMP,NOMEST,NOMIMP,TIPIMP,PIVIMP,CFIMP,INDIMP,LOCIMP,CAPIMP,PROIMP,CODCIT," +
              "NAZIMP,TELIMP,FAXIMP,ANNOTI) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            sqlManager.update(select, new Object[]{codimp,nome_fornitore,nomimp,new Long(1),partita_iva,
                codice_fiscale,indirizzo1,citta,cap,provincia,codiceIstat,nazimp,tel,fax,note});

            //Se vi sono occorrenze in SMAT_VENDORS_ALL per il codice fornitore selezionato con ID_SEDE diversi
            //si devono inserire le occorrenze in IMPIND
            select="select INDIRIZZO1,CITTA,CAP,PROVINCIA,T_PREFISSO,T_NUMERO,F_PREFISSO,F_NUMERO "+
              " from SMAT_VENDORS_ALL where ID_FORNITORE = ? and ID_SEDE <> ? and INACTIVE_DATE is NULL";

            List datiListSMAT = sqlManager.getListVector(select, new Object[]{id_fornitore,id_sede});
            if (datiListSMAT != null) {
              Long indcon = new Long(0);
              for (int i = 0; i < datiListSMAT.size(); i++) {
                Vector row = (Vector) datiListSMAT.get(i);
                indirizzo1 = row.get(0).toString();
                if(indirizzo1!= null && indirizzo1.length()>60)
                  indirizzo1 = indirizzo1.substring(0, 60);
                citta = row.get(1).toString();
                cap = row.get(2).toString();
                if (cap!=null && cap.length()>5)
                  cap = cap.substring(0, 5);

                provincia = row.get(3).toString();
                if (provincia!=null && provincia.length()>2)
                  provincia = provincia.substring(0, 2);
                if (provincia!= null && citta!= null)
                  codiceIstat = pgManager.getCodiceISTAT(citta.toUpperCase(), provincia.toUpperCase());

                t_prefisso = row.get(4).toString();
                t_numero= row.get(5).toString();
                tel = this.componiStringhe(t_prefisso, t_numero, null);

                f_prefisso = row.get(6).toString();
                f_numero = row.get(7).toString();
                fax = this.componiStringhe(f_prefisso, f_numero, null);
                if (fax.length()>20)
                  fax= fax.substring(0, 20);

                indcon = new Long (indcon.longValue() + 1);
                select="insert into impind(CODIMP5,INDCON,INDIND,INDLOC,INDCAP,INDPRO,CODCIT,INDTEL,INDFAX) "+
                  "values(?,?,?,?,?,?,?,?,?)";
                sqlManager.update(select, new Object[]{codimp,indcon,indirizzo1,citta,cap,provincia,codiceIstat,tel,fax});
              }
            }
          }
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore nell'inserimento dell'impresa", null, e);
        }
        //Riassegnazione del codimp ,in quanto puo' essere ricalcolato
        if(dataColumnContainer.isColumn("V_IMPR_SMAT.CODIMP")){
          dataColumnContainer.setValue("V_IMPR_SMAT.CODIMP", codimp);
        }
      }

    }

    return ret;
  }
  /**
   * Viene composta la stringa str1 + str2 se unione è null,
   * altrimenti str1 + unione + str2
   *
   * @param str1
   * @param str2
   * @param unione
   * @return String
   */
  public String componiStringhe(String str1, String str2,String unione){
    String ret="";
    if (str1!=null && !"".equals(str1))
      ret = str1.trim();
    if(unione!= null && !"".equals(unione) && str1!=null && !"".equals(str1) && str2!=null && !"".equals(str2))
      ret+=unione;
    if (str2!=null && !"".equals(str2))
      ret += str2.trim();

    return ret;
  }

  /**
   * Ordine imputato manualmente
   *
   *
   * @return valore del campo TAB2D2
   *
   * @throws GestoreException
   *
   *
   */
  public int updOrdineDaImputazioneManuale(String numeroOrdine, String codiceGara, String numeroGara) throws GestoreException {

    Long idOrdine= null;
    Double importoOrdinato = null; //verificare che sia effettivamente un double
    String codiceFiscale = null;
    String partitaIVA = null;
    String ragioneSociale = null;
    String descrizioneTestata = null;
    String cig = null;
    Date dataApprovazione = null;
    Long tipologiaAffidamento = null;
    String flagPubblicabile = "N";

    String selectOrdini = "select id_ordine, ordine, importo_ordinato, cfis, piva, rag_soc, descrizione_testata," +
    " cig, data_approvazione, tipo_affidam, stato, lotto_pubblicabile" +
    "  from v_smat_ordini" +
    "  where ordine = ? and stato = 'Approvato' ";

    List listaOrdiniDaElaborare = null;
    try {
      listaOrdiniDaElaborare = sqlManager.getListVector(selectOrdini,
            new Object[] { numeroOrdine });

    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della view V_SMAT_ORDINI ", null, e);
    }

    if (listaOrdiniDaElaborare != null && listaOrdiniDaElaborare.size() > 0) {
        for (int i = 0; i < listaOrdiniDaElaborare.size(); i++) {
          idOrdine = SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 0).longValue();
          importoOrdinato = SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 2).doubleValue();
          codiceFiscale = SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 3).getStringValue();
          partitaIVA = SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 4).getStringValue();
          ragioneSociale = SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 5).getStringValue();
          descrizioneTestata = SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 6).getStringValue();
          cig = SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 7).getStringValue();
          dataApprovazione = (Date) SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 8).getValue();
          tipologiaAffidamento = (Long) SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 9).getValue();
          flagPubblicabile = SqlManager.getValueFromVectorParam(listaOrdiniDaElaborare.get(i), 11).getStringValue();
          flagPubblicabile = UtilityStringhe.convertiNullInStringaVuota(flagPubblicabile);

          HashMap datiOrdine = new HashMap();
          datiOrdine.put("idOrdine", idOrdine);
          datiOrdine.put("numeroOrdine", numeroOrdine);
          datiOrdine.put("importoOrdinato", importoOrdinato);
          datiOrdine.put("codiceFiscale", codiceFiscale);
          datiOrdine.put("partitaIVA", partitaIVA);
          datiOrdine.put("ragioneSociale", ragioneSociale);
          datiOrdine.put("descrizioneTestata", descrizioneTestata);
          datiOrdine.put("cig", cig);
          datiOrdine.put("dataApprovazione", dataApprovazione);
          datiOrdine.put("tipologiaAffidamento", tipologiaAffidamento);
          datiOrdine.put("flagPubblicabile", flagPubblicabile);

          try {
            this.updDaOrdineApprovato(datiOrdine, "N", codiceGara, numeroGara, "N");
          } catch (GestoreException e) {
            throw new GestoreException("Errore nella lettura dell'ordine imputato manualmente!", null, e);
          }
        }
    }

      return 0;
  }

  public int insPubblicazioneLotto(String codiceGara, String codiceLotto)
    throws GestoreException {

    String selectDatiTORN = "select cenint,codrup,imptor,iterga,dteoff,dtepar,dinvit,oteoff,desoff,oesoff from torn where codgar = ?";
    Vector datiTORN;
      try {
      datiTORN = sqlManager.getVector(selectDatiTORN, new Object[] {codiceGara });
      String codicePubblicazione = "";
      String valore = "";
      String controlloSuperato = "";
      Timestamp dteoff;
      Long iterga;
      Timestamp dtepar;
      Timestamp dinvit;
      String oteoff;
      if (datiTORN != null) {
        // Controllo Stazione appaltante
        valore = ((JdbcParametro) datiTORN.get(0)).getStringValue();
        if (valore == null || "".equals(valore)) {
          controlloSuperato = "NO";
        }
        // Controllo RUP
        valore = ((JdbcParametro) datiTORN.get(1)).getStringValue();
        if (valore == null || "".equals(valore)) {
          controlloSuperato = "NO";
        }

        Double imptor = ((JdbcParametro) datiTORN.get(2)).doubleValue();
        iterga = ((JdbcParametro) datiTORN.get(3)).longValue();
        dteoff = ((JdbcParametro) datiTORN.get(4)).dataValue();
        dtepar = ((JdbcParametro) datiTORN.get(5)).dataValue();
        dinvit = ((JdbcParametro) datiTORN.get(6)).dataValue();
        oteoff = ((JdbcParametro) datiTORN.get(7)).stringValue();
        Timestamp desoff = ((JdbcParametro) datiTORN.get(8)).dataValue();
        String oesoff = ((JdbcParametro) datiTORN.get(9)).stringValue();
      }

      String selectImpBaseAsta = "select IMPAPP from gare where ngara = ?";
      Double importo = (Double) sqlManager.getObject(selectImpBaseAsta, new Object[] {codiceLotto });
      // controllo sull'importo
      if (importo == null) {
        controlloSuperato = "NO";
      }
      // commento perche' il controllo non risulta bloccante
      /*
       * String ditta = (String)sqlManager.getObject("select ditta from gare where ngara=?", new Object[]{codiceLotto}); if(ditta==null ||
       * "".equals(ditta)){ //controlloSuperato = "NO"; }
       */
      if ('$' == codiceGara.charAt(0)) {
        codicePubblicazione = codiceLotto;
      } else {
        codicePubblicazione = codiceGara;
      }
      if (!"NO".equals(controlloSuperato)) {
        // eventualmente verifico che la riga non esista già
        String selectPUBG = "select coalesce(max(npubg),0) from pubg where ngara = ?";
        Long nPubg = (Long) sqlManager.getObject(selectPUBG, new Object[] {codicePubblicazione });
        int np = 0;
        if (new Long(0).equals(nPubg)) {
          np = nPubg.intValue() + 1;
          Date dataOdierna = UtilityDate.getDataOdiernaAsDate();
          this.sqlManager.update("insert into PUBG (ngara, npubg, tippubg, dinpubg )" + " values(?,?,?,?)", new Object[] {
              codicePubblicazione, new Long(np), new Long(12), dataOdierna });
        }
      } else {
        return 2; // non sono stati superati i controlli
      }
      } catch (SQLException e) {
        throw new GestoreException("Errore nella pubblicazione dell'esito per il lotto " + codiceLotto, null, e);
      }
      return 0;

  }



  private int insDitteLotto(HashMap datiDitta, String codiceGara, String numeroGara )
    throws GestoreException, SQLException {


    if (datiDitta != null) {
      String codiceFiscale = (String) datiDitta.get("codiceFiscale");
      codiceFiscale = UtilityStringhe.convertiNullInStringaVuota(codiceFiscale);
      String codiceDitta = (String) datiDitta.get("codiceDitta");
      codiceDitta = UtilityStringhe.convertiNullInStringaVuota(codiceDitta);
      String nomimp = (String) datiDitta.get("nomimp");
      nomimp = UtilityStringhe.convertiNullInStringaVuota(nomimp);

      String codiceTornata = null;
      Long nProgressivoDITG, nProgressivoEDIT = null;
      Double importoAppalto = null;
      Double importoAggiudicazioneAppalto = null;
      Double ribassoAggiudicazioneAppalto = null;
      boolean esisteOccorrenzaInEDIT = false;
      boolean esisteOccorrenzaInDITG = false;
      boolean isProceduraAggiudicazioneAperta = false;
      boolean modalitaNPROGGsuLotti = pgManager.isModalitaNPROGGsuLotto();

        List datiGara;
          try {
            datiGara = sqlManager.getListVector("select CODGAR1, TIPGARG, IMPAPP, IAGGIU, RIBAGG" +
            		" from GARE where NGARA = ? ", new Object[] { numeroGara });
            Vector dati = (Vector) datiGara.get(0);


            codiceTornata = ((JdbcParametro) dati.get(0)).getStringValue();
            if (((JdbcParametro) dati.get(1)).getValue() != null)
            isProceduraAggiudicazioneAperta = ((Long) ((JdbcParametro) dati.get(1)).getValue()).intValue() == 1;
            importoAppalto = (Double) ((JdbcParametro) dati.get(2)).getValue();
            importoAggiudicazioneAppalto = (Double) ((JdbcParametro) dati.get(3)).getValue();
            ribassoAggiudicazioneAppalto = (Double) ((JdbcParametro) dati.get(4)).getValue();
            // Determino il valore del progressivo dal campo EDIT.NPROGT per la
            // tornata in analisi e la ditta che si vuole inserire, se presente.
            nProgressivoEDIT = (Long) sqlManager.getObject("select NPROGT from EDIT " + "where CODGAR4 = ? "
                      + "and CODIME = ? ", new Object[] { codiceTornata, codiceDitta });
            if (nProgressivoEDIT != null && nProgressivoEDIT.intValue() > 0)
            esisteOccorrenzaInEDIT = true;
            else {
            // Se non esiste alcuna occorrenza nella EDIT relativa alla gara
            // in analisi, allora determino valore massimo del campo EDIT.NPROGT
            nProgressivoEDIT = (Long) sqlManager.getObject("select max(NPROGT) from EDIT where CODGAR4 = ? ",
                              new Object[] { codiceTornata });
            // Se la EDIT non ha occorrenze per la gara in analisi,
            // inizializzo la variabile nProgressivoEDIT a 0
            if (nProgressivoEDIT == null)
            nProgressivoEDIT = new Long(0);
            }
            if (modalitaNPROGGsuLotti)
            nProgressivoDITG = (Long) sqlManager.getObject( "select max(NPROGG) from DITG " + "where CODGAR5 = ? "
                                      + "and NGARA5 = ? ", new Object[] { codiceTornata, numeroGara });
            else
            nProgressivoDITG = (Long) sqlManager.getObject("select max(NPROGG) from DITG " + "where CODGAR5 = ? ",
                              new Object[] { codiceTornata });
            if (nProgressivoDITG == null)
            nProgressivoDITG = new Long(0);


          } catch (SQLException e) {
            logger.error("Errore nel determinare il progressivo "
                + "della ditta (codice ditta: " + codiceDitta   + ") nella gara " +
                numeroGara);
            throw new GestoreException("Errore nel determinare il progressivo "
                + "della ditta (codice ditta: " + codiceDitta   + ") nella gara " +
                numeroGara, null, e);

          }

          Long numProgDITG = null;
          if (modalitaNPROGGsuLotti) {
              numProgDITG = new Long(nProgressivoDITG.intValue() + 1);
          } else {
              if (esisteOccorrenzaInEDIT) {
                  // Se esiste l'occorrenza nella EDIT, allora l'insert nella DITG
                  // lo si effettua con DITG.NPROGG = EDIT.NPROGT
                  numProgDITG = nProgressivoEDIT;
              } else {
                  // Tra nProgressivoDITG e nProgressivoEDIT si va a scegliere
                  // come valore del campo DITG.NPROGG il max fra i due valori
                  if (nProgressivoDITG.compareTo(nProgressivoEDIT) > 0)
                      numProgDITG = new Long(nProgressivoDITG.longValue() + 1);
                  else
                      numProgDITG = new Long(nProgressivoEDIT.longValue() + 1);
              }
          }

          Long nProgressivo=null;
          if (!esisteOccorrenzaInEDIT) {
              // Insert dell'occorrenza nella tabella EDIT
              if (modalitaNPROGGsuLotti)
                nProgressivo  =new Long(nProgressivoEDIT.longValue() + 1);
              else
                nProgressivo = numProgDITG;

              try {
                String insertEdit = "insert into EDIT (CODGAR4,CODIME,NOMIME,DOCOK,DATOK,DITINV,NPROGT) ";
                insertEdit += " values(?,?,?,?,?,?,?)";
                sqlManager.update(insertEdit,new Object[]{codiceTornata,codiceDitta,nomimp,"1","1","1",nProgressivo});
              }catch (SQLException s) {
                logger.error("Errore nell'inserimento in EDIT ");
                if (logger.isDebugEnabled())
                  logger.debug(s.getMessage());
                throw new GestoreException("Errore nell'inserimento in EDIT ", null, s);
              }
          }

          try {
            //
            String selectDitg="select count(ngara5) from ditg where ngara5=? and codgar5=? and dittao=?";
            Long count = (Long) sqlManager.getObject(
                    selectDitg,new Object[] { numeroGara,codiceTornata,codiceDitta });
            if (count.longValue() == 0) {
                selectDitg="insert into DITG (NGARA5,DITTAO,CODGAR5,NOMIMO,NPROGG,CATIMOK,INVGAR,NUMORDPL,INVOFF," +
                		"IMPAPPD,IMPOFF,RIBAUO) ";
                selectDitg+="values (?,?,?,?,?,?,?,?,?,?,?,?)";
                String invoff=null;
                //In affidamenti diretti invio anche l'offerta indipendentemente dal fatto che si tratti
                // di una procedura aperta(inserimento iniziale in DITG)
                  invoff="1";

                sqlManager.update(selectDitg,new Object[]{numeroGara,codiceDitta,codiceTornata,nomimp,
                    numProgDITG,"1","1",numProgDITG,invoff,importoAppalto,
                    importoAggiudicazioneAppalto,ribassoAggiudicazioneAppalto});
            } else {
              esisteOccorrenzaInDITG = true;
              //eventualmente si opera un aggiornamento,ma in questo caso
              // diamo per buoni i dati preesistenti
              logger.error("La ditta selezionata risulta già inserita in gara");
            }
          } catch (SQLException e) {
            logger.error("Errore nell'inserimento in DITG");
            if (logger.isDebugEnabled())
              logger.debug(e.getMessage());
            throw e;

          }

    }

    return 0;

  }


  public int updAnagraficaElaborata(HashMap datiAnagrafica, Long idSupplierInterface )
    throws GestoreException {
      if (logger.isDebugEnabled()) logger.debug("updAnagraficaElaborata: inizio metodo");
        Date dataOdierna = UtilityDate.getDataOdiernaAsDate();
        // Aggiorno l'informazione di eleaborazione avvenuta

        if (datiAnagrafica != null) {
          String codimp = (String) datiAnagrafica.get("codimp");
          String ebs_supplier_num = (String) datiAnagrafica.get("ebs_supplier_num");
          try {
            String codimpDestinazione = ebs_supplier_num;
            String codimpOrigine = codimp;
            this.updCodiceAnagraficaElaborata("IMPR", codimpDestinazione, codimpOrigine);

            this.sqlManager.update("update SMAT_AP210_SUPPLIERS_INTERFACE set alice_processed_status = ? , alice_processed_date = ? "
                + " where supplier_interface_id = ? ", new Object[] {"PROCESSED", dataOdierna, idSupplierInterface });
          } catch (SQLException e) {
                 throw new GestoreException("Errore nell' aggiornamento dello stato elaborazione dell'anagrafica " , null, e);
          }

        }



      if (logger.isDebugEnabled()) logger.debug("updAnagraficaElaborata: fine metodo");

      return 0;
  }


  /**
   * Funzione che esegue l'aggiornamento delle intestazioni nel Database
   *
   * @param entita
   *        Entita modificata
   * @param intestazione
   *        Nuova intestazione
   * @param chiavi
   *        Chiavi dell'entità
   */
  private void updCodiceAnagraficaElaborata(String entita, String codDest, String codOrig)
      throws GestoreException {
    String updateTables[] = new String[] {};
    Object params[] = new Object[] {};
    if ("IMPR".equalsIgnoreCase(entita)) {
      params = new Object[] { codDest, codOrig };
      // Aggiornamento delle imprese
      updateTables = new String[] { "ditg",
          "update ditg set dittao = ? where dittao = ?", "ditgeventi",
          "update ditgeventi set dittao = ? where dittao = ?", "edit",
          "update edit set codime = ? where codime = ?", "gare",
          "update gare set ditta = ? where ditta = ?", "gare",
          "update gare set dittap = ? where dittap = ?", "appa",
          "update appa set cimpag = ? where cimpag = ?", "appa",
          "update appa set ncodim = ? where ncodim = ?", "bglpag",
          "update bglpag set cimpfat = ? where cimpfat = ?", "ordi",
          "update ordi set ditimp = ? where ditimp = ?", "suba",
          "update suba set codsub = ? where codsub = ?", "ragimp",
          "update perp set codimp = ? where codimp = ?", "itcsmat",
          "update itcsmat set codimp = ? where codimp = ?", "impazi",
          "update impazi set codimp4 = ? where codimp4 = ?" ,"impleg",
          "update impleg set codimp2 = ? where codimp2 = ?" ,"impdte",
          "update impdte set codimp3 = ? where codimp3 = ?" ,"impind",
          "update impind set codimp5 = ? where codimp5 = ?" ,"impope",
          "update impope set codimp = ? where codimp = ?" ,"impcase",
          "update impcase set codimp = ? where codimp = ?" ,"impdurc",
          "update impdurc set codimp = ? where codimp = ?" ,"impanno",
          "update impanno set codimp = ? where codimp = ?" ,"ragimp",
          "update ragimp set codime9 = ? where codime9 = ?" ,"ragimp",
          "update ragimp set coddic = ? where coddic = ?" ,"antimaf",
          "update antimaf set codimp = ? where codimp = ?" ,"schcon",
          "update schcon set codimp = ? where codimp = ?" ,"cate",
          "update cate set codimp1 = ? where codimp1 = ?" ,"ragdet",
          "update ragdet set codimp = ? where codimp = ?" ,"ragdet",
          "update ragdet set coddic = ? where coddic = ?" ,"usrsys",
          "update usrsys set codimp = ? where codimp = ?" ,"operap",
          "update operap set codimp = ? where codimp = ?" ,"g2durc",
          "update g2durc set codimp = ? where codimp = ?" ,"scheanag",
          "update scheanag set codimp = ? where codimp = ?" ,"ditgstati",
          "update ditgstati set dittao = ? where dittao = ?" ,"ditgammis",
          "update ditgammis set dittao = ? where dittao = ?" ,"ditgavval",
          "update ditgavval set dittao = ? where dittao = ?" ,"imprdocg",
          "update imprdocg set codimp = ? where codimp = ?","iscrizcat",
          "update iscrizcat set codimp = ? where codimp = ?","ordintmax",
          "update ordintmax set impresa = ?  where impresa = ?" ,"garecont",
          "update garecont set codimp = ? where codimp = ?","iscrizclassi",
          "update iscrizclassi set codimp = ? where codimp = ?","iscrizuff",
          "update iscrizuff set codimp = ? where codimp = ?","g_impcol",
          "update g_impcol set codimp = ? where codimp = ?","impantimafia",
          "update impantimafia set codimp = ? where codimp = ? ","c0oggass",
          "update c0oggass set c0akey1 = ? where c0akey1 = ? and c0aent='IMPR'","w_puser",
          "update w_puser set userkey1 = ? where userkey1  = ?","g_noteavvisi",
          "update g_noteavvisi set notekey1 = ? where notekey1 = ? and noteent='IMPR'","w_invcomdes",
          "update w_invcomdes set descodsog = ? where descodsog = ? and descodent='IMPR'","garacquisiz",
          "update garacquisiz set codimp = ? where codimp = ?","dpun",
          "update dpun set dittao = ? where dittao = ?","g1crival",
          "update g1crival set dittao = ? where dittao = ?","dpre",
          "update dpre set dittao = ? where dittao = ?","garvarpre",
          "update garvarpre set dittao = ? where dittao = ?","aerilpre",
          "update aerilpre set dittao = ? where dittao = ?","garilanci",
          "update garilanci set dittao = ? where dittao = ?","ditgaq",
          "update ditgaq set dittao = ? where dittao = ?","impr",
          "update impr set codimp = ? where codimp = ?"};
    }
    for (int i = 0; (i + 1) < updateTables.length; i += 2) {
      // Eseguo l'update solo se esiste la tabella
      if (this.sqlManager.isTable(updateTables[i])) {
        // Eseguo l'update
        try {
          this.sqlManager.update(updateTables[i + 1], params);
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore durante l'aggiornamento del codice di "
                  + entita
                  + " per la tabella: "
                  + updateTables[i], "updCodiceAnagraficaElaborata" + entita, e);
        }
      }
    }
  }

}