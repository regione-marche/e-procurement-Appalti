package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

public class AutorizzatoModificaComunicazioneFunction extends
    AbstractFunzioneTag {

  public AutorizzatoModificaComunicazioneFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String idprg = (String) params[1];
    Long idcom = new Long((String) params[2]);
    String controlloTabellatoTelematiche = (String) params[3];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    
    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        pageContext, PgManagerEst1.class);

    String autorizzato = "false";
    String autorizzatoTelematiche="true";

    try {
      List datiW_INVCOM = sqlManager.getListVector(
          "select coment, comkey1, comkey2 from w_invcom where idprg = ? and idcom = ?",
          new Object[] { idprg, idcom });

      if (datiW_INVCOM != null && datiW_INVCOM.size() > 0) {
        String coment = (String) SqlManager.getValueFromVectorParam(
            datiW_INVCOM.get(0), 0).getValue();
        String comkey1 = (String) SqlManager.getValueFromVectorParam(
            datiW_INVCOM.get(0), 1).getValue();
        String comkey2 = (String) SqlManager.getValueFromVectorParam(
            datiW_INVCOM.get(0), 2).getValue();
        // Codice della tornata: se l'entità è GARE devo ricavarla
        // prima di utilizzarla per interrogare la G_PERMESSI
        String codgar = "";
        if (coment == null) {
          // si tratta di una comunicazione ricevuta
          // si recupera il codice gara partendo dal campo codice inserito nel portale in comkey2, solitamente corrispondente a ngara
          // eccetto il caso di gara a lotti con offerta unica in cui corrisponde al codice gara vero e proprio
          //Nel caso di ODA si deve prendere come chiave l'id di meric
          Long idric = (Long) sqlManager.getObject("select idric from v_oda where ngara = ?", new Object[] {comkey2 });
          if(idric!=null){
            codgar=idric.toString();
            coment="GARECONT";
          }else{
            codgar = (String) sqlManager.getObject("select codgar1 from gare where ngara = ?", new Object[] {comkey2 });
            // verifico NSO_ORDINI in caso contrario si continua con le verifiche prestabilite 
            if (codgar == null) {
              codgar = (String) sqlManager.getObject("select g.codgar1 from gare g"
                                                    + " JOIN nso_ordini no on no.ngara = g.ngara"
                                                    + " where no.id = ?", new Object[] {comkey2 });
              if(StringUtils.isNotBlank(codgar)) {
                coment="NSO_ORDINI";
              }
            }
            if (codgar == null) {
              // gara a lotti con offerte distinte, e' il dato stesso
              codgar = comkey2;
            }
          }
        } else if ("GARE".equals(coment)) {
          // si tratta di una comunicazione da inviare che parte da gare (elenco, mercato, gara a lotto unico o lotto di gara)
          codgar = (String) sqlManager.getObject("select codgar1 from gare where ngara = ?", new Object[] {comkey1 });
        } else if ("GARECONT".equals(coment)) {
          // si tratta di una comunicazione da inviare che parte dai una ODA
          Long idric = (Long) sqlManager.getObject("select idric from v_oda where ngara = ?", new Object[] {comkey1 });
          codgar=idric.toString();
        } else {
          // si tratta di una comunicazione da inviare che parte dalla tornata (offerta unica)
          codgar = comkey1;
        }

        ProfiloUtente profilo = (ProfiloUtente) pageContext.getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);


        String autorizzazione[] = pgManagerEst1.controlloPermessiModificaUtente(coment, codgar, profilo, new Boolean(controlloTabellatoTelematiche).booleanValue());
        autorizzato = autorizzazione[0];
        autorizzatoTelematiche = autorizzazione[1];
        pageContext.setAttribute("autorizzatoTelematiche", autorizzatoTelematiche, PageContext.REQUEST_SCOPE);
      }

    } catch (SQLException s) {
      throw new JspException(
          "Errore durante la lettura dello stato della comunicazione", s);
    }

    return autorizzato;

  }

}
