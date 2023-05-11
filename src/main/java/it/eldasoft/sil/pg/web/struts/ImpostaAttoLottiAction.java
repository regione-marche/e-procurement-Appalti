package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneProgrammazioneManager;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONObject;

public class ImpostaAttoLottiAction extends Action {

  static Logger logger = Logger.getLogger(ImpostaAttoLottiAction.class);

  private SqlManager sqlManager;
  
  private GestioneProgrammazioneManager gestioneProgrammazioneManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  
  public void setGestioneProgrammazioneManager(GestioneProgrammazioneManager gestioneProgrammazioneManager) {
    this.gestioneProgrammazioneManager = gestioneProgrammazioneManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    String codiceGara = request.getParameter("codiceGara");
    String tattoa = request.getParameter("tattoa");
    String dattoa = request.getParameter("dattoa");
    String nattoa = request.getParameter("nattoa");
    String nproaa = request.getParameter("nproaa");

    Long tattoaLong=null;
    if(tattoa!=null && !"".equals(tattoa))
      tattoaLong = new Long(tattoa);

    Date dattoaDate=null;
    dattoaDate = UtilityDate.convertiData(dattoa, UtilityDate.FORMATO_GG_MM_AAAA);

    TransactionStatus status = null;
    boolean commitTransaction = false;

    int livEvento = 3;
    String errMsgEvento="";

    try {
      status = this.sqlManager.startTransaction();
      this.sqlManager.update("update gare set tattoa = ?,  dattoa = ?,nattoa = ?,nproaa = ?  where codgar1 = ? ", new Object[] { tattoaLong, dattoaDate,nattoa,nproaa, codiceGara });

      //Integrazione programmazione
      if(gestioneProgrammazioneManager.isAttivaIntegrazioneProgrammazione())
        this.gestioneProgrammazioneManager.aggiornaRdaGara(codiceGara,null,null);

      commitTransaction = true;
      livEvento = 1;
    } catch (GestoreException e) {
      commitTransaction = false;
      errMsgEvento =e.getMessage();
      logger.error("Si e' verificato un errore durante l'aggiornamento dello stato delle RdA collegate alla gara " + codiceGara, e);
    }catch (Exception e) {
      commitTransaction = false;
      errMsgEvento =e.getMessage();
      logger.error("Errore nell'aggiornamento degli atti di aggiudicazione della gara " + codiceGara, e);
    } finally {
      if (status != null) {
        if (commitTransaction) {
          this.sqlManager.commitTransaction(status);
        } else {
          this.sqlManager.rollbackTransaction(status);
        }
      }

      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setLivEvento(livEvento);
      logEvento.setOggEvento(codiceGara);
      logEvento.setCodEvento("GA_ATTOAGG_COMUNE");
      logEvento.setDescr("Impostazione atto di aggiudicazione comune a tutti i lotti");
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);

      String esito="ok";
      if(livEvento==3)
        esito="nok";
      result.put("esito", esito);

    }

    out.print(result);
    out.flush();

    return null;
  }
}
