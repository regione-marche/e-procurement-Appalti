<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="entita" value="torn" />
<c:if test="${fn:startsWith(param.id, '$')}">
	<c:set var="entita" value="gare" />
</c:if>

<html>
  <head>
    <meta HTTP-EQUIV="REFRESH" content="0; url=<%=request.getContextPath()%>/ApriPagina.do?href=gare/${entita}/${entita}-scheda.jsp&key=V_GARE_TORN.CODGAR=T:${param.id}"></meta>
  </head>
</html>
