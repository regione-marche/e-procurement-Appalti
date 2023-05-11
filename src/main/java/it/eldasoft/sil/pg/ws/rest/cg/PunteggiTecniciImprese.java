package it.eldasoft.sil.pg.ws.rest.cg;

public class PunteggiTecniciImprese {
	private String codimp;
	private double punteggio;
	/**
	 * @return the codimp
	 */
	public String getCodimp() {
		return codimp;
	}
	/**
	 * @return the punteggio
	 */
	public double getPunteggio() {
		return punteggio;
	}
	
	public PunteggiTecniciImprese codimp(String codimp) {
		this.codimp = codimp;
		return this;
	}
	
	public PunteggiTecniciImprese punteggio(double punteggio) {
		this.punteggio = punteggio;
		return this;
	}
}
