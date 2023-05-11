
<%
/*
 * Created on: 17-mag-2021
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 /* Lista Documentazione di Contratto */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<gene:template file="popup-template.jsp" gestisciProtezioni="false" >

	<gene:redefineInsert name="head" >
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.rowsorter-2.1.1.js"></script>
	</gene:redefineInsert>	

	<gene:setString name="titoloMaschera" value="Riporta documenti in compilazione" />
	
	<c:choose>
		<c:when test='${not empty param.idStipula}'>
			<c:set var="idStipula" value="${param.idStipula}" />
		</c:when>
		<c:otherwise>
			<c:set var="idStipula" value="${idStipula}" />
		</c:otherwise>
	</c:choose>
	
	<c:set var="where" value="idstipula=${idStipula} and STATODOC = 4 and VISIBILITA = 3"/>
	
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
	
	<br>Nella lista sotto sono riportati i documenti della stipula ricevuti dall'operatore. E' possibile riportarli in compilazione per poterli richiedere nuovamente all'operatore.
	<br>Selezionare i documenti che si vuole riportare in compilazione:
			
  	<%// Creo la lista per gcap e dpre mediante la vista v_gcap_dpre%>
		<table class="lista">
					
			<tr>
				<td ${stileDati}>
  				<gene:formLista entita="G1DOCSTIPULA" where="${where}" pagesize="0" tableclass="datilista" sortColumn="4;5" gestisciProtezioni="false" >
  					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
						
					<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
						<input type="checkbox" name="keys" value="${chiaveRiga}" />
					</gene:campoLista>
					<gene:campoLista campo="ID"  visibile="false" />
					<gene:campoLista campo="IDSTIPULA"  visibile="false" />
					<gene:campoLista campo="FASE"  visibile="true"/>
					<gene:campoLista campo="NUMORD"  visibile="false"/>
					<gene:campoLista campo="TITOLO"  />
					<gene:campoLista campo="DESCRIZIONE"  />
					<gene:campoLista campo="IDPRG" visibile="false" entita="V_GARE_DOCSTIPULA" where="V_GARE_DOCSTIPULA.ID=G1DOCSTIPULA.ID" />
				    <gene:campoLista campo="IDDOCDIG" visibile="false" entita="V_GARE_DOCSTIPULA" where="V_GARE_DOCSTIPULA.ID=G1DOCSTIPULA.ID" />
				    <gene:campoLista campo="DIGNOMDOC" visibile="true" entita="V_GARE_DOCSTIPULA" where="V_GARE_DOCSTIPULA.ID=G1DOCSTIPULA.ID" ordinabile="false"
				    href="javascript: 	visualizzaFileAllegato('${datiRiga.V_GARE_DOCSTIPULA_IDPRG}','${datiRiga.V_GARE_DOCSTIPULA_IDDOCDIG}', '${datiRiga.V_GARE_DOCSTIPULA_DIGNOMDOC}');" />
					<gene:campoLista campo="ID_FIT" visibile="false" value="${datiRiga.G1DOCSTIPULA_ID}" edit="true"  campoFittizio="true" 
					definizione="N12" title ="ID_FIT"/>

					<input type="hidden" name="idStipula" id="idStipula" value="${idStipula}" />
				</gene:formLista>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
							<INPUT type="button"  class="bottone-azione" value='Conferma' title='Conferma' onclick="javascript:riportaInCompilazione()">
							<INPUT type="button"  class="bottone-azione" value='Annulla' title='Annulla' onclick="javascript:chiudi();">
							
					&nbsp;
				</td>
			</tr>
			
		</table>
  </gene:redefineInsert>
	<gene:javaScript>
	
	<c:if test="${requestScope.RISULTATO eq 'OK'}">
		opener.historyReload();
	</c:if>
	
	function chiudi(){
		
		window.close();
	}
	
	function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
		
		var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
		document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
	}
	
	function riportaInCompilazione() {
			var numeroOggetti = contaCheckSelezionati(document.forms[0].keys);
	  		if (numeroOggetti == 0) {
	      		alert("Selezionare almeno un documento");
	      	} else {
	      		document.forms[0].action="${pageContext.request.contextPath}/pg/SetStatoDocumentoStipula.do?"+csrfToken;
				bloccaRichiesteServer();
				document.forms[0].submit();
 			}
	}
	
	
	</gene:javaScript>
	
	
</gene:template>