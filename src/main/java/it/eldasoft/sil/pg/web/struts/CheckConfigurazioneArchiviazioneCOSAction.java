package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.eldasoft.utils.properties.ConfigManager;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import static it.eldasoft.gene.utils.ConfigurazionePerUffintUtils.getConfigurazionePerUfficioIntestatario;
import static it.eldasoft.gene.utils.ConfigurazionePerUffintUtils.getOptConfigurazionePerUfficioIntestatario;

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

    String codein = null;
    String append = "";
            String modalita = getConfigurazionePerUfficioIntestatario("conservazione.modalita",cenint);
    if(StringUtils.isNotBlank(modalita)) { //siamo su una property multiente. Tutte le property vanno lette dai record multiente
      codein = cenint;
      append = "/"+cenint;
    }

    modalita = getConfigurazionePerUfficioIntestatario("conservazione.modalita",codein);//ricalcolo nel caso fosse nulla la property multiente
    String alias_sp = getConfigurazionePerUfficioIntestatario("cos.sftp.aliasSp",codein);
    String alias_da = getConfigurazionePerUfficioIntestatario("cos.sftp.aliasDa",codein);
    String port = getConfigurazionePerUfficioIntestatario("conservazione.sftp.port",codein);
    String url = getConfigurazionePerUfficioIntestatario("conservazione.sftp.url",codein);
    String login = getConfigurazionePerUfficioIntestatario("conservazione.sftp.login",codein);
    String password = getConfigurazionePerUfficioIntestatario("conservazione.sftp.password",codein);
    String pathBase = getConfigurazionePerUfficioIntestatario("cos.sftp.pathBase",codein);
    String pathBasePrefisso = getConfigurazionePerUfficioIntestatario("cos.sftp.pathBasePrefisso",codein);
    String maxRigheIndice = ConfigManager.getValore("cos.maxRigheIndice");
    String ambiente = getConfigurazionePerUfficioIntestatario("digip.sftp.ambiente",codein);
    String struttura = getConfigurazionePerUfficioIntestatario("digip.sftp.struttura",codein);
    String ente = getConfigurazionePerUfficioIntestatario("digip.sftp.ente",codein);
    String userid = getConfigurazionePerUfficioIntestatario("digip.sftp.userid",codein);
    String tipoRegistro = getConfigurazionePerUfficioIntestatario("digip.sftp.tipoRegistro",codein);
    String classificazione = getConfigurazionePerUfficioIntestatario("digip.sftp.classificazione",codein);
    String tempoConservazione = getConfigurazionePerUfficioIntestatario("digip.sftp.tempoConservazione",codein);
    String urlFascicolazione = getConfigurazionePerUfficioIntestatario("digip.sftp.fascicolazione.url",codein);
    String formati = getConfigurazionePerUfficioIntestatario("conservazione.formatiConsentiti",codein);


    
    boolean success = true;
    //errors = "Sono stati riscontrati i seguenti errori nella configurazione dell'integrazione a COS, i seguenti campi non sono stati valorizzati:\n";
    ArrayList<String> errorsconfig = new ArrayList<String>();
    ArrayList<String> errorsgara = new ArrayList<String>();

    if("1".equals(modalita)){
    if(alias_sp == null || "".equals(alias_sp)){
      errorsconfig.add("cos.sftp.aliasSp"+append);
    }
    if(alias_da == null || "".equals(alias_da)){
      errorsconfig.add("cos.sftp.aliasDa"+append);
    }
      if(pathBase == null || "".equals(pathBase)){
        errorsconfig.add("cos.pathBase"+append);
      }
      if(pathBasePrefisso == null || "".equals(pathBasePrefisso)){
        errorsconfig.add("cos.pathBasePrefisso"+append);
      }
      if(maxRigheIndice == null || "".equals(maxRigheIndice)){
        errorsconfig.add("cos.maxRigheIndice");
      }
      }
    if(port == null || "".equals(port)){
      errorsconfig.add("conservazione.sftp.port"+append);
    }
    if(url == null || "".equals(url)){
      errorsconfig.add("conservazione.sftp.url"+append);
    }
    if(login == null || "".equals(login)){
      errorsconfig.add("conservazione.sftp.login"+append);
    }
    if(password == null || "".equals(password)){
      errorsconfig.add("conservazione.sftp.password"+append);
    }


    if(cenint == null || "".equals(cenint)){
      errorsgara.add("Riferimento alla stazione appaltante");
    }else{
      if(nomein == null || "".equals(nomein)){
        errorsgara.add("Denominazione della stazione appaltante");
      }
    }

    if(codrup == null || "".equals(codrup)){
      errorsgara.add("Riferimento al RUP");
    }else{
      if(cftec == null || "".equals(cftec)){
        errorsgara.add("Codice fiscale del RUP");
      }
      if(nomtec == null || "".equals(nomtec)){
        errorsgara.add("Nome del RUP");
      } 
    }

    if("2".equals(modalita)){
      if(StringUtils.isBlank(ambiente))
        errorsconfig.add("digip.sftp.ambiente"+append);
      if(StringUtils.isBlank(struttura))
        errorsconfig.add("digip.sftp.struttura"+append);
      if(StringUtils.isBlank(ente))
        errorsconfig.add("digip.sftp.ente"+append);
      if(StringUtils.isBlank(userid))
        errorsconfig.add("digip.sftp.userid"+append);
      if(StringUtils.isBlank(tipoRegistro))
        errorsconfig.add("digip.sftp.tipoRegistro"+append);
      if(StringUtils.isBlank(classificazione))
        errorsconfig.add("digip.sftp.classificazione"+append);
      if(StringUtils.isBlank(tempoConservazione))
        errorsconfig.add("digip.sftp.tempoConservazione"+append);
      if(StringUtils.isBlank(urlFascicolazione))
        errorsconfig.add("digip.sftp.fascicolazione"+append);
    }else{
      if(StringUtils.isBlank(formati))
        errorsconfig.add("conservazione.formatiConsentiti"+append);
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
