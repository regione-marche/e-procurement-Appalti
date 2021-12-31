/*
 * Created on 20/apr/2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreRAGIMPMultiplo;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.SmatManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;
/**
 * Gestore salvataggio entita' DITG
 *
 * @author Luca.Giacomazzo
 */
public class GestoreDITG extends AbstractGestoreEntita {

  protected PgManager pgManager;

  /** Manager di SMAT */
  private SmatManager  smatManager ;


	@Override
  public String getEntita() {
		return "DITG";
	}

	public GestoreDITG(boolean isGestoreStandard) {
		super(isGestoreStandard);
	}

	public GestoreDITG() {
		super(true);
	}

	@Override
  public void setRequest(HttpServletRequest request) {
	    super.setRequest(request);
	    // Estraggo il manager per gestire diversi SQL
	    this.pgManager = (PgManager) UtilitySpring.getBean("pgManager",
	        this.getServletContext(), PgManager.class);
        // Estraggo il manager per SMAT
        this.smatManager = (SmatManager) UtilitySpring.getBean("smatManager",
            this.getServletContext(), SmatManager.class);
	}

	@Override
  public void preDelete(TransactionStatus status,
			DataColumnContainer dataColumnContainer) throws GestoreException {

		// Gestione della cancellazione di una ditta da una gara
		String codiceDitta = dataColumnContainer.getString("DITG.DITTAO");
		String codiceLotto = dataColumnContainer.getString("DITG.NGARA5");
		String codiceTornata = dataColumnContainer.getString("DITG.CODGAR5");

		Long acquisizione = null;
        try {
          acquisizione = (Long)this.getSqlManager().getObject("select acquisizione from ditg where dittao = ?  and ngara5 =?  " +
                "and codgar5 = ? ", new Object []{codiceDitta,codiceLotto,codiceTornata});
        } catch (SQLException e) {
          throw new GestoreException("Errore nella lettura del campo acquisizione della ditta ", null, e);
        }

        boolean isGaraLottiConOffertaUnica = false;
        String tmp = this.getRequest().getParameter("isGaraLottiConOffertaUnica");
        if(tmp == null)
            tmp = (String) this.getRequest().getAttribute("isGaraLottiConOffertaUnica");

        if("true".equals(tmp))
            isGaraLottiConOffertaUnica = true;

        String isGaraElenco = this.getRequest().getParameter("isGaraElenco");
        if(isGaraElenco == null)
          isGaraElenco = (String) this.getRequest().getAttribute("isGaraElenco");

        String isGaraCatalogo = this.getRequest().getParameter("isGaraCatalogo");
        if(isGaraCatalogo == null)
          isGaraCatalogo = (String) this.getRequest().getAttribute("isGaraCatalogo");

        //Si deve controllare se dalla ditta è stata generato un RT
        try {
          String codiceRT=(String)this.sqlManager.getObject("select rtofferta from ditg where ngara5=? and codgar5=? and dittao=?", new Object[]{codiceLotto, codiceTornata, codiceDitta});
          if(codiceRT!=null && !"".equals(codiceRT)){
            if(!isGaraLottiConOffertaUnica){
                //Cancellazione delle occorrenze figlie di DITG associate alla gara, ma riferite al RT creato
                this.cancellaFiglieDITG(codiceLotto, codiceTornata, codiceRT, acquisizione, isGaraLottiConOffertaUnica, isGaraElenco, isGaraCatalogo, status);
                this.sqlManager.update("delete from ditg where ngara5 = ? and codgar5=? and dittao =? ", new Object[] {codiceLotto,codiceTornata,codiceRT});

            }else{
              //Devo ciclare per tutti i lotti e chiamare la cancelazione
              List<?> listaLotti = this.sqlManager.getListVector(
                  "select NGARA from GARE " +
                   "where CODGAR1 = ? and NGARA <> CODGAR1 and GENERE is null",
                   new Object[]{codiceLotto});

              if(listaLotti != null && listaLotti.size() > 0){
                for(int i=0; i < listaLotti.size(); i++){
                  String ngaraLotto = SqlManager.getValueFromVectorParam(listaLotti.get(i),0 ).getStringValue();
                  //Cancallazione delle occorrenze figlie di DITG associate al lotto, ma riferite al RT creato
                  this.cancellaFiglieDITG(ngaraLotto, codiceTornata, codiceRT, acquisizione, isGaraLottiConOffertaUnica, isGaraElenco, isGaraCatalogo, status);
                }
              }
              //Cancallazione delle occorrenze figlie di DITG associate alla gara, ma riferite al RT creato
              this.cancellaFiglieDITG(codiceLotto, codiceTornata, codiceRT, acquisizione, isGaraLottiConOffertaUnica, isGaraElenco, isGaraCatalogo, status);
              this.sqlManager.update("delete from ditg where codgar5=? and dittao = ? ", new Object[] {codiceTornata,codiceRT});
            }
          }
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore nella cancellazione del raggruppamento temporaneo creato a partire dalla ditta " +
                  codiceDitta, null, e);
        }
        //Cancellazione figlie di DITG associate alla gara e della ditta in gara
        this.cancellaFiglieDITG(codiceLotto, codiceTornata, codiceDitta, acquisizione, isGaraLottiConOffertaUnica, isGaraElenco, isGaraCatalogo, status);

	}

	@Override
  public void postDelete(DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void preInsert(TransactionStatus status,
			DataColumnContainer dataColumnContainer) throws GestoreException {

		String numeroGara = dataColumnContainer.getString("DITG.NGARA5");
		String ragioneSociale = dataColumnContainer.getString("DITG.NOMIMO");

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
		boolean isGaraElenco=false;
		boolean isGaraCatalogo=false;

		String isRTI = UtilityStruts.getParametroString(this.getRequest(), "isRTI");
	    String codiceRaggruppamento = UtilityStruts.getParametroString(this.getRequest(), "codiceRaggruppamento");

	    String inserimentoDitteSMAT = UtilityStruts.getParametroString(this.getRequest(),"inserimentoDitteSMAT");
	    if ("SI".equals(inserimentoDitteSMAT) && "0".equals(isRTI)){
	      //Personalizzazione SMAT
	      int ret = smatManager.gestioneSMAT(dataColumnContainer, true);
	      if (ret<0 )
	        throw new GestoreException("Gestione SMAT", "aggiungiDittaSMAT.sediDisattive");
	      else{
	        dataColumnContainer.removeColumns(new String[]{"DITTAO_SMAT","ID_SEDE","ID_FORNITORE","IS_IMPRESA_OA"});
	      }
	    }


	    //Gestione inserimento della RTI
	    if("1".equals(isRTI) && (codiceRaggruppamento == null || "".equals(codiceRaggruppamento))){
	      String tipoRTI = UtilityStruts.getParametroString(this.getRequest(), "tipoRTI");

	      //Inserimento in IMPR della mandataria della RTI per SMAT
	      if ("SI".equals(inserimentoDitteSMAT)){
	        int ret = smatManager.gestioneSMAT(dataColumnContainer, false);
	          if (ret<0 )
	            throw new GestoreException("Gestione SMAT", "aggiungiDittaSMAT.sediDisattive");
	          else{
	            dataColumnContainer.removeColumns(new String[]{"ID_SEDE","ID_FORNITORE","IS_IMPRESA_OA"});
	          }
	      }
	      //Gestione inserimento della RTI
	      GeneManager gene = this.getGeneManager();
	      String codimp = null;
	      if (gene.isCodificaAutomatica("IMPR", "CODIMP")) {
	        // Setto il codice impresa come chiave altrimenti non ritorna sulla riga
	        // giusta
	        codimp = gene.calcolaCodificaAutomatica("IMPR","CODIMP");
	        dataColumnContainer.setValue("DITG.DITTAO", codimp);
	      }else{
	        codimp = dataColumnContainer.getString("DITG.DITTAO");
	        try {
	          //Prima di inserire l'occorrenza si controlla se non esiste già in database
	          //una occorrenza di IMPR
	          //Recupera il genere della gara per differenziare il msg di errore nel caso di elenchi
	          String select="select count(codimp) from impr where codimp=?";
	          Long count = (Long) this.geneManager.getSql().getObject(
	                        select,new Object[] { codimp });
	          if (count.longValue() != 0)   {
	             throw new GestoreException("Il raggruppamento inserito risulta già inserito in anagrafica","raggruppamentoDuplicato",null);
	            }
	        } catch (SQLException e) {
	            throw new GestoreException("Errore nel controllo ditta duplicata", null, e);
	        }
	      }

	      String nomest = dataColumnContainer.getString("NOMEST");
	      String nomimp = nomest;
	      Long tipimp = new Long(3);
	      if(tipoRTI!= null && "10".equals(tipoRTI))
	        tipimp = new Long(10);

	      if(nomimp!=null && !"".equals(nomimp) && nomimp.length()>61)
	        nomimp = nomimp.substring(0, 60);
	      String sqlInsert="insert into IMPR( CODIMP, NOMEST, NOMIMP, TIPIMP) values (?, ?, ?, ?)";
	      try {
	        this.geneManager.getSql().update(sqlInsert, new Object[]{codimp,nomest,nomimp,tipimp});
	      } catch (SQLException e) {
	        throw new GestoreException("Errore nell'inserimento del raggruppamento temporaneo di concorrenti " + codimp, "aggiungiRaggruppamento",e);
	      }

	      //Inserimento della mandataria
	      String coddic = dataColumnContainer.getString("CODDIC");
	      if(coddic!=null && !"".equals(coddic)){
	        String nomdic = dataColumnContainer.getString("NOMEST1");
	        if(nomdic!=null && !"".equals(nomdic) && nomdic.length()>61)
	          nomdic = nomdic.substring(0, 60);
	        Double quodic = null;
	        if(dataColumnContainer.isColumn("QUODIC")){
	          quodic = dataColumnContainer.getDouble("QUODIC");
	          //Poichè quando il campo QUODIC è in sola visualizzazione il suo valore viene visto come String e
	          //non come Double, c'è un errore nella pagina a livello di librerie generali, allora introduco
	          //la varibile quotaPartecip.
	          String quotaPartecip = UtilityStruts.getParametroString(this.getRequest(),"quotaPartecip");
	          if(quotaPartecip!=null && !"".equals(quotaPartecip))
	            quodic = Double.valueOf(quotaPartecip);
	        }
	        sqlInsert="insert into RAGIMP( CODIME9, CODDIC, NOMDIC, IMPMAN, QUODIC) values (?, ?, ?, ?, ?)";
	        try {
	          this.geneManager.getSql().update(sqlInsert, new Object[]{codimp,coddic,nomdic,"1",quodic});
	        } catch (SQLException e) {
	          throw new GestoreException("Errore nell'inserimento della mandataria del raggruppamento temporaneo di concorrenti " + codimp,
	              "aggiungiRaggruppamento.mandataria", new Object[] {codimp},e);
	        }
	      }
	      //Inserimento in IMPR delle mandanti provenienti da SMAT
	      if ("SI".equals(inserimentoDitteSMAT)){
	        this.insertMandantiInIMPR(dataColumnContainer);
	      }


	      //Inserimento delle componenti del raggruppamento
	      GestoreRAGIMPMultiplo sezioniRaggruppamento = new GestoreRAGIMPMultiplo();
	      sezioniRaggruppamento.setRequest(this.getRequest());
	      try{
	        dataColumnContainer.addColumn("IMPR.CODIMP", JdbcParametro.TIPO_TESTO, codimp);
	        sezioniRaggruppamento.preUpdate(status, dataColumnContainer);
	        dataColumnContainer.removeColumns(new String[]{"IMPR.CODIMP"});
	      }catch (GestoreException e) {
	        throw new GestoreException("Errore nell'inserimento delle componenti del raggruppamento temporaneo di concorrenti " + codimp,
	            "aggiungiRaggruppamento.componenti", new Object[] {codimp},e);
	      }


	    }

	    String codiceDitta = dataColumnContainer.getString("DITG.DITTAO");
	    String sortinv = null;
		try {
			List<?> datiGara = this.getSqlManager().getListVector(
					"select CODGAR1, ITERGA, IMPAPP, GENERE, GARTEL, sortinv from GARE,TORN where NGARA = ? and codgar1=codgar",
					new Object[] { numeroGara });
			Vector<?> dati = (Vector<?>) datiGara.get(0);

			codiceTornata = ((JdbcParametro) dati.get(0)).getStringValue();
			if (((JdbcParametro) dati.get(1)).getValue() != null)
    			isProceduraAggiudicazioneAperta = ((Long) ((JdbcParametro)
    					dati.get(1)).getValue()).intValue() == 1;
			importoAppalto = (Double) ((JdbcParametro) dati.get(2)).getValue();

			Long genere = (Long) ((JdbcParametro) dati.get(3)).getValue();
			if(genere!= null && genere.longValue()==10)
			  isGaraElenco=true;
			if(genere!= null && genere.longValue()==20)
			  isGaraCatalogo=true;

			 sortinv = (String)((JdbcParametro) dati.get(5)).getValue();

			//Il controllo seguente va fatto solo per gare telematiche e vanno escluse le gare aperte e ristrette
			String gartel = (String) ((JdbcParametro) dati.get(4)).getValue();
			Long iterga = (Long) ((JdbcParametro) dati.get(1)).getValue();
			if("1".equals(gartel) && !new Long(1).equals(iterga) && !new Long(2).equals(iterga)  && !new Long(4).equals(iterga)){
    			String dittaDaControllare=null;
    			String chiaveMsg=null;
    			String msgLog=null;
    			//Si deve controllare che la mandataria non sia già presente direttamente nella gara o come mandataria di altri RT in gara
    			if("1".equals(isRTI) || dataColumnContainer.isColumn("BARCODE")){
    			  dittaDaControllare = dataColumnContainer.getString("CODDIC");
    			  chiaveMsg = "aggiungiRaggruppamento.mandatariaDuplicata";
    			  msgLog="La mandataria del raggruppamento è già presente in gara.";
        		}else{
    			  //Si controlla che la ditta non sia presente come mandataria di un RT in gara
        		  dittaDaControllare = codiceDitta;
    			  chiaveMsg = "aggiungiRaggruppamento.dittaDuplicata";
    			  msgLog="La ditta del raggruppamento è già presente in gara.";
    			}


    			String datiControllo[] = pgManager.controlloEsistenzaDittaElencoGara(dittaDaControllare, numeroGara,codiceTornata,codiceDitta);
                String tipoGara = "gara";
                if(isGaraElenco)
                  tipoGara = "elenco";
                else if(isGaraCatalogo)
                  tipoGara = "catalogo";
                //Se la ditta è stata trovata si lancia l'errore
                if(!"0".equals(datiControllo[0]))
                  throw new GestoreException(msgLog,chiaveMsg,new Object[]{tipoGara},null);
			}

			if("1".equals(gartel)){
			  //Nel caso di gare telematiche, non si deve guardare il valore del tabellato A1041, si ha semore
			  //la modalità sui lotti, poichè siamo sicuramente con gare a lotto unico o offerta unica. Per
			  //questa tipologia di gare non va applicata la gestione del tabellato
			  modalitaNPROGGsuLotti = true;
			}

			// Determino il valore del progressivo dal campo EDIT.NPROGT per la
			// tornata in analisi e la ditta che si vuole inserire, se presente.
			nProgressivoEDIT = (Long) this.getSqlManager().getObject(
					"select NPROGT from EDIT " + "where CODGAR4 = ? "
							+ "and CODIME = ? ",
					new Object[] { codiceTornata, codiceDitta });

			if (nProgressivoEDIT != null && nProgressivoEDIT.intValue() > 0)
				esisteOccorrenzaInEDIT = true;
			else {
				// Se non esiste alcuna occorrenza nella EDIT relativa alla gara
				// in analisi, allora determino valore massimo del campo EDIT.NPROGT
				nProgressivoEDIT = (Long) this.getSqlManager().getObject(
						"select max(NPROGT) from EDIT where CODGAR4 = ? ",
						new Object[] { codiceTornata });
				// Se la EDIT non ha occorrenze per la gara in analisi,
				// inizializzo la variabile nProgressivoEDIT a 0
				if (nProgressivoEDIT == null)
					nProgressivoEDIT = new Long(0);
			}

			if (modalitaNPROGGsuLotti)
				nProgressivoDITG = (Long) this.getSqlManager().getObject(
						"select max(NPROGG) from DITG " + "where CODGAR5 = ? "
								+ "and NGARA5 = ? ",
						new Object[] { codiceTornata, numeroGara });
			else
				nProgressivoDITG = (Long) this.getSqlManager().getObject(
						"select max(NPROGG) from DITG " + "where CODGAR5 = ? ",
						new Object[] { codiceTornata });

			if (nProgressivoDITG == null)
				nProgressivoDITG = new Long(0);
			// }
		} catch (SQLException e) {
			throw new GestoreException("Errore nel determinare il progressivo "
					+ "della ditta (codice ditta: " + codiceDitta	+ ") nella gara " +
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

		String offertaRT =  UtilityStruts.getParametroString(this.getRequest(), "offertaRT");
		if("1".equals(offertaRT)){
		  numProgDITG = dataColumnContainer.getLong("DITG.NPROGG");
		}
		dataColumnContainer.setValue("DITG.NPROGG", numProgDITG);
		dataColumnContainer.setValue("DITG.CODGAR5", codiceTornata);
		dataColumnContainer.addColumn("DITG.CATIMOK", "1");

		Long acquisiz=null;
	    if(dataColumnContainer.isColumn("DITG.ACQUISIZIONE")){
	      acquisiz = dataColumnContainer.getLong("DITG.ACQUISIZIONE");
	    }

	    String invito=null;
	    if(!"1".equals(sortinv) || new Long(8).equals(acquisiz))
	      invito="1";
		dataColumnContainer.addColumn("DITG.INVGAR", invito);
		if (!isGaraElenco && !isGaraCatalogo){
		    // Il campo Numero d'ordine del plico (DITG.NUMORDPL) viene inizializzato
	        // con lo stesso valore del campo DITG.NPROGG
	        dataColumnContainer.addColumn("DITG.NUMORDPL", numProgDITG);
		}else{
		  dataColumnContainer.addColumn("DITG.ABILITAZ", new Long(6));
		}

		dataColumnContainer.addColumn("DITG.IMPAPPD", JdbcParametro.TIPO_DECIMALE,
				importoAppalto);

		// Inserimento occorrenza DITG
		// gestoreDITG.inserisci(status, dataColumnContainer);

		if (!esisteOccorrenzaInEDIT) {
			// Insert dell'occorrenza nella tabella EDIT
			if (modalitaNPROGGsuLotti)
				this.insertEDIT(status, codiceTornata, numeroGara, codiceDitta,
						ragioneSociale, new Long(nProgressivoEDIT.longValue() + 1),
						this.getRequest());
			else
				this.insertEDIT(status, codiceTornata, numeroGara, codiceDitta,
						ragioneSociale, numProgDITG, this.getRequest());
		}

		//Solo per il profilo protocollo viene gestita la documentazione di gara
		if (this.geneManager.getProfili().checkProtec((String) this.getRequest().getSession().getAttribute(
            CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS", "ALT.GARE.V_GARE_NSCAD-lista.ApriGare")){
		  //Quando si inserisce una ditta in gara si deve in automatico popolare IMPRDOCG
		  //a partire dalle occorrenze di DOCUMGARA, impostando SITUAZDOCI a 2 e PROVENI a 1
		  String codiceGara = dataColumnContainer.getString("DITG.CODGAR5");
		  pgManager.inserimentoDocumentazioneDitta(codiceGara, numeroGara, codiceDitta);
		}

		if(!dataColumnContainer.isColumn("DITG.NCOMOPE"))
	        dataColumnContainer.addColumn("DITG.NCOMOPE", JdbcParametro.TIPO_TESTO, "1");

	}

	@Override
  public void postInsert(DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	  String codiceDitta = datiForm.getString("DITG.DITTAO");
	  String codiceTornata = datiForm.getString("DITG.CODGAR5");
	  String codiceLotto = datiForm.getString("DITG.NGARA5");

	  boolean isGaraLottiConOffertaUnica = false;
      /*
	  String tmp = this.getRequest().getParameter("isGaraLottiConOffertaUnica");
      if(tmp == null)
          tmp = (String) this.getRequest().getAttribute("isGaraLottiConOffertaUnica");

      if("true".equals(tmp))
          isGaraLottiConOffertaUnica = true;
	  */
	  try {
      Long genere = (Long)this.sqlManager.getObject("select genere from gare where ngara=?", new Object[]{codiceLotto});
      if(genere!=null && genere.longValue()==3)
        isGaraLottiConOffertaUnica = true;
    } catch (SQLException e1) {
      throw new GestoreException("Errore nella lettura del genere della gara " + codiceLotto , null, e1);
    }

      if(datiForm.isColumn("DITG.NUM_ORDPL")){
        if(! datiForm.getColumn("DITG.NUM_ORDPL").isModified()){
          datiForm.removeColumns(new String[]{"DITG.NUM_ORDPL"});
        } else {
          // Dalla popup e' stato cambiato il campo DITG.NUNORDPL: nel campo
          // DITG_NUM_ORDPL e' stato memorizzato il nuovo valore
          // Aggiunta del campo DITG.NUMORDPL nel dataColumnContainerDiRiga
          datiForm.addColumn("DITG.NUMORDPL",
              datiForm.getLong("DITG.NUM_ORDPL"));
                // Set del valore originale del campo DITG.NUMORDPL per forzare
                // l'update nel caso di valore null
            if(datiForm.getLong("DITG.NUM_ORDPL") == null)
              datiForm.getColumn("DITG.NUMORDPL").setObjectOriginalValue(new Long(-1));

            datiForm.removeColumns(new String[]{"DITG.NUM_ORDPL"});

          // Se la gara e' a lotti con offerta unica, bisogna aggiornare il campo
          // DITG.NUMORDPL dei vari lotti della gara
          if(isGaraLottiConOffertaUnica){
              try {
                            this.sqlManager.update(
                                    "update DITG set NUMORDPL=? where CODGAR5=? and NGARA5<>? and DITTAO=?",
                                    new Object[]{
                                        datiForm.getColumn("DITG.NUMORDPL").getValue().getValue(),
                                            codiceTornata, codiceLotto, codiceDitta});
                        } catch (SQLException e) {
                            throw new GestoreException("Errore nel update del campo NUMORDPL " +
                                    "dei lotti della gara a lotti con plico unico (CODGAR='" +
                                    codiceTornata + "')", null, e);
                        }
          }
        }
      }else  if(datiForm.isColumn("DITG.NUMORDPL") && datiForm.getColumn("DITG.NUMORDPL").isModified() && isGaraLottiConOffertaUnica){
      // Se la gara e' a lotti con offerta unica, bisogna aggiornare il campo
      // DITG.NUMORDPL dei vari lotti della gara
         try {
              this.sqlManager.update(
                      "update DITG set NUMORDPL=? where CODGAR5=? and NGARA5<>? and DITTAO=?",
                      new Object[]{
                          datiForm.getColumn("DITG.NUMORDPL").getValue().getValue(),
                              codiceTornata, codiceLotto, codiceDitta});
          } catch (SQLException e) {
              throw new GestoreException("Errore nel update del campo NUMORDPL " +
                      "dei lotti della gara a lotti con plico unico (CODGAR='" +
                      codiceTornata + "')", null, e);
          }
      }


	  if(datiForm.isColumn("DITG.NUM_PROGG")){
        if(! datiForm.getColumn("DITG.NUM_PROGG").isModified()){
          datiForm.removeColumns(new String[]{"DITG.NUM_PROGG"});
          //gestoreDITG.update(status, dataColumnContainerDiRiga);
        } else {
          // Dalla popup e' stato cambiato il campo DITG.NPROGG: nel campo
          // DITG_NUM_PROGG e' stato memorizzato il nuovo valore
          // Aggiunta del campo DITG.NPROGG nel nuovo dataColumnContainer
          datiForm.addColumn("DITG.NPROGG",
              datiForm.getLong("DITG.NUM_PROGG"));

          // Aggiornamento del campo EDIT.NPROGT solo se non e' attiva la
          // modalita' di numerazione delle ditte nella DITG nello lotto (e'
          // quindi attiva la numerazione sulla tornata)
          if(isGaraLottiConOffertaUnica){
            // Aggiornamento del campo EDIT.NPROGT solo se l'occorrenza in DITG
            // e' l'unica legata all'occorrenza in EDIT (gara a lotto unico o
            // unico lotto di gara)
            updateEDIT_NPROGT(codiceTornata, codiceDitta,
                datiForm.getColumn("DITG.NUM_PROGG"), true);
          } else {
              if(! pgManager.isModalitaNPROGGsuLotto()){
                // Aggiornamento del campo EDIT.NPROGT solo se l'occorrenza in DITG
                // e' l'unica legata all'occorrenza in EDIT (gara a lotto unico o
                // unico lotto di gara)
                updateEDIT_NPROGT(codiceTornata, codiceDitta,
                    datiForm.getColumn("DITG.NUM_PROGG"), false);
              }
            }
            // Rimozione del dataColumnContainer i campi fittizi
              datiForm.removeColumns(new String[]{"DITG.NUM_PROGG"});
            //gestoreDITG.update(status, dataColumnContainerDiRiga);
          }
      }else if(datiForm.isColumn("DITG.NPROGG") && datiForm.getColumn("DITG.NPROGG").isModified()){
          if(isGaraLottiConOffertaUnica){
            // Aggiornamento del campo EDIT.NPROGT solo se l'occorrenza in DITG
            // e' l'unica legata all'occorrenza in EDIT (gara a lotto unico o
            // unico lotto di gara)
            updateEDIT_NPROGT(codiceTornata, codiceDitta,
                datiForm.getColumn("DITG.NPROGG"), true);
          } else {
              if(! pgManager.isModalitaNPROGGsuLotto()){
                // Aggiornamento del campo EDIT.NPROGT solo se l'occorrenza in DITG
                // e' l'unica legata all'occorrenza in EDIT (gara a lotto unico o
                // unico lotto di gara)
                updateEDIT_NPROGT(codiceTornata, codiceDitta,
                    datiForm.getColumn("DITG.NPROGG"), false);
              }
            }
        }
	}

	@Override
  public void postUpdate(DataColumnContainer datiForm)
			throws GestoreException {
	}

	private void insertEDIT(TransactionStatus status, String codiceTornata,
			String numeroGara, String codiceDitta, String ragioneSociale,
			Long nProgressivo, HttpServletRequest request)
			throws GestoreException {

		// preparazione dei dati da inserire nel container
		Vector<DataColumn> campiEDIT = new Vector<DataColumn>();
		campiEDIT.add(new DataColumn("EDIT.CODGAR4", new JdbcParametro(
				JdbcParametro.TIPO_TESTO, codiceTornata)));
		campiEDIT.add(new DataColumn("EDIT.CODIME", new JdbcParametro(
				JdbcParametro.TIPO_TESTO, codiceDitta)));
		campiEDIT.add(new DataColumn("EDIT.NOMIME", new JdbcParametro(
				JdbcParametro.TIPO_TESTO, ragioneSociale)));
		campiEDIT.add(new DataColumn("EDIT.DOCOK", new JdbcParametro(
				JdbcParametro.TIPO_TESTO, "1")));
		campiEDIT.add(new DataColumn("EDIT.DATOK", new JdbcParametro(
				JdbcParametro.TIPO_TESTO, "1")));
		campiEDIT.add(new DataColumn("EDIT.DITINV", new JdbcParametro(
				JdbcParametro.TIPO_TESTO, "1")));
		campiEDIT.add(new DataColumn("EDIT.NPROGT", new JdbcParametro(
				JdbcParametro.TIPO_NUMERICO, nProgressivo)));

		// Creo il gestore dell'entita' EDIT
		DefaultGestoreEntita gestoreEDIT = new DefaultGestoreEntita("EDIT",	request);
		// Osservazione: si e' deciso di usare la classe DefaultGestoreEntita
		// invece di creare la classe GestoreEDIT come apposito gestore di entita',
		// perche' essa non avrebbe avuto alcuna logica di business

		DataColumnContainer dataColumnContainer = new DataColumnContainer(campiEDIT);
		gestoreEDIT.inserisci(status, dataColumnContainer);

	}

	/**
	 * Metodo per porre a null i campi RIBAUO, IMPOFF, PUNTEC, PUNECO di tutti i
	 * lotti della gara. Funzione usata per gare a lotti con offerta unica,
	 * quando la ditta non viene ammessa alla gara (il campo AMMGAR = 2)
	 *
	 * Oss.: il metodo e' protected per poter essere usato dalle classi
	 * GestoreFasiRicezione e GestoreFasiGara (nel package
	 * it.eldasoft.sil.pg.tags.gestori.submit) che gestiscono campi dell'entita'
	 * DITG e di entita' figlie.
	 *
	 * @param ammgar
	 * @param codiceDitta
	 * @param codiceTornata
	 * @throws GestoreException
	 */
	protected void sbiancaCampi(String ammgar, String codiceDitta,
			String codiceTornata, Long faseGara) throws GestoreException {
		try {
			if ("2".equals(ammgar) || "6".equals(ammgar)) {
				// PUNTEC va sbiancato solo per FASGAR<=5
				String sql = "";
                if (faseGara.longValue() <= 5){
                  if (faseGara.longValue() < 5)
  					sql = "update DITG set RIBAUO = null, IMPOFF = null, PUNTEC = null, "+
  							"PUNECO = null, REQMIN = null, IMPSICAZI = null, IMPMANO= null, IMPPERM = null, IMPCANO = null, " +
  							"AMMGAR = ?, FASGAR = ? where DITTAO = ? " +
  							"and CODGAR5 = ? and NGARA5 <> CODGAR5";
                  else if (faseGara.longValue() == 5)
  					sql = "update DITG set RIBAUO = null, IMPOFF = null, PUNECO = null, IMPSICAZI = null,  IMPMANO = null, IMPPERM = null, IMPCANO = null, "+
  							"AMMGAR = ?, FASGAR = ? where DITTAO = ? and CODGAR5 = ? " +
  							"and NGARA5 <> CODGAR5";
                  //Si effettua lo sbiancamento dei campi solo per le ditte che partecipano alla gara,
                  //poichè per quelle che non partecipano non ci dovrebbe essere il bisogno di sbiancare, campi già nulli.
                  //Inoltre in questo modo non si perde l'informazione sulla fase di gara in cui per un lotto è stato posto
                  //partgar='2'
                  sql += " and (PARTGAR is null or PARTGAR = '1')";

                  this.sqlManager.update(sql, new Object[] { new Long(2), faseGara,
    						codiceDitta, codiceTornata });
                }
			}


		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nell'aggiornamento della ditta con codice '" + codiceDitta +
					"' dei diversi lotti della gara divisa in lotti con plico unico '" +
					codiceTornata	+ "', dopo la non ammissione alla gara della stessa " +
					"ditta in uno dei lotti",	null, e);
		}
	}

	/**
	 * Metodo per salvare le occorrenze nella tabella DITGAMMIS, tabella figlia
	 * di DITG. Questo metodo e' usato dai due gestori di submit
	 * GestoreFasiRicezione e GestoreFasiRicezione
	 *
	 * @throws GestoreException
	 */
	public void gestioneDITGAMMIS(String codiceTornata, String codiceLotto,
			String codiceDitta, Long numeroFaseAttiva, DataColumn[] columns,
			TransactionStatus status) throws GestoreException {
		// Osservazione: la tabella DITGAMMIS e la relativa vista (V_DITGAMMIS) non
		// hanno il campo STEPGAR, perche' l'aggiornamento dello stato di ammissione
		// di una gara puo' avvenire solo negli step con pagina a lista del wizard
		// 'Fasi di ricezione offerte'. In tali step infatti i valori  dell'eventuale
		// STEPGAR sarebbero identici al quelli del campo AMMGAR

    // Gestore dell'entita' DITGAMMIS figlia di DITG
    AbstractGestoreEntita gestoreDITGAMMIS = new DefaultGestoreEntita(
        "DITGAMMIS", this.getRequest());

    // Inserimento dei campi chiave dell'entita' in un nuovo DataColumnContainer
    // per trasferire i campi dall'entita' V_DITGAMMIS (che e' una vista)
    // all'entita' DITGAMMIS
    DataColumn d1 = new DataColumn("DITGAMMIS.CODGAR", new JdbcParametro(
    		JdbcParametro.TIPO_TESTO, codiceTornata));
    d1.setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceTornata));
    d1.setChiave(true);
    DataColumn d2 = new DataColumn("DITGAMMIS.NGARA", new JdbcParametro(
    		JdbcParametro.TIPO_TESTO, codiceLotto));
    d2.setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceLotto));
    d2.setChiave(true);
    DataColumn d3 = new DataColumn("DITGAMMIS.DITTAO", new JdbcParametro(
    		JdbcParametro.TIPO_TESTO, codiceDitta));
    d3.setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceDitta));
    d3.setChiave(true);
    DataColumn d4 = new DataColumn("DITGAMMIS.FASGAR", new JdbcParametro(
    		JdbcParametro.TIPO_NUMERICO, numeroFaseAttiva));
    d4.setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numeroFaseAttiva));
    d4.setChiave(true);

    DataColumnContainer dataColumnContainer = new DataColumnContainer(columns);
    dataColumnContainer.addColumn("DITGAMMIS.CODGAR", d1);
    dataColumnContainer.addColumn("DITGAMMIS.NGARA",  d2);
    dataColumnContainer.addColumn("DITGAMMIS.DITTAO", d3);
    dataColumnContainer.addColumn("DITGAMMIS.FASGAR", d4);
    DataColumn ammgar = dataColumnContainer.getColumn("V_DITGAMMIS.AMMGAR");
  	DataColumn motivEscl = dataColumnContainer.getColumn("V_DITGAMMIS.MOTIVESCL");
  	DataColumn dettMotivEscl = dataColumnContainer.getColumn("V_DITGAMMIS.DETMOTESCL");

  	dataColumnContainer.addColumn("DITGAMMIS.AMMGAR", ammgar);
  	dataColumnContainer.addColumn("DITGAMMIS.MOTIVESCL", motivEscl);
  	dataColumnContainer.addColumn("DITGAMMIS.DETMOTESCL", dettMotivEscl);

    // Creare un nuovo dataColumnContainer
    long numeroOccorrenze = this.geneManager.countOccorrenze("DITGAMMIS",
    		"CODGAR=? and NGARA=? and DITTAO=? and FASGAR=?",	new Object[]{codiceTornata,
    		codiceLotto, codiceDitta, numeroFaseAttiva});

    if(numeroOccorrenze == 0){
    	// Si inserisce l'occorrenza nella DITGAMMIS solo se il valore del campo
    	// DITGAMMIS.AMMGAR e' diverso da null. Infatti inserire tale riga non
    	// farebbe propagare lo stato di ammissione nella vista V_DITGAMMIS
    	if(ammgar.getValue().getValue() != null){

    		gestoreDITGAMMIS.inserisci(status, dataColumnContainer);
    	}
    } else {
    	// Se esiste un'occorrenza in DITGAMMIS e se il valore del campo
    	// DITGAMMIS.AMMGAR e' null, allora tale occorrenza bisogna eliminarla,
    	// altrimenti la si aggiorna
    	if(ammgar.getValue().getValue() == null){
    		gestoreDITGAMMIS.elimina(status, dataColumnContainer);

    	}
    	else {
    		gestoreDITGAMMIS.update(status, dataColumnContainer);

    	}
    }
	}

	/**
   * Metodo per salvare le occorrenze nella tabella DITGSTATI, tabella figlia
	 * di DITG. Questo metodo e' usato dai due gestori di submit
	 * GestoreFasiRicezione e GestoreFasiRicezione
	 *
	 * @throws GestoreException
   */
	protected void gestioneDITGSTATI(String codiceTornata, String codiceLotto,
			String codiceDitta, Long numeroStepAttivo, Long faseGara,
			DataColumnContainer dataColumnContainer, TransactionStatus status) throws GestoreException {

  	// Gestore dell'entita' DITGSTATI figlia di DITG
  	AbstractGestoreEntita gestoreDITGSTATI = new DefaultGestoreEntita(
        "DITGSTATI", this.getRequest());

  	// Inserimento dei campi chiave dell'entita' nel dataColumnContainer
    DataColumn d1 = new DataColumn("DITGAMMIS.CODGAR", new JdbcParametro(
    		JdbcParametro.TIPO_TESTO, codiceTornata));
    d1.setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceTornata));
    d1.setChiave(true);
    DataColumn d2 = new DataColumn("DITGAMMIS.NGARA", new JdbcParametro(
    		JdbcParametro.TIPO_TESTO, codiceLotto));
    d2.setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceLotto));
    d2.setChiave(true);
    DataColumn d3 = new DataColumn("DITGAMMIS.DITTAO", new JdbcParametro(
    		JdbcParametro.TIPO_TESTO, codiceDitta));
    d3.setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceDitta));
    d3.setChiave(true);
    DataColumn d4 = new DataColumn("DITGAMMIS.FASGAR", new JdbcParametro(
    		JdbcParametro.TIPO_NUMERICO, faseGara));
    d4.setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_NUMERICO, faseGara));
    d4.setChiave(true);

  	dataColumnContainer.addColumn("DITGSTATI.CODGAR", d1);
  	dataColumnContainer.addColumn("DITGSTATI.NGARA",  d2);
  	dataColumnContainer.addColumn("DITGSTATI.DITTAO", d3);
  	dataColumnContainer.addColumn("DITGSTATI.FASGAR", d4);
  	dataColumnContainer.addColumn("DITGSTATI.STEPGAR", numeroStepAttivo);

  	long numeroOccorrenze = this.geneManager.countOccorrenze("DITGSTATI",
    		"CODGAR=? and NGARA=? and DITTAO=? and FASGAR=?",
    		new Object[]{codiceTornata, codiceLotto, codiceDitta, faseGara});

    if(numeroOccorrenze == 0){
    	gestoreDITGSTATI.inserisci(status, dataColumnContainer);
    } else {
    	// Si puo' cancellare il record dalla DITGSTATI quando tutti i campi non
    	// chiave sono null
    	DataColumn[] campiDITGSTATInonChiave = dataColumnContainer.getColumns("DITGSTATI", 2);
    	boolean cancellareOccorrenza = true;
    	if(campiDITGSTATInonChiave != null && campiDITGSTATInonChiave.length > 0){
    		for(int iii=0; iii < campiDITGSTATInonChiave.length && cancellareOccorrenza; iii++){
    			if(campiDITGSTATInonChiave[iii] != null && campiDITGSTATInonChiave[iii].getValue() != null &&
    					campiDITGSTATInonChiave[iii].getValue().getValue() != null)
    				cancellareOccorrenza = false;
    		}
    	}

    	if(cancellareOccorrenza)
    		gestoreDITGSTATI.elimina(status, dataColumnContainer);
    	else
    		gestoreDITGSTATI.update(status, dataColumnContainer);
    }
	}

	/**
	 * Metodo per allienamento dei valori dei campi DITG.AMMGAR  e
	 * DITG.FASGAR con i valori di DITGAMMIS.AMMGAR e DITGAMMIS.FASGAR al variare
	 * di questi ultimi campi
	 *
	 * @param codiceTornata
	 * @param codiceLotto
	 * @param codiceDitta
	 * @param numeroFaseAttiva
	 * @param columns
	 * @param status
	 * @throws GestoreException
	 */
	public void aggiornaStatoAmmissioneDITG(String codiceTornata, String codiceLotto,
			String codiceDitta, Long numeroFaseAttiva, DataColumn[] columns,
			TransactionStatus status) throws GestoreException {

        // Gestore dell'entita' DITG
        AbstractGestoreEntita gestoreDITG = new DefaultGestoreEntita(
            "DITG", this.getRequest());
        Vector<?> valoriStatoAmmissioneDITG = null;
        try {
			valoriStatoAmmissioneDITG = this.sqlManager.getVector(
					"select FASGAR, AMMGAR from DITG " +
					"where CODGAR5=? and NGARA5=? and DITTAO=?",
					new Object[]{codiceTornata, codiceLotto, codiceDitta});
		} catch (SQLException e) {
			throw new GestoreException(
					"Errore nella lettura dei dati originali di FASGAR e AMMGAR  " +
					"dalla tabella DITG relativi alla ditta con codice '" + codiceDitta +
					"' che partecipa alla gara con NGARA = '" + codiceLotto +
					"'", null, e);
		}

		if(valoriStatoAmmissioneDITG != null && valoriStatoAmmissioneDITG.size() > 0){
			// Se non si estrae nulla, allora c'è qualche problema nella base dati!!!
			Long   valoreDITG_FASGAR =   (Long)((JdbcParametro)	valoriStatoAmmissioneDITG.get(0)).getValue();

  	    // Inserimento dei campi chiave dell'entita' in un nuovo DataColumnContainer
  	    // per allineare i campi AMMGAR, ANNOFF, MOTIES e FASGAR di DITG con
  	    // i valori dell'entita' DITGAMMIS
  	    DataColumn d1 = new DataColumn("DITG.CODGAR5", new JdbcParametro(
  	    		JdbcParametro.TIPO_TESTO, codiceTornata));
  	    d1.setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceTornata));
  	    d1.setChiave(true);
  	    DataColumn d2 = new DataColumn("DITG.NGARA5", new JdbcParametro(
  	    		JdbcParametro.TIPO_TESTO, codiceLotto));
  	    d2.setChiave(true);
  	    d2.setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceLotto));
  	    DataColumn d3 = new DataColumn("DITG.DITTAO", new JdbcParametro(
  	    		JdbcParametro.TIPO_TESTO, codiceDitta));
  	    d3.setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceDitta));
  	    d3.setChiave(true);

  	    DataColumnContainer dataColumnContainer = new DataColumnContainer(columns);
  	    dataColumnContainer.addColumn("DITG.CODGAR5", d1);
  	    dataColumnContainer.addColumn("DITG.NGARA5", d2);
  	    dataColumnContainer.addColumn("DITG.DITTAO", d3);
  	  	DataColumn ammgar = dataColumnContainer.getColumn("V_DITGAMMIS.AMMGAR");

  	  	// Conversione dei valori del campo DITGAMMIS.AMMGAR nei valori che puo'
  	  	// assumere il campo DITG.AMMGAR, secondo la quanto di seguito indicato:
  	  	// - se DITGAMMIS.AMMGAR = 1, 3, 4, 5, 8, 10 DITG.AMMGAR = 1 (Si);
  	  	// - se DITGAMMIS.AMMGAR = 2, 6 , 7, 9 DITG.AMMGAR = 2 (No);
  	  	// - se DITGAMMIS.AMMGAR = null,  DITG.AMMGAR = null.
  	  	// Viene eseguita la conversione sia del valore che del valore originale
  	  	// del campo DITGAMMIS.AMMGAR
  	  	Long valoreDITGAMMIS_AMMGAR = (Long) ammgar.getValue().getValue();
  	  	Long valoreConvertitoDITGAMMIS_AMMGAR = null;

  	  	if(valoreDITGAMMIS_AMMGAR != null){
  	  		if("1345810".indexOf(valoreDITGAMMIS_AMMGAR.toString()) >= 0)
  	  			valoreConvertitoDITGAMMIS_AMMGAR = new Long(1);
  	  		else if("2679".indexOf(valoreDITGAMMIS_AMMGAR.toString()) >= 0)
  	  			valoreConvertitoDITGAMMIS_AMMGAR = new Long(2);
  	  	}

  	  	//Long valoreConvertitoDITGAMMIS_AMMGARorig = (Long) ammgar.getOriginalValue().getValue();
  	  	Long valoreConvertitoDITGAMMIS_AMMGARorig = null;
  	  	if( ammgar.getOriginalValue()!=null)
  	  	  valoreConvertitoDITGAMMIS_AMMGARorig = (Long) ammgar.getOriginalValue().getValue();
  	  	if(valoreConvertitoDITGAMMIS_AMMGARorig != null){
  	  		if("1345810".indexOf(valoreConvertitoDITGAMMIS_AMMGARorig.toString()) >= 0)
  	  			valoreConvertitoDITGAMMIS_AMMGARorig = new Long(1);
  	  		else if("2679".indexOf(valoreConvertitoDITGAMMIS_AMMGARorig.toString()) >= 0)
  	  			valoreConvertitoDITGAMMIS_AMMGARorig = new Long(2);
  	  	}


  	  	if(valoreDITG_FASGAR != null && numeroFaseAttiva.longValue() > valoreDITG_FASGAR.longValue()){
  	  	  // Se sono in una fase successiva alla fase di esclusione della ditta:
            // caso che non si può verificare, perche' una ditta esclusa in una
            // fase, non e' piu' visibile nelle fasi successive
            // Nel caso delle gare ad elenco invece va gestito il caso in cui ammetto la ditta
            String select="select count(ngara) from gare where codgar1=? and genere=10";
            Long numOccorrenze = null;
            try {
              numOccorrenze = (Long)this.sqlManager.getObject(select, new Object[]{codiceTornata});
            } catch (SQLException e) {
              throw new GestoreException("Errore nella determinazione del genere della gara",null,e);
            }
            if(numOccorrenze!= null && numOccorrenze.longValue()==1){
              if(new Long(1).equals(valoreConvertitoDITGAMMIS_AMMGAR)){
                dataColumnContainer.addColumn("DITG.AMMGAR", JdbcParametro.TIPO_TESTO); // DITG.AMMGAR e' un campo VARCHAR2(1)
                dataColumnContainer.getColumn("DITG.AMMGAR").setObjectValue(
                        valoreConvertitoDITGAMMIS_AMMGAR != null ?
                                valoreConvertitoDITGAMMIS_AMMGAR.toString() : null);
                dataColumnContainer.getColumn("DITG.AMMGAR").setObjectOriginalValue(
                        valoreConvertitoDITGAMMIS_AMMGARorig  != null ?
                                valoreConvertitoDITGAMMIS_AMMGARorig.toString() : null);


                dataColumnContainer.addColumn("DITG.FASGAR", JdbcParametro.TIPO_NUMERICO);
                dataColumnContainer.getColumn("DITG.FASGAR").setObjectValue(null);
                dataColumnContainer.getColumn("DITG.FASGAR").setObjectOriginalValue(valoreDITG_FASGAR);
                dataColumnContainer.addColumn("DITG.MOTIES", JdbcParametro.TIPO_NUMERICO);
                dataColumnContainer.getColumn("DITG.MOTIES").setObjectValue(null);//...
                dataColumnContainer.getColumn("DITG.MOTIES").setObjectOriginalValue(new Long(1));
                dataColumnContainer.addColumn("DITG.ANNOFF", JdbcParametro.TIPO_TESTO);
                dataColumnContainer.getColumn("DITG.ANNOFF").setObjectValue("");//...
                dataColumnContainer.getColumn("DITG.ANNOFF").setObjectOriginalValue(" ");

              }
            }
  	  	}else if(valoreConvertitoDITGAMMIS_AMMGAR == null && valoreConvertitoDITGAMMIS_AMMGARorig != null
  	  	    && ( valoreDITG_FASGAR == null || ( valoreDITG_FASGAR != null && numeroFaseAttiva.longValue() == valoreDITG_FASGAR.longValue()) )){
      	  	//Viene sbiancato l'ammgar
  	  	    /*
  	  	    dataColumnContainer.addColumn("DITG.AMMGAR", JdbcParametro.TIPO_TESTO); // DITG.AMMGAR e' un campo VARCHAR2(1)
              dataColumnContainer.getColumn("DITG.AMMGAR").setObjectValue(null);
              dataColumnContainer.getColumn("DITG.AMMGAR").setObjectOriginalValue("1");
              dataColumnContainer.addColumn("DITG.FASGAR", JdbcParametro.TIPO_NUMERICO);
              dataColumnContainer.getColumn("DITG.FASGAR").setObjectValue(null);
              dataColumnContainer.getColumn("DITG.FASGAR").setObjectOriginalValue(valoreDITG_FASGAR);
              dataColumnContainer.addColumn("DITG.MOTIES", JdbcParametro.TIPO_NUMERICO);
              dataColumnContainer.getColumn("DITG.MOTIES").setObjectValue(null);//...
              dataColumnContainer.getColumn("DITG.MOTIES").setObjectOriginalValue(new Long(0));
              dataColumnContainer.addColumn("DITG.ANNOFF", JdbcParametro.TIPO_TESTO);
              dataColumnContainer.getColumn("DITG.ANNOFF").setObjectValue("");//...
              dataColumnContainer.getColumn("DITG.ANNOFF").setObjectOriginalValue(" ");
              */
  	  	    //Non si deve sbiancare AMMGAR ma si deve allineare il valore di ammgar con quello di ditgammis
  	  	    //della fase precedente se presenti
  	  	    String ammgarDITG = null;
  	  	    try {
  	  	      Long ammgarDITGAMMIS = (Long)this.sqlManager.getObject("select ammgar from ditgammis where codgar=? and ngara=? and" +
                		" dittao=? and fasgar < ? order by fasgar", new Object[]{codiceTornata, codiceLotto, codiceDitta, numeroFaseAttiva });
                if(ammgarDITGAMMIS!=null ){
                  if(ammgarDITGAMMIS.longValue()==1 || ammgarDITGAMMIS.longValue()==3 || ammgarDITGAMMIS.longValue()==4 || ammgarDITGAMMIS.longValue()==5 || ammgarDITGAMMIS.longValue()==8 || ammgarDITGAMMIS.longValue()==10)
                    ammgarDITG = "1";
                  else if(ammgarDITGAMMIS.longValue()==2 || ammgarDITGAMMIS.longValue()==6 || ammgarDITGAMMIS.longValue()==7 || ammgarDITGAMMIS.longValue()==9)
                    ammgarDITG = "2";
                }
              } catch (SQLException e) {
                throw new GestoreException("Errore nella determinazione i valori di DITGAMMIS delle fasi precedenti",null,e);
              }
              dataColumnContainer.addColumn("DITG.AMMGAR", JdbcParametro.TIPO_TESTO); // DITG.AMMGAR e' un campo VARCHAR2(1)
              dataColumnContainer.getColumn("DITG.AMMGAR").setObjectValue(ammgarDITG);
              dataColumnContainer.getColumn("DITG.AMMGAR").setObjectOriginalValue("-1");
              dataColumnContainer.addColumn("DITG.FASGAR", JdbcParametro.TIPO_NUMERICO);
              dataColumnContainer.getColumn("DITG.FASGAR").setObjectValue(null);
              dataColumnContainer.getColumn("DITG.FASGAR").setObjectOriginalValue(valoreDITG_FASGAR);
              dataColumnContainer.addColumn("DITG.MOTIES", JdbcParametro.TIPO_NUMERICO);
              dataColumnContainer.getColumn("DITG.MOTIES").setObjectValue(null);//...
              dataColumnContainer.getColumn("DITG.MOTIES").setObjectOriginalValue(new Long(0));
              dataColumnContainer.addColumn("DITG.ANNOFF", JdbcParametro.TIPO_TESTO);
              dataColumnContainer.getColumn("DITG.ANNOFF").setObjectValue("");//...
              dataColumnContainer.getColumn("DITG.ANNOFF").setObjectOriginalValue(" ");
          }else if(valoreConvertitoDITGAMMIS_AMMGAR != null ){
  	  	  //Viene valorizzato ammgar e viene impostato ad ad uno dei valori 1,3,4,5 e FASGAR.DITG = fase corrente o nullo
  	  	  if(new Long(1).equals(valoreConvertitoDITGAMMIS_AMMGAR) && (valoreDITG_FASGAR == null || (valoreDITG_FASGAR!= null && numeroFaseAttiva.longValue() == valoreDITG_FASGAR.longValue()))){
    	  	    dataColumnContainer.addColumn("DITG.AMMGAR", JdbcParametro.TIPO_TESTO); // DITG.AMMGAR e' un campo VARCHAR2(1)
              dataColumnContainer.getColumn("DITG.AMMGAR").setObjectValue("1");
              dataColumnContainer.getColumn("DITG.AMMGAR").setObjectOriginalValue(null);
              dataColumnContainer.addColumn("DITG.FASGAR", JdbcParametro.TIPO_NUMERICO);
              dataColumnContainer.getColumn("DITG.FASGAR").setObjectValue(null);
              dataColumnContainer.getColumn("DITG.FASGAR").setObjectOriginalValue(valoreDITG_FASGAR);
              dataColumnContainer.addColumn("DITG.MOTIES", JdbcParametro.TIPO_NUMERICO);
              dataColumnContainer.getColumn("DITG.MOTIES").setObjectValue(null);//...
              dataColumnContainer.getColumn("DITG.MOTIES").setObjectOriginalValue(new Long(0));
              dataColumnContainer.addColumn("DITG.ANNOFF", JdbcParametro.TIPO_TESTO);
              dataColumnContainer.getColumn("DITG.ANNOFF").setObjectValue("");//...
              dataColumnContainer.getColumn("DITG.ANNOFF").setObjectOriginalValue(" ");
          }else if(new Long(2).equals(valoreConvertitoDITGAMMIS_AMMGAR)){
  	  	    //Viene valorizzato ammgar e viene impostato ad ad uno dei valori 2,6
  	  	    dataColumnContainer.addColumn("DITG.AMMGAR", JdbcParametro.TIPO_TESTO); // DITG.AMMGAR e' un campo VARCHAR2(1)
              dataColumnContainer.getColumn("DITG.AMMGAR").setObjectValue("2");
              dataColumnContainer.getColumn("DITG.AMMGAR").setObjectOriginalValue(null);
              dataColumnContainer.addColumn("DITG.FASGAR", JdbcParametro.TIPO_NUMERICO);
              dataColumnContainer.getColumn("DITG.FASGAR").setObjectValue(numeroFaseAttiva);
              dataColumnContainer.getColumn("DITG.FASGAR").setObjectOriginalValue(valoreDITG_FASGAR);
              DataColumn moties = dataColumnContainer.getColumn("V_DITGAMMIS.MOTIVESCL");
              Long valoreDITGAMMIS_MOTIES = (Long) moties.getValue().getValue();
              DataColumn annoff = dataColumnContainer.getColumn("V_DITGAMMIS.DETMOTESCL");
              String valoreDITGAMMIS_ANNOFF = (String) annoff.getValue().getValue();
              dataColumnContainer.addColumn("DITG.MOTIES", JdbcParametro.TIPO_NUMERICO);
              dataColumnContainer.getColumn("DITG.MOTIES").setObjectValue(valoreDITGAMMIS_MOTIES);
              dataColumnContainer.getColumn("DITG.MOTIES").setObjectOriginalValue("-100");
              dataColumnContainer.addColumn("DITG.ANNOFF", JdbcParametro.TIPO_TESTO);
              dataColumnContainer.getColumn("DITG.ANNOFF").setObjectValue(valoreDITGAMMIS_ANNOFF);
              dataColumnContainer.getColumn("DITG.ANNOFF").setObjectOriginalValue(" ");
              try {

                // Cancellazine del record in DITGAMMIS con fasgar > fase corrente
                this.sqlManager.update(
                        "delete from DITGAMMIS where CODGAR = ? and NGARA = ? and DITTAO = ? and FASGAR > ? ",
                        new Object[]{codiceTornata, codiceLotto, codiceDitta, numeroFaseAttiva});

              } catch (SQLException e) {
                      throw new GestoreException("Errore nella cancellazione del record " +
                              "relativo alla DITGAMMIS", null, e);
              }
  	  	  }
  	  	}


  	  	// Aggiornamento di DITG
  	  	gestoreDITG.update(status, dataColumnContainer);
  	  	//Nel caso AMMGAR.V_DITGAMMIS non è nullo e AMMGAR.DITGAMMIS = 1, 3,4,5 e FASGAR.DITG = fase corrente o nullo
  	  	//e lotto di gara con offerta unica, si fa l'ulteriore verifica se la ditta è stata esclusa a livello di gara
  	  	//ed eventualmente se ne forza nuovamente l'esclusione allineando AMMGAR e FASGAR.DITG ai valori della DITG relativa alla gara.
  	  	//Ampliata la casistica, sempre nel caso di lotto di gara con offerta unica, al caso di AMMGAR.V_DITGAMMIS nullo e valore originale=2
  	  	if(!codiceTornata.equals(codiceLotto) && (new Long(1).equals(valoreConvertitoDITGAMMIS_AMMGAR) || (valoreConvertitoDITGAMMIS_AMMGAR == null && new Long(2).equals(valoreConvertitoDITGAMMIS_AMMGARorig)))
  	  	    && (valoreDITG_FASGAR == null || (valoreDITG_FASGAR!= null && numeroFaseAttiva.longValue() == valoreDITG_FASGAR.longValue()))){
              try {
                Long genere = (Long)this.sqlManager.getObject("select genere,ngara,codgar1 from gare where codgar1=? and ngara=codgar1", new Object[]{codiceTornata});
                if(genere!= null && genere.longValue()==3){
                  Long fasgarDittaGaraCompl=null;
                  String ammgarDittaGaraCompl=null;

                  Vector<?> datiDittaGaraCompl = this.sqlManager.getVector("select fasgar,ammgar from ditg where ngara5=? and codgar5=? and dittao=? and (ammgar='2' or ammgar='6') and fasgar>?",
                    new Object[]{ codiceTornata,codiceTornata,codiceDitta,numeroFaseAttiva});
                  if(datiDittaGaraCompl!= null && datiDittaGaraCompl.size()>0){
                    fasgarDittaGaraCompl = (Long)((JdbcParametro) datiDittaGaraCompl.get(0)).getValue();
                    ammgarDittaGaraCompl = (String)((JdbcParametro) datiDittaGaraCompl.get(1)).getValue();
                  }
                  if(fasgarDittaGaraCompl!= null){
                    String update = "update ditg set ammgar = ?, fasgar = ? where codgar5=? and ngara5=? and dittao=? ";
                    this.sqlManager.update(update, new Object[]{ammgarDittaGaraCompl,fasgarDittaGaraCompl,codiceTornata,codiceLotto,codiceDitta});
                  }
                }
              } catch (SQLException e) {
                throw new GestoreException("Errore nell'allineamento dei dati della ditg per un lotto di una gara a plico unico", null, e);
              }
          }


  	  	  if(codiceTornata.equals(codiceLotto)){
    			// Se gara a lotti con offerta unica, si e' appena aggiornato nella tabella
    			// DITG il record relativo all'occorrenza complementare, mentre
    			// bisogna aggiornare i relativi record per ciascun lotto esistente

    			try {
    				List<?> listaCodiceLotti = this.sqlManager.getListVector(
    						"select NGARA,FASGAR from GARE where CODGAR1=? and NGARA<>?",
    						new Object[]{codiceTornata, codiceLotto});
    				if(listaCodiceLotti != null && listaCodiceLotti.size() > 0){
    				  for(int i=0; i < listaCodiceLotti.size(); i++){
    					    String ngaraLotto = SqlManager.getValueFromVectorParam(listaCodiceLotti.get(i), 0).stringValue();
    						if(valoreDITGAMMIS_AMMGAR!= null && (valoreDITGAMMIS_AMMGAR.longValue()==2 || valoreDITGAMMIS_AMMGAR.longValue()==6 || valoreDITGAMMIS_AMMGAR.longValue()==7 || valoreDITGAMMIS_AMMGAR.longValue()==9)){
    						  /*
    						  Long fasgarDitta = (Long)this.sqlManager.getObject("select fasgar from ditg where codgar5=? and ngara5=? and dittao=?",
    						    new Object[]{codiceTornata,ngaraLotto,codiceDitta});
    						  String update = "update ditg set ammgar = '2' ";
    						  if(fasgarDitta== null || (fasgarDitta!= null && fasgarDitta.longValue() > numeroFaseAttiva.longValue()))
    						    update+=", fasgar = " + numeroFaseAttiva.toString();
    						  update+=" where codgar5=? and ngara5=? and dittao=?";
    						  this.sqlManager.update(update, new Object[]{codiceTornata,ngaraLotto,codiceDitta});
    						  */
    						  //String update = "update ditg set ammgar = ?, fasgar = ? where codgar5=? and ngara5=? and dittao=?  and (ammgar is null or ammgar <> '2')";
    						  Long fasgarDitta = (Long)this.sqlManager.getObject("select fasgar from ditg where codgar5=? and ngara5=? and dittao=?",
                                  new Object[]{codiceTornata,ngaraLotto,codiceDitta});
    						  String update = "update ditg set ammgar = ? ";
    						  if(fasgarDitta==null || (fasgarDitta !=null && fasgarDitta.longValue()>numeroFaseAttiva.longValue()))
    						    update += ", fasgar = " + numeroFaseAttiva.longValue();

    						  if(numeroFaseAttiva.longValue() < -3){
    						    update += ", invgar = null ";
    						  }else if(numeroFaseAttiva.longValue() == -3){
    						    update += ", invgar = '2' ";
    						  }
    						  update += " where codgar5=? and ngara5=? and dittao=?  and (ammgar is null or ammgar <> '2')";
    						  this.sqlManager.update(update, new Object[]{"2",codiceTornata,ngaraLotto,codiceDitta});
    						}else{
    						  /*
    						  String update = "update ditg set ammgar = ?, fasgar =? where codgar5=? and ngara5=? and dittao=?  ";
    						  Long fasgarV_ditgammis = (Long)this.sqlManager.getObject("select fasgar from v_ditgammis where codgar=? and ngara=? and dittao=?  " +
    						  		"and (ammgar = 2 or ammgar = 6) and fasgar >= ?",
                                new Object[]{codiceTornata,ngaraLotto,codiceDitta, numeroFaseAttiva});

    						  String valoreAmmgarLotto = "2";
    						  if(fasgarV_ditgammis==null){
    						    valoreAmmgarLotto = "1";
    						  }
    						  */
    						  /*
    						  Long numOccorrenzeV_ditgammis = (Long)this.sqlManager.getObject("select count(codgar) from v_ditgammis where codgar=? and ngara=? and dittao=?  " +
                                  "and (ammgar = '2' or ammgar = '6')",
                                new Object[]{codiceTornata,ngaraLotto,codiceDitta});
    						  if(numOccorrenzeV_ditgammis == null || (numOccorrenzeV_ditgammis!=null && numOccorrenzeV_ditgammis.longValue()==0)){ //aggiungere and con partgar=2 del lotto
        						  String update = "update ditg set ammgar = ?, fasgar =? where codgar5=? and ngara5=? and dittao=?  ";
                                  this.sqlManager.update(update, new Object[]{null,null,codiceTornata,ngaraLotto,codiceDitta});
      						  }
      						  */
    						  String update = "update ditg set ammgar = ?, fasgar =?";
    						  if(numeroFaseAttiva.longValue() <= -3)
                                update += ", invgar = '1' ";
    						  update += " where codgar5=? and ngara5=? and dittao=?";
    						  List<?> listaFasgarV_ditgammis = this.sqlManager.getListVector("select fasgar from v_ditgammis where codgar=? and ngara=? and dittao=?  " +
                                  "and (ammgar = '2' or ammgar = '6') order by fasgar",
                                new Object[]{codiceTornata,ngaraLotto,codiceDitta});
    						  String partgar = (String)this.sqlManager.getObject("select partgar from ditg where ngara5=? and codgar5=? and dittao=?", new Object[]{ngaraLotto, codiceTornata, codiceDitta});
    						  if((listaFasgarV_ditgammis==null || (listaFasgarV_ditgammis!=null && listaFasgarV_ditgammis.size()==0)) && !"2".equals(partgar)){
    						    String ammgarLotto= null;
    						    Long conteggio = (Long)this.sqlManager.getObject("select count(ngara) from ditgammis where codgar=? and ngara=? and dittao=? and ammgar is not null",
    						        new Object[]{codiceTornata,ngaraLotto,codiceDitta});
    						    if(conteggio!=null && conteggio.longValue()>0)
    						      ammgarLotto="1";
    						    this.sqlManager.update(update, new Object[]{ammgarLotto,null,codiceTornata,ngaraLotto,codiceDitta});
    						  }else if(listaFasgarV_ditgammis!=null && listaFasgarV_ditgammis.size()>0){
    						    Long fasgar = SqlManager.getValueFromVectorParam(listaFasgarV_ditgammis.get(0), 0).longValue();
    						    this.sqlManager.update(update, new Object[]{"2",fasgar,codiceTornata,ngaraLotto,codiceDitta});
    						  }

    						}

    					}
    				}
  				} catch (SQLException e) {
  					throw new GestoreException("Errore nell'eliminazione dei record " +
  							"nella tabella DITGAMMIS, relativi ai lotti della gara a lotti " +
  							"con plico unico (CODGAR = '" + codiceTornata + "'", null, e);
  				}
    		}
		}
	}

	/**
	   * Aggiornamento del campo EDIT.NPROGT: per gare a lotti con offerta unica si
	   * aggiorna sia la tabella EDIT che la DITG, mentre per gli altri tipi di gara
	   * solo se l'occorrenza in DITG e' l'unica in relazione con la tabella EDIT
	   * (gara a lotto unico o unico lotto di gara)
	   *
	   * @param codiceGara
	   * @param codiceImpresa
	   * @param numProgOriginale
	   * @param numProgNuovo
	   * @param isGaraLottiConOffertaUnica
	   * @throws GestoreException
	   */
	  private void updateEDIT_NPROGT(String codiceGara, String codiceImpresa,
	      DataColumn campoDITG_NPROGG, boolean isGaraLottiConOffertaUnica)
	                throws GestoreException{

	    //SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
	    //    this.getServletContext(), SqlManager.class);
	    try{
	        if(isGaraLottiConOffertaUnica){
	            // Aggiornamento del campo EDIT.NPROGT
	            sqlManager.update(
	            "update EDIT set NPROGT = ? " +
	             "where CODGAR4 = ? " +
	               "and CODIME = ? " +
	               "AND NPROGT = ?",
	          new Object[]{(Long) campoDITG_NPROGG.getValue().getValue(),
	                codiceGara, codiceImpresa,
	                (Long) campoDITG_NPROGG.getOriginalValue().getValue()});

	            // Aggiornamento del campoDITG.NPROGG per tutti i lotti della gara
	            sqlManager.update("update DITG set NPROGG = ? where CODGAR5 = ? and DITTAO = ? ",
	                    new Object[]{(Long) campoDITG_NPROGG.getValue().getValue(),
	            codiceGara, codiceImpresa});
	        } else {
	            Long numeroDitte = (Long) sqlManager.getObject(
	          "select count(*) from DITG " +
	           "where CODGAR5 = ? " +
	             "and DITTAO = ? ", new Object[]{codiceGara, codiceImpresa});

	            if(numeroDitte.intValue() == 1){
	                sqlManager.update(
	            "update EDIT set NPROGT = ? " +
	             "where CODGAR4 = ? " +
	               "and CODIME = ? " +
	               "AND NPROGT = ?",
	          new Object[]{(Long) campoDITG_NPROGG.getValue().getValue(),
	                codiceGara, codiceImpresa,
	                (Long) campoDITG_NPROGG.getOriginalValue().getValue()}, 1);
	            }
	        }
	    } catch(SQLException s){
	      throw new GestoreException(
	          "Errore nell'operazione di aggiornamento del campo EDIT.NPROGT in " +
	          "seguito alla modifica del campo DITG.NPROGG dalle fasi di ricezione " +
	          "domande/offerte", null, s);
	    }
	  }

	  /**
       * Inserimento delle mandanti delle sezioni dinamiche della pagina ditg-schedaPopup-insert.jsp
       *
       * @param impl
       * @throws GestoreException
       */
	  void insertMandantiInIMPR(DataColumnContainer impl) throws GestoreException{
	    String nomeCampoNumeroRecord = "NUMERO_RAGIMP";
	    String nomeCampoMod = "MOD_RAGIMP";

	    if (impl.isColumn(nomeCampoNumeroRecord)) {

	      // Osservazione: si e' deciso di usare la classe DefaultGestoreEntita, invece
	      // di creare la classe GestoreRAGIMP come apposito gestore di entita', perche'
	      // essa non avrebbe avuto alcuna logica di business

	      String[] campiChiave = new String[]{"RAGIMP.CODIME9", "RAGIMP.CODDIC"};
	      Vector<String> vectorNomiCampiChiave = new Vector<String>(
	                java.util.Arrays.asList(campiChiave));

	      int numeroImprese = impl.getLong(nomeCampoNumeroRecord).intValue();

	      // Cancellazione delle imprese eliminate nella scheda
	      for (int i = 1; i <= numeroImprese; i++) {
	        DataColumn[] campiImprese = impl.getColumnsBySuffix("_" + i, false);

	        // effettuo il parsing dei campi fittizzi del singolo indirizzo e
	        // setto i campi chiave
	        for (int j = 0; j < campiImprese.length; j++) {
	          // setto i campi chiave
	          if (vectorNomiCampiChiave.contains(campiImprese[j].getNomeFisico()))
	              campiImprese[j].setChiave(true);
	        }

	        DataColumnContainer newImpl = new DataColumnContainer(campiImprese);

	        boolean updateOccorrenza = newImpl.isColumn(nomeCampoMod)
	            && "1".equals(newImpl.getString(nomeCampoMod));

	        if (updateOccorrenza){
              // l'occorrenza è da inserire in quanto è una di quelle senza chiave
              // e risulta attiva (non eliminata)
	          int ret = smatManager.gestioneSMAT(newImpl, true);
              if (ret<0 ){
                throw new GestoreException("Gestione SMAT", "aggiungiDittaSMAT.sediDisattive");
              }else{
                String coddic = newImpl.getString("RAGIMP.CODDIC");
                  impl.setValue("RAGIMP.CODDIC_" + i, coddic);
              }
            }
	      }
	    }

	  }

	  /**
       * Cancellazione delle entità figlie di DITG
       *
       * @param codiceLotto
       * @param codiceTornata
       * @param codiceDitta
       * @param acquisizione
       * @param isGaraLottiConOffertaUnica
       * @param isGaraElenco
       * @param isGaraCatalogo
       * @param status
       * @throws GestoreException
       */
	  private void cancellaFiglieDITG(String codiceLotto,String codiceTornata,String codiceDitta,Long acquisizione,
	      boolean isGaraLottiConOffertaUnica, String isGaraElenco, String isGaraCatalogo, TransactionStatus status) throws GestoreException{

	    if (codiceTornata.equalsIgnoreCase(codiceLotto)) {
          // Caso di gara a lotti con offerta unica
          // Cancellazione delle occorrenze nella tabelle EDIT e IMDODO
          this.getGeneManager().deleteTabelle(new String[]{"EDIT"},
                  "CODGAR4 = ? and CODIME = ?",   new Object[]{codiceTornata, codiceDitta});
          this.getGeneManager().deleteTabelle(new String[] { "IMDODO" },
                  "CODGAR8 = ? and CODIME8 = ?", new Object[]{codiceTornata, codiceDitta});
        } else {
          // Determino se l'occorrenza in cancellazione nella DITG e' l'unica
          // in collegata alla EDIT: se si, allora cancella l'occorrenza nella
          // EDIT e nella sua entita' figlia (IMDODO)
          try {
              Long numeroRiferimenti = (Long) this.getSqlManager().getObject(
                      "select count(*) from DITG where CODGAR5 = ? and DITTAO = ? ",
                      new Object[] { codiceTornata, codiceDitta });

              if (numeroRiferimenti != null   && numeroRiferimenti.intValue() == 1) {
                  // cancellare anche le occorrenze nella tabelle EDIT e IMDODO
                  this.getGeneManager().deleteTabelle(new String[]{"EDIT"},
                          "CODGAR4 = ? and CODIME = ?", new Object[]{codiceTornata,
                          codiceDitta });
                  this.getGeneManager().deleteTabelle(new String[]{"IMDODO"},
                          "CODGAR8 = ? and CODIME8 = ?", new Object[]{codiceTornata,
                          codiceDitta });
              }
          } catch (SQLException e) {
              throw new GestoreException(
                      "Errore nel determinare se cancellare le occorrenze delle tabelle " +
                      "EDIT e IMDODO", null, e);
          }
      }
      // Cancellazione delle entita figlie della DITG.
      this.getGeneManager().deleteTabelle(new String[] { "DPUN", "DPRE" },
              "DITTAO = ? and NGARA = ?",
              new Object[] { codiceDitta, codiceLotto });
      this.getGeneManager().deleteTabelle(new String[] { "OPSD" },
              "CODGAR3 = ? and DITTAO = ? and NGARA6 = ?",
              new Object[] { codiceTornata, codiceDitta, codiceLotto });
      this.getGeneManager().deleteTabelle(new String[] { "RAGDET" },
              "NGARA = ? and codimp = ?",
              new Object[] { codiceLotto, codiceDitta });
      this.getGeneManager().deleteTabelle(new String[] { "DITGAVVAL" },
          "NGARA = ? and DITTAO = ?",
          new Object[] { codiceLotto, codiceDitta });
      this.getGeneManager().deleteTabelle(new String[] { "DITGAMMIS", "DITGSTATI" },
              "CODGAR = ? and NGARA = ? and DITTAO = ?",
              new Object[] { codiceTornata, codiceLotto, codiceDitta });
      if(codiceTornata.equals(codiceLotto)){
          // Se gara a lotti con offerta unica, allora oltre a cancellare la ditta
          // nell'occorrenza complementare si cancellano le ditte nei singoli lotti
          this.getGeneManager().deleteTabelle(new String[] { "DITGAMMIS", "DITGSTATI" },
                  "CODGAR = ? and NGARA <> ? and DITTAO = ?",
                  new Object[] { codiceTornata, codiceLotto, codiceDitta });
          //
          try {
            List<?> listaLotti = this.sqlManager.getListVector("select ngara from gare where codgar1=? and ngara<>codgar1", new Object[]{codiceTornata});
            if(listaLotti!=null && listaLotti.size()>0){
              for(int i=0;i<listaLotti.size();i++){
                String ngaraLotto =SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).stringValue();
                this.sqlManager.update("delete from DPRE where DITTAO = ? and NGARA=?", new Object[] {codiceDitta,ngaraLotto});
              }

            }

          } catch (SQLException e) {
              throw new GestoreException(
                  "Errore durante l'eliminazione dele righe delle tabella DPRE ", null,  e);
          }
      }


      // Se la gara e' di tipo 'a lotti con offerta unica' bisogna eliminare la
      // ditta ad ogni lotto esistente, oltre alla occorrenza complementare in GARE

      if(isGaraLottiConOffertaUnica){

              //Cancellazione di IMPRDOCG
              try {
                this.sqlManager.update("delete from IMPRDOCG where CODGAR = ? and CODIMP = ?", new Object[] { codiceTornata, codiceDitta});
              } catch (SQLException e) {
                throw new GestoreException(
                    "Errore durante l'eliminazione dele righe delle tabella IMPRDOCG ", null,  e);
              }
      } else {

          //cancellazione di W_DOCDIG
          String delete="delete from w_docdig where IDPRG in(select IMPRDOCG.IDPRG from IMPRDOCG where CODGAR = ? and NGARA=? and CODIMP = ?) and IDDOCDIG in (select IMPRDOCG.IDDOCDG from IMPRDOCG where CODGAR = ? and NGARA=? and CODIMP = ?)";
          try {
            this.sqlManager.update(delete, new Object[] { codiceTornata, codiceLotto, codiceDitta,codiceTornata,codiceLotto,codiceDitta});
          } catch (SQLException e) {
            throw new GestoreException(
                "Errore durante l'eliminazione dele righe delle tabella IMPRDOCG ", null,  e);
          }

          //Cancellazione di IMPRDOCG
          try {
            this.sqlManager.update("delete from IMPRDOCG where CODGAR = ? and CODIMP = ? and NGARA=?", new Object[] { codiceTornata, codiceDitta,codiceLotto});
          } catch (SQLException e) {
            throw new GestoreException(
                "Errore durante l'eliminazione dele righe delle tabella IMPRDOCG ", null,  e);
          }
          //Cancellazione di ISCRIZCAT e ISCRIZCLASSI

          if("1".equals(isGaraElenco) || "1".equals(isGaraCatalogo)){
            try {
              this.sqlManager.update("delete from ISCRIZCAT where CODGAR = ? and CODIMP = ? and NGARA=?", new Object[] { codiceTornata, codiceDitta,codiceLotto});
              this.sqlManager.update("delete from ISCRIZCLASSI where CODGAR = ? and CODIMP = ? and NGARA=?", new Object[] { codiceTornata, codiceDitta,codiceLotto});
            } catch (SQLException e) {
              throw new GestoreException(
                  "Errore durante l'eliminazione dele righe delle tabella ISCRIZCAT ", null,  e);
            }
          }
      }



      //  Se la ditta è stata prelevata da un elenco si deve decrementare il numero di inviti
      if(acquisizione !=null && acquisizione.longValue()==3){
        try {
          String select="select genere,elencoe from gare where ngara=?";
          List<?> datiGara = this.getSqlManager().getListVector( select,new Object[] { codiceLotto });
          Vector<?> dati = (Vector<?>) datiGara.get(0);
          Long genere = (Long) ((JdbcParametro) dati.get(0)).getValue();
          if(genere== null || (genere!= null && genere.longValue()!=10)){
            String numeroElenco = (String) ((JdbcParametro) dati.get(1)).getValue();
            if(numeroElenco!=null && !"".equals(numeroElenco)){
              String codiceElenco = "$" + numeroElenco;

              //Si determina il tipo algo dell'elenco
              Long tipoalgo = (Long)sqlManager.getObject("select tipoalgo from garealbo where ngara=? and codgar= ?", new Object[]{numeroElenco, codiceElenco});


              select="select catiga, numcla from catg where ngara=? and ncatg=1";
              String catiga=null;
              Long numcla=null;
              /*
              String catiga = (String) this.geneManager.getSql().getObject(
                  select,new Object[] { codiceLotto });
              */
              Vector<?> datiCatg = this.geneManager.getSql().getVector(select, new Object[] { codiceLotto });
              if(datiCatg!=null && datiCatg.size()>0){
                catiga = (String)((JdbcParametro) datiCatg.get(0)).getValue();
                numcla = (Long)((JdbcParametro) datiCatg.get(1)).getValue();
              }

            //Devo ricavare il tipo della gara
              Long tipgen=null;
              try {
                tipgen = (Long)sqlManager.getObject("select tipgen from torn where codgar=?", new Object[]{codiceTornata});
              } catch (SQLException e) {
                throw new GestoreException("Errore nella lettura della tipo della gara ", null, e);
              }

              if(tipoalgo.longValue()==1 || tipoalgo.longValue()==3 || tipoalgo.longValue()==4 || tipoalgo.longValue()==5 || tipoalgo.longValue()==11 || tipoalgo.longValue()==12 || tipoalgo.longValue()==14 || tipoalgo.longValue()==15){
                if(catiga== null || "".equals(catiga))
                    catiga="0";

                  pgManager.aggiornaNumInviti(codiceElenco, numeroElenco, codiceDitta, catiga, "DEL", tipgen,null,null);
                  //Se per la categoria prevalente è stata specificata la classe, si devono calcolare gli inviti
                  //sul dettaglio della classe della categoria d'iscrizione dell'operatore
                  if(!"0".equals(catiga) && numcla!=null){
                    pgManager.aggiornaNumInviti(codiceElenco, numeroElenco, codiceDitta, catiga, "DEL", tipgen,numcla,null);
                  }
              }else if(tipoalgo.longValue()==2 || tipoalgo.longValue()==6 || tipoalgo.longValue()==7 || tipoalgo.longValue()==10 || tipoalgo.longValue()==13){
                pgManager.aggiornaNumInviti(codiceElenco, numeroElenco, codiceDitta, "0", "DEL", tipgen,null,null);

              }else if(tipoalgo.longValue()==8 || tipoalgo.longValue()==9){
                //Si deve fare il conteggio sulla categoria '0'
                pgManager.aggiornaNumInviti(codiceElenco, numeroElenco, codiceDitta, "0", "DEL", tipgen,null,null);
                String stazioneAppaltante = (String)this.sqlManager.getObject("select cenint from torn where codgar=?", new Object[]{codiceTornata});
                //Si deve fare il conteggio anche su ISCRIZUFF.INVREA sempre per la categoria '0'
                pgManager.aggiornaNumInviti(codiceElenco, numeroElenco, codiceDitta, "0", "DEL", tipgen,null,stazioneAppaltante);
              }
            }
          }

        } catch (SQLException e) {
          throw new GestoreException(
              "Errore durante l'aggiornamento del numero inviti ", null,  e);
        }

      }


	  }

	  /**
	   *
	   * @param codgar
	   * @param ngara
	   * @param ditta
	   * @param fase
	   * @throws SQLException
	   */
	  public void gestioneDITGAMMIS(String codgar, String ngara, String ditta, Long fase, TransactionStatus status) throws SQLException{
	    String selectCOUNT_DITGAMMIS = "select count(*) from ditgammis where codgar = ? and ngara = ? and dittao = ? and fasgar = ?";
        String insertDITGAMMIS = "insert into ditgammis (codgar, ngara, dittao, fasgar, ammgar) values (?,?,?,?,?)";
        String updateDITGAMMIS = "update ditgammis set ammgar = ? where codgar = ? and ngara = ? and dittao = ? and fasgar = ?";
        Long count_ditgammis = (Long) this.sqlManager.getObject(selectCOUNT_DITGAMMIS, new Object[] { codgar, ngara,
            ditta, fase });
        if (count_ditgammis != null && count_ditgammis.longValue() > 0) {
          // Aggiornamento
          this.sqlManager.update(updateDITGAMMIS, new Object[] { new Long(2), codgar, ngara, ditta, fase });
        } else {
          // Inserimento
          this.sqlManager.update(insertDITGAMMIS, new Object[] { codgar, ngara, ditta, fase, new Long(2) });
        }
	  }
}