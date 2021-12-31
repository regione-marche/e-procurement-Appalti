<%
/*
 * Created on: 25/05/2009
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<c:choose>
	<c:when test='${not empty param.tipoCriterio}'>
		<c:set var="tipoCriterio" value='${param.tipoCriterio}' />
	</c:when>
	<c:otherwise>
		<c:set var="tipoCriterio" value="${tipoCriterio}" />
	</c:otherwise>
</c:choose>

<c:if test="${tipoCriterio eq 2}">
	<c:set var="abilitataGestionePrezzo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1149", "1", "true")}'/>
</c:if>


<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="GOEVMOD-scheda">

	<c:choose>
		<c:when test="${tipoCriterio eq 2}">
			<gene:setString name="titoloMaschera" value='Criterio di valutazione della busta economica'/>
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value='Criterio di valutazione della busta tecnica'/>
		</c:otherwise>
	</c:choose>
	
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="GOEVMOD" gestisciProtezioni="true">
			
			<gene:gruppoCampi idProtezioni="DATIGEN">
				<gene:campoScheda>
					<td colspan="2"><b>Dati generali</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="ID" visibile="false"/>
				<gene:campoScheda campo="IDCRIMOD" visibile="false"/>
				<gene:campoScheda campo="NECVAN1" visibile="false"/>
				<gene:campoScheda campo="NORPAR" />
				<gene:campoScheda campo="TIPPAR" visibile="false" defaultValue="${tipoCriterio }"/>
				<gene:campoScheda campo="DESPAR" obbligatorio="true"/>
				<c:if test="${tipoCriterio eq '2' and abilitataGestionePrezzo eq '1'}">
					<gene:campoScheda campo="ISNOPRZ" visibile="false"  />
					<gene:campoScheda campo="ISNOPRZ_FIT" campoFittizio="true" definizione="T30" title="Ai fini del calcolo soglia anomalia, criterio relativo a" value='${gene:if(datiRiga.GOEVMOD_ISNOPRZ eq "1", "Altri elementi di valutazione" ,"Prezzo")}' />
				</c:if>
				<gene:campoScheda campo="MAXPUN" obbligatorio="true"/>
				<gene:campoScheda campo="MINPUN" />
				<gene:campoScheda campo="LIVPAR" visibile="false" defaultValue="1"/>
				<gene:campoScheda campo="NORPAR1" visibile="false" defaultValue="0"/>
			</gene:gruppoCampi>
			
			
			<c:set var="idcrimod" value="${datiRiga.GOEVMOD_IDCRIMOD}" />
			<c:set var="id" value="${datiRiga.GOEVMOD_ID}" />
			<c:set var="numeroCriterio" value='${gene:getValCampo(key, "GOEVMOD.NECVAN1")}' />
			<c:set var="norpar" value="${datiRiga.GOEVMOD_NORPAR}" scope="request" />
			
			<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetSubCriteriGoevModFunction" parametro="${key}" />
			
			<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
				<jsp:param name="entita" value='GOEVMOD'/>
				<jsp:param name="chiave" value='${id};'/>
				<jsp:param name="nomeAttributoLista" value='subcriteriGoevmod' />
				<jsp:param name="idProtezioni" value="SUBCRIT" />
				<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gare/goevmod/sub-criterio.jsp"/>
				<jsp:param name="arrayCampi" value="'GOEVMOD_MAXPUN_', 'GOEVMOD_DESPAR_', 'GOEVMOD_LIVPAR_','GOEVMOD_NECVAN1_','GOEVMOD_ID_','GOEVMOD_NORPAR1_'"/>		
				<jsp:param name="titoloSezione" value="Sub-criterio" />
				<jsp:param name="titoloNuovaSezione" value="Nuovo sub-criterio" />
				<jsp:param name="descEntitaVociLink" value="sub-criterio" />
				<jsp:param name="msgRaggiuntoMax" value="i sub-criteri"/>
				<jsp:param name="usaContatoreLista" value="true" />
				<jsp:param name="sezioneListaVuota" value="false" />
				<jsp:param name="sezioneEliminabile" value="false" />
				<jsp:param name="sezioneInseribile" value="false" />
				<jsp:param name="tipoCriterioPadre" value="${tipoCriterio}" />
			</jsp:include>
			
			
			<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
			<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
					
			<input type="hidden" name="tipoCriterio" value="${tipoCriterio }"/>
			
		</gene:formScheda>

	</gene:redefineInsert>
	<gene:javaScript>
		
		<c:choose>
			<c:when test='${empty subCriteri}'>
				var idUltimoSubcriterio = 0;
				var maxIdSubcriterioVisualizzabile = 5;

				<c:set var="numeroSubcriteri" value="0" />
			</c:when>
			<c:otherwise>
				var idUltimoSubcriterio = ${fn:length(subCriteri)};
				var maxIdSubcriterioVisualizzabile = ${fn:length(subCriteri)+5};
				<c:set var="numerosubCriteri" value="${fn:length(subCriteri)}" />
			</c:otherwise>
		</c:choose>
		
		if(idUltimoSubcriterio>0){
			showObj("rowG1CRIDEF_DESCRI", false);
			showObj("rowG1CRIDEF_MODPUNTI", false);
			showObj("rowG1CRIDEF_MODMANU", false);
			showObj("rowDET", false);
		}
		
	
	</gene:javaScript>
</gene:template>