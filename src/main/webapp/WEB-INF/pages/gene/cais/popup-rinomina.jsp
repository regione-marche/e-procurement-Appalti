<%--

/*
 * Created on: 07-06-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

Finestra per gestire la rinominazione di una categoria
--%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<c:choose>
	<c:when test='${not empty RISULTATO and RISULTATO eq "OK"}' >
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


<c:set var="id" value='${gene:getValCampo(param.chiaveRiga, "CAISIM")}'/>

<c:set var="modo" value="NUOVO" scope="request" />

<c:set var="esistonoComunicazioni" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoComunicazioniIscrAggFunction", pageContext, id)}' />

<gene:template file="popup-message-template.jsp">
	<gene:setString name="titoloMaschera" value='Rinomina categoria ${id}' />
	<c:if test="${esistonoComunicazioni  ne 'true' }">
		<c:set var="isCategoriaAdoperata" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.CheckIsCategoriaAdoperataInPGPLFunction",  pageContext,id,"Si")}' />
	</c:if>
	
	<gene:redefineInsert name="corpo" >
		<gene:formScheda entita="CAIS" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreRinominaCategoria">
			<gene:campoScheda>
				<td colSpan="2">
					<br>
					<c:if test="${esistonoComunicazioni eq 'true' }">
						<b>Non è possibile procedere alla rinominazione.</b>
						<br>E' necessario prima elaborare le richieste di iscrizione e aggiornamento a elenco o catalogo da portale
						<br><br>
					</c:if>
					<c:if test="${isCategoriaAdoperata=='true' }">
						${messaggio }
						<br>
					</c:if>
				</td>
			</gene:campoScheda>
			
			
			<gene:campoScheda campo="NEWCODICE" title="Nuovo codice" campoFittizio="true" definizione="T30;0;;;CAISIM" modificabile="${RISULTATO ne 'NOK' }">
				<gene:checkCampoScheda funzione='"##".indexOf("/") < 0 && "##".indexOf(". ") < 0 && "##".indexOf(" .") < 0' obbligatorio="true" messaggio="Il carattere / e le sequenze ' .' e '. ' non sono accettate nel valore del campo codice categoria" />
			</gene:campoScheda>
			
			<gene:campoScheda>
				<td colSpan="2">
					<c:if test="${RISULTATO ne 'NOK' && esistonoComunicazioni ne 'true'}">
					<br>
					Confermi la rinomina?
					<br>
					<br>
					</c:if>
				</td>
			</gene:campoScheda>
			
			<input type="hidden" name="codice" id="codice" value="${id}">
			
		</gene:formScheda>
	</gene:redefineInsert>
	<c:if test="${RISULTATO == 'NOK' || esistonoComunicazioni eq 'true'}" >
		<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
		</gene:redefineInsert>
	</c:if>
	<gene:javaScript>
	function conferma(){
		document.forms[0].jspPathTo.value="gene/cais/popup-rinomina.jsp";
		schedaConferma();
	}
	
	function annulla(){
		window.close();
	}
	</gene:javaScript>
</gene:template>
</c:otherwise>
</c:choose>