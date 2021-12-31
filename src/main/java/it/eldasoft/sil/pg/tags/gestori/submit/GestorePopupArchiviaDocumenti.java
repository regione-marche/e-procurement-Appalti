/*
 * Created on 13/04/15
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

public class GestorePopupArchiviaDocumenti extends
    AbstractGestoreEntita {

  /** Logger */
  static Logger logger = Logger.getLogger(GestorePopupArchiviaDocumenti.class);

  @Override
  public String getEntita() {
    return "DOCUMGARA";
  }

  public GestorePopupArchiviaDocumenti() {
    super(false);
  }


  public GestorePopupArchiviaDocumenti(boolean isGestoreStandard) {
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

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    String[] listaDocumentiSelezionati = this.getRequest().getParameterValues("keys");
    String codgar = this.getRequest().getParameter("codgar1");
    String ngara = this.getRequest().getParameter("ngara");
    String norddocg = null;
    String tipoDoc = this.getRequest().getParameter("tipoDoc");
    String isarchi = this.getRequest().getParameter("isarchi");
    String lottoDiGara = this.getRequest().getParameter("lottoDiGara");
    String modlic = this.getRequest().getParameter("modlic");
    String isProceduraTelematica = this.getRequest().getParameter("isProceduraTelematica");
    String valtec = this.getRequest().getParameter("valtec");
    String avviso = this.getRequest().getParameter("avviso");
    String bustalotti = this.getRequest().getParameter("bustalotti");
    String sezionitec = this.getRequest().getParameter("sezionitec");
    String elencoNorddocg = "";
    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
        "tabellatiManager", this.getServletContext(), TabellatiManager.class);


    if (listaDocumentiSelezionati != null && !"1".equals(isarchi)) {
      //variabili per tracciatura eventi
      String messageKey = null;
      int livEvento = 3;
      String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
      String codGara = "";
      try {

        String updateDocumgara = "update documgara set isarchi=? where codgar=? and norddocg=?";
        try {
          for (int i = 0; i < listaDocumentiSelezionati.length; i++) {
            norddocg = listaDocumentiSelezionati[i];
            elencoNorddocg += norddocg;
            if (i < listaDocumentiSelezionati.length - 1)
              elencoNorddocg += ",";
            if (codgar != null && norddocg != null) {
                this.sqlManager.update(updateDocumgara, new Object[] { "1", codgar,
                    new Long(norddocg) });

              Date oggi = UtilityDate.getDataOdiernaAsDate();
              this.sqlManager.update("update torn set dultagg=? where codgar=?", new Object[] {oggi, codgar});
              this.getRequest().setAttribute("RISULTATO", "OK");
            }
          }

          if (!"1".equals(lottoDiGara)) {
			String select = "select count(norddocg) from documgara where codgar=? and gruppo=? and (ISARCHI is null or ISARCHI<>'1') and statodoc = 5";
            Object parametri[] = null;

            if (ngara != null && !"".equals(ngara)) {
              select += " and ngara=?";
              parametri = new Object[]{codgar, new Long(tipoDoc), ngara};
            } else {
              select += " and ngara is null";
              parametri = new Object[]{codgar, new Long(tipoDoc)};
            }

            //Si deve controllare che dopo gli aggiornamenti siano rispettate le condizioni per la pubblicazione su Portale
           String msgErrore = "";
           if("1".equals(tipoDoc) || "4".equals(tipoDoc) || "6".equals(tipoDoc)){
              //Deve rimanere almeno un documento
              Long conteggio = (Long)this.sqlManager.getObject(select,parametri);
              if(conteggio==null || conteggio.longValue()==0){
                if("1".equals(tipoDoc)){
                  msgErrore="del bando/avviso";
                }else if("4".equals(tipoDoc)){
                  msgErrore="dell'esito";
                }else{
                  msgErrore="dell'invito";
                }
                if("1".equals(avviso))
                  msgErrore="";

                livEvento = 3;
                errMsgEvento = "Errore nell'archiviazione dei documenti per la gara." +
                		"Almeno un documento " + msgErrore + " deve rimanere attivo";

                this.getRequest().setAttribute("RISULTATO", "ERRORI_CONTROLLI");
                throw new GestoreException(
                    "Errore nell'archiviazione dei documenti per la gara "
                    + codgar  , "archiviazioneDocumenti.assenzaDocumenti",new Object[]{msgErrore,""}, new Exception());
              }
            } else if ("3".equals(tipoDoc) && "true".equals(isProceduraTelematica)) {

              //Controlli da effettuare solo nel caso di TORN.ITERGA = 1 oppure esiste occ. in PUBBLI con TIPPUB=13,23
              Long iterga = (Long)this.sqlManager.getObject("select iterga from torn where codgar=?", new Object[]{codgar});
              Long conteggioPubblicazioni = (Long)this.sqlManager.getObject("select count(codgar9) from pubbli where "
                  + "codgar9=? and (tippub=? or tippub=?)", new Object[]{codgar, new Long(13), new Long(23)});
              if ((new Long(1)).equals(iterga) || (conteggioPubblicazioni != null && conteggioPubblicazioni.longValue() > 0)) {
                Long conteggio1 = null;  //economico
                Long conteggio2 = null;  //tecnico
                Long conteggio2_1 = null; //tecnico qualitativo
                Long conteggio2_2 = null; //tecnico quantitativo
                if (!"1".equals(bustalotti)) {
                  String selectBusta = select + " and busta=3";
                  conteggio1 = (Long) this.sqlManager.getObject(selectBusta, parametri);
                  if ("6".equals(modlic) || "1".equals(valtec)) {
                    if ("1".equals(sezionitec)) {
                      selectBusta = select + " and busta=2 and seztec = 1";
                      conteggio2_1 = (Long) this.sqlManager.getObject(selectBusta, parametri);
                      selectBusta = select + " and busta=2 and seztec = 2";
                      conteggio2_2 = (Long) this.sqlManager.getObject(selectBusta, parametri);
                    } else {
                      selectBusta = select + " and busta=2";
                      conteggio2 = (Long) this.sqlManager.getObject(selectBusta, parametri);
                    }
                  }
                } else {
                  //nel caso di bustalotti=1 si deve considerare che le occorrenze di documgara possono essere associate sia alla gara
                  //che ai lotti
                  PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
                      this.getServletContext(), PgManager.class);
                  select="select count(codgar) from DOCUMGARA where CODGAR=? and gruppo=? and busta=? and ngara is null and (ISARCHI is null or ISARCHI<>'1') and statodoc = 5";
                  //busta tecnica
                  parametri = new Object[] { codgar, new Long(3), new Long(2) };
                  if ("6".equals(modlic) || "1".equals(valtec)) {
                    if ("1".equals(sezionitec)) {
                      conteggio2_1 = (Long) sqlManager.getObject(select + " and seztec = 1", parametri);
                      conteggio2_2 = (Long) sqlManager.getObject(select + " and seztec = 2", parametri);
                      if (!(new Long(1).compareTo(conteggio2_1) <= 0 &&  new Long(1).compareTo(conteggio2_2) <= 0)) {
                        //Si deve ciclare a livello di lotti
                        if (!pgManager.controlloDocumentazioneLotti(codgar, true, new Long(2), true, false, false, "1")) {
                          conteggio2_1 = new Long(0);
                          conteggio2_2 = new Long(0);
                        } else {
                          conteggio2_1 = new Long(1);
                          conteggio2_2 = new Long(1);
                        }
                      }
                    } else {
                      Long conteggio = (Long) sqlManager.getObject(select, parametri);
                      if (conteggio == null || (conteggio != null && conteggio.longValue() == 0)) {
                        //Si deve ciclare a livello di lotti
                        if (!pgManager.controlloDocumentazioneLotti(codgar, true, new Long(2), true, false, false)) {
                          conteggio2 = new Long(0);
                        }
                      }
                    }
                  }
                  //busta economica
                  parametri = new Object[] { codgar, new Long(3), new Long(3) };
                  Long conteggio = (Long)sqlManager.getObject(select, parametri);
                  if(conteggio==null || (conteggio!=null && conteggio.longValue()==0)){
                    if(!pgManager.controlloDocumentazioneLotti(codgar, false, new Long(3),true, false,false)){
                      conteggio1 = new Long (0);
                    }
                  }
                }
                if (!"1".equals(sezionitec)) {
                  if (conteggio1 != null && conteggio1.longValue() == 0 && conteggio2 != null && conteggio2.longValue() == 0)
                    msgErrore =  "della busta tecnica e della busta economica";
                  else if (conteggio1!=null && conteggio1.longValue()==0)
                    msgErrore =  "della busta economica";
                  else if (conteggio2!=null && conteggio2.longValue()==0)
                    msgErrore =  "della busta tecnica";
                } else {
                  if (new Long(0).equals(conteggio1) && (!(new Long(1).compareTo(conteggio2_1) <= 0 &&  new Long(1).compareTo(conteggio2_2) <= 0))) {
                    msgErrore =  "della busta tecnica (per la sezione qualitativa e uno per la sezione quantitativa) e della busta economica";
                  } else if (conteggio1 != null && conteggio1.longValue() == 0) {
                    msgErrore =  "della busta economica";
                  } else if (!(new Long(1).compareTo(conteggio2_1) <= 0 &&  new Long(1).compareTo(conteggio2_2) <= 0)) {
                    msgErrore =  "della busta tecnica per la sezione qualitativa e uno per la sezione quantitativa ";
                  }
                }


                if(!"".equals(msgErrore)){
                  String msgErrore1= "";
                  if("1".equals(avviso))
                    msgErrore="";
                  else{
                    if("1".equals(bustalotti))
                      msgErrore1 = " per i lotti";
                  }

                  livEvento = 3;
                  errMsgEvento = "Errore nell'archiviazione dei documenti per la gara." +
                  		"Almeno un documento " + msgErrore + " deve rimanere attivo " + msgErrore1;

                  this.getRequest().setAttribute("RISULTATO", "ERRORI_CONTROLLI");
                  throw new GestoreException(
                      "Errore nell'archiviazione dei documenti per la gara "
                      + codgar  , "archiviazioneDocumenti.assenzaDocumenti",new Object[]{msgErrore, msgErrore1}, new Exception());
                }
              }
            }else if("15".equals(tipoDoc)){
              Long conteggioPubblicazioni = (Long)this.sqlManager.getObject("select count(codgar9) from pubbli where "
                  + "codgar9=? and tippub=?", new Object[]{codgar, new Long(15)});
              if(conteggioPubblicazioni!=null && conteggioPubblicazioni.longValue()>0){
                Long conteggio = (Long)this.sqlManager.getObject(select,parametri);
                if(conteggio==null || conteggio.longValue()==0){
                  livEvento = 3;
                  errMsgEvento = "Errore nell'archiviazione dei documenti per la gara." +
                        "Almeno un documento di delibera a contrarre deve rimanere attivo";
                  msgErrore="della delibera a contrarre";

                  this.getRequest().setAttribute("RISULTATO", "ERRORI_CONTROLLI");
                  throw new GestoreException(
                      "Errore nell'archiviazione dei documenti per la gara "
                      + codgar  , "archiviazioneDocumenti.assenzaDocumenti",new Object[]{msgErrore,""}, new Exception());
                }

              }
            }else if("5".equals(tipoDoc)){
              Long conteggioPubblicazioni = (Long)this.sqlManager.getObject("select count(*) from pubg, gare where "
                  + "gare.codgar1=? and pubg.ngara = gare.ngara and pubg.tippubg=?", new Object[]{codgar, new Long(14)});
              if(conteggioPubblicazioni!=null && conteggioPubblicazioni.longValue()>0){
                select = "select count(*) from documgara d, pubg p where d.codgar=? and d.gruppo=5 and p.tippubg = 14 and p.ngara = d.ngara and (d.ISARCHI is null or d.ISARCHI<>'1') and d.statodoc = 5";
                Long conteggio = (Long)this.sqlManager.getObject(select,new Object[]{codgar});
                if(conteggio==null || conteggio.longValue()==0){
                  livEvento = 3;
                  errMsgEvento = "Errore nell'archiviazione dei documenti per la gara." +
                        "Almeno un documento per la trasparenza deve rimanere attivo";
                  msgErrore="per la trasparenza";

                  this.getRequest().setAttribute("RISULTATO", "ERRORI_CONTROLLI");
                  throw new GestoreException(
                      "Errore nell'archiviazione dei documenti per la gara "
                      + codgar  , "archiviazioneDocumenti.assenzaDocumenti",new Object[]{msgErrore,""}, new Exception());
                }

              }
            }
          }
        } catch (SQLException e) {
          this.getRequest().setAttribute("RISULTATO", "ERRORI");
          throw new GestoreException(
              "Errore nell'archiviazione dei documenti per la gara "
              + codgar  , null, e);
        }

        //best case
        livEvento = 1 ;
        errMsgEvento = "";

      }finally{
        //Tracciatura eventi
        try {
          ngara = UtilityStringhe.convertiNullInStringaVuota(ngara);
          if(!"".equals(ngara)){
            Long genere = (Long) sqlManager.getObject("select genere from V_GARE_GENERE where codice = ?", new Object[]{ngara});
            if(genere != null){
              if(new Long(100).equals(genere)){
                codGara = codgar;
              }else{
                codGara = ngara;
              }
            }
          }
          String descrTipoDoc = tabellatiManager.getDescrTabellato("A1064", tipoDoc);
          descrTipoDoc = "Archiviazione " + descrTipoDoc + " (id.doc.: " + elencoNorddocg + ")";
          LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
          logEvento.setLivEvento(livEvento);
          logEvento.setOggEvento(codGara);
          logEvento.setCodEvento("GA_ARCHIVIA_DOC");
          logEvento.setDescr(descrTipoDoc);
          logEvento.setErrmsg(errMsgEvento);
          LogEventiUtils.insertLogEventi(logEvento);
        } catch (Exception le) {
          messageKey = "errors.logEventi.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), le);
        }
      }
    }
  }


}