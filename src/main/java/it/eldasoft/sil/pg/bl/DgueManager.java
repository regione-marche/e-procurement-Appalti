package it.eldasoft.sil.pg.bl;

import java.util.HashMap;
import java.util.List;

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
 * Classe di Manager per la gestione delle entita Gare
 * (eg. TORN,GARE, ... )
 * @author gabriele.nencini
 *
 */
public class DgueManager {

  private DgueDAO dgueDao;
  
  /**
   * @return the gareDao
   */
  public DgueDAO getGareDao() {
    return dgueDao;
  }

  /**
   * @param gareDao the gareDao to set
   */
  public void setDgueDao(DgueDAO dgueDao) {
    this.dgueDao = dgueDao;
  }

  public Torn getTornFullByPK(String codgar) {
    return dgueDao.getTornFullByPK(codgar);
  }
  
  public Gare getGareFullByPK(String ngara) {
    return dgueDao.getGareFullByPK(ngara);
  }
  
  public List<Gare> getGareFullByFKDirect(String codgar1) {
    return dgueDao.getGareFullByFKDirect(codgar1);
  }
  
  public Garcpv getGarcpvFullByPK(Garcpv input) {
    return dgueDao.getGarcpvFullByPK(input);
  }
  public List<Garcpv> getGarcpvFullByNgaraLike(Garcpv input){
    return dgueDao.getGarcpvFullByNgaraLike(input);
  }
  
  public List<DgueBatch> getListDgueBatchByStatus(DgueBatchStatus status) {
    return dgueDao.getListDgueBatchByStatus(status);
  }
  public List<DgueBatchDoc> getListDgueBatchDocByDgueBatchAndStatus(DgueBatchDoc element) {
    return dgueDao.getListDgueBatchDocByDgueBatchAndStatus(element);
  }
  
  public void updateDgueBatchStatus(DgueBatch elem) {
    this.dgueDao.updateDgueBatchStatus(elem);
  }
  public void updateDgueBatchDocStatus(DgueBatchDoc elem) {
    this.dgueDao.updateDgueBatchDocStatus(elem);
  }
  
  public void insertSingleDgueElaborazione(DgueElaborazione element) {
    this.dgueDao.insertSingleDgueElaborazione(element);
  }
  
  public void insertSingleDgueElabSub(DgueElabSub element) {
    this.dgueDao.insertSingleDgueElabSub(element);
  }
  
  public void updateDitgStatodguepreq(DgueBatch element, Integer stato) {
    HashMap<String,Object> map = new HashMap<String,Object>();
    map.put("stato", stato);
    map.put("dgueBatch", element);
    this.dgueDao.updateDitgStatodguepreq(map);
  }
  public void updateDitgStatodgueamm(DgueBatch element, Integer stato) {
    HashMap<String,Object> map = new HashMap<String,Object>();
    map.put("stato", stato);
    map.put("dgueBatch", element);
    this.dgueDao.updateDitgStatodgueamm(map);
  }
  
  public List<Pubbli> getListPubbliFullSelectByCodgar(String codgar) {
    return  this.dgueDao.getListPubbliFullSelectByCodgar(codgar);
  }
  public List<Pubbli> getListPubbliFullSelectByCodgarAndTIPPUB3(String codgar) {
    return  this.dgueDao.getListPubbliFullSelectByCodgarAndTIPPUB3(codgar);
  }
}
