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
 * Decoratore del campo GAREALBO.TIPOELE per la pagina di scheda del
 *
 * @author Luca.Giacomazzo
 */
public class GestoreCampoTipoElenco extends AbstractGestoreCampo {

	public GestoreCampoTipoElenco() {
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
		if(!visualizzazione && abilitato){
			StringBuffer result = new StringBuffer("<span style=\"display: none;\">");
			return result.toString();
		} else
			return null;
	}

	@Override
  public String getHTML(boolean visualizzazione, boolean abilitato) {
		return null;
	}

	@Override
  public String postHTML(boolean visualizzazione, boolean abilitato) {
		if(!visualizzazione && abilitato){
			boolean isLavoriChecked = false;
			boolean isFornitureChecked = false;
			boolean isServiziChecked = false;
			boolean isAltroChecked = false;


			String garaAvviso = (String) this.getPageContext().getAttribute("garaAvviso",
                PageContext.REQUEST_SCOPE);

			if(this.campo.getValue() != null && this.campo.getValue().length() > 0){
				char[] valore = this.campo.getValue().toCharArray();
				if(valore.length == 1){
					if('4'== valore[0])
					  isAltroChecked = true;
					else
					  isServiziChecked = '1' == valore[0];
				}else {
					if(valore.length == 2){
						isFornitureChecked = '1' == valore[0];
						isServiziChecked = '1' == valore[1];
					} else {
						isLavoriChecked = '1' == valore[0];
						isFornitureChecked = '1' == valore[1];
						isServiziChecked = '1' == valore[2];
					}
				}
			}
			StringBuffer stringBuffer = new StringBuffer("");
			stringBuffer.append("</span>");
			stringBuffer.append("<span id=\"spanCheckBox\">");
			stringBuffer.append("<input type=\"checkbox\" id=\"checkLavori\" " +
                    "name=\"checkLavori\" value=\"1\" onclick=\"javascript:calcolaTipoEle(1);\"");
            if(isLavoriChecked)
                stringBuffer.append(" checked=\"true\" ");
            stringBuffer.append("/>Lavori");
            stringBuffer.append("&nbsp;&nbsp;&nbsp;");

			stringBuffer.append("<input type=\"checkbox\" id=\"checkForniture\" " +
					"name=\"checkForniture\" value=\"2\" onclick=\"javascript:calcolaTipoEle(2);\" ");
			if(isFornitureChecked)
				stringBuffer.append(" checked=\"true\" ");
			stringBuffer.append("/>Forniture");
			stringBuffer.append("&nbsp;&nbsp;&nbsp;");
			stringBuffer.append("<input type=\"checkbox\" id=\"checkServizi\" " +
					"name=\"checkServizi\" value=\"3\" onclick=\"javascript:calcolaTipoEle(3);\" ");
			if(isServiziChecked)
				stringBuffer.append(" checked=\"true\" ");
			stringBuffer.append("/>Servizi");
			stringBuffer.append("&nbsp;&nbsp;&nbsp;");
			if("true".equals(garaAvviso)){
			  stringBuffer.append("<input type=\"checkbox\" id=\"checkAltro\" " +
                    "name=\"checkAltro\" value=\"4\" onclick=\"javascript:calcolaTipoEle(4);\" ");
			  if(isAltroChecked)
                stringBuffer.append(" checked=\"true\" ");
              stringBuffer.append("/>Altro");
			}
			stringBuffer.append("</span>");
			return stringBuffer.toString();
		} else
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