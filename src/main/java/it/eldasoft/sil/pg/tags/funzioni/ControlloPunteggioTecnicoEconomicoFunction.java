/*
 * Created on 12/06/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class ControlloPunteggioTecnicoEconomicoFunction extends AbstractFunzioneTag {

  MEPAManager mepaManager = null;

  public ControlloPunteggioTecnicoEconomicoFunction() {
    super(5, new Class[] { PageContext.class, String.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager", pageContext, PgManager.class);
    mepaManager = (MEPAManager) UtilitySpring.getBean("mepaManager", pageContext, MEPAManager.class);
    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1", pageContext, PgManagerEst1.class);

    String ngara = (String) params[1];
    StringBuilder messaggio = new StringBuilder();
    String fase = (String) params[2];
    String tipoMessaggi = (String) params[3];
    String forzatoLottoUnico = (String) params[4];

    String suffissoMessaggi = null;
    if("offerteEconomiche".equals(tipoMessaggi))
      suffissoMessaggi = "attivazioneAperturaOfferteEconomiche";
    else if("calcoloAggiudicazione".equals(tipoMessaggi))
      suffissoMessaggi = "attivazioneCalcoloAggiudicazione";
    else if("aperturaDocAmministrativa".equals(tipoMessaggi))
      suffissoMessaggi = "attivazioneAperturaDocAmministrativa";
    else if("calcoloPunteggi".equals(tipoMessaggi))
      suffissoMessaggi = "calcoloPunteggi";

    try {
      if (ngara != null) {
        boolean isGaraOffertaUnica = false;
        String codgar = (String) sqlManager.getObject("select codgar1 from gare where ngara = ?", new Object[] { ngara });
        if(!"true".equals(forzatoLottoUnico)){
          Vector datiGare = sqlManager.getVector("select genere,bustalotti from gare where ngara=?", new Object[]{codgar});
          if(datiGare!=null && datiGare.size()>0){
            try {
              Long genere = SqlManager.getValueFromVectorParam(datiGare, 0).longValue();
              Long bustalotti = SqlManager.getValueFromVectorParam(datiGare, 1).longValue();
              //Nel caso di gara ad offerta unica, se si è nella fase di apertura documentazione amministrativa, i controlli vanno fatti su tutti i lotti
              //anche nel caso di bustalotti=1
              if(genere!=null && genere.longValue()==3 && ((bustalotti!=null && bustalotti.longValue()==2) || "aperturaDocAmministrativa".equals(tipoMessaggi) ))
                isGaraOffertaUnica = true;
            } catch (GestoreException e) {
              throw new JspException("Errore durante il controllo del punteggio tecnico ed economico", e);
            }
          }
        }


        if (!isGaraOffertaUnica) {
          Long modlicg = (Long) sqlManager.getObject("select modlicg from gare where ngara = ?", new Object[] { ngara });
          if (modlicg != null && modlicg.longValue() == 6) {
            boolean esitoControlloTotalePunteggi=true;
            // Controlli sui punteggi per le gare OEPV
            Double maxPunTecnico = pgManager.getSommaPunteggioTecnico(ngara);
            Double maxPunEconomico = pgManager.getSommaPunteggioEconomico(ngara);
            /*
            Double sogliaMinimaTecnica = (Double)sqlManager.getObject("select mintec from gare1 where ngara=?", new Object[]{ngara});
            Double sogliaEconomicaMinima = (Double)sqlManager.getObject("select mineco from gare1 where ngara=?", new Object[]{ngara});
            String costofisso = (String) sqlManager.getObject("select costofisso from gare1 where ngara = ?", new Object[] { ngara });
            */
            Double sogliaMinimaTecnica = null;
            Double sogliaEconomicaMinima = null;
            String costofisso = null;
            Long riptec=null;
            Long ripeco=null;
            Long ripcritec=null;
            Long ripcrieco=null;

            Vector datiGare1 = sqlManager.getVector("select mintec, mineco, costofisso, riptec, ripeco, ripcritec, ripcrieco from gare1 where ngara=?", new Object[]{ngara});
            if(datiGare1!=null && datiGare1.size()>0){
              try {
                sogliaMinimaTecnica = SqlManager.getValueFromVectorParam(datiGare1, 0).doubleValue();
                sogliaEconomicaMinima = SqlManager.getValueFromVectorParam(datiGare1, 1).doubleValue();
                costofisso = SqlManager.getValueFromVectorParam(datiGare1, 2).stringValue();
                riptec = SqlManager.getValueFromVectorParam(datiGare1, 3).longValue();
                ripeco = SqlManager.getValueFromVectorParam(datiGare1, 4).longValue();
                ripcritec = SqlManager.getValueFromVectorParam(datiGare1, 5).longValue();
                ripcrieco = SqlManager.getValueFromVectorParam(datiGare1, 6).longValue();
              }catch(GestoreException e) {
                throw new JspException("Errore durante il caricamento dei dati da GARE1 per esegire il controllo dei criteri della gara", e);
              }
            }

            // Controllo che i punteggi tecnico ed economico siano valorizzati
            if (maxPunTecnico == null || (maxPunEconomico == null && !"1".equals(costofisso))) {
              messaggio.append(UtilityTags.getResource("errors.gestoreException.*." + suffissoMessaggi + ".ControlloPunteggiGara", null, false));
            } else {
              if(!"1".equals(costofisso)) {
                if(maxPunEconomico==null)
                  maxPunEconomico=new Double(0);
                if(maxPunTecnico.doubleValue() + maxPunEconomico.doubleValue() != 100){
                  messaggio.append(UtilityTags.getResource("errors.gestoreException.*." + suffissoMessaggi + ".ControlloTotalePunteggiCriteriGara", null, false));
                  esitoControlloTotalePunteggi=false;
                }
              }
              boolean soglieMinimeImpostate= false;
              if(esitoControlloTotalePunteggi){
                //Se RIPTEC, RIPECO.GARE1 = 2 & RIPCRITEC, RIPCRIECO.GARE1 = 1 si deve controllare che non vi siano impostate soglie minime per i singoli criteri
                String tipoCriteri= "";
                if(new Long(2).equals(riptec) && new Long(1).equals(ripcritec)){
                  Long numSoglieMinImpostate = (Long)sqlManager.getObject("select count(ngara) from goev where ngara=? and tippar=? and minpun is not null", new Object[]{ngara, new Long(1)});
                  if(numSoglieMinImpostate!=null && numSoglieMinImpostate.longValue()>0){
                    soglieMinimeImpostate=true;
                    tipoCriteri+="tecnici";
                  }
                }
                if(new Long(2).equals(ripeco) && new Long(1).equals(ripcrieco)){
                  Long numSoglieMinImpostate = (Long)sqlManager.getObject("select count(ngara) from goev where ngara=? and tippar=? and minpun is not null", new Object[]{ngara, new Long(2)});
                  if(numSoglieMinImpostate!=null && numSoglieMinImpostate.longValue()>0){
                    soglieMinimeImpostate=true;
                    if(!"".equals(tipoCriteri))
                      tipoCriteri+= " ed ";
                    tipoCriteri+="economici";
                  }
                }

                if(soglieMinimeImpostate){
                  messaggio.append(UtilityTags.getResource("errors.gestoreException.*." + suffissoMessaggi + ".ControlloSoglieMinimeCriteri", new String[]{tipoCriteri}, false));
                }else{
                  //Si deve fare il controllo per i soli criteri economici che non siano tutti di tipo 'altri elementi' (ISNOPRZ.GOEV= '1'), solo per LIVPAR.GOEV = 1,3
                  if(!pgManagerEst1.esistonoCriteriEconomiciPrezzo(ngara))
                    messaggio.append(UtilityTags.getResource("errors.gestoreException.*." + suffissoMessaggi + ".ControlloCriteriEcoAnchePrezzo", new String[]{tipoCriteri}, false));
                }

              }

              //Controllo soglie punteggi
              if(!"aperturaDocAmministrativa".equals(tipoMessaggi) && !"calcoloPunteggi".equals(tipoMessaggi) && esitoControlloTotalePunteggi && !soglieMinimeImpostate){
                boolean risultato[] = mepaManager.controlloSogliePunteggiDitte(ngara, false, sqlManager, messaggio,fase, suffissoMessaggi);
                if(risultato[0] && risultato[2]){
                  // Controllo punteggi ditte
                  this.controlloPunteggiDitte(ngara, maxPunTecnico, maxPunEconomico, sogliaMinimaTecnica, sogliaEconomicaMinima, false, sqlManager, messaggio,suffissoMessaggi);
                }
              }
            }
          }
        } else {
          Long modlic = (Long) sqlManager.getObject("select modlic from torn where codgar = ?", new Object[] { codgar });
          if (modlic != null && modlic.longValue() == 6) {
            String selectLottiOEPV = "select g.ngara, g.codiga, g1.costofisso, g1.mintec, g1.mineco, g1.riptec, g1.ripeco, g1.ripcritec, g1.ripcrieco from gare g, gare1 g1 where g.ngara=g1.ngara and g.codgar1=? and g.modlicg=6 and g.ngara<>g.codgar1 order by g.codiga";
            List<?> listaLottiOEPV = sqlManager.getListVector(selectLottiOEPV, new Object[] { codgar });
            if (listaLottiOEPV != null && listaLottiOEPV.size() > 0) {

              Double maxPunTecnico = null;
              Double maxPunEconomico = null;
              String msgPunteggiGaraOffUnica = "";
              String msgTotaliPunteggiCriteriGaraOffUnica = "";
              String msgSoglieMinimeCriteriGaraOffUnica = "";
              String msgPunteggiDitteOffUnica = "";
              String msgPunteggiCriteriOffUnica = "";
              String msgPunteggiCriteriOffUnicaNonTuttiValorizzati = "";
              String msgCriteriEcoPrezzoOffUnica = "";
              Double sogliaMinimaTecnica=null;
              Double sogliaEconomicaMinima=null;
              boolean soglieMinimeValorizzate= false;
              boolean soglieMinimeCriteriValorizzate = false;
              String costofisso="";
              Long riptec=null;
              Long ripeco=null;
              Long ripcritec=null;
              Long ripcrieco=null;
              boolean soglieMinimeImpostate=false;
              Long numTotCriteriEco=null;
              Long numTotCriteriEcoNoPrez = null;

              for (int i = 0; i < listaLottiOEPV.size(); i++) {
                Vector<?> lotto = (Vector<?>) listaLottiOEPV.get(i);
                String ngaraLotto = ((JdbcParametro) lotto.get(0)).getStringValue();
                String codiga = ((JdbcParametro) lotto.get(1)).getStringValue();
                costofisso = ((JdbcParametro) lotto.get(2)).getStringValue();
                maxPunTecnico = pgManager.getSommaPunteggioTecnico(ngaraLotto);
                maxPunEconomico = pgManager.getSommaPunteggioEconomico(ngaraLotto);
                //sogliaMinimaTecnica = (Double)sqlManager.getObject("select mintec from gare1 where ngara=?", new Object[]{ngaraLotto});
                //sogliaEconomicaMinima = (Double)sqlManager.getObject("select mineco from gare1 where ngara=?", new Object[]{ngaraLotto});
                sogliaMinimaTecnica = ((JdbcParametro) lotto.get(3)).doubleValue();
                sogliaEconomicaMinima = ((JdbcParametro) lotto.get(4)).doubleValue();
                riptec = ((JdbcParametro) lotto.get(5)).longValue();
                ripeco = ((JdbcParametro) lotto.get(6)).longValue();
                ripcritec = ((JdbcParametro) lotto.get(7)).longValue();
                ripcrieco = ((JdbcParametro) lotto.get(8)).longValue();
                soglieMinimeImpostate=false;

                // Controllo che i punteggi tecnico ed economico di ogni lotti
                // siano valorizzati
                if (maxPunTecnico == null || (maxPunEconomico == null && !"1".equals(costofisso))) {
                  msgPunteggiGaraOffUnica += codiga + ",";
                }else{
                  //Per i lotti con costofisso!=1 si deve controllare che La somma dei punteggi massimi dei criteri di valutazione deve essere 100
                  if(!"1".equals(costofisso)) {
                    if(maxPunEconomico==null)
                      maxPunEconomico=new Double(0);
                    if(maxPunTecnico.doubleValue() + maxPunEconomico.doubleValue()!=100){
                      msgTotaliPunteggiCriteriGaraOffUnica += codiga + ",";
                    }else{
                      //Per i lotti per cui RIPTEC, RIPECO.GARE1 = 2 & RIPCRITEC, RIPCRIECO.GARE1 = 1 si deve controllare che non vi siano impostate soglie minime per i singoli criteri
                      if((new Long(2).equals(riptec) && new Long(1).equals(ripcritec)) || (new Long(2).equals(ripeco) && new Long(1).equals(ripcrieco))){
                        Long numSoglieMinImpostate = (Long)sqlManager.getObject("select count(ngara) from goev where ngara=? and (tippar=? or tippar=?) and minpun is not null", new Object[]{ngaraLotto, new Long(1), new Long(2)});
                        if(numSoglieMinImpostate!=null && numSoglieMinImpostate.longValue()>0){
                          soglieMinimeImpostate=true;
                          msgSoglieMinimeCriteriGaraOffUnica += codiga + ",";

                        }
                      }else{
                        //Si deve fare il controllo per i soli criteri economici che non siano tutti di tipo 'altri elementi' (ISNOPRZ.GOEV= '1'), solo per LIVPAR.GOEV = 1,3
                        if(!pgManagerEst1.esistonoCriteriEconomiciPrezzo(ngaraLotto))
                          msgCriteriEcoPrezzoOffUnica += codiga + ",";
                      }
                    }
                  }
                }

                if(!"aperturaDocAmministrativa".equals(tipoMessaggi) && !soglieMinimeImpostate){
                  if(sogliaMinimaTecnica!=null || sogliaEconomicaMinima!=null)
                    soglieMinimeValorizzate=true;

                  //Controllo soglie punteggi
                  boolean risultato[] = mepaManager.controlloSogliePunteggiDitte(ngaraLotto, true, sqlManager, messaggio, fase, suffissoMessaggi);
                  if(!risultato[0]){
                    msgPunteggiCriteriOffUnica += codiga + ",";
                  }else if(!risultato[2]){
                    //Controllo che tutti i punteggi siano valorizzati
                    msgPunteggiCriteriOffUnicaNonTuttiValorizzati += codiga + ",";
                  }else{
                    // Controllo punteggi ditte
                    if(fase!=null && new Long(fase).longValue()<GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE){
                      sogliaEconomicaMinima=null;
                      maxPunEconomico=null;
                    }
                    if (!this.controlloPunteggiDitte(ngaraLotto, maxPunTecnico, maxPunEconomico, sogliaMinimaTecnica, sogliaEconomicaMinima, true, sqlManager, messaggio,suffissoMessaggi)) {
                      msgPunteggiDitteOffUnica += codiga + ",";
                    }
                  }
                  if(risultato[1])
                    soglieMinimeCriteriValorizzate = true;
                }
              }

              if (msgPunteggiGaraOffUnica != "") {
                msgPunteggiGaraOffUnica = msgPunteggiGaraOffUnica.substring(0, msgPunteggiGaraOffUnica.length() - 1);
                String tmp[] = msgPunteggiGaraOffUnica.split(",");
                if (tmp.length > 1) {
                  msgPunteggiGaraOffUnica = "(verificare i lotti " + msgPunteggiGaraOffUnica + ")";
                } else {
                  msgPunteggiGaraOffUnica = "(verificare il lotto " + msgPunteggiGaraOffUnica + ")";
                }
                messaggio.append(UtilityTags.getResource("errors.gestoreException.*." + suffissoMessaggi + ".ControlloPunteggiGaraOffUnica",
                    new String[] { msgPunteggiGaraOffUnica }, false));
              }

              if (msgTotaliPunteggiCriteriGaraOffUnica != "") {
                msgTotaliPunteggiCriteriGaraOffUnica = msgTotaliPunteggiCriteriGaraOffUnica.substring(0, msgTotaliPunteggiCriteriGaraOffUnica.length() - 1);
                String tmp[] = msgTotaliPunteggiCriteriGaraOffUnica.split(",");
                if (tmp.length > 1) {
                  msgTotaliPunteggiCriteriGaraOffUnica = "(verificare i lotti " + msgTotaliPunteggiCriteriGaraOffUnica + ")";
                } else {
                  msgTotaliPunteggiCriteriGaraOffUnica = "(verificare il lotto " + msgTotaliPunteggiCriteriGaraOffUnica + ")";
                }
                if (msgPunteggiGaraOffUnica != "")
                  messaggio.append("<br>");
                messaggio.append(UtilityTags.getResource("errors.gestoreException.*." + suffissoMessaggi + ".ControlloTotalePunteggiCriteriGaraOffUnica",
                    new String[] { msgTotaliPunteggiCriteriGaraOffUnica }, false));
              }

              if (msgSoglieMinimeCriteriGaraOffUnica != "") {
                msgSoglieMinimeCriteriGaraOffUnica = msgSoglieMinimeCriteriGaraOffUnica.substring(0, msgSoglieMinimeCriteriGaraOffUnica.length() - 1);
                String tmp[] = msgSoglieMinimeCriteriGaraOffUnica.split(",");
                if (tmp.length > 1) {
                  msgSoglieMinimeCriteriGaraOffUnica = "(verificare i lotti " + msgSoglieMinimeCriteriGaraOffUnica + ")";
                } else {
                  msgSoglieMinimeCriteriGaraOffUnica = "(verificare il lotto " + msgSoglieMinimeCriteriGaraOffUnica + ")";
                }

                if(messaggio.length()>0)
                  messaggio.append("<br>");
                messaggio.append(UtilityTags.getResource("errors.gestoreException.*." + suffissoMessaggi + ".ControlloSoglieMinimeCriteriGaraOffUnica",
                    new String[] { msgSoglieMinimeCriteriGaraOffUnica }, false));
              }

              if (msgCriteriEcoPrezzoOffUnica != "") {
                msgCriteriEcoPrezzoOffUnica = msgCriteriEcoPrezzoOffUnica.substring(0, msgCriteriEcoPrezzoOffUnica.length() - 1);
                String tmp[] = msgCriteriEcoPrezzoOffUnica.split(",");
                if (tmp.length > 1) {
                  msgCriteriEcoPrezzoOffUnica = "(verificare i lotti " + msgCriteriEcoPrezzoOffUnica + ")";
                } else {
                  msgCriteriEcoPrezzoOffUnica = "(verificare il lotto " + msgCriteriEcoPrezzoOffUnica + ")";
                }

                if(messaggio.length()>0)
                  messaggio.append("<br>");
                messaggio.append(UtilityTags.getResource("errors.gestoreException.*." + suffissoMessaggi + ".ControlloCriteriEcoAnchePrezzoGaraOffUnica",
                    new String[] { msgCriteriEcoPrezzoOffUnica }, false));
              }


              if (msgPunteggiCriteriOffUnicaNonTuttiValorizzati != "") {
                msgPunteggiCriteriOffUnicaNonTuttiValorizzati = msgPunteggiCriteriOffUnicaNonTuttiValorizzati.substring(0, msgPunteggiCriteriOffUnicaNonTuttiValorizzati.length() - 1);
                String tmp[] = msgPunteggiCriteriOffUnicaNonTuttiValorizzati.split(",");
                if (tmp.length > 1) {
                  msgPunteggiCriteriOffUnicaNonTuttiValorizzati = "(verificare i lotti " + msgPunteggiCriteriOffUnicaNonTuttiValorizzati + ")";
                } else {
                  msgPunteggiCriteriOffUnicaNonTuttiValorizzati = "(verificare il lotto " + msgPunteggiCriteriOffUnicaNonTuttiValorizzati + ")";
                }
                if(messaggio.length()>0)
                  messaggio.append("<br>");
                messaggio.append(UtilityTags.getResource("errors.gestoreException.*." + suffissoMessaggi + ".ControlloPresenzaPunteggiCriteriOffUnica",
                    new String[] { msgPunteggiCriteriOffUnicaNonTuttiValorizzati }, false));
              }

              if (msgPunteggiCriteriOffUnica != "") {
                msgPunteggiCriteriOffUnica = msgPunteggiCriteriOffUnica.substring(0, msgPunteggiCriteriOffUnica.length() - 1);
                String tmp[] = msgPunteggiCriteriOffUnica.split(",");
                if (tmp.length > 1) {
                  msgPunteggiCriteriOffUnica = "(verificare i lotti " + msgPunteggiCriteriOffUnica + ")";
                } else {
                  msgPunteggiCriteriOffUnica = "(verificare il lotto " + msgPunteggiCriteriOffUnica + ")";
                }
                if (msgPunteggiGaraOffUnica != "" || msgPunteggiCriteriOffUnicaNonTuttiValorizzati!=null)
                  messaggio.append("<br>");
                String msg="";
                if(soglieMinimeCriteriValorizzate)
                  msg="o inferiore alla sua soglia minima ";
                messaggio.append(UtilityTags.getResource("errors.gestoreException.*." + suffissoMessaggi + ".ControlloPunteggiCriteriOffUnica",
                    new String[] { msgPunteggiCriteriOffUnica,msg }, false));
              }

              if (msgPunteggiDitteOffUnica != "") {
                msgPunteggiDitteOffUnica = msgPunteggiDitteOffUnica.substring(0, msgPunteggiDitteOffUnica.length() - 1);
                String tmp[] = msgPunteggiDitteOffUnica.split(",");
                if (tmp.length > 1) {
                  msgPunteggiDitteOffUnica = "(verificare i lotti " + msgPunteggiDitteOffUnica + ")";
                } else {
                  msgPunteggiDitteOffUnica = "(verificare il lotto " + msgPunteggiDitteOffUnica + ")";
                }
                String msg="";
                if(soglieMinimeValorizzate)
                  msg="o inferiore alle soglie minime ";
                if (msgPunteggiCriteriOffUnica != "" || msgPunteggiCriteriOffUnicaNonTuttiValorizzati!=null)
                  messaggio.append("<br>");
                messaggio.append(UtilityTags.getResource("errors.gestoreException.*." + suffissoMessaggi + ".ControlloPunteggiDitteOffUnica",
                    new String[] { msgPunteggiDitteOffUnica,msg }, false));
              }
              if (msgPunteggiCriteriOffUnica != "" || msgPunteggiDitteOffUnica !="" || msgPunteggiCriteriOffUnicaNonTuttiValorizzati != "") {
                  messaggio.append(".<br>Consultare il prospetto punteggi ditte dei singoli lotti per i dettagli.");
              }
            }
          }
        }
      }
    } catch (SQLException e) {
      throw new JspException("Errore durante il controllo del punteggio tecnico ed economico", e);
    }catch (GestoreException e) {
      throw new JspException("Errore durante il controllo del punteggio tecnico ed economico", e);
    }

    if (messaggio != null) {
      return messaggio.toString();
    } else {
      return null;
    }
  }

  /**
   *
   * Si controlla che tutte le ditte abbiano punteggio tecnico ed economico non
   * superiori a quelli massimi della gara e superiori alle soglie minime.
   * Nel caso di offerta unica se il controllo non viene superato, viene
   * restituito false Per gli altri tipi di gara, se il controllo non viene
   * superato viene generato un errore e si produce il relativo messaggio.
   *
   * @param ngara
   * @param maxPunTecnico
   * @param maxPunEconomico
   * @param isGaraOffUnica
   * @return boolean
   *
   * @throws GestoreException
   */
  private boolean controlloPunteggiDitte(String ngara, Double maxPunTecnico, Double maxPunEconomico,
      Double sogliaTecnicaMinima, Double sogliaEconomicaMinima, boolean isGaraOffUnica, SqlManager sqlManager,
      StringBuilder messaggio, String suffissoMessaggi) throws JspException {

    boolean ret = true;


    // Tutte le ditte devono avere punteggio tecnico ed economico non superiori
    // a
    // quelli della gara. Si devono caricare i dati delle ditte visualizzate
    // allo step
    // dell'apertura
    String selectDITG = "select puntec, puneco from ditg where ngara5=? and (INVOFF <> '2' or INVOFF is null) and "
        + "(AMMGAR <> '2' or AMMGAR is null) and (MOTIES < 99 or MOTIES is null)";

    try {
      List<?> listaDitte = sqlManager.getListVector(selectDITG, new Object[] { ngara });

      if (listaDitte != null && listaDitte.size() > 0) {

        boolean controlloPunteggiTecniciSuperato = true;
        boolean controlloPunteggiEconomiciSuperato = true;
        boolean controlloSogliaMinimaTecnicaSuperato = true;
        boolean controlloSogliaMinimaEconomicaSuperato = true;
        Double punteggioTecnico = null;
        Double punteggioEconomico = null;

        //Confronto dei punteggi totali delle ditte rispetto al punteggio massimo(sia tecnico che economico)
        for (int i = 0; i < listaDitte.size(); i++) {
          Vector<?> ditta = (Vector<?>) listaDitte.get(i);
          punteggioTecnico = ((JdbcParametro) ditta.get(0)).doubleValue();
          punteggioEconomico = ((JdbcParametro) ditta.get(1)).doubleValue();

          if (punteggioTecnico != null && maxPunTecnico != null && punteggioTecnico.doubleValue() > maxPunTecnico.doubleValue()) {
            controlloPunteggiTecniciSuperato = false;
          }

          if (punteggioEconomico != null && maxPunEconomico != null && punteggioEconomico.doubleValue() > maxPunEconomico.doubleValue()) {
            controlloPunteggiEconomiciSuperato = false;
          }
        }

        //confronto dei punteggi totali tecnici delle ditte rispetto alla soglia minima tecnica se presente
        if(sogliaTecnicaMinima!=null)
          controlloSogliaMinimaTecnicaSuperato = this.mepaManager.esitoControlloPunteggiTotaliDitteSogliaMinima(ngara, sogliaTecnicaMinima, new Long(1),null);

        //confronto dei punteggi totali economici delle ditte rispetto alla soglia minima economica se presente
        if(sogliaEconomicaMinima!=null)
          controlloSogliaMinimaEconomicaSuperato = this.mepaManager.esitoControlloPunteggiTotaliDitteSogliaMinima(ngara, sogliaEconomicaMinima, new Long(2),null);

        if (!isGaraOffUnica) {
          String[] par = null;

          if ((!controlloPunteggiTecniciSuperato || !controlloSogliaMinimaTecnicaSuperato) && controlloPunteggiEconomiciSuperato) {
            if(!controlloSogliaMinimaTecnicaSuperato )
              par = new String[]{"o inferiore alla soglia minima","i"};
            else
              par = new String[]{"","o"};
            if (messaggio!=null)
              messaggio.append("<br>");
            messaggio.append(UtilityTags.getResource("errors.gestoreException.*." +suffissoMessaggi + ".ControlloPunteggiTecniciDitte", par,
                false));
            // messaggio =
            // "Non e' possibile procedere con l'apertura delle offerte economiche perche' alcune ditte presentano un punteggio tecnico superiore ai valori massimi previsti per la gara";
          } else if (controlloPunteggiTecniciSuperato && (!controlloPunteggiEconomiciSuperato || !controlloSogliaMinimaEconomicaSuperato)) {
            if(!controlloSogliaMinimaEconomicaSuperato)
              par = new String[]{"o inferiore alla soglia minima","i"};
            else
              par = new String[]{"","o"};
            if (messaggio!=null)
              messaggio.append("<br>");
            messaggio.append(UtilityTags.getResource("errors.gestoreException.*." + suffissoMessaggi +".ControlloPunteggiEconomiciDitte", par,
                false));
            // messaggio =
            // "Non e' possibile procedere con l'apertura delle offerte economiche perche' alcune ditte presentano un punteggio economico superiore ai valori massimi previsti per la gara";
          } else if (!controlloPunteggiTecniciSuperato && !controlloPunteggiEconomiciSuperato) {
            if(sogliaTecnicaMinima!=null || sogliaEconomicaMinima!=null )
              par = new String[]{"o inferiore alla soglia minima"};
            if (messaggio!=null)
              messaggio.append("<br>");
            messaggio.append(UtilityTags.getResource("errors.gestoreException.*." + suffissoMessaggi +".ControlloPunteggiDitte", par, false));
            // messaggio =
            // "Non e' possibile procedere con l'apertura delle offerte economiche perche' alcune ditte presentano un punteggio tecnico o economico superiore ai valori massimi previsti per la gara";
          }
        } else {
          if (!controlloPunteggiTecniciSuperato || !controlloPunteggiEconomiciSuperato || !controlloSogliaMinimaEconomicaSuperato || !controlloSogliaMinimaTecnicaSuperato) {
            ret = false;
          }
        }
      }
    } catch (GestoreException e) {
      throw new JspException("Errore durante il controllo del punteggio tecnico ed economico", e);
    } catch (SQLException e) {
      throw new JspException("Errore durante il controllo del punteggio tecnico ed economico", e);
    }
    return ret;
  }

}