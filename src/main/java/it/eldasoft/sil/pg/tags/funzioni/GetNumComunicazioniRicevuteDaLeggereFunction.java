/*
 * Created on 29/07/2012
 *
 * Copyright (c) Maggioli S.p.A. - Divisione EldaSoft
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.GestoreProfili;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per il calcolo del numero di comunicazioni ricevute dal portale ma non ancora lette. Attenzione: la funzione lavora
 * correttamente se la chiave &egrave; costituita da un solo campo.
 *
 * @author Stefano.Sabbadin
 *
 */
public class GetNumComunicazioniRicevuteDaLeggereFunction extends AbstractFunzioneTag {

  public GetNumComunicazioniRicevuteDaLeggereFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String codice = (String) params[1];
    String select = "select count(idcom) from w_invcom where idprg=? and coment is null and comkey2=? and comtipo=? and comdatlet is null and comstato='3' ";

    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        pageContext, PgManagerEst1.class);
    GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
        pageContext, GeneManager.class);
    GestoreProfili gestoreProfili = geneManager.getProfili();
    String profiloAttivo = (String) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);
    String profilo ="1";
    if (gestoreProfili.checkProtec(profiloAttivo, "FUNZ", "VIS", "ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare"))
      profilo ="2";
    else if (gestoreProfili.checkProtec(profiloAttivo, "FUNZ", "VIS", "ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare"))
      profilo ="3";
    select += pgManagerEst1.getFiltroComunicazioniSoccorsoIstruttorio(profilo);

    Long conteggio = null;
    try {
      conteggio = (Long) sqlManager.getObject(select, new Object[] {"PA", codice, "FS12" });
    } catch (SQLException e) {
      throw new JspException("Errore nel conteggio di comunicazioni ricevute non ancora lette per il record con chiave " + codice, e);
    }

    return conteggio.toString();
  }

}
