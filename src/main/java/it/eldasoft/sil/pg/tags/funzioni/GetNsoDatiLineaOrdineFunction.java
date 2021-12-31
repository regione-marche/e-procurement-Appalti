/*
 * Created on 10/06/2020
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
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che recupera i dati della linea dell' ordine NSO
 *
 * @author Cristian.Febas
 */
public class GetNsoDatiLineaOrdineFunction extends AbstractFunzioneTag {

  public GetNsoDatiLineaOrdineFunction() {
    super(3, new Class[] { PageContext.class,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String idOrdine = (String) params[1];
    idOrdine = UtilityStringhe.convertiNullInStringaVuota(idOrdine);
    String idLineaOrdine = (String) params[2];
    idLineaOrdine = UtilityStringhe.convertiNullInStringaVuota(idLineaOrdine);

    if(!"".equals(idOrdine) && !"".equals(idLineaOrdine)){
      Long idO = new Long(idOrdine);
      Long idLineO = new Long(idLineaOrdine);

      String selectLineaOrdine = "select o.stato_ordine,o.ngara,l.codice,f.codimp,l.quantita,l.prezzo_unitario" +
      		" from nso_ordini o, nso_linee_ordini l, nso_fornitore f" +
      		" where o.id = l.nso_ordini_id" +
      		" and o.id = f.nso_ordini_id" +
      		" and l.id = ?";

      String selectQuantitaOriginaria = "select coalesce(quantieff,0),coalesce(preoff,0)" +
      " from v_gcap_dpre where ngara = ? and cod_ditta = ? and codvoc= ? ";


      String selectQuantitaConsumata = "select sum(coalesce(l.quantita,0))" +
      		" from nso_ordini o, nso_linee_ordini l" +
      		" where o.id = l.nso_ordini_id" +
      		" and o.ngara = ? and l.codice = ?" +
      		" and (o.stato_ordine <> ? or o.stato_ordine <> ?) and o.versione = ? ";// +
      		//" and l.id <> ? ";

      String selectPrezzoConsumato = "select sum(coalesce(l.prezzo_unitario,0)*coalesce(l.quantita,0))" +
      " from nso_ordini o, nso_linee_ordini l" +
      " where o.id = l.nso_ordini_id" +
      " and o.ngara = ? and l.codice = ?" +
      " and (o.stato_ordine <> ? or o.stato_ordine <> ?)  and o.versione = ?";// +


      try {



        Vector<?> datiLineaOrdine = sqlManager.getVector(selectLineaOrdine,new Object[] { new Long(idLineO) });
        if (datiLineaOrdine != null && datiLineaOrdine.size() > 0){
          Long statoOrdine = SqlManager.getValueFromVectorParam(datiLineaOrdine, 0).longValue();
          pageContext.setAttribute("statoOrdine",statoOrdine,PageContext.REQUEST_SCOPE);
          String ngara = SqlManager.getValueFromVectorParam(datiLineaOrdine, 1).stringValue();
          String codice = SqlManager.getValueFromVectorParam(datiLineaOrdine, 2).stringValue();
          String codimp = SqlManager.getValueFromVectorParam(datiLineaOrdine, 3).stringValue();
          Double quantita = SqlManager.getValueFromVectorParam(datiLineaOrdine, 4).doubleValue();
          Double prezun = SqlManager.getValueFromVectorParam(datiLineaOrdine, 5).doubleValue();

          if(quantita==null){
            quantita = new Double(0);
          }

          if(prezun==null){
            prezun = new Double(0);
          }

          //verifico che la gara sia monoriga
          Long countProdottiAggiudicataria = (Long) sqlManager.getObject(
              "select count(*) from V_GCAP_DPRE where NGARA= ? and COD_DITTA = ?",
              new Object[] { ngara,codimp });

          String isMonoRiga = "0";
          Double importoDiContratto = null;


          if(new Long(0)<countProdottiAggiudicataria){//MULTIRIGA
            Double qtaOriginaria = (Double) sqlManager.getObject(selectQuantitaOriginaria,new Object[] { ngara,codimp,codice });
            //Double qtaConsumata = (Double) sqlManager.getObject(selectQuantitaConsumata,new Object[] { ngara,codice,new Long(3),new Long(7),new Long(0)});
            Double qtaConsumata = new Double(0);
            Object sum_qtaConsumataTemp = sqlManager.getObject(selectQuantitaConsumata,new Object[] { ngara,codice,new Long(3),new Long(7),new Long(0)});
            if (sum_qtaConsumataTemp != null) {
              if (sum_qtaConsumataTemp instanceof Long) {
                qtaConsumata = new Double(((Long) sum_qtaConsumataTemp));
              } else if (sum_qtaConsumataTemp instanceof Double) {
                qtaConsumata = new Double((Double) sum_qtaConsumataTemp);
              }
            }

            if(qtaOriginaria != null){
              if(qtaConsumata==null){
                qtaConsumata =new Double(0);
              }
              pageContext.setAttribute("qtaConsumata",qtaConsumata,PageContext.REQUEST_SCOPE);
              Double qtaDisponibile = qtaOriginaria-qtaConsumata;
              qtaDisponibile = (Double) UtilityNumeri.arrotondaNumero(qtaDisponibile, new Integer(5));
              pageContext.setAttribute("qtaDisponibile",qtaDisponibile,PageContext.REQUEST_SCOPE);
            }
            //Double prezzoConsumato = (Double) sqlManager.getObject(selectPrezzoConsumato,new Object[] { ngara,codice,new Long(3),new Long(7),new Long(0)});
            Double prezzoConsumato = new Double(0);
            Object sum_prezzoConsumatoTemp = sqlManager.getObject(selectPrezzoConsumato,new Object[] { ngara,codice,new Long(3),new Long(7),new Long(0)});
            if (sum_prezzoConsumatoTemp != null) {
              if (sum_prezzoConsumatoTemp instanceof Long) {
                prezzoConsumato = new Double(((Long) sum_prezzoConsumatoTemp));
              } else if (sum_prezzoConsumatoTemp instanceof Double) {
                prezzoConsumato = new Double((Double) sum_prezzoConsumatoTemp);
              }
            }

            pageContext.setAttribute("prezzoConsumato",prezzoConsumato,PageContext.REQUEST_SCOPE);

            //calcolo l'importo di aggiudicazione
            importoDiContratto = (Double) sqlManager.getObject(
                "select iaggiu from GARE where NGARA= ?", new Object[] { ngara });
            Double prezzoDisponibile = null;
            if(prezzoConsumato!= null){
              prezzoDisponibile = importoDiContratto-prezzoConsumato;
              if(prezzoDisponibile<0){
                prezzoDisponibile = new Double(0);
              }
            }else{
              prezzoDisponibile = importoDiContratto;
            }
            prezzoDisponibile = (Double) UtilityNumeri.arrotondaNumero(prezzoDisponibile, new Integer(2));
            pageContext.setAttribute("prezzoDisponibile",prezzoDisponibile,PageContext.REQUEST_SCOPE);

          }else{
            isMonoRiga = "1";
            //calcolo l'importo di aggiudicazione
            importoDiContratto = (Double) sqlManager.getObject(
                "select iaggiu from GARE where NGARA= ?", new Object[] { ngara });
            Double qtaDisponibile = null;
            Double qtaOriginaria = new Double(1);
            Double qtaConsumata = (Double) sqlManager.getObject(selectQuantitaConsumata,new Object[] { ngara,"1",new Long(3),new Long(7),new Long(0)});
            if(qtaConsumata!= null){
              qtaDisponibile = qtaOriginaria-qtaConsumata;
              if(qtaDisponibile<0){
                qtaDisponibile = new Double(0);
              }
            }else{
              qtaDisponibile = qtaOriginaria;
            }
            pageContext.setAttribute("qtaConsumata",qtaConsumata,PageContext.REQUEST_SCOPE);
            qtaDisponibile = (Double) UtilityNumeri.arrotondaNumero(qtaDisponibile, new Integer(5));
            pageContext.setAttribute("qtaDisponibile",qtaDisponibile,PageContext.REQUEST_SCOPE);
            Double prezzoDisponibile = null;
            Double prezzoConsumato = (Double) sqlManager.getObject(selectPrezzoConsumato,new Object[] { ngara,"1",new Long(3),new Long(7),new Long(0)});
            if(prezzoConsumato!= null){
              prezzoDisponibile = importoDiContratto-prezzoConsumato;
              if(prezzoDisponibile<0){
                prezzoDisponibile = new Double(0);
              }
            }else{
              prezzoDisponibile = importoDiContratto;
            }
            pageContext.setAttribute("prezzoConsumato",prezzoConsumato,PageContext.REQUEST_SCOPE);
            prezzoDisponibile = (Double) UtilityNumeri.arrotondaNumero(prezzoDisponibile, new Integer(2));
            pageContext.setAttribute("prezzoDisponibile",prezzoDisponibile,PageContext.REQUEST_SCOPE);

          }//end monoriga
          Double prezzoTotRiga = quantita * prezun;
          prezzoTotRiga = (Double) UtilityNumeri.arrotondaNumero(prezzoTotRiga, new Integer(5));
          pageContext.setAttribute("prezzoTotRiga",prezzoTotRiga,PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("isMonoRiga",isMonoRiga,PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("numeroGara",ngara,PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("codiceLinea",codice,PageContext.REQUEST_SCOPE);
          pageContext.setAttribute("importoDiContratto",importoDiContratto,PageContext.REQUEST_SCOPE);

        }

      } catch (SQLException e) {
        throw new JspException("Errore nell'estrarre i dati della linea dell'ordine", e);
      } catch (GestoreException ge) {
        throw new JspException("Errore nell'estrarre i dati della linea dell'ordine", ge);
      }

    }

    return null;
  }

}
