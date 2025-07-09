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
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import it.eldasoft.sil.pg.bl.siem.AbstractSiemManager;
import it.eldasoft.sil.pg.bl.siem.SiemException;
import it.eldasoft.sil.pg.bl.siem.SiemManagerResolver;
import it.eldasoft.sil.pg.bl.InviaBandoAvvisoSimapManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import it.appaltiecontratti.agid.consnorma.datatypes.ModalitaDiFormazioneType;
import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import net.sf.json.JSONObject;

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

	public String getQueryDocGara() {
		return "select NGARA, GRUPPO, DESCRIZIONE, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, isarchi as ARCHIVIATO, " + this.sqlManager.getDBFunction("datetimetostring",  new String[] {"DATARILASCIO"}) + " ," +
				"       w.DIGDESDOC, w.firmacheck "+
				"from documgara d join w_docdig w on d.iddocdg=w.iddocdig and d.idprg=w.idprg and digent='DOCUMGARA' " +
				"where d.codgar = ? and d.idprg is not null and d.iddocdg is not null order by gruppo, numord, norddocg";
	}
	public String getQueryDocComunicazioni() {
		return "select i.comkey1, i.commsgogg, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, case when COMSTATO='12' then '1' else cast(null as varchar(2))end as ARCHIVIATO, " + this.sqlManager.getDBFunction("datetimetostring",  new String[] {"i.COMDATAPUB"}) + ", w.DIGDESDOC, w.firmacheck " +
				"from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + "=w.digkey2 and i.idprg=w.digkey1 and w.digent='W_INVCOM' join gare g on i.comkey1=g.ngara " +
				"where g.codgar1 = ? and i.compub = '1' and (i.comstato='3' or i.comstato='12') order by i.idcom,w.iddocdig";
	}
	public String getQueryDocRiepilogoBuste() {
		return "select distinct CASE WHEN COMTIPO='FS10' THEN 'Riepilogo busta domanda di partecipazione' WHEN COMTIPO='FS11' THEN 'Riepilogo busta offerta' ELSE 'Notifica invio rinuncia offerta' END as GRUPPO, comkey2 as LOTTO,"+
				" CASE WHEN COMTIPO='FS14' THEN comkey1 ELSE dittao END as DITTA,  W.DIGDESDOC as DESCRIZIONE, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO,"+
				" CASE WHEN COMTIPO='FS10' THEN "+this.sqlManager.getDBFunction("datetimetostring",  new String[] {"DRICIND"})+" WHEN COMTIPO='FS11' THEN "+this.sqlManager.getDBFunction("datetimetostring",  new String[] {"DATOFF"})+" ELSE CAST(null as VARCHAR(10)) END AS DATA,  CASE WHEN COMTIPO='FS10' THEN ORADOM WHEN COMTIPO='FS11' THEN ORAOFF ELSE CAST(NULL as VARCHAR(8)) END AS ORA, "+
				" CASE WHEN COMTIPO='FS14' THEN "+this.sqlManager.getDBFunction("datetimetostring",  new String[] {"DATRINUNCIA"}) + " ELSE CAST(NULL as VARCHAR(30)) END as DATRINUNCIA, i.comnumprot, comtipo "+
				" , w.firmacheck "+
				" from ditg left outer join ragimp on (codime9=dittao and impman='1')"+
				" inner join w_puser on (userkey1=dittao or (userkey1=coddic and impman='1'))"+
				" inner join w_invcom i on (comkey1=usernome and comkey2=ngara5 and (comkey3 = ncomope or comkey3 is null))"+
				" join w_docdig w on (CAST(i.idcom as varchar(12)) = w.digkey1 and w.digent='W_INVCOM') "+
				" where ((COMTIPO in('FS10','FS11') and upper(DIGNOMDOC) LIKE 'RIEPILOGO_BUSTE.PDF%') or COMTIPO='FS14') and COMSTATO='6' and ditg.codgar5 = ? "+
				" and (CASE WHEN COMTIPO='FS10' THEN DRICIND WHEN COMTIPO='FS11' THEN DATOFF ELSE DATRINUNCIA END is not null) order by comkey2, comtipo, ditta";
	}
	public String getQueryDocAssociatiDB() {
		return "select c.c0akey1, c.c0atit, w.idprg, w.iddocdig, c.c0anomogg, coalesce("+this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) + ","
				+this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) + ","
				+ this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"})
				+ ") "
				+" , w.firmacheck "
				+" from c0oggass c join gare g on c.c0akey1=g.ngara join w_docdig w on cast(c.c0acod as varchar(10))=w.digkey1 and c.c0aprg=w.idprg and w.digent='C0OGGASS' where g.codgar1 = ? and c.c0aent in ('GARE','GCAP','GOEV','GARSED','GARECONT','GAREAVVISI')"
				+ " union select c.c0akey1, c.c0atit, w.idprg, w.iddocdig, c.c0anomogg, coalesce("
				+ this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) + ","
				+ this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) + ","
				+ this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"})
				+") "
				+" , w.firmacheck "
				+" from c0oggass c join torn t on c.c0akey1=t.codgar join w_docdig w on cast(c.c0acod as varchar(10))=w.digkey1 and c.c0aprg=w.idprg and w.digent='C0OGGASS' where t.codgar = ? and c.c0aent = 'TORN' ";
	}
	public String getQueryDocAssociatiFS() {
		return "select c.c0akey1, c.c0atit, c.c0aprg, c.c0acod, c.c0anomogg, coalesce("
				+ this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) + ","
				+ this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) + ","
				+ this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"})
				+ ") "
				+" , 'X' as firmacheck "
				+ "from c0oggass c join gare g on c.c0akey1=g.ngara where g.codgar1 = ? and c.c0aent in ('GARE','GCAP','GOEV','GARSED','GARECONT','GAREAVVISI')"
				+" union select c.c0akey1, c.c0atit, c.c0aprg, c.c0acod, c.c0anomogg, coalesce("
				+ this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) + ","
				+ this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) + ","
				+ this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"})
				+ ") "
				+" , 'X' as firmacheck "
				+" from c0oggass c join torn t on c.c0akey1=t.codgar where t.codgar = ? and c.c0aent  = 'TORN' ";
	}
	public String getQueryDocDitta() {
		return "select d.bustadesc, d.ngara, d.codimp, d.descrizione, d.IDPRG, d.iddocdg, d.DIGNOMDOC, d.datarilascio, d.orarilascio, d.docannul as  ARCHIVIATO, d.doctel as DOCTEL, i.uuid as UUID, w.firmacheck " +
				"from v_gare_docditta d join w_docdig w on d.iddocdg=w.iddocdig and d.idprg=w.idprg " +
				"join imprdocg i on d.codgar = i.codgar and d.ngara = i.ngara and d.codimp = i.codimp and d.norddoci = i.norddoci and d.proveni = i.proveni " +
				"where d.codgar = ? and d.idprg is not null and d.iddocdg is not null order by d.codgar, d.ngara, d.bustaord, d.codimp, d.norddoci";
	}
	public String getQueryDocComAllaDitta(boolean escludiEML){
		String filtroEML = "(w.digent = 'W_INVCOM' or (w.digent='W_INVCOMDES' and w.digkey3="+this.sqlManager.getDBFunction("inttostr",  new String[] {"id.idcomdes"})+"))";
		if(escludiEML)
			filtroEML = "w.digent = 'W_INVCOM'";

		return "select DISTINCT comkey1, i.commsgogg, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, id.descodsog, " + this.sqlManager.getDBFunction("datetimetostring",  new String[] {"id.DESDATINV"}) + ", w.DIGDESDOC, id.idcom, descodent, i.comnumprot, w.firmacheck  " +
				"from w_invcom i join (select DISTINCT idprg,idcom,descodsog,descodent,DESSTATO,MIN(DESDATINV) as DESDATINV,idcomdes from w_invcomdes group by idprg,idcom,descodsog,descodent,DESSTATO,idcomdes) id on id.idprg = i.idprg and id.idcom = i.idcom " +
				"join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + "=w.digkey2 and w.digkey1=i.idprg and "+filtroEML+" join gare g on i.comkey1=g.ngara " +
				"where g.codgar1 = ? and i.compub <> '1' and (COMTIPO is null or COMTIPO<>'FS12') and (id.DESSTATO = '2' or id.DESSTATO = '4')";
	}
	public String getQueryDocComDallaDittaTorn() {
		return "select comkey1, comkey2, i.commsgogg, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, " + this.sqlManager.getDBFunction("datetimetostring",  new String[] {"i.COMDATINS"}) + ", w.DIGDESDOC, i.comnumprot, w.firmacheck " +
				"from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + "=w.digkey1 and i.idprg=w.idprg and w.digent='W_INVCOM' " +
				"join torn t on (i.comkey2=t.codgar) " +
				"where t.codgar = ? and i.compub <> '1' and COMTIPO='FS12' and COMSTATO='3' and COMENT IS NULL order by comkey1,i.idcom,w.iddocdig";
	}
	public String getQueryDocComDallaDittaGare(){
		return "select comkey1, comkey2, i.commsgogg, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, " + this.sqlManager.getDBFunction("datetimetostring",  new String[] {"i.COMDATINS"}) + ", w.DIGDESDOC, i.comnumprot, w.firmacheck " +
				"from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + "=w.digkey1 and i.idprg=w.idprg and w.digent='W_INVCOM' " +
				"join gare g on i.comkey2=g.ngara " +
				"where g.codgar1 = ? and i.compub <> '1' and COMTIPO='FS12' and COMSTATO='3' and COMENT IS NULL order by comkey1,i.idcom,w.iddocdig";
	}
	public String getQueryDocPubbAtti() {
		return "select d.NGARA, d.dittao as DITTA, w.digdesdoc as DESCRIZIONE, d.IDPRG, d.IDDOCDIG, w.DIGNOMDOC, d.isarchi as ARCHIVIATO, "
				+ this.sqlManager.getDBFunction("datetimetostring",  new String[] {"g1.DPUBATTI"}) + ", w.firmacheck, g1.nproatti "+
				"from g1documatti d join w_docdig w on d.iddocdig=w.iddocdig and d.idprg=w.idprg and digent='G1DOCUMATTI' " +
				"join gare1 g1 on d.ngara=g1.ngara where codgar1=? and g1.dpubatti is not null and d.idprg is not null and d.iddocdig is not null and d.statodoc = 5";
	}

	public String getQueryqueryDocComAllaDittaStipula(boolean escludiEML) {
		String filtroEML = "(w.digent = 'W_INVCOM' or (w.digent='W_INVCOMDES' and w.digkey3="+this.sqlManager.getDBFunction("inttostr",  new String[] {"id.idcomdes"})+"))";
		if(escludiEML)
			filtroEML = "w.digent = 'W_INVCOM'";

		return "select DISTINCT comkey1, i.commsgogg, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, id.descodsog, " + this.sqlManager.getDBFunction("datetimetostring",  new String[] {"id.DESDATINV"}) + ", w.DIGDESDOC, id.idcom, descodent, i.comnumprot, w.firmacheck " +
						"from w_invcom i join g1stipula g on i.comkey1= g.codstipula " +
						"join (select DISTINCT idprg,idcom,descodsog,descodent,DESSTATO,MIN(DESDATINV) as DESDATINV,idcomdes from w_invcomdes group by idprg,idcom,descodsog,descodent,DESSTATO,idcomdes) id on id.idprg = i.idprg and id.idcom = i.idcom " +
						"join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + "=w.digkey2 and w.digkey1=i.idprg and "+filtroEML +
						" where g.id = ? and i.compub <> '1' and (COMTIPO is null or COMTIPO<>'FS12') and (id.DESSTATO = '2' or id.DESSTATO = '4')";

	}

	/**
   *
   * vengono creati gli archivi di tutti i documenti delle gare richieste dagli utenti
 * @throws Exception 
   *
   */
  public void creazioneArchivioDocumentiGara(){
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
        String tipoSiemQuery = "SELECT valore FROM w_config WHERE chiave = 'it.eldasoft.sil.pg.registroDiSistema'";

        String getNgara = "SELECT DISTINCT ngara FROM gare WHERE codgar1 = ?";
        String getJobsSql="select id_archiviazione, syscon, codgara,entita,tipo_archiviazione, esportalog, esportaeml from gardoc_jobs where da_processare = '1' and tipo_archiviazione in (1,5) order by id_archiviazione";
        List<?> jobs = this.sqlManager.getListVector(getJobsSql, null);

        if(jobs!=null && jobs.size()>0){

          ZipOutputStream zipOut = null;
          PrintWriter fileIndice = null;

          String queryDocAssociati = null;
          if (documentiAssociatiDB.equals("1")) {
        	  queryDocAssociati = getQueryDocAssociatiDB();
		  } else {
			  queryDocAssociati = getQueryDocAssociatiFS();
		  }

          //Documenti stipula
          String castDATA="DATE";
          if("POS".equals(this.sqlManager.getTipoDB()))
            castDATA="TIMESTAMP";
          else if("MSQ".equals(this.sqlManager.getTipoDB()))
            castDATA="DATETIME";

          String condizioneAppend = this.sqlManager.getDBFunction("concat",  new String[] {"'Documento contratto - '" , "t.tab1desc" });

          String queryDocStipula = "select cast(null as varchar(20)) as LOTTO, " + condizioneAppend + " as GRUPPO,  " +
              " TITOLO as DESCRIZIONE, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, " +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"CASE WHEN d.visibilita=3 THEN i.comdatins ELSE cast(null as " + castDATA + ") END"})+" as DATA, "+
              "CASE WHEN d.visibilita = 3 THEN i.comnumprot ELSE cast(null as varchar(20)) END AS NUMEROPROTOCOLLO, CASE WHEN d.visibilita = 3 THEN vg.codimp ELSE cast(null as varchar(120)) END as DITTA " +
              " ,w.firmacheck  "+
              "from G1DOCSTIPULA  d join V_GARE_DOCSTIPULA w on w.id=d.id join tab1 t on t.tab1tip=d.visibilita " +
              "join V_GARE_STIPULA vg on w.idstipula=vg.id left join w_invcom i on i.idcom = (select max (idcom) from w_invcom where comkey2=vg.codstipula and COMMSGOGG = 'Conferma invio documenti' group by comkey1,comkey2) "+
              "where d.idstipula = ? and w.idprg is not null and w.IDDOCDIG is not null and t.tab1cod='A1182' order by d.visibilita, numord";

          String queryDocAssociatiStipula = null;
          String sqlSintassi= this.sqlManager.getDBFunction("inttostr",  new String[] {"g.id"});
          if (documentiAssociatiDB.equals("1")) {
            queryDocAssociatiStipula = "select c.c0akey1, c.c0atit, w.idprg, w.iddocdig, c.c0anomogg, coalesce(";
            queryDocAssociatiStipula += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) + ",";
            queryDocAssociatiStipula += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) + ",";
            queryDocAssociatiStipula += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"});
            queryDocAssociatiStipula += ") ";
            queryDocAssociatiStipula += ", w.firmacheck ";                      
            queryDocAssociatiStipula += "from c0oggass c join G1STIPULA g on c.c0akey1=" + sqlSintassi + " join w_docdig w on cast(c.c0acod as varchar(10))=w.digkey1 and c.c0aprg=w.idprg and w.digent='C0OGGASS' where g.id = ? and c.c0aent in ('G1STIPULA','G1DOCSTIPULA')";
          } else {
            queryDocAssociatiStipula = "select c.c0akey1, c.c0atit, c.c0aprg, c.c0acod, c.c0anomogg, coalesce(";
            queryDocAssociatiStipula += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) + ",";
            queryDocAssociatiStipula += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) + ",";
            queryDocAssociatiStipula += this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"});
            queryDocAssociatiStipula += ") ";
            queryDocAssociatiStipula += ",'X' as firmacheck ";                                    		
            queryDocAssociatiStipula +=	"from c0oggass c join G1STIPULA g on c.c0akey1=" + sqlSintassi + " where g.id = ? and c.c0aent in ('G1STIPULA','G1DOCSTIPULA')";
          }

          String queryDocComDallaDittaStipula = "select comkey1, comkey2, i.commsgogg, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, " + this.sqlManager.getDBFunction("datetimetostring",  new String[] {"i.COMDATINS"}) + ", w.DIGDESDOC, i.comnumprot, w.firmacheck  " +
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
        	  Long tipo_archiviazione = SqlManager.getValueFromVectorParam(jobs.get(i), 4).longValue();
            Long esportalog = SqlManager.getValueFromVectorParam(jobs.get(i), 5).longValue();
						String esportaEml = SqlManager.getValueFromVectorParam(jobs.get(i), 6).stringValue();
        	  Long genere = null;
        	  String oggetto = "gara";
        	  String entita=null;
        	  Long idStipula=null;
        	  String codgarApp=null;
        	  
        	  boolean devoGeneraXmlMetaInfo = Long.valueOf(5).equals(tipo_archiviazione);

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
        		DataInfoDocumentiContainer datiDocumenti = new DataInfoDocumentiContainer();
        	    List<?> listaDocumentiGara = null;
                List<?> listaDocumentiComunicazioni = null;
                List<?> listaDocumentiRiepilogoBuste = null;
                List<?> listaDocumentiDitta = null;
                List<?> listaDocumentiAssociati = null;
                List<?> listaDocumentiComAllaDitta = null;
                List<?> listaDocumentiComDallaDitta = null;

                List<?> listaDocumentiStipula = null;
				List<?> listaDocumentiPubAtti = null;
                
                boolean isStipula = "G1STIPULA".equals(entitaGardoc);

        	    if (isStipula) {
        	      listaDocumentiStipula = this.sqlManager.getListVector(queryDocStipula, new Object[]{idStipula});
                  listaDocumentiAssociati = this.sqlManager.getListVector(queryDocAssociatiStipula, new Object[]{idStipula});
                  listaDocumentiComAllaDitta = this.sqlManager.getListVector(getQueryqueryDocComAllaDittaStipula(!"1".equals(esportaEml)), new Object[]{idStipula});
                  listaDocumentiComDallaDitta = this.sqlManager.getListVector(queryDocComDallaDittaStipula, new Object[]{idStipula});
        	    }else {
        		  listaDocumentiGara = this.sqlManager.getListVector(getQueryDocGara(), new Object[]{codgar});
        		  listaDocumentiComunicazioni = this.sqlManager.getListVector(getQueryDocComunicazioni(), new Object[]{codgar});
        		  listaDocumentiRiepilogoBuste = this.sqlManager.getListVector(getQueryDocRiepilogoBuste(), new Object[]{codgar});
        		  listaDocumentiDitta = this.sqlManager.getListVector(getQueryDocDitta(), new Object[]{codgar});
        		  listaDocumentiAssociati = this.sqlManager.getListVector(queryDocAssociati, new Object[]{codgar, codgar});
        		  listaDocumentiComAllaDitta = this.sqlManager.getListVector(getQueryDocComAllaDitta(!"1".equals(esportaEml)), new Object[]{codgar});
        		  listaDocumentiComDallaDitta = null;
        		  if(new Long(1).equals(genere)){
        		    listaDocumentiComDallaDitta = this.sqlManager.getListVector(getQueryDocComDallaDittaTorn(), new Object[]{codgar});
        		  }else{
        		    listaDocumentiComDallaDitta = this.sqlManager.getListVector(getQueryDocComDallaDittaGare(), new Object[]{codgar});
        		  }
					listaDocumentiPubAtti = this.sqlManager.getListVector(getQueryDocPubbAtti(), new Object[]{codgar});
        	    }

            	  if((listaDocumentiGara!=null && listaDocumentiGara.size()>0) ||
            			  (listaDocumentiComDallaDitta!=null && listaDocumentiComDallaDitta.size()>0) ||
            			  (listaDocumentiComAllaDitta!=null && listaDocumentiComAllaDitta.size()>0) ||
            			  (listaDocumentiAssociati!=null && listaDocumentiAssociati.size()>0) ||
            			  (listaDocumentiRiepilogoBuste!=null && listaDocumentiRiepilogoBuste.size()>0) ||
            			  (listaDocumentiDitta!=null && listaDocumentiDitta.size()>0) ||
            			  (listaDocumentiComunicazioni!=null && listaDocumentiComunicazioni.size()>0) ||
            			  (listaDocumentiStipula!=null && listaDocumentiStipula.size()>0) ||
						  (listaDocumentiPubAtti!=null && !listaDocumentiPubAtti.isEmpty())){
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
            			  fileIndiceRow.append("N.protocollo;");
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
                				  CampiDocumento campi = popolaCampiDocumento(listaDocumentiGara, j, "1", oggetto);
                				  this.addFileToArchive(codgar, entitaGardoc, pathArchivioDocumenti, campi.getHashCampi(), zipOut, fileIndice);
                				  
                				  if(devoGeneraXmlMetaInfo) {
                					  datiDocumenti.add("U", "listaDocumentiGara", campi);                				  
                				  }
                				  
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
                            	CampiDocumento campi = new CampiDocumento();

                                campi.put("argomento", "Documentazione " + oggetto);
                                campi.put("gruppo", SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j), 1).getStringValue());
                                campi.put("lotto", SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j), 0).getStringValue());
                                this.getDitta(SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j),9).getStringValue(), campi.getHashCampi(), false,"IMPR");
                                campi.put("descrizione", SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j), 2).getStringValue());
                                campi.put("idprg", SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j), 3).getStringValue());
                                campi.put("iddocdig", SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j), 4).longValue());
                                campi.put("dignomdoc", SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j), 5).getStringValue());
                                campi.put("data", SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j), 7).getStringValue());
                                campi.put("archiviato", SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j), 6).getStringValue().equals("1")?"si":"");
                                campi.put("protocollo", SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j), 8).getStringValue());

                                this.addFileToArchive(codgar, entitaGardoc, pathArchivioDocumenti, campi.getHashCampi(), zipOut, fileIndice);
                            
                                String gruppo = (String) campi.get("gruppo");
                                String tipologiaDiGrupppo = "Documento contratto - Interno".equalsIgnoreCase(gruppo)?"I":"U";
                                
                                campi.put("firmacheck", SqlManager.getValueFromVectorParam(listaDocumentiStipula.get(j), 10).getStringValue());
                                
                                if(devoGeneraXmlMetaInfo) {
                                	datiDocumenti.add(tipologiaDiGrupppo, "listaDocumentiStipula", campi);
                                }
                                
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
								  CampiDocumento campi = popolaCampiDocumento(listaDocumentiComunicazioni, j, "2", oggetto);
                				  this.addFileToArchive(codgar, entitaGardoc, pathArchivioDocumenti, campi.getHashCampi(), zipOut, fileIndice);
                				  
                				  if(devoGeneraXmlMetaInfo) {
                					  datiDocumenti.add("U", "listaDocumentiComunicazioni", campi);
                				  }

                				  
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
                				  CampiDocumento campi = popolaCampiDocumento(listaDocumentiAssociati, j, "4", oggetto);

                				  if (documentiAssociatiDB.equals("1")) {
                					  this.addFileToArchive(codgar, entitaGardoc, pathArchivioDocumenti, campi.getHashCampi(), zipOut, fileIndice);                					                  					  
                				  } else {
														campi.put("documentiAssociatiFS","1");
                					  this.addFileToArchive(codgar, entitaGardoc, pathArchivioDocumenti, campi.getHashCampi(), zipOut, fileIndice, pathDocumentiAssociati);                					                 					  
                				  }  
                				  
                				  if(devoGeneraXmlMetaInfo) {
                					  datiDocumenti.add("I", "listaDocumentiAssociati", campi); 
                				  }
                				  
                			  } catch(Exception e) {
                				  logger.error("creazioneArchivioDocumentiGara : errore durante il recupero di un file Documenti associati", e);
                				  messaggioErrore += "Errore documenti associati: " + e.getMessage() + "\r\n";
                				  numErrori++;
                			  }
                		  }
                      }

                      //Documenti riepilogo buste
                      if(listaDocumentiRiepilogoBuste!=null) {
                          String dataApp;
                          for(int j=0; j<listaDocumentiRiepilogoBuste.size();j++) {
                              try {
                            	  CampiDocumento campi = popolaCampiDocumento(listaDocumentiRiepilogoBuste, j, "8", oggetto);
                                  
                                  this.addFileToArchive(codgar, entitaGardoc, pathArchivioDocumenti, campi.getHashCampi(), zipOut, fileIndice);
                                  
                                  if (devoGeneraXmlMetaInfo) {
                                	  datiDocumenti.add("E", "listaDocumentiRiepilogoBuste", campi);  
                                  }
                                  
                                  
                              } catch(Exception e) {
                                  logger.error("creazioneArchivioDocumentiGara : errore durante il recupero di un file Documenti riepilogo buste", e);
                                  messaggioErrore += "Errore documentazione delle ditte: " + e.getMessage() + "\r\n";
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
                          String selecStatoComunicazione = "select i.comstato from w_invcom i,w_docdig d where d.idprg=? and d.digkey3=? and i.idprg=d.idprg and CAST(i.idcom as VARCHAR(12))=d.digkey1";
                		  for(int j=0; j<listaDocumentiDitta.size();j++) {
                			  try {
                				  idprg = SqlManager.getValueFromVectorParam(listaDocumentiDitta.get(j), 4).getStringValue();
                				  doctel = SqlManager.getValueFromVectorParam(listaDocumentiDitta.get(j), 10).getStringValue();
                				  uuid = SqlManager.getValueFromVectorParam(listaDocumentiDitta.get(j), 11).getStringValue();
                				  if("1".equals(doctel) && uuid!=null && !"".equals(uuid)) {
                	                 String comstato = (String)this.sqlManager.getObject(selecStatoComunicazione, new Object[]{idprg,uuid});
                	                 if("13".equals(comstato) || "20".equals(comstato))
                	                   continue;
                	              }

								  CampiDocumento campi = popolaCampiDocumento(listaDocumentiDitta, j, "3",oggetto);
                				  
                				  this.addFileToArchive(codgar, entitaGardoc, pathArchivioDocumenti, campi.getHashCampi(), zipOut, fileIndice);
                				                  				  
                				  if (devoGeneraXmlMetaInfo) {
                					  datiDocumenti.add("E", "listaDocumentiDitta", campi);
                				  }
                				  
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
                				  CampiDocumento campi = popolaCampiDocumento(listaDocumentiComAllaDitta,j,"5",oggetto);
                				  
                				  this.addFileToArchive(codgar, entitaGardoc, pathArchivioDocumenti, campi.getHashCampi(), zipOut, fileIndice);

                				  String idprg = UtilityStringhe.convertiNullInStringaVuota( (String) campi.get("idprg"));
                				  String dignomdoc = UtilityStringhe.convertiNullInStringaVuota( (String) campi.get("dignomdoc"));
                				  Long iddocdig =  (Long) campi.get("iddocdig");
                				                 				  
                				  String documento = idprg + iddocdig + "_" + dignomdoc;
                				  String destinatario = (String)campi.get("descodsog");
                				  
                				  if (devoGeneraXmlMetaInfo) {
                					  datiDocumenti.addAggregato("U", "listaDocumentiComAllaDitta", documento, destinatario, campi); 
                				  }
                				  
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
                				  CampiDocumento campi = popolaCampiDocumento(listaDocumentiComDallaDitta,j,"6",oggetto);

                				  this.addFileToArchive(codgar, entitaGardoc, pathArchivioDocumenti, campi.getHashCampi(), zipOut, fileIndice);
                				  
                				  String idprg = UtilityStringhe.convertiNullInStringaVuota( (String) campi.get("idprg"));
                				  String idcom = UtilityStringhe.convertiNullInStringaVuota( (String) campi.get("idcom"));
                				  String dignomdoc = UtilityStringhe.convertiNullInStringaVuota( (String) campi.get("dignomdoc"));
                				  Long iddocdig =  (Long) campi.get("iddocdig");
                				  
                				  //String documento = idprg+idcom+dignomdoc;
                				  String documento = idprg + iddocdig + "_" + dignomdoc;
                				  String destinatario = (String)campi.get("descodsog");
                				  
                				  if (devoGeneraXmlMetaInfo) {
                					  datiDocumenti.addAggregato("E", "listaDocumentiComDallaDitta", documento, destinatario, campi);                 				  
                				  }
                				  
                			  } catch(Exception e) {
                				  logger.error("creazioneArchivioDocumentiGara : errore durante il recupero di un file Documenti inviato dalla ditta", e);
                				  messaggioErrore += "Errore documenti inviati dalla ditta: " + e.getMessage() + "\r\n";
                				  numErrori++;
                			  }
                		  }
                      }

					  //Documenti pubblicati art 36
					  if(listaDocumentiPubAtti!=null) {
						  for(int j=0; j<listaDocumentiPubAtti.size();j++) {
							  try {
								  CampiDocumento campi = popolaCampiDocumento(listaDocumentiPubAtti, j, "9", oggetto);

								  campi.put("protocollo", "");

								  this.addFileToArchive(codgar, entitaGardoc, pathArchivioDocumenti, campi.getHashCampi(), zipOut, fileIndice);

								  if (devoGeneraXmlMetaInfo) {
									  datiDocumenti.add("U", "listaDocumentiPubAtti", campi);
								  }

							  } catch(Exception e) {
								  logger.error("creazioneArchivioDocumentiGara : errore durante il recupero di un file Documenti pubblicazione atti e offerte", e);
								  messaggioErrore += "Errore documenti pubblicazione atti e offerte: " + e.getMessage() + "\r\n";
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
                      
                      List<Map<String,Object>> listaInfoDocXmlGenerati = new ArrayList();
                      if (devoGeneraXmlMetaInfo) {
                    	  NominativiPerMetaXml nominativi = new NominativiPerMetaXml(sqlManager);                    	  
	                      listaInfoDocXmlGenerati = new ArrayList<Map<String,Object>>();
                                            
	                      // costruisco gli xml delle liste che hanno dati NON aggregati
	                      for(String qualeLista: datiDocumenti.getChiaviListeDocumenti()) {
		                      List<CampiDocumento> listaCampi = datiDocumenti.get(qualeLista);
		                      for (CampiDocumento campi : listaCampi) {  
		                    	  Map<String, String> fileInfo;
								  try {
								      String versoLista = (String) campi.get("versoLista");
									  Map<String,Object> result = generaDocumentazioneXml(isStipula, qualeLista, datiDocumenti, versoLista, pathArchivioDocumenti, codgarMod, campi.getHashCampi(),"", nominativi);
									  Map<String,Object> infoNodoIdDoc = (Map<String,Object>) result.get("infoNodoIdDoc");
									  listaInfoDocXmlGenerati.add(infoNodoIdDoc);
									  fileInfo = (Map<String, String>) result.get("fileInfo");								  
		                    		  addXmlToArchive(fileInfo, zipOut);
		                    	  } catch(Exception e){
		                    		  logger.error("creazioneArchivioDocumentiGara : errore durante la generazione del file xml relativo all lista "+qualeLista, e);
	                				  messaggioErrore += "Errore nella generazione del file xml " + e.getMessage() + "\r\n";
	                				  numErrori++;
		                    	  }//try
							  }//for 
	                      }//for
	                      
	                      // costruisco gli xml delle liste che hanno dati aggregati
	                      for(String qualeLista: datiDocumenti.getChiaviListeDatiAggregati()){
	                    	  for (String documento: datiDocumenti.getChiaviDocumentiDatiAggregati(qualeLista)) {
	                    	  
	                    		  // Nota: per uno stesso documento prima venivano generate più righe una per ogni destinatario;
	                    		  // le righe differivano solo per le info del destinatario, tutte le altre informazioni
	                    		  // sono uguali, quindi considero il record campi del primo destinatario per avere le info di cui ho 
	                    		  // bisogno ( le info sui destinatari verranno poi ricavati internamente )
	                    		  Set<String> elencoDestinatari = datiDocumenti.getElencoDestinatari(qualeLista, documento);                    		  
	                    		  Iterator<String> iterator = elencoDestinatari.iterator();
	                    		  String destinatario = iterator.next();
			                      List<CampiDocumento> listaCampi =  datiDocumenti.getElencoCampi(qualeLista, documento, destinatario);
			                      		                      
			                      String versoLista =  (String) listaCampi.get(0).getHashCampi().get("versoLista");   
			                      
		                    	  try{
		                    		  Map<String,Object> result = generaDocumentazioneXml(isStipula, qualeLista, datiDocumenti,versoLista, pathArchivioDocumenti, codgarMod, listaCampi.get(0).getHashCampi(), documento, nominativi);
		                    		  Map<String,Object> infoNodoIdDoc = (Map<String,Object>) result.get("infoNodoIdDoc");
									  listaInfoDocXmlGenerati.add(infoNodoIdDoc);
		                    		  Map<String, String> fileInfo = (Map<String, String>) result.get("fileInfo");	
		                    		  addXmlToArchive(fileInfo, zipOut);
		                    	  } catch(Exception e){
		                    		  logger.error("creazioneArchivioDocumentiGara : errore durante la generazione del file xml relativo all lista "+qualeLista, e);
	                				  messaggioErrore += "Errore nella generazione del file xml " + e.getMessage() + "\r\n";
	                				  numErrori++;
		                    	  }//try	                     
		                      
	                      	  }//for
	                      }//for 
                      
                      }//if (devoGeneraXmlMetaInfo)

                      if (devoGeneraXmlMetaInfo) {
                    	  Map<String,Object> result =  generaAggregatoXml(pathArchivioDocumenti, isStipula, codgar, listaInfoDocXmlGenerati, genere, codgarMod, nomeFile);
                    	  Map<String, String> fileInfo = (Map<String, String>) result.get("fileInfo");	
                		  addXmlToArchive(fileInfo, zipOut);
                      }// if 
                      
                      if (esportalog != null && !esportalog.equals(0L)) {
                        try {
							final String tipoSiem = (String)this.sqlManager.getObject(tipoSiemQuery, new Object[] {});
							final SiemManagerResolver resolver = new SiemManagerResolver(tipoSiem);
							final AbstractSiemManager cloudLoggerManager = resolver.get(esportalog);
							List<String> listTarget = new ArrayList<>();
							listTarget.add(codgarApp);
							final List<Vector> targets = this.sqlManager.getListVector(getNgara, new Object[] { codgar });
							if(targets!=null && !targets.isEmpty()) {
								for (Vector target : targets) {
									final String value = SqlManager.getValueFromVectorParam(target, 0).getStringValue();
									if (!listTarget.contains(value))
										listTarget.add(value);
								}
							}

						  	final JSONObject extract = cloudLoggerManager.extract(listTarget);
						  	if (extract != null) {
							  final String filename = String.format("%s/%s/%s_events.json", pathArchivioDocumenti, codgarMod, nomeFile);
							  try (final PrintWriter writer = new PrintWriter(filename, "UTF-8")) {
								  writer.write(extract.toString());
							  }

							  final File file = new File(filename);
							  final byte[] buffer = Files.readAllBytes(file.toPath());

							  zipOut.putNextEntry(new ZipEntry(String.format("%s_events.json",nomeFile)));
							  zipOut.write(buffer, 0, buffer.length);
							  zipOut.flush();
						  }

                        } catch (SiemException e) {
                          logger.error("creazioneArchivioDocumentiGare : errore durante l'estrazione dei log", e);

                          messaggioErrore += "Errore durante l'estrazione dei log: " + e.getMessage();
						  numErrori++;
                        }
                      }

                      zipOut.close();
                      
                      //cancello directory temporanea della gara
                      this.RemoveDirectory(CartellaTemporaneaDocGara); 
                      
                      //scrivo il messaggio di avvenuta creazione dell'archivio all'utente che l'ha richiesto
                      this.updateJob(syscon, codgar, numErrori > 0 ? 1:0, id_archiviazione, messaggioErrore, genere, entitaGardoc);
                      
            	  } else {
            		  //non esistono documenti per la gara
            		  this.updateJob(syscon, codgar, 2, id_archiviazione, messaggioErrore, genere, entitaGardoc);
            	  }//if
            	              	  
        	  } catch (SQLException e) {
              logger.error("creazioneArchivioDocumentiGara : errore durante il recupero dei documenti gara con queryDoc " + codgar, e);
              messaggioErrore += "Errore di estrapolazione dati: " + e.getMessage();
              this.updateJob(syscon, codgar, 3, id_archiviazione, messaggioErrore, genere, entitaGardoc);
    		    }     //try
        	  
        	  
          }//for
        }
      } catch (SQLException | IOException e) {
        logger.error("Errore durante la lettura dei dati degli elenchi/cataloghi da processare per i controlli sui rinnovi in scadenza", e);
      } catch (GestoreException e) {
        logger.error("Errore durante la lettura dei dati degli elenchi/cataloghi da processare per i controlli sui rinnovi in scadenza", e);
      } catch (DataAccessException e) {
        logger.error("Errore durante l'inserimento della comunicazione di richiesta rinnovo", e);
      }
    }

    logger.debug("creazioneArchivioDocumentiGara: fine metodo");
  }

	private Map<String,Object> generaAggregatoXml( 	 String pathArchivioDocumenti, 
													 boolean isStipula, 
													 String codgar,
													 List<Map<String, Object>> listaInfoDocXmlGenerati, 
													 Long genere_,
													 String codgarMod,
													 String nomeFile
												 ) throws SQLException, GestoreException, IOException  {
		
		  AggregazioniDocumentaliInformatiche aggDocInf = new AggregazioniDocumentaliInformatiche();
		  
		  Map <String,String> fileInfo = new HashMap<String,String>();
		  String file = nomeFile+"_fileIndice.xml";
		  String nomeFileConPath = pathArchivioDocumenti + "/" + codgarMod + "/" + file;
		  fileInfo.put("nomeFile", file);
		  fileInfo.put("nomeFileConPath", nomeFileConPath);
		  fileInfo.put("pathFile", pathArchivioDocumenti + "/" + codgarMod + "/");

		  // sezione IdAgg
		  String genere = "";
		  if (genere_ != null) {
			  genere = genere_.toString();
		  }
		  
		  String idAggregazione = getIdAggregazione(isStipula, codgar, genere);
		  aggDocInf.setIdAgg(idAggregazione);
		  
		  // sezione TipologiaFascicolo
		  aggDocInf.setTipologiaFascicoloProcedimentoAmministrativo();
		  
		  // genero la sezione Soggetti
		  aggDocInf.addSoggetto();
		  //  -  AmministrazioneTitolare
		  Map<String,String> infoAmm = getInfoAmm(isStipula, codgar);
		  aggDocInf.addRuoloAmministrazioneTitolare(infoAmm, infoAmm, getInfoAmmIndirizziDigitaliDiRiferimento(infoAmm));
		  //  - RUP
		  aggDocInf.addRuoloRUP(getCognomeRup(infoAmm), getNomeRup(infoAmm), infoAmm, infoAmm, infoAmm, getInfoRupIndirizziDigitaliDiRiferimento(infoAmm));
		  
		  //genero la sezione Assegnazione
		  aggDocInf.addAssegnazione();
		  Date dataAssegnazione = getDataInizioAssegnazione(isStipula, codgar, genere);
		  aggDocInf.assegnazione( infoAmm, infoAmm, getInfoAssegnazioneIndirizziDigitaliDiRiferimento(infoAmm), dataAssegnazione);
		  
		  // sezione dataApertura                      
		  aggDocInf.setDataApertura(dataAssegnazione);
		  
		  // sezione ChiaveDescrittiva
		  Map<String,Object> oggettoChiaveDescrittiva = getOggettoChiaveDescrittiva(codgar, isStipula, genere);
		  aggDocInf.setChiaveDescrittiva(oggettoChiaveDescrittiva, getParoleChiaviChiaveDescrittivaAggregazione(isStipula, codgar, oggettoChiaveDescrittiva,genere));
		  
		  // sezione ProcedimentoAmministrativo
		  aggDocInf.setProcedimentoAmministrativo("Appalti e contratti", oggettoChiaveDescrittiva, InviaBandoAvvisoSimapManager.getUrlDocumentiGara(codgar), dataAssegnazione);
		  	                      
		  // sezione IndiceDocumenti 
		  aggDocInf.setIndiceDocumenti();
		  for (Iterator iterator = listaInfoDocXmlGenerati.iterator(); iterator.hasNext();) {
			Map<String, Object> infoNodoIdDoc = (Map<String, Object>) iterator.next();						
			aggDocInf.setInfoDocumentoInformatico(infoNodoIdDoc);
		  }//for
		  
		  // salvo il file xml relativo all'indice che ho generato in aggDocInf
		  aggDocInf.save(nomeFileConPath);
		  
		  Map<String, Object> result = new HashMap<String,Object>();
		  result.put("fileInfo", fileInfo);
		  
		  return result;
		  
	}//generaAggregatoXml
  
	private String getCognomeRup(Map<String, String> info) {
	   return UtilityStringhe.convertiNullInStringaVuota(info.get("cognome"));
    }

	private String getNomeRup(Map<String, String> info) {
	   return UtilityStringhe.convertiNullInStringaVuota(info.get("nome"));
    }

	public Map<String, String> getInfoAmm(boolean isStipula, String codgar) throws SQLException {
	   String query;
	   if (isStipula) {
		   query =  "  select u.NOMEIN "		// campo  0
			   		+ "     , u.CODIPA "		// campo  1
			   		+ "     , u.EMAI2IN "		// campo  2
			   		+ "     , u.emaiin "		// campo  3
			   		+ "     , tec.COGTEI "		// campo  4
			   		+ "     , tec.nometei "		// campo  5
			   		+ "from torn t "
			   		+ "join v_gare_stipula  gs on gs.codstipula = ? "
			   		+ "                       and gs.codgar = t.codgar  "
			   		+ "left join uffint u  on u.codein  =  t.cenint  "
			   		+ "left join tecni tec on tec.codtec =  t.codrup ";		   
	   } else {
		   query =    "select u.nomein " 		// campo  0
					+ "      ,u.codipa "		// campo  1
					+ "      ,u.emai2in "		// campo  2
					+ "      ,u.emaiin "		// campo  3
					+ "      ,tec.cogtei "		// campo  4
					+ "      ,tec.nometei "		// campo  5
					+ "from torn t "
					+ "left join tecni tec on tec.codtec =  t.codrup "
					+ "left join uffint u  on u.codein   =  t.cenint "
					+ "where t.codgar = ? ";
	   }//if
	   
	   String nomein  = "";
	   String codipa  = "";
	   String emai2in = "";
	   String emaiin  = "";
	   String cogtei  = "";
	   String nometei = "";
	   List<Object> dati = this.sqlManager.getListVector(query, new Object[] {codgar});
	
	   if (dati.size() > 0) {
		   nomein  = SqlManager.getValueFromVectorParam(dati.get(0), 0).getStringValue();
		   codipa  = SqlManager.getValueFromVectorParam(dati.get(0), 1).getStringValue();
		   emai2in = SqlManager.getValueFromVectorParam(dati.get(0), 2).getStringValue();
		   emaiin  = SqlManager.getValueFromVectorParam(dati.get(0), 3).getStringValue();
		   cogtei  = SqlManager.getValueFromVectorParam(dati.get(0), 4).getStringValue();
		   nometei = SqlManager.getValueFromVectorParam(dati.get(0), 5).getStringValue();
	   }// if
		 
	   Map<String, String> risultato = new HashMap<String, String>();
	   risultato.put("denominazione" ,UtilityStringhe.convertiNullInStringaVuota(nomein));
	   risultato.put("codiceIPA"     ,UtilityStringhe.convertiNullInStringaVuota(codipa));
	   risultato.put("indirizzo1"    ,UtilityStringhe.convertiNullInStringaVuota(emai2in));
	   risultato.put("indirizzo2"    ,UtilityStringhe.convertiNullInStringaVuota(emaiin));
	   risultato.put("cognome"       ,UtilityStringhe.convertiNullInStringaVuota(cogtei));
	   risultato.put("nome"          ,UtilityStringhe.convertiNullInStringaVuota(nometei));
	   
	   return risultato;
    }//getInfoAmm

	private Map<String,Object> getOggettoChiaveDescrittiva(String codgar, boolean isStipula, String genere) throws SQLException {	
		
		String oggetto = "";		
		String query   = "";
		List<Object> dati;
		String oggettoDb = "";;
		Map<String,Object> oggettoRisultato = new HashMap<String,Object>();
		oggettoRisultato.put("isCig", false);
		String codgarSenzaS = codgar.replace("$", "");
		
		if (isStipula) {
			query =   "select gs.oggetto "
					+ "from  g1stipula gs "
					+ "where 1=1 "
					+ "  and gs.codstipula = ? ";
			
			dati = this.sqlManager.getListVector(query, new Object[] {codgar});
			if (dati.size() > 0) {
			    oggettoDb = SqlManager.getValueFromVectorParam(dati.get(0), 0).getStringValue();
			    oggettoDb = UtilityStringhe.convertiNullInStringaVuota(oggettoDb);
			    
				oggetto = codgar;
				if (!oggettoDb.isEmpty()) {
					oggetto += "_"+ oggettoDb;
				}//if
				
				oggettoRisultato.put("oggettoSenzaCig", oggetto);
			}//if
			
		} else {
			
			boolean isGenereInElenco= "'10','11','20'".contains("'"+genere+"'");
			
			if (isGenereInElenco) {
				
				switch (genere) {
				    case "20":  // caso catagolo
					case "10":  // caso elenchi 
								query  =  "select ga.oggetto "
										+ "from  garealbo ga "
										+ "where 1=1 "
										+ "  and ga.codgar = ? "; 							
								
								break;
								
					case "11":  // caso avvisi
								query  =  "select ga.oggetto "
										+ "from  gareavvisi ga "
										+ "where 1=1 "
										+ "  and ga.codgar = ? "; 
															
								break;
				}//switch	
				
				dati = this.sqlManager.getListVector(query, new Object[] { codgar });
				
			    oggettoDb = "";
				if (dati.size() > 0) {
				    oggettoDb = SqlManager.getValueFromVectorParam(dati.get(0), 0).getStringValue();
				    oggettoDb = UtilityStringhe.convertiNullInStringaVuota(oggettoDb);
					oggetto = codgarSenzaS;
					if (!oggettoDb.isEmpty()) {
						oggetto += "_" + oggettoDb;
					}//if
										
					oggettoRisultato.put("oggettoSenzaCig", oggetto);
				}// if	
				
			} else {
				// trattiamo il caso gara
				
				query =   "select vgt.oggetto " 	// campo 0
						+ "      ,vgt.codcig "  	// campo 1
						+ "from  v_gare_torn vgt "
						+ "where 1=1  "
						+ "  and vgt.codgar = ? "; 

				dati = this.sqlManager.getListVector(query, new Object[] {codgar});

				oggettoDb = "";
				String codcig = "";
				if (dati.size() > 0) {
					oggettoDb = SqlManager.getValueFromVectorParam(dati.get(0), 0).getStringValue();
					oggettoDb = UtilityStringhe.convertiNullInStringaVuota(oggettoDb);
					codcig    = SqlManager.getValueFromVectorParam(dati.get(0), 1).getStringValue();					
					codcig    = UtilityStringhe.convertiNullInStringaVuota(codcig);
					
					String oggettoSenzaCig  = codgarSenzaS;
					if (!oggettoDb.isEmpty()) {
						oggettoSenzaCig += "_" + oggettoDb;
					}
										
					oggetto =  oggettoSenzaCig;
					if (!codcig.isEmpty()) {
						oggetto +=  "_" + codcig;
					}//if								
					
					oggettoRisultato.put("cig",codcig);
					oggettoRisultato.put("isCig", true);
					oggettoRisultato.put("oggettoSenzaCig", oggettoSenzaCig);											
					
				}//if					
			}//if
		}//if	
		
		oggettoRisultato.put("oggetto", oggetto);
		
		return oggettoRisultato;
		
	}//getOggettoChiaveDescrittiva

	private String[] getParoleChiaviChiaveDescrittivaAggregazione(	boolean isStipula,  
			     													String codgarMod, 
			     													Map<String,Object> oggettoChiaveDescrittiva, 
			     													String genere) throws SQLException {
	
		String oggettoSenzaCig = (String) oggettoChiaveDescrittiva.get("oggettoSenzaCig");
		       oggettoSenzaCig = UtilityStringhe.convertiNullInStringaVuota(oggettoSenzaCig);
		String cig = (String) oggettoChiaveDescrittiva.get("cig");			
			   cig = UtilityStringhe.convertiNullInStringaVuota(cig);
					   
		List<String> parole = new ArrayList<String>();
		if (!oggettoSenzaCig.isEmpty()) {
			parole.add(oggettoSenzaCig);
		}//if
		
		if (!cig.isEmpty()) {
			parole.add(cig);
		}//if
		
		String[] elencoParole = parole.toArray(new String[parole.size()]);
		
		return elencoParole;
		
	}//getParoleChiaviChiaveDescrittivaAggregazione
	
	private String[] getInfoAssegnazioneIndirizziDigitaliDiRiferimento(Map<String,String> info) {			
		return getInfoAmmIndirizziDigitaliDiRiferimento(info);
    }

	private Date getDataInizioAssegnazione(boolean isStipula, String codgar, String genere) throws SQLException, GestoreException {
		Date dataInizioAssgnazione = null;
		
		String query;
		
		if (isStipula) {
		   query =    "select g.daatto "
		   			+ "from torn t "
		   		    + "join V_GARE_STIPULA  gs on gs.codstipula = ? "
		   		    + "                       and gs.codgar = t.codgar "
		   		    + "join  gare g on g.codgar1 = t.codgar ";
		} else {
	       query =    "select vgd.datpub "
					+ "from   v_gare_durata vgd "
					+ "where  1=1 "
					+ "  and  vgd.codgar = ? ";
		}//if
		
		List<Object> dati = this.sqlManager.getListVector(query, new Object[] { codgar });

		if (dati.size() > 0) {
			Date data = SqlManager.getValueFromVectorParam(dati.get(0), 0).dataValue();
			if (data!=null) {
			   dataInizioAssgnazione = data;
			}//if
		}// if	
		
		if (dataInizioAssgnazione==null) {
		
			boolean isGenereInElenco="'10','11','20'".contains("'"+genere+"'");
			if (isGenereInElenco) {
				query =   "select p.datpub "
						+ "from  pubbli p "
						+ "where 1=1 "
						+ "  and p.tippub = 11 "
						+ "  and p.codgar9 = ? ";
				
				dati = this.sqlManager.getListVector(query, new Object[] { codgar });
	
				if (dati.size() > 0) {
					Date data = SqlManager.getValueFromVectorParam(dati.get(0), 0).dataValue();
					if (data!=null) {
						   dataInizioAssgnazione = data;
					}//if				
			    }//if
			}//if		
		}//if
		
		if (dataInizioAssgnazione==null) {
			dataInizioAssgnazione = new Date();
		}//if
		
		return dataInizioAssgnazione;
		
	}//getDataInizioAssegnazione
	
	public String[] getInfoAmmIndirizziDigitaliDiRiferimento(Map<String,String> info) {
		
	   String[] indirizzi = {""};
	   
	   String indirizzo1 = UtilityStringhe.convertiNullInStringaVuota(info.get("indirizzo1"));
	   if (indirizzo1.isEmpty()) {
		   indirizzi[0] = UtilityStringhe.convertiNullInStringaVuota(info.get("indirizzo2"));
	   } else {
		   indirizzi[0] = indirizzo1;
	   }//if
			   
	   return indirizzi;
	}
		
	private String[] getInfoRupIndirizziDigitaliDiRiferimento(Map<String,String> info) {
		   return getInfoAmmIndirizziDigitaliDiRiferimento(info);
	}	

	private String getIdAggregazione(boolean isStipula, String codgar, String genere) throws SQLException {
		String query;
		List<Object> dati;
		String codice = "";
		
		if (isStipula) {
			query =   "select t.idappaltonpa "
					+ "     , t.idpianinpa "
					+ "from torn t "
					+ "join V_GARE_STIPULA  gs on gs.codstipula = ? "
					+ "                       and gs.codgar = t.codgar ";
			dati = this.sqlManager.getListVector(query, new Object[] { codgar });
		} else {
			query = "select idappaltonpa, idpianinpa from torn where codgar=? ";
			dati = this.sqlManager.getListVector(query, new Object[] { codgar }); 
		}//if
						
		String idappaltonpa = ""; 
		String idpianinpa = "";
			
		if (dati.size() > 0) {
		    idappaltonpa = SqlManager.getValueFromVectorParam(dati.get(0), 0).getStringValue();
			idpianinpa = SqlManager.getValueFromVectorParam(dati.get(0), 1).getStringValue();
		    
			if (!genere.isEmpty() && ("'10','11','20'".contains("'"+genere+"'")))
			  codice = idpianinpa;
			else
			  codice = idappaltonpa;
		}//if
		
		// se assenti, utilizzare TORN.CODGAR senza ‘$’
		if (codice.isEmpty()) {
			codice = codgar.replace("$", "");
		}//if		
		
		return codice;
		
	}//getDbIdAggregazione


  private Map<String, Object> generaDocumentazioneXml(boolean isStipula, String listaInElaborazione, DataInfoDocumentiContainer datiDocumenti, String tipologiaDelFlusso, String pathArchivioDocumenti, String codgarMod, HashMap<String, Object> campi, String documento, NominativiPerMetaXml nominativi) throws Exception {
      			  			  
		  String idprg = UtilityStringhe.convertiNullInStringaVuota( (String) campi.get("idprg") ); 
		  Long iddocdig = (Long) campi.get("iddocdig");		 
		  
		  DocumentoInformatico doc = new DocumentoInformatico();

			String dignomdoc = UtilityStringhe.convertiNullInStringaVuota( (String) campi.get("dignomdoc") );
			String identificativo = dignomdoc;
			byte[] impronta = null;
			if("1".equals(campi.get("documentiAssociatiFS"))){
				String pathDocumentiAssociati = ConfigManager.getValore("it.eldasoft.documentiAssociati");
				File fileAssociato = new File(pathDocumentiAssociati + "/" + dignomdoc);
				impronta = getImpronta(FileUtils.readFileToByteArray(fileAssociato));
			}else{
				identificativo = idprg + iddocdig + "_" + dignomdoc;
				BlobFile bf = fileAllegatoManager.getFileAllegato(idprg, iddocdig);
				impronta = getImpronta(bf.getStream());
			}
		  // sezione IdDoc
		  doc.setIdDoc(identificativo, impronta, "SHA-256");
		  
		  // nome del file
		  String nomeFile = identificativo + ".xml";
		  String nomeFileConPath = pathArchivioDocumenti + "/" + codgarMod + "/" + nomeFile;
		  
		  Map <String,String> fileInfo = new HashMap<String,String>();
		  fileInfo.put("nomeFile", nomeFile);
		  fileInfo.put("nomeFileConPath", nomeFileConPath);
		  fileInfo.put("pathFile", pathArchivioDocumenti + "/" + codgarMod + "/");			  
		  
		  // sezione ModalitaDiFormazione
		  doc.setModalitaDiFormazione(ModalitaDiFormazioneType.CREAZIONE_TRAMITE_UTILIZZO_DI_STRUMENTI_SOFTWARE_CHE_ASSICURINO_LA_PRODUZIONE_DI_DOCUMENTI_NEI_FORMATI_PREVISTI_IN_ALLEGATO_2);			  
		  			  
		  // sezione Tipologia documentale
		  String gruppo = (String) campi.get("gruppo");
		  doc.setTipologiaDocumentale(gruppo);
		  
		  // sezione Dati di Registazione 		  
		  String dataStr = (String) campi.get("data");
		  Calendar data = Calendar.getInstance();
		  if (dataStr.isEmpty()) {
			  data.setTimeInMillis( System.currentTimeMillis() );
		  } else {
			  data.setTime(UtilityDate.convertiData(dataStr, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS));
		  }//if
		  			 
		  String protocollo = (String)campi.get("protocollo");
		  if(StringUtils.isBlank(protocollo))
		    protocollo = idprg + iddocdig;		  
		  
		  // sezione DatiDiRegistrazione
		  doc.setDatiDiRegistrazione(tipologiaDelFlusso, "Nessuno", data, protocollo);
		  
		  // sezione Soggetti
	      doc.addSoggetto();
	  
		  // viene popolato il MITTENTE
	      Map<String,String> infoNominativo = null;
	      String query ="";
	      switch(tipologiaDelFlusso) {			 
	          case "E":				  
						  String cfDitta = UtilityStringhe.convertiNullInStringaVuota( (String) campi.get("cfDitta") );
						  String[] dati = getDatiImpr(cfDitta);
						  if (dati[0]!=null) {
							  if ("6".equals(dati[0])){
								  doc.addRuoloMittentePF(dati[2], dati[3], dati[1]);
							  } else {
								  doc.addRuoloMittentePG(dati[4], dati[1]);
							  }//if
						  }//if
						  
					  	  break;
			
	          case "U":
	          case "I":   infoNominativo = nominativi.getNominativo(codgarMod, isStipula);
	        	          if (infoNominativo!=null) {
	        	        	  String NOMEIN = infoNominativo.get("nomein");
	        	        	  String CFEIN  = infoNominativo.get("cfein");
	        	        	  
	        	        	  doc.addRuoloMittentePG(NOMEIN, CFEIN);
	        	          }//if
	
						  break;
	      }//switch	
	      
	      // sezione SoggettoCheEffettuaLaRegistrazione            
          infoNominativo = nominativi.getNominativo(codgarMod, isStipula);
          if (infoNominativo!=null) {
        	  String NOMEIN = infoNominativo.get("nomein");
        	  String CFEIN  = infoNominativo.get("cfein");
        	  
        	  doc.addSoggettoCheEffettuaLaRegistrazione(NOMEIN, CFEIN);	
          }//if
	                   
		  // Destinatario
		  // listaDocumentiComAllaDitta
		  // listaDocumentiComDallaDitta 
		  if (isListaConDatiAggregati(listaInElaborazione)){

			  for(String destinatario: datiDocumenti.getChiaviDestinatariDatiAggregati(listaInElaborazione,documento)) {
				  List<CampiDocumento> listaCampi = datiDocumenti.getElencoCampi(listaInElaborazione, documento, destinatario);
				  for(CampiDocumento campi_ :  listaCampi) {						  					  
					  String cfDitta = UtilityStringhe.convertiNullInStringaVuota( (String) campi_.get("cfDitta") );
					  if (!cfDitta.isEmpty()) {
						  switch (listaInElaborazione) {
						   	case "listaDocumentiComAllaDitta":
								   if("TECNI".equals(campi_.get("entita"))){
									   Vector datiTecnico = sqlManager.getVector("select cogtei,nometei from tecni where codtec = ?", new Object[] {campi_.get("descodsog")});
									   if(datiTecnico!=null && !datiTecnico.isEmpty())
									   	doc.addRuoloDestinatarioPF(SqlManager.getValueFromVectorParam(datiTecnico,0).getStringValue(),SqlManager.getValueFromVectorParam(datiTecnico,1).getStringValue(), cfDitta);
								   }else {
									   String[] dati = getDatiImpr(cfDitta);
									   if ("6".equals(dati[0])) {
										   doc.addRuoloDestinatarioPF(dati[2], dati[3], dati[1]);
									   } else {
										   doc.addRuoloDestinatarioPG(dati[4], dati[1]);
									   }//if
								   }
						   		  break;
						   		  
						   	case "listaDocumentiComDallaDitta": 						   		
								  
						          infoNominativo = nominativi.getNominativo(codgarMod, isStipula);
						          if (infoNominativo!=null) {
						        	  String NOMEIN = infoNominativo.get("nomein");
						        	  String CFEIN  = infoNominativo.get("cfein");
						        	  
						        	  doc.addRuoloDestinatarioPG(NOMEIN, CFEIN); 
						          }//if
						          
						   		  break;
						   		  
						  }//switch
					  }//if
				  }//for
			  }//for
			  
		  }//if
		  			  
		  // sezione Chiave descrittiva
		  String descrizione = (String) campi.get("descrizione");
		  String oggetto = UtilityStringhe.convertiNullInStringaVuota(descrizione);			  			  
		  doc.setChiaveDescrittiva(oggetto, getParoleDescrittiveChiave(gruppo) );
		  
		  // sezione Allegati			  
		  doc.setAllegati(0);			  
		 
		  // sezione Classificazione => dal documento vedo che non e' richiesto
		  			  
		  // sezione Riservato
		  boolean isDocumentoRiservato = true; 			  										
		  doc.setRiservato(isDocumentoRiservato);
		  
		  // Sezione Identificativo del formato	
		  Map<String,Object> infoFile = getEstensionePerIdentificativoFormato(dignomdoc);
		  String identificativoDelFormato = (String) infoFile.get("estensione");	
		  doc.setIdentificativoDelFormato(identificativoDelFormato);
		  
		  // sezione Verifica
		  boolean contieneTSD = (boolean) infoFile.get("contieneTSD");
		  boolean contieneP7M = (boolean) infoFile.get("contieneP7M");
		  String FIRMACHECK = (String) campi.get("firmacheck");
		  boolean isVerificaFirma = "1".equals(FIRMACHECK); // 
		  boolean isFirmatoDigitalmente = isVerificaFirma || contieneP7M;
		  boolean isMarcaturaTemporale  = contieneTSD;
		  
		  doc.setVerifica(isFirmatoDigitalmente, false, isMarcaturaTemporale);
		  
		  // sezione DocumentoPrimario => nella specifica c'e' scritto di omettere
		  
		  // sezione nome del documento			  
		  doc.setNomeDelDocumento(dignomdoc);
		  
		  // sezione versione del documento
		  doc.setVersioneDelDocumento("1"); // impostato come da specifica 
		  
		  // sezione Tracciature modifiche documento => nelle specifiche c'e' scritto di omettere
		  
		  // sezione Tracciature modifiche documento => nelle specifiche c'e' scritot che non e' necessario
		  
		  // sezione Note => nelle specifiche c'e' scritot che non e' necessario
		  
		  // generazione del file .xml
		  doc.save(nomeFileConPath);
			  			 
	      Map<String, Object> result = new HashMap<String,Object>();
	      result.put("fileInfo", fileInfo);
	      result.put("infoNodoIdDoc", doc.getInfoIdDocPerAggregazioneDocumentoInformatico());
	    
	      return result;
      
  }//generaDocumentazioneXml

	/**
		0: tipimp, 1: cfimp, 2: cognome, 3: nome, 4: nomest
	 */
	public String[] getDatiImpr(String cfDitta) throws SQLException {
		String query = "select i.TIPIMP, i.CFIMP, i.COGNOME, i.NOME, i.NOMEST from IMPR i where i.CFIMP = ?";
		List datiImpr = this.sqlManager.getListVector(query, new Object[]{cfDitta});

		String[] dati = new String[5];

		if (!datiImpr.isEmpty()) {
			dati[0] = SqlManager.getValueFromVectorParam(datiImpr.get(0), 0).getStringValue();
			dati[1]  = SqlManager.getValueFromVectorParam(datiImpr.get(0), 1).getStringValue();

			if ("6".equals(dati[0])){
				dati[2] = SqlManager.getValueFromVectorParam(datiImpr.get(0), 2).getStringValue();
				dati[3]    = SqlManager.getValueFromVectorParam(datiImpr.get(0), 3).getStringValue();
			} else {
				dati[4] = SqlManager.getValueFromVectorParam(datiImpr.get(0), 4).getStringValue();
			}
		}
		return dati;
	}
  
  public Map<String, Object> getEstensionePerIdentificativoFormato(String nomeFile) {
      nomeFile = nomeFile.toUpperCase();

      // Dividi il nome del file per "." per separare le estensioni
      String[] parti = nomeFile.split("\\.");

      // Se il nome del file ha più di una parte
      String estensione = "";
      boolean contieneTSD = false;
      boolean contieneP7M = false;
      if (parti.length > 1) {
          // Controlla le estensioni da destra
          for (int i = parti.length - 1; i >= 0; i--) {
              estensione = parti[i];
              // Se l'estensione è diversa da "tsd" e "p7m", restituisci l'estensione
              boolean estensioneTSD = estensione.toUpperCase().contains("TSD");
              boolean estensioneP7M = estensione.toUpperCase().contains("P7M");
              contieneTSD |= estensioneTSD;
              contieneP7M |= estensioneP7M;
              if (!estensioneTSD && !estensioneP7M) {
                  break;
              }//if
          }//for
      }//if

      // Se non ci sono altre estensioni, restituisci la prima estensione trovata a partire da destra
      if (estensione.isEmpty()) {
          estensione = parti[parti.length - 1];
      }//if

      Map<String, Object> risultato = new HashMap<>();
      risultato.put("estensione" , (Object) estensione);
      risultato.put("contieneTSD", (Object) contieneTSD);
      risultato.put("contieneP7M", (Object) contieneP7M);

      return risultato;

  }//getEstensionePerIdentificativoFormato  
  
  private boolean isListaConDatiAggregati(String listaInElaborazione) {
	return     "listaDocumentiComDallaDitta".equals(listaInElaborazione)
			|| "listaDocumentiComAllaDitta".equals(listaInElaborazione);
  }//isListaConDatiAggregati

  private String[] getParoleDescrittiveChiave(String riga) {
      // Divisione della stringa in parole utilizzando uno o più spazi come delimitatori
      String[] parole = riga.split("\\s+");

      // Creazione di un nuovo array con al massimo 5 parole
      int maxLength = Math.min(5, parole.length);
      String[] resultArray = new String[maxLength];

      // Copia delle prime 5 parole nell'array risultante
      System.arraycopy(parole, 0, resultArray, 0, maxLength);

       return resultArray;
       
  }//getParoleDescrittiveChiave
  
  public byte[] getImpronta(byte[] buffer) throws NoSuchAlgorithmException, IOException {
	    int count;
	    MessageDigest digest = MessageDigest.getInstance("SHA-256");

	    java.io.BufferedInputStream bis = new java.io.BufferedInputStream(new java.io.ByteArrayInputStream(buffer));
	    while ((count = bis.read(buffer)) > 0) {
	      digest.update(buffer, 0, count);
	    }
	    bis.close();

	    byte[] hash = digest.digest();
	    
	    return hash;
  }//getImpronta 	  
  
  // ----------------------------------------------------------------
  // ----------------------------------------------------------------

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
				  return "Atto o documento";
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

  private void addXmlToArchive(Map<String,String> infoFile, ZipOutputStream zipOut) throws IOException {
	  String path = infoFile.get("pathFile");
	  String nomeFile = infoFile.get("nomeFile");
	  String fileConPath = infoFile.get("nomeFileConPath");
	  
      File file = new File(fileConPath);
      byte[] fileContent = null;

      try (FileInputStream fis = new FileInputStream(file)) {
          fileContent = new byte[(int) file.length()];
          fis.read(fileContent);
      } catch (IOException e) {
          e.printStackTrace();
          throw e;
      }//try
      
	  try {
		  zipOut.putNextEntry(new ZipEntry(nomeFile));
		  zipOut.write(fileContent, 0,  (int) file.length() );// e se il file è più grande?
	      zipOut.closeEntry();
	      zipOut.flush();
	  } catch (ZipException e) {
		  // il file e' gia' presente nello zip .., lo ignoro
	  }
  
  }//addXmlToArchive

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
	  fileIndiceRow.append("\"").append((String)campi.get("protocollo")).append("\";");
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
		  fileIndiceRow.append("\"").append((String)campi.get("protocollo")).append("\";");
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
		  		message = "La documentazione per " + oggetto + " " + codgarApp + " è stata generata con alcuni errori. Per scaricare l'archivio premi " + link + "qui</a>" ;
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
  public Long insertJob(Long syscon, String codgar, String entita, String includiXmlMetadati) throws Exception{

	Long id_archiviazione = null;
	Long tipo_archiviazione = new Long(1);
	
	if ( "1".equals(includiXmlMetadati) ) {
		tipo_archiviazione = new Long(5);
	}//if
	
    try {
		  id_archiviazione = new Long(genChiaviManager.getNextId("GARDOC_JOBS"));

		  //aggiorno flag di esecuzione nella lista record da elaborare
          this.sqlManager.update("insert into gardoc_jobs(id_archiviazione, syscon, codgara, data_inserimento, da_processare, tipo_archiviazione, entita) "
              + "values(?,?,?,?,'1',?,?)", new Object[]{id_archiviazione, syscon, codgar, new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime()),tipo_archiviazione, entita});

	  } catch (Exception e) {
		  logger.error("creazioneArchivioDocumentiGara.insertJob : errore durante l'inserimento del job di esportazione documenti per la gara " + codgar, e);
		  throw e;
	  }
	  return id_archiviazione;
  }

  public CampiDocumento popolaCampiDocumento(List datiEstratti, int j, String provenienza, String oggetto) throws Exception {
	  CampiDocumento campi = new CampiDocumento();
	  String dataApp = "";
	  switch (provenienza) {
		  case "1":
			  campi.put("argomento", "Documentazione " + oggetto);
			  campi.put("gruppo", this.getGruppo(SqlManager.getValueFromVectorParam(datiEstratti.get(j), 1).getStringValue()));
			  campi.put("lotto", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 0).getStringValue());
			  campi.put("cfDitta", "");
			  campi.put("ditta", "");
			  campi.put("descrizione", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 2).getStringValue());
			  campi.put("idprg", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 3).getStringValue());
			  campi.put("iddocdig", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 4).longValue());
			  campi.put("dignomdoc", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 5).getStringValue());
			  campi.put("data", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 7).getStringValue());
			  campi.put("archiviato", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 6).getStringValue().equals("1")?"si":"");
			  campi.put("protocollo", "");
			  campi.put("digdesdoc", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 8).getStringValue());
			  campi.put("firmacheck", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 9).getStringValue());
			  break;
		  case "2":
			  campi.put("argomento", "Documentazione " + oggetto);
			  campi.put("gruppo", "Comunicazione pubblica");
			  campi.put("lotto", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 0).getStringValue());
			  campi.put("cfDitta", "");
			  campi.put("ditta", "");
			  campi.put("descrizione", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 1).getStringValue() + " - " + SqlManager.getValueFromVectorParam(datiEstratti.get(j), 6).getStringValue());
			  campi.put("idprg", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 2).getStringValue());
			  campi.put("iddocdig", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 3).longValue());
			  campi.put("dignomdoc", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 4).getStringValue());
			  campi.put("archiviato", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 5).getStringValue().equals("1")?"si":"");
			  campi.put("data", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 6).getStringValue());
			  campi.put("protocollo", "");
			  campi.put("firmacheck", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 8).getStringValue());
			  break;
		  case "3":
			  campi.put("argomento", "Documentazione presentata dalle ditte");
			  campi.put("gruppo", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 0).getStringValue());
			  campi.put("lotto", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 1).getStringValue());
			  this.getDitta(SqlManager.getValueFromVectorParam(datiEstratti.get(j), 2).getStringValue(), campi.getHashCampi(), false,"IMPR");
			  campi.put("descrizione", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 3).getStringValue());
			  campi.put("idprg", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 4).getStringValue());
			  campi.put("iddocdig", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 5).longValue());
			  campi.put("dignomdoc", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 6).getStringValue());
			  if (SqlManager.getValueFromVectorParam(datiEstratti.get(j), 7).dataValue() != null) {
				  dataApp = UtilityDate.convertiData(SqlManager.getValueFromVectorParam(datiEstratti.get(j), 7).dataValue(), UtilityDate.FORMATO_GG_MM_AAAA);
				  dataApp += " " +  SqlManager.getValueFromVectorParam(datiEstratti.get(j), 8).getStringValue();
			  }
			  campi.put("data", dataApp);
			  campi.put("archiviato", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 9).getStringValue().equals("1")?"si":"");
			  campi.put("protocollo", "");

			  campi.put("firmacheck", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 12).getStringValue());
			  break;
		  case "4":
		  case "7":
			  campi.put("argomento", "Documentazione " + oggetto);
			  campi.put("gruppo", "Documento associato");
			  campi.put("lotto", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 0).getStringValue());
			  campi.put("cfDitta", "");
			  campi.put("ditta", "");
			  campi.put("descrizione", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 1).getStringValue());
			  campi.put("idprg", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 2).getStringValue());
			  campi.put("iddocdig", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 3).longValue());
			  campi.put("dignomdoc", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 4).getStringValue());
			  campi.put("data", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 5).getStringValue());
			  campi.put("archiviato", "");
			  campi.put("protocollo", "");
			  campi.put("firmacheck", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 6).getStringValue());
			  break;
		  case "5":
			  campi.put("argomento", "Documentazione " + oggetto);
			  campi.put("gruppo", "Documento inviato alla ditta");
			  campi.put("lotto", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 0).getStringValue());
			  String entita= SqlManager.getValueFromVectorParam(datiEstratti.get(j), 9).getStringValue();
			  campi.put("entita",entita);
			  this.getDitta(SqlManager.getValueFromVectorParam(datiEstratti.get(j), 5).getStringValue(), campi.getHashCampi(), false,entita);
			  if (campi.get("ditta").equals("")) {
				  Long idcom = SqlManager.getValueFromVectorParam(datiEstratti.get(j), 8).longValue();
				  String email = (String) this.sqlManager.getObject("select desmail from w_invcomdes where idcom=? and descodent is null and descodsog is null", new Object[]{idcom});
				  campi.put("ditta", email);
			  }
			  campi.put("descrizione", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 1).getStringValue() + " - " + SqlManager.getValueFromVectorParam(datiEstratti.get(j), 7).getStringValue());
			  campi.put("idprg", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 2).getStringValue());
			  campi.put("iddocdig", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 3).longValue());
			  campi.put("dignomdoc", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 4).getStringValue());
			  campi.put("data", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 6).getStringValue());
			  campi.put("archiviato", "");
			  campi.put("protocollo", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 10).getStringValue());
			  campi.put("descodsog", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 5).getStringValue());
			  campi.put("firmacheck", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 11).getStringValue());
			  break;
		  case "6":
			  campi.put("argomento", "Documentazione presentata dalle ditte");
			  campi.put("gruppo", "Documento inviato dalla ditta");
			  campi.put("lotto", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 1).getStringValue());
			  this.getDitta(SqlManager.getValueFromVectorParam(datiEstratti.get(j), 0).getStringValue(), campi.getHashCampi(), true,"IMPR");
			  campi.put("descrizione", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 2).getStringValue()+ " - " + SqlManager.getValueFromVectorParam(datiEstratti.get(j), 7).getStringValue());
			  campi.put("idprg", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 3).getStringValue());
			  campi.put("iddocdig", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 4).longValue());
			  campi.put("dignomdoc", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 5).getStringValue());
			  campi.put("data", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 6).getStringValue());
			  campi.put("archiviato", "");
			  campi.put("protocollo", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 8).getStringValue());
			  campi.put("descodsog", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 5).getStringValue());
			  campi.put("firmacheck", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 9).getStringValue());
			  break;
		  case "8":
			  campi.put("argomento", "Documentazione presentata dalle ditte");
			  campi.put("gruppo", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 0).getStringValue());
			  campi.put("lotto", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 1).getStringValue());
			  this.getDitta(SqlManager.getValueFromVectorParam(datiEstratti.get(j), 2).getStringValue(), campi.getHashCampi(), false,"IMPR");
			  campi.put("descrizione", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 3).getStringValue());
			  campi.put("idprg", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 4).getStringValue());
			  campi.put("iddocdig", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 5).longValue());
			  campi.put("dignomdoc", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 6).getStringValue());
			  String datrinuncia = SqlManager.getValueFromVectorParam(datiEstratti.get(j), 10).stringValue();
			  if (datrinuncia != null) {
				  dataApp = datrinuncia;
			  }else {
				  String ora = SqlManager.getValueFromVectorParam(datiEstratti.get(j), 9).getStringValue();
				  dataApp = SqlManager.getValueFromVectorParam(datiEstratti.get(j), 8).stringValue();
				  dataApp= dataApp.substring(0, 11) + ora;
				  if("".equals(ora))
					  dataApp += " " +  ora;
				  if(ora.length()==5)
					  dataApp += ":00";
			  }
			  campi.put("data", dataApp);
			  campi.put("archiviato", "");
			  campi.put("protocollo", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 11).getStringValue());

			  campi.put("firmacheck", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 13).getStringValue());
			  break;
		  case "9":
			  campi.put("argomento", "Pubblicazione atti e offerte");
			  campi.put("gruppo", "Pubblicazione atti e offerte");
			  campi.put("lotto", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 0).getStringValue());
			  if (StringUtils.isNotEmpty(SqlManager.getValueFromVectorParam(datiEstratti.get(j), 1).getStringValue())) {
				  this.getDitta(SqlManager.getValueFromVectorParam(datiEstratti.get(j), 1).getStringValue(), campi.getHashCampi(), false, "IMPR");
			  } else {
				  campi.put("cfDitta", "");
				  campi.put("ditta", "");
			  }
			  campi.put("descrizione", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 2).getStringValue());
			  campi.put("idprg", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 3).getStringValue());
			  campi.put("iddocdig", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 4).longValue());
			  campi.put("dignomdoc", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 5).getStringValue());
			  campi.put("archiviato", "1".equals(SqlManager.getValueFromVectorParam(datiEstratti.get(j), 6).getStringValue())?"si":"");
			  campi.put("data", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 7).getStringValue());
			  campi.put("firmacheck", SqlManager.getValueFromVectorParam(datiEstratti.get(j), 8).getStringValue());
			  break;
	  }
	  return campi;
  }

}
