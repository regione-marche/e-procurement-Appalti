/*
 * Created on 8-ott-2009
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che estrae l'indirizzo e-mail e pec di un impresa.
 * Nel caso di un ATI, considera la mandataria.
 * Viene passato come parametro una stringa avente la seguente struttura
 * codice destinatario;idprg;idcom;entita;SI(NO).
 * L'ultimo valore indica nel caso di entita IMPR se considerare RT
 *
 * @author Cristian Febas
 */
public class GetMailTipoDittaFunction extends AbstractFunzioneTag {

	public GetMailTipoDittaFunction() {
		super(1, new Class[] { String.class});
	}

	public GetMailTipoDittaFunction(int numeroParam, Class[] arrayClassi) {
      super(numeroParam, arrayClassi);
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
  throws JspException {

  String[] email = new String[3];
  String[] emailDisabled = new String[3];

    String[] parametri = ((String) params[0]).split(";");
  String codiceDitta = parametri[0];
  String idprg = parametri[1];
  Long idcom = new Long(parametri[2]);
  String entita=parametri[3];
  boolean considerareRT=false;
  if("IMPR".equals(entita) && "SI".equals(parametri[4]))
    considerareRT=true;

  boolean mandatariaPresente=true;
  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
    pageContext, SqlManager.class);

  if (codiceDitta != null && !"".equals(codiceDitta)) {
    try {
      String select="select emai2ip,emaiip,faximp,tipimp from impr where codimp=?";
      if("TECNI".equals(entita))
        select="select ema2tec,ematec from tecni where codtec=?";
      Vector datiImpr = sqlManager.getVector(select, new Object[] { codiceDitta });
      if(datiImpr!=null){
        String tipoImpr=null;
        if("IMPR".equals(entita))
          tipoImpr = ((JdbcParametro) datiImpr.get(3)).getStringValue();

        //Se l'impresa è di tipo ATI, considera l'indirizzo mail della mandataria
        if(("3".equals(tipoImpr) || "10".equals(tipoImpr)) && considerareRT){


            String selectComponenti= "select CODDIC, NOMDIC, emai2ip, emaiip, faximp" +
            		" from RAGIMP,IMPR where CODIME9 = ? and CODDIC=CODIMP and IMPMAN='1'";
            List listaComponenti = sqlManager.getListVector(selectComponenti,new Object[]{ codiceDitta });
            if (listaComponenti != null && listaComponenti.size() == 1){
              for (int k = 0; k< listaComponenti.size(); k++) {
                String codComponente = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 0).getStringValue();
                String nomeComponente = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 1).getStringValue();
                String emai2ip = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 2).getStringValue();
                emai2ip = UtilityStringhe.convertiNullInStringaVuota(emai2ip);
                if(!"".equals(emai2ip)){
                  email[0] =emai2ip;
                }
                String emaiip = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 3).getStringValue();
                emaiip = UtilityStringhe.convertiNullInStringaVuota(emaiip);
                if(!"".equals(emaiip)){
                  email[1] = emaiip;
                }
                String faximp = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 4).getStringValue();
                faximp = UtilityStringhe.convertiNullInStringaVuota(faximp);
                if(!"".equals(faximp)){
                  email[2] = faximp;
                }
              }
            }else{
              mandatariaPresente=false;
            }

        } else {
          email[0] = ((JdbcParametro) datiImpr.get(0)).getStringValue();
          email[1] = ((JdbcParametro) datiImpr.get(1)).getStringValue();
          if("IMPR".equals(entita))
            email[2] = ((JdbcParametro) datiImpr.get(2)).getStringValue();
        }
      }
      //Verifica per quali tipo indirizzo la ditta è già stata inserita tra i destinatari
      emailDisabled[0] = "false";
      emailDisabled[1] = "false";
      emailDisabled[2] = "false";
      List destinatariDitta = sqlManager.getListVector(
          "select comtipma from w_invcomdes where descodent=? and idprg = ? and idcom = ? and " +
            "descodsog = ?", new Object[] {entita,idprg, idcom, codiceDitta});
      if (destinatariDitta != null && destinatariDitta.size() > 0) {
        for (int i = 0; i < destinatariDitta.size(); i++) {
          Long tipoIndirizzo = (Long) ((JdbcParametro) ((Vector) destinatariDitta.get(i)).get(0)).getValue();
          emailDisabled[tipoIndirizzo.intValue() - 1] = "true";
        }
      }

    } catch (SQLException e) {
      throw new JspException(
          "Errore durante la lettura dei dati nell'anagrafica impresa ", e);
    }
  }

  pageContext.setAttribute("emailPec", email[0], PageContext.REQUEST_SCOPE);
  pageContext.setAttribute("email", email[1], PageContext.REQUEST_SCOPE);
  pageContext.setAttribute("fax", email[2], PageContext.REQUEST_SCOPE);

  pageContext.setAttribute("emailPecDisabled", emailDisabled[0], PageContext.REQUEST_SCOPE);
  pageContext.setAttribute("emailDisabled", emailDisabled[1], PageContext.REQUEST_SCOPE);
  pageContext.setAttribute("faxDisabled", emailDisabled[2], PageContext.REQUEST_SCOPE);
  pageContext.setAttribute("mandatariaPresente", new Boolean(mandatariaPresente), PageContext.REQUEST_SCOPE);

  return null;
  }

}