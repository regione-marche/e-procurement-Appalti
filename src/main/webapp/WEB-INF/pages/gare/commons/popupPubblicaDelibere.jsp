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
			
			function chiudi(){
				window.close();
			}
			
			window.onload = function () { 
				window.opener.bloccaRichiesteServer();
				window.opener.location = "${contextPath}/History.do?metodo=reload";
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
<c:set var="tipoPubSitoIstituzionale" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", tipoPubblicazioneSitoIstituzionale)}'/>
<c:set var="urlDeliberaPortaleAppalti" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "invioScp.urlDeliberaPortaleAppalti")}'/>

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
	
	<c:set var="where" value="codgar=?"/>
	<c:set var="sql" value="${'select sezlavpub from torn where '}${where}"/>
	<c:set var="sezLavPubTORN" value="${gene:callFunctionN('it.eldasoft.gene.tags.functions.GetSafeDataFromDB', pageContext, sql, codgar)}"/>
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="${entita }" gestisciProtezioni="false" 
	plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestorePubblicaDelibere" 
	gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePubblicaDelibere">	
	
		<gene:setString name="titoloMaschera" value='Pubblica su portale Appalti' />
		<gene:campoScheda>
			<td colspan="2">
			<c:choose>
				<c:when test='${controlloSuperato eq "SI"}'>
					<br>
					Confermi la pubblicazione della delibera a contrarre sul portale Appalti?
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
			<input type="hidden" name="isProceduraTelematica" id="isProceduraTelematica" value="${isProceduraTelematica}" />
			<input type="hidden" name="bando" id="bando" value="${bando}" />
			<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
			<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
			<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}" />
			<input type="hidden" name="listaDocumenti" id="listaDocumenti" value="${param.listaDocumenti}" />

			<c:if test="${ not empty bandoIdRegMarche and tipoPubSitoIstituzionale eq '1' }">
				<input id="bandoIdRegMarche" type="hidden" name="bandoIdRegMarche" value="${bandoIdRegMarche}" /> 
			</c:if>

		<c:if test="${tipoPubSitoIstituzionale eq '1' and controlloSuperato eq 'SI'}">
		<gene:campoScheda>
		<td colSpan="2">
			<tr>
				<td colspan="2">
					<b>Dati per pubblicazione su sito istituzionale</b>
				</td>
			</tr>
			<tr>
				<td class="etichetta-dato">Struttura (*)</td>
				<td class="valore-dato">
					<select id="strutturaRegionale" name="strutturaRegionale" style="width:350px;">
						<option value=""></option>
						<c:forEach items="${listaStruttureRegionali}" step="1" var="strutturaRegionale" varStatus="status" >
							 <option value="${strutturaRegionale[1]}" <c:if test="${idStrutturaRegionale eq strutturaRegionale[1] }">selected</c:if>>${strutturaRegionale[0]}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td class="etichetta-dato">Tema (*)</td>
				<td class="valore-dato">
					<select id="temaRegionale" name="temaRegionale" style="width:350px;">
						<option value=""></option>
						<c:forEach items="${listaTemiRegionali}" step="1" var="temaRegionale" varStatus="status" >
							 <option value="${temaRegionale[1]}" <c:if test="${idTemaRegionale eq  temaRegionale[1]}">selected</c:if>>${temaRegionale[0]}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td class="etichetta-dato">Tipo procedura (*)</td>
				<td class="valore-dato">
					<select id="tipologiaBando" name="tipologiaBando" style="width:350px;">
						<option value=""></option>
						<c:forEach items="${listaTipologieProcedure}" step="1" var="tipologiaProcedura" varStatus="status" >
							 <option value="${tipologiaProcedura[1]}" <c:if test="${idTipologiaBando eq tipologiaProcedura[1] }">selected</c:if>>${tipologiaProcedura[0]}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td class="etichetta-dato">Lavori pubblici? (*)</td>
				<td class="valore-dato">
					<select id="lavoriPubblici" name="lavoriPubblici" style="width:350px;">
						<option value=""></option>
						<option value="1">Si</option>
						<option value="2">No</option>
					</select>
				</td>
			</tr>
		</td>
		</gene:campoScheda>
		</c:if>
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
		
		<c:if test="${tipoPubSitoIstituzionale eq '1'}">
			var dataPubblicazione = getValue("DATPUB");
			if(dataPubblicazione== null || dataPubblicazione==""){
				alert("Specificare la data di pubblicazione");
				return;
			}
			var strutturaRegionale = getValue("strutturaRegionale");
			if(strutturaRegionale== null || strutturaRegionale==""){
				alert("Specificare la struttura");
				return;
			}else{
				var d = new Date();
				var oggi = d.getDate()  + "/" + (d.getMonth()+1) + "/" + d.getFullYear();
				if(dataPubblicazione > oggi){
					alert("la data inserita non può essere precedente a quella attuale");
					return;
				}
			}
			var temaRegionale = getValue("temaRegionale");
			if(temaRegionale== null || temaRegionale==""){
				alert("Specificare il tema");
				return;
			}
			var tipologiaBando = getValue("tipologiaBando");
			if(tipologiaBando== null || tipologiaBando==""){
				alert("Specificare il tipo procedura");
				return;
			}
			var lavoriPubblici = getValue("lavoriPubblici");
			if(lavoriPubblici== null || lavoriPubblici==""){
				alert("Specificare se fa parte di lavori pubblici");
				return;
			}
			<c:if test="${ empty urlDeliberaPortaleAppalti or urlDeliberaPortaleAppalti eq ''}">
				alert("Url delle delibere del portale non valorizzato");
				return;
			</c:if>
		</c:if>
		
			document.forms[0].jspPathTo.value="gare/commons/popupPubblicaIntegrazioni.jsp";
			schedaConferma();
		}

		$(document).ready(function(){
			const sezLavPubTORN = '${sezLavPubTORN}';
			if (sezLavPubTORN != "") {
				$("#lavoriPubblici").val(sezLavPubTORN);
				$("#lavoriPubblici").attr('disabled',true);
				$("#lavoriPubblici").css('background','#ececec');
			}
		});

	</gene:javaScript>	

</gene:template>
</div>

	</c:otherwise>
</c:choose>