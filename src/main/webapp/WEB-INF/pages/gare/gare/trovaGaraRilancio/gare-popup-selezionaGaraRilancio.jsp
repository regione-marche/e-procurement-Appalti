
<%
	/*
	 * Created on 30-10-2018
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
	<c:when test='${not empty param.tipgen}'>
		<c:set var="tipgen" value="${param.tipgen}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipgen" value="${tipgen}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${uffintAbilitati eq '1' }">
		<c:set var="condizioneTorn" value="(select codgar from torn where gare.codgar1=codgar and tipgen=${tipgen } and offtel=1 and (ricastae is null or ricastae='2') and cenint='${uffint }')"/>
	</c:when>
	<c:otherwise>
		<c:set var="condizioneTorn" value="(select codgar from torn where gare.codgar1=codgar and tipgen=${tipgen } and offtel=1 and (ricastae is null or ricastae='2'))"/>
	</c:otherwise>
</c:choose>

<c:set var="where" value="ngara<>codgar1 and (preced is null or preced='') and fasgar=6 
 and (ribcal is null or ribcal != 3) and (modlicg<>6 or (modlicg=6 and exists (select g1.ngara from g1cridef g1 where g1.ngara=gare.ngara and formato in (50,51,52)))) 
 and not exists (select g2.ngara from gare g2 where g2.codgar1=gare.codgar1 and g2.codgar1=g2.ngara and g2.bustalotti=2)
 and exists ${condizioneTorn }" />

<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />

<c:if test="${ sessionScope.profiloUtente.abilitazioneGare ne 'A'}">
	<c:set var="where" value="${where } and exists (select gp.numper from g_permessi gp where gp.codgar=codgar1 and gp.syscon=${sessionScope.profiloUtente.id })" />
</c:if>

<gene:template file="popup-template.jsp" gestisciProtezioni="false" schema="GARE">
	<gene:setString name="titoloMaschera" value="Selezione gara oggetto di rilancio" />
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
			<c:choose>
				<c:when test="${tipgen eq '1' }">
					<c:set var="tipo" value="lavori"/>
				</c:when>
				<c:when test="${tipgen eq '2' }">
					<c:set var="tipo" value="forniture"/>
				</c:when>
				<c:otherwise>
					<c:set var="tipo" value="servizi"/>
				</c:otherwise>
			</c:choose>
		
			<br/>
			Nella lista sottostante sono riportate le gare, o lotti di gara, per ${tipo} e in fase di apertura offerte economiche, per cui possono essere gestite delle procedure di rilancio.
			<br>Non compaiono nella lista i lotti delle gare con unica busta tecnica ed economica per tutti i lotti, le gare con offerta presentata mediante prezzi unitari e somma sconti pesati,
			le gare OEPV che non hanno criteri per la busta economica con formato del tipo 'Offerta complessiva espressa mediante ...'.   
			<br/>
			Selezionare la gara, o lotto di gara, di interesse.
			<br/>
		
		<table class="lista">
			<tr>
				<td><gene:formLista entita="GARE" pagesize="25" tableclass="datilista" gestisciProtezioni="false" sortColumn="-2" where="${where}" >
					<gene:campoLista title="Opzioni"	width="50">
						<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
						</gene:PopUp>
					</gene:campoLista>
					<gene:campoLista campo="NGARA"  ordinabile="true" href="javascript:archivioSeleziona(${datiArchivioArrayJs})"/>
					<gene:campoLista campo="NOT_GAR"  ordinabile="true"/>
					
					<input type="hidden" name="tipgen" id="tipgen" value="${tipgen}" />
					
				</gene:formLista></td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<INPUT type="button"  id="Chiudi" class="bottone-azione" value='Chiudi' title='Chiudi' onclick="window.close();">&nbsp;
					
				</td>
			</tr>			
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		
		
	</gene:javaScript>
</gene:template>

