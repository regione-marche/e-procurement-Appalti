<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="modo" value="MODIFICA" scope="request" />

	<gene:setString name="titoloMaschera" value='Pubblicazione massiva sul sito istituzionale' />
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePubblicaSuPortalePAT">
			
	<gene:campoScheda nome="msgPagina">
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${empty requestScope.pubblicazioneEseguita }' >
					Confermi la pubblicazione massiva sul sito istituzionale dei bandi/esiti pubblicati?
				</c:when>
				<c:otherwise>
					Elaborazione conclusa.<br>
					<c:if test="${ not empty requestScope.messaggi}">
					Di seguito sono riportati gli errori riscontrati
					</c:if>
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
	
	<c:choose>
		<c:when test='${not empty requestScope.pubblicazioneEseguita and requestScope.pubblicazioneEseguita eq "1"}'>
			<gene:campoScheda visibile="${ not empty requestScope.messaggi}">
				<td colSpan="2">
					<textarea cols="95" rows="14" readonly="readonly">${requestScope.messaggi}</textarea>
				</td>
			</gene:campoScheda>
		</c:when>
		<c:otherwise>
		</c:otherwise>
	</c:choose>
	
	</gene:formScheda>
  </gene:redefineInsert>

	
	<gene:redefineInsert name="buttons">
			<c:choose>
				<c:when test='${not empty requestScope.pubblicazioneEseguita and requestScope.pubblicazioneEseguita eq "1"}'>
					<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:window.close();">&nbsp;
				</c:when>
				<c:otherwise>
					<INPUT type="button" id="pulsanteConferma" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">&nbsp;
					<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:window.close();">&nbsp;
				</c:otherwise>
			</c:choose>
	</gene:redefineInsert>

	<gene:javaScript>
		function conferma(){
			document.forms[0].jspPathTo.value="gare/commons/popup-pubblicazioneMassivaPAT.jsp";
			schedaConferma();
		}
	</gene:javaScript>
</gene:template>
</div>
