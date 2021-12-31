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

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

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
    String faxEsistonoAllegatiFormatoValido = "TRUE";
    String faxAllegatoObbligatorio = "FALSE";
    String faxNumeroMaxAllegatiValido = "TRUE";
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
        String richiestaFirma = ConfigManager.getValore(CostantiAppalti.PROP_RICHIESTA_FIRMA);
        //Si verifica se vi sono degli allegati in attesa di firma
        if("1".equals(richiestaFirma)){
          Long numAllegatiDaFirmare = (Long)sqlManager.getObject("select count(iddocdig) from w_docdig where "
            + " digkey1 = ? and digkey2 = ? and digent = 'W_INVCOM' and digfirma = ?", new Object[] { idprg, idcom.toString(),"1" });
          if(numAllegatiDaFirmare!=null && numAllegatiDaFirmare.longValue()>0)
            esistonoAllegatiDaFirmare = "TRUE";
        }

        String formatoAllegati = ConfigManager.getValore(CostantiAppalti.FORMATO_ALLEGATI);
        if(formatoAllegati!=null && !"".equals(formatoAllegati)){
          List listaFileAllegati=sqlManager.getListVector("select dignomdoc from w_docdig where digkey1 = ? and digkey2 = ? and digent = 'W_INVCOM'",
              new Object[]{idprg, idcom.toString()});
          if(!pgManagerEst1.controlloAllegatiFormatoValido(listaFileAllegati,0,formatoAllegati))
            esistonoAllegatiFormatoNonValido = "TRUE";
        }
      }
      //Verifica se ci sono destinatari con tipo indirizzo Fax
      Long conteggioFax = (Long) sqlManager.getObject(
          "select count(*) from w_invcomdes where "
              + " idprg = ? and idcom = ? and comtipma = ?",
          new Object[] { idprg, idcom, new Long(3) });
      //Controlli nel caso ci siano dei destinatari di tipo Fax
      if (conteggioFax != null && conteggioFax.longValue() > 0) {

        //Verifica se gli allegati rientrano tra i formati validi indicati da properties
        if (esistonoAllegatiComunicazione == "TRUE") {
          String faxFormatoAllegati = ConfigManager.getValore(CostantiGenerali.PROP_FAX_FORMATO_ALLEGATI);
          //Se non indicato il formato valido da properties, si assume vada bene qualsiasi formato
          if (faxFormatoAllegati != null && !"".equals(faxFormatoAllegati)) {
            for (int i = 0; i < docAllegati.size(); i++) {
              String nomeFileAllegato = ((JdbcParametro) ((Vector) docAllegati.get(i)).get(2)).getStringValue();
              String estensioneFileAllegato = nomeFileAllegato.substring(nomeFileAllegato.indexOf(".") + 1).toUpperCase();
              //Se almeno un allegato non è nel formato valido, dà esito negativo
              if (faxFormatoAllegati.toUpperCase().indexOf(";" + estensioneFileAllegato + ";") < 0)
               faxEsistonoAllegatiFormatoValido="FALSE";
            }
          }

          //Verifica se ci sono più documenti allegati di quelli previsti
          String numeroMaxAllegati = ConfigManager.getValore(CostantiGenerali.PROP_FAX_NUMERO_MAX_ALLEGATI);
          Long numeroAllegatiComunicazione = new Long (docAllegati.size());
          if (numeroMaxAllegati != null && !"".equals(numeroMaxAllegati))
            if (numeroAllegatiComunicazione.compareTo(new Long(numeroMaxAllegati)) > 0)
              faxNumeroMaxAllegatiValido = "FALSE";
        }
        //Verifica se è obbligatorio allegare almeno un documento
        if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_FAX_ALLEGATO_OBBLIGATORIO)))
          faxAllegatoObbligatorio = "TRUE";

      }

    } catch (SQLException e) {
      throw new JspException(
          "Errore nel conteggio degli allegati della comunicazione", e);
    }

    pageContext.setAttribute("esistonoAllegatiComunicazione", esistonoAllegatiComunicazione, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("faxAllegatoObbligatorio", faxAllegatoObbligatorio, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("faxNumeroMaxAllegatiValido", faxNumeroMaxAllegatiValido, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("faxEsistonoAllegatiFormatoValido", faxEsistonoAllegatiFormatoValido, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("esistonoAllegatiDaFirmare", esistonoAllegatiDaFirmare, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("esistonoAllegatiFormatoNonValido", esistonoAllegatiFormatoNonValido, PageContext.REQUEST_SCOPE);
    return null;

  }

}
