/*
 * Created on 15/09/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiRicezioneFunction;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
/**
 * Gestore non standard che si occupa di gestire la funzionalità
 * per assegnare il numero d'ordine (DITG.NUMORDPL)
 *
 * @author Marcello Caminiti
 */
public class GestoreAssegnaNumOrdine extends AbstractGestoreEntita {

  /** Logger */
  static Logger            logger           = Logger.getLogger(GestoreAssegnaNumOrdine.class);

  private PgManager pgManager = null;

  @Override
  public String getEntita() {
    return "DITG";
  }

  public GestoreAssegnaNumOrdine() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestoreAssegnaNumOrdine(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per gestire diversi SQL
    this.pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer
      dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {

    //variabili per tracciatura eventi
    int livEvento = 1;
    String codEvento = "GA_ASSEGNA_ORDINE_ELENCO";
    String oggEvento = "";
    String descrEvento = "";
    String errMsgEvento = "";
    int modalitaAssegnamento=0;
    String  aggnumord = UtilityStruts.getParametroString(this.getRequest(),"aggnumord");
    String modalita = "";
    try{
      String  ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
      String  modAssegnamento = UtilityStruts.getParametroString(this.getRequest(),"modalitaAss");
      String campoApplicazione = UtilityStruts.getParametroString(this.getRequest(),"campoApp");


      if(modAssegnamento!=null && !"".equals(modAssegnamento))
        modalitaAssegnamento = Integer.parseInt(modAssegnamento);

      oggEvento = ngara;
      if("1".equals(aggnumord))
        descrEvento="Attivazione operatori in ";
      else
        descrEvento="Assegnazione numero ordine operatori in ";

      if(modalitaAssegnamento==3)
        descrEvento = "Annullato numero ordine operatori in ";

      //Valutazione se elenco o catalogo
      Long genere = null;
      try {
        genere = (Long)this.sqlManager.getObject("select genere from gare where ngara=?", new Object[]{ngara});
      } catch (SQLException e) {
        livEvento = 3;
        errMsgEvento = e.getMessage();
        descrEvento += "elenco/catalogo";
        throw new GestoreException("Errore nella lettura del campo GARE.GENERE",null, e);
      }

      if(new Long(10).equals(genere))
        descrEvento += "elenco";
      else
        descrEvento += "catalogo";
      //la variabile modalitaAssegnamento può assumere i seguenti valori:
      //1 -> Assegnare il numero d'ordine in modalità casuale
      //2 -> Assegnare il numero d'ordine in base alla data e ora di arrivo delle domande di iscrizione
      //3 -> Annullare il numero d'ordine per assegnarlo manualmente
      //4 -> Assegnare il numero d'ordine in base alla data di abilitazione

      //la variabile campoApplicazione può assumere i seguenti valori:
      //1 -> Solo per i nuovi iscritti (ovvero quelli che hanno null il numero d'ordine)
      //2 -> Per tutti gli operatori (rigenera il numero d'ordine su tutti i record)

      switch(modalitaAssegnamento){
        case 1:
          //Modalità casuale
          modalita = " in modalità casuale ";
          this.modalitaCasuale(ngara, campoApplicazione,status);
          break;
        case 2:
          modalita = " in base a data domanda iscrizione ";
          this.modalitaDataArrivoDomandeIscrizione(ngara, campoApplicazione,status);
          break;
        case 3:
          modalita = "";
          this.modalitaAnnullaNumeroOrdine(ngara);
          break;
        case 4:
          modalita = " in base a data abilitazione ";
          this.modalitaDataAbilitazione(ngara, campoApplicazione,status);
          break;

      }

      PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
          this.getServletContext(), PgManager.class);


      pgManager.aggiornaFaseGara(new Long(GestioneFasiRicezioneFunction.FASE_ELENCO_CONCORRENTI_ABILITATI), ngara, true);

      //Se tutto è andato bene setto nel request il parametro numeroAssegnato = 1
      this.getRequest().setAttribute("numeroAssegnato", "1");
    }catch(GestoreException e){
      livEvento =3;
      errMsgEvento = e.getMessage();
      throw e;
    } finally{
      try {
        if(!"1".equals(aggnumord) && !"".equals(modalita)){
          descrEvento +=  modalita;
        }
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(oggEvento);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descrEvento);
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        String messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
      }
    }
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer dataColumnContainer)
      throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }


  /**
   * Metodo per il settaggio del numero d'ordine (DITG.NUMORDPL) secondo la modalità casuale
   * (Algoritmo preso da Powerbuilder)
   *
   * @param ngara
   * @param campoApplicazione   : 1   solo per NUMORDPL=null
   *                              2   per tutti
   *                              3   solo per NUMORDPL!=null
   * @param status
   *
   * @throws GestoreException
   */
  public void modalitaCasuale(String ngara, String campoApplicazione,TransactionStatus status) throws GestoreException {

    String codgar= "$" + ngara; //Per le gare elenco operatori economici codgar="$"+ngara
    String select="select dittao,numordpl from ditg where codgar5 = ? and ngara5=? and (((ammgar=1 or ammgar is null) and abilitaz=1)";

    int numordplPartenza=0;

    try {
      if (campoApplicazione!=null && "1".equals(campoApplicazione)){
        select+= " and numordpl is null";
        //Nel caso di campoApplicazione = 1 la scelta del numero d'ordine deve essere fatta
        //a partire dal max numordpl + 1
        Long maxNumordpl = (Long) sqlManager.getObject(
            "select max(numordpl) from ditg where codgar5 = ? and ngara5=? and (ammgar=1 or ammgar is null)", new Object[] { codgar, ngara});
        if (maxNumordpl!=null && maxNumordpl.longValue()>0)
          numordplPartenza = maxNumordpl.intValue();
      }else  if ("2".equals(campoApplicazione)){
        select+= " or numordpl is not null";
      }else{
        select="select dittao,numordpl from ditg where codgar5 = ? and ngara5=? and ( numordpl is not null";
      }
      select+=")";


      List listaOperatori = this.sqlManager.getListVector(select, new Object[]{codgar,ngara});

      if (listaOperatori!=null && listaOperatori.size()>0){
        int numeroOperatori = listaOperatori.size();
        ArrayList  valori = new ArrayList(numeroOperatori);
        String update="update ditg set numordpl = ?, dattivaz = ? where codgar5 = ? and ngara5=? and dittao = ?";
        Date dataOdierna = this.getDataOdierna();

        for (int i=0;i<numeroOperatori;i++)
          valori.add(i, new Long(i + 1 + numordplPartenza));

        Random r =  new Random();

        for(int i = 0;i<numeroOperatori;i++) {
          // valore compreso tra 0 e la dimensione del vettore valori
          int rand=     r.nextInt(valori.size());

          Vector operatore = (Vector) listaOperatori.get(i);
          String dittao = (String)((JdbcParametro) operatore.get(0)).getValue();
          Long numordplOriginario = ((JdbcParametro) operatore.get(1)).longValue();

          if (numordplOriginario==null )
            this.pgManager.updatePenalita("$" + ngara, ngara, dittao, status);

          //Aggiornamento del campo numordpl
          if(this.dataAttivazValorizzata(ngara, dittao))
            this.sqlManager.update("update ditg set numordpl = ? where codgar5 = ? and ngara5=? and dittao = ?", new Object[] { valori.get(rand),codgar, ngara,dittao});
          else
            this.sqlManager.update(update, new Object[] { valori.get(rand),dataOdierna,codgar, ngara,dittao});

          //si deve aggiornare il vettore dei valori, eliminado il valore appena assegnato
          valori.remove(rand);
        }

      }

    } catch (SQLException e) {
      throw new GestoreException("Errore nell'assegnamento del numero d'ordine in modalità casuale", null, e);
    }
  }

  /**
   * Metodo per il settaggio del numero d'ordine (DITG.NUMORDPL) secondo la modalità
   * in base alla data e ora di arrivo delle domande di iscrizione
   *
   * @param ngara
   * @param campoApplicazione   : 1   solo per NUMORDPL=null
   *                              2   per tutti
   * @param status
   *
   * @throws GestoreException
   */
  private void modalitaDataArrivoDomandeIscrizione(String ngara, String campoApplicazione,TransactionStatus status) throws GestoreException {
    try {
      this.assegnaNumeroOrdine(ngara, campoApplicazione, status, "order by dricind,oradom", "dricind");
    } catch (SQLException e) {
      throw new GestoreException("Errore nell'assegnamento del numero d'ordine secondo la modalità "+
          "in base alla data e ora di arrivo delle domande di iscrizione", null, e);
    }
  }

  /**
   * Metodo per il settaggio del numero d'ordine (DITG.NUMORDPL) secondo la modalità
   * in base alla data e ora di arrivo delle domande di iscrizione
   *
   * @param ngara
   * @param campoApplicazione   : 1   solo per NUMORDPL=null
   *                              2   per tutti
   *
   * @throws GestoreException
   */
  private void modalitaAnnullaNumeroOrdine(String ngara) throws GestoreException {

    String codgar= "$" + ngara; //Per le gare elenco operatori economici codgar="$"+ngara
    String update="update ditg set numordpl = null, dattivaz = null where codgar5 = ? and ngara5=? and (ammgar=1 or ammgar is null) and abilitaz=1";

    //Aggiornamento del campo numordpl
    try {
      this.sqlManager.update(update, new Object[] {codgar, ngara});
    } catch (SQLException e) {
      throw new GestoreException("Errore nell'annullamento del numero ordine ", null, e);
    }

  }


  /**
   * Metodo per il settaggio del numero d'ordine (COMMNOMIN.NUMORDPL) secondo la modalità casuale
   *
   * Rivisitare i parametri
   * @param ngara
   * @param campoApplicazione   : 1   solo per NUMORDPL=null
   *                              2   per tutti
   * @param status
   *
   * @throws GestoreException
   */
  public void modalitaCasualeCommissione(String ngara, TransactionStatus status) throws GestoreException {

    String codgar= "$" + ngara; //Per le gare elenco operatori economici codgar="$"+ngara
    String select="select id,codtec,numord from commnomin where dataab is not null";
    int numordplPartenza=0;

      List listaNominativi;
      try {
        listaNominativi = this.sqlManager.getListVector(select, new Object[]{});
        if (listaNominativi!=null && listaNominativi.size()>0){
          int numeroNominativi = listaNominativi.size();
          ArrayList valori = new ArrayList(numeroNominativi);
          String update = "update commnomin set numord = ? where id = ? ";
          Date dataOdierna = this.getDataOdierna();

          for (int i = 0; i < numeroNominativi; i++)
            valori.add(i, new Long(i + 1 + numordplPartenza));

          Random r = new Random();

          for (int i = 0; i < numeroNominativi; i++) {
            // valore compreso tra 0 e la dimensione del vettore valori
            int rand = r.nextInt(valori.size());

            Vector nominativo = (Vector) listaNominativi.get(i);
            Long id = (Long) ((JdbcParametro) nominativo.get(0)).getValue();
            Long numordOriginario = ((JdbcParametro) nominativo.get(2)).longValue();

            // Aggiornamento del campo numord
            // questo if potrebbe tornare utile in caso di Abilitazione massiva....
            // if(this.dataAttivazValorizzata(ngara, dittao))
            this.sqlManager.update(update, new Object[] {valori.get(rand), id });

            // si deve aggiornare il vettore dei valori, eliminando il valore appena assegnato
            valori.remove(rand);
          }
        }

      } catch (SQLException e) {
        throw new GestoreException("Errore nell'assegnamento del numero d'ordine in modalità casuale", null, e);
      }


  }


  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {


  }

  /**
   * Viene determinata la data odierna
   *
   * @return  data odierna
   */
  private Date getDataOdierna(){
    String dataOdiernaString = UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA);
    Date dataOdierna = UtilityDate.convertiData(dataOdiernaString, UtilityDate.FORMATO_GG_MM_AAAA);

    return dataOdierna;
  }



  private boolean dataAttivazValorizzata(String ngara,String ditta)throws GestoreException {
    String select="select dattivaz from ditg where codgar5 = ? and ngara5=? and dittao = ?";
    boolean ret =false;
    try {
      Date dattivaz = (Date)this.sqlManager.getObject(select, new Object[]{"$" + ngara, ngara,ditta});
      if (dattivaz != null)
        ret = true;
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della data attivazione della ditta " + ditta, null, e);
    }


    return ret;
  }

  /**
   * Metodo per il settaggio del numero d'ordine (DITG.NUMORDPL) secondo la modalità
   * in base alla data di abilitazione
   *
   * @param ngara
   * @param campoApplicazione   : 1   solo per NUMORDPL=null
   *                              2   per tutti
   * @param status
   *
   * @throws GestoreException
   */
  private void modalitaDataAbilitazione(String ngara, String campoApplicazione,TransactionStatus status) throws GestoreException {
    try {
      this.assegnaNumeroOrdine(ngara, campoApplicazione, status, "order by dabilitaz,dricind,oradom", "dabilitaz");
    } catch (SQLException e) {
      throw new GestoreException("Errore nell'assegnamento del numero d'ordine secondo la modalità "+
          "in base alla data di abilitazione", null, e);
    }
  }

  /**
   * Metodo per il settaggio del numero d'ordine (DITG.NUMORDPL) secondo la modalità
   * in base alla data di abilitazione
   *
   * @param ngara
   * @param campoApplicazione   : 1   solo per NUMORDPL=null
   *                              2   per tutti
   * @param status
   *
   * @throws GestoreException
   */
  private void assegnaNumeroOrdine(String ngara, String campoApplicazione,TransactionStatus status, String ordinamento, String campoNonNull) throws SQLException, GestoreException {
    String codgar= "$" + ngara; //Per le gare elenco operatori economici codgar="$"+ngara
    String select="select dittao,numordpl from ditg where codgar5 = ? and ngara5=? and (((ammgar=1 or ammgar is null) "+
          "and abilitaz=1 and " + campoNonNull + " is not null)";

    int numordplPartenza=0;

    if (campoApplicazione!=null && "1".equals(campoApplicazione)){
      select+= " and numordpl is null";
      //Nel caso di campoApplicazione = 1 la scelta del numero d'ordine deve essere fatta
      //a partire dal max numordpl + 1
      Long maxNumordpl = (Long) sqlManager.getObject(
          "select max(numordpl) from ditg where codgar5 = ? and ngara5=? and (ammgar=1 or ammgar is null) and " + campoNonNull + "  is not null", new Object[] { codgar, ngara});
      if (maxNumordpl!=null && maxNumordpl.longValue()>0)
        numordplPartenza = maxNumordpl.intValue();
    }else{
      select+= " or numordpl is not null";
    }
    select+=")";

    select += " " + ordinamento;
    List listaOperatori = this.sqlManager.getListVector(select, new Object[]{codgar,ngara});

    if (listaOperatori!=null && listaOperatori.size()>0){
      int numeroOperatori = listaOperatori.size();
      String update="update ditg set numordpl = ?, dattivaz = ?  where codgar5 = ? and ngara5=? and dittao = ?";
      Date dataOdierna = this.getDataOdierna();

      for(int i = 0;i<numeroOperatori;i++) {
        Vector operatore = (Vector) listaOperatori.get(i);
        String dittao = (String)((JdbcParametro) operatore.get(0)).getValue();
        Long numordplOriginario = ((JdbcParametro) operatore.get(1)).longValue();

        if (numordplOriginario==null )
          this.pgManager.updatePenalita("$" + ngara, ngara, dittao, status);

        numordplPartenza++;

        //Aggiornamento del campo numordpl
        if(this.dataAttivazValorizzata(ngara, dittao))
          this.sqlManager.update("update ditg set numordpl = ? where codgar5 = ? and ngara5=? and dittao = ?", new Object[] {new Long(numordplPartenza),codgar, ngara,dittao});
        else
          this.sqlManager.update(update, new Object[] { new Long(numordplPartenza),dataOdierna,codgar, ngara,dittao});

      }
    }



  }
}