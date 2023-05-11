/*
 * Created on 19/nov/08
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
import java.sql.Timestamp;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Inizializzazione della popup di aggiudicazione definitiva per definire la
 * ditta aggiudicataria della gara: si determina se la modalita' di
 * aggiudicazione e' supportata o meno
 *
 * @author Luca.Giacomazzo
 */
public class GestorePopupAggiudicazioneDefinitivaFunction extends AbstractFunzioneTag {

  public GestorePopupAggiudicazioneDefinitivaFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String ngara = (String) params[1];
    String genere = (String) params[2];

    AggiudicazioneManager aggiudicazioneManager = (AggiudicazioneManager) UtilitySpring.getBean(
        "aggiudicazioneManager", pageContext, AggiudicazioneManager.class);

    try {
      if (!aggiudicazioneManager.controllaModalitaAggiudicazione(ngara)) {
        pageContext.setAttribute("modAggiuNoSupportata", "1");
        UtilityStruts.addMessage(pageContext.getRequest(), "error",
            "errors.gestoreException.*.aggiudicazioneFaseA.ControlloModalitaAggiudicazione",
            null);
      } else if(! aggiudicazioneManager.controlloAggiudicazioneProvvisoriaEseguita(ngara)){
        pageContext.setAttribute("modAggiuNoSupportata", "2");
        /*UtilityStruts.addMessage(pageContext.getRequest(), "error",
            "errors.gestoreException.*.aggiudicazioneFaseA.ControlloAggiudicazioneEseguita",
            null);*/
      }
    } catch (GestoreException e) {
      throw new JspException(
          "Errore in fase di determinazione se il criterio di aggiudicazione"
              + " in uso e' supportato o meno o se l'aggiudicazione provvisoria"
              + " e' stata avviata o meno", e);
    }

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    Long modastg = null;
    Long modlicg = null;
    Long esineg = null;
    Timestamp dattoa = null;
    String inversa = null;
    String codgar = null;
    try {
      Vector datiGare = sqlManager.getVector("select g.modastg,g.modlicg,g.esineg, g.dattoa,t.inversa, t.codgar from gare g,torn t where g.ngara = ? and t.codgar=g.codgar1", new Object[]{ngara});
      if(datiGare!=null && datiGare.size()>0){
        modastg=SqlManager.getValueFromVectorParam(datiGare, 0).longValue();
        modlicg=SqlManager.getValueFromVectorParam(datiGare, 1).longValue();
        esineg=SqlManager.getValueFromVectorParam(datiGare, 2).longValue();
        dattoa = SqlManager.getValueFromVectorParam(datiGare, 3).dataValue();
        inversa = SqlManager.getValueFromVectorParam(datiGare, 4).stringValue();
        codgar = SqlManager.getValueFromVectorParam(datiGare, 5).stringValue();
      }
      pageContext.setAttribute("modastg", modastg, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("modlicg", modlicg, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("esineg", esineg, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("inversa", inversa, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("codgar", codgar, PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      throw new JspException(
          "Errore nella lettura dei dati di Gare", e);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore nella lettura dei dati di Gare", e);
    }


    boolean bloccoDattoa=false;
    try {
        Vector datiGare1 = sqlManager.getVector("select aqoper, aqnumope from gare1 where ngara=?", new Object[]{ngara});
        if(datiGare1!=null){
          Long aqoper = SqlManager.getValueFromVectorParam(datiGare1, 0).longValue();
          Long aqnumope = SqlManager.getValueFromVectorParam(datiGare1, 1).longValue();
          pageContext.setAttribute("aqoper", aqoper, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("aqnumope", aqnumope, PageContext.REQUEST_SCOPE);
        }
        if("3".equals(genere) && dattoa!=null){
          //Si deve controllare se vi sono altri lotti aggiudicati
          String select = "select count(ngara) from gare where codgar1=(select codgar1 from gare where ngara=?) and ngara!=codgar1 and (ditta !=null or ditta is not null) and ngara!=?";
          Long conteggio = (Long)sqlManager.getObject(select, new Object[]{ngara,ngara});
          if(conteggio!=null && conteggio.longValue()>0)
            bloccoDattoa= true;
        }

    } catch (SQLException e) {
      throw new JspException(
          "Errore in fase di estrazione dei dati di TORN", e);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore in fase di estrazione dei dati di TORN", e);
    }
    pageContext.setAttribute("bloccoDattoa", new Boolean(bloccoDattoa), PageContext.REQUEST_SCOPE);

    return null;
  }

}