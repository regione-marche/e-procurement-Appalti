/*
 * Created on 24/02/2021
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
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Determina la situazione dell'assegnamento Numero Ordine Invito:
 * 0 = nessuna ditta con numero ordine invito
 * 1 = tutte le ditte in gara con numero ordine invito
 * 2 = numero ditte con numero ordine invito < numero ditte in gara
 *
 * @author C.F.
 */

public class GetSituazioneNumOrdineInvitoFunction extends AbstractFunzioneTag {

  public GetSituazioneNumOrdineInvitoFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String codiceGara = (String) params[1];
    String ngara = (String) params[2];

    String situazioneNOI = null;

    if (ngara != null) {
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

      try {
    	  List listaDitteInGara = sqlManager.getListVector( "select dittao,numordinv from ditg where codgar5 = ? and ngara5 = ? ",new Object[]{codiceGara,ngara});
    	  int numDitteInGara =listaDitteInGara.size();
    	  int numDitteOrdInv = 0;
    	  for(int i=0;i<listaDitteInGara.size();i++){
              String codiceDitta = SqlManager.getValueFromVectorParam(listaDitteInGara.get(i), 0).stringValue();
              Long numOrdInv = SqlManager.getValueFromVectorParam(listaDitteInGara.get(i), 1).longValue();
              if(numOrdInv!=null) {
            	  numDitteOrdInv++;
              }
    	  }
    	  
    	  if(numDitteOrdInv == 0) {
    		  situazioneNOI = "0";
    	  }
    	  if(numDitteOrdInv > 0 && numDitteOrdInv< numDitteInGara ) {
    		  situazioneNOI = "2";
    	  }
    	  if(numDitteOrdInv > 0 && numDitteOrdInv == numDitteInGara ) {
    		  situazioneNOI = "1";
    	  }


      } catch (SQLException e) {
    	  throw new JspException("Errore durante il controllo del numero ordine invito", e);
      } catch (GestoreException e) {
    	  throw new JspException("Errore durante il controllo del numero ordine invito", e);    	  
      }
    }

    return situazioneNOI;
  }

}