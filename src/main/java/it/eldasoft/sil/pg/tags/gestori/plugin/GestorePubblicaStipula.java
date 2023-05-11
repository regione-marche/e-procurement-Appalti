/*
 * Created on 10/05/21
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

import org.apache.commons.lang.StringUtils;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.bl.ValidatorManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Gestore che effettua il controllo della valorizzazione di alcuni
 * campi. Se anche uno non risulta valorizzato, allora si deve riportare
 * un messaggio opportuno alla finestra g1stipula-PubblicaDocumenti.jsp
 *
 * @authorCristian Febas
 */
public class GestorePubblicaStipula extends AbstractGestorePreload {

  SqlManager sqlManager = null;
  PgManager pgManager = null;
  TabellatiManager tabellatiManager = null;
  FileAllegatoManager fileAllegatoManager = null;
  PgManagerEst1 pgManagerEst1 = null;
  ValidatorManager validatorManager= null;


  public GestorePubblicaStipula(BodyTagSupportGene tag) {
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
    String messaggio = "<b>Non è possibile procedere con la pubblicazione dei documenti della stipula su area riservata del portale Appalti.</b><br>";
    String controlloSuperato="SI";
    String MsgConferma = "Confermi la pubblicazione dei documenti della stipula su area riservata del portale Appalti?";


    String profilo = (String) page.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);

      page.setAttribute("MsgConferma", MsgConferma, PageContext.REQUEST_SCOPE);

      try {

          select="select IMPSTIPULA,CODDEC,STATO,OGGETTO,CODSTIPULA from G1STIPULA where ID = ?";
          Vector datiStipula = sqlManager.getVector(select, new Object[]{idStipula});

          String codstipula=null;
          if(datiStipula!=null){
            String coddec = SqlManager.getValueFromVectorParam(datiStipula,1).getStringValue();
            coddec = StringUtils.stripToEmpty(coddec);
            Long statoStipula = (Long) SqlManager.getValueFromVectorParam(datiStipula,2).getValue();
            page.setAttribute("statoStipula", statoStipula, PageContext.REQUEST_SCOPE);
            String oggetto = SqlManager.getValueFromVectorParam(datiStipula,3).getStringValue();
            oggetto = StringUtils.stripToEmpty(oggetto);
            //Controllo oggetto
            if ("".equals(oggetto)) {
                controlloSuperato = "NO";
                messaggio += "<br>Non è stato inserito l'oggetto della stipula";
            }
            codstipula = SqlManager.getValueFromVectorParam(datiStipula,4).getStringValue();
            page.setAttribute("codstipula", codstipula, PageContext.REQUEST_SCOPE);
          }

          	//Controlli preliminari:
	        //controllo la presenza dei file nei documenti obbligatori nei doc con visibilita 1 o 2
          Long conteggioDocObbligatori = (Long)this.sqlManager.getObject("select count(*) from G1DOCSTIPULA where IDSTIPULA = ? and OBBLIGATORIO = ?"
          		+ " and (VISIBILITA = ? or VISIBILITA = ?) ",
        		  new Object[]{idStipula,"1",Long.valueOf(1),Long.valueOf(2)});
          Long conteggioDocObblPresenti = (Long)this.sqlManager.getObject("select count(*) from G1DOCSTIPULA g,W_DOCDIG w where w.DIGENT=?"
          		+ " and w.digkey1 = " + sqlManager.getDBFunction("inttostr",  new String[] {"g.ID"})
          		+ " and g.IDSTIPULA=? and g.OBBLIGATORIO= ? and (g.VISIBILITA = ? or g.VISIBILITA = ?)",
          		new Object[]{"G1DOCSTIPULA",idStipula,"1",Long.valueOf(1),Long.valueOf(2)});

          if((conteggioDocObbligatori.longValue()- conteggioDocObblPresenti.longValue())>0){
            controlloSuperato = "NO";
            messaggio += "<br>Ci sono documenti in cui non e' presente l'allegato obbligatorio";
          }

          String codimp = (String) sqlManager.getObject("select codimp from v_gare_stipula where id = ?", new Object[]{idStipula});

          //verifico se si tratta di una RTI
          String codice = null;
          Long tipimp  = (Long) sqlManager.getObject("select tipimp from impr where codimp = ? ", new Object[]{codimp});
          if(Long.valueOf(3).equals(tipimp) || Long.valueOf(10).equals(tipimp)) {
        	  codice = (String) sqlManager.getObject("select coddic from ragimp where codime9 = ? and impman = '1'", new Object[]{codimp});
        	  codice = StringUtils.stripToEmpty(codice);
        	  if("".equals(codice)) {
                  controlloSuperato = "NO";
                  messaggio += "<br>Il destinatario non ha una mandataria specificata in anagrafica";
        	  }
          }else {
        	  codice = codimp;
          }

          String selectImpr = "select emaiip, emai2ip, faximp,tipimp,nomimp from impr where codimp = ? ";
          Vector<?> datiSoggettoDestinatario = this.sqlManager.getVector(selectImpr, new Object[] {codice});
          String[] soggettoDestinatario = new String[4];
          String email= null;
          String pec = null;
          String fax = null;
          String nomimp =null;
          if (datiSoggettoDestinatario != null) {
            if (datiSoggettoDestinatario.get(0) != null){
              email = ((JdbcParametro) datiSoggettoDestinatario.get(0)).getStringValue();
              email =  UtilityStringhe.convertiNullInStringaVuota(email);
            }
            if (datiSoggettoDestinatario.get(1) != null){
              pec = ((JdbcParametro) datiSoggettoDestinatario.get(1)).getStringValue();
              pec =  UtilityStringhe.convertiNullInStringaVuota(pec);
            }
            if (datiSoggettoDestinatario.get(2) != null)
              fax = ((JdbcParametro) datiSoggettoDestinatario.get(2)).getStringValue();
            if (datiSoggettoDestinatario.get(3) != null)
              tipimp = (Long) ((JdbcParametro) datiSoggettoDestinatario.get(3)).getValue();
            if (datiSoggettoDestinatario.get(4) != null)
              nomimp = ((JdbcParametro) datiSoggettoDestinatario.get(4)).getStringValue();


            soggettoDestinatario[0] = nomimp;
            if(!"".equals(pec)){
              soggettoDestinatario[1] = pec;
              soggettoDestinatario[2] = "PEC";
            }else{
              soggettoDestinatario[1] = email;
              soggettoDestinatario[2] = "E-mail";
            }
            soggettoDestinatario[3] = codimp;


          }

          if("".equals(pec) && "".equals(email)) {
              controlloSuperato = "NO";
              messaggio += "<br>Il destinatario non ha un indirizzo PEC o E-mail specificato in anagrafica";
          }

         if("NO".equals(controlloSuperato)){
           page.setAttribute("controlloSuperato", controlloSuperato, PageContext.REQUEST_SCOPE);
           page.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);
         }else{
           page.setAttribute("soggettoDestinatario", soggettoDestinatario, PageContext.REQUEST_SCOPE);
         }

      } catch (SQLException e) {
    	  throw new JspException("Errore nella lettura dati per la pubblicazione della stipula", e);
      }
  }
}