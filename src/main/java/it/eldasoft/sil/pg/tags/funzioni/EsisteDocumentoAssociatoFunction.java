/*
 * Created on 18-01-2023
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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che controlla se esistono documenti in WSDOCUMENTO
 *
 * @author Marcello Caminiti
 */
public class EsisteDocumentoAssociatoFunction extends AbstractFunzioneTag {

  public EsisteDocumentoAssociatoFunction() {
    super(3 , new Class[] { PageContext.class, String.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String esisteDoc= "false";
    String entita = (String) params[1];
    String chiave1 = (String) params[2];


    String select="select count(id) from wsdocumento where entita = ? and key1 = ?";
    Object parametri[]=new Object[]{entita,chiave1};

    try {
      Long conteggioDocumenti = (Long)sqlManager.getObject(select, parametri);
      if(conteggioDocumenti!= null && conteggioDocumenti.longValue()>0)
        esisteDoc= "true";
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura di WSDOCUMENTO)",e);
    }



    return esisteDoc;
  }

}
