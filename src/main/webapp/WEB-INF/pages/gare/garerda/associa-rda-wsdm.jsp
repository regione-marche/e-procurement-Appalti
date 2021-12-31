<%/*
   * Created on 16-Mar-2009
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>



<gene:template file="scheda-template.jsp">

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
</gene:redefineInsert>

<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "TECNI", "CODTEC")}'/>

<c:choose>
	<c:when test='${not empty param.tipoGara}'>
		<gene:setString name="titoloMaschera" value="Nuova gara"/>
	</c:when>
	<c:otherwise>
		<gene:setString name="titoloMaschera" value="Nuovo lotto di gara"/>
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.chiaveRiga}'>
		<c:set var="chiaveRiga" value="${param.chiaveRiga}" />
	</c:when>
	<c:otherwise>
		<c:set var="chiaveRiga" value="${chiaveRiga}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.livpro}'>
		<c:set var="livpro" value="${param.livpro}" />
	</c:when>
	<c:otherwise>
		<c:set var="livpro" value="${livpro}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.tipoAppalto}'>
		<c:set var="tipoAppalto" value="${param.tipoAppalto}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipoAppalto" value="${tipoAppalto}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.proceduraTelematica}'>
		<c:set var="proceduraTelematica" value="${param.proceduraTelematica}" />
	</c:when>
	<c:otherwise>
		<c:set var="proceduraTelematica" value="${proceduraTelematica}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.idconfi}'>
		<c:set var="idconfi" value="${param.idconfi}" />
	</c:when>
	<c:otherwise>
		<c:set var="idconfi" value="${idconfi}" />
	</c:otherwise>
</c:choose>

	<c:set var="modo" value="NUOVO" />
	<gene:redefineInsert name="documentiAzioni" />
	<gene:redefineInsert name="schedaConferma" >
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:creaNuovaGara();" title="Avanti" tabindex="1502">
					Avanti &gt;
				</a>
			</td>
		</tr>
	<c:if test='${not empty param.tipoGara}' >
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:indietro();" title="Indietro" tabindex="1503">
					&lt; Indietro
				</a>
			</td>
		</tr>
	</c:if>
	</gene:redefineInsert>
	<gene:redefineInsert name="schedaAnnulla" >
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:annullaCreazione();" title="Annulla" tabindex="1504">
					Annulla
				</a>
			</td>
		</tr>	
	</gene:redefineInsert>
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="GARE" gestisciProtezioni="true" >
			<gene:campoScheda>
				<td colspan="2">
					<br><b>Impostare il riferimento alla RdA:</b>
					<br>
				</td>
			</gene:campoScheda>
			<gene:campoScheda title="Numero RDA" campo="NUMERO_RDA" campoFittizio="true" definizione="T20" >&nbsp;
			<a href="javascript:leggiDatiRda();" id="linkleggiDatiRda" >Leggi RdA</a>
			</gene:campoScheda>

			<gene:campoScheda addTr="false" >
				<tr>
					<td class="etichetta-dato">Oggetto</td>
					<td class="valore-dato">
						<span id="oggettodocumento"></span>
					</td>						
				</tr>
			</gene:campoScheda>
			
			<gene:campoScheda addTr="false" >
				<tr>
					<td class="etichetta-dato">Tipo documento</td>
					<td class="valore-dato">
						<span id="tipodocumentodescrizione"></span>
					</td>						
				</tr>
			</gene:campoScheda>
			
			<gene:campoScheda addTr="false" >
				<tr>
					<td class="etichetta-dato">Anno fascicolo</td>
					<td class="valore-dato">
						<span id="annofascicolo"></span>
					</td>						
				</tr>
			</gene:campoScheda>
			
			<gene:campoScheda addTr="false" >
				<tr>
					<td class="etichetta-dato">Numero fascicolo</td>
					<td class="valore-dato">
						<span id="numerofascicolo"></span>
					</td>						
				</tr>
			</gene:campoScheda>

			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
		      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaCreazione();">&nbsp;&nbsp;&nbsp;&nbsp;
		   	<c:if test='${not empty param.tipoGara}' >
		     	<INPUT type="button" class="bottone-azione" value="&lt; Indietro" title="Indietro" onclick="javascript:indietro();">&nbsp;
		    </c:if>
		      <INPUT type="button" class="bottone-azione" value="Avanti &gt;" title="Avanti" onclick="javascript:creaNuovaGara();">&nbsp;
				</td>
			</gene:campoScheda>
			
			<table class="dettaglio-notab" id="datiLogin">
				<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp"/>
			</table>

			<input type="hidden" name="step" id="step" value="${step}" />
			<input type="hidden" id="servizio"  value="DOCUMENTALE" />
			<input type="hidden" id="syscon" value="${profiloUtente.id}" /> 
			<input type="hidden" id="tiposistemaremoto" value="" />
			<input id="tabellatiInDB" type="hidden" value="" />
			<input type="hidden" id="entita"  value="GARE" /> 
			<input type="hidden" id="idprg"  value="PG" />
			<input type="hidden" id="key1"  name="key1" value="${ngara }" />
			<input type="hidden" id="key2"  name="key2" value="" /> 
			<input type="hidden" id="key3"  name="key3" value="" /> 
			<input type="hidden" id="key4"  name="key4" value="" /> 
			<input type="hidden" id="chiaveOriginale" value="${ngara }" />
			<input id="abilitazioneGare" type="hidden" value="${profiloUtente.abilitazioneGare}" />
			<input type="hidden" id="idconfi" value="${idconfi}" />
			<input type="hidden" id="tipoDatiPersonalizzati"  name="tipoDatiPersonalizzati" value="" />
			
			<input type="hidden" name="codlav" value="${codlav}" />
			<input type="hidden" name="nappal" value="${nappal}" />
			<input type="hidden" name="chiaveRiga" value="${chiaveRiga}" />
		</gene:formScheda>
	</gene:redefineInsert>

<gene:javaScript>

		/*
	     * Gestione utente ed attributi per il collegamento remoto
	     */
		_getWSTipoSistemaRemoto();
		_popolaTabellato("ruolo","ruolo");
		_getWSLogin();
		_gestioneWSLogin();
		
		$("#datiLogin").hide();
		
		associaFunzioniEventoOnchange();
		
		function associaFunzioniEventoOnchange(){
			document.getElementById("NUMERO_RDA").onchange = sbiancaCampo;
		}
		
		function sbiancaCampo(){
			$("#oggettodocumento").text("");
			$("#tipodocumentodescrizione").text("");
			$("#annofascicolo").text("");
			$("#numerofascicolo").text("");
		}
		

		function leggiDatiRda(){
			var nrda = getValue("NUMERO_RDA");
			if(nrda == ""){
				alert('Valorizzare il numero RdA per la lettura!')
			}else{
				readRda(nrda);
			}
		
		}

		function readRda(nrda) {
			_getWSDMDocumento(nrda,function(esito){
				if(esito==false){
				   alert('RdA non trovata!');
				}
			});
		}

		function annullaCreazione(){
			bloccaRichiesteServer();
			historyBack();
		}

		function indietro(){
			bloccaRichiesteServer();
			document.location.href = "${pageContext.request.contextPath}/pg/InitNuovaGara.do?" + csrfToken;
		}

		function creaNuovaGara(){
			if(getValue("NUMERO_RDA") != ""){
				var nrda = getValue("NUMERO_RDA");
				
				var oggetto = $("#oggettodocumento").text();
				if(oggetto == ""){
					alert('Effettuare la lettura della Rda!')
					return -1;
				}
				
				var codificaAutomatica = "${isCodificaAutomatica}";
				if(codificaAutomatica == "false"){
					alert("Per procedere alla creazione della gara deve essere prima attivata la codifica automatica per l'archivio dei tecnici progettisti");
					return -1;
				}
				
				var tdp = $("#tipoDatiPersonalizzati").val();
				if(tdp != "RDA"){
					alert('Elemento documentale non di tipo Rda!');
					return -1;
				}else{
					//verifico presenza rda in banca dati
					if (_verificaPresenzaRda(nrda) == true) {
						alert('La Rda risulta utilizzata in precedenza!');
						return -1;
					}
				}
			}
			
			
			
			var tipoAppalto = ${param.tipoAppalto};
			<c:choose>
			<c:when test='${param.tipoGara=="garaDivisaLottiOffUnica"}'>
				document.forms[0].activePage.value = 0;
				document.forms[0].jspPath.value="/WEB-INF/pages/gare/torn/torn-scheda.jsp";
				document.forms[0].jspPathTo.value="/WEB-INF/pages/gare/torn/torn-scheda.jsp";
			</c:when>
			<c:otherwise>
				document.forms[0].activePage.value = 0;
				document.forms[0].jspPath.value="/WEB-INF/pages/gare/gare/gare-scheda.jsp";
				document.forms[0].jspPathTo.value="/WEB-INF/pages/gare/gare/gare-scheda.jsp";
			</c:otherwise>
			</c:choose>

			if(tipoAppalto == '1'){
				document.forms[0].action+="&tipoAppalto=${param.tipoAppalto}&tipoGara=${param.tipoGara}&proceduraTelematica=${param.proceduraTelematica}&modalitaPresentazione=${param.modalitaPresentazione}&chiaveRiga=${param.chiaveRiga}&livpro=${param.livpro}&numeroRda=" + getValue("NUMERO_RDA")+"&integrazioneERPvsWSDM=1";
			}else
				document.forms[0].action+="&tipoAppalto=${param.tipoAppalto}&tipoGara=${param.tipoGara}&proceduraTelematica=${param.proceduraTelematica}&modalitaPresentazione=${param.modalitaPresentazione}&numeroRda=" + getValue("NUMERO_RDA")+"&integrazioneERPvsWSDM=1";
			
			document.forms[0].action+="&idconfi=" + ${idconfi};
			bloccaRichiesteServer();
			document.forms[0].submit();
		}
		
		
		function _verificaPresenzaRda(numeroRda) {
			var _pres = false;
			$.ajax({
				type: "POST",
				dataType: "json",
				async: false,
				timeout: 3000,
				beforeSend: function(x) {
					if(x && x.overrideMimeType) {
						x.overrideMimeType("application/json;charset=UTF-8");
					}
				},
				url: "pg/GetWSERPPresenzaRda.do",
				data: "numeroRda=" + numeroRda, 
				success: function(data){
					if (data == true) {
						_pres = true;
					} 
				}
			});
			return _pres;
		}
		
		
		

	<c:if test='${not empty param.chiavePadre}'>
		document.forms[0].keyParent.value = ${param.chiavePadre};
	</c:if>

	</gene:javaScript>
</gene:template>