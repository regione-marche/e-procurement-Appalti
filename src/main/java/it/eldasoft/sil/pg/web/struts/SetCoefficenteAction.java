package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

public class SetCoefficenteAction extends Action {

  private SqlManager sqlManager;
  private GenChiaviManager genChiaviManager;

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

    String ngara = request.getParameter("gara");
    String ditta = request.getParameter("ditta");
    String tipoDettaglio = request.getParameter("tipoDettaglio");
    String note = request.getParameter("note");
    Double coeff = null;
    if (request.getParameter("coeff") != null && !"".equals(request.getParameter("coeff"))) {
      coeff = new Double(request.getParameter("coeff"));
    }
    Double punteg = null;
    if (request.getParameter("punteg") != null && !"".equals(request.getParameter("punteg"))) {
      punteg = new Double(request.getParameter("punteg"));
    }
    Long idCridef = null;
    if (request.getParameter("idCridef") != null && !"".equals(request.getParameter("idCridef"))) {
      idCridef = new Long(request.getParameter("idCridef"));
    }
    Long necvan = null;
    if (request.getParameter("necvan") != null && !"".equals(request.getParameter("necvan"))) {
      necvan = new Long(request.getParameter("necvan"));
    }
    
    String oggEvento = ngara;
    int livEvento = 1;
    String codEvento = "GA_OEPV_ASSEGNA_COEFFI_TOT";
    String descrEvento = "Assegna coefficiente criterio di valutazione per l'operatore '"+ditta+"' ";
    String errMsgEvento  = "";
    
    TransactionStatus status = null;
    boolean commitTransaction = false;
    try {
      status = this.sqlManager.startTransaction();
      //Si controlla se esiste l'occorrenza in G1CRIVAL, se non esiste la si aggiunge, altrimenti la si aggiorna
      Long id = (Long)sqlManager.getObject("select id from g1crival where ngara=? and dittao=? and necvan=? and idcridef=?",
          new Object[]{ngara, ditta, necvan, idCridef});
      if (id != null) {
        this.sqlManager.update("update g1crival set coeffi = ?, punteg = ?, note = ? where id = ?", new Object[] { coeff, punteg, note, id });
      } else {
        int idInt = this.genChiaviManager.getNextId("G1CRIVAL");
        id = new Long(idInt);
        this.sqlManager.update("insert into G1CRIVAL(id, ngara, dittao, necvan, idcridef, coeffi, punteg, note) values(?,?,?,?,?,?,?,?)",
            new Object[] { id, ngara, ditta, necvan, idCridef, coeff, punteg, note});
      }
      
      descrEvento += " (num.criterio: "+necvan+" - id.g1crival: "+id+")";
      
      //Aggiornamento della fase della gara
      Long fasgar=null;
      Long stepgar=null;
      if("1".equals(tipoDettaglio))
        stepgar = new Long(GestioneFasiGaraFunction.FASE_VALUTAZIONE_TECNICA);
      else
        stepgar = new Long(GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE);
      Double faseDouble = new Double(Math.floor(stepgar.doubleValue() / 10));
      this.sqlManager.update("update gare set fasgar=?, stepgar=? where ngara=?", new Object[]{new Long(faseDouble.longValue()),
          stepgar, ngara});
      commitTransaction = true;
      
      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setCodApplicazione("PG");
      logEvento.setOggEvento(oggEvento);
      logEvento.setLivEvento(livEvento);
      logEvento.setCodEvento(codEvento);
      logEvento.setDescr(descrEvento);
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);
      
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
    return null;
  }
}
