/*
 * Created on 20/12/10
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.example.getDettaglioOffertaFornitore.RetXmlGetDettaglioOffertaFornitoreDocument.RetXmlGetDettaglioOffertaFornitore.RigheOffertaFornitore;
import org.example.getElencoFornitoriResults.DettaglioFornitore;
import org.example.getRigheCarrelloResults.RetXmlGetRigheCarrelloDocument.RetXmlGetRigheCarrello.RigheCarrello;
import org.example.getRigheCarrelloResults.RetXmlGetRigheCarrelloDocument.RetXmlGetRigheCarrello.RigheCarrello.RigaCarrello;

import pc_nicola.aur.WebServices.WsAURSoapProxy;

/**
 * Classe di gestione delle funzionalita' inerenti  l'esportazione e
 * l'importazione in formato Excel per OEPV
 *
 * @author Stefano.Cestaro
 */
public class AurManager {

	/** Logger */
	static Logger logger = Logger.getLogger(AurManager.class);

	/** Manager SQL per le operazioni su database */
	private SqlManager sqlManager;

	private PgManager pgManager;

	/** Manager con funzionalita' generali */
	private GeneManager               geneManager;

	/** Manager con le funzionalità di import\Export offerta prezzi */
	private ImportExportOffertaPrezziManager importExportOffertaPrezziManager;


	/**
	 * Set SqlManager
	 *
	 * @param sqlManager
	 */
	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

    public void setPgManager(PgManager pgManager) {
      this.pgManager = pgManager;
    }

    /**
     * @param geneManager
     *        geneManager da settare internamente alla classe.
     */
    public void setGeneManager(GeneManager geneManager) {
      this.geneManager = geneManager;
    }

    /**
     * @param geneManager
     *        geneManager da settare internamente alla classe.
     */
    public void setImportExportOffertaPrezziManager(ImportExportOffertaPrezziManager importExportOffertaPrezziManager) {
      this.importExportOffertaPrezziManager = importExportOffertaPrezziManager;
    }

    /**
     * Viene effettuato l'inserimento del fornitore
     *
     * @param fornitore
     *           contiene i dati del fornitore
     * @param ngara
     * @param garaLottiConOffertaUnica
     *           "1" gara con offerta unica, "2" altrimenti
     * @param numeroFaseAttiva
     *           Fase attiva
     * @throws SQLException
     */
	public void insertConcorrenti(DettaglioFornitore fornitore, String ngara,String garaLottiConOffertaUnica, Long numeroFaseAttiva) throws SQLException{
	  String codiceFornitore = fornitore.getCodiceFornitore();
	  String ragsoc = fornitore.getRagSoc();
	  String nomimp=ragsoc;
      if(nomimp.length()>61)
        nomimp = nomimp.substring(0, 60);

      codiceFornitore = "A" + codiceFornitore;
	  if(codiceFornitore.length()>10)
	    codiceFornitore=codiceFornitore.substring(0, 10);

	  //Il fornitore esiste in Alice?
      String select="select count(codimp) from impr where codimp = ? ";
      boolean fornitorePresente = false;
      try {
        Long occorrenze = (Long)sqlManager.getObject(select, new Object[]{codiceFornitore});
        if (occorrenze != null && occorrenze.longValue()>0){
          fornitorePresente = true;
        }

        //Gestione IMPR
        String indirizzo = fornitore.getIndirizzo();
        String tipoFornitore = fornitore.getTipoFornitore();
        String dataCCIAA = fornitore.getDataCCIAA();
        String numeroCCIA = fornitore.getNumeroCCIA();
        //String nomeCCIA = fornitore.getNomeCCIAA();
        String CAP = fornitore.getCAP();
        String comune = fornitore.getComune();
        String provincia = fornitore.getProvincia();
        String stato = fornitore.getStato();
        String telefono = fornitore.getTelefono();
        String fax = fornitore.getFax();
        String email = fornitore.getEmail();

        //Per adesso si salta la gestione delle RTI, quindi mandataria e mandante

        if(provincia.length()>2)
          provincia = provincia.substring(0, 2);

        //Tipo impresa nel nostro db è un campo tabellato, quindi mi aspetto un
        //numero, invece arriva una descrizione!!!!
        Long tipimp=null;
        if(tipoFornitore!=null && "Impresa Singola".equals(tipoFornitore)){
          tipimp= new Long(1);
        }

        String codiceIstat = null;
        if (provincia!= null && comune!=null){
        //codiceIstat = this.getCodiceISTAT(comune.toUpperCase(), provincia.toUpperCase());
          codiceIstat = pgManager.getCodiceISTAT(comune.toUpperCase(), provincia.toUpperCase());
        }



        select="select tab1tip from tab1 where tab1cod='Ag010' and upper(tab1desc) = ?";

        Long nazimp = (Long)sqlManager.getObject(select, new Object[]{stato.toUpperCase()});


        Date campoData=null;
        if(dataCCIAA!=null && !"".equals(dataCCIAA))
          campoData = UtilityDate.convertiData(dataCCIAA, UtilityDate.FORMATO_GG_MM_AAAA);

        if(fornitorePresente){
          select="update IMPR set NOMEST=?, NOMIMP=?,TIPIMP=?,INDIMP=?,CAPIMP=?,LOCIMP=?,";
          select+="PROIMP=?,NAZIMP=?,EMAIIP=?,TELIMP=?,FAXIMP=?,NCCIAA=?,DCCIAA=?,DAESTERN=?,";
          select+="CODCIT=? where CODIMP=?";
          sqlManager.update(select,new Object[]{ragsoc,nomimp,tipimp,indirizzo,CAP,
              comune,provincia,nazimp,email,telefono,fax,numeroCCIA,campoData,"2",codiceIstat,codiceFornitore});
        }else{
          select="insert into IMPR (CODIMP,NOMEST,NOMIMP,TIPIMP,INDIMP,CAPIMP,LOCIMP,PROIMP,";
          select+="NAZIMP,EMAIIP,TELIMP,FAXIMP,NCCIAA,DCCIAA,DAESTERN,CODCIT) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
          sqlManager.update(select,new Object[]{codiceFornitore,ragsoc,nomimp,tipimp,indirizzo,CAP,
              comune,provincia,nazimp,email,telefono,fax,numeroCCIA,campoData,"2",codiceIstat});
        }



      } catch (SQLException e) {
        logger.error("Errore nell'inserimento nella tabella IMPR");
        if (logger.isDebugEnabled())
          logger.debug(e.getMessage());
        throw e;
      }


      //Gestione DITG
      String codiceTornata = null;
      Long nProgressivoDITG, nProgressivoEDIT = null;
      Double importoAppalto = null;

      boolean esisteOccorrenzaInEDIT = false;
      boolean isProceduraAggiudicazioneAperta = false;
      // Flag per indicare che:
      // se valorizzato a true: l'inserimento della ditta avviene determinando
      // il valore del DITG.NPROGG come il max del campo stesso (anche se si
      // tratta di una gara a lotti) se valorizzato a false: l'inserimento della
      // ditta avviene usando il campo EDIT.NPROGT se la ditta e' gia' presente
      // nella tabella EDIT, oppure determinando il max fra il campo EDIT.NPROGT
      // e DITG.NPROGG relativamente all'intera tornata (e non nel lotto di gara
      // in analisi (per una gara divisa a lotti))
      boolean modalitaNPROGGsuLotti = pgManager.isModalitaNPROGGsuLotto();

      try {
        List datiGara = sqlManager.getListVector(
                "select CODGAR1, TIPGARG, IMPAPP from GARE where NGARA = ? ",
                new Object[] { ngara });
        Vector dati = (Vector) datiGara.get(0);

        codiceTornata = ((JdbcParametro) dati.get(0)).getStringValue();
        if (((JdbcParametro) dati.get(1)).getValue() != null)
            isProceduraAggiudicazioneAperta = ((Long) ((JdbcParametro)
                    dati.get(1)).getValue()).intValue() == 1;
        importoAppalto = (Double) ((JdbcParametro) dati.get(2)).getValue();


        // Determino il valore del progressivo dal campo EDIT.NPROGT per la
        // tornata in analisi e la ditta che si vuole inserire, se presente.
        nProgressivoEDIT = (Long) sqlManager.getObject(
                "select NPROGT from EDIT " + "where CODGAR4 = ? "
                        + "and CODIME = ? ",
                new Object[] { codiceTornata, codiceFornitore });

        if (nProgressivoEDIT != null && nProgressivoEDIT.intValue() > 0)
            esisteOccorrenzaInEDIT = true;
        else {
            // Se non esiste alcuna occorrenza nella EDIT relativa alla gara
            // in analisi, allora determino valore massimo del campo EDIT.NPROGT
            nProgressivoEDIT = (Long) sqlManager.getObject(
                    "select max(NPROGT) from EDIT where CODGAR4 = ? ",
                    new Object[] { codiceTornata });
            // Se la EDIT non ha occorrenze per la gara in analisi,
            // inizializzo la variabile nProgressivoEDIT a 0
            if (nProgressivoEDIT == null)
                nProgressivoEDIT = new Long(0);
        }

        if (modalitaNPROGGsuLotti)
            nProgressivoDITG = (Long) sqlManager.getObject(
                    "select max(NPROGG) from DITG " + "where CODGAR5 = ? "
                            + "and NGARA5 = ? ",
                    new Object[] { codiceTornata, ngara });
        else
            nProgressivoDITG = (Long) sqlManager.getObject(
                    "select max(NPROGG) from DITG " + "where CODGAR5 = ? ",
                    new Object[] { codiceTornata });

        if (nProgressivoDITG == null)
            nProgressivoDITG = new Long(0);
        // }
      } catch (SQLException e) {
          logger.error("Errore nel determinare il progressivo "
              + "della ditta (codice ditta: " + codiceFornitore   + ") nella gara " +
              ngara);
          throw e;

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
          select="insert into EDIT (CODGAR4,CODIME,NOMIME,DOCOK,DATOK,DITINV,NPROGT) ";
          select+=" values(?,?,?,?,?,?,?)";
          sqlManager.update(select,new Object[]{codiceTornata,codiceFornitore,nomimp,"1","1","1",nProgressivo});
        }catch (SQLException s) {
          logger.error("Errore nell'inserimento in EDIT ");
          if (logger.isDebugEnabled())
            logger.debug(s.getMessage());
          throw s;
        }
      }

      try {
        //Per le ditte inserite da AUR il campo DITG.ACQUISIZIONE = 12
        select="select count(ngara5) from ditg where ngara5=? and codgar5=? and dittao=?";
        Long count = (Long) sqlManager.getObject(
                select,new Object[] { ngara,codiceTornata,codiceFornitore });
        if (count.longValue() == 0) {
            select="insert into DITG (NGARA5,DITTAO,CODGAR5,NOMIMO,NPROGG,CATIMOK,INVGAR,NUMORDPL,INVOFF,IMPAPPD,ACQUISIZIONE) ";
            select+="values (?,?,?,?,?,?,?,?,?,?,?)";
            String invoff=null;
            Double impAppalto=null;
            if(isProceduraAggiudicazioneAperta){
              invoff="1";
              impAppalto=importoAppalto;
            }

            sqlManager.update(select,new Object[]{ngara,codiceFornitore,codiceTornata,nomimp,
                numProgDITG,"1","1",numProgDITG,invoff,impAppalto,new Long(12)});
        } else {
          logger.error("La ditta selezionata risulta già inserita in gara");
          throw new SQLException();
        }
      } catch (SQLException e) {
        logger.error("Errore nell'inserimento in DITG");
        if (logger.isDebugEnabled())
          logger.debug(e.getMessage());
        throw e;

      }


      //Quando si inserisce una ditta in gara si deve in automatico popolare IMPRDOCG
      //a partire dalle occorrenze di DOCUMGARA, impostando SITUAZDOCI a 2 e PROVENI a 1
      try {
        pgManager.inserimentoDocumentazioneDitta(codiceTornata, ngara, codiceFornitore);
      } catch (GestoreException e1) {
        logger.error("Errore nell'inserimento della documentazione di gara");
        throw new SQLException();
      }


      if("1".equals(garaLottiConOffertaUnica)){
        try{
          // Estrazione di NGARA e IMPAPP dei lotti della gara con offerta unica,
          // Per inserire la ditta in ciascun lotto
          List listaLotti = this.sqlManager.getListVector(
                  "select NGARA, IMPAPP from GARE " +
                   "where CODGAR1 = ? and NGARA <> CODGAR1 and GENERE is null",
                   new Object[]{ngara});

          if(listaLotti != null && listaLotti.size() > 0){
            select="insert into DITG (CODGAR5,NGARA5,DITTAO,NOMIMO,NPROGG,IMPAPPD,NUMORDPL,ACQUISIZIONE) " +
                  "values(?,?,?,?,?,?,?,?)";
            for(int i=0; i < listaLotti.size(); i++){
              Vector lotto = (Vector) listaLotti.get(i);
              String tmpCodiceLotto = (String)((JdbcParametro) lotto.get(0)).getValue();
              Double tmpImpApp = (Double)((JdbcParametro) lotto.get(1)).getValue();
              sqlManager.update(select,new Object[]{codiceTornata,tmpCodiceLotto,codiceFornitore,
                  nomimp,numProgDITG,tmpImpApp,numProgDITG, new Long(12)});
            }
          }

        }catch (SQLException e) {
          logger.error("Errore nell'inserimento della ditta nei " +
                    "lotti della gara ");
          if (logger.isDebugEnabled())
            logger.debug(e.getMessage());
          throw e;

        }

      }

      // Aggiornamento GARE.FASGAR e GARE.STEPGAR, solo se non si è già passati
      // alle fasi di gara, ovvero solo se FASGAR.GARE < 2
      try {
        pgManager.aggiornaFaseGara(numeroFaseAttiva, ngara, true);
      } catch (GestoreException e) {
        logger.error("Errore nell'aggiornamento della fase di gara");
        throw new SQLException();
      }

	}


	  /**
	   * Vengono adoperate le righe del carrello per popolare le righe di GCAP e
	   * GCAP_SAN. Inoltre nel caso di gara ad offerta unica e con codifica automatica
	   * attiva vengono ricreati i lotti
	   *
	   * @param righeCarrello
	   *           righe del carrello
	   * @param ngara
	   * @param garaLottiConOffertaUnica
	   *           "1" gara con offerta unica, "2" altrimenti
	   * @param mappaDatiWebService
	   *           hashmap contenente i parametri per la chiamata del webservice AUR_SetGaraCarrello
	   * @throws SQLException
	   */
	  public void insertCarrello(RigheCarrello righeCarrello, String ngara,String garaLottiConOffertaUnica, HashMap mappaDatiWebService) throws SQLException{
	    if (logger.isDebugEnabled())
	      logger.debug("insertCarrello: inizio metodo");

	    boolean isCodificaAutomaticaAttiva = this.geneManager.isCodificaAutomatica(
	        "GARE", "NGARA");

	    RigaCarrello[] rigaCarrelloArray = righeCarrello.getRigaCarrelloArray();

	    //Per potere lanciare la procedura di import ci deve essere almeno una
	    //occorrenza nel carrello con il campo "VoceLotto" valorizzato
	    //Nel caso di gare con Offerta unica controllo anche "Lotto"
	    //long numOccorrenzeLotto=0;
	    long numOccorrenzeVoceLotto=0;

	    if (rigaCarrelloArray!= null && rigaCarrelloArray.length>0){
	         for(int i=0;i<rigaCarrelloArray.length;i++){
	            RigaCarrello rigaCarrello = righeCarrello.getRigaCarrelloArray(i);
	            //String codiceLavorazFornitura = rigaCarrello.getVoceLotto();
	            String codiceLavorazFornitura = rigaCarrello.getCodiceAUR();
	            //String codiga = rigaCarrello.getLotto();
	            if (codiceLavorazFornitura!= null && !"".equals(codiceLavorazFornitura))
	              numOccorrenzeVoceLotto++;

	            /*
	            if (codiga!= null && !"".equals(codiga))
	              numOccorrenzeLotto++;
	            */
	         }
	    }


	    if (numOccorrenzeVoceLotto==0){
	        logger.error("Non è possibile procedere con l'import poichè nel carrello"+
	            " alcune righe non hanno il codice AUR valorizzato");
	        throw new SQLException();
	    }

	    //Si salta per adesso il controllo sulla valorizzazione del campo Lotto
	    // metto sempre 1
	    /*
	    if ("1".equals(garaLottiConOffertaUnica) && numOccorrenzeLotto == 0) {
	        logger.error("Non è possibile procedere con l'import poichè nel carrello"+
              " tutte le righe hanno il Lotto non valorizzato");
	        throw new SQLException();
	    }
	    */

	    String codiceGara=null;
	    Long tipoFornitura = null;
	    String codicesaAUR = null;

	    Vector datiTORN = this.sqlManager.getVector(
	        "select torn.tipforn, torn.codgar,torn.cenint from torn, gare where torn.codgar = gare.codgar1 and gare.ngara = ?",
	        new Object[] { ngara });
	    if (datiTORN != null && datiTORN.size() > 0) {
	      if (SqlManager.getValueFromVectorParam(datiTORN, 0) != null) {

	        try {
              tipoFornitura = sqlManager.getValueFromVectorParam(datiTORN, 0).longValue();
            } catch (GestoreException e) {
              logger.error("Errore nella determinazione del tipo forniture");
              throw new SQLException();
            }
	      }
	      try{
	        codiceGara = sqlManager.getValueFromVectorParam(datiTORN, 1).stringValue();
	      }catch (GestoreException e) {
	        logger.error("Errore nella determinazione del codice gara");
            throw new SQLException();
	      }

	      try{
	        codicesaAUR = sqlManager.getValueFromVectorParam(datiTORN, 2).stringValue();
          }catch (GestoreException e) {
            logger.error("Errore nella determinazione della stazione appaltante");
            throw new SQLException();
          }

	    }

	    if (tipoFornitura == null) tipoFornitura = new Long(3);

	    if ("1".equals(garaLottiConOffertaUnica)) {
	      try {
    	      List listaLotti = this.sqlManager.getListVector(
    	            "select NGARA from GARE where GARE.CODGAR1 = ? and "
    	                + "(GARE.GENERE is null or GARE.GENERE <> 3)",
    	            new Object[] { ngara });
    	      if (isCodificaAutomaticaAttiva) {
    	          // Tenuto conto che, nel caso di gara ad offerta unica, con codifica
    	          // automatica attiva, la funzione di import dell'offerta prezzi lato
    	          // gara e' attiva solo se la gara non ha nessuna ditta associata
    	          // (entita' DITG), si puo' avviare direttamente la cancellazione di
    	          // lotti della gara
    	          // Cancellazione dei lotti della gara ad offerta unica, a meno della
    	          // occorrenza complementare in GARE e delle occorrenze che, se pur
    	          // presenti in entita' figlie di GARE, sono gestite come entita'
    	          // figlie di TORN solamente per le gare a lotti con offerta unica
    	          if (listaLotti != null && listaLotti.size() > 0) {
    	            for (int yi = 0; yi < listaLotti.size(); yi++) {
    	              Vector tmpVect = (Vector) listaLotti.get(yi);
    	              String numeroLotto = ((JdbcParametro) tmpVect.get(0)).getStringValue();
    	              // Cancellazione entita' figlie del lotto
    	              this.pgManager.deleteGARE(numeroLotto);
    	              // Cancellazione del lotto vero e proprio
    	              this.geneManager.deleteTabelle(new String[] { "GARE" },
    	                  "NGARA = ? and (GARE.GENERE is null or GARE.GENERE <> 3)",
    	                  new Object[] { numeroLotto });
    	              if (logger.isDebugEnabled()) {
    	                logger.debug("Cancellazione del lotto '" + numeroLotto
    	                    + "' della gara a lotti ad offerta unica '" + ngara + "'");
    	              }
    	            }
    	          }
    	      } else {
    	          if (listaLotti == null || listaLotti.size() == 0) {
    	            if (logger.isDebugEnabled())
    	              logger.debug("Errore per avvio dell'operazione di import su una "
    	                  + "gara ad offerta unica (con codifica automatica disattivata) "
    	                  + "priva di lotti definiti");
    	              throw new SQLException();
    	          } else {
    	            sqlManager.update("delete from GCAP where GCAP.NGARA in ("
    	                + "select NGARA from GARE where GARE.CODGAR1 = ? and "
    	                + "(GARE.GENERE is null or GARE.GENERE <> 3))",
    	                new Object[] { ngara });
    	            sqlManager.update("delete from GCAP_EST where NGARA in ("
    	                + "select NGARA from GARE where GARE.CODGAR1 = ? and "
    	                + "(GARE.GENERE is null or GARE.GENERE <> 3))",
    	                new Object[] { ngara });
    	            sqlManager.update("delete from GCAP_SAN where NGARA in ("
    	                + "select NGARA from GARE where GARE.CODGAR1 = ? and "
    	                + "(GARE.GENERE is null or GARE.GENERE <> 3))",
    	                new Object[] { ngara });
    	            sqlManager.update("delete from DPRE where DPRE.NGARA in ("
    	                + "select NGARA from GARE where GARE.CODGAR1 = ? and "
    	                + "(GARE.GENERE is null or GARE.GENERE <> 3))",
    	                new Object[] { ngara });
    	            sqlManager.update("delete from DPRE_SAN where DPRE_SAN.NGARA in ("
    	                + "select NGARA from GARE where GARE.CODGAR1 = ? and "
    	                + "(GARE.GENERE is null or GARE.GENERE <> 3))",
    	                new Object[] { ngara });
    	          }
    	      }
	      }catch (SQLException g) {
            logger.error("Errore nella cancellazione delle occorrenze presenti " +
                  "nelle tabelle GCAP, GCAP_EST, GCAP_SAN, DPRE e DPRE_SAN " +
                  "relative alla gara a lotti con offerta unica '" + ngara + "'",
                  g);
            throw g;
	      }catch (GestoreException g) {
            logger.error("Errore nella cancellazione delle occorrenze presenti " +
                  "nelle tabelle GCAP, GCAP_EST, GCAP_SAN, DPRE e DPRE_SAN " +
                  "relative alla gara a lotti con offerta unica '" + ngara + "'",
                  g);
            throw new SQLException();
          }
	    }else {
	      try {
	        sqlManager.update("delete from GCAP where NGARA = ?",
	            new Object[] { ngara });
	        sqlManager.update("delete from GCAP_SAN where NGARA = ?",
	            new Object[] { ngara });
	        sqlManager.update("delete from GCAP_EST where NGARA = ?",
	            new Object[] { ngara });
	        sqlManager.update("delete from DPRE where NGARA = ?",
	            new Object[] { ngara });
	        sqlManager.update("delete from DPRE_SAN where NGARA = ?",
	            new Object[] { ngara });
	      } catch (SQLException g) {
	        logger.error("Errore nella cancellazione delle occorrenze presenti "
	            + "nelle tabelle GCAP, GCAP_EST, GCAP_SAN, DPRE, DPRE_SAN "
	            + "relative alla gara '" + ngara + "'", g);
	        throw g;
	      }
	    }

	    HashMap mappaCodiciLotti = new HashMap();
	    HashMap mappaDescrizioniLotti = new HashMap();
	    long numRecordImportati=0;

	    //Si deve ciclare fra le righe del carrello, se per una riga non è valorizzato il campo "VoceLotto" salto
	    //l'import dei dati per quella riga
	    //Nel caso di gare con offerta unica salto quando non è valorizzato "Lotto"
	    if (rigaCarrelloArray!= null && rigaCarrelloArray.length>0){
	      for(int i=0;i<rigaCarrelloArray.length;i++){
	        RigaCarrello rigaCarrello = righeCarrello.getRigaCarrelloArray(i);
	        //String codiceLavorazFornitura = rigaCarrello.getVoceLotto();
	        String codiceLavorazFornitura = rigaCarrello.getCodiceAUR();
	        String codiga = rigaCarrello.getLotto();

	        if(codiga==null || "".equals(codiga) || !this.isNumberCheck(codiga))
	          codiga = "1";
	        /*
	        if (("1".equals(garaLottiConOffertaUnica) && (codiga== null || "".equals(codiga))) ||
	            (codiceLavorazFornitura == null || "".equals(codiceLavorazFornitura))){
	          logger.error("Non e' stata inserita la riga " + (i + 1) + " per mancanza del valore del Lotto o della Voce lotto");
	            continue;
	        }
	        */

	        String descrizione =   rigaCarrello.getDescrCodiceAUR();
	        String codiceLotto = "";
	        //String codiga = null;
	        if ("1".equals(garaLottiConOffertaUnica)) {
	          if (isCodificaAutomaticaAttiva) {
	            //codiga = rigaCarrello.getLotto();

	            HashMap mappa = null;
                try {
                  mappa = this.pgManager.calcolaCodificaAutomatica("GARE",
                      Boolean.FALSE, ngara, new Long(codiga));
                } catch (NumberFormatException e) {
                  logger.error("Errore nella codifica automatica",e);
                  throw new SQLException();
                } catch (GestoreException e) {
                  logger.error("Errore nella codifica automatica",e);
                  throw new SQLException();
                }
	            codiceLotto = (String) mappa.get("numeroGara");

	            //Metto in una hashmap codiceLotto(NGARA) per poi ricreare i Lotti
	            if (codiceLotto != null && codiceLotto.length() > 0) {
	              if (!mappaCodiciLotti.containsKey(codiga))
	                  mappaCodiciLotti.put(codiga, codiceLotto);
	            }

	          } else {
	            codiceLotto = codiga;
	          }
	        }else {
	          codiceLotto = ngara;
	        }


	        //Il valore di Norvoc viene impostato con quello di Contaf
	        long newContaf,newNorvoc;
	        Long clasi1=new Long(3);
	        String solsic=null;
	        String sogrib=null;

	        //Nel caso di gara ad offerta unica si determina il valore di contaf
	        //come max(contaf)
	        if ("1".equals(garaLottiConOffertaUnica)){
              Long tmpMaxContaf = (Long)this.sqlManager.getObject("select max(CONTAF) from GCAP where NGARA = ?", new Object[]{codiceLotto});
              if (tmpMaxContaf == null)
                tmpMaxContaf = new Long(0);
              newContaf = tmpMaxContaf.longValue() + 1;
              newNorvoc = tmpMaxContaf.longValue() + 1;
              if(! new Long(3).equals(tipoFornitura)){
                solsic = "2";
                sogrib = "2";
              }

            }else{
              //Negli altri casi si considera il numero di record importati
              newContaf = numRecordImportati + 1;
              newNorvoc = numRecordImportati + 1;
            }

	        String codvoc = codiceLavorazFornitura;
	        BigDecimal tmpQuanti = rigaCarrello.getQuantita();
	        BigDecimal tmpPrezzoUnitario = rigaCarrello.getPrezzoUnitario();

	        Double quanti = new Double(tmpQuanti.doubleValue());
	        Double prezzoUnitario = new Double(tmpPrezzoUnitario.doubleValue());

	        String strSqlInsert="insert into GCAP (NGARA,CONTAF,NORVOC,CODVOC,QUANTI,PREZUN,CLASI1," +
	            "SOLSIC,SOGRIB,VOCE) values (?,?,?,?,?,?,?,?,?,?)";

	        try{
	          this.sqlManager.update(strSqlInsert, new Object[]{codiceLotto,new Long(newContaf),
	                new Long(newNorvoc),codvoc,quanti,prezzoUnitario,clasi1,solsic,sogrib,descrizione});
	        }catch(SQLException e){
	          logger.error("Errore nell'inserimento dell'entita GCAP", e);
	            throw e;
	        }

	        if(!new Long(3).equals(tipoFornitura)){
	        //Inserimento in GCAP_SAN
	            List listaValoriGCAP_SAN = new ArrayList();
	            String sqlInsertGCAP_SAN = null;

	            listaValoriGCAP_SAN.add(codiceLotto);
	            listaValoriGCAP_SAN.add(new Long(newContaf));


	            //Per Farmaci
	            if(new Long(1).equals(tipoFornitura)){
	              String codatc = rigaCarrello.getCodiceATC();
	              listaValoriGCAP_SAN.add(codatc);

	              String codaur = rigaCarrello.getCodiceAUR();
	              listaValoriGCAP_SAN.add(codaur);

	              String princatt = rigaCarrello.getPrincipioAttivo();
	              listaValoriGCAP_SAN.add(princatt);

	              String formaFarm = rigaCarrello.getFormaFarmaceutica();
	              listaValoriGCAP_SAN.add(formaFarm);

	              String dosaggio = rigaCarrello.getDosaggio();
	              listaValoriGCAP_SAN.add(dosaggio);

	              String viasomm = rigaCarrello.getViaSomministrazione();
	              listaValoriGCAP_SAN.add(viasomm);

	              //String note = rigaCarrello.getNote();  //Nel campo note metto la descrizione AUR
	              String note = descrizione;
	              listaValoriGCAP_SAN.add(note);

	              sqlInsertGCAP_SAN = "insert into gcap_san (NGARA,CONTAF,CODATC,CODAUR," +
	                "PRINCATT,FORMAFARM,DOSAGGIO,VIASOMM,NOTE) values (?,?,?,?,?,?,?,?,?)";

	            }else if(new Long(2).equals(tipoFornitura)){ //Per dispositivi medici
	              String codclass = rigaCarrello.getCodiceNazionale();  //Da modificare, stefano sta facendo aggiungere il campo in AUR
	              listaValoriGCAP_SAN.add(codclass);

	              String deprodcn = rigaCarrello.getDescrizioneNazionale(); //Da modificare, stefano sta facendo aggiungere il campo in AUR
	              listaValoriGCAP_SAN.add(deprodcn);

	              String codaur = rigaCarrello.getCodiceAUR();
	              listaValoriGCAP_SAN.add(codaur);

	              //String dropdcap = rigaCarrello.getDescrizioneProdotto(); //Nel campo Descrizione del prodotto da capitolato metto la descrizione AUR
	              String dropdcap = descrizione;
	              listaValoriGCAP_SAN.add(dropdcap);

	              sqlInsertGCAP_SAN = "insert into gcap_san (NGARA,CONTAF,CODCLASS,DEPRODCN," +
	              "CODAUR,DPRODCAP) values (?,?,?,?,?,?)";
	            }

	            try{
	              this.sqlManager.update(sqlInsertGCAP_SAN, listaValoriGCAP_SAN.toArray());
	            }catch(SQLException e){
	              logger.error("Errore nell'inserimento dell'entita GCAP_SAN", e);
	                throw e;
	            }
	        }



	        //Se l'inserimento in GCAP e GCAP_SAN è andato a buon fine incremento la variabile
	        //numRecordImportati
	        numRecordImportati++;

	        if (logger.isDebugEnabled())
              logger.debug("Inserimento dei valori della riga "
                  + (i + 1) + " avvenuta con successo");

	      }

	    }

	    if (!mappaCodiciLotti.isEmpty()) {
	      LoggerImportOffertaPrezzi loggerImport = new LoggerImportOffertaPrezzi();

	      try {
	        importExportOffertaPrezziManager.creazioneLotti(mappaCodiciLotti, ngara, loggerImport, tipoFornitura,mappaDescrizioniLotti,null,null);
	      } catch (GestoreException e) {
            logger.error("Errore nell'inserimento dei lotti", e);
            throw new SQLException();
          }
	    }


	    if(codiceGara.indexOf("$")==0)
          codiceGara = codiceGara.substring(1);

	    String codiceCarrello = (String)mappaDatiWebService.get("carrello");

	    try{
	      WsAURSoapProxy proxy = (WsAURSoapProxy)mappaDatiWebService.get("proxy");
          String userAUR = (String)mappaDatiWebService.get("user");
          String passwordAUR = (String)mappaDatiWebService.get("password");
          //String codicesaAUR = (String)mappaDatiWebService.get("SA");
          if(logger.isDebugEnabled()) {
            String log="Chiamata al servizio AUR_SetGaraCarrello con i seguenti parametri:";
            log+=" utente=" + userAUR + "; password=" + passwordAUR + "; codiceSA=" + codicesaAUR + "; codiceGara=" + codiceGara + "; codiceCarrello=" + codiceCarrello;
            logger.debug(log);
          }
	      long esito=proxy.AUR_SetGaraCarrello( userAUR, passwordAUR, codicesaAUR, codiceGara, codiceCarrello);

	      if (esito<0){
	        logger.error("Il webservice AUR_SetGaraCarrello ha restituito il codice di errore:" + String.valueOf(esito));
	        throw new SQLException();
	      }



        }catch (RemoteException e) {
          logger.error("Errore nella chiamata al web service AUR_SetGaraCarrello", e);
          throw new SQLException();
        }


        //Aggiornamento del campo gare.carrello
        try{
          this.sqlManager.update("update gare set carrello=? where ngara=?", new Object[]{codiceCarrello,ngara});
        }catch(SQLException e){
          logger.error("Errore nell'aggiornamento del campo CARRELLO di GARE", e);
            throw e;
        }

	    if (logger.isDebugEnabled()) {
	      logger.debug("insertCarrello: fine metodo");
	    }

	  }

	  /**
       * Viene richiamato il webservice  AUR_SetFornitoreEscluso
       *
       * @param mappaDatiWebService
       *           una hash map che contiene i valori:
       *            -proxy
       *            -fornitore: codice fornitore AUR
       *            -tipoAzione: 1(Non ammesso),2(ammesso)
       *            -codiceGara: codice gara Alice
       *            -codiceLotto: codice lotto Alice
       *            -tipoEsclusione:
       *            -descrizioneEsclusione
       *
       *
       * @throws SQLException
       */
	  public void setEsclusioneFornitore(HashMap mappaDatiWebService) throws SQLException{

	    String userAUR = ConfigManager.getValore("it.eldasoft.sil.pg.userAUR");
	    String passwordAUR = ConfigManager.getValore("it.eldasoft.sil.pg.passwordAUR");
	    //String codicesaAUR = ConfigManager.getValore("it.eldasoft.sil.pg.codicesaAUR");
	    String codicesaAUR = null;

	    WsAURSoapProxy proxy = (WsAURSoapProxy)mappaDatiWebService.get("proxy");
	    String codiceFornitore = (String)mappaDatiWebService.get("fornitore");
	    Long tipoAzione = (Long)mappaDatiWebService.get("tipoAzione");
	    String codiceGara = (String)mappaDatiWebService.get("codiceGara");
	    //Si determina la stazione appaltante
	    codicesaAUR = (String)this.sqlManager.getObject("select cenint from torn where codgar=?",
	        new Object[]{codiceGara});
	    if(codiceGara.indexOf("$")==0)
	      codiceGara = codiceGara.substring(1);
	    String codiceLotto = (String)mappaDatiWebService.get("codiceLotto");
	    Long tipoMotivoLong = (Long)mappaDatiWebService.get("tipoEsclusione");
	    String descrizioneMotivo = (String)mappaDatiWebService.get("descrizioneEsclusione");
	    int tipoMotivo =0;
	    if (tipoMotivoLong != null)
	      tipoMotivo = tipoMotivoLong.intValue();

	    if (descrizioneMotivo == null){
	      descrizioneMotivo = "non specificato";
	    }

	    try {
	      if(logger.isDebugEnabled()) {
	          String log="Chiamata al servizio AUR_SetFornitoreEscluso con i seguenti parametri:";
	          log+=" utente=" + userAUR + "; password=" + passwordAUR + "; codiceSA=" + codicesaAUR + "; codiceFornitore=" + codiceFornitore + "; tipoAzione=" + tipoAzione.toString()+ "; codiceGara=" + codiceGara + "; codiceLotto=" +codiceLotto + "; tipoMotivo=" + String.valueOf(tipoMotivo) + "; descrizioneMotivo=" + descrizioneMotivo;
	          logger.debug(log);
	        }
	      long esito=proxy.AUR_SetFornitoreEscluso( userAUR, passwordAUR, codicesaAUR, codiceFornitore, tipoAzione.intValue(), codiceGara, codiceLotto,tipoMotivo, descrizioneMotivo);

          if (esito<0){
            logger.error("Il webservice AUR_SetFornitoreEscluso ha restituito il codice di errore:" + String.valueOf(esito));
            throw new SQLException();
          }

        } catch (RemoteException e) {
          logger.error("Errore nella chiamata al web service AUR_SetFornitoreEscluso", e);
          throw new SQLException();
        }

	  }



      /**
       * Vengono letti i dati dell'offerta dal portale AUR per inserire su Alice il prodotto
       *
       * @param righeOffertaFornitore
       *           dettaglio dei dati del prodotto
       * @param ngara
       * @param codiceDitta
       * @param garaLottiConOffertaUnica
       *           "1" gara con offerta unica, "2" altrimenti
       * @param tipoForniture
       *            "1" farmaci, "2" dispositivi medici
       *
       * @throws SQLException
       */
      public void insertOffertaProdotto(RigheOffertaFornitore righeOffertaFornitore, String ngara, String codiceDitta,String garaLottiConOffertaUnica,
          String tipoForniture) throws SQLException{


        long dimArray = righeOffertaFornitore.getRigaOffertaFornitoreArray().length;
        if(dimArray>0){

          /*
          //Nel vettore per ogni codice aur possono essere presenti più offerte, si deve
          //prendere in considerazione solo l'ultima, ossia quella con il valore
          //di NrRigaProdotto più grande
          HashMap datiDaProcessare = new HashMap();
          for(int i=0;i<dimArray;i++){
            String codiceAur = righeOffertaFornitore.getRigaOffertaFornitoreArray(i).getCodiceAUR();
            int nRigaProdotto = righeOffertaFornitore.getRigaOffertaFornitoreArray(i).getNrRigaProdotto();
            if(!datiDaProcessare.containsKey(codiceAur)){
              Object ob[] = {new Long(nRigaProdotto),new Long(i)};
              datiDaProcessare.put(codiceAur, ob);
            }else{
              Object ob[] = (Object[])datiDaProcessare.get(codiceAur);
              Long oldRigaProdotto =(Long)ob[0];
              if(nRigaProdotto>oldRigaProdotto.intValue()){
                datiDaProcessare.remove(codiceAur);
                Object newOb[] = {new Long(nRigaProdotto),new Long(i)};
                datiDaProcessare.put(codiceAur, newOb);
              }
            }

          }
          //ciclo sulla hashmap
          Iterator hashIterator = datiDaProcessare.keySet().iterator();
          while(hashIterator.hasNext()){
            String chiave = (String)hashIterator.next();
            Object ob[] = (Object[])datiDaProcessare.get(chiave);
            Long indiceVettore = (Long)ob[1];
            int i = indiceVettore.intValue();



          }
          */


          //Nel caso di gare ad offerta unica (ngara = codgar1 per la gara fittizia), si è assunto che
          //tutti i prodotti vengano associati ad un lotto con catiga=1
          //questo perchè da aur non arriva nulla relativo al lotto!!!!
          if("1".equals(garaLottiConOffertaUnica)){
            String codiga = "1";
            String ngaraGaraFittizzia = ngara;
            String select="select ngara from gare where codiga=? and codgar1=?";
            String ngaraLotto = (String)this.sqlManager.getObject(select, new Object[]{codiga,ngaraGaraFittizzia});
            if(ngaraLotto== null || "".equals(ngaraLotto)){
              logger.error("Errore nella determinazione del codice del lotto");
              throw new SQLException();
            }
            ngara = ngaraLotto;
          }


          for(int i=0;i<dimArray;i++){

            String codiceAur = righeOffertaFornitore.getRigaOffertaFornitoreArray(i).getCodiceAUR();
            String select="";
            String sql= null;

            //Si devono individuare i prodotti a cui associare l'offerta tramita il codice AUR
            select="select gcap_san.contaf from gcap_san,gcap where gcap_san.ngara=? and gcap_san.codaur=? " +
              "and gcap_san.ngara=gcap.ngara and gcap_san.contaf=gcap.contaf and gcap.dittao is null";
            Long valoreContaf = (Long)this.sqlManager.getObject(select, new Object[]{ngara,codiceAur});

            try{
              //Cancello l'occorrenza nelle entità DPRE, DPRE_SAN
              this.sqlManager.update(
                  "delete from DPRE_SAN where NGARA = ? and CONTAF = ? and DITTAO = ?",
                  new Object[] { ngara, valoreContaf, codiceDitta });
              this.sqlManager.update(
                  "delete from DPRE where NGARA = ? and CONTAF = ? and DITTAO = ?",
                  new Object[] { ngara,valoreContaf, codiceDitta });

            }catch(SQLException e){
                logger.error("Errore nella cancellazione delle entita DPRE e DPRE_SAN", e);
                throw e;
            }

            String prodottoConforme = righeOffertaFornitore.getRigaOffertaFornitoreArray(i).getProdottoConforme();
            if(prodottoConforme!=null){
              if("TRUE".equals(prodottoConforme.toUpperCase()))
                prodottoConforme="1";
              else if("FALSE".equals(prodottoConforme.toUpperCase()))
                prodottoConforme="2";
            }

            BigDecimal tmpPrezzoUnitario = righeOffertaFornitore.getRigaOffertaFornitoreArray(i).getPrezzoUnitario();
            Double prezzoUnitario = null;
            Double prezzoOfferto = null;

            //Si deve calcolare DPRE.IMPOFF = GCAP.QUANTI * prezzo unitario
            if(tmpPrezzoUnitario!= null && tmpPrezzoUnitario.doubleValue() !=0){
              prezzoUnitario = new Double(tmpPrezzoUnitario.doubleValue());
              Double quanti = (Double)this.sqlManager.getObject("select quanti from gcap where "+
                  " ngara=? and contaf = ? ", new Object[]{ngara,valoreContaf});
              if(quanti!=null)
                prezzoOfferto = new Double(tmpPrezzoUnitario.doubleValue() * quanti.doubleValue());
            }

            //Inserimento in DPRE
            sql ="insert into dpre (ngara,contaf,dittao,preoff,impoff,reqmin) values(?,?,?,?,?,?)";
            this.sqlManager.update(sql, new Object[]{ngara,valoreContaf,codiceDitta,prezzoUnitario,prezzoOfferto,prodottoConforme});

            String codiceProdotto = righeOffertaFornitore.getRigaOffertaFornitoreArray(i).getCodiceProdotto();
            String descrizioneProdotto = righeOffertaFornitore.getRigaOffertaFornitoreArray(i).getDescrizioneProdotto();
            BigDecimal tmpquantitaProdotto = righeOffertaFornitore.getRigaOffertaFornitoreArray(i).getQuantitaProdotto();
            Double quantitaProdotto = null;
            if(tmpquantitaProdotto!=null)
              quantitaProdotto = new Double(tmpquantitaProdotto.doubleValue());

            String note = righeOffertaFornitore.getRigaOffertaFornitoreArray(i).getNote();
            Object parametri[] = null;

            //Inserimento in DPRE_SAN
            if("1".equals(tipoForniture)){
              sql="insert into dpre_san(ngara,contaf,dittao,codaic,denomprod,nuniconf,acquisito) values(?,?,?,?,?,?,?)";
              parametri = new Object[7];
            }else{
              sql="insert into dpre_san(ngara,contaf,dittao,codprod,denomprod,quanticonf,acquisito,note) values(?,?,?,?,?,?,?,?)";
              parametri = new Object[8];
              parametri[7] = note;
            }
            parametri[0] = ngara;
            parametri[1] = valoreContaf;
            parametri[2] = codiceDitta;
            parametri[3] = codiceProdotto;
            parametri[4] = descrizioneProdotto;
            parametri[5] = quantitaProdotto;
            parametri[6] = "1";
            this.sqlManager.update(sql, parametri);
          }
        }

      }


      /**
       * Viene richiamato il servizio AUR_SetProdottoConforme per indicare al portale AUR
       * se il prodotto offerto è conforme o meno
       *
       * @param ngara
       * @param codiceGara
       * @param fornitore
       * @param conforme
       *            "1" conform, "2" non conforme
       * @param codiceProdotto
       *
       * @throws SQLException
       */
      public void setProdottoConforme(String ngara,String codiceGara,String fornitore,String conforme,String codiceProdotto) throws SQLException{

        String urlWsAUR = ConfigManager.getValore("it.eldasoft.sil.pg.integrazioneAUR.ws.url");
        WsAURSoapProxy proxy=new WsAURSoapProxy(urlWsAUR);

        String userAUR = ConfigManager.getValore("it.eldasoft.sil.pg.userAUR");
        String passwordAUR = ConfigManager.getValore("it.eldasoft.sil.pg.passwordAUR");
        //String codicesaAUR = ConfigManager.getValore("it.eldasoft.sil.pg.codicesaAUR");
        String codicesaAUR = null;
        codicesaAUR = (String)this.sqlManager.getObject("select cenint from torn where codgar=?",
            new Object[]{codiceGara});
        String codiceFornitore = null;
        codiceFornitore = fornitore.substring(1);  //I fornitori inseriti da AUR cominciano tutti con "A"

        boolean conformeBool = true;

        int tipoMotivo=0;
        String descrizioneMotivo=null;

        if(conforme==null || "".equals(conforme))
          conforme="1";

        if("2".equals(conforme)){
          conformeBool = false;
          descrizioneMotivo="non specificata";
        }

        if(codiceGara.indexOf("$")==0)
          codiceGara = codiceGara.substring(1);
        /*
        //Si devono ricavare i codici delle ditte inserite da AUR per la gara
        select="select dittao from ditg where codgar5=? and ngara5=? and acquisizione=12";
        List listaCodiciDitta = this.sqlManager.getListVector(select,new Object[]{codgar, ngara});
        if(listaCodiciDitta != null && listaCodiciDitta.size() > 0){
          //Per ogni ditta estraggo i prodotti offerti inseriti da AUR,
          //con Cristian abbiamo concordato che un prodotto inserito da AUR si
          //riconosce perchè ha il codaur valorizzato(quello di DPRE_SAN???)
          select="select GCAP_SAN.codaur,GCAP_SAN.contaf from GCAP_SAN,DPRE_SAN where GCAP_SAN.ngara=?"+
              " and  GCAP_SAN.ngara = DPRE_SAN.ngara and GCAP_SAN.contaf = DPRE_SAN.contaf and " +
              " GCAP_SAN.codaur is not null and GCAP_SAN.codaur = DPRE_SAN.codaur and  dittao=?";
          for(int i=0; i < listaCodiciDitta.size(); i++){
            Vector tmpVect = (Vector) listaCodiciDitta.get(i);
            String dittao = (String) ((JdbcParametro) tmpVect.get(0)).getValue();
            codiceFornitore = dittao.substring(1);
            List listaDatiProdotto=this.sqlManager.getListVector(select,new Object[]{ngara,dittao});
            if(listaDatiProdotto != null && listaDatiProdotto.size() > 0){
              for(int j=0; j < listaDatiProdotto.size(); j++){
                Vector tmpVect1 = (Vector) listaDatiProdotto.get(j);
                codiceProdotto = (String)((JdbcParametro) tmpVect1.get(0)).getValue();
                Long contaf = (Long) ((JdbcParametro) tmpVect1.get(1)).getValue();
                reqmin=(String)this.sqlManager.getObject("select reqmin from dpre where ngara=? "+
                    " and contaf=? and dittao=? ", new Object[]{ngara,contaf,dittao});

                if(reqmin==null || "".equals(reqmin))
                  reqmin="2";

                if("2".equals(reqmin))
                  conforme=false;
                else
                  conforme=true;

                if(conforme==true)
                  tipoMotivo=0;

                 try {
                   String codiceGara= codgar;
                   if(codiceGara.indexOf("$")==0)
                     codiceGara = codiceGara.substring(1);
                   long esito=proxy.AUR_SetProdottoConforme( userAUR, passwordAUR, codicesaAUR, codiceFornitore, codiceProdotto, conforme, codgar, ngara,tipoMotivo, descrizioneMotivo);
                    String aaa=null;

                    if (esito<0){
                      logger.error("Il webservice AUR_SetProdottoConforme ha restituito il codice di errore:" + String.valueOf(esito));
                      throw new SQLException();
                    }

                  } catch (RemoteException e) {
                    logger.error("Errore nella chiamata al web service AUR_SetFornitoreEscluso", e);
                    throw new SQLException();
                  }



              }
            }

          }
        }

        */

        try {
          if(logger.isDebugEnabled()) {
            String conformeString = "true";
            if(! conformeBool)
              conformeString = "false";

            String log="Chiamata al servizio AUR_SetProdottoConforme con i seguenti parametri:";
            log+=" utente=" + userAUR + "; password=" + passwordAUR + "; codiceSA=" + codicesaAUR + "; codiceFornitore=" + codiceFornitore;
            log+= "; codiceProdotto=" + codiceProdotto + "; conformeBool=" + conformeString + "; codiceGara=" + codiceGara + "; ngara=" + ngara;
            String descMotivo = descrizioneMotivo;
            if(descMotivo==null)
              descMotivo = "";
            log+= "; tipoMotivo=0; descrizioneMotivo=" + descMotivo;

            logger.debug(log);
          }
          long esito=proxy.AUR_SetProdottoConforme( userAUR, passwordAUR, codicesaAUR, codiceFornitore, codiceProdotto, conformeBool, codiceGara, ngara,tipoMotivo, descrizioneMotivo);

          if (esito<0){
            logger.error("Il webservice AUR_SetProdottoConforme ha restituito il codice di errore:" + String.valueOf(esito));
            throw new SQLException();
          }

        } catch (RemoteException e) {
          logger.error("Errore nella chiamata al web service AUR_SetProdottoConforme", e);
          throw new SQLException();
        }


      }

      /**
       * Viene controllato se la stringa è numerica
       *
       * @param String
       *
       * @return true se la stringa è numerica, false se è alfanumerica
       */
      private boolean isNumberCheck(String s)
      {
        boolean ret = true;

        for (int i= 0; i < s.length(); i++){
          char carattere =   s.charAt(i);
          if ((Character.isLetter(carattere)))
            return false;
        }

        return ret;
      }
}