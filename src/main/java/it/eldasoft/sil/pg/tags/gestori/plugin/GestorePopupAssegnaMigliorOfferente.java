/*
 * Created on 01/03/2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore che effettua i controlli preliminari pria di azionare l'assegnamento dei prodotti agli offerenti
 *
 * @author Cristian Febas
 */
public class GestorePopupAssegnaMigliorOfferente extends AbstractGestorePreload {

  private SqlManager sqlManager = null;

  public GestorePopupAssegnaMigliorOfferente(BodyTagSupportGene tag) {
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

    // lettura dei parametri di input
    String ngara = page.getRequest().getParameter("ngara");
    Vector<?> datiDPRE=null;
    String controlloSuperato="SI";
    String msg ="";
    String msgNonAssegnati ="";

    //Controllo se ci sono elementi aggiudicati
    try {
      Long numProdottiAggiudicati=(Long) this.sqlManager.getObject("select count(qtaordinata) from dpre where ngara=? and qtaordinata is not null", new Object[]{ngara});
      if(numProdottiAggiudicati>0) {
        controlloSuperato="NO";
        msg ="Non è possibile procedere all'assegnamento al miglior offerente in quanto esistono prodotti assegnati";
        page.setAttribute("msg", msg, PageContext.REQUEST_SCOPE);
      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura delle quantita' sui prodotti assegnati", e);
    }

    //Controllo se ci sono elementi non assegnati
    try {
      Long numProdottiNonAssegnati=(Long) this.sqlManager.getObject("select count(isprodneg) from gcap where ngara=? ", new Object[]{ngara});
      if(numProdottiNonAssegnati>0) {
        msgNonAssegnati ="ATTENZIONE: ci sono delle lavorazioni in stato \"non assegnato\". Queste lavorazioni vengono escluse dalla valutazione massiva.";
        page.setAttribute("msgNonAssegnati", msgNonAssegnati, PageContext.REQUEST_SCOPE);
      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura delle quantita' sui prodotti non assegnati", e);
    }

    page.setAttribute("controlloSuperato", controlloSuperato, PageContext.REQUEST_SCOPE);
  }


}