package it.eldasoft.sil.pg.ws.rest.cg;

import java.util.List;

public class CgResponse {
	private List<Coefficienti> coefficienti;
	private List<PunteggiMassimi> punteggiMassimi;
	private List<PunteggiTecniciImprese> punteggiTecniciImprese;
	private ResponseStatus statoCalcolo;


  /**
	 * @return the coefficienti
	 */
	public List<Coefficienti> getCoefficienti() {
		return coefficienti;
	}
	/**
	 * @param coefficienti the coefficienti to set
	 */
	public void setCoefficienti(List<Coefficienti> coefficienti) {
		this.coefficienti = coefficienti;
	}
	/**
	 * @return the punteggiMassimi
	 */
	public List<PunteggiMassimi> getPunteggiMassimi() {
		return punteggiMassimi;
	}
	/**
	 * @param punteggiMassimi the punteggiMassimi to set
	 */
	public void setPunteggiMassimi(List<PunteggiMassimi> punteggiMassimi) {
		this.punteggiMassimi = punteggiMassimi;
	}
	/**
	 * @return the punteggiTecniciImprese
	 */
	public List<PunteggiTecniciImprese> getPunteggiTecniciImprese() {
		return punteggiTecniciImprese;
	}
	/**
	 * @param punteggiTecniciImprese the punteggiTecniciImprese to set
	 */
	public void setPunteggiTecniciImprese(List<PunteggiTecniciImprese> punteggiTecniciImprese) {
		this.punteggiTecniciImprese = punteggiTecniciImprese;
	}

	/**
	 *
	 * @return statoCalcolo
	 */
	public ResponseStatus getStatoCalcolo() {
	  return statoCalcolo;
	}

	/**
	 *
	 * @param statoCalcolo
	 */
	public void setStatoCalcolo(ResponseStatus statoCalcolo) {
	  this.statoCalcolo = statoCalcolo;
	}
}
