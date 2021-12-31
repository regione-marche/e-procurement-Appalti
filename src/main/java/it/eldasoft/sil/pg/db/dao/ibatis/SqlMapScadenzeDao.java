/*
 * Created on 07/nov/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.db.dao.ibatis;

import java.util.HashMap;

import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.sil.pg.db.dao.ScadenzeDao;

/**
 * Classe di appoggio per l'esecuzione tramite estensione del framework Spring
 * di interazioni con la tabella ACCOUNT tramite iBatis.
 * 
 * @author Stefano.Sabbadin
 */
public class SqlMapScadenzeDao extends SqlMapClientDaoSupportBase implements
    ScadenzeDao {

  public Integer getGiorniScadenza(Integer tipoCalcolo, Integer tipoAppalto,
      Double importoGara, Integer tipoProcedura, String proceduraUrgente,
      String termineRidotto, String bandoWeb, String docWeb,
      Integer oggettoContratto) {
    HashMap hash = new HashMap();
    hash.put("tipoCalcolo", tipoCalcolo);
    hash.put("tipoAppalto", tipoAppalto);
    hash.put("importoGara", importoGara);
    hash.put("tipoProcedura", tipoProcedura);
    hash.put("proceduraUrgente", proceduraUrgente);
    hash.put("termineRidotto", termineRidotto);
    hash.put("bandoWeb", bandoWeb);
    hash.put("docWeb", docWeb);
    hash.put("oggettoContratto", oggettoContratto);
    hash.put("strNonDefinita", "0");
    hash.put("numNonDefinito", new Integer(0));
    
    return (Integer)this.getSqlMapClientTemplate().queryForObject("getGiorniScadenza", hash);
  }

}
