/*
 * Created on 13/07/17
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
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
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;


/**
 * Gestore non standard (in quanto è implementato il costruttore in modo da non
 * gestire l'aggiornamento dei dati sull'entità principale), per gestire l'annullamento
 * del calcolo dei punteggi lanciato da popupAnnullaCalcoloPunteggi.jp
 *
 * @author Marcello Caminiti
 */
public class GestorePopupAnnullaCalcoloPunteggi extends
    AbstractGestoreEntita {

  static Logger               logger         = Logger.getLogger(GestorePopupAnnullaCalcoloPunteggi.class);

  public GestorePopupAnnullaCalcoloPunteggi() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;


  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);
  }

  @Override
  public String getEntita() {
    return "GARE";
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
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    //variabili per tracciatura eventi
    int livEvento = 3;
    String codEvento = "GA_OEPV_ANNULLA_TEC";
    String oggEvento = "";
    String descrEvento = "Annulla calcolo punteggi ditte per criteri di valutazione busta tecnica";
    String errMsgEvento = this.resBundleGenerale.getString("errors.logEventi.inaspettataException");

    String ngara = UtilityStruts.getParametroString(this.getRequest(),"ngara");
    String tipo= UtilityStruts.getParametroString(this.getRequest(),"tipo");
    String codgar = UtilityStruts.getParametroString(this.getRequest(),"codgar");

    String campoPunteggioTotale="puntec";
    String campoPunteggioTotaleRip="puntecrip";
    String campoPunteggioTotaleRip1="puntecrip1";
    oggEvento = ngara;

    Long fasgar= new Long(5);
    if("2".equals(tipo)){
      codEvento = "GA_OEPV_ANNULLA_ECO";
      descrEvento = "Annulla calcolo punteggi ditte per criteri di valutazione busta economica";
      campoPunteggioTotale="puneco";
      campoPunteggioTotaleRip="punecorip";
      campoPunteggioTotaleRip1="punecorip1";
      fasgar= new Long(6);
    }

    try{
      
      try {
        this.sqlManager.update("update g1crival set punteg = NULL, coeffi = NULL where g1crival.idcridef in ( select g1cridef.id from g1cridef, goev where g1cridef.modpunti = 2 and goev.tippar = ? " +
        		"and g1cridef.ngara = ? and goev.ngara = g1cridef.ngara and g1cridef.necvan = goev.necvan)", new Object[]{new Long(tipo),ngara});
      } catch (NumberFormatException e1) {
        errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.calcoloPunteggi.cancellaCRIVAL");
        throw new GestoreException(errMsgEvento, "calcoloPunteggi.cancellaCRIVAL",e1);
      } catch (SQLException e) {
        errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.calcoloPunteggi.cancellaCRIVAL");
        throw new GestoreException(errMsgEvento, "calcoloPunteggi.cancellaCRIVAL",e);
      }
      
      try {
        //si cancellano tutte le occorrenze in DPUN per la gara
        this.sqlManager.update("delete from dpun where dpun.ngara=? and dpun.necvan=(select goev.necvan from goev where goev.ngara=dpun.ngara and goev.necvan=dpun.necvan and tippar=?)", new Object[]{ngara, new Long(tipo)});
      } catch (SQLException e) {
        livEvento = 3;
        this.getRequest().setAttribute("calcoloEseguito", "2");
        errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.calcoloPunteggi.cancellaDPUN");
        throw new GestoreException(errMsgEvento, "calcoloPunteggi.cancellaDPUN",e);
      }
      try {
        //si cancellano i punteggi totali delle ditte
        this.sqlManager.update("update ditg set " + campoPunteggioTotale + "=null, " + campoPunteggioTotaleRip + "=null, " + campoPunteggioTotaleRip1 + "=null where ngara5=?", new Object[]{ngara});
      } catch (SQLException e) {
        livEvento = 3;
        this.getRequest().setAttribute("calcoloEseguito", "2");
        errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.annullaCalcoloPunteggi.cancellaPunteggiTot");
        throw new GestoreException(errMsgEvento, "annullaCalcoloPunteggi.cancellaPunteggiTot",e);
      }
      //Riammissione delle ditte escluse alla fase corrente in seguito a punteggio sotto la soglia minima
      String select="select dittao from ditg where ngara5=? and fasgar=? and moties=104";
      try {
        List listaDitte = this.sqlManager.getListVector(select, new Object[]{ngara, fasgar});
        if(listaDitte!=null && listaDitte.size()>0){
          String ditta = null;
          GestoreDITG gestoreDITG= new GestoreDITG();
          gestoreDITG.setRequest(this.getRequest());

          DataColumn[] columnsV_DITGAMMIS= new DataColumn[3];
          columnsV_DITGAMMIS[0] = new DataColumn("V_DITGAMMIS.DETMOTESCL",new JdbcParametro(JdbcParametro.TIPO_TESTO,""));
          columnsV_DITGAMMIS[0].setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_TESTO," "));
          columnsV_DITGAMMIS[1] = new DataColumn("V_DITGAMMIS.AMMGAR",new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(1)));
          columnsV_DITGAMMIS[2] = new DataColumn("V_DITGAMMIS.MOTIVESCL",new JdbcParametro(JdbcParametro.TIPO_NUMERICO,null));
          columnsV_DITGAMMIS[2].setOriginalValue(new JdbcParametro(JdbcParametro.TIPO_NUMERICO,new Long(-100)));
          for(int i=0;i<listaDitte.size();i++){
            ditta = SqlManager.getValueFromVectorParam(listaDitte.get(i), 0).getStringValue();
            try{
              gestoreDITG.gestioneDITGAMMIS(codgar, ngara, ditta, fasgar, columnsV_DITGAMMIS, status);
              gestoreDITG.aggiornaStatoAmmissioneDITG(codgar, ngara, ditta, fasgar, columnsV_DITGAMMIS, status);

              Long valoreDITG_FASGAR = (Long)this.sqlManager.getObject(
                  "select FASGAR from ditg where CODGAR5=? and NGARA5=? and DITTAO=?",
                  new Object[]{codgar, ngara, ditta});
              if(valoreDITG_FASGAR!= null && fasgar.longValue() == valoreDITG_FASGAR.longValue()){
                this.sqlManager.update("update ditg set ammgar=1, fasgar=null where ngara5=? and ditta=?", new Object[]{ngara,ditta});
              }
            }catch (GestoreException e) {
              livEvento = 3;
              this.getRequest().setAttribute("calcoloEseguito", "2");
              errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.annullaCalcoloPunteggi.riammissioneDitte");
              throw e;
            }
          }
        }
      } catch (SQLException e) {
        livEvento = 3;
        this.getRequest().setAttribute("calcoloEseguito", "2");
        errMsgEvento = this.resBundleGenerale.getString("errors.gestoreException.*.annullaCalcoloPunteggi.riammissioneDitte");
        throw new GestoreException(errMsgEvento, "annullaCalcoloPunteggi.riammissioneDitte",e);
      }
      livEvento = 1;
      errMsgEvento = "";
      this.getRequest().setAttribute("calcoloEseguito", "1");
    } finally{
      //Tracciatura eventi
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(oggEvento);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descrEvento);
        logEvento.setErrmsg(errMsgEvento);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        String messageKey = "errors.logEventi.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), le);
        this.getRequest().setAttribute("calcoloEseguito", "2");
      }
    }
  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
