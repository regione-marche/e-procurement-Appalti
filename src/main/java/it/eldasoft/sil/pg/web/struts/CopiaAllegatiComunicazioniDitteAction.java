package it.eldasoft.sil.pg.web.struts;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import net.sf.json.JSONObject;

public class CopiaAllegatiComunicazioniDitteAction extends Action {

  private static String selectW_DOCDIG = "select DIGDESDOC, DIGNOMDOC from W_DOCDIG where IDPRG = ? AND IDDOCDIG = ?";
  private static String selectW_INVOCM = "select COMNUMPROT, COMMSGOGG, COMTIPMA from W_INVCOM where IDPRG = ? AND IDCOM = ? ";
  private static String updateW_DOCDIG="update w_docdig set digkey1=?, digkey2=? where idprg=? and iddocdig=?";
  String insertIMPRDOCG="insert into imprdocg(codgar,ngara, codimp,proveni,doctel,norddoci,busta,descrizione,datarilascio,orarilascio,idprg,iddocdg) "
      + "values(?,?,?,?,?,?,?,?,?,?,?,?)";


  private SqlManager sqlManager;
  private FileAllegatoManager fileAllegatoManager;
  private TabellatiManager tabellatiManager;

  /**
   * @param sqlManager
   *        the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * @param fileAllegatoManager
   *        the fileAllegatoManager to set
   */
  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  /**
   * @param fileAllegatoManager
   *        the fileAllegatoManager to set
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }


  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    String ngara = request.getParameter("ngara");
    String codgar = request.getParameter("codgar");
    String ditta = request.getParameter("ditta");
    String genereGara = request.getParameter("genereGara");
    String idprg = request.getParameter("idprg");
    String idprgCom = request.getParameter("idprgCom");
    String iddocdig = request.getParameter("iddocdig");
    String idcom = request.getParameter("idcom");
    String comdatins = request.getParameter("comdatins");
    String soccorsoIstruttorio = request.getParameter("soccorsoIstruttorio");
    String bustaString = request.getParameter("busta");
    String bustaDescr = request.getParameter("bustaDescr");
    Long busta = null;
    if (bustaString != null && !"".equals(bustaString)) {
      busta = new Long(bustaString);
    }

    Long iddocdigLong = new Long(iddocdig);
    BlobFile fileAllegato = null;
    ByteArrayOutputStream baos = null;

    int livEvento = 1;
    String errMsgEvento = "";

    TransactionStatus status = null;
    boolean commit = true;

    try {

      List<?> allegati = this.sqlManager.getListVector(selectW_DOCDIG,
          new Object[]{idprg, iddocdigLong});
      if (allegati != null && allegati.size() > 0) {
        Long newIddocdg = null;
        String digdescdoc = null;
        String dignomdoc = null;
        Long maxNorddoci = null;
        String descrizione = null;
        Vector<?> datiW_invcom = null;
        String comnumprot = null;
        String comsogg = null;
        Long comtipma = null;
        String inizioDescDoc = "(Comunicazione ricevuta";
        Date dataRilascio = null;
        String oraRilascio = null;
        if ("1".equals(soccorsoIstruttorio)) {
          String descTab = tabellatiManager.getDescrTabellato("W_008", soccorsoIstruttorio);
          inizioDescDoc = "(" + descTab ;
        }

        //Estrazione data ed ora da comdatins
        if (comdatins != null && !"".equals(comdatins)) {
          dataRilascio = new Date(new Long(comdatins).longValue());
          Format formatter = new SimpleDateFormat("HH:mm:ss");
          oraRilascio = formatter.format(dataRilascio);
        }


        status = this.sqlManager.startTransaction();

        for (int i = 0; i < allegati.size(); i++) {
          digdescdoc = SqlManager.getValueFromVectorParam(allegati.get(i), 0).getStringValue();
          dignomdoc = SqlManager.getValueFromVectorParam(allegati.get(i), 1).getStringValue();

          fileAllegato = fileAllegatoManager.getFileAllegato(idprg,iddocdigLong);

          if (fileAllegato != null && fileAllegato.getStream() != null) {
            baos = new ByteArrayOutputStream();
            baos.write(fileAllegato.getStream());
          } else {
            baos = null;
          }

          newIddocdg = (Long) sqlManager.getObject("select max(IDDOCDIG) + 1 from "
              + "W_DOCDIG where IDPRG='PG'", null);

          //Inserimento occorrenza in W_DOCDIG
          DataColumnContainer dccW_docdig = new DataColumnContainer(new DataColumn[] {
              new DataColumn("W_DOCDIG.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")),
              new DataColumn("W_DOCDIG.IDDOCDIG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newIddocdg)),
              new DataColumn("W_DOCDIG.DIGENT", new JdbcParametro(JdbcParametro.TIPO_TESTO, "IMPRDOCG")),
              new DataColumn("W_DOCDIG.DIGNOMDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, dignomdoc)),
              new DataColumn("W_DOCDIG.DIGOGG", new JdbcParametro(JdbcParametro.TIPO_BINARIO, baos))});
          dccW_docdig.insert("W_DOCDIG", this.sqlManager);


          //NORDDOCI
          maxNorddoci = (Long) sqlManager.getObject("select max(NORDDOCI) from IMPRDOCG "
              + "where CODGAR=? and CODIMP=?", new Object[] {codgar, ditta });

          if (maxNorddoci == null) {
            maxNorddoci = new Long(0);
          }
          maxNorddoci = new Long(maxNorddoci.longValue() + 1);


          datiW_invcom = sqlManager.getVector(selectW_INVOCM, new Object[]{idprgCom, new Long(idcom)});
          if (datiW_invcom != null) {
            comnumprot = SqlManager.getValueFromVectorParam(datiW_invcom, 0).getStringValue();
            comsogg = SqlManager.getValueFromVectorParam(datiW_invcom, 1).getStringValue();
            comtipma = SqlManager.getValueFromVectorParam(datiW_invcom, 2).longValue();
            if(comtipma!=null)
              busta = comtipma;
          }
          //Descrizione
          descrizione = inizioDescDoc;
          if(comnumprot != null && !"".equals(comnumprot))
            descrizione += " - n.prot.: " + comnumprot ;
          descrizione += ") " + comsogg + "-" + digdescdoc;
          if (descrizione.length() > 2000) {
            descrizione = descrizione.substring(0, 2000);
          }

          //Inserimento occorrenza in IMPRDOCG
          this.sqlManager.update(insertIMPRDOCG, new Object[]{codgar, ngara, ditta,
              new Long(2), "2", maxNorddoci, busta, descrizione, dataRilascio, oraRilascio,
              "PG", newIddocdg});

          // aggiornamento di W_DOCDIG con i riferimenti della nuova occorrenza in IMPRDOCG
          this.sqlManager.update(updateW_DOCDIG, new Object[]{codgar, maxNorddoci,
              "PG", newIddocdg});
        }
      }


    } catch (Exception e) {
      livEvento = 3;
      errMsgEvento = e.getMessage();
      commit = false;

    } finally {
      if (status != null) {
        try {
          if (commit == true) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        } catch (SQLException e) {
          livEvento = 3;
          errMsgEvento = e.getMessage();
        }
      }

      String oggettoEvento = ngara;
      if ("1".equals(genereGara) || "3".equals(genereGara)) {
        oggettoEvento = codgar;
      }

      String msgDescr = "Copia allegato comunicazione ricevuta nei documenti";
      if (bustaDescr != null && !"".equals(bustaDescr)) {
        msgDescr += " della busta " + bustaDescr;
      }
      msgDescr += " della ditta (id.allegato:" + idprg + '/' + idcom + ")";

      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setLivEvento(livEvento);
      logEvento.setOggEvento(oggettoEvento);
      logEvento.setCodEvento("GA_COPIA_ALLEGATI_RICEVUTI");
      logEvento.setDescr(msgDescr);
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);
      String esito = "1";
      if (livEvento == 3) {
        esito = "-1";
      }
      result.put("esito", esito);
    }

    out.println(result);
    out.flush();

    return null;

  }
}
