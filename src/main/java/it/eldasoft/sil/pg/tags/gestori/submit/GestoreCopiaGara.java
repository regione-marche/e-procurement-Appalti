/*
 * Created on 10-ott-2008
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
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore della copia di una gara a lotti e a lotto unico
 *
 * @author Luca.Giacomazzo
 */
public class GestoreCopiaGara extends AbstractGestoreEntita {

  TabellatiManager tabellatiManager = null;

  /** Logger */
  static Logger            logger           = Logger.getLogger(GestoreCopiaGara.class);

  public GestoreCopiaGara() {
    super(false);
  }

  @Override
  public String getEntita() {
    return "TORN";
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    PgManager pgManager = (PgManager) UtilitySpring.getBean(
        "pgManager", this.getServletContext(), PgManager.class);

    tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
        "tabellatiManager", this.getServletContext(), TabellatiManager.class);

    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean(
        "pgManagerEst1", this.getServletContext(), PgManagerEst1.class);

    boolean isCodificaAutomatica = geneManager.isCodificaAutomatica("TORN", "CODGAR");
    //Nel caso di gare per rilancio per verificare se è attiva la codifica automatica
    //cambia il criterio per la verifica se sia attiva la codifica automatica
    if(this.getGeneManager().getProfili().checkProtec(
        (String) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "COLS", "VIS", "GARE.GARE.PRECED"))
      isCodificaAutomatica = geneManager.isCodificaAutomatica("GARE", "PRECED");

    String tipoCopia = datiForm.getString("TIPO_COPIA");
    String sorgente = datiForm.getString("SORGENTE");
    String destinazione = datiForm.getString("DESTINAZIONE");
    if(destinazione!=null && !"".equals(destinazione))
      destinazione = destinazione.trim();
    boolean copiaScadenzario = datiForm.getLong("COPIA_SCADENZARIO").intValue() == 1 ? true : false;
    boolean copiaDocumentazione = datiForm.getLong("COPIA_DOCUMENTAZIONE").intValue() == 1 ? true : false;
    boolean copiaTermini = datiForm.getLong("COPIA_TERMINI").intValue() == 1 ? true : false;

  //variabili per tracciatura eventi, solo per la creazione di una gara a lotto unico
    int livEvento = 1;
    String codEvento = "GA_CREAZIONE_PROCEDURA_DACOPIA";
    String oggEvento = "";
    String descrEvento = "Inserimento della gara mediante funzione di copia (cod.gara sorgente #SORGENTE#)";
    String errMsgEvento = "";

    try{
      if(tipoCopia.equalsIgnoreCase("listaTornate")){
        try{
          // La copia e' stata richiesta dalla pagina 'Lista gare' (v_gare_torn-lista.jsp)
          // Flag per indicare se la gara sorgente e' una gara a lotto unico o meno
          boolean garaLottoUnico = datiForm.getLong("GARA_LOTTO_UNICO").intValue() == 1 ? true : false;

          String codiceGaraSorgente = sorgente;
          String codiceGaraDestinazione = null;
          String prefissoCodiceLotti = null;

          // Flag copia ditte
          boolean copiaDitte = datiForm.getLong("COPIA_DITTE").intValue() == 1 ? true : false;
          // Flag copia offerte delle ditte
          boolean copiaOfferte = datiForm.getLong("COPIA_OFFERTE").intValue() == 1 ? true : false;


          if(! garaLottoUnico){
            // Set dei diversi parametri per la copia di una gara a lotti in
            // un'altra gara a lotti e verifica della loro unicita' nella base dati

            boolean copiaLotti =
              datiForm.getLong("COPIA_LOTTI").intValue() == 1 ? true : false;

            if(isCodificaAutomatica){
              HashMap hm = pgManager.calcolaCodificaAutomatica("TORN", null, null,
              		null);
              codiceGaraDestinazione = (String) hm.get("codiceGara");
              if(copiaLotti)
                prefissoCodiceLotti = (String) hm.get("codiceGara");
            } else {
              codiceGaraDestinazione = destinazione;
              if(copiaLotti)
                prefissoCodiceLotti = datiForm.getString("PREFISSO_CODICE_LOTTI");
            }

            oggEvento = codiceGaraDestinazione;
            descrEvento = descrEvento.replace("#SORGENTE#", codiceGaraSorgente);

            // Flag per la copia dei lotti della gara a lotti
            try {
              // Verifica dei dati in ingresso
              pgManager.verificaPreliminareDatiCopiaGara(codiceGaraSorgente,
                  codiceGaraDestinazione, null, null, false);
              pgManager.copiaTORN(status, codiceGaraSorgente, codiceGaraDestinazione,
                  copiaLotti, prefissoCodiceLotti, copiaDitte, copiaOfferte,copiaScadenzario,
                  isCodificaAutomatica, copiaDocumentazione,this.getRequest(),copiaTermini);

              Long genereGara = null;
              Long bustalotti = null;
              Vector datiGara = sqlManager.getVector("select genere,bustalotti from gare where ngara=?",  new Object[]{codiceGaraDestinazione});
              if(datiGara!=null && datiGara.size()>0){
                genereGara = SqlManager.getValueFromVectorParam(datiGara, 0).longValue();
                bustalotti = SqlManager.getValueFromVectorParam(datiGara, 1).longValue();
              }

              //inizializzazione dei documenti delle ditte dei lotti
              if(copiaDitte && !copiaOfferte){

                String selectLotti = "select ngara from gare where codgar1=? and codgar1!=ngara";
                //Se la gara è ad offerta unica e bustalotti =2 si deve considerare solo come lotto quello con ngara=codgar1
                //Negli altri casi viene esclusa dalla lista la gara con ngara=codgar (quindi nel caso di bustalotti=1 si elimina la gara fittizzia)
                if((new Long (3)).equals(genereGara) && (new Long (2)).equals(bustalotti))
                  selectLotti = "select ngara from gare where ngara=?";

               //Lotti della gara
                List listLotti = sqlManager.getListVector(selectLotti,
                    new Object[]{codiceGaraDestinazione});
                if(listLotti!= null && listLotti.size()>0){
                  for(int j=0; j< listLotti.size(); j++) {
                    String ngaraLottoDestinazione = (String) SqlManager.getValueFromVectorParam(
                        listLotti.get(j), 0).getValue();
                    this.inizializzaDocumentazioneDitte(codiceGaraDestinazione, ngaraLottoDestinazione,  pgManager);

                  }
                }
                //Nel caso di genere = 3 e bustalotti=1 poichè è stato eliminato dai lotti il caso di ngara=codgar1,
                //si deve considerare il caso dell'inserimento in IMPRDOCG delle occorrenze relative a DOCUMGARA con
                //NGARA = null e gruppo = 3 e busta 1,2.
                //Per farle processare dal metodo inizializzaDocumentazioneDitte specifico come codice lotto un valore qualsiasi
                //che non coincida con codgar
                if((new Long (3)).equals(genereGara) && (new Long (1)).equals(bustalotti))
                  this.inizializzaDocumentazioneDitteGaraFittizziaBustalotti1(codiceGaraDestinazione);

              }

              //Gestione per gare telematiche
              Long offtel = (Long)this.sqlManager.getObject("select offtel from torn where codgar=?", new Object[]{codiceGaraSorgente});
              String gartel = (String)this.geneManager.getSql().getObject("select gartel from torn where codgar=?", new Object[]{codiceGaraSorgente});
              Long offtelDestinazione = this.getOfftelDestinazione(offtel, gartel, codiceGaraDestinazione);
              if(!copiaDocumentazione){
                //Se procedura telematica con offerta presentata da portale(OFFTEL=1) si inserisce in DOCUMGARA l'occorrenza per l'offerta economica
                if(new Long(1).equals(offtelDestinazione)){
                  this.sqlManager.update("insert into documgara(codgar, norddocg, busta, gruppo, tipodoc, descrizione, obbligatorio, modfirma, valenza, gentel, numord) " +
                            "values(?,?,?,?,?,?,?,?,?,?,?)", new Object[]{codiceGaraDestinazione, new Long(1), new Long(3), new Long(3), new Long(1),
                      "Offerta economica", "1", new Long(1), new Long(0), "1", new Long(1) });
                }
                String selectLotti = "select ngara from gare where codgar1=? and codgar1!=ngara order by ngara asc";
                String selectFormatoDefinito = "select g1cridef.ngara from g1cridef, goev where g1cridef.ngara = ? and g1cridef.formato != 100 and g1cridef.ngara = goev.ngara and goev.necvan = g1cridef.necvan and goev.tippar = 1";
                String selectMaxNord = "select max(norddocg) from documgara where codgar = ?";
                Long maxNorddocg = new Long(0);
                Long norddocg = (Long) sqlManager.getObject(selectMaxNord,new Object[] { codiceGaraDestinazione });
                if(norddocg != null){maxNorddocg = norddocg;}
                //Lotti della gara
                 List listLotti = sqlManager.getListVector(selectLotti,
                     new Object[]{codiceGaraDestinazione});
                 List listLottiSorgente = sqlManager.getListVector(selectLotti,
                     new Object[]{codiceGaraSorgente});
                 if(listLotti!= null && listLotti.size()>0 && listLottiSorgente!= null && listLottiSorgente.size()>0){
                   for(int j=0; j< listLotti.size(); j++) {

                     String ngaraLottoSorgente = (String) SqlManager.getValueFromVectorParam(
                         listLottiSorgente.get(j), 0).getValue();
                     String ngaraLottoDestinazione = (String) SqlManager.getValueFromVectorParam(
                         listLotti.get(j), 0).getValue();
                     String flag = (String) sqlManager.getObject(selectFormatoDefinito,new Object[] { ngaraLottoSorgente });
                     if(flag != null){
                     this.sqlManager.update("insert into documgara(codgar, ngara, norddocg, busta, gruppo, tipodoc, descrizione, obbligatorio, modfirma, valenza, gentel, numord) " +
                         "values(?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{codiceGaraDestinazione, ngaraLottoDestinazione, new Long(j+2+maxNorddocg), new Long(2), new Long(3), new Long(1),
                         "Offerta tecnica", "1", new Long(1), new Long(0), "1", new Long(1) });
                     }
                   }
                 }
              }else{
                if(offtel==null)
                  offtel= new Long(0);
                if(offtelDestinazione==null)
                  offtelDestinazione= new Long(0);
                if(offtel.longValue()!=offtelDestinazione.longValue() && "1".equals(gartel)){
                  if(offtelDestinazione!=null && offtelDestinazione.longValue()==1){
                    this.sqlManager.update("delete from w_docdig where IDPRG in(select DOCUMGARA.IDPRG from DOCUMGARA where CODGAR = ? and busta=? and descrizione=?) " +
                            "and IDDOCDIG in (select DOCUMGARA.IDDOCDG from DOCUMGARA where CODGAR = ? and busta=? and descrizione=? )",
                            new Object[]{codiceGaraDestinazione, new Long(3),"Offerta economica",codiceGaraDestinazione, new Long(3),"Offerta economica"});
                    this.sqlManager.update("delete from documgara where codgar=? and busta=? and descrizione=?",new Object[]{codiceGaraDestinazione, new Long(3),"Offerta economica"});
                    //si inserisce in DOCUMGARA l'occorrenza per l'offerta economica
                    Long maxNorddocg = (Long) this.geneManager.getSql().getObject(
                        "select max(NORDDOCG) from DOCUMGARA where CODGAR= ?",
                        new Object[] { codiceGaraDestinazione });
                    long newNorddocg = 1;
                    if (maxNorddocg != null && maxNorddocg.longValue() > 0)
                      newNorddocg = maxNorddocg.longValue() + 1;
                    this.sqlManager.update("insert into documgara(codgar, norddocg, busta, gruppo, tipodoc, descrizione, obbligatorio, modfirma, valenza, gentel, numord) " +
                        "values(?,?,?,?,?,?,?,?,?,?,?)", new Object[]{codiceGaraDestinazione, new Long(newNorddocg), new Long(3), new Long(3), new Long(1),
                          "Offerta economica", "1", new Long(1), new Long(0), "1", new Long(1) });
                  }else if(offtelDestinazione!=null && offtelDestinazione.longValue()==2){
                    this.sqlManager.update("delete from w_docdig where IDPRG in(select DOCUMGARA.IDPRG from DOCUMGARA where CODGAR = ? and busta=? and descrizione=? and gentel = ?) " +
                        "and IDDOCDIG in (select DOCUMGARA.IDDOCDG from DOCUMGARA where CODGAR = ? and busta=? and descrizione=? and gentel = ?)",
                        new Object[]{codiceGaraDestinazione, new Long(3),"Offerta economica","1", codiceGaraDestinazione, new Long(3),"Offerta economica","1"});
                    this.sqlManager.update("delete from documgara where codgar=? and gentel = ? and busta=? and descrizione=?",new Object[]{codiceGaraDestinazione, "1", new Long(3),"Offerta economica"});
                  }
                }
              }

              //Se gara ad offerta unica telematica, se il campo OFFTEL di destinazione assume il valore 1, allora il campo BUSTALOTTI deve essere forzato a 1
              if(new Long(1).equals(offtelDestinazione) && (new Long (3)).equals(genereGara)){
                this.sqlManager.update("update gare set bustalotti=? where codgar1=? and ngara=codgar1",new Object[]{new Long(1), codiceGaraDestinazione});
              }

              this.getRequest().setAttribute("TIPOGARA",
                  this.getRequest().getParameter("TIPOGARA"));
              this.getRequest().setAttribute("GENEREGARA",
                      this.getRequest().getParameter("GENEREGARA"));
              this.getRequest().setAttribute("RISULTATO", "OK");

              // Messaggi da visualizzare nel risultato dell'operazione, nel caso
              // di codifica automatica attiva
              if(isCodificaAutomatica){
                this.getRequest().setAttribute("codiceGara", codiceGaraDestinazione);
                this.getRequest().setAttribute("COPIA_LOTTI", datiForm.getLong("COPIA_LOTTI"));
              }
            } catch (GestoreException e) {
              this.ripristinoParametri();
              this.getRequest().setAttribute("RISULTATO", "KO");

              throw e;
            }catch (SQLException e1) {
              this.ripristinoParametri();
              this.getRequest().setAttribute("RISULTATO", "KO");

              throw new GestoreException("Errore nell'aggiornamento della documentazione delle ditte !",null, e1);
            }
          } else {
            // Copia di una gara a lotto unico
            String nGaraSorgente = codiceGaraSorgente.replaceFirst("\\$", "");
            String nGaraDestinazione = null;

            // Flag per indicare che NON si sta copiando una gara dal metodo
            // PgManager.copiaTORN
            boolean copiaGareDaTorn = false;

            boolean copiaComeLotto = datiForm.getLong(
                "COPIA_COME_LOTTO").intValue() == 1 ? true : false;

            String codiceLotto = null;
            boolean copiaComeLottoNellaStessaGara = true;

            try {
              if(copiaComeLotto){


                // Set dei diversi parametri per la copia della gara a lotto unico
                // sorgente come un lotto di una gara a lotti e verifica della loro
                // unicita' nella base dati
                if(isCodificaAutomatica){
                  codiceGaraDestinazione = datiForm.getString("CODICE_GARA");
                  HashMap hm = pgManager.calcolaCodificaAutomatica("GARE",
                      Boolean.FALSE, codiceGaraDestinazione, null);
                  //codiceGaraDestinazione = (String) hm.get("codiceGara");
                  nGaraDestinazione = (String) hm.get("numeroGara");
                } else {
                  codiceGaraDestinazione = datiForm.getString("CODICE_GARA");
                  nGaraDestinazione = destinazione;
                }

                try{
                  codiceLotto = datiForm.getString("CODICE_LOTTO");
                  if(!UtilityNumeri.isAValidNumber(codiceLotto, UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE) || ('0' == codiceLotto.charAt(0) && codiceLotto.length() > 1)){
                    this.ripristinoParametri();
                    this.getRequest().setAttribute("RISULTATO", "KO");
                    UtilityStruts.addMessage(this.getRequest(), "error",  "errors.gestoreException.*.codigaNumerico", null);
                    return;
                  }
                  if(!this.verificaCodiga(datiForm,codiceGaraDestinazione,codiceLotto)){
                    this.ripristinoParametri();
                    this.getRequest().setAttribute("RISULTATO", "KO");
                    UtilityStruts.addMessage(this.getRequest(), "error",  "error.copiaLotto.codiceDupilcato", null);
                    return;
                  }
                }catch (SQLException e) {
                  this.ripristinoParametri();
                  this.getRequest().setAttribute("RISULTATO", "KO");
                  throw new GestoreException("Errore nella verifica sul campo CODIGA dei lotti !",null, e);
                }

              } else {
                // Set dei diversi parametri per la copia della gara a lotto unico
                // sorgente in un'altra gara a lotto unico e verifica della loro
                // unicita' nella base dati
                if(isCodificaAutomatica){
                  if(this.getGeneManager().getProfili().checkProtec(
                      (String) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), "COLS", "VIS", "GARE.GARE.PRECED")){
                    //Profilo gare per rilancio
                    String preced=(String)this.sqlManager.getObject("select preced from gare where ngara=?", new Object[]{nGaraSorgente});
                    nGaraDestinazione = pgManager.getNumeroGaraCodificaAutomatica(preced,null,"GARE","PRECED");
                    codiceGaraDestinazione="$"+nGaraDestinazione;
                  }else{
                    HashMap hm = pgManager.calcolaCodificaAutomatica("GARE",
                    		Boolean.TRUE, null, null);
                    codiceGaraDestinazione = (String) hm.get("codiceGara");
                    nGaraDestinazione = (String) hm.get("numeroGara");
                  }
                } else {
                  codiceGaraDestinazione = "$".concat(destinazione);
                  nGaraDestinazione = destinazione;
                }
              }

              oggEvento = nGaraDestinazione;

              descrEvento = descrEvento.replace("#SORGENTE#", nGaraSorgente);

              // Verifica dei dati in ingresso (Istruzione superflua nel caso di codifica automatica)
              pgManager.verificaPreliminareDatiCopiaGara(codiceGaraSorgente,
                  codiceGaraDestinazione, nGaraSorgente, nGaraDestinazione,
                  copiaComeLotto);

              // Esecuzione copia della gara
              pgManager.copiaGARE(status, nGaraSorgente, codiceGaraSorgente,
                  nGaraDestinazione, codiceGaraDestinazione, copiaDitte, copiaOfferte,
                  copiaGareDaTorn, this.getRequest(),copiaComeLotto,copiaScadenzario,copiaTermini);

              if(copiaComeLotto){
                //Si deve aggironare il CODIGA del lotto appena creato con il valore di CODICE_LOTTO
                sqlManager.update("update GARE set CODIGA = ? where NGARA=?",
                    new Object[] { codiceLotto,nGaraDestinazione});
                //Allineamento del tipgar del lotto con quello della gara
                pgManager.updateTipgargLotto(nGaraDestinazione, codiceGaraDestinazione);
                if(!codiceGaraSorgente.equals(codiceGaraDestinazione))
                  copiaComeLottoNellaStessaGara= false;
              }

              //Gestione per gare telematiche
              Long offtel = (Long)this.sqlManager.getObject("select offtel from torn where codgar=?", new Object[]{codiceGaraSorgente});
              String gartel = (String)this.geneManager.getSql().getObject("select gartel from torn where codgar=?", new Object[]{codiceGaraSorgente});
              /*
              Long offtelDestinazione = offtel;
              if("1".equals(gartel) && !this.geneManager.getProfili().checkProtec(
                    (String) this.getRequest().getSession().getAttribute(
                        CostantiGenerali.PROFILO_ATTIVO), "COLS", "MOD",
                    "GARE.TORN.OFFTEL")){
                String desc = tabellatiManager.getDescrTabellato("A1099", "2");
                if (desc != null) {
                  desc = desc.substring(0, 1);
                  offtelDestinazione = new Long(desc);
                  this.geneManager.getSql().update("update torn set offtel=? where codgar=?", new Object[]{offtelDestinazione,codiceGaraDestinazione});
                }
              }
               */
              Long offtelDestinazione = this.getOfftelDestinazione(offtel, gartel, codiceGaraDestinazione);

              //Copia della documentazione di gara
              if(copiaDocumentazione){
                pgManager.copiaDocumentazione(status,nGaraSorgente, codiceGaraSorgente, nGaraDestinazione, codiceGaraDestinazione, false,"GARE");
                if(offtel==null)
                  offtel= new Long(0);
                if(offtelDestinazione==null)
                  offtelDestinazione= new Long(0);
                if(offtel.longValue()!=offtelDestinazione.longValue() && "1".equals(gartel)){
                  if(offtelDestinazione!=null && offtelDestinazione.longValue()==1){
                    this.sqlManager.update("delete from w_docdig where IDPRG in(select DOCUMGARA.IDPRG from DOCUMGARA where CODGAR = ? and busta=? and descrizione=?) " +
                    		"and IDDOCDIG in (select DOCUMGARA.IDDOCDG from DOCUMGARA where CODGAR = ? and busta=? and descrizione=? )",
                    		new Object[]{codiceGaraDestinazione, new Long(3),"Offerta economica",codiceGaraDestinazione, new Long(3),"Offerta economica"});
                    this.sqlManager.update("delete from documgara where codgar=? and busta=? and descrizione=?",new Object[]{codiceGaraDestinazione, new Long(3),"Offerta economica"});
                    //si inserisce in DOCUMGARA l'occorrenza per l'offerta economica
                    Long maxNorddocg = (Long) this.geneManager.getSql().getObject(
                        "select max(NORDDOCG) from DOCUMGARA where CODGAR= ?",
                        new Object[] { codiceGaraDestinazione });
                    long newNorddocg = 1;
                    if (maxNorddocg != null && maxNorddocg.longValue() > 0)
                      newNorddocg = maxNorddocg.longValue() + 1;
                    this.sqlManager.update("insert into documgara(codgar, ngara, norddocg, busta, gruppo, tipodoc, descrizione, obbligatorio, modfirma, valenza, gentel, numord) " +
                        "values(?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{codiceGaraDestinazione, nGaraDestinazione, new Long(newNorddocg), new Long(3), new Long(3), new Long(1),
                          "Offerta economica", "1", new Long(1), new Long(0), "1", new Long(1) });
                  }else if(offtelDestinazione!=null && offtelDestinazione.longValue()==2){
                    this.sqlManager.update("delete from w_docdig where IDPRG in(select DOCUMGARA.IDPRG from DOCUMGARA where CODGAR = ? and busta=? and descrizione=? and gentel = ?) " +
                        "and IDDOCDIG in (select DOCUMGARA.IDDOCDG from DOCUMGARA where CODGAR = ? and busta=? and descrizione=? and gentel = ?)",
                        new Object[]{codiceGaraDestinazione, new Long(3),"Offerta economica","1", codiceGaraDestinazione, new Long(3),"Offerta economica","1"});
                    this.sqlManager.update("delete from documgara where codgar=? and gentel = ? and busta=? and descrizione=?",new Object[]{codiceGaraDestinazione, "1", new Long(3),"Offerta economica"});
                  }
                }
              }else{
                //Se procedura telematica con offerta presentata da portale(OFFTEL=1) si inserisce in DOCUMGARA l'occorrenza per l'offerta economica
                if(new Long(1).equals(offtelDestinazione)){
                  this.sqlManager.update("insert into documgara(codgar, ngara, norddocg, busta, gruppo, tipodoc, descrizione, obbligatorio, modfirma, valenza, gentel, numord) " +
                            "values(?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{codiceGaraDestinazione, nGaraDestinazione, new Long(1), new Long(3), new Long(3), new Long(1),
                      "Offerta economica", "1", new Long(1), new Long(0), "1", new Long(1) });
                }
                String SelectLottiFormatoDef = "select g1cridef.ngara from g1cridef, gare, goev where gare.codgar1 = ? " +
                		"and g1cridef.ngara = gare.ngara and g1cridef.formato != 100 " +
                		"and g1cridef.ngara = goev.ngara and goev.necvan = g1cridef.necvan and goev.tippar = 1";
                List ngaraLotti = sqlManager.getListVector(SelectLottiFormatoDef,new Object[] { codiceGaraSorgente });

                if (ngaraLotti != null && ngaraLotti.size() > 0) {
                  this.sqlManager.update("insert into documgara(codgar, ngara, norddocg, busta, gruppo, tipodoc, descrizione, obbligatorio, modfirma, valenza, gentel, numord) " +
                      "values(?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{codiceGaraDestinazione, nGaraDestinazione, new Long(2), new Long(2), new Long(3), new Long(1),
                      "Offerta tecnica", "1", new Long(1), new Long(0), "1", new Long(1) });
                  }
              }

              if(copiaDitte && !copiaOfferte)
                this.inizializzaDocumentazioneDitte(codiceGaraDestinazione, nGaraDestinazione,pgManager);
              else if(copiaDitte && copiaOfferte && copiaDocumentazione)
                pgManager.copiaIMPRDOCG(status,nGaraSorgente,codiceGaraSorgente, nGaraDestinazione, codiceGaraDestinazione);

              this.getRequest().setAttribute("TIPOGARA",
                  this.getRequest().getParameter("TIPOGARA"));
              this.getRequest().setAttribute("GENEREGARA",
                      this.getRequest().getParameter("GENEREGARA"));
              this.getRequest().setAttribute("RISULTATO", "OK");

              // Messaggi da visualizzare nel risultato dell'operazione, nel caso
              // di codifica automatica attiva
              if(isCodificaAutomatica){
                if(copiaComeLotto){
                  // Codice del nuovo lotto di gara: 'NGARA'
                  this.getRequest().setAttribute("numeroGara", nGaraDestinazione);
                  this.getRequest().setAttribute("COPIA_COME_LOTTO",
                      datiForm.getLong("COPIA_COME_LOTTO"));
                } else {
                  // Codice della nuova gara a lotto unico: 'CODGAR'
                  this.getRequest().setAttribute("codiceGara", codiceGaraDestinazione);
                }
              }
              if(copiaComeLotto){
                this.AggiornaImportoTotaleTorn(nGaraDestinazione, codiceGaraDestinazione, nGaraSorgente, pgManagerEst1);

              }
            } catch (GestoreException e) {
              this.ripristinoParametri();
              this.getRequest().setAttribute("RISULTATO", "KO");
              throw e;
            }catch (SQLException e) {
              this.ripristinoParametri();
              this.getRequest().setAttribute("RISULTATO", "KO");
              throw new GestoreException("Errore nella copia del lotto di gara !",null, e);
            }

          }
        }catch( GestoreException e){
          if(tipoCopia.equalsIgnoreCase("listaTornate")){
            livEvento = 3;
            errMsgEvento = e.getMessage();
          }
          throw e;
        }
      } else if(tipoCopia.equalsIgnoreCase("listaLotti")){
        // La copia e' stata richiesta della pagina 'Lista lotti' (torn-pg-lista-lotti.jsp)

        String nGaraSorgente = sorgente;
        String codiceGaraSorgente = null;
        String nGaraDestinazione = null;
        String codiceGaraDestinazione = null;
        String codiceGaraSorgenteTmp = null;
        String codiceLotto=null;

        boolean copiaDitte =
          datiForm.getLong("COPIA_DITTE").intValue() == 1 ? true : false;
        // Flag copia offerte delle ditte
        boolean copiaOfferte = datiForm.getLong("COPIA_OFFERTE").intValue() == 1 ? true : false;

        boolean copiaComeLotto =
          datiForm.getLong("COPIA_COME_LOTTO").intValue() == 1 ? true : false;
        boolean copiaGareDaTorn = false;
        boolean copiaComeLottoNellaStessaGara = true;

        if(copiaComeLotto){

          // Set dei diversi parametri per la copia del lotto di gara sorgente come
          // lotto di gara di un'altra gara a lotti o della stessa gara a lotti
          if(isCodificaAutomatica){
            codiceGaraDestinazione = datiForm.getString("CODICE_GARA");
            HashMap hm = pgManager.calcolaCodificaAutomatica("GARE",
                Boolean.FALSE, codiceGaraDestinazione, null);
            nGaraDestinazione = (String) hm.get("numeroGara");
          } else {
            codiceGaraDestinazione = datiForm.getString("CODICE_GARA");
            nGaraDestinazione = destinazione;
          }

          try{
            /*
            codiceLotto = datiForm.getString("CODICE_LOTTO");
            //determino il codice gara sorgente
            codiceGaraSorgenteTmp = (String) sqlManager.getObject(
                "select codgar1 from gare where ngara= ?", new Object[] { nGaraSorgente });
            //Si deve controllare che non esista già un lotto con CODIGA pari a CODICE_LOTTO
            Long numOccorrenze = (Long)sqlManager.getObject(
                "select count(ngara) from gare where codgar1=? and codiga=?", new Object[] { codiceGaraSorgenteTmp, codiceLotto});
            if(numOccorrenze!= null && numOccorrenze.longValue()>0){
              this.ripristinoParametri();
              this.getRequest().setAttribute("RISULTATO", "KO");
              //Throwable t= new Throwable();
              //throw new GestoreException("Il valore specificato nel campo Lotto già presente nella gara !",null, t);
              UtilityStruts.addMessage(this.getRequest(), "error",  "error.copiaLotto.codiceDupilcato", null);
              return;
            }
            */
            codiceLotto = datiForm.getString("CODICE_LOTTO");
            if(!UtilityNumeri.isAValidNumber(codiceLotto, UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE) || ('0' == codiceLotto.charAt(0) && codiceLotto.length() > 1)){
              this.ripristinoParametri();
              this.getRequest().setAttribute("RISULTATO", "KO");
              UtilityStruts.addMessage(this.getRequest(), "error",  "errors.gestoreException.*.codigaNumerico", null);
              return;
            }
            if(!this.verificaCodiga(datiForm,codiceGaraDestinazione,codiceLotto)){
              this.ripristinoParametri();
              this.getRequest().setAttribute("RISULTATO", "KO");
              UtilityStruts.addMessage(this.getRequest(), "error",  "error.copiaLotto.codiceDupilcato", null);
              return;
            }
          }catch (SQLException e) {
            this.ripristinoParametri();
            this.getRequest().setAttribute("RISULTATO", "KO");
            throw new GestoreException("Errore nella verifica sul campo CODIGA dei lotti !",null, e);
          }

        } else {
          // Set dei diversi parametri per la copia del lotto di gara sorgente
          // come gara a lotto unico
          if(isCodificaAutomatica){
            HashMap hm = pgManager.calcolaCodificaAutomatica("GARE", Boolean.TRUE,
            		null, null);
            codiceGaraDestinazione = (String) hm.get("codiceGara");
            nGaraDestinazione = (String) hm.get("numeroGara");
          } else {
            codiceGaraDestinazione = "$" + destinazione;
            nGaraDestinazione = destinazione;
          }
        }

        try {
          // Verifica dei dati in ingresso
          pgManager.verificaPreliminareDatiCopiaGara(codiceGaraSorgente,
              codiceGaraDestinazione, nGaraSorgente, nGaraDestinazione, copiaComeLotto);

          pgManager.copiaGARE(status, nGaraSorgente, codiceGaraSorgente,
              nGaraDestinazione, codiceGaraDestinazione, copiaDitte, copiaOfferte,
              copiaGareDaTorn, this.getRequest(),copiaComeLotto,copiaScadenzario,copiaTermini);

          if(copiaComeLotto){
            //Si deve aggironare il CODIGA del lotto appena creato con il valore di CODICE_LOTTO
            sqlManager.update("update GARE set CODIGA = ? where NGARA=?",
                new Object[] { codiceLotto,nGaraDestinazione});
            //Allineamento del tipgar del lotto con quello della gara
            pgManager.updateTipgargLotto(nGaraDestinazione, codiceGaraDestinazione);
          }

          //determino il codice gara sorgente
          if(codiceGaraSorgenteTmp!= null)
            codiceGaraSorgente = codiceGaraSorgenteTmp;
          else
            codiceGaraSorgente = (String) sqlManager.getObject(
              "select codgar1 from gare where ngara= ?", new Object[] { nGaraSorgente });

          //Copia della documentazione di gara
          if(copiaDocumentazione){
            pgManager.copiaDocumentazione(status,nGaraSorgente, codiceGaraSorgente, nGaraDestinazione, codiceGaraDestinazione, true,"GARE");
          }else{

            String SelectLottiFormatoDef = "select g1cridef.ngara from g1cridef, gare, goev where gare.ngara = ? and g1cridef.ngara = gare.ngara and g1cridef.formato != 100 and g1cridef.ngara = goev.ngara and goev.necvan = g1cridef.necvan and goev.tippar = 1";
            List ngaraLotti = sqlManager.getListVector(SelectLottiFormatoDef,new Object[] { nGaraSorgente });
            if (ngaraLotti != null && ngaraLotti.size() > 0) {
              String selectMaxNord = "select max(norddocg) from documgara where codgar = ?";
              Long maxNorddocg = new Long(0);
              Long norddocg = (Long) sqlManager.getObject(selectMaxNord,new Object[] { codiceGaraSorgente });
              if(norddocg != null){maxNorddocg = norddocg;}
              this.sqlManager.update("insert into documgara(codgar, ngara, norddocg, busta, gruppo, tipodoc, descrizione, obbligatorio, modfirma, valenza, gentel, numord) " +
                  "values(?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{codiceGaraDestinazione, nGaraDestinazione, maxNorddocg+1, new Long(2), new Long(3), new Long(1),
                  "Offerta tecnica", "1", new Long(1), new Long(0), "1", new Long(1) });
              }
          }

          if(copiaComeLotto && !codiceGaraSorgente.equals(codiceGaraDestinazione))
            copiaComeLottoNellaStessaGara= false;

          if(copiaDitte && !copiaOfferte){
            //Nel caso di bustalotti=2 le occorrenze di imprdocg vanno associate alla gara
            Long genereGara = null;
            Long bustalotti = null;
            Vector datiGara = sqlManager.getVector("select genere,bustalotti from gare where ngara=?",  new Object[]{codiceGaraDestinazione});
            if(datiGara!=null && datiGara.size()>0){
               genereGara = SqlManager.getValueFromVectorParam(datiGara, 0).longValue();
               bustalotti = SqlManager.getValueFromVectorParam(datiGara, 1).longValue();

            }
            if(!((new Long (3)).equals(genereGara) && (new Long (2)).equals(bustalotti)))
              this.inizializzaDocumentazioneDitte(codiceGaraDestinazione, nGaraDestinazione,pgManager);
          }else if(copiaDitte && copiaOfferte && copiaDocumentazione)
            pgManager.copiaIMPRDOCG(status,nGaraSorgente,codiceGaraSorgente, nGaraDestinazione, codiceGaraDestinazione);

          this.getRequest().setAttribute("TIPOGARA",
              this.getRequest().getParameter("TIPOGARA"));
          this.getRequest().setAttribute("GENEREGARA",
                  this.getRequest().getParameter("GENEREGARA"));
          this.getRequest().setAttribute("RISULTATO", "OK");

          // Messaggi da visualizzare nel risultato dell'operazione, nel caso
          // di codifica automatica attiva
          if(isCodificaAutomatica){
            if(copiaComeLotto){
              // Codice del nuovo lotto di gara: 'NGARA'
              this.getRequest().setAttribute("numeroGara", nGaraDestinazione);
              this.getRequest().setAttribute("COPIA_COME_LOTTO",
                  datiForm.getLong("COPIA_COME_LOTTO"));
            } else {
              // Codice della nuova gara a lotto unico: 'CODGAR'
              this.getRequest().setAttribute("codiceGara", codiceGaraDestinazione);
            }
          }

          if(copiaComeLotto){
            this.AggiornaImportoTotaleTorn(nGaraDestinazione, codiceGaraDestinazione, nGaraSorgente, pgManagerEst1);
          }

        } catch (GestoreException e) {
          this.ripristinoParametri();
          this.getRequest().setAttribute("RISULTATO", "KO");
          throw e;
        } catch (SQLException e) {
          this.ripristinoParametri();
          this.getRequest().setAttribute("RISULTATO", "KO");
          throw new GestoreException("Errore nella copia del lotto di gara !",null, e);
        }


      }
    } finally{
      //Tracciatura eventi
      try {
        if(tipoCopia.equalsIgnoreCase("listaTornate")){
          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(oggEvento);
          logEvento.setCodEvento(codEvento);
          logEvento.setDescr(descrEvento);
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        }
      } catch (Exception le) {
        String messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
      }
    }
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  /**
   * Ripristino dei parametri ricevuti dal request per la riapertura della popup
   * di copia gara in caso di errore nell'operazione
   */
  private void ripristinoParametri(){
    this.getRequest().setAttribute("TIPO_COPIA",
        this.getRequest().getParameter("TIPO_COPIA"));
    this.getRequest().setAttribute("TIPOGARA",
        this.getRequest().getParameter("TIPOGARA"));
    this.getRequest().setAttribute("GENEREGARA",
            this.getRequest().getParameter("GENEREGARA"));
    this.getRequest().setAttribute("GARA_LOTTO_UNICO",
        this.getRequest().getParameter("GARA_LOTTO_UNICO"));
    this.getRequest().setAttribute("COPIA_LOTTI",
        this.getRequest().getParameter("COPIA_LOTTI"));
    this.getRequest().setAttribute("SORGENTE",
        this.getRequest().getParameter("SORGENTE"));
    this.getRequest().setAttribute("CODICE_GARA",
        this.getRequest().getParameter("CODICE_GARA"));
    this.getRequest().setAttribute("DESTINAZIONE",
        this.getRequest().getParameter("DESTINAZIONE"));
    this.getRequest().setAttribute("PREFISSO_CODICE_LOTTI",
        this.getRequest().getParameter("PREFISSO_CODICE_LOTTI"));
    this.getRequest().setAttribute("COPIA_DITTE",
        this.getRequest().getParameter("COPIA_DITTE"));
    //this.getRequest().setAttribute("COPIA_OFFERTE",
    //    this.getRequest().getParameter("COPIA_OFFERTE"));
    this.getRequest().setAttribute("COPIA_COME_LOTTO",
        this.getRequest().getParameter("COPIA_COME_LOTTO"));
    this.getRequest().setAttribute("CODICE_LOTTO",
        this.getRequest().getParameter("CODICE_LOTTO"));
    this.getRequest().setAttribute("TIPGAR",
        this.getRequest().getParameter("TIPGAR"));
    this.getRequest().setAttribute("chiave",
        this.getRequest().getParameter("chiave"));
    this.getRequest().setAttribute("COPIA_SCADENZARIO",
        this.getRequest().getParameter("COPIA_SCADENZARIO"));
    this.getRequest().setAttribute("COPIA_DOCUMENTAZIONE",
        this.getRequest().getParameter("COPIA_DOCUMENTAZIONE"));
    this.getRequest().setAttribute("COPIA_TERMINI",
        this.getRequest().getParameter("COPIA_TERMINI"));

  }

  /**
   * Verifica se nella gara di destinazione il codiceLotto è già adoperato
   *
   * @param datiForm
   * @param codiceGaraDestinazione
   * @param codiceLotto
   *
   * @throws SQLException
   * @throws GestoreException
   */
  private boolean verificaCodiga(DataColumnContainer datiForm, String codiceGaraDestinazione,String codiceLotto) throws SQLException, GestoreException{
    boolean ret= true;
    //determino il codice gara sorgente
    //String codiceGaraDestTmp = (String) sqlManager.getObject(
    //    "select codgar1 from gare where ngara= ?", new Object[] { nGaraDestinazione });
    //Si deve controllare che non esista già un lotto con CODIGA pari a CODICE_LOTTO
    Long numOccorrenze = (Long)sqlManager.getObject(
        "select count(ngara) from gare where codgar1=? and codiga=?", new Object[] { codiceGaraDestinazione, codiceLotto});
    if(numOccorrenze!= null && numOccorrenze.longValue()>0)
      ret=false;


    return ret;
  }

  private void inizializzaDocumentazioneDitte(String codiceGaraDestinazione, String nGaraDestinazione, PgManager pgManager) throws SQLException, GestoreException{
    List listaDitteCopiate = sqlManager.getListVector("select dittao from ditg where codgar5=? " +
        "and ngara5=?",new Object[]{codiceGaraDestinazione,nGaraDestinazione});
      if(listaDitteCopiate!= null && listaDitteCopiate.size()>0){
        for(int j=0; j< listaDitteCopiate.size(); j++) {
          String codiceDitta = (String) SqlManager.getValueFromVectorParam(
              listaDitteCopiate.get(j), 0).getValue();

          //Viene popolata la IMPRDOCG a partire dalle occorrenze di DOCUMGARA,
          //della gara destinazione, impostando SITUAZDOCI a 2 e PROVENI a 1
          pgManager.inserimentoDocumentazioneDitta(codiceGaraDestinazione, nGaraDestinazione, codiceDitta);
        }
      }
  }

  private void AggiornaImportoTotaleTorn(String ngaraDestinazione,String codgarDestinazione,String ngaraSorgente,PgManagerEst1 pgManagerEst1) throws SQLException, GestoreException{
    //Visto che la copia avviene sempre in una gara ad offerte distinte o ad offerte unica, non si deve controllare che la gara
    //destinazione non sia lotto unico
    Double importoLotto = (Double)sqlManager.getObject("select impapp from gare where ngara=?", new Object[]{ngaraSorgente});
    Double importoRinnoviLotto = null;
    Vector datiGare1=this.sqlManager.getVector("select imprin,impserv,imppror,impaltro from gare1 where ngara=?", new Object[]{ngaraSorgente});
    if(datiGare1!=null && datiGare1.size()>0){
      Double imprinLotto= SqlManager.getValueFromVectorParam(datiGare1, 0).doubleValue();
      Double impservLotto= SqlManager.getValueFromVectorParam(datiGare1, 1).doubleValue();
      Double impprorLotto= SqlManager.getValueFromVectorParam(datiGare1, 2).doubleValue();
      Double impaltroLotto= SqlManager.getValueFromVectorParam(datiGare1, 3).doubleValue();
      importoRinnoviLotto = pgManagerEst1.calcoloValoreMassimoStimato(importoLotto, imprinLotto, impservLotto, impprorLotto, impaltroLotto);
    }
    pgManagerEst1.setImportoTotaleTorn(codgarDestinazione, ngaraDestinazione, importoLotto,importoRinnoviLotto);

  }


  /**
   * inserimento in imprdocg delle occorrenze relative a documgara con ngara=null e gruppo=3 e busta=1,2 e relative ad una gara con genere=3, bustalotti=1
   *
   * @param codiceGaraDestinazione
   *
   * @throws SQLException
   * @throws GestoreException
   */
  private void inizializzaDocumentazioneDitteGaraFittizziaBustalotti1(String codiceGaraDestinazione) throws SQLException, GestoreException{
    List listaDitteCopiate = sqlManager.getListVector("select dittao from ditg where codgar5=? " +
        "and ngara5=?",new Object[]{codiceGaraDestinazione,codiceGaraDestinazione});
      if(listaDitteCopiate!= null && listaDitteCopiate.size()>0){
        String sql = "insert into IMPRDOCG(CODGAR,CODIMP,NORDDOCI,NGARA,PROVENI,DOCTEL) values(?,?,?,?,?,?)";
        for(int j=0; j< listaDitteCopiate.size(); j++) {
          String codiceDitta = (String) SqlManager.getValueFromVectorParam(
              listaDitteCopiate.get(j), 0).getValue();

          //Viene popolata la IMPRDOCG a partire dalle occorrenze di DOCUMGARA,
          //della gara destinazione, impostando SITUAZDOCI a 2 e PROVENI a 1
          String gartel = (String) sqlManager.getObject(
              "select gartel from torn where codgar = ?",
              new Object[] { codiceGaraDestinazione });

          Long acquisizione = (Long) sqlManager.getObject(
              "select acquisizione from ditg where codgar5 = ? and ngara5 = ? and dittao=?",
              new Object[] { codiceGaraDestinazione, codiceGaraDestinazione, codiceDitta });

          String select="select NORDDOCG from DOCUMGARA where CODGAR = ? and NGARA is null and GRUPPO = 3 "
              + " and (busta =1 or busta =2)";
          if(acquisizione!=null && acquisizione.longValue()==5)
            select += " and BUSTA <> 4";

          List listaDocumgara = this.sqlManager.getListVector(select,
              new Object[] { codiceGaraDestinazione });

          if (listaDocumgara != null && listaDocumgara.size() > 0) {

            for (int i = 0; i < listaDocumgara.size(); i++) {
              Vector documento = (Vector) listaDocumgara.get(i);
              Long tmpProgressivoDoc = (Long) ((JdbcParametro) documento.get(0)).getValue();
              String doctel = "2";
              if("1".equals(gartel))
                doctel="1";
                this.sqlManager.update(sql, new Object[] { codiceGaraDestinazione, codiceDitta,
                    tmpProgressivoDoc, codiceGaraDestinazione, new Long(1), doctel });
            }
          }

        }
      }
  }

  private Long getOfftelDestinazione(Long offtelPartenza, String gartel, String codiceGaraDestinazione) throws SQLException{
    Long offtelDestinazione = offtelPartenza;
    if("1".equals(gartel) && !this.geneManager.getProfili().checkProtec(
          (String) this.getRequest().getSession().getAttribute(
              CostantiGenerali.PROFILO_ATTIVO), "COLS", "MOD",
          "GARE.TORN.OFFTEL")){
      String desc = tabellatiManager.getDescrTabellato("A1099", "2");
      if (desc != null) {
        desc = desc.substring(0, 1);
        offtelDestinazione = new Long(desc);
        this.geneManager.getSql().update("update torn set offtel=? where codgar=?", new Object[]{offtelDestinazione,codiceGaraDestinazione});
      }
    }
    return offtelDestinazione;
  }
}