<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test="${!empty param.ngara}">
		<c:set var="ngara" value="${param.ngara}"/>
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.codiceGara}">
		<c:set var="codiceGara" value="${param.codiceGara}"/>
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value="${codiceGara}"/>
	</c:otherwise>
</c:choose>

<gene:template file="scheda-template.jsp" gestisciProtezioni="false" >
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="head" >
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easytabs.js"></script>
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Aggiungi scheda ANAC" />
	<c:set var="tmp" value='${gene:callFunctionN("it.eldasoft.sil.pg.tags.funzioni.npa.GetAnacFormAggiungibiliFunction", pageContext, codiceGara, ngara)}' />
	
	<gene:redefineInsert name="addToAzioni" >
		<c:if test="${not empty listaFormulariAggiungibili}">
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:aggiungiForm();" title="Conferma">
						Conferma
					</a>
				</td>
			</tr>
		</c:if>
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:historyBack();" title="Annulla" tabindex="1501">
					Annulla
				</a>
			</td>
		</tr>
	</gene:redefineInsert>
	
	<gene:redefineInsert name="corpo">
		<p>Selezionare la scheda che si vuole produrre:</p>

		<c:if test="${empty listaFormulariAggiungibili}">
			<p><b>Nessuna voce disponibile.</b><br><br>
				${messaggioNonAggiungibili}
			</p>
		</c:if>

		<form name="formAggiungiAnacForm"  action="${contextPath}/Lista.do" method="post">
		<table class="dettaglio-notab" id="tabellaAggiungiAnacForm">

		<tr>
			<td colspan="2"> 
				<div style="display:block;">
				<br>
					<c:forEach items="${listaFormulariAggiungibili}" var="formulario">
					&nbsp;<input type="radio" name="formularioScelto" value="${formulario[0]}" />&nbsp;<b>${formulario[1]}</b><br><br>
					<div style="margin-left: 35px;"><i>${formulario[2]}</i></div>
					<br><br>
					</c:forEach>
				<br>
				</div>
			</td>
		</tr>
		
		<tr>
			<td class="comandi-dettaglio" colspan="2">
				<c:if test="${not empty listaFormulariAggiungibili}">
					<INPUT type="button" id="aggiungi" class="bottone-azione" value="Conferma" title="Conferma"  onclick="javascript:aggiungiForm();"/>
				</c:if>
				<INPUT type="button" id="annulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:historyBack();"/>
				&nbsp;
			</td>
		</tr>
		</table>

		<input type="hidden" name="codiceGara" value="${codiceGara}" />
		<input type="hidden" name="activePage" value="0" /> 
		<input type="hidden" name="isPopUp" value="0" /> 
		<input type="hidden" name="numeroPopUp" value="0" /> 
		<input type="hidden" name="metodo" value="nuovo" /> 
		<input type="hidden" name="entita" value="G1NPAFORM" /> 
		<input type="hidden" name="gestisciProtezioni" value="1" />

		</form>

		<gene:redefineInsert name="documentiAssociati"></gene:redefineInsert> 
		<gene:redefineInsert name="noteAvvisi"></gene:redefineInsert>
		<gene:redefineInsert name="helpPagina" ></gene:redefineInsert>

	</gene:redefineInsert>
<gene:javaScript>

$(document).ready(function() {
	$("#alinkIndietro").parent().remove();
});

function aggiungiForm(){
	if ($('input:radio[name="formularioScelto"]').filter(':checked').val() == null) {
		alert("Selezionare una voce nell'elenco");
		return;
	}
	bloccaRichiesteServer();
	document.forms[0].submit();
}

</gene:javaScript>	
</gene:template>