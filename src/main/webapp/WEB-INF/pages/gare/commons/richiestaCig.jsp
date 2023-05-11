<%/*
       * Created on 09-mar-2015
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test="${!empty param.key1}">
		<c:set var="key1" value="${param.key1}"/>
	</c:when>
	<c:otherwise>
		<c:set var="key1" value="${key1}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.genereGara}">
		<c:set var="genereGara" value="${param.genereGara}"/>
	</c:when>
	<c:otherwise>
		<c:set var="genereGara" value="${genereGara}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.ngara}">
		<c:set var="ngara" value="${param.ngara}"/>
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.codiceGara}">
		<c:set var="codiceGara" value="${param.codiceGara}"/>
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value="${codiceGara}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.idStipula}">
		<c:set var="idStipula" value="${param.idStipula}"/>
	</c:when>
	<c:otherwise>
		<c:set var="idStipula" value="${idStipula}"/>
	</c:otherwise>
</c:choose>

<c:set var="esisteCigAssECanc" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteCigAssECancFunction", pageContext, codiceGara, genereGara)}'/>

<gene:template file="scheda-template.jsp" gestisciProtezioni="false" >
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="head" >
				
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easytabs.js"></script>
		
		<style type="text/css">
		.dataTables_filter {
	     	display: none;
		}
		
		.dataTables_length {
			padding-top: 5px;
			padding-bottom: 5px;
		}
		
		.dataTables_length label {
			vertical-align: bottom;
		}
		
		.dataTables_paginate {
			padding-bottom: 5px;
		}

	</style>
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Richiesta CIG" />
	<c:set var="tmp" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.GetControlliPreliminariRichiestaCigFunction", pageContext, ngara, codiceGara, genereGara, idStipula)}' />
	
	
	
	<gene:redefineInsert name="addToAzioni" >
		<c:choose>
			<c:when test='${not empty erroriBloccanti}'>
			<tr>
					<td class="vocemenulaterale">
						<a href="javascript:historyBack();" title="Annulla" tabindex="1501">
							Annulla
						</a>
					</td>
				</tr>			
			
			</c:when>
			<c:when test='${empty erroriBloccanti && !empty erroriNonBloccanti}'>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:confermaStep2();" title="Conferma" tabindex="1501">
							Conferma
						</a>
					</td>
				</tr>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:historyBack();" title="Annulla" tabindex="1502">
							Annulla
						</a>
					</td>
				</tr>
			</c:when>
			<c:when test='${empty controlliPreliminari}'>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:confermaStep1();" title="Conferma" tabindex="1500">
							Conferma
						</a>
					</td>
				</tr>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:historyBack();" title="Annulla" tabindex="1501">
							Annulla
						</a>
					</td>
				</tr>
			</c:when>
			<c:otherwise>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:historyBack();" title="Annulla" tabindex="1501">
							Annulla
						</a>
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
	</gene:redefineInsert>
	
	<gene:redefineInsert name="corpo">
		<form id="richiestacigform">
		<table class="dettaglio-notab" id="richiestacigtable">			
			
		<c:set var="testoConferma" value="la creazione"/>
		<c:if test="${!empty isAnagraficaGara}">
			<c:set var="testoConferma" value="l'aggiornamento"/>
		</c:if>
			
		<c:choose>
			<c:when test='${not empty erroriBloccanti}'>
			
				<tr>
					<br>
					<br>
						<b>Non è possibile procedere con ${testoConferma} dell'anagrafica gara SIMOG per i seguenti motivi:</b>
					<ul>
					<c:forEach items="${erroriBloccanti}" step="1" var="item">
						<li>${item}
					</c:forEach>
					</ul>
					<br>
					<c:if test="${not empty erroriNonBloccanti}">
						<b>Ci sono dei dati incompleti nella gara:</b>
					</c:if>
					<ul>
					<c:forEach items="${erroriNonBloccanti}" step="1" var="item">
						<li>${item}
					</c:forEach>
					</ul>
					<br>
				</tr>
				<tr>
					<td class="comandi-dettaglio" colspan="2">
						<INPUT type="button" id="annullaS1" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:historyBack();"/>
						&nbsp;
					</td>
				</tr>
			</c:when>

			<c:when test='${empty erroriBloccanti && !empty erroriNonBloccanti}'>
			
			
				<tr>
					<br>
					<br>
					<c:if test="${not empty erroriNonBloccanti}">
						<b>Ci sono dei dati incompleti nella gara:</b>
					</c:if>
					<ul>
					<c:forEach items="${erroriNonBloccanti}" step="1" var="item">
						<li>${item}
					</c:forEach>
					</ul>
					<br>Si vuole procedere ugualmente con ${testoConferma} dell'anagrafica gara SIMOG?
					<br><br>
				</tr>
				<tr>
					<td class="comandi-dettaglio" colspan="2">
						<INPUT type="button" id="confermaS2" class="bottone-azione" value="Conferma" title="Conferma"  onclick="javascript:confermaStep2();"/>
						<INPUT type="button" id="annullaS2" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:historyBack();"/>
						&nbsp;
					</td>
				</tr>
			</c:when>
			
			<c:when test='${empty controlliPreliminari}'>
			<tr>
				<td colspan="2"> 
					<div id="tiporichSelez" style="display:block;">
						<br>
						<b>Tipo di richiesta:</b>
						<br>
						&nbsp;<input type="radio" name="tiporichiesta" id="CIG" value="CIG" />&nbsp;creazione anagrafica gara SIMOG finalizzata alla richiesta <b>CIG</b>
						<br>
						&nbsp;<input type="radio" name="tiporichiesta" id="SCIG" value="SCIG"/>&nbsp;creazione anagrafica gara SIMOG finalizzata alla richiesta <b>SMARTCIG</b>
						<br>
					</div>
					<br>
					<c:if test='${!empty isAnagraficaGara}'>
						<div id="tipoopSelez" style="display:block;">
							<br>
							<b>Modalità di accesso all'anagrafica SIMOG:</b>
							<br>
							&nbsp;<input id="UPD" type="radio" name="tipooperazione" value="UPD" checked/>&nbsp;accedi ad anagrafica gara SIMOG e aggiorna i dati con quelli della gara corrente
							<br>
							&nbsp;<input id="NOUPD" type="radio" name="tipooperazione" value="NOUPD"/>&nbsp;accedi ad anagrafica gara SIMOG senza aggiornare i dati
							<br>
							<c:if test='${!empty isAnagraficaGara && empty idGara}'>
								&nbsp;<input id="DEL" type="radio" name="tipooperazione" value="DEL"  />&nbsp;elimina anagrafica gara SIMOG
								<br>
							</c:if>
						</div>
						<br>
					</c:if>
					<c:if test="${esisteCigAssECanc}"><br><span style="float:left"><a id="aLinkVisualizzaDettaglioCigAssECanc" href="javascript:showDettCigAssECanc();" class="link-generico">Visualizza CIG assegnati alla gara e successivamente cancellati</a></span><br><br></c:if></td>
				</td>
			</tr>				
				
			<c:if test='${esisteCigAssECanc}'>
				<tr id="rigatabellaCigAssECanc">
					<td colspan="2">
						<table id="tabellaCigAssECanc" class="griglia" >
							
							
						</table>
						<br>
					<td>
				<tr>
			</c:if>	
				
			<tr>
				<td class="comandi-dettaglio" colspan="2">
					<INPUT type="button" id="confermaS1" class="bottone-azione" value="Conferma" title="Conferma"  onclick="javascript:confermaStep1();"/>
					<INPUT type="button" id="annullaS1" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:historyBack();"/>
					&nbsp;
				</td>
			</tr>
			
					
			</c:when>
			<c:otherwise>
				<tr>
					<br>
					<br>
					<c:if test="${not empty controlliPreliminari}">
						<b>La richiesta non può essere svolta per i seguenti motivi:</b>
					</c:if>
					<ul>
					<c:forEach items="${controlliPreliminari}" step="1" var="item">
						<li>${item}
					</c:forEach>
					</ul>
				</tr>
			<tr>
				<td class="comandi-dettaglio" colspan="2">
					<INPUT type="button" id="annullaS1" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:historyBack();"/>
					&nbsp;
				</td>
			</tr>
			</c:otherwise>
		</c:choose>
			
			
			
						
			
		</table>
		
				 
		
		</form>
		
		
	<form name="formPopolaAnagrafica" id="formPopolaAnagrafica" action="${pageContext.request.contextPath}/pg/PopolaAnagraficaSimog.do" method="post">
		<input type="hidden" id="genereGara" name="genereGara" value="${genereGara}" />
		<input type="hidden" id="codiceGara" name="codiceGara" value="${codiceGara}" />
		<input type="hidden" id="tiporichiesta" name="tiporichiesta" value="${tiporichiesta}" />
		<input type="hidden" id="tipooperazione" name="tipooperazione" value="${tipooperazione}" />
		<input type="hidden" id="modalita" name="modalita" value="${tiporichiesta}" />
		<input type="hidden" id="idStipula" name="idStipula" value="${idStipula}" />
	</form>

		
			
		<gene:redefineInsert name="documentiAssociati"></gene:redefineInsert> 
		<gene:redefineInsert name="noteAvvisi"></gene:redefineInsert>
		<gene:redefineInsert name="helpPagina" ></gene:redefineInsert>



	</gene:redefineInsert>
<gene:javaScript>
$(document).ready(function() {

	$("#alinkIndietro").parent().remove();
	
<c:choose>
	<c:when test='${!empty isAnagraficaGara}'>
		<c:choose>
			<c:when test='${isAnagraficaGara eq "G"}'>
				$("#CIG").prop('disabled', true);
				$("#SCIG").prop('disabled',true);
				$("#CIG").prop('checked', true);
			</c:when>
			<c:when test='${isAnagraficaGara eq "S"}'>
				$("#CIG").prop('disabled', true);
				$("#SCIG").prop('disabled',true);
				$("#SCIG").prop('checked',true);
			</c:when>
			<c:otherwise>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:if test='${isSmartCig eq "false"}'>
			$("#CIG").prop('checked', true);
			$("#SCIG").prop('disabled',true);
		</c:if>
	</c:otherwise>
</c:choose>

			<c:if test='${isgaraanacocodcig eq true}'>
				$("#UPD").prop('checked', false);
				$("#NOUPD").prop('checked', true);
			</c:if>	

			<c:if test='${isAnagraficaModificabile eq false}'>
				$("#UPD").prop('disabled', true);
				$("#NOUPD").prop('disabled',true);
				$("#UPD").prop('checked', false);
				$("#NOUPD").prop('checked', true);
			</c:if>
		
	
});


function confermaStep1(){
		if($("#CIG").prop('checked')){
			$("#tiporichiesta").val('Cig');
		}else{
			if($("#SCIG").prop('checked')){
				$("#tiporichiesta").val('Scig');
			}else{
				alert('Selezionare il tipo di richiesta');
				return(-1);				
			}
		}
		
		if($("#DEL").prop('checked')){
			var msgConferma = "Confermi l'eliminazione della anagrafica gara SIMOG?";
			if(confirm(msgConferma)){
				$("#tipooperazione").val('DEL');
				bloccaRichiesteServer();
				document.formPopolaAnagrafica.submit();
			}else{
				return(-1);
			}		
		}else{
			if($("#UPD").prop('checked')){
				$("#tipooperazione").val('UPD');
			}else{
				if($("#NOUPD").prop('checked')){
					$("#tipooperazione").val('NOUPD');
				}
			}
			bloccaRichiesteServer();
			document.formPopolaAnagrafica.submit();
		}				
		
}


function confermaStep2(){
		$("#modalita").val('COMPLETA');
		bloccaRichiesteServer();
		document.formPopolaAnagrafica.submit();
}


$("#rigatabellaCigAssECanc").hide();
$("#tabellaCigAssECanc").hide();

storicoCigAssECanc = false;

function showDettCigAssECanc(){
		var codgar="${codiceGara}";
		var contextPath = "${contextPath}";
		var genere= "${genereGara}";
		var tipo = 1;
		if(storicoCigAssECanc==false){
			caricamentoStoricoCigAssECanc(codgar, genere, contextPath);
			storicoCigAssECanc=true;
		}
		if ($('#tabellaCigAssECanc').is(':visible')) {  
			$('#tabellaCigAssECanc').hide();
			$("#rigatabellaCigAssECanc").hide();
			$('#aLinkVisualizzaDettaglioCigAssECanc').text('Visualizza CIG assegnati alla gara e successivamente cancellati');
		}else{
			$('#aLinkVisualizzaDettaglioCigAssECanc').text('Nascondi CIG assegnati alla gara e successivamente cancellati');
			$('#tabellaCigAssECanc').show();
			$("#rigatabellaCigAssECanc").show();
		}
}

/*
 * Genera lo storico dei Cig assegnati e cancellati
 * @param {string} codgar codice della gara
 * @param {string} genere gara
 * @returns {undefined}
 */
function caricamentoStoricoCigAssECanc(codgar, genere, contextPath) {
         $.ajax({
             type: "GET",
             dataType: "json",
             async: false,
             beforeSend: function(x) {
 				if(x && x.overrideMimeType) {
     				x.overrideMimeType("application/json;charset=UTF-8");
		    	}
			 },
             url: contextPath + "/pg/GetCigAssegnatiCancellati.do",
             data: "codgar=" + codgar + "&genere=" + genere,
             success: function(data){
             	if (data && data.length > 0) {
					var _intestazione='<tr style="BACKGROUND-COLOR: #EFEFEF;">';
					_intestazione+='<td colspan="2" class="titolo-valore-dato">Numero gara ANAC</td>';
					_intestazione+='<td colspan="2" class="titolo-valore-dato">CIG</td>';
					_intestazione+='<td colspan="2" class="titolo-valore-dato">Data assegnazione</td>';
					_intestazione+='<td colspan="2" class="titolo-valore-dato">Data cancellazione</td>';
					_intestazione+='<td colspan="2" class="titolo-valore-dato">Motivo cancellazione</td>';
					_intestazione+='<td colspan="2" class="titolo-valore-dato">Oggetto</td>';
					if(genere != "2"){
						_intestazione+='<td colspan="2" class="titolo-valore-dato">N.Lotto</td>';
					}
					_intestazione+='</tr>';
             		$('#tabellaCigAssECanc').append(_intestazione);
             		
             		$.map( data, function( item ) {
             			var _riga = '<tr >';
						// Numero gara 0
						var numero_gara = item[0];
	             		if(numero_gara==null)
	             			numero_gara ="";
	             		_riga += '<td colspan="2" class="valore-dato">' + numero_gara + '</td>';
						
						// CIG 1
						var cig = item[1];
	             		if(cig==null)
	             			cig ="";
	             		_riga += '<td colspan="2" class="valore-dato">' + cig + '</td>';
						
						// Data assegnazione 2
						var data_assegnazione = item[2];
	             		if(data_assegnazione==null)
	             			data_assegnazione ="";
	             		_riga += '<td colspan="2" class="valore-dato">' + data_assegnazione + '</td>';
						
						// Data cancellazione 3
						var data_cancellazione = item[3];
	             		if(data_cancellazione==null)
	             			data_cancellazione ="";
	             		_riga += '<td colspan="2" class="valore-dato">' + data_cancellazione + '</td>';
						
						// Motivo cancellazione 4
						var motivo_cancellazione = item[4];
	             		if(motivo_cancellazione==null)
	             			motivo_cancellazione ="";
	             		_riga += '<td colspan="2" class="valore-dato">' + motivo_cancellazione + '</td>';
						
						// Oggetto 5
						var oggetto = item[5];
	             		if(oggetto==null)
	             			oggetto ="";
	             		_riga += '<td colspan="2" class="valore-dato">' + oggetto + '</td>';
						
						if(genere != 2){
							// N.Lotto
							var n_lotto = item[6];
	             			if(n_lotto==null)
	             				n_lotto ="";
	             			_riga += '<td colspan="2" class="valore-dato">' + n_lotto + '</td>';
						}
								             			
             			_riga += '</tr>';
             			$('#tabellaCigAssECanc').append(_riga);
             				
             		});
             	}
            	
             },
             error: function(e){
                 alert("Errore nella lettura dello storico delle richieste CIG annullate");
             }
         });
     }

</gene:javaScript>	

</gene:template>
