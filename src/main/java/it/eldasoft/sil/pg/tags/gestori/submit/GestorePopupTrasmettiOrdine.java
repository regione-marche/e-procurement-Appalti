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

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.sil.pg.bl.ValidatorManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import com.lowagie.text.DocumentException;

/**
 * Gestore popup per la trasmissione di un ordine di acquisto
 *
 * @author Marco.Perazzetta
 */
public class GestorePopupTrasmettiOrdine extends AbstractGestoreEntita {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(GestorePopupTrasmettiOrdine.class);

	@Override
	public String getEntita() {
		return "GARECONT";
	}

	public GestorePopupTrasmettiOrdine() {
		super(false);
	}

	public GestorePopupTrasmettiOrdine(boolean isGestoreStandard) {
		super(isGestoreStandard);
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

		if (logger.isDebugEnabled()) {
			logger.debug("GestoreW_DOCDIG: preInsert: inizio metodo");
		}

		ValidatorManager validatorManager = (ValidatorManager) UtilitySpring.getBean(
						"validatorManager", this.getServletContext(), ValidatorManager.class);

		String uffintAbilitato = (String)this.getRequest().getSession().getAttribute("uffint");
        uffintAbilitato = UtilityStringhe.convertiNullInStringaVuota(uffintAbilitato);
        if("".equals(uffintAbilitato)){
          uffintAbilitato = CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD;
        }
        //Si deve controllare che la dimensione totale dei file allegati non superi il massimo specificato
        //fra le proprietà di configurazione della email.
        //Oltre il file direttamente  allegato tramite la popup, si devono considerare i documenti allegati
        //all'ordine (GRUPPO=11 and allmail=1)
        String selectAllegatiOrdine = "select descrizione, dignomdoc, d.IDPRG, d.IDDOCDG from DOCUMGARA d,W_DOCDIG w "
            + "where CODGAR=? and NGARA = ? and GRUPPO = ? and d.IDPRG=w.IDPRG and d.IDDOCDG = w.IDDOCDIG and allmail=? order by norddocg";

        String codgar = datiForm.getString("GARE.CODGAR1");
        String nGara = datiForm.getString("GARECONT.NGARA");
        String idcfg = null;
        //Valorizzazione di IDCFG
        try {
          String cenint = (String)sqlManager.getObject("select t.cenint from gare g,torn t where g.codgar1 = t.codgar and ngara=?", new Object[]{nGara});
          cenint = UtilityStringhe.convertiNullInStringaVuota(cenint);
          if(!"".equals(cenint)){
            idcfg = cenint;
          }else{
            idcfg = uffintAbilitato;
          }
        } catch (SQLException sqle) {
          this.getRequest().setAttribute("ordineTrasmesso", "Errori");
          throw new GestoreException("Errore nella lettura di TORN.CENINT",null, sqle);
        }


        try {
            long dimTotaleAllegati = 0;

            List listaDocumenti = sqlManager.getListVector(selectAllegatiOrdine, new Object[]{codgar, nGara, new Long(11),"1"});
            if(listaDocumenti!=null && listaDocumenti.size()>0){

              FileAllegatoManager fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean(
                  "fileAllegatoManager", this.getServletContext(), FileAllegatoManager.class);
              for(int i=0;i<listaDocumenti.size();i++){
                Long iddocdig = (Long)SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 3).getValue();
                String idprg = (String)SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 2).getValue();
                BlobFile fileAllegatoBlob = fileAllegatoManager.getFileAllegato(idprg, iddocdig);

                if(fileAllegatoBlob!=null)
                  dimTotaleAllegati += fileAllegatoBlob.getStream().length;

              }
            }
            dimTotaleAllegati +=this.getForm().getSelezioneFile().getFileSize();
            validatorManager.validateFiles(idcfg,dimTotaleAllegati);
    		validatorManager.validateFileFirmato(this.getServletContext(), this.getForm().getSelezioneFile());


    		byte[] file = null;
    		if (datiForm.isColumn("FDIGNOMDOC")
    						&& datiForm.getString("FDIGNOMDOC") != null
    						&& !datiForm.getString("FDIGNOMDOC").trim().equals("")) {

    				file = this.getForm().getSelezioneFile().getFileData();

    		}


			ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
							CostantiGenerali.PROFILO_UTENTE_SESSIONE);
			MEPAManager mepaManager = (MEPAManager) UtilitySpring.getBean(
							"mepaManager", this.getServletContext(), MEPAManager.class);

			String ragioneSocialeOperatore = datiForm.getString("GARE.NOMIMA");
			String destinatarioMail = datiForm.getString("FDESMAIL");
			String oggettoMail = datiForm.getString("FCOMMSGOGG");
			String abilitaIntestazioneVariabile = datiForm.getString("FCOMINTEST");
			String testoInHtml = datiForm.getString("FCOMMSGTIP");
			String testoMail = datiForm.getString("FCOMMSGTES");
			String mittenteMail = datiForm.getString("FCOMMITT");
			String flagMailPec = datiForm.getString("FMAILPEC");
			Long nCont = datiForm.getLong("GARECONT.NCONT");
			String nomeEntita = this.getRequest().getParameter("nomeEntita");
			String codImpr = datiForm.getString("CODIMP");
			String nomeFile = datiForm.getString("FDIGNOMDOC");
			String integrazioneWSDM = this.getRequest().getParameter("integrazioneWSDM");

			HashMap<String,String> datiWSDM = null;
			if("1".equals(integrazioneWSDM)){
		        //Popolamento contenitore dati per  WSDM
		        datiWSDM = new  HashMap<String, String>();
		        datiWSDM.put("classificadocumento",this.getRequest().getParameter("classificadocumento"));
		        datiWSDM.put("tipodocumento",this.getRequest().getParameter("tipodocumento"));
		        datiWSDM.put("oggettodocumento",this.getRequest().getParameter("oggettodocumento"));
		        datiWSDM.put("descrizionedocumento", this.getRequest().getParameter("descrizionedocumento"));
		        datiWSDM.put("mittenteinterno",this.getRequest().getParameter("mittenteinterno"));
		        datiWSDM.put("codiceregistrodocumento",this.getRequest().getParameter("codiceregistrodocumento"));
		        datiWSDM.put("inout",this.getRequest().getParameter("inout"));
		        datiWSDM.put("idindice",this.getRequest().getParameter("idindice"));
		        datiWSDM.put("idtitolazione",this.getRequest().getParameter("idtitolazione"));
		        datiWSDM.put("idunitaoperativamittente",this.getRequest().getParameter("idunitaoperativamittente"));
		        datiWSDM.put("inserimentoinfascicolo",this.getRequest().getParameter("inserimentoinfascicolo"));
		        datiWSDM.put("codicefascicolo",this.getRequest().getParameter("codicefascicolo"));
		        datiWSDM.put("oggettofascicolo",this.getRequest().getParameter("oggettofascicolonuovo"));
		        datiWSDM.put("classificafascicolo",this.getRequest().getParameter("classificafascicolonuovo"));
		        datiWSDM.put("descrizionefascicolo",this.getRequest().getParameter("descrizionefascicolonuovo"));
		        datiWSDM.put("annofascicolo",this.getRequest().getParameter("annofascicolo"));
		        datiWSDM.put("numerofascicolo",this.getRequest().getParameter("numerofascicolo"));
		        datiWSDM.put("username",this.getRequest().getParameter("username"));
		        String password = this.getRequest().getParameter("password");
		        if(password==null)
		          password="";
		        datiWSDM.put("password",password);
		        datiWSDM.put("ruolo",this.getRequest().getParameter("ruolo"));
		        datiWSDM.put("nome",this.getRequest().getParameter("nome"));
		        datiWSDM.put("cognome",this.getRequest().getParameter("cognome"));
		        datiWSDM.put("codiceuo",this.getRequest().getParameter("codiceuo"));
		        datiWSDM.put("idutente",this.getRequest().getParameter("idutente"));
		        datiWSDM.put("idutenteunop",this.getRequest().getParameter("idutenteunop"));
		        datiWSDM.put("key1",this.getRequest().getParameter("key1"));
		        datiWSDM.put("indirizzomittente", this.getRequest().getParameter("indirizzomittente"));
		        datiWSDM.put("mezzoinvio",this.getRequest().getParameter("mezzoinvio"));
		        datiWSDM.put("codiceaoo",this.getRequest().getParameter("codiceaoonuovo"));
		        datiWSDM.put("codiceufficio",this.getRequest().getParameter("codiceufficionuovo"));
		        datiWSDM.put("mezzo",this.getRequest().getParameter("mezzo"));
		        datiWSDM.put("societa",this.getRequest().getParameter("societa"));
		        datiWSDM.put("codiceGaralotto",this.getRequest().getParameter("codicegaralotto"));
		        datiWSDM.put("cig",this.getRequest().getParameter("cig"));
		        datiWSDM.put("supporto",this.getRequest().getParameter("supporto"));
		        datiWSDM.put("struttura",this.getRequest().getParameter("strutturaonuovo"));
		        datiWSDM.put("tipofascicolo",this.getRequest().getParameter("tipofascicolonuovo"));
		        datiWSDM.put("idconfi",this.getRequest().getParameter("idconfi"));
		        datiWSDM.put("servizio","FASCICOLOPROTOCOLLO");
		        datiWSDM.put("classificadescrizione",this.getRequest().getParameter("classificadescrizione"));
		        datiWSDM.put("voce",this.getRequest().getParameter("voce"));
		        datiWSDM.put("RUP",this.getRequest().getParameter("RUP"));
		        datiWSDM.put("nomeRup",this.getRequest().getParameter("nomeR"));
		        datiWSDM.put("acronimoRup",this.getRequest().getParameter("acronimoR"));
		        datiWSDM.put("sottotipo",this.getRequest().getParameter("sottotipo"));
		    }

			mepaManager.inviaComunicazioneOrdine(profilo, nGara, codgar, nomeEntita, nCont,
							codImpr, ragioneSocialeOperatore, destinatarioMail, oggettoMail,
							abilitaIntestazioneVariabile, testoInHtml, testoMail,
							mittenteMail, idcfg, flagMailPec, nomeFile, file,
							integrazioneWSDM, datiWSDM, listaDocumenti);

			this.getRequest().setAttribute("ordineTrasmesso", "SI");

		} catch (SQLException e) {
		  this.getRequest().setAttribute("ordineTrasmesso", "Errori");
		  throw new GestoreException("Errore nella procedura di abilitazione degli operatori", null, e);
		}catch (FileNotFoundException ex) {
		  this.getRequest().setAttribute("ordineTrasmesso", "Errori");
          throw new GestoreException("Impossibile trovare il file " + this.getForm().getSelezioneFile().getFileName(),
              "upload.validate.fileNotFound", new String[]{this.getForm().getSelezioneFile().getFileName()}, ex);
		} catch (IOException ex) {
		  this.getRequest().setAttribute("ordineTrasmesso", "Errori");
		  throw new GestoreException("Si è verificato un errore durante la scrittura del buffer", "upload.bufferError", ex);
		}catch (GestoreException e){
		  this.getRequest().setAttribute("ordineTrasmesso", "Errori");
		  throw e;
		}catch (DocumentException ex) {
          this.getRequest().setAttribute("ordineTrasmesso", "Errori");
          throw new GestoreException("Si è verificato un errore durante la scrittura del buffer", "upload.bufferError", ex);
        }
	}
}
