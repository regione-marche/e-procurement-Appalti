package it.eldasoft.sil.pg.tags.gestori.submit;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.bl.ValidatorManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;

public class GestoreW_INVCOM extends AbstractGestoreChiaveNumerica {

  /** Logger */
  static Logger logger = Logger.getLogger(GestoreW_INVCOM.class);

  GestioneWSDMManager gestioneWSDMManager = null;

  @Override
  public String[] getAltriCampiChiave() {
    return new String[] { "IDPRG" };
  }

  @Override
  public String getCampoNumericoChiave() {
    return "IDCOM";
  }

  @Override
  public String getEntita() {
    return "W_INVCOM";
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("GestoreW_INVCOM: preDelete: inizio metodo");

    GeneManager geneManager = this.getGeneManager();
    String idprg = datiForm.getString("W_INVCOM.IDPRG");
    Long idcom = datiForm.getLong("W_INVCOM.IDCOM");

    // Cancellazione della lista dei destinatari
    geneManager.deleteTabelle(new String[] { "W_INVCOMDES" },
        "IDPRG = ? AND IDCOM = ?", new Object[] { idprg, idcom });

    // Cancellazione degli oggetti associati alla comunicazione
    geneManager.deleteTabelle(new String[] { "W_DOCDIG" },
        "IDPRG = ? AND DIGENT = ? AND DIGKEY1 = ? AND DIGKEY2 = ?",
        new Object[] { idprg, "W_INVCOM", idprg, idcom.toString() });

    if (logger.isDebugEnabled())
      logger.debug("GestoreW_INVCOM: preDelete: fine metodo");

  }

  @SuppressWarnings("unchecked")
  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    if (logger.isDebugEnabled())
      logger.debug("GestoreW_INVCOM: preInsert: inizio metodo");

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);

    PgManagerEst1 pgManagerest1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        this.getServletContext(), PgManagerEst1.class);

    gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        this.getServletContext(), GestioneWSDMManager.class);

    // Operatore (utente di USRSYS che ha avuto accesso all'applicativo)
    ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    datiForm.setValue("W_INVCOM.COMCODOPE", new Long(profilo.getId()));

    // Data di inserimento
    datiForm.setValue("W_INVCOM.COMDATINS", new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()));

    String COMMSGTES = datiForm.getString("W_INVCOM.COMMSGTES");
    String COMMSGTIP = datiForm.getString("W_INVCOM.COMMSGTIP");
    if(COMMSGTES!=null && "1".equals(COMMSGTIP)){
      COMMSGTES=COMMSGTES.replaceAll("<br>", "<br\\/>");
      datiForm.setValue("W_INVCOM.COMMSGTES", COMMSGTES);
    }
    //Eventuale valorizzazione di IDCFG
    try {
      String cenint = (String)sqlManager.getObject("select t.cenint from gare g,torn t where g.codgar1 = t.codgar and ngara=?", new Object[]{datiForm.getString("W_INVCOM.COMKEY1")});
      cenint = UtilityStringhe.convertiNullInStringaVuota(cenint);
      if(!"".equals(cenint)){
        datiForm.setValue("W_INVCOM.IDCFG", cenint);
      }
    } catch (SQLException sqle) {
      throw new GestoreException("Errore nella lettura di TORN.CENINT",null, sqle);
    }

    Long commodello = datiForm.getLong("W_INVCOM.COMMODELLO");

    if (new Long(1).equals(commodello)) {
      String stepWizard = UtilityStruts.getParametroString(this.getRequest(), "stepWizard");
      if(stepWizard != null && !"".equals(stepWizard)) {
        Long busta = pgManagerest1.getValoreBusta(stepWizard);
        datiForm.setValue("W_INVCOM.COMTIPMA", busta);
      }
    }

    super.preInsert(status, datiForm);

    String ditta = UtilityStruts.getParametroString(this.getRequest(),"ditta");
    String tipo = this.getRequest().getParameter("tipo");
    String idconfi = this.getRequest().getParameter("idconfi");

    Long compub = datiForm.getLong("W_INVCOM.COMPUB");
    if(compub==null)
      compub= new Long(0);

    String idprg = datiForm.getString("W_INVCOM.IDPRG");
    Long idcom = datiForm.getLong("W_INVCOM.IDCOM");
    String coment = datiForm.getString("W_INVCOM.COMENT");
    String comkey1 = datiForm.getString("W_INVCOM.COMKEY1");
    String ngara=comkey1;
    String codgar=null;
    if("TORN".equals(coment) || "PERI".equals(coment) || "APPA".equals(coment)){
      //Gare ad offerta unica
      codgar=comkey1;
    }else if("NSO_ORDINI".equals(coment)){

      try {
      //si tratta di una comunicazione da inviare per gli Ordini integrati con NSO
        Vector<JdbcParametro> vectorList = sqlManager.getVector("SELECT g.codgar1,g.ngara,g.ditta FROM GARE g "
                                                    + "JOIN NSO_ORDINI n ON n.ngara = g.ngara AND n.id=?"
                                                    ,new Object[] {comkey1});

        codgar = vectorList.get(0).getStringValue();
        ngara = vectorList.get(1).getStringValue();
        ditta = vectorList.get(2).getStringValue();

        if(logger.isDebugEnabled())
          logger.debug("RE-SET ngara:"+ngara+" , codgar:"+codgar+" , ditta:"+ditta);
      } catch (SQLException e) {
        throw new GestoreException("Errore nella lettura di GARE.CODGAR1",null, e);
      }
    }else if("G1STIPULA".equals(coment)){
      //si tratta di una comunicazione da inviare per le stipule
      try {
        Vector<JdbcParametro> vectorList = sqlManager.getVector("SELECT codgar,ngara,codimp" +
                " FROM v_gare_stipula where codstipula=?",new Object[] {comkey1});

        codgar = vectorList.get(0).getStringValue();
        ngara = vectorList.get(1).getStringValue();
        ditta = vectorList.get(2).getStringValue();

        if(logger.isDebugEnabled())
          logger.debug("RE-SET ngara:"+ngara+" , codgar:"+codgar+" , ditta:"+ditta);
      } catch (SQLException e) {
        throw new GestoreException("Errore nella lettura di G1STIPULA.ID",null, e);
      }
    }else{
      try {
        codgar = (String)sqlManager.getObject("select codgar1 from gare where ngara=?", new Object[]{ngara});
      } catch (SQLException e) {
        throw new GestoreException("Errore nella lettura di GARE.CODGAR1",null, e);
      }
    }

    String integrazioneWSDM="0";

    try {
      boolean isIntegrazioneWSDMAttivaValida = gestioneWSDMManager.isIntegrazioneWSDMAttivaValida(GestioneWSDMManager.SERVIZIO_FASCICOLOPROTOCOLLO, idconfi);
      if(isIntegrazioneWSDMAttivaValida)
        integrazioneWSDM="1";
    }catch (SQLException e) {
      throw new GestoreException("Errore nella verifica dell'integrazione WSDM Protocollo per la gara " + codgar, null, e);
    }

    boolean delegaInvioMailDocumentaleAbilitata = false;
    String valoreWSDM = ConfigManager.getValore("pg.wsdm.invioMailPec."+idconfi);
    if(valoreWSDM!=null && "1".equals(valoreWSDM))
      delegaInvioMailDocumentaleAbilitata=true;

    String tipoWSDM=null;
    if("1".equals(integrazioneWSDM) && delegaInvioMailDocumentaleAbilitata){

      WSDMConfigurazioneOutType configurazione = gestioneWSDMManager.wsdmConfigurazioneLeggi("FASCICOLOPROTOCOLLO",idconfi);
      if (configurazione.isEsito())
        tipoWSDM = configurazione.getRemotewsdm();
    }

    boolean condizioneDelegaInvioMailDocumentale = false;
    if("1".equals(integrazioneWSDM) && delegaInvioMailDocumentaleAbilitata && ("PALEO".equals(tipoWSDM) || "JIRIDE".equals(tipoWSDM)
        || "ENGINEERING".equals(tipoWSDM) || "ENGINEERINGDOC".equals(tipoWSDM) || "TITULUS".equals(tipoWSDM) || "SMAT".equals(tipoWSDM))){
      condizioneDelegaInvioMailDocumentale = true;
    }


    if (datiForm.isColumn("W_INVCOMDES.DESCODSOG")) {
      // la comunicazione e' stata inserita da rispondi di una comunicazione ricevuta

      try {
        String codimp = datiForm.getColumn("W_INVCOMDES.DESCODSOG").getValue().stringValue();
        String nomimp = (String)sqlManager.getObject("select nomimp from impr where codimp=?", new Object[]{codimp});
        StringBuffer buf = new StringBuffer();
        // inserisco il mittente della mail ricevuta come destinatario della risposta
        boolean msgAvviso = inserisciSoggDest(idprg ,idcom ,codimp ,"" ,nomimp ,pgManager ,buf , condizioneDelegaInvioMailDocumentale);
        if (msgAvviso) {
          throw new GestoreException("Soggetto destinatario con codice " + codimp + " non contenente tutti i dati necessari per predisporre la comunicazione",null);
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'inserimento del soggetto destinatario ricavato dalla comunicazione a cui si risponde",null, e);
      }
    } else {
      //Se la comunicazione è stata inserita da modello si devo inserire anche dei soggetti destinatari
      String numModello = UtilityStruts.getParametroString(this.getRequest(), "numModello");
      if("7".equals(tipo) && (numModello==null || "".equals(numModello) || "0".equals(numModello))&& (ditta== null || "".equals(ditta))){
        try {
          ditta = (String)sqlManager.getObject("select ditta from gare where ngara = ?", new Object[]{ngara});
        } catch (SQLException e) {
          throw new GestoreException("Errore nell'inserimento dei soggetti destinatari",null, e);
        }
      }



      if(numModello!=null && !"".equals(numModello) && !"0".equals(numModello) && compub.longValue()!=1 && (ditta== null || "".equals(ditta))){
        try {
          Long numModelloLong = new Long(numModello);
          String filtroSog = (String)sqlManager.getObject("select filtrosog from w_confcom where numpro = ?", new Object[]{numModelloLong});

          String select="select dittao,nomimp,tipimp from ditg,impr where codgar5=? and ngara5=? and dittao = codimp and RTOFFERTA is null  ";
          if(filtroSog!=null && !"".equals(filtroSog))
            select+=" and " + filtroSog;

          List listaDittao = sqlManager.getListVector(select, new Object[]{codgar,ngara});
          if(listaDittao!=null && listaDittao.size()>0){
            StringBuffer buf = new StringBuffer("<br><ul>");
            boolean msgAvviso = false;
            boolean mandatariaPresente =true;
            for(int i=0;i<listaDittao.size();i++){
              String dittao = SqlManager.getValueFromVectorParam(listaDittao.get(i), 0).stringValue();
              String nomimp = SqlManager.getValueFromVectorParam(listaDittao.get(i), 1).stringValue();
              String tipologiaImpresa = SqlManager.getValueFromVectorParam(listaDittao.get(i), 2).getStringValue();
              tipologiaImpresa = UtilityStringhe.convertiNullInStringaVuota(tipologiaImpresa);
              if ("3".equals(tipologiaImpresa) || "10".equals(tipologiaImpresa)) {
               String selectComponenti= "select CODDIC, NOMDIC from RAGIMP,IMPR where CODIME9 = ? and CODDIC=CODIMP and IMPMAN='1'";
                List listaComponenti = sqlManager.getListVector(selectComponenti,new Object[]{ dittao });
                if (listaComponenti != null && listaComponenti.size() == 1){
                  for (int k = 0; k< listaComponenti.size(); k++) {
                    String codComponente = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 0).getStringValue();
                    String nomeComponente = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 1).getStringValue();
                    nomeComponente = nomimp+" - "+nomeComponente+" - Mandataria";
                    if(inserisciSoggDest(idprg ,idcom ,dittao ,codComponente, nomeComponente ,pgManager ,buf,  condizioneDelegaInvioMailDocumentale ))
                      msgAvviso = true;
                  }
                }else{
                  mandatariaPresente=false;
                  buf.append("<li style=\"list-style-type: disc;margin-left: 30px;\" >");
                  buf.append(dittao);
                  buf.append(" - ");
                  buf.append(nomimp);
                  buf.append("</li>");
                  msgAvviso = true;
                }
              }else{
                //per le ditte singole
                if(inserisciSoggDest(idprg ,idcom ,dittao ,"" ,nomimp ,pgManager ,buf, condizioneDelegaInvioMailDocumentale ))
                  msgAvviso = true;
              }
            }
            if(msgAvviso){
              buf.append("</ul>");
              // Aggiungo il messaggio al request
              String msg ="warnings.comunicazioni.indirizzoMailVuotoSenzaFax";
              if(condizioneDelegaInvioMailDocumentale)
                msg ="warnings.comunicazioni.indirizzoPECVuoto";
              if(!mandatariaPresente)
                msg+="ConRT";
              UtilityStruts.addMessage(this.getRequest(), "warning",
                  msg, new Object[] { buf.toString() });
            }

          }
        } catch (SQLException e) {
          throw new GestoreException("Errore nell'inserimento dei soggetti destinatari",null, e);
        }
      }else if(ditta!= null && !"".equals(ditta)){

        try {
          Long idcomdes = null;
          Vector<JdbcParametro> datiDitg = new Vector<JdbcParametro>();
          datiDitg = this.sqlManager.getVector("select tipimp,nomimp from ditg,impr where codgar5=? and ngara5=? and dittao=? and dittao = codimp", new Object[]{codgar,ngara,ditta});
          String tipologiaImpresa = datiDitg.get(0).getStringValue();
          String nomimp = datiDitg.get(1).getStringValue();
          String[] mailFax = new String[0];
          tipologiaImpresa = UtilityStringhe.convertiNullInStringaVuota(tipologiaImpresa);
          StringBuffer buf = new StringBuffer("<br><ul>");
          boolean msgAvviso=false;
          boolean mandatariaPresente=true;
          if ("3".equals(tipologiaImpresa) || "10".equals(tipologiaImpresa)) {

            String selectComponenti= "select CODDIC, NOMDIC from RAGIMP,IMPR where CODIME9 = ? and CODDIC=CODIMP and IMPMAN='1'";
            List listaComponenti = sqlManager.getListVector(selectComponenti,new Object[]{ ditta });
            if (listaComponenti != null && listaComponenti.size() == 1){
              for (int k = 0; k< listaComponenti.size(); k++) {
                String codComponente = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 0).getStringValue();
                String nomeComponente = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 1).getStringValue();

                nomeComponente = nomimp+" - "+nomeComponente+" - Mandataria";
                //per ogni ditta componente
                //StringBuffer buf = new StringBuffer("");
                if(inserisciSoggDest(idprg ,idcom ,ditta ,codComponente ,nomeComponente ,pgManager ,buf , condizioneDelegaInvioMailDocumentale ))
                msgAvviso = true;
              }
            }else{
              mandatariaPresente=false;
              buf.append("<li style=\"list-style-type: disc;margin-left: 30px;\" >");
              buf.append(ditta);
              buf.append(" - ");
              buf.append(nomimp);
              buf.append("</li>");
              msgAvviso = true;
            }
          }else{
            //per ogni ditta singola
            //StringBuffer buf = new StringBuffer("");
           if(inserisciSoggDest(idprg ,idcom ,ditta ,"" ,nomimp ,pgManager ,buf , condizioneDelegaInvioMailDocumentale ))
             msgAvviso = true;
          }

          if(msgAvviso){
            buf.append("</ul>");
            // Aggiungo il messaggio al request
            String msg ="warnings.comunicazioni.indirizzoMailVuotoSenzaFax";
            if(condizioneDelegaInvioMailDocumentale)
              msg ="warnings.comunicazioni.indirizzoPECVuoto";
            if(!mandatariaPresente)
              msg+="ConRT";
            UtilityStruts.addMessage(this.getRequest(), "warning",
                msg, new Object[] { buf.toString() });
          }

        } catch (SQLException e) {
          throw new GestoreException("Errore nell'inserimento dei soggetti destinatari",null, e);
        }
      }
    }



    if (logger.isDebugEnabled())
      logger.debug("GestoreW_INVCOM: preInsert: fine metodo");

  }

  private boolean inserisciSoggDest(String idprg, Long idcom,String dittao ,String codComponente, String nomimo ,PgManager pgManager ,
      StringBuffer buf , boolean condizioneDelegaInvioMailDocumentale) throws GestoreException, SQLException {


    boolean msgAvviso = false;
    String mailFax[] = new String[4];
    if("".equals(codComponente)){
      mailFax = pgManager.getMailFax(dittao);
    }else{
      mailFax = pgManager.getMailFax(codComponente);
    }

    String email = mailFax[0];
    String Pec = mailFax[1];
    String fax = mailFax[2];
    email = UtilityStringhe.convertiNullInStringaVuota(email);
    Pec = UtilityStringhe.convertiNullInStringaVuota(Pec);
    fax = UtilityStringhe.convertiNullInStringaVuota(fax);

    if (("".equals(email) && "".equals(Pec)) || (condizioneDelegaInvioMailDocumentale && "".equals(Pec))) {
      // La ditta non va inserita fra i soggetti destinatari
      // creare il messaggio da visualizzare a video
      buf.append("<li style=\"list-style-type: disc;margin-left: 30px;\" >");
      buf.append(dittao);
      buf.append(" - ");
      buf.append(nomimo);
      buf.append("</li>");
      msgAvviso = true;
    } else {
      Long idcomdes = (Long) sqlManager.getObject("select max(idcomdes) + 1 from w_invcomdes where idprg = ? and idcom = ?",
          new Object[] { idprg, idcom });
      if (idcomdes == null) idcomdes = new Long(1);
      String update = "insert into w_invcomdes (idprg, idcom, idcomdes, descodent,descodsog,desmail,desintest,comtipma)" +
      		" values(?,?,?,?,?,?,?,?) ";
      String desmail = email;
      Long comtipma = new Long(2);
      if (Pec != null && !"".equals(Pec)) {
        desmail = Pec;
        comtipma = new Long(1);
      }
      if (desmail == null || "".equals(desmail)) {
        desmail = fax;
        comtipma = new Long(3);
      }
      sqlManager.update(update, new Object[] {idprg, idcom, idcomdes, "IMPR", dittao, desmail, nomimo, comtipma });
    }
    return msgAvviso;
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    ValidatorManager validatorManager = (ValidatorManager) UtilitySpring.getBean("validatorManager",
              this.getServletContext(), ValidatorManager.class);

    if(datiForm.isColumn("W_INVCOM.COMMSGTES") && datiForm.isColumn("W_INVCOM.COMMSGTIP")){
      String COMMSGTES = datiForm.getString("W_INVCOM.COMMSGTES");
      String COMMSGTIP = datiForm.getString("W_INVCOM.COMMSGTIP");
      if(COMMSGTES!=null && "1".equals(COMMSGTIP)){
        COMMSGTES=COMMSGTES.replaceAll("<br>", "<br\\/>");
        datiForm.setValue("W_INVCOM.COMMSGTES", COMMSGTES);
      }
    }

    if(datiForm.isColumn("MAX_NUMORD")){
      Long maxNumord= datiForm.getLong("MAX_NUMORD");
      // Gestione delle sezioni documenti richiesti per soccorso istruttorio
      AbstractGestoreChiaveIDAutoincrementante gestoreG1DOCSOC = new DefaultGestoreEntitaChiaveIDAutoincrementante(
          "G1DOCSOC", "ID", this.getRequest());
      this.gestisciAggiornamentiG1DOCSOC(status, datiForm,
          gestoreG1DOCSOC, "DOCSOCISTR",null, null,maxNumord);
    }

    Long COMPUB = (Long)datiForm.getObject("W_INVCOM.COMPUB");
    if(datiForm.isModifiedColumn("W_INVCOM.COMPUB") && new Long (2).equals(COMPUB)) {

    	String entita = "W_INVCOM";
    	String chiave1 = datiForm.getString("W_INVCOM.IDPRG");
    	Long chiave2 = datiForm.getLong("W_INVCOM.IDCOM");

    	double dimMaxTotaleFileByte=0;
    	String dimMaxTotaleFileStringa= null;
    	long dimTotaleAllegati = 0;
    	String idcfg = datiForm.getString("W_INVCOM.IDCFG");
    	if(idcfg==null || "".equals(idcfg))
    		idcfg = CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD;
    	dimMaxTotaleFileStringa = validatorManager.getDimensioneMassimaFile(idcfg);

    	//Si deve determinare la dimensione massima dei file già allegati e di quello che si sta allegando
    	if(dimMaxTotaleFileStringa!= null && !"".equals(dimMaxTotaleFileStringa)){
    		dimMaxTotaleFileStringa = dimMaxTotaleFileStringa.trim();
    		dimMaxTotaleFileByte = Math.pow(2, 20) * Double.parseDouble(dimMaxTotaleFileStringa);


    		try {

    			List listaW_DOCDIG = sqlManager.getListVector("select IDDOCDIG,IDPRG  from W_DOCDIG where W_DOCDIG.DIGENT = ? AND W_DOCDIG.DIGKEY1 = ? AND W_DOCDIG.DIGKEY2 = ? "
    					, new Object[]{entita, chiave1, chiave2});
    			if(listaW_DOCDIG!=null && listaW_DOCDIG.size()>0){

    				FileAllegatoManager fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean(
    						"fileAllegatoManager", this.getServletContext(), FileAllegatoManager.class);

    				for(int i=0;i<listaW_DOCDIG.size();i++){
    					Long iddocdig = (Long)SqlManager.getValueFromVectorParam(listaW_DOCDIG.get(i), 0).getValue();
    					String idprg = (String)SqlManager.getValueFromVectorParam(listaW_DOCDIG.get(i), 1).getValue();
    					BlobFile fileAllegatoBlob = fileAllegatoManager.getFileAllegato(idprg, iddocdig);

    					if(fileAllegatoBlob!=null)
    						dimTotaleAllegati += fileAllegatoBlob.getStream().length;

    				}
    			}
    		} catch (SQLException e) {
    			throw new GestoreException("Errore nella lettura degli allegati della comunicazione(W_DOCDIG.DIGOGG)", null);
    		} catch (IOException e) {
    			throw new GestoreException("Errore nella lettura degli allegati della comunicazione(W_DOCDIG.DIGOGG)", null);
    		}
    	}


    	if(dimTotaleAllegati> dimMaxTotaleFileByte){  //Controllo sulla dimensione totale massima di tutti gli allegati
            throw new GestoreException("La dimensione totale dei file da allegare supera il limite consentito dal server di posta "
                + "di " + dimMaxTotaleFileStringa + " MB" , "upload.overflowMultiplo", new String[] { dimMaxTotaleFileStringa },null);
          }
    }

  }

  /**
   * Gestisce le operazioni di update, insert, delete dei dettagli dei record della
   * scheda multipla dei documenti.
   * Riprende il codice delle librerie generali, aggiungendo l'aggiornamento del campo
   * numord
   *
   *
   * @param status
   *        stato della transazione
   * @param dataColumnContainer
   *        container di partenza da cui filtrare i record
   * @param gestore
   *        gestore a chiave ID autoincrementante per l'aggiornamento del record di una
   *        scheda multipla
   * @param suffissoContaRecord
   *        suffisso da concatenare a "NUMERO_" per ottenere il campo che indica
   *        il numero di occorrenze presenti nel container
   * @param valoreChiave
   *        parte non numerica della chiave dell'entità, per la valorizzazione
   *        in fase di inserimento se i dati non sono presenti nel container
   * @param nomeCampoDelete
   *        campo utilizzato per marcare la delete di un record della scheda
   *        multipla
   * @param campiDaNonAggiornare
   *        elenco eventuale di ulteriori campi fittizi da eliminare prima di
   *        eseguire l'aggiornamento nel DB
   * @param maxNumord
   *        valore max del campo numord presente in db.
   *
   * @throws GestoreException
   */
  private void gestisciAggiornamentiG1DOCSOC(
      TransactionStatus status, DataColumnContainer dataColumnContainer,
      AbstractGestoreChiaveIDAutoincrementante gestore, String suffisso,
      DataColumn[] valoreChiave, String[] campiDaNonAggiornare, Long maxNumord)
      throws GestoreException {

    ////////////////////////////////////////////////////////////////
    // ATTENZIONE: METODO CON UTILIZZO ID MAX + 1 (TRADIZIONALE)!!!!
    ////////////////////////////////////////////////////////////////

    String nomeCampoNumeroRecord = "NUMERO_" + suffisso;
    String nomeCampoDelete = "DEL_" + suffisso;
    String nomeCampoMod = "MOD_" + suffisso;


    long newNumord = 1;
    if(maxNumord!=null)
      newNumord = new Long(maxNumord) + 1;

    // Gestione delle pubblicazioni bando solo se esiste la colonna con il
    // numero di occorrenze
    if (dataColumnContainer.isColumn(nomeCampoNumeroRecord)) {

      // Estraggo dal dataColumnContainer tutte le occorrenze dei campi
      // dell'entità definita per il gestore
      DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(
          dataColumnContainer.getColumns(gestore.getEntita(), 0));

      int numeroRecord = dataColumnContainer.getLong(nomeCampoNumeroRecord).intValue();

      // Sabbadin 07/12/2011: spostato fuori dal ciclo questo controllo in modo
      // da fare una volta sola la verifica e l'append dell'entita' (SE
      // NECESSARIA) al nome di campo da non aggiornare
      if (campiDaNonAggiornare != null) {
        for (int j = 0; j < campiDaNonAggiornare.length; j++)
          if (campiDaNonAggiornare[j].indexOf('.') == -1)
            campiDaNonAggiornare[j] = gestore.getEntita()
                + "."
                + campiDaNonAggiornare[j];
      }

      for (int i = 1; i <= numeroRecord; i++) {
        DataColumnContainer newDataColumnContainer = new DataColumnContainer(
            tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

        boolean deleteOccorrenza = newDataColumnContainer.isColumn(nomeCampoDelete)
            && "1".equals(newDataColumnContainer.getString(nomeCampoDelete));
        boolean updateOccorrenza = newDataColumnContainer.isColumn(nomeCampoMod)
            && "1".equals(newDataColumnContainer.getString(nomeCampoMod));

        // Rimozione dei campi fittizi (il campo per la marcatura della delete e
        // tutti gli eventuali campi passati come argomento)
        newDataColumnContainer.removeColumns(new String[] {
            gestore.getEntita() + "." + nomeCampoDelete,
            gestore.getEntita() + "." + nomeCampoMod});

        if (campiDaNonAggiornare != null) {
          newDataColumnContainer.removeColumns(campiDaNonAggiornare);
        }

        if (deleteOccorrenza) {
          // Se è stata richiesta l'eliminazione e il campo chiave numerica e'
          // diverso da null eseguo l'effettiva eliminazione del record
          if (newDataColumnContainer.getLong(gestore.getCampoNumericoChiave()) != null)
            gestore.elimina(status, newDataColumnContainer);
          // altrimenti e' stato eliminato un nuovo record non ancora inserito
          // ma predisposto nel form per l'inserimento
        } else {
          if (updateOccorrenza) {

            if (newDataColumnContainer.getLong(gestore.getCampoNumericoChiave()) == null){
              newDataColumnContainer.getColumn("G1DOCSOC.NUMORD").setValue(new JdbcParametro(JdbcParametro.TIPO_DECIMALE,new Long(newNumord)));
              gestore.inserisci(status, newDataColumnContainer);
              newNumord+=1 ;
            }

            else
              gestore.update(status, newDataColumnContainer);
          }

        }
      }
    }
  }

}
