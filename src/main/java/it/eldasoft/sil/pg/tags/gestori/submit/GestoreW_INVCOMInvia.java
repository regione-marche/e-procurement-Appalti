package it.eldasoft.sil.pg.tags.gestori.submit;



import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.sil.pg.bl.utils.AllegatoSintesiUtils;
import it.eldasoft.sil.pg.bl.utils.MarcaturaTemporaleFileUtils;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;


public class GestoreW_INVCOMInvia extends AbstractGestoreChiaveNumerica {

  /** Logger */
  static Logger logger = Logger.getLogger(GestoreW_INVCOMInvia.class);

  @Override
  public String[] getAltriCampiChiave() {
    return new String[] { "IDPRG" };
  }

  @Override
  public String getCampoNumericoChiave() {
    return "IDCOM";
  }

  @Override
  public String getEntita() {
    return "W_INVCOM";
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {


  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    String idprg = datiForm.getString("IDPRG");
    Long idcom = datiForm.getLong("IDCOM");
    Long compub = datiForm.getLong("COMPUB");
    String cenint = datiForm.getString("IDCFG_NEW");

    Long idcomris = datiForm.getLong("IDCOMRIS");
    String idprgris = datiForm.getString("IDPRGRIS");

    this.getRequest().setAttribute("IDPRG", idprg);
    this.getRequest().setAttribute("IDCOM", idcom.toString());
    this.getRequest().setAttribute("COMPUB", compub.toString());
    this.getRequest().setAttribute("IDCFG", cenint);

    String comkey1 = datiForm.getString("COMKEY1");
    
    String inviaDescc = UtilityStruts.getParametroString(this.getRequest(),"descc");
    this.getRequest().setAttribute("DESCC", inviaDescc);

    //Gestione dell'allegato di sintesi
    GestioneWSDMManager gestioneWSDMManager = (GestioneWSDMManager) UtilitySpring.getBean("gestioneWSDMManager",
        this.getServletContext(), GestioneWSDMManager.class);

    PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
        this.getServletContext(), PgManagerEst1.class);
    
    //se è configurato il WSDM e l'invio è a carico del documentale, cancello i destinatari in cc
      if("false".equals(inviaDescc)) {
        try {
          Object par2[] = new Object[] { idprg, idcom.toString() };
          String delete_W_INVCOMDES = "delete from W_INVCOMDES where IDPRG = ? and IDCOM =? and descc = 1";
          this.sqlManager.update(delete_W_INVCOMDES, par2);
        } catch (SQLException e) {
          logger.error("Si è verificato un errore nella cancellazione dei dati dalla W_INVCOMDES", e);
        }
      }
    



    if (compub != null && compub.intValue() == 1){
      datiForm.setValue("W_INVCOM.COMSTATO", "3");
      //////
      //Comunicazioni pubbliche, valorizzata la data pubblicazione (COMDATAPUB.W_INVCOM) con la data corrente
      // Data pubblicazione
      datiForm.setValue("W_INVCOM.COMDATAPUB", new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()));
      //////
      //Aggiornamento della data ultimo aggiornamento



      pgManagerEst1.aggiornamentoDataAggiornamentoPortaleComunicazione(comkey1, idcom);

      //Cancello eventuali destinatari rimasti nella W_INVCOMDES
      //Questo succede nel caso la comunicazione fosse stata prima impostata come riservata e valorizzata dei destinatari
      //e successivamente portata a pubblica

      try {
    	  Object par2[] = new Object[] { idprg, idcom.toString() };

    	  String delete_W_INVCOMDES = "delete from W_INVCOMDES where IDPRG = ? and IDCOM =? ";
    	  this.sqlManager.update(delete_W_INVCOMDES, par2);
      } catch (SQLException e) {
    	  logger.error("Si è verificato un errore nella cancellazione dei dati dalla W_INVCOMDES", e);
      }


   }else {

     try {
       //Si deve verificare se esiste il file marcato temporalemente:
       //se esiste non si deve fare nulla
       //se non esiste si deve cancellare l'eventuale file esistente e ricrearlo
       boolean applicareMarcaTemp = false;
       boolean esisteMarcatura = false;
       boolean esitoControMarcatura[] = pgManagerEst1.applicareMarcaTemporalmente(idprg, idcom,true);
       esisteMarcatura = esitoControMarcatura[0];
       applicareMarcaTemp = esitoControMarcatura[1];
       if(!esisteMarcatura) {
         //Si deve cancellare un eventuale allegato di sintesi già presente
         String delete = "delete from W_DOCDIG  where DIGKEY1 = ? and DIGKEY2=? and DIGENT =? and DIGNOMDOC = "
             + AllegatoSintesiUtils.creazioneFiltroNomeFileSintesi(false,this.sqlManager);
         Object par[] = new Object[] {idprg, idcom.toString(), "W_INVCOM"};
         this.sqlManager.update(delete, par);

         String commsgogg = datiForm.getString("COMMSGOGG");
         String commsgtes = datiForm.getString("COMMSGTES");
         String coment = datiForm.getString("COMENT");
         byte[] pdf = null;
         InputStream iccInputStream = new FileInputStream(getRequest().getSession(true).getServletContext().getRealPath("/WEB-INF/jrReport/sRGB_v4_ICC_preference.icc"));
         if("G1STIPULA".equals(coment)) {
           pdf = gestioneWSDMManager.getTestoComunicazioneFormattatoStipula(comkey1, commsgogg, commsgtes, idprg,idcom,iccInputStream);
         }else {
           String cig=(String)this.sqlManager.getObject("select codcig from v_gare_torn where codice = ?", new Object[] {comkey1});
           pdf = gestioneWSDMManager.getTestoComunicazioneFormattato(comkey1, cig, commsgogg, commsgtes, idprg,idcom,iccInputStream);
         }
         Long maxContatore = (Long)this.sqlManager.getObject("select coalesce(max(iddocdig),0) + 1 from W_DOCDIG where idprg = ?", new Object[] {idprg});
         String insert="insert into w_docdig(idprg,iddocdig,digent,digkey1,digkey2,dignomdoc,digdesdoc,digogg) values(?,?,?,?,?,?,?,?)";
         String nomeAllegato = idprg + maxContatore.toString() + "_comunicazione.pdf";
         //Se attiva la gestione della marcatemporale si deve chiamare l'apposito servizio

         String descrizioneAllegato = "Riepilogo comunicazione";
         if(applicareMarcaTemp) {
           HashMap<String,Object> marcaTemporale = MarcaturaTemporaleFileUtils.creaMarcaTemporale(pdf, idcom, comkey1, coment,getRequest());
           String esito = (String)marcaTemporale.get("esito");
           if("OK".equals(esito)) {
             pdf = (byte[])marcaTemporale.get("file");
             nomeAllegato+=".tsd";
             descrizioneAllegato += " con marcatura temporale";
           }else {
             this.getRequest().setAttribute("RISULTATO", "ERRORE.MARCATEMP");
             return;
           }

         }

         LobHandler lobHandler = new DefaultLobHandler();
         this.sqlManager.update(insert, new Object[] {idprg, maxContatore, "W_INVCOM",idprg,idcom.toString(),nomeAllegato,descrizioneAllegato,new SqlLobValue(pdf, lobHandler)});
       }
     } catch (Exception e) {
       this.getRequest().setAttribute("RISULTATO", "ERRORE.MARCATEMP");
       return;
     }


     datiForm.setValue("W_INVCOM.COMSTATO", "2");
   }
   datiForm.setValue("W_INVCOM.IDCFG", cenint);

   if(idcomris != null && idprgris !=null && !"".equals(idprgris)){
      try {
        Date comdatalet = (Date) sqlManager.getObject("select COMDATLET from w_invcom where IDPRG = ? and IDCOM = ?", new Object[] { idprgris, idcomris });
        if(comdatalet == null){
          this.sqlManager.update("update W_INVCOM set COMDATLET = ? where IDPRG = ? and IDCOM=?", new Object[] {
              new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), idprgris, idcomris });
        }
      } catch (SQLException e) {
        logger.error("Si è verificato un errore nel salvataggio dei dati dalla comunicazione originale", e);
      }
   }
   this.getRequest().setAttribute("RISULTATO", "INVIOESEGUITO");

  }

}
