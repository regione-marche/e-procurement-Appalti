/*
 * Created on 14-lug-2023
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

/**
 * Gestore per update dei dati
 *
 * @author Manuel Bridda
 */
public class GestoreCriteriValutazioneGOEV extends AbstractGestoreEntita {

  /** Logger */
  static Logger            logger           = Logger.getLogger(GestoreCriteriValutazioneGOEV.class);
  @Override
  public String getEntita() {
    return "GOEV";
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    
    String descrErrMsg = "";
    String bloccoModificaPubblicazione = UtilityStruts.getParametroString(this.getRequest(), "bloccoModificaPubblicazione");
    if("true".equals(bloccoModificaPubblicazione)){
      HashMap<String, DataColumn> hm = datiForm.getColonne();
      Iterator<?> it = hm.entrySet().iterator();

      boolean modificheDaTracciare=false;
      while (it.hasNext()) {
        Entry<String, DataColumn> pair = (Entry<String, DataColumn>)it.next();
        if(pair.getValue().isModified()){
          modificheDaTracciare=true;
         //la gestione è copiata dal gestore gare
          if(!"".equals(descrErrMsg)){
            descrErrMsg+="\n";
          }
          JdbcParametro paramOrigin = pair.getValue().getOriginalValue();
          String valOriginale = "";
          if(paramOrigin!=null){
            valOriginale = paramOrigin.toString();
          }
          JdbcParametro paramNuovo = pair.getValue().getValue();
          String valNuovo = "";
          if(paramNuovo!=null){
            valNuovo = paramNuovo.toString();
          }
          if("".equals(valOriginale)){valOriginale = "NULL";}
          if("".equals(valNuovo)){valNuovo = "NULL";}
          descrErrMsg+= pair.getKey() + ": ";
          descrErrMsg+= "valore vecchio= " + valOriginale + ", ";
          descrErrMsg+= "valore nuovo= " + valNuovo;
          }
        }

      if(modificheDaTracciare){
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(1);
        logEvento.setOggEvento(datiForm.getColumn("NGARA").getValue().getStringValue());
        logEvento.setCodEvento("GA_MODIFICA_CRIDEF_PUB");
        logEvento.setDescr("Modifica dettaglio criterio di valutazione dopo pubblicazione su portale Appalti");
        logEvento.setErrmsg("Dettaglio modifiche:\n" + descrErrMsg);
        LogEventiUtils.insertLogEventi(logEvento);
      }
    }
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

}