package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ControlloProtocollazioneDocBandoAction extends Action {

  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    try {
      boolean docTuttiNonProtocollati=true;
      String entita = request.getParameter("entita");
      String key1 = request.getParameter("key1");
      String ngara = request.getParameter("ngara");
      String codgar = request.getParameter("codgar");
      String genereGara = request.getParameter("genereGara");
      
      List<Object> parameters = new ArrayList<Object>();
      String select="select IDPRG, IDDOCDG from DOCUMGARA where CODGAR=? ";
      parameters.add(codgar);
     
      if( genereGara== null || "2".equals(genereGara)) {
        select+=" and NGARA = ?";
        parameters.add(ngara);
      }
      
      select+=" and GRUPPO = ? order by NUMORD";
      parameters.add(new Long(1));
      List<?> datiDoc=this.sqlManager.getListVector(select, parameters.toArray());

      if(datiDoc!=null && datiDoc.size()>0){
        String idprg = null;
        Long iddocdg = null;
        String selectWsAllegati="select a.id from wsallegati a, wsdocumento d where a.entita=? and a.key1=? and a.key2=? and a.idwsdoc=d.id and d.entita=? and d.key1=?";
        Long idAllegato=null;
        for(int i=0; i < datiDoc.size(); i++){
          idprg = SqlManager.getValueFromVectorParam(datiDoc.get(i), 0).getStringValue();
          iddocdg = SqlManager.getValueFromVectorParam(datiDoc.get(i), 1).longValue();
          idAllegato = (Long)this.sqlManager.getObject(selectWsAllegati, new Object[]{"W_DOCDIG",idprg,iddocdg,entita,key1});
          if(idAllegato!=null){
            docTuttiNonProtocollati= false;
            break;
          }
        }
      }


      result.put("esito", docTuttiNonProtocollati);

    } catch (SQLException e) {
      throw new JspException("Errore nella verifica del numero di documenti del bando protocollati", e);
    }

    out.println(result);
    out.flush();

    return null;

  }
}
