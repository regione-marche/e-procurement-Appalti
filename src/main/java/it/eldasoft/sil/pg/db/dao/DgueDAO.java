package it.eldasoft.sil.pg.db.dao;

import java.util.List;
import java.util.Map;

import it.eldasoft.sil.pg.db.domain.Garcpv;
import it.eldasoft.sil.pg.db.domain.Gare;
import it.eldasoft.sil.pg.db.domain.Torn;
import it.eldasoft.sil.pg.db.domain.DgueBatch;
import it.eldasoft.sil.pg.db.domain.DgueBatchDoc;
import it.eldasoft.sil.pg.db.domain.DgueBatchStatus;
import it.eldasoft.sil.pg.db.domain.DgueElabSub;
import it.eldasoft.sil.pg.db.domain.DgueElaborazione;
import it.eldasoft.sil.pg.db.domain.Pubbli;

/**
 * Interfaccia per i metodi di accesso alle tabelle Gare
 * (eg. TORN,GARE, ... )
 * @author gabriele.nencini
 *
 */
public interface DgueDAO {
  public Torn getTornFullByPK(String codgar);
  public Gare getGareFullByPK(String ngara);
  public List<Gare> getGareFullByFKDirect(String codgar1);
  public Garcpv getGarcpvFullByPK(Garcpv input);
  public List<Garcpv> getGarcpvFullByNgaraLike(Garcpv input);
  public List<DgueBatch> getListDgueBatchByStatus(DgueBatchStatus status);
  public void updateDgueBatchStatus(DgueBatch batch);
  public void updateDgueBatch(List<DgueBatch> batchList);
  public List<DgueBatchDoc> getListDgueBatchDocByDgueBatchAndStatus(DgueBatchDoc element);
  public void updateDgueBatchDocStatus(DgueBatchDoc element);
  public void insertSingleDgueElaborazione(DgueElaborazione element);
  public void insertSingleDgueElabSub(DgueElabSub element);
  public void updateDitgStatodguepreq(Map<String,Object> element);
  public void updateDitgStatodgueamm(Map<String,Object> element);
  public List<Pubbli> getListPubbliFullSelectByCodgar(String codgar);
  public List<Pubbli> getListPubbliFullSelectByCodgarAndTIPPUB3(String codgar);
}
