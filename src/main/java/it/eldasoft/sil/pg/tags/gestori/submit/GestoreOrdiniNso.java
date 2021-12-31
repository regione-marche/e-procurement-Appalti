package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.web.struts.UploadMultiploForm;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;
import org.springframework.transaction.TransactionStatus;

public class GestoreOrdiniNso extends AbstractGestoreChiaveIDAutoincrementante {

  private static final Logger logger = Logger.getLogger(GestoreOrdiniNso.class);

  GenChiaviManager genChiaviManager = null;

  @Override
  public String getCampoNumericoChiave() {
    return "ID";
  }

  @Override
  public String getEntita() {
    return "NSO_ORDINI";
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    super.preInsert(status, datiForm);

    ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Long syscon = new Long(profilo.getId());

    //Imposto il codice dell'ordine in base a codifica automatica
    if(geneManager.isCodificaAutomatica("NSO_ORDINI", "CODORD")){
      String codord = geneManager.calcolaCodificaAutomatica("NSO_ORDINI", "CODORD");
      datiForm.setValue("NSO_ORDINI.CODORD", codord);
    } else {
      String codord = datiForm.getString("NSO_ORDINI.CODORD");
      try {
        List ret = this.sqlManager.getVector(
            "select 1 from nso_ordini where codord = ?",
            new Object[] { codord });
        if (ret != null && ret.size() > 0)
          throw new GestoreException(
              "Il codice dell'ordine risulta pre-esistente",
              "verificaOrdini.duplicato");
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella verifica dell'unicita' del codice dell'ordine",
            "verificaOrdini", e);
      }
    }

    Long idOrdine = datiForm.getLong("NSO_ORDINI.ID");
    datiForm.setValue("NSO_ORDINI.ID_ORIGINARIO", idOrdine);
    datiForm.setValue("NSO_ORDINI.SYSCON", syscon);
    datiForm.setValue("NSO_ORDINI.VERSIONE", new Long(0));
    datiForm.setValue("NSO_ORDINI.IS_REVISIONE", "2");

  }

  @Override
  public void afterInsertEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager", this.getServletContext(),
        GenChiaviManager.class);

    String dittaAggiudicataria = this.getRequest().getParameter("dittaAgg");
    dittaAggiudicataria = UtilityStringhe.convertiNullInStringaVuota(dittaAggiudicataria);

    Long idOrdine = datiForm.getLong("NSO_ORDINI.ID");

    HttpSession session = this.getRequest().getSession();
    String uffint = (String) session.getAttribute("uffint");

    if(!"".equals(dittaAggiudicataria)){

      try {

          //inserisco i dati del FORNITORE
          Vector<?> datiFornitore = this.sqlManager.getVector("select tipimp,codimp,nomimp,endpoint_nso,cfimp," +
                  "indimp,nciimp,locimp,capimp,nazimp,pivimp,natgiui from impr where codimp = ?", new Object[]{dittaAggiudicataria});

          if(datiFornitore!=null && datiFornitore.size()>0){
            String cf = null;
            Long tipimp =  (Long) SqlManager.getValueFromVectorParam(datiFornitore, 0).getValue();
            String codimp =  SqlManager.getValueFromVectorParam(datiFornitore, 1).getStringValue();
            String nomimp =  SqlManager.getValueFromVectorParam(datiFornitore, 2).getStringValue();
            String endpoint_nso =  SqlManager.getValueFromVectorParam(datiFornitore, 3).getStringValue();
            String cfimp =  SqlManager.getValueFromVectorParam(datiFornitore, 4).getStringValue();
            String indimp =  SqlManager.getValueFromVectorParam(datiFornitore, 5).getStringValue();
            String nciimp =  SqlManager.getValueFromVectorParam(datiFornitore, 6).getStringValue();
            String locimp =  SqlManager.getValueFromVectorParam(datiFornitore, 7).getStringValue();
            String capimp =  SqlManager.getValueFromVectorParam(datiFornitore, 8).getStringValue();
            Long nazimp =  (Long) SqlManager.getValueFromVectorParam(datiFornitore, 9).getValue();
            String pivimp =  SqlManager.getValueFromVectorParam(datiFornitore, 10).getStringValue();
            Long natgiui =  (Long) SqlManager.getValueFromVectorParam(datiFornitore, 11).getValue();
            if(new Long(10).equals(natgiui)){
              cf = cfimp;
            }else{
              cf = pivimp;
            }
            if(new Long(3).equals(tipimp)){
              String dittaMandataria =  (String) this.sqlManager.getObject("select CODDIC" +
              		" from RAGIMP,IMPR where CODIME9 = ? and CODDIC=CODIMP and IMPMAN='1' ", new Object[]{dittaAggiudicataria});
              datiFornitore = this.sqlManager.getVector("select tipimp,codimp,nomimp,endpoint_nso,cfimp," +
                  "indimp,nciimp,locimp,capimp,nazimp,pivimp,natgiui from impr where codimp = ?", new Object[]{dittaMandataria});
              tipimp =  (Long) SqlManager.getValueFromVectorParam(datiFornitore, 0).getValue();
              codimp =  SqlManager.getValueFromVectorParam(datiFornitore, 1).getStringValue();
              nomimp =  SqlManager.getValueFromVectorParam(datiFornitore, 2).getStringValue();
              endpoint_nso =  SqlManager.getValueFromVectorParam(datiFornitore, 3).getStringValue();
              cfimp =  SqlManager.getValueFromVectorParam(datiFornitore, 4).getStringValue();
              indimp =  SqlManager.getValueFromVectorParam(datiFornitore, 5).getStringValue();
              nciimp =  SqlManager.getValueFromVectorParam(datiFornitore, 6).getStringValue();
              locimp =  SqlManager.getValueFromVectorParam(datiFornitore, 7).getStringValue();
              capimp =  SqlManager.getValueFromVectorParam(datiFornitore, 8).getStringValue();
              nazimp =  (Long) SqlManager.getValueFromVectorParam(datiFornitore, 9).getValue();
              pivimp =  SqlManager.getValueFromVectorParam(datiFornitore, 10).getStringValue();
              natgiui =  (Long) SqlManager.getValueFromVectorParam(datiFornitore, 11).getValue();
              if(new Long(10).equals(natgiui)){
                cf = cfimp;
              }else{
                cf = pivimp;
              }
            }

            //rivisitare nazione
             this.sqlManager.update("insert into nso_fornitore(id,nso_ordini_id,codimp, nomimp, endpoint, cfimp," +
                  "via,citta,cap,codnaz) values(?,?,?,?,?,?,?,?,?,?)",
                new Object[] {new Long(genChiaviManager.getNextId("NSO_FORNITORE")), idOrdine,codimp,nomimp,endpoint_nso,cf,
                indimp+" "+nciimp,locimp,capimp,nazimp});
          }

          //Recupero i dati di uffint
          Vector<?>  datiUffint = this.sqlManager.getVector("select nomein,codcons_nso,viaein,nciein,citein,capein,codnaz," +
              "iscuc,endpoint_nso,notein,ivaein,codipa" +
              " from uffint where codein = ?", new Object[]{uffint});

          if(datiUffint!=null && datiUffint.size()>0){
            String nomein =  SqlManager.getValueFromVectorParam(datiUffint, 0).getStringValue();
            String puntocons =  SqlManager.getValueFromVectorParam(datiUffint, 1).getStringValue();
            String via =  SqlManager.getValueFromVectorParam(datiUffint, 2).getStringValue();
            String ncivico =  SqlManager.getValueFromVectorParam(datiUffint, 3).getStringValue();
            String localita =  SqlManager.getValueFromVectorParam(datiUffint, 4).getStringValue();
            String cap =  SqlManager.getValueFromVectorParam(datiUffint, 5).getStringValue();
            Long codnaz =  (Long) SqlManager.getValueFromVectorParam(datiUffint, 6).getValue();
            String iscuc =  SqlManager.getValueFromVectorParam(datiUffint, 7).getStringValue();
            String endpointNso =  SqlManager.getValueFromVectorParam(datiUffint, 8).getStringValue();
            String note =  SqlManager.getValueFromVectorParam(datiUffint, 9).getStringValue();
            String iva =  SqlManager.getValueFromVectorParam(datiUffint, 10).getStringValue();
            String codiceIPA =  SqlManager.getValueFromVectorParam(datiUffint, 11).getStringValue();

            this.sqlManager.update("insert into nso_punticons(id, nso_ordini_id, codein, cod_punto_cons, indirizzo, localita, cap, codnaz)" +
                    " values(?,?,?,?,?,?,?,?)",
              new Object[] {new Long(genChiaviManager.getNextId("NSO_PUNTICONS")), idOrdine,uffint,puntocons,via+" "+ncivico,localita,cap,codnaz});
            //nel caso in cui non si tratti di una CUC popolo gli ordinanti
            iscuc=UtilityStringhe.convertiNullInStringaVuota(iscuc);
            codiceIPA=UtilityStringhe.convertiNullInStringaVuota(codiceIPA);

            if(!"1".equals(iscuc)){
              //ORDINANTE IPA
              if(!"".equals(codiceIPA)){
                //tipo=1
                this.sqlManager.update("insert into nso_ordinanti(id, nso_ordini_id," +
                    " tipo,codein,nomein,endpoint,note,via,citta,cap,codnaz,piva) values(?,?,?,?,?,?,?,?,?,?,?,?)",
                  new Object[] {new Long(genChiaviManager.getNextId("NSO_ORDINANTI")),idOrdine,new Long(1),uffint,nomein,endpointNso,note,
                  via,localita,cap,codnaz,iva});
                //tipo=2
                this.sqlManager.update("insert into nso_ordinanti(id, nso_ordini_id," +
                    " tipo,codein,nomein,via,citta,cap,codnaz,piva) values(?,?,?,?,?,?,?,?,?,?)",
                  new Object[] {new Long(genChiaviManager.getNextId("NSO_ORDINANTI")),idOrdine,new Long(2),uffint,nomein,via,localita,cap,codnaz,iva});
              }
            }else{ //se non si tratta di CUC
              ;
            }

          }//if uffint

          //popolo le linee di ordine, eventualmente con la lista lavori/forniture

          String ngara =datiForm.getColumn("NSO_ORDINI.NGARA").getValue().getStringValue();
          ngara=UtilityStringhe.convertiNullInStringaVuota(ngara);
          String arrmultikey = this.getRequest().getParameter("arrmultikey");
          arrmultikey=UtilityStringhe.convertiNullInStringaVuota(arrmultikey);

          String centroCosto =datiForm.getColumn("NSO_ORDINI.CENTRO_COSTO").getValue().getStringValue();
          centroCosto=UtilityStringhe.convertiNullInStringaVuota(centroCosto);

          String codiga = null;
          String oggetto = null;
          Double iaggiu = null;
          String notega = null;
          String cpv = null;

          String selectQuantitaConsumata = "select sum(coalesce(l.quantita,0))" +
          " from nso_ordini o, nso_linee_ordini l" +
          " where o.id = l.nso_ordini_id and o.ngara = ? and l.codice = ? and (o.stato_ordine <> ? and o.stato_ordine <> ?) and versione = ?" +
          " group by l.codice";

          //recupero i dati di gara/lotto
          Vector<?> datiGaraLotto = this.sqlManager.getVector("select g.codiga,g.not_gar,g.iaggiu,g.notega,c.codcpv" +
              " from gare g" +
              " LEFT JOIN garcpv c on g.ngara = c.ngara and c.numcpv=1" +
              " where  g.ngara = ? ", new Object[]{ngara});

          if(datiGaraLotto!=null && datiGaraLotto.size()>0){
            codiga =  SqlManager.getValueFromVectorParam(datiGaraLotto, 0).getStringValue();
            codiga = UtilityStringhe.convertiNullInStringaVuota(codiga);
            if("".equals(codiga)){
              codiga="1";
            }
            oggetto =  SqlManager.getValueFromVectorParam(datiGaraLotto, 1).getStringValue();
            iaggiu =  (Double) SqlManager.getValueFromVectorParam(datiGaraLotto, 2).getValue();
            notega =  SqlManager.getValueFromVectorParam(datiGaraLotto, 3).getStringValue();
            cpv =  SqlManager.getValueFromVectorParam(datiGaraLotto, 4).getStringValue();

            //verifico se si tratta di monolotto:
            if(!"".equals(ngara)){
                //la verifica se si tratta di monolotto va fatta senza considerare la quantita consumata
                //e considerando i prodotti dell'aggiudicataria
                Long countProdottiAggiudicataria = (Long) this.sqlManager.getObject(
                    "select count(*) from V_GCAP_DPRE where NGARA= ? and COD_DITTA = ?",
                    new Object[] { ngara,dittaAggiudicataria });

                if(new Long(0)<countProdottiAggiudicataria){//MULTIRIGA

                  if(!"".equals(arrmultikey)){
                    String[] ContafVect = arrmultikey.split(";");
                    String inClause = arrmultikey.replace(";", ",");
                    inClause = inClause.substring(0, inClause.length()-1);
                    List listaProdottiAggiudicataria  = this.sqlManager.getListVector(
                        "select CODGAR,NGARA,CODVOC,VOCE,UNIMISEFF,QUANTIEFF,PREOFF" +
                        " from V_GCAP_DPRE where NGARA= ? and COD_DITTA = ? and CONTAF in("+ inClause + ")",
                        new Object[] { ngara,dittaAggiudicataria });
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
                        Double qtaConsumata = (Double) sqlManager.getObject(selectQuantitaConsumata,new Object[] { ngara,codvoc,new Long(3),new Long(7),new Long(0)});
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

                        this.sqlManager.update("insert into nso_linee_ordini(id, nso_ordini_id, id_linea, codice, descrizione, quantita, unimis, prezzo_unitario, centro_costo, note, codcpv, codein_rich)" +
                            " values(?,?,?,?,?,?,?,?,?,?,?,?)",  new Object[] {new Long(genChiaviManager.getNextId("NSO_LINEE_ORDINI")),
                            idOrdine, (k+1), codvoc, voce, qtaDisponibile, unimiseff,preoff,centroCosto,notega,cpv,uffint});
                      }

                    }//if prodotti aggiudicataria

                  }

                }else{//MONORIGA

                  //calcolo comunque la quantità utilizzata rispetto all'importo di aggiudicazione
                  //verifico la consumazione attuale:
                  Double qtaDisponibile = null;
                  Double qtaConsumata = (Double) sqlManager.getObject(selectQuantitaConsumata,new Object[] { ngara,"1",new Long(3),new Long(7),new Long(0)});
                  if(qtaConsumata != null){
                    qtaDisponibile = 1 - qtaConsumata;
                  }else{
                    qtaDisponibile = new Double(1);
                  }

                  this.sqlManager.update("insert into nso_linee_ordini(id, nso_ordini_id, id_linea, codice, descrizione, quantita, unimis, prezzo_unitario, centro_costo, note, codcpv, codein_rich)" +
                      " values(?,?,?,?,?,?,?,?,?,?,?,?)",  new Object[] {new Long(genChiaviManager.getNextId("NSO_LINEE_ORDINI")),
                      idOrdine, new Long(1), codiga, oggetto,qtaDisponibile, "ac",iaggiu,centroCosto,notega,cpv,uffint});
                }


            }
          }

      } catch (SQLException e) {
        throw new GestoreException("Errore nell'inserimento automatico nelle tabelle figlie", null, e);
      }

    }//IF AGGIUDICATARIA

  }


  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    Long idOrdine =datiForm.getLong("NSO_ORDINI.ID");

    try {
      this.sqlManager.update(
          "update nso_ordini set is_revisione = ? where id in (select id_padre from nso_ordini where id = ?)",
          new Object[] { "2",idOrdine });

      this.sqlManager.update(
          "update nso_ordini set versione = versione - 1 where id_originario in (select id_originario from nso_ordini where id = ?)",
          new Object[] { idOrdine });

    } catch (SQLException e) {
      throw new GestoreException("Errore nell'annullamento dell'operazione di variazione ordine!", null, e);
    }

  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    Long idOrdine =datiForm.getLong("NSO_ORDINI.ID");

    //Gestione dati obbligatori condizionali
    if(datiForm.isColumn("NSO_ORDINI.ESENZIONE_CIG")){
      if(!StringUtils.isNumeric(StringUtils.trimToEmpty(datiForm.getString("NSO_ORDINI.ESENZIONE_CIG")))) {
        throw new GestoreException("Il valore del campo Esenzione CIG deve essere un numero.","nso.esenzionecig.numeric.error");
      }
      String cig = datiForm.getString("NSO_ORDINI.CIG");
      cig = UtilityStringhe.convertiNullInStringaVuota(cig);
      if("".equals(cig)){
            //gestione sulla maschera dell'obbligatorio
      }
    }

    if(datiForm.isColumn("NSO_ORDINI.CENTRO_COSTO")){
      String centroCosto = datiForm.getString("NSO_ORDINI.CENTRO_COSTO");
      centroCosto = UtilityStringhe.convertiNullInStringaVuota(centroCosto);
      if(!"".equals(centroCosto)){
        //aggiorno sulle linee solo se vuoto lasciandolo comunque editabile
        try {
          this.sqlManager.update("update NSO_LINEE_ORDINI set centro_costo = ?" +
          		" where nso_ordini_id = ? and (centro_costo is null or centro_costo = '') ", new Object[] {centroCosto,idOrdine});
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore in aggiornamento delle linee dell'ordine",null);
        }
      }
    }

    if(datiForm.isColumn("NSO_ORDINI.DATA_INIZIO_FORN") && datiForm.isColumn("NSO_ORDINI.DATA_FINE_FORN")){
      Date dataInizioForn = datiForm.getData("NSO_ORDINI.DATA_INIZIO_FORN");
      Date dataFineForn = datiForm.getData("NSO_ORDINI.DATA_FINE_FORN");
      if(dataInizioForn != null || dataFineForn!= null){
        //aggiorno sulle linee solo se entrambe vuote lasciandole comunque editabili
        try {
          this.sqlManager.update("update NSO_LINEE_ORDINI set data_inizio_cons = ?,data_fine_cons = ?" +
                " where nso_ordini_id = ? and data_inizio_cons is null and data_fine_cons is null", new Object[] {dataInizioForn,dataFineForn,idOrdine});
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore in aggiornamento delle linee dell'ordine",null);
        }
      }
    }

      // Gestione degli ordinanti
      AbstractGestoreChiaveIDAutoincrementante gestoreNSO_ORDIN = new DefaultGestoreEntitaChiaveIDAutoincrementante(
          "NSO_ORDINANTI", "ID", this.getRequest());
      this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
          gestoreNSO_ORDIN, "NSO_ORDIN",
          new DataColumn[] { datiForm.getColumn("NSO_ORDINI.ID") }, null);


    if(datiForm.isColumn("NSO_ORDINI.IS_DIV_BENEF")){

      String isDivBenef = datiForm.getColumn("NSO_ORDINI.IS_DIV_BENEF").getValue().getStringValue();
      isDivBenef = UtilityStringhe.convertiNullInStringaVuota(isDivBenef);
      if("1".equals(isDivBenef)){
        AbstractGestoreChiaveIDAutoincrementante gestoreNsoBeneficiario = new DefaultGestoreEntitaChiaveIDAutoincrementante(
            "NSO_BENEFICIARIO", "ID", this.getRequest());
        Long idBeneficiario = (Long) datiForm.getColumn("NSO_BENEFICIARIO.ID").getValue().getValue();
        if(idBeneficiario!= null){
          gestoreNsoBeneficiario.update(status, datiForm);
        }else{
          datiForm.setValue("NSO_BENEFICIARIO.NSO_ORDINI_ID", idOrdine);
          gestoreNsoBeneficiario.inserisci(status, datiForm);
        }

      }
    }

    //aggiornamento di NSO_FORNITORE
    if(datiForm.isColumn("NSO_FORNITORE.PERSONA_RIF")){

      DefaultGestoreEntitaChiaveNumerica gestoreNsoFornitore = new DefaultGestoreEntitaChiaveNumerica(
          "NSO_FORNITORE", "ID",null, this.getRequest());
      gestoreNsoFornitore.update(status, datiForm);

    }

    //aggiornamento di NSO_PUNTICONS
    if(datiForm.isColumn("NSO_PUNTICONS.COD_PUNTO_CONS")){

      DefaultGestoreEntitaChiaveNumerica gestoreNsoPuntiConsegna = new DefaultGestoreEntitaChiaveNumerica(
          "NSO_PUNTICONS", "ID",null, this.getRequest());
      gestoreNsoPuntiConsegna.update(status, datiForm);

    }

    if(datiForm.isColumn("NSO_ALLEGATI.ID")){
      this.gestisciAggiornamentiRecordSchedaMultiplaNSO_ALLEGATI(status, datiForm);
    }

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

  /**
   * Gestione aggiornamento facsimile certificati.
   *
   * @param status
   * @param datiForm
   * @throws GestoreException
   */
  private void gestisciAggiornamentiRecordSchedaMultiplaNSO_ALLEGATI(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    AbstractGestoreChiaveIDAutoincrementante gestoreNSO_ALLEGATI = new DefaultGestoreEntitaChiaveIDAutoincrementante("NSO_ALLEGATI", "ID", this.getRequest());

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", this.getServletContext(),
        TabellatiManager.class);

    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1", this.getServletContext(),
        PgManagerEst1.class);


    String nomeCampoNumeroRecord = "NUMERO_NSO_ALLEGATI";
    String nomeCampoDelete = "DEL_NSO_ALLEGATI";
    String nomeCampoMod = "MOD_NSO_ALLEGATI";

    if (datiForm.isColumn(nomeCampoNumeroRecord)) {
      DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(datiForm.getColumns("NSO_ALLEGATI", 0));

      int numeroRecord = datiForm.getLong(nomeCampoNumeroRecord).intValue();

      // *** Controllo dimensione massima dei file in UPLOAD
      HashMap<?, ?> hm = ((UploadMultiploForm) this.getForm()).getFormFiles();
      long dimensioneTotale = 0;
      FormFile ff = null;
      for (int i = 1; i <= numeroRecord; i++) {
        ff = (FormFile) hm.get(new Long(i));
        if (ff != null && ff.getFileSize() > 0) dimensioneTotale += ff.getFileSize();
      }

      String dimensioneTotaleTabellatoStringa = tabellatiManager.getDescrTabellato("A1072", "1");
      if (dimensioneTotaleTabellatoStringa == null || "".equals(dimensioneTotaleTabellatoStringa)) {
        throw new GestoreException("Non è presente il tabellato A1072 per determinare la dimensione massima totale dell'upload dei file",
            "uploadMultiplo.noTabellato", null);
      }

      int pos = dimensioneTotaleTabellatoStringa.indexOf("(");
      if (pos < 1) {
        throw new GestoreException("Non è possibile determinare dal tabellato A1072 la dimensione massima totale dell'upload dei file",
            "uploadMultiplo.noValore", null);
      }

      dimensioneTotaleTabellatoStringa = dimensioneTotaleTabellatoStringa.substring(0, pos - 1);
      dimensioneTotaleTabellatoStringa = dimensioneTotaleTabellatoStringa.trim();
      double dimensioneTotaleTabellatoByte = Math.pow(2, 20) * Double.parseDouble(dimensioneTotaleTabellatoStringa);
      if (dimensioneTotale > dimensioneTotaleTabellatoByte) {
        throw new GestoreException("La dimensione totale dei file da salvare ha superato il limite consentito di "
            + dimensioneTotaleTabellatoStringa
            + " MB", "uploadMultiplo.overflowMultiplo", new String[] { dimensioneTotaleTabellatoStringa }, null);
      }
      // *** Fine controllo dimensione massima file in upload

      for (int indice = 1; indice <= numeroRecord; indice++) {
        DataColumnContainer newDataColumnContainer = new DataColumnContainer(tmpDataColumnContainer.getColumnsBySuffix("_" + indice, false));

        boolean deleteOccorrenza = newDataColumnContainer.isColumn(nomeCampoDelete)
            && "1".equals(newDataColumnContainer.getString(nomeCampoDelete));
        boolean updateOccorrenza = newDataColumnContainer.isColumn(nomeCampoMod)
            && "1".equals(newDataColumnContainer.getString(nomeCampoMod));

        // Rimozione dei campi fittizi (il campo per la marcatura della delete e
        // tutti gli eventuali campi passati come argomento)
        newDataColumnContainer.removeColumns(new String[] { "NSO_ALLEGATI." + nomeCampoDelete, "NSO_ALLEGATI." + nomeCampoMod });

        // E' stata richiesta la cancellazione della riga, se il campo chiave
        // numerica è diverso da NULL eseguo effettivamente la cancellazione del
        // record.
        Long id = newDataColumnContainer.getLong("NSO_ALLEGATI.ID");

        if (deleteOccorrenza) {
          if (id != null) {
            String idprg = newDataColumnContainer.getString("NSO_ALLEGATI.IDPRG");
            Long iddocdig = newDataColumnContainer.getLong("NSO_ALLEGATI.IDDOCDIG");
            pgManagerEst1.cancellaW_DOCDIG(idprg, iddocdig);
            gestoreNSO_ALLEGATI.elimina(status, newDataColumnContainer);
          }
        } else if (updateOccorrenza) {
          // Se il campo chiave numerico è nullo significa che bisogna inserire
          // una nuova occorrenza nelle tabelle W_DOCDIG e MEALLARTCAT
          if (id == null) {
            //select max su ordine
            Long maxNprogrOrdine = null;
            try {
              maxNprogrOrdine = (Long) this.sqlManager.getObject("select max(nprogr) from nso_allegati where nso_ordini_id = ?",
                  new Object[] { datiForm.getLong("NSO_ORDINI.ID") });
              if(maxNprogrOrdine!= null){
                maxNprogrOrdine= maxNprogrOrdine + new Long(1);
              }else{
                maxNprogrOrdine = new Long(1);
              }

            } catch (SQLException e) {
              throw new GestoreException("Errore nella determinazione del max progressivo degli allegati dell'ordine", null);
            }

            Long iddocdig = pgManagerEst1.inserisciW_DOCDIG(this.getRequest(), this.getForm(),indice, "PG", "NSO_ALLEGATI");
            newDataColumnContainer.setValue("NSO_ALLEGATI.IDPRG", "PG");
            newDataColumnContainer.setValue("NSO_ALLEGATI.IDDOCDIG", iddocdig);
            newDataColumnContainer.setValue("NSO_ALLEGATI.NSO_ORDINI_ID", datiForm.getLong("NSO_ORDINI.ID"));
            newDataColumnContainer.setValue("NSO_ALLEGATI.NPROGR", maxNprogrOrdine);
            gestoreNSO_ALLEGATI.inserisci(status, newDataColumnContainer);
          } else {
            // In questo caso si tratta di aggiornare un record esistente
            String idprg = newDataColumnContainer.getString("NSO_ALLEGATI.IDPRG");
            Long iddocdig = newDataColumnContainer.getLong("NSO_ALLEGATI.IDDOCDIG");
            pgManagerEst1.aggiornaW_DOCDIG(this.getRequest(), this.getForm(), indice, idprg, iddocdig);
            gestoreNSO_ALLEGATI.update(status, newDataColumnContainer);
          }
        }
      }
    }
  }

}
