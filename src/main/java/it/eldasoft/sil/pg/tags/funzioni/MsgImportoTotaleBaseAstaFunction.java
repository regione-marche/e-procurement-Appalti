/*
 * Created on 17/nov/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Classe che gestisce la modifica del messaggio d'errore sugli importi base
 * di gara nel dettaglio gara
 * 
 * @author Stefano.Sabbadin
 */
public class MsgImportoTotaleBaseAstaFunction extends AbstractFunzioneTag {

  public MsgImportoTotaleBaseAstaFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    StringBuffer buf = new StringBuffer("");
    Vector<String[]> defs = new Vector<String[]>();
    String campoTot = (String) params[1];

    if ("GARE.IMPMIS".equals(campoTot)) {

      // "L'importo opere a misura deve essere maggiore o uguale
      // dell'importo non soggetto a ribasso e della sicurezza"
      buf.append("L'importo opere a misura deve essere maggiore o uguale dell'importo ");
      defs.add(new String[] { "GARE.IMPNRM", "GARE.IMPSMI" });
      defs.add(new String[] { "non soggetto a ribasso", "della sicurezza" });
    } else if ("GARE.IMPCOR".equals(campoTot)) {

      // "L'importo opere a corpo deve essere maggiore o uguale
      // dell'importo non soggetto a ribasso e della sicurezza"
      buf.append("L'importo opere a corpo deve essere maggiore o uguale dell'importo ");
      defs.add(new String[] { "GARE.IMPNRC", "GARE.IMPSCO" });
      defs.add(new String[] { "non soggetto a ribasso", "della sicurezza" });
    } else if ("GARE.IMPAPP".equals(campoTot)) {

      // "L'importo totale a base di gara deve essere maggiore o uguale
      // dell'importo non soggetto a ribasso, della sicurezza e degli oneri di
      // progettazione"
      buf.append("L'importo totale a base di gara deve essere maggiore o uguale dell'importo ");
      defs.add(new String[] { "GARE.IMPNRL", "GARE.IMPSIC", "GARE.ONPRGE"});
      defs.add(new String[] { "non soggetto a ribasso", "della sicurezza",
          "degli oneri di progettazione"});
    } else if ("GARE.IMPAPP_NO_ONPRGE".equals(campoTot)) {
    	//Per le gare per forniture e servizi
        // "L'importo totale a base di gara deve essere maggiore o uguale
        // dell'importo non soggetto a ribasso, della sicurezza 
        buf.append("L'importo totale a base di gara deve essere maggiore o uguale dell'importo ");
        defs.add(new String[] { "GARE.IMPNRL", "GARE.IMPSIC"});
        defs.add(new String[] { "non soggetto a ribasso", "della sicurezza"});
      }
    

    if (defs.size() == 2) {
      String campi[] = (String[]) defs.get(0);
      String descr[] = (String[]) defs.get(1);
      Vector<String> toAdd = new Vector<String>();

      for (int i = 0; i < campi.length; i++) {
        if (UtilityTags.checkProtection(pageContext, "COLS", "VIS", "GARE."
            + campi[i], true)) {
          toAdd.add(descr[i]);
        }
      }
      for (int i = 0; i < toAdd.size(); i++) {
        if (i > 0) {
          if (i == (toAdd.size() - 1))
            buf.append(" e ");
          else
            buf.append(", ");
        }
        buf.append(toAdd.get(i));
      }
    }
    return buf.toString();
  }

}
