/*
 * Created on 07/dic/08
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
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.springframework.transaction.TransactionStatus;

/**
 * Calcolo del numero protocollo Funzione accessibile dalla scheda Ricezione
 * Plichi
 * 
 * @author Marcello Caminiti
 */
public class GetNumeroProtocolloFunction extends AbstractFunzioneTag {

  public GetNumeroProtocolloFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
        "tabellatiManager", pageContext, TabellatiManager.class);

    //Calcola il numero protocollo solo se si è nel profilo Protocollo
    Boolean isProfiloProtocollo = new Boolean(false); 
    if (UtilityTags.checkProtection(pageContext, "FUNZ.VIS.ALT.GARE.V_GARE_NSCAD-lista.ApriGare",true))
      isProfiloProtocollo=new Boolean(true);

    String tipscad = (String) params[1];
    String ngara = (String) params[2];

    TransactionStatus status = null;

    String numProtocollo = "";

    try {
      String tab1cod = "A1044";
      String tab1tip = "1";
      
      if (isProfiloProtocollo.booleanValue()){
        synchronized (GetNumeroProtocolloFunction.class) {
          String tab1desc = tabellatiManager.getDescrTabellato(tab1cod, tab1tip);
  
          if (tab1desc != null) {
            long contatore = Long.parseLong(tab1desc) + 1;
    
            numProtocollo = Long.toString(contatore);
            numProtocollo = UtilityStringhe.fillLeft(numProtocollo, '0', 7);
            status = sqlManager.startTransaction();
            sqlManager.update(
                "update tab1 set tab1desc = ? where tab1cod = ? and tab1tip = ?",
                new Object[] { numProtocollo, tab1cod, tab1tip });
            sqlManager.commitTransaction(status);
    
            switch (Integer.parseInt(tipscad)) {
            case 1:
              numProtocollo = numProtocollo + "-P";
              break;
            case 2:
              numProtocollo = numProtocollo + "-O";
              break;
            case 3:
              numProtocollo = numProtocollo + "-D";
              break;
            }
          }
        }
      }

      // Vengono determinati la data e l'ora correnti
      Date date = new Date();
      String orario = "";

      pageContext.setAttribute("dataAttuale", UtilityDate.convertiData(date,
          UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);

      GregorianCalendar ga = new GregorianCalendar();
      orario = "" + UtilityStringhe.fillLeft("" + ga.get(Calendar.HOUR_OF_DAY),'0', 2) + ":" + UtilityStringhe.fillLeft("" + ga.get(Calendar.MINUTE), '0', 2);
      pageContext.setAttribute("oraAttuale", orario, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException(
          "Errore nella lettura determinazione del numero protocollo ", e);
    }
    return numProtocollo;
  }

}