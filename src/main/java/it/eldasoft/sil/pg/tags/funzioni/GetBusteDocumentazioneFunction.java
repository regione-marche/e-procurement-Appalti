/*
 * Created on 02-JAN-2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Funzione che in base allo step di gara in cui mi trovo
 * estrae il set di buste (A,B,C) relazionate allo step
 * di gara stesso
 *
 * @author Cristian Febas
 */
public class GetBusteDocumentazioneFunction extends AbstractFunzioneTag {

	public GetBusteDocumentazioneFunction() {
		super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
	}


	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", pageContext, TabellatiManager.class);

    String stepWizard = (String) GeneralTagsFunction.cast("string", params[1]);
    String entita = (String) GeneralTagsFunction.cast("string", params[2]);
    String campo = (String) GeneralTagsFunction.cast("string", params[3]);
    String inclause = "";
    List listaTabellatoA1014 = tabellatiManager.getTabellato("A1014");

    for(int i=0; i < listaTabellatoA1014.size() ; i++){
      Tabellato singleTabellato = (Tabellato) listaTabellatoA1014.get(i);
      String tipo_i =singleTabellato.getTipoTabellato();
      String desc_i = singleTabellato.getDescTabellato();
      desc_i = UtilityStringhe.convertiNullInStringaVuota(desc_i);
      if ("".equals(desc_i)) {
        if("".equals(inclause)){
          inclause = tipo_i;
        }else{
          inclause = inclause + "," + tipo_i;
        }
      } else {
        if (desc_i.indexOf(stepWizard) >= 0) {
          if("".equals(inclause)){
            inclause = tipo_i;
          }else{
            inclause = inclause + "," + tipo_i;
          }
        }
      }
    }
    String whereBusteAttive = entita + "." + campo + " is null";
    if(!"".equals(inclause)){
      whereBusteAttive = entita + "." + campo + " is null OR " + entita + "." + campo + " in (" + inclause + ")";
    }
    return whereBusteAttive;

 }
}
