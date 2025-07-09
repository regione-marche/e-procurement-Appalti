package it.eldasoft.sil.pg.bl;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.*;


/**
 * @author manuel.bridda
 *
 */
public class GestioneFVOEManager {

  /** Logger */
  static Logger logger = Logger.getLogger(GestioneFVOEManager.class);

  private SqlManager          sqlManager;
  private GenChiaviManager    genChiaviManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  /**
   * @param codgar
   * @return un oggetto JSON con i record di DITG aggiungibili alla G1FVOE
   * @throws SQLException
   */
  public JSONObject recuperaDitte(String codgar,boolean soloNonInserite) throws SQLException {

    JSONObject resp =  new JSONObject();
    JSONArray array =  new JSONArray();
    boolean ok = true;
    String nomeSchedaAttiva = (String) sqlManager.getObject("select nome_scheda from g1npascheda, g1npaform, g1cf_npa where g1cf_npa.id=g1npaform.idcfnpa and g1npaform.id=g1npascheda.idform " +
            "and idschedanpa in (select idappaltonpa from torn where codgar=?)", new Object[]{codgar});
    String select = "select distinct dittao, nomimo from ditg  where codgar5=? and (fasgar is null or fasgar > 1) and invoff = '1' and idpartecipnpa is not null";
    if(soloNonInserite)
      select+= " and dittao not in (select dittao from g1fvoe where codgar=codgar5) ";

    //se l'appalto nasce da un affidamento diretto, filtro sull'aggiudicatario
    if(nomeSchedaAttiva!=null && (nomeSchedaAttiva.indexOf("AD")==0 || nomeSchedaAttiva.indexOf("A3_6")==0))
      select+=" and (dittao in (select ditta from gare where ngara=ngara5) or dittao in (select dittao from ditgaq where ngara=ngara5))";

    List<?> datiDitte = sqlManager.getListVector(select, new Object[]{codgar});
    if (datiDitte != null && datiDitte.size() > 0) {
      for (int l = 0; l < datiDitte.size(); l++) {
        JSONObject item = new JSONObject();
        try {
          String dittao = (String) SqlManager.getValueFromVectorParam(datiDitte.get(l), 0).getValue();
          String nomimo = (String) SqlManager.getValueFromVectorParam(datiDitte.get(l), 1).getValue();
          item.put("dittao", dittao);
          item.put("nomimo", nomimo);
          array.add(item);
        }catch(Exception e) {
          logger.error(e);
          ok=false;
        }
      }
    }else {
      ok=false;
    }

    resp.put("esito", ok);
    resp.put("listaDitte",array);

    return resp;
  }

  /**
   * @param codgar
   * @param arrmultikey
   * @return l'esito dell'operazione
   * @throws SQLException
   */
  public JSONObject aggiungiDitte(String codgar, String arrmultikey) throws SQLException {

    JSONObject resp = new JSONObject();
    String esito = "KO";
    arrmultikey = UtilityStringhe.convertiNullInStringaVuota(arrmultikey);
    if(!"".equals(arrmultikey)){
      String[] multikey = arrmultikey.split(";");

      for (int k = 0; k < multikey.length; k++) {
        String dittao = multikey[k];
        try {
          List<Vector> datiDitta = sqlManager.getListVector("select distinct dittao, tipimp from ditg, impr where dittao=codimp and dittao=? and codgar5=? and idpartecipnpa is not null", new Object[]{dittao, codgar});
          if (datiDitta != null && !datiDitta.isEmpty()) {
            for (int i = 0; i < datiDitta.size(); i++) {
              String dittaoOriginale = (String) SqlManager.getValueFromVectorParam(datiDitta.get(i), 0).getValue();
              Long tipimp = (Long) SqlManager.getValueFromVectorParam(datiDitta.get(i), 1).getValue();
              if (tipimp != null && (tipimp == 3 || tipimp == 10)) {
                List<Vector> datiRagimp = sqlManager.getListVector("select coddic, impman from ragimp where codime9 = ?", new Object[]{dittaoOriginale});
                for (int x = 0; x < datiRagimp.size(); x++) {
                  String codimp = (String) SqlManager.getValueFromVectorParam(datiRagimp.get(x), 0).getValue();
                  String impMan = (String) SqlManager.getValueFromVectorParam(datiRagimp.get(x), 1).getValue();
                  if ("1".equals(impMan))
                    aggiungiDitta(dittaoOriginale, codgar, codimp, 1L);
                  else
                    aggiungiDitta(dittaoOriginale, codgar, codimp, 2L);
                }
              } else if (tipimp != null && (tipimp == 2 || tipimp == 11)) {
                aggiungiDitta(dittaoOriginale, codgar, dittao, 7L);
                List<Vector> datiRagdet = sqlManager.getListVector("select coddic from ragdet where codimp = ? and ngara in (select ngara from gare where codgar1=?)",
                        new Object[]{dittao, codgar});
                if (datiRagdet != null && !datiRagdet.isEmpty()) {
                  for (int x = 0; x < datiRagdet.size(); x++) {
                    String codimp = (String) SqlManager.getValueFromVectorParam(datiRagdet.get(x), 0).getValue();
                    aggiungiDitta(dittaoOriginale, codgar, codimp, 11L);
                  }
                }
              } else
                aggiungiDitta(dittaoOriginale, codgar, dittao, 3L);

              List<Vector> datiAvvalim = sqlManager.getListVector("select dittaav from ditgavval where dittao=? and ngara in (select ngara from gare where codgar1=?)", new Object[]{dittao, codgar});
              if (datiAvvalim != null && !datiAvvalim.isEmpty()) {
                for (int x = 0; x < datiAvvalim.size(); x++) {
                  String codimp = (String) SqlManager.getValueFromVectorParam(datiAvvalim.get(x), 0).getValue();
                  aggiungiDitta(dittaoOriginale, codgar, codimp, 4L);
                }
              }
            }
          }
          esito="OK";
        }catch(Exception e) {
          logger.error(e);
          resp.put("messaggio", e);
        }
      }
    }else{
      resp.put("messaggio", "Nessun elemento selezionato");
    }

    resp.put("esito", esito);

    return resp;
  }

  private void aggiungiDitta(String dittao, String codgar, String codimp, Long ruolo) throws SQLException {
    Long countOccorrenzaDittao = (Long) sqlManager.getObject("select count(1) from g1fvoe where dittao=? and codimp=? and codgar=?", new Object[]{dittao, codimp, codgar});
    if (countOccorrenzaDittao == 0) {//APPALTI-2142 aggiungo solo le occorrenze non ancora inserite. Questo per supportare il caso di import di una ditta già presente in cui vengono modificati/aggiunti partecipanti all'offerta (mandanti, esecutrici, ditte in avvalimento).
      String isAttivo = "1";
      Long countOccorrenze = (Long) sqlManager.getObject("select count(1) from g1fvoe where codimp=? and codgar=?", new Object[]{codimp, codgar});
      if (countOccorrenze > 0)
        isAttivo = "2";
      int id = genChiaviManager.getNextId("G1FVOE");
      sqlManager.update("insert into g1fvoe (id,dittao, codgar, codimp, isattivo, ruolo) values (?,?,?,?,?,?)",
              new Object[]{(long) id, dittao, codgar, codimp, isAttivo, ruolo});
    }
  }
}



