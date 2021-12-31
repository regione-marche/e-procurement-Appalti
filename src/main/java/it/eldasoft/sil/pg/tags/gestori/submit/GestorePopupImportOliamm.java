/*
 * Created on 31/07/12
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
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ImportExportOffertaPrezziManager;
import it.eldasoft.sil.pg.bl.LoggerImportOffertaPrezzi;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityMath;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire l'import
 * dei prodotti da OLIAMM
 *
 * @author Marcello Caminiti
 */
public class GestorePopupImportOliamm extends
    AbstractGestoreEntita {

  public GestorePopupImportOliamm() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

  private ImportExportOffertaPrezziManager importExportOffertaPrezziManager = null;

  private PgManager pgManager = null;

  private TabellatiManager tabellatiManager = null;

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);

    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);

    importExportOffertaPrezziManager = (ImportExportOffertaPrezziManager) UtilitySpring.getBean("importExportOffertaPrezziManager",
        this.getServletContext(), ImportExportOffertaPrezziManager.class);

    pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);

    tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        this.getServletContext(), TabellatiManager.class);
  }

  @Override
  public String getEntita() {
    return "V_GARE_OUT";
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

    String  numeroGara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String  garaLottiConOffertaUnica = UtilityStruts.getParametroString(this.getRequest(),"garaLottiConOffertaUnica");
    boolean isGaraLottiConOffertaUnica = false;
    if("1".equals(garaLottiConOffertaUnica))
      isGaraLottiConOffertaUnica = true;

    HashMap mappaCodiciLotti = new HashMap();
    HashMap mappaDescrizioniLotti = new HashMap();
    HashMap mappaCodiciLottiOLIAMM = new HashMap();

    //Cancellazione delle occorrenze in GCAP e figlie
    try {
      importExportOffertaPrezziManager.cancellaDati(numeroGara, isGaraLottiConOffertaUnica, true);
    } catch (SQLException e) {
      this.getRequest().setAttribute("importEseguito", "ERRORI");
      throw new GestoreException("Errore nella cancellazione delle occorrenze presenti "
          + "nelle tabelle GCAP, GCAP_EST, GCAP_SAN, DPRE, DPRE_SAN "
          + "relative alla gara '" + numeroGara + "'",
          null, e);
    }

    List listaDati =null;

    try {
      Long id_lista = (Long)this.sqlManager.getObject("select cliv1 from gare where ngara=?", new Object[]{numeroGara});
      String select = "select riga, prodotto, descrizione, note, um, qta, prezzo, lotto_gara, iva,desc_lotto  from v_gare_out where id_lista=? order by lotto_gara,riga";

      listaDati = this.sqlManager.getListVector(select, new Object[]{id_lista});
      if(listaDati!=null && listaDati.size()>0){
        Long contaf = new Long(0);
        long progressivoLotto =1;
        Long lotto_garaOld= null;
        String codiceLotto = null;
        for (int i = 0; i < listaDati.size(); i++) {
          Vector tmpVect = (Vector) listaDati.get(i);
          Long riga = ((JdbcParametro) tmpVect.get(0)).longValue();
          String prodotto = (((JdbcParametro) tmpVect.get(1)).longValue()).toString();
          String descrizione = ((JdbcParametro) tmpVect.get(2)).getStringValue();
          String note = ((JdbcParametro) tmpVect.get(3)).getStringValue();
          String um = ((JdbcParametro) tmpVect.get(4)).getStringValue();
          Double qta = ((JdbcParametro) tmpVect.get(5)).doubleValue();
          Double prezzo = ((JdbcParametro) tmpVect.get(6)).doubleValue();
          Long lotto_gara = ((JdbcParametro) tmpVect.get(7)).longValue();
          Double iva = ((JdbcParametro) tmpVect.get(8)).doubleValue();
          String desc_lotto = ((JdbcParametro) tmpVect.get(9)).getStringValue();
          contaf = new Long(contaf.longValue()+1);

          if(isGaraLottiConOffertaUnica ){
            if(lotto_garaOld == null || !lotto_garaOld.equals(lotto_gara)){
              lotto_garaOld = lotto_gara;
              //codiga = (String) valoriCampiRigaExcel.get(colonnaCODIGA);
              HashMap mappa = this.pgManager.calcolaCodificaAutomatica("GARE",
                  Boolean.FALSE, numeroGara, progressivoLotto);
              codiceLotto = (String) mappa.get("numeroGara");
              if (!mappaCodiciLotti.containsKey(codiceLotto)){
                mappaCodiciLotti.put(Long.toString(progressivoLotto), codiceLotto);
                mappaDescrizioniLotti.put(Long.toString(progressivoLotto), desc_lotto);
                mappaCodiciLottiOLIAMM.put(Long.toString(progressivoLotto), lotto_gara);
                progressivoLotto++;

              }
            }

            Long tmpMaxContaf = (Long)this.sqlManager.getObject(
                "select max(CONTAF) from GCAP where NGARA = ? ",
                new Object[] { codiceLotto });
            if (tmpMaxContaf == null) tmpMaxContaf = new Long(0);
            contaf = new Long(tmpMaxContaf.longValue()+1);
          }else
            codiceLotto = numeroGara;

          String insert="insert into gcap(ngara,contaf,norvoc,codvoc,voce,unimis,quanti,prezun,ivaprod,solsic,sogrib) values(?,?,?,?,?,?,?,?,?,?,?)";
          //Il campo iva nella view v_gare_out prevede dei decimali, mentre il campo ivaprod di gcap non ha decimali, quindi si deve troncare il valore
          //prima di inserire in gcap
          int ivaTmp = (int)iva.doubleValue();
          this.sqlManager.update(insert,new Object[]{codiceLotto,contaf,riga,prodotto,descrizione,um,qta,prezzo,ivaTmp,"2","2"});

          if(note!=null && !"".equals(note)){
            insert="insert into GCAP_EST (NGARA, CONTAF, DESEST) values (?, ?, ?) ";
            this.sqlManager.update(insert,new Object[]{codiceLotto,contaf,note});
          }

          //Si deve controllare se il valore GCAP.UNIMIS appena inserito è presente nella tabella UNIMIS
          //(UNMIS.TIPO = GCAP.UNIMIS). Se ciò non accade si deve inserire l'occorrenza in UNIMIS.
          String tipoUnimis = (String)this.sqlManager.getObject("select tipo from unimis where tipo =? and conta=-1", new Object[]{um});
          if(tipoUnimis == null){
            this.sqlManager.update("insert into unimis(conta,tipo,desuni,numdec) values(?,?,?,?)",
                new Object[]{new Long(-1),um,um,new Long(0)});
          }
        }
      }

      if (!mappaCodiciLotti.isEmpty()) {
        LoggerImportOffertaPrezzi loggerImport = new LoggerImportOffertaPrezzi();
        importExportOffertaPrezziManager.creazioneLotti(mappaCodiciLotti, numeroGara, loggerImport, new Long(98),mappaDescrizioniLotti,mappaCodiciLottiOLIAMM,null);
        importExportOffertaPrezziManager.aggiornamentoDitteLotti(mappaCodiciLotti, numeroGara);
      }else{
        //Aggiornamento IMPAPP,GAROFF,IDIAUT.GARE e ISTAUT.TORN per la gara a lotto unico
        double importoSoggettoARibasso = 0;

        List listaImportiSoggettiARibasso = sqlManager.getListVector(
                    "select QUANTI, PREZUN from GCAP where NGARA = ? "
                        + "and SOLSIC = '2' and SOGRIB = '2' and QUANTI is not null "
                        +   "and PREZUN is not null", new Object[] { numeroGara });

        if (listaImportiSoggettiARibasso != null
            && listaImportiSoggettiARibasso.size() > 0) {
          for (int i = 0; i < listaImportiSoggettiARibasso.size(); i++) {
            Vector vettore = (Vector) listaImportiSoggettiARibasso.get(i);
            if (vettore != null && vettore.size() > 0) {
              Double quantita = (Double) ((JdbcParametro) vettore.get(0)).getValue();
              Double prezzoUnitario = (Double) ((JdbcParametro) vettore.get(1)).getValue();
              importoSoggettoARibasso += (quantita.doubleValue() * prezzoUnitario.doubleValue());
            }
          }
        }


        Double impapp = new Double(UtilityMath.round(importoSoggettoARibasso, 2));
        String fdfs = PgManager.getTabellatoPercCauzioneProvvisoria(2);
        String tmpPerCauzProvv = this.tabellatiManager.getDescrTabellato(fdfs,
            "1");
        Double percentualeCauzioneProvv = new Double(tmpPerCauzProvv);

        int numeroDecimali = this.pgManager.getArrotondamentoCauzioneProvvisoria(2);

        double garoff = UtilityMath.round(impapp.doubleValue()
            * percentualeCauzioneProvv.doubleValue()
            / 100, numeroDecimali);

        Double importoDitta = this.pgManager.getContributoAutoritaStAppaltante(
            impapp, "A1z01");

        this.sqlManager.update("update gare set impapp=?, garoff=?, idiaut=? where ngara=?",
            new Object[]{impapp,new Double(garoff),importoDitta,numeroGara});

        Double importoContributo = this.pgManager.getContributoAutoritaStAppaltante(impapp, "A1z02");
        this.sqlManager.update("update torn set istaut=? where codgar=?",
            new Object[]{importoContributo,"$" + numeroGara});

      }

    } catch (SQLException e) {
      this.getRequest().setAttribute("importEseguito", "ERRORI");
      throw new GestoreException("Errore nella lettura della gara OLIAMM'",
          null, e);
    }


    this.getRequest().setAttribute("importEseguito", "OK");
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
