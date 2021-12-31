/*
 * Created on 067/10/11
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

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Viene letto il tabellato 'A1z03' di tab2 per costruire il filtro
 * su tipgar per la lista delle gare
 * 
 * @author Marcello Caminiti
 */
public class GetFiltroTipoGaraFunction extends AbstractFunzioneTag {

  
  public GetFiltroTipoGaraFunction(){
    super(1, new Class[]{PageContext.class});
  }
  
  
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    
    
    String filtro = "";
    String profiloAttivo = (String) pageContext.getSession().getAttribute("profiloAttivo");
    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        pageContext, PgManager.class);  
    try {
      String descTab = pgManager.getFiltroTipoGara(profiloAttivo);
      if (descTab!=null)
        filtro = "V_GARE_TORN.TIPGAR IN(" + descTab + ")";
    } catch (GestoreException e) {
      throw new JspException(
          "Errore durante la lettura della tabellato 'A1z03' ", e);
    }
    
    return filtro;
  }
  
}