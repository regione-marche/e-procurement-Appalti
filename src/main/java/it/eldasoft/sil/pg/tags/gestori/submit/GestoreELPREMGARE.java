package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityNumeri;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

public class GestoreELPREMGARE extends AbstractGestoreEntita {

	static Logger logger = Logger.getLogger(GestoreELPREMGARE.class);

	public static String queryQuesitiPunteggiELPREMGARE = "select elpremgare.id, elpremgare.idquesito, elpremgare.idopzione,"
			+ " elpremalbo.pesoponmin,  elpremalbo.pesoponmax, elpremgare.pesopon, elpremgare.pesoponr"
			+ " from elpremgare,elpremalbo where elpremgare.ngara=? and elpremgare.idquesito=elpremalbo.idquesito and elpremgare.idopzione=elpremalbo.idopzione"
			+ " and elpremalbo.ngara=(select elencoe from gare where ngara=?)"
			+ " order by elpremgare.idquesito, elpremgare.id";

	@Override
	public String getEntita() {
		return "ELPREMGARE";
	}

	@Override
	public void setRequest(HttpServletRequest request) {
		super.setRequest(request);
	}

	public GestoreELPREMGARE() {
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
	public void preDelete(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

		if (logger.isDebugEnabled()) {
			logger.debug("GestoreELPREMGARE.preDelete: inizio metodo");
		}

		try {
			String ngara = this.getRequest().getParameter("ngara");
			Long id = (Long) impl.getColumn("ELPREMGARE.ID").getValue().getValue();
			Long idquesito = (Long) sqlManager.getObject("select idquesito from elpremgare where id=?", new Object[] {id});
			this.getGeneManager().deleteTabelle(new String[]{this.getEntita()}, "NGARA = ? and IDQUESITO = ?", new Object[]{ngara, idquesito});
		} catch (SQLException e) {
			throw new GestoreException(e.getMessage(), null, e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("GestoreELPREMGARE.preDelete: fine metodo");
		}
	}

	@Override
	public void preInsert(TransactionStatus status, DataColumnContainer impl) throws GestoreException {
	}

	@Override
	public void preUpdate(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

		if (logger.isDebugEnabled()) {
			logger.debug("GestoreELPREMGARE.preUpdate: inizio metodo");
		}

		try {

			int numeroRighe = 0;
			String numRighe = this.getRequest().getParameter("numeroRighe");
			if (numRighe != null && numRighe.length() > 0) {
				numeroRighe = UtilityNumeri.convertiIntero(this.getRequest().getParameter("numeroRighe")).intValue();
			}
			for (int i = 1; i <= numeroRighe; i++) {
				DataColumnContainer dataColumnContainerDiRiga = new DataColumnContainer(impl.getColumnsBySuffix("_" + i, false));
				if (dataColumnContainerDiRiga.isModifiedTable("ELPREMGARE"))
					dataColumnContainerDiRiga.update("ELPREMGARE", sqlManager);
			}
		} catch (Exception e) {
			throw new GestoreException(e.getMessage(), null, e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("GestoreELPREMGARE.preUpdate: fine metodo");
		}
	}
}
