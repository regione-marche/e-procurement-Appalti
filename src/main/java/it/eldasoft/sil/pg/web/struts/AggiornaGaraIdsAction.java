package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

public class AggiornaGaraIdsAction extends ActionBaseNoOpzioni {

  protected static final String FORWARD_ERRORE  = "aggiornagaraidserror";
  protected static final String FORWARD_SUCCESS = "aggiornagaraidssuccess";

  static Logger                 logger          = Logger.getLogger(AggiornaGaraIdsAction.class);

  private SqlManager            sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("AggiornaGaraIds: inizio metodo");

    String codiceGara = new String(request.getParameter("codiceGara"));
    String genereGara = new String(request.getParameter("genereGara"));

    String[] tipoOperazione = {"Collega", "Scollega"};

    String target = FORWARD_SUCCESS;
    String messageKey = null;
    String selectIds = "select numero_protocollo, data_protocollo, data_ricezione, ora_ricezione, sigla_entita_richiedente" +
    		"  from v_lista_ids where ids_prog = ?";
    String selectTORN ="select nattot,dattot,cenint from torn where codgar = ?";
    String selectGARE ="select g.nattog,g.dattog,t.cenint from gare g,torn t where g.codgar1 = ? and g.codgar1 = t.codgar";

    String selectMaxGareids ="select coalesce(max(numids),0) from gareids where codgar = ?";
    String insertGAREIDS = "insert into gareids(codgar, numids, datemiss, nprot, datricez, progids) values (?,?,?,?,?,?)";

    String selectUFFINT = "select codein from uffint where codein = ?";
    String updateTORNperSA = "update torn set cenint = ? where codgar = ?";
    String selectImportoGara = "select sum(coalesce(importo,0)) from v_lista_ids_collegati where codice_gara = ?";
    //String updateImportoTORN = "update torn set imptor = ? where codgar = ? and imptor is null";
    String updateImportoGARE = "update gare set impapp = ? where codgar1 = ? and impapp is null";
    String updateOggettoTORN = "update torn set  destor = ? where codgar = ? and destor is null";
    String updateOggettoGARE = "update gare set not_gar = ? where codgar1 = ? and not_gar is null";





    //MASSIMA ATTENZIONE
    String deleteGAREIDS = "delete from gareids where codgar = ? and nprot = ?";

    TransactionStatus status = null;
    boolean commit = true;

    try {

      status = this.sqlManager.startTransaction();

      String[] listaIdsSelezionati;


      for (int j = 0; j < tipoOperazione.length; j++) {
        //Nell'ordine si svolgono le operazioni 'Collega' e successivamente 'Scollega'
        listaIdsSelezionati = request.getParameterValues("keys" + tipoOperazione[j]);
        if (listaIdsSelezionati != null) {
          for (int i = 0; i < listaIdsSelezionati.length; i++) {
            //lo split mi servirà piu' avanti se ho altri valori di riga da memorizzare
            String[] valoriIdsSelezionati = listaIdsSelezionati[i].split(";");
            if (valoriIdsSelezionati.length >= 1) {
              if (valoriIdsSelezionati[0] != null ) {
                String ids_prog = valoriIdsSelezionati[0];
                Long progIds = Long.valueOf(ids_prog).longValue();
                Vector vectorIds = sqlManager.getVector(selectIds,new Object[]{ ids_prog });
                if (vectorIds != null && vectorIds.size() > 0){
                    String numProtocollo = (String) SqlManager.getValueFromVectorParam(vectorIds, 0).getValue();

                    Date dataProtocollo = (Date) SqlManager.getValueFromVectorParam(vectorIds, 1).getValue();
                    Date dataRicezione = (Date) SqlManager.getValueFromVectorParam(vectorIds, 2).getValue();
                    //String oraRicezioneStr = (String) SqlManager.getValueFromVectorParam(vectorIds, 3).getValue();
                    String siglaEntitaRichiedente = (String) SqlManager.getValueFromVectorParam(vectorIds, 4).getValue();
                    //Operazione -COLLEGA-
                  if ("Collega".equals(tipoOperazione[j])) {
                    if ("1".equals(genereGara)) {
                      Vector vectorTorn = this.sqlManager.getVector(selectTORN, new Object[] {codiceGara });
                      if (vectorTorn != null && vectorTorn.size() > 0 ) {
                        //String nattot = (String) SqlManager.getValueFromVectorParam(vectorTorn, 0).getValue();
                        //nattot = UtilityStringhe.convertiNullInStringaVuota(nattot);
                        String cenint = (String) SqlManager.getValueFromVectorParam(vectorTorn, 2).getValue();
                        cenint = UtilityStringhe.convertiNullInStringaVuota(cenint);
                        Long maxGareids = (Long) this.sqlManager.getObject(selectMaxGareids, new Object[] {codiceGara });
                        maxGareids = new Long(maxGareids.intValue() + 1);
                        this.sqlManager.update(insertGAREIDS, new Object[] {codiceGara, maxGareids, dataProtocollo, numProtocollo, dataRicezione, progIds});
                        if("".equals(cenint)){
                          String codein = (String)sqlManager.getObject(selectUFFINT, new Object[]{siglaEntitaRichiedente});
                          codein = UtilityStringhe.convertiNullInStringaVuota(codein);
                          if(!"".equals(codein)){
                            this.sqlManager.update(updateTORNperSA, new Object[] {codein, codiceGara });
                          }
                        }
                      }
                    }
                    if ("2".equals(genereGara)) {
                      Vector vectorGare = this.sqlManager.getVector(selectGARE, new Object[] {codiceGara });
                      if (vectorGare != null && vectorGare.size() > 0 ) {
                        String cenint = (String) SqlManager.getValueFromVectorParam(vectorGare, 2).getValue();
                        cenint = UtilityStringhe.convertiNullInStringaVuota(cenint);
                        Long maxGareids = (Long) this.sqlManager.getObject(selectMaxGareids, new Object[] {codiceGara });
                        maxGareids = new Long(maxGareids.intValue() + 1);
                        this.sqlManager.update(insertGAREIDS, new Object[] {codiceGara, maxGareids, dataProtocollo, numProtocollo, dataRicezione, progIds});
                        if("".equals(cenint)){
                          String codein = (String)sqlManager.getObject(selectUFFINT, new Object[]{siglaEntitaRichiedente});
                          this.sqlManager.update(updateTORNperSA, new Object[] {codein, codiceGara });
                        }
                      }
                    }
                  }
                  //Operazione -SCOLLEGA-
                  if ("Scollega".equals(tipoOperazione[j])) {
                    if ("1".equals(genereGara)) {
                        //MAX ATTENZIONE...fare verifica prima eventualmente
                        this.sqlManager.update(deleteGAREIDS, new Object[] {codiceGara,numProtocollo});
                    }
                    if ("2".equals(genereGara)) {
                        //MAX ATTENZIONE...fare verifica prima eventualmente
                        this.sqlManager.update(deleteGAREIDS, new Object[] {codiceGara,numProtocollo});
                    }
                  }
                }
              }
            }
          }//if lista ids

          Double sum_impIds = new Double(0);
          Object sum_impIdsTemp = this.sqlManager.getObject(selectImportoGara, new Object[] {codiceGara});
          if (sum_impIdsTemp != null) {
            if (sum_impIdsTemp instanceof Long) {
              sum_impIds = new Double(((Long) sum_impIdsTemp));
            } else if (sum_impIdsTemp instanceof Double) {
              sum_impIds = new Double((Double) sum_impIdsTemp);
            }
          }
          //concatenazione descrizioni ids per comporre oggetto
          // controllare 2000 caratteri

          List<Object> listaDescrizioniIds = new Vector<Object>();
          listaDescrizioniIds = sqlManager.getListVector(
              "select oggetto from v_lista_ids_collegati where codice_gara = ?", new Object[] { codiceGara });
          String oggetto = "";
          for (int n = 0; n < listaDescrizioniIds.size(); n++) {
            Vector vect = (Vector) listaDescrizioniIds.get(n);
            String n_oggetto = SqlManager.getValueFromVectorParam(vect, 0).stringValue();
            oggetto = oggetto + n_oggetto + "\n";
          }
          if(oggetto.length() > 2000){
            oggetto = oggetto.substring(0,1999);
          }

          if ("2".equals(genereGara)) {
            this.sqlManager.update(updateImportoGARE, new Object[] {sum_impIds, codiceGara });
            this.sqlManager.update(updateOggettoGARE, new Object[] {oggetto, codiceGara });
          }else{
            //aggiorno imptor anche se la definizione dei lotti altera comunque il dato (mail 17/06/2015 Ettore G.)
            //this.sqlManager.update(updateImportoTORN, new Object[] {sum_impIds, codiceGara });
            this.sqlManager.update(updateOggettoTORN, new Object[] {oggetto, codiceGara });
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
      logger.debug("AggiornaGaraIds: fine metodo");

    return mapping.findForward(target);

  }

}
