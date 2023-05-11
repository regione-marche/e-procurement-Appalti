/*
 * Created on 28/07/2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityStringhe;
import net.sf.json.JSONObject;

public class CodificaAnonimaDitteAction extends Action {

  static Logger       logger = Logger.getLogger(CodificaAnonimaDitteAction.class);

  private static final String     PREFISSO_CODIFICA       = "operatore";

  private MEPAManager mepaManager;

  private PgManager             pgManager;

  private GeneManager         geneManager;

  private SqlManager          sqlManager;

  private AggiudicazioneManager          aggiudicazioneManager;

  public void setmepaManager(MEPAManager mepaManager) {
    this.mepaManager = mepaManager;
  }

  public void setpgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }

  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setAggiudicazioneManager(AggiudicazioneManager aggiudicazioneManager) {
    this.aggiudicazioneManager = aggiudicazioneManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

    JSONObject result = new JSONObject();
    String ngara = request.getParameter("ngara");


    //variabili per tracciatura eventi
    String genericMsgErr = "Errore inaspettato durante la tracciatura su w_logeventi";
    int livEvento = 3;
    String errMsgEvento = genericMsgErr;

    String messaggioPerLog = "";
    boolean erroreGestito = false;
    String codEvento = "GA_OEPV_ASSEGNA_IDANONIMO";
    String descrEvento = "Assegna identificativo ditta per valutazione anonima busta tecnica";

    TransactionStatus status = null;
    boolean commitTransaction = false;



    String messageKey="";

    try{

      commitTransaction = false;
      status = this.sqlManager.startTransaction();

      //La funzione può essere lanciata più volte, quindi quando si crea il progressivo, si deve tenere conto del fatto che la funzione sia stata già lanciata
      String selectDitteConId = "select count(dittao) from ditg "
          + "left outer join ragimp on (codime9=dittao and impman='1') "
          + "inner join w_puser on (userkey1=dittao or (userkey1=coddic and impman='1')) "
          + "inner join w_invcom on (comkey1=usernome and comkey2=ngara5 and (comkey3 = ncomope or comkey3 is null) and comtipo='FS11B') "
          + "where ngara5=? and idanonimo is not null";
      Long numDitteConIdCreato =(Long)this.sqlManager.getObject(selectDitteConId, new Object[] {ngara});
      if(numDitteConIdCreato==null)
        numDitteConIdCreato = new Long(0);

      String selectDitte = "select dittao from ditg "
          + "left outer join ragimp on (codime9=dittao and impman='1') "
          + "inner join w_puser on (userkey1=dittao or (userkey1=coddic and impman='1')) "
          + "inner join w_invcom on (comkey1=usernome and comkey2=ngara5 and (comkey3 = ncomope or comkey3 is null) and comtipo='FS11B' and comstato='13') "
          + "where ngara5=? and idanonimo is null order by numordpl";

      int numope=0;
      String DitteAnonimizzate = "";
      List listaDitte = this.sqlManager.getListVector(selectDitte, new Object[]{ngara});
      if(listaDitte!=null && listaDitte.size()>0){
        String elenco="";
        numope=listaDitte.size();
        String ditta=null;

        for(int i=0;i<listaDitte.size(); i++){
          ditta=SqlManager.getValueFromVectorParam(listaDitte.get(i), 0).stringValue();
          elenco+=ditta;
          if(i < listaDitte.size() - 1)
            elenco+=",";
        }
        HashMap hMapDatiSelezione = new HashMap();
        DitteAnonimizzate = new String(elenco);
        this.aggiudicazioneManager.selezioneCasualeParimerito("3", elenco,numope, hMapDatiSelezione);
        if(hMapDatiSelezione.get("listaParimeritoSelezionate")!=null){
          elenco = (String)hMapDatiSelezione.get("listaParimeritoSelezionate");
          //Elenco è una stringa che contiene i codici delle ditte sorteggiate, separati da ,
          String vetCodDitte[] = elenco.split(",");
          String idCifrato=null;
          if(vetCodDitte!=null && vetCodDitte.length>0){
            for(int j=0; j < vetCodDitte.length; j++){
              ditta= vetCodDitte[j];
              idCifrato = this.composizioneIdDittaCifrato(ngara, ditta, numDitteConIdCreato.intValue() + j + 1);
              this.sqlManager.update("update ditg set idanonimo=? where ngara5=? and dittao=?", new Object[]{idCifrato,ngara,ditta});
            }
          }
          if(DitteAnonimizzate.indexOf(",,")>=0){
            DitteAnonimizzate = DitteAnonimizzate.replaceAll(",,", "");
          }
          if(",".equals(DitteAnonimizzate.substring(DitteAnonimizzate.length()-1)))
            DitteAnonimizzate=DitteAnonimizzate.substring(0,DitteAnonimizzate.length()-2);
          DitteAnonimizzate = DitteAnonimizzate.replaceAll(",", ", ");

        }
      }
      descrEvento +="(n.ditte codificate: " + numope + ")";
      errMsgEvento="Ditte codificate:" + DitteAnonimizzate;
      //best case
      livEvento = 1;
      commitTransaction = true;
      result.put("Esito", "Successo");
    } catch (Exception ge) {
      livEvento = 3;
      errMsgEvento = ge.getMessage();
      result.put("Esito", "Errore");
      result.put("MsgErrore", errMsgEvento);
      logger.error(messaggioPerLog);
    }  finally {
      if (status != null) {
        if (commitTransaction) {
          try {
            this.sqlManager.commitTransaction(status);
          } catch (Exception e) {

          }
        } else {
          try {
            this.sqlManager.rollbackTransaction(status);

          } catch (Exception e) {

          }
        }
      }
      //Tracciatura eventi
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(ngara);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descrEvento);
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        logger.error(genericMsgErr);
      }


    }

    out.print(result);
    out.flush();

    return null;
  }

  /**
   * Metodo per la creazione dell'id cifrato per ogni ditta.
   * L'id in chiaro ha la seguente forma: operatore00n
   * L'id cifrato è ottenuto dalla stringa in chiaro composta dall'id in chiaro concatenando la gara e ditta:
   * operatore00n#<NGARA5.DITG>#<DITTAO.DITG>
   *
   * @param ngara
   * @param ditta
   * @param progressivo
   * @return String
   * @throws CriptazioneException
   */
  String composizioneIdDittaCifrato(String ngara, String ditta, int progressivo) throws CriptazioneException {
    String identificativo=null;
    int dimMax=3;
    if(progressivo>=1000)
      dimMax=4;
    identificativo= PREFISSO_CODIFICA + UtilityStringhe.fillLeft(""+progressivo, '0', dimMax) + "#" + ngara + "#" + ditta;
    ICriptazioneByte valoreICriptazioneByte = FactoryCriptazioneByte.getInstance(
        ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), identificativo.getBytes(),
        ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
    identificativo = new String(valoreICriptazioneByte.getDatoCifrato());
    return identificativo;
  }
}
