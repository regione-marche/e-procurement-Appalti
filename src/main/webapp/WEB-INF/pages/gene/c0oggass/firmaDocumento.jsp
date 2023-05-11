<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:if test='${firmaDocumento eq "1"}'>
	<a style="float:right;" href="javascript:apriModaleRichiestaFirma('${sessionScope.moduloAttivo}','${datiRiga.W_DOCDIG_IDDOCDIG}','');">
	<img src="${pageContext.request.contextPath}/img/firmaRemota.png" title="Firma digitale del documento" alt="Firma documento" width="16" height="16">
	<span title="Firma digitale del documento">Firma documento</span></a>
	
</c:if>

