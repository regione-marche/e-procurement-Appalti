package it.eldasoft.sil.pg.web.struts;

import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.maggioli.eldasoft.ws.dm.WSDMFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMInserimentoInFascicoloType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAnagraficaType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoInType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloInOutType;
import net.sf.json.JSONObject;

public class SetRichiestaFirmaAction extends Action {

  private SqlManager sqlManager;

  private GestioneWSDMManager gestioneWSDMManager;

  private FileAllegatoManager fileAllegatoManager;

  private GenChiaviManager    genChiaviManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
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

    String codice = request.getParameter("codice");
    String classifica = request.getParameter("classifica");
    String anno = request.getParameter("anno");
    String oggetto = request.getParameter("oggetto");

    String firmatario = request.getParameter("firmatario");
    String ufficiofirmatario = request.getParameter("ufficiofirmatario");
    String idconfi =  request.getParameter("idconfi");
    String idprg =  request.getParameter("idprg");
    String iddocdig =  request.getParameter("iddocdig");
    String syscon = request.getParameter("syscon");

    WSDMProtocolloDocumentoInType wsdmProtocolloDocumentoIn = new WSDMProtocolloDocumentoInType();
    wsdmProtocolloDocumentoIn.setClassifica(classifica);
    wsdmProtocolloDocumentoIn.setOggetto(oggetto);
    wsdmProtocolloDocumentoIn.setInout(WSDMProtocolloInOutType.fromString("OUT"));
    wsdmProtocolloDocumentoIn.setGenericS41(firmatario);
    wsdmProtocolloDocumentoIn.setGenericS42(ufficiofirmatario);

    wsdmProtocolloDocumentoIn.setInserimentoInFascicolo(WSDMInserimentoInFascicoloType.SI_FASCICOLO_ESISTENTE);
    WSDMFascicoloType wsdmFascicolo = new WSDMFascicoloType();
    wsdmFascicolo.setCodiceFascicolo(codice);
    wsdmFascicolo.setClassificaFascicolo(classifica);
    anno = UtilityStringhe.convertiNullInStringaVuota(anno);
    if (!"".equals(anno)) wsdmFascicolo.setAnnoFascicolo(new Long(anno));
    wsdmProtocolloDocumentoIn.setFascicolo(wsdmFascicolo);

    WSDMProtocolloAllegatoType[] allegati = null;
    String selectW_DOCDIG = "select digdesdoc, dignomdoc from w_docdig where idprg = ? and iddocdig = ?";
    Vector<?> datiW_DOCDIG = sqlManager.getVector(selectW_DOCDIG, new Object[] { idprg, new Long(iddocdig)});
    if(datiW_DOCDIG!=null && datiW_DOCDIG.size()>0) {
      allegati = new WSDMProtocolloAllegatoType[1];
      String digdesdoc = (String) SqlManager.getValueFromVectorParam(datiW_DOCDIG, 0).getValue();
      String dignomdoc = (String) SqlManager.getValueFromVectorParam(datiW_DOCDIG, 1).getValue();
      String tipo = "";
      int index = dignomdoc.lastIndexOf('.');
      if (index > 0) {
        tipo = dignomdoc.substring(index + 1);
      }
      if(digdesdoc==null)
        digdesdoc="";
      allegati[0] = new WSDMProtocolloAllegatoType();
      allegati[0].setNome(dignomdoc);
      allegati[0].setTitolo(digdesdoc);
      allegati[0].setTipo(tipo);
      BlobFile digogg = fileAllegatoManager.getFileAllegato(idprg, new Long(iddocdig));
      allegati[0].setContenuto(digogg.getStream());
    }
    wsdmProtocolloDocumentoIn.setAllegati(allegati);

    String destinatarioFinto = ConfigManager.getValore("wsdm.firmaDocumenti.destinatario." + idconfi);
    WSDMProtocolloAnagraficaType[] destinatari = new WSDMProtocolloAnagraficaType[1];
    destinatari[0] = new WSDMProtocolloAnagraficaType();
    destinatari[0].setCognomeointestazione(destinatarioFinto);
    destinatari[0].setCodiceFiscale(" ");
    /*
    destinatari[i].setCodiceFiscale(codiceFiscale);
    destinatari[i].setPartitaIVA(piva);
    destinatari[i].setIndirizzoResidenza(indirizzoResidenza);
    destinatari[i].setComuneResidenza(comuneResidenza);
    destinatari[i].setCodiceComuneResidenza(codiceComuneResidenza);
    destinatari[i].setEmail(desmail);
    destinatari[i].setEmailAggiuntiva(emailAggiuntiva);
    destinatari[i].setCapResidenza(cap);
    destinatari[i].setProvinciaResidenza(provincia);
    */
    wsdmProtocolloDocumentoIn.setDestinatari(destinatari);

    WSDMProtocolloDocumentoResType docRes = this.gestioneWSDMManager.wsdmFirmaInserisci(username, password, wsdmProtocolloDocumentoIn, idconfi);
    result.put("esito", docRes.isEsito());
    result.put("messaggio", docRes.getMessaggio());

    if (docRes.isEsito()) {
      String numDoc = docRes.getProtocolloDocumento().getNumeroDocumento();

      TransactionStatus status = null;
      boolean commit = true;

      try {
        status = this.sqlManager.startTransaction();
        String insertSql = "insert into WSRICFIRMA(id,idprg,iddocdig,numerodoc,syscon,idconfiwsdm) values(?,?,?,?,?,?)";
        String wsdmLoginComune = ConfigManager.getValore(GestioneWSDMManager.PROP_WSDM_LOGIN_COMUNE+idconfi);
        if (wsdmLoginComune != null && "1".equals(wsdmLoginComune))
          syscon="-1";
        int id = this.genChiaviManager.getNextId("WSRICFIRMA");
        this.sqlManager.update(insertSql, new Object[] {new Long(id), idprg, new Long(iddocdig), numDoc, new Long(syscon), new Long(idconfi) });
        this.sqlManager.update("update w_docdig set digfirma='1' where idprg = ? and iddocdig = ?", new Object[] {idprg, new Long(iddocdig) });
      } catch (Throwable t) {
        commit = false;
        result.put("esito", false);
        result.put("messaggio", t.getMessage());
      } finally {
        if (status != null) {

          if (commit == true) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }

        }
      }
    }




    out.println(result);
    out.flush();


    return null;
  }


}
