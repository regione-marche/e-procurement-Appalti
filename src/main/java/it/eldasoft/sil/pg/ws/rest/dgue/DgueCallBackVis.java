package it.eldasoft.sil.pg.ws.rest.dgue;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xml.security.exceptions.Base64DecodingException;
import org.apache.xml.security.utils.Base64;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.PropsConfigManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.admin.UffintManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.PropsConfig;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.sil.pg.db.domain.Torn;
import it.eldasoft.sil.pg.bl.DgueManager;
import it.eldasoft.sil.pg.bl.utils.EstrazioneContenutoFileFirmatoMarcato;
import it.eldasoft.sil.pg.bl.utils.JwtTokenUtilities;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;
import net.sf.json.JSONObject;

/**
 * End point per fare visualizzare un file DGUE xml in chiaro direttamente in M-DGUE
 * 
 * @author gabriele.nencini
 *
 */
@Path("/dguevis")
public class DgueCallBackVis {

  private static final String JSON_CODEIN   = "codein";
  private static final String DGUE_JWTKEY   = "dgue-jwtkey";
  private static final String DGUE_SYMKEY   = "dgue-symkey";
  private static final String SYSCON        = "syscon";
  private static final String DGUE          = "dgue";
  private static final String JSON_ENC_DATA = "enc-data";
  private static final String JSON_DATA     = "data";
  private static final String JSON_CODICE   = "codice";

  private final Logger        logger        = Logger.getLogger(getClass());

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getDatum(@Context HttpServletRequest request) {
    long start = System.currentTimeMillis();
    try {
      logger.debug("Called dgue end point, returning UNAUTHORIZED by default now");
      String auth = request.getHeader("Authorization");
      if(StringUtils.isBlank(auth)) return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).build();
      
      String token = StringUtils.substringAfter(auth, "Bearer ");
      if(StringUtils.isBlank(token)) return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).build();
      
      String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
      HttpSession session = request.getSession();
      PropsConfigManager pcm = (PropsConfigManager) UtilitySpring.getBean("propsConfigManager", session.getServletContext(), PropsConfigManager.class);
      List<PropsConfig> listOfProp = pcm.getPropertiesByPrefix(codapp, DGUE);
      PropsConfig paramDgueJwtKey = null;
      PropsConfig paramDgueSymKey = null;
      for(PropsConfig pc : listOfProp) {
        if(DGUE_SYMKEY.equals(pc.getChiave())) {
          paramDgueSymKey = pc;
        } else if(DGUE_JWTKEY.equals(pc.getChiave())) {
          paramDgueJwtKey = pc;
        }
          /*
           * dgue-symkey
           * dgue-jwtkey
           * dgue-jwtkey-expiration
           * dgue-url-mdgue
           */
      }
      if(paramDgueJwtKey==null || paramDgueSymKey==null) {
        logger.debug("Impossibile trovare a db una delle configurazioni: "+DGUE_SYMKEY+","+DGUE_JWTKEY);
        return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).build();
      }
      JSONObject body = JSONObject.fromObject(JwtTokenUtilities.getBodyFromJwt(JwtTokenUtilities.parseJwt(token,paramDgueJwtKey.getValore())));
      
      JSONObject dataFromInput = (JSONObject) body.get(JSON_DATA);
      String encDataStr = dataFromInput.getString(JSON_ENC_DATA);
      
      JSONObject encData =  decodeData(encDataStr,paramDgueSymKey);
      logger.debug("encData: "+encData.toString());
      Integer syscon = Integer.valueOf(encData.getInt(SYSCON));
      // recupero il codice gara
      String codice = encData.getString(JSON_CODICE);
      String codein = encData.getString(JSON_CODEIN);
      String idprg = encData.getString("idprg");
      String iddocdig = encData.getString("iddocdig");
      String codiceditta = encData.getString("codiceDitta");
      
      if(StringUtils.isBlank(codice) || syscon == null) {
        logger.debug("Impossibile trovare nel token codice o chiave utenza");
        return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).build();
      }
      ServletContext sc = session.getServletContext();
      AccountManager am = (AccountManager) UtilitySpring.getBean("accountManager", sc, AccountManager.class);
      Account sys = am.getAccountById(syscon);
      
      if(sys == null) {
        logger.debug("Impossibile trovare utenza per chiave inserita: "+syscon);
        return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).build();        
      }
      OpzioniUtente opzioniUtente = new OpzioniUtente(sys.getOpzioniUtente());

      CheckOpzioniUtente opzioniAmministratoreSistema = new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
      
      boolean isNotAdmin = !opzioniAmministratoreSistema.test(opzioniUtente);
      
      UffintManager um = (UffintManager) UtilitySpring.getBean("uffintManager", sc, UffintManager.class);
      String uffint = (String) session.getAttribute("uffint");
      uffint = StringUtils.stripToEmpty(uffint);
      if(!"".equals(uffint) && isNotAdmin) {
      List<String> listUffint =  um.getCodiciUfficiIntestatariAccount(syscon);
        if(!listUffint.contains(codein)) {
          logger.debug("Impossibile trovare ufficio intestario: "+codein+" legato ad utente: "+syscon);
          return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).build();        
        }
      }
      DgueManager dm = (DgueManager) UtilitySpring.getBean("dgueManager", sc, DgueManager.class);
      Torn t = dm.getTornFullByPK(codice);
      
      if(t==null) {
        logger.debug("Impossibile trovare gara per codice inserito: "+codice);
        return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).build();                
      }
      if(!codein.equals(t.getCenint())) {
        logger.debug("La gara specificata non corrisponde alla codein inserita: "+codice+" codein:"+codein);
        return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).build();                
      }
      
      Map<String,Object> map = new HashMap<String,Object>();
      HashMap<String,Map<String,Object>> m = new HashMap<String,Map<String,Object>>();
      m.put("data", map);
      
      // dgueRequest - si fa immediatamente il controllo se esiste un documento allegato di tipo DGUE
      // se esiste si restituisce solo quello
      BlobFile f = null;
      if(iddocdig != null && !"".equals(iddocdig)) {
        SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", session.getServletContext(), SqlManager.class);
        Long countDocument = (Long) sqlManager.getObject("select count(*) from IMPRDOCG where codgar = ? and idprg = ? and iddocdg = ? and codimp = ?",new Object [] {codice, idprg, iddocdig, codiceditta});
        if(countDocument>0) {
          FileAllegatoManager fam = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager", sc, FileAllegatoManager.class);
          f = fam.getFileAllegato(idprg, Long.valueOf(iddocdig));
        }else {
          logger.debug("Nessun documento presente per i documenti della ditta");
          return Response.status(Status.NOT_FOUND).type(MediaType.APPLICATION_JSON_TYPE).build();
        }   
      }
      if(f==null) {
        logger.debug("File non trovato. idprg: "+idprg+" , iddocdig: "+iddocdig);
        return Response.status(Status.NOT_FOUND).type(MediaType.APPLICATION_JSON_TYPE).build();
      }
      byte[] doc = EstrazioneContenutoFileFirmatoMarcato.estraiContenutoFile(f.getStream(), f.getNome());
      map.put("dgueResponse", Base64.encode(doc,1));//to avoid 76 char chunks
      return Response.ok().entity(m).type(MediaType.APPLICATION_JSON_TYPE).build();
    } catch(ExpiredJwtException e){
      logger.error("Error reading Jwt-token data",e);
    } catch(MalformedJwtException e){
      logger.error("Error reading Jwt-token data",e);
    } catch(SignatureException e){
      logger.error("Error reading Jwt-token data",e);
    } catch(IllegalArgumentException e){
      logger.error("Error reading Jwt-token data",e);
    } catch(Exception e) {
      logger.error("Error retrieving data",e);
      return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON_TYPE).build();
    } finally {
      logger.debug("DgueCallBackVis time execution "+(System.currentTimeMillis()-start)+"ms");
    }
    return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).build();
  }

  private JSONObject decodeData(String dataToBeDecoded, PropsConfig paramDgueSymKey)
      throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
      InvalidAlgorithmParameterException, Base64DecodingException, IllegalBlockSizeException, BadPaddingException {
    Cipher cipher = SymmetricEncryptionUtils.getDecoder(Base64.decode(paramDgueSymKey.getValore()));
    return JSONObject.fromObject(new String(cipher.doFinal(Base64.decode(dataToBeDecoded.getBytes()))));
  }
}
