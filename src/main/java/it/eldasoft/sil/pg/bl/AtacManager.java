/*
 * Created on 21/02/2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Classe di gestione delle funzionalita' per ATAC
 *
 * @author Cristian.Febas
 */
public class AtacManager {

  /** Logger */
  static Logger logger = Logger.getLogger(AtacManager.class);

  /** Manager SQL per le operazioni su database */
  private SqlManager sqlManager;

  private PgManager pgManager;

  /**
   * Set SqlManager
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setPgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }

  private static final String SQL_LAVORAZIONI_OFFERTE = "select count (distinct CONTAF) from V_GCAP_DPRE where  ngara=? and PREOFF is not null and isprodneg is null";
  private static final String SQL_LAVORAZIONI_OFFERTE_AGGIUDICATE = SQL_LAVORAZIONI_OFFERTE + " and QTAORDINATA is not null";
  private static final String SQL_VALUTAZIONE_PRODOTTI = "select count(*) from V_GARE_PRODOTTI_VALUTATI where ngara=?";
  private static final String SQL_VALUTAZIONE_PRODOTTI_AFFIDAMENTI = SQL_VALUTAZIONE_PRODOTTI + " and affidamento is not null";
  private static final String UPDATE_STATO_RICERCA =  "update gare set ISRICONCLUSA=?, DATCONCLUSA=?  where ngara=?";

  public int insAffidamentoValutazioneProdotti(Long idUtente, String seguen,String dittao, String uffint) throws GestoreException{

    String insertGara = "insert into gare(ngara, codgar1, codcig, tipgarg, not_gar, ditta, nomima, iaggiu, ribagg, dattoa, impapp, seguen, oggcont, prosla, loclav, locint, numssl, nomssl)" +
        " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String insertGare1 = "insert into gare1(ngara, codgar1, ammrin, ammopz, codcnel, impsomme, motcoll, esecris) values(?,?,?,?,?,?,?,?)";
    String insertTorn = "insert into torn(codgar, cenint, tipgen, offaum, compreq, istaut, iterga, cliv1, settore, codrup, vernpa, ispnrr, motcig, isnodpcm24, isnoinizia, strumproc, ordiele, orgricorso)" +
        " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    String insertGarecont = "insert into garecont(ngara, ncont, dverbc, dcertu, impliq, codimp) values(?,?,?,?,?,?)";

    String insertEdit ="insert into edit (codgar4,codime,nomime,docok,datok,ditinv,nprogt) values(?,?,?,?,?,?,?)";

    String insertDitg = "insert into ditg (ngara5,dittao,codgar5,nomimo,nprogg,catimok,invgar,numordpl,invoff,impappd,impoff,ncomope,codcnel,ribauo) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    String insertG_permessi = "insert into g_permessi(numper, syscon, autori, propri, codgar) values(?,?,?,?,?)";

    //ATTENZIONE in ATAC DEVE ESSERCI LA CODIFICA AUTOMATICA
    HashMap hm = pgManager.calcolaCodificaAutomatica("GARE", Boolean.TRUE, null,null);
    String ngara =  (String) hm.get("numeroGara");
    String codgar = "$" + ngara;
    String codgarPadre = "$" + seguen;
    String codcig = null;
    Long tipgarg = null;
    Long iterga = null;
    Long tipgen = null;
    String offaum = null;
    String compreq = null;
    String codrup = null;
    String valoreA1115 = null;
    String valoreA1191 = null;

    Long oggcont = null;
    Long motcig = null;
    String isnodpcm24 = null;
    String isnoinizia = null;
    Long strumproc = null;
    String prosla = null;
    String loclav = null;
    String locint = null;
    String numssl = null;
    String nomssl = null;
    String codcnel = null;
    Long orgricorso = null;
    try {

      String datiGarePadreSql = "select t.tipgen, t.offaum, t.compreq, t.codrup, "
          + "g.oggcont, t.motcig, t.isnodpcm24, t.isnoinizia, t.strumproc, "
          + "g.prosla, g.loclav, g.locint, g.numssl, g.nomssl, g1.codcnel, t.orgricorso "
          + "from torn t "
          + "inner join gare g on t.codgar = g.codgar1 "
          + "inner join gare1 g1 on g1.ngara = g.ngara and g1.codgar1 = g.codgar1 "
          + "where g.ngara = ? ";
      Vector<?> datiGaraPadre = this.sqlManager.getVector(datiGarePadreSql, new Object[]{seguen});
      if(datiGaraPadre!=null && !datiGaraPadre.isEmpty()){
        tipgen = (Long) SqlManager.getValueFromVectorParam(datiGaraPadre, 0).getValue();
        offaum = (String) SqlManager.getValueFromVectorParam(datiGaraPadre, 1).getValue();
        compreq = (String) SqlManager.getValueFromVectorParam(datiGaraPadre, 2).getValue();
        codrup = (String) SqlManager.getValueFromVectorParam(datiGaraPadre, 3).getValue();
        oggcont = (Long) SqlManager.getValueFromVectorParam(datiGaraPadre, 4).getValue();
        motcig = (Long) SqlManager.getValueFromVectorParam(datiGaraPadre, 5).getValue();
        isnodpcm24 = (String) SqlManager.getValueFromVectorParam(datiGaraPadre, 6).getValue();
        isnoinizia = (String) SqlManager.getValueFromVectorParam(datiGaraPadre, 7).getValue();
        strumproc = (Long) SqlManager.getValueFromVectorParam(datiGaraPadre, 8).getValue();
        prosla = (String) SqlManager.getValueFromVectorParam(datiGaraPadre, 9).getValue();
        loclav = (String) SqlManager.getValueFromVectorParam(datiGaraPadre, 10).getValue();
        locint = (String) SqlManager.getValueFromVectorParam(datiGaraPadre, 11).getValue();
        numssl = (String) SqlManager.getValueFromVectorParam(datiGaraPadre, 12).getValue();
        nomssl = (String) SqlManager.getValueFromVectorParam(datiGaraPadre, 13).getValue();
        codcnel = (String) SqlManager.getValueFromVectorParam(datiGaraPadre, 14).getValue();
        orgricorso = (Long) SqlManager.getValueFromVectorParam(datiGaraPadre, 15).getValue();
      }

      //TIPO PROCEDURA
      valoreA1191 = (String) this.sqlManager.getObject(
          "select tab1desc from tab1 where tab1cod='A1191' and tab1tip= ?", new Object[]{Long.valueOf(1)});
      valoreA1191 = UtilityStringhe.convertiNullInStringaVuota(valoreA1191);
      String tipoproc = null;
      if(!"".equals(valoreA1191)){
        tipoproc = valoreA1191.substring(0,2) ;
      }
      if(tipoproc != null){
        tipgarg = Long.valueOf(tipoproc);
        iterga = this.pgManager.getITERGA(Long.valueOf(tipgarg));
      }
      //SETTORE
      valoreA1115 = (String) this.sqlManager.getObject(
          "select tab1desc from tab1 where tab1cod='A1115' and tab1tip= ?", new Object[]{new Long(6)});
      valoreA1115 = UtilityStringhe.convertiNullInStringaVuota(valoreA1115);
      String settore = null;
      if(!"".equals(valoreA1115)){
        settore = valoreA1115.substring(0,1) ;
      }
      String elencoLavorazioni="Elenco voci:"+"\r\n";
      String composizioneOggetto="Affidamento derivante da ricerca di mercato."+"\r\n";

      String codiceAppalto = (String) this.sqlManager.getObject("select distinct(codcarr) from gcap where gcap.ngara=?", new Object[] {seguen});
      codiceAppalto =UtilityStringhe.convertiNullInStringaVuota(codiceAppalto);
      if(!"".equals(codiceAppalto)) {
        composizioneOggetto="Riferimento appalto: "+codiceAppalto+"\r\n";
        elencoLavorazioni="Elenco codici:"+"\r\n";
      }


      List<String> vociList = this.sqlManager.getListVector("select gcap.codvoc,gcap.voce from gcap,dpre"
          + " where gcap.ngara= dpre.ngara and gcap.contaf=dpre.contaf"
          + " and dpre.ngara=? and dpre.dittao=? and dpre.qtaordinata is not null",
          new Object[] {seguen,dittao});
      //String descrVoci= "";


      if (vociList != null && vociList.size() > 0) {
        for (int i = 0; i < vociList.size(); i++) {
          String codvoc = (String) SqlManager.getValueFromVectorParam(vociList.get(i), 0).getValue();
          codvoc =UtilityStringhe.convertiNullInStringaVuota(codvoc);
          //String voce = (String) SqlManager.getValueFromVectorParam(vociList.get(i), 1).getValue();
          //voce =UtilityStringhe.convertiNullInStringaVuota(voce);
          if(!"".equals(codvoc)) {
            elencoLavorazioni+=" - " + codvoc+"\r\n";
          }
          /*
        		 if(!"".equals(voce)) {
            		 if(i!=0) {
            			 descrVoci +=" - ";
            		 }
            		 descrVoci +=voce;
        		 }
           */
        }//end for
        composizioneOggetto+=elencoLavorazioni;
      }

      Double importoAggiudicazione = null;

      Object importo = this.sqlManager.getObject( "select imptot from v_gare_prodotti_valutati"
          + " where ngara=? and dittao = ? ", new Object[] {seguen,dittao});
      
      if (importo instanceof Number) {
        importoAggiudicazione = ((Number) importo).doubleValue();
      }

      Date dataInizio = null;
      Date dataUltimazione = null;

      String nomimo = (String) this.sqlManager.getObject( "select nomimp from impr where codimp = ? ", new Object[] {dittao});
      Double impLiquidato = null;
      Double ribassoAggiudicazione = Double.valueOf(0);

		String vernpa = pgManager.getVersioneNpaCorrente();

      this.sqlManager.update(insertGara,
          new Object[] { ngara, codgar, codcig, tipgarg, composizioneOggetto, dittao, nomimo, importoAggiudicazione, ribassoAggiudicazione, dataInizio, importoAggiudicazione, seguen, oggcont, prosla, loclav, locint, numssl, nomssl});

      this.sqlManager.update(insertGare1, new Object[] {ngara,codgar,"2","2",codcnel,Double.valueOf(0),Long.valueOf(10),"2"});
      Long cliv1 = null;

      this.sqlManager.update(insertTorn, new Object[] {codgar,uffint,tipgen,offaum,compreq,null,iterga,cliv1,settore,codrup,vernpa,"2", motcig, isnodpcm24, isnoinizia, strumproc,"2",orgricorso});

      this.sqlManager.update(insertGarecont, new Object[] {ngara, Long.valueOf(1), dataInizio, dataUltimazione, impLiquidato, dittao});

      //DITTE
      Long nProgressivo= new Long(0);

      List<String> ditteGaraPadreList = this.sqlManager.getListVector("select dittao, nomimo, ditg.codcnel, impr.codcnel  from ditg join impr on codimp=dittao where ngara5=? and invoff=? and dittao<>?",
          new Object[] {seguen,"1",dittao});
      for (int k = 0; k < ditteGaraPadreList.size(); k++) {
        String dittao_k = (String) SqlManager.getValueFromVectorParam(ditteGaraPadreList.get(k), 0).getValue();
        String nomimo_k = (String) SqlManager.getValueFromVectorParam(ditteGaraPadreList.get(k), 1).getValue();
        String codcnel_k = (String) SqlManager.getValueFromVectorParam(ditteGaraPadreList.get(k), 2).getValue();
        if(StringUtils.isBlank(codcnel_k)) {
          codcnel_k = (String) SqlManager.getValueFromVectorParam(ditteGaraPadreList.get(k), 3).getValue();
        }
        //inserimento in EDIT
        nProgressivo = Long.valueOf(nProgressivo.longValue() +1);
        sqlManager.update(insertEdit,new Object[]{codgar,dittao_k,nomimo_k,"1","1","1",nProgressivo});
        sqlManager.update(insertDitg,new Object[]{ngara,dittao_k,codgar,nomimo_k,nProgressivo,"1","1",nProgressivo,"1",null,Double.valueOf(0),"1",codcnel_k,null});
      }
      //viene valorizzato l'importo offerto pari all'aggiudicato su ditg per quella aggiudicataria
      Vector datiAggiudicataria = this.sqlManager.getVector("select ditg.codcnel, impr.codcnel  from ditg join impr on codimp=dittao where ngara5 = ? and invoff = ? and dittao = ?", new Object[]{seguen,"1",dittao});
      String codcnelAggiudicataria = (String) SqlManager.getValueFromVectorParam(datiAggiudicataria, 0).getValue();
      if(StringUtils.isBlank(codcnelAggiudicataria)) {
        codcnelAggiudicataria = (String) SqlManager.getValueFromVectorParam(datiAggiudicataria, 1).getValue();
      }
      nProgressivo = Long.valueOf(nProgressivo.longValue() +1);
      sqlManager.update(insertEdit,new Object[]{codgar,dittao,nomimo,"1","1","1",nProgressivo});
      sqlManager.update(insertDitg,new Object[]{ngara,dittao,codgar,nomimo,nProgressivo,"1","1",nProgressivo,"1",null,importoAggiudicazione,"1",codcnelAggiudicataria,Double.valueOf(0)});

      String selectG_PERMESSI = "select syscon,autori,propri from g_permessi where codgar = ? union select "+idUtente +",1,'1' from g_permessi ";
      List<?> datiG_PERMESSI = this.sqlManager.getListVector(selectG_PERMESSI, new Object[] { codgarPadre });

      for (int p = 0; p < datiG_PERMESSI.size(); p++) {
        Long utente  = (Long) SqlManager.getValueFromVectorParam(datiG_PERMESSI.get(p), 0).getValue();
        Long autori  = (Long) SqlManager.getValueFromVectorParam(datiG_PERMESSI.get(p), 1).getValue();
        String propri  = (String) SqlManager.getValueFromVectorParam(datiG_PERMESSI.get(p), 2).getValue();
        // si inserisce l'utente solo se non esiste l'associazione nella G_PERMESSI con l'entita'
        Vector ret = this.sqlManager.getVector(
            "select count(numper) from g_permessi where codgar"
                + " = ? and syscon = ?", new Object[] { codgar, utente });
        if (ret.size() > 0) {
          Long count = SqlManager.getValueFromVectorParam(ret, 0).longValue();
          if (count != null && count.longValue() == 0) {
            // non esiste, quindi tento l'inserimento
            long maxNumper = this.getMaxIdGPermessi() + 1;
            this.sqlManager.update(insertG_permessi, new Object[] { Long.valueOf(maxNumper), utente, autori, propri, codgar });
          }
        }
      }

      //Va richiamata la nuova funzione che serve per l'inserimento/copia della lista lavorazioni forniture della gara padre
      inserimentoListaLavorazionieForniture(dittao, seguen, ngara);
      
      controllaStatoRicercaDiMercato(seguen);
      
    } catch (SQLException e) {
      logger.error("Errore durante la creazione dell'affidamento derivato: " + e.getMessage(), e );
      throw new GestoreException("Errore nella creazione dell'affidamento derivato " + codcig, null,e);
    }

    return 0;

  }
  
  public void controllaStatoRicercaDiMercato(String seguen) throws SQLException {
    //Impostazione dello stato concluso della ricerca di mercato
    String isRiConclusa=null;
    Timestamp dataIsRiconclusa = null;
    //Lavorazioni con offerte
    Long numOfferte = (Long)this.sqlManager.getObject(SQL_LAVORAZIONI_OFFERTE, new Object[] {seguen});

    //Lavorazioni con offerte e aggiudicate
    Long numOfferteAgg = (Long)this.sqlManager.getObject(SQL_LAVORAZIONI_OFFERTE_AGGIUDICATE, new Object[] {seguen});
    if(numOfferte!=null && numOfferteAgg!=null && numOfferte.longValue()==numOfferteAgg.longValue()) {
      //Valutazione prodotti
      Long numValProdotti = (Long)this.sqlManager.getObject(SQL_VALUTAZIONE_PRODOTTI, new Object[] {seguen});

      //Valutazione prodotti con affidamento
      Long numValProdottiAff = (Long)this.sqlManager.getObject(SQL_VALUTAZIONE_PRODOTTI_AFFIDAMENTI, new Object[] {seguen});

      if(numValProdotti!=null && numValProdottiAff!=null && numValProdotti.longValue()==numValProdottiAff.longValue()) {
        isRiConclusa="1";
        dataIsRiconclusa = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
      }
    }
    this.sqlManager.update(UPDATE_STATO_RICERCA, new Object[] { isRiConclusa, dataIsRiconclusa, seguen});
  }

  /**
   * Ritorna l'ultimo id generato per la tabella G_PERMESSI
   *
   * @return ultimo id generato, 0 altrimenti
   * @throws GestoreException
   */
  private long getMaxIdGPermessi() throws GestoreException {
    long id = 0;
    try {
      Vector ret = this.sqlManager.getVector(
          "select max(numper) from g_permessi", new Object[] {});
      if (ret.size() > 0) {
        Long count = SqlManager.getValueFromVectorParam(ret, 0).longValue();
        if (count != null && count.longValue() > 0) {
          id = count.longValue();
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'estrazione dell'ultimo id utilizzato nella G_PERMESSI",
          "getMaxIdPermessi", e);
    }
    return id;
  }

  private void inserimentoListaLavorazionieForniture(String dittao, String seguen, String ngara) throws SQLException, GestoreException {

    List<String> lista = this.sqlManager.getListVector(""
        + "SELECT G.CONTAFAQ,G.CODVOC,G.VOCE,G.CODCAT,D.CLASI1,"
        + "G.SOLSIC,G.SOGRIB,G.UNIMIS,G.QUANTI,D.PREOFF,D.PERCIVA,G.PESO, G.CODCARR, G.POSRDA, G.CODRDA, E.DESEST "
        + "FROM GCAP G "
        + "LEFT JOIN GCAP_EST E ON G.NGARA=E.NGARA AND G.CONTAF=E.CONTAF "
        + "JOIN DPRE D ON G.NGARA=D.NGARA AND G.CONTAF=D.CONTAF "
        + "WHERE D.QTAORDINATA IS NOT NULL "
        + "AND D.DITTAO = ? "
        + "AND G.NGARA = ?",
        new Object[] {dittao, seguen});


    if (lista != null && lista.size() > 0) {
      for (int i = 0; i < lista.size(); i++) {

        Long contaf = new Long(0);
        Long contafaq = SqlManager.getValueFromVectorParam(lista.get(i), 0).longValue();
        Double norvoc = new Double(0);
        String codvoc = (String) SqlManager.getValueFromVectorParam(lista.get(i), 1).getValue();
        codvoc = UtilityStringhe.convertiNullInStringaVuota(codvoc);
        String voce = (String) SqlManager.getValueFromVectorParam(lista.get(i), 2).getValue();
        voce = UtilityStringhe.convertiNullInStringaVuota(voce);
        String codcat = (String) SqlManager.getValueFromVectorParam(lista.get(i), 3).getValue();
        codcat = UtilityStringhe.convertiNullInStringaVuota(codcat);
        Long clasi1 = SqlManager.getValueFromVectorParam(lista.get(i), 4).longValue();
        String solsic = (String) SqlManager.getValueFromVectorParam(lista.get(i), 5).getValue();
        solsic = UtilityStringhe.convertiNullInStringaVuota(solsic);
        String sogrib = (String) SqlManager.getValueFromVectorParam(lista.get(i), 6).getValue();
        sogrib = UtilityStringhe.convertiNullInStringaVuota(sogrib);
        String unimis = (String) SqlManager.getValueFromVectorParam(lista.get(i), 7).getValue();
        unimis = UtilityStringhe.convertiNullInStringaVuota(unimis);
        Double quanti = SqlManager.getValueFromVectorParam(lista.get(i), 8).doubleValue();
        Double preoff = SqlManager.getValueFromVectorParam(lista.get(i), 9).doubleValue();
        Long perciva = SqlManager.getValueFromVectorParam(lista.get(i), 10).longValue();
        Double peso = SqlManager.getValueFromVectorParam(lista.get(i), 11).doubleValue();
        String codcarr = (String) SqlManager.getValueFromVectorParam(lista.get(i), 12).getValue();
        codcarr = UtilityStringhe.convertiNullInStringaVuota(codcarr);
        String posrda = (String) SqlManager.getValueFromVectorParam(lista.get(i), 13).getValue();
        posrda = UtilityStringhe.convertiNullInStringaVuota(posrda);
        String codrda = (String) SqlManager.getValueFromVectorParam(lista.get(i), 14).getValue();
        codrda = UtilityStringhe.convertiNullInStringaVuota(codrda);
        String desest = (String) SqlManager.getValueFromVectorParam(lista.get(i), 15).getValue();
        desest = UtilityStringhe.convertiNullInStringaVuota(desest);
        

        Long maxContaf = (Long)this.sqlManager.getObject("select max(contaf) from gcap where ngara=?",
            new Object[]{ngara});
        
        if(maxContaf==null) 
          contaf =  new Long(1);
        else
          contaf = new Long(maxContaf.longValue()+1);
        
        norvoc = Double.valueOf(contaf);

        String sqlInsertGcap = "INSERT INTO GCAP"
            + "(NGARA,CONTAF,CONTAFAQ,NORVOC,CODVOC,VOCE,CODCAT,CLASI1,SOLSIC,SOGRIB,UNIMIS,QUANTI,PREZUN,PERCIVA,PESO,CODCARR,POSRDA,CODRDA) "
            + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        sqlManager.update(sqlInsertGcap,new Object[]{ngara,contaf,contafaq,norvoc,codvoc,voce,codcat,clasi1,solsic,sogrib,unimis,quanti,preoff,perciva,peso,codcarr,posrda,codrda});

        String sqlInsertGcap_est = "INSERT INTO GCAP_EST(NGARA,CONTAF,DESEST) VALUES(?,?,?)";

        sqlManager.update(sqlInsertGcap_est,new Object[]{ngara,contaf,desest});


      }

    }
  }

}