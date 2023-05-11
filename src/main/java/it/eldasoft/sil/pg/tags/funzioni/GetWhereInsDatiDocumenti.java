/*
 * Created on 24-09-2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

/**
 * Compone la where per la lista G1DOCUMOD dei documenti
 * predefiniti
 *
 * 
 * @author Riccardo.Peruzzo
 */

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;

public class GetWhereInsDatiDocumenti extends AbstractFunzioneTag {

  public GetWhereInsDatiDocumenti() {
    super(1, new Class[]{PageContext.class});
  }
  
  public String function(PageContext page, Object[] params)
      throws JspException {
    
    page = (PageContext) params[0];
    
    // lettura dei parametri di input
    String tiplav = page.getRequest().getParameter("tiplav");
    String tipgarg = page.getRequest().getParameter("tipgarg");
    String critlic = page.getRequest().getParameter("critlic");
    String gruppo = page.getRequest().getParameter("gruppo");
    String busta = page.getRequest().getParameter("busta");
    String isProceduraTelematica = page.getRequest().getParameter("isProceduraTelematica");
    
    Long bustaLong = null;
    if(busta!= null && !"".equals(busta))
      bustaLong = new Long(busta);
    Long tiplavLong = null;
    if(tiplav!= null && !"".equals(tiplav))
      tiplavLong = new Long(tiplav);
    Long tipgargLong = null;
    if(tipgarg!= null && !"".equals(tipgarg))
      tipgargLong = new Long(tipgarg);
    Long critlicLong = null;
    if(critlic!= null && !"".equals(critlic))
      critlicLong = new Long(critlic);
    Long gruppoLong = null;
    if(gruppo!= null && !"".equals(gruppo))
      gruppoLong = new Long(gruppo);

    String gartel = null;
    String where = "";
    
    if("true".equals(isProceduraTelematica)){
        gartel = "1";
      }else{
        gartel = "2";
      }

      where="(tipogara = " + tiplavLong + " or tipogara is null) and " +
        "(tipoproc = " + tipgargLong + " or tipoproc is null) and (gartel = " + gartel + " or gartel is null)  and " +
        "(critlic = " + critlicLong + " or critlic is null or critlic=0) and gruppo = " + gruppoLong;
      if(busta != null && !"".equals(busta)){
    	  where = where + " and busta = " + bustaLong;
      }

      page.setAttribute("where",where, PageContext.REQUEST_SCOPE);
      page.setAttribute("documentiPresenti","SI", PageContext.REQUEST_SCOPE);
      return "";
  }

}