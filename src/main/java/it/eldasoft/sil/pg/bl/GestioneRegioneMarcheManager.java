/*
 * Created on 11/04/2018
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;


import intra.regionemarche.BandoClass;
import intra.regionemarche.ResultClass;
import intra.regionemarche.StrutturaClass;
import intra.regionemarche.TemiRegionaliClass;
import intra.regionemarche.TipiProceduraClass;
import intra.regionemarche.WsBandiLocator;
import intra.regionemarche.WsBandiSoap;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;

import java.math.BigDecimal;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;
import org.apache.log4j.Logger;

/**
 * Manager che si occupa di gestire tutte le operazioni di business logic sulla
 * parte relativa all'integrazione con Regione Marche
 *
 * @author Marcello Caminiti
 */

public class GestioneRegioneMarcheManager {

  static Logger               logger                                          = Logger.getLogger(GestioneRegioneMarcheManager.class);

  private SqlManager            sqlManager;
  private PgManagerEst1         pgManagerEst1;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
    this.pgManagerEst1 = pgManagerEst1;
  }


  /**
   * Restituisce puntatore al servizio WS Bandi.
   *
   * @return WsBandiSoap
   * @throws GestoreException
   */
  private WsBandiSoap getWSBandi() throws GestoreException {

    WsBandiSoap wsbandi = null;
    String url = ConfigManager.getValore(CostantiAppalti.PROP_WSBANDI_URL);

    WsBandiLocator bandiLocator = new WsBandiLocator();

    bandiLocator.setwsBandiSoapEndpointAddress(url);
    try {
      Remote remote = bandiLocator.getPort(WsBandiSoap.class);
      Stub axisPort = (Stub) remote;
      wsbandi = (WsBandiSoap) axisPort;
    } catch (ServiceException e) {
      throw new GestoreException("Si e' verificato un errore durante la lettura del servio remoto di pubblicazione bandi della Regione Marche: " + e.getMessage(),
          "ws.bandiRegioneMarche.servizio.error", e);
    }

    return wsbandi;

  }

  /**
   * Restituisce l'elenco dei temi regionali
   * @return TemiRegionaliClass[]
   * @throws GestoreException
   */
  public TemiRegionaliClass[] getTemiRegionali() throws GestoreException{
    TemiRegionaliClass temiRegionali[]=null;
    WsBandiSoap wsBandi = this.getWSBandi();
    try {
      temiRegionali = wsBandi.getTemiRegionali();
    } catch (Throwable e) {
      throw new GestoreException("Si e' verificato un errore durante la lettura della lista dei temi regionali del servizio remoto di pubblicazione bandi della Regione Marche: " + e.getMessage(),
          "ws.bandiRegioneMarche.temiRegionali.error", e);
    }
    return temiRegionali;
  }

  /**
   * Restituisce l'elenco delle tipologie di procedure
   * @return TipiProceduraClass[]
   * @throws GestoreException
   */
  public TipiProceduraClass[] getTipologieProcedure() throws GestoreException{
    TipiProceduraClass tipologiaProcedure[]=null;
    WsBandiSoap wsBandi = this.getWSBandi();
    try {
      tipologiaProcedure = wsBandi.getTipologiaProcedure();
    } catch (Throwable e) {
      throw new GestoreException("Si e' verificato un errore durante la lettura della lista delle tipologie di procedure del servizio remoto di pubblicazione bandi della Regione Marche: " + e.getMessage(),
          "ws.bandiRegioneMarche.tipiProcedure.error", e);
    }
    return tipologiaProcedure;
  }

  /**
   * Metodo che restituisce l'elenco delle strutture regionali
   * @return StrutturaClass[]
   * @throws GestoreException
   */
  public StrutturaClass[] getStruttureRegionali() throws GestoreException{
    StrutturaClass strutture[]=null;
    WsBandiSoap wsBandi = this.getWSBandi();
    try {
      strutture = wsBandi.getStruttureRegionali();
    } catch (Throwable e) {
      throw new GestoreException("Si e' verificato un errore durante la lettura della lista delle strutture regionali del servizio remoto di pubblicazione bandi della Regione Marche: " + e.getMessage(),
          "ws.bandiRegioneMarche.struttureRegionali.error", e);
    }
    return strutture;
  }

  /**
   * Restituisce se esiste un bando individuato tramite il codice della gara
   * @param codiceGara
   * @return ResultClass
   * @throws GestoreException
   */
  public ResultClass getBando(String codiceGara) throws GestoreException{
    ResultClass bando = null;
    WsBandiSoap wsBandi = this.getWSBandi();
    try {
      bando = wsBandi.getBandoByCodiceGara(codiceGara);
    } catch (Throwable e) {
      throw new GestoreException("Si e' verificato un errore nel servizio remoto di pubblicazione bandi della Regione Marche per la lettura del bando della gara: " + codiceGara + " " + e.getMessage(),
          "ws.bandiRegioneMarche.getBando.error", e);
    }
    return bando;
  }


  /**
   * Metodo che valorizza i campi dell'oggetto BandoClass
   * @param ngara
   * @param codiceGara
   * @param genereGara
   * @param dataPub
   * @param strutturaReg
   * @param temaReg
   * @param tipologiaBando
   * @param tipoBando
   * @param bando
   * @throws SQLException
   * @throws GestoreException
   * @throws ParseException
   */
  private void setDatiBando(String ngara, String codiceGara,Long genereGara, Timestamp dataPub, Long strutturaReg, Long temaReg, Long tipologiaBando, String tipoBando, BandoClass bando) throws SQLException, GestoreException, ParseException{
  //Codice gara
    String codGara=ngara;
    if(genereGara.longValue()==1 || genereGara.longValue()==3)
      codGara=codiceGara;
    bando.setCodiceGara(codGara);

    //Codice cig e importo
    String cig=null;
    Double importo= null;
    String selectDati="";
    Object par[]= new Object[1];
    if(genereGara.longValue()==1 || genereGara.longValue()==3){
      //cig=(String)this.sqlManager.getObject("select codcig,importo from v_gare_torn where codgar=?", new Object[]{codiceGara});
      selectDati = "select codcig,importo from v_gare_torn where codgar=?";
      par[0]= codiceGara;
    }else {
      //cig=(String)this.sqlManager.getObject("select codcig, impapp from gare where ngara=?", new Object[]{ngara});
      selectDati = "select codcig, impapp from gare where ngara=?";
      par[0]= ngara;
    }
    Vector<?> dati = this.sqlManager.getVector(selectDati, par);
    if(dati!=null && dati.size()>0){
      cig=SqlManager.getValueFromVectorParam(dati, 0).getStringValue();
      importo=SqlManager.getValueFromVectorParam(dati, 1).doubleValue();
    }
    bando.setCig(cig);
    if(importo==null)
      importo= new Double(0);
    bando.setImporto(new BigDecimal((BigDecimal.valueOf(importo)).toPlainString()));

    //Oggetto
    String oggetto=null;
    if(!new Long(11).equals(genereGara))
      oggetto=this.pgManagerEst1.getOggettoGara(codGara, codiceGara, genereGara);
    else
      oggetto=(String)this.sqlManager.getObject("select oggetto from gareavvisi where ngara=?", new Object[]{ngara});
    bando.setOggetto(oggetto);

    TimeZone tzUTC = TimeZone.getTimeZone("UTC");
    //Data pubblicazione
    Date dataPubblicazione = new Date(dataPub.getTime());
    dataPubblicazione = this.setTimeZone(dataPubblicazione, tzUTC);
    Calendar cal = Calendar.getInstance(tzUTC);
    cal.setTime(dataPubblicazione);
    bando.setDataPubblicazione(cal);

    String select="select iterga, dtepar, otepar, dteoff, oteoff, codrup, altrisog,sommaur from torn where codgar=?";
    Vector<?> datiTorn = this.sqlManager.getVector(select, new Object[]{codiceGara});
    Long iterga=null;
    Timestamp dtepar=null;
    Timestamp dteoff=null;
    String otepar=null;
    String oteoff=null;
    String codrup=null;
    String oraScadenza="";
    String minutiScadenza="";
    Long altrisog = null;
    String sommaur = "";
    if(datiTorn!=null && datiTorn.size()>0){
      iterga= SqlManager.getValueFromVectorParam(datiTorn, 0).longValue();
      dtepar=SqlManager.getValueFromVectorParam(datiTorn, 1).dataValue();
      otepar=SqlManager.getValueFromVectorParam(datiTorn, 2).getStringValue();
      dteoff=SqlManager.getValueFromVectorParam(datiTorn, 3).dataValue();
      oteoff=SqlManager.getValueFromVectorParam(datiTorn, 4).getStringValue();
      codrup=SqlManager.getValueFromVectorParam(datiTorn, 5).getStringValue();
      altrisog = SqlManager.getValueFromVectorParam(datiTorn, 6).longValue();
      sommaur=SqlManager.getValueFromVectorParam(datiTorn, 7).getStringValue();
    }

    //Tipo profilo committente
    if(altrisog==null)
      altrisog=new Long(1);
    bando.setTipoProfiloCommittente(new Integer(altrisog.intValue()));

    //Somma urgenza
    boolean sommaUrg=false;
    if("1".equals(sommaur))
      sommaUrg=true;
    bando.setSommaUrgenza(sommaUrg);

    //Calcolo data scadenza
    Calendar cal1 = Calendar.getInstance(tzUTC);
    Date dataScadenza=null;
    boolean dataAvvisoValorizzata=false;
    if(new Long(11).equals(genereGara)){
      //Per gli avvisi si deve considerare il campo GAREAVVISI.DATSCA se è valorizzato
      Date datsca= (Date)this.sqlManager.getObject("select datsca from gareavvisi where ngara=?", new Object[]{ngara});
      if(datsca!=null){
        datsca = this.setTimeZone(datsca, tzUTC);
        cal1.setTime(datsca);
        cal1.add(Calendar.HOUR, 23);
        cal1.add(Calendar.MINUTE, 59);
        dataAvvisoValorizzata=true;
      }
    }else if(new Long(10).equals(genereGara) || new Long(20).equals(genereGara)){
      //Per gli elenchi e cataloghi si deve considerare il max del campo PUBBTERM .DTERMPRES se valorizzato
      Vector datiPubbter = this.sqlManager.getVector("select dtermpres, otermpres from pubbterm where ngara =? and dtermpres is not null order by dtermpres desc", new Object[]{ngara});
      if(datiPubbter!=null && datiPubbter.size()>0){
        Date dtermpres = (Date)SqlManager.getValueFromVectorParam(datiPubbter, 0).getValue();
        String otermpres = SqlManager.getValueFromVectorParam(datiPubbter, 1).getStringValue();
        if(dtermpres!=null){
          dtermpres = this.setTimeZone(dtermpres, tzUTC);
          cal1.setTime(dtermpres);
          if(otermpres!=null && !"".equals(otermpres)){
            oraScadenza = otermpres.substring(0, 2);
            minutiScadenza = otermpres.substring(3);
            cal1.add(Calendar.HOUR, new Integer(oraScadenza).intValue());
            cal1.add(Calendar.MINUTE, new Integer(minutiScadenza).intValue());
          }else{
            cal1.add(Calendar.HOUR, 23);
            cal1.add(Calendar.MINUTE, 59);
          }
          dataAvvisoValorizzata=true;
        }
      }
    }

    if("ESITO".equals(tipoBando) || (!dataAvvisoValorizzata && (new Long(10).equals(genereGara) || new Long(20).equals(genereGara) || new Long(11).equals(genereGara)))){
      //Per gli esiti o avvisi, elenchi e cataloghi senza data valorizzata
      cal1.setTime(dataPubblicazione);
    }else if(genereGara.longValue()!=10 && genereGara.longValue()!=11 && genereGara.longValue()!=20){
      if((new Long(2)).equals(iterga) || (new Long(4)).equals(iterga)){
        dataScadenza = new Date(dtepar.getTime());
        oraScadenza = otepar.substring(0, 2);
        minutiScadenza = otepar.substring(3);
      }else{
        dataScadenza = new Date(dteoff.getTime());
        oraScadenza = oteoff.substring(0, 2);
        minutiScadenza = oteoff.substring(3);
      }
      dataScadenza = this.setTimeZone(dataScadenza, tzUTC);
      cal1.setTime(dataScadenza);
      cal1.add(Calendar.HOUR, new Integer(oraScadenza).intValue());
      cal1.add(Calendar.MINUTE, new Integer(minutiScadenza).intValue());
    }

    bando.setDataScadenza(cal1);

    //Struttura regionale
    bando.setStrutturaRiferimentoID(strutturaReg.intValue());

    //Tema regionale
    bando.setTemaRegionaleID(temaReg.intValue());

    //Tipologia bando
    bando.setTipologiaProcedura(tipologiaBando.intValue());


    Vector<?> datiTecni = this.sqlManager.getVector("select nomtec, teltec, ematec from tecni where codtec=?", new Object[]{codrup});
    String nomtec = "";
    String teltec = "";
    String ematec = "";
    if(datiTecni!=null && datiTecni.size()>0){
      nomtec = SqlManager.getValueFromVectorParam(datiTecni, 0).getStringValue();
      teltec = SqlManager.getValueFromVectorParam(datiTecni, 1).getStringValue();
      ematec = SqlManager.getValueFromVectorParam(datiTecni, 2).getStringValue();
    }

    //Nome RUP
    bando.setNomeRup(nomtec);

    //Telefono RUP
    bando.setTelefonoRup(teltec);

    //Email RUP
    bando.setEmailRup(ematec);

    String urlAnagrafica="";
    if("BANDO".equals(tipoBando)){
      if(new Long(10).equals(genereGara) || new Long(20).equals(genereGara) || new Long(11).equals(genereGara))
        urlAnagrafica=ConfigManager.getValore(CostantiAppalti.PROP_WS_PORTALEAPPALTI_AVVISI_URL);
      else
        urlAnagrafica=ConfigManager.getValore(CostantiAppalti.PROP_WS_PORTALEAPPALTI_BANDI_URL);
    }else
      urlAnagrafica=ConfigManager.getValore(CostantiAppalti.PROP_WS_PORTALEAPPALTI_ESITI_URL);
    urlAnagrafica+=codGara;
    bando.setUrlAnagraficaGara(urlAnagrafica);
  }


  /**
   * Metodo che inserisce un bando
   * @param ngara
   * @param codiceGara
   * @param genereGara
   * @param dataPub
   * @param strutturaReg
   * @param temaReg
   * @param tipologiaBando
   * @param tipoBando
   * @return ResultClass
   * @throws GestoreException
   */
  public ResultClass insertBando(String ngara, String codiceGara,Long genereGara, Timestamp dataPub, Long strutturaReg, Long temaReg, Long tipologiaBando, String tipoBando) throws GestoreException{
    ResultClass risposta=null;

    WsBandiSoap wsBandi = this.getWSBandi();
    BandoClass bando= new BandoClass();

    try {
      this.setDatiBando(ngara, codiceGara, genereGara, dataPub, strutturaReg, temaReg, tipologiaBando, tipoBando, bando);
      risposta=wsBandi.addBando(bando);
      //risposta=wsBandi.addBandoTest(bando);
    } catch (SQLException  e) {
      throw new GestoreException("Si e' verificato un errore nella chiamata al servizio remoto di pubblicazione bandi della Regione Marche per l'inserimento del bando della gara: " + bando.getCodiceGara() + " " + e.getMessage(),
          "ws.bandiRegioneMarche.addBando.error", e);
    } catch (RemoteException e) {
      throw new GestoreException("Si e' verificato un errore nella chiamata al servizio remoto di pubblicazione bandi della Regione Marche per l'inserimento del bando della gara: " + bando.getCodiceGara() + " " + e.getMessage(),
          "ws.bandiRegioneMarche.addBando.error", e);
    }catch (ParseException e) {
      throw new GestoreException("Si e' verificato un errore nella chiamata al servizio remoto di pubblicazione bandi della Regione Marche per l'inserimento del bando della gara: " + bando.getCodiceGara() + " " + e.getMessage(),
          "ws.bandiRegioneMarche.addBando.error", e);
    }
    return risposta;
  }

  /**
   * Metodo che aggiorna un bando ricavando tutti i dati dalla gara
   * @param ngara
   * @param codiceGara
   * @param genereGara
   * @param dataPub
   * @param strutturaReg
   * @param temaReg
   * @param tipologiaBando
   * @param tipoBando
   * @param idBando
   * @return ResultClass
   * @throws GestoreException
   */
  public ResultClass updateBando(String ngara, String codiceGara,Long genereGara, Timestamp dataPub, Long strutturaReg, Long temaReg, Long tipologiaBando, String tipoBando, Long idBando) throws GestoreException{
    WsBandiSoap wsBandi=this.getWSBandi();
    BandoClass bando= new BandoClass();
    ResultClass risposta=null;

    try {
      this.setDatiBando(ngara, codiceGara, genereGara, dataPub, strutturaReg, temaReg, tipologiaBando, tipoBando, bando);
      risposta=wsBandi.updateBando(bando);

    } catch (SQLException e) {
      throw new GestoreException("Si e' verificato un errore nella chiamata al servizio remoto di pubblicazione bandi della Regione Marche per l'aggiornamento del bando:" + idBando.toString() + " della gara: " + bando.getCodiceGara() + " " + e.getMessage(),
          "ws.bandiRegioneMarche.updateBando.error", e);
    } catch (RemoteException e) {
      throw new GestoreException("Si e' verificato un errore nella chiamata al servizio remoto di pubblicazione bandi della Regione Marche per l'aggiornamento del bando:" + idBando.toString() + " della gara: " + bando.getCodiceGara() + " " + e.getMessage(),
          "ws.bandiRegioneMarche.updateBando.error", e);
    }catch (ParseException e) {
      throw new GestoreException("Si e' verificato un errore nella chiamata al servizio remoto di pubblicazione bandi della Regione Marche per l'aggiornamento del bando:" + idBando.toString() + " della gara: " + bando.getCodiceGara() + " " + e.getMessage(),
          "ws.bandiRegioneMarche.updateBando.error", e);
    }

    return risposta;
  }

  /**
   * Metodo che aggiornda un Bando a partire da un oggetto Bando
   * @param bando
   * @return ResultClass
   * @throws GestoreException
   */
  public ResultClass updateBando(BandoClass bando) throws GestoreException{
    WsBandiSoap wsBandi=this.getWSBandi();
    ResultClass risposta=null;
    try {
      risposta=wsBandi.updateBando(bando);
    } catch (RemoteException e) {
      throw new GestoreException("Si e' verificato un errore nella chiamata al servizio remoto di pubblicazione bandi della Regione Marche per l'aggiornamento del bando:" + bando.getBandoID().toString() + " della gara: " + bando.getCodiceGara() + " " + e.getMessage(),
          "ws.bandiRegioneMarche.updateBando.error", e);
    }
    return risposta;
  }

  /**
   * Metodo per impostare il fuso orario ad una data.
   *
   * @param data
   * @param tz
   * @return Ritorna la data in cui è stato impostato il timezone
   * @throws ParseException ParseException
   */

  public Date setTimeZone(Date data, TimeZone tz)  throws ParseException{
    if (logger.isDebugEnabled())
      logger.debug("dateString2Calendar: inizio metodo");

    String dataString = UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA);
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    sdf.setTimeZone(tz);

    java.util.Date parsed = sdf.parse(dataString);

    return parsed;

  }
}
