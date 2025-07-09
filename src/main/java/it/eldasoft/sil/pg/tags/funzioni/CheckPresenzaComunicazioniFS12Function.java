/*
 * Created on 07-06-2013
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
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;
import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.sql.SQLException;

/**
 * Function che verifica se la categoria viene usata in entità di GENE, GARE e PL.
 *
 * @author Manuel Bridda
 */
public class CheckPresenzaComunicazioniFS12Function extends AbstractFunzioneTag {

  public CheckPresenzaComunicazioniFS12Function() {
    super(5, new Class[] {PageContext.class, String.class,String.class  });
  }

  /**
   * Si controlla se ci sono comunicazioni di rettifica per la ditta e la gara
   *
   * @return "true" se esiste almeno una comunicazione, false altrimenti
   */
  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager", pageContext, PgManager.class);
    String codgar = (String) params[1];
    String ngara = (String) params[2];
    String dittao = (String) params[3];
    String stepWizard = (String) params[4];
    Long step;

    try {
      step = new Long(stepWizard);
    } catch (NumberFormatException e){
      step=0L;
    }

    if(step<5)
      return "false";

    String commodello = "10,13";
    if(step>5){
      commodello += ",15,18";
    }
    if(StringUtils.isNotBlank(codgar) && codgar.startsWith("$"))
      codgar=codgar.substring(1);

    Long count = 0L;
    try{

      //per la gare con idanonimo devo recuperare l'impresa in chiaro per recuperare la comunicazione di rettifica
      String idanonimo = (String) sqlManager.getObject("select idanonimo from ditg where ngara5=? and dittao=?",new Object[] {ngara,dittao});
      if(StringUtils.isNotBlank(idanonimo)) {
        try {
          String iddecodificato = pgManager.decodifica(idanonimo);
          if (iddecodificato.indexOf("#") > -1) {
            String[] token = iddecodificato.split("#");
            dittao = token[token.length - 1];
          }
        } catch (Exception e) {
          throw new JspException("Errore nella decifratura dell'idanonimo", e);
        }
      }
      Long tipimp = (Long) sqlManager.getObject("select tipimp from impr where codimp=?", new Object[]{dittao});
      if(tipimp != null && (tipimp.longValue()==3 || tipimp.longValue()==10)) {
        dittao = (String) sqlManager.getObject("select coddic from ragimp where codime9=? and impman=?", new Object[]{dittao, "1"});
      }
      count = (Long) sqlManager.getObject("select count(*) from  w_invcom, w_puser where commodello in ("+commodello+") " +
              "and comkey2=? and (comkey3 is null or comkey3=?) and comkey1=usernome and userkey1=?",
              new Object[]{codgar,ngara,dittao});
      if (count != null && count > 0)
         return "true";
    } catch (SQLException e) {
      throw new JspException("Errore nel conteggio delle comunicazioni di rettifica ",e);
    }

    return "false";
  }

}
