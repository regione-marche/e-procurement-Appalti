/*
 * Created on 07/07/2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore che effettua i controlli preliminari per la creazione
 * della commissione dall'albo, distinguendo fra gli stati di :
 * VUOTA
 * INCOMPLETA
 * COMPLETA
 *
 * @author Cristian Febas
 */
public class GestoreControlliPreliminariCreaCommissione extends AbstractGestorePreload {



  public GestoreControlliPreliminariCreaCommissione(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);


    String ngara = page.getRequest().getParameter("ngara");
    String codgar = page.getRequest().getParameter("codgar");
    String garaLottiConOffertaUnica = page.getRequest().getParameter("garaLottiConOffertaUnica");

    String statoCommissione = "VUOTA";

    //occorre distinguere per offerta unica
    String selectNominativiConfigurazione = "";
    if(!"true".equals(garaLottiConOffertaUnica)){
      selectNominativiConfigurazione = "select c.tipostruttura, c.codein, c.ruolo, c.riservaruolo, c.numcompo, t.codrup" +
      " from commconf c, gare g , torn t" +
      " where c.criterioagg = g.critlicg and g.codgar1 = t.codgar and g.codgar1 = ? and g.ngara = ?" +
      " order by id";
    }else{
      selectNominativiConfigurazione = "select c.tipostruttura, c.codein, c.ruolo, c.riservaruolo, c.numcompo, t.codrup" +
      " from commconf c, gare g , torn t" +
      " where c.criterioagg = t.critlic and g.codgar1 = t.codgar and g.codgar1 = ? and g.ngara = ?" +
      " order by id";
    }


    String selectPresentiCommissione = "select g.codfof, g.incfof, g.indisponibilita, g.datarichiesta, g.dataaccettazione, c.codein" +
    		" from gfof g,commnomin c where g.ngara2 = ? and g.numcomm = ? and g.codfof = c.codtec and c.idalbo = ? " +
    		" order by g.incfof, g.indisponibilita, g.datarichiesta, g.dataaccettazione";

    /*
     *Controllo preliminare su esistenza SA e RUP
     */
    String selectTorn  = "select codrup, cenint, idcommalbo from torn where codgar = ? ";
    String codRUP = "";
    String codSA = "";
    Long idCommAlbo = null;

    try {

      Vector datiTorn = sqlManager.getVector(selectTorn, new Object[] {codgar});
      if(datiTorn!= null && datiTorn.size()>0){
        if(datiTorn.get(0)!= null){
          codRUP = (String)((JdbcParametro)datiTorn.get(0)).getValue();
          codRUP =  UtilityStringhe.convertiNullInStringaVuota(codRUP);
        }else{
          statoCommissione = "NORUPSA";
        }
        if(datiTorn.get(1)!= null){
          codSA = (String)((JdbcParametro)datiTorn.get(1)).getValue();
          codSA =  UtilityStringhe.convertiNullInStringaVuota(codSA);
        }else{
          statoCommissione = "NORUPSA";
        }
        if(datiTorn.get(2)!= null){
          idCommAlbo = (Long)((JdbcParametro)datiTorn.get(2)).getValue();
        }
      }
      if("".equals(codRUP) || "".equals(codSA)){
        statoCommissione = "NORUPSA";
      }

      if(!"NORUPSA".equals(statoCommissione)){
        List listaPresentiCommissione = sqlManager.getListVector(selectPresentiCommissione, new Object[] {ngara, new Long(1), idCommAlbo });
        List listaNominativiConfigurazione = sqlManager.getListVector(selectNominativiConfigurazione, new Object[] {codgar, ngara });
        if (listaNominativiConfigurazione != null && listaNominativiConfigurazione.size() > 0) {
          long flag_statoCommissione = 0;
          long sizeCommissione = 0;
          long flag_richiestaIndisponibilita = 0;
          long flag_accettazioneIndisponibilita = 0;
          Map<Long, Long> indispRuoloMap = new HashMap<Long, Long>();
          for (int i = 0; i < listaNominativiConfigurazione.size(); i++) {
            Long tipoStruttura = SqlManager.getValueFromVectorParam(listaNominativiConfigurazione.get(i), 0).longValue();
            String struttura = SqlManager.getValueFromVectorParam(listaNominativiConfigurazione.get(i), 1).getStringValue();
            Long ruolo = SqlManager.getValueFromVectorParam(listaNominativiConfigurazione.get(i), 2).longValue();
            Long riservaRuolo = SqlManager.getValueFromVectorParam(listaNominativiConfigurazione.get(i), 3).longValue();
            Long numCompo = SqlManager.getValueFromVectorParam(listaNominativiConfigurazione.get(i), 4).longValue();
            if (numCompo != null) {
              sizeCommissione = sizeCommissione + numCompo.longValue();
            } else {
              // gestisci un errore
            }

            indispRuoloMap.put(ruolo, new Long(0));
            for (int j = 1; j <= numCompo; j++) {
              boolean ruoloFounded = false;
              Long ruolok = null;
              if (listaPresentiCommissione != null && listaPresentiCommissione.size() > 0) {
                for (int n = 0; n < 2; n++) {
                  if (n == 0) {
                    ruolok = ruolo;
                  } else {
                    ruolok = riservaRuolo;
                  }
                  if (ruolok != null) {

                    for (int k = 0; k < listaPresentiCommissione.size(); k++) {
                      Long incarico = SqlManager.getValueFromVectorParam(listaPresentiCommissione.get(k), 1).longValue();
                      String indisponibilita = SqlManager.getValueFromVectorParam(listaPresentiCommissione.get(k), 2).getStringValue();
                      Date dataRichiesta = SqlManager.getValueFromVectorParam(listaPresentiCommissione.get(k), 3).dataValue();
                      Date dataAccettazione = SqlManager.getValueFromVectorParam(listaPresentiCommissione.get(k), 4).dataValue();
                      String codein = SqlManager.getValueFromVectorParam(listaPresentiCommissione.get(k), 5).getStringValue();
                      if (ruolok != null && ruolok.equals(incarico)) {
                        if(new Long(1).equals(tipoStruttura)){
                          ruoloFounded = true;
                        }else{
                          if(new Long(2).equals(tipoStruttura) && codein.equals(struttura)){
                            ruoloFounded = true;
                          }else{
                            if(new Long(3).equals(tipoStruttura) && codein.equals(codSA)){
                              ruoloFounded = true;
                            }
                          }
                        }
                      }
                      if (ruoloFounded == true) {
                        if ("1".equals(indisponibilita)) {
                          if (dataRichiesta != null) {
                            if (dataAccettazione != null) {
                              Long indispRuolo = indispRuoloMap.get(ruolo);
                              if (indispRuolo < numCompo) {
                                // occorre verificare che non sia già stato sostituito
                                flag_accettazioneIndisponibilita = flag_accettazioneIndisponibilita + 1;
                                indispRuoloMap.put(ruolo, indispRuolo + 1);
                              }

                              ruoloFounded = false;
                              listaPresentiCommissione.remove(k);
                              k = k - 1;
                            } else {
                              flag_richiestaIndisponibilita = flag_richiestaIndisponibilita + 1;
                              flag_statoCommissione = flag_statoCommissione + 1;
                            }
                          } else {
                            flag_statoCommissione = flag_statoCommissione + 1;
                          }
                        } else {
                          flag_statoCommissione = flag_statoCommissione + 1;
                        }
                        if (ruoloFounded == true) {
                          listaPresentiCommissione.remove(k);
                          k = k - 1;
                          break;
                        } else {
                          ;
                        }

                      }// if
                    }// for

                    if (ruoloFounded == true) {
                      break;
                    }
                  }// if ruolok not null
                }// for ruolo/ruoloRiserva
              }
            }

          }// for nominativi configurazione

          // sizeCommissione = size lato configurazione

          if (flag_statoCommissione > 0) {
            if (flag_statoCommissione == sizeCommissione) {
              statoCommissione = "COMPLETA";
              /*
               * if(flag_richiestaIndisponibilita > 0 ){ statoCommissione = "ATTESA"; }
               */
            } else {
              if (flag_statoCommissione < sizeCommissione) {
                statoCommissione = "SOSTITUZIONE";
              }
              /*
               * if(flag_accettazioneIndisponibilita > 0 ){ if(flag_statoCommissione + flag_accettazioneIndisponibilita == sizeCommissione){
               * statoCommissione = "SOSTITUZIONE"; }else{ statoCommissione = "INCOMPLETA"; } }else{ statoCommissione = "INCOMPLETA"; }
               */
            }
          } else {
            statoCommissione = "VUOTA";
          }
        } else {
          // non esiste la configurazione
          statoCommissione = "NOCONF";
        }
        page.setAttribute("statoCommissione", statoCommissione);

    }else{
      page.setAttribute("statoCommissione", statoCommissione);
    }


    } catch (SQLException sqle) {
      throw new JspException("Errore nella verifica preliminare della commissione", sqle);
    } catch (GestoreException ge) {
      throw new JspException("Errore nella verifica preliminare della commissione", ge);
    }

  }

}