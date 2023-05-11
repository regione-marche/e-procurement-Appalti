/*
 * Created on 13/ott/09
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
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare le sezioni delle ulteriori categorie di una gara
 * divisa in lotti con offerta unica
 *
 * @author Marcello Caminiti
 */
public class GestioneUlteriCategorieFunction extends AbstractFunzioneTag {

  public GestioneUlteriCategorieFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String nGara = (String) params[0];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        pageContext, TabellatiManager.class);

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        pageContext, PgManager.class);

    String selectSql = null;
    Object[] selectParam = null;

  	selectSql = "select NGARA3, NOPEGA, CATOFF, DESCOP, NUMCLU, ISCOFF, ISFOGLIA  from OPES, V_CAIS_TIT " +
    		"where OPES.NGARA3 = ? and OPES.CATOFF = V_CAIS_TIT.CAISIM order by OPES.NOPEGA asc";
  	selectParam = new Object[] { nGara };

    try {
      List listaUlterioriCategorie = sqlManager.getListVector(
          selectSql, selectParam);

      if (listaUlterioriCategorie != null && listaUlterioriCategorie.size() > 0){
    	//Per ogni categoria ulteriore si deve prelevare la descrizione
        //Preleva anche il tipo appalto per poter poi associare la corrispondente classifica
    	  List listaDescrizioni = new ArrayList(
    			  listaUlterioriCategorie.size());
    	  for (int i = 0; i < listaUlterioriCategorie.size(); i++) {
    		  Vector datiCais = sqlManager.getVector(
    		  		"select descat, tiplavg from cais where caisim= ?",
						new Object[] { SqlManager.getValueFromVectorParam(
								listaUlterioriCategorie.get(i), 2).getStringValue() });
    		  String desc="";
    		  String tiplavg="";
    		  if(datiCais!=null && datiCais.size()>0){
                desc=((JdbcParametro) datiCais.get(0)).getStringValue();
                tiplavg=((JdbcParametro) datiCais.get(1)).getStringValue();
    		  }
              listaDescrizioni.add(desc+"|"+tiplavg);
    	  }

    	  pageContext.setAttribute("ulterioriCategorie", listaUlterioriCategorie,
    	      PageContext.REQUEST_SCOPE);
    	  pageContext.setAttribute("listaDescrizioni",
					listaDescrizioni, PageContext.REQUEST_SCOPE);
      }
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre le ulteriori categorie "
          + "della gara " + nGara, e);
    }

    pageContext.setAttribute("importiIscrizioneLavori",
        tabellatiManager.getTabellato("G_z09"), PageContext.REQUEST_SCOPE);

    List listaImporti =tabellatiManager.getTabellato("G_z07");
    pgManager.aggiornaListaImporti(listaImporti);
    pageContext.setAttribute("importiIscrizioneForniture",
        listaImporti, PageContext.REQUEST_SCOPE);

    listaImporti = tabellatiManager.getTabellato("G_z08");
    pgManager.aggiornaListaImporti(listaImporti);
    pageContext.setAttribute("importiIscrizioneServizi",
        listaImporti, PageContext.REQUEST_SCOPE);

    listaImporti = tabellatiManager.getTabellato("G_z11");
    pgManager.aggiornaListaImporti(listaImporti);
    pageContext.setAttribute("importiIscrizioneLavori150",
        listaImporti, PageContext.REQUEST_SCOPE);

    listaImporti = tabellatiManager.getTabellato("G_z12");
    pgManager.aggiornaListaImporti(listaImporti);
    pageContext.setAttribute("importiIscrizioneServiziProfessionali",
        listaImporti, PageContext.REQUEST_SCOPE);

    return null;
  }

}