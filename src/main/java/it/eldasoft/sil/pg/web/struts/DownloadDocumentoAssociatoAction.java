package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.sql.sqlparser.JdbcWhere;
import it.eldasoft.gene.tags.decorators.scheda.FormSchedaTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Esegue il download di un file allegato
 */
public class DownloadDocumentoAssociatoAction extends ActionBaseNoOpzioni {

  static Logger               logger = Logger.getLogger(DownloadDocumentoAssociatoAction.class);

  private FileAllegatoManager fileAllegatoManager;

  private SqlManager          sqlManager;

  private GeneManager geneManager;

  /**
   *
   * @param fileAllegatoManager
   */
  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  /**
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }



  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("DownloadDocumentoAssociatoAction-runAction: inizio metodo");

      String target = null;
      String messageKey = null;
      String c0acod = request.getParameter("c0acod");
      String dignomdoc = new String(request.getParameter("dignomdoc"));
      String campo = request.getParameter("campo");
      String codgar= request.getParameter("codgar");

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    String entitaDB = "TORN";
    try {
      Long count = 0L;

      if("ngaraaq".equals(campo))
        count = (Long) sqlManager.getObject("select count(1) from torn where ngaraaq in (select gare.ngara from gare,garecont gc, c0oggass cogg where ((gc.ngara=gare.ngara and gc.ncont=1) or (gc.ngara=gare.codgar1 and (gc.ngaral is null or gc.ngaral=gare.ngara))) and " +
                " gc.ngara=cogg.c0akey1 and cast( gc.ncont as text)=cogg.c0akey2 and cogg.c0aent = 'GARECONT' and cogg.c0acod=?) and codgar=?", new Object[]{c0acod, codgar});
      else if ("clavor".equals(campo))
        count = (Long) sqlManager.getObject("select count(1) from gare where clavor in (select c0akey1 from c0oggass where c0acod = ?) and codgar1=?", new Object[]{c0acod, codgar});
      else if("adesione".equals(campo)){
        count = (Long) sqlManager.getObject("select count(1) from torn t, gare g, c0oggass cogg where t.codgar=g.codgar1 and g.ngara=cogg.c0akey1 and cogg.c0acod=? and t.ngaraaq in (select ngara from gare where codgar1=?)", new Object[]{c0acod, codgar});
      }
      if(count == 0) throw new Exception("");
      String key = "TORN.CODGAR=T:"+codgar;
      // trattasi di utente loggato, quindi eseguo i controlli sulla visibilita' dei dati
      String chiaveEntitaControllataConPermessi = FormSchedaTag.trovaChiaveEntitaControllataConPermessi(key.toString(), profiloUtente, sqlManager, geneManager);
      // se il collegamento con G_PERMESSI e' stato trovato, allora eseguo una query sull'entita' che la contiene filtrando con la condizione di visibilita'
      if (chiaveEntitaControllataConPermessi != null) {
        String[] datiDaElaborare = chiaveEntitaControllataConPermessi.split("#");
        JdbcWhere where = new JdbcWhere();
        UtilityTags.jdbcAddKeyWhere(where, datiDaElaborare[2]);
        String query = "SELECT COUNT(*) FROM "
                + datiDaElaborare[0]
                + where.toString()
                + " AND "
                + datiDaElaborare[1];

        long numOccorrenze = (Long) sqlManager.getObject(query, SqlManager.getObjectFromPram(where.getParametri()));
        // si sta tentando di forzare l'accesso ad un record non visibile all'utente
        if (numOccorrenze == 0) {
          logger.error("Bloccato accesso al documento associato con chiave " + c0acod + " dell'entita' " + entitaDB + " con chiave " + key + " perche' non e' visibile all'utente " + profiloUtente.getId());
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.download.unauthorized";
          this.aggiungiMessaggio(request, messageKey);
        }
      }
    } catch (Exception e) {
      logger.error("Errore durante le verifiche se il record di c0oggass con chiave " + c0acod + " risulta visibile all'utente " + profiloUtente.getId());
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.download.unauthorized";
      this.aggiungiMessaggio(request, messageKey);
    }

    if (target == null) {
      // se i controlli preventivi sono andati a buon fine, allora si prosegue con la logica applicativa reale
        try {
          List<?> datiC0OGGASS = this.sqlManager.getVector("select idprg, iddocdig from w_docdig where digent = ? and digkey1 = ?", new Object[] { "C0OGGASS", c0acod });
          if (datiC0OGGASS != null && datiC0OGGASS.size() > 0) {
            String idprg = (String) SqlManager.getValueFromVectorParam(datiC0OGGASS, 0).getValue();
            Long iddocdig = (Long) SqlManager.getValueFromVectorParam(datiC0OGGASS, 1).getValue();

            String codiceApplicazione = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
            codiceApplicazione = UtilityStringhe.convertiNullInStringaVuota(codiceApplicazione);
            if("".equals(codiceApplicazione)){
              codiceApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
            }
            String codiceProfilo = (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);
            Integer idUtente = null;
            if(request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE) != null){
              int idUtenteInt = ((ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId();
              idUtente = new Integer(idUtenteInt);
            }
            String ip = request.getRemoteAddr();
            this.fileAllegatoManager.downloadFileAllegato(dignomdoc, idprg, iddocdig, codiceApplicazione, codiceProfilo, idUtente, ip,response);
          } else {
            target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
            messageKey = "errors.download.nodocdig";
            this.aggiungiMessaggio(request, messageKey);
          }
        } catch (Exception io) {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.download";
          this.aggiungiMessaggio(request, messageKey);
        }

      }

    if (logger.isDebugEnabled()) logger.debug("DownloadDocumentoAssociatoAction-runAction: fine metodo");

    if (target != null)
      return mapping.findForward(target);
    else
      return null;
  }

}
