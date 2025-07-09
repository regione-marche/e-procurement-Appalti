package it.eldasoft.sil.pg.bl.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataInfoDocumentiContainer {
	
	private Map<String, List<CampiDocumento>> dati;
	private Map<String, Map<String, Map<String,List<CampiDocumento>>>> datiAggregati;
	
	public DataInfoDocumentiContainer() {
		dati = new HashMap<>();
		datiAggregati  = new HashMap<>();
	}
	
	public void add(String versoLista, String qualeLista, CampiDocumento campi){
		List<CampiDocumento> listaCampi= dati.get(qualeLista);
		campi.put("versoLista", versoLista);
		if (listaCampi==null) {
			listaCampi = new ArrayList();
			dati.put(qualeLista, listaCampi);
		}
		listaCampi.add(campi);
	}
	
	public List<CampiDocumento> get(String qualeLista){
		return dati.get(qualeLista);
	}
	
	public Set<String> getChiaviListeDocumenti(){
		return dati.keySet();
	}
	
	public Set<String> getChiaviListeDatiAggregati(){
		return datiAggregati.keySet();
	}
	
	public Set<String> getChiaviDocumentiDatiAggregati(String qualeLista){
		return datiAggregati.get(qualeLista).keySet();
	}
	
	public Set<String> getChiaviDestinatariDatiAggregati(String qualeLista, String documento){
		return datiAggregati.get(qualeLista).get(documento).keySet();
	}
	
	public void addAggregato(String versoLista, String qualeLista, String documento, String destinatario, CampiDocumento campi) {
		
		Map<String, Map<String,List<CampiDocumento>>> documentoHash = datiAggregati.get(qualeLista);
		Map<String,List<CampiDocumento>> destinatariHash;
		List<CampiDocumento> listaCampi;
		campi.put("versoLista", versoLista);
		
		if (documentoHash==null) {
			// significa che non ho ancora inserito documenti nella "lista" qualeLista
			documentoHash   = new HashMap<>();
			destinatariHash = new HashMap<>();
			listaCampi      = new ArrayList<>();
			
			listaCampi.add(campi);
			destinatariHash.put(destinatario, listaCampi);
			documentoHash.put(documento, destinatariHash);
			datiAggregati.put(qualeLista, documentoHash);
			
		} else {
			// significa che ho gia' inserito dei campi relativi a questo documento
			destinatariHash = documentoHash.get(documento);
			
			if (destinatariHash==null){
				destinatariHash = new HashMap<>();
				listaCampi      = new ArrayList<>();				
				listaCampi.add(campi);
				destinatariHash.put(destinatario, listaCampi);
				documentoHash.put(documento, destinatariHash);
			} else {
			
				if (!destinatariHash.containsKey(destinatario)) {
					listaCampi = new ArrayList<>();
					listaCampi.add(campi);
					destinatariHash.put(destinatario, listaCampi);
				} else {
					listaCampi = destinatariHash.get(destinatario);
					listaCampi.add(campi);
				}//if
				
			}//if
			
		}//if
		
	}//addAggregato
	
	public Set<String> getElencoDocumenti(String qualeLista){
		Map<String, Map<String,List<CampiDocumento>>> documentiHash = datiAggregati.get(qualeLista);
		return documentiHash.keySet();
	}
	
	public Set<String> getElencoDestinatari(String qualeLista, String documento){
		Map<String, Map<String,List<CampiDocumento>>> documentiHash = datiAggregati.get(qualeLista);
		Map<String,List<CampiDocumento>> destinatariHash = documentiHash.get(documento);
		return destinatariHash.keySet();
	}
	
	public List<CampiDocumento> getElencoCampi(String qualeLista, String documento, String destinatario){
		Map<String, Map<String,List<CampiDocumento>>> documentiHash = datiAggregati.get(qualeLista);
		Map<String,List<CampiDocumento>> destinatariHash = documentiHash.get(documento);
		return destinatariHash.get(destinatario);
	}

}//DataInfoDocumentiContainer
