/*
 * Created on 03/08/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;
import java.util.Vector;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard delle occorrenze dell'entita ISCRIZCAT presenti piu' volte
 * nella pagina "Elenco categorie iscrizione" (iscrizcat-listaScheda.jsp)
 *
 * Questa classe NON e' un gestore standard e prepara i dati di ciascuna
 * occorrenza presente nella scheda e demanda alla classe DefaultGestoreEntita
 * le operazioni di insert, update e delete
 *
 * @author Marcello Caminiti
 */
public class GestoreISCRIZCAT extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "ISCRIZCAT";
  }

  public GestoreISCRIZCAT() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestoreISCRIZCAT(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

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


    String numeroGara=this.getRequest().getParameter("numeroGara");
    String codiceDitta=this.getRequest().getParameter("codiceDitta");
    String codiceGara=this.getRequest().getParameter("codiceGara");

    Long numordpl =null;
    try {
      numordpl = (Long)this.getSqlManager().getObject("select numordpl from ditg where codgar5=? and ngara5=? and dittao=?",
          new Object[]{codiceGara,numeroGara,codiceDitta});
    } catch (SQLException e) {
      throw new GestoreException("Errore nel caricamento del numero d'ordine",null, e);
    }

    String[] listaCategorieSelezionate = this.getRequest().getParameterValues("keys");
    int numeroCategorie = 0;
    String numCategorie = this.getRequest().getParameter("numeroCategorie");
    if(numCategorie != null && numCategorie.length() > 0)
      numeroCategorie =  UtilityNumeri.convertiIntero(numCategorie).intValue();

    PgManager pgManager = (PgManager) UtilitySpring.getBean("pgManager",
        this.getServletContext(), PgManager.class);

    for (int i = 1; i <= numeroCategorie; i++) {
      DataColumnContainer dataColumnContainerDiRiga = new DataColumnContainer(
          impl.getColumnsBySuffix("_" + i, false));
      String codcat = dataColumnContainerDiRiga.getString("V_ISCRIZCAT_TIT.CAISIM");
      String isfoglia = dataColumnContainerDiRiga.getString("V_ISCRIZCAT_TIT.ISFOGLIA");
      if (!"0".equals(codcat) && "1".equals(isfoglia)){
        boolean categoriaTrovata = false;
        boolean calcoloInviti = false;

        if(listaCategorieSelezionate!= null && listaCategorieSelezionate.length > 0) {
          for (int j = 0; j < listaCategorieSelezionate.length; j++) {

            String codiceCategoriaSelezionata = listaCategorieSelezionate[j];
            if(codiceCategoriaSelezionata.equals(codcat)){
              categoriaTrovata = true;
              break;
            }

          }
        }

        if(categoriaTrovata){
          //Inserimento o aggiornamento in ISCRIZCAT
          Long tiplavg = dataColumnContainerDiRiga.getLong("V_ISCRIZCAT_TIT.TIPLAVG");


          //Controllo se la categoria è già presente in iscrizcat
          long countOccorrenze = geneManager.countOccorrenze(
              "ISCRIZCAT", "codgar = ? and ngara = ? and codimp = ?  and codcat = ? and tipcat =? ", new Object[]{
                  "$" + numeroGara, numeroGara, codiceDitta, codcat, tiplavg});

          try {
            Long infnumclass = dataColumnContainerDiRiga.getLong("ISCRIZCAT.INFNUMCLASS");
            Long supnumclass = dataColumnContainerDiRiga.getLong("ISCRIZCAT.SUPNUMCLASS");
            if(countOccorrenze > 0){
              //Aggiornamento
              if(dataColumnContainerDiRiga.isModifiedColumn("ISCRIZCAT.INFNUMCLASS") || dataColumnContainerDiRiga.isModifiedColumn("ISCRIZCAT.SUPNUMCLASS")){
                //Long infnumclass = dataColumnContainerDiRiga.getLong("ISCRIZCAT.INFNUMCLASS");
                //Long supnumclass = dataColumnContainerDiRiga.getLong("ISCRIZCAT.SUPNUMCLASS");
                this.getSqlManager().update(
                    "update ISCRIZCAT set INFNUMCLASS = ?,SUPNUMCLASS = ?  where CODGAR = ? and NGARA = ? and CODIMP = ? and CODCAT = ?",
                    new Object[] { infnumclass,supnumclass,"$" + numeroGara, numeroGara,codiceDitta,codcat});

                //Cancello le occorrenze in ISCRIZCLASSI
                //pgManager.updateIscrizclassi("DEL","$" + numeroGara, numeroGara,codiceDitta,codcat,tiplavg,null,null,calcoloInviti);

                if(numordpl!=null)
                  calcoloInviti = true;
                //Aggiornamento ISCRIZCLASSI
                pgManager.updateIscrizclassi("UPD","$" + numeroGara, numeroGara,codiceDitta,codcat,tiplavg,infnumclass,supnumclass,calcoloInviti);



                //pgManager.updateIscrizclassi("INS","$" + numeroGara, numeroGara,codiceDitta,codcat,tiplavg,infnumclass,supnumclass,calcoloInviti);
              }
            }else{
              //Inserimento

              dataColumnContainerDiRiga.addColumn("ISCRIZCAT.CODGAR", JdbcParametro.TIPO_TESTO, "$" + numeroGara);
              dataColumnContainerDiRiga.getColumn("ISCRIZCAT.CODGAR").setChiave(true);
              dataColumnContainerDiRiga.addColumn("ISCRIZCAT.NGARA", JdbcParametro.TIPO_TESTO, numeroGara);
              dataColumnContainerDiRiga.getColumn("ISCRIZCAT.NGARA").setChiave(true);
              dataColumnContainerDiRiga.addColumn("ISCRIZCAT.CODIMP", JdbcParametro.TIPO_TESTO, codiceDitta);
              dataColumnContainerDiRiga.getColumn("ISCRIZCAT.CODIMP").setChiave(true);
              dataColumnContainerDiRiga.addColumn("ISCRIZCAT.CODCAT", JdbcParametro.TIPO_TESTO, codcat);
              dataColumnContainerDiRiga.getColumn("ISCRIZCAT.CODCAT").setChiave(true);
              dataColumnContainerDiRiga.addColumn("ISCRIZCAT.TIPCAT", JdbcParametro.TIPO_NUMERICO, tiplavg);
              dataColumnContainerDiRiga.getColumn("ISCRIZCAT.TIPCAT").setChiave(true);


              if(pgManager.controlliPreliminariCalcoloNumPenalita("$" + numeroGara ,numeroGara,status) && (numordpl!=null ) ){
                Long numPenalita=pgManager.getNumeroPenalita("$" + numeroGara, numeroGara, codiceDitta, codcat, tiplavg,null,"ISCRIZCAT",null,true);
                dataColumnContainerDiRiga.addColumn("ISCRIZCAT.INVPEN", JdbcParametro.TIPO_NUMERICO, numPenalita);
                //Aggiornamento ISCRIZUFF in base al calcolo di numero inviti virtuali
                pgManager.aggInvitiVirtualiIscrizuff("$" + numeroGara, numeroGara, codcat, codiceDitta, tiplavg);
              }

              dataColumnContainerDiRiga.insert("ISCRIZCAT", sqlManager);


              //Inserimento dei padri della categoria corrente

              Vector datiCategoriaFoglia = this.sqlManager.getVector("select codliv1,codliv2,codliv3,codliv4 from cais where caisim = ?", new Object[]{codcat});
              if(datiCategoriaFoglia!= null && datiCategoriaFoglia.size()>0){
                String codliv[] = new String[4];

                codliv[0] = (String) ((JdbcParametro) datiCategoriaFoglia.get(0)).getValue();
                codliv[1] = (String) ((JdbcParametro) datiCategoriaFoglia.get(1)).getValue();
                codliv[2] = (String) ((JdbcParametro) datiCategoriaFoglia.get(2)).getValue();
                codliv[3] = (String) ((JdbcParametro) datiCategoriaFoglia.get(3)).getValue();

                for(int j=0;j<4;j++){
                  if(codliv[j]== null || "".equals(codliv[j]))
                    break;
                  else{
                    Long numOccorrenze = (Long)this.getSqlManager().getObject("select count(codcat) from ISCRIZCAT where CODGAR = ?" +
                        " and NGARA = ? and CODIMP = ? and CODCAT = ?", new Object[] { "$" + numeroGara, numeroGara,codiceDitta,codliv[j]});
                    if(numOccorrenze.longValue()==0){
                      this.sqlManager.update("insert into ISCRIZCAT (CODGAR,NGARA,CODIMP,CODCAT,TIPCAT) values (?,?,?,?,?)",
                          new Object[]{"$" + numeroGara, numeroGara, codiceDitta,codliv[j],tiplavg});
                    }
                  }
                }
              }


              //INSERIMENTO ISCRIZCLASSI
              if(numordpl!=null)
                calcoloInviti = true;

              pgManager.updateIscrizclassi("INS","$" + numeroGara, numeroGara,codiceDitta,codcat,tiplavg,infnumclass,supnumclass,calcoloInviti);
            }



          }catch (SQLException e) {
            throw new GestoreException("Errore nell'aggiornamento dei dati in ISCRIZCAT",null, e);
          }


        }else{
          //Eliminazione dalla ISCRIZCAT
          try {
            Long numOccorrenze = (Long)this.getSqlManager().getObject("select count(codcat) from ISCRIZCAT where CODGAR = ?" +
            		" and NGARA = ? and CODIMP = ? and CODCAT = ?", new Object[] { "$" + numeroGara, numeroGara,codiceDitta,codcat});
            if(numOccorrenze!= null && numOccorrenze.longValue()>0){
              pgManager.cancellaCategoriaConGerarchia(numeroGara,codcat,codiceDitta,"ISCRIZCAT") ;
              //Eliminazione dalla ISCRIZCLASSI
              Long tiplavg = dataColumnContainerDiRiga.getLong("V_ISCRIZCAT_TIT.TIPLAVG");
              pgManager.updateIscrizclassi("DEL","$" + numeroGara, numeroGara,codiceDitta,codcat,tiplavg,null,null,calcoloInviti);
            }

          } catch (SQLException e) {
            throw new GestoreException(
                "Errore nella cancellazione delle occorrenze di ISCRIZCAT con chiave "
                + "CODGAR = $" + numeroGara + ", NGARA = " + numeroGara
                + ", CODIMP = " + codiceDitta + ", CODCAT = " + codcat, null, e);
          }

        }
      }
    }

  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

}