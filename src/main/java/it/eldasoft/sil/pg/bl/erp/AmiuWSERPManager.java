/*
 * Created on 29/03/2022
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


public class AmiuWSERPManager {
  /** Logger */
  static Logger               logger                = Logger.getLogger(AmiuWSERPManager.class);

  private SqlManager          sqlManager;

  private GestioneWSERPManager gestioneWSERPManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }




   public WSERPRdaResType  inviaDatiContratto (String username, String password, HashMap datiMask) throws GestoreException, SQLException{
     WSERPRdaResType wserpRdaRes = new WSERPRdaResType();
     String codgar = (String)datiMask.get("codgar");
     String ngara = (String)datiMask.get("ngara");
     String codcig = (String)datiMask.get("codcig");
     String codcigaq = (String)datiMask.get("codcigaq");
     String ditta = (String)datiMask.get("ditta");
     String oggettoGara = (String)datiMask.get("oggettoGara");
     Double iaggiu = (Double)datiMask.get("iaggiu");
     Double ribagg = (Double)datiMask.get("ribagg");
     Double riboepv = (Double)datiMask.get("riboepv");
     Long modlicg = (Long)datiMask.get("modlicg");
     
     String[] listaDitte = null;
     String dittaRTI = "";
     String linkrda = "";
     int esitoComunicazioneRda = 0;

     WSERPGaraType datiGara = new WSERPGaraType();

       datiGara.setCodiceCig(codcig);
       oggettoGara = UtilityStringhe.convertiNullInStringaVuota(oggettoGara);
       if(oggettoGara.length()>120){
         oggettoGara = oggettoGara.substring(0, 119);
       }

       
       datiGara.setImportoAggiudicazione(iaggiu);
       //verificare qui:
       if (new Long(6).equals(modlicg)) {
         datiGara.setRibassoAggiudicazione(riboepv);
       }else{
         datiGara.setRibassoAggiudicazione(ribagg);
       }


       
       
       listaDitte = new String[1];
       listaDitte[0] = ditta;
       if(listaDitte != null){
         for(int i=0;i<listaDitte.length;i++){
           ditta = listaDitte[i];
           
           Vector<?> datiGarecont  = this.sqlManager.getVector(
                   "select gc.dcertu from GARECONT gc,GARE ga"
                   + " where gc.codimp = ga.ditta and ((gc.ngara=ga.ngara and gc.ncont=1) or (gc.ngara=ga.codgar1 and (gc.ngaral is null or gc.ngaral=ga.ngara)))"
                   + " and ga.ditta is not null and ga.ngara= ? and ga.ditta = ?", new Object[] { ngara, ditta });
           
           if(datiGarecont!=null && datiGarecont.size()>0){
               Date dcertu = (Date) SqlManager.getValueFromVectorParam(datiGarecont, 0).getValue();
               if(dcertu != null){
                 Calendar calDcertu = Calendar.getInstance();
                 calDcertu.setTime(dcertu);
                 datiGara.setDataFineValidita(calDcertu);
               }
           }

           
           
           
           Long tipimp = null;
           String cfimp=null;
           String pivimp=null;
           //inizio integrazione ERP
           if(ditta != null){
        	   Vector<?> datiDitta = this.sqlManager.getVector("select tipimp,cfimp,pivimp from impr where codimp = ?", new Object[]{ditta});
        	   
               if(datiDitta!=null && datiDitta.size()>0){
                 tipimp   = (Long) SqlManager.getValueFromVectorParam(datiDitta, 0).getValue();
                 cfimp   = (String) SqlManager.getValueFromVectorParam(datiDitta, 1).getValue();
                 pivimp  = (String) SqlManager.getValueFromVectorParam(datiDitta, 2).getValue();
                 if(tipimp != null && (new Long(3).equals(tipimp) || new Long(10).equals(tipimp))){
                     String mandataria = (String)this.sqlManager.getObject("select coddic from ragimp" +
                            " where codime9 = ? and impman = ? ", new Object[]{ditta, "1"});
                     dittaRTI = ditta;
                     ditta= mandataria;
                 }

               }

        	   
               List listaRdaGara = null;   
             
               listaRdaGara  = this.sqlManager.getListVector(
                       "select r.CODGAR,r.NUMRDA,r.POSRDA from GARERDA r where r.CODGAR = ?" +
                       " order by r.numrda,r.posrda", new Object[] { codgar });

               if(listaRdaGara != null && listaRdaGara.size() > 0){
                 linkrda = "1";
                 int nRda = listaRdaGara.size();
                 if(nRda == 0){
                   Vector vectRdaGara = (Vector) listaRdaGara.get(0);
                   String numRda = ((JdbcParametro) vectRdaGara.get(1)).getStringValue();
                   String posRda = ((JdbcParametro) vectRdaGara.get(2)).getStringValue();

                   if(iaggiu != null){
                     WSERPOdaType[] odaArray = new WSERPOdaType[1];
                     WSERPOdaType oda = new WSERPOdaType();
                     oda.setCodiceRda(numRda);
                     //AGGIUDICA
                     odaArray[0] = oda;

                     wserpRdaRes = this.gestioneWSERPManager.wserpAggiudicaRdaGara(username, password, "WSERP", odaArray, datiGara);

                     //settare codiceRda, idFornitore M
                     //CASO UNA RDA

                   }

                 }else{

                   if(iaggiu != null){

                     WSERPOdaType[] odaArray = new WSERPOdaType[listaRdaGara.size()];
                     for (int k = 0; k < listaRdaGara.size(); k++) {
                       Vector vectRdaGara = (Vector) listaRdaGara.get(k);
                       String numRda = ((JdbcParametro) vectRdaGara.get(1)).getStringValue();
                       String posRda = ((JdbcParametro) vectRdaGara.get(2)).getStringValue();
                       WSERPOdaType oda = new WSERPOdaType();
                       oda.setCodiceRda(numRda);
                       oda.setPosizioneRda(posRda);
                       oda.setNumeroGara(ngara);
                       
                       oda.setDescrizione(oggettoGara);
                       oda.setCfFornitore(cfimp);
                       oda.setPivaFornitore(pivimp);

                       //AGGIUDICA
                         odaArray[k] = oda;

                     }//for

                     //AGGIUDICA
                       wserpRdaRes = this.gestioneWSERPManager.wserpAggiudicaRdaGara(username, password, "WSERP", odaArray, datiGara);

                   }//if iaggiu

                 } //if nrda
               }


             return wserpRdaRes;

           }//fine if ditta

         }//for ditte

       }



     return wserpRdaRes;
   }




  }


