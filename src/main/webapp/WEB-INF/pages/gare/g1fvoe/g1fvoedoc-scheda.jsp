<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="G1FVOE-DOC">

	<gene:redefineInsert name="addHistory">
		<gene:historyAdd titolo='Documenti FVOE della ditta' id="listaDocFVOE" />
	</gene:redefineInsert>

	<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
	<c:set var="codiceGara" value="${param.codiceGara}" scope="request"/>
	<c:set var="codiceDitta" value="${param.codiceDitta}" scope="request"/>
	<c:set var="autorizzatoModifiche" value="${param.autorizzatoModifiche}" scope="request"/>
	<c:set var="accessoAutorizzato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.CheckAccessoAutorizzatoFVOEFunction",  pageContext, codiceGara, codiceDitta)}' scope="request"/>
	<c:set var="nomimp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetNOMIMPFunction", pageContext,codiceDitta)}' scope="request"/>
	<c:set var="activePage" value="${param.activePage}"/>

	<gene:redefineInsert name="corpo">
		<gene:formPagine gestisciProtezioni="true">
			<gene:pagina title="Documenti disponibili" idProtezioni="DOCDISP" >
				<jsp:include page="g1fvoedoc-lista.jsp"/>
			</gene:pagina>
			<gene:pagina title="Richieste documenti" idProtezioni="DOCRICH" >
				<jsp:include page="g1fvoedoc-lista.jsp"/>
			</gene:pagina>
		</gene:formPagine>
		<gene:javaScript>
			function selezionaPagina(activePage){
				document.formVisualizzaFVOEDOC.activePage.value=activePage;
				document.formVisualizzaFVOEDOC.submit();
			}
		</gene:javaScript>
	<form name="formVisualizzaFVOEDOC" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="gare/g1fvoe/g1fvoedoc-scheda.jsp" />
		<input type="hidden" name="codiceGara" value="${codiceGara}" />
		<input type="hidden" name="codiceDitta" value="${codiceDitta}" />
		<input type="hidden" name="activePage" value=""/>
		<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche }" />
	</form>
	</gene:redefineInsert>
</gene:template>
