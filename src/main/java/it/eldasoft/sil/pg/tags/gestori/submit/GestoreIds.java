/*
 * Created on 18-Set-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per gestire gli ids
 *
 * @author Cristian Febas
 */
public class GestoreIds extends AbstractGestoreEntita {


    @Override
    public String getEntita() {
        return "V_LISTA_IDS";
    }

    public GestoreIds() {
        super(false);
      }


      public GestoreIds(boolean isGestoreStandard) {
        super(isGestoreStandard);
      }

    @Override
    public void postDelete(DataColumnContainer datiForm)
            throws GestoreException {
    }


    @Override
    public void postInsert(DataColumnContainer datiForm)
            throws GestoreException {
    }


    @Override
    public void postUpdate(DataColumnContainer datiForm)
            throws GestoreException {
    }


    @Override
    public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
            throws GestoreException {
      ;
    }

    @Override
    public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    }


    @Override
    public void preUpdate(TransactionStatus status, DataColumnContainer datiForm )
            throws GestoreException {

            int numeroIds = 0;
            Long id_utente=null;
            Long ids_prog=null;
            Date data_scad_emrdo = null;
            Date data_assegnazione = null;
            Long flag_respingi =null;
            Long flag_evadi =null;
            Long flag_annulla =null;
            String numIds = this.getRequest().getParameter("numeroIds");
            if(numIds != null && numIds.length() > 0)
              numeroIds =  UtilityNumeri.convertiIntero(numIds).intValue();
              for (int i = 1; i <= numeroIds; i++) {


              if ((datiForm.isColumn("V_LISTA_IDS.ID_UTENTE_" + i) &  datiForm.isModifiedColumn("V_LISTA_IDS.ID_UTENTE_" + i)) ||
                  (datiForm.isColumn("V_LISTA_IDS.DATA_SCADENZA_EMISSIONE_RDO_" + i) &  datiForm.isModifiedColumn("V_LISTA_IDS.DATA_SCADENZA_EMISSIONE_RDO_" + i)) ||
                  (datiForm.isColumn("V_LISTA_IDS.FLAG_RESPINGI_" + i) &  datiForm.isModifiedColumn("V_LISTA_IDS.FLAG_RESPINGI_" + i)) ||
                  (datiForm.isColumn("V_LISTA_IDS.FLAG_EVADI_" + i) &  datiForm.isModifiedColumn("V_LISTA_IDS.FLAG_EVADI_" + i)) ||
                  (datiForm.isColumn("V_LISTA_IDS.FLAG_ANNULLA_" + i) &  datiForm.isModifiedColumn("V_LISTA_IDS.FLAG_ANNULLA_" + i))){

                ids_prog = datiForm.getColumn("V_LISTA_IDS.IDS_PROG_" + i).getValue().longValue();
                id_utente = datiForm.getColumn("V_LISTA_IDS.ID_UTENTE_" + i).getValue().longValue();
                data_scad_emrdo = datiForm.getColumn("V_LISTA_IDS.DATA_SCADENZA_EMISSIONE_RDO_" + i).getValue().dataValue();
                flag_respingi = datiForm.getColumn("V_LISTA_IDS.FLAG_RESPINGI_" + i).getValue().longValue();
                flag_evadi = datiForm.getColumn("V_LISTA_IDS.FLAG_EVADI_" + i).getValue().longValue();
                flag_annulla = datiForm.getColumn("V_LISTA_IDS.FLAG_ANNULLA_" + i).getValue().longValue();

                if ((datiForm.isColumn("V_LISTA_IDS.ID_UTENTE_" + i) &  datiForm.isModifiedColumn("V_LISTA_IDS.ID_UTENTE_" + i))){
                  String dataOdiernaString = UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA);
                  data_assegnazione = UtilityDate.convertiData(dataOdiernaString, UtilityDate.FORMATO_GG_MM_AAAA);
                }


                Vector elencoCampi = new Vector();
                elencoCampi.add(new DataColumn("UTENTI_IDS.IDS_PROG",
                    new JdbcParametro(JdbcParametro.TIPO_NUMERICO, ids_prog)));
                elencoCampi.add(new DataColumn("UTENTI_IDS.ID_UTENTE",
                    new JdbcParametro(JdbcParametro.TIPO_NUMERICO, id_utente)));
                elencoCampi.add(new DataColumn("UTENTI_IDS.DATA_SCADENZA_EMISSIONE_RDO",
                    new JdbcParametro(JdbcParametro.TIPO_DATA, data_scad_emrdo)));
                elencoCampi.add(new DataColumn("UTENTI_IDS.FLAG_RESPINGI",
                    new JdbcParametro(JdbcParametro.TIPO_NUMERICO, flag_respingi)));
                elencoCampi.add(new DataColumn("UTENTI_IDS.FLAG_EVADI",
                    new JdbcParametro(JdbcParametro.TIPO_NUMERICO, flag_evadi)));
                elencoCampi.add(new DataColumn("UTENTI_IDS.FLAG_ANNULLA",
                    new JdbcParametro(JdbcParametro.TIPO_NUMERICO, flag_annulla )));
                if(data_assegnazione != null){
                  elencoCampi.add(new DataColumn("UTENTI_IDS.DATA_ASSEGNAZIONE",
                      new JdbcParametro(JdbcParametro.TIPO_DATA, data_assegnazione)));
                }

                DataColumnContainer dccUTENTI_IDS = new DataColumnContainer(elencoCampi);

                dccUTENTI_IDS.getColumn("UTENTI_IDS.IDS_PROG").setChiave(true);


              try {

                //Verifica se esiste l'occorrenza in UTENTI_IDS
                String selectUTENTI_IDS = "select count(*) from UTENTI_IDS where ids_prog = ? ";
                boolean esisteIds = false;
                Long countIds = (Long) this.sqlManager.getObject(selectUTENTI_IDS, new Object[]{ ids_prog });
                if (countIds != null && countIds.longValue()>0){
                  esisteIds = true;
                }


                dccUTENTI_IDS.getColumn("UTENTI_IDS.ID_UTENTE").setObjectOriginalValue("");
                if (esisteIds){
                  dccUTENTI_IDS.getColumn("UTENTI_IDS.IDS_PROG").setObjectOriginalValue(ids_prog);
                  if(id_utente != null){
                    dccUTENTI_IDS.update("UTENTI_IDS", sqlManager);
                  }else{
                    dccUTENTI_IDS.delete("UTENTI_IDS", sqlManager);
                  }

                }else{
                  dccUTENTI_IDS.getColumn("UTENTI_IDS.IDS_PROG").setObjectOriginalValue("");
                  dccUTENTI_IDS.insert("UTENTI_IDS", sqlManager);
                }
              } catch (SQLException e) {
                   throw new GestoreException("Errore nell'aggiornamento dei dati in UTENTI_IDS",null, e);
              }
              }
          }


    }






}