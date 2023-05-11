
<%
	/*
	 * Created on 30-Aug-2022
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */

	// Scheda degli intestatari della concessione stradale
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<gene:callFunction obj="it.eldasoft.sil.w3.tags.funzioni.GetIsRupFunction" />

<div style="width:97%;">

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="W3LOADER_APPALTO_USR-Scheda" schema="W3">
<c:set var="loginRPNTenabled" value='${gene:callFunction("it.eldasoft.sil.w3.tags.funzioni.IsAttivaLoginRPNTFunction", pageContext)}' scope="request"/>

	
	<gene:setString name="titoloMaschera" value='Imposta credenziali RUP' />
	
	<gene:redefineInsert name="corpo">
		<form action="${contextPath}/w3/LoginRupSimog.do" method="post" name="formIDGARACIG">

			<input type="hidden" name="metodo" value="loginSimog" />
			<input type="hidden" name="memorizza" value="memorizza"/>	
			<input type="hidden" name="rpntFailed" value="1" />
			
			<table class="dettaglio-notab">
			
			<c:choose>
				<c:when test="${isAbilitato}">
					<c:choose>
						<c:when test="${isRup eq 'true'}">
							<br>
								Le credenziali di accesso ai servizi SIMOG sono state impostate.
								<br>
								<br>
								<td colspan="2" class="comandi-dettaglio">
								<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:historyVaiIndietroDi(1);">&nbsp;&nbsp;
							</td>
						</c:when>
						<c:otherwise>
						<tr>
							<td class="valore-dato" colspan="2">
							<br>
							Mediante questa funzione è possibile impostare nel sistema le proprie credenziali ai <b>servizi SIMOG</b>.
							<br>
							<br>
							Per l'utente corrente le credenziali <b>risultano già impostate</b>.
							<br>
							<br>
							Per modificare la password, digitare il nuovo valore e procedere con l'operazione di <b>Login</b>.
							<br>
							Per eliminare le credenziali, procedere con l'operazione di <b>Elimina credenziali</b>. 
							<br>
							<br>
						</tr>
						
							<jsp:include page="sezione-credenziali-simog.jsp"/>
							
							<tr>
							<td colspan="2" class="comandi-dettaglio">
								<INPUT type="button" class="bottone-azione" value="Login" title="Login" onclick="javascript:login();">&nbsp;&nbsp;
								<INPUT type="button" class="bottone-azione" value="Elimina credenziali" title="Elimina credenziali" onclick="javascript:deleteCredenzialiSimog();">
								<br><br>
								<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:historyVaiIndietroDi(1);">&nbsp;&nbsp;
							</td>
						</tr>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${isRup eq 'false'}">
							<br>
								Le credenziali di accesso ai servizi SIMOG sono state rimosse.
								<br>
								<br>
								<td colspan="2" class="comandi-dettaglio">
								<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:historyVaiIndietroDi(1);">&nbsp;&nbsp;
							</td>
						</c:when>
						<c:otherwise>
						<tr>
							<td class="valore-dato" colspan="2">
							<br>
							Mediante questa funzione è possibile impostare nel sistema le proprie credenziali ai <b>servizi SIMOG</b>.
							<br>
							<br>
							Per impostare le credenziali, digitare la password e procedere con l'operazione di <b>Login</b>.
							<br>
							<br>
						</tr>
						
							<jsp:include page="sezione-credenziali-simog.jsp"/>
							
							<tr>
							<td colspan="2" class="comandi-dettaglio">
								<INPUT type="button" class="bottone-azione" value="Login" title="Login" onclick="javascript:login();">&nbsp;&nbsp;
								<br><br>
								<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:historyVaiIndietroDi(1);">&nbsp;&nbsp;
							</td>
						</tr>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
			
			</table>
		</form>	
		<form action="${contextPath}/w3/CancellaCredenzialiSimog.do" method="post" name="formDeleteCredenzialiSimog">
	</gene:redefineInsert>
	
	<gene:redefineInsert name="addToAzioni">
		<c:if test="${empty isRup }">
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:login();" title="Nuovo" tabindex="1501">
					Login</a>
				</td>
			</tr>
				<c:if test="${isAbilitato}">
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:deleteCredenzialiSimog();" title="Elimina" tabindex="1503">
						Elimina credenziali</a>
					</td>
				</tr>
			</c:if>
		</c:if>
	</gene:redefineInsert>
	
	<gene:redefineInsert name="modelliPredisposti" />
	<gene:redefineInsert name="documentiAssociati"/>
	<gene:redefineInsert name="noteAvvisi" />
			
	<gene:javaScript>
	
		if("${isRup}"){
			window.opener.location.href = '${pageContext.request.contextPath}/ApriPagina.do?' + csrfToken + '&href=w3/w3deleghe/w3deleghe-scheda.jsp?'+csrfToken+'&modo=NUOVO';
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
		function deleteCredenzialiSimog() {
			var msg = "ATTENZIONE: cancellando le credenziali, i propri collaboratori non potranno più inviare i dati all'ANAC.\n\nContinuare?";
			var loginRPNTenabled = "${loginRPNTenabled eq '1'}";
			if(loginRPNTenabled === 'true'){
				msg = "Procedere con l'eliminazione delle credenziali?";
			}
			if(confirm(msg)){
				document.formDeleteCredenzialiSimog.submit();
			}
		}
	
	</gene:javaScript>	
</gene:template>