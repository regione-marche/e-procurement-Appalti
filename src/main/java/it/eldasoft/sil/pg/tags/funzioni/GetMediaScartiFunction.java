/*
 * Created on 06-03-2013
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che calcola la media degli scarti: GARE.LIMMAX - GARE.MEDIA
 *
 * @author Marcello Caminiti
 */
public class GetMediaScartiFunction extends AbstractFunzioneTag {

  public GetMediaScartiFunction() {
    super(2, new Class[] {PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    // Codice della gara
    String ngara = (String) params[1];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      Double limmax=null;
      Double media = null;

      Vector datiGara = sqlManager.getVector(
          "select limmax,media from gare where ngara = ? ", new Object[]{ngara});

      if(datiGara!=null && datiGara.size()>0){
        Double mediaScarti = null;
        if (((JdbcParametro) datiGara.get(0)).getValue() != null)
          limmax = ((JdbcParametro) datiGara.get(0)).doubleValue();

        if (((JdbcParametro) datiGara.get(1)).getValue() != null)
          media = ((JdbcParametro) datiGara.get(1)).doubleValue();

        if(media==null || limmax==null)
          media =null;
        else if(limmax!= null && limmax.doubleValue()!=0){
          if(media== null)
            media = new Double(0);
          mediaScarti = new Double(limmax.doubleValue() - media.doubleValue());
        }else
          mediaScarti = new Double(0);

        pageContext.setAttribute("mediaScarti",
            mediaScarti, PageContext.REQUEST_SCOPE);
      }

    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura dei campi LIMMAX e MEDIA della gara " + ngara, e);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore durante la lettura dei campi LIMMAX e MEDIA della gara " + ngara, e);
    }

    return null;
  }
}
