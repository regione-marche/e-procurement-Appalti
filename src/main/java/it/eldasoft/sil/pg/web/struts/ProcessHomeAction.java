package it.eldasoft.sil.pg.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction;
import it.eldasoft.gene.tags.utils.functions.GetUpperCaseDBFunction;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.sil.pg.tags.funzioni.GetFiltroTipoGaraFunction;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;

public class ProcessHomeAction extends DispatchActionBaseNoOpzioni {

  private static final Logger LOGGER = Logger.getLogger(ProcessHomeAction.class);

  private GeneManager geneManager;

  public void setGeneManager(final GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  public ActionForward trovaGara(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) {
    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaGara: inizio metodo");

    final String href = "gare/v_gare_torn/v_gare_torn-lista.jsp";
    final String entita = "V_GARE_TORN";
    UtilityTags.getUtilityHistory(request.getSession()).addQueriedEntity(entita);
    String findStr = request.getParameter("findstr");
    findStr = findStr == null ? "" : findStr.trim().toUpperCase();
    String where = "(V_GARE_TORN.ISARCHI <> ? OR V_GARE_TORN.ISARCHI IS NULL)";
    String params = "T:1";

    String functionUpperCase = "";
    try {
      functionUpperCase = GetUpperCaseDBFunction.getUpperCaseDb();
    } catch (JspException e) {
      final String messageKey = "errors.applicazione.inaspettataException";
      LOGGER.error(this.resBundleGenerale.getString(messageKey), e);
    }
    if (findStr != null && !findStr.isEmpty()) {
      where += " AND (" + functionUpperCase + "( V_GARE_TORN.CODICE ) like ? OR " + functionUpperCase + "(V_GARE_TORN.OGGETTO ) like ?";
      where += " OR " + functionUpperCase + "(V_GARE_TORN.CODCIG) like ?)";
      params += ";T:%" + findStr + "%;T:%" + findStr + "%;T:%" +  findStr + "%";
    }

    final String filtroProfiloAttivo = (String) request.getSession().getAttribute("filtroProfiloAttivo");
    if (filtroProfiloAttivo != null && !filtroProfiloAttivo.isEmpty()) {
      where += " AND V_GARE_TORN.PROFILOWEB = ?";
      params += ";N:" + filtroProfiloAttivo;
    }

    String filtroTipoGara = "";
    try {
      filtroTipoGara = GetFiltroTipoGaraFunction.getFiltroTipoGara(
          (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO), request.getSession().getServletContext());
    } catch (JspException e) {
      final String messageKey = "errors.applicazione.inaspettataException";
      LOGGER.error(this.resBundleGenerale.getString(messageKey), e);
    }
    if (!filtroTipoGara.isEmpty()) {
      if(where!=null && where !="")
        where += " AND";
      where += " " + filtroTipoGara;
    }

    final ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    final String filtroLivelloUtente = FiltroLivelloUtenteFunction.getFiltroLivelloUtente(entita, profiloUtente);
    if (!filtroLivelloUtente.isEmpty())
      where += " AND " + filtroLivelloUtente;

    final boolean visualizzazioneGareALotti = geneManager.getProfili().checkProtec(
        (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO),
            "FUNZ", "VIS", "ALT.GARE.GARE.GestioneGareALotti", true);
    if (!visualizzazioneGareALotti) {
      where += " AND V_GARE_TORN.GENERE <>1 ";
    }

    final boolean visualizzazioneGareLottiOffUnica = geneManager.getProfili().checkProtec(
        (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO),
            "FUNZ", "VIS", "ALT.GARE.GARE.GestioneGareLottiOffUnica", true);
    if (!visualizzazioneGareLottiOffUnica) {
      where += " AND V_GARE_TORN.GENERE <>3 ";
    }

    final boolean visualizzazioneGareALottoUnico = geneManager.getProfili().checkProtec(
        (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO),
            "FUNZ", "VIS", "ALT.GARE.GARE.GestioneGareALottoUnico", true);
    if (!visualizzazioneGareALottoUnico) {
      where += " AND V_GARE_TORN.GENERE <>2 ";
    }

    UtilityTags.createHashAttributeForSqlBuild(request.getSession(), entita, 0);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, params);

    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaGara: fine metodo");

    return UtilityStruts.redirectToPage(href, false, request);
  }

  public ActionForward trovaTutteLeGare(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) {
    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaTutteLeGare: inizio metodo");

    final String href = "gare/v_gare_profilo/v_gare_profilo-lista.jsp";
    final String entita = "V_GARE_PROFILO";
    String findStr = request.getParameter("findstr");
    findStr = findStr == null ? "" : findStr.toUpperCase();
    String where = "(V_GARE_PROFILO.ISARCHI <> ? OR V_GARE_PROFILO.ISARCHI IS NULL)";
    String params = "T:1";

    String functionUpperCase = "";
    try {
      functionUpperCase = GetUpperCaseDBFunction.getUpperCaseDb();
    } catch (JspException e) {
      final String messageKey = "errors.applicazione.inaspettataException";
      LOGGER.error(this.resBundleGenerale.getString(messageKey), e);
    }
    if(findStr != null && !findStr.isEmpty()){
      where += " AND (" + functionUpperCase + "( V_GARE_PROFILO.CODICE ) like ? OR " + functionUpperCase + "(V_GARE_PROFILO.OGGETTO ) like ?";
      where += " OR " + functionUpperCase + "(V_GARE_PROFILO.CODCIG) like ?)";
      params += ";T:%" + findStr + "%;T:%" + findStr + "%;T:%" + findStr;
    }

    where += " AND V_GARE_PROFILO.CODPROFILO IN (SELECT COD_PROFILO FROM W_ACCPRO WHERE ID_ACCOUNT = ?)";

    final ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    params += "%;T:" + profiloUtente.getId();

    final String uffintAbilitata = ConfigManager.getValore("it.eldasoft.associazioneUffintAbilitata");
    final OpzioniUtente opzioniUtente = new OpzioniUtente(profiloUtente.getFunzioniUtenteAbilitate());
    final CheckOpzioniUtente opzioniAmministratore = new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
    if (uffintAbilitata.equals("1") && !opzioniAmministratore.test(opzioniUtente)) {
      where += " AND CENINT IN (select codein from usr_ein where syscon = ? )";
      params += ";T:" + profiloUtente.getId();
    }

    final String filtroLivelloUtente = FiltroLivelloUtenteFunction.getFiltroLivelloUtente(entita, profiloUtente);
    if (filtroLivelloUtente != null && !filtroLivelloUtente.isEmpty())
      where += " AND " + filtroLivelloUtente;

    UtilityTags.createHashAttributeForSqlBuild(request.getSession(), entita, 0);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, params);

    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaTutteLeGare: fine metodo");

    return UtilityStruts.redirectToPage(href, false, request);
  }

  public ActionForward trovaGaraElencoDitte(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) {
    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaGaraElencoDitte: inizio metodo");

    final String href = "gare/v_gare_eleditte/v_gare_eleditte-lista.jsp";
    final String entita = "V_GARE_ELEDITTE";
    String findStr = request.getParameter("findstr");
    findStr = findStr == null ? "" : findStr.toUpperCase();
    String where = "(V_GARE_ELEDITTE.ISARCHI <> ? OR V_GARE_ELEDITTE.ISARCHI IS NULL)";
    String params = "T:1";

    String functionUpperCase = "";
    try {
      functionUpperCase = GetUpperCaseDBFunction.getUpperCaseDb();
    } catch (JspException e) {
      final String messageKey = "errors.applicazione.inaspettataException";
      LOGGER.error(this.resBundleGenerale.getString(messageKey), e);
    }

    String tipoRicerca = request.getParameter("tipoRicerca");
    if (tipoRicerca.equals("2")) {
      // Ricerca per elenco
      if(findStr != null && !findStr.isEmpty()) {
        where += " AND (" + functionUpperCase + "( V_GARE_ELEDITTE.CODICE ) like ? OR " + functionUpperCase + "(V_GARE_ELEDITTE.OGGETTO ) like ?)";
        params += ";T:%" + findStr + "%;T:%" + findStr + "%";
      }
    } else {
      // Ricerca per operatore
      if(findStr != null && !findStr.isEmpty()) {
          if(StringUtils.isNumeric(findStr)){
            where += " AND EXISTS(select ngara5 from ditg,impr where codgar5=codgar and ngara5=codice and dittao=codimp and pivimp like ?)";
            params += ";T:%" + findStr + "%";
          }else{
            where += " AND EXISTS(select ngara5 from ditg where codgar5=codgar and ngara5=codice and " + functionUpperCase + "(nomimo) like ?)";
            params += ";T:%" + findStr + "%";
          }
      }
    }

    final ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    final String filtroLivelloUtente = FiltroLivelloUtenteFunction.getFiltroLivelloUtente(entita, profiloUtente);
    if (filtroLivelloUtente != null && !filtroLivelloUtente.isEmpty()) {
      where += " AND " + filtroLivelloUtente;
    }

    UtilityTags.createHashAttributeForSqlBuild(request.getSession(), entita, 0);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, params);

    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaGaraElencoDitte: fine metodo");

    return UtilityStruts.redirectToPage(href + "?tipoRicerca=" + tipoRicerca, false, request) ;
  }

  public ActionForward trovaGaraCataloghi(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) {
    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaGaraCataloghi: inizio metodo");

    final String href = "gare/v_gare_catalditte/v_gare_catalditte-lista.jsp";
    final String entita = "V_GARE_CATALDITTE";
    String findStr = request.getParameter("findstr");
    findStr = findStr == null ? "" : findStr.toUpperCase();
    String where = "(V_GARE_CATALDITTE.ISARCHI <> ? OR V_GARE_CATALDITTE.ISARCHI IS NULL)";;
    String params = "T:1";

    String functionUpperCase = "";
    try {
      functionUpperCase = GetUpperCaseDBFunction.getUpperCaseDb();
    } catch (JspException e) {
      final String messageKey = "errors.applicazione.inaspettataException";
      LOGGER.error(this.resBundleGenerale.getString(messageKey), e);
    }

    String tipoRicerca = request.getParameter("tipoRicerca");
    if (tipoRicerca.equals("2")) {
      // Ricerca per elenco
      if(findStr != null && !findStr.isEmpty()) {
        where += " AND (" + functionUpperCase + "( V_GARE_CATALDITTE.CODICE ) like ? OR " + functionUpperCase + "(V_GARE_CATALDITTE.OGGETTO ) like ?)";
        params += ";T:%" + findStr + "%;T:%" + findStr + "%";
      }
    } else {
      // Ricerca per operatore
      if(findStr != null && !findStr.isEmpty()) {
          if(StringUtils.isNumeric(findStr)){
            where += " AND EXISTS(select ngara5 from ditg,impr where codgar5=codgar and ngara5=codice and dittao=codimp and pivimp like ?)";
            params += ";T:%" + findStr + "%";
          }else{
            where += " AND EXISTS(select ngara5 from ditg where codgar5=codgar and ngara5=codice and " + functionUpperCase + "(nomimo) like ?)";
            params += ";T:%" + findStr + "%";
          }
      }
    }

    final ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    final String filtroLivelloUtente = FiltroLivelloUtenteFunction.getFiltroLivelloUtente(entita, profiloUtente);
    if (filtroLivelloUtente != null && !filtroLivelloUtente.isEmpty()) {
      where += " AND " + filtroLivelloUtente;
    }

    UtilityTags.createHashAttributeForSqlBuild(request.getSession(), entita, 0);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, params);

    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaGaraCataloghi: fine metodo");

    return UtilityStruts.redirectToPage(href + "?tipoRicerca=" + tipoRicerca, false, request);
  }

  public ActionForward trovaRicercaMercato(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) {
    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaRicercaMercato: inizio metodo");

    final String href = "gare/meric/meric-lista.jsp";
    final String entita = "MERIC";
    String findStr = request.getParameter("findstr");
    findStr = findStr == null ? "" : findStr.toUpperCase();
    String where = "";
    String params = "";

    String functionUpperCase = "";
    try {
      functionUpperCase = GetUpperCaseDBFunction.getUpperCaseDb();
    } catch (JspException e) {
      final String messageKey = "errors.applicazione.inaspettataException";
      LOGGER.error(this.resBundleGenerale.getString(messageKey), e);
    }

    String tipoRicerca = request.getParameter("tipoRicerca");
    if (tipoRicerca.equals("2")) {
      // Ricerca per ricerca di mercato
      where = "(MERIC.ISARCHI <> ? OR MERIC.ISARCHI IS NULL)";
      params = "T:1";
      if(findStr != null && !findStr.isEmpty()){
          where += " AND (" + functionUpperCase + "( MERIC.CODRIC ) like ? OR " + functionUpperCase + "(MERIC.OGGETTO ) like ?)";
          params += ";T:%" + findStr + "%;T:%" + findStr + "%";
      }
    } else {
      // Ricerca per ordine di acquisto
      if(findStr != null && !findStr.isEmpty()){
          where = " EXISTS(select idric from v_oda where idric=id and (" + functionUpperCase + "(oggetto) like ? or " + functionUpperCase +  "(numoda) like ? or " + functionUpperCase + "(ngara) like ?))";
          params = "T:%" + findStr + "%;T:%" + findStr + "%;T:%" + findStr + "%";
      }
    }

    final ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    final String filtroLivelloUtente = FiltroLivelloUtenteFunction.getFiltroLivelloUtente(entita, profiloUtente);
    if (filtroLivelloUtente != null && !filtroLivelloUtente.isEmpty()) {
      if (!where.isEmpty()) where += " AND ";
      where += filtroLivelloUtente;
    }

    UtilityTags.createHashAttributeForSqlBuild(request.getSession(), entita, 0);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, params);

    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaRicercaMercato: fine metodo");

    return UtilityStruts.redirectToPage(href, false, request);
  }

  public ActionForward trovaAvvisi(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) {
    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaAvvisi: iniziometodo");

    final String href = "gare/gareavvisi/gareavvisi-lista.jsp";
    final String entita = "GAREAVVISI";
    String findStr = request.getParameter("findstr");
    findStr = findStr == null ? "" : findStr.toUpperCase();
    String where = "(GAREAVVISI.ISARCHI <> ? OR GAREAVVISI.ISARCHI IS NULL)";
    String params = "T:1";

    String functionUpperCase = "";
    try {
      functionUpperCase = GetUpperCaseDBFunction.getUpperCaseDb();
    } catch (JspException e) {
      final String messageKey = "errors.applicazione.inaspettataException";
      LOGGER.error(this.resBundleGenerale.getString(messageKey), e);
    }

    if (findStr != null && !findStr.isEmpty()) {
      where += " AND (" + functionUpperCase + "( GAREAVVISI.NGARA ) like ? OR " + functionUpperCase + "(GAREAVVISI.OGGETTO ) like ?)";
      params += ";T:%" + findStr + "%;T:%" + findStr + "%";
    }

    final ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    final String filtroLivelloUtente = FiltroLivelloUtenteFunction.getFiltroLivelloUtente(entita, profiloUtente);
    if (!filtroLivelloUtente.isEmpty()) {
      where += " AND " + filtroLivelloUtente;
    }

    UtilityTags.createHashAttributeForSqlBuild(request.getSession(), entita, 0);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, params);

    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaAvvisi: fine metodo");

    return UtilityStruts.redirectToPage(href, false, request);
  }

  public ActionForward trovaOrdiniNso(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) {
    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaOrdiniNso: inizio metodo");

    final String href = "gare/nso_ordini/nso_ordini-lista.jsp";
    final String entita = "NSO_ORDINI";
    String findStr = request.getParameter("findstr");
    findStr = findStr == null ? "" : findStr.toUpperCase();
    String where = "(NSO_ORDINI.ID <> ?)";
    String params = "T:0";

    String functionUpperCase = "";
    try {
      functionUpperCase = GetUpperCaseDBFunction.getUpperCaseDb();
    } catch (JspException e) {
      final String messageKey = "errors.applicazione.inaspettataException";
      LOGGER.error(this.resBundleGenerale.getString(messageKey), e);
    }

    if (findStr != null && !findStr.isEmpty()) {
      where += " AND (" + functionUpperCase + "( NSO_ORDINI.CODORD) like ? OR " + functionUpperCase + "(NSO_ORDINI.OGGETTO ) like ?)";
      params += ";T:%" + findStr + "%;T:%" + findStr + "%";
    }

    final ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    final String filtroLivelloUtente = FiltroLivelloUtenteFunction.getFiltroLivelloUtente(entita, profiloUtente);
    if (filtroLivelloUtente != null && !filtroLivelloUtente.isEmpty()) {
      where += " AND " + filtroLivelloUtente;
    }

    UtilityTags.createHashAttributeForSqlBuild(request.getSession(), entita, 0);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, params);

    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaOrdiniNso: fine metodo");

    return UtilityStruts.redirectToPage(href, false, request);
  }

  public ActionForward trovaGaraProtocollo(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) {
    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaGaraProtocollo: inizio metodo");

    final String href = "gare/v_gare_nscad/v_gare_nscad-lista.jsp";
    final String entita = "V_GARE_NSCAD";
    String findStr = request.getParameter("findstr");
    findStr = findStr == null ? "" : findStr.toUpperCase();
    String where = "";
    String params = "";

    String functionUpperCase = "";
    try {
      functionUpperCase = GetUpperCaseDBFunction.getUpperCaseDb();
    } catch (JspException e) {
      final String messageKey = "errors.applicazione.inaspettataException";
      LOGGER.error(this.resBundleGenerale.getString(messageKey), e);
    }

    if(findStr != null && !findStr.isEmpty()) {
      where = "(" + functionUpperCase + "( V_GARE_NSCAD.NGARA ) like ? OR " + functionUpperCase + "(V_GARE_NSCAD.OGGETTO ) like ?)";
      params= "T:%" + findStr + "%;T:%" + findStr + "%";
    }

    UtilityTags.createHashAttributeForSqlBuild(request.getSession(), entita, 0);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, params);

    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaGaraProtocollo: fine metodo");

    return UtilityStruts.redirectToPage(href, false, request);
  }

  public ActionForward trovaImprese(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) {
    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaImprese: inizio metodo");

    final String href = "gare/v_ditte_prit/v_ditte_prit-lista.jsp";
    final String entita = "V_DITTE_PRIT";
    final String tipoDoc = request.getParameter("tipoDocSelect");
    final String risultatiPerPagina = request.getParameter("risultatiPerPagina");
    String where = "";
    String params = "";

    if (tipoDoc != null && !tipoDoc.equals("0")) {
      where += "V_DITTE_PRIT.TIPROT = ?";
      params += "N:" + tipoDoc;
    }

    final ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    final String filtroLivelloUtente = FiltroLivelloUtenteFunction.getFiltroLivelloUtente(entita, profiloUtente);
    if (filtroLivelloUtente != null && !filtroLivelloUtente.isEmpty()) {
      if (where.isEmpty())
        where = filtroLivelloUtente;
      else
        where += " AND " + filtroLivelloUtente;
    }

    UtilityTags.createHashAttributeForSqlBuild(request.getSession(), entita, 0);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, params);

    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaImprese: fine metodo");

    return UtilityStruts.redirectToPage(href + "?risultatiPerPagina=" + risultatiPerPagina, false, request);
  }

  public ActionForward trovaDittaAntimafia(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) {
    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaDittaAntimafia: inizio metodo");

    final String href = "gene/impr/impr-lista.jsp";
    final String entita = "IMPR";
    String findStr = request.getParameter("findstr");
    findStr = findStr == null ? "" : findStr.toUpperCase();
    String where = "";
    String params = "";

    String functionUpperCase = "";
    try {
      functionUpperCase = GetUpperCaseDBFunction.getUpperCaseDb();
    } catch (JspException e) {
      final String messageKey = "errors.applicazione.inaspettataException";
      LOGGER.error(this.resBundleGenerale.getString(messageKey), e);
    }

    if(findStr != null && !findStr.isEmpty()) {
      where += " " + functionUpperCase + "( IMPR.NOMEST ) like ? OR " + functionUpperCase + "(IMPR.CFIMP ) like ? OR " + functionUpperCase + "(IMPR.PIVIMP ) like ?";
      params += "T:%" + findStr + "%;T:%" + findStr + "%;T:%" + findStr + "%";
    }

    UtilityTags.createHashAttributeForSqlBuild(request.getSession(), entita, 0);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, params);

    if(LOGGER.isDebugEnabled()) LOGGER.debug("trovaDittaAntimafia: fine metodo");

    return UtilityStruts.redirectToPage(href, false, request);
  }

  public ActionForward trovaStipula(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("trovaStipula: inizio metodo");

    final String href = "gare/v_gare_stipula/v_gare_stipula-lista.jsp";
    final String entita = "V_GARE_STIPULA";
    String findStr = request.getParameter("findstr");
    findStr = findStr == null ? "" : findStr.toUpperCase();
    String where = "(V_GARE_STIPULA.ISARCHI <> ? OR V_GARE_STIPULA.ISARCHI IS NULL)";
    String params = "T:1";

    final ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    final String filtroLivelloUtente = FiltroLivelloUtenteFunction.getFiltroLivelloUtente(entita, profiloUtente);

    String functionUpperCase = "";
    try {
      functionUpperCase = GetUpperCaseDBFunction.getUpperCaseDb();
    } catch (JspException e) {
      final String messageKey = "errors.applicazione.inaspettataException";
      LOGGER.error(this.resBundleGenerale.getString(messageKey), e);
    }

    if(findStr != null && !findStr.isEmpty()){
      where += " AND (" + functionUpperCase + "( V_GARE_STIPULA.CODSTIPULA) like ? OR " + functionUpperCase + "(V_GARE_STIPULA.OGGETTO ) like ?)";
      params += ";T:%" + findStr + "%;T:%" + findStr + "%";
    }

    if (filtroLivelloUtente != "")
      where += " AND " + filtroLivelloUtente;

    UtilityTags.createHashAttributeForSqlBuild(request.getSession(), entita, 0);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, params);

    if (LOGGER.isDebugEnabled()) LOGGER.debug("trovaStipula: fine metodo");

    return UtilityStruts.redirectToPage(href, false, request);
  }

  public ActionForward trovaGaraLotto(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("trovaGaraLotto: inizio metodo");

    final String href = "w3/v_w3gare/v_w3gare-lista.jsp";
    final String entita = "V_W3GARE";
    String findStr = request.getParameter("findstr");
    findStr = findStr == null ? "" : findStr.toUpperCase();
    final String filtroLivelloUtenteW3GARA =
        "V_W3GARE.DATA_CONFERMA_GARA IS NULL AND ((V_W3GARE.TIPO_GARA='S' AND V_W3GARE.STATO_SIMOG<>2) "
        + "OR (V_W3GARE.TIPO_GARA='G' AND V_W3GARE.STATO_SIMOG<>7 AND V_W3GARE.STATO_SIMOG<>6 AND V_W3GARE.STATO_SIMOG<>99)) ";
    String where = "";
    String params = "";

    String functionUpperCase = "";
    try {
      functionUpperCase = GetUpperCaseDBFunction.getUpperCaseDb();
    } catch (JspException e) {
      final String messageKey = "errors.applicazione.inaspettataException";
      LOGGER.error(this.resBundleGenerale.getString(messageKey), e);
    }

    if(findStr != null && !findStr.isEmpty()){
      where += "((" + functionUpperCase + "( v_w3gare.oggetto) like ?) OR (" + functionUpperCase + "(v_w3gare.id_gara) like ?) OR "
              + "(exists (select * from w3lott where v_w3gare.numgara = w3lott.numgara and ((" + functionUpperCase + "(w3lott.oggetto) like ?) OR "
              + "(" + functionUpperCase + "(w3lott.cig) like ?)))))";
      params += "T:%" + findStr + "%;T:%" + findStr + "%;T:%" + findStr + "%;T:%" + findStr + "%";
    }

    if (!where.isEmpty()) {
      where += " AND ";
    }
    where += filtroLivelloUtenteW3GARA;

    if (LOGGER.isDebugEnabled()) LOGGER.debug("trovaGataLotto: fine metodo");

    UtilityTags.createHashAttributeForSqlBuild(request.getSession(), entita, 0);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putAttributeForSqlBuild(request.getSession(), entita, 0, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, params);

    return UtilityStruts.redirectToPage(href, false, request);
  }

}
