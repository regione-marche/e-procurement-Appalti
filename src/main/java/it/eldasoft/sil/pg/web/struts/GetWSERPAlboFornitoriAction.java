/*
 * Created on 27/sept/2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;



import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.bl.erp.RaiwayWSERPManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoriResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author Cristian.Febas - Eldasoft S.p.A. Treviso
 *
 */
public class GetWSERPAlboFornitoriAction extends ActionBaseNoOpzioni {

	/** FORWARD_SUCCESS forward richiesta lista cup avvenuta con successo. */
  protected static final String          FORWARD_SUCCESS   = "listafalbosuccess";
  /** FORWARD_SUCCESS_NO_POPUP forward richiesta lista cup avvenuta con successo da form non in popup. */
  protected static final String          FORWARD_SUCCESS_NO_POPUP   = "listafalbosuccessnopopup";
  /** FORWARD_ERROR forward per richiesta lista cup ha generato un errore. */
  protected static final String          FORWARD_ERROR     = "listafalboerror";
  /** FORWARD_ERROR forward per richiesta lista cup ha generato un errore non in popup. */
  protected static final String          FORWARD_ERROR_NO_POPUP     = "listafalboerrornopopup";
  /** Logger Log4J di classe. */
  private static Logger                          logger            = Logger.getLogger(GetWSERPAlboFornitoriAction.class);
  
  private static final String PROP_ARIBA_WS_OATH            = "ariba.ws.oath";
  private static final String PROP_ARIBA_WS_APIKEY            = "ariba.ws.apikey";
  
  private GestioneWSERPManager gestioneWSERPManager;

  private SqlManager sqlManager;
  
  private RaiwayWSERPManager raiwayWSERPManager;
  
  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setRaiwayWSERPManager(RaiwayWSERPManager raiwayWSERPManager){
    this.raiwayWSERPManager = raiwayWSERPManager;
  }
  
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("GetWSERPAlboFornitoriAction: inizio metodo");
    }

    String target = FORWARD_SUCCESS;
    String messageKey = null;
    
    try {    
      String username = ConfigManager.getValore(PROP_ARIBA_WS_APIKEY);  
      String password = ConfigManager.getValore(PROP_ARIBA_WS_OATH);
      String codgar = request.getParameter("codgar");
      codgar = StringUtils.stripToEmpty(codgar);
      String ngara = request.getParameter("ngara");
      ngara = StringUtils.stripToEmpty(ngara);
      List<Vector< ? >> risultatoListaFornitori = new ArrayList<Vector< ? >>();
      
      
      String tipoWSERP = request.getParameter("tipoWSERP");
      String inPopup = request.getParameter("inPopup");
      
      if("RAIWAY".equals(tipoWSERP)) {
        WSERPFornitoreType rdaFornitoreType = new WSERPFornitoreType();
        String ragioneSociale = request.getParameter("ragionesociale");
        ragioneSociale = StringUtils.stripToEmpty(ragioneSociale);
        if(!"".equals(ragioneSociale)) {
          rdaFornitoreType.setRagioneSociale(ragioneSociale);  
        }
        
        String categoria = request.getParameter("categoria");
        categoria = StringUtils.stripToEmpty(categoria);
        if(!"".equals(categoria)) {
          rdaFornitoreType.setCategoria(categoria);  
        }
        
        WSERPFornitoriResType fornitoreResType = this.gestioneWSERPManager.wserpListaFornitori(username, password, "WSERP", rdaFornitoreType);
        String errMsgEvento = fornitoreResType.getMessaggio();
        errMsgEvento = StringUtils.stripToEmpty(errMsgEvento);
        if(!"".equals(errMsgEvento)) {
            //traccio eventuali incongruenze
          String msgIncongruenzeAlbo="L'estrazione ha segnalato le seguenti incongruenze:\n";
          errMsgEvento=msgIncongruenzeAlbo+errMsgEvento;
          LogEvento logEvento = LogEventiUtils.createLogEvento(request);
            logEvento.setCodApplicazione("PG");
            logEvento.setLivEvento(3);
            logEvento.setOggEvento(categoria);
            logEvento.setCodEvento("GA_CATALBO_ERP");
            logEvento.setDescr("Estrazione ditte (albo fornitori) con categoria "+categoria);
            logEvento.setErrmsg(errMsgEvento);
            this.gestioneWSERPManager.updLogEventi(logEvento);
        }
  
        WSERPFornitoreType[] fornitoreArray = fornitoreResType.getFornitoreArray();
        if(fornitoreArray!= null) {
            int totalFornitori = fornitoreArray.length;
            
            for (int i = 0; i < totalFornitori; i++) {
              WSERPFornitoreType fornitoreSingle = fornitoreArray[i];
              Vector<String> dati = new Vector<>();
              
              //Tutti i dati necessari per eseguire la query, ma nel caso la partita Iva e' null l'oggetto viene ridimensionato di conseguenza
              Object[] obj = new Object[] { fornitoreSingle.getCodiceFiscale(), fornitoreSingle.getPartitaIva() };
              String query = "select codimp, cfimp, pivimp, locimp, proimp, nomimp, emai2ip from impr where cfimp = ? ";
              if(fornitoreSingle.getPartitaIva() != null) {
                query += "and pivimp = ? ";
              } else {
                query += "and pivimp is null ";
                obj = new Object[] { fornitoreSingle.getCodiceFiscale() };
              }
              
              Vector<?> datiFornitore = this.sqlManager.getVector(query, obj);
              if(datiFornitore!=null && !datiFornitore.isEmpty()){
                  String codimp = SqlManager.getValueFromVectorParam(datiFornitore, 0).stringValue();
                  String cfimp = SqlManager.getValueFromVectorParam(datiFornitore, 1).stringValue();
                  String pivimp = SqlManager.getValueFromVectorParam(datiFornitore, 2).stringValue();
                  String locimp = SqlManager.getValueFromVectorParam(datiFornitore, 3).stringValue();
                  String proimp = SqlManager.getValueFromVectorParam(datiFornitore, 4).stringValue();
                  String nomimp = SqlManager.getValueFromVectorParam(datiFornitore, 5).stringValue();
                  String pec = SqlManager.getValueFromVectorParam(datiFornitore, 6).stringValue();
                  String[] risultatiRicercaIfExistImpresa = controllaCheckImprExistFe(codimp);
                  dati.add(codimp);
                  dati.add(fornitoreSingle.getIdFornitore());
                  dati.add(nomimp);
                  dati.add(cfimp);
                  dati.add(pivimp);
                  String ecc = fornitoreSingle.getSystemIdFornitore();
                  ecc = StringUtils.stripToEmpty(ecc);
                  if(!"".equals(ecc) && !"-1".equals(ecc)) {
                    dati.add(ecc);
                  }else {
                    dati.add("Non disponibile");
                  }
                  locimp = StringUtils.stripToEmpty(locimp);
                  if(!"".equals(locimp)) {
                    dati.add(locimp);
                  }else {
                    dati.add("Non disponibile");
                  }
                  proimp = StringUtils.stripToEmpty(proimp);
                  if(!"".equals(proimp)) {
                    dati.add(proimp);
                  }else {
                    dati.add("Non disponibile");
                  }
                  pec = StringUtils.stripToEmpty(pec);
                  if(!"".equals(pec)) {
                    dati.add(pec);
                  }else {
                    dati.add("Non disponibile");
                  }
                  dati.add(fornitoreSingle.getRagioneSociale());
                  dati.add(risultatiRicercaIfExistImpresa[0]);
                  dati.add(risultatiRicercaIfExistImpresa[1]);
                  dati.add("si"); //checkvisible

                  Object[] objcheck = new Object[] { codgar, ngara, codimp };
                  String querycheck = "select count(*) from ditg where codgar5 = ? and ngara5 = ? and dittao = ? ";
                  
                  Long nditg = (Long)this.sqlManager.getObject(querycheck, objcheck);
                  if(datiFornitore!=null && nditg.equals(Long.valueOf(0))){
                    risultatoListaFornitori.add(dati);
                  }
                  
              } else {
                
                String codimp="Non disponibile";
                String cfimp=fornitoreSingle.getCodiceFiscale();
                String pivimp=fornitoreSingle.getPartitaIva();
                String locimp="Non disponibile";
                String proimp="Non disponibile";
                String nomimp="Non disponibile";
                String pec="Non disponibile";
                String checkVisible="si";

                JSONObject operatoreJson =  this.raiwayWSERPManager.getOperatoreEconomico(fornitoreSingle.getCodiceFiscale(), fornitoreSingle.getPartitaIva());
                if(operatoreJson != null) {
                  locimp = operatoreJson.getString("comune");
                  proimp = operatoreJson.getString("provinciaSigla");
                  pec = operatoreJson.getString("pec");
                } else {
                  checkVisible="no";
                }
                
                String[] risultatiRicercaIfExistImpresa = controllaCheckImprExistFe(codimp);
                dati.add(codimp);
                dati.add(fornitoreSingle.getIdFornitore());
                dati.add(nomimp);
                dati.add(cfimp);
                dati.add(pivimp);
                String ecc = fornitoreSingle.getSystemIdFornitore();
                ecc = StringUtils.stripToEmpty(ecc);
                if(!"".equals(ecc) && !"-1".equals(ecc)) {
                  dati.add(ecc);
                }else {
                  dati.add("Non disponibile");
                }
                locimp = StringUtils.stripToEmpty(locimp);
                if(!"".equals(locimp)) {
                  dati.add(locimp);
                }else {
                  dati.add("Non disponibile");
                }
                dati.add(proimp);
                dati.add(pec);
                dati.add(fornitoreSingle.getRagioneSociale());
                dati.add(risultatiRicercaIfExistImpresa[0]);
                dati.add(risultatiRicercaIfExistImpresa[1]);
                dati.add(checkVisible);

                risultatoListaFornitori.add(dati);
              }
              
            }
        }
        if(fornitoreResType.isEsito()) {
          if(inPopup != null && !inPopup.isEmpty()) {
            target = FORWARD_SUCCESS_NO_POPUP;
          } else {
            target = FORWARD_SUCCESS;
          }
          request.setAttribute("risultatoListaFornitori", risultatoListaFornitori);
        
        }else {
          if(inPopup != null && !inPopup.isEmpty()) {
            target = FORWARD_ERROR_NO_POPUP;
          } else {
            target = FORWARD_ERROR;
          }
          messageKey = "errors.alboFornitoriLista.error";
          this.aggiungiMessaggio(request, messageKey, fornitoreResType.getMessaggio());
        }
      } else {//RAI


        WSERPRdaType rdaSearch = new WSERPRdaType();
        
        String numeroFaseAttiva = request.getParameter("numeroFaseAttiva");
        numeroFaseAttiva = StringUtils.stripToEmpty(numeroFaseAttiva);
        String garaLottiConOffertaUnica = request.getParameter("garaLottiConOffertaUnica");
        garaLottiConOffertaUnica = StringUtils.stripToEmpty(garaLottiConOffertaUnica);
        String category = request.getParameter("categoria");
        category = StringUtils.stripToEmpty(category);
        if(!"".equals(category)) {
      	  rdaSearch.setCodiceMateriale(request.getParameter("categoria"));  
        }
  
        String descat = request.getParameter("descat");
        descat = StringUtils.stripToEmpty(descat);
        if(!"".equals(descat)) {
      	  rdaSearch.setCodiceArticolo(request.getParameter("descat"));  
        }
        
        String qsl = request.getParameter("qsl");
        qsl = StringUtils.stripToEmpty(qsl);
        if(!"".equals(qsl)) {
      	  rdaSearch.setNatura(qsl);  
        }
        
        String qslMsg="solo fornitori qualificati";
        if("2".equals(qsl)) {
        	qslMsg="fornitori con documenti scaduti";
        }
  
        
        request.setAttribute("ngara", ngara);
        request.setAttribute("numeroFaseAttiva", numeroFaseAttiva);
        request.setAttribute("garaLottiConOffertaUnica", garaLottiConOffertaUnica);
        request.setAttribute("categoria", category);
        request.setAttribute("qsl", qsl);
        
        WSERPRdaResType wserpRdaRes = this.gestioneWSERPManager.wserpListaRda(username, password, "WSERP", rdaSearch );
        String errMsgEvento = wserpRdaRes.getMessaggio();
        errMsgEvento = StringUtils.stripToEmpty(errMsgEvento);
        if(!"".equals(errMsgEvento)) {
            //traccio eventuali incongruenze
      	  String msgIncongruenzeAlbo="L'estrazione ha segnalato le seguenti incongruenze:\n";
      	  errMsgEvento=msgIncongruenzeAlbo+errMsgEvento;
      	  LogEvento logEvento = LogEventiUtils.createLogEvento(request);
            logEvento.setCodApplicazione("PG");
            logEvento.setLivEvento(3);
            logEvento.setOggEvento(category);
            logEvento.setCodEvento("GA_CATALBO_ERP");
            logEvento.setDescr("Estrazione ditte (albo fornitori) con categoria "+category+" e con opzione: "+qslMsg);
            logEvento.setErrmsg(errMsgEvento);
            this.gestioneWSERPManager.updLogEventi(logEvento);
        }
  
        WSERPRdaType[] rdaArray = wserpRdaRes.getRdaArray();
        if(rdaArray!= null) {
            int totalRDA = rdaArray.length;
            
            for (int a = 0; a < totalRDA; a++) {
          	  WSERPRdaType rdaSingle = rdaArray[a];
  
                Vector<?> datiFornitore = this.sqlManager.getVector("select codimp,cfimp,pivimp,locimp,proimp"
                		+ " from impr where cgenimp = ? and codimp not in (select dittao from ditg where codgar5=?and ngara5=?)",
              		  new Object[] { rdaSingle.getCodiceCarrello(),codgar,ngara });
                if(datiFornitore!=null && datiFornitore.size()>0){
                    Vector<String> dati = new Vector<String>();
                    String codimp=SqlManager.getValueFromVectorParam(datiFornitore, 0).stringValue();
                    String cfimp=SqlManager.getValueFromVectorParam(datiFornitore, 1).stringValue();
                    String pivimp=SqlManager.getValueFromVectorParam(datiFornitore, 2).stringValue();
                    String locimp=SqlManager.getValueFromVectorParam(datiFornitore, 3).stringValue();
                    String proimp=SqlManager.getValueFromVectorParam(datiFornitore, 4).stringValue();
                    dati.add(codimp);
                    dati.add(rdaSingle.getCodiceCarrello());
                    dati.add(rdaSingle.getCodiceRda());
                    dati.add(cfimp);
                    dati.add(pivimp);
                    String ecc = rdaSingle.getCodiceCigAQ();
                    ecc = StringUtils.stripToEmpty(ecc);
                    if(!"".equals(ecc) && !"-1".equals(ecc)) {
                  	  dati.add(rdaSingle.getCodiceCigAQ());
                    }else {
                  	  dati.add("Non disponibile");
                    }
                    locimp = StringUtils.stripToEmpty(locimp);
                    if(!"".equals(locimp)) {
                  	  dati.add(locimp);
                    }else {
                  	  dati.add("Non disponibile");
                    }
                    proimp = StringUtils.stripToEmpty(proimp);
                    if(!"".equals(proimp)) {
                  	  dati.add(proimp);
                    }else {
                  	  dati.add("Non disponibile");
                    }
                    dati.add(rdaSingle.getCodiceMateriale());
                    risultatoListaFornitori.add(dati);
                }else {
                        datiFornitore = this.sqlManager.getVector("select codimp,cfimp,pivimp,locimp,proimp"
                        		+ " from impr where cgenimp = ? and codimp not in (select dittao from ditg where codgar5=?and ngara5=?)",
                      		  new Object[] { rdaSingle.getCodiceMateriale(),codgar,ngara });
                        if(datiFornitore!=null && datiFornitore.size()>0){
	                        Vector<String> dati = new Vector<String>();
	                        String codimp=SqlManager.getValueFromVectorParam(datiFornitore, 0).stringValue();
	                        String cfimp=SqlManager.getValueFromVectorParam(datiFornitore, 1).stringValue();
	                        String pivimp=SqlManager.getValueFromVectorParam(datiFornitore, 2).stringValue();
	                        String locimp=SqlManager.getValueFromVectorParam(datiFornitore, 3).stringValue();
	                        String proimp=SqlManager.getValueFromVectorParam(datiFornitore, 4).stringValue();
	                        dati.add(codimp);
	                        dati.add(rdaSingle.getCodiceCarrello());
	                        dati.add(rdaSingle.getCodiceRda());
	                        dati.add(cfimp);
	                        dati.add(pivimp);
	                        String ecc = rdaSingle.getCodiceCigAQ();
	                        ecc = StringUtils.stripToEmpty(ecc);
	                        if(!"".equals(ecc) && !"-1".equals(ecc)) {
	                      	  dati.add(rdaSingle.getCodiceCigAQ());
	                        }else {
	                      	  dati.add("Non disponibile");
	                        }
	                        locimp = StringUtils.stripToEmpty(locimp);
	                        if(!"".equals(locimp)) {
	                      	  dati.add(locimp);
	                        }else {
	                      	  dati.add("Non disponibile");
	                        }
	                        proimp = StringUtils.stripToEmpty(proimp);
	                        if(!"".equals(proimp)) {
	                      	  dati.add(proimp);
	                        }else {
	                      	  dati.add("Non disponibile");
	                        }
	                        dati.add(rdaSingle.getCodiceMateriale());
	                        risultatoListaFornitori.add(dati);
                	}else {
                        Vector<String> dati = new Vector<String>();
                        String codimp="Non disponibile";
                        String cfimp="Non disponibile";
                        String pivimp="Non disponibile";
                        String locimp="Non disponibile";
                        String proimp="Non disponibile";
                        dati.add(codimp);
                        dati.add(rdaSingle.getCodiceCarrello());
                        dati.add(rdaSingle.getCodiceRda());
                        dati.add(cfimp);
                        dati.add(pivimp);
                        String ecc = rdaSingle.getCodiceCigAQ();
                        ecc = StringUtils.stripToEmpty(ecc);
                        if(!"".equals(ecc) && !"-1".equals(ecc)) {
                      	  dati.add(rdaSingle.getCodiceCigAQ());
                        }else {
                      	  dati.add("Non disponibile");
                            locimp = StringUtils.stripToEmpty(locimp);
                            if(!"".equals(locimp)) {
                          	  dati.add(locimp);
                            }else {
                          	  dati.add("Non disponibile");
                            }
                        }
                        dati.add(locimp);
                        dati.add(proimp);
                        dati.add(rdaSingle.getCodiceMateriale());
                        risultatoListaFornitori.add(dati);
                	}
                }
                
            }
      	  
        }
      
        if(wserpRdaRes.isEsito()) {
            target = FORWARD_SUCCESS;
            request.setAttribute("risultatoListaFornitori", risultatoListaFornitori);
      	  
        }else {
            target = FORWARD_ERROR;
            messageKey = "errors.alboFornitoriLista.error";
            this.aggiungiMessaggio(request, messageKey, wserpRdaRes.getMessaggio());
        }
      }
    } catch (Exception e) {
      target = FORWARD_ERROR;
      messageKey = "errors.alboFornitoriLista.error";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey, e.getMessage());
    } catch (Throwable e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (messageKey != null) {
    	response.reset();
    }

    if (logger.isDebugEnabled()) {
    	logger.debug("GetWSERPAlboFornitoriAction: fine metodo");
    }

    return mapping.findForward(target);

  }
  
  /**
   * 
   * @param codimpr
   * @return Possono tornare tre valori distinti: <br/>
   * <b>0</b> = non esiste nel DB interno; <br/>
   * <b>1</b> = non esiste nel FE ma nel BE esiste; <br/>
   * <b>2</b> = esiste in entrambi i casi sia BE che FE 
   * @throws Exception
   */
  
  private String checkImprExistFe(String codimpr) throws Exception {
    String existImprFe = "0";
    if(codimpr != null && !"".equals(codimpr) && !"Non disponibile".equals(codimpr)) {
      existImprFe = "1";
      String checkImprExist = "select count(*) from w_puser where userent = 'IMPR' and userkey1 = ? ";
      Long count = (Long) this.sqlManager.getObject(checkImprExist, new Object[]{ codimpr });
      if(count != null && count.longValue() > 0) {
        existImprFe = "2";
        String getCodgar = "select codgar5 from ditg d " +
            "inner join garealbo g on d.codgar5 = g.codgar and d.ngara5 = g.ngara "+ 
            "where d.dittao = ? "+ 
            "and d.dabilitaz is not null ";
        List<String> listaCodgar = this.sqlManager.getListVector(getCodgar, new Object[]{codimpr});
        if(listaCodgar!=null && !listaCodgar.isEmpty()){
          existImprFe += ";";
          StringBuilder bld = new StringBuilder();
          bld.append(existImprFe);
          for (int x = 0; x < listaCodgar.size(); x++) {
            bld.append((String) SqlManager.getValueFromVectorParam(listaCodgar.get(x), 0).getValue()+",");
          }
          existImprFe = bld.toString().substring(0, bld.toString().length() - 1);
        }
      }
    }
    return existImprFe;
  }
  
  private String[] controllaCheckImprExistFe(String codimpr) throws Exception {
    String[] res = new String[2];
    res[0] = checkImprExistFe(codimpr);
    if (res[0] != null) {
      if ("0".equals(res[0])) {
        res[1] = "";
      } else if("1".equals(res[0])) {
        res[1] = "";
      } else if(res[0].contains(";")) { // siamo nel caso un cui torna il valore 2 concatenando tutti i codici gare associati al codice impresa
        res[1] = res[0].split(";")[1];
        res[0] = res[0].split(";")[0];
      }
    }
    return res;
  }

}
