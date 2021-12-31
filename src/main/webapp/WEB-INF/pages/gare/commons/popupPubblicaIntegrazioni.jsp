<%
/*
 * Created on: 31-08-2010
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
		Finestra per l'attivazione della funzione 'Pubblica su portale Alice Gare'
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.pubblicazioneEseguita and requestScope.pubblicazioneEseguita eq "1"}' >
			<gene:template file="popup-message-template.jsp">
			
			<gene:redefineInsert name="corpo">
			<gene:setString name="titoloMaschera" value='Pubblica su portale Appalti' />
			<c:set var="contextPath" value="${pageContext.request.contextPath}" />

			<br>
			Pubblicazione su portale completata.
			<br>&nbsp;
			<br>&nbsp;
			
			<gene:redefineInsert name="buttons">
				<input type="button" class="bottone-azione"  id="cancel" value="Chiudi" title="Chiudi" onclick="chiudi();"/>&nbsp;&nbsp;
			</gene:redefineInsert>
			<gene:javaScript>
			
			window.onload = function () { 
				window.opener.bloccaRichiesteServer();
				window.opener.location = "${contextPath}/History.do?"+csrfToken+"&metodo=reload";
			}
			function chiudi(){
				window.close();
			}
			</gene:javaScript>
			</gene:redefineInsert>
		</gene:template>
	</c:when>
	<c:otherwise>

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />
<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${contextPath}/js/jHtmlArea-0.7.5.min.js"></script> 
	<link rel="Stylesheet" type="text/css" href="${contextPath}/css/jquery/jHtmlArea/jHtmlArea.css" />
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
</gene:redefineInsert>



<c:set var="modo" value="NUOVO" scope="request" />

	<c:choose>
		<c:when test='${!empty param.isProceduraTelematica}'>
			<c:set var="isProceduraTelematica" value="${param.isProceduraTelematica}" />
		</c:when>
		<c:otherwise>
			<c:set var="isProceduraTelematica" value="${isProceduraTelematica}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.bando}'>
			<c:set var="bando" value="${param.bando}" />
		</c:when>
		<c:otherwise>
			<c:set var="bando" value="${bando}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.codgar}'>
			<c:set var="codgar" value="${param.codgar}" />
		</c:when>
		<c:otherwise>
			<c:set var="codgar" value="${codgar}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.ngara}'>
			<c:set var="ngara" value="${param.ngara}" />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${ngara}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.entita}'>
			<c:set var="entita" value="${param.entita}" />
		</c:when>
		<c:otherwise>
			<c:set var="entita" value="${entita}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.valtec}'>
			<c:set var="valtec" value="${param.valtec}" />
		</c:when>
		<c:otherwise>
			<c:set var="valtec" value="${valtec}" />
		</c:otherwise>
	</c:choose>
	
	<c:if test="${empty entita ||  entita==''}">
		<c:set var="entita" value="TORN" />
	</c:if>

	<c:choose>
		<c:when test='${!empty ngara}'>
			<c:set var="valoreChiave" value="${ngara}" />
		</c:when>
		<c:otherwise>
			<c:set var="valoreChiave" value="${codgar}" />
		</c:otherwise>
	</c:choose>
		
	<c:choose>
		<c:when test='${!empty param.garavviso}'>
			<c:set var="garavviso" value="${param.garavviso}" />
		</c:when>
		<c:otherwise>
			<c:set var="garavviso" value="${garavviso}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.idconfi}'>
			<c:set var="idconfi" value="${param.idconfi}" />
		</c:when>
		<c:otherwise>
			<c:set var="idconfi" value="${idconfi}" />
		</c:otherwise>
	</c:choose>
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="${entita }" gestisciProtezioni="false" 
	plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestorePubblicaIntegrazioni" 
	gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePubblicaIntegrazioni">	
	
		<gene:setString name="titoloMaschera" value='Pubblica su portale Appalti' />
		<gene:campoScheda>
			<td colspan="2">
			<c:choose>
				<c:when test='${controlloSuperato eq "SI"}'>
					<br>
					<c:if test="${not empty msgWarning}">
					<b>ATTENZIONE:</b>
					${msgWarning}
					<br><br>
					</c:if>
					Confermi l'integrazione dei documenti di gara sul portale Appalti?
					<br>
					Se si procede con l'operazione, i documenti che verranno pubblicati diverranno disponibili in sola visualizzazione. 
					<br>&nbsp;
					<br>&nbsp;
				</c:when>
				<c:otherwise>
					<br>
					<b>${titoloMsg}</b>
					${msg}
					<br>&nbsp;
					<br>&nbsp;
				</c:otherwise>
			</c:choose>
			 
				<gene:redefineInsert name="buttons">
					<c:choose>
						<c:when test='${controlloSuperato eq "SI"}'>
							<INPUT type="button" id="pulsanteConferma" class="bottone-azione" value="Pubblica" title="Pubblica" onclick="javascript:conferma()">&nbsp;
							<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
						</c:when>
						<c:otherwise>
							<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
						</c:otherwise>
					</c:choose>
				</gene:redefineInsert>
			</td>
		</gene:campoScheda>	
			<c:if test='${controlloSuperato eq "SI"}'>
				<gene:campoScheda campo="DATPUB" campoFittizio="true" definizione="D;0;;;DATPUB" obbligatorio="true"/>
			</c:if>
			<gene:campoScheda campo="CODGAR" campoFittizio="true" defaultValue="${codgar}" visibile="false" definizione="T21;0"/>
			<gene:campoScheda campo="NGARA" campoFittizio="true" defaultValue="${ngara}" visibile="false" definizione="T20;0"/>
			<gene:campoScheda campo="LISTADOC" campoFittizio="true" defaultValue="${listaDocumenti}" visibile="false" definizione="T1;0"/>
			<gene:campoScheda campo="PUBBTRASPARENZA" campoFittizio="true" value="${pubblicaTrasparenza}" visibile="false" definizione="T21;0"/>
			<input type="hidden" name="isProceduraTelematica" id="isProceduraTelematica" value="${isProceduraTelematica}" />
			<input type="hidden" name="bando" id="bando" value="${bando}" />
			<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
			<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
			<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}" />
			<input type="hidden" name="listaDocumenti" id="listaDocumenti" value="${param.listaDocumenti}" />
			<input type="hidden" name="pubblicaTrasparenza" id="pubblicaTrasparenza" value="${param.pubblicaTrasparenza}" />
			<h2>${param.listaDocumenti}</h2>
			
	</gene:formScheda>
	</gene:redefineInsert>
	

	
	<gene:javaScript>
		
		var $datepicker = $('#DATPUB');
		$datepicker.datepicker();
		$datepicker.datepicker( "option", "dateFormat", "dd/mm/yy" );
		$datepicker.datepicker('setDate', new Date());
		
		<c:if test="${isProceduraTelematica}"> 
			$("#DATPUB").prop("disabled",true);
		</c:if>
		
		function annulla(){
			window.close();
		}
		function conferma(){
			document.forms[0].jspPathTo.value="gare/commons/popupPubblicaIntegrazioni.jsp";
			schedaConferma();
		}
		
		
	</gene:javaScript>	
  
</gene:template>
</div>

	</c:otherwise>
</c:choose>
	