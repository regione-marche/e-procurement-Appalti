/*
 * Created on 21/05/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Si controlla se in GAREALBO esiste una sola occorrenza con la condizione:
 *  DTERMVAL.GAREALBO >= today() oppure nullo
 *  catalogo pubblicato su portale (esiste occ. in PUBBLI con TIPPUB = 11 e DATPUB <= today())
 *
 * in tale caso
 * di questa vengono estratti  NGARA ed OGGETTO
 *
 * @author Marcello Caminiti
 */
public class ValorizzaCatalogoElettronicoFunction extends AbstractFunzioneTag {


  public ValorizzaCatalogoElettronicoFunction(){
    super(1, new Class[]{String.class});
  }


  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String modo = (String)params[0];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
        pageContext, GeneManager.class);

    String dataOdierna = UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA);

    String dbFunctionStringToDate = sqlManager.getDBFunction("stringtodate",
        new String[] { dataOdierna });

    String where = "GAREALBO.NGARA in (select NGARA from MECATALOGO where MECATALOGO.CODGAR=GAREALBO.CODGAR and MECATALOGO.NGARA=GAREALBO.NGARA) " +
    		"and (GAREALBO.DTERMVAL >= " + dbFunctionStringToDate  + " or GAREALBO.DTERMVAL is null)" +
    		" and exists(select codgar9 from pubbli where codgar9=garealbo.codgar and tippub=11 and DATPUB <="
      +  dbFunctionStringToDate + ")";

    if("NUOVO".equals(modo)){
      long occorrenzeGarealbo = geneManager.countOccorrenze("GAREALBO", where, null);
      if(occorrenzeGarealbo==1){
        String select ="select ngara,oggetto from garealbo where " + where;
        Vector datiGarealbo;
        try {
          datiGarealbo = sqlManager.getVector(select, null);
          String ngara = null;
          String oggetto = null;
          if(datiGarealbo.get(0) != null){
            ngara = ((JdbcParametro) datiGarealbo.get(0)).getStringValue();
          }
          if(datiGarealbo.get(1) != null){
            oggetto = ((JdbcParametro) datiGarealbo.get(1)).getStringValue();
          }
          pageContext.setAttribute("initCODCATA", ngara, PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("initOGGETTO", oggetto, PageContext.REQUEST_SCOPE);
        } catch (SQLException e) {
          throw new JspException(
              "Errore durante la lettura della tabella GAREALBO ", e);
        }
      }
    }

    pageContext.setAttribute("whereGarealbo", where, PageContext.REQUEST_SCOPE);

    return null;
  }

}