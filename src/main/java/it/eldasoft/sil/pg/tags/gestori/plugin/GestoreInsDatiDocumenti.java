/*
 * Created on 26/08/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore che estrae alcune informazioni da visualizzare come riepilogo nella
 * popup di conferma per l'inserimento della documentazione predefinita
 *
 * @author Marcello Caminiti
 */
public class GestoreInsDatiDocumenti extends AbstractGestorePreload {

  public GestoreInsDatiDocumenti(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);

    // lettura dei parametri di input
    //String codgar = page.getRequest().getParameter("codgar");
    String ngara = page.getRequest().getParameter("ngara");
    String tiplav = page.getRequest().getParameter("tiplav");
    String tipgarg = page.getRequest().getParameter("tipgarg");
    String importo = page.getRequest().getParameter("importo");
    String critlic = page.getRequest().getParameter("critlic");
    String faseInvito = page.getRequest().getParameter("faseInvito");
    String gruppo = page.getRequest().getParameter("gruppo");
    String busta = page.getRequest().getParameter("busta");
    String isProceduraTelematica = page.getRequest().getParameter("isProceduraTelematica");
    Double ImportoDouble = null;
    if(importo!= null && !"".equals(importo))
      ImportoDouble = new Double(importo);
    Long critlicLong = null;
    if(critlic!= null && !"".equals(critlic))
      critlicLong = new Long(critlic);
    Long gruppoLong = null;
    if(gruppo!= null && !"".equals(gruppo))
      gruppoLong = new Long(gruppo);

    //LA SELECT SEGUENTE, A PARTE I CAMPI ESTRATTI, E' IDENTICA A QUELLA ESEGUITA NEL
    //GESTORE DI SUBMIT GESTOREINSERTDOCUMENTIPREDEFINITI.
    //SE SI MODIFICA LA SELECT SEGUENTE SI DEVE MODIFICARE ANCHE IL GESTORE DI SUBMIT!

    String gartel = null;
    String select= "";
    Long numeroDocumentiPredefiniti= null;

    try {
      select="select count(codarch) from archdocg where (tipogara = ? or tipogara is null) and " +
        "(tipoproc = ? or tipoproc is null) and (gartel = ? or gartel is null)  and " +
        "(LIMINF <= ? or LIMINF is null) and " +
        "(LIMSUP > ? or LIMSUP is null) and (critlic = ? or critlic is null or critlic=0) and gruppo = ?";
      if(busta != null && !"".equals(busta)){
        select = select + " and busta = " + busta;
      }
      if("true".equals(isProceduraTelematica)){
        gartel = "1";
      }else{
        gartel = "2";
      }

      numeroDocumentiPredefiniti = (Long) sqlManager.getObject(
          select, new Object[] {tiplav,tipgarg,gartel,ImportoDouble,ImportoDouble, critlicLong, gruppoLong});

      if (numeroDocumentiPredefiniti!= null && numeroDocumentiPredefiniti.longValue()>0)
        page.setAttribute("documentiPresenti","SI", PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante il calcolo del numero dei documenti predefiniti per la gara",e);
    }
  }
}