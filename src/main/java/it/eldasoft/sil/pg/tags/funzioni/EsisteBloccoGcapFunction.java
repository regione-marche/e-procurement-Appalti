/*
 * Created on 26-07-2012
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
 * Funzione che controlla se vi è il blocco relativo all'esistenza di occorrenze
 * in GCAP per la gara in esame
 *
 * @author Marcello Caminiti
 */
public class EsisteBloccoGcapFunction extends AbstractFunzioneTag {

  public EsisteBloccoGcapFunction() {
    super(3 , new Class[] { PageContext.class, String.class, String.class});
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String bloccoGcap= "false";
    String ngara = (String) params[1];
    String tipologia = (String) params[2];

    String select="select count(ngara) from gcap where ngara=?";

    if("OFFERTA_UNICA".equals(tipologia)){
      select="select count(gcap.ngara) from gcap,gare  where codgar1=? and gare.ngara = gcap.ngara" ;
    }

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {

      Long numOccorrenzeGcap = (Long)sqlManager.getObject(select,new Object[]{ngara});
      if(numOccorrenzeGcap!= null && numOccorrenzeGcap.longValue()>0)
        bloccoGcap = "true";


    } catch (SQLException e) {
      throw new JspException("Errore nella conteggio delle occorrenze in GCAP)",e);
    }
    return bloccoGcap;
  }

}
