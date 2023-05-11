/*
 * Created on 04-Dic-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore preload per la pagina Atto contrattuale
 *
 * @author Marcello.Caminiti
 */
public class GestoreInizializzazioniAttoContrattuale extends AbstractGestorePreload {

  public GestoreInizializzazioniAttoContrattuale(BodyTagSupportGene tag) {
    super(tag);
  }

  // Viene calcolata la somma dei campi GARE.IMPGAR dei lotti aggiudicati dalla
  // ditta relativa alla pagina
  @Override
  public void doBeforeBodyProcessing(PageContext pageContext,
      String modoAperturaScheda) throws JspException {

	  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
	          pageContext, SqlManager.class);

	  HashMap key = UtilityTags.stringParamsToHashMap(
              (String) pageContext.getAttribute(
                  UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, PageContext.REQUEST_SCOPE),
              null);

	  String ngara = "";
      ngara = ((JdbcParametro) key.get("GARE.NGARA")).getStringValue();
	  String modcont =  (String) pageContext.getAttribute("modcont");
	  if("2".equals(modcont)){
	    String select="select codgar1, ditta from GARE where ngara = ?";
    	try {

    		Vector datiGARE = sqlManager.getVector(select,
					new Object[] { ngara });

			String codiceGara="";
			String ditta="";

    		if (datiGARE != null && datiGARE.size() > 0) {
				codiceGara = ((String) ((JdbcParametro) datiGARE.get(0)).getValue());
				ditta = ((String) ((JdbcParametro) datiGARE.get(1)).getValue());
			}

    		//Importo complessivo aggiudicazione
            select = "select sum(iaggiu) from gare where codgar1= ? and ditta = ? and (genere is null or genere <>3)";

            Object sumIaggiu = sqlManager.getObject(
                    select, new Object[] { codiceGara,ditta });
            pageContext.setAttribute("importoAggiudicazioneComplessivo",sumIaggiu,
                        PageContext.REQUEST_SCOPE);

    		//Importo dettaglio prezzi
			select = "select sum(impgar) from gare where codgar1= ? and ditta = ? and (genere is null or genere <>3)";

			Object sumImpgar = sqlManager.getObject(
					select, new Object[] { codiceGara,ditta });
			pageContext.setAttribute("importoCauzione",sumImpgar,
	                    PageContext.REQUEST_SCOPE);

    	}catch (SQLException e) {
            throw new JspException(
                    "Errore durante l'inizializzazione dell'atto contrattuale", e);
        }
	  }
	  Long aqoper = null;
	    try {
	      aqoper = (Long)sqlManager.getObject("select aqoper from gare1 where ngara=?", new Object[]{ngara});
	      pageContext.setAttribute("aqoper", aqoper, PageContext.REQUEST_SCOPE);
	    } catch (SQLException e) {
	      throw new JspException(
	          "Errore nella lettura del campo TORN.AQOPER", e);
	    }

	    if(new Long(2).equals(aqoper)){

	      try {
	        List listaDitteAggiudicatarie = sqlManager.getListVector("select aq.id, aq.ngara, aq.dittao, i.nomest, aq.iaggiu, d.ricsub, aq.ridiso, "
	            + "aq.impgar, aq.nquiet, aq.dquiet, aq.istcre, aq.indist, d.ribauo, d.impoff, aq.banapp, aq.coorba, aq.codbic from ditgaq aq, impr i, ditg d "
	            + "where aq.ngara=? and aq.dittao = i.codimp and aq.ngara=d.ngara5 and aq.dittao=d.dittao order by aq.numord", new Object[]{ngara});
	        pageContext.setAttribute("listaDitteAggiudicatarie", listaDitteAggiudicatarie,
	            PageContext.REQUEST_SCOPE);
	        long numeroDitteAggiudicatarie=0;
	        if(listaDitteAggiudicatarie!=null & listaDitteAggiudicatarie.size()>0){
	          numeroDitteAggiudicatarie=listaDitteAggiudicatarie.size();
	        }
	        pageContext.setAttribute("numeroDitteAggiudicatarie", new Long(numeroDitteAggiudicatarie),
	            PageContext.REQUEST_SCOPE);
	      } catch (SQLException e) {
	        throw new JspException("Errore nell'estrarre i dati di DITGAQ "
	            + "della gara "
	            + ngara, e);
	      }

	    }
    }



  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }


}