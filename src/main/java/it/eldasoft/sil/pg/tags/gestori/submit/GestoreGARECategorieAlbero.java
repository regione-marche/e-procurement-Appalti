package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

public class GestoreGARECategorieAlbero extends AbstractGestoreEntita {

  static Logger      logger     = Logger.getLogger(GestoreGARECategorieAlbero.class);

  private SqlManager sqlManager = null;

  private final static String FILTRO_CARATTERI_DA_ESCLUDERE = " and (caisim not like '%/%' or caisim not like '%. %' or caisim not like '% .%')";

  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", this.getServletContext(), SqlManager.class);
  }

  @Override
  public String getEntita() {
    return "GARE";
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    try {

      String ngara = datiForm.getString("GARE.NGARA");

      String unchecked = datiForm.getString("UNCHECKED");
      if (unchecked != null && !"".equals(unchecked.trim())) {
        String[] nodes_unchecked = unchecked.split(",");
        for (int iunck = 0; iunck < nodes_unchecked.length; iunck++) {
          String node_unck = nodes_unchecked[iunck];
          if (node_unck.startsWith("R_____")) {
            String tiplavg_s = node_unck.substring(6);
            if (tiplavg_s != null) {
              String deleteOPES = "delete from opes where ngara3 = ? "
                  + " and catoff in (select caisim from cais where tiplavg = ?) "
                  + " and not exists (select nopega from meartcat where opes.ngara3 = meartcat.ngara and opes.nopega = meartcat.nopega)";
              this.sqlManager.update(deleteOPES, new Object[] { ngara, new Long(tiplavg_s) });
            }
          } else if (node_unck.startsWith("T_____")) {
            String titcat = node_unck.substring(6, node_unck.indexOf("_____R"));
            String tiplavg_s = node_unck.substring(node_unck.indexOf("R_____") + 6);
            if (titcat != null && tiplavg_s != null) {
              String deleteOPES = "delete from opes where ngara3 = ? "
                  + " and catoff in (select caisim from cais where tiplavg = ? and titcat = ?) "
                  + "  and not exists (select nopega from meartcat where opes.ngara3 = meartcat.ngara and opes.nopega = meartcat.nopega)";
              this.sqlManager.update(deleteOPES, new Object[] { ngara, new Long(tiplavg_s), titcat });
            }

          } else if (node_unck.startsWith("C_____")) {
            String caisim = node_unck.substring(6, node_unck.indexOf("_____", 6));
            String deleteOPES = "delete from opes where ngara3 = ? "
                + " and catoff in (select caisim from cais where caisim = ? or codliv1 = ? or codliv2 = ? or codliv3 = ? or codliv4 = ?) "
                + " and not exists (select nopega from meartcat where opes.ngara3 = meartcat.ngara and opes.nopega = meartcat.nopega)";
            this.sqlManager.update(deleteOPES, new Object[] { ngara, caisim, caisim, caisim, caisim, caisim });
          }
        }
      }

      String checked = datiForm.getString("CHECKED");
      if (checked != null && !"".equals(checked.trim())) {
        String contaOPES = "select count(*) from opes where ngara3 = ? and catoff = ?";
        String maxOPES = "select max(nopega) from opes where ngara3 = ?";
        String insertOPES = "insert into opes (ngara3, nopega, catoff) values (?,?,?)";

        String[] nodes_checked = checked.split(",");
        for (int ick = 0; ick < nodes_checked.length; ick++) {
          String node_ck = nodes_checked[ick];

          List<?> datiCAIS = null;
          if (node_ck.startsWith("R_____")) {
            String tiplavg_s = node_ck.substring(6);
            if (tiplavg_s != null) {
              String selectCAIS = "select caisim from cais where tiplavg = ? and (isarchi is null or isarchi <> '1')" + FILTRO_CARATTERI_DA_ESCLUDERE;
              datiCAIS = this.sqlManager.getListVector(selectCAIS, new Object[] { new Long(tiplavg_s) });
            }
          } else if (node_ck.startsWith("T_____")) {
            String titcat = node_ck.substring(6, node_ck.indexOf("_____R"));
            String tiplavg_s = node_ck.substring(node_ck.indexOf("R_____") + 6);
            String selectCAIS = "select caisim from cais where tiplavg = ? and titcat = ? and (isarchi is null or isarchi <> '1')" + FILTRO_CARATTERI_DA_ESCLUDERE;
            datiCAIS = this.sqlManager.getListVector(selectCAIS, new Object[] { new Long(tiplavg_s), titcat });

          } else if (node_ck.startsWith("C_____")) {
            String caisim = node_ck.substring(6, node_ck.indexOf("_____", 6));
            String selectCAIS = "select caisim from cais where (caisim = ? or codliv1 = ? or codliv2 = ? or codliv3 = ? or codliv4 = ?) and (isarchi is null or isarchi <> '1')" + FILTRO_CARATTERI_DA_ESCLUDERE;
            datiCAIS = this.sqlManager.getListVector(selectCAIS, new Object[] { caisim, caisim, caisim, caisim, caisim });
          }

          if (datiCAIS != null && datiCAIS.size() > 0) {
            for (int icais = 0; icais < datiCAIS.size(); icais++) {
              String caisim_insert = (String) SqlManager.getValueFromVectorParam(datiCAIS.get(icais), 0).getValue();
              Long conta_caisim = (Long) this.sqlManager.getObject(contaOPES, new Object[] { ngara, caisim_insert });
              if (conta_caisim != null && conta_caisim.longValue() == 0) {
                Long nopegaMax = (Long) this.sqlManager.getObject(maxOPES, new Object[] { ngara });
                if (nopegaMax == null) nopegaMax = new Long(0);
                nopegaMax = new Long(nopegaMax.longValue() + 1);
                sqlManager.update(insertOPES, new Object[] { ngara, nopegaMax, caisim_insert });
              }
            }
          }

        }
      }

      String undetermined = datiForm.getString("UNDETERMINED");
      if (undetermined != null && !"".equals(undetermined.trim())) {
        String contaOPES = "select count(*) from opes where ngara3 = ? and catoff = ?";
        String maxOPES = "select max(nopega) from opes where ngara3 = ?";
        String insertOPES = "insert into opes (ngara3, nopega, catoff) values (?,?,?)";

        String[] nodes_undetermine = undetermined.split(",");
        for (int iunde = 0; iunde < nodes_undetermine.length; iunde++) {
          String node_unde = nodes_undetermine[iunde];

          if (node_unde.startsWith("C_____")) {
            String caisim_undetemined = node_unde.substring(6, node_unde.indexOf("_____", 6));
            Long conta_caisim_undetermined = (Long) this.sqlManager.getObject(contaOPES, new Object[] { ngara, caisim_undetemined });
            if (conta_caisim_undetermined != null && conta_caisim_undetermined.longValue() == 0) {
              Long nopegaMax_undetemined = (Long) this.sqlManager.getObject(maxOPES, new Object[] { ngara });
              if (nopegaMax_undetemined == null) nopegaMax_undetemined = new Long(0);
              nopegaMax_undetemined = new Long(nopegaMax_undetemined.longValue() + 1);
              sqlManager.update(insertOPES, new Object[] { ngara, nopegaMax_undetemined, caisim_undetemined });
            }
          }
        }
      }

    } catch (SQLException e) {
      throw new GestoreException("Errore nella memorizzazione delle categorie della gara", null, e);
    }

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

  }

}
