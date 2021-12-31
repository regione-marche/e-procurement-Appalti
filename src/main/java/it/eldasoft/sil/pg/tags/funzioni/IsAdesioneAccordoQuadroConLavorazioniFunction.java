/*
 * Created on 13-10-2016
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
 * La funzione verifica l'esistenza di un accordo quadro con Lavorazioni associate
 *
 * @author Marcello Caminiti
 */
public class IsAdesioneAccordoQuadroConLavorazioniFunction extends AbstractFunzioneTag {

  public IsAdesioneAccordoQuadroConLavorazioniFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String result = "false";
    String ngara = (String) params[1];

    if (ngara != null && !"".equals(ngara)) {


      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);

      try {
        String ngaraaq = (String)sqlManager.getObject("select NGARAAQ  from torn,gare where codgar1=codgar and ngara =?", new Object[]{ngara});
        if(ngaraaq != null && !"".equals(ngaraaq)){
          pageContext.setAttribute("ngaraaq", ngaraaq, PageContext.REQUEST_SCOPE);
          Long modlicg = (Long)sqlManager.getObject("select MODLICG from GARE where NGARA = ?", new Object[]{ngaraaq});
          if(modlicg!= null && (modlicg.longValue() == 5 || modlicg.longValue()== 14 || modlicg.longValue() == 6)){
            Long conteggio = (Long)sqlManager.getObject("select count(ngara) from gcap where ngara =?", new Object[]{ngaraaq});
            if(conteggio!=null && conteggio.longValue()>0){
              result = "true";
            }
          }
        }

      }catch (SQLException s) {
        throw new JspException("Errore nella verifica che la gara sia un affidamento in adesione a un accordo quadro con unico operatore per cui sono state definite lavorazioni e forniture", s);
      }
    }
    return result;
  }

}