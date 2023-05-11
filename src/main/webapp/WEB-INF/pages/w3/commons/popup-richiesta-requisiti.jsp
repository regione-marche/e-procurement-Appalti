
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
	
		<c:if test="${entita eq 'W3GARAREQ'}">
			<gene:setString name="titoloMaschera"  value='Richiesta requisiti gara' />
		</c:if>

	<c:set var="loginRPNT" value='${gene:callFunction("it.eldasoft.sil.w3.tags.funzioni.IsAttivaLoginRPNTFunction", pageContext)}' scope="request"/>
	
	<gene:redefineInsert name="corpo">
		<form action="${contextPath}/w3/RichiestaRequisiti.do" method="post" name="formIDGARACIG">
		
			<c:set var="entita" value="${param.entita}" scope="request"/>
		
			<input type="hidden" name="entita" value="${entita}" />
			<input type="hidden" name="numgara" value="${param.numgara}" />
			<input type="hidden" name="idgara" value="${param.idgara}" />
			<input type="hidden" name="numreq" value="${param.numreq}" />
			
			<table class="dettaglio-notab">
			<c:set var="entitaFnc" value ="CREDENZRUP" />
			<c:set var="numgara" value="${param.numgara}" />
			<c:set var="stato" value='${gene:callFunction5("it.eldasoft.sil.w3.tags.funzioni.GestioneValidazioneIDGARACIGFunction",pageContext,entitaFnc,profiloUtente.id,numgara,entita)}'/>	
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
				</c:otherwise>
			</c:choose>
				<tr>
					<td colspan="2" class="comandi-dettaglio">
						<c:if test="${erroreInvioRichiestaSimog ne 'true'}">
							<INPUT type="button" class="bottone-azione" value="Invia richiesta requisiti" title="Invia richiesta requisiti" onclick="javascript:richiestaRequisiti();">
						</c:if>
						<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</form>	
	</gene:redefineInsert>
	
	<gene:javaScript>
		document.forms[0].jspPathTo.value="w3/commons/popup-richiesta-requisiti.jsp";
		
		function annulla(){
			window.close();
		}
		
		function richiestaRequisiti(){
		
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

