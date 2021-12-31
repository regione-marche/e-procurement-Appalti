
<%
	/*
	 * Created on 06-09-2010
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<c:choose>
	<c:when test='${not empty RISULTATO and RISULTATO eq "OK"}' >
		<script type="text/javascript">
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
			window.close();
		</script>
	</c:when>
	<c:otherwise>
<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.preced}'>
		<c:set var="preced" value="${param.preced}" />
	</c:when>
	<c:otherwise>
		<c:set var="preced" value="${preced}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.risultatiPerPagina}'>
		<c:set var="risultatiPerPagina" value="${param.risultatiPerPagina}" />
	</c:when>
	<c:otherwise>
		<c:set var="risultatiPerPagina" value="${risultatiPerPagina}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.WIZARD_PAGINA_ATTIVA}'>
		<c:set var="WIZARD_PAGINA_ATTIVA" value="${param.WIZARD_PAGINA_ATTIVA}" />
	</c:when>
	<c:otherwise>
		<c:set var="WIZARD_PAGINA_ATTIVA" value="${WIZARD_PAGINA_ATTIVA}" />
	</c:otherwise>
</c:choose>

<c:set var="where" value="DITG.NGARA5='${preced }' and (DITG.FASGAR >=6 or DITG.FASGAR is null) and DITG.DITTAO not in (select D1.DITTAO from DITG D1 where D1.NGARA5='${ngara }')"/>
<c:set var="modo" value="MODIFICA" scope="request" />

<gene:template file="popup-template.jsp" gestisciProtezioni="false" >
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>	
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/controlliFormali.js"></script>	
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Selezione ditte dalla gara oggetto di rilancio ${preced} " />
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		
		<br/>
		Nella lista sottostante sono riportate le ditte partecipanti e ammesse alla fase di apertura offerte economiche della gara oggetto di rilancio.
		<br> Selezionare quelle a cui si intende richiedere il rilancio dell'offerta economica.
		
		<br/>
		
		<table class="lista">
			<tr>
				<td><gene:formLista entita="DITG" pagesize="${risultatiPerPagina}" tableclass="datilista" gestisciProtezioni="false" sortColumn="2" where="${where}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupSelDitteRilancio">
					<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"	width="50">
						<c:if test="${currentRow >= 0}">
							<input type="checkbox" name="keys" value="${datiRiga.DITG_DITTAO}"  />
						</c:if>
					</gene:campoLista>
										
					<gene:campoLista campo="NUMORDPL" width="80" ordinabile="true"/>
					<gene:campoLista campo="NOMIMO" ordinabile="true"/>
					<gene:campoLista campo="DITTAO" visibile="false"/>
					
					<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
                    <input type="hidden" name="preced" id="preced" value="${preced}" />
                    <input type="hidden" name="risultatiPerPagina" id="risultatiPerPagina" value="${risultatiPerPagina}" />
                    <input type="hidden" name="WIZARD_PAGINA_ATTIVA" id="WIZARD_PAGINA_ATTIVA" value="${WIZARD_PAGINA_ATTIVA}" />
                </gene:formLista></td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<c:if test="${datiRiga.rowCount > 0}">
						<INPUT type="button"  id="Aggiungi" class="bottone-azione" value='Aggiungi ditte selezionate' title='Aggiungi ditte selezionate' onclick="javascript:aggiungi();">&nbsp;&nbsp;&nbsp;
					</c:if>
					<INPUT type="button"  id="Chiudi" class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:chiudi();">&nbsp;
					
				</td>
			</tr>			
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		
		function aggiungi(){
			var numeroOggetti = contaCheckSelezionati(document.forms[0].keys);
	  		if (numeroOggetti == 0) {
	      		alert("Selezionare almeno una ditta dalla lista");
	      	} else {
				listaConferma();
 			}
		}
		
		//Viene ricaricata la pagina chiamante in modo da ripulire le 
		//variabili di sessioni
		function chiudi(){
			window.close();
		}
	</gene:javaScript>
</gene:template>
</c:otherwise>
</c:choose>