/*
 * Created on 23 dec 2013
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
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare le sezioni degli atti di gara
 * (momentaneamente per atti di aggiudicazione)
 *
 * @author Cristian.Febas
 */
public class GestioneAttiGaraFunction extends AbstractFunzioneTag {

  public GestioneAttiGaraFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String tipoRelazione = null;
    String codice = null;

    String[] chiave = ((String) params[0]).split(";");
    tipoRelazione = chiave[0];
    tipoRelazione = UtilityStringhe.convertiNullInStringaVuota(tipoRelazione);
    if(!"".equals(tipoRelazione))
    {
        codice = chiave[1];
    }else{
      return null;
    }


    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    String selectAttiGara = null;

    try {
      if ("TORN".equals(tipoRelazione)){
        selectAttiGara = "select  ID, CODGAR, NGARA, NUMATTO, TIPOATTO, DATAATTO, NUMPROTATTO, DATAPROTATTO "
          + "from GAREATTI "
          + "where GAREATTI.CODGAR = ? "
          + "order by GAREATTI.NUMATTO asc";
      }else{
        if("GARE".equals(tipoRelazione)){
          selectAttiGara = "select  ID, CODGAR, NGARA, NUMATTO, TIPOATTO, DATAATTO, NUMPROTATTO, DATAPROTATTO "
            + "from GAREATTI "
            + "where GAREATTI.NGARA = ? "
            + "order by GAREATTI.NUMATTO asc";
        }

      }
      List listaAttiGara = sqlManager.getListVector(selectAttiGara , new Object[] { codice });
      if (listaAttiGara != null && listaAttiGara.size() > 0)
        pageContext.setAttribute("attiGara", listaAttiGara,
            PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre gli atti della gara "+ codice, e);
    }

    return null;
  }

}
