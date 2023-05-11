<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:if test='${fn:contains(listaOpzioniDisponibili, "OP114#")}' >
<gene:pagina  title="Configurazione portale Appalti" idProtezioni="WSDM_ALT_TAB">

	<table class="dettaglio-tab">
		<c:choose>
			<c:when test="${modalita ne 'modifica'}">
				<jsp:include page="..\wsdmconfipro\dettConfigPortaleWSDM.jsp">
					<jsp:param name="idconfi" value="${idconfi}"/>
					<jsp:param name="descri" value="${descri}"/>
					<jsp:param name="codapp" value="${codapp}"/>
				</jsp:include>
			</c:when>
			<c:otherwise>
				<jsp:include page="..\wsdmconfipro\modConfigPortaleWSDM.jsp">
					<jsp:param name="idconfi" value="${idconfi}"/>
					<jsp:param name="descri" value="${descri}"/>
					<jsp:param name="codapp" value="${codapp}"/>
				</jsp:include>
			</c:otherwise>
		</c:choose>
	</table>
	
</gene:pagina>
</c:if>

