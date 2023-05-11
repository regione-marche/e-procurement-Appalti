/*
 * Created on 26/09/2022
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.erp;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSERPManager;
import it.eldasoft.utils.utility.UtilityMath;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreResType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreType;
import it.maggioli.eldasoft.ws.erp.WSERPGaraType;
import it.maggioli.eldasoft.ws.erp.WSERPOdaType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;


public class AtacWSERPManager {
  /** Logger */
  static Logger               logger                = Logger.getLogger(AtacWSERPManager.class);

  /** Manager per l'interrogazione dei tabellati */
  private TabellatiManager            tabellatiManager;

  private SqlManager          sqlManager;

  private GestioneWSERPManager gestioneWSERPManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }

  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }


  public WSERPRdaResType inviaDatiAggiudicazione (String username, String password, HashMap datiMask) throws GestoreException, SQLException{

    WSERPRdaResType wserpRdaRes = new WSERPRdaResType();
    String ngara = (String)datiMask.get("ngara");
    String codcig = (String)datiMask.get("codcig");
    String codcigaq = (String)datiMask.get("codcigaq");
    String ditta = (String)datiMask.get("ditta");
    Double iaggiu = (Double)datiMask.get("iaggiu");

    String[] listaDitte = null;
    String dittaRTI = "";
    int esitoComunicazioneRda = 0;

    WSERPGaraType datiGara = new WSERPGaraType();

    datiGara.setCodiceCig(codcig);

    datiGara.setImportoAggiudicazione(iaggiu);

    listaDitte = new String[1];
    listaDitte[0] = ditta;
    if(listaDitte != null){
      for(int i=0;i<listaDitte.length;i++){
        ditta = listaDitte[i];
        String pivimp=null;

        if(ditta != null){
          Vector<?> datiDitta = this.sqlManager.getVector("select pivimp from impr where codimp = ?", new Object[]{ditta});

          if(datiDitta!=null && datiDitta.size()>0){
            pivimp  = (String) SqlManager.getValueFromVectorParam(datiDitta, 0).getValue();
          }


          List listaRdaGara = null;   

          listaRdaGara  = this.sqlManager.getListVector(
              "SELECT T.TIPGEN, T.SETTORE, "
                  + "G.DATTOA, G.TIPGARG, "
                  + "C.CODCARR, C.CODRDA, C.POSRDA, C.PREZUN "
                  + "FROM TORN T, GARE G, GCAP C "
                  + "WHERE T.CODGAR = G.CODGAR1 "
                  + "AND G.NGARA = C.NGARA "
                  + "AND G.NGARA = ?", new Object[] { ngara });

          if(listaRdaGara != null && listaRdaGara.size() > 0){

            if(iaggiu != null){

              WSERPOdaType[] odaArray = new WSERPOdaType[listaRdaGara.size()];
              for (int k = 0; k < listaRdaGara.size(); k++) {
                Vector vectRdaGara = (Vector) listaRdaGara.get(k);
                String tipgen = ((JdbcParametro) vectRdaGara.get(0)).getStringValue();
                String settore = ((JdbcParametro) vectRdaGara.get(1)).getStringValue();
                Date dattoa = (Date) ((JdbcParametro) vectRdaGara.get(2)).getValue();
                String tipgarg = ((JdbcParametro) vectRdaGara.get(3)).getStringValue();
                String codcarr = ((JdbcParametro) vectRdaGara.get(4)).getStringValue();
                String numRda = ((JdbcParametro) vectRdaGara.get(5)).getStringValue();
                String posRda = ((JdbcParametro) vectRdaGara.get(6)).getStringValue();
                Double prezun = ((JdbcParametro) vectRdaGara.get(7)).doubleValue();

                WSERPOdaType oda = new WSERPOdaType();
                oda.setWbs(settore);
                oda.setCodiceCarrello(codcarr);                
                oda.setCodiceRda(numRda);
                oda.setPosizioneRda(posRda);
                oda.setNumeroGara(ngara);
                oda.setPivaFornitore(pivimp);
                oda.setPrezzo(prezun);

                String tipoGara = tabellatiManager.getDescrTabellato("A2044", tipgarg);
                datiGara.setDefinizioneCig(tipoGara);
                datiGara.setTipoContratto(tipgen);
                if(dattoa != null){
                  Calendar cdaggeff = Calendar.getInstance();
                  cdaggeff.setTime(dattoa);
                  datiGara.setDataAggiudicazione(cdaggeff);
                }
                odaArray[k] = oda;

              }//for



              //AGGIUDICA
              wserpRdaRes = this.gestioneWSERPManager.wserpAggiudicaRdaGara(username, password, "WSERP", odaArray, datiGara);

            }//if iaggiu

          }

        }//fine if ditta

      }//for ditte

    }

    return wserpRdaRes;

    //return null;
  }




}


