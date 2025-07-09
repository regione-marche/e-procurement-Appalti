package it.eldasoft.sil.pg.bl.tasks;

import java.util.HashMap;

public class CampiDocumento {
	
	private HashMap<String,Object> campi;
	
	public CampiDocumento() {
		campi = new HashMap<String,Object>();
	}

	public HashMap<String,Object> getHashCampi(){
		return campi;
	}
	
	public CampiDocumento put(String chiave, Object valore) {
		campi.put(chiave, valore);
		return this; // per chaining
	}

	public Object get(String chiave) {
		return campi.get(chiave);
	}
	
}//CampiDocumento
