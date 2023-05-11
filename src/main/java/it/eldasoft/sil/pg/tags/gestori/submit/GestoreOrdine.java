/*
 * Created on 11/06/14
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
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore per gli ordini di acquisto
 *
 * @author Marcello.Caminiti
 */
public class GestoreOrdine extends GestoreDocumentazioneGara {

  @Override
  public String getEntita() {
    return "GARECONT";
  }

  public GestoreOrdine() {
    super(false);
  }


  public GestoreOrdine(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    String ngara= datiForm.getString("V_ODA.NGARA");
    if(ngara!=null && !"".equals(ngara)){
      PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
          this.getServletContext(), PgManager.class);
      pgManager.deleteGARE(ngara);

      String ditta = datiForm.getString("V_ODA.DITTA");
      try {
        this.getSqlManager().update("delete from gare where ngara=? ",new Object[]{ngara});
        this.getSqlManager().update("delete from torn where codgar=? ",new Object[]{"$"+ngara});
        this.getSqlManager().update("delete from WSFASCICOLO where entita='GARE' and key1=? ",new Object[]{"$"+ngara});
        this.getSqlManager().update("delete from WSDOCUMENTO where entita='GARE' and key1=? ",new Object[]{"$"+ngara});
        this.getSqlManager().update("update mericart set ngara=null where id in (select idricart from v_odaprod where ngara=? and codimp=?)",
            new Object[]{ngara,ditta});
        this.getSqlManager().update("delete from edit where codgar4=? and codime=?", new Object[]{"$"+ngara,ditta});
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella cancellazione dell'ordine",
            null, e);
      }
    }
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
	}

	@Override
	public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
					throws GestoreException {

			if (datiForm.isColumn("GARE.CODCIG") && datiForm.isModifiedColumn("GARE.CODCIG") && datiForm.getColumn("GARE.CODCIG").getValue().stringValue() != null
							&& !"".equals(datiForm.getColumn("GARE.CODCIG").getValue().stringValue()) && datiForm.getColumn("GARE.CODCIG").getValue().stringValue().length() != 10) {
				throw new GestoreException("Il codice CIG specificato non è valido", "controlloCodiceCIG");
			}

			if(datiForm.isColumn("GARE.CUPPRG") && datiForm.isModifiedColumn("GARE.CUPPRG") && datiForm.getColumn("GARE.CUPPRG").getValue().stringValue()!=null
		        && !"".equals(datiForm.getColumn("GARE.CUPPRG").getValue().stringValue()) && datiForm.getColumn("GARE.CUPPRG").getValue().stringValue().length()!= 15){
		      throw new GestoreException("Il codice CUP specificato non è valido","controlloCodiceCUP");
			}

			if(datiForm.isColumn("GARECONT.CENINT") && datiForm.isColumn("TORN.CENINT")  && datiForm.isColumn("GARECONT.PCOESE") &&
			    datiForm.isColumn("GARECONT.PCOFAT") && (datiForm.isModifiedColumn("GARECONT.PCOESE") || datiForm.isModifiedColumn("GARECONT.PCOFAT"))){
			  if(datiForm.getLong("GARECONT.PCOESE")==null && datiForm.getLong("GARECONT.PCOFAT")==null)
			    datiForm.setValue("GARECONT.CENINT", null);
			  else{
			    String cenint = datiForm.getString("TORN.CENINT");
			    datiForm.setValue("GARECONT.CENINT", cenint);
			  }

			}

			super.preUpdate(status, datiForm);

			//Aggiornamento GARECONT
			if (datiForm.isModifiedTable("GARECONT")) {
				AbstractGestoreEntita gestoreGARECONT = new DefaultGestoreEntita("GARECONT", this.getRequest());
				gestoreGARECONT.update(status, datiForm);
			}

			// Gestione del codice CIG fittizio
            if (datiForm.isColumn("ESENTE_CIG") && datiForm.isModifiedColumn("ESENTE_CIG")) {
                String esenteCig = datiForm.getString("ESENTE_CIG");
                String codCigFittizio = datiForm.getString("CODCIG_FIT");
                if ("1".equals(esenteCig)) {
                    if (StringUtils.isEmpty(codCigFittizio) || " ".equals(codCigFittizio)) {
                      GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
                          this.getServletContext(), GenChiaviManager.class);
                      int nextId = genChiaviManager.getNextId("GARE.CODCIG");
                        codCigFittizio = "#".concat(StringUtils.leftPad(""+nextId, 9, "0"));
                        datiForm.setValue("GARE.CODCIG", codCigFittizio);
                    } else {
                        datiForm.setValue("GARE.CODCIG", codCigFittizio);
                    }
                }
            }

			//Aggiornamento di GARE
			if (datiForm.isModifiedTable("GARE")) {
				AbstractGestoreEntita gestoreGARE = new DefaultGestoreEntita("GARE", this.getRequest());
				gestoreGARE.update(status, datiForm);
			}

			//Gestione sezione dinamica IVA
			AbstractGestoreChiaveIDAutoincrementante gestoreGAREIVA = new DefaultGestoreEntitaChiaveIDAutoincrementante(
		        "GAREIVA", "ID", this.getRequest());
		    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
		        gestoreGAREIVA, "GAREIVA",
		        new DataColumn[] {datiForm.getColumn("GARECONT.NGARA"),
		            datiForm.getColumn("GARECONT.NCONT") }, null);

		    try{
    		    if(datiForm.isColumn("GARE.CODCIG") && datiForm.getColumn("GARE.CODCIG").getValue().stringValue()!=null
    	            && !"".equals(datiForm.getColumn("GARE.CODCIG").getValue().stringValue())){
    	         PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
    	              this.getServletContext(), PgManager.class);
    	         String msg = pgManager.controlloUnicitaCIG(datiForm.getColumn("GARE.CODCIG").getValue().stringValue(),datiForm.getColumn("GARE.NGARA").getValue().stringValue());
    	         if(msg!=null){
        	         String descrizione = (String) this.sqlManager.getObject(
        	             "select tab1desc from tab1 where tab1cod = ? and tab1tip = ?",
        	             new Object[] { "A1151","1" });
        	         if(descrizione.substring(0, 1).equals("0")){
        	             UtilityStruts.addMessage(this.getRequest(), "warning",
        	                 "warnings.gare.codiceCIGDuplicato",
        	                 new Object[] {msg });
        	         }else{
        	           throw new GestoreException("Errore durante l'aggiornamento del campo GARE.CODCIG", "gare.codiceCIGDuplicato",new Object[] {msg },  new Exception());
        	         }
    	         }
  	          }
		    }catch (SQLException e1) {
	          throw new GestoreException("Errore nella lettura della campo tabellato A1151",null,e1);
	        }

	}

	@Override
	 public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

	  }
}
