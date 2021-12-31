/*
 * Created on 067/10/11
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
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Viene letto il tabellato 'A1z03' di tab2 per costruire il filtro
 * su tipgar per la lista delle gare
 *
 * @author Marcello Caminiti
 */
public class GetComunicazioniDaLeggereFunction extends AbstractFunzioneTag {

  public GetComunicazioniDaLeggereFunction(){
    super(3, new Class[]{PageContext.class,String.class, String.class});
  }


  /**
   * Viene costruito il filtro per le comunicazioni da leggere.
   * Il secondo parametro indica il tipo di operazione (count\sel).
   * Il secondo parametro indica il tipo di profilo, i valori sono:
   *          1 - Gare
   *          2 - Elenchi
   *          3 - Cataloghi
   *          4 - Ricerche di mercato
   *          5 - Avvisi
   *          6 - Protocollo
   *          7 - Affidamenti
   *          8 -
   *          9 - Ordini NSO
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
    String filtroProfilo = null;
    boolean visualizzazioneGareALotti = false;
    boolean visualizzazioneGareLottiOffUnica = false;
    boolean visualizzazioneGareALottoUnico = false;

    HttpSession sessione = pageContext.getSession();
    filtroUffint = (String)sessione.getAttribute("uffint");

    if("1".equals(profilo)){
      //Profilo Gare
      filtroUtente = (String)pageContext.getRequest().getAttribute("filtroLivelloUtente");
      profiloWeb = (String)sessione.getAttribute("filtroProfiloAttivo");
      filtroTipoGara = (String)pageContext.getRequest().getAttribute("filtroTipoGara");
      visualizzazioneGareALotti = ((Boolean)pageContext.getRequest().getAttribute("visualizzazioneGareALotti")).booleanValue();
      visualizzazioneGareLottiOffUnica = ((Boolean)pageContext.getRequest().getAttribute("visualizzazioneGareLottiOffUnica")).booleanValue();
      visualizzazioneGareALottoUnico = ((Boolean)pageContext.getRequest().getAttribute("visualizzazioneGareALottoUnico")).booleanValue();
    }else if("8".equals(profilo)){
      filtroUtente = (String)pageContext.getRequest().getAttribute("filtroLivelloUtente");
      profiloWeb = (String)sessione.getAttribute("filtroProfiloAttivo");
      filtroUffint = (String)pageContext.getRequest().getAttribute("filtroUffint");
      filtroProfilo = (String)pageContext.getRequest().getAttribute("filtroProfilo");
    }else if("2".equals(profilo)){
      filtroUtente = (String)pageContext.getRequest().getAttribute("filtroLivelloUtenteElencoOperatori");
    }else if("3".equals(profilo)){
      filtroUtente = (String)pageContext.getRequest().getAttribute("filtroLivelloUtenteCataloghi");
    }else if("4".equals(profilo)){
      filtroUtente = (String)pageContext.getRequest().getAttribute("filtroLivelloRicercheMercato");
    }else if("5".equals(profilo)){
      filtroUtente = (String)pageContext.getRequest().getAttribute("filtroLivelloUtenteAvvisi");
    }else if("6".equals(profilo)){
      filtroUtente = (String)pageContext.getRequest().getAttribute("filtroLivelloUtenteProtocollo");
    }else if("7".equals(profilo)){
      filtroUtente = (String)pageContext.getRequest().getAttribute("filtroLivelloUtenteService");
    }else if("9".equals(profilo)){
      filtroUtente = (String)pageContext.getRequest().getAttribute("filtroLivelloUtente");
      profiloWeb = (String)sessione.getAttribute("filtroProfiloAttivo");
      filtroUffint = (String)pageContext.getRequest().getAttribute("filtroUffint");
      filtroProfilo = (String)pageContext.getRequest().getAttribute("filtroProfilo");
    }
    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
            pageContext, PgManager.class);

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    if("count".equals(tipoOperazione))
      filtro = "select count(w_invcom.idprg) ";
    else{
      //Poichè il campo comdatins contiene anche l'orario per poterlo visualizzare correttamente nella jsp lo devo trasformare in stringa
      String dbFunctionDateToString = sqlManager.getDBFunction("DATETIMETOSTRING",
          new String[] { "w_invcom.comdatins" });
      filtro = "select w_invcom.idprg, w_invcom.idcom, w_invcom.comkey1, w_puser.userdesc, w_invcom.commsgogg, " + dbFunctionDateToString + ", w_invcom.comkey2 ";
      if("9".equals(profilo)) {
        filtro += ", nso.codord ";
      }
    }

    if("8".equals(profilo) && "count".equals(tipoOperazione)){
      filtro = "select count(distinct idcom) ";
    }else if("8".equals(profilo) && "sel".equals(tipoOperazione)){
      filtro += ",v_gare_profilo.codprofilo, v_gare_profilo.cenint, v_gare_profilo.genere ";
    }

    filtro += pgManager.getFiltroComunicazioneDaLeggere(profilo, filtroUtente, filtroTipoGara, visualizzazioneGareALotti, visualizzazioneGareLottiOffUnica,
        visualizzazioneGareALottoUnico, profiloWeb,tipoOperazione, filtroUffint, filtroProfilo);

    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        pageContext, PgManagerEst1.class);

    filtro += pgManagerEst1.getFiltroComunicazioniSoccorsoIstruttorio(profilo);

    if("count".equals(tipoOperazione)){
     try {
        Long conteggio = (Long)sqlManager.getObject(filtro, null);
        if (conteggio==null)
          conteggio = new Long(0);
        result = conteggio.toString();
      } catch (SQLException e) {
        throw new JspException(
            "Errore durante il conteggio delle comunicazioni da leggere ", e);
      }
    }else{
      try {
        filtro += " order by w_invcom.comdatins";
        List listaComunicazioni = sqlManager.getListVector(filtro, null);
        List newListaComunicazione = new ArrayList();
        if(listaComunicazioni!=null && listaComunicazioni.size()>0){
          Vector datiRiga = new Vector(10);
          String dataInserimento = null;
          Timestamp comdatinsTimestamp = null;
          Date comdatinsDate = null;
          for(int i=0;i<listaComunicazioni.size(); i++){
            datiRiga = new Vector();
            datiRiga.add(0, SqlManager.getValueFromVectorParam(listaComunicazioni.get(i), 0).stringValue()); ;
            datiRiga .add(1,SqlManager.getValueFromVectorParam(listaComunicazioni.get(i), 1).longValue());
            datiRiga .add(2,SqlManager.getValueFromVectorParam(listaComunicazioni.get(i), 2).stringValue());
            datiRiga .add(3, SqlManager.getValueFromVectorParam(listaComunicazioni.get(i), 3).stringValue());
            datiRiga .add(4, SqlManager.getValueFromVectorParam(listaComunicazioni.get(i), 4).stringValue());
            dataInserimento = SqlManager.getValueFromVectorParam(listaComunicazioni.get(i), 5).stringValue();
            datiRiga .add(5, dataInserimento);
            datiRiga .add(6, SqlManager.getValueFromVectorParam(listaComunicazioni.get(i), 6).stringValue());

            if(dataInserimento!=null && !"".equals(dataInserimento)){
              comdatinsDate = UtilityDate.convertiData(dataInserimento, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
              comdatinsTimestamp = new Timestamp(comdatinsDate.getTime());
            }else
              comdatinsTimestamp = null;

            datiRiga .add(7,comdatinsTimestamp);
            if("8".equals(profilo)){
              datiRiga .add(8, SqlManager.getValueFromVectorParam(listaComunicazioni.get(i), 7).stringValue());
              datiRiga .add(9, SqlManager.getValueFromVectorParam(listaComunicazioni.get(i), 8).stringValue());
              datiRiga .add(10, SqlManager.getValueFromVectorParam(listaComunicazioni.get(i), 9).longValue());
            }
            if("9".equals(profilo)) {
              datiRiga .add(8, SqlManager.getValueFromVectorParam(listaComunicazioni.get(i), 7).stringValue());
            }
            newListaComunicazione.add(datiRiga);
          }
        }

        pageContext.getRequest().setAttribute("risultatoListaComunicazioni", newListaComunicazione);
      } catch (SQLException e) {
        throw new JspException(
            "Errore nell'estrazione dei dati delle comunicazioni da leggere ", e);
      } catch (GestoreException e) {
        throw new JspException(
            "Errore nell'estrazione dei dati delle comunicazioni da leggere ", e);
      }
    }


    return result;
  }

}