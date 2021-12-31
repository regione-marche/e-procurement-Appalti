/*
 * Created on 13/03/15
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/*
 *  Viene controllato se esistono delle occorrenze in GARRETTIF per la gara e il tipo
 *  passati come parametri
 */
public class EsistonoDatiRettificaTerminiFunction extends AbstractFunzioneTag {

  public EsistonoDatiRettificaTerminiFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String codgar = (String) params[1];
    String tipo = (String) params[2];

    String esistonoDatiRettificaTermini = "false";

    if (codgar != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

      try {

        String selectGarrettif = "select count(*) from garrettif where codgar = ? and tipter = ? ";
        Long conteggio = (Long) sqlManager.getObject(selectGarrettif, new Object[]{codgar, new Long(tipo)});

        if (conteggio != null && conteggio.longValue() > 0) {
          esistonoDatiRettificaTermini = "true";
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo dell'esistenza dello storico delle rettifiche per la gara " + codgar +" e tipo " + tipo, e);
      }
    }

    return esistonoDatiRettificaTermini;
  }

}