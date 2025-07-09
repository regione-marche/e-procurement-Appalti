package it.eldasoft.sil.pg.tags.gestori.submit;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import it.eldasoft.gene.ws.objectstorage.ObjectStorageUtils;
import it.eldasoft.gene.ws.objectstorage.UploadInfo;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.TransactionStatus;

import com.lowagie.text.DocumentException;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.bl.utils.AllegatoSintesiUtils;
import it.eldasoft.sil.pg.bl.utils.MarcaturaTemporaleFileUtils;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloResType;
import it.maggioli.eldasoft.ws.dm.WSDMInviaMailType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAnagraficaType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoInType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;
import it.maggioli.eldasoft.ws.dm.WSDMTipoVoceRubricaType;
import it.eldasoft.utils.properties.ConfigManager;

public class GestorePubblicaAttiSuPortale extends AbstractGestoreEntita {

	static Logger logger = Logger.getLogger(GestorePubblicaAttiSuPortale.class);

	private GestioneWSDMManager gestioneWSDMManager;
	private PgManagerEst1 pgManagerEst1;
	private FileAllegatoManager fileAllegatoManager;

	public GestorePubblicaAttiSuPortale() {
		super(false);
	}

	@Override
	public void setRequest(HttpServletRequest request) {
		super.setRequest(request);

		gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager", this.getServletContext(), GestioneWSDMManager.class);
		pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1", this.getServletContext(), PgManagerEst1.class);
		fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager", this.getServletContext(), FileAllegatoManager.class);
	}

	@Override
	public String getEntita() {
		return "TORN";
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
	public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
	}

	@Override
	public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

	  	String messageKey = null;
	  	String errMsgEvento = "";
	  	Integer livEvento = 1;
	  	String descrLog = "Pubblicazione atti e offerte su portale Appalti";
	  	String ngara = datiForm.getString("NGARA");
	  	String oggEvento = ngara;
	  	String codEvento = "GA_PUBBLICA_ATTIOFFERTE";
	  	String idconfi = this.getRequest().getParameter("idconfi");
		String inviaComunicazione = this.getRequest().getParameter("inviaComunicazione");
		if ("1".equals(inviaComunicazione)) {
			descrLog += "\r\nPubblicazione con invio comunicazione";
		} else {
			descrLog += "\r\nPubblicazione senza invio comunicazione";
		}

		try {
			if ("1".equals(inviaComunicazione)) {
				Long genere = (Long) sqlManager.getObject("select genere from V_GARE_TORN where codgar = ?", new Object[]{datiForm.getString("CODGAR")});
				if (genere != null && (genere.equals(3L) || genere.equals(1L))) {
					oggEvento = datiForm.getString("CODGAR");
				}
				Date dpubatti = (Date) sqlManager.getObject("select dpubatti from gare1 where ngara=?", new Object[]{ngara});
				if (dpubatti != null) {
					descrLog = "Integrazione pubblicazione atti e offerte su portale Appalti";
					codEvento = "GA_INTEGRA_ATTIOFFERTE";
					String idDocumentiPubblicati = "";

					List<Vector<?>> documentiDaPubblicare = sqlManager.getListVector("select id from g1documatti where ngara=? and statodoc is null", new Object[]{ngara});
					if (documentiDaPubblicare == null || documentiDaPubblicare.isEmpty()) {
						throw new GestoreException("Nessun documento da pubblicare", null);
					}

					for (int i = 0; i < documentiDaPubblicare.size(); i++) {
						idDocumentiPubblicati += ((JdbcParametro) documentiDaPubblicare.get(i).get(0)).longValue() + ",";
					}
					idDocumentiPubblicati = idDocumentiPubblicati.substring(0, idDocumentiPubblicati.length() - 1);
					Timestamp dataAttuale = new Timestamp(Calendar.getInstance().getTimeInMillis());
					sqlManager.update("update g1documatti set statodoc=5, datpub=? where ngara=? and statodoc is null", new Object[]{dataAttuale, ngara});

					descrLog += " (id.doc. pubblicati: " + idDocumentiPubblicati + ")";
					this.getRequest().setAttribute("pubblicazioneEseguita", "1");
					this.getRequest().setAttribute("isIntegrazioneDocumenti", "1");
					return;
				}

				ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
				Object[] parametri = new Object[15];
				String idprg = "PG";
				String coment = null;
				String comkey1 = null;
				parametri[0] = idprg; // IDPRG
				Long newIdcom = (Long) sqlManager.getObject("SELECT max(idcom) FROM w_invcom WHERE idprg=?", new Object[]{idprg});
				if (newIdcom == null) {
					newIdcom = new Long(0);
				}
				newIdcom = newIdcom + 1;
				parametri[1] = newIdcom; // IDCOM
				if (ngara == null || "".equals(ngara) || (genere != null && genere.longValue() == 3)) {
					coment = "TORN";
					comkey1 = datiForm.getString("CODGAR");
				} else {
					coment = "GARE";
					comkey1 = datiForm.getString("NGARA");
				}
				parametri[2] = coment; // COMENT
				parametri[3] = comkey1; // COMKEY1
				parametri[4] = (long) profilo.getId(); // COMCODOPE
				Timestamp dataOdierna = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
				parametri[5] = dataOdierna; // COMDATINS
				parametri[6] = datiForm.getString("COMMITT"); // COMMITT
				parametri[7] = "2"; // COMSTATO
				parametri[8] = datiForm.getString("COMINTEST"); // COMINTEST
				parametri[9] = datiForm.getString("COMMSGOGG"); // COMMSGOGG
				parametri[10] = datiForm.getString("COMMSGTES"); // COMMSGTES
				parametri[11] = 2L; // COMPUB
				parametri[12] = datiForm.getString("COMMSGTIP"); // COMMSGTIP
				parametri[13] = "1"; // COMNORISPONDI
				String uffintAbilitato = (String) this.getRequest().getSession().getAttribute("uffint");
				uffintAbilitato = UtilityStringhe.convertiNullInStringaVuota(uffintAbilitato);
				if ("".equals(uffintAbilitato)) {
					uffintAbilitato = CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD;
				}
				String cenint = null;
				// Valorizzazione di IDCFG
				if (ngara == null || "".equals(ngara) || (genere != null && genere.longValue() == 3)) {
					cenint = (String) sqlManager.getObject("select t.cenint from torn t where t.codgar = ?", new Object[]{datiForm.getString("CODGAR")});
				} else {
					cenint = (String) sqlManager.getObject("select t.cenint from gare g,torn t where g.codgar1 = t.codgar and ngara = ?", new Object[]{ngara});
				}
				cenint = UtilityStringhe.convertiNullInStringaVuota(cenint);
				if (!"".equals(cenint)) {
					parametri[14] = cenint; // IDCFG
				} else {
					parametri[14] = uffintAbilitato; // IDCFG
				}
				sqlManager.update("INSERT INTO w_invcom(IDPRG,IDCOM,COMENT,COMKEY1,COMCODOPE,COMDATINS,"
						+ "COMMITT,COMSTATO,COMINTEST,COMMSGOGG,COMMSGTES,COMPUB,COMMSGTIP,COMNORISPONDI,IDCFG)"
						+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", parametri);

				String integrazioneWSDM = this.getRequest().getParameter("integrazioneWSDM");
				String classificadocumento = this.getRequest().getParameter("classificadocumento");
				String tipodocumento = this.getRequest().getParameter("tipodocumento");
				String oggettodocumento = this.getRequest().getParameter("oggettodocumento");
				String descrizionedocumento = this.getRequest().getParameter("descrizionedocumento");
				String mittenteinterno = this.getRequest().getParameter("mittenteinterno");
				String indirizzoMittente = this.getRequest().getParameter("indirizzomittente");
				String mezzoinvio = this.getRequest().getParameter("mezzoinvio");
				String mezzo = this.getRequest().getParameter("mezzo");
				String codiceregistrodocumento = this.getRequest().getParameter("codiceregistrodocumento");
				String inout = this.getRequest().getParameter("inout");
				String idindice = this.getRequest().getParameter("idindice");
				String idtitolazione = this.getRequest().getParameter("idtitolazione");
				String idunitaoperativamittente = this.getRequest().getParameter("idunitaoperativamittente");
				String inserimentoinfascicolo = this.getRequest().getParameter("inserimentoinfascicolo");
				String codicefascicolo = this.getRequest().getParameter("codicefascicolo");
				String oggettofascicolo = this.getRequest().getParameter("oggettofascicolonuovo");
				String classificafascicolo = this.getRequest().getParameter("classificafascicolonuovo");
				String descrizionefascicolo = this.getRequest().getParameter("descrizionefascicolonuovo");
				String annofascicolo = this.getRequest().getParameter("annofascicolo");
				String numerofascicolo = this.getRequest().getParameter("numerofascicolo");
				String username = this.getRequest().getParameter("username");
				String password = this.getRequest().getParameter("password");

				String wsdmLoginComune = ConfigManager.getValore(GestioneWSDMManager.PROP_WSDM_LOGIN_COMUNE + idconfi);
				if ("1".equals(wsdmLoginComune)) {
					String[] credenziali = this.gestioneWSDMManager.recuperaCredenzialiComuni(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO, idconfi);
					username = credenziali[0];
					password = credenziali[1];
				}

				String ruolo = this.getRequest().getParameter("ruolo");
				String nome = this.getRequest().getParameter("nome");
				String cognome = this.getRequest().getParameter("cognome");
				String codiceuo = this.getRequest().getParameter("codiceuo");
				String idutente = this.getRequest().getParameter("idutente");
				String idutenteunop = this.getRequest().getParameter("idutenteunop");
				String codiceaoo = this.getRequest().getParameter("codiceaoonuovo");
				String codiceufficio = this.getRequest().getParameter("codiceufficionuovo");
				String struttura = this.getRequest().getParameter("strutturaonuovo");
				String societa = this.getRequest().getParameter("societa");
				String codiceGaralotto = this.getRequest().getParameter("codicegaralotto");
				String cig = this.getRequest().getParameter("cig");
				String supporto = this.getRequest().getParameter("supporto");
				String tipofascicolo = this.getRequest().getParameter("tipofascicolonuovo");
				String classificadescrizione = this.getRequest().getParameter("classificadescrizione");
				String voce = this.getRequest().getParameter("voce");
				String codiceaoodes = this.getRequest().getParameter("codiceaoodes");
				String codiceufficiodes = this.getRequest().getParameter("codiceufficiodes");
				String RUP = this.getRequest().getParameter("RUP");
				String nomeRup = UtilityStruts.getParametroString(this.getRequest(), "nomeR");
				String acronimoRup = UtilityStruts.getParametroString(this.getRequest(), "acronimoR");
				String sottotipo = this.getRequest().getParameter("sottotipo");
				String tipofirma = this.getRequest().getParameter("tipofirma");
				String idunitaoperativamittenteDesc = this.getRequest().getParameter("idunitaoperativamittenteDesc");
				String uocompetenza = UtilityStruts.getParametroString(this.getRequest(), "uocompetenza");
				String uocompetenzadescrizione = UtilityStruts.getParametroString(this.getRequest(), "uocompetenzadescrizione");
				String livelloRiservatezza = this.getRequest().getParameter("livelloriservatezza");
				String isRiservatezzaAttiva = this.getRequest().getParameter("isRiservatezzaAttiva");

				String entita = null;
				if (ngara == null || "".equals(ngara) || (genere != null && genere.longValue() == 1)) {
					entita = "TORN";
				} else {
					entita = "GARE";
				}
				String key1 = comkey1;
				String key2 = UtilityStruts.getParametroString(this.getRequest(), "key2");
				String key3 = UtilityStruts.getParametroString(this.getRequest(), "key3");
				String key4 = UtilityStruts.getParametroString(this.getRequest(), "key4");

				if (this.gestioneWSDMManager.esisteWSFascicolo(entita, key1, key2, key3, key4) && "SI_FASCICOLO_NUOVO".equals(inserimentoinfascicolo)) {
					inserimentoinfascicolo = "SI_FASCICOLO_ESISTENTE";
					Long anno = null;

					if (codicefascicolo == null || "".equals(codicefascicolo)) {
						Vector<?> datiFascicolo = this.gestioneWSDMManager.getDatiWsfascicolo(entita, key1, key2, key3, key4);
						if (datiFascicolo != null && datiFascicolo.size() > 0) {
							codicefascicolo = SqlManager.getValueFromVectorParam(datiFascicolo, 0).stringValue();
							numerofascicolo = SqlManager.getValueFromVectorParam(datiFascicolo, 2).stringValue();
							anno = SqlManager.getValueFromVectorParam(datiFascicolo, 1).longValue();
							if (anno != null)
								annofascicolo = Long.toString(anno.longValue());

						}
					}

					if (codicefascicolo == null || "".equals(codicefascicolo)) {
						WSDMFascicoloResType wsdmFascicoloRes = this.gestioneWSDMManager.wsdmFascicoloMetadatiLeggi(username, password, ruolo, nome, cognome, codiceuo, idutente, idutenteunop, null, anno, numerofascicolo, "FASCICOLOPROTOCOLLO", classificafascicolo, idconfi);
						if (wsdmFascicoloRes.isEsito()) {
							if (wsdmFascicoloRes.getFascicolo() != null) {
								codicefascicolo = wsdmFascicoloRes.getFascicolo().getCodiceFascicolo();
							}
						}
					}

				}

				String tipoWSDM = null;
				if ("1".equals(integrazioneWSDM)) {
					WSDMConfigurazioneOutType configurazione = gestioneWSDMManager.wsdmConfigurazioneLeggi("FASCICOLOPROTOCOLLO", idconfi);
					if (configurazione.isEsito())
						tipoWSDM = configurazione.getRemotewsdm();
				}

				WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = null;
				boolean abilitatoInvioMailDocumentale = false;
				if ("1".equals(integrazioneWSDM))
					abilitatoInvioMailDocumentale = gestioneWSDMManager.abilitatoInvioMailDocumentale("FASCICOLOPROTOCOLLO",
							idconfi);

				if ("TITULUS".equals(tipoWSDM) && !abilitatoInvioMailDocumentale)
					tipodocumento = GestioneWSDMManager.TIPO_DOCUMENTO_GARA;
				else if ("TITULUS".equals(tipoWSDM) && abilitatoInvioMailDocumentale)
					tipodocumento = GestioneWSDMManager.TIPO_DOCUMENTO_GARA_PEC;

				// Per TITULUS si deve sbiancare il contenuto della classifica del fascicolo
				if ("TITULUS".equals(tipoWSDM))
					classificafascicolo = null;

				if ("1".equals(integrazioneWSDM)) {
					if (password == null)
						password = "";

					if ("".equals(codiceaoo))
						codiceaoo = null;

					if ("".equals(codiceufficio))
						codiceufficio = null;

					if ("ARCHIFLOWFA".equals(tipoWSDM) && StringUtils.isNotEmpty(oggettodocumento) && oggettodocumento.length() > 4000) {
						oggettodocumento = oggettodocumento.substring(0, 4000);
					}

					HashMap<String, Object> par = new HashMap<String, Object>();
					par.put("classificadocumento", classificadocumento);
					par.put("tipodocumento", tipodocumento);
					par.put("oggettodocumento", oggettodocumento);
					par.put("descrizionedocumento", descrizionedocumento);
					par.put("mittenteinterno", mittenteinterno);
					par.put("codiceregistrodocumento", codiceregistrodocumento);
					par.put("inout", inout);
					par.put("idindice", idindice);
					par.put("idtitolazione", idtitolazione);
					par.put("idunitaoperativamittente", idunitaoperativamittente);
					par.put("inserimentoinfascicolo", inserimentoinfascicolo);
					par.put("codicefascicolo", codicefascicolo);
					par.put("oggettofascicolo", oggettofascicolo);
					par.put("classificafascicolo", classificafascicolo);
					par.put("descrizionefascicolo", descrizionefascicolo);
					par.put("annofascicolo", annofascicolo);
					par.put("numerofascicolo", numerofascicolo);
					par.put("tipoWSDM", tipoWSDM);
					par.put("idprg", idprg);
					par.put("idcom", newIdcom);
					par.put("mezzo", mezzo);
					par.put("societa", societa);
					par.put("codiceGaralotto", codiceGaralotto);
					par.put("cig", cig);
					par.put("servizio", "FASCICOLOPROTOCOLLO");
					par.put("numeroallegati", new Long(0));
					par.put("struttura", struttura);
					par.put("supporto", supporto);
					par.put("tipofascicolo", tipofascicolo);
					par.put("classificadescrizione", classificadescrizione);
					par.put("voce", voce);
					par.put(GestioneWSDMManager.LABEL_RUP, RUP);
					par.put(GestioneWSDMManager.LABEL_NOME_RUP, nomeRup);
					par.put(GestioneWSDMManager.LABEL_ACRONIMO_RUP, acronimoRup);
					par.put(GestioneWSDMManager.LABEL_SOTTOTIPO, sottotipo);
					par.put(GestioneWSDMManager.LABEL_UOCOMPETENZA, uocompetenza);
					par.put("tipofirma", tipofirma);
					par.put("idunitaoperativamittenteDesc", idunitaoperativamittenteDesc);
					par.put("livelloriservatezza", livelloRiservatezza);

					wsdmProtocolloDocumentoIn = gestioneWSDMManager.wsdmProtocolloDocumentoPopola(par, idconfi);
				}

				String insertW_INVCOMDES = "insert into w_invcomdes(IDPRG,IDCOM,IDCOMDES,DESCODENT,DESCODSOG,DESMAIL,DESTESTO,DESIDDOCDIG,DESDATINV,DESDATINV_S,DESSTATO,DESERRORE,DESINTEST,COMTIPMA)"
						+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				//Si cancellano eventuali dati sporchi nel db
				this.sqlManager.update("delete from w_invcomdes where idprg=? and idcom=?", new Object[]{idprg, newIdcom});
				String codiceDitta_i = null;
				parametri = new Object[14];

				String ngaraInvio = ngara;
				String ngarapa = (String) sqlManager.getObject("select ngara from gare where ngarapa=?", new Object[] {ngara});
				if (StringUtils.isNotEmpty(ngarapa)) // se è presente secondo grado, mando la comunicazione alle ditte contenute nella lista del secondo grado
					ngaraInvio = ngarapa;
				List<Vector<?>> listaDitte = sqlManager.getListVector("select dittao from v_ditgpub where ngara=? and livpubatti in (1,2)", new Object[] {ngaraInvio});

				String descodent = "IMPR";
				Long comtipma = null;

				String desmail = null;
				String mailAggiuntiva = null;

				ArrayList<WSDMProtocolloAnagraficaType> listaDitteWSDM = new ArrayList<WSDMProtocolloAnagraficaType>();
				String destinatarioPrincipale = "";
				for (int i = 0; i < listaDitte.size(); i++) {

					codiceDitta_i = ((JdbcParametro) (listaDitte.get(i)).get(0)).stringValue();
					Boolean isRT = false;

					// verifico se si tratta di una RTI
					String codice = null;
					String denominazioneRT = null;
					Long tipimp = (Long) sqlManager.getObject("select tipimp from impr where codimp = ? ", new Object[]{codiceDitta_i});
					if (Long.valueOf(3).equals(tipimp) || Long.valueOf(10).equals(tipimp)) {
						denominazioneRT = (String) sqlManager.getObject("select nomimp from impr where codimp=?", new Object[]{codiceDitta_i});
						codice = (String) sqlManager.getObject(
								"select coddic from ragimp where codime9 = ? and impman = '1'", new Object[]{codiceDitta_i});
						if (StringUtils.isEmpty(codice)) {
							codice = "";
						}
						isRT = true;
					} else {
						codice = codiceDitta_i;
					}

					String selectImpr = "select emaiip,emai2ip,tipimp,nomest,nomimp from impr where codimp = ?";
					Vector<?> datiSoggettoDestinatario = this.sqlManager.getVector(selectImpr, new Object[]{codice});
					HashMap<String, String> datiImpr = gestioneWSDMManager.getDatiImpresa(codice);

					String email = null;
					String pec = null;
					String nomest = null;
					String nomimp = null;
					if (datiSoggettoDestinatario != null) {
						if (datiSoggettoDestinatario.get(0) != null) {
							email = ((JdbcParametro) datiSoggettoDestinatario.get(0)).getStringValue();
							email = UtilityStringhe.convertiNullInStringaVuota(email);
						}
						if (datiSoggettoDestinatario.get(1) != null) {
							pec = ((JdbcParametro) datiSoggettoDestinatario.get(1)).getStringValue();
							pec = UtilityStringhe.convertiNullInStringaVuota(pec);
						}
						if (datiSoggettoDestinatario.get(2) != null)
							tipimp = (Long) ((JdbcParametro) datiSoggettoDestinatario.get(2)).getValue();
						if (datiSoggettoDestinatario.get(3) != null)
							nomest = ((JdbcParametro) datiSoggettoDestinatario.get(3)).getStringValue();
						if (datiSoggettoDestinatario.get(4) != null)
							nomimp = ((JdbcParametro) datiSoggettoDestinatario.get(4)).getStringValue();
					}

					String desintest = (isRT ? denominazioneRT + " - " : "") + nomimp + (isRT ? " - Mandataria" : "");
					destinatarioPrincipale += desintest + ", ";
					if (StringUtils.isNotEmpty(pec)) {
						desmail = pec;
						comtipma = 1L;
						if (StringUtils.isNotEmpty(email)) {
							mailAggiuntiva = email;
						}
					} else {
						desmail = email;
						comtipma = 2L;
					}
					desmail = UtilityStringhe.convertiNullInStringaVuota(desmail);

					WSDMProtocolloAnagraficaType destinatario = new WSDMProtocolloAnagraficaType();
					if (datiImpr != null) {
						if (!"FOLIUM".equals(tipoWSDM)) {
							destinatario.setCodiceFiscale(datiImpr.get("codiceFiscale"));
							destinatario.setPartitaIVA(datiImpr.get("piva"));
						} else {
							destinatario.setCodiceFiscale("");
							destinatario.setPartitaIVA("");
						}
						destinatario.setIndirizzoResidenza(datiImpr.get("indirizzoResidenza"));
						destinatario.setComuneResidenza(datiImpr.get("comuneResidenza"));
						destinatario.setCodiceComuneResidenza(datiImpr.get("codiceComuneResidenza"));
						destinatario.setCognomeointestazione(nomest);
						if (mezzoinvio != null && !"".equals(mezzoinvio))
							destinatario.setMezzo(mezzoinvio);
						destinatario.setEmail(desmail);
						if (StringUtils.isNotEmpty(mailAggiuntiva)) {
							destinatario.setEmailAggiuntiva(mailAggiuntiva);
						}
						destinatario.setProvinciaResidenza(datiImpr.get("proimp"));
						destinatario.setCapResidenza(datiImpr.get("capimp"));
						destinatario.setTipoVoceRubrica(WSDMTipoVoceRubricaType.IMPRESA);
					}
					listaDitteWSDM.add(destinatario);

					parametri[0] = idprg;
					parametri[1] = newIdcom;
					parametri[2] = new Long(i + 1);
					parametri[3] = descodent;
					parametri[4] = codice;
					parametri[5] = desmail;
					parametri[6] = null;
					parametri[7] = null;
					parametri[8] = null;
					parametri[9] = null;
					parametri[10] = null;
					parametri[11] = null;
					parametri[12] = desintest;
					parametri[13] = comtipma;

					sqlManager.update(insertW_INVCOMDES, parametri);
				}

				if ("1".equals(integrazioneWSDM)) {
					WSDMProtocolloAnagraficaType[] destinatari = new WSDMProtocolloAnagraficaType[listaDitteWSDM.size()];
					for (int i = 0; i < listaDitteWSDM.size(); i++) {
						destinatari[i] = listaDitteWSDM.get(i);
					}

					if (StringUtils.isNotEmpty(destinatarioPrincipale) && destinatarioPrincipale.length() > 4000) {
						destinatarioPrincipale = destinatarioPrincipale.substring(0, 3990) + ", Altri…";
					} else if (StringUtils.isNotEmpty(destinatarioPrincipale))
						destinatarioPrincipale = destinatarioPrincipale.substring(0, destinatarioPrincipale.length() - 2);

					wsdmProtocolloDocumentoIn.setDestinatari(destinatari);
					destinatarioPrincipale = gestioneWSDMManager.formattazioneDestinatarioPrincipale(destinatarioPrincipale);
					wsdmProtocolloDocumentoIn.setDestinatarioPrincipale(destinatarioPrincipale);
				}
				if ("1".equals(integrazioneWSDM) && abilitatoInvioMailDocumentale
						&& ("ENGINEERING".equals(tipoWSDM) || "TITULUS".equals(tipoWSDM) || "SMAT".equals(tipoWSDM) || "URBI".equals(tipoWSDM) || "ENGINEERINGDOC".equals(tipoWSDM) || "LAPISOPERA".equals(tipoWSDM) || "HALLEY".equals(tipoWSDM))) {
					WSDMInviaMailType inviaMail = new WSDMInviaMailType();

					String commsgtes = datiForm.getString("COMMSGTES");
					if (commsgtes == null) {
						commsgtes = "[testo vuoto]";
					}
					inviaMail.setTestoMail(commsgtes);
					if ("ENGINEERING".equals(tipoWSDM)) {
						inviaMail.setOggettoMail(oggettodocumento);
					}
					inviaMail.setMittenteMail(indirizzoMittente);//APPALTI-2175
					this.gestioneWSDMManager.setDestinatariMail(idprg, newIdcom, inviaMail, wsdmProtocolloDocumentoIn);
				}

				boolean applicareMarcaTemp = false;
				boolean esisteMarcatura = false;
				boolean esitoControMarcatura[] = pgManagerEst1.applicareMarcaTemporalmente(idprg, newIdcom, true);
				esisteMarcatura = esitoControMarcatura[0];
				applicareMarcaTemp = esitoControMarcatura[1];
				if (!esisteMarcatura) {
					String where = " where DIGKEY1 = ? and DIGKEY2=? and DIGENT =? and DIGNOMDOC = " + AllegatoSintesiUtils.creazioneFiltroNomeFileSintesi(false, this.sqlManager);
					UploadInfo oldBucketInfo = ObjectStorageUtils.getUploadInfo("select bucket_ref, bucket_uuid from w_docdig where idprg=? and iddocdig=? and digent=?", new Object[]{idprg, newIdcom.toString(), "W_INVCOM"});
					ObjectStorageUtils.deleteFile(oldBucketInfo.bucket_ref(), oldBucketInfo.uuid());
					String delete = "delete from W_DOCDIG " + where;
					Object par[] = new Object[]{idprg, newIdcom.toString(), "W_INVCOM"};
					this.sqlManager.update(delete, par);

					String commsgogg = datiForm.getString("COMMSGOGG");
					String commsgtes = datiForm.getString("COMMSGTES");

					byte[] pdf = null;
					if ("G1STIPULA".equals(coment)) {
						pdf = gestioneWSDMManager.getTestoComunicazioneFormattatoStipula(comkey1, commsgogg, commsgtes, idprg, newIdcom);
					} else if ("HOMEPG".equals(coment)) {
						pdf = gestioneWSDMManager.getTestoComunicazioneFormattatoHomePG(cenint, commsgogg, commsgtes, idprg, newIdcom);
					} else {
						cig = (String) this.sqlManager.getObject("select codcig from v_gare_torn where codice = ?",
								new Object[]{comkey1});
						pdf = gestioneWSDMManager.getTestoComunicazioneFormattato(comkey1, cig, commsgogg, commsgtes, idprg, newIdcom);
					}
					Long maxContatore = (Long) this.sqlManager.getObject(
							"select coalesce(max(iddocdig),0) + 1 from W_DOCDIG where idprg = ?", new Object[]{idprg});
					String insert = "insert into w_docdig(idprg,iddocdig,digent,digkey1,digkey2,digtipdoc,dignomdoc,digdesdoc,digogg) values(?,?,?,?,?,?,?,?,?)";
					String nomeAllegato = idprg + maxContatore.toString() + "_comunicazione.pdf";

					String descrizioneAllegato = "Riepilogo comunicazione";
					String estensioneFile = "pdf";
					if (applicareMarcaTemp) {
						HashMap<String, Object> marcaTemporale = MarcaturaTemporaleFileUtils.creaMarcaTemporale(pdf, newIdcom, comkey1, coment, getRequest());
						String esito = (String) marcaTemporale.get("esito");
						if ("OK".equals(esito)) {
							pdf = (byte[]) marcaTemporale.get("file");
							nomeAllegato += ".tsd";
							estensioneFile = "tsd";
							descrizioneAllegato += " con marcatura temporale";
						} else {
							this.getRequest().setAttribute("RISULTATO", "ERRORE.MARCATEMP");
							return;
						}
					}
					LobHandler lobHandler = new DefaultLobHandler();
					this.sqlManager.update(insert, new Object[]{idprg, maxContatore, "W_INVCOM", idprg, newIdcom.toString(), "pdf/a", nomeAllegato, descrizioneAllegato, new SqlLobValue(pdf, lobHandler)});
					ObjectStorageUtils.addFile(idprg, maxContatore);

					if ("1".equals(integrazioneWSDM) && !("LAPISOPERA".equals(tipoWSDM))) {
						// Come unico allegato c'è il corpo della comunicazione
						WSDMProtocolloAllegatoType[] allegati = new WSDMProtocolloAllegatoType[1];
						// Aggiunta del testo della comunicazione

						allegati[0] = new WSDMProtocolloAllegatoType();
						allegati[0].setNome(nomeAllegato);
						allegati[0].setTipo(estensioneFile);
						allegati[0].setTitolo(descrizioneAllegato);
						allegati[0].setContenuto(pdf);
						if ("TITULUS".equals(tipoWSDM))
							allegati[0].setIdAllegato("W_INVCOM|" + idprg + "|" + newIdcom.toString());

						if ("NUMIX".equals(tipoWSDM)) {
							allegati[0] = GestioneWSDMManager.popolaAllegatoInfo(nomeAllegato, allegati[0]);
							allegati[0].setIsSealed(new Long(1));
						}

						wsdmProtocolloDocumentoIn.setAllegati(allegati);

						WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = gestioneWSDMManager.wsdmProtocolloInserisci(
								username, password, ruolo, nome, cognome, codiceuo, idutente, idutenteunop, codiceaoo,
								codiceufficio, wsdmProtocolloDocumentoIn, idconfi);

						if (wsdmProtocolloDocumentoRes.isEsito()) {
							String numeroDocumento = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroDocumento();
							Long annoProtocollo = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getAnnoProtocollo();
							String numeroProtocollo = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getNumeroProtocollo();

							Timestamp dataProtocollo = gestioneWSDMManager.getDataProtocollo(wsdmProtocolloDocumentoRes);
							if (annoProtocollo == null)
								annoProtocollo = gestioneWSDMManager.getAnnoFromDate(dataProtocollo);

							HashMap<String, String> datiWSDM = new HashMap<String, String>();
							datiWSDM.put("numeroDocumento", numeroDocumento);
							datiWSDM.put("annoProtocollo", annoProtocollo.toString());
							datiWSDM.put("numeroProtocollo", numeroProtocollo);
							datiWSDM.put("oggettodocumento", oggettodocumento);
							datiWSDM.put("testoMail", datiForm.getString("COMMSGTES"));
							datiWSDM.put("indirizzomittente", indirizzoMittente);
							datiWSDM.put("formatoMail", "2");
							datiWSDM.put("idconfi", idconfi);
							datiWSDM.put("username", username);
							datiWSDM.put("password", password);
							datiWSDM.put("ruolo", ruolo);
							datiWSDM.put("nome", nome);
							datiWSDM.put("cognome", cognome);
							datiWSDM.put("codiceuo", codiceuo);

							String statoComunicazione = gestioneWSDMManager.wsdmInvioMailEAggiornamentoDb(idprg,
									"FASCICOLOPROTOCOLLO", newIdcom.toString(), datiWSDM, allegati, 0);

							// Salvataggio del numero protocollo nella comunicazione ed impostazione
							// dello stato a "In uscita"
							// Il campo COMMITT va aggiornato solo se indirizzoMittente è valorizzato

							Object param[] = null;
							String updateW_INVCOM = null;

							if (abilitatoInvioMailDocumentale && ("JIRIDE".equals(tipoWSDM)) || "ARCHIFLOW".equals(tipoWSDM) || "ARCHIFLOWFA".equals(tipoWSDM) || "HALLEY".equals(tipoWSDM)) {
								updateW_INVCOM = "update w_invcom set comstato = ?, comdatprot = ?, comnumprot = ?, committ = ? where idprg = ? and idcom = ?";
								param = new Object[]{statoComunicazione, dataProtocollo, numeroProtocollo, indirizzoMittente,
										idprg, newIdcom};
							} else {
								updateW_INVCOM = "update w_invcom set comstato = ?, comdatprot = ?, comnumprot = ? where idprg = ? and idcom = ?";
								param = new Object[]{statoComunicazione, dataProtocollo, numeroProtocollo, idprg, newIdcom};
							}
							this.sqlManager.update(updateW_INVCOM, param);

							// Salvataggio del riferimento al fascicolo
							if ("SI_FASCICOLO_NUOVO".equals(inserimentoinfascicolo)) {
								String codiceFascicoloNUOVO = null;
								Long annoFascicoloNUOVO = null;
								String numeroFascicoloNUOVO = null;

								codiceFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo()
										.getCodiceFascicolo();
								if (wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo().getAnnoFascicolo() != null)
									annoFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo()
											.getAnnoFascicolo();
								else
									annoFascicoloNUOVO = gestioneWSDMManager.getAnnoFromDate(dataProtocollo);
								numeroFascicoloNUOVO = wsdmProtocolloDocumentoRes.getProtocolloDocumento().getFascicolo()
										.getNumeroFascicolo();

								Long riservatezza = null;
								if ("1".equals(isRiservatezzaAttiva)) {
									riservatezza = new Long(1);
								}
								gestioneWSDMManager.setWSFascicolo(entita, key1, null, null, null, codiceFascicoloNUOVO,
										annoFascicoloNUOVO, numeroFascicoloNUOVO, classificafascicolo, codiceaoo, codiceufficio,
										struttura, riservatezza, classificadescrizione, voce, codiceaoodes, codiceufficiodes);
							}

							sqlManager.update("update GARE1 set nproatti=? where ngara=?", new Object[]{numeroProtocollo, ngara});
							// Salvatagio in WSDOCUMENTO
							Long idWSDocumento = gestioneWSDMManager.setWSDocumento(entita, key1, null, null, null, numeroDocumento, annoProtocollo, numeroProtocollo, oggettodocumento, inout);

							// Salvataggio della mail in WSALLEGATI
							gestioneWSDMManager.setWSAllegati("W_INVCOM", idprg, newIdcom.toString(), null, null, idWSDocumento);

							// Salvataggio della mail in WSALLEGATI dell'allegato di sintesi
							gestioneWSDMManager.setWSAllegati("W_DOCDIG", idprg, maxContatore.toString(), null, null, idWSDocumento);
						} else {
							String messaggio = wsdmProtocolloDocumentoRes.getMessaggio();
							throw new GestoreException("Errore nella protocollazione del fascicolo", "wsdm.fascicoloprotocollo.protocollazione.error", new Object[]{messaggio}, new Exception());
						}
					}
				}
			}
			Timestamp dataAttuale = new Timestamp(Calendar.getInstance().getTimeInMillis());
			sqlManager.update("update g1documatti set statodoc=5, datpub=? where ngara=? and isarchi is null and statodoc is null", new Object[] {dataAttuale,ngara});
			sqlManager.update("update gare1 set dpubatti=? where ngara=?", new Object[] {dataAttuale, ngara});
		} catch (GestoreException e) {
			livEvento = 3;
			errMsgEvento = e.getMessage();
			throw e;
		} catch (IOException | DocumentException | SQLException | CriptazioneException e) {
			livEvento = 3;
			errMsgEvento = e.getMessage();
			throw new GestoreException("Errore nella pubblicazione atti e offerte su portale", null, e);
		} finally {
			try {
				LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
				logEvento.setLivEvento(livEvento);
				logEvento.setOggEvento(oggEvento);
				logEvento.setCodEvento(codEvento);
				logEvento.setDescr(descrLog);
				if (livEvento.equals(3)) {
					logEvento.setErrmsg(errMsgEvento);
				}
				LogEventiUtils.insertLogEventi(logEvento);
			} catch (Exception le) {
				messageKey = "errors.logEventi.inaspettataException";
				logger.error(this.resBundleGenerale.getString(messageKey), le);
			}
		}
		this.getRequest().setAttribute("pubblicazioneEseguita","1");
	}

	@Override
	public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

		String messageKey = null;
		String errMsgEvento = "";
		Integer livEvento = 1;
		String descrLog = "Annullamento pubblicazione atti e offerte su portale Appalti";
		String ngara = datiForm.getString("NGARA");
		String oggEvento = ngara;
		String codEvento = "GA_ANNULLA_ATTIOFFERTE";

		try {
			sqlManager.update("update g1documatti set statodoc=null,datpub=null where ngara=? and (isarchi is null or isarchi='2')", new Object[] {ngara});
			sqlManager.update("update gare1 set dpubatti=null,nproatti=null where ngara=?", new Object[] {ngara});
		} catch (SQLException e) {
			livEvento = 3;
			errMsgEvento = e.getMessage();
			throw new GestoreException("Errore nella annullamento pubblicazione atti e offerte", null, e);
		} finally {
			try {
				LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
				logEvento.setLivEvento(livEvento);
				logEvento.setOggEvento(oggEvento);
				logEvento.setCodEvento(codEvento);
				logEvento.setDescr(descrLog);
				if (livEvento.equals(3)) {
					logEvento.setErrmsg(errMsgEvento);
				}
				LogEventiUtils.insertLogEventi(logEvento);
			} catch (Exception le) {
				messageKey = "errors.logEventi.inaspettataException";
				logger.error(this.resBundleGenerale.getString(messageKey), le);
			}
		}
		this.getRequest().setAttribute("annullamentoPubblicazione","1");
	}

}
