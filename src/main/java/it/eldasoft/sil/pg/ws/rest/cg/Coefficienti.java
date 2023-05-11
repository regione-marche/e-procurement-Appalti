package it.eldasoft.sil.pg.ws.rest.cg;

public class Coefficienti {
	private String ngara;
	private String codimp;
	private Long necvan;//id criterio
	private Long necvan1;//id criterio sub
	private double coefficiente;
	private double punteggio;
	
	/**
	 * @return the ngara
	 */
	public String getNgara() {
		return ngara;
	}
	/**
	 * @return the codimp
	 */
	public String getCodimp() {
		return codimp;
	}
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
	 * @return the coefficiente
	 */
	public double getCoefficiente() {
		return coefficiente;
	}
	/**
	 * @return the punteggio
	 */
	public double getPunteggio() {
		return punteggio;
	}
	public Coefficienti ngara(String ngara) {
		this.ngara = ngara;
		return this;
	}
	public Coefficienti codimp(String codimp) {
		this.codimp = codimp;
		return this;
	}
	public Coefficienti necvan(Long necvan) {
		this.necvan = necvan;
		return this;
	}
	public Coefficienti necvan1(Long necvan1) {
		this.necvan1 = necvan1;
		return this;
	}
	public Coefficienti coefficiente(double coefficiente) {
		this.coefficiente = coefficiente;
		return this;
	}
	public Coefficienti punteggio(double punteggio) {
		this.punteggio = punteggio;
		return this;
	}
	@Override
	public String toString() {
		return "Coefficienti [" + (ngara != null ? "ngara=" + ngara + ", " : "")
				+ (codimp != null ? "codimp=" + codimp + ", " : "") + (necvan != null ? "necvan=" + necvan + ", " : "")
				+ "coefficiente=" + coefficiente + ", punteggio=" + punteggio + "]";
	}
	
	
}
