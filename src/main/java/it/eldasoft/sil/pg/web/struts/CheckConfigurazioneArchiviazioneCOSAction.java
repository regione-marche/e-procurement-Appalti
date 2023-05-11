package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.tasks.ArchiviazioneDocumentiManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloInType;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloResType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoInType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class CheckConfigurazioneArchiviazioneCOSAction extends Action {
  
  private SqlManager sqlManager;
  
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  
  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {
     
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();
    
    String codgar = request.getParameter("codgar");
    
    String alias_sp = ConfigManager.getValore("cos.sftp.aliasSp");
    String alias_da = ConfigManager.getValore("cos.sftp.aliasDa");
    String port = ConfigManager.getValore("cos.sftp.port");
    String url = ConfigManager.getValore("cos.sftp.url");
    String login = ConfigManager.getValore("cos.sftp.login");
    String password = ConfigManager.getValore("cos.sftp.password");
    String pathBase = ConfigManager.getValore("cos.sftp.pathBase");
    String pathBasePrefisso = ConfigManager.getValore("cos.sftp.pathBasePrefisso");
    String maxRigheIndice = ConfigManager.getValore("cos.maxRigheIndice");
    
    String cenint = null;
    String codrup = null;
    String nomein = null;
    String cftec = null;
    String nomtec = null;
    
    String queryUffint = "select cenint, nomein from torn t, uffint u where u.codein = t.cenint and codgar = ?";
    Vector<JdbcParametro> datiUffint = this.sqlManager.getVector(queryUffint,new Object[]{codgar});
    if(datiUffint!=null && datiUffint.size()>0){
      cenint = (String) (datiUffint.get(0)).getValue();
      nomein = (String) (datiUffint.get(1)).getValue();
    }

    String queryTecni = "select codrup, cftec, nomtec from torn t, tecni tec  where tec.codtec = t.codrup and codgar = ?";
    Vector<JdbcParametro> datiTecni = this.sqlManager.getVector(queryTecni,new Object[]{codgar});
    if(datiTecni!=null && datiTecni.size()>0){
      codrup = (String) (datiTecni.get(0)).getValue();
      cftec = (String) (datiTecni.get(1)).getValue();
      nomtec = (String) (datiTecni.get(2)).getValue();
    }
    
    boolean success = true;
    //errors = "Sono stati riscontrati i seguenti errori nella configurazione dell'integrazione a COS, i seguenti campi non sono stati valorizzati:\n";
    ArrayList<String> errorsconfig = new ArrayList<String>();
    ArrayList<String> errorsgara = new ArrayList<String>();
    
    if(alias_sp == null || "".equals(alias_sp)){
      errorsconfig.add("cos.sftp.aliasSp");
    } 
    if(alias_da == null || "".equals(alias_da)){
      errorsconfig.add("cos.sftp.aliasDa");
    }
    if(port == null || "".equals(port)){
      errorsconfig.add("cos.sftp.port");
    }
    if(url == null || "".equals(url)){
      errorsconfig.add("cos.sftp.url");
    }
    if(login == null || "".equals(login)){
      errorsconfig.add("cos.sftp.login");
    }
    if(password == null || "".equals(password)){
      errorsconfig.add("cos.sftp.password");
    }
    if(pathBase == null || "".equals(pathBase)){
      errorsconfig.add("cos.pathBase");
    }
    if(pathBasePrefisso == null || "".equals(pathBasePrefisso)){
      errorsconfig.add("cos.pathBasePrefisso");
    }
    if(maxRigheIndice == null || "".equals(maxRigheIndice)){
      errorsconfig.add("cos.maxRigheIndice");
    }
    
    
    if(cenint == null || "".equals(cenint)){
      errorsgara.add("Riferimento alla stazione appaltante");
    }else{
      if(nomein == null || "".equals(nomein)){
        errorsgara.add("Denominazione della stazione appaltante");
      }
    }
    
    if(codrup == null || "".equals(codrup)){
      errorsgara.add("Riferimento al responsabile unico di procedimento");
    }else{
      if(cftec == null || "".equals(cftec)){
        errorsgara.add("Codice fiscale del responsabile unico di procedimento");
      }
      if(nomtec == null || "".equals(nomtec)){
        errorsgara.add("Nome del responsabile unico di procedimento");
      } 
    }
    
    if(errorsgara.size() > 0 || errorsconfig.size() > 0){
      success = false;
    }
    
    DataSourceTransactionManagerBase.setRequest(request);
    JSONObject result = new JSONObject();
    result.put("errorsconfig", errorsconfig);
    result.put("errorsgara", errorsgara);
    result.put("result", success);

    out.println(result);
    out.flush();

    return null;
  }

}
