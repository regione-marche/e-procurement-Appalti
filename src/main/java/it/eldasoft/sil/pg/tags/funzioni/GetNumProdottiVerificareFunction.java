package it.eldasoft.sil.pg.tags.funzioni;
/*
 * Created on 26/03/14
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
 * Funzione per calcolare il numero di richieste di prodotti da verificare
 * per il mercato elettronico
 *
 * @author Marcello Caminiti
 */
public class GetNumProdottiVerificareFunction extends AbstractFunzioneTag{
	public GetNumProdottiVerificareFunction() {
	    super(3, new Class[]{PageContext.class, String.class, String.class});
	  }

	@Override
  public String function(PageContext pageContext, Object[] params)
    throws JspException {


		  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
		      "sqlManager", pageContext, SqlManager.class);

		  String messaggio="{0} prodotti per il catalogo {1}";
		  String messaggioProdottoSingolo="{0} prodotto per il catalogo {1}";

		  List listaCataloghi = null;
          List<Object[]> ret = new Vector<Object[]>();
		  String filtroLivelloUtente = (String)GeneralTagsFunction.cast("string", params[1]);
		  String codUffint = (String)GeneralTagsFunction.cast("string", params[2]);
		  String select="select codice from v_gare_catalditte ";
		  if(filtroLivelloUtente!=null && !"".equals(filtroLivelloUtente))
		    select+=" where " + filtroLivelloUtente;
		  if(codUffint!=null && !"".equals(codUffint)){
		    if(filtroLivelloUtente!=null && !"".equals(filtroLivelloUtente))
		      select+=" and";
		    else
		      select+=" where";
		    select+=" exists (select codgar from torn where codgar = V_GARE_CATALDITTE.CODGAR and CENINT = '" + codUffint + "')";
		  }
          try {
            listaCataloghi = sqlManager.getListVector(select,null);
            if(listaCataloghi!=null && listaCataloghi.size()>0){
              String codiceCatalogo=null;
              Long numRichieste=null;
              String msg = null;
              for(int i=0; i< listaCataloghi.size(); i++ ){
                codiceCatalogo = SqlManager.getValueFromVectorParam(listaCataloghi.get(i), 0).getStringValue();
                numRichieste = (Long)sqlManager.getObject("select count(id) from meiscrizprod where codgar=? and ngara=? and stato=?",
                    new Object[]{"$" + codiceCatalogo, codiceCatalogo, new Long(2)});
                if(numRichieste!=null && numRichieste.longValue()>1){
                  msg = messaggio.replace("{0}", numRichieste.toString());
                  msg = msg.replace("{1}", codiceCatalogo);
                  ret.add(((new Object[] {msg, codiceCatalogo})));
                }if(numRichieste!=null && numRichieste.longValue()==1){
                  msg = messaggioProdottoSingolo.replace("{0}", numRichieste.toString());
                  msg = msg.replace("{1}", codiceCatalogo);
                  ret.add(((new Object[] {msg, codiceCatalogo})));
                }
              }
              pageContext.setAttribute("listaRichiesteProdotti", ret, PageContext.REQUEST_SCOPE);
            }
		  } catch (SQLException e) {
		    throw new JspException("Errore nel conteggio dei prodotti da verificare per ogni catalogo", e);
		  }



          return null;
		}
}
