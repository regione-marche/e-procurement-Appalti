/*
 * Created on 10/02/14
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
 * Gestore del campo
 *
 * @author Marcello Caminiti C0OGGASS.C0ATIT della pagina gare-popup-visualizzaDocAppalto.jsp
 */
public class GestoreCampoTitoloDocumentoDownload extends AbstractGestoreCampo {

  @Override
  public String gestisciDaTrova(Vector params,
      DataColumn colWithValue, String conf, SqlManager manager) {
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
  public String getHTML(boolean visualizzazione, boolean abilitato) {
    if(campo.isVisualizzazione()) {
      StringBuffer buf = new StringBuffer("");
      buf.append("<span ");
      buf.append(UtilityTags.getHtmlAttrib("title", "Download documento"));
      buf.append(">");
      // Appendo il valore per la visualizzazione
      buf.append(campo.getValuePerVisualizzazione());
      buf.append("</span>");
      return buf.toString();
    } else
      return null;
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
  protected void initGestore() {

  }

  @Override
  public String postHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

}