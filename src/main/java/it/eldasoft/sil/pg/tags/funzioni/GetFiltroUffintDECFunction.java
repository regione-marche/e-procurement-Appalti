/*
 * Created on 01/10/15
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
 * Se TORN.ALTRISOG=2 viene prelevato il valore di GARALTSOG.CENINT
 *
 * @author Marcello Caminiti
 */
public class GetFiltroUffintDECFunction extends AbstractFunzioneTag {

  public GetFiltroUffintDECFunction(){
    super(2, new Class[]{PageContext.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    //Se gara a lotto unico con modcont!=1 non si deve fare nulla
    String ngara = (String)params[1];
    String select="select altrisog from torn,gare where ngara = ? and codgar=codgar1";
    String ret = null;
    boolean eseguireControllo=true;
    try {
      Long genere = (Long)sqlManager.getObject("select v.genere from v_gare_torn v, gare g where g.ngara=? and v.codgar=g.codgar1", new Object[]{ngara});
      if(new Long(3).equals(genere)){
        Long modcont = (Long)sqlManager.getObject("select modcont from torn, gare where ngara=? and codgar=codgar1", new Object[]{ngara});
        if(!new Long(1).equals(modcont)){
          eseguireControllo=false;
          ret="NO_FILTRO_UFFINT";
        }
      }
      if(eseguireControllo){
        Long altrisog = (Long)sqlManager.getObject(select, new Object[]{ngara});
        if(altrisog!=null && altrisog.longValue()==2){
          ret = (String)sqlManager.getObject("select cenint from garaltsog where ngara=?", new Object[]{ngara});
        }
      }

    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura della stazione appaltante ", e);
    }

    return ret;
  }

}