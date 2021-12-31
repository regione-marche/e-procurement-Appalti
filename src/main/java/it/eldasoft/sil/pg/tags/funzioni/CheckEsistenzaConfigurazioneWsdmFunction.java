/*
 * Created on 12/giu/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.util.List;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;


public class CheckEsistenzaConfigurazioneWsdmFunction extends AbstractFunzioneTag {

  public CheckEsistenzaConfigurazioneWsdmFunction() {
    super(2, new Class[]{PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    boolean trovato = false;
    String parameter = (String) params[1];
    String sql = "select valore from wsdmconfipro, wsdmconfi where wsdmconfi.id = wsdmconfipro.idconfi and wsdmconfi.codapp = 'PG' and (wsdmconfipro.chiave = 'wsdmconfigurazione.fascicoloprotocollo.url' or wsdmconfipro.chiave = 'wsdmconfigurazione.documentale.url')";
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    GestioneWSDMManager wsdmManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager", pageContext, GestioneWSDMManager.class);
    try {
      List<?> configurazioni = sqlManager.getListVector(sql, new Object[]{});
      if (configurazioni != null && configurazioni.size() > 0) {
        String url=null;
        for (int i=0;i<configurazioni.size() && !trovato;i++) {
          url = SqlManager.getValueFromVectorParam(configurazioni.get(i), 0).stringValue();
          if(url!=null && !"".equals(url)){
            try {
            WSDMConfigurazioneOutType configurazione = wsdmManager.getWsdmConfigurazione(url);
              if(configurazione!=null){
                String sistemaRemoto = configurazione.getRemotewsdm();
                if(parameter.equals(sistemaRemoto)){
                trovato = true;
                }
              }
            } catch (GestoreException e) {
              //
            }
          }
        }
      }
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura delle configurazioni WSDM; " + e);
    } catch (GestoreException e) {
      throw new JspException("Errore nella lettura delle configurazioni WSDM; " + e);
    }
    if(trovato){
      return "SI";
    }else{
      return "NO";
    }
  }
  
}
