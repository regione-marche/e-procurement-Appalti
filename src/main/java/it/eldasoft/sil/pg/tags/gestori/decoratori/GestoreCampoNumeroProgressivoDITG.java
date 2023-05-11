/*
 * Created on 05/nov/08
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

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Gestore del DITG.NPROGG e del campo DITG.NUMORDPL per inserire il valore del
 * campo dentro il tag HTML 'span' provvisto di un id che e' funzione del 
 * numero di riga
 *  
 * @author Luca.Giacomazzo
 */
public class GestoreCampoNumeroProgressivoDITG extends AbstractGestoreCampo {

  public String gestisciDaTrova(Vector params,
      DataColumn colWithValue, String conf, SqlManager manager) {
    return null;
  }

  public String getClasseEdit() {
    return null;
  }

  public String getClasseVisua() {
    return null;
  }

  public String getHTML(boolean visualizzazione, boolean abilitato) {
    if(campo.isVisualizzazione()) {
      StringBuffer buf = new StringBuffer("");
      buf.append("<span ");
      // Appendo il tooltip se non è un link con il proprio tooltip
      if(campo.getHref() == null || campo.getTitleHref() == null)
        buf.append(UtilityTags.getHtmlAttrib("title", campo.getTooltip()));

      String[] arrayCampo = campo.getNome().split("_");
      buf.append(UtilityTags.getHtmlAttrib("id", arrayCampo[1] + "_VALUE_" +
      		arrayCampo[2]));
      buf.append(">");

      // Appendo il valore per la visualizzazione
      buf.append(campo.getValuePerVisualizzazione());
      buf.append("</span>");
      return buf.toString();
    } else
      return null;
  }

  public String getValore(String valore) {
    return null;
  }

  public String getValorePerVisualizzazione(String valore) {
    return null;
  }

  public String getValorePreUpdateDB(String valore) {
    return null;
  }

  protected void initGestore() {

  }

  public String postHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  public String preHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

}