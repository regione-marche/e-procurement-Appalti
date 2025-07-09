<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test='${not empty requestScope.RISULTATO and requestScope.RISULTATO eq "OK"}' >
		<script type="text/javascript">
				opener.historyReload();
				window.close();
		</script>
	</c:when>
<c:otherwise>

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.dittao}'>
		<c:set var="dittao" value="${param.dittao}" />
	</c:when>
	<c:otherwise>
		<c:set var="dittao" value="${dittao}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.isarchi and empty isarchi}'>
		<c:set var="isarchi" value="${param.isarchi}" />
	</c:when>
	<c:otherwise>
		<c:set var="isarchi" value="${isarchi}" />
	</c:otherwise>
</c:choose>

<gene:template file="popup-template.jsp" gestisciProtezioni="false" >
	<gene:setString name="titoloMaschera" value="Archivia documenti" />

	<c:set var="modo" value="MODIFICA" scope="request" />

	<gene:redefineInsert name="corpo">
	<br>
	<c:choose>
		<c:when test='${RISULTATO eq "ERRORI"}'>
			Si sono presentati degli errori durante l'operazione di archiviazione
		</c:when>
		<c:when test='${isarchi eq "2"}'>
			Selezionare i documenti che si intendono archiviare.<br>
			Si sottolinea che i documenti, una volta archiviati, non possono essere più ripristinati.<br>
		</c:when>
		<c:otherwise>
			Nella lista sotto sono riportati i documenti archiviati.<br>
			E' possibile procedere all'archiviazione di altri documenti premendo il pulsante 'Archivia documenti'.<br>
		</c:otherwise>
	</c:choose>

	<c:set var="numeroDocumentiDaArchiviare" value="0"/>

		<table class="lista">
			<c:choose>
				<c:when test="${not empty dittao}">
					<c:set var="where" value="dittao=?" />
					<c:set var="parametri" value="T:${dittao};T:${ngara}"/>
				</c:when>
				<c:otherwise>
					<c:set var="where" value="dittao is null" />
					<c:set var="parametri" value="T:${ngara}"/>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${isarchi eq '2'}">
					<c:set var="where" value="${where} and (isarchi is null or isarchi != '1')"/>
				</c:when>
				<c:otherwise>
					<c:set var="where" value="${where} and isarchi = '1'"/>
				</c:otherwise>
			</c:choose>
			<c:set var="where" value="${where} and ngara=? and statodoc = 5"/>
			<tr>
				<td ${stileDati}>
				${gene:callFunction4("it.eldasoft.gene.tags.functions.ImpostazioneFiltroFunction", pageContext, "G1DOCUMATTI", where, parametri)}
  				<gene:formLista entita="G1DOCUMATTI"  pagesize="20" tableclass="datilista" sortColumn="2" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.npa.GestorePopupArchiviaG1DOCUMATTI">
  					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />

					<gene:set name="titoloMenu">
						<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
					</gene:set>
					<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50" visibile="${isarchi ne 1}" >
						<c:if test="${currentRow >= 0 and isarchi eq '2'}">
							<input type="checkbox" name="keys" value="${datiRiga.G1DOCUMATTI_ID}"  />
							<c:set var="numeroDocumentiDaArchiviare" value="${numeroDocumentiDaArchiviare + 1}"/>
						</c:if>
					</gene:campoLista>

					<gene:campoLista campo="ID"  visibile="false" />
					<gene:campoLista campo="NGARA"  visibile="false" />
					<gene:campoLista campo="IDPRG"  visibile="false" />
					<gene:campoLista campo="IDDOCDIG"  visibile="false" />
					<gene:campoLista campo="DIGNOMDOC"  entita="W_DOCDIG" where="G1DOCUMATTI.idprg=w_docdig.idprg and G1DOCUMATTI.iddocdig=w_docdig.iddocdig"
						href="javascript:visualizzaFileAllegato('${datiRiga.G1DOCUMATTI_IDPRG}','${datiRiga.G1DOCUMATTI_IDDOCDIG}',${gene:string4Js(datiRiga.W_DOCDIG_DIGNOMDOC)});" />
					<gene:campoLista campo="DIGDESDOC"  entita="W_DOCDIG" />

					<input type="hidden" name="numeroDocumenti" id="numeroDocumenti" value="" />
					<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
					<input type="hidden" name="dittao" id="dittao" value="${dittao}" />
					<input type="hidden" name="isarchi" id="isarchi" value="${isarchi }" />
				</gene:formLista>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<c:choose>
						<c:when test="${isarchi eq '2'}">
							<INPUT type="button"  class="bottone-azione" value='Conferma' title='Conferma' onclick="javascript:listaConferma();">
							<INPUT type="button"  class="bottone-azione" value='Annulla' title='Annulla' onclick="javascript:annulla();">
							&nbsp;
						</c:when>
						<c:otherwise>
							<INPUT type="button"  class="bottone-azione" value='Archivia' title='Archivia' onclick="javascript:archivia();">
							<INPUT type="button"  class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:chiudi();">
							&nbsp;
						</c:otherwise>
					</c:choose>

				</td>
			</tr>
		</table>
  </gene:redefineInsert>
	<gene:javaScript>

	function chiudi(){
		window.close();
	}

	function archivia(){
		listaConferma();
	}

	function annulla(){
		document.getElementById("isarchi").value = 1;
		listaAnnullaModifica();
	}

	function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
		var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
		document.location.href=href+"?idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
	}

	</gene:javaScript>
</gene:template>
</c:otherwise>
</c:choose>