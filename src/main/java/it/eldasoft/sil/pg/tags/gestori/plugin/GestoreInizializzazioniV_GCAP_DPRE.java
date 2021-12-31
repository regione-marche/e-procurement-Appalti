/*
 * Created on 22-Set-2009
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
 * Gestore preload per la pagina scheda V_GCAP_DPRE
 *
 * @author Marcello.Caminiti
 */
public class GestoreInizializzazioniV_GCAP_DPRE extends AbstractGestorePreload {

  public GestoreInizializzazioniV_GCAP_DPRE(BodyTagSupportGene tag) {
    super(tag);
  }

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
	  String ngara = "";
	  String dittao="";
	  String tipoFornitura = "";

      tipoFornitura= (String) pageContext.getAttribute("tipoFornitura",PageContext.PAGE_SCOPE);

    if ("NUOVO".equals(modoAperturaScheda)) {


    	if (key.get("V_GCAP_DPRE.NGARA") != null){
            ngara = ((JdbcParametro) key.get("V_GCAP_DPRE.NGARA")).getStringValue();
    		dittao = ((JdbcParametro) key.get("V_GCAP_DPRE.COD_DITTA")).getStringValue();
    	}else {
            HashMap keyParent = UtilityTags.stringParamsToHashMap(
                (String) pageContext.getAttribute(
                    UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT,
                    PageContext.REQUEST_SCOPE), null);
            ngara = ((JdbcParametro) keyParent.get("DITG.NGARA5")).getStringValue();
            dittao = ((JdbcParametro) keyParent.get("DITG.DITTAO")).getStringValue();
        }

    	//Per problemi con i driver Oracle non si può fare il max
    	//di norvoc, poichè il tipo restituito sarebbe long e non double
    	String select="select distinct norvoc from gcap where ngara = ? and (dittao is null or dittao = ?) order by norvoc desc";
    	try {
            Double norvoc = (Double) sqlManager.getObject(select,
                new Object[] { ngara, dittao });
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

            GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
                pageContext, GeneManager.class);
            boolean isCodificaAutomatica = geneManager.isCodificaAutomatica("GARE", "NGARA");
            if (isCodificaAutomatica && "3".equals(pageContext.getAttribute("genereGara",
                PageContext.REQUEST_SCOPE)))
              pageContext.setAttribute("campoObbligatorio", Boolean.TRUE, PageContext.PAGE_SCOPE);
       } catch (SQLException e) {
        throw new JspException(
            "Errore durante il calcolo del numero d'ordine del criterio", e);
       }
    }else if("VISUALIZZA".equals(modoAperturaScheda)){
    	//Nel caso di GCAP.SOLSIC=1 o GCAP.SOGRIB=1 si deve determinare il valore
    	//GCAP.QUANTI*GCAP.PREZUN da passare alla scheda per valorizzare il campo fittizio
    	ngara = ((JdbcParametro) key.get("V_GCAP_DPRE.NGARA")).getStringValue();
    	try {
			Long contaf = ((JdbcParametro) key.get("V_GCAP_DPRE.CONTAF")).longValue();

			String select="select SOLSIC,SOGRIB,QUANTI,PREZUN from GCAP where ngara = ? and contaf = ?";

			Vector datiGCAP = sqlManager.getVector(select,
    	            new Object[] { ngara, contaf });

    		 if(datiGCAP != null && datiGCAP.size() > 0){
    			 String solsic="";
    			 String sogrib="";

    			 solsic = ((JdbcParametro) datiGCAP.get(0)).getStringValue();
    			 sogrib = ((JdbcParametro) datiGCAP.get(1)).getStringValue();
    			 if((solsic != null && "1".equals(solsic)) || (sogrib != null && "1".equals(sogrib))){
    				 Object obj1, obj2 = null;
        			 Double importo= null;
        			 obj1 = ((JdbcParametro) datiGCAP.get(2)).getValue();
        			 if(obj1 == null)
        			        obj1 = new Double(0);
        			 obj2 = ((JdbcParametro) datiGCAP.get(3)).getValue();
        			 if(obj2 == null)
        			        obj2 = new Double(0);

        			 importo = new Double(((Double) obj1).doubleValue() * ((Double) obj2).doubleValue());
        			 pageContext.setAttribute("importo",importo,
      	                    PageContext.REQUEST_SCOPE);
    			 }
    		 }

             if(tipoFornitura != null && "98".equals(tipoFornitura)){
//               if (key.get("V_GCAP_DPRE.NGARA") != null){
               dittao = ((JdbcParametro) key.get("V_GCAP_DPRE.COD_DITTA")).getStringValue();
               select="select NUNICONF,PRELORDOFF,SCONTOBBL,QUANTI from DPRE,DPRE_SAN" +
               		" where DPRE.NGARA = ? and DPRE.CONTAF = ? and DPRE.DITTAO = ?" +
               		" and DPRE_SAN.NGARA = DPRE.NGARA and DPRE_SAN.CONTAF = DPRE.CONTAF and DPRE_SAN.DITTAO = DPRE.DITTAO";
               Vector datiDPRE = sqlManager.getVector(select,
                   new Object[] { ngara, contaf ,dittao});
               if(datiDPRE != null && datiDPRE.size() > 0){
                 Double nuniconf=null;
                 Double prelordoff=null;
                 Double scontobbl=null;
                 Double quanti =null;
                 nuniconf = ((JdbcParametro) datiDPRE.get(0)).doubleValue();
                 prelordoff = ((JdbcParametro) datiDPRE.get(1)).doubleValue();
                 scontobbl = ((JdbcParametro) datiDPRE.get(2)).doubleValue();
                 quanti = ((JdbcParametro) datiDPRE.get(3)).doubleValue();

                 if(nuniconf != null && prelordoff != null){
                   Object obj1, obj2, obj3 = null;
                   double imp = 0;
                   Double importo= null;
                   obj1 = ((JdbcParametro) datiDPRE.get(0)).getValue();
                   if(obj1 == null){
                     importo = new Double(0);
                   }else{
                     obj2 = ((JdbcParametro) datiDPRE.get(1)).getValue();
                     if(obj2 == null)
                            obj2 = new Double(0);
                     obj3 = ((JdbcParametro) datiDPRE.get(2)).getValue();
                     if(obj3 == null)
                            obj3 = new Double(0);
                     imp = (((Double) obj2).doubleValue() / ((Double) obj1).doubleValue());
                     importo = new Double(imp * (1.0-(1.0/100.0*((Double) obj3).doubleValue())));
                   }
                   pageContext.setAttribute("importo1",importo,
                          PageContext.REQUEST_SCOPE);
               }

                 if(quanti != null && prelordoff != null){
                   Object obj1, obj2, obj3 = null;
                   double imp = 0;
                   Double importo= null;
                   obj1 = ((JdbcParametro) datiDPRE.get(3)).getValue();
                   if(obj1 == null){
                     importo = new Double(0);
                   }else{
                     obj2 = ((JdbcParametro) datiDPRE.get(1)).getValue();
                     if(obj2 == null)
                            obj2 = new Double(0);
                     obj3 = ((JdbcParametro) datiDPRE.get(2)).getValue();
                     if(obj3 == null)
                            obj3 = new Double(0);
                     imp = (((Double) obj2).doubleValue() * ((Double) obj1).doubleValue());
                     importo = new Double(imp * (1.0-(1.0/100.0*((Double) obj3).doubleValue())));
                   }
                   pageContext.setAttribute("importo2",importo,
                          PageContext.REQUEST_SCOPE);
               }

               }

             }


		} catch (GestoreException e) {
			throw new JspException(
		            "Errore durante il caricamento dei dati da GCAP", e);
		}catch (SQLException e) {
	        throw new JspException(
	                "Errore durante il caricamento dei dati da GCAP", e);
	           }
    }else {
    	ngara = ((JdbcParametro) key.get("V_GCAP_DPRE.NGARA")).getStringValue();

    }

    //Si determina il valore di Tipgen, poichè per TIPGEN =2,3 non si deve
    //visualizzare il campo CLASI1.GCAP della scheda
    try{
   	 this.setTipgen (pageContext,sqlManager,ngara);
   	}catch (SQLException e) {
	        throw new JspException(
		            "Errore durante la lettura del TIPGEN della gara", e);
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