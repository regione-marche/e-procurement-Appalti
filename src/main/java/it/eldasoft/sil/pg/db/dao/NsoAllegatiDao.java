package it.eldasoft.sil.pg.db.dao;

import java.util.List;

import it.eldasoft.sil.pg.db.domain.nso.NsoAllegato;

public interface NsoAllegatiDao {
  public List<NsoAllegato> getNsoAllegatiByNsoOrdiniId(Long nsoOrdiniId);
}
