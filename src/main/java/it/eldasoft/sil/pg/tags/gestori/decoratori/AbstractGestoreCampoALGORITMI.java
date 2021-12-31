/*
 * Created on 03/dic/08
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
 * Questo gestore prevede la ridefinizione di initGestore rispetto alla classe padre, 
 * ed utilizza il primo campo della select come codice, mentre concatena il primo ed 
 * il secondo per la descrizione 
 * 
 * @author Stefano.Sabbadin
 */
abstract class AbstractGestoreCampoALGORITMI extends AbstractGestoreCampo {

  private boolean addCodADescr;
  private String  tipoCampo;
  private boolean addNull = true;

  
  public AbstractGestoreCampoALGORITMI(boolean addCodADescr, String tipoCampo) {
    this.addCodADescr = addCodADescr;
    this.tipoCampo = tipoCampo;
  }
  public class SqlSelect {

    private String sql;
    private Object param[];

    public SqlSelect(String sql, Object param[]) {
      this.sql = sql;
      this.param = param;
    }

    public SqlSelect(String sql) {
      this.sql = sql;
      this.param = new Object[] {};
    }

    /**
     * @return the param
     */
    public Object[] getParam() {
      return param;
    }

    /**
     * @return the sql
     */
    public String getSql() {
      return sql;
    }

  }

  public String getClasseEdit() {
    return null;
  }

  public String getClasseVisua() {
    return null;
  }

  public String getHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  public String getValore(String valore) {
    return null;
  }

  public String getValorePerVisualizzazione(String valore) {
    return null;
  }

  public String getValorePreUpdateDB(String valore) {
    return null;
  }
  



  protected void initGestore() {
    SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getPageContext(), SqlManager.class);
    this.getCampo().setTipo("E" + this.getTipoCampo());
    this.getCampo().getValori().clear();
    SqlSelect select = this.getSql();
    if (select != null
        && select.getSql() != null
        && select.getSql().length() > 0) {
      if (this.isAddNull()) this.getCampo().addValore("", "");
      try {
        List ret = sql.getListVector(select.getSql(), select.getParam());
        for (int i = 0; i < ret.size(); i++) {
          Vector row = (Vector) ret.get(i);
          String cod = row.get(0).toString();
          String descr = row.get(0).toString() + " - " + row.get(1).toString();

          if (descr != null && descr.length() > 80)
            descr = descr.substring(0, 80) + "...";
          this.getCampo().addValore(cod, descr);
        }
      } catch (SQLException e) {

      }
    }
  }
  public String postHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  public String preHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  abstract public SqlSelect getSql();

  /**
   * @return the addNull
   */
  public boolean isAddNull() {
    return addNull;
  }

  /**
   * @param addNull
   *        the addNull to set
   */
  public void setAddNull(boolean addNull) {
    this.addNull = addNull;
  }
  
  public String gestisciDaTrova(Vector params, DataColumn colWithValue, String conf, SqlManager manager) {
    return null;
 }

  
  public boolean isAddCodADescr() {
    return addCodADescr;
  }

  
  public String getTipoCampo() {
    return tipoCampo;
  }
  
}
