
<%
  /*
			 * Created on 01-07-2013
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
<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<c:choose>
	<c:when test='${!empty param.importoSottoSoglia}'>
		<c:set var="importoSottoSoglia" value="${param.importoSottoSoglia}" />
	</c:when>
	<c:otherwise>
		<c:set var="importoSottoSoglia" value="${importoSottoSoglia}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.uuidTorn}'>
		<c:set var="uuidTorn" value="${param.uuidTorn}" />
	</c:when>
	<c:otherwise>
		<c:set var="uuidTorn" value="${uuidTorn}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.uuidGare1}'>
		<c:set var="uuidGare1" value="${param.uuidGare1}" />
	</c:when>
	<c:otherwise>
		<c:set var="uuidGare1" value="${uuidGare1}" />
	</c:otherwise>
</c:choose>

<c:set var="nomeApplicativo" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", nomeAppicativoCig)}'/>
<c:if test="${empty nomeApplicativo or nomeApplicativo eq '' }">
	<c:set var="nomeApplicativo" value='Vigilanza' />
</c:if>

<div style="width: 97%;"><gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:setString name="titoloMaschera" value='Invio dei dati a ${nomeApplicativo } per la richiesta di generazione del codice CIG' />

	<gene:redefineInsert name="corpo">
	
		<form action="${contextPath}/pg/InviaDatiRichiestaCig.do" method="post" name="formInviaDatiRichiestaCig" >
			<input type=hidden name="genere" value="${param.genere}" />
			 <input type=hidden name="codgar" value="${param.codgar}" />
			<input type=hidden name="numeroLotto" value="${param.numeroLotto}" />
			<input type=hidden name="importoSottoSoglia" value="${importoSottoSoglia}" />
			<input type=hidden name="uuidTorn" value="${uuidTorn}" />
			<input type=hidden name="uuidGare1" value="${uuidGare1}" />	
			
			<table class="dettaglio-notab">
				<tr>
					<td class="valore-dato" colspan="2">
						<br>
						Questa funzione prepara ed invia a ${nomeApplicativo} i dati della
						gara corrente per il successivo controllo ed invio al
						<b>SIMOG.</b>
						<br>
						<br>
						<br>
						<c:if test='${!profiloUtente.autenticazioneSSO}'>
							<b>Credenziali (utente e password) per la connessione al servizio</b>
							<br>
							<br>
							<input id="correnti" type="radio" name="credenziali" value="CORRENTI" checked onchange="javascipt:gestioneCredenziali()">Credenziali correnti
							<br>
							<input id="altre" type="radio" name="credenziali" value="ALTRE" onchange="javascipt:gestioneCredenziali()">Altre credenziali
							<br>
						</c:if>
					</td>
				</tr>
				
				<c:if test='${!profiloUtente.autenticazioneSSO}'>
				<tr>	
					<td class="etichetta-dato">Utente</td>
					<td class="valore-dato">
						<input type="text" name="username" size="15" disabled/>
					</td>
				</tr>
				<tr>
					<td class="etichetta-dato">Password</td>
					<td class="valore-dato">
						<input type="password" name="password" size="15" disabled/>
					</td>
				</tr>
				</c:if>
				
				<c:if test='${(param.genere eq "1" || param.genere eq "3") and param.numeroLotto ne null and param.numeroLotto ne ""}'>
					<c:set var="lotto" value="true"/>
				</c:if>
			 
				<tr>
					<td class="valore-dato" colspan="2"> 
						<br>
						<br>
						<br>
						<input id="fs1" type="radio" name="operazione" value="OP1" onclick="javascript:cambiaMetodo(this.value);" checked/><b>Invia dati gara</b>
						<br>
						<div id="sottotipoSelez" style="display:block;">
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="richiesta1" type="radio" name="tiporichiesta" value="Cig" <c:if test='${lotto ne "true"}'>checked</c:if>/>&nbsp;Richiesta CIG <span id="spanRichiestaCigInoltrata" style="display: none;"><b>&nbsp;&nbsp;(risulta già inviata una richiesta CIG per la gara)</b></span>
							<br>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="richiesta2" type="radio" name="tiporichiesta" value="SmartCig"/>&nbsp;Richiesta Smart CIG <span id="spanRichiestaSmartCigInoltrata" style="display: none;"><b>&nbsp;&nbsp;(risulta già inviata una richiesta Smart CIG per la gara)</b></span>
							<br>
						</div>
						<br>
						<input id="fs2" type="radio" name="operazione" value="OP2" onclick="javascript:cambiaMetodo(this.value);" <c:if test='${lotto eq "true"}'>checked</c:if>/><b>Consulta dati gara</b>
						<br>
						<br>						
					</td>
				</tr>
			 			
			  
	
				<tr>
					<td colspan="2" class="comandi-dettaglio">
						<INPUT type="button" class="bottone-azione" value="Invia richiesta" title="Invia richiesta"	onclick="javascript:inviadatirichiestacig();">
						<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</form>	
	</gene:redefineInsert>

	<gene:javaScript>
		
		<c:choose>
			<c:when test='${(param.genere eq "1" || param.genere eq "3") and param.numeroLotto ne null and param.numeroLotto ne ""}'>
			var lotto="true";
			</c:when>
			<c:otherwise>
			var lotto="false";
			</c:otherwise>
		</c:choose>
		var genere="${param.genere }";
		var uuidTorn =document.formInviaDatiRichiestaCig.uuidTorn.value;
		var uuidGare1 =document.formInviaDatiRichiestaCig.uuidGare1.value;
		var smartCigEsistente=false;
		if(uuidTorn==uuidGare1 && uuidTorn!="" && uuidTorn!=null)
			smartCigEsistente=true;
		var cigEsistente=false;
		
		
		var importoSottoSoglia = document.formInviaDatiRichiestaCig.importoSottoSoglia.value;
		if(importoSottoSoglia=="false" || genere!=2)
			document.formInviaDatiRichiestaCig.richiesta2.disabled = true;
		if(lotto=="true"){
			document.formInviaDatiRichiestaCig.fs1.disabled = true;
			document.formInviaDatiRichiestaCig.richiesta1.disabled = true;
			document.formInviaDatiRichiestaCig.richiesta2.disabled = true;
		}
		if(genere==2){
			if(smartCigEsistente){
				document.formInviaDatiRichiestaCig.richiesta1.disabled = true;
				document.formInviaDatiRichiestaCig.richiesta2.checked = true;
				$("#spanRichiestaSmartCigInoltrata").show();
			}else if(uuidTorn!=null && uuidTorn!=""){
				document.formInviaDatiRichiestaCig.richiesta1.checked = true;
				document.formInviaDatiRichiestaCig.richiesta2.disabled = true;
				$("#spanRichiestaCigInoltrata").show();
			}
		}else{
			if(uuidTorn!=null && uuidTorn!=""){
				document.formInviaDatiRichiestaCig.richiesta1.checked = true;
				document.formInviaDatiRichiestaCig.richiesta2.disabled = true;
				$("#spanRichiestaCigInoltrata").show();
			}
		}
			
		
		
		function cambiaMetodo(operazione){
			if(operazione =='OP2'){
				$('input:radio[name="tiporichiesta"]').each(function () { $(this).prop('checked', false); });
			}else{
				if($('#richiesta1').is(':enabled'))
					$('#richiesta1').prop('checked',true);
				else
					$('#richiesta2').prop('checked',true);
			}
		}
		
		function annulla(){
			window.close();
		}
		
		
		function inviadatirichiestacig() {
		
			var invia = "true";
			
			<c:if test='${!profiloUtente.autenticazioneSSO}'>
			var altre = document.formInviaDatiRichiestaCig.altre;
			if (altre.checked) {
				var username = document.formInviaDatiRichiestaCig.username;
				var password = document.formInviaDatiRichiestaCig.password;
				
				if (username.value == "") {
					alert("Inserire l'utente");
					invia = "false";
				}
				
				if (password.value == "") {
					alert("Inserire la password");
					invia = "false";
				}
			}
			</c:if>
		
			
				
			if (invia == "true") {
				document.formInviaDatiRichiestaCig.submit();
				bloccaRichiesteServer();
			}
		}
		
		function gestioneCredenziali() {
			var altre = document.formInviaDatiRichiestaCig.altre;
			
			if (altre.checked) {
				document.formInviaDatiRichiestaCig.username.disabled = false;
				document.formInviaDatiRichiestaCig.password.disabled = false;					
			} else {
				document.formInviaDatiRichiestaCig.username.disabled = true;
				document.formInviaDatiRichiestaCig.password.disabled = true;
				document.formInviaDatiRichiestaCig.username.value = "";
				document.formInviaDatiRichiestaCig.password.value = "";
			}			
		}
		
	
	</gene:javaScript>
</gene:template></div>

