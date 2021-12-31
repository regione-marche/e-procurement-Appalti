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
	<tr id="rowG1CRIREG_VALMIN_${param.contatore}">
</gene:campoScheda>
	<gene:campoScheda addTr="false" campo="PUNTUALE_${param.contatore}" campoFittizio="true" entita="G1CRIREG"  definizione="T200;0;;;G1PUNTUAR" value="${item[0]}"/>
	<gene:campoScheda addTr="false" campo="VALMIN_${param.contatore}" campoFittizio="true" entita="G1CRIREG" definizione="F12.5;0;;;G1VALMINR" value="${item[1]}">
		<gene:checkCampoScheda funzione='checkValminValmax("##","#G1CRIREG_VALMAX_${param.contatore}#","#G1CRIREG_FORMULA#")' messaggio="Il limite inferiore dell'intervallo di valori ammessi deve essere minore del limite superiore" obbligatorio="true" onsubmit="false"/>
	</gene:campoScheda>
	<gene:campoScheda addTr="false" campo="VALMAX_${param.contatore}" campoFittizio="true" entita="G1CRIREG"  definizione="F12.5;0;;;G1VALMAXR" value="${item[2]}">
		<gene:checkCampoScheda funzione='checkValminValmax("#G1CRIREG_VALMIN_${param.contatore}#","##","#G1CRIREG_FORMULA#")' messaggio="Il limite superiore dell'intervallo di valori ammessi deve essere maggiore del limite inferiore" obbligatorio="true" onsubmit="false"/>
	</gene:campoScheda>
	<gene:campoScheda addTr="false" campo="COEFFI_${param.contatore}" campoFittizio="true" entita="G1CRIREG"  definizione="F12.9;0;;;G1COEFFIR" value="${item[3]}" obbligatorio="true">
		<gene:checkCampoScheda funzione='"##" <= 1 && "##" >= 0' messaggio="Il coefficente deve essere compreso tra 0 e 1" obbligatorio="true" onsubmit="false"/>
	</gene:campoScheda>
	<gene:campoScheda addTr="false" campo="IDCRIDEF_${param.contatore}" visibile="false" campoFittizio="true"  definizione="N12;0;;;G1IDCDEF1" value="${item[4]}"/>
	<gene:campoScheda addTr="false" campo="ID_${param.contatore}" campoFittizio="true" visibile="false" entita="G1CRIREG"  definizione="N12;1;;;G1IDCRIREG" value="${item[5]}"/>
	<c:set var="righeVisualizzate" value='${righeVisualizzate + 1}'/>
	<c:set var="countdownrighe" value='${countdownrighe + 1}'/>
<gene:campoScheda addTr="false">
<td id="elimina_VALMIN_${param.contatore}"></td>
	</tr>
</gene:campoScheda>
	</c:when>
	
	<c:otherwise>
	<gene:campoScheda addTr="false">
	<tr id="rowG1CRIREG_VALMIN_${param.contatore}">
	</gene:campoScheda>
	<gene:campoScheda addTr="false" campo="PUNTUALE_${param.contatore}" campoFittizio="true" entita="G1CRIREG"  definizione="T200;0;;;G1PUNTUAR"/>
	<gene:campoScheda addTr="false" campo="VALMIN_${param.contatore}" campoFittizio="true" entita="G1CRIREG"  definizione="F12.5;0;;;G1VALMINR">
		<gene:checkCampoScheda funzione='checkValminValmax("##","#G1CRIREG_VALMAX_${param.contatore}#","#G1CRIREG_FORMULA#")' messaggio="Il limite inferiore dell'intervallo di valori ammessi deve essere minore del limite superiore" obbligatorio="true" onsubmit="false"/>
	</gene:campoScheda>
	<gene:campoScheda addTr="false" campo="VALMAX_${param.contatore}" campoFittizio="true" entita="G1CRIREG" definizione="F12.5;0;;;G1VALMAXR">
		<gene:checkCampoScheda funzione='checkValminValmax("#G1CRIREG_VALMIN_${param.contatore}#","##","#G1CRIREG_FORMULA#")' messaggio="Il limite superiore dell'intervallo di valori ammessi deve essere maggiore del limite inferiore" obbligatorio="true" onsubmit="false"/>
	</gene:campoScheda>
	<gene:campoScheda addTr="false" campo="COEFFI_${param.contatore}" campoFittizio="true" entita="G1CRIREG"  definizione="F12.9;0;;;G1COEFFIR" obbligatorio="true">
		<gene:checkCampoScheda funzione='"##" <= 1 && "##" >= 0' messaggio="Il coefficente deve essere compreso tra 0 e 1" obbligatorio="true" onsubmit="false"/>
	</gene:campoScheda>
	<gene:campoScheda addTr="false" campo="IDCRIDEF_${param.contatore}" visibile="false" campoFittizio="true" entita="G1CRIREG" definizione="N12;0;;;G1IDCDEF1" value="${param.chiave }"/>
	<gene:campoScheda addTr="false" campo="ID_${param.contatore}" campoFittizio="true" visibile="false" entita="G1CRIREG"  definizione="N12;1;;;G1IDCRIREG" />
<gene:campoScheda addTr="false">
<td id="elimina_VALMIN_${param.contatore}"></td>
	</tr>
</gene:campoScheda>
	</c:otherwise>
</c:choose>
<gene:javaScript>
		$('#elimina_VALMIN_${param.contatore}').append($('#rowtitoloG1CRIREG_${param.contatore}'));
		var count = $('#rowLinkAddG1CRIREG').children('td').length;
		if(count < 2){
			$('<td></td>').insertBefore($('#rowLinkAddG1CRIREG .valore-dato')); 
			}
		$('#rowLinkAddG1CRIREG .valore-dato').attr('colspan',1);
		$('#rowG1CRIREG_VALMIN_${param.contatore}').find("td:nth-child(2)").find("input").css('max-width','90%');
		
		function checkValminValmax(valmin,valmax,formula){
			valmin = parseFloat(valmin);
			valmax = parseFloat(valmax);
			if(formula == 2){
				if(valmin >= valmax){
					return false;
				}
			}else{
				if(valmin && valmax && valmin >= valmax){
					return false;
				}
			}
			return true;
		}
		

</gene:javaScript>