package it.eldasoft.sil.pg.bl.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.encoding.Base64;
import org.apache.xmlbeans.XmlOptions;

import it.appaltiecontratti.agid.consnorma.datatypes.AllegatiType;
import it.appaltiecontratti.agid.consnorma.datatypes.ChiaveDescrittivaType;
import it.appaltiecontratti.agid.consnorma.datatypes.DatiDiRegistrazioneType;
import it.appaltiecontratti.agid.consnorma.datatypes.DocumentoInformaticoDocument;
import it.appaltiecontratti.agid.consnorma.datatypes.DocumentoInformaticoType;
import it.appaltiecontratti.agid.consnorma.datatypes.IdDocType;
import it.appaltiecontratti.agid.consnorma.datatypes.IdentificativoDelFormatoType;
import it.appaltiecontratti.agid.consnorma.datatypes.ImprontaCrittograficaDelDocumentoType;
import it.appaltiecontratti.agid.consnorma.datatypes.ModalitaDiFormazioneType;
import it.appaltiecontratti.agid.consnorma.datatypes.NoRegistroType;
import it.appaltiecontratti.agid.consnorma.datatypes.PFType;
import it.appaltiecontratti.agid.consnorma.datatypes.PGType;
import it.appaltiecontratti.agid.consnorma.datatypes.ProtocolloType;
import it.appaltiecontratti.agid.consnorma.datatypes.RuoloType;
import it.appaltiecontratti.agid.consnorma.datatypes.SoggettiType;
import it.appaltiecontratti.agid.consnorma.datatypes.TipoRegistroType;
import it.appaltiecontratti.agid.consnorma.datatypes.TipoSoggetto1Type;
import it.appaltiecontratti.agid.consnorma.datatypes.TipoSoggetto31Type;
import it.appaltiecontratti.agid.consnorma.datatypes.TipoSoggetto32Type;
import it.appaltiecontratti.agid.consnorma.datatypes.TipologiaDiFlussoType;
import it.appaltiecontratti.agid.consnorma.datatypes.VerificaType;

public class DocumentoInformatico {

  private DocumentoInformaticoDocument document;
  private DocumentoInformaticoType doc;
  private XmlOptions options;
  private SoggettiType soggettiType;
  private Map<String,Object> infoNodoIdDoc; // queste info sono utilizzate per la generazione di AggregazioneDocumentoInformatico
  
  public DocumentoInformatico() {
    options = new XmlOptions();
    options.setCharacterEncoding("UTF-8");
    options.setUseDefaultNamespace();
    options.setSavePrettyPrint();
   
    document = DocumentoInformaticoDocument.Factory.newInstance(options);
    doc = document.addNewDocumentoInformatico();
    soggettiType = null;
    infoNodoIdDoc = new HashMap<String,Object>();
  }
  
  public void setIdDoc(String id, byte[] impronta, String algoritmo) {
	IdDocType idDoc = doc.addNewIdDoc();
	
    ImprontaCrittograficaDelDocumentoType improntaCrittograficaDelDocumentoType = idDoc.addNewImprontaCrittograficaDelDocumento();
    
    improntaCrittograficaDelDocumentoType.setImpronta(impronta);
    improntaCrittograficaDelDocumentoType.setAlgoritmo(algoritmo);
    idDoc.setIdentificativo(id);
    
    infoNodoIdDoc.put("id", id);
    infoNodoIdDoc.put("algoritmo", algoritmo);
    infoNodoIdDoc.put("impronta",impronta);
  }
  
  public Map<String, Object> getInfoIdDocPerAggregazioneDocumentoInformatico(){
	  // questo metodo e' utilizzato per la generazione di AggregazioneDocumentoInformatico, riporta in una struttura dati
	  // le informazioni del nodo IdDoc ( Impronta, Algoritmo, Identificativo )
	  return infoNodoIdDoc;
  }

  public void setModalitaDiFormazione(ModalitaDiFormazioneType.Enum modalita) {
    doc.setModalitaDiFormazione(modalita);
  }
  
  public void setTipologiaDocumentale(String tipologiaDocumentale) {
	  doc.setTipologiaDocumentale(tipologiaDocumentale);	  
  }
  
  public void setDatiDiRegistrazione(String tipologiaDelFlusso, String tipoRegistro, Calendar data, String protocollo) {
	  DatiDiRegistrazioneType datiDiRegistrazione = doc.addNewDatiDiRegistrazione();
	  
	  switch( tipologiaDelFlusso ) {
	  	case "E": datiDiRegistrazione.setTipologiaDiFlusso(TipologiaDiFlussoType.E);
	  	          break;
	  	case "I": datiDiRegistrazione.setTipologiaDiFlusso(TipologiaDiFlussoType.I);
	  			  break;
	  	case "U": datiDiRegistrazione.setTipologiaDiFlusso(TipologiaDiFlussoType.U);
		  		  break;
	  }//switch
	  
	  
	  TipoRegistroType tipoRegistroType =  datiDiRegistrazione.addNewTipoRegistro();
	  
	  NoRegistroType noRegistroType = tipoRegistroType.addNewNessuno();
	  //ProtocolloType protocolloType = tipoRegistroType.addNewProtocolloOrdinarioProtocolloEmergenza();
	  noRegistroType.setTipoRegistro(tipoRegistro); 	
	  noRegistroType.setDataDocumento(data); 			// TODO:ROS: e' la corretta rappresentazione della data?
	  noRegistroType.setOraDocumento(data);  			// TODO:ROS: e' la corretta rappresentazione dell'ora?
	  noRegistroType.setNumeroDocumento(protocollo);   
	  
  }

  public void addSoggetto() {
	  soggettiType = doc.addNewSoggetti();
  }
  
  public void addRuoloMittentePF(String cognome, String nome, String cf) {
	 RuoloType ruoloType = soggettiType.addNewRuolo();
	 TipoSoggetto32Type mittente = ruoloType.addNewMittente();
	 mittente.setTipoRuolo("Mittente");
	 PFType pfType = mittente.addNewPF();
	 pfType.setCognome(cognome);
	 pfType.setNome(nome);
	 pfType.setCodiceFiscale(cf);
  }
  
  public void addRuoloMittentePG(String nomeImpresa, String cfImpresa) {
	 RuoloType ruoloType = soggettiType.addNewRuolo();
	 TipoSoggetto32Type mittente = ruoloType.addNewMittente();
	 mittente.setTipoRuolo("Mittente");
	 PGType pgType = mittente.addNewPG();
	 pgType.setDenominazioneOrganizzazione(nomeImpresa);
	 pgType.setCodiceFiscalePartitaIva(cfImpresa);
  } 
  
  public void addSoggettoCheEffettuaLaRegistrazione(String nomeImpresa, String cfImpresa) {
	 RuoloType ruoloType = soggettiType.addNewRuolo();
	 TipoSoggetto1Type soggettoCheEffettuaLaRegistrazione = ruoloType.addNewSoggettoCheEffettuaLaRegistrazione();	
	 soggettoCheEffettuaLaRegistrazione.setTipoRuolo("Soggetto Che Effettua La Registrazione");
	 PGType pg = soggettoCheEffettuaLaRegistrazione.addNewPG();
	 pg.setDenominazioneOrganizzazione(nomeImpresa);
	 pg.setCodiceFiscalePartitaIva(cfImpresa);
 }
  
  public void addRuoloDestinatarioPF(String cognome, String nome, String cf){
	 RuoloType ruoloType = soggettiType.addNewRuolo();
	 TipoSoggetto31Type destinatario = ruoloType.addNewDestinatario();
	 destinatario.setTipoRuolo("Destinatario");
	 PFType pfType = destinatario.addNewPF();
	 pfType.setCognome(cognome);
	 pfType.setNome(nome);
	 pfType.setCodiceFiscale(cf);	  
  }
  
  public void addRuoloDestinatarioPG(String nomeImpresa, String cfImpresa) {
	 RuoloType ruoloType = soggettiType.addNewRuolo();
	 TipoSoggetto31Type destinatario = ruoloType.addNewDestinatario();
	 destinatario.setTipoRuolo("Destinatario");
	 PGType pgType = destinatario.addNewPG();
	 pgType.setDenominazioneOrganizzazione(nomeImpresa);
	 pgType.setCodiceFiscalePartitaIva(cfImpresa);
  }  
  
  public void setChiaveDescrittiva(String oggetto, String[] elencoParolaChiave) {
	  ChiaveDescrittivaType chiaveDescrittivaType = doc.addNewChiaveDescrittiva();
	  chiaveDescrittivaType.setOggetto(oggetto);
	  chiaveDescrittivaType.setParoleChiaveArray(elencoParolaChiave);	  
  }
  
  public void setAllegati(int numeroAllegati) {
	  AllegatiType allegatiType = doc.addNewAllegati();
	  allegatiType.setNumeroAllegati(numeroAllegati);
  }
  
  public void setRiservato(boolean isRiservato) {
	  doc.setRiservato(isRiservato);
  }
  
  public void setIdentificativoDelFormato(String formato) {
	  IdentificativoDelFormatoType identificativoDelFormatoType = doc.addNewIdentificativoDelFormato();
	  identificativoDelFormatoType.setFormato(formato);	  
  }
  
  public void setVerifica(boolean isFirmatoDigitalmente, boolean isSigillatoElettronicamente, boolean isMarcaturaTemporale) {
	  VerificaType verificaType = doc.addNewVerifica();
	  verificaType.setFirmatoDigitalmente(isFirmatoDigitalmente);
	  verificaType.setSigillatoElettronicamente(isSigillatoElettronicamente);
	  verificaType.setMarcaturaTemporale(isMarcaturaTemporale);
  }
  
  public void setNomeDelDocumento(String nomeDocumento) {
	  doc.setNomeDelDocumento(nomeDocumento);
  }
  
  public void setVersioneDelDocumento(String versione) {
	  doc.setVersioneDelDocumento(versione);
  }
  
  public void setNote(String note) {
	  doc.setNote(note);
  }

  public void save(String fileNameWithPath) throws Exception {
	File fileDocumentoXml = new File(fileNameWithPath);
	document.save(fileDocumentoXml, options);
  }

}
