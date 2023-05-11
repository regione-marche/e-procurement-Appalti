/*
 * Created on 23/Febbraio/2016
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
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare le sezioni degli atti aggiuntivi di un contratto
 *
 * @author Cristian.Febas
 */
public class GestioneAttiAggiuntiviContrattoFunction extends AbstractFunzioneTag {

  public GestioneAttiAggiuntiviContrattoFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String ngara = (String) params[1];
    String strNcont = (String) params[2];
    strNcont = UtilityStringhe.convertiNullInStringaVuota(strNcont);
    if(!"".equals(strNcont)){
      Long ncont = new Long(strNcont);
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);

      String select="";

      Object[] param = new Object[2];
        select= "select ID, NGARA, NCONT, NREPAT, DAATTO, NIMPCO, TIATTO "
          + "from GARATTIAGG "
          + "where GARATTIAGG.NGARA = ? and GARATTIAGG.NCONT = ? "
          + "order by GARATTIAGG.ID asc";
        param[0] = ngara;
        param[1] = ncont;

      try {
        List listaAttiAggiuntiviContratto = sqlManager.getListVector(select, param);

        Double sum_netto_atti = new Double(0);
        if (listaAttiAggiuntiviContratto != null && listaAttiAggiuntiviContratto.size() > 0){
          pageContext.setAttribute("attiAggiuntiviContratto", listaAttiAggiuntiviContratto, PageContext.REQUEST_SCOPE);

          String selectSUM_NETTO_ATTI = "select sum(coalesce(nimpco,0)) from GARATTIAGG where NGARA = ? and NCONT = ? ";
          Object sum_netto_attiTemp = sqlManager.getObject(selectSUM_NETTO_ATTI, new Object[] {ngara, ncont });
          if (sum_netto_attiTemp != null) {
            if (sum_netto_attiTemp instanceof Long) {
              sum_netto_atti = new Double(((Long) sum_netto_attiTemp));
            } else if (sum_netto_attiTemp instanceof Double) {
              sum_netto_atti = new Double((Double) sum_netto_attiTemp);
            }
          }
        }
        pageContext.setAttribute("totNettoAtti", sum_netto_atti, PageContext.REQUEST_SCOPE);


      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre gli atti aggiuntivi "
            + "della tabella GARATTIAGG", e);
      }

    }

    return null;
  }

}
