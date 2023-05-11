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

<c:set var="simogwsuser" value='${gene:callFunction3("it.eldasoft.sil.w3.tags.funzioni.GetUsernameSimogFunction", pageContext, param.numgara, param.codrup)}' scope="request"/>
<c:choose>
	<c:when test="${loginRPNT ne 1}">
		<input type="hidden" name="rpntFailed" value="1" />
	</c:when>
	<c:otherwise>
		<input type="hidden" name="rpntFailed" value="${erroreCredenzialiRPNT ? '1' : '0'}" />
	</c:otherwise>
</c:choose>
<input type="hidden" name="codrup" value="${codrup}" />
<input type="hidden" name="recuperauser" value="1" />
<input type="hidden" name="recuperapassword" value="1" />	
<input type="hidden" name="codeinFromGare" value="${param.codeinFromGare}" />
<c:choose>
	<c:when test="${empty simogwsuser && !isAbilitato}">
		<tr>
			<td class="valore-dato" colspan="2">
				<br>
				<c:choose>		
				<c:when test="${loginRPNT eq '1' && !erroreCredenzialiRPNT}">Il sistema risulta predisposto all'utilizzo delle credenziali RPNT per l'accesso a SIMOG. <br><br>In caso di errore, verrano richieste le credenziali del RUP.</c:when>
				<c:otherwise>Indicare le <b>credenziali</b> per l'accesso al servizio.</c:otherwise>
				</c:choose>	
				<br>
				<br>
			</td>
		</tr>
		<tr>	
			<td class="etichetta-dato">Utente/Password (*)</td>
			<td class="valore-dato">
				<c:if test="${empty codrup }">
					${sessionScope.profiloUtente.codiceFiscale}<input type="hidden" name="simogwsuser" id="simogwsuser" size="20" value='${sessionScope.profiloUtente.codiceFiscale}' readOnly/>
					<input type="${loginRPNT ne '1' || erroreCredenzialiRPNT ? 'password' : 'hidden'}" name="simogwspass" value="${loginRPNT ne '1' || erroreCredenzialiRPNT ? '' : '.'}" id="simogwspass" onfocus="javascript:resetRecuperaPassword();" onclick="javascript:resetRecuperaPassword();" size="20"/>
				</c:if>
				<c:if test="${!empty codrup}">
					<input type="text" name="simogwsuser" id="simogwsuser" size="20" />
					<input type="${loginRPNT ne '1' || erroreCredenzialiRPNT ? 'password' : 'hidden'}" name="simogwspass" id="simogwspass" value="................" onfocus="javascript:resetRecuperaPassword();" onclick="javascript:resetRecuperaPassword();" size="20"/>
				</c:if>
				
			</td>
		</tr>
						
	</c:when>
	<c:otherwise>	
		<tr>
			<td class="valore-dato" colspan="2">
				<br>
				<c:choose>		
				<c:when test="${loginRPNT eq '1' && !erroreCredenzialiRPNT}">Il sistema risulta predisposto all'utilizzo delle credenziali RPNT per l'accesso a SIMOG. <br><br>In caso di errore, verrano recuperate le credenziali del RUP.</c:when>
				<c:otherwise>Le <b>credenziali</b> del RUP per l'accesso al servizio sono state recuperate dal sistema.</c:otherwise>
				</c:choose>	
				<br>
				<br>
			</td>
		</tr>
		<tr>
			<c:choose>		
				<c:when test="${loginRPNT ne '1' || erroreCredenzialiRPNT}"><td class="etichetta-dato" >Utente/Password (*)</td></c:when>
				<c:otherwise><td class="etichetta-dato" style="width:400px">Codice fiscale del RUP</td></c:otherwise>
			</c:choose>	
			<td class="valore-dato">
			<c:choose>
			<c:when test="${!empty simogwsuser}">
				${simogwsuser}<input type="hidden" name="simogwsuser" id="simogwsuser" size="20" value="${simogwsuser}" />
				<input type="${loginRPNT ne '1' || erroreCredenzialiRPNT ? 'password' : 'hidden'}" readonly name="simogwspass" id="simogwspass" size="12" value="................" class="importoNoEdit"/>
			 </c:when>
			<c:otherwise>
				${sessionScope.profiloUtente.codiceFiscale}<input type="hidden" name="simogwsuser" id="simogwsuser" size="20" value="${sessionScope.profiloUtente.codiceFiscale}" />	
				<input type="${loginRPNT ne '1' || erroreCredenzialiRPNT ? 'password' : 'hidden'}" name="simogwspass" id="simogwspass" value="................" onclick="javascript:resetRecuperaPassword();" size="20"/>
			</c:otherwise>
			</c:choose>
			</td>
		</tr>
	</c:otherwise>
</c:choose>
				
<gene:javaScript>
	
	function resetRecuperaUser() {
		document.formIDGARACIG.recuperauser.value = "0";
	}

	function resetRecuperaPassword() {
		document.formIDGARACIG.recuperapassword.value = "0";
		resetPassword();
	}

	function resetPassword() {
		document.formIDGARACIG.simogwspass.value = "";
	}	
</gene:javaScript>	