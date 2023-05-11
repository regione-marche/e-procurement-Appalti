<%
/*
 * Created on: 03-09-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Popup esporta dati Adempimenti legge 190/2012 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">


<c:choose>
	<c:when test='${not empty param.id}'>
		<c:set var="id" value="${param.id}" />
	</c:when>
	<c:otherwise>
		<c:set var="id" value="${id}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.anno}'>
		<c:set var="anno" value="${param.anno}" />
	</c:when>
	<c:otherwise>
		<c:set var="anno" value="${anno}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.urlsito}'>
		<c:set var="urlsito" value="${param.urlsito}" />
	</c:when>
	<c:otherwise>
		<c:set var="urlsito" value="${urlsito}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.isIntegrazionePortale}'>
		<c:set var="isIntegrazionePortale" value="${param.isIntegrazionePortale}" />
	</c:when>
	<c:otherwise>
		<c:set var="isIntegrazionePortale" value="${isIntegrazionePortale}" />
	</c:otherwise>
</c:choose>

<gene:setString name="titoloMaschera" value="Export dati anno ${anno}"/>
<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="ANTICORLOTTI" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupExportDatiAnticor">
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			 <c:choose>
			 	<c:when test='${requestScope.erroreOperazione eq "1" }'>
			 		Non è possibile procedere con l'export
			 	</c:when>
			 	<c:when test='${requestScope.operazioneEseguita eq "1" }'>
			 		Export eseguito con successo
			 	</c:when>
			 	<c:otherwise>
			 		Si vuole procedere con l'export dei dati?
			 	</c:otherwise>
			 </c:choose>
			 
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
	<gene:campoScheda nome="messaggioUrlsitoAdoperato">
		<td colspan="2" >
			<span  id="messaggioUrlsitoAdoperato" style="display: none; color: #0000FF;">
				<br>
				L'url inserito &egrave; gi&agrave; adoperato in altri adempimenti.
				<br><br>
			</span>
		</td>
	</gene:campoScheda>	
	<gene:campoScheda title="URL sito pubblicaz." campo="URL" campoFittizio="true" definizione="T200" defaultValue="${urlsito }" modificabile='${isIntegrazionePortale ne true and requestScope.operazioneEseguita ne "1"}' obbligatorio="true"/>
	<input type="hidden" name="id" id="id" value="${id}" />
	<input type="hidden" name="anno" id="anno" value="${anno}" />
	<input type="hidden" name="isIntegrazionePortale" id="isIntegrazionePortale" value="${isIntegrazionePortale}" />
	<input type="hidden" name="urlsito" id="urlsito" value="${urlsito}" />
	<gene:campoScheda>
		<td colspan="2" >
			<br>
		</td>	
	</gene:campoScheda>	
		
	</gene:formScheda>
  </gene:redefineInsert>

<c:if test='${requestScope.erroreOperazione eq "1"}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
	</gene:redefineInsert>
</c:if>
<c:if test='${requestScope.operazioneEseguita eq "1"}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
	</gene:redefineInsert>
</c:if>	
	<gene:javaScript>
		
		document.forms[0].jspPathTo.value="gare/anticor/popup-esportaDati.jsp";
		
		function conferma() {
			var protocollo = $("#URL").val();
			protocollo = protocollo.substring(0, 5);
			if ("http:".indexOf(protocollo) == -1 ){
				if (confirm("L'URL del sito di pubblicazione dei dati non contiene il protocollo http e pertanto non verr\u00E0 ritenuto corretto.\nL'indirizzo deve contenere obbligatoriamente il protocollo http e non https per essere ritenuto corretto.\n\nConfermi l'operazione?")) {
					document.forms[0].jspPathTo.value="gare/anticor/popup-esportaDati.jsp";
					schedaConferma();
				}
			}else{
				document.forms[0].jspPathTo.value="gare/anticor/popup-esportaDati.jsp";
				schedaConferma();
			}
		}
		
		function annulla(){
			window.close();
		}
		
		function chiudi(){
			window.opener.historyReload();
			window.close();
		}
		
		
		$('#URL').change(function() {
			if ($('#URL').val() != "") {
				   	urlAdoperata($('#URL').val(),${id });
			} else {
				$("#messaggioUrlsitoAdoperato").hide();
				showObj("rowmessaggioUrlsitoAdoperato",false);

			}
	    });
	    
	    
	    function urlAdoperata(urlsito,id) {
                $.ajax({
                    type: "GET",
                    dataType: "json",
                    async: false,
                    beforeSend: function(x) {
        			if(x && x.overrideMimeType) {
            			x.overrideMimeType("application/json;charset=UTF-8");
				       }
    				},
                    url: "${pageContext.request.contextPath}/pg/UrlsitoAdoperata.do",
                    data: "urlsito=" + urlsito  + "&id=" + id,
                    success: function(data){
                    	if (data.urlAdoperata == true) {
                   			$("#messaggioUrlsitoAdoperato").show();
                   			showObj("rowmessaggioUrlsitoAdoperato",true);
                        } else {
                        	$("#messaggioUrlsitoAdoperato").hide();
                        	showObj("rowmessaggioUrlsitoAdoperato",false);
                        }
                    },
                    error: function(e){
                        alert("Codice articolo: errore durante il controllo sull'urlsito");
                    }
                });
            }
	    
	    showObj("rowmessaggioUrlsitoAdoperato",false);
	</gene:javaScript>
</gene:template>
</div>

	