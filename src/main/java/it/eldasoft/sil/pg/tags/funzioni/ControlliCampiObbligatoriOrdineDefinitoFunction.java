package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class ControlliCampiObbligatoriOrdineDefinitoFunction extends AbstractFunzioneTag {

  public ControlliCampiObbligatoriOrdineDefinitoFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object params[]) throws JspException {
    String msg="<b>Non è possibile procedere e impostare l'ordine a definito.</b><br>";

    try {

      String ngara = (String) params[1];
      boolean controlloSuperato=true;

      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
      Vector datiGare = sqlManager.getVector("select cenint, not_gar, nrepat, daatto, ditta, dattoa, genere from torn,gare where ngara=? " +
      		"and codgar1=codgar", new Object[]{ngara});
      if(datiGare!=null && datiGare.size()>0){
        String cenint = (String) SqlManager.getValueFromVectorParam(datiGare, 0).getValue();
        String not_gar = (String) SqlManager.getValueFromVectorParam(datiGare, 1).getValue();
        String nrepat = (String) SqlManager.getValueFromVectorParam(datiGare, 2).getValue();
        Date daatto = (Date) SqlManager.getValueFromVectorParam(datiGare, 3).getValue();
        String ditta = (String) SqlManager.getValueFromVectorParam(datiGare, 4).getValue();
        Date dattoa = (Date) SqlManager.getValueFromVectorParam(datiGare, 5).getValue();
        Long genere = (Long)SqlManager.getValueFromVectorParam(datiGare, 6).getValue();
        if(cenint==null || "".equals(cenint)){
          controlloSuperato = false;
          msg+="<br>Non è stata inserita la stazione appaltante.";
        }
        if(not_gar==null || "".equals(not_gar)){
          controlloSuperato = false;
          msg+="<br>Non è stato inserito l'oggetto dell'ordine.";
        }
        if(nrepat==null || "".equals(nrepat)){
          controlloSuperato = false;
          msg+="<br>Non è stato inserito il numero identificativo dell'ordine.";
        }
        if(daatto==null ){
          controlloSuperato = false;
          msg+="<br>Non è stata inserita la data dell'ordine.";
        }
        if(ditta==null || "".equals(ditta)){
          controlloSuperato = false;
          msg+="<br>Non è stata inserita la ditta aggiudicataria.";
        }
        if(!(new Long(4).equals(genere)) && dattoa==null ){
          controlloSuperato = false;
          msg+="<br>Non è stata inserita la data dell'atto di aggiudicazione.";
        }
      }
      if(!controlloSuperato ){
        pageContext.setAttribute("erroriControlloGare", new Boolean(true));
      }else
        msg="";

    } catch (SQLException e) {
      throw new JspException("Errore nella funzione di controllo dei dati obbligatori per impostare l'ordine a definito", e);
    }

    return msg;

  }

}