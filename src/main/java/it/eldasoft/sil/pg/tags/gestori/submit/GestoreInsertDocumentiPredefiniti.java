/*
 * Created on 26/08/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per inserire la
 * documentazione predefinita
 *
 * @author Marcello Caminiti
 */
public class GestoreInsertDocumentiPredefiniti extends
    AbstractGestoreEntita {

  public GestoreInsertDocumentiPredefiniti() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

  /** Manager per la gestione dei file allegati */
  private FileAllegatoManager fileAllegatoManager;


  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);

    fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager",
        this.getServletContext(), FileAllegatoManager.class);

  }

  @Override
  public String getEntita() {
    return "G1DOCUMOD";
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
  }



  /**
   * Estrae i dati predefiniti per la documentazione predefinita
   *
   * @param codgar
   *        codice della tornata
   * @param ngara
   *        codice della gara
   * @param tiplav
   * @param tipgarg
   * @param importo
   *
   * @return lista di vector di JdbcParametro, con le colonne prelevate da archdocg
   *
   *
   * @throws GestoreException
   */
  private List getListaDocumentazione(String modelloSelezionato) throws GestoreException {

    String select = null;
    modelloSelezionato = modelloSelezionato.substring(modelloSelezionato.indexOf(':') + 1);

    List listaDocumenti = null;
    try {

        select="select g.gruppo, g.busta, ga.reqcap, ga.tipodoc, ga.contestoval, ga.descrizione, ga.obbligatorio, ga.modfirma, ga.idstampa, ga.allmail, ga.idprg, ga.iddocdg" +
          " from g1documod g, g1arcdocumod ga where g.id=? and g.id=ga.iddocumod order by ga.numord,ga.id";
 
        listaDocumenti = this.sqlManager.getListVector(select,new Object[] { modelloSelezionato});

    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'estrazione della tabella dei documenti predefiniti",
          null, e);
    }

    return listaDocumenti;
  }

  /**
   * Data la lista, effettua gli inserimenti delle occorrenze nella tabella DOCUMGARA
   *
   * @param status
   *        status della transazione
   * @param codgar
   *        codice della tornata
   * @param ngara
   *        codice della gara
   * @param valenza
   *        2 -> lotto di gara
   *        0 -> altrimenti
   * @param listaDocumenti
   *        lista di vector di JdbcParametro, con le colonne prelevate da archdocg
   * @throws GestoreException
   */
  private void insertElencoDocumentazionePredefinita(TransactionStatus status,
      String codgar, String ngara,Integer valenza, List listaDocumenti, String isOffertaUnica,Long tipologia, String isProceduraTelematica)
      throws GestoreException {
    String entitaInserimento = null;
    String campoChiaveGara = null;
    String valoreChiaveGara = null;
    String campoChiaveNumerica = null;
    String campoTabellato = null;
    
    entitaInserimento = "DOCUMGARA";
    campoChiaveGara = "CODGAR";
    valoreChiaveGara = codgar;
    campoChiaveNumerica = "NORDDOCG";

    if (isOffertaUnica!=null && "1".equals(isOffertaUnica) )
      ngara=null;

    // si passa all'inserimento dei dati nella tabella DOCUMGARA
    if (listaDocumenti != null && listaDocumenti.size()>0) {
      // si predispongono le informazioni necessarie per l'inserimento
      Vector elencoCampi = new Vector();
      elencoCampi.add(new DataColumn(entitaInserimento + "." + campoChiaveGara,
          new JdbcParametro(JdbcParametro.TIPO_TESTO, valoreChiaveGara)));
      elencoCampi.add(new DataColumn(entitaInserimento
          + "."
          + campoChiaveNumerica, new JdbcParametro(JdbcParametro.TIPO_NUMERICO,
          null)));
      elencoCampi.add(new DataColumn(entitaInserimento + ".NGARA",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
      elencoCampi.add(new DataColumn(entitaInserimento + ".GRUPPO",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
      elencoCampi.add(new DataColumn(entitaInserimento + ".BUSTA",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
      elencoCampi.add(new DataColumn(entitaInserimento + ".REQCAP",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
      elencoCampi.add(new DataColumn(entitaInserimento + ".TIPODOC",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
      elencoCampi.add(new DataColumn(entitaInserimento + ".CONTESTOVAL",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
      elencoCampi.add(new DataColumn(entitaInserimento + ".DESCRIZIONE",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, null)));
      elencoCampi.add(new DataColumn(entitaInserimento + ".IDPRG",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")));
      elencoCampi.add(new DataColumn(entitaInserimento + ".VALENZA",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, valenza)));
      elencoCampi.add(new DataColumn(entitaInserimento + ".OBBLIGATORIO",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, null)));
      elencoCampi.add(new DataColumn(entitaInserimento + ".MODFIRMA",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
      elencoCampi.add(new DataColumn(entitaInserimento + ".IDSTAMPA",
              new JdbcParametro(JdbcParametro.TIPO_TESTO, null)));
      elencoCampi.add(new DataColumn(entitaInserimento + ".ALLMAIL",
          new JdbcParametro(JdbcParametro.TIPO_TESTO, null)));
      elencoCampi.add(new DataColumn(entitaInserimento + ".TIPOLOGIA",
          new JdbcParametro(JdbcParametro.TIPO_NUMERICO,tipologia)));


      DataColumnContainer container = new DataColumnContainer(elencoCampi);
      // si predispone il gestore per l'aggiornamento dell'entità
      DefaultGestoreEntitaChiaveNumerica gestore = new DefaultGestoreEntitaChiaveNumerica(
          entitaInserimento, campoChiaveNumerica,
          new String[] { campoChiaveGara }, this.getRequest());
      
      boolean documentiGruppo3= false;
      for (int i = 0; i < listaDocumenti.size(); i++) {
        Long gruppo = (Long) SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 0).getValue();
        String allmail = (String) SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 9).getValue();
        if(!"true".equals(isProceduraTelematica) && (gruppo != null && gruppo.intValue() == 6)){
          allmail = "2";
        }
        // si legge il singolo tipo pubblicazione e si aggiorna il campo
        // corrispondente nel container, quindi si esegue l'inserimento
        container.getColumn(entitaInserimento + "." + campoChiaveNumerica).setValue(
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null));
        container.getColumn(entitaInserimento + ".GRUPPO").setValue(
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO,SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 0).getValue()));
        container.getColumn(entitaInserimento + ".BUSTA").setValue(
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO,SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 1).getValue()));
        container.getColumn(entitaInserimento + ".REQCAP").setValue(
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO,SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 2).getValue()));
        container.getColumn(entitaInserimento + ".TIPODOC").setValue(
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO,SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 3).getValue()));
        container.getColumn(entitaInserimento + ".CONTESTOVAL").setValue(
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO,SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 4).getValue()));
        container.getColumn(entitaInserimento + ".DESCRIZIONE").setValue(
            new JdbcParametro(JdbcParametro.TIPO_TESTO,SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 5).getValue()));
        container.getColumn(entitaInserimento + ".OBBLIGATORIO").setValue(
            new JdbcParametro(JdbcParametro.TIPO_TESTO,SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 6).getValue()));
        container.getColumn(entitaInserimento + ".MODFIRMA").setValue(
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO,SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 7).getValue()));
        container.getColumn(entitaInserimento + ".IDSTAMPA").setValue(
                new JdbcParametro(JdbcParametro.TIPO_TESTO,SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 8).getValue()));
        container.getColumn(entitaInserimento + ".ALLMAIL").setValue(
            new JdbcParametro(JdbcParametro.TIPO_TESTO,allmail));

        gestore.inserisci(status, container);

        Long norddocg = (Long) container.getColumn(entitaInserimento + ".NORDDOCG").getValue().getValue();
        //verifica esistenza allegato ed inserimento dello stesso:
        String idprg = (String) SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 10).getValue();
        Long iddocdg = (Long) SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 11).getValue();
        if("PG".equals(idprg) && iddocdg != null){
          //copio l'allegato e metto iriferimenti
          long numOccorrenzeW_DOCDIG = this.geneManager.countOccorrenze(
              "W_DOCDIG", "IDPRG = ? and IDDOCDIG = ?", new Object[] { idprg, iddocdg });
          if (numOccorrenzeW_DOCDIG > 0) {
            String select = "select IDPRG,IDDOCDIG,DIGENT,DIGKEY1,DIGKEY2,DIGTIPDOC,DIGNOMDOC,DIGDESDOC from W_DOCDIG where IDPRG = ? and IDDOCDIG = ?";
            try {
              HashMap occorrenzaW_DOCDIGDaCopiare = this.geneManager.getSql().getHashMap( select, new Object[] { idprg, iddocdg });
              if (occorrenzaW_DOCDIGDaCopiare != null && occorrenzaW_DOCDIGDaCopiare.size() > 0) {
                DataColumnContainer campiDaCopiareW_DOCDIG = new DataColumnContainer( this.geneManager.getSql(), "W_DOCDIG", select, new Object[] { idprg, iddocdg });
                campiDaCopiareW_DOCDIG.setValoriFromMap( occorrenzaW_DOCDIGDaCopiare, true);
                campiDaCopiareW_DOCDIG.getColumn("IDPRG").setChiave(true);
                campiDaCopiareW_DOCDIG.getColumn("IDDOCDIG").setChiave(true);
                // Si deve calcolare il valore di IDDOCDIG
                Long maxIDDOCDIG = (Long) this.geneManager.getSql().getObject( "select max(IDDOCDIG) from W_DOCDIG where IDPRG= ?", new Object[] { idprg });
                long newIDDOCDIG = 1;
                if (maxIDDOCDIG != null && maxIDDOCDIG.longValue() > 0){
                  newIDDOCDIG = maxIDDOCDIG.longValue() + 1;
                }
                campiDaCopiareW_DOCDIG.setValue("IDDOCDIG", new Long( newIDDOCDIG));
                campiDaCopiareW_DOCDIG.setValue("DIGENT", entitaInserimento);
                campiDaCopiareW_DOCDIG.setValue("DIGKEY1", codgar);
                campiDaCopiareW_DOCDIG.setValue("DIGKEY2", norddocg);

                BlobFile fileAllegato = null;
                fileAllegato = fileAllegatoManager.getFileAllegato(idprg, iddocdg);
                ByteArrayOutputStream baos = null;
                if (fileAllegato != null && fileAllegato.getStream() != null) {
                  baos = new ByteArrayOutputStream();
                  baos.write(fileAllegato.getStream());
                }
                campiDaCopiareW_DOCDIG.addColumn("W_DOCDIG.DIGOGG", JdbcParametro.TIPO_BINARIO, baos);
                // Inserimento del nuovo record su w_docdig
                campiDaCopiareW_DOCDIG.insert("W_DOCDIG", this.geneManager.getSql());
                // Aggiornamento dei campi IDPRG e IDDOCDG di documgara
                this.sqlManager.update( "update DOCUMGARA set IDPRG=?,IDDOCDG = ? where CODGAR=? and NORDDOCG=?",
                    new Object[] { idprg, new Long(newIDDOCDIG),  codgar, norddocg });
              }

            } catch (IOException e) {
                throw new GestoreException(
                    "Errore nella lettura del campo BLOB DIGOGG della W_DOCDIG", "insertElencoDocumentazionePredefinita", e);
            } catch (SQLException e) {
                throw new GestoreException(
                    "Errore nella copia dell'allegato per entita' DOCUMGARA", "insertElencoDocumentazionePredefinita", e);
            }

          }

        }

        gruppo = SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 0).longValue();
        if(gruppo!=null && gruppo.longValue()==3)
          documentiGruppo3=true;

      }
      if(documentiGruppo3){
        //Aggiornamento IMPRDOCG
        PgManager pgManager = (PgManager) UtilitySpring.getBean(
            "pgManager", this.getServletContext(), PgManager.class);

        pgManager.updateImprdocgDaDocumgara(codgar, ngara);
      }
    }
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
	    // lettura dei parametri di input
	  	String  codgar = UtilityStruts.getParametroString(this.getRequest(),"codgar");
		String modelloSelezionato = this.getRequest().getParameter("modello");
		modelloSelezionato = modelloSelezionato.substring(modelloSelezionato.indexOf(':') + 1);	
		String  ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
		String  tiplav = UtilityStruts.getParametroString(this.getRequest(),"tiplav");
		String  tipgarg = UtilityStruts.getParametroString(this.getRequest(),"tipgarg");
		String  lottoDiGara = UtilityStruts.getParametroString(this.getRequest(),"lottoDiGara");
	    Integer valenza = new Integer(0);
	    String  isOffertaUnica = UtilityStruts.getParametroString(this.getRequest(),"isOffertaUnica");
		Long critlic=null;
		String critlicStr=UtilityStruts.getParametroString(this.getRequest(),"critlic");
		if(!"".equals(critlicStr) && critlicStr!=null) {
			critlic = Long.valueOf(critlicStr);
		}
		String  faseInvito = UtilityStruts.getParametroString(this.getRequest(),"faseInvito");
		String  isProceduraTelematica = UtilityStruts.getParametroString(this.getRequest(),"isProceduraTelematica");
		Long tipologia = null;
		String tipologiaStr=UtilityStruts.getParametroString(this.getRequest(),"tipologia");
		if(!"".equals(tipologiaStr) && tipologiaStr!=null) {
			tipologia = Long.valueOf(tipologiaStr);
		}
		Long busta=null;
		String bustaStr=UtilityStruts.getParametroString(this.getRequest(),"busta");
		if(!"".equals(bustaStr) && bustaStr!=null) {
			busta = Long.valueOf(bustaStr);
		}
		Long gruppo=null;
		String gruppoStr=UtilityStruts.getParametroString(this.getRequest(),"gruppo");
		if(!"".equals(gruppoStr) && gruppoStr!=null) {
			gruppo = Long.valueOf(gruppoStr);
		}
		
	    if(lottoDiGara!=null && "1".equals(lottoDiGara))
	      valenza = new Integer(2);

	    // estrae l'elenco dei documenti d'archivio
	    List listaDocumenti = this.getListaDocumentazione(modelloSelezionato);
	    
	    // verifico presenza documenti M-DGUE
	    if(!verificaMDGUEMultipli(codgar,modelloSelezionato,busta,gruppo)) {
	    throw new GestoreException(
                "Errore nell'inserimento dei documenti predefiniti causa M-DGUE multipli", "presenzaDocumentiMDGUEMultipli", null);
	    };
	    
	    // esegue gli inserimenti
	    this.insertElencoDocumentazionePredefinita(status, codgar, ngara,valenza,listaDocumenti,isOffertaUnica,tipologia,isProceduraTelematica);

	    ////////////////////////////////////////////////////////////////////////////////
	    //Ricalcolo NUMORD.DOCUMGARA    
	    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
	        this.getServletContext(), PgManagerEst1.class);
	    gruppo= null;
	    Map<Integer, Long> gruppiAggiornati = new HashMap<Integer, Long>();
	    if (listaDocumenti != null && listaDocumenti.size() > 0) {
	      for (int i = 0; i < listaDocumenti.size(); i++) {
	        gruppo = SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 0).longValue();
	        //se un gruppo è già stato aggiornato, evito il ricalcolo
	        if (!gruppiAggiornati.containsValue(gruppo)) {
	          pgManagerEst1.ricalcNumordDocGara(codgar, gruppo);
	          gruppiAggiornati.put(i,gruppo);
	        }
	      }
	    }    
	    ////////////////////////////////////////////////////////////////////////////////

	    // setta l'operazione a completata, in modo da scatenare il reload della
	    // pagina principale
	    this.getRequest().setAttribute("documentiInseriti", "1");
  }
  
  /**
   * Dato codgar e modelloSelezionato verifica che sia presente al più un documento M-DGUE
   *
   * @param codgar
   *        codgar della gara
   * @param modelloSelezionato
   *        modello predefinito selezionato
   * @throws GestoreException
   */
  private boolean verificaMDGUEMultipli(String codgar,String modelloSelezionato,Long busta,Long gruppo) throws GestoreException {
	  
	  String selectCountMDGUEDocumgara = "select count(*) from documgara where codgar=? and IDSTAMPA = 'DGUE' and gruppo=? ";
	  String selectCountMDGUEG1arcdocumod = "select count(*) from g1arcdocumod where iddocumod=? and IDSTAMPA = 'DGUE'";
	  if(busta!=null){
		  selectCountMDGUEDocumgara = selectCountMDGUEDocumgara + "and busta=? ";
	  }
	  Long countOccMDGUEDocumgara;
	  Long countOcccountMDGUEG1arcdocumod;
	  Long totale=new Long(0);
	  boolean res=true;
	  
	  try {
		  if(busta!=null) {
			  countOccMDGUEDocumgara = (Long)this.sqlManager.getObject(selectCountMDGUEDocumgara,new Object[] { codgar,gruppo,busta});
		  }
		  else {
			  countOccMDGUEDocumgara = (Long)this.sqlManager.getObject(selectCountMDGUEDocumgara,new Object[] { codgar,gruppo});
		  }
		  countOcccountMDGUEG1arcdocumod = (Long)this.sqlManager.getObject(selectCountMDGUEG1arcdocumod,new Object[] { modelloSelezionato});
	  }
	  catch (SQLException e) {
          throw new GestoreException(
              "Errore nel conteggio dei documenti M-DGUE in DOCUMGARA e G1ARCDOCUMOD", "insertElencoDocumentazionePredefinita", e);
      }
	  
	  totale=countOccMDGUEDocumgara+countOcccountMDGUEG1arcdocumod;
	  if(totale>1) {
		  res=false;
	  }
	  
	  return res;
  }

}
