package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

public class AggiungiDitteConcorrentiAction extends ActionBaseNoOpzioni {

  protected static final String FORWARD_ERRORE  = "aggiungiditteconcorrentierror";
  protected static final String FORWARD_SUCCESS = "aggiungiditteconcorrentisuccess";

  static Logger                 logger          = Logger.getLogger(AggiungiDitteConcorrentiAction.class);

  private SqlManager            sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("AggiungiDitteConcorrenti: inizio metodo");

    String idprg = new String(request.getParameter("idprg"));
    Long idcom = new Long(request.getParameter("idcom"));

    String[] tipoIndirizzo={"Pec", "Email", "Fax"};

    String target = FORWARD_SUCCESS;
    String messageKey = null;

    String insertW_INVCOMDES = "insert into w_invcomdes (idprg, idcom, idcomdes, descodent, "
    	+ "descodsog, desmail, desintest, comtipma)"
        + " values (?,?,?,?,?,?,?,?)";

    TransactionStatus status = null;
    boolean commit = true;

    try {

      status = this.sqlManager.startTransaction();

      String[] listaDitteSelezionate;

      Long maxId = (Long) this.sqlManager.getObject(
          "select max(idcomdes) from w_invcomdes where idprg = ? and idcom = ?",
          new Object[] { idprg, idcom });
      if (maxId == null) maxId = new Long(0);

      for (int j = 0; j < tipoIndirizzo.length; j++) {
        listaDitteSelezionate = request.getParameterValues("keys" + tipoIndirizzo[j]);
        if (listaDitteSelezionate != null) {
          for (int i = 0; i < listaDitteSelezionate.length; i++) {
            String[] valoriDittaSelezionata = listaDitteSelezionate[i].split(";");
            if (valoriDittaSelezionata.length >= 3) {
              if (valoriDittaSelezionata[0] != null && valoriDittaSelezionata[1] != null ) {
                //WE715 per i raggruppamenti si riportano anche le componenti
                String codRigaImpr = valoriDittaSelezionata[0];
                String selectTipimp = "select coalesce(tipimp,1) from impr where codimp = ?";
                Long tipimp = (Long) sqlManager.getObject(selectTipimp, new Object[] { codRigaImpr });
                if(tipimp != null && (tipimp == 3 || tipimp == 10 )) {
                  String nomeRTI = valoriDittaSelezionata[1];
                  String  selectComponenti= "";
                  if(j == 0){
                    selectComponenti= "select CODDIC, NOMDIC, EMAI2IP from RAGIMP,IMPR where CODIME9 = ? and CODDIC=CODIMP ";
                  }else{
                    if(j == 1){
                      selectComponenti= "select CODDIC, NOMDIC, EMAIIP from RAGIMP,IMPR  where CODIME9 = ? and CODDIC=CODIMP ";
                    }else{
                      if(j == 2){
                        selectComponenti= "select CODDIC, NOMDIC, FAXIMP from RAGIMP,IMPR  where CODIME9 = ? and CODDIC=CODIMP ";
                      }
                    }
                  }

                  selectComponenti+=" and IMPMAN='1'";

                  List listaComponenti = sqlManager.getListVector(selectComponenti,new Object[]{ codRigaImpr });
                  if (listaComponenti != null && listaComponenti.size() > 0){
                    // si può migliorare la gestione dei contatori
                    for (int k = 0; k< listaComponenti.size(); k++) {
                      String codComponente = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 0).getStringValue();
                      String nomeComponente = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 1).getStringValue();
                      String indirizzoComponente = SqlManager.getValueFromVectorParam( listaComponenti.get(k), 2).getStringValue();
                      indirizzoComponente = UtilityStringhe.convertiNullInStringaVuota(indirizzoComponente);
                      if(!"".equals(indirizzoComponente)){
                        maxId = new Long(maxId.longValue() + 1);
                        this.sqlManager.update(insertW_INVCOMDES, new Object[] { idprg,
                            idcom, maxId, "IMPR", codRigaImpr, indirizzoComponente, nomeRTI+" - "+nomeComponente+" - Mandataria", new Long(j+1) });
                      }
                    }
                  }
                } else {
                  maxId = new Long(maxId.longValue() + 1);
                  String descodent = "IMPR";
                  // se esiste il parametro che indica se l'incaricato è interno o esterno se interno associo la tabella TECNI
                  if (valoriDittaSelezionata.length == 4) {
                    if (valoriDittaSelezionata[3] != null && valoriDittaSelezionata[3].equals("1")) {
                      descodent = "TECNI";
                    }
                  }
                  this.sqlManager.update(insertW_INVCOMDES, new Object[] {idprg, idcom, maxId, descodent, valoriDittaSelezionata[0],
                      valoriDittaSelezionata[2], valoriDittaSelezionata[1], new Long(j + 1) });

                }
              }
            }
          }
        }
      }

    } catch (Throwable t) {
      commit = false;
      target = FORWARD_ERRORE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);

    } finally {
      if (status != null) {
        try {
          if (commit == true) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        } catch (SQLException e) {

        }
      }
    }

    if (messageKey != null) response.reset();

    if (logger.isDebugEnabled())
      logger.debug("AggiungiDitteConcorrenti: fine metodo");

    return mapping.findForward(target);

  }

}
