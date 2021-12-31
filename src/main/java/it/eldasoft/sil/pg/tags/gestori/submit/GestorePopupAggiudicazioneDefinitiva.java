/*
 * Created on 30/lug/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.integrazioni.CinecaWSManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.sil.pg.bl.ControlliOepvManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore della popup di aggiudicazione definitiva per definire la ditta
 * aggiudicataria della gara
 *
 * @author Luca.Giacomazzo
 */
public class GestorePopupAggiudicazioneDefinitiva extends AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestorePopupAggiudicazioneDefinitiva.class);
  /** Manager Integrazione Cineca */
  private CinecaWSManager cinecaWSManager;

  private PgManagerEst1 pgManagerEst1;

  private PgManager pgManager;

  private ControlliOepvManager controlliOepvManager;

  @Override
  public String getEntita() {
    return "GARE";
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    String integrazioneCineca = ConfigManager.getValore("integrazioneAnagraficheUGOV");
    integrazioneCineca = UtilityStringhe.convertiNullInStringaVuota(integrazioneCineca);
    if("1".equals(integrazioneCineca)){
      // Estraggo il manager per Cineca
      cinecaWSManager = (CinecaWSManager) UtilitySpring.getBean("cinecaWSManager",
          this.getServletContext(), CinecaWSManager.class);
    }
    pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        this.getServletContext(), PgManagerEst1.class);
    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);
    controlliOepvManager = (ControlliOepvManager) UtilitySpring.getBean("controlliOepvManager",
        this.getServletContext(), ControlliOepvManager.class);

  }

  public GestorePopupAggiudicazioneDefinitiva() {
    super(false);
  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    AggiudicazioneManager aggiudicazioneManager = (AggiudicazioneManager) UtilitySpring.getBean(
        "aggiudicazioneManager", this.getServletContext(),
        AggiudicazioneManager.class);

    String ngara = null;
    if(impl.isColumn("NGARA5_1"))
      ngara = impl.getString("NGARA5_1");
    else
      ngara = impl.getString("NGARA5");

    String oldAggiudicataria = null;


    HashMap hMapConteggi = new HashMap();
    String modoRichiamo = UtilityStruts.getParametroString(this.getRequest(), "MODORICHIAMO");
    String aqoper = this.getRequest().getParameter("aqoper");

    //variabili per tracciatura eventi
    String messageKey = null;
    int livEvento = 3;
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
    String descrDitta = "";

    try{

      // Creazione del DataColumnContainer necessario al metodo
      // AggiudicazioneManager.aggiudicazioneFaseB
      DataColumnContainer dataColumnContainerGARE = new DataColumnContainer(
          this.getSqlManager(), "GARE",
          "select CODGAR1, NGARA, PRECUT, LIMMAX, NOFVAL, RIDISO, FASGAR, "
              + "NOMIMA, DITTA, IAGGIU, RIBAGG, IMPGAR, RIBOEPV from GARE "
              + "where NGARA = ? ", new Object[]{ngara});

      String codgar = dataColumnContainerGARE.getString("CODGAR1");

      dataColumnContainerGARE.getColumn("GARE.NGARA").setChiave(true);
      if(impl.isColumn("PRIMAAGGIUDICATARIASELEZIONATA_1"))
        dataColumnContainerGARE.addColumn("GARE.PRIMAAGGIUDICATARIASELEZIONATA",
          impl.getColumn("PRIMAAGGIUDICATARIASELEZIONATA_1"));
      else
        dataColumnContainerGARE.addColumn("GARE.PRIMAAGGIUDICATARIASELEZIONATA",
            impl.getColumn("PRIMAAGGIUDICATARIASELEZIONATA"));

      try {
        Long genere=(Long)this.sqlManager.getObject("select genere from v_gare_torn where codice=?", new Object[]{codgar});

        String chiaveGara= ngara;
        if((new Long(3)).equals(genere))
          chiaveGara=codgar;

        String elencoe=(String)this.sqlManager.getObject("select elencoe from gare where ngara=?", new Object[]{chiaveGara});

        oldAggiudicataria = (String)this.sqlManager.getObject("select ditta from gare where ngara = ?", new Object[]{ngara});

        // Aggiornamento del campo DITG.RICSUB e DITG.IMPOFF della ditta aggiudicataria
        DataColumnContainer dataColumncontainerDITG = new DataColumnContainer(
              this.getSqlManager(),"DITG",
              "select CODGAR5, DITTAO, NGARA5, RICSUB, IMPOFF from DITG " +
               "where DITTAO = ? and NGARA5 = ?", new Object[]{
                dataColumnContainerGARE.getColumn("GARE.PRIMAAGGIUDICATARIASELEZIONATA").getValue().getValue(),
                ngara});
        dataColumncontainerDITG.getColumn("DITG.CODGAR5").setChiave(true);
        dataColumncontainerDITG.getColumn("DITG.NGARA5").setChiave(true);
        dataColumncontainerDITG.getColumn("DITG.DITTAO").setChiave(true);


        if(impl.isColumn("DITG.RICSUB") || impl.isColumn("DITG.IMPOFF")){
          if (impl.isColumn("DITG.RICSUB")) {
            dataColumncontainerDITG.setValue("DITG.RICSUB", impl.getColumn("DITG.RICSUB").getValue());
          }
          if (impl.isColumn("DITG.IMPOFF")) {
            dataColumncontainerDITG.setValue("DITG.IMPOFF", impl.getColumn("DITG.IMPOFF").getValue());
          }
          dataColumncontainerDITG.update("DITG", this.sqlManager);
        }

        aggiudicazioneManager.aggiudicazioneDefinitiva(dataColumnContainerGARE, hMapConteggi);



        //Gestione GARE1
        AbstractGestoreEntita gestoreGARE1 = new DefaultGestoreEntita("GARE1", this.getRequest());
        gestoreGARE1.update(status, impl);

        if(modoRichiamo == null){
          this.setStopProcess(true);
          this.getRequest().setAttribute("modoRichiamo", "SUBAPPALTO_LAVORI");
          this.getRequest().setAttribute("PRIMAAGGIUDICATARIASELEZIONATA",
              impl.getColumn("PRIMAAGGIUDICATARIASELEZIONATA_1").getValue().getStringValue());
          //Gestione accordo quadtro più operatori
          if("2".equals(aqoper)){
            String[] ditteDefinitive = this.getRequest().getParameterValues("dittaDefinitiva");
            String elencoDitteSelezionate = "";
            for (int i = 0; i < ditteDefinitive.length; i++) {
              elencoDitteSelezionate += ditteDefinitive[i] + ";";
            }
            elencoDitteSelezionate = elencoDitteSelezionate.substring(0, elencoDitteSelezionate.length() - 1);
            this.getRequest().setAttribute("elencoDitteSelezionate", elencoDitteSelezionate);
          }
        }
        // ngara va settato in entrambe le modalità di richiamo della jsp
        this.getRequest().setAttribute("ngara", ngara);

        // Aggiornamento dell'occorrenza di GARE, rimuovendo prima i campi fittizi
        // quali PRIMAAGGIUDICATARIASELEZIONATA
        dataColumnContainerGARE.removeColumns(new String[]{"GARE.PRIMAAGGIUDICATARIASELEZIONATA"});
        dataColumnContainerGARE.update("GARE", this.getSqlManager());

        //Aggiornamento dati atto di aggiudicazione
        Long tattoa = null;
        Date dattoa = null;
        String nattoa = null;
        if("SUBAPPALTO_LAVORI".equals(modoRichiamo)){
          if(impl.isColumn("GARE.TATTOA"))
            tattoa = impl.getLong("GARE.TATTOA");
          if(impl.isColumn("GARE.DATTOA"))
            dattoa = impl.getData("GARE.DATTOA");
          if(impl.isColumn("GARE.NATTOA"))
            nattoa = impl.getString("GARE.NATTOA");

          //Se iterga=6 si deve impostare tiatto=8
          String update ="update gare set tattoa=?, dattoa=?, nattoa=?";
          Long iterga=(Long)this.sqlManager.getObject("select iterga from torn, gare where ngara=? and codgar1=codgar", new Object[]{ngara});
          if(iterga != null && iterga.longValue()==6 && !"2".equals(aqoper))
            update +=",tiatto=8";
          update +=" where ngara=?";

          this.sqlManager.update(update, new Object[] { tattoa,dattoa,nattoa,ngara });
        }

        String integrazioneCineca = ConfigManager.getValore("integrazioneAnagraficheUGOV");
        integrazioneCineca = UtilityStringhe.convertiNullInStringaVuota(integrazioneCineca);
        int msgCineca = 0;

        //Aggiornamento numero aggiudicazioni per selezione da elenco, se la ditta aggiudicataria è variata
        Long tipoalgo = null;
        String catiga=null;
        Long numcla=null;
        Long tipoGara = null;

        String codiceDitta= dataColumnContainerGARE.getString("DITTA");

        String codiceElenco="$"+elencoe;
        String stazioneAppaltante = null;

        if(elencoe!=null && !"".equals(elencoe) && !codiceDitta.equals(oldAggiudicataria) && dattoa!=null){
          //Si determina il tipo algo dell'elenco
          tipoalgo = (Long)sqlManager.getObject("select tipoalgo from garealbo where ngara=?", new Object[]{elencoe});
          tipoGara = (Long)sqlManager.getObject("select tipgen from torn where codgar=?", new Object[]{codgar});
          Vector datiCatg = this.sqlManager.getVector("select catiga, numcla from catg where ngara=? and ncatg=1", new Object[]{chiaveGara});
          if(datiCatg!=null && datiCatg.size()>0){
            catiga = (String)((JdbcParametro) datiCatg.get(0)).getValue();
            numcla = (Long)((JdbcParametro) datiCatg.get(1)).getValue();
          }

          String codiceDittaElenco = this.pgManager.getDittaSelezionataDaElenco(codiceDitta,chiaveGara);
          stazioneAppaltante = (String)this.sqlManager.getObject("select cenint from torn where codgar=?", new Object[]{codgar});
          if(codiceDittaElenco!=null){
            this.pgManager.aggiornaNumAggiudicazioni(codiceElenco, elencoe, codiceDittaElenco, catiga, tipoGara, numcla, stazioneAppaltante, tipoalgo, "INS");
          }
        }

        //Gestione DITGAQ
        if("SUBAPPALTO_LAVORI".equals(modoRichiamo) && "2".equals(aqoper)){
          String elencoDitteSelezionate = this.getRequest().getParameter("elencoDitteSelezionate");
          String listaDitte[] = elencoDitteSelezionate.split(";");
          String ditta = null;
          String codiceDittaElencoDitgaq=null;
          long numOrdine = 0;
          Long modlicg = impl.getLong("GARE.MODLICG");

          this.sqlManager.update("delete from ditgaq where ngara=?", new Object[]{ngara});
          AbstractGestoreChiaveIDAutoincrementante gestoreDITGAQ = new DefaultGestoreEntitaChiaveIDAutoincrementante(
              "DITGAQ", "ID", this.getRequest());

          String ridiso = "2";
          long tipgen = 0;

          HashMap hMapTORN = new HashMap();
          HashMap hMapGARE = new HashMap();
          // Inizializzazione valori letti da TORN
          aggiudicazioneManager.inizializzaTORN(codgar, hMapTORN);

          // Inizializzazione valori letti da GARE
          aggiudicazioneManager.inizializzaGARE(ngara, hMapGARE);

          // Inizializzazione valori letti da GARE1
          aggiudicazioneManager.inizializzaGareDaGare1(ngara, hMapGARE);

          if (hMapGARE.get("ridiso") != null)
            ridiso = (String) hMapGARE.get("ridiso");

          if (hMapTORN.get("tipgen") != null)
            tipgen = ((Long) hMapTORN.get("tipgen")).longValue();

          String coorba = null;
          String codbic = null;
          Vector datiBancari = null;

          for(int i=0;i<listaDitte.length;i++){
            ditta = listaDitte[i];
            numOrdine = i+1;
            Vector elencoCampi = new Vector();
            elencoCampi.add(new DataColumn("DITGAQ.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
            elencoCampi.add(new DataColumn("DITGAQ.DITTAO", new JdbcParametro(JdbcParametro.TIPO_TESTO, ditta)));
            elencoCampi.add(new DataColumn("DITGAQ.NUMORD", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(numOrdine))));
            Double ribasso = (Double)this.sqlManager.getObject("select ribauo from ditg where ngara5=? and dittao=?", new Object[]{ngara,ditta});
            if(new Long(6).equals(modlicg))
              elencoCampi.add(new DataColumn("DITGAQ.PUNTOT", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, ribasso)));
            else
              elencoCampi.add(new DataColumn("DITGAQ.RIBAGG", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, ribasso)));

            HashMap hMapParametri = new HashMap();
            hMapParametri.put("primaAggiudicatariaSelezionata",ditta);
            aggiudicazioneManager.calcolaImportoAggiudicazione(ngara, hMapTORN, hMapGARE, hMapParametri);

            double iaggiu = 0;
            double ribauo = 0;
            if (hMapParametri.get("iaggiu") != null)
              iaggiu = ((Double) hMapParametri.get("iaggiu")).doubleValue();
            if (hMapParametri.get("ribauo") != null)
              ribauo = ((Double) hMapParametri.get("ribauo")).doubleValue();

            elencoCampi.add(new DataColumn("DITGAQ.IAGGIU", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Double(iaggiu))));

            // Calcolo il valore del ribasso dall'importo offerto
            // In questa modalità "Offerta Economicamente più vantaggiosa" il
            // campo RIBAUO riporta il punteggio e non il ribasso
            if (new Long(6).equals(modlicg)) {
                double riboepv = 0;

                if (hMapParametri.get("riboepv") != null){
                  riboepv = ((Double) hMapParametri.get("riboepv")).doubleValue();
                  ribauo = riboepv;
                }
                
                elencoCampi.add(new DataColumn("DITGAQ.RIBAGG", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, riboepv)));
            }

            if(hMapGARE.get("impapp")!=null)
              iaggiu = ((Double) hMapGARE.get("impapp")).doubleValue();
            else
              iaggiu=0;

            double impgar = aggiudicazioneManager.calcolaImportoGaranzia(iaggiu, ribauo, tipgen,
                ridiso.equals("1"));

            elencoCampi.add(new DataColumn("DITGAQ.IMPGAR", new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Double(impgar))));

            //Coordinate bancarie
            coorba = null;
            codbic = null;
            datiBancari = this.sqlManager.getVector("select coorba,codbic from impr where codimp=?", new Object[]{ditta});
            if(datiBancari!=null && datiBancari.size()>0){
              coorba=SqlManager.getValueFromVectorParam(datiBancari, 0).stringValue();
              codbic=SqlManager.getValueFromVectorParam(datiBancari, 1).stringValue();
            }
            elencoCampi.add(new DataColumn("DITGAQ.COORBA", new JdbcParametro(JdbcParametro.TIPO_TESTO, coorba)));
            elencoCampi.add(new DataColumn("DITGAQ.CODBIC", new JdbcParametro(JdbcParametro.TIPO_TESTO, codbic)));

            DataColumnContainer containerDITGAG = new DataColumnContainer(elencoCampi);
            gestoreDITGAQ.inserisci(status, containerDITGAG);

            //Aggiornamento del numero di aggiudicazioni, si deve considerare che nel campo DITG.GARE viene inserito casualmente una fra le ditte aggiudicatarie,
            //ditta che poi sarà presente anche in DITGAQ, quindi quando si effettua il conteggio, tale ditta va considerata una solo volta
            if(elencoe!=null && !"".equals(elencoe) && dattoa!=null  && !codiceDitta.equals(ditta)){
              codiceDittaElencoDitgaq = this.pgManager.getDittaSelezionataDaElenco(ditta,chiaveGara);
              if(codiceDittaElencoDitgaq!=null){
                this.pgManager.aggiornaNumAggiudicazioni(codiceElenco, elencoe, codiceDittaElencoDitgaq, catiga, tipoGara, numcla, stazioneAppaltante, tipoalgo, "INS");
              }
            }

            String dittaCineca = containerDITGAG.getString("DITTAO");

            //inizio integrazione Cineca
            if("1".equals(integrazioneCineca) && dittaCineca != null){
              try {
                //verifica che non si tratti un RTI o ....
                Vector<?> naturaImprVect = this.sqlManager.getVector("select TIPIMP,NATGIUI from IMPR where CODIMP = ?", new Object[] { dittaCineca });
                Long tipimp= null;
                Long natgiui = null;
                if (naturaImprVect != null && naturaImprVect.size() > 0) {
                  tipimp = (Long) SqlManager.getValueFromVectorParam(naturaImprVect, 0).getValue();
                  natgiui = (Long) SqlManager.getValueFromVectorParam(naturaImprVect, 1).getValue();
                }
                if(tipimp != null && (new Long(3).equals(tipimp) || new Long(2).equals(tipimp) || new Long(10).equals(tipimp) || new Long(11).equals(tipimp))){
                  List ditteComponentiCineca  = this.sqlManager.getListVector(
                      "select CODDIC,NOMDIC from RAGIMP where CODIME9 = ? ", new Object[] { dittaCineca });
                  if (ditteComponentiCineca != null && ditteComponentiCineca.size() > 0) {
                    for (int k = 0; k < ditteComponentiCineca.size(); k++) {
                      Vector tmp = (Vector) ditteComponentiCineca.get(k);
                      String dittaComponenteCineca = ((JdbcParametro) tmp.get(0)).getStringValue();
                      String[] resMsg = cinecaWSManager.gestioneDittaCineca(this.getRequest(),dittaComponenteCineca,tipimp,natgiui);
                      if(resMsg[0]!= null){
                        int res = Integer.parseInt(resMsg[0]);
                        if(res <0){
                          String msg = "";
                          if(res == -2){
                            msg = " - Codice Fiscale o Partita IVA non validi!";
                          }else if(res == -19){
                            msg = " - Non risultano valorizzati i seguenti dati obbligatori: \n" + resMsg[1];

                          }else{
                            msg = resMsg[1];
                          }
                          msgCineca = 1;
                          UtilityStruts.addMessage(this.getRequest(), "warning",
                              "warnings.cineca.mancataIntegrazione",
                              new Object[] { dittaCineca,msg });
                        }
                      }
                    }
                  }
                }
                if(!(tipimp != null && (new Long(3).equals(tipimp) || new Long(10).equals(tipimp)))){
                  String[] resMsg = cinecaWSManager.gestioneDittaCineca(this.getRequest(),dittaCineca,tipimp,natgiui);
                  if(resMsg[0]!= null){
                    int res = Integer.parseInt(resMsg[0]);
                    if(res <0){
                      String msg = "";
                      if(res == -2){
                        msg = " - Codice Fiscale o Partita IVA non validi!";
                      }else if(res == -19){
                        msg = " - Non risultano valorizzati i seguenti dati obbligatori: \n" + resMsg[1];
                      }else{
                        msg = resMsg[1];
                      }
                      msgCineca = 1;
                      UtilityStruts.addMessage(this.getRequest(), "warning",
                          "warnings.cineca.mancataIntegrazione",
                          new Object[] { dittaCineca, msg });
                    }
                  }
                }



              } catch (SQLException e) {
                throw new GestoreException(
                    "Errore nell'integrazione con Cineca ","aggiornaImplGAREFaseB", e);
              }

            }//fine integrazione Cineca

          }
        }

        //Gestione GARECONT


        String dittaAggiud = dataColumnContainerGARE.getString("GARE.DITTA");
        //Long genere = (Long) this.sqlManager.getObject("select genere from gare where ngara=?", new Object[]{codgar});

          //  inizio integrazione Cineca
          if("1".equals(integrazioneCineca) && dittaAggiud != null && "SUBAPPALTO_LAVORI".equals(modoRichiamo) && !"2".equals(aqoper)){
            String dittaCineca = dittaAggiud;
            try {
              //verifica che non si tratti un RTI o ....
              Vector<?> naturaImprVect = this.sqlManager.getVector("select TIPIMP,NATGIUI from IMPR where CODIMP = ?", new Object[] { dittaCineca });
              Long tipimp= null;
              Long natgiui = null;
              if (naturaImprVect != null && naturaImprVect.size() > 0) {
                tipimp = (Long) SqlManager.getValueFromVectorParam(naturaImprVect, 0).getValue();
                natgiui = (Long) SqlManager.getValueFromVectorParam(naturaImprVect, 1).getValue();
              }
              if(tipimp != null && (new Long(3).equals(tipimp) || new Long(2).equals(tipimp) || new Long(10).equals(tipimp) || new Long(11).equals(tipimp))){
                List ditteComponentiCineca  = this.sqlManager.getListVector(
                    "select CODDIC,NOMDIC from RAGIMP where CODIME9 = ? ", new Object[] { dittaCineca });
                if (ditteComponentiCineca != null && ditteComponentiCineca.size() > 0) {
                  for (int k = 0; k < ditteComponentiCineca.size(); k++) {
                    Vector tmp = (Vector) ditteComponentiCineca.get(k);
                    String dittaComponenteCineca = ((JdbcParametro) tmp.get(0)).getStringValue();
                    String[] resMsg = cinecaWSManager.gestioneDittaCineca(this.getRequest(),dittaComponenteCineca,tipimp,natgiui);
                    if(resMsg[0]!= null){
                      int res = Integer.parseInt(resMsg[0]);
                      if(res <0){
                        String msg = "";
                        if(res == -2){
                          msg = " - Codice Fiscale o Partita IVA non validi!\n";
                        }else if(res == -19){
                          msg = " - Non risultano valorizzati i seguenti dati obbligatori: \n" + resMsg[1];
                        }else{
                          msg = resMsg[1];
                        }
                        msgCineca = 1;
                        UtilityStruts.addMessage(this.getRequest(), "warning",
                            "warnings.cineca.mancataIntegrazione",
                            new Object[] { dittaComponenteCineca, msg });
                      }
                    }
                  }
                }
              }
              if(!(tipimp != null && (new Long(3).equals(tipimp) || new Long(10).equals(tipimp)))){
                String[] resMsg = cinecaWSManager.gestioneDittaCineca(this.getRequest(),dittaCineca,tipimp,natgiui);
                if(resMsg[0]!= null){
                  int res = Integer.parseInt(resMsg[0]);
                  if(res <0){
                    String msg = "";
                    if(res == -2){
                      msg = " - Codice Fiscale o Partita IVA non validi!\n";
                      UtilityStruts.addMessage(this.getRequest(), "warning",
                          "warnings.cineca.mancataIntegrazione",
                          new Object[] { dittaCineca,msg });
                    }else if(res == -19){
                      msg = " - Non risultano valorizzati i seguenti dati obbligatori: \n" + resMsg[1];
                      UtilityStruts.addMessage(this.getRequest(), "warning",
                          "warnings.cineca.mancataIntegrazione",
                          new Object[] { dittaCineca,msg });
                    }else if(res == -5){
                        msg = "";
                        UtilityStruts.addMessage(this.getRequest(), "warning",
                            "warnings.cineca.mancataIntegrazioneCoordPag.warning",
                            new Object[] {msg});
                    }else{
                        msg = resMsg[1];
                        UtilityStruts.addMessage(this.getRequest(), "warning",
                            "warnings.cineca.mancataIntegrazione",
                            new Object[] {dittaCineca,msg});
                    }

                    msgCineca = 1;
                  }

                }
              }

            } catch (SQLException e) {
              throw new GestoreException(
                  "Errore nell'integrazione con Cineca ","aggiornaImplGAREFaseB", e);
            }
          }//fine integrazione Cineca

        if(genere!=null && genere.longValue()==3){
          Long modcont = null;
          String accqua = null;
          Long altrisog = null;
          Vector datiTorn = this.sqlManager.getVector("select modcont, accqua, altrisog from torn where codgar=?", new Object[]{codgar});
          if(datiTorn!=null && datiTorn.size()>0){
            modcont = SqlManager.getValueFromVectorParam(datiTorn, 0).longValue();
            accqua = SqlManager.getValueFromVectorParam(datiTorn, 1).stringValue();
            altrisog = SqlManager.getValueFromVectorParam(datiTorn, 2).longValue();
          }

          if((new Long(2)).equals(modcont)){
            //Si devono cancelare le occorrenze di garecont con codimp nullo oppure associato a ditte che non sono aggiudicatarie
            //definitive di nessun lotto della gara
            aggiudicazioneManager.cancellaGarecontOffertaUnica(codgar);
          }else if((new Long(1)).equals(modcont)){
            //Si cancella l'occorrenza con NGARAL e CODIMP nullo, (ovvero la prima occ. fittizia inserita in inizializzazione della gara)
            this.sqlManager.update(
                "delete from garecont where ngara=? and codimp is null and ngaral is null",
                new Object[] { codgar });
          }

          //Lettura del campo COORBA di impr della ditta aggiudicataria
          String coorba = null;
          String codbic = null;
          Vector datiBancari = this.sqlManager.getVector("select coorba,codbic from impr where codimp=?", new Object[]{dittaAggiud});
          if(datiBancari!=null && datiBancari.size()>0){
            coorba=SqlManager.getValueFromVectorParam(datiBancari, 0).stringValue();
            codbic=SqlManager.getValueFromVectorParam(datiBancari, 1).stringValue();
          }

          //Se non esiste già un'occorrenza di GARECONT con NGARA uguale a quello dell'occorrenza complementare
          //e CODIMP uguale a quello della ditta aggiudicataria la devo creare.

          Long count = null;
          if((new Long(2)).equals(modcont))
            count=(Long)this.sqlManager.getObject("select count(ngara) from garecont where ngara=? and codimp=?", new Object[]{codgar,dittaAggiud});
          else
            count=(Long)this.sqlManager.getObject("select count(ngara) from garecont where ngara=? and ngaral=?", new Object[]{codgar, ngara});
          Long ncont = null;
          if(count==null || ( count!=null && count.longValue()==0)){
            ncont=(Long)this.sqlManager.getObject("select max(ncont) from garecont where ngara=?", new Object[]{codgar});
            if(ncont==null)
              ncont=new Long(1);
            else
              ncont = new Long (ncont.longValue() + 1);

            String ngaral=null;
            if((new Long(1)).equals(modcont))
              ngaral = ngara;
            if("2".equals(aqoper))
              this.sqlManager.update(
                "insert into garecont(ngara,ncont,codimp,ngaral) values(?,?,?,?)",
                new Object[] { codgar, ncont,dittaAggiud,ngaral});
            else
              this.sqlManager.update(
                  "insert into garecont(ngara,ncont,codimp,coorba,codbic,ngaral) values(?,?,?,?,?,?)",
                  new Object[] { codgar, ncont,dittaAggiud,coorba,codbic,ngaral});
          }else{
            if((new Long(2)).equals(modcont))
              ncont=(Long)this.sqlManager.getObject("select ncont from garecont where ngara=? and codimp=?", new Object[]{codgar,dittaAggiud});
            else{
              ncont=(Long)this.sqlManager.getObject("select ncont from garecont where ngara=? and ngaral=?", new Object[]{codgar, ngara});
              if("2".equals(aqoper))
                this.sqlManager.update("update garecont set codimp=? where ngara=? and ncont=?", new Object[]{dittaAggiud, codgar, ncont});
              else
                this.sqlManager.update("update garecont set codimp=?, coorba =?, codbic=? where ngara=? and ncont=?", new Object[]{dittaAggiud, coorba, codbic, codgar, ncont});
            }
          }


          //Se accqua=1 si deve procedere alla valorizzazione del campo GARECONT.IMPQUA
          if("1".equals(accqua)){
            Double impapp=null;
            String esecscig = null;
            String contspe = null;

            if((new Long(2)).equals(modcont)){
              Object importo = this.sqlManager.getObject("select sum(impapp) from gare where codgar1=? and ditta=?", new Object[]{codgar, dittaAggiud});
              if(importo!=null){
                if (importo instanceof Long)
                  impapp = new Double(((Long) importo));
                else if (importo instanceof Double)
                  impapp = new Double((Double) importo);
              }
            }else{
              impapp = (Double)this.sqlManager.getObject("select impapp from gare where ngara=?", new Object[]{ngara});
            }

            //gestione esecuzione contratto
            Vector datiGarecont = this.sqlManager.getVector("select esecscig,contspe from garecont where ngara=? and ncont=?",new Object[]{codgar, ncont});
            if(datiGarecont!=null && datiGarecont.size() >0){
              esecscig = SqlManager.getValueFromVectorParam(datiGarecont, 0).getStringValue();
              esecscig = UtilityStringhe.convertiNullInStringaVuota(esecscig);
              contspe = SqlManager.getValueFromVectorParam(datiGarecont, 1).getStringValue();
            }

            if("1".equals(aqoper) && !(new Long(3).equals(altrisog))){
              if("".equals(esecscig)){
                esecscig="1";
              }
            }else{
              esecscig = null;
            }

            if(!"1".equals(esecscig) && (contspe == null || "".equals(contspe)))
              contspe = aggiudicazioneManager.getValoreInizializzazioneContspe();
            else if("1".equals(esecscig))
              contspe = null;
            this.sqlManager.update("update garecont set impqua=?, esecscig =?, contspe =?  where ngara=? and ncont=?", new Object[]{impapp, esecscig, contspe, codgar, ncont});

            //Nel caso di MODCONT=2 si può presentare la situazione che una ditta inizialmente si è aggiudicata due lotti, poi
            //si cambia l'aggiudicataria di uno dei due lotti, allora viene inserita correttamente la nuova occorrenza per la
            //nuova aggiudicataria in GARECONT, ma si deve aggiornare l'importo GARECONT.IMPQUA della ditta che prima si era
            //aggiudicata entrambi i lotti.
            if(oldAggiudicataria!=null && !oldAggiudicataria.equals(dittaAggiud) && (new Long(2)).equals(modcont)){
              Object importo = this.sqlManager.getObject("select sum(impapp) from gare where codgar1=? and ditta=?", new Object[]{codgar, oldAggiudicataria});
              if(importo!=null){
                if (importo instanceof Long)
                  impapp = new Double(((Long) importo));
                else if (importo instanceof Double)
                  impapp = new Double((Double) importo);
                this.sqlManager.update("update garecont set impqua =? where ngara=? and codimp=?", new Object[]{impapp, codgar, oldAggiudicataria});
              }

            }
          }else{
            this.sqlManager.update("update garecont set esecscig =?, contspe = ? where ngara=? and ncont=?", new Object[]{null, null, codgar, ncont});
          }

          if("SUBAPPALTO_LAVORI".equals(modoRichiamo)){
            if(elencoe!=null && !"".equals(elencoe)){
              //Devo prelevare il valore originale della dattoa!!
              Timestamp dattoaOrig = impl.getColumn("GARE.DATTOA").getOriginalValue().dataValue();
              //Aggiornamento del numero di aggiudicazioni su tutti i lotti escluso il lotto corrente
              if(dattoaOrig==null && dattoa!=null){
                this.pgManager.aggiornaNumAggiudicazioniLotti(codiceElenco, elencoe, codgar, ngara, catiga, tipoGara, numcla, stazioneAppaltante, tipoalgo, "INS");
              }
            }
            //Allineamento dei dati dell'atto in tutti i lotti
            this.sqlManager.update(
                "update gare set tattoa=?, dattoa=?, nattoa=? where codgar1=? and ngara<>?",
                new Object[] { tattoa,dattoa,nattoa,codgar,ngara });
          }
        }

        //Aggiornamento della data ultimo aggiornamento
        String pubblicazioneBandoPortale = pgManagerEst1.esisteBloccoPubblicazionePortale(codgar, "BANDO", false);
        String pubblicazioneEsitoPortale = pgManagerEst1.esisteBloccoPubblicazionePortale(codgar, "ESITO", true);
        if("TRUE".equals(pubblicazioneBandoPortale) || "TRUE".equals(pubblicazioneEsitoPortale)){
          java.util.Date oggi = UtilityDate.getDataOdiernaAsDate();
          this.sqlManager.update("update TORN set DULTAGG = ? where CODGAR=?",new Object[]{oggi,codgar});
        }

        if("SUBAPPALTO_LAVORI".equals(modoRichiamo)){
          this.getRequest().setAttribute("RISULTATO", "CALCOLOESEGUITO");
          if(msgCineca == 1){
            this.getRequest().setAttribute("RISULTATO", "CALCOLOCONWARNING");
          }
        }else{
          this.getRequest().setAttribute("RISULTATO", "CALCOLOINTERROTTO");
        }


        //best case
        livEvento = 1;
        dittaAggiud = UtilityStringhe.convertiNullInStringaVuota(dittaAggiud);
        if(!"".equals(dittaAggiud)){
          descrDitta = "(ditta " + dittaAggiud + ")";
        }
        errMsgEvento = "";

      } catch (Throwable e) {
        livEvento = 3;
        messageKey = "errors.gestoreException.*.aggiudicazioneFaseB-Definitva";
        errMsgEvento = this.resBundleGenerale.getString(messageKey);
        this.getRequest().setAttribute("RISULTATO", "ERRORI");
        throw new GestoreException(
            "Errore durante il calcolo dell'aggiudicazione definitiva",
            "aggiudicazioneFaseB-Definitva", e);
      }

    }finally{
      if("SUBAPPALTO_LAVORI".equals(modoRichiamo)){
        //Tracciatura eventi
        try {
          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(ngara);
          logEvento.setCodEvento("GA_AGGIUDICAZIONE_DEF");
          logEvento.setDescr("Aggiudicazione definitiva della gara " + descrDitta);
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          messageKey = "errors.logEventi.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), le);
        }
      }
    }
  }


}