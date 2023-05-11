/*
 * Created on 22/gen/2019
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.InviaDatiRichiestaCigManager;
import it.eldasoft.sil.pg.bl.ScpManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityFiscali;

/**
 * Gestore che effettua i controlli preliminari e popola la popup per
 * la rettifica dei termini di gara
 *
 * @author Diego Pavan
 */
public class GestoreInvioDatiSCP extends AbstractGestorePreload {

  private static final String ERROR_CENINT = "<li>Il riferimento alla stazione appaltante non è valorizzato.";
  private static final String ERROR_NOMEIN = "<li>La denominazione della stazione appaltante non è valorizzata.";
  private static final String ERROR_CFEIN_NULL = "<li>Il codice fiscale della stazione appaltante non è valorizzato.";
  private static final String ERROR_CFEIN = "<li>Il codice fiscale della stazione appaltante non ha un formato valido.";
  private static final String ERROR_OGGETTO_GARA = "<li>L'oggetto della gara non è valorizzato.";
  private static final String ERROR_RUP = "<li>Il riferimento al responsabile unico procedimento non è valorizzato.";
  private static final String ERROR_COGTEC = "<li>Il cognome del responsabile unico procedimento non è valorizzato.";
  private static final String ERROR_NOMTEC = "<li>Il nome del responsabile unico procedimento non è valorizzato.";
  private static final String ERROR_CFIVATEC_NULL = "<li>Il codice fiscale e la partita Iva del responsabile unico procedimento sono entrambi non valorizzati.";
  private static final String ERROR_CFTEC = "<li>Il codice fiscale del responsabile unico procedimento non ha un formato valido.";
  private static final String ERROR_IVATEC = "<li>La partita Iva del responsabile unico procedimento non ha un formato valido.";
  private static final String ERROR_DATNEG = "<li>La data di esito gara non aggiudicata non è valorizzata.";
  private static final String ERROR_MODREA = "<li>La modalità di realizzazione non è valorizzata.";
  private static final String ERROR_NUTS = "<li>Il codice NUTS non è valorizzato.";
  private static final String ERROR_IMPAPP = "<li>L'importo a base di gara non è valorizzato.";
  private static final String ERROR_CIG_NULL = "<li>Il codice CIG non è valorizzato.";
  private static final String ERROR_CIG = "<li>La procedura risulta esente CIG. Pertanto non ne è previsto l'invio a SCP.";
  private static final String ERROR_RAGIONESOC = "<li>La ragione sociale della ditta aggiudicataria o delle sue componenti, nel caso di raggruppamento, non è valorizzata.";
  private static final String ERROR_CFIVADITTA = "<li>Il codice fiscale o la partita Iva della ditta aggiudicataria o delle sue componenti, nel caso di raggruppamento, non sono valorizzati o non hanno formato valido.";
  private static final String ERROR_LOTTI = "<li>Non sono stati definiti i lotti della gara.";
  private static final String ERROR_ANAC = "<li>Il numero gara ANAC non è valorizzato.";
  private static final String ERROR_GARA_AGG = "<li>La gara non risulta aggiudicata.";
  private static final String ERROR_LOTTI_AGG = "<li>Nessun lotto di gara risulta aggiudicato.";
  private static final String ERROR_LOTTI_OGGETTO = "<li>L'oggetto della gara non è valorizzato nei lotti ";
  private static final String ERROR_LOTTI_IMPAPP = "<li>L'importo a base di gara non è valorizzato nei lotti ";
  private static final String ERROR_LOTTI_CIG_NULL = "<li>Il codice CIG non è valorizzato nei lotti ";
  private static final String ERROR_LOTTI_CIG = "<li>Alcuni lotti della procedura risultano esenti CIG. Pertanto non ne è previsto l'invio a SCP. Verificare i lotti ";
  private static final String ERROR_LOTTI_RAGIONESOC = "<li>La ragione sociale della ditta aggiudicataria o delle sue componenti, nel caso di raggruppamento, non è valorizzata. Verificare i lotti ";
  private static final String ERROR_LOTTI_CFIVADITTA = "<li>Il codice fiscale o la partita Iva della ditta aggiudicataria o delle sue componenti, nel caso di raggruppamento, non sono valorizzati o non hanno formato valido. Verificare i lotti ";

  private SqlManager sqlManager = null;
  private InviaDatiRichiestaCigManager inviaDatiRichiestaCigManager = null;

  public GestoreInvioDatiSCP(BodyTagSupportGene tag) {
    super(tag);
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }

  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {
    /*
    String queryDocumentiDaPubblicare = "SELECT * FROM DOCUMGARA WHERE CODGAR = ? AND STATODOC IS NULL AND ((GRUPPO = 1 AND EXISTS (SELECT * FROM PUBBLI WHERE CODGAR9 = DOCUMGARA.CODGAR AND TIPPUB = 11))" +
    "OR (GRUPPO = 6 AND EXISTS (SELECT * FROM PUBBLI WHERE CODGAR9 = DOCUMGARA.CODGAR AND TIPPUB = 13)) OR (GRUPPO = 3 AND EXISTS (SELECT * FROM PUBBLI WHERE CODGAR9 = DOCUMGARA.CODGAR AND (TIPPUB = 13 OR TIPPUB = 11)))"+
    "OR  (GRUPPO = 2 AND EXISTS (SELECT * FROM PUBBLI WHERE CODGAR9 = DOCUMGARA.CODGAR AND (TIPPUB = 13 OR TIPPUB = 11))) OR  (GRUPPO = 4 AND EXISTS (SELECT * FROM PUBG WHERE NGARA = DOCUMGARA.CODGAR AND TIPPUBG = 12)))";
    */

    String messaggioErrori = "";
    String controlloSuperato="SI";

    String ngaraLotto;
    String not_gar;
    Long impapp;
    String codcig;
    String ditta;
    String codiga;

    String notgarLottiError = "";
    String impappLottiError = "";
    String codcigLottiError = "";
    String codcigLottiNonValido = "";
    String codiceFiscalePIVALottiError = "";
    String ragioneSocialeLottiError = "";

    ArrayList<String> codcpvLottiMsgErr = null;

    boolean pubblicare17 = false;
    boolean pubblicare19 = false;
    boolean pubblicare20 = false;

    boolean numavcpNullo = false;
    boolean cigNonEsenteNonSmart = false;

    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);

    inviaDatiRichiestaCigManager = (InviaDatiRichiestaCigManager) UtilitySpring.getBean("inviaDatiRichiestaCigManager",
        page, InviaDatiRichiestaCigManager.class);

    // lettura dei parametri di input
    String codiceGara = page.getRequest().getParameter("codiceGara");
    String genere = page.getRequest().getParameter("genere");
    String ngara = page.getRequest().getParameter("ngara");

    String entita = "";

    if("10".equals(genere) || "11".equals(genere) || "20".equals(genere)){
      entita = "avvisi";
    }else{
      entita = "pubblicazioni";
    }

    try {

      boolean trovatoLottiAggiudicati = true;
      boolean trovatoLottoAnnullato = true;
      if("pubblicazioni".equals(entita)){

      List<?> pubblicazioni = sqlManager.getListVector("select documgara.tipologia from documgara, g1cf_pubb where documgara.tipologia = g1cf_pubb.id " +
          "and documgara.codgar = ? and documgara.statodoc = 5 and (documgara.isarchi is null or documgara.isarchi != 1) and g1cf_pubb.invioscp = '1'", new Object[] {codiceGara});
      ArrayList<Integer> tipologieDaPubb = new ArrayList<Integer>();
      for (int i = 0; i < pubblicazioni.size(); i++) {
        Long tipologia = SqlManager.getValueFromVectorParam(pubblicazioni.get(i), 0).longValue();
        if(!tipologieDaPubb.contains(tipologia.intValue())){
          String select = "select count(*) from garattiscp where codgar = ? and tipologia = ?";
          Long count = (Long) sqlManager.getObject(select, new Object[] {codiceGara,tipologia});
          if(count.intValue()==0){
            tipologieDaPubb.add(tipologia.intValue());
            if(tipologia.intValue() == 17){pubblicare17=true;}
            if(tipologia.intValue() == 19){pubblicare19=true;}
            if(tipologia.intValue() == 20){pubblicare20=true;}
          }else{
            if(!"2".equals(genere) && (tipologia.intValue() == 17 || tipologia.intValue() == 19 || tipologia.intValue() == 20)){
              tipologieDaPubb.add(tipologia.intValue());
            }
          }
        }
      }
      /*
      boolean lottoAggNonPubblicato = true;
      if(tipologieDaPubb.contains(new Long(19)) || tipologieDaPubb.contains(new Long(20))){
        lottoAggNonPubblicato = false;
      }*/

      //se gara a lotti le pubblicazioni di tipo 17 19 20 sono associate ai lotto invece che alla gara

      List<?> lottiAggiudicati = sqlManager.getListVector("select ngara from GARE where codgar1 = ? and ngara != codgar1 and ditta is not null", new Object[] {codiceGara});
      if(lottiAggiudicati != null && lottiAggiudicati.size() > 0){
        for (int p = 0; p < lottiAggiudicati.size(); p++) {
          ngaraLotto = SqlManager.getValueFromVectorParam(lottiAggiudicati.get(p), 0).stringValue();
          if(tipologieDaPubb.contains(19)){
            String select = "select datpub from garattiscp where ngara = ? and tipologia = 19";
            Date datpub = (Date) sqlManager.getObject(select, new Object[] {ngaraLotto});
            if(datpub == null){pubblicare19 = true;}
          }else{
            if(tipologieDaPubb.contains(20)){
              String select = "select datpub from garattiscp where ngara = ? and tipologia = 20";
              Date datpub = (Date) sqlManager.getObject(select, new Object[] {ngaraLotto});
              if(datpub == null){pubblicare20 = true;}
            }
          }
        }
      }else{
        if(tipologieDaPubb.contains(19) || tipologieDaPubb.contains(20)){
          pubblicare19 = true;
          pubblicare20 = true;
          trovatoLottiAggiudicati = false;
        }
      }
      List<?> lottiAnnullati = sqlManager.getListVector("select ngara from GARE where codgar1 = ? and ngara != codgar1 and esineg is not null", new Object[] {codiceGara});
      if(lottiAnnullati != null && lottiAnnullati.size() > 0){
        for (int p = 0; p < lottiAnnullati.size(); p++) {
          ngaraLotto = SqlManager.getValueFromVectorParam(lottiAnnullati.get(p), 0).stringValue();
          if(tipologieDaPubb.contains(17)){
            String select = "select datpub from garattiscp where ngara = ? and tipologia = 17";
            Date datpub = (Date) sqlManager.getObject(select, new Object[] {ngaraLotto});
            if(datpub == null){pubblicare17 = true;}
          }
        }
      }else{
        if(tipologieDaPubb.contains(17)){
          trovatoLottoAnnullato = false;
          pubblicare17 = true;
        }
      }

      if(!pubblicare17){
        tipologieDaPubb.remove(new Integer(17));
      }
      if(!pubblicare19){
        tipologieDaPubb.remove(new Integer(19));
      }
      if(!pubblicare20){
        tipologieDaPubb.remove(new Integer(20));
      }

      if(tipologieDaPubb.size()==0){
        controlloSuperato = "NO";
        messaggioErrori += "<li>Non ci sono atti da inviare";
        page.setAttribute("erroriBloccanti", messaggioErrori, PageContext.REQUEST_SCOPE);
        return;
      }

      }else{
        String select = "select count(*) from pubbli where codgar9 = ? and tippub = 11";
        Long countPubbli = (Long) sqlManager.getObject(select, new Object[] {codiceGara});
        if(countPubbli == null || countPubbli.intValue() == 0){
          controlloSuperato = "NO";
          messaggioErrori += "<li>L'avviso non è ancora pubblicato su portale Appalti";
          page.setAttribute("erroriBloccanti", messaggioErrori, PageContext.REQUEST_SCOPE);
          return;
        }else{
          select = "select count(*) from pubbli where codgar9 = ? and tippub = 16";
          countPubbli = (Long) sqlManager.getObject(select, new Object[] {codiceGara});
          if(countPubbli != null && countPubbli.intValue() > 0){
            controlloSuperato = "NO";
            messaggioErrori += "<li>L'avviso risulta già inviato";
            page.setAttribute("erroriBloccanti", messaggioErrori, PageContext.REQUEST_SCOPE);
            return;
          }
        }
      }

      String login = ConfigManager.getValore(ScpManager.PROP_WS_PUBBLICAZIONI_USERNAME);
      String password = ConfigManager.getValore(ScpManager.PROP_WS_PUBBLICAZIONI_PASSWORD);
      String url = ConfigManager.getValore(ScpManager.PROP_WS_PUBBLICAZIONI_URL);
      String urlLogin = ConfigManager.getValore(ScpManager.PROP_WS_PUBBLICAZIONI_URL_LOGIN);
      String idClient = ConfigManager.getValore(ScpManager.PROP_WS_PUBBLICAZIONI_IDCLIENT);
      String keyClient = ConfigManager.getValore(ScpManager.PROP_WS_PUBBLICAZIONI_KEYCLIENT);

      boolean configError = false;
      String configParam = "";

      if(login == null || "".equals(login)){
        configError = true;
        if(!"".equals(configParam)){
          configParam+=", ";
        }
        configParam += ScpManager.PROP_WS_PUBBLICAZIONI_USERNAME;
      }
      if(password == null || "".equals(password)){
        configError = true;
        if(!"".equals(configParam)){
          configParam+=", ";
        }
        configParam += ScpManager.PROP_WS_PUBBLICAZIONI_PASSWORD;
      }
      if(url == null || "".equals(url)){
        configError = true;
        if(!"".equals(configParam)){
          configParam+=", ";
        }
        configParam += ScpManager.PROP_WS_PUBBLICAZIONI_URL;
      }
      if(urlLogin == null || "".equals(urlLogin)){
        configError = true;
        if(!"".equals(configParam)){
          configParam+=", ";
        }
        configParam += ScpManager.PROP_WS_PUBBLICAZIONI_URL_LOGIN;
      }
      if(idClient == null || "".equals(idClient)){
        configError = true;
        if(!"".equals(configParam)){
          configParam+=", ";
        }
        configParam += ScpManager.PROP_WS_PUBBLICAZIONI_IDCLIENT;
      }
      if(keyClient == null || "".equals(keyClient)){
        configError = true;
        if(!"".equals(configParam)){
          configParam+=", ";
        }
        configParam += ScpManager.PROP_WS_PUBBLICAZIONI_KEYCLIENT;
      }
      if(configError){
        controlloSuperato = "NO";
        if(!"".equals(configParam)){
          configParam+=".";
        }
        messaggioErrori = "<li>Manca la configurazione dei seguenti parametri nella sezione 'Invio dati a SCP':  " + configParam;
      }

      String urlPubblicaPortale = ConfigManager.getValore("portaleAppalti.urlPubblica");
      if(urlPubblicaPortale == null || "".equals(urlPubblicaPortale)){
        controlloSuperato = "NO";
        messaggioErrori = "<li>Manca la configurazione del parametro 'portaleAppalti.urlPubblica' nella sezione 'Portale Appalti'.";
      }

      String cenint;
      String destor;
      String codrup;
      Date datneg;
      String numavcp;
      String modrea;
      String codnuts;

      //controllo dati generali di gara TORN
      List<?> datiTorn = sqlManager.getListVector("select cenint, destor, codrup, numavcp, modrea, codnuts from TORN where codgar = ?", new Object[] {codiceGara});
      for (int n = 0; n < datiTorn.size(); n++) {
        cenint = SqlManager.getValueFromVectorParam(datiTorn.get(n), 0).stringValue();
        destor = SqlManager.getValueFromVectorParam(datiTorn.get(n), 1).stringValue();
        codrup = SqlManager.getValueFromVectorParam(datiTorn.get(n), 2).stringValue();
        numavcp = SqlManager.getValueFromVectorParam(datiTorn.get(n), 3).stringValue();
        modrea = SqlManager.getValueFromVectorParam(datiTorn.get(n), 4).stringValue();
        codnuts = SqlManager.getValueFromVectorParam(datiTorn.get(n), 5).stringValue();

        if(cenint == null || "".equals(cenint)){
          controlloSuperato = "NO";
          messaggioErrori += ERROR_CENINT;
        }else{
          List<?> datiUffint = sqlManager.getListVector("select nomein, cfein from UFFINT where CODEIN = ?", new Object[] {cenint});
          String nomein = SqlManager.getValueFromVectorParam(datiUffint.get(0), 0).stringValue();
          String cfein = SqlManager.getValueFromVectorParam(datiUffint.get(0), 1).stringValue();
          if(nomein == null || "".equals(nomein)){
            controlloSuperato = "NO";
            messaggioErrori += ERROR_NOMEIN;
          }
          if(cfein == null || "".equals(cfein)){
            controlloSuperato = "NO";
            messaggioErrori += ERROR_CFEIN_NULL;
          }else{
            if(!UtilityFiscali.isValidCodiceFiscale(cfein) && !UtilityFiscali.isValidPartitaIVA(cfein)){
              controlloSuperato = "NO";
              messaggioErrori += ERROR_CFEIN;
            }
          }
        }//fine controlli su uffint
        if("1".equals(genere) || "3".equals(genere)){
          if(destor == null || "".equals(destor)){
            controlloSuperato = "NO";
            messaggioErrori += ERROR_OGGETTO_GARA;
          }
        }else{
          if("11".equals(genere)){
            String oggetto = (String) sqlManager.getObject("select oggetto from gareavvisi where codgar = ?", new Object[] {codiceGara});
            if(oggetto == null || "".equals(oggetto)){
              controlloSuperato = "NO";
              messaggioErrori += ERROR_OGGETTO_GARA;
            }
          }else{
            if("10".equals(genere) || "20".equals(genere)){
              String oggetto = (String) sqlManager.getObject("select oggetto from garealbo where codgar = ?", new Object[] {codiceGara});
              if(oggetto == null || "".equals(oggetto)){
                controlloSuperato = "NO";
                messaggioErrori += ERROR_OGGETTO_GARA;
              }
            }
          }
        }

        if(codrup == null || "".equals(codrup)){
          controlloSuperato = "NO";
          messaggioErrori += ERROR_RUP;
        }else{
          List<?> datiTecni = sqlManager.getListVector("select cogtei, nometei, cftec, pivatec from TECNI where CODTEC = ?", new Object[] {codrup});
          String cogtei = SqlManager.getValueFromVectorParam(datiTecni.get(0), 0).stringValue();
          String nometei = SqlManager.getValueFromVectorParam(datiTecni.get(0), 1).stringValue();
          String cftec = SqlManager.getValueFromVectorParam(datiTecni.get(0), 2).stringValue();
          String pivatec = SqlManager.getValueFromVectorParam(datiTecni.get(0), 3).stringValue();
          if(cogtei == null || "".equals(cogtei)){
            controlloSuperato = "NO";
            messaggioErrori += ERROR_COGTEC;
          }
          if(nometei == null || "".equals(nometei)){
            controlloSuperato = "NO";
            messaggioErrori += ERROR_NOMTEC;
          }
          if((cftec == null || "".equals(cftec)) && (pivatec == null || "".equals(pivatec))){
            controlloSuperato = "NO";
            messaggioErrori += ERROR_CFIVATEC_NULL;
          }else{
            if(!(cftec == null || "".equals(cftec))){
              if(!UtilityFiscali.isValidCodiceFiscale(cftec) && !UtilityFiscali.isValidPartitaIVA(cftec)){
                controlloSuperato = "NO";
                messaggioErrori += ERROR_CFTEC;
              }
            }
            if(!(pivatec == null || "".equals(pivatec))){
              if(!UtilityFiscali.isValidCodiceFiscale(pivatec) && !UtilityFiscali.isValidPartitaIVA(pivatec)){
                controlloSuperato = "NO";
                messaggioErrori += ERROR_IVATEC;
              }
            }
          }
        }//fine controlli sul RUP

        if("pubblicazioni".equals(entita)){
          //c'è almeno un cig NON esente e NON smart
          if(numavcp == null || "".equals(numavcp)){
            numavcpNullo = true;
          }
          if(modrea == null || "".equals(modrea)){
            controlloSuperato = "NO";
            messaggioErrori += ERROR_MODREA;
          }
          if(codnuts == null || "".equals(codnuts)){
            controlloSuperato = "NO";
            messaggioErrori += ERROR_NUTS;
          }
        }
      }//fine controlli su TORN

      String profilo = (String) page.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);



      if("pubblicazioni".equals(entita)){
        //controlli dati per lotto
        boolean cigNonSmartNonEsente = false;
        List<?> lotti = sqlManager.getListVector("select ngara, not_gar, impapp, codcig, ditta, codiga, datneg from GARE where codgar1 = ? and ngara != codgar1", new Object[] {codiceGara});
        if(lotti != null && lotti.size()>0){
          for (int n = 0; n < lotti.size(); n++) {
            ngaraLotto = SqlManager.getValueFromVectorParam(lotti.get(n), 0).stringValue();
            not_gar = SqlManager.getValueFromVectorParam(lotti.get(n), 1).stringValue();
            impapp = SqlManager.getValueFromVectorParam(lotti.get(n), 2).longValue();
            codcig = SqlManager.getValueFromVectorParam(lotti.get(n), 3).stringValue();
            ditta = SqlManager.getValueFromVectorParam(lotti.get(n), 4).stringValue();
            codiga = SqlManager.getValueFromVectorParam(lotti.get(n), 5).stringValue();
            datneg = (Date) SqlManager.getValueFromVectorParam(lotti.get(n), 6).getValue();

            if(not_gar == null || "".equals(not_gar)){
              controlloSuperato = "NO";
              if("1".equals(genere) || "3".equals(genere)){
                if(!"".equals(notgarLottiError)){
                  notgarLottiError +=", ";
                }
                notgarLottiError += codiga;
              }else{
                messaggioErrori += ERROR_OGGETTO_GARA;
              }
            }
            if(impapp == null){
              controlloSuperato = "NO";
              if("1".equals(genere) || "3".equals(genere)){
                if(!"".equals(impappLottiError)){
                  impappLottiError +=", ";
                }
                impappLottiError += codiga;
              }else{
                messaggioErrori += ERROR_IMPAPP;
              }
            }
            if(codcig == null || "".equals(codcig)){
              controlloSuperato = "NO";
              if("1".equals(genere) || "3".equals(genere)){
                if(!"".equals(codcigLottiError)){
                  codcigLottiError +=", ";
                }
                codcigLottiError += codiga;
              }else{
                messaggioErrori += ERROR_CIG_NULL;
              }
            }else{
              if(codcig.startsWith("#") || codcig.startsWith("$") || codcig.startsWith("NOCIG")){
                controlloSuperato = "NO";
                if("1".equals(genere) || "3".equals(genere)){
                  if(!"".equals(codcigLottiNonValido)){
                    codcigLottiNonValido +=", ";
                  }
                  codcigLottiNonValido += codiga;
                }else{
                  messaggioErrori += ERROR_CIG;
                }
              }else{
                if(Character.isDigit(codcig.charAt(0)) && numavcpNullo){
                  cigNonEsenteNonSmart = true;
                }
              }
            }
            //se questo lotto è aggiudicato, ed è stata fatta una pubblciazione dell'esito non ancora inviata ad SCP,
            //segnifica verrà inviato un atto dell'esito che farà riferimento a questo lotto; è quindi necessario eseguire i controlli sull'aggiudicataria.
            String select = null;
            Date datpub = null;
            if(!(ditta == null || "".equals(ditta))){
              if(pubblicare20 || pubblicare19){

                String sqlGare1 = "SELECT AQOPER FROM GARE1 WHERE NGARA = ?";
                Long aqoper = (Long) sqlManager.getObject(sqlGare1, new Object[] {ngaraLotto});
                ArrayList<String> arrayDitteAgg = new ArrayList<String>();
                if(aqoper!=null && aqoper.intValue()==2){
                  List listDitgaq = sqlManager.getListVector("select dittao from ditgaq where ngara = ? order by numord", new Object[] {ngaraLotto});
                  for (int i = 0; i < listDitgaq.size(); i++) {
                    List aggiudicatarie = (List< ? >) listDitgaq.get(i);
                    arrayDitteAgg.add(SqlManager.getValueFromVectorParam(aggiudicatarie, 0).stringValue());
                  }
                }else{
                  arrayDitteAgg.add(ditta);
                }
                for (int index = 0; index < arrayDitteAgg.size(); index++) {
                  //se devo pubblicare un atto dell'esito
                  select = "select tipimp from impr where codimp = ?";
                  Long tipimp = (Long) sqlManager.getObject(select, new Object[] {ditta});
                  ArrayList<String> arrayDitte = new ArrayList<String>();
                  if(tipimp != null && (tipimp.intValue() == 3 || tipimp.intValue() == 10)){
                    select = "select coddic from ragimp where codime9 = ?";
                    List<?> datiRagimp = sqlManager.getListVector(select, new Object[] {ditta});
                    for (int m = 0; m < datiRagimp.size(); m++) {
                      arrayDitte.add(SqlManager.getValueFromVectorParam(datiRagimp.get(m), 0).stringValue());
                    }
                  }else{
                    arrayDitte.add(ditta);
                  }
                  for (int m = 0; m < arrayDitte.size(); m++) {
                    ditta = arrayDitte.get(m);
                    select = "select nomest, nomimp,cfimp, pivimp, nazimp from impr where codimp = ?";
                    List datiImpr = sqlManager.getListVector(select, new Object[] {ditta});
                    String nomest = SqlManager.getValueFromVectorParam(datiImpr.get(0), 0).stringValue();
                    String nomimp = SqlManager.getValueFromVectorParam(datiImpr.get(0), 1).stringValue();
                    String cfimp = SqlManager.getValueFromVectorParam(datiImpr.get(0), 2).stringValue();
                    String pivimp = SqlManager.getValueFromVectorParam(datiImpr.get(0), 3).stringValue();
                    Long nazimp = SqlManager.getValueFromVectorParam(datiImpr.get(0), 4).longValue();

                    if(nomest == null || "".equals(nomest)){
                      controlloSuperato = "NO";
                      if("1".equals(genere) || "3".equals(genere)){
                        if(!"".equals(ragioneSocialeLottiError)){
                          ragioneSocialeLottiError +=", ";
                        }
                        ragioneSocialeLottiError += codiga;
                      }
                      messaggioErrori += ERROR_RAGIONESOC;
                    }
                    if((cfimp == null || "".equals(cfimp)) && (pivimp == null || "".equals(pivimp))){
                      controlloSuperato = "NO";
                      if("1".equals(genere) || "3".equals(genere)){
                        if(!"".equals(codiceFiscalePIVALottiError)){
                          codiceFiscalePIVALottiError +=", ";
                        }
                        codiceFiscalePIVALottiError += codiga;
                      }
                      messaggioErrori += ERROR_CFIVADITTA;
                    }else{
                      //solo se italiano
                      if(nazimp != null && nazimp.intValue() == 1){
                        //se cf è valorizzato ed non è valido oppure se p.iva è valorizzato e non è valido
                        if((cfimp != null && !"".equals(cfimp) && (!UtilityFiscali.isValidCodiceFiscale(cfimp) && !UtilityFiscali.isValidPartitaIVA(cfimp))) ||
                          (pivimp != null && !"".equals(pivimp) && !UtilityFiscali.isValidPartitaIVA(pivimp))){
                          controlloSuperato = "NO";
                          if("1".equals(genere) || "3".equals(genere)){
                            if(!"".equals(codiceFiscalePIVALottiError)){
                              codiceFiscalePIVALottiError +=", ";
                            }
                            codiceFiscalePIVALottiError += codiga;
                          }else{
                            messaggioErrori += ERROR_CFIVADITTA;
                          }
                        }
                      }//fine controlli da fare sul CF e PIVA se impresa italiana.
                    }//fine controlli sul codiceFiscale partita iva.
                  }//fine controllo sulla ditta aggiudicataria
                }//fine ciclo sulle ditte aggiudicatarie/accordo quadro
              }//fine della condizione "devono pubb un atto dell'esito per questo lotto"
            }//fine controllo sul lotto aggiudicato
            codcpvLottiMsgErr = this.inviaDatiRichiestaCigManager.getControlloGarcpv(codiceGara, ngaraLotto, genere, profilo);
            if(codcpvLottiMsgErr.size()>0){
              controlloSuperato = "NO";
            }
          }// fine controlli sul lotto

          if(pubblicare17){
            if(!trovatoLottoAnnullato){
              controlloSuperato = "NO";
              messaggioErrori += ERROR_DATNEG;
            }
          }
          if(pubblicare19 || pubblicare20){
            if(!trovatoLottiAggiudicati){
              controlloSuperato = "NO";
              if(!"2".equals(genere)){
                messaggioErrori += ERROR_LOTTI_AGG;
              }else{
                messaggioErrori += ERROR_GARA_AGG;
              }
            }
          }

        }else{
          if("1".equals(genere) || "3".equals(genere)){
          controlloSuperato = "NO";
          messaggioErrori += ERROR_LOTTI;
          }
        }

      if("1".equals(genere) || "3".equals(genere)){
        if(!"".equals(notgarLottiError)){
          messaggioErrori += ERROR_LOTTI_OGGETTO + notgarLottiError + ".";
        }
        if(!"".equals(impappLottiError)){
          messaggioErrori += ERROR_LOTTI_IMPAPP + impappLottiError + ".";
        }
        if(!"".equals(codcigLottiError)){
          messaggioErrori += ERROR_LOTTI_CIG_NULL + codcigLottiError + ".";
        }
        if(!"".equals(codcigLottiNonValido)){
          messaggioErrori += ERROR_LOTTI_CIG + codcigLottiNonValido + ".";
        }
        if(!"".equals(ragioneSocialeLottiError)){
          messaggioErrori += ERROR_LOTTI_RAGIONESOC + ragioneSocialeLottiError + ".";
        }
        if(!"".equals(codiceFiscalePIVALottiError)){
          messaggioErrori += ERROR_LOTTI_CFIVADITTA + codiceFiscalePIVALottiError + ".";
        }
      }

      for(int n=0;n<codcpvLottiMsgErr.size();n++){
          messaggioErrori += "<li>"+codcpvLottiMsgErr.get(n);
      }

      if(cigNonEsenteNonSmart && numavcpNullo){
        controlloSuperato = "NO";
        messaggioErrori += ERROR_ANAC;
      }

      }//fine controlli sull'entità "pubblicazioni"

      if("NO".equals(controlloSuperato)){
        page.setAttribute("erroriBloccanti", messaggioErrori, PageContext.REQUEST_SCOPE);
      }else{
        //Controlli non bloccanti
        String messaggio="";

        ArrayList<String> errori= null;
        //CUPPRG
        if(!"11".equals(genere) && !"10".equals(genere) && !"20".equals(genere)) {
          errori= this.inviaDatiRichiestaCigManager.getControlloCUPCUI(codiceGara, ngara, genere, profilo, "CUPPRG");
          if(errori!=null && errori.size()>0){
            controlloSuperato = "WARNING";
            Iterator iter =errori.iterator();
            while (iter.hasNext())
              messaggio+="<li>" + iter.next();

          }

          //CODCUI
          errori= this.inviaDatiRichiestaCigManager.getControlloCUPCUI(codiceGara, ngara, genere, profilo, "CODCUI");
          if(errori!=null && errori.size()>0){
            controlloSuperato = "WARNING";
            Iterator iter =errori.iterator();
            while (iter.hasNext())
              messaggio+="<li>" + iter.next();
          }
        }



        if("WARNING".equals(controlloSuperato)){
          page.setAttribute("controlloSuperato", controlloSuperato, PageContext.REQUEST_SCOPE);
          page.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);
        }
      }

    } catch (SQLException e) {
      page.setAttribute("erroriBloccanti", e.getMessage(), PageContext.REQUEST_SCOPE);
      throw new JspException("Errore nella lettura dei dati per i controlli preliminari", e);
    } catch (GestoreException e) {
      page.setAttribute("erroriBloccanti", e.getMessage(), PageContext.REQUEST_SCOPE);
      throw new JspException("Errore nella lettura dei dati per i controlli preliminari", e);
    }
  }
}