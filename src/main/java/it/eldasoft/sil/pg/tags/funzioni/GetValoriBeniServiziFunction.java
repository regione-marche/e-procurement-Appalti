package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione realizzata per Cineca che carica i valori
 * dei beni e servizi presenti nella tabella proprietaria t_ubuy_beniservizi
 *
 * @author Cristian Febas
 */

public class GetValoriBeniServiziFunction extends AbstractFunzioneTag {

  public GetValoriBeniServiziFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))) {

      String cat = null;
      if (params[0] != null) {
        cat = (String) params[0];
      }

      try {
        if (cat != null) {
          String selectBeniServizi = "";
          List<?> datiBeniServizi = null;
            selectBeniServizi = "select t_ubuy_beniservizi.codcat," +
            		" t_ubuy_beniservizi.num_bs," +
            		" t_ubuy_beniservizi.cod_bs," +
            		" t_ubuy_beniservizi.des_bs," +
            		" t_ubuy_beniservizi.desest_cat" +
            		" from t_ubuy_beniservizi where t_ubuy_beniservizi.codcat = ? ";
            datiBeniServizi = sqlManager.getListVector(selectBeniServizi, new Object[] { cat });
          if (datiBeniServizi != null && datiBeniServizi.size() > 0) {
            pageContext.setAttribute("datiBeniServizi", datiBeniServizi, PageContext.REQUEST_SCOPE);
          }
        }
      } catch (SQLException e) {
        throw new JspException("Errore nella lettura dei beni/servizi", e);
      }

    }
    return null;
  }
}
