/*
 * Created on 04/06/13
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */


package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore associato alla pagina della commissione per
 * il popolamento dei componenti
 *
 * @author Cristian Febas
 *
**/

public class GestorePopupCreaCommissione extends
    AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "GFOF";
  }

  public GestorePopupCreaCommissione() {
    super(false);
  }


  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    //Selezione della configurazione in base al criterio di aggiudicazione nella gara
    String codgar = impl.getString("GARE.CODGAR1");
    String ngara = impl.getString("GARE.NGARA");
    String statoCommissione = this.getRequest().getParameter("statoCommissione");
    String garaLottiConOffertaUnica = this.getRequest().getParameter("garaLottiConOffertaUnica");
    
    GenChiaviManager genchiaviManager = (GenChiaviManager) UtilitySpring.getBean(
        "genChiaviManager", this.getServletContext(), GenChiaviManager.class);

    Boolean flag_commCompleta = true;

    String codRUP = "";
    String codSA = "";
    Long idCommAlbo = null;
    String selectTorn  = "select codrup, cenint, idcommalbo from torn where codgar = ? ";

    String selectNominativiConfigurazione = "";
    if(!"true".equals(garaLottiConOffertaUnica)){
      selectNominativiConfigurazione = "select c.tipostruttura, c.codein, c.ruolo, c.riservaruolo, c.numcompo, t.codrup, c.isnorup" +
      " from commconf c, gare g , torn t" +
      " where c.criterioagg = g.critlicg and g.codgar1 = t.codgar and g.codgar1 = ? and g.ngara = ?" +
      " order by id";
    }else{
      selectNominativiConfigurazione = "select c.tipostruttura, c.codein, c.ruolo, c.riservaruolo, c.numcompo, t.codrup, c.isnorup" +
      " from commconf c, gare g , torn t" +
      " where c.criterioagg = t.critlic and g.codgar1 = t.codgar and g.codgar1 = ? and g.ngara = ?" +
      " order by id";
    }

    List listaComponentiDaInserire = new ArrayList<Vector>();
    List listaComponentiValidi = new ArrayList<Vector>();
    List listaNominativiConfigurazione;
    try {
      Vector datiTorn = sqlManager.getVector(selectTorn, new Object[] {codgar});
      if(datiTorn!= null && datiTorn.size()>0){
        if(datiTorn.get(0)!= null){
          codRUP = (String)((JdbcParametro)datiTorn.get(0)).getValue();
          codRUP =  UtilityStringhe.convertiNullInStringaVuota(codRUP);
        }
        if(datiTorn.get(1)!= null){
          codSA = (String)((JdbcParametro)datiTorn.get(1)).getValue();
          codSA =  UtilityStringhe.convertiNullInStringaVuota(codSA);
        }
        if(datiTorn.get(2)!= null){
          idCommAlbo = (Long)((JdbcParametro)datiTorn.get(2)).getValue();
        }
      }

      listaNominativiConfigurazione = sqlManager.getListVector(selectNominativiConfigurazione, new Object[] {codgar,ngara});
      if (listaNominativiConfigurazione != null && listaNominativiConfigurazione.size() > 0) {

          if("".equals(codSA)){
            this.getRequest().setAttribute("ERRORI", "SI");
            throw new GestoreException(
                "SA assente.Non risulta possibile la creazione della commissione",
                "noSACreazioneCommissione", null);
          }
          if("".equals(codRUP)){
            this.getRequest().setAttribute("ERRORI", "SI");
            throw new GestoreException(
                "RUP assente.Non risulta possibile la creazione della commissione",
                "noRUPCreazioneCommissione", null);
          }

        //Istanzio il gestore per assegnare il numero ordine
          GestoreAssegnaNumOrdine gestoreAssegnaNumOrdine = new GestoreAssegnaNumOrdine();
          gestoreAssegnaNumOrdine.setRequest(this.getRequest());
          try{
            gestoreAssegnaNumOrdine.modalitaCasualeCommissione(ngara, status);
          }catch (Exception e){
            this.getRequest().setAttribute("ERRORI", "SI");
            throw new GestoreException("Errore nel calcolo del numero d'ordine dei nominativi dell'elenco " + ngara, null, e);
          }

        Long numOrdCompo = new Long(0);

        for (int i = 0; i < listaNominativiConfigurazione.size(); i++) {
          Long tipoStruttura = SqlManager.getValueFromVectorParam(listaNominativiConfigurazione.get(i), 0).longValue();
          String struttura = SqlManager.getValueFromVectorParam(listaNominativiConfigurazione.get(i), 1).getStringValue();
          Long ruolo = SqlManager.getValueFromVectorParam(listaNominativiConfigurazione.get(i), 2).longValue();
          Long riservaRuolo = SqlManager.getValueFromVectorParam(listaNominativiConfigurazione.get(i), 3).longValue();
          Long numCompo = SqlManager.getValueFromVectorParam(listaNominativiConfigurazione.get(i), 4).longValue();
          String codrup = SqlManager.getValueFromVectorParam(listaNominativiConfigurazione.get(i), 5).getStringValue();
          String isNoRup = SqlManager.getValueFromVectorParam(listaNominativiConfigurazione.get(i), 6).getStringValue();
          //Eventuale reset inviti
          this.verificaPerReset(idCommAlbo,ruolo);
          numOrdCompo = new Long(i+1) ;
          if("SOSTITUZIONE".equals(statoCommissione)){
            //Verifica preliminare per sostituzione:recupero tutti i soggetti da sostituire
              Long numElementiDaSost = this.verificaPerSostituzione(listaComponentiValidi, ngara, ruolo, riservaRuolo, tipoStruttura, struttura, codSA, numCompo);
              for (int j = 0; j < numElementiDaSost; j++) {
                boolean ruoloFounded = this.selezionaInserisciRuolo(listaComponentiDaInserire, ngara, codrup, idCommAlbo, ruolo, isNoRup, tipoStruttura, struttura, codSA, new Long(1));
                if(false == ruoloFounded && riservaRuolo != null){
                  ruoloFounded = this.selezionaInserisciRuolo(listaComponentiDaInserire, ngara, codrup, idCommAlbo, riservaRuolo, isNoRup, tipoStruttura, struttura, codSA, new Long(1));
                }
                if(false == ruoloFounded){
                  flag_commCompleta=false;
                }
              }
          }else{
            if("VUOTA".equals(statoCommissione)){
                boolean ruoloFounded = this.selezionaInserisciRuolo(listaComponentiDaInserire, ngara, codrup, idCommAlbo, ruolo, isNoRup, tipoStruttura, struttura, codSA, numCompo);
                if(false == ruoloFounded && riservaRuolo != null){
                  ruoloFounded = this.selezionaInserisciRuolo(listaComponentiDaInserire,ngara, codrup, idCommAlbo, riservaRuolo, isNoRup, tipoStruttura, struttura, codSA, numCompo);
                }
                if(false == ruoloFounded){
                  flag_commCompleta=false;
                }
            }
          }//modalita statoCommissione

          if(flag_commCompleta.equals(false)){
            this.getRequest().setAttribute("ERRORI", "SI");
            throw new GestoreException(
                "Errore durante il completamento della creazione della commissione",
                "completaCreazioneCommissione", null);
          }
        }//nominativi configurazione

        String insertComponente = "insert into gfof (ngara2, codfof, incfof, numcomm, nomfof, intfof, numord, id ) values (?, ?, ?, ?, ?, ?, ?, ? )";
        String updateNumeroPresenzeCommissione = "update commruoli set inviti = (inviti + 1)" +
        " where idalbo = ? and idnomin = ? and ruolo= ? ";

        //spostare qui calcolo numord
        String selectMaxNumord = "select coalesce(max(numord),0) from gfof where ngara2 = ? and numcomm = ?";
        Long numOrdCompoIns = (Long) sqlManager.getObject(selectMaxNumord, new Object[] {ngara, new Long(1)});

        if(flag_commCompleta.equals(true)){
          for (int l = 0; l < listaComponentiDaInserire.size(); l++) {
            numOrdCompoIns = numOrdCompoIns + 1;
            String codtecSel = (String) SqlManager.getValueFromVectorParam(listaComponentiDaInserire.get(l), 1).getValue();
            Long ruolo = (Long) SqlManager.getValueFromVectorParam(listaComponentiDaInserire.get(l), 2).getValue();
            Long numcomm = (Long) SqlManager.getValueFromVectorParam(listaComponentiDaInserire.get(l), 3).getValue();
            String nomtecSel = (String) SqlManager.getValueFromVectorParam(listaComponentiDaInserire.get(l), 4).getValue();
            Long interno = (Long) SqlManager.getValueFromVectorParam(listaComponentiDaInserire.get(l), 5).getValue();
            //Long numord = (Long) SqlManager.getValueFromVectorParam(listaComponentiDaInserire.get(l), 6).getValue();
            Long idAlboSel = (Long) SqlManager.getValueFromVectorParam(listaComponentiDaInserire.get(l), 7).getValue();
            Long idNominativoSel = (Long) SqlManager.getValueFromVectorParam(listaComponentiDaInserire.get(l), 8).getValue();
            Long id = new Long(genchiaviManager.getNextId("GFOF"));
            this.sqlManager.update(insertComponente, new Object[] {ngara, codtecSel, ruolo, numcomm, nomtecSel, interno, numOrdCompoIns, id});
            sqlManager.update(updateNumeroPresenzeCommissione, new Object[] { idAlboSel, idNominativoSel, ruolo });
          }
        }
      }
    } catch (SQLException e) {
      this.getRequest().setAttribute("ERRORI", "SI");
      throw new GestoreException(
            "Errore nella selezione dei componenti della commissione di gara", null, e);

    }
    this.getRequest().setAttribute("commissioneCreata", "1");

  }

  /**
   * Metodo per verificare se ci sono componenti da sostituire
   *
   * @param
   *
   * @throws SQLException
   */
  private Long verificaPerSostituzione(List listaComponentiValidi, String ngara, Long ruolo, Long ruoloRiserva, Long tipoStruttura, String struttura, String codSA, Long numCompo) throws SQLException, GestoreException {
    
    Long numElementiDaSost = new Long(0);
    Long numElementiValidi = new Long(0);

    String codtec = "";
    String codein = "";
    String indisponibilita = "";
    Date dataaccettazione= null;
    Long numord = null;
    String selectComponente = "";
    List listaComponenti = null;



    if(tipoStruttura != null){
      if(new Long(1).equals(tipoStruttura)){ //qualsiasi
        selectComponente = " select g.codfof, g.indisponibilita, g.dataaccettazione, g.numord, n.codein" +
        " from gfof g, commnomin n" +
        " where g.codfof = n.codtec and g.ngara2 = ? and g.incfof =? and g.numcomm = ? " +
        " order by numord";
        listaComponenti = sqlManager.getListVector(selectComponente, new Object[] {ngara, ruolo, new Long(1)});
      }
      if(new Long(2).equals(tipoStruttura)){ //specifica
        selectComponente = " select g.codfof, g.indisponibilita, g.dataaccettazione, g.numord, n.codein" +
        " from gfof g, commnomin n" +
        " where g.codfof = n.codtec and g.ngara2 = ? and n.codein = ? and g.incfof =? and g.numcomm = ? " +
        " order by numord";
        listaComponenti = sqlManager.getListVector(selectComponente, new Object[] {ngara,struttura, ruolo, new Long(1)});
      }

      if(new Long(3).equals(tipoStruttura)){ //richiedente
        selectComponente = " select g.codfof, g.indisponibilita, g.dataaccettazione, g.numord, n.codein" +
        " from gfof g, commnomin n" +
        " where g.codfof = n.codtec and g.ngara2 = ? and n.codein = ? and g.incfof =? and g.numcomm = ? " +
        " order by numord";
        listaComponenti = sqlManager.getListVector(selectComponente, new Object[] {ngara,codSA, ruolo, new Long(1)});
      }
    }


    if (listaComponenti != null && listaComponenti.size() > 0) {
      for (int i = 0; i < listaComponenti.size(); i++) {
        codtec = SqlManager.getValueFromVectorParam(listaComponenti.get(i), 0).getStringValue();
        codtec = UtilityStringhe.convertiNullInStringaVuota(codtec);
        indisponibilita = SqlManager.getValueFromVectorParam(listaComponenti.get(i), 1).getStringValue();
        dataaccettazione = SqlManager.getValueFromVectorParam(listaComponenti.get(i), 2).dataValue();
        numord = SqlManager.getValueFromVectorParam(listaComponenti.get(i), 3).longValue();
        codein = SqlManager.getValueFromVectorParam(listaComponenti.get(i), 4).getStringValue();
        codein = UtilityStringhe.convertiNullInStringaVuota(codein);
        if("1".equals(indisponibilita) && dataaccettazione != null){
          ;
        }else{
          boolean isInElementiValidi = false;
          for (int l = 0; l < listaComponentiValidi.size(); l++) {
            String codtecVal = (String) SqlManager.getValueFromVectorParam(listaComponentiValidi.get(l), 1).getValue();
            if(codtec.equals(codtecVal)){
              isInElementiValidi = true;
              break;
            }
          }
          if(isInElementiValidi == false){
            numElementiValidi = numElementiValidi + 1;
            Vector vectComponenteValido = new Vector();
            vectComponenteValido.add(0, new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara));
            vectComponenteValido.add(1, new JdbcParametro(JdbcParametro.TIPO_TESTO, codtec));
            listaComponentiValidi.add(vectComponenteValido);
          }
        }
      }// for ruolo
    }

    if(numCompo > numElementiValidi && ruoloRiserva != null){

      if(new Long(1).equals(tipoStruttura)){ //qualsiasi
        listaComponenti = sqlManager.getListVector(selectComponente, new Object[] {ngara, ruoloRiserva, new Long(1)});
      }

      if(new Long(2).equals(tipoStruttura)){ //specifica
        listaComponenti = sqlManager.getListVector(selectComponente, new Object[] {ngara,struttura, ruoloRiserva, new Long(1)});
      }

      if(new Long(3).equals(tipoStruttura)){ //richiedente
        listaComponenti = sqlManager.getListVector(selectComponente, new Object[] {ngara,codSA, ruoloRiserva, new Long(1)});
      }

      if (listaComponenti != null && listaComponenti.size() > 0) {
        for (int i = 0; i < listaComponenti.size(); i++) {
          codtec = SqlManager.getValueFromVectorParam(listaComponenti.get(i), 0).getStringValue();
          codtec = UtilityStringhe.convertiNullInStringaVuota(codtec);
          indisponibilita = SqlManager.getValueFromVectorParam(listaComponenti.get(i), 1).getStringValue();
          dataaccettazione = SqlManager.getValueFromVectorParam(listaComponenti.get(i), 2).dataValue();
          numord = SqlManager.getValueFromVectorParam(listaComponenti.get(i), 3).longValue();
          codein = SqlManager.getValueFromVectorParam(listaComponenti.get(i), 4).getStringValue();
          codein = UtilityStringhe.convertiNullInStringaVuota(codein);
          if("1".equals(indisponibilita) && dataaccettazione != null){
            ;
          }else{
            boolean isInElementiValidi = false;
            for (int l = 0; l < listaComponentiValidi.size(); l++) {
              String codtecVal = (String) SqlManager.getValueFromVectorParam(listaComponentiValidi.get(l), 1).getValue();
              if(codtec.equals(codtecVal)){
                isInElementiValidi = true;
                break;
              }
            }
            if(isInElementiValidi == false){
              numElementiValidi = numElementiValidi + 1;
              Vector vectComponenteValido = new Vector();
              vectComponenteValido.add(0, new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara));
              vectComponenteValido.add(1, new JdbcParametro(JdbcParametro.TIPO_TESTO, codtec));
              listaComponentiValidi.add(vectComponenteValido);
            }
          }
        }// for riserva ruolo
      }
    }

    numElementiDaSost = numCompo - numElementiValidi;

    return numElementiDaSost;

  }//end verificaPerSostituzione

  /**
   * Metodo per
   *
   * @param
   *
   * @throws SQLException
   */
  private boolean selezionaInserisciRuolo(List listaComponentiDaInserire ,String ngara, String codrup, Long idAlbo, Long ruolo, String isNoRup, Long tipoStruttura, String struttura, String codSA, Long numCompo) throws SQLException, GestoreException {

    String selectRuoli = " select r.id, r.idalbo, r.idnomin, r.ruolo, r.inviti" +
    		" from commnomin n,commruoli r" +
    		" where r.idalbo = n.idalbo and r.idnomin = n.id and r.idalbo =? and r.ruolo = ?" +
    		" order by r.inviti,n.numord ";
    String selectNominativi = "";
    if(tipoStruttura != null){
      if(new Long(1).equals(tipoStruttura)){//qualsiasi
        selectNominativi = " select c.id, c.codein, c.codtec, t.nomtec, c.numord, c.idalbo" +
        " from commnomin c,tecni t where" +
        " t.codtec = c. codtec and" +
        " (c.dataab is not null and c.numord is not null) and" +
        " c.id = ? and c.idalbo = ? ";
      }
      if(new Long(2).equals(tipoStruttura)){ //specifica
        selectNominativi = " select c.id, c.codein, c.codtec, t.nomtec, c.numord, c.idalbo" +
        " from commnomin c,tecni t where" +
        " t.codtec = c. codtec and" +
        " (c.dataab is not null and c.numord is not null) and" +
        " c.id = ? and c.idalbo = ? and c.codein = ? ";
      }
      if(new Long(3).equals(tipoStruttura)){ //richiedente
        selectNominativi = " select c.id, c.codein, c.codtec, t.nomtec, c.numord, c.idalbo" +
        " from commnomin c,tecni t where" +
        " t.codtec = c. codtec and" +
        " (c.dataab is not null and c.numord is not null) and" +
        " c.id = ? and c.idalbo = ? and c.codein = ? ";
      }
    }




    String selectIncaricati = "select count(*) from gfof where ngara2 = ? and codfof = ? and numcomm = ? " ;

    List listaRuoli = sqlManager.getListVector(selectRuoli, new Object[] {idAlbo,ruolo});

    Long numCompoSelected = new Long(0);

    if (listaRuoli != null && listaRuoli.size() > 0) {
      for (int j = 0; j < listaRuoli.size(); j++) {
        Long idNomin = SqlManager.getValueFromVectorParam(listaRuoli.get(j), 2).longValue();
        Long numInviti = SqlManager.getValueFromVectorParam(listaRuoli.get(j), 4).longValue();
        Long numOrd = null;
        //Long idAlbo = null;
        String codein = null;
        String codtec = null;
        String nomtec = null;


        Vector datiNominativo = null;
        if(new Long(1).equals(tipoStruttura)){//qualsiasi
          datiNominativo = sqlManager.getVector(selectNominativi, new Object[] {idNomin,idAlbo});
        }
        if(new Long(2).equals(tipoStruttura)){ //specifica
          datiNominativo = sqlManager.getVector(selectNominativi, new Object[] {idNomin,idAlbo,struttura});
        }
        if(new Long(3).equals(tipoStruttura)){ //richiedente
          datiNominativo = sqlManager.getVector(selectNominativi, new Object[] {idNomin,idAlbo,codSA});
        }

        if(datiNominativo!= null && datiNominativo.size()>0){
          if(datiNominativo.get(1)!= null){
            codein = (String)((JdbcParametro)datiNominativo.get(1)).getValue();
            codein =  UtilityStringhe.convertiNullInStringaVuota(codein);
          }
          if(datiNominativo.get(2)!= null){
            codtec = (String)((JdbcParametro)datiNominativo.get(2)).getValue();
          }
          if(datiNominativo.get(3)!= null){
            nomtec = (String)((JdbcParametro)datiNominativo.get(3)).getValue();
          }
          if(datiNominativo.get(4)!= null){
            numOrd = (Long)((JdbcParametro)datiNominativo.get(4)).getValue();
          }
          if(datiNominativo.get(5)!= null){
            idAlbo = (Long)((JdbcParametro)datiNominativo.get(5)).getValue();
          }


        Long countIncaricati = new Long(0);
          countIncaricati = (Long) sqlManager.getObject(selectIncaricati, new Object[] {ngara, codtec, new Long(1)});
          //devo controllare anche che non si a uno che sto per inserire..
          for (int l = 0; l < listaComponentiDaInserire.size(); l++) {
            String codtecIns = (String) SqlManager.getValueFromVectorParam(listaComponentiDaInserire.get(l), 1).getValue();
            if(codtecIns.equals(codtec)){
              countIncaricati = countIncaricati +1;
            }
          }

        if((codtec.equals(codrup) && "1".equals(isNoRup)) || countIncaricati > 0 || (new Long(2).equals(tipoStruttura) && !codein.equals(struttura))){
          ;
        }else{
          if(idNomin != null){
            Vector vectComponenteDaInserire = new Vector();
            vectComponenteDaInserire.add(0, new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara));
            vectComponenteDaInserire.add(1, new JdbcParametro(JdbcParametro.TIPO_TESTO, codtec));
            vectComponenteDaInserire.add(2, new JdbcParametro(JdbcParametro.TIPO_NUMERICO, ruolo));
            vectComponenteDaInserire.add(3, new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1)));
            vectComponenteDaInserire.add(4, new JdbcParametro(JdbcParametro.TIPO_TESTO, nomtec));
            vectComponenteDaInserire.add(5, new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1)));
            //il numero ordine del componente lo calcolo dopo
            vectComponenteDaInserire.add(6, new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null));
            vectComponenteDaInserire.add(7, new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idAlbo));
            vectComponenteDaInserire.add(8, new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idNomin));
            listaComponentiDaInserire.add(vectComponenteDaInserire);
            numCompoSelected  = numCompoSelected + 1;
          }

          if(numCompo.equals(numCompoSelected)){
            break;
          }

        }

        }


      }//for nominativi ruolo


      if(!numCompo.equals(numCompoSelected)){
        return false;
      }
    }else{//non ci sono nominativi in db per quel ruolo
      return false;
    }


    return true;

  }//end selezionaPerRuolo


  /**
   * Metodo per verificare la percentuale di popolamento corrente
   * ed eventualmente effettuare il reset inviti per la rotazione
   *
   * @param
   *
   * @throws SQLException
   */
  private int verificaPerReset(Long idAlbo,Long ruolo) throws SQLException, GestoreException {

    String selectPercentualeReset = "select percreset from commalbo where id = ?";
    //solo abilitati
    String selectTotaleNominativiRuolo = "select count(*) from commnomin n1 where" +
    		" exists(select n2.id from commnomin n2, commruoli r" +
    		" where n1.id = n2.id and n1.idalbo = n2.idalbo" +
    		" and n2.id = r.idnomin and n2.idalbo = r.idalbo" +
    		" and n2.idalbo = ? and r.ruolo = ? and n2.dataab is not null)";
    String selectNominativiCommissione = "select n1.id from commnomin n1 where" +
    " exists(select n2.id from commnomin n2, commruoli r" +
    " where n1.id = n2.id and n1.idalbo = n2.idalbo" +
    " and n2.id = r.idnomin and n2.idalbo = r.idalbo" +
    " and n2.idalbo = ? and r.ruolo = ? and n2.dataab is not null and r.inviti > 0)";

    String updateRuoloNominativi = "update commruoli set inviti = ? where idalbo = ? and idnomin = ? and ruolo = ?";

    Double percentualeReset = (Double) sqlManager.getObject(selectPercentualeReset, new Object[] {idAlbo});
    Long totNominativiRuolo = (Long) sqlManager.getObject(selectTotaleNominativiRuolo, new Object[] {idAlbo,ruolo});
    if(percentualeReset!=null && percentualeReset > 0 && totNominativiRuolo!=null && totNominativiRuolo > 0){
      int totNomRuolo = totNominativiRuolo.intValue();
      List listaNominativiCommissione = sqlManager.getListVector(selectNominativiCommissione, new Object[] {idAlbo,ruolo});
      int numNominativiCommissione = listaNominativiCommissione.size();
      double percCorrente = (numNominativiCommissione * 100) / totNomRuolo;
      double percReset = percentualeReset.doubleValue();
      if(percCorrente >= percReset){
        for (int i = 0; i < listaNominativiCommissione.size(); i++) {
          Long idNomin = SqlManager.getValueFromVectorParam(listaNominativiCommissione.get(i), 0).longValue();
          this.sqlManager.update(updateRuoloNominativi, new Object[] {new Long(0), idAlbo, idNomin, ruolo});
        }
    }

    }

    return 0;
  }//end verificaPerReset



}