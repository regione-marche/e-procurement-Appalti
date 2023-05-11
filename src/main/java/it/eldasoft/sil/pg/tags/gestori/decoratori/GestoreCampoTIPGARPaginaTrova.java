/*
 * Created on 10/10/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.decoratori;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato;
import it.eldasoft.gene.tags.decorators.campi.ValoreTabellato;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

/**
 * Gestore del campo tipo TIPGAR. Questo gestore elimina dai valori del tabellato
 * "A2044" associato al campo, i valori che non sono presenti nel tabellato "A1z03"
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoTIPGARPaginaTrova extends AbstractGestoreCampoTabellato {

  public GestoreCampoTIPGARPaginaTrova() {
    super(false, "N7");
  }


  @Override
  public SqlSelect getSql() {
    String profiloAttivo = (String) this.getPageContext().getSession().getAttribute("profiloAttivo");
    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getPageContext(), PgManager.class);

    String select = "select tab1tip,tab1desc from tab1 where tab1cod='A2044' order by tab1nord,tab1tip";

    try {
      String descTab = pgManager.getFiltroTipoGara(profiloAttivo);
      if (descTab!=null){
        select = "select tab1tip,tab1desc from tab1 where tab1cod='A2044' and tab1tip in (" + descTab + ") order by tab1nord,tab1tip";

      }
    } catch (GestoreException e) {

    }

    return new SqlSelect(select, null);
  }


}
