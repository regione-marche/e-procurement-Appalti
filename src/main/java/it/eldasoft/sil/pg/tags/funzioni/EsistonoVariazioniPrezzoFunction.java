/*
 * Created on 13/03/15
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

/*
 *  Viene controllato se esistono delle occorrenze in garvarpre per la lavorazione
 */
public class EsistonoVariazioniPrezzoFunction extends AbstractFunzioneTag {

  public EsistonoVariazioniPrezzoFunction() {
    super(5, new Class[] { PageContext.class, String.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];
    String contaf = (String) params[2];
    String ditta = (String) params[3];
    String ricercaNeiLotti= (String) params[4];

    String esistonoVariazioniPrezzo = "false";

    if (ngara != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

      try {

        String selectGarvarpre = "select count(*) from garvarpre where ngara = ? ";
        if("1".equals(ricercaNeiLotti))
          selectGarvarpre = "select count(*) from garvarpre, gare where garvarpre.ngara=gare.ngara and gare.codgar1 = ? and gare.ngara!=gare.codgar1 ";

        if(contaf!=null && !"".equals(contaf))
          selectGarvarpre+= "and contaf = ? ";
        Object par[];
        if(!"NoDitta".equals(ditta)){
          if(ditta!=null && !"".equals(ditta)){
            selectGarvarpre += " and dittao=?";
            if(contaf!=null && !"".equals(contaf)){
              par=new Object[3];
              par[2]=ditta;
            }else{
              par=new Object[2];
              par[1]=ditta;
            }
          }else{
            selectGarvarpre += " and dittao is null";
            if(contaf!=null && !"".equals(contaf))
              par=new Object[2];
            else
              par=new Object[1];
          }
        }else{
          par=new Object[1];
        }
        par[0]=ngara;
        if(contaf!=null && !"".equals(contaf))
          par[1]=new Long(contaf);

        Long conteggio = (Long) sqlManager.getObject(selectGarvarpre, par);

        if (conteggio != null && conteggio.longValue() > 0) {
          esistonoVariazioniPrezzo = "true";
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante il controllo dell'esistenza dello storico delle variazioni prezzo per la lavorazione: ngara=" + ngara +", contaf=" + contaf, e);
      }
    }

    return esistonoVariazioniPrezzo;
  }

}