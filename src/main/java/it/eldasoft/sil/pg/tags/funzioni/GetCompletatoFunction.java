/*
 * Created on 06/09/13
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
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che estrae il valore di ANTICOR.COMPLETATO a partire
 * dall'id del lotto
 *
 * @author Marcello Caminiti
 */
public class GetCompletatoFunction extends AbstractFunzioneTag {

  public GetCompletatoFunction() {
    super(2, new Class[] { PageContext.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String chiave = (String) GeneralTagsFunction.cast("string", params[1]);
    String select="";
    String ret="";
    Long idAnticorLotti=null;
    if(chiave.indexOf("ANTICORLOTTI")==0){
      String id = chiave.substring(chiave.indexOf(':')+1);
      idAnticorLotti = new Long(id);
    }else if(chiave.indexOf("ANTICORPARTECIP")==0){
      String idPartecip = chiave.substring(chiave.indexOf(':')+1);
      select="select idanticorlotti from anticorpartecip where id=?";
      Long id=null;
      try {
        id=(Long)sqlManager.getObject(select,new Object[]{new Long(idPartecip)});
        idAnticorLotti = new Long(id);
      } catch (NumberFormatException e) {
        throw new JspException(
            "Errore durante la lettura del campo ANTICORPARTECIP.IDANTICORLOTTI ", e);
      } catch (SQLException e) {
        throw new JspException(
            "Errore durante la lettura del campo ANTICORPARTECIP.IDANTICORLOTTI", e);
      }

    }

    select="select completato from anticor a,anticorlotti b where a.id=b.idanticor and b.id=?";

    try {
      String completato = (String)sqlManager.getObject(select, new Object[]{new Long(idAnticorLotti)});
      ret=completato;
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura del campo ANTICOR.COMPLETATO ", e);
    }


    return ret;

  }

}
