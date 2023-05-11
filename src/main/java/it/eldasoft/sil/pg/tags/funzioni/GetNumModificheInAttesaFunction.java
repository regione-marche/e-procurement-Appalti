package it.eldasoft.sil.pg.tags.funzioni;
/*
 * Created on 21/11/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */


import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
/**
 * Funzione per calcolare il numero di rinnovi da verificare
 * per elenchi/cataloghi
 *
 * @author Marcello Caminiti
 */
public class GetNumModificheInAttesaFunction extends AbstractFunzioneTag{
	public GetNumModificheInAttesaFunction() {
	    super(4, new Class[]{PageContext.class, String.class, String.class, String.class});
	  }

	@Override
  public String function(PageContext pageContext, Object[] params)
    throws JspException {


		  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
		      "sqlManager", pageContext, SqlManager.class);

		  String entita = (String)GeneralTagsFunction.cast("string", params[3]);
		  String messaggio="{0} operatori ";
		  String messaggioRinnovoSingolo="{0} operatore ";
		  if("V_GARE_ELEDITTE".equals(entita)){
		    messaggio+=" per l'elenco {1}";
		    messaggioRinnovoSingolo +=" per l'elenco {1}";
		  }else{
		    messaggio+=" per il catalogo {1}";
            messaggioRinnovoSingolo +=" per il catalogo {1}";
		  }

		  List lista = null;
          List<Object[]> ret = new Vector<Object[]>();
		  String filtroLivelloUtente = (String)GeneralTagsFunction.cast("string", params[1]);
		  String codUffint = (String)GeneralTagsFunction.cast("string", params[2]);
		  String select="select g.ngara, count(id) from " + entita + ", garacquisiz g where " + entita + ".codice=g.ngara and g.stato = 1 "
		  		/*+ and tipologia<>3 and valiscr>0" +*/
		   + " and exists (select dittao from ditg where ngara5=g.ngara)";
		  if(filtroLivelloUtente!=null && !"".equals(filtroLivelloUtente))
		    select+= " and " + filtroLivelloUtente;
		  if(codUffint!=null && !"".equals(codUffint))
		    select+= " and exists (select codgar from torn where codgar = " + entita + ".codgar and cenint = '" + codUffint + "')";
		  select+=" group by g.ngara";
          try {
            lista = sqlManager.getListVector(select,null);
            if(lista!=null && lista.size()>0){
              String codice=null;
              Long numeroDitte = null;
              String msg = null;
              for(int i=0; i< lista.size(); i++ ){
                codice = SqlManager.getValueFromVectorParam(lista.get(i), 0).getStringValue();
                numeroDitte = (Long) SqlManager.getValueFromVectorParam(lista.get(i), 1).getValue();
                if(numeroDitte > 1){
                  msg = messaggio.replace("{0}", numeroDitte.toString());
                  msg = msg.replace("{1}", codice);
                }else{
                  msg = messaggioRinnovoSingolo.replace("{0}", numeroDitte.toString());
                  msg = msg.replace("{1}", codice);
                }
                ret.add(((new Object[] {msg, codice})));
              }
              pageContext.setAttribute("listaModificheInAttesa", ret, PageContext.REQUEST_SCOPE);
            }
		  } catch (SQLException e) {
		    throw new JspException("Errore nel conteggio delle richieste di aggiornamento categorie d'iscrizione per ogni elenco/catalogo", e);
		  }
          return null;
		}
}
