/*
 * Created on 25/03/2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Manager che raccoglie alcune funzionalità per Autovie
 *
 * @author Cristian.Febas
 */
public class AutovieManager {

  /** Logger */
  static Logger       logger = Logger.getLogger(ElencoOperatoriManager.class);

   /** Manager SQL per le operazioni su database */
  private SqlManager  sqlManager;

  /** Manager per la generazione delle chiavi */
  private GenChiaviManager    genChiaviManager;

  /**
   * Set SqlManager
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * @param genChiaviManager
   *        genChiaviManager da settare internamente alla classe.
   */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }



  /**
   * Funzione che esegue il calcolo delle codifica automatica specifica per Autovie
   *
   * @param entita
   *        Entita = GARE o TORN
   * @param isGaraLottoUnico
   * @param codiceGaraLotti
   *        Codice della gara a lotti alla quale si sta creando un nuovo lotto
   *
   * @return HashMap contenente due oggetti associati alle chiavi 'codiceGara' e
   *         'numeroGara'
   * @throws GestoreException
   */
  public HashMap calcolaCodificaAutomatica(String entita,Boolean isGaraLottoUnico, String codiceGaraLotti)
      throws GestoreException {

    HashMap result = new HashMap();
    String codiceGara = null;
    String numeroGara = null;

    if (entita != null && entita.length() > 0) {
      Long contatoreCodgarcli = new Long(genChiaviManager.getNextId("TORN.CODGARCLI"));
      GregorianCalendar dataOdierna = new GregorianCalendar();
      Integer anno = new Integer(dataOdierna.get(Calendar.YEAR));
      String annoString = anno.toString();
      String codiceCodgarcli = contatoreCodgarcli.toString() + "/" + annoString.substring(2);

      if ("GARE".equals(entita.toUpperCase())) {
        if (isGaraLottoUnico != null) {
          if (isGaraLottoUnico.booleanValue()) {
            result.put("codiceGara", "$" + codiceCodgarcli);
            result.put("numeroGara", codiceCodgarcli);
          }
        }
      } else if ("TORN".equals(entita.toUpperCase())) {
        result.put("codiceGara", codiceCodgarcli);
      } else if ("LOTTO".equals(entita.toUpperCase())) {

          Long maxSuffix = null;
          String numeroLotto = null;
          try {
            maxSuffix = (Long) sqlManager.getObject("" +
            		"select max(coalesce(to_number(substr(ngara, length(ngara)-2,length(ngara)),'999'),0))" +
            		" from gare where codgar1 = ? and codgar1<>ngara", new Object[]{codiceGaraLotti});
          } catch (SQLException e1) {
            logger.info("Non risulta possibile determinare il max suffisso per un lotto della gara!");
          }
          if (maxSuffix ==  null){
            maxSuffix = new Long(0);
          }

          boolean numGaraExists = true;
          Long newSuffix = null;
          while(numGaraExists){
            newSuffix = maxSuffix + 1;
            StringBuffer strBuffer = new StringBuffer("");
            // Giustifico a destra
            for (int i = 0; i < (3 - String.valueOf(newSuffix).length()); i++){
              strBuffer.append('0');
            }
            strBuffer.append(String.valueOf(newSuffix));
            numeroLotto = new String(strBuffer);
            numeroGara = codiceGaraLotti  + "/" + numeroLotto;
            //verifico esistenza lotto
            Long res;
            try {
              res = (Long) this.sqlManager.getObject(
                  "select count(*) from GARE where NGARA = ?",
                  new Object[] { numeroGara });
              if ((res != null && res > 0)){
                numGaraExists = true;
              }else{
                numGaraExists = false;
              }

            } catch (SQLException e) {
              throw new GestoreException(
                  "Errore nella selezione del numero della gara preesistenti", null, e);
            }
          }

          result.put("numeroGara", numeroGara);
          result.put("codiga",Long.valueOf(String.valueOf(numeroLotto)));
      }
    }

    return result;
  }

  public void verificaEsistenzaNumeroGara(String nGaraInserito)
    throws GestoreException {

    if (nGaraInserito != null && nGaraInserito.length() > 0) {
      // Verifico la non esistenza del numero di gara inserito manualmente
      try {
        List ret1 = this.sqlManager.getVector(
            "select 1 from GARE where NGARA = ?",
            new Object[] { nGaraInserito });

        if ((ret1 != null && ret1.size() > 0)){
          throw new GestoreException(
              "Il numero della gara inserito è già esistente",
              "verificaEsistenzaNumeroGara.nGaraEsistente");
        }
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella selezione del numero della gara inserito", null, e);
      }
    }
  }


}