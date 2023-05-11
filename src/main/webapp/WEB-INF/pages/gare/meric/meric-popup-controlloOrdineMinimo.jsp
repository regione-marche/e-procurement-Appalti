<%
/*
 * Created on: 18-09-2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Popup controllo importo ordine minimo */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>



<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value="Controllo importo ordine minimo" />

<c:set var="controlloSuperato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlloImportoOrdineMinimoFunction", pageContext, param.id, param.ditta)}'/>
<c:set var="ragSociale" value="${param.ragSociale}"/>
<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="ANTICORLOTTI" gestisciProtezioni="false" >
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			Si &egrave; verificato se l'ordine per l'operatore '${ragSociale}' rispetta le soglie di importo minimo stabilite da catalogo.
			<br>La verifica consiste nel confrontare l'importo ordine minimo attribuito a categorie in catalogo 
			con l'importo totale dei prodotti dell'ordine che fanno riferimento a queste categorie.
			<br><br> 
			<c:choose>
				<c:when test='${controlloSuperato eq "true" and requestScope.nessunOrdineminImpostato eq "NO"}'>
					<b>Non sono state riscontrate anomalie.</b>
				</c:when>
				<c:when test='${controlloSuperato eq "true" and requestScope.nessunOrdineminImpostato eq "SI"}'>
					<b>Non risultano impostate soglie di importo minimo per i prodotti dell'ordine.</b>
				</c:when>
				<c:otherwise>
					<b>Sono state riscontrate delle anomalie.</b><br><br>
					Di seguito vengono elencati i casi in cui l'importo dell'ordine non raggiunge 
					la soglia indicata in catalogo:
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
		</td>
	</gene:campoScheda>
	
	<c:if test='${controlloSuperato ne "true"}'>
		<gene:campoScheda >
			<td colSpan="2">
				<textarea cols="95" rows="14" readonly="readonly">${requestScope.messaggi}</textarea>
			</td>
		</gene:campoScheda>
	</c:if>
		
	
	<input type="hidden" name="id" id="id" value="${id}" />
	</gene:formScheda>
  </gene:redefineInsert>

<gene:redefineInsert name="buttons">
	<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:window.close();">&nbsp;
</gene:redefineInsert>

	
</gene:template>
</div>

	