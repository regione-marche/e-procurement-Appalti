/*
 * Created on 04-04-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che elabora i filtri impostati nelle pagine
 * popup-trova-filtroDitte.jsp e popup-trova-filtroCategorie.jsp  e popup-trova-filtroNominativi.jsp
 *  per impostare la where da inserire * nella variabili di sessione
 *   "filtroDitte" e "filtroCategorie" e "filtroNominativi"
 *
 * @author Marcello Caminiti
 */

public class InitFiltroListaSessioneFunction extends AbstractFunzioneTag {


  public InitFiltroListaSessioneFunction() {
    super(4, new Class[]{PageContext.class, String.class, String.class, String.class});
  }

  @Override
  /**
   * Viene passato come parametro un valore che indica quale filtro sbiancare:
   *    1  Tutti i filtri
   *    2  Solo il filtro su filtroDitte
   *    3  Solo il filtro su filtroCategorie
   *    4  Solo il filtro su filtroNominativi
   *
   * @author Marcello Caminiti
   */
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String parametro = (String) params[1];
    String tipoChiamante = (String) params[2];
    String entita = (String) params[3];
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    if (entita != null && !"".equals(entita) && !sqlManager.isTable(entita)) {
        throw new JspException("Errore di validazione");
    }

    HttpSession sessione = pageContext.getSession();
    //String trovaAddWhere = UtilityTags.getParametro(pageContext,UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA);
    //String trovaParameter = UtilityTags.getParametro(pageContext,UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA);
    String trovaAddWhere = UtilityTags.getAttributeForSqlBuild(sessione, entita, UtilityTags.getNumeroPopUp(pageContext), "trovaAddWhere");
    String trovaParameter = UtilityTags.getAttributeForSqlBuild(sessione, entita, UtilityTags.getNumeroPopUp(pageContext), "trovaParameter");
    String filtro = UtilityTags.getAttributeForSqlBuild(sessione, entita, UtilityTags.getNumeroPopUp(pageContext), "filtro");

    if ("1".equals(parametro)) {
      sessione.removeAttribute("filtroDitte");
      sessione.removeAttribute("trovaDITG");
      sessione.removeAttribute("filtroCategorie");
      sessione.setAttribute("filtroDitteLocale", "si");
      sessione.removeAttribute("filtroAppalti");
      sessione.removeAttribute("trovaANTICOR");
      sessione.removeAttribute("filtroComunicazioniIn");
      sessione.removeAttribute("filtroComunicazioniOut");
      sessione.removeAttribute("filtroLotti");
    } else if ("2".equals(parametro)) {
      sessione.removeAttribute("filtroDitte");
      sessione.setAttribute("filtroDitteLocale", "si");
    } else if ("3".equals(parametro)) {
      sessione.removeAttribute("filtroCategorie");
    } else if ("4".equals(parametro)) {
      sessione.removeAttribute("filtroNominativi");
    } else if ("5".equals(parametro)) {
      sessione.removeAttribute("filtroComunicazioniOut");
      sessione.removeAttribute("trovaW_INVCOM");
    } else if ("6".equals(parametro) ) {
      sessione.removeAttribute("filtroComunicazioniIn");
      sessione.removeAttribute("trovaW_INVCOM");
    } else if ("7".equals(parametro) ) {
      sessione.removeAttribute("filtroLotti");
    }  else if ("8".equals(parametro) ) {
      sessione.removeAttribute("filtroValutazione");
    }else if (StringUtils.isNotEmpty((trovaAddWhere))) {

      if (trovaParameter!=null && !"".equals(trovaParameter)) {
        String parametriElaborati = StringUtils.replace(trovaParameter, "\\;", "çç");
        String[] parametri = parametriElaborati.split(";");
        int len = parametri.length;

        for (int i=0; i<len; i++) {
          parametri[i] =  StringUtils.replace(parametri[i], "çç", "\\;" );
          //La stringa ha una struttura fissa T:valore, dove il primo carattere è sempre il tipo, il secondo è sempre :
          String tipo = parametri[i].substring(0,1);
          String valore = parametri[i].substring(2);
          if ("T".equals(tipo)) {
            boolean sostituzioneCarattere = false;
            if (valore.indexOf("$") > 0) {
              valore = StringUtils.replace(valore, "$", "_ç°ç_");
              sostituzioneCarattere = true;
            }
            //Se nella stringa è presente il carattere ' va raddoppiato
            valore = valore.replace("'", "''");
            trovaAddWhere = trovaAddWhere.replaceFirst("\\?", "'" + valore +"'");

            if (sostituzioneCarattere)
              trovaAddWhere = StringUtils.replace(trovaAddWhere, "_ç°ç_", "$");
          } else if("N".equals(tipo) || "F".equals(tipo)) {
            trovaAddWhere = trovaAddWhere.replaceFirst("\\?", valore);

          } else if("D".equals(tipo)) {
            String dbFunctionStringToDate = sqlManager.getDBFunction("stringtodate",
                new String[] { valore });
            trovaAddWhere = trovaAddWhere.replaceFirst("\\?", dbFunctionStringToDate);
          }
        }
      }

      if (trovaAddWhere.indexOf("V_CAIS_TIT") >= 0) {
        //sessione.removeAttribute("filtroDitte");
        sessione.setAttribute("filtroCategorie", trovaAddWhere);
      } else if(trovaAddWhere.indexOf("ANTICORLOTTI") >= 0) {
    	/*
        String trovaAddWhere1 = UtilityTags.getParametro(pageContext,UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA);
    	String trovaParameter1 = UtilityTags.getParametro(pageContext,UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA);

    	HashMap<String,String> hash = new HashMap<String,String>();
    	hash.put("trovaAddWhere", trovaAddWhere1);
    	hash.put("trovaParameter", trovaParameter1);
    	sessione.setAttribute("filtroAppalti", hash);
    	*/
        sessione.setAttribute("filtroAppalti", " and " + trovaAddWhere);
      } else if(trovaAddWhere.indexOf("COMMNOMIN") >= 0 || "COMMNOMIN".equals(entita)) {
        sessione.setAttribute("filtroNominativi", " and " + trovaAddWhere);
      } else if(trovaAddWhere.indexOf("W_INVCOM") >= 0) {
        // ho 2 oggetti in sessione per le comunicazioni, i filtri per le comunicazioni ricevute
    	// e quelli per le inviate, per cui viene iniettata nella condizione di filtro una
    	// condizione sempre vera ma che funge da discriminante
        if (trovaAddWhere.indexOf("1=1") >= 0) {
          sessione.setAttribute("filtroComunicazioniIn", trovaAddWhere);
        } else if (trovaAddWhere.indexOf("2=2") >= 0) {
          sessione.setAttribute("filtroComunicazioniOut", trovaAddWhere);
        }
      } else {
        if (trovaAddWhere.indexOf("GARE") >= 0) {
          sessione.setAttribute("filtroLotti", " and " + trovaAddWhere);
        }else if (trovaAddWhere.indexOf("COEFFI") >= 0){
          pageContext.setAttribute("filtroValutazione", " and " + trovaAddWhere);
          sessione.removeAttribute("filtroDitte");
          sessione.removeAttribute("trovaDITG");
        }else{
          sessione.setAttribute("filtroDitte", " and " + trovaAddWhere);
          sessione.setAttribute("filtroDitteLocale", "si");
        }
        //sessione.removeAttribute("filtroCategorie");
      }
    } else if ("Valutazione".equals(tipoChiamante) && StringUtils.isNotEmpty((filtro))) {
      pageContext.setAttribute("filtroValutazione", " and " + filtro);
      sessione.removeAttribute("filtroDitte");
      sessione.removeAttribute("trovaDITG");
    } else {
      if ("Ditte".equals(tipoChiamante)) {
        sessione.removeAttribute("filtroDitte");
        sessione.setAttribute("filtroDitteLocale", "si");
      }
      if ("Lotti".equals(tipoChiamante)) {
        sessione.removeAttribute("filtroLotti");
      }
      if ("Valutazione".equals(tipoChiamante)) {
        sessione.removeAttribute("filtroValutazione");
        sessione.removeAttribute("filtroDitte");
        sessione.setAttribute("filtroDitteLocale", "si");
      }
      if ("Cat".equals(tipoChiamante))
        sessione.removeAttribute("filtroCategorie");
      if ("Anticor".equals(tipoChiamante))
        sessione.removeAttribute("filtroAppalti");
      if ("Nominativi".equals(tipoChiamante))
        sessione.removeAttribute("filtroNominativi");
    }

    return null;
  }
}
