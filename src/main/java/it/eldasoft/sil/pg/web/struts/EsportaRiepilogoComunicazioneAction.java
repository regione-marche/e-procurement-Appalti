 /* Created on 23/dic/2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

package it.eldasoft.sil.pg.web.struts;

import it.eldasoft.gene.bl.FileManager;
import it.eldasoft.gene.bl.FileManagerException;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.TempFileUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.lowagie.text.DocumentException;

/**
 * Action per eseguire l'export lotti gara su foglio Excel
 *
 * @author Manuel.Bridda
 */
public class EsportaRiepilogoComunicazioneAction extends
        ActionBaseNoOpzioni {
  
    private static final String SUCCESS_DOWNLOAD         = "success";

    static Logger logger = Logger.getLogger(EsportaRiepilogoComunicazioneAction.class);

    private GestioneWSDMManager gestioneWSDMManager;
    
    private FileManager fileManager;

    public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
      this.gestioneWSDMManager = gestioneWSDMManager;
    }
    
    public void setFileManager(FileManager fileManager) {
      this.fileManager = fileManager;
    }

    @Override
    protected ActionForward runAction(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
                    throws IOException, ServletException {
        if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
        
        String messageKey = null;
        String target = SUCCESS_DOWNLOAD;
        
        //recupero le info
        String idprg= request.getParameter("idprg");
        String idcom= request.getParameter("idcom");
        
       ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
       
        
        InputStream iccInputStream = new FileInputStream(request.getSession(true).getServletContext().getRealPath("/WEB-INF/jrReport/sRGB_v4_ICC_preference.icc"));
        
        if(idprg!= null && idcom != null) {
          try {
            Long idcomm = Long.parseLong(idcom);
            byte[] pdf = this.gestioneWSDMManager.esportaRiepilogoComunicazione(idprg, idcomm, profilo.getNome(), iccInputStream);
            //this.fileManager.download("PG"+idcom+"_reportComunicazione.pdf", pdf ,response);
            File tempFile = TempFileUtilities.getTempFileSenzaNumeoRandom("PG"+idcom+"_reportComunicazione.pdf",
                request.getSession());
            FileOutputStream os = new FileOutputStream(tempFile);
            os.write(pdf);
            
            request.setAttribute("nomeFile", "PG"+idcom+"_reportComunicazione.pdf");
            request.setAttribute("esito", "ok");
            
          } catch (SQLException e) {
            target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
            messageKey = "errors.database.dataAccessException";
            this.aggiungiMessaggio(request, messageKey);
            logger.error(this.resBundleGenerale.getString(messageKey), e);
            e.printStackTrace();
          } catch (GestoreException g) {
            target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
            messageKey = "errors.database.dataAccessException";
            this.aggiungiMessaggio(request, messageKey);
            logger.error(this.resBundleGenerale.getString(messageKey), g);
            g.printStackTrace();
          }catch(NumberFormatException n) {
            target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
            messageKey = "EXPORT.IO";
            logger.error(messageKey,n);
            this.aggiungiMessaggio(request, messageKey);
            n.printStackTrace();
          } catch (DocumentException d) {
            target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
            messageKey = "EXPORT.IO";
            logger.error(messageKey,d);
           this.aggiungiMessaggio(request, messageKey);
            d.printStackTrace();
          } catch (Exception ex) {
            target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
            messageKey = "EXPORT.IO";
            logger.error(messageKey,ex);
            this.aggiungiMessaggio(request, messageKey);
            ex.printStackTrace();
          }
        }
        return mapping.findForward(target);
    }

}