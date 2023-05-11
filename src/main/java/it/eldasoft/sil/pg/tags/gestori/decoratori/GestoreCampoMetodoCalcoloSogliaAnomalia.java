/*
 * Created on 04/04/22
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
import java.util.HashMap;

import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.decorators.campi.ValoreTabellato;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreTabellatoNoOpzioneVuota;
import it.eldasoft.utils.spring.UtilitySpring;


public class GestoreCampoMetodoCalcoloSogliaAnomalia extends GestoreTabellatoNoOpzioneVuota {

  private SqlManager sqlManager = null;
	
  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {

      sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
              this.getPageContext(), SqlManager.class);
	  
      HashMap datiRiga = (HashMap) this.getPageContext().getAttribute("datiRiga",
              PageContext.REQUEST_SCOPE);

      String codgar = datiRiga.get("GARE_CODGAR1").toString();
      String numDitte = datiRiga.get("FIT_NUMDITTE").toString();
      
      try {
            String SelectCalcsome = "select calcsome from torn where codgar = ?";
            String calcsome = (String) sqlManager.getObject(SelectCalcsome, new Object[]{codgar});
            if("1".equals(calcsome)){
            	if("2".equals(numDitte) || "3".equals(numDitte) || "4".equals(numDitte)) {
            	      ValoreTabellato opzione = new ValoreTabellato("1", "Metodo A (art.97 c.2/a DLgs 50/2016)");
            	      int posizione = this.getCampo().getValori().indexOf(opzione);

            	      if (posizione >= 0)
            	         this.getCampo().getValori().remove(posizione);

            	      opzione = new ValoreTabellato("2", "Metodo B (art.97 c.2/b DLgs 50/2016)");
            	      posizione = this.getCampo().getValori().indexOf(opzione);

            	      if (posizione >= 0)
            	         this.getCampo().getValori().remove(posizione);
            	      
            	      opzione = new ValoreTabellato("5", "Metodo E (art.97 c.2/e DLgs 50/2016)");
            	      posizione = this.getCampo().getValori().indexOf(opzione);

            	      if (posizione >= 0)
            	         this.getCampo().getValori().remove(posizione);
            	}
            }
        } catch (SQLException e) {
        }

    return null;
  }
}
