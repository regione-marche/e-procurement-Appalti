/*
 * Created on 04/09/2018
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
 * La funzione verifica se sono valorizzati tutti i prezzi unitari offerti della ditta
 *
 * @author Marcello Caminiti
 */
public class ControlloValorizzazionePrezziOffertiDittaFunction extends AbstractFunzioneTag {

  public ControlloValorizzazionePrezziOffertiDittaFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ngara = (String) params[1];
    String codiceDitta = (String) params[2];
    String isGaraLottiConOffertaUnica = (String) params[3];

    String esito = "OK";
    try {
    	if (ngara != null && !"".equals(ngara)){
    		String selectGcap="";
    		if(!"1".equals(isGaraLottiConOffertaUnica)){
    		  selectGcap="select contaf, ngara from gcap where ngara = ? and dittao is null and (sogrib is null or sogrib='2')";
    		}else{
    		  selectGcap="select p.contaf,p.ngara from gcap p,gare g where g.codgar1=? and g.ngara=p.ngara and g.codgar1!=g.ngara and p.dittao is null and (p.sogrib is null or p.sogrib='2')";
    		}

    		esito = this.controlloDPRE(selectGcap, ngara, codiceDitta, sqlManager);

		    if("OK".equals(esito)){
		      if(!"1".equals(isGaraLottiConOffertaUnica)){
		        selectGcap="select contaf,ngara from gcap where ngara = ? and dittao = '" + codiceDitta + "' and (sogrib is null or sogrib='2')";
		      }else{
		        selectGcap="select p.contaf,p.ngara from gcap p,gare g where g.codgar1=? and g.ngara=p.ngara and g.codgar1!=g.ngara and p.dittao = '" + codiceDitta + "' and (p.sogrib is null or p.sogrib='2')";
		      }
		      esito = this.controlloDPRE(selectGcap, ngara, codiceDitta, sqlManager);
		      if("NoOccorrenze".equals(esito))
		        //Se non vi sono occorrenze in GCAP non vi sono controlli da fare
	            esito="OK";
		    }else if("NoOccorrenze".equals(esito)){
		      //Se non vi sono occorrenze in GCAP non vi sono controlli da fare
		      esito="OK";
		    }
    	}

    }catch (SQLException e) {
        throw new JspException(
                "Errore durante la lettura delle offerte delle ditte ", e);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore durante la lettura delle offerte delle ditte ", e);
    }

    return esito;
   }

  private String controlloDPRE(String selectGcap,String ngara, String codiceDitta, SqlManager sqlManager) throws SQLException, GestoreException{
    String esito="OK";
    List listaOccorrenze = sqlManager.getListVector(selectGcap, new Object[]{ngara});
    if(listaOccorrenze!=null && listaOccorrenze.size()>0){
      Long contaf=null;
      String codiceLotto=null;
      String selectDpre="select count(contaf) from dpre where ngara = ? and contaf=? and dittao =? and preoff is not null";
      Long conteggio=null;
      for(int i=0;i<listaOccorrenze.size();i++){
        contaf=SqlManager.getValueFromVectorParam(listaOccorrenze.get(i), 0).longValue();
        codiceLotto = SqlManager.getValueFromVectorParam(listaOccorrenze.get(i), 1).stringValue();
        conteggio=(Long)sqlManager.getObject(selectDpre, new Object[]{codiceLotto,contaf,codiceDitta});
        if(conteggio==null || new Long(0).equals(conteggio)){
          esito="NOK";
          break;
        }
      }
    }else{
      esito="NoOccorrenze";
    }
    return esito;
  }

}