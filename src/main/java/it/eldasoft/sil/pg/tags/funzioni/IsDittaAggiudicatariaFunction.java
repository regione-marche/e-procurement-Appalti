/*
 * Created on 04/09/18
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che determina se una ditta risulta essere aggiudicataria per la gara
 *
 * @author Marcello.Caminiti
 */
public class IsDittaAggiudicatariaFunction extends AbstractFunzioneTag {

  public IsDittaAggiudicatariaFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class});
  }


  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String ngara = (String) params[1];
    String ditta = (String) params[2];
    String isGaraLottiConOffertaUnica = (String) params[3];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String dittaAggiudicataria = "false";

    if (ngara != null) {

      try {
        String selectLotti = "";
        if(!"true".equals(isGaraLottiConOffertaUnica)){
          selectLotti = "select g.ditta, g1.aqoper, g.ngara from gare g, gare1 g1 where g.ngara=? and g.ngara=g1.ngara";
        }else{
          selectLotti = "select g.ditta, g1.aqoper, g.ngara from gare g, gare1 g1 where g.codgar1=? and g.ngara=g1.ngara and g.ngara!=g.codgar1"
              + " and modlicg in(5,6,14) and g.ngara in(select p.ngara from gcap p where p.ngara=g.ngara)";

        }
        dittaAggiudicataria = this.controlloAggiudicatariaNeiLotti(ngara, ditta, selectLotti, sqlManager);

      } catch (SQLException e) {
        throw new JspException("Errore durante la verifica che la ditta corrente " + ditta + " sia aggiudicataria della gara", e);
      } catch (GestoreException e) {
        throw new JspException("Errore durante la verifica che la ditta corrente " + ditta + " sia aggiudicataria della gara", e);
      }
    }

    return dittaAggiudicataria;
  }

  private String controlloAggiudicatariaNeiLotti(String ngara,String codiceDitta, String selectLotti, SqlManager sqlManager) throws SQLException, GestoreException{
    String dittaAggiudicataria="false";
    List listaDatiGara = sqlManager.getListVector(selectLotti, new Object[]{ngara});
    if(listaDatiGara!=null && listaDatiGara.size()>0){
      String dittaAgg = null;
      Long aqoper = null;
      String lotto = null;
      for(int i=0;i<listaDatiGara.size();i++){
        dittaAgg=SqlManager.getValueFromVectorParam(listaDatiGara.get(i), 0).stringValue();
        aqoper = SqlManager.getValueFromVectorParam(listaDatiGara.get(i), 1).longValue();
        lotto = SqlManager.getValueFromVectorParam(listaDatiGara.get(i), 2).stringValue();
        if(new Long(2).equals(aqoper)){
          //Accordo quadro con più operatori, si deve controllare DITGAQ
          String dittaAggAcc=(String)sqlManager.getObject("select dittao from ditgaq where ngara=? and dittao=?", new Object[]{lotto,codiceDitta});
          if(dittaAggAcc!=null && !"".equals(dittaAggAcc)){
            dittaAggiudicataria = "true";
            break;
          }
        }else{
          if(dittaAgg!=null && !"".equals(dittaAgg) && dittaAgg.equals(codiceDitta)){
            dittaAggiudicataria = "true";
            break;
          }
        }
      }
    }
    return dittaAggiudicataria;
  }
}