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

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore  per la pagina "Svincolo cauzione provvisoria"
 *
 * @author  Cristian Febas
 */
public class GestoreDITGSTATI extends AbstractGestoreEntita {


    @Override
    public String getEntita() {
        return "DITGSTATI";
    }

    public GestoreDITGSTATI() {
        super(false);
      }


      public GestoreDITGSTATI(boolean isGestoreStandard) {
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

            int numeroDitte = 0;
            String i_codgar = null;
            String i_ngara = null;
            String i_dittao = null;
            String i_ncomsvip = null;
            Date i_dcomsvip = null;
            Long i_fasgar = new Long(8);
            Long i_stepgar = new Long(80);

            String numDitte = this.getRequest().getParameter("numeroDitte");
    if (numDitte != null && numDitte.length() > 0) numeroDitte = UtilityNumeri.convertiIntero(numDitte).intValue();
    for (int i = 1; i <= numeroDitte; i++) {

      if ((datiForm.isColumn("DITGSTATI.NCOMSVIP_" + i) & datiForm.isModifiedColumn("DITGSTATI.NCOMSVIP_" + i))
          || (datiForm.isColumn("DITGSTATI.DCOMSVIP_" + i) & datiForm.isModifiedColumn("DITGSTATI.DCOMSVIP_" + i))) {

        i_codgar = datiForm.getColumn("DITG.CODGAR5_" + i).getValue().stringValue();
        i_ngara = datiForm.getColumn("DITG.NGARA5_" + i).getValue().stringValue();
        i_dittao = datiForm.getColumn("DITG.DITTAO_" + i).getValue().stringValue();

        i_ncomsvip = datiForm.getColumn("DITGSTATI.NCOMSVIP_" + i).getValue().stringValue();
        i_dcomsvip = datiForm.getColumn("DITGSTATI.DCOMSVIP_" + i).getValue().dataValue();

        Vector elencoCampi = new Vector();
        elencoCampi.add(new DataColumn("DITGSTATI.CODGAR", new JdbcParametro(JdbcParametro.TIPO_TESTO, i_codgar)));
        elencoCampi.add(new DataColumn("DITGSTATI.NGARA", new JdbcParametro(JdbcParametro.TIPO_TESTO, i_ngara)));
        elencoCampi.add(new DataColumn("DITGSTATI.DITTAO", new JdbcParametro(JdbcParametro.TIPO_TESTO, i_dittao)));
        elencoCampi.add(new DataColumn("DITGSTATI.FASGAR", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, i_fasgar)));
        elencoCampi.add(new DataColumn("DITGSTATI.STEPGAR", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, i_stepgar)));
        elencoCampi.add(new DataColumn("DITGSTATI.NCOMSVIP", new JdbcParametro(JdbcParametro.TIPO_TESTO, i_ncomsvip)));
        elencoCampi.add(new DataColumn("DITGSTATI.DCOMSVIP", new JdbcParametro(JdbcParametro.TIPO_DATA, i_dcomsvip)));

        DataColumnContainer dccDITGSTATI = new DataColumnContainer(elencoCampi);

        dccDITGSTATI.getColumn("DITGSTATI.CODGAR").setChiave(true);
        dccDITGSTATI.getColumn("DITGSTATI.NGARA").setChiave(true);
        dccDITGSTATI.getColumn("DITGSTATI.DITTAO").setChiave(true);
        dccDITGSTATI.getColumn("DITGSTATI.FASGAR").setChiave(true);
        dccDITGSTATI.getColumn("DITGSTATI.STEPGAR").setChiave(true);

        try {

          // Verifica se esiste l'occorrenza in UTENTI_IDS
          String selectDITGSTATI = "select ncomsvip, dcomsvip from DITGSTATI" + " where codgar=? and ngara=? and dittao=? and fasgar=?";
          boolean esisteOccDitgstati = false;
/*
          Long countOccDitgstati = (Long) this.sqlManager.getObject(selectDITGSTATI, new Object[] {i_codgar, i_ngara, i_dittao, i_fasgar });
          if (countOccDitgstati != null && countOccDitgstati.longValue() > 0) {
            esisteOccDitgstati = true;
          }
*/
          String ncomsvip = null;
          Date dcomsvip = null;
          List OccDitgstati= this.sqlManager.getVector(selectDITGSTATI, new Object[] {i_codgar, i_ngara, i_dittao, i_fasgar });
          if (OccDitgstati != null && OccDitgstati.size() > 0) {
            esisteOccDitgstati = true;
            ncomsvip = (String) SqlManager.getValueFromVectorParam(OccDitgstati, 0).getValue();
            dcomsvip = (Date) SqlManager.getValueFromVectorParam(OccDitgstati, 1).getValue();

          }
          dccDITGSTATI.getColumn("DITGSTATI.NCOMSVIP").setObjectOriginalValue(ncomsvip);
          dccDITGSTATI.getColumn("DITGSTATI.DCOMSVIP").setObjectOriginalValue(dcomsvip);
          if (esisteOccDitgstati) {
            dccDITGSTATI.getColumn("DITGSTATI.CODGAR").setObjectOriginalValue(i_codgar);
            dccDITGSTATI.getColumn("DITGSTATI.NGARA").setObjectOriginalValue(i_ngara);
            dccDITGSTATI.getColumn("DITGSTATI.DITTAO").setObjectOriginalValue(i_dittao);
            dccDITGSTATI.getColumn("DITGSTATI.FASGAR").setObjectOriginalValue(i_fasgar);
            dccDITGSTATI.getColumn("DITGSTATI.STEPGAR").setObjectOriginalValue(i_stepgar);
            i_ncomsvip = UtilityStringhe.convertiNullInStringaVuota(i_ncomsvip);
            if("".equals(i_ncomsvip) && i_dcomsvip == null){
              dccDITGSTATI.delete("DITGSTATI", sqlManager);
            }else{
              dccDITGSTATI.update("DITGSTATI", sqlManager);
            }
          } else {
            dccDITGSTATI.insert("DITGSTATI", sqlManager);
          }
        } catch (SQLException e) {
          throw new GestoreException("Errore nell'aggiornamento dei dati in DITGSTATI", null, e);

        }
      }//if
    }//for

    }//if

}