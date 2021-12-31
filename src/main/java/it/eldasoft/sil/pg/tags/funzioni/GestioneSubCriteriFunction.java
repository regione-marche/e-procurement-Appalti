/*
 * Created on 20/nov/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare le sezioni dei sub-criteri
 *
 * @author Marcello Caminiti
 */
public class GestioneSubCriteriFunction extends AbstractFunzioneTag {

  public GestioneSubCriteriFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    // Precarico le occorrenze della PERP solo se si accede alla scheda di una
    // seduta di gara in visualizzazione e in modifica
    if(! UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(
        UtilityTags.getParametro(pageContext,
            UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))){
      String[] chiave = ((String) params[0]).split(";");

      String nGara, numeroCriterio = null;
      if(chiave[0].indexOf("NGARA") >= 0){
        nGara = chiave[0].substring(chiave[0].indexOf(":")+1);
        numeroCriterio = chiave[1].substring(chiave[1].indexOf(":")+1);
      } else {
        nGara = chiave[1].substring(chiave[1].indexOf(":")+1);
        numeroCriterio = chiave[0].substring(chiave[0].indexOf(":")+1);
      }
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);
      try {
        List listaSubCriteri = sqlManager.getListVector(
            "select NGARA, NECVAN, NORPAR1, DESPAR, MAXPUN, NECVAN1 " +
                "from GOEV where NGARA = ? and NECVAN <> ? and NECVAN1 = ?" +
                " order by NORPAR1",
              new Object[]{nGara, new Long(numeroCriterio), new Long(numeroCriterio)});

        if (listaSubCriteri != null && listaSubCriteri.size() > 0)
        	pageContext.setAttribute("subCriteri", listaSubCriteri,
              PageContext.REQUEST_SCOPE);
      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre i sub-criteri presenti " +
              "alla seduta della gara " + nGara, e);
      }
    }
    return null;
  }

}