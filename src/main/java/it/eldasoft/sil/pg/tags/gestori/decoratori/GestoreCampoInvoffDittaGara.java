/*
 * Created on 06/03/20
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.decoratori;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

/**
 * Gestore del campo fittizzio numero offerte della pagina delle fasi di iscrizione,
 * per tale campo si deve calcolare il numero totale di offerte presentate dall'operatore
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoInvoffDittaGara extends AbstractGestoreCampo {

    @Override
  public String gestisciDaTrova(Vector params, DataColumn colWithValue,
            String conf, SqlManager manager) {
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
  public String getValore(String valore) {
        return null;
    }

    @Override
  public String getValorePerVisualizzazione(String valore) {

        SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
                this.getPageContext(), SqlManager.class);
        HashMap datiRiga = (HashMap) this.getPageContext().getAttribute("datiRiga",
                PageContext.REQUEST_SCOPE);

        String dittao = datiRiga.get("DITG_DITTAO").toString();
        String ngara = datiRiga.get("DITG_NGARA5").toString();
        String rtofferta = datiRiga.get("DITG_RTOFFERTA").toString();
        
        String valoreCampo = "No";
        String filtroAcq = "";
        
        if(rtofferta!=null && !"".equals(rtofferta)){
          dittao = rtofferta;
          filtroAcq = " and acquisizione = 5";
        }
        try {
            String selectInvoff = "select invoff from ditg where dittao = ? and ngara5 = ?" + filtroAcq;
            String invoff = (String) sqlManager.getObject(selectInvoff, new Object[]{dittao,ngara});
            if("1".equals(invoff)){
              valoreCampo="Sì";
            }
        } catch (SQLException e) {
        }
        return valoreCampo;
    }
    
    @Override
  public String getValorePreUpdateDB(String valore) {
        return null;
    }

    @Override
  protected void initGestore() {

    }

    @Override
  public String postHTML(boolean visualizzazione, boolean abilitato) {
        return null;
    }

    @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {
        return null;
    }


  @Override
  public String getHTML(boolean visualizzazione, boolean abilitato) {

    return null;
  }

}