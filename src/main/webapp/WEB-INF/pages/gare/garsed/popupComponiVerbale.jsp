<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<div style="display: none;">
<gene:template file="popup-message-template.jsp">
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="urlPubblica" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "portaleAppalti.urlPubblica")}'/>
<c:set var="idUtente" value="${ profiloUtente.id }" />
<c:set var="nomeFileModello" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "verbaleGara.nomeModello")}'/>
<c:set var="selectModello" value="select id_modello from W_MODELLI where nomefile=?" />
<c:set var="idModello" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, selectModello, nomeFileModello)}'/>
<c:set var="selectModello" value="select nome from W_MODELLI where nomefile=?" />
<c:set var="nomeModello" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, selectModello, nomeFileModello)}'/>
<c:set var="contatore" value="1" scope="page"/>

<gene:redefineInsert name="corpo">
	<form name="componiVerbaleForm" id="componiVerbaleForm" action="pg/SalvaParametriVerbale.do" method="post">

		<input type="hidden" name="parametriModello" value="${ urlPubblica }"/>

		<input type="hidden" name="metodo" value="salvaEComponiModello"/>
		<input type="hidden" name="idModello" value="${ idModello }">
		<input type="hidden" name="tipo" value="1">
		<input type="hidden" name="nomeModello" value="${ nomeModello }">
		<input type="hidden" name="entita" value="GARSED">
		<input type="hidden" name="nomeChiavi" value="NGARA;NUMSED">
		<%-- <input type="hidden" name="valChiavi" value="G02515;1"/> --%>
		<input type="hidden" name="noFiltroEntitaPrincipale" value="0">
		<input type="hidden" name="paginaSorgente" value="scheda">
		<input type="hidden" name="riepilogativo" value="0">
	</form>
</gene:redefineInsert>
<gene:redefineInsert name="buttons"/>
<gene:javaScript>

$(document).ready(function() {
	assegnaParametri();
	document.componiVerbaleForm.submit();
});

const elencoParametriNomi = new Array ( 'stampaTutto','elencoPlichiD','elencoDocumentiD','elencoAmmissioniD','elencoInvitate','soloElencoPlichiO','ditteEstratte',
	'elencoDocumentiO','elencoAmmissioniO','elencoDocumentiT','valutazioniTecniche','elencoDocumentiE','offertePunteggi','graduatoria'
);

function aggiungiCampoNascosto(nome,valore){
	var newField = document.createElement('input');
	newField.setAttribute('type','hidden');
	newField.setAttribute('name',nome);
	newField.setAttribute('value',valore);
	componiVerbaleForm.appendChild(newField);
}

function assegnaParametri(){

	const queryString = window.location.search;
	const urlParams = new URLSearchParams(queryString);

	elencoParametriNomi.forEach((nomeCampo) => {
		var valoreDaImpostare = urlParams.get(nomeCampo);
		if (valoreDaImpostare == null || valoreDaImpostare == '') {
			valoreDaImpostare = '2';
		}
    	aggiungiCampoNascosto('parametriModello',valoreDaImpostare);
	});

	var numeroGara = urlParams.get('numeroGara');
	var numeroSeduta = urlParams.get('numeroSeduta');
	aggiungiCampoNascosto('valChiavi',numeroGara + ';' + numeroSeduta);
}

</gene:javaScript>
</gene:template>
</div>