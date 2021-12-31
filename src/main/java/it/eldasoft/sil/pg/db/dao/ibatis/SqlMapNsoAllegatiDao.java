package it.eldasoft.sil.pg.db.dao.ibatis;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.sil.pg.db.dao.NsoAllegatiDao;
import it.eldasoft.sil.pg.db.domain.nso.NsoAllegato;

public class SqlMapNsoAllegatiDao extends SqlMapClientDaoSupportBase implements NsoAllegatiDao {
  private static final Logger logger = Logger.getLogger(SqlMapNsoAllegatiDao.class);

  @SuppressWarnings("unchecked")
  @Override
  public List<NsoAllegato> getNsoAllegatiByNsoOrdiniId(Long nsoOrdiniId) {
    logger.debug("Getting list of attachments for NsoOrdine "+nsoOrdiniId);
    List<NsoAllegato> list = this.getSqlMapClientTemplate().queryForList("getNsoAllegatiByNsoOrdiniId", nsoOrdiniId);
    if(list==null) {
      list = Collections.EMPTY_LIST;
    }
    logger.info("Extracted "+list.size()+" attachments for NsoOrdine "+nsoOrdiniId);
    return list;
  }
}
