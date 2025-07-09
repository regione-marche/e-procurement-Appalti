package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.eldasoft.gene.ws.objectstorage.ObjectStorageUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPAllegatoType;
import net.sf.json.JSONObject;

public class InserisciAllegatoOdaComunicazioniAction extends Action {

  private SqlManager sqlManager;

  private GestioneWSERPManager gestioneWSERPManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    String descrizioneFile = request.getParameter("descrizioneFile");
    String nomeFile = request.getParameter("nomeFile");
    String codgar = request.getParameter("codgar");
    String idcomunicazione = request.getParameter("idcom");

    String servizio = request.getParameter("servizio");
    if(servizio==null || "".equals(servizio)){
      servizio ="WSERP";
    }

    ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long syscon = new Long(profilo.getId());
    String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);

    String username = credenziali[0];
    String password = credenziali[1];

    WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi(servizio);
    String tipoWSERP = configurazione.getRemotewserp();

    switch(tipoWSERP) {
    case "CONI":
    {

      WSERPAllegatoType allegato = gestioneWSERPManager.wserpLeggiAllegato(username, password, nomeFile, servizio, descrizioneFile, "ODA");

      String nomeallegatodocumentale = allegato.getNome();
      byte[] contenutoallegatodocumentale =  allegato.getContenuto();


      TransactionStatus status = null;
      boolean commit = true;

      try {
        status = this.sqlManager.startTransaction();
        Long maxContatore = (Long)this.sqlManager.getObject("select coalesce(max(iddocdig),0) + 1 from W_DOCDIG where idprg = ?", new Object[] {"PG"});
        String insert="insert into w_docdig(idprg,iddocdig,digent,digkey1,digkey2,digtipdoc,dignomdoc,digdesdoc,digogg) values(?,?,?,?,?,?,?,?,?)";
        LobHandler lobHandler = new DefaultLobHandler();
        this.sqlManager.update(insert, new Object[] {"PG", maxContatore, "W_INVCOM","PG",idcomunicazione,null,nomeallegatodocumentale,"Documento ordine "+descrizioneFile,new SqlLobValue(contenutoallegatodocumentale, lobHandler)});
        ObjectStorageUtils.addFile("PG",maxContatore);
        
        } catch (Throwable t) {
        commit = false;
      } finally {
        if (status != null) {

          if (commit == true) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }

        }
      }

    }; break;
    }//switch;



    out.print(result);
    out.flush();
    return null;

  }
}



