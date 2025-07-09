<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="ID_${param.contatore}" entita="G1MISPREMIALI" campoFittizio="true" visibile="false" definizione="N12;1" value="${item[0]}" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="G1MISPREMIALI" campoFittizio="true" visibile="false" definizione="T20;0" value="${item[1]}" />
		<gene:campoScheda campo="MISURA_${param.contatore}" entita="G1MISPREMIALI"  campoFittizio="true" definizione="N7;0;A1258;;G1MISURA"  value="${item[2]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="ID_${param.contatore}" entita="G1MISPREMIALI" campoFittizio="true" visibile="false" definizione="N12;1" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="G1MISPREMIALI" campoFittizio="true" visibile="false" definizione="T20;0" value="${param.chiave }" />
		<gene:campoScheda campo="MISURA_${param.contatore}" entita="G1MISPREMIALI"  campoFittizio="true" definizione="N7;0;A1258;;G1MISURA" />
	</c:otherwise>
</c:choose>


<gene:javaScript>
	<c:if test="${modo ne 'VISUALIZZA'}">
		var count = $('#rowLinkAddG1MISPREMIALI').children('td').length;
		if(count < 2){
			$('<td></td>').insertBefore($('#rowLinkAddG1MISPREMIALI .valore-dato')); 
			}
		$('#rowLinkAddG1MISPREMIALI .valore-dato').attr('colspan',1);
		$('#rowG1MISPREMIALI_MISURA_${param.contatore}').find("td:last").append($('#rowtitoloG1MISPREMIALI_${param.contatore}'));
		$('#rowtitoloG1MISPREMIALI_${param.contatore}').css("float","right");
	</c:if>
</gene:javaScript>