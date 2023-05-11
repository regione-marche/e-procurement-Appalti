package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.TransactionStatus;

public class GestorePopupCopiaCommissione extends AbstractGestoreEntita {

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
		
		GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
	        this.getServletContext(), GenChiaviManager.class);

		try {

			String deleteGFOF = "delete from gfof where ngara2 = ?";
			sqlManager.update(deleteGFOF,new Object[] {lottodestinazione});

			String selectGFOF = "select ngara2, codfof, incfof, nomfof, intfof, impfof, impliq, impspe, dliqspe, numcomm,"
			    + "numord, indisponibilita,motivindisp, datarichiesta, dataaccettazione, espgiu, id, sezalbo from gfof where ngara2 = ?";

			List datiGFOF = sqlManager.getListHashMap(selectGFOF, new Object[] {lottosorgente});

			if (datiGFOF != null && datiGFOF.size()>0) {

				DataColumnContainer datiGFOFDestinazione = new DataColumnContainer(sqlManager,"GFOF","select * from GFOF", new Object[] {});

				for (int i = 0; i < datiGFOF.size(); i++) {
					datiGFOFDestinazione.setValoriFromMap((HashMap) datiGFOF.get(i),true);
					datiGFOFDestinazione.setValue("GFOF.NGARA2", lottodestinazione);
					Long id = new Long(genChiaviManager.getNextId("GFOF"));
					datiGFOFDestinazione.setValue("GFOF.ID", id);
					datiGFOFDestinazione.getColumn("GFOF.ID").setChiave(true);
					datiGFOFDestinazione.insert("GFOF", sqlManager);
				}
			}

			this.getRequest().setAttribute("RISULTATO", "COPIAESEGUITA");

		} catch (SQLException e) {
			this.getRequest().setAttribute("RISULTATO", "ERRORI");
			throw new GestoreException(
					"Errore durante la copia dei componenti della commissione)",
					"copiaComponentiCommissione", e);
		} catch (Throwable e) {
			this.getRequest().setAttribute("RISULTATO", "ERRORI");
			throw new GestoreException(
					"Errore durante la copia dei componenti della commissione)",
					"copiaComponentiCommissione", e);
		}

	}

}
