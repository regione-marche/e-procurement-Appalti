
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che determina se esiste il questionario per un lotto di gara
 */
public class GetStatoQuestionarioLottoFunction extends AbstractFunzioneTag {

  public GetStatoQuestionarioLottoFunction() {
    super(5, new Class[] { PageContext.class,String.class,String.class,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        pageContext, PgManagerEst1.class);

    String result="";

    String ngara = (String)params[1];
    String codgar = (String)params[2];
    String itergaString = (String)params[3];
    String bustaString = (String)params[4];

    if(ngara!=null && !"".equals(ngara)) {
      int busta=3;
      if("2".equals(bustaString))
        busta=2;
      Long iterga= null;
      if(itergaString!=null && !"".equals(itergaString))
        iterga = new Long(itergaString);
      try {
        result = pgManagerEst1.gestioneQuestionari(ngara,codgar, new Long(3), busta, iterga,true);
        SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
            pageContext, SqlManager.class);
        String sqlCountQformStato = "select count(*) from QFORM where ENTITA='GARE' and KEY1=? and BUSTA=? and STATO=?";
        Long numeroPubb = (Long)sqlManager.getObject(sqlCountQformStato, new Object[] {ngara, busta, new Long(5)});
        Long numeroAttesaPubb = (Long)sqlManager.getObject(sqlCountQformStato, new Object[] {ngara, busta, new Long(1)});
        Long numeroArchiviati = (Long)sqlManager.getObject(sqlCountQformStato, new Object[] {ngara, busta, new Long(8)});
        Long numeroRettificati = (Long)sqlManager.getObject(sqlCountQformStato, new Object[] {ngara, busta, new Long(7)});
        String riepilogoPubb = "";
        if(!new Long(0).equals(numeroRettificati)){
          if(!new Long(0).equals(numeroPubb))
            riepilogoPubb = "Q-form pubblicato, " ;
          riepilogoPubb += "Rettifica in corso" ;
        } else if(!new Long(0).equals(numeroAttesaPubb))
          riepilogoPubb = "Q-form da pubblicare" ;
        else if(!new Long(0).equals(numeroPubb))
          riepilogoPubb = "Q-form pubblicato" ;

        if(!new Long(0).equals(numeroArchiviati))
          riepilogoPubb += ", archiviati: "+ numeroArchiviati;

        pageContext.setAttribute("labelStato", riepilogoPubb, PageContext.REQUEST_SCOPE);
      } catch (SQLException e) {
        throw new JspException("Errore nella lettura dei questionari del lotto " + ngara, e);
      }
    }
    return result;
  }
}
