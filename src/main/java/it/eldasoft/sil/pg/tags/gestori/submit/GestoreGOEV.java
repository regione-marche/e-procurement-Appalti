/*
 * Created on 26-may-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore per update dei dati della pagina criteri di valutazione. Poichè nella
 * pagina sono presenti delle sezioni dinamiche che si basano sulla stessa
 * entità della pagina, nel gestore si sono dovuti tenere dei comportamenti
 * particolari
 *
 * @author Marcello Caminiti
 */
public class GestoreGOEV extends AbstractGestoreChiaveNumerica {

  private GenChiaviManager genChiaviManager = null;

  @Override
  public String[] getAltriCampiChiave() {
    return new String[] { "NGARA" };
  }

  @Override
  public String getCampoNumericoChiave() {
    return "NECVAN";
  }

  @Override
  public String getEntita() {
    return "GOEV";
  }


  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);

    genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);

  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    JdbcParametro nGara = datiForm.getColumn("GOEV.NGARA").getValue();
    JdbcParametro numeroCriterio = datiForm.getColumn("GOEV.NECVAN").getValue();

    // Delete delle entita figlie di GOEV: DPUN,GCAP e DPRE
    // Se il criterio ha dei sub-criteri vanno eliminati pure questi
    try {
      // verifica se si deve effettuare l'eliminazione dei sub-criteri
      List listaSubCriteri = sqlManager.getListVector(
          "select NECVAN from GOEV where NGARA = ? and NECVAN <> ? and NECVAN1 = ?",
          new Object[] { nGara.getValue(), numeroCriterio.getValue(),
              numeroCriterio.getValue() });
      if (listaSubCriteri != null && listaSubCriteri.size() > 0) {
        for (Iterator iterator = listaSubCriteri.iterator(); iterator.hasNext();) {
          Vector criterio = (Vector) iterator.next();
          Long necvan = ((JdbcParametro) criterio.get(0)).longValue();
          // Vengono cancellate le occorrenze in DPUN, GCAP e DPRE dei
          // sub-criteri
          deleteFiglie(nGara, necvan);
          // Viene cancellato il sub-criterio da GOEV
          this.getSqlManager().update(
              "delete from GOEV where NGARA = ? and NECVAN = ?",
              new Object[] { nGara.getValue(), necvan });
        }
      }

      // Cancellazione delle occorrenze in DPUN, GCAP e DPRE del criterio
      // corrente
      deleteFiglie(nGara, numeroCriterio.longValue());

      deleteDoc(nGara.getStringValue(), numeroCriterio.longValue());


    } catch (SQLException e) {
      throw new GestoreException("Errore nella cancellazione del criterio "
          + "di valutazione "
          + numeroCriterio.getStringValue()
          + "della gara "
          + nGara.getValue(), null, e);
    }
    // La cancellazione dell'occorrenza in GOEV viene lasciata
    // alla classe AbstractGestoreEntita
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    super.preInsert(status, datiForm);


    //Cotrollo soglie minime
    this.controlloSoglie(datiForm,"INS");

    // si deve togliere il campo "NUMERO_SUBCRIT" dal DataColumnContainer
    // altrimenti vi sono problemi nella creazione del datiFormNew
    String nomeCampoNumeroRecord = "NUMERO_SUBCRIT";
    int numRecord = datiForm.getLong(nomeCampoNumeroRecord).intValue();
    datiForm.removeColumns(new String[]{nomeCampoNumeroRecord, "INDICE_SUBCRIT",
    		"GOEV.MAXPUN_FIT"});

    DataColumnContainer datiFormNew = new DataColumnContainer(datiForm);

    // Le sezioni dei sub-criteri sono sempre sull'entità GOEV,
    // quindi dal DataColumnContainer vanno tolti i campi delle sezioni
    // altrimenti ci sono problemi nell'aggiornamento di GOEV
    // Però togliendo i campi fittizzi non avviene l'inserimento dei dati
    // delle sezioni dinamiche, quindi c'è la necessità di fare una copia
    // del DataColumnContainer prima dell'eliminazione dei campi
    deleteColonneFittizzie(datiForm, numRecord);

    // il campo necvan1 del criterio deve essere valorizzato con necvan
    impostaNecvan1(datiForm, datiForm);

    try {

      // poichè si deve inserire il criterio padre prima dei figli(necvan
      // padre in necvan1 figli),
      // devo inserire subito manualmente il criterio padre e non fare
      // scattare la gestione standard

      datiForm.insert(this.getEntita(), this.sqlManager);

      String ngara = datiForm.getString("GOEV.NGARA");
      Long necvan = datiForm.getLong("GOEV.NECVAN");
      //Gestione del salvataggio su G1CRIDEF del criterio se non ci sono sottocriteri
      Long livpar = datiForm.getLong("GOEV.LIVPAR");
      if((new Long(1)).equals(livpar)){
        Double maxpun = datiForm.getDouble("GOEV.MAXPUN");
        this.impostaDatiInserimentoG1CRIDEF(ngara, necvan, maxpun);
      }


      // Gestione delle sezioni sub-criteri
      AbstractGestoreChiaveNumerica gestoreGOEVSubCriteri =
      	new DefaultGestoreEntitaChiaveNumerica("GOEV", "NECVAN",
      			new String[] { "NGARA" }, this.getRequest());

      gestioneSezioneDinamiche(status, datiFormNew, gestoreGOEVSubCriteri,
          numRecord);

      this.verificaPunteggiMax(datiForm);

      String sezionitec = UtilityStruts.getParametroString(this.getRequest(),
          "sezionitec");

      if ("1".equals(sezionitec) && (new Long(3)).equals(datiForm.getLong("GOEV.LIVPAR"))) {
        Long seztec = datiForm.getLong("GOEV.SEZTEC");
        this.sqlManager.update("update goev set seztec=? where ngara=? and necvan1=? and livpar=2", new Object[] {seztec, ngara, necvan});
      }

      this.setStopProcess(true);
      sqlManager.commitTransaction(status);


    } catch (SQLException e) {
      throw new GestoreException("Errore durante l'inserimento del criterio ",
          null, e);
    }

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
    this.verificaPunteggiMax(datiForm);
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {


    String  paginaCriterio = UtilityStruts.getParametroString(this.getRequest(),
        "paginaCriterio");

    String sezionitec = UtilityStruts.getParametroString(this.getRequest(),
        "sezionitec");

    if (!"3".equals(paginaCriterio)) {


      //Cotrollo soglie minime
      this.controlloSoglie(datiForm,"MOD");

      // Gestione delle sezioni sub-criteri
      AbstractGestoreChiaveNumerica gestoreGOEVSubCriteri = new DefaultGestoreEntitaChiaveNumerica(
          "GOEV", "NECVAN", new String[] { "NGARA" }, this.getRequest());

      boolean subcriteri = false;

      // Gestione delle sezioni dinamiche
      subcriteri = gestioneSezioneDinamiche(status, datiForm,
          gestoreGOEVSubCriteri);



      int numRecord = datiForm.getLong("NUMERO_SUBCRIT").intValue();

      // Le sezioni dei sub-criteri sono sempre sull'entità GOEV,
      // quindi dal DataColumnContainer vanno tolti i campi delle sezioni
      // altrimenti ci sono problemi nell'aggiornamento di GOEV
      deleteColonneFittizzie(datiForm, numRecord);

      // Se non vi sono più subcriteri, allora LIVPAR del padre deve essere
      // pari a 1
      String ngara = null;
      Long necvan = null;
      try {
        ngara = datiForm.getColumn("GOEV.NGARA").getValue().getStringValue();
        necvan = datiForm.getColumn("GOEV.NECVAN").getValue().longValue();
        Long numSubCriteri = (Long) sqlManager.getObject(
            "select count(*) from goev where ngara= ? and necvan1= ? and necvan<>?",
            new Object[] { ngara, necvan, necvan });
        if (numSubCriteri != null && numSubCriteri.equals(new Long(0))) {
          datiForm.getColumn("GOEV.LIVPAR").setObjectValue(new Long(1));
        }

        datiForm.removeColumns(new String[] { "GOEV.MAXPUN_FIT" });

      } catch (SQLException e) {
        throw new GestoreException(
            "Errore durante l'aggiornamento del criterio ", null, e);
      }

      // Se si inseriscono subcriteri, allora per il criterio padre livpar=3
      if (subcriteri == true)
        datiForm.getColumn("GOEV.LIVPAR").setObjectValue(new Long(3));



      try{
        if(datiForm.isModifiedColumn("GOEV.LIVPAR") && (new Long(1)).equals(datiForm.getColumn("GOEV.LIVPAR").getOriginalValue().longValue()) &&
            (new Long(3)).equals(datiForm.getLong("GOEV.LIVPAR"))){
          //Se livpar passa da 1 a 3, si deve cancellare l'occorrenza di G1CRIDEF, poichè G1CRIDEF è associata solo a LIVPAR=1 o LIVPAR=2
          this.sqlManager.update("delete from G1CRIDEF where ngara=? and necvan=?", new Object[]{ngara, necvan});
        } else if(datiForm.isModifiedColumn("GOEV.LIVPAR") && (new Long(3)).equals(datiForm.getColumn("GOEV.LIVPAR").getOriginalValue().longValue()) &&
            (new Long(1)).equals(datiForm.getLong("GOEV.LIVPAR"))){
          //Se livpar passa da 3 a 1, si inserire l'occorrenza di G1CRIDEF
          Double maxpun = datiForm.getDouble("GOEV.MAXPUN");
          this.impostaDatiInserimentoG1CRIDEF(ngara, necvan, maxpun);
        } else if (datiForm.isModifiedColumn("GOEV.MAXPUN") && (new Long(1)).equals(datiForm.getLong("GOEV.LIVPAR"))) {
          //Se modificato il solo punteggio si deve aggiornare il punteggio in G1CRIDEF
          Double maxpun = datiForm.getDouble("GOEV.MAXPUN");
          this.aggiornaPunteggioG1CRIDEF(ngara, necvan, maxpun);
        }
        //Aggiornamento del campo SEZTEC dei sottocriteri
        if ("1".equals(sezionitec) && (new Long(3)).equals(datiForm.getLong("GOEV.LIVPAR"))) {
          Long seztec = datiForm.getLong("GOEV.SEZTEC");
          this.sqlManager.update("update goev set seztec=? where ngara=? and necvan1=? and livpar=2", new Object[] {seztec, ngara, necvan});
        }
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nell'aggiornamento di G1CRIDEF", null, e);
      }

    } else {
      //Cotrollo soglie minime

      Double mintec = datiForm.getDouble("GARE1.MINTEC");
      Double mineco = datiForm.getDouble("GARE1.MINECO");

      Double maxPunTecnico = null;
      Double maxPunEconomico = null;

      if (datiForm.getObject("MAXTEC") instanceof Double)
        maxPunTecnico = datiForm.getDouble("MAXTEC");
      if (datiForm.getObject("MAXECO") instanceof Double)
        maxPunEconomico = datiForm.getDouble("MAXECO");

      if (maxPunTecnico==null &&  mintec!=null) {
        SQLException e = new SQLException();
        throw new GestoreException(
            "Errore nei controlli della soglia minima", "errors.gestoreException.*.criteriValutazione.punteggioTecnicoNullo", e);
      }

      if (maxPunEconomico == null &&  mineco != null) {
        SQLException e = new SQLException();
        throw new GestoreException(
            "Errore nei controlli della soglia minima", "errors.gestoreException.*.criteriValutazione.punteggioEconomicoNullo", e);
      }

      if(mintec!=null && maxPunTecnico!=null && mintec.doubleValue()>maxPunTecnico.doubleValue()){
        SQLException e = new SQLException();
        throw new GestoreException(
            "Errore nei controlli della soglia minima", "errors.gestoreException.*.criteriValutazione.sogliaTecnica",new Object[]{mintec,maxPunTecnico}, e);

      }
      if(mineco!=null && maxPunEconomico!=null && mineco.doubleValue()>maxPunEconomico.doubleValue()){
        SQLException e = new SQLException();
        throw new GestoreException(
            "Errore nei controlli della soglia minima", "errors.gestoreException.*.criteriValutazione.sogliaEconomica",new Object[]{mineco,maxPunEconomico}, e);

      }
      AbstractGestoreEntita gestoreGARE1 = new DefaultGestoreEntita("GARE1", this.getRequest());
      gestoreGARE1.update(status, datiForm);
      return;
    }


  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
    String  paginaCriterio = UtilityStruts.getParametroString(this.getRequest(),
        "paginaCriterio");


    if(!"3".equals(paginaCriterio)){
      this.verificaPunteggiMax(datiForm);
      }else{
        this.getRequest().setAttribute("salvataggioOK", "true");
      }
  }

  // Vengono eliminate le occorrenze di DPUN, GCAP e DPRE figlie si GOEV
  private void deleteFiglie(JdbcParametro nGara, Long necvan)
      throws GestoreException {
    try {
      this.getSqlManager().update(
          "delete from DPUN where NGARA = ? and NECVAN = ?",
          new Object[] { nGara.getValue(), necvan });
      this.getSqlManager().update(
          "delete from GCAP where NGARA = ? and NUMPAR = ?",
          new Object[] { nGara.getValue(), necvan });
      this.getSqlManager().update(
          "delete from DPRE where NGARA = ? and NUMPAR = ?",
          new Object[] { nGara.getValue(), necvan });

    } catch (SQLException e) {
      throw new GestoreException("Errore nella cancellazione del criterio "
          + "di valutazione "
          + necvan.toString()
          + "della gara "
          + nGara.getValue(), null, e);
    }
  }

  // Vengono eliminati i campi fittizzi dal DataColumnContainer.
  private void deleteColonneFittizzie(DataColumnContainer datiForm, int numeroRecord)
      throws GestoreException {
    String nomeCampoNumeroRecord = "NUMERO_SUBCRIT";
    String nomeCampoDelete = "DEL_SUBCRIT";
    String nomeCampoMod = "MOD_SUBCRIT";
    Long livpar = datiForm.getLong("GOEV.LIVPAR");
    for (int i = 1; i <= numeroRecord; i++) {
      datiForm.removeColumns(new String[]{
          "GOEV." + nomeCampoNumeroRecord,
          "GOEV." + nomeCampoDelete + "_" + i,
          "GOEV." + nomeCampoMod + "_" + i,
          "GOEV.NECVAN_"  + i,
          "GOEV.NGARA_"   + i,
          "GOEV.DESPAR_"  + i,
          "GOEV.NORPAR1_" + i,
          "GOEV.MAXPUN_"  + i,
          "GOEV.LIVPAR_"  + i,
          "GOEV.TIPPAR_"  + i,
          "GOEV.NORPAR_"  + i});
    }
  }

  private boolean gestioneSezioneDinamiche(TransactionStatus status,
      DataColumnContainer datiForm,
      AbstractGestoreChiaveNumerica gestoreGOEVSubCriteri)
      throws GestoreException {
    // Ricopio il codice del metodo
    // gestisciAggiornamentiRecordSchedaMultipla poichè
    // vengono richiesti dei controlli aggiuntivi alla cancellazione
    // e all'inserimento
    String nomeCampoNumeroRecord = "NUMERO_SUBCRIT";
    String nomeCampoDelete = "DEL_SUBCRIT";
    String nomeCampoMod = "MOD_SUBCRIT";

    boolean subcriteri = false;

    DataColumn[] valoreChiave = new DataColumn[] { datiForm.getColumn("GOEV.NGARA") };
    // Gestione delle pubblicazioni bando solo se esiste la colonna con il
    // numero di occorrenze
    if (datiForm.isColumn(nomeCampoNumeroRecord)) {

      // Estraggo dal dataColumnContainer tutte le occorrenze dei campi
      // dell'entità definita per il gestore
      DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(
          datiForm.getColumns(gestoreGOEVSubCriteri.getEntita(), 0));

    //Estrazione dal DataColumnContainer dei dati di G1CRIDEF
      DataColumnContainer tmpG1CRIDEF = new DataColumnContainer(
          datiForm.getColumns("G1CRIDEF", 0));

      int numeroRecord = datiForm.getLong(nomeCampoNumeroRecord).intValue();

      for (int i = 1; i <= numeroRecord; i++) {
        DataColumnContainer newDataColumnContainer = new DataColumnContainer(
            tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

        DataColumnContainer newG1CRIDEF = new DataColumnContainer(
            tmpG1CRIDEF.getColumnsBySuffix("_" + i, false));

        boolean deleteOccorrenza = newDataColumnContainer.isColumn(nomeCampoDelete)
            && "1".equals(newDataColumnContainer.getString(nomeCampoDelete));
        boolean updateOccorrenza = newDataColumnContainer.isColumn(nomeCampoMod)
            && "1".equals(newDataColumnContainer.getString(nomeCampoMod));

        // Rimozione dei campi fittizi (il campo per la marcatura della
        // delete e
        // tutti gli eventuali campi passati come argomento)
        newDataColumnContainer.removeColumns(new String[] {
            gestoreGOEVSubCriteri.getEntita() + "." + nomeCampoDelete,
            gestoreGOEVSubCriteri.getEntita() + "." + nomeCampoMod });

        if (deleteOccorrenza) {
          // Se è stata richiesta l'eliminazione e il campo chiave numerica e'
          // diverso da null eseguo l'effettiva eliminazione del record
          if (newDataColumnContainer.getLong(gestoreGOEVSubCriteri.getCampoNumericoChiave()) != null) {
            // Cancellazione delle occorrenze in DPUN, GCAP e DPRE
            // del criterio corrente
            deleteFiglie(
                datiForm.getColumn("GOEV.NGARA").getValue(),
                newDataColumnContainer.getColumn("NECVAN").getValue().longValue());
            gestoreGOEVSubCriteri.elimina(status, newDataColumnContainer);
          }

          // altrimenti e' stato eliminato un nuovo record non ancora inserito
          // ma predisposto nel form per l'inserimento
        } else {
          if (updateOccorrenza) {
            // si settano tutti i campi chiave con i valori ereditati dal
            // chiamante
            for (int z = 0; z < gestoreGOEVSubCriteri.getAltriCampiChiave().length; z++) {
              if (newDataColumnContainer.getColumn(
                  gestoreGOEVSubCriteri.getAltriCampiChiave()[z]).getValue().getValue() == null)
                newDataColumnContainer.getColumn(
                    gestoreGOEVSubCriteri.getAltriCampiChiave()[z]).setValue(
                    valoreChiave[z].getValue());
            }
            if (newDataColumnContainer.getLong(gestoreGOEVSubCriteri.getCampoNumericoChiave()) == null) {
              // Viene impostato il campo necvan1 con necvan del padre
              impostaNecvan1(datiForm, newDataColumnContainer);
              gestoreGOEVSubCriteri.inserisci(status, newDataColumnContainer);
              subcriteri = true;
              try {
                Long necvan = newDataColumnContainer.getLong("GOEV.NECVAN");
                Double maxpun = newDataColumnContainer.getDouble("GOEV.MAXPUN");
                String ngara = newDataColumnContainer.getString("GOEV.NGARA");
                this.impostaDatiInserimentoG1CRIDEF(ngara, necvan, maxpun);
              } catch (SQLException e) {
                throw new GestoreException("Errore nell'inserimento in G1CRIDEF",null ,e);
              }
            } else{
              gestoreGOEVSubCriteri.update(status, newDataColumnContainer);
              try {
                Long necvan = newDataColumnContainer.getLong("GOEV.NECVAN");
                Double maxpun = newDataColumnContainer.getDouble("GOEV.MAXPUN");
                String ngara = newDataColumnContainer.getString("GOEV.NGARA");
                this.aggiornaPunteggioG1CRIDEF(ngara, necvan, maxpun);
              } catch (SQLException e) {
                throw new GestoreException("Errore nell'aggiornamento del punteggio in G1CRIDEF",null ,e);
              }
            }
          }
        }
      }
    }
    return subcriteri;
  }

  private void gestioneSezioneDinamiche(TransactionStatus status,
      DataColumnContainer datiForm,
      AbstractGestoreChiaveNumerica gestoreGOEVSubCriteri, int numRecord)
      throws GestoreException {
    // Ricopio il codice del metodo
    // gestisciAggiornamentiRecordSchedaMultipla poichè
    // vengono richiesti dei controlli aggiuntivi alla cancellazione
    // e all'inserimento
    String nomeCampoDelete = "DEL_SUBCRIT";
    String nomeCampoMod = "MOD_SUBCRIT";

    DataColumn[] valoreChiave = new DataColumn[] { datiForm.getColumn("GOEV.NGARA") };
    // Gestione delle pubblicazioni bando solo se esiste la colonna con il
    // numero di occorrenze

    // Estraggo dal dataColumnContainer tutte le occorrenze dei campi
    // dell'entità definita per il gestore
    DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(
        datiForm.getColumns(gestoreGOEVSubCriteri.getEntita(), 0));

    //Estrazione dal DataColumnContainer dei dati di G1CRIDEF
    DataColumnContainer tmpG1CRIDEF = new DataColumnContainer(
        datiForm.getColumns("G1CRIDEF", 0));

    int numeroRecord = numRecord;

    for (int i = 1; i <= numeroRecord; i++) {
      DataColumnContainer newDataColumnContainer = new DataColumnContainer(
          tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

      DataColumnContainer newG1CRIDEF = new DataColumnContainer(
          tmpG1CRIDEF.getColumnsBySuffix("_" + i, false));

      boolean deleteOccorrenza = newDataColumnContainer.isColumn(nomeCampoDelete)
          && "1".equals(newDataColumnContainer.getString(nomeCampoDelete));
      boolean updateOccorrenza = newDataColumnContainer.isColumn(nomeCampoMod)
          && "1".equals(newDataColumnContainer.getString(nomeCampoMod));
      // Rimozione dei campi fittizi (il campo per la marcatura della
      // delete e tutti gli eventuali campi passati come argomento)
      newDataColumnContainer.removeColumns(new String[] {
          gestoreGOEVSubCriteri.getEntita() + "." + nomeCampoDelete,
          gestoreGOEVSubCriteri.getEntita() + "." + nomeCampoMod });

      if (deleteOccorrenza) {
        // Se è stata richiesta l'eliminazione e il campo chiave numerica e'
        // diverso da null eseguo l'effettiva eliminazione del record
        if (newDataColumnContainer.getLong(gestoreGOEVSubCriteri.getCampoNumericoChiave()) != null) {
          // Cancellazione delle occorrenze in DPUN, GCAP e DPRE
          // del criterio corrente
          deleteFiglie(datiForm.getColumn("GOEV.NGARA").getValue(),
              newDataColumnContainer.getColumn("NECVAN").getValue().longValue());
          gestoreGOEVSubCriteri.elimina(status, newDataColumnContainer);
        }

        // altrimenti e' stato eliminato un nuovo record non ancora inserito
        // ma predisposto nel form per l'inserimento
      } else {
        if (updateOccorrenza) {
          // si settano tutti i campi chiave con i valori ereditati dal
          // chiamante
          for (int z = 0; z < gestoreGOEVSubCriteri.getAltriCampiChiave().length; z++) {
            if (newDataColumnContainer.getColumn(
                gestoreGOEVSubCriteri.getAltriCampiChiave()[z]).getValue().getValue() == null)
              newDataColumnContainer.getColumn(
                  gestoreGOEVSubCriteri.getAltriCampiChiave()[z]).setValue(
                  valoreChiave[z].getValue());
          }


          if (newDataColumnContainer.getLong(gestoreGOEVSubCriteri.getCampoNumericoChiave()) == null) {
            // Viene impostato il campo necvan1 con necvan del padre
            impostaNecvan1(datiForm, newDataColumnContainer);
            gestoreGOEVSubCriteri.inserisci(status, newDataColumnContainer);
            try {
              Long necvan = newDataColumnContainer.getLong("GOEV.NECVAN");
              Double maxpun = newDataColumnContainer.getDouble("GOEV.MAXPUN");
              String ngara = newDataColumnContainer.getString("GOEV.NGARA");
              this.impostaDatiInserimentoG1CRIDEF(ngara, necvan, maxpun);
            } catch (SQLException e) {
              throw new GestoreException("Errore nell'inserimento in G1CRIDEF",null ,e);
            }
          } else{
            gestoreGOEVSubCriteri.update(status, newDataColumnContainer);
            try {
              Long necvan = newDataColumnContainer.getLong("GOEV.NECVAN");
              Double maxpun = newDataColumnContainer.getDouble("GOEV.MAXPUN");
              String ngara = newDataColumnContainer.getString("GOEV.NGARA");
              this.aggiornaPunteggioG1CRIDEF(ngara, necvan, maxpun);
            } catch (SQLException e) {
              throw new GestoreException("Errore nell'aggiornamento del punteggio in G1CRIDEF",null ,e);
            }
          }
        }
      }
    }

  }

  // Viene impostato in datiFormFiglio il campo necvan1 con il campo necvan di
  // datiFormPadre
  private void impostaNecvan1(DataColumnContainer datiFormPadre,
      DataColumnContainer datiFormFiglio) throws GestoreException {
    Long necvan;

    necvan = datiFormPadre.getColumn("GOEV.NECVAN").getValue().longValue();
    if (!datiFormFiglio.isColumn(this.getEntita() + ".NECVAN1"))
      datiFormFiglio.addColumn(this.getEntita() + ".NECVAN1",
          JdbcParametro.TIPO_NUMERICO);
    datiFormFiglio.setValue(this.getEntita() + ".NECVAN1", necvan);

  }

  /**
   * La funzione emette un warning quando il punteggio massimo di
   * tutti i criteri supera il valore 100
   *
   * @param datiForm
   * @throws GestoreException
   */
  private void verificaPunteggiMax(DataColumnContainer datiForm)
      throws GestoreException {

    String ngara = datiForm.getColumn("GOEV.NGARA").getValue().getStringValue();

    try {

      String sumMaxpun = "select MAXPUN from GOEV where NGARA = ? and (LIVPAR = 1 or LIVPAR = 3)";
      String costofisso = (String)sqlManager.getObject("select costofisso from gare1 where ngara=?", new Object[]{ngara});
      if("1".equals(costofisso))
        sumMaxpun = "select MAXPUN from GOEV where NGARA = ? and (LIVPAR = 1 or LIVPAR = 3) and TIPPAR=1";

      List<?> listaMaxPunteggio = sqlManager.getListHashMap(sumMaxpun,
          new Object[] { ngara });

      double maxPunteggio = 0;
      if (listaMaxPunteggio != null && listaMaxPunteggio.size() > 0) {
        for (int i = 0; i < listaMaxPunteggio.size(); i++) {
          Double tmp = (Double) ((JdbcParametro) ((HashMap<?,?>)
              listaMaxPunteggio.get(i)).get("MAXPUN")).getValue();
          if(tmp != null)
            maxPunteggio += tmp.doubleValue();
        }
      }

      if (maxPunteggio > 100){
        UtilityStruts.addMessage(this.getRequest(), "warning",
            "warnings.goev.verificaPunteggiMax.soglia100", null);
        }else{
          this.getRequest().setAttribute("salvataggioOK", "true");
        }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'estrazione del punteggio massimo", "getMaxPunteggio", e);
    }
  }


  /**
   * Se MINTEC.GARE1 > punteggio tecnico complessivo oppure
   * MINECO.GARE1 > punteggio economico complessivo, viene visualizzato
   * un messaggio di errore e viene bloccato il salvataggio
   *
   * @param datiForm
   * @param modalita
   * @throws GestoreException
   */
  private void controlloSoglie(DataColumnContainer datiForm, String modalita)
    throws GestoreException{

    boolean controllo = true;
    String ngara = datiForm.getString("GOEV.NGARA");
    Long tippar = null;
    String msgControllo=null;
    Double mintec = null;
    Double mineco = null;
    Double MaxPunteggio = null;
    Double soglia = null;
    Double maxpun = null;
    try {

      Vector datiGare1 = sqlManager.getVector("select mintec, mineco from gare1 where ngara = ?", new Object[]{ngara});
      if(datiGare1!=null && datiGare1.size()>0){
        PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
            this.getServletContext(), PgManager.class);

        mintec = (Double)((JdbcParametro) datiGare1.get(0)).getValue();
        mineco = (Double)((JdbcParametro) datiGare1.get(1)).getValue();

        Long numeroCriterio = datiForm.getLong("GOEV.NECVAN");

        if("DEL".equals(modalita)){

          Vector datiGOEV = sqlManager.getVector("select tippar, maxpun from goev where ngara = ? and necvan = ? ", new Object[]{ngara,numeroCriterio});
          if(datiGOEV!=null && datiGOEV.size()>0){
            tippar = (Long)((JdbcParametro) datiGOEV.get(0)).getValue();
            maxpun = (Double)((JdbcParametro) datiGOEV.get(1)).getValue();
          }
        }else{
          tippar = datiForm.getLong("GOEV.TIPPAR");
          maxpun = datiForm.getDouble("GOEV.MAXPUN");
        }


        if(maxpun==null)
          maxpun = new Double(0);

        if(tippar.longValue()==1){
          MaxPunteggio = pgManager.getSommaPunteggio(ngara, new Long(1), numeroCriterio);
          if(MaxPunteggio==null)
            MaxPunteggio = new Double(0);

          if(!"DEL".equals(modalita))
            MaxPunteggio = new Double(MaxPunteggio.doubleValue() + maxpun.doubleValue());

          if(mintec!=null && MaxPunteggio.doubleValue()!=0 && mintec.doubleValue()>MaxPunteggio.doubleValue()){
            controllo = false;
            msgControllo="errors.gestoreException.*.criteriValutazione.sogliaMaggiorePuntec";
            soglia = mintec;
          }
        }else{
          MaxPunteggio = pgManager.getSommaPunteggio(ngara, new Long(2), numeroCriterio);

          if(MaxPunteggio==null)
            MaxPunteggio = new Double(0);

          if(!"DEL".equals(modalita))
            MaxPunteggio = new Double(MaxPunteggio.doubleValue() + maxpun.doubleValue());

          if(mineco!=null && MaxPunteggio.doubleValue()!=0 && mineco.doubleValue()>MaxPunteggio.doubleValue()){
            controllo = false;
            msgControllo="errors.gestoreException.*.criteriValutazione.sogliaMaggiorePuneco";
            soglia = mineco;
          }
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nei controlli della soglia minima", null, e);
    }

    if(!controllo ){
      SQLException e = new SQLException();
      throw new GestoreException(
          "Errore nei controlli della soglia minima", msgControllo,new Object[]{soglia,MaxPunteggio} ,e);
    }

  }

  /**
   * Inizializzazioni dei campi dell'entità G1CRIDEF per l'inserimento
   *
   * @param ngara
   * @param necvan
   * @param maxpun
   * @throws GestoreException, SQLException
   */
  private void impostaDatiInserimentoG1CRIDEF(String ngara, Long necvan, Double maxpun) throws GestoreException, SQLException{
    Long id = new Long(this.genChiaviManager.getNextId("G1CRIDEF"));
    this.sqlManager.update("insert into g1cridef(id, ngara, necvan, formato, modpunti, modmanu,maxpun) values(?,?,?,?,?,?,?)",
        new Object[]{id, ngara,necvan,new Long(100),new Long(1), new Long(1), maxpun});
  }

  /**
   * Aggiornamento punteggio dell'entità G1CRIDEF
   *
   * @param ngara
   * @param necvan
   * @param maxpun
   * @throws SQLException
   */
  private void aggiornaPunteggioG1CRIDEF(String ngara, Long necvan, Double maxpun) throws SQLException{
    this.sqlManager.update("update g1cridef set maxpun=? where ngara=? and necvan=?", new Object[]{maxpun,ngara,necvan});
  }

  /**
   *
   * @param ngara
   * @throws GestoreException
   * @throws SQLException
   */
  private void deleteDoc(String ngara,Long necvan) throws GestoreException, SQLException{
    String findIdCriterioEliminato = "select g1cridef.ID from goev, g1cridef where g1cridef.necvan = goev.necvan and g1cridef.ngara = GOEV.NGARA and goev.ngara = ? and goev.necvan = ? ";
    Long id = (Long) sqlManager.getObject(findIdCriterioEliminato,
        new Object[] { ngara , necvan});
    if(id != null){
    //cancella occ in documgare
    String cercaCriteriTecniciDef = "select g1cridef.formato from g1cridef, gare, goev where gare.ngara = ? " +
        "and g1cridef.ngara = gare.ngara and g1cridef.formato != 100 " +
        "and g1cridef.ngara = goev.ngara and goev.necvan = g1cridef.necvan and goev.tippar = 1 and g1cridef.id != ?";
    List listaCriteriTecnici = sqlManager.getListVector(cercaCriteriTecniciDef,new Object[] { ngara, id});

    if (listaCriteriTecnici == null || listaCriteriTecnici.size() <= 0) {
      String delete = "delete from documgara where ngara = ? and descrizione = 'Offerta tecnica'";
      sqlManager.update(delete, new Object[]{ngara});
      }
    }
  }
}