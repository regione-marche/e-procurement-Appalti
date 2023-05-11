/*
 * Created on 05/10/22
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.transaction.TransactionStatus;

import it.appaltiecontratti.appalticgmsclient.api.V10Api;
import it.appaltiecontratti.appalticgmsclient.model.GarecgDto;
import it.appaltiecontratti.appalticgmsclient.model.GarecgInsertResponse;
import it.appaltiecontratti.appalticgmsclient.model.GarecgUpdateResponse;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.invcom.InvioComunicazioniManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.invcom.DestinatarioComunicazione;
import it.eldasoft.gene.db.domain.invcom.InvioComunicazione;
import it.eldasoft.gene.db.domain.invcom.ModelloComunicazione;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.MEvalManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.utils.JwtTokenUtilities;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

public class GestorePopupAbilitaValutazMEval extends
    AbstractGestoreEntita {

  private static final int CODICE_GENERE_ABILITAZIONE_M_EVAL = 58;
  public static final int INDIRIZZO_PEC = 1;
  public static final int INDIRIZZO_MAIL = 2;

  private String urlFrontEnd = ConfigManager.getValore(MEvalManager.PROP_INTEGRAZIONE_MEVAL_URL_FRONTEND);

  /** Logger */
  static Logger logger = Logger.getLogger(GestorePopupAbilitaValutazMEval.class);

  private InvioComunicazioniManager invioComunicazioniManager;
  private PgManager pgManager;
  private TabellatiManager tabellatiManager;


  @Override
  public String getEntita() {
    return "GFOF";
  }

  public GestorePopupAbilitaValutazMEval() {
    super(false);
  }

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    invioComunicazioniManager = (InvioComunicazioniManager) UtilitySpring.getBean("invioComunicazioniManager",
        this.getServletContext(), InvioComunicazioniManager.class);
    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);
    tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        this.getServletContext(), TabellatiManager.class);

  }

  public GestorePopupAbilitaValutazMEval(boolean isGestoreStandard) {
    super(isGestoreStandard);
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

  private class Commissario{
    String cf="";
    String nome="";
    String cognome="";
    boolean isPresidente=false;
    Long id=null;
    String intestazione="";
    String codice="";
    String email="";
    Integer tipoMail=null;

    public void setCf(String cf) {
      this.cf=cf;
    }

    public void setNome(String nome) {
      this.nome=nome;
    }

    public void setCognome(String cognome) {
      this.cognome=cognome;
    }

    public void setIsPresidente(boolean isPresidente) {
      this.isPresidente=isPresidente;
    }

    public void setId(Long id) {
      this.id=id;
    }

    public void setIntestazione(String intestazione) {
      this.intestazione=intestazione;
    }

    public void setCodice(String codice) {
      this.codice=codice;
    }

    public void setEmail(String email) {
      this.email=email;
    }

    public void setTipoMail(Integer tipoMail) {
      this.tipoMail=tipoMail;
    }

    public String getCf() {
      return this.cf;
    }

    public String getNome() {
      return this.nome;
    }

    public String getCognome() {
      return this.cognome;
    }

    public boolean getIsPresidente() {
      return this.isPresidente;
    }

    public Long getId() {
      return this.id;
    }

    public String getIntestazione() {
      return this.intestazione;
    }

    public String getCodice() {
      return this.codice;
    }

    public String getEmail() {
      return this.email;
    }

    public Integer getTipoMail() {
      return this.tipoMail;
    }
  }

  private class Report{
    long numAbilitazioniOk=0;
    long numAbilitazioniNok=0;
    long numDisabilitazioniOk=0;
    long numDisabilitazioniNok=0;
    String msgAbilitazioniOk="";
    String msgAbilitazioniNOk="";
    String msgDisabilitazioniOk="";
    String msgDisabilitazioniNOk="";
    private String lottiInteressati="";

    public long getNumAbilitazioniOk() {
      return numAbilitazioniOk;
    }

    public void incrementeNumAbilitazioniOk() {
      this.numAbilitazioniOk ++;
    }

    public long getNumAbilitazioniNok() {
      return numAbilitazioniNok;
    }

    public void incrementaNumAbilitazioniNok() {
      this.numAbilitazioniNok ++;
    }

    public long getNumDisabilitazioniOk() {
      return numDisabilitazioniOk;
    }

    public void incrementaNumDisabilitazioniOk() {
      this.numDisabilitazioniOk ++;
    }

    public long getNumDisabilitazioniNok() {
      return numDisabilitazioniNok;
    }

    public void incrementaNumDisabilitazioniNok() {
      this.numDisabilitazioniNok ++;
    }

    public String getMsgAbilitazioniOk() {
      return msgAbilitazioniOk;
    }

    public void setMsgAbilitazioniOk(String msgAbilitazioniOk) {
      this.msgAbilitazioniOk += msgAbilitazioniOk;
    }

    public String getMsgAbilitazioniNOk() {
      return msgAbilitazioniNOk;
    }

    public void setMsgAbilitazioniNOk(String msgAbilitazioniNOk) {
      this.msgAbilitazioniNOk += msgAbilitazioniNOk;
    }

    public String getMsgDisabilitazioniOk() {
      return msgDisabilitazioniOk;
    }

    public void setMsgDisabilitazioniOk(String msgDisabilitazioniOk) {
      this.msgDisabilitazioniOk += msgDisabilitazioniOk;
    }

    public String getMsgDisabilitazioniNOk() {
      return msgDisabilitazioniNOk;
    }

    public void setMsgDisabilitazioniNOk(String msgDisabilitazioniNOk) {
      this.msgDisabilitazioniNOk += msgDisabilitazioniNOk;
    }

    public String getLottiInteressati() {
      return lottiInteressati;
    }

    public void setLottiInteressati(String lottiInteressati) {
      this.lottiInteressati += lottiInteressati;
    }

    public void rimozioneCaratteri() {
      if(msgAbilitazioniOk.length()>2) {
        msgAbilitazioniOk= msgAbilitazioniOk.substring(0, msgAbilitazioniOk.length() - 2);
      }
      if(msgAbilitazioniNOk.length()>2) {
        msgAbilitazioniNOk= msgAbilitazioniNOk.substring(0, msgAbilitazioniNOk.length() - 2);
      }
      if(msgDisabilitazioniOk.length()>2) {
        msgDisabilitazioniOk= msgDisabilitazioniOk.substring(0, msgDisabilitazioniOk.length() - 2);
      }
      if(msgDisabilitazioniNOk.length()>2) {
        msgDisabilitazioniNOk= msgDisabilitazioniNOk.substring(0, msgDisabilitazioniNOk.length() - 2);
      }
      if(lottiInteressati.length()>2) {
        lottiInteressati= lottiInteressati.substring(0, lottiInteressati.length() - 2);
      }
    }
  }

  private class Rup {
    String nomeRup="";
    String cfRup="";
    Long incRup=null;

    Rup(String codgar, SqlManager sqlManager){
      try {
        Vector<JdbcParametro> datiRup = sqlManager.getVector("select nomtec, cftec,inctec from torn,tecni where codgar=? and codrup=codtec", new String[] {codgar});
        if(datiRup!=null) {
          this.nomeRup = SqlManager.getValueFromVectorParam(datiRup, 0).getStringValue();
          this.cfRup = SqlManager.getValueFromVectorParam(datiRup, 1).getStringValue();
          this.incRup =  SqlManager.getValueFromVectorParam(datiRup, 2).longValue();
        }
      } catch (Exception e) {
        logger.error("Errore nella lettura dei dati del rup della gara " + codgar, e);
      }
    }


    public String getNomeRup() {
      return nomeRup;
    }


    public String getCfRup() {
      return cfRup;
    }


    public Long getIncRup() {
      return incRup;
    }


  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    String[] listaComponentiAbilitati = this.getRequest().getParameterValues("keys");
    String codgar = this.getRequest().getParameter("codgar");
    String lotto = this.getRequest().getParameter("lotto");
    String genere = this.getRequest().getParameter("genere");
    String esito ="OK";
    String msgControllo="";
    String idDisabilitati = this.getRequest().getParameter("idDisabilitati");
    String commTuttiDis = this.getRequest().getParameter("commTuttiDis");

    //Controlli preliminari
    String cfSa=null;
    String denominazioneSa=null;
    try {
      Vector<JdbcParametro> datiSa = this.sqlManager.getVector("select cfein,nomein from torn,uffint where codgar=? and cenint=codein", new String[] {codgar});
      if(datiSa!=null) {
        cfSa = SqlManager.getValueFromVectorParam(datiSa, 0).getStringValue();
        denominazioneSa = SqlManager.getValueFromVectorParam(datiSa, 1).getStringValue();
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura della stazione appaltante",null,e);
    }
    if(cfSa == null || "".equals(cfSa)) {
      esito="NOK";
      msgControllo+="<br>Non &egrave; valorizzato il codice fiscale della stazione appaltante.";
    }

    String id = null;
    List<Commissario> listaCommissariDaAbilitare = null;
    Report report = new Report();
    String cfComm=null;
    String codComm = null;
    String intestazione = null;
    Vector<JdbcParametro> datiComm=null;

    String obbligoPresidente = ConfigManager.getValore(MEvalManager.PROP_INTEGRAZIONE_MEVAL_OBBLIGO_PRESIDENTE);
    long conteggioPresidenteAbilitati=0;

    if (listaComponentiAbilitati != null) {
      listaCommissariDaAbilitare = new ArrayList<Commissario>();
      String selectDatiCommissari="select cftec, ematec, ema2tec, codtec, incfof, cogtei, nometei, nomtec, codfof from tecni, gfof where id=? and codfof=codtec";
      String emailComm=null;
      String pecComm=null;
      Long incfof = null;
      String nomeTec=null;
      String cognomeTec=null;
      String codiceComm=null;


      String datiKey[]=null;
      String commicg="";
      Commissario commissario= null;

      for (int i = 0; i < listaComponentiAbilitati.length; i++) {
        datiKey = listaComponentiAbilitati[i].split(";");
        commicg = "";
        id = datiKey[0];
        if(datiKey.length>1)
          commicg = datiKey[1];

        try {
          datiComm=this.sqlManager.getVector(selectDatiCommissari, new Long[] {new Long(id)});
          if(datiComm!=null) {
            cfComm = SqlManager.getValueFromVectorParam(datiComm, 0).getStringValue();
            emailComm = SqlManager.getValueFromVectorParam(datiComm, 1).getStringValue();
            pecComm = SqlManager.getValueFromVectorParam(datiComm, 2).getStringValue();
            codComm = SqlManager.getValueFromVectorParam(datiComm, 3).getStringValue();
            incfof = SqlManager.getValueFromVectorParam(datiComm, 4).longValue();
            cognomeTec = SqlManager.getValueFromVectorParam(datiComm, 5).getStringValue();
            nomeTec = SqlManager.getValueFromVectorParam(datiComm, 6).getStringValue();
            intestazione = SqlManager.getValueFromVectorParam(datiComm, 7).getStringValue();
            codiceComm = SqlManager.getValueFromVectorParam(datiComm, 8).getStringValue();
          }
          if(!"1".equals(commicg)) {
            commissario= new Commissario();
            commissario.setCf(cfComm);
            commissario.setNome(nomeTec);
            commissario.setCognome(cognomeTec);
            commissario.setId(new Long(id));
            commissario.setIntestazione(intestazione);
            commissario.setCodice(codiceComm);
            if(cfComm== null || "".equals(cfComm)) {
              esito="NOK";
              msgControllo+="<br>Non &egrave; valorizzato il codice fiscale del commissario " + codComm + ".";
            }
            if((emailComm== null || "".equals(emailComm)) && (pecComm== null || "".equals(pecComm))){
              esito="NOK";
              msgControllo+="<br>Non &egrave; valorizzata ne la email ne la pec del commissario " + codComm + ".";
            }else {
              if(pecComm!= null && !"".equals(pecComm)) {
                commissario.setEmail(pecComm);
                commissario.setTipoMail(INDIRIZZO_PEC);
              }else {
                commissario.setEmail(emailComm);
                commissario.setTipoMail(INDIRIZZO_MAIL);
              }
            }
          }
          if("1".equals(obbligoPresidente) && this.isPresidente(incfof)) {
            //String desc=tabellatiManager.getDescrTabellato("A1001", Long.toString(incfof));
            //if(desc!=null && "PRESIDENTE".equals(desc.toUpperCase())) {
            conteggioPresidenteAbilitati++;
            if(!"1".equals(commicg))
              commissario.setIsPresidente(true);
          }

          if(!"1".equals(commicg)) {
            if(!"1".equals(obbligoPresidente))
              commissario.setIsPresidente(true);
            listaCommissariDaAbilitare.add(commissario);
          }
        } catch (NumberFormatException | SQLException e) {
          throw new GestoreException("Errore nella lettura dei dati del commissario",null,e);
        }
      }

    }

    long conteggioPresidenteDisabilitati=0;
    List<Commissario> listaCommissariDaDisabilitare = null;
    if(idDisabilitati!=null && !"".equals(idDisabilitati)) {
      listaCommissariDaDisabilitare = new ArrayList<Commissario>();
      String selectDatiCommissari="select cftec, nomtec, codtec, incfof from tecni, gfof where id=? and codfof=codtec";
      String idDaDisabilitare[]= idDisabilitati.split(",");
      Commissario commissario= null;
      Long incfof = null;
      for(int i = 0; i < idDaDisabilitare.length; i++) {
        try {
          id=idDaDisabilitare[i];
          datiComm=this.sqlManager.getVector(selectDatiCommissari, new Long[] {new Long(id)});
          if(datiComm!=null) {
            cfComm = SqlManager.getValueFromVectorParam(datiComm, 0).getStringValue();
            intestazione = SqlManager.getValueFromVectorParam(datiComm, 1).getStringValue();
            codComm = SqlManager.getValueFromVectorParam(datiComm, 2).getStringValue();
            incfof = SqlManager.getValueFromVectorParam(datiComm, 3).longValue();
            commissario= new Commissario();
            commissario.setCf(cfComm);
            commissario.setId(new Long(id));
            commissario.setIntestazione(intestazione);
            listaCommissariDaDisabilitare.add(commissario);
            if("1".equals(obbligoPresidente) && conteggioPresidenteAbilitati==0 && this.isPresidente(incfof)) {
              conteggioPresidenteDisabilitati++;
            }
          }

          if(cfComm== null || "".equals(cfComm)) {
            esito="NOK";
            msgControllo+="<br>Non &egrave; valorizzato il codice fiscale del commissario " + codComm + ".";
          }

        } catch (NumberFormatException | SQLException e) {
          throw new GestoreException("Errore nella lettura dei dati del commissario",null,e);
        }
      }

      if("1".equals(obbligoPresidente) && listaComponentiAbilitati==null && "1".equals(commTuttiDis))
        conteggioPresidenteDisabilitati=0;
    }

    if("1".equals(obbligoPresidente)) {
      if((conteggioPresidenteAbilitati==0 && listaComponentiAbilitati != null) || (conteggioPresidenteDisabilitati>0 && idDisabilitati!=null && !"".equals(idDisabilitati)) ) {
        esito="NOK";
        msgControllo+="<br>Almeno uno tra i componenti della commissione abilitati alla valutazione su M-Eval deve avere l'incarico di presidente.";
      }
    }

    if("NOK".equals(esito)) {
      this.getRequest().setAttribute("RISULTATO", esito);
      this.getRequest().setAttribute("msg", msgControllo);
      return;
    }

    int livEvento = 3;
    try {
      List<Vector<JdbcParametro>> listaLotti = null;
      if((listaCommissariDaAbilitare!=null && listaCommissariDaAbilitare.size() >0 )|| (listaCommissariDaDisabilitare!=null && listaCommissariDaDisabilitare.size()>0)) {

        Rup rup = new Rup(codgar, this.sqlManager);
        V10Api apiAppaltiCgMs = MEvalManager.getClient(cfSa, 60); //Si imposta 1h come durata di validità del token
        String oggettoGaraOffUnica=null;
        String selectLotti = "select g.ngara, not_gar, codiga, codcig, impapp, statocg from gare g, gare1 g1 where g.codgar1=? and g.modlicg=? and g.codgar1!=g.ngara and g.ngara=g1.ngara and "
            + "exists(select gc.ngara from g1cridef gc, goev gv where gc.ngara=g.ngara and gv.ngara=gc.ngara and gv.necvan=gc.necvan and (modpunti=1 or modpunti=3) and tippar=1)";
        try {
          listaLotti = this.sqlManager.getListVector(selectLotti, new Object[] {codgar, new Long(6)});
          if("3".equals(genere)) {
            oggettoGaraOffUnica = (String)this.sqlManager.getObject("select destor from torn where codgar=?", new Object[] {codgar});
            if(listaLotti!=null && listaCommissariDaAbilitare != null && listaCommissariDaAbilitare.size()>0) {
              for(int z=0; z <listaLotti.size(); z++) {
                report.setLottiInteressati(SqlManager.getValueFromVectorParam(listaLotti.get(z), 0).getStringValue() + ", ");
              }
            }
          }
        } catch (SQLException e) {
          throw new GestoreException("Errore nella lettura dei dati della gara",null,e);
        }

        //Abilitazione commissari
        if (listaCommissariDaAbilitare != null && listaCommissariDaAbilitare.size()>0) {
          Iterator<Commissario> iter = listaCommissariDaAbilitare.iterator();
          String chiaveSegretaJwtChiaro= JwtTokenUtilities.getChiaveSegretaJwtChiaro();
          String urlAppaltiMs = ConfigManager.getValore(CostantiAppalti.PROP_APPALTI_MS_URL);
          while (iter.hasNext()) {
            Commissario commissario= iter.next();
            this.abilitaOperatore(commissario, apiAppaltiCgMs, listaLotti, urlAppaltiMs, cfSa, denominazioneSa, codgar, oggettoGaraOffUnica, chiaveSegretaJwtChiaro, rup, report,genere,lotto);
          }
          this.gestioneGarpesi(codgar, "INS", status);
        }

        //Disabilitazione commissari
        if(listaCommissariDaDisabilitare!=null && listaCommissariDaDisabilitare.size()>0) {

          Iterator<Commissario> iter = listaCommissariDaDisabilitare.iterator();
          while (iter.hasNext()) {
            Commissario commissario= iter.next();
            this.disabilitaOperatore(commissario, apiAppaltiCgMs, codgar, report, cfSa, lotto);
          }
          if("1".equals(commTuttiDis))
            this.gestioneGarpesi(codgar, "CANC", status);
        }

        //Aggiornamento stato
        if(listaLotti!=null) {
          boolean commitTransaction=true;
          Iterator<Vector<JdbcParametro>> iter = listaLotti.iterator();
          String chiaveGfof=lotto;
          if("3".equals(genere))
            chiaveGfof=codgar;
          while (iter.hasNext()) {
            Vector<JdbcParametro> vetLotto = iter.next();
            String nLotto = SqlManager.getValueFromVectorParam(vetLotto, 0).getStringValue();

            try {
              status = this.sqlManager.startTransaction();
              Long conteggio=(Long)this.sqlManager.getObject("select count(*) from gfof where commicg= ? and ngara2 =?", new Object[] {"1", chiaveGfof});
              Long statocgNuovo=null;
              if(conteggio!= null && conteggio.longValue()>0) {
                statocgNuovo= new Long(1);
              }
              this.sqlManager.update("update gare1 set statocg=? where ngara=?", new Object[] {statocgNuovo, nLotto});
              commitTransaction=true;
            }catch (SQLException e) {
              commitTransaction = false;
              logger.error("Errore nell'aggiornamento di statocg per il lotto " + nLotto, e);
            }finally {
              if (status != null) {
                try {
                  if (commitTransaction) {
                    this.sqlManager.commitTransaction(status);
                  } else {
                    this.sqlManager.rollbackTransaction(status);
                  }
                }catch (SQLException e) {
                  logger.error("Errore nell'aggiornamento di statocg per il lotto " + nLotto, e);
                }
              }
            }
          }
        }
      } else {
        esito="OK-NOP";
      }

      livEvento = 1 ;
      this.getRequest().setAttribute("RISULTATO", esito);
      this.getRequest().setAttribute("numAbilitatiOk", report.getNumAbilitazioniOk());
      this.getRequest().setAttribute("numDisabilitatiOk", report.getNumDisabilitazioniOk());
      this.getRequest().setAttribute("numAbilitatiNOk", report.getNumAbilitazioniNok());
      this.getRequest().setAttribute("numDisabilitatiNOk", report.getNumDisabilitazioniNok());
      if(report.getLottiInteressati().length()>0) {
        String lotti = report.getLottiInteressati();
        if(lotti.length()>2) {
          lotti= lotti.substring(0, lotti.length() - 2);
        }
        this.getRequest().setAttribute("lottiInteressati", lotti);
      }

    }finally {
      // Tracciatura eventi
      String errMsgEvento= "";

      if("OK-NOP".equals(esito)) {
        errMsgEvento= "Nessuna operazione apportata";
      }else {
        report.rimozioneCaratteri();
        if(report.getNumAbilitazioniOk()>0)
          errMsgEvento += "Commissari abilitati:" + report.msgAbilitazioniOk + "\r\n";
        if(report.getNumAbilitazioniNok()>0)
          errMsgEvento += "Commissari non abilitati in seguito a errori:" + report.msgAbilitazioniNOk + "\r\n";
        if(report.getNumDisabilitazioniOk()>0)
          errMsgEvento += "Commissari disabilitati:" + report.msgDisabilitazioniOk+ "\r\n";
        if(report.getNumDisabilitazioniNok()>0)
          errMsgEvento += "Commissari non disabilitati in seguito a errori:" + report.msgDisabilitazioniNOk + "\r\n";
        if(report.getLottiInteressati().length()>0)
          errMsgEvento += "Lotti interessati:" + report.getLottiInteressati() + "\r\n";
      }


      LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
      logEvento.setLivEvento(livEvento);
      String oggEvento = codgar;
      if(codgar.startsWith("$"))
        oggEvento=lotto;
      logEvento.setOggEvento(oggEvento);
      logEvento.setCodEvento("GA_ABILITAVAL_MEVAL");
      logEvento.setDescr("Abilitazione valutazione su M-Eval");
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);
    }
  }

  private  void abilitaOperatore(Commissario commissario, V10Api apiAppaltiCgMs, List<Vector<JdbcParametro>> listaLotti,
      String urlAppaltiMs, String cfSa, String denominazioneSa, String codgar, String oggettoGaraOffUnica, String chiaveSegretaJwtChiaro, Rup rup,
      Report report, String genere, String ngara ) {
    TransactionStatus status = null;
    boolean commitTransaction=true;

    if(listaLotti!=null) {

      String token = this.getTokenCommissario(commissario, chiaveSegretaJwtChiaro);
      if(token==null) {
        report.setMsgAbilitazioniNOk(commissario.getIntestazione() + "(" + commissario.getCf() + ") - gara " + ngara + ", ");
        report.incrementaNumAbilitazioniNok();
        logger.error("Errore nella generazione del token per la gara "  +  ngara  + " del commissario id:" + commissario.getId());
        return;
      }

      List<GarecgDto> listaDatiGara= new ArrayList<GarecgDto>();
      GarecgDto datiGara=null;
      Iterator<Vector<JdbcParametro>> iter = listaLotti.iterator();
      while (iter.hasNext()) {
        datiGara = new GarecgDto().apiurl(urlAppaltiMs).cfsa(cfSa).denominazionesa(denominazioneSa).codicegara(codgar);
        Vector<JdbcParametro> lotto = iter.next(); //"select ngara, not_gar, codiga, codcig, impapp from gare where codgar1=? and modlicg=? and codgar1!=ngara";
        String ngaraLotto = SqlManager.getValueFromVectorParam(lotto, 0).getStringValue();
        String not_gar = SqlManager.getValueFromVectorParam(lotto, 1).getStringValue();
        String codiga = SqlManager.getValueFromVectorParam(lotto, 2).getStringValue();
        String codcig = SqlManager.getValueFromVectorParam(lotto, 3).getStringValue();
        Double impapp = null;
        try {
          impapp = SqlManager.getValueFromVectorParam(lotto, 4).doubleValue();
        } catch (GestoreException e) {
          report.incrementaNumAbilitazioniNok();
          logger.error("Errore nella lettura dell'importo dell lotto " +  ngaraLotto  + " del commissario id:" + commissario.getId() ,e);
          report.setMsgAbilitazioniNOk(commissario.getIntestazione() + "(" + commissario.getCf() + "), ");
          return;
        }
        datiGara.setCodicelotto(ngaraLotto);
        if(codiga!=null && !"".equals(codiga)) {
          BigDecimal bdCodiga = new BigDecimal(codiga);
          datiGara.setNumlotto(bdCodiga);
        }
        datiGara.setCig(codcig);
        datiGara.setOggetto(not_gar);
        if(impapp!=null) {
          BigDecimal bdImpapp = new BigDecimal(impapp);
          datiGara.setImporto(bdImpapp);
        }
        datiGara.setCfu(UtilityTags.genSha256(commissario.getCf()));
        datiGara.setUsrkey(token);
        datiGara.setDatainserimento(getDataOdierna());
        datiGara.setStatoconferma(new Integer(0));
        datiGara.setPresidente(new Boolean(commissario.isPresidente));
        listaDatiGara.add(datiGara);
      }
      try {
        GarecgInsertResponse resp = apiAppaltiCgMs.insertGareUsingPOST(listaDatiGara);
        if(resp.isEsito()) {
          try {
            status = this.sqlManager.startTransaction();
            this.sqlManager.update("update gfof set commicg=? where id=?", new Object[] {"1", commissario.getId()});
            commitTransaction=true;
            report.incrementeNumAbilitazioniOk();
            report.setMsgAbilitazioniOk(commissario.getIntestazione() + "(" + commissario.getCf() + "), ");
            //Invio mail per ogni lotto
            iter = listaLotti.iterator();
            while (iter.hasNext()) {
              Vector<JdbcParametro> lotto = iter.next();
              this.creazioneMail(commissario, codgar, lotto, oggettoGaraOffUnica, rup);
            }

          } catch (SQLException e) {
            commitTransaction = false;
            logger.error("Errore nell'aggiornamento di commicg della gara " +  ngara  + " del commissario id:" + commissario.getId() ,e);
            report.setMsgAbilitazioniNOk(commissario.getIntestazione() + "(" + commissario.getCf() + "), ");
            report.incrementaNumAbilitazioniNok();
          } finally {
            if (status != null) {
              try {
              if (commitTransaction) {
                this.sqlManager.commitTransaction(status);
              } else {
                this.sqlManager.rollbackTransaction(status);
              }
              }catch(Exception e) {
                logger.error("Errore nell'aggiornamento di commicg della gara " +  ngara  + " del commissario id:" + commissario.getId() ,e);
              }
            }
          }
        }else {
          List<String> listaMessaggi = resp.getInfoMessaggi();
          if(listaMessaggi!=null) {
            Iterator<String> msg = listaMessaggi.iterator();
            while(msg.hasNext())
              logger.error("La chiamata al servizio insertGareUsingPOST per la gara " +  ngara  + " e commissario id:" + commissario.getId() + " ha restituito il seguente messaggio: " + msg.next());
          }
          report.setMsgAbilitazioniNOk(commissario.getIntestazione() + "(" + commissario.getCf() + "), ");
          report.incrementaNumAbilitazioniNok();
        }
      } catch (Exception e) {
        logger.error("Errore nella chiamata al servizio insertGaraUsingPOST della gara " +  ngara  + " del commissario id:" + commissario.getId() ,e);
        report.setMsgAbilitazioniNOk(commissario.getIntestazione() + "(" + commissario.getCf() + "), ");
        report.incrementaNumAbilitazioniNok();
      }
    }
  }

  private String getTokenCommissario(Commissario commissario, String chiaveSegretaJwtChiaro) {
    String token="";
    String giorniString = ConfigManager.getValore(MEvalManager.PROP_INTEGRAZIONE_MEVAL_EXP_TOKEN_COMMISSARIO);
    long giorni = new Long(giorniString).longValue();
    long expiration = System.currentTimeMillis() + 60 * 24 * giorni * 60000l;  //Si imposta la durata di validità del token in giorni
    Map<String, Object> claims = new HashMap<String, Object>();
    claims.put("USER_CF", commissario.getCf());
    claims.put("USER_SURNAME", commissario.getCognome());
    claims.put("USER_NAME", commissario.getNome());
    claims.put("syscon", new Long(0));
    Object codeinVal=null;
    try {
      JSONParser parser = new JSONParser();
      codeinVal = parser.parse("[]");
    } catch (ParseException e) {
      logger.error("Errore nella creazione del token per il commissario id:" + commissario.getId() ,e);
      return null;
    }
    claims.put("codein", codeinVal);

    token = JwtTokenUtilities.generateTokenWithClaims("Appalti", expiration, chiaveSegretaJwtChiaro,claims);

    return token;
  }

  private static String getDataOdierna() {
    String dataFormattata = null;
    Date data = UtilityDate.getDataOdiernaAsDate();
    if (data != null) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dataFormattata = formatter.format(data);
    }
    return dataFormattata;
  }


  private  void disabilitaOperatore(Commissario commissario, V10Api apiAppaltiCgMs, String codgar, Report report , String cfSa, String ngara) {
    TransactionStatus status = null;
    boolean commitTransaction=true;

    try {
      GarecgUpdateResponse resp = apiAppaltiCgMs.deleteGareByGaraCfuUsingDELETE(cfSa, UtilityTags.genSha256(commissario.getCf()), codgar);
      if(resp.isEsito()) {
        try {
          status = this.sqlManager.startTransaction();
          this.sqlManager.update("update gfof set commicg=? where id=?", new Object[] {"2", commissario.getId()});
          commitTransaction=true;
          report.incrementaNumDisabilitazioniOk();
          report.setMsgDisabilitazioniOk(commissario.getIntestazione() + "(" + commissario.getCf() + "), ");
        } catch (SQLException e) {
          report.incrementaNumDisabilitazioniNok();
          commitTransaction = false;
          logger.error("Errore nell'aggiornamento di commicg della gara " +  ngara  + " del commissario id:" + commissario.getId() ,e);
          report.setMsgDisabilitazioniNOk(commissario.getIntestazione() + "(" + commissario.getCf() + "), ");
        } finally {
          if (status != null) {
            if (commitTransaction) {
              this.sqlManager.commitTransaction(status);
            } else {
              this.sqlManager.rollbackTransaction(status);
            }
          }
        }

      }else {
        List<String> listaMessaggi = resp.getInfoMessaggi();
        if(listaMessaggi!=null) {
          Iterator<String> msg = listaMessaggi.iterator();
          while(msg.hasNext())
            logger.error("La chiamata al servizio deleteGareByGaraCfuUsingDELETE per la gara " +  ngara  + " e commissario id:" + commissario.getId() + " ha restituito il seguente messaggio: " + msg.next());
        }
        report.setMsgDisabilitazioniNOk(commissario.getIntestazione() + "(" + commissario.getCf() + "), ");
        report.incrementaNumDisabilitazioniNok();
      }
    } catch (Exception e) {
      logger.error("Errore nella chiamata al servizio deleteGareByGaraCfuUsingDELETE della gara " +  ngara  + " del commissario id:" + commissario.getId() ,e);
      report.setMsgDisabilitazioniNOk(commissario.getIntestazione() + "(" + commissario.getCf() + "), ");
      report.incrementaNumDisabilitazioniNok();
    }
  }

  private void creazioneMail(Commissario comm, String codgar, Vector<JdbcParametro> datiLotto, String oggettoGaraOffUnica, Rup rup) {
    ModelloComunicazione modello = this.invioComunicazioniManager.getModelloComunicazioneByGenere(CODICE_GENERE_ABILITAZIONE_M_EVAL);
    InvioComunicazione comunicazione = this.invioComunicazioniManager.createComunicazioneFromModello(modello);
    comunicazione.getPk().setIdProgramma("PG");

    String ngara="";
    String codiga="";
    String cig= "";
    String oggettoLotto="";
    try {
      ngara=SqlManager.getValueFromVectorParam(datiLotto, 0).stringValue();
      codiga=SqlManager.getValueFromVectorParam(datiLotto, 2).stringValue();
      cig= SqlManager.getValueFromVectorParam(datiLotto, 3).stringValue();
      oggettoLotto = SqlManager.getValueFromVectorParam(datiLotto, 1).stringValue();
    } catch (GestoreException e) {
      logger.error("Errore nella lettura dei dati della gara " + ngara, e);
    }

    if(oggettoGaraOffUnica!=null) {
      comunicazione.setEntita("TORN");
      comunicazione.setChiave1(codgar);
    }else {
      comunicazione.setEntita("GARE");
      comunicazione.setChiave1(ngara);
    }
    comunicazione.setNomeMittente(ConfigManager.getValore(CostantiGenerali.PROP_TITOLO_APPLICATIVO));
    String testo = comunicazione.getTesto();
    HashMap<String, Object> parametri = new HashMap<String, Object>();
    parametri.put("oggetto", testo);
    parametri.put("ngara", ngara);
    parametri.put("codgar", codgar);
    parametri.put("codiga",  codiga);
    parametri.put("g1codcig", cig);
    parametri.put("oggetta", oggettoLotto);
    parametri.put("g1destor", oggettoGaraOffUnica);
    parametri.put("nomtecrup", rup.getNomeRup());
    parametri.put("cftecrup", rup.getNomeRup());
    String incRup = null;
    if(rup.incRup!=null)
      incRup = rup.incRup.toString();
    parametri.put("inctec", incRup);
    parametri.put("urlMEval", urlFrontEnd);
    testo = pgManager.sostituzioneMnemonici(parametri);
    comunicazione.setTesto(testo);

    String oggetto = comunicazione.getOggetto();
    parametri.replace("oggetto", oggetto);
    oggetto = pgManager.sostituzioneMnemonici(parametri);
    comunicazione.setOggetto(oggetto);

    comunicazione.setComunicazionePubblica(InvioComunicazione.COMUNICAZIONE_RISERVATA);
    comunicazione.setDataPubblicazione(new Date());
    comunicazione.setStato(InvioComunicazione.STATO_COMUNICAZIONE_IN_USCITA);

    //Destinatario
    DestinatarioComunicazione destinatario = new DestinatarioComunicazione();
    destinatario.setEntitaArchivioDestinatario("TECNI");
    destinatario.setCodiceSoggettoArchivio(comm.getCodice());
    destinatario.setIndirizzo(comm.getEmail());
    destinatario.setTipoIndirizzo(comm.getTipoMail());
    destinatario.setIntestazione(comm.getIntestazione());
    comunicazione.getDestinatariComunicazione().add(destinatario);

    this.invioComunicazioniManager.insertComunicazione(comunicazione);
  }

  private boolean isPresidente(Long incfof) {
    boolean ret =false;
    String desc=this.tabellatiManager.getDescrTabellato("A1001", Long.toString(incfof));
    if(desc!=null && "PRESIDENTE".equals(desc.toUpperCase()))
      ret=true;
    return ret;
  }

  private void gestioneGarpesi(String codiceGara, String modo, TransactionStatus status) {

    boolean commitTransaction=true;
    try {
      status=null;
      if("INS".equals(modo)) {
        Long contGarPesi= (Long)this.sqlManager.getObject("select count(*) from garpesi where codicegara=?", new Object[] {codiceGara});
        if(contGarPesi==null || new Long(0).equals(contGarPesi)) {
          status = this.sqlManager.startTransaction();
          List<?> listaTab2 = this.sqlManager.getListVector("select tab2tip, tab2d1, tab2d2, tab2nord  from tab2 where tab2cod='A1z14' and (tab2arc is null or tab2arc = '2')", null);
          if(listaTab2!=null && listaTab2.size()>0) {
            String tab2tip = null;
            String tab2d1 = null;
            String tab2d2 = null;
            Long tab2nord = null;
            String descexcel = null;
            for(int i=0;i <listaTab2.size(); i++){
              tab2tip = SqlManager.getValueFromVectorParam(listaTab2.get(i), 0).stringValue();
              tab2d1 = SqlManager.getValueFromVectorParam(listaTab2.get(i), 1).stringValue();
              tab2d2 = SqlManager.getValueFromVectorParam(listaTab2.get(i), 2).stringValue();
              tab2nord = SqlManager.getValueFromVectorParam(listaTab2.get(i), 3).longValue();
              descexcel = this.getValore(tab2d2);
              this.sqlManager.update("insert into garpesi(codicegara,numpesi,valore,descrizione,ordine,descexcel) values(?,?,?,?,?,?)",
                  new Object[] {codiceGara, tab2tip, tab2d1, tab2d2, tab2nord, descexcel});
            }
          }
        }
      }else {
        status = this.sqlManager.startTransaction();
        this.sqlManager.update("delete from garpesi where codicegara=?", new Object[] {codiceGara});
      }

      commitTransaction=true;
    }catch (Exception e) {
      commitTransaction = false;
      logger.error("Errore nell'aggiornamento di garpesi per la gara " + codiceGara, e);
    }finally {
      if (status != null) {
        try {
          if (commitTransaction) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        }catch (SQLException e) {
          logger.error("Errore nell'aggiornamento di garpesi per la gara " + codiceGara, e);
        }
      }
    }
  }

  private String getValore(String stringa) {
    final String regex = "(?<=\\()(.*?)(?=\\))";
    String ret="";
    final Pattern pattern = Pattern.compile(regex);
    final Matcher matcher = pattern.matcher(stringa);

    if (matcher.find())
      ret=matcher.group(0);

    return ret;
  }
}