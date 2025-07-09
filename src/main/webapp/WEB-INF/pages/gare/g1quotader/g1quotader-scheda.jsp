<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="ID_${param.contatore}" entita="G1QUOTADER" campoFittizio="true" visibile="false" definizione="N12;1" value="${item[0]}" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="G1QUOTADER" campoFittizio="true" visibile="false" definizione="T20;0" value="${item[1]}" />
		<gene:campoScheda campo="MOTDEROGA_${param.contatore}" entita="G1QUOTADER"  campoFittizio="true" definizione="N7;0;A1257;;G1MOTDEROGA"  value="${item[2]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="ID_${param.contatore}" entita="G1QUOTADER" campoFittizio="true" visibile="false" definizione="N12;1" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="G1QUOTADER" campoFittizio="true" visibile="false" definizione="T20;0" value="${param.chiave }" />
		<gene:campoScheda campo="MOTDEROGA_${param.contatore}" entita="G1QUOTADER"  campoFittizio="true" definizione="N7;0;A1257;;G1MOTDEROGA" />
	</c:otherwise>
</c:choose>

<gene:javaScript>
	<c:if test="${modo ne 'VISUALIZZA'}">
		var count = $('#rowLinkAddG1QUOTADER').children('td').length;
		if(count < 2){
			$('<td></td>').insertBefore($('#rowLinkAddG1QUOTADER .valore-dato')); 
			}
		$('#rowLinkAddG1QUOTADER .valore-dato').attr('colspan',1);
		$('#rowG1QUOTADER_MOTDEROGA_${param.contatore}').find("td:last").append($('#rowtitoloG1QUOTADER_${param.contatore}'));
		$('#rowtitoloG1QUOTADER_${param.contatore}').css("float","right");
	</c:if>
</gene:javaScript>