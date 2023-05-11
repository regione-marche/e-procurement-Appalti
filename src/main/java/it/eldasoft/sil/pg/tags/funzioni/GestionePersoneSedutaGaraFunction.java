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

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione per inizializzare le sezioni delle persone presenti alla seduta di 
 * gara a lotto unico o di un lotto di gara in fase di modifica
 * 
 * @author Luca.Giacomazzo
 */
public class GestionePersoneSedutaGaraFunction extends AbstractFunzioneTag {

  public GestionePersoneSedutaGaraFunction() {
    super(1, new Class[] { String.class });
  }
  
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    // Precarico le occorrenze della PERP solo se si accede alla scheda di una
    // seduta di gara in visualizzazione e in modifica
    if(! UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(
        UtilityTags.getParametro(pageContext,
            UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))){
      String[] chiave = ((String) params[0]).split(";");
      
      String nGara, numeroSeduta = null;
      if(chiave[0].indexOf("NGARA") >= 0){
        nGara = chiave[0].substring(chiave[0].indexOf(":")+1);
        numeroSeduta = chiave[1].substring(chiave[1].indexOf(":")+1);
      } else {
        nGara = chiave[1].substring(chiave[1].indexOf(":")+1);
        numeroSeduta = chiave[0].substring(chiave[0].indexOf(":")+1);
      }
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);
      try {
        List listaPersonePresenti = sqlManager.getListVector(
            "select NGARA, NUMPER, NOMPER, CODIMP, NOMIMP, DESPER, NUMSED " +
              "from PERP where NGARA = ? and NUMSED = ?",
              new Object[]{nGara, new Long(numeroSeduta)});
  
        if (listaPersonePresenti != null && listaPersonePresenti.size() > 0)
          pageContext.setAttribute("personePresenti", listaPersonePresenti,
              PageContext.REQUEST_SCOPE);
      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre le persone presenti " +
              "alla seduta della gara " + nGara, e);
      }
    }
    return null;
  }

}