<%
/*
 * Created on: 15-11-2016
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
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

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:redefineInsert name="head">
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
		
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js?v=${sessionScope.versioneModuloAttivo}"></script>	
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmuffici.js?v=${sessionScope.versioneModuloAttivo}"></script>	
</gene:redefineInsert>



<c:set var="modo" value="NUOVO" scope="request" />

	<c:choose>
		<c:when test='${!empty param.gara}'>
			<c:set var="gara" value="${param.gara}" />
		</c:when>
		<c:otherwise>
			<c:set var="gara" value="${gara}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.entita}'>
			<c:set var="entita" value="${param.entita}" />
		</c:when>
		<c:otherwise>
			<c:set var="entita" value="${entita}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.idconfi}'>
			<c:set var="idconfi" value="${param.idconfi}" />
		</c:when>
		<c:otherwise>
			<c:set var="idconfi" value="${idconfi}" />
		</c:otherwise>
	</c:choose>
	
	
	
	<gene:setString name="titoloMaschera" value="Modifica unità operativa di competenza" />
		
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GARE" gestisciProtezioni="false" >
			
	<gene:campoScheda nome="msgPagina">
		<td colSpan="2">
			<br>
			Mediante tale funzione è possibile modificare l'unità operativa di competenza ai fini delle protocollazioni della gara.
			<br>
			<br>
			<span id="spanErr" style="display: none; color: red; font-weight:bold;">Si è presentato un errore, non è stato possibile terminare l'operazione.<br>Controllare il log per i dettagli<br><br></span>
		</td>
	</gene:campoScheda>
	<gene:campoScheda>
		<td colspan="2"><b>Parametri utente per l'inoltro delle richieste al servizio remoto</b></td>
	</gene:campoScheda>
	<gene:campoScheda>
		<tr id="rigaUtente">
			<td class="etichetta-dato">Utente (*)</td>
			<td class="valore-dato"><input id="username" name="username" title="Utente" class="testo" type="text" size="24" value="" maxlength="100"/></td>
		</tr>
		<tr id="rigaPassword">
			<td class="etichetta-dato">Password (*)</td>
			<td class="valore-dato"><input id="password" name="password" title="Password" class="testo" type="password" size="24" value="" maxlength="100"/></td>
		</tr>
		
	</gene:campoScheda>
	<gene:campoScheda>
		<td colspan="2"><b>Unit&agrave; operativa di competenza</b></td>
	</gene:campoScheda>
	<gene:campoScheda>
		<tr>
		<td class="etichetta-dato">Codice</td>
		<td class="valore-dato">
			<input id="uocompetenza" name="uocompetenza" title="Codice" class="testo" type="text" size="24" value="" maxlength="100">
			&nbsp;
			<a href="javascript:apriListaUffici();" title="Seleziona unit&agrave; operativa" id="selezioneuocompetenza">
				Seleziona unit&agrave; operativa
			</a>
		</td>
		</tr>
		<tr>
		<td class="etichetta-dato">Descrizione</td>
		<td class="valore-dato" id="tdDescrizioneUo">
		<textarea id="uocompetenzadescrizione" name="uocompetenzadescrizione" title="Descrizione" class="testo" rows="4" cols="45" style="resize: none;border: none; outline: none;"></textarea>
			
		</td>
		</tr>
	</gene:campoScheda>
		
		<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}" />
		<input type="hidden" name="gara" id="gara" value="${gara}" />
		<input id="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
		<input id="syscon" type="hidden" value="${profiloUtente.id}" /> 
		<input type="hidden" id="tiposistemaremoto" name = "tiposistemaremoto" value="ENGINEERINGDOC" />
		<input id="tabellatiInDB" type="hidden" value="" />
		<input id="idprg" type="hidden" name="idprg" value="PG" />
		<input id="entita" type="hidden" value="${entita }" /> 
		<input id="key1" type="hidden" name="key1" value="${gara }" /> 
		<input id="key2" type="hidden" name="key2" value="" /> 
		<input id="key3" type="hidden" name="key3" value="" /> 
		<input id="key4" type="hidden" name="key4" value="" /> 
		<input id="abilitazioneGare" type="hidden" value="${profiloUtente.abilitazioneGare}" />
		
	</gene:formScheda>
  </gene:redefineInsert>
		
	
	<gene:redefineInsert name="buttons">
			<INPUT type="button" id="pulsanteConferma" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma()">&nbsp;
			<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
	</gene:redefineInsert>

	
	
	<gene:javaScript>
		var tdLunghezza=$("#tdDescrizioneUo").width();
		$("#uocompetenzadescrizione").css({"width":tdLunghezza});
		$("#spanErr").hide();
		_getWSLogin();
		
		$("#username").prop("readonly", true);
		$("#username").addClass("readonly");
		$("#password").prop("readonly", true);
		$("#password").addClass("readonly");
		
		$("#uocompetenza").prop("readonly", true);
		$("#uocompetenza").addClass("readonly");
		$("#uocompetenzadescrizione").prop("readonly", true);
		$("#uocompetenzadescrizione").addClass("readonly");
		
		_getWSFascicolo("ENGINEERINGDOC");
		
		function apriListaUffici(modo) {
			_ctx = "${pageContext.request.contextPath}";
			$("#finestraListaUffici").dialog('option','width',500);
			$("#finestraListaUffici").dialog('option','height',400);
			$("#finestraListaUffici").dialog("open");
			_creaContainerListaUffici();
		}
		
		function conferma() {
			var uocompetenza = $("#uocompetenza").val();
			if(uocompetenza==null || uocompetenza ==""){
				alert("Non è stata selezionata una unità operativa");
				return;
			}
			
			var esito = aggiornaUnitaCompetenzaDb();
			if(esito==1){
				window.opener.selezionaPagina(window.opener.document.pagineForm.activePage.value);
				window.close();
			}else{
				$("#spanErr").show();
			}
		}
		
					
			
		function annulla(){
			window.close();
		}
			
			
	</gene:javaScript>
</gene:template>
</div>
