/*
 * Created on 25/giu/2018
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;


public class isVecchiaOEPVFunction extends AbstractFunzioneTag {

  public isVecchiaOEPVFunction() {
    super(2, new Class[] { PageContext.class, String.class });
    // TODO Auto-generated constructor stub
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    String codiceGara = (String)params[1];
    String numeroGara = null;

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    HttpSession sessione = pageContext.getSession();

    if(codiceGara ==null || "".equals(codiceGara)){
      numeroGara = UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
      if (numeroGara != null && numeroGara.length() > 0){
        numeroGara = numeroGara.substring(numeroGara.indexOf(":") + 1);}
      if(numeroGara == null || numeroGara.length() == 0){
        sessione.setAttribute("vecchiaOepv", false);
        return "false";}
      try {
        codiceGara = (String) sqlManager.getObject(
            "select codgar1 from gare where ngara = ?",
            new Object[] { numeroGara });
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    Long offtel;
    Long tippub;

      try {
        offtel = (Long) sqlManager.getObject(
            "select offtel from torn where codgar = ?",
            new Object[] { codiceGara });
        tippub = (Long) sqlManager.getObject(
            "select tippub from pubbli,torn where pubbli.codgar9 = ? and torn.codgar = pubbli.codgar9 and (pubbli.tippub = 13 or pubbli.tippub = 23 or (pubbli.tippub = 11 and torn.iterga = 1))",
            new Object[] { codiceGara });


        if(offtel == null || offtel == 2){
          sessione.setAttribute("vecchiaOepv", true);
          return "true";
        }
        if(offtel == 1 && tippub != null){
          String selectLottiFormatoDef = "select gare.ngara from gare inner join g1cridef on formato != 100 and gare.ngara = g1cridef.ngara  and codgar1 = ? and modlicg = 6";
          List datiLotti = sqlManager.getVector(selectLottiFormatoDef,
              new Object[] { codiceGara });
          if(datiLotti != null && datiLotti.size() > 0) {
            sessione.setAttribute("vecchiaOepv", false);
            return "false";
          }
          else{
            sessione.setAttribute("vecchiaOepv", true);
            return "true";
          }
        }
        sessione.setAttribute("vecchiaOepv", false);
        return "false";


      } catch (SQLException s) {
        throw new JspException("Errore durante la lettura della fase di gara "
            + "(GARE.FASGAR)", s);
      }
  }


}
