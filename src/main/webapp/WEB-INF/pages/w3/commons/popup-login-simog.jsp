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
	<gene:setString name="titoloMaschera" value='Login ai servizi SIMOG' />
	
	<gene:redefineInsert name="corpo">
		<form action="${contextPath}/w3/LoginSimog.do" method="post" name="formIDGARACIG">

			<input type="hidden" name="metodo" value="loginSimog" />
			<input type="hidden" name="memorizza" value="memorizza"/>	
			<gene:callFunction obj="it.eldasoft.sil.w3.tags.funzioni.GetIsRupFunction" />
			<c:set var="loginRPNT" value='${gene:callFunction("it.eldasoft.sil.w3.tags.funzioni.IsAttivaLoginRPNTFunction", pageContext)}' scope="request"/>
			
			<table class="dettaglio-notab">
			
			<c:choose>
			
				<c:when test="${!empty listaControlli}">
					<gene:set name="titoloGenerico" value="${titolo}" />
					<gene:set name="listaGenericaControlli" value="${listaControlli}" />
					<gene:set name="numeroErrori" value="${numeroErrori}" />
					<gene:set name="numeroWarning" value="${numeroWarning}" />
				
					<jsp:include page="../commons/popup-validazione-interno.jsp" />	
				</c:when>
				
				<c:otherwise>
					<jsp:include page="sezione-credenziali-simog.jsp"/>
				</c:otherwise>
			</c:choose>
				
				<tr>
					<td colspan="2" class="comandi-dettaglio">
						<c:if test="${empty listaControlli}">
						<INPUT type="button" class="bottone-azione" value="Login" title="Login" onclick="javascript:login();">
						</c:if>
						<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</form>	
	</gene:redefineInsert>
	
	<gene:javaScript>
	
		if("${isRup}"){
			window.opener.location.href = '${pageContext.request.contextPath}/ApriPagina.do?' + csrfToken + '&href=w3/w3deleghe/w3deleghe-scheda.jsp&modo=NUOVO';
			window.close();
		}
	
		document.forms[0].jspPathTo.value="w3/commons/popup-login-simog.jsp";
		
		function annulla() {
			window.close();
		}
		
		function login() {
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
