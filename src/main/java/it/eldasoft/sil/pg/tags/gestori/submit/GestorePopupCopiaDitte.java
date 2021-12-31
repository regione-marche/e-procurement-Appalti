package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.springframework.transaction.TransactionStatus;

public class GestorePopupCopiaDitte extends AbstractGestoreEntita {

	@Override
  public String getEntita() {
		return "TORN";
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

	@Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

	}

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

		String codgar = datiForm.getString("CODGAR");
		String lottosorgente = datiForm.getString("LOTTOSORGENTE");
		String lottodestinazione = datiForm.getString("LOTTODESTINAZIONE");

		this.getRequest().setAttribute("CODGAR", codgar);
		this.getRequest().setAttribute("LOTTOSORGENTE", lottosorgente);
		this.getRequest().setAttribute("LOTTODESTINAZIONE", lottodestinazione);


		PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
	        this.getServletContext(), PgManager.class);

		GestoreDITG gestDITG = new GestoreDITG();
		gestDITG.setRequest(this.getRequest());

		try {
			Vector elencoCampiDITG = new Vector();

		    elencoCampiDITG.add(new DataColumn("DITG.CODGAR5",
                new JdbcParametro(JdbcParametro.TIPO_TESTO, codgar)));

		    elencoCampiDITG.add(new DataColumn("DITG.NGARA5",
		         new JdbcParametro(JdbcParametro.TIPO_TESTO, lottodestinazione)));

		    String select ="select dittao from ditg where codgar5 = ? and ngara5 = ? ";
		    String dittao=null;
		    List listaDittao = this.sqlManager.getListVector(select, new Object[]{codgar,lottodestinazione});
		    if(listaDittao!=null && listaDittao.size()>0){
		      for(int i=0;i<listaDittao.size();i++){
		        if(elencoCampiDITG.size()>2)
	                elencoCampiDITG.remove(2);
	            dittao = (String) this.sqlManager.getValueFromVectorParam(listaDittao.get(i), 0).getValue();
	            elencoCampiDITG.add(new DataColumn("DITG.DITTAO",new JdbcParametro(JdbcParametro.TIPO_TESTO, dittao)));

	            DataColumnContainer dataColumnContainer = new DataColumnContainer(elencoCampiDITG);
	            gestDITG.preDelete(status, dataColumnContainer);
	            this.getGeneManager().deleteTabelle(new String[]{"DITG"},
	                "CODGAR5 = ? and DITTAO = ? and NGARA5 = ?",
	                      new Object[]{codgar, dittao, lottodestinazione});
		      }

		    }

			//Inserimento delle ditte
            pgManager.copiaDitte( lottosorgente ,codgar,lottodestinazione,codgar,false,true,true);

            //Si deve aggiornare IMPAPPD delle ditte copiate con IMPAPP del lotto di destinazione
            Double impapp = (Double)sqlManager.getObject("select impapp from gare where ngara = ? ", new Object[]{lottodestinazione});
            sqlManager.update("update ditg set impappd=? where ngara5=? and codgar5=?", new Object[]{impapp,lottodestinazione,codgar});

            List datiDITG = sqlManager.getListVector("select dittao,acquisizione from ditg where codgar5 = ? and ngara5 = ?", new Object[] {codgar, lottosorgente});

            if (datiDITG != null && datiDITG.size()>0) {
              Long acquisizione = null;
              for(int j=0; j< datiDITG.size(); j++) {
                String codiceDitta = (String) SqlManager.getValueFromVectorParam(
                    datiDITG.get(j), 0).getValue();
                acquisizione = (Long) SqlManager.getValueFromVectorParam(
                    datiDITG.get(j), 1).getValue();

                if(acquisizione==null || (acquisizione!=null && acquisizione.longValue()!=5))
                  //Viene popolata la IMPRDOCG a partire dalle occorrenze di DOCUMGARA,
                  //della gara destinazione, impostando SITUAZDOCI a 2 e PROVENI a 1
                  pgManager.inserimentoDocumentazioneDitta(codgar, lottodestinazione, codiceDitta);
              }


            }

			this.getRequest().setAttribute("RISULTATO", "COPIAESEGUITA");

		} catch (SQLException e) {
			this.getRequest().setAttribute("RISULTATO", "ERRORI");
			throw new GestoreException(
					"Errore durante la copia delle ditte)",
					"copiaDitte", e);
		} catch (Throwable e) {
			this.getRequest().setAttribute("RISULTATO", "ERRORI");
			throw new GestoreException(
					"Errore durante la copia delle ditte)",
					"copiaDitte", e);
		}

	}

}
