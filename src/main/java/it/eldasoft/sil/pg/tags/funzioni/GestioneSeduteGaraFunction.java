/*
 * Created on 31/05/13
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
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione per caricare nella pagina delle sedute di gare i valori di
 * TORN.ITERGa e GARE.CRITLICG (TORN.CRITLIC se gara ad offerta unica)
 * @author Marcello Caminiti
 */
public class GestioneSeduteGaraFunction extends AbstractFunzioneTag {

  public GestioneSeduteGaraFunction() {
    super(3, new Class[] { PageContext.class,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String genereGara = StringUtils.stripToNull((String) params[1]);
    String valoreChiave = (String) params[2];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String select="select iterga, critlicg, compreq,g1.valtec,nobustamm from torn,gare g,gare1 g1  where g.ngara=? and g.codgar1=codgar and g1.ngara=g.ngara";
    if(genereGara!=null && "3".equals(genereGara))
      select="select iterga, critlic, compreq, valtec,nobustamm from torn where codgar=?";

    try {
      @SuppressWarnings("unchecked")
      Vector<JdbcParametro> datiGara = sqlManager.getVector(select, new Object[] { valoreChiave });

      if (datiGara != null && datiGara.size() > 0){
        Long iterga = (Long) (datiGara.get(0)).getValue();
        Long critlic = (Long) (datiGara.get(1)).getValue();
        String compreq = (String)(datiGara.get(2)).getValue();
        String valtec = (String)(datiGara.get(3)).getValue();
        String nobustamm = (String)(datiGara.get(4)).getValue();
        pageContext.setAttribute("iterga", iterga,
            PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("critlic", critlic,
            PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("compreq", compreq,
            PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("valtec", valtec,
            PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("nobustamm", nobustamm,
            PageContext.REQUEST_SCOPE);
      }
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre dell'iterga e modlic "
          + "della gara "
          + valoreChiave, e);
    }

    return null;
  }

}
