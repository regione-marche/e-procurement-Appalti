/*
 * Created on 23 set 2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.ws.rest.cg.CgResponse;
import it.eldasoft.sil.pg.ws.rest.cg.Coefficienti;
import it.eldasoft.sil.pg.ws.rest.cg.PunteggiMassimi;
import it.eldasoft.sil.pg.ws.rest.cg.PunteggiTecniciImprese;
import it.eldasoft.sil.pg.ws.rest.cg.ResponseStatus;
import it.eldasoft.utils.utility.UtilityMath;

public class CalcoloPunteggiManager {
  /** Logger */
  static Logger logger = Logger.getLogger(CalcoloPunteggiManager.class);

  private SqlManager sqlManager;

  private GenChiaviManager  genChiaviManager;

  private TabellatiManager  tabellatiManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  private static final String insertG1Crival = "insert into G1CRIVAL(id,ngara,necvan,dittao,idcridef,coeffi,punteg) values(?,?,?,?,?,?,?)";
  private static final String insertDpun = "insert into DPUN(ngara,necvan,dittao,coeffi,punteg) values(?,?,?,?,?)";
  private static final String updatePunteggi ="update ditg set #NOMECAMPO#=? where ngara5=? and dittao=?";
  private static final String selectG1CridefMaxpun0="select g1.necvan, g1.id from g1cridef g1, goev g where g1.ngara=? and g.ngara=g1.ngara and g.necvan=g1.necvan and g.tippar=? and (g1.modpunti=1 or g1.modpunti=3) and g1.maxpun = 0";
  private static final String selectG1CridefAuto = "select g1.necvan, g1.id, g1.formula, g1.formato, g1.maxpun, g1.esponente from g1cridef g1, goev g where g1.ngara=? and g.ngara=g1.ngara and g.necvan=g1.necvan and g.tippar=? and modpunti = 2";
  private static final String selectG1Crival="select v.necvan, v.coeffi,v.punteg, g.livpar,g.necvan1 from g1cridef d, goev g, g1crival v where d.ngara=? and g.ngara=d.ngara and g.necvan=d.necvan and d.id=v.idcridef "
      + "and v.dittao=? and g.tippar=? and (g.livpar=1 or g.livpar=2) order by norpar, necvan1,norpar1,necvan";

  private static final String selectG1CrivalFiglieG1Cridef = "select count(id) from g1crival where idcridef=? and ngara=? and dittao=?";
  private static final String selectG1CrivalNulli = "select gv.id from g1crival gv, g1cridef gd where gv.idcridef=? and gd.id = gv.idcridef and gv.ngara=? and gv.dittao=? and gd.necvan = ? and gd.formato != 100";
  private static final String selectCrivalStg = "select valstg, id from g1crival g where idcridef = ? and necvan = ? and g.dittao = (select dittao from ditg where ngara5 = ? and ditg.dittao = g.dittao and (fasgar is null or fasgar > ?) )";
  private static final String selectCrivalNum = "select valnum, id from g1crival g where idcridef = ? and necvan = ? and g.dittao = (select dittao from ditg where ngara5 = ? and ditg.dittao = g.dittao and (fasgar is null or fasgar > ?) )";
  private static final String updateCrival = "update G1CRIVAL set coeffi = ?, punteg = ? where id = ?";
  private static final String selectCrivalcom = "select coeffi from g1crivalcom g where g.idcrival = ?";
  private static final String deleteDpun = "delete from dpun where dpun.ngara=? and dpun.necvan=(select g.necvan from goev g where g.ngara=dpun.ngara and g.necvan=dpun.necvan and tippar=?)";
  private static final String selectCriteri = "select v.id, g.maxpun from g1cridef g, g1crival v where g.id = v.idcridef and (g.modpunti = '1' or g.modpunti = '3') and g.maxpun > 0 and v.coeffi is null and v.punteg is null and g.ngara = ? and dittao = ?";
  private static final String selectPunteggiGoev = "select g.necvan, g.necvan1, g.maxpun from goev g, g1cridef gr where g.ngara=? and g.ngara=gr.ngara and g.necvan=gr.necvan and tippar=? and (livpar=1 or livpar=2) and (modpunti=1 or modpunti=3)";
  private static final String selectDitte="select dittao from ditg where ngara5=? and (fasgar is null or fasgar > ?) order by NUMORDPL asc,NOMIMO asc";
  private static final String selectImpapp="select impapp from gare where ngara=?";
  private static final String selectGfof="select count(*) from gfof where ngara2 = ? and espgiu = '1'";

  private static final String ENTITA_CRIVAL="G1CRIVAL";
  private static final String ENTITA_CRIVALCOM="G1CRIVALCOM";
  private static final String ENTITA_DPUN="DPUN";
  private static final String SUFFISSO_ENTITA="_CG";

  /*
  public static final String codiceErroreDITG="-1";
  public static final String codiceErroreG1CRIDEF="-2";
  public static final String codiceErroreIMPAPP="-3";
  public static final String codiceOK="1";
  public static final String codiceNoDitte="O";
*/

  public static final String CALCOLO_DA_APPALTI="1";
  public static final String CALCOLO_DA_APP_ESTERNA="2";

  public static final String CALCOLO_PUNT_TEC="1";
  public static final String CALCOLO_PUNT_ECO="2";

  /**
   * Calcolo del valore minimo di Valnum
   * @param datiG1crival
   * @return BigDecimal
   * @throws SQLException
   */
  private BigDecimal getMinValnum(List<?> datiG1crival) throws SQLException {
    BigDecimal min = null;
    for(int k=0;k<datiG1crival.size();k++){
      Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
      BigDecimal bigValnum= BigDecimal.valueOf(valnum);
      if(min == null){
        min = bigValnum;
      }else{
        int res = bigValnum.compareTo(min);
        if(res < 0){
          min = bigValnum;
        }
      }
    }
    return min;
  }

  private void SetCoefficenteDaFormula(String ngara, Long idCridef, Long necvan, Long formula, Long formato, Double maxpun, Long fasgar,Double esponente,Double impapp, String modo) throws SQLException, GestoreException{

    String tabA1049 = (String)sqlManager.getObject("select tab1desc from tab1 where tab1cod=? and tab1tip=?", new Object[]{"A1049",new Long(1)});
    int decimali = Integer.parseInt(tabA1049);
    int precision = 9;
    String selectCrivalStgModificata=replaceEnt(selectCrivalStg,ENTITA_CRIVAL,modo);
    String selectCrivalNumModificata=replaceEnt(selectCrivalNum,ENTITA_CRIVAL,modo);
    String updateCrivalModificato = replaceEnt(updateCrival,ENTITA_CRIVAL,modo);

    if(formula == 1){
      List<Vector<JdbcParametro>> datiG1crival = this.sqlManager.getListVector(selectCrivalStgModificata, new Object[]{idCridef,necvan,ngara, fasgar});
      if(datiG1crival!=null && datiG1crival.size()>0){
        for(int k=0;k<datiG1crival.size();k++){
          String valstg = SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).stringValue();
          Long id = (Long) SqlManager.getValueFromVectorParam(datiG1crival.get(k),1).getValue();
          Double criregCoeffi = (Double) this.sqlManager.getObject("select g1crireg.coeffi from g1crireg where g1crireg.idcridef = ? and g1crireg.puntuale = ?", new Object[]{idCridef,valstg});
          if(criregCoeffi == null){
            String errMsgEvento="Errore nella lettura del valore puntuale";
            throw new GestoreException(errMsgEvento, "calcoloPunteggi.valorePuntuale",null);
          }
          this.sqlManager.update(updateCrivalModificato, new Object[]{ criregCoeffi,UtilityMath.round((criregCoeffi*maxpun), decimali),id});
          }
        }
    }
    if(formula == 2){
     List<Vector<JdbcParametro>> datiG1crival = this.sqlManager.getListVector(selectCrivalNumModificata, new Object[]{idCridef,necvan,ngara,fasgar});
      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        Long id = (Long) SqlManager.getValueFromVectorParam(datiG1crival.get(k),1).getValue();
        Double criregCoeffi = (Double) this.sqlManager.getObject("select g1crireg.coeffi from g1crireg where g1crireg.idcridef = ? and g1crireg.valmin <= ? and g1crireg.valmax > ?", new Object[]{idCridef,valnum,valnum});
        this.sqlManager.update(updateCrivalModificato, new Object[]{ criregCoeffi,UtilityMath.round((criregCoeffi*maxpun), decimali),id});
        }
    }
    if(formula == 3 || formula == 11){
      List<Vector<JdbcParametro>> datiG1crival = this.sqlManager.getListVector(selectCrivalNumModificata, new Object[]{idCridef,necvan,ngara,fasgar});
      BigDecimal max = new BigDecimal(0);
      BigDecimal bigMaxpun = BigDecimal.valueOf(maxpun);
      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        BigDecimal bigValnum= BigDecimal.valueOf(valnum);
        int res = bigValnum.compareTo(max);
        if(res > 0){max = bigValnum;}
      }
      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        BigDecimal bigValnum= BigDecimal.valueOf(valnum);
        Long id = (Long) SqlManager.getValueFromVectorParam(datiG1crival.get(k),1).getValue();
        BigDecimal criregCoeffi;
        if(max.compareTo(BigDecimal.ZERO)==0){
          criregCoeffi = new BigDecimal(0);
        }else{
          criregCoeffi = bigValnum.divide(max,precision, BigDecimal.ROUND_HALF_UP);
        }
        criregCoeffi = criregCoeffi.setScale(precision, BigDecimal.ROUND_HALF_UP);
        if(formula == 11){
          double coeff = Math.pow(criregCoeffi.doubleValue(), esponente);
          criregCoeffi = BigDecimal.valueOf(coeff);
          criregCoeffi = criregCoeffi.setScale(precision, BigDecimal.ROUND_HALF_UP);
        }
        this.sqlManager.update(updateCrivalModificato, new Object[]{ criregCoeffi.doubleValue(),((criregCoeffi.multiply(bigMaxpun)).setScale(decimali, BigDecimal.ROUND_HALF_UP)).doubleValue(),id});
        }
    }
    if(formula == 14 ) {
      List<Vector<JdbcParametro>> datiG1crival = this.sqlManager.getListVector(selectCrivalNumModificata, new Object[]{idCridef,necvan,ngara,fasgar});
      BigDecimal bigMaxpun = BigDecimal.valueOf(maxpun);
      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        BigDecimal bigValnum= BigDecimal.valueOf(valnum);
        Long id = (Long) SqlManager.getValueFromVectorParam(datiG1crival.get(k),1).getValue();
        BigDecimal criregCoeffi;

        criregCoeffi = bigValnum.divide(BigDecimal.valueOf(100),precision, BigDecimal.ROUND_HALF_UP);
        criregCoeffi = BigDecimal.valueOf(1).subtract(criregCoeffi);
        double coeff = Math.pow(criregCoeffi.doubleValue(), esponente);
        criregCoeffi = BigDecimal.valueOf(coeff);
        criregCoeffi = criregCoeffi.setScale(precision, BigDecimal.ROUND_HALF_UP);
        criregCoeffi = BigDecimal.valueOf(1).subtract(criregCoeffi);
        criregCoeffi = criregCoeffi.setScale(precision, BigDecimal.ROUND_HALF_UP);
        this.sqlManager.update(updateCrivalModificato, new Object[]{ criregCoeffi.doubleValue(),((criregCoeffi.multiply(bigMaxpun)).setScale(decimali, BigDecimal.ROUND_HALF_UP)).doubleValue(),id});
       }
    }
    if(formula == 4 || formula == 5 || formula == 6){
      BigDecimal x = null;
      if(formula == 4){
        x = BigDecimal.valueOf(0.8);
      }else{
        if(formula == 5){
          x = BigDecimal.valueOf(0.85);
        }else{
          if(formula == 6){
            x = BigDecimal.valueOf(0.9);
          }
        }
      }
      List<Vector<JdbcParametro>> datiG1crival = this.sqlManager.getListVector(selectCrivalNumModificata, new Object[]{idCridef,necvan,ngara,fasgar});
      BigDecimal media = new BigDecimal(0);
      BigDecimal max = new BigDecimal(0);
      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        BigDecimal bigValnum= BigDecimal.valueOf(valnum);
        media = media.add(bigValnum);
        int res = bigValnum.compareTo(max);
        if(res > 0){max = bigValnum;}
      }
      media = media.divide(BigDecimal.valueOf(datiG1crival.size()),precision, BigDecimal.ROUND_HALF_UP);
      media.setScale(precision, BigDecimal.ROUND_HALF_UP);

      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        BigDecimal bigValnum= BigDecimal.valueOf(valnum);
        Long id = (Long) SqlManager.getValueFromVectorParam(datiG1crival.get(k),1).getValue();
        BigDecimal criregCoeffi = null;
        if(media.compareTo(BigDecimal.ZERO)==0 && bigValnum.compareTo(BigDecimal.ZERO)==0){
          criregCoeffi = new BigDecimal(0);
        }else{
          int res = bigValnum.compareTo(media);
          if(res <= 0){
            criregCoeffi = (x.multiply(bigValnum)).divide(media,precision, BigDecimal.ROUND_HALF_UP);
          }else{
            if(res > 0){
              if(media.compareTo(max)==0){
                //Non si dovrebbe mai entrare in questa casistica, perchè max == media solo se tutti i valori offerti sono uguali, quindi avremmo valnum == media
                //e si ricadrebbe nel caso precedente con res == 0
                criregCoeffi = new BigDecimal(0);
              }else{
                criregCoeffi = x.add(BigDecimal.valueOf(1).subtract(x).multiply((bigValnum.subtract(media)).divide((max.subtract(media)),precision, BigDecimal.ROUND_HALF_UP)));
              }
            }
          }
        }
        criregCoeffi = criregCoeffi.setScale(precision, BigDecimal.ROUND_HALF_UP);
        this.sqlManager.update(updateCrivalModificato, new Object[]{ criregCoeffi.doubleValue(),((criregCoeffi.multiply(BigDecimal.valueOf(maxpun))).setScale(decimali, BigDecimal.ROUND_HALF_UP)).doubleValue(),id});
        }
    }
    if(formula == 7){
      List<Vector<JdbcParametro>> datiG1crival = this.sqlManager.getListVector(selectCrivalNumModificata, new Object[]{idCridef,necvan,ngara, fasgar});
      BigDecimal min = this.getMinValnum(datiG1crival);
      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        BigDecimal bigValnum = BigDecimal.valueOf(valnum);
        Long id = (Long) SqlManager.getValueFromVectorParam(datiG1crival.get(k),1).getValue();
        BigDecimal criregCoeffi;
        if(bigValnum.compareTo(BigDecimal.ZERO)==0){
          criregCoeffi = new BigDecimal(1);
        }else{
          criregCoeffi = min.divide(bigValnum,precision, BigDecimal.ROUND_HALF_UP);
        }
        criregCoeffi = criregCoeffi.setScale(precision, BigDecimal.ROUND_HALF_UP);
        this.sqlManager.update(updateCrivalModificato, new Object[]{ criregCoeffi.doubleValue(),((criregCoeffi.multiply(BigDecimal.valueOf(maxpun))).setScale(decimali, BigDecimal.ROUND_HALF_UP)).doubleValue(),id});
        }
    }
    if(formula == 8 || formula == 9 || formula == 10){
      BigDecimal bigImpapp = BigDecimal.valueOf(impapp);

      BigDecimal x = null;
      if(formula == 8){
        x = BigDecimal.valueOf(0.8);
      }else{
        if(formula == 9){
          x = BigDecimal.valueOf(0.85);
        }else{
          if(formula == 10){
            x = BigDecimal.valueOf(0.9);
          }
        }
      }
      List<Vector<JdbcParametro>> datiG1crival = this.sqlManager.getListVector(selectCrivalNumModificata, new Object[]{idCridef,necvan,ngara,fasgar});
      BigDecimal media = new BigDecimal(0);
      BigDecimal min = this.getMinValnum(datiG1crival);
      BigDecimal max = new BigDecimal(0);
      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        BigDecimal bigValnum= BigDecimal.valueOf(valnum);
        media = media.add(bigValnum);
        int res = bigValnum.compareTo(max);
        if(res > 0){max = bigValnum;}
      }

      media = media.divide(BigDecimal.valueOf(datiG1crival.size()),precision, BigDecimal.ROUND_HALF_UP);
      media.setScale(precision, BigDecimal.ROUND_HALF_UP);

      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        BigDecimal bigValnum= BigDecimal.valueOf(valnum);
        Long id = (Long) SqlManager.getValueFromVectorParam(datiG1crival.get(k),1).getValue();
        BigDecimal criregCoeffi = null;
        int res = bigValnum.compareTo(media);
        if(bigImpapp.compareTo(media)==0){
          criregCoeffi = new BigDecimal(0);
        }else{
          if(res >= 0){
            criregCoeffi = x.multiply((bigImpapp.subtract(bigValnum))).divide((bigImpapp.subtract(media)),precision, BigDecimal.ROUND_HALF_UP);
          }else{
            if(res < 0){
              if(min.compareTo(media)==0){
                //Non si dovrebbe mai entrare in questa casistica, perchè min == media solo se tutti i valori offerti sono uguali, quindi avremmo valnum == media
                //e si ricadrebbe nel caso precedente con res == 0
                criregCoeffi = new BigDecimal(0);
              }else{
                criregCoeffi = x.add((BigDecimal.valueOf(1).subtract(x)).multiply(((media.subtract(bigValnum)).divide((media.subtract(min)),precision, BigDecimal.ROUND_HALF_UP))));
              }
            }
          }
        }
        criregCoeffi = criregCoeffi.setScale(precision, BigDecimal.ROUND_HALF_UP);
        this.sqlManager.update(updateCrivalModificato, new Object[]{ criregCoeffi.doubleValue(),((criregCoeffi.multiply(BigDecimal.valueOf(maxpun))).setScale(decimali, BigDecimal.ROUND_HALF_UP)).doubleValue(),id});
      }
    }
    if(formula == 12 || formula == 13){
      List<Vector<JdbcParametro>> datiG1crival = this.sqlManager.getListVector(selectCrivalNumModificata, new Object[]{idCridef,necvan,ngara,fasgar});
      BigDecimal min = this.getMinValnum(datiG1crival);
      BigDecimal criregCoeffi = new BigDecimal(0);
      BigDecimal bigImpapp = BigDecimal.valueOf(impapp);
      for(int k=0;k<datiG1crival.size();k++){
        Long id = (Long) SqlManager.getValueFromVectorParam(datiG1crival.get(k),1).getValue();
        if(bigImpapp.subtract(min).compareTo(BigDecimal.ZERO)!=0) {
          Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
          BigDecimal bigValnum = BigDecimal.valueOf(valnum);
          BigDecimal numeratore = bigImpapp.subtract(bigValnum);
          BigDecimal denominatore = bigImpapp.subtract(min);
          criregCoeffi = numeratore.divide(denominatore,precision, BigDecimal.ROUND_HALF_UP);
          if(formula == 13){
            double coeff = Math.pow(criregCoeffi.doubleValue(), esponente);
            criregCoeffi = BigDecimal.valueOf(coeff);
            criregCoeffi = criregCoeffi.setScale(precision, BigDecimal.ROUND_HALF_UP);
          }
        }
        this.sqlManager.update(updateCrivalModificato, new Object[]{ criregCoeffi.doubleValue(),((criregCoeffi.multiply(BigDecimal.valueOf(maxpun))).setScale(decimali, BigDecimal.ROUND_HALF_UP)).doubleValue(),id});
      }
    }
    if(formula == 15 ) {
      List<Vector<JdbcParametro>> datiG1crival = this.sqlManager.getListVector(selectCrivalNumModificata, new Object[]{idCridef,necvan,ngara,fasgar});
      BigDecimal bigMaxpun = BigDecimal.valueOf(maxpun);
      BigDecimal bigImpapp = BigDecimal.valueOf(impapp);
      for(int k=0;k<datiG1crival.size();k++){
        Double valnum = (Double) SqlManager.getValueFromVectorParam(datiG1crival.get(k),0).getValue();
        BigDecimal bigValnum= BigDecimal.valueOf(valnum);
        Long id = (Long) SqlManager.getValueFromVectorParam(datiG1crival.get(k),1).getValue();
        BigDecimal criregCoeffi;

        criregCoeffi = bigValnum.divide(bigImpapp,precision, BigDecimal.ROUND_HALF_UP);
        criregCoeffi = criregCoeffi.setScale(precision, BigDecimal.ROUND_HALF_UP);
        double coeff = Math.pow(criregCoeffi.doubleValue(), esponente);
        criregCoeffi = BigDecimal.valueOf(coeff);
        criregCoeffi = BigDecimal.valueOf(1).subtract(criregCoeffi);
        criregCoeffi = criregCoeffi.setScale(precision, BigDecimal.ROUND_HALF_UP);
        this.sqlManager.update(updateCrivalModificato, new Object[]{ criregCoeffi.doubleValue(),((criregCoeffi.multiply(bigMaxpun)).setScale(decimali, BigDecimal.ROUND_HALF_UP)).doubleValue(),id});
       }
    }
  }

  /**
   * Viene inserita l'occorenza in DPUN relativa al caso di criterio con sottocriteri (LIVPAR=3)
   */
  private void insertDpunPadre(String ngara, Long necvanPadre, String ditta, double punteggioPadre, String modo) throws SQLException{
    Double maxpun = (Double)this.sqlManager.getObject("select maxpun from goev where ngara=? and necvan=?", new Object[]{ngara,necvanPadre});
    if(maxpun!=null){
      double coeffiPadre;
      if(maxpun.doubleValue()!=0){
        coeffiPadre = punteggioPadre / maxpun.doubleValue();
        coeffiPadre =UtilityMath.round(coeffiPadre, 9);
      }else{
        coeffiPadre = new Double(0);
      }
      String sqlInsert=replaceEnt(insertDpun,ENTITA_DPUN,modo);
      this.sqlManager.update(sqlInsert, new Object[]{ ngara,necvanPadre,ditta,new Double(coeffiPadre), new Double(punteggioPadre)});
    }
  }

  public final String replaceEnt(String stringa, String entitaBase, String modo) {
    String sqlFinale = stringa;
    if(CALCOLO_DA_APP_ESTERNA.equals(modo)) {
      sqlFinale = sqlFinale.toUpperCase();
      sqlFinale = sqlFinale.replace(entitaBase, entitaBase + SUFFISSO_ENTITA);
    }
    return sqlFinale;

  }

  /**
   * Viene effettuato il calcolo dei punteggi delle ditte, che nel caso modo valga CALCOLO_DA_APPALTI, vengono salvati nella banca dati.
   * Nel caso in cui modo valga CALCOLO_DA_APP_ESTERNA, i punteggi delle ditte calcolati e le altre informazioni sui criteri vengono caricati nella classe  CgResponse.
   * Il parametro chiaveCommissione viene usato per la lettura dei dati della commissione (entità GFOF), ed è il valore di NGARA, tranne che per le gare a plico unico
   * per cui è CODGAR.
   * Il parametro tipo indica se effettuare il calcolo per i punteggi tecnici o punteggi economici.
   * Nell'elaborazione viene restituito il codice di errore per le operazioni su db che non necessitano un rollback, negli altri casi si genera l'eccezione per ottenere
   * il rollback automatico
   *
   * @param chiaveCommissione
   * @param ngara
   * @param tipo
   * @param modo
   * @return CgResponse
   * @throws SQLException
   * @throws GestoreException
   */
  public CgResponse calcolaPunteggi(String chiaveCommissione, String ngara, String tipo, String modo) throws SQLException, GestoreException {
    CgResponse risposta = new CgResponse();
    List<PunteggiTecniciImprese> punteggiTecniciImprese = new ArrayList<PunteggiTecniciImprese>();
    List<Coefficienti> coefficienti = new ArrayList<Coefficienti>();
    List<PunteggiMassimi> punteggiMassimi = new ArrayList<PunteggiMassimi>();


    Object par[] = new Object[2];
    Long fasgar;
    par[0]=ngara;
    if(CALCOLO_PUNT_TEC.equals(tipo)){
      par[1]=new Long(5);
      fasgar=new Long(5);
    }
    else{
      par[1]=new Long(6);
      fasgar=new Long(6);
    }

    List<?> listaDitteGara= null;
    try {
      listaDitteGara=sqlManager.getListVector(selectDitte, par);
    }catch (SQLException e) {
      risposta.setStatoCalcolo(ResponseStatus.ERRORE_LETTURA_DITG);
      return risposta;
    }

    List<?> datiG1cridefmaxpun0 = null;
    List<?> datiG1cridefAuto = null;

    try {
      datiG1cridefmaxpun0 = this.sqlManager.getListVector(selectG1CridefMaxpun0, new Object[]{ngara,new Long(tipo)});
      datiG1cridefAuto = this.sqlManager.getListVector(selectG1CridefAuto, new Object[]{ngara,new Long(tipo)});
    } catch (SQLException e) {
      risposta.setStatoCalcolo(ResponseStatus.ERRORE_LETTURA_G1CRIDEF);
      return risposta;
    }

    Double impapp= null;
    try {
      impapp=(Double)sqlManager.getObject(selectImpapp, new Object[] {ngara});
    }catch (SQLException e) {
      risposta.setStatoCalcolo(ResponseStatus.ERRORE_LETTURA_IMPAPP);
      return risposta;
    }

    try {
      //si cancellano tutte le occorrenze in DPUN per la gara
      String delDpun = replaceEnt(deleteDpun,ENTITA_DPUN,modo);
      this.sqlManager.update(delDpun, new Object[]{ngara, new Long(tipo)});
    } catch (SQLException e) {
      throw e;
    }

    if(listaDitteGara!=null && listaDitteGara.size()>0){
      String ditta = null;
      Long necvan=null;
      Long idCridef=null;
      Long formula=null;
      Long formato=null;
      long idCrival;
      Double maxpun = null;

      List<?> datiG1crival = null;
      Long necvang1crival=null;
      Double coeffi = null;
      Long livpar = null;
      Double punteg = null;
      Long necvan1 = null;
      Long necvanPadre = null;
      double punteggioPadre = 0;
      double punteggioTotale=0;
      boolean gruppoCriteriFigli=false;
      boolean ultimoGruppoSalvato=false;
      Double esponente = null;

      //Inserimento occorrenza in G1CRIVAL per occorrenze di G1CRIDEF con MODPUNTI = 2
      if(datiG1cridefAuto!=null && datiG1cridefAuto.size()>0){

        for(int k=0;k<datiG1cridefAuto.size();k++){
          //Prima di inserire l'occorrenza, si deve controllare che non esista già
          necvan=SqlManager.getValueFromVectorParam(datiG1cridefAuto.get(k),0).longValue();
          idCridef=SqlManager.getValueFromVectorParam(datiG1cridefAuto.get(k),1).longValue();
          formula=SqlManager.getValueFromVectorParam(datiG1cridefAuto.get(k),2).longValue();
          formato=SqlManager.getValueFromVectorParam(datiG1cridefAuto.get(k),3).longValue();
          maxpun=SqlManager.getValueFromVectorParam(datiG1cridefAuto.get(k),4).doubleValue();
          esponente=SqlManager.getValueFromVectorParam(datiG1cridefAuto.get(k),5).doubleValue();
          this.SetCoefficenteDaFormula(ngara,idCridef,necvan,formula,formato,maxpun,fasgar,esponente,impapp,modo);
        }
      }

      Integer numeroDecimali = new Integer(tabellatiManager.getDescrTabellato("A1049", "1"));

      for(int i=0;i<listaDitteGara.size();i++){
        ditta = SqlManager.getValueFromVectorParam(listaDitteGara.get(i), 0).getStringValue();
        punteggioPadre = 0;
        punteggioTotale=0;
        gruppoCriteriFigli=false;
        necvanPadre=null;
        String updateCrival_Mod = replaceEnt(updateCrival,ENTITA_CRIVAL,modo);

        //Inserimento occorrenza in G1CRIVAL per occorrenze di G1CRIDEF con MODPUNTI = 1 & MAXPUN = 0
        if(datiG1cridefmaxpun0!=null && datiG1cridefmaxpun0.size()>0){
          Long numeroOcc=null;
          String selectG1CrivalFiglieG1Cridef_Mod = replaceEnt(selectG1CrivalFiglieG1Cridef,ENTITA_CRIVAL,modo);
          String insertG1Crival_Mod = replaceEnt(insertG1Crival,ENTITA_CRIVAL,modo);
          String selectG1CrivalNulli_Mod = replaceEnt(selectG1CrivalNulli,ENTITA_CRIVAL,modo);
          String ent = ENTITA_CRIVAL + (CALCOLO_DA_APP_ESTERNA.equals(modo) ? SUFFISSO_ENTITA : "");
          for(int k=0;k<datiG1cridefmaxpun0.size();k++){
            //Prima di inserire l'occorrenza, si deve controllare che non esista già
            necvan=SqlManager.getValueFromVectorParam(datiG1cridefmaxpun0.get(k),0).longValue();
            idCridef=SqlManager.getValueFromVectorParam(datiG1cridefmaxpun0.get(k),1).longValue();
            //se non ci sono occorrenze in crival
            numeroOcc = (Long)this.sqlManager.getObject(selectG1CrivalFiglieG1Cridef_Mod, new Object[]{idCridef,ngara,ditta});
            if(numeroOcc==null || (new Long(0)).equals(numeroOcc)){
              idCrival = this.genChiaviManager.getNextId(ent);
              this.sqlManager.update(insertG1Crival_Mod, new Object[]{ idCrival,ngara,necvan,ditta,idCridef,new Double(0),new Double(0)});
            }
            else{
              Long id = (Long)this.sqlManager.getObject(selectG1CrivalNulli_Mod, new Object[]{idCridef,ngara,ditta,necvan});
              if(id!=null){
                this.sqlManager.update(updateCrival_Mod, new Object[]{ new Double(0),new Double(0), id});
              }
            }
          }
        }

        Long conteggioGfof = (Long) sqlManager.getObject(selectGfof, new Object[] {chiaveCommissione});
        if(conteggioGfof != null && conteggioGfof.intValue()>0){
          String selectCriteri_Mod = replaceEnt(selectCriteri,ENTITA_CRIVAL,modo);
          datiG1crival = this.sqlManager.getListVector(selectCriteri_Mod, new Object[]{ngara, ditta});
          if(datiG1crival!=null && datiG1crival.size()>0){
            String selectCrivalcom_Mod = replaceEnt(selectCrivalcom,ENTITA_CRIVALCOM,modo);
            for(int j=0;j<datiG1crival.size();j++){
               Long idcrival =SqlManager.getValueFromVectorParam(datiG1crival.get(j),0).longValue();
               Double maxpunCridef =(Double) SqlManager.getValueFromVectorParam(datiG1crival.get(j),1).getValue();

               List listaValutazioniCommissari = this.sqlManager.getListVector(selectCrivalcom_Mod, new Object[]{idcrival});
               Double mediaVal = new Double(0);
               if(listaValutazioniCommissari!=null && listaValutazioniCommissari.size()>0){
                 for(int n=0;n<listaValutazioniCommissari.size();n++){
                   Double coeffiCommissario =(Double) SqlManager.getValueFromVectorParam(listaValutazioniCommissari.get(n),0).getValue();
                   if (coeffiCommissario == null)
                     coeffiCommissario = new Double(0);
                   mediaVal = mediaVal+ coeffiCommissario;
                 }
                 mediaVal = mediaVal/listaValutazioniCommissari.size();

                 Double puntegCrival = UtilityMath.round(mediaVal * maxpunCridef, numeroDecimali);
                 this.sqlManager.update(updateCrival_Mod, new Object[]{ mediaVal,puntegCrival, idcrival});
               }
            }
          }

        }

        String selectG1Crival_Mod = replaceEnt(selectG1Crival,ENTITA_CRIVAL,modo);
        datiG1crival = this.sqlManager.getListVector(selectG1Crival_Mod, new Object[]{ngara, ditta, new Long(tipo)});
        if(datiG1crival!=null && datiG1crival.size()>0){
          String insertDpun_Mod = replaceEnt(insertDpun,ENTITA_DPUN,modo);
          for(int j=0;j<datiG1crival.size();j++){
            //Inserimento occorrenze in DPUN relative solo ai criteri senza sottocriteri e ai sottocriteri
            necvang1crival=SqlManager.getValueFromVectorParam(datiG1crival.get(j),0).longValue();
            coeffi=SqlManager.getValueFromVectorParam(datiG1crival.get(j),1).doubleValue();
            punteg=SqlManager.getValueFromVectorParam(datiG1crival.get(j),2).doubleValue();
            livpar=SqlManager.getValueFromVectorParam(datiG1crival.get(j),3).longValue();
            necvan1=SqlManager.getValueFromVectorParam(datiG1crival.get(j),4).longValue();

            if(coeffi==null)
              coeffi=new Double(0);
            if(punteg==null)
              punteg=new Double(0);

            if(CALCOLO_DA_APP_ESTERNA.equals(modo)) {
              Coefficienti coeff= new Coefficienti()
                  .coefficiente(BigDecimal.valueOf(coeffi).setScale(9, BigDecimal.ROUND_HALF_UP).doubleValue())
                  .codimp(ditta)
                  .necvan(necvang1crival)
                  .necvan1(necvan1)
                  .ngara(ngara)
                  .punteggio(BigDecimal.valueOf(punteg).setScale(numeroDecimali, BigDecimal.ROUND_HALF_UP).doubleValue());
              coefficienti.add(coeff);
            }

            this.sqlManager.update(insertDpun_Mod, new Object[]{ ngara,necvang1crival,ditta,coeffi,punteg});
            //Inserimento occorrenza in DPUN relativa ai criteri con sottocriteri
            //Per fare ciò si sfrutta il fatto che le occorrenze caricate da G1CRIVAL sono ordinate in modo da
            //raggruppare tutti i sottocriteri di uno stesso criterio
            ultimoGruppoSalvato=false;
            if(new Long(2).equals(livpar)){
              if(necvanPadre==null || !necvanPadre.equals(necvan1)){
                gruppoCriteriFigli=true;
                if(necvanPadre!=null && !necvanPadre.equals(necvan1)){
                  punteggioTotale += punteggioPadre;
                  this.insertDpunPadre(ngara, necvanPadre, ditta, punteggioPadre,modo);
                  punteggioPadre = 0;
                }
                necvanPadre = necvan1;
                punteggioPadre += punteg.doubleValue();

              }else{
                punteggioPadre += punteg.doubleValue();
              }
            }else if(new Long(1).equals(livpar)){
              if(gruppoCriteriFigli){
                punteggioTotale += punteggioPadre;
                this.insertDpunPadre(ngara, necvanPadre, ditta, punteggioPadre,modo);
                ultimoGruppoSalvato=true;
              }
              punteggioTotale+= punteg.doubleValue();
              gruppoCriteriFigli=false;
              necvanPadre=null;
              punteggioPadre=0;
            }
          }
          if(!ultimoGruppoSalvato && gruppoCriteriFigli==true){
            punteggioTotale += punteggioPadre;
            this.insertDpunPadre(ngara, necvanPadre, ditta, punteggioPadre,modo);
          }


          if(CALCOLO_DA_APPALTI.equals(modo)) {
            //Salvataggio punteggio totale della ditta
            String sqlUpdatePunteggi=null;
            if(CALCOLO_PUNT_TEC.equals(tipo))
              sqlUpdatePunteggi=updatePunteggi.replace("#NOMECAMPO#", "puntec");
            else
              sqlUpdatePunteggi=updatePunteggi.replace("#NOMECAMPO#", "puneco");

            this.sqlManager.update(sqlUpdatePunteggi, new Object[]{ new Double(punteggioTotale), ngara,ditta});
          }else if(CALCOLO_DA_APP_ESTERNA.equals(modo)) {
            PunteggiTecniciImprese punTecImpresa= new PunteggiTecniciImprese().codimp(ditta).punteggio(BigDecimal.valueOf(punteggioTotale).setScale(numeroDecimali, BigDecimal.ROUND_HALF_UP).doubleValue());
            punteggiTecniciImprese.add(punTecImpresa);
          }

        }

      }

      //Lettura dei punteggi massimi
      List<Vector<JdbcParametro>> listaPunteggiMax = sqlManager.getListVector(selectPunteggiGoev, new Object[] {ngara,new Long(1)});
      if(listaPunteggiMax!=null) {
        Iterator<Vector<JdbcParametro>> iter = listaPunteggiMax.iterator();
          Vector<JdbcParametro> vetCriterio = null;
          Double puntMaxDouble=null;
          while (iter.hasNext()) {
            vetCriterio = iter.next();
            puntMaxDouble = SqlManager.getValueFromVectorParam(vetCriterio,2).doubleValue();
            punteggiMassimi.add(new PunteggiMassimi()
                .necvan(SqlManager.getValueFromVectorParam(vetCriterio,0).longValue())
                .necvan1(SqlManager.getValueFromVectorParam(vetCriterio,1).longValue())
                .punteggioMassimo(BigDecimal.valueOf(puntMaxDouble).setScale(numeroDecimali, BigDecimal.ROUND_HALF_UP).doubleValue()));
        }
      }
      risposta.setStatoCalcolo(ResponseStatus.OK);
    }else {
      risposta.setStatoCalcolo(ResponseStatus.NO_DITTE);
    }
    if(CALCOLO_DA_APP_ESTERNA.equals(modo)) {
      risposta.setPunteggiTecniciImprese(punteggiTecniciImprese);
      risposta.setPunteggiMassimi(punteggiMassimi);;
      risposta.setCoefficienti(coefficienti);
    }
    return risposta;
  }

}
