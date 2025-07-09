/*
 * Created on 19-nov-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveIDAutoincrementante;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntitaChiaveNumerica;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

/**
 * Gestore per update dei dati della pagina commissione
 *
 * @author Luca.Giacomazzo
 */
public class GestoreCriteriValutazione extends AbstractGestoreEntita {

  /** Logger */
  static Logger            logger           = Logger.getLogger(GestoreCriteriValutazione.class);
  @Override
  public String getEntita() {
    return "G1CRIDEF";
  }

  /* (non-Javadoc)
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#preDelete(org.springframework.transaction.TransactionStatus, it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  /* (non-Javadoc)
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postDelete(it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  /* (non-Javadoc)
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#preInsert(org.springframework.transaction.TransactionStatus, it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    AbstractGestoreChiaveNumerica gestoreG1CRIREG = new DefaultGestoreEntitaChiaveNumerica(
        "G1CRIREG", "ID", new String[] {}, this.getRequest());

    String nomeCampoNumeroRecord = "NUMERO_G1CRIREG";
    String nomeCampoDelete = "DEL_G1CRIREG";
    String nomeCampoMod = "MOD_G1CRIREG";

    DataColumnContainer tmpDataColumnContainer = new DataColumnContainer(
        datiForm.getColumns(gestoreG1CRIREG.getEntita(), 0));

    int numeroRecord = datiForm.getLong(nomeCampoNumeroRecord).intValue();

    logger.debug(numeroRecord);

    for (int i = 1; i <= numeroRecord; i++) {
      DataColumnContainer newDataColumnContainer = new DataColumnContainer(
          tmpDataColumnContainer.getColumnsBySuffix("_" + i, false));

      // Rimozione dei campi fittizi (il campo per la marcatura della
      // delete e
      // tutti gli eventuali campi passati come argomento)
      newDataColumnContainer.removeColumns(new String[] {
          gestoreG1CRIREG.getEntita() + "." + nomeCampoDelete,
          gestoreG1CRIREG.getEntita() + "." + nomeCampoMod });

      gestoreG1CRIREG.update(status, newDataColumnContainer);
    }
  }

  /* (non-Javadoc)
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postInsert(it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  /* (non-Javadoc)
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#preUpdate(org.springframework.transaction.TransactionStatus, it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {


    DefaultGestoreEntita gestoreCRIDEF = new DefaultGestoreEntita("G1CRIDEF",
        this.getRequest());
    gestoreCRIDEF.update(status, datiForm);
    
    //APP-94, gestione update campo GOEV.ISNORIPAR
    DefaultGestoreEntita gestoreGOEV = new DefaultGestoreEntita("GOEV",
        this.getRequest());
    gestoreGOEV.update(status, datiForm);
    
    //APP-94, gestione modifica dopo pubblicazione
    String descrErrMsg = "";
    String bloccoModificaPubblicazione = UtilityStruts.getParametroString(this.getRequest(), "bloccoModificaPubblicazione");
    if("true".equals(bloccoModificaPubblicazione)){
      HashMap<String, DataColumn> hm = datiForm.getColonne();
      Iterator<?> it = hm.entrySet().iterator();

      boolean modificheDaTracciare=false;
      while (it.hasNext()) {
        Entry<String, DataColumn> pair = (Entry<String, DataColumn>)it.next();
        if(pair.getValue().isModified()){
          modificheDaTracciare=true;
         //la gestione è copiata dal gestore gare
          if(!"".equals(descrErrMsg)){
            descrErrMsg+="\n";
          }
          JdbcParametro paramOrigin = pair.getValue().getOriginalValue();
          String valOriginale = "";
          if(paramOrigin!=null){
            valOriginale = paramOrigin.toString();
          }
          JdbcParametro paramNuovo = pair.getValue().getValue();
          String valNuovo = "";
          if(paramNuovo!=null){
            valNuovo = paramNuovo.toString();
          }
          if("".equals(valOriginale)){valOriginale = "NULL";}
          if("".equals(valNuovo)){valNuovo = "NULL";}
          descrErrMsg+= pair.getKey() + ": ";
          descrErrMsg+= "valore vecchio= " + valOriginale + ", ";
          descrErrMsg+= "valore nuovo= " + valNuovo;
          }
        }

      if(modificheDaTracciare){
        LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
        logEvento.setLivEvento(1);
        logEvento.setOggEvento(datiForm.getColumn("NGARA").getValue().getStringValue());
        logEvento.setCodEvento("GA_MODIFICA_CRIDEF_PUB");
        logEvento.setDescr("Modifica dettaglio criterio di valutazione dopo pubblicazione su portale Appalti");
        logEvento.setErrmsg("Dettaglio modifiche:\n" + descrErrMsg);
        LogEventiUtils.insertLogEventi(logEvento);
      }
    }

    AbstractGestoreChiaveIDAutoincrementante gestoreGARCOMPREQ = new DefaultGestoreEntitaChiaveIDAutoincrementante(
        "G1CRIREG", "ID", this.getRequest());
    this.gestisciAggiornamentiRecordSchedaMultipla(status, datiForm,
        gestoreGARCOMPREQ, "G1CRIREG",
        new DataColumn[] {datiForm.getColumn("G1CRIDEF.ID")}, null);
    String id = datiForm.getColumn("G1CRIDEF.ID").getValue().getStringValue();
    String codgar = datiForm.getColumn("CODGAR").getValue().getStringValue();
    String ngara = datiForm.getColumn("NGARA").getValue().getStringValue();
    
    String formatoCorrente = datiForm.getColumn("G1CRIDEF.FORMATO").getValue().getStringValue();
    if(formatoCorrente == null || formatoCorrente.length()<= 0){
      throw new GestoreException("Formato non definito","criteri.formatoNonDefinito");
    }
    String modpunti = datiForm.getColumn("G1CRIDEF.MODPUNTI").getValue().getStringValue();
    if(modpunti == null || modpunti.length()<= 0){
      throw new GestoreException("Modalità assegnazione punteggio non definita","criteri.modalitaCalcoloNonDefinita");
    }
    if(modpunti.equals("1") || modpunti.equals("3")){
      String modmanu = datiForm.getColumn("G1CRIDEF.MODMANU").getValue().getStringValue();
      if(modmanu == null || modmanu.length()<= 0){
        throw new GestoreException("Modalità punteggio manuale non definita","criteri.punteggioManualeNonDefinito");
      }
    }else if(modpunti.equals("2")){
      String formula = datiForm.getColumn("G1CRIDEF.FORMULA").getValue().getStringValue();
      if(formula == null || formula.length()<= 0){
        throw new GestoreException("Formula calcolo punteggio automatico non definita","criteri.punteggioAutomaticoNonDefinito");
      }
      if("1".equals(formatoCorrente) || "4".equals(formatoCorrente) || "100".equals(formatoCorrente)) {
        throw new GestoreException("Modalita' assegnazione punteggio e formato non compatibili","criteri.modpuntiFormatoNonCompatibili");
      }
    }
    if(formatoCorrente.equals("50") || formatoCorrente.equals("51") || formatoCorrente.equals("52")){
    String checkUnicoFormato = "select formato from g1cridef, gare where g1cridef.ngara = gare.ngara and g1cridef.ngara = ? and id != ? and (formato = 50 or formato = 51 or formato = 52)";
      try {
        List listaFormati = this.sqlManager.getListVector(checkUnicoFormato,new Object[]{ngara, id});

        if(listaFormati != null && listaFormati.size() > 0){
              throw new GestoreException("Gestore criteri", "criteri.formatoNonConsentito");
        }
      } catch (SQLException e) {
        //insert error message
      }
    }

	String delete = "delete from documgara where ngara = ? and busta = 2 and gentel = '1' and descrizione = 'Offerta tecnica'";
    String insertDoc = "insert into documgara (codgar,ngara,norddocg,numord,busta,gruppo,tipodoc,descrizione,obbligatorio,modfirma,valenza,gentel,seztec) values (?,?,?,1,2,3,1,'Offerta tecnica','1',1,0,'1',2)";
    String queryCriteriDefiniti = "select formato from g1cridef, goev where g1cridef.ngara = ? and g1cridef.necvan = goev.necvan and g1cridef.ngara = goev.ngara and formato != 100 and goev.tippar=1";
	String selectDocumentiTecniciGenerati = "select ngara from documgara where ngara = ? and busta = 2 and gentel = '1' and descrizione = 'Offerta tecnica'";
    String selectMaxNord = "select max(norddocg) from documgara where codgar = ?";
    try {
      List listaFormatiDef = this.sqlManager.getListVector(queryCriteriDefiniti, new Object[] { ngara });
      if (listaFormatiDef != null && listaFormatiDef.size() > 0) {
        List documentiTecniciGenerati = this.sqlManager.getListVector(selectDocumentiTecniciGenerati, new Object[] { ngara });
        Boolean trovatoDocumentoInserito = (documentiTecniciGenerati != null && documentiTecniciGenerati.size() > 0) ? true : false;
        if (!trovatoDocumentoInserito) {
          Long MaxNorddocg = new Long(0);
          Long norddocg = (Long) sqlManager.getObject(selectMaxNord, new Object[] { codgar });
          if (norddocg != null) {
            MaxNorddocg = norddocg;
          }
          logger.debug("norddocg = " + norddocg + ", codgar = " + codgar);
          sqlManager.update(insertDoc, new Object[]{codgar, ngara, MaxNorddocg + 1});
        }
      } else {
        sqlManager.update(delete, new Object[]{ngara});
        logger.debug("codgar = " + codgar);
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nell'aggiornamento dei documenti richiesti", "preUpdate", e);
    }
  }

  /* (non-Javadoc)
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postUpdate(it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

}