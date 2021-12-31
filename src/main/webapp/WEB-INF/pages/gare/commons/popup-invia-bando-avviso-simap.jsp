
<%
  /*
			 * Created on 15-lug-2008
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

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<c:set var="settore" value="${param.settore}"/>

<div style="width: 97%;"><gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:setString name="titoloMaschera" value='Invio dei dati a Formulari Europei per la creazione dei formulari GUUE' />

	<gene:redefineInsert name="corpo">
	
		<form action="${contextPath}/pg/InviaBandoAvvisoSimap.do" method="post" name="formInviaBandoAvvisoSimap" id="BandoInvioForm" >
			<input type="hidden" name="codgar" value="${param.codgar}" />
			<input type="hidden" name="genereGara" value="${param.genereGara}" />
			<input type="hidden" id="metodoInvio" name="metodo" value="controllaEdInvia" />
			<input type="hidden" name="iterga" value="${param.iterga}"/>
			<table class="dettaglio-notab">
				<tr>
					<td class="valore-dato" colspan="2">
						<br>
						Questa funzione prepara ed invia a Formulari Europei i dati della
						gara selezionata per il successivo controllo ed invio al
						<b>SIMAP della Comunit&agrave; Europea.</b>
						<br>
						<br>
						<br>
						<c:if test='${!profiloUtente.autenticazioneSSO}'>
							<b>Credenziali (utente e password) per la connessione a Formulari Europei</b>
							<br>
							<br>
							<input id="correnti" type="radio" name="credenziali" value="CORRENTI" checked onchange="javascipt:gestioneCredenziali()">Credenziali correnti
							<br>
							<input id="altre" type="radio" name="credenziali" value="ALTRE" onchange="javascipt:gestioneCredenziali()">Altre credenziali
							<br>
						</c:if>
					</td>
				</tr>
				
				<c:if test='${!profiloUtente.autenticazioneSSO}'>
					<tr>	
						<td class="etichetta-dato">Utente</td>
						<td class="valore-dato">
							<input type="text" name="username" size="15" disabled/>
						</td>
					</tr>
					<tr>
						<td class="etichetta-dato">Password</td>
						<td class="valore-dato">
							<input type="password" name="password" size="15" disabled/>
						</td>
					</tr>
				</c:if>
				
				<tr>
					<td class="valore-dato" colspan="2"> 
						<br>
						<br>
						<input id="invia" type="radio" name="tipoRichiesta" value="invia" onclick="javascript:cambiaMetodo('controllaEdInvia');" checked><b>Invia bando o avviso</b>
						<br>
						
						<c:choose>
						<c:when test="${settore eq 'S'}">
							<c:set var="LabelAvvisoPreinformazione" value="Avviso periodico indicativo"/>
							<c:set var="LabelSottotipoAvvisoPreinformazione" value="Soltanto avviso periodico indicativo"/>
						</c:when>
						<c:otherwise>
							<c:set var="LabelAvvisoPreinformazione" value="Avviso di preinformazione"/>
							<c:set var="LabelSottotipoAvvisoPreinformazione" value="Soltanto avviso di preinformazione"/>
						</c:otherwise>
						</c:choose>
						
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="fs1" type="radio" name="formulario" value="FS1" onclick="javascript:aggiornaSottoTipo(true);" checked>${LabelAvvisoPreinformazione}
						<br>
						<div id="sottotipoSelez" style="display:block;">
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="avvisoTipo1" type="radio" name="tipoavviso" value="1" />&nbsp;${LabelSottotipoAvvisoPreinformazione}
							<br>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="avvisoTipo2" type="radio" name="tipoavviso" value="2" />&nbsp;Avviso per ridurre i termini di ricezione offerte
							<br>
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="avvisoTipo3" type="radio" name="tipoavviso" value="3" />&nbsp;Avviso di indizione di gara
							<br>
							<br>
						</div>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="fs2" type="radio" name="formulario" value="FS2" onclick="javascript:aggiornaSottoTipo(false);" >Bando di gara
						<br>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="fs3" type="radio" name="formulario" value="FS3" onclick="javascript:aggiornaSottoTipo(false);" >Avviso di aggiudicazione
						<br>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="fs8" type="radio" name="formulario" value="FS8" onclick="javascript:aggiornaSottoTipo(false);" >Avviso relativo al profilo di committente
						<br>
						<br>
						<input id="consulta" type="radio" name="tipoRichiesta" value="consulta" onclick="javascript:cambiaMetodo('leggiPubblicazioni');"><b>Consulta pubblicazioni bando o avviso</b>	
						<br>
						<br>						
					</td>
				</tr>
				<tr>
					<td colspan="2" class="comandi-dettaglio">
						<INPUT type="button" class="bottone-azione" value="Invia richiesta" title="Invia richiesta"	onclick="javascript:inviabandoavvisosimap();">
						<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</form>	
	</gene:redefineInsert>

	<gene:javaScript>
	
		var formulario;
		var sottotipo;
		
		function aggiornaSottoTipo(flag) {
			if (flag == true) {
				if(document.getElementById("sottotipoSelez")!=null)
					document.getElementById("sottotipoSelez").style.display="block";
			} else {
				if(document.getElementById("sottotipoSelez")!=null)
					document.getElementById("sottotipoSelez").style.display="none";
			} 
		}
	
		function cambiaMetodo(metodo){
			if(metodo =='leggiPubblicazioni'){
				formulario = $('input[name=formulario]:checked', '#BandoInvioForm').attr("id");
				sottotipo = $('input[name=radioName]:checked', '#BandoInvioForm').attr("id");
				if(formulario == 'fs1'){aggiornaSottoTipo(false);}
				$('input:radio[name="formulario"]').each(function () { $(this).prop('checked', false); });
			}else{
				$('#' + formulario).prop('checked',true);
				if(formulario == 'fs1'){aggiornaSottoTipo(true);}
			}
			$("#metodoInvio").val(metodo);
		}
		
		function annulla(){
			window.close();
		}
		
		
		function inviabandoavvisosimap() {
		
			var invia = "true";
			
			
			var metodo = $("#metodoInvio").val();
			
			if(metodo == 'controllaEdInvia'){
			<c:if test='${!profiloUtente.autenticazioneSSO}'>
				var altre = document.formInviaBandoAvvisoSimap.altre;
				if (altre.checked) {
					var username = document.formInviaBandoAvvisoSimap.username;
					var password = document.formInviaBandoAvvisoSimap.password;
					
					if (username.value == "") {
						alert("Inserire l'utente");
						invia = "false";
					}
					
					if (password.value == "") {
						alert("Inserire la password");
						invia = "false";
					}
				}
			</c:if>
				var formulario = "";
				var fs1 = document.formInviaBandoAvvisoSimap.fs1;
				var fs2 = document.formInviaBandoAvvisoSimap.fs2;
				var fs3 = document.formInviaBandoAvvisoSimap.fs3;
				var fs8 = document.formInviaBandoAvvisoSimap.fs8;
				
				if (fs1.checked) {
					formulario = "FS1";
					var avvisotipo1 = document.formInviaBandoAvvisoSimap.avvisoTipo1;
					var avvisotipo2 = document.formInviaBandoAvvisoSimap.avvisoTipo2;
					var avvisotipo3 = document.formInviaBandoAvvisoSimap.avvisoTipo3;
					if(!avvisotipo1.checked && !avvisotipo2.checked && !avvisotipo3.checked){
						alert("Selezionare una tipologia di avviso");
						invia = "false";
					}
				} 			
				if (fs2.checked) {
					formulario = "FS2";
				} 
				if (fs3.checked) {
					formulario = "FS3";
				}
				if (fs8.checked) {
					formulario = "FS8";
				} 
				if(formulario == "") {
					alert("Selezionare una tipologia di bando o avviso");
					invia = "false";
				}
			}
			if (invia == "true") {
				document.formInviaBandoAvvisoSimap.submit();
				bloccaRichiesteServer();
			}
		}
		
		function gestioneCredenziali() {
			var altre = document.formInviaBandoAvvisoSimap.altre;
			
			if (altre.checked) {
				document.formInviaBandoAvvisoSimap.username.disabled = false;
				document.formInviaBandoAvvisoSimap.password.disabled = false;					
			} else {
				document.formInviaBandoAvvisoSimap.username.disabled = true;
				document.formInviaBandoAvvisoSimap.password.disabled = true;
				document.formInviaBandoAvvisoSimap.username.value = "";
				document.formInviaBandoAvvisoSimap.password.value = "";
			}
		}
		
		<c:choose>
			<c:when test="${param.genereGara eq 11 }">
				var radioButton = document.formInviaBandoAvvisoSimap.formulario;
				console.log("tipo 11");
				for(var i = 0; i < radioButton.length; i++) { // uso radioButton.length per sapere quanti radio button ci sono
					if(radioButton[i].value!="FS1" && radioButton[i].value!="FS8") { // scorre tutti i vari radio button
						radioButton[i].disabled="disabled";
					}
				}
				var radioButton = document.formInviaBandoAvvisoSimap.tipoavviso;
				console.log("temp");
				for(var i = 0; i < radioButton.length; i++) { // uso radioButton.length per sapere quanti radio button ci sono
					if(radioButton[i].value!="1" ) { // scorre tutti i vari radio button
						radioButton[i].disabled="disabled";
					}
				}
				$("#avvisoTipo1").attr('checked', 'checked');
			</c:when>
			<c:otherwise>
			console.log("tipo non 11");
				<c:if test="${param.iterga eq 1}">
					var radioButton = document.formInviaBandoAvvisoSimap.formulario;
					for(var i = 0; i < radioButton.length; i++) { // uso radioButton.length per sapere quanti radio button ci sono
						if(radioButton[i].value!="FS2" && radioButton[i].value!="FS3" ) { // scorre tutti i vari radio button
							radioButton[i].disabled="disabled";
						}
					}
					$("#avvisoTipo3").attr('checked', 'checked');
					$("#fs2").attr('checked', 'checked');
					aggiornaSottoTipo(false);
				</c:if>
				
				<c:if test="${param.iterga eq 2 or  param.iterga eq 4}">
					var radioButton = document.formInviaBandoAvvisoSimap.tipoavviso;
					for(var i = 0; i < radioButton.length; i++) { // uso radioButton.length per sapere quanti radio button ci sono
						if(radioButton[i].value=="1" ) { // scorre tutti i vari radio button
							radioButton[i].disabled="disabled";
						}
					}
					$("#fs1").attr('checked', 'checked');
					$("#avvisoTipo3").attr('checked', 'checked');
					aggiornaSottoTipo(true);
				</c:if>
				
				<c:if test="${param.iterga ne 1 and param.iterga ne 2 and param.iterga ne 4}">
					var radioButton = document.formInviaBandoAvvisoSimap.formulario;
					for(var i = 0; i < radioButton.length; i++) { // uso radioButton.length per sapere quanti radio button ci sono
						if(radioButton[i].value!="FS3" ) { // scorre tutti i vari radio button
							radioButton[i].disabled="disabled";
						}
					}
					$("#avvisoTipo3").attr('checked', 'checked');
					$("#fs3").attr('checked', 'checked');
					aggiornaSottoTipo(false);
				</c:if>
			</c:otherwise>
		</c:choose>
		
	</gene:javaScript>
</gene:template></div>

