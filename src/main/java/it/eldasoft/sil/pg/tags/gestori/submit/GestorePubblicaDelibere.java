/*
 * Created on 13/feb/2019
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import intra.regionemarche.ResultClass;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneRegioneMarcheManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;


public class GestorePubblicaDelibere extends
AbstractGestoreEntita {

  static Logger               logger         = Logger.getLogger(GestorePubblicaSuPortale.class);

private GestioneRegioneMarcheManager gestioneRegioneMarcheManager;
  
  public GestorePubblicaDelibere() {
    super(false);
  }
  
  @Override
  public String getEntita() {
    // TODO Auto-generated method stub
    return "TORN";
  }

  @Override
  public void postDelete(DataColumnContainer arg0) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void postInsert(DataColumnContainer arg0) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void postUpdate(DataColumnContainer arg0) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void preDelete(TransactionStatus arg0, DataColumnContainer arg1) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    
    String errMsgEvento = "";
    String codgar = datiForm.getString("CODGAR");
    String ngara = datiForm.getString("NGARA");
    Timestamp datpub = datiForm.getData("DATPUB");
    if(datpub == null){
      datpub = new Timestamp(System.currentTimeMillis());
      Date data = new Date(datpub.getTime());
      data = DateUtils.truncate(data, Calendar.DATE);
      datpub = new Timestamp(data.getTime());
    }
    String logOggetto ="";
    String listaDoc = "";
    Integer livEvento = 1;
    String listaDocumentiString = (String) datiForm.getString("LISTADOC");
    
    Vector elencoCampi1 = new Vector();
    elencoCampi1.add(new DataColumn("PUBBLI.CODGAR9",
        new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar)));
    elencoCampi1.add(new DataColumn("PUBBLI.NUMPUB", new JdbcParametro(JdbcParametro.TIPO_NUMERICO,null)));
    elencoCampi1.add(new DataColumn("PUBBLI.TIPPUB",
        new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(15))));
    elencoCampi1.add(new DataColumn("PUBBLI.DATPUB",
        new JdbcParametro(JdbcParametro.TIPO_DATA, datpub)));

    DataColumnContainer container = new DataColumnContainer(elencoCampi1);
    // si predispone il gestore per l'aggiornamento dell'entità
    DefaultGestoreEntitaChiaveNumerica gestore = new DefaultGestoreEntitaChiaveNumerica(
        "PUBBLI", "NUMPUB",new String[] { "CODGAR9" }, this.getRequest());
    
    container.getColumn("PUBBLI.NUMPUB").setValue(
        new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null));
    gestore.inserisci(status, container);

    ngara = UtilityStringhe.convertiNullInStringaVuota(ngara);
    this.getRequest().setAttribute("pubblicazioneEseguita", "1");

    Long genPubblicazione = null;
    String messageKey = null;
    try {
      if (GestioneRegioneMarcheManager.isConfiguratoPerRM()) {
    	gestioneRegioneMarcheManager = (GestioneRegioneMarcheManager) UtilitySpring.getBean("gestioneRegioneMarcheManager",
    	        this.getServletContext(), GestioneRegioneMarcheManager.class);
    	String temaRegionale = UtilityStruts.getParametroString(this.getRequest(), "temaRegionale");
		String tipologiaBando = UtilityStruts.getParametroString(this.getRequest(), "tipologiaBando");
		String strutturaRegionale = UtilityStruts.getParametroString(this.getRequest(), "strutturaRegionale");
		String sezioneLavoriPubblici = UtilityStruts.getParametroString(this.getRequest(), "lavoriPubblici");
		String idBando = UtilityStruts.getParametroString(this.getRequest(), "bandoIdRegMarche");
		genPubblicazione = (Long) sqlManager.getObject("select genere from V_GARE_GENERE where codgar = ? and genere <100 ", new Object[]{codgar});
		if ("1".equals(sezioneLavoriPubblici) || "2".equals(sezioneLavoriPubblici)) {
			this.sqlManager.update("update TORN set sezlavpub=? where CODGAR=?",new Object[]{sezioneLavoriPubblici,codgar});
		}
		if (idBando != null && !"".equals(idBando)) {
			ResultClass risposta = this.gestioneRegioneMarcheManager.updateDelibera(ngara, codgar,
					genPubblicazione, datpub, new Long(strutturaRegionale), new Long(temaRegionale),
					new Long(tipologiaBando), "BANDO", new Long(idBando));
			if (!risposta.isResult()) {
				String msg = risposta.getError();
				livEvento = 3;
				messageKey = "errors.gestoreException.*.ws.bandiRegioneMarche.updateBando.esitoNOK";
				errMsgEvento = this.resBundleGenerale.getString(messageKey);
				errMsgEvento = errMsgEvento.replace("{0}", msg);
				throw new GestoreException(
						"L'aggiornamento del bando da parte del servizio remoto non e' andato a buon fine",
						"ws.bandiRegioneMarche.updateBando.esitoNOK", new Object[] { msg },
						new Exception());
			}
		}else {
	    	ResultClass risposta = this.gestioneRegioneMarcheManager.insertDelibera(ngara, codgar,
					genPubblicazione, datpub, new Long(strutturaRegionale), new Long(temaRegionale),
					new Long(tipologiaBando), "BANDO");
	    	if (!risposta.isResult()) {
				String msg = risposta.getError();
				livEvento = 3;
				messageKey = "errors.gestoreException.*.ws.bandiRegioneMarche.addBando.esitoNOK";
				errMsgEvento = this.resBundleGenerale.getString(messageKey);
				errMsgEvento = errMsgEvento.replace("{0}", msg);
				throw new GestoreException(
						"L'inserimento del bando da parte del servizio remoto non e' andato a buon fine",
						"ws.bandiRegioneMarche.addBando.esitoNOK", new Object[] { msg }, new Exception());
			}
		}
      }

      List<String> itemsString = Arrays.asList(listaDocumentiString.split(",\\s*"));
      for(int i = 0; i<itemsString.size();i++){
        Long norddocg = Long.parseLong(itemsString.get(i).replaceAll("[^0-9]", ""));
        if(!listaDoc.equals("")){listaDoc = listaDoc+", ";}
        listaDoc = listaDoc + norddocg;
      }
      this.sqlManager.update("update DOCUMGARA set DATARILASCIO = ?, STATODOC = ? where CODGAR = ? and NORDDOCG  in ( " + listaDoc + " )", new Object[]{datpub,new Long(5),codgar});
      this.sqlManager.update("update TORN set DULTAGG = ? where CODGAR=?",new Object[]{datpub,codgar});
      ngara = UtilityStringhe.convertiNullInStringaVuota(ngara);
      this.getRequest().setAttribute("pubblicazioneEseguita", "1");

      Long genere = (Long) sqlManager.getObject("select genere from V_GARE_GENERE where codgar = ? and genere <100 ", new Object[]{codgar});
      if(new Long(2).equals(genere)){
        logOggetto = ngara;
      }else{
        logOggetto = codgar;
      }

    } catch (SQLException e) {
        livEvento = 3;
        errMsgEvento = e.getMessage();
        this.getRequest().setAttribute("pubblicazioneEseguita", "error");
      throw new GestoreException("Errore nell'aggiornamento della gara", null);
    }finally{
        //Tracciatura eventi
        try {
          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(logOggetto);
          logEvento.setCodEvento("GA_PUBBLICA_DELIBERA");
          logEvento.setDescr("Pubblicazione delibera a contrarre o atto equivalente su portale Appalti (id.doc.pubblicati: " + listaDoc + ").");
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          messageKey = "errors.logEventi.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), le);
        }
      }
  }

  @Override
  public void preUpdate(TransactionStatus arg0, DataColumnContainer arg1) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

}
