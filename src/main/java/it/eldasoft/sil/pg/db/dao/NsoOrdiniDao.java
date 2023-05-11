package it.eldasoft.sil.pg.db.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import it.eldasoft.sil.pg.db.domain.nso.Beneficiario;
import it.eldasoft.sil.pg.db.domain.nso.Fornitore;
import it.eldasoft.sil.pg.db.domain.nso.LineaOrdine;
import it.eldasoft.sil.pg.db.domain.nso.Ordinante;
import it.eldasoft.sil.pg.db.domain.nso.Ordine;
import it.eldasoft.sil.pg.db.domain.nso.PuntoConsegna;

public interface NsoOrdiniDao {
  public Ordine getOrderById(Long id) throws DataAccessException;
  public PuntoConsegna getDeliveryPointByNsoOrdiniId(Long id) throws DataAccessException;
  public List<LineaOrdine> getOrderLinesByNsoOrdiniId(Long id) throws DataAccessException;
  public Ordinante getBuyerCustomerPartyByNsoOrdiniIdAndTypeOne(Long id) throws DataAccessException;
  public List<Ordinante> getBuyerCustomerPartiesByNsoOrdiniId(Long id) throws DataAccessException;
  public Fornitore getSellerSupplierPartyByNsoOrdiniId(Long id) throws DataAccessException;
  public Beneficiario getDeliveryPartyByNsoOrdiniId(Long id) throws DataAccessException;
  public byte[] getNsoWsOrdineFileXmlFromFileName(String fileName) throws DataAccessException;
}
