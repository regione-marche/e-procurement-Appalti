package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.MEPAManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

public class GestoreMERICARTLista extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "MERICART";
  }

  public GestoreMERICARTLista() {
    super(false);
  }

  public GestoreMERICARTLista(boolean isGestoreStandard) {
    super(isGestoreStandard);
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
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    try {
      for (int i = 1; i < datiForm.getColonne().size(); i++) {
        if (datiForm.isColumn("MERICART.ID_" + i) && datiForm.isColumn("MERICART.QUANTI_" + i) && datiForm.isColumn("MERICART.ID_" + i)) {
          DataColumnContainer datiFormRiga = new DataColumnContainer(datiForm.getColumnsBySuffix("_" + i, false));
          if (datiFormRiga.isModifiedTable("MERICART")) {
            Long mericart_id = datiFormRiga.getLong("MERICART.ID");
            Double mericart_quanti = datiFormRiga.getDouble("MERICART.QUANTI");
            String desdet1=null;
            String desdet2 = null;
            Double quadet1 = null;
            Double quadet2 = null;
            if(datiFormRiga.isColumn("MERICART.DESDET1"))
              desdet1 = datiFormRiga.getString("MERICART.DESDET1");
            if(datiFormRiga.isColumn("MERICART.DESDET2"))
              desdet2 = datiFormRiga.getString("MERICART.DESDET2");
            if(datiFormRiga.isColumn("MERICART.QUADET1"))
              quadet1 = datiFormRiga.getDouble("MERICART.QUADET1");
            if(datiFormRiga.isColumn("MERICART.QUADET2"))
              quadet2 = datiFormRiga.getDouble("MERICART.QUADET2");
            if (mericart_quanti != null && mericart_quanti.doubleValue() > 0) {
              this.sqlManager.update("update mericart set quanti = ?, desdet1=?, desdet2=?, quadet1=?, quadet2=? where id = ?",
                  new Object[] { mericart_quanti, desdet1, desdet2, quadet1, quadet2, mericart_id });
            }

          }
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nell'aggiornamento della quantita' degli articoli", "MERICART.aggiornamentoquantita", e);
    }
  }
}
