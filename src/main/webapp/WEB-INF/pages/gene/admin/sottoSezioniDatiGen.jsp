<%
/*
 * Created on 19-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO
 // DEI DATI GENERALI DI UN ACCOUNT CONTENENTE LA SEZIONE RELATIVA ALLE
 // SOTTOSEZIONI DELLA PAGINA STESSA.
 // QUESTA PAGINA E' STATA RIDEFINITA NEL PROGETTO PL-WEB PER UNA
 // PERSONALIZZAZIONE DEI DATI GENERALI DELL'ACCOUNT.
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="listaOpzioniUtenteSys" value="${fn:join(accountForm.opzioniUtenteSys,'#')}#" />
<c:set var="account" value="${accountForm}"/>
<c:set var="moduloAttivo" value="${sessionScope.moduloAttivo}" scope="request"/>

<c:if test='${moduloAttivo eq "PG" }'>
	<tr>
		<td colspan="2">
			<b>Configurazione Gare</b>
		</td>
	</tr>
	<tr>
		<td class="etichetta-dato">Privilegi dell'utente sulle gare</td>
		<td class="valore-dato">
 			<c:choose>
 				<c:when test='${account.abilitazioneGare eq "A"}'>
 					${account.listaTextGare[1]}
 					<c:if test='${fn:contains(listaOpzioniUtenteSys, "ou89#")}'>
 					<div class="info-wizard">ATTENZIONE: un utente "Amministratore di sistema" dovrebbe avere accesso solo alle funzionalità amministrative. Privilegi superiori a quello "utente" implicano una non conformit&agrave; alle norme (GDPR, AGID, ...).</div>
 					</c:if>
 				</c:when>
 				<c:when test='${account.abilitazioneGare eq "U"}'>
 					${account.listaTextGare[0]}
 				</c:when>
 			</c:choose>
		</td>
	</tr>
	<tr>
		<td class="etichetta-dato">Ruolo per procedure telematiche e mercato elettronico</td>
		<td class="valore-dato"> 
	      	<c:forEach items="${listaRuoliME}" var="RuoloME">
 	     		<c:if test="${RuoloME.tipoTabellato eq account.ruoloUtenteMercatoElettronico}">
		      	${RuoloME.descTabellato}
      			</c:if>
    	  	</c:forEach>
		</td>
	</tr>
	<tr>
		<td class="etichetta-dato" >Abilita funzioni di amministrazione sulle gare</td>
			<td class="valore-dato">
				<c:choose>
					<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou233#")}'>
						<c:out value="Si"/>
					</c:when>
					<c:otherwise>
						<c:out value="No"/>
					</c:otherwise>
				</c:choose>
			</td>
	</tr>
	<tr >
		<td class="etichetta-dato" >Privilegi dell'utente su selezione da elenco operatori</td>
  			<td class="valore-dato"> 
 				<c:choose>
 					<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou235#")}'>
 						${account.listaTextSelOp[1]}
 					</c:when>
 					<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou236#")}'>
 						${account.listaTextSelOp[2]}
					</c:when>
					<c:when test='${fn:contains(listaOpzioniUtenteSys, "ou237#")}'>
 						${account.listaTextSelOp[3]}
					</c:when>
 					<c:otherwise>
 						${account.listaTextSelOp[0]}
 					</c:otherwise>
 				</c:choose>
		</td>
  	</tr>
</c:if>
<c:set var="integrazioneLavori" value='${gene:isTable(pageContext,"APPA")}' />
<c:if test='${moduloAttivo eq "PL" || (moduloAttivo eq "PG" && integrazioneLavori eq true)}'>
	<tr>
		<td colspan="2">
			<b>Configurazione Lavori</b>
		</td>
	</tr>
	<tr>
		<td class="etichetta-dato">Privilegi dell'utente sui lavori</td>
		<td class="valore-dato">
 			<c:choose>
 				<c:when test='${account.abilitazioneLavori eq "A"}'>
 					${account.listaTextLavori[1]}
 					<c:if test='${fn:contains(listaOpzioniUtenteSys, "ou89#")}'>
 					<div class="info-wizard">ATTENZIONE: un utente "Amministratore di sistema" dovrebbe avere accesso solo alle funzionalità amministrative. Privilegi superiori a quello "utente" implicano una non conformit&agrave; alle norme (GDPR, AGID, ...).</div>
 					</c:if>
 				</c:when>
 				<c:when test='${account.abilitazioneLavori eq "U"}'>
 					${account.listaTextLavori[0]}
 				</c:when>
 			</c:choose>
		</td>
	</tr>
</c:if>