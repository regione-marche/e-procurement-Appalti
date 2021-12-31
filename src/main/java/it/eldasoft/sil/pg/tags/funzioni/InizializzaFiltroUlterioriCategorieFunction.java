/*
 * Created on 28/11/12
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

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare la pagina gare-popup-filtroUlterioriCategorie.jsp
 *
 * @author Marcello Caminiti
 */
public class InizializzaFiltroUlterioriCategorieFunction extends AbstractFunzioneTag {

  public InizializzaFiltroUlterioriCategorieFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {


    String ngara = (String) pageContext.getAttribute("ngara");
    String select="select caisim from v_cais_tit where exists (select catiga from v_gare_categorie where (catiga=caisim " +
            "or catiga=codliv1 or catiga=codliv2 or catiga=codliv3 or catiga=codliv4) and ngara=?)";

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    List ListaDatiCategorie = new Vector();
    try {
      List listaCategorire = sqlManager.getListVector(select, new Object[]{ngara});
      if(listaCategorire!=null && listaCategorire.size()>0){
        for(int i=0; i<listaCategorire.size(); i++){
          String categoria = (String) SqlManager.getValueFromVectorParam(
              listaCategorire.get(i), 0).getValue();

          //Estrazione dei dati da v_gare_categorie
          select="select isprev, numcla, acontec,quaobb from v_gare_categorie where ngara=? and catiga=?";
          Vector datiCategoria = sqlManager.getVector(select, new Object[]{ngara,categoria});
          ListaDatiCategorie.add(datiCategoria);
        }
      }
      pageContext.setAttribute("ListaDatiCategorie", ListaDatiCategorie, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre i dati delle categorie della gara " + ngara, e);
    }


    return null;
  }

}