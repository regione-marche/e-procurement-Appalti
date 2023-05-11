/*
 * Created on 14/feb/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.importData.CampoType;
import it.eldasoft.importData.DatiAliceDocument;
import it.eldasoft.importData.DatiAliceType;
import it.eldasoft.importData.OggettoType;
import it.eldasoft.importData.RecordType;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.properties.ConfigManager;

/**
 * Action per la gestione della creazione e download del file in formato XML per verifica e accertamenti antimafia.
 *
 * @author Sara.Santi
 */
public class EsportaAntimafiaAction extends ActionBaseNoOpzioni {

  static Logger      logger = Logger.getLogger(EsportaAntimafiaAction.class);

  /** Manager generico per l'accesso ai dati */
  private SqlManager sqlManager;

  /**
   * @param sqlManager
   *        sqlManager da settare internamente alla classe.
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts
   * .action.ActionMapping, org.apache.struts.action.ActionForm,
   * javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    String messageKey = null;
    String target = null;

    String codiceGara = request.getParameter("codiceGara");
    String tipoRichiesta = request.getParameter("tipoRichiesta");
    String islottoGara= request.getParameter("islottoGara");
    String moduloAttivo = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);

    String nomefileExport = null;
    DatiAliceDocument document = DatiAliceDocument.Factory.newInstance();
    document.documentProperties().setEncoding("UTF-8");
    DatiAliceType root = document.addNewDatiAlice();
    root.setData(new GregorianCalendar());

    OggettoType oggetto = null;

    // FASE 1: estrazione dei dati
     try {
       if ("VERIFICA".equals(tipoRichiesta)) {
         //Tipo export = "VERIFICA_INTERDIZIONE"
         //Recupera dati dall'anagrafico imprese delle ditte partecipanti alla gara.
         //Se una ditta è ATI, invece della ditta stessa, considera le ditte componenti il raggruppamento.
         //Il campo calcolato 'componente_ati' contiene 0 se la ditta non è un ATI, 1 se la ditta è componente di un ATI
         List listaDitte = sqlManager.getListHashMap(
            "select distinct i_comp.codimp, i_comp.nomimp, i_comp.cfimp, i_comp.pivimp, i_comp.locimp,"
            + " (case when i_princ.codimp = i_comp.codimp then 0 else 1 end) componente_ati"
            + " from impr i_princ,ditg,impr i_comp"
            + " where ngara5=? and dittao=i_princ.codimp and"
            + " (((i_princ.tipimp is null or (i_princ.tipimp <>3 and i_princ.tipimp <>10)) and i_princ.codimp=i_comp.codimp) or"
            + " ((i_princ.tipimp=3 or i_princ.tipimp=10) and i_comp.codimp in (select coddic from ragimp where i_princ.codimp=codime9)))",
            new Object[] { codiceGara });

         if (listaDitte != null && listaDitte.size() > 0) {
           nomefileExport="verifica-interdizione.xml";
           root.setTipo("VERIFICA INTERDIZIONE");
           root.setVersion("1.0.0");

           //Carica la versione del DB
           oggetto = root.addNewOggetto();
           this.caricaVersioneDB(moduloAttivo, oggetto);

           oggetto = root.addNewOggetto();
           this.caricaRecord(listaDitte, oggetto, "IMPR");

         } else {
           target = "errore";
           if ("true".equals(islottoGara)){
             messageKey = "errors.exportAntimafia.ListaDitteVuota.lotto";
           } else {
             messageKey = "errors.exportAntimafia.ListaDitteVuota.gara";
           }
           this.aggiungiMessaggio(request, messageKey);
         }

       } else {
           //Tipo export = "ACCERTAMENTO_ANTIMAFIA"
           //recupera i dati anagrafici della ditta aggiudicataria provvisoria della gara
           List datiDittaAgg= sqlManager.getListHashMap(
               "select impr.* from impr,gare where codimp=dittap and ngara=? ",
               new Object[] { codiceGara });

           if (datiDittaAgg != null && datiDittaAgg.size() > 0) {
             nomefileExport="accertamento-antimafia.xml";
             root.setTipo("ACCERTAMENTO ANTIMAFIA");
             root.setVersion("1.0.0");

             //Carica la versione del DB
             oggetto = root.addNewOggetto();
             this.caricaVersioneDB(moduloAttivo, oggetto);

             //Recupera il codice e la tipologia (Impresa semplice, ATI, Consorzio) della ditta aggiudicataria
             HashMap hashDitta = (HashMap) datiDittaAgg.get(0);
             JdbcParametro campoJDBC = (JdbcParametro) hashDitta.get("CODIMP");
             String codiceDitta = campoJDBC.getStringValue();
             campoJDBC = (JdbcParametro) hashDitta.get("TIPIMP");
             Long tipoDitta = campoJDBC.longValue();

             //Dati anagrafici ditta aggiudicataria provvisoria
             oggetto = root.addNewOggetto();
             this.caricaRecord(datiDittaAgg, oggetto, "IMPR");

             //Dati entità figlie della ditta aggiudicataria provvisoria (IMPLEG, IMPDTE, IMPAZI, IMPIND)
             this.caricaEntitaFiglie(codiceDitta,oggetto);

             //Carica un'occorrenza fittizia per l'entità IMPANTIMAFIA, solo nel caso la ditta non sia un ATI
             if (tipoDitta == null || (tipoDitta.longValue()!=3 && tipoDitta.longValue()!=10)) {
               this.caricaImpantimafia(codiceDitta, codiceGara, oggetto);
             }

             //Carica i tecnici impresa collegati a Legali rappresentanti, Direttori tecnici e Azionisti
             this.caricaTecniciImpresa(codiceDitta,oggetto);

             //Recupera lista delle ditte componenti il raggruppamento
             List datiRagimp=sqlManager.getListHashMap(
                 "select * from ragimp where codime9=?", new Object[] { codiceDitta });
             this.caricaRecord(datiRagimp, oggetto, "RAGIMP");

             for (int i = 0; i < datiRagimp.size(); i++) {
               HashMap hashRagimp = (HashMap) datiRagimp.get(i);
               campoJDBC = (JdbcParametro) hashRagimp.get("CODDIC");
               String codiceDittaRaggruppamento = campoJDBC.getStringValue();

               oggetto = root.addNewOggetto();

               List datiDittaRaggruppamento= sqlManager.getListHashMap(
                   "select * from impr where codimp=? ",
                   new Object[] { codiceDittaRaggruppamento });

               //Dati anagrafici ditta componente il raggruppamento
               this.caricaRecord(datiDittaRaggruppamento, oggetto, "IMPR");

               //Dati entità figlie della ditta componente il raggruppamento
               this.caricaEntitaFiglie(codiceDittaRaggruppamento,oggetto);

               //Se la ditta principale è un ATI, carica un'occorrenza fittizia di IMPANTIMAFIA per ogni ditta del raggruppamento
               if (tipoDitta != null && (tipoDitta.longValue()==3 || tipoDitta.longValue()==10)) {
                 this.caricaImpantimafia(codiceDittaRaggruppamento, codiceGara, oggetto);
               }

               //Carica i tecnici impresa collegati a Legali rappresentanti, Direttori tecnici e Azionisti
               this.caricaTecniciImpresa(codiceDittaRaggruppamento,oggetto);

             }
           } else {
             target = "errore";
             if ("true".equals(islottoGara)){
               messageKey = "errors.exportAntimafia.GaraNonAggiudicata.lotto";
             } else {
               messageKey = "errors.exportAntimafia.GaraNonAggiudicata.gara";
             }
             this.aggiungiMessaggio(request, messageKey);
           }
         }

     } catch (SQLException e) {
       target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
       messageKey = "errors.database.dataAccessException";
       this.aggiungiMessaggio(request, messageKey);
       logger.error(this.resBundleGenerale.getString(messageKey), e);
     } catch (Throwable t) {
       target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
       messageKey = "errors.applicazione.inaspettataException";
       logger.error(this.resBundleGenerale.getString(messageKey), t);
       this.aggiungiMessaggio(request, messageKey);
   }

    // FASE 3: popolamento della response con l'xml
    if (target == null) {
      response.setContentType("application/octet-stream");
      response.setCharacterEncoding("UTF-8");
      response.setHeader("Content-Disposition","attachment;filename=" + nomefileExport);
      response.setContentLength(document.toString().getBytes("UTF-8").length);
      OutputStream out = response.getOutputStream();
      Writer writer = new OutputStreamWriter(out, "UTF-8");
      writer.write(document.toString());
      writer.flush();
      writer.close();
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    if (target != null)
      return mapping.findForward(target);
    else
      return null;
  }

  private void caricaVersioneDB(String moduloAttivo, OggettoType oggetto)
  throws SQLException{

    List datiVersioneDB=sqlManager.getListHashMap(
        "select codapp, numver from eldaver where codapp=?", new Object[] {moduloAttivo});
    this.caricaRecord(datiVersioneDB, oggetto, "ELDAVER");

  }

  private void caricaEntitaFiglie(String codiceDitta, OggettoType oggetto)
        throws SQLException{

    String[] elencoTabelle = {"IMPLEG", "IMPDTE", "IMPAZI", "IMPIND","G_IMPCOL"};
    String[] elencoWhere = {"codimp2=?", "codimp3=?", "codimp4=?", "codimp5=?", "codimp=?"};

    for (int i = 0; i < elencoTabelle.length; i++) {
      List datiEntitaFiglie=sqlManager.getListHashMap(
          "select * from " + elencoTabelle[i] + " where " + elencoWhere[i],
          new Object[] { codiceDitta });
      this.caricaRecord(datiEntitaFiglie, oggetto, elencoTabelle[i]);
    }
  }

  private void caricaImpantimafia(String codiceDitta, String codiceGara, OggettoType oggetto)
  throws SQLException{

    String db =ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);
    String select="select '" + codiceDitta + "' codimp, 1 tipric, tipgen, impapp, 1 entric, ngara, codcig, not_gar " +
      "from gare,torn where gare.ngara=? and gare.codgar1=torn.codgar";
    if("POS".equals(db))
      select="select '" + codiceDitta + "'::text codimp, 1 tipric, tipgen, impapp, 1 entric, ngara, codcig, not_gar " +
      "from gare,torn where gare.ngara=? and gare.codgar1=torn.codgar";


    List datiImpantimafia=sqlManager.getListHashMap(select, new Object[] { codiceGara });
    this.caricaRecord(datiImpantimafia, oggetto, "IMPANTIMAFIA");
  }

  private void caricaTecniciImpresa(String codiceDitta, OggettoType oggetto)
    throws SQLException{

    List datiTecnici=sqlManager.getListHashMap(
        "select * from teim where exists (select codleg from impleg where codimp2=? and codleg = codtim) " +
        "or exists (select coddte from impdte where codimp3=? and coddte=codtim) " +
        "or exists (select codtec from impazi where codimp4=? and codtec=codtim) " +
        "or exists (select codtec from g_impcol where codimp=? and codtec=codtim)",
        new Object[] {codiceDitta, codiceDitta, codiceDitta, codiceDitta});
    this.caricaRecord(datiTecnici, oggetto, "TEIM");
  }


  private void caricaRecord(List listaDati, OggettoType oggetto, String nomeTabella){
    RecordType record = null;
    CampoType campo = null;

    DizionarioCampi dizCampi = DizionarioCampi.getInstance();
    Campo campodiz = null;

    for (int i = 0; i < listaDati.size(); i++) {

      HashMap hashDati = (HashMap) listaDati.get(i);
      Set setCampi = hashDati.keySet();
      Iterator iter = setCampi.iterator();

      record = oggetto.addNewRecord();
      record.setNome(nomeTabella);

      while(iter.hasNext()){
        String nomeCampo = (String) iter.next();
        JdbcParametro campoJDBC = (JdbcParametro) hashDati.get(nomeCampo);
        String valoreCampo = campoJDBC.getStringValue();
        char tipoCampo = campoJDBC.getTipo();

        campo = record.addNewCampo();
        campo.setNome(nomeCampo);
        campo.setValore(valoreCampo);
        campo.setTipo(""+tipoCampo);

        campodiz = dizCampi.getCampoByNomeFisico(nomeTabella + "." + nomeCampo);
        if (campodiz != null){
          campo.setChiave(campodiz.isCampoChiave());
        } else {
          campo.setChiave(false);
        }
      }
    }

  }

}


