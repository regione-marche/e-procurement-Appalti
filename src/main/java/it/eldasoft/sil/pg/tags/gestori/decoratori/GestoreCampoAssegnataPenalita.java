/*
 * Created on 24/01/12
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
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

/**
 * Gestore del campo fittizzio ASS_PEN della pagina delle fasi di iscrizione,
 * per tale campo si deve calcolare il numero di penalita assegnate alla categoria
 * o alle classi per cui la ditta è iscritta in elenco
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoAssegnataPenalita extends AbstractGestoreCampo {

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

	/**
	 * Nel campo valore viene passato un valore booleano che indica se effettuare il calcolo
	 * per popolare il campo
	 */
	@Override
  public String getValorePerVisualizzazione(String valore) {

	  SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager",
          this.getPageContext(), SqlManager.class);
      HashMap datiRiga = (HashMap) this.getPageContext().getAttribute("datiRiga",
              PageContext.REQUEST_SCOPE);

      String ngara = datiRiga.get("DITG_NGARA5").toString();
      String dittao = datiRiga.get("DITG_DITTAO").toString();

      String valoreCampo="No";

      try {
        Long tipoalgo = (Long)sql.getObject("select tipoalgo from garealbo where ngara=?", new Object[]{ngara});
        String selectIscrizcat="select count(ngara) from iscrizcat where codimp = ? and ngara = ? and (";
        String selectIscrizclassi="select count(ngara) from iscrizclassi where codimp = ? and ngara = ? and (";

        if(tipoalgo != null && (tipoalgo.longValue()==1 || tipoalgo.longValue()==5)){
          selectIscrizcat += "(INVPEN is not null and INVPEN>0) or";
          selectIscrizclassi += "(INVPEN is not null and INVPEN>0) or";
        }
        selectIscrizcat += "(ALTPEN is not null and ALTPEN>0)) ";
        selectIscrizclassi += "(ALTPEN is not null and ALTPEN>0)) ";

        Long numPenalitaIscrizcat = (Long)sql.getObject(selectIscrizcat, new Object[]{dittao,ngara});
        Long numPenalitaIscrizclassi = (Long)sql.getObject(selectIscrizclassi, new Object[]{dittao,ngara});

        if((numPenalitaIscrizcat != null && numPenalitaIscrizcat.longValue()>0) || (numPenalitaIscrizclassi != null && numPenalitaIscrizclassi.longValue()>0))
           valoreCampo = "Si";

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