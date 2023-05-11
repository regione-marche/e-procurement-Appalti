package it.eldasoft.sil.pg.ws.rest.cg;

public class PunteggiMassimi {
	private Long necvan;
	private Long necvan1;
	private double punteggioMassimo;
	/**
	 * @return the necvan
	 */
	public Long getNecvan() {
		return necvan;
	}
	/**
	 * @return the necvan
	 */
	public Long getNecvan1() {
		return necvan1;
	}
	/**
	 * @return the necvan
	 */
	public double getPunteggioMassimo() {
		return punteggioMassimo;
	}

	public PunteggiMassimi necvan(Long necvan) {
		this.necvan = necvan;
		return this;
	}
	public PunteggiMassimi necvan1(Long necvan1) {
		this.necvan1 = necvan1;
		return this;
	}
	public PunteggiMassimi punteggioMassimo(double punteggioMassimo) {
		this.punteggioMassimo = punteggioMassimo;
		return this;
	}

}
