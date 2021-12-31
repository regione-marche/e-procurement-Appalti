package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityMath;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class GetProspettoPunteggiFunction extends AbstractFunzioneTag {

  public GetProspettoPunteggiFunction() {
    super(4, new Class[] { PageContext.class, String.class,String.class,String.class});
  }

  /**
  * Viene creata la lista per popolare il prospetto.
  * Ogni elemento della lista è relativo ad una ditta e contiene i seguenti valori:
  *         <ul>
  *         <li>0 - nomimo</li>
  *         <li>1 - Totale dei punteggi tecnici(punteggi prelevati da dpun)</li>
  *         <li>2 - Totale dei punteggi economici(punteggi prelevati da dpun)</li>
  *         <li>3 - Somma dei due totali precedenti</li>
  *         <li>4 - Fasgar</li>
  *         <li>5 - listaCriteriTecniciPunteggi</li>
  *         <li>6 - listaCriteriEconomiciPunteggi</li>
  *         <li>7 - Punteggio tecnico totale oltre il punteggio tecnico max?</li>
  *         <li>8 - Punteggio economico totale oltre il punteggio economico max?</li>
  *         <li>9 - Punteggio tecnico totale inferiore alla soglia minima tecnica?</li>
  *         <li>10 - Punteggio economico totale inferiore alla soglia minima economica?</li>
  *         <li>11 - Totale dei punteggi tecnici riparametrizzati (punteggi prelevati da dpun)</li>
  *         <li>12 - Totale dei punteggi economici riparametrizzati (punteggi prelevati da dpun)</li>
  *         <ul>13 - Somma dei due totali precedenti</li>
  *
  *Le due liste  listaCriteriTecniciPunteggi e listaCriteriEconomiciPunteggi, contengono i punteggi di una ditta
  *per i criteri(tecnici ed economici) specificati in goev, ed hanno la stessa struttura, che è la seguente:
  *
  *         <ul>
  *         <li>0 - despar</li>
  *         <li>1 - maxpun</li>
  *         <li>2 - livpar</li>
  *         <li>3 - minpun</li>
  *         <li>4 - norpar</li>
  *         <li>5 - norpar1</li>
  *         <li>6 - necvan</li>
  *         <li>7 - punteg(punteggio prelevato da dpun)</li>
  *         <li>8 - puntegrip(prelevato da dpun)</li>
  *         <ul>
  */

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        pageContext, PgManager.class);

    String ngara = (String) params[1];
    String codgar = (String) params[2];
    String isOffertaUnica = (String) params[3];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    List listaPunteggiDitte = new Vector();
    List listaCriteriTecnici = null;
    List listaCriteriEconomici = null;
    Double punteggioTecMax =null;
    Double punteggioEcoMax = null;
    Double mintec = null;
    Double mineco = null;
    Long riptec = null;
    Long ripeco = null;
    Long ripcritec = null;
    Long ripcrieco = null;
    boolean riparametrizzazione = false;
    boolean controlliSuPuntRiparamTecPerCriteri = false;
    boolean controlliSuPuntRiparamEcoPerCriteri = false;
    boolean saltareControlliSuPuntRiparamTecPerCriteri = false;
    boolean saltareControlliSuPuntRiparamEcoPerCriteri = false;

    try {

      //Caricamento dei punteggi massimi tecnici ed economici
      punteggioTecMax =pgManager.getSommaPunteggioTecnico(ngara);
      punteggioEcoMax =pgManager.getSommaPunteggioEconomico(ngara);

      if(punteggioTecMax== null)
        punteggioTecMax = new Double(0);

      if(punteggioEcoMax== null)
        punteggioEcoMax = new Double(0);


      //Caricamento delle soglie minime
      Vector datiGare1 = sqlManager.getVector("select mintec, mineco, riptec, ripeco, ripcritec, ripcrieco from gare1 where ngara = ?", new Object[]{ngara});
      if(datiGare1!=null && datiGare1.size()>0){
        mintec = (Double)((JdbcParametro) datiGare1.get(0)).getValue();
        mineco = (Double)((JdbcParametro) datiGare1.get(1)).getValue();
        riptec = (Long)((JdbcParametro) datiGare1.get(2)).getValue();
        ripeco = (Long)((JdbcParametro) datiGare1.get(3)).getValue();
        ripcritec = (Long)((JdbcParametro) datiGare1.get(4)).getValue();
        ripcrieco = (Long)((JdbcParametro) datiGare1.get(5)).getValue();
      }

      if(mintec== null)
        mintec = new Double(0);

      if(mineco== null)
        mineco = new Double(0);

      if(new Long(1).equals(riptec) || new Long(1).equals(ripeco) || new Long(2).equals(riptec) || new Long(2).equals(ripeco))
        riparametrizzazione = true;

      if(new Long(2).equals(riptec)  && ( new Long(2).equals(ripcritec) || new Long(3).equals(ripcritec)))
        controlliSuPuntRiparamTecPerCriteri = true;
      else if(new Long(2).equals(riptec)  &&  new Long(1).equals(ripcritec))
        saltareControlliSuPuntRiparamTecPerCriteri=true;

      if( new Long(2).equals(ripeco) && (new Long(2).equals(ripcrieco) || new Long(3).equals(ripcrieco)) )
        controlliSuPuntRiparamEcoPerCriteri = true;
      else if(new Long(2).equals(ripeco)  &&  new Long(1).equals(ripcrieco))
        saltareControlliSuPuntRiparamEcoPerCriteri=true;


      String selectCriteri ="select despar, maxpun, livpar, minpun,norpar,norpar1,necvan from goev g where ngara=? and necvan1 in " +
      "(select necvan from goev where ngara=? and tippar=? )order by norpar, necvan1, norpar1, necvan";

      //Caricamento dei criteri tecnici
      listaCriteriTecnici = sqlManager.getListVector(selectCriteri, new Object[] { ngara,ngara, new Long(1) });

      //Caricamento dei criteri economici
      listaCriteriEconomici = sqlManager.getListVector(selectCriteri, new Object[] { ngara, ngara,new Long(2) });

      List listaDatiDitg = null;

      //Si caricano le ditte visibili nella fase Valutazione tecnica
      String selectDitte ="select nomimo, fasgar,dittao,puntec,puneco,puntecrip,punecorip from ditg where ngara5=? and " +
      		"(DITG.INVOFF in ('0', '1') or DITG.INVOFF is null) and (DITG.FASGAR > 4 or DITG.FASGAR = 0 or DITG.FASGAR is null)" +
      		" order by numordpl,nomimo ";

      if(isOffertaUnica!= null && "true".equals(isOffertaUnica)){
        selectDitte ="select nomimo, fasgar,dittao,puntec,puneco,puntecrip,punecorip from ditg where ngara5=? and codgar5=?" +
        " and (DITG.FASGAR > 4 or DITG.FASGAR = 0 or DITG.FASGAR is null) order by numordpl";
        listaDatiDitg = sqlManager.getListVector(selectDitte, new Object[] { ngara,codgar });
      }else{
        listaDatiDitg = sqlManager.getListVector(selectDitte, new Object[] { ngara});
      }

      if(listaDatiDitg!= null && listaDatiDitg.size()>0){
        for(int i=0;i<listaDatiDitg.size(); i++){
          String nomimo = SqlManager.getValueFromVectorParam(listaDatiDitg.get(i),0).getStringValue();
          Long fasgar = SqlManager.getValueFromVectorParam(listaDatiDitg.get(i),1).longValue();
          String dittao = SqlManager.getValueFromVectorParam(listaDatiDitg.get(i),2).getStringValue();

          Double puntec = SqlManager.getValueFromVectorParam(listaDatiDitg.get(i),3).doubleValue();
          Double puneco = SqlManager.getValueFromVectorParam(listaDatiDitg.get(i),4).doubleValue();
          Double puntecrip= SqlManager.getValueFromVectorParam(listaDatiDitg.get(i),5).doubleValue();
          Double punecorip= SqlManager.getValueFromVectorParam(listaDatiDitg.get(i),6).doubleValue();

          if(puntec== null)
            puntec = new Double(0);

          if(puneco == null)
            puneco = new Double(0);

          if(puntecrip== null)
            puntecrip = new Double(0);

          if(punecorip == null)
            punecorip = new Double(0);

          Double punteggioTotale = null;
          Double punteggioTotaleRip = null;
          List listaCriteriTecniciPunteggi = new Vector();
          List listaCriteriEconomiciPunteggi = new Vector();
          boolean puntecTotaleOltreMax = false;
          boolean punecoTotaleOltreMax = false;
          boolean puntecTotaleInferioreSoglia = false;
          boolean punecoTotaleInferioreSoglia = false;

          //Caricamento dei punteggi tecnici da DPUN
          if(listaCriteriTecnici!= null && listaCriteriTecnici.size()>0){
            for(int ii=0;ii<listaCriteriTecnici.size();ii++){
              String despar = SqlManager.getValueFromVectorParam(listaCriteriTecnici.get(ii),0).stringValue();
              Double maxpun = SqlManager.getValueFromVectorParam(listaCriteriTecnici.get(ii),1).doubleValue();
              Long livpar = SqlManager.getValueFromVectorParam(listaCriteriTecnici.get(ii),2).longValue();
              Double minpun = SqlManager.getValueFromVectorParam(listaCriteriTecnici.get(ii),3).doubleValue();
              Double norpar = SqlManager.getValueFromVectorParam(listaCriteriTecnici.get(ii),4).doubleValue();
              Double norpar1 = SqlManager.getValueFromVectorParam(listaCriteriTecnici.get(ii),5).doubleValue();
              Long necvan = SqlManager.getValueFromVectorParam(listaCriteriTecnici.get(ii),6).longValue();

              Double punteg = null;
              Double puntegrip =  null;
              Vector datiDPUN = sqlManager.getVector("select punteg, puntegrip from dpun where ngara=? and dittao=? and necvan=? ", new Object[]{ngara,dittao,necvan});
              if(datiDPUN!=null && datiDPUN.size()>0){
                punteg = SqlManager.getValueFromVectorParam(datiDPUN, 0).doubleValue();
                puntegrip = SqlManager.getValueFromVectorParam(datiDPUN, 1).doubleValue();
              }
              if(punteg!= null)
                punteg = UtilityMath.round(punteg, 5);
              if(puntegrip!= null)
                puntegrip = UtilityMath.round(puntegrip, 5);
              listaCriteriTecniciPunteggi.add(((new Object[] {despar, maxpun, livpar, minpun, norpar, norpar1, necvan, punteg, puntegrip})));

            }
          }

          //Caricamento dei punteggi economici da DPUN
          if(listaCriteriEconomici!= null && listaCriteriEconomici.size()>0){
            for(int ii=0;ii<listaCriteriEconomici.size();ii++){
              //Vector tmpVect = (Vector) listaCriteriEconomici.get(ii);
              String despar = SqlManager.getValueFromVectorParam(listaCriteriEconomici.get(ii),0).stringValue();
              Double maxpun = SqlManager.getValueFromVectorParam(listaCriteriEconomici.get(ii),1).doubleValue();
              Long livpar = SqlManager.getValueFromVectorParam(listaCriteriEconomici.get(ii),2).longValue();
              Double minpun = SqlManager.getValueFromVectorParam(listaCriteriEconomici.get(ii),3).doubleValue();
              Double norpar = SqlManager.getValueFromVectorParam(listaCriteriEconomici.get(ii),4).doubleValue();
              Double norpar1 = SqlManager.getValueFromVectorParam(listaCriteriEconomici.get(ii),5).doubleValue();
              Long necvan = SqlManager.getValueFromVectorParam(listaCriteriEconomici.get(ii),6).longValue();

              Double punteg = null;
              Double puntegrip =  null;
              Vector datiDPUN = sqlManager.getVector("select punteg, puntegrip from dpun where ngara=? and dittao=? and necvan=? ", new Object[]{ngara,dittao,necvan});
              if(datiDPUN!=null && datiDPUN.size()>0){
                punteg = SqlManager.getValueFromVectorParam(datiDPUN, 0).doubleValue();
                puntegrip = SqlManager.getValueFromVectorParam(datiDPUN, 1).doubleValue();
              }
              if(punteg!= null)
                punteg = UtilityMath.round(punteg, 5);
              if(puntegrip!= null)
                puntegrip = UtilityMath.round(puntegrip, 5);
              listaCriteriEconomiciPunteggi.add(((new Object[] {despar, maxpun, livpar, minpun, norpar, norpar1, necvan, punteg, puntegrip})));

            }
          }
          punteggioTotale = new Double(puntec.doubleValue() + puneco.doubleValue());
          if(new Long(1).equals(riptec) || new Long(2).equals(riptec))
            punteggioTotaleRip = new Double(puntecrip.doubleValue());
          else
            punteggioTotaleRip = new Double(puntec.doubleValue());

          if(new Long(1).equals(ripeco) || new Long(2).equals(ripeco))
            punteggioTotaleRip += new Double(punecorip.doubleValue());
          else
            punteggioTotaleRip += new Double(puneco.doubleValue());


          //Per il controllo del superamento del valore massimo tecnico si considera sempre PUNTEC
          double punteggioConfronto=puntec.doubleValue();
          if(punteggioConfronto > punteggioTecMax.doubleValue() && punteggioTecMax.doubleValue()!=0)
            puntecTotaleOltreMax=true;
          //Per il controllo sulla soglia minima tecnica nel caso di RIPTEC=2 si considera il punteggio tecnico riparametrizzato
          if(new Long(2).equals(riptec))
            punteggioConfronto= puntecrip.doubleValue();
          if(punteggioConfronto < mintec.doubleValue() && punteggioConfronto!=0 && mintec.doubleValue()!=0)
            puntecTotaleInferioreSoglia=true;

           //Per il controllo del superamento del valore massimo economico si considera sempre PUNECO
          punteggioConfronto=puneco.doubleValue();
          if(punteggioConfronto > punteggioEcoMax.doubleValue() && punteggioEcoMax.doubleValue()!=0)
            punecoTotaleOltreMax=true;
          //Per il controllo sulla soglia minima economica nel caso di RIPECO=2 si considera il punteggio economico riparametrizzato
          if(new Long(2).equals(ripeco))
            punteggioConfronto= punecorip.doubleValue();
          if(punteggioConfronto < mineco.doubleValue() && punteggioConfronto!=0 && mineco.doubleValue()!=0)
            punecoTotaleInferioreSoglia=true;

          puntec = UtilityMath.round(puntec, 5);
          puneco = UtilityMath.round(puneco, 5);
          punteggioTotale = UtilityMath.round(punteggioTotale, 5);
          punteggioTotaleRip = UtilityMath.round(punteggioTotaleRip, 5);


          listaPunteggiDitte.add(((new Object[] { nomimo, puntec, puneco, punteggioTotale,fasgar,listaCriteriTecniciPunteggi, listaCriteriEconomiciPunteggi,
              puntecTotaleOltreMax,punecoTotaleOltreMax,puntecTotaleInferioreSoglia,punecoTotaleInferioreSoglia,puntecrip,punecorip,punteggioTotaleRip})));
        }
      }








    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura del prospetto", e);
    } catch (GestoreException e) {
      throw new JspException("Errore durante la lettura del prospetto", e);
    }

    if(listaPunteggiDitte.size()>0)
      pageContext.setAttribute("listaPunteggiDitte", listaPunteggiDitte, PageContext.REQUEST_SCOPE);

    pageContext.setAttribute("punteggioTecMax", punteggioTecMax, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("punteggioEcoMax", punteggioEcoMax, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("mintec", mintec, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("mineco", mineco, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("riptec", riptec, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("ripeco", ripeco, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("riparametrizzazione", new Boolean(riparametrizzazione), PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("controlliSuPuntRiparamTecPerCriteri", new Boolean(controlliSuPuntRiparamTecPerCriteri), PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("controlliSuPuntRiparamEcoPerCriteri", new Boolean(controlliSuPuntRiparamEcoPerCriteri), PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("saltareControlliSuPuntRiparamTecPerCriteri", new Boolean(saltareControlliSuPuntRiparamTecPerCriteri), PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("saltareControlliSuPuntRiparamEcoPerCriteri", new Boolean(saltareControlliSuPuntRiparamEcoPerCriteri), PageContext.REQUEST_SCOPE);
    return null;
  }







}
