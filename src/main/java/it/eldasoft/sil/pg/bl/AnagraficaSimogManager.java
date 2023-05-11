/*
 * Created on 09/06/21
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.tags.gestori.submit.GestoreDITG;
import it.eldasoft.sil.w3.bl.GestioneServiziIDGARACIGManager;
import it.eldasoft.utils.utility.UtilityFiscali;
import it.eldasoft.utils.utility.UtilityNumeri;

/**
 * Classe di gestione delle funzionalita' inerenti  il
 * popolamento della anagrafica per la richiesta cig
 *s
 * @author Cristian.Febas
 */
public class AnagraficaSimogManager {

	/** Logger */
	static Logger logger = Logger.getLogger(AnagraficaSimogManager.class);

	private static final String ERRORI_NON_BLOCCANTI_KEY          = "erroriNonBloccanti";

    private static final String ERRORI_BLOCCANTI_KEY              = "erroriBloccanti";

	  private static final String PROP_INVIO_DATI_CIG_WS_URL        = "it.eldasoft.inviodaticig.ws.url";

	  private static final String CTR_NESSUN_LOTTO                  = "Non sono stati definiti i lotti della gara";

	  private static final String CTR_GARA_OGGETTO                  = "L'oggetto della gara non e' valorizzato";

	  private static final String CTR_STIPULA_OGGETTO               = "L'oggetto della stipula non e' valorizzato";

	  private static final String CTR_LOTTI_OGGETTO                 = "L'oggetto della gara non è valorizzato nei lotti ";

	  private static final String CTR_GARA_RUP                      = "Il riferimento al responsabile unico procedimento non e' valorizzato";

	  private static final String CTR_GARA_RUP_CHANGED              = "Il riferimento al responsabile unico procedimento e' differente dall'originario";

	  private static final String CTR_GARA_SA_CHANGED              = "Il riferimento alla stazione appaltante e' differente dall'originario";

	  private static final String CTR_GARA_IMPORTO                  = "L'importo a base di gara non e' valorizzato";

	  private static final String CTR_STIPULA_IMPORTO               = "L'importo della stipula non e' valorizzato";

	  private static final String CTR_LOTTI_IMPORTO                 = "L'importo a base di gara non è valorizzato nei lotti ";

	  private static final String CTR_LOTTI_CIG                     = "Risultano valorizzati i CIG nei lotti ";

	  private static final String CTR_RUP_INTESTAZIONE              = "Il nome del responsabile unico procedimento non e' valorizzato";

	  private static final String CTR_RUP_CF                        = "Il codice fiscale del responsabile unico procedimento non e' valorizzato";

	  private static final String CTR_RUP_CF_NON_VALIDO             = "Il codice fiscale del responsabile unico procedimento non ha un formato valido";

	  private static final String CTR_RUP_PIVA_NON_VALIDA           = "La partita Iva del responsabile unico procedimento non ha un formato valido";

	  private static final String CTR_GARE_TORN_TIPGAR              = "Il tipo procedura non è valorizzato";

	  private static final String CTR_MODREA                        = "La modalità di realizzazione non è valorizzata";

	  private static final String CTR_TIPLAV                        = "La tipologia lavoro non è valorizzata";

	  private static final String CTR_TIPLAV_LOTTI                  = "La tipologia lavoro non è valorizzata nei lotti ";

	  private static final String CTR_OGGCONT                       = "L'oggetto contratto non è valorizzato";

	  private static final String CTR_OGGCONT_LOTTI                 = "L'oggetto contratto non è valorizzato nei lotti ";

	  private static final String CTR_CPV                           = "Il codice CPV non è valorizzato";

	  private static final String CTR_CPV_LOTTI                     = "Il codice CPV non è valorizzato nei lotti ";

	  private static final String CTR_CPV_NON_VALIDO                = "Il codice CPV deve essere dettagliato almeno fino al terzo livello";

	  private static final String CTR_CPV_NON_VALIDO_LOTTI          = "Il codice CPV deve essere dettagliato almeno fino al terzo livello nei lotti ";

	  private static final String CTR_DONUTS                        = "Il codice NUTS non è valorizzato";

	  private static final String CTR_ISTAT                        = "Il codice ISTAT del luogo di esecuzione non è valorizzato";

	  private static final String CTR_ISTAT_LOTTI                  = "Il codice ISTAT del luogo di esecuzione non è valorizzato nei lotti ";

	  private static final String CTR_DONUTS_ISTAT                  = "Il codice ISTAT del luogo di esecuzione e il codice NUTS sono entrambi non valorizzati. Valorizzare almeno uno dei due.";

	  private static final String CTR_DONUTS_ISTAT_LOTTI            = "Il codice ISTAT del luogo di esecuzione e il codice NUTS sono entrambi non valorizzati. Valorizzare almeno il codice NUTS nella gara o il codice ISTAT nei lotti ";

	  private static final String CTR_SA_CF                         = "Il codice fiscale della stazione appaltante non e' valorizzato";

	  private static final String CTR_SA                            = "Il riferimento alla stazione appaltante non e' valorizzato";

	  private static final String CTR_SA_CF_NON_VALIDO              = "Il codice fiscale della stazione appaltante non ha un formato valido";

	  private static final String CTR_CUI                           = "Il codice CUI non è valorizzato. Procedendo la procedura verrà intesa come non inserita nella programmazione";

	  private static final String CTR_CUPPRG                        = "Il codice CUP di progetto non è valorizzato. Procedendo la procedura verrà intesa come esente CUP";

	  private static final String CTR_CUI_LOTTI                     = "Il codice CUI non è valorizzato nei lotti ";

	  private static final String CTR_CUPPRG_LOTTI                  = "Il codice CUP di progetto non è valorizzato nei lotti ";

	  private static final String CTR_GARE_TORN_MAP_TIPGAR_A1z11    = "Il tipo procedura non risulta mappato nei valori di scelta contraente SIMOG (verificare il parametro A1z11)";
	  private static final String CTR_GARE_TORN_MAP_TIPGAR_A1z05    = "Il tipo procedura non risulta mappato nei valori di scelta contraente L.190/2012 (verificare il parametro A1z05)";

	  private static final String CTR_GARA_NOMOD                     = "La gara che si sta tentando di aggiornare e' in uno stato non modificabile: non e' possibile procedere nell'aggiornamento";


	/** Manager SQL per le operazioni su database */
	private SqlManager sqlManager;

	private GenChiaviManager   genChiaviManager;

	private TabellatiManager tabellatiManager;

	private InviaVigilanzaManager  inviaVigilanzaManager;

	private InviaDatiRichiestaCigManager  inviaDatiRichiestaCigManager;

	private GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager;

	private PgManager pgManager;

	private PgManagerEst1 pgManagerEst1;

	/**
	 * Set dei manager
	 */

	public void setSqlManager(SqlManager sqlManager) {
		this.sqlManager = sqlManager;
	}

    public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
	    this.genChiaviManager = genChiaviManager;
	}

	public void setTabellatiManager(TabellatiManager tabellatiManager) {
	    this.tabellatiManager = tabellatiManager;
	}

	public void setInviaVigilanzaManager(InviaVigilanzaManager inviaVigilanzaManager) {
	    this.inviaVigilanzaManager = inviaVigilanzaManager;
    }

	public void setInviaDatiRichiestaCigManager(InviaDatiRichiestaCigManager inviaDatiRichiestaCigManager) {
	    this.inviaDatiRichiestaCigManager = inviaDatiRichiestaCigManager;
    }

	public void setGestioneServiziIDGARACIGManager(GestioneServiziIDGARACIGManager gestioneServiziIDGARACIGManager) {
		this.gestioneServiziIDGARACIGManager = gestioneServiziIDGARACIGManager;
	}

	public void setPgManager(PgManager pgManager) {
		this.pgManager = pgManager;
	}

	public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
		this.pgManagerEst1 = pgManagerEst1;
	}

  /**
   * Restituisce le chiavi dell'anagrafica CIG creata o aggiornata
   * @param codgar
   * @param ngara
   * @param genereString
   * @return String
   * @throws SQLException
   * @throws IOException
   */
  public HashMap<String,Object> setAnagraficaCig(String codgar, String genereString, String tipoOp, String idStipula)
  	throws SQLException,IOException, Exception {

    if (logger.isDebugEnabled())
	        logger.debug("setAnagraficaCig: inizio metodo");

    HashMap<String,Object> hm = new HashMap<String,Object>();

    //DISTINGUO il genere della Gara
    Long genere = new Long(genereString);
    String selectDatiGara = null;


    switch (genere.intValue()) {
    case 1: //gara a lotti con offerte distinte
    case 3:
        selectDatiGara = "select "
            + " torn.destor, "      //0
            + " torn.imptor, "      //1
            + " torn.codrup, "      //2
            + " torn.isadesione, "  //3
            + " torn.codcigaq, "    //4
            + " torn.modrea, "       //5
            + " torn.settore, "      //6
            + " uffint.cfein, "      //7
            + " torn.sommaur, "       //8
            + " torn.accqua, "      //9
            + " torn.altrisog, "     //10
            + " torn.aqdurata, "    //11
            + " torn.aqtempo, "     //12
            + " uffint.iscuc,"      //13
            + " uffint.cfanac, "    //14
            + " torn.cenint "       //15
            + " from torn, uffint where codgar = ? and torn.cenint = uffint.codein";
        break;
    case 2: //gara a lotto unico
    	selectDatiGara = "select "
          + " gare.not_gar, "       //0
          + " gare.impapp, "        //1
          + " torn.codrup, "        //2
          + " torn.isadesione, "    //3
          + " torn.codcigaq,"       //4
          + " torn.modrea, "         //5
          + " torn.settore, "        //6
          + " uffint.cfein, "      //7
          + " torn.sommaur, "       //8
          + " torn.accqua, "      //9
          + " torn.altrisog, "    //10
          + " torn.aqdurata, "    //11
          + " torn.aqtempo, "     //12
          + " uffint.iscuc,"      //13
          + " uffint.cfanac, "     //14
          + " torn.cenint "       //15
          + " from torn, gare,uffint where torn.codgar = gare.codgar1 "
          + " and torn.codgar = ? and torn.cenint = uffint.codein";
      break;
    }

    List datiGara = sqlManager.getVector(selectDatiGara,new Object[] { codgar });
    if (datiGara != null && datiGara.size() > 0) {
    	String codRUP = (String) SqlManager.getValueFromVectorParam(datiGara,2).getValue();
    	/*APPALTI-1063
    	Long syscon = (Long) sqlManager.getObject("select syscon from w3usrsys where rup_codtec=? order by syscon", new Object[] { codRUP });
    	*/
    	String codein = (String) SqlManager.getValueFromVectorParam(datiGara,15).getValue();
        //Codice fiscale stazione appaltante
        String cfein = null;
        Long altrisog= (Long) SqlManager.getValueFromVectorParam(datiGara,10).getValue();
        String iscuc = (String) SqlManager.getValueFromVectorParam(datiGara,13).getValue();
        if("1".equals(iscuc) && (new Long(2).equals(altrisog) || new Long(3).equals(altrisog))){
          cfein = (String) SqlManager.getValueFromVectorParam(datiGara,14).getValue();
          if(cfein==null || "".equals(cfein))
            cfein = (String) SqlManager.getValueFromVectorParam(datiGara,7).getValue();
        }else
          cfein = (String) SqlManager.getValueFromVectorParam(datiGara,7).getValue();

       boolean esisteW3GARA = false;
       Long numgara = null;
       numgara = (Long) sqlManager.getObject("select numgara from w3gara where codgar = ? and stato_simog<>6", new Object[]{codgar});
       if(numgara != null) {
	   	 esisteW3GARA = true;
	   }
       if (!esisteW3GARA) {

	       // LA GARA NON ESISTE: SI PROCEDE A CREARNE UNA NUOVA

	       // Calcolo della chiave
	       numgara = Long.valueOf(genChiaviManager.getNextId("W3GARA"));

	       DataColumnContainer dccW3GARA = new DataColumnContainer(new DataColumn[] { new DataColumn("W3GARA.NUMGARA", new JdbcParametro(
	           JdbcParametro.TIPO_NUMERICO, numgara)) });

	       // Utente proprietario

	        //APPALTI-1063
	       //dccW3GARA.addColumn("W3GARA.SYSCON", JdbcParametro.TIPO_NUMERICO, syscon);
	       // Stato della gara: nuova gara in attesa di richiesta, a SIMOG,
	       // del numero gara IDGARA

	       // codice gara
	       dccW3GARA.addColumn("W3GARA.CODGAR", codgar);

	       dccW3GARA.addColumn("W3GARA.STATO_SIMOG", JdbcParametro.TIPO_NUMERICO, Long.valueOf(1));

	       // Aggiungo la lista delle colonne, per poterle utilizzare
	       // nel metodo successivo
	        dccW3GARA.addColumn("W3GARA.OGGETTO", JdbcParametro.TIPO_TESTO, null);
	        dccW3GARA.addColumn("W3GARA.TIPO_SCHEDA", JdbcParametro.TIPO_TESTO, null);
	        dccW3GARA.addColumn("W3GARA.MODO_INDIZIONE", JdbcParametro.TIPO_NUMERICO, null);
	        dccW3GARA.addColumn("W3GARA.MODO_REALIZZAZIONE", JdbcParametro.TIPO_NUMERICO, null);
	        dccW3GARA.addColumn("W3GARA.IMPORTO_GARA", JdbcParametro.TIPO_DECIMALE, null);
	        dccW3GARA.addColumn("W3GARA.RUP_CODTEC", JdbcParametro.TIPO_TESTO, null);
	        dccW3GARA.addColumn("W3GARA.COLLABORAZIONE", JdbcParametro.TIPO_DECIMALE, null);
	        dccW3GARA.addColumn("W3GARA.CIG_ACC_QUADRO", JdbcParametro.TIPO_TESTO, null);
	        dccW3GARA.addColumn("W3GARA.CODEIN", JdbcParametro.TIPO_TESTO, codein);

	        dccW3GARA.addColumn("W3GARA.M_RICH_CIG", JdbcParametro.TIPO_NUMERICO, null);
	        dccW3GARA.addColumn("W3GARA.ALLEGATO_IX", JdbcParametro.TIPO_NUMERICO, null);
	        dccW3GARA.addColumn("W3GARA.STRUMENTO_SVOLGIMENTO", JdbcParametro.TIPO_NUMERICO, null);
	        dccW3GARA.addColumn("W3GARA.URGENZA_DL133", JdbcParametro.TIPO_TESTO, null);
	        dccW3GARA.addColumn("W3GARA.ESTREMA_URGENZA", JdbcParametro.TIPO_NUMERICO, null);

	        dccW3GARA.addColumn("W3GARA.ESCLUSO_AVCPASS", JdbcParametro.TIPO_TESTO, null);
	        dccW3GARA.addColumn("W3GARA.DURATA_ACCQUADRO", JdbcParametro.TIPO_NUMERICO, null);
	        dccW3GARA.addColumn("W3GARA.VER_SIMOG", JdbcParametro.TIPO_NUMERICO, Long.valueOf(4));
	        // Setto nel DataColumnContainer anche gli altri dati
	        this.dccW3GARA_AltriDatiGara("INS", dccW3GARA, datiGara, numgara, null, cfein, codgar, Long.valueOf(genereString));

	        dccW3GARA.insert("W3GARA", this.sqlManager);
	        this.gestioneServiziIDGARACIGManager.gestioneRequisiti(numgara);

       }else {//la gara esiste
    	   if(("UPD").equals(tipoOp)) {

    		   // Controllo : la gara deve essere in uno stato modificabile
               Long stato_simog = (Long) sqlManager.getObject(
            	          "select stato_simog from w3gara where codgar = ? and stato_simog<>6", new Object[] {codgar});
               Boolean isModificabile = this.isSTATO_SIMOGModificabile(stato_simog);


               if (!isModificabile) {
                 logger.error("La gara (NUMGARA: "
                     + numgara.toString()
                     + ") che si sta tentando di aggiornare e' in uno stato non modificabile: non e' possibile procedere nell'aggiornamento");
                 throw new GestoreException(
                     "La gara che si sta tentando di aggiornare e' in uno stato non modificabile: non e' possibile procedere nell'aggiornamento",
                     "errors.inserisciGaraLotto.nonmodificabile", null);
               }

               DataColumnContainer dccW3GARA = new DataColumnContainer(this.sqlManager, "W3GARA", "select * from w3gara where numgara = ?",
                   new Object[] { numgara });

               // Aggiungo al DataColumnContainer anche gli altri dati comuni
               this.dccW3GARA_AltriDatiGara("UPD", dccW3GARA, datiGara, numgara, null, cfein, codgar,Long.valueOf(genereString));

               if (dccW3GARA.isModifiedTable("W3GARA")) {
                   if (!Long.valueOf(1).equals(stato_simog)) {
                       dccW3GARA.setValue("W3GARA.STATO_SIMOG", new Long(3));
                     }

                     dccW3GARA.getColumn("W3GARA.NUMGARA").setChiave(true);
                     dccW3GARA.setValue("W3GARA.NUMGARA", numgara);
                     dccW3GARA.setOriginalValue("W3GARA.NUMGARA", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numgara));
                     dccW3GARA.update("W3GARA", this.sqlManager);
            		this.gestioneServiziIDGARACIGManager.gestioneRequisiti(numgara);


               }


    	   }else{
    		   if(("DEL").equals(tipoOp) && numgara!=null) {
    			   this.gestioneW3LOTT(codgar, numgara, genere, tipoOp);
                   DataColumnContainer dccW3GARA = new DataColumnContainer(this.sqlManager, "W3GARA", "select * from w3gara where numgara = ?",
                           new Object[] { numgara });
                   dccW3GARA.getColumn("W3GARA.NUMGARA").setChiave(true);
                   dccW3GARA.setValue("W3GARA.NUMGARA", numgara);
                   dccW3GARA.delete("W3GARA", this.sqlManager);

    		   }
    	   }//UPD
       }

       // Gestione dell'inserimento/aggiornamento dei lotti associata alla gara
       //Nel caso in cui si e' scelto di non aggiornare la gara o di cancellarla non si aggiornano/creano
       //neanche i lotti
       if(!("NOUPD").equals(tipoOp) && !("DEL").equals(tipoOp)) {
    	   this.gestioneW3LOTT(codgar, numgara, genere, tipoOp);
       }

       	hm.put("key", numgara.toString());
	   	hm.put("tipoRichiesta", "C");

    }//dati gara


	if (logger.isDebugEnabled())
	          logger.debug("setAnagraficaCig: fine metodo");

    return hm;

  }

  /**
   * Restituisce le chiavi dell'anagrafica CIG creata o aggiornata
   * @param codgar
   * @param ngara
   * @param genereString
   * @return String
   * @throws SQLException
   * @throws IOException
   */
  public String creaAggGaraCigCollegato(String idStipula, HttpServletRequest request)
  	throws SQLException,IOException, Exception {

    if (logger.isDebugEnabled())
	        logger.debug("setCreaAggGaraCigCollegato: inizio metodo");

    ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long syscon = new Long(profilo.getId());

    String selectDatiStipula = null;
    String N_codgar=null;

    selectDatiStipula = "select "
            + " gare.ngara, "  //0
            + " g1stipula.oggetto, "  //1
            + " g1stipula.impstipula, "  //2
            + " g1stipula.ngaravar, "  //3
            + " g1stipula.ncont, "  //4
            + " g1stipula.id_padre " //5
            + " from g1stipula, gare where g1stipula.ngara = gare.ngara "
            + " and g1stipula.id = ?";

    List DatiStipula = sqlManager.getVector(selectDatiStipula,new Object[] { idStipula });

    String ngara = (String) SqlManager.getValueFromVectorParam(DatiStipula,0).getValue();
    String oggetto = (String) SqlManager.getValueFromVectorParam(DatiStipula,1).getValue();
    Double impstipula = (Double) SqlManager.getValueFromVectorParam(DatiStipula,2).getValue();
    String ngaravar = (String) SqlManager.getValueFromVectorParam(DatiStipula,3).getValue();

    Long ncont = (Long) SqlManager.getValueFromVectorParam(DatiStipula,4).getValue();

    Long id_padre=(Long) SqlManager.getValueFromVectorParam(DatiStipula,5).getValue();

    String selectDatiTorn = null;

    selectDatiTorn = "select "
            + " torn.tipgen, "  //0
            + " torn.settore, "  //1
            + " torn.cenint, "  //2
            + " torn.codrup "  //3
            + " from torn,gare where gare.codgar1=torn.codgar "
            + " and gare.ngara = ? ";

    List DatiTorn = sqlManager.getVector(selectDatiTorn,new Object[] { ngara });

    Long N_tipgen = (Long) SqlManager.getValueFromVectorParam(DatiTorn,0).getValue();
    String N_settore = (String) SqlManager.getValueFromVectorParam(DatiTorn,1).getValue();
    String N_cenint = (String) SqlManager.getValueFromVectorParam(DatiTorn,2).getValue();
    String N_codrup = (String) SqlManager.getValueFromVectorParam(DatiTorn,3).getValue();

    N_codgar = "$" + ngaravar;

	String iscuc = (String)this.sqlManager.getObject(
			"select iscuc from UFFINT where codein = ? ",
			new Object[] { N_cenint });

	Long altrisog= null;
	if("1".equals(iscuc)){
		altrisog=new Long(1);
	}

    //Inizializzazione del campo MODREA, ma solo per lotto unico e per offerte distinte o offerta unica(ma non per i lotti)
    String N_modrea = this.pgManagerEst1.getModrea(new String("2"), altrisog, new Long(89));

    String S_N_prerib = (String)this.sqlManager.getObject(
			"select tab1desc from TAB1 where tab1cod = ? ",
			new Object[] { "A1028" });
    int N_prerib = Integer.parseInt(S_N_prerib);

    String selectDatiAggiud = null;

    selectDatiAggiud = "select "
            + " codimp, "  //0
            + " nomest, "  //1
            + " daatto "  //2
            + " from v_aggiudicatari_stipula where ngara = ? and ncont = ? ";

    List DatiAggiud = sqlManager.getVector(selectDatiAggiud,new Object[] { ngara,ncont });

    String N_ditta = (String) SqlManager.getValueFromVectorParam(DatiAggiud,0).getValue();
    String N_nomima = (String) SqlManager.getValueFromVectorParam(DatiAggiud,1).getValue();
    Date N_dattoa = (Date) SqlManager.getValueFromVectorParam(DatiAggiud,2).getValue();

 	//Procedo alla creazione della gara visto che non esiste
    TransactionStatus status = null;
    boolean errore = false;
    try {
    status=sqlManager.startTransaction();

    String N_ngara = null;
    if("".equals(ngaravar) || ngaravar==null) {

        HashMap hm1 = pgManager.calcolaCodificaAutomatica("GARE", Boolean.TRUE, null,null);
        N_ngara =  (String) hm1.get("numeroGara");
        N_codgar = "$" + N_ngara;

    	String insertGara ="insert into gare(ngara, codgar1, codcig, tipgarg, not_gar, impapp, ditta, nomima, iaggiu, dattoa," +
    		    " fasgar, stepgar, modastg, ribagg, sicinc, temesi, ribcal, precut, pgarof, garoff, idiaut, calcsoang, onsogrib) " +
    		    " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    	String insertTorn = "insert into torn(codgar, cenint, tipgen, offaum, compreq, istaut, iterga, codrup, numavcp, settore, modrea, accqua, isadesione, altrisog, prerib, cliv2) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        String insertGare1 = "insert into gare1(ngara, codgar1) values(?,?)";
        String insertGarecont = "insert into garecont(ngara, ncont, codimp) values(?,?,?)";
        String insertG_permessi = "insert into g_permessi(numper, syscon, autori, propri, codgar) values(?,?,?,?,?)";

        String updateG1stipula = "update g1stipula set ngaravar = ? where id = ? ";

        Object parametriGare[] = new Object[23];
        //Inserimento Gara
        //ngara
        parametriGare[0] = N_ngara;
        //codgar1
        parametriGare[1] = N_codgar;
        //codcig
        parametriGare[2] = null;
        //tipgarg
        parametriGare[3] = new Long(89);
        //not_gar
        parametriGare[4] = oggetto;
        //impapp
        parametriGare[5] = impstipula;
        //ditta
        parametriGare[6] = N_ditta;
        //nomima
        parametriGare[7] = N_nomima;
        //iaggiu
        parametriGare[8] = impstipula;
        //dattoa
        parametriGare[9] = N_dattoa;
        //fasgar
        parametriGare[10] = new Long(-3);
        //stepgar
        parametriGare[11] = new Long(-30);
        //modastg
        parametriGare[12] = new Long(2);
        //ribagg
        if(parametriGare[5]==null || (parametriGare[5]!=null && ((Double)parametriGare[5]==0)))
          parametriGare[13] = new Double(0);
        else{
          Double iaggiu = ((Double)parametriGare[8]);
          if(iaggiu==null)
            iaggiu= new Double(0);
          Double ribagg = (iaggiu - (Double)parametriGare[5])*100/(Double)parametriGare[5];
          if (N_prerib!=0){
            ribagg = UtilityNumeri.convertiDouble(UtilityNumeri.convertiDouble(ribagg, UtilityNumeri.FORMATO_DOUBLE_CON_VIRGOLA_DECIMALE, N_prerib));
          }
          parametriGare[13] = ribagg;
        }
        //sicinc
        parametriGare[14] = "1";
        //temesi
        parametriGare[15] = new Long(1);
        //ribcal
        parametriGare[16] = new Long(1);
        //precut
        parametriGare[17] = new Long(tabellatiManager.getDescrTabellato("A1018", "1"));
        //pgarof
        parametriGare[18] = pgManager.initPGAROF(N_tipgen);
        if(parametriGare[18]==null)
      	  parametriGare[18]= new Double(0);
        else
      	parametriGare[18]= pgManager.initPGAROF(N_tipgen).doubleValue();
        //garoff
        parametriGare[19] = pgManager.calcolaGAROFF((Double)parametriGare[5],(Double)parametriGare[18],N_tipgen);
        //idiaut
        parametriGare[20] = pgManager.getContributoAutoritaStAppaltante((Double)parametriGare[5], "A1z01");
        //calcsoang
        parametriGare[21] = "2";
        //onsogrib
        parametriGare[22] = "1";

        this.sqlManager.update(insertGara, parametriGare);

        //Inserimento TORN
        Object parametriTorn[] = new Object[16];
        //codgar
        parametriTorn[0] = N_codgar;
        //cenint
        parametriTorn[1] = N_cenint;
        //tipgen
        parametriTorn[2] = N_tipgen;
        //offaum
        parametriTorn[3] = "2";
        //compreq
        parametriTorn[4] = "2";
        //istaut
        parametriTorn[5] = pgManager.getContributoAutoritaStAppaltante(
            (Double)parametriGare[5], "A1z02");
        //iterga
        parametriTorn[6] = pgManager.getITERGA((Long)parametriGare[3]);
        //codrup
        parametriTorn[7] = N_codrup;
        //numavcp
        parametriTorn[8] = null;
        //settore
        parametriTorn[9] = N_settore;
        //modrea
        parametriTorn[10] = N_modrea;
        //accqua
        parametriTorn[11] = new String ("2");
        //isadesione
        parametriTorn[12] = new String ("2");
        //altrisog
        parametriTorn[13] = altrisog;
        //prerib
        parametriTorn[14] = N_prerib;
        //cliv2
        parametriTorn[15] = syscon;

        this.sqlManager.update(insertTorn, parametriTorn);

        this.sqlManager.update(insertGare1, new Object[] {N_ngara,N_codgar});

        this.sqlManager.update(insertGarecont, new Object[] {N_ngara, new Long(1), N_ditta});

        Long numper = (Long) this.sqlManager.getObject( "select max(numper) from g_permessi", new Object[] {});
                numper =numper + new Long(1);

        this.sqlManager.update(insertG_permessi, new Object[] {numper, syscon, new Long(1), new Long(1), N_codgar});

        this.sqlManager.update(updateG1stipula, new Object[] {N_ngara, idStipula});

        //Inserimento in DITG
        Vector elencoCampi = new Vector();
        elencoCampi.add(new DataColumn("DITG.NGARA5",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, N_ngara)));
        elencoCampi.add(new DataColumn("DITG.CODGAR5",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, "$"+N_ngara)));
        elencoCampi.add(new DataColumn("DITG.DITTAO",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, N_ditta)));
        elencoCampi.add(new DataColumn("DITG.NOMIMO",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, N_nomima)));
        elencoCampi.add(new DataColumn("DITG.NPROGG",
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
        elencoCampi.add(new DataColumn("DITG.NUMORDPL",
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
        elencoCampi.add(new DataColumn("DITG.IMPOFF",
            new JdbcParametro(JdbcParametro.TIPO_DECIMALE, parametriGare[8])));
        elencoCampi.add(new DataColumn("DITG.INVOFF",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, "1")));
        elencoCampi.add(new DataColumn("DITG.IMPAPPD",
            new JdbcParametro(JdbcParametro.TIPO_DECIMALE, parametriGare[5])));
        elencoCampi.add(new DataColumn("DITG.RIBAUO",
            new JdbcParametro(JdbcParametro.TIPO_DECIMALE, parametriGare[13])));

        DataColumnContainer containerDITG = new DataColumnContainer(elencoCampi);
        GestoreDITG gestoreDITG = new GestoreDITG();
        gestoreDITG.setRequest(request);
        gestoreDITG.inserisci(status, containerDITG);

        ngaravar = N_ngara;

    }
    else {

    	//Se la gara è già presente la tengo allineata
        String updateGara = "update gare set not_gar=? , impapp=? , iaggiu=? " +
	    		" where ngara=?";
        String updateTorn = "update torn set tipgen=? , settore=? , cenint=? , codrup=? " +
	    		" where codgar=?";
        String updateDitg = "update ditg set impappd=? , impoff=? where ngara5=? and codgar5=? and dittao=?";

        N_ngara =  ngaravar;

        this.sqlManager.update(updateGara,new Object[] {oggetto,impstipula,impstipula,N_ngara});

        this.sqlManager.update(updateTorn, new Object[] {N_tipgen,N_settore,N_cenint,N_codrup,N_codgar});

        this.sqlManager.update(updateDitg, new Object[] {impstipula,impstipula,N_ngara, N_codgar, N_ditta});

    }

    //Gestione CPV principale, si deve prendere il primo cpv principale valorizzato fra tutti i lotti
    Vector<JdbcParametro> datiGaraPadre = this.sqlManager.getVector("select ncont,ngara from g1stipula where g1stipula.id = ?", new Object[] {id_padre});
    String ngaraPadre=null;
    if(datiGaraPadre!=null) {
      ncont= SqlManager.getValueFromVectorParam(datiGaraPadre, 0).longValue();
      ngaraPadre = SqlManager.getValueFromVectorParam(datiGaraPadre, 1).getStringValue();
    }
    this.sqlManager.update("delete from garcpv where ngara=?", new Object[] {N_ngara});

    String select="select  codcpv from garcpv where TIPCPV='1' and codcpv is not null and ngara in("
        + "  select GARE.NGARA "
        + "  from GARE "
        + "  join GARECONT on ((GARECONT.NGARA=GARE.NGARA AND GARECONT.NCONT=1) OR (GARECONT.NGARA=GARE.CODGAR1 AND (GARECONT.NGARAL IS NULL OR GARECONT.NGARAL=GARE.NGARA)))"
        + "  join TORN on TORN.CODGAR=GARE.CODGAR1 "
        + "  join GARE1 on GARE1.NGARA=GARE.NGARA WHERE GARECONT.NGARA = ? and GARECONT.NCONT = ? AND (GENERE IS NULL OR GENERE<>3))";
    String cpv = (String)sqlManager.getObject(select , new Object[] {ngaraPadre,ncont});
    if(cpv!=null)
      this.sqlManager.update("insert into garcpv(ngara,numcpv,codcpv,tipcpv) values(?,?,?,?)", new Object[] {N_ngara,new Long(1),cpv, "1"});

    }catch (Throwable e) {
        errore=true;
        N_codgar = null;
      }
    finally {
        if (status != null) {
          try {
            if (!errore)
              this.sqlManager.commitTransaction(status);
            else
              this.sqlManager.rollbackTransaction(status);
          } catch (SQLException e) {
          }
        }
      }
	if (logger.isDebugEnabled())
	          logger.debug("setCreaAggGaraCigCollegato: fine metodo");

	  return N_codgar;

  }


  private void dccW3GARA_AltriDatiGara(String tipoOperazione, DataColumnContainer dccW3GARA, List datiGara, Long numgara, Long syscon_remoto,
		  String cfein,String codgar,Long genere)  throws GestoreException, SQLException {

	if (logger.isDebugEnabled()) logger.debug("AnagraficaSIMOGManager.dccW3GARA_AltriDatiGara: inizio metodo");

	    //Oggetto
		String oggetto = (String) SqlManager.getValueFromVectorParam(datiGara,0).getValue();
		if (oggetto.length()>1024) {
		   	  oggetto=oggetto.substring(0, 1024);
		}
		dccW3GARA.setValue("W3GARA.OGGETTO",oggetto);

		// Tipo scheda
	    String settore = (String) SqlManager.getValueFromVectorParam(datiGara,6).getValue();
	    if(settore != null && "S".equals(settore)){
	         //Tipo_scheda;
	     	dccW3GARA.setValue("W3GARA.TIPO_SCHEDA", "S");
	    }else{
	       //Tipo_scheda;
	     	dccW3GARA.setValue("W3GARA.TIPO_SCHEDA", "O");
	    }

	    // Modo realizzazione
	    String modrea = (String) SqlManager.getValueFromVectorParam(datiGara,5).getValue();
	    if(modrea!=null) {
	      dccW3GARA.setValue("W3GARA.MODO_REALIZZAZIONE", modrea);
	    }

	    // Importo gara
	 	String selectImporto="select valmax from v_gare_importi where codgar=?";
	     if(genere==2)
	       selectImporto+=" and ngara is not null";
	     else
	       selectImporto+=" and ngara is null";
	     Double importoGara = null;
	     Object importoGaraObject = sqlManager.getObject(selectImporto, new Object[] { codgar });
	     if (importoGaraObject != null) {
	       if (importoGaraObject instanceof Long)
	         importoGara = new Double(((Long) importoGaraObject));
	       else if (importoGaraObject instanceof Double)
	         importoGara = new Double((Double) importoGaraObject);
	     }
	     if(importoGara!=null) {
	     	dccW3GARA.setValue("W3GARA.IMPORTO_GARA",importoGara.doubleValue());
	     }

	     // RUP
	     String codRUP = (String) SqlManager.getValueFromVectorParam(datiGara,2).getValue();
	     dccW3GARA.setValue("W3GARA.RUP_CODTEC",codRUP);
	     /*APPALTI-1063
	     // Gestione della collaborazione.Solo in Inserimento. Inserisco la collaborazione di default
	     //solo se la collaborazione associata al RUP indicato è una sola
	     if (codRUP != null && "INS".equals(tipoOperazione)) {
	         List<?> datiW3AZIENDAUFFICIO = sqlManager.getListVector(
		            "select w3aziendaufficio.id "
		                + "from w3aziendaufficio, w3usrsyscoll "
		                + "where w3aziendaufficio.id = w3usrsyscoll.w3aziendaufficio_id "
		                + "and w3usrsyscoll.syscon = ? and w3usrsyscoll.rup_codtec = ? and w3aziendaufficio.azienda_cf = ?",
		            new Object[] { syscon_remoto, codRUP, cfein });
			if (datiW3AZIENDAUFFICIO != null && datiW3AZIENDAUFFICIO.size() == 1) {
				Long collaborazione = (Long) SqlManager.getValueFromVectorParam(datiW3AZIENDAUFFICIO.get(0), 0).getValue();
				dccW3GARA.setValue("W3GARA.COLLABORAZIONE", collaborazione);
			}
		 }*/

	   	//Cig Accordo quadro - solo nel caso di adesione ad accordo quadro
        String isAdesione = (String) SqlManager.getValueFromVectorParam(datiGara,3).getValue();
        if (isAdesione != null && "1".equals(isAdesione)){
          String cigAccordoQuadro = (String) SqlManager.getValueFromVectorParam(datiGara,4).getValue();
          if (cigAccordoQuadro != null) {
        	  dccW3GARA.setValue("W3GARA.CIG_ACC_QUADRO", cigAccordoQuadro);
          }
        }

        //SOMMAURGENZA
        String sommaur = (String) SqlManager.getValueFromVectorParam(datiGara,8).getValue();
        if("1".equals(sommaur)) {
        	dccW3GARA.setValue("W3GARA.URGENZA_DL133","1");
        }else {
    		//default NO
    		dccW3GARA.setValue("W3GARA.URGENZA_DL133", "2");
        }

        //ESCLUSO_AVCPASS
       if ("INS".equals(tipoOperazione)) {
         dccW3GARA.setValue("W3GARA.ESCLUSO_AVCPASS", "2");
         if("9".equals(modrea) || "17".equals(modrea) || "18".equals(modrea)) {
  			dccW3GARA.setValue("W3GARA.ESCLUSO_AVCPASS", "1");
  		}
       }

        //DURATA ACCORDO QUADRO
		Long altrisog= (Long) SqlManager.getValueFromVectorParam(datiGara,10).getValue();//appunto per la cuc
        String accqua = (String) SqlManager.getValueFromVectorParam(datiGara,9).getValue();
        if("1".equals(accqua) || new Long(2).equals(altrisog) || new Long(3).equals(altrisog)){
          int tempoUtile=-1;
          if("1".equals(accqua)){
            Long aqdurata = (Long)SqlManager.getValueFromVectorParam(datiGara,
                11).getValue();
            Long aqtempo = (Long)SqlManager.getValueFromVectorParam(datiGara,
                12).getValue();

            if(aqdurata!=null){
                if(aqtempo != null) {
              	  if(aqtempo.longValue()==1) {
              		  tempoUtile= aqdurata.intValue() * 30;
              	  }else if(aqtempo.longValue()==3) {
              		  tempoUtile= aqdurata.intValue();
              	  }else {
              		  tempoUtile= aqdurata.intValue() * 365;
              	  }
                }else {
              	  tempoUtile= aqdurata.intValue() * 365;
                }
            }
          }else{
            tempoUtile = this.inviaVigilanzaManager.calcolaTempoUtile(codgar, genere.toString());
          }
          if(tempoUtile>0)
        	  dccW3GARA.setValue("W3GARA.DURATA_ACCQUADRO", Long.valueOf(tempoUtile));
        }

		  boolean inserisciCategorie = true;
		  BigInteger[] datiCategorie = new BigInteger[1];
		  datiCategorie[0] = new BigInteger("999");
		  // Confronto la lista delle categorie gia' memorizzate con quelle attuali
		  // Se ci sono differenze provvedo a cancellare le categorie presenti
		  // per poi inserirle nuovamente in blocco
		  String sqlSelectW3GARAMERC = "select categoria from W3GARAMERC where NUMGARA = ?";
		  List<?> datiW3GARAMERC = this.sqlManager.getListVector(sqlSelectW3GARAMERC, new Object[] { numgara });
		  if (datiW3GARAMERC != null && datiW3GARAMERC.size() > 0) {
			BigInteger[] datiCategorieW3GARAMERC = new BigInteger[datiW3GARAMERC.size()];
		    for (int i = 0; i < datiW3GARAMERC.size(); i++) {
		    	if (SqlManager.getValueFromVectorParam(datiW3GARAMERC.get(i), 0).getValue() != null) {
		    		datiCategorieW3GARAMERC[i] = new BigInteger(SqlManager.getValueFromVectorParam(datiW3GARAMERC.get(i), 0).getValue().toString());
		    	}
		    }
		    java.util.Arrays.sort(datiCategorieW3GARAMERC);
		    java.util.Arrays.sort(datiCategorie);
		    if (java.util.Arrays.equals(datiCategorieW3GARAMERC, datiCategorie)) {
		      inserisciCategorie = false;
		    } else {
		      inserisciCategorie = true;
		      String sqlDeleteW3GARAMERC = "delete from W3GARAMERC where NUMGARA = ?";
		      this.sqlManager.update(sqlDeleteW3GARAMERC, new Object[] { numgara });
		    }
		  }
		  // Inserimento di tutte le categorie
		  if (inserisciCategorie) {
		    if (datiCategorie != null && datiCategorie.length > 0) {
		      for (int j = 0; j < datiCategorie.length; j++) {
		        DataColumnContainer dccW3SMARTCIGMERC = new DataColumnContainer(new DataColumn[] { new DataColumn("W3GARAMERC.NUMGARA",
		            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numgara)) });
		        dccW3SMARTCIGMERC.addColumn("W3GARAMERC.NUMMERC", JdbcParametro.TIPO_NUMERICO, new Long(j + 1));
		        dccW3SMARTCIGMERC.addColumn("W3GARAMERC.CATEGORIA", JdbcParametro.TIPO_NUMERICO, datiCategorie[j].longValue());
		        dccW3SMARTCIGMERC.insert("W3GARAMERC", this.sqlManager);
		      }
		    }
		  }


	if (logger.isDebugEnabled()) logger.debug("AnagraficaSIMOGManager.dccW3GARA_AltriDatiGara: fine metodo");

}

  /**
   * Inserimento dei lotti associati alla gara
   *
   * @param datiGara
   * @param numgara
   * @throws GestoreException
   * @throws SQLException
   */
  private void gestioneW3LOTT(String codgar, Long numgara, Long genere, String tipoOp) throws GestoreException, SQLException {

    if (logger.isDebugEnabled()) logger.debug("AnagarficaSIMOGManager.gestioneW3LOTT: inizio metodo");
    String selectLottiGara = null;
    //separo per tipologia di gara
    switch (genere.intValue()) {
    case 1: // gara a lotto unico
    case 2: //gara a lotti con offerte distinte
        selectLottiGara = "select NGARA from GARE where GARE.CODGAR1 = ? and " +
		"(GARE.GENERE is null or GARE.GENERE <> 3) order by ngara";
        break;
    case 3://gara offerta unica
        selectLottiGara = "select NGARA from GARE where GARE.CODGAR1 = ? and " +
		"(GARE.GENERE is null or GARE.GENERE <> 3) and GARE.CODGAR1 <> GARE.NGARA order by ngara";
        break;
    }

    List datiListaLotti = this.sqlManager.getListVector(selectLottiGara, new Object[]{codgar});
    if (datiListaLotti != null && datiListaLotti.size() > 0) {
      for (int i = 0; i < datiListaLotti.size(); i++) {
  	    Vector<JdbcParametro> datiLotto = (Vector) datiListaLotti.get(i);
  		String numeroLotto =  datiLotto.get(0).getStringValue();

  		boolean esisteW3LOTT = false;
    	Long numlott = (Long) sqlManager.getObject("select numlott from w3lott where numgara=? and ngara = ? and stato_simog<>6", new Object[]{numgara,numeroLotto});
        if(numlott != null) {
   	   	 esisteW3LOTT = true;
   	    }

        // Il lotto non esiste di procede all'inserimento di un nuovo lotto
        if (!"DEL".equals(tipoOp) && !esisteW3LOTT) {
        	numlott = this.getNextValNUMLOTT(numgara);

          DataColumnContainer dccW3LOTT = new DataColumnContainer(new DataColumn[] { new DataColumn("W3LOTT.NUMGARA", new JdbcParametro(
              JdbcParametro.TIPO_NUMERICO, numgara)) });
          dccW3LOTT.addColumn("W3LOTT.NUMLOTT", JdbcParametro.TIPO_NUMERICO, numlott);

          // Identificativo Univoco Universale
          //dccW3LOTT.addColumn("W3LOTT.LOTTO_UUID", datiLotto.getUUID());
          dccW3LOTT.addColumn("W3LOTT.NGARA", numeroLotto);

          // Stato del lotto: nuovo lotto in attesa di richiesta, a SIMOG,
          // del codice identificativo CIG
          dccW3LOTT.addColumn("W3LOTT.STATO_SIMOG", JdbcParametro.TIPO_NUMERICO, Long.valueOf(1));

          // Aggiunto gli altri campi in modo da poterli utilizzare
          // nel metodo successivo
          dccW3LOTT.addColumn("W3LOTT.OGGETTO", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.SOMMA_URGENZA", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.TIPO_CONTRATTO", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.FLAG_ESCLUSO", JdbcParametro.TIPO_TESTO, "2");
          dccW3LOTT.addColumn("W3LOTT.CPV", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.ID_SCELTA_CONTRAENTE", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.IMPORTO_LOTTO", JdbcParametro.TIPO_DECIMALE, null);
          dccW3LOTT.addColumn("W3LOTT.IMPORTO_ATTUAZIONE_SICUREZZA", JdbcParametro.TIPO_DECIMALE, null);
          dccW3LOTT.addColumn("W3LOTT.ID_CATEGORIA_PREVALENTE", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.LUOGO_ISTAT", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.LUOGO_NUTS", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.TRIENNIO_ANNO_INIZIO", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.TRIENNIO_ANNO_FINE", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.TRIENNIO_PROGRESSIVO", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.FLAG_PNRR_PNC", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.FLAG_CUP", JdbcParametro.TIPO_TESTO, null);

          dccW3LOTT.addColumn("W3LOTT.ID_AFF_RISERVATI", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.ID_ESCLUSIONE", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.FLAG_REGIME", JdbcParametro.TIPO_TESTO, "2");
          dccW3LOTT.addColumn("W3LOTT.ART_REGIME", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.FLAG_DL50", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.PRIMA_ANNUALITA", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.ANNUALE_CUI_MININF", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.FLAG_PREVEDE_RIP", JdbcParametro.TIPO_TESTO, "2");
          dccW3LOTT.addColumn("W3LOTT.FLAG_RIPETIZIONE", JdbcParametro.TIPO_TESTO, "2");
          dccW3LOTT.addColumn("W3LOTT.CIG_ORIGINE_RIP", JdbcParametro.TIPO_TESTO, null);
          dccW3LOTT.addColumn("W3LOTT.MOTIVO_COLLEGAMENTO", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.DURATA_AFFIDAMENTO", JdbcParametro.TIPO_NUMERICO, null);
          dccW3LOTT.addColumn("W3LOTT.IMPORTO_OPZIONI", JdbcParametro.TIPO_DECIMALE, null);

          // Setto nel DataColumnContainer anche gli altri dati
          this.dccW3LOTT_AltriDatiLotto(dccW3LOTT, codgar, genere, numeroLotto, numgara, numlott,tipoOp);

          dccW3LOTT.insert("W3LOTT", this.sqlManager);

        }

        // Il lotto esiste gia' si procede all'aggiornamento
        if (esisteW3LOTT) {
	    	if("DEL".equals(tipoOp) && numgara!=null && numlott!=null) {
	              DataColumnContainer dccW3LOTT = new DataColumnContainer(this.sqlManager, "W3LOTT",
		                  "select * from w3lott where numgara = ? and numlott = ?", new Object[] { numgara, numlott });
	                dccW3LOTT.getColumn("W3LOTT.NUMGARA").setChiave(true);
	                dccW3LOTT.getColumn("W3LOTT.NUMLOTT").setChiave(true);
	                dccW3LOTT.setValue("W3LOTT.NUMGARA", numgara);
	                dccW3LOTT.setValue("W3LOTT.NUMLOTT", numlott);

	                dccW3LOTT.delete("W3LOTT", this.sqlManager);

	    	}else {
	            HashMap<String, Long> hMap = new HashMap<String, Long>();

	            // Il lotto deve essere in uno stato modificabile
	            if (this.isW3LOTT_Modificabile(numgara,numlott)) {

	              DataColumnContainer dccW3LOTT = new DataColumnContainer(this.sqlManager, "W3LOTT",
	                  "select * from w3lott where numgara = ? and numlott = ?", new Object[] { numgara, numlott });

	              // Aggiungo al DataColumnContainer anche gli altri dati comuni
	              boolean listaUlterioriCategorieModificata = this.dccW3LOTT_AltriDatiLotto(dccW3LOTT, codgar, genere, numeroLotto, numgara, numlott,tipoOp);

	              if (dccW3LOTT.isModifiedTable("W3LOTT") || listaUlterioriCategorieModificata == true) {
	                // Stato del lotto: lotto modifica in attesa di
	                // allineamento a SIMOG
	                Long stato_simog = dccW3LOTT.getLong("W3LOTT.STATO_SIMOG");
	                if (!new Long(1).equals(stato_simog)) {
	                  dccW3LOTT.setValue("W3LOTT.STATO_SIMOG", new Long(3));
	                }

	                dccW3LOTT.getColumn("W3LOTT.NUMGARA").setChiave(true);
	                dccW3LOTT.getColumn("W3LOTT.NUMLOTT").setChiave(true);
	                dccW3LOTT.setValue("W3LOTT.NUMGARA", numgara);
	                dccW3LOTT.setValue("W3LOTT.NUMLOTT", numlott);

	                dccW3LOTT.update("W3LOTT", this.sqlManager);

	              }
	            }

	    	}

        }
      }
    }

    if (logger.isDebugEnabled()) logger.debug("AnagraficaSIMOGManager.gestioneW3LOTT: fine metodo");

  }


  /**
   * Gestione dei dati comuni per l'inserimento e l'aggiornamento in W3LOTT
   *
   * @param dccW3LOTT
   * @param datiLotto
   * @param numgara
   * @param numlott
   * @param tipoOp
   * @throws GestoreException
   * @throws SQLException
   */
  private boolean dccW3LOTT_AltriDatiLotto(DataColumnContainer dccW3LOTT, String codgar, Long genere, String numeroLotto, Long numgara, Long numlott,
      String tipoOp)
      throws GestoreException, SQLException {

    if (logger.isDebugEnabled()) logger.debug("AnagraficaSIMOGManager.dccW3LOTT_AltriDatiLotto: inizio metodo");

    boolean isListaUlterioriCategorieModificata = false;
    boolean isListaUlterioriCUPModificata = false;
    boolean isListaUlterioriTipologieLavoroModificata = false;
    boolean isListaUlterioriTipologieFornituraModificata = false;
    boolean isListaUlterioriCondizioniModificata = false;

    String selectDatiLotto = "select g.ngara,g.codcig,g.not_gar,t.tipgen,g.tipgarg,g.impapp,g.impsic," +
            "t.codnuts, t.sommaur, t.tiplav, g.tiplav, t.oggcont, g.oggcont, g.locint, g.cupprg, g.tipneg, g1.codcui, g1.annint,"
            + " g.temesi, g.teutil, t.accqua, t.aqdurata, t.aqtempo, g1.imprin, g1.imppror, g1.impaltro, g1.impserv,"
            + " g1.ammrin, g1.ammopz, t.ispnrr"
            + " from gare g,torn t, gare1 g1 where g.codgar1= t.codgar and g.ngara=g1.ngara and g.ngara = ?";

    List datiLotto = sqlManager.getVector(selectDatiLotto, new Object[] { numeroLotto });
	if (datiLotto != null && datiLotto.size() > 0) {
      //oggettoLotto
      String oggettoLotto = (String) SqlManager.getValueFromVectorParam(datiLotto,2).getValue();
      if (oggettoLotto.length()>1024) {
    	  oggettoLotto=oggettoLotto.substring(0, 1024);
      }
      dccW3LOTT.setValue("W3LOTT.OGGETTO", oggettoLotto);

      //sommaUrgenza
      String sommaUrgenza = (String) SqlManager.getValueFromVectorParam(datiLotto,8).getValue();
      if("1".equals(sommaUrgenza)) {
    	  dccW3LOTT.setValue("W3LOTT.SOMMA_URGENZA", "1");
      }else {
    	  dccW3LOTT.setValue("W3LOTT.SOMMA_URGENZA", "2");
      }

      //tipoContratto (Lavori,Forniture,Servizi)
      Long tipoContratto = (Long) SqlManager.getValueFromVectorParam(datiLotto,3).getValue();
      if (tipoContratto != null) {
    	    switch (tipoContratto.intValue()) {
    	    case 1: //lavori
    	    	dccW3LOTT.setValue("W3LOTT.TIPO_CONTRATTO", "L");
    	    	break;
    	    case 2: //forniture
    	    	dccW3LOTT.setValue("W3LOTT.TIPO_CONTRATTO", "F");
    	    	break;
    	    case 3: //servizi
    	    	dccW3LOTT.setValue("W3LOTT.TIPO_CONTRATTO", "S");
    	    	break;
    	    }
      }

      //In inserimento dell'anagrafica il campo tipoOp non è valorizzato. Solo in inserimento
      //va inizializzato il campo
      if("".equals(tipoOp))
        dccW3LOTT.setValue("W3LOTT.FLAG_ESCLUSO", "2");

      Object param[] = new Object[1];
      if(genere.longValue()==3){
        param[0] = codgar;
      }else{
        param[0] = numeroLotto;
      }

      //codice CPV
      //nel caso di offerta unica il codice CPV è associato alla gara fittizia
      String codiceCPV = (String) sqlManager.getObject(
              "select codcpv from garcpv where ngara = ? and TIPCPV='1'",new Object[]{numeroLotto});
      if (codiceCPV != null) {
    	  dccW3LOTT.setValue("W3LOTT.CPV", codiceCPV);
      }

      //tipo Procedura
      Long idTipoProcedura = (Long) SqlManager.getValueFromVectorParam(datiLotto,4).getValue();
      String proceduraScelta= inviaVigilanzaManager.getFromTab2("A1z11", idTipoProcedura,false);
      dccW3LOTT.setValue("W3LOTT.ID_SCELTA_CONTRAENTE", Long.valueOf(proceduraScelta));

      // Importo Lotto
      String selectImportoLotto="select valmax from v_gare_importi where ngara=?";
      Double importoLotto=null;
      Object importoLottoObject = sqlManager.getObject(selectImportoLotto, new Object[] { numeroLotto });
      if (importoLottoObject != null) {
        if (importoLottoObject instanceof Long)
          importoLotto = new Double(((Long) importoLottoObject));
        else if (importoLottoObject instanceof Double)
          importoLotto = new Double((Double) importoLottoObject);
      }
      //Double importoLotto = (Double) SqlManager.getValueFromVectorParam(datiLotto,5).getValue();
      if (importoLotto != null) {
      	dccW3LOTT.setValue("W3LOTT.IMPORTO_LOTTO", importoLotto.doubleValue());
      }else {
        //throw new Exception("Lotto " + numeroLotto + ": " + CTR_LOTTO_IMPORTO);
      }

      // Importo sicurezza Lotto
      Double importoSicurezzaLotto = (Double) SqlManager.getValueFromVectorParam(datiLotto,6).getValue();
      if (importoSicurezzaLotto != null) {
      	dccW3LOTT.setValue("W3LOTT.IMPORTO_ATTUAZIONE_SICUREZZA", importoSicurezzaLotto.doubleValue());
      }

      //DURATA AFFIDAMENTO
      String accqua = (String) SqlManager.getValueFromVectorParam(datiLotto,20).getValue();
      int tempoUtile=-1;
      if("1".equals(accqua)){
          Long aqdurata = (Long)SqlManager.getValueFromVectorParam(datiLotto,21).getValue();
          Long aqtempo = (Long)SqlManager.getValueFromVectorParam(datiLotto,22).getValue();
          if(aqdurata!=null){
              if(aqtempo != null) {
            	  if(aqtempo.longValue()==1) {
            		  tempoUtile= aqdurata.intValue() * 30;
            	  }else if(aqtempo.longValue()==3) {
            		  tempoUtile= aqdurata.intValue();
            	  }else {
            		  tempoUtile= aqdurata.intValue() * 365;
            	  }
              }else {
            	  tempoUtile= aqdurata.intValue() * 365;
              }
          }
      }else {
    	  if(Long.valueOf(1).equals(genere)) {
    		  Long temesi = (Long)SqlManager.getValueFromVectorParam(datiLotto,18).getValue();
    		  Long teutil = (Long)SqlManager.getValueFromVectorParam(datiLotto,19).getValue();
    		    if(teutil!=null){
    		        tempoUtile = teutil.intValue();
    		        if(temesi!=null && temesi.longValue()==2)
    		          tempoUtile*=30;
    		      }
    	  }else {
    		  tempoUtile = this.inviaVigilanzaManager.calcolaTempoUtile(codgar, genere.toString());
    	  }
      }
      if(tempoUtile>0) {
    	  dccW3LOTT.setValue("W3LOTT.DURATA_AFFIDAMENTO", Long.valueOf(tempoUtile));
      }

      //Contratto d'appalto oggetto di rinnovo?
      String ammrin = (String) SqlManager.getValueFromVectorParam(datiLotto,27).getValue();
      //Ricorso a opzioni?
      String ammopz = (String) SqlManager.getValueFromVectorParam(datiLotto,28).getValue();

      if("1".equals(ammrin) || "1".equals(ammopz)) {
    	  dccW3LOTT.setValue("W3LOTT.FLAG_PREVEDE_RIP", "1");
      }else {
    	  dccW3LOTT.setValue("W3LOTT.FLAG_PREVEDE_RIP", "2");
      }

      //IMPORTO per OPZIONI/RIPETIZIONI
      Double importoRinnovi = (Double) SqlManager.getValueFromVectorParam(datiLotto,23).getValue();
      if(importoRinnovi == null) {
    	  importoRinnovi =Double.valueOf(0);
      }
      Double importoProroghe = (Double) SqlManager.getValueFromVectorParam(datiLotto,24).getValue();
      if(importoProroghe == null) {
    	  importoProroghe =Double.valueOf(0);
      }
      Double importoOpzioni = (Double) SqlManager.getValueFromVectorParam(datiLotto,25).getValue();
      if(importoOpzioni == null) {
    	  importoOpzioni =Double.valueOf(0);
      }
      Double importoServizi = (Double) SqlManager.getValueFromVectorParam(datiLotto,26).getValue();
      if(importoServizi == null) {
    	  importoServizi =Double.valueOf(0);
      }

      Double importoOpzioniRipetizioni = importoRinnovi + importoProroghe + importoOpzioni + importoServizi;
      importoOpzioniRipetizioni = (Double)UtilityNumeri.arrotondaNumero(importoOpzioniRipetizioni, Integer.valueOf(2));
      if(importoOpzioniRipetizioni.doubleValue()>0) {
    	  dccW3LOTT.setValue("W3LOTT.IMPORTO_OPZIONI", importoOpzioniRipetizioni);
      }

      //nel caso di gara ad offerta unica si devono considerare le categorie associate alla gara fittizia

    	//codice Categoria Prevalente
      Vector<JdbcParametro> datiCategoriaPrevalente = sqlManager.getVector("select cg.catiga,cs.tiplavg  " +
          " from catg cg,cais cs where cg.catiga = cs.caisim and cg.ncatg = 1 " +
          "  and cg.ngara = ?", param);
      String codiceCategoriaPrevalente = null;
      if(datiCategoriaPrevalente!=null && datiCategoriaPrevalente.size()>0){
    	  Long tiplavg = datiCategoriaPrevalente.get(1).longValue();
        codiceCategoriaPrevalente = datiCategoriaPrevalente.get(0).getStringValue();
    	  if(tiplavg== null )
    	    tiplavg= new Long(0);
        if(codiceCategoriaPrevalente!= null && !"".equals(codiceCategoriaPrevalente)){
          if(tipoContratto.longValue()==2)
            codiceCategoriaPrevalente="FB";
          else if(tipoContratto.longValue()==3)
            codiceCategoriaPrevalente="FS";
          else{
            //Gara per lavori
            if((new Long(2)).equals(tiplavg))
              codiceCategoriaPrevalente="FB";
            else if((new Long(3)).equals(tiplavg) || (new Long(5)).equals(tiplavg))
              codiceCategoriaPrevalente="FS";
            else if((new Long(4)).equals(tiplavg))
              codiceCategoriaPrevalente="AA";
            else if (codiceCategoriaPrevalente.length()>5 || !(codiceCategoriaPrevalente.startsWith("OG") || codiceCategoriaPrevalente.startsWith("OS")))
              codiceCategoriaPrevalente="AA";
          }
        }
        if (codiceCategoriaPrevalente != null) {
            dccW3LOTT.setValue("W3LOTT.ID_CATEGORIA_PREVALENTE", codiceCategoriaPrevalente);
      	}
      }

      //Categorie Ulteriori
      String selectCategorieLotto = "select op.catoff, cs.tiplavg from opes op,cais cs" +
          " where op.catoff = cs.caisim and op.ngara3 = ? ";
      List listaCategorieLotto = this.sqlManager.getListVector(selectCategorieLotto, param);
      if(listaCategorieLotto != null && listaCategorieLotto.size() > 0){
    	  	boolean inserisciCategorie = true;
    	  	String[] datiCategorie = new String[listaCategorieLotto.size()];
	        for(int i=0; i < listaCategorieLotto.size(); i++){
	          String codiceCategoria="";
	          Vector<JdbcParametro> tmpVect = (Vector) listaCategorieLotto.get(i);
	           codiceCategoria = tmpVect.get(0).getStringValue();
	           Long tiplavg = tmpVect.get(1).longValue();
  				if(tiplavg== null )
  	              tiplavg= new Long(0);
  	          if(codiceCategoria!= null && !"".equals(codiceCategoria)){
	            if(tipoContratto.longValue()==2)
	              codiceCategoria="FB";
	            else if(tipoContratto.longValue()==3)
	              codiceCategoria="FS";
	            else{
	              //Gara per lavori
	              if((new Long(2)).equals(tiplavg))
	                codiceCategoria="FB";
	              else if((new Long(3)).equals(tiplavg) || (new Long(5)).equals(tiplavg))
	                codiceCategoria="FS";
	              else if((new Long(4)).equals(tiplavg))
	                codiceCategoria="AA";
	              else if (codiceCategoria.length()>5 || !(codiceCategoria.startsWith("OG") || codiceCategoria.startsWith("OS")))
	                codiceCategoria="AA";
  	            }
  	          }

  	          	datiCategorie[i] = codiceCategoria;
	        }

	        // Confronto la lista delle categorie gia' memorizzate con quelle attuali
	        // Se ci sono differenze provvedo a cancellare le categorie presenti
	        // per poi inserirle nuovamente in blocco
	        String sqlSelectW3LOTTCATE = "select categoria from w3lottcate where numgara = ? and numlott = ?";
	        List<?> datiW3LOTTCATE = this.sqlManager.getListVector(sqlSelectW3LOTTCATE, new Object[] { numgara, numlott });
	        if (datiW3LOTTCATE != null && datiW3LOTTCATE.size() > 0) {

	          String[] datiCategorieW3LOTTCATE = new String[datiW3LOTTCATE.size()];
	          for (int i = 0; i < datiW3LOTTCATE.size(); i++) {
	            datiCategorieW3LOTTCATE[i] = (String) SqlManager.getValueFromVectorParam(datiW3LOTTCATE.get(i), 0).getValue();
	          }
	          java.util.Arrays.sort(datiCategorieW3LOTTCATE);
	          java.util.Arrays.sort(datiCategorie);

	          if (java.util.Arrays.equals(datiCategorieW3LOTTCATE, datiCategorie)) {
	            inserisciCategorie = false;
	          } else {
	            isListaUlterioriCategorieModificata = true;
	            inserisciCategorie = true;
	            String sqlDeleteW3LOTTCATE = "delete from w3lottcate where numgara = ? and numlott = ?";
	            this.sqlManager.update(sqlDeleteW3LOTTCATE, new Object[] { numgara, numlott });
	          }

	        }
	        // Inserimento di tutte le categorie
	        if (inserisciCategorie) {
	          if (datiCategorie != null && datiCategorie.length > 0) {
	            for (int j = 0; j < datiCategorie.length; j++) {
	              DataColumnContainer dccW3LOTTCATE = new DataColumnContainer(new DataColumn[] { new DataColumn("W3LOTTCATE.NUMGARA",
	                  new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numgara)) });
	              dccW3LOTTCATE.addColumn("W3LOTTCATE.NUMLOTT", JdbcParametro.TIPO_NUMERICO, numlott);
	              dccW3LOTTCATE.addColumn("W3LOTTCATE.NUMCATE", JdbcParametro.TIPO_NUMERICO, new Long(j + 1));
	              dccW3LOTTCATE.addColumn("W3LOTTCATE.CATEGORIA", JdbcParametro.TIPO_TESTO, datiCategorie[j]);
	              dccW3LOTTCATE.insert("W3LOTTCATE", this.sqlManager);
	            }
	          }
	        }
      }

      //Luogo istat
      String locint = (String)SqlManager.getValueFromVectorParam(datiLotto,13).getValue();
      if(genere.intValue()==3)
        locint=(String)this.sqlManager.getObject("select locint from gare where ngara=?", new Object[]{codgar});
      if(locint!=null){
        dccW3LOTT.setValue("W3LOTT.LUOGO_ISTAT", locint);
      }

      locint = StringUtils.stripToEmpty(locint);
      if("".equals(locint)) {
          //codice NUTS Lotto
          String codiceNUTS = (String) SqlManager.getValueFromVectorParam(datiLotto,7).getValue();
          if (codiceNUTS != null && !"".equals(codiceNUTS)) {
        	dccW3LOTT.setValue("W3LOTT.LUOGO_NUTS", codiceNUTS);
          }
      }
      
      //ispnrr
      String ispnrr = (String)SqlManager.getValueFromVectorParam(datiLotto,29).getValue();
      if(ispnrr == null) {
    	dccW3LOTT.setValue("W3LOTT.FLAG_PNRR_PNC", null);
      } else if("1".equals(ispnrr)) {
        dccW3LOTT.setValue("W3LOTT.FLAG_PNRR_PNC", "1");
      } else if("2".equals(ispnrr)) {
        dccW3LOTT.setValue("W3LOTT.FLAG_PNRR_PNC", "2");
      }

      //cupprg
      String cupprg = (String)SqlManager.getValueFromVectorParam(datiLotto,14).getValue();
      if(cupprg!=null) {
    	  dccW3LOTT.setValue("W3LOTT.FLAG_CUP", "1");
      }else {
    	  dccW3LOTT.setValue("W3LOTT.FLAG_CUP", "2");
      }

      //Lista CUP
      if(cupprg!=null){
          boolean inserisciCUP = true;

          String[] datiCUP = new String[1];
          datiCUP[0] = cupprg;

          // Confronto la lista dei cup gia' memorizzati con quelli attuali
          // Se ci sono differenze provvedo a cancellare i CUP presenti
          // per poi inserirli nuovamente in blocco
          String sqlSelectW3LOTTCUP = "select CUP from W3LOTTCUP where numgara = ? and numlott = ?";
          List<?> datiW3LOTTCUP = this.sqlManager.getListVector(sqlSelectW3LOTTCUP, new Object[] { numgara, numlott });
          if (datiW3LOTTCUP != null && datiW3LOTTCUP.size() > 0) {

            String[] datiCUPW3LOTTCUP = new String[datiW3LOTTCUP.size()];
            for (int i = 0; i < datiW3LOTTCUP.size(); i++) {
            	datiCUPW3LOTTCUP[i] = (String) SqlManager.getValueFromVectorParam(datiW3LOTTCUP.get(i), 0).getValue();
            }
            java.util.Arrays.sort(datiCUPW3LOTTCUP);
            java.util.Arrays.sort(datiCUP);

            if (java.util.Arrays.equals(datiCUPW3LOTTCUP, datiCUP)) {
            	inserisciCUP = false;
            } else {
              isListaUlterioriCUPModificata = true;
              inserisciCUP = true;
              String sqlDeleteW3LOTTCUP = "delete from W3LOTTCUP where numgara = ? and numlott = ?";
              this.sqlManager.update(sqlDeleteW3LOTTCUP, new Object[] { numgara, numlott });
            }

          }

          // Inserimento di tutti i CUP
          if (inserisciCUP) {
            if (datiCUP != null && datiCUP.length > 0) {
              for (int j = 0; j < datiCUP.length; j++) {
                DataColumnContainer dccW3LOTTCUP = new DataColumnContainer(new DataColumn[] { new DataColumn("W3LOTTCUP.NUMGARA",
                    new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numgara)) });
                dccW3LOTTCUP.addColumn("W3LOTTCUP.NUMLOTT", JdbcParametro.TIPO_NUMERICO, numlott);
                dccW3LOTTCUP.addColumn("W3LOTTCUP.NUMCUP", JdbcParametro.TIPO_NUMERICO, new Long(j + 1));
                dccW3LOTTCUP.addColumn("W3LOTTCUP.CUP", JdbcParametro.TIPO_TESTO, datiCUP[j]);
                dccW3LOTTCUP.insert("W3LOTTCUP", this.sqlManager);
              }
            }
          }
      }


      //Tipologia lavoro
      Long tiplav=null;
      if(genere.longValue()==3)
        tiplav = (Long)SqlManager.getValueFromVectorParam(datiLotto,9).getValue();
      else
        tiplav = (Long)SqlManager.getValueFromVectorParam(datiLotto,10).getValue();
      if(Long.valueOf(1).equals(tipoContratto) && tiplav!=null){

          boolean inserisciTipologiaLavoro = true;

          BigInteger[] datiTipologiaLavoro = new BigInteger[1];
          datiTipologiaLavoro[0] = new BigInteger(tiplav.toString());
          // Confronto la lista delle tipologie gia' memorizzate con quelle attuali
          // Se ci sono differenze provvedo a cancellare le tipologie presenti
          // per poi inserirle nuovamente in blocco
          String sqlSelectW3LOTTTIPI = "select IDAPPALTO from W3LOTTTIPI where numgara = ? and numlott = ?";
          List<?> datiW3LOTTTIPI = this.sqlManager.getListVector(sqlSelectW3LOTTTIPI, new Object[] { numgara, numlott });
          if (datiW3LOTTTIPI != null && datiW3LOTTTIPI.size() > 0) {

        	BigInteger[] datiTipologieW3LOTTTIPI = new BigInteger[datiW3LOTTTIPI.size()];
            for (int i = 0; i < datiW3LOTTTIPI.size(); i++) {
            	if (SqlManager.getValueFromVectorParam(datiW3LOTTTIPI.get(i), 0).getValue() != null) {
            		datiTipologieW3LOTTTIPI[i] = new BigInteger(SqlManager.getValueFromVectorParam(datiW3LOTTTIPI.get(i), 0).getValue().toString());
            	}
            }
            java.util.Arrays.sort(datiTipologieW3LOTTTIPI);
            java.util.Arrays.sort(datiTipologiaLavoro);

            if (java.util.Arrays.equals(datiTipologieW3LOTTTIPI, datiTipologiaLavoro)) {
            	inserisciTipologiaLavoro = false;
            } else {
            	isListaUlterioriTipologieLavoroModificata = true;
            	inserisciTipologiaLavoro = true;
            	String sqlDeleteW3LOTTTIPI = "delete from W3LOTTTIPI where numgara = ? and numlott = ?";
            	this.sqlManager.update(sqlDeleteW3LOTTTIPI, new Object[] { numgara, numlott });
            }

          }

          // Inserimento di tutte le tipologie
          if (inserisciTipologiaLavoro) {
            if (datiTipologiaLavoro != null && datiTipologiaLavoro.length > 0) {
              for (int j = 0; j < datiTipologiaLavoro.length; j++) {
                DataColumnContainer dccW3LOTTTIPI = new DataColumnContainer(new DataColumn[] { new DataColumn("W3LOTTTIPI.NUMGARA",
                    new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numgara)) });
                dccW3LOTTTIPI.addColumn("W3LOTTTIPI.NUMLOTT", JdbcParametro.TIPO_NUMERICO, numlott);
                dccW3LOTTTIPI.addColumn("W3LOTTTIPI.NUMTIPI", JdbcParametro.TIPO_NUMERICO, new Long(j + 1));
                dccW3LOTTTIPI.addColumn("W3LOTTTIPI.IDAPPALTO", JdbcParametro.TIPO_NUMERICO, datiTipologiaLavoro[j].longValue());
                dccW3LOTTTIPI.insert("W3LOTTTIPI", this.sqlManager);
              }
            }
          }
      }


      //Tipologia fornitura
      Long oggcont=null;
      if(genere.longValue()==3)
        oggcont = (Long)SqlManager.getValueFromVectorParam(datiLotto,11).getValue();
      else
        oggcont = (Long)SqlManager.getValueFromVectorParam(datiLotto,12).getValue();
      if((new Long(2)).equals(tipoContratto) && oggcont!=null){
        oggcont = new Long(oggcont.longValue() - 9);
        boolean inserisciTipologiaFornitura = true;

        BigInteger[] datiTipologiaFornitura = new BigInteger[1];
        datiTipologiaFornitura[0] = new BigInteger(oggcont.toString());

        // Confronto la lista delle tipologie gia' memorizzate con quelle attuali
        // Se ci sono differenze provvedo a cancellare le tipologie presenti
        // per poi inserirle nuovamente in blocco
        String sqlSelectW3LOTTTIPI = "select IDAPPALTO from W3LOTTTIPI where numgara = ? and numlott = ?";
        List<?> datiW3LOTTTIPI = this.sqlManager.getListVector(sqlSelectW3LOTTTIPI, new Object[] { numgara, numlott });
        if (datiW3LOTTTIPI != null && datiW3LOTTTIPI.size() > 0) {

      	BigInteger[] datiTipologieW3LOTTTIPI = new BigInteger[datiW3LOTTTIPI.size()];
          for (int i = 0; i < datiW3LOTTTIPI.size(); i++) {
          	if (SqlManager.getValueFromVectorParam(datiW3LOTTTIPI.get(i), 0).getValue() != null) {
          		datiTipologieW3LOTTTIPI[i] = new BigInteger(SqlManager.getValueFromVectorParam(datiW3LOTTTIPI.get(i), 0).getValue().toString());
          	}
          }
          java.util.Arrays.sort(datiTipologieW3LOTTTIPI);
          java.util.Arrays.sort(datiTipologiaFornitura);

          if (java.util.Arrays.equals(datiTipologieW3LOTTTIPI, datiTipologiaFornitura)) {
          	inserisciTipologiaFornitura = false;
          } else {
          	isListaUlterioriTipologieFornituraModificata = true;
          	inserisciTipologiaFornitura = true;
          	String sqlDeleteW3LOTTTIPI = "delete from W3LOTTTIPI where numgara = ? and numlott = ?";
          	this.sqlManager.update(sqlDeleteW3LOTTTIPI, new Object[] { numgara, numlott });
          }
        }

        // Inserimento di tutte le tipologie
        if (inserisciTipologiaFornitura) {
          if (datiTipologiaFornitura != null && datiTipologiaFornitura.length > 0) {
            for (int j = 0; j < datiTipologiaFornitura.length; j++) {
              DataColumnContainer dccW3LOTTTIPI = new DataColumnContainer(new DataColumn[] { new DataColumn("W3LOTTTIPI.NUMGARA",
                  new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numgara)) });
              dccW3LOTTTIPI.addColumn("W3LOTTTIPI.NUMLOTT", JdbcParametro.TIPO_NUMERICO, numlott);
              dccW3LOTTTIPI.addColumn("W3LOTTTIPI.NUMTIPI", JdbcParametro.TIPO_NUMERICO, new Long(j + 1));
              dccW3LOTTTIPI.addColumn("W3LOTTTIPI.IDAPPALTO", JdbcParametro.TIPO_NUMERICO, datiTipologiaFornitura[j].longValue());
              dccW3LOTTTIPI.insert("W3LOTTTIPI", this.sqlManager);
            }
          }
        }
      }

      //TIPNEG
      Long tipneg = (Long)SqlManager.getValueFromVectorParam(datiLotto,15).getValue();
      if(tipneg != null && tipneg.longValue() > 32 && tipneg.longValue() < 44 ){

          boolean inserisciCondizione = true;

          BigInteger[] datiCondizione = new BigInteger[1];
          datiCondizione[0] = new BigInteger(tipneg.toString());
          // Confronto la lista delle condizioni gia' memorizzate con quelle attuali
          // Se ci sono differenze provvedo a cancellare le condizioni presenti
          // per poi inserirle nuovamente in blocco
          String sqlSelectW3COND = "select ID_CONDIZIONE from W3COND where numgara = ? and numlott = ?";
          List<?> datiW3COND = this.sqlManager.getListVector(sqlSelectW3COND, new Object[] { numgara, numlott });
          if (datiW3COND != null && datiW3COND.size() > 0) {

        	  BigInteger[] datiCondizioniW3COND = new BigInteger[datiW3COND.size()];
        	  for (int i = 0; i < datiW3COND.size(); i++) {
        		  datiCondizioniW3COND[i] = new BigInteger(SqlManager.getValueFromVectorParam(datiW3COND.get(i), 0).getValue().toString());
        	  }
            java.util.Arrays.sort(datiCondizioniW3COND);
            java.util.Arrays.sort(datiCondizione);

            if (java.util.Arrays.equals(datiCondizioniW3COND, datiCondizione)) {
            	inserisciCondizione = false;
            } else {
            	isListaUlterioriCondizioniModificata = true;
            	inserisciCondizione = true;
            	String sqlDeleteW3COND = "delete from W3COND where numgara = ? and numlott = ?";
            	this.sqlManager.update(sqlDeleteW3COND, new Object[] { numgara, numlott });
            }

          }

          // Inserimento di tutte le condizioni
          if (inserisciCondizione) {
            if (datiCondizione != null && datiCondizione.length > 0) {
              for (int j = 0; j < datiCondizione.length; j++) {
                DataColumnContainer dccW3COND = new DataColumnContainer(new DataColumn[] { new DataColumn("W3COND.NUMGARA",
                    new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numgara)) });
                dccW3COND.addColumn("W3COND.NUMLOTT", JdbcParametro.TIPO_NUMERICO, numlott);
                dccW3COND.addColumn("W3COND.NUM_COND", JdbcParametro.TIPO_NUMERICO, new Long(j + 1));
                dccW3COND.addColumn("W3COND.ID_CONDIZIONE", JdbcParametro.TIPO_NUMERICO, datiCondizione[j].longValue());
                dccW3COND.insert("W3COND", this.sqlManager);
              }
            }
          }
      }

      //Programmazione
      String codcui=(String)SqlManager.getValueFromVectorParam(datiLotto,16).getValue();
      if(codcui!=null){
    	dccW3LOTT.setValue("W3LOTT.FLAG_DL50", "1");
    	dccW3LOTT.setValue("W3LOTT.ANNUALE_CUI_MININF", codcui);
      }else
    	dccW3LOTT.setValue("W3LOTT.FLAG_DL50", "2");

      Long annint = (Long)SqlManager.getValueFromVectorParam(datiLotto,17).getValue();
      if(annint!=null){
    	  dccW3LOTT.setValue("W3LOTT.PRIMA_ANNUALITA", annint);
      }

      //dccW3LOTT.setValue("W3LOTT.FLAG_PREVEDE_RIP", "2");

      dccW3LOTT.setValue("W3LOTT.FLAG_RIPETIZIONE", "2");

	}//if datiLotto



    if (logger.isDebugEnabled()) logger.debug("AnagraficaSIMOGManager.dccW3LOTT_AltriDatiLotto: fine metodo");

    return isListaUlterioriCategorieModificata || isListaUlterioriCUPModificata ||
    isListaUlterioriTipologieLavoroModificata || isListaUlterioriTipologieFornituraModificata ||
    isListaUlterioriCondizioniModificata;

  }


  /**
   * Restituisce la chiave dell'Anagrafica Simog (Smart CIG) creata o aggiornata
   * @param codgar
   * @param ngara
   * @param genereString
   * @return String
   * @throws SQLException
   * @throws IOException
   */
  public HashMap<String,Object> setAnagraficaSmartCig(String codgar, String genereString, String tipoOp, String idStipula)
  	throws SQLException,IOException, Exception {
	    if (logger.isDebugEnabled())
	        logger.debug("setAnagraficaSmartCig: inizio metodo");

	    HashMap<String,Object> hm = new HashMap<String,Object>();


	    String selectDatiGara = "select "
	            + " gare.not_gar, "     //0
	            + " gare.codcig, "      //1
	            + " torn.tipgen, "      //2
	            + " torn.isadesione, "  //3
	            + " torn.codcigaq, "    //4
	            + " gare.cupprg, "      //5
	            + " torn.codrup, "      //6
	            + " gare.tipgarg, "     //7
	            + " uffint.cfein, "     //8
	            + " uffint.iscuc, "     //9
	            + " uffint.cfanac, "    //10
	            + " torn.altrisog, "    //11
            	+ " torn.cenint "       //12
	            + " from torn, gare,uffint where torn.codgar = gare.codgar1 "
	            + " and torn.codgar = ? and torn.cenint = uffint.codein";


        List datiGara = sqlManager.getVector(selectDatiGara,new Object[] { codgar });
        if (datiGara != null && datiGara.size() > 0) {

        	boolean esisteW3SMARTCIG = false;
	        Long codrich = null;
	        codrich = (Long) sqlManager.getObject("select codrich from w3smartcig where codgar = ? and stato<>6", new Object[]{codgar});
	        if(codrich != null) {
	        	esisteW3SMARTCIG = true;
	        }


	        String codRUP = (String) SqlManager.getValueFromVectorParam(datiGara,6).getValue();
	        /*APPALTI-1063
	        Long syscon = (Long) sqlManager.getObject("select syscon from w3usrsys where rup_codtec=? order by syscon", new Object[] { codRUP });
	        */
	        String codein = (String) SqlManager.getValueFromVectorParam(datiGara,12).getValue();

	        //Codice fiscale stazione appaltante
	        String cfein = null;
	        Long altrisog= (Long) SqlManager.getValueFromVectorParam(datiGara,11).getValue();
	        String iscuc = (String) SqlManager.getValueFromVectorParam(datiGara,9).getValue();
	        if("1".equals(iscuc) && (new Long(2).equals(altrisog) || new Long(3).equals(altrisog))){
	          cfein = (String) SqlManager.getValueFromVectorParam(datiGara,10).getValue();
	          if(cfein==null || "".equals(cfein))
	            cfein = (String) SqlManager.getValueFromVectorParam(datiGara,8).getValue();
	        }else
	          cfein = (String) SqlManager.getValueFromVectorParam(datiGara,8).getValue();


            if (!esisteW3SMARTCIG) {
              // LO SMARTCIG NON ESISTE: SI PROCEDE A CREARNE UNO NUOVO

              // Calcolo della chiave
              codrich = Long.valueOf(genChiaviManager.getNextId("W3SMARTCIG"));

              DataColumnContainer dccW3SMARTCIG = new DataColumnContainer(new DataColumn[] { new DataColumn("W3SMARTCIG.CODRICH", new JdbcParametro(
                  JdbcParametro.TIPO_NUMERICO, codrich)) });
              //APPALTI-1063
              // Utente proprietario
              //dccW3SMARTCIG.addColumn("W3SMARTCIG.SYSCON", JdbcParametro.TIPO_NUMERICO, syscon);

              // Identificativo Univoco Universale
              dccW3SMARTCIG.addColumn("W3SMARTCIG.CODGAR", codgar);

              // Stato della gara: nuova gara in attesa di richiesta, a SIMOG,
              // del numero SMARTCIG
              dccW3SMARTCIG.addColumn("W3SMARTCIG.STATO", JdbcParametro.TIPO_NUMERICO, new Long(1));

              // Aggiungo la lista delle colonne, per poterle utilizzare
              // nel metodo successivo
              dccW3SMARTCIG.addColumn("W3SMARTCIG.OGGETTO", JdbcParametro.TIPO_TESTO, null);
              dccW3SMARTCIG.addColumn("W3SMARTCIG.FATTISPECIE", JdbcParametro.TIPO_TESTO, null);
              dccW3SMARTCIG.addColumn("W3SMARTCIG.IMPORTO", JdbcParametro.TIPO_DECIMALE, null);
              dccW3SMARTCIG.addColumn("W3SMARTCIG.TIPO_CONTRATTO", JdbcParametro.TIPO_TESTO, null);
              dccW3SMARTCIG.addColumn("W3SMARTCIG.CIG_ACC_QUADRO", JdbcParametro.TIPO_TESTO, null);
              dccW3SMARTCIG.addColumn("W3SMARTCIG.CUP", JdbcParametro.TIPO_TESTO, null);
              dccW3SMARTCIG.addColumn("W3SMARTCIG.M_RICH_CIG_COMUNI", JdbcParametro.TIPO_TESTO, null);
              dccW3SMARTCIG.addColumn("W3SMARTCIG.M_RICH_CIG", JdbcParametro.TIPO_TESTO, null);
              dccW3SMARTCIG.addColumn("W3SMARTCIG.RUP", JdbcParametro.TIPO_TESTO, null);
              dccW3SMARTCIG.addColumn("W3SMARTCIG.CODEIN", JdbcParametro.TIPO_TESTO, codein);
              dccW3SMARTCIG.addColumn("W3SMARTCIG.ID_SCELTA_CONTRAENTE", JdbcParametro.TIPO_TESTO, null);
              dccW3SMARTCIG.addColumn("W3SMARTCIG.COLLABORAZIONE", JdbcParametro.TIPO_DECIMALE, null);
              // Setto nel DataColumnContainer anche gli altri dati
              this.dccW3SMARTCIG_AltriDati("INS", dccW3SMARTCIG, datiGara, codrich, null, cfein, codgar, Long.valueOf(genereString));

              dccW3SMARTCIG.insert("W3SMARTCIG", this.sqlManager);

            } else {
              // LO SMARTCIG ESISTE GIA': SI PROCEDE AD AGGIORNARLA PREVIO
              // SUPERAMENTO DI ALCUNI CONTROLLI

              // Ricavo la chiave codrich = (Long) sqlManager.getObject("select codrich from w3smartcig where codgar = ?", new Object[]{codgar});

            	if(("UPD").equals(tipoOp)) {
                    // Controllo : la gara deve essere in uno stato modificabile
                    if (!this.isW3SMARTCIG_Modificabile(codgar)) {
                      logger.error("Lo smartcig (CODRICH: "
                          + codrich.toString()
                          + ") che si sta tentando di aggiornare e' in uno stato non modificabile: non e' possibile procedere nell'aggiornamento");
                      throw new GestoreException(
                          "Lo smartcig che si sta tentando di aggiornare e' in uno stato non modificabile: non e' possibile procedere nell'aggiornamento",
                          "errors.inserisciSmartCIG.nonmodificabile", null);
                    }

                    DataColumnContainer dccW3SMARTCIG = new DataColumnContainer(this.sqlManager, "W3SMARTCIG", "select * from w3smartcig where codrich = ?",
                        new Object[] { codrich });

                    // Aggiungo al DataColumnContainer anche gli altri dati comuni
                    this.dccW3SMARTCIG_AltriDati("UPD", dccW3SMARTCIG, datiGara, codrich, null, cfein, codgar, Long.valueOf(genereString));

                    if (dccW3SMARTCIG.isModifiedTable("W3SMARTCIG")) {

                      Long stato_simog = dccW3SMARTCIG.getLong("W3SMARTCIG.STATO");
                      if (!new Long(1).equals(stato_simog)) {
                    	  dccW3SMARTCIG.setValue("W3SMARTCIG.STATO", new Long(3));
                      }

                      dccW3SMARTCIG.getColumn("W3SMARTCIG.CODRICH").setChiave(true);
                      dccW3SMARTCIG.setValue("W3SMARTCIG.CODRICH", codrich);
                      dccW3SMARTCIG.setOriginalValue("W3SMARTCIG.CODRICH", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codrich));
                      dccW3SMARTCIG.update("W3SMARTCIG", this.sqlManager);

                    }
            	}else {
         		   if(("DEL").equals(tipoOp) && codrich!=null) {
                       DataColumnContainer dccW3SMARTCIG = new DataColumnContainer(this.sqlManager, "W3SMARTCIG", "select * from w3smartcig where codrich = ?",
                               new Object[] { codrich });
                       dccW3SMARTCIG.getColumn("W3SMARTCIG.CODRICH").setChiave(true);
                       dccW3SMARTCIG.setValue("W3SMARTCIG.CODRICH", codrich);
                       dccW3SMARTCIG.delete("W3SMARTCIG", this.sqlManager);
        		   }

            	}

            }


           	hm.put("key", codrich.toString());
           	hm.put("tipoRichiesta", "S");

        }

	   	if (logger.isDebugEnabled())
	          logger.debug("setAnagraficaSmartCig: fine metodo");

	   	return hm;

  }


  /**
   * Gestione dei dati dello smartcig all'inserimento e all'aggiornamento in
   * W3SMARTCIG
   *
   * @param dccW3SMARTCIG
   * @param datiSmartCig
   * @param codrich
   * @param syscon_remoto
   * @param codein
   * @throws GestoreException
   * @throws SQLException
   */
  private void dccW3SMARTCIG_AltriDati(String tipoOperazione, DataColumnContainer dccW3SMARTCIG, List datiGara, Long codrich, Long syscon_remoto, String cfein, String codgar, Long genere) throws GestoreException,
      SQLException {

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.dccW3SMARTCIG_AltriDati: inizio metodo");

    dccW3SMARTCIG.addColumn("W3SMARTCIG.RUP", JdbcParametro.TIPO_TESTO, null);


    //Oggetto
    String oggetto = (String) SqlManager.getValueFromVectorParam(datiGara,0).getValue();
    if (oggetto.length()>1024) {
    	oggetto=oggetto.substring(0, 1024);
    }
    dccW3SMARTCIG.setValue("W3SMARTCIG.OGGETTO", oggetto);

    // Importo Gara
    String selectImporto="select valmax from v_gare_importi where codgar=? and ngara is not null";
    Double importoGara = null;
    Object importoGaraObject = sqlManager.getObject(selectImporto, new Object[] { codgar });
    if (importoGaraObject != null) {
      if (importoGaraObject instanceof Long)
        importoGara = new Double(((Long) importoGaraObject));
      else if (importoGaraObject instanceof Double)
        importoGara = new Double((Double) importoGaraObject);
    }
    dccW3SMARTCIG.setValue("W3SMARTCIG.IMPORTO", importoGara.doubleValue());

    //tipoContratto (Lavori,Forniture,Servizi)
    Long tipoContratto = (Long) SqlManager.getValueFromVectorParam(datiGara,2).getValue();
    if (tipoContratto != null) {
        switch (tipoContratto.intValue()) {
        case 1: //lavori
        	dccW3SMARTCIG.setValue("W3SMARTCIG.TIPO_CONTRATTO", "L");
            break;
        case 2: //forniture
        	dccW3SMARTCIG.setValue("W3SMARTCIG.TIPO_CONTRATTO", "F");
            break;
        case 3: //servizi
        	dccW3SMARTCIG.setValue("W3SMARTCIG.TIPO_CONTRATTO", "S");
            break;
        }

    }


    //Cig Accordo quadro - solo nel caso di adesione ad accordo quadro
    String isAdesione = (String) SqlManager.getValueFromVectorParam(datiGara,3).getValue();
    if (isAdesione != null && "1".equals(isAdesione)){
      String cigAccordoQuadro = (String) SqlManager.getValueFromVectorParam(datiGara,4).getValue();
      if (cigAccordoQuadro != null) {
    	  dccW3SMARTCIG.setValue("W3SMARTCIG.CIG_ACC_QUADRO", cigAccordoQuadro);
      }
    }

    //CUP
    String cup = (String) SqlManager.getValueFromVectorParam(datiGara,5).getValue();
    if(cup!=null && !"".equals(cup)) {
    	dccW3SMARTCIG.setValue("W3SMARTCIG.CUP", cup);
    }

    //Scelta contraente
    Long tipgarg=(Long) SqlManager.getValueFromVectorParam(datiGara,7).getValue();
    String proceduraScelta= inviaVigilanzaManager.getFromTab2("A1z05", tipgarg,true);
    dccW3SMARTCIG.setValue("W3SMARTCIG.ID_SCELTA_CONTRAENTE", proceduraScelta);

    //RUP
    String codRUP = (String) SqlManager.getValueFromVectorParam(datiGara,6).getValue();
    if (codRUP != null) {
        dccW3SMARTCIG.setValue("W3SMARTCIG.RUP", codRUP);
    }

    /*APPALTI-1063
    // Gestione della collaborazione.
    // Inserisco la collaborazione di default solo se la collaborazione
    // associata al RUP indicato è una sola
    if (codRUP != null && "INS".equals(tipoOperazione)) {
    	List<?> datiW3AZIENDAUFFICIO = sqlManager.getListVector(
                "select w3aziendaufficio.id "
                    + "from w3aziendaufficio, w3usrsyscoll "
                    + "where w3aziendaufficio.id = w3usrsyscoll.w3aziendaufficio_id "
                    + "and w3usrsyscoll.syscon = ? and w3usrsyscoll.rup_codtec = ? and w3aziendaufficio.azienda_cf = ?",
                new Object[] { syscon_remoto, codRUP, cfein });
    	if (datiW3AZIENDAUFFICIO != null && datiW3AZIENDAUFFICIO.size() == 1) {
    		Long collaborazione = (Long) SqlManager.getValueFromVectorParam(datiW3AZIENDAUFFICIO.get(0), 0).getValue();
    		dccW3SMARTCIG.setValue("W3SMARTCIG.COLLABORAZIONE", collaborazione);
    	}
    }*/

    // Gestione delle categorie
      boolean inserisciCategorie = true;
      BigInteger[] datiCategorie = new BigInteger[1];
      datiCategorie[0] = new BigInteger("999");
      // Confronto la lista delle categorie gia' memorizzate con quelle inviate
      // via WS
      // Se ci sono differenze provvedo a cancellare le categorie presenti
      // per poi inserirle nuovamente in blocco
      String sqlSelectW3SMARTCIGMERC = "select categoria from w3smartcigmerc where codrich = ?";
      List<?> datiW3SMARTCIGMERC = this.sqlManager.getListVector(sqlSelectW3SMARTCIGMERC, new Object[] { codrich });
      if (datiW3SMARTCIGMERC != null && datiW3SMARTCIGMERC.size() > 0) {
    	BigInteger[] datiCategorieW3SMARTCIGMERC = new BigInteger[datiW3SMARTCIGMERC.size()];
        for (int i = 0; i < datiW3SMARTCIGMERC.size(); i++) {
        	if (SqlManager.getValueFromVectorParam(datiW3SMARTCIGMERC.get(i), 0).getValue() != null) {
        		datiCategorieW3SMARTCIGMERC[i] = new BigInteger(SqlManager.getValueFromVectorParam(datiW3SMARTCIGMERC.get(i), 0).getValue().toString());
        	}
        }
        java.util.Arrays.sort(datiCategorieW3SMARTCIGMERC);
        java.util.Arrays.sort(datiCategorie);
        if (java.util.Arrays.equals(datiCategorieW3SMARTCIGMERC, datiCategorie)) {
          inserisciCategorie = false;
        } else {
          inserisciCategorie = true;
          String sqlDeleteW3SMARTCIGMERC = "delete from w3smartcigmerc where codrich = ?";
          this.sqlManager.update(sqlDeleteW3SMARTCIGMERC, new Object[] { codrich });
        }
      }
      // Inserimento di tutte le categorie
      if (inserisciCategorie) {
        if (datiCategorie != null && datiCategorie.length > 0) {
          for (int j = 0; j < datiCategorie.length; j++) {
            DataColumnContainer dccW3SMARTCIGMERC = new DataColumnContainer(new DataColumn[] { new DataColumn("W3SMARTCIGMERC.CODRICH",
                new JdbcParametro(JdbcParametro.TIPO_NUMERICO, codrich)) });
            dccW3SMARTCIGMERC.addColumn("W3SMARTCIGMERC.NUMMERC", JdbcParametro.TIPO_NUMERICO, new Long(j + 1));
            dccW3SMARTCIGMERC.addColumn("W3SMARTCIGMERC.CATEGORIA", JdbcParametro.TIPO_NUMERICO, datiCategorie[j].longValue());
            dccW3SMARTCIGMERC.insert("W3SMARTCIGMERC", this.sqlManager);
          }
        }
      }

    if (logger.isDebugEnabled()) logger.debug("EldasoftSIMOGWSManager.dccW3SMARTCIG_AltriDati: fine metodo");
  }



  /***
   * Il metodo effettua i controlli bloccanti e quelli non bloccanti sui dati della gara per la richiesta
   * del codice CIG
   *
   * @param codgar
   * @param numeroLotto
   * @param genere
   * @param sessione
   * @return HashMap
   * @throws SQLException
   * @throws GestoreException
   */
  public HashMap<String,Object> controllaDatiRichiestaCIG(String codgar, String numeroLotto, String genere, HttpSession sessione) throws SQLException, GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("controllaDatiRichiestaCIG: inizio metodo");

    HashMap<String,Object> response = new HashMap<String,Object>();
    ArrayList<String> erroriBloccanti = new ArrayList<String>();
    ArrayList<String> erroriNonBloccanti = new ArrayList<String>();

    String selectDatiGara = null;

    if("1".equals(genere) || "3".equals(genere)){
      //controllo che vi sia almeno un lotto
      int numeroLotti = inviaDatiRichiestaCigManager.getNumeroLotti(codgar);
      if(numeroLotti <= 0){
        erroriBloccanti.add(CTR_NESSUN_LOTTO);
      }

      //Gare a lotti
      selectDatiGara = "select "
          + " torn.destor, "   //0
          + " torn.codrup, "   //1
          + " torn.tipgar, "   //2
          + " torn.modrea, "   //3
          + " torn.tipgen, "   //4
          + " torn.codnuts, "  //5
          + " torn.cenint"     //6
          + " from torn where codgar = ?";
    }else{
      //gara a lotto unico
      selectDatiGara = "select "
          + " gare.not_gar, "  //0
          + " torn.codrup, "   //1
          + " gare.tipgarg, "  //2
          + " torn.modrea, "   //3
          + " torn.tipgen, "   //4
          + " torn.codnuts, "  //5
          + " torn.cenint,"    //6
          + " gare.ngara, "    //7
          + " gare.impapp "    //8
          + " from torn, gare where torn.codgar = gare.codgar1 "
          + " and torn.codgar = ?";
    }

    String ngara=null;
    List datiTORNGARE = sqlManager.getVector(selectDatiGara,new Object[] { codgar });
    if (datiTORNGARE != null && datiTORNGARE.size() > 0) {

      String profilo = (String) sessione.getAttribute(CostantiGenerali.PROFILO_ATTIVO);
        ArrayList erroriTemp=null;
        if("2".equals(genere)){
            ngara = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,7).getValue();
        }

        //Inizio - Controlli bloccanti

        String oggetto = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,0).getValue();
        //Oggetto della gara
        if (oggetto == null || "".equals(oggetto)) {
            if("2".equals(genere)){
              erroriBloccanti.add(CTR_GARA_OGGETTO);
            }else{
              erroriBloccanti.add(CTR_GARA_OGGETTO);
            }
        }

        //Oggetto dei lotti
        if("1".equals(genere) || "3".equals(genere)){
          erroriBloccanti.addAll(inviaDatiRichiestaCigManager.getControlloNOTGAR(codgar));
        }

        //Controllo codice fiscale stazione appaltante
        String cenint =  (String) SqlManager.getValueFromVectorParam(datiTORNGARE,6).getValue();
        if (cenint != null) {
	        // I nuovi dati devono essere in carico alla stessa SA che li aveva in carico originariamente
	        String sa_originale = (String) sqlManager.getObject(
	            "select codein from w3gara where w3gara.codgar = ?", new Object[] { codgar });
	        if (sa_originale != null) {
	          if (!sa_originale.equals(cenint)) {
	        	  erroriBloccanti.add(CTR_GARA_SA_CHANGED);
	          }
	        }

          String cfein = (String) sqlManager.getObject("select cfein from uffint where codein =?", new Object[]{cenint});

          if(cfein==null || "".equals(cfein)){
            erroriBloccanti.add(CTR_SA_CF);
          }else{
            if (!UtilityFiscali.isValidPartitaIVA(cfein) & !UtilityFiscali.isValidCodiceFiscale(cfein)){
              erroriBloccanti.add(CTR_SA_CF_NON_VALIDO);
            }
          }
        }else{
          erroriBloccanti.add(CTR_SA);
        }

        String codRUP = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,1).getValue();
        //RUP
        if (codRUP != null) {
        	//verifico se ci sono collaborazioni
          /* APPALTI-1063
        	Long sysconCount = (Long) sqlManager.getObject("select count(syscon) from w3usrsys where rup_codtec=?", new Object[] { codRUP });
    	    if(Long.valueOf(0).equals(sysconCount)) {
    	    	erroriBloccanti.add("Non e' stato impostato il collegamento del RUP con un utente applicativo." +
    	    			"<br>Utilizzare la funzione 'RUP e centri di costo per richiesta CIG', disponibile dal menu' 'Gare', per configurare i dati.");
    	    }*/
            // I nuovi dati devono essere in carico allo stesso RUP che li aveva in carico originariamente
            String rup_originale = (String) sqlManager.getObject(
                "select rup_codtec from w3gara where w3gara.codgar = ?", new Object[] { codgar });
            if (rup_originale != null) {
              if (!rup_originale.equals(codRUP)) {
            	  erroriBloccanti.add(CTR_GARA_RUP_CHANGED);
              }
            }
            erroriBloccanti.addAll(inviaDatiRichiestaCigManager.controlloDatiTecnico(codRUP,true));
        }else{
          erroriBloccanti.add(CTR_GARA_RUP);
        }

        //Importo Gara
        if("2".equals(genere)){
          Object importo = SqlManager.getValueFromVectorParam(
              datiTORNGARE, 8).getValue();
          if(importo==null)
            erroriBloccanti.add(CTR_GARA_IMPORTO);
        }else{
          // Importo lotti
          erroriBloccanti.addAll(inviaDatiRichiestaCigManager.getControlloImportoGara(codgar, ngara, genere));
        }


        //Controllo su tipgar
        Long tipo = (Long) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 2).getValue();
        if (tipo == null) {
          erroriBloccanti.add(CTR_GARE_TORN_TIPGAR);
        }else{
          String count = inviaVigilanzaManager.getFromTab2("A1z11", tipo, false);
          if(count == null || "".equals(count)){
            erroriBloccanti.add(CTR_GARE_TORN_MAP_TIPGAR_A1z11);
          }
        }

        //CONTROLLO MODREA E CODNUTS
        String modrea = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,3).getValue();
        String codnuts = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,5).getValue();
        String controllo[] = inviaDatiRichiestaCigManager.controlloModreaCodnuts(modrea, codnuts, genere, profilo);
        if("NOK".equals(controllo[0])){
          erroriBloccanti.add(CTR_MODREA);
        }
        /*
        if("NOK".equals(controllo[1])){
          erroriBloccanti.add(CTR_DONUTS);
        }
        */

    	//determino in base al genere il codice ISTAT
    	String locint = null;
    	if("2".equals(genere)){
    		locint = (String) sqlManager.getObject("select locint from gare,torn where codgar = ? and torn.codgar = gare.codgar1", new Object[] { codgar });
    	}
    	if("3".equals(genere)){
    		locint = (String) sqlManager.getObject("select locint from gare,torn where codgar = ? and torn.codgar = gare.ngara", new Object[] { codgar });
    	}


        if("1".equals(genere)) {
        	erroriBloccanti.addAll(this.getControlloIstatNutsGara(codgar, codnuts, profilo));
        }else {
    		String controlloNutsIstat[] = inviaDatiRichiestaCigManager.controlloNutsIstat(codnuts, locint, genere, profilo);
            if("NOK".equals(controlloNutsIstat[0]) && "NOK".equals(controlloNutsIstat[1]) ){
              erroriBloccanti.add(CTR_DONUTS_ISTAT);
            }
            if("NOK".equals(controlloNutsIstat[0]) && "OK".equals(controlloNutsIstat[1]) ){
                erroriBloccanti.add(CTR_DONUTS);
            }
            if("OK".equals(controlloNutsIstat[0]) && "NOK".equals(controlloNutsIstat[1]) ){
                erroriBloccanti.add(CTR_ISTAT);
            }
        }


        Long tipgen = (Long) SqlManager.getValueFromVectorParam(
                datiTORNGARE, 4).getValue();

        //Controllo su TIPLAV
        if((new Long(1)).equals(tipgen)){
          erroriBloccanti.addAll(inviaDatiRichiestaCigManager.getControlloDatiTornGare(codgar, ngara, genere, "TIPLAV", profilo));
        }

        //Controllo OGGCONT
        if((new Long(2)).equals(tipgen)){
          erroriBloccanti.addAll(inviaDatiRichiestaCigManager.getControlloDatiTornGare(codgar, ngara, genere, "OGGCONT", profilo));
        }

        //Controllo CPV
        erroriBloccanti.addAll(inviaDatiRichiestaCigManager.getControlloGarcpv(codgar, ngara, genere, profilo));


        //Fine - Controlli bloccanti

        //Inizio - Controlli non bloccanti

        //controllo cupprg
        erroriNonBloccanti.addAll(inviaDatiRichiestaCigManager.getControlloCUPCUI(codgar, ngara, genere, profilo, "CUPPRG"));

        //controllo codcui
        erroriNonBloccanti.addAll(inviaDatiRichiestaCigManager.getControlloCUPCUI(codgar, ngara, genere, profilo, "CODCUI"));


      //Fine - Controlli non bloccanti
    }

    response.put(ERRORI_BLOCCANTI_KEY, erroriBloccanti);
    response.put(ERRORI_NON_BLOCCANTI_KEY, erroriNonBloccanti);

    if (logger.isDebugEnabled())
      logger.debug("controllaDatiRichiestaCIG: fine metodo");
    return response;
  }

  /***
   * Il metodo effettua i controlli bloccanti e quelli non bloccanti sui dati della gara per la richiesta
   * del codice CIG
   *
   * @param idStipula
   * @param sessione
   * @return HashMap
   * @throws SQLException
   * @throws GestoreException
   */
  public HashMap<String,Object> controllaDatiRichiestaCIGCollegato(String idStipula, HttpSession sessione) throws SQLException, GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("controllaDatiRichiestaCIG: inizio metodo");

    HashMap<String,Object> response = new HashMap<String,Object>();
    ArrayList<String> erroriBloccanti = new ArrayList<String>();
    ArrayList<String> erroriNonBloccanti = new ArrayList<String>();

    String selectDatiGara = null;
    String selectDatiStipula = null;

    selectDatiStipula = "select "
          + " gare.codgar1, "  //0
          + " g1stipula.oggetto, "  //1
          + " g1stipula.impstipula "  //2
          + " from g1stipula, gare where g1stipula.ngara = gare.ngara "
          + " and g1stipula.id = ?";

    //gara a lotto unico
    selectDatiGara = "select "
          + " torn.codrup, "   //0
          + " torn.cenint"    //1
          + " from torn where "
          + " torn.codgar = ?";


    String ngara=null;
    List datiG1STIPULA = sqlManager.getVector(selectDatiStipula,new Object[] { idStipula });
    if (datiG1STIPULA != null && datiG1STIPULA.size() > 0) {
    String codgar1 = (String) SqlManager.getValueFromVectorParam(datiG1STIPULA,0).getValue();
    String oggetto = (String) SqlManager.getValueFromVectorParam(datiG1STIPULA,1).getValue();
    Double impstipula = (Double) SqlManager.getValueFromVectorParam(datiG1STIPULA,2).getValue();

    List datiTORNGARE = sqlManager.getVector(selectDatiGara,new Object[] { codgar1 });
    if (datiTORNGARE != null && datiTORNGARE.size() > 0) {

      String profilo = (String) sessione.getAttribute(CostantiGenerali.PROFILO_ATTIVO);
        ArrayList erroriTemp=null;

        //Inizio - Controlli bloccanti

        //Oggetto della gara
        if (oggetto == null || "".equals(oggetto)) {
           erroriBloccanti.add(CTR_STIPULA_OGGETTO);
        }

        //Controllo codice fiscale stazione appaltante
        String cenint =  (String) SqlManager.getValueFromVectorParam(datiTORNGARE,1).getValue();
        if (cenint != null) {
	        // I nuovi dati devono essere in carico alla stessa SA che li aveva in carico originariamente
	        String sa_originale = (String) sqlManager.getObject(
	            "select codein from w3gara where w3gara.codgar = ?", new Object[] { codgar1 });
	        if (sa_originale != null) {
	          if (!sa_originale.equals(cenint)) {
	        	  erroriBloccanti.add(CTR_GARA_SA_CHANGED);
	          }
	        }

          String cfein = (String) sqlManager.getObject("select cfein from uffint where codein =?", new Object[]{cenint});

          if(cfein==null || "".equals(cfein)){
            erroriBloccanti.add(CTR_SA_CF);
          }else{
            if (!UtilityFiscali.isValidPartitaIVA(cfein) & !UtilityFiscali.isValidCodiceFiscale(cfein)){
              erroriBloccanti.add(CTR_SA_CF_NON_VALIDO);
            }
          }
        }else{
          erroriBloccanti.add(CTR_SA);
        }

        String codRUP = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,0).getValue();
        //RUP
        if (codRUP != null) {
          /* APPALTI-1063
        	//verifico se ci sono collaborazioni
        	Long sysconCount = (Long) sqlManager.getObject("select count(syscon) from w3usrsys where rup_codtec=?", new Object[] { codRUP });
    	    if(Long.valueOf(0).equals(sysconCount)) {
    	    	erroriBloccanti.add("Non e' stato impostato il collegamento del RUP con un utente applicativo." +
    	    			"<br>Utilizzare la funzione 'RUP e centri di costo per richiesta CIG', disponibile dal menu' 'Stipule', per configurare i dati.");
    	    }*/
            // I nuovi dati devono essere in carico allo stesso RUP che li aveva in carico originariamente
            String rup_originale = (String) sqlManager.getObject(
                "select rup_codtec from w3gara where w3gara.codgar = ? and stato_simog<>6", new Object[] { codgar1 });
            if (rup_originale != null) {
              if (!rup_originale.equals(codRUP)) {
            	  erroriBloccanti.add(CTR_GARA_RUP_CHANGED);
              }
            }
            erroriBloccanti.addAll(inviaDatiRichiestaCigManager.controlloDatiTecnico(codRUP,true));
        }else{
          erroriBloccanti.add(CTR_GARA_RUP);
        }

        //Importo Stipula
          if(impstipula==null)
            erroriBloccanti.add(CTR_STIPULA_IMPORTO);

        //Fine - Controlli bloccanti

        //Inizio - Controlli non bloccanti

      //Fine - Controlli non bloccanti
    }}

  response.put(ERRORI_BLOCCANTI_KEY, erroriBloccanti);
  response.put(ERRORI_NON_BLOCCANTI_KEY, erroriNonBloccanti);

  if (logger.isDebugEnabled())
    logger.debug("controllaDatiRichiestaCIG: fine metodo");
  return response;
  }


  /***
   * Il metodo effettua i controlli bloccanti sui dati della gara per la richiesta
   * del codice  Smart CIG
   *
   * @param codgar
   * @param numeroLotto
   * @return HashMap
   * @throws SQLException
   * @throws GestoreException
   */
  public HashMap<String,Object> controllaDatiRichiestaSmartCIG(String codgar, String numeroLotto, HttpSession sessione) throws SQLException, GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("controllaDatiRichiestaSmartCIG: inizio metodo");

    HashMap<String,Object> response = new HashMap<String,Object>();
    ArrayList<String> erroriBloccanti = new ArrayList<String>();
    ArrayList<String> erroriNonBloccanti = new ArrayList<String>();

    String  selectDatiGara = "select "
          + " gare.not_gar, "  //0
          + " torn.codrup, "   //1
          + " gare.tipgarg, "  //2
          + " gare.impapp, "    //3
          + " torn.cenint "    //4
          + " from torn, gare where torn.codgar = gare.codgar1 "
          + " and torn.codgar = ?";


    List datiTORNGARE = sqlManager.getVector(selectDatiGara,new Object[] { codgar });
    if (datiTORNGARE != null && datiTORNGARE.size() > 0) {
        ArrayList erroriTemp=null;

        String oggetto = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,0).getValue();
        //Oggetto della gara
        if (oggetto == null || "".equals(oggetto)) {
          erroriBloccanti.add(CTR_GARA_OGGETTO);
        }

        //Controllo codice fiscale stazione appaltante
        String cenint =  (String) SqlManager.getValueFromVectorParam(datiTORNGARE,4).getValue();
        if (cenint != null) {
	        // I nuovi dati devono essere in carico alla stessa SA che li aveva in carico originariamente
	        String sa_originale = (String) sqlManager.getObject(
	            "select codein from w3smartcig where codgar = ?", new Object[] { codgar });
	        if (sa_originale != null) {
	          if (!sa_originale.equals(cenint)) {
	        	  erroriBloccanti.add(CTR_GARA_SA_CHANGED);
	          }
	        }

          String cfein = (String) sqlManager.getObject("select cfein from uffint where codein =?", new Object[]{cenint});

          if(cfein==null || "".equals(cfein)){
            erroriBloccanti.add(CTR_SA_CF);
          }else{
            if (!UtilityFiscali.isValidPartitaIVA(cfein) & !UtilityFiscali.isValidCodiceFiscale(cfein)){
              erroriBloccanti.add(CTR_SA_CF_NON_VALIDO);
            }
          }

        }else{
          erroriBloccanti.add(CTR_SA);
        }

        String codRUP = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,1).getValue();
        //RUP
        if (codRUP != null) {
          /* APPALTI-1063
        	//verifico se ci sono collaborazioni
        	Long sysconCount = (Long) sqlManager.getObject("select count(syscon) from w3usrsys where rup_codtec=?", new Object[] { codRUP });
    	    if(Long.valueOf(0).equals(sysconCount)) {
    	    	erroriBloccanti.add("Non e' stato impostato il collegamento del RUP con un utente applicativo." +
    	    			"<br>Utilizzare la funzione 'RUP e centri di costo per richiesta CIG', disponibile dal menu' 'Gare', per configurare i dati.");
    	    }*/
            // I nuovi dati devono essere in carico allo stesso RUP che li aveva in carico originariamente
            String rup_originale = (String) sqlManager.getObject(
                "select rup from w3smartcig where codgar = ?", new Object[] { codgar });
            if (rup_originale != null) {
              if (!rup_originale.equals(codRUP)) {
            	  erroriBloccanti.add(CTR_GARA_RUP_CHANGED);
              }
            }
            erroriBloccanti.addAll(inviaDatiRichiestaCigManager.controlloDatiTecnico(codRUP,true));
        }else{
          erroriBloccanti.add(CTR_GARA_RUP);
        }

        // Importo Gara
        Object importo = SqlManager.getValueFromVectorParam(
            datiTORNGARE, 3).getValue();
        if(importo==null)
          erroriBloccanti.add(CTR_GARA_IMPORTO);

        //Controllo su tipgar
        Long tipo = (Long) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 2).getValue();
        if (tipo == null) {
          erroriBloccanti.add(CTR_GARE_TORN_TIPGAR);
        }else{
          String tipoProc = this.inviaVigilanzaManager.getFromTab2("A1z05", tipo, true);
          if(tipoProc == null || "".equals(tipoProc)){
            erroriBloccanti.add(CTR_GARE_TORN_MAP_TIPGAR_A1z05);
          }
        }


    }

    response.put(ERRORI_BLOCCANTI_KEY, erroriBloccanti);
    response.put(ERRORI_NON_BLOCCANTI_KEY, erroriNonBloccanti);

    if (logger.isDebugEnabled())
      logger.debug("controllaDatiRichiestaSmartCIG: fine metodo");
    return response;
  }

  /***
   * Il metodo effettua i controlli bloccanti sui dati della gara collegata per la richiesta
   * del codice  Smart CIG
   *
   * @param idStipula
   * @return HashMap
   * @throws SQLException
   * @throws GestoreException
   */
  public HashMap<String,Object> controllaDatiRichiestaSmartCIGCollegato(String idStipula, HttpSession sessione) throws SQLException, GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("controllaDatiRichiestaSmartCIG: inizio metodo");

    HashMap<String,Object> response = new HashMap<String,Object>();
    ArrayList<String> erroriBloccanti = new ArrayList<String>();
    ArrayList<String> erroriNonBloccanti = new ArrayList<String>();

    String selectDatiGara = null;
    String selectDatiStipula = null;

    selectDatiStipula = "select "
          + " gare.codgar1, "  //0
          + " g1stipula.oggetto, "  //1
          + " g1stipula.impstipula "  //2
          + " from g1stipula, gare where g1stipula.ngara = gare.ngara "
          + " and g1stipula.id = ?";

    //gara a lotto unico
    selectDatiGara = "select "
          + " torn.codrup, "   //0
          + " torn.cenint "    //1
          + " from torn where "
          + " torn.codgar = ?";


    String ngara=null;
    List datiG1STIPULA = sqlManager.getVector(selectDatiStipula,new Object[] { idStipula });
    if (datiG1STIPULA != null && datiG1STIPULA.size() > 0) {
    String codgar1 = (String) SqlManager.getValueFromVectorParam(datiG1STIPULA,0).getValue();
    String oggetto = (String) SqlManager.getValueFromVectorParam(datiG1STIPULA,1).getValue();
    Double impstipula = (Double) SqlManager.getValueFromVectorParam(datiG1STIPULA,2).getValue();

    List datiTORNGARE = sqlManager.getVector(selectDatiGara,new Object[] { codgar1 });
    if (datiTORNGARE != null && datiTORNGARE.size() > 0) {
        ArrayList erroriTemp=null;

        //Oggetto della gara
        if (oggetto == null || "".equals(oggetto)) {
          erroriBloccanti.add(CTR_STIPULA_OGGETTO);
        }

        //Controllo codice fiscale stazione appaltante
        String cenint =  (String) SqlManager.getValueFromVectorParam(datiTORNGARE,1).getValue();
        if (cenint != null) {
	        // I nuovi dati devono essere in carico alla stessa SA che li aveva in carico originariamente
	        String sa_originale = (String) sqlManager.getObject(
	            "select codein from w3smartcig where codgar = ?", new Object[] { codgar1 });
	        if (sa_originale != null) {
	          if (!sa_originale.equals(cenint)) {
	        	  erroriBloccanti.add(CTR_GARA_SA_CHANGED);
	          }
	        }

          String cfein = (String) sqlManager.getObject("select cfein from uffint where codein =?", new Object[]{cenint});

          if(cfein==null || "".equals(cfein)){
            erroriBloccanti.add(CTR_SA_CF);
          }else{
            if (!UtilityFiscali.isValidPartitaIVA(cfein) & !UtilityFiscali.isValidCodiceFiscale(cfein)){
              erroriBloccanti.add(CTR_SA_CF_NON_VALIDO);
            }
          }

        }else{
          erroriBloccanti.add(CTR_SA);
        }

        String codRUP = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,0).getValue();
        //RUP
        if (codRUP != null) {
          /* APPALTI-1063
        	//verifico se ci sono collaborazioni
        	Long sysconCount = (Long) sqlManager.getObject("select count(syscon) from w3usrsys where rup_codtec=?", new Object[] { codRUP });
    	    if(Long.valueOf(0).equals(sysconCount)) {
    	    	erroriBloccanti.add("Non e' stato impostato il collegamento del RUP con un utente applicativo." +
    	    			"<br>Utilizzare la funzione 'RUP e centri di costo per richiesta CIG', disponibile dal menu' 'Gare', per configurare i dati.");
    	    }*/
            // I nuovi dati devono essere in carico allo stesso RUP che li aveva in carico originariamente
            String rup_originale = (String) sqlManager.getObject(
                "select rup from w3smartcig where codgar = ?", new Object[] { codgar1 });
            if (rup_originale != null) {
              if (!rup_originale.equals(codRUP)) {
            	  erroriBloccanti.add(CTR_GARA_RUP_CHANGED);
              }
            }
            erroriBloccanti.addAll(inviaDatiRichiestaCigManager.controlloDatiTecnico(codRUP,true));
        }else{
          erroriBloccanti.add(CTR_GARA_RUP);
        }

        // Importo Gara
        if(impstipula==null)
          erroriBloccanti.add(CTR_STIPULA_IMPORTO);

    }
    }

    response.put(ERRORI_BLOCCANTI_KEY, erroriBloccanti);
    response.put(ERRORI_NON_BLOCCANTI_KEY, erroriNonBloccanti);

    if (logger.isDebugEnabled())
      logger.debug("controllaDatiRichiestaSmartCIG: fine metodo");
    return response;
  }

  /**
   * Test sullo stato della gara/lotto (STATO_SIMOG) per verificare se si tratta
   * di una gara o un lotto modificabile
   *
   * Una gara o un lotto sono modificabili se in uno dei seguenti stati:
   * <ul>
   * <li>1 - Dati da inviare a SIMOG</li>
   * <li>2 - Dati inviati a SIMOG</li>
   * <li>3 - Dati modificati, richiesta di modifica da inoltrare a SIMOG</li>
   * <li>4 - Dati inviati a SIMOG (dopo richiesta di modifica)</li>
   * </ul>
   *
   * Gli altri stati:
   * <ul>
   * <li>5 - Dati in richiesta di cancellazione</li>
   * <li>6 - Dati cancellati</li>
   * <li>7 - Dati pubblicati</li>
   * <li>99 - Dati recuperati da SIMOG</li>
   * </ul>
   *
   * rendono la gara o il lotto non modificabili
   *
   *
   * @param stato_simog
   * @return
   */
  public boolean isSTATO_SIMOGModificabile(Long stato_simog) {

    boolean isModificabile = false;

    if (stato_simog != null) {
      switch (stato_simog.intValue()) {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
        isModificabile = true;
        break;
      case 7:
      case 99:
        isModificabile = false;

      default:
        break;
      }
    }
    return isModificabile;
  }

  /**
   * Verifica se lo smartcig individuato e' modificabile
   *
   * @param uuid
   * @return
   * @throws GestoreException
   */
  public boolean isW3SMARTCIG_Modificabile(String codgar) throws GestoreException {

    boolean isModificabile = false;
    try {
      Long stato_simog = (Long) sqlManager.getObject(
          "select stato from w3smartcig where codgar = ?",
          new Object[] { codgar });
      isModificabile = this.isSTATO_SIMOGModificabile(stato_simog);

    } catch (SQLException e) {
      throw new GestoreException("Errore durante la lettura della gara",
          "controlloW3GARA", e);
    }

    return isModificabile;
  }


  /**
   * Calcola NUMLOTT di W3LOTT
   *
   * @param numgara
   * @return
   * @throws GestoreException
   */
  private Long getNextValNUMLOTT(Long numgara) throws GestoreException {

    if (logger.isDebugEnabled()) logger.debug("getNextValNUMLOTT: inizio metodo");

    Long numlott = null;

    try {
      numlott = (Long) this.sqlManager.getObject("select max(numlott) from w3lott where numgara = ?", new Object[] { numgara });
      if (numlott == null) numlott = new Long(0);
      numlott = new Long(numlott.longValue() + 1);
    } catch (SQLException e) {
      throw new GestoreException("Errore durante il controllo del lotto", "controlloW3LOTT", e);
    }

    if (logger.isDebugEnabled()) logger.debug("getNextValNUMLOTT: inizio metodo");

    return numlott;

  }

  /**
   * Verifica se il lotto individuato e' modificabile
   *
   * @param uuid
   * @return
   * @throws GestoreException
   */
  public boolean isW3LOTT_Modificabile(Long numgara,Long numlott) throws GestoreException {

    boolean isModificabile = false;
    try {
      Long stato_simog = (Long) sqlManager.getObject(
          "select stato_simog from w3lott where numgara = ? and numlott = ?",
          new Object[] { numgara,numlott });
      isModificabile = isSTATO_SIMOGModificabile(stato_simog);

    } catch (SQLException e) {
      throw new GestoreException("Errore durante la lettura del lotto",
          "controlloW3LOTT", e);
    }

    return isModificabile;
  }


  /**
   * Controlli sui campi CODCIG
   *
   * @param codgar
   * @param ngara
   * @param genere
   * @return ArrayList<String>
   * @throws SQLException
   */
  public ArrayList<String> getControlloCigGara(String codgar, String ngara, String genere) throws SQLException{
    ArrayList<String> errors = new ArrayList<String>();
    String MessageError = CTR_LOTTI_CIG;
    String selectGARE = "select codiga, codcig"
        + " from gare where codgar1 = ?"
        + " and codgar1!=ngara order by ngara";

    List datiGARE = sqlManager.getListVector(selectGARE,
        new Object[] { codgar });

    if (datiGARE != null && datiGARE.size() > 0) {
      String codiga=null;
      Object cig=null;
      boolean isFirst = true;
      for (int i = 0; i < datiGARE.size(); i++) {
        codiga = (String) SqlManager.getValueFromVectorParam(
            datiGARE.get(i), 0).getValue();
        cig = SqlManager.getValueFromVectorParam(
            datiGARE.get(i), 1).getValue();

        if (cig != null) {
          if(!isFirst){
            MessageError = MessageError + ", ";
          }
          MessageError = MessageError + codiga;
          isFirst = false;
        }
      }
      if(!isFirst){
        errors.add(MessageError);
      }
    }
    return errors;
  }

  /**
   * Controlli sui campi ISTAT e NUTS
   *
   * @param codgar
   * @param ngara
   *
   *
   * @param genere
   * @return ArrayList<String>
   * @throws SQLException
   */
  public ArrayList<String> getControlloIstatNutsGara(String codgar, String codnuts, String profilo) throws SQLException{
    ArrayList<String> errors = new ArrayList<String>();
    String MessageError = null;
    String selectGARE = "select ngara, codiga, locint"
        + " from gare where codgar1 = ?"
        + " and codgar1!=ngara order by ngara";

    List datiGARE = sqlManager.getListVector(selectGARE,
        new Object[] { codgar });

    if (datiGARE != null && datiGARE.size() > 0) {
      String ngara=null;
      String codiga=null;
      String locint=null;
      boolean isFirst = true;
      for (int i = 0; i < datiGARE.size(); i++) {
    	ngara = (String) SqlManager.getValueFromVectorParam(datiGARE.get(i), 0).getValue();
        codiga = (String) SqlManager.getValueFromVectorParam(datiGARE.get(i), 1).getValue();
        locint = (String) SqlManager.getValueFromVectorParam(datiGARE.get(i), 2).getValue();

        if (ngara != null) {
    		String controlloNutsIstat[] = inviaDatiRichiestaCigManager.controlloNutsIstat(codnuts, locint, "1", profilo);
            if("NOK".equals(controlloNutsIstat[0]) && "NOK".equals(controlloNutsIstat[1]) ){
            	if(isFirst){
            	  MessageError = CTR_DONUTS_ISTAT_LOTTI;
            	}
                if(!isFirst){
                  MessageError = MessageError + ", ";
                }
                MessageError = MessageError + codiga;
                isFirst = false;
            }
            if("NOK".equals(controlloNutsIstat[0]) && "OK".equals(controlloNutsIstat[1]) ){
            	if(isFirst){
              	  MessageError = CTR_DONUTS;
              	}
                isFirst = false;
            }
            if("OK".equals(controlloNutsIstat[0]) && "NOK".equals(controlloNutsIstat[1]) ){
            	if(isFirst){
              	  MessageError = CTR_ISTAT_LOTTI;
              	}
                  if(!isFirst){
                    MessageError = MessageError + ", ";
                  }
                  MessageError = MessageError + codiga;
                  isFirst = false;
            }
        }
      }
      if(!isFirst){
        errors.add(MessageError);
      }
    }
    return errors;
  }


  /*
   *
   *
   *
   */


  /**
   * Aggiornamento
   *
   * @param codgar
   * @param ngara
   * @param genere
   * @return ArrayList<String>
   * @throws SQLException
   */
  public int setGaraLotto(String modoAgg, Long numgara, Long numlott, String numavcp, String codcig, Date datacqcig ) throws SQLException{

	  String updateNUMAVCP = "update torn set numavcp = ? where codgar= ? ";
	  String updateCODCIG = "update gare set codcig = ? where ngara = ? ";
	  String updateG1STIPULA = "update g1stipula set cigvar = ? where ngaravar = ? ";
	  String updateDACQCIG = "update gare set dacqcig = ? where ngara = ? ";
	  String updateDACQCIGGara = "update gare set dacqcig = ? where ngara = ? and dacqcig is null";


	  if("G".equals(modoAgg)) {
	      String ngara = (String) sqlManager.getObject(
	              "select ngara from W3LOTT where numgara = ? and numlott = ? ",
	              new Object[] { numgara,numlott });

		  this.sqlManager.update(updateCODCIG, new Object[] { codcig,ngara });

		  //verifico se si tratta di una richiesta CIG collegato
	      String ngaraCollegata = (String) sqlManager.getObject(
	              "select ngara from G1STIPULA where ngaravar = ? ",
	              new Object[] { ngara });

	      if(!"".equals(ngaraCollegata) && ngaraCollegata != null) {
	    	  this.sqlManager.update(updateG1STIPULA, new Object[] { codcig,ngara });
	      }

		  //aggiornamento di data acquisizione cig (se sono in una gara a lotti aggiorno la data sulla gara,altrimenti sul lotto)
	      String codgar = (String) sqlManager.getObject(
	              "select codgar from W3GARA where numgara = ?",
	              new Object[] { numgara});
	      String ngaraGara  = (String) sqlManager.getObject(
	              "select ngara from gare where ngara = ?",
	              new Object[] { codgar});
	      ngaraGara  = StringUtils.stripToEmpty(ngaraGara);
	      if(!"".equals(ngaraGara)) {
	    	  this.sqlManager.update(updateDACQCIGGara, new Object[] { datacqcig,ngaraGara });
	      }else {
	    	  this.sqlManager.update(updateDACQCIG, new Object[] { datacqcig,ngara });
	      }
	  }
	  if("S".equals(modoAgg)) {
	      String codgar = (String) sqlManager.getObject(
	              "select codgar from W3SMARTCIG where codrich = ? ",
	              new Object[] { numgara });
	      String ngara = (String) sqlManager.getObject(
	              "select ngara from GARE where codgar1= ? ",
	              new Object[] { codgar });

		  this.sqlManager.update(updateCODCIG, new Object[] { codcig,ngara });

		  //verifico se si tratta di una richiesta CIG collegato
	      String ngaraCollegata = (String) sqlManager.getObject(
	              "select ngara from G1STIPULA where ngaravar = ? ",
	              new Object[] { ngara });

	      if(!"".equals(ngaraCollegata) && ngaraCollegata != null) {
	    	  this.sqlManager.update(updateG1STIPULA, new Object[] { codcig,ngara });
	      }

		//aggiornamento di data acquisizione cig
		  this.sqlManager.update(updateDACQCIG, new Object[] { datacqcig,ngara });
	  }
	  if("T".equals(modoAgg)) {
	      String codgar = (String) sqlManager.getObject(
	              "select codgar from W3GARA where numgara = ?",
	              new Object[] { numgara });

		  this.sqlManager.update(updateNUMAVCP, new Object[] { numavcp,codgar });
	  }

	    return 0;
  }


}