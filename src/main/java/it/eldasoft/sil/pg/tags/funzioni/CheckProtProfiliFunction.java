/*
 * Created on 13/set/2018
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import net.sf.json.JSONObject;

import org.apache.xpath.functions.Function;


public class CheckProtProfiliFunction extends AbstractFunzioneTag {
  

  public CheckProtProfiliFunction() {
    super(3, new Class[] { PageContext.class, String.class });
    // TODO Auto-generated constructor stub
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String profilo = (String) params[1];
    String genereString = (String) params[2];
    Long genere = Long.parseLong(genereString);
    String config = null;
    
    if(genere == 1){
      config = "ALT.GARE.GARE.GestioneGareALotti";
    }
    if(genere == 2){
        config = "ALT.GARE.GARE.GestioneGareALottoUnico";
    }
    if(genere == 3){
        config = "ALT.GARE.GARE.GestioneGareLottiOffUnica";
    }
    
    JSONObject json = new JSONObject();

    //si prelevano i valori dalla tabela UFFSET
    Long valore = null;
    try {
      valore = (Long) sqlManager.getObject("select valore from w_proazi where cod_profilo = ? and oggetto = ?", new Object[]{profilo, config});
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    if(valore == null){
      return "true";
    }
    
    if(valore == 1){
      return "true";
    }
    return "false";
  }


}
