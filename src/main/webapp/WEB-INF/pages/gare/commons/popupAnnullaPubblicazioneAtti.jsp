<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${!empty param.codgar}'>
		<c:set var="codgar" value="${param.codgar}" />
	</c:when>
	<c:otherwise>
		<c:set var="codgar" value="${codgar}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:set var="bustalotti" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetBustalottiFunction", pageContext, "TORN.CODGAR=T:".concat(codgar))}' />

<c:choose>
	<c:when test="${bustalotti eq '1'}">
		<c:set var="key1" value="${codgar }"/>
	</c:when>
	<c:otherwise>
		<c:set var="key1" value="${ngara }"/>
	</c:otherwise>
</c:choose>
<c:set var="key" value='GARE.NGARA=T:${key1}' scope="request" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<gene:template file="popup-message-template.jsp">
<gene:setString name="titoloMaschera" value='Annulla pubblicazione atti e offerte' />

<c:choose>
	<c:when test='${requestScope.annullamentoPubblicazione eq "1"}' >

		<gene:redefineInsert name="corpo">
		<br>
		&nbsp;&nbsp;&nbsp;La pubblicazione su portale Appalti è stata annullata.
		<br>&nbsp;
		<br>&nbsp;
			<script>
				window.opener.historyReload();
				window.close();
			</script>
		
		<gene:redefineInsert name="buttons">
			<input type="button" class="bottone-azione"  id="cancel" value="Chiudi" title="Chiudi" onclick="chiudi();"/>&nbsp;&nbsp;
		</gene:redefineInsert>
		<gene:javaScript>

		function chiudi(){
			window.opener.historyReload();
			window.close();
		}
		</gene:javaScript>
		</gene:redefineInsert>
	</c:when>
	<c:otherwise>

	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${contextPath}/js/jHtmlArea-0.7.5.min.js"></script> 
		<link rel="Stylesheet" type="text/css" href="${contextPath}/css/jquery/jHtmlArea/jHtmlArea.css" />
		<link rel="stylesheet" type="text/css" href="${contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
	</gene:redefineInsert>

	<c:set var="modo" value="MODIFICA" scope="request" />

	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePubblicaAttiSuPortale">

	<gene:campoScheda nome="msgPagina">
		<td colSpan="2">
			<br>
			Mediante questa funzione si procede ad annullare la pubblicazione degli atti e offerte della gara sul portale Appalti.
			<br><br>Confermi l’operazione?
			<br><br>
		</td>
	</gene:campoScheda>

		<gene:campoScheda campo="CODGAR1" visibile="false" />
		<gene:campoScheda campo="NGARA" campoFittizio="true" value="${ngara}" visibile="false" definizione="T20;0"/>

		<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />

	</gene:formScheda>
  </gene:redefineInsert>

	<gene:redefineInsert name="buttons">
		<INPUT type="button" id="pulsanteConferma" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">&nbsp;
		<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
	</gene:redefineInsert>

	<gene:javaScript>
	function conferma() {
		document.forms[0].jspPathTo.value="gare/commons/popupAnnullaPubblicazioneAtti.jsp";
		schedaConferma();
	}
	function annulla(){
		window.close();
	}
	</gene:javaScript>
	</c:otherwise>
</c:choose>
</gene:template>
