/*
 * Created on 28/06/2023
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
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che recupera altri dati del DURC
 *
 * @author Alex.Mancini
 */
public class GetDatiRichiestaDURCFunction extends AbstractFunzioneTag {

  public GetDatiRichiestaDURCFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String codgar = null;
    String ngara = null;
    String codimp = null;
    String norddoci = null;
    String proveni = null;

    String key = (String) params[1];
    if(key != null && !"".equals(key)) {
      String[] dati = key.split(";");
      for (int i = 0; i < dati.length; i++) {
        String[] datiInterni = null;
        if(dati[i].contains("=T:")) {
          datiInterni = dati[i].split("=T:");
        }
        if(dati[i].contains("=N:")) {
          datiInterni = dati[i].split("=N:");
        }
        if(datiInterni != null) {
          if("V_GARE_DOCDITTA.CODGAR".equalsIgnoreCase(datiInterni[0])) {
            codgar = datiInterni[1];
          } else if("V_GARE_DOCDITTA.NGARA".equalsIgnoreCase(datiInterni[0])) {
            ngara = datiInterni[1];
          } else if("V_GARE_DOCDITTA.CODIMP".equalsIgnoreCase(datiInterni[0])) {
            codimp = datiInterni[1];
          } else if("V_GARE_DOCDITTA.NORDDOCI".equalsIgnoreCase(datiInterni[0])) {
            norddoci = datiInterni[1];
          } else if("V_GARE_DOCDITTA.PROVENI".equalsIgnoreCase(datiInterni[0])) {
            proveni = datiInterni[1];
          }
        }
      }
    }

    codgar = UtilityStringhe.convertiNullInStringaVuota(codgar);
    ngara = UtilityStringhe.convertiNullInStringaVuota(ngara);
    codimp = UtilityStringhe.convertiNullInStringaVuota(codimp);
    norddoci = UtilityStringhe.convertiNullInStringaVuota(norddoci);
    proveni = UtilityStringhe.convertiNullInStringaVuota(proveni);

    String idImpDurc = null;
    String dataRichiesta = null;
    String dataScadenza = null;
    String dataEsito = null;
    String numeroProtocollo = null;
    String annullato = null;
    String dataAnnullamento = null;

    try { 
      if(!"".equals(codgar) && !"".equals(ngara) && !"".equals(codimp) && !"".equals(norddoci) && !"".equals(proveni)){
        Long codNorddoci = new Long(norddoci);
        Long codProveni = new Long(proveni);

        String selectAltriDati = "select IMPDURC.ID ,IMPDURC.DATRIC ,IMPDURC.DATSCA ,IMPDURC.DATESITO ,IMPDURC.NPROTO ,IMPDURC.ANNULLATO ,IMPDURC.DATANNULLAMENTO "
            + "from V_GARE_DOCDITTA,IMPDURC,W_DOCDIG,IMPRDOCG "
            + "where V_GARE_DOCDITTA.CODGAR = ? "
            + "and V_GARE_DOCDITTA.NGARA = ? "
            + "and V_GARE_DOCDITTA.CODIMP = ? "
            + "and V_GARE_DOCDITTA.NORDDOCI = ? "
            + "and V_GARE_DOCDITTA.PROVENI = ? "
            + "and (CAST(IMPDURC.ID AS VARCHAR(10)) = W_DOCDIG.DIGKEY4 and V_GARE_DOCDITTA.IDPRG = W_DOCDIG.IDPRG and V_GARE_DOCDITTA.IDDOCDG = W_DOCDIG.IDDOCDIG "
            + "and IMPRDOCG.CODIMP = IMPDURC.CODIMP and IMPRDOCG.CODGAR = V_GARE_DOCDITTA.CODGAR and IMPRDOCG.NGARA = V_GARE_DOCDITTA.NGARA "
            + "and IMPRDOCG.CODIMP = V_GARE_DOCDITTA.CODIMP and IMPRDOCG.NORDDOCI = V_GARE_DOCDITTA.NORDDOCI "
            + "and IMPRDOCG.PROVENI = V_GARE_DOCDITTA.PROVENI) ";
        Vector<?> dati = sqlManager.getVector(selectAltriDati,new Object[] { codgar, ngara, codimp, codNorddoci, codProveni });

        if(dati!=null && !dati.isEmpty()){
          idImpDurc = UtilityStringhe.convertiNullInStringaVuota(String.valueOf(SqlManager.getValueFromVectorParam(dati, 0).longValue()));

          Timestamp dataRic = SqlManager.getValueFromVectorParam(dati, 1).dataValue();
          if(dataRic != null)
            dataRichiesta = UtilityDate.convertiData(new Date(dataRic.getTime()), UtilityDate.FORMATO_GG_MM_AAAA);

          Timestamp dataSca = SqlManager.getValueFromVectorParam(dati, 2).dataValue();
          if(dataSca != null)
            dataScadenza = UtilityDate.convertiData(new Date(dataSca.getTime()), UtilityDate.FORMATO_GG_MM_AAAA);

          Timestamp dataEsi = SqlManager.getValueFromVectorParam(dati, 3).dataValue();
          if(dataEsi != null)
            dataEsito = UtilityDate.convertiData(new Date(dataEsi.getTime()), UtilityDate.FORMATO_GG_MM_AAAA);

          numeroProtocollo = SqlManager.getValueFromVectorParam(dati, 4).getStringValue();
          annullato = SqlManager.getValueFromVectorParam(dati, 5).getStringValue();

          Timestamp dataAnn = SqlManager.getValueFromVectorParam(dati, 6).dataValue();
          if(dataAnn!=null)
            dataAnnullamento = UtilityDate.convertiData(new Date(dataAnn.getTime()), UtilityDate.FORMATO_GG_MM_AAAA);
        }

        pageContext.setAttribute("idImpDurc",idImpDurc,PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("dataRichiesta",dataRichiesta,PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("dataScadenza",dataScadenza,PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("dataEsito",dataEsito,PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("numeroProtocollo",numeroProtocollo,PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("annullato",annullato,PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("dataAnnullamento",dataAnnullamento,PageContext.REQUEST_SCOPE);
      }

    } catch (SQLException e) {
      throw new JspException("Errore nel recupero del dettaglio DURC", e);
    } catch (Exception ex) {
      throw new JspException("Errore nel recupero del dettaglio DURC", ex);
    }

    return null;
  }

}
