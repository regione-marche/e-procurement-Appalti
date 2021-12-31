<%
/*
 * Created on: 04/06/2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra che visualizza la lista delle imprese del raggruppamento da associare alla gara
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div style="width:97%;">

<gene:template file="popup-message-template.jsp">
	
	<c:if test='${not empty param.codimp && not empty param.ngara}'>
		<c:set var="codimp" value="${param.codimp}" />
		<c:set var="ngara" value="${param.ngara}" />
		
		<c:set var="where" value="RAGIMP.CODIME9='${codimp}' AND RAGIMP.CODDIC NOT IN (SELECT CODDIC FROM RAGDET WHERE RAGDET.NGARA ='${ngara }' AND RAGDET.CODIMP='${codimp}')" />
	</c:if>
	
	<c:choose>
		<c:when test='${not empty param.ngara}'>
			<c:set var="ngara" value="${param.ngara}" />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${ngara}" />
		</c:otherwise>
	</c:choose>
		
	<gene:setString name="titoloMaschera" value='Selezione consorziate esecutrici' />
		
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>

		<br>
		<c:choose>
			<c:when test='${empty RISULTATO }'>
				Selezionare dalla lista sottostante le ditte del consorzio che si intende designare come esecutrici
			</c:when>
			<c:when test='${RISULTATO eq "OK"}'>
				Le ditte selezionate sono state aggiunte all'elenco delle consorziate esecutrici
			</c:when>
			<c:when test='${RISULTATO eq "ERRORI"}'>
				Le ditte selezionate non sono state aggiunte all'elenco delle consorziate esecutrici
			</c:when>
		</c:choose>
		
		<br>
		<br>	
		
			
		<c:choose>
			<c:when test='${empty RISULTATO}'>
				<c:set var="modo" value="MODIFICA" scope="request" />
			</c:when>
			<c:otherwise>
				<c:set var="modo" value="APRI" scope="request" />
			</c:otherwise>
		</c:choose>
		
		
		<gene:formLista entita="RAGIMP" where="${where}" pagesize="20" sortColumn="3" tableclass="datilista" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAssociaDitteRAGDET">
			<gene:campoLista title="Seleziona<br><center>${titoloMenu}</center>" width="50">
				<c:if test="${currentRow >= 0}">
					<input type="checkbox" name="keys" value="${datiRiga.RAGIMP_CODIME9};${datiRiga.RAGIMP_CODDIC};${ngara}" />
				</c:if>
			</gene:campoLista>
			<gene:campoLista campo="CODIME9" visibile="false"/>
			<gene:campoLista campo="CODDIC" title="Codice ditta" />
			<gene:campoLista campo="NOMDIC" title="Ragione sociale" />
			<gene:campoLista campo="CFIMP" entita="IMPR" where="IMPR.CODIMP=RAGIMP.CODDIC"/>
			<gene:campoLista campo="PIVIMP" entita="IMPR" where="IMPR.CODIMP=RAGIMP.CODDIC"/>
			<input type="hidden" name="ngara" id="ngara" value="${ngara}" />			
						
			<gene:redefineInsert name="buttons">
				<c:if test='${empty RISULTATO}'>
					<INPUT type="button" class="bottone-azione" value="Aggiungi ditte selezionate" title="Aggiungi ditte selezionate" onclick="javascript:aggiungi()">&nbsp;
				</c:if>
				<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi()">&nbsp;
			</gene:redefineInsert>
			
		</gene:formLista>

  	</gene:redefineInsert>

	<gene:javaScript>
		
		<c:if test='${not empty param.codimp && not empty param.ngara}'>
			document.forms[0].trovaAddWhere.value="${where}";
		</c:if>
		
		function aggiungi(){
			var numeroOggetti = contaCheckSelezionati(document.forms[0].keys);
	  		if (numeroOggetti == 0) {
	      		alert("Selezionare almeno una ditta dalla lista");
	      	} else {
	      		listaConferma();
 			}
		}

		
		function chiudi(){
			<c:if test='${not empty RISULTATO}'>
				var paginalista = opener.document.forms[0].pgCorrente.value = 0;
				opener.listaVaiAPagina(paginalista);
			</c:if>
			window.close();
		}
		


	</gene:javaScript>
	
	</gene:template>
</div>
