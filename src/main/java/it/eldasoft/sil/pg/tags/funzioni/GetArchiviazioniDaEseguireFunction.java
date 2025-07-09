/*
 * Created on 07/04/23
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.tags.utils.UtilityTags;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione per estrarre il numero di gare/avvisi/elenchi/stipule con documenti non archiviati per un certo utente
 * 
 *
 * @author Manuel Bridda
 */
public class GetArchiviazioniDaEseguireFunction extends AbstractFunzioneTag {

  public GetArchiviazioniDaEseguireFunction(){
    super(3, new Class[]{PageContext.class,String.class, String.class});
  }
  
  static Logger      logger = Logger.getLogger(GetArchiviazioniDaEseguireFunction.class);

  /**
   * Viene costruito il filtro per le comunicazioni da leggere. 
   * Il secondo parametro indica il tipo di operazione (count\sel).
   * Il secondo parametro indica il tipo di profilo, i valori sono:
   *          1 - Gare
   *          2 - Elenchi
   *          3 - Cataloghi
   *          4 - (non utilizzato) Ricerche di mercato
   *          5 - Avvisi
   *          6 - (non utilizzato) Protocollo 
   *          7 - (non utilizzato) Affidamenti
   *          8 - (non utilizzato)
   *          9 - (non utilizzato) Ordini NSO
   *         10 - Stipule contratti
   *         
   *         I valori sono gli stessi che si trovano per GetComunicazioniDaLeggereFunction
   */
  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String result=null;
    String filtro = "";
    String tipoOperazione = (String) params[1];
    String profilo = (String) params[2];
    String filtroUtente = null;
    String profiloWeb = null;
    String filtroTipoGara  = null;
    String filtroUffint = null;
    boolean visualizzazioneGareALotti = false;
    boolean visualizzazioneGareLottiOffUnica = false;
    boolean visualizzazioneGareALottoUnico = false;
    
    HttpSession sessione = pageContext.getSession();
    filtroUffint = (String)sessione.getAttribute("uffint");
    
    String entita=null;
    String chiave = null;
    String codgar = null;
    String genere = "";
    String gartel = "";
    switch (profilo) {
    case "1":
      //Profilo Gare
      filtroUtente = (String)pageContext.getRequest().getAttribute("filtroLivelloUtente");
      profiloWeb = (String)sessione.getAttribute("filtroProfiloAttivo");
      filtroTipoGara = (String)pageContext.getRequest().getAttribute("filtroTipoGara");
      visualizzazioneGareALotti = ((Boolean)pageContext.getRequest().getAttribute("visualizzazioneGareALotti")).booleanValue();
      visualizzazioneGareLottiOffUnica = ((Boolean)pageContext.getRequest().getAttribute("visualizzazioneGareLottiOffUnica")).booleanValue();
      visualizzazioneGareALottoUnico = ((Boolean)pageContext.getRequest().getAttribute("visualizzazioneGareALottoUnico")).booleanValue();
      break;
    case "2":
      filtroUtente = (String)pageContext.getRequest().getAttribute("filtroLivelloUtenteElencoOperatori");
      break;
    case "3":
      filtroUtente = (String)pageContext.getRequest().getAttribute("filtroLivelloUtenteCataloghi");
      break;
    case "5":
      filtroUtente = (String)pageContext.getRequest().getAttribute("filtroLivelloUtenteAvvisi");
      break;
    case "10":
      entita="G1STIPULA";
      filtroUtente = (String)pageContext.getRequest().getAttribute("filtroLivelloUtenteStipuleFS12");
      profiloWeb = (String)sessione.getAttribute("filtroProfiloAttivo");
      break;
    }
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    
    GestioneWSDMManager gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        pageContext, GestioneWSDMManager.class);

    if("count".equals(tipoOperazione))
      filtro = "select count(1)";
    else{
      filtro = "select ENTITA.CODGAR as codgar, CHIAVE as chiave_entita, oggetto";
      if ("1".equals(profilo)) {
        filtro+= ", genere, torn.gartel";
      }
    }
    
    String joinTorn = "";
    if ("10".equals(profilo)) {
      joinTorn += " join gare on gare.ngara=g1stipula.ngara join torn on gare.codgar1=torn.codgar where (G1STIPULA.doc_da_arch='1' or G1STIPULA.cos_da_arch='1')";
      
    }else {
      joinTorn += " join torn on torn.codgar=ENTITA.codgar where (TORN.doc_da_arch='1' or TORN.cos_da_arch='1')";
    }
    
    filtro += " from ENTITA"+joinTorn;
    
    switch (profilo) {
    case "1":
      entita = "v_gare_torn";
      chiave = "codice";
      codgar="codgar";
      //Inserimento filtro sull'utente
      if (filtroUtente != null && !"".equals(filtroUtente))
        filtro += " and " + filtroUtente;
      //Inserimento filtro su profilo attivo
      if (profiloWeb != null && !"".equals(profiloWeb))
        filtro+=" and torn.profiloweb=" +  profiloWeb;
      //Inserimento filtro tipo gara
      if (filtroTipoGara != null && !"".equals(filtroTipoGara))
        filtro+=" and " +  filtroTipoGara;
      //Filtri sul genere della gara
      if (!visualizzazioneGareALotti)
        filtro += " and genere <>1";
      if (!visualizzazioneGareLottiOffUnica)
        filtro += " and genere <>3";
      if (!visualizzazioneGareALottoUnico)
        filtro += " and genere <>2";
      if (filtroUffint != null && !"".equals(filtroUffint))
        filtro += " and  torn.CENINT = '" + filtroUffint + "'";
      break;
    case "2":
      entita = "v_gare_eleditte";
      chiave = "codice";
      codgar="codgar";
      genere="10";
      gartel="2";
      //Inserimento filtro sull'utente
      if (filtroUtente != null && !"".equals(filtroUtente))
        filtro += " and " + filtroUtente;
      if (filtroUffint != null && !"".equals(filtroUffint))
        filtro += " and torn.cenint = '" + filtroUffint + "'";
      break;
    case "3":
      entita = "v_gare_catalditte";
      chiave = "codice";
      codgar="codgar";
      genere="20";
      gartel="2";
      //Inserimento filtro sull'utente
      if (filtroUtente != null && !"".equals(filtroUtente))
        filtro += " and " + filtroUtente;
      if (filtroUffint != null && !"".equals(filtroUffint))
        filtro += " and torn.cenint = '" + filtroUffint + "'";
      break;
    case "5":
      entita = "gareavvisi";
      chiave = "ngara";
      codgar="codgar";
      genere="11";
      gartel="2";
      //Inserimento filtro sull'utente
      if (filtroUtente != null && !"".equals(filtroUtente))
        filtro += " and " + filtroUtente;
      if (filtroUffint != null && !"".equals(filtroUffint))
        filtro += " and torn.cenint = '" + filtroUffint + "'";
      if (UtilityTags.checkProtection(pageContext, "FUNZ.VIS.ALT.GARE.GAREAVVISI.filtroAvvisiGenerali", false)) {
        filtro += " and gareavvisi.tipogen is not null";
      } else {
        filtro +=" and gareavvisi.tipogen is null";
      }
      break;
    case "10":
      entita = "g1stipula";
      chiave = "codstipula";
      codgar="id";
      if (filtroUtente != null && !"".equals(filtroUtente))
          filtro += " and " + filtroUtente;
      if (StringUtils.isNotBlank(filtroUffint)) {
        filtro += " and torn.cenint = '" + filtroUffint + "'";
      }
      break;
    }
    filtro = StringUtils.replace(filtro, "ENTITA", entita);
    filtro = StringUtils.replace(filtro, "CHIAVE", chiave);
    filtro = StringUtils.replace(filtro, "CODGAR", codgar);

    if("count".equals(tipoOperazione)){
     try {
        Long conteggio = (Long)sqlManager.getObject(filtro, null);

        if (conteggio==null)
          result="";
        else if(conteggio >0)
          result = conteggio.toString();

      } catch (SQLException e) {
        logger.error("Errore durante il conteggio dei dati delle archiviazioni da eseguire",e);
        throw new JspException(
            "Errore durante il conteggio dei dati delle archiviazioni da eseguire", e);
      }
    }else{
      try {
        filtro += " order by chiave_entita";
        List lista = sqlManager.getListVector(filtro, null);
        List newLista = new ArrayList();
        if(lista!=null && lista.size()>0){
          Vector datiRiga = new Vector(6);
          for(int i=0;i<lista.size(); i++){
            datiRiga = new Vector();     
            Long idconfi = null;
            if ("10".equals(profilo)) {
              Long idstipula = SqlManager.getValueFromVectorParam(lista.get(i), 0).longValue();
              idconfi=gestioneWSDMManager.getWsdmConfigurazioneFromIdStipula(idstipula, "PG");
              datiRiga.add(0, idstipula.toString());
            }
            else {
              String codgar1 = SqlManager.getValueFromVectorParam(lista.get(i), 0).stringValue();
              idconfi=gestioneWSDMManager.getWsdmConfigurazioneFromCodgar(codgar1, "PG");
              datiRiga.add(0, codgar1);
            }
            String codice = SqlManager.getValueFromVectorParam(lista.get(i), 1).stringValue();
            datiRiga.add(1,codice);
            datiRiga.add(2,SqlManager.getValueFromVectorParam(lista.get(i), 2).stringValue());

            if ("1".equals(profilo)) {
              genere = SqlManager.getValueFromVectorParam(lista.get(i), 3).longValue().toString();
              gartel = SqlManager.getValueFromVectorParam(lista.get(i), 4).stringValue();
            }
            datiRiga.add(3,genere);
            datiRiga.add(4,gartel);
            
           
           
            datiRiga.add(5,idconfi);

            newLista.add(datiRiga);
          }
        }

        pageContext.getRequest().setAttribute("risultatoListaArchiviazioniDaEseguire", newLista);
      } catch (SQLException e) {
        logger.error("Errore nell'estrazione dei dati delle archiviazioni da eseguire",e);
        throw new JspException(
            "Errore nell'estrazione dei dati delle archiviazioni da eseguire", e);
      } catch (GestoreException e) {
        logger.error("Errore nell'estrazione dei dati delle archiviazioni da eseguire",e);
        throw new JspException(
            "Errore nell'estrazione dei dati delle archiviazioni da eseguire", e);
      }
    }


    return result;
  }

}