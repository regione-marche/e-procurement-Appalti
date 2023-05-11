
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

<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />

<c:set var="where" value="ngara<>codgar1 and ditta is not null and exists (select gp.numper from g_permessi gp where gp.codgar=gare.codgar1 and gp.syscon=${sessionScope.profiloUtente.id })
	 and exists(select t.codgar from torn t,garecont gc where t.codgar=gare.codgar1
	 and ((gc.ngara=gare.ngara and gc.ncont=1) or (gc.ngara=gare.codgar1 and (gc.ngaral is null or gc.ngaral=gare.ngara)))
	  and ((t.accqua is null or  t.accqua <>'1') or gc.esecscig = '1'))" />

<gene:template file="popup-template.jsp" gestisciProtezioni="false" schema="GARE">
	<gene:setString name="titoloMaschera" value="Selezione gara oggetto dell'ordine" />
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
			Nella lista sottostante sono riportate le gare, o lotti di gara, che sono state aggiudicate da cui puo' derivare l'ordine  
			<br/>
			Selezionare la gara, o lotto di gara, di interesse.
			<br/>
		<table class="lista">
			<tr>
				<td><gene:formLista entita="GARE" pagesize="25" tableclass="datilista" gestisciProtezioni="false" sortColumn="-3" where="${where}" >
					<gene:campoLista title="Opzioni"	width="50">
						<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
						</gene:PopUp>
					</gene:campoLista>
					<gene:campoLista campo="CODCIG"  ordinabile="true" href="javascript:archivioSeleziona(${datiArchivioArrayJs})"/>
					<gene:campoLista campo="NOT_GAR"  ordinabile="true"/>
					<gene:campoLista campo="CODGAR1" visibile="false"/>
					<gene:campoLista campo="NGARA"  ordinabile="true" href="javascript:archivioSeleziona(${datiArchivioArrayJs})"/>
					<gene:campoLista campo="DITTA"  />
					<gene:campoLista campo="TIPGEN"  entita="TORN" where="TORN.CODGAR=GARE.CODGAR1" ordinabile="true"/>
					<gene:campoLista campo="IMPAPP"  ordinabile="true"/>
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

