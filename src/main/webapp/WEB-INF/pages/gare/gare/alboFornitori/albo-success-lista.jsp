
<%
	/*
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

<script src="https://cdn.jsdelivr.net/npm/exceljs/dist/exceljs.min.js"></script>


<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<c:set var="integrazioneWSERP" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSERPFunction", pageContext)}' />
<c:set var="tipoWSERP" value='${requestScope.tipoWSERP}' />

<c:set var="descrizioneEProc" value="Descrizione" />
<c:if test="${tipoWSERP eq 'RAIWAY'}">
	<c:set var="descrizioneEProc" value="Descrizione eProc" />
</c:if>
<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.numeroFaseAttiva}'>
		<c:set var="numeroFaseAttiva" value="${param.numeroFaseAttiva}" />
	</c:when>
	<c:otherwise>
		<c:set var="numeroFaseAttiva" value="${numeroFaseAttiva}" />
	</c:otherwise>
</c:choose>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="ALBO-lista" >

	<gene:setString name="titoloMaschera" value='Lista fornitori albo'/>
	<input id="listaCategorie" type="hidden" value="'${risultatoListaFornitori}'"/>
	<gene:redefineInsert name="addToAzioni">
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:exportToExcel();" title="Esporta in file Excel" tabindex="1500">Esporta in Excel
				</a>
			</td>
		</tr>
	</gene:redefineInsert>
	
	<gene:redefineInsert name="addHistory">
		<gene:historyAdd titolo="Lista fornitori albo" id="Lista fornitori albo" />
	</gene:redefineInsert>
	
	<gene:redefineInsert name="corpo">
		<table class="lista" id="data-table-albo">
			<tr>
				<td>
					<gene:formLista  
						tableclass="datilista" pagesize="1000"
						gestisciProtezioni="false"
						sortColumn="0"
						varName="risultatoListaFornitori">
						
						<gene:campoLista ordinabile="false" title="Codice Maggioli" campo="C01" definizione="T10;0" />
						<c:choose>
						    <c:when test="${datiRiga.OBJ1 eq 'Non disponibile'}">
								<c:set var="visualizzaLink" value='false'/>						    
						    </c:when>
						    <c:otherwise>
						        <c:set var="visualizzaLink" value='true'/>
						    </c:otherwise>
						</c:choose>
						<gene:campoLista ordinabile="false" title="Codice SAP" campo="C02" definizione="T4;0" />
						<gene:campoLista ordinabile="false" title="${descrizioneEProc}" campo="C03" definizione="T15;0" />
						<c:if test="${tipoWSERP eq 'RAIWAY'}">
							<gene:campoLista ordinabile="false" title="Descrizione SAP" campo="C10" definizione="T15;0" />
						</c:if>
						<gene:campoLista ordinabile="false" title="Codice Fiscale" campo="C04" definizione="T30;0" />
						<gene:campoLista ordinabile="false" title="Partita IVA" campo="C05" definizione="T30;0" />
						<gene:campoLista ordinabile="false" title="ECC" campo="C06" definizione="T16;0" />							
						<gene:campoLista ordinabile="false" title="Comune" campo="C07" definizione="T16;0" />
						<gene:campoLista ordinabile="false" title="Provincia" campo="C08" definizione="T16;0" />
						<c:if test="${tipoWSERP eq 'RAIWAY'}">
							<gene:campoLista ordinabile="false" title="PEC" campo="C09" definizione="T10;0" />
							<gene:campoLista ordinabile="false" title="Presenza in Raiway" definizione="T10;0">
								<c:if test="${datiRiga.OBJ11 eq '1'}">
								    <img width="16" height="16" title="${datiRiga.OBJ12}" alt="" src="${pageContext.request.contextPath}/img/ditta_acquisita_noncompleta.png"/>
							    </c:if>
								<c:if test="${datiRiga.OBJ11 eq '2'}">
								    <img width="16" height="16" title="${datiRiga.OBJ12}" alt="" src="${pageContext.request.contextPath}/img/ditta_acquisita.png"/>
							    </c:if>
							</gene:campoLista>
							<gene:campoLista visibile = "false" title="checkVisible" campo="C13" definizione="T10;0" />
						</c:if>
					</gene:formLista>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>

	<gene:javaScript>
		
		$(document).ready(function() {
			$('#menulaterale table tbody tr:nth-child(2) td, #menulaterale table tbody tr:nth-child(3) td').hide();
			
			// Tolgo gli apici dalla stringa
			$("#listaCategorie").val($("#listaCategorie").val().substring(1, $("#listaCategorie").val().length - 1));
			
		});
		
		function chiudi(){
			historyVaiIndietroDi(1);
		}

		function exportToExcel() {
			var dati = $("#listaCategorie").val();
			
			// Rimuovi gli spazi tra le virgole
			var jsonData = dati.replace(/, /g, ",");

			// Aggiungi doppi apici ad ogni campo e correggo la formattazione
			jsonData = jsonData.replaceAll(",", '","');
			jsonData = jsonData.replaceAll(']","[', '"],["');
			jsonData = jsonData.replace("[[", '[["');
			jsonData = jsonData.replace("]]", '"]]');

			var dataArray = JSON.parse(jsonData);

			// riordino le colonne per la corretta visualizzazione
			moveColumn(dataArray, 9, 3);
			
			jsonData = JSON.stringify(dataArray);

			//Intestazione
			var header = ['Codice Maggioli', 'Codice SAP', 'Descrizione eProc', 'Descrizione SAP', 'Codice Fiscale', 'Partita IVA', 'ECC', 'Comune', 'Provincia', 'PEC', 'Presenza in Raiway', 'Codice elenco', 'Presenza in Raiway testo'];

			// Conversione dell'intestazione in una stringa JSON
			var headerJson = JSON.stringify(header);

			// Aggiunta dell'intestazione alla stringa
			jsonData = '[' + headerJson + ',' + jsonData.substring(1);

			// Nascondo la colonna che non voglio visualizzare nel file excel
			var columnsToRemove = [12];

			// Conversione dei dati JSON in un array di array
			var dataArray = JSON.parse(jsonData);

			// Rimozione delle colonne specificate
			var newDataArray = removeColumns(dataArray, columnsToRemove);

			// Conversione del nuovo array di array in JSON
			var newJsonData = JSON.stringify(newDataArray);
			
			// Creo il foglio excel
			var workbook = new ExcelJS.Workbook();
			var worksheet = workbook.addWorksheet('Sheet1');
			
			// Aggiungo tutti i dati
			worksheet.addRows(newDataArray);
			
			// controllo la colonna 11 che contiene i codici per la visualizzazione dell'icona "bandierina"
			// e codifico il codice in un messaggio coerente al codice
			worksheet.getColumn(11).eachCell(function (cell) {
				if(cell.value && cell.value.toString().length > 0){
					if(cell.value == 0){
						cell.value = "Non presente";
					} else if(cell.value == 1){
						cell.value = "Presente ma non registrato sul portale";
					} else if(cell.value == 2){
						cell.value = "Registrato sul portale";
					}
				}
			});
			
			// controllo la colonna 12 che contiene "Codice elenco" per formattarla correttamente
			worksheet.getColumn(12).eachCell(function (cell) {
				if(cell.value && cell.value.toString().length > 0){
					cell.value = cell.value.replaceAll(';',',');
					cell.value = cell.value.replaceAll('$','');
				}
			});
			
			// Aggiungo la colorazione dell'intestazione e la formattazione
			worksheet.getRow(1).eachCell(function (cell) {
				cell.fill = {
					type: 'pattern',
					pattern: 'solid',
					fgColor: { argb: 'A7BFD9' } // Imposta il colore di riempimento
				};
				cell.font = {
					bold: true, // Rende il testo in grassetto
					name: 'Calibri' // Imposta lo stile del carattere a "Calibri"
				};
				cell.border = {
					top: {style:'thin', color: {argb:'FFFFFF'}}, 
					left: {style:'thin', color: {argb:'FFFFFF'}}, 
					bottom: {style:'thin', color: {argb:'FFFFFF'}}, 
					right: {style:'thin', color: {argb:'FFFFFF'}} 
				};
			});

			// Aggiungo la colorazione dell'intestazione e la formattazione escludendo la prima riga che sarebbe l'intestazione
			worksheet.eachRow(function (row, rowNumber) {
				if (rowNumber !== 1) {
					if (rowNumber % 2 === 0) {
						row.eachCell(function (cell) {
							cell.fill = {
								type: 'pattern',
								pattern: 'solid',
								fgColor: { argb: 'E7F1FF' } // Imposta il colore di riempimento
							};
							cell.font = {
								name: 'Calibri' // Imposta lo stile del carattere a "Calibri"
							};
							cell.border = {
								top: {style:'thin', color: {argb:'FFFFFF'}},
								left: {style:'thin', color: {argb:'FFFFFF'}},
								bottom: {style:'thin', color: {argb:'FFFFFF'}},
								right: {style:'thin', color: {argb:'FFFFFF'}}
							};
						});
					} else {
						row.eachCell(function (cell) {
							cell.fill = {
								type: 'pattern',
								pattern: 'solid',
								fgColor: { argb: 'CEDAEB' } // Imposta il colore di riempimento
							};
							cell.font = {
								name: 'Calibri' // Imposta lo stile del carattere a "Calibri"
							};
							cell.border = {
								top: {style:'thin', color: {argb:'FFFFFF'}},
								left: {style:'thin', color: {argb:'FFFFFF'}},
								bottom: {style:'thin', color: {argb:'FFFFFF'}},
								right: {style:'thin', color: {argb:'FFFFFF'}}
							};
						});
					}
				}
			});
			
			// Imposto la larghezza di ogni colonna basandomi sul contenuto di ogni colonna
			worksheet.columns.forEach(function(column) {
				var maxLength = 0;
				column.eachCell(function(cell) {
					maxLength = Math.max(maxLength, cell.value ? cell.value.toString().length : 0);
				});
				column.width = maxLength < 10 ? 10 : maxLength + 2; // Imposta la larghezza minima della colonna a 10
			});

			
			workbook.xlsx.writeBuffer().then(function(buffer) {
				var blob = new Blob([buffer], {type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'});
				var link = document.createElement('a');
				link.href = window.URL.createObjectURL(blob);
				link.download = 'exportAlboFornitori.xlsx';
				link.click();
			});
		}
		
		function removeColumns(data, columnsToRemove) {
			return data.map(function(row) {
				return row.filter(function(_, index) {
					return !columnsToRemove.includes(index);
				});
			});
		}
		
		// Funzione per spostare una colonna in una nuova posizione
		function moveColumn(data, fromIndex, toIndex) {
			// Controllo per assicurarsi che gli indici siano validi
			if (fromIndex < 0 || fromIndex >= data[0].length || toIndex < 0 || toIndex >= data[0].length) {
				console.error("Indici non validi.");
				return;
			}

			// Per ogni riga dell'array
			for (var i = 0; i < data.length; i++) {
				// Memorizziamo il valore della colonna che vogliamo spostare
				var columnToMove = data[i][fromIndex];

				// Rimuoviamo il valore dalla colonna originale
				data[i].splice(fromIndex, 1);

				// Inseriamo il valore nella nuova posizione
				data[i].splice(toIndex, 0, columnToMove);
			}
		}
		
	</gene:javaScript>	
	
</gene:template>
