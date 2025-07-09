<%
/*
 * Created on: 07/03/2019
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="ID_${param.contatore}" entita="GARECUP" campoFittizio="true" visibile="false" definizione="N12;1" value="${item[0]}" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="GARECUP" campoFittizio="true" visibile="false" definizione="T20;0" value="${item[1]}" />
		<gene:campoScheda campo="CUP_${param.contatore}" entita="GARECUP"  campoFittizio="true" definizione="T15;0;;;G1CUPCUP"  value="${item[2]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="ID_${param.contatore}" entita="GARECUP" campoFittizio="true" visibile="false" definizione="N12;1"  />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="GARECUP" campoFittizio="true" visibile="false" definizione="T20;0"  value="${param.chiave }"/>
		<gene:campoScheda campo="CUP_${param.contatore}" entita="GARECUP"  campoFittizio="true" definizione="T15;0;;;G1CUPCUP" />
	</c:otherwise>
</c:choose>


<gene:javaScript>
	<c:if test='${modo eq "MODIFICA" }'>
		$("#GARECUP_CUP_${param.contatore}").on('change', function() {
		 	validazioneCup(this);
		});
	</c:if>
	
	<c:if test="${modo ne 'VISUALIZZA'}">
		$('#rowGARECUP_CUP_${param.contatore}').find("td:last").append($('#rowtitoloGARECUP_${param.contatore}'));
		$('#rowtitoloGARECUP_${param.contatore}').css("float","right");
		var count = $('#rowLinkAddGARECUP').children('td').length;
		if(count < 2){
			$('<td></td>').insertBefore($('#rowLinkAddGARECUP .valore-dato')); 
			}
		$('#rowLinkAddGARECUP .valore-dato').attr('colspan',1);
	</c:if>		
</gene:javaScript>