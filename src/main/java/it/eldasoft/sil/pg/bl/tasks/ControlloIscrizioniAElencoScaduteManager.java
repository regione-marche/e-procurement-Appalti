/*
 * Created on 15/11/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.invcom.InvioComunicazioniManager;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.invcom.DestinatarioComunicazione;
import it.eldasoft.gene.db.domain.invcom.InvioComunicazione;
import it.eldasoft.gene.db.domain.invcom.ModelloComunicazione;
import it.eldasoft.gene.utils.MailUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.mail.IMailSender;
import it.eldasoft.utils.mail.MailSenderException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

public class ControlloIscrizioniAElencoScaduteManager {

  private static final String REPLACEMENT_OGGETTO_GARA = "#OGGETTOGA#";

  private static final String REPLACEMENT_TIPOLOGIA_RINNOVO = "#all'albo|al mercato elettronico#";

  private static final String REPLACEMENT_TESTO_DATA_SCADENZA = "#dataScadenza#";

  private static final int CODICE_GENERE_RINNOVO_ISCRIZIONE = 54;

  static Logger            logger                 = Logger.getLogger(ControlloIscrizioniAElencoScaduteManager.class);

  private SqlManager       sqlManager;

  private TabellatiManager tabellatiManager;

  private MailManager      mailManager;

  private InvioComunicazioniManager invioComunicazioniManager;

  private final String     TITOLOMAIL             = "Notifica di iscrizioni a elenco operatori economici o catalogo scadute o in scadenza";

  private final int        NUMERO_TENTATIVI_INVIO = 5;

  /**
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   *
   * @param tabellatiManager
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @param mailManager mailManager da settare internamente alla classe.
   */
  public void setMailManager(MailManager mailManager) {
    this.mailManager = mailManager;
  }

  /**
   * @param invioComunicazioniManager invioComunicazioniManager da settare internamente alla classe.
   */
  public void setInvioComunicazioniManager(InvioComunicazioniManager invioComunicazioniManager) {
    this.invioComunicazioniManager = invioComunicazioniManager;
  }

  /**
   *
   * Viene gestito il processo di controllo degli operatori scaduti e/o
   * in fase di preavviso
   */
  public void controlloIscrizioniAElencoScadute()  {

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato
    if (WebUtilities.isAppNotReady()) return;

    logger.debug("controlloIscrizioniAElencoScadute: inizio metodo");


      try {

        String select="select gare.ngara,valiscr,oggetto,genere,gpreavrin,cenint, rifiscr from garealbo,torn,gare where (tipologia <>3 or tipologia is null) and valiscr > 0 and gpreavrin > 0 and gare.ngara=garealbo.ngara and gare.codgar1=garealbo.codgar and torn.codgar=garealbo.codgar and gare.codgar1=torn.codgar";
        List listaGarealbo = this.sqlManager.getListVector(select, null);
        boolean isElenco=true;
        boolean isCatalogo=false;
        IMailSender mailSender = null;

        if(listaGarealbo!=null && listaGarealbo.size()>0){

          String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);

          String dataOdierna = UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA);

          StringBuffer msgScaduti = null;
          StringBuffer msgInScadenza = null;
          StringBuffer messaggio = null;

          select="select ditg.dittao, ditg.nomimo, ditg.dricind, impr.nomest, ditg.dscad, ditg.dricrin, emai2ip, emaiip,tipimp, ditg.dabilitaz from ditg,impr where ditg.ngara5 = ? " +
          		"and ditg.codgar5 = ? and ditg.dittao = impr.codimp and (ditg.abilitaz = 1) and ditg.dabilitaz is not null and ditg.dricind is not null order by ditg.nomimo";

          for(int i=0;i<listaGarealbo.size();i++){
            String ngara = SqlManager.getValueFromVectorParam(listaGarealbo.get(i), 0).getStringValue();
            String codgar = "$" + ngara;
            Long valiscr = SqlManager.getValueFromVectorParam(listaGarealbo.get(i), 1).longValue();
            String oggetto = SqlManager.getValueFromVectorParam(listaGarealbo.get(i),2).getStringValue();
            Long genere = SqlManager.getValueFromVectorParam(listaGarealbo.get(i),3).longValue();
            Long giorniPreavviso = SqlManager.getValueFromVectorParam(listaGarealbo.get(i),4).longValue();
            String idcfg = (String) SqlManager.getValueFromVectorParam(listaGarealbo.get(i),5).getValue();
            Long rifIscrizione = SqlManager.getValueFromVectorParam(listaGarealbo.get(i),6).longValue();

            mailSender = MailUtils.getInstance(this.mailManager,codapp,idcfg);

            isElenco=true;
            isCatalogo=false;
            if(genere!=null && genere.longValue()==20){
              isElenco=false;
              isCatalogo=true;
            }

            List listaDatiDitta = this.sqlManager.getListVector(select, new Object[]{ngara,codgar});
            boolean nuovoElencoPerMsgScaduti = true;
            boolean nuovoElencoPerMsgInScadenza = true;

            InvioComunicazione comunicazione = null;

            if(listaDatiDitta!=null && listaDatiDitta.size()>0){
              msgScaduti = null;
              msgInScadenza = null;
              for(int j=0; j<listaDatiDitta.size();j++){
                String dittao = SqlManager.getValueFromVectorParam(listaDatiDitta.get(j), 0).getStringValue();
                //String nomimo = SqlManager.getValueFromVectorParam(listaDatiDitta.get(j), 1).getStringValue();
                String nomest = SqlManager.getValueFromVectorParam(listaDatiDitta.get(j), 3).getStringValue();
                String pec = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(listaDatiDitta.get(j), 6).getStringValue());
                String mail = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(listaDatiDitta.get(j), 7).getStringValue());
                String mailRiferimento = null;
                Long tipimp = SqlManager.getValueFromVectorParam(listaDatiDitta.get(j), 8).longValue();
                if(tipimp!=null && (tipimp.longValue()==3 || tipimp.longValue()==10)){
                  //Si devono considerare emai2ip, emaiip della mandataria
                  Vector datiMandataria = this.sqlManager.getVector("select emai2ip, emaiip from impr,ragimp " +
                  		"where codime9=? and impman='1' and coddic=codimp", new Object[]{dittao});
                  if(datiMandataria!=null && datiMandataria.size()>0){
                    pec = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(datiMandataria, 0).getStringValue());
                    mail = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(datiMandataria, 1).getStringValue());
                  }
                }
                int tipoMail = 0;
                if (pec != null) {
                  mailRiferimento = pec;
                  tipoMail = DestinatarioComunicazione.INDIRIZZO_PEC;
                } else if (mail != null) {
                  mailRiferimento = mail;
                  tipoMail = DestinatarioComunicazione.INDIRIZZO_MAIL;
                }
                Timestamp dricindTimestamp = SqlManager.getValueFromVectorParam(listaDatiDitta.get(j), 2).dataValue();
                Timestamp dscadTimestamp = SqlManager.getValueFromVectorParam(listaDatiDitta.get(j), 4).dataValue();
                Timestamp dricrinTimestamp = SqlManager.getValueFromVectorParam(listaDatiDitta.get(j), 5).dataValue();
                Timestamp dabilitazTimestamp = SqlManager.getValueFromVectorParam(listaDatiDitta.get(j), 9).dataValue();
                //(APP-342)la data inizio validità iscrizione viene formulata sulla base del tabellato A1169
                Timestamp dinizioValIscrTimestamp = null;
                if(rifIscrizione!= null && Long.valueOf(2).equals(rifIscrizione)) {
                	dinizioValIscrTimestamp = dabilitazTimestamp;
                }else {
                	dinizioValIscrTimestamp = dricindTimestamp;
                }
                
                // 26/09/2014: si prende data ultimo rinnovo se valorizzato, altrimenti la data prima iscrizione
                Date dataIscrizioneORinnovo = (dscadTimestamp != null ? new Date(dscadTimestamp.getTime()) : new Date(dinizioValIscrTimestamp.getTime()));

                if (!(dricrinTimestamp != null && dricrinTimestamp.compareTo(dataIscrizioneORinnovo) > 0)) {
                  // eseguo i controlli solo se non e' gia' arrivato un rinnovo successivo all'ultimo ufficiale (o all'iscrizione) da approvare manualmente

                  Date dataScadenza = this.incrementaDecrementaData(dataIscrizioneORinnovo, valiscr - 1);
                  int confronto1 = this.confrontoDataConDataOdierna(dataScadenza);
                  Date dataPreavviso = this.incrementaDecrementaData(dataScadenza, new Long(-1 * giorniPreavviso.longValue()));
                  int confronto2 = this.confrontoDataConDataOdierna(dataPreavviso);

                  if(confronto1<0){
                    //sono nella situazione di operatore scaduto, per cui lo traccio nella mail di notifica all'ente e disabilito l'operatore
                    if(nuovoElencoPerMsgScaduti){
                      msgScaduti = new StringBuffer();
                      nuovoElencoPerMsgScaduti=false;
                      msgScaduti.append("In data ").append(dataOdierna).append(" risultano scadute le iscrizioni per i seguenti operatori ");
                      if(isElenco) {
                        msgScaduti.append("dell'elenco \"");
                      }
                      if(isCatalogo) {
                        msgScaduti.append("del catalogo \"");
                      }
                      msgScaduti.append(oggetto).append("\":\n");
                    }
                    msgScaduti.append("- ").append(nomest).append("\n");

                    Date dataOggi=UtilityDate.convertiData(
                            UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA),
                            UtilityDate.FORMATO_GG_MM_AAAA);

                    //sono nella condizione di elenco scaduto si deve aggiornare la banca dati
                    this.sqlManager.update("update ditg set abilitaz = ?, dsospe = ? " +
                          "where ngara5=? and codgar5 = ? and dittao = ?", new Object[]{new Long(8),dataOggi,ngara,codgar,dittao});
                  }

                  if(confronto2==0){
                    //sono nel giorno di preavviso
                    if(nuovoElencoPerMsgInScadenza){
                      msgInScadenza = new StringBuffer();
                      nuovoElencoPerMsgInScadenza=false;
                      msgInScadenza.append("In data ").append(dataOdierna).append(" risultano in scadenza tra ").append(giorniPreavviso);
                      if(giorniPreavviso==1) {
                        msgInScadenza.append(" giorno");
                      } else {
                        msgInScadenza.append(" giorni");
                      }
                      msgInScadenza.append(" le iscrizioni per i seguenti operatori ");
                      if(isElenco) {
                        msgInScadenza.append("dell'elenco \"");
                      }
                      if(isCatalogo) {
                        msgInScadenza.append("del catalogo \"");
                      }
                      msgInScadenza.append(oggetto).append("\":\n");

                      // si crea la nuova comunicazione
                      ModelloComunicazione modello = this.invioComunicazioniManager.getModelloComunicazioneByGenere(CODICE_GENERE_RINNOVO_ISCRIZIONE);
                      comunicazione = this.invioComunicazioniManager.createComunicazioneFromModello(modello);
                      comunicazione.getPk().setIdProgramma(codapp);
                      comunicazione.setEntita("GARE");
                      comunicazione.setChiave1(ngara);
                      comunicazione.setNomeMittente(ConfigManager.getValore(CostantiGenerali.PROP_TITOLO_APPLICATIVO));
                      String testo = comunicazione.getTesto();
                      testo = StringUtils.replace(testo, REPLACEMENT_TESTO_DATA_SCADENZA, UtilityDate.convertiData(dataScadenza, UtilityDate.FORMATO_GG_MM_AAAA));
                      String[] tipologie = StringUtils.replace(REPLACEMENT_TIPOLOGIA_RINNOVO, "#", "").split("\\|");
                      testo = StringUtils.replace(testo, REPLACEMENT_TIPOLOGIA_RINNOVO, (isCatalogo ? tipologie[1] : tipologie[0]));
                      testo = StringUtils.replace(testo, REPLACEMENT_OGGETTO_GARA, oggetto);
                      comunicazione.setTesto(testo);
                      comunicazione.setComunicazionePubblica(InvioComunicazione.COMUNICAZIONE_RISERVATA);
                      comunicazione.setDataPubblicazione(new Date());
                      comunicazione.setStato(InvioComunicazione.STATO_COMUNICAZIONE_IN_USCITA);
                      comunicazione.setIdcfg(idcfg);
                    }

                    msgInScadenza.append("- ").append(nomest).append(" (");
                    if (mailRiferimento != null) {
                      msgInScadenza.append(mailRiferimento);
                    } else {
                      msgInScadenza.append("Recapito mail non disponibile");
                    }
                    msgInScadenza.append(")\n");

                    // aggiungere il destinatario
                    if (mailRiferimento != null) {
                      DestinatarioComunicazione destinatario = new DestinatarioComunicazione();
                      destinatario.setEntitaArchivioDestinatario("IMPR");
                      destinatario.setCodiceSoggettoArchivio(dittao);
                      destinatario.setIndirizzo(mailRiferimento);
                      destinatario.setTipoIndirizzo(tipoMail);
                      destinatario.setIntestazione(nomest);
                      comunicazione.getDestinatariComunicazione().add(destinatario);
                    }
                  }
                }
              }

              if(msgScaduti!= null){
                if(messaggio!=null)
                  messaggio.append("\n");
                else
                  messaggio = new StringBuffer();

                messaggio.append(msgScaduti);
              }

              if(msgInScadenza!= null){
                if(msgScaduti!= null || (msgScaduti == null && messaggio!=null))
                  messaggio.append("\n");
                else if(messaggio==null)
                  messaggio = new StringBuffer();

                messaggio.append(msgInScadenza);
              }
            }

            // inserire la comunicazione
            if (comunicazione != null) {
              this.invioComunicazioniManager.insertComunicazione(comunicazione);
            }
          }

          //Invio mail unica per tutti gli elenchi/cataloghi
          if (messaggio != null) {
              //Estrazione degli indirizzi dal tabellato A1082
              List listaDestinatari = tabellatiManager.getTabellato("A1082");
              boolean isMailDestinatari = true;

              //Se non vi sono destinatari allora scrivo nel log tale situazione
              if(listaDestinatari==null || listaDestinatari.size()==0){
                logger.error("Mail di controllo iscrizioni a elenchi e cataloghi NON INVIABILE, in quanto non sono stati configurati gli indirizzi mail dei destinatari (parametro A1082).");
                isMailDestinatari = false;
              }

              boolean indirizziDestTuttiValorizzati = true;
              String destinatari[] = new String[listaDestinatari.size()];
              for(int z=0;z<listaDestinatari.size();z++){
                destinatari[z]= ((Tabellato) listaDestinatari.get(z)).getDescTabellato();
                if(destinatari[z]== null || "".equals(destinatari[z]))
                  indirizziDestTuttiValorizzati= false;
              }

              //Se non tutti i destinatari sono valorizzati allora scrivo nel log tale situazione
              if(!indirizziDestTuttiValorizzati){
                logger.error("Mail di controllo iscrizioni a elenchi e cataloghi NON INVIABILE in quanto nel parametro A1082 vi sono delle righe non valorizzate correttamente.");
                isMailDestinatari = false;
              }

            if(isMailDestinatari){
	            int numeroTentativi = this.NUMERO_TENTATIVI_INVIO;
	            boolean mailInviata = true;
	            mailSender = MailUtils.getInstance(this.mailManager,codapp,CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD);
	            // Tentativi di invio
	            do {
	              try {
	                mailSender.send(destinatari, null, null, TITOLOMAIL, messaggio.toString(), null);
	                mailInviata = true;
	              } catch (MailSenderException ms) {
	                numeroTentativi--;
	                mailInviata = false;
	                int tentativo = this.NUMERO_TENTATIVI_INVIO - numeroTentativi;
	                logger.error("Errore durante l'invio della mail, tentativo numero " + tentativo + ": " + ms.getMessage(), ms);
	              }
	            } while (numeroTentativi > 0 && !mailInviata);
            }
          }

        }
      } catch (SQLException e) {
        logger.error("Errore durante la lettura dei dati degli elenchi/cataloghi da processare per i controlli sui rinnovi in scadenza", e);
      } catch (GestoreException e) {
        logger.error("Errore durante la lettura dei dati degli elenchi/cataloghi da processare per i controlli sui rinnovi in scadenza", e);
      } catch (MailSenderException e) {
        logger.error("Errore durante l'invio della mail di notifica degli elenchi/cataloghi con rinnovi in scadenza", e);
      } catch (DataAccessException e) {
        logger.error("Errore durante l'inserimento della comunicazione di richiesta rinnovo", e);
      }


    logger.debug("controlloIscrizioniAElencoScadute: fine metodo");
  }

  /**
   * Viene determinata la data che si ottiene sommando giorni alla data
   *
   * @param data
   * @param giorni
   *
   * @return Date
   *
   *
   */
  private Date incrementaDecrementaData(Date data, Long giorni){
    GregorianCalendar dataTmp = new GregorianCalendar();
    dataTmp.setTimeInMillis(data.getTime());
    dataTmp.add(Calendar.DATE, giorni.intValue());
    return dataTmp.getTime();

  }


  /**
   * Viene confrontata una data con la data odierna. Per effettuare il confronto per entrambe
   * le date vengono posti a zero leore, i minuti e i secondi, in modo da confrontare esclusivamente
   * le date
   *
   * @param data
   *
   * @return 0 se le date coincidono
   *         <0 se data < dataOggi
   *         >0 se data > dataOggi
   *
   *
   */
  private int confrontoDataConDataOdierna(Date data){
    //Data odierna
    GregorianCalendar c=new GregorianCalendar() ;
    c.set(Calendar.HOUR, 0);
    c.set(Calendar.MINUTE, 0);
    c.set(Calendar.SECOND, 0);
    c.set(Calendar.MILLISECOND, 0);
    Date dataOggi=c.getTime();

    GregorianCalendar c2=new GregorianCalendar();
    c2.setTime(data);
    int giorno2=c2.get(Calendar.DAY_OF_MONTH);
    int anno2=c2.get(Calendar.YEAR);
    int mese2=c2.get(Calendar.MONTH);
    GregorianCalendar k2=new GregorianCalendar() ;
    k2.set(anno2, mese2, giorno2);
    k2.set(Calendar.HOUR, 0);
    k2.set(Calendar.MINUTE, 0);
    k2.set(Calendar.SECOND, 0);
    k2.set(Calendar.MILLISECOND, 0);
    Date dataFinale=k2.getTime();

    int risultato = dataFinale.compareTo(dataOggi);  //=0 le due date sono uguali, <0 se dataFinale < dataOggi, >0 altrimenti
    return risultato;
  }
}
