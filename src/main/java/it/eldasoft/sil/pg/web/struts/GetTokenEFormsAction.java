package it.eldasoft.sil.pg.web.struts;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.xml.security.utils.Base64;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.sil.pg.bl.utils.JwtTokenUtilities;
import it.eldasoft.utils.properties.ConfigManager;
import net.sf.json.JSONObject;

public class GetTokenEFormsAction extends ActionBaseNoOpzioni {

  Logger             logger = Logger.getLogger(GetTokenEFormsAction.class);
  private static final String JSON_ENC_DATA = "enc-data";
  public static final String URL_M_EFORM = "integrazioneMEforms.url";
  private static final String JWT_SECRET = "utiCt/R4}_7MuoYaVuP-1,Z3kdTI%3:%fqaTX0G:td<!|_k8z<-moTTjw?kzg|3|";

  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  protected ActionForward runAction(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");

    String url = ConfigManager.getValore(URL_M_EFORM);
    String idscheda = request.getParameter("idscheda");
    String invokingUrl = request.getParameter("urlChiamante");

    TransactionStatus status = null;
    boolean commit = true;

    try {

      status = this.sqlManager.startTransaction();

      String uuid = null;
      if(idscheda != null) {
        uuid = getUid(Long.valueOf(idscheda));
      }
      String query = "select n.eform "
          + "from g1cf_npa n "
          + "inner join g1npaform f on n.id = f.idcfnpa "
          + "inner join g1npascheda s on s.idform = f.id "
          + "where s.id = ? ";
      String codiceEform = (String) this.sqlManager.getObject(query, new Object[] {idscheda});
      
      String rettificaUuidPadre = null;
      String query1 = "select UUID_EFORM "
          + "from g1npascheda "
          + "where id = ("
          + "    select idschedanparet "
          + "    from g1npascheda "
          + "    where id = ? "
          + ") ";
      rettificaUuidPadre = (String) this.sqlManager.getObject(query1, new Object[] {idscheda});

      ActionForward af = new ActionForward();
      af.setRedirect(true);
      af.setPath(url+"?t="+generaToken(uuid, codiceEform, invokingUrl, rettificaUuidPadre));
      return af;

    } catch (Throwable t) {
      commit = false;
      logger.error("Errore durante la creazione del token", t);
    } finally {
      if (status != null) {
          try {
          if (commit) {
			this.sqlManager.commitTransaction(status);
          } else {
              this.sqlManager.rollbackTransaction(status);
          }
	
			
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			
		}
    }
    }
	return mapping
			.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
  }



  private String getUid(Long idScheda) throws SQLException {
    String uidQuery = "select UUID_EFORM from G1NPASCHEDA where id=?";
    String uuid = (String) sqlManager.getObject(uidQuery, new Object[] { idScheda });
    if (uuid == null) {
      UUID newUuid = UUID.randomUUID();
      uuid = newUuid.toString();
      String updateG1NPASCHEDA = "update G1NPASCHEDA set UUID_EFORM=? where id=?";
      this.sqlManager.update(updateG1NPASCHEDA, new Object[] { uuid,idScheda });
    }
    return uuid;
  }

  private String generaToken(String uuid, String codiceEform, String invokingUrl, String rettificaUuidPadre) throws Exception {
    HashMap<String,String> data = new HashMap<>();
    data.put("noticeUUID", uuid);
    data.put("noticeSubType", codiceEform);
    data.put("noticeChangedUUID", rettificaUuidPadre);
    
    if (invokingUrl.endsWith("/"))
      invokingUrl = invokingUrl.substring(0, invokingUrl.length() - 1);
    
    data.put("dataGetUrl", invokingUrl+"/rest/eforms/data-get");
    data.put("keepAliveUrl", invokingUrl+"/rest/eforms/keep-alive");
    data.put("persistenceUrl", invokingUrl+"/rest/eforms/persistence");

    //long expiration = System.currentTimeMillis() + 5 * 60000L; //5 minuti
    long expiration = System.currentTimeMillis() + 30 * 1000L; // 30 secondi
    return JwtTokenUtilities.generateToken("Appalti", expiration, JWT_SECRET, data);
  }


}



