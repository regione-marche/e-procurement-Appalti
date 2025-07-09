/*
 * Created on 17/apr/2018
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.sil.pg.bl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import it.eldasoft.gene.ws.objectstorage.ObjectStorageUtils;
import it.eldasoft.gene.ws.objectstorage.UploadInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xml.security.utils.Base64;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.simap.ws.xmlbeans.AuthorityAType;
import it.eldasoft.simap.ws.xmlbeans.AuthorityType;
import it.eldasoft.simap.ws.xmlbeans.AvvisoAggiudicazioneDocument;
import it.eldasoft.simap.ws.xmlbeans.AvvisoAggiudicazioneLottoType;
import it.eldasoft.simap.ws.xmlbeans.AvvisoAggiudicazioneSettoriSpecialiDocument;
import it.eldasoft.simap.ws.xmlbeans.AvvisoAggiudicazioneSettoriSpecialiType;
import it.eldasoft.simap.ws.xmlbeans.AvvisoAggiudicazioneType;
import it.eldasoft.simap.ws.xmlbeans.AvvisoPeriodicoIndicativoDocument;
import it.eldasoft.simap.ws.xmlbeans.AvvisoPeriodicoIndicativoType;
import it.eldasoft.simap.ws.xmlbeans.AvvisoPreinformazioneDocument;
import it.eldasoft.simap.ws.xmlbeans.AvvisoPreinformazioneType;
import it.eldasoft.simap.ws.xmlbeans.AvvisoProfiloCommittenteDocument;
import it.eldasoft.simap.ws.xmlbeans.AvvisoProfiloCommittenteType;
import it.eldasoft.simap.ws.xmlbeans.BandoGaraDocument;
import it.eldasoft.simap.ws.xmlbeans.BandoGaraSettoriSpecialiDocument;
import it.eldasoft.simap.ws.xmlbeans.BandoGaraSettoriSpecialiType;
import it.eldasoft.simap.ws.xmlbeans.BandoGaraType;
import it.eldasoft.simap.ws.xmlbeans.CPVType;
import it.eldasoft.simap.ws.xmlbeans.CriteriaType;
import it.eldasoft.simap.ws.xmlbeans.EconomicOperatorType;
import it.eldasoft.simap.ws.xmlbeans.Empty;
import it.eldasoft.simap.ws.xmlbeans.LottoAggiudicatoType;
import it.eldasoft.simap.ws.xmlbeans.LottoCommonType;
import it.eldasoft.simap.ws.xmlbeans.SnType;
import it.eldasoft.simap.ws.xmlbeans.W3Z40Type;
import it.eldasoft.simap.ws.xmlbeans.W3Z45Type;
import it.eldasoft.simap.ws.xmlbeans.W3Z46PreType;
import it.eldasoft.simap.ws.xmlbeans.W3Z46Type;
import it.eldasoft.simap.ws.xmlbeans.W3Z54Type;
import it.eldasoft.simap.ws.xmlbeans.W3Z61Type;
import it.eldasoft.simap.ws.xmlbeans.W3Z72Type;
import it.eldasoft.simap.ws.xmlbeans.W3Z74Type;
import it.eldasoft.simap.ws.xmlbeans.W3Z78Type;
import it.eldasoft.simap.ws.xmlbeans.W3Z79Type;
import it.eldasoft.simap.ws.xmlbeans.W3Z80Type;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityFiscali;
import it.eldasoft.utils.utility.UtilityMath;
import it.eldasoft.utils.utility.UtilityNumeri;
import net.sf.json.JSONObject;


public class InviaBandoAvvisoSimapManager {

  static Logger               logger                         = Logger.getLogger(InviaBandoAvvisoSimapManager.class);

  private static final String PROP_BANDO_AVVISO_SIMAP_WS_URL = "it.eldasoft.bandoavvisosimap.ws.url";
  private static final String PROP_URL_BANDI = "invioScp.urlBandiPortaleAppalti";
  private static final String PROP_URL_AVVISI = "invioScp.urlAvvisiPortaleAppalti";
  private static final String PROP_URL_PUBBLICA = "portaleAppalti.urlPubblica";
  private static final String PROP_URL_PROCEDURE = "invioScp.urlProcedurePortaleAppalti";

  public static final String PROP_TOKEN_ACCESSO_FEU = "feu.token";

  private SqlManager          sqlManager;
  private GenChiaviManager    genChiaviManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  /**
   * funzione che viene invocata quando il controllo sugli errori è già stato eseguito, e non ha rivelato errori bloccanti
   *
   * @param codgar
   * @param formulario
   * @param username
   * @param password
   * @param xml
   * @return
   * @throws Exception
   * @throws Throwable
   */
  public String inviaBandoAvvisoSimap(String codgar, Long idForm, String formulario, BigInteger sottoTipo,
      String username, String password, HttpServletRequest request, boolean usaToken) throws Exception, Throwable {

    if (logger.isDebugEnabled())
      logger.debug("InviaBandoAvvisoSimap2018: inizio metodo");

    String settore = (String) sqlManager.getObject(
        "select settore from torn where codgar = ?",
        new Object[] { codgar });
    Long tipoAvvGareAvvisi = (Long) sqlManager.getObject(
            "select tipoavv from gareavvisi where codgar = ?",
            new Object[] { codgar });

    String xml = null;
    String xmlEnc = null;
    String authStringEnc = null;
    HttpURLConnection conn = null;
    String authString = username + ":" + password;
    byte[] authByte = authString.getBytes("UTF-8");
    authStringEnc = DatatypeConverter.printBase64Binary(authByte);

    String urlString = ConfigManager.getValore(PROP_BANDO_AVVISO_SIMAP_WS_URL);
    if (urlString == null || "".equals(urlString)) {
      throw new GestoreException(
          "L'indirizzo per la connessione al web service non e' definito",
          "inviabandoavvisosimap.ws.url");
    }
    if(urlString.endsWith("/"))
      urlString=urlString.substring(0, urlString.length()-1);

    String numeroEFORM = null;
	Long idScheda = null;
	String uuidEForm = null;

    try {
		if (idForm != null) {
			Vector<?> datiScheda = sqlManager.getVector("select g1npascheda.id, g1cf_npa.eform"
	    			+ " from g1npaform,g1cf_npa,g1npascheda"
	    			+ " where g1npaform.idcfnpa=g1cf_npa.id and g1npaform.id=g1npascheda.idform"
	    			+ " and g1npaform.id=?", new Object[] { idForm });

	    	idScheda = ((JdbcParametro) datiScheda.get(0)).longValue();
	    	numeroEFORM = ((JdbcParametro) datiScheda.get(1)).getStringValue();

			if (numeroEFORM == null) {
				throw new Exception("Numero EFORM è nullo");
			}
			switch (Integer.parseInt(numeroEFORM)) {
			case 1: case 2:
				formulario = "FS8";
				break;

			case 16: case 17: case 23: case 24:
				formulario = "FS2";
				break;

			case 4: case 5: case 7: case 8: case 10: case 11:
				formulario = "FS1";
				switch (Integer.parseInt(numeroEFORM)) {
				case 4: case 5:
					sottoTipo = new BigInteger("1");
					break;
				case 7: case 8:
					sottoTipo = new BigInteger("2");
					break;
				case 10: case 11:
					sottoTipo = new BigInteger("3");
					break;
				}
				break;

			case 29: case 30: case 36: case 37:
				formulario = "FS3";
				break;
			}
		}

      if ("FS1".equals(formulario)) {

        xml = this.getXMLAvvisoSettore(codgar, formulario, sottoTipo,settore,numeroEFORM,idScheda);
        if("S".equals(settore) || ((Long) 2L).equals(tipoAvvGareAvvisi)){
          urlString = urlString + "/avvisoperiodicoindicativo";
        }else{
          urlString = urlString + "/avvisopreinformazione";
        }
      }
      else if ("FS2".equals(formulario)) {

         xml = this.getXMLBandoGaraSettore(codgar, formulario, settore,numeroEFORM,idScheda);
         if("S".equals(settore)){
           urlString = urlString + "/bandogarasettorispeciali";
         }else{
           urlString = urlString + "/bandogara";
         }
      }
      else if ("FS3".equals(formulario)) {

         xml = this.getXMLAvvisoAggiudicazioneSettore(codgar, formulario,settore,numeroEFORM,idScheda);
         if("S".equals(settore)){
           urlString = urlString + "/avvisoaggiudicazionesettorispeciali";
         }else{
           urlString = urlString + "/avvisoaggiudicazione";
         }
      }
      else if ("FS8".equals(formulario)) {
         urlString = urlString + "/avvisoprofilocommittente";
         xml = this.getXMLAvvisoProfiloCommittente(codgar, formulario, settore,numeroEFORM,idScheda);
      }

      if (usaToken) {
          urlString += "token";
      }

      URL url = new URL(urlString);
      conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Authorization", "Basic " + authStringEnc);

      byte[] xmlByte = xml.getBytes("UTF-8");
      xmlEnc = DatatypeConverter.printBase64Binary(xmlByte);

      if (idForm != null) {
    	String codiceGara = codgar;
  		if (codiceGara.startsWith("$")) {
  			codiceGara = codiceGara.substring(1);
  		}

    	int livEvento = 1;
		String oggEvento = codiceGara;
		String codEvento = "GA_FEU_REQ_INVIODATI";
        uuidEForm = (String) sqlManager.getObject("select uuid_eform from g1npascheda where id=?", new Object[] {idScheda});
		String descrEvento = "Invio richiesta a FEU - NUOVO EFORM " + numeroEFORM + " (id.avviso " + uuidEForm + " - id.scheda " + idScheda + ")";
		String errMsgEvento = xml;

    	LogEvento logEvento = null;
		if (request == null) {
			logEvento = new LogEvento();
		} else {
			logEvento = LogEventiUtils.createLogEvento((HttpServletRequest) request);
		}
		logEvento.setCodApplicazione("PG");
		logEvento.setLivEvento(livEvento);
		logEvento.setOggEvento(oggEvento);
		logEvento.setCodEvento(codEvento);
		logEvento.setDescr(descrEvento);
		logEvento.setErrmsg(errMsgEvento);
		LogEventiUtils.insertLogEventi(logEvento);
      }

    } catch (Throwable t) {
      throw t;
    }

    logger.debug("InviaBandoAvvisoSimap2018: XML = \n" + xml);

    OutputStream os = conn.getOutputStream();
    os.write(xmlEnc.getBytes());
    os.flush();

    int risposta = conn.getResponseCode();
    System.out.println("response code: " + risposta);
    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
    String res = "";
    String output;
    System.out.println("Output from Server .... \n");
    while ((output = br.readLine()) != null) {
      res= res + output;
    }

    if (idForm != null) {
    	String codiceGara = codgar;
  		if (codiceGara.startsWith("$")) {
  			codiceGara = codiceGara.substring(1);
  		}

    	int livEvento = 1;
		String oggEvento = codiceGara;
		String codEvento = "GA_FEU_RES_INVIODATI";
		String descrEvento = "Risposta FEU - NUOVO EFORM " + numeroEFORM + " (id.avviso " + uuidEForm + " - id.scheda " + idScheda + ")";
		String errMsgEvento = res;

    	LogEvento logEvento = null;
		if (request == null) {
			logEvento = new LogEvento();
		} else {
			logEvento = LogEventiUtils.createLogEvento((HttpServletRequest) request);
		}
		logEvento.setCodApplicazione("PG");
		logEvento.setLivEvento(livEvento);
		logEvento.setOggEvento(oggEvento);
		logEvento.setCodEvento(codEvento);
		logEvento.setDescr(descrEvento);
		logEvento.setErrmsg(errMsgEvento);
		LogEventiUtils.insertLogEventi(logEvento);
      }

    conn.disconnect();
    return res;
  }


  /**
   * Restituisce XML contenente i dati dell'avviso di preinformazione
   *
   * @param codgar
   * @return
   * @throws SQLException
   * @throws IOException
   * @throws Exception
   */
  private AvvisoPreinformazioneType getXMLAvvisoGenerico(String codgar, String formulario, BigInteger tipoAvviso)
      throws SQLException, IOException, Exception {

    if (logger.isDebugEnabled())
      logger.debug("getXMLAvvisoPreinformazione: inizio metodo");

    AvvisoPreinformazioneType avvisoPreinformazione = AvvisoPreinformazioneType.Factory.newInstance();

    Empty empty = Empty.Factory.newInstance();

    Long genere = this.getGENERE(codgar);
    if(codgar.indexOf("$")>=0){
      Long genereTmp = this.getGENEREGARE(codgar.substring(1));
      if(genereTmp!= null && genereTmp.longValue()==11)
        genere = new Long(11);
    }

    String selectTORNGARE = null;

    switch (genere.intValue()) {
    case 1:
      selectTORNGARE = "select torn.cenint, "
          + " torn.destor, "
          + " torn.tipgen, "
          + " v_gare_importi.valmax,"
          + " torn.offlot, "
          + " torn.nofdit, "
          + " torn.ngadit, "
          + " torn.pcodoc, "
          + " torn.pcopre, "
          + " torn.iterga, "
          + " torn.accappub, "
          + " torn.dtepar, "
          + " torn.otepar, "
          + " torn.accqua, "
          + " torn.aqoper, "
          + " torn.aqnumope, "
          + " torn.ricastae "
          + " from torn, v_gare_importi where torn.codgar = ? and v_gare_importi.codgar = torn.codgar "
          + " and v_gare_importi.ngara is null";
      break;
    case 3:
      selectTORNGARE = "select torn.cenint, "
          + " torn.destor, "
          + " torn.tipgen, "
          + " v_gare_importi.valmax,"
          + " torn.offlot, "
          + " torn.nofdit, "
          + " torn.ngadit, "
          + " torn.pcodoc, "
          + " torn.pcopre, "
          + " torn.iterga, "
          + " torn.accappub, "
          + " torn.dtepar, "
          + " torn.otepar, "
          + " torn.accqua, "
          + " torn.aqoper, "
          + " torn.aqnumope, "
          + " torn.ricastae "
          + " from torn, v_gare_importi where torn.codgar = ? and v_gare_importi.codgar = torn.codgar " +
            " and v_gare_importi.ngara is null";
      break;
    case 2:
      selectTORNGARE = "select torn.cenint, "
          + " gare.not_gar, "
          + " torn.tipgen, "
          + " v_gare_importi.valmax,"
          + " torn.offlot, "
          + " torn.nofdit, "
          + " torn.ngadit, "
          + " torn.pcodoc, "
          + " torn.pcopre, "
          + " torn.iterga, "
          + " torn.accappub, "
          + " torn.dtepar, "
          + " torn.otepar, "
          + " torn.accqua, "
          + " torn.aqoper, "
          + " torn.aqnumope, "
          + " torn.ricastae "
          + " from torn, gare, v_gare_importi where torn.codgar = gare.codgar1 and v_gare_importi.codgar = torn.codgar "
          + " and torn.codgar = ?";
      /*
        * + " gare1.aqoper, "
          + " gare1.aqnumope, "
          + " torn.ricastae "
          + " from torn, gare, v_gare_importi, gare1 where torn.codgar = gare.codgar1 and v_gare_importi.codgar = torn.codgar "
          + " and torn.codgar = ?  and gare1.ngara = gare.ngara";
       * */
      break;
    case 11:
      selectTORNGARE = "select torn.cenint, "
        + " gareavvisi.oggetto, "
        + " gareavvisi.tipoapp, "
        + " torn.imptor,"
        + " torn.offlot, "
        + " torn.nofdit, "
        + " torn.ngadit, "
        + " torn.pcodoc, "
        + " torn.pcopre, "
        + " torn.iterga, "
        + " torn.accappub, "
        + " torn.dtepar, "
        + " torn.otepar, "
        + " torn.ricastae "
        + " from torn, gareavvisi where torn.codgar = gareavvisi.codgar"
        + " and torn.codgar = ?";
    break;
    }

    List datiTORNGARE = sqlManager.getVector(selectTORNGARE,
        new Object[] { codgar });

    if (datiTORNGARE != null && datiTORNGARE.size() > 0) {

      // Amministrazione
      String codein = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
          0).getValue();
      if (codein != null) {
        AuthorityAType authority = AuthorityAType.Factory.newInstance();
        authority = this.getAuthority(codein);
        avvisoPreinformazione.setAUTHORITYA(authority);
      }

      // Titolo del contratto e descrizione breve
      String title = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
          1).getValue();
      if (title != null) {
        avvisoPreinformazione.setTITLECONTRACT(title);
        avvisoPreinformazione.setSHORTDESCRIPTION(title);
      }
      /*
      else{
        avvisoPreinformazione.setTITLECONTRACT("errore");
        avvisoPreinformazione.setSHORTDESCRIPTION("errore");
      }*/

      // cpv
      String cpv = this.getCPVGara(codgar, genere);
      if(cpv != null)avvisoPreinformazione.setCPV(cpv);


      // Tipo di contratto
      Long type_contract = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 2).getValue();
      if (type_contract != null) {
        if(genere != 11){
          switch (type_contract.intValue()) {
            case 1:
              avvisoPreinformazione.setTYPECONTRACT(W3Z40Type.WORK);
            break;
            case 2:
              avvisoPreinformazione.setTYPECONTRACT(W3Z40Type.SUPP);
            break;
            case 3:
              avvisoPreinformazione.setTYPECONTRACT(W3Z40Type.SERV);
            break;
          }
        }else{
          if(100 <= type_contract){
            avvisoPreinformazione.setTYPECONTRACT(W3Z40Type.WORK);
          }
          if(10 <= type_contract && type_contract < 100){
            avvisoPreinformazione.setTYPECONTRACT(W3Z40Type.SUPP);
          }
          if(10 > type_contract){
            avvisoPreinformazione.setTYPECONTRACT(W3Z40Type.SERV);
          }
        }
      }

      //scope cost valmax
      //controllo se funziona per gare a plicco unico
      if(genere != 11){
        Number scopecost = (Number) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 3).getValue();
        if(scopecost != null)avvisoPreinformazione.setSCOPECOST(scopecost.doubleValue());
      }

      //gara a lotti?
      if(genere == 1 || genere == 3){
        avvisoPreinformazione.setDIVINTOLOTSYES(empty);
        Long offlot = (Long) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 4).getValue();
        Long nofdit = (Long) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 5).getValue();
        Long ngadit = (Long) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 6).getValue();

        //OFFLOT
        if(offlot != null){
          if(offlot == 1)avvisoPreinformazione.setDIVLOTSVALUE(W3Z45Type.X_1);
          if(offlot == 2)avvisoPreinformazione.setDIVLOTSVALUE(W3Z45Type.X_2);
          if(offlot == 3)avvisoPreinformazione.setDIVLOTSVALUE(W3Z45Type.X_3);
          }

        //NOFDIT
        if(nofdit != null){
          avvisoPreinformazione.setDIVLOTSMAX(nofdit.intValue());
        }

        //NGADIT
        if(ngadit != null){
          avvisoPreinformazione.setLOTSMAXTENDERER(ngadit.intValue());
        }

        LottoCommonType lots[] = null;
        //Si deve controllare se il CODIGA di tutti i lotti è costituito da un valore numerico.
        String selectLotti = "select ngara,codiga from gare where codgar1 = ? and (genere is null or genere != 3)";
        List lottiNGARA = sqlManager.getListVector(selectLotti,
            new Object[] { codgar });
        if (lottiNGARA != null && lottiNGARA.size() > 0) {
          boolean codigaTuttiNumeri=true;
          for (int i = 0; i < lottiNGARA.size(); i++) {
            String codiga = (String) SqlManager.getValueFromVectorParam(
                lottiNGARA.get(i), 1).getValue();
            if(!UtilityNumeri.isAValidNumber(codiga, UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE) || ('0' == codiga.charAt(0) && codiga.length() > 1)){
              codigaTuttiNumeri=false;
              break;
            }
          }

          lots = new LottoCommonType[lottiNGARA.size()];
          int indice=0;
          for (int i = 0; i < lottiNGARA.size(); i++) {
            String ngara = (String) SqlManager.getValueFromVectorParam(
                lottiNGARA.get(i), 0).getValue();
            if(!codigaTuttiNumeri)
              indice = i;
            else{
              String codiga = (String) SqlManager.getValueFromVectorParam(
                  lottiNGARA.get(i), 1).getValue();
              indice = Integer.parseInt(codiga);
            }

            lots[i] = this.getLottoCommonInfo(ngara, codgar, formulario, genere, indice, !codigaTuttiNumeri);
          }
        }
        avvisoPreinformazione.setLOTSArray(lots);
      }
      else{
        avvisoPreinformazione.setDIVINTOLOTSNO(empty);
        String ngara = (String) sqlManager.getObject(
            "select ngara from gare where codgar1 = ?",
            new Object[] { codgar });
        avvisoPreinformazione.setLOT(this.getLottoCommonInfo(ngara, codgar, formulario, genere, 0, true));
      }

      //punto di contatto PCODOC
        Long pcodoc = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 7).getValue();
      if (pcodoc == null) {
        avvisoPreinformazione.setFURTHERINFOIDEM(empty);
      }
      else{
        avvisoPreinformazione.setFURTHERINFOADD(empty);
        AuthorityType authority = this.getPuntoDiContattoInfo(codein,pcodoc);
        avvisoPreinformazione.setFURTHERINFOBODY(authority);
      }

      //punto di contatto PCOPRE
      if(genere != 11){
        Long pcopre = (Long) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 8).getValue();
        if (pcopre == null) {
          avvisoPreinformazione.setPARTECIPATIONIDEM(empty);
        }
        else{
          avvisoPreinformazione.setPARTECIPATIONADD(empty);
          AuthorityType authority = this.getPuntoDiContattoInfo(codein,pcopre);
          avvisoPreinformazione.setPARTECIPATIONBODY(authority);
        }
      }

      if(genere!=11){
        String accqua = (String) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 13).getValue();
        if (accqua == null || accqua.equals("2")){
          avvisoPreinformazione.setFRAMEWORKNO(empty);
        }else{
          if(accqua.equals("1")){
            avvisoPreinformazione.setFRAMEWORKYES(empty);
            Long aqoper = (Long) SqlManager.getValueFromVectorParam(
                datiTORNGARE, 14).getValue();
            if (aqoper == null || aqoper==1){
              avvisoPreinformazione.setFRAMESEVERALOPNO(empty);
            }else{
              if(aqoper==2){
                avvisoPreinformazione.setFRAMESEVERALOPYES(empty);
              }
              Number aqnumope = (Number) SqlManager.getValueFromVectorParam(
                  datiTORNGARE, 15).getValue();
              if (aqnumope != null){
                  avvisoPreinformazione.setFRAMEOPERATORSNUMBER(aqnumope.intValue());
              }
            }
          }
        }
      }

      String ricastae = null;
      if(genere == 11){
        ricastae = (String) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 13).getValue();
      }else{
        ricastae = (String) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 16).getValue();
      }
      if (ricastae != null){
        avvisoPreinformazione.setISELECTRONIC(SnType.Enum.forString(ricastae));
      }


      avvisoPreinformazione.setNOTICEF01(tipoAvviso);

      if(!(BigInteger.valueOf(2).compareTo(tipoAvviso) == 0) && !(tipoAvviso == BigInteger.valueOf(2))){

      Long iterga = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 9).getValue();
      
      if(new Long(9).equals(iterga))
        iterga = 1L;
      
      if(iterga != null){
        String strIterga = Long.toString(iterga);
        avvisoPreinformazione.setTYPEPROCEDURE(W3Z46PreType.Enum.forString(strIterga));
      }

      Calendar cal = Calendar.getInstance();
      Date dtepar = (Date) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 11).getValue();
      if(dtepar != null){
        cal.setTime(dtepar);
        avvisoPreinformazione.setDATERECEIPT(cal);
      }
      String otepar = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 12).getValue();
      if(otepar != null){
        avvisoPreinformazione.setTIMERECEIPT((otepar));
        }
      }

      String accappub = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 10).getValue();
      if(accappub != null)avvisoPreinformazione.setGPA(SnType.Enum.forString(accappub));

      avvisoPreinformazione.setDOCUMENTURL(getUrlDocumentiGara(codgar));
      avvisoPreinformazione.setDOCUMENTFURE(BigInteger.valueOf(1L));

      //FINE DELLA SCRITTURA DEL XML
    }

    return avvisoPreinformazione;

  }


  /**
   * Restituisce XML contenente i dati dell'avviso di preinformazione
   *
   * @param codgar
   * @return
   * @throws SQLException
   * @throws IOException
   * @throws Exception
   */
  private String getXMLAvvisoSettore(String codgar, String formulario, BigInteger tipoAvviso, String settore, String numeroEFORM, Long idScheda)
      throws SQLException, IOException, Exception {

    if (logger.isDebugEnabled())
      logger.debug("getXMLAvvisoPreinformazione: inizio metodo");

    String xml = null;
    AvvisoPreinformazioneType avvisoPreinformazione = this.getXMLAvvisoGenerico(codgar, formulario, tipoAvviso);

    Long iterga = (Long) sqlManager.getObject(
        "select iterga from torn where codgar = ?",
        new Object[] { codgar });
    
    if(new Long(9).equals(iterga))
      iterga = 1L;

    Long tipoAvvGareAvvisi = (Long) sqlManager.getObject(
            "select tipoavv from gareavvisi where codgar = ?",
            new Object[] { codgar });

    if("S".equals(settore) || ((Long) 2L).equals(tipoAvvGareAvvisi)){
      AvvisoPeriodicoIndicativoDocument avvisoPeriodicoIndicativoDocument = AvvisoPeriodicoIndicativoDocument.Factory.newInstance();
      avvisoPeriodicoIndicativoDocument.documentProperties().setEncoding("UTF-8");
      AvvisoPeriodicoIndicativoType avvisoPeriodicoIndicativo = avvisoPeriodicoIndicativoDocument.addNewAvvisoPeriodicoIndicativo();

      avvisoPeriodicoIndicativo = (AvvisoPeriodicoIndicativoType) avvisoPreinformazione.changeType(AvvisoPeriodicoIndicativoType.type);

		if (idScheda == null) {
			String uid;
			uid = this.getUid(4, codgar);
			if (uid != null) {
				avvisoPeriodicoIndicativo.setUUID(uid);
			}
		} else {
			String uid;
			uid = this.getUid(idScheda);
			if (uid != null) {
				avvisoPeriodicoIndicativo.setUUID(uid);
			}
		}

      if (StringUtils.isNotEmpty(numeroEFORM)) {
    	  avvisoPeriodicoIndicativo.setEFORMSSUBTYPE(numeroEFORM);
      } else {
	      switch (tipoAvviso.intValue()) {
	      case 1:
	    	  avvisoPeriodicoIndicativo.setEFORMSSUBTYPE("5");
	    	  break;
	      case 2:
	    	  avvisoPeriodicoIndicativo.setEFORMSSUBTYPE("8");
	    	  break;
	      case 3:
	    	  avvisoPeriodicoIndicativo.setEFORMSSUBTYPE("11");
	    	  break;
	      }
      }

      if(!(BigInteger.valueOf(2).compareTo(tipoAvviso) == 0) && !(tipoAvviso == BigInteger.valueOf(2))){
        if(iterga != null){
          String strIterga = Long.toString(iterga);
          if("2".equals(strIterga)){
            avvisoPeriodicoIndicativo.setTYPEPROCEDURE(W3Z80Type.Enum.forString("1"));
          }else{
            if("4".equals(strIterga)){
              avvisoPeriodicoIndicativo.setTYPEPROCEDURE(W3Z80Type.Enum.forString("2"));
            }
          }
        }
      }
      ByteArrayOutputStream baosAvvisoPeriodicoIndicativo = new ByteArrayOutputStream();
      avvisoPeriodicoIndicativoDocument.setAvvisoPeriodicoIndicativo(avvisoPeriodicoIndicativo);
      avvisoPeriodicoIndicativoDocument.save(baosAvvisoPeriodicoIndicativo);
      xml = baosAvvisoPeriodicoIndicativo.toString();
      baosAvvisoPeriodicoIndicativo.close();

    }else{

      AvvisoPreinformazioneDocument avvisoPreinformazioneDocument = AvvisoPreinformazioneDocument.Factory.newInstance();
      avvisoPreinformazioneDocument.documentProperties().setEncoding("UTF-8");

		if (idScheda == null) {
			String uid;
			uid = this.getUid(1, codgar);
			if (uid != null) {
				avvisoPreinformazione.setUUID(uid);
			}
		} else {
			String uid;
			uid = this.getUid(idScheda);
			if (uid != null) {
				avvisoPreinformazione.setUUID(uid);
			}
		}

      if (StringUtils.isNotEmpty(numeroEFORM)) {
    	  avvisoPreinformazione.setEFORMSSUBTYPE(numeroEFORM);
      } else {
	      switch (tipoAvviso.intValue()) {
	      case 1:
	    	  avvisoPreinformazione.setEFORMSSUBTYPE("4");
	    	  break;
	      case 2:
	    	  avvisoPreinformazione.setEFORMSSUBTYPE("7");
	    	  break;
	      case 3:
	    	  avvisoPreinformazione.setEFORMSSUBTYPE("10");
	    	  break;
	      }
      }

      if(!(BigInteger.valueOf(2).compareTo(tipoAvviso) == 0) && !(tipoAvviso == BigInteger.valueOf(2))){
        if(iterga != null){
          String strIterga = Long.toString(iterga);
          avvisoPreinformazione.setTYPEPROCEDURE(W3Z46PreType.Enum.forString(strIterga));
        }
      }

      ByteArrayOutputStream baosAvvisoPreinformazione = new ByteArrayOutputStream();
      avvisoPreinformazioneDocument.setAvvisoPreinformazione(avvisoPreinformazione);
      avvisoPreinformazioneDocument.save(baosAvvisoPreinformazione);
      xml = baosAvvisoPreinformazione.toString();
      baosAvvisoPreinformazione.close();

    }

    if (logger.isDebugEnabled())
      logger.debug("getXMLAvvisoPreinformazione: fine metodo");

    return xml;

  }


  /**
   * Restituisce XML contenente i dati del bando di gara
   *
   * @param codgar
   * @return
   * @throws SQLException
   * @throws IOException
   */
  private BandoGaraType getXMLBandoGaraGenerico(String codgar, String formulario) throws SQLException,
      IOException, Exception {

    if (logger.isDebugEnabled())
      logger.debug("getXMLBandoGara: inizio metodo");

    BandoGaraType bandoGara = BandoGaraType.Factory.newInstance();
    Empty empty = Empty.Factory.newInstance();

    Long genere = this.getGENERE(codgar);
    if(codgar.indexOf("$")>=0){
      Long genereTmp = this.getGENEREGARE(codgar.substring(1));
      if(genereTmp!= null && genereTmp.longValue()==11)
        genere = new Long(11);
    }

    String selectTORNGARE = null;

    if(genere == 1 || genere == 3){
      selectTORNGARE = "select torn.cenint, "
          + " torn.destor, "
          + " torn.tipgen, "
          + " v_gare_importi.valmax,"
          + " torn.offlot, "
          + " torn.nofdit, "
          + " torn.ngadit, "
          + " torn.pcodoc, "
          + " torn.iterga, "
          + " torn.pcopre, "
          + " torn.pcooff, "
          + " torn.numavcp, "
          + " torn.prourg, "
          + " torn.motacc, "
          + " torn.accappub, "
          + " torn.dtepar, "
          + " torn.otepar, "
          + " torn.dteoff,"
          + " torn.oteoff,"
          + " torn.desoff,"
          + " torn.oesoff,"
          + " torn.accqua, "
          + " torn.aqoper, "
          + " torn.aqnumope, "
          + " torn.ricastae "
          + " from torn, v_gare_importi where torn.codgar = ? and v_gare_importi.codgar = torn.codgar "
          + " and v_gare_importi.ngara is null";
      }
    else{
      selectTORNGARE = "select torn.cenint, "
          + " gare.not_gar, "
          + " torn.tipgen, "
          + " v_gare_importi.valmax,"
          + " torn.offlot, "
          + " torn.nofdit, "
          + " torn.ngadit, "
          + " torn.pcodoc, "
          + " torn.iterga, "
          + " torn.pcopre, "
          + " torn.pcooff, "
          + " torn.numavcp, "
          + " torn.prourg, "
          + " torn.motacc, "
          + " torn.accappub, "
          + " torn.dtepar, "
          + " torn.otepar, "
          + " torn.dteoff,"
          + " torn.oteoff,"
          + " torn.desoff,"
          + " torn.oesoff,"
          + " torn.accqua, "
          + " torn.aqoper, "
          + " torn.aqnumope, "
          + " torn.ricastae "
          + " from torn, gare, v_gare_importi where torn.codgar = gare.codgar1 and torn.codgar = ? "
          + " and v_gare_importi.codgar = torn.codgar";
      /*
       *  + " gare1.aqoper, "
          + " gare1.aqnumope, "
          + " torn.ricastae "
          + " from torn, gare, v_gare_importi, gare1 where torn.codgar = gare.codgar1 and torn.codgar = ? "
          + " and v_gare_importi.codgar = torn.codgar and gare1.ngara = gare.ngara";
       **/
    }

    List datiTORNGARE = sqlManager.getVector(selectTORNGARE,
        new Object[] { codgar });

    if (datiTORNGARE != null && datiTORNGARE.size() > 0) {

      // Amministrazione
      String codein = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
          0).getValue();
      if (codein != null) {
        AuthorityAType authority = AuthorityAType.Factory.newInstance();
        authority = this.getAuthority(codein);
        bandoGara.setAUTHORITYA(authority);
      }

      // Titolo del contratto e descrizione breve
      String title = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
          1).getValue();
      if (title != null) {
        bandoGara.setTITLECONTRACT(title);
        bandoGara.setSHORTDESCRIPTION(title);
      }
      /*else{
        bandoGara.setTITLECONTRACT("errore titolo non definito");
        bandoGara.setSHORTDESCRIPTION("errore titolo non definito");
      }*/


      //cpv
      String cpv = this.getCPVGara(codgar, genere);
      if(cpv != null)bandoGara.setCPV(cpv);

      // Tipo di contratto
      Long type_contract = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 2).getValue();
      if (type_contract != null) {
        switch (type_contract.intValue()) {
          case 1:
            bandoGara.setTYPECONTRACT(W3Z40Type.WORK);
          break;
          case 2:
            bandoGara.setTYPECONTRACT(W3Z40Type.SUPP);
          break;
          case 3:
            bandoGara.setTYPECONTRACT(W3Z40Type.SERV);
          break;
        }
      }

      //scope cost valmax
      Number scopecost = (Number) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 3).getValue();
      if(scopecost != null)bandoGara.setSCOPECOST(scopecost.doubleValue());

      //gara a lotti?
      if(genere == 1 || genere == 3){
        bandoGara.setDIVINTOLOTSYES(empty);
        Long offlot = (Long) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 4).getValue();
        Long nofdit = (Long) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 5).getValue();
        Long ngadit = (Long) SqlManager.getValueFromVectorParam(
            datiTORNGARE, 6).getValue();
        //OFFLOT
        if(offlot != null){
          if(offlot == 1)bandoGara.setDIVLOTSVALUE(W3Z45Type.X_1);
          if(offlot == 2)bandoGara.setDIVLOTSVALUE(W3Z45Type.X_2);
          if(offlot == 3)bandoGara.setDIVLOTSVALUE(W3Z45Type.X_3);
        }
        //NOFDIT
        if(nofdit != null){
          bandoGara.setDIVLOTSMAX(nofdit.intValue());
        }
        //NGADIT
        if(ngadit != null){
          bandoGara.setLOTSMAXTENDERER(ngadit.intValue());
        }

        LottoCommonType lots[] = null;
        //Si deve controllare se il CODIGA di tutti i lotti è costituito da un valore numerico.
        String selectLotti = "select ngara,codiga from gare where codgar1 = ? and (genere is null or genere != 3)";
        List lottiNGARA = sqlManager.getListVector(selectLotti,
            new Object[] { codgar });
        if (lottiNGARA != null && lottiNGARA.size() > 0) {
          boolean codigaTuttiNumeri=true;
          for (int i = 0; i < lottiNGARA.size(); i++) {
            String codiga = (String) SqlManager.getValueFromVectorParam(
                lottiNGARA.get(i), 1).getValue();
            if(!UtilityNumeri.isAValidNumber(codiga, UtilityNumeri.FORMATO_DOUBLE_CON_PUNTO_DECIMALE) || ('0' == codiga.charAt(0) && codiga.length() > 1)){
              codigaTuttiNumeri=false;
              break;
            }
          }
          int indice=0;
          lots = new LottoCommonType[lottiNGARA.size()];
          for (int i = 0; i < lottiNGARA.size(); i++) {
            String ngara = (String) SqlManager.getValueFromVectorParam(
                lottiNGARA.get(i), 0).getValue();
            if(!codigaTuttiNumeri)
              indice = i;
            else{
              String codiga = (String) SqlManager.getValueFromVectorParam(
                  lottiNGARA.get(i), 1).getValue();
              indice = Integer.parseInt(codiga);
            }
            lots[i] = this.getLottoCommonInfo(ngara, codgar, formulario, genere, indice, !codigaTuttiNumeri);
          }
        }
        bandoGara.setLOTSArray(lots);
      }
      else{
        bandoGara.setDIVINTOLOTSNO(empty);
        String ngara = (String) sqlManager.getObject(
            "select ngara from gare where codgar1 = ?",
            new Object[] { codgar });
        LottoCommonType avvAgg = LottoCommonType.Factory.newInstance();
        bandoGara.setLOT(this.getLottoCommonInfo(ngara, codgar, formulario, genere, 0, true));
      }

      //punto di contatto PCODOC
      Long pcodoc = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 7).getValue();
      if (pcodoc == null) {
        bandoGara.setFURTHERINFOIDEM(empty);
      }
      else{
        bandoGara.setFURTHERINFOADD(empty);
        AuthorityType authority = this.getPuntoDiContattoInfo(codein,pcodoc);
        bandoGara.setFURTHERINFOBODY(authority);
      }

      //punto di contatto PCOPRE
      Long iterga = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 8).getValue();
      Long pcopre = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 9).getValue();
      Number pcooff = (Number) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 10).getValue();

      if(iterga != null && (iterga == 2 || iterga == 4)){
        if (pcopre == null) {
          bandoGara.setPARTECIPATIONIDEM(empty);
        }
        else{
          bandoGara.setPARTECIPATIONADD(empty);
          AuthorityType authority = this.getPuntoDiContattoInfo(codein,pcopre);
          bandoGara.setPARTECIPATIONBODY(authority);
        }
      }else{
        if (pcooff == null) {
          bandoGara.setPARTECIPATIONIDEM(empty);
        }
        else{
          bandoGara.setPARTECIPATIONADD(empty);
          AuthorityType authority = this.getPuntoDiContattoInfo(codein,pcooff.longValue());
          bandoGara.setPARTECIPATIONBODY(authority);
        }
      }

      String acqua = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 21).getValue();
      if (acqua == null || acqua.equals("2")){
        bandoGara.setFRAMEWORKNO(empty);
      }else{
        if(acqua.equals("1")){
          bandoGara.setFRAMEWORKYES(empty);
          Long aqoper = (Long) SqlManager.getValueFromVectorParam(
              datiTORNGARE, 22).getValue();
          if (aqoper == null || aqoper==1){
            bandoGara.setFRAMESEVERALOPNO(empty);
          }else{
            if(aqoper==2){
              bandoGara.setFRAMESEVERALOPYES(empty);
            }
            Number aqnumope = (Number) SqlManager.getValueFromVectorParam(
                datiTORNGARE, 23).getValue();
            if (aqnumope != null){
              bandoGara.setFRAMEOPERATORSNUMBER(aqnumope.intValue());
            }
          }
        }
      }

      String ricastae = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 24).getValue();
      if (ricastae != null){
        bandoGara.setISELECTRONIC(SnType.Enum.forString(ricastae));
      }

      //numavcp
      String numavcp = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 11).getValue();
      if(numavcp != null){
        bandoGara.setIDGARA(numavcp);
      }else{
        bandoGara.setIDGARA("numavcp non def.");
      }

      //motacc
      String motacc = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 13).getValue();
      if(motacc != null)bandoGara.setACCELERATED(motacc);

      //accappub
      String accappub = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 14).getValue();
      if(accappub != null)bandoGara.setGPA(SnType.Enum.forString(accappub));

      //receipt date and time
      Date dtepar = (Date) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 15).getValue();
      String otepar = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 16).getValue();
      Date dteoff = (Date) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 17).getValue();
      String oteoff = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 18).getValue();
      Date desoff = (Date) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 19).getValue();
      String oesoff = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 20).getValue();
      Calendar cal = Calendar.getInstance();
      if(iterga != null && (iterga == 2 || iterga == 4)){
        if(dtepar != null)cal.setTime(dtepar); bandoGara.setRECEIPTLIMITDATE(cal);
        if(otepar != null)bandoGara.setRECEIPTLIMITTIME(otepar);
      }
      else{
        if(dteoff != null){cal.setTime(dteoff);bandoGara.setRECEIPTLIMITDATE(cal);}
        if(oteoff != null){bandoGara.setRECEIPTLIMITTIME(oteoff);}
        if(desoff != null){cal.setTime(desoff);bandoGara.setOPENINGTENDERSDATE(cal);}
        if(oesoff != null){bandoGara.setOPENINGTENDERSTIME(oesoff);}
      }

      bandoGara.setRECURRENTPROCNO(empty);

      bandoGara.setDOCUMENTURL(getUrlDocumentiGara(codgar));
      bandoGara.setDOCUMENTFURE(BigInteger.valueOf(1L));

      //FINE DELLA SCRITTURA
    }

    if (logger.isDebugEnabled()) logger.debug("getXMLBandoGara: fine metodo");

    return bandoGara;
  }


  /**
   * Restituisce XML contenente i dati del bando di gara
   *
   * @param codgar
   * @return
   * @throws SQLException
   * @throws IOException
   */
  private String getXMLBandoGaraSettore(String codgar, String formulario, String settore, String numeroEFORM, Long idScheda) throws SQLException,
      IOException, Exception {

    if (logger.isDebugEnabled())
      logger.debug("getXMLBandoGaraSettore: inizio metodo");

    String xml = null;

    BandoGaraType bandoGara = this.getXMLBandoGaraGenerico(codgar, formulario);

    Long iterga = (Long) sqlManager.getObject(
        "select iterga from torn where codgar = ?",
        new Object[] { codgar });

    if(new Long(9).equals(iterga))
      iterga = 1L;
    
    if("S".equals(settore)){

      BandoGaraSettoriSpecialiDocument bandoGaraSettoriSpecialiDocument = BandoGaraSettoriSpecialiDocument.Factory.newInstance();
      bandoGaraSettoriSpecialiDocument.documentProperties().setEncoding("UTF-8");
      BandoGaraSettoriSpecialiType bandoGaraSettoriSpeciali = bandoGaraSettoriSpecialiDocument.addNewBandoGaraSettoriSpeciali();

      bandoGaraSettoriSpeciali = (BandoGaraSettoriSpecialiType) bandoGara.changeType(BandoGaraSettoriSpecialiType.type);

		if (idScheda == null) {
			String uid;
			uid = this.getUid(5, codgar);
			if (uid != null) {
				bandoGaraSettoriSpeciali.setUUID(uid);
			}
		} else {
			String uid;
			uid = this.getUid(idScheda);
			if (uid != null) {
				bandoGaraSettoriSpeciali.setUUID(uid);
			}
		}
      
      if (StringUtils.isNotEmpty(numeroEFORM)) {
    	  bandoGaraSettoriSpeciali.setEFORMSSUBTYPE(numeroEFORM);
      } else {
    	  bandoGaraSettoriSpeciali.setEFORMSSUBTYPE("17");
      }

      if(iterga == 1)bandoGaraSettoriSpeciali.setTYPEPROCEDURE(W3Z78Type.Enum.forString("1"));
      else
      if(iterga == 2)bandoGaraSettoriSpeciali.setTYPEPROCEDURE(W3Z78Type.Enum.forString("2"));
      else
      if(iterga == 4)bandoGaraSettoriSpeciali.setTYPEPROCEDURE(W3Z78Type.Enum.forString("11"));

      //bandoGaraSettoriSpeciali.setRULESCRITERIA(null);
      //bandoGaraSettoriSpeciali.setDEPOSITGUARANTEEREQUIRED(null);
      //bandoGaraSettoriSpeciali.setMAINFINANCINGCONDITION(null);
      //bandoGaraSettoriSpeciali.setLEGALFORM(null);

      ByteArrayOutputStream baosBandoGara = new ByteArrayOutputStream();
      bandoGaraSettoriSpecialiDocument.setBandoGaraSettoriSpeciali(bandoGaraSettoriSpeciali);
      bandoGaraSettoriSpecialiDocument.save(baosBandoGara);
      xml = baosBandoGara.toString();
      baosBandoGara.close();
    }else{

      BandoGaraDocument bandoGaraDocument = BandoGaraDocument.Factory.newInstance();
      bandoGaraDocument.documentProperties().setEncoding("UTF-8");

		if (idScheda == null) {
			String uid;
			uid = this.getUid(2, codgar);
			if (uid != null) {
				bandoGara.setUUID(uid);
			}
		} else {
			String uid;
			uid = this.getUid(idScheda);
			if (uid != null) {
				bandoGara.setUUID(uid);
			}
		}
      
      if (StringUtils.isNotEmpty(numeroEFORM)) {
    	  bandoGara.setEFORMSSUBTYPE(numeroEFORM);
      } else {
    	  bandoGara.setEFORMSSUBTYPE("16");
      }

      String prourg = (String) sqlManager.getObject(
          "select prourg from torn where codgar = ?",
          new Object[] { codgar });

      if(iterga == 1 && (prourg == null || prourg.equals("2")))bandoGara.setTYPEPROCEDURE(W3Z46Type.Enum.forInt(1));
      else
      if(iterga == 2 && (prourg == null || prourg.equals("2")))bandoGara.setTYPEPROCEDURE(W3Z46Type.Enum.forInt(2));
      else
      if(iterga == 2 && (prourg.equals("1")))bandoGara.setTYPEPROCEDURE(W3Z46Type.Enum.forInt(3));
      else
      if(iterga == 4 && (prourg == null || prourg.equals("2")))bandoGara.setTYPEPROCEDURE(W3Z46Type.Enum.forInt(4));
      else
      if(iterga == 4 && (prourg.equals("1")))bandoGara.setTYPEPROCEDURE(W3Z46Type.Enum.forInt(5));
      else
      if(iterga == 1 && (prourg.equals("1")))bandoGara.setTYPEPROCEDURE(W3Z46Type.Enum.forInt(7));

      ByteArrayOutputStream baosBandoGara = new ByteArrayOutputStream();
      bandoGaraDocument.setBandoGara(bandoGara);
      bandoGaraDocument.save(baosBandoGara);
      xml = baosBandoGara.toString();
      baosBandoGara.close();

    }

    if (logger.isDebugEnabled()) logger.debug("getXMLBandoGaraSettore: fine metodo");

    return xml;
  }


  /**
   * Restituisce XML contenente i dati dell'avviso di aggiudicazione
   *
   * @param codgar
   * @return
   * @throws SQLException
   * @throws IOException
   */
  private AvvisoAggiudicazioneType getXMLAvvisoAggiudicazioneGenerico(String codgar, String formulario) throws SQLException,
      IOException, Exception {

    if (logger.isDebugEnabled())
      logger.debug("getXMLAvvisoAggiudicazione: inizio metodo");

    AvvisoAggiudicazioneType avvisoAggiudicazione = AvvisoAggiudicazioneType.Factory.newInstance();
    Empty empty = Empty.Factory.newInstance();

    Long genere = this.getGENERE(codgar);
    if(codgar.indexOf("$")>=0){
      Long genereTmp = this.getGENEREGARE(codgar.substring(1));
      if(genereTmp!= null && genereTmp.longValue()==11)
        genere = new Long(11);
    }

    String selectTORNGARE = null;

    if(genere == 1 || genere == 3){
      selectTORNGARE = "select torn.cenint, "
          + " torn.destor, "
          + " torn.tipgen, "
          + " v_gare_importi.valmax,"
          + " torn.numavcp, "
          + " torn.iterga, "
          + " torn.prourg, "
          + " torn.motacc, "
          + " torn.accappub, "
          + " torn.accqua, "
          + " torn.ricastae "
          + " from torn, v_gare_importi where torn.codgar = ? and v_gare_importi.codgar = torn.codgar "
          + " and v_gare_importi.ngara is null";
      }
    else{
      selectTORNGARE = "select torn.cenint, "
          + " gare.not_gar, "
          + " torn.tipgen, "
          + " v_gare_importi.valmax,"
          + " torn.numavcp, "
          + " torn.iterga, "
          + " torn.prourg, "
          + " torn.motacc, "
          + " torn.accappub, "
          + " torn.accqua, "
          + " torn.ricastae "
          + " from torn, gare, v_gare_importi where torn.codgar = gare.codgar1 and torn.codgar = ? "
          + " and v_gare_importi.codgar = torn.codgar";
    }

    List datiTORNGARE = sqlManager.getVector(selectTORNGARE,
        new Object[] { codgar });

    if (datiTORNGARE != null && datiTORNGARE.size() > 0) {

      // Amministrazione
      String codein = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
          0).getValue();
      if (codein != null) {
        AuthorityAType authority = AuthorityAType.Factory.newInstance();
        authority = this.getAuthority(codein);
        avvisoAggiudicazione.setAUTHORITYA(authority);
      }

      // Titolo del contratto e descrizione breve
      String title = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
          1).getValue();
      if (title != null) {
        avvisoAggiudicazione.setTITLECONTRACT(title);
        avvisoAggiudicazione.setSHORTDESCRIPTION(title);
      }
      /*else{
        avvisoAggiudicazione.setTITLECONTRACT("errore titolo non definito");
        avvisoAggiudicazione.setSHORTDESCRIPTION("errore titolo non definito");
      }*/

      //diviso in lotti?
      if(genere == 1 || genere == 3){
        avvisoAggiudicazione.setDIVINTOLOTSYES(empty);
        AvvisoAggiudicazioneLottoType lots[] = null;
        String selectLotti = "select ngara from gare where codgar1 = ? and (genere is null or genere != 3) and (ditta is not null or esineg is not null) order by NGARA";
        List lottiNGARA = sqlManager.getListVector(selectLotti,
            new Object[] { codgar });
        if (lottiNGARA != null && lottiNGARA.size() > 0) {
          lots = new AvvisoAggiudicazioneLottoType[lottiNGARA.size()];
          for (int i = 0; i < lottiNGARA.size(); i++) {
            String ngara = (String) SqlManager.getValueFromVectorParam(
                lottiNGARA.get(i), 0).getValue();
            lots[i] = this.getLottoAggiudicazioneInfo(ngara, codgar, formulario, genere, i);
          }
          avvisoAggiudicazione.setLOTSArray(lots);
        }
      }else{
        avvisoAggiudicazione.setDIVINTOLOTSNO(empty);
        String ngara = (String) sqlManager.getObject(
            "select ngara from gare where codgar1 = ?",
            new Object[] { codgar });
        AvvisoAggiudicazioneLottoType avvAgg = AvvisoAggiudicazioneLottoType.Factory.newInstance();
        avvisoAggiudicazione.setLOT(this.getLottoAggiudicazioneInfo(ngara, codgar, formulario, genere, 0));
      }

      //cpv
      String cpv = this.getCPVGara(codgar, genere);
      if(cpv != null)avvisoAggiudicazione.setCPV(cpv);

      // Tipo di contratto
      Long type_contract = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 2).getValue();
      if (type_contract != null) {
        switch (type_contract.intValue()) {
          case 1:
            avvisoAggiudicazione.setTYPECONTRACT(W3Z40Type.WORK);
          break;
          case 2:
            avvisoAggiudicazione.setTYPECONTRACT(W3Z40Type.SUPP);
          break;
          case 3:
            avvisoAggiudicazione.setTYPECONTRACT(W3Z40Type.SERV);
          break;
        }
      }

      //scope cost valmax
      Number scopecost = (Number) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 3).getValue();
      if(scopecost != null)avvisoAggiudicazione.setSCOPECOST(scopecost.doubleValue());

      String acqua = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 9).getValue();
      if (acqua != null){
        avvisoAggiudicazione.setFRAMEWORK(SnType.Enum.forString(acqua));
      }

      String ricastae = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 10).getValue();
      if (ricastae != null){
        avvisoAggiudicazione.setISELECTRONIC(SnType.Enum.forString(ricastae));
      }

      //numavcp
      String numavcp = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 4).getValue();
      if(numavcp != null){
        avvisoAggiudicazione.setIDGARA(numavcp);
      }else{
        avvisoAggiudicazione.setIDGARA("numavcp non def");
      }

      Long iterga = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 5).getValue();
      
      if(new Long(9).equals(iterga))
        iterga = 1L;

      //type procedure
      String prourg = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 6).getValue();

      if(iterga == 1 && (prourg == null || prourg.equals("2")))avvisoAggiudicazione.setTYPEPROCEDURE(W3Z54Type.Enum.forInt(1));
      else
      if(iterga == 2 && (prourg == null || prourg.equals("2")))avvisoAggiudicazione.setTYPEPROCEDURE(W3Z54Type.Enum.forInt(2));
      else
      if(iterga == 2 && (prourg.equals("1")))avvisoAggiudicazione.setTYPEPROCEDURE(W3Z54Type.Enum.forInt(3));
      else
      if(iterga == 4 && (prourg == null || prourg.equals("2")))avvisoAggiudicazione.setTYPEPROCEDURE(W3Z54Type.Enum.forInt(4));
      else
      if(iterga == 4 && (prourg.equals("1")))avvisoAggiudicazione.setTYPEPROCEDURE(W3Z54Type.Enum.forInt(5));
      else
      if(iterga == 5 || iterga == 6)avvisoAggiudicazione.setTYPEPROCEDURE(W3Z54Type.Enum.forInt(8));
      else
      if(iterga == 1 && (prourg.equals("1")))avvisoAggiudicazione.setTYPEPROCEDURE(W3Z54Type.Enum.forInt(9));


      //motacc
      String motacc = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 7).getValue();
      if(motacc != null)avvisoAggiudicazione.setACCELERATED(motacc);

      //accappub
      String accappub = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE, 8).getValue();
      if(accappub != null)avvisoAggiudicazione.setGPA(SnType.Enum.forString(accappub));

      String selectLottiAggiudicati = "select ngara from gare where codgar1 = ? and ( esineg is not null or ditta is not null )  and (genere is null or genere != 3) order by ngara";
      List lottiNGARA = sqlManager.getListVector(selectLottiAggiudicati,
          new Object[] { codgar });
      if (lottiNGARA != null && lottiNGARA.size() > 0) {
        LottoAggiudicatoType lottiAggiudicati[] = new LottoAggiudicatoType[lottiNGARA.size()];
        for (int i = 0; i < lottiNGARA.size(); i++) {
          String ngara = (String) SqlManager.getValueFromVectorParam(
              lottiNGARA.get(i), 0).getValue();
          lottiAggiudicati[i] = this.getDettaglioLottoAggiudicato(ngara,i);
        }
        if(lottiAggiudicati!=null)avvisoAggiudicazione.setLOTTIAGGIUDICATIArray(lottiAggiudicati);
      }

      //avvisoAggiudicazione.setAGREETOPUBLISH(null);
      
      //aggiunto il numero del bando (Numero avviso GU/S sezione IV.2.1 -> W3FS3.NOTICE_NUMBER_OJ).
      String navpub = (String) sqlManager.getObject("select navpub from pubbli where codgar9 = ? and tippub=3 and navpub is not null order by numpub desc", 
          new Object[] { codgar });
      if(navpub != null) avvisoAggiudicazione.setNOTICENUMBEROJ(navpub);

      //FINE DELLA SCRITTURA
    }

    if (logger.isDebugEnabled())
      logger.debug("getXMLAvvisoAggiudicazione: fine metodo");

    return avvisoAggiudicazione;
  }


  /**
   * Restituisce XML contenente i dati dell'avviso di aggiudicazione
   *
   * @param codgar
   * @return
   * @throws SQLException
   * @throws IOException
   */
  private String getXMLAvvisoAggiudicazioneSettore(String codgar, String formulario,String settore, String numeroEFORM, Long idScheda) throws SQLException,
      IOException, Exception {

    if (logger.isDebugEnabled())
      logger.debug("getXMLAvvisoAggiudicazione: inizio metodo");

    String xml = null;
    AvvisoAggiudicazioneType avvisoAggiudicazione = this.getXMLAvvisoAggiudicazioneGenerico(codgar, formulario);

    Long iterga = (Long) sqlManager.getObject(
        "select iterga from torn where codgar = ?",
        new Object[] { codgar });
    
    if(new Long(9).equals(iterga))
      iterga = 1L;

    if("S".equals(settore)){

      AvvisoAggiudicazioneSettoriSpecialiDocument avvisoAggiudicazioneSettoriSpecialiDocument = AvvisoAggiudicazioneSettoriSpecialiDocument.Factory.newInstance();
      avvisoAggiudicazioneSettoriSpecialiDocument.documentProperties().setEncoding("UTF-8");
      AvvisoAggiudicazioneSettoriSpecialiType avvisoAggiudicazioneSettoriSpeciali = avvisoAggiudicazioneSettoriSpecialiDocument.addNewAvvisoAggiudicazioneSettoriSpeciali();

      avvisoAggiudicazioneSettoriSpeciali = (AvvisoAggiudicazioneSettoriSpecialiType) avvisoAggiudicazione.changeType(AvvisoAggiudicazioneSettoriSpecialiType.type);

		if (idScheda == null) {
			String uid = this.getUid(6, codgar);
			if (uid != null) {
				avvisoAggiudicazioneSettoriSpeciali.setUUID(uid);
			}
		} else {
			String uid;
			uid = this.getUid(idScheda);
			if (uid != null) {
				avvisoAggiudicazioneSettoriSpeciali.setUUID(uid);
			}
		}

      if(iterga == 1)avvisoAggiudicazioneSettoriSpeciali.setTYPEPROCEDURE(W3Z79Type.Enum.forString("1"));
      else
      if(iterga == 2)avvisoAggiudicazioneSettoriSpeciali.setTYPEPROCEDURE(W3Z79Type.Enum.forString("2"));
      else
      if(iterga == 4)avvisoAggiudicazioneSettoriSpeciali.setTYPEPROCEDURE(W3Z79Type.Enum.forString("11"));
      else
      if(iterga == 5 || iterga == 6)avvisoAggiudicazioneSettoriSpeciali.setTYPEPROCEDURE(W3Z79Type.Enum.forString("8"));

      if (StringUtils.isNotEmpty(numeroEFORM)) {
    	  avvisoAggiudicazioneSettoriSpeciali.setEFORMSSUBTYPE(numeroEFORM);
      } else {
    	  avvisoAggiudicazioneSettoriSpeciali.setEFORMSSUBTYPE("30");
      }

      ByteArrayOutputStream baosBandoGara = new ByteArrayOutputStream();
      avvisoAggiudicazioneSettoriSpecialiDocument.setAvvisoAggiudicazioneSettoriSpeciali(avvisoAggiudicazioneSettoriSpeciali);
      avvisoAggiudicazioneSettoriSpecialiDocument.save(baosBandoGara);
      xml = baosBandoGara.toString();
      baosBandoGara.close();

    }else{
      AvvisoAggiudicazioneDocument avvisoAggiudicazioneDocument = AvvisoAggiudicazioneDocument.Factory.newInstance();
      avvisoAggiudicazioneDocument.documentProperties().setEncoding("UTF-8");

		if (idScheda == null) {
			String uid = this.getUid(3, codgar);
			if (uid != null) {
				avvisoAggiudicazione.setUUID(uid);
			}
		} else {
			String uid;
			uid = this.getUid(idScheda);
			if (uid != null) {
				avvisoAggiudicazione.setUUID(uid);
			}
		}

      if (StringUtils.isNotEmpty(numeroEFORM)) {
    	  avvisoAggiudicazione.setEFORMSSUBTYPE(numeroEFORM);
      } else {
    	  avvisoAggiudicazione.setEFORMSSUBTYPE("29");
      }

      String prourg = (String) sqlManager.getObject(
          "select prourg from torn where codgar = ?",
          new Object[] { codgar });

      if(iterga == 1 && (prourg == null || prourg.equals("2")))avvisoAggiudicazione.setTYPEPROCEDURE(W3Z54Type.Enum.forInt(1));
      else
      if(iterga == 2 && (prourg == null || prourg.equals("2")))avvisoAggiudicazione.setTYPEPROCEDURE(W3Z54Type.Enum.forInt(2));
      else
      if(iterga == 2 && (prourg.equals("1")))avvisoAggiudicazione.setTYPEPROCEDURE(W3Z54Type.Enum.forInt(3));
      else
      if(iterga == 4 && (prourg == null || prourg.equals("2")))avvisoAggiudicazione.setTYPEPROCEDURE(W3Z54Type.Enum.forInt(4));
      else
      if(iterga == 4 && (prourg.equals("1")))avvisoAggiudicazione.setTYPEPROCEDURE(W3Z54Type.Enum.forInt(5));
      else
      if(iterga == 5 || iterga == 6)avvisoAggiudicazione.setTYPEPROCEDURE(W3Z54Type.Enum.forInt(8));
      else
      if(iterga == 1 && (prourg.equals("1")))avvisoAggiudicazione.setTYPEPROCEDURE(W3Z54Type.Enum.forInt(9));

      ByteArrayOutputStream baosAvvisoAggiudicazione = new ByteArrayOutputStream();
      avvisoAggiudicazioneDocument.setAvvisoAggiudicazione(avvisoAggiudicazione);
      avvisoAggiudicazioneDocument.save(baosAvvisoAggiudicazione);
      xml = baosAvvisoAggiudicazione.toString();
      baosAvvisoAggiudicazione.close();

    }

    if (logger.isDebugEnabled())
      logger.debug("getXMLAvvisoAggiudicazione: fine metodo");

    return xml;
  }


  /**
   * Restituisce XML con i dati dell'avviso sul profilo di committente
   *
   * @param codgar
   * @return
   * @throws SQLException
   * @throws IOException
   * @throws Exception
   */
  private String getXMLAvvisoProfiloCommittente(String codgar, String formulario, String settore, String numeroEFORM, Long idScheda)
      throws SQLException, IOException, Exception {

    if (logger.isDebugEnabled())
      logger.debug("getXMLAvvisoProfiloCommittente: inizio metodo");

    String xml = null;
    AvvisoProfiloCommittenteDocument avvisoProfiloCommittenteDocument = AvvisoProfiloCommittenteDocument.Factory.newInstance();
    avvisoProfiloCommittenteDocument.documentProperties().setEncoding("UTF-8");
    AvvisoProfiloCommittenteType avvisoProfiloCommittente = avvisoProfiloCommittenteDocument.addNewAvvisoProfiloCommittente();
    Empty empty = Empty.Factory.newInstance();

    Long genere = this.getGENERE(codgar);
    if(codgar.indexOf("$")>=0){
      Long genereTmp = this.getGENEREGARE(codgar.substring(1));
      if(genereTmp!= null && genereTmp.longValue()==11)
        genere = new Long(11);
    }
    Long tipoAvvGareAvvisi = (Long) sqlManager.getObject(
            "select tipoavv from gareavvisi where codgar = ?",
            new Object[] { codgar });

    String selectTORNGARE = null;

    switch (genere.intValue()) {
    case 1:
      selectTORNGARE = "select torn.cenint, "
        + " torn.destor, "
        + " torn.tipgen, "
        + " torn.codnuts, "
        + " gare.prosla, "
        + " gare.loclav, "
        + " gare.nomssl, "
        + " torn.pcodoc "
        + " from torn, gare where codgar = ? and gare.codgar1 = torn.codgar";
      break;
    case 3:
      selectTORNGARE = "select torn.cenint, "
        + " torn.destor, "
        + " torn.tipgen, "
        + " torn.codnuts, "
        + " gare.prosla, "
        + " gare.loclav, "
        + " gare.nomssl, "
        + " torn.pcodoc"
        + " from torn, gare where codgar = ? and gare.codgar1 = torn.codgar and gare.genere = 3";
      break;
    case 2:
      selectTORNGARE = "select torn.cenint, "
        + " gare.not_gar, "
        + " torn.tipgen, "
        + " torn.codnuts, "
        + " gare.prosla, "
        + " gare.loclav, "
        + " gare.nomssl, "
        + " torn.pcodoc "
        + " from torn, gare where codgar = ? and gare.codgar1 = torn.codgar";
      break;
    case 11:
      selectTORNGARE = "select torn.cenint, "
        + " gareavvisi.oggetto, "
        + " gareavvisi.tipoapp, "
        + " torn.codnuts, "
        + " gare.prosla, "
        + " gare.loclav, "
        + " gare.nomssl, "
        + " torn.pcodoc "
        + " from torn, gare, gareavvisi where torn.codgar = ? "
        + " and gare.codgar1 = torn.codgar and torn.codgar = gareavvisi.codgar";
    break;
    }

    List datiTORNGARE = sqlManager.getListVector(selectTORNGARE,
        new Object[] { codgar });

    if (datiTORNGARE != null && datiTORNGARE.size() > 0) {

      //tipo di avviso
      if("S".equals(settore) || ((Long) 2L).equals(tipoAvvGareAvvisi)){
        avvisoProfiloCommittente.setNOTICERELATION(W3Z61Type.Enum.forString("4"));
        if (StringUtils.isNotEmpty(numeroEFORM)) {
        	avvisoProfiloCommittente.setEFORMSSUBTYPE(numeroEFORM);
        } else {
        	avvisoProfiloCommittente.setEFORMSSUBTYPE("2");
        }
      }else{
        avvisoProfiloCommittente.setNOTICERELATION(W3Z61Type.Enum.forString("3"));
        if (StringUtils.isNotEmpty(numeroEFORM)) {
        	avvisoProfiloCommittente.setEFORMSSUBTYPE(numeroEFORM);
        } else {
        	avvisoProfiloCommittente.setEFORMSSUBTYPE("1");
        }
      }


      // Amministrazione
      String codein = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE.get(0), 0).getValue();
      if (codein != null) {
        AuthorityAType authority = AuthorityAType.Factory.newInstance();
        authority = this.getAuthority(codein);
        avvisoProfiloCommittente.setAUTHORITYA(authority);
      }

      // Titolo del contratto e descrizione breve
      String title = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE.get(0), 1).getValue();
      if (title != null) {
        avvisoProfiloCommittente.setTITLECONTRACT(title);
        avvisoProfiloCommittente.setSHORTDESCRIPTION(title);
      }
      /*else{
        avvisoProfiloCommittente.setTITLECONTRACT("errore titolo non valorizzato");
        avvisoProfiloCommittente.setSHORTDESCRIPTION("errore titolo non valorizzato");
      }*/

      //cpv principale
      String cpv = this.getCPVGara(codgar, genere);
      if(cpv!=null)avvisoProfiloCommittente.setCPV(cpv);

      // Tipo di contratto
      Long type_contract = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE.get(0), 2).getValue();
      if (type_contract != null) {
        if(genere != 11){
          switch (type_contract.intValue()) {
            case 1:
              avvisoProfiloCommittente.setTYPECONTRACT(W3Z40Type.WORK);
            break;
            case 2:
              avvisoProfiloCommittente.setTYPECONTRACT(W3Z40Type.SUPP);
            break;
            case 3:
              avvisoProfiloCommittente.setTYPECONTRACT(W3Z40Type.SERV);
            break;
          }
        }else{
          if(100 <= type_contract){
            avvisoProfiloCommittente.setTYPECONTRACT(W3Z40Type.WORK);
          }
          if(10 <= type_contract && type_contract < 100){
            avvisoProfiloCommittente.setTYPECONTRACT(W3Z40Type.SUPP);
          }
          if(10 > type_contract){
            avvisoProfiloCommittente.setTYPECONTRACT(W3Z40Type.SERV);
          }
        }
      }

      //cod nuts
      String codnuts = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE.get(0), 3).getValue();
      if(codnuts != null)avvisoProfiloCommittente.setNUTS(codnuts);

      //site label
      String prosla = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE.get(0), 4).getValue();
      String loclav = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE.get(0), 5).getValue();
      String nomssl = (String) SqlManager.getValueFromVectorParam(
          datiTORNGARE.get(0), 6).getValue();

      String siteLabel = "";
      if(nomssl != null){siteLabel = siteLabel + nomssl;}
      if(nomssl != null && prosla != null){siteLabel = siteLabel + "  ";}
      if(loclav != null){siteLabel = siteLabel + loclav;}
      if(prosla != null){siteLabel = siteLabel + " (" + prosla + ")";}

      if(siteLabel.length() > 0){avvisoProfiloCommittente.setSITELABEL(siteLabel);}

      //punto di contatto PCODOC
      Long pcodoc = (Long) SqlManager.getValueFromVectorParam(
          datiTORNGARE.get(0), 7).getValue();
      if (pcodoc == null) {
        avvisoProfiloCommittente.setFURTHERINFOIDEM(empty);
      }
      else{
        avvisoProfiloCommittente.setFURTHERINFOADD(empty);
        AuthorityType authority = this.getPuntoDiContattoInfo(codein,pcodoc);
        avvisoProfiloCommittente.setFURTHERINFOBODY(authority);
      }

		if (idScheda == null) {
			String uid = this.getUid(8, codgar);
			if (uid != null) {
				avvisoProfiloCommittente.setUUID(uid);
			}
		} else {
			String uid;
			uid = this.getUid(idScheda);
			if (uid != null) {
				avvisoProfiloCommittente.setUUID(uid);
			}
		}
      //FINE DELLA SCRITTURA DEL XML
    }

    ByteArrayOutputStream baosAvvisoProfiloCommittente = new ByteArrayOutputStream();
    avvisoProfiloCommittenteDocument.save(baosAvvisoProfiloCommittente);
    xml = baosAvvisoProfiloCommittente.toString();
    baosAvvisoProfiloCommittente.close();

    if (logger.isDebugEnabled())
      logger.debug("getXMLAvvisoProfiloCommittente: fine metodo");

    return xml;

  }

	public void salvaXML(JSONObject pubblicazione, Long idForm) throws Throwable {
		TransactionStatus transazioneUpdate = this.sqlManager.startTransaction();
		boolean commitTransaction = false;
		try {

			Vector<?> datiScheda = sqlManager.getVector(
					"select g1npascheda.id, idprg_eform, iddoc_eform, eform, codgar from g1npaform,g1npascheda,g1cf_npa where g1npaform.id=? and g1npaform.id=g1npascheda.idform and g1npaform.idcfnpa=g1cf_npa.id",
					new Object[] { idForm });

			Long idScheda = SqlManager.getValueFromVectorParam(datiScheda, 0).longValue();
			String idprgEform = SqlManager.getValueFromVectorParam(datiScheda, 1).stringValue();
			Long iddocEform = SqlManager.getValueFromVectorParam(datiScheda, 2).longValue();
			String numeroEFORM = SqlManager.getValueFromVectorParam(datiScheda, 3).stringValue();
			String codgar = SqlManager.getValueFromVectorParam(datiScheda, 4).stringValue();

			if (idprgEform != null && iddocEform != null) {
                UploadInfo oldBucketInfo = ObjectStorageUtils.getUploadInfo("select bucket_ref, bucket_uuid from w_docdig where idprg=? and iddocdig=?",new Object[] {idprgEform,iddocEform});
                ObjectStorageUtils.deleteFile(oldBucketInfo.bucket_ref(),oldBucketInfo.uuid());
				sqlManager.update("delete from W_DOCDIG where idprg=? and iddocdig=?", new Object[] { idprgEform, iddocEform });
			}

			Long maxIdDocDig = (Long) sqlManager.getObject("select max(iddocdig) from w_docdig where idprg='PG'", null);
			Long newIdDocDig = null;
			if (maxIdDocDig == null) {
				newIdDocDig = 0L;
			} else {
				newIdDocDig = maxIdDocDig + 1;
			}

			String eformsXML = (String) pubblicazione.get("eforms_xml");
			byte[] xmlByte = Base64.decode(eformsXML);
			ByteArrayOutputStream baos = null;
			baos = new ByteArrayOutputStream();
			baos.write(xmlByte);

			if (codgar != null && codgar.startsWith("$")) {
				codgar = codgar.substring(1);
			}
			String descrizioneDocumento = "eForm " + numeroEFORM;
			String nomeDocumento = codgar + "_eForm_" + numeroEFORM + ".xml";

			DataColumnContainer containerDOCDIG = new DataColumnContainer(new DataColumn[] {
					new DataColumn("W_DOCDIG.IDPRG", new JdbcParametro(JdbcParametro.TIPO_TESTO, "PG")),
					new DataColumn("W_DOCDIG.IDDOCDIG", new JdbcParametro(JdbcParametro.TIPO_NUMERICO, newIdDocDig)),
					new DataColumn("W_DOCDIG.DIGENT", new JdbcParametro(JdbcParametro.TIPO_TESTO, "G1NPASCHEDA")),
					new DataColumn("W_DOCDIG.DIGKEY1", new JdbcParametro(JdbcParametro.TIPO_TESTO, idScheda.toString())),
					new DataColumn("W_DOCDIG.DIGNOMDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, nomeDocumento)),
					new DataColumn("W_DOCDIG.DIGDESDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, descrizioneDocumento)),
					new DataColumn("W_DOCDIG.DIGOGG", new JdbcParametro(JdbcParametro.TIPO_BINARIO, baos)) });
			containerDOCDIG.insert("W_DOCDIG", sqlManager);
            ObjectStorageUtils.addFile("PG", newIdDocDig);

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			sqlManager.update("update g1npascheda set idprg_eform=?, iddoc_eform=?, dnot_eform=? where g1npascheda.id=?", new Object[] { "PG", newIdDocDig, timestamp, idScheda });
			commitTransaction = true;
		} catch (SQLException e) {
			logger.error("Errore durante il salvataggio dell'eForm", e);
		} finally {
			try {
				if (commitTransaction) {
					this.sqlManager.commitTransaction(transazioneUpdate);
				} else {
					this.sqlManager.rollbackTransaction(transazioneUpdate);
				}
			} catch (SQLException e) {
				throw new Throwable("Errore durante la chiusura della transazione", e);
			}
		}
	}

  /* FUNZIONI*UTILI****FUNZIONI*UTILI****FUNZIONI*UTILI****FUNZIONI*UTILI****FUNZIONI*UTILI****FUNZIONI*UTILI****FUNZIONI*UTILI */

  /**
   *
   * @param ngara
   * @return
   * @throws SQLException
   */
  private CriteriaType[] criteriGoevLotto(String ngara, Long modlicg) throws SQLException {
    CriteriaType criteriaArray[] = new CriteriaType[1];
    if(modlicg== null || modlicg != 6){
      CriteriaType criteria = CriteriaType.Factory.newInstance();
      criteria.setTYPE(W3Z74Type.P);
      criteria.setWEIGHTING(100);
      criteriaArray[0] = criteria;
    }
    else{
      String selectGoev = "select tippar, despar, maxpun from goev where ngara = ? and livpar != 2";
      List datiGOEV = sqlManager.getListVector(selectGoev,
          new Object[] { ngara });
      if (datiGOEV != null && datiGOEV.size() > 0) {
        criteriaArray = new CriteriaType[datiGOEV.size()];
        for (int i = 0; i < datiGOEV.size(); i++) {
          Long tippar = (Long) SqlManager.getValueFromVectorParam(
              datiGOEV.get(i), 0).getValue();
          String despar = (String) SqlManager.getValueFromVectorParam(
              datiGOEV.get(i), 1).getValue();
          Double maxpun = (Double) SqlManager.getValueFromVectorParam(
              datiGOEV.get(i), 2).getValue();
          CriteriaType criteria = CriteriaType.Factory.newInstance();
          if(tippar != null){
            if(tippar == 1)criteria.setTYPE(W3Z74Type.Q);
            if(tippar == 2)criteria.setTYPE(W3Z74Type.P);
          }
          if(maxpun != null){
            maxpun = UtilityMath.round(maxpun, 0);
            criteria.setWEIGHTING(maxpun.intValue());}
          if(despar != null) {
            if(despar.length()>500)
              despar = despar.substring(0, 500);
            criteria.setCRITERIA(despar);
          }
          criteriaArray[i] = criteria;
        }
      }
    }
   return criteriaArray;
  }

  /**
   * Estrae il campo GENERE:
   * <ul>
   * <li>1 – gara divisa in lotti con offerte distinte</li>
   * <li>2 – gara a lotto unico</li>
   * <li>3 – gara divisa in lotti con offerta unica</li>
   * </ul>
   *
   * @param codgar
   * @return
   * @throws SQLException
   */
  private Long getGENERE(String codgar) throws SQLException {
    Long genere = (Long) sqlManager.getObject(
        "select genere from v_gare_torn where codgar = ?",
        new Object[] { codgar });
    return genere;
  }


  /**
   * Estrae il campo GENERE dell'entita GARE:
   * <ul>
   * <li>1 – gara divisa in lotti con offerte distinte</li>
   * <li>2 – gara a lotto unico</li>
   * <li>3 – gara divisa in lotti con offerta unica</li>
   * <li>11 – Avvisi</li>
   * </ul>
   *
   * @param codgar
   * @return
   * @throws SQLException
   */
  private Long getGENEREGARE(String ngara) throws SQLException {
    Long genere = (Long) sqlManager.getObject(
        "select genere from gare where ngara = ?",
        new Object[] { ngara });
    return genere;
  }


  private HashMap<String,Object> getDatiFittizia(String codgar) throws SQLException{
    HashMap<String,Object> response = new HashMap<String,Object>();
    String selectFittizia = "select prosla, loclav, nomssl, teutil, temesi, codcig, dinlavg  from gare where gare.codgar1 = ? and gare.genere = 3";
    List dati = sqlManager.getVector(selectFittizia,
        new Object[] { codgar });
    if (dati != null && dati.size() > 0) {
      String prosla = (String) SqlManager.getValueFromVectorParam(dati, 0).getValue();
      response.put("prosla",prosla);
      String loclav = (String) SqlManager.getValueFromVectorParam(dati, 1).getValue();
      response.put("loclav",loclav);
      String nomssl = (String) SqlManager.getValueFromVectorParam(dati, 2).getValue();
      response.put("nomssl",nomssl);
      Long teutil = (Long) SqlManager.getValueFromVectorParam(dati, 3).getValue();
      response.put("teutil",teutil);
      Long temesi = (Long) SqlManager.getValueFromVectorParam(dati, 4).getValue();
      response.put("temesi",temesi);
      String codcig = (String) SqlManager.getValueFromVectorParam(dati, 5).getValue();
      response.put("codcig",codcig);
      Date dinlavg = (Date) SqlManager.getValueFromVectorParam(dati, 6).getValue();
      response.put("dinlavg",dinlavg);
    }
    return response;
  }



  /**
   * Gestione oggetto CPV (Principale e complementare) per un lotto
   *
   * @param ngara
   * @return
   * @throws SQLException
   */
  private CPVType getCPVLotto(String ngara) throws SQLException {

    if (logger.isDebugEnabled()) logger.debug("getCPVLotto: inizio metodo");

    CPVType cpv = CPVType.Factory.newInstance();

    String selectGARCPV = "select codcpv from garcpv where ngara = ? and tipcpv = '1' order by numcpv";
    String cpvMain = (String) sqlManager.getObject(selectGARCPV,
        new Object[] { ngara });

    if (cpvMain != null) {
      cpv.setCPVMAIN(cpvMain);
      String selectGARCPV_Additional = "select codcpv from garcpv where ngara = ? and tipcpv = '2' order by numcpv";
      List datiGARCPV_Additional = sqlManager.getListVector(
          selectGARCPV_Additional, new Object[] { ngara });

      if (datiGARCPV_Additional != null && datiGARCPV_Additional.size() > 0) {
        for (int j = 0; j < datiGARCPV_Additional.size(); j++) {
          String cpvAdditional = (String) SqlManager.getValueFromVectorParam(
              datiGARCPV_Additional.get(j), 0).getValue();
          if (cpvAdditional != null) cpv.addCPVADDITIONAL(cpvAdditional);
        }
      }
    }
    if (logger.isDebugEnabled()) logger.debug("getCPVLotto: fine metodo");
    return cpv;
  }

  /**
   * Gestione oggetto CPV (Principale e complementare) per un lotto
   *
   * @param ngara
   * @return
   * @throws SQLException
   */
  private String getCPVGara(String codgar, Long genere) throws SQLException {

    if (logger.isDebugEnabled()) logger.debug("getCPVLotto: inizio metodo");
    //controllo se funziona anche per l'avviso
    if(genere == 1 || genere == 2 || genere == 3 || genere == 11){
      String select = "select ngara from gare where codgar1 = ?";
      if(genere == 3)
        select+=" and codgar1 != ngara";
      List listNgara = sqlManager.getListVector(select,
          new Object[] { codgar });
      if (listNgara != null && listNgara.size() > 0) {
        codgar  = (String) SqlManager.getValueFromVectorParam(
            listNgara.get(0), 0).getValue();
      }
    }

    String selectGARCPV = "select codcpv from garcpv where ngara = ? and tipcpv = '1' order by numcpv";
    String cpv = (String) sqlManager.getObject(selectGARCPV,
        new Object[] { codgar });

    if (logger.isDebugEnabled()) logger.debug("getCPVLotto: fine metodo");
    return cpv;
  }


  /**
   * Gestione dell'amministrazione aggiudicatrice
   * (ufficio intestatario)
   *
   * @param codein
   * @return
   * @throws SQLException
   */
  private AuthorityAType getAuthority(String codein) throws SQLException,
      Exception {

    if (logger.isDebugEnabled()) logger.debug("getAuthority: inizio metodo");
    AuthorityAType authority = AuthorityAType.Factory.newInstance();

    String selectUFFINT = "select nomein, "
        + "cfein, "
        + "ivaein "
        + "from uffint where codein = ?";

    List datiUFFINT = sqlManager.getVector(selectUFFINT,
        new Object[] { codein });

    if (datiUFFINT != null && datiUFFINT.size() > 0) {
      String nomein = (String) SqlManager.getValueFromVectorParam(datiUFFINT, 0).getValue();
      if (nomein != null) {
        authority.setNOMEIN(nomein);
      }

      String cfein = (String) SqlManager.getValueFromVectorParam(datiUFFINT, 1).getValue();
      if (cfein != null) {
        if (UtilityFiscali.isValidPartitaIVA(cfein) || UtilityFiscali.isValidCodiceFiscale(cfein)){
          authority.setCFEIN(cfein);
        }
      }else{
        String ivaein = (String) SqlManager.getValueFromVectorParam(datiUFFINT, 2).getValue();
        if (ivaein != null) {
          if (UtilityFiscali.isValidPartitaIVA(ivaein)){
            authority.setIVAEIN(ivaein);
          }
        }
      }
    }
    if (logger.isDebugEnabled()) logger.debug("getAuthority: fine metodo");
    return authority;
  }


  /**
  *
  * @param ngara
  * @param codgar
  * @param formulario
  * @param genere
  * @param index
  * @param indiceProgressivo
  * @return
  * @throws SQLException
  */
 public LottoCommonType getLottoCommonInfo(String ngara, String codgar, String formulario, Long genere, int index, boolean indiceProgressivo) throws SQLException{

   LottoCommonType lottoCommon = LottoCommonType.Factory.newInstance();
   Empty empty = Empty.Factory.newInstance();
   Boolean isAvvAgg = false;
   if(formulario.equals("FS3")){isAvvAgg = true;}

   String selectLottoCommon = null;
   if(genere != 11){
     selectLottoCommon = "select gare.not_gar," +
           " torn.codnuts," +
           " gare.prosla," +
           " gare.loclav," +
           " gare.nomssl," +
           " gare.modlicg," +
           " v_gare_importi.valmax," +
           " gare.teutil," +
           " gare.temesi," +
           " torn.accqua," +
           " torn.aqdurata," +
           " torn.aqtempo," +
           " torn.ammvar," +
           " gare1.ammrin," +
           " gare1.ammopz," +
           " gare1.desrin," +
           " gare1.desopz," +
           " torn.apfinfc," +
           " torn.progeu," +
           " gare.codcig" +
           ", gare.dinlavg" +
           " from torn, gare, gare1, v_gare_importi " +
           " where torn.codgar = gare.codgar1 and gare.ngara = ? and gare1.ngara = gare.ngara and v_gare_importi.ngara = gare.ngara";
   } else {
     selectLottoCommon = "select gareavvisi.oggetto," +
             " torn.codnuts," +
             " gare.prosla," +
             " gare.loclav," +
             " gare.nomssl," +
             " gare.modlicg," +
             " gare.impapp," +
             " gare.teutil," +
             " gare.temesi," +
             " torn.accqua," +
             " torn.aqdurata," +
             " torn.aqtempo," +
             " torn.ammvar," +
             " torn.ammrin," +
             " torn.ammopz," +
             " torn.desrin," +
             " torn.desopz," +
             " torn.apfinfc," +
             " torn.progeu," +
             " gare.codcig" +
             ", gare.dinlavg" +
             " from torn, gare, gareavvisi" +
             " where torn.codgar = gare.codgar1 and torn.codgar = gareavvisi.codgar and gare.ngara = ?";
   }
   List datiTORNGARE = sqlManager.getVector(selectLottoCommon,
       new Object[] { ngara });

   HashMap<String,Object> datiRigaFittizia = null;
   if(genere == 3){
     datiRigaFittizia = this.getDatiFittizia(codgar);
   }

   if (datiTORNGARE != null && datiTORNGARE.size() > 0) {

   if(indiceProgressivo)
     lottoCommon.setLOTNO(index+1);
   else
     lottoCommon.setLOTNO(index);
   //title
   String title = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
       0).getValue();
   if(title != null){
     if(title.length() > 500){
       title = title.substring(0, 499);
     }
     lottoCommon.setTITLE(title);
   }
   /*else
   lottoCommon.setTITLE("errore titolo mancante");  */
   //cod nuts
   String codnuts =(String) SqlManager.getValueFromVectorParam(datiTORNGARE,1).getValue();
   if(codnuts != null){lottoCommon.setNUTS(codnuts);}
   //site label
   String prosla = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
       2).getValue();
   String loclav = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
       3).getValue();
   String nomssl = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
       4).getValue();

   if(genere == 3){
     prosla = (String) datiRigaFittizia.get("prosla");
     loclav = (String) datiRigaFittizia.get("loclav");
     nomssl = (String) datiRigaFittizia.get("nomssl");
   }

   String siteLabel = "";
   if(nomssl != null){siteLabel = siteLabel + nomssl;}
   if(nomssl != null && prosla != null){siteLabel = siteLabel + "  ";}
   if(loclav != null){siteLabel = siteLabel + loclav;}
   if(prosla != null){siteLabel = siteLabel + " (" + prosla + ")";}

   if(siteLabel.length() > 0){lottoCommon.setSITELABEL(siteLabel);}
   //description
   String description = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,0).getValue();
   if(description != null)lottoCommon.setDESCRIPTION(description);
   }
   //cpv
   CPVType cpvLotto = CPVType.Factory.newInstance();
   cpvLotto = this.getCPVLotto(ngara);
   if (cpvLotto != null && cpvLotto.getCPVMAIN() != null)
     lottoCommon.setCPV(cpvLotto);

   if(!isAvvAgg){
     //ac_doc
     if(genere != 11)lottoCommon.setACDOC(SnType.Enum.forInt(2));
     //goev criteri aggiudicazione
     Long modlicg = (Long) SqlManager.getValueFromVectorParam(datiTORNGARE,
         5).getValue();
     //criteri di aggiudicazione
     //controllo se funziona per avviso
     if(genere != 11){
       lottoCommon.setAWCRITERIAArray(this.criteriGoevLotto(ngara,modlicg));
     }

     // cost impapp
     if(genere != 11){
     Number cost = (Number) SqlManager.getValueFromVectorParam(datiTORNGARE, 6).getValue();
     if(cost!= null)lottoCommon.setCOST(cost.doubleValue());
     }

     //work month
     Long teutil = (Long) SqlManager.getValueFromVectorParam(datiTORNGARE,
         7).getValue();
     Long temesi = (Long) SqlManager.getValueFromVectorParam(datiTORNGARE,
         8).getValue();
     String accqua = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
         9).getValue();
     Number aqdurata = (Number) SqlManager.getValueFromVectorParam(datiTORNGARE,
         10).getValue();
     Long aqtempo = (Long) SqlManager.getValueFromVectorParam(datiTORNGARE,
         11).getValue();
     Date dinlavgDate = (Date) SqlManager.getValueFromVectorParam(datiTORNGARE,20).getValue();

     if(genere == 3){
       teutil = (Long) datiRigaFittizia.get("teutil");
       temesi = (Long) datiRigaFittizia.get("temesi");
       dinlavgDate = (Date) datiRigaFittizia.get("dinlavg");
     }

     if ("1".equals(accqua)) {
         if (aqdurata != null && aqtempo != null) {
             switch (aqtempo.intValue()) {
                 case 1:
                     lottoCommon.setWORKMONTH(aqdurata.intValue());
                     break;
                 case 2:
                     lottoCommon.setWORKMONTH(aqdurata.intValue()*12);
                     break;
                 case 3:
                     lottoCommon.setWORKDAYS(aqdurata.intValue());
                     break;
                 default:
                     break;
             }
         }
     } else if (teutil != null && temesi != null) {
        if (((Long) 1L).equals(temesi)) {
            if (dinlavgDate == null) {
                lottoCommon.setWORKDAYS(teutil.intValue());
            } else {
                Calendar dinlavg = Calendar.getInstance();
                dinlavg.setTime(dinlavgDate);
                lottoCommon.setWORKSTARTDATE(dinlavg);
                dinlavg.add(Calendar.DATE,teutil.intValue());
                lottoCommon.setWORKENDDATE(dinlavg);
            }
        } else if (((Long) 2L).equals(temesi)) {
            if (dinlavgDate == null) {
                lottoCommon.setWORKMONTH(teutil.intValue());
            } else {
                Calendar dinlavg = Calendar.getInstance();
                dinlavg.setTime(dinlavgDate);
                lottoCommon.setWORKSTARTDATE(dinlavg);
                dinlavg.add(Calendar.MONTH,teutil.intValue());
                lottoCommon.setWORKENDDATE(dinlavg);
            }
        }
     }

     String ammvar = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
         12).getValue();
     if(ammvar != null){
       if(ammvar.equals("1")){
         lottoCommon.setACCVARIANTS(SnType.X_1);
         }
       if(ammvar.equals("2")){
         lottoCommon.setACCVARIANTS(SnType.X_2);
         }
       }
     }
   String ammrin = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
       13).getValue();
   String ammopz = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
       14).getValue();
   String desrin = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
       15).getValue();

   if(ammrin != null && ammrin.equals("2")){
     lottoCommon.setRENEWALNO(empty);
   }
   if(ammrin != null && ammrin.equals("1")){
     lottoCommon.setRENEWALYES(empty);
     if(desrin == null){desrin = "";}
     lottoCommon.setRENEWALDESCR(desrin);
   }
   if(ammopz != null && ammopz.equals("1")){
     lottoCommon.setOPTIONSYES(empty);
     String desopz = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
         16).getValue();
     if(desopz == null){desopz = "";}
     lottoCommon.setOPTIONSDESCR(desopz);
   }
   if(ammopz != null && ammopz.equals("2")){
     lottoCommon.setOPTIONSNO(empty);
   }

   String apfinfc = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
       17).getValue();
   if(apfinfc != null && apfinfc.equals("2")){
     lottoCommon.setEUPROGRNO(empty);
   }
   if(apfinfc != null && apfinfc.equals("1")){
     lottoCommon.setEUPROGRYES(empty);
     String progeu = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
         18).getValue();
     if(progeu == null){
       progeu = "";
     } else if (progeu.length() > 200) {
    	 progeu = progeu.substring(0,200);
     }
     lottoCommon.setEUPROGRDESCR(progeu);
   }

   /**
   if(formulario.equals("FS2")){
     String codcig = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
         19).getValue();
     if(genere == 3){codcig = (String) datiRigaFittizia.get("codcig");}
     lottoCommon.set
   }*/
   return lottoCommon;
 }


public AvvisoAggiudicazioneLottoType getLottoAggiudicazioneInfo(String ngara, String codgar, String formulario, Long genere, int index) throws SQLException{

    AvvisoAggiudicazioneLottoType lottoCommon = AvvisoAggiudicazioneLottoType.Factory.newInstance();
    Empty empty = Empty.Factory.newInstance();
    Boolean isAvvAgg = false;
    if(formulario.equals("FS3")){isAvvAgg = true;}

    String selectLottoCommon = null;
    if(genere != 11){
      selectLottoCommon = "select gare.not_gar," +
            " torn.codnuts," +
            " gare.prosla," +
            " gare.loclav," +
            " gare.nomssl," +
            " gare.modlicg," +
            " v_gare_importi.valmax," +
            " gare.teutil," +
            " gare.temesi," +
            " torn.accqua," +
            " torn.aqdurata," +
            " torn.aqtempo," +
            " torn.ammvar," +
            " gare1.ammrin," +
            " gare1.ammopz," +
            " gare1.desrin," +
            " gare1.desopz," +
            " torn.apfinfc," +
            " torn.progeu," +
            " gare.codcig" +
            " from torn, gare, gare1, v_gare_importi " +
            " where torn.codgar = gare.codgar1 and gare.ngara = ? and gare1.ngara = gare.ngara and v_gare_importi.ngara = gare.ngara";

    } else {
      selectLottoCommon = "select gareavvisi.oggetto," +
              " torn.codnuts," +
              " gare.prosla," +
              " gare.loclav," +
              " gare.nomssl," +
              " gare.modlicg," +
              " gare.impapp," +
              " gare.teutil," +
              " gare.temesi," +
              " torn.accqua," +
              " torn.aqdurata," +
              " torn.aqtempo," +
              " torn.ammvar," +
              " torn.ammrin," +
              " torn.ammopz," +
              " torn.desrin," +
              " torn.desopz," +
              " torn.apfinfc," +
              " torn.progeu," +
              " gare.codcig" +
              " from torn, gare, gareavvisi " +
              " where torn.codgar = gare.codgar1 and torn.codgar = gareavvisi.codgar and gare.ngara = ?";
    }
    List datiTORNGARE = sqlManager.getVector(selectLottoCommon,
        new Object[] { ngara });

    HashMap<String,Object> datiRigaFittizia = null;
    if(genere == 3){
      datiRigaFittizia = this.getDatiFittizia(codgar);
    }

    if (datiTORNGARE != null && datiTORNGARE.size() > 0) {

    lottoCommon.setLOTNO(index+1);
    // Titolo del contratto e descrizione breve
    String title = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
        0).getValue();
    if (title != null) {
      if(title.length() > 500){
        title = title.substring(0, 499);
      }
      lottoCommon.setTITLE(title);
      lottoCommon.setDESCRIPTION(title);
    }
    /*else{
      lottoCommon.setTITLE("errore");
      lottoCommon.setDESCRIPTION("errore");
    }*/

    //cod nuts
    String codnuts =(String) SqlManager.getValueFromVectorParam(datiTORNGARE,1).getValue();
    if(codnuts != null)lottoCommon.setNUTS(codnuts);
    //site label
    String prosla = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
        2).getValue();
    String loclav = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
        3).getValue();
    String nomssl = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
        4).getValue();

    if(genere == 3){
      prosla = (String) datiRigaFittizia.get("prosla");
      loclav = (String) datiRigaFittizia.get("loclav");
      nomssl = (String) datiRigaFittizia.get("nomssl");
    }

    String siteLabel = "";
    if(nomssl != null){siteLabel = siteLabel + nomssl;}
    if(nomssl != null && prosla != null){siteLabel = siteLabel + "  ";}
    if(loclav != null){siteLabel = siteLabel + loclav;}
    if(prosla != null){siteLabel = siteLabel + " (" + prosla + ")";}
    if(siteLabel.length() > 0){lottoCommon.setSITELABEL(siteLabel);}

    //goev criteri aggiudicazione
    Long modlicg = (Long) SqlManager.getValueFromVectorParam(datiTORNGARE,
        5).getValue();
    //criteri di aggiudicazione
    //controllo se funziona per avviso
    if(genere != 11){
      lottoCommon.setAWCRITERIAArray(this.criteriGoevLotto(ngara,modlicg));
    }

    if(siteLabel.length() <= 0){lottoCommon.setSITELABEL(siteLabel);}
    //description
    String description = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,0).getValue();
    if(description != null)lottoCommon.setDESCRIPTION(description);
    }

    //cpv
    CPVType cpvLotto = this.getCPVLotto(ngara);
    if (cpvLotto != null && cpvLotto.getCPVMAIN() != null)
      lottoCommon.setCPV(cpvLotto);


    String ammopz = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
        14).getValue();

    if(ammopz != null && ammopz.equals("1")){
      lottoCommon.setOPTIONSYES(empty);
      String desopz = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
          16).getValue();
      if(desopz == null){desopz = "";}
      lottoCommon.setOPTIONSDESCR(desopz);
    }
    if(ammopz != null && ammopz.equals("2")){
      lottoCommon.setOPTIONSNO(empty);
    }

    String apfinfc = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
        17).getValue();
    if(apfinfc != null && apfinfc.equals("2")){
      lottoCommon.setEUPROGRNO(empty);
    }
    if(apfinfc != null && apfinfc.equals("1")){
      lottoCommon.setEUPROGRYES(empty);
      String progeu = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
          18).getValue();
      if(progeu == null){
        progeu = "";}
      lottoCommon.setEUPROGRDESCR(progeu);
    }

    return lottoCommon;
  }


  public AuthorityType getPuntoDiContattoInfo(String codein , Long numpun) throws SQLException{

    String selectPuntoCont = "select punticon.nompun, " +
    		"uffint.cfein, " +
    		"uffint.ivaein, " +
    		"punticon.viaein, " +
    		"punticon.nciein, " +
    		"punticon.citein, " +
    		"punticon.proein, " +
    		"punticon.capein, " +
    		"punticon.codnaz, " +
    		"punticon.telein, " +
    		"punticon.faxein, " +
    		"punticon.emaiin, " +
    		"punticon.indweb, " +
    		"punticon.codres " +
    		"from punticon, uffint where punticon.codein = uffint.codein and punticon.numpun = ? and punticon.codein = ?";
    String selectResponsabile = "select tecni.nomtec from tecni, punticon " +
    		"where punticon.codres = tecni.codtec and punticon.codres = ?";
    AuthorityType authority = AuthorityType.Factory.newInstance();
    List datiTORNGARE = sqlManager.getVector(selectPuntoCont,
        new Object[] { numpun, codein});
    if (datiTORNGARE != null && datiTORNGARE.size() > 0) {
      String nomein = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,0).getValue();
      if(nomein != null) authority.setNOMEIN(nomein);
      String cfein = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,1).getValue();
      if(cfein != null){authority.setCFEIN(cfein);}
      else{String ivaein = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,2).getValue();
      if(ivaein != null) authority.setIVAEIN(ivaein);}
      String viaein = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,3).getValue();
      if(viaein != null) authority.setVIAEIN(viaein);
      String ncien = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,4).getValue();
      if(ncien != null) authority.setNCIEIN(ncien);
      String citein = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,5).getValue();
      String proimp = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,6).getValue();
      String town = "";
      if(citein != null){town = town + citein;}
      if(proimp != null){town = town +  " (" + proimp + ")";}
      if(town.length() > 0)authority.setTOWN(town);
      String codnuts = (String) sqlManager.getObject(
          "select codice from tabnuts where siglaprov = ?", new Object[] { proimp });
      if(codnuts!=null){authority.setNUTS(codnuts);}
      String capein = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,7).getValue();
      if(capein != null) authority.setCAPEIN(capein);
      Long codnaz = (Long) SqlManager.getValueFromVectorParam(datiTORNGARE,8).getValue();
      if(codnaz != null) authority.setCODNAZ(codnaz.intValue());
      String telein = (String) SqlManager.getValueFromVectorParam(datiTORNGARE, 9).getValue();
      if(telein != null) authority.setTELEIN(telein);
      String faxein = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,10).getValue();
      if(faxein != null) authority.setFAXEIN(faxein);
      String emaiin = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,11).getValue();
      if(emaiin != null) authority.setEMAIIN(emaiin);
      String indweb = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,12).getValue();
      if(indweb != null) authority.setINDWEB(indweb);
      String codres = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,13).getValue();
      if(codres != null){authority.setCONTACTPOINT((String) sqlManager.getObject(
      selectResponsabile, new Object[] { codres }));}
    }
    return authority;
  }

  private LottoAggiudicatoType  getDettaglioLottoAggiudicato(String ngara, int index) throws SQLException,GestoreException{
    LottoAggiudicatoType lottoAggiudicato = LottoAggiudicatoType.Factory.newInstance();
    Empty empty = Empty.Factory.newInstance();

    String selectLottoAggiudicato = "select gare.not_gar, " +
    		" gare.esineg, " +
    		" gare.dattoa, " +
    		" gare.ditta, " +
    		" gare.iaggiu, " +
    		" torn.accqua, " +
    		" gare1.aqoper " +
            ", GARE.DAATTO" +
    		" from gare, torn, gare1 where gare.ngara = ? and gare1.ngara = gare.ngara and gare.codgar1 = torn.codgar" ;

    List datiTORNGARE = sqlManager.getVector(selectLottoAggiudicato,
        new Object[] { ngara });
    if (datiTORNGARE != null && datiTORNGARE.size() > 0) {

      String notgar = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
          0).getValue();
      Long esineg = (Long) SqlManager.getValueFromVectorParam(datiTORNGARE,
          1).getValue();
      Date dattoaDate = (Date) SqlManager.getValueFromVectorParam(datiTORNGARE,
          2).getValue();
      String ditta = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
          3).getValue();
      Double iaggiu = (Double) SqlManager.getValueFromVectorParam(datiTORNGARE,
          4).getValue();
      String accqua = (String) SqlManager.getValueFromVectorParam(datiTORNGARE,
          5).getValue();
      Long aqoper = (Long) SqlManager.getValueFromVectorParam(datiTORNGARE,
          6).getValue();
      Date daattoDate = (Date) SqlManager.getValueFromVectorParam(datiTORNGARE, 7).getValue();

    List<Vector<JdbcParametro>> countOfferte = getCountDitteMPMIeEstere(ngara);

    if(daattoDate != null){
        Calendar daatto = Calendar.getInstance();
        daatto.setTime(dattoaDate);
        lottoAggiudicato.setCONTRACTISSUEDATE(daatto);
    }

    lottoAggiudicato.setLOTNO(index+1);
    if(notgar!=null)lottoAggiudicato.setTITLE(notgar);
    if(esineg != null) {
      lottoAggiudicato.setAWARDEDNO(empty);
      if(esineg == 2 || esineg == 3){
        lottoAggiudicato.setNOAWARDEDTYPE(W3Z72Type.Enum.forInt(2));
      }else{
        lottoAggiudicato.setNOAWARDEDTYPE(W3Z72Type.Enum.forInt(1));
      }
    }else{
      lottoAggiudicato.setAWARDEDYES(empty);
      if(dattoaDate != null){
        Calendar dattoa = Calendar.getInstance();
        dattoa.setTime(dattoaDate);
        lottoAggiudicato.setCONTRACTAWARDDATE(dattoa);
      }
      List listDitg = sqlManager.getListVector("select ngara5 from ditg where ngara5 = ? and invoff = '1'",
          new Object[] { ngara });

      lottoAggiudicato.setOFFERSRECEIVED(listDitg.size());

      if(ditta != null){
        EconomicOperatorType ecoOpe[] = null;
        Long tipimp = (Long) sqlManager.getObject(
            "select tipimp from impr where codimp = ?",
            new Object[] { ditta });
          if(tipimp != null && (tipimp == 10 || tipimp == 3)){
            lottoAggiudicato.setAWARDEDGROUP(SnType.Enum.forInt(1));
            List listRagimp = sqlManager.getListVector("select coddic from ragimp where codime9 = ?",
                new Object[] { ditta });
            ecoOpe = new EconomicOperatorType[listRagimp.size()];
            for(int i=0; i<listRagimp.size(); i++) {
              String dittaRT  = (String) SqlManager.getValueFromVectorParam(
                  listRagimp.get(i), 0).getValue();
              ecoOpe[i] = this.getEconomicOperatorInfo(dittaRT);
              }
            }else if(aqoper != null && aqoper.intValue()==2){
              List datiDitgaq = sqlManager.getListVector(
                  "select dittao from ditgaq where ngara = ?",
                  new Object[] { ngara });
              if(datiDitgaq != null && datiDitgaq.size()>0){
                if(datiDitgaq.size()>1){
                  lottoAggiudicato.setAWARDEDGROUP(SnType.Enum.forInt(1));
                }else{
                  lottoAggiudicato.setAWARDEDGROUP(SnType.Enum.forInt(2));
                }
                ArrayList<EconomicOperatorType> operatoriDitgaq = new ArrayList<EconomicOperatorType>();
                for(int i=0;i<datiDitgaq.size();i++){
                  String dittao = (String) SqlManager.getValueFromVectorParam(datiDitgaq.get(i),0).getValue();
                  Long tipimpAq = (Long) sqlManager.getObject(
                      "select tipimp from impr where codimp = ?",
                      new Object[] { dittao });
                  if(tipimpAq != null && (tipimpAq == 10 || tipimpAq == 3)){
                    List listRagimp = sqlManager.getListVector("select coddic from ragimp where codime9 = ?",
                        new Object[] { dittao });
                    ecoOpe = new EconomicOperatorType[listRagimp.size()];
                    for(int j=0; j<listRagimp.size(); j++) {
                      String dittaRT  = (String) SqlManager.getValueFromVectorParam(
                          listRagimp.get(j), 0).getValue();
                      EconomicOperatorType operatore = this.getEconomicOperatorInfo(dittaRT);
                      operatoriDitgaq.add(operatore);
                      }
                    }else{
                      EconomicOperatorType operatore = this.getEconomicOperatorInfo(dittao);
                      operatoriDitgaq.add(operatore);
                    }
                }
                ecoOpe = operatoriDitgaq.toArray(new EconomicOperatorType[operatoriDitgaq.size()]);
              }
            } else{
            lottoAggiudicato.setAWARDEDGROUP(SnType.Enum.forInt(2));
            ecoOpe = new EconomicOperatorType[1];
            ecoOpe[0] = this.getEconomicOperatorInfo(ditta);
          }
        lottoAggiudicato.setECONOMICOPERATORSArray(ecoOpe);
        //lottoAggiudicato.setAGREETOPUBLISHTENDERS(null);
        //lottoAggiudicato.setAGREETOPUBLISHCONTRACTOR(null);
        //lottoAggiudicato.setAGREETOPUBLISHVALUE(null);
        //lottoAggiudicato.setTENDERSEXCLUDED(null);
      }//fine ditta non esiste

        lottoAggiudicato.setNBSME(countOfferte.get(0).get(0).longValue().intValue());
        lottoAggiudicato.setOFFERSRECEIVEDELECTR(countOfferte.get(3).get(0).longValue().intValue());
        lottoAggiudicato.setNBOTHEREU(countOfferte.get(1).get(0).longValue().intValue());
        lottoAggiudicato.setNBOTHERNONEU(countOfferte.get(2).get(0).longValue().intValue());

	  if(iaggiu != null)
		lottoAggiudicato.setFINALCOST(iaggiu);
     }//fine non è annullata
    }//fine query non ha funzionato
    return lottoAggiudicato;
  }

  private List<Vector<JdbcParametro>> getCountDitteMPMIeEstere(String ngara) throws SQLException {
      String selectBase = "select count(1) from ditg";
      String joinIMPR1 = " join IMPR IMPR1 on ditg.dittao=impr1.codimp";
      String joinG_NAZIONI1 = " left join G_NAZIONI G_NAZIONI1 on impr1.nazimp=g_nazioni1.codice";
      String joinRAGIMP = " left join ragimp on ragimp.codime9=impr1.codimp and ragimp.impman = '1'";
      String joinIMPR2 = " left join IMPR IMPR2 on ragimp.coddic=impr2.codimp";
      String joinG_NAZIONI2 = " left join G_NAZIONI G_NAZIONI2 on impr2.nazimp=g_nazioni2.codice";
      String whereBase = " where ngara5=? and ditg.invoff='1' and rtofferta is null";
      String condizioneISMPMI = " and ((coddic is null and impr1.ismpmi='1') or (coddic is not null and impr2.ismpmi='1'))";
      String condizioneISMEMBROUE = " and ((coddic is null and g_nazioni1.ismembroue='1' and impr1.nazimp<>1) or (coddic is not null and g_nazioni2.ismembroue='1' and impr2.nazimp<>1))";
      String condizioneNotISMEMBROUE = " and ((coddic is null and g_nazioni1.ismembroue is null and impr1.nazimp<>1) or (coddic is not null and g_nazioni2.ismembroue is null and impr2.nazimp<>1))";
      String selectCountOfferte = selectBase + joinIMPR1 + joinRAGIMP + joinIMPR2 + whereBase + condizioneISMPMI
              + " UNION ALL "
              + selectBase + joinIMPR1 + joinG_NAZIONI1 + joinRAGIMP + joinIMPR2 + joinG_NAZIONI2 + whereBase + condizioneISMEMBROUE
              + " UNION ALL "
              + selectBase + joinIMPR1 + joinG_NAZIONI1 + joinRAGIMP + joinIMPR2 + joinG_NAZIONI2 + whereBase + condizioneNotISMEMBROUE
              + " UNION ALL "
              + selectBase + whereBase;
      return sqlManager.getListVector(selectCountOfferte, new Object[] {ngara,ngara,ngara,ngara});
  }

  private String getUid(int numeroFormulario, String codgar) throws SQLException{

    String uidQuery = "select uuid from garuuid where garuuid.codgar = ? and tipric = 'SIMAP-FS" + numeroFormulario + "'";
    String uuid = (String) sqlManager.getObject(
        uidQuery, new Object[] { codgar });
    logger.debug("getUid method finished, query = " + uidQuery + ", with param = " + codgar);

    if(uuid == null){
      Integer newId= this.genChiaviManager.getNextId("GARUUID");
      UUID newUuid = UUID.randomUUID();
      uuid = newUuid.toString();
      String insertGaruuid = "insert into garuuid(id,codgar,tipric,uuid) values(?,?,?,?)";
      this.sqlManager.update(insertGaruuid, new Object[] { newId,codgar,"SIMAP-FS"+numeroFormulario,uuid});
    }

    return uuid;
  }

	private String getUid(Long idScheda) throws SQLException {

		String uidQuery = "select UUID_EFORM from G1NPASCHEDA where id=?";
		String uuid = (String) sqlManager.getObject(uidQuery, new Object[] { idScheda });
		if (uuid == null) {
			UUID newUuid = UUID.randomUUID();
			uuid = newUuid.toString();
			String updateG1NPASCHEDA = "update G1NPASCHEDA set UUID_EFORM=? where id=?";
			this.sqlManager.update(updateG1NPASCHEDA, new Object[] { uuid,idScheda });
		}
		return uuid;
	}

	public void eliminaEFORM(Long idForm) throws Throwable {
		String selectDati = "select idprg_eform, iddoc_eform from G1NPASCHEDA where idform=?";
		Vector<?> datiScheda = sqlManager.getVector(selectDati, new Object[] { idForm });
		String idPrg = SqlManager.getValueFromVectorParam(datiScheda, 0).stringValue();
		Long idDoc = SqlManager.getValueFromVectorParam(datiScheda, 1).longValue();
		if (idPrg != null && idDoc != null) {
            UploadInfo oldBucketInfo = ObjectStorageUtils.getUploadInfo("select bucket_ref, bucket_uuid from w_docdig where idprg=? and iddocdig=?",new Object[] {idPrg,idDoc});
            ObjectStorageUtils.deleteFile(oldBucketInfo.bucket_ref(),oldBucketInfo.uuid());
			sqlManager.update("delete from W_DOCDIG where idprg=? and iddocdig=?", new Object[] { idPrg, idDoc });
			sqlManager.update("update g1npascheda set idprg_eform=null, iddoc_eform=null, dnot_eform=null where idform=?", new Object[] { idForm });
		}
	}

  private EconomicOperatorType getEconomicOperatorInfo(String ditta) throws SQLException{
    EconomicOperatorType ecoOpe = EconomicOperatorType.Factory.newInstance();
    String selectEconomicOperator = "select nomest, " +
    		" cfimp, " +
    		" pivimp, " +
    		" indimp, " +
    		" nciimp, " +
    		" locimp, " +
    		" proimp, " +
    		" capimp, " +
    		" nazimp, " +
    		" telimp, " +
    		" faximp, " +
    		" emaiip, " +
    		" indweb, " +
    		" ismpmi " +
            ", emai2ip " +
    		" from impr where codimp = ?";
    List datiEcoOpe = sqlManager.getVector(selectEconomicOperator,
        new Object[] { ditta });
    if (datiEcoOpe != null && datiEcoOpe.size() > 0) {
      String nomest = (String) SqlManager.getValueFromVectorParam(datiEcoOpe,0).getValue();
      if(nomest != null) ecoOpe.setNOMEST(nomest);
      String cfimp = (String) SqlManager.getValueFromVectorParam(datiEcoOpe,1).getValue();
      if(cfimp != null) ecoOpe.setCFIMP(cfimp);
      else{String pivimp = (String) SqlManager.getValueFromVectorParam(datiEcoOpe,2).getValue();
        if(pivimp != null) ecoOpe.setPIVIMP(pivimp);}
      String indimp = (String) SqlManager.getValueFromVectorParam(datiEcoOpe,3).getValue();
      if(indimp != null) ecoOpe.setINDIMP(indimp);
      String nciimp = (String) SqlManager.getValueFromVectorParam(datiEcoOpe,4).getValue();
      if(nciimp != null) ecoOpe.setNCIIMP(nciimp);
      String locimp = (String) SqlManager.getValueFromVectorParam(datiEcoOpe,5).getValue();
      String proimp = (String) SqlManager.getValueFromVectorParam(datiEcoOpe,6).getValue();
      String town = "";
      if(locimp != null){town = town + locimp;}
      if(proimp != null){town = town +  " (" + proimp + ")";}
      if(town.length() > 0){ecoOpe.setTOWN(town);}
      String codnuts = (String) sqlManager.getObject(
          "select codice from tabnuts where siglaprov = ?", new Object[] { proimp });
      if(codnuts!=null){ecoOpe.setNUTS(codnuts);}
      String capimp = (String) SqlManager.getValueFromVectorParam(datiEcoOpe,7).getValue();
      if(capimp != null) ecoOpe.setCAPIMP(capimp);
      Long nazimp = (Long) SqlManager.getValueFromVectorParam(datiEcoOpe,8).getValue();
      if(nazimp != null) {
        ecoOpe.setNAZIMP(nazimp.intValue());
      }else{
        ecoOpe.setNAZIMP(1);
      }
      String telimp = (String) SqlManager.getValueFromVectorParam(datiEcoOpe, 9).getValue();
      if(telimp != null) ecoOpe.setTELIMP(telimp);
      String faximp = (String) SqlManager.getValueFromVectorParam(datiEcoOpe,10).getValue();
      if(faximp != null) ecoOpe.setFAXIMP(faximp);
      String emaiip = (String) SqlManager.getValueFromVectorParam(datiEcoOpe,11).getValue();
      String emai2ip = (String) SqlManager.getValueFromVectorParam(datiEcoOpe,14).getValue();
      if(StringUtils.isNotEmpty(emai2ip)) ecoOpe.setEMAIIP(emai2ip);
      else if(emaiip != null) ecoOpe.setEMAIIP(emaiip);
      String indweb = (String) SqlManager.getValueFromVectorParam(datiEcoOpe,12).getValue();
      if(indweb != null) ecoOpe.setINDWEB(indweb);
      String sme = (String) SqlManager.getValueFromVectorParam(datiEcoOpe,13).getValue();
      if(sme != null && sme.equals("1")){
        ecoOpe.setSME(SnType.Enum.forString("1"));
        }
      else{
        ecoOpe.setSME(SnType.Enum.forString("2"));
        }
      }
    return ecoOpe;
  }

	public static String getUrlDocumentiGara(String codgar) {
		String urlDocumentiGara = ConfigManager.getValore(PROP_URL_PUBBLICA);
        String codice = "";
        if (codgar.indexOf("$") == 0) {
            codice = codgar.substring(1);
        } else {
            codice = codgar;
        }
        String urlProcedure = ConfigManager.getValore(PROP_URL_PROCEDURE);
        if (urlDocumentiGara.endsWith("/") && urlProcedure.startsWith("/")) {
            urlProcedure = urlProcedure.substring(1);
        }
        urlDocumentiGara += urlProcedure + codice;
		return urlDocumentiGara;
	}

}

