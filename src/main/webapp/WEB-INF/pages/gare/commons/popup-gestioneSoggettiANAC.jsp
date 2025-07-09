<%
/*
 * Created on: 11-10-2012
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
		Finestra per la valorizzazione dei campi CODCIG, DACQCIG 
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>

<div style="display: none">
	<div class="contenitore-errori-custom" >
		<div class="errori-javascript-titolo">
			<div><img src="${pageContext.request.contextPath}/img/jsMsgOn.gif" alt="Messaggi">Messaggi</div>
			<div class="errori-javascript-dettaglio">

			</div>
		</div>
	</div>
</div>

<c:set var="titolo" value="${param.ruolo eq 'RP'? 'Presa in carico/ modifica RUP':'Presa in carico trasmissione schede ANAC'}"/>
<c:choose>
	<c:when test='${not empty esito and esito eq "OK"}' >
	<div style="width:97%;">
	<gene:template file="popup-message-template.jsp">
	<gene:setString name="titoloMaschera" value='${titolo}' />	
		<gene:redefineInsert name="corpo">
			<gene:formScheda entita="TORN" gestisciProtezioni="false">
				<gene:campoScheda campo="CODGAR" visibile="false"/>
				<gene:campoScheda>
					<td colSpan="2">
						<br>
						Operazione di modifica eseguita con successo
						
						<br>&nbsp;
						<br>&nbsp;
					</td>
				</gene:campoScheda>
			</gene:formScheda>
	  </gene:redefineInsert>

	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
	</gene:redefineInsert>
	
	</gene:template>
		<script type="text/javascript">
		function chiudi(){
				opener.historyReload();
				window.close();
		}
		</script>
	</c:when>
	<c:otherwise>
		<c:set var="key" value="TORN.CODGAR=T:${param.codgar}" scope="request"/>
		<c:set var="ruolo" value="${param.ruolo}"/>

		<div style="width:97%;">
		<gene:template file="popup-template.jsp">

		
		
		
		<c:set var="modo" value="MODIFICA" scope="request" />
			
			<gene:redefineInsert name="corpo">
			<gene:formScheda entita="TORN" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupGestioneSoggettiANAC">
			<gene:setString name="titoloMaschera" value='${titolo}' />	
				<input type="hidden" name="codgar" value="${param.codgar}"/>
				<input type="hidden" name="ruolo" value="${ruolo}"/>
				<gene:campoScheda campo="CODGAR" visibile="false"/>
				<c:choose>
					<c:when test="${not empty result}">
						<gene:campoScheda>
							<td colspan="2">
								<br>
								L'operazione non è andata a buon fine
								<br><br><br>
							</td>
						</gene:campoScheda>
						<gene:campoScheda>
							<td class="comandi-dettaglio" colspan="2">	
								<input type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla()">&nbsp;
							</td>
						</gene:campoScheda>
					</c:when>
					<c:when test="${ruolo eq 'RP'}">
						<c:set var="nomtec" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select tecni.nomtec from tecni, torn where torn.codrup=tecni.codtec and torn.codgar=?", gene:getValCampo(key,"CODGAR"))}'/>
						<gene:campoScheda><td colSpan="2"><br>Mediante questa funzione è possibile procedere alla presa in carico da parte del nuovo RUP.
								<br>L'operazione prevede la trasmissione di una richiesta ad ANAC per l'allineamento del dato. <br><br>Confermi l'operazione?
								<br><br>
							</td>
						</gene:campoScheda>
						<gene:archivio titolo="Tecnici"
						 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP"),"gene/tecni/tecni-lista-popup.jsp","")}'
						 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
						 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
						 campi="TECNI.CODTEC;TECNI.NOMTEC"
						 functionId="skip"
						 chiave="TORN_CODRUP"
						 inseribile="true">
							<gene:campoScheda campo="CODRUP" entita="TORN" where="GARE.CODGAR1 = TORN.CODGAR" obbligatorio="true"/>
							<gene:campoScheda campo="NOMTEC" title="Nome" campoFittizio="true" definizione="T100;;;;NOMTEC1" value="${nomtec}" />
						</gene:archivio>
						<gene:campoScheda>
							<td colSpan="2"><br></td>
						</gene:campoScheda>
					</c:when>
					<c:otherwise>
						<c:set var="nomtec" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select tecni.nomtec from tecni, torn where torn.coddrp=tecni.codtec and torn.codgar=?", gene:getValCampo(key,"CODGAR"))}'/>
						<gene:campoScheda><td colSpan="2"><br>Mediante questa funzione è possibile prendere in carico la gestione delle schede ANAC.
								<br>L'operazione prevede la trasmissione di una richiesta ad ANAC per l'allineamento del dato.<br><br>Confermi l'operazione?
								<br><br>
							</td>
						</gene:campoScheda>
						<gene:archivio titolo="Tecnici"
						 lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODDRP"),"gene/tecni/tecni-lista-popup.jsp","")}'
						 scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
						 schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
						 campi="TECNI.CODTEC;TECNI.NOMTEC"
						 functionId="skip"
						 chiave="TORN_CODDRP"
						 inseribile="true">
							<gene:campoScheda campo="CODDRP" entita="TORN" obbligatorio="true" where="GARE.CODGAR1 = TORN.CODGAR"/>
							<gene:campoScheda campo="NOMTEC" title="Nome" obbligatorio="true" campoFittizio="true" definizione="T100;;;;NOMTEC1" value="${not empty datiRiga.TORN_CODDRP? nomtec : null}" />
						</gene:archivio>
						<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetListaSAPresaCaricoFunction" parametro="${param.codgar}" />
						<gene:campoScheda>
							<tr>
								<td class="etichetta-dato">Stazione appaltante (*)</td>
								<td class="valore-dato">
								<select name="SAPresaCarico" id="SAPresaCarico">
									<c:forEach items="${listaSA}" var="sa">
										<option value="${sa[0]}">${sa[0]} - ${sa[1]}</option>
									</c:forEach>
								</select>
								</td>
							</tr>
						</gene:campoScheda>
						<gene:campoScheda>
							<td colSpan="2"><br></td>
						</gene:campoScheda>
					</c:otherwise>
				</c:choose>
				<gene:campoScheda>
				<c:if test="${empty result}">
					<td class="comandi-dettaglio" colspan="2">	
						<input type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma()">&nbsp;
						<input type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
					</td>
				</c:if>
				</gene:campoScheda>
				
			</gene:formScheda>
		  </gene:redefineInsert>

			<gene:javaScript>
				<c:if test="${empty result && empty esito}">
				NOMTEC.value='';
				<c:choose>
					<c:when test="${param.ruolo eq 'RP'}">
						TORN_CODRUP.value='';
					</c:when>
					<c:otherwise>
						TORN_CODDRP.value='';
					</c:otherwise>
				</c:choose>
				</c:if>
				document.forms[0].jspPathTo.value="gare/commons/popup-gestioneSoggettiANAC.jsp";
				
				function conferma() {
					schedaConferma();
				}
				
				function annulla(){
					window.close();
				}		
				
				<c:if test="${not empty result}">
				var jsonConErrori = ${result};
				mapResponse(jsonConErrori);

				function mapResponse(data){
					console.log(data)
					var anacErrors = data.anacErrors;
					var internalErrors = data.internalErrors;
					var pgErrors = data.pgErrors;
					var $elements = null;
				
					if(data.done=='N'){
						$elements = $("<li>").addClass('errori-javascript-err').text('ERRORE: ').append($("<small>").text("Si è verificato un errore nell'elaborazione della richiesta da parte di ANAC."));
					}
					if(anacErrors?.length>0){
						$elements = anacErrors.map((error) => $("<li>").addClass('errori-javascript-err').text('ERRORE: ').append($("<small>").text((error.codice=="ERR9999"? "" :error.codice+": ")+(error.dettaglio? error.dettaglio:""))));
					}
					if(internalErrors?.length>0){
						$elements = internalErrors.map((error) => $("<li>").addClass('errori-javascript-err').text('ERRORE: ').append($("<small>").text(error)));
					}
					if(pgErrors){
						$elements = $("<li>").addClass('errori-javascript-err').text('ERRORE: ').append($("<small>").text(pgErrors));
					}
					if(anacErrors?.length>0 || internalErrors?.length>0 || pgErrors || data.done=='N'){
						$('.errori-javascript-dettaglio').append($("<ul>").html($elements));
						$('.errori-javascript-dettaglio').append($("<br>"));
						var $divErr = $('.contenitore-errori-custom')
						$('.contenitore-errori-arealavoro').first().html($divErr);
					}
				}
				</c:if>

				$(document).ready(function(){
					if($("#SAPresaCarico option" ).length==1){
						$("#SAPresaCarico" ).on('mousedown', function(e) {
							e.preventDefault();
							this.blur();
							window.focus();
						});
						$("#SAPresaCarico" ).css('background-color','#EFEFEF');
					}
				});
				
			</gene:javaScript>
		</gene:template>
		</div>

	</c:otherwise>
</c:choose>
