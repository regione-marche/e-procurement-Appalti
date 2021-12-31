/*
 * Created on 09-Set-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore preload per la pagina scheda gcap
 *
 * @author Marcello.Caminiti
 */
public class GestoreInizializzazioniGcap extends AbstractGestorePreload {

  public GestoreInizializzazioniGcap(BodyTagSupportGene tag) {
    super(tag);
  }

  // Viene calcolato il prodotto QUANTI*PREZUN per popolare
  // il campo fittizio nel caso la pagina non sia in inserimento
  // In inserimento si deve calcolare il nuovo valore di NORVOC
  // come max(NORVOC) + 1
  @Override
  public void doBeforeBodyProcessing(PageContext pageContext,
      String modoAperturaScheda) throws JspException {

	  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
	          pageContext, SqlManager.class);

	  HashMap key = UtilityTags.stringParamsToHashMap(
              (String) pageContext.getAttribute(
                  UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, PageContext.REQUEST_SCOPE),
              null);

    if (!"NUOVO".equals(modoAperturaScheda)) {

    	String ngara = ((JdbcParametro) key.get("GCAP.NGARA")).getStringValue();
    	String contaf = ((JdbcParametro) key.get("GCAP.CONTAF")).getStringValue();

    	String select="select QUANTI,PREZUN, CONTAFAQ from GCAP where ngara = ? and contaf = ?";
    	try {
    		Vector datiGCAP = sqlManager.getVector(select,
    	            new Object[] { ngara, contaf });

    		 if(datiGCAP != null && datiGCAP.size() > 0){
    			 Object obj1, obj2, obj3 = null;
    			 Double importo= null;
    			 obj1 = ((JdbcParametro) datiGCAP.get(0)).getValue();
    			 if(obj1 == null)
    			        obj1 = new Double(0);
    			 obj2 = ((JdbcParametro) datiGCAP.get(1)).getValue();
    			 if(obj2 == null)
    			        obj2 = new Double(0);

    			 importo = new Double(((Double) obj1).doubleValue() * ((Double) obj2).doubleValue());

    			 pageContext.setAttribute("importo",importo,
 	                    PageContext.REQUEST_SCOPE);

    			 obj3 = ((JdbcParametro) datiGCAP.get(2)).getValue();

    			 pageContext.setAttribute("contafaq",obj3,
                     PageContext.REQUEST_SCOPE);

    			 this.setTipgen (pageContext,sqlManager,ngara);
    		 }


    	}catch (SQLException e) {
            throw new JspException(
                    "Errore durante le inizializzazioni della lavorazione o fornitura", e);
              }

    }else {
    	String ngara = "";
    	boolean garaOffertaSingola= false;
    	//Poichè la pagina gcap-scheda.jsp può essere aperta anche da TORN
        //per le gare divise a lotti con offerta unica, si deve differenziare
        //la valorizzazione del campo ngara
    	if ("3".equals(pageContext.getAttribute("genereGara",
                PageContext.REQUEST_SCOPE))) {
    		 HashMap keyParent = UtilityTags.stringParamsToHashMap(
    	                (String) pageContext.getAttribute(
    	                    UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT,
    	                    PageContext.REQUEST_SCOPE), null);
    		 ngara = ((JdbcParametro) keyParent.get("TORN.CODGAR")).getStringValue();
    		 garaOffertaSingola = true;

    		 //Nel caso di gara ad offerta unica, modalità Offerta Prezzi e codifica
    		 //automatica attiva alcuni campi devono essere resi obbligatori.
    		 GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
    		        pageContext, GeneManager.class);
    		 boolean isCodificaAutomatica = geneManager.isCodificaAutomatica("GARE", "NGARA");
    		 if (isCodificaAutomatica)
    		   pageContext.setAttribute("campoObbligatorio", Boolean.TRUE, PageContext.PAGE_SCOPE);
    	} else {
    		  if (key.get("GCAP.NGARA") != null)
	            ngara = ((JdbcParametro) key.get("GCAP.NGARA")).getStringValue();
	          else {
	            HashMap keyParent = UtilityTags.stringParamsToHashMap(
	                (String) pageContext.getAttribute(
	                    UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT,
	                    PageContext.REQUEST_SCOPE), null);

	                	ngara = ((JdbcParametro) keyParent.get("GARE.NGARA")).getStringValue();

	          }
    	}
    	if (garaOffertaSingola == false){
	    	//Per problemi con i driver Oracle non si può fare il max
	    	//di norvoc, poichè il tipo restituito sarebbe long e non double
	    	String select="select distinct norvoc from gcap where ngara = ? and dittao is null order by norvoc desc";
	    	try {
	            Double norvoc = (Double) sqlManager.getObject(select,
	                new Object[] { ngara });
	            double newNorvoc = 1;
	            if (norvoc != null){
	            	newNorvoc = Math.ceil(norvoc.doubleValue());
	              if(norvoc.doubleValue() == newNorvoc)
	            	  newNorvoc += 1;
	            }

	            pageContext.setAttribute("newNorvoc",
	                UtilityNumeri.convertiDouble(new Double(newNorvoc),
	                    UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE, 3),
	                        PageContext.REQUEST_SCOPE);
	       } catch (SQLException e) {
	        throw new JspException(
	            "Errore durante il calcolo del numero d'ordine del criterio", e);
	       }
    	}
    	try{
    	 this.setTipgen (pageContext,sqlManager,ngara);
    	}catch (SQLException e) {
	        throw new JspException(
		            "Errore durante la lettura del TIPGEN della gara", e);
		       }
    }

    //Si determina se è attiva l'integrazione con WSERP
    String integrazioneWSERP="0";
    String urlWSERP = ConfigManager.getValore("wserp.erp.url");
    if(urlWSERP != null && !"".equals(urlWSERP)){
      integrazioneWSERP ="1";
      GestioneWSERPManager gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
          pageContext, GestioneWSERPManager.class);

      try {
        WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi("WSERP");
        if (configurazione.isEsito()) {
          String tipoWSERP = configurazione.getRemotewserp();
          pageContext.setAttribute("tipoWSERP", tipoWSERP, PageContext.REQUEST_SCOPE);
        }
      } catch (GestoreException e) {
        UtilityStruts.addMessage(pageContext.getRequest(), "error",
            "wserpconfigurazione.erp.configurazioneleggi.remote.error",new Object[]{":\r\n Configurazione integrazione con sistema ERP non corretta"});
      }

    }
    pageContext.setAttribute("integrazioneWSERP", integrazioneWSERP, PageContext.REQUEST_SCOPE);


  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }

  /**
   * Estrazione del valore del campo TORN.TIPGEN
   *
   * @param page
   * @param sqlManager
   * @param numeroGara
   * @throws SQLException
   */
  private void setTipgen (PageContext page,SqlManager Manager,String numeroGara)throws SQLException{
	String select = "select tipgen from torn,gare where gare.ngara = ? and gare.codgar1 = torn.codgar";
	Long tipGen = (Long) Manager.getObject(select, new Object[]{numeroGara});
	if(tipGen != null)
        page.setAttribute("tipgen", tipGen, PageContext.PAGE_SCOPE);

  }
}