/*
 * Created on 10/04/20
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.spring.UtilitySpring;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;


public class GestoreInsertDocumentiGruppo15 extends
    AbstractGestoreEntita {

  public GestoreInsertDocumentiGruppo15() {
    super(false);
  }

  /** Manager per l'esecuzione di query */
  private SqlManager sqlManager = null;

  /** Manager per la gestione dei file allegati */
  private FileAllegatoManager fileAllegatoManager;


  @Override
  public void setRequest(HttpServletRequest request) {
    super.setRequest(request);
    // Estraggo il manager per eseguire query
    sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);

    fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager",
        this.getServletContext(), FileAllegatoManager.class);

  }

  @Override
  public String getEntita() {
    return "TORN";
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    // lettura dei parametri di input
    String codgar = datiForm.getString("CODGAR");
    String ngara = datiForm.getString("ngara");
    Long tipologia = datiForm.getLong("TIPOLOGIA");

    List<?> listaOccorenzeDaCopiare = null;
    DataColumnContainer campiDaCopiare = null;

    int livello = 1;
    String errMsgEvento = null;

    String sql = "select * from DOCUMGARA where CODGAR = ? and GRUPPO = ? and (isarchi <>'1' or isarchi is null) order by numord";
    try {
      listaOccorenzeDaCopiare = this.sqlManager.getListHashMap(sql, new Object[] { codgar, new Long(15) });
      if (listaOccorenzeDaCopiare != null
          && listaOccorenzeDaCopiare.size() > 0) {
        for (int row = 0; row < listaOccorenzeDaCopiare.size(); row++) {
            campiDaCopiare = new DataColumnContainer(
                this.sqlManager, "DOCUMGARA", sql,
                new Object[] { codgar, new Long(15) });

          campiDaCopiare.setValoriFromMap(
              (HashMap<?,?>) listaOccorenzeDaCopiare.get(row), true);

          String campiDaNonCopiare[] = new String[]{ "DOCUMGARA.DATARILASCIO", "DOCUMGARA.DATAPROV", "DOCUMGARA.NUMPROV","DOCUMGARA.STATODOC"};
          campiDaCopiare.removeColumns(campiDaNonCopiare);

          campiDaCopiare.getColumn("CODGAR").setChiave(true);
          campiDaCopiare.getColumn("NORDDOCG").setChiave(true);

          long newNorddocg = 1;
          Long maxNorddocg = (Long) this.sqlManager.getObject(
              "select max(NORDDOCG) from DOCUMGARA where CODGAR= ?",
              new Object[] { codgar });

          if (maxNorddocg != null && maxNorddocg.longValue() > 0)
            newNorddocg = maxNorddocg.longValue() + 1;

          campiDaCopiare.setValue("NORDDOCG", new Long(newNorddocg));
          campiDaCopiare.setValue("GRUPPO", new Long(4));
          campiDaCopiare.setValue("TIPOLOGIA", tipologia);

          campiDaCopiare.setValue("IDPRG", "PG");
          campiDaCopiare.setValue("IDDOCDG", null);
          campiDaCopiare.setValue("NUMORD", new Long(newNorddocg));

          // Inserimento del nuovo record
          campiDaCopiare.insert("DOCUMGARA", this.sqlManager);

          // Copia delle occorrenze di W_DOCDIG figlie di DOCUMGARA
          HashMap<?,?> hm = (HashMap<?,?>) listaOccorenzeDaCopiare.get(row);
          String idprg = hm.get("IDPRG").toString();
          Long iddocdg = ((JdbcParametro) hm.get("IDDOCDG")).longValue();

          long numOccorrenzeW_DOCDIG = this.geneManager.countOccorrenze(
              "W_DOCDIG", "IDPRG = ? and IDDOCDIG = ?", new Object[] { idprg,
                  iddocdg });
          if (numOccorrenzeW_DOCDIG > 0) {
            // Il campo W_DOCDIG.DIGOGG è di tipo BLOB e va trattato
            // separatamente
            String select = "select IDPRG,IDDOCDIG,DIGENT,DIGKEY1,DIGKEY2,DIGTIPDOC,DIGNOMDOC,DIGDESDOC,DIGFIRMA from W_DOCDIG where IDPRG = ? and IDDOCDIG = ?";
            List<?> occorrenzeW_DOCDIGDaCopiare = this.sqlManager.getListHashMap(
                select, new Object[] { idprg, iddocdg });
            if (occorrenzeW_DOCDIGDaCopiare != null
                && occorrenzeW_DOCDIGDaCopiare.size() > 0) {
              for (int i = 0; i < occorrenzeW_DOCDIGDaCopiare.size(); i++) {
                DataColumnContainer campiDaCopiareW_DOCDIG = new DataColumnContainer(
                    this.sqlManager, "W_DOCDIG", select,
                    new Object[] { idprg, iddocdg });
                campiDaCopiareW_DOCDIG.setValoriFromMap(
                    (HashMap<?,?>) occorrenzeW_DOCDIGDaCopiare.get(i), true);
                campiDaCopiareW_DOCDIG.getColumn("IDPRG").setChiave(true);

                campiDaCopiareW_DOCDIG.getColumn("IDDOCDIG").setChiave(true);

                // Si deve calcolare il valore di IDDOCDIG
                Long maxIDDOCDIG = (Long) this.sqlManager.getObject(
                    "select max(IDDOCDIG) from W_DOCDIG where IDPRG= ?",
                    new Object[] { idprg });

                long newIDDOCDIG = 1;
                if (maxIDDOCDIG != null && maxIDDOCDIG.longValue() > 0)
                  newIDDOCDIG = maxIDDOCDIG.longValue() + 1;

                campiDaCopiareW_DOCDIG.setValue("IDDOCDIG", new Long(
                    newIDDOCDIG));
                campiDaCopiareW_DOCDIG.setValue("DIGKEY2", new Long(
                    newNorddocg));
                BlobFile fileAllegato = null;
                fileAllegato = fileAllegatoManager.getFileAllegato(idprg,
                    iddocdg);
                ByteArrayOutputStream baos = null;
                if (fileAllegato != null && fileAllegato.getStream() != null) {
                  baos = new ByteArrayOutputStream();
                  baos.write(fileAllegato.getStream());
                }
                campiDaCopiareW_DOCDIG.addColumn("W_DOCDIG.DIGOGG",
                    JdbcParametro.TIPO_BINARIO, baos);

                // Inserimento del nuovo record su w_docdig
                campiDaCopiareW_DOCDIG.insert("W_DOCDIG",
                    this.sqlManager);

                // Aggiornamento dei campi IDPRG e IDDOCDG di documgara
                this.sqlManager.update(
                    "update DOCUMGARA set IDPRG=?,IDDOCDG = ? where CODGAR=? and NORDDOCG=?",
                    new Object[] { idprg, new Long(newIDDOCDIG),
                        codgar, new Long(newNorddocg) });
              }
            }
          }

        }
      }

      //Ricalcolo NUMORD.DOCUMGARA
      PgManagerEst1 pgManagerEst1 = (PgManagerEst1) UtilitySpring.getBean("pgManagerEst1",
      this.getServletContext(), PgManagerEst1.class);
      pgManagerEst1.ricalcNumordDocGara(codgar, new Long(4));

      // setta l'operazione a completata, in modo da scatenare il reload della
      // pagina principale
      this.getRequest().setAttribute("documentiInseriti", "1");

    } catch (Exception e) {
      livello = 3;
      errMsgEvento= e.getMessage();
      throw new GestoreException("Errore nella copia dei documenti delibera a contrarre in documenti esito per la gara " + codgar, "copiaDocGruppo15InGruppo4");
    }finally{
      String oggetto=codgar;
      if(oggetto.startsWith("$"))
        oggetto = ngara;
      LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
      logEvento.setCodApplicazione("PG");
      logEvento.setLivEvento(livello);
      logEvento.setOggEvento(oggetto);
      logEvento.setCodEvento("GA_COPIA_DELIBERA_DOC");
      logEvento.setDescr("Copia documenti delibera a contrarre in documenti esito");
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);
    }
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

}
