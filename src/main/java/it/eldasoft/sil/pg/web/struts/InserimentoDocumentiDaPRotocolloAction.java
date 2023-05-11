package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.sil.pg.bl.PgManagerEst1;
import it.eldasoft.utils.utility.UtilityDate;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoType;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

public class InserimentoDocumentiDaPRotocolloAction extends Action {

  private SqlManager sqlManager;

  private PgManagerEst1 pgManagerEst1;

  private GestioneWSDMManager gestioneWSDMManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }


  public void setPgManagerEst1(PgManagerEst1 pgManagerEst1) {
    this.pgManagerEst1 = pgManagerEst1;
  }

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    String username = request.getParameter("username");
    String password = request.getParameter("password");

    String servizio = request.getParameter("servizio");
    String idconfi = request.getParameter("idconfi");

    String profilo = request.getParameter("profilo");

    String codiceGara = request.getParameter("codiceGara");
    String ngara = request.getParameter("ngara");
    String genereGara = request.getParameter("genereGara");
    String gruppo = request.getParameter("gruppo");
    String tipologiaDoc = request.getParameter("tipologiaDoc");

    String entita = request.getParameter("entita");
    String key1 = request.getParameter("key1");

    String codiceFascicolo = request.getParameter("codicefascicolo");
    String annoFascicolo = request.getParameter("annofascicolo");
    String numeroFascicolo = request.getParameter("numerofascicolo");
    String classifica = request.getParameter("classifica");
    String classificaDescrizione = request.getParameter("classificadescrizione");
    String codiceAoo = request.getParameter("codiceaoo");
    String codiceUfficio = request.getParameter("codiceufficio");
    String codiceAooDes = request.getParameter("codiceaoodes");
    String codiceUfficioDes = request.getParameter("codiceufficiodes");
    String voce = request.getParameter("voce");

    String numeroDocumento = request.getParameter("numerodocumento");
    String annoProtocollo = request.getParameter("annoprotocollo");
    String numeroProtocollo = request.getParameter("numeroprotocollo");
    String oggettoDocumento = request.getParameter("oggettodocumento");
    String inout = request.getParameter("inout");
    String dataprov = request.getParameter("dataprov");
    String numprov = request.getParameter("numprov");
    String sso = request.getParameter("sso");
    String utenteSso = request.getParameter("utenteSso");

    String associareFascicolo = request.getParameter("associarefascicolo");

    String fascicoliUguali = request.getParameter("fascicoliUguali");

    int livEvento = 1;
    String errMsgEvento = null;
    String descr ="Inserimento documenti di gara da protocollo (n.protocollo: " + numeroProtocollo + ", id.doc.: ";

    if("".equals(ngara))
      ngara=null;

    if("".equals(dataprov))
      dataprov=null;

    if("".equals(numprov))
      numprov=null;

    TransactionStatus status = null;
    boolean commitTransaction = false;
    try{
      boolean ssoBool = false;
      if("true".equals(sso))
        ssoBool = true;

      WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = this.gestioneWSDMManager.wsdmProtocolloLeggi(username, password, null,
          null, null, null, null, null, new Long(annoProtocollo), numeroProtocollo, servizio,idconfi,profilo,ssoBool,utenteSso);

      if (wsdmProtocolloDocumentoRes.isEsito()) {
        WSDMProtocolloDocumentoType wsdmProtocolloDocumento = wsdmProtocolloDocumentoRes.getProtocolloDocumento();
        if (wsdmProtocolloDocumento != null) {
          if (wsdmProtocolloDocumento.getAllegati() != null) {
            WSDMProtocolloAllegatoType[] allegati = wsdmProtocolloDocumento.getAllegati();
            String nomeFile = null;
            String descrizione = null;
            String select = null;
            String insert ="insert into DOCUMGARA( TIPOLOGIA, NGARA, DESCRIZIONE, IDPRG, IDDOCDG, GRUPPO, NORDDOCG, CODGAR, VALENZA, DATAPROV, NUMPROV) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
            Long nProgressivoW_DOCDIG = null;
            Long newProgressivoW_DOCDIG = null;
            Vector<DataColumn> elencoCampiW_DOCDIG =null;
            Long norddocg=null;
            Object par [] = new Object[11];
            par[0]= new Long(tipologiaDoc);  //tipologia
            par[1]= ngara;                    //ngara
            par[3]= "PG";                     //idprg
            par[5]= new Long(gruppo);   //gruppo
            par[7]= codiceGara;   //codgar
            par[8]= new Long(0);   //valenza
            par[9]= UtilityDate.convertiData(dataprov, UtilityDate.FORMATO_GG_MM_AAAA);   //dataprov
            par[10]= numprov;   //numprov

            byte[] file = null;

            String vetProgressiviW_docig[] = new String[allegati.length];
            status = this.sqlManager.startTransaction();

            String digkey1 = codiceGara;

            for (int a = 0; a < allegati.length; a++) {
              descrizione = allegati[a].getTitolo();
              nomeFile = allegati[a].getNome();
              file = allegati[a].getContenuto();
              if(nomeFile!=null ){
                nomeFile = nomeFile.replaceAll("\\[", "(");
                nomeFile = nomeFile.replaceAll("\\]", ")");
                if(nomeFile.length() >100){
                  String splitTMP[] = nomeFile.split("\\.");
                  if(splitTMP.length>0){
                    int pos = splitTMP.length - 1;
                    String tipo = splitTMP[pos];
                    nomeFile = nomeFile.substring(0, 98 - tipo.length()) + "." + tipo;
                  }

                }
              }

              par[2]= descrizione;   //descrizione

              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              baos.write(file);

              //Inserimento in ws_docdig
              select = "SELECT MAX(iddocdig) FROM w_docdig WHERE idprg = 'PG'";
              nProgressivoW_DOCDIG = (Long) this.sqlManager.getObject(select, null);

              if (nProgressivoW_DOCDIG == null) {
                nProgressivoW_DOCDIG = new Long(0);
              }

              newProgressivoW_DOCDIG = nProgressivoW_DOCDIG + 1;

              if(a > 0)
                descr +=", ";
              descr +=newProgressivoW_DOCDIG.toString();

              vetProgressiviW_docig[a] = newProgressivoW_DOCDIG.toString();

              elencoCampiW_DOCDIG = new Vector<DataColumn>();

              elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.IDPRG",
                  new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")));
              elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.IDDOCDIG",
                              new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newProgressivoW_DOCDIG)));
              elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.DIGENT",
                              new JdbcParametro(JdbcParametro.TIPO_TESTO, "DOCUMGARA")));
              elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.DIGNOMDOC",
                              new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeFile)));
              elencoCampiW_DOCDIG.add(new DataColumn("W_DOCDIG.DIGOGG",
                              new JdbcParametro(JdbcParametro.TIPO_BINARIO, baos)));

              DataColumnContainer containerW_DOCDIG = new DataColumnContainer(elencoCampiW_DOCDIG);

              containerW_DOCDIG.insert("W_DOCDIG", sqlManager);

              //Inserimento in DOCUMGARA
              par[4]= newProgressivoW_DOCDIG;   //iddocdg

              select="select max(NORDDOCG) from DOCUMGARA where CODGAR = ?";
              norddocg = (Long)this.sqlManager.getObject(select, new Object[]{codiceGara});
              if (norddocg == null) {
                norddocg = new Long(0);
              }
              norddocg = new Long(norddocg.longValue() + 1);

              par[6]= norddocg;   //norddocg
              this.sqlManager.update(insert, par);

              this.sqlManager.update(
                  "update W_DOCDIG set DIGKEY1=?, DIGKEY2=? where IDPRG=? and IDDOCDIG=?",
                  new Object[] {digkey1,  norddocg.toString(), "PG", newProgressivoW_DOCDIG });
            }

            ///////////////////////////////////////////////////////////////////////////////
            //Ricalcolo NUMORD.DOCUMGARA

            pgManagerEst1.ricalcNumordDocGara(codiceGara, new Long(gruppo));
            ////////////////////////////////////////////////////////////////////////////////


            //Salvataggio del fascicolo
            if("true".equals(associareFascicolo))
              this.gestioneWSDMManager.setWSFascicolo(entita, key1, null, null, null, codiceFascicolo, new Long(annoFascicolo),
                  numeroFascicolo, classifica, codiceAoo, codiceUfficio,null,null,classificaDescrizione,voce,codiceAooDes,codiceUfficioDes);

            if("true".equals(associareFascicolo) || "true".equals(fascicoliUguali)){
              //Salvatagio in WSDOCUMENTO
              Long idWSDocumento = this.gestioneWSDMManager.setWSDocumento(entita, key1, null, null, null, numeroDocumento, new Long(annoProtocollo), numeroProtocollo, oggettoDocumento,inout);

              //Salvataggio dei documenti in WSALLEGATI
              for (int a = 0; a < allegati.length; a++) {
                this.gestioneWSDMManager.setWSAllegati("W_DOCDIG", "PG", vetProgressiviW_docig[a], null, null, idWSDocumento);
              }
            }
            commitTransaction = true;
          }
        }
      }
    }catch(Exception e){
      commitTransaction = false;
      livEvento = 3;
      errMsgEvento = e.getMessage();
      throw e;
    }finally{
      if (status != null) {
        if (commitTransaction) {
          this.sqlManager.commitTransaction(status);
        } else {
          this.sqlManager.rollbackTransaction(status);
        }
      }

      descr +=")";
      String codice= ngara;
      if(!"2".equals(genereGara))
        codice = codiceGara;
      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setLivEvento(livEvento);
      logEvento.setOggEvento(codice);
      logEvento.setCodEvento("GA_WSDM_DOCDAPROTOCOLLO");
      logEvento.setDescr(descr);
      logEvento.setErrmsg(errMsgEvento);
      LogEventiUtils.insertLogEventi(logEvento);

    }



    out.println(result);
    out.flush();


    return null;
  }


}
