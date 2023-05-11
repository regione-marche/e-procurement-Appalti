/*
 * Created on 03/08/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;

/**
 * Gestore non standard delle occorrenze dell'entita IMPRDOCG presenti piu' volte
 * nella pagina "Verifica documenti richiesti" (documgara-lista.jsp)
 *
 * Questa classe NON e' un gestore standard e prepara i dati di ciascuna
 * occorrenza presente nella scheda e demanda alla classe DefaultGestoreEntita
 * le operazioni di insert, update e delete
 *
 * @author Marcello Caminiti
 */
public class GestoreVerificaDocumenti extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "IMPRDOCG";
  }

  public GestoreVerificaDocumenti() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestoreVerificaDocumenti(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  final double MAX_FILE_SIZE = Math.pow(2, 20) * 5;

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    String codgar = impl.getString("V_GARE_DOCDITTA.CODGAR");
    Long norddoci = impl.getLong("V_GARE_DOCDITTA.NORDDOCI");
    String codimp = impl.getString("V_GARE_DOCDITTA.CODIMP");
    String ngara = impl.getString("V_GARE_DOCDITTA.NGARA");
    Long proveni = new Long(2);

  //cancellazione di W_DOCDIG
    String delete="delete from w_docdig where IDPRG in(select IMPRDOCG.IDPRG from IMPRDOCG where CODGAR = ? and NORDDOCI=? and CODIMP = ? and NGARA = ? and PROVENI=?) and IDDOCDIG in (select IMPRDOCG.IDDOCDG from IMPRDOCG where CODGAR = ? and NORDDOCI=? and CODIMP = ? and NGARA = ? and PROVENI=?)";
    try {
      this.sqlManager.update(delete, new Object[] { codgar, norddoci, codimp,ngara, proveni, codgar,norddoci,codimp, ngara, proveni});
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'eliminazione dele righe delle tabella IMPRDOCG ", null,  e);
    }

    try {
      this.getSqlManager().update(
          "delete from IMPRDOCG where CODGAR = ? and NORDDOCI = ? and CODIMP =? and ngara = ? and proveni=?",
          new Object[] { codgar, norddoci,codimp,ngara, proveni});
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nella cancellazione delle occorrenze di IMPRDOCG con chiave "
          + "CODGAR = " + codgar + ", NORDDOCI = " + norddoci.toString()
          + ", CODIMP = " + codimp, null, e);
    }

  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {


    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
        "tabellatiManager", this.getServletContext(), TabellatiManager.class);

    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean(
        "pgManagerEst1", this.getServletContext(), PgManagerEst1.class);

    //calcolo del progressivo di IMPRDOCG
    String codiceGara=impl.getString("IMPRDOCG.CODGAR");
    String codiceDitta=impl.getString("IMPRDOCG.CODIMP");
    String ngara=impl.getString("IMPRDOCG.NGARA");

    String stepWizard = UtilityStruts.getParametroString(this.getRequest(),
      "stepWizard");

    String select="select max(NORDDOCI) from IMPRDOCG where CODGAR=? and CODIMP=?";
    Long progressivo=null;
    boolean inserimentoW_DOCDIG= false;
    long newIDDOCDIG=1;

    try {
      Long nProgressivoIMPRDOCG = (Long) this.getSqlManager().getObject(
        select,new Object[]{codiceGara,codiceDitta});

      Long nProgressivoDOCUMGARA = (Long) this.getSqlManager().getObject(
          "select max(NORDDOCG) from DOCUMGARA where CODGAR=?",new Object[]{codiceGara});

      if (nProgressivoIMPRDOCG==null)
        nProgressivoIMPRDOCG = new Long (0);

      if (nProgressivoDOCUMGARA==null)
        nProgressivoDOCUMGARA = new Long (0);

      progressivo=nProgressivoDOCUMGARA;
      if (nProgressivoIMPRDOCG.longValue()>nProgressivoDOCUMGARA.longValue())
        progressivo = nProgressivoIMPRDOCG;

      impl.getColumn("IMPRDOCG.NORDDOCI").setChiave(true);
      impl.setValue("IMPRDOCG.NORDDOCI", new Long(progressivo.longValue() + 1));

      impl.addColumn("IMPRDOCG.DOCTEL", JdbcParametro.TIPO_TESTO,"2");

      //Valorizzazione campo BUSTA
      //Nel caso di elenchi e cataloghi non si deve valorizzare il campo busta
      //Long step = new Long(stepWizard);
      Long genere = (Long)this.sqlManager.getObject("select genere from gare where ngara=?", new Object[]{ngara});
      if(genere== null || (genere.longValue()!=10 && genere.longValue()!=20)){
        Long busta=pgManagerEst1.getValoreBusta(stepWizard);
        impl.addColumn("IMPRDOCG.BUSTA", JdbcParametro.TIPO_NUMERICO,busta);
      }
    }catch(SQLException e){
      throw new GestoreException("Errore nel calcolo del progressivo di IMPRDCG", null, e);
    }


    //this.inserisci(status, impl);
    try{
      impl.insert("IMPRDOCG", sqlManager);
    }catch(SQLException e){
      throw new GestoreException("Errore nell'inserimento in  IMPRDCG", null, e);
    }

    //Gestione inserimento su W_DOCDIG
    if (impl.isColumn("FILEDAALLEGARE")
        && impl.getString("FILEDAALLEGARE") != null
        && !impl.getString("FILEDAALLEGARE").trim().equals("")) {
      ByteArrayOutputStream baos = null;
      try {

        String dimMassimaTabellatoStringa = tabellatiManager.getDescrTabellato("A1072", "1");
        if(dimMassimaTabellatoStringa==null || "".equals(dimMassimaTabellatoStringa)){
          throw new GestoreException("Non è presente il tabellato A1072 per determinare la dimensione "
              + "massima dell'upload del file", "upload.noTabellato", null);
        }
        int pos = dimMassimaTabellatoStringa.indexOf("(");
        if (pos<1){
          throw new GestoreException("Non è possibile determinare dal tabellato A1072 la dimensione "
              + "massima dell'upload del file", "upload.noValore", null);
        }
        dimMassimaTabellatoStringa = dimMassimaTabellatoStringa.substring(0, pos-1);
        dimMassimaTabellatoStringa = dimMassimaTabellatoStringa.trim();
        double dimMassimaTabellatoByte = Math.pow(2, 20) * Double.parseDouble(dimMassimaTabellatoStringa);

        if(this.getForm().getSelezioneFile().getFileSize() == 0 ){
          throw new GestoreException("Il file specificato è vuoto. Per continuare specificare un altro file",
              "upload.fileVuoto", null, null);
        }else if(this.getForm().getSelezioneFile().getFileSize()> dimMassimaTabellatoByte){
          throw new GestoreException("Il file selezionato ha una dimensione "
              + "superiore al massimo consentito (" + dimMassimaTabellatoStringa + " MB)" , "upload.overflow", new String[] { dimMassimaTabellatoStringa + " MB" },null);
        }else {
          String fileName = this.getForm().getSelezioneFile().getFileName();
          if(!FileAllegatoManager.isEstensioneFileAmmessa(fileName)){
            throw new GestoreException("Il file selezionato da caricare ha un'estensione non accettata",
                "upload.estensioneNonAmmessa", new String[]{fileName}, null);
          }else{
            baos = new ByteArrayOutputStream();
            baos.write(this.getForm().getSelezioneFile().getFileData());
          }
        //Si deve calcolare il valore di IDDOCDIG
        Long maxIDDOCDIG = (Long) this.geneManager.getSql().getObject(
                  "select max(IDDOCDIG) from W_DOCDIG where IDPRG= ?",
                  new Object[] {"PG"} );


          if (maxIDDOCDIG != null && maxIDDOCDIG.longValue()>0)
            newIDDOCDIG = maxIDDOCDIG.longValue() + 1;

          String nomeFile="";
          int len = impl.getString("FILEDAALLEGARE").length();
          int posizioneBarra = impl.getString("FILEDAALLEGARE").lastIndexOf("\\");
          nomeFile=impl.getString("FILEDAALLEGARE").substring(posizioneBarra+1,len).toUpperCase();

          impl.addColumn("W_DOCDIG.IDPRG", JdbcParametro.TIPO_TESTO,"PG");
          impl.getColumn("W_DOCDIG.IDPRG").setChiave(true);
          impl.addColumn("W_DOCDIG.IDDOCDIG", JdbcParametro.TIPO_NUMERICO,new Long(newIDDOCDIG));
          impl.getColumn("W_DOCDIG.IDDOCDIG").setChiave(true);
          impl.addColumn("W_DOCDIG.DIGENT", JdbcParametro.TIPO_TESTO,"IMPRDOCG");
          impl.addColumn("W_DOCDIG.DIGKEY1", JdbcParametro.TIPO_TESTO,codiceGara);
          impl.addColumn("W_DOCDIG.DIGKEY2", JdbcParametro.TIPO_NUMERICO,new Long(progressivo.longValue() + 1));
          impl.addColumn("W_DOCDIG.DIGNOMDOC", JdbcParametro.TIPO_TESTO,nomeFile);
          impl.addColumn("W_DOCDIG.DIGOGG", JdbcParametro.TIPO_BINARIO,baos);
          impl.insert("W_DOCDIG", sqlManager);
          inserimentoW_DOCDIG = true;
        }

      } catch (FileNotFoundException e) {
        throw new GestoreException("File da caricare non trovato", "upload", e);
      } catch (IOException e) {
        throw new GestoreException(
            "Si è verificato un errore durante la scrittura del buffer per il salvataggio del file allegato su DB",
            "upload", e);
      } catch (SQLException e) {
        throw new GestoreException("Errore nel calcolo del contatore di W_DOCDIG", null, e);
      }
    }

    //Aggiornamento di IMPRDOCG con i riferimenti a W_DOCDIG
    if(inserimentoW_DOCDIG){
      try {
        Long idUtente = null;
        if(this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE) != null){
          int idUtenteInt = ((ProfiloUtente) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId();
          idUtente = new Long(idUtenteInt);
        }
        this.getSqlManager().update("update IMPRDOCG set IDPRG=?, IDDOCDG=?, DATALETTURA=?, SYSCONLET=? where CODGAR=? and CODIMP=? and NORDDOCI=? and NGARA = ? and PROVENI=?",
            new Object[] { "PG",new Long(newIDDOCDIG), new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), idUtente,codiceGara,codiceDitta,new Long(progressivo.longValue() + 1), ngara, new Long(2)});
      } catch (SQLException e) {
        throw new GestoreException("Errore nel valorizzare il riferimento a W_DOCDIG in IMPRDOCG", null, e);
      }
    }

    // Se l'operazione di insert e' andata a buon fine (cioe' nessuna
    // eccezione) inserisco nel request l'attributo RISULTATO valorizzato con
    // "OK", che permettera' alla popup di inserimento documentazione di richiamare
    // il refresh della finestra padre e di chiudere se stessa

    this.getRequest().setAttribute("RISULTATO", "OK");
  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

  	int numeroDocumenti = 0;
    String numDocumenti = this.getRequest().getParameter("numeroDocumenti");
    if(numDocumenti != null && numDocumenti.length() > 0)
      numeroDocumenti =  UtilityNumeri.convertiIntero(numDocumenti).intValue();




    for (int i = 1; i <= numeroDocumenti; i++) {
      DataColumnContainer dataColumnContainerDiRiga = new DataColumnContainer(
      		impl.getColumnsBySuffix("_" + i, false));

      try {
        if(dataColumnContainerDiRiga.isModifiedTable("IMPRDOCG"))
          dataColumnContainerDiRiga.update("IMPRDOCG", sqlManager);
      } catch (SQLException e) {
           throw new GestoreException("Errore nell'aggiornamento dei dati in IMPRDOCG",null, e);
      }
    }


  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

}