/*
 * Created on 22/giu/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.docass.DocumentiAssociatiManager;
import it.eldasoft.gene.commons.web.TempFileUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.docass.DocumentoAssociato;
import it.eldasoft.gene.web.struts.docass.CostantiDocumentiAssociati;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.ImportExportOEPVManager;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.utils.spring.UtilitySpring;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.transaction.TransactionStatus;

public class GestorePopupExportOEPV extends AbstractGestoreEntita {

	/** Logger */
	static Logger logger = Logger.getLogger(GestorePopupExportOEPV.class);

	/** Manager per la gestione dell'esportazione ed importazione OEPV */
	private ImportExportOEPVManager importExportOEPVManager = null;

	private DocumentiAssociatiManager documentiAssociatiManager;

	@Override
  public String getEntita() {
		return "GARE";
	}

	@Override
  public void setRequest(HttpServletRequest request) {
		super.setRequest(request);
		this.importExportOEPVManager = (ImportExportOEPVManager)
				UtilitySpring.getBean("importExportOEPVManager",
						this.getServletContext(),	ImportExportOEPVManager.class);
		this.documentiAssociatiManager = (DocumentiAssociatiManager)
				UtilitySpring.getBean("documentiAssociatiManager",
						this.getServletContext(),	DocumentiAssociatiManager.class);
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

	@Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
			throws GestoreException {

		String modoRichiamo = datiForm.getString("MODORICHIAMO");
		String ngara = datiForm.getString("NGARA");
		String step =  this.getRequest().getParameter("step");
		String campo  = this.getRequest().getParameter("campo");

		String tmp = null;
		// Lettura del flag che abilita l'insert in C0OGGASS del file Excel che si
		// sta generando
		boolean archiviaXLSDocAss = false;
		if(this.getRequest().getParameter("archiviaXLSDocAss") != null)
			tmp = this.getRequest().getParameter("archiviaXLSDocAss");
		else if(this.getRequest().getAttribute("archiviaXLSDocAss") != null)
			tmp = (String) this.getRequest().getAttribute("archiviaXLSDocAss");
		if("1".equals(tmp))
			archiviaXLSDocAss = true;

		this.getRequest().setAttribute("MODORICHIAMO", modoRichiamo);
		this.getRequest().setAttribute("NGARA", ngara);

		// *** ESPORTAZIONE ***
		if ("ESPORTA".equals(modoRichiamo)) {
			try {
				HSSFWorkbook XLS = importExportOEPVManager.esportazione(ngara,
						archiviaXLSDocAss, null,step,campo);

				int stepAttuale = Integer.parseInt(step);
				String nomeFile = ngara.toUpperCase().replaceAll("/", "_");
				nomeFile += "_" + FilenameUtils.getBaseName(importExportOEPVManager.NOME_FILE_C0OGGASS_EXPORT) + "_";
				if(stepAttuale==GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE)
				  nomeFile += "ECO.";
				else
				  nomeFile += "TEC.";
				nomeFile += FilenameUtils.getExtension(importExportOEPVManager.NOME_FILE_C0OGGASS_EXPORT);
				File tempFile = TempFileUtilities.getTempFileSenzaNumeoRandom(nomeFile,
						this.getRequest().getSession());

				FileOutputStream fos = new FileOutputStream(tempFile);
				// Scrittura dell'oggetto workBook nel file temporaneo
				XLS.write(fos);
				fos.close();

				if(archiviaXLSDocAss){
					String moduloAttivo = (String) this.getRequest().getSession().getAttribute(
							CostantiGenerali.MODULO_ATTIVO);
					DocumentoAssociato docAss = new DocumentoAssociato();
					docAss.setEntita("GARE");
					docAss.setCodApp(moduloAttivo);
					docAss.setCampoChiave1(ngara);
					docAss.setCampoChiave2("#");
					docAss.setCampoChiave3("#");
					docAss.setCampoChiave4("#");
					docAss.setCampoChiave5("#");
					docAss.setDataInserimento(new Date());

			        if(stepAttuale==GestioneFasiGaraFunction.FASE_APERTURA_OFFERTE_ECONOMICHE)
			          docAss.setTitolo(ImportExportOEPVManager.TITOLO_FILE_C0OGASS_EXPORT_ECONOMICI);
			        else
			          docAss.setTitolo(ImportExportOEPVManager.TITOLO_FILE_C0OGASS_EXPORT_TECNICI);

					docAss.setNomeDocAss(ImportExportOEPVManager.NOME_FILE_C0OGGASS_EXPORT);
					docAss.setPathDocAss(CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT);

					this.documentiAssociatiManager.associaFile(docAss, FileUtils.readFileToByteArray(tempFile));
				}
				this.getRequest().setAttribute("nomeFileExcel", tempFile.getName());
				this.getRequest().setAttribute("RISULTATO", "OPERAZIONEESEGUITA");
			} catch (GestoreException e) {
				this.getRequest().setAttribute("RISULTATO", "ERRORI");
				throw e;
			} catch (Throwable e) {
				this.getRequest().setAttribute("RISULTATO", "ERRORI");
				throw new GestoreException("Errore durante l'esportazione in Excel",
						"importaesportaexcel.esporazioneexcel", e);
			}
		}
	}

}