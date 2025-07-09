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
	<c:when test="${requestScope.rettificaDaEseguireSuANAC eq '1' and not empty requestScope.rettificaEseguita and requestScope.rettificaEseguita eq '1'}">
	<gene:template file="popup-message-template.jsp">
		<gene:setString name="titoloMaschera" value='Rettifica termini di gara' />
		<gene:redefineInsert name="corpo">
			<table class="dettaglio-notab">
				<tbody>
					<tr>
						<td colSpan="2">
							<br>
							Rettifica dei dati in corso. <br><br>Per completare l'operazione è necessario procedere con l'invio della scheda di rettifica a ANAC. La scheda è stata generata correttamente. Accedere alla pagina 'Documenti e atti' per gestirne l'invio
							<br>
							<br>&nbsp;
						</td>
					</tr>
				</tbody>
			</table>
		</gene:redefineInsert>
		<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla()">&nbsp;
		</gene:redefineInsert>
		<gene:javaScript>
			opener.historyReloadWithoutPopUps();
			
			function annulla(){
				window.close();
			}
				
		</gene:javaScript>
	</gene:template>
	</c:when>
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
	<c:choose>
		<c:when test='${!empty param.modalitaAmministratore}'>
			<c:set var="modalitaAmministratore" value="${param.modalitaAmministratore}" />
		</c:when>
		<c:otherwise>
			<c:set var="modalitaAmministratore" value="${modalitaAmministratore}" />
		</c:otherwise>
	</c:choose>
	<c:if test="${modalitaAmministratore eq 'true'}">
		<c:set var="forza" value="true" />
	</c:if>
	<c:choose>
		<c:when test='${!empty param.esisteSchedaRettificaAperta}'>
			<c:set var="esisteSchedaRettificaAperta" value="${param.esisteSchedaRettificaAperta}" />
		</c:when>
		<c:otherwise>
			<c:set var="esisteSchedaRettificaAperta" value="${esisteSchedaRettificaAperta}" />
		</c:otherwise>
	</c:choose>
	
	<c:set var="pubblicazionePortaleBando" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codgar,"BANDO11","false")}'/>
	<c:set var="pubblicazionePortaleInvito" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoPubblicazionePortaleFunction", pageContext,codgar,"BANDO13","false")}'/>
	
	<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
			
	<gene:setString name="titoloMaschera" value='Rettifica termini di gara ${forza eq "true"? "- modalità Amministratore":""}' />
		
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
					<c:when test="${esisteSchedaRettificaAperta eq 'true' && modalitaAmministratore ne 'true'}">
						Risulta già in corso la rettifica dei termini, che prevede l'invio della scheda ad ANAC. <br>La scheda è tuttora nello stato 'In compilazione'. Procedendo i dati verranno sovrascritti. <br><br>Confermi l'operazione?<br></span>
					</c:when>
					<c:otherwise>
						Impostare i nuovi termini di gara
						<br>
						<c:if test="${requestScope.terminiSuperati eq 'SI' }">
							<br><b>Attenzione</b>: ${requestScope.msgInfoSupTermini }
							<c:if test='${offerteAcquisite || OffertaDitteNonPresente }'>
							<br><br>E' possibile procedere alla sola rettifica dei termini di apertura plichi perchè si è già proceduto all'acquisizione delle offerte.
							</c:if>
						</c:if>
					</c:otherwise>
				</c:choose>
				<br>&nbsp;
				
				<c:if test="${forza eq 'true'}">
				<div style="color: blue"><b>ATTENZIONE:</b> rettifica termini in  modalità Amministratore.
				Procedendo con la rettifica, i nuovi termini saranno disallineati rispetto a quelli comunicati ad ANAC</div>
				<br>
				</c:if>
			</td>
		</gene:campoScheda>
		
		
		<c:choose>
			<c:when test="${pagina eq 'Datigen' and (iterga eq '2' or iterga eq '4' or iterga eq '7')}">
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
			<c:when test="${(pagina eq 'Datigen' and (iterga eq '1' or iterga eq '3' or iterga eq '5' or iterga eq '6' or iterga eq '9')) or pagina eq 'Invito'}">
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
				<gene:campoScheda campo="DESOFF" campoFittizio="true" defaultValue="${initDesoff}"  obbligatorio="${(not visualizzarePresentazioneOfferta and  visualizzareAperturaPlichi) or requestScope.controlloSuperato eq 'NO-Presentazione' || requestScope.controlloSuperato eq 'NO' || requestScope.controlloOffertaDitteSuperato eq 'NO'}" definizione="D;0;;DATE;GDESOFF" />
				<gene:campoScheda campo="OESOFF" campoFittizio="true" defaultValue="${initOesoff}"  obbligatorio="${(not visualizzarePresentazioneOfferta and  visualizzareAperturaPlichi) or requestScope.controlloSuperato eq 'NO-Presentazione' || requestScope.controlloSuperato eq 'NO' || requestScope.controlloOffertaDitteSuperato eq 'NO'}" definizione="T6;0;;ORA;GOESOFF" />
			</c:when>
		</c:choose>
						
		<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
		<input type="hidden" name="iterga" id="iterga" value="${iterga}" />
		<input type="hidden" name="pagina" id="pagina" value="${pagina}" />
		<input type="hidden" name="tipoGara" id="tipoGara" value="${tipoGara}" />
		<input type="hidden" name="termineVisualizzato" id="termineVisualizzato" value="" />
		<input type="hidden" name="pubblicazionePortaleBando" id="pubblicazionePortaleBando" value="${pubblicazionePortaleBando }" />
		<input type="hidden" name="pubblicazionePortaleInvito" id="pubblicazionePortaleInvito" value="${pubblicazionePortaleInvito }" />
		<input type="hidden" name="gartel" id="gartel" value="${gartel}" />
		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		<input type="hidden" name="forza" id="forza" value="${forza}" />
		<input type="hidden" name="modalitaAmministratore" id="modalitaAmministratore" value="${modalitaAmministratore}" />
		<input type="hidden" name="esisteSchedaRettificaAperta" id="esisteSchedaRettificaAperta" value="${esisteSchedaRettificaAperta}"/>
	</gene:formScheda>
  </gene:redefineInsert>

<c:if test='${(fn:contains(listaOpzioniUtenteAbilitate, "ou89#") or fn:contains(listaOpzioniUtenteAbilitate, "ou233#")) && esisteSchedaRettificaAperta eq "true" && empty forza}'>
	<gene:redefineInsert name="buttons">
		<input type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma()">&nbsp;
		<input type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
		<INPUT type="button" class="bottone-azione" value="Rettifica senza invio a ANAC" title="Rettifica senza invio a ANAC" onclick="javascript:forzaRettifica()"></td>
	</gene:redefineInsert>
</c:if>		
<c:if test='${not empty requestScope.controlloSuperato and requestScope.controlloSuperato eq "NO" and (not empty requestScope.controlloBusteSuperato and requestScope.controlloBusteSuperato eq "NO" )}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
			<c:if test='${(fn:contains(listaOpzioniUtenteAbilitate, "ou89#") or fn:contains(listaOpzioniUtenteAbilitate, "ou233#")) && esisteSchedaRettificaAperta eq "true" && empty forza}'>
				<INPUT type="button" class="bottone-azione" value="Rettifica senza invio ad ANAC" title="Rettifica senza invio ad ANAC" onclick="javascript:forzaRettifica()">&nbsp;
			</c:if>	
	</gene:redefineInsert>
</c:if>
	
	
	<gene:javaScript>
	<c:if test="${esisteSchedaRettificaAperta eq 'true' && modalitaAmministratore ne 'true'}">
		setValue("DTEPAR",'${param.DTEPAR}');
		setValue("OTEPAR",'${param.OTEPAR}');
		setValue("DTERMRICHCDP",'${param.DTERMRICHCDP}');
		setValue("DTERMRISPCDP",'${param.DTERMRISPCDP}');
		
		setValue("DTEOFF",'${param.DTEOFF}');
		setValue("OTEOFF",'${param.OTEOFF}');
		setValue("DTERMRICHCPO",'${param.DTERMRICHCPO}');
		setValue("DTERMRISPCPO",'${param.DTERMRISPCPO}');
		
		setValue("DESOFF",'${param.DESOFF}');
		setValue("OESOFF",'${param.OESOFF}');
		
		showObj("rowOFF",false);
		showObj("rowDESOFF", false);
		showObj("rowOESOFF", false);
		
		showObj("rowPDO",false);
		showObj("rowDTEOFF", false);	
		showObj("rowOTEOFF", false);
		showObj("rowDTERMRICHCDP", false);
		showObj("rowDTERMRISPCPO", false);
		
		showObj("rowPDP",false);
		showObj("rowDTEPAR", false);	
		showObj("rowOTEPAR", false);
		showObj("rowDTERMRICHCPO", false);
		showObj("rowDTERMRISPCDP", false);
		
		
	</c:if>
			
		function conferma() {
			var controlliSuperati=false;
			
			<c:choose>
				<c:when test="${pagina eq 'Datigen' and (iterga eq '2' or iterga eq '4' or iterga eq '7')}">
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
				<c:when test="${(pagina eq 'Datigen' and (iterga eq '1' or iterga eq '3' or iterga eq '5' or iterga eq '6' or iterga eq '9')) or pagina eq 'Invito'}">
					var termineVisualizzato = document.getElementById("termineVisualizzato").value;
					
					var checkOff=-2;
					if(termineVisualizzato==2){
						var dteoff_orig = getValue("DTEOFF_ORIG");
						var dteoff = getValue("DTEOFF");
						var oteoff_orig = getValue("OTEOFF_ORIG");
						var oteoff = getValue("OTEOFF");
						
						var dtermrichcpo_orig = getValue("DTERMRICHCPO_ORIG");
						var dtermrichcpo = getValue("DTERMRICHCPO");
						var dtermrispcpo_orig = getValue("DTERMRISPCPO_ORIG");
						var dtermrispcpo = getValue("DTERMRISPCPO");
						
						var checkOff = controlloDateNoAlerts(dteoff_orig,dteoff,oteoff_orig,oteoff,dtermrichcpo_orig,dtermrichcpo,dtermrispcpo_orig,dtermrispcpo);
						if(checkOff==-3){
							alert("La data e ora specificata per i termini di presentazione dell'offerta deve essere successive alla data e ora corrente")
							return;
						}
					}
					var desoff_orig = getValue("DESOFF_ORIG");
					var desoff = getValue("DESOFF");
					var oesoff_orig = getValue("OESOFF_ORIG");
					var oesoff = getValue("OESOFF");
					
					var checkPlichi = controlloDateNoAlerts(desoff_orig,desoff,oesoff_orig,oesoff,null,null,null,null);
					if(checkPlichi==-3){
						alert("La data e ora specificata per i termini apertura plichi deve essere successiva alla data e ora corrente")
						return;
					}
					
					var dataPlichiNulla = (desoff == null || desoff=="");
					var oraPlichiNulla = (oesoff == null || oesoff == "");
					if(dataPlichiNulla != oraPlichiNulla && termineVisualizzato==2){
						alert("I campi data e ora per i termini di apertura dei plichi devono essere obbligatoriamente compilati o lasciati vuoti");
						return;
					}
					
					if(checkOff==-2 && checkPlichi==-2){
						alert("Nessun dato è stato variato")
						return;
					}
					var dataPlichiOrigValorizzata = !(desoff_orig == null || desoff_orig=="");
					var oraPlichiOrigValorizzata  = !(oesoff_orig == null || oesoff_orig == "");
					if(checkOff==-2 && checkPlichi==-1 && (dataPlichiOrigValorizzata || oraPlichiOrigValorizzata)){
						alert("I campi data e ora per i termini di apertura dei plichi non possono essere sbiancati")
						return;
					}
					if(checkOff==-2 && checkPlichi==-1){
						alert("Nessun dato è stato variato")
						return;
					}
					if(checkOff==-1){
						alert("I campi data e ora per i termini di presentazione dell'offerta sono obbligatori");
						return;
					}
					
					
					
					if( false && checkPlichi==-1 && checkOff==-2){
						if(checkOff==-2)
							alert("Per procedere alla rettifica è necessario compilare i campi data e ora per i termini apertura plichi");
						if(checkOff==-1)
							alert("Per procedere alla rettifica è necessario compilare i campi data e ora per per i termini di presentazione dell'offerta e/o per i termini apertura plichi");
						return;
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
		
		function controlloDateNoAlerts(dataOriginale,data,oraOriginale,ora,dataRichiestaOriginale,dataRichiesta,dataRispostaOriginale,dataRisposta){
			if(data == null || data=="" || ora == null || ora == "") {
				return -1
			}
			
			if(!(dataOriginale!=data || oraOriginale!=ora || dataRichiestaOriginale!=dataRichiesta || dataRispostaOriginale!= dataRisposta)){
				return -2;
			}
			var dataTermine = DateDaStringa(data);
			if(ora!=null){
				var orario = ora.split(":");
				dataTermine.setHours(orario[0]);
				dataTermine.setMinutes(orario[1]);
			}
			var oggi = new Date();
			if(dataTermine < oggi){
				return -3;
			}
		}
		
		function annulla(){
			window.close();
		}
		
		<c:if test="${(pagina eq 'Datigen' and (iterga eq '1' or iterga eq '3' or iterga eq '5' or iterga eq '6' or iterga eq '9')) or pagina eq 'Invito'}">
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
					showObj("rowDESOFF", false);
					showObj("rowOESOFF", false);
					document.getElementById("termineVisualizzato").value=2;
				</c:when>
				<c:when test="${(not visualizzarePresentazioneOfferta and  visualizzareAperturaPlichi) or requestScope.controlloSuperato eq 'NO-Presentazione' || requestScope.controlloSuperato eq 'NO' || requestScope.controlloOffertaDitteSuperato eq 'NO'}">
					showObj("rowPDO",false);
					showObj("rowDTEOFF", false);	
					showObj("rowOTEOFF", false);
					showObj("rowDTERMRICHCPO", false);
					showObj("rowDTERMRISPCPO", false);
					document.getElementById("termineVisualizzato").value=3;
				</c:when>
				<c:otherwise>
					document.getElementById("termineVisualizzato").value=2;
				</c:otherwise>
			</c:choose>
		</c:if>
		
		function forzaRettifica(){
			document.getElementById("modalitaAmministratore").value="true";
			schedaConferma();
		}
	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>