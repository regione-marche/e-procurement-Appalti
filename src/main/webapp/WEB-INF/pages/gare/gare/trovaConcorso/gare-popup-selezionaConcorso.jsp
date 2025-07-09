
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

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions.ComponiWhereGAREFunction" />

<c:choose>
	<c:when test='${not empty param.tipgen}'>
		<c:set var="tipgen" value="${param.tipgen}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipgen" value="${tipgen}" />
	</c:otherwise>
</c:choose>

<c:if test='${gene:matches(tipgen, "^1|2|3$", true)}' />

<c:choose>
	<c:when test="${uffintAbilitati eq '1' }">
		<c:set var="condizioneTorn" value="(select codgar from torn where gare.codgar1=codgar and tipgen= ${tipgen} and iterga=9 and tipoconc=2 and cenint='${uffint }')"/>
	</c:when>
	<c:otherwise>
		<c:set var="condizioneTorn" value="(select codgar from torn where gare.codgar1=codgar and tipgen= ${tipgen} and iterga=9 and tipoconc=2)"/>
	</c:otherwise>
</c:choose>

<c:set var="where" value="stepgar=55 and not exists(select 1 from gare g where g.ngarapa=gare.ngara) and exists ${condizioneTorn }" />

<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />

<c:if test="${ sessionScope.profiloUtente.abilitazioneGare ne 'A'}">
	<c:set var="where" value="${where } and exists (select gp.numper from g_permessi gp where gp.codgar=codgar1 and gp.syscon=${sessionScope.profiloUtente.id })" />
</c:if>

<gene:template file="popup-template.jsp" gestisciProtezioni="false" schema="GARE">
	<gene:setString name="titoloMaschera" value="Selezione concorso di progettazione" />
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
			Nella lista sottostante sono riportati i concorsi di progettazione in due gradi per ${tipo}, per cui si è conclusa la prima fase di valutazione tecnica.
			<br/>
			Selezionare il concorso di progettazione di interesse.
			<br/>
		
		<table class="lista">
			<tr>
				<td><gene:formLista entita="GARE" where="${where}" pagesize="25" tableclass="datilista" gestisciProtezioni="false" sortColumn="-2" >
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

