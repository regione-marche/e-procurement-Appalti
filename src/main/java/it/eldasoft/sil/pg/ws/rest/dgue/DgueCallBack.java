package it.eldasoft.sil.pg.ws.rest.dgue;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.ws.rs.OPTIONS;
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
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.admin.TecniciManager;
import it.eldasoft.gene.bl.admin.UffintManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.PropsConfig;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.UfficioIntestatario;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.sil.pg.db.domain.Garcpv;
import it.eldasoft.sil.pg.db.domain.Gare;
import it.eldasoft.gene.db.domain.admin.Tecni;
import it.eldasoft.sil.pg.db.domain.Torn;
import it.eldasoft.sil.pg.bl.DgueManager;
import it.eldasoft.sil.pg.bl.InviaVigilanzaManager;
import it.eldasoft.sil.pg.bl.utils.EstrazioneContenutoFileFirmatoMarcato;
import it.eldasoft.sil.pg.bl.utils.JwtTokenUtilities;
import it.eldasoft.sil.pg.db.domain.Pubbli;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;
import net.sf.json.JSONObject;

@Path("/dgue")
public class DgueCallBack {
  
  private static final String JSON_CODEIN = "codein";
  private static final String DGUE_JWTKEY = "dgue-jwtkey";
  private static final String DGUE_SYMKEY = "dgue-symkey";
  private static final String SYSCON = "syscon";
  private static final String DGUE = "dgue";
  private static final String JSON_ENC_DATA = "enc-data";
  private static final String JSON_DATA = "data";
  private static final String JSON_CODICE = "codice";
  
  private final Logger logger = Logger.getLogger(getClass());
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getDatum(@Context HttpServletRequest request) {
    long start = System.currentTimeMillis();
    try {
      logger.debug("Called dgue end point.");
      String auth = request.getHeader("Authorization");
      if(StringUtils.isBlank(auth)) return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).build();
      
      String token = StringUtils.substringAfter(auth, "Bearer ");
      if(StringUtils.isBlank(token)) return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).build();
      
      HttpSession session =  request.getSession();
      String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
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
      logger.debug("enc-data:"+encDataStr);
      
      JSONObject encData =  decodeData(encDataStr,paramDgueSymKey);
      logger.debug("enc-data:"+encData);
      
      Integer syscon = Integer.valueOf(encData.getInt(SYSCON));
      // recupero il codice gara
      String codice = encData.getString(JSON_CODICE);
      String codein = encData.getString(JSON_CODEIN);
      String idprg = encData.getString("idprg");
      String iddocdig = encData.getString("iddocdig");
      
      if(StringUtils.isBlank(codice) ||syscon == null) {
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
      
      //verifico se é attivo il filtro per stazione appaltante: se non lo è, non faccio il controllo
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
      if(iddocdig != null && !"".equals(iddocdig)) {
        SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", session.getServletContext(), SqlManager.class);
        Long countDocument = (Long) sqlManager.getObject("select count(*) from documgara where codgar = ? and idprg = ? and iddocdg = ? and idstampa = ? ",new Object [] {codice, idprg, iddocdig,"DGUE"});
        if(countDocument>0) {
          FileAllegatoManager fam = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager", sc, FileAllegatoManager.class);
          BlobFile lf = fam.getFileAllegato(idprg, Long.valueOf(iddocdig));
          if(lf!=null) { 
            byte[] doc = EstrazioneContenutoFileFirmatoMarcato.estraiContenutoFile(lf.getStream(), lf.getNome());
            map.put("dgueRequest", Base64.encode(doc,1));//to avoid 76 char chunks
            return Response.ok().entity(m).type(MediaType.APPLICATION_JSON_TYPE).build();  
          }else {
            logger.debug("File non trovato. idprg: "+idprg+" , iddocdig: "+iddocdig);
            return Response.status(Status.NOT_FOUND).type(MediaType.APPLICATION_JSON_TYPE).build();
          }
        }else {
          logger.debug("Nessun documento DGUE presente per la gara");
          return Response.status(Status.NOT_FOUND).type(MediaType.APPLICATION_JSON_TYPE).build();
        }
      }
      
      PropsConfig pc = pcm.getProperty("PG", "portaleAppalti.urlPubblica");
      
      //dato che non esiste alcun file allegato allora vengono estratti tutti i dati minimi per compilare il M-DGUE
      UfficioIntestatario ui = um.getUfficioIntestatarioFullByPKWithNation(t.getCenint());
      
      StazioneAppaltanteDgueDto sa = new StazioneAppaltanteDgueDto();
      sa.setCap(ui.getCapein());
      
      sa.setCitta(buildCity(ui.getCitein(),ui.getProein()));
      sa.setNazione(ui.getCodnaz());
      sa.setPartitaIva(ui.getCodFiscale());
      if(StringUtils.isEmpty(sa.getPartitaIva())){
        sa.setPartitaIva(ui.getPartitaIVA());
      }
      sa.setRagioneSociale(ui.getNome());
      //aggiunta URL
      sa.setIndrizzoProfiloCommittente(ui.getProfco());
      sa.setSitoWeb(ui.getIndweb());
      sa.setVia(buildAddress(ui.getViaein(),ui.getNciein()));
      //aggiunta email sa
      if(pc!=null)
        sa.setEmail(StringUtils.trimToNull(pc.getValore()));
      
      map.put("sa",sa);
      
      ProceduraGaraDgueDto pg = new ProceduraGaraDgueDto();
      if(t.getTipgen()!=null) {
        pg.setOggetto(t.getTipgen().toString());//tipo gara: forniture / servizi / etc ..
      }
      pg.setTitolo(t.getDestor());
      
      if(t.getOfflot()!=null) {
        pg.setCodiceOffertePresentate(t.getOfflot().toString());
      }
      Integer maxLottiOfferta = t.getNgadit();
      Integer maxLottiAggiudicabili = t.getNofdit();
      
      pg.setCodiceGara(StringUtils.removeStart(codice, "$"));// - con $ o senza $ ? -> senza dollaro
      pg.setCodiceANAC(t.getNumavcp());
      
      List<Gare> lg = dm.getGareFullByFKDirect(codice);
      if(lg==null || lg.size()==0) throw new Exception("Entita in GARE non trovata per codgar "+codice);
      
      Garcpv input = new Garcpv();
      input.setNgara("%"+StringUtils.removeStart(codice, "$")+"%");
      Collection<Garcpv> lcpv = dm.getGarcpvFullByNgaraLike(input);
      if(lcpv!=null && lcpv.size()>0) {
        List<String> temp = new ArrayList<String>(lcpv.size());
        /*
         * Non posso usare un set
         */
        for(Garcpv gcpv : lcpv) {
          if(!temp.contains(gcpv.getCodcpv())) {
          temp.add(gcpv.getCodcpv());
          }
        }
        pg.setCpv(temp);
      }
      // tecni => RUP
      TecniciManager tm = (TecniciManager) UtilitySpring.getBean("tecniciManager", sc, TecniciManager.class);
      Tecni rup = tm.getTecniFullByPK(t.getCodrup());
      if(rup != null) {
      TecniDgueDto drup = new TecniDgueDto();
      String email = rup.getEma2tec();
      if(StringUtils.isBlank(email)) {
        email = rup.getEmatec();
      }
      drup.setEmail(email );
      drup.setNome(rup.getNomtec());// campo nomtec = concat di nome e cognome
      drup.setFax(rup.getFaxtec());
      drup.setTelefono(rup.getTeltec());
      
      pg.setRup(drup);
      }
      List<LottoDgueDto> lotti = new ArrayList<LottoDgueDto>(lg.size());
      if(lg.size()==1) {
        //gara lotto unico
        pg.setTitolo(lg.get(0).getNot_gar());
      }
      // tipo procedura deve essere quella che viene inviata per vigilanza
      String proceduraScelta = null;
      for(Gare g : lg) {
        if((g.getGenere()==null || g.getGenere().intValue()!= 3 )) {
          LottoDgueDto l = new LottoDgueDto();
          l.setCig(g.getCodcig());
          l.setNumLotto(g.getCodiga());
          if(l.getNumLotto()==null || "".equals(l.getNumLotto())) {
            l.setNumLotto("1");
          }
          lotti.add(l);
        }
        if(g.getTipgarg()!=null) {
          proceduraScelta = g.getTipgarg().toString();
        }
      }
      pg.setLotti(lotti);
      

      if(maxLottiOfferta==null)
        maxLottiOfferta=lotti.size();
      if(maxLottiAggiudicabili==null)
        maxLottiAggiudicabili=lotti.size();
      
      pg.setMaxLottiOfferta(maxLottiOfferta);
      pg.setMaxLottiAggiudicabili(maxLottiAggiudicabili);
      
      logger.debug("proceduraScelta: "+proceduraScelta);
      InviaVigilanzaManager inviaVigilanzaManager = (InviaVigilanzaManager) UtilitySpring.getBean("inviaVigilanzaManager", sc, InviaVigilanzaManager.class);
      proceduraScelta = inviaVigilanzaManager.getFromTab2("A1z11", Long.valueOf(proceduraScelta),false);
      logger.debug(" => A1z11:"+proceduraScelta);
      pg.setTipoProcedura(proceduraScelta);
      // aggiunta pubbli
      List<Pubbli> pubbliList = dm.getListPubbliFullSelectByCodgarAndTIPPUB3(codice);
      if(pubbliList!=null && !pubbliList.isEmpty()) {
        TabellatiManager tabm =  (TabellatiManager) UtilitySpring.getBean("tabellatiManager", sc, TabellatiManager.class);
        List<Tabellato> listaTab = tabm.getTabellato("A1008", 1);
        Map<Integer,TipoPubblicazioneDto > oggettoTabellato = new HashMap<Integer,TipoPubblicazioneDto>();
        for(Tabellato tab : listaTab) {
          oggettoTabellato.put(Integer.valueOf(tab.getTipoTabellato()), new TipoPubblicazioneDto(Integer.valueOf(tab.getTipoTabellato()),tab.getDescTabellato()));
        }
        List<Tabellato> listaTipoAppalto = tabm.getTabellato("A1007", 1);
        Map<Integer,String > tipoAppalto = new HashMap<Integer,String>();
        for(Tabellato tab : listaTipoAppalto) {
          tipoAppalto.put(Integer.valueOf(tab.getTipoTabellato()), tab.getDescTabellato());
        }
        List<PubblicazioniDto> lpdto = new ArrayList<PubblicazioniDto>(pubbliList.size());
        PubblicazioniDto dto = null;
        for(Pubbli p : pubbliList) {
          dto = new PubblicazioniDto();
          dto.setTipoPubblicazione(oggettoTabellato.get(p.getTippub()));
          //aggiunti titolo, numero pubblicazione, numero dell'avviso o bando, url documento
          dto.setTedUrl(p.getUrlpub());
          dto.setTitle(p.getTitpub());
          dto.setNojcnNumber(p.getNavpub());
          dto.setTedReceptionId(p.getNavnum());
          lpdto.add(dto);
        }
        pg.setPubblicazioni(lpdto);
      }
      
      if(pc!=null) {
        map.put("serviceProviderURL", StringUtils.trimToNull(pc.getValore()));
      }
      map.put("procedura", pg);
      
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
      logger.debug("DgueCallBack time execution "+(System.currentTimeMillis()-start)+"ms");
    }
    return Response.status(Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON_TYPE).build();
  }

  private JSONObject decodeData(String dataToBeDecoded, PropsConfig paramDgueSymKey) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException, Base64DecodingException, IllegalBlockSizeException, BadPaddingException {
    Cipher cipher = SymmetricEncryptionUtils.getDecoder(Base64.decode(paramDgueSymKey.getValore()));
    return JSONObject.fromObject(new String(cipher.doFinal(Base64.decode(dataToBeDecoded.getBytes()))));
  }

  private String buildAddress(String viaein, String nciein) {
    StringBuilder sb = new StringBuilder();
    if(StringUtils.isNotEmpty(viaein))
      sb.append(viaein);
    if(StringUtils.isNotEmpty(nciein)) {
      if(StringUtils.isNotEmpty(viaein))
         sb.append(", ");
      sb.append(nciein);
    }
    return sb.toString();
  }
  
  private String buildCity(String citein, String proein) {
    StringBuilder sb = new StringBuilder();
    if(StringUtils.isNotEmpty(citein))
      sb.append(citein);
    if(StringUtils.isNotEmpty(proein)) {
      if(StringUtils.isNotEmpty(citein))
        sb.append(" ");
         sb.append("("+ proein+")");
    }
    return sb.toString();
  }
}
