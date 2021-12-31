/*
 * Created on 17/dic/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per inserire le
 * pubblicazioni predefinite per il bando e per l'esito
 *
 * @author Stefano.Sabbadin
 */
public class GestoreInsertPubblicazioniPredefinite extends
    AbstractGestoreEntita {

  public GestoreInsertPubblicazioniPredefinite() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
  }

  @Override
  public String getEntita() {
    return "TORN";
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    // lettura dei parametri di input
    String codgar = datiForm.getString("CODGAR");
    String ngara = datiForm.getString("NGARA");
    String genere = datiForm.getString("GENERE");
    Long bando = datiForm.getLong("BANDO");
    Long sorgentePerPubblicazioneEsito = datiForm.getLong("SRC_ESITO");

    if (bando.longValue() == 0
        && sorgentePerPubblicazioneEsito.longValue() == 2) {
      // nel caso si parta dalla pubblicazione esito, e si selezioni nella popup
      // la pubblicazione del bando di gara, si devono utilizzare le occorrenze
      // prese nel bando di gara
      this.insertElencoPubblicazioniDaBando(status, codgar, ngara);

    } else {
      // in tutti gli altri casi (pubblicazione esito partendo da esiti
      // predefiniti, e pubblicazioni bando), si prendono i dati predefiniti

      // estrae l'elenco di tipi pubblicazione
      List listaTabellati = null;
      if("1".equals(genere) || "3".equals(genere)){
        listaTabellati = this.getListaTipiPubblicazione(codgar, null, bando);
      }else{
        listaTabellati = this.getListaTipiPubblicazione(codgar, ngara, bando);
      }


      // esegue gli inserimenti
      this.insertElencoPubblicazioniPredefinite(status, codgar, ngara, bando,
          listaTabellati);
    }

    // setta l'operazione a completata, in modo da scatenare il reload della
    // pagina principale
    this.getRequest().setAttribute("pubblicazioniInserite", "1");
  }

  /**
   * Legge le pubblicazioni del bando di gara dalla tabella PUBBLI, quindi copia
   * i campi TIPPUB e TESPUB delle occorrenze nella tabella PUBG per gli esiti
   *
   * @param status
   *        status della transazione
   * @param codgar
   *        codice della tornata
   * @param ngara
   *        codice della gara
   * @throws GestoreException
   */
  private void insertElencoPubblicazioniDaBando(TransactionStatus status,
      String codgar, String ngara) throws GestoreException {
    List listaTabellati = null;

    // si estraggono le pubblicazioni bando
    try {
      listaTabellati = this.sqlManager.getListVector(
          "select tippub, tespub from pubbli where codgar9 = ? and tippub not in (11,13,15,23) order by numpub asc",
          new Object[] { codgar });
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'estrazione delle pubblicazioni del bando di gara per la copia nelle pubblicazioni esito",
          "getPUBBLI", e);
    }

    String entitaInserimento = "PUBG";
    String campoChiaveGara = "NGARA";
    String valoreChiaveGara = ngara;
    String campoChiaveNumerica = "NPUBG";
    String campoTabellato = "TIPPUBG";
    String campoTesto = "TESPUBG";

    // se esistono pubblicazioni bando proseguo con gli inserimenti
    if (listaTabellati != null) {
      // si predispongono le informazioni necessarie per l'inserimento
      Vector elencoCampi = new Vector();
      elencoCampi.add(new DataColumn(entitaInserimento + "." + campoChiaveGara,
          new JdbcParametro(JdbcParametro.TIPO_TESTO, valoreChiaveGara)));
      elencoCampi.add(new DataColumn(entitaInserimento
          + "."
          + campoChiaveNumerica, new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
          null)));
      elencoCampi.add(new DataColumn(entitaInserimento + "." + campoTabellato,
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
      elencoCampi.add(new DataColumn(entitaInserimento + "." + campoTesto,
          new JdbcParametro(JdbcParametro.TIPO_TESTO, null)));
      DataColumnContainer container = new DataColumnContainer(elencoCampi);
      // si predispone il gestore per l'aggiornamento dell'entità
      DefaultGestoreEntitaChiaveNumerica gestore = new DefaultGestoreEntitaChiaveNumerica(
          entitaInserimento, campoChiaveNumerica,
          new String[] { campoChiaveGara }, this.getRequest());

      for (int i = 0; i < listaTabellati.size(); i++) {
        // si legge il singolo tipo pubblicazione e testo e si aggiornano i
        // campi
        // corrispondenti nel container, quindi si esegue l'inserimento
        container.getColumn(entitaInserimento + "." + campoChiaveNumerica).setValue(
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null));
        container.getColumn(entitaInserimento + "." + campoTabellato).setValue(
            new JdbcParametro(
                JdbcParametro.TIPO_NUMERICO,
                SqlManager.getValueFromVectorParam(listaTabellati.get(i), 0).getValue()));
        container.getColumn(entitaInserimento + "." + campoTesto).setValue(
            new JdbcParametro(
                JdbcParametro.TIPO_TESTO,
                SqlManager.getValueFromVectorParam(listaTabellati.get(i), 1).getValue()));
        gestore.inserisci(status, container);
      }
    }

  }

  /**
   * Estrae, a seconda dei parametri di input, i dati predefiniti per la
   * pubblicazione di bando o di esito
   *
   * @param codgar
   *        codice della tornata
   * @param ngara
   *        codice della gara
   * @param bando
   *        1 se pubblicazione bando, 2 se pubblicazione esito
   * @return lista di vector di JdbcParametro, con un'unica colonna contenente
   *         il tipo del tabellato
   *
   * @throws GestoreException
   */
  private List getListaTipiPubblicazione(String codgar, String ngara, Long bando)
      throws GestoreException {

    // NB: IL CODICE SOTTOSTANTE E' RICONDUCIBILE, A MENO DEI CAMPI ESTRATTI
    // NELLA SELECT, DI QUANTO IMPLEMENTATO IN GestoreInsDatiPubblicazioni;
    // DI CONSEGUENZA UNA EVENTUALE SUA MODIFICA IMPLICA UNA RIPETIZIONE DELLA
    // STESSA ANCHE NELL'ALTRA CLASSE

    // predisposizione dati di input per la query da eseguire
    Integer filtroClassificazione = null;
    if (bando.longValue() == 1)
      filtroClassificazione = new Integer(1);
    else
      filtroClassificazione = new Integer(2);

    // costruzione dinamica della query
    String appendFrom = "";
    String tipgar = "torn.tipgar";
    String importo = "torn.imptor";
    if (ngara != null) {
      // siamo nel caso di gara a lotto unico o lotto di gara
      appendFrom = ", gare";
      tipgar = "gare.tipgarg";
      importo = "gare.impapp";
    }

    StringBuffer sqlEstrazioneCATPUB = new StringBuffer("");
    sqlEstrazioneCATPUB.append("select codtab ");
    sqlEstrazioneCATPUB.append("from catpub, torn");
    sqlEstrazioneCATPUB.append(appendFrom);
    sqlEstrazioneCATPUB.append(" ");
    sqlEstrazioneCATPUB.append("where torn.codgar = ? ");
    sqlEstrazioneCATPUB.append("and torn.tipgen = catpub.tiplav ");
    // tipgar deve essere uguale al tipgar della tabella di partenza, oppure 0
    // oppure null
    sqlEstrazioneCATPUB.append("and (catpub.tipgar in (").append(tipgar).append(
        ", 0) or catpub.tipgar is null) ");
    // se l'importo è nullo allora si prende la fascia con limite inferiore 0,
    sqlEstrazioneCATPUB.append("and (  ")
      .append("( ").append(importo).append(" is null and (catpub.liminf = 0 or catpub.liminf is null)")
      .append(" )")
      // altrimenti si prende quella che contiene l'importo
      .append(" or ( ").append(importo).append(" is not null ")
      // In particolare se il limite superiore non è previsto, il campo viene lasciato nullo anziché impostato a un valore molto grande.
      // Analogamente per il limite inferiore, viene lasciato nullo invece che impostato a 0.
      // Va adeguato di conseguenza il criterio di selezione in CATPUB.
      .append("and (catpub.liminf is null or catpub.liminf <= ").append(importo).append(") and (").append(importo).append(" <= catpub.limsup or catpub.limsup is null)")
      .append(" )")
      .append("  ) ");
    sqlEstrazioneCATPUB.append("and catpub.tipcla = ? ");

    /* La query sqlEstrazioneCATPUB viene riscritta in sql puro (privo cioe'
     * del codice Java) al solo scopo di agevolare una futura manutenzione
     * del codice. Con '<importo>' si intende si intende la variabile java
     * importo (di tipo String)

      select codtab,
        from catpub, torn,....
       where torn.codgar = ?
         and torn.tipgen = catpub.tiplav
         and (catpub.tipgar in (?, 0) or catpub.tipgar is null)
         and (
              (<importo> is null and (catpub.liminf = 0 or catpub.liminf is null))
              or
              (<importo> is not null and
               ((catpub.liminf is null and 0 <= importo)
                or
                (catpub.liminf is not null and <importo> >= catpub.liminf)
               ) and catpub.limsup <= <importo>
              )
             )
         and catpub.tipcla = ?
         ...
     */

    if (ngara != null) {
      sqlEstrazioneCATPUB.append("and torn.codgar = gare.codgar1 ");
      sqlEstrazioneCATPUB.append("and gare.ngara = ? ");
    }

    // definizione dei parametri (se ngara è valorizzato va passato un
    // parametro in più)
    Object[] params = null;
    if (ngara != null) {
      params = new Object[] { codgar, filtroClassificazione, ngara };
    } else {
      params = new Object[] { codgar, filtroClassificazione };
    }

    // FINE PARTE SIMILE

    // estrazione del codtab
    Long codtab = null;
    try {
      codtab = (Long) this.sqlManager.getObject(sqlEstrazioneCATPUB.toString(),
          params);
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'estrazione delle categorie pubblicazioni bando ed esito",
          "getCATPUB", e);
    }

    // estrazione dell'elenco dei tipi pubblicazione predefiniti
    String sqlEstrazioneTABPUB = "select tippub from tabpub where codtab = ? order by codpub asc";
    List listaTabellati = null;
    try {
      listaTabellati = this.sqlManager.getListVector(sqlEstrazioneTABPUB,
          new Object[] { codtab });
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'estrazione della tabella pubblicazioni bando ed esito",
          "getTABPUB", e);
    }
    return listaTabellati;
  }

  /**
   * Data la lista, effettua gli inserimenti delle occorrenze nella tabella di
   * destinazione
   *
   * @param status
   *        status della transazione
   * @param codgar
   *        codice della tornata
   * @param ngara
   *        codice della gara
   * @param bando
   *        1 se pubblicazione bando, 2 se pubblicazione esito
   * @return lista di vector di JdbcParametro, con un'unica colonna contenente
   *         il tipo del tabellato
   * @throws GestoreException
   */
  private void insertElencoPubblicazioniPredefinite(TransactionStatus status,
      String codgar, String ngara, Long bando, List listaTabellati)
      throws GestoreException {
    String entitaInserimento = null;
    String campoChiaveGara = null;
    String valoreChiaveGara = null;
    String campoChiaveNumerica = null;
    String campoTabellato = null;
    if (bando.longValue() == 1) {
      entitaInserimento = "PUBBLI";
      campoChiaveGara = "CODGAR9";
      valoreChiaveGara = codgar;
      campoChiaveNumerica = "NUMPUB";
      campoTabellato = "TIPPUB";
    } else {
      entitaInserimento = "PUBG";
      campoChiaveGara = "NGARA";
      valoreChiaveGara = ngara;
      campoChiaveNumerica = "NPUBG";
      campoTabellato = "TIPPUBG";
    }

    // si passa all'inserimento dei dati nella tabella di interesse
    if (listaTabellati != null) {
      // si predispongono le informazioni necessarie per l'inserimento
      Vector elencoCampi = new Vector();
      elencoCampi.add(new DataColumn(entitaInserimento + "." + campoChiaveGara,
          new JdbcParametro(JdbcParametro.TIPO_TESTO, valoreChiaveGara)));
      elencoCampi.add(new DataColumn(entitaInserimento
          + "."
          + campoChiaveNumerica, new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
          null)));
      elencoCampi.add(new DataColumn(entitaInserimento + "." + campoTabellato,
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
      DataColumnContainer container = new DataColumnContainer(elencoCampi);
      // si predispone il gestore per l'aggiornamento dell'entità
      DefaultGestoreEntitaChiaveNumerica gestore = new DefaultGestoreEntitaChiaveNumerica(
          entitaInserimento, campoChiaveNumerica,
          new String[] { campoChiaveGara }, this.getRequest());

      for (int i = 0; i < listaTabellati.size(); i++) {
        // si legge il singolo tipo pubblicazione e si aggiorna il campo
        // corrispondente nel container, quindi si esegue l'inserimento
        container.getColumn(entitaInserimento + "." + campoChiaveNumerica).setValue(
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null));
        container.getColumn(entitaInserimento + "." + campoTabellato).setValue(
            new JdbcParametro(
                JdbcParametro.TIPO_NUMERICO,
                SqlManager.getValueFromVectorParam(listaTabellati.get(i), 0).getValue()));
        gestore.inserisci(status, container);
      }
    }
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
