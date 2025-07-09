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
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.tasks.ArchiviazioneDocumentiManager;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;
import net.sf.json.JSONObject;

public class GetListaDocumentiAction extends Action {

  /**
   * Manager per la gestione delle interrogazioni di database.
   */
  private ArchiviazioneDocumentiManager archiviazioneDocumentiManager;
  
  /**
   * @param sqlManager
   *        the sqlManager to set
   */
  public void setArchiviazioneDocumentiManager(ArchiviazioneDocumentiManager archiviazioneDocumentiManager) {
    this.archiviazioneDocumentiManager = archiviazioneDocumentiManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();

    String codgar = request.getParameter("codgar");
    String genere = request.getParameter("genere");
    String key1 = request.getParameter("key1");
    String stipula = request.getParameter("stipula");
    String dittaoFiltro = request.getParameter("dittao");
    boolean filtroDocumentiGara = request.getParameter("isDocumentiGara") != null && request.getParameter("isDocumentiGara").equals("true");
    boolean escludiEML = "true".equals(request.getParameter("escludiEML"));
    
    try {

      result = archiviazioneDocumentiManager.getListaDocumenti(codgar, genere, key1, stipula, dittaoFiltro, filtroDocumentiGara, escludiEML);

    } catch (SQLException e) {
      throw new JspException("Errore durante la selezione dei documenti della gara da archiviare", e);
    } catch (GestoreException e) {
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

