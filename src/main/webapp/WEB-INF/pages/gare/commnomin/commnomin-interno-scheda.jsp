<%
	/*
	 * Created on 29-Mar-2012
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda addTr="false">
			<tr id="rowtitoloCOMMR_${param.contatore}">
		</gene:campoScheda>
		<gene:campoScheda addTr="false" entita="COMMRUOLI" campo="ID_${param.contatore}" campoFittizio="true" visibile="false" definizione="N12;1" value="${item[0]}" modificabile="false" />
		<gene:campoScheda addTr="false" entita="COMMRUOLI" campo="IDALBO_${param.contatore}" campoFittizio="true" visibile="false" definizione="N12;1" value="${item[1]}" modificabile="false" />
		<gene:campoScheda addTr="false" entita="COMMRUOLI" campo="IDNOMIN_${param.contatore}" campoFittizio="true" visibile="false" definizione="N12;1" value="${item[2]}" modificabile="false" />
		<gene:campoScheda addTr="false" hideTitle="true" entita="COMMRUOLI" campo="RUOLO_${param.contatore}" campoFittizio="true" visibile="true" definizione="T20;0;;;" value="${item[3]}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTIPO_RUOLO" obbligatorio="true" title="Ruolo"/>
		<gene:campoScheda addTr="false" entita="COMMRUOLI" campo="INVITI_${param.contatore}" campoFittizio="true" visibile="true" definizione="N7;0;;;" value="${item[4]}" modificabile="false" title="Presenze in commissione"/>
		<gene:campoScheda addTr="false">
			</tr>
		</gene:campoScheda>
	</c:when>
	<c:otherwise>
		<gene:campoScheda addTr="false">
			<tr id="rowtitoloCOMMR_${param.contatore}">
		</gene:campoScheda>
		<gene:campoScheda addTr="false" entita="COMMRUOLI" campo="ID_${param.contatore}" campoFittizio="true" visibile="false" definizione="N12;1" modificabile="false" />
		<gene:campoScheda addTr="false" entita="COMMRUOLI" campo="IDALBO_${param.contatore}" campoFittizio="true" visibile="false" definizione="N12;1" value="${param.idalbo}" modificabile="false" />
		<gene:campoScheda addTr="false" entita="COMMRUOLI" campo="IDNOMIN_${param.contatore}" campoFittizio="true" visibile="false" definizione="N12;1" value="${param.chiave}" modificabile="false" />
		<gene:campoScheda addTr="false" hideTitle="true" entita="COMMRUOLI" campo="RUOLO_${param.contatore}" campoFittizio="true" visibile="true" definizione="T20;0;;;" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTIPO_RUOLO" obbligatorio="true" title="Ruolo"/>
		<gene:campoScheda addTr="false" entita="COMMRUOLI" campo="INVITI_${param.contatore}" campoFittizio="true" visibile="true" definizione="N7;0;;;" modificabile="false" value="${param.presenze}" title="Presenze in commissione"/>
		<gene:campoScheda addTr="false">
			</tr>
		</gene:campoScheda>
	</c:otherwise>
</c:choose>

<gene:javaScript>
	<c:if test='${modoAperturaScheda ne "VISUALIZZA"}'>
		$('#COMMRUOLI_INVITI_${param.contatore}view').parent().append('&nbsp;').append($('#rowtitoloCOMMR_${param.contatore}'));		
	
		 $('#COMMRUOLI_RUOLO_${param.contatore}').change(function(e){
		 	var selectedid = $(this);
	   		var selectedEl = $(this).find(":selected").text();
	   		$('select[id^="COMMRUOLI_RUOLO_"]').each(function() {   		
	   		  var id = $(this).attr('id');
			  if( $(this).find(":selected").text() === selectedEl && selectedid.attr('id') != id && $(this).find(":selected").is(":visible")){
			  	alert('Ruolo già definito');
			  	$(selectedid).val('');
			  }
			});
	    });
	</c:if>
	<c:if test='${modoAperturaScheda eq "NUOVO"}'>	
		$('[id^="COMMRUOLI_RUOLO_"]').each(function() {
			setValue($(this)[0].name,'');
		});		
	</c:if>

	$('tr[id^="rowtitoloCOMMR_"]').find('td.etichetta-dato').remove();

</gene:javaScript>