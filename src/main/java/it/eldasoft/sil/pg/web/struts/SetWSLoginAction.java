package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

public class SetWSLoginAction extends Action {

  private SqlManager sqlManager;
  
  private GenChiaviManager    genChiaviManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  
   public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
     this.genChiaviManager = genChiaviManager;
   }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    String syscon = null;

    String servizio = request.getParameter("servizio");
    String idconfi = request.getParameter("idconfi");
    
    String filtroConfi = "";
    if ("FASCICOLOPROTOCOLLO".equals(servizio) || "DOCUMENTALE".equals(servizio)){
      idconfi = request.getParameter("idconfi");
      filtroConfi = " and idconfiwsdm = " + idconfi;
    }
    
    if("WSERP".equals(servizio) || "WSERP_L190".equals(servizio)){
      syscon = "-1";
    }else{
      String wsdmLoginComune = ConfigManager.getValore(GestioneWSDMManager.PROP_WSDM_LOGIN_COMUNE+idconfi);
      if (wsdmLoginComune != null && "1".equals(wsdmLoginComune)) {
        syscon = "-1";
      } else {
        syscon = request.getParameter("syscon");
      }
    }

    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String ruolo = request.getParameter("ruolo");
    String nome = request.getParameter("nome");
    String cognome = request.getParameter("cognome");
    String codiceuo = request.getParameter("codiceuo");
    String idutente = request.getParameter("idutente");
    String idutenteunop = request.getParameter("idutenteunop");
    
    if (syscon != null && !"".equals(syscon) && servizio != null && !"".equals(servizio)) {

      String passwordEncoded = null;
      if (password != null && password.trim().length() > 0) {
        ICriptazioneByte passwordICriptazioneByte = null;
        passwordICriptazioneByte = FactoryCriptazioneByte.getInstance(
            ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), password.getBytes(),
            ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
        passwordEncoded = new String(passwordICriptazioneByte.getDatoCifrato());
      }

      TransactionStatus status = null;
      boolean commitTransaction = false;
      try {
        status = this.sqlManager.startTransaction();
        Long cnt = (Long) this.sqlManager.getObject("select count(*) from wslogin where syscon = ? and servizio = ?" + filtroConfi, new Object[] {
            new Long(syscon), servizio });
        if (cnt != null && cnt.longValue() > 0) {
          this.sqlManager.update(
              "update wslogin set username = ?, password = ?, ruolo = ?, nome = ?, cognome = ?, codiceuo = ? , idutente = ?, idutenteunop = ? " +
              "where syscon = ? and servizio = ?" + filtroConfi,
              new Object[] { username, passwordEncoded, ruolo, nome, cognome, codiceuo, idutente, idutenteunop, new Long(syscon), servizio });
        } else {
          Long id = new Long(this.genChiaviManager.getNextId("WSLOGIN"));
          if ("FASCICOLOPROTOCOLLO".equals(servizio) || "DOCUMENTALE".equals(servizio)){
            this.sqlManager.update(
                "insert into wslogin (syscon, servizio, username, password, ruolo, nome, cognome, codiceuo, idutente, idutenteunop,idconfiwsdm,id) values (?,?,?,?,?,?,?,?,?,?,?,?)",
                new Object[] { new Long(syscon), servizio, username, passwordEncoded, ruolo, nome, cognome, codiceuo, idutente, idutenteunop, new Long(idconfi),id });
          }else{
            this.sqlManager.update(
                "insert into wslogin (syscon, servizio, username, password, ruolo, nome, cognome, codiceuo, idutente, idutenteunop,id) values (?,?,?,?,?,?,?,?,?,?,?)",
                new Object[] { new Long(syscon), servizio, username, passwordEncoded, ruolo, nome, cognome, codiceuo, idutente, idutenteunop,id });
          }
          
        }

        commitTransaction = true;
      } catch (Exception e) {
        commitTransaction = false;
        throw e;
      } finally {
        if (status != null) {
          if (commitTransaction) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        }
      }

    }

    return null;

  }
}
