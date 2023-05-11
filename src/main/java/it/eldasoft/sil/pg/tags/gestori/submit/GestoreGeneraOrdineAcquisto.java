/*
 * Created on 05-06-2014
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
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per la popup meric-popup-generaOrdine.jsp
 *
 * @author Marcello Caminiti
 */
public class GestoreGeneraOrdineAcquisto extends GestoreDITG {


	@Override
  public String getEntita() {
		return "GARECONT";
	}

	public GestoreGeneraOrdineAcquisto() {
	    super(false);
	  }


	  public GestoreGeneraOrdineAcquisto(boolean isGestoreStandard) {
	    super(isGestoreStandard);
	  }

	@Override
  public void postDelete(DataColumnContainer datiForm)
			throws GestoreException {
	}


	@Override
  public void postInsert(DataColumnContainer datiForm)
			throws GestoreException {
	}


	@Override
  public void postUpdate(DataColumnContainer datiForm)
			throws GestoreException {
	}


	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	}

	//Gestione dell'inserimento nelle entita' GCAP e DPRE
	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

	  String id = UtilityStruts.getParametroString(this.getRequest(),"id");
	  String ditta = UtilityStruts.getParametroString(this.getRequest(),"ditta");
	  Long idLong = new Long(id);

	  GeneManager geneManager = (GeneManager) UtilitySpring.getBean(
          "geneManager", this.getServletContext(), GeneManager.class);

	  String ngara = null;

	  if(geneManager.isCodificaAutomatica("GARECONT", "NGARA")){
	    ngara = geneManager.calcolaCodificaAutomatica("GARECONT", "NGARA");
	  }else{
	    this.getRequest().setAttribute("NoCodificaAutomatica", "1");
	    return;
	  }

	  String nomima = null;
	  String nomest = null;
	  try {
	    nomest = (String)this.getSqlManager().getObject("select nomest from v_oda where idric=? and ditta=?", new Object[]{idLong,ditta});
	    if(nomest!=null && nomest.length()>61)
          nomima = nomest.substring(0, 61);
	    else
	      nomima = nomest;

      } catch (SQLException e) {
        this.getRequest().setAttribute("Errore", "1");
        throw new GestoreException(
            "Errore nella lettura dei dati di V_ODA",
            null, e);
      }

	  try {
	    //Inserimento in gare
	    this.getSqlManager().update("insert into gare(ngara,codgar1,tipgarg,genere,ditta,nomima,tiatto,idric) " +
        		"values(?,?,?,?,?,?,?,?)", new Object[]{ngara, "$"+ngara, new Long(73), new Long(4),ditta, nomima, new Long(8),idLong});

	    String cenint = (String)this.getSqlManager().getObject("select cenint from meric where id=?", new Object[]{idLong});

	    //Inserimento in torn
	    this.getSqlManager().update("insert into torn(codgar,cenint) values(?,?)", new Object[]{"$"+ngara,cenint});

	    //Inserimento in garecont
	    this.getSqlManager().update("insert into garecont(ngara,ncont,stato,codimp) values(?,?,?,?)", new Object[]{ngara,new Long(1), new Long(2),ditta});

	    datiForm.addColumn("DITG.NGARA5", ngara);
	    datiForm.addColumn("DITG.NOMIMO", nomima);
	    datiForm.addColumn("DITG.DITTAO", ditta);
	    datiForm.addColumn("DITG.NPROGG", JdbcParametro.TIPO_NUMERICO);
	    datiForm.addColumn("DITG.CODGAR5", JdbcParametro.TIPO_TESTO);

	    super.preInsert(status, datiForm);

	    DefaultGestoreEntita gestoreDITG = new DefaultGestoreEntita("DITG",
	        this.getRequest());
	    gestoreDITG.inserisci(status, datiForm);

	    //Calcolo importo dell'ordine
	    List datiImporti = this.getSqlManager().getListVector("select sum(preoff),perciva from v_odaprod where ngara is null and codimp=? and idric=? group by perciva", new Object[]{ditta, idLong});
	    if(datiImporti!= null && datiImporti.size()>0){

	      GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
	          this.getServletContext(), GenChiaviManager.class);

	      TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
	          "tabellatiManager", this.getServletContext(), TabellatiManager.class);

	      double totaleImporto=0;
	      double totaleImpIVA=0;

	      for(int i=0;i<datiImporti.size();i++){
	        Double importo = SqlManager.getValueFromVectorParam(datiImporti.get(i), 0).doubleValue();
	        Long iva = SqlManager.getValueFromVectorParam(datiImporti.get(i), 1).longValue();
	        String perciva = tabellatiManager.getDescrTabellato("G_055", iva.toString());

	        if(importo!=null){
	          double troncato =  Math.round(importo.doubleValue()*100.0)/100.0;
	          importo = new Double(troncato);
	          totaleImporto+=troncato;
	        }

	        Double importoIva = null;
	        if(importo!=null && iva!=null ){
	          double importoDouble = importo.doubleValue();
	          //double ivaDouble = iva.doubleValue();
	          double ivaDouble = (new Long(perciva)).doubleValue();
	          double impiva =  importoDouble * ivaDouble / 100;
	          impiva = Math.round(impiva*100.0)/100.0;
	          importoIva = new Double(impiva);
	          totaleImpIVA += importoIva;
	        }
	        //Inserimento in GAREIVA
	        this.getSqlManager().update("insert into gareiva(id,ngara,ncont,perciva,imponib,impiva) values(?,?,?,?,?,?)",
	            new Object[]{new Long(genChiaviManager.getNextId("GAREIVA")),ngara,new Long(1),iva,importo,importoIva});


	      }
	      Double totImporto = null;
	      if(totaleImporto!=0)
	        totImporto = new Double(totaleImporto);

	      Double totImportoIva = null;
          if(totaleImpIVA!=0)
            totImportoIva = new Double(totaleImpIVA);

          double totale = totaleImporto +  totaleImpIVA;
          Double tot =  null;
          if(totale!=0)
            tot = new Double(totale);

	      this.getSqlManager().update("update gare set iaggiu=? where ngara=?", new Object[]{totImporto,ngara});
	      this.getSqlManager().update("update garecont set impiva=?,imptot=?  where ngara=? and ncont=?", new Object[]{totImportoIva,tot,ngara,new Long(1)});

	    }

	    //Aggiornamento MERICART
	    this.getSqlManager().update("update mericart set ngara=? where id in (select idricart from v_odaprod where ngara is null and codimp=? and idric=?)",
	        new Object[]{ngara,ditta,idLong});

	    this.getRequest().setAttribute("creazioneEseguita", "1");
	    this.getRequest().setAttribute("codiceOrdine", ngara);


      } catch (SQLException e) {
        this.getRequest().setAttribute("Errore", "1");
        throw new GestoreException(
            "Errore nella creazione dell'ordine di acquisto",
            null, e);
      }



	}


	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm )
			throws GestoreException {



	}
}