package it.eldasoft.sil.pg.web.struts;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.utils.sicurezza.DatoBase64;
import it.eldasoft.utils.utility.UtilityDate;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetDettaglioProdottoAction extends Action {

  /**
   * Manager per la gestione delle interrogazioni di database.
   */
  private SqlManager          sqlManager;

  /**
   * Manager per la lettura dei file allegati
   */
  private FileAllegatoManager fileAllegatoManager;

  /**
   * @param sqlManager
   *        the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONArray jsonArray = new JSONArray();

    Long mericprod_id = new Long(request.getParameter("mericprod_id"));

    try {

      // 0 - Codice prodotto
      // 1 - Tipo di articolo (valore numerico)
      // 2 - Marca
      // 3 - Codice prodotto del produttore
      // 4 - Nome commerciale
      // 5 - Descrizione aggiuntiva
      // 6 - Dimensioni
      // 7 - Obbligo garanzia dell'articolo
      // 8 - Durata garanzia espressa in mesi
      // 9 - Modalita' di acquisto (valore numerico)
      // 10 - Unita' di misura su cui e' espresso il prezzo (valore numerico)
      // 11 - Prezzo
      // 12 - Num. unita' su cui e' espresso il prezzo componenti l'unita' di
      // misura a cui e' riferito l'acquisto
      // 13 - Unita' di misura a cui e' riferito l'acquisto (valore numerico)
      // 14 - Prezzo del prodotto
      // 15 - Aliquota IVA (valore numerico)
      // 16 - Lotto minimo per unita' di misura per il prodotto
      // 17 - Lotto minimo per unita' di misura per l'articolo
      // 18 - Tempo di consegna
      // 19 - Tempo di consegna espresso in... (valore numerico)
      // 20 - Identificativo del prodotto
      // 21 - Codice dell'impresa
      // 22 - Denominazione dell'impresa

      // Per la selezione dei dati dei prodotti si utilizza la vista
      // V_MEPRODOTTI
      // e non direttamente la tabella MEISCRIZPROD

      String selectMEISCRIZPROD = "select v_meprodotti.codoe, " // 0
          + " meartcat.tipo, " // 1
          + " v_meprodotti.marcaprodut, " // 2
          + " v_meprodotti.codprodut, " // 3
          + " v_meprodotti.nome, " // 4
          + " v_meprodotti.descagg, " // 5
          + " v_meprodotti.dimensioni, " // 6
          + " meartcat.obblgar, " // 7
          + " v_meprodotti.garanzia, " // 8
          + " meartcat.przunitper, " // 9
          + " meartcat.unimisprz, " // 10
          + " v_meprodotti.przunit, " // 11
          + " v_meprodotti.qunimisprz, " // 12
          + " meartcat.unimisacq, " // 13
          + " v_meprodotti.przunitprod, " // 14
          + " v_meprodotti.perciva, " // 15
          + " v_meprodotti.qunimisacq, " // 16
          + " meartcat.qunimisacq, " // 17
          + " v_meprodotti.tempocons, " // 18
          + " meartcat.unimistempocons, " // 19
          + " v_meprodotti.id, " // 20
          + " v_meprodotti.codimp " // 21
          + " from v_meprodotti, mericprod, meartcat "
          + " where v_meprodotti.id = mericprod.idprod "
          + " and v_meprodotti.idartcat = meartcat.id "
          + " and mericprod.id = ? ";

      List<?> datiMEISCRIZPROD = sqlManager.getListVector(selectMEISCRIZPROD, new Object[] { mericprod_id });
      if (datiMEISCRIZPROD != null && datiMEISCRIZPROD.size() > 0) {

        for (int i = 0; i < datiMEISCRIZPROD.size(); i++) {

          String meiscrizprod_codoe = (String) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 0).getValue();
          Long meartcat_tipo = (Long) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 1).getValue();
          String meiscrizprod_marcaprodut = (String) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 2).getValue();
          String meiscrizprod_codprodut = (String) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 3).getValue();
          String meiscrizprod_nome = (String) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 4).getValue();
          String meiscrizprod_descagg = (String) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 5).getValue();
          String meiscrizprod_dimensioni = (String) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 6).getValue();
          String meartcat_obblgar = (String) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 7).getValue();
          Long meiscrizprod_garanzia = (Long) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 8).getValue();
          Long meartcat_przunitper = (Long) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 9).getValue();
          Long meartcat_unimisprz = (Long) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 10).getValue();
          Double meiscrizprod_przunit = (Double) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 11).getValue();
          Double meiscrizprod_qunimisprz = (Double) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 12).getValue();
          Long meartcat_unimisacq = (Long) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 13).getValue();
          Double meiscrizprod_przunitprod = (Double) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 14).getValue();
          Long meiscrizprod_perciva = (Long) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 15).getValue();
          Double meiscrizprod_qunimisacq = (Double) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 16).getValue();
          Double meartcat_qunimisacq = (Double) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 17).getValue();
          Long meiscrizprod_tempocons = (Long) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 18).getValue();
          Long meartcat_unimistempocons = (Long) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 19).getValue();
          Long meiscrizprod_id = (Long) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 20).getValue();
          String meiscrizprod_codimp = (String) SqlManager.getValueFromVectorParam(datiMEISCRIZPROD.get(i), 21).getValue();
          String nomest = (String) sqlManager.getObject("select nomest from impr where codimp = ?", new Object[] {meiscrizprod_codimp});

          Object[] rowProdotto = new Object[32];
          rowProdotto[0] = meiscrizprod_codoe;
          rowProdotto[1] = meartcat_tipo;
          rowProdotto[2] = meiscrizprod_marcaprodut;
          rowProdotto[3] = meiscrizprod_codprodut;
          rowProdotto[4] = meiscrizprod_nome;
          rowProdotto[5] = meiscrizprod_descagg;
          rowProdotto[6] = meiscrizprod_dimensioni;
          rowProdotto[7] = meartcat_obblgar;
          rowProdotto[8] = meiscrizprod_garanzia;
          rowProdotto[9] = meartcat_przunitper;
          rowProdotto[10] = meartcat_unimisprz;
          rowProdotto[11] = meiscrizprod_przunit;
          rowProdotto[12] = meiscrizprod_qunimisprz;
          rowProdotto[13] = meartcat_unimisacq;
          rowProdotto[14] = meiscrizprod_przunitprod;
          rowProdotto[15] = meiscrizprod_perciva;
          rowProdotto[16] = meiscrizprod_qunimisacq;
          rowProdotto[17] = meartcat_qunimisacq;
          rowProdotto[18] = meiscrizprod_tempocons;
          rowProdotto[19] = meartcat_unimistempocons;
          rowProdotto[20] = meiscrizprod_id;
          rowProdotto[21] = meiscrizprod_codimp;
          rowProdotto[22] = nomest;
          

          // 23 - Modalita' di acquisto (descrizione)
          rowProdotto[23] = this.getDescrizione("ME003", meartcat_przunitper);

          // 24 - Unita' di misura su cui e' espresso il prezzo (descrizione)
          rowProdotto[24] = this.getDescrizione("ME007", meartcat_unimisprz);

          // 25 - Unita' di misura a cui e' riferito l'acquisto (descrizione)
          rowProdotto[25] = this.getDescrizione("ME007", meartcat_unimisacq);

          // 26 - Aliquota IVA (descrizione)
          rowProdotto[26] = this.getDescrizione("G_055", meiscrizprod_perciva);

          // 27 - Tempo di consegna espresso in... (descrizione)
          rowProdotto[27] = this.getDescrizione("ME004", meartcat_unimistempocons);

          // 28 - Data scadenza gia' formattata
          String selectDATSCADOFF = "select datscadoff from v_meprodotti where v_meprodotti.id = ?";
          Date meiscrizprod_datscadoff = (Date) this.sqlManager.getObject(selectDATSCADOFF, new Object[] { meiscrizprod_id });
          rowProdotto[28] = UtilityDate.convertiData(meiscrizprod_datscadoff, UtilityDate.FORMATO_GG_MM_AAAA);

          // Gestione dei documenti allegati
          String selectMEALLISCRIZPROD = "select w_docdig.idprg, w_docdig.iddocdig, w_docdig.dignomdoc "
              + " from v_meallprodotti, w_docdig "
              + " where v_meallprodotti.idprg = w_docdig.idprg "
              + " and v_meallprodotti.iddocdig = w_docdig.iddocdig "
              + " and v_meallprodotti.idprod = ?"
              + " and v_meallprodotti.tipo = ?";

          // 29 - Immagine
          List<Object> immagini = new Vector<Object>();
          List<?> datiIMMAGINI = sqlManager.getListVector(selectMEALLISCRIZPROD, new Object[] { meiscrizprod_id, new Long(1) });
          if (datiIMMAGINI != null && datiIMMAGINI.size() > 0) {
            for (int iImmagini = 0; iImmagini < datiIMMAGINI.size(); iImmagini++) {
              String idprg = (String) SqlManager.getValueFromVectorParam(datiIMMAGINI.get(iImmagini), 0).getValue();
              Long iddocdig = (Long) SqlManager.getValueFromVectorParam(datiIMMAGINI.get(iImmagini), 1).getValue();
              String dignomdoc = (String) SqlManager.getValueFromVectorParam(datiIMMAGINI.get(iImmagini), 2).getValue();

              // Lettura dello stream
              String base64String = null;

              try {
                BlobFile blobImage = fileAllegatoManager.getFileAllegato(idprg, iddocdig);
                InputStream in = new ByteArrayInputStream(blobImage.getStream());
                BufferedImage bi_original = ImageIO.read(in);
                
                Double max_width = new Double(100);
                Double max_height = new Double(100);
                Double ratio = new Double(1);
                
                Double scaled_width = new Double(bi_original.getWidth());
                Double scaled_height = new Double(bi_original.getHeight());

                if(scaled_width.doubleValue() > max_width.doubleValue()){
                    ratio = new Double(max_width.doubleValue() / scaled_width.doubleValue()); 
                    scaled_width = new Double(scaled_width.doubleValue() * ratio.doubleValue());
                    scaled_height = new Double(scaled_height.doubleValue() * ratio.doubleValue());
                }
                
                if(scaled_height.doubleValue() > max_height.doubleValue()){
                    ratio = new Double(max_height.doubleValue() / scaled_height.doubleValue());
                    scaled_width = new Double(scaled_width.doubleValue() * ratio.doubleValue());
                    scaled_height = new Double(scaled_height.doubleValue() * ratio.doubleValue());
                }
                
                BufferedImage bi_scaled = this.getScaledImage(bi_original, scaled_width.intValue(), scaled_height.intValue());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bi_scaled, "png", baos);
                baos.flush();
                DatoBase64 base64Image = new DatoBase64(baos.toByteArray(), DatoBase64.FORMATO_ASCII);
                baos.close();
                base64String = new String(base64Image.getByteArrayDatoBase64());
              } catch (Exception e) {

              }

              Object[] rowImmagine = new Object[4];
              rowImmagine[0] = idprg;
              rowImmagine[1] = iddocdig;
              rowImmagine[2] = dignomdoc;
              rowImmagine[3] = base64String;
              immagini.add(rowImmagine);

            }
            rowProdotto[29] = immagini;
          }

          // 30 - Certificazioni richieste
          List<Object> certificazioni = new Vector<Object>();
          List<?> datiCERTIFICAZIONI = this.sqlManager.getListVector(selectMEALLISCRIZPROD, new Object[] { meiscrizprod_id, new Long(2) });
          if (datiCERTIFICAZIONI != null && datiCERTIFICAZIONI.size() > 0) {
            for (int iCertificazioni = 0; iCertificazioni < datiCERTIFICAZIONI.size(); iCertificazioni++) {
              String idprg = (String) SqlManager.getValueFromVectorParam(datiCERTIFICAZIONI.get(iCertificazioni), 0).getValue();
              Long iddocdig = (Long) SqlManager.getValueFromVectorParam(datiCERTIFICAZIONI.get(iCertificazioni), 1).getValue();
              String dignomdoc = (String) SqlManager.getValueFromVectorParam(datiCERTIFICAZIONI.get(iCertificazioni), 2).getValue();
              Object[] rowCertificazione = new Object[3];
              rowCertificazione[0] = idprg;
              rowCertificazione[1] = iddocdig;
              rowCertificazione[2] = dignomdoc;
              certificazioni.add(rowCertificazione);
            }
            rowProdotto[30] = certificazioni;
          }

          // 31 - Certificazioni richieste
          List<Object> schedeTecniche = new Vector<Object>();
          List<?> datiSCHEDETECNICHE = this.sqlManager.getListVector(selectMEALLISCRIZPROD, new Object[] { meiscrizprod_id, new Long(3) });
          if (datiSCHEDETECNICHE != null && datiSCHEDETECNICHE.size() > 0) {
            for (int iSchedeTecniche = 0; iSchedeTecniche < datiSCHEDETECNICHE.size(); iSchedeTecniche++) {
              String idprg = (String) SqlManager.getValueFromVectorParam(datiSCHEDETECNICHE.get(iSchedeTecniche), 0).getValue();
              Long iddocdig = (Long) SqlManager.getValueFromVectorParam(datiSCHEDETECNICHE.get(iSchedeTecniche), 1).getValue();
              String dignomdoc = (String) SqlManager.getValueFromVectorParam(datiSCHEDETECNICHE.get(iSchedeTecniche), 2).getValue();
              Object[] rowSchedaTecnica = new Object[3];
              rowSchedaTecnica[0] = idprg;
              rowSchedaTecnica[1] = iddocdig;
              rowSchedaTecnica[2] = dignomdoc;
              schedeTecniche.add(rowSchedaTecnica);
            }
            rowProdotto[31] = schedeTecniche;
          }

          jsonArray.add(rowProdotto);

        }

      }

    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura delle informazioni relative al prodotto.", e);
    }

    out.println(jsonArray);
    out.flush();

    return null;

  }

  /**
   * Ricava la descrizione.
   * 
   * @param tab1cod
   * @param tab1tip
   * @return
   * @throws Exception
   */
  private String getDescrizione(String tab1cod, Long tab1tip) throws Exception {
    String descrizione = null;
    if (tab1tip != null) {
      descrizione = (String) sqlManager.getObject("select tab1desc from tab1 where tab1cod = ? and tab1tip = ?", new Object[] { tab1cod,
          tab1tip });
    }
    return descrizione;
  }

  /**
   * Ridimensiona l'immagine prima di trasferirla alla pagina
   * 
   * @param bi
   * @param width
   * @param height
   * @return
   */
  public BufferedImage getScaledImage(BufferedImage bi, int width, int height) {
    BufferedImage new_bi = new BufferedImage(width, height, bi.getType());
    Graphics g = new_bi.getGraphics();
    g.drawImage(bi, 0, 0, width, height, null);
    return new_bi;
  }

}
