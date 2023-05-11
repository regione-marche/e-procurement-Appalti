/*
 * Created on 30/08/2010
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
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per inserire/
 * cancellare le categorie d'iscrizione predefinite per tipologia di categoria
 * (lavori, forniture e servizi)
 *
 * Usato dalla scheda delle categorie per elenchi operatori economici
 *
 * @author Luca.Giacomazzo
 */
public class GestoreInsertDeleteCategoriePredefinite extends AbstractGestoreEntita {

	public GestoreInsertDeleteCategoriePredefinite() {
		super(false);
	}

	@Override
  public String getEntita() {
		return "GARE";
	}

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
  }

	@Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {
	}

	@Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
	}

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	}

	@Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	  String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
	  String tipoCategoria = UtilityStruts.getParametroString(this.getRequest(),"tipoCategoria");
	  String categoriaSelezionata = UtilityStruts.getParametroString(this.getRequest(),"categoriaSelezionata");
	  String tipoInserimento = UtilityStruts.getParametroString(this.getRequest(),"tipoInserimento");
	  String numeroLivello = UtilityStruts.getParametroString(this.getRequest(),"numeroLivello");

	  try{
	    if("Multiplo".equals(tipoInserimento)){
	        // Cancellazione delle categorie associate alla gara in analisi
	        // in base al valore della variabile tipoCategoria

	        this.sqlManager.update(
	                "delete from OPES where NGARA3=? and CATOFF in (select CAISIM from CAIS where TIPLAVG=?)",
	                new Object[]{ngara, tipoCategoria});

	        // Inserimento delle categorie nella OPES dall'archivio CAIS in base al
            // valore della variabile tipoCategoria
            // Determino il massimo valore di NOPEGA per la gara in analisi
            Long maxNOPEGA = (Long) this.sqlManager.getObject("select max(NOPEGA) from OPES where NGARA3=?",
                    new Object[]{ngara});
            if(maxNOPEGA == null)
                maxNOPEGA = new Long(0);

            List listaCategorie = sqlManager.getListVector(
                    "select CAISIM from CAIS where TIPLAVG=? and CAISIM <> 'OG' and CAISIM <> 'OS' order by CAISORD asc",
                    new Object[]{tipoCategoria});
            for(int i=0; i < listaCategorie.size(); i++){
                Vector vettore = (Vector) listaCategorie.get(i);
                this.sqlManager.update("insert into OPES (NGARA3, NOPEGA, CATOFF) values (?,?,?)",
                        new Object[]{ngara, new Long((maxNOPEGA.longValue() + 1 + i)),
                        (String)((JdbcParametro)vettore.get(0)).getValue()});
            }

	    }else{
	        int numlivello = Integer.parseInt(numeroLivello);
	        //Cancellazione dell'occorrenza selezionata e delle sue figlie
	        String select="delete from OPES where NGARA3=? and (CATOFF = ? or CATOFF in (select CAISIM from CAIS where CODLIV" + numlivello + "=? ))";
	        if(numlivello<5)
	          this.sqlManager.update(select, new Object[]{ngara, categoriaSelezionata,categoriaSelezionata});
	        else{
	          //Una categoria di livello 5 è sicuramente non foglia, quindi non ha figlie
	          select="delete from OPES where NGARA3=? and CATOFF = ?";
	          this.sqlManager.update(select, new Object[]{ngara, categoriaSelezionata});
	        }

	        //Inserimento della categoria selezionata e di tutte le sue figlie e dei padri
	        Long maxNOPEGA = (Long) this.sqlManager.getObject("select max(NOPEGA) from OPES where NGARA3=?",
                new Object[]{ngara});
            if(maxNOPEGA == null)
                maxNOPEGA = new Long(0);

            long nopega = maxNOPEGA.longValue();

            List vettorePadri = sqlManager.getVector("select codliv1, codliv2,codliv3,codliv4 from cais where caisim = ? ", new Object[]{categoriaSelezionata});
            if(vettorePadri!=null && vettorePadri.size()>0){
              for(int i=0; i<vettorePadri.size();i++){
                String catoff = (String)((JdbcParametro)vettorePadri.get(i)).getValue();
                if(catoff!= null &&!"".equals(catoff)){
                  Long occorrenze = (Long)this.sqlManager.getObject("select count(catoff) from opes where ngara3=? and catoff=?", new Object[]{ngara,catoff});
                  if(occorrenze== null || (occorrenze!= null && occorrenze.longValue()==0)){
                    nopega++;
                    this.sqlManager.update("insert into OPES (NGARA3, NOPEGA, CATOFF) values (?,?,?)",
                        new Object[]{ngara, nopega,catoff});
                  }
                }

              }
            }

            List listaCategorie = null;
            if(numlivello<5)
              listaCategorie = sqlManager.getListVector(
                "select CAISIM from CAIS where (CAISIM = ?  or CODLIV" + numlivello + " = ? ) order by CAISORD asc",
                new Object[]{categoriaSelezionata,categoriaSelezionata});
            else
              listaCategorie = sqlManager.getListVector(
                  "select CAISIM from CAIS where CAISIM = ?  order by CAISORD asc",
                  new Object[]{categoriaSelezionata});
            for(int i=0; i < listaCategorie.size(); i++){
              nopega++;
              Vector vettore = (Vector) listaCategorie.get(i);
              this.sqlManager.update("insert into OPES (NGARA3, NOPEGA, CATOFF) values (?,?,?)",
                      new Object[]{ngara, nopega,
                      (String)((JdbcParametro)vettore.get(0)).getValue()});
            }
	    }
	  } catch (SQLException e) {
	    throw new GestoreException("Errore nell'inserimento delle categorie " +
            "d'iscrizione predefinite nella gara " + ngara, null, e);
      }


	  this.getRequest().setAttribute("categorieInserite", "1");
	}

	@Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
	}

}