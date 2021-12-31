<%
/*
 * Created on: 05-03-2015
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
		Finestra per la rettifica dei termini
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.rettificaEseguita and requestScope.rettificaEseguita eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:redefineInsert name="head">
	<script type="text/javascript" src="${contextPath}/js/date.js"></script> 
</gene:redefineInsert>

<c:set var="modo" value="NUOVO" scope="request" />

	<c:choose>
		<c:when test='${!empty param.iterga}'>
			<c:set var="iterga" value="${param.iterga}" />
		</c:when>
		<c:otherwise>
			<c:set var="iterga" value="${iterga}" />
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
		<c:when test='${!empty param.pagina}'>
			<c:set var="pagina" value="${param.pagina}" />
		</c:when>
		<c:otherwise>
			<c:set var="pagina" value="${pagina}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.tipoGara}'>
			<c:set var="tipoGara" value="${param.tipoGara}" />
		</c:when>
		<c:otherwise>
			<c:set var="tipoGara" value="${tipoGara}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.gartel}'>
			<c:set var="gartel" value="${param.gartel}" />
		</c:when>
		<c:otherwise>
			<c:set var="gartel" value="${gartel}" />
		</c:otherwise>
	</c:choose>
	
	<c:set var="pubblicazionePortaleBando" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codgar,"BANDO11","false")}'/>
		
	<gene:setString name="titoloMaschera" value='Rettifica termini di gara' />
		
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="TORN" gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestorePopupRettificaTermini" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupRettificaTermini">
	

	<c:set var="offerteAcquisite" value='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO"}'/>
	<c:set var="OffertaDitteNonPresente" value='${not empty requestScope.controlloOffertaDitteSuperato and requestScope.controlloOffertaDitteSuperato eq "NO"}'/>
	
		<gene:campoScheda>
			<td colSpan="2">
				<br>
				<c:choose>
					<c:when test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO" and (not empty requestScope.controlloBusteSuperato and requestScope.controlloBusteSuperato eq "NO" )}' >
						${requestScope.msg }
					</c:when>
					<c:otherwise>
						<c:choose>
							<c:when test="${visualizzarePresentazioneOfferta and  visualizzareAperturaPlichi }">
							Selezionare i termini di gara da rettificare e impostare i nuovi valori:<br>
								<c:if test='${!offerteAcquisite && !OffertaDitteNonPresente}'>
									<input type="radio" value="1" name="terminiFaseInvito" id="terminiPresentazioneOfferta" <c:if test="${!offerteAcquisite && !OffertaDitteNonPresente}">checked="checked"</c:if> onclick="javascript:cambiaVisualizzazioneTermini(1);" />
									 termini per la presentazione dell'offerta
									<br>
								</c:if>
								<input type="radio" value="2" name="terminiFaseInvito" id="terminiAperturaPlichi" <c:if test="${offerteAcquisite || OffertaDitteNonPresente}">checked="checked"</c:if> onclick="javascript:cambiaVisualizzazioneTermini(2);" />
								 termini di apertura plichi
								<br>
							</c:when>
							<c:otherwise>
								Impostare i nuovi termini di gara
								<br>
							</c:otherwise>
						</c:choose>
						<c:if test="${requestScope.terminiSuperati eq 'SI' }">
							<br><b>Attenzione</b>: ${requestScope.msgInfoSupTermini }
							<c:if test='${offerteAcquisite || OffertaDitteNonPresente }'>
							<br><br>E' possibile procedere alla sola rettifica dei termini di apertura plichi perchè si è già proceduto all'acquisizione delle offerte.
							</c:if>
						</c:if>
					</c:otherwise>
				</c:choose>
				<br>&nbsp;
			</td>
		</gene:campoScheda>
		
		
		<c:choose>
			<c:when test="${pagina eq 'Datigen' and (iterga eq '2' or iterga eq '4')}">
				<c:if test='${requestScope.controlloSuperato eq "SI"}'>
					<gene:campoScheda nome="PDP">
						<td colspan="2"><b>Termini per la presentazione della domanda di partecipazione</b></td>
					</gene:campoScheda>
					<gene:campoScheda campo="DTEPAR_ORIG" campoFittizio="true" defaultValue="${initDtepar}" visibile="false" definizione="D;0"/>
					<gene:campoScheda campo="OTEPAR_ORIG" campoFittizio="true" defaultValue="${initOtepar}" visibile="false" definizione="T6;0"/>
					<gene:campoScheda campo="DTERMRICHCDP_ORIG" campoFittizio="true" defaultValue="${initDtermrichcdp}" visibile="false" definizione="D;0"/>
					<gene:campoScheda campo="DTERMRISPCDP_ORIG" campoFittizio="true" defaultValue="${initDtermrispcdp}" visibile="false" definizione="D;0"/>
					<gene:campoScheda campo="DTEPAR" campoFittizio="true" defaultValue="${initDtepar}"  obbligatorio="true" definizione="D;0;;;DTEPAR" />
					<gene:campoScheda campo="OTEPAR" campoFittizio="true" defaultValue="${initOtepar}"  obbligatorio="true" definizione="T6;0;;ORA;OTEPAR" />
					<gene:campoScheda campo="DTERMRICHCDP" campoFittizio="true" defaultValue="${initDtermrichcdp}"  definizione="D;0;;;DATTRCDP" visibile="${gene:checkProt(pageContext, 'COLS.VIS.GARE.TORN.DTERMRICHCDP') }"/>
					<gene:campoScheda campo="DTERMRISPCDP" campoFittizio="true" defaultValue="${initDtermrispcdp}"  definizione="D;0;;;DATTURCDP" visibile="${gene:checkProt(pageContext, 'COLS.VIS.GARE.TORN.DTERMRISPCDP') }"/>
				</c:if>
			</c:when>
			<c:when test="${(pagina eq 'Datigen' and (iterga eq '1' or iterga eq '3' or iterga eq '5' or iterga eq '6')) or pagina eq 'Invito'}">
				<gene:campoScheda nome="PDO">
					<td colspan="2"><b>Termini per la presentazione dell'offerta</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="DTEOFF_ORIG" campoFittizio="true" defaultValue="${initDteoff}" visibile="false" definizione="D;0"/>
				<gene:campoScheda campo="OTEOFF_ORIG" campoFittizio="true" defaultValue="${initOteoff}" visibile="false" definizione="T6;0"/>
				<gene:campoScheda campo="DTERMRICHCPO_ORIG" campoFittizio="true" defaultValue="${initDtermrichcpo}" visibile="false" definizione="D;0"/>
				<gene:campoScheda campo="DTERMRISPCPO_ORIG" campoFittizio="true" defaultValue="${initDtermrispcpo}" visibile="false" definizione="D;0"/>
				<gene:campoScheda campo="DTEOFF" campoFittizio="true" defaultValue="${initDteoff}"  obbligatorio="true" definizione="D;0;;DATE;DTEOFF" />
				<gene:campoScheda campo="OTEOFF" campoFittizio="true" defaultValue="${initOteoff}"  obbligatorio="true" definizione="T6;0;;ORA;OTEOFF" />
				<gene:campoScheda campo="DTERMRICHCPO" campoFittizio="true" defaultValue="${initDtermrichcpo}" definizione="D;0;;DATE;DATTRCPO" visibile="${gene:checkProt(pageContext, 'COLS.VIS.GARE.TORN.DTERMRICHCPO') }"/>
				<gene:campoScheda campo="DTERMRISPCPO" campoFittizio="true" defaultValue="${initDtermrispcpo}" definizione="D;0;;DATE;DATTURCPO" visibile="${gene:checkProt(pageContext, 'COLS.VIS.GARE.TORN.DTERMRISPCPO') }"/>
				<gene:campoScheda nome="OFF">
					<td colspan="2"><b>Apertura plichi</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="DESOFF_ORIG" campoFittizio="true" defaultValue="${initDesoff}" visibile="false" definizione="D;0"/>
				<gene:campoScheda campo="OESOFF_ORIG" campoFittizio="true" defaultValue="${initOesoff}" visibile="false" definizione="T6;0"/>
				<gene:campoScheda campo="DESOFF" campoFittizio="true" defaultValue="${initDesoff}"  obbligatorio="true" definizione="D;0;;DATE;GDESOFF" />
				<gene:campoScheda campo="OESOFF" campoFittizio="true" defaultValue="${initOesoff}"  obbligatorio="true" definizione="T6;0;;ORA;GOESOFF" />
				<c:if test="${visualizzarePresentazioneOfferta and  visualizzareAperturaPlichi }">
					<gene:fnJavaScriptScheda funzione='bloccoCambioTermini()' elencocampi='DTEOFF;OTEOFF;DTERMRICHCPO;DTERMRISPCPO;DESOFF;OESOFF' esegui="false" />
				</c:if>
			</c:when>
		</c:choose>
		
						
		<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
		<input type="hidden" name="iterga" id="iterga" value="${iterga}" />
		<input type="hidden" name="pagina" id="pagina" value="${pagina}" />
		<input type="hidden" name="tipoGara" id="tipoGara" value="${tipoGara}" />
		<input type="hidden" name="termineVisualizzato" id="termineVisualizzato" value="" />
		<input type="hidden" name="pubblicazionePortaleBando" id="pubblicazionePortaleBando" value="${pubblicazionePortaleBando }" />
		<input type="hidden" name="gartel" id="gartel" value="${gartel}" />
		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
	</gene:formScheda>
  </gene:redefineInsert>

<c:if test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO" and (not empty requestScope.controlloBusteSuperato and requestScope.controlloBusteSuperato eq "NO" )}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
	</gene:redefineInsert>
</c:if>
	
	
	<gene:javaScript>
			
		function conferma() {
			var controlliSuperati=false;
			
			<c:choose>
				<c:when test="${pagina eq 'Datigen' and (iterga eq '2' or iterga eq '4')}">
					var dtepar_orig = getValue("DTEPAR_ORIG");
					var dtepr = getValue("DTEPAR");
					var otepar_orig = getValue("OTEPAR_ORIG");
					var otepar = getValue("OTEPAR");
					var dtermrichcdp_orig = getValue("DTERMRICHCDP_ORIG");
					var dtermrichcdp = getValue("DTERMRICHCDP");
					var dtermrispcdp_orig = getValue("DTERMRISPCDP_ORIG");
					var dtermrispcdp = getValue("DTERMRISPCDP");
					
					if(controlloDate(dtepar_orig,dtepr,otepar_orig,otepar,dtermrichcdp_orig,dtermrichcdp,dtermrispcdp_orig,dtermrispcdp)<0){
						return;
					}
					document.getElementById("termineVisualizzato").value=1;
				</c:when>
				<c:when test="${(pagina eq 'Datigen' and (iterga eq '1' or iterga eq '3' or iterga eq '5' or iterga eq '6')) or pagina eq 'Invito'}">
					var termineVisualizzato = document.getElementById("termineVisualizzato").value;
					//Controllo solo i valori del check attivo
					var termineVisualizzato = document.getElementById("termineVisualizzato").value;
					if(termineVisualizzato==2){
						var dteoff_orig = getValue("DTEOFF_ORIG");
						var dteoff = getValue("DTEOFF");
						var oteoff_orig = getValue("OTEOFF_ORIG");
						var oteoff = getValue("OTEOFF");
						
						var dtermrichcpo_orig = getValue("DTERMRICHCPO_ORIG");
						var dtermrichcpo = getValue("DTERMRICHCPO");
						var dtermrispcpo_orig = getValue("DTERMRISPCPO_ORIG");
						var dtermrispcpo = getValue("DTERMRISPCPO");
						
						if(controlloDate(dteoff_orig,dteoff,oteoff_orig,oteoff,dtermrichcpo_orig,dtermrichcpo,dtermrispcpo_orig,dtermrispcpo)<0){
							return;
						}
					}else{
						var desoff_orig = getValue("DESOFF_ORIG");
						var desoff = getValue("DESOFF");
						var oesoff_orig = getValue("OESOFF_ORIG");
						var oesoff = getValue("OESOFF");
						if(controlloDate(desoff_orig,desoff,oesoff_orig,oesoff,null,null,null,null)<0){
							return;
						}
					}
				</c:when>
			</c:choose>
			
			document.forms[0].jspPathTo.value="gare/commons/popup-rettificaTermini.jsp";
			schedaConferma();
			
		}
		
		function controlloDate(dataOriginale,data,oraOriginale,ora,dataRichiestaOriginale,dataRichiesta,dataRispostaOriginale,dataRisposta){
			if(data == null || data=="" || ora == null || ora == "") {
				alert("I campi data ed ora sono obbligatori");
				return -1
			}
			
			if(!(dataOriginale!=data || oraOriginale!=ora || dataRichiestaOriginale!=dataRichiesta || dataRispostaOriginale!= dataRisposta)){
				alert("Nessun dato è stato variato");
				return -1;
			}
			var dataTermine = DateDaStringa(data);
			if(ora!=null){
				var orario = ora.split(":");
				dataTermine.setHours(orario[0]);
				dataTermine.setMinutes(orario[1]);
			}
			var oggi = new Date();
			if(dataTermine < oggi){
				alert("La data e ora specificata deve essere successiva alla data e ora corrente");
				return -1;
			}
		}
		
		function annulla(){
			window.close();
		}
		
		function cambiaVisualizzazioneTermini(valore){
			if(valore=="1"){
				showObj("rowPDO",true);
				showObj("rowDTEOFF", true);
				showObj("rowOTEOFF", true);
				showObj("rowDTERMRICHCPO", true);
				showObj("rowDTERMRISPCPO", true);
				showObj("rowOFF",false);
				showObj("rowDESOFF", false);
				showObj("rowOESOFF", false);
				document.getElementById("termineVisualizzato").value=2;
			}else{
				showObj("rowOFF",true);
			    showObj("rowDESOFF", true);
				showObj("rowOESOFF", true);
				showObj("rowPDO",false);
				showObj("rowDTEOFF", false);	
				showObj("rowOTEOFF", false);
				showObj("rowDTERMRICHCPO", false);
				showObj("rowDTERMRISPCPO", false);
				document.getElementById("termineVisualizzato").value=3;
			}
		}
		
		<c:if test="${(pagina eq 'Datigen' and (iterga eq '1' or iterga eq '3' or iterga eq '5' or iterga eq '6')) or pagina eq 'Invito'}">
			<c:choose>
				<c:when test="${ requestScope.controlloSuperato eq 'NO' and requestScope.controlloBusteSuperato eq 'NO'}">
					showObj("rowOFF",false);
					showObj("rowOTEOFF", true);
					showObj("rowDESOFF", false);
					showObj("rowOESOFF", false);
					showObj("rowPDO",false);
					showObj("rowDTEOFF", false);	
					showObj("rowOTEOFF", false);
					showObj("rowDTERMRICHCPO", false);
					showObj("rowDTERMRISPCPO", false);
				</c:when>
				<c:when test="${ (visualizzarePresentazioneOfferta and not visualizzareAperturaPlichi) or requestScope.controlloSuperato eq 'NO-Apertura'}">
					showObj("rowOFF",false);
					showObj("rowOTEOFF", true);
					showObj("rowDESOFF", false);
					showObj("rowOESOFF", false);
					document.getElementById("termineVisualizzato").value=2;
				</c:when>
				<c:when test="${ (not visualizzarePresentazioneOfferta and  visualizzareAperturaPlichi) or requestScope.controlloSuperato eq 'NO-Presentazione'}">
					showObj("rowPDO",false);
					showObj("rowDTEOFF", false);	
					showObj("rowOTEOFF", false);
					showObj("rowDTERMRICHCPO", false);
					showObj("rowDTERMRISPCPO", false);
					document.getElementById("termineVisualizzato").value=3;
				</c:when>
				<c:when test="${ requestScope.controlloSuperato eq 'NO' || requestScope.controlloOffertaDitteSuperato eq 'NO'}">
					cambiaVisualizzazioneTermini(2);
				</c:when>	
				<c:otherwise>
					cambiaVisualizzazioneTermini(1);
				</c:otherwise>
			</c:choose>
		</c:if>
		
		<c:if test="${visualizzarePresentazioneOfferta and  visualizzareAperturaPlichi }">
			//Se si modifica qualcosa in una sezione si blocca il radio button per passare all'altra
			function bloccoCambioTermini(){
				if(document.getElementById("terminiPresentazioneOfferta")!=null){
					var sezioneVisualizzata=document.getElementById("termineVisualizzato").value;
					if(sezioneVisualizzata==2)
						document.getElementById("terminiAperturaPlichi").disabled=true;
					else
						document.getElementById("terminiPresentazioneOfferta").disabled=true;
				}
			}
		</c:if>
	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>