/**
 * 
 */
package it.eldasoft.sil.pg.db.dao.ibatis;

import java.util.List;
import java.util.Map;

import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.sil.pg.db.domain.Garcpv;
import it.eldasoft.sil.pg.db.domain.Gare;
import it.eldasoft.sil.pg.db.domain.Torn;
import it.eldasoft.sil.pg.db.dao.DgueDAO;
import it.eldasoft.sil.pg.db.domain.DgueBatch;
import it.eldasoft.sil.pg.db.domain.DgueBatchDoc;
import it.eldasoft.sil.pg.db.domain.DgueBatchStatus;
import it.eldasoft.sil.pg.db.domain.DgueElabSub;
import it.eldasoft.sil.pg.db.domain.DgueElaborazione;
import it.eldasoft.sil.pg.db.domain.Pubbli;


/**
 * Implementazione dei metodi per la lettura delle tabelle per DGUE
 * (eg. TORN, GARE ...)
 * @author gabriele.nencini
 *
 */
public class SqlMapDgueDao extends SqlMapClientDaoSupportBase implements DgueDAO {

  @Override
  public Torn getTornFullByPK(String codgar) {
    return (Torn) this.getSqlMapClientTemplate().queryForObject("getTornFullByPK", codgar);
  }

  @Override
  public Gare getGareFullByPK(String ngara) {
    return (Gare) this.getSqlMapClientTemplate().queryForObject("getGareFullByPK", ngara);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Gare> getGareFullByFKDirect(String codgar1) {
    return this.getSqlMapClientTemplate().queryForList("getGareFullByFKDirect", codgar1);
  }

  @Override
  public Garcpv getGarcpvFullByPK(Garcpv input) {
    return (Garcpv) this.getSqlMapClientTemplate().queryForObject("getGarcpvFullByPK", input);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Garcpv> getGarcpvFullByNgaraLike(Garcpv input) {
    return this.getSqlMapClientTemplate().queryForList("getGarcpvFullByNgaraLike", input);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DgueBatch> getListDgueBatchByStatus(DgueBatchStatus status) {
    return this.getSqlMapClientTemplate().queryForList("getListDgueBatchByStatus", status);
  }

  @Override
  public void updateDgueBatch(List<DgueBatch> batchList) {
    // TODO Auto-generated method stub
//    this.getSqlMapClientTemplate().queryForObject(statementName);
  }

  @Override
  public void updateDgueBatchDocStatus(DgueBatchDoc element) {
    this.getSqlMapClientTemplate().update("updateDgueBatchDocStatus", element);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<DgueBatchDoc> getListDgueBatchDocByDgueBatchAndStatus(DgueBatchDoc element) {
    return this.getSqlMapClientTemplate().queryForList("getListDgueBatchDocByDgueBatchAndStatus", element);
  }

  @Override
  public void updateDgueBatchStatus(DgueBatch batch) {
    this.getSqlMapClientTemplate().update("updateDgueBatchStatus", batch);
  }
  
  @Override
  public void insertSingleDgueElaborazione(DgueElaborazione element) {
    this.getSqlMapClientTemplate().insert("insertSingleDgueElaborazione", element);    
  }
  
  @Override
  public void insertSingleDgueElabSub(DgueElabSub element) {
    this.getSqlMapClientTemplate().insert("insertSingleDgueElabSub", element);    
  }

  @Override
  public void updateDitgStatodguepreq(Map<String,Object> element) {
    this.getSqlMapClientTemplate().update("updateDitgStatodguepreq", element);
  }

  @Override
  public void updateDitgStatodgueamm(Map<String,Object> element) {
    this.getSqlMapClientTemplate().update("updateDitgStatodgueamm", element);
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Pubbli> getListPubbliFullSelectByCodgar(String codgar) {
    return this.getSqlMapClientTemplate().queryForList("getListPubbliFullSelectByCodgar", codgar);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public List<Pubbli> getListPubbliFullSelectByCodgarAndTIPPUB3(String codgar) {
    return this.getSqlMapClientTemplate().queryForList("getListPubbliFullSelectByCodgarAndTIPPUB3", codgar);
  }
  
}
