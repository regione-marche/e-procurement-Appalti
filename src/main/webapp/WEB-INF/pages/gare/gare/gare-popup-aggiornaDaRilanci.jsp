
<%
	/*
	 * Created on 11-10-2018
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


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
<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.isGaraLottiConOffertaUnica}'>
		<c:set var="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
	</c:when>
	<c:otherwise>
		<c:set var="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.risultatiPerPagina}'>
		<c:set var="risultatiPerPagina" value="${param.risultatiPerPagina}" />
	</c:when>
	<c:otherwise>
		<c:set var="risultatiPerPagina" value="${risultatiPerPagina}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.modlicg}'>
		<c:set var="modlicg" value="${param.modlicg}" />
	</c:when>
	<c:otherwise>
		<c:set var="modlicg" value="${modlicg}" />
	</c:otherwise>
</c:choose>

<c:if test="${RISULTATO ne 'NOK' }" >
	<c:set var="esistonoAcquisizioniOfferteDaElaborareFS11C" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, ngara, "FS11C" )}' />
	<c:if test="${esistonoAcquisizioniOfferteDaElaborareFS11C ne 'true'}">
		<c:set var="esistonoDitteAmmissioneNulla" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteAmmissioneNullaFunction", pageContext, ngara,"60")}' />
		<c:if test="${esistonoDitteAmmissioneNulla ne 'true' }">
			<c:if test="${modlicg eq '6' }">
				<c:set var="esistonoDitteConPunteggio" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePunteggioValorizzatoFunction", pageContext, ngara, "2" )}' />
				<c:if test="${esistonoDitteConPunteggio ne 'si' }">
					<c:set var="controlloValnum" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.ControlloValorizzazioneValnumFunction", pageContext, ngara)}' />
				</c:if>
			</c:if>
		</c:if>
	</c:if>
</c:if>
<c:set var="controlliSuperati" value="true"/>
<c:if test="${esistonoAcquisizioniOfferteDaElaborareFS11C eq 'true' or  esistonoDitteAmmissioneNulla eq 'true' or esistonoDitteConPunteggio eq 'si' or controlloValnum eq 'nok' }">
	<c:set var="controlliSuperati" value="false"/>
</c:if>

<c:set var="where" value="GARE.PRECED='${ngara }' and GARE.ESINEG is null and exists (select CODGAR9 from PUBBLI where PUBBLI.CODGAR9=GARE.CODGAR1 and (PUBBLI.TIPPUB=11 or PUBBLI.TIPPUB=13))"/>
<c:set var="modo" value="MODIFICA" scope="request" />

<gene:template file="popup-template.jsp" gestisciProtezioni="false" >
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>	
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/controlliFormali.js"></script>	
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Aggiornamento offerte economiche da rilancio" />
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		
		<br>
		<c:choose>
			<c:when test="${controlliSuperati eq 'true' and RISULTATO ne 'NOK'}">
				Nella lista sottostante sono riportate le gare effettuate per il rilancio delle offerte economiche della gara corrente.<br/>
				Selezionare la gara da cui acquisire il rilancio dell'offerta economica. La gara deve essere in fase di apertura offerte economiche. 
				<br>
			</c:when>
			<c:when test="${RISULTATO eq 'NOK'}">
				Non è possibile procedere all'aggiornamento.
				${mgsErr }
				<br><br>
			</c:when>
			<c:otherwise>
				Per procedere con l'aggiornamento delle offerte economiche in seguito a rilancio,   
				<c:choose>
					<c:when test="${esistonoAcquisizioniOfferteDaElaborareFS11C eq 'true' }">
						deve essere prima completata l'acquisizione delle buste economiche nella gara corrente. 
					</c:when>
					<c:when test="${esistonoDitteAmmissioneNulla eq 'true' }">
						deve essere specificato lo stato di ammissione per tutte le ditte in gara.
					</c:when>
					<c:when test="${esistonoDitteConPunteggio eq 'si' }">
						deve essere annullato il calcolo dei punteggi dei criteri di valutazione della busta economica. Attivare la funzione 'Annulla calcolo punteggi' nella fase corrente. 
					</c:when>
					<c:otherwise>
						deve essere prima completata la compilazione del dettaglio valutazione per tutte le ditte in gara.
					</c:otherwise>
				</c:choose>
				<br><br>
			</c:otherwise>
		</c:choose>
		<br/>
		
		<table class="lista">
			<c:if test="${controlliSuperati eq 'true' and (RISULTATO eq 'OK' or empty RISULTATO)  }">
			<tr>
				<td><gene:formLista entita="GARE" pagesize="${risultatiPerPagina}" tableclass="datilista" gestisciProtezioni="false" sortColumn="-2" where="${where}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAggiornaDaRilanci">
					<gene:campoLista title=""	width="20">
						<c:if test="${currentRow >= 0 }">
							<input type="radio" value="${datiRiga.GARE_NGARA}" name="keyRilancio" id="keys${currentRow}"  onclick="javascript:impostaRilancio('${datiRiga.GARE_NGARA}');"  <c:if test="${datiRiga.GARE_FASGAR ne 6}">disabled="disabled"</c:if>/>
						</c:if>
					</gene:campoLista>
										
					<gene:campoLista campo="NGARA" width="80" ordinabile="true"/>
					<gene:campoLista campo="NOT_GAR" ordinabile="true"/>
					<gene:campoLista campo="DTEOFF" title="Data termine pres.off." entita="TORN" where="TORN.CODGAR=GARE.CODGAR1" ordinabile="true"  width="80"/>
					<gene:campoLista campo="OTEOFF" title="Ora" entita="TORN" where="TORN.CODGAR=GARE.CODGAR1" ordinabile="true"  width="70"/>
					<gene:campoLista campo="FASGAR" ordinabile="true"/>
										
					<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
                    <input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
                    <input type="hidden" name="risultatiPerPagina" id="risultatiPerPagina" value="${risultatiPerPagina}" />
                    <input type="hidden" name="garaRilancioSelezionata" id="garaRilancioSelezionata" value="" />
                    <input type="hidden" name="modlicg" id="modlicg" value="${modlicg}" />
                </gene:formLista></td>
			</tr>
			</c:if>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<c:choose>
						<c:when test="${datiRiga.rowCount > 0 and controlliSuperati eq 'true' and (RISULTATO eq 'OK' or empty RISULTATO)  }">
							<INPUT type="button"  id="Aggiungi" class="bottone-azione" value='Conferma' title='Conferma' onclick="javascript:aggiornaOfferte();">&nbsp;&nbsp;
							<INPUT type="button"  id="Chiudi" class="bottone-azione" value='Annulla' title='Annulla' onclick="javascript:chiudi();">&nbsp;
						</c:when>
						<c:otherwise>
							<INPUT type="button"  id="Chiudi" class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:chiudi();">&nbsp;
						</c:otherwise>
					</c:choose>
					
				</td>
			</tr>			
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		
		function aggiornaOfferte(){
			var garaRilancioSelezionata = $("#garaRilancioSelezionata").val();
			if (garaRilancioSelezionata==null || garaRilancioSelezionata=="") {
	      		alert("Selezionare una gara nella lista");
	      	} else {
				listaConferma();
 			}
		}
		
		function impostaRilancio(ngara){
			$("#garaRilancioSelezionata").val(ngara);
		}
		
		function chiudi(){
			window.close();
		}
	</gene:javaScript>
</gene:template>
</c:otherwise>
</c:choose>