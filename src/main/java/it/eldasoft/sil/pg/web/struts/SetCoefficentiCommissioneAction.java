package it.eldasoft.sil.pg.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;

public class SetCoefficentiCommissioneAction extends Action {

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

    String idCridef = request.getParameter("idcridef");
    String ngara = request.getParameter("gara");
    String ditta = request.getParameter("ditta");
    Long necvan = null;
    if (request.getParameter("necvan") != null && !"null".equals(request.getParameter("necvan"))) {
      necvan = new Long(request.getParameter("necvan"));
    }
    Long idcrival = null;
    if (request.getParameter("idcrival") != null && !"null".equals(request.getParameter("idcrival"))) {
      idcrival = new Long(request.getParameter("idcrival"));
    }
    int length = 0;
    if (request.getParameter("length") != null && !"null".equals(request.getParameter("length"))) {
      length = new Long(request.getParameter("length")).intValue();
    }

    String oggEvento = ngara;
    int livEvento = 1;
    String codEvento = "GA_OEPV_ASSEGNA_COEFFI_COM";
    String descrEvento = "";
    String errMsgEvento  = "";

    TransactionStatus status = null;
    boolean commitTransaction = false;
    try {
      status = this.sqlManager.startTransaction();
      //Si controlla se esiste l'occorrenza in G1CRIVAL, se non esiste la si aggiunge, altrimenti la si aggiorna
      if (idcrival != null) {
        this.sqlManager.update("update g1crival set coeffi = null, punteg = null where id = ?", new Object[] { idcrival });
      } else {
        //In alcuni casi vengono inserite più occorrenze uguali, quindi prima di inserire, si effettua un controllo andando a guardare
        // i campi ngara, dittao, necvan, idcridef
        Long id = (Long)this.sqlManager.getObject("select id from g1crival where ngara=? and dittao=? and necvan=? and idcridef=?",
            new Object[] {ngara, ditta, necvan, new Long(idCridef)});
        if(id == null) {
          idcrival = new Long(this.genChiaviManager.getNextId("G1CRIVAL"));
          this.sqlManager.update("insert into G1CRIVAL(id, ngara, dittao, necvan, idcridef) values(?,?,?,?,?)",
              new Object[] { new Long(idcrival), ngara, ditta, necvan, new Long(idCridef) });
        }
      }

      String prefixIdgfof ="idgfof_";
      String prefixCoeffi ="coeffCommissario_";
      String prefixNote ="noteCommissario_";
      String prefixIdcrivalcom ="idcrivalcom_";
      String prefixNomfof ="nomfof_";
      String prefixUpdate ="update_";
      for(int i=0;i<length;i++){

        String idcrivalcomStr = request.getParameter(prefixIdcrivalcom+i);
        String idgfofStr = request.getParameter(prefixIdgfof+i);
        String coeffiStr = request.getParameter(prefixCoeffi+i);
        String note = request.getParameter(prefixNote+i);
        String update = request.getParameter(prefixUpdate+i);
        String nomfof = request.getParameter(prefixNomfof+i);
        Long idcrivalcom = null;
        Long idgfof = null;
        Double coeffi = null;

        if("true".equals(update)){
          if(idcrivalcomStr != null && !"null".equals(idcrivalcomStr) && !"".equals(idcrivalcomStr)){ idcrivalcom = new Long(idcrivalcomStr); }
          if(idgfofStr != null && !"null".equals(idgfofStr)){ idgfof = new Long(idgfofStr); }
          if(coeffiStr != null && !"null".equals(coeffiStr) && !"".equals(coeffiStr)){ coeffi = Double.parseDouble(coeffiStr); }

          if(idcrivalcom == null){
            //In alcuni casi vengono inserite più occorrenze uguali, quindi prima di inserire, si effettua un controllo andando a guardare
            // i campi idcrival, idgfof, idcridef
            Long id = (Long)this.sqlManager.getObject("select id from g1crivalcom where idcrival = ? and  idgfof= ? and idcridef = ?",
                new Object[] {idcrival, idgfof, new Long(idCridef)});
            if(id == null) {
              int idInt = this.genChiaviManager.getNextId("G1CRIVALCOM");
              idcrivalcom =  new Long(idInt);
              this.sqlManager.update("insert into G1CRIVALCOM(id, idcrival, idgfof, idcridef, coeffi, note) values(?,?,?,?,?,?)",
                  new Object[] { idcrivalcom, idcrival, idgfof, new Long(idCridef), coeffi ,note});
            }
          }else {
            this.sqlManager.update("update g1crivalcom set coeffi = ? , note = ? where id = ?", new Object[] { coeffi,note, idcrivalcom });
          }

          descrEvento = "Assegna coefficiente criterio di valutazione per l'operatore '"+ditta+
          "' da parte del commissario '" + nomfof + "' (num.criterio: "+necvan+" - id.g1crivalcom: "+idcrivalcom+")";

          LogEvento logEvento = LogEventiUtils.createLogEvento(request);
          logEvento.setCodApplicazione("PG");
          logEvento.setOggEvento(oggEvento);
          logEvento.setLivEvento(livEvento);
          logEvento.setCodEvento(codEvento);
          logEvento.setDescr(descrEvento);
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);

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
    return null;
  }
}
