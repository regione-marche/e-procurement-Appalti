/*
 * Created on 12/10/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore che effettua il controllo della valorizzazione di alcuni
 * campi. Se anche uno non risulta valorizzato, allora si deve riportare
 * un messaggio opportuno alla finestra popupPubblicaSuAmministrazioneTrasparente.jsp
 *
 * @author Cristian Febas
 */
public class GestorePubblicaSuAmministrazioneTrasparente extends AbstractGestorePreload {

  public GestorePubblicaSuAmministrazioneTrasparente(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);

    // lettura dei parametri di input
    String codgar = page.getRequest().getParameter("codgar");
    codgar = UtilityStringhe.convertiNullInStringaVuota(codgar);
    String select=null;
    Long genere = null;
    String messaggio = "<b>Non è possibile procedere con la pubblicazione della gara ai fini della trasparenza sul portale Appalti.</b><br>";
    String controlloSuperato="SI";
    boolean controlloSuperatoFirma=true;
    boolean controlloAllegatoSuperato = true;
    String MsgConferma = "";
    Long numOccorrenze=null;
    Long gruppo= new Long(5);

    try {
      genere = (Long) sqlManager.getObject("select genere from V_GARE_TORN where codgar = ?", new Object[] {codgar});
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura del genere della gara ", e);
    }

    try {

      // messaggio di conferma
      MsgConferma = "Confermi la pubblicazione della gara ai fini della trasparenza sul portale Appalti?";
      page.setAttribute("MsgConferma", MsgConferma, PageContext.REQUEST_SCOPE);

      select = "select cenint,codrup,imptor,iterga,dteoff,dtepar,dinvit from torn where codgar = ?";
      Vector datiTORN = sqlManager.getVector(select, new Object[]{codgar});

      if(datiTORN!=null){
        // Controllo Stazione appaltante
        String codiceSA = ((JdbcParametro) datiTORN.get(0)).getStringValue();
        codiceSA = UtilityStringhe.convertiNullInStringaVuota(codiceSA);
        if ("".equals(codiceSA)) {
          controlloSuperato = "NO";
          messaggio += "<br>Non è stata inserita la stazione appaltante.";
        }
        // Controllo RUP
        String codiceRUP = ((JdbcParametro) datiTORN.get(1)).getStringValue();
        codiceRUP = UtilityStringhe.convertiNullInStringaVuota(codiceRUP);
        if ("".equals(codiceRUP)) {
          controlloSuperato = "NO";
          messaggio += "<br>Non è stato inserito il responsabile unico procedimento.";
        }
      }
      //Controllo se la gara è aggiudicata ed eventualmente se la ditta aggiudicataria
      // ha CF e/o PIVA - nelle RTI si controlla la mandataria
      if (genere != null && (genere.longValue() == 3 || genere.longValue() == 1)) {
        if(genere.longValue() == 3){
          Vector datiGara = sqlManager.getVector(
              "select codgar1,ngara,dattoa from gare where codgar1=? and ngara = codgar1", new Object[] {codgar});
          String dattoa = SqlManager.getValueFromVectorParam(datiGara,2).getStringValue();
          dattoa = UtilityStringhe.convertiNullInStringaVuota(dattoa);
          if("".equals(dattoa)){
            controlloSuperato = "NO";
            messaggio += "<br>Non è stata inserita la data atto dell'aggiudicazione definitiva.";
          }
        }
        List listaLotti =  sqlManager.getListVector(
            "select codiga,ngara,ditta,dattoa from gare where codgar1=? and ngara!=codgar1", new Object[] {codgar });
        if (listaLotti != null && listaLotti.size() > 0) {
          Long numDitteAggiudicatarie = new Long(0);
          boolean dattoaLottiErr = false;
          String lottiErrorDattoa = "";
          for (int i = 0; i < listaLotti.size(); i++) {
            String codiga = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).getStringValue();
            String codiceLotto = SqlManager.getValueFromVectorParam(listaLotti.get(i), 1).getStringValue();
            String ditta = SqlManager.getValueFromVectorParam(listaLotti.get(i), 2).getStringValue();
            String dattoa = SqlManager.getValueFromVectorParam(listaLotti.get(i), 3).getStringValue();
            ditta = UtilityStringhe.convertiNullInStringaVuota(ditta);
            dattoa = UtilityStringhe.convertiNullInStringaVuota(dattoa);
            if (!"".equals(ditta)) {
              numDitteAggiudicatarie = numDitteAggiudicatarie + 1;
              if ("".equals(dattoa) && genere.longValue() == 1){
                dattoaLottiErr = true;
                if(!"".equals(lottiErrorDattoa)){
                  lottiErrorDattoa+=", ";
                }
                lottiErrorDattoa +=codiga;
              }
            }
          }
          if(dattoaLottiErr){
            controlloSuperato = "NO";
            messaggio += "<br>Non è stata inserita la data atto dell'aggiudicazione definitiva nel lotto " + lottiErrorDattoa;
          }
          if (numDitteAggiudicatarie.longValue() == 0) {
            controlloSuperato = "NO";
            messaggio += "<br>Nessun lotto della gara è stato aggiudicato in via definitiva.";
          }
        }

        // controllo su cf e PIVA ditte aggiudicatarie
        List listaDitteAgg = sqlManager.getListVector(
            "select distinct(ditta) from gare where codgar1=? and ngara!=codgar1 and ditta is not null", new Object[] {codgar });
        if (listaDitteAgg != null && listaDitteAgg.size() > 0) {
          for (int i = 0; i < listaDitteAgg.size(); i++) {
            String ditta = SqlManager.getValueFromVectorParam(listaDitteAgg.get(i),0).getStringValue();
            ditta = UtilityStringhe.convertiNullInStringaVuota(ditta);
            String[] msgArray = controlloDittaAggiudicataria(genere, ditta, page, sqlManager);
            if("NO".equals(msgArray[0])){
              controlloSuperato =msgArray[0];
              messaggio += msgArray[1];
            }
          }
        }

      } else {// Gara a lotto unico o lotto di gara
        Vector datiGara = sqlManager.getVector("select ditta,dattoa from gare where codgar1=?", new Object[] {codgar });
        if(datiGara!=null){
          String ditta = ((JdbcParametro) datiGara.get(0)).getStringValue();
          String dattoa = ((JdbcParametro) datiGara.get(1)).getStringValue();
          ditta = UtilityStringhe.convertiNullInStringaVuota(ditta);
          dattoa = UtilityStringhe.convertiNullInStringaVuota(dattoa);
          if ("".equals(ditta)){
            controlloSuperato = "NO";
            messaggio += "<br>La gara non è stata aggiudicata in via definitiva.";
          }else{
            if ("".equals(dattoa)){
              controlloSuperato = "NO";
              messaggio += "<br>Non è stata inserita la data atto dell'aggiudicazione definitiva.";
            }
            String[] msgArray = controlloDittaAggiudicataria(genere, ditta, page, sqlManager);
            if("NO".equals(msgArray[0])){
              controlloSuperato =msgArray[0];
              messaggio += msgArray[1];
            }
          }
        }
      }
      
      select="select idprg ,iddocdg, dittaagg, ngara from documgara where codgar = ? and gruppo = ? and statodoc is null";
      String selectFirma="select count(codgar) from documgara, w_docdig where codgar= ? and gruppo =? and documgara.idprg=w_docdig.idprg and documgara.iddocdg=w_docdig.iddocdig and digfirma = '1'";
      List listaDocDittaAgg = sqlManager.getListVector(select , new Object[] {codgar ,gruppo });
      if (listaDocDittaAgg != null && listaDocDittaAgg.size() > 0) {
        boolean controlloDocumLottoAgg = false;//solo se genere == 1
        boolean controlloDocumDittaAgg = false;//solo se genere == 3
        for (int j = 0; j < listaDocDittaAgg.size(); j++) {
          String idprg = SqlManager.getValueFromVectorParam(listaDocDittaAgg.get(j),0).getStringValue();
          String iddocdg = SqlManager.getValueFromVectorParam(listaDocDittaAgg.get(j),1).getStringValue();
          String dittaagg = SqlManager.getValueFromVectorParam(listaDocDittaAgg.get(j),2).getStringValue();
          String ngara = SqlManager.getValueFromVectorParam(listaDocDittaAgg.get(j),3).getStringValue();
          if(controlloPresenzaAllegato(idprg ,iddocdg ,sqlManager ) < 0){
            controlloAllegatoSuperato = false;
          }
          if(genere!= null && genere.intValue() == 1){
            Long cnt_agg = (Long) sqlManager.getObject("select count(*) from gare where ngara=? and ditta is not null", new Object[] {ngara });
            if (cnt_agg != null && cnt_agg.intValue() > 0) {
              controlloDocumLottoAgg = true;
            }
          }
          if(genere!= null && genere.intValue() == 3){
            Long cnt_agg = (Long) sqlManager.getObject("select count(*) from gare where ditta=? and codgar1 = ?", new Object[] {dittaagg,codgar });
            if (cnt_agg != null && cnt_agg.intValue() > 0) {
              controlloDocumDittaAgg = true;
            }
          }
        }
        if(controlloAllegatoSuperato && !controlloDocumLottoAgg && (genere!= null && genere.intValue() == 1)){//se genere == 1 
          controlloSuperato = "NO";
          messaggio += "<br>Non è stato inserito nessun documento associato ad uno dei lotti aggiudicati.";
        }
        if(controlloAllegatoSuperato && !controlloDocumDittaAgg && (genere!= null && genere.intValue() == 3)){//se genere == 3
          controlloSuperato = "NO";
          messaggio += "<br>Non è stato inserito nessun documento associato ad una delle ditte aggiudicatarie.";
        }
        String richiestaFirma = ConfigManager.getValore("documentiDb.richiestaFirma");
        //si deve controllare se vi sono dei documenti in attesa di firma
        if("1".equals(richiestaFirma) && !"NO".equals(controlloSuperato)){
          numOccorrenze = (Long) sqlManager.getObject(selectFirma, new Object[]{codgar, gruppo});
          if(numOccorrenze!= null && numOccorrenze.longValue()>0)
            controlloSuperatoFirma = false;
        }

      }else{
        controlloSuperato = "NO";
        messaggio += "<br>Non è stato inserito nessun documento per la trasparenza.";
      }
      if(!controlloAllegatoSuperato){
        controlloSuperato = "NO";
        messaggio += "<br>Per tutti i documenti per la trasparenza da pubblicare deve essere specificato l'allegato";
      }
      
      } catch (SQLException e) {
        throw new JspException("Errore nel controllo dei campi obbligatori ", e);
      } catch (GestoreException e) {
        throw new JspException("Errore nella lettura degli allegati", e);
      }
      if("NO".equals(controlloSuperato)){
        page.setAttribute("controlloSuperato", controlloSuperato, PageContext.REQUEST_SCOPE);
        page.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);
      }else {
        //Eseguo i controlli non bloccanti
        //controllo presenza documenti di gara
        messaggio = "<br><br><b>ATTENZIONE:</b>";
        if(!controlloSuperatoFirma){
          messaggio += "<br>Ci sono dei documenti per la trasparenza da pubblicare in attesa di firma.";
          controlloSuperato = "WARNING";
        }
      }

    }

    private int controlloPresenzaAllegato(String idprg ,String iddocdg , SqlManager sqlManager ) throws SQLException, GestoreException{
        // Tutti le righe di documenti di gara del gruppo 5 devono avere il documento associato
        boolean filePresente = true;
        idprg = UtilityStringhe.convertiNullInStringaVuota(idprg);
        iddocdg = UtilityStringhe.convertiNullInStringaVuota(iddocdg);
        if ("".equals(idprg) || "".equals(iddocdg)) {
          return -1;
        }
        Vector docAllegato = sqlManager.getVector("select DIGNOMDOC from W_DOCDIG where IDPRG = ? and IDDOCDIG = ?",
            new Object[] {idprg, iddocdg });
        if (docAllegato != null) {
          String nomedoc = ((JdbcParametro) docAllegato.get(0)).getStringValue();
          nomedoc = UtilityStringhe.convertiNullInStringaVuota(nomedoc);
          if ("".equals(nomedoc)) {
            filePresente = false;
          }
        } else {
          filePresente = false;
        }

        if (!filePresente) {
          return -1;
        }

        return 0;
    }

    private String[] controlloDittaAggiudicataria(Long genere , String ditta , PageContext page, SqlManager sqlManager) throws SQLException{
      Vector datiAggiudicataria = sqlManager.getVector("select tipimp, pivimp, cfimp, nomimp from impr where codimp =  ? ",
          new Object[] {ditta });

      String[] sArray= new String[2];
      String ctrlSuperato="SI";
      String ctrlmess="";

      String tipoImpresa = ((JdbcParametro) datiAggiudicataria.get(0)).getStringValue();
      String piva = ((JdbcParametro) datiAggiudicataria.get(1)).getStringValue();
      String cf = ((JdbcParametro) datiAggiudicataria.get(2)).getStringValue();
      String nomeImpresa = ((JdbcParametro) datiAggiudicataria.get(3)).getStringValue();
      tipoImpresa = UtilityStringhe.convertiNullInStringaVuota(tipoImpresa);
      nomeImpresa = UtilityStringhe.convertiNullInStringaVuota(nomeImpresa);


      if ("3".equals(tipoImpresa) || "10".equals(tipoImpresa)) {
        Vector datiRaggruppamento = sqlManager.getVector("select coddic, pivimp, cfimp from impr,ragimp"
          + "  where codimp = coddic and impman ='1' and codime9 = ?", new Object[] {ditta });
        if (datiRaggruppamento != null && datiRaggruppamento.size() > 0) {
          piva = ((JdbcParametro) datiRaggruppamento.get(1)).getStringValue();
          cf = ((JdbcParametro) datiRaggruppamento.get(2)).getStringValue();
        }else{
          ctrlSuperato = "NO";
          ctrlmess += "<br>Non è stata indicata la mandataria per la ditta aggiudicataria  "+ nomeImpresa +".";
          sArray[0] = ctrlSuperato;
          sArray[1] = ctrlmess;
          return sArray;
        }
      }

      piva = UtilityStringhe.convertiNullInStringaVuota(piva);
      cf = UtilityStringhe.convertiNullInStringaVuota(cf);

      if ("".equals(piva) && "".equals(cf)) {
        if (genere != null && genere.longValue() == new Long(3)) {
          ctrlSuperato = "NO";
          ctrlmess += "<br>Non sono stati inseriti il codice fiscale e la partita Iva della ditta aggiudicataria"
            + " " +nomeImpresa + ".";
        } else {
          ctrlSuperato = "NO";
          ctrlmess += "<br>Non sono stati inseriti il codice fiscale e la partita Iva della ditta aggiudicataria.";
        }
      }

      sArray[0] = ctrlSuperato;
      sArray[1] = ctrlmess;
      return sArray;
    }

}

