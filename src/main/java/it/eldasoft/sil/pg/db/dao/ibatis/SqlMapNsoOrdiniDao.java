/*
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.db.dao.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.sil.pg.db.dao.NsoOrdiniDao;
import it.eldasoft.sil.pg.db.domain.nso.Beneficiario;
import it.eldasoft.sil.pg.db.domain.nso.Fornitore;
import it.eldasoft.sil.pg.db.domain.nso.LineaOrdine;
import it.eldasoft.sil.pg.db.domain.nso.NsoWsOrdine;
import it.eldasoft.sil.pg.db.domain.nso.Ordinante;
import it.eldasoft.sil.pg.db.domain.nso.Ordine;
import it.eldasoft.sil.pg.db.domain.nso.PuntoConsegna;

/**
 * Classe di appoggio per l'esecuzione tramite estensione del framework Spring
 * di interazioni con la tabella ACCOUNT tramite iBatis.
 * 
 * @author Stefano.Sabbadin
 */
public class SqlMapNsoOrdiniDao extends SqlMapClientDaoSupportBase implements NsoOrdiniDao {

  private static final Logger logger = Logger.getLogger(SqlMapNsoOrdiniDao.class);
  
  @Override
  public Ordine getOrderById(Long id) throws DataAccessException {
    logger.debug("Getting Object by id:" +id);
    Ordine ord = (Ordine) this.getSqlMapClientTemplate().queryForObject("getOrderById",id);
    logger.info("Ordine ord: "+ ord);
    return ord;
  }

  @Override
  public PuntoConsegna getDeliveryPointByNsoOrdiniId(Long id) throws DataAccessException {
    logger.debug("Getting PuntoConsegna by nso_ordini_id:" +id);
    PuntoConsegna pc = (PuntoConsegna) this.getSqlMapClientTemplate().queryForObject("getDeliveryPointByNsoOrdiniId",id);
    logger.info("PuntoConsegna ord: "+ pc);
    return pc;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<LineaOrdine> getOrderLinesByNsoOrdiniId(Long id) throws DataAccessException {
    logger.info("Getting List of LineaOrdine by nso_ordini_id:" +id);
    List<LineaOrdine> list = this.getSqlMapClientTemplate().queryForList("getOrderLinesByNsoOrdiniId", id);
    if(list==null) {
      list = new ArrayList<LineaOrdine>(0);
    }
    logger.debug("Found total lines of LineaOrdine: "+ list.size());
    return list;
  }

  @Override
  public Ordinante getBuyerCustomerPartyByNsoOrdiniIdAndTypeOne(Long id) throws DataAccessException {
    logger.debug("Getting Ordinante by nso_ordini_id:" +id);
    HashMap<String,Object> hash = new HashMap<String,Object>();
    hash.put("id", id);
    hash.put("tipo", 1l);
    Ordinante ord = (Ordinante) this.getSqlMapClientTemplate().queryForObject("getBuyerCustomerPartyByNsoOrdiniIdAndType",hash);
    logger.info("Ordinante ord: "+ ord);
    return ord;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public List<Ordinante> getBuyerCustomerPartiesByNsoOrdiniId(Long id) throws DataAccessException {
    logger.info("Getting List of Ordinante by nso_ordini_id:" +id);
    List<Ordinante> list = this.getSqlMapClientTemplate().queryForList("getBuyerCustomerPartiesByNsoOrdiniId",id);
    if(list==null) {
      list = new ArrayList<Ordinante>(0);
    }
    logger.debug("Found total lines of Ordinante: "+ list.size());
    return list;
  }

  @Override
  public Fornitore getSellerSupplierPartyByNsoOrdiniId(Long id) throws DataAccessException {
    logger.debug("Getting Fornitore by nso_ordini_id:" +id);
    Fornitore forn = (Fornitore) this.getSqlMapClientTemplate().queryForObject("getSellerSupplierPartyByNsoOrdiniId",id);
    logger.info("Fornitore ord: "+ forn);
    return forn;
  }
  
  @Override
  public Beneficiario getDeliveryPartyByNsoOrdiniId(Long id) throws DataAccessException {
    logger.debug("Getting Beneficiario by nso_ordini_id:" +id);
    Beneficiario ben = (Beneficiario) this.getSqlMapClientTemplate().queryForObject("getDeliveryPartyByNsoOrdiniId",id);
    logger.info("Beneficiario ord: "+ ben);
    return ben;
  }

  @Override
  public byte[] getNsoWsOrdineFileXmlFromFileName(String fileName) throws DataAccessException {
    return ((NsoWsOrdine)this.getSqlMapClientTemplate().queryForObject("getNsoWsOrdineFileXmlFromFileName", fileName)).getFileXml();
  }
 
}
