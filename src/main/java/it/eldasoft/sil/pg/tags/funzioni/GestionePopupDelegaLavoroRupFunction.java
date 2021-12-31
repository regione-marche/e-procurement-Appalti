/*
 * Created on 19/05/2011
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
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Classe per i controlli preliminari da effettuare per la popup
 * delega lavoro al RUP
 *
 * @author Marcello.Caminiti
 */
public class GestionePopupDelegaLavoroRupFunction extends
		AbstractFunzioneTag {

	public GestionePopupDelegaLavoroRupFunction(){
		super(3, new Class[]{PageContext.class,String.class,String.class });
	}

	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ret="NO";

    String codrup=(String)params[1];
    String clavor = (String)params[2];

    String select="select syscon from tecni where codtec = ?";

    try {
      Long syscon = (Long)sqlManager.getObject(select, new Object[]{codrup});

      if (syscon == null){
        pageContext.setAttribute("controlloRUPSuperato", "NO",
            PageContext.REQUEST_SCOPE);
      }else{
        select="select count(numper) from g_permessi where syscon = ? and codlav= ?";
        Long count = (Long)sqlManager.getObject(select, new Object[]{syscon,clavor});
        if(count!= null && count.longValue()>0)
          ret="SI";
      }

      //Controllo sui tecnici interni
      String  tecniciTuttiDelegati = "SI";
      List listaTecniciInterni = sqlManager.getListVector("select distinct(codtec) from g2tecn where codlav=? and inttec=?",
          new Object[]{clavor, "1"});
      if(listaTecniciInterni!=null && listaTecniciInterni.size()>0){
        select="select syscon from tecni where codtec = ?";
        String selectConteggio="select count(numper) from g_permessi where syscon = ? and codlav= ?";
        for(int i=0;i<listaTecniciInterni.size();i++){
          String codtec = SqlManager.getValueFromVectorParam(listaTecniciInterni.get(i), 0).getStringValue();
          syscon = (Long)sqlManager.getObject(select, new Object[]{codtec});
          if (syscon != null){
            Long count = (Long)sqlManager.getObject(selectConteggio, new Object[]{syscon,clavor});
            if(count== null || ( count!= null && count.longValue()==0)){
              tecniciTuttiDelegati= "NO";
              break;
            }

          }
        }
      }
      pageContext.setAttribute("tecniciTuttiDelegati", tecniciTuttiDelegati,
          PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
      throw new JspException("Errore nei controlli preliminari per effettuare la delega lavoro al RUP ", e);
    }
    return ret;
	}

}