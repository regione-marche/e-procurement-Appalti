/*
 * Created on 29/06/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
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

public class SetW_CONFIGAction extends Action {

  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");

    String codapp = request.getParameter("codapp");
    String chiave = request.getParameter("chiave");
    String valore = request.getParameter("valore");
    String criptato = request.getParameter("criptato");

    TransactionStatus status = null;
    boolean commitTransaction = false;
    try {
      status = this.sqlManager.startTransaction();

      if ("1".equals(criptato) && valore != null && !"".equals(valore.trim())) {
        ICriptazioneByte valoreICriptazioneByte = null;
        valoreICriptazioneByte = FactoryCriptazioneByte.getInstance(
            ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), valore.getBytes(),
            ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
        valore = new String(valoreICriptazioneByte.getDatoCifrato());
      }

      Long cnt = (Long) this.sqlManager.getObject("select count(*) from w_config where codapp = ? and chiave = ?", new Object[] { codapp,
          chiave });
      if (cnt != null && cnt.longValue() > 0) {
        this.sqlManager.update("update w_config set valore = ? where codapp = ? and chiave = ?", new Object[] { valore, codapp, chiave });
      } else {
        this.sqlManager.update("insert into w_config (codapp, chiave, valore, criptato) values (?,?,?,?)", new Object[] { codapp, chiave,
            valore, criptato });
      }

      if (valore == null) valore = new String("");
      if (ConfigManager.esisteProprietaDB(chiave)) {
        ConfigManager.ricaricaProprietaDB(chiave, valore);
      } else {
        ConfigManager.caricaProprietaDB(chiave, valore);
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
