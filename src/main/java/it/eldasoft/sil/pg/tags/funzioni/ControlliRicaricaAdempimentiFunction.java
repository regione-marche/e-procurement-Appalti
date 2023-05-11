/*
 * Created on 21/10/14
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.funzioni;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

/**
 * Funzione che effettua il controllo sulla duplicazione dei codici CIG
 *
 * @author Marcello Caminiti
 */
public class ControlliRicaricaAdempimentiFunction extends AbstractFunzioneTag {

  public ControlliRicaricaAdempimentiFunction() {
    super(2, new Class[] { PageContext.class, String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String esito="ok";

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ufficioIntestatario = null;
    HttpSession session = this.getRequest().getSession();
    if (session != null) {
      ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
    }

    String id = (String) params[1];
    Long idAnticor = new Long(id);
    Long annorif = null;

    try {
	    annorif = (Long) sqlManager.getObject("select annorif from anticor where id=?",
	    		new Object[] { idAnticor });
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new JspException("Errore nella lettura di ANTICOR.ANNORIF", e);
    }

    java.util.Date dataInizio = null;
    if (annorif.intValue() == 2013)
    	dataInizio = UtilityDate.convertiData("01/12/2012",
    			UtilityDate.FORMATO_GG_MM_AAAA);
    else
    	dataInizio = UtilityDate.convertiData("01/01/" + annorif.toString(),
    			UtilityDate.FORMATO_GG_MM_AAAA);

    java.util.Date dataFine = UtilityDate.convertiData("31/12/" + annorif.toString(),
        UtilityDate.FORMATO_GG_MM_AAAA);

    String select = "select distinct upper(cig) from v_dati_lotti where (((datpub >= ? and  datpub <= ? "
    	+ "or (datpub is null and (dinvit >= ? and  dinvit <= ?))) "
    	+ "or (esineg is not null and datneg >= ? and  datneg <= ?) "
    	+ "or (dattoa >= ? and  dattoa <= ?) "
    	+ "or (dattoa is not null and  ((datainizio >=? and  datainizio <= ?)" +
    			" or (dataultimazione >=? and dataultimazione<=?))))) ";

    if (ufficioIntestatario != null)
      select += " and codiceprop='" + ufficioIntestatario + "'";

    String selectElencoCigDuplicati="select upper(cig) from v_dati_lotti where cig is not null group by upper(cig) having(count(*)>1)";

    try {
      //Si deve controllare che nella banca dati non vi siano lotti con uguale cig a quello dei lotti che si stanno importando
      String selectControlloDB = select + " and cig in (" + selectElencoCigDuplicati + ")";
      List<?> listaCigDuplicati = sqlManager.getListVector(
          selectControlloDB, new Object[]{dataInizio, dataFine, dataInizio, dataFine, dataInizio, dataFine,
              dataInizio, dataFine, dataInizio, dataFine, dataInizio, dataFine,});
      if (listaCigDuplicati != null && listaCigDuplicati.size() > 0) {
        String msg = "";
        for (int i = 0; i < listaCigDuplicati.size(); i++) {
          String cig = SqlManager.getValueFromVectorParam(
              listaCigDuplicati.get(i), 0).getStringValue();
          if (i > 0) msg += ", ";
          msg += cig;
        }
        esito = "nok";
        pageContext.setAttribute("CIGDuplicatiInDB",msg,PageContext.REQUEST_SCOPE);
      }
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new JspException(
          "Errore nella valutazione dell'esistenza CIG duplicati nel DB ", e);
    }

    // Prima di procedere si deve controllare se vi sono lotti con LOTTOINBO=2 con cig uguale a quello dei lotti
    // che si stanno importando. Vengono esclusi i lotti con CIG duplicato già scartati dal primo controllo
    select += " and cig in (select cig from anticorlotti where idanticor=? and lottoinbo='2') " +
    		"and cig not in (" + selectElencoCigDuplicati  + ")";

    try {
      List<?> listaCigDuplicati = sqlManager.getListVector(
          select, new Object[]{
              dataInizio, dataFine, dataInizio, dataFine, dataInizio, dataFine,
              dataInizio, dataFine, dataInizio, dataFine, dataInizio, dataFine, id });
      if (listaCigDuplicati != null && listaCigDuplicati.size() > 0) {
        String msg = "";
        for (int i = 0; i < listaCigDuplicati.size(); i++) {
          String cig = SqlManager.getValueFromVectorParam(
              listaCigDuplicati.get(i), 0).getStringValue();
          if (msg.indexOf(cig) < 0) {
            if (i > 0) msg += ", ";
            msg += cig;
          }
        }
        esito = "nok";
       pageContext.setAttribute("CIGDuplicatiLotti",msg,PageContext.REQUEST_SCOPE);
      }
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new JspException("Errore nella ricerca dei cig duplicati", e);
    }

    return esito;
  }

}