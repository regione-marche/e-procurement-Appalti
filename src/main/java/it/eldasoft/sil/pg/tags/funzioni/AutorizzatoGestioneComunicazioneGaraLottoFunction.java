package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class AutorizzatoGestioneComunicazioneGaraLottoFunction extends
    AbstractFunzioneTag {

  public AutorizzatoGestioneComunicazioneGaraLottoFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String coment = (String) params[1];
    String comkey1 = (String) params[2];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        pageContext, PgManagerEst1.class);

    String autorizzato = "false";

    try {

      String codgar = "";
      if ("GARE".equals(coment)) {
        // si tratta di una comunicazione da inviare che parte da gara plico unico
        codgar = (String) sqlManager.getObject("select codgar1 from gare where ngara = ?", new Object[] {comkey1 });
      } else if ("G1STIPULA".equals(coment)) {
          Long idStipula = (Long) sqlManager.getObject("select id from g1stipula where codstipula = ?", new Object[] {comkey1 });
          codgar=idStipula.toString();
      } else {
   		  codgar = comkey1;	  
      }

      ProfiloUtente profilo = (ProfiloUtente) pageContext.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      autorizzato = pgManagerEst1.controlloPermessiModificaUtente(coment, codgar, profilo, false )[0];

    } catch (SQLException s) {
      throw new JspException(
          "Errore durante la lettura dello stato della comunicazione", s);
    }

    return autorizzato;

  }

}
