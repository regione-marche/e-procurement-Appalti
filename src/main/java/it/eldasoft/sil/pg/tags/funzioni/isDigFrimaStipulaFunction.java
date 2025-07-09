/*
 * Created on 24/01/2023
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
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che controlla se il documento ha la richiesta di essere firmato
 *
 * @author Alex.Mancini
 */
public class isDigFrimaStipulaFunction extends AbstractFunzioneTag {

  public isDigFrimaStipulaFunction() {
    super(5, new Class[] { PageContext.class, String.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String docStipulaIdPrg = (String) params[1];
    String docStipulaIdDocDig = (String) params[2];
    String digent = (String) params[3];
    String docStipulaId = (String) params[4];

    docStipulaIdPrg = UtilityStringhe.convertiNullInStringaVuota(docStipulaIdPrg);
    docStipulaIdDocDig = UtilityStringhe.convertiNullInStringaVuota(docStipulaIdDocDig);
    digent = UtilityStringhe.convertiNullInStringaVuota(digent);
    docStipulaId = UtilityStringhe.convertiNullInStringaVuota(docStipulaId);

    String digFirma = null;
    Long formato = null;
    Vector<?> dati = null;
    String res = "0";
    try { 
      if(!"".equals(docStipulaIdPrg) && !"".equals(docStipulaIdDocDig) && !"".equals(digent) && !"".equals(docStipulaId)){
        Long idDocDig = new Long(docStipulaIdDocDig);
        Long id = new Long(docStipulaId);
        
        String selectDigFirma = "select digfirma "
                              + "from W_DOCDIG wd "
                              + "where wd.iddocdig = ? "
                              + "and wd.idprg = ? "
                              + "and wd.digent = ? "
                              + "and wd.digkey1 = ?";
        dati = sqlManager.getVector(selectDigFirma,new Object[] { idDocDig, docStipulaIdPrg, digent, id.toString() });
        
        if(dati!=null && !dati.isEmpty()){
          digFirma = UtilityStringhe.convertiNullInStringaVuota((String) SqlManager.getValueFromVectorParam(dati, 0).getValue());
          if("1".equals(digFirma)) {
            res = "1";
          }
        }
        
      } else if(!"".equals(docStipulaId)){
        Long id = new Long(docStipulaId);
        String selectDigFirma = "select formato "
            + "from G1DOCSTIPULA "
            + "where id = ? "
            + "and (visibilita is null or (visibilita is not null and visibilita <> 3)) ";
        dati = sqlManager.getVector(selectDigFirma,new Object[] { id });
        
        if(dati!=null && !dati.isEmpty()){
          formato = (Long) SqlManager.getValueFromVectorParam(dati, 0).getValue();
          if(formato != null && formato.compareTo(Long.valueOf(1))==0) {
            res = "1";
          }
        }
      }

    } catch (SQLException e) {
      throw new JspException("Errore nel controllo della richiesta di firma", e);
    } catch (Exception ex) {
      throw new JspException("Errore nel controllo della richiesta di firma", ex);
    }

    return res;
  }

}
