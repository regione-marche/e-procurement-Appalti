/*
 * Created on 20/ott/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.w3.tags.gestori.submit;

import java.sql.SQLException;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.tags.gestori.submit.GestoreEntita;

public class GestoreW3LOTTMASSIVO extends AbstractGestoreEntita {

  static Logger logger = Logger.getLogger(GestoreW3LOTTMASSIVO.class);


  public GestoreW3LOTTMASSIVO() {
    super(false);
  }

  @Override
  public String getEntita() {
    return "W3GARA";
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  private void gestioneLotti(TransactionStatus status, DataColumnContainer datiForm) {
    Long numgara = 0L;
    String evento = "";
    int livEvento = 1;
    try {
      if(StringUtils.isNotEmpty(this.getRequest().getParameter("lottiAggiornamento"))) {
        String[] lotti= this.getRequest().getParameter("lottiAggiornamento").toString().split(";");
        numgara = (Long) datiForm.getColumn("W3LOTT.NUMGARA").getValue().getValue();

        for(int i=0; i<lotti.length;i++) {
          logger.debug("aggiornamento lotto "+lotti[i]+": inizio metodo");
          Long lotto=  Long.parseLong(lotti[i]);
          datiForm.setValue("W3LOTT.NUMLOTT", lotto);
          datiForm.setOriginalValue("W3LOTT.NUMLOTT", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, lotto));
          
          if(i==0) { //poiché i dati sono uguali, scriviamo solo al primo passaggio nel ciclo
            evento="Richiesto aggiornamento massivo per i seguenti lotti: "+String.join(", ", lotti)
            +"\nAggiornati i seguenti campi: ";
                DataColumn[] colonne =  datiForm.getColumns("W3LOTT", 2);
            for (int column=0; column < colonne.length; column++) {
              String[] tmp = colonne[column].getNomeFisico().split("\\.");
              if(tmp.length>1)
                evento+= "\n-"+tmp[1]+": "+ colonne[column].getValue();
              else
                evento+= "\n-"+tmp+": "+ colonne[column].getValue();
            }
          }
          
          evento += this.gestioneUlterioriCategorie(status, datiForm, "on".equals(this.getRequest().getParameter("elimina_ulteriori_categorie")),i==0);
          evento += this.gestioneCPVSecondarie(status, datiForm, "on".equals(this.getRequest().getParameter("elimina_cpv")),i==0);
          evento += this.gestioneCondizioni(status, datiForm, "on".equals(this.getRequest().getParameter("elimina_condizioni")),i==0);
          evento += this.gestioneCup(status, datiForm, "on".equals(this.getRequest().getParameter("elimina_cup")),i==0);
          evento += this.gestioneMisurePremiali(status, datiForm, "on".equals(this.getRequest().getParameter("elimina_misure_premiali")),i==0);
          evento += this.gestioneMotiviDeroga(status,datiForm, "on".equals(this.getRequest().getParameter("elimina_motivi_deroga")),i==0);
   
          AbstractGestoreEntita gestoreW3LOTT = new DefaultGestoreEntita(
              "W3LOTT", this.getRequest());
          gestoreW3LOTT.update(status, datiForm);

          logger.debug("aggiornamento lotto "+lotti[i]+" della gara "+datiForm.getColumn("W3LOTT.NUMGARA").getValue().getValue()+": fine metodo");
        }
      }else {
        this.getRequest().setAttribute("erroreAggiornamentoMassivo", "Nessun lotto selezionato!");
        livEvento=3;
        evento="Errore durante l'aggiornamento: nessun lotto selezionato";
      }
      this.getRequest().setAttribute("backToList", "true");
    }catch(Exception ex) {
      logger.error(ex);
      this.getRequest().setAttribute("erroreAggiornamentoMassivo", "Errore nell'aggiornamento");
      livEvento=3;
      evento="Errore durante l'aggiornamento dei lotti: "+ex;
    }
  //Logeventi
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setCodApplicazione("PG");
        logEvento.setOggEvento(numgara.toString());
        logEvento.setLivEvento(livEvento);
        logEvento.setCodEvento("GA_SIMOG_AGGIORNA_MASSIVO");
        logEvento.setDescr("Aggiornamento massivo dei lotti dell'anagrafica gara");
        logEvento.setErrmsg(evento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
      }
    

    logger.debug("aggiornamento lotti"+" della gara "+numgara+": fine metodo");
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    this.gestioneLotti(status,datiForm);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    this.gestioneLotti(status,datiForm);
  }

  private String gestioneInserimentiSchedeMultiple(
      TransactionStatus status, DataColumnContainer dataColumnContainer,
      AbstractGestoreChiaveNumerica gestore, String suffisso,
      DataColumn[] valoreChiave, boolean delete, boolean log)
          throws GestoreException {
    String toLog="";
    String nomeCampoNumeroRecord = "NUMERO_" + suffisso;
    String nomeCampoDelete = "DEL_" + suffisso;
    String nomeCampoMod = "MOD_" + suffisso;

    DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(
        dataColumnContainer.getColumns(gestore.getEntita(), 0));

    int numeroRecord = dataColumnContainer.getLong(nomeCampoNumeroRecord).intValue();

    //se presente almeno un record della scheda multipla, cancello tutto
    // Gestione delle pubblicazioni bando solo se esiste la colonna con il
    // numero di occorrenze
    if (dataColumnContainer.isColumn(nomeCampoNumeroRecord)) {

      if(delete) {
        try {
          this.sqlManager.update("delete from "+gestore.getEntita()+" where numgara=? and numlott=?", 
              new Object[] {dataColumnContainer.getColumn("W3LOTT.NUMGARA").getValue().getValue(),dataColumnContainer.getColumn("W3LOTT.NUMLOTT").getValue().getValue()});
          
          if(log) toLog+="\nEliminazione delle occorrenze in "+gestore.getEntita();
        } catch (SQLException e) {
          throw new GestoreException("Errore nell'eliminazione delle occorrende in "+gestore.getEntita(), null, e);
        }
      }

      int count = 0;
      for (int i = 1; i <= numeroRecord; i++) {
        DataColumnContainer newDataColumnContainer = new DataColumnContainer(
            tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

        if(newDataColumnContainer.isColumn(nomeCampoMod)
            && "1".equals(newDataColumnContainer.getString(nomeCampoMod)) && !(newDataColumnContainer.isColumn(nomeCampoDelete)
                && "1".equals(newDataColumnContainer.getString(nomeCampoDelete)))) {

          // Rimozione dei campi fittizi (il campo per la marcatura della delete e
          // tutti gli eventuali campi passati come argomento)
          newDataColumnContainer.removeColumns(new String[] {
              gestore.getEntita() + "." + nomeCampoDelete,
              gestore.getEntita() + "." + nomeCampoMod});

          // si settano tutti i campi chiave con i valori ereditati dal
          // chiamante
          if(gestore.getAltriCampiChiave()!=null){
            for (int z = 0; z < gestore.getAltriCampiChiave().length; z++) {
              newDataColumnContainer.getColumn(
                  gestore.getAltriCampiChiave()[z]).setValue(
                      valoreChiave[z].getValue());
            }
          }
          gestore.inserisci(status, newDataColumnContainer);
          count++;
        } 
      }
      if(log && count>0) toLog+="\nInserite "+count+" occorrenze in "+gestore.getEntita();
    }
    return toLog;
  }

  /**
   *
   * @param status
   * @param datiForm
   * @throws GestoreException
   */
  private String gestioneUlterioriCategorie(TransactionStatus status,
      DataColumnContainer datiForm, boolean delete, boolean log) throws GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("gestioneUlterioriCategorie: inizio metodo");

    AbstractGestoreChiaveNumerica gestoremultiploW3LOTTCATE = new DefaultGestoreEntitaChiaveNumerica(
        "W3LOTTCATE", "NUMCATE", new String[] { "NUMGARA", "NUMLOTT" },
        this.getRequest());
    String dati = gestioneInserimentiSchedeMultiple(status, datiForm,
        gestoremultiploW3LOTTCATE, "W3LOTTCATE", new DataColumn[] {
            datiForm.getColumn("W3LOTT.NUMGARA"),
            datiForm.getColumn("W3LOTT.NUMLOTT")},delete,log);

    if (logger.isDebugEnabled())
      logger.debug("gestioneUlterioriCategorie: fine metodo");
    return dati;
  }

  /**
   *
   * @param status
   * @param datiForm
   * @throws GestoreException
   */
  private String gestioneCondizioni(TransactionStatus status,
      DataColumnContainer datiForm, boolean delete, boolean log) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("gestioneCondizioni: inizio metodo");

    AbstractGestoreChiaveNumerica gestoremultiploW3COND = new DefaultGestoreEntitaChiaveNumerica(
        "W3COND", "NUM_COND", new String[] { "NUMGARA", "NUMLOTT" },
        this.getRequest());
    String dati = gestioneInserimentiSchedeMultiple(status, datiForm,
        gestoremultiploW3COND, "W3COND", new DataColumn[] {
            datiForm.getColumn("W3LOTT.NUMGARA"),
            datiForm.getColumn("W3LOTT.NUMLOTT") },delete,log);

    if (logger.isDebugEnabled())
      logger.debug("gestioneCondizioni: fine metodo");
    return dati;
  }

  /**
   *
   * @param status
   * @param datiForm
   * @throws GestoreException
   */
  private String gestioneCPVSecondarie(TransactionStatus status,
      DataColumnContainer datiForm, boolean delete, boolean log) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("gestioneCPVSecondarie: inizio metodo");

    AbstractGestoreChiaveNumerica gestoremultiploW3CPV = new DefaultGestoreEntitaChiaveNumerica(
        "W3CPV", "NUM_CPV", new String[] { "NUMGARA", "NUMLOTT" },
        this.getRequest());

    String dati = gestioneInserimentiSchedeMultiple(status, datiForm,
        gestoremultiploW3CPV, "W3CPV", new DataColumn[] {
            datiForm.getColumn("W3LOTT.NUMGARA"),
            datiForm.getColumn("W3LOTT.NUMLOTT")}, delete,log);

    if (logger.isDebugEnabled())
      logger.debug("gestioneCPVSecondarie: fine metodo");
    return dati;
  }

  /**
   *
   * @param status
   * @param datiForm
   * @throws GestoreException
   */
  private String gestioneCup(TransactionStatus status,
      DataColumnContainer datiForm, boolean delete, boolean log) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("gestioneCup: inizio metodo");

    AbstractGestoreChiaveNumerica gestoremultiploW3LOTTCUP = new DefaultGestoreEntitaChiaveNumerica(
        "W3LOTTCUP", "NUMCUP", new String[] { "NUMGARA", "NUMLOTT" },
        this.getRequest());
    String dati = gestioneInserimentiSchedeMultiple(status, datiForm,
        gestoremultiploW3LOTTCUP, "W3LOTTCUP", new DataColumn[] {
            datiForm.getColumn("W3LOTT.NUMGARA"),
            datiForm.getColumn("W3LOTT.NUMLOTT") }, delete, log);

    if (logger.isDebugEnabled())
      logger.debug("gestioneCup: fine metodo");
    return dati;
  }

  private String gestioneMisurePremiali(TransactionStatus status,
      DataColumnContainer datiForm, boolean delete, boolean log) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("gestioneMisurePremiali: inizio metodo");

    AbstractGestoreChiaveNumerica gestoremultiploW3MISPREMIALI = new DefaultGestoreEntitaChiaveNumerica(
        "W3MISPREMIALI", "IDMISURA", new String[] { "NUMGARA", "NUMLOTT" },
        this.getRequest());
    String dati = gestioneInserimentiSchedeMultiple(status, datiForm,
        gestoremultiploW3MISPREMIALI, "W3MISPREMIALI", new DataColumn[] {
            datiForm.getColumn("W3LOTT.NUMGARA"),
            datiForm.getColumn("W3LOTT.NUMLOTT") },delete, log);

    if (logger.isDebugEnabled())
      logger.debug("gestioneMisurePremiali: fine metodo");
    return dati;
  }

  private String gestioneMotiviDeroga(TransactionStatus status,
      DataColumnContainer datiForm, boolean delete, boolean log) throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("gestioneMotiviDeroga: inizio metodo");

    AbstractGestoreChiaveNumerica gestoremultiploW3LOTTDEROGHE = new DefaultGestoreEntitaChiaveNumerica(
        "W3LOTTDEROGHE", "IDDEROGA", new String[] { "NUMGARA", "NUMLOTT" },
        this.getRequest());
    String dati = gestioneInserimentiSchedeMultiple(status, datiForm,
        gestoremultiploW3LOTTDEROGHE, "W3LOTTDEROGHE", new DataColumn[] {
            datiForm.getColumn("W3LOTT.NUMGARA"),
            datiForm.getColumn("W3LOTT.NUMLOTT") },delete, log);

    if (logger.isDebugEnabled())
      logger.debug("gestioneMotiviDeroga: fine metodo");
    return dati;
  }
}

