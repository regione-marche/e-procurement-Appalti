/*
 * Created on 12/06/18
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore che effettua alcuni controlli inerenti il WSERP e
 * produce i messaggi opportuni nella finestra popup
 *
 * @author Cristian Febas
 */
public class GestoreWSERPMessaggi extends AbstractGestorePreload {

  SqlManager sqlManager = null;
  GestioneWSERPManager gestioneWSERPManager = null;

  public GestoreWSERPMessaggi(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }

  public void inizializzaManager(PageContext page){
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);


    gestioneWSERPManager = (GestioneWSERPManager) UtilitySpring.getBean("gestioneWSERPManager",
        page, GestioneWSERPManager.class);


  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    this.inizializzaManager(page);

    String codgar = page.getRequest().getParameter("codgar");
    String ngara = page.getRequest().getParameter("ngara");
    Long genere = null;
    Long gentip = null;
    String messaggio = "";
    String controlloSuperato="SI";


    if("".equals(ngara))
      ngara=null;

    if (codgar != null){
      try {
        //Se elenco operatori, la variabile resta nulla
        gentip = (Long) sqlManager.getObject("select genere from V_GARE_TORN where codgar = ?", new Object[]{codgar});
      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura del genere della gara ", e);
      }
    }

    if (ngara == null || "".equals(ngara)){
      try {
        genere = (Long) sqlManager.getObject(
            "select genere from GARE where ngara = ?", new Object[]{codgar});
      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura del genere della gara ", e);
      }
    }else if (ngara != null){
      try {
        genere = (Long) sqlManager.getObject(
            "select genere from GARE where ngara = ?", new Object[]{ngara});
      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura del genere della gara ", e);
      }
    }

    boolean lottoUnico = false;
    if(!(genere!=null && genere.longValue()==11)){
      if(ngara==null  || (gentip != null && gentip.longValue() == 1) || (genere!=null && genere.longValue()==3)){//Gara divisa a lotti con offerte distinte o offerta unica
        ;
      }else if( ngara!=null && genere!=null && (genere.longValue()==10 || genere.longValue()==20)){//gare ad elenco
        ;
      }else{ //gara a lotto unico
        lottoUnico=true;
      }
    }

    try {

      ProfiloUtente profilo = (ProfiloUtente) page.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = new Long(profilo.getId());
      String[] credenziali;
        credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, "WSERP");
      String username = credenziali[0];
      String password = credenziali[1];

      if(lottoUnico){
        Long countLav = (Long)this.sqlManager.getObject("select count(*) from GCAP where GCAP.NGARA = ?",
            new Object[]{ngara});
        if(countLav>0){
          String controlliGCAP[] = gestioneWSERPManager.verificaIntegrazioneArticoli(username, password, "WSERP", ngara);
          //controllare che si riesca a raggiungere il ws...altrimenti tutti gli articoli sono disallineati
          if("NO".equals(controlliGCAP[0])){
            controlloSuperato = "NO";
            messaggio +=  controlliGCAP[1];
          }else{
            controlloSuperato = "SI";
            messaggio +=  controlliGCAP[1];
          }
        }
      }else{
        List listaLotti = this.sqlManager.getListVector("select ngara,modlicg from gare where codgar1<>ngara and codgar1=?", new Object[]{codgar});
        if(listaLotti!=null && listaLotti.size()>0){
          String lotto=null;
          for(int i=0;i<listaLotti.size();i++){
            lotto = SqlManager.getValueFromVectorParam(listaLotti.get(i), 0).getStringValue();
            Long countLav = (Long)this.sqlManager.getObject("select count(*)  from GCAP where GCAP.NGARA = ?",
                new Object[]{lotto});
            if(countLav>0){
              messaggio +="Lotto " + lotto + ":" ;
              String controlliGCAP[] = gestioneWSERPManager.verificaIntegrazioneArticoli(username, password, "WSERP", lotto);
              if("NO".equals(controlliGCAP[0]) || "ERR".equals(controlliGCAP[0])){
                controlloSuperato = "NO";
                messaggio +=  controlliGCAP[1];
              }else{
                controlloSuperato = "SI";
                messaggio +=  controlliGCAP[1];
              }
              messaggio +="<br>";
            }
          }
        }
      }//a lotti

      //controllo sulla lunghezza del msg (su una popup)
      if(messaggio.length() > 500){
        messaggio = messaggio.substring(0, 450) + "...etc...";
      }

      page.setAttribute("controlloSuperato", controlloSuperato, PageContext.REQUEST_SCOPE);
      page.setAttribute("msg", messaggio, PageContext.REQUEST_SCOPE);




    } catch (GestoreException e) {
      throw new JspException("Errore nella verifica dell'integrazione WSERP per la gara " + codgar ,e);
    } catch (SQLException e) {
      throw new JspException("Errore nella verifica dell'integrazione WSERP per la gara " + codgar ,e);
    }








  }


}