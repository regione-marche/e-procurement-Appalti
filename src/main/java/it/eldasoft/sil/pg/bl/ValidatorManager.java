/*
 * Created on 02/04/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.system.ConfigurazioneMail;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sign.DigitalSignatureChecker;
import it.eldasoft.utils.sign.DigitalSignatureException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.upload.FormFile;

/**
 * Classe di gestione delle funzionalita' inerenti il Mercato Elettronico
 *
 * @author Marco.Perazzetta
 */
public class ValidatorManager {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(ValidatorManager.class);

	private MailManager mailManager;

	private static final String ESTENSIONE_FILE_FIRMATO = "P7M";

	/**
	 * Set MailManager
	 *
	 * @param mailManager
	 */
	public void setMailManager(MailManager mailManager) {
		this.mailManager = mailManager;
	}

	public void validateFile(ServletContext context, FormFile file) throws GestoreException {

		long dimensione = 0;
		if (file != null && file.getFileSize() > 0) {
			dimensione = file.getFileSize();
		} else {
			throw new GestoreException("Il file specificato è vuoto. Per continuare specificare un altro file",
							"upload.fileVuoto", null, null);
		}

		TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean(
						"tabellatiManager", context, TabellatiManager.class);

		String dimMaxSingoloFile = tabellatiManager.getDescrTabellato("A1072", "1");
		if (dimMaxSingoloFile == null || "".equals(dimMaxSingoloFile)) {
			throw new GestoreException("Non è presente il tabellato A1072 per determinare la dimensione "
							+ "massima totale dell'upload dei file", "upload.noTabellato", null);
		}
		int pos = dimMaxSingoloFile.indexOf("(");
		if (pos < 1) {
			throw new GestoreException("Non è possibile determinare dal tabellato A1072 la dimensione "
							+ "massima totale dell'upload dei file", "upload.noValore", null);
		}
		dimMaxSingoloFile = dimMaxSingoloFile.substring(0, pos - 1);
		dimMaxSingoloFile = dimMaxSingoloFile.trim();
		double dimMaxSingoloFileByte = Math.pow(2, 20) * Double.parseDouble(dimMaxSingoloFile);
		if (dimensione > dimMaxSingoloFileByte) {
			throw new GestoreException("La dimensione totale dei file da salvare ha superato il limite consentito "
							+ "di " + dimMaxSingoloFile + " MB", "upload.overflow", new String[]{dimensione + " MB"}, null);
		}
	}

	public void validateFiles(String  idcfg, long dimensioneTotaleFiles) throws GestoreException {

		double dimMaxTotaleFilesByte = 0;
		boolean eseguireControlloDimTotale = false;
		String dimMaxTotaleFiles = this.getDimensioneMassimaFile(idcfg);

		//Si deve determinare la dimensione massima dei file già allegati e di quello che si sta allegando
		if (dimMaxTotaleFiles != null && !"".equals(dimMaxTotaleFiles)) {
			dimMaxTotaleFiles = dimMaxTotaleFiles.trim();
			eseguireControlloDimTotale = true;
			dimMaxTotaleFilesByte = Math.pow(2, 20) * Double.parseDouble(dimMaxTotaleFiles);
		}

		if (eseguireControlloDimTotale && dimensioneTotaleFiles > dimMaxTotaleFilesByte) {
			throw new GestoreException("La dimensione totale dei file da allegare supera il limite consentito dal server di posta"
							+ "di " + dimMaxTotaleFiles + " MB", "uploadMultiplo.overflowMultiploAllegati", new String[]{dimMaxTotaleFiles}, null);
		}
	}

	public String getDimensioneMassimaFile(String  idcfg) throws GestoreException {

      String dimMaxTotaleFiles = null;
      try {
        if(idcfg==null || "".equals(idcfg))
          idcfg = CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD;
        ConfigurazioneMail cfg = mailManager.getConfigurazione("PG", idcfg);
        if(cfg!=null)
          dimMaxTotaleFiles = cfg.getDimTotAll();
      } catch (CriptazioneException e) {
        throw new GestoreException("Errore nella lettura della configurazione del server di posta per determinare il limite max in Mb del totale dei file allegati", "letturaConfigurazioneMail", e);
      }

      return dimMaxTotaleFiles;
    }

	public void validateFileFirmato(ServletContext context, FormFile file) throws GestoreException {

		validateFile(context, file);
		checkExtensionFileFirmato(file.getFileName());
		InputStream fileFirmato = null;
		try {
			DigitalSignatureChecker checker = new DigitalSignatureChecker();
			fileFirmato = file.getInputStream();
			boolean signVerified = checker.verifySignature(IOUtils.toByteArray(fileFirmato));
			if (!signVerified) {
				throw new GestoreException("Il file" + file.getFileName() + " non risulta firmato digitalmente o le firme non sono valide",
								"upload.validate.invaliSignature", new String[]{file.getFileName()}, null);
			}
		} catch (FileNotFoundException ex) {
			throw new GestoreException("Impossibile trovare il file " + file.getFileName(),
							"upload.validate.fileNotFound", new String[]{file.getFileName()}, ex);
		} catch (DigitalSignatureException ex) {
			throw new GestoreException("Non e' stato possibile verificare la fima digitale del file",
							"upload.validate.unableToValidate", ex);
		} catch (IOException ex) {
			throw new GestoreException("Si è verificato un errore durante la scrittura del buffer", "upload.bufferError", ex);
		} finally {
			if (fileFirmato != null) {
				try {
					fileFirmato.close();
				} catch (IOException ex) {
				}
			}
		}
	}

	private void checkExtensionFileFirmato(String fileName) throws GestoreException {

		if (fileName != null) {
			String[] estensioni = StringUtils.split(ESTENSIONE_FILE_FIRMATO, ',');
			boolean trovato = false;
			for (String estensione : estensioni) {
				if (fileName.toUpperCase().endsWith(estensione)) {
					trovato = true;
				}
			}
			if (!trovato) {
				throw new GestoreException("Per poter inserire il riepilogo firmato occorre allegare un file con estensione .p7m", "upload.validate.wrongExtension", null);
			}
		}
	}
}
