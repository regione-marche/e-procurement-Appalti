
<%
  /*
			 * Created on 01-07-2013
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


<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<div style="width: 97%;"><gene:template file="popup-template.jsp">

	<gene:redefineInsert name="head" >
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/std/pl.css" >
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/dataTable/dataTable/jquery.dataTables.css" >
	</gene:redefineInsert>
	
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	
	<c:choose>
		<c:when test="${param.genere eq '10'}">
			<c:set var="oggetto" value=" dell'elenco operatori"/>
		</c:when>
		<c:when test="${param.genere eq '20'}">
			<c:set var="oggetto" value=" del catalogo elettronico"/>
		</c:when>
		<c:otherwise>
			<c:set var="oggetto" value=" della gara"/>
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value="Controllo dei dati ${oggetto} ${fn:replace(param.codgar,'$','')} ai fini dell'adempimento L.190/2012" />
	<c:set var="stato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.Controllo190Function",pageContext,param.codgar)}'/>
	
	
	<gene:redefineInsert name="corpo">
<table class="lista" id="maintable" width="100%">
<tr>
<td>
<table id="tablevalidazionew3" class="validazionew3" width="100%">
<thead>
	<tr class="intestazione">
		<td width="30px">N.</td>
		<c:if test="${GaraLotti}">
		<td><b>Lotto</b></td>
		</c:if>
		<td><b>Titolo</b></td>
		<td><b>Dettaglio</b></td>			
	</tr>
</thead>
<tbody>	
<c:set var="cnt" value="0" />
<c:forEach items="${listaGare}" var="list" varStatus="listStatus">
	<c:choose>
		<c:when test="${empty list[1]}">
			<tr>
				<td width="30px" class="center">-</td>
				<c:choose>
					<c:when test="${GaraLotti}">
						<td class="center">${list[0]}</td>
						<td>Dati corretti</td>
						<td>Non sono stati rilevati dati mancanti o non validi per il lotto di gara</td>
					</c:when>
					<c:otherwise>
						<td>Dati corretti</td>
						<td>Non sono stati rilevati dati mancanti o non validi per la gara</td>
					</c:otherwise>
				</c:choose>
			</tr>
		</c:when>
		<c:otherwise>
		<!--<tr><td colspan=4  class="center"><b>${list[0]}</b></td></tr>-->
			<c:forEach items="${list[1]}" step="1" var="item">
				<tr>
					<c:set var="cnt" value="${cnt + 1}" />
					<td width="40px" class="center">${cnt}</td>
					<c:if test="${GaraLotti}">
					<td class="center">${list[0]}</td>
					</c:if>
					<td>${item[0]}</td>
					<td>${item[1]}</td>
				</tr>
			</c:forEach>
		</c:otherwise>
	</c:choose>
</c:forEach>
</tbody>
<tfoot class="intestazione">
	<tr>
		<td id="indexCol" width="30px">N.</td>
		<c:if test="${GaraLotti}">
		<td><b></b>Lotto</td>
		</c:if>
		<td><b></b>Titolo</td>
		<td><b></b>Messaggio</td>			
	</tr>
</tfoot>
</table>
</td>
</tr>	
<tr>
	<td class="comandi-dettaglio" colspan="2">
			<INPUT type="button" class="bottone-azione" value="Controlla nuovamente" title="Controlla nuovamente" onclick="javascript:controlla()">
		<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla()">&nbsp;
	</td>
</tr>
</table>
	</gene:redefineInsert>

	<gene:javaScript>
	
		window.opener.currentPopUp=null;
	
		window.onfocus=resettaCurrentPopup;

		function resettaCurrentPopup() {
			window.opener.currentPopUp=null;
		}
		
		function annulla(){
			window.close();
		}
		
		function controlla(){
			window.location.reload();
		}
		
		$(document).ready(function() {
			
		 $('#tablevalidazionew3').DataTable( {
			"language": {
				"sInfo": "Visualizzazione da _START_ a _END_ di _TOTAL_ ",
				"sLengthMenu":  "Visualizza _MENU_ righe",
				"oPaginate": {
					"sFirst":      "<<",
					"sPrevious":   "<",
					"sNext":       ">",
					"sLast":       ">>"
				}
			},
			"pagingType": "full_numbers",
			"lengthMenu": [[5 ,10, 15, 20, 999], ["5 righe", "10 righe", "15 righe", "20 righe", "tutte le righe"]],
			"ordering": false
		});
		$("#tablevalidazionew3_length").hide();
		$("#tablevalidazionew3_filter").hide();
		
		$('#tablevalidazionew3 tfoot td').each( function (i) {
			var title = $('#tablevalidazionew3 thead td').eq( $(this).index() ).text();
			if (this.id != 'indexCol'){$(this).html( '<input type="text" style="width: 100%; font-size:11px!important;" placeholder="Cerca per '+title+'" data-index="'+i+'" />' );}
			else{$(this).html( '<input type="text" style="width: 100%; font-size:11px!important;" placeholder="'+title+'" data-index="'+i+'" />' );}				
		} );
	  
		var table = $('#tablevalidazionew3').DataTable();
	 
		$( table.table().container() ).on( 'keyup', 'tfoot input', function () {
			table
				.column( $(this).data('index') )
				.search( this.value )
				.draw();
		} );
	} );

	 
		

	
	</gene:javaScript>
</gene:template></div>

