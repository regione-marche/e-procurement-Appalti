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
  * fasi di ricezione offerte
  */
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<% // All'intero che rappresenta ciascuno step del wizard ricezione offerte si %>
<% // associazione una variabile per future rivesitazioni del wizard stesso    %>
<% // Fase Ricez. domande partecip. (pagina a lista) %>
<c:set var="step1Wizard" value="-50" scope="request" />
<% // Fase Apert. domande partecip. (pagina a lista) %>
<c:set var="step2Wizard" value="-40" scope="request" />
<% // Fase Elenco ditte invitate (pagina a lista) %>
<c:set var="step3Wizard" value="-30" scope="request" />
<% // Fase Inviti (pagina a scheda) %>
<c:set var="step4Wizard" value="-25" scope="request" />
<% // Fase Ricezioni plichi (pagina a lista) %>
<c:set var="step5Wizard"  value="10" scope="request" />
<% // Fase Chiusura Ricez. offerte (pagina a scheda) %>
<c:set var="step6Wizard"  value="20" scope="request" />

<% // Il campo GARE.STEPGAR viene aggiornato solo se assume un valore < step6wizard, %>
<% // poiche' tale valore implica che le fasi di gara siano state attivate. %>
<% // Il campo GARE.FASGAR viene aggiornato seguendo la stessa regola e, per %>
<% // compatibilita' con PWB, assume valore pari a floor(GARE.STEPGAR/10), cioè il %>
<% // piu' grande intero minore o uguale a GARE.STEPGAR/10 %>
