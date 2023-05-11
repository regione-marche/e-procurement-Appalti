/*
 * Created on 15/06/20
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.AggiudicazioneManager;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire il
 * sorteggio ditte per inviti
 *
 * @author Marcello Caminiti
 */
public class GestorePopupSorteggioDitteInviti extends
    AbstractGestoreEntita {

  public GestorePopupSorteggioDitteInviti() {
    super(false);
  }

   /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

  private AggiudicazioneManager aggiudicazioneManager = null;

  private PgManager pgManager = null;

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);

    aggiudicazioneManager = (AggiudicazioneManager) UtilitySpring.getBean("aggiudicazioneManager",
        this.getServletContext(), AggiudicazioneManager.class);

    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);
  }

  @Override
  public String getEntita() {
    return "GARE";
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

    // lettura dei parametri di input
    String ngara = StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
      "ngara"));
    Long numope = datiForm.getLong("NUMOPE");
    String codgar=StringUtils.stripToNull(UtilityStruts.getParametroString(this.getRequest(),
        "codgar"));

    if(numope==null)
      numope = new Long(0);
    int livEvento =3;
    String errMsgEvento = "";
    String descrMsg = "Sorteggio ditte da invitare a presentare offerta";
    String ditteSorteggiate ="";
    try{
      //Si deve creare una stringa in cui si ha l'elenco dei codici delle ditte separati da ,
      String elenco="";

      List listaDitte = this.sqlManager.getListVector("select dittao,codgar5 from ditg where ngara5=? and (fasgar is null or fasgar >=-3) "
          + "and (acquisizione is null or acquisizione <> 8)", new Object[]{ngara});
      if(listaDitte!=null && listaDitte.size()>0){
        List<String> elencoDitteNonInvitate= new ArrayList<String>(listaDitte.size());
        String ditta=null;
        boolean ditteTutteInvitate=false;
        if(numope !=null && numope.longValue() >= listaDitte.size())
          ditteTutteInvitate=true;
        for(int i=0;i<listaDitte.size(); i++){
          ditta=SqlManager.getValueFromVectorParam(listaDitte.get(i), 0).stringValue();
          elenco+=ditta;
          if(i < listaDitte.size() - 1)
            elenco+=",";
          if(codgar==null)
            codgar=SqlManager.getValueFromVectorParam(listaDitte.get(i), 1).stringValue();
          elencoDitteNonInvitate.add(ditta);
        }

        HashMap hMapDatiSelezione = new HashMap();
        this.aggiudicazioneManager.selezioneCasualeParimerito("3", elenco,numope, hMapDatiSelezione);
        if(hMapDatiSelezione.get("listaParimeritoSelezionate")!=null){
          elenco = (String)hMapDatiSelezione.get("listaParimeritoSelezionate");
          //Elenco è una stringa che contiene i codici delle ditte sorteggiate, separati da ,
          String vetCodDitte[] = elenco.split(",");
          String update="update ditg set invgar=?, sortinv=? where ngara5=? and dittao=?";
          if(vetCodDitte!=null && vetCodDitte.length>0){
            for(int j=0; j < vetCodDitte.length; j++){
              ditta= vetCodDitte[j];
              this.sqlManager.update(update, new Object[]{"1","1",ngara,ditta});
              if(!ditteTutteInvitate){
                elencoDitteNonInvitate.remove(ditta);
              }
            }
            if(elenco.indexOf(",,")>=0){
              elenco = elenco.replaceAll(",,", "");
            }
            if(",".equals(elenco.substring(elenco.length()-1)))
              elenco=elenco.substring(0,elenco.length()-2);
            elenco = elenco.replaceAll(",", ", ");
            ditteSorteggiate +="Ditte sorteggiate : " + elenco;
          }
          if(!ditteTutteInvitate){
            update="update DITG set INVGAR=?, SORTINV=?, FASGAR = ?, AMMGAR = ?, RIBAUO = ?, MOTIES = ?, "
                + "IMPOFF = ?, IMPOFF1 = ?, PUNTEC = ?, PUNECO = ? "
                + "where CODGAR5 = ? "
                + "and NGARA5 = ? and DITTAO =? ";

            Object[] sqlParam = new Object[13];

            sqlParam[0] = new Long(2); // INVGAR
            sqlParam[1] = new Long(2); // SORTINV
            sqlParam[2] = new Long(-3); // FASGAR
            sqlParam[3] = new Long(2); // AMMGAR
            sqlParam[4] = null; // RIBAUO
            sqlParam[5] = null; // MOTIES
            sqlParam[6] = null; // IMPOFF
            sqlParam[7] = null; // IMPOFF1
            sqlParam[8] = null; // PUNTEC
            sqlParam[9] = null; // PUNECO
            sqlParam[10] = codgar; // CODGAR5
            sqlParam[11] = ngara; // NGARA5


            for(int z=0; z < elencoDitteNonInvitate.size(); z++){
              ditta = elencoDitteNonInvitate.get(z);
              sqlParam[12] = elencoDitteNonInvitate.get(z); // DITTAO
              this.sqlManager.update(update, sqlParam);
              this.pgManager.aggiornaDITGAMMIS(codgar, ngara, ditta,
                  new Long(-3), new Long(2), null, null, true, false, false);
            }

          }


        }
      }
      livEvento = 1 ;
      this.getRequest().setAttribute("sorteggioEseguito", "1");
    } catch (SQLException e) {
      errMsgEvento=e.getMessage();
      throw new GestoreException("Errore nella funzione di sorteggio ditte per l'invito", "sorteggioDitteInviti", e);
    }finally{
      //Tracciatura eventi
      LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
      logEvento.setLivEvento(livEvento);
      logEvento.setOggEvento(ngara);
      logEvento.setCodEvento("GA_SORTEGGIO_DITTE_INVITO");
      logEvento.setDescr(descrMsg);
      logEvento.setErrmsg(ditteSorteggiate + " " + errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);

    }

  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }


}
