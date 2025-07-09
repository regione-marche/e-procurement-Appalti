package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.utils.JwtTokenUtilities;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class LargeFileGenTokenAction extends DispatchActionBaseNoOpzioni {

  private final Logger logger = Logger.getLogger(LargeFileGenTokenAction.class);

  private static final String LARGEFILE_LINK_ACTION = "/rest/largefile";

  @Override
  protected ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    return this.defaultAction(mapping, form, request, response);
  }

  /**
   * The default action of this Action
   */
  private ActionForward defaultAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
    long start = System.currentTimeMillis();
    try {
      logger.debug("Called LargeFileGenToken.defaultAction");
      
      String codgar = request.getParameter("codiceGara");
      String norddocg = request.getParameter("norddocg");
      String descrizioneFile = "", nomeEnte = "", cfEnte = "";
      
      HttpSession session = request.getSession();
      String mLargeFileUrl = ConfigManager.getValore("largefiles.url");

      TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", session.getServletContext(), TabellatiManager.class);
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", session.getServletContext(), SqlManager.class);
      List datiDB = sqlManager.getListVector("select d.descrizione, u.nomein, u.cfein from torn t, documgara d, uffint u where t.codgar=d.codgar and t.cenint=u.codein and d.codgar=? and d.norddocg=?",new Object [] {codgar, norddocg});
      if(datiDB!=null && !datiDB.isEmpty()) {
        descrizioneFile = SqlManager.getValueFromVectorParam(datiDB.get(0),0).getStringValue();
        nomeEnte = SqlManager.getValueFromVectorParam(datiDB.get(0),1).getStringValue();
        cfEnte = SqlManager.getValueFromVectorParam(datiDB.get(0),2).getStringValue();
      }
      
      HashMap<String,String> data = new HashMap<String,String>();
      
      String urlServizio = StringUtils.substringBefore(request.getRequestURL().toString(), request.getServletPath());
      String baseURL = ConfigManager.getValore("integrazioneMDgue.applicationBaseURL");
      if(StringUtils.isNotBlank(baseURL))
        urlServizio = baseURL;
      urlServizio += LARGEFILE_LINK_ACTION;

      String dimMaxSingoloFile = tabellatiManager.getDescrTabellato("A1072", "1");
      if (dimMaxSingoloFile == null || "".equals(dimMaxSingoloFile)) {
        throw new GestoreException("Non è presente il tabellato A1072 per determinare la dimensione "
                + "massima totale dell'upload dei file", "upload.noTabellato", null);
      }
      int pos = dimMaxSingoloFile.indexOf("(");
      if (pos < 1) {
        throw new GestoreException("Non è possibile determinare dal tabellato A1072 la dimensione "
                + "massima totale dell'upload dei file", "upload.noValore", null);
      }
      dimMaxSingoloFile = dimMaxSingoloFile.substring(0, pos - 1);
      dimMaxSingoloFile = dimMaxSingoloFile.trim();

      ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
              CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      int syscon = profiloUtente.getId();
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss", Locale.ITALIAN);
      String dateString = sdf.format(new Date());

      data.put("url", urlServizio); //url callback
      data.put("estensioniAmmesse", ConfigManager.getValore("uploadFile.estensioniAmmesse"));
      data.put("min_upload_size", dimMaxSingoloFile);
      data.put("tenant", cfEnte);
      data.put("operation", "upload");
      data.put("regexpFileName", "^[A-Za-z0-9-_()\\s\\.\\[\\]]+$");

      JSONArray params = new JSONArray();
      addPair(params,"codgar",codgar);
      addPair(params,"norddocg",norddocg);
      addPair(params,"user", String.valueOf(syscon));
      data.put("params", params.toString());

      JSONArray pdfParams = new JSONArray();
      String codiceGara = codgar;
      if(codgar.startsWith("$"))
        codiceGara = codgar.substring(1);
      addPair(pdfParams,"Data e ora",dateString);
      addPair(pdfParams,"Stazione appaltante",nomeEnte);
      addPair(pdfParams,"Codice gara",codiceGara);
      addPair(pdfParams,"Descrizione file",descrizioneFile);
      data.put("pdfParams", pdfParams.toString());

      long expiration = System.currentTimeMillis() + 5 * 60000L;

      String token = JwtTokenUtilities.generateToken("Appalti", expiration, ConfigManager.getValore("largefiles.symkey"),data);
      
      ActionForward af = new ActionForward();
      af.setRedirect(true);
      af.setPath(mLargeFileUrl+"?t="+token);
      return af;
    } catch(Exception e) {
      logger.error("Impossibile effettuare redirect a M-LARGEFILES.",e);
      String messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } finally {
      logger.debug("LargeFileToken executed: "+(System.currentTimeMillis()-start));
    }
    
    return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
  }

 private static void addPair(JSONArray array, String campo, String valore){
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("name", campo);
    jsonObject.put("value", valore);
    array.add(jsonObject);
 }
  
}
