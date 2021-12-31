/*
 * Created on 6-01-2013
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
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetAbilitazioneInvioComunicazioneFunction extends AbstractFunzioneTag {

  public GetAbilitazioneInvioComunicazioneFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        pageContext, PgManager.class);
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String invioFax = ConfigManager.getValore(CostantiGenerali.PROP_FAX_ABILITATO);

    String result = "Si";

    //E' nella forma:DITG.CODGAR5=T:$E00036;DITG.DITTAO=T:IMP131;DITG.NGARA5=T:E00036
    String chiave = (String) GeneralTagsFunction.cast("string", params[1]);

    String chiaveVet[]= chiave.split(";");
    String codiceDitta=chiaveVet[1].substring(chiaveVet[1].indexOf(":")+1);

    String condizioneInvioMailDocumentale = (String) GeneralTagsFunction.cast("string", params[2]);

    try {

      String mailFax[] = pgManager.getMailFax(codiceDitta);
      String email=mailFax[0];
      String Pec = mailFax[1];
      String fax = mailFax[2];
      String isRTI = mailFax[3];

      if("no".equals(isRTI)){
        if("true".equals(condizioneInvioMailDocumentale) &&  (Pec==null || "".equals(Pec)))
          result="NoPec";
        else if(((email==null || "".equals(email)) && (Pec==null || "".equals(Pec))) && ((invioFax==null || "".equals(invioFax) || "0".equals(invioFax)) || ("1".equals(invioFax) && (fax==null || "".equals(fax))))){
          result="NoMail";
          if("1".equals(invioFax)){
            result="NoMailFax";
          }
        }
      }else{
        //verifico nel caso di RTI che la mandataria esista ed abbia gli indirizzi
        if("si".equals(isRTI)){
          String isIndSpec = "si";
          String selectComponenti= "select emai2ip, emaiip, faximp from RAGIMP,IMPR where CODIME9 = ? and CODDIC=CODIMP and impman='1'";
          List listaComponenti = sqlManager.getListVector(selectComponenti,new Object[]{ codiceDitta });
          if (listaComponenti != null && listaComponenti.size() == 1){
            for (int k = 0; k< listaComponenti.size(); k++) {
              String emai2ip = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 0).getStringValue();
              emai2ip = UtilityStringhe.convertiNullInStringaVuota(emai2ip);
              String emaiip = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 1).getStringValue();
              emaiip = UtilityStringhe.convertiNullInStringaVuota(emaiip);
              String faximp = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 2).getStringValue();
              faximp = UtilityStringhe.convertiNullInStringaVuota(faximp);
              if("true".equals(condizioneInvioMailDocumentale) &&  "".equals(emai2ip)){
                isIndSpec ="NoPecRTI";
                break;
              }else if(("".equals(emai2ip) && "".equals(emaiip)) && ((invioFax==null || "".equals(invioFax) || "0".equals(invioFax)) || ("1".equals(invioFax) && "".equals(faximp)))){
                isIndSpec ="no";
              }
            }
            if("NoPecRTI".equals(isIndSpec))
              result ="NoPecRTI";
            if("no".equals(isIndSpec)){
              result="NoMailRTI";
              if("1".equals(invioFax)){
                result="NoMailFaxRTI";
              }
            }
          }else{
            result="NoMandatariaRTI";
          }


        }
      }
    } catch (SQLException e) {
        throw new JspException("Errore nella lettura dell'indirizzo mail, pec e fax dell'impresa:" + codiceDitta, e);
    }

    return result;
  }
}
