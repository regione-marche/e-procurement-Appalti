package it.eldasoft.sil.pg.tags.funzioni;
/*
 * Created on 09/feb/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */


import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
/**
 * Funzione per caricare la lista della tipologia 
 * documentazione per la home page 
 * per home page 
 * 
 * @author Marcello Caminiti
 */
public class GetListaTipologiaDocFunction extends AbstractFunzioneTag{
	public GetListaTipologiaDocFunction() {
	    super(1, new Class[]{PageContext.class});
	  }
	
	public String function(PageContext pageContext, Object[] params)
    throws JspException {
  
		  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
		      "sqlManager", pageContext, SqlManager.class);
		
		  try {
			  String tab1cod="A1043";
			  List resultSelect = sqlManager.getListHashMap(
		        "select TAB1TIP as codice, TAB1DESC as descrizione " +
		          "from TAB1 where tab1cod = ?",
		        new Object[]{tab1cod});
		    
		    if(resultSelect != null && resultSelect.size() > 0){
		      List listaTipologiaDoc = new ArrayList(resultSelect.size());
		      for(int i=0; i < resultSelect.size(); i ++){
		        HashMap tmp = (HashMap) resultSelect.get(i);
		
		        Tabellato TipologiaDoc = new Tabellato();
		        TipologiaDoc.setTipoTabellato(((JdbcParametro) tmp.get("CODICE")).getStringValue());
		        TipologiaDoc.setDescTabellato(((JdbcParametro) tmp.get("DESCRIZIONE")).getStringValue());
		        listaTipologiaDoc.add(TipologiaDoc);
		      }
		      this.getRequest().setAttribute("listaTipologiaDoc", listaTipologiaDoc);
		    }
		  } catch (SQLException e) {
		    throw new JspException("Errore nell'estrarre la lista della tipologia " +
		          "di documentazione", e);
		  }
		
		  return null;
		}
}
