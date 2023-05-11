/*
 * Created on 20/05/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import java.sql.SQLException;
import java.util.List;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard che effettua la delega lavoro al RUP
 *
 * @author Marcello Caminiti
 */
public class GestorePopupDelegaLavoroRup extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "TORN";
  }

  public GestorePopupDelegaLavoroRup() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestorePopupDelegaLavoroRup(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer
      dataColumnContainer) throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status,
      DataColumnContainer dataColumnContainer) throws GestoreException {
    String  nmaximo = UtilityStruts.getParametroString(this.getRequest(),"nmaximo");
    String  isLavoroDelegato = UtilityStruts.getParametroString(this.getRequest(),"isLavoroDelegato");
    String clavor = UtilityStruts.getParametroString(this.getRequest(),"clavor");
    String codrup = UtilityStruts.getParametroString(this.getRequest(),"codrup");
    String tecniciTuttiDelegati = UtilityStruts.getParametroString(this.getRequest(),"tecniciTuttiDelegati");

    String sql=null;
    String insert="insert into g_permessi(numper,syscon,codlav,autori,propri) "+
      "values(?,?,?,?,?)";

    try {
      sql="update peri set nmaximo = ? where codlav = ?";
      sqlManager.update(sql,new Object[] {nmaximo, clavor});

      //Se il lavoro non è già delegato si effettua la delega
      if("NO".equals(isLavoroDelegato)){
        sql="select syscon from tecni where codtec = ?";
        Long syscon = (Long)sqlManager.getObject(sql, new Object[]{codrup});

        Long numper = this.getMaxNumper();
        sqlManager.update(insert,new Object[] {numper,syscon, clavor,new Long(1),"1"});

      }
      //Delega per i tecnici interni del lavoro
      if("NO".equals(tecniciTuttiDelegati)){
        List listaTecniciInterni = sqlManager.getListVector("select distinct(codtec) from g2tecn where codlav=? and inttec=?",
            new Object[]{clavor, "1"});
        if(listaTecniciInterni!=null && listaTecniciInterni.size()>0){
          String select="select syscon from tecni where codtec = ?";
          String selectConteggio="select count(numper) from g_permessi where syscon = ? and codlav= ?";
          for(int i=0;i<listaTecniciInterni.size();i++){
            String codtec = SqlManager.getValueFromVectorParam(listaTecniciInterni.get(i), 0).getStringValue();
            Long syscon = (Long)sqlManager.getObject(select, new Object[]{codtec});
            if (syscon != null){
              Long count = (Long)sqlManager.getObject(selectConteggio, new Object[]{syscon,clavor});
              if(count== null || ( count!= null && count.longValue()==0)){
                Long numper = this.getMaxNumper();
                sqlManager.update(insert,new Object[] {numper,syscon, clavor,new Long(1),"2"});
              }

            }
          }
        }
      }

      this.getRequest().setAttribute("delegaEseguita", "1");
    }  catch (SQLException e) {
      this.getRequest().setAttribute("RISULTATO", "ERRORI");
      throw new GestoreException("Errore nella funzione di delega", null, e);
    }

  }


  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer dataColumnContainer)
      throws GestoreException {


  }

  @Override
  public void postUpdate(DataColumnContainer dataColumnContainer) throws GestoreException {
  }

   @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

   private Long getMaxNumper() throws SQLException{
     String sql = "select max(numper) from g_permessi";
     Long numper = (Long)sqlManager.getObject(sql, null);
     if( numper== null)
       numper = new Long(0);

     numper = new Long(numper.longValue() + 1);

     return numper;
   }

}