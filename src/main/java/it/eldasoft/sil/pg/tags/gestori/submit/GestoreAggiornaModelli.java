/*
 * Created on 19/set/2018
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.sql.SQLException;
import java.util.List;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;


public class GestoreAggiornaModelli extends AbstractGestoreEntita{

  @Override
  public String getEntita() {
    // TODO Auto-generated method stub
    return "G1CRIMOD";
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub
    
  }
  
  @Override
  public void afterInsertEntita(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    
    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);
    
    String ngara = datiForm.getString("NGARA");
    Long idcrimod = datiForm.getLong("ID");
    
    try {
      int goevmodId = 0;
      int cridefId = 0;
      
      List datiGoev = sqlManager.getListVector("select goev.norpar,goev.tippar,goev.maxpun,goev.minpun,goev.despar,goev.livpar,goev.necvan1,goev.norpar1,goev.necvan,goev.isnoprz, " +
            "g1cridef.descri,g1cridef.maxpun,g1cridef.formato,g1cridef.numdeci,g1cridef.modpunti,g1cridef.modmanu,g1cridef.formula,g1cridef.id " +
            "from goev left join g1cridef on g1cridef.ngara = goev.ngara and goev.necvan = g1cridef.necvan where goev.ngara = ? " +
            "order by goev.necvan", new Object[] { ngara });

      if (datiGoev != null && datiGoev.size() > 0) {
        for (int i = 0; i < datiGoev.size(); i++) {
          //goev
          Double norpar = SqlManager.getValueFromVectorParam(datiGoev.get(i),
              0).doubleValue();
          Long tippar = SqlManager.getValueFromVectorParam(datiGoev.get(i),
              1).longValue();
          Double maxpun = SqlManager.getValueFromVectorParam(datiGoev.get(i),
              2).doubleValue();
          Double minpun = SqlManager.getValueFromVectorParam(datiGoev.get(i),
              3).doubleValue();
          String despar = SqlManager.getValueFromVectorParam(datiGoev.get(i),
              4).stringValue();
          Long livpar = SqlManager.getValueFromVectorParam(datiGoev.get(i),
              5).longValue();
          Long necvan1 = SqlManager.getValueFromVectorParam(datiGoev.get(i),
              6).longValue();
          Double norpar1 = SqlManager.getValueFromVectorParam(datiGoev.get(i),
              7).doubleValue();
          Long necvan = SqlManager.getValueFromVectorParam(datiGoev.get(i),
              8).longValue();
          String isnoprz = SqlManager.getValueFromVectorParam(datiGoev.get(i),
              9).stringValue();
          //g1cridef
          String g1descri = SqlManager.getValueFromVectorParam(datiGoev.get(i),
              10).stringValue();
          Double g1maxpun = SqlManager.getValueFromVectorParam(datiGoev.get(i),
              11).doubleValue();
          Long formato = SqlManager.getValueFromVectorParam(datiGoev.get(i),
              12).longValue();
          Long numdeci = SqlManager.getValueFromVectorParam(datiGoev.get(i),
              13).longValue();
          Long modpunti = SqlManager.getValueFromVectorParam(datiGoev.get(i),
              14).longValue();
          Long modmanu = SqlManager.getValueFromVectorParam(datiGoev.get(i),
              15).longValue();
          Long formula = SqlManager.getValueFromVectorParam(datiGoev.get(i),
              16).longValue();
          Long vecchioIdCridef = SqlManager.getValueFromVectorParam(datiGoev.get(i),
              17).longValue();
          
          goevmodId = genChiaviManager.getNextId("GOEVMOD");
          cridefId = genChiaviManager.getNextId("G1CRIDEF");
          
          sqlManager.update("insert into goevmod (id,idcrimod,necvan,norpar,tippar,isnoprz,maxpun,minpun,despar,livpar,necvan1,norpar1) values (?,?,?,?,?,?,?,?,?,?,?,?)",  new Object[] { goevmodId,idcrimod,necvan,norpar,tippar,isnoprz,maxpun,minpun,despar,livpar,necvan1,norpar1});
          if(livpar != 3){
            sqlManager.update("insert into g1cridef (id,idgoevmod,descri,maxpun,formato,numdeci,modpunti,modmanu,formula) values (?,?,?,?,?,?,?,?,?)",  new Object[] { cridefId,goevmodId,g1descri,g1maxpun,formato,numdeci,modpunti,modmanu,formula});
          }
          List datiCrireg = sqlManager.getListVector("select puntuale, valmin, valmax, coeffi from g1crireg where idcridef = ?", new Object[] { vecchioIdCridef });
          int idcrireg=0;
          if (datiCrireg != null && datiCrireg.size() > 0) {
            for (int n = 0; n < datiCrireg.size(); n++) {
              //goev
              String puntuale = SqlManager.getValueFromVectorParam(datiCrireg.get(n),
                  0).stringValue();
              Double valmin = SqlManager.getValueFromVectorParam(datiCrireg.get(n),
                  1).doubleValue();
              Double valmax = SqlManager.getValueFromVectorParam(datiCrireg.get(n),
                  2).doubleValue();
              Double coeffi = SqlManager.getValueFromVectorParam(datiCrireg.get(n),
                  3).doubleValue();
              idcrireg = genChiaviManager.getNextId("G1CRIREG");
              sqlManager.update("insert into g1crireg (id,idcridef,puntuale,valmin,valmax,coeffi) values (?,?,?,?,?,?)",  new Object[] { idcrireg,cridefId,puntuale,valmin,valmax,coeffi});
              
            }
          }
        }
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void preDelete(TransactionStatus arg0, DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void preInsert(TransactionStatus arg0, DataColumnContainer datiForm) throws GestoreException {
    GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
        this.getServletContext(), GenChiaviManager.class);   
    
    int idcrimod = genChiaviManager.getNextId("G1CRIMOD");
    
    datiForm.getColumn("G1CRIMOD.ID").setChiave(true);
    datiForm.setValue("G1CRIMOD.ID", new Long(idcrimod));
  }

  @Override
  public void preUpdate(TransactionStatus arg0, DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

}
