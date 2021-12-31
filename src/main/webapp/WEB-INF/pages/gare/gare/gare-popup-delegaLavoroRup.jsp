<%
/*
 * Created on: 20-05-2011
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
		Finestra per l'attivazione della funzione 'Delega commessa RUP'
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.delegaEseguita and requestScope.delegaEseguita eq "1"}' >
<script type="text/javascript">
		window.close();
</script>
	</c:when>
	<c:otherwise>
	
	
<div style="width:97%;">
<gene:template file="popup-message-template.jsp">




<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.ditta}'>
		<c:set var="ditta" value="${param.ditta}" />
	</c:when>
	<c:otherwise>
		<c:set var="ditta" value="${ditta}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codrup}'>
		<c:set var="codrup" value="${param.codrup}" />
	</c:when>
	<c:otherwise>
		<c:set var="codrup" value="${codrup}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.nmaximo}'>
		<c:set var="nmaximo" value="${param.nmaximo}" />
	</c:when>
	<c:otherwise>
		<c:set var="nmaximo" value="${nmaximo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.clavor}'>
		<c:set var="clavor" value="${param.clavor}" />
	</c:when>
	<c:otherwise>
		<c:set var="clavor" value="${clavor}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.numera}'>
		<c:set var="numera" value="${param.numera}" />
	</c:when>
	<c:otherwise>
		<c:set var="numera" value="${numera}" />
	</c:otherwise>
</c:choose>

<gene:setString name="titoloMaschera" value='Delega della commessa al RUP' />

<c:set var="isLavoroDelegato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GestionePopupDelegaLavoroRupFunction", pageContext, codrup,clavor)}'/>
	
<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="TORN" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupDelegaLavoroRup">
	
	<gene:campoScheda>
		<td colSpan="2">
			<c:if test='${empty codrup or codrup eq ""}' >
				<c:set var="msgBlocco" value="<br>- non è stato specificato il RUP" /> 
			</c:if>
			<c:if test='${empty ditta or ditta eq ""}' >
				<c:set var="msgBlocco" value="${msgBlocco}<br>- la gara non è aggiudicata in via definitiva"/>
			</c:if>
			<c:if test='${not empty codrup and codrup ne "" and not empty requestScope.controlloRUPSuperato and requestScope.controlloRUPSuperato eq "NO"}'>
				<c:set var="msgBlocco" value="${msgBlocco}<br>- il RUP non risulta associato a nessun utente applicativo" />
			</c:if>
			<c:if test='${numera ne 1}'>
				<c:set var="msgBlocco" value="${msgBlocco}<br>- la gara deve essere associata al primo appalto della commessa" />
			</c:if>
			<br>
			<c:choose>
				<c:when test='${not empty msgBlocco and msgBlocco ne ""}' >
					Non è possibile procedere alla delega della commessa al RUP:
					<br>${msgBlocco}
				</c:when>
				<c:when test='${isLavoroDelegato eq "SI" && requestScope.tecniciTuttiDelegati eq "SI"}' >
					La commessa risulta già delegata al RUP e a tutti i soggetti presenti nell'elenco degli incarichi professionali della commessa stessa.
					<br>Si procederà al solo aggiornamento del numero ordine.
				</c:when>
				<c:when test='${isLavoroDelegato eq "SI" && requestScope.tecniciTuttiDelegati eq "NO"}' >
					La commessa risulta già delegata al RUP. 
					<br>Si procederà alla delega per i soli soggetti presenti nell'elenco 
					degli incarichi professionali della commessa che non risultano ancora delegati 
					e all'aggiornamento del numero ordine.
				</c:when>
				<c:otherwise>
					Confermi la delega della commessa al RUP e 
					a tutti i soggetti presenti nell'elenco degli incarichi professionali della commessa stessa?
					<br>Si procederà anche all'aggiornamento del numero ordine.
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
	<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
	<input type="hidden" name="ditta" id="ditta" value="${ditta}" />
	<input type="hidden" name="codrup" id="codrup" value="${codrup}" />
	<input type="hidden" name="nmaximo" id="nmaximo" value="${nmaximo}" />	
	<input type="hidden" name="clavor" id="clavor" value="${clavor}" />
	<input type="hidden" name="isLavoroDelegato" id="isLavoroDelegato" value="${isLavoroDelegato}" />
	<input type="hidden" name="tecniciTuttiDelegati" id="tecniciTuttiDelegati" value="${requestScope.tecniciTuttiDelegati}" />
	</gene:formScheda>
  </gene:redefineInsert>

<c:if test='${(not empty requestScope.controlloRUPSuperato and requestScope.controlloRUPSuperato eq "NO") or empty ditta or ditta eq "" or empty codrup or codrup eq "" or numera ne 1}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
	</gene:redefineInsert>
</c:if>
	
	
	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/gare/gare-popup-delegaLavoroRup.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
		
	

	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>