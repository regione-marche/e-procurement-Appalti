/*
 * Created on 15/11/16
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.bl.tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;

public class CreazioneArchivioDocumentiGaraManager {

  static Logger            logger                 = Logger.getLogger(CreazioneArchivioDocumentiGaraManager.class);

  private SqlManager       sqlManager;

  private FileAllegatoManager fileAllegatoManager;

  private GenChiaviManager genChiaviManager;

  /**
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
  *
  * @param fileAllegatoManager
  */
 public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
   this.fileAllegatoManager = fileAllegatoManager;
 }

 /**
	 * @param genChiaviManager the genChiaviManager to set
	 */
	public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
		this.genChiaviManager = genChiaviManager;
	}

  /**
   *
   * vengono creati gli archivi di tutti i documenti delle gare richieste dagli utenti
   *
   */
  public void creazioneArchivioDocumentiGara()  {

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato
    if (WebUtilities.isAppNotReady()) return;

    logger.debug("creazioneArchivioDocumentiGara: inizio metodo");

    String pathArchivioDocumenti = ConfigManager.getValore("it.eldasoft.sil.pg.pathArchivioDocumentiGara");
    String documentiAssociatiDB = ConfigManager.getValore("it.eldasoft.documentiAssociatiDB");
    String pathDocumentiAssociati = ConfigManager.getValore("it.eldasoft.documentiAssociati");
    int numErrori = 0;

    if(pathArchivioDocumenti != null && !"".equals(pathArchivioDocumenti)){
      try {

        String getJobsSql="select id_archiviazione, syscon, codgara,entita from gardoc_jobs where da_processare = '1' and tipo_archiviazione = 1 order by id_archiviazione";
        List<?> jobs = this.sqlManager.getListVector(getJobsSql, null);

        if(jobs!=null && jobs.size()>0){

          ZipOutputStream zipOut = null;
          PrintWriter fileIndice = null;
          String queryDocGara = "select NGARA, GRUPPO, DESCRIZIONE, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, isarchi as ARCHIVIATO, " + this.sqlManager.getDBFunction("datetimetostring",  new String[] {"DATARILASCIO"}) + " " +
           		"from documgara d join w_docdig w on d.iddocdg=w.iddocdig and d.idprg=w.idprg and digent='DOCUMGARA' " +
          		"where d.codgar = ? and d.idprg is not null and d.iddocdg is not null order by gruppo, numord, norddocg";

          String queryDocComunicazioni = "select i.comkey1, i.commsgogg, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, case when COMSTATO='12' then '1' else cast(null as varchar(2))end as ARCHIVIATO, " + this.sqlManager.getDBFunction("datetimetostring",  new String[] {"i.COMDATAPUB"}) + ", w.DIGDESDOC " +
     		"from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + "=w.digkey2 and i.idprg=w.digkey1 and w.digent='W_INVCOM' join gare g on i.comkey1=g.ngara " +
    		"where g.codgar1 = ? and i.compub = '1' and (i.comstato='3' or i.comstato='12') order by i.idcom,w.iddocdig";

          String queryDocDitta = "select d.bustadesc, d.ngara, d.codimp, d.descrizione, d.IDPRG, d.iddocdg, d.DIGNOMDOC, d.datarilascio, d.orarilascio, d.docannul as  ARCHIVIATO, d.doctel as DOCTEL, i.uuid as UUID " +
   			"from v_gare_docditta d join w_docdig w on d.iddocdg=w.iddocdig and d.idprg=w.idprg " +
   			"join imprdocg i on d.codgar = i.codgar and d.ngara = i.ngara and d.codimp = i.codimp and d.norddoci = i.norddoci and d.proveni = i.proveni " +
   			"where d.codgar = ? and d.idprg is not null and d.iddocdg is not null order by d.codgar, d.ngara, d.bustaord, d.codimp, d.norddoci";

          String queryDocAssociati = null;
          if (documentiAssociatiDB.equals("1")) {
        	  queryDocAssociati = "select c.c0akey1, c.c0atit, w.idprg, w.iddocdig, c.c0anomogg, coalesce(";
        	  queryDocAssociati += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) + ",";
        	  queryDocAssociati += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) + ",";
        	  queryDocAssociati += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"});
        	  queryDocAssociati += ") from c0oggass c join gare g on c.c0akey1=g.ngara join w_docdig w on c.c0acod=w.digkey1 and c.c0aprg=w.idprg and w.digent='C0OGGASS' where g.codgar1 = ? and c.c0aent in ('GARE','GCAP','GOEV','GARSED','GARECONT','GAREAVVISI')";
        	  queryDocAssociati += " union select c.c0akey1, c.c0atit, w.idprg, w.iddocdig, c.c0anomogg, coalesce(";
        	  queryDocAssociati += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) + ",";
              queryDocAssociati += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) + ",";
              queryDocAssociati += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"});
              queryDocAssociati +=") from c0oggass c join torn t on c.c0akey1=t.codgar join w_docdig w on c.c0acod=w.digkey1 and c.c0aprg=w.idprg and w.digent='C0OGGASS' where t.codgar = ? and c.c0aent = 'TORN'";
          } else {
        	  queryDocAssociati = "select c.c0akey1, c.c0atit, c.c0aprg, c.c0acod, c.c0anomogg, coalesce(";
        	  queryDocAssociati += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) + ",";
              queryDocAssociati += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) + ",";
              queryDocAssociati += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"});
              queryDocAssociati += ") from c0oggass c join gare g on c.c0akey1=g.ngara where g.codgar1 = ? and c.c0aent in ('GARE','GCAP','GOEV','GARSED','GARECONT','GAREAVVISI')";
        	  queryDocAssociati +=" union select c.c0akey1, c.c0atit, c.c0aprg, c.c0acod, c.c0anomogg, coalesce(";
        	  queryDocAssociati += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) + ",";
              queryDocAssociati += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) + ",";
              queryDocAssociati += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"});
              queryDocAssociati += ") from c0oggass c join torn t on c.c0akey1=t.codgar where t.codgar = ? and c.c0aent  = 'TORN'";
          }

          String queryDocComAllaDitta = "select DISTINCT comkey1, i.commsgogg, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, id.descodsog, " + this.sqlManager.getDBFunction("datetimetostring",  new String[] {"id.DESDATINV"}) + ", w.DIGDESDOC, id.idcom, descodent " +
          "from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + "=w.digkey2 and w.digkey1=i.idprg and w.digent='W_INVCOM' join gare g on i.comkey1=g.ngara " +
          "join (select DISTINCT idprg,idcom,descodsog,descodent,DESSTATO,MIN(DESDATINV) as DESDATINV from w_invcomdes group by idprg,idcom,descodsog,descodent,DESSTATO) id on id.idprg = i.idprg and id.idcom = i.idcom " +
          "where g.codgar1 = ? and i.compub <> '1' and (COMTIPO is null or COMTIPO<>'FS12') and (id.DESSTATO = '2' or id.DESSTATO = '4')";

          String queryDocComDallaDittaTorn = "select comkey1, comkey2, i.commsgogg, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, " + this.sqlManager.getDBFunction("datetimetostring",  new String[] {"i.COMDATINS"}) + ", w.DIGDESDOC " +
          "from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + "=w.digkey1 and i.idprg=w.idprg and w.digent='W_INVCOM' " +
          "join torn t on (i.comkey2=t.codgar) " +
          "where t.codgar = ? and i.compub <> '1' and COMTIPO='FS12' and COMSTATO='3' and COMENT IS NULL order by comkey1,i.idcom,w.iddocdig";

          String queryDocComDallaDittaGare = "select comkey1, comkey2, i.commsgogg, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, " + this.sqlManager.getDBFunction("datetimetostring",  new String[] {"i.COMDATINS"}) + ", w.DIGDESDOC " +
          "from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + "=w.digkey1 and i.idprg=w.idprg and w.digent='W_INVCOM' " +
    	  "join gare g on i.comkey2=g.ngara " +
          "where g.codgar1 = ? and i.compub <> '1' and COMTIPO='FS12' and COMSTATO='3' and COMENT IS NULL order by comkey1,i.idcom,w.iddocdig";

          //Documenti stipula
          String castDATA="DATE";
          if("POS".equals(this.sqlManager.getTipoDB()))
            castDATA="TIMESTAMP";
          else if("MSQ".equals(this.sqlManager.getTipoDB()))
            castDATA="DATETIME";

          String condizioneAppend = this.sqlManager.getDBFunction("concat",  new String[] {"'Documento contratto - '" , "t.tab1desc" });

          String queryDocStipula = "select cast(null as varchar(20)) as LOTTO, " + condizioneAppend + " as GRUPPO,  " +
              " TITOLO as DESCRIZIONE, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, " +
              "cast(null as " + castDATA + ") as DATA " +
              "from G1DOCSTIPULA  d join V_GARE_DOCSTIPULA w on w.id=d.id join tab1 t on t.tab1tip=d.visibilita " +
              "where d.idstipula = ? and w.idprg is not null and w.IDDOCDIG is not null and t.tab1cod='A1182' ";

          String queryDocAssociatiStipula = null;
          String sqlSintassi= this.sqlManager.getDBFunction("inttostr",  new String[] {"g.id"});
          if (documentiAssociatiDB.equals("1")) {
            queryDocAssociatiStipula = "select c.c0akey1, c.c0atit, w.idprg, w.iddocdig, c.c0anomogg, coalesce(";
            queryDocAssociatiStipula += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) + ",";
            queryDocAssociatiStipula += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) + ",";
            queryDocAssociatiStipula += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"});
            queryDocAssociatiStipula += ") from c0oggass c join G1STIPULA g on c.c0akey1=" + sqlSintassi + " join w_docdig w on c.c0acod=w.digkey1 and c.c0aprg=w.idprg and w.digent='C0OGGASS' where g.id = ? and c.c0aent in ('G1STIPULA','G1DOCSTIPULA')";
          } else {
            queryDocAssociatiStipula = "select c.c0akey1, c.c0atit, c.c0aprg, c.c0acod, c.c0anomogg, coalesce(";
            queryDocAssociatiStipula += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) + ",";
            queryDocAssociatiStipula += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) + ",";
            queryDocAssociatiStipula += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"});
            queryDocAssociatiStipula += ") from c0oggass c join G1STIPULA g on c.c0akey1=" + sqlSintassi + " where g.id = ? and c.c0aent in ('G1STIPULA','G1DOCSTIPULA')";
          }

          String queryDocComAllaDittaStipula = "select DISTINCT comkey1, i.commsgogg, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, id.descodsog, " + this.sqlManager.getDBFunction("datetimetostring",  new String[] {"id.DESDATINV"}) + ", w.DIGDESDOC, id.idcom, descodent " +
              "from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + "=w.digkey2 and w.digkey1=i.idprg and w.digent='W_INVCOM' join g1stipula g on i.comkey1= g.codstipula " +
              "join (select DISTINCT idprg,idcom,descodsog,descodent,DESSTATO,MIN(DESDATINV) as DESDATINV from w_invcomdes group by idprg,idcom,descodsog,descodent,DESSTATO) id on id.idprg = i.idprg and id.idcom = i.idcom " +
              "where g.id = ? and i.compub <> '1' and (COMTIPO is null or COMTIPO<>'FS12') and (id.DESSTATO = '2' or id.DESSTATO = '4')";

          String queryDocComDallaDittaStipula = "select comkey1, comkey2, i.commsgogg, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, " + this.sqlManager.getDBFunction("datetimetostring",  new String[] {"i.COMDATINS"}) + ", w.DIGDESDOC " +
              "from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + "=w.digkey1 and i.idprg=w.idprg and w.digent='W_INVCOM' " +
              "join g1stipula g on i.comkey2=g.codstipula " +
              "where g.id = ? and i.compub <> '1' and COMTIPO='FS12' and COMSTATO='3' and COMENT = 'G1STIPULA' order by comkey1,i.idcom,w.iddocdig";

          for(int i=0;i<jobs.size();i++){
        	  numErrori = 0;
        	  String messaggioErrore = "";
        	  Long id_archiviazione = SqlManager.getValueFromVectorParam(jobs.get(i), 0).longValue();
        	  Long syscon = SqlManager.getValueFromVectorParam(jobs.get(i), 1).longValue();
        	  String chiaveGardoc = SqlManager.getValueFromVectorParam(jobs.get(i), 2).getStringValue();
        	  String codgar = null;
        	  String entitaGardoc =SqlManager.getValueFromVectorParam(jobs.get(i), 3).getStringValue();
        	  Long genere = null;
        	  String oggetto = "gara";
        	  String entita=null;
        	  Long idStipula=null;
        	  String codgarApp=null;

        	  if("G1STIPULA".equals(entitaGardoc)) {
        	    oggetto = "stipula";
        	    idStipula =  new Long(chiaveGardoc);
        	    codgar = (String)this.sqlManager.getObject("select codstipula from g1stipula where id=?", new Object[] {idStipula});
        	    codgarApp = codgar;
        	  }else {
        	    codgar = chiaveGardoc;
        	    codgarApp = codgar;
        	    genere = (Long)this.sqlManager.getObject("select min(genere) from v_gare_genere where codgar = ?", new Object[]{codgar});
                if (codgar.startsWith("$")) {
                    codgarApp = codgar.substring(1);
                }
            	  if (genere != null) {
            		  if (genere.equals(new Long(10))) {
                		  oggetto = "elenco operatori";
                	  } else if (genere.equals(new Long(20))) {
                		  oggetto = "catalogo elettronico";
                	  }else if (genere.equals(new Long(11))) {
                        oggetto = "avviso";
                    }
            	  }
        	  }

        	  //Per ogni record da elaborare
        	  try {
        	    List<?> listaDocumentiGara = null;
                List<?> listaDocumentiComunicazioni = null;
                List<?> listaDocumentiDitta = null;
                List<?> listaDocumentiAssociati = null;
                List<?> listaDocumentiComAllaDitta = null;
                List<?> listaDocumentiComDallaDitta = null;

                List<?> listaDocumentiStipula = null;

        	    if("G1STIPULA".equals(entitaGardoc)) {
        	      listaDocumentiStipula = this.sqlManager.getListVector(queryDocStipula, new Object[]{idStipula});
                  listaDocumentiAssociati = this.sqlManager.getListVector(queryDocAssociatiStipula, new Object[]{idStipula});
                  listaDocumentiComAllaDitta = this.sqlManager.getListVector(queryDocComAllaDittaStipula, new Object[]{idStipula});
                  listaDocumentiComDallaDitta = this.sqlManager.getListVector(queryDocComDallaDittaStipula, new Object[]{idStipula});
        	    }else {
        		  listaDocumentiGara = this.sqlManager.getListVector(queryDocGara, new Object[]{codgar});
        		  listaDocumentiComunicazioni = this.sqlManager.getListVector(queryDocComunicazioni, new Object[]{codgar});
        		  listaDocumentiDitta = this.sqlManager.getListVector(queryDocDitta, new Object[]{codgar});
        		  listaDocumentiAssociati = this.sqlManager.getListVector(queryDocAssociati, new Object[]{codgar, codgar});
        		  listaDocumentiComAllaDitta = this.sqlManager.getListVector(queryDocComAllaDitta, new Object[]{codgar});
        		  listaDocumentiComDallaDitta = null;
        		  if(new Long(1).equals(genere)){
        		    listaDocumentiComDallaDitta = this.sqlManager.getListVector(queryDocComDallaDittaTorn, new Object[]{codgar});
        		  }else{
        		    listaDocumentiComDallaDitta = this.sqlManager.getListVector(queryDocComDallaDittaGare, new Object[]{codgar});
        		  }
        	    }

            	  if((listaDocumentiGara!=null && listaDocumentiGara.size()>0) ||
            			  (listaDocumentiComDallaDitta!=null && listaDocumentiComDallaDitta.size()>0) ||
            			  (listaDocumentiComAllaDitta!=null && listaDocumentiComAllaDitta.size()>0) ||
            			  (listaDocumentiAssociati!=null && listaDocumentiAssociati.size()>0) ||
            			  (listaDocumentiDitta!=null && listaDocumentiDitta.size()>0) ||
            			  (listaDocumentiComunicazioni!=null && listaDocumentiComunicazioni.size()>0) ||
            			  (listaDocumentiStipula!=null && listaDocumentiStipula.size()>0)){
            		  //creo una cartella temporanea per contenere tutti i documenti della gara
            		  File CartellaTemporaneaDocGara = null;
            		  String codgarMod = codgar.toUpperCase().replaceAll("/", "_");
            		  try {
            		      CartellaTemporaneaDocGara = new File (pathArchivioDocumenti + "/" + codgarMod);
            			  if (CartellaTemporaneaDocGara.exists()) {
            				  this.RemoveDirectory(CartellaTemporaneaDocGara);
            			  }
            			  CartellaTemporaneaDocGara.mkdirs();
            		  } catch (Exception e) {
            			  logger.error("creazioneArchivioDocumentiGara : impossibile creare directory " + pathArchivioDocumenti + "/" + codgarMod, e);
           		          return;
            		  }

            		  String nomeFile = codgarApp.toUpperCase().replaceAll("/", "_");
            		  try {
            			  fileIndice = new PrintWriter(pathArchivioDocumenti + "/" + codgarMod + "/" + nomeFile + "_fileIndice.csv", "UTF-8");
            			  //Aggiungo intestazione file indice
            			  StringBuffer fileIndiceRow = new StringBuffer();
            			  fileIndiceRow.append("Codice " + oggetto + ";");
            			  if (!codgar.startsWith("$") && !"G1STIPULA".equals(entitaGardoc)) {
            				  fileIndiceRow.append("Codice lotto;");
            			  }
            			  fileIndiceRow.append("Argomento;");
            			  fileIndiceRow.append("Tipologia;");
            			  fileIndiceRow.append("Cod.fiscale ditta;");
            			  fileIndiceRow.append("Ragione sociale ditta;");
            			  fileIndiceRow.append("Descrizione file;");
            			  fileIndiceRow.append("Nome file;");
            			  fileIndiceRow.append("Data;");
            			  fileIndiceRow.append("Doc.archiviato/annullato da operatore?");
            			  fileIndice.println(fileIndiceRow.toString());
            		  } catch (Exception e) {
            			  logger.error("creazioneArchivioDocumentiGara : impossibile creare fileIndiceGara " + codgarMod + ".csv", e);
           		          return;
            		  }

            		  //creo file Zip contenente tutti i documenti
            		  try {
            		      zipOut = new ZipOutputStream(new FileOutputStream(pathArchivioDocumenti + "/" + nomeFile + "_Documenti.zip"));
            			  zipOut.setMethod(ZipOutputStream.DEFLATED);
                          zipOut.setLevel(Deflater.DEFAULT_COMPRESSION);
            		  } catch (FileNotFoundException e) {
            			  logger.error("creazioneArchivioDocumentiGara : impossibile creare " + nomeFile + "_Documenti.zip", e);
           		          return;
            		  }
            		  //Documentazione di gara
            		  if(listaDocumentiGara!=null){
                		  for(int j=0; j<listaDocumentiGara.size();j++) {
                			  try {
                				  HashMap<String,Object> campi = new HashMap<String,Object>();
                				  campi.put("argomento", "Documentazione " + oggetto);
                				  campi.put("gruppo", this.getGruppo(SqlManager.getValueFromVectorParam(listaDocumentiGara.get(j), 1).getStringValue()));
                				  campi.put("lotto", SqlManager.getValueFromVectorParam(listaDocumentiGara.get(j), 0).getStringValue());
                				  campi.put("cfDitta", "");
                				  campi.put("ditta", "");
                				  campi.put("descrizione", SqlManager.getValueFromVectorParam(listaDocumentiGara.get(j), 2).getStringValue());
                				  campi.put("idprg", SqlManager.getValueFromVectorParam(listaDocumentiGara.get(j), 3).getStringValue());
                				  campi.put("iddocdig", SqlManager.getValueFromVectorParam(listaDocumentiGara.get(j), 4).longValue());
                				  campi.put("dignomdoc", SqlManager.getValueFromVectorParam(listaDocumentiGara.get(j), 5).getStringValue());
                				  campi.put("data", SqlManager.getValueFromVectorParam(listaDocumentiGara.get(j), 7).getStringValue());
                				  campi.put("archiviato", SqlManager.getValueFromVectorParam(listaDocumentiGara.get(j), 6).getStringValue().equals("1")?"si":"");
                				  this.addFileToArchive(codgar, entitaGardoc, pathArchivioDocumenti, campi, zipOut, fileIndice);
                			  } catch(Exception e) {
                				  logger.error("creazioneArchivioDocumentiGara : errore durante il recupero di un file Documentazione di gara ", e);
                				  messaggioErrore += "Errore documentazione di gara: " + e.getMessage() + "\r\n";
                				  numErrori++;
                			  }
                		  }
            		  }

            		  //Documentazione di stipula
                      if(listaDocumentiStipula!=null) {
              		  for(int j=0; j<listaDocumentiStipula.size();j++) {
                            try {
                                HashMap<String,Object> campi = new HashMap<String,Object>();
                                campi.put("argomento", "Documentazione " + oggetto);
                                campi.put("gruppo", SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j), 1).getStringValue());
                                campi.put("lotto", SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j), 0).getStringValue());
                                campi.put("cfDitta", "");
                                campi.put("ditta", "");
                                campi.put("descrizione", SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j), 2).getStringValue());
                                campi.put("idprg", SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j), 3).getStringValue());
                                campi.put("iddocdig", SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j), 4).longValue());
                                campi.put("dignomdoc", SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j), 5).getStringValue());
                                campi.put("data", SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j), 7).getStringValue());
                                campi.put("archiviato", SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j), 6).getStringValue().equals("1")?"si":"");
                                this.addFileToArchive(codgar, entitaGardoc, pathArchivioDocumenti, campi, zipOut, fileIndice);
                            } catch(Exception e) {
                                logger.error("creazioneArchivioDocumentiGara : errore durante il recupero di un file Documentazione della stipula ", e);
                                messaggioErrore += "Errore documentazione della stipula: " + e.getMessage() + "\r\n";
                                numErrori++;
                            }
                        }
                      }

            		  //Comunicazioni pubbliche
                      if(listaDocumentiComunicazioni!=null) {
                          for(int j=0; j<listaDocumentiComunicazioni.size();j++) {
                			  try {
                				  HashMap<String,Object> campi = new HashMap<String,Object>();
                				  campi.put("argomento", "Documentazione " + oggetto);
                				  campi.put("gruppo", "Comunicazione pubblica");
                				  campi.put("lotto", SqlManager.getValueFromVectorParam(listaDocumentiComunicazioni.get(j), 0).getStringValue());
                				  campi.put("cfDitta", "");
                				  campi.put("ditta", "");
                				  campi.put("descrizione", SqlManager.getValueFromVectorParam(listaDocumentiComunicazioni.get(j), 1).getStringValue() + " - " + SqlManager.getValueFromVectorParam(listaDocumentiComunicazioni.get(j), 6).getStringValue());
                				  campi.put("idprg", SqlManager.getValueFromVectorParam(listaDocumentiComunicazioni.get(j), 2).getStringValue());
                				  campi.put("iddocdig", SqlManager.getValueFromVectorParam(listaDocumentiComunicazioni.get(j), 3).longValue());
                				  campi.put("dignomdoc", SqlManager.getValueFromVectorParam(listaDocumentiComunicazioni.get(j), 4).getStringValue());
                                  campi.put("archiviato", SqlManager.getValueFromVectorParam(listaDocumentiComunicazioni.get(j), 5).getStringValue().equals("1")?"si":"");
                				  campi.put("data", SqlManager.getValueFromVectorParam(listaDocumentiComunicazioni.get(j), 6).getStringValue());
                				  this.addFileToArchive(codgar, entitaGardoc, pathArchivioDocumenti, campi, zipOut, fileIndice);
                			  } catch(Exception e) {
                				  logger.error("creazioneArchivioDocumentiGara : errore durante il recupero di un file Comunicazioni pubbliche", e);
                				  messaggioErrore += "Errore comunicazioni pubbliche: " + e.getMessage() + "\r\n";
                				  numErrori++;
                			  }
                		  }
                      }

            		  //Documenti associati
                      if(listaDocumentiAssociati!=null) {
                          for(int j=0; j<listaDocumentiAssociati.size();j++) {
                			  try {
                				  HashMap<String,Object> campi = new HashMap<String,Object>();
                				  campi.put("argomento", "Documentazione " + oggetto);
                				  campi.put("gruppo", "Documento associato");
                				  campi.put("lotto", SqlManager.getValueFromVectorParam(listaDocumentiAssociati.get(j), 0).getStringValue());
                				  campi.put("cfDitta", "");
                				  campi.put("ditta", "");
                				  campi.put("descrizione", SqlManager.getValueFromVectorParam(listaDocumentiAssociati.get(j), 1).getStringValue());
                				  campi.put("idprg", SqlManager.getValueFromVectorParam(listaDocumentiAssociati.get(j), 2).getStringValue());
                				  campi.put("iddocdig", SqlManager.getValueFromVectorParam(listaDocumentiAssociati.get(j), 3).longValue());
                				  campi.put("dignomdoc", SqlManager.getValueFromVectorParam(listaDocumentiAssociati.get(j), 4).getStringValue());
               					  campi.put("data", SqlManager.getValueFromVectorParam(listaDocumentiAssociati.get(j), 5).getStringValue());
                				  campi.put("archiviato", "");
                				  if (documentiAssociatiDB.equals("1")) {
                					  this.addFileToArchive(codgar, entitaGardoc, pathArchivioDocumenti, campi, zipOut, fileIndice);
                				  } else {
                					  this.addFileToArchive(codgar, entitaGardoc, pathArchivioDocumenti, campi, zipOut, fileIndice, pathDocumentiAssociati);
                				  }
                			  } catch(Exception e) {
                				  logger.error("creazioneArchivioDocumentiGara : errore durante il recupero di un file Documenti associati", e);
                				  messaggioErrore += "Errore documenti associati: " + e.getMessage() + "\r\n";
                				  numErrori++;
                			  }
                		  }
                      }

            		  //Documenti presentati dalla ditta
                      if(listaDocumentiDitta!=null) {
                          String dataApp;
                		  String idprg = null;
                          String doctel = null;
                          String uuid = null;
                          String selecStatoComunicazione = "select i.comstato from w_invcom i,w_docdig d where d.idprg=? and d.digkey3=? and i.idprg=d.idprg and i.idcom=" + this.sqlManager.getDBFunction("strtoint",  new String[] {"d.digkey1" });
                		  for(int j=0; j<listaDocumentiDitta.size();j++) {
                			  try {
                				  HashMap<String,Object> campi = new HashMap<String,Object>();
                				  idprg = SqlManager.getValueFromVectorParam(listaDocumentiDitta.get(j), 4).getStringValue();
                				  doctel = SqlManager.getValueFromVectorParam(listaDocumentiDitta.get(j), 10).getStringValue();
                				  uuid = SqlManager.getValueFromVectorParam(listaDocumentiDitta.get(j), 11).getStringValue();
                				  if("1".equals(doctel) && uuid!=null && !"".equals(uuid)) {
                	                 String comstato = (String)this.sqlManager.getObject(selecStatoComunicazione, new Object[]{idprg,uuid});
                	                 if("13".equals(comstato) || "20".equals(comstato))
                	                   continue;
                	              }

                				  campi.put("argomento", "Documentazione presentata dalle ditte");
                				  campi.put("gruppo", SqlManager.getValueFromVectorParam(listaDocumentiDitta.get(j), 0).getStringValue());
                				  campi.put("lotto", SqlManager.getValueFromVectorParam(listaDocumentiDitta.get(j), 1).getStringValue());
                				  this.getDitta(SqlManager.getValueFromVectorParam(listaDocumentiDitta.get(j), 2).getStringValue(), campi, false,null);
                				  campi.put("descrizione", SqlManager.getValueFromVectorParam(listaDocumentiDitta.get(j), 3).getStringValue());
                				  campi.put("idprg", SqlManager.getValueFromVectorParam(listaDocumentiDitta.get(j), 4).getStringValue());
                				  campi.put("iddocdig", SqlManager.getValueFromVectorParam(listaDocumentiDitta.get(j), 5).longValue());
                				  campi.put("dignomdoc", SqlManager.getValueFromVectorParam(listaDocumentiDitta.get(j), 6).getStringValue());
                				  dataApp = "";
                				  if (SqlManager.getValueFromVectorParam(listaDocumentiDitta.get(j), 7).dataValue() != null) {
                					  dataApp = UtilityDate.convertiData(SqlManager.getValueFromVectorParam(listaDocumentiDitta.get(j), 7).dataValue(), UtilityDate.FORMATO_GG_MM_AAAA);
                					  dataApp += " " +  SqlManager.getValueFromVectorParam(listaDocumentiDitta.get(j), 8).getStringValue();
                				  }
                				  campi.put("data", dataApp);
                				  campi.put("archiviato", SqlManager.getValueFromVectorParam(listaDocumentiDitta.get(j), 9).getStringValue().equals("1")?"si":"");
                				  this.addFileToArchive(codgar, entitaGardoc, pathArchivioDocumenti, campi, zipOut, fileIndice);
                			  } catch(Exception e) {
                				  logger.error("creazioneArchivioDocumentiGara : errore durante il recupero di un file Documenti presentati dalla ditta", e);
                				  messaggioErrore += "Errore documentazione delle ditte: " + e.getMessage() + "\r\n";
                				  numErrori++;
                			  }
                		  }
                      }

            		  //Documenti inviati alla ditta
                      if(listaDocumentiComAllaDitta!=null) {
                          for(int j=0; j<listaDocumentiComAllaDitta.size();j++) {
                			  try {
                				  HashMap<String,Object> campi = new HashMap<String,Object>();
                				  campi.put("argomento", "Documentazione " + oggetto);
                				  campi.put("gruppo", "Documento inviato alla ditta");
                				  campi.put("lotto", SqlManager.getValueFromVectorParam(listaDocumentiComAllaDitta.get(j), 0).getStringValue());
                				  entita= SqlManager.getValueFromVectorParam(listaDocumentiComAllaDitta.get(j), 9).getStringValue();
                				  this.getDitta(SqlManager.getValueFromVectorParam(listaDocumentiComAllaDitta.get(j), 5).getStringValue(), campi, false,entita);
                				  if (campi.get("ditta").equals("")) {
                					  Long idcom = SqlManager.getValueFromVectorParam(listaDocumentiComAllaDitta.get(j), 8).longValue();
                					  String email = (String) this.sqlManager.getObject("select desmail from w_invcomdes where idcom=? and descodent is null and descodsog is null", new Object[]{idcom});
                					  campi.put("ditta", email);
                				  }
                				  campi.put("descrizione", SqlManager.getValueFromVectorParam(listaDocumentiComAllaDitta.get(j), 1).getStringValue() + " - " + SqlManager.getValueFromVectorParam(listaDocumentiComAllaDitta.get(j), 7).getStringValue());
                				  campi.put("idprg", SqlManager.getValueFromVectorParam(listaDocumentiComAllaDitta.get(j), 2).getStringValue());
                				  campi.put("iddocdig", SqlManager.getValueFromVectorParam(listaDocumentiComAllaDitta.get(j), 3).longValue());
                				  campi.put("dignomdoc", SqlManager.getValueFromVectorParam(listaDocumentiComAllaDitta.get(j), 4).getStringValue());
               					  campi.put("data", SqlManager.getValueFromVectorParam(listaDocumentiComAllaDitta.get(j), 6).getStringValue());
                				  campi.put("archiviato", "");
                				  this.addFileToArchive(codgar, entitaGardoc, pathArchivioDocumenti, campi, zipOut, fileIndice);
                			  } catch(Exception e) {
                				  logger.error("creazioneArchivioDocumentiGara : errore durante il recupero di un file Documenti inviato alla ditta", e);
                				  messaggioErrore += "Errore documenti inviati alla ditta: " + e.getMessage() + "\r\n";
                				  numErrori++;
                			  }
                		  }
                      }

            		  //Documenti inviati dalla ditta
                      if(listaDocumentiComDallaDitta!=null) {
                          for(int j=0; j<listaDocumentiComDallaDitta.size();j++) {
                			  try {
                				  HashMap<String,Object> campi = new HashMap<String,Object>();
                				  campi.put("argomento", "Documentazione presentata dalle ditte");
                				  campi.put("gruppo", "Documento inviato dalla ditta");
                				  campi.put("lotto", SqlManager.getValueFromVectorParam(listaDocumentiComDallaDitta.get(j), 1).getStringValue());
                				  this.getDitta(SqlManager.getValueFromVectorParam(listaDocumentiComDallaDitta.get(j), 0).getStringValue(), campi, true,null);
                				  campi.put("descrizione", SqlManager.getValueFromVectorParam(listaDocumentiComDallaDitta.get(j), 2).getStringValue()+ " - " + SqlManager.getValueFromVectorParam(listaDocumentiComDallaDitta.get(j), 7).getStringValue());
                				  campi.put("idprg", SqlManager.getValueFromVectorParam(listaDocumentiComDallaDitta.get(j), 3).getStringValue());
                				  campi.put("iddocdig", SqlManager.getValueFromVectorParam(listaDocumentiComDallaDitta.get(j), 4).longValue());
                				  campi.put("dignomdoc", SqlManager.getValueFromVectorParam(listaDocumentiComDallaDitta.get(j), 5).getStringValue());
                				  campi.put("data", SqlManager.getValueFromVectorParam(listaDocumentiComDallaDitta.get(j), 6).getStringValue());
                				  campi.put("archiviato", "");
                				  this.addFileToArchive(codgar, entitaGardoc, pathArchivioDocumenti, campi, zipOut, fileIndice);
                			  } catch(Exception e) {
                				  logger.error("creazioneArchivioDocumentiGara : errore durante il recupero di un file Documenti inviato dalla ditta", e);
                				  messaggioErrore += "Errore documenti inviati dalla ditta: " + e.getMessage() + "\r\n";
                				  numErrori++;
                			  }
                		  }
                      }

            		  fileIndice.close();
            		  //aggiungo il file indice allo zip
            		  File f = new File(pathArchivioDocumenti + "/" + codgarMod + "/" + nomeFile + "_fileIndice.csv");
            		  byte[] bFile = new byte[(int) f.length()];
            		  FileInputStream indice = new FileInputStream(f);
            		  indice.read(bFile);
            		  indice.close();
            		  zipOut.putNextEntry(new ZipEntry(nomeFile + "_fileIndice.csv"));
                      zipOut.write(bFile, 0, bFile.length);
                      zipOut.closeEntry();
                      zipOut.flush();
                      zipOut.close();
                      //cancello directory temporanea della gara
                      this.RemoveDirectory(CartellaTemporaneaDocGara);
                      //scrivo il messaggio di avvenuta creazione dell'archivio all'utente che l'ha richiesto
                      this.updateJob(syscon, codgar, numErrori > 0 ? 1:0, id_archiviazione, messaggioErrore, genere, entitaGardoc);
            	  } else {
            		  //non esistono documenti per la gara
            		  this.updateJob(syscon, codgar, 2, id_archiviazione, messaggioErrore, genere, entitaGardoc);
            	  }
        	  } catch (SQLException e) {
    			  logger.error("creazioneArchivioDocumentiGara : errore durante il recupero dei documenti gara con queryDoc " + codgar, e);
    			  messaggioErrore += "Errore di estrapolazione dati: " + e.getMessage();
    			  this.updateJob(syscon, codgar, 3, id_archiviazione, messaggioErrore, genere, entitaGardoc);
    		  }
          }
        }
      } catch (SQLException e) {
        logger.error("Errore durante la lettura dei dati degli elenchi/cataloghi da processare per i controlli sui rinnovi in scadenza", e);
      } catch (GestoreException e) {
        logger.error("Errore durante la lettura dei dati degli elenchi/cataloghi da processare per i controlli sui rinnovi in scadenza", e);
      } catch (DataAccessException e) {
        logger.error("Errore durante l'inserimento della comunicazione di richiesta rinnovo", e);
      } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
      }
    }

    logger.debug("creazioneArchivioDocumentiGara: fine metodo");
  }

  private String getGruppo(String gruppo) {
	  if (gruppo != null && !gruppo.equals("")) {
		  int codiceGruppo = Integer.parseInt(gruppo);
		  switch(codiceGruppo) {
		  	case 1:
		  		return "Documento del bando/avviso";
		  	case 3:
				  return "Fac-simile documento richiesto ai concorrenti";
		  	case 4:
				  return "Documento dell'esito";
		  	case 5:
				  return "Documento per la trasparenza";
		  	case 6:
				  return "Documento dell'invito a presentare offerta";
		  	case 10:
				  return "Atto o documento art.29 c.1 DLgs.50/2016";
		  	case 11:
				  return "Documento allegato all'ordine di acquisto";
		  	case 12:
				  return "Documento dell'invito all'asta elettronica";
		  	case 15:
                  return "Delibera a contrarre o atto equivalente";
		  }
	  }
	  return "";
  }

  private void addFileToArchive(String codgar, String entita, String pathArchivioDocumenti, HashMap<?,?> campi, ZipOutputStream zipOut, PrintWriter fileIndice) throws Exception {
	  BlobFile file = this.fileAllegatoManager.getFileAllegato((String)campi.get("idprg"),(Long)campi.get("iddocdig"));
	  String codgarMod = codgar.toUpperCase().replaceAll("/", "_");
	  OutputStream out = new FileOutputStream(pathArchivioDocumenti + "/" + codgarMod + "/" + (String)campi.get("idprg") + campi.get("iddocdig") + "_" + (String)campi.get("dignomdoc"));
	  out.write(file.getStream());
	  out.close();
	  //Aggiungo il file allo zip
	  try {
		  zipOut.putNextEntry(new ZipEntry((String)campi.get("idprg") + (Long)campi.get("iddocdig") + "_" + (String)campi.get("dignomdoc")));
		  zipOut.write(file.getStream(), 0, file.getStream().length);
	      zipOut.closeEntry();
	      zipOut.flush();
	  } catch (ZipException e) {
		  // il file è già presente nello zip .., lo ignoro
		  ;
	  }
	  StringBuffer fileIndiceRow = new StringBuffer();
	  String codgarApp = codgar;
	  if (codgar.startsWith("$")) {
		  codgarApp = codgar.substring(1);
	  }
	  fileIndiceRow.append("\"").append(codgarApp).append("\";");
	  if (!codgar.startsWith("$") && !"G1STIPULA".equals(entita)) {
		  if (codgarApp.equals(campi.get("lotto"))) {
			  fileIndiceRow.append("\"").append("\";");
		  } else {
			  fileIndiceRow.append("\"").append((String)campi.get("lotto")).append("\";");
		  }
	  }
	  fileIndiceRow.append("\"").append((String)campi.get("argomento")).append("\";");
	  fileIndiceRow.append("\"").append((String)campi.get("gruppo")).append("\";");
	  fileIndiceRow.append("\"").append((String)campi.get("cfDitta")).append("\";");
	  fileIndiceRow.append("\"").append((String)campi.get("ditta")).append("\";");
	  fileIndiceRow.append("\"").append((String)campi.get("descrizione")).append("\";");
	  fileIndiceRow.append("\"").append((String)campi.get("idprg")).append(campi.get("iddocdig")).append("_").append((String)campi.get("dignomdoc")).append("\";");
	  fileIndiceRow.append((String)campi.get("data")).append(";");
	  fileIndiceRow.append("\"").append((String)campi.get("archiviato")).append("\"");
	  fileIndice.println(fileIndiceRow.toString());
  }

  private void addFileToArchive(String codgar, String entita, String pathArchivioDocumenti, HashMap<?,?> campi, ZipOutputStream zipOut, PrintWriter fileIndice, String pathDocumentiAssociati) throws Exception {
	  File fileAssociato = new File(pathDocumentiAssociati + "/" + (String)campi.get("dignomdoc"));
	  if (fileAssociato.exists()) {
		  //copia il file associato nella cartella
	      String codgarMod = codgar.toUpperCase().replaceAll("/", "_");
	      File destFile = new File(pathArchivioDocumenti + "/" + codgarMod + "/" + (String)campi.get("idprg") + campi.get("iddocdig") + "_" + (String)campi.get("dignomdoc"));
		  FileUtils.copyFile(fileAssociato, destFile);
		  //Aggiungo il file allo zip
		  byte[] b = FileUtils.readFileToByteArray(fileAssociato);
		  zipOut.putNextEntry(new ZipEntry((String)campi.get("dignomdoc")));
	      zipOut.write(b, 0, b.length);
	      zipOut.closeEntry();
	      zipOut.flush();
		  StringBuffer fileIndiceRow = new StringBuffer();
		  String codgarApp = codgar;
		  if (codgar.startsWith("$")) {
			  codgarApp = codgar.substring(1);
		  }
		  fileIndiceRow.append("\"").append(codgarApp).append("\";");
		  if (!codgar.startsWith("$") && !"G1STIPULA".equals(entita)) {
			  if (codgarApp.equals(campi.get("lotto"))) {
				  fileIndiceRow.append("\"").append("\";");
			  } else {
				  fileIndiceRow.append("\"").append((String)campi.get("lotto")).append("\";");
			  }
		  }
		  fileIndiceRow.append("\"").append((String)campi.get("argomento")).append("\";");
		  fileIndiceRow.append("\"").append((String)campi.get("gruppo")).append("\";");
		  fileIndiceRow.append("\"").append((String)campi.get("cfDitta")).append("\";");
		  fileIndiceRow.append("\"").append((String)campi.get("ditta")).append("\";");
		  fileIndiceRow.append("\"").append((String)campi.get("descrizione")).append("\";");
		  fileIndiceRow.append("\"").append((String)campi.get("dignomdoc")).append("\";");
		  fileIndiceRow.append((String)campi.get("data")).append(";");
		  fileIndiceRow.append("\"").append((String)campi.get("archiviato")).append("\"");
		  fileIndice.println(fileIndiceRow.toString());
	  }
  }

  private void getDitta(String codimp, HashMap<String,Object> campi, boolean isLogin, String entita) throws Exception {
	  //se sto verificando la presenza dell'utente nell W_PUSER
	  campi.put("cfDitta", "");
	  campi.put("ditta", "");
	  if (isLogin) {
		  String codPUser = (String)this.sqlManager.getObject("select userkey1 from w_puser where usernome = ? and userent = 'IMPR'", new Object[]{codimp});
		  if (codPUser != null) {
			  codimp = codPUser;
		  } else {
			  return;
		  }
	  }
	  //verifico se la ditta è un raggruppamento
	  String getDatiDestinatario = "select codimp, cfimp, nomest, tipimp from impr where codimp = ?";
	  if("TECNI".equals(entita))
	    getDatiDestinatario = "select codtec, cftec, nomtec from tecni where codtec = ?";
	  List<?> listaImpresa = this.sqlManager.getListVector(getDatiDestinatario, new Object[]{codimp});
	  if (listaImpresa != null && listaImpresa.size()>0) {
		  String cod = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 0).getStringValue();
		  Long tipimp = null;
		  if("IMPR".equals(entita))
		    tipimp=SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).longValue();
		  String nomeImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue();
		  if (tipimp!=null && (tipimp == 3 || tipimp == 10)) {
			  //la ditta è un raggruppamento allora prendo i dati della mandataria
			  cod = (String)this.sqlManager.getObject("select coddic from ragimp where codime9 = ? and impman='1'", new Object[]{cod});
			  listaImpresa = this.sqlManager.getListVector(getDatiDestinatario, new Object[]{cod});
			  if (listaImpresa != null && listaImpresa.size()>0) {
				  nomeImpresa += "(mandataria " + SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue() + ")";
			  }
		  }
		  campi.put("cfDitta", SqlManager.getValueFromVectorParam(listaImpresa.get(0), 1).getStringValue());
		  campi.put("ditta", nomeImpresa);
	  }
  }

  private void RemoveDirectory(File directory) {
	  try {
		  String[]entries = directory.list();
		  for(String s: entries){
		      File currentFile = new File(directory.getPath(),s);
		      currentFile.delete();
		  }
		  directory.delete();
	  } catch (Exception e) {
		  logger.error("creazioneArchivioDocumentiGara.RemoveDirectory : impossibile rimuovere la cartella", e);
	  }
  }

  private void updateJob(Long syscon, String codgar, int errore, Long id_archiviazione, String MessaggiErrore, Long genere, String entita) {
	  try {
		  String codgarApp = codgar;
    	  if (codgar.startsWith("$")) {
    		  codgarApp = codgar.substring(1);
    	  }
    	  String oggetto = "la gara";
    	  if("G1STIPULA".equals(entita)) {
    	    oggetto = "la stipula";
    	  }else {
        	  if (genere != null) {
        		  if (genere.equals(new Long(10))) {
            		  oggetto = "l'elenco operatori";
            	  } else if (genere.equals(new Long(20))) {
            		  oggetto = "il catalogo elettronico";
            	  } else if (genere.equals(new Long(11))) {
            	    oggetto = "l'avviso";
            	  }
        	  }
    	  }
		  String link = "<a href='javascript:downloadExportDocumenti(" + id_archiviazione + ")'>";
		  String message = "E' stata generata l'intera documentazione per " + oggetto + " " + codgarApp + ". Per scaricare l'archivio premi " + link + "qui</a>" ;
		  switch (errore) {
		  	case 1:
		  		message = "La documentazione per " + oggetto + "a " + codgarApp + " è stata generata con alcuni errori. Per scaricare l'archivio premi " + link + "qui</a>" ;
		  		break;
		  	case 2:
		  		message = "Non ci sono documenti per " + oggetto + " " + codgarApp;
		  		break;
		  	case 3:
		  		message = "Si è verificato un errore durante la generazione della documentazione per " + oggetto + " " + codgarApp;
		  		break;
		  }
		  //aggiorno flag di esecuzione nella lista record da elaborare
		  String messaggioApp = message;
		  if (!MessaggiErrore.equals("")) {
			  messaggioApp += "\r\n" + MessaggiErrore;
		  }
          this.sqlManager.update("update gardoc_jobs set da_processare = '2', data_creazione = ?, esito = ? where id_archiviazione = ?", new Object[]{new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()), messaggioApp, id_archiviazione});
          //inserisco il messaggio
	      String insertW_MESSAGE_IN = "insert into w_message_in (message_id, message_date, message_subject, message_body, message_sender_syscon, message_recipient_syscon, message_recipient_read) values (?,?,?,?,?,?,?)";
	      Long maxMessageIdIn = (Long) this.sqlManager.getObject("select max(message_id) from w_message_in", new Object[] {});
	      if (maxMessageIdIn == null) maxMessageIdIn = new Long(0);
	      maxMessageIdIn = new Long(maxMessageIdIn.longValue() + 1);
	      this.sqlManager.update(insertW_MESSAGE_IN, new Object[] { maxMessageIdIn, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),
	    		  message, null, 50, syscon, new Long(0) });
	  } catch (Exception e) {
		  logger.error("creazioneArchivioDocumentiGara.scriviMessaggio : impossibile scrivere il messaggio di avvenuta creazione file zip", e);
	  }
  }

  /**
   * Il metodo restituisce l'id dell'occorrenza inserita in gardoc_jobs
   * @param syscon
   * @param codgar
   * param entita
   * @return Long
   * @throws Exception
   */
  public Long insertJob(Long syscon, String codgar, String entita) throws Exception{

	Long id_archiviazione = null;
    try {
		  id_archiviazione = new Long(genChiaviManager.getNextId("GARDOC_JOBS"));

		  //aggiorno flag di esecuzione nella lista record da elaborare
          this.sqlManager.update("insert into gardoc_jobs(id_archiviazione, syscon, codgara, data_inserimento, da_processare, tipo_archiviazione, entita) "
              + "values(?,?,?,?,'1',?,?)", new Object[]{id_archiviazione, syscon, codgar, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),new Long(1), entita});

	  } catch (Exception e) {
		  logger.error("creazioneArchivioDocumentiGara.insertJob : errore durante l'inserimento del job di esportazione documenti per la gara " + codgar, e);
		  throw e;
	  }
	  return id_archiviazione;
  }

}
