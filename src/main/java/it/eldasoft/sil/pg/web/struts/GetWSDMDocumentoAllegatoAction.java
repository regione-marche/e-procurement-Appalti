/*
 * Created on 29/06/2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.sil.pg.web.struts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoType;

public class GetWSDMDocumentoAllegatoAction extends ActionBaseNoOpzioni {

  static Logger               logger = Logger.getLogger(GetWSDMDocumentoAllegatoAction.class);

  private GestioneWSDMManager gestioneWSDMManager;

  public void setGestioneWSDMManager(GestioneWSDMManager gestioneWSDMManager) {
    this.gestioneWSDMManager = gestioneWSDMManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("GetWSDMDocumentoAllegatoAction: inizio metodo");

    String target = null;
    String messageKey = null;

    try {

      String username = request.getParameter("getdocumentoallegato_username");
      String password = request.getParameter("getdocumentoallegato_password");
      String ruolo = request.getParameter("getdocumentoallegato_ruolo");
      String nome = request.getParameter("getdocumentoallegato_nome");
      String cognome = request.getParameter("getdocumentoallegato_cognome");
      String codiceuo = request.getParameter("getdocumentoallegato_codiceuo");
      String idutente = request.getParameter("idutente");
      String idutenteunop = request.getParameter("idutenteunop");
      String numeroDocumento = request.getParameter("getdocumentoallegato_numerodocumento");
      String nomeallegato = request.getParameter("getdocumentoallegato_nomeallegato");
      String tipoallegato = request.getParameter("getdocumentoallegato_tipoallegato");
      String servizio = request.getParameter("getdocumentoallegato_servizio");
      String idconfi = request.getParameter("getdocumentoallegato_idconfi");

      WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = this.gestioneWSDMManager.wsdmDocumentoLeggi(username, password, ruolo,
          nome, cognome, codiceuo, idutente, idutenteunop, numeroDocumento,servizio,idconfi);

      if (wsdmProtocolloDocumentoRes.isEsito()) {
        WSDMProtocolloDocumentoType wsdmProtocolloDocumento = wsdmProtocolloDocumentoRes.getProtocolloDocumento();
        if (wsdmProtocolloDocumento != null) {
          if (wsdmProtocolloDocumento.getAllegati() != null) {
            WSDMProtocolloAllegatoType[] allegati = wsdmProtocolloDocumento.getAllegati();
            boolean allegatoTrovato=false;
            for (int a = 0; a < allegati.length; a++) {
              allegatoTrovato=false;
              if(allegati[a]!=null){
                if (tipoallegato == null || "".equals(tipoallegato.trim()) || "null".equals(tipoallegato.trim())) {
                  if ((nomeallegato.equals(allegati[a].getNome())))
                    allegatoTrovato=true;
                }else {
                  String nometipoallegato = allegati[a].getNome() + "." + allegati[a].getTipo();
                  if ((nomeallegato.equals(allegati[a].getNome()) || nomeallegato.equals(nometipoallegato))
                      && tipoallegato.equals(allegati[a].getTipo()))
                    allegatoTrovato=true;
                }

                if (allegatoTrovato) {
                  String nomeAllegato = allegati[a].getNome();
                  byte[] contenutoAllegato = allegati[a].getContenuto();
                  response.setContentType("application/octet-stream");
                  response.setHeader("Content-Disposition", "attachment;filename=\"" + nomeAllegato + "\"");
                  ServletOutputStream output = response.getOutputStream();
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  baos.write(contenutoAllegato);
                  baos.close();
                  baos.writeTo(output);
                  output.flush();
                }
              }
            }
          }
        }
      }
    } catch (Throwable e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (messageKey != null) response.reset();

    if (logger.isDebugEnabled()) logger.debug("GetWSDMDocumentoAllegatoAction: fine metodo");

    return mapping.findForward(target);

  }
}
