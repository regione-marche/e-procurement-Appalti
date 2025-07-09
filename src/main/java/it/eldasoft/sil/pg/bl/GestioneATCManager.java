/*
 * Created on 08/11/2018
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;


import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.internetsoluzioni.www.WebService.Campo;
import it.internetsoluzioni.www.WebService.InternetSoluzioniPortType;
import it.internetsoluzioni.www.WebService.InternetSoluzioniServiceLocator;
import it.internetsoluzioni.www.WebService.RequestWS;
import it.internetsoluzioni.www.WebService.ResponseWS;
import it.internetsoluzioni.www.WebService.Valore;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Manager che si occupa di gestire tutte le operazioni di business logic sulla
 * parte relativa all'integrazione con ATC
 *
 * @author Marcello Caminiti
 */

public class GestioneATCManager {

  static Logger               logger                                          = Logger.getLogger(GestioneATCManager.class);

  private SqlManager            sqlManager;
  private PgManagerEst1         pgManagerEst1;
  private InviaVigilanzaManager  inviaVigilanzaManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
    this.pgManagerEst1 = pgManagerEst1;
  }

  public void setInviaVigilanzaManager(InviaVigilanzaManager inviaVigilanzaManager) {
	    this.inviaVigilanzaManager = inviaVigilanzaManager;
  }

  /**
   * Restituisce puntatore al servizio WS
   *
   * @return InternetSoluzioniPortType
   * @throws GestoreException
   */
  private InternetSoluzioniPortType getWSInternetSoluzioni() throws GestoreException {

    InternetSoluzioniPortType servizio = null;
    String url = ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_URL);

    InternetSoluzioniServiceLocator locator = new InternetSoluzioniServiceLocator();

    locator.setInternetSoluzioniPortEndpointAddress(url);
    try {
      Remote remote = locator.getPort(InternetSoluzioniPortType.class);
      Stub axisPort = (Stub) remote;
      servizio = (InternetSoluzioniPortType)axisPort;
    } catch (ServiceException e) {
      throw new GestoreException("Si e' verificato un errore durante la lettura del servio remoto di pubblicazione bandi ATC: " + e.getMessage(),
          "ws.ATC.InternetSoluzioni.servizio.error", e);
    }

    return servizio;

  }

  /**
   * Restituisce l'elenco delle strutture
   * @return List<Map<String, String>>
   * @throws GestoreException
   */
  public List<Map<String, String>> getStrutture() throws GestoreException{
    List<Map<String, String>> listaStrutture = new ArrayList<Map<String, String>>();
    RequestWS request = new RequestWS();
    Valore valori[] = this.setValori("strutture");
    request.setValori(valori);
    InternetSoluzioniPortType ws = this.getWSInternetSoluzioni();
    try {
      ResponseWS risposta=ws.getOggetti(request);
      if(risposta!=null && risposta.getValori()!=null) {
    	  if(risposta.getValori().length>0) {
	        for(int i=0;i<risposta.getValori().length;i++){
	            listaStrutture.add((HashMap<String,String>)risposta.getValori()[i]);
	          }
    	  }
      }
    } catch (Throwable e) {
      throw new GestoreException("Si e' verificato un errore durante la lettura della lista delle strutture del servizio remoto di pubblicazione bandi ATC: " + e.getMessage(),
          "ws.ATC.InternetSoluzioni.strutture", e);
    }
    return listaStrutture;
  }

  private Valore[] setValori(String tipo){
    Valore valori[] = new Valore[3];
    valori[0]= new Valore();
    valori[0].setChiave("user");
    valori[0].setValore(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_USER));
    valori[1]= new Valore();
    valori[1].setChiave("token");
    valori[1].setValore(ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_TOKEN));
    valori[2]= new Valore();
    valori[2].setChiave("tipo");
    valori[2].setValore(tipo);

    return valori;
  }

	private HashMap<String, String> setRisposta(HashMap<String,String> risposta, String idGara, String msg, int numLottiOk) {
		risposta.put("idGara", idGara);
	    risposta.put("msg", msg);
	    risposta.put("numLotti", String.valueOf(numLottiOk));
	    
	    return risposta;
	}

	/**
	 * Viene effettuato l'inserimento del bando/esito
	 * 
	 * @param ngara
	 * @param codiceGara
	 * @param genereGara
	 * @param dataPub
	 * @param struttura
	 * @param tipoBando
	 * @return
	 * @throws GestoreException
	 * @throws SQLException
	 */
	public HashMap<String, String> insertBando(String ngara, String codiceGara, Long genereGara, Timestamp dataPub,
			String struttura, String tipoBando) throws GestoreException {
		HashMap<String, String> risposta = new HashMap<String, String>();
		String idGara = null;
		String msg = null;
		String idLotto = null;
		Boolean isALotti = genereGara.longValue() == 1 || genereGara.longValue() == 3;
		int numLottiOk = 0;

		ResponseWS rispostaWs = this.insertBandoGara(ngara, codiceGara, genereGara, dataPub, struttura, tipoBando);
		if (rispostaWs == null) {
			return setRisposta(risposta, idGara, msg, numLottiOk);
		}
		@SuppressWarnings("unchecked")
		HashMap<String, String> vettoreGara = null;
		HashMap<String, String> vettoreLotto = null;

		if (StringUtils.isNotEmpty(rispostaWs.getMessage())) {
			msg = rispostaWs.getMessage();
			return setRisposta(risposta, idGara, msg, numLottiOk);
		}

		vettoreGara = (HashMap<String, String>) rispostaWs.getValori()[0];
		idGara = vettoreGara.get("id");

		if (!isALotti) {
			return setRisposta(risposta, idGara, msg, numLottiOk);
		}

		try {
			@SuppressWarnings("unchecked")
			List<String> lotti = this.sqlManager.getListVector(
					"select ngara from gare where codgar1=? and ngara!=codgar1 order by ngara",
					new Object[] { codiceGara });

			if (lotti == null || lotti.size() == 0)
				return setRisposta(risposta, idGara, msg, numLottiOk);

			String codiceLotto = null;
			String msgLotti = "";

			for (int i = 0; i < lotti.size(); i++) {
				codiceLotto = SqlManager.getValueFromVectorParam(lotti.get(i), 0).stringValue();
				rispostaWs = this.insertBandoLotto(codiceLotto, idGara, null, dataPub);

				if (rispostaWs == null)
					continue;
				if (StringUtils.isNotEmpty(rispostaWs.getMessage())) {
					msgLotti += rispostaWs.getMessage();
					continue;
				}
				numLottiOk++;
				vettoreLotto = (HashMap<String, String>) rispostaWs.getValori()[0];
				idLotto = vettoreLotto.get("id");
				risposta.put("idLotto" + numLottiOk, idLotto);
				risposta.put("codiceLotto" + numLottiOk, codiceLotto);
			}
			if (!"".equals(msgLotti))
				msg = msgLotti;

		} catch (SQLException e) {
			throw new GestoreException(
					"Si e' verificato un errore nella chiamata al servizio remoto di pubblicazione bandi PAT per l'inserimento del bando della gara: "
							+ codiceGara + " " + e.getMessage(),
					"ws.ATC.InternetSoluzioni.addOggetto.error", e);
		}
		return setRisposta(risposta, idGara, msg, numLottiOk);
	}

  /**
   * Metodo per l'aggiornamento dei termini del bando, ma solo se non è valorizzato uuid
   * @param codgar
   * @param tipoGara
   * @param data
   * @param dataOrig
   * @param ora
   * @param oraOrig
   * @return
   * @throws GestoreException
   */
  public ResponseWS updateTerminiBando(String codgar, String tipoGara, Timestamp data, Timestamp dataOrig, String ora, String oraOrig) throws GestoreException{
    RequestWS request = new RequestWS();
    ResponseWS risposta = null;
    try {
      String uuid=(String)this.sqlManager.getObject("select uuid from garuuid where codgar=? and tipric=?", new Object[]{codgar, "ATCPAT_bando"});
      if((!data.equals(dataOrig) || !ora.equals(oraOrig)) && uuid!=null && !"".equals(uuid)){
        String chiaveGara=codgar;
        if(!"1".equals(tipoGara) && !"3".equals(tipoGara)) //in questi casi la chiave ha una forma del tipo: $...
          chiaveGara=chiaveGara.substring(1);
        Calendar cal = Calendar.getInstance();
        Date dataScadenza = new Date(data.getTime());
        String oraScadenza = ora.substring(0, 2);
        String minutiScadenza = ora.substring(3);
        cal.setTime(dataScadenza);
        cal.add(Calendar.HOUR, new Integer(oraScadenza).intValue());
        cal.add(Calendar.MINUTE, new Integer(minutiScadenza).intValue());
        Valore valori[] = this.setValori("gare_bando");
        request.setValori(valori);
        Campo campi[] = new Campo[2];
        //id

        campi[0] = new Campo();
        campi[0].setNome("id");
        campi[0].setValore(uuid);
        //data_scadenza
        campi[1] = new Campo();
        campi[1].setNome("data_scadenza");
        campi[1].setValore(this.getTimestampUnix(cal.getTimeInMillis()));
        request.setValori(valori);
        request.setCampi(campi);
        InternetSoluzioniPortType ws = this.getWSInternetSoluzioni();
        risposta = ws.editOggetto(request);
      }
    } catch (RemoteException e) {
      throw new GestoreException("Si e' verificato un errore nella chiamata al servizio remoto di pubblicazione bandi PAT per la modifica del bando della gara: " + codgar + " " + e.getMessage(),
          "ws.ATC.InternetSoluzioni.updateBando.error", e);
    } catch (SQLException e) {
      throw new GestoreException("Si e' verificato un errore nella chiamata al servizio remoto di pubblicazione bandi PAT per la modifica del bando della gara: " + codgar + " " + e.getMessage(),
          "ws.ATC.InternetSoluzioni.updateBando.error", e);
    }

    return risposta;
  }

	private Campo[] creaArrayCampi(ArrayList<Campo> campi) {

		Campo[] arrayCampi = new Campo[campi.size()];
		for (int i = 0; i < campi.size(); i++) {
			arrayCampi[i] = campi.get(i);
		}
		return arrayCampi;
	}

	private void aggiungiCampo(ArrayList<Campo> campi, String nomeCampo, String valoreCampo) {
		if (StringUtils.isEmpty(valoreCampo) || StringUtils.isEmpty(nomeCampo))
			return;

		Campo campo = new Campo();
		campo.setNome(nomeCampo);
		campo.setValore(valoreCampo);
		campi.add(campo);
	}

  /**
   * Viene effettuato l'inserimento del bando/esito per la gara
   * @param ngara
   * @param codiceGara
   * @param genereGara
   * @param dataPub
   * @param struttura
   * @param tipoBando
   * @return
   * @throws GestoreException
   * @throws SQLException
   */
	private ResponseWS insertBandoGara(String ngara, String codiceGara, Long genereGara, Timestamp dataPub,
			String struttura, String tipoBando) throws GestoreException {

		RequestWS request = new RequestWS();
		ResponseWS risposta = null;

		String codGara = ngara;
		Boolean isALotti = genereGara.longValue() == 1 || genereGara.longValue() == 3;
		if (isALotti) {
			codGara = codiceGara;
		}

		try {
			request = setDatiGara(ngara, codiceGara, genereGara, dataPub, struttura, tipoBando, "");
			InternetSoluzioniPortType ws = this.getWSInternetSoluzioni();
			risposta = ws.addOggetto(request);

		} catch (RemoteException e) {
			throw new GestoreException(
					"Si e' verificato un errore nella chiamata al servizio remoto di pubblicazione bandi PAT per l'inserimento del bando della gara: "
							+ codGara + " " + e.getMessage(),
					"ws.ATC.InternetSoluzioni.addOggetto.error", e);
		} catch (SQLException e) {
			throw new GestoreException(
					"Si e' verificato un errore nella chiamata al servizio remoto di pubblicazione bandi PAT per l'inserimento del bando della gara: "
							+ codGara + " " + e.getMessage(),
					"ws.ATC.InternetSoluzioni.addOggetto.error", e);
		}
		return risposta;
	}

	private String getDescrizioneProcedura190(Long tipgar, String codcig) throws GestoreException, SQLException {

		Long tab1tip = tipgar;

		if (tipgar < 51 || tipgar > 89) {
			String proceduraScelta = this.inviaVigilanzaManager.getFromTab2("A1z05", tipgar, false);
			tab1tip = Long.parseLong(proceduraScelta);
		}

		String descrizioneProcedura = null;

		descrizioneProcedura = (String) this.sqlManager.getObject(
				"select tab1desc from tab1 where tab1cod = 'A2044' and tab1tip = ?", new Object[] { tab1tip });

		descrizioneProcedura = UtilityStringhe.convertiNullInStringaVuota(descrizioneProcedura);
		return descrizioneProcedura;
	}

  /**
   * Inserimento di un lotto
   * @param codGara
   * @param idGara
   * @param idLotto
   * @param dataPub
   * @return
   * @throws GestoreException
   */
  private ResponseWS insertBandoLotto(String codGara, String idGara, String idLotto, Timestamp dataPub) throws GestoreException{
    RequestWS request = null;
    ResponseWS risposta = null;

    try {
      request=this.setDatiLotto(codGara, idGara, idLotto, dataPub, "INS");
      InternetSoluzioniPortType ws = this.getWSInternetSoluzioni();

      risposta = ws.addOggetto(request);
    } catch (RemoteException e) {
      throw new GestoreException("Si e' verificato un errore nella chiamata al servizio remoto di pubblicazione bandi PAT per l'inserimento del bando della gara: " + codGara + " " + e.getMessage(),
          "ws.ATC.InternetSoluzioni.addOggetto.error", e);
    } catch (SQLException e) {
      throw new GestoreException("Si e' verificato un errore nella chiamata al servizio remoto di pubblicazione bandi PAT per l'inserimento del bando della gara: " + codGara + " " + e.getMessage(),
          "ws.ATC.InternetSoluzioni.addOggetto.error", e);
    }

    return risposta;
  }

  /**
   * Viene restituita una stringa che rappresenta la data in timestamp in formato UNIX
   * @param data
   * @return
   */
  private String getTimestampUnix(long data){
    return Long.toString(data/1000);
  }


  /**
   * Viene controllato se esiste la pubblicazione, andando a controllare se è presente la data di publicazione
   * @param codiceGara
   * @param tipoPub
   * @return
   * @throws SQLException
   */
  public boolean getPubblicazione(String codiceGara,String tipoPub) throws SQLException{
    boolean ret=true;
    Long conteggio  = null;
    String select="";
    if("BANDO".equals(tipoPub)){
      select="select count(codgar9) from pubbli where codgar9=? and tippub=11 and datpub is not null";
    }else if("ESITO".equals(tipoPub)){
      select  = "select count(p.ngara) from pubg p, gare g where g.codgar1=? and p.ngara=g.ngara and tippubg=12 and dinpubg is not null";
    }
    conteggio  = (Long)this.sqlManager.getObject(select, new Object[]{codiceGara});
    if(conteggio.longValue()==0)
      ret=false;
    return ret;
  }

  /**
   * Viene restituito il valore di uuid per il tipo di pubblicazione specificata
   * @param codiceGara
   * @param tipo
   * @return
   * @throws SQLException
   */
  public String getGaruuid(String codiceGara, String tipo) throws SQLException{
    Object par[] = new Object[2];
    par[0] = codiceGara;
    if("BANDO".equals(tipo))
      par[1]="ATCPAT_bando";
    else
      par[1]="ATCPAT_esito";
    String ret=(String)this.sqlManager.getObject("select uuid from garuuid where codgar=? and tipric=?", par);
    if(ret==null)
      ret="";
    return ret;
  }

/**
 * Viene effettuato l'aggiornamento dei dati della gara già pubblicata sul sito ATC
 * @param codiceGara
 * @param ngara
 * @param id
 * @param struttura
 * @param tipoBando
 * @param genereGara
 * @return
 * @throws GestoreException
 */
  public ResponseWS updateGara(String codiceGara, String ngara, String id, String struttura, String tipoBando, Long genereGara) throws GestoreException{
    RequestWS request = new RequestWS();
    ResponseWS risposta = null;

    try {
      request = setDatiGara(ngara, codiceGara, genereGara, null, struttura, tipoBando, id);
      InternetSoluzioniPortType ws = this.getWSInternetSoluzioni();
      risposta = ws.editOggetto(request);
    } catch (GestoreException e) {
      throw new GestoreException("Si è verificato un errore nell'allineamento dei dati presenti nel portale del sito istituzionale con quelli della gara: " + ngara + " " + e.getMessage(),
          "ATC.AllineamentoDati.error", e);
    } catch (RemoteException e) {
      throw new GestoreException("Si e' verificato un errore nella chiamata al servizio remoto di pubblicazione bandi PAT per la modifica del bando della gara: " + ngara + " " + e.getMessage(),
          "ws.ATC.InternetSoluzioni.updateBando.error", e);
    } catch (SQLException e) {
      throw new GestoreException("Si è verificato un errore nell'allineamento dei dati presenti nel portale del sito istituzionale con quelli della gara: " + ngara + " " + e.getMessage(),
          "ATC.AllineamentoDati.error", e);
    }
    return risposta;
  }

	private RequestWS setDatiGara(String ngara, String codiceGara, Long genereGara, Timestamp dataPub, String struttura,
			String tipoBando, String id) throws GestoreException, SQLException {
		RequestWS request = new RequestWS();

		Boolean isALotti = genereGara.longValue() == 1 || genereGara.longValue() == 3;
		String codGara = ngara;
		if (isALotti) {
			codGara = codiceGara;
		}

		Boolean isAvviso = genereGara.longValue() == 10 || genereGara.longValue() == 11 || genereGara.longValue() == 20;
		Boolean isBando = "BANDO".equals(tipoBando) && !isAvviso;

		Long numeroPubblicazioni = (Long) this.sqlManager.getObject(
				"select count(numpub) from pubbli where codgar9=? and tippub=?",
				new Object[] { codiceGara, new Long(11) });
		Boolean isEsito = numeroPubblicazioni != null && numeroPubblicazioni.longValue() > 0;

		Boolean isAffidamento = !isEsito && !"BANDO".equals(tipoBando);
		Boolean isGaraLottoUnico = genereGara.longValue() == 2;

		String tipo = null, tipologia = null, idBandoCollegato = null, url = null, sceltaContraente = null, cig = null,
				oggetto = null, annoAnac = null, denominazioneAmministrazione = null, dataAttivazione = null,
				codiceFiscaleAmministrazione = null, idRecordCigPrincipale = null, dataScadenza = null,
				valoreBaseAsta = null;
		Long tipoProcedura = null;

		ArrayList<Campo> campi = new ArrayList<Campo>();

		if (isGaraLottoUnico) {

			Vector<?> datiGara = this.sqlManager.getVector("select codcig, tipgarg, impapp from gare where ngara=?",
					new Object[] { ngara });
			cig = SqlManager.getValueFromVectorParam(datiGara, 0).getStringValue();
			tipoProcedura = (Long) SqlManager.getValueFromVectorParam(datiGara, 1).getValue();
			valoreBaseAsta = SqlManager.getValueFromVectorParam(datiGara, 2).getStringValue();
		} else {
			tipoProcedura = (Long) this.sqlManager.getObject("select tipgar from torn where codgar=?",
					new Object[] { codiceGara });
		}

		if (isBando) {
			tipo = "gare_bando";
			tipologia = "bandi ed inviti";
			url = ConfigManager.getValore(CostantiAppalti.PROP_WS_PORTALEAPPALTI_BANDI_URL);
		} else if (isAvviso) {
			tipo = "gare_avviso";
			tipologia = "avvisi pubblici";
			url = ConfigManager.getValore(CostantiAppalti.PROP_WS_PORTALEAPPALTI_AVVISI_URL);
		} else if (isEsito) {
			tipo = "gare_esito";
			tipologia = "esiti";
			url = ConfigManager.getValore(CostantiAppalti.PROP_WS_PORTALEAPPALTI_ESITI_URL);

			idBandoCollegato = (String) this.sqlManager.getObject(
					"select uuid from garuuid where codgar=? and tipric=?",
					new Object[] { codiceGara, "ATCPAT_bando" });
		} else if (isAffidamento) {
			tipo = "gare_affidamento";
			tipologia = "affidamenti";
			url = ConfigManager.getValore(CostantiAppalti.PROP_WS_PORTALEAPPALTI_ESITI_URL);
		}
		url += codGara;

		if (isBando || isAvviso) {
			dataScadenza = calcoloDataScadenza(ngara, codiceGara, genereGara);
		}

		if (isALotti) {
			if (StringUtils.isEmpty(id))
				idRecordCigPrincipale = "1";
			else
				idRecordCigPrincipale = id;
		}

		String dettagli = "<a href='" + url + "'>URL di pubblicazione su portale Appalti</a> ";

		if (!new Long(11).equals(genereGara)) // non un elenco
			oggetto = this.pgManagerEst1.getOggettoGara(codGara, codiceGara, genereGara);
		else
			oggetto = (String) this.sqlManager.getObject("select oggetto from gareavvisi where ngara=?",
					new Object[] { ngara });

		if (isBando || isAffidamento) {
			sceltaContraente = getDescrizioneProcedura190(tipoProcedura, cig);

			Vector<?> datiAmministrazione = this.sqlManager.getVector(
					"select nomein, cfein from uffint where codein in (select cenint from torn where codgar=?)",
					new Object[] { codiceGara });
			denominazioneAmministrazione = SqlManager.getValueFromVectorParam(datiAmministrazione, 0).getStringValue();
			codiceFiscaleAmministrazione = SqlManager.getValueFromVectorParam(datiAmministrazione, 1).getStringValue();

			if (dataPub != null) {
				annoAnac = dataPub.toString();
				annoAnac = annoAnac.substring(0, 4);
			}
		}

		if (dataPub != null)
			dataAttivazione = this.getTimestampUnix(dataPub.getTime());

		aggiungiCampo(campi, "id", id);
		aggiungiCampo(campi, "cig", cig);
		aggiungiCampo(campi, "dettagli", dettagli);
		aggiungiCampo(campi, "tipologia", tipologia);
		aggiungiCampo(campi, "data_attivazione", dataAttivazione);
		aggiungiCampo(campi, "struttura", struttura);
		aggiungiCampo(campi, "scelta_contraente", sceltaContraente);
		aggiungiCampo(campi, "oggetto", oggetto);
		aggiungiCampo(campi, "bando_collegato", idBandoCollegato);
		aggiungiCampo(campi, "anac_anno", annoAnac);
		aggiungiCampo(campi, "denominazione_aggiudicatrice", denominazioneAmministrazione);
		aggiungiCampo(campi, "dati_aggiudicatrice", codiceFiscaleAmministrazione);
		aggiungiCampo(campi, "id_record_cig_principale", idRecordCigPrincipale);
		aggiungiCampo(campi, "data_scadenza", dataScadenza);
		aggiungiCampo(campi, "valore_base_ast", valoreBaseAsta);

		Valore valori[] = this.setValori(tipo);
		request.setValori(valori);
		request.setCampi(creaArrayCampi(campi));

		return request;
	}

	private String calcoloDataScadenza(String ngara, String codiceGara, Long genereGara)
			throws GestoreException, SQLException {
		Calendar cal = Calendar.getInstance();
		boolean dataValorizzata = false;
		String oraScadenza = "";
		String minutiScadenza = "";
		if (new Long(11).equals(genereGara)) {
			// Per gli avvisi si deve considerare il campo GAREAVVISI.DATSCA se è
			// valorizzato
			Date datsca = (Date) this.sqlManager.getObject("select datsca from gareavvisi where ngara=?",
					new Object[] { ngara });
			if (datsca != null) {
				cal.setTime(datsca);
				cal.add(Calendar.HOUR, 23);
				cal.add(Calendar.MINUTE, 59);
				dataValorizzata = true;
			}
		} else if (new Long(10).equals(genereGara) || new Long(20).equals(genereGara)) {
			// Per gli elenchi e cataloghi si deve considerare il max del campo PUBBTERM
			// .DTERMPRES se valorizzato
			Vector datiPubbter = this.sqlManager.getVector(
					"select dtermpres, otermpres from pubbterm where ngara =? and dtermpres is not null order by dtermpres desc",
					new Object[] { ngara });
			if (datiPubbter != null && datiPubbter.size() > 0) {
				Date dtermpres = (Date) SqlManager.getValueFromVectorParam(datiPubbter, 0).getValue();
				String otermpres = SqlManager.getValueFromVectorParam(datiPubbter, 1).getStringValue();
				if (dtermpres != null) {
					cal.setTime(dtermpres);
					if (otermpres != null && !"".equals(otermpres)) {
						oraScadenza = otermpres.substring(0, 2);
						minutiScadenza = otermpres.substring(3);
						cal.add(Calendar.HOUR, new Integer(oraScadenza).intValue());
						cal.add(Calendar.MINUTE, new Integer(minutiScadenza).intValue());
					} else {
						cal.add(Calendar.HOUR, 23);
						cal.add(Calendar.MINUTE, 59);
					}
					dataValorizzata = true;
				}
			}
		}

		if (genereGara.longValue() != 10 && genereGara.longValue() != 11 && genereGara.longValue() != 20) {
			cal = this.getDataScadenzaGara(codiceGara);
			if (cal != null)
				dataValorizzata = true;
		}
		if (dataValorizzata)
			return this.getTimestampUnix(cal.getTimeInMillis());
		else
			return null;
	}

  /**
   * Viene calcolata la data di scadenza per la gara
   * @param codiceGara
   * @return
   * @throws GestoreException
   * @throws SQLException
   */
  private Calendar getDataScadenzaGara(String codiceGara) throws GestoreException, SQLException{
    String select="select iterga, dtepar, otepar, dteoff, oteoff from torn where codgar=?";
    Vector<?> datiTorn = this.sqlManager.getVector(select, new Object[]{codiceGara});
    Long iterga=null;
    Timestamp dtepar=null;
    Timestamp dteoff=null;
    String otepar=null;
    String oteoff=null;
    Date dataScadenza = null;
    String oraScadenza = "";
    String minutiScadenza = "";
    Calendar cal = Calendar.getInstance();
    if(datiTorn!=null && datiTorn.size()>0){
      iterga= SqlManager.getValueFromVectorParam(datiTorn, 0).longValue();
      dtepar=SqlManager.getValueFromVectorParam(datiTorn, 1).dataValue();
      otepar=SqlManager.getValueFromVectorParam(datiTorn, 2).getStringValue();
      dteoff=SqlManager.getValueFromVectorParam(datiTorn, 3).dataValue();
      oteoff=SqlManager.getValueFromVectorParam(datiTorn, 4).getStringValue();
    }

    if((new Long(2)).equals(iterga) || (new Long(4)).equals(iterga)){
	  if (dtepar == null)
	    return null;
      dataScadenza = new Date(dtepar.getTime());
      if (StringUtils.isNotEmpty(otepar)) {
    	  oraScadenza = otepar.substring(0, 2);
    	  minutiScadenza = otepar.substring(3);
      }
    }else{
      if (dteoff == null)
        return null;
      dataScadenza = new Date(dteoff.getTime());
      if (StringUtils.isNotEmpty(oteoff)) {
    	  oraScadenza = oteoff.substring(0, 2);
    	  minutiScadenza = oteoff.substring(3);
      }
    }
    cal.setTime(dataScadenza);

    if (StringUtils.isNotEmpty(oraScadenza))
    	cal.add(Calendar.HOUR, new Integer(oraScadenza).intValue());
    if (StringUtils.isNotEmpty(minutiScadenza))
    	cal.add(Calendar.MINUTE, new Integer(minutiScadenza).intValue());

    return cal;
  }

  /**
   * Popolamento del request di un lotto
   * @param codGara
   * @param idGara
   * @param idLotto
   * @param data
   * @param modo
   * @return
   * @throws SQLException
   */
	private RequestWS setDatiLotto(String codGara, String idGara, String idLotto, Timestamp data, String modo)
			throws SQLException {
		RequestWS request = new RequestWS();

		Valore valori[] = this.setValori("gare_lotto");
		request.setValori(valori);

		ArrayList<Campo> campi = new ArrayList<Campo>();

		// id del lotto da aggiornare
		if ("UPD".equals(modo))
			aggiungiCampo(campi, "id", idLotto);

		aggiungiCampo(campi, "tipologia", "lotto");
		aggiungiCampo(campi, "id_record_cig_principale", idGara);

		String cig = (String) this.sqlManager.getObject("select codcig from gare where ngara=?",
				new Object[] { codGara });

		aggiungiCampo(campi, "cig", cig);

		String oggetto = (String) this.sqlManager.getObject("select not_gar from gare where ngara=?",
				new Object[] { codGara });
		aggiungiCampo(campi, "oggetto", oggetto);

		String valoreBaseAsta = ((Double) this.sqlManager.getObject("select impapp from gare where ngara=?",
				new Object[] { codGara })).toString();
		aggiungiCampo(campi, "valore_base_asta", valoreBaseAsta);

		request.setValori(valori);
		request.setCampi(creaArrayCampi(campi));

		return request;
	}

  /**
   * Aggiornamento di un lotto
   * @param codGara
   * @param idGara
   * @param idLotto
   * @param dataPub
   * @return
   * @throws GestoreException
   */
  private ResponseWS updateLotto(String codGara, String idGara, String idLotto, Timestamp data) throws GestoreException{
    RequestWS request = null;
    ResponseWS risposta = null;

    try {
      request=this.setDatiLotto(codGara, idGara, idLotto, data,"UPD");
      InternetSoluzioniPortType ws = this.getWSInternetSoluzioni();

      risposta = ws.editOggetto(request);
    } catch (RemoteException e) {
      throw new GestoreException("Si e' verificato un errore nella chiamata al servizio remoto di pubblicazione bandi PAT per l'aggiornamento dei dati del lotto con id:" + idGara +" del bando della gara: " + codGara + " " + e.getMessage(),
          "ws.ATC.InternetSoluzioni.editOggetto.error", e);
    } catch (SQLException e) {
      throw new GestoreException("Si è verificato un errore nell'allineamento dei dati presenti nel portale del sito istituzionale con quelli del lotto con id:" + idGara +" del bando della gara: " + codGara + " " + e.getMessage(),
          "ATC.AllineamentoDati.error", e);
    }

    return risposta;
  }

	/**
	 * Viene effettuato l'allineamento dei dati della gara presente sul sito
	 * istituzionale con i valori del backoffice
	 * 
	 * @param ngara
	 * @param codiceGara
	 * @param genereGara
	 * @param struttura
	 * @param tipoBando
	 * @param idGara
	 * @param esistePubbli
	 * @return
	 * @throws GestoreException
	 */
	public HashMap<String, String> allineamentoDatiGara(String ngara, String codiceGara, Long genereGara,
			String struttura, String tipoBando, String idGara, boolean esistePubbli) throws GestoreException {
		HashMap<String, String> risposta = new HashMap<String, String>();
		String msg = null;
		String idLotto = null;
		Boolean isALotti = genereGara.longValue() == 1 || genereGara.longValue() == 3;
		int numLottiOk = 0;
		// Aggiornamento dati della gara
		ResponseWS rispostaWs = this.updateGara(codiceGara, ngara, idGara, struttura, tipoBando, genereGara);
		if (rispostaWs == null) {
			return setRisposta(risposta, idGara, msg, numLottiOk);
		}
		@SuppressWarnings("unchecked")
		HashMap<String, String> vettoreLotto = null;

		if (StringUtils.isNotEmpty(rispostaWs.getMessage())) {
			msg = rispostaWs.getMessage();
			return setRisposta(risposta, idGara, msg, numLottiOk);
		}

		if (!isALotti) {
			return setRisposta(risposta, idGara, msg, numLottiOk);
		}

		try {
			@SuppressWarnings("unchecked")
			String tipric = "ATCPAT_bando";
			if ("ESITO".endsWith(tipoBando))
				tipric = "ATCPAT_esito";
			Long conteggio = (Long) this.sqlManager.getObject(
					"select count(id) from garuuid where codgar=? and ngara is not null and tipric=?",
					new Object[] { codiceGara, tipric });

			if (conteggio == null || conteggio.longValue() == 0)
				return setRisposta(risposta, idGara, msg, numLottiOk);

			List<String> lotti = this.sqlManager.getListVector(
					"select g.ngara,g1.uuid from gare g left join garuuid g1 on g.ngara=g1.ngara and g1.tipric=? where g.codgar1=? and g.ngara!=g.codgar1 order by g.ngara",
					new Object[] { tipric, codiceGara });

			if (lotti == null || lotti.size() == 0)
				return setRisposta(risposta, idGara, msg, numLottiOk);

			String codiceLotto = null;
			String msgLotti = "";
			Timestamp oggi = null;
			for (int i = 0; i < lotti.size(); i++) {
				codiceLotto = SqlManager.getValueFromVectorParam(lotti.get(i), 0).stringValue();
				idLotto = SqlManager.getValueFromVectorParam(lotti.get(i), 1).stringValue();
				if (idLotto == null) {
					// Lotto da inserire
					oggi = new Timestamp(System.currentTimeMillis());
					rispostaWs = this.insertBandoLotto(codiceLotto, idGara, idLotto, oggi);

					if (rispostaWs == null)
						continue;
					if (StringUtils.isNotEmpty(rispostaWs.getMessage())) {
						msgLotti += "Lotto:" + codiceLotto + " " + rispostaWs.getMessage();
						continue;
					}
					numLottiOk++;
					vettoreLotto = (HashMap<String, String>) rispostaWs.getValori()[0];
					idLotto = vettoreLotto.get("id");
					risposta.put("idLotto" + numLottiOk, idLotto);
					risposta.put("codiceLotto" + numLottiOk, codiceLotto);

				} else {
					// Lotto da aggiornare
					rispostaWs = this.updateLotto(codiceLotto, idGara, idLotto, oggi);
					if (rispostaWs != null && rispostaWs.getMessage() != null && !"".equals(rispostaWs.getMessage())) {
						msgLotti += "Lotto:" + codiceLotto + " " + rispostaWs.getMessage();
					}
				}
			}
			if (!"".equals(msgLotti))
				msg = msgLotti;

		} catch (SQLException e) {
			throw new GestoreException(
					"Si e' verificato un errore nell'allineamento dei dati presenti nel portale del sito istituzionale con quelli della gara: "
							+ codiceGara + " " + e.getMessage(),
					"ATC.AllineamentoDati.error", e);
		}

		return setRisposta(risposta, idGara, msg, numLottiOk);
	}
}