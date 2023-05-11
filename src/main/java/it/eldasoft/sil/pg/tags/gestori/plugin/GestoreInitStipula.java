/*
 * Created on 15/04/2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Gestore che inizializza una stipula
 *
 * @author Cristian Febas
 */
public class GestoreInitStipula extends AbstractGestorePreload {

  SqlManager sqlManager = null;



  public GestoreInitStipula(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }

  public void inizializzaManager(PageContext page){
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);



  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    this.inizializzaManager(page);

    HttpSession session = page.getSession();
    String uffint = (String) session.getAttribute("uffint");

    Long idStipula=null;
    Long ncont = null;
    String ngara = page.getRequest().getParameter("ngara");
    if(ngara==null) {
    	ngara = (String) page.getRequest().getAttribute("ngara");
    }
    ngara = UtilityStringhe.convertiNullInStringaVuota(ngara);
    String ncontStr = page.getRequest().getParameter("ncont");
    ncontStr = UtilityStringhe.convertiNullInStringaVuota(ncontStr);
    if(!"".equals(ncontStr)) {
    	ncont = Long.valueOf(ncontStr);

    }
    String codgar = null;
    String codice = null;
    String codiceLotto = null;
    String codiga = null;
    String oggetto = null;
    String codimp = null;
    String nomimp = null;
    Double iaggiu = null;
    Long modcont = null;
    String messaggio = "";
    String controlloSuperato="SI";
    Long idPadre = null;
    Long idOriginario = null;
    String cartella=null;

    String modo = (String) page.getAttribute(
            UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO, PageContext.REQUEST_SCOPE);
    HashMap key = null;

    try {

	    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modo)) {
	        // carico la chiave dell'occorrenza
	        key = UtilityTags.stringParamsToHashMap(
	            (String) page.getAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA,
	                PageContext.REQUEST_SCOPE), null);
	        idStipula = (Long) ((JdbcParametro) key.get("G1STIPULA.ID")).getValue();
	        codice = (String) this.sqlManager.getObject("select codice from v_gare_stipula where  id = ? ", new Object[]{idStipula});
	        ncont = (Long) this.sqlManager.getObject("select ncont from v_gare_stipula where  id = ? ", new Object[]{idStipula});

	    }


        Vector<?> datiAggiudicatarioStipula = this.sqlManager.getVector("select codgar,ngara,oggetto,iaggiu,codimp,nomest,modcont,codice" +
          		" from v_aggiudicatari_stipula where ngara = ? and ncont = ? ", new Object[]{ngara,ncont});
          if(datiAggiudicatarioStipula!=null && datiAggiudicatarioStipula.size()>0){
              codgar =  SqlManager.getValueFromVectorParam(datiAggiudicatarioStipula, 0).getStringValue();
              ngara =  SqlManager.getValueFromVectorParam(datiAggiudicatarioStipula, 1).getStringValue();
              oggetto =  SqlManager.getValueFromVectorParam(datiAggiudicatarioStipula, 2).getStringValue();
              Object impAgg =  SqlManager.getValueFromVectorParam(datiAggiudicatarioStipula, 3).getValue();
              if (impAgg != null) {
                  if (impAgg instanceof Long) {
                    iaggiu = Double.valueOf((Long) impAgg);
                  } else if (impAgg instanceof Double) {
                    iaggiu = Double.valueOf((Double) impAgg);
                  }
              }
              codimp =  SqlManager.getValueFromVectorParam(datiAggiudicatarioStipula, 4).getStringValue();
              nomimp =  SqlManager.getValueFromVectorParam(datiAggiudicatarioStipula, 5).getStringValue();
              modcont =  (Long) SqlManager.getValueFromVectorParam(datiAggiudicatarioStipula, 6).getValue();
              codice =  SqlManager.getValueFromVectorParam(datiAggiudicatarioStipula, 7).getStringValue();

              if(modcont!=null) {
                  if(Long.valueOf(modcont).equals(Long.valueOf(1))) {
                	  codiceLotto = (String) this.sqlManager.getObject("select ngaral from garecont where  ngara = ? and ncont =?", new Object[]{ngara,ncont});
                  }else {
                	  if(Long.valueOf(modcont).equals(Long.valueOf(2))) {
                		  codiceLotto = (String) this.sqlManager.getObject("select min(ga.ngara) from gare ga,garecont gc"
                		  		+ " where ga.codgar1=gc.ngara and gc.ngara = ? and gc.ncont = ?"
                		  		+ " and (genere is null or  genere<>3)", new Object[]{ngara,ncont});
                	  }else {
                		  if(Long.valueOf(modcont).equals(Long.valueOf(0))) {
                			  codiceLotto = ngara;
                		  }
                	  }
                  }

                  codiga = (String) this.sqlManager.getObject("select codiga from gare where  ngara = ?", new Object[]{codiceLotto});
              }

              page.setAttribute("modcont", modcont, PageContext.REQUEST_SCOPE);
              page.setAttribute("codiga", codiga, PageContext.REQUEST_SCOPE);
              page.setAttribute("codiceLotto", codiceLotto, PageContext.REQUEST_SCOPE);
          }


	    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modo)) {
	        // carico la chiave dell'occorrenza
	        key = UtilityTags.stringParamsToHashMap(
	            (String) page.getAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA,
	                PageContext.REQUEST_SCOPE), null);
	        codgar = (String) this.sqlManager.getObject("select codgar from v_gare_stipula where  id = ? ", new Object[]{idStipula});

      		String nomerup= (String) sqlManager.getObject(
        		      "select nomtec from tecni,torn where tecni.codtec = torn.codrup and torn.codgar = ? ",
        		      new Object[] { codgar });
    		page.setAttribute("tecnicorup", nomerup, PageContext.REQUEST_SCOPE);


    		String nomedec= (String) sqlManager.getObject(
        		      "select nomtec from tecni,g1stipula where tecni.codtec = g1stipula.coddec and g1stipula.id = ?",
        		      new Object[] { idStipula });
        	page.setAttribute("tecnicodec", nomedec, PageContext.REQUEST_SCOPE);
	      	idPadre = (Long)this.sqlManager.getObject("select id_padre from g1stipula"
	      	    	+ " where g1stipula.id = ? ", new Object[]{idStipula});

	    }else {
	    	//INSERIMENTO
	        if (!"".equals(ngara)){
	            	String SIdPadre = page.getRequest().getParameter("idpadre");
	            	if(SIdPadre!=null) {
	            		idPadre = Long.valueOf(SIdPadre);
	            		idOriginario = Long.valueOf(page.getRequest().getParameter("idoriginario"));
	            	}
	        		if (idPadre==null || idPadre.equals(idOriginario)) {
	        			//page.setAttribute("initNGARA", ngara, PageContext.REQUEST_SCOPE);
	        			if (idPadre!=null) {
	        				Vector<?> datiGaraStipulaPadre= this.sqlManager.getVector("select "
		        					+ "oggetto,impstipula,cartella"
			                		+ " from g1stipula where id =?",
			                		new Object[]{idPadre});
		        			oggetto =  (String) SqlManager.getValueFromVectorParam(datiGaraStipulaPadre, 0).getValue();
		        			Object impAgg =  SqlManager.getValueFromVectorParam(datiGaraStipulaPadre, 1).getValue();
		                    if (impAgg != null) {
		                        if (impAgg instanceof Long) {
		                          iaggiu = Double.valueOf((Long) impAgg);
		                        } else if (impAgg instanceof Double) {
		                          iaggiu = Double.valueOf((Double) impAgg);
		                        }
		                    }
		                    cartella =  (String) SqlManager.getValueFromVectorParam(datiGaraStipulaPadre, 2).getValue();
	        			}
	        			page.setAttribute("initOGGETTO", oggetto, PageContext.REQUEST_SCOPE);
	        			page.setAttribute("initIAGGIU", iaggiu, PageContext.REQUEST_SCOPE);
	        			page.setAttribute("initCARTELLA", cartella, PageContext.REQUEST_SCOPE);
						page.setAttribute("initCODIMP", codimp, PageContext.REQUEST_SCOPE);
	        			page.setAttribute("initNOMIMP", nomimp, PageContext.REQUEST_SCOPE);
	        			page.setAttribute("initCODICE", codice, PageContext.REQUEST_SCOPE);
	        			//recupero i dati della gara in generale (per RAI saranno bloccati da profilo ma alcuni,
	        			//come l'importo a garanzia potrebbero arrivare dall'aggiudicazione)
	        			Vector<?> datiGaraStipula=null;
        				if(Long.valueOf(modcont).equals(Long.valueOf(1))) {
        					String codLotto = (String) this.sqlManager.getObject("select ngaral from garecont where  ngara = ? and ncont =?", new Object[]{ngara,ncont});
		        			datiGaraStipula= this.sqlManager.getVector("select ridiso,impgar,nquiet,dquiet,istcre,indist"
		        					+ " from gare where ngara =?",new Object[]{codLotto});
        				}else {
    	        			if(Long.valueOf(2).equals(modcont)) {

    	        				String codLotto = (String) sqlManager.getObject(
    	        	        		      "select min(ngara) from gare where codgar1=? and ditta=? and (genere is null or genere<>3)",
    	        	        		      new Object[] { codgar,codimp });
    		        			datiGaraStipula= this.sqlManager.getVector("select ridiso,impgar,nquiet,dquiet,istcre,indist"
    		        					+ " from gare where ngara =?",new Object[]{codLotto});
    	        			}else {
    		        			datiGaraStipula= this.sqlManager.getVector("select ridiso,impgar,nquiet,dquiet,istcre,indist"
    		        					+ " from gare where ngara =?",new Object[]{ngara});
    	        			}
        				}

	        			if(datiGaraStipula!=null && datiGaraStipula.size()>0){
	        				String ridiso =  (String) SqlManager.getValueFromVectorParam(datiGaraStipula, 0).getValue();
	        				Double impgar =  (Double) SqlManager.getValueFromVectorParam(datiGaraStipula, 1).getValue();
	        				String nquiet =  (String) SqlManager.getValueFromVectorParam(datiGaraStipula, 2).getValue();
	        				Date dquiet =  (Date) SqlManager.getValueFromVectorParam(datiGaraStipula, 3).getValue();
	        				String istcre =  (String) SqlManager.getValueFromVectorParam(datiGaraStipula, 4).getValue();
	        				String indist =  (String) SqlManager.getValueFromVectorParam(datiGaraStipula, 5).getValue();
							page.setAttribute("initRIDISO", ridiso, PageContext.REQUEST_SCOPE);
	        				page.setAttribute("initIMPGAR", impgar, PageContext.REQUEST_SCOPE);
	        				page.setAttribute("initNQUIET", nquiet, PageContext.REQUEST_SCOPE);
	        				page.setAttribute("initDQUIET", UtilityDate.convertiData(dquiet, UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);
	        				page.setAttribute("initISTCRE", istcre, PageContext.REQUEST_SCOPE);
	        				page.setAttribute("initINDIST", indist, PageContext.REQUEST_SCOPE);
	        			}

	        			Vector<?> datiGarecontStipula= this.sqlManager.getVector("select dscapo from garecont where ngara =? and ncont=?",
	        					new Object[]{ngara,ncont});
	        			if(datiGarecontStipula!=null && datiGarecontStipula.size()>0){
	        				Date dscapo =  (Date) SqlManager.getValueFromVectorParam(datiGarecontStipula, 0).getValue();
	        				page.setAttribute("initDSCAPO", UtilityDate.convertiData(dscapo, UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);
	        			}
	        		}
	        		else {
	        			Vector<?> datiGaraStipulaPadre= this.sqlManager.getVector("select "
	        					+ "ridiso,impgar,nquiet,dquiet,istcre,indist,dscapo,oggetto,impstipula,cartella"
		                		+ " from g1stipula where id =?",
		                		new Object[]{idPadre});
	        			oggetto =  (String) SqlManager.getValueFromVectorParam(datiGaraStipulaPadre, 7).getValue();
	        			Object impAgg =  SqlManager.getValueFromVectorParam(datiGaraStipulaPadre, 8).getValue();
	                    if (impAgg != null) {
	                        if (impAgg instanceof Long) {
	                          iaggiu = Double.valueOf((Long) impAgg);
	                        } else if (impAgg instanceof Double) {
	                          iaggiu = Double.valueOf((Double) impAgg);
	                        }
	                    }
	        			page.setAttribute("initOGGETTO", oggetto, PageContext.REQUEST_SCOPE);
		                page.setAttribute("initIAGGIU", iaggiu, PageContext.REQUEST_SCOPE);
		                page.setAttribute("initCODIMP", codimp, PageContext.REQUEST_SCOPE);
		                page.setAttribute("initNOMIMP", nomimp, PageContext.REQUEST_SCOPE);
		                page.setAttribute("initCODICE", codice, PageContext.REQUEST_SCOPE);
	                	String ridiso =  (String) SqlManager.getValueFromVectorParam(datiGaraStipulaPadre, 0).getValue();
		                Double impgar =  (Double) SqlManager.getValueFromVectorParam(datiGaraStipulaPadre, 1).getValue();
		                String nquiet =  (String) SqlManager.getValueFromVectorParam(datiGaraStipulaPadre, 2).getValue();
		                Date dquiet =  (Date) SqlManager.getValueFromVectorParam(datiGaraStipulaPadre, 3).getValue();
	                	String istcre =  (String) SqlManager.getValueFromVectorParam(datiGaraStipulaPadre, 4).getValue();
	                	String indist =  (String) SqlManager.getValueFromVectorParam(datiGaraStipulaPadre, 5).getValue();
	                	Date dscapo =  (Date) SqlManager.getValueFromVectorParam(datiGaraStipulaPadre, 6).getValue();
	                	cartella = (String) SqlManager.getValueFromVectorParam(datiGaraStipulaPadre, 9).getValue();
		                page.setAttribute("initRIDISO", ridiso, PageContext.REQUEST_SCOPE);
		                page.setAttribute("initIMPGAR", impgar, PageContext.REQUEST_SCOPE);
		                page.setAttribute("initNQUIET", nquiet, PageContext.REQUEST_SCOPE);
		                page.setAttribute("initDQUIET", UtilityDate.convertiData(dquiet, UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);
		                page.setAttribute("initISTCRE", istcre, PageContext.REQUEST_SCOPE);
		                page.setAttribute("initINDIST", indist, PageContext.REQUEST_SCOPE);
		                page.setAttribute("initDSCAPO", UtilityDate.convertiData(dscapo, UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);
						page.setAttribute("initCARTELLA", cartella, PageContext.REQUEST_SCOPE);
	        		}

	      	    //tecnico RUP
	      	    Vector<?> datiRup = this.sqlManager.getVector("select codtec,nomtec from tecni,torn"
	      	    		+ " where tecni.codtec = torn.codrup and torn.codgar = ? ", new Object[]{codgar});
	      	    if(datiRup!=null && datiRup.size()>0){
	      	    	String codrup =  SqlManager.getValueFromVectorParam(datiRup, 0).getStringValue();
	      	    	page.setAttribute("initCODTECRUP", codrup, PageContext.REQUEST_SCOPE);
	      	    	String nomerup =  SqlManager.getValueFromVectorParam(datiRup, 1).getStringValue();
	      	    	page.setAttribute("initNOMTECRUP", nomerup, PageContext.REQUEST_SCOPE);
	      	    }


	        }

	        //caricamento del campo Dattoa
	        Date dattoa= (Date)this.sqlManager.getObject("select dattoa from gare where ngara=?", new Object[] {codiceLotto});
	        page.setAttribute("initDATTOA", UtilityDate.convertiData(dattoa, UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);


	    }
	    //Dati stipula padre
      	if(idPadre!=null){
  	    	String codStipulaPadre = (String)this.sqlManager.getObject("select codstipula from g1stipula"
	      	    	+ " where g1stipula.id = ? ", new Object[]{idPadre});
  	    	page.setAttribute("initCODSTIPULAPADRE", codStipulaPadre, PageContext.REQUEST_SCOPE);
  	    }


    	//S.A.
    	Vector<?> datiSA = this.sqlManager.getVector("select codein,nomein from uffint,torn"
				+ " where uffint.codein = torn.cenint and torn.codgar = ? ", new Object[]{codgar});
	    if(datiSA!=null && datiSA.size()>0){
	    	String cenint =  SqlManager.getValueFromVectorParam(datiSA, 0).getStringValue();
	    	page.setAttribute("initCENINT", cenint, PageContext.REQUEST_SCOPE);
	    	String nomein =  SqlManager.getValueFromVectorParam(datiSA, 1).getStringValue();
	    	page.setAttribute("initNOMEIN", nomein, PageContext.REQUEST_SCOPE);
	    }

	    boolean dattoaMod = true;
        Long genere=(Long)this.sqlManager.getObject("select genere from gare where codgar1=? and codgar1=ngara", new Object[]{codgar});
        if(new Long(3).equals(genere))
          dattoaMod = false;
        page.setAttribute("dattoaMod", new Boolean(dattoaMod), PageContext.REQUEST_SCOPE);

    } catch (SQLException e) {
        throw new JspException("Errore durante la lettura dei dati della gara/lotto associata alla stipula ", e);
    }

      page.setAttribute("controlloSuperato", controlloSuperato, PageContext.REQUEST_SCOPE);
      page.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);





  }

}