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

import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.sil.pg.bl.GestioneWSDMManager;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloAllegatoType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoResType;
import it.maggioli.eldasoft.ws.dm.WSDMProtocolloDocumentoType;

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

public class GetWSDMProtocolloAllegatoAction extends ActionBaseNoOpzioni {

  static Logger               logger = Logger.getLogger(GetWSDMProtocolloAllegatoAction.class);

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

      String username = request.getParameter("getprotocolloallegato_username");
      String password = request.getParameter("getprotocolloallegato_password");
      String ruolo = request.getParameter("getprotocolloallegato_ruolo");
      String nome = request.getParameter("getprotocolloallegato_nome");
      String cognome = request.getParameter("getprotocolloallegato_cognome");
      String codiceuo = request.getParameter("getprotocolloallegato_codiceuo");
      String idutente = request.getParameter("idutente");
      String idutenteunop = request.getParameter("idutenteunop");
      Long annoprotocollo = new Long(request.getParameter("getprotocolloallegato_annoprotocollo"));
      String numeroprotocollo = request.getParameter("getprotocolloallegato_numeroprotocollo");
      String nomeallegato = request.getParameter("getprotocolloallegato_nomeallegato");
      String tipoallegato = request.getParameter("getprotocolloallegato_tipoallegato");
      String servizio = request.getParameter("getprotocolloallegato_servizio");
      String idconfi = request.getParameter("getprotocolloallegato_idconfi");

      WSDMProtocolloDocumentoResType wsdmProtocolloDocumentoRes = this.gestioneWSDMManager.wsdmProtocolloLeggi(username, password, ruolo,
          nome, cognome, codiceuo, idutente, idutenteunop, annoprotocollo, numeroprotocollo, servizio, idconfi);

      if (wsdmProtocolloDocumentoRes.isEsito()) {
        WSDMProtocolloDocumentoType wsdmProtocolloDocumento = wsdmProtocolloDocumentoRes.getProtocolloDocumento();
        if (wsdmProtocolloDocumento != null) {
          if (wsdmProtocolloDocumento.getAllegati() != null) {
            WSDMProtocolloAllegatoType[] allegati = wsdmProtocolloDocumento.getAllegati();
            for (int a = 0; a < allegati.length; a++) {
              if (nomeallegato.equals(allegati[a].getNome()) && tipoallegato.equals(allegati[a].getTipo())) {
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
