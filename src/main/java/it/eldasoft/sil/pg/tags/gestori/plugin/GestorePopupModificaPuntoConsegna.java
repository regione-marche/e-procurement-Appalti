/*
 * Created on 30/04/2020
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
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore che effettua i controlli preliminari e popola la popup per
 * la modifica del Punto di consegna
 *
 * @author Cristian Febas
 */
public class GestorePopupModificaPuntoConsegna extends AbstractGestorePreload {

  private SqlManager sqlManager = null;


  public GestorePopupModificaPuntoConsegna(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);



    // lettura dei parametri di input
    String idPuntoConsegna = page.getRequest().getParameter("idPuntoConsegna");
    String idOrdine = page.getRequest().getParameter("idOrdine");
      //Caricamento datipunti consegna
      try {
        Vector<?> datiPuntoConsegna=this.sqlManager.getVector("select ID,NSO_ORDINI_ID,COD_PUNTO_CONS,INDIRIZZO,LOCALITA,CAP,CITTA,CODNAZ,ALTRE_INDIC,ALTRO_PUNTO_CONS,CONS_DOMICILIO" +
        		" from NSO_PUNTICONS where NSO_ORDINI_ID = ?", new Object[]{idOrdine});
        if(datiPuntoConsegna!=null && datiPuntoConsegna.size()>0){
          Long id = (Long) SqlManager.getValueFromVectorParam(datiPuntoConsegna, 0).getValue();
          //Long idOrdine = (Long) SqlManager.getValueFromVectorParam(datiPuntoConsegna, 1).getValue();
          String codPuntoCons = SqlManager.getValueFromVectorParam(datiPuntoConsegna, 2).getStringValue();
          String indirizzo = SqlManager.getValueFromVectorParam(datiPuntoConsegna, 3).getStringValue();
          String localita = SqlManager.getValueFromVectorParam(datiPuntoConsegna, 4).getStringValue();
          String cap = SqlManager.getValueFromVectorParam(datiPuntoConsegna, 5).getStringValue();
          String citta = SqlManager.getValueFromVectorParam(datiPuntoConsegna, 6).getStringValue();
          String codNaz = SqlManager.getValueFromVectorParam(datiPuntoConsegna, 7).getStringValue();
          String altreIndic = SqlManager.getValueFromVectorParam(datiPuntoConsegna, 8).getStringValue();
          String consDom = SqlManager.getValueFromVectorParam(datiPuntoConsegna, 9).getStringValue();
          page.setAttribute("initCodPuntoCons", codPuntoCons, PageContext.REQUEST_SCOPE);
          page.setAttribute("initIndirizzo", indirizzo, PageContext.REQUEST_SCOPE);
          page.setAttribute("initLocalita", localita, PageContext.REQUEST_SCOPE);
          page.setAttribute("initCap", cap, PageContext.REQUEST_SCOPE);
          page.setAttribute("initCitta", citta, PageContext.REQUEST_SCOPE);
          page.setAttribute("initCodNaz", codNaz, PageContext.REQUEST_SCOPE);
          page.setAttribute("initAltreIndic", altreIndic, PageContext.REQUEST_SCOPE);
          page.setAttribute("initConsDom", consDom, PageContext.REQUEST_SCOPE);

        }


      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura delle date dei termini di presentazione della domanda di partecipazione ", e);
      }

  }

}