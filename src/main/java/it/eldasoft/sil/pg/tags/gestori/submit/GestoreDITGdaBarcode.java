/*
 * Created on 13/12/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiRicezioneFunction;
import it.eldasoft.sil.portgare.datatypes.AggiornamentoAnagraficaImpresaDocument;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneType;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.xmlbeans.XmlException;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard che si occupa di preparare i dati per potere
 * effettuare l'inserimento in DITG. Il gestore viene richiamato dalla
 * pagina ditg-schedaInsert-Barcode.jsp
 *
 * @author Marcello Caminiti
 */
public class GestoreDITGdaBarcode extends GestoreDITG {
  static final String fileXmlBarcode = "dati_partrti.xml";
  static final String fileXmlAggAnagrafica = "dati_agganag.xml";

  @Override
  public String getEntita() {
    return "DITG";
  }

  public GestoreDITGdaBarcode() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestoreDITGdaBarcode(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer
      dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {

    String select=null;
    String tipscad = UtilityStruts.getParametroString(this.getRequest(),"tipscad");
    String impresa = UtilityStruts.getParametroString(this.getRequest(),"impresa"); //login
    String codiceGara = UtilityStruts.getParametroString(this.getRequest(),"gara");
    String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String genere = UtilityStruts.getParametroString(this.getRequest(),"genere");
    String dittao = UtilityStruts.getParametroString(this.getRequest(),"dittao");   //codice ditta richiedente (eventualmente la mandataria dell'ati)
    String nomimoEstesa = UtilityStruts.getParametroString(this.getRequest(),"nomimoEstesa");   //ragione sociale ditta richiedente
    String tipoRTI = UtilityStruts.getParametroString(this.getRequest(),"tipoRTI"); //1 se richiesta ATI
    String nomeATI = UtilityStruts.getParametroString(this.getRequest(),"nomeATI"); //ragione sociale ATI
    String NPRDOMFIT = dataColumnContainer.getString("NPRDOMFIT");
    Timestamp DATA = dataColumnContainer.getData("DATA");
    String ORA = dataColumnContainer.getString("ORA");
    Long MEZZO = dataColumnContainer.getLong("MEZZO");
    Long STATO = dataColumnContainer.getLong("STATO");
    String NSPED = dataColumnContainer.getString("NSPED");
    Long RIT = dataColumnContainer.getLong("RIT");
    String NOTP = dataColumnContainer.getString("NOTP");
    String tipimpNewRTI = UtilityStruts.getParametroString(this.getRequest(),"tipimpNewRTI"); //valore del tipimp per la nuova ATI
    Long tipoImpresa = null;
    String quotaMandataria = UtilityStruts.getParametroString(this.getRequest(),"quotaMandataria");

    String isProfiloProtocollo = UtilityStruts.getParametroString(this.getRequest(),"isProfiloProtocollo");
    Boolean isProfiloProtocolloBool = new Boolean(false);
    if("true".equals(isProfiloProtocollo))
      isProfiloProtocolloBool = new Boolean(true);
    String cfimp = UtilityStruts.getParametroString(this.getRequest(),"cfimp");
    String pivimp = UtilityStruts.getParametroString(this.getRequest(),"pivimp");

    String codiceATI=null;
    String codimp=null;
    String nomimp=null;

    boolean gestioneComponenti= false;
    boolean esclusioneLotto=false;
    boolean offertaRT =false;
    boolean mandatariaRTesistente=false;

    codimp = dittao;
    nomimp = nomimoEstesa;
    if(nomimp.length() > 61)
      nomimp = nomimp.substring(0, 60);

    Timestamp dataProtTimestamp = null;
    if(dataColumnContainer.isColumn("DATPROT_NASCOSTO") && dataColumnContainer.getColumn("DATPROT_NASCOSTO").isModified()){
      String dataProtNascosto = dataColumnContainer.getString("DATPROT_NASCOSTO");
      if(dataProtNascosto!=null && !"".equals(dataProtNascosto)){
        //La stringa ha il formato GG/MM/AAAA HH:MM per poterne fare la conversione si devono aggiungere i secondi
        dataProtNascosto += ":00";
        Date dataProt = UtilityDate.convertiData(dataProtNascosto, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS);
        dataProtTimestamp = new Timestamp(dataProt.getTime());
      }
    }

    if("1".equals(tipoRTI)){
      try {
        //Si deve controllare se la mandataria è già presente nella gara individualmente
        select="select count(dittao) from ditg where ngara5=? and dittao=?";
        Long conteggio = (Long)this.sqlManager.getObject(select, new Object[]{ngara,codimp});
        if(conteggio!=null && conteggio.longValue()>0)
          mandatariaRTesistente=true;
        //Verifica se ci sono ATI in gara con uguale ragione sociale e con mandataria la ditta richiedente
        select="select codimp from impr,ragimp,edit where impr.codimp=edit.codime and impr.codimp=ragimp.codime9 and " +
        " edit.codgar4=? and ragimp.coddic=? and ragimp.impman='1' and upper(impr.nomest)=?";
        List listaATI = this.sqlManager.getListVector(select, new Object[]{codiceGara,codimp,nomeATI.toUpperCase()});
        if(listaATI!=null && listaATI.size()>0){
          //Se ne trova più di una, considera la prima occ. trovata
          codiceATI = this.sqlManager.getValueFromVectorParam(listaATI.get(0), 0).stringValue();
          //Se l'ATI è presente nel backoffice  non si devono inserire le componenti presenti nel messaggio FS9
          gestioneComponenti=false;
          if("1".equals(genere))
            esclusioneLotto=true;
        } else {
          //Se non ce ne sono, inserisce una nuova ati in anagrafica
          codiceATI = this.getGeneManager().calcolaCodificaAutomatica("IMPR","CODIMP");
          String nomeATITroncato= nomeATI;
          if(nomeATITroncato.length()>61)
            nomeATITroncato = nomeATITroncato.substring(0, 60);
          sqlManager.update("insert into IMPR (CODIMP,NOMIMP,NOMEST,TIPIMP) values(?,?,?,?)",
              new Object[]{codiceATI,nomeATITroncato,nomeATI,new Long(tipimpNewRTI)});
          sqlManager.update("insert into RAGIMP (IMPMAN,CODDIC,NOMDIC,CODIME9,QUODIC) values(?,?,?,?,?)",
              new Object[]{"1",codimp,nomimp,codiceATI, new Double(quotaMandataria)});
          tipoImpresa = new Long(tipimpNewRTI);
          gestioneComponenti=true;
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'inserimento dell'impresa ", null, e);
      }

      codimp=codiceATI;
      nomimp=nomeATI;
      if(nomimp.length()>61)
        nomimp = nomimp.substring(0, 60);
    }else{
      //Si deve controllare se l'impresa è un consorzio
      try {
        tipoImpresa= (Long)this.sqlManager.getObject("select tipimp from impr where codimp=?", new Object[]{codimp});
        if(tipoImpresa!=null && (tipoImpresa.longValue()==2 || tipoImpresa.longValue()==11))
          gestioneComponenti=true;
      } catch (SQLException e) {
        throw new GestoreException("Errore nella lettura del tipo impresa dell'impresa: " + codimp, null, e);
      }
    }



    Vector elencoCampi = new Vector();
    elencoCampi.add(new DataColumn("DITG.NGARA5",
        new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
    elencoCampi.add(new DataColumn("DITG.CODGAR5",
        new JdbcParametro(JdbcParametro.TIPO_TESTO, codiceGara)));
    elencoCampi.add(new DataColumn("DITG.DITTAO",
        new JdbcParametro(JdbcParametro.TIPO_TESTO, codimp)));
    elencoCampi.add(new DataColumn("DITG.NOMIMO",
        new JdbcParametro(JdbcParametro.TIPO_TESTO, nomimp)));
    elencoCampi.add(new DataColumn("DITG.NPROGG",
        new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
    elencoCampi.add(new DataColumn("DITG.NUMORDPL",
        new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));

    switch (Integer.parseInt(tipscad)) {
      case 1:
        elencoCampi.add(new DataColumn("DITG.DRICIND",
            new JdbcParametro(JdbcParametro.TIPO_DATA, DATA)));
        elencoCampi.add(new DataColumn("DITG.ORADOM",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, ORA)));
        elencoCampi.add(new DataColumn("DITG.MEZDOM",
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, MEZZO)));
        elencoCampi.add(new DataColumn("DITG.PLIDOM",
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, STATO)));
        elencoCampi.add(new DataColumn("DITG.NSPEDDOM",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, NSPED)));
        elencoCampi.add(new DataColumn("DITG.RITDOM",
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, RIT)));
        elencoCampi.add(new DataColumn("DITG.NOTPDOM",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, NOTP)));
        elencoCampi.add(new DataColumn("DITG.NPRDOM",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, NPRDOMFIT)));
        elencoCampi.add(new DataColumn("DITG.INVGAR",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, "1")));
        if(dataProtTimestamp!=null)
          elencoCampi.add(new DataColumn("DITG.DPRDOM",
              new JdbcParametro(JdbcParametro.TIPO_DATA, dataProtTimestamp)));
        break;
      case 2:
        elencoCampi.add(new DataColumn("DITG.DATOFF",
            new JdbcParametro(JdbcParametro.TIPO_DATA, DATA)));
        elencoCampi.add(new DataColumn("DITG.ORAOFF",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, ORA)));
        elencoCampi.add(new DataColumn("DITG.MEZOFF",
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, MEZZO)));
        elencoCampi.add(new DataColumn("DITG.PLIOFF",
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, STATO)));
        elencoCampi.add(new DataColumn("DITG.NSPEDOFF",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, NSPED)));
        elencoCampi.add(new DataColumn("DITG.RITOFF",
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, RIT)));
        elencoCampi.add(new DataColumn("DITG.NOTPOFF",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, NOTP)));
        elencoCampi.add(new DataColumn("DITG.NPROFF",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, NPRDOMFIT)));
        elencoCampi.add(new DataColumn("DITG.INVOFF",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, "1")));
        if(dataProtTimestamp!=null)
          elencoCampi.add(new DataColumn("DITG.DPROFF",
              new JdbcParametro(JdbcParametro.TIPO_DATA, dataProtTimestamp)));
        break;
      case 3:
        elencoCampi.add(new DataColumn("DITG.DATREQ",
            new JdbcParametro(JdbcParametro.TIPO_DATA, DATA)));
        elencoCampi.add(new DataColumn("DITG.ORAREQ",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, ORA)));
        elencoCampi.add(new DataColumn("DITG.MEZREQ",
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, MEZZO)));
        elencoCampi.add(new DataColumn("DITG.NSPEDREQ",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, NSPED)));
        elencoCampi.add(new DataColumn("DITG.RITREQ",
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, RIT)));
        elencoCampi.add(new DataColumn("DITG.NOTPREQ",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, NOTP)));
        elencoCampi.add(new DataColumn("DITG.NPRREQ",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, NPRDOMFIT)));
        elencoCampi.add(new DataColumn("DITG.ESTIMP",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, "1")));
        break;
    }



    //Prima di inserire l'occorrenza si controlla se non esiste già in database
    //una occorrenza di DITG
    try {

      if("1".equals(tipoRTI)){
        //Inserisco un campo per il controllo della duplicazione della mandataria
        elencoCampi.add(new DataColumn("CODDIC",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, dittao)));
        elencoCampi.add(new DataColumn("BARCODE",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, "1")));
        if(mandatariaRTesistente || esclusioneLotto){
          elencoCampi.add(new DataColumn("DITG.ACQUISIZIONE",
              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(5))));
          elencoCampi.add(new DataColumn("DITG.DITTAINV",
              new JdbcParametro(JdbcParametro.TIPO_TESTO, dittao)));
        }
      }

      DataColumnContainer containerDITG = new DataColumnContainer(elencoCampi);

      if("1".equals(tipoRTI) &&  (mandatariaRTesistente || esclusioneLotto)){
        Long nprogg = (Long)this.sqlManager.getObject("select nprogg from ditg where ngara5=? and codgar5=? and dittao=?", new Object[]{ngara,codiceGara,dittao});
        containerDITG.setValue("DITG.NPROGG", nprogg);
        containerDITG.setValue("DITG.NUMORDPL", nprogg);
        this.getRequest().setAttribute("offertaRT", "1");

        offertaRT = true;
      }

      DefaultGestoreEntita gestoreDITG = new DefaultGestoreEntita("DITG",
          this.getRequest());

      select="select count(ngara5) from ditg where ngara5=? and dittao=?";
      Long count = (Long) this.geneManager.getSql().getObject(
              select,new Object[] { ngara,codimp });
      if (count.longValue() == 0) {
          super.preInsert(status, containerDITG);

          //Nella preInsert sono state inserite le occorrenze in DPRE e nella EDIT
          //e sono stati popolati i campi per la DITG.
          //Si effettua qui l'inserimento che prima veniva effettuato nella preInsert
          //di modo che GestoreDITG sia standard
          gestoreDITG.inserisci(status, containerDITG);
          //Inizializzazione documenti della ditta
          pgManager.inserimentoDocumentazioneDitta(codiceGara, ngara, codimp);

          // Se la gara e' di tipo 'a lotti con offerta unica' bisogna associare la
          // ditta ad ogni lotto esistente, oltre alla occorrenza complementare in GARE
          if(genere != null && "3".equals(genere)){
              String ammgar = null;
              String partgar = null;
              Long fasgar = null;
              String invgar = null;

              // Estrazione di NGARA dei lotti della gara con offerta unica,
              // Per inserire la ditta in ciascun lotto
              List listaLotti = this.sqlManager.getListVector(
                      "select NGARA,IMPAPP from GARE " +
                       "where CODGAR1 = ? and NGARA <> CODGAR1 and GENERE is null",
                       new Object[]{ngara});
              if(listaLotti != null && listaLotti.size() > 0){
                // Campi da valorizzare nell'inserimento della ditta per i diversi
                // lotti della gara
                String campiDaInserire = "DITG.CODGAR5,DITG.NOMIMO,DITG.NPROGG,DITG.DITTAO," +
                        "DITG.NGARA5,DITG.IMPAPPD,DITG.NUMORDPL";

                HashMap hm = containerDITG.getColonne();
                String[] campiDaRimuovere = new String[hm.size() - campiDaInserire.split(",").length];
                Set chiaviMap = hm.keySet();
                Iterator iter = chiaviMap.iterator();
                int indice = 0;

                // Ciclo sui campi presenti nel DataColumnContainer per determinare i
                // quelli da non inserire
                while(iter.hasNext()){
                    String tmpChiaveMap = (String) iter.next();
                    if(campiDaInserire.indexOf(tmpChiaveMap.toUpperCase()) < 0){
                        campiDaRimuovere[indice] = tmpChiaveMap;
                        indice++;
                    }
                }
                // Rimozione dei campi diversi da quelli presenti nella variabile
                // campiDaInserire
                containerDITG.removeColumns(campiDaRimuovere);

                for(int i=0; i < listaLotti.size(); i++){
                    Vector lotto = (Vector) listaLotti.get(i);
                    String tmpCodiceLotto = (String)((JdbcParametro) lotto.get(0)).getValue();
                    Double tmpImpApp = (Double)((JdbcParametro) lotto.get(1)).getValue();
                    containerDITG.setValue("DITG.NGARA5", tmpCodiceLotto);
                    containerDITG.setValue("DITG.IMPAPPD", tmpImpApp);
                    if(offertaRT){
                      //Nel caso di presentazione offerta in RT si devono riportare nei lotti i valori di
                      //PARTGAR, AMMGAR e FASGAR della ditta del lotto corrispondente
                      Vector datiDitta = this.sqlManager.getVector("select partgar, ammgar, fasgar, invgar from ditg where ngara5=? and codgar5=? and dittao=?",
                          new Object[]{tmpCodiceLotto, codiceGara, dittao});
                      invgar = null;
                      if(datiDitta!=null && datiDitta.size()>0){
                        partgar = SqlManager.getValueFromVectorParam(datiDitta, 0).stringValue();
                        ammgar = SqlManager.getValueFromVectorParam(datiDitta, 1).stringValue();
                        fasgar = SqlManager.getValueFromVectorParam(datiDitta, 2).longValue();
                        invgar = SqlManager.getValueFromVectorParam(datiDitta, 3).stringValue();
                        containerDITG.addColumn("DITG.PARTGAR", partgar);
                        containerDITG.addColumn("DITG.AMMGAR", ammgar);
                        containerDITG.addColumn("DITG.FASGAR", fasgar);
                        containerDITG.addColumn("DITG.DITTAINV", dittao);
                      }

                    }
                    gestoreDITG.inserisci(status, containerDITG);
                    if(offertaRT){
                      //Nel gestore DITG all'inserimento viene impostato INVGAR=1, ma si deve riportare in realtà il valore
                      //letto dal db
                      this.sqlManager.update("update DITG set INVGAR = ? where CODGAR5=? and NGARA5=? and DITTAO=?",
                            new Object[]{invgar,codiceGara, tmpCodiceLotto,codimp});
                    }
                }
             }
          }
          //esclusione della mandataria
          if(offertaRT){
            GestoreFasiRicezione gfr = new GestoreFasiRicezione();
            gfr.setRequest(this.getRequest());
            Double faseGara = new Double(Math.floor(GestioneFasiRicezioneFunction.FASE_RICEZIONE_PLICHI/10));
            Long faseGaraLong = new Long(faseGara.longValue());
            gfr.escludiDittaOfferta(codimp, ngara, codiceGara, dittao, faseGaraLong, status);
          }
      } else {
          containerDITG.getColumn("DITG.NGARA5").setChiave(true);
          containerDITG.getColumn("DITG.NGARA5").setObjectOriginalValue(ngara);
          containerDITG.getColumn("DITG.DITTAO").setChiave(true);
          containerDITG.getColumn("DITG.DITTAO").setObjectOriginalValue(codimp);
          containerDITG.getColumn("DITG.CODGAR5").setChiave(true);
          containerDITG.getColumn("DITG.CODGAR5").setObjectOriginalValue(codiceGara);
          containerDITG.update("DITG", sqlManager);
      }
    } catch (SQLException e) {
      //Si impostano nella pagina i valori letti da barcode
      this.getRequest().setAttribute("tipscad", tipscad);
      this.getRequest().setAttribute("impresa", impresa);
      this.getRequest().setAttribute("gara", codiceGara);
      this.getRequest().setAttribute("lotto", ngara);
      this.getRequest().setAttribute("genere", genere);
      this.getRequest().setAttribute("ngara", ngara);
      this.getRequest().setAttribute("dittao", dittao);

      this.getRequest().setAttribute("nomimoEstesa", nomimoEstesa);
      this.getRequest().setAttribute("tipoRTI", tipoRTI);
      this.getRequest().setAttribute("nomeATI", nomeATI);
      this.getRequest().setAttribute("pivimp", pivimp);
      this.getRequest().setAttribute("cfimp", cfimp);
      this.getRequest().setAttribute("isProfiloProtocollo", isProfiloProtocolloBool);

      this.getRequest().setAttribute("tipimpNewRTI", new Long(tipimpNewRTI));
      this.getRequest().setAttribute("quotaMandataria",quotaMandataria);
      throw new GestoreException("Errore nell'inserimento della ditta in gara ", null, e);
    }catch (GestoreException e) {
      //Si impostano nella pagina i valori letti da barcode
      this.getRequest().setAttribute("tipscad", tipscad);
      this.getRequest().setAttribute("impresa", impresa);
      this.getRequest().setAttribute("gara", codiceGara);
      this.getRequest().setAttribute("lotto", ngara);
      this.getRequest().setAttribute("genere", genere);
      this.getRequest().setAttribute("ngara", ngara);
      this.getRequest().setAttribute("dittao", dittao);

      this.getRequest().setAttribute("nomimoEstesa", nomimoEstesa);
      this.getRequest().setAttribute("tipoRTI", tipoRTI);
      this.getRequest().setAttribute("nomeATI", nomeATI);
      this.getRequest().setAttribute("pivimp", pivimp);
      this.getRequest().setAttribute("cfimp", cfimp);
      this.getRequest().setAttribute("isProfiloProtocollo", isProfiloProtocolloBool);

      this.getRequest().setAttribute("tipimpNewRTI", new Long(tipimpNewRTI));
      this.getRequest().setAttribute("quotaMandataria",quotaMandataria);
      throw e;
    }

    boolean erroriFS9 =  false;
    String comkey2=ngara;
    if ("1".equals(genere))
     comkey2 =codiceGara;

    if(gestioneComponenti){
      //Lettura del messaggio FS9 per estrarre le eventuali componenti del raggruppamento o del consorzio
      erroriFS9 = this.gestioneMessaggi("FS9", impresa, dittao,ngara,codimp,comkey2);
    }

   //Aggiorna lo stato delle comunicazioni di tipo FS9
    try {

      sqlManager.update("update w_invcom set comstato = ? where idprg = ? and comstato = ? and comtipo = ? and comkey1 = ? and comkey2= ?",
          new Object[]{"6","PA","5","FS9",impresa,comkey2});
    } catch (SQLException e) {
      throw new GestoreException("Errore nell'aggiornamento dello stato del messaggio FS9 ", null, e);
    }

    //Acquisisce eventuali richieste di aggiornamento anagrafica della ditta richiedente
    boolean erroriFS5 = this.gestioneMessaggi("FS5", impresa, dittao,null,null,null);

    if(erroriFS5 || erroriFS9 ){
      this.getRequest().setAttribute("erroreAcquisizione", "1");
    }else{
      this.getRequest().setAttribute("acquisizioneEseguita", "1");
    }

    //String stampaEtichetta = dataColumnContainer.getString("STAMPA_EP");
    Long stampaEtichetta = dataColumnContainer.getLong("STAMPA_EP");
    if(stampaEtichetta!=null &&  stampaEtichetta.longValue() == 1){
      //Poichè dopo il salvataggio si vuole chiamare la stampa etichetta in automatico
      //ho bisogno di passare alla pagina per la stampa il parametro key con la seguente
      //struttura:
      //key:DITG.CODGAR5=T:<codgar>;DITG.DITTAO=T:<dittao>;DITG.NGARA5=T:<ngara>
      //ed anche il tipo protocollo
      String chiave="DITG.CODGAR5=T:" + codiceGara + ";DITG.DITTAO=T:" + codimp + ";DITG.NGARA5=T:" + ngara;
      this.getRequest().setAttribute("chiave", chiave);
      String tipoProtocollo = tipscad;
      this.getRequest().setAttribute("tipoProtocollo", tipoProtocollo);
    }

  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer dataColumnContainer)
      throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  /**
   * Determina se si tratta di una nuova ATI oppure no. In quest'ultimo caso restituisce il
   * codice dell'ATI.
   * @param ngara
   * @param codiceMandataria
   * @return Eventuale codice della ATI
   * @throws SQLException
   */
  private String nuovaATI(String nGara, String codiceMandataria)throws SQLException{
    String ret=null;
    String select="select count(coddic) from ragimp where coddic=? and impman=1";
    Long numMandatarie= (Long)this.sqlManager.getObject(select, new Object[]{codiceMandataria});
    if (numMandatarie!=null && numMandatarie.longValue()>0){
      select="select codime9 from ragimp where coddic=? and impman=1";
      List listaCodiciAti = this.sqlManager.getListVector(select, new Object[]{codiceMandataria});
      if(listaCodiciAti!=null && listaCodiciAti.size()>0){
        select="select count(ngara5) from ditg where ngara5=? and dittao=?";
        Long numDitte=null;
        for(int i=0; i < listaCodiciAti.size(); i++){
          Vector codiceAtiVector = (Vector) listaCodiciAti.get(i);
          String codiceAti = (String)((JdbcParametro) codiceAtiVector.get(0)).getValue();
          numDitte = (Long)this.sqlManager.getObject(select, new Object[]{nGara,codiceAti});
          if(numDitte!= null && numDitte.longValue()>0){
            ret = codiceAti;
            break;
          }
        }
      }
    }

    return ret;
  }

  /**
   * Lettura dei messaggi FS5 e FS9 provenienti da portale
   * @param comtipo
   * @param comkey1
   * @param ditta
   * @param ngara
   * @param codiceRaggruppamento
   * @param comkey2
   * @return boolean true sono stati riscontrati errori
   * @throws GestoreException
   */
  private boolean gestioneMessaggi(String comtipo, String comkey1, String ditta,String ngara,String codiceRaggruppamento, String comkey2) throws GestoreException{
    String sql_W=null;
    String select=null;
    boolean errori=false;
    //Occorrenze per le quali in w_invcom è richiesto aggiornamento anagrafico
    String idprg="PA";
    String comstato= "5";

    //sql_W="select IDCOM,COMKEY1 from w_invcom where idprg = ? and comstato = ? and comtipo = ? and comkey1 = ? order by IDCOM";
    sql_W="select IDCOM,COMKEY1 from w_invcom where idprg = ? and comtipo = ? and comkey1 = ?";
    Object par[] = new Object[4];
    par[0]= idprg;
    par[1] = comtipo;
    par[2] = comkey1;
    if("FS9".equals(comtipo)){
      sql_W+=" and comkey2= ?";
      par[3] = comkey2;
    }else{
      sql_W+=" and comstato = ?";
      par[3] = comstato;
    }sql_W+=" order by IDCOM";
    //consideriamo una lista anche se dovre essere unico il file
    List listaIDCOM = null;
    try {
      listaIDCOM = sqlManager.getListVector(sql_W,par);
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreAcquisizione", "1");
      throw new GestoreException("Errore nella lettura della tabella W_INVCOM ", null, e);
    }

    if (listaIDCOM != null && listaIDCOM.size() > 0) {
      for (int i = 0; i < listaIDCOM.size(); i++) {
        //Nel caso di messaggi FS9 se ne deve processare solo uno, si prende il primo
        if("FS9".equals(comtipo) && i>0)
          break;
        Long idcom = SqlManager.getValueFromVectorParam(listaIDCOM.get(i), 0).longValue();
        //Vengono letti i documenti associati ad ogni occorrenza di W_INVCOM
        select="select idprg,iddocdig from w_docdig where DIGENT = ? and digkey1 = ? and idprg = ? and dignomdoc = ? ";
        String digent="W_INVCOM";
        String idprgW_DOCDIG="PA";
        Vector datiW_DOCDIG = null;
        String nomeFile = fileXmlAggAnagrafica;
        if("FS9".equals(comtipo))
          nomeFile = fileXmlBarcode;
        try {
          datiW_DOCDIG = sqlManager.getVector(select,
               new Object[]{digent, idcom.toString(), idprgW_DOCDIG, nomeFile});

        } catch (SQLException e) {
          this.getRequest().setAttribute("erroreAcquisizione", "1");
          throw new GestoreException("Errore nella lettura della tabella W_DOCDIG ", null, e);
        }
        String idprgW_INVCOM = null;
        Long iddocdig = null;
        GestorePopupAcquisisciDaPortale gacqport = new GestorePopupAcquisisciDaPortale();
        gacqport.setRequest(this.getRequest());
        if(datiW_DOCDIG != null ){
          if(((JdbcParametro)datiW_DOCDIG.get(0)).getValue() != null)
            idprgW_INVCOM = ((JdbcParametro) datiW_DOCDIG.get(0)).getStringValue();

          if(((JdbcParametro)datiW_DOCDIG.get(1)).getValue() != null)
          iddocdig = ((JdbcParametro) datiW_DOCDIG.get(1)).longValue();

          BlobFile fileAllegato = null;
          FileAllegatoManager fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager",
              this.getServletContext(), FileAllegatoManager.class);
          try {
            fileAllegato = fileAllegatoManager.getFileAllegato(idprgW_INVCOM,iddocdig);
          } catch (Exception e) {
            this.getRequest().setAttribute("erroreAcquisizione", "1");
            throw new GestoreException("Errore nella lettura del file allegato presente nella tabella W_DOCDIG", null, e);
          }
          String xml=null;

          if(fileAllegato!=null && fileAllegato.getStream()!=null){
            xml = new String(fileAllegato.getStream());
          }else{
            errori=true;
            gacqport.aggiornaStatoW_INVOCM(idcom,"7");
          }

          if("FS5".equals(comtipo)){
            AggiornamentoAnagraficaImpresaDocument document;
            try {
              document = AggiornamentoAnagraficaImpresaDocument.Factory.parse(xml);

              pgManager.aggiornaDitta(document,ditta,"UPDATE");

              //Aggiornamento dello stato delle occorrenze per le quali in w_invcom è richiesta l' Iscrizione in elenco
              gacqport.aggiornaStatoW_INVOCM(idcom,"6");

            } catch (XmlException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }else if("FS9".equals(comtipo)){

            ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
                CostantiGenerali.PROFILO_UTENTE_SESSIONE);
            TipoPartecipazioneDocument document;
            try {
              document = TipoPartecipazioneDocument.Factory.parse(xml);
              TipoPartecipazioneType tipoPartecipazione= document.getTipoPartecipazione();
              //Si controlla se vi sono partecipanti
              if(tipoPartecipazione.isSetPartecipantiRaggruppamento()){
                Long tipoImpresa = new Long(2);
                if(tipoPartecipazione.getRti())
                  tipoImpresa = new Long(3);
                pgManager.gestionePartecipanti(tipoPartecipazione.getPartecipantiRaggruppamento(),codiceRaggruppamento,tipoImpresa,profilo.getId(),ngara,ditta);
              }
            } catch (XmlException e) {
              this.getRequest().setAttribute("erroreAcquisizione", "1");
              throw new GestoreException("Errore nella lettura del file allegato presente nella tabella W_DOCDIG", null, e);
            }

          }
        }else{

          errori=true;
          gacqport.aggiornaStatoW_INVOCM(idcom,"7");
        }//if
      }//for
    }//if
    return errori;
  }
}