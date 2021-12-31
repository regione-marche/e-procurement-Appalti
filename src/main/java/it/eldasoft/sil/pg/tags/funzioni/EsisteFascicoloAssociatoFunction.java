/*
 * Created on 04-04-2016
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
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che controlla se esiste un fascicolo associato, andando a controllare se
 * da configurazione è prevista la fascicolazione
 *
 * @author Marcello Caminiti
 */
public class EsisteFascicoloAssociatoFunction extends AbstractFunzioneTag {

  public EsisteFascicoloAssociatoFunction() {
    super(4 , new Class[] { PageContext.class, String.class,String.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String esisteFascicolo= "false";
    String entita = (String) params[1];
    String chiave1 = (String) params[2];
    String idconfi = (String) params[3];

    String valore = ConfigManager.getValore("pg.wsdm.applicaFascicolazione."+idconfi);
    

    if("1".equals(valore)){

      String select="select count(id) from wsfascicolo where entita = ? and key1 = ?";
      Object parametri[]=new Object[]{entita,chiave1};
   
      try {
        Long conteggioFascicoli = (Long)sqlManager.getObject(select, parametri);
        if(conteggioFascicoli!= null && conteggioFascicoli.longValue()>0)
          esisteFascicolo= "true";
      } catch (SQLException e) {
        throw new JspException("Errore nella conteggio del numero di lotti della gara)",e);
      }

    }else
      esisteFascicolo = "true";

    return esisteFascicolo;
  }

}
