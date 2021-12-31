/*
 * Created on 17-12-2013
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore preload per la pagina di dettagli dei prodotti meiscrizprod
 *
 * @author Marcello.Caminiti
 */
public class GestoreInizializzazioniMEISCRIZPROD extends AbstractGestorePreload {

  public GestoreInizializzazioniMEISCRIZPROD(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doBeforeBodyProcessing(PageContext pageContext,
      String modoAperturaScheda) throws JspException {

	  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
	          pageContext, SqlManager.class);

	  HashMap key = UtilityTags.stringParamsToHashMap(
              (String) pageContext.getAttribute(
                  UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, PageContext.REQUEST_SCOPE),
              null);

	  String id = ((JdbcParametro) key.get("MEISCRIZPROD.ID")).getStringValue();

    if ("VISUALIZZA".equals(modoAperturaScheda)) {



    	String select="select idartcat from meiscrizprod where id = ?";
    	try {
    		Long idArtcat  = (Long)sqlManager.getObject(select,
    	            new Object[] {new Long(id) });

    		pageContext.setAttribute("idArticolo",idArtcat.toString() );
    	}catch (SQLException e) {
            throw new JspException(
                    "Errore durante la lettura di MEISCRIZPROD con id=" + id, e);
       }

    	select="select id, idiscrizprod, mealliscrizprod.idprg, mealliscrizprod.iddocdig, w_docdig.dignomdoc, " +
    			" tipo from mealliscrizprod, w_docdig where idiscrizprod=? and tipo=?" +
    			" and mealliscrizprod.idprg = w_docdig.idprg " +
                " and mealliscrizprod.iddocdig = w_docdig.iddocdig";
    	//Caricamento dati da MEALLISCRIZPROD per tipo=1
    	try {
    	  List<?> datiMEALLISCRIZPROD1 = sqlManager.getListVector(select, new Object[] { new Long(id), new Long(1) });
    	  pageContext.setAttribute("datiMEALLISCRIZPROD1",datiMEALLISCRIZPROD1,PageContext.REQUEST_SCOPE );

    	  List<?> datiMEALLISCRIZPROD2 = sqlManager.getListVector(select, new Object[] { new Long(id), new Long(2) });
          pageContext.setAttribute("datiMEALLISCRIZPROD2",datiMEALLISCRIZPROD2,PageContext.REQUEST_SCOPE );

          List<?> datiMEALLISCRIZPROD3 = sqlManager.getListVector(select, new Object[] { new Long(id), new Long(3) });
          pageContext.setAttribute("datiMEALLISCRIZPROD3",datiMEALLISCRIZPROD3,PageContext.REQUEST_SCOPE );

      } catch (SQLException e) {
        throw new JspException(
            "Errore durante la lettura di MEALLISCRIZPROD con idiscrizprod=" + id, e);
      }

    }

    String select="select descat from MEISCRIZPROD,MEARTCAT,OPES,CAIS where MEISCRIZPROD.ID =? and MEARTCAT.ID = MEISCRIZPROD.IDARTCAT " +
    		"and MEARTCAT.NGARA = OPES.NGARA3 and MEARTCAT.NOPEGA = OPES.NOPEGA and OPES.CATOFF = CAIS.CAISIM";
    try {
      String descr = (String)sqlManager.getObject(select, new Object[]{id});
      pageContext.setAttribute("initDescatt",descr,PageContext.REQUEST_SCOPE );
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura della descrizione della categoria associata al prodotto con idiscrizprod=" + id, e);
    }

  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }


}