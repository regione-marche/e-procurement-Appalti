/*
 * Created on 04-05-2018
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
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Funzione che data una ditta verifica lo stato della verifica  art. 80
 *
 * @author Marcello Caminiti
 */
public class CheckStatoArt80Function extends AbstractFunzioneTag {

  public CheckStatoArt80Function() {
    super(2, new Class[] {PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String stato=null;
    String codimp = (String) params[1];
    codimp = UtilityStringhe.convertiNullInStringaVuota(codimp);

    String codein=(String) this.getRequest().getSession().getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
    boolean art80Gateway=false;
    if(codein!=null && !"".equals(codein)  && "1".equals(ConfigManager.getValore("art80.ws.url.gateway")) && "1".equals(ConfigManager.getValore("art80.gateway.multiuffint")))
      art80Gateway=true;

    if(!"".equals(codimp)){
      Object par[] = null;
      String selectArt80Stato = "select art80_stato from impr where codimp=?";
      if(art80Gateway){
        selectArt80Stato = "select stato from art80 where codimp=? and codein=?";
        par= new Object[]{codimp,codein};
      }else{
        par= new Object[]{codimp};
      }

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

      try {
        Long art80Stato = (Long)sqlManager.getObject(selectArt80Stato, par);
        if(art80Stato!=null){
          switch(art80Stato.intValue()){
          case 1: case 10:
            stato="In lavorazione";
            break;
          case -1:
            stato="Anomalo";
            break;
          case 2:
            stato="Non anomalo";
            break;
          }
        }
      } catch (SQLException e) {
        throw new JspException("Errore nella lettura del campo ART80_STATO di IMPR", e);
      }
    }
    return stato;
  }
}
