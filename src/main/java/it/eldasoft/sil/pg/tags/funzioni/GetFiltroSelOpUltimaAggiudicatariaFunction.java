/*
 * Created on 02/02/12
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
import it.eldasoft.gene.tags.functions.EsisteClassificaCategoriaFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che costruisce il filtro per la pagina gare-popup-selOpUltimaAgg.jsp.
 *
 * @author Marcello Caminiti
 */
public class GetFiltroSelOpUltimaAggiudicatariaFunction extends AbstractFunzioneTag {



  public GetFiltroSelOpUltimaAggiudicatariaFunction() {
    super(1, new Class[] { PageContext.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        pageContext, PgManager.class);

    String where="";
    String where1="";
    String categoriaPrev = pageContext.getRequest().getParameter("categoriaPrev");
    String garaElenco = pageContext.getRequest().getParameter("garaElenco");
    String classifica = pageContext.getRequest().getParameter("classifica");
    String ngara = pageContext.getRequest().getParameter("ngara");
    Long tipoCategoria = this.getTipoCategoria(categoriaPrev, sqlManager);

    EsisteClassificaCategoriaFunction esisteClassificaCategoria = new EsisteClassificaCategoriaFunction();
    String esisteClassificaForniture = esisteClassificaCategoria.function(pageContext, new Object[]{pageContext,"TAB1","G_035"});
    String esisteClassificaServizi = esisteClassificaCategoria.function(pageContext, new Object[]{pageContext,"TAB1","G_036"});
    String esisteClassificaLavori150 = esisteClassificaCategoria.function(pageContext, new Object[]{pageContext,"TAB1","G_037"});
    String esisteClassificaServiziProfessionali = esisteClassificaCategoria.function(pageContext, new Object[]{pageContext,"TAB1","G_049"});

    where = "ngara <> '" + ngara + "' and codgar1=codgar and (accqua != '1' or accqua is null)";
    where +=" and ditta is not null  and ((genere is null and elencoe='" + garaElenco + "' and dattoa is not null";
    where +=" and exists(select catg.ngara from catg where catg.ngara=gare.ngara and catg.ncatg=1 and catg.catiga";
    if(categoriaPrev!=null && !"".equals(categoriaPrev)){
      where +="='" + categoriaPrev + "' and catg.numcla";
      if(classifica!=null && !"".equals(classifica))
        where +="=" + classifica;
      else
        where +=" is null";
      where +=")";
    }else{
      where +=" is null)";
    }
    where +=") or exists (select b.ngara from gare B where b.genere=3 and b.ngara=gare.codgar1 and b.elencoe='" + garaElenco + "' and b.dattoa is not null";
    where +=" and exists(select catg.ngara from catg where catg.ngara=b.ngara and catg.ncatg=1 and catg.catiga";
    if(categoriaPrev!=null && !"".equals(categoriaPrev)){
      where +="='" + categoriaPrev + "' and catg.numcla";
      if(classifica!=null && !"".equals(classifica))
        where +="=" + classifica;
      else
        where +=" is null";
      where +=")";
    }else{
      where +=" is null)";
    }
    //where +=")) and gare.ditta not in (select DITTAO from DITG,GARE where GARE.NGARA='" + ngara + "' and  GARE.NGARA=DITG.NGARA5 and GARE.CODGAR1=DITG.CODGAR5)";
    where +=")) ";

    where1 = where + " order by dattoa desc";

    try {
      List listaDatiGare = sqlManager.getListVector("select ngara,dattoa from gare,torn where " + where1, null);
      if (listaDatiGare!=null && listaDatiGare.size()>0){
        Timestamp dattoa=null;
        where = "ngara in(";
        for(int i=0; i<listaDatiGare.size();i++){
          String gara = SqlManager.getValueFromVectorParam(listaDatiGare.get(i),0).getStringValue();
          Timestamp dattoaTmp = SqlManager.getValueFromVectorParam(listaDatiGare.get(i),1).dataValue();
          if(dattoa==null){
            dattoa = dattoaTmp;
            where +="'"+gara+"'";
          }else if(dattoa.equals(dattoaTmp)){
            where +=",'"+gara+"'";
          }else{
            break;
          }
        }
        where +=")";
      }else
        where ="1=0";
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura dei dati delle gare associate all'elenco " + garaElenco , e);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore durante la lettura dei dati delle gare associate all'elenco "  + garaElenco, e);
    }

    StringBuffer html= new StringBuffer("");
    if(categoriaPrev!=null && !"".equals(categoriaPrev)){
      html.append("<b>");
      html.append(categoriaPrev);
      html.append("</b> - ");
      html.append(this.getDescrizioneCategoria(categoriaPrev, sqlManager));
      html.append("&nbsp;&nbsp;&nbsp;&nbsp;");

      String descClassifica="";

      if("1".equals(tipoCategoria.toString()) || ("2".equals(tipoCategoria.toString()) && "true".equals(esisteClassificaForniture))
           || ("3".equals(tipoCategoria.toString()) && "true".equals(esisteClassificaServizi)) || ("4".equals(tipoCategoria.toString()) && "true".equals(esisteClassificaLavori150))
           || ("5".equals(tipoCategoria.toString()) && "true".equals(esisteClassificaServiziProfessionali))){
        html.append("- ");
        html.append("Classifica: <b>");
        descClassifica = "";
        if(classifica!=null && !"".equals(classifica))
          descClassifica = pgManager.getDescCampoClassifica(classifica, tipoCategoria.toString());
        html.append(descClassifica);
        html.append("</b>");
      }

      html.append("<br>\n");
    }else{
      html.append("nessuna categoria specificata<br>\n");

    }
    pageContext.setAttribute("tableCategoria", html, PageContext.REQUEST_SCOPE);

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
}
