<%
/*
 * Created on: 25-03-2011
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
		Finestra per l'acquisizione puntale dei messaggi 'FS2' e 'FS5' provenienti da Potale Alice
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.acquisizioneEseguita and requestScope.acquisizioneEseguita eq "1"}' >
<script type="text/javascript">
		window.opener.document.forms[0].pgSort.value = "";
		window.opener.document.forms[0].pgLastSort.value = "";
		window.opener.document.forms[0].pgLastValori.value = "";
		window.opener.bloccaRichiesteServer();
		window.opener.listaVaiAPagina(0);
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">


<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.idcom}'>
		<c:set var="idcom" value="${param.idcom}" />
	</c:when>
	<c:otherwise>
		<c:set var="idcom" value="${idcom}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.tipo}'>
		<c:set var="tipo" value="${param.tipo}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipo" value="${tipo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare")}'>
		<c:set var="tipologia" value="elenco"/>
	</c:when>
	<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_CATALDITTE-lista.ApriGare")}'>
		<c:set var="tipologia" value="catalogo"/>
	</c:when>
</c:choose>

<c:set var="comkey1" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetCOMKEYGaraLottoFunction",pageContext,"PA",idcom)}' />

<c:if test='${tipo eq "FS4"}'>
	<c:set var="esito" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlloVariazioniAggiornamentoDaPortaleFunction", pageContext, ngara,idcom,comkey1,tipo)}' />
	<gene:setString name="titoloMaschera" value='Acquisizione aggiornamento iscrizione a ${tipologia } da portale Appalti' />
</c:if>
<c:if test='${tipo eq "FS2"}'>
	<gene:setString name="titoloMaschera" value='Acquisizione iscrizione a ${tipologia } da portale Appalti' />
</c:if>


<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="IMPR" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAcquisizionePuntualeDaPortale">
	
	<gene:campoScheda>
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${requestScope.erroreAcquisizione eq "1"}'>
					&nbsp;Ci sono stati degli errori durante l'acquisizione.${requestScope.msg}
				</c:when>
				<c:when test='${esito eq "-1"}'>
					&nbsp;Non è possibile procedere con l'acquisizione perchè ci sono altre richieste, da parte dello stesso richiedente <br>
					&nbsp;e per lo stesso ${tipologia }, precedenti a quella corrente e non ancora acquisite.<br>
				</c:when>
				<c:when test='${esito eq "-3"}'>
					&nbsp;Non è possibile procedere con l'acquisizione perchè il richiedente non risulta presente in elenco.<br>
				</c:when>
				<c:when test='${esito eq "-4"}'>
					&nbsp;Non è possibile procedere con l'acquisizione perchè il richiedente risulta mandatario di più RT in elenco.<br>
				</c:when>
				<c:when test='${esito eq "1" and not empty requestScope.messaggi }'>
					&nbsp;Sono presenti le seguenti variazioni:
				</c:when>
				<c:when test='${esito eq "1" and empty requestScope.messaggi }'>
					&nbsp;&nbsp;Non sono presenti variazioni.
				</c:when>
				<c:when test='${esito eq "-5"}'>
					&nbsp;Non è possibile procedere con l'acquisizione perchè, per l'operatore richiedente, devono prima essere confermate <br>
					&nbsp;precedenti richieste di modifica alle categorie d'iscrizione. <br><br>
					&nbsp;Accedere alla form di dettaglio delle categorie d'iscrizione dell'operatore richiedente per accettare o rifiutare le modifiche richieste.<br>
				</c:when>
				<c:otherwise>
					&nbsp;Confermi l'acquisizione dal portale Appalti?
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
	
	<c:if test='${tipo eq "FS4" and not empty requestScope.messaggi}'>
		<gene:campoScheda>
			<td colSpan="2">
				<textarea cols="90" rows="14" readonly="readonly">${requestScope.messaggi }</textarea>
			</td>
		</gene:campoScheda>	
	</c:if>
	
		
		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		<input type="hidden" name="idcom" id="idcom" value="${idcom}" />
		<input type="hidden" name="comkey1" id="comkey1" value="${comkey1}" />
		<input type="hidden" name="tipo" id="tipo" value="${tipo}" />
		<input type="hidden" name="WIZARD_PAGINA_ATTIVA" id="WIZARD_PAGINA_ATTIVA" value="-50" />
		<input type="hidden" name="messagggioCategorie" id="messagggioCategorie" value="${msgCategorie}" />
		<input type="hidden" name="saltareAggCateg" id="saltareAggCateg" value="${saltareAggCateg}" />
	</gene:formScheda>
  </gene:redefineInsert>

<c:if test='${requestScope.erroreAcquisizione eq "1" || esito eq "-1" || esito eq "-3" || esito eq "-4" || esito eq "-5"}' >
	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;
	</gene:redefineInsert>
</c:if>
	
	<gene:javaScript>
		
		
		function conferma() {
			document.forms[0].jspPathTo.value="gare/commons/popupIscriviAggiornaDaPortale.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}
		
		function chiudi(){
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
			window.close();	
		}
		
	

	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>