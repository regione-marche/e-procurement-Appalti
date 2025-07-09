<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.annullaCalcolo}'>
		<c:set var="annullaCalcolo" value="${param.annullaCalcolo}" />
	</c:when>
	<c:otherwise>
		<c:set var="annullaCalcolo" value="${annullaCalcolo}" />
	</c:otherwise>
</c:choose>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:template file="popup-message-template.jsp">

	<c:choose>
		<c:when test="${not empty annullaCalcolo}">
			<c:set var="modo" value="MODIFICA" scope="request" />
			<gene:setString name="titoloMaschera" value='Annulla calcolo punteggi' />
		</c:when>
		<c:otherwise>
			<c:set var="modo" value="NUOVO" scope="request" />
			<gene:setString name="titoloMaschera" value='Conferma dati e calcolo punteggi' />
		</c:otherwise>
	</c:choose>



<c:choose>
	<c:when test='${requestScope.calcoloEseguito eq "1"}' >

		<gene:redefineInsert name="corpo">
		<br>
		&nbsp;&nbsp;&nbsp;Calcolo punteggi completato.
		<br>&nbsp;
		<br>&nbsp;
		
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
	<c:when test='${requestScope.annullaCalcoloEseguito eq "1"}' >

		<gene:redefineInsert name="corpo">
			<br>
			&nbsp;&nbsp;&nbsp;Annulla calcolo punteggi completato.
			<br>&nbsp;
			<br>&nbsp;

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

	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GARE" gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreELPREMPUN" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreELPREMPUN">

	<gene:campoScheda nome="msgPagina">
		<td colSpan="2">
			<c:choose>
				<c:when test="${not empty annullaCalcolo}">
					<br>
					Mediante questa funzione si procede ad annullare il calcolo punteggi delle ditte.
					<br><br>Confermi l’operazione?
					<br><br>
				</c:when>
				<c:when test='${requestScope.controlloSuperato eq "NO"}' >
					<br>
						<b>Non è possibile procedere alla conferma dei dati inseriti e al calcolo punteggi delle ditte.</b>
					<br>
					${requestScope.msg }
					<br><br>
				</c:when>
				<c:otherwise>
					<br>
					Mediante questa funzione si procede alla conferma dei dati inseriti e al calcolo punteggi delle ditte.
					<br><br>Confermi l’operazione?
					<c:if test='${requestScope.controlloSuperato eq "WARNING"}' >
						<span style="color:blue"><b><br><br>ATTENZIONE:</b> ${requestScope.msg}</span>
					</c:if>
					<br><br>
				</c:otherwise>
			</c:choose>
		</td>
	</gene:campoScheda>

		<gene:campoScheda campo="NGARA" campoFittizio="true" value="${ngara}" visibile="false" definizione="T20;0"/>

		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		<input type="hidden" name="annullaCalcolo" id="annullaCalcolo" value="${annullaCalcolo}" />
	</gene:formScheda>
  </gene:redefineInsert>

	<gene:redefineInsert name="buttons">
		<c:choose>
			<c:when test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO"}'>
				<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
			</c:when>
			<c:otherwise>
				<INPUT type="button" id="pulsanteConferma" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma()">&nbsp;
				<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
			</c:otherwise>
		</c:choose>
	</gene:redefineInsert>

	<gene:javaScript>
	function conferma() {
		document.forms[0].jspPathTo.value="gare/elprempun/popupCalcoloELPREMPUN.jsp";
		schedaConferma();
	}
	function annulla(){
		window.close();
	}


	</gene:javaScript>
	</c:otherwise>
</c:choose>
</gene:template>
