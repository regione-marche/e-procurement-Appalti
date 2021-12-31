<%
	/*
	 * Created on 29-Set-2017
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:template file="scheda-template.jsp">

	<gene:redefineInsert name="head" >
		<script type="text/javascript" src="${contextPath}/js/jquery.cookie.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.pg.permessi.garatelematica.js"></script>
		
		<style type="text/css">
		
			TABLE.schedagperm {
				margin-top: 5px;
				margin-bottom: 5px;
				padding: 0px;
				font-size: 11px;
				border-collapse: collapse;
				border-left: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
			}
	
			TABLE.schedagperm TR.intestazione {
				background-color: #EFEFEF;
				border-bottom: 1px solid #A0AABA;
			}
			
			TABLE.schedagperm TR.intestazione TD, TABLE.schedagperm TR.intestazione TH {
				padding: 5 2 5 2;
				text-align: center;
				font-weight: bold;
				border-left: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-bottom: 1px solid #A0AABA;
				height: 30px;
			}
		
			TABLE.schedagperm TR.sezione {
				background-color: #EFEFEF;
				border-bottom: 1px solid #A0AABA;
			}
			
			TABLE.schedagperm TR.sezione TD, TABLE.schedagperm TR.sezione TH {
				padding: 5 2 5 2;
				text-align: left;
				font-weight: bold;
				height: 25px;
			}
		
			TABLE.schedagperm TR {
				background-color: #FFFFFF;
			}
	
			TABLE.schedagperm TR TD {
				padding-left: 3px;
				padding-top: 1px;
				padding-bottom: 1px;
				padding-right: 3px;
				text-align: left;
				border-left: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-bottom: 1px solid #A0AABA;
				height: 25px;
				font: 11px Verdana, Arial, Helvetica, sans-serif;
			}
			
			TABLE.schedagperm TR.intestazione TH.codice, TABLE.schedagperm TR TD.codice {
				width: 70px;
			}

			TABLE.schedagperm TR.intestazione TH.matricola, TABLE.schedagperm TR TD.matricola {
				width: 100px;
			}

			TABLE.schedagperm TR.intestazione TH.descr, TABLE.schedagperm TR TD.descr {
				width: 140px;
			}
			
			TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.ck {
				width: 35px;
				text-align: center;
			}
			
			img.img_titolo {
				padding-left: 8px;
				padding-right: 8px;
				width: 24px;
				height: 24px;
				vertical-align: middle;
			}
			
			.dataTables_length, .dataTables_filter {
				padding-bottom: 5px;
			}

			.dataTables_empty {
				padding-top: 6px;
			}
				
			div.tooltip {
				width: 300px;
				margin-top: 3px;
				margin-bottom:3px;
				border: 1px solid #A0AABA;
				padding: 10px;
				display: none;
				position: absolute;
				z-index: 1000;
				background-color: #F4F4F4;
			}
			

			input.search {
				height: 16px;
				font: 11px Verdana, Arial, Helvetica, sans-serif;
				background-color: #FFFFFF;
				color: #000000;
				vertical-align: middle;
				border: 1px #366A9B solid;
				width: 98%;
				font-style: italic;
			}
				
		</style>
		
	</gene:redefineInsert>
	
	<gene:redefineInsert name="corpo">
		<gene:setString name="titoloMaschera" value="Punto ordinante e punto istruttore della gara ${fn:replace(codgar, '$', '')}" />

		<form id="formVisualizzaPermessiUtentiStandard" name="formVisualizzaPermessiUtenti" action="${pageContext.request.contextPath}/pg/VisualizzaPermessiUtentiGaraTelematica.do" method="post">
			<input type="hidden" name="metodo" id="metodo" value="apri" />
			<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
			<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="${permessimodificabili}" />
			<input type="hidden" name="operation" id="operation" value="${operation}" />
			<input type="hidden" name="codein" id="codein" value="${codein}" />
		</form> 
		
		<form id="formModificaPermessiUtentiStandard" name="formModificaPermessiUtentiStandard" action="${pageContext.request.contextPath}/pg/ModificaPermessiUtentiGaraTelematica.do" method="post">
			<input type="hidden" name="metodo" id="metodo" value="apri" />
			<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
			<input type="hidden" name="operation" id="operation" value="${operation}" />
			<input type="hidden" name="permessimodificabili" id="permessimodificabili" value="${permessimodificabili}" />
			<input type="hidden" name="codein" id="codein" value="${codein}" />
		</form>

		<table class="dettaglio-notab">
			<tr>
				<td>
					<br>
					<div id="utentiContainer" style="margin-left:8px; width: 98%"></div>
				    <br>
				</td>
			</tr>
			<tr>	
				<td class="comandi-dettaglio">
					<c:if test="${permessimodificabili eq 'true'}">
					<c:choose>
						<c:when test='${isNavigazioneDisattiva ne "1"}'>
							<INPUT type="button" id="pulsantemodificapermessi" class="bottone-azione" value='Modifica' title='Modifica'>
						</c:when>
						<c:otherwise>
							<INPUT type="button" id="pulsantesalvamodifichepermessi" class="bottone-azione" value="Salva" title="Salva">
							<INPUT type="button" id="pulsanteannullamodifichepermessi" class="bottone-azione" value="Annulla" title="Annulla">
						</c:otherwise>
					</c:choose>
					</c:if>
					&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>

	<gene:redefineInsert name="addToAzioni" >
		<c:if test="${permessimodificabili eq 'true'}">
			<c:choose>
				<c:when test='${isNavigazioneDisattiva ne "1"}'>
		     	<tr>
	        	<td class="vocemenulaterale">
							<a href="#" id="menumodificapermessi" title="Modifica" tabindex="1512">Modifica</a>
			  		</td>
			  	</tr>
			  	<tr id="impostaCondivisionePredef">
				  	<td class="vocemenulaterale" >
				  		<a href="javascript:impostaCondivisionePredefinita();" tabindex="1513" title="Imposta condivisione predefinita">
				  			Imposta condivisione predefinita
				  		</a>
				  	</td>
			  	</tr>
        </c:when>
	    	<c:otherwise>
		    	<tr>
			     	<td class="vocemenulaterale">
							<a href="#" id="menusalvamodifichepermessi" title="Salva" tabindex="1513">Salva</a>
				  	</td>
					</tr>
		      <tr>
			     	<td class="vocemenulaterale">
							<a href="#" id="menuannullamodifichepermessi" title="Annulla" tabindex="1514">Annulla</a>
				  	</td>
				 </tr>  	
			  </c:otherwise>
			</c:choose>
		</c:if>
	</gene:redefineInsert>

	<gene:redefineInsert name="noteAvvisi"/>
	<gene:redefineInsert name="documentiAssociati"/>
	
	<gene:javaScript>

		function impostaCondivisionePredefinita() {
	<c:choose>
		<c:when test='${! empty esisteCondivisionePredefinita}'>
			if (confirm('<fmt:message key="info.permessi.condivisionePredefinita.sovrascrittura"/>')) {
		</c:when>
		<c:otherwise>
			if (confirm('<fmt:message key="info.permessi.condivisionePredefinita"/>')) {
		</c:otherwise>
	</c:choose>
				bloccaRichiesteServer();
				formVisualizzaPermessiUtentiStandard.metodo.value = "setPermessiPredefiniti";
				formVisualizzaPermessiUtentiStandard.submit();
			}
		}
	
	</gene:javaScript>

</gene:template>
