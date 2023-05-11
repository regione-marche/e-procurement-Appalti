/*
 * Created on 31/08/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.TransactionStatus;

import com.lowagie.text.DocumentException;

import intra.regionemarche.ResultClass;
import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.utils.MailUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ControlliOepvManager;
import it.eldasoft.sil.pg.bl.GestioneATCManager;
import it.eldasoft.sil.pg.bl.GestioneRegioneMarcheManager;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.cifrabuste.CifraturaBusteManager;
import it.eldasoft.sil.pg.db.domain.CostantiAppalti;
import it.eldasoft.utils.mail.IMailSender;
import it.eldasoft.utils.mail.MailSenderException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.conf.WSDMConfigurazioneOutType;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloResType;
import it.maggioli.eldasoft.ws.dm.WSDMInviaMailType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAnagraficaType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoInType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire l'inoltro
 * per approvazione
 *
 * @author Cristian Febas
 */
public class GestoreInoltraPerApprovazione extends
    AbstractGestoreEntita {

  static Logger               logger         = Logger.getLogger(GestoreInoltraPerApprovazione.class);
  
  private SqlManager  sqllManager;
  
  private MailManager mailManager;
  
  private GenChiaviManager genChiaviManager;

  
  public GestoreInoltraPerApprovazione() {
    super(false);
  }
  
  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
            this.getServletContext(), SqlManager.class);
    mailManager = (MailManager) UtilitySpring.getBean("mailManager",
            this.getServletContext(), MailManager.class);
    genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
            this.getServletContext(), GenChiaviManager.class);

    
  }

  @Override
  public String getEntita() {
    return "G1STIPULA";
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
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
	  
  	Long sysconAssegnatario = datiForm.getLong("USRSYS.SYSCON");
  	Long idStipula = datiForm.getLong("ID");
  	Long statoStipula = datiForm.getLong("STATO");
  	Long sysconMittente = datiForm.getLong("CONMITT");
  	

    try {

        String oggettoMail = datiForm.getString("COMMSGOGG");    //oggetto
        String testoMail = datiForm.getString("COMMSGTES");    //testo
        testoMail="Gentile utente,\n"+testoMail;
	  	String indirizzoMail = null;
    	
		
		//inserimento in g1iterstipula
		  Long nextIterId = Long.valueOf(this.genChiaviManager.getNextId("G1ITERSTIPULA"));
		  Date data_step = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
		  this.sqlManager.update("insert into g1iterstipula(id, idstipula, dataiter,da_utente,a_utente,titolo,testo )values(?,?,?,?,?,?,?)",
			        new Object[]{nextIterId, idStipula,data_step,sysconMittente,sysconAssegnatario,oggettoMail,testoMail});
		  
			Vector<?> datiAssegnatario = this.sqlManager.getVector("select email,emailpec from usrsys where syscon=?", new Object[]{sysconAssegnatario});
	        if(datiAssegnatario!=null && datiAssegnatario.size()>0){
	            String email = SqlManager.getValueFromVectorParam(datiAssegnatario, 0).stringValue();
	            String pec = SqlManager.getValueFromVectorParam(datiAssegnatario, 1).stringValue();
	            indirizzoMail = email;
	        }
	    	
	        oggettoMail = datiForm.getString("COMMSGOGG");    //oggetto
	        testoMail = datiForm.getString("COMMSGTES");    //testo
	        testoMail="Gentile utente,\n"+testoMail;
			IMailSender mailSender = MailUtils.getInstance(mailManager, ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE),CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD);
		  
		  
			this.sqlManager.update("update g1stipula set assegnatario=? where id=?",
				        new Object[]{sysconAssegnatario, idStipula});
			
			
			String codStipula = (String) this.sqlManager.getObject("select codstipula from g1stipula where id = ? ", new Object[]{idStipula});
			
			Vector<?> datiPermessi = this.sqlManager.getVector(
		              "select numper,autori, propri from g_permessi where idstipula = ? and syscon = ?",
		              new Object[] { idStipula, sysconAssegnatario });
			if(datiPermessi!=null && datiPermessi.size()>0){
				Long numper = SqlManager.getValueFromVectorParam(datiPermessi, 0).longValue();
				Long autori = SqlManager.getValueFromVectorParam(datiPermessi, 1).longValue();
				String propri = SqlManager.getValueFromVectorParam(datiPermessi, 2).stringValue();
				if(Long.valueOf(2).equals(autori)) {
		            String sqlUpdate = "update g_permessi set autori = ? where numper= ?";
		            this.sqlManager.update(sqlUpdate, new Object[] { Long.valueOf(1), numper});
				}
			}else {
	          long maxNumper = this.getMaxIdGPermessi() + 1;
	            String sqlInsert = "insert into g_permessi (numper, syscon, autori, propri, idstipula) values (?, ?, ?, ?, ?)";
	            this.sqlManager.update(sqlInsert, new Object[] { Long.valueOf(maxNumper), sysconAssegnatario, Long.valueOf(1), Long.valueOf(2), idStipula });
			}
  
			//aggiorno lo stato della stipula se lo stato originario non e' ancora pubblicato 
	          if(statoStipula<Long.valueOf(3)) {
	        	 this.sqlManager.update("update g1stipula set stato = ?  where id =?", new Object[] { Long.valueOf(2), idStipula});
	          }

	        	  
			
			this.getRequest().setAttribute("InoltroPerApprovazioneEseguito", "1");
			
			mailSender.send(indirizzoMail, oggettoMail, testoMail);

		
	} catch (SQLException e) {
		throw new GestoreException("Errore nella lettura dei dati dell'assegnatario", null, e);
	} catch (MailSenderException ms) {
	      String logMessageKey = ms.getChiaveResourceBundle();
	      String logMessageError = resBundleGenerale.getString(logMessageKey);
	      for (int i = 0; ms.getParametri() != null && i < ms.getParametri().length; i++)
	        logMessageError = logMessageError.replaceAll(
	            UtilityStringhe.getPatternParametroMessageBundle(i),
	            (String) ms.getParametri()[i]);
	      String messaggio = "ATTENZIONE: si sono riscontrati problemi nell'invio mail.\n"
	      		+ "L'inoltro al nuovo Contract Manager è stato comunque completato.";
	      this.getRequest().setAttribute("msg", messaggio);
	      this.getRequest().setAttribute("InoltroPerApprovazioneEseguito", "-1");
	      
	      logger.error(logMessageError, ms);
	}

  	
  	
  	
       
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  /**
   * Ritorna l'ultimo id generato per la tabella G_PERMESSI
   *
   * @return ultimo id generato, 0 altrimenti
   * @throws GestoreException
   */
  private long getMaxIdGPermessi() throws GestoreException {
    long id = 0;
    try {
      Vector ret = this.sqlManager.getVector(
          "select max(numper) from g_permessi", new Object[] {});
      if (ret.size() > 0) {
        Long count = SqlManager.getValueFromVectorParam(ret, 0).longValue();
        if (count != null && count.longValue() > 0) {
          id = count.longValue();
        }
      }
    } catch (SQLException e) {
      throw new GestoreException(
          "Errore nell'estrazione dell'ultimo id utilizzato nella G_PERMESSI",
          "getMaxIdPermessi", e);
    }
    return id;
  }
  
}
