package it.eldasoft.sil.w3.db.dao.ibatis;

import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.sil.w3.db.dao.DocumentoAllegatoDao;


import java.util.HashMap;

import org.springframework.dao.DataAccessException;



public class SqlMapDocumentoAllegatoDao extends SqlMapClientDaoSupportBase implements
    DocumentoAllegatoDao {
  
  public BlobFile getDocumentoAllegato(HashMap params) throws DataAccessException {
	    return (BlobFile) this.getSqlMapClientTemplate().queryForObject("getDocumentoAllegato", params);
	  }

}
