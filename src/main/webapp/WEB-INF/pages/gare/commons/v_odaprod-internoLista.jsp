<%
/*
 * Created on: 12/06/2014
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<tr>
	<td>
		<gene:formLista entita="V_ODAPROD" where="${param.where }" tableclass="datilista" sortColumn='1;2' 
				gestisciProtezioni="true"   pagesize="25" >
											
			<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
			<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>				
			<gene:redefineInsert name="pulsanteListaInserisci"></gene:redefineInsert>
			<gene:redefineInsert name="pulsanteListaEliminaSelezione"></gene:redefineInsert>			
			
			<gene:campoLista campo="CODOE" headerClass="sortable" />
			<gene:campoLista campo="IDPROD" visibile="false" />
			<gene:campoLista campo="NOME" headerClass="sortable" />
			<gene:campoLista campo="UNIMISACQ" headerClass="sortable" />
			<gene:campoLista campo="PRZUNITPROD"  headerClass="sortable"  />
			<gene:campoLista campo="QUANTIRIC" headerClass="sortable"  />
			<gene:campoLista campo="PRZUNITPER" visibile="true"/>
			<gene:campoLista campo="DESDET1" visibile="true"/>
			<gene:campoLista campo="DESDET2" visibile="true"/>
			<gene:campoLista campo="QUADET1" visibile="true"/>
			<gene:campoLista campo="QUADET2" visibile="true"/>
			<gene:campoLista campo="QUANTIOFF" headerClass="sortable" />
			<gene:campoLista campo="PREOFF" headerClass="sortable" />
			<gene:campoLista campo="PERCIVA" headerClass="sortable" />
			<gene:campoLista campo="TEMPOCONSEGNA" headerClass="sortable" />
			<input type="hidden" id="id" name="id" value="${id}" />
		</gene:formLista>
	<gene:javaScript>
	
		// spegnimento delle colonne di ausilio per la definizione del tooltip per la tipologia 4
		$("th[id^='titV_ODAPROD_PRZUNITPER']").hide();
		$("th[id^='titV_ODAPROD_DESDET1']").hide();
		$("th[id^='titV_ODAPROD_QUADET1']").hide();
		$("th[id^='titV_ODAPROD_DESDET2']").hide();
		$("th[id^='titV_ODAPROD_QUADET2']").hide();
		$("span[id^='colV_ODAPROD_PRZUNITPER_']").parent().hide();
		$("span[id^='colV_ODAPROD_DESDET1_']").parent().hide();
		$("span[id^='colV_ODAPROD_QUADET1_']").parent().hide();
		$("span[id^='colV_ODAPROD_DESDET2_']").parent().hide();
		$("span[id^='colV_ODAPROD_QUADET2_']").parent().hide();

		// individuazione della colonna a cui aggiungere il tooltip
		var nomeColonnaConTooltip = "th[id^='titV_ODAPROD_QUANTIRIC']";
		var colonnaConTooltip = $(nomeColonnaConTooltip);
		var indiceColonnaDaAggiungereTooltip = $("th[id^='tit']").index(colonnaConTooltip);

		// si istanzia il tooltip ove necessario
		$("span[id^='colV_ODAPROD_QUANTIRIC_']").each( function( index, element ){
			var desdet1 = $("#colV_ODAPROD_DESDET1_"+(index+1) + " > span").text();
			var desdet2 = $("#colV_ODAPROD_DESDET2_"+(index+1) + " > span").text();
			var quadet1 = $("#colV_ODAPROD_QUADET1_"+(index+1) + " > span").text();
			var quadet2 = $("#colV_ODAPROD_QUADET2_"+(index+1) + " > span").text();
			if (quadet1 != "" && quadet2 != "") {
				var testoTooltip = " - Dettaglio quantit&agrave; richiesta:<br/><ul><li>" + quadet1 + " " + desdet1 + " per " + quadet2 + " " + desdet2 + "</li></ul>";
				$(this).after(" <img id=\"INFO_TOOLTIP" + index + "\" title=\"" + testoTooltip + "\" src=\"${contextPath}/img/dettaglioQuantita.gif\" class=\"right\">");
			}
		});

		//associazione del plugin jquery-tooltip allo span
		$("img[id^='INFO_TOOLTIP']").tooltip({ 
			track: true, 
			delay: 250, 
			showURL: false, 
			opacity: 1, 
			fixPNG: true, 
			showBody: " - ", 
			extraClass: "pretty fancy", 
			top: 15, 
			left: 5 
		});
		
	</gene:javaScript>

	</td>
</tr>
