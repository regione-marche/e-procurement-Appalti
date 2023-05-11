<%
/*
 * Created on: 30-apr-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 /* Definizione di costanti comuni alle varie jsp coinvolte nel wizard delle
  * fasi di gara 
  */
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<% // All'intero che rappresenta ciascuno step del wizard ricezione offerte si %>
<% // associazione una variabile per future rivesitazioni del wizard stesso    %>
<% // Fase Apertura documentazione amministrativa (pagina a lista) %>
<c:set var="step1Wizard" value="20" scope="request" />
<% // Fase Sorteggio controllo requisiti (pagina a lista) %>
<c:set var="step2Wizard" value="30" scope="request" />
<% // Fase Chiusura verifica doc. amministrativa (pagina a scheda) %>
<c:set var="step3Wizard" value="35" scope="request" />
<% // Fase Esito controllo sorteggiate (pagina a lista) %>
<c:set var="step4Wizard" value="40" scope="request" />
<% // Fase Conclusione comprova requisiti (pagina a scheda) %>
<c:set var="step5Wizard"  value="45" scope="request" />
<% // Fase Valutazione tecnica (pagina a lista) %>
<c:set var="step6Wizard"  value="50" scope="request" />
<% // Fase Chiusura valutazione tecnica (pagina a scheda) %>
<c:set var="step6_5Wizard"  value="55" scope="request" />
<% // Fase Apertura offera economica (pagina a lista) %>
<c:set var="step7Wizard" value="60" scope="request" />
<% // Fase Apertura asta elettronica %>
<c:set var="step7_5Wizard" value="65" scope="request" />
<% // Fase Calcolo aggiudicazione (pagina a lista) %>
<c:set var="step8Wizard" value="70" scope="request" />
<% // Fase Aggiudicazione provvisoria (pagina a scheda) %>
<c:set var="step9Wizard"  value="75" scope="request" />
<% // Fase Aggiudicazione definitiva (pagina a scheda) %>
<c:set var="step10Wizard"  value="80" scope="request" />

<% // Il campo GARE.FASGAR per compatibilita' con PWB assume valore pari a %>
<% // floor(GARE.STEPGAR/10), cioè il piu' grande intero minore o uguale a %>
<% // GARE.STEPGAR/10 %>

