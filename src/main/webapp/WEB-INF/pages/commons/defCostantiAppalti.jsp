<%
/*
 * Created on: 13-apr-2018
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 /* Definizione di costanti per le jsp di Appalti
  */
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<% // Tipo di pubblicazione sul sito istituzionale %>
<% // 1 Regione Marche %>
<% // 2 ATC %>
<c:set var="tipoPubblicazioneSitoIstituzionale" value="sitoIstituzionale.ws.tipo" scope="request" />

<% // Url del ws del sito istituzionale %>
<c:set var="urlWsSitoIstituzionale" value="sitoIstituzionale.ws.url" scope="request" />

<% // Url del ws per verifiche art. 80 sulle ditte %>
<c:set var="urlWsArt80" value="art80.ws.url" scope="request" />

<% // gateway Url del ws per verifiche art. 80 sulle ditte %>
<c:set var="gatewayArt80" value="art80.ws.url.gateway" scope="request" />

<% // Url del ws per la richiesta del codice CIG %>
<c:set var="urlWsCig" value="it.eldasoft.inviodaticig.ws.url" scope="request" />

<% // Nome applicativo per l'interazione col ws per la richiesta del codice Cig %>
<c:set var="nomeAppicativoCig" value="it.eldasoft.inviodaticig.nomeApplicativo" scope="request" />

<% // Property che indica se la mail ? in carico al documentale %>
<c:set var="mailCaricoDocumentale" value="pg.wsdm.invioMailPec" scope="request" />

<% // Property che indica il prefisso del nome del file di cui fare il download su cui fare una richiesta a procedere %>
<c:set var="prefissoFileDownloadComuneBari" value="avviso.downloadDocumenti.prefissoQuantitativo" scope="request" />

<% // Property specifica di TITULUS che indica se attiva  la possibilit? di inserire documenti da protocollo%>
<c:set var="inserimentoDocDaProtocollo" value="wsdm.documentiDaProtocollo" scope="request" />

<% // Property che indica l'accesso alla funzine crea fascicolo%>
<c:set var="accessoCreaFascicolo" value="wsdm.accediCreaFascicolo" scope="request" />

<% // Property che indica l'accesso alla funzine fascicolo documentale della commessa%>
<c:set var="accessoFascicoloDocumentaleCommessa" value="wsdm.accediFascicoloDocumentaleCommessa" scope="request" />
