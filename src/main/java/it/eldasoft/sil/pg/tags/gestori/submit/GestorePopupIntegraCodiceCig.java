/*
 * Created on 11/10/12
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import it.eldasoft.sil.pg.bl.PgManager;

import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per valorizzare i campi GARE.CODCIG e GARE.DACQCIG
 *
 * @author Francesco.DiMattei
 */
public class GestorePopupIntegraCodiceCig extends AbstractGestoreEntita {

  private GenChiaviManager genChiaviManager = null;
  
  /** Manager di PG */
  private PgManager        pgManager        = null;
  
  
  public GestorePopupIntegraCodiceCig() {
    super(false);
  }

  @Override
  public String getEntita() {
    return "GARE";
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    
    genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);
    
    // Estraggo il manager di Piattaforma Gare
    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);
  }
  
  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    // lettura dei parametri di input
    String ngara = datiForm.getString("NGARA");
    String codgar1 = datiForm.getString("CODGAR1");
    String plicoUnico = UtilityStruts.getParametroString(this.getRequest(),"plicoUnico");
    String codcig = datiForm.getString("CODCIG");
    String numavcp = datiForm.getString("NUMAVCP");
    String esenteCig = datiForm.getString("ESENTE_CIG");
    Date dacqcig = datiForm.getData("DACQCIG");
    String codcigPerUpdate = null;
    String sql = null;
    
    try {
      // Gestione del codice CIG fittizio
      if ("1".equals(esenteCig)) {
        int nextId = this.genChiaviManager.getNextId("GARE.CODCIG");
        String codCigFittizio = "#".concat(StringUtils.leftPad("" + nextId, 9, "0"));
        codcigPerUpdate = codCigFittizio;          
      } else {
        //codice CIG Duplicato
        if (StringUtils.isNotEmpty(codcig)) {
          String msg = pgManager.controlloUnicitaCIG(codcig,ngara);
          if (msg != null) {
            throw new GestoreException(
                "Errore durante l'aggiornamento del campo GARE.CODCIG", "gare.codiceCIGDuplicato",new Object[] {msg },  new Exception());
          }
          codcigPerUpdate = codcig;
          if(!codcig.startsWith("#") && !codcig.startsWith("$") && !codcig.startsWith("NOCIG") && Character.isDigit(codcig.charAt(0))){
            if (!StringUtils.isNotEmpty(numavcp)) {
              throw new GestoreException(
                  "Errore durante l'aggiornamento del campo TORN.NUMAVCP", "gare.codiceANACNullo",  new Exception());
            }
          }
        }
        
      }
      if(StringUtils.isNotEmpty(numavcp)){
        String updateAnac = "update torn set NUMAVCP = ? where codgar = ? and NUMAVCP is null";
        this.getSqlManager().update(updateAnac, new Object[] { numavcp, codgar1 });   
      }
      
      if ("No".equals(plicoUnico)) {
        sql = "update gare set CODCIG=?, DACQCIG=?  where NGARA = ?";
        this.getSqlManager().update(sql, new Object[] { codcigPerUpdate, dacqcig, ngara });       
      }else{
        sql = "update gare set CODCIG=? where NGARA = ?";
        this.getSqlManager().update(sql, new Object[] { codcigPerUpdate, ngara });    
        sql = "update gare set DACQCIG=? where NGARA = ?";
        this.getSqlManager().update(sql, new Object[] {dacqcig, codgar1 });
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante l'aggiornamento del campo GARE.CODCIG", null,  e);
    }
    // setta l'operazione a completata, in modo da scatenare il reload della
    // pagina principale
    this.getRequest().setAttribute("esito", "1");
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
