package it.eldasoft.sil.pg.bl;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;


/**
 *
 * @author cristian.febas
 *
 */
public class NsoOrdiniManager {
  private static final Logger logger = Logger.getLogger(NsoOrdiniManager.class);

  /** Manager per le transazioni e selezioni nel database */
  private GeneManager         geneManager;
  /** Manager per la gestione delle chiavi di una entita */
  private GenChiaviManager   genChiaviManager;


  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }


  /**
   * Variazione ordine
   *
   * @param status
   * @param idOrdineSorgente
   *        ,
   * @param idOrdineNuovo
   * @throws SQLException
   */
  public Long variazioneOrdine(TransactionStatus status, Long idOrdineOrigine, HttpServletRequest request) throws GestoreException {

    ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long syscon = new Long(profilo.getId());
    Long idNuovoOrdine = null;
    Long idOriginarioOrdine = null;
    // verifico l'esistenza dell'ordine orgine
    try {
      List ret = this.geneManager.getSql().getVector("select 1 from nso_ordini where id = ?", new Object[] { idOrdineOrigine });
      if (ret == null || ret.size() == 0)
        throw new GestoreException("L'ordine non esiste nella base dati !", "copiaOrdine.origineNonEsistente");
    } catch (SQLException e) {
      throw new GestoreException("Errore nella selezione dell'ordine origine !", "copiaOrdine", e);
    }
    String entitaCopia[] = { "NSO_ORDINI","NSO_ORDINANTI","NSO_BENEFICIARIO",
        "NSO_FORNITORE","NSO_PUNTICONS","NSO_LINEE_ORDINI" };
    for (int i = 0; i < entitaCopia.length; i++) {
      String filtroSelEntita = null;
      if("NSO_ORDINI".equals(entitaCopia[i])){
        filtroSelEntita = " id = ? ";
      }else{
        filtroSelEntita = " nso_ordini_id = ?";
      }
      if (this.geneManager.getSql().isTable(entitaCopia[i])
          && this.geneManager.countOccorrenze(entitaCopia[i], filtroSelEntita, new Object[] { idOrdineOrigine }) > 0) {
        StringBuffer sql = new StringBuffer("select * from ");
        sql.append(entitaCopia[i]);
        sql.append( " where" + filtroSelEntita);
        // Se ci sono occorenze allora eseguo la copia
        DataColumnContainer impl = new DataColumnContainer(this.geneManager.getSql(), entitaCopia[i], sql.toString(),
            new Object[] { idOrdineOrigine });
        try {
          List dati = this.geneManager.getSql().getListHashMap(sql.toString(), new Object[] { idOrdineOrigine });
          if (dati != null && dati.size() > 0) {
            for (int row = 0; row < dati.size(); row++) {
              impl.setValoriFromMap((HashMap) dati.get(row), true);
              if("NSO_ORDINI".equals(entitaCopia[i])){
                idOriginarioOrdine = impl.getLong("NSO_ORDINI.ID_ORIGINARIO");
                idNuovoOrdine = new Long(genChiaviManager.getNextId(entitaCopia[i]));
                impl.setOriginalValue("ID",null);
                impl.setValue("ID",idNuovoOrdine);
                impl.getColumn("NSO_ORDINI.ID").setChiave(true);
                impl.setValue("ID_PADRE", idOrdineOrigine);
                impl.setValue("SYSCON", syscon);
                impl.setValue("STATO_ORDINE", new Long(1));
                if(geneManager.isCodificaAutomatica("NSO_ORDINI", "CODORD")){
                  String newCodord = geneManager.calcolaCodificaAutomatica("NSO_ORDINI", "CODORD");
                  impl.setValue("NSO_ORDINI.CODORD", newCodord);
                } else {
                  throw new GestoreException(
                      "Errore nella verifica dell'unicita' del codice dell'ordine",
                      "verificaOrdini", null);
                }
              }else{
                impl.setValue("ID",new Long(genChiaviManager.getNextId(entitaCopia[i])));
                impl.setValue("NSO_ORDINI_ID", idNuovoOrdine);
              }
              impl.insert(entitaCopia[i], this.geneManager.getSql());
            }
          }
        } catch (SQLException e) {
          throw new GestoreException("Errore nella copia dell'ordine per entità " + entitaCopia[i], "variazioneOrdine", e);
        }
      }
    }//for entita di copia
    //aggiorno la riga origine e le dipendenze

    if(idOrdineOrigine!=null && idOriginarioOrdine != null){
      try {
        this.geneManager.getSql().update(
            "update nso_ordini set versione= versione+1 where id_originario = ? and id <> ?",
            new Object[] { idOriginarioOrdine,idNuovoOrdine });
        this.geneManager.getSql().update(
            "update nso_ordini set is_revisione = ? where  id = ?",
            new Object[] { "1",idOrdineOrigine });
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'aggiornamento dello stato dell'ordine ", "variazioneOrdine", e);
      }

    }

    return idNuovoOrdine;
  }

  public void variazStatoOrdineToInviato(Long idOrdine) throws GestoreException {
    try {
      this.geneManager.getSql().update(
          "update nso_ordini set stato_ordine = 4 where  id = ?",
          new Object[] { idOrdine });
      this.geneManager.getSql().update(
          "update nso_ordini set is_revisione = ?, stato_ordine = ? where id = (select id_padre from nso_ordini where id = ?)",
          new Object[] { "2",new Long(3),idOrdine });

    } catch (SQLException e) {
      throw new GestoreException("Errore nell'aggiornamento dello stato dell'ordine ", "variazioneOrdine", e);
    }
  }

  public byte[] getFileXmlFromNsoWsOrdiniByFileName(String fileName) throws GestoreException {
    try {
      Object obj = this.geneManager.getSql().getObject("select XML_FILE from nso_ws_ordini where nome_file=?", new Object[] { fileName });
      logger.info("CLass: "+obj.getClass().getCanonicalName());
      return obj.getClass().getCanonicalName().getBytes();
    } catch (SQLException e) {
      throw new GestoreException("Errore nel download dell'ordine.", "variazioneOrdine", e);
    }
  }


  public List<HashMap<String, Object>> getListaLavorazioniNso(String codiceGara,String numeroGara,String codiceDitta,String incluseConsumate) throws GestoreException {

    List<HashMap<String, Object>> hMapLavNso = new ArrayList<HashMap<String, Object>>();
    HashMap<String, Object> hMapConsumedQuantity = new HashMap<String, Object>();

    try {
      List listaQuantitaConsumate = this.geneManager.getSql().getListVector(
      "select l.codice,sum(coalesce(l.quantita,0)) from nso_ordini o, nso_linee_ordini l" +
      " where o.id = l.nso_ordini_id and o.ngara = ? and (o.stato_ordine <> ? and o.stato_ordine <> ?)" +
      " group by l.codice", new Object[] { numeroGara, new Long(3), new Long(7) });


      if (listaQuantitaConsumate != null && listaQuantitaConsumate.size() > 0) {
        for (int k = 0; k < listaQuantitaConsumate.size(); k++) {
          Vector vectQuantitaConsumate = (Vector) listaQuantitaConsumate.get(k);
          String codice = ((JdbcParametro) vectQuantitaConsumate.get(0)).getStringValue();
          Double quantita = null;
          Object quantieffObj = ((JdbcParametro) vectQuantitaConsumate.get(1)).getValue();
          if (quantieffObj instanceof Long){
            quantita = ((Long) quantieffObj).doubleValue();
          }else{
            if(quantieffObj instanceof Double){
              quantita = (Double) quantieffObj;
            }
          }

          hMapConsumedQuantity.put(codice, quantita);

        }

      }

      List listaProdottiAggiudicataria  = this.geneManager.getSql().getListVector(
          "select v.CODVOC,v.VOCE,v.UNIMISEFF,v.QUANTIEFF,v.PREOFF,v.CONTAF,g.DESEST" +
          " from V_GCAP_DPRE v" +
          " join GCAP_EST g on v.ngara=g.ngara and v.contaf=g.contaf" +
          " where v.CODGAR = ? and v.NGARA= ? and v.COD_DITTA = ? ", new Object[] { codiceGara,numeroGara,codiceDitta });
      if (listaProdottiAggiudicataria != null && listaProdottiAggiudicataria.size() > 0) {
        for (int k = 0; k < listaProdottiAggiudicataria.size(); k++) {
          HashMap<String, Object> hMap = new HashMap<String, Object>();
          Vector vectProdottiAgg = (Vector) listaProdottiAggiudicataria.get(k);
          String codvoc = ((JdbcParametro) vectProdottiAgg.get(0)).getStringValue();
          String voce = ((JdbcParametro) vectProdottiAgg.get(1)).getStringValue();
          String unimiseff = ((JdbcParametro) vectProdottiAgg.get(2)).getStringValue();
          Double quantieff = null;
          Object quantieffObj = ((JdbcParametro) vectProdottiAgg.get(3)).getValue();
          if (quantieffObj instanceof Long){
            quantieff = ((Long) quantieffObj).doubleValue();
          }else{
            if(quantieffObj instanceof Double){
              quantieff = (Double) quantieffObj;
            }
          }
          Double preoff = null;
          Object preoffObj = ((JdbcParametro) vectProdottiAgg.get(4)).getValue();
          if (preoffObj instanceof Long){
            preoff = ((Long) preoffObj).doubleValue();
          }else{
            if(preoffObj instanceof Double){
              preoff = (Double) preoffObj;
            }
          }

          Double prezzoUnitario = (Double) UtilityNumeri.arrotondaNumero(preoff, new Integer(5));

          Long contaf = (Long) ((JdbcParametro) vectProdottiAgg.get(5)).getValue();
          String descrizioneEstesa = ((JdbcParametro) vectProdottiAgg.get(6)).getStringValue();

          //verifico se la lavorazione risulta consumata (introduco anche una tolleranza --Math.abs?)

          Double qtaConsumata = null;

            qtaConsumata = (Double) hMapConsumedQuantity.get(codvoc);


          Double tolleranza = new Double(0.001);
          if("1".equals(incluseConsumate)){
            if(quantieff != null && qtaConsumata == null || (qtaConsumata!= null )){
              if(qtaConsumata == null){
                qtaConsumata = new Double(0);
              }
              Double qtaDisponibile = quantieff-qtaConsumata;
              if(qtaDisponibile < new Double(0)){
                qtaDisponibile = null;
              }else{
                qtaDisponibile = (Double) UtilityNumeri.arrotondaNumero(qtaDisponibile, new Integer(5));
              }

              hMap.put("voce", codvoc);
              hMap.put("descrizione",voce);
              hMap.put("um",unimiseff);
              hMap.put("quantita",qtaDisponibile);
              hMap.put("prezzoUnitario",prezzoUnitario);
              hMap.put("contaf",contaf);
              hMap.put("descrizioneEstesa",descrizioneEstesa);
              hMap.put("checkRda", null);
              hMapLavNso.add(hMap);
            }

          }else{
            if(quantieff != null && qtaConsumata == null || (qtaConsumata!= null && (quantieff-qtaConsumata > tolleranza) )){
              if(qtaConsumata == null){
                qtaConsumata = new Double(0);
              }
              Double qtaDisponibile = quantieff-qtaConsumata;
              qtaDisponibile = (Double) UtilityNumeri.arrotondaNumero(qtaDisponibile, new Integer(5));
              hMap.put("voce", codvoc);
              hMap.put("descrizione",voce);
              hMap.put("um",unimiseff);
              hMap.put("quantita",qtaDisponibile);
              hMap.put("prezzoUnitario",prezzoUnitario);
              hMap.put("contaf",contaf);
              hMap.put("descrizioneEstesa",descrizioneEstesa);
              hMap.put("checkRda", null);
              hMapLavNso.add(hMap);
            }

          }

        }//for

      }//if prodotti aggiudicataria

    } catch (SQLException e) {
      throw new GestoreException("Errore nella selezione delle lavorazioni della gara associata!", "nsoLavOrdine", e);
    }

    return hMapLavNso;
  }

  public boolean getPresenzaLavForn(String codiceGara,String numeroGara,String codiceDitta) throws GestoreException {

    boolean verify = false;
    try {
      Long countLavForn = (Long) this.geneManager.getSql().getObject("select count(*)" +
      		" from v_gcap_dpre where codgar = ? and ngara = ? and cod_ditta =? ",
      		new Object[] { codiceGara,numeroGara,codiceDitta });

      if(new Long(0)<countLavForn){
        verify = true;
      }

      return verify;

    } catch (SQLException e) {
      throw new GestoreException("Errore nell'aggiornamento dello stato dell'ordine ", "variazioneOrdine", e);
    }
  }


  public int variazioneLineeOrdine(HttpServletRequest request, String arrmultikey , Long idOrdine, String numeroGara, String codiceDitta, String uffint)
    throws GestoreException {

  logger.debug("variazioneLineeOrdine: inizio metodo");

  String selectQuantitaConsumata = "select sum(coalesce(l.quantita,0))" +
  " from nso_ordini o, nso_linee_ordini l" +
  " where o.id = l.nso_ordini_id and o.ngara = ? and l.codice = ? and (o.stato_ordine <> ? and o.stato_ordine <> ?) and versione = ?" +
  " group by l.codice";

  String codiga= null;
  String centroCosto = null;
  String notega= null;
  String cpv = null;


  try {

    if(!"".equals(arrmultikey)){
      String[] ContafVect = arrmultikey.split(";");
      String inClause = arrmultikey.replace(";", ",");
      inClause = inClause.substring(0, inClause.length()-1);

      //recupero i dati di gara/lotto
      Vector<?> datiGaraLottoOrdine = this.geneManager.getSql().getVector("select g.codiga,g.not_gar,g.iaggiu,g.notega,c.codcpv,o.centro_costo" +
          " from gare g" +
          " LEFT JOIN garcpv c on g.ngara = c.ngara and c.numcpv=1" +
          " LEFT JOIN nso_ordini o on g.ngara = o.ngara" +
          " where  g.ngara = ? ", new Object[]{numeroGara});

      if(datiGaraLottoOrdine!=null && datiGaraLottoOrdine.size()>0){
        codiga =  SqlManager.getValueFromVectorParam(datiGaraLottoOrdine, 0).getStringValue();
        codiga = UtilityStringhe.convertiNullInStringaVuota(codiga);
        if("".equals(codiga)){
          codiga="1";
        }
        notega =  SqlManager.getValueFromVectorParam(datiGaraLottoOrdine, 3).getStringValue();
        cpv =  SqlManager.getValueFromVectorParam(datiGaraLottoOrdine, 4).getStringValue();
        centroCosto =  SqlManager.getValueFromVectorParam(datiGaraLottoOrdine, 5).getStringValue();
      }
      //recupero il contatore di linea
      Long maxIdLinea = (Long) this.geneManager.getSql().getObject("select max(id_linea) from nso_linee_ordini where nso_ordini_id = ?", new Object[]{idOrdine});

      List listaProdottiAggiudicataria  = this.geneManager.getSql().getListVector(
          "select CODGAR,NGARA,CODVOC,VOCE,UNIMISEFF,QUANTIEFF,PREOFF" +
          " from V_GCAP_DPRE where NGARA= ? and COD_DITTA = ? and CONTAF in("+ inClause + ")",
          new Object[] { numeroGara,codiceDitta });
      if (listaProdottiAggiudicataria != null && listaProdottiAggiudicataria.size() > 0) {
        for (int k = 0; k < listaProdottiAggiudicataria.size(); k++) {
          Vector vectProdottiAgg = (Vector) listaProdottiAggiudicataria.get(k);
          String codvoc = ((JdbcParametro) vectProdottiAgg.get(2)).getStringValue();
          String voce = ((JdbcParametro) vectProdottiAgg.get(3)).getStringValue();
          String unimiseff = ((JdbcParametro) vectProdottiAgg.get(4)).getStringValue();
          Double quantieff = null;
          Object quantieffObj = ((JdbcParametro) vectProdottiAgg.get(5)).getValue();
          if(quantieffObj != null){
            if (quantieffObj instanceof Long){
              quantieff = ((Long) quantieffObj).doubleValue();
            }else{
              if(quantieffObj instanceof Double){
                quantieff = (Double) quantieffObj;
              }
            }
          }else{
           // break;
          }

          //verifico la consumazione attuale:
          Double qtaDisponibile = null;
          Double qtaConsumata = (Double) this.geneManager.getSql().getObject(selectQuantitaConsumata,new Object[] { numeroGara,codvoc,new Long(3),new Long(7),new Long(0)});
          if(quantieff != null && qtaConsumata != null){
            qtaDisponibile = quantieff - qtaConsumata;
          }else{
            qtaDisponibile = quantieff;
          }

          Double preoff = null;
          Object preoffObj = ((JdbcParametro) vectProdottiAgg.get(6)).getValue();
          if(preoffObj != null){
            if (preoffObj instanceof Long){
              preoff = ((Long) preoffObj).doubleValue();
            }else{
              if(preoffObj instanceof Double){
                preoff = (Double) preoffObj;
              }
            }
          }else{
            //break;
          }

          // inserisco in ordini (linee)

          this.geneManager.getSql().update("insert into nso_linee_ordini(id, nso_ordini_id, id_linea, codice, descrizione, quantita, unimis, prezzo_unitario, centro_costo, note, codcpv, codein_rich)" +
              " values(?,?,?,?,?,?,?,?,?,?,?,?)",  new Object[] {new Long(genChiaviManager.getNextId("NSO_LINEE_ORDINI")),
              idOrdine, (maxIdLinea+k+1), codvoc, voce, qtaDisponibile, unimiseff,preoff,centroCosto,notega,cpv,uffint});
        }

      }//if prodotti aggiudicataria
    }


  } catch (SQLException e) {
    throw new GestoreException("Errore nella variazioni delle linee ordine dalla gara (" + numeroGara +")", null, e);
  }

  if (logger.isDebugEnabled()) logger.debug("variazioneLineeOrdine: fine metodo");

  return 0;
}





}
