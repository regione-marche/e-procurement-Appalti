<%
	/*
	 *
	 * Copyright (c) Maggioli S.p.A.
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
	<gene:setString name="titoloMaschera" value='Confronto dati con SIMOG' />
	<c:set var="loginRPNT" value='${gene:callFunction("it.eldasoft.sil.w3.tags.funzioni.IsAttivaLoginRPNTFunction", pageContext)}' scope="request"/>
	
	<gene:redefineInsert name="corpo">
		<form action="${contextPath}/w3/ConfrontaCIG.do" method="post" name="formIDGARACIG">

			<input type="hidden" name="numgara" value="${param.numgara}" />
			<input type="hidden" name="numlott" value="${param.numlott}" />
			<input type="hidden" name="cig"    value="${param.cig}" />
			<input type="hidden" name="metodo" value="avvioConfronto" />
			
			<table class="dettaglio-notab">
			<gene:set name="titoloGenerico" value="${titolo}" />
			<gene:set name="listaGenericaControlli" value="${listaControlli}" />
			<gene:set name="numeroErrori" value="${numeroErrori}" />
			<gene:set name="numeroWarning" value="${numeroWarning}" />
			<c:set var="notDelegato" value ="false"/>	
			<c:forEach items="${listaControlli}" step="1" var="controllo" varStatus="status" >
					<c:if test="${controllo[2] eq 'Delega del RUP assente'}">
						<c:set var="notDelegato" value="true"/>	
					</c:if>
			</c:forEach>
			<c:choose>
				<c:when test="${!empty listaControlli && ((loginRPNT ne '1') ||  (loginRPNT eq '1' && erroreCredenzialiRPNT) || notDelegato)}">
					<jsp:include page="../commons/popup-validazione-interno.jsp" />	
					<c:set var="erroreInvioRichiestaSimog" value="true"/>
				</c:when>
				<c:otherwise>
			
					<jsp:include page="sezione-credenziali-simog.jsp"/>
					<tr>
						<td class="valore-dato" colspan="2">
							<b><br><br>Informazioni sul confronto dati con SIMOG</b>
							<br><br>
							Sar&agrave; possibile confrontare i dati della gara e lotto con quelli presenti 
							<br>
							in SIMOG ed eventualmente aggiornare i dati in locale
							<br><br><br> 
						</td>
					</tr>
				</c:otherwise>
			</c:choose>
				
				<tr>
					<td colspan="2" class="comandi-dettaglio">
						<c:if test="${empty listaControlli}">
						<INPUT type="button" class="bottone-azione" value="Avvia confronto dati" title="Avvia confronto dati" onclick="javascript:confrontoDati();">
						</c:if>
						<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</form>	
	</gene:redefineInsert>
	
	<gene:javaScript>
		document.forms[0].jspPathTo.value="w3/commons/popup-riallinea-cig.jsp";
		
		function annulla() {
			window.close();
		}
		
		function confrontoDati() {
			var invia = "true";
		
			var simogwsuser = document.formIDGARACIG.simogwsuser;
			if (simogwsuser.value == "") {
				alert("Inserire l'utente");
				invia = "false";
			}
			
			var simogwspass = document.formIDGARACIG.simogwspass;			
			if (invia == "true" && simogwspass.value == "") {
				alert("Inserire la password");
				invia = "false";
			}

			if (invia == "true") {
				document.formIDGARACIG.submit();
				bloccaRichiesteServer();
			}
		}
	
	</gene:javaScript>	
</gene:template>

</div>
