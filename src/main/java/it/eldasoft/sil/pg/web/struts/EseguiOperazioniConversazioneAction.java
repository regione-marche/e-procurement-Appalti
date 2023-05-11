package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

/**
 * @author Stefano.Cestaro
 * 
 */
public class EseguiOperazioniConversazioneAction extends Action {

  public static final String PUBBLICA_MESSAGGIO      = "pubblicaMessaggio";
  public static final String SET_MESSAGGIO_LETTO     = "setMessaggioLetto";
  public static final String SET_MESSAGGIO_NON_LETTO = "setMessaggioNonLetto";

  public static final Long   DESTINATARI_TUTTI       = new Long(1);
  public static final Long   DESTINATARI_ALCUNI      = new Long(2);

  private SqlManager         sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  private GeneManager geneManager;

  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");

    Long discid_p = null;
    if (request.getParameter("discid_p") != null && request.getParameter("discid_p") != "") {
      discid_p = new Long(request.getParameter("discid_p"));
    }

    Long discid = null;
    if (request.getParameter("discid") != null && request.getParameter("discid") != "") {
      discid = new Long(request.getParameter("discid"));
    }

    Long syscon = null;
    if (request.getParameter("syscon") != null && request.getParameter("syscon") != "") {
      syscon = new Long(request.getParameter("syscon"));
    }

    String operazione = request.getParameter("operazione");

    TransactionStatus status = null;
    boolean commitTransaction = false;
    try {
      status = this.sqlManager.startTransaction();

      if (PUBBLICA_MESSAGGIO.equals(operazione)) {

        // Inserimento dei destinatari
        // Se nella conversazione e' definito "Tutti gli utenti"
        // (W_DISCUSS_P.DISCDESTTYPE == 1) si devono inserire tutti gli utenti
        // della gara/elenco prelevandoli direttamente dalla G_PERMESSI.
        // Se nella conversazione e' definito "Solo alcuni utenti"
        // (W_DISCUSS_P.DISCDESTTYPE == 2) si devono caricare gli utenti
        // definiti nella tabella W_DISCDEST con DISCID_P della conversazione e
        // DISCID == -1 (messaggio fittizio).
        String selectW_DISCUSS_P = "select discdesttype from w_discuss_p where discid_p = ?";
        String selectG_PERMESSI = "select syscon from g_permessi where (codgar = ? or codgar = ?) and syscon <> ?";
        String selectW_DISCDEST = "select destid, destname, destmail from w_discdest where discid_p = ? and discid = ?";
        String deleteW_DISCDEST = "delete from w_discdest where discid_p = ? and discid = ?";
        String insertW_DISCDEST = "insert into w_discdest (discid_p, discid, destnum, destid, destname, destmail) values (?,?,?,?,?,?)";

        Long discdesttype = (Long) sqlManager.getObject(selectW_DISCUSS_P, new Object[] { discid_p });
        
        this.sqlManager.update(deleteW_DISCDEST, new Object[] {discid_p, discid});
        
        if (DESTINATARI_TUTTI.equals(discdesttype)) {
          String codgar = (String) this.sqlManager.getObject("select disckey1 from w_discuss_p where discid_p = ?",
              new Object[] { discid_p });
          List<?> datiG_PERMESSI = this.sqlManager.getListVector(selectG_PERMESSI, new Object[] { codgar, "$" + codgar, syscon });
          for (int p = 0; p < datiG_PERMESSI.size(); p++) {
            Long g_permessi_syscon = (Long) SqlManager.getValueFromVectorParam(datiG_PERMESSI.get(p), 0).getValue();
            String operatoreNome = (String) this.sqlManager.getObject("select sysute from usrsys where syscon = ?",
                new Object[] { g_permessi_syscon });
            String operatoreEMail = (String) this.sqlManager.getObject("select email from usrsys where syscon = ?",
                new Object[] { g_permessi_syscon });
            if (operatoreNome != null && !"".equals(operatoreNome.trim()) && operatoreEMail != null && !"".equals(operatoreEMail)) {
              this.sqlManager.update(insertW_DISCDEST, new Object[] { discid_p, discid, new Long(p + 1), g_permessi_syscon, operatoreNome,
                  operatoreEMail });
            }
          }
        } else if (DESTINATARI_ALCUNI.equals(discdesttype)) {
          List<?> datiW_DISCDEST = this.sqlManager.getListVector(selectW_DISCDEST, new Object[] { discid_p, new Long(-1) });
          for (int u = 0; u < datiW_DISCDEST.size(); u++) {
            Long destid = (Long) SqlManager.getValueFromVectorParam(datiW_DISCDEST.get(u), 0).getValue();
            String destname = (String) SqlManager.getValueFromVectorParam(datiW_DISCDEST.get(u), 1).getValue();
            String destmail = (String) SqlManager.getValueFromVectorParam(datiW_DISCDEST.get(u), 2).getValue();
            this.sqlManager.update(insertW_DISCDEST, new Object[] { discid_p, discid, new Long(u + 1), destid, destname, destmail });
          }
        }

        // Aggiornamento stato di pubblicazione
        String updateW_DISCUSS = "update w_discuss set discmesspubbl = ?, discmessins = ? where discid_p = ? and discid = ?";
        this.sqlManager.update(updateW_DISCUSS, new Object[] { "1", new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), discid_p,
            discid });
      }

      if (SET_MESSAGGIO_LETTO.equals(operazione)) {
        Long cnt = (Long) this.sqlManager.getObject(
            "select count(*) from w_discread where discid_p = ? and discid = ? and discmessope = ?", new Object[] { discid_p, discid,
                syscon });
        if (cnt == null || (cnt != null && cnt.longValue() == 0)) {
          String insertW_DISCREAD = "insert into w_discread (discid_p, discid, discmessope) values (?,?,?)";
          this.sqlManager.update(insertW_DISCREAD, new Object[] { discid_p, discid, syscon });
        }
      }

      if (SET_MESSAGGIO_NON_LETTO.equals(operazione)) {
        String deleteW_DISCREAD = "delete from w_discread where discid_p = ? and discid = ? and discmessope = ?";
        this.sqlManager.update(deleteW_DISCREAD, new Object[] { discid_p, discid, syscon });
      }

      commitTransaction = true;

    } catch (Exception e) {
      commitTransaction = false;
    } finally {
      if (status != null) {
        if (commitTransaction) {
          this.sqlManager.commitTransaction(status);
        } else {
          this.sqlManager.rollbackTransaction(status);
        }
      }
    }

    return null;

  }

}
