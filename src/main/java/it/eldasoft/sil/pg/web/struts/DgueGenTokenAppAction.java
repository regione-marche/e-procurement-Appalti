package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.xml.security.utils.Base64;

import it.eldasoft.gene.bl.PropsConfigManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.PropsConfig;
import it.eldasoft.sil.pg.bl.utils.JwtTokenUtilities;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.maggioli.eldasoft.security.EncryptionConstants;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;
import net.sf.json.JSONObject;

/**
 * Classe che serve a generare il JWT token per permettere a M-DGUE di aprire un xml in sola lettura
 * @author gabriele.nencini
 *
 */
public class DgueGenTokenAppAction extends DispatchActionBaseNoOpzioni {

  private static final String JSON_CODEIN = "codein";

  private final Logger logger = Logger.getLogger(DgueGenTokenAppAction.class);
  
  private static final String DGUE_JWTKEY_EXPIRATION = "dgue-jwtkey-expiration";
  private static final String DGUE_JWTKEY = "dgue-jwtkey";
  private static final String DGUE_SYMKEY = "dgue-symkey";
  private static final String SYSCON = "syscon";
  private static final String DGUE = "dgue";
  private static final String JSON_ENC_DATA = "enc-data";
  private static final String JSON_CODICE = "codice";
  private static final String DGUE_LINK_ACTION = "/rest/dguevis";

  
  @Override
  protected ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    return this.defaultAction(mapping, form, request, response);
  }

  /**
   * The default action of this Action
   */
  private ActionForward defaultAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    long start = System.currentTimeMillis();
    try {
      logger.debug("Called DgueGenToken.defaultAction");
      String codice = request.getParameter("codiceGara");
      String idprg = request.getParameter("idprg");
      String iddocdig = request.getParameter("iddocdig");
      String codiceDitta = request.getParameter("codiceDitta");
      
      logger.trace("mapping.getActionId(): "+mapping.getActionId());
      PropsConfigManager pcm = (PropsConfigManager) UtilitySpring.getBean("propsConfigManager", request.getSession().getServletContext(), PropsConfigManager.class);
      String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
      PropsConfig mdgueUrl = pcm.getProperty(codapp, "integrazioneMDgue.url.vis");
      
      HttpSession session = request.getSession();
      ProfiloUtente profilo = (ProfiloUtente) session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", session.getServletContext(), SqlManager.class);
      String codein = (String) sqlManager.getObject("select cenint from torn where codgar=?",new Object [] {codice});
      
      logger.debug("profilo: "+profilo.getId());
      logger.debug("codein:  "+codein);
      Integer syscon = Integer.valueOf(profilo.getId());
      
      List<PropsConfig> listOfProp = pcm.getPropertiesByPrefix("PG", DGUE);
      PropsConfig paramDgueJwtKey = null;
      PropsConfig paramDgueSymKey = null;
      PropsConfig paramDgueJwtKeyExpiration = null;
      for(PropsConfig pc : listOfProp) {
        if(DGUE_SYMKEY.equals(pc.getChiave())) {
          paramDgueSymKey = pc;
        } else if(DGUE_JWTKEY.equals(pc.getChiave())) {
          paramDgueJwtKey = pc;
        } else if(DGUE_JWTKEY_EXPIRATION.equals(pc.getChiave())) {
          paramDgueJwtKeyExpiration = pc;
        }
          /*
           * dgue-symkey
           * dgue-jwtkey
           * dgue-jwtkey-expiration
           * dgue-url-mdgue
           */
      }
      
      
      if(paramDgueSymKey == null 
          || paramDgueJwtKey == null
          || paramDgueJwtKeyExpiration == null
          || StringUtils.isBlank(paramDgueSymKey.getValore())
          || StringUtils.isBlank(paramDgueJwtKey.getValore())
          || StringUtils.isBlank(paramDgueJwtKeyExpiration.getValore())
          ) {
        PropsConfig[] toInsert = new PropsConfig[3];
        paramDgueSymKey = new PropsConfig();
        paramDgueSymKey.setCodApp("PG");
        paramDgueSymKey.setChiave(DGUE_SYMKEY);
        paramDgueSymKey.setValore(Base64.encode(generateAesKey()));
        toInsert[0] = paramDgueSymKey;

        paramDgueJwtKey = new PropsConfig();
        paramDgueJwtKey.setCodApp("PG");
        paramDgueJwtKey.setChiave(DGUE_JWTKEY);
        paramDgueJwtKey.setValore(RandomStringUtils.random(8,true,true));
        toInsert[1] = paramDgueJwtKey;

        paramDgueJwtKeyExpiration = new PropsConfig();
        paramDgueJwtKeyExpiration.setCodApp("PG");
        paramDgueJwtKeyExpiration.setChiave(DGUE_JWTKEY_EXPIRATION);
        paramDgueJwtKeyExpiration.setValore(String.valueOf(5));
        toInsert[2] = paramDgueJwtKeyExpiration;

        pcm.insertProperties(toInsert);
      }
      
      HashMap<String,String> data = new HashMap<String,String>();
      
      String urlServizio = StringUtils.substringBefore(request.getRequestURL().toString(), request.getServletPath());
      String baseURL = pcm.getProperty(codapp, "integrazioneMDgue.applicationBaseURL").getValore();
      if(StringUtils.isNotBlank(baseURL))
        urlServizio = baseURL;
      urlServizio += DGUE_LINK_ACTION;
      if(urlServizio.startsWith("http://") && mdgueUrl.getValore().startsWith("https://")) {
        //forzo https nella callback dato che mdgueUrl viene esposto in https
        //requisito che APpalti venga esposto in https
    	  // Commentato rinomina della url di callback da http a https per permettere di invocare mdgue in https da appalti in localhost e quindi in http.
    	  // urlServizio = StringUtils.replace(urlServizio, "http://", "https://");
      }
      data.put("urlServizio", urlServizio);
      logger.debug("urlServizio: "+urlServizio);
      
      JSONObject obj = new JSONObject();
      obj.put(SYSCON, syscon);
      obj.put(JSON_CODEIN, codein);
      obj.put(JSON_CODICE, codice);
      obj.put("idprg", idprg);
      obj.put("iddocdig", iddocdig);
      obj.put("codiceDitta", codiceDitta);
      logger.debug("enc-data: "+obj.toString());
      byte[] chiaveSimmetrica = Base64.decode(paramDgueSymKey.getValore());

      long expiration = System.currentTimeMillis() + 5 * 60000l;
      Cipher cipher = SymmetricEncryptionUtils.getEncoder(chiaveSimmetrica);
      data.put(JSON_ENC_DATA,Base64.encode(cipher.doFinal(obj.toString().getBytes())));
      String token = JwtTokenUtilities.generateToken("Appalti", expiration, paramDgueJwtKey.getValore(),data);
      
      ActionForward af = new ActionForward();
      af.setRedirect(true);
      af.setPath(mdgueUrl.getValore()+"?t="+token);
      return af;
    } catch(Exception e) {
      logger.error("Impossibile effettuare redirect a M-DGUE.",e);
      String messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } finally {
      logger.debug("DgueGenToken executed: "+(System.currentTimeMillis()-start));
    }
    
    return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
  }
  
  private byte[] generateAesKey() throws NoSuchAlgorithmException, NoSuchProviderException {
    //TODO export into EncryptionUtils
// se non esiste la comunicazione allora si genera una nuova chiave
   // di sessione AES
   KeyGenerator keyGenerator = KeyGenerator.getInstance(EncryptionConstants.SESSION_KEY_GEN_ALGORITHM,
                                                         EncryptionConstants.SECURITY_PROVIDER);
   keyGenerator.init(128);
   Key aesKey = keyGenerator.generateKey();
   return aesKey.getEncoded();
 }
  
}
