/*
 * Created on 12-10-2012
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
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che controlla se esiste almeno un lotto GARE.DITTA non valorizzato
 *
 * (S.Santi) Aggiunto controllo su esistenza lotti aggiudicati provvisoriamente
 * 
 * @author Marcello Caminiti
 */
public class ControlloDittaLottiFunction extends AbstractFunzioneTag {

  public ControlloDittaLottiFunction() {
    super(3 , new Class[] { PageContext.class, String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String esisteDittaNonValorizzato= "true";
    String codgar = (String) params[1];
    String tipologiaGara = (String)params[2];
    String esisteLottoDittaEsinegNulli = "no";
    String tuttiLottiDittaOEsinegValorizzati;
    String esisteLottoDittaProvValorizzato = "no";
    
    if(codgar!=null && !"".equals(codgar)){
      String select="select ditta,esineg,dittap from gare where codgar1 = ?";
      if(tipologiaGara!=null && "3".equals(tipologiaGara))
        select += " and ngara <> codgar1";

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
          pageContext, SqlManager.class);

      try {

        List listaDitta = sqlManager.getListVector(select, new Object[]{codgar});
        if(listaDitta!= null && listaDitta.size()>0){
          int numCampiDittaValorizzati = 0;
          int numCampiDittaOEsinegValorizzati = 0;
          for(int i=0;i<listaDitta.size(); i++){
            String ditta = SqlManager.getValueFromVectorParam(listaDitta.get(i), 0).getStringValue();
            Long esineg = SqlManager.getValueFromVectorParam(listaDitta.get(i), 1).longValue();
            String dittaProv = SqlManager.getValueFromVectorParam(listaDitta.get(i), 2).getStringValue();
            if(ditta!=null && !"".equals(ditta)){
              numCampiDittaValorizzati++;
            }
            if((ditta==null || "".equals(ditta)) && esineg== null)
              esisteLottoDittaEsinegNulli = "Si";

            if((ditta!=null && !"".equals(ditta)) || esineg != null)
              numCampiDittaOEsinegValorizzati++;

            if(dittaProv!=null && !"".equals(dittaProv))
              esisteLottoDittaProvValorizzato = "Si";

          }
          if(numCampiDittaValorizzati == listaDitta.size()){
            pageContext.setAttribute("dittaLottiTuttiValorizzati","Si",PageContext.REQUEST_SCOPE);
            esisteDittaNonValorizzato = "false";
          }else if(numCampiDittaValorizzati==0)
            pageContext.setAttribute("dittaLottiTuttiNulli","Si",PageContext.REQUEST_SCOPE);

          pageContext.setAttribute("esisteLottoDittaEsinegNulli",esisteLottoDittaEsinegNulli,PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("esisteLottoDittaProvValorizzato",esisteLottoDittaProvValorizzato,PageContext.REQUEST_SCOPE);

          if(numCampiDittaOEsinegValorizzati == listaDitta.size())
            tuttiLottiDittaOEsinegValorizzati = "Si";
          else
            tuttiLottiDittaOEsinegValorizzati = "No";
          pageContext.setAttribute("tuttiLottiDittaOEsinegValorizzati",tuttiLottiDittaOEsinegValorizzati,PageContext.REQUEST_SCOPE);

        }else{
          pageContext.setAttribute("dittaLottiTuttiNulli","Si",PageContext.REQUEST_SCOPE);
        }

      } catch (SQLException e) {
        throw new JspException("Errore nella lettura del campo GARE.DITTA dei lotti.",e);
      } catch (GestoreException e) {
        throw new JspException("Errore nella lettura del campo GARE.ESINEG dei lotti.",e);
      }
    }else{
      pageContext.setAttribute("dittaLottiTuttiNulli","Si",PageContext.REQUEST_SCOPE);
    }

    return esisteDittaNonValorizzato;
  }

}
