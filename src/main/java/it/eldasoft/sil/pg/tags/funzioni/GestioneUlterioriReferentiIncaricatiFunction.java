/*
 * Created on 05/mag/10
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

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare le sezioni degli ulteriori referenti e incaricati di una gara a
 * lotto unico o di un lotto di gara in fase di modifica
 * 
 * @author Marcello.Caminiti
 */
public class GestioneUlterioriReferentiIncaricatiFunction extends AbstractFunzioneTag {

  public GestioneUlterioriReferentiIncaricatiFunction() {
    super(1, new Class[] { String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String nGara = (String) params[0];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      List listaReferentiIncaricati = sqlManager.getListVector(
          "select CODGAR, NUMTEC, CODTEC, NOMTEC, INCTEC "
              + "from GARTECNI "
              + "where GARTECNI.CODGAR = ? "
              + "order by GARTECNI.NUMTEC asc", new Object[] { nGara });

      if (listaReferentiIncaricati != null && listaReferentiIncaricati.size() > 0)
        pageContext.setAttribute("ultReferentiIncaricati", listaReferentiIncaricati,
            PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre gli ulteriori referenti e incaricati "
          + "della gara "
          + nGara, e);
    }

    return null;
  }

}
