/*
 * Created on 19/04/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

public class EsistonoAllegatiComunicazioneFunction extends AbstractFunzioneTag {

  public EsistonoAllegatiComunicazioneFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        pageContext, PgManagerEst1.class);

    String[] chiave = ((String) params[0]).split(";");
    String idprg = chiave[0];
    Long idcom = new Long(chiave[1]);

    String esistonoAllegatiComunicazione = "FALSE";
    String esistonoAllegatiDaFirmare = "FALSE";
    String esistonoAllegatiFormatoNonValido = "FALSE";

    try {

      List docAllegati = sqlManager.getListVector(
      "select idprg, iddocdig, dignomdoc from w_docdig where "
          + " digkey1 = ? and digkey2 = ? and digent = 'W_INVCOM'",
      new Object[] { idprg, idcom.toString() });

      if (docAllegati != null && docAllegati.size() > 0)
        esistonoAllegatiComunicazione = "TRUE";

      if("TRUE".equals(esistonoAllegatiComunicazione)){
        //Si verifica se vi sono degli allegati in attesa di firma
        Long numAllegatiDaFirmare = (Long)sqlManager.getObject("select count(iddocdig) from w_docdig where "
          + " digkey1 = ? and digkey2 = ? and digent = 'W_INVCOM' and digfirma = ?", new Object[] { idprg, idcom.toString(),"1" });
        if(numAllegatiDaFirmare!=null && numAllegatiDaFirmare.longValue()>0)
          esistonoAllegatiDaFirmare = "TRUE";


        String formatoAllegati = ConfigManager.getValore(CostantiAppalti.FORMATO_ALLEGATI);
        if(formatoAllegati!=null && !"".equals(formatoAllegati)){
          List listaFileAllegati=sqlManager.getListVector("select dignomdoc from w_docdig where digkey1 = ? and digkey2 = ? and digent = 'W_INVCOM'",
              new Object[]{idprg, idcom.toString()});
          if(!pgManagerEst1.controlloAllegatiFormatoValido(listaFileAllegati,0,formatoAllegati))
            esistonoAllegatiFormatoNonValido = "TRUE";
        }
      }

    } catch (SQLException e) {
      throw new JspException(
          "Errore nel conteggio degli allegati della comunicazione", e);
    }

    pageContext.setAttribute("esistonoAllegatiComunicazione", esistonoAllegatiComunicazione, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("esistonoAllegatiDaFirmare", esistonoAllegatiDaFirmare, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("esistonoAllegatiFormatoNonValido", esistonoAllegatiFormatoNonValido, PageContext.REQUEST_SCOPE);
    return null;

  }

}
