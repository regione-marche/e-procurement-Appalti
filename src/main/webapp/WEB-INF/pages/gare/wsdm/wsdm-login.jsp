<%
/*
 * Created on: 17-feb-2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<tr>
	<td colspan="2">
		<c:if test="${param.gestioneVisualizzazioneContratta eq '1' }">
			<a class="left" href="javascript:showParametriUtente()"><img id="onParametriUtente" src="${param.contextPath}/img/TreeExpand.png" alt="Apri dettaglio" title="Apri dettaglio" ><img id="offParametriUtente" src="${param.contextPath}/img/TreeCollapse.png" alt="Chiudi dettaglio" title="Chiudi dettaglio" style="display: none" ></a>
			&nbsp;
		</c:if>
		<b>Parametri utente per l'inoltro delle richieste al servizio remoto</b>
	</td>
</tr>
<tr id="rigaUtente">
	<td class="etichetta-dato">Utente (*)</td>
	<td class="valore-dato"><input id="username" name="username" title="Utente" class="testo" type="text" size="24" value="" maxlength="100"/></td>
</tr>
<tr id="rigaPassword">
	<td class="etichetta-dato">Password (*)</td>
	<td class="valore-dato"><input id="password" name="password" title="Password" class="testo" type="password" size="24" value="" maxlength="100"/></td>
</tr>
<tr id="rigaRuolo">
	<td class="etichetta-dato">Ruolo (*)</td>
	<td class="valore-dato">
		<select id="ruolo" name="ruolo"></select>
		<input id="ruolovisualizza" name="ruolovisualizza" title="Ruolo" class="testo" type="text" size="24" value="" maxlength="20"/>
	</td>
</tr>
<tr id="rigaNome">
	<td class="etichetta-dato">Nome (*)</td>
	<td class="valore-dato"><input id="nome" name="nome" title="Nome" class="testo" type="text" size="24" value="" maxlength="20"/></td>
</tr>
<tr id="rigaCognome">
	<td class="etichetta-dato">Cognome (*)</td>
	<td class="valore-dato"><input id="cognome" name="cognome" title="Cognome" class="testo" type="text" size="24" value="" maxlength="20"/></td>
</tr>
<tr id="rigaUnitaOrganizzativa">
	<td class="etichetta-dato">Codice unit&agrave; organizzativa (*)</td>
	<td class="valore-dato">
		<select id="codiceuo" name="codiceuo"></select>
		<input id="codiceuovisualizza" name="codiceuovisualizza" title="Codice UO" class="testo" type="text" size="24" value="" maxlength="20"/>
	</td>
</tr>
<tr id="rigaIdUtente">
	<td class="etichetta-dato">Identificativo utente (*)</td>
	<td class="valore-dato"><input id="idutente" name="idutente" title="Identificativo utente" class="testo" type="text" size="24" value="" maxlength="20"/></td>
</tr>
<tr id="rigaIdUtenteUnitaOperativa">
	<td class="etichetta-dato">Identificativo unit&agrave; operativa (*)</td>
	<td class="valore-dato"><input id="idutenteunop" name="idutenteunop" title="Identificativo unit&agrave; operativa" class="testo" type="text" size="24" value="" maxlength="20"/></td>
</tr>
