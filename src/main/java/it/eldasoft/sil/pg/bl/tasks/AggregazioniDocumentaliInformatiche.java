package it.eldasoft.sil.pg.bl.tasks;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlbeans.XmlOptions;

import it.appaltiecontratti.agid.consnorma.datatypes.ASType;
import it.appaltiecontratti.agid.consnorma.datatypes.AggregazioneDocumentaliInformaticheDocument;
import it.appaltiecontratti.agid.consnorma.datatypes.AggregazioneDocumentaliInformaticheType;
import it.appaltiecontratti.agid.consnorma.datatypes.AssAggType;
import it.appaltiecontratti.agid.consnorma.datatypes.AssegnazioneAggType;
import it.appaltiecontratti.agid.consnorma.datatypes.ChiaveDescrittivaType;
import it.appaltiecontratti.agid.consnorma.datatypes.CodiceIPAType;
import it.appaltiecontratti.agid.consnorma.datatypes.FaseAggType;
import it.appaltiecontratti.agid.consnorma.datatypes.IdAggType2;
import it.appaltiecontratti.agid.consnorma.datatypes.IdDocDIAggType;
import it.appaltiecontratti.agid.consnorma.datatypes.ImprontaCrittograficaDelDocumentoType;
import it.appaltiecontratti.agid.consnorma.datatypes.IndiceDocumentiAggType;
import it.appaltiecontratti.agid.consnorma.datatypes.PAIAggType;
import it.appaltiecontratti.agid.consnorma.datatypes.ProcedimentoAmministrativoAggType;
import it.appaltiecontratti.agid.consnorma.datatypes.RUPType;
import it.appaltiecontratti.agid.consnorma.datatypes.RuoloAggType;
import it.appaltiecontratti.agid.consnorma.datatypes.RuoloType;
import it.appaltiecontratti.agid.consnorma.datatypes.SoggettiAggType;
import it.appaltiecontratti.agid.consnorma.datatypes.TipoAggregazioneType2;
import it.appaltiecontratti.agid.consnorma.datatypes.TipoAssegnazioneAggType;
import it.appaltiecontratti.agid.consnorma.datatypes.TipoDocumentoAggType;
import it.appaltiecontratti.agid.consnorma.datatypes.TipoFase3AggType;
import it.appaltiecontratti.agid.consnorma.datatypes.TipoFaseAggType;
import it.appaltiecontratti.agid.consnorma.datatypes.TipoSoggetto1AggType;
import it.appaltiecontratti.agid.consnorma.datatypes.TipoSoggetto4AggType;
import it.appaltiecontratti.agid.consnorma.datatypes.TipologiaFascicoloAggType;

public class AggregazioniDocumentaliInformatiche {
	
	  private AggregazioneDocumentaliInformaticheDocument document;
	  private AggregazioneDocumentaliInformaticheType doc;
	  private XmlOptions options;
	  private SoggettiAggType soggettiType;
	  private AssegnazioneAggType assegnazione;
	  private IndiceDocumentiAggType indiceDocumenti;
	  
	  public AggregazioniDocumentaliInformatiche() {
		    options = new XmlOptions();
		    options.setCharacterEncoding("UTF-8");
		    options.setUseDefaultNamespace();
		    options.setSavePrettyPrint();
		   
		    document = AggregazioneDocumentaliInformaticheDocument.Factory.newInstance(options);
		    doc = document.addNewAggregazioneDocumentaliInformatiche();
		    soggettiType = null;
		    assegnazione = null;
		    indiceDocumenti = null;
	  }//AggregazioniDocumentaliInformatiche	
	  
	  
	 public void setIdAgg(String id) {
		 IdAggType2 idAgg = doc.addNewIdAgg();
		 idAgg.setTipoAggregazione(TipoAggregazioneType2.FASCICOLO);
		 idAgg.setIdAggregazione(id);
	  }
	  
	  // TODO: da vedere se lasciare o eliminare
	  public void setTipologiaFascicolo(TipologiaFascicoloAggType.Enum tipologiaFascicolo) {
		  doc.setTipologiaFascicolo(tipologiaFascicolo);
	  }
	  
	  public void setTipologiaFascicoloProcedimentoAmministrativo() {
		  doc.setTipologiaFascicolo(TipologiaFascicoloAggType.PROCEDIMENTO_AMMINISTRATIVO);
	  }	  
	  
	  public void addSoggetto() {
		  soggettiType = doc.addNewSoggetti();
	  }
	  
	  // questo e' un metodo di utilita' per passare le info nei parametri
	  public static Map<String,String> makeInfoCodiceIPAType(String denominazione, String codiceIpa) {
		  
		 HashMap<String,String> infoCodiceIPAType = new HashMap<String,String>();
		 infoCodiceIPAType.put("denominazione",denominazione);
		 infoCodiceIPAType.put("codiceIpa",codiceIpa);
		 
		 return infoCodiceIPAType;		  
	  }
	  
	  public void addRuoloAmministrazioneTitolare( Map<String,String> iPAAmmInfo, 
			  									   Map<String,String> iPAAOOInfo, 
			  									   String[] indirizzi) {
		  
		  RuoloAggType ruolo = soggettiType.addNewRuolo();
		  TipoSoggetto1AggType amministatoreTitolare = ruolo.addNewAmministrazioneTitolare();
		  amministatoreTitolare.setTipoRuolo("Amministrazione Titolare"); // fixed 
		  
		  PAIAggType pai = amministatoreTitolare.addNewPAI();
		  
		  // inserisco le, eventuali, info relative a IPAAmm
		  if (iPAAmmInfo!=null && !iPAAmmInfo.isEmpty() ) {
			  CodiceIPAType IPAAmm = pai.addNewIPAAmm();
			  String denominazione = (String) iPAAmmInfo.get("denominazione");
			  String codiceIpa     = (String) iPAAmmInfo.get("codiceIPA");
			  IPAAmm.setDenominazione(denominazione);
			  IPAAmm.setCodiceIPA(codiceIpa);			  
		  }
		  
		  // inserisco le, eventuali, info relative a IPAAOO
		  if (iPAAOOInfo!=null && !iPAAOOInfo.isEmpty() ) {
			  CodiceIPAType IPAAOO = pai.addNewIPAAOO();
			  String denominazione = (String) iPAAOOInfo.get("denominazione");
			  String codiceIpa     = (String) iPAAOOInfo.get("codiceIPA");
			  IPAAOO.setDenominazione(denominazione);
			  IPAAOO.setCodiceIPA(codiceIpa);			  
		  }
		  
		  pai.setIndirizziDigitaliDiRiferimentoArray(indirizzi);
	  }
	  
	  private RUPType addRuoloRUPAux( String cognome, 
				      			   	  String nome, 
				      			   	  String[] IndirizziDigitaliDiRiferimento ){
		  
		  RuoloAggType ruolo = soggettiType.addNewRuolo();
		  TipoSoggetto4AggType rup = ruolo.addNewRUP();
		  
		  rup.setTipoRuolo("RUP"); // fixed
		 		  
		  RUPType rupType = rup.addNewRUP();
		  rupType.setCognome(cognome);
		  rupType.setNome(nome);		 
		  rupType.setIndirizziDigitaliDiRiferimentoArray(IndirizziDigitaliDiRiferimento);
		  
		  return rupType;
				
	  }//addRuoloRUPAux
	  
		public void addRuoloRUP(String cognome, 
								String nome, 
								Map iPAAmmInfo, 
								Map iPAAOOInfo, 
								Map iPAUORInfo,
								String[] IndirizziDigitaliDiRiferimento) {

			RUPType rupType = addRuoloRUPAux(cognome, nome, IndirizziDigitaliDiRiferimento);

			// aggiungo l'eventuale IPAAmm
			if (iPAAmmInfo != null && !iPAAmmInfo.isEmpty()) {
				String denominazione = (String) iPAAmmInfo.get("denominazione");
				String codiceIpa = (String) iPAAmmInfo.get("codiceIPA");
				CodiceIPAType IPAAmm = rupType.addNewIPAAmm();
				IPAAmm.setDenominazione(denominazione);
				IPAAmm.setCodiceIPA(codiceIpa);
			}

			// aggiungo l'eventuale IPAAOO
			if (iPAAOOInfo != null && !iPAAOOInfo.isEmpty()) {
				String denominazione = (String) iPAAOOInfo.get("denominazione");
				String codiceIpa = (String) iPAAOOInfo.get("codiceIPA");
				CodiceIPAType IPAAOO = rupType.addNewIPAAOO();
				IPAAOO.setDenominazione(denominazione);
				IPAAOO.setCodiceIPA(codiceIpa);
			}

			// aggiungo l'eventuale IPAUOR
			if (iPAUORInfo != null && !iPAUORInfo.isEmpty()) {
				String denominazione = (String) iPAUORInfo.get("denominazione");
				String codiceIpa = (String) iPAUORInfo.get("codiceIPA");
				CodiceIPAType IPAUOR = rupType.addNewIPAUOR();
				IPAUOR.setDenominazione(denominazione);
				IPAUOR.setCodiceIPA(codiceIpa);
			}

		}// addRuoloRUP
		
	  public void addAssegnazione() {
		  assegnazione = doc.addNewAssegnazione(); 
	  }
	  	
	  public void assegnazione( Map<String,String> iPAAmmInfo, 
			  					Map<String,String> iPAAOOInfo, 
			  					String[] indirizzi, 
			  					Date dataInizioAssengazione ) {
		  		  
		  TipoAssegnazioneAggType tipoAssegnazione = assegnazione.addNewTipoAssegnazione();
		  AssAggType assegnazionePerCompetenza = tipoAssegnazione.addNewPerCompetenza();
		  assegnazionePerCompetenza.setTipoAssegnazioneRuolo("Per Competenza"); // fixed in xml
		  
		  ASType soggettoAssegnatario = assegnazionePerCompetenza.addNewSoggettoAssegnatario();
		  
		  if ( iPAAmmInfo!= null && !iPAAmmInfo.isEmpty() ) {
			  String denominazione = (String) iPAAmmInfo.get("denominazione");
			  String codiceIpa = (String) iPAAmmInfo.get("codiceIPA");
			  
			  CodiceIPAType IPAAmm = soggettoAssegnatario.addNewIPAAmm();
			  IPAAmm.setDenominazione(denominazione);
			  IPAAmm.setCodiceIPA(codiceIpa);
		  }
		  
		  if ( iPAAOOInfo!= null && !iPAAOOInfo.isEmpty() ) {
			  String denominazione = (String) iPAAOOInfo.get("denominazione");
			  String codiceIpa = (String) iPAAOOInfo.get("codiceIPA");
			  
			  CodiceIPAType IPAAOO = soggettoAssegnatario.addNewIPAAOO();
			  IPAAOO.setDenominazione(denominazione);
			  IPAAOO.setCodiceIPA(codiceIpa);
		  }		  
		  
		  soggettoAssegnatario.setIndirizziDigitaliDiRiferimentoArray(indirizzi);
		  
		  Calendar data = Calendar.getInstance();
		  data.setTime(dataInizioAssengazione);
		  assegnazionePerCompetenza.setDataInizioAssegnazione(data);
		  assegnazionePerCompetenza.setOraInizioAssegnazione(data); //TODO: da capire come inserire l'orario e in che fomrato
	  }
	  	  	 
	  public void setDataApertura(Date dataApertura) {
		  Calendar data = Calendar.getInstance();
		  data.setTime(dataApertura);
		  doc.setDataApertura(data);
	  }	  
	  
	  public void setChiaveDescrittiva(Map<String,Object> oggettoObj, String[] elencoParolaChiave) {
		  ChiaveDescrittivaType chiaveDescrittivaType = doc.addNewChiaveDescrittiva();
		  String oggetto = (String) oggettoObj.get("oggetto");
		  chiaveDescrittivaType.setOggetto(oggetto);
		  chiaveDescrittivaType.setParoleChiaveArray(elencoParolaChiave);	  
	  }
	  
	  public void setProcedimentoAmministrativo( String materiaArgomentoStruttura,
			  									 Map<String,Object> procedimentoObj,
			  									 String catalogoProcedimenti,
			  									 Date   dataInizioFase) {
		  
		  ProcedimentoAmministrativoAggType procedimentoAmministrativo = doc.addNewProcedimentoAmministrativo();
		  procedimentoAmministrativo.setMateriaArgomentoStruttura(materiaArgomentoStruttura);
		  String procedimento = (String) procedimentoObj.get("oggetto");
		  procedimentoAmministrativo.setProcedimento(procedimento);
		  procedimentoAmministrativo.setCatalogoProcedimenti(catalogoProcedimenti);
		  FaseAggType fasi = procedimentoAmministrativo.addNewFasi();
		 
		  TipoFaseAggType tipoFaseAggType = fasi.addNewTipoFase(); // e' il nodo che contiene l'array
		  
		  TipoFase3AggType consuntiva = tipoFaseAggType.addNewConsultiva();
	      consuntiva.setFase("Consultiva");  // fixed
	      Calendar data = Calendar.getInstance();
		  data.setTime(dataInizioFase);
	      consuntiva.setDataInizioFase(data);
		  
	  }
	  
	  public void setIndiceDocumenti(){
		  indiceDocumenti = doc.addNewIndiceDocumenti();  
	  }
	  
	  public void setInfoDocumentoInformatico(String identificativo, byte[] impronta, String algoritmo) {		 
		  TipoDocumentoAggType tipoDocumento = indiceDocumenti.addNewTipoDocumento();
		  
		  IdDocDIAggType documentoInformatico = tipoDocumento.addNewDocumentoinformatico();
		  ImprontaCrittograficaDelDocumentoType improntaCrittograficaDelDocumento = documentoInformatico.addNewImprontaCrittograficaDelDocumento();		  
		  improntaCrittograficaDelDocumento.setImpronta(impronta);
		  improntaCrittograficaDelDocumento.setAlgoritmo(algoritmo);
		  
		  documentoInformatico.setIdentificativo(identificativo);
	  }
	  
	  // questo metodo e' stato inserito per "togliere complessita'" al chiamante
	  public void setInfoDocumentoInformatico(Map<String, Object> infoNodoIdDoc) {
			String id = (String) infoNodoIdDoc.get("id");
			byte[] impronta = (byte[]) infoNodoIdDoc.get("impronta");
			String algoritmo = (String) infoNodoIdDoc.get("algoritmo");
			setInfoDocumentoInformatico(id, impronta, algoritmo);
	  }
	  
	  public void save(String fileNameWithPath) throws IOException  {
			File fileDocumentoXml = new File(fileNameWithPath);
			document.save(fileDocumentoXml, options);
	  }	  
	  
}//AggregazioniDocumentaliInformatiche
