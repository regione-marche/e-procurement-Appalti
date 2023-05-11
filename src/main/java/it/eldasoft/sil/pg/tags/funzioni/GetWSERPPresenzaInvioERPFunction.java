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
public class GetWSERPPresenzaInvioERPFunction extends AbstractFunzioneTag {



  public GetWSERPPresenzaInvioERPFunction() {
    super(5, new Class[] { PageContext.class,String.class,String.class,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    Boolean isPresenzaInvioERP = false;


    String codgar = (String)params[1];
    String ngara = (String)params[2];
    String tipoWSERP = (String)params[3];
    String provenienza = (String)params[4];

    try {

      if(tipoWSERP.equals("ATAC")) {

        SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

        String selectSEGUEN = "SELECT SEGUEN FROM GARE WHERE NGARA = ?";
        codgar = codgar.replace("$", "");
        List<?> resSeguen = sqlManager.getListVector(selectSEGUEN, new Object[] { codgar });
        if (resSeguen != null && resSeguen.size() > 0) {

          String codGaraPadre = (String) SqlManager.getValueFromVectorParam(resSeguen.get(0), 0).getValue();
          codGaraPadre = UtilityStringhe.convertiNullInStringaVuota(codGaraPadre);

          String selectRDAPADRE = "select count(*) from gcap where ngara = ? and codrda is not null";
          Long resRDAPADRE = (Long) sqlManager.getObject(selectRDAPADRE, new Object[] { codGaraPadre });

          if (resRDAPADRE != null && resRDAPADRE > Long.valueOf(0)) {
            isPresenzaInvioERP = true;
          }

        }
      }
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura dei dati delle rda della gara", e);       
    }
    return isPresenzaInvioERP.toString();
  }
}
