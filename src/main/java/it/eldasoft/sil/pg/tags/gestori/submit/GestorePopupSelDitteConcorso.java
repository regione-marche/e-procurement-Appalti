/*
 * Created on 06/09/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 *
 * @author Manuel Bridda
 */
public class GestorePopupSelDitteConcorso extends GestoreFasiRicezione {

  static Logger               logger         = Logger.getLogger(GestorePopupSelDitteConcorso.class);
  
  private GenChiaviManager genChiaviManager;

  @Override
  public String getEntita() {
    return "DITG";
  }

  public GestorePopupSelDitteConcorso() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupSelDitteConcorso(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer
      dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {


  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer dataColumnContainer)
      throws GestoreException {
    
    this.genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);

    //Anche se la pagina è aperta in modifica, vi è la necessità di eseguire le operazioni
    //legate all'inserimento di una ditta, operazioni si trovano nel preInsert GestoreFasiRicezione,
    //quindi anche se sono nel preUpdate si richiama super.preInsert.
    String[] listaDitteSelezionate = this.getRequest().getParameterValues("keys");
    List<String> keyList = Arrays.asList(listaDitteSelezionate);
    Collections.shuffle(keyList);
    String ngara=UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String codgar="$" + ngara;
    String dittao=null;
    String ngarapa=UtilityStruts.getParametroString(this.getRequest(),"ngarapa");
    String codgarpa="$" + ngarapa;

    try{
      int numeroOperatoriDaSelezionare=keyList.size();
      String select="select idanonimo from ditg where dittao = ? and codgar5 = ? and ngara5=?";

      for (int i = 0; i < keyList.size(); i++) {
        dittao = keyList.get(i);
        String idanonimo=null;
        try {
          idanonimo = (String) sqlManager.getObject(select, new Object[] {dittao, codgarpa, ngarapa });
        } catch (SQLException e) {
          throw new GestoreException("Errore nella lettura della ragione sociale della ditta ", null, e);
        }
        Long  progressivoOperatore= new Long(this.genChiaviManager.getNextId("G1IMPRCONC"));
        String numeroOperatore= String.format("%5s", progressivoOperatore.toString()).replace(' ', '0');
        String ragioneSociale="operatore "+numeroOperatore;
        String dittaoAnonimo="ope"+numeroOperatore;

        //campi chiave di ditg
        Vector elencoCampi = new Vector();
        elencoCampi.add(new DataColumn("DITG.NGARA5",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)));
        elencoCampi.add(new DataColumn("DITG.CODGAR5",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar)));
        elencoCampi.add(new DataColumn("DITG.DITTAO",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, dittaoAnonimo)));

        //campi che si devono inserire
        elencoCampi.add(new DataColumn("DITG.IDANONIMO",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, idanonimo)));
        elencoCampi.add(new DataColumn("DITG.NOMIMO",
            new JdbcParametro(JdbcParametro.TIPO_TESTO, ragioneSociale)));
        elencoCampi.add(new DataColumn("DITG.NPROGG",
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
        elencoCampi.add(new DataColumn("DITG.NUMORDPL",
            new JdbcParametro(JdbcParametro.TIPO_NUMERICO, null)));
        elencoCampi.add(new DataColumn("GARACONCORSO",//serve per la tracciatura nella superclasse
            new JdbcParametro(JdbcParametro.TIPO_TESTO, numeroOperatoriDaSelezionare+"")));
        
        try {
        //inserimento in G1IMPRCONC
        String isRT= (String) sqlManager.getObject("select ISRT from G1IMPRCONC where codimp=?" , new Object[] {dittao});
        sqlManager.update("insert into G1IMPRCONC (CODIMP,NOMIMP,ISRT) values(?,?,?)", new Object[] {dittaoAnonimo,ragioneSociale,isRT});
        //inserimento in W_PUSER
        Long newIdUser = new Long(genChiaviManager.getNextId("W_PUSER"));
        String usernomePadre = (String) sqlManager.getObject("select usernome from W_PUSER where userkey1=? and userent='G1IMPRCONC'", new Object[] {dittao});
        sqlManager.update("insert into W_PUSER (IDUSER,USERNOME,USERENT,USERKEY1) values (?,?,?,?)", 
            new Object[] {newIdUser, usernomePadre, "G1IMPRCONC", dittaoAnonimo});
        }catch(SQLException e) {
          throw new GestoreException("Errore nell'inserimento dei dati della ditta", null,e);
        }

        DataColumnContainer containerDITG = new DataColumnContainer(elencoCampi);
        super.preInsert(status, containerDITG);

      }

      //best case
      this.getRequest().setAttribute("RISULTATO", "OK");
    }catch(GestoreException e){
      this.getRequest().setAttribute("RISULTATO", "NOK");
      throw e;

    }


  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

}