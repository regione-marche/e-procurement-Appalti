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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

public class AggiungiDestinatariAnagraficaAction extends ActionBaseNoOpzioni {

  protected static final String FORWARD_ERRORE  = "aggiungidestinatarianagraficaerror";
  protected static final String FORWARD_SUCCESS = "aggiungidestinatarianagraficasuccess";

  static Logger                 logger          = Logger.getLogger(AggiungiDestinatariAnagraficaAction.class);

  private SqlManager            sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("AggiungiDestinatariAnagrafica: inizio metodo");

    String idprg = new String(request.getParameter("idprg"));
    Long idcom = new Long(request.getParameter("idcom"));
    String entita = new String(request.getParameter("entitaRicerca"));
    String destinatarioInCC = request.getParameter("descc");
    if("".equals(destinatarioInCC)) {
      destinatarioInCC=null;
    }

    String[] tipoIndirizzo={"Pec", "Email", /*"Fax",*/ "Operatore"}; //Fax non più supportato

    String target = FORWARD_SUCCESS;
    String messageKey = null;

    String insertW_INVCOMDES = "insert into w_invcomdes (idprg, idcom, idcomdes, descodent, "
    	+ "descodsog, desmail, desintest, comtipma, descc, idanonimo)"
        + " values (?,?,?,?,?,?,?,?,?,?)";

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
            String[] valoriDittaSelezionata = listaDitteSelezionate[i].split("#;#");
            if (valoriDittaSelezionata.length >= 3) {
              if (valoriDittaSelezionata[0] != null && valoriDittaSelezionata[1] != null ) {
                Long tipimp=null;
                String codRigaImpr = valoriDittaSelezionata[0];
                if(!"TECNI".equals(entita)) {
                  //WE715 per i raggruppamenti si riportano anche le componenti
                  String selectTipimp = "select coalesce(tipimp,1) from impr where codimp = ?";
                  tipimp = (Long) sqlManager.getObject(selectTipimp, new Object[] { codRigaImpr });
                }
                if(tipimp != null && (tipimp == 3 || tipimp == 10 )) {
                  String nomeRTI = valoriDittaSelezionata[1];
                  String  selectComponenti= "";
                  if("Pec".equals(tipoIndirizzo[j])){
                    selectComponenti= "select CODDIC, NOMDIC, EMAI2IP from RAGIMP,IMPR where CODIME9 = ? and CODDIC=CODIMP ";
                  }else{
                    if("Email".equals(tipoIndirizzo[j])){
                      selectComponenti= "select CODDIC, NOMDIC, EMAIIP from RAGIMP,IMPR  where CODIME9 = ? and CODDIC=CODIMP ";
                    }else{
                      if("Fax".equals(tipoIndirizzo[j])){
                        selectComponenti= "select CODDIC, NOMDIC, FAXIMP from RAGIMP,IMPR  where CODIME9 = ? and CODDIC=CODIMP ";
                      }else{
                        if("Operatore".equals(tipoIndirizzo[j])){
                          selectComponenti= "select CODDIC, NOMDIC, cast(null as varchar(20)) from RAGIMP,IMPR  where CODIME9 = ? and CODDIC=CODIMP ";
                        }
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
                      if(StringUtils.isNotBlank(indirizzoComponente) || "Operatore".equals(tipoIndirizzo[j])){
                        maxId = new Long(maxId.longValue() + 1);
                        Long comtipma = null;
                        if(!"Operatore".equals(tipoIndirizzo[j]))
                          comtipma= new Long(j + 1); //vogliamo valorizzarlo per mail e pec, nullo per comunicazione operatore anonimo!
                        this.sqlManager.update(insertW_INVCOMDES, new Object[] { idprg,
                            idcom, maxId, "IMPR", codRigaImpr, indirizzoComponente, nomeRTI+" - "+nomeComponente+" - Mandataria", comtipma,destinatarioInCC,null });
                      }
                    }
                  }
                } else {
                  maxId = new Long(maxId.longValue() + 1);
                  String descodent = "IMPR";
                  if("TECNI".equals(entita))
                    descodent="TECNI";
                  // se esiste il parametro che indica se l'incaricato è interno o esterno se interno associo la tabella TECNI
                  if (valoriDittaSelezionata.length == 4) {
                    if (valoriDittaSelezionata[3] != null && valoriDittaSelezionata[3].equals("1")) {
                      descodent = "TECNI";
                    }
                  }
                  
                  Long comtipma = null;
                  String desmail = null;
                  String idanonimo = null;
                  if(!"Operatore".equals(tipoIndirizzo[j])) {
                    comtipma= new Long(j + 1); //vogliamo valorizzarlo per mail e pec, nullo per comunicazione operatore anonimo!
                    desmail = valoriDittaSelezionata[2];
                  }else {
                    idanonimo = valoriDittaSelezionata[2];
                    if("undefined".equals(idanonimo))
                      idanonimo=null;
                  }
                  this.sqlManager.update(insertW_INVCOMDES, new Object[] {idprg, idcom, maxId, descodent, valoriDittaSelezionata[0],
                      desmail, valoriDittaSelezionata[1], comtipma ,destinatarioInCC,idanonimo});
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
      logger.debug("AggiungiDestinatariAnagrafica: fine metodo");

    return mapping.findForward(target);

  }

}
