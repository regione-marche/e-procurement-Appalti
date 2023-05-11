package it.eldasoft.sil.pg.web.struts;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import net.sf.json.JSONObject;

public class GetListaDocumentiAction extends Action {

  /**
   * Manager per la gestione delle interrogazioni di database.
   */
  private SqlManager sqlManager;

  /**
   * @param sqlManager
   *        the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    String documentiAssociatiDB = ConfigManager.getValore("it.eldasoft.documentiAssociatiDB");

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();
    int total = 0;
    int totalAfterFilter = 0;

    String operation = request.getParameter("operation");
    String codgar = request.getParameter("codgar");
    String genere = request.getParameter("genere");
    String key1 = request.getParameter("key1");
    String chiaveOriginale = request.getParameter("chiaveOriginale");
    String stipula = request.getParameter("stipula");
    String oggetto = "gara";
    Long idStipula=null;

    try {

      String queryDocGara = "";
      String queryDocComunicazioni = "";
      String queryDocDitta = "";
      String queryDocAssociati = "";
      String queryDocComAllaDitta = "";
      String queryDocComDallaDitta = "";

      String queryDocStipula = "";
      String queryDocAssociatiStipula = "";
      String queryDocComAllaDittaStipula = "";
      String queryDocComDallaDittaStipula = "";

      if("true".equals(stipula)) {
        idStipula = new Long(key1);
        oggetto = "stipula contratto";
        String condizioneAppend = this.sqlManager.getDBFunction("concat",  new String[] {"'Documentazione '" , "'" + oggetto + "'" });

        String castDATA="DATE";
        if("POS".equals(this.sqlManager.getTipoDB()))
          castDATA="TIMESTAMP";
        else if("MSQ".equals(this.sqlManager.getTipoDB()))
          castDATA="DATETIME";

        String condizioneAppend1 = this.sqlManager.getDBFunction("concat",  new String[] {"'Documento contratto - '" , "t.tab1desc" });

        //Documentazione contratto
        queryDocStipula = "select " + condizioneAppend + " as ARGOMENTO, " + condizioneAppend1 + " as GRUPPO,  cast(null as varchar(20)) as LOTTO," +
            " cast(null as varchar(16)) as CFDITTA, cast(null as varchar(120)) as DITTA, " +
            " TITOLO as DESCRIZIONE, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, " +
            "cast(null as " + castDATA + ") as DATA " +
            "from G1DOCSTIPULA  d join V_GARE_DOCSTIPULA w on w.id=d.id join tab1 t on t.tab1tip=d.visibilita " +
            "where d.idstipula = ? and w.idprg is not null and w.IDDOCDIG is not null and t.tab1cod='A1182' ";
        /*
        " UNION select " + condizioneAppend + " as ARGOMENTO, t.tab1desc as GRUPPO, cast(null as varchar(20)) as LOTTO, " +
            " cast(null as varchar(16)) as CFDITTA, cast(null as varchar(120)) as DITTA, " +
            " TITOLO as DESCRIZIONE, cast(null as varchar(3)) as IDPRG , cast(null as numeric(12)) as IDDOCDIG, cast(null as varchar(100)) as DIGNOMDOC, " +
            "cast(null as varchar(2)) as ARCHIVIATO, cast(null as " + castDATA + ") as DATA " +
            "from G1DOCSTIPULA  join tab1 t on t.tab1tip=visibilita where idstipula = ? and visibilita= 3 and statodoc = 3  and t.tab1cod='A1182'" ;
*/
        //DOCUMENTI ASSOCIATI
        String sqlSintassi= this.sqlManager.getDBFunction("inttostr",  new String[] {"g.id"});
        if (documentiAssociatiDB.equals("1")) {
          queryDocAssociatiStipula = "select " + condizioneAppend + " as ARGOMENTO," +
                " cast('Documento associato' as varchar(100)) as GRUPPO, cast(null as varchar(20)) as LOTTO, " +
          "cast(null as varchar(16)) as CFDITTA, cast(null as varchar(120)) as DITTA, " +
          "c.c0atit as DESCRIZIONE , w.idprg, w.iddocdig, c.c0anomogg as DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, coalesce(" +
          this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) +"," +
          this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) +"," +
          this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"}) + ") as DATA " +
          "from c0oggass c join G1STIPULA g on c.c0akey1=" + sqlSintassi + " join w_docdig w on c.c0acod=w.digkey1 and c.c0aprg=w.idprg and w.digent='C0OGGASS' " +
          "where g.id = ? and c.c0aent in ('G1STIPULA','G1DOCSTIPULA') " ;

        } else {
          queryDocAssociatiStipula = "select " + condizioneAppend + " as ARGOMENTO," +
                  " cast('Documento associato' as varchar(100)) as GRUPPO, cast(null as varchar(20)) as LOTTO, " +
            "cast(null as varchar(16)) as CFDITTA, cast(null as varchar(120)) as DITTA, " +
            "c.c0atit as DESCRIZIONE, c.c0aprg as IDPRG, c.c0acod as IDDOCDIG, c.c0anomogg as DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, coalesce(" +
            this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) +"," +
            this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) +"," +
            this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"}) + ") as DATA " +
            "from c0oggass c join G1STIPULA g on c.c0akey1= " + sqlSintassi +
            "where g.id = ? and c.c0aent in ('G1STIPULA','G1DOCSTIPULA') " ;

        }

        //Documenti inviati alla ditta
        queryDocComAllaDittaStipula = "select DISTINCT 'Documentazione delle ditte' as ARGOMENTO," +
            " cast('Documento inviato alla ditta' as varchar(100))   as GRUPPO, cast(null as varchar(20)) as LOTTO, " +
           " cast(null as varchar(16)) as CFDITTA, id.descodsog as DITTA, " +
           this.sqlManager.getDBFunction("concat",  new String[] {this.sqlManager.getDBFunction("concat",  new String[] {"i.commsgogg" , "' - '" }) , "w.DIGDESDOC" }) + " as DESCRIZIONE," +
           " w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, " +
            this.sqlManager.getDBFunction("datetimetostring",  new String[] {"id.DESDATINV"}) + " as DATA, id.idcom as IDCOM, DESCODENT " +
          "from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + "=w.digkey2 and w.digkey1=i.idprg " +
            "and w.digent='W_INVCOM' join g1stipula g on i.comkey1= g.codstipula"  +
          " join (select DISTINCT idprg,idcom,descodsog,descodent,DESSTATO,MIN(DESDATINV) as DESDATINV from w_invcomdes group by idprg,idcom,descodsog,descodent,DESSTATO) id on id.idprg = i.idprg and id.idcom = i.idcom " +
          "where g.id = ? and i.compub <> '1' and (COMTIPO is null or COMTIPO<>'FS12') and (id.DESSTATO = '2' or id.DESSTATO = '4') order by DITTA,IDCOM,IDDOCDIG";


        //Documenti inviati dalla ditta

        queryDocComDallaDittaStipula = "select cast('Documentazione delle ditte' as varchar(100)) as ARGOMENTO, cast('Documento ricevuto dalla ditta' as varchar(100)) as GRUPPO, " +
            "cast(null as varchar(20)) as LOTTO, cast(null as varchar(16)) as CFDITTA, comkey1 as DITTA, " +
          this.sqlManager.getDBFunction("concat",  new String[] {this.sqlManager.getDBFunction("concat",  new String[] {"i.commsgogg" , "' - '" }) , "w.DIGDESDOC" }) + " as DESCRIZIONE, " +
          "w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, " +
          this.sqlManager.getDBFunction("datetimetostring",  new String[] {"i.COMDATINS"}) + " as DATA " +
          "from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + " = w.digkey1 and i.idprg = w.idprg and w.digent = 'W_INVCOM' " +
          "join g1stipula g on i.comkey2=g.codstipula " +
          "where g.id = ? and i.compub <> '1' and COMTIPO='FS12' and COMSTATO='3' and COMENT = 'G1STIPULA' order by ditta,i.idcom,iddocdig";


      }else{
        String condizioneAppend = this.sqlManager.getDBFunction("concat",  new String[] {"'Documentazione '" , "'" + oggetto + "'" });
          //Documentazione di gara
          queryDocGara = "select " + condizioneAppend + " as ARGOMENTO, GRUPPO, NGARA as LOTTO," +
          		" cast(null as varchar(16)) as CFDITTA, cast(null as varchar(120)) as DITTA, " +
          		" DESCRIZIONE, w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, isarchi as ARCHIVIATO, " +
          this.sqlManager.getDBFunction("datetimetostring",  new String[] {"DATARILASCIO"}) + " as DATA " +
          "from documgara d join w_docdig w on d.iddocdg=w.iddocdig and d.idprg=w.idprg and digent='DOCUMGARA' " +
          "where d.codgar = ? and d.idprg is not null and d.iddocdg is not null order by gruppo, numord, norddocg";

          //Comunicazioni pubbliche
          queryDocComunicazioni = "select " + this.sqlManager.getDBFunction("concat",  new String[] {"'Documentazione '" ,"'" + oggetto + "'"}) + " as ARGOMENTO, cast('Comunicazione pubblica' as varchar(100)) as GRUPPO, g.codiga as LOTTO," +
                " cast(null as varchar(16)) as CFDITTA, cast(null as varchar(120)) as DITTA, " +
                this.sqlManager.getDBFunction("concat",  new String[] {this.sqlManager.getDBFunction("concat",  new String[] {"i.commsgogg" , "' - '" }) , "w.DIGDESDOC" }) + " as DESCRIZIONE," +
          		" w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, (case when comstato='12' then '1' else cast(null as varchar(2)) end) as ARCHIVIATO, " +
                 this.sqlManager.getDBFunction("datetimetostring",  new String[] {"i.COMDATAPUB"}) + " as DATA " +
          "from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + "=w.digkey2 and i.idprg=w.digkey1 and w.digent='W_INVCOM' join gare g on i.comkey1=g.ngara " +
          "where g.codgar1 = ? and i.compub = '1' and (i.comstato='3' or i.comstato='12') order by i.idcom,w.iddocdig";


          //Documenti presentati dalla ditta
          queryDocDitta = "select " + this.sqlManager.getDBFunction("concat",  new String[] {"'Documentazione presentata dalle ditte '" ,  "d.ngara" }) + " as ARGOMENTO, coalesce(d.bustadesc,'Documento presentato dalla ditta') as GRUPPO, d.ngara as LOTTO," +
                " cast(null as varchar(16)) as CFDITTA, d.codimp as DITTA, " +
          		" d.descrizione as DESCRIZIONE, d.IDPRG, d.iddocdg as IDDOCDIG, d.DIGNOMDOC , (case when docannul='1' then '3' else cast(null as varchar(2)) end) as ARCHIVIATO, " +
          		this.sqlManager.getDBFunction("datetimetostring",  new String[] {"d.datarilascio"})  + " as DATA , d.orarilascio as ORA  , d.doctel as DOCTEL, i.uuid as UUID " +
          "from v_gare_docditta d join w_docdig w on d.iddocdg=w.iddocdig and d.idprg=w.idprg " +
          "join imprdocg i on d.codgar = i.codgar and d.ngara = i.ngara and d.codimp = i.codimp and d.norddoci = i.norddoci and d.proveni = i.proveni " +
          "where d.codgar = ? and d.idprg is not null and d.iddocdg is not null order by d.codgar, d.ngara, d.bustaord, d.numord, d.codimp, d.norddoci";

          if (documentiAssociatiDB.equals("1")) {
              queryDocAssociati = "select " + this.sqlManager.getDBFunction("concat",  new String[] {"'Documentazione '" ,  "'" + oggetto + "'" }) + " as ARGOMENTO," +
              		" cast('Documento associato' as varchar(100)) as GRUPPO, c.c0akey1 as LOTTO, " +
              "cast(null as varchar(16)) as CFDITTA, cast(null as varchar(120)) as DITTA, " +
              "c.c0atit as DESCRIZIONE , w.idprg, w.iddocdig, c.c0anomogg as DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, coalesce(" +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) +"," +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) +"," +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"}) + ") as DATA " +
              "from c0oggass c join gare g on c.c0akey1=g.ngara join w_docdig w on c.c0acod=w.digkey1 and c.c0aprg=w.idprg and w.digent='C0OGGASS' " +
              "where g.codgar1 = ? and c.c0aent in ('GARE','GCAP','GOEV','GARSED','GARECONT','GAREAVVISI') union " +

              "select " + this.sqlManager.getDBFunction("concat",  new String[] {"'Documentazione '" ,  "'" + oggetto + "'" }) + " as ARGOMENTO," +
              " cast('Documento associato' as varchar(100)) as GRUPPO, c.c0akey1 as LOTTO, " +
              "cast(null as varchar(16)) as CFDITTA, cast(null as varchar(120)) as DITTA, " +
              "c.c0atit as DESCRIZIONE , w.idprg, w.iddocdig, c.c0anomogg as DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, coalesce(" +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) +"," +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) +"," +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"}) +") as DATA " +
              "from c0oggass c join torn t on c.c0akey1=t.codgar join w_docdig w on c.c0acod=w.digkey1 and c.c0aprg=w.idprg and w.digent='C0OGGASS' " +
              "where t.codgar = ? and c.c0aent  = 'TORN' ";


          } else {
              queryDocAssociati = "select " + this.sqlManager.getDBFunction("concat",  new String[] {"'Documentazione '" , "'" + oggetto + "'" }) + " as ARGOMENTO," +
              		" cast('Documento associato' as varchar(100)) as GRUPPO, c.c0akey1 as LOTTO, " +
              "cast(null as varchar(16)) as CFDITTA, cast(null as varchar(120)) as DITTA, " +
              "c.c0atit as DESCRIZIONE, c.c0aprg as IDPRG, c.c0acod as IDDOCDIG, c.c0anomogg as DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, coalesce(" +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) +"," +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) +"," +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"}) + ") as DATA " +
              "from c0oggass c join gare g on c.c0akey1=g.ngara " +
              "where g.codgar1 = ? and c.c0aent in ('GARE','GCAP','GOEV','GARSED','GARECONT','GAREAVVISI') union " +


              "select " + this.sqlManager.getDBFunction("concat",  new String[] {"'Documentazione '" , "'" + oggetto + "'" }) + " as ARGOMENTO," +
              " cast('Documento associato' as varchar(100)) as GRUPPO, c.c0akey1 as LOTTO, " +
              "cast(null as varchar(16)) as CFDITTA, cast(null as varchar(120)) as DITTA, " +
              "c.c0atit as DESCRIZIONE, c.c0aprg as IDPRG, c.c0acod as IDDOCDIG, c.c0anomogg as DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, coalesce(" +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADATTO"}) +"," +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.C0ADPROT"}) +"," +
              this.sqlManager.getDBFunction("datetimetostring",  new String[] {"c.c0adat"}) + ") as DATA " +
              "from c0oggass c join torn t on c.c0akey1=t.codgar " +
              "where t.codgar = ? and c.c0aent  = 'TORN'";

          }

        //Documenti inviati alla ditta

          queryDocComAllaDitta = "select DISTINCT " + this.sqlManager.getDBFunction("concat",  new String[] {"'Documentazione '" , "'" + oggetto + "'" }) + " as ARGOMENTO," +
          		" cast('Documento inviato alla ditta' as varchar(100))   as GRUPPO, comkey1 as LOTTO, " +
               " cast(null as varchar(16)) as CFDITTA, id.descodsog as DITTA, " +
               this.sqlManager.getDBFunction("concat",  new String[] {this.sqlManager.getDBFunction("concat",  new String[] {"i.commsgogg" , "' - '" }) , "w.DIGDESDOC" }) + " as DESCRIZIONE," +
          	   " w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, " +
          		this.sqlManager.getDBFunction("datetimetostring",  new String[] {"id.DESDATINV"}) + " as DATA, id.idcom as IDCOM, DESCODENT " +
          "from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + "=w.digkey2 and w.digkey1=i.idprg and w.digent='W_INVCOM' join gare g on i.comkey1=g.ngara " +
          "join (select DISTINCT idprg,idcom,descodsog,descodent,DESSTATO,MIN(DESDATINV) as DESDATINV from w_invcomdes group by idprg,idcom,descodsog,descodent,DESSTATO) id on id.idprg = i.idprg and id.idcom = i.idcom " +
          "where g.codgar1 = ? and i.compub <> '1' and (COMTIPO is null or COMTIPO<>'FS12') and (id.DESSTATO = '2' or id.DESSTATO = '4') order by DITTA,IDCOM,IDDOCDIG";


          //Documenti inviati dalla ditta
          if ("1".equals(genere)) {
            queryDocComDallaDitta = "select cast('Documentazione presentata dalle ditte' as varchar(100)) as ARGOMENTO, cast('Documento inviato dalla ditta' as varchar(100)) as GRUPPO, comkey2 as LOTTO, " +
            "cast(null as varchar(16)) as CFDITTA, comkey1 as DITTA, " +
            this.sqlManager.getDBFunction("concat",  new String[] {this.sqlManager.getDBFunction("concat",  new String[] {"i.commsgogg" , "' - '" }) , "w.DIGDESDOC" }) + " as DESCRIZIONE, " +
            "w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, " +
            this.sqlManager.getDBFunction("datetimetostring",  new String[] {"i.COMDATINS"}) + " as DATA " +
            "from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + " = w.digkey1 and i.idprg = w.idprg and w.digent = 'W_INVCOM' " +
            "join torn t on (i.comkey2=t.codgar) " +
            "where t.codgar = ? and i.compub <> '1' and COMTIPO='FS12' and COMSTATO='3' and COMENT IS NULL order by ditta,i.idcom,w.iddocdig";
          }else{
            queryDocComDallaDitta = "select cast('Documentazione presentata dalle ditte' as varchar(100)) as ARGOMENTO, cast('Documento inviato dalla ditta' as varchar(100)) as GRUPPO, comkey2 as LOTTO, " +
            "cast(null as varchar(16)) as CFDITTA, comkey1 as DITTA, " +
            this.sqlManager.getDBFunction("concat",  new String[] {this.sqlManager.getDBFunction("concat",  new String[] {"i.commsgogg" , "' - '" }) , "w.DIGDESDOC" }) + " as DESCRIZIONE, " +
            "w.IDPRG, w.IDDOCDIG, w.DIGNOMDOC, cast(null as varchar(2)) as ARCHIVIATO, " +
            this.sqlManager.getDBFunction("datetimetostring",  new String[] {"i.COMDATINS"}) + " as DATA " +
            "from w_invcom i join w_docdig w on " + this.sqlManager.getDBFunction("inttostr",  new String[] {"i.idcom"}) + " = w.digkey1 and i.idprg = w.idprg and w.digent = 'W_INVCOM' " +
            "join gare g on i.comkey2=g.ngara " +
            "where g.codgar1 = ? and i.compub <> '1' and COMTIPO='FS12' and COMSTATO='3' and COMENT IS NULL order by ditta,i.idcom,iddocdig";
          }
      }

      List<HashMap> hmDocGara = null;
      if("true".equals(stipula)) {
        hmDocGara = sqlManager.getListHashMap(queryDocStipula, new Object[] { idStipula });
      }else {
        hmDocGara = sqlManager.getListHashMap(queryDocGara, new Object[] { codgar });
      }
        if(hmDocGara.size() >0 ){
            for(int j=0; j<hmDocGara.size();j++) {
              HashMap hmj = hmDocGara.get(j);
              JdbcParametro jdbcLotto = (JdbcParametro) hmj.get("LOTTO");
              if (jdbcLotto != null) {
                String lotto = jdbcLotto.stringValue();
                String codiga = (String)this.sqlManager.getObject("select codiga from gare where ngara = ? ", new Object[]{lotto});
                hmj.put("LOTTO", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiga));
              }
              //inserisco gli attributi per wsallegati e per gardoc_wsdm
              String idprg = null;
              Long iddocdig = null;
              Long idwsdoc = null;
              Long stato_archiviazione = null;
              String esito = null;
              JdbcParametro jdbcIdprg = (JdbcParametro) hmj.get("IDPRG");
              if (jdbcIdprg != null){
                idprg = jdbcIdprg.stringValue();
              }

              JdbcParametro jdbcIddocdig = (JdbcParametro) hmj.get("IDDOCDIG");
              if (jdbcIddocdig != null){
                iddocdig = jdbcIddocdig.longValue();
              }
              HashMap hmAllegato = new HashMap();

              Vector<?> datiAllegati = this.sqlManager.getVector("select ws.idwsdoc from wsallegati ws  where ws.key1 = ? and ws.key2 = ? and ws.entita='W_DOCDIG'", new Object[]{idprg,iddocdig});
              if (datiAllegati != null && datiAllegati.size()>0) {
                idwsdoc = (Long) SqlManager.getValueFromVectorParam(datiAllegati, 0).getValue();
              }

              String selectGARDOC_WSDM = null;
              selectGARDOC_WSDM = "select gwsdm1.stato_archiviazione,gwsdm1.esito from gardoc_wsdm gwsdm1 where gwsdm1.entita='W_DOCDIG' and gwsdm1.key1 = ? and gwsdm1.key2 = ? order by id_archiviazione desc";
              List<?> listaDatiGardocWsdm = this.sqlManager.getListVector(selectGARDOC_WSDM, new Object[]{idprg,iddocdig});
              if (listaDatiGardocWsdm != null && listaDatiGardocWsdm.size()>0) {
                stato_archiviazione = (Long) SqlManager.getValueFromVectorParam(listaDatiGardocWsdm.get(0), 0).getValue();
                esito = SqlManager.getValueFromVectorParam(listaDatiGardocWsdm.get(0), 1).getStringValue();
              }

              String selectGARDOC_COS = null;
              selectGARDOC_COS = "select gwsdm1.stato_archiviazione,gwsdm1.esito from gardoc_wsdm gwsdm1 join gardoc_jobs job on gwsdm1.id_archiviazione = job.id_archiviazione where job.tipo_archiviazione = 3 and gwsdm1.entita='W_DOCDIG' and gwsdm1.key1 = ? and gwsdm1.key2 = ? order by gwsdm1.id_archiviazione desc";
              List<?> listaDatiGardocCOS = this.sqlManager.getListVector(selectGARDOC_COS,
                  new Object[] { idprg, iddocdig });

              Long stato_archiviazione_cos = null;
              if (listaDatiGardocCOS != null && listaDatiGardocCOS.size() > 0) {
                stato_archiviazione_cos = (Long) SqlManager
                    .getValueFromVectorParam(listaDatiGardocCOS.get(0), 0).getValue();
              }

              hmj.put("IDWSDOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idwsdoc));
              hmj.put("STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, stato_archiviazione));
              hmj.put("STATO_COS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, stato_archiviazione_cos));
              hmj.put("PROVENIENZA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "1"));
              hmj.put("ESITO", new JdbcParametro(JdbcParametro.TIPO_TESTO, esito));

              hmDocGara.set(j, hmj);


            }//for
          }

          if(!"true".equals(stipula)) {
          List<HashMap> hmDocComunicazioni = sqlManager.getListHashMap(queryDocComunicazioni, new Object[] { codgar });
          if(hmDocComunicazioni.size() >0 ){
            for(int j=0; j<hmDocComunicazioni.size();j++) {

              HashMap hmj = hmDocComunicazioni.get(j);

              //inserisco gli attributi per wsallegati e per gardoc_wsdm
              String idprg = null;
              Long iddocdig = null;
              Long idwsdoc = null;
              Long stato_archiviazione = null;
              String esito = null;
              JdbcParametro jdbcIdprg = (JdbcParametro) hmj.get("IDPRG");
              if (jdbcIdprg != null){
                idprg = jdbcIdprg.stringValue();
              }

              JdbcParametro jdbcIddocdig = (JdbcParametro) hmj.get("IDDOCDIG");
              if (jdbcIddocdig != null){
                iddocdig = jdbcIddocdig.longValue();
              }
              HashMap hmAllegato = new HashMap();

              Vector<?> datiAllegati = this.sqlManager.getVector("select ws.idwsdoc from wsallegati ws  where ws.key1 = ? and ws.key2 = ? and ws.entita='W_DOCDIG'", new Object[]{idprg,iddocdig});
              if (datiAllegati != null && datiAllegati.size()>0) {
                idwsdoc = (Long) SqlManager.getValueFromVectorParam(datiAllegati, 0).getValue();
              }

              String selectGARDOC_WSDM = null;
              selectGARDOC_WSDM = "select gwsdm1.stato_archiviazione,gwsdm1.esito from gardoc_wsdm gwsdm1 where gwsdm1.entita='W_DOCDIG' and gwsdm1.key1 = ? and gwsdm1.key2 = ? order by id_archiviazione desc";
              List<?> listaDatiGardocWsdm = this.sqlManager.getListVector(selectGARDOC_WSDM, new Object[]{idprg,iddocdig});

              if (listaDatiGardocWsdm != null && listaDatiGardocWsdm.size()>0) {
                stato_archiviazione = (Long) SqlManager.getValueFromVectorParam(listaDatiGardocWsdm.get(0), 0).getValue();
                esito = SqlManager.getValueFromVectorParam(listaDatiGardocWsdm.get(0), 1).getStringValue();
              }

              Long stato_archiviazione_cos = null;
              String selectGARDOC_COS = null;
              selectGARDOC_COS = "select gwsdm1.stato_archiviazione,gwsdm1.esito from gardoc_wsdm gwsdm1 join gardoc_jobs job on gwsdm1.id_archiviazione = job.id_archiviazione where job.tipo_archiviazione = 3 and gwsdm1.entita='W_DOCDIG' and gwsdm1.key1 = ? and gwsdm1.key2 = ? order by gwsdm1.id_archiviazione desc";
              List<?> listaDatiGardocCOS = this.sqlManager.getListVector(selectGARDOC_COS,
                  new Object[] { idprg, iddocdig });

              if (listaDatiGardocCOS != null && listaDatiGardocCOS.size() > 0) {
                stato_archiviazione_cos = (Long) SqlManager
                    .getValueFromVectorParam(listaDatiGardocCOS.get(0), 0).getValue();
              }

              hmj.put("IDWSDOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idwsdoc));
              hmj.put("STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, stato_archiviazione));
              hmj.put("STATO_COS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, stato_archiviazione_cos));
              hmj.put("PROVENIENZA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "2"));
              hmj.put("ESITO", new JdbcParametro(JdbcParametro.TIPO_TESTO, esito));

              hmDocComunicazioni.set(j, hmj);


              hmDocGara.add(hmDocComunicazioni.get(j));
            }
          }
          }

          //DOCUMENTI ASSOCIATI
          List<HashMap> hmDocAssociati = null;
          if("true".equals(stipula)) {
            hmDocAssociati = sqlManager.getListHashMap(queryDocAssociatiStipula, new Object[] { idStipula });
          }else {
            hmDocAssociati = sqlManager.getListHashMap(queryDocAssociati, new Object[] { codgar,codgar });
          }

          if(hmDocAssociati.size() >0 ){
            for(int j=0; j<hmDocAssociati.size();j++) {
              HashMap hmj = hmDocAssociati.get(j);
              JdbcParametro jdbcLotto = (JdbcParametro) hmj.get("LOTTO");
              if (jdbcLotto != null) {
                String lotto = jdbcLotto.stringValue();
                String codiga = (String)this.sqlManager.getObject("select codiga from gare where ngara = ? ", new Object[]{lotto});
                hmj.put("LOTTO", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiga));
              }

              //inserisco gli attributi per wsallegati e per gardoc_wsdm
              String idprg = null;
              Long iddocdig = null;
              Long idwsdoc = null;
              Long stato_archiviazione = null;
              String esito = null;
              JdbcParametro jdbcIdprg = (JdbcParametro) hmj.get("IDPRG");
              if (jdbcIdprg != null){
                idprg = jdbcIdprg.stringValue();
              }

              JdbcParametro jdbcIddocdig = (JdbcParametro) hmj.get("IDDOCDIG");
              if (jdbcIddocdig != null){
                iddocdig = jdbcIddocdig.longValue();
              }
              HashMap hmAllegato = new HashMap();

              if (documentiAssociatiDB.equals("1")) {
                Vector<?> datiAllegati = this.sqlManager.getVector("select ws.idwsdoc from wsallegati ws  where ws.key1 = ? and ws.key2 = ? and ws.entita='W_DOCDIG'", new Object[]{idprg,iddocdig});
                if (datiAllegati != null && datiAllegati.size()>0) {
                  idwsdoc = (Long) SqlManager.getValueFromVectorParam(datiAllegati, 0).getValue();
                }

                String selectGARDOC_WSDM = null;
                selectGARDOC_WSDM = "select gwsdm1.stato_archiviazione,gwsdm1.esito from gardoc_wsdm gwsdm1 where gwsdm1.entita='W_DOCDIG' and gwsdm1.key1 = ? and gwsdm1.key2 = ? order by id_archiviazione desc";
                List<?> listaDatiGardocWsdm = this.sqlManager.getListVector(selectGARDOC_WSDM, new Object[]{idprg,iddocdig});

                if (listaDatiGardocWsdm != null && listaDatiGardocWsdm.size()>0) {
                  stato_archiviazione = (Long) SqlManager.getValueFromVectorParam(listaDatiGardocWsdm.get(0), 0).getValue();
                  esito = SqlManager.getValueFromVectorParam(listaDatiGardocWsdm.get(0), 1).getStringValue();
                }
                Long stato_archiviazione_cos = null;
                String selectGARDOC_COS = null;
                selectGARDOC_COS = "select gwsdm1.stato_archiviazione,gwsdm1.esito from gardoc_wsdm gwsdm1 join gardoc_jobs job on gwsdm1.id_archiviazione = job.id_archiviazione where job.tipo_archiviazione = 3 and gwsdm1.entita='W_DOCDIG' and gwsdm1.key1 = ? and gwsdm1.key2 = ? order by gwsdm1.id_archiviazione desc";
                List<?> listaDatiGardocCOS = this.sqlManager.getListVector(selectGARDOC_COS,
                    new Object[] { idprg, iddocdig });

                if (listaDatiGardocCOS != null && listaDatiGardocCOS.size() > 0) {
                  stato_archiviazione_cos = (Long) SqlManager
                      .getValueFromVectorParam(listaDatiGardocCOS.get(0), 0).getValue();
                }

                hmj.put("IDWSDOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idwsdoc));
                hmj.put("STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, stato_archiviazione));
                hmj.put("STATO_COS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, stato_archiviazione_cos));
                hmj.put("PROVENIENZA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "7"));
                hmj.put("ESITO", new JdbcParametro(JdbcParametro.TIPO_TESTO, esito));

              }else{
                Vector<?> datiAllegati = this.sqlManager.getVector("select ws.idwsdoc from wsallegati ws  where ws.key1 = ? and ws.key2 = ? and ws.entita='C0OGGASS'", new Object[]{idprg,iddocdig});
                if (datiAllegati != null && datiAllegati.size()>0) {
                  idwsdoc = (Long) SqlManager.getValueFromVectorParam(datiAllegati, 0).getValue();
                }

                String selectGARDOC_WSDM = null;
                selectGARDOC_WSDM = "select gwsdm1.stato_archiviazione,gwsdm1.esito from gardoc_wsdm gwsdm1 where gwsdm1.entita='C0OGGASS' and gwsdm1.key1 = ? and gwsdm1.key2 = ? order by id_archiviazione desc";
                List<?> listaDatiGardocWsdm = this.sqlManager.getListVector(selectGARDOC_WSDM, new Object[]{idprg,iddocdig});

                if (listaDatiGardocWsdm != null && listaDatiGardocWsdm.size()>0) {
                  stato_archiviazione = (Long) SqlManager.getValueFromVectorParam(listaDatiGardocWsdm.get(0), 0).getValue();
                  esito = SqlManager.getValueFromVectorParam(listaDatiGardocWsdm.get(0), 1).getStringValue();
                }
                Long stato_archiviazione_cos = null;
                String selectGARDOC_COS = null;
                selectGARDOC_COS = "select gwsdm1.stato_archiviazione,gwsdm1.esito from gardoc_wsdm gwsdm1 join gardoc_jobs job on gwsdm1.id_archiviazione = job.id_archiviazione where job.tipo_archiviazione = 3 and gwsdm1.entita='C0OGGASS' and gwsdm1.key1 = ? and gwsdm1.key2 = ? order by gwsdm1.id_archiviazione desc";
                List<?> listaDatiGardocCOS = this.sqlManager.getListVector(selectGARDOC_COS,
                    new Object[] { idprg, iddocdig });

                if (listaDatiGardocCOS != null && listaDatiGardocCOS.size() > 0) {
                  stato_archiviazione_cos = (Long) SqlManager
                      .getValueFromVectorParam(listaDatiGardocCOS.get(0), 0).getValue();
                }

                hmj.put("IDWSDOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idwsdoc));
                hmj.put("STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, stato_archiviazione));
                hmj.put("STATO_COS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, stato_archiviazione_cos));
                hmj.put("PROVENIENZA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "4"));
                hmj.put("ESITO", new JdbcParametro(JdbcParametro.TIPO_TESTO, esito));

              }

              hmDocAssociati.set(j, hmj);

            }//for

          }
          if(hmDocAssociati.size() >0 ){
            for(int j=0; j<hmDocAssociati.size();j++) {
              hmDocGara.add(hmDocAssociati.get(j));
            }
          }

          if(!"true".equals(stipula)) {
          List<HashMap> hmDocDitta = sqlManager.getListHashMap(queryDocDitta, new Object[] { codgar });
          if(hmDocDitta.size() >0 ){
            String selecStatoComunicazione = "select i.comstato from w_invcom i,w_docdig d where d.idprg=? and d.digkey3=? and i.idprg=d.idprg and i.idcom=" + this.sqlManager.getDBFunction("strtoint",  new String[] {"d.digkey1" });
            for(int j=0; j<hmDocDitta.size();j++) {
              HashMap hmj = hmDocDitta.get(j);

              String idprg = null;
              Long iddocdig = null;
              JdbcParametro jdbcIdprg = (JdbcParametro) hmj.get("IDPRG");
              if (jdbcIdprg != null){
                idprg = jdbcIdprg.stringValue();
              }

              JdbcParametro jdbcIddocdig = (JdbcParametro) hmj.get("IDDOCDIG");
              if (jdbcIddocdig != null){
                iddocdig = jdbcIddocdig.longValue();
              }

              JdbcParametro jdbcDoctel = (JdbcParametro) hmj.get("DOCTEL");
              JdbcParametro jdbcUuid = (JdbcParametro) hmj.get("UUID");
              if(jdbcDoctel!=null && jdbcUuid != null) {
                String doctel = jdbcDoctel.stringValue();
                String uuid = jdbcUuid.stringValue();
                if("1".equals(doctel) && uuid!=null && !"".equals(uuid)) {
                  String comstato = (String)this.sqlManager.getObject(selecStatoComunicazione, new Object[]{idprg,uuid});
                  if("13".equals(comstato) || "20".equals(comstato))
                    continue;
                }
              }

              JdbcParametro jdbcLotto = (JdbcParametro) hmj.get("LOTTO");
              if (jdbcLotto != null) {
                String lotto = jdbcLotto.stringValue();
                String codiga = (String)this.sqlManager.getObject("select codiga from gare where ngara = ? ", new Object[]{lotto});
                hmj.put("LOTTO", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiga));
              }

              JdbcParametro jdbcDitta = (JdbcParametro) hmj.get("DITTA");
              if (jdbcDitta != null) {
                String codimp = jdbcDitta.stringValue();
                //verifico se la ditta è un raggruppamento
                String getImpr = "select codimp, tipimp, cfimp, nomest from impr where codimp = ?";
                List<?> listaImpresa = this.sqlManager.getListVector(getImpr, new Object[]{codimp});
                if (listaImpresa != null && listaImpresa.size()>0) {
                    String cod = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 0).getStringValue();
                    Long tipimp = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 1).longValue();
                    String nomeImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue();
                    if (tipimp != null && (new Long(3).equals(tipimp) || new Long(10).equals(tipimp))) {
                        //la ditta è un raggruppamento allora prendo i dati della mandataria
                        cod = (String)this.sqlManager.getObject("select coddic from ragimp where codime9 = ? and impman='1'", new Object[]{cod});
                        listaImpresa = this.sqlManager.getListVector(getImpr, new Object[]{cod});
                        if (listaImpresa != null && listaImpresa.size()>0) {
                            nomeImpresa += "(mandataria " + SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue() + ")";
                        }
                    }
                    hmj.put("DITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeImpresa));
                    hmj.put("CFDITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue()));
                    //hmDocDitta.set(j, hmj);
                }
              }

              JdbcParametro jdbcData = (JdbcParametro) hmj.get("DATA");
              JdbcParametro jdbcOra = (JdbcParametro) hmj.get("ORA");
              if (jdbcData.getValue() != null) {
                String data = jdbcData.stringValue();
                if (jdbcOra.getValue() != null) {
                  String ora = jdbcOra.stringValue();
                  data = data.substring(0, 11) + ora + ":00" ;
                }

                hmj.remove("ORA");
                hmj.put("DATA", new JdbcParametro(JdbcParametro.TIPO_TESTO, data));
              }

                //inserisco gli attributi per wsallegati e per gardoc_wsdm

                Long idwsdoc = null;
                Long stato_archiviazione = null;
                String esito = null;

                HashMap hmAllegato = new HashMap();

                Vector<?> datiAllegati = this.sqlManager.getVector("select ws.idwsdoc from wsallegati ws  where ws.key1 = ? and ws.key2 = ? and ws.entita='W_DOCDIG'", new Object[]{idprg,iddocdig});
                if (datiAllegati != null && datiAllegati.size()>0) {
                  idwsdoc = (Long) SqlManager.getValueFromVectorParam(datiAllegati, 0).getValue();
                }

                String selectGARDOC_WSDM = null;
                selectGARDOC_WSDM = "select gwsdm1.stato_archiviazione,gwsdm1.esito from gardoc_wsdm gwsdm1 where gwsdm1.entita='W_DOCDIG' and gwsdm1.key1 = ? and gwsdm1.key2 = ? order by id_archiviazione desc";
                List<?> listaDatiGardocWsdm = this.sqlManager.getListVector(selectGARDOC_WSDM, new Object[]{idprg,iddocdig});

                if (listaDatiGardocWsdm != null && listaDatiGardocWsdm.size()>0) {
                  stato_archiviazione = (Long) SqlManager.getValueFromVectorParam(listaDatiGardocWsdm.get(0), 0).getValue();
                  esito = SqlManager.getValueFromVectorParam(listaDatiGardocWsdm.get(0), 1).getStringValue();
                }

                Long stato_archiviazione_cos = null;
                String selectGARDOC_COS = null;
                selectGARDOC_COS = "select gwsdm1.stato_archiviazione,gwsdm1.esito from gardoc_wsdm gwsdm1 join gardoc_jobs job on gwsdm1.id_archiviazione = job.id_archiviazione where job.tipo_archiviazione = 3 and gwsdm1.entita='W_DOCDIG' and gwsdm1.key1 = ? and gwsdm1.key2 = ? order by gwsdm1.id_archiviazione desc";
                List<?> listaDatiGardocCOS = this.sqlManager.getListVector(selectGARDOC_COS,
                    new Object[] { idprg, iddocdig });

                if (listaDatiGardocCOS != null && listaDatiGardocCOS.size() > 0) {
                  stato_archiviazione_cos = (Long) SqlManager
                      .getValueFromVectorParam(listaDatiGardocCOS.get(0), 0).getValue();
                }

                hmj.put("IDWSDOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idwsdoc));
                hmj.put("STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, stato_archiviazione));
                hmj.put("STATO_COS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, stato_archiviazione_cos));
                hmj.put("PROVENIENZA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "3"));
                hmj.put("ESITO", new JdbcParametro(JdbcParametro.TIPO_TESTO, esito));

                hmDocDitta.set(j, hmj);


              hmDocGara.add(hmDocDitta.get(j));


            }//for
          }//hmDocDitta
          }

          List<HashMap> hmDocComAllaDitta = null;
          if("true".equals(stipula)) {
            hmDocComAllaDitta = sqlManager.getListHashMap(queryDocComAllaDittaStipula, new Object[] { idStipula });
          }else {
            hmDocComAllaDitta = sqlManager.getListHashMap(queryDocComAllaDitta, new Object[] { codgar });
          }

          if(hmDocComAllaDitta.size() >0 ){
            for(int j=0; j<hmDocComAllaDitta.size();j++) {
              HashMap hmj = hmDocComAllaDitta.get(j);

              JdbcParametro jdbcLotto = (JdbcParametro) hmj.get("LOTTO");
              if (jdbcLotto != null) {
                String lotto = jdbcLotto.stringValue();
                String codiga = (String)this.sqlManager.getObject("select codiga from gare where ngara = ? ", new Object[]{lotto});
                hmj.put("LOTTO", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiga));
              }


              JdbcParametro jdbcDitta = (JdbcParametro) hmj.get("DITTA");

              if (jdbcDitta != null) {
                String codimp = jdbcDitta.stringValue();
                JdbcParametro jdbcDescodent = (JdbcParametro) hmj.get("DESCODENT");
                String entita=jdbcDescodent.stringValue();

                //verifico se la ditta è un raggruppamento
                String getDatiDest = "select codimp, cfimp, nomest, tipimp from impr where codimp = ?";
                if("TECNI".equals(entita))
                  getDatiDest = "select codtec, cftec, nomtec from tecni where codtec = ?";
                List<?> listaImpresa = this.sqlManager.getListVector(getDatiDest, new Object[]{codimp});
                if (listaImpresa != null && listaImpresa.size()>0) {
                    String cod = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 0).getStringValue();
                    Long tipimp = null;
                    if("IMPR".equals(entita))
                      tipimp = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).longValue();
                    String nomeImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue();
                    if (tipimp != null && (new Long(3).equals(tipimp) || new Long(10).equals(tipimp))) {
                        //la ditta è un raggruppamento allora prendo i dati della mandataria
                        cod = (String)this.sqlManager.getObject("select coddic from ragimp where codime9 = ? and impman='1'", new Object[]{cod});
                        listaImpresa = this.sqlManager.getListVector(getDatiDest, new Object[]{cod});
                        if (listaImpresa != null && listaImpresa.size()>0) {
                            nomeImpresa += "(mandataria " + SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue() + ")";
                        }
                    }
                    nomeImpresa = UtilityStringhe.convertiNullInStringaVuota(nomeImpresa);
                    if("".equals(nomeImpresa)){
                      JdbcParametro jdbcIdcom = (JdbcParametro) hmj.get("IDCOM");
                      if (jdbcIdcom != null) {
                        Long idcom = jdbcIdcom.longValue();
                        String email = (String) this.sqlManager.getObject("select desmail from w_invcomdes where idcom=? and descodent is null and descodsog is null", new Object[]{idcom});
                        hmj.remove("IDCOM");
                        hmj.put("DITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, email));
                      }
                    }else{
                      hmj.put("DITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeImpresa));
                      hmj.put("CFDITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, SqlManager.getValueFromVectorParam(listaImpresa.get(0), 1).getStringValue()));
                    }
                }else{
                  JdbcParametro jdbcIdcom = (JdbcParametro) hmj.get("IDCOM");
                  if (jdbcIdcom != null) {
                    Long idcom = jdbcIdcom.longValue();
                    String email = (String) this.sqlManager.getObject("select desmail from w_invcomdes where idcom=? and descodent is null and descodsog is null", new Object[]{idcom});
                    hmj.remove("IDCOM");
                    hmj.put("DITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, email));
                  }
                }//listaImprese


              }


              //inserisco gli attributi per wsallegati e per gardoc_wsdm
              String idprg = null;
              Long iddocdig = null;
              Long idwsdoc = null;
              Long stato_archiviazione = null;
              String esito = null;
              JdbcParametro jdbcIdprg = (JdbcParametro) hmj.get("IDPRG");
              if (jdbcIdprg != null){
                idprg = jdbcIdprg.stringValue();
              }

              JdbcParametro jdbcIddocdig = (JdbcParametro) hmj.get("IDDOCDIG");
              if (jdbcIddocdig != null){
                iddocdig = jdbcIddocdig.longValue();
              }
              HashMap hmAllegato = new HashMap();

              Vector<?> datiAllegati = this.sqlManager.getVector("select ws.idwsdoc from wsallegati ws  where ws.key1 = ? and ws.key2 = ? and ws.entita='W_DOCDIG'", new Object[]{idprg,iddocdig});
              if (datiAllegati != null && datiAllegati.size()>0) {
                idwsdoc = (Long) SqlManager.getValueFromVectorParam(datiAllegati, 0).getValue();
              }

              String selectGARDOC_WSDM = null;
              selectGARDOC_WSDM = "select gwsdm1.stato_archiviazione,gwsdm1.esito from gardoc_wsdm gwsdm1 where gwsdm1.entita='W_DOCDIG' and gwsdm1.key1 = ? and gwsdm1.key2 = ? order by id_archiviazione desc";
              List<?> listaDatiGardocWsdm = this.sqlManager.getListVector(selectGARDOC_WSDM, new Object[]{idprg,iddocdig});

              if (listaDatiGardocWsdm != null && listaDatiGardocWsdm.size()>0) {
                stato_archiviazione = (Long) SqlManager.getValueFromVectorParam(listaDatiGardocWsdm.get(0), 0).getValue();
                esito = SqlManager.getValueFromVectorParam(listaDatiGardocWsdm.get(0), 1).getStringValue();
              }

              Long stato_archiviazione_cos = null;
              String selectGARDOC_COS = null;
              selectGARDOC_COS = "select gwsdm1.stato_archiviazione,gwsdm1.esito from gardoc_wsdm gwsdm1 join gardoc_jobs job on gwsdm1.id_archiviazione = job.id_archiviazione where job.tipo_archiviazione = 3 and gwsdm1.entita='W_DOCDIG' and gwsdm1.key1 = ? and gwsdm1.key2 = ? order by gwsdm1.id_archiviazione desc";
              List<?> listaDatiGardocCOS = this.sqlManager.getListVector(selectGARDOC_COS,
                  new Object[] { idprg, iddocdig });

              if (listaDatiGardocCOS != null && listaDatiGardocCOS.size() > 0) {
                stato_archiviazione_cos = (Long) SqlManager
                    .getValueFromVectorParam(listaDatiGardocCOS.get(0), 0).getValue();
              }

              hmj.put("IDWSDOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idwsdoc));
              hmj.put("STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, stato_archiviazione));
              hmj.put("STATO_COS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, stato_archiviazione_cos));
              hmj.put("PROVENIENZA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "5"));
              hmj.put("ESITO", new JdbcParametro(JdbcParametro.TIPO_TESTO, esito));

              hmDocComAllaDitta.set(j, hmj);

              hmDocGara.add(hmDocComAllaDitta.get(j));


            }//for
          }//ditta

          //Documenti inviati dalla ditta

          List<HashMap> hmDocComDallaDitta = null;
          if("true".equals(stipula)) {
            hmDocComDallaDitta = sqlManager.getListHashMap(queryDocComDallaDittaStipula, new Object[] { idStipula });
          }else {
            hmDocComDallaDitta = sqlManager.getListHashMap(queryDocComDallaDitta, new Object[] { codgar });
          }

          if(hmDocComDallaDitta.size() >0 ){
            for(int j=0; j<hmDocComDallaDitta.size();j++) {
              HashMap hmj = hmDocComDallaDitta.get(j);

              JdbcParametro jdbcLotto = (JdbcParametro) hmj.get("LOTTO");
              if (jdbcLotto != null) {
                String lotto = jdbcLotto.stringValue();
                String codiga = (String)this.sqlManager.getObject("select codiga from gare where ngara = ? ", new Object[]{lotto});
                hmj.put("LOTTO", new JdbcParametro(JdbcParametro.TIPO_TESTO, codiga));
              }

              JdbcParametro jdbcDitta = (JdbcParametro) hmj.get("DITTA");
              if (jdbcDitta != null) {
                String codimp = jdbcDitta.stringValue();

                String codPUser = (String)this.sqlManager.getObject("select userkey1 from w_puser where usernome = ? and userent = 'IMPR'", new Object[]{codimp});
                if (codPUser != null) {
                    codimp = codPUser;
                } else {
                  ;
                }

                //verifico se la ditta è un raggruppamento
                String getImpr = "select codimp, tipimp, cfimp, nomest from impr where codimp = ?";
                List<?> listaImpresa = this.sqlManager.getListVector(getImpr, new Object[]{codimp});
                if (listaImpresa != null && listaImpresa.size()>0) {
                    String cod = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 0).getStringValue();
                    Long tipimp = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 1).longValue();
                    String nomeImpresa = SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue();
                    if (tipimp != null && (new Long(3).equals(tipimp) || new Long(10).equals(tipimp))) {
                        //la ditta è un raggruppamento allora prendo i dati della mandataria
                        cod = (String)this.sqlManager.getObject("select coddic from ragimp where codime9 = ? and impman='1'", new Object[]{cod});
                        listaImpresa = this.sqlManager.getListVector(getImpr, new Object[]{cod});
                        if (listaImpresa != null && listaImpresa.size()>0) {
                            nomeImpresa += "(mandataria " + SqlManager.getValueFromVectorParam(listaImpresa.get(0), 3).getStringValue() + ")";
                        }
                    }
                    hmj.put("DITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeImpresa));
                    hmj.put("CFDITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, SqlManager.getValueFromVectorParam(listaImpresa.get(0), 2).getStringValue()));
                }else{
                  JdbcParametro jdbcMail = (JdbcParametro) hmj.get("MAIL");
                  if (jdbcMail != null) {
                    String mail = jdbcMail.stringValue();
                    hmj.remove("MAIL");
                    hmj.put("DITTA", new JdbcParametro(JdbcParametro.TIPO_TESTO, mail));
                  }
                }//listaImprese
              }


              //inserisco gli attributi per wsallegati e per gardoc_wsdm
              String idprg = null;
              Long iddocdig = null;
              Long idwsdoc = null;
              Long stato_archiviazione = null;
              String esito = null;
              JdbcParametro jdbcIdprg = (JdbcParametro) hmj.get("IDPRG");
              if (jdbcIdprg != null){
                idprg = jdbcIdprg.stringValue();
              }

              JdbcParametro jdbcIddocdig = (JdbcParametro) hmj.get("IDDOCDIG");
              if (jdbcIddocdig != null){
                iddocdig = jdbcIddocdig.longValue();
              }
              HashMap hmAllegato = new HashMap();

              Vector<?> datiAllegati = this.sqlManager.getVector("select ws.idwsdoc from wsallegati ws  where ws.key1 = ? and ws.key2 = ? and ws.entita='W_DOCDIG'", new Object[]{idprg,iddocdig});
              if (datiAllegati != null && datiAllegati.size()>0) {
                idwsdoc = (Long) SqlManager.getValueFromVectorParam(datiAllegati, 0).getValue();
              }

              String selectGARDOC_WSDM = null;
              selectGARDOC_WSDM = "select gwsdm1.stato_archiviazione,gwsdm1.esito from gardoc_wsdm gwsdm1 where gwsdm1.entita='W_DOCDIG' and gwsdm1.key1 = ? and gwsdm1.key2 = ? order by id_archiviazione desc";
              List<?> listaDatiGardocWsdm = this.sqlManager.getListVector(selectGARDOC_WSDM, new Object[]{idprg,iddocdig});

              if (listaDatiGardocWsdm != null && listaDatiGardocWsdm.size()>0) {
                stato_archiviazione = (Long) SqlManager.getValueFromVectorParam(listaDatiGardocWsdm.get(0), 0).getValue();
                esito = SqlManager.getValueFromVectorParam(listaDatiGardocWsdm.get(0), 1).getStringValue();
              }

              Long stato_archiviazione_cos = null;
              String selectGARDOC_COS = null;
              selectGARDOC_COS = "select gwsdm1.stato_archiviazione,gwsdm1.esito from gardoc_wsdm gwsdm1 join gardoc_jobs job on gwsdm1.id_archiviazione = job.id_archiviazione where job.tipo_archiviazione = 3 and gwsdm1.entita='W_DOCDIG' and gwsdm1.key1 = ? and gwsdm1.key2 = ? order by gwsdm1.id_archiviazione desc";
              List<?> listaDatiGardocCOS = this.sqlManager.getListVector(selectGARDOC_COS,
                  new Object[] { idprg, iddocdig });

              if (listaDatiGardocCOS != null && listaDatiGardocCOS.size() > 0) {
                stato_archiviazione_cos = (Long) SqlManager
                    .getValueFromVectorParam(listaDatiGardocCOS.get(0), 0).getValue();
              }

              hmj.put("IDWSDOC", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, idwsdoc));
              hmj.put("STATO", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, stato_archiviazione));
              hmj.put("STATO_COS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, stato_archiviazione_cos));
              hmj.put("PROVENIENZA", new JdbcParametro(JdbcParametro.TIPO_TESTO, "6"));
              hmj.put("ESITO", new JdbcParametro(JdbcParametro.TIPO_TESTO, esito));


              hmDocComDallaDitta.set(j, hmj);

              hmDocGara.add(hmDocComDallaDitta.get(j));

            }//for


          }



          if (hmDocGara != null && hmDocGara.size() > 0) {
            total = hmDocGara.size();
            totalAfterFilter = hmDocGara.size();
          }

          ArrayList<String> formatiCOS = new ArrayList<String>();
          ArrayList<String> formatiCOSFirmaDigitale = new ArrayList<String>();
          String _formati = ConfigManager.getValore("cos.formatiConsentiti");
          if (_formati != null) {
            formatiCOS = new ArrayList<String>(Arrays.asList(_formati.split(",")));
          }
          String _formatiFirmaDigitale = ConfigManager.getValore("cos.formatiConsentitiFirmaDigitale");
          if (_formatiFirmaDigitale != null) {
            formatiCOSFirmaDigitale = new ArrayList<String>(Arrays.asList(_formatiFirmaDigitale.split(",")));
          }
          for (int o = 0; o < hmDocGara.size(); o++) {
            HashMap hm = hmDocGara.get(o);
            Long stato = null;
            JdbcParametro parametrodata = (JdbcParametro) hm.get("DATA");
            if (parametrodata.getValue() == null) {
              stato = new Long(-1);
            }

            String nomeFile = ((JdbcParametro) hm.get("DIGNOMDOC")).stringValue();
            Boolean fine = false;
            while (true)
            {
                if (nomeFile != null) {
                    if (nomeFile.indexOf(".") >= 0) {
                        String estensione = nomeFile.substring(nomeFile.lastIndexOf(".")).toLowerCase().replace(".","");
                        if (!formatiCOS.contains(estensione) && formatiCOSFirmaDigitale.contains(estensione)) {
                          //estensione per firma digitale,la ignoro e vado avanti
                          nomeFile = nomeFile.substring(0,nomeFile.lastIndexOf("."));
                        }
                        else
                        {
                            //qui mi aspetto l'estensione originale
                            if (!formatiCOS.contains(estensione))
                            {
                                stato = new Long(-2);
                            }
                            break;
                        }
                    } else break;
                } else break;
            }
    // se non esiste la data o l'estensione non e' ammessa, override stato
            if (stato != null) {
              hm.put("STATO_COS", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, stato));
            }
          }

          result.put("iTotalRecords", total);
          result.put("iTotalDisplayRecords", totalAfterFilter);
          result.put("data", hmDocGara);

    } catch (SQLException e) {
      throw new JspException("Errore durante la selezione dei documenti della gara da archiviare", e);
    }

    out.println(result);
    out.flush();

    return null;

  }

  public String escape(String s) {
    return s.replace('|', '-').replace(';', '-');
}

  public String impronta(byte[] buffer) throws NoSuchAlgorithmException, IOException {
    int count;
    MessageDigest digest = MessageDigest.getInstance("SHA-256");

    java.io.BufferedInputStream bis = new java.io.BufferedInputStream(new java.io.ByteArrayInputStream(buffer));
    while ((count = bis.read(buffer)) > 0) {
        digest.update(buffer, 0, count);
    }
    bis.close();

    byte[] hash = digest.digest();
//return new BASE64Encoder().encode(hash);
//return new sun.misc.HexDumpEncoder().encode(hash);
    return javax.xml.bind.DatatypeConverter.printHexBinary(hash);
}

}

