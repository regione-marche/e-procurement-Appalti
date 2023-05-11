<%
/*
 * Created on: 26/11/2008
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:choose>
<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda addTr="false">
			<tr id="rowGARCPV_CODCPV_${param.contatore}">
		</gene:campoScheda>
		<gene:campoScheda addTr="false" campo="NGARA_${param.contatore}" entita="GARCPV" campoFittizio="true" visibile="false" definizione="T20;1;;;G1NGARACPV" value="${item[0]}"/>
		<gene:campoScheda addTr="false" campo="NUMCPV_${param.contatore}" entita="GARCPV" campoFittizio="true" visibile="false" definizione="N3;1;;;G1NUMCPV" value="${item[1]}"/>
		<c:choose>
			<c:when test="${param.datiModificabili }">
				<gene:campoScheda  title="Codice CPV complementare" addTr="false" campo="CODCPV_${param.contatore}" entita="GARCPV" href="javascript:formCPV(${requestScope.modCODCPV}, 'GARCPV_CODCPV_${param.contatore}')" campoFittizio="true" definizione="T20;;;;G1CODCPV" value="${item[2]}" speciale="true">
					<gene:popupCampo titolo="Dettaglio codice CPV" href="formCPV(${requestScope.modCODCPV}, 'GARCPV_CODCPV_${param.contatore}')" />
				</gene:campoScheda>
				</c:when>
				<c:otherwise>
					<gene:campoScheda  title="Codice CPV complementare" addTr="false" campo="CODCPV_${param.contatore}" entita="GARCPV" campoFittizio="true" definizione="T20;;;;G1CODCPV" value="${item[2]}" modificabile="false" />
				</c:otherwise>
			</c:choose>
		<gene:campoScheda addTr="false" campo="TIPCPV_${param.contatore}" entita="GARCPV" campoFittizio="true" visibile="false" definizione="T2;;;;G1TIPCPV" value="${item[3]}"/>
		<gene:campoScheda addTr="false">
			</tr>
		</gene:campoScheda>
		
</c:when>
<c:otherwise>
		<gene:campoScheda addTr="false">
			<tr id="rowGARCPV_CODCPV_${param.contatore}">
		</gene:campoScheda>
		<gene:campoScheda addTr="false" campo="NGARA_${param.contatore}" entita="GARCPV" campoFittizio="true" visibile="false" definizione="T20;1;;;G1NGARACPV" value="${param.chiave}"/>
		<gene:campoScheda addTr="false" campo="NUMCPV_${param.contatore}" entita="GARCPV" campoFittizio="true" visibile="false" definizione="N3;1;;;G1NUMCPV"/>
		<gene:campoScheda title="Codice CPV complementare" addTr="false" campo="CODCPV_${param.contatore}" entita="GARCPV" href="javascript:formCPV(${requestScope.modCODCPV}, 'GARCPV_CODCPV_${param.contatore}')" campoFittizio="true" definizione="T20;;;;G1CODCPV" speciale="true">
			<gene:popupCampo titolo="Dettaglio codice CPV" href="formCPV(${requestScope.modCODCPV}, 'GARCPV_CODCPV_${param.contatore}')" />
		</gene:campoScheda>
		<gene:campoScheda addTr="false" campo="TIPCPV_${param.contatore}" entita="GARCPV" campoFittizio="true" visibile="false" definizione="T2;;;;G1TIPCPV" value="2"/>
		<gene:campoScheda addTr="false">
			</tr>
		</gene:campoScheda>
</c:otherwise>
</c:choose>
<gene:javaScript>

	
	<c:if test="${modo ne 'VISUALIZZA'}">
		$('#rowGARCPV_CODCPV_${param.contatore}').find("td:last").append($('#rowtitoloCPVCOMP_${param.contatore}'));
		$('#rowtitoloCPVCOMP_${param.contatore}').css("float","right");
		var count = $('#rowLinkAddCPVCOMP').children('td').length;
		if(count < 2){
			$('<td></td>').insertBefore($('#rowLinkAddCPVCOMP .valore-dato')); 
			}
		$('#rowLinkAddCPVCOMP .valore-dato').attr('colspan',1);
	</c:if>		
	
	$(window).ready(function (){
		
		_creaLinkAlberoCpvVP($("#GARCPV_CODCPV_${param.contatore}").parent(), "${modo}", $("#GARCPV_CODCPV_${param.contatore}"), $("#GARCPV_CODCPV_${param.contatore}view") );
		
		$("#GARCPV_CODCPV_${param.contatore}").change(function(){
			$("#GARCPV_MOD_CPVCOMP_${param.contatore}").val("1");
		});
		
	});

	
</gene:javaScript>