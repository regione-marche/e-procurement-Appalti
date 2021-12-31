<%
/*
 * Created on 20-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI EDIT DEL
 // DETTAGLIO DEI DATI GENERALI DI UN ACCOUNT CONTENENTE LA SEZIONE RELATIVA
 // ALLE SOTTOSEZIONI DELLA PAGINA STESSA.
 // QUESTA PAGINA E' STATA RIDEFINITA NEL PROGETTO PL-WEB PER UNA
 // PERSONALIZZAZIONE DELL?EDIT DEI DATI GENERALI DELL'ACCOUNT.
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="listaOpzioniUtenteSys" value="${fn:join(accountForm.opzioniUtenteSys,'#')}#" />
<c:set var="account" value="${accountForm}"/>
<c:set var="integrazioneLavori" value='${gene:isTable(pageContext,"APPA")}' />

<c:if test='${moduloAttivo eq "PG" }'>
	<tr>
		<td colspan="2">
			<b>Configurazione Gare</b>
		</td>
	</tr>
	<tr>
		<td class="etichetta-dato">Privilegi dell'utente sulle gare</td>
		<td class="valore-dato">
			<html:select property="abilitazioneGare" name="accountForm" styleId="abilitazioneGare">
				<html:options property="listaValueLavori" labelProperty="listaTextGare"/>
			</html:select>
			<div class="info-wizard" id="warning-abilitazioneGare">ATTENZIONE: un utente "Amministratore di sistema" dovrebbe avere accesso solo alle funzionalità amministrative. Privilegi superiori a quello "utente" implicano una non conformit&agrave; alle norme (GDPR, AGID, ...).</div>
		  			<gene:javaScript>
		  			
		  			$(document).ready(function() {
		  				// questo serve per l'impostazione della visualizzazione del messaggio per Appalti in apertura della pagina 
		  				var amministratoreSistema = $('#opzioniUtenteSys-ou89').is(':checkbox') ? $('#opzioniUtenteSys-ou89').prop("checked") : $('#opzioniUtenteSys-ou89').val()=='ou89';
		  				if (!amministratoreSistema || $("#abilitazioneGare").val() != 'A') {
		  					$('#warning-abilitazioneGare').hide();
		  				}
		  				// questo serve per attivare alla modifica del valore la gestione del messaggio per Appalti
		  				$('#abilitazioneGare').change(function() {
		  					showHideWarning($(this), '#warning-abilitazioneGare');
		  				});
		  				<c:if test='${moduloAttivo eq "PL" || (moduloAttivo eq "PG" && integrazioneLavori eq true)}'>
		  				// questo serve per l'impostazione della visualizzazione del messaggio per Lavori in apertura della pagina 
		  				if (!amministratoreSistema || $("#abilitazioneLavori").val() != 'A') {
		  					$('#warning-abilitazioneLavori').hide();
		  				}
		  				// questo serve per attivare alla modifica del valore la gestione del messaggio per Lavori
		  				$('#abilitazioneLavori').change(function() {
		  					showHideWarning($(this), '#warning-abilitazioneLavori');
		  				});
		  				</c:if>
		  				// questo serve per triggerare i 2 messaggi per Appalti e Lavori quando cambia l'opzione di amministratore di sistema
		  				$('#opzioniUtenteSys-ou89').click(function() {
		  				 	showHideWarning($('#abilitazioneGare'), '#warning-abilitazioneGare');
		  				 	<c:if test='${moduloAttivo eq "PL" || (moduloAttivo eq "PG" && integrazioneLavori eq true)}'>
		  				 	showHideWarning($('#abilitazioneLavori'), '#warning-abilitazioneLavori');
		  				 	</c:if>
		  				});
		  			});

	  				function showHideWarning(obj, idWarning) {
	  				 	var amministratoreSistema = $('#opzioniUtenteSys-ou89').is(':checkbox') ? $('#opzioniUtenteSys-ou89').prop("checked") : $('#opzioniUtenteSys-ou89').val()=='ou89';
	  				 	if (!amministratoreSistema || obj.val() != 'A') {
	  				 		$(idWarning).hide();
	  				 	} else {
	  				 		$(idWarning).show();
	  				 	}
		  			}

		  			</gene:javaScript>
			
		</td>
	</tr>
	<tr>
		<td class="etichetta-dato">Ruolo per procedure telematiche e mercato elettronico</td>
		<td class="valore-dato">
	      	<html:select property="ruoloUtenteMercatoElettronico" >
	      		<html:option value="">&nbsp;</html:option>
		      	<html:options collection="listaRuoliME" property="tipoTabellato" labelProperty="descTabellato" />
	      	</html:select>
		  </td>
	</tr>
</c:if>

<c:if test='${moduloAttivo eq "PL" || (moduloAttivo eq "PG" && integrazioneLavori eq true)}'>
	<tr>
		<td colspan="2">
			<b>Configurazione Lavori</b>
		</td>
	</tr>
	<tr>
		<td class="etichetta-dato">Privilegi dell'utente sui lavori</td>
		<td class="valore-dato">
			<html:select property="abilitazioneLavori" name="accountForm"  styleId="abilitazioneLavori">
				<html:options property="listaValueLavori" labelProperty="listaTextLavori"/>
			</html:select>
			<div class="info-wizard" id="warning-abilitazioneLavori">ATTENZIONE: un utente "Amministratore di sistema" dovrebbe avere accesso solo alle funzionalità amministrative. Privilegi superiori a quello "utente" implicano una non conformit&agrave; alle norme (GDPR, AGID, ...).</div>
		</td>
	</tr>
</c:if>


<% 
// Valori di default dei campi SYSLIV, SYSLIG, SYSABG, SYSLIC e SYSABC
// della tabella USRSYS
%>

<c:if test='${not empty account.livelloLavori}'>
	<input type="hidden" name="livelloLavori" value="${account.livelloLavori}"/>
</c:if>
<c:if test='${not empty account.abilitazioneLavori}'>
	<input type="hidden" name="abilitazioneLavori" value="${account.abilitazioneLavori}"/>
</c:if>
<c:if test='${not empty account.livelloGare}'>
	<input type="hidden" name="livelloGare" value="${account.livelloGare}"/>
</c:if>
<c:if test='${not empty account.abilitazioneGare}'>
	<input type="hidden" name="abilitazioneGare" value="${account.abilitazioneGare}"/>
</c:if>
<c:if test='${not empty account.livelloContratti}'>
	<input type="hidden" name="livelloContratti" value="${account.livelloContratti}"/>
</c:if>
<c:if test='${not empty account.abilitazioneContratti}'>
	<input type="hidden" name="abilitazioneContratti" value="${account.abilitazioneContratti}"/>
</c:if>

<script type="text/javascript">
<!--
// funzione richiamata per eseguire i controlli di obbligatorietà sulla sezione.
// aggiungere eventuali controlli di obbligatorietà del caso 
function eseguiControlliSezioneCustomSalva() {
	return true;
}
-->
</script>
