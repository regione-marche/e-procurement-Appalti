package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.PropsConfigManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.PropsConfig;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Integrazione Autovie - Esegue la composizione della url da richiamare
 */


public class ConsultaDocumentiArchiflowAction extends ActionBaseNoOpzioni {

  private static final String PROP_AUTOVIE_URL    = "autovie.url";

  static Logger                 logger          = Logger.getLogger(ConsultaDocumentiArchiflowAction.class);

  private SqlManager            sqlManager;
  private PropsConfigManager    propsConfigManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setPropsConfigManager(PropsConfigManager propsConfigManager) {
    this.propsConfigManager = propsConfigManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("ConsultaDocumentiArchiflow: inizio metodo");

    String messageKey = null;
    String autovieUrl = null;
  //selezioni in configurazione protocolli per autovie
    String selectConf = "select campo, archivio, tipodocumento from confarchiflow where campo = ? ";
    String campoProtocollo = new String(request.getParameter("campoProtocollo"));
    String valoreProtocollo = new String(request.getParameter("valoreProtocollo"));
    valoreProtocollo = UtilityStringhe.convertiNullInStringaVuota(valoreProtocollo.replace("/", "|"));
    valoreProtocollo = valoreProtocollo + "|" + valoreProtocollo;

    // Gestione dei parametri
    autovieUrl = ConfigManager.getValore(PROP_AUTOVIE_URL);
    autovieUrl = UtilityStringhe.convertiNullInStringaVuota(autovieUrl);
    if (autovieUrl == null || "".equals(autovieUrl)) {
      if (logger.isInfoEnabled()) logger.info("La url per Archiflow non e' definita!");
      try {
        throw new GestoreException("La url per Archiflow non e' definita!", "error.autovie.url", null);
      } catch (GestoreException e) {
        logger.error("La url per Archiflow non e' definita!");
        messageKey = "error.autovie.url";
        this.aggiungiMessaggio(request, messageKey);
      }
    }else{

      try {
        Vector vectorConf = this.sqlManager.getVector(selectConf, new Object[] {campoProtocollo});
        if (vectorConf != null && vectorConf.size() > 0 ) {
          String campo = (String) SqlManager.getValueFromVectorParam(vectorConf, 0).getValue();
          campo = UtilityStringhe.convertiNullInStringaVuota(campo);
          String archivio = (String) SqlManager.getValueFromVectorParam(vectorConf, 1).getValue();
          archivio = UtilityStringhe.convertiNullInStringaVuota(archivio);
          String tipoDocumento = (String) SqlManager.getValueFromVectorParam(vectorConf, 2).getValue();
          tipoDocumento = UtilityStringhe.convertiNullInStringaVuota(tipoDocumento);
          String strParametri = "A_:"+archivio+";D_:"+tipoDocumento+";P_:"+valoreProtocollo+";";
          autovieUrl = autovieUrl+strParametri;
        }

      } catch (SQLException e) {
        logger.error("Errore nella selezione della configurazione Archiflow!");
        messageKey = "error.autovie.selconf";
        this.aggiungiMessaggio(request, messageKey);
      }
    }

    if (logger.isDebugEnabled())
      logger.debug("ConsultaDocumentiArchiflow: fine metodo");

      response.sendRedirect(autovieUrl);

    return null;

  }

}
