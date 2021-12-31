package it.eldasoft.sil.pg.tags.funzioni;

import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.tags.functions.AbstractGetTitleFunction;


public class GetTitleComunicazioneFunction extends AbstractGetTitleFunction {

  protected String getTitleInserimento(PageContext pageContext, String table) {
    return null;
  }

  protected String getTitleModifica(PageContext pageContext, String table,
      String keys) {
    return null;
  }

  public String[] initFunction() {
    return new String[] {
        // Comunicazioni
        "W_INVCOM|Nuova comunicazione|Comunicazione del {0}" +
            "|| select comdatins from w_invcom where idprg = #W_INVCOM.IDPRG# and idcom = #W_INVCOM.IDCOM#",
        // Comunicazioni in arrivo
        "W_INVCOM_IN||Comunicazione ricevuta il {0}"
            + "|| select comdatins from w_invcom where idprg = #W_INVCOM.IDPRG# and idcom = #W_INVCOM.IDCOM#",
        // Allegati
        "W_DOCDIG|Nuovo allegato|Allegato della comunicazione del {0}" + 
            "||select comdatins, iddocdig from w_invcom, w_docdig " +
            " where w_docdig.idprg = #W_DOCDIG.IDPRG# and w_docdig.iddocdig = #W_DOCDIG.IDDOCDIG# and " +
            " w_invcom.idprg = w_docdig.digkey1 and w_invcom.idcom = w_docdig.digkey2 and w_docdig.digent = 'W_INVCOM'",
    };
  }

}
