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

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoMoney;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Calcolo del totale degli importi a base d'asta dei singoli lotti di una gara
 * Funzione accessibile dalla pagina 'Dati generali' di una gara divisa in lotti
 * 
 * @author Luca.Giacomazzo
 */
public class GetImportiComplessiviFunction extends AbstractFunzioneTag {

  public GetImportiComplessiviFunction(){
    super(1, new Class[]{String.class});
  }
  
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    String codiceGara = (String) params[0];
    
    try {
      Double importoGara = (Double) sqlManager.getObject("select IMPTOR from TORN where CODGAR = ?", new Object[]{codiceGara});
      
      if(importoGara != null){
        String importo = new GestoreCampoMoney().getValorePerVisualizzazione(importoGara == null
            ? ""  : importoGara.toString());
        pageContext.setAttribute("importoGara", importo, PageContext.REQUEST_SCOPE);
      }
      List listaImporti = sqlManager.getListVector(
          "select IMPAPP from GARE where CODGAR1 = ?",
          new Object[]{codiceGara});
      
      if(listaImporti != null && listaImporti.size() > 0){
        double totale = 0;
        
        for(int i=0; i < listaImporti.size(); i++){
          if(listaImporti.get(i) != null){
            Vector obj = (Vector)listaImporti.get(i);
            Double tmp = ((JdbcParametro) obj.get(0)).doubleValue();
            if(tmp != null)
              totale += tmp.doubleValue();
          }
        }
        if(totale != 0){
          String totaleImportiGare = new GestoreCampoMoney().getValorePerVisualizzazione("" + totale); 
          pageContext.setAttribute("totaleImportiGare", totaleImportiGare, PageContext.REQUEST_SCOPE);
        }
      }
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrazione degli importi a base " +
            "di gara dei singoli lotti della gara " + codiceGara, e);
    } catch (GestoreException e) {
      throw new JspException("Errore nel determinare il totale della somma " +
            "degli importi a base di gara dei singoli lotti della gara " +
            codiceGara, e);
    }  
    return null;
  }
  
}