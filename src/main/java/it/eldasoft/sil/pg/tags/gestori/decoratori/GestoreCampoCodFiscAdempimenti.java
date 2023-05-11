/*
 * Created on 04/09/13
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

/**
 * Gestore del campo fittizzio CODFISC_FIT della pagina dei partecipanti (anticor-pg-partecipanti.jsp)
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoCodFiscAdempimenti extends AbstractGestoreCampo {

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
	 * Il campo fittizio per il codice fiscale vale:
	 *     ANTICORPARTECIP.CODFISC o ANTICORDITTE.IDFISCEST  se ANTICORPARTECIP.TIPO=1
	 *     vuoto  se ANTICORPARTECIP.TIPO=2
	 *
	 */
	@SuppressWarnings("unchecked")
  @Override
  public String getValorePerVisualizzazione(String valore) {

	  SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager",
          this.getPageContext(), SqlManager.class);
      HashMap datiRiga = (HashMap) this.getPageContext().getAttribute("datiRiga",
              PageContext.REQUEST_SCOPE);

      String tipo = datiRiga.get("ANTICORPARTECIP_TIPO").toString();
      String id = datiRiga.get("ANTICORPARTECIP_ID").toString();

      String valoreCampo="";

      if(tipo!=null && "2".equals(tipo))
        valoreCampo ="";
      else{
        try {
          Vector<JdbcParametro> datiAnticorDitte = sql.getVector("select codfisc, idfiscest from anticorditte where idanticorpartecip=?", new Object[]{new Long(id)});
          if(datiAnticorDitte!=null && datiAnticorDitte.size()>0){
            String codfisc = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(datiAnticorDitte, 0).getStringValue());
            String idfiscest = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(datiAnticorDitte, 1).getStringValue());
            if(codfisc!=null)
              valoreCampo = codfisc;
            else
              valoreCampo = idfiscest;
          }



        } catch (SQLException e) {

        }
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