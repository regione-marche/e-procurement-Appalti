/*
 * Created on 29/04/2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.bl.ValidatorManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

/**
 * Gestore che effettua il controllo della valorizzazione di alcuni
 * campi. Se anche uno non risulta valorizzato, allora si deve riportare
 * un messaggio opportuno alla finestra popipPubblicaSuPortale.jsp
 *
 * @author Cristian Febas
 */
public class GestoreInoltraPerApprovazione extends AbstractGestorePreload {

  SqlManager sqlManager = null;
  PgManager pgManager = null;
  TabellatiManager tabellatiManager = null;
  FileAllegatoManager fileAllegatoManager = null;
  PgManagerEst1 pgManagerEst1 = null;
  ValidatorManager validatorManager= null;

  public GestoreInoltraPerApprovazione(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }

  public void inizializzaManager(PageContext page){
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);

    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        page, PgManager.class);

    tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        page, TabellatiManager.class);

    fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean(
        "fileAllegatoManager", page.getServletContext(), FileAllegatoManager.class);

    pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1", page, PgManagerEst1.class);

    validatorManager = (ValidatorManager) UtilitySpring.getBean("validatorManager",
        page, ValidatorManager.class);

  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    this.inizializzaManager(page);

    // lettura dei parametri di input
    String idStipula = page.getRequest().getParameter("idStipula");
    String idconfi = page.getRequest().getParameter("idconfi");
    String select=null;
    Long genere = null;
    String chiave=null;
    String messaggio = "";
    String controlloSuperato="SI";
    String MsgConferma = "";
    Double importo = null;
  	String codiceStipula = null;
  	String oggetto = null;
  	Double impstipula = null;
  	String impStipula = null;
  	String nomest = null;
  	Long statoStipula = null;
  	String nome_creatore = null;
  	String nome_contract = null;
  	String ufficio = null;
    String profilo = (String) page.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);


		try {


	    	Vector<?> datiStipula = this.sqlManager.getVector("select codstipula,oggetto,impstipula,nomest,stato,cenint,nome_creatore,nome_contract from v_gare_stipula where id=?", new Object[]{idStipula});
	    	if(datiStipula!=null && datiStipula.size()>0){
	    		codiceStipula = SqlManager.getValueFromVectorParam(datiStipula, 0).stringValue();
	    		oggetto = SqlManager.getValueFromVectorParam(datiStipula, 1).stringValue();
	    		impstipula = SqlManager.getValueFromVectorParam(datiStipula, 2).doubleValue();
	    		impStipula = UtilityNumeri.convertiImporto(impstipula, 2) + " euro";
	    		nomest = SqlManager.getValueFromVectorParam(datiStipula, 3).stringValue();
	    		statoStipula = SqlManager.getValueFromVectorParam(datiStipula, 4).longValue();
	    		String cenint = SqlManager.getValueFromVectorParam(datiStipula, 5).stringValue();
	    		nome_creatore = SqlManager.getValueFromVectorParam(datiStipula, 6).stringValue();
	    		nome_contract = SqlManager.getValueFromVectorParam(datiStipula, 7).stringValue();
	    		if(cenint!= null && !"".equals(cenint)) {
	    		  ufficio = (String)this.sqlManager.getObject("select nomein from uffint where codein=?", new Object[] {cenint});
	    		}
	    	}

	    	if(ufficio==null)
	    	  ufficio="";
	    	if(nome_creatore==null)
	    	  nome_creatore="";
	    	if(nome_contract==null)
	    	  nome_contract="";

			Long assegnatario = (Long)sqlManager.getObject("select assegnatario from g1stipula where id=?", new Object[]{idStipula});
			page.setAttribute("sysconMittenteMail", assegnatario, PageContext.REQUEST_SCOPE);
	    	String mittenteMail=(String)sqlManager.getObject("select sysute from usrsys where syscon=?", new Object[]{assegnatario});
	    	page.setAttribute("mittenteMail", mittenteMail, PageContext.REQUEST_SCOPE);
	    	String oggettoMail="Inoltro per approvazione";
	    	page.setAttribute("oggettoMail", oggettoMail, PageContext.REQUEST_SCOPE);
	    	String testoMail="con la presente si informa che Le è stato assegnato il ruolo di \"Contract Manager\" relativamente al seguente contratto:\n\n"
	    			+ "Codice: "+codiceStipula+"\n"+ "Titolo: "+oggetto+"\n"+ "Importo: "+impStipula+"\n"+ "Operatore economico: "+nomest+"\n\n"
	    			+ "Ufficio: "+ufficio + "\n\nContratto creato da: " + nome_creatore + "\nContract Manager uscente: " +  nome_contract + "\n\n\n   Cordiali saluti";
	    	page.setAttribute("testoMail", testoMail, PageContext.REQUEST_SCOPE);

	    	page.setAttribute("statoStipula", statoStipula, PageContext.REQUEST_SCOPE);


	        MsgConferma = "Mediante questa funzione si procede all'inoltro per approvazione del contratto. Confermi l'operazione?" +
	        		"<br><br>Indicare il nuovo Contract Manager e il contenuto della mail di notifica.";
	        messaggio += " dei documenti della stipula.</b><br>";
	        page.setAttribute("MsgConferma", MsgConferma, PageContext.REQUEST_SCOPE);

	        if("NO".equals(controlloSuperato)){
	          page.setAttribute("controlloSuperato", controlloSuperato, PageContext.REQUEST_SCOPE);
	          page.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);
	        }else if(true){
	          //Controlli non bloccanti
	          messaggio="";

	        }


		} catch (SQLException sqle) {
			throw new JspException("Errore nella determinazione dell'assegnatario della stipula", sqle);
		} catch (GestoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}









  }




}