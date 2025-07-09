    /*
     * Created on 30/nov/09
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
    import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
    import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
    import it.eldasoft.utils.utility.UtilityNumeri;
    import org.springframework.transaction.TransactionStatus;

    import java.sql.SQLException;
    import java.util.List;
    import java.util.Vector;

    /**
     * Gestore non standard delle occorrenze dell'entita G1FVOE
     * Questa classe NON e' un gestore standard e prepara i dati di ciascuna
     * occorrenza presente nella scheda e demanda alla classe DefaultGestoreEntita
     * le operazioni di insert, update e delete
     *
     * @author Manuel.Bridda
     */
    public class GestoreG1FVOE extends AbstractGestoreEntita {

      @Override
      public String getEntita() {
        return "G1FVOE";
      }

      public GestoreG1FVOE() {
        super(false);
      }

      public GestoreG1FVOE(boolean isGestoreStandard) {
        super(isGestoreStandard);
      }

      @Override
      public void preDelete(TransactionStatus status, DataColumnContainer impl)
          throws GestoreException {

          // E' necessario cancellare tutte le ditte associate allo stesso dittao. Vanno anche ricalcolati i record attivi (isattivo=1)
          Long id = impl.getLong("G1FVOE.ID");
          String codgar = "";
          try {
              List<Vector> lista = this.sqlManager.getListVector("select isattivo,codimp,codgar,id from g1fvoe where dittao in (select dittao from g1fvoe where id=?)",new Object[]{id});
              if(lista!=null && !lista.isEmpty()) {
                  for(Vector v: lista) {
                      if("1".equals(SqlManager.getValueFromVectorParam(v,0).stringValue())) {//se record attivo, cerco se c'è un record non attivo (isattivo=2) da rendere attivo
                          String codimp = SqlManager.getValueFromVectorParam(v, 1).stringValue();
                          codgar = SqlManager.getValueFromVectorParam(v, 2).stringValue();
                          Long idtmp = SqlManager.getValueFromVectorParam(v, 3).longValue();
                          Long idNuovoRecordAttivo = (Long) sqlManager.getObject("select min(id) from g1fvoe where codimp=? and codgar=? and id <> ?", new Object[]{codimp, codgar, idtmp});
                          if (idNuovoRecordAttivo != null)
                              sqlManager.update("update g1fvoe set isattivo=1 where id=?", new Object[]{idNuovoRecordAttivo});
                      }
                  }
                  this.sqlManager.update("delete from g1fvoe where dittao in (select dittao from g1fvoe where id=?) and codgar=?  ",new Object[]{id,codgar});
                  this.sqlManager.update("delete from g1fvoedoc g1 where codimp not in (select codimp f from g1fvoe f where f.codgar=g1.codgar) and codgar=?",new Object[]{codgar});
              }
          } catch (SQLException e) {
              throw new GestoreException("Errore durante l'eliminazione delle ditte collegate", null,e);
          }
      }

      @Override
      public void postDelete(DataColumnContainer impl) throws GestoreException {
      }

      @Override
      public void preInsert(TransactionStatus status, DataColumnContainer impl)
          throws GestoreException {
      }

      @Override
      public void postInsert(DataColumnContainer impl) throws GestoreException {
      }

      @Override
      public void preUpdate(TransactionStatus status, DataColumnContainer impl)
          throws GestoreException {

          int numeroDitte = 0;
          String numDitte = this.getRequest().getParameter("numeroDitte");
          if(numDitte != null && !numDitte.isEmpty())
              numeroDitte = UtilityNumeri.convertiIntero(numDitte);

          for (int i = 1; i <= numeroDitte; i++) {
              DataColumnContainer dataColumnContainerDiRiga = new DataColumnContainer(
                      impl.getColumnsBySuffix("_" + i, false));

              boolean isG1FVOEModificata = dataColumnContainerDiRiga.isModifiedTable("G1FVOE");

              if(isG1FVOEModificata) {
                  String codgar = dataColumnContainerDiRiga.getColumn("G1FVOE.CODGAR").getOriginalValue().getStringValue();
                  String codimp = dataColumnContainerDiRiga.getColumn("G1FVOE.CODIMP").getOriginalValue().getStringValue();
                  Long stato = dataColumnContainerDiRiga.getColumn("G1FVOE.STATOVERIFICHE").getValue().longValue();
                  try {//aggiorno tutti i record dell'impresa su cui apporto la modifica!
                      sqlManager.update("update G1FVOE set statoverifiche=? where codgar=? and codimp=?",
                               new Object[] {stato, codgar, codimp});
                  } catch (SQLException e) {
                      throw new GestoreException("Errore nel salvataggio dei dati in G1FVOE",
                              null, e);
                  }
              }
              }

      }

      @Override
      public void postUpdate(DataColumnContainer impl) throws GestoreException {
      }

    }