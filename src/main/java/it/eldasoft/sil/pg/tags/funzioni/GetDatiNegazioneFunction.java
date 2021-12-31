/*
 * Created on 18-03-2016
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
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Estrae da GARE1 i campi relativi alla negazione
 *
 *
 * @author Marcello Caminiti
 */
public class GetDatiNegazioneFunction extends AbstractFunzioneTag {

  public GetDatiNegazioneFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String select = null;
    String codiceGara = (String)params[1];
    String tipoGara= (String)params[2];
    String offerteDistinte = null;
    String offertaUnica = null;
    if("3".equals(tipoGara))
      offertaUnica = "Si";
    else if("1".equals(tipoGara))
      offerteDistinte = "Si";

    if (codiceGara != null && codiceGara.length() > 0) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);

      try {
        //Nel caso di gara ad offerte distinte non si caricano i dati da GARE1
        /*
        if("Si".equals(offerteDistinte))
          select = "select notneg, npannrevagg from GARE1 where codgar1 = ?";
        else
          select = "select notneg, npannrevagg from GARE1 where ngara = ?";
         */
        if(!"Si".equals(offerteDistinte)){
          String notneg = (String) sqlManager.getObject(
              "select notneg from GARE1 where ngara = ?",
              new Object[] { codiceGara });
          pageContext.getRequest().setAttribute("initNotneg", notneg);
        }

      } catch (SQLException s) {
        throw new JspException("Errore durante la lettura dei campi di GARE1 "
            + codiceGara, s);
      }

      Long conteggio = null;
      if("Si".equals(offerteDistinte)){
        select="select count(ngara) from gare where codgar1=? and #CAMPO# is not null";
      }else if("Si".equals(offertaUnica)){
        select="select count(ngara) from gare where codgar1=? and ngara!=codgar1 and #CAMPO# is not null ";
      }else{
        select="select count(ngara) from gare where ngara=? and #CAMPO# is not null";
      }
      try {
        //Si controlla se la gara (o un lotto) risulta già aggiudicato.
        String select1 = select.replace("#CAMPO#", "ditta");
        conteggio = (Long)sqlManager.getObject(select1, new Object[]{codiceGara});
        if(conteggio!=null && conteggio.longValue()>0)
          pageContext.getRequest().setAttribute("aggiudicazione", "Si");

        //Si controlla se per la gara (o un lotto) risulta presente un esito.
        select1 = select.replace("#CAMPO#", "esineg");
        conteggio = (Long)sqlManager.getObject(select1, new Object[]{codiceGara});
        if(conteggio!=null && conteggio.longValue()>0)
          pageContext.getRequest().setAttribute("esitoPresente", "Si");

      } catch (SQLException e) {
        throw new JspException("Errore nella verifica dell'aggiudicazione o della presenza dell'esito negativo della gara/lotto "
            + codiceGara, e);
      }
    }
    return null;
  }

}