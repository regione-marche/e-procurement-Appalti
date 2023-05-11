/*
 * Created on 07/mag/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.tags.funzioni.GestioneFasiGaraFunction;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore operazioni di ritiro definitivo dei plichi 
 * 
 * @author Marcello.Caminiti
 */
public class GestoreOperazioniRitiro extends AbstractGestoreEntita {

  public String getEntita() {
    return "DITG";
  }

  public GestoreOperazioniRitiro() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestoreOperazioniRitiro(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

    int numeroDitte = 0;
    String numDitte = this.getRequest().getParameter("numeroDitte");
    if (numDitte != null && numDitte.length() > 0)
      numeroDitte = UtilityNumeri.convertiIntero(numDitte).intValue();

    Long numeroFaseAttiva = new Long(UtilityStruts.getParametroString(
        this.getRequest(), GestioneFasiGaraFunction.PARAM_WIZARD_PAGINA_ATTIVA));

    boolean isListaModificata = false;

    for (int i = 1; i <= numeroDitte; i++) {
      DataColumn[] campiRitiro = impl.getColumnsBySuffix("_" + i, false);
      DataColumnContainer newImpl = new DataColumnContainer(campiRitiro);

      String codiceDitta = newImpl.getString("V_DITTE_PRIT.DITTAO");
      String numeroGara = newImpl.getString("V_DITTE_PRIT.CAMPOFITTIZIONGARA");
      String codiceGara = newImpl.getString("V_DITTE_PRIT.CAMPOFITTIZIOCODGAR");
      int codiceRitiro = 0;
      isListaModificata = false;

      
      if (newImpl.getColumn("V_DITTE_PRIT.RITIRO_FASE1").isModified()) {
        codiceRitiro = 1;
        isListaModificata = true;
      }

      if (newImpl.getColumn("V_DITTE_PRIT.RITIRO_FASE2").isModified()) {
        codiceRitiro = 0;
        isListaModificata = true;
      }

      if (isListaModificata == true)
        updateDITG_Ritiro(codiceGara, numeroGara, codiceDitta, codiceRitiro,
            numeroFaseAttiva,
            newImpl.getColumn("V_DITTE_PRIT.CAMPOFITTIZIOTIPPROT"));
    }
  }

  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

  private void updateDITG_Ritiro(String codiceGara, String numeroGara,
      String codiceImpresa, int valoreRitiro, Long numeroFaseAttiva,
      DataColumn campoV_DITTE_PRIT_TIPPROT) throws GestoreException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);

    String campoRitiro = "";
    String campoDataRitiro = "";
    String campoOraRitiro = "";
    String campoIdUtenteRitiro = "";

    Long campoTIPPROT = (Long) campoV_DITTE_PRIT_TIPPROT.getValue().getValue();

    switch (campoTIPPROT.intValue()) {
    case 1:
      campoRitiro = "RITDOM";
      campoDataRitiro = "DATRDOM";
      campoOraRitiro = "ORARDOM";
      campoIdUtenteRitiro = "SCRDOM";
      break;
    case 2:
      campoRitiro = "RITOFF";
      campoDataRitiro = "DATROFF";
      campoOraRitiro = "ORAROFF";
      campoIdUtenteRitiro = "SCROFF";
      break;
    case 3:
      campoRitiro = "RITREQ";
      campoDataRitiro = "DATRREQ";
      campoOraRitiro = "ORARREQ";
      campoIdUtenteRitiro = "SCRREQ";
      break;
    }

    try {
      StringBuffer sqlUpdate = new StringBuffer("update DITG set ");
      sqlUpdate.append(campoRitiro + " = ?, ");
      sqlUpdate.append(campoDataRitiro + " = ?, ");
      sqlUpdate.append(campoOraRitiro + " = ?, ");
      sqlUpdate.append(campoIdUtenteRitiro + " = ? ");
      sqlUpdate.append("where CODGAR5 = ? and NGARA5 = ? AND DITTAO = ?");
      
      String orario = null;
      Long idUtente = null;
      Date date = null;

      if (numeroFaseAttiva.equals(new Long(1))) {
        GregorianCalendar calendar = new GregorianCalendar();
        date = new Date();

        String data = UtilityDate.convertiData(date, UtilityDate.FORMATO_GG_MM_AAAA);
        date = UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA);

        orario = "";
        
        int ore = calendar.get(GregorianCalendar.HOUR);
        int minuti = calendar.get(GregorianCalendar.MINUTE);

        if (calendar.get(GregorianCalendar.AM_PM) != 0) ore += 12;

        orario = UtilityStringhe.fillLeft(Integer.toString(ore), '0', 2)
            + ":"
            + UtilityStringhe.fillLeft(Integer.toString(minuti), '0', 2);

        ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);

        idUtente = new Long(profilo.getId());
      }

      sqlManager.update(sqlUpdate.toString(), new Object[]{
        new Long(valoreRitiro), date, orario, idUtente, codiceGara,
        numeroGara, codiceImpresa }, 1); 
      

    } catch (SQLException s) {
      throw new GestoreException(
          "Errore nell'operazione di aggiornamento del campo DITG."
              + campoRitiro, null, s);
    }
  }

}