/*
 * Created on 26-09-2017
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
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che controlla se esiste integrazione con Cineca e
 * se la categoria riguarda forniture o servizi
 * al fine di presentare la sezione per il collegamento con i beni/servizi
 * (T_UBUY_BENISERVIZI in CINECA)
 *
 * @author Cristian Febas
 */
public class IsBeniServiziCinecaFunction extends AbstractFunzioneTag {

  public IsBeniServiziCinecaFunction() {
    super(2, new Class[] {PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String key = (String) params[1];

    String isBS= "false";
    Long tipo =null;

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String integrazioneCineca = ConfigManager.getValore("integrazioneAnagraficheUGOV");
    integrazioneCineca = UtilityStringhe.convertiNullInStringaVuota(integrazioneCineca);

    try {

      tipo = (Long) sqlManager.getObject("select tiplavg from cais where caisim = ?", new String[] {key});

    } catch (SQLException e) {
      throw new JspException("Errore nel calcolo della tipologia della categoria " + key  ,e);
    }


    if("1".equals(integrazioneCineca) && (new Long(2).equals(tipo) || new Long(3).equals(tipo))){
      isBS = "true";
    }

    return isBS;

  }

}
