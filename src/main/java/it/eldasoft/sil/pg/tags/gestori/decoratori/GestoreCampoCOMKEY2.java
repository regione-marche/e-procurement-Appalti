/*
 * Created on 07/03/2011
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
 * Gestore del campo W_INVCOM.COMKEY2 per modificare il tooltip
 * mettendo fisso il valore "Elenco"
 *  
 * @author Marcello Caminiti
 */
public class GestoreCampoCOMKEY2 extends AbstractGestoreCampo {

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
      buf.append(UtilityTags.getHtmlAttrib("title", "Elenco"));
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