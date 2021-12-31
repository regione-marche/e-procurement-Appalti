/*
 * Created on 16/01/15
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
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Verifica che il profilo attivo sia RDO andando a leggere i tabellati
 * A1z03 e A1z04, sfruttando il fatto che per il profilo RDO iterga =6
 *
 * @author Marcello Caminiti
 */
public class IsProfiloRDOFunction extends AbstractFunzioneTag {

  public IsProfiloRDOFunction(){
    super(1, new Class[]{PageContext.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String profiloAttivo = (String) pageContext.getSession().getAttribute("profiloAttivo");
    String ret="false";

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        pageContext, PgManager.class);

    if(profiloAttivo!=null){
      try {
        String tipoProcedura = (String)sqlManager.getObject("select tab2d2 from tab2 where tab2cod=? and tab2d1=?",
            new Object[] { "A1z03", profiloAttivo });
        if (tipoProcedura != null && !"".equals(tipoProcedura)) {
          if (tipoProcedura.indexOf(",") > 0) {
            String vettValori[] = tipoProcedura.split(",");
            ret = "true";
            for(int j=0; j < vettValori.length; j++) {
              String desc1 = vettValori[j];
              Long iterga = pgManager.getITERGA(new Long(desc1));
              if (iterga.longValue() != 6) {
                ret = "false";
                break;
              }
            }
          } else {
            Long iterga = pgManager.getITERGA(new Long(tipoProcedura));
            if (iterga.longValue() == 6)
              ret = "true";
          }
        }
      } catch (SQLException e) {
        throw new JspException(
            "Errore durante la lettura della tabella TAB per determinare se il profilo è RDO ", e);
      }
    }

    return ret;
  }

}