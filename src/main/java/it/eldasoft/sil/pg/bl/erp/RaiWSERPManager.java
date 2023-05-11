/*
 * Created on 23/19/2021
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
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityMath;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreResType;
import it.maggioli.eldasoft.ws.erp.WSERPFornitoreType;
import it.maggioli.eldasoft.ws.erp.WSERPGaraType;
import it.maggioli.eldasoft.ws.erp.WSERPOdaType;
import it.maggioli.eldasoft.ws.erp.WSERPRdaResType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.example.getElencoFornitoriResults.DettaglioFornitore;


public class RaiWSERPManager {
  /** Logger */
  static Logger               logger                = Logger.getLogger(RaiWSERPManager.class);

  private SqlManager          sqlManager;
  
  private PgManager pgManager;

  private GestioneWSERPManager gestioneWSERPManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  
  public void setPgManager(PgManager pgManager) {
      this.pgManager = pgManager;
  }

  public void setGestioneWSERPManager(GestioneWSERPManager gestioneWSERPManager) {
    this.gestioneWSERPManager = gestioneWSERPManager;
  }


   int inviaQualcosa (){
     return 0;
   }

   public WSERPFornitoreResType  getListaFornitori (String username, String password, HashMap datiMask) throws GestoreException, SQLException{
     WSERPFornitoreResType wserpFornitoreRes = new WSERPFornitoreResType();

       //  wserpFornitoreRes = gestioneWSERPManager.wserpCreaFornitore(username, password, "WSERP", fornitore);

       


     return wserpFornitoreRes;
   }


   public List<Vector< ? >>  getListaFornitori (String username, String password) throws GestoreException, SQLException{
	   List<Vector< ? >>  wserpFornitoreRes = new ArrayList<Vector< ? >>();

	       //  wserpFornitoreRes = gestioneWSERPManager.wserpCreaFornitore(username, password, "WSERP", fornitore);

	       


	     return wserpFornitoreRes;
	   }


   /**
    * Viene effettuato l'inserimento del fornitore
    *
    * @param fornitore
    *           contiene i dati del fornitore
    * @param ngara
    * @param garaLottiConOffertaUnica
    *           "1" gara con offerta unica, "2" altrimenti
    * @param numeroFaseAttiva
    *           Fase attiva
    * @throws SQLException
    */
	public void insertFornitoreAlbo(String codiceFornitore, String ngara,String garaLottiConOffertaUnica, Long numeroFaseAttiva) throws SQLException{

		
	  //Il fornitore esiste in Alice?
      String select="select count(codimp) from impr where codimp = ? ";
      boolean fornitorePresente = false;
      Long occorrenze = (Long)sqlManager.getObject(select, new Object[]{codiceFornitore});
	  if (occorrenze != null && occorrenze.longValue()>0){
	      fornitorePresente = true;
	  }
      List datiImpr = sqlManager.getListVector(
              "select CODIMP, NOMIMP, CFIMP, PIVIMP from IMPR where CODIMP = ? ",
              new Object[] { codiceFornitore });
      Vector datiFornitore = (Vector) datiImpr.get(0);
      String nomimp = ((JdbcParametro) datiFornitore.get(1)).getStringValue();

		


     //Gestione DITG
     String codiceTornata = null;
     Long nProgressivoDITG, nProgressivoEDIT = null;
     Double importoAppalto = null;

     boolean esisteOccorrenzaInEDIT = false;
     boolean isProceduraAggiudicazioneAperta = false;
     // Flag per indicare che:
     // se valorizzato a true: l'inserimento della ditta avviene determinando
     // il valore del DITG.NPROGG come il max del campo stesso (anche se si
     // tratta di una gara a lotti) se valorizzato a false: l'inserimento della
     // ditta avviene usando il campo EDIT.NPROGT se la ditta e' gia' presente
     // nella tabella EDIT, oppure determinando il max fra il campo EDIT.NPROGT
     // e DITG.NPROGG relativamente all'intera tornata (e non nel lotto di gara
     // in analisi (per una gara divisa a lotti))
     boolean modalitaNPROGGsuLotti = pgManager.isModalitaNPROGGsuLotto();

     try {
       List datiGara = sqlManager.getListVector(
               "select CODGAR1, TIPGARG, IMPAPP from GARE where NGARA = ? ",
               new Object[] { ngara });
       Vector dati = (Vector) datiGara.get(0);

       codiceTornata = ((JdbcParametro) dati.get(0)).getStringValue();
       if (((JdbcParametro) dati.get(1)).getValue() != null)
           isProceduraAggiudicazioneAperta = ((Long) ((JdbcParametro)
                   dati.get(1)).getValue()).intValue() == 1;
       importoAppalto = (Double) ((JdbcParametro) dati.get(2)).getValue();


       // Determino il valore del progressivo dal campo EDIT.NPROGT per la
       // tornata in analisi e la ditta che si vuole inserire, se presente.
       nProgressivoEDIT = (Long) sqlManager.getObject(
               "select NPROGT from EDIT " + "where CODGAR4 = ? "
                       + "and CODIME = ? ",
               new Object[] { codiceTornata, codiceFornitore });

       if (nProgressivoEDIT != null && nProgressivoEDIT.intValue() > 0)
           esisteOccorrenzaInEDIT = true;
       else {
           // Se non esiste alcuna occorrenza nella EDIT relativa alla gara
           // in analisi, allora determino valore massimo del campo EDIT.NPROGT
           nProgressivoEDIT = (Long) sqlManager.getObject(
                   "select max(NPROGT) from EDIT where CODGAR4 = ? ",
                   new Object[] { codiceTornata });
           // Se la EDIT non ha occorrenze per la gara in analisi,
           // inizializzo la variabile nProgressivoEDIT a 0
           if (nProgressivoEDIT == null)
               nProgressivoEDIT = new Long(0);
       }

       if (modalitaNPROGGsuLotti)
           nProgressivoDITG = (Long) sqlManager.getObject(
                   "select max(NPROGG) from DITG " + "where CODGAR5 = ? "
                           + "and NGARA5 = ? ",
                   new Object[] { codiceTornata, ngara });
       else
           nProgressivoDITG = (Long) sqlManager.getObject(
                   "select max(NPROGG) from DITG " + "where CODGAR5 = ? ",
                   new Object[] { codiceTornata });

       if (nProgressivoDITG == null)
           nProgressivoDITG = new Long(0);
       // }
     } catch (SQLException e) {
         logger.error("Errore nel determinare il progressivo "
             + "della ditta (codice ditta: " + codiceFornitore   + ") nella gara " +
             ngara);
         throw e;

     }

     Long numProgDITG = null;
     if (modalitaNPROGGsuLotti) {
         numProgDITG = new Long(nProgressivoDITG.intValue() + 1);
     } else {
         if (esisteOccorrenzaInEDIT) {
             // Se esiste l'occorrenza nella EDIT, allora l'insert nella DITG
             // lo si effettua con DITG.NPROGG = EDIT.NPROGT
             numProgDITG = nProgressivoEDIT;
         } else {
             // Tra nProgressivoDITG e nProgressivoEDIT si va a scegliere
             // come valore del campo DITG.NPROGG il max fra i due valori
             if (nProgressivoDITG.compareTo(nProgressivoEDIT) > 0)
                 numProgDITG = new Long(nProgressivoDITG.longValue() + 1);
             else
                 numProgDITG = new Long(nProgressivoEDIT.longValue() + 1);
         }
     }




         Long nProgressivo=null;
     if (!esisteOccorrenzaInEDIT) {
         // Insert dell'occorrenza nella tabella EDIT 
         if (modalitaNPROGGsuLotti)
           nProgressivo  =new Long(nProgressivoEDIT.longValue() + 1);
         else
           nProgressivo = numProgDITG;



       try {
         select="insert into EDIT (CODGAR4,CODIME,NOMIME,DOCOK,DATOK,DITINV,NPROGT) ";
         select+=" values(?,?,?,?,?,?,?)";
         sqlManager.update(select,new Object[]{codiceTornata,codiceFornitore,nomimp,"1","1","1",nProgressivo});
       }catch (SQLException s) {
         logger.error("Errore nell'inserimento in EDIT ");
         if (logger.isDebugEnabled())
           logger.debug(s.getMessage());
         throw s;
       }
     }

     try {
       //Per le ditte inserite da AUR il campo DITG.ACQUISIZIONE = 12
       select="select count(ngara5) from ditg where ngara5=? and codgar5=? and dittao=?";
       Long count = (Long) sqlManager.getObject(
               select,new Object[] { ngara,codiceTornata,codiceFornitore });
       if (count.longValue() == 0) {
           select="insert into DITG (NGARA5,DITTAO,CODGAR5,NOMIMO,NPROGG,CATIMOK,INVGAR,NUMORDPL,INVOFF,IMPAPPD,ACQUISIZIONE,NCOMOPE) ";
           select+="values (?,?,?,?,?,?,?,?,?,?,?,?)";
           String invoff=null;
           Double impAppalto=null;
           if(isProceduraAggiudicazioneAperta){
             invoff="1";
             impAppalto=importoAppalto;
           }

           sqlManager.update(select,new Object[]{ngara,codiceFornitore,codiceTornata,nomimp,
               numProgDITG,"1","1",numProgDITG,invoff,impAppalto,new Long(12),"1"});
       } else {
         logger.error("La ditta selezionata risulta già inserita in gara");
         throw new SQLException();
       }
     } catch (SQLException e) {
       logger.error("Errore nell'inserimento in DITG");
       if (logger.isDebugEnabled())
         logger.debug(e.getMessage());
       throw e;

     }


     //Quando si inserisce una ditta in gara si deve in automatico popolare IMPRDOCG
     //a partire dalle occorrenze di DOCUMGARA, impostando SITUAZDOCI a 2 e PROVENI a 1
     try {
       pgManager.inserimentoDocumentazioneDitta(codiceTornata, ngara, codiceFornitore);
     } catch (GestoreException e1) {
       logger.error("Errore nell'inserimento della documentazione di gara");
       throw new SQLException();
     }


     if("1".equals(garaLottiConOffertaUnica)){
       try{
         // Estrazione di NGARA e IMPAPP dei lotti della gara con offerta unica,
         // Per inserire la ditta in ciascun lotto
         List listaLotti = this.sqlManager.getListVector(
                 "select NGARA, IMPAPP from GARE " +
                  "where CODGAR1 = ? and NGARA <> CODGAR1 and GENERE is null",
                  new Object[]{ngara});

         if(listaLotti != null && listaLotti.size() > 0){
           select="insert into DITG (CODGAR5,NGARA5,DITTAO,NOMIMO,NPROGG,IMPAPPD,NUMORDPL,ACQUISIZIONE,NCOMOPE) " +
                 "values(?,?,?,?,?,?,?,?,?)";
           for(int i=0; i < listaLotti.size(); i++){
             Vector lotto = (Vector) listaLotti.get(i);
             String tmpCodiceLotto = (String)((JdbcParametro) lotto.get(0)).getValue();
             Double tmpImpApp = (Double)((JdbcParametro) lotto.get(1)).getValue();
             sqlManager.update(select,new Object[]{codiceTornata,tmpCodiceLotto,codiceFornitore,
                 nomimp,numProgDITG,tmpImpApp,numProgDITG, new Long(12),"1"});
           }
         }

       }catch (SQLException e) {
         logger.error("Errore nell'inserimento della ditta nei " +
                   "lotti della gara ");
         if (logger.isDebugEnabled())
           logger.debug(e.getMessage());
         throw e;

       }

     }

     // Aggiornamento GARE.FASGAR e GARE.STEPGAR, solo se non si è già passati
     // alle fasi di gara, ovvero solo se FASGAR.GARE < 2
     try {
       pgManager.aggiornaFaseGara(numeroFaseAttiva, ngara, true);
     } catch (GestoreException e) {
       logger.error("Errore nell'aggiornamento della fase di gara");
       throw new SQLException();
     }

	}
   


  }


