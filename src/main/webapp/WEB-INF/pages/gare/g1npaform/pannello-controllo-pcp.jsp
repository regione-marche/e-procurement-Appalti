<%
	/*
	 * Created on 20-Ott-2008
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:template file="scheda-template.jsp">

	<gene:setString name="titoloMaschera" value="Pannello di supporto operativo ANAC - Procedura ${param.codiceGara} - Scheda ${param.nomeScheda}" />

	<gene:redefineInsert name="head" >
		<script type="text/javascript" src="${contextPath}/js/jquery.json-editor.min.js"></script>
		<script>
			$(document).ready(function () {
				$("#statonpa option").hide();
				var stati = "${param.statiPossibili}";
				stati.split(";").map((stato) =>
						$("#statonpa option[value='" + stato + "']").show());

				var form = document.getElementById("formRestClient");
				form?.addEventListener("submit", eseguiOperazione);
			});

			function handleDropdownChange() {
				var selectedValue = document.getElementById('statonpa').value;
				var submitButton = document.getElementById('submitButton');
				document.getElementById('additionalInputs').innerHTML = '';

				if (selectedValue === '') {
					submitButton.disabled = true;
				} else {
					submitButton.disabled = false;
				}

				switch (selectedValue) {
					case '10':
					<c:if test="${param.tipoScheda eq 'appalto'}">
						document.getElementById('additionalInputs').innerHTML = `
							<label for="idAppalto">ID appalto (facoltativo):</label>
							<input type="text" id="idAppalto" name="idAppalto"><br>
							<label for="coddrp">Trasmissione schede in carico a (facoltativo):</label>
							<input type="text" id="coddrp" name="coddrp"><br>
						`;
					</c:if>
					<c:if test="${param.tipoScheda eq 'pianificazione'}">
						document.getElementById('additionalInputs').innerHTML = `
							<label for="idPianificazione">ID pianificazione (facoltativo):</label>
							<input type="text" id="idPianificazione" name="idPianificazione"><br>
							<label for="coddrp">Trasmissione schede in carico a:</label>
							<input type="text" id="coddrp" name="coddrp"><br>
						`;
					</c:if>
					<c:if test="${param.tipoScheda eq 'scheda'}">
						document.getElementById('additionalInputs').innerHTML = `
							<label for="idScheda">ID scheda (facoltativo):</label>
							<input type="text" id="idScheda" name="idScheda"><br>
						`;
					</c:if>
						break;
					case '30':
						document.getElementById('additionalInputs').innerHTML = `
                        <label for="dataconf">Data Conferma (facoltativo):</label>
                        <input type="date" id="dataconf" name="dataconf"><br>
                    `;
						break;
					case '32':
						document.getElementById('additionalInputs').innerHTML = `
                        <label for="idAvviso">ID avviso (facoltativo):</label>
                        <input type="text" id="idAvviso" name="idAvviso"><br>
                    `;
						break;
					default:
						// No additional inputs for other values
						break;
				}
			}

			function handleDropdownChangeRestClient() {
				var selectedValue = document.getElementById('operazione').value;
				var submitButton = document.getElementById('submitButtonRestClient');
				document.getElementById('additionalInputsRestClient').innerHTML = '';

				if (selectedValue === '') {
					submitButton.disabled = true;
				} else {
					submitButton.disabled = false;
				}

				switch (selectedValue) {
					case 'ESITO_OPERAZIONE':
						document.getElementById('additionalInputsRestClient').innerHTML = `
							<label for="tipoOperazione">Tipo operazione:</label>
							<select id="tipoOperazione" name="tipoOperazione">
								<option value="AP_VERIF">(AP_VERIF) Esito verifica appalto</option>
								<option value="AP_CONF">(AP_CONF) Esito conferma appalto</option>
								<option value="SC_VERIF">(SC_VERIF) Esito verifica scheda</option>
								<option value="SC_CONF">(SC_CONF) Esito conferma scheda</option>
								<option value="PI_VERIF">(PI_VERIF) Esito verifica piano</option>
								<option value="PI_CONF">(PI_CONF) Esito conferma piano</option>
								<option value="AV_PUBB">(AV_PUBB) Esito pubblicazione avviso</option>
								<option value="AV_RETT">(AV_RETT) Esito pubblicazione rettifica</option>
							</select><br>
							<label for="tipoRicerca">Tipo ricerca:</label>
							<select id="tipoRicerca" name="tipoRicerca">
								<option value="ULTIMO_ESITO">(ULTIMO_ESITO) Ultimo esito</option>
								<option value="TUTTI_ESITI">(TUTTI_ESITI) Tutti gli esiti</option>
							</select><br>
						`;
						break;
					default:
						// No additional inputs for other values
						break;
				}
			}
			function eseguiOperazione(event) {
				event.preventDefault()
				$("#success").hide();
				$("#json-display").show();
				new JsonEditor('#json-display',"Richiesta in corso...",{"editable":false});
				setTimeout(() => {
					$.ajax({
						type: "POST",
						dataType: "json",
						url: "${contextPath}/pg/ScambioDatiPCP.do",
						data: {
							metodo :"gestionePannelloPCP",
							codgar: "${param.codgar}",
							codiceGara: "${param.codiceGara}",
							idForm: ${param.idForm},
							servizio: document.getElementById('operazione').value,
							tipoOperazione: document.getElementById('tipoOperazione')?.value,
							tipoRicerca: document.getElementById('tipoRicerca')?.value,
							leggi: 'true'
						},
						async: false,
						success: function (res) {
							var editor = new JsonEditor('#json-display', res,{"editable":false});
							window.scrollTo(0, document.body.scrollHeight);
						}
					});
				},150);
			}
			function checkServiceUP(){
				setTimeout(() => {
					$.ajax({
						type: "GET",
						dataType: "json",
						url: "${contextPath}/pg/ScambioDatiPCP.do?metodo=getHealthStatus",
						async: false,
						success: function(json) {
							alert("Il servizio è "+json.status);
						}
					});
				},150);
			}
		</script>
		<style>
			.form-container {
				max-width: 600px;
				margin: 20px auto;
				padding: 20px;
				background: #fff;
				border-radius: 10px;
				box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
			}
			h1 {
				text-align: center;
				color: #333;
			}
			label {
				display: block;
				margin-bottom: 8px;
				color: #555;
			}
			input[type="text"],
			input[type="date"],
			select {
				width: calc(100% - 20px);
				padding: 8px;
				margin-bottom: 20px;
				border: 1px solid #ccc;
				border-radius: 5px;
				box-sizing: border-box;
			}
			input[type="submit"]{
				width: 100%;
				padding: 10px;
				background-color: #007bff;
				border: none;
				border-radius: 5px;
				color: #fff;
				font-size: 16px;
				cursor: pointer;
			}
			input[type="submit"]:disabled {
				background-color: #ccc;
				cursor: not-allowed;
			}
			input[type="submit"]:hover:enabled {
				background-color: #0056b3;
			}

			.message-box {
				padding: 1rem;
				border-radius: 3px;
				margin: 1em 0;
			}
			#errori {
				color: #721c24;
				background-color: #f8d7da;
				border: 1px solid #f5c6cb;
			}
			#success {
				color: #155724;
				background-color: #d4edda;
				border-color: #c3e6cb;
			}
			hr {
				color: #555;
				margin: 15px 10px;
			}
			h3 {
				margin-bottom: 20px;
				text-align: center;
			}
			#json-input {
				display: block;
				width: 100%;
				height: 200px;
			}
			#translate {
				display: block;
				height: 28px;
				margin: 20px 0;
				border-radius: 3px;
				border: 2px solid;
				cursor: pointer;
			}
			#json-display {
				border: 1px solid;
				margin: 20px 0 0 0;
				padding: 10px 20px;
				text-wrap: wrap;
			}

			.json-editor-blackbord {
				font-size: 12px;
				background: #fff;
				color: #1c2833;
			}

			.json-property {
				color: #b16b2a !important;
				font-weight: bold;
			}

			.json-string {
				color: green;
				font-weight: bold;
			}

			.json-url {
				color: #1e90ff;
				font-weight: bold;
			}

			.json-literal {
				font-weight: bold;
				color: #ff8c00;
			}

			.json-literal-boolean {
				font-weight: bold;
				color: #8f3b8f;
			}
		</style>

	</gene:redefineInsert>

	<gene:redefineInsert name="corpo">
		<c:set var="opzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" scope="request"/>
		<gene:redefineInsert name="addToAzioni">
			<c:if test='${fn:contains(opzioniUtenteAbilitate, "ou89#") and param.modalita ne "visualizza"}'>
				<tr>
					<td class="vocemenulaterale" >
						<a href="javascript:checkServiceUP();" title="Verifica operatività del servizio">
							Verifica operatività dei servizi ANAC
						</a>
					</td>
				</tr>
			</c:if>
		</gene:redefineInsert>
			<div class="form-container">
			<h3 class="titolomaschera">Aggiornamento stato scheda</h3>
			<c:if test="${not empty risultato and risultato ne 'OK'}">
				<div id="errori" class="message-box"><b>Operazione non eseguita.</b><br>${risultato}</div>
			</c:if>
			<c:if test="${not empty risultato and risultato eq 'OK'}">
				<div id="success" class="message-box"><b>Operazione eseguita.</b></div>
			</c:if>
			<p>Tramite il seguente form è possibile modificare lo stato della scheda e aggiornare in modo controllato alcuni dati legati alla scheda e/o alla procedura.</p>
			<p><b>Se un campo viene valorizzato, andrà a sostituire l'attuale valore registrato in database!</b><br>Lasciare vuoto per evitarne l'aggiornamento.</p>
				<p><b>ATTENZIONE: l'aggiornamento tramite questa pagina non trasmette alcun dato ad ANAC.</b></p>
			<c:if test="${empty risultato || risultato.indexOf('non dispone dei permessi necessari')<0}">
				<form action="${contextPath}/pg/ScambioDatiPCP.do" method="post">
					<input type="hidden" name="codgar" value="${param.codgar}">
					<input type="hidden" name="codiceGara" value="${param.codiceGara}">
					<input type="hidden" name="idForm" value="${param.idForm}">
					<input type="hidden" name="tipoScheda" value="${param.tipoScheda}">
					<input type="hidden" name="statiPossibili" value="${param.statiPossibili}">
					<input type="hidden" name="nomeScheda" value="${param.nomeScheda}">
					<input type="hidden" name="metodo" value="gestionePannelloPCP">
					<input type="hidden" name="update" value="true">
					<hr>
					<p>Stato attuale della scheda: <i>${requestScope.statonpaScheda}</i></p>
					<label for="statonpa">Nuovo stato (obbligatorio):</label>
					<select id="statonpa" name="statonpa" onchange="handleDropdownChange()">
						<option value=""></option>
						<option value="10">In lavorazione</option>
						<option value="12">In corso di verifica</option>
						<option value="13">Verificato negativamente</option>
						<option value="14">Verificato positivamente</option>
						<option value="15">In conferma</option>
						<option value="30">Confermato</option>
						<option value="31">Acquisizione CIG</option>
						<option value="32">Recupero id avviso</option>
						<c:if test="${param.tipoScheda eq 'appalto'}"><option value="33">Richiesta pubblicazione</option></c:if>
						<option value="40">In attesa pubblicazione</option>
						<option value="60">Pubblicato</option>
					</select><br>

					<div id="additionalInputs">
					</div>

					<input type="submit" id="submitButton" value="Aggiorna dati scheda e procedura" disabled>
				</form>
			</c:if>
		</div>
		<div class="form-container">
			<h3 class="titolomaschera">Consulta servizi ANAC</h3>
			<c:set var="authorized" value="${not empty profiloUtente.codiceFiscale && (fn:toUpperCase(requestScope.DRPProcedura) eq fn:toUpperCase(profiloUtente.codiceFiscale) || empty requestScope.DRPProcedura) && not empty accountSSO && accountSSO.hasLoa(3)}"/>
			<c:if test="${authorized eq 'false' || risultato.indexOf('non dispone dei permessi necessari')>0}">
				<div id="errori" class="message-box"><b>L'utente non dispone dei permessi necessari o non risulta impersonificato come RUP della procedura</b></div>
			</c:if>
			<p>Tramite il seguente form è possibile eseguire alcune chiamate ad ANAC (solo operazioni di lettura) previa impersonificazione come RUP della procedura
			<p><b>NOTA: Le operazioni eseguite non provocheranno alcuna modifica ai dati registrati presso ANAC o nell'applicativo.</b></p>
			<c:if test="${authorized eq 'true'}">
				<form id="formRestClient">
					<hr>
					<label for="operazione">Scegli l'operazione (obbligatorio):</label>
					<select id="operazione" name="operazione" onchange="handleDropdownChangeRestClient()">
						<option value=""></option>
						<option value="ESITO_OPERAZIONE">(esito-operazione) Esito operazione</option>
						<option value="RECUPERA_CIG">(recupera-cig) Recupera CIG</option>
						<option value="STATO_APPALTO">(stato-appalto) Stato appalto</option>
						<option value="STATO_AVVISO">(stato-avviso) Stato avviso</option>
						<option value="CONSULTA_AVVISO">(consulta-avviso) Consulta avviso</option>
						<option value="CONSULTA_SCHEDA">(consulta-scheda) Consulta scheda</option>
						<option value="CONSULTA_PIANO">(consulta-piano) Consulta piano</option>
						<c:if test="${param.tipoScheda eq 'appalto'}"><option value="RICERCA_APPALTO">(ricerca-appalto) Ricerca appalto tramite codiceAppalto</option></c:if>
					</select><br>

					<div id="additionalInputsRestClient">
					</div>

					<input type="submit" id="submitButtonRestClient" value="Esegui operazione" disabled>
				</form>
				<pre id="json-display" hidden></pre>
			</c:if>
		</div>

		<table class="ricerca">

			<tr>
				<td class="comandi-dettaglio">
					<gene:insert name="buttons">
						<INPUT type="button" class="bottone-azione" value="Indietro" title="Indietro" onclick="javascript:historyVaiIndietroDi(1)">&nbsp;
					</gene:insert>
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
	<gene:redefineInsert name="noteAvvisi"/>
	<gene:redefineInsert name="documentiAssociati"/>

</gene:template>