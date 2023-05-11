/*
 * Created on 23/04/15
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
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.MEvalManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

public class EsisteIntegrazioneAppMEVALFunction extends AbstractFunzioneTag {

  public EsisteIntegrazioneAppMEVALFunction(){
    super(3, new Class[]{PageContext.class, String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String integrazione="0";

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        pageContext, TabellatiManager.class);
    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        pageContext, PgManagerEst1.class);

    String url = ConfigManager.getValore(MEvalManager.PROP_INTEGRAZIONE_MEVAL_URL_BACKEND);
    if(url != null && !"".equals(url)){
      String ngara = (String) GeneralTagsFunction.cast("string", params[1]);
      String codgar = (String) GeneralTagsFunction.cast("string", params[2]);
      if(ngara!=null && codgar!=null && !"".equals(ngara) && !"".equals(codgar)) {
        try {
          String gartel= (String)sqlManager.getObject("select gartel from torn where codgar=?", new String[] {codgar});
          String desctabA1153 = tabellatiManager.getDescrTabellato("A1153", "1");
          desctabA1153 = desctabA1153.substring(0,1);
          if("1".equals(gartel) || "1".equals(desctabA1153)) {
            Long modlicg=(Long)sqlManager.getObject("select modlicg from gare where ngara=?", new String[] {ngara});
            //nel caso di gara ad offerta unica si deve verificare se c'è almeno un lotto OEPV
            if(codgar.equals(ngara)) {
              Long conteggio = (Long)sqlManager.getObject("select count(*) from gare where codgar1=? and codgar1!=ngara and modlicg=?", new Object[] {codgar, new Long(6)});
              if(conteggio != null && conteggio.longValue()>0)
                modlicg= new Long(6);
            }
            if(new Long(6).equals(modlicg)) {
              String pubbli11 = pgManagerEst1.esisteBloccoPubblicazionePortale(codgar, "BANDO11", false);
              String pubbli13 = pgManagerEst1.esisteBloccoPubblicazionePortale(codgar, "BANDO13", false);
              if("TRUE".equals(pubbli11) || "TRUE".equals(pubbli13)) {
                integrazione="1";
                String obbligoPresidente = ConfigManager.getValore("appCg.obbligoPresidente");
                pageContext.setAttribute("obbligoPresidente", obbligoPresidente,
                    PageContext.REQUEST_SCOPE);
              }
            }
          }
        } catch (SQLException e) {
          throw new JspException(
              "Errore durante nei controlli per determinare se attiva l'integrazione con app M-Eval", e);
        }
      }
    }

    return integrazione;

  }

}