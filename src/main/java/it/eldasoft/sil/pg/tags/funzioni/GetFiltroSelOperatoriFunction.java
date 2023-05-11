/*
 * Created on 23/11/11
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
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Funzione che costruisce il filtro per la pagina gare-popup-selOpEconomici.jsp.
 * Inoltre viene creata una table html per visualizzare la lista delle categorie
 * impostate come filtro
 *
 * @author Marcello Caminiti
 */
public class GetFiltroSelOperatoriFunction extends AbstractFunzioneTag {

  protected static final String REGEXP = "^[a-zA-Z0-9-_\\\\./ \\\\$@]+$";

  public GetFiltroSelOperatoriFunction() {
    super(1, new Class[] { PageContext.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    HttpSession sessione = pageContext.getSession();

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        pageContext, PgManager.class);

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    EsisteClassificaCategoriaFunction esisteClassificaCategoria = new EsisteClassificaCategoriaFunction();
    String esisteClassificaForniture = esisteClassificaCategoria.function(pageContext, new Object[]{pageContext,"TAB1","G_035"});
    String esisteClassificaServizi = esisteClassificaCategoria.function(pageContext, new Object[]{pageContext,"TAB1","G_036"});
    String esisteClassificaLavori150 = esisteClassificaCategoria.function(pageContext, new Object[]{pageContext,"TAB1","G_037"});
    String esisteClassificaServiziProfessionali = esisteClassificaCategoria.function(pageContext, new Object[]{pageContext,"TAB1","G_049"});

    String where="";
    String entita = "";
    String categoriaPrev = pageContext.getRequest().getParameter("categoriaPrev");
//    String garaElenco = pageContext.getRequest().getParameter("garaElenco");
    String classifica = pageContext.getRequest().getParameter("classifica");
    String garaElenco= "";
    String ngara = pageContext.getRequest().getParameter("ngara");
    String filtroUltCategorie = (String)sessione.getAttribute("filtro");
    String filtroSpecifico = (String)sessione.getAttribute("filtroSpecifico");
    String filtroZoneAtt = (String)sessione.getAttribute("filtroZoneAtt");
    String filtroAffidatariEsclusi = (String)sessione.getAttribute("filtroAffidatariEsclusi");
    String filtroSpecificoObbl = "";
    String elencoMsgFiltriSpecificiObbl = "";
    String elencoIdFiltriSpecificiObbl = "";
    boolean isFiltroUltCategorie = false;
    boolean isFiltroSpecifico = false;
    boolean isFiltroZoneAtt = false;
    boolean isFiltroAffidatariEsclusi = false;
    StringBuffer html= new StringBuffer("");
    StringBuffer fo_html= new StringBuffer("");
    StringBuffer fa_html= new StringBuffer("");
    StringBuffer za_html= new StringBuffer("");
    StringBuffer ae_html= new StringBuffer("");
    String filtroCategoria= "";
    String filtriUlterioriObbl= "";
    String filtriUlteriori= "";
    String filtriZone= "";
    String filtriAffidatariEsclusi= "";
    String elencoNumcla = (String)sessione.getAttribute("elencoNumcla");
    String arrayElencoNumcla[] = null;
    String prevalenteSelezionata = (String)sessione.getAttribute("prevalenteSelezionata");
    String tipoalgo =  pageContext.getRequest().getParameter("tipoalgo");
//    String stazioneAppaltante =  pageContext.getRequest().getParameter("stazioneAppaltante");
//    String tipoGara = pageContext.getRequest().getParameter("tipoGara");
    String stazioneAppaltante =  "";
    String tipoGara = "";
    Boolean applicatoFiltroInOr = (Boolean)sessione.getAttribute("applicatoFiltroInOr");
    if(applicatoFiltroInOr==null)
      applicatoFiltroInOr = new Boolean(false);

    //Controlli per Sql injection
    // classifica deve essere un numero
    if(classifica!=null && !"".equals(classifica)) {
      try {
        new Long(classifica);
      }catch(NumberFormatException e) {
        throw new JspException(
            "Errore di validazione dei parametri");
      }
    }
    if(categoriaPrev!= null && !"".equals(categoriaPrev) && !categoriaPrev.matches(REGEXP)) {
      throw new JspException(
          "Errore di validazione dei parametri");
    }


    if(filtroUltCategorie!= null && !"".equals(filtroUltCategorie))
      isFiltroUltCategorie = true;

    if(filtroSpecifico!= null && !"".equals(filtroSpecifico))
      isFiltroSpecifico = true;

    if(filtroZoneAtt!= null && !"".equals(filtroZoneAtt))
      isFiltroZoneAtt = true;

    if(filtroAffidatariEsclusi!= null && !"".equals(filtroAffidatariEsclusi))
      isFiltroAffidatariEsclusi = true;

    if(elencoNumcla!=null && !",".equals(elencoNumcla)){
      arrayElencoNumcla = elencoNumcla.split(",");
      if(arrayElencoNumcla[0]!=null)
        classifica = arrayElencoNumcla[0];
      else
        classifica = null;
    }

    try {
      Vector<?> datiGara = sqlManager.getVector("select g.elencoe, c.catiga, c.numcla, t.cenint, t.tipgen from gare g left join catg c on g.ngara=c.ngara "
          + "left join torn t on g.codgar1 = t.codgar where g.ngara = ?", new Object[] {ngara});
      if(datiGara!=null && datiGara.size()>0){
        if(pageContext.getRequest().getParameter("garaElenco")!=null && !pageContext.getRequest().getParameter("garaElenco").isEmpty())
        garaElenco   =      SqlManager.getValueFromVectorParam(datiGara, 0).getValue().toString();

        if(pageContext.getRequest().getParameter("stazioneAppaltante")!=null && !pageContext.getRequest().getParameter("stazioneAppaltante").isEmpty())
        stazioneAppaltante= SqlManager.getValueFromVectorParam(datiGara, 3).getValue().toString();

        if(pageContext.getRequest().getParameter("tipoGara")!=null &&  !pageContext.getRequest().getParameter("tipoGara").isEmpty())
        tipoGara  =         SqlManager.getValueFromVectorParam(datiGara, 4).getValue().toString();
      }else {
        throw new JspException(
            "Errore durante il prelievo delle informazioni della gara");
      }
     }
     catch(SQLException ex) {
       throw new JspException(
           "Errore durante la validazione", ex);
     }

    String condizioneIn = "(select DITTAO from DITG,GARE where GARE.NGARA='" + ngara + "' and  GARE.NGARA=DITG.NGARA5 and GARE.CODGAR1=DITG.CODGAR5)";
    if(categoriaPrev!=null && !"".equals(categoriaPrev)){
      entita = "V_DITTE_ELECAT";
      if("8".equals(tipoalgo) || "9".equals(tipoalgo))
        entita = "V_DITTE_ELECAT_SA";

      Long tipoCatPrev = this.getTipoCategoria(categoriaPrev, sqlManager);
      if(!applicatoFiltroInOr.booleanValue()) {

        if((prevalenteSelezionata == null) || (prevalenteSelezionata!=null && "si".equals(prevalenteSelezionata))){
          where = entita + ".GARA = '" + garaElenco + "' ";
          where+= " and " + entita + ".CODCAT = '" + categoriaPrev + "'";
          if("V_DITTE_ELECAT_SA".equals(entita))
            where+= " and " + entita + ".CENINT = '" + stazioneAppaltante + "'";
          if(classifica != null && !"".equals(classifica)){
            where += " and (" + entita + ".NUMCLASS = " + classifica + " or " + entita + ".NUMCLASS is null)";
          }else{
            where += " and (" + entita + ".NUMCLASS = (select min(a.NUMCLASS) from iscrizclassi a where a.NGARA = '" + garaElenco + "' and a.CODCAT = '" + categoriaPrev + "'";
            where += " and a.codimp = " + entita + ".codice) or " + entita + ".NUMCLASS is null)";
          }
        }
      }

      if(isFiltroUltCategorie){
        where += filtroUltCategorie;
      }
      where += " and " + entita + ".CODICE not in" + condizioneIn;

      String descClassifica="";

      if((prevalenteSelezionata == null) || (prevalenteSelezionata!=null && "si".equals(prevalenteSelezionata))){
        html.append("<b>");
        html.append(categoriaPrev);
        html.append("</b> - ");
        String descrizioneCategoria = this.getDescrizioneCategoria(categoriaPrev, sqlManager);
        html.append(descrizioneCategoria);
        html.append("&nbsp;&nbsp;&nbsp;&nbsp;");
        filtroCategoria = categoriaPrev + " - " + descrizioneCategoria;
        if("1".equals(tipoCatPrev.toString()) || ("2".equals(tipoCatPrev.toString()) && "true".equals(esisteClassificaForniture))
             || ("3".equals(tipoCatPrev.toString()) && "true".equals(esisteClassificaServizi)) || ("4".equals(tipoCatPrev.toString()) && "true".equals(esisteClassificaLavori150))
             || ("5".equals(tipoCatPrev.toString()) && "true".equals(esisteClassificaServiziProfessionali))){
          html.append("- ");
          html.append("Classifica: <b>");
          descClassifica = "";
          if(classifica!=null && !"".equals(classifica))
            descClassifica = pgManager.getDescCampoClassifica(classifica, tipoCatPrev.toString());
          html.append(descClassifica);
          html.append("</b>");
          filtroCategoria+= " - classifica: " + descClassifica;
        }

        html.append("<br>\n");
      }
      //Informazioni sulle categorie ulteriori
      if(isFiltroUltCategorie){
        String elencoUlterioriCategorie = (String)sessione.getAttribute("elencoUlterioriCategorie");
        String elencoTiplavgUltCategorie = (String)sessione.getAttribute("elencoTiplavgUltCategorie");
        String arrayElencoUlterioriCategorie[] = elencoUlterioriCategorie.split(",");
        String arrayTiplavgUltCategorie[] = elencoTiplavgUltCategorie.split(",");
        for (int i=0;i<arrayElencoUlterioriCategorie.length;i++){
          html.append("<b>");
          html.append(arrayElencoUlterioriCategorie[i]);
          html.append("</b> - ");
          String descCategoria = this.getDescrizioneCategoria(arrayElencoUlterioriCategorie[i], sqlManager);
          html.append(descCategoria);
          html.append("&nbsp;&nbsp;&nbsp;&nbsp;");
          filtroCategoria+= "\n" + arrayElencoUlterioriCategorie[i] + " - " + descCategoria;
          if("1".equals(arrayTiplavgUltCategorie[i])
              || ("2".equals(arrayTiplavgUltCategorie[i]) && "true".equals(esisteClassificaForniture))
              || ("3".equals(arrayTiplavgUltCategorie[i]) && "true".equals(esisteClassificaServizi))
              || ("4".equals(arrayTiplavgUltCategorie[i]) && "true".equals(esisteClassificaLavori150))
              || ("5".equals(arrayTiplavgUltCategorie[i]) && "true".equals(esisteClassificaServiziProfessionali))){
            html.append("- ");
            html.append("Classifica: <b>");
            if(arrayElencoNumcla[i+1]!=null)
              classifica= arrayElencoNumcla[i+1];
            else
              classifica= null;
            if(classifica!= null && !" ".equals(classifica))
              descClassifica = pgManager.getDescCampoClassifica(classifica, arrayTiplavgUltCategorie[i]);
            else
              descClassifica="";
            html.append(descClassifica);
            html.append("</b>");
            filtroCategoria+= " - classifica: " + descClassifica;
          }
          html.append("<br>\n");
        }
      }
      if(elencoNumcla==null){
        elencoNumcla= classifica + ",";
        sessione.setAttribute("elencoNumcla", elencoNumcla);
      }
    }else{
      entita = "V_DITTE_ELESUM";
      if("8".equals(tipoalgo) || "9".equals(tipoalgo))
        entita = "V_DITTE_ELECAT_SA";
      where=entita + ".GARA = '" + garaElenco + "'";
      if("V_DITTE_ELECAT_SA".equals(entita)){
        where+= " and " + entita + ".CODCAT = '0'";
        where+= " and " + entita + ".CENINT = '" + stazioneAppaltante + "'";
        where+= " and " + entita + ".TIPCAT = " + tipoGara ;

      }
      where+=" and " + entita +".CODICE not in " + condizioneIn;
      html.append("nessuna categoria specificata<br>\n");
    }

    //Informazioni su filtro specifico
    tipoGara = UtilityStringhe.convertiNullInStringaVuota(tipoGara);
    String tipoelecases = "";
    if(!"".equals(tipoGara)){
      if ("1".equals(tipoGara)) {
        tipoelecases="'100','110','101','111'";
       } else if ("2".equals(tipoGara)) {
        tipoelecases="'10','110','11','111'";
       } else if ("3".equals(tipoGara)) {
        tipoelecases="'1','11','101','111'";
      }
    }

    String gEle = "%|" + garaElenco + "|%";
    try {
      int countFiltroZoneAttivita = 0;
      int countFiltroUltCat = 0;
      int countFiltroSpecifico = 0;
      int countFiltroSpecificoObbl = 0;
      int countFiltroEsclAffUsc = 0;

      List<?> listaFiltriSpecifici = sqlManager.getListVector("select tipofiltro,filtroatt,filtroman,queryfiltro,msgfiltro,id from G1FILTRIELE" +
      		" where tipofiltro is not null and (((applicaele is null or applicaele like ? )" +
      		" and (tipoele is null or tipoele in (" + tipoelecases + ")) and tipofiltro = 1)" +
			" or tipofiltro >1)", new Object[] { gEle });
      if (listaFiltriSpecifici != null && listaFiltriSpecifici.size() > 0) {
        for (int i = 0; i < listaFiltriSpecifici.size(); i++) {
          Long tipofiltro = (Long) SqlManager.getValueFromVectorParam(listaFiltriSpecifici.get(i), 0).getValue();
          String filtroatt = (String) SqlManager.getValueFromVectorParam(listaFiltriSpecifici.get(i), 1).getValue();
          filtroatt = UtilityStringhe.convertiNullInStringaVuota(filtroatt);
          String filtroman = (String) SqlManager.getValueFromVectorParam(listaFiltriSpecifici.get(i), 2).getValue();
          filtroman = UtilityStringhe.convertiNullInStringaVuota(filtroman);
          String queryfiltro = (String) SqlManager.getValueFromVectorParam(listaFiltriSpecifici.get(i), 3).getValue();
          queryfiltro = UtilityStringhe.convertiNullInStringaVuota(queryfiltro);
          String msgfiltro = (String) SqlManager.getValueFromVectorParam(listaFiltriSpecifici.get(i), 4).getValue();
          Long idfiltro = (Long) SqlManager.getValueFromVectorParam(listaFiltriSpecifici.get(i), 5).getValue();
          //mappatura fissa per tipofiltro
          if(new Long(4).equals(tipofiltro) && "1".equals(filtroatt)){
            countFiltroEsclAffUsc = countFiltroEsclAffUsc + 1;
          }
          if(new Long(3).equals(tipofiltro) && "1".equals(filtroatt)){
            countFiltroZoneAttivita = countFiltroZoneAttivita + 1;
          }
          if(new Long(2).equals(tipofiltro) && "1".equals(filtroatt)){
            countFiltroUltCat = countFiltroUltCat + 1;
          }
          if(new Long(1).equals(tipofiltro) && ("1".equals(filtroatt) && !"1".equals(filtroman))){
            countFiltroSpecifico = countFiltroSpecifico + 1;
          }
          if(new Long(1).equals(tipofiltro) && ("1".equals(filtroman))){
              countFiltroSpecificoObbl = countFiltroSpecificoObbl + 1;
              filtroSpecificoObbl += " and " + queryfiltro;
              if(!"".equals(elencoIdFiltriSpecificiObbl)) {
            	  elencoMsgFiltriSpecificiObbl+=",";
            	  elencoIdFiltriSpecificiObbl+=",";
              }
        	  elencoMsgFiltriSpecificiObbl+=msgfiltro;
        	  elencoIdFiltriSpecificiObbl+=idfiltro;
          }

        }
      }

      if(countFiltroZoneAttivita > 0){
        pageContext.setAttribute("showFiltroZoneAttivita", "1", PageContext.REQUEST_SCOPE);
      }
      if(countFiltroUltCat > 0){
        pageContext.setAttribute("showFiltroUltCat", "1", PageContext.REQUEST_SCOPE);
      }
      if(countFiltroSpecifico > 0){
        pageContext.setAttribute("showFiltroSpecifico", "1", PageContext.REQUEST_SCOPE);
      }
      if(countFiltroSpecificoObbl > 0){
          pageContext.setAttribute("showFiltroSpecificoObbl", "1", PageContext.REQUEST_SCOPE);
      }
      if(countFiltroEsclAffUsc > 0){
        pageContext.setAttribute("showFiltroEsclAffUsc", "1", PageContext.REQUEST_SCOPE);
      }


    } catch (SQLException sqle) {
      throw new JspException(
          "Errore durante la lettura dei filtri aggiuntivi dall'entita G1FILTRIELE", sqle);
    }

    if(!"".equals(filtroSpecificoObbl)){
        where += filtroSpecificoObbl;
    }

    if(isFiltroSpecifico){
      where += filtroSpecifico;
    }

    if(isFiltroZoneAtt){
      where += filtroZoneAtt;
    }

    if(isFiltroAffidatariEsclusi){
      where += filtroAffidatariEsclusi;
    }

    if(!"".equals(filtroSpecificoObbl)){
      //Contiene i messaggi di riferimento per i filtri specifici obbligatori
      String arrayElencoFiltriSpecificiObbl[] = elencoMsgFiltriSpecificiObbl.split(",");
      for (int i=0;i<arrayElencoFiltriSpecificiObbl.length;i++){
        fo_html.append(arrayElencoFiltriSpecificiObbl[i]);
        fo_html.append("&nbsp;&nbsp;&nbsp;&nbsp;");
        fo_html.append("<br>\n");
        filtriUlterioriObbl+="\n"+arrayElencoFiltriSpecificiObbl[i];
      }
    }


    if(isFiltroSpecifico){
      String elencoMsgFiltriSpecifici = (String)sessione.getAttribute("elencoMsgFiltriSpecifici");
      String arrayElencoFiltriSpecifici[] = elencoMsgFiltriSpecifici.split(",");
      for (int i=0;i<arrayElencoFiltriSpecifici.length;i++){
        fa_html.append(arrayElencoFiltriSpecifici[i]);
        fa_html.append("&nbsp;&nbsp;&nbsp;&nbsp;");
        fa_html.append("<br>\n");
        filtriUlteriori+="\n"+arrayElencoFiltriSpecifici[i];
      }
    }

    if(isFiltroZoneAtt){
      String[] elencoRegioni = new String[] { "Piemonte", "Valle d'Aosta",
          "Liguria", "Lombardia", "Friuli Venezia Giulia", "Trentino Alto Adige",
          "Veneto", "Emilia Romagna", "Toscana", "Umbria", "Marche", "Abruzzo",
          "Molise", "Lazio", "Campania", "Basilicata", "Puglia", "Calabria",
          "Sardegna", "Sicilia" };
      String elencoIdZoneAttivita = (String)sessione.getAttribute("elencoIdZoneAttivita");
      elencoIdZoneAttivita = UtilityStringhe.convertiNullInStringaVuota(elencoIdZoneAttivita);
      if("ALL".equals(elencoIdZoneAttivita)){
        za_html.append("Tutte le regioni");
        za_html.append("&nbsp;&nbsp;&nbsp;&nbsp;");
        za_html.append("<br>\n");
        filtriZone+="Tutte le regioni";
      }else{
        String arrayElencoIdZoneAttivita[] = elencoIdZoneAttivita.split(",");
        for (int i=0;i<arrayElencoIdZoneAttivita.length;i++){
          String i_pos = arrayElencoIdZoneAttivita[i];
          int k = Integer.parseInt(i_pos);
          za_html.append(elencoRegioni[k]);
          if(i != arrayElencoIdZoneAttivita.length-1){
            za_html.append(",");
          }
        }
        filtriZone+=za_html;
        za_html.append("&nbsp;&nbsp;&nbsp;&nbsp;");
        za_html.append("<br>\n");
      }
    }

    if(isFiltroAffidatariEsclusi){
      String elencoAffidatariEsclusi = (String)sessione.getAttribute("elencoAffidatariEsclusi");
      String arrayElencoAffidatariEsclusi[] = elencoAffidatariEsclusi.split(",");
      for (int i=0;i<arrayElencoAffidatariEsclusi.length;i++){
        String codAffEscl =arrayElencoAffidatariEsclusi[i];
        ae_html.append("<b>");
        ae_html.append(codAffEscl);
        ae_html.append("</b> - ");
        String descrizioneImpresa = this.getDescrizioneImpresa(codAffEscl, sqlManager);
        ae_html.append(descrizioneImpresa);
        ae_html.append("&nbsp;&nbsp;&nbsp;&nbsp;");
        ae_html.append("<br>\n");
        filtriAffidatariEsclusi+=""+codAffEscl + " - " + descrizioneImpresa;
      }
    }

    pageContext.setAttribute("entita", entita, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("tableCategorie", html, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("tableFiltriAggiuntiviObbl", fo_html, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("tableFiltriAggiuntivi", fa_html, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("tableZoneAttivita", za_html, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("tableAffidatariEsclusi", ae_html, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("filtroCategoria", filtroCategoria, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("filtriUlterioriObbl", filtriUlterioriObbl, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("elencoIdFiltriSpecificiObbl", elencoIdFiltriSpecificiObbl, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("filtriUlteriori", filtriUlteriori, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("filtriZone", filtriZone, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("filtriAffidatariEsclusi", filtriAffidatariEsclusi, PageContext.REQUEST_SCOPE);

    return where;
  }

  private String getDescrizioneCategoria(String codCategoria, SqlManager sqlManager) throws JspException{
    String desc = "";
    try {
      desc = (String)sqlManager.getObject("select descat from cais where caisim = ? ", new Object[]{codCategoria});
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura della descrizione della categoria " + codCategoria, e);
    }
    return desc;
  }

  private Long getTipoCategoria(String codCategoria, SqlManager sqlManager) throws JspException{
    Long tipo = null;
    try {
      tipo = (Long)sqlManager.getObject("select tiplavg from cais where caisim = ? ", new Object[]{codCategoria});
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura della tipologia della categoria " + codCategoria, e);
    }
    return tipo;
  }

  private String getDescrizioneImpresa(String codImpresa, SqlManager sqlManager) throws JspException{
    String desc = "";
    try {
      desc = (String)sqlManager.getObject("select nomimp from impr where codimp = ? ", new Object[]{codImpresa});
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura della descrizione della ditta  " + codImpresa, e);
    }
    return desc;
  }

}
