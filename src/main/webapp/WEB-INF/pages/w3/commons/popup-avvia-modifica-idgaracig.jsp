
<%
	/*
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
%>


<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:setString name="titoloMaschera"  value='Richiesta di modifica dati' />
	
	<c:set var="entita" value="${param.entita}" />
	<c:set var="numgara" value="${param.numgara}" />
	<c:set var="numlott" value="${param.numlott}" />
	
	<gene:redefineInsert name="corpo">
		<table class="dettaglio-notab">
			<tr>
				<td class="valore-dato" colspan="2">
					<br>
					Questa funzione <b>prepara i dati e li invia all'ANAC</b>
					per la <b>modifica dei dati precedentemente inviati.</b>
					<br>
					<br>
					Tale richiesta, tuttavia, è possibile solamente se tutti i dati inseriti 
					superano i controlli di validit&agrave;.
					<br>
					<br>
				</td>
			</tr>

			<tr>
				<td colspan="2" class="comandi-dettaglio">
					<c:choose>
						<c:when test="${entita eq 'W3GARA'}">
							<INPUT type="button" class="bottone-azione" value="Controlla i dati ed invia la richiesta" title="Controlla i dati ed invia la richiesta" onclick="javascript:avviaModificaIDGARACIG('${entita}','${numgara}','1');">		
						</c:when>
						<c:when test="${entita eq 'W3LOTT'}">
							<INPUT type="button" class="bottone-azione" value="Controlla i dati ed invia la richiesta" title="Controlla i dati ed invia la richiesta" onclick="javascript:avviaModificaIDGARACIG('${entita}','${numgara}','${numlott}');">		
						</c:when>
					</c:choose>
					<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
	
	<gene:javaScript>
		document.forms[0].jspPathTo.value="w3/commons/popup-avvia-modifica-idgaracig.jsp";
		
		function annulla(){
			window.close();
		}
		
		function avviaModificaIDGARACIG(entita,numgara,numlott){
			var action = "${pageContext.request.contextPath}/w3/AvviaModificaIDGARACIG.do";
			action=action + '?' + csrfToken + '&entita=' + entita + '&numgara=' + numgara + '&numlott=' + numlott;
			document.location.href=action;
		}
	
	</gene:javaScript>	
</gene:template>

</div>

