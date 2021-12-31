/*
 * Created on 06/03/15
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.plugin;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore che effettua i controlli preliminari e popola la popup per
 * la rettifica dei termini di gara
 *
 * @author Marcello Caminiti
 */
public class GestorePopupRettificaTermini extends AbstractGestorePreload {

  private SqlManager sqlManager = null;
  private GeneManager geneManager = null;

  public GestorePopupRettificaTermini(BodyTagSupportGene tag) {
    super(tag);
  }


  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }


  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);

    geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
        page, GeneManager.class);

    // lettura dei parametri di input
    String codgar = page.getRequest().getParameter("codgar");
    String iterga = page.getRequest().getParameter("iterga");
    String pagina = page.getRequest().getParameter("pagina");
    String tipoGara = page.getRequest().getParameter("tipoGara");
    String gartel = page.getRequest().getParameter("gartel");
    String ngara = page.getRequest().getParameter("ngara");
    Vector<?> datiTorn=null;
    Date datadioggi = UtilityDate.getDataOdiernaAsDate();
    String controlloBusteSuperato = "NO";
    String terminiSuperati="NO";
    String msgInfoSupTermini="";
    String tipoComunicazione="";
    String tipoRettifica="";
    String msgTipoRettifica="";
    boolean visualizzarePresentazioneOfferta = false;
    if("Datigen".equals(pagina) && ("2".equals(iterga) || "4".equals(iterga))){
      //Caricamento dati termini per la presentazione della domanda di partecipazione
      try {
        datiTorn=this.sqlManager.getVector("select DTEPAR, OTEPAR, DTERMRICHCDP, DTERMRISPCDP from TORN where codgar=?", new Object[]{codgar});
      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura delle date dei termini di presentazione della domanda di partecipazione ", e);
      }
      if(datiTorn!=null && datiTorn.size()>0){
        try {
          Date dtepar = SqlManager.getValueFromVectorParam(datiTorn, 0).dataValue();
          String otepar = SqlManager.getValueFromVectorParam(datiTorn, 1).getStringValue();
          Date dtermrichcdp = SqlManager.getValueFromVectorParam(datiTorn, 2).dataValue();
          Date dtermrispcdp = SqlManager.getValueFromVectorParam(datiTorn, 3).dataValue();
          page.setAttribute("initDtepar", UtilityDate.convertiData(dtepar, UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);
          page.setAttribute("initOtepar", otepar, PageContext.REQUEST_SCOPE);
          page.setAttribute("initDtermrichcdp", UtilityDate.convertiData(dtermrichcdp, UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);
          page.setAttribute("initDtermrispcdp", UtilityDate.convertiData(dtermrispcdp, UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);

          if (dtepar!=null){
            dtepar = this.componiTermine(dtepar, otepar);
            if(datadioggi.after(dtepar)){
              msgInfoSupTermini = "I termini per la presentazione della domanda di partecipazione sono stati superati";
              page.setAttribute("msgInfoSupTermini", msgInfoSupTermini, PageContext.REQUEST_SCOPE);
              page.setAttribute("terminiSuperati", "SI", PageContext.REQUEST_SCOPE);
            }
          }

        } catch (GestoreException e) {
          throw new JspException("Errore durante la lettura delle date dei termini di presentazione della domanda di partecipazione ", e);
        }

      }
      tipoComunicazione="FS10";
      tipoRettifica="per la presentazione della domanda di partecipazione";
      msgTipoRettifica = "domande di partecipazione";
    }else{
      //Caricamento dati termini di presentazione dell'offerta e termini di apertura dei plichi
      visualizzarePresentazioneOfferta = false;
      boolean visualizzareAperturaPlichi = false;
      String profilo = (String) page.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);

      //if(("Datigen".equals(pagina) && "1".equals(iterga)) || condizioniProfiloAffidamentiVerificate ){
      if("Datigen".equals(pagina) && ("1".equals(iterga) || ("3".equals(iterga) || "5".equals(iterga) || "6".equals(iterga)))){
        //Apertura dalla pagina dei dati generali
        if((geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.TORN-scheda.DATIGEN.PDO") && "1".equals(tipoGara)) ||
            (geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.GARE-scheda.DATIGEN.PDO") && "2".equals(tipoGara)) ||
            (geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.TORN-OFFUNICA-scheda.DATIGEN.PDO") && "3".equals(tipoGara)))
          visualizzarePresentazioneOfferta = true;
        if(("1".equals(iterga) || ("3".equals(iterga) || "5".equals(iterga) || "6".equals(iterga))) && ((geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.TORN-scheda.DATIGEN.OFF") && "1".equals(tipoGara)) ||
            (geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.GARE-scheda.DATIGEN.OFF") && "2".equals(tipoGara)) ||
            (geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.TORN-OFFUNICA-scheda.DATIGEN.OFF") && "3".equals(tipoGara))))
          visualizzareAperturaPlichi = true;
      }else{
        //Apertura dalla fase ricezione inviti
        if((geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.GARE-scheda.FASIRICEZIONE.PRESOFF") && ("1".equals(tipoGara) || "2".equals(tipoGara))) ||
            (geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.TORN-OFFUNICA-scheda.FASIRICEZIONE.PRESOFF") && "3".equals(tipoGara)))
          visualizzarePresentazioneOfferta = true;
        if((geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.GARE-scheda.FASIRICEZIONE.APERPLIC") && ("1".equals(tipoGara) || "2".equals(tipoGara))) ||
            (geneManager.getProfili().checkProtec(profilo, "SEZ", "VIS","GARE.TORN-OFFUNICA-scheda.FASIRICEZIONE.APERPLIC") && "3".equals(tipoGara)))
          visualizzareAperturaPlichi = true;
      }
      page.setAttribute("visualizzarePresentazioneOfferta", new Boolean(visualizzarePresentazioneOfferta), PageContext.REQUEST_SCOPE);
      page.setAttribute("visualizzareAperturaPlichi", new Boolean(visualizzareAperturaPlichi), PageContext.REQUEST_SCOPE);

      try {
        datiTorn=this.sqlManager.getVector("select DTEOFF, OTEOFF, DTERMRICHCPO, DTERMRISPCPO, DESOFF, OESOFF from TORN where codgar=?", new Object[]{codgar});
      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura delle date termini di presentazione dell'offerta e termini di apertura dei plichi ", e);
      }
      if(datiTorn!=null && datiTorn.size()>0){
        try {
          Date dteoff = SqlManager.getValueFromVectorParam(datiTorn, 0).dataValue();
          String oteoff = SqlManager.getValueFromVectorParam(datiTorn, 1).getStringValue();
          Date dtermrichcpo = SqlManager.getValueFromVectorParam(datiTorn, 2).dataValue();
          Date dtermrispcpo = SqlManager.getValueFromVectorParam(datiTorn, 3).dataValue();
          Date desoff = SqlManager.getValueFromVectorParam(datiTorn, 4).dataValue();
          String oesoff = SqlManager.getValueFromVectorParam(datiTorn, 5).getStringValue();
          page.setAttribute("initDteoff", UtilityDate.convertiData(dteoff, UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);
          page.setAttribute("initOteoff", oteoff, PageContext.REQUEST_SCOPE);
          page.setAttribute("initDtermrichcpo", UtilityDate.convertiData(dtermrichcpo, UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);
          page.setAttribute("initDtermrispcpo", UtilityDate.convertiData(dtermrispcpo, UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);
          page.setAttribute("initDesoff", UtilityDate.convertiData(desoff, UtilityDate.FORMATO_GG_MM_AAAA), PageContext.REQUEST_SCOPE);
          page.setAttribute("initOesoff", oesoff, PageContext.REQUEST_SCOPE);

          boolean controlloSuperatoPresentazione = true;
          if (dteoff!=null && visualizzarePresentazioneOfferta){
            dteoff = this.componiTermine(dteoff, oteoff);
            if(datadioggi.after(dteoff)){
              controlloSuperatoPresentazione = false;
              msgInfoSupTermini = "I termini per la presentazione dell'offerta sono stati superati";
              terminiSuperati ="SI";
            }
          }
          if (visualizzareAperturaPlichi){
        	  if (desoff!=null){
	            desoff = this.componiTermine(desoff, oesoff);
	            if(datadioggi.after(desoff)){
	              terminiSuperati ="SI";
	              if(!controlloSuperatoPresentazione){
	                msgInfoSupTermini ="I termini per la presentazione dell'offerta e di apertura dei plichi sono stati superati";
	              } else {
	                msgInfoSupTermini = "I termini di apertura dei plichi sono stati superati";
	              }
	            }
        	  }
          }

          if(terminiSuperati =="SI"){
            page.setAttribute("msgInfoSupTermini", msgInfoSupTermini, PageContext.REQUEST_SCOPE);
            page.setAttribute("terminiSuperati", "SI", PageContext.REQUEST_SCOPE);
          }



        } catch (GestoreException e) {
          throw new JspException("Errore durante la lettura delle date termini di presentazione dell'offerta e termini di apertura dei plichi ", e);
        }

      }
      tipoComunicazione="FS11";
      tipoRettifica="per la presentazione offerte e l'apertura dei plichi";
      msgTipoRettifica = "offerte e all'apertura delle buste";
    }
    //Se gara telematica si deve bloccare se esistono comunicazioni già acquisite
    String controlloSuperato="SI";
    String msg ="";
    try{
      if("1".equals(gartel)){
        String chiaveComunicazione="";
        if("2".equals(tipoGara))
          chiaveComunicazione=ngara;
        else
          chiaveComunicazione=codgar;
        Long conteggio=(Long)this.sqlManager.getObject("select count(idcom) from w_invcom where idprg='PA' and comkey2=? and comtipo=? and (comstato=6 or comstato=7)", new Object[]{chiaveComunicazione,tipoComunicazione});
        if(conteggio!=null && conteggio.longValue()>0){
          controlloSuperato="NO";
          msg = "Non è possibile procedere alla rettifica dei termini " + tipoRettifica + " perchè si è già proceduto all'acquisizione delle " +  msgTipoRettifica;
          page.setAttribute("msg", msg, PageContext.REQUEST_SCOPE);

          if("FS11".equals(tipoComunicazione))  {
          Long conteggioBusteAperte=(Long)this.sqlManager.getObject("select count(idcom) from w_invcom where idprg='PA' and comkey2=? and comtipo like ? and (comstato=6 or comstato=7)", new Object[]{chiaveComunicazione,"FS11_%"});
          if(conteggioBusteAperte==null || conteggioBusteAperte.longValue()<= 0){
            controlloBusteSuperato = "SI";
            }
          }
        }else{
          //Controllo ditte che non hanno presentato offerta nel caso si visibile la sezione della presentazione dell'offerta
          if(visualizzarePresentazioneOfferta){
            String select="select count(dittao) from ditg where codgar5=?  and fasgar=?";
            if("3".equals(tipoGara))
              select +=" and ngara5=codgar5";
            Long conteggioDitteNoOfferta=(Long)this.sqlManager.getObject(select, new Object[]{codgar,new Long(1)});
            if(conteggioDitteNoOfferta!=null && conteggioDitteNoOfferta.longValue()>0){
              page.setAttribute("controlloOffertaDitteSuperato", "NO", PageContext.REQUEST_SCOPE);
            }
          }
        }
      }
    }catch (SQLException e) {
      throw new JspException("Errore durante la lettura delle comunicazioni " + tipoComunicazione + " della gara " + ngara, e);
    }
    page.setAttribute("controlloSuperato", controlloSuperato, PageContext.REQUEST_SCOPE);
    page.setAttribute("controlloBusteSuperato", controlloBusteSuperato, PageContext.REQUEST_SCOPE);
  }

  /**
   * A aprtire da una data ed un orario, viene costruito
   * un oggetto Date contenente quella data e quell'orario
   *
   * @param data
   * @param orario
   *
   * @return Date
   */
  private Date componiTermine(Date data, String orario){

    GregorianCalendar dataTermine = null;
    int anno;
    int mese;
    int giorno;
    int ore=0;
    int minuti=0;
    Calendar cal = Calendar.getInstance();
    cal.setTime(data);
    anno = cal.get(Calendar.YEAR);
    mese = cal.get(Calendar.MONTH);
    giorno = cal.get(Calendar.DAY_OF_MONTH);
    if(orario==null || "".equals(orario))
      orario = "23:59";

      String stringaOrario[] = orario.split(":");
      if(stringaOrario.length==2){
        ore = new Integer(stringaOrario[0]).intValue();
        minuti = new Integer(stringaOrario[1]).intValue();
      }

    dataTermine = new GregorianCalendar(anno,mese,giorno,ore,minuti);

    return dataTermine.getTime();

  }
}