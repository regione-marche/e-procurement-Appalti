/*
 * Created on 08-10-2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.xml.rpc.ServiceException;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreResType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreType;

/**
 * Funzione che estrae i dati delle rda (o delle rda) collegate alla gara
 * @author Cristian Febas
 */
public class IsAffidamentoDerivatoFunction extends AbstractFunzioneTag {



  public IsAffidamentoDerivatoFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    Boolean isAffidamentoDerivato = false;

    String ngara = (String)params[1];

    try {

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

      String selectSEGUEN = "SELECT SEGUEN FROM GARE WHERE NGARA = ?";

      List<?> resSeguen = sqlManager.getListVector(selectSEGUEN, new Object[] { ngara });
      if (resSeguen != null && resSeguen.size() > 0) {

        String codGaraPadre = (String) SqlManager.getValueFromVectorParam(resSeguen.get(0), 0).getValue();
        codGaraPadre = UtilityStringhe.convertiNullInStringaVuota(codGaraPadre);

        String selectITERGA = "SELECT ITERGA FROM TORN, GARE WHERE TORN.CODGAR = GARE.CODGAR1 AND NGARA = ? AND ITERGA IS NOT NULL";
        List<?> resITERGA = sqlManager.getListVector(selectITERGA, new Object[] { codGaraPadre });

        if (resITERGA != null && resITERGA.size() > 0) {
          String iterga = SqlManager.getValueFromVectorParam(resITERGA.get(0), 0).getValue().toString();
          iterga = UtilityStringhe.convertiNullInStringaVuota(iterga);

          if(iterga.equals("8"))
            isAffidamentoDerivato = true;
        }

      }
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura dei dati delle rda della gara", e);       
    }
    return isAffidamentoDerivato.toString();
  }
}
