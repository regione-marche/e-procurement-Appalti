
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

<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />

<c:set var="filtroUffint" value=""/> 
<c:if test="${! empty sessionScope.uffint}">
	<c:set var="filtroUffint" value=" and exists (select id from v_gare_stipula vgs where vgs.id=g1stipula.id and vgs.cenint='${sessionScope.uffint}')"/>
</c:if>

<c:choose>
	<c:when test='${abilitazioneGare eq "A"}'>
		<c:set var="where" value="stato=5 and livello=0${filtroUffint}" />
	</c:when>
	<c:otherwise>
		<c:set var="where" value="stato=5 and livello=0
		 and exists (select gp.numper from g_permessi gp where gp.idstipula=g1stipula.id and gp.syscon=${sessionScope.profiloUtente.id})${filtroUffint}" />
	</c:otherwise>
</c:choose>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.archWhereFunctions.ComponiWhereG1STIPULAFunction" />

<gene:template file="popup-template.jsp" gestisciProtezioni="false" schema="GARE">
	<gene:setString name="titoloMaschera" value="Selezione del contratto oggetto di stipula" />
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
	
			<br/>
			Nella lista sottostante sono riportate le stipule, per cui si puo' procedere alla creazione di un atto aggiuntivo/variante .  
			<br/>
			Selezionare la stipula.
			<br/>
		<table class="lista">
			<tr>
				<td><gene:formLista entita="G1STIPULA" pagesize="25" tableclass="datilista" gestisciProtezioni="false"  sortColumn="2">
					<gene:campoLista title="Opzioni"	width="50">
						<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
						</gene:PopUp>
					</gene:campoLista>
					<gene:campoLista campo="CODSTIPULA" title="Codice stipula" href="javascript:archivioSeleziona(${datiArchivioArrayJs})" />
					<gene:campoLista campo="OGGETTO" />
					<gene:campoLista campo="NGARA" />
					<gene:campoLista campo="NCONT" visibile='false'/>
					<gene:campoLista campo="CODIGA" entita="GARE" where="G1STIPULA.NGARA=GARE.NGARA" visibile='false'/>
					<gene:campoLista campo="ID" visibile='false'/>
					<gene:campoLista campo="ID_ORIGINARIO" visibile='false'/>
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

</gene:template>

