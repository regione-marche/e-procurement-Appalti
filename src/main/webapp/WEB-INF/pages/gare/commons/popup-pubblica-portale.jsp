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
	<script type="text/javascript">
			opener.historyReload();
			window.close();
	</script>
	</c:when>
	<c:otherwise>
<gene:template file="popup-message-template.jsp">

<c:set var="modo" value="NUOVO" scope="request" />

<c:set var="codiceGara" value="${param.codiceGara}" scope="request" />
<c:set var="ngara" value="${param.ngara}" scope="request" />
<c:set var="gruppo" value="${param.gruppo}" scope="request" />
<c:set var="entita" value="${param.entita}" scope="request" />
<c:set var="genereGara" value="${param.genereGara}" scope="request" />
<c:set var="idconfi" value="${param.idconfi}" scope="request" />
<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codiceGara, idconfi)}' />
<c:set var="notElencoCatalogoAvviso" value='${genereGara ne "10" and genereGara ne "20" and genereGara ne "11"}'/>

<gene:redefineInsert name="corpo">
<gene:formScheda entita="GARE" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestorePopUpPubblicaSuPortale">
	<gene:setString name="titoloMaschera" value='Pubblica su portale Appalti' />
	<c:choose>
	<c:when test='${error eq "true"}'>
	<gene:campoScheda>
		<td colspan="2">
			<br>
			${requestScope.errorMsg}
			<br>
			<br>
			<gene:redefineInsert name="buttons">
			<INPUT type="button" id="pulsanteAnnulla" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
			</gene:redefineInsert>
		</td>
		</gene:campoScheda>	
	</c:when>
	<c:otherwise>
		<gene:campoScheda>
			<td colspan="2">
				Mediante questa funzione si procede alla pubblicazione su portale Appalti dei dati ${genere}.
				<br>Specificare l'oggetto di pubblicazione:<br><br>
				<c:if test='${deliberaVisibile}'>
				<input type="radio" value="1" name="tipoPubblicazione" id="tipoPubblicazione" <c:if test="${deliberaDisabilitata}">disabled="true"</c:if> onclick="javascript:cambiaTipoPubblicazione('1');" />
						<span><b>Delibera a contrarre o atto equivalente</b></span>
						<c:choose>
							<c:when test='${notElencoCatalogoAvviso}'>
								<div style="padding-left: 26px;"><i>Pubblica i documenti relativi alla delibera a contrarre o atto equivalente 
									(opzione disponibile fino alla pubblicazione in area pubblica o riservata della gara).</i></div>
							</c:when>
							<c:otherwise>
								<br>
							</c:otherwise>
						</c:choose>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br>
				</c:if>
				<c:if test='${bandoVisibile}'>
				<input type="radio" value="2" name="tipoPubblicazione" id="tipoPubblicazione" <c:if test="${bandoDisabilitato}">disabled="true"</c:if> onclick="javascript:cambiaTipoPubblicazione('2');" />
						<span><b>Bando o avviso</b></span>
						<c:choose>
							<c:when test='${notElencoCatalogoAvviso}'>
								<div style="padding-left: 26px;"><i>Pubblica la gara con i documenti del bando o avviso.
									Effettua inoltre la pubblicazione dell'eventuale delibera a contrarre o atto equivalente 
									e degli altri atti e documenti inseriti nel sistema ma non ancora pubblicati.</i></div>
							</c:when>
							<c:otherwise>
								<br>
							</c:otherwise>
						</c:choose>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br>
				</c:if>
				<c:if test='${invitoVisibile}'>
				<input type="radio" value="3" name="tipoPubblicazione" id="tipoPubblicazione" <c:if test="${invitoDisabilitato}">disabled="true"</c:if>  onclick="javascript:cambiaTipoPubblicazione('3');" />
						<span><b>Invito in area riservata</b></span>
						<c:choose>
							<c:when test='${notElencoCatalogoAvviso}'>
								<div style="padding-left: 26px;"><i>Pubblica la gara nell'area riservata agli operatori invitati con i documenti della lettera di invito. 
									Effettua inoltre la pubblicazione dell'eventuale delibera a contrarre o atto equivalente 
									e degli altri atti e documenti inseriti nel sistema ma non ancora pubblicati.</i></div>
							</c:when>
							<c:otherwise>
								<br>
							</c:otherwise>
						</c:choose>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br>
				</c:if>
				<c:if test='${invitoComunicazioneVisibile}'>
				<input type="radio" value="4" name="tipoPubblicazione" id="tipoPubblicazione" <c:if test="${invitoComunicazioneDisabilitato}">disabled="true"</c:if> onclick="javascript:cambiaTipoPubblicazione('4');" />
						<span><b>Invito in area riservata con invio comunicazione</b></span>
						<c:choose>
							<c:when test='${notElencoCatalogoAvviso}'>
								<div style="padding-left: 26px;"><i>Invia gli inviti agli operatori economici tramite mail o PEC e pubblica la gara in area riservata con i documenti della lettera di invito.
									Effettua inoltre la pubblicazione dell'eventuale delibera a contrarre o atto equivalente 
									e degli altri atti e documenti inseriti nel sistema ma non ancora pubblicati.</i></div>
							</c:when>
							<c:otherwise>
								<br>
							</c:otherwise>
						</c:choose>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br>
				</c:if>
				<c:if test='${esitoVisibile }'>
				<input type="radio" value="5" name="tipoPubblicazione" id="tipoPubblicazione" <c:if test="${esitoDisabilitato}">disabled="true"</c:if> onclick="javascript:cambiaTipoPubblicazione('5');" />
						<span><b>Esito di gara</b></span>
						<c:choose>
							<c:when test='${notElencoCatalogoAvviso}'>
								<div style="padding-left: 26px;"><i>Pubblica l'esito della gara con i relativi documenti.
									Effettua inoltre la pubblicazione dell'eventuale delibera a contrarre o atto equivalente 
									e degli altri atti e documenti inseriti nel sistema ma non ancora pubblicati (non considera i documenti del bando o avviso e della lettera di invito).</i></div>
							</c:when>
							<c:otherwise>
								<br>
							</c:otherwise>
						</c:choose>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br>
				</c:if>
				<c:if test='${trasparenzaVisibile and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.PubblicaAmministrazioneTrasparente")}'>
				<input type="radio" value="7" name="tipoPubblicazione" id="tipoPubblicazione"  <c:if test="${trasparenzaDisabilitato}">disabled="true"</c:if> onclick="javascript:cambiaTipoPubblicazione('7');" />
						<span><b>Pubblicazione per la trasparenza</b></span>
						<c:choose>
							<c:when test='${notElencoCatalogoAvviso}'>
								<div style="padding-left: 26px;"><i>Pubblica i documenti inseriti nella tipologia "Documento per la trasparenza".</i></div>
							</c:when>
							<c:otherwise>
								<br>
							</c:otherwise>
						</c:choose>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br>
				</c:if>
				<c:if test='${true }'>
				<input type="radio" value="6" name="tipoPubblicazione" id="tipoPubblicazione"  <c:if test="${integrazioneDisabilitata}">disabled="true"</c:if> onclick="javascript:cambiaTipoPubblicazione('6');" />
						<span><b>Integrazione di documenti
						<c:if test='${notElencoCatalogoAvviso}'>
							 e pubblicazione altri atti
						</c:if>
						</b></span>
						<c:choose>
							<c:when test='${notElencoCatalogoAvviso}'>
								<div style="padding-left: 26px;"><i>Pubblica le integrazioni e le rettifiche dei documenti già pubblicati in precedenza.
									Effettua inoltre la pubblicazione dell'eventuale delibera a contrarre o atto equivalente 
									e degli altri atti e documenti inseriti nel sistema ma non ancora pubblicati.</i></div>
							</c:when>
							<c:otherwise>
								<br>
							</c:otherwise>
						</c:choose>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br>
				</c:if>
				<br>
			</td>
		</gene:campoScheda>	
	</c:otherwise>
	</c:choose>
	
	</gene:formScheda>
	</gene:redefineInsert>
	<gene:javaScript>
	
	function cambiaTipoPubblicazione(){}
	
	function conferma(){
			
		var controllo = false;
		var value;
		var tipoPubblicazione = document.forms[1].tipoPubblicazione;
		
		if (tipoPubblicazione.checked) {
			controllo=true;
		}
		
		for(i=0; i < tipoPubblicazione.length; i++) {
			if(tipoPubblicazione[i].checked) {
				controllo=true;
				value = tipoPubblicazione[i].value;
			}
		}
		
		if(!controllo) {
				alert("Selezionare una tipologia");
			}
		
		if (controllo) {
			if(value == 1){
				//DELIBERA A CONTRARRE
				reloadPopup.href.value = "gare/commons/popupPubblicaDelibere.jsp";
			}
			if(value == 2){
				//BANDO
				reloadPopup.href.value = "gare/commons/popupPubblicaSuPortale.jsp";
				reloadPopup.bando.value = 1;
			}
			if(value == 3){
				//INVITO
				reloadPopup.href.value = "gare/commons/popupPubblicaSuPortale.jsp";
				reloadPopup.bando.value = 3;
			}
			if(value == 4){
				//INVITO COMUNICAZIONE
				reloadPopup.href.value = "gare/commons/popupPubblicaSuPortale.jsp?step=1";
				reloadPopup.bando.value = 3;
			}
			if(value == 5){
				//ESITO
				reloadPopup.href.value = "gare/commons/popupPubblicaSuPortale.jsp";
				reloadPopup.bando.value = 0;
			}
			if(value == 6){
				//INTEGRAZIONE DOCUMENTI
				reloadPopup.href.value = "gare/commons/popupPubblicaIntegrazioni.jsp";
			}
			if(value == 7){
				//TRASPARENZA 
				reloadPopup.href.value = "gare/commons/popupPubblicaSuAmministrazioneTrasparente.jsp";
			}
				reloadPopup.tipoPubblicazione.value = value;
				reloadPopup.submit();
			}
	}
	
	function inizializzaRadioForm(){
		var controllo = false;
		var tipoPubblicazione = document.forms[1].tipoPubblicazione;
		if (tipoPubblicazione.checked) {
			controllo=true;
		}
		for(i=0; i < tipoPubblicazione.length && controllo == false; i++) {
			if(!tipoPubblicazione[i].disabled) {
				controllo=true;
				tipoPubblicazione[i].setAttribute("checked","true");
			}
		}
	}
	inizializzaRadioForm();
	
	function annulla(){
			window.close();
		}
	
	</gene:javaScript>
	<form name="reloadPopup" id="reloadPopup" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="gare/commons/temp.jsp" />
		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		<input type="hidden" name="genere" id="genere" value="${genereGara}" />
		<input type="hidden" name="codgar" id="codgar" value="${codiceGara}" />
		<input type="hidden" name="isProceduraTelematica" id="isProceduraTelematica" value="${param.isProceduraTelematica}" />
		<input type="hidden" name="bando" id="bando" value="1" />
		<input type="hidden" name="entita" id="entita" value="${entita}"/>
		<input type="hidden" name="valtec" id="valtec" value="${param.valtec}" />
		<input type="hidden" name="integrazioneWSDM" id="integrazioneWSDM" value="${integrazioneWSDM}" />
		<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}" />
		<input type="hidden" name="tipoPubblicazione" id="tipoPubblicazione" value="" />
	</form>
</gene:template>

	</c:otherwise>
</c:choose>
