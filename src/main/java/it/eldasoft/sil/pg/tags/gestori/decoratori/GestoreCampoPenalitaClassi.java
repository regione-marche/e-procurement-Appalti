/*
 * Created on 17/05/18
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
 * Gestore del campo fittizzio PEN della pagina delle categorie di iscrizione di un operatore.
 * Si deve impostare a 'sì' se risultano assegnate delle penalità sulle classi della categoria (INVPEN + ALTPEN.ISCRIZCLASSI).
 * Si devono conteggiare gli inviti virtuali (INVPEN.ISCRIZCLASSI) solo se TIPOALGO.GAREALBO = 5
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoPenalitaClassi extends AbstractGestoreCampo {

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

      String ngara = datiRiga.get("ISCRIZCAT_NGARA").toString();
      String dittao = datiRiga.get("ISCRIZCAT_CODIMP").toString();
      String categoria = datiRiga.get("ISCRIZCAT_CODCAT").toString();

      String valoreCampo="No";

      try {
        Long tipoalgo = (Long)sql.getObject("select tipoalgo from garealbo where ngara=?", new Object[]{ngara});
        String selectIscrizclassi="select count(codcat) from iscrizclassi where codimp = ? and ngara = ? and codcat=? and ((ALTPEN is not null and ALTPEN>0)";
        if(tipoalgo != null && tipoalgo.longValue()==5){
          selectIscrizclassi += " or (INVPEN is not null and INVPEN>0)";
        }
        selectIscrizclassi += ")";
        Long numPenalitaIscrizclassi = (Long)sql.getObject(selectIscrizclassi, new Object[]{dittao,ngara,categoria});

        if(numPenalitaIscrizclassi != null && numPenalitaIscrizclassi.longValue()>0)
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