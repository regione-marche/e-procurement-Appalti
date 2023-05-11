
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

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<c:set var="loginRPNT" value='${gene:callFunction("it.eldasoft.sil.w3.tags.funzioni.IsAttivaLoginRPNTFunction", pageContext)}' scope="request"/>

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	
	<c:choose>
		<c:when test="${entita eq 'W3GARA'}">
			<gene:setString name="titoloMaschera"  value='Richiesta assegnazione numero gara' />
		</c:when>
		<c:when test="${entita eq 'W3LOTT'}">
			<gene:setString name="titoloMaschera"  value='Richiesta assegnazione codice CIG' />
		</c:when>
		<c:when test="${entita eq 'W3SMARTCIG'}">
			<gene:setString name="titoloMaschera"  value='Richiesta assegnazione codice SMARTCIG' />
		</c:when>
	</c:choose>

	<gene:redefineInsert name="corpo">
		<form action="${contextPath}/w3/RichiestaIDGARACIG.do" method="post" name="formIDGARACIG">
		
			<c:set var="entita" value="${param.entita}" scope="request"/>
		
			<input type="hidden" name="entita" value="${entita}" />
			<input type="hidden" name="numgara" value="${param.numgara}" />
			<input type="hidden" name="numlott" value="${param.numlott}" />
			
			<table class="dettaglio-notab">
			<c:set var="entitaFnc" value ="CREDENZRUP" />
			<c:set var="numgara" value="${param.numgara}" />
			<c:set var="stato" value='${gene:callFunction5("it.eldasoft.sil.w3.tags.funzioni.GestioneValidazioneIDGARACIGFunction",pageContext,entitaFnc,profiloUtente.id,numgara,entita)}'/>	
			<gene:set name="titoloGenerico" value="${titolo}" />
			<gene:set name="listaGenericaControlli" value="${listaControlli}" />
			<gene:set name="numeroErrori" value="${numeroErrori}" />
			<gene:set name="numeroWarning" value="${numeroWarning}" />
			<c:set var="notDelegato" value ="false"/>	
			<c:forEach items="${listaControlli}" step="1" var="controllo" varStatus="status" >
					<c:if test="${controllo[2] eq 'Delega del RUP assente'}">
						<c:set var="notDelegato" value="true"/>	
					</c:if>
			</c:forEach>
			<c:choose>
				<c:when test="${!empty listaControlli && ((loginRPNT ne '1') ||  (loginRPNT eq '1' && erroreCredenzialiRPNT) || notDelegato)}">
					<jsp:include page="../commons/popup-validazione-interno.jsp" />	
					<c:set var="erroreInvioRichiestaSimog" value="true"/>
				</c:when>
				<c:otherwise>
				<c:choose>
				<c:when test="${cigNonPresenti.size() > 0 }">
				<b>Attenzione: in ANAC sono presenti dei CIG (${cigNonPresenti.size()}) per questa gara non associati ad alcuno dei lotti in anagrafica.</b>
				<br>
				<br>				
				Scegliere se associare uno dei CIG disponibili al lotto corrente o richiedere l'assegnazione di un nuovo CIG:
				<br>
				
				<div style="text-align:center" onchange="javascript:cambiaModalita();">
				<br>
				<INPUT type="radio" name="modalita" value="collega" checked><label><b>Collega CIG</b></label>
				<INPUT type="radio" name="modalita" value="richiedi" ><label><b>Richiesta nuovo CIG</b></label>
				<br>
				<br>
				</div>
				<div style="border-bottom: 1px solid #A0AABA;">	
				</div>
				<br>
					<table class="dettaglio-notab" id="collega">
						<tr>
							<td class="valore-dato" style="width:auto;padding:0 3px;background-color:#ccc;border:1px solid dimgray"><center>CIG</center></td>
							<td class="valore-dato" style="width:auto;padding:0 3px;background-color:#ccc;border:1px solid dimgray"><center>Creazione</center></td>
							<td class="valore-dato" style="width:auto;padding:0 5px;background-color:#ccc;border:1px solid dimgray"><center>Oggetto in ANAC</center></td>
							<td class="valore-dato" style="width:auto;padding:0 3px;background-color:#ccc;border:1px solid dimgray"><center>Importo in ANAC</center></td>
						</tr> 
						<c:forEach var="cig" items="${cigNonPresenti}" >
						<tr>
							<td class="etichetta-dato" style="text-align:left;width:110px;border:1px solid dimgray">
								<INPUT type="radio" class="bottone-azione" name="cig" value="${cig.key};${cig.value[2]}">
								<span >${cig.key}</span>
							</td>
							<td class="valore-dato" style="text-align:center;width:auto;padding:0 3px;border-right:1px solid dimgray">
								<span >${cig.value[2]}</span>
							</td>
							<td class="valore-dato" style="text-align:left;width:auto;padding:0 5px;border-right:1px solid dimgray">
								<span >${cig.value[1]}</span>
							</td>
							<td class="valore-dato" style="text-align:center;width:auto;padding:0 3px;border-right:1px solid dimgray">
								<span >${cig.value[0]} &euro;</span>
							</td>	
						</tr>
						</c:forEach>
						<tr>
							<td colspan="4" class="comandi-dettaglio">
										<INPUT type="button" class="bottone-azione" value="Collega il cig selezionato" title="Collega il cig selezionato" onclick="javascript:collegaIDGARACIG();">
										<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;		
							</td>
						</tr>
					</table>
				<table class="dettaglio-notab" id="richiedi" style="display:none">
						<jsp:include page="sezione-credenziali-simog.jsp"/>
						<tr>
							<td colspan="2" class="comandi-dettaglio">
								<c:if test="${erroreInvioRichiestaSimog ne 'true'}">
									<INPUT type="button" class="bottone-azione" value="Invia richiesta di assegnazione" title="Invia richiesta di assegnazione" onclick="javascript:richiestaIDGARACIG();">
									<INPUT type="hidden" name="inviaConCigNonPresenti" value="1">
								</c:if>
								<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
							</td>
						</tr>
					</table>
				</c:when>
				<c:otherwise>
					<table class="dettaglio-notab">
						<jsp:include page="sezione-credenziali-simog.jsp"/>
						<tr>
							<td colspan="2" class="comandi-dettaglio">
								<c:if test="${erroreInvioRichiestaSimog ne 'true'}">
									<INPUT type="button" class="bottone-azione" value="Invia richiesta di assegnazione" title="Invia richiesta di assegnazione" onclick="javascript:richiestaIDGARACIG();">
								</c:if>
								<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
							</td>
						</tr>
					</table>
				</c:otherwise>
			</c:choose>
			</c:otherwise>
			</c:choose>
		</form>	
	</gene:redefineInsert>
	
	<gene:javaScript>
		document.forms[0].jspPathTo.value="w3/commons/popup-richiesta-idgaracig.jsp";
		
		function annulla(){
			window.close();
		}
		
		function richiestaIDGARACIG(){
		
			var invia = "true";
		
			var simogwsuser = document.formIDGARACIG.simogwsuser;
			if (simogwsuser.value == "") {
				alert("Inserire l'utente");
				invia = "false";
			}
			
			var simogwspass = document.formIDGARACIG.simogwspass;			
			if (invia == "true" && simogwspass.value == "") {
				alert("Inserire la password");
				invia = "false";
			}

			if (invia == "true") {
				document.formIDGARACIG.submit();
				bloccaRichiesteServer();
			}		
		}
		
		function collegaIDGARACIG(){
			var cig = document.formIDGARACIG.cig.value;
			if(cig){
				if(confirm("Il CIG "+cig.split(";")[0]+" verr\u00E0 assegnato al lotto corrente.\nProcedere?")){
					var form = document.formIDGARACIG;
					var hiddenInput = document.createElement('input');
					hiddenInput.type = 'hidden';
					hiddenInput.name = 'codiceCig';
					hiddenInput.value = cig.split(";")[0];
					form.appendChild(hiddenInput);
					var hiddenInput2 = document.createElement('input');
					hiddenInput2.name = 'dataCreazioneLotto';
					hiddenInput2.type = 'hidden';
					hiddenInput2.value = cig.split(";")[1];
					form.appendChild(hiddenInput2)
					document.formIDGARACIG.submit();
					bloccaRichiesteServer();
				}	
			}
			else{
				alert("Nessun CIG selezionato per il collegamento");
			}	
		}
		
		function cambiaModalita(){
			var modalita = document.formIDGARACIG.modalita.value;
			if(modalita=="collega"){
				$('#collega').show();
				$('#richiedi').hide();
			}else{
				$('#collega').hide();
				$('#richiedi').show();
			}
		}
	</gene:javaScript>	
</gene:template>

</div>

