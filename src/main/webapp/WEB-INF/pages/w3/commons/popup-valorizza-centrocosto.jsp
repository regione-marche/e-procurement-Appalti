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
<c:set var="loginRPNT" value='${gene:callFunction("it.eldasoft.sil.w3.tags.funzioni.IsAttivaLoginRPNTFunction", pageContext)}' scope="request"/>

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:setString name="titoloMaschera" value='Login ai servizi SIMOG' />
	
	
	<gene:redefineInsert name="corpo">
		<form action="${contextPath}/w3/ValorizzaCentroCosto.do" method="post" name="formIDGARACIG">

			<input type="hidden" name="metodo" value="loginSimog" />
			
			<table class="dettaglio-notab">
			<c:set var="entitaFnc" value ="CREDENZRUP" />
			<c:set var="codrup" value="${param.codrup}" />
			<c:set var="stato" value='${gene:callFunction5("it.eldasoft.sil.w3.tags.funzioni.GestioneValidazioneIDGARACIGFunction",pageContext,entitaFnc,profiloUtente.id,"0",codrup)}'/>	
			<gene:set name="titoloGenerico" value="${titolo}" />
			<gene:set name="listaGenericaControlli" value="${listaControlli}" />
			<gene:set name="numeroErrori" value="${numeroErrori}" />
			<gene:set name="numeroWarning" value="${numeroWarning}" />
			<c:set var="visualizzacontrolli" value="${!empty listaControlli && ((loginRPNT ne '1') ||  (loginRPNT eq '1' && erroreCredenzialiRPNT))}"/>
			
			<c:choose>
				<c:when test="${visualizzacontrolli}">
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
						<INPUT type="button" class="bottone-azione" value="Login" title="Login" onclick="javascript:login();">
						</c:if>
						<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</form>	
	</gene:redefineInsert>
	
	<c:if test="${!empty listaCC}">
	
	<gene:setString name="titoloMaschera" value='Scelta del centro di costo' />
	<gene:redefineInsert name="corpo">
	<table class="datilista">
		<thead>
		<tr>
			<th class="etichetta-dato"><center><b>Codice ufficio</b></center></th>
			<th class="etichetta-dato"><center><b>Codice fiscale</b></center></th>
			<th class="etichetta-dato"><center><b>Denominazione</b></center></th>
			<th class="etichetta-dato"><center><b>Centro di costo</b></center></th>
		</tr>
		</thead>
		<c:forEach items="${listaCC}" var="valoreCC" varStatus="status">
		<tr class="${status.getCount()%2==0 ? 'even' : 'odd'}" name="rowCC" id="row_${status.getCount()}">
		<td class="valore-dato">${valoreCC[5]}</td>
			<td class="valore-dato">${valoreCC[2]}</td>
			<td class="valore-dato">${valoreCC[3]}</td>
			<td class="valore-dato"><a href='javascript:getCC(${status.getCount()});'>${valoreCC[1]}</a></td>
			<input type="hidden" value="${valoreCC[0]}" name="rowId"/>
			<input type="hidden" value="${valoreCC[1]}" name="rowDesc"/>
			<input type="hidden" value="${valoreCC[2]}" name="rowCF"/>
			<input type="hidden" value="${valoreCC[3]}" name="rowAzi"/>
			<input type="hidden" value="${valoreCC[4]}" name="rowCodcc"/>
			<input type="hidden" value="${valoreCC[5]}" name="rowCodein"/>
		</tr>
		</c:forEach>
		<tr>
			<td colspan="4" class="comandi-dettaglio" style="text-align:right">
				<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
			</td>
		</tr>
	</table>
	</gene:redefineInsert>
	</c:if>
	
	
	
	<gene:javaScript>
		function getCC(idx){
			var id = $('#row_'+idx).find('input[name="rowId"]')[0].value;
			var denom = $('#row_'+idx).find('input[name="rowDesc"]')[0].value;
			var cf = $('#row_'+idx).find('input[name="rowCF"]')[0].value;
			var azi = $('#row_'+idx).find('input[name="rowAzi"]')[0].value;
			var codcc = $('#row_'+idx).find('input[name="rowCodcc"]')[0].value;
			var codein = $('#row_'+idx).find('input[name="rowCodein"]')[0].value;
			selezionaCC(id,denom,cf,azi,codcc,codein);
			
		}
		function selezionaCC(id, denom, cfein, nomein, codcc, codein){
			if(window.opener.document.forms[0].entita.value == "W3SMARTCIG"){
				window.opener.document.forms[0].W3SMARTCIG_IDCC.value = id;
				window.opener.document.forms[0].W3SMARTCIG_CODEIN.value = codein;
			}else{
				window.opener.document.forms[0].W3GARA_IDCC.value = id;
				window.opener.document.forms[0].W3GARA_CODEIN.value = codein;
			}
			window.opener.document.forms[0].CENTRICOSTO_DENOMCENTRO.value = denom;
			if(${empty sessionScope.uffint}){
				window.opener.document.getElementById("UFFINT_CFEINview").innerText = cfein;
				window.opener.document.getElementById("UFFINT_NOMEINview").innerText = nomein;
			}
			window.opener.document.getElementById("CENTRICOSTO_CODCENTROview").innerText = codcc;
			window.close();
		}
		
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
		
		$(document).ready(function(){
			if($('tr[name*="rowCC"]').length == 1){
				getCC(1);
			}
			var width = 800;
			var height = 500;
			var l = Math.floor((screen.availWidth - width)/2);
			var t = Math.floor((screen.availHeight - 500)/2);
			window.resizeTo(width,500);
			window.moveTo(l,t);
		})	
	
	</gene:javaScript>	
</gene:template>

</div>
