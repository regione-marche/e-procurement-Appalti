/*
 * Created on 19/05/22
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

/**
 * Gestore non standard per la sospensione di una gara
 *
 * @author Riccardo.Peruzzo
 */
public class GestorePopupSospendiGara extends AbstractGestoreEntita {

  /** Manager Chiavi */
  private GenChiaviManager genChiaviManager;

  public GestorePopupSospendiGara() {
    super(false);
  }

  @Override
  public String getEntita() {
    return "GARSOSPE";
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

	ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
	Long syscon = new Long(profilo.getId());

    // lettura dei parametri di input
    String opz = datiForm.getString("OPZ");
    String codgar = datiForm.getString("CODGARA");
    String note = datiForm.getString("NOTE");

    genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
            this.getServletContext(), GenChiaviManager.class);

	String oggEvento = codgar;
	Date datpubStipula = null;

	if(codgar.indexOf("$")>=0) {
		oggEvento=codgar.substring(codgar.indexOf("$")+1);
	}

	int livEvento = 1;
	String codEvento = "GA_SOSPENDI_GARA";
	String descEvento = "";
	String msgErr = "";

	if ("1".equals(opz)) {
		descEvento = "Attiva sospensione gara";
	} else if ("2".equals(opz)) {
		descEvento = "Disattiva sospensione gara";
	}

	String update = "";
	Object par[] = null;

	if ("1".equals(opz)) {
		Long id = new Long(this.genChiaviManager.getNextId("GARSOSPE"));
		update = "insert into GARSOSPE (id, codgar, datini, note, sysconini) values (?, ?, ?, ?, ?)";
		par = new Object[] { id, codgar, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), note, syscon };
	} else {
		update = "update GARSOSPE set DATFINE =?, SYSCONFINE = ? where CODGAR=? and datfine is null";
		par = new Object[] { new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), syscon, codgar };
	}

	try {
		this.sqlManager.update(update, par);
	} catch (Exception e) {
		livEvento = 3;
		msgErr = e.getMessage();
		throw new GestoreException(
		          "Errore durante l'aggiornamento della tabella campo GARSOSP",null,  e);
	} finally {
		LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
		logEvento.setLivEvento(livEvento);
		logEvento.setOggEvento(oggEvento);
		logEvento.setCodEvento(codEvento);
		logEvento.setDescr(descEvento);
		logEvento.setErrmsg(msgErr);
		LogEventiUtils.insertLogEventi(logEvento);
	}

    // setta l'operazione a completata, in modo da scatenare il reload della
    // pagina principale
    this.getRequest().setAttribute("esito", "1");
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
