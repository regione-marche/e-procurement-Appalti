/*
 * Created on 25/03/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per l'acquisizione di un singolo aggiornamento FS2 o FS4 proveniente
 * da Portale ALice
 *
 * @author Marcello Caminiti
 */
public class GestorePopupAcquisizionePuntualeDaPortale extends AbstractGestoreEntita {

  /** Logger */
  static Logger            logger           = Logger.getLogger(GestorePopupAcquisizionePuntualeDaPortale.class);

  static String     nomeFileXML_Iscrizione = "dati_iscele.xml";
  static String     nomeFileXML_Aggiornamento = "dati_aggisc.xml";

  @Override
  public String getEntita() {
    return "IMPR";
  }

  public GestorePopupAcquisizionePuntualeDaPortale() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupAcquisizionePuntualeDaPortale(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer
      dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {

    //variabili per tracciatura eventi
    int livEvento = 1;
    String codEvento = "";
    String oggEvento = "";
    String descrEvento = "";
    String errMsgEvento = "";

    FileAllegatoManager fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager",
        this.getServletContext(), FileAllegatoManager.class);

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);

    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);

    String idocmString = UtilityStruts.getParametroString(this.getRequest(),"idcom");
    Long idcom =  new Long(idocmString);
    String user = UtilityStruts.getParametroString(this.getRequest(),"comkey1");
    String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String tipo = UtilityStruts.getParametroString(this.getRequest(),"tipo");
    String messaggioCategorie =  UtilityStruts.getParametroString(this.getRequest(),"messagggioCategorie");
    String saltareAggCateg = UtilityStruts.getParametroString(this.getRequest(),"saltareAggCateg");
    boolean aggiornamentoCategorie = (!"true".equals(saltareAggCateg));
    String codiceDittaUser=null;

    String profiloAttivo = (String) this.getRequest().getSession().getAttribute("profiloAttivo");

    if("FS2".equals(tipo)){
      codEvento = "GA_ACQUISIZIONE_ISCRIZIONE";
      if("PG_GARE_ELEDITTE".equalsIgnoreCase(profiloAttivo))
        descrEvento = "Acquisizione iscrizione a elenco operatori da portale Appalti";
      else
        descrEvento = "Acquisizione iscrizione a catalogo da portale Appalti";
    }else{
      codEvento = "GA_ACQUISIZIONE_INTEGRAZIONE";
      if("PG_GARE_ELEDITTE".equalsIgnoreCase(profiloAttivo))
        descrEvento = "Acquisizione integrazione dati/documenti a iscrizione elenco operatori da portale Appalti";
      else
        descrEvento = "Acquisizione integrazione dati/documenti a iscrizione catalogo da portale Appalti";
    }
    oggEvento = ngara;

    String select=null;
    boolean errori=false;

    try{
      select="select USERKEY1 from w_puser where USERNOME = ? ";
      String codiceDitta;
      try {
        codiceDitta = (String)sqlManager.getObject(select, new Object[]{user});
      } catch (SQLException e) {
        this.getRequest().setAttribute("erroreAcquisizione", "1");
        throw new GestoreException("Errore nella lettura della tabella W_PUSER ",null, e);
      }

      //Nel caso di FS4 se il richiedente fa parte di una RTI, allora l'aggiornamento deve avvenire
      //sulla RTI
      if("FS4".equals(tipo)){
        //Nella finestra popIscriviAggiornaDaPortale.jsp all'apertura si blocca se non si trova la ditta in gara
        //quindi in questa fase sono sicuro di trovare la ditta.
        codiceDittaUser = codiceDitta;
        String datiControllo[] = pgManager.controlloEsistenzaDittaElencoGara(codiceDitta, ngara, "$" + ngara, null);
        codiceDitta =datiControllo[1];
      }

      descrEvento += "(cod. ditta " + codiceDitta + ")";
      //Vengono letti i documenti associati ad ogni occorrenza di di W_INVCOM
      select="select idprg,iddocdig from w_docdig where DIGENT = ? and digkey1 = ? and idprg = ? and dignomdoc = ? ";
      String digent="W_INVCOM";
      String idprgW_DOCDIG="PA";

      String nomeFile=null;
      if("FS2".equals(tipo))
        nomeFile = nomeFileXML_Iscrizione;
      else
        nomeFile= nomeFileXML_Aggiornamento;

      Vector datiW_DOCDIG = null;
      try {

          datiW_DOCDIG = sqlManager.getVector(select,
              new Object[]{digent, idcom.toString(), idprgW_DOCDIG, nomeFile});

      } catch (SQLException e) {
        this.getRequest().setAttribute("erroreAcquisizione", "1");
        throw new GestoreException("Errore nella lettura della tabella W_DOCDIG ",null, e);
      }
      String idprgW_INVCOM = null;
      Long iddocdig = null;
      GestorePopupAcquisisciDaPortale gacqport = new GestorePopupAcquisisciDaPortale();
      gacqport.setRequest(this.getRequest());
      if(datiW_DOCDIG != null ){
        if(((JdbcParametro)datiW_DOCDIG.get(0)).getValue() != null)
          idprgW_INVCOM = ((JdbcParametro) datiW_DOCDIG.get(0)).getStringValue();

        if(((JdbcParametro)datiW_DOCDIG.get(1)).getValue() != null)
        try {
          iddocdig = ((JdbcParametro) datiW_DOCDIG.get(1)).longValue();
        } catch (GestoreException e2) {
          this.getRequest().setAttribute("erroreAcquisizione", "1");
          throw new GestoreException("Errore nella lettura della tabella W_DOCDIG ",null, e2);
        }


        //Lettura del file xml immagazzinato nella tabella W_DOCDIG
        BlobFile fileAllegato = null;
        try {
          fileAllegato = fileAllegatoManager.getFileAllegato(idprgW_INVCOM,iddocdig);
        } catch (Exception e) {
          this.getRequest().setAttribute("erroreAcquisizione", "1");
          throw new GestoreException("Errore nella lettura del file allegato presente nella tabella W_DOCDIG",null, e);
        }
        String xml=null;
        if(fileAllegato!=null && fileAllegato.getStream()!=null){
          xml = new String(fileAllegato.getStream());

          if("FS2".equals(tipo))
            gacqport.iscrizioneElencoOperatori(ngara, codiceDitta, status, xml, pgManager, idcom, user, fileAllegatoManager, false);
          else
            gacqport.aggiornamentoIscrizioneElencoOperatori(ngara, codiceDitta, codiceDittaUser, status, xml, pgManager, idcom, user, fileAllegatoManager, false, aggiornamentoCategorie);

        }else{
          //Aggiornamento dello stato a errore
          gacqport.aggiornaStatoW_INVOCM(idcom,"7");
          errori=true;
        }
      }else{
      //Aggiornamento dello stato a errore
        gacqport.aggiornaStatoW_INVOCM(idcom,"7");
        errori=true;
      }

      if("FS4".equals(tipo) && aggiornamentoCategorie== false && !errori && messaggioCategorie!=null && !"".equals(messaggioCategorie)){
        //Si deve popolare la tabella GARACQUISIZ
        String insert = "insert into GARACQUISIZ(id,ngara,codimp,idprg,idcom,stato,logmsg) values(?,?,?,?,?,?,?)";
        int id = genChiaviManager.getNextId("GARACQUISIZ");
        try {
          sqlManager.update(insert, new Object[]{new Long(id),ngara,codiceDitta,idprgW_DOCDIG,idcom,new Long(1),messaggioCategorie});
          descrEvento = descrEvento.replace("(cod. ditta", ", con elaborazione delle categorie posticipata (cod. ditta");
        } catch (SQLException e) {
          this.getRequest().setAttribute("erroreAcquisizione", "1");
          throw new GestoreException("Errore nella scrittura sulla tabella GARACQUISIZ",null, e);
        }
      }

      if(errori)
        this.getRequest().setAttribute("erroreAcquisizione", "1");
      else
        this.getRequest().setAttribute("acquisizioneEseguita", "1");
    }catch(GestoreException e){
      livEvento = 3;
      errMsgEvento = e.getMessage();
      throw e;
    }finally {
      //Tracciatura eventi
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(oggEvento);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descrEvento);
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        String messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
      }
    }
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer dataColumnContainer)
      throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }




  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {


  }

}