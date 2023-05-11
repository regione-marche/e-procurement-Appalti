package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.utils.sicurezza.DatoBase64;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author Cristian.Febas
 * 
 */
public class AnteprimaDocumentoAllegatoAction extends Action {

  private FileAllegatoManager fileAllegatoManager;

  /**
   * @param fileAllegatoManager
   *        the fileAllegatoManager to set
   */
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
    
    String idprg = request.getParameter("idprg");
    Long iddocdig = new Long(request.getParameter("iddocdig"));

    try {
      BlobFile blobImage = this.fileAllegatoManager.getFileAllegato(idprg,iddocdig);
      InputStream in = new ByteArrayInputStream(blobImage.getStream());
      BufferedImage bi_original = ImageIO.read(in);
  
      Double max_width = new Double(200);
      Double max_height = new Double(200);
      Double ratio = new Double(1);
  
      Double scaled_width = new Double(bi_original.getWidth());
      Double scaled_height = new Double(bi_original.getHeight());
  
      if (scaled_width.doubleValue() > max_width.doubleValue()) {
        ratio = new Double(max_width.doubleValue() / scaled_width.doubleValue());
        scaled_width = new Double(scaled_width.doubleValue() * ratio.doubleValue());
        scaled_height = new Double(scaled_height.doubleValue() * ratio.doubleValue());
      }
  
      if (scaled_height.doubleValue() > max_height.doubleValue()) {
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
      String base64String = new String(base64Image.getByteArrayDatoBase64());
      jsonArray.add(new Object[] { base64String });
    } catch (Exception e) {
      
    }
    out.println(jsonArray);
    out.flush();

    return null;

  }

  public BufferedImage getScaledImage(BufferedImage bi, int width, int height) {
    BufferedImage new_bi = new BufferedImage(width, height, bi.getType());
    Graphics g = new_bi.getGraphics();
    g.drawImage(bi, 0, 0, width, height, null);
    return new_bi;
  }

}
