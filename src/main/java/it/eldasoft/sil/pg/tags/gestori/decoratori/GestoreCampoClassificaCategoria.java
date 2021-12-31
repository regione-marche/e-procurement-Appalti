/*
 * Created on 18/Nov/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.decoratori;

import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellatoArc;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.util.HashMap;

import javax.servlet.jsp.PageContext;

/**
 * Gestore per i campi Classifica inferiore e superiore con tabellati diversi
 * a seconda del tipo di appalto della categoria (Lavori, forniture o servizi)
 *
 * @author Sara Santi
 */
public class GestoreCampoClassificaCategoria extends AbstractGestoreCampoTabellatoArc {

  public GestoreCampoClassificaCategoria() {
    super(false, "N2");
  }

  /**
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato#getSql()
   */
  @Override
  public SqlSelect getSql() {

    HashMap datiRiga = (HashMap) this.getPageContext().getAttribute("datiRiga",
        PageContext.REQUEST_SCOPE);

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getPageContext(), PgManager.class);

    String updateLista =  this.getPageContext().getRequest().getParameter("updateLista");

    String tiplavg = "1";
    String tabcod="A1015";
    if (datiRiga.get("V_ISCRIZCAT_TIT_TIPLAVG") != null){
      tiplavg = (String) datiRiga.get("V_ISCRIZCAT_TIT_TIPLAVG");
    }else if(datiRiga.get("V_GARE_CATEGORIE_TIPLAVG") != null){
      tiplavg = (String) datiRiga.get("V_GARE_CATEGORIE_TIPLAVG");
    }else if(datiRiga.get("V_ISCRIZCAT_CLASSI_TIPLAVG") != null){
      tiplavg = (String) datiRiga.get("V_ISCRIZCAT_CLASSI_TIPLAVG");
    }
    if ("2".equals(tiplavg))
      tabcod="G_035";
    else if ("3".equals(tiplavg))
      tabcod="G_036";
    else if ("4".equals(tiplavg))
      tabcod="G_037";
    else if ("5".equals(tiplavg))
      tabcod="G_049";

    String select ="select tab1tip,tab1desc,tab1arc from tab1 where tab1cod='"+tabcod+"' "
    + "order by tab1nord,tab1tip";

    //Elenchi operatori economici: gestione classifica SOA fino a un certo valore
    if(datiRiga.get("V_ISCRIZCAT_TIT_TIPLAVG") != null && "1".equals(tiplavg)){

      Long limite=pgManager.getLimiteTabA1084();
      //Se la lista non è in modifica si salta l'impostazione sul limite superiore
      if(limite != null && "1".equals(updateLista)){
        select = "select tab1tip,tab1desc,tab1arc from tab1 where tab1cod='"+tabcod+"' "
        + "and (tab1tip <= " + limite.toString();
        String supnumclass = (String) datiRiga.get("ISCRIZCAT_SUPNUMCLASS");
        if(supnumclass!=null && !"".equals(supnumclass))
          select += " or tab1tip = " + supnumclass;
        select+=  ") order by tab1nord,tab1tip";
      }
    }

    return new SqlSelect(select, null);
  }


}
