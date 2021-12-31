/*
 * Created on 01/set/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.decoratori;

import java.util.Vector;

import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;

/**
 * Gestore del campo V_GARE_ELEDITTE.TIPOELE per la pagina di trova elenchi
 *
 * @author Luca.Giacomazzo
 */
public class GestoreCampoTipoElencoPaginaTrova extends AbstractGestoreCampo {

	public GestoreCampoTipoElencoPaginaTrova() {
		super();
	}

	@Override
  public String getValore(String valore) {
		return null;
	}

	@Override
  public String getValorePerVisualizzazione(String valore) {
		return null;
	}

	@Override
  public String getValorePreUpdateDB(String valore) {
		return null;
	}

	@Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {
		return null;
	}

	@Override
  public String getHTML(boolean visualizzazione, boolean abilitato) {



	  String idMAschera = (String) this.getPageContext().getAttribute("ID_MASCHERA_REQUEST",
          PageContext.REQUEST_SCOPE);

	  /*
	  GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
          this.getPageContext(), GeneManager.class);
	  boolean isCatalogo= geneManager.getProfili().checkProtec(
          (String) this.getPageContext().getAttribute(
              CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS",
          "ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare");
      */

	  StringBuffer stringBuffer = new StringBuffer("");
		stringBuffer.append("<select id=\"Campo1\" name=\"Campo1\" >");
		stringBuffer.append("<option value=\"\">&nbsp;</option>");
		stringBuffer.append("<option value=\"Lavori\"");
  		if("Lavori".equalsIgnoreCase(this.campo.getValue()))
  			stringBuffer.append(" selected=\"true\"");
  		stringBuffer.append(">Lavori</option>");
		stringBuffer.append("<option value=\"Forniture\"");
		if("Forniture".equalsIgnoreCase(this.campo.getValue()))
			stringBuffer.append(" selected=\"true\"");
		stringBuffer.append(">Forniture</option>");

		stringBuffer.append("<option value=\"Servizi\"");
		if("Servizi".equalsIgnoreCase(this.campo.getValue()))
			stringBuffer.append(" selected=\"true\"");
		stringBuffer.append(">Servizi</option>");
		stringBuffer.append("</select>");
		return stringBuffer.toString();
	}

	@Override
  public String postHTML(boolean visualizzazione, boolean abilitato) {
		return null;
	}

	@Override
  public String getClasseEdit() {
		return null;
	}

	@Override
  public String getClasseVisua() {
		return null;
	}

	@Override
  protected void initGestore() {
	}

	@Override
  public String gestisciDaTrova(Vector params, DataColumn col, String conf,
			SqlManager manager) {
		return null;
	}

}