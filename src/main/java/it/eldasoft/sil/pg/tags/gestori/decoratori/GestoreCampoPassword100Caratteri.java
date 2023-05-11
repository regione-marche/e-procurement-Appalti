/*
 * Created on 07/mar/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.decoratori;

import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoLogin;
import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * In edit viene generato il campo di input password in cui è possibile inserire
 * fino a 100 caratteri
 *
 * @author Stefano.Sabbadin
 * @since 1.4.4
 */
public class GestoreCampoPassword100Caratteri extends GestoreCampoLogin {

  @Override
  public String getHTML(boolean visualizzazione, boolean abilitato) {
    String risultato = null;
    if (!visualizzazione && abilitato) {
      StringBuffer buf = new StringBuffer("");
      buf.append("<input ");
      buf.append(this.getDefaultHtml(true));
      buf.append(UtilityTags.getHtmlAttrib("class", "testo"));
      buf.append(UtilityTags.getHtmlAttrib("type", "password"));
      buf.append(UtilityTags.getHtmlAttrib("size", "30"));
      buf.append(UtilityTags.getHtmlAttrib("value", this.getCampo().getValue()));
      buf.append(UtilityTags.getHtmlAttrib("maxlength","100"));
      buf.append("/>");
      risultato = buf.toString();
    }

    return risultato;
  }
}

