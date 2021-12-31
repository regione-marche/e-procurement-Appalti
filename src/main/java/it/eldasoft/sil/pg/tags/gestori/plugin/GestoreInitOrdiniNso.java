/*
 * Created on 11/03/2020
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
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore che
 *
 * @author Cristian Febas
 */
public class GestoreInitOrdiniNso extends AbstractGestorePreload {

  SqlManager sqlManager = null;



  public GestoreInitOrdiniNso(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }

  public void inizializzaManager(PageContext page){
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);



  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    this.inizializzaManager(page);

    HttpSession session = page.getSession();
    String uffint = (String) session.getAttribute("uffint");


    String ngara = page.getRequest().getParameter("ngara");
    ngara = UtilityStringhe.convertiNullInStringaVuota(ngara);
    String arrmultikey = page.getRequest().getParameter("arrmultikey");

    String codgar = null;
    String oggetto = null;
    String dittaAgg = null;
    Long numPlico = null;
    String messaggio = "";
    String controlloSuperato="SI";


    if (!"".equals(ngara)){
      try {
        //
        Vector<?> datiGaraLotto = this.sqlManager.getVector("select codgar1, not_gar, ditta, codcig ,cupprg, nrepat, dattoa" +
        		" from gare where ngara = ?", new Object[]{ngara});
        if(datiGaraLotto!=null && datiGaraLotto.size()>0){
          codgar =  SqlManager.getValueFromVectorParam(datiGaraLotto, 0).getStringValue();
          oggetto =  SqlManager.getValueFromVectorParam(datiGaraLotto, 1).getStringValue();
          dittaAgg =  SqlManager.getValueFromVectorParam(datiGaraLotto, 2).getStringValue();
          dittaAgg = UtilityStringhe.convertiNullInStringaVuota(dittaAgg);
          if(!"".equals(dittaAgg)){//
            page.setAttribute("initDITTA", dittaAgg, PageContext.REQUEST_SCOPE);
            numPlico = (Long)this.sqlManager.getObject("select numordpl from ditg" +
            		" where codgar5 = ? and ngara5 = ? and dittao = ? ", new Object[]{codgar,ngara,dittaAgg});
          }
          String codCig =  SqlManager.getValueFromVectorParam(datiGaraLotto, 3).getStringValue();
          String cup =  SqlManager.getValueFromVectorParam(datiGaraLotto, 4).getStringValue();
          String nrepat =  SqlManager.getValueFromVectorParam(datiGaraLotto, 5).getStringValue();
          Date dataAggiudicazione =  (Date) SqlManager.getValueFromVectorParam(datiGaraLotto, 6).getValue();
          String dataAggStr = UtilityDate.convertiData(dataAggiudicazione, UtilityDate.FORMATO_GG_MM_AAAA);

          page.setAttribute("initArrmultikey", arrmultikey, PageContext.REQUEST_SCOPE);
          page.setAttribute("initNGARA", ngara, PageContext.REQUEST_SCOPE);
          page.setAttribute("initOGGETTO", oggetto, PageContext.REQUEST_SCOPE);
          if(numPlico != null){
            page.setAttribute("initNUMORDPL", numPlico.toString(), PageContext.REQUEST_SCOPE);
          }
          page.setAttribute("initCODCIG", codCig, PageContext.REQUEST_SCOPE);
          page.setAttribute("initCUP", cup, PageContext.REQUEST_SCOPE);
          page.setAttribute("initNREPAT", nrepat, PageContext.REQUEST_SCOPE);
          page.setAttribute("initDATORD", dataAggStr, PageContext.REQUEST_SCOPE);
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura dei dati della gara/lotto associata all'ordine ", e);
      }
    }
      page.setAttribute("initUFFINT", uffint, PageContext.REQUEST_SCOPE);
      page.setAttribute("controlloSuperato", controlloSuperato, PageContext.REQUEST_SCOPE);
      page.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);

  }

}