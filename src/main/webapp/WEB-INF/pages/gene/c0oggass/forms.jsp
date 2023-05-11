<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<jsp:include page="/WEB-INF/pages/gare/commons/modalPopupFirmaDocumento.jsp">
		<jsp:param name="oggettoDoc" value="${titolo}"/>
		<jsp:param name="key1" value="${param.key1}"/>
	</jsp:include>