/*
 * Created on 13/12/2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.bl.SmatManager;

public class CalcolaImportoDiGaraDaRdaAction extends ActionBaseNoOpzioni {

  static Logger               logger = Logger.getLogger(CalcolaImportoDiGaraDaRdaAction.class);

  private SqlManager          sqlManager;
  
  private PgManagerEst1 pgManagerEst1;
  
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  
  public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
    this.pgManagerEst1 = pgManagerEst1;
  }
  
  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    
    if (logger.isDebugEnabled()) logger.debug("CalcolaImportoDiGaraDaRdaAction: inizio metodo");

    String target = "success";
    String codgar = request.getParameter("codgar");
    String ngara = request.getParameter("ngara");
    String importo = request.getParameter("importo");

    if(StringUtils.isNotEmpty(codgar)) {   
      TransactionStatus status = null;
      boolean commitTransaction = false;
      
      try {
        status = this.sqlManager.startTransaction();
        Double imp = null;
        if(!"0".equals(importo))
          imp = Double.valueOf(importo);
        ngara = StringUtils.stripToNull(ngara);
        
        List<Object> parameters = new ArrayList<Object>();
        parameters.add(imp);
        parameters.add(codgar);
        
        if(codgar.indexOf("$")==0 || ngara!=null) {
          String query = "update gare set impapp=?, IMPMIS = null, IMPNRM=null, IMPSMI=null, IMPCOR=null, IMPNRC=null, IMPSCO=null, ONPRGE=null, IMPNRL=null, IMPSIC=null where codgar1 = ? ";
          if(ngara!=null) {
            query += "and ngara = ?";
            parameters.add(ngara);
          }else {
            query += "and codiga is null";
          }
          this.sqlManager.update(query, parameters.toArray());
          
          //aggiorno imptor per la tornata se siamo in un lotto
          if(ngara!=null) {
            Double importoTotaleLotti = this.pgManagerEst1.updImportoTotaleTorn(codgar);
            this.sqlManager.update("update torn set imptor =? where codgar = ?",new Object[] {importoTotaleLotti,codgar});
          }
        }
        
        commitTransaction = true;
      } catch (Exception e) {
        commitTransaction = false;
        logger.error("Errore nell'aggiornamento dell'importo della gara", e);
        String messageKey = "errors.database.dataAccessException";
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey, e.getMessage());
      } finally {
        try {
          if (status != null) {
            if (commitTransaction) {
              this.sqlManager.commitTransaction(status);
            } else {
              this.sqlManager.rollbackTransaction(status);
              target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;  
            }
          }
        }catch(SQLException e) {
          logger.error("Errore nell'aggiornamento dell'importo della gara", e);
        }
      }
    }
    if (logger.isDebugEnabled()) logger.debug("CalcolaImportoDiGaraDaRdaAction: fine metodo");
    return mapping.findForward(target);
  }
}
