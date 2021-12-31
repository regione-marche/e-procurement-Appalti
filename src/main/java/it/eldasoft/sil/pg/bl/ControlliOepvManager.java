/*
 * Created on 07/ago/2018
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.bl;

import it.eldasoft.gene.bl.SqlManager;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Manager che raccoglie alcune funzionalità per la gestione Oepv
 *
 * @author Cristian.Febas
 */
public class ControlliOepvManager {

  /** Logger */
  static Logger       logger = Logger.getLogger(ElencoOperatoriManager.class);

   /** Manager SQL per le operazioni su database */
  private SqlManager  sqlManager;

  /**
   * Set SqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public boolean isVecchiaOepv(String codiceGara) throws SQLException{
    Long offtel;
    Long tippub;
        offtel = (Long) sqlManager.getObject(
            "select offtel from torn where codgar = ?",
            new Object[] { codiceGara });
        tippub = (Long) sqlManager.getObject(
            "select tippub from pubbli where codgar9 = ? and (tippub = 11 or tippub = 13 or tippub = 23)",
            new Object[] { codiceGara });


        if(offtel == null || offtel == 2){
          return true;
        }
        if(offtel == 1 && tippub != null){
          String selectLottiFormatoDef = "select gare.ngara from gare inner join g1cridef on formato != 100 and gare.ngara = g1cridef.ngara  and codgar1 = ? and modlicg = 6";
          List datiLotti = sqlManager.getVector(selectLottiFormatoDef,
              new Object[] { codiceGara });
          if(datiLotti != null && datiLotti.size() > 0) {
            return false;
          }
          else{
            return true;
          }
        }
        return false;
  }

  public boolean isVecchiaOepvFromNgara(String ngara) throws SQLException{
    Long offtel;
    Long tippub;
    String codiceGara = null;

    codiceGara = (String) sqlManager.getObject(
        "select codgar1 from gare where ngara = ?",
        new Object[] { ngara });
    offtel = (Long) sqlManager.getObject(
        "select offtel from torn where codgar = ?",
        new Object[] { codiceGara });
    tippub = (Long) sqlManager.getObject(
        "select tippub from pubbli where codgar9 = ? and (tippub = 11 or tippub = 13 or tippub = 23)",
        new Object[] { codiceGara });


    if(offtel == null || offtel == 2){
      return true;
    }
    if(offtel == 1 && tippub != null){
      String selectLottiFormatoDef = "select gare.ngara from gare inner join g1cridef on formato != 100 and gare.ngara = g1cridef.ngara  and codgar1 = ? and modlicg = 6";
      List datiLotti = sqlManager.getVector(selectLottiFormatoDef,
          new Object[] { codiceGara });
      if(datiLotti != null && datiLotti.size() > 0) {
        return false;
      }
      else{
        return true;
      }
    }
    return false;
  }

  public boolean checkFormato(String ngara, Long formato) throws SQLException {
    Long result = null;
    result = (Long) sqlManager.getObject(
        "select formato from g1cridef where formato = ? and g1cridef.ngara = ?",
        new Object[] { formato, ngara });
    if(result != null){
      return true;
    }
    return false;
  }

  public boolean checkQualuqueFormatoDefinito(String ngara, Long tippar) throws SQLException {

    Long count = (Long)  sqlManager.getObject("select count(*) from g1cridef, goev where g1cridef.ngara = goev.ngara " +
        "and g1cridef.necvan = goev.necvan and tippar = ? and formato != 100 and g1cridef.ngara = ?", new Object[] { tippar, ngara });
    if(new Long(0).equals(count)){
      return false;
    }else{
      return true;
    }
  }


}