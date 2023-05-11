/*
 * Created on 17/feb/2017
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.utils.AllegatoSintesiUtils;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiRicezioneFunction;
import it.eldasoft.sil.pg.web.struts.UploadMultiploForm;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityFiscali;
import it.eldasoft.utils.utility.UtilityMath;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Classe di estensione della gestione delle funzionalita' comuni di PG
 *
 * @author M.Caminiti
 */
public class PgManagerEst1 {

  static Logger               logger                        = Logger.getLogger(PgManagerEst1.class);

  private static final String REPLACEMENT_DOCMANCANTI = "#DOCMANCANTI#";
  public static final String QFORM_NON_ABILITATO = "NOQFORM";
  public static final String QFORM_INSERIMENTO = "INSQFORM";
  public static final String QFORM_INSERIMENTO_OBBLIGO = "INSQFORM_OBBLIGO";
  public static final String QFORM_VISUALIZZAZIONE = "VISQFORM";
  public static final String QFORM_VOCEPROFILO_TUTTE_BUSTE = "ALT.GARE.QuestionariQForm.associazioneBusta.tutte";
  public static final String QFORM_TEC_LISTA_LOTTI = "listaTEC";
  public static final String QFORM_ECO_LISTA_LOTTI = "listaECO";
  public static final String ELENCO_DOC_RINNOVO = "RINN";
  public static final String ELENCO_DOC_ISCRIZIONE = "ISCR";
  public static final String OPZIONE_QFORM_ELENCHI = "OP136";


  private SqlManager          sqlManager;

  private PgManager          pgManager;

  private TabellatiManager          tabellatiManager;

  private InviaDatiRichiestaCigManager          inviaDatiRichiestaCigManager;

  /** Manager per la gestione delle chiavi di una entita */
  private GenChiaviManager   genChiaviManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setPgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }

  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  public void setInviaDatiRichiestaCigManager(InviaDatiRichiestaCigManager inviaDatiRichiestaCigManager) {
    this.inviaDatiRichiestaCigManager = inviaDatiRichiestaCigManager;
  }

  /**
   * Determina se esiste la pubblicazione su portale (o del bando o dell'esito) della gara/lotto
   * Nel caso di pubblicazione bando si può specificare se controllare contemporaneamente la tipologia
   * 11 e 13, oppure separatament.
   * Si può anche specificare se eseguire il controllo sul singolo lotto o su tutti i lotti nel caso
   * di controllo sulla pubblicazione esito
   *
   * @param chiaveGara
   * @param  tipologiaPubblicazione
   *            BANDO, BANDO11, BANDO13, ESITO
   * @param tuttiLotti
   *            se true si controlla la pubblicazione dell'esito su tutti i lotti
   * @return "TRUE" se esite la pubblicazione su portale, altrimenti "FALSE"
   * @throws JspException
   * @throws SQLException
   */
  public String esisteBloccoPubblicazionePortale(String chiaveGara, String tipologiaPubblicazione, boolean tuttiLotti)
      throws SQLException{
    String select="";

    if("BANDO".equals(tipologiaPubblicazione)){
      select = "select count(CODGAR9) from PUBBLI where CODGAR9 = ? and (TIPPUB=11 or TIPPUB=13 or TIPPUB=23)";
    }else if("BANDO11".equals(tipologiaPubblicazione)){
      select = "select count(CODGAR9) from PUBBLI where CODGAR9 = ? and TIPPUB=11";
    }else if("BANDO13".equals(tipologiaPubblicazione)){
      select = "select count(CODGAR9) from PUBBLI where CODGAR9 = ? and (TIPPUB=13 or TIPPUB=23)";
    }else{
      if(!tuttiLotti)
        select = "select count(NGARA) from PUBG where NGARA = ? and (TIPPUBG=12)";
      else
        select = "select count(ngara) from pubg where ngara in (select ngara from gare where codgar1=?) and tippubg=12";
    }

    String pubblicazioneSuPortale = "FALSE";

    Long numeroPubblicazioni = (Long) sqlManager.getObject(select, new Object[] { chiaveGara });

    if (numeroPubblicazioni != null && numeroPubblicazioni.longValue()>0)
        pubblicazioneSuPortale = "TRUE";

    return pubblicazioneSuPortale;
  }

  /**
   * Viene determinato se per la gara deve essere gestita l'offerta economica, andato a controllare il campo
   * GARE1.COSTOFISSO.
   * Per le gare ad offerta unica con bustalotti=2, tutti i lotti devono avere GARE1.COSTOFISSO=1 per evitare
   * la gestione dell'offerta economica
   *
   * @param codiceGara
   * @param  bustalotti
   * @return boolean
   * @throws GestoreException
   * @throws SQLException
   */
  public boolean gestioneOffertaEconomicaDaCostofisso(String codiceGara, String bustalotti) throws SQLException, GestoreException{
    boolean visOffertaEco=true;
    if(!"2".equals(bustalotti)){
      String costofisso = (String)sqlManager.getObject("select costofisso from gare1 where ngara=?", new Object[]{codiceGara});
      if("1".equals(costofisso))
        visOffertaEco = false;
    }else{
      //Se tutti i lotti hanno costofisso=1 non si visualizza la fase di apertura delle offerte economiche
      List<?> listaCostofissoLotti = sqlManager.getListVector("select costofisso from gare1 where codgar1=? and ngara!=codgar1", new Object[]{codiceGara});
      if(listaCostofissoLotti!=null && listaCostofissoLotti.size()>0){
        String costofisso = null;
        int numLottiCostofisso=0;
        for(int i=0;i<listaCostofissoLotti.size(); i++){
          costofisso = SqlManager.getValueFromVectorParam(listaCostofissoLotti.get(i),0).stringValue();
          if(!"1".equals(costofisso))
            break;
          else
            numLottiCostofisso++;
        }
        if(numLottiCostofisso==listaCostofissoLotti.size())
          visOffertaEco = false;
      }
    }
    return visOffertaEco;
  }

  /**
   * Viene controllato se nella gara esistono delle ditte con punteggio Tecnico (o economico) valorizzato
   * @param ngara
   * @param  tipoPunteggio
   * @return boolean
   * @throws SQLException
   */
  public boolean esistonoDittePunteggioValorizzato(String ngara, String tipoPunteggio) throws SQLException{
    boolean ret=false;

    String nomeCampo="puntec";
    Long fase = new Long(5);
    if("ECO".equals(tipoPunteggio)){
      nomeCampo="puneco";
      fase = new Long(6);
    }
    String select="select count(ngara5) from ditg where ngara5=? and (fasgar>=? or fasgar is null) and " + nomeCampo + " is not null";
    Long conteggioDitteConPunteggio = (Long)sqlManager.getObject(select, new Object[]{ngara,fase});
    if(conteggioDitteConPunteggio != null && conteggioDitteConPunteggio.longValue()>0)
      ret=true;

    return ret;
  }

  /**
   * Metodo che determina il valore da assegnare al campo modrea
   *
   * @param accqua
   * @param altrisog
   * @param tipgar
   * @throws GestoreException
   * @return    String
   */
  public String getModrea(String accqua, Long altrisog, Long tipgar) throws GestoreException{
    String ret="";
    if("1".equals(accqua) || (altrisog!= null && (altrisog.longValue()==2 || altrisog.longValue()==3)))
      ret="17";
    else{
      Long tabRitorno= null;
      List<?> listaValoriTabellato = null;

      try {
        listaValoriTabellato = this.sqlManager.getListVector("select tab2d2, tab2tip from " +
              "tab2 where tab2cod=? and tab2d2 is not null and tab2d2 like ?", new Object[] {
            "A1z06", "%" + tipgar.toString() + "%" });
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella lettura del tabellato A1z06",
            null, e);
      }

      if(listaValoriTabellato!=null && listaValoriTabellato.size()>0){
        for (int j = 0; j < listaValoriTabellato.size(); j++) {
          String tab2d2 = SqlManager.getValueFromVectorParam(listaValoriTabellato.get(j), 0).getStringValue();
          if(tab2d2.contains(",")){
            String vetValori[] = tab2d2.split(",");
            for(int z=0;z<vetValori.length;z++ ){
              if (tipgar.longValue()== (new Long(vetValori[z]).longValue())){
                tabRitorno = new Long(SqlManager.getValueFromVectorParam(listaValoriTabellato.get(j), 1).getStringValue());
                break;
              }
            }
          }else{
            if (tipgar.longValue()== (new Long(tab2d2).longValue())){
              tabRitorno = new Long(SqlManager.getValueFromVectorParam(listaValoriTabellato.get(j), 1).getStringValue());
              break;
            }
          }
        }
        if(tabRitorno==null )
          ret = "1";
        else
          ret = tabRitorno.toString();
      }else{
        ret = "1";
      }
    }
    return ret;
  }

  /**
   * Metodo che determina il numero di criteri economici con ISNOPRZ=1
   *
   * @param ngara
   * @throws SQLException
   * @return    Long
   */
  public Long getNumCriteriEcoNoPrezzo(String ngara) throws SQLException{
    Long numTotCriteriEcoNoPrez=(Long)sqlManager.getObject("select count(ngara) from goev where ngara=? and tippar=? and livpar in (1,3) and ISNOPRZ = ?", new Object[]{ngara, new Long(2), "1"});
    return numTotCriteriEcoNoPrez;
  }

  /**
   * Metodo che determina se esistono criteri economici di tipo prezzo
   *
   * @param ngara
   * @throws SQLException
   * @return    boolean
   */
  public boolean esistonoCriteriEconomiciPrezzo(String ngara) throws SQLException{
    boolean ret= true;
    Long numTotCriteriEco=(Long)sqlManager.getObject("select count(ngara) from goev where ngara=? and tippar=? and livpar in (1,3)", new Object[]{ngara, new Long(2)});
    Long numTotCriteriEcoNoPrez=this.getNumCriteriEcoNoPrezzo(ngara);
    if(numTotCriteriEco!=null && numTotCriteriEcoNoPrez!=null && numTotCriteriEco.longValue()>0 && numTotCriteriEco.longValue()==numTotCriteriEcoNoPrez.longValue())
      return false;
    return ret;
  }

  /**
   * Metodo che permette il ricalcolo del numero ordine della documentazione di gara
   *
   * @param codgar
   * @param gruppo
   * @throws SQLException
   * @throws GestoreException
   */
  public void ricalcNumordDocGara(String codgar, Long gruppo) throws GestoreException{
    List<?> listaDocumentiGara = null;
    Long norddocg= null;
    String select="";
    //differenziato order by su NUMORD nel caso di sqlserver (sqlserver di default mette i valori null in testa anziché in coda)
    String db =ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);
    if("MSQ".equals(db)){
      select= "select CODGAR, NGARA, TAB1NORD, FASELE, NORDDOCG, NUMORD, DESCRIZIONE from DOCUMGARA left join TAB1 on TAB1COD=? and TAB1TIP=BUSTA where CODGAR=? and GRUPPO = ? order by TAB1NORD,FASELE,isnull(NUMORD,10000),NORDDOCG";
    }else{
      select= "select CODGAR, NGARA, TAB1NORD, FASELE, NORDDOCG, NUMORD, DESCRIZIONE from DOCUMGARA left join TAB1 on TAB1COD=? and TAB1TIP=BUSTA where CODGAR=? and GRUPPO = ? order by TAB1NORD,FASELE,NUMORD,NORDDOCG";
    }
    try {
      listaDocumentiGara = sqlManager.getListVector(select, new Object[] { "A1013", codgar, gruppo});
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della tabella DOCUMGARA ", null, e);
    }
    if (listaDocumentiGara != null && listaDocumentiGara.size() > 0) {
      for (int i = 0; i < listaDocumentiGara.size(); i++) {
        norddocg = SqlManager.getValueFromVectorParam(listaDocumentiGara.get(i), 4).longValue();
        try {
          this.sqlManager.update(
              "update DOCUMGARA set NUMORD = ? where CODGAR=? and NORDDOCG=?",
              new Object[] {i+1 , codgar , norddocg });
        } catch (SQLException e) {
          throw new GestoreException("Errore nell'aggiornamento del campo NUMORD.DOCUMGARA ", null, e);
        }
      }
    }
  }

  /**
   * Viene controllato se la gara corrispondente alla comunicazione è pubblicata su portale, ed in questo caso
   * viene aggiornata la TORN.DULTAGG
   *
   * @param ngara
   * @param idcom
   * @throws GestoreException
   */
  public void aggiornamentoDataAggiornamentoPortaleComunicazione(String ngara,Long idcom) throws GestoreException {
    try {
      String codiceGara = (String)this.sqlManager.getObject("select codgar1 from gare where ngara=?", new Object[]{ngara});
      String pubblicazioneBandoPortale = this.esisteBloccoPubblicazionePortale(codiceGara, "BANDO", false);
      String pubblicazioneEsitoPortale = this.esisteBloccoPubblicazionePortale(codiceGara, "ESITO", true);
      if("TRUE".equals(pubblicazioneBandoPortale) || "TRUE".equals(pubblicazioneEsitoPortale)){
        java.util.Date oggi = UtilityDate.getDataOdiernaAsDate();
        this.sqlManager.update("update TORN set DULTAGG = ? where CODGAR=?",new Object[]{oggi,codiceGara});
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nell'aggiornamento della data ultimo aggiornamento contenuto su portale della comunicazione" + idcom.toString(), null, e);
    }

  }

  /**
   * Viene restituito l'oggetto della gara, che nel caso di gara a lotto unico è GARE.NOT_GAR,
   * nel caso di gare ad offerta unica o offerte distinte è TORN.DESTOR, mentre per elenchi e
   * cataloghi è GARELBO.OGGETTO
   *
   * @param ngara
   * @param codgar
   * @param genereGara
   * @return String
   */
  public String getOggettoGara(String ngara, String codgar, Long genereGara) throws SQLException {
    String chiave= ngara;
    //Se gara è a plico unico si prende GARE.NOT_GAR
    String select = "select not_gar from gare where ngara = ?";
    if(genereGara!=null && (genereGara.longValue()==3 || genereGara.longValue()==1)){
      //Se gara è ad offerta unica o ad offerte distinte si prende TORN.DESTOR
      select = "select destor from torn where codgar = ?";
      if(genereGara.longValue()==1)
        chiave=codgar;
    }else if(genereGara!=null && (genereGara.longValue()==10 || genereGara.longValue()==20)){
      select = "select oggetto from garealbo where ngara = ?";
    }else if(genereGara!=null && genereGara.longValue()==11){
      select = "select oggetto from gareavvisi where ngara = ?";
    }

    String oggetto = (String)sqlManager.getObject(select, new Object[]{chiave});
    return oggetto;

  }

  /**
   * Viene restituito l'oggetto della gara, che nel caso di gara a lotto unico è GARE.NOT_GAR,
   * nel caso di gare ad offerta unica o offerte distinte è TORN.DESTOR, mentre per elenchi e
   * cataloghi è GARELBO.OGGETTO
   * Viene integrata la gestione delle stipule
   *
   * @param ngara
   * @param codgar
   * @param genereGara
   * @param entita
   * @return String
   */
  public String getOggettoGara(String ngara, String codgar, Long genereGara, boolean stipula) throws SQLException {
    String oggetto= "";
    if(stipula){
      Long id=null;
      if(ngara!=null && !"".equals(ngara))
        id=new Long(ngara);
      oggetto=(String)sqlManager.getObject("select oggetto from g1stipula where id=?", new Object[]{id});
    }else {
      oggetto = this.getOggettoGara(ngara, codgar, genereGara);
    }
    return oggetto;

  }

  /**
   * Viene fatto il controllo che tutti gli allegati presenti nella lista(in posizione indicata dal parametro), abbiano
   * un formato specificato fra quelli validi
   * @param listaAllegati
   * @param indiceNomeFile
   * @param formatoAllegati
   * @return boolean
   */
  public boolean controlloAllegatiFormatoValido(List<?> listaAllegati, int indiceNomeFile, String formatoValidiAllegati){
    boolean esito=true;
    int numFormatoCorretto=0;
    if(listaAllegati!=null && listaAllegati.size()>0){
     String nomeFile=null;
     String formatiValidi[] = formatoValidiAllegati.split(";");
      for(int i=0;i<listaAllegati.size();i++){
        nomeFile=SqlManager.getValueFromVectorParam(listaAllegati.get(i), indiceNomeFile).getStringValue();
        if(nomeFile!=null && !"".equals(nomeFile)){
          nomeFile=nomeFile.toUpperCase();
          for(int j=0;j<formatiValidi.length;j++){
            if(formatiValidi[j]!=null && !"".equals(formatiValidi[j]) && nomeFile.lastIndexOf(formatiValidi[j].toUpperCase())>0){
              numFormatoCorretto++;
              break;
            }
          }
        }
      }
    }
    if(numFormatoCorretto!=listaAllegati.size())
      esito=false;
    return esito;
  }

  public Double calcoloValoreMassimoStimato(Double impapp, Double imprin, Double impserv, Double imppror, Double impaltro){
    double valMax=0;
    if(impapp==null)
      impapp=new Double(0);
    valMax=impapp;

    if(imprin ==null )
      imprin=new Double(0);
    valMax+=imprin;

    if(impserv ==null)
        impserv=new Double(0);
    valMax+=impserv;

    if(imppror==null)
        imppror=new Double(0);
    valMax+=imppror;

    if(impaltro==null)
        impaltro=new Double(0);
    valMax+=impaltro;

    return new Double(UtilityMath.round(valMax, 2) );
  }


  /**
   * Vengono aggiornati i valori di TORN.IMPCOR e TORN.ISTAUT tenendo conto del
   * nuovo valore dell'importo del lotto
   *
   * @param codiceGara
   * @param numeroLotto
   * @param importoLotto
   * @param importoLottoRinnovo
   * @throws GestoreException
   */
  public void setImportoTotaleTorn(String codiceGara, String numeroLotto,
      Double importoLotto, Double importoLottoRinnovo) throws GestoreException {

    String select = "select sum(impapp) from gare where codgar1=? and ngara <> ?";

    try {
      Double importoTotaleGara = null;
      Object importoTemp = this.sqlManager.getObject(select, new Object[] {
          codiceGara,numeroLotto });
      if (importoTemp != null) {
        if (importoTemp instanceof Long) {
          importoTotaleGara = new Double(((Long) importoTemp));
        } else if (importoTemp instanceof Double) {
          importoTotaleGara = new Double((Double) importoTemp);
        }

      }

      if (importoTotaleGara == null) importoTotaleGara = new Double(0);

      if (importoLotto == null) importoLotto = new Double(0);
      importoTotaleGara = new Double(importoTotaleGara.doubleValue()
          + importoLotto.doubleValue());

      select = "select sum(valmax) from v_gare_importi where codgar=? and ngara is not null and ngara <>?";
      Double importoTotaleValmax = new Double(0);
      importoTemp = this.sqlManager.getObject(select, new Object[] {
          codiceGara,numeroLotto });
      if (importoTemp != null) {
        if (importoTemp instanceof Long) {
          importoTotaleValmax = new Double(((Long) importoTemp));
        } else if (importoTemp instanceof Double) {
          importoTotaleValmax = new Double((Double) importoTemp);
        }

      }

      Double maxVal = null;
      if(importoLottoRinnovo==null){
        Double imprinLotto= new Double(0);;
        Double impservLotto= new Double(0);
        Double impprorLotto= new Double(0);
        Double impaltroLotto= new Double(0);
        Vector<?> datiGare1=this.sqlManager.getVector("select imprin,impserv,imppror,impaltro from gare1 where ngara=?", new Object[]{numeroLotto});
        if(datiGare1!=null && datiGare1.size()>0){
          imprinLotto= SqlManager.getValueFromVectorParam(datiGare1, 0).doubleValue();
          impservLotto= SqlManager.getValueFromVectorParam(datiGare1, 1).doubleValue();
          impprorLotto= SqlManager.getValueFromVectorParam(datiGare1, 2).doubleValue();
          impaltroLotto= SqlManager.getValueFromVectorParam(datiGare1, 3).doubleValue();
        }
        maxVal = this.calcoloValoreMassimoStimato(importoLotto, imprinLotto, impservLotto, impprorLotto, impaltroLotto);
      }else
        maxVal = importoLottoRinnovo;


      importoTotaleValmax = new Double(importoTotaleValmax.doubleValue()
          + maxVal.doubleValue());

      Double importoContributo = this.pgManager.getContributoAutoritaStAppaltante(
          importoTotaleValmax.doubleValue(), "A1z02");
      this.sqlManager.update(
          "update torn set imptor = ?,istaut = ? where codgar = ? ",
          new Object[] { importoTotaleGara, importoContributo, codiceGara });
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante la lettura degli importi dei lotti di gara", null, e);
    }
  }

  /**
   * Determina gli importi degli scaglioni della Stazione appaltante
   * e li carica in una lista
   *
   * @param tabellato
   * @param List
   * @throws SQLException
   */
  public List<Tabellato> getScaglioni(String tabellato)
      throws SQLException {
    List<Tabellato> listaScaglioni = null;
    List<?> lista = tabellatiManager.getTabellato(tabellato);
    if (lista != null && lista.size() > 0) {
        String importoContributo = null;
        String descImportoMassimoGara = null;
        int posizioneSpazio = -1;
        Double importoEstratto = null;
        double importoMassimoGara ;
        listaScaglioni = new ArrayList<Tabellato>(lista.size());
        for (int i = 0; i < lista.size(); i++) {
            importoContributo = ((Tabellato) lista.get(i)).getDatoSupplementare();
            descImportoMassimoGara = ((Tabellato) lista.get(i)).getDescTabellato();
            posizioneSpazio = descImportoMassimoGara.indexOf(' ');
            importoEstratto = null;
            if (posizioneSpazio > 0)
                importoEstratto = UtilityNumeri.convertiDouble(
                        UtilityStringhe.replace(descImportoMassimoGara.substring(0,
                                posizioneSpazio), ",", "."),
                                UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE);

            // l'ultima riga non indica il limite massimo, perchè non esiste,
            // per cui si fissa un limite massimo più alto possibile in modo
            // da essere sempre sotto
          if (importoEstratto != null)
            importoMassimoGara = importoEstratto.doubleValue();
          else
            importoMassimoGara = Double.MAX_VALUE;

          if (importoContributo == null) importoContributo = "";

          Tabellato singoloScaglione = new Tabellato();
          singoloScaglione.setTipoTabellato(importoContributo);
          singoloScaglione.setDescTabellato(Double.toString(importoMassimoGara));
          listaScaglioni.add(singoloScaglione);
        }
    }
    return listaScaglioni;
  }

  /**
   * Determina se in gara sono presenti delle comunicazioni
   *
   * @param ngara
   * @param tipoComunicazione
   * @return boolean
   *
   * @throws SQLException
   */
  public boolean esistonoComunicazioni(String ngara, String tipoComunicazione) throws SQLException{
    boolean esistonoComunicazioni = false;

    if (ngara != null) {
        String selectW_INVCOM = "select count(*) from w_invcom where comtipo = ? and comkey2 = ?";
        Long conteggio = (Long) sqlManager.getObject(selectW_INVCOM, new Object[] {tipoComunicazione, ngara});

        if (conteggio != null && conteggio.longValue() > 0) {
          esistonoComunicazioni = true;
        }
    }

    return esistonoComunicazioni;

  }

  /**
   * Vengono controllati i permessi per l'utente corrente, andando a controllare l'abilitazione a livello di USRSYS
   * e i permessi sulla G_PERMESSI. Per le gare telematiche e per le ODA(entità GARECONT) si controlla anche
   * G_PERMESSI.MERUOLO, ma solo se il tabellato A1152 abilita il controllo
   *
   * @param entita
   * @param chiave
   * @param profilo
   * @param controlloTabellatoTelematiche
   * @return String[]
   *        true(autorizzato), false
   *        true(autorizzato) per gara telematica o per ODA
   * @throws SQLException
   */
  public String[] controlloPermessiModificaUtente(String entita, String chiave,ProfiloUtente profilo, boolean controlloTabellatoTelematiche) throws SQLException{
    String codgar = "";
    String codlav = "";
    Long idstipula = null;
    Long idMeric=null;
    String autorizzato = "false";
    String autorizzatoTelematicaOda = "true";

    if ("PERI".equals(entita) || "APPA".equals(entita)) {
       codlav = chiave;
    }else if("GARECONT".equals(entita)){
       idMeric= new Long(chiave);
    }else if("G1STIPULA".equals(entita)){
      idstipula= new Long(chiave);
    }else {
       codgar = chiave;
    }

    Long syscon = new Long(profilo.getId());
    if ("PERI".equals(entita) || "APPA".equals(entita)) {
      String abilitazioneStd = new String (profilo.getAbilitazioneStd());
      Long autori = (Long) sqlManager.getObject(
              "select autori from g_permessi where codlav = ? and syscon = ?",
              new Object[] { codlav, syscon });

          if ((new Long(1)).equals(autori) || (new String("A")).equals(abilitazioneStd)) {
            autorizzato = "true";
          }
     }else if("NSO_ORDINI".equals(entita)){
      autorizzato=String.valueOf(true);
      autorizzatoTelematicaOda = String.valueOf("1".equalsIgnoreCase(profilo.getRuoloUtenteMercatoElettronico()) || "3".equalsIgnoreCase(profilo.getRuoloUtenteMercatoElettronico()));
     }else if("G1STIPULA".equals(entita)){
       String abilitazioneStd = new String (profilo.getAbilitazioneStd());
       Long autori = (Long) sqlManager.getObject(
               "select autori from g_permessi where idstipula = ? and syscon = ?",
               new Object[] { idstipula, syscon });
           if ((new Long(1)).equals(autori) || (new String("A")).equals(abilitazioneStd)) {
             autorizzato = "true";
           }
     }else if("GARECONT".equals(entita)){
      String abilitazioneGare = new String (profilo.getAbilitazioneGare());
      Long autori = (Long) sqlManager.getObject(
              "select autori from g_permessi where idmeric = ? and syscon = ?",
              new Object[] { idMeric, syscon });

          if ((new Long(1)).equals(autori) || (new String("A")).equals(abilitazioneGare)) {
            autorizzato = "true";
            if(controlloTabellatoTelematiche){
              String desc = tabellatiManager.getDescrTabellato("A1152", "1");
              if(desc!=null && !"".equals(desc))
                desc=desc.substring(0,1);
              if("1".equals(desc) ){
                Long meruolo = (Long) sqlManager.getObject(
                    "select meruolo from g_permessi where idmeric = ? and syscon = ?",
                    new Object[] { idMeric, syscon });

                if (meruolo==null || (meruolo!=null && meruolo.longValue()==2)) {
                  autorizzatoTelematicaOda = "false";
                }
              }
            }
          }
    }else {
        String abilitazioneGare = new String (profilo.getAbilitazioneGare());
        Long autori = (Long) sqlManager.getObject(
                "select autori from g_permessi where codgar = ? and syscon = ?",
                new Object[] { codgar, syscon });

            if ((new Long(1)).equals(autori) || (new String("A")).equals(abilitazioneGare)) {
              autorizzato = "true";
            }
        //Nel caso di gara telematica si deve controllare anche il ruolo
        if("true".equals(autorizzato)){
          String gartel = (String)sqlManager.getObject(
              "select gartel from torn where codgar = ? ",
              new Object[] { codgar});
          if("1".equals(gartel) && controlloTabellatoTelematiche){
            String desc = tabellatiManager.getDescrTabellato("A1152", "1");
            if(desc!=null && !"".equals(desc))
              desc=desc.substring(0,1);
            if("1".equals(desc)){
              Long meruolo = (Long) sqlManager.getObject(
                  "select meruolo from g_permessi where codgar = ? and syscon = ?",
                  new Object[] { codgar, syscon });

              if (meruolo==null || (meruolo!=null && meruolo.longValue()==2)) {
                autorizzatoTelematicaOda = "false";
              }
            }
          }
        }
    }
    return new String[]{autorizzato,autorizzatoTelematicaOda};
  }

  /**
   * Gestione dell'entità GARALTSOG nel caso in cui sia impostato TORN.ALTRISOG=2
   * @param datiForm
   * @throws GestoreException
   */
  public void gestioneGaraltSog(DataColumnContainer datiForm) throws GestoreException{
    if(datiForm.isColumn("CENINT_GARALTSOG") && (new Long (2)).equals(datiForm.getLong("TORN.ALTRISOG"))){
      String ngara = datiForm.getString("GARE.NGARA");
      String cenint = datiForm.getString("CENINT_GARALTSOG");
      String codrup = datiForm.getString("CODRUP_GARALTSOG");
      try {
        String select = "select count(id) from garaltsog where ngara=?";
        Long conteggio = (Long)this.sqlManager.getObject(select, new Object[]{ngara});
        if((conteggio==null || conteggio.longValue()==0) && (cenint!=null && !"".equals(cenint) || (codrup!=null && !"".equals(codrup)))){
          Long id = new Long(this.genChiaviManager.getNextId("GARALTSOG"));
          this.sqlManager.update("insert into garaltsog(id,ngara,cenint,codrup) values(?,?,?,?)", new Object[]{id, ngara, cenint,codrup});
        }else if(conteggio!=null && conteggio.longValue()!=0){
          if((cenint==null || "".equals(cenint)) && (codrup==null || "".equals(codrup)))
            this.sqlManager.update("delete from garaltsog where ngara=?", new Object[]{ngara});
          else if ( (cenint!=null && !"".equals(cenint)) || (codrup!=null && !"".equals(codrup)))
            this.sqlManager.update("update garaltsog set  cenint =?, codrup =? where ngara=?", new Object[]{cenint, codrup, ngara });
        }


      } catch (SQLException e) {
        throw new GestoreException("Errore nel salvataggio dei dati in GARALTSOG",
            null, e);
      }
    }
  }

  /**
   * Si controlla se vi sono ditte in gara ed è attiva la codifica
   * @param ngara
   * @param iterga
   * @throws GestoreException
   */
  public boolean esistonoDitteInGara(String ngara ) throws GestoreException{
    boolean esistono=false;
    try {
      Long conteggioDitteElenco = (Long)this.sqlManager.getObject("select count(dittao) from ditg where ngara5=?  ",
          new Object[]{ngara});
      if(conteggioDitteElenco!= null && conteggioDitteElenco.longValue()>0){
        esistono=true;
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura delle ditte caricate da elenco",null,e);
    }
    return esistono;
  }

//La funzione elabora la stringa passatale come argomento
  // in modo da visualizzare secondo il formato Money5
/**
 * La funzione elabora la stringa passatale come argomento
   in modo da visualizzare secondo il formato Money5
 *
 * @param importo
 * @return String
 */
  public static String convertiMoney5(String importo) {
      String ret = "";
      if (importo.indexOf(",") >= 0) {
          ret = importo;
          for (int i = 0; i < 3; i++) {
              if (ret.endsWith("0")) {
                  int len = ret.length();
                  ret = ret.substring(0, len - 1);
              }
          }

      } else {
        if(importo==null || "".equals(importo))
          importo="0";
          ret = importo + ",00";
      }
      return ret;
  }

  /**
   * Viene convertito un importo contenuto in un Object in un Double
   * @param importoObj
   * @return Double
   */
  public Double getImportoDaObject(Object importoObj){
    Double importo = null;
    if (importoObj != null) {
      if (importoObj instanceof Long) {
        importo = new Double(((Long) importoObj));
      } else if (importoObj instanceof Double) {
        importo = new Double((Double) importoObj);
      }
    }
    return importo;
  }

  /**
   * Determina se in gara sono presenti delle comunicazioni con un determinato stato
   *
   * @param ngara
   * @param tipoComunicazione
   * @param stato
   * @return boolean
   *
   * @throws SQLException
   */
  public boolean esistonoComunicazioni(String ngara, String tipoComunicazione, String stato) throws SQLException{
    boolean esistonoComunicazioni = false;

    if (ngara != null) {
        String selectW_INVCOM = "select count(*) from w_invcom where comtipo = ? and comkey2 = ? and comstato = ?";
        Long conteggio = (Long) sqlManager.getObject(selectW_INVCOM, new Object[] {tipoComunicazione, ngara, stato});

        if (conteggio != null && conteggio.longValue() > 0) {
          esistonoComunicazioni = true;
        }
    }

    return esistonoComunicazioni;

  }

  /**
   * Il metodo controlla che:
   *  se è valorizzato il cenint, allora devono essere valorizzati il nomein e cfein(che deve contenere un valore valido);
   *  se è valorizzato il codrup, allora devono essere valorizzati il cogtei,nometei, cfein(che deve contenere un valore valido se nazionalità italiana) o pivatec(che deve contenere un valore valido se nazionalità italiana);
   *  se accqua=1, allora aqdurata e aqtempo devono essere valorizzati
   *
   * @param ngara
   * @param codgar
   * @param cenint
   * @param codrup
   * @param tipo
   * @return HashMap<String,String>
   * @throws SQLException
   * @throws GestoreException
   */
  public HashMap<String,String> controlloDatiBloccantiInvioSCP(String ngara,String codgar, String cenint, String codrup, String tipo, Long genere, String profilo) throws SQLException, GestoreException{
    HashMap<String,String> ret=new HashMap<String,String>();
    String erroriBloccanti="NO";
    String msgErroriBloccanti="";

    if(cenint!=null && !"".equals(cenint)){
      Vector<?> datiUffint=this.sqlManager.getVector("select nomein, cfein from uffint where codein = ?", new Object[]{cenint});
      if(datiUffint!=null && datiUffint.size()>0){
        String nomein = (String) SqlManager.getValueFromVectorParam(datiUffint, 0).getValue();
        if (nomein == null || "".equals(nomein)) {
          erroriBloccanti = "SI";
          msgErroriBloccanti += "<br>La denominazione della stazione appaltante non e' valorizzata.";
        }

        String cfein = (String) SqlManager.getValueFromVectorParam(datiUffint, 1).getValue();
        if (cfein != null && !"".equals(cfein)) {
          if (!UtilityFiscali.isValidPartitaIVA(cfein) && !UtilityFiscali.isValidCodiceFiscale(cfein)){
            erroriBloccanti = "SI";
            msgErroriBloccanti += "<br>Il codice fiscale della stazione appaltante non ha un formato valido.";
          }
        }else{
          erroriBloccanti = "SI";
          msgErroriBloccanti += "<br>Il codice fiscale della stazione appaltante non e' valorizzato.";
        }
      }
    }

    if(codrup!=null && !"".equals(codrup)){
      Vector<?> datiTecni=this.sqlManager.getVector("select cogtei, nometei, cftec, pivatec, naztei from tecni where codtec = ?", new Object[]{codrup});
      if(datiTecni!=null && datiTecni.size()>0){
        String cogtei = (String) SqlManager.getValueFromVectorParam(datiTecni, 0).getValue();
        if(cogtei==null || "".equals(cogtei)){
          erroriBloccanti = "SI";
          msgErroriBloccanti += "<br>Il cognome del responsabile unico procedimento non e' valorizzato.";
        }
        String nometei = (String) SqlManager.getValueFromVectorParam(datiTecni, 1).getValue();
        if(nometei==null || "".equals(nometei)){
          erroriBloccanti = "SI";
          msgErroriBloccanti += "<br>Il nome del responsabile unico procedimento non e' valorizzato.";
        }
        String cftec = (String) SqlManager.getValueFromVectorParam(datiTecni, 2).getValue();
        String pivatec = (String) SqlManager.getValueFromVectorParam(datiTecni, 3).getValue();
        cftec=StringUtils.stripToNull(cftec);
        pivatec=StringUtils.stripToNull(pivatec);
        if(cftec == null  && pivatec == null ){
          erroriBloccanti = "SI";
          msgErroriBloccanti += "<br>Il codice fiscale e la partita iva del responsabile unico procedimento sono entrambi non valorizzati.";
        }else{
          Long naztei = SqlManager.getValueFromVectorParam(datiTecni, 4).longValue();
          if(naztei==null || (naztei!=null && naztei.longValue()==1)){
            if(cftec!=null){
              if (!UtilityFiscali.isValidPartitaIVA(cftec) && !UtilityFiscali.isValidCodiceFiscale(cftec)){
                erroriBloccanti = "SI";
                msgErroriBloccanti += "<br>Il codice fiscale del responsabile unico procedimento non ha un formato valido.";
              }
            }if(pivatec!=null){

              if (!UtilityFiscali.isValidPartitaIVA(pivatec) ){
                erroriBloccanti = "SI";
                msgErroriBloccanti += "<br>La partita iva del responsabile unico procedimento non ha un formato valido.";
              }

            }

          }
        }
      }
    }


    if((new Long(1).equals(genere) || new Long(2).equals(genere) || new Long(3).equals(genere)) && !"DELIBERE".equals(tipo)){
      String selectTorn = "select accqua, aqdurata, aqtempo, modrea, codnuts from torn where codgar = ?";
      Vector<?> datiTorn=this.sqlManager.getVector(selectTorn, new Object[]{codgar});
      if(datiTorn!=null && datiTorn.size()>0){

        boolean controlliIntegrazioneSCP=false;
        /*
        String integrazioneSCP = ConfigManager.getValore(ScpManager.PROP_WS_PUBBLICAZIONI_URL);
        if(integrazioneSCP != null && !"".equals(integrazioneSCP)){
          controlliIntegrazioneSCP=true;
          Long numPubbli = (Long)this.sqlManager.getObject("select count(codgar9) from pubbli where codgar9=? and tippub=11", new Object[]{codgar});
          if(!"BANDO".equals(tipo) && numPubbli!=null && numPubbli.longValue()>0){
            controlliIntegrazioneSCP=false;
          }

        }
        */

        //MODREA e CODNUTS
        String modrea = ((JdbcParametro)datiTorn.get(3)).stringValue();
        String codnuts = ((JdbcParametro)datiTorn.get(4)).stringValue();
        String genereString="";
        if(genere!=null)
          genereString = genere.toString();
        String controllo[] = this.inviaDatiRichiestaCigManager.controlloModreaCodnuts(modrea, codnuts, genereString, profilo);
        if("NOK".equals(controllo[0])){
          erroriBloccanti = "SI";
          msgErroriBloccanti+="<br>Non è stata inserita la modalità di realizzazione";
        }
        if("NOK".equals(controllo[1]) && controlliIntegrazioneSCP){
          erroriBloccanti = "SI";
          msgErroriBloccanti+="<br>Non è stato inserito il codice NUTS";
        }

        //GARCPV
        if(controlliIntegrazioneSCP){
          ArrayList<String> errori= this.inviaDatiRichiestaCigManager.getControlloGarcpv(codgar, ngara, genereString, profilo);
          if(errori!=null && errori.size()>0){
            erroriBloccanti = "SI";
            Iterator<String> iter =errori.iterator();
            while (iter.hasNext())
              msgErroriBloccanti+="<br>" + iter.next();

          }
        }

        if("BANDO".equals(tipo) || "INVITO".equals(tipo)){
          String accqua = (String) SqlManager.getValueFromVectorParam(datiTorn, 0).getValue();
          if("1".equals(accqua)){
            Long aqdurata = SqlManager.getValueFromVectorParam(datiTorn, 1).longValue();
            if (aqdurata==null){
              erroriBloccanti = "SI";
              msgErroriBloccanti += "<br>Non è stata inserita la durata dell'accordo quadro.";
            }
            Long aqtempo = SqlManager.getValueFromVectorParam(datiTorn, 2).longValue();
            if (aqtempo==null){
              erroriBloccanti = "SI";
              msgErroriBloccanti += "<br>Non è stato specificato se la durata dell'accordo quadro è espressa in giorni o mesi o anni.";
            }
          }
        }

      }
    }

    ret.put("erroriBloccanti", erroriBloccanti);
    ret.put("msgErroriBloccanti", msgErroriBloccanti);
    return ret;
  }


  /**
   * Il metodo restistuisce l'elenco dei lotti di una gara aggiudicati per una determinata ditta, nel formato
   * 'lotto1','lotto2',...
   * @param codgar
   * @param ditta
   * @return String
   * @throws SQLException
   */
  public String getElencoLottiAggiudicati(String codgar, String ditta) throws SQLException{
    String elencoLotti="";
    String select="select ngara from gare where codgar1=? and ditta=? and genere is null";
    List<?> listaLotti = this.sqlManager.getListVector(select, new Object[]{codgar,ditta});
    if(listaLotti!=null && listaLotti.size()>0){
      for(int i=0;i<listaLotti.size();i++){
        if(i>0)
          elencoLotti+=",";
        elencoLotti+="'" + SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).getStringValue() +"'";
      }
    }
    return elencoLotti;
  }

  /**
   * Eliminazione occorrenza W_DOCDIG.
   *
   * @param idprg
   * @param iddocdig
   * @throws GestoreException
   */
  public void cancellaW_DOCDIG(String idprg, Long iddocdig) throws GestoreException {
    try {
      String deleteW_DOCDIG = "delete from w_docdig where idprg = ? and iddocdig = ?";
      this.sqlManager.update(deleteW_DOCDIG, new Object[] { idprg, iddocdig });
    } catch (SQLException e) {
      throw new GestoreException("Errore nella cancellazione di W_DOCDIG", null, e);
    }
  }

  /**
   * Inserimento in W_DOCDIG.
   *
   * @param indice
   * @param idprg
   * @param digent
   * @return
   * @throws GestoreException
   */
  public Long inserisciW_DOCDIG(HttpServletRequest request, UploadFileForm uploadFileForm, int indice, String idprg, String digent, String digkey1) throws GestoreException {

    String fname = null;
    Long iddocdig = null;
    try {
      HashMap<?, ?> hm = ((UploadMultiploForm) uploadFileForm).getFormFiles();

      DataColumnContainer dccW_DOCDIG = new DataColumnContainer(new DataColumn[] { new DataColumn("W_DOCDIG.IDPRG", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, idprg)) });

      String selectMaxW_DOCDIG = "select max(iddocdig) from w_docdig where idprg = ?";
      iddocdig = (Long) this.sqlManager.getObject(selectMaxW_DOCDIG, new Object[] { idprg });
      if (iddocdig == null) iddocdig = new Long(0);
      iddocdig = new Long(iddocdig.longValue() + 1);
      dccW_DOCDIG.addColumn("W_DOCDIG.IDDOCDIG", JdbcParametro.TIPO_NUMERICO, iddocdig);
      dccW_DOCDIG.addColumn("W_DOCDIG.DIGENT", JdbcParametro.TIPO_TESTO, digent);
      dccW_DOCDIG.addColumn("W_DOCDIG.DIGKEY1", JdbcParametro.TIPO_TESTO, digkey1);

      // Gestione upload del file
      FormFile ff = (FormFile) hm.get(new Long(indice));
      if (ff != null) {
        fname = ff.getFileName();
        if (fname.length() != 0 && ff.getFileSize() > 0) {
          dccW_DOCDIG.addColumn("W_DOCDIG.DIGNOMDOC", JdbcParametro.TIPO_TESTO, fname);
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          baos.write(ff.getFileData());
          dccW_DOCDIG.addColumn("W_DOCDIG.DIGOGG", JdbcParametro.TIPO_BINARIO, baos);
        } else if (fname.length() != 0 && ff.getFileSize() == 0) {
          UtilityStruts.addMessage(request, "warning", "warnings.gare.documentazioneGare.uploadMultiplo.fileVuoto",
              new String[] { fname });
        }
      }
      dccW_DOCDIG.insert("W_DOCDIG", this.sqlManager);

    } catch (FileNotFoundException e) {
      throw new GestoreException("File da caricare non trovato", "uploadMultiplo", new String[] { fname }, e);
    } catch (IOException e) {
      throw new GestoreException("Si è verificato un errore durante la scrittura del buffer per il salvataggio del file "
          + fname
          + " su database", "uploadMultiplo", new String[] { fname }, e);
    } catch (SQLException e) {
      throw new GestoreException("Si è verificato un errore durante l'aggiornamento della tabella W_DOCDIG", null, e);
    }

    return iddocdig;
  }



  /**
   * Aggiornamento di W_DOCDIG.
   *
   * @param indice
   * @param idprg
   * @param iddocdig
   * @throws GestoreException
   */
  public void aggiornaW_DOCDIG(HttpServletRequest request, UploadFileForm uploadFileForm, int indice, String idprg, Long iddocdig) throws GestoreException {
    String fname = null;

    try {
      HashMap<?, ?> hm = ((UploadMultiploForm) uploadFileForm).getFormFiles();

      DataColumnContainer dccW_DOCDIG = new DataColumnContainer(new DataColumn[] { new DataColumn("W_DOCDIG.IDPRG", new JdbcParametro(
          JdbcParametro.TIPO_TESTO, idprg)) });
      dccW_DOCDIG.addColumn("W_DOCDIG.IDDOCDIG", JdbcParametro.TIPO_NUMERICO, iddocdig);
      dccW_DOCDIG.getColumn("W_DOCDIG.IDPRG").setChiave(true);
      dccW_DOCDIG.getColumn("W_DOCDIG.IDDOCDIG").setChiave(true);
      dccW_DOCDIG.setValue("W_DOCDIG.IDPRG", idprg);
      dccW_DOCDIG.setValue("W_DOCDIG.IDDOCDIG", iddocdig);
      dccW_DOCDIG.setOriginalValue("W_DOCDIG.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, idprg));
      dccW_DOCDIG.setOriginalValue("W_DOCDIG.IDDOCDIG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, iddocdig));

      // Gestione upload del file
      FormFile ff = (FormFile) hm.get(new Long(indice));
      if (ff != null) {
        fname = ff.getFileName();
        if (fname.length() != 0 && ff.getFileSize() > 0) {
          dccW_DOCDIG.addColumn("W_DOCDIG.DIGNOMDOC", JdbcParametro.TIPO_TESTO, fname);
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          baos.write(ff.getFileData());
          dccW_DOCDIG.addColumn("W_DOCDIG.DIGOGG", JdbcParametro.TIPO_BINARIO, baos);
        } else if (fname.length() != 0 && ff.getFileSize() == 0) {
          UtilityStruts.addMessage(request, "warning", "warnings.gare.documentazioneGare.uploadMultiplo.fileVuoto",
              new String[] { fname });
        }
      }
      dccW_DOCDIG.update("W_DOCDIG", this.sqlManager);

    } catch (FileNotFoundException e) {
      throw new GestoreException("File da caricare non trovato", "uploadMultiplo", new String[] { fname }, e);
    } catch (IOException e) {
      throw new GestoreException("Si è verificato un errore durante la scrittura del buffer per il salvataggio del file "
          + fname
          + " su database", "uploadMultiplo", new String[] { fname }, e);
    } catch (SQLException e) {
      throw new GestoreException("Si è verificato un errore durante l'aggiornamento della tabella W_DOCDIG", null, e);
    }
  }

  /**
   * Metodo che calcola il valore dell'importo offerto a partire dal ribasso
   *
   * @param codiceGara
   * @param ribasso
   * @return Double
   * @throws SQLException
   */
  public Double calcoloImpoffDaRibasso(String codiceGara, Double ribasso) throws  SQLException {
    Double impoff=new Double(0);

    double denominatore;
    Double onprge = null;
    Double impapp = null;
    Double impsic = null;
    Double impnrl = null;
    String sicinc = "";
    double numeratore;
    String onsogrib = "";

    HashMap<String, Object> datiGara = this.pgManager.getDatiGaraRibassoImporto(codiceGara);

    if (datiGara != null && datiGara.size() > 0) {
      onprge = (Double) datiGara.get("onprge");
      impapp = (Double) datiGara.get("impapp");
      impsic = (Double) datiGara.get("impsic");
      impnrl = (Double) datiGara.get("impnrl");
      sicinc = (String) datiGara.get("sicinc");
      onsogrib = (String) datiGara.get("onsogrib");

      numeratore = impapp.doubleValue()
          - impsic.doubleValue()
          - impnrl.doubleValue();
      if (!"1".equals(onsogrib)) numeratore -= onprge.doubleValue();


      denominatore = impnrl.doubleValue();
      if ("1".equals(sicinc)) denominatore += impsic.doubleValue();

      if (!"1".equals(onsogrib)) denominatore += onprge.doubleValue();

      double ribassoVal=0;
      if(ribasso!=null)
        ribassoVal = ribasso.doubleValue();
      double importo = (numeratore * (1 + ribassoVal/100)) + denominatore;
      impoff = new Double(importo);
    }

    return impoff;
  }

  /**
   * Metodo per calcolare il ribasso a partire dall'importo offerto
   *
   * @param codiceGara
   * @param impoff
   * @param cifreRibasso
   *@throws SQLException
   * @return Double
   */
  public Double calcoloRibassoDaImpoff( String codiceGara, Double impoff, String cifreRibasso) throws SQLException {

    Double ribasso = new Double(0);
    double denominatore;
    Double onprge = null;
    Double impapp = null;
    Double impsic = null;
    Double impnrl = null;
    String sicinc = "";
    double numeratore=0;
    String onsogrib = "";

    HashMap<String, Object> datiGara = this.pgManager.getDatiGaraRibassoImporto(codiceGara);

    if (datiGara != null && datiGara.size() > 0) {

      onprge = (Double) datiGara.get("onprge");
      impapp = (Double) datiGara.get("impapp");
      impsic = (Double) datiGara.get("impsic");
      impnrl = (Double) datiGara.get("impnrl");
      sicinc = (String) datiGara.get("sicinc");
      onsogrib = (String) datiGara.get("onsogrib");

      denominatore = impapp.doubleValue()
          - impsic.doubleValue()
          - impnrl.doubleValue();
      if (!"1".equals(onsogrib)) denominatore -= onprge.doubleValue();

      numeratore = -impapp.doubleValue();
      if (sicinc == null || "".equals(sicinc) || "2".equals(sicinc))
        numeratore += impsic.doubleValue();

      if (denominatore != 0){
        double impoffVal=0;
        if(impoff!=null)
          impoffVal = impoff.doubleValue();
        double rib = (impoffVal + numeratore) * 100 / denominatore ;
        ribasso = new Double(rib);
        ribasso = (Double)UtilityNumeri.arrotondaNumero(ribasso, new Integer(cifreRibasso));
      }
    }
    return ribasso;
  }

  /**
   * Viene impostato il numero di decimali da adoperare per il ribasso.
   * Se è valorizzato prerib si considera tale valore, altrimenti si considera
   * il tabellato A1028
   * @param codiceGara
   * @return string
   * @throws SQLException
   */
  public String getNumeroDecimaliRibasso(String codiceGara) throws SQLException{
    String numDecimali = this.tabellatiManager.getDescrTabellato("A1028", "1");
    Long prerib = (Long)this.sqlManager.getObject("select prerib from torn where codgar=?", new Object[]{codiceGara});
    if(prerib!=null)
      numDecimali= prerib.toString();
    return numDecimali;
  }


  /**
   * Viene eseguito il calcolo del numero d'ordine delle ditte di una gara secondo una delle seguenti modalità:
   *   1 -> Ragione sociale
   *   2 -> Data e ora presentazione domanda di partecipazione
   *   3 -> Numero protocollo presentazione domanda di partecipazione
   *   4 -> Data e ora presentazione offerta
   *   5 -> Numero protocollo presentazione offerta
   *
   * @param ngara
   * @param codgar
   * @param isGaraLottiConOffertaUnica
   * @param isProceduraAggiudicazioneAperta
   * @param isDitteConcorrenti
   * @param paginaAttiva
   * @param modalitaRiassegnamento
   * @throws SQLException
   */
  public void setNumeroOrdine(String  ngara, String codgar, String isGaraLottiConOffertaUnica, String isProceduraAggiudicazioneAperta,
      String isDitteConcorrenti, String paginaAttiva, int modalitaRiassegnamento) throws SQLException{

    String select = "";
    String ordinamento="";
    String db = ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);

    String isGaraLottoUnico ="false";
    if (codgar.startsWith("$"))
      isGaraLottoUnico = "true";

    String campoChiave="ngara5";
    if("true".equals(isGaraLottiConOffertaUnica))
      campoChiave="codgar5";


    switch(modalitaRiassegnamento){
      case 1:
        select="select dittao,invoff from ditg,impr where " + campoChiave + " = ? and codimp = dittao ";
        ordinamento=" order by IMPR.NOMEST, DITG.NPROGG";
        break;
      case 2:
        select="select dittao,invoff,coalesce(oradom,'24:00') as oradom_1 from ditg where " + campoChiave + " = ? ";
        ordinamento=" order by DRICIND,oradom_1, NPROGG";
        break;
      case 3:
        if("ORA".equals(db)){
          select="select dittao,invoff,to_number(nvl2(LENGTH (TRIM (TRANSLATE (nprdom, ' +-.0123456789', ' ') ) ) ,null,nprdom)) as nprdom_numerico from ditg where " + campoChiave + " = ? ";
          ordinamento = " order by nprdom_numerico, NPROGG";
        }else if("MSQ".equals(db)){
          select="select dittao,invoff, CONVERT( numeric ,CASE when isnumeric(nprdom)=1 then nprdom else null end) as nprdom_numerico from ditg where " + campoChiave + " = ? ";
          ordinamento=" order by nprdom_numerico, NPROGG";
        }else if("POS".equals(db)){
          select="select dittao,invoff, CAST(nullif(nprdom, '') AS integer) as nprdom_numerico from ditg where " + campoChiave + " = ? ";
          ordinamento=" order by nprdom_numerico, NPROGG";
        }
        break;
      case 4:
        select="select dittao,invoff,coalesce(oraoff,'24:00') as oraoff_1 from ditg where " + campoChiave + " = ? ";
        ordinamento="order by DATOFF,oraoff_1, NUMORDPL";
        break;
      case 5:
        if("ORA".equals(db)){
          select="select dittao,invoff,to_number(nvl2(LENGTH (TRIM (TRANSLATE (NPROFF, ' +-.0123456789', ' ') ) ) ,null,NPROFF)) as nproff_numerico from ditg where " + campoChiave + " = ? ";
          ordinamento = " order by nproff_numerico, NUMORDPL";
        }
        else if("MSQ".equals(db)){
          select="select dittao,invoff, CONVERT( numeric ,CASE when isnumeric(NPROFF)=1 then nproff else null end) as nproff_numerico from ditg where " + campoChiave + " = ? ";
          ordinamento=" order by nproff_numerico, NPROGG";
        }else if("POS".equals(db)){
          select="select dittao,invoff, CAST(nullif(NPROFF, '') AS integer) as nproff_numerico from ditg where " + campoChiave + " = ? ";
          ordinamento=" order by nproff_numerico, NPROGG";
        }
        break;

    }

    if("true".equals(isGaraLottiConOffertaUnica))
      select+= " and ngara5=codgar5 ";

    if((paginaAttiva!=null && !"".equals(paginaAttiva) && Integer.parseInt(paginaAttiva)==GestioneFasiRicezioneFunction.FASE_RICEZIONE_PLICHI && (!"true".equals(isProceduraAggiudicazioneAperta))))
      select+= " and (invoff is null or not invoff = '2') ";

    select+= ordinamento;

    List<?> listaCodiciDitte = this.sqlManager.getListVector(select, new Object[]{ngara});
    if(listaCodiciDitte!= null && listaCodiciDitte.size()>0){
      String update="";
      for(int i=0;i<listaCodiciDitte.size();i++){
        String codiceDitta = SqlManager.getValueFromVectorParam(listaCodiciDitte.get(i), 0).getStringValue();
        String invoff = SqlManager.getValueFromVectorParam(listaCodiciDitte.get(i), 1).getStringValue();
        //update="update ditg set numordpl = " + Integer.toString(i + 1);
        boolean daAggiornare = false;
        update="update ditg set ";
        if(invoff==null || !"2".equals(invoff)){
          update+= "numordpl =" + Integer.toString(i + 1);
          daAggiornare=true;
        }

        if("true".equals(isDitteConcorrenti) || (paginaAttiva!=null && !"".equals(paginaAttiva) && (Integer.parseInt(paginaAttiva)==GestioneFasiRicezioneFunction.FASE_RICEZIONE_DOMANDE_DI_PARTECIPAZIONE ||
            Integer.parseInt(paginaAttiva)==GestioneFasiRicezioneFunction.FASE_ELENCO_DITTE_INVITATE) || "true".equals(isProceduraAggiudicazioneAperta))){
          //update+= ", nprogg = " + Integer.toString(i + 1);
          if(daAggiornare)
            update+=",";
          update+= " nprogg = " + Integer.toString(i + 1);
          daAggiornare=true;

          if("true".equals(isGaraLottiConOffertaUnica) || "true".equals(isGaraLottoUnico)){
            this.sqlManager.update("update edit set nprogt = ? where codgar4 = ? and codime = ? ", new Object[]{new Long(i+1),codgar,codiceDitta});
          }
        }

        if(daAggiornare){
          update += " where " + campoChiave + "=? and dittao = ?";
          this.sqlManager.update(update, new Object[]{ngara,codiceDitta});
        }

      }
    }
  }

  /**
   *
   * @param profilo :
     *          1 - Gare
     *          2 - Elenchi
     *          3 - Cataloghi
     *          4 - Ricerche di mercato
     *          5 - Avvisi
     *          6 - Protocollo
     *          7 - Affidamenti
   * @return
   */
  public String getFiltroComunicazioniSoccorsoIstruttorio(String profilo){
    String filtro="";
    String property = "comunicazioniInGare.soccorsoIstruttorio.filtroTermine";
    if ("2".equals(profilo) || "3".equals(profilo) )
      property = "comunicazioniInElenco.soccorsoIstruttorio.filtroTermine";
    String valoreProperty = ConfigManager.getValore(property);
    if("1".equals(valoreProperty)){
      String tipoDB = SqlManager.getTipoDB();
      if("ORA".equals(tipoDB))
        filtro = " and not exists(select w1.idcom from w_invcom w1 where w1.commodello=1 and w1.idprg=w_invcom.IDPRGRIS and w1.idcom=w_invcom.IDCOMRIS and to_date(to_char(w1.COMDATSCA,'dd/mm/yyyy') || ' ' || w1.COMORASCA, 'dd/mm/yyyy HH24:mi') > sysdate)";
      else if("MSQ".equals(tipoDB))
        filtro =" and not exists( select w1.idcom from w_invcom w1 where w1.commodello=1 and w1.idprg=w_invcom.IDPRGRIS and w1.idcom=w_invcom.IDCOMRIS and CONVERT(DATETIME,CONVERT(varchar,w1.COMDATSCA,103) + ' ' + w1.COMORASCA) > CURRENT_TIMESTAMP)";
      else if("POS".equals(tipoDB))
        filtro =" and not exists(select w1.idcom from w_invcom w1 where w1.commodello=1 and w1.idprg=w_invcom.IDPRGRIS and w1.idcom=w_invcom.IDCOMRIS and TO_TIMESTAMP(to_char(w1.COMDATSCA,'DD/MM/YYYY') || ' ' || w1.COMORASCA, 'DD/MM/YYYY HH24:MI:SS') > current_timestamp)";

    }
    return filtro;
  }

  /**
   * Viene calcolato il valore del campo BUSTA di IMPRDOCG
   * @param stepWizard
   * @return Long
   */
  public Long getValoreBusta(String stepWizard) {
    Long ret = null;
    List<?> listaTabellatoA1014 = tabellatiManager.getTabellato("A1014");
    for (int i = 0; i < listaTabellatoA1014.size(); i++) {
      Tabellato rigaTabellato = (Tabellato) listaTabellatoA1014.get(i);
      String tipo_i = rigaTabellato.getTipoTabellato();
      String desc_i = rigaTabellato.getDescTabellato();
      if (desc_i.indexOf(stepWizard) >= 0) {
        ret = new Long(tipo_i);
      }
    }
    return ret;
  }

  /**
   * Viene controllato se esistono dite in gara con lo stato e lo step specificati. Inoltre si può decidere se attivare il controllo per il
   * sorteggio inviti.
   * Viene restituito il risultato del controllo in "esistonoDitteAmmissione".
   * Se attivo il controllo sul sorteggio inviti viene resituito anche "numope"
   * @param ngara
   * @param step
   * @param stato
   * @param controlloSortin
   * @param controlloLotti
   * @return HashMap<String, Object>
   * @throws Exception
   */
  public HashMap<String, Object> esistonoDitteConAmmissionePariA(String ngara, String step, Long stato, boolean controlloSortin, boolean controlloLotti) throws Exception {
    HashMap<String, Object> ret = new HashMap<String, Object>();
    boolean controlloStatoAmmissione = true;
    String sortinv = null;
   Long numope = null;

    ret.put("esistonoDitteAmmissione", new Boolean(false));
    if (ngara != null && step != null && !"".equals(step)) {
      String select = "select count(vd.dittao) from v_ditgammis vd, ditg d where ";
      if(controlloLotti)
        select += "vd.codgar=? ";
      else
        select += "vd.ngara=? ";
      select += " and vd.fasgar=? and vd.ammgar";
      if (stato == null) {
        select += " is null";
      } else {
        select += " = " + stato.toString();
      }
      select += " and vd.ngara=d.ngara5  and vd.dittao =d.dittao and (d.ammgar <> '2' or d.ammgar is null) and (d.fasgar < ? or d.fasgar is null)";
      Long stepLong = new Long(step);
      Long fase = null;
      if (stepLong.longValue() == GestioneFasiGaraFunction.FASE_CHIUSURA_VERIFICA_DOCUMENTAZIONE_AMMINISTRATIVA)
        fase = new Long(2);
      else if (stepLong.longValue() == GestioneFasiGaraFunction.FASE_CONCLUSIONE_COMPROVA_REQUISITI)
        fase = new Long(4);
      else if (stepLong.longValue() == GestioneFasiGaraFunction.FASE_VALUTAZIONE_TECNICA)
        fase = new Long(5);
      else if (stepLong.longValue() == GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE)
        fase = new Long(6);
      else if (stepLong.longValue() == GestioneFasiRicezioneFunction.FASE_APERTURA_DOMANDE_DI_PARTECIPAZIONE)
        fase = new Long(-4);
      if (controlloSortin) {
        List<?> datiTorn = sqlManager.getVector("select sortinv,numope from gare,torn where codgar1= codgar and ngara = ?", new Object[] {ngara});
        if (datiTorn != null && datiTorn.size() > 0) {
          sortinv = (String) SqlManager.getValueFromVectorParam(datiTorn, 0).getValue();
          numope = (Long) SqlManager.getValueFromVectorParam(datiTorn, 1).getValue();
          if (Long.valueOf(-4).equals(fase) && "1".equals(sortinv)) {
            ret.put("numope", numope);
            Long conteggioAmmesse = Long.valueOf(0);
            List<?> listaDitteOrdinataNOI = sqlManager.getListVector("select vd.ammgar from v_ditgammis vd, ditg d"
                    + " where vd.ngara=? and vd.fasgar=? and vd.ngara=d.ngara5 and vd.dittao =d.dittao"
                    + " and (d.ammgar <> '2' or d.ammgar is null) order by numordinv",
                    new Object[] {ngara, fase});
            if (numope != null) {
              int intNumOpe = numope.intValue();
              for (int j = 0; j < listaDitteOrdinataNOI.size(); j++){
                Long statoAmmissione = SqlManager.getValueFromVectorParam(listaDitteOrdinataNOI.get(j), 0).longValue();
                if (intNumOpe >= (j + 1) && statoAmmissione == null) {
                  ret.put("esistonoDitteAmmissione", new Boolean(true));
                  return ret;
                }
                if (Long.valueOf(1).equals(statoAmmissione) || Long.valueOf(3).equals(statoAmmissione) || Long.valueOf(4).equals(statoAmmissione)
                    || Long.valueOf(5).equals(statoAmmissione) || Long.valueOf(8).equals(statoAmmissione) || Long.valueOf(10).equals(statoAmmissione)) {
                  conteggioAmmesse++;
                }
              }

              if (conteggioAmmesse != null && conteggioAmmesse.longValue() > 0) {
                if (conteggioAmmesse < numope) {
                  controlloStatoAmmissione = true;
                } else {
                  controlloStatoAmmissione = false;
                }
              }
            }
          }//sortinv eq 1
        }
      }

      if (controlloStatoAmmissione) {
        Long conteggio = (Long) sqlManager.getObject(select, new Object[] {ngara, fase, fase});
        if (conteggio != null && conteggio.longValue() > 0) {
          ret.put("esistonoDitteAmmissione", new Boolean(true));
        }
      }

    }

    return ret;
  }

  /**
   *
   * @param codiceGara
   * @param gara
   * @param ditta
   * @param genere
   * @param whereBusteAttiveWizard
   * @param whereBusteAttiveWizardImprdocg
   * @param commsgtes
   * @param sostituzione
   * @return String
   * @throws Exception
   */
  public String sostituzioneMnemonicoDocumentiMancanti(String codiceGara, String gara, String ditta, Long genere, String whereBusteAttiveWizard,  String whereBusteAttiveWizardImprdocg,
      String commsgtes, boolean sostituzione) throws Exception {
    String ret = "";

    String select="select descrizione,proveni, situazdoci, notedoci,norddoci from imprdocg where codgar=? and (ngara is null or ngara=? ) and codimp=? and (situazdoci =1 or situazdoci=5) order by norddoci";
    if(genere.longValue()==4){
      select="select descrizione,proveni, situazdoci, notedoci,norddoci from imprdocg where codgar=? and (ngara is null or ngara=? ) and codimp=? and (situazdoci =1 or situazdoci=5) ";
      select += "and (exists(select CODGAR from DOCUMGARA where DOCUMGARA.CODGAR=IMPRDOCG.CODGAR and DOCUMGARA.NORDDOCG=IMPRDOCG.NORDDOCI and IMPRDOCG.PROVENI=1 and ("+whereBusteAttiveWizard+")) or (proveni=2 and (" + whereBusteAttiveWizardImprdocg + "))) order by norddoci";
    }
    List<?> listaDati = sqlManager.getListVector(select, new Object[]{codiceGara, gara, ditta});
    if(listaDati!= null && listaDati.size()>0){
      StringBuffer testoMail= new StringBuffer("");
      for(int i=0;i<listaDati.size();i++){
        String descrizione = SqlManager.getValueFromVectorParam(listaDati.get(i), 0).getStringValue();
        Long proveni = SqlManager.getValueFromVectorParam(listaDati.get(i), 1).longValue();
        Long situazdoci = SqlManager.getValueFromVectorParam(listaDati.get(i), 2).longValue();
        String notedoci = SqlManager.getValueFromVectorParam(listaDati.get(i), 3).getStringValue();
        String situazdociDesc = tabellatiManager.getDescrTabellato("A1062", situazdoci.toString());
        Long norddoci = SqlManager.getValueFromVectorParam(listaDati.get(i), 4).longValue();
        if(proveni!=null && proveni.longValue()==1){
          descrizione = (String)sqlManager.getObject("select descrizione from documgara where codgar=? and ngara=? and norddocg=?", new Object[]{codiceGara, gara,norddoci});
        }
        if(descrizione!=null && !"".equals(descrizione))
          testoMail.append(descrizione);
        if(situazdociDesc!=null && !"".equals(situazdociDesc)){
          if(descrizione!=null && !"".equals(descrizione))
            testoMail.append(" - ") ;
          testoMail.append(situazdociDesc) ;
        }
        if(notedoci!=null && !"".equals(notedoci))
          testoMail.append(" - " +notedoci) ;

        if(i!=listaDati.size()-1)
          testoMail.append(";\r\n") ;
        else
          testoMail.append("\r\n") ;
      }
      if(testoMail.length() > 1)
        if(sostituzione)
          ret = commsgtes.replaceAll(REPLACEMENT_DOCMANCANTI, testoMail.toString());
        else
          ret = testoMail.toString();
    } else {
      if(sostituzione)
        ret = commsgtes.replaceAll(REPLACEMENT_DOCMANCANTI, "");
    }
    return ret;
  }

  /**
   * Il metodo controlla se è attiva la gestione dei formulari
   * Il parametro lottoGara, va settato a true solo nel caso di busta tecnica ed economica per le gare a lotti
   * @param ngara
   * @param codgar
   * @param genere
   * @param busta
   * @param iterga
   * @param lottoGara
   * @return
   * @throws GestoreException
   * @throws SQLException
   */
  public String gestioneQuestionari(String ngara,String codgar,Long genere, int busta,Long iterga, boolean lottoGara) throws SQLException {
    String esito=QFORM_NON_ABILITATO;
    //Controllo esistenza documenti
    Long bustaLong = new Long(busta);
    String chiaveQform = ngara;
    if((ngara==null || "".equals(ngara) || new Long(3).equals(genere)) && !lottoGara)
      chiaveQform = codgar;
    String selectDocumgara="select count(*) from documgara where codgar=? and gruppo=3 and busta=?";
    String condizioneDGUE ="";
    if(busta==1 || busta==4)
      condizioneDGUE += " and (IDSTAMPA <> 'DGUE' or IDSTAMPA is null)";
    Long conteggioDocumenti = (Long)this.sqlManager.getObject(selectDocumgara + condizioneDGUE , new Object[] {codgar, bustaLong});
    Long conteggioQuestionari = (Long)this.sqlManager.getObject("select count(*) from qform where entita='GARE' and key1=? and busta=? and (stato = 1 or stato = 5)", new Object[] {chiaveQform, bustaLong});
    if((conteggioDocumenti==null || new Long(0).equals(conteggioDocumenti)) && (conteggioQuestionari==null || new Long(0).equals(conteggioQuestionari))) {
      boolean garaPubblicata= false;
      String select="select count(CODGAR9) from PUBBLI where CODGAR9 = ? and TIPPUB=?";
      Object par[] = new Object[2];
      par[0]=codgar;
      //Si deve controllare se la gara è pubblicata
      if(busta==4 || ((busta==1 || busta==2 || busta==3) && new Long(1).equals(iterga))) {
        par[1]= new Long(11);
      }else {
        par[1]= new Long(13);
      }
      Long numeroPubblicazioni = (Long) sqlManager.getObject(select, par);
      if (numeroPubblicazioni != null && numeroPubblicazioni.longValue()>0)
        garaPubblicata = true;
      if(!garaPubblicata) {
        esito=QFORM_INSERIMENTO;
      }
    }else if(conteggioQuestionari!=null && conteggioQuestionari.longValue()>0)
      esito=QFORM_VISUALIZZAZIONE;

    return esito;
  }

  /**
   * Il metodo controlla se è attiva la gestione dei formulari per l'elenco
   * Il parametro lottoGara, va settato a true solo nel caso di busta tecnica ed economica per le gare a lotti
   * @param ngara
   * @param codgar
   * @return String
   * @throws GestoreException
   * @throws SQLException
   */
  public String gestioneQuestionariElenco(String ngara,String codgar) throws SQLException {
    String esito=QFORM_NON_ABILITATO;
    //Controllo esistenza documenti
    Long conteggioDocumenti = (Long)this.sqlManager.getObject("select count(*) from documgara where codgar=? and gruppo=3 and (fasele=1 or fasele=2)", new Object[] {codgar});
    Long conteggioQuestionari = (Long)this.sqlManager.getObject("select count(*) from qform where entita='GARE' and key1=? and (stato = 1 or stato = 5)", new Object[] {ngara});
    if((conteggioDocumenti==null || new Long(0).equals(conteggioDocumenti)) && (conteggioQuestionari==null || new Long(0).equals(conteggioQuestionari))) {
      boolean garaPubblicata= false;
      String select="select count(CODGAR9) from PUBBLI where CODGAR9 = ? and TIPPUB=?";
      Long numeroPubblicazioni = (Long) sqlManager.getObject(select, new Object[] {codgar, new Long(11)});
      if (numeroPubblicazioni != null && numeroPubblicazioni.longValue()>0)
        garaPubblicata = true;
      if(!garaPubblicata)
        esito=QFORM_INSERIMENTO;
    }else if(conteggioQuestionari!=null && conteggioQuestionari.longValue()>0)
      esito=QFORM_VISUALIZZAZIONE;

    return esito;
  }

  /**
   * Viene verificato se esiste la property per la marca temporale.
   * Se esiste ed è true controlloEsistenzaFile, viene controllata l'esitenza del file marcato temporalmente
   * @param idprg
   * @param idcom
   * @param controlloEsistenzaFile
   * @return boolean[]
   * @throws SQLException
   */
  public boolean[] applicareMarcaTemporalmente(String idprg, Long idcom, boolean controlloEsistenzaFile) throws SQLException {
    boolean esisteFileMarcato = false;
    Object par[] = new Object[] {idprg, idcom.toString()};
    String urlAccesso = ConfigManager.getValore("marcaturaTemp.url");
    boolean applicareMarcaTemp = false;
    if(urlAccesso!=null && !"".equals(urlAccesso))
      applicareMarcaTemp=true;
    if(applicareMarcaTemp && controlloEsistenzaFile) {
      String sqlFileMarcaTemporale = "select count(idprg) from W_DOCDIG where DIGKEY1 = ? and DIGKEY2=? and DIGENT ='W_INVCOM' and DIGNOMDOC = "
          + AllegatoSintesiUtils.creazioneFiltroNomeFileSintesi(true,this.sqlManager);
      Long conteggio=(Long)this.sqlManager.getObject(sqlFileMarcaTemporale, par);
      if(conteggio.longValue()!=0)
        esisteFileMarcato = true;
    }
    boolean ret[] = {esisteFileMarcato,applicareMarcaTemp};
    return ret;
  }

  public boolean esistonoDitteDGUE(String codgar, Long gruppo) throws GestoreException{
    boolean esistonoDitte=false;

    Long numDitte=null;
    try {
      numDitte = (Long)this.sqlManager.getObject("select count(*) from documgara where codgar=? and gruppo =? and idstampa=?", new Object[] {codgar,gruppo, "DGUE"});
    } catch (SQLException e) {
      throw new GestoreException("Si è verificato un errore durante la lettura di DOCUMGARA", null, e);
    }
    if(numDitte!= null && numDitte.longValue()>0)
      esistonoDitte=true;
    return esistonoDitte;
  }

  public boolean isGaraSospesa(String codgar) throws GestoreException{
	    boolean isGaraSospesa = false;

	    Long numSosp=null;
	    try {
	    	numSosp = (Long)this.sqlManager.getObject("select count(*) from garsospe where codgar=? and datfine is null", new Object[] {codgar});
	    } catch (SQLException e) {
	      throw new GestoreException("Si è verificato un errore durante la lettura di GARSOSPE", null, e);
	    }
	    if(numSosp!= null && numSosp.longValue()>0)
	    	isGaraSospesa=true;
	    return isGaraSospesa;
	  }

  public String gestioneDocDGUEConcorrenti(String codgar, String ngara, Long gruppoDGUE, Long busta, boolean soloInserimento) throws SQLException, GestoreException {
    String select="select dignomdoc, documgara.idprg, iddocdg, descrizione from documgara, w_docdig where codgar= ? and GRUPPO = ? and documgara.idprg=w_docdig.idprg and documgara.iddocdg=w_docdig.iddocdig";
    select +=" and idstampa = 'DGUE' and (isarchi='2' or isarchi is null)";
    Long gruppoDocConcorrenti = new Long(3);

    String ret=null;

    Vector<?> datiDocumentoDGUE = this.sqlManager.getVector(select, new Object[]{codgar,gruppoDGUE});
    if(datiDocumentoDGUE!=null && datiDocumentoDGUE.size()>0) {
      String nomeAllegato = SqlManager.getValueFromVectorParam(datiDocumentoDGUE, 0).getStringValue();
      if(nomeAllegato!=null && !"".equals(nomeAllegato) && nomeAllegato.toUpperCase().lastIndexOf("XML")>0) {
        String desrizione = SqlManager.getValueFromVectorParam(datiDocumentoDGUE, 3).getStringValue();
        String idprg=SqlManager.getValueFromVectorParam(datiDocumentoDGUE, 1).getStringValue();
        Long iddocdg=SqlManager.getValueFromVectorParam(datiDocumentoDGUE, 2).longValue();
        Long norddocg = (Long)this.sqlManager.getObject("select norddocg from documgara where codgar= ? and GRUPPO = ? and busta =? and idprg=? and iddocdg=? "
            + "and idstampa = 'DGUE' and (isarchi='2' or isarchi is null)",
            new Object[] {codgar, gruppoDocConcorrenti, busta, idprg, iddocdg});
        if(norddocg==null) {
          Long conteggioQuestionari = null;
          if(!soloInserimento) {
            //Si deve controllare se vi sono qform associati al gruppo 3 e busta di destinazione, se vi sono, non va fatto l'inserimento
            String chiaveQform = codgar;
            if(ngara!=null)
              chiaveQform = ngara;
            conteggioQuestionari = (Long)this.sqlManager.getObject("select count(*) from qform where entita='GARE' and key1=? and busta=? and (stato = 1 or stato = 5)", new Object[] {chiaveQform, busta});
          }
          if(conteggioQuestionari==null || new Long(0).equals(conteggioQuestionari)) {
            String insert="insert into documgara(codgar, ngara, norddocg,gruppo, descrizione, idstampa, valenza, idprg, iddocdg, busta, obbligatorio, modfirma, numord) "
                + "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            Object params[]= new Object[13];
            params[0]=  codgar;
            params[1]=  ngara;

            norddocg = (Long)this.sqlManager.getObject("select max(norddocg) from documgara where codgar=?", new Object[] {codgar});
            if(norddocg == null)
              norddocg = new Long(0);
            norddocg = new Long(norddocg.longValue() + 1);
            params[2]=  norddocg;
            params[3] = gruppoDocConcorrenti;
            params[4] = desrizione;
            params[5] = "DGUE";
            params[6] = new Long(0);
            params[7] = idprg;
            params[8] = iddocdg;
            params[9] = busta;
            params[10] = new Long(1);
            params[11] = new Long(1);

            Long numord = (Long)this.sqlManager.getObject("select max(numord) from documgara where codgar=? and gruppo = ? and busta = ?", new Object[] {codgar, gruppoDocConcorrenti, busta});
            if(numord == null)
              numord = new Long(0);
            numord = new Long(numord.longValue() + 1);
            params[12] = numord;
            this.sqlManager.update(insert, params);
            ret="INS";
          }
        }else if(!soloInserimento){
          this.sqlManager.update("update documgara set descrizione=? where codgar=? and norddocg=?", new Object[] {desrizione, codgar, norddocg});
          ret="AGG";
        }
      }
    }else  if(!soloInserimento){
      //Se non c'è occorrenza con documgara DGUE con file allegato xml, si deve controllare se vi è l'occorrenza dei documenti dei concorrenti DGUE
      //se c'è, va cancellata
      Long norddocg = (Long)this.sqlManager.getObject("select norddocg from documgara where codgar= ? and GRUPPO = ? and busta =? and idstampa = 'DGUE' and (isarchi='2' or isarchi is null)",
          new Object[] {codgar, gruppoDocConcorrenti, busta});
      if(norddocg!=null) {
        this.sqlManager.update("delete from documgara where NORDDOCG = ? and  codgar=?", new Object[] {norddocg, codgar});
        ret="CANC";
      }
    }
    return ret;
  }

  /**
   * Vengono letti i codici cig dei lotti, scartando i cig fittizzi, e viene
   * composta una stringa del tipo: 'cig1','cig2',...
   *
   * @param codiceGara
   * @param ditta
   * @return String
   * @throws SQLException
   */
  public String getElencoCigLotti(String codiceGara, String ditta) throws SQLException {
    String elencoCodcigLotti="";
    String select="select codcig from gare where codgar1=? and ditta=? and genere is null";
    List<?> listaCigLotti = sqlManager.getListVector(select, new Object[]{codiceGara,ditta});
    if(listaCigLotti!=null && listaCigLotti.size()>0){
      String codcig=null;
      for(int i=0;i<listaCigLotti.size();i++){
        codcig = SqlManager.getValueFromVectorParam(listaCigLotti.get(i), 0).getStringValue() ;
        if(codcig != null && !"".equals(codcig) && !codcig.startsWith("#")){
          if(elencoCodcigLotti.length() > 1)
            elencoCodcigLotti+=",";
          elencoCodcigLotti+="'" + codcig +"'";
        }
      }

    }
    if("".equals(elencoCodcigLotti))
      elencoCodcigLotti=null;

    return elencoCodcigLotti;
  }

  public static String getTabellatoClassifica(Long tipcat){
    String codiceTabellato = "A1015";
    if (tipcat.longValue() == 2)
      codiceTabellato = "G_035";
    else if (tipcat.longValue() == 3)
      codiceTabellato = "G_036";
    else if (tipcat.longValue() == 4)
      codiceTabellato = "G_037";
    else if (tipcat.longValue() == 5)
      codiceTabellato = "G_049";
    return codiceTabellato;
  }

  public Double updImportoTotaleTorn(String codiceGara) throws GestoreException{
    if (logger.isDebugEnabled()) logger.debug("updImportoTotaleTorn: inizio metodo");
    String select = "select sum(coalesce(impapp,0)) from gare where codgar1=? and ngara <> ?";
    try {
      Double importoTotale = new Double(0);
      Object importoTemp = sqlManager.getObject(select, new Object[] {codiceGara, codiceGara });
      if (importoTemp != null) {
        if (importoTemp instanceof Long) {
          importoTotale = new Double(((Long) importoTemp));
        } else if (importoTemp instanceof Double) {
          importoTotale = new Double((Double) importoTemp);
        }
      }
      importoTotale = new Double(importoTotale.doubleValue());
      if (logger.isDebugEnabled()) logger.debug("updImportoTotaleTorn: fine metodo");
      return importoTotale;
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore durante la lettura degli importi dei lotti di gara", null, e);
    }
  }

}
