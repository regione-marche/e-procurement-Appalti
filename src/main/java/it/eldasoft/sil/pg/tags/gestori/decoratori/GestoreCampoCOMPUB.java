/*
 * Created on 26/gen/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.decoratori;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.gene.tags.decorators.campi.ValoreTabellato;
import it.eldasoft.utils.spring.UtilitySpring;


public class GestoreCampoCOMPUB extends AbstractGestoreCampo {
  
  static Logger logger = Logger.getLogger(GestoreCampoCOMPUB.class);

  @Override
  public String getValore(String valore) {
    return null;
  }

  @Override
  public String getValorePerVisualizzazione(String valore) {
    String valoreVisualizzazione = null;
    if(this.campo.getFormName().indexOf("localformScheda")>-1)
      return null; //non voglio utilizzare il metodo se sono in un form scheda
    if(valore !=null){
      if("1".equals(valore))
        valoreVisualizzazione = "Pubblica";
      else
        valoreVisualizzazione = "Riservata";
    }
    return valoreVisualizzazione;
  }

  @Override
  public String getValorePreUpdateDB(String valore) {
    return null;
  }

  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {
    if(this.campo.getFormName().indexOf("localformScheda")>-1) { //non voglio utilizzare il metodo se sono in un form lista
      String ngara = (String) this.getPageContext().getAttribute("ngara",
          PageContext.PAGE_SCOPE);
  
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          this.getPageContext(), SqlManager.class);
      
      try { 
        List<?> data = sqlManager.getListVector("select isfineconc from gare, torn where gare.codgar1=torn.codgar and gare.ngara=? and (torn.iterga=9 or torn.iterga=10)",new Object[]{ngara} );
        if(data != null && data.size()>0) {
          if(!"1".equals(sqlManager.getValueFromVectorParam(data.get(0), 0).getValue()))//rimuovo dal tabellato il valore 2 se iterga =9, a meno che isfineconc non valga 1
            this.getCampo().getValori().remove(this.getCampo().getValori().indexOf(new ValoreTabellato("2", "Riservata (invio mail o PEC e consultazione in area riservata)")));
        }else {
          this.getCampo().getValori().remove(this.getCampo().getValori().indexOf(new ValoreTabellato("3", "Riservata (solo consultazione in area riservata)"))); //in generale non vogliamo vedere il valore 3 del tabellato. Se iterga<>9, lo si toglie
        }
      }catch(SQLException e) {
        logger.error("Errore nell'estrazione dei valori di compub disponibili per l'entita", e);
      }
    }

    return null;
  }

  @Override
  public String getHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  @Override
  public String postHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  @Override
  public String getClasseEdit() {
    return null;
  }

  @Override
  public String getClasseVisua() {
    return null;
  }

  @Override
  protected void initGestore() {
  }

  @Override
  public String gestisciDaTrova(Vector params, DataColumn col, String conf, SqlManager manager) {
    return null;
  }

}
