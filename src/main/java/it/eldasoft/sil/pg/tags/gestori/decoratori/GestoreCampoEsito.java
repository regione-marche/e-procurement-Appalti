/*
 * Created on 06/12/12
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
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

/**
 * Gestore del campo V_GARE_STATOESITO.ESITO
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoEsito extends AbstractGestoreCampo {

  @Override
  public String gestisciDaTrova(Vector params, DataColumn col, String conf,
      SqlManager manager) {

    return null;
  }

  @Override
  public String getClasseEdit() {

    return null;
  }

  @Override
  public String getClasseVisua() {

    return null;
  }

  @Override
  public String getHTML(boolean visualizzazione, boolean abilitato) {

    return null;
  }

  @Override
  public String getValore(String valore) {

    return null;
  }

  @Override
  public String getValorePerVisualizzazione(String valore) {

    return null;
  }

  @Override
  public String getValorePreUpdateDB(String valore) {

    return null;
  }

  @Override
  protected void initGestore() {

    SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getPageContext(), SqlManager.class);
    this.getCampo().setTipo("ET100");
    this.getCampo().getValori().clear();
    String  select = "select tab1desc from tab1 where tab1cod='A1088' order by tab1nord,tab1tip";

      this.getCampo().addValore("", "");
      this.getCampo().addValore("Aggiudicata", "Aggiudicata");
      this.getCampo().addValore("Emesso contratto/ordine", "Emesso contratto/ordine");
      try {
        List ret = sql.getListVector(select, null);
        for (int i = 0; i < ret.size(); i++) {
          Vector row = (Vector) ret.get(i);
          String cod = "";
          String descr = row.get(row.size() - 1).toString();
          cod=descr;
          if(descr!=null && descr.length()>80)descr=descr.substring(0,80)+"...";
          this.getCampo().addValore(cod, descr);
        }
      } catch (SQLException e) {

      }

  }

  @Override
  public String postHTML(boolean visualizzazione, boolean abilitato) {

    return null;
  }

  @Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {

    return null;
  }

}
