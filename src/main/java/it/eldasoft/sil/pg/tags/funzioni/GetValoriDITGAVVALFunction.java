package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetValoriDITGAVVALFunction extends AbstractFunzioneTag {

  public GetValoriDITGAVVALFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);


    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))) {


      // Chiave della ditta che e' nella forma
      // key='DITG.CODGAR5=T:00070;DITG.DITTAO=T:0170233059;DITG.NGARA5=T:00070';
      String chiaveDitta = UtilityTags.getParametro(pageContext,
          UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);

      String[] tmp = chiaveDitta.split(";");

      String codiceGara  = tmp[0].substring(tmp[0].indexOf(":")+1);
      String codiceDitta = tmp[1].substring(tmp[1].indexOf(":")+1);
      String numeroGara  = tmp[2].substring(tmp[2].indexOf(":")+1);



      try {
        if (numeroGara != null) {
          String selectDITGAVVAL = "select dv.id, " //0
              + " dv.ngara, "    //1
              + " dv.dittao, "   //2
              + " dv.tipoav, "   //3
              + " dv.dittart, "  //4
              + " i1.nomest, "   //5
              + " dv.dittaav, "  //6
              + " i2.nomest, "    //7
              + " dv.codcat, "  //8
              + " vc.descat, "    //9
              + " dv.numcla, "    //10
              + " dv.noteav "    //11
              + " from ditgavval dv "
              +	" left join impr i1 on dv.dittart = i1.codimp"
              + " left join impr i2 on dv.dittaav = i2.codimp"
              + " left join v_cais_tit vc on dv.codcat = vc.caisim"
              + " where dv.ngara = ? and dv.dittao = ?"
              + " order by dv.id";


          List<?> datiDITGAVVAL = sqlManager.getListVector(selectDITGAVVAL, new Object[] { numeroGara,codiceDitta });
          if (datiDITGAVVAL != null && datiDITGAVVAL.size() > 0) {
            pageContext.setAttribute("datiDITGAVVAL", datiDITGAVVAL, PageContext.REQUEST_SCOPE);
          }
        }
      } catch (SQLException e) {
        throw new JspException("Errore nella lettura degli avvalimenti per la ditta", e);
      }

    }
    return null;
  }
}
