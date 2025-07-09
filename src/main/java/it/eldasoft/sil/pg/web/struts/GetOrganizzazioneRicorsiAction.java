package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.tasks.ArchiviazioneDocumentiManager;
import it.eldasoft.utils.spring.UtilitySpring;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

public class GetOrganizzazioneRicorsiAction extends Action {

    private SqlManager sqlManager;

    public void setSqlManager(SqlManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
                                       final HttpServletResponse response) throws Exception {
        String codein = request.getParameter("codein");
        String orgricorso = request.getParameter("orgricorso");

        DataSourceTransactionManagerBase.setRequest(request);

        response.setHeader("cache-control", "no-cache");
        response.setContentType("text/text;charset=utf-8");
        PrintWriter out = response.getWriter();

        JSONObject result = new JSONObject();

        try{
            if(StringUtils.isEmpty(orgricorso)) {
                String query = "select count(ORGRICORSO)"
                        + "from PUNTICON "
                        + "where ORGRICORSO = '1' and CODEIN = ? ";
                long numOrgRicorsi = (long) sqlManager.getObject(query, new Object[]{codein});
                if(numOrgRicorsi == 1 ){
                    List listaOrganizzazioni = listaOrganizzazioni = sqlManager.getListVector(
                            "select NUMPUN, NOMPUN "
                                    + "from PUNTICON "
                                    + "where ORGRICORSO = '1' and CODEIN = ?",
                            new Object[] {codein});
                    result.put("numPun", SqlManager.getValueFromVectorParam(listaOrganizzazioni.get(0), 0).getValue());
                    result.put("nomPun",  SqlManager.getValueFromVectorParam(listaOrganizzazioni.get(0), 1).getValue());
                }
            }else{
                String nompun = (String) sqlManager.getObject(
                        "select NOMPUN "
                                + "from PUNTICON "
                                + "where ORGRICORSO = '1' and CODEIN = ? and numpun=?",
                        new Object[] {codein, Long.parseLong(orgricorso)});
                result.put("numPun", orgricorso);
                result.put("nomPun", nompun);
            }
        } catch (SQLException e) {
            throw new JspException("Errore " + e);
        }

        out.println(result);
        out.flush();

        return null;
    }

}

