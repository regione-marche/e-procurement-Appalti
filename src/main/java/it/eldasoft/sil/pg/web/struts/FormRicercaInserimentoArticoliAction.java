package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.sil.pg.web.struts.sigla.Articolo;
import it.eldasoft.sil.pg.web.struts.sigla.ObjpaginazioneListaArticoli;
import it.eldasoft.sil.pg.web.struts.sigla.TrovaArticoloForm;
import it.maggioli.eldasoft.ws.conf.WSERPConfigurazioneOutType;
import it.maggioli.eldasoft.ws.erp.WSERPPageClauseType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaType;
import it.maggioli.eldasoft.ws.erp.WSERPSearchClauseType;

/**
 * Action per la ricerca e selezione degli articoli. 
 * 
 * @author Alex.Mancini
 */
public class FormRicercaInserimentoArticoliAction extends DispatchActionBaseNoOpzioni {

  protected final String FORWARD_VISUALIZZA = "visualizza";

  static Logger                 logger             = Logger.getLogger(FormRicercaInserimentoArticoliAction.class);

  private SqlManager sqlManager;
  private GestioneWSERPManager gestioneWSERPManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }


  public ActionForward apri(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("FormRicercaInserimentoArticoliAction: inizio metodo apri");
    }

    String target = FORWARD_VISUALIZZA;

    // lista per il popolamento della comboBox dei valori della combobox di
    // confronto fra stringhe
    request.setAttribute("listaValueConfrontoStringa",
        new String[] {"=", "contiene", "iniziaPer", "terminaPer", "nonNullo", "nullo" });
    // lista per il popolamento della comboBox dei testi della combobox di
    // confronto fra stringhe
    request.setAttribute("listaTextConfrontoStringa",
        new String[] {"uguale", "contiene", "inizia per", "termina per", "valorizzato", "non valorizzato" });

    request.setAttribute("listaRisPerPagina", CostantiGenerali.CBX_RIS_PER_PAGINA);
    
    request.setAttribute("booleani", new String[] {"", "true", "false"});
    request.setAttribute("booleaniLabel", new String[] {"", "Si", "No"});

    String messageKey = null;

    try {

      // Se c'è in sessione l'oggetto trova account la carico nel request per
      // predisporre la pagina di trova, altrimenti lo creo
      if (request.getSession().getAttribute("trovaArticoli") != null) {
        request.setAttribute("trovaArticoloForm",
            request.getSession().getAttribute(
                "trovaArticoli"));
      } else {
        TrovaArticoloForm trovaArticoloForm = new TrovaArticoloForm();
        request.setAttribute("trovaArticoloForm", trovaArticoloForm);
      }

    } catch (Exception e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (messageKey != null)
      response.reset();

    if (logger.isDebugEnabled()) {
      logger.debug("FormRicercaInserimentoArticoliAction: fine metodo apri");
    }

    return mapping.findForward(target);

  }
  
  public ActionForward apriNuovo(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("FormRicercaInserimentoArticoliAction: inizio metodo apri");
    }

    String target = FORWARD_VISUALIZZA;

    // lista per il popolamento della comboBox dei valori della combobox di
    // confronto fra stringhe
    request.setAttribute("listaValueConfrontoStringa",
        new String[] {"=", "contiene", "iniziaPer", "terminaPer", "nonNullo", "nullo" });
    // lista per il popolamento della comboBox dei testi della combobox di
    // confronto fra stringhe
    request.setAttribute("listaTextConfrontoStringa",
        new String[] {"uguale", "contiene", "inizia per", "termina per", "valorizzato", "non valorizzato" });

    request.setAttribute("listaRisPerPagina", CostantiGenerali.CBX_RIS_PER_PAGINA);
    
    request.setAttribute("booleani", new String[] {"", "true", "false"});
    request.setAttribute("booleaniLabel", new String[] {"", "Si", "No"});
    String messageKey = null;

    try {
      String provenienzaLista = (String) request.getSession().getAttribute("provengoDallaLista");
      if (provenienzaLista != null && !"Si".equals(provenienzaLista)) {
        TrovaArticoloForm trovaArticoloForm = new TrovaArticoloForm();
        request.setAttribute("trovaArticoloForm", trovaArticoloForm);
      } else {
        if (request.getSession().getAttribute("trovaArticoli") != null) {
          request.setAttribute("trovaArticoloForm",
              request.getSession().getAttribute(
                  "trovaArticoli"));
        } else {
          TrovaArticoloForm trovaArticoloForm = new TrovaArticoloForm();
          request.setAttribute("trovaArticoloForm", trovaArticoloForm);
        }
        request.getSession().setAttribute("provengoDallaLista","No");
      }
    } catch (Exception e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (messageKey != null)
      response.reset();

    if (logger.isDebugEnabled()) {
      logger.debug("FormRicercaInserimentoArticoliAction: fine metodo apri");
    }

    return mapping.findForward(target);

  }



  public ActionForward trova(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException, GestoreException {

    if (logger.isDebugEnabled()) {
      logger.debug("FormRicercaInserimentoArticoliAction: inizio metodo trova");
    }
    String target = "success";
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    String messageKey = null;
    boolean cambioPaginaRicerca = false;
    try {

      TrovaArticoloForm trovaArticoloForm = (TrovaArticoloForm) form;

      if (trovaArticoloForm == null || ( trovaArticoloForm != null
          && trovaArticoloForm.getCodice() == null
          && trovaArticoloForm.getDescrizione() == null
          && trovaArticoloForm.getUnitaDiMisura() == null
          && trovaArticoloForm.getGestioneInventario() == null
          && trovaArticoloForm.getGestioneMagazzino() == null
          && trovaArticoloForm.getCodiceIva() == null
          && trovaArticoloForm.getTipoGestione() == null
          && "20".equals(trovaArticoloForm.getRisPerPagina())
          && Boolean.FALSE.equals(trovaArticoloForm.isVisualizzazioneAvanzata()))
          ){

        // l'azione è stata richiamata per ripetere l'ultima ricerca effettuata,
        // quindi i dati relativi al form sono da leggere dalla sessione
        trovaArticoloForm = (TrovaArticoloForm) request.getSession().getAttribute(
            "trovaArticoli");
        // alla prima esecuzione, se non vengono impostati filtri si entra
        // comunque qui, e si rischia di aggiornare trovaArticoloForm con un
        // reference ad un bean che non esiste ancora in sessione
        if (trovaArticoloForm == null) {
          trovaArticoloForm = new TrovaArticoloForm();
        } else {
          cambioPaginaRicerca = true;
          Enumeration<?> paramNames = request.getParameterNames();
          while(paramNames.hasMoreElements()) {
            String name = (String) paramNames.nextElement();
            if (name != null && name.startsWith("d-") && name.endsWith("-p")) {
              String pageValue = request.getParameter(name);
              if (pageValue != null) {
                // ho aggiunto il -1 perche la paginazione per SIGLA inizia da 0
                trovaArticoloForm.setActivePage(String.valueOf(Integer.parseInt(pageValue)-1));
              }
            }
          }
        }
      }

      request.getSession().setAttribute("trovaArticoli",
          trovaArticoloForm);
      request.getSession().setAttribute("provengoDallaLista","Si");

      String servizio = request.getParameter("servizio");
      if(servizio==null || "".equals(servizio)){
        servizio ="WSERP";
      }

      ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = new Long(profilo.getId());
      String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);

      String username = credenziali[0];
      String password = credenziali[1];

      WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi(servizio);
      String tipoWSERP = configurazione.getRemotewserp();
      if("SIGLA".equals(tipoWSERP)){
        WSERPRdaType erpSearch= new WSERPRdaType();
        List<WSERPSearchClauseType> listClauseTypes = new ArrayList<WSERPSearchClauseType>();
        WSERPSearchClauseType searchClauseType = new WSERPSearchClauseType();
        if(!"".equals(trovaArticoloForm.getCodice())) {
          searchClauseType = new WSERPSearchClauseType();
          searchClauseType.setCondition(trovaArticoloForm.getCodiceCondition());
          searchClauseType.setOperator(convertOperator(trovaArticoloForm.getOperatoreCodice(),trovaArticoloForm.isVisualizzazioneAvanzata()));
          searchClauseType.setFieldValue(getFieldValue(trovaArticoloForm.getOperatoreCodice(),trovaArticoloForm.getCodice(),trovaArticoloForm.isVisualizzazioneAvanzata()));
          searchClauseType.setFieldName("cd_bene_servizio");
          listClauseTypes.add(searchClauseType);
        }
        if(!"".equals(trovaArticoloForm.getDescrizione())) {
          searchClauseType = new WSERPSearchClauseType();
          searchClauseType.setCondition(trovaArticoloForm.getDescrizioneCondition());
          searchClauseType.setOperator(convertOperator(trovaArticoloForm.getOperatoreDescrizione(),trovaArticoloForm.isVisualizzazioneAvanzata()));
          searchClauseType.setFieldValue(getFieldValue(trovaArticoloForm.getOperatoreDescrizione(),trovaArticoloForm.getDescrizione(),trovaArticoloForm.isVisualizzazioneAvanzata()));
          searchClauseType.setFieldName("ds_bene_servizio");
          listClauseTypes.add(searchClauseType);
        }
        if(!"".equals(trovaArticoloForm.getUnitaDiMisura())) {
          searchClauseType = new WSERPSearchClauseType();
          searchClauseType.setCondition(trovaArticoloForm.getUnitaDiMisuraCondition());
          searchClauseType.setOperator(convertOperator(trovaArticoloForm.getOperatoreUnitaDiMisura(),trovaArticoloForm.isVisualizzazioneAvanzata()));
          searchClauseType.setFieldValue(getFieldValue(trovaArticoloForm.getOperatoreUnitaDiMisura(),trovaArticoloForm.getUnitaDiMisura(),trovaArticoloForm.isVisualizzazioneAvanzata()));
          searchClauseType.setFieldName("unita_misura");
          listClauseTypes.add(searchClauseType);
        }
        if(!"".equals(trovaArticoloForm.getGestioneInventario())) {
          searchClauseType = new WSERPSearchClauseType();
          searchClauseType.setCondition(trovaArticoloForm.getGestioneInventarioCondition());
          searchClauseType.setOperator(convertOperator("=",trovaArticoloForm.isVisualizzazioneAvanzata()));
          searchClauseType.setFieldValue(getFieldValue("=",Boolean.valueOf(trovaArticoloForm.getGestioneInventario()),trovaArticoloForm.isVisualizzazioneAvanzata()));
          searchClauseType.setFieldName("fl_gestione_inventario");
          listClauseTypes.add(searchClauseType);
        }
        if(!"".equals(trovaArticoloForm.getGestioneMagazzino())) {
          searchClauseType = new WSERPSearchClauseType();
          searchClauseType.setCondition(trovaArticoloForm.getGestioneMagazzinoCondition());
          searchClauseType.setOperator(convertOperator("=",trovaArticoloForm.isVisualizzazioneAvanzata()));
          searchClauseType.setFieldValue(getFieldValue("=",Boolean.valueOf(trovaArticoloForm.getGestioneMagazzino()),trovaArticoloForm.isVisualizzazioneAvanzata()));
          searchClauseType.setFieldName("fl_gestione_magazzino");
          listClauseTypes.add(searchClauseType);
        }
        if(!"".equals(trovaArticoloForm.getCodiceIva())) {
          searchClauseType = new WSERPSearchClauseType();
          searchClauseType.setCondition(trovaArticoloForm.getCodiceIvaCondition());
          searchClauseType.setOperator(convertOperator(trovaArticoloForm.getOperatoreCodiceIva(),trovaArticoloForm.isVisualizzazioneAvanzata()));
          searchClauseType.setFieldValue(getFieldValue(trovaArticoloForm.getOperatoreCodiceIva(),trovaArticoloForm.getCodiceIva(),trovaArticoloForm.isVisualizzazioneAvanzata()));
          searchClauseType.setFieldName("cd_codice_iva");
          listClauseTypes.add(searchClauseType);
        }
        if(!"".equals(trovaArticoloForm.getTipoGestione())) {
          searchClauseType = new WSERPSearchClauseType();
          searchClauseType.setCondition(trovaArticoloForm.getTipoGestioneCondition());
          searchClauseType.setOperator(convertOperator(trovaArticoloForm.getOperatoreTipoGestione(),trovaArticoloForm.isVisualizzazioneAvanzata()));
          searchClauseType.setFieldValue(getFieldValue(trovaArticoloForm.getOperatoreTipoGestione(),trovaArticoloForm.getTipoGestione(),trovaArticoloForm.isVisualizzazioneAvanzata()));
          searchClauseType.setFieldName("tipoGestione");
          listClauseTypes.add(searchClauseType);
        }
        WSERPSearchClauseType[] vettSearchClauseType = new WSERPSearchClauseType[listClauseTypes.size()];
        for (int i = 0; i < listClauseTypes.size(); i++) {
          vettSearchClauseType[i] = listClauseTypes.get(i);
        }
        erpSearch.setSearchClauseArray(vettSearchClauseType);
        erpSearch.setPageClause(new WSERPPageClauseType(Long.valueOf(0), Long.valueOf(trovaArticoloForm.getActivePage()), Long.valueOf(trovaArticoloForm.getRisPerPagina())));
        WSERPRdaResType wserpRdaRes = this.gestioneWSERPManager.wserpListaRda(username, password, servizio, erpSearch);
        if(!wserpRdaRes.isEsito()){
          throw new GestoreException("Si e' verificato un errore durante la associazione delle RdA: " + wserpRdaRes.getMessaggio(),
              "wserp.erp.associarda.remote.error", null);
        }else{
          WSERPRdaType[] rdaArray = wserpRdaRes.getRdaArray();
          int conta = 1;
          ObjpaginazioneListaArticoli objpaginazioneListaArticoli = new ObjpaginazioneListaArticoli();
          objpaginazioneListaArticoli.setElements(new ArrayList<Articolo>());
          //Se non ci sono Articoli l'oggetto torna vuoto e non null
          if(rdaArray != null) {
            List<Articolo> listaArticoli = new ArrayList<Articolo>();
            for (WSERPRdaType rda : rdaArray) {
              Articolo articolo = new Articolo();
              articolo.setId(conta);
              articolo.setCd_bene_servizio(rda.getCodiceArticolo());
              articolo.setDs_bene_servizio(rda.getDescrizione());
              articolo.setUnita_misura(rda.getUm());
              articolo.setFl_gestione_inventario(rda.getGestioneInventario());
              articolo.setFl_gestione_magazzino(rda.getGestioneMagazzino());
              articolo.setCd_voce_iva(rda.getMotGreen());
              //articolo.setCd_voce_iva(rda.getIva()!=null?rda.getIva().toString():null);
              articolo.setTipoGestione(rda.getTipoGestione());
              listaArticoli.add(articolo);
              conta++;
            }
            WSERPPageClauseType pageClause = wserpRdaRes.getRdaArray(0).getPageClause();
            objpaginazioneListaArticoli.setElements(listaArticoli);
            objpaginazioneListaArticoli.setActivePage(Integer.parseInt(pageClause.getActivePage().toString()));
            objpaginazioneListaArticoli.setMaxItemsPerPage(Integer.parseInt(pageClause.getMaxItemsPerPage().toString()));
            objpaginazioneListaArticoli.setTotalNumItems(Integer.parseInt(pageClause.getTotalNumItems().toString()));
          }
          request.setAttribute("listaArticoli", objpaginazioneListaArticoli);
        }
      }
      request.setAttribute("cambioPaginaRicerca", cambioPaginaRicerca);
      request.setAttribute("risultatiPerPagina",
          "Tutti".equals(trovaArticoloForm.getRisPerPagina())
          ? null
              : trovaArticoloForm.getRisPerPagina());
    } catch (Exception e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("FormRicercaInserimentoArticoliAction: fine metodo trova");
    }
    //request.setAttribute("codein", "");
    return mapping.findForward(target);

  }

  private Object getFieldValue(String operatoreCodice, Object codice, boolean isRicercaAvanzata) {
    Object res = codice;
    if(isRicercaAvanzata) {
      if(operatoreCodice != null && !"".equals(operatoreCodice)) {
        if("contiene".equals(operatoreCodice)) {
          res = "%"+codice+"%";
        } else if("iniziaPer".equals(operatoreCodice)) {
          res = codice+"%";
        } else if("terminaPer".equals(operatoreCodice)) {
          res = "%"+codice;
        } else if("nonNullo".equals(operatoreCodice) || "nullo".equals(operatoreCodice)) {
          res = "";
        }
      }
    }
    return res;
  }

  private String convertOperator(String operatoreCodice, boolean isRicercaAvanzata) {
    String res = "";
    if(isRicercaAvanzata) {
      if(operatoreCodice != null && !"".equals(operatoreCodice)) {
        if("contiene".equals(operatoreCodice) || "iniziaPer".equals(operatoreCodice) || "terminaPer".equals(operatoreCodice)) {
          res = "like";
        } else if ("nonNullo".equals(operatoreCodice)) {
          res = "isnotnull";
        } else if ("nullo".equals(operatoreCodice)) {
          res = "isnull";
        }
      }
    } else {
      res = "=";
    }
    return res;
  }

  public ActionForward inserisci(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException, GestoreException {

    if (logger.isDebugEnabled()) {
      logger.debug("FormRicercaInserimentoArticoliAction: inizio metodo trova");
    }
    String target = "success";
    //target = FORWARD_VISUALIZZA;
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    String messageKey = null;

    try {
      String servizio = request.getParameter("servizio");
      if(servizio==null || "".equals(servizio)){
        servizio ="WSERP";
      }

      ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      Long syscon = new Long(profilo.getId());
      String[] credenziali = this.gestioneWSERPManager.wserpGetLogin(syscon, servizio);

      String username = credenziali[0];
      String password = credenziali[1];

      WSERPConfigurazioneOutType configurazione = gestioneWSERPManager.wserpConfigurazioneLeggi(servizio);
      String tipoWSERP = configurazione.getRemotewserp();
      if("SIGLA".equals(tipoWSERP)){

        String[] listaIdsSelezionati = request.getParameterValues("keys");
        WSERPRdaType[] rdaArray = null;
        List<WSERPRdaType> rdaArray2 = null;
        if (listaIdsSelezionati != null) {

          rdaArray2 = new ArrayList<WSERPRdaType>();
          for (int i = 0; i < listaIdsSelezionati.length; i++) {
            String valoriIdsSelezionati = listaIdsSelezionati[i];
            if (valoriIdsSelezionati != null && !"".equals(valoriIdsSelezionati)) {
              String[] valori = valoriIdsSelezionati.split("_#_");
              WSERPRdaType rda = new WSERPRdaType();
              if(valori[1] != null && !"".equals(valori[1]))
                rda.setCodiceArticolo(valori[1]);
              if(valori[2] != null && !"".equals(valori[2]))
                rda.setDescrizione(valori[2]);
              if(valori[3] != null && !"".equals(valori[3]))
                rda.setUm(valori[3]);
              if(valori[4] != null && !"".equals(valori[4]))
                rda.setGestioneInventario(Boolean.valueOf(valori[4]));
              if(valori[5] != null && !"".equals(valori[5]))
                rda.setGestioneMagazzino(Boolean.valueOf(valori[5]));
              if(valori[6] != null && !"".equals(valori[6])) {
                if(StringUtils.isNumeric(valori[6])) {
                    rda.setIva(Double.valueOf(valori[6]));
                } else {
                  Object percentuale = this.sqlManager.getObject(
                      "SELECT percentuale FROM codiciivasigla where cd_voce_iva = ?", new Object[] { valori[6] });
                  if(percentuale != null) {
                    rda.setIva(Double.valueOf(percentuale.toString()));
                  }
                }
              }
              if(valori[7] != null && !"".equals(valori[7]))
                rda.setTipoGestione(valori[7]);
              rdaArray2.add(rda);
            }
          }
          rdaArray = new WSERPRdaType[rdaArray2.size()];
          for (int i = 0; i < rdaArray2.size(); i++) {
            rdaArray[i] = rdaArray2.get(i);
          }
        }

        String codiceGara = request.getParameter("ngara");

        this.gestioneWSERPManager.insCollegamentoRda(request, username, password, servizio, codiceGara, null, rdaArray, null, null);
        request.setAttribute("reload", "1");
        //request.setAttribute("codein", "");
      }
    } catch (Exception e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }


    if (logger.isDebugEnabled()) {
      logger.debug("FormRicercaInserimentoArticoliAction: fine metodo trova");
    }

    return mapping.findForward(target);

  }




}
