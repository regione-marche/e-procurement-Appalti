/*
 * Created on 11/07/14
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
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetAutorizzatoModificaFunction extends
    AbstractFunzioneTag {

  /**
   * Funzione che controlla i diritti in modifica di una gara, considerando il campo "PROPRI" o "AUTORI"
   * parametri:
   * PageContext
   * nomeCampoWhere
   * valoreCampo
   * tipoControllo, "1" per "PROPRI", "2" per "AUTORI"
   *
   * @author Marcello Caminiti
   */
  public GetAutorizzatoModificaFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String nomeCampoWhere = (String) params[1];
    String valoreCampo = (String) params[2];
    String tipoControllo = (String) params[3];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String autorizzato = "false";
    String nomeCampo="autori";
    if("1".equals(tipoControllo))
      nomeCampo="propri";

    String seletc="select " + nomeCampo + " from g_permessi where " + nomeCampoWhere + " = ? and syscon = ?";
    try {
        Object param[]= new Object[2];

        if(nomeCampoWhere.contains("IDMERIC"))
          param[0]= new Long(valoreCampo);
        else
          param[0]= valoreCampo;

        ProfiloUtente profilo = (ProfiloUtente) pageContext.getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        param[1] = new Long(profilo.getId());

        Object obj = sqlManager.getObject(seletc, param);

        if("1".equals(tipoControllo)){
          String valore = (String)obj;
          String abilitazione = new String (profilo.getAbilitazioneStd());
          if(nomeCampoWhere.contains("IDMERIC")||nomeCampoWhere.contains("CODGAR"))
            abilitazione = new String (profilo.getAbilitazioneGare());

          if ("1".equals(valore) || (new String("A")).equals(abilitazione)){
            autorizzato = "true";
          }
        }else{
          Long valore = (Long)obj;
          if ((new Long(1)).equals(valore)){
            autorizzato = "true";
          }
        }

    } catch (SQLException s) {
      throw new JspException(
          "Errore durante la lettura dello stato della comunicazione", s);
    }

    return autorizzato;

  }

}
