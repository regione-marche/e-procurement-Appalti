<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="ID_${param.contatore}" entita="G1DFINA" campoFittizio="true" visibile="false" definizione="N12;1" value="${item[0]}" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="G1DFINA" campoFittizio="true" visibile="false" definizione="T20;0" value="${item[1]}" />
		<gene:campoScheda campo="TIFINA_${param.contatore}" entita="G1DFINA" campoFittizio="true" definizione="T5;0;A1z25;;G1TIFINA" value="${item[2]}" />
		<gene:campoScheda campo="IMPOFI_${param.contatore}" entita="G1DFINA"  campoFittizio="true" definizione="F15;0;;MONEY;G1IMPOFI" value="${item[3]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="ID_${param.contatore}" entita="G1DFINA" campoFittizio="true" visibile="false" definizione="N12;1" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="G1DFINA" campoFittizio="true" visibile="false" definizione="T20;0" value="${param.chiave }" />
		<gene:campoScheda campo="TIFINA_${param.contatore}" entita="G1DFINA" campoFittizio="true" definizione="T5;0;A1z25;;G1TIFINA" />
		<gene:campoScheda campo="IMPOFI_${param.contatore}" entita="G1DFINA" campoFittizio="true" definizione="F15;0;;MONEY;G1IMPOFI"/>
	</c:otherwise>
</c:choose>
