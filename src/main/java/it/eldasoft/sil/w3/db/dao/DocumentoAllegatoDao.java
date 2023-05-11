package it.eldasoft.sil.w3.db.dao;

import it.eldasoft.gene.db.domain.BlobFile;

import java.util.HashMap;

import org.springframework.dao.DataAccessException;


public interface DocumentoAllegatoDao {
  
  BlobFile getDocumentoAllegato(HashMap params) throws DataAccessException;
  
}
