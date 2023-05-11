/*
 * Created on 04/03/2014
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
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non gestire l'aggiornamento dei dati sull'entità principale),
 * per gestire la pubblicazione su Amministrazione trasparente
 *
 * @author Cristian Febas
 */
public class GestorePubblicaSuAmministrazioneTrasparente extends AbstractGestoreEntita {

  public GestorePubblicaSuAmministrazioneTrasparente() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", this.getServletContext(), SqlManager.class);
  }

  @Override
  public String getEntita() {
    return "TORN";
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
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    // lettura dei parametri di input
    String codgar = datiForm.getString("CODGAR");
    String ngara = datiForm.getString("NGARA");
    String genere = datiForm.getString("GENERE");
    Timestamp datpub = datiForm.getData("DATPUB");
    codgar = UtilityStringhe.convertiNullInStringaVuota(codgar);
    ngara = UtilityStringhe.convertiNullInStringaVuota(ngara);
    String chiave = ngara;
    if ("".equals(ngara)){
      chiave = codgar;
    }
    
    if("1".equals(genere)){
      try {
        List lottiAgg = this.sqlManager.getListVector("select NGARA from GARE where CODGAR1 = ? and DITTA is not null", new Object[]{codgar});
        if(lottiAgg != null && lottiAgg.size() > 0){
          for(int i = 0; i<lottiAgg.size(); i++){
            ngara = SqlManager.getValueFromVectorParam(lottiAgg.get(i), 0).getStringValue();
            Vector elencoCampiPUBG = new Vector();
            elencoCampiPUBG.add(new DataColumn("PUBG.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
            elencoCampiPUBG.add(new DataColumn("PUBG.NPUBG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
            elencoCampiPUBG.add(new DataColumn("PUBG.TIPPUBG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(14))));
            elencoCampiPUBG.add(new DataColumn("PUBG.DINPUBG", new JdbcParametro(JdbcParametro.TIPO_DATA, datpub)));

            DataColumnContainer containerPUBG = new DataColumnContainer(elencoCampiPUBG);
            // si predispone il gestore per l'aggiornamento dell'entità
            DefaultGestoreEntitaChiaveNumerica gestorePUBG = new DefaultGestoreEntitaChiaveNumerica("PUBG", "NPUBG", new String[] {"NGARA" },
                this.getRequest());

            containerPUBG.getColumn("PUBG.NPUBG").setValue(new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null));
            gestorePUBG.inserisci(status, containerPUBG);
            
            try {
              
              this.sqlManager.update("update DOCUMGARA set DATARILASCIO=?, STATODOC = 5 where CODGAR=? and NGARA=? and GRUPPO = ?", new Object[] {datpub, codgar, ngara, new Long(5)});
              
            } catch (SQLException e) {
              throw new GestoreException("Errore nell'aggiornamento dello stato dei documenti della gara", null, e);
            }
            
          }
        }
      } catch (SQLException e) {
        throw new GestoreException("Errore nella ricerca dei lotti aggiudicati della gara", null, e);
      }
    }else{
      Vector elencoCampiPUBG = new Vector();
      elencoCampiPUBG.add(new DataColumn("PUBG.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, chiave)));
      elencoCampiPUBG.add(new DataColumn("PUBG.NPUBG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
      elencoCampiPUBG.add(new DataColumn("PUBG.TIPPUBG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(14))));
      elencoCampiPUBG.add(new DataColumn("PUBG.DINPUBG", new JdbcParametro(JdbcParametro.TIPO_DATA, datpub)));
  
      DataColumnContainer containerPUBG = new DataColumnContainer(elencoCampiPUBG);
      // si predispone il gestore per l'aggiornamento dell'entità
      DefaultGestoreEntitaChiaveNumerica gestorePUBG = new DefaultGestoreEntitaChiaveNumerica("PUBG", "NPUBG", new String[] {"NGARA" },
          this.getRequest());
  
      containerPUBG.getColumn("PUBG.NPUBG").setValue(new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null));
      gestorePUBG.inserisci(status, containerPUBG);
      
      // Si deve aggiornare lo stato della documentazione di gara, impostando
      // STATODOC.DOCUMGARA=5
      try {

        if (ngara != null && !"".equals(ngara))
          this.sqlManager.update("update DOCUMGARA set DATARILASCIO=?, STATODOC = 5 where CODGAR=? and NGARA=? and GRUPPO = ?", new Object[] {datpub, codgar, ngara, new Long(5)});
        else
          this.sqlManager.update("update DOCUMGARA set DATARILASCIO=?, STATODOC = 5 where CODGAR=? and GRUPPO = ?", new Object[] {datpub, codgar, new Long(5)});

      } catch (SQLException e) {
        throw new GestoreException("Errore nell'aggiornamento dello stato dei documenti della gara", null, e);
      }
    }
    

    // setta l'operazione a completata, in modo da scatenare il reload della
    // pagina principale
    this.getRequest().setAttribute("pubblicazioneEseguita", "1");
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
  }

}
