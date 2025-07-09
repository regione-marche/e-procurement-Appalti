package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.integrazioni.FirmaRemotaManager;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.utils.MailUtils;
import it.eldasoft.gene.web.struts.SetFirmaDigitaleRemotaAction;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.mail.IMailSender;
import it.eldasoft.utils.mail.MailSenderException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

/**
 * Aggiorna lo stato di una stipula
 */


public class SetFirmaInoltraStipulaMassivaAction extends ActionBaseNoOpzioni {

  Logger             logger = Logger.getLogger(SetFirmaDigitaleRemotaAction.class);
  
  private static final String PROP_INFOCERT_MODAL_AUTO     = "digital-signature-auto-mode";
  private static final String PROP_PROVIDER                = "digital-signature-provider";
  private static final String PROVIDER_COSIGN              = "3";
  private static final String PROP_CLIENT_ID               = "digital-signature-client-id";
  
  private SqlManager sqlManager;
  private FirmaRemotaManager firmaRemotaManager;
  private GenChiaviManager    genChiaviManager;
  private MailManager mailManager;

  public void setFirmaRemotaManager(FirmaRemotaManager firmaRemotaManager) {
    this.firmaRemotaManager = firmaRemotaManager;
  }
  
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }
  
  public void setMailManager(MailManager mailManager) {
    this.mailManager = mailManager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
  
    TransactionStatus transazione = null;
    String select = "select syscon from WSLOGIN where servizio = 'FIRMAREMOTA' and syscon = ? and username = ?";
    
    String alias= request.getParameter("alias");
    String pin = request.getParameter("pin");
    String idprg= request.getParameter("idprg");
    String otp = request.getParameter("otp");
    
    Boolean sendToOriginario = request.getParameter("sendTo").equals("originatario");
    Boolean withFirma = request.getParameter("withFirma").equals("si");
    Boolean isErroreMassiva = false;
    String messageErroreModMassiva = "";
    String entitaFirma = request.getParameter("entitaFirma");
    String chiaveFirma = request.getParameter("chiaveFirma");

    String modalitaFirma = request.getParameter("modalitaFirma");

    String provider = ConfigManager.getValore(PROP_PROVIDER);
    String clientId = ConfigManager.getValore(PROP_CLIENT_ID);
    String tokenSession = "";
    
    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    
    if(profiloUtente == null){
      return null;
    }
    
    Long syscon;
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();
    JSONObject json = new JSONObject();

    String codiceProfilo = null;
    String ip = null;
    Integer idUtente = null;
    String messaggioInoltroNonEseguito = "";
    String messaggioInoltroErrore = "";
    try {
      codiceProfilo = (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);
      if(request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE) != null){
        int idUtenteInt = ((ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId();
        idUtente = new Integer(idUtenteInt);
      }
      ip = request.getRemoteAddr();
      
      syscon = (Long) sqlManager.getObject(select,new Object[]{profiloUtente.getId(),alias} );
      
      String passwordEncoded = null;
      if (pin != null && pin.trim().length() > 0) {
        ICriptazioneByte passwordICriptazioneByte = null;
        passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
            ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), pin.getBytes(),
            ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
        passwordEncoded = new String(passwordICriptazioneByte.getDatoCifrato());
      }
      
      transazione = this.sqlManager.startTransaction();
      
      if(Boolean.TRUE.equals(withFirma)) {
        aggiornaWSLOGIN(syscon, alias, passwordEncoded, profiloUtente.getId());
      }
      
      String mode = "remota";
      if("1".equals(StringUtils.stripToEmpty(ConfigManager.getValore(PROP_INFOCERT_MODAL_AUTO)))){
        mode = "automatica";
      }
      String c0acod = request.getParameter("c0acod");
      c0acod = StringUtils.stripToEmpty(c0acod);
      Long c0acodLong = null;
      if(!"".equals(c0acod)) {
        c0acodLong = Long.valueOf(c0acod);
      }
      
      Boolean isLockUser = false;
      String[] listaChiavi = chiaveFirma.split(",");
      if(listaChiavi.length>0) {
        if (PROVIDER_COSIGN.equalsIgnoreCase(provider.toLowerCase())) {
          tokenSession = firmaRemotaManager.openSignatureSession(mode, alias, pin, otp, clientId);
        }
      
        for (int i = 0; i < listaChiavi.length && Boolean.FALSE.equals(isLockUser); i++) {
          Long chiave = Long.valueOf(listaChiavi[i]);
          
          String query = null;
          switch (entitaFirma) {
          case "DOCSTIPULA":
              query = "select iddocdig, dignomdoc from V_GARE_DOCSTIPULA where digfirma = '1' and idprg = ? and idstipula = ? ";
            break;
    
          default:
            break;
          }
          Boolean isErroreStipula = false;
          if(Boolean.TRUE.equals(withFirma)) {
            List<?> listaIddocdig = this.sqlManager.getListVector(query, new Object[]{idprg,chiave});
          
            if (listaIddocdig != null && !listaIddocdig.isEmpty()) {
              
              isErroreStipula = false;
              
              for (int y = 0; y < listaIddocdig.size() && Boolean.FALSE.equals(isLockUser); y++) {
                Long iddocdig = (Long) SqlManager.getValueFromVectorParam(listaIddocdig.get(y), 0).getValue();
                String nomeDocumento = SqlManager.getValueFromVectorParam(listaIddocdig.get(y), 1).getStringValue();
                if(iddocdig != null){
                  try {
                    firmaRemotaManager.sign(idprg, iddocdig, mode, modalitaFirma, alias, pin, otp, codiceProfilo, idUtente, ip);
                    
                    //se documento associato aggiorno l'occorrenza anche in c0oggass
                    if(c0acodLong != null) {
                      aggiornaOccorrenzeC0oggass(c0acodLong, idprg, iddocdig);
                    }
                  }catch (Exception e) {
                    isErroreStipula = true;
                    if(e.getMessage().contains("ErroreServizioEsternoGestitoCosign:")) {
                      messageErroreModMassiva += "- "+nomeDocumento+": "+e.getMessage().replace("ErroreServizioEsternoGestitoCosign:", "")+"\n";
                    } else {
                      messageErroreModMassiva += "- "+nomeDocumento+": "+e.getMessage()+"\n";
                    }
                    //Errore di autenticazione, uscita anticipata per evitare il lock del profilo
                    if(e.getMessage().contains("019") || e.getMessage().contains("010")) { 
                      isLockUser = true;
                      messageErroreModMassiva = "Uscita anticipata per evitare il lock del profilo "+messageErroreModMassiva;
                    }
                  }
                }
              }
              
              if(Boolean.TRUE.equals(isErroreStipula)) {
                isErroreMassiva = isErroreStipula;
              }
            }
          }
          if(Boolean.FALSE.equals(isErroreStipula)) {
            messaggioInoltroErrore += inoltra(chiave,sendToOriginario);
          } else {
            //recupero il codice stipula per visualizzarlo all'utente
            String codStipula = (String) sqlManager.getObject("select codstipula from g1stipula where id = ?",new Object[]{ chiave });
            
            messaggioInoltroNonEseguito += "Inoltro non eseguito per la stipula "+ codStipula +" in quanto la firma dei documenti ha presentato degli errori: \n";
            messaggioInoltroNonEseguito += "Il sistema di firma non consente di apporre la firma sui seguenti documenti: \n"+messageErroreModMassiva;
            messageErroreModMassiva = ""; //resetto per rinserire eventuali nuovi errori
          }
        }
        
        if (PROVIDER_COSIGN.equalsIgnoreCase(provider.toLowerCase()) && !"".equals(tokenSession)) {
          firmaRemotaManager.closeSignatureSession(mode, alias, pin, otp, clientId, tokenSession);
        }
        
        if(Boolean.TRUE.equals(isErroreMassiva)) {
          throw new Exception(messaggioInoltroNonEseguito);
        } else if(!"".equals(messaggioInoltroErrore)) {
          throw new Exception("Il sistema di Inoltro ha segnalato i seguenti errori: \n"+messaggioInoltroErrore);
        }
        
      }
    } catch (Exception e) {
      if(e.getMessage().contains("ErroreServizioEsternoGestitoCosign:")) {
        this.aggiungiMessaggio(request, e.getMessage().replace("ErroreServizioEsternoGestitoCosign:", ""));
        json.put("message", e.getMessage().replace("ErroreServizioEsternoGestitoCosign:", ""));
      } else {
        this.aggiungiMessaggio(request, e.getMessage());
        json.put("message", e.getMessage());
      }
    }finally{
      if (transazione != null) {
        try {
          sqlManager.commitTransaction(transazione);
          if(json.get("message") == null) {
            json.put("message", "Procedura terminata correttamente.");
          }
        } catch (SQLException e) {
          
        }
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug("setFirmaDigitale: conclusa con successo");
      this.aggiungiMessaggio(request, "everything ok");
    }
    
    out.println(json);
    out.flush();
    return null;
  }
  
  private void aggiornaOccorrenzeC0oggass(Long c0acod, String idprg, Long iddocdig) throws SQLException {
    String nome = (String) this.sqlManager.getObject("select DIGNOMDOC from w_docdig where idprg = ? and iddocdig = ?", new Object[]{idprg, iddocdig});
    this.sqlManager.update("update c0oggass set C0ANOMOGG = ? where C0ACOD = ?", new Object[]{nome, c0acod});
  }
  
  private void aggiornaWSLOGIN(Long syscon, String alias, String passwordEncoded, int idprofiloUtente) throws SQLException {
    String queryUpdate = null;
    if(syscon == null){
      Long id = Long.valueOf(this.genChiaviManager.getNextId("WSLOGIN"));
      queryUpdate = "insert into WSLOGIN(USERNAME,PASSWORD,SERVIZIO,SYSCON,ID) values (?,?,'FIRMAREMOTA',?,?)";
      sqlManager.update(queryUpdate,new Object[]{alias,passwordEncoded,idprofiloUtente,id});
    }else{
      queryUpdate = "update WSLOGIN set USERNAME = ?,PASSWORD = ? where SERVIZIO = 'FIRMAREMOTA' and SYSCON = ?";
      sqlManager.update(queryUpdate,new Object[]{alias,passwordEncoded,idprofiloUtente});
    }
  }
  

  private String inoltra(Long idStipula, Boolean sendToOriginario) throws SQLException, GestoreException {
    String codiceStipula = null;
    String oggetto = null;
    String impStipula = null;
    String nomest = null;
    Long statoStipula = null;
    Long creatore = null;
    String nome_creatore = null;
    Long contract = null;
    String nome_contract = null;
    String ufficio = null;
    
    Vector<?> datiStipula = this.sqlManager.getVector("select codstipula,oggetto,impstipula,nomest,stato,cenint,creatore,nome_creatore,contract,nome_contract from v_gare_stipula where id=?", new Object[]{idStipula});
    if(datiStipula!=null && !datiStipula.isEmpty()){
        codiceStipula = SqlManager.getValueFromVectorParam(datiStipula, 0).stringValue();
        oggetto = SqlManager.getValueFromVectorParam(datiStipula, 1).stringValue();
        impStipula = UtilityNumeri.convertiImporto(SqlManager.getValueFromVectorParam(datiStipula, 2).doubleValue(), 2) + " euro";
        nomest = SqlManager.getValueFromVectorParam(datiStipula, 3).stringValue();
        statoStipula = SqlManager.getValueFromVectorParam(datiStipula, 4).longValue();
        String cenint = SqlManager.getValueFromVectorParam(datiStipula, 5).stringValue();
        creatore = SqlManager.getValueFromVectorParam(datiStipula, 6).longValue();
        nome_creatore = SqlManager.getValueFromVectorParam(datiStipula, 7).stringValue();
        contract = SqlManager.getValueFromVectorParam(datiStipula, 8).longValue();
        nome_contract = SqlManager.getValueFromVectorParam(datiStipula, 9).stringValue();
        if(cenint!= null && !"".equals(cenint)) {
          ufficio = (String)this.sqlManager.getObject("select nomein from uffint where codein=?", new Object[] {cenint});
        }
    }

    if(ufficio==null)
      ufficio="";
    if(nome_creatore==null)
      nome_creatore="";
    if(nome_contract==null)
      nome_contract="";

    String oggettoMail="Inoltro per approvazione";
    String testoMail="Gentile utente,\ncon la presente si informa che Le è stato assegnato il ruolo di \"Contract Manager\" relativamente al seguente contratto:\n\n"
            + "Codice: "+codiceStipula+"\n"+ "Titolo: "+oggetto+"\n"+ "Importo: "+impStipula+"\n"+ "Operatore economico: "+nomest+"\n\n"
            + "Ufficio: "+ufficio + "\n\nContratto creato da: " + nome_creatore + "\nContract Manager uscente: " +  nome_contract + "\n\n\n   Cordiali saluti";

    String eventualeMessaggioErrore = "";
    Long destinatario = Long.valueOf(0);
    if(Boolean.TRUE.equals(sendToOriginario)) {
      if(!contract.equals(creatore)) {
        destinatario = creatore;
      } else {
        eventualeMessaggioErrore = "\nNon e' stato fatto l'inoltro perche' sei gia' l'originario per la stipula: "+ codiceStipula;
      }
    } else { //Caso assegnazione al precedente
      Vector<?> datiIterPrecedente = this.sqlManager.getVector("select da_utente from g1iterstipula where idstipula=? order by dataiter desc", new Object[]{idStipula});
      if(datiIterPrecedente!=null && !datiIterPrecedente.isEmpty()){
        destinatario = SqlManager.getValueFromVectorParam(datiIterPrecedente, 0).longValue();
      } else {
        eventualeMessaggioErrore = "\nNon e' stato fatto l'inoltro perche' non c'e' il precedente per la stipula: "+ codiceStipula;
      }
    }
    
    if(destinatario != null && !destinatario.equals(Long.valueOf(0))) {
      
      try {
        String indirizzoMail = null;
        
        //inserimento in g1iterstipula
        Long nextIterId = Long.valueOf(this.genChiaviManager.getNextId("G1ITERSTIPULA"));
        Date data_step = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
        this.sqlManager.update("insert into g1iterstipula(id, idstipula, dataiter,da_utente,a_utente,titolo,testo )values(?,?,?,?,?,?,?)",
                  new Object[]{nextIterId, idStipula,data_step,contract,destinatario,oggettoMail,testoMail});
          
        Vector<?> datiAssegnatario = this.sqlManager.getVector("select email,emailpec from usrsys where syscon=?", new Object[]{destinatario});
        if(datiAssegnatario!=null && datiAssegnatario.size()>0){
            String email = SqlManager.getValueFromVectorParam(datiAssegnatario, 0).stringValue();
            //String pec = SqlManager.getValueFromVectorParam(datiAssegnatario, 1).stringValue();
            indirizzoMail = email;
        }

        IMailSender mailSender = MailUtils.getInstance(mailManager, ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE),CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD);
        
        this.sqlManager.update("update g1stipula set assegnatario=? where id=?",
                    new Object[]{destinatario, idStipula});
        
        //aggiorno lo stato della stipula se lo stato originario non e' ancora pubblicato 
        if(statoStipula<Long.valueOf(3)) {
           this.sqlManager.update("update g1stipula set stato = ?  where id =?", new Object[] { Long.valueOf(2), idStipula});
        }
        
        mailSender.send(indirizzoMail, oggettoMail, testoMail);
        
      } catch (SQLException e) {
          throw new GestoreException("Errore nella lettura dei dati dell'assegnatario", null, e);
      } catch (MailSenderException ms) {
          String logMessageKey = ms.getChiaveResourceBundle();
          String logMessageError = resBundleGenerale.getString(logMessageKey);
          for (int i = 0; ms.getParametri() != null && i < ms.getParametri().length; i++)
            logMessageError = logMessageError.replaceAll(
                UtilityStringhe.getPatternParametroMessageBundle(i),
                (String) ms.getParametri()[i]);
          String messaggio = "\nATTENZIONE: si sono riscontrati problemi nell'invio mail per la seguente stipula "+codiceStipula+"\n"
                + "L'inoltro al nuovo Contract Manager è stato comunque completato.";
          logger.error(logMessageError, ms);
          eventualeMessaggioErrore = messaggio;
      }

    }
    return eventualeMessaggioErrore;
    
  }
}
