/*
 * Created on 03/dic/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore per la pagina Atto contrattuale
 *
 * @author Marcello.Caminiti
 */
public class GestoreAttoContrattuale extends
    AbstractGestoreEntita {


  @Override
  public String getEntita() {
    return "GARECONT";
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
}

  /**
   * Al salvataggio si devono allineare i dati su tutti i lotti aggiudicati
   * dalla ditta.
   * Se si modifica ridiso si deve aggiornare l'importo IMPGAR su tutti i lotti
   * aggiudicati
   *
   * @author Marcello.Caminiti
   */
  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

      String modcont = this.getRequest().getParameter("MODCONT");
      Object[] params = new Object[20];
      params[0] = datiForm.getLong("GARE.TIATTO");
      params[1] = datiForm.getString("GARE.NREPAT");
      params[2] = datiForm.getData("GARE.DAATTO");
      params[3] = datiForm.getString("GARE.RIDISO");
      params[4] = datiForm.getString("GARE.NQUIET");
      params[5] = datiForm.getData("GARE.DQUIET");
      params[6] = datiForm.getString("GARE.ISTCRE");
      params[7] = datiForm.getString("GARE.INDIST");
      params[8] = datiForm.getData("GARE.DRICZDOCCR");
      params[9] = datiForm.getLong("GARE.TAGGEFF");
      params[10] = datiForm.getString("GARE.NAGGEFF");
      params[11] = datiForm.getData("GARE.DAGGEFF");
      params[12] = datiForm.getData("GARE.DCOMDITTAGG");
      params[13] = datiForm.getString("GARE.NCOMDITTAGG");
      params[14] = datiForm.getData("GARE.DCOMDITTNAG");
      params[15] = datiForm.getString("GARE.NCOMDITTNAG");
      params[16] = datiForm.getString("GARE.NMAXIMO");
      params[17] = datiForm.getString("GARE.CODGAR1");
      params[18] = datiForm.getString("GARE.DITTA");
      params[19] = datiForm.getString("GARE.NGARA");

	  try {
	    if("2".equals(modcont)){

    	    //Aggiorno tutti i lotti tranne il lotto corrente che viene aggiornato dal
    		//gestore
    		this.sqlManager.update(
    		  			"update GARE set TIATTO = ?, NREPAT = ?, DAATTO = ?, RIDISO = ?," +
    		  			                "NQUIET = ?, DQUIET = ?, ISTCRE = ?, INDIST = ?, " +
    		  			 "DRICZDOCCR=?, TAGGEFF=?, NAGGEFF=?, DAGGEFF=?, DCOMDITTAGG=?," +
                         "NCOMDITTAGG=?, DCOMDITTNAG=?, NCOMDITTNAG=?, NMAXIMO=? " +
    		  			 "where CODGAR1 = ? and DITTA = ? and NGARA <>? and genere is null",
    		  			params);

    		/*
    		//Aggiornamento GARECONT associata all'occorrenza complementare
    		if(datiForm.isModifiedTable("GARECONT")){
    	        AbstractGestoreEntita gestoreGARECONT = new DefaultGestoreEntita("GARECONT", this.getRequest());
    	        gestoreGARECONT.update(status, datiForm);
    	      }
            */

    		//Se è stato modificato il campo RIDISO si devono aggiornare gli importi dei campi IMPGAR
    		//dei lotti aggiudicati
    		if (datiForm.getColumn("GARE.RIDISO").isModified()) {
    			String ridiso = datiForm.getString("GARE.RIDISO");
    			String oldRidiso = datiForm.getColumn("GARE.RIDISO").getOriginalValue().getStringValue();

    			if (ridiso==null || "".equals(ridiso)) ridiso="2";
    			if (oldRidiso==null || "".equals(oldRidiso)) oldRidiso="2";

    			String update="";
    			if(!ridiso.equals(oldRidiso)){
    				if ("1".equals(ridiso)) update="update gare set impgar = impgar /2 where CODGAR1 = ? and DITTA = ? and genere is null";
    				else update="update gare set impgar = impgar * 2 where CODGAR1 = ? and DITTA = ? and genere is null";

    				this.sqlManager.update(update, new Object[]{params[17],params[18]});
    			}

    		}
	    }else{
	      Long aqoper=null;
	      if (datiForm.isColumn("GARE1.AQOPER"))
	          aqoper = datiForm.getLong("GARE1.AQOPER");
	      if((aqoper==null || (aqoper!=null && aqoper.longValue()!=2)) && datiForm.isModifiedTable("DITG")){
	        AbstractGestoreEntita gestoreGARE = new DefaultGestoreEntita("DITG", this.getRequest());
            gestoreGARE.update(status, datiForm);
          }
	    }
		if(datiForm.isModifiedTable("GARE")){
	        AbstractGestoreEntita gestoreGARE = new DefaultGestoreEntita("GARE", this.getRequest());
	        gestoreGARE.update(status, datiForm);
	      }
	} catch (SQLException e) {
		throw new GestoreException(
				"Errore nell'allineamento dei dati dei lotti aggiudicati dalla ditta " + params[9], null,e);

	}

	AbstractGestoreChiaveIDAutoincrementante gestoreMultiploGARATTIAGG = new DefaultGestoreEntitaChiaveIDAutoincrementante(
        "GARATTIAGG", "ID", this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
        gestoreMultiploGARATTIAGG, "GARATTIAGG",
        new DataColumn[] {datiForm.getColumn("GARE.NGARA")}, null);

  //Gestione sezioni dinamiche DITGAQ
    AbstractGestoreChiaveNumerica gestoreDITGAQ = new DefaultGestoreEntitaChiaveNumerica(
        "DITGAQ", "ID", new String[] {}, this.getRequest());

    String nomeCampoNumeroRecord = "NUMERO_DITGAQ" ;
    String nomeCampoDelete = "DEL_DITGAQ" ;
    String nomeCampoMod = "MOD_DITGAQ" ;

    if (datiForm.isColumn(nomeCampoNumeroRecord)) {
      // Estraggo dal dataColumnContainer tutte le occorrenze dei campi
      // dell'entità definita per il gestore
      DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(
          datiForm.getColumns(gestoreDITGAQ.getEntita(), 0));

      int numeroRecord = datiForm.getLong(nomeCampoNumeroRecord).intValue();

      for (int i = 1; i <= numeroRecord; i++) {
        DataColumnContainer newDataColumnContainer = new DataColumnContainer(
            tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

        // Rimozione dei campi fittizi (il campo per la marcatura della
        // delete e
        // tutti gli eventuali campi passati come argomento)
        newDataColumnContainer.removeColumns(new String[] {
            gestoreDITGAQ.getEntita() + "." + nomeCampoDelete,
            gestoreDITGAQ.getEntita() + "." + nomeCampoMod });

        gestoreDITGAQ.update(status, newDataColumnContainer);

        //Se è stata modoficata l'entità DITG si deve forzare l'aggiornamento
        if (datiForm.isModifiedColumn("DITG.RICSUB_" + i)) {
          String ricsub = datiForm.getString("DITG.RICSUB_" + i);
          String dittao = newDataColumnContainer.getString("DITGAQ.DITTAO");
          try {
            this.sqlManager.update("update ditg set ricsub=? where ngara5=? and dittao=? and codgar5=?",
                new Object[]{ricsub,datiForm.getString("GARE.NGARA"),dittao,datiForm.getString("GARE.CODGAR1")});
          } catch (SQLException e) {
            throw new GestoreException(
                "Errore nell'aggiornamento del campo DITG.RICSUB per la ditta:" + dittao + " della gara:" + datiForm.getString("GARE.NGARA"), null, e);
          }
        }
      }
    }

  }


}
