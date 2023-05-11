/*
 * Created on 23-01-2013
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
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.math.NumberUtils;
/**
 * Funzione che imposta in sessione il filtro impostato
 * nella home degli elenchi o quello della pagina di ricerca avanzata
 *
 * @author Marcello Caminiti
 */
public class FiltroHomeRicercaElenchiFunction extends AbstractFunzioneTag {

  public FiltroHomeRicercaElenchiFunction() {
    super(3, new Class[] { PageContext.class ,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String filtroDitte="";
    HttpSession sessione = pageContext.getSession();
    if("paginaHome".equals(params[1])){
      String valoreCampo = (String)params[2];
      String campoFiltro = "DITG.NOMIMO";

      if(valoreCampo!= null && !"".equals(valoreCampo)){
        //Se nella stringa è presente il carattere ' va raddoppiato
        valoreCampo=valoreCampo.replace("'", "''");
        if(NumberUtils.isNumber(valoreCampo)){
          campoFiltro = "IMPR.PIVIMP";
          filtroDitte = " and exists (select 1 from impr where dittao=codimp and pivimp like '%" + valoreCampo + "%')";
        }else{
          filtroDitte = " and UPPER(" + campoFiltro + ") like '%" + valoreCampo.toUpperCase() + "%'";
        }
        sessione.setAttribute("campoFiltroHome", campoFiltro);
        sessione.setAttribute("valoreFiltroHome", valoreCampo);

        sessione.setAttribute("filtroDitte", filtroDitte);
      }
    }else{
      String valoreCodimp = (String)pageContext.getAttribute("valoreCodimp");
      String valoreNomimo = (String)pageContext.getAttribute("valoreNomimo");
      String valoreCf = (String)pageContext.getAttribute("valoreCf");
      String valorePiva = (String)pageContext.getAttribute("valorePiva");
      String valoreTipimp = (String)pageContext.getAttribute("valoreTipimp");
      String valoreIsmpmi = (String)pageContext.getAttribute("valoreIsmpmi");
      String valoreEmail = (String)pageContext.getAttribute("valoreEmail");
      String valorePec = (String)pageContext.getAttribute("valorePec");
      String valoreCodCat = (String)pageContext.getAttribute("valoreCodCat");
      String valoreDescCat = (String)pageContext.getAttribute("valoreDescCat");
      String valoreTipCat = (String)pageContext.getAttribute("valoreTipCat");
      String valoreNumclass = (String)pageContext.getAttribute("valoreNumclass");
      String  valoreAbilitaz = (String)pageContext.getAttribute("valoreAbilitaz");
      String valoreDricind = (String)pageContext.getAttribute("valoreDricind");
      String valoreDscad = (String)pageContext.getAttribute("valoreDscad");
      String valoreStrin = (String)pageContext.getAttribute("valoreStrin");
      String valoreAltnot = (String)pageContext.getAttribute("valoreAltnot");
      String valoreCoordsic = (String)pageContext.getAttribute("valoreCoordsic");
      String ignoraCaseSensitive = (String)pageContext.getAttribute("ignoraCaseSensitive");
      String filtroImpr=" and exists (select 1 from impr where dittao=codimp and ";
      String filtroV_ISCRIZCAT_CLASSI=" and exists(select 1  from V_ISCRIZCAT_CLASSI where DITG.NGARA5 = V_ISCRIZCAT_CLASSI.NGARA and DITG.DITTAO = V_ISCRIZCAT_CLASSI.CODIMP   and ";
      boolean filtroImprImpostato=false;
      boolean filtroV_ISCRIZCAT_CLASSIImpostato=false;

      filtroDitte=" and ";

      if(valoreCodimp!=null && !"".equals(valoreCodimp)){
        filtroImprImpostato=true;
        if("si".equals(ignoraCaseSensitive))
          filtroImpr +=" UPPER(codimp) like '%" + valoreCodimp.toUpperCase() + "%'";
        else
          filtroImpr +=" codimp like '%" + valoreCodimp + "%'";
        sessione.setAttribute("valoreFiltroCodimp", valoreCodimp);
      }
      if(valoreNomimo!=null && !"".equals(valoreNomimo)){
        //Se nella stringa è presente il carattere ' va raddoppiato
        valoreNomimo=valoreNomimo.replace("'", "''");
        if("si".equals(ignoraCaseSensitive))
          filtroDitte += " UPPER(ditg.nomimo) like '%" + valoreNomimo.toUpperCase() + "%'";
        else
          filtroDitte += " ditg.nomimo like '%" + valoreNomimo + "%'";
        sessione.setAttribute("valoreFiltroNomimo", valoreNomimo);
      }
      if(valoreCf!=null && !"".equals(valoreCf)){
        if(filtroImprImpostato)
          filtroImpr += " and ";
        else
          filtroImprImpostato=true;
        
        if("si".equals(ignoraCaseSensitive))
          filtroImpr +=" UPPER(cfimp) like '%" + valoreCf.toUpperCase() + "%'";
        else
          filtroImpr +=" cfimp like '%" + valoreCf + "%'";
        sessione.setAttribute("valoreFiltroCF", valoreCf);
      }
      if(valorePiva!=null && !"".equals(valorePiva)){
        if(filtroImprImpostato)
          filtroImpr += " and ";
        else
          filtroImprImpostato=true;

        if("si".equals(ignoraCaseSensitive))
          filtroImpr += " UPPER(pivimp) like '%" + valorePiva.toUpperCase() + "%'";
        else
          filtroImpr += " pivimp like '%" + valorePiva + "%'";
        sessione.setAttribute("valoreFiltroPIVA", valorePiva);
      }
      if(valoreTipimp!=null && !"".equals(valoreTipimp)){
        if(filtroImprImpostato)
          filtroImpr += " and ";
        else
          filtroImprImpostato=true;

        filtroImpr += " tipimp = " + valoreTipimp;
        sessione.setAttribute("valoreFiltroTipimp", valoreTipimp);
      }
      if(valoreIsmpmi!=null && !"".equals(valoreIsmpmi)){
        if(filtroImprImpostato)
          filtroImpr += " and ";
        else
          filtroImprImpostato=true;

        filtroImpr += " ismpmi = " + valoreIsmpmi;
        sessione.setAttribute("valoreFiltroIsmpmi", valoreIsmpmi);
      }
      if(valoreEmail!=null && !"".equals(valoreEmail)){
        if(filtroImprImpostato)
          filtroImpr += " and ";
        else
          filtroImprImpostato=true;

        if("si".equals(ignoraCaseSensitive))
          filtroImpr += " UPPER(emaiip) like '%" + valoreEmail.toUpperCase() + "%'";
        else
          filtroImpr += " emaiip like '%" + valoreEmail + "%'";
        sessione.setAttribute("valoreFiltroEmail", valoreEmail);
      }
      if(valorePec!=null && !"".equals(valorePec)){
        if(filtroImprImpostato)
          filtroImpr += " and ";
        else
          filtroImprImpostato=true;

        if("si".equals(ignoraCaseSensitive))
          filtroImpr += " UPPER(emai2ip) like '%" + valorePec.toUpperCase() + "%'";
        else
          filtroImpr += " emai2ip like '%" + valorePec + "%'";
        sessione.setAttribute("valoreFiltroPec", valorePec);
      }
      if(valoreCodCat!=null && !"".equals(valoreCodCat)){
        filtroV_ISCRIZCAT_CLASSIImpostato=true;
        if("si".equals(ignoraCaseSensitive))
          filtroV_ISCRIZCAT_CLASSI +=" UPPER(V_ISCRIZCAT_CLASSI.CAISIM ) like '%" + valoreCodCat.toUpperCase() + "%'";
        else
          filtroV_ISCRIZCAT_CLASSI +=" V_ISCRIZCAT_CLASSI.CAISIM like '%" + valoreCodCat + "%'";
        sessione.setAttribute("valoreFiltroCodCat", valoreCodCat);
      }
      if(valoreDescCat!=null && !"".equals(valoreDescCat)){
        valoreDescCat=valoreDescCat.replace("'", "''");
        if(filtroV_ISCRIZCAT_CLASSIImpostato)
          filtroV_ISCRIZCAT_CLASSI += " and ";
        else
          filtroV_ISCRIZCAT_CLASSIImpostato=true;

        if("si".equals(ignoraCaseSensitive))
          filtroV_ISCRIZCAT_CLASSI += " UPPER(V_ISCRIZCAT_CLASSI.DESCAT1 ) like '%" + valoreDescCat.toUpperCase() + "%'";
        else
          filtroV_ISCRIZCAT_CLASSI += " V_ISCRIZCAT_CLASSI.DESCAT1 like '%" + valoreDescCat + "%'";
        sessione.setAttribute("valoreFiltroDescCat", valoreDescCat);
      }
      if(valoreTipCat!=null && !"".equals(valoreTipCat)){
        if(filtroV_ISCRIZCAT_CLASSIImpostato)
          filtroV_ISCRIZCAT_CLASSI += " and ";
        else
          filtroV_ISCRIZCAT_CLASSIImpostato=true;
        filtroV_ISCRIZCAT_CLASSI += " V_ISCRIZCAT_CLASSI.TIPLAVG =" + valoreTipCat;
        sessione.setAttribute("valoreFiltroTipcat", valoreTipCat);
      }
      if(valoreNumclass!=null && !"".equals(valoreNumclass)){
        if(filtroV_ISCRIZCAT_CLASSIImpostato)
          filtroV_ISCRIZCAT_CLASSI += " and ";
        else
          filtroV_ISCRIZCAT_CLASSIImpostato=true;
        filtroV_ISCRIZCAT_CLASSI += " V_ISCRIZCAT_CLASSI.NUMCLASS =" + valoreNumclass;
        sessione.setAttribute("valoreFiltroNumclass", valoreNumclass);
      }
      if(valoreAbilitaz!=null && !"".equals(valoreAbilitaz)){

        if(filtroDitte.length()>5)
          filtroDitte+=" and ";
        filtroDitte += " DITG.ABILITAZ =" + valoreAbilitaz ;
        sessione.setAttribute("valoreFiltroAbilitaz", valoreAbilitaz);
      }
      if(valoreDricind!=null && !"".equals(valoreDricind)){
        if(filtroDitte.length()>5)
          filtroDitte+=" and ";
        SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
            pageContext, SqlManager.class);

        String dbFunctionStringToDate = sqlManager.getDBFunction("stringtodate",
            new String[] { valoreDricind });

        filtroDitte += " DITG.DRICIND = " + dbFunctionStringToDate;
        sessione.setAttribute("valoreFiltroDricind", valoreDricind);
      }

      if(valoreDscad!=null && !"".equals(valoreDscad)){
        if(filtroDitte.length()>5)
          filtroDitte+=" and ";
        SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
            pageContext, SqlManager.class);

        String dbFunctionStringToDate = sqlManager.getDBFunction("stringtodate",
            new String[] { valoreDscad });

        filtroDitte += " DITG.DSCAD = " + dbFunctionStringToDate;
        sessione.setAttribute("valoreFiltroDscad", valoreDscad);
      }
      
      if(valoreAltnot!=null && !"".equals(valoreAltnot)){
        //Se nella stringa è presente il carattere ' va raddoppiato
        valoreAltnot=valoreAltnot.replace("'", "''");
        if(filtroDitte.length()>5)
          filtroDitte+=" and ";
        if("si".equals(ignoraCaseSensitive))
          filtroDitte += " UPPER(ditg.altnot) like '%" + valoreAltnot.toUpperCase() + "%'";
        else
          filtroDitte += " ditg.altnot like '%" + valoreAltnot + "%'";
        sessione.setAttribute("valoreFiltroAltnot", valoreAltnot);
      }

      if(valoreCoordsic!=null && !"".equals(valoreCoordsic)){
        //Se nella stringa è presente il carattere ' va raddoppiato
        valoreCoordsic=valoreCoordsic.replace("'", "''");
        if(filtroDitte.length()>5)
          filtroDitte+=" and ";
        if("si".equals(ignoraCaseSensitive))
          filtroDitte += " UPPER(ditg.coordsic) like '%" + valoreCoordsic.toUpperCase() + "%'";
        else
          filtroDitte += " ditg.coordsic like '%" + valoreCoordsic + "%'";
        sessione.setAttribute("valoreFiltroCoordsic", valoreCoordsic);
      }
      
      if(valoreStrin!=null && !"".equals(valoreStrin)){

        if(filtroDitte.length()>5)
          filtroDitte+=" and ";
        filtroDitte += " DITG.STRIN =" + valoreStrin ;
        sessione.setAttribute("valoreFiltroStrin", valoreStrin);
      }

      if(filtroDitte.length()<=5)
        filtroDitte="";

      if(filtroImprImpostato){
        filtroImpr+=")";
        filtroDitte+=filtroImpr;
      }

      if(filtroV_ISCRIZCAT_CLASSIImpostato){
        filtroV_ISCRIZCAT_CLASSI+=")";
        filtroDitte+=filtroV_ISCRIZCAT_CLASSI;
      }

      if(filtroDitte.length()>0)
        sessione.setAttribute("filtroDitte", filtroDitte);


    }



    return null;
  }
}
