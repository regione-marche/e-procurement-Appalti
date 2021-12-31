package it.eldasoft.sil.pg.web.struts;
/*
 * Created on 10/12/2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.sil.portgare.datatypes.TipoPartecipazioneDocument;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.DatoBase64;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.spring.UtilitySpring;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.xmlbeans.XmlException;
/**
 * Esegue i controlli sul BARCODE
 */
public class LeggiBarcodeAction extends ActionBaseNoOpzioni {

  static Logger     logger = Logger.getLogger(LeggiBarcodeAction.class);

  private SqlManager sqlManager;
  private GeneManager geneManager;
  private PgManager pgManager;

  public void setSqlManager(SqlManager sqlManager){
      this.sqlManager = sqlManager;
  }

  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  public void setPgManager(PgManager pgManager) {
    this.pgManager = pgManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = null;
    String datidecripted = null;
    String messageKey = null;
    String messageLog = "";
    String datiacquisiti = new String(request.getParameter("datiacquisiti"));
    String selbarcode=null;
    String selbarcount=null;
    String selcodATI=null;
    String codimp=null;
    String filtroLivelloUtente =  new String(request.getParameter("filtroLivelloUtente"));
    String filtroTipoGara =  new String(request.getParameter("filtroTipoGara"));
    String visualizzazioneGareALotti =  new String(request.getParameter("visualizzazioneGareALotti"));
    String visualizzazioneGareLottiOffUnica =  new String(request.getParameter("visualizzazioneGareLottiOffUnica"));
    String filtroProfiloAttivo =  new String(request.getParameter("filtroProfiloAttivo"));
    String filtroUffint =  new String(request.getParameter("filtroUffint"));
    String abilitazioneGare = new String(request.getParameter("abilitazioneGare"));

    //Il contenuto del codice a barre è una stringa con una struttura del tipo:
    //  1|IMPR002|2|G0008||2| dove,
    //  1 : tipo richiesta
    //  IMPR002 : login della ditta richiedente
    //  2: genere della gara (1 - gara divisa in lotti con offerte distinte, 2 - gara a lotto unico, 3 - gara divisa in lotti con offerta unica)
    //  G0008 - codgar epurato del $
    //  eventuale codice del lotto (se genere = 1)
    //  2: flag per indicare se è RTI(1) oppure no(2)
    //  (modifica 15.05.12) il campo 7 è vuoto. Conteneva i primi 60 caratteri della ragione sociale del RT. E' stata tolta perchè
    //  l'intera ragione sociale viene letta direttamente dalla comunicazione FS9. In questo modo il codice a barre generato
    //  ha sempre la stessa dimensione.
    try {
    //proviamo ad utilizzare String invece che byte[]
    DatoBase64 base64 = new DatoBase64(datiacquisiti,DatoBase64.FORMATO_BASE64);
    ICriptazioneByte decriptatore = FactoryCriptazioneByte.getInstance(FactoryCriptazioneByte.CODICE_CRIPTAZIONE_ADVANCED,base64.getByteArrayDatoAscii(),ICriptazioneByte.FORMATO_DATO_CIFRATO);
//    ICriptazioneByte decriptatore = FactoryCriptazioneByte.getInstance(FactoryCriptazioneByte.CODICE_CRIPTAZIONE_ADVANCED,base64.getDatoAscii().getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
    datidecripted = new String(decriptatore.getDatoNonCifrato());
    ///cambiare il messaggio...
        } catch (CriptazioneException e) {
          logger.error("Errore nel formato del codice a barre letto");
          target = "codiceNOK";
          messageKey = "errors.barcodeNOK";
          this.aggiungiMessaggio(request, messageKey);
          request.setAttribute("barcode", datiacquisiti);
          return mapping.findForward(target);
        }


    String dati[] = datidecripted.split("\\|");
//    String dati[] = datiacquisiti.split("\\|");
    if(dati.length < 6){
      logger.error("Errore nel formato del codice a barre letto");
      target = "codiceNOK";
      messageKey = "errors.barcodeNOK";
      this.aggiungiMessaggio(request, messageKey);
      request.setAttribute("barcode", datiacquisiti);
    }else{
      String tipoMsg = dati[0];
      String impresa=dati[1];
      String genere=dati[2];
      String gara=dati[3];
      String lotto=dati[4]; //Valorizzato solo nel caso di un lotto di gara
      String tipoImpresa = dati[5];

      String nomeATI= "";
      String ngara=null;
      String tipscad=null;
      String dittao=null;
      String nomimoEstesa=null;
      String cfimp=null;
      String pivimp=null;
      Long tipimp = null;
      Double quotaMandataria =  null;

      if (tipoMsg==null || "".equals(tipoMsg)){
        logger.error("Nel codice a barre non è valorizzato il tipo di richiesta");
        target = "codiceNOK";
        messageKey = "errors.barcode.tipoMessaggioVuoto";
        this.aggiungiMessaggio(request, messageKey);
        request.setAttribute("barcode", datiacquisiti);
        return mapping.findForward(target);
      }


      //Tipologie di eventi: Partecipazione,Offerta,Articolo 48
      // S.Santi - 07062011 - Per identificare il tipo di timbro, il portale non utilizza più le codifiche FS6, FS7 e FS8 ma
      // le codifiche 1, 2 e 3. Le codifiche FSx riguardano le comunicazioni inviate dal portale a BKO, per cui
      // il loro utilizzo nei timbri sarebbe improprio.
      // Nel BKO si continuano a gestire anche le vecchie codifiche per retrocompatibilità.
      if ( !("FS6".equals(tipoMsg) | "1".equals(tipoMsg) |
              "FS7".equals(tipoMsg) | "2".equals(tipoMsg) |
                "FS8".equals(tipoMsg) |"3".equals(tipoMsg))){
        logger.error("Il tipo di richiesta nel codice a barre non è corretto");
        target = "codiceNOK";
        messageKey = "errors.barcode.tipoMessaggioNOK";
        this.aggiungiMessaggio(request, messageKey);
        request.setAttribute("barcode", datiacquisiti);
        return mapping.findForward(target);
      }

      if ("FS6".equals(tipoMsg) | "1".equals(tipoMsg)){
        tipscad="1";
      }
      if ("FS7".equals(tipoMsg) | "2".equals(tipoMsg)){
        tipscad="2";
      }
      if ("FS8".equals(tipoMsg) | "3".equals(tipoMsg)){
        tipscad="3";
      }

      if(impresa==null || "".equals(impresa)){
        logger.error("Nel codice a barre non è valorizzata la login dell'utente");
        target = "codiceNOK";
        messageKey = "errors.barcode.impresaVuota";
        this.aggiungiMessaggio(request, messageKey);
        request.setAttribute("barcode", datiacquisiti);
        return mapping.findForward(target);
      }

      if(gara==null || "".equals(gara)){
        logger.error("Nel codice a barre non è valorizzato il codice della gara");
        target = "codiceNOK";
        messageKey = "errors.barcode.garaVuota";
        this.aggiungiMessaggio(request, messageKey);
        request.setAttribute("barcode", datiacquisiti);
        return mapping.findForward(target);
      }

      if(tipoImpresa==null || "".equals(tipoImpresa)){
        logger.error("Nel codice a barre non è valorizzato il tipo dell'impresa");
        target = "codiceNOK";
        messageKey = "errors.barcode.tipoImpresaVuoto";
        this.aggiungiMessaggio(request, messageKey);
        request.setAttribute("barcode", datiacquisiti);
        return mapping.findForward(target);
      }

      if(genere==null || "".equals(genere)){
        logger.error("Nel codice a barre non è valorizzato il genere della gara");
        target = "codiceNOK";
        messageKey = "errors.barcode.genereVuoto";
        this.aggiungiMessaggio(request, messageKey);
        request.setAttribute("barcode", datiacquisiti);
        return mapping.findForward(target);
      }

      if("1".equals(genere) && (lotto == null || "".equals(lotto))){
        logger.error("Nel codice a barre non è valorizzato il codice del lotto di gara");
        target = "codiceNOK";
        messageKey = "errors.barcode.lottoVuoto";
        this.aggiungiMessaggio(request, messageKey);
        request.setAttribute("barcode", datiacquisiti);
        return mapping.findForward(target);
      }

      //Verifica se configurata la codifica automatica per IMPR e TEIM
      if(!pgManager.getCodificaAutomaticaPerPortaleAttiva()) {
        logger.error("Non risulta configurata la codifica automatica per gli archivi generali");
        target = "codiceNOK";
        messageKey = "errors.barcode.noCodificaAutomatica";
        this.aggiungiMessaggio(request, messageKey);
        request.setAttribute("barcode", datiacquisiti);
        return mapping.findForward(target);
      }

      if("2".equals(genere)){
        //lotto unico
        ngara = gara;
        gara="$"+gara;
      }else if("3".equals(genere)){
        ngara = gara;
      }else{
        //1=gara a lotti
        ngara = lotto;
      }

      if(target == null){
        //Si deve controllare che la gara a cui si riferisce il codice a barre rispetti le condizioni
        //presenti nella lista delle gare: v_gare_torn-lista.jsp
        String selectGara="select codice from v_gare_torn where codgar='" + gara + "'";
        if(filtroProfiloAttivo!=null && !"".equals(filtroProfiloAttivo))
          selectGara += " and profiloweb = " + filtroProfiloAttivo;
        if(filtroTipoGara!=null && !"".equals(filtroTipoGara))
          selectGara += " and " + filtroTipoGara;
        if(filtroLivelloUtente!=null && !"".equals(filtroLivelloUtente))
          selectGara+=" and " + filtroLivelloUtente;
        if(!"true".equals(visualizzazioneGareALotti))
          selectGara+=" and genere <> 1";
        if(!"true".equals(visualizzazioneGareLottiOffUnica))
          selectGara+=" and genere <> 3";
        if(filtroUffint!=null && !"".equals(filtroUffint)){
          selectGara+=" and CENINT = '" + filtroUffint + "')";
        }
        String codiceGaraTrovata="";
        try {
          codiceGaraTrovata = (String)this.sqlManager.getObject(selectGara, null);
        } catch (SQLException e) {
          logger.error("Errore nella lettura di V_GARE_TORN", e);
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.database.dataAccessException";
          this.aggiungiMessaggio(request, messageKey);
          return mapping.findForward(target);
        }

        if(codiceGaraTrovata==null || "".equals(codiceGaraTrovata)){
          logger.error("Non è possibile procedere. La gara a cui è riferito il codice a barre non è disponibile oppure non si hanno i privilegi di accesso");
          target = "codiceNOK";
          messageKey = "errors.barcode.garaNonAccessibile";
          this.aggiungiMessaggio(request, messageKey);
          request.setAttribute("barcode", datiacquisiti);
          return mapping.findForward(target);
        }else{
          try {
            ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
                CostantiGenerali.PROFILO_UTENTE_SESSIONE);
            Long idUtente = new Long(profilo.getId());
            Long autori = (Long) this.sqlManager.getObject(
                "select autori from g_permessi where codgar = ? and syscon = ?", new Object[]{gara,idUtente});
            Long stepgar = (Long)this.sqlManager.getObject(
                "select stepgar from gare where ngara = ?", new Object[]{ngara});
            boolean bloccoAggiudicazione = false;
            if(stepgar!=null && stepgar.intValue() >= GestioneFasiGaraFunction.FASE_CALCOLO_AGGIUDICAZIONE)
              bloccoAggiudicazione = true;
            if (!"A".equals(abilitazioneGare) && (autori == null  || autori.longValue()==2)){
              logger.error("Non è possibile procedere. Non si hanno i privilegi di modifica per la gara a cui è riferito il codice a barre(cod. gara " + codiceGaraTrovata + ")");
              target = "codiceNOK";
              messageKey = "errors.barcode.garaUtenteSenzaDirittiModifica";
              //this.aggiungiMessaggio(request, messageKey);
              this.aggiungiMessaggio(request, messageKey, codiceGaraTrovata);
              request.setAttribute("barcode", datiacquisiti);
              return mapping.findForward(target);
            }else if (bloccoAggiudicazione){
              logger.error("Non è possibile procedere. La gara a cui è riferito il codice a barre è in fase di aggiudicazione o conclusa(cod. gara " + codiceGaraTrovata + ")");
              target = "codiceNOK";
              messageKey = "errors.barcode.garaAggiudicazione";
              this.aggiungiMessaggio(request, messageKey, codiceGaraTrovata);
              request.setAttribute("barcode", datiacquisiti);
              return mapping.findForward(target);
            }
          } catch (SQLException e) {
            logger.error("Errore nella lettura della g_permessi per la gara" + ngara, e);
            target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
            messageKey = "errors.database.dataAccessException";
            this.aggiungiMessaggio(request, messageKey);
            return mapping.findForward(target);
          }
        }
      }




      if(target == null){
        target="success";

        try {
          //Recupera la ragione sociale della ditta corrispondente alla login
          String selectW_PUSER = "select userkey1,cfimp,pivimp,nomest,tipimp from w_puser,impr where usernome = ? " +
          "and userkey1=codimp";
           List datiW_PUSER = this.sqlManager.getListVector(selectW_PUSER,new Object[] { impresa });
           if (datiW_PUSER != null && datiW_PUSER.size() > 0) {
             for (int i = 0; i < datiW_PUSER.size(); i++) {
                 dittao = SqlManager.getValueFromVectorParam(datiW_PUSER.get(i), 0).toString();
                 cfimp = SqlManager.getValueFromVectorParam(datiW_PUSER.get(i), 1).toString();
                 pivimp = SqlManager.getValueFromVectorParam(datiW_PUSER.get(i), 2).toString();
                 nomimoEstesa = SqlManager.getValueFromVectorParam(datiW_PUSER.get(i), 3).toString();
                 tipimp = SqlManager.getValueFromVectorParam(datiW_PUSER.get(i), 4).longValue();
              }
           }

         if("1".equals(tipoImpresa)){
           //Si deve prelevare la descrizione dell'ATI dal messaggio FS9 (considera l'ultima occ. inserita indipendentemente dallo stato)

           if(tipimp!= null && tipimp.longValue()<=5)
             tipimp=new Long(3);
           else if(tipimp!= null && tipimp.longValue()>5)
             tipimp=new Long(10);


           String sql_W=null;
           String idprg="PA";
           String comtipo="FS9";
           sql_W="select IDCOM,COMKEY1 from w_invcom where idprg = ? and comtipo = ? and comkey1 = ? and comkey2= ? order by IDCOM desc";

           Vector DatiIDCOM= null;
           try {
             String comkey2=ngara;
             if ("1".equals(genere))
               comkey2=gara;
             DatiIDCOM = sqlManager.getVector(sql_W,
                 new Object[] { idprg, comtipo,impresa, comkey2});
           } catch (SQLException e) {
             messageLog = ": errore nella lettura della tabella W_INVCOM";
           }
           if (DatiIDCOM != null && DatiIDCOM.size() > 0) {

             Long idcom = sqlManager.getValueFromVectorParam(DatiIDCOM, 0).longValue();
             //Vengono letti i documenti associati ad ogni occorrenza di di W_INVCOM
             String select="select idprg,iddocdig from w_docdig where DIGENT = ? and digkey1 = ? and idprg = ? and dignomdoc = ? ";
             String digent="W_INVCOM";
             String idprgW_DOCDIG="PA";
             Vector datiW_DOCDIG = null;
             try {
               datiW_DOCDIG = sqlManager.getVector(select,
                    new Object[]{digent, idcom.toString(), idprgW_DOCDIG, "dati_partrti.xml"});

             } catch (SQLException e) {
               messageLog = ": errore nella lettura della tabella W_DOCDIG";
             }
             String idprgW_INVCOM = null;
             Long iddocdig = null;
             if(datiW_DOCDIG != null ){
               if(((JdbcParametro)datiW_DOCDIG.get(0)).getValue() != null)
                 idprgW_INVCOM = ((JdbcParametro) datiW_DOCDIG.get(0)).getStringValue();

               if(((JdbcParametro)datiW_DOCDIG.get(1)).getValue() != null)
               iddocdig = ((JdbcParametro) datiW_DOCDIG.get(1)).longValue();

               BlobFile fileAllegato = null;
               FileAllegatoManager fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager",
                   request.getSession().getServletContext(), FileAllegatoManager.class);

               try {
                 fileAllegato = fileAllegatoManager.getFileAllegato(idprgW_INVCOM,iddocdig);
               } catch (Exception e) {
                 logger.error("Errore nella lettura del file allegato presente nella tabella W_DOCDIG", e);
               }
               String xml=null;

               if(fileAllegato!=null && fileAllegato.getStream()!=null){
                 xml = new String(fileAllegato.getStream());
                 TipoPartecipazioneDocument document;
                 try {
                   //Lettura della ragione sociale estesa della ditta
                   document = TipoPartecipazioneDocument.Factory.parse(xml);
                   nomeATI = document.getTipoPartecipazione().getDenominazioneRti();
                   if(document.getTipoPartecipazione().isSetQuotaMandataria())
                     quotaMandataria = new Double(document.getTipoPartecipazione().getQuotaMandataria());
                 } catch (XmlException e) {
                   logger.error("Errore nella lettura del file xml", e);
                 }
               }else{
                 logger.error("Non è stato possibile leggere il file xml associato alla comunicazione", null);
               }
             }else{
               logger.error("Non vi sono file associati alla comunicazione", null);

             }
           }

            //Recupera il codice dell'eventuale ATI in gara con uguale ragione sociale e con mandataria la ditta richiedente
            // per fare il controllo sul numero protocollo
            selcodATI="select codimp from impr,ragimp,edit where impr.codimp=edit.codime and impr.codimp=ragimp.codime9 and " +
            		" edit.codgar4=? and ragimp.coddic=? and ragimp.impman='1' and upper(impr.nomest)=?";
            Object o = sqlManager.getObject(selcodATI, new Object[]{gara,dittao,nomeATI.toUpperCase()});
            if(o != null){
              String codATI =null;
              if (o instanceof String) {
                String codATItmp = (String) o;
                codATI = String.valueOf(codATItmp);
                codimp = codATI;
              }
            }

          } else{
            //Ditta non ATI
            codimp = dittao;
            tipimp = new Long(0);

          }

         //Verifica se si è nel profilo Protocollo
         Boolean isProfiloProtocollo = new Boolean(false);
         if (this.geneManager.getProfili().checkProtec((String) request.getSession().getAttribute(
             CostantiGenerali.PROFILO_ATTIVO), "FUNZ", "VIS", "ALT.GARE.V_GARE_NSCAD-lista.ApriGare"))
           isProfiloProtocollo=new Boolean(true);

         if (isProfiloProtocollo.booleanValue()){
            //Controllo che il numero protocollo non sia già valorizzato
            if (codimp!=null){
              String tipscadDesc="";
              selbarcount="select count(*) from ditg where ngara5=? and dittao=?";
              switch (Integer.parseInt(tipscad)) {
              case 1:
                selbarcode="select nprdom from ditg where ngara5=? and dittao=?";
                tipscadDesc="della domanda di partecipazione";
                break;
              case 2:
                selbarcode="select nproff from ditg where ngara5=? and dittao=?";
                tipscadDesc="dell'offerta";
                break;
              case 3:
                selbarcode="select nprreq from ditg where ngara5=? and dittao=?";
                tipscadDesc="della documentazione per comprova requisiti";
                break;
              }
              try {
                  Object o = sqlManager.getObject(selbarcount, new Object[]{ngara,codimp});
                  String count = null;
                  if(o !=null){
                   if (o instanceof Long) {
                     Long counttmp = (Long) o;
                     count = String.valueOf(counttmp.intValue());
                   } else if (o instanceof Double) {
                     Double counttmp = (Double) o;
                     if (counttmp != null) {
                       count = String.valueOf(counttmp.intValue());
                     }
                   }
                  }
                  if ("1".equals(count)) {
                    o = sqlManager.getObject(selbarcode, new Object[]{ngara,codimp});
                    //correggere qui
                    if(o != null){
                      String protocollo =null;
                      if (o instanceof String) {
                        String protocollotmp = (String) o;
                        protocollo = String.valueOf(protocollotmp);
                      }
                      //Ricava la descrizione breve per riportarla nel msg di errore
                      if (protocollo != null) {
                        String nomimp="";
                        if ("1".equals(tipoImpresa))
                            nomimp=nomeATI;
                        else
                          nomimp=nomimoEstesa;
                        if (nomimp.length()>61)
                          nomimp=nomimp.substring(0,60);
                        logger.error("Il numero protocollo "+tipscadDesc +" della ditta '"+nomimp+"' della gara " + ngara + " risulta già assegnato");
                        target = "codiceNOK";
                        messageKey = "errors.esisteNumeroProtocollo";
                        this.aggiungiMessaggio(request, messageKey, tipscadDesc, nomimp, ngara);
                      }
                    }
                  }
              } catch (SQLException e) {
                messageLog = ": errore nella lettura del numero protocollo";
              }
            }
          }

          request.setAttribute("tipscad", tipscad);
          request.setAttribute("impresa", impresa);
          request.setAttribute("gara", gara);
          request.setAttribute("lotto", lotto);
          request.setAttribute("genere", genere);
          request.setAttribute("ngara", ngara);
          request.setAttribute("dittao", dittao);

          request.setAttribute("nomimoEstesa", nomimoEstesa);
          request.setAttribute("cfimp", cfimp);
          request.setAttribute("pivimp", pivimp);
          request.setAttribute("tipoRTI", tipoImpresa);
          request.setAttribute("nomeATI", nomeATI);

          request.setAttribute("isProfiloProtocollo", isProfiloProtocollo);

          request.setAttribute("tipimpNewRTI", tipimp);
          request.setAttribute("quotaMandataria",quotaMandataria);

        } catch (SQLException e) {
          logger.error("Errore nella selezione dei dati relativi alla lettura del codice a barre" + messageLog, e);
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.database.dataAccessException";
          this.aggiungiMessaggio(request, messageKey);
        } catch (GestoreException e) {
          logger.error("Errore nella selezione dei dati relativi alla lettura del codice a barre", e);
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.applicazione.inaspettataException";
          this.aggiungiMessaggio(request, messageKey);
        }
      }
    }

    if(logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    if(target != null)
      return mapping.findForward(target);
    else
      return null;
  }

}

