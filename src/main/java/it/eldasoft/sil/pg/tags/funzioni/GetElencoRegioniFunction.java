package it.eldasoft.sil.pg.tags.funzioni;
/*
 * Created on 06/feb/2019
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */


import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;

import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
/**
 * Funzione per caricare la lista delle regioni
 *
 * @author Cristian Febas
 */
public class GetElencoRegioniFunction extends AbstractFunzioneTag{
	public GetElencoRegioniFunction() {
	    super(1, new Class[]{PageContext.class});
	  }

	@Override
  public String function(PageContext pageContext, Object[] params)
    throws JspException {

		  String[] elencoRegioni = new String[] { "Piemonte", "Valle d'Aosta",
		        "Liguria", "Lombardia", "Friuli Venezia Giulia", "Trentino Alto Adige",
		        "Veneto", "Emilia Romagna", "Toscana", "Umbria", "Marche", "Abruzzo",
		        "Molise", "Lazio", "Campania", "Basilicata", "Puglia", "Calabria",
		        "Sardegna", "Sicilia" };

	        List<Object> listaRegioni = new Vector<Object>();
	        if(elencoRegioni.length == 1){
            ;
	        }else{
              for (int i = 0; i < elencoRegioni.length; i++) {
                String descRegione = elencoRegioni[i];
                listaRegioni.add(new Object[] {i, descRegione});
              }
              pageContext.setAttribute("listaRegioni", listaRegioni, PageContext.REQUEST_SCOPE);
            }

		  return null;
	}
}
