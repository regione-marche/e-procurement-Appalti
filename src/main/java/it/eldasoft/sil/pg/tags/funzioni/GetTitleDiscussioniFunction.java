package it.eldasoft.sil.pg.tags.funzioni;

import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.tags.functions.AbstractGetTitleFunction;


public class GetTitleDiscussioniFunction extends AbstractGetTitleFunction {

  protected String getTitleInserimento(PageContext pageContext, String table) {
    return null;
  }

  protected String getTitleModifica(PageContext pageContext, String table,
      String keys) {
    return null;
  }

  public String[] initFunction() {
    return new String[] {
        // Conversazioni
        "W_DISCUSS_P|Nuova conversazione|{0}" +
            "|| select discoggetto from w_discuss_p where discid_p = #W_DISCUSS_P.DISCID_P#",
        // Messaggi della conversazione
        "W_DISCUSS|Nuovo messaggio|{0}"
            + "|| select discmesstesto from w_discuss where discid_p = #W_DISCUSS.DISCID_P# and discid = #W_DISCUSS.DISCID#",
        // Allegati dei messaggi
        "W_DISCALL|Nuovo allegato|Allegato del messaggio '{0}'" + 
            "||select discmesstesto from w_discall, w_discuss " +
            " where w_discall.discid_p = #W_DISCALL.DISCID_P# and w_discall.discid = #W_DISCALL.DISCID# and " +
            " w_discall.allnum = #W_DISCALL.ALLNUM# and w_discall.discid_p = w_discuss.discid_p and w_discall.discid = w_discuss.discid",
    };
  }

}
