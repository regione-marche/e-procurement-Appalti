/*
 * Created on 21/11/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.tags.gestori.submit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
/**
 * Gestore non standard che si occupa di gestire la funzionalità
 * di copia di una comunicazione
 *
 * @author Marcello Caminiti
 */
public class GestoreCopiaComunicazione extends AbstractGestoreEntita {


  @Override
  public String getEntita() {
    return "W_INVCOM";
  }

  public GestoreCopiaComunicazione() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */
  public GestoreCopiaComunicazione(boolean isGestoreStandard) {
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

    String  idprg = UtilityStruts.getParametroString(this.getRequest(),"idprg");
    String  idcomString = UtilityStruts.getParametroString(this.getRequest(),"idcom");
    Long idcom = new Long(idcomString);
    String committ = UtilityStruts.getParametroString(this.getRequest(),"committ");
    String allegati = dataColumnContainer.getString("ALLEGATI");
    String destinatari = dataColumnContainer.getString("DESTINATARI");
    String compub = UtilityStruts.getParametroString(this.getRequest(),"compub");
    String copiaDestinatariErrore = dataColumnContainer.getString("DESTINATARI_ERRORE");
    String documenti = dataColumnContainer.getString("DOCUMENTI");

    String select="";
    String update="";
    //Caricamento dati W_INVCOM
    select="select coment,comkey1,comkey2,comkey3,comkey4,comkey5,comintest,commsgogg,commsgtes,commodello,compub,comtipo," +
    		"comdatastato,comdatapub,comtipma,commsgtip,COMDATSCA,COMORASCA  from w_invcom where idprg=? and idcom=?";

    try {
      Vector datiW_INVCOM = sqlManager.getVector(select, new Object[]{idprg,idcom});
      if(datiW_INVCOM!=null){
        update = "insert into w_invcom(IDPRG,IDCOM,COMENT,COMKEY1,COMKEY2,COMKEY3,COMKEY4,COMKEY5,COMCODOPE,COMDATINS,COMMITT," +
        		"COMSTATO,COMINTEST,COMMSGOGG,COMMSGTES,COMMODELLO,COMPUB,COMTIPO,COMDATASTATO,COMDATAPUB,COMTIPMA,COMMSGTIP,IDCFG," +
                "COMDATSCA,COMORASCA ) " + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Long newIdcom = (Long)sqlManager.getObject("select max(idcom) from w_invcom where idprg=?", new Object[]{idprg});
        if(newIdcom==null)
          newIdcom = new Long(0);
        newIdcom = new Long(newIdcom.longValue() + 1);

        // Operatore (utente di USRSYS che ha avuto accesso all'applicativo)
        ProfiloUtente profilo = (ProfiloUtente) this.getRequest().getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);

        Object[] parametri = new Object[25];
        parametri[0] = idprg;
        parametri[1] = newIdcom;
        parametri[2] = SqlManager.getValueFromVectorParam(datiW_INVCOM, 0).stringValue();
        parametri[3] = SqlManager.getValueFromVectorParam(datiW_INVCOM, 1).stringValue();
        parametri[4] = SqlManager.getValueFromVectorParam(datiW_INVCOM, 2).stringValue();
        parametri[5] = SqlManager.getValueFromVectorParam(datiW_INVCOM, 3).stringValue();
        parametri[6] = SqlManager.getValueFromVectorParam(datiW_INVCOM, 4).stringValue();
        parametri[7] = SqlManager.getValueFromVectorParam(datiW_INVCOM, 5).stringValue();
        parametri[8] = new Long(profilo.getId());
        parametri[9] =  new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
        if(!"1".equals(compub))
          parametri[10] = committ;
        else
          parametri[10] = null;
        parametri[11] = "1";
        parametri[12] = SqlManager.getValueFromVectorParam(datiW_INVCOM, 6).stringValue();
        parametri[13] = SqlManager.getValueFromVectorParam(datiW_INVCOM, 7).stringValue();
        parametri[14] = SqlManager.getValueFromVectorParam(datiW_INVCOM, 8).stringValue();
        parametri[15] = SqlManager.getValueFromVectorParam(datiW_INVCOM, 9).longValue();
        parametri[16] = SqlManager.getValueFromVectorParam(datiW_INVCOM, 10).longValue();
        parametri[17] = SqlManager.getValueFromVectorParam(datiW_INVCOM, 11).stringValue();
        parametri[18] = SqlManager.getValueFromVectorParam(datiW_INVCOM, 12).dataValue();
        //////////////////////////////////
        //Data pubblicazione inizializzata a null (COMDATAPUB.W_INVCOM =   null)
        parametri[19] = null;
        //////////////////////////////////
        parametri[20] = SqlManager.getValueFromVectorParam(datiW_INVCOM, 14).longValue();
        parametri[21] = SqlManager.getValueFromVectorParam(datiW_INVCOM, 15).stringValue();
        //Codice stazione appaltante della gara
        String chiave = SqlManager.getValueFromVectorParam(datiW_INVCOM, 1).stringValue();
        String cenint = (String)this.sqlManager.getObject("select cenint from torn,gare where codgar1=codgar and ngara=?", new Object[]{chiave});
        parametri[22] = cenint;
        parametri[23] = SqlManager.getValueFromVectorParam(datiW_INVCOM, 16).dataValue();
        parametri[24] = SqlManager.getValueFromVectorParam(datiW_INVCOM, 17).stringValue();
        sqlManager.update(update, parametri);

        //Copia dei destinatari
        if("1".equals(destinatari)){
          select="select IDCOMDES,DESCODENT,DESCODSOG,DESMAIL,DESTESTO,DESIDDOCDIG,DESINTEST,COMTIPMA from " +
          		"w_invcomdes where idprg = ?  and IDCOM = ?";
          if("1".equals(copiaDestinatariErrore))
            select += " and (desstato=3 or desstato =5)";
          List lsitaW_INVCOMDES = sqlManager.getListVector(select, new Object[]{idprg,idcom});
          if(lsitaW_INVCOMDES!=null && lsitaW_INVCOMDES.size()>0){
            for(int i=0;i<lsitaW_INVCOMDES.size();i++){
              Vector comunicazione = (Vector) lsitaW_INVCOMDES.get(i);
              Long invcomdes = ((JdbcParametro) comunicazione.get(0)).longValue();
              String descodent = ((JdbcParametro) comunicazione.get(1)).stringValue();
              String descodsog = ((JdbcParametro) comunicazione.get(2)).stringValue();
              String desmail = ((JdbcParametro) comunicazione.get(3)).stringValue();
              String desttesto = ((JdbcParametro) comunicazione.get(4)).stringValue();
              Long desiddocdig = ((JdbcParametro) comunicazione.get(5)).longValue();
              String desintest = ((JdbcParametro) comunicazione.get(6)).stringValue();
              Long comtipma = ((JdbcParametro) comunicazione.get(7)).longValue();

              update = "insert into w_invcomdes(IDPRG ,IDCOM,IDCOMDES,DESCODENT,DESCODSOG,DESMAIL," +
              		"DESTESTO,DESIDDOCDIG,DESDATINV,DESDATINV_S,DESSTATO,DESERRORE,DESINTEST,COMTIPMA) values(" +
              		"?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

              parametri = new Object[14];
              parametri[0] = idprg;
              parametri[1] = newIdcom;
              parametri[2] = invcomdes;
              parametri[3] = descodent;
              parametri[4] = descodsog;
              parametri[5] = desmail;
              parametri[6] = desttesto;
              parametri[7] = desiddocdig;
              parametri[8] = null;
              parametri[9] = null;
              parametri[10] = null;
              parametri[11] = null;
              parametri[12] = desintest;
              parametri[13] = comtipma;
              sqlManager.update(update, parametri);
            }

          }
        }

        //copia allegati
        if("1".equals(allegati)){
          select="select IDPRG,IDDOCDIG,DIGENT,DIGKEY1,DIGKEY2,DIGKEY3,DIGKEY4,DIGKEY5,DIGTIPDOC,DIGNOMDOC,DIGDESDOC, DIGFIRMA from " +
          "w_docdig where idprg = ?  and DIGENT = ? and DIGKEY2 = ?";

          List lsitaW_DOCDIG = sqlManager.getListHashMap(select, new Object[]{idprg,"W_INVCOM",idcomString});
          if(lsitaW_DOCDIG!=null && lsitaW_DOCDIG.size()>0){

            FileAllegatoManager fileAllegatoManager= (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager",
                this.getServletContext(), FileAllegatoManager.class);

            for(int i=0;i<lsitaW_DOCDIG.size();i++){

              Long newIddocdig = (Long)sqlManager.getObject("select max(iddocdig) from w_docdig where idprg=?", new Object[]{idprg});
              if(newIddocdig==null)
                newIddocdig = new Long(0);
              newIddocdig = new Long(newIddocdig.longValue() + 1);

              //Vector allegato = (Vector) lsitaW_DOCDIG.get(i);

              Long iddocdgOld = ((JdbcParametro)((HashMap) lsitaW_DOCDIG.get(i)).get("IDDOCDIG")).longValue();

              DataColumnContainer campiDaCopiareW_DOCDIG = new DataColumnContainer(this.geneManager.getSql(), "W_DOCDIG", select,new Object[] { idprg,"W_INVCOM",idcomString});
              campiDaCopiareW_DOCDIG.setValoriFromMap((HashMap)lsitaW_DOCDIG.get(i), true);
              campiDaCopiareW_DOCDIG.getColumn("IDPRG" ).setChiave(true);

              campiDaCopiareW_DOCDIG.getColumn("IDDOCDIG" ).setChiave(true);
              campiDaCopiareW_DOCDIG.setValue("IDDOCDIG",newIddocdig);
              campiDaCopiareW_DOCDIG.setValue("DIGKEY1",idprg);
              campiDaCopiareW_DOCDIG.setValue("DIGKEY2",newIdcom.toString());

              BlobFile fileAllegato = null;
              fileAllegato = fileAllegatoManager.getFileAllegato(idprg,iddocdgOld);
              ByteArrayOutputStream baos = null;
              if(fileAllegato!=null && fileAllegato.getStream()!=null){
                baos = new ByteArrayOutputStream();
                baos.write(fileAllegato.getStream());
              }
              campiDaCopiareW_DOCDIG.addColumn("W_DOCDIG.DIGOGG", JdbcParametro.TIPO_BINARIO,baos);

              // Inserimento del nuovo record su w_docdig
              campiDaCopiareW_DOCDIG.insert("W_DOCDIG", this.geneManager.getSql());
            }
          }
        }

        //Copia documenti richiesti
        if("1".equals(documenti)){
          select="select NUMORD,DESCRIZIONE, OBBLIGATORIO, FORMATO from G1DOCSOC where idprg = ?  and idcom = ?";
          List<?> listaDocumenti = this.sqlManager.getListVector(select, new Object[]{idprg,idcom});
          if(listaDocumenti!=null && listaDocumenti.size()>0){
            GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
                this.getServletContext(), GenChiaviManager.class);
            int nextId = 0;
            String sqlInsert= "insert into G1DOCSOC(ID,IDPRG,IDCOM,NUMORD,DESCRIZIONE, OBBLIGATORIO, FORMATO) values(?,?,?,?,?,?,?)";
            Long numord= null;
            String descrizione = null;
            String obbligatorio = null;
            Long formato= null;
            for(int i=0;i<listaDocumenti.size();i++){
              nextId = genChiaviManager.getNextId("G1DOCSOC");
              numord = SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 0).longValue();
              descrizione = SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 1).getStringValue();
              obbligatorio = SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 2).getStringValue();
              formato = SqlManager.getValueFromVectorParam(listaDocumenti.get(i), 3).longValue();
              this.sqlManager.update(sqlInsert, new Object[]{new Long(nextId),idprg, newIdcom,numord,descrizione,obbligatorio,formato});
            }
          }

        }
      }
    } catch (SQLException e) {
      this.getRequest().setAttribute("copiaEseguita", "2");
      throw new GestoreException("Errore nella copia della comunicazione:" + idcomString, null, e);
    } catch (IOException e) {
      throw new GestoreException("Errore nella copia della comunicazione:" + idcomString, null, e);
    }


    //Se tutto è andato bene setto nel request il parametro copiaEseguita = 1
    this.getRequest().setAttribute("copiaEseguita", "1");
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

}