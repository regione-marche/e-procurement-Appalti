/*
 * Created on 11/07/17
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
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class IsRiservatezzaAttivaFunction extends AbstractFunzioneTag {

  public IsRiservatezzaAttivaFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String chiave = (String) params[1];
    String idconfi = (String) params[2];
    String esito = "0";

    GestioneWSDMManager gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        pageContext, GestioneWSDMManager.class);

    String riservatezzaAttiva = ConfigManager.getValore("wsdm.applicaRiservatezza." + idconfi);

    if (!"".equals(chiave) && chiave != null) {

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      try {
        String riserva;
        HashMap<String, Object> hm = gestioneWSDMManager.getGenereCodiceGara(chiave);
        Long genere = (Long) hm.get("genereGara");
        if(genere != null && (genere.intValue() == 10 || genere.intValue() == 11 || genere.intValue() == 20)){
          esito = "0";
        }
        else{
          WSDMConfigurazioneOutType conf = gestioneWSDMManager.wsdmConfigurazioneLeggi(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO,idconfi);
          String tipoWSDM = conf.getRemotewsdm();
          //se il tipo del sistema WSDM non è JIRIDE non viene mai gestita la riservatezza
          if(!"JIRIDE".equals(tipoWSDM) || !"1".equals(riservatezzaAttiva)){
            return esito;
          }

          Vector<JdbcParametro> datiGara = sqlManager.getVector("select id,isriserva from wsfascicolo where (entita='GARE' or entita='TORN') and key1=?", new Object[]{chiave});
          if(datiGara!=null && datiGara.size()>0){
            riserva = (String) (datiGara.get(1)).getValue();
            if(riserva == null || riserva.length() == 0){
              esito = "0";
            }else{
              if(riserva.equals("1")){esito = "1";}
            }
          }else{
            esito = "null";
          }
        }
      } catch (SQLException e) {
        throw new JspException("Errore nel controllo riservatezza attiva", e);
      } catch (GestoreException e) {
        return esito;
      }
    }
    return esito;
  }

}