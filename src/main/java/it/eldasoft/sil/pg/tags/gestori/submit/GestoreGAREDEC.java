package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pl.struts.gestori.GestoreAPPADEC;

import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

public class GestoreGAREDEC extends AbstractGestoreEntita {

  static Logger logger = Logger.getLogger(GestoreGAREDEC.class);

  // Gestore fittizio. Le operazioni di insert e delete devono essere eseguite
  // esplicitamente in questo gestore.
  public GestoreGAREDEC() {
    super(false);
  }

  @Override
  public String getEntita() {
    return "GARE";
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

    String ngara = impl.getString("GARE.NGARA");
    this.getRequest().setAttribute("NGARA", ngara);
    String ncont_String = UtilityStruts.getParametroString(this.getRequest(),"ncont");
    Long ncont= new Long(ncont_String);

    String clavor = impl.getString("GARE.CLAVOR");
    Long numera = impl.getLong("GARE.NUMERA");

    //variabili per tracciatura eventi
    String messageKey = null;
    int livEvento = 3;
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");
    String codiceLavoro = "";
    String numeroAppalto = "";

    try {
      if (clavor == null && numera == null) {
        // Non esiste integrazione. Aggiungo alcuni campi al DataColumnContainer
        // prima di chiamare il gestore GestoreAPPADEC

        // Ricavo l'ufficio intestatario
        String cenint = null;
        String entint = null;
        String codgar1 = (String) this.sqlManager.getObject("select codgar1 from gare where ngara = ?", new Object[] {ngara});
        if (codgar1 != "") {
          Vector datiTorn = this.sqlManager.getVector("select altrisog, cenint, modcont from torn where codgar = ?", new Object[] {codgar1});
          if(datiTorn!=null && datiTorn.size()>0){
            Long altrisog = SqlManager.getValueFromVectorParam(datiTorn, 0).longValue();
            Long modcont = SqlManager.getValueFromVectorParam(datiTorn, 2).longValue();
            Long genere = impl.getLong("V_GARE_TORN.GENERE");
            if(altrisog!=null && altrisog.longValue()==2 && ((new Long(3).equals(genere) && new Long(1).equals(modcont)) || (!new Long(3).equals(genere)))){
              cenint = (String) this.sqlManager.getObject("select cenint from garaltsog where ngara = ?", new Object[] {ngara});
            }else{
              cenint =  SqlManager.getValueFromVectorParam(datiTorn, 1).getStringValue();
            }
            if (cenint != null) {
              entint = (String) this.sqlManager.getObject("select nomein from uffint where codein = ?", new Object[] {cenint});
            }
          }
          /*
          cenint = (String) this.sqlManager.getObject("select cenint from torn where codgar = ?", new Object[] {codgar1});
          if (cenint != null) {
            entint = (String) this.sqlManager.getObject("select nomein from uffint where codein = ?", new Object[] {cenint});
          }
          */
        }

        DataColumnContainer dccAPPA = new DataColumnContainer(new DataColumn[] {
            new DataColumn("GARE.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)),
            new DataColumn("PERI.CENINT", new JdbcParametro(JdbcParametro.TIPO_TESTO, cenint)),
            new DataColumn("PERI.ENTINT", new JdbcParametro(JdbcParametro.TIPO_TESTO, entint)),
            new DataColumn("PERI.CODLAV", new JdbcParametro(JdbcParametro.TIPO_TESTO, impl.getString("PERI_CODLAV"))),
            new DataColumn("PERI.TITSIL", new JdbcParametro(JdbcParametro.TIPO_TESTO, impl.getString("PERI_TITSIL"))),
            new DataColumn("APPA.CODLAV", new JdbcParametro(JdbcParametro.TIPO_TESTO, impl.getString("APPA_CODLAV"))),
            new DataColumn("APPA.NAPPAL", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, impl.getLong("APPA_NAPPAL"))),
            new DataColumn("APPA.TIPLAVG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, impl.getLong("TORN.TIPGEN"))),
            new DataColumn("APPA.NOTAPP", new JdbcParametro(JdbcParametro.TIPO_TESTO, impl.getString("APPA_NOTAPP"))),
            new DataColumn("APPA.FASEAPPALTO", new JdbcParametro(JdbcParametro.TIPO_TESTO, "E")),
            new DataColumn("LAVORO_ESISTENTE", new JdbcParametro(JdbcParametro.TIPO_TESTO, impl.getString("LAVORO_ESISTENTE"))),
            new DataColumn("NCONT", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, ncont))});
        dccAPPA.setOriginalValue("GARE.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, null));
        this.inserisci(status, dccAPPA, new GestoreAPPADEC());
        this.getRequest().setAttribute("RISULTATO", "CREAZIONEESEGUITA");
        codiceLavoro = dccAPPA.getColumn("APPA.CODLAV").getValue().getStringValue();
        numeroAppalto = dccAPPA.getColumn("APPA.NAPPAL").getValue().getStringValue();
      } else {
        // Il contratto di GARE è integrato con un contratto DEC. E' necessario
        // aggiornare il contratto DEC
        DataColumnContainer dccAPPA = new DataColumnContainer(new DataColumn[] {
            new DataColumn("GARE.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, ngara)),
            new DataColumn("APPA.CODLAV", new JdbcParametro(JdbcParametro.TIPO_TESTO, clavor)),
            new DataColumn("APPA.NAPPAL", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, numera)),
            new DataColumn("NCONT", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, ncont))});
        dccAPPA.setOriginalValue("GARE.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, null));
        this.update(status, dccAPPA, new GestoreAPPADEC());
        this.getRequest().setAttribute("RISULTATO", "AGGIORNAMENTOESEGUITO");
        codiceLavoro = dccAPPA.getColumn("APPA.CODLAV").getValue().getStringValue();
        numeroAppalto = dccAPPA.getColumn("APPA.NAPPAL").getValue().getStringValue();
      }
      //best case
      livEvento = 1 ;
      errMsgEvento = "";
    } catch (SQLException e) {
      livEvento = 3;
      errMsgEvento = "Errore nella gestione dell'ufficio intestatario";
      this.getRequest().setAttribute("RISULTATO", "ERRORI");
      throw new GestoreException("Errore nella gestione dell'ufficio intestatario", null,e);
    } catch (GestoreException e) {
      this.getRequest().setAttribute("RISULTATO", "ERRORI");
      throw e;
    }finally{
      //Tracciatura eventi
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(ngara);
        logEvento.setCodEvento("GA_ESECUZIONE_CONTRATTO");
        logEvento.setDescr("Esecuzione contratto (codice appalto: " + codiceLavoro + "/" + numeroAppalto + ")");
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
      }

    }

  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl) throws GestoreException {

  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {

  }

}
