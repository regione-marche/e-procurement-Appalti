/*
 * Created on 19/10/18
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore che popola la popup per la rettifica dell'importo di aggiudicazione
 *
 * @author Marcello Caminiti
 */
public class GestorePopupRettificaImportoAggiudicazione extends AbstractGestorePreload {

  private SqlManager sqlManager = null;
  private TabellatiManager tabellatiManager = null;
  private PgManager pgManager = null;
  private PgManagerEst1 pgManagerEst1 = null;

  public GestorePopupRettificaImportoAggiudicazione(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);

    tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        page, TabellatiManager.class);

    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        page, PgManager.class);

    pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        page, PgManagerEst1.class);

    // lettura dei parametri di input
    String codgar = page.getRequest().getParameter("codgar");
    String ngara = page.getRequest().getParameter("ngara");
    String isOfferteUnica = page.getRequest().getParameter("isOfferteUnica");
    String aqoper = page.getRequest().getParameter("aqoper");

    String numeroCifreDecimaliRibasso;
    try {
      numeroCifreDecimaliRibasso = this.pgManagerEst1.getNumeroDecimaliRibasso(codgar);
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura del numero di decimali da usare per il ribasso della gara(NGARA = '" + ngara + ")", e);
    }
    page.setAttribute("numeroCifreDecimaliRibasso",numeroCifreDecimaliRibasso);

    pgManager.getOFFAUM(page, sqlManager, ngara);

    if("2".equals(aqoper)){

      try {
        List listaDitteAggiudicatarie = sqlManager.getListVector("select aq.id, aq.ngara, aq.dittao, i.nomest, aq.ribagg, aq.puntot, aq.iaggiu, d.impoff, "
            + "d.impperm, d.impcano, d.ricsub, aq.ridiso, aq.impgar, aq.banapp, aq.coorba, aq.codbic, aq.ribaggini, aq.iaggiuini from ditgaq aq, impr i, ditg d where aq.ngara=? and aq.dittao = i.codimp and "
            + "aq.ngara=d.ngara5 and aq.dittao=d.dittao order by aq.numord", new Object[]{ngara});
        page.setAttribute("listaDitteAggiudicatarie", listaDitteAggiudicatarie,
            PageContext.REQUEST_SCOPE);
        long numeroDitteAggiudicatarie=0;
        if(listaDitteAggiudicatarie!=null & listaDitteAggiudicatarie.size()>0){
          numeroDitteAggiudicatarie=listaDitteAggiudicatarie.size();
        }
        page.setAttribute("numeroDitteAggiudicatarie", new Long(numeroDitteAggiudicatarie),
            PageContext.REQUEST_SCOPE);
      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre i dati di DITGAQ "
            + "della gara "
            + ngara, e);
      }

    }


  }


}