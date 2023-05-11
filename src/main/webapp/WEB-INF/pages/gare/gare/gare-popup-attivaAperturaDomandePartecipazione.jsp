<%
/*
 * Created on: 01-07-2016
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


<c:choose>
	<c:when test='${not empty requestScope.operazioneEseguita and requestScope.operazioneEseguita eq "OK"}' >
<script type="text/javascript">
		window.opener.bloccaRichiesteServer();
		window.opener.avanti();
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
			<c:when test='${not empty param.codgar}'>
				<c:set var="codgar" value="${param.codgar}" />
			</c:when>
			<c:otherwise>
				<c:set var="codgar" value="${codgar}" />
			</c:otherwise>
		</c:choose>
		
			
		<gene:redefineInsert name="addHistory" />
		<gene:redefineInsert name="gestioneHistory" />
		<gene:setString name="titoloMaschera" value='Attiva apertura domande di partecipazione' />
		
		<c:set var="isSuperataDataTerminePresentazione" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsSuperataDataTerminePresentazioneFunction", pageContext, ngara, "2")}' />
		<c:set var="esistonoAcquisizioniOfferteDaElaborareFS10" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, ngara, "FS10" )}' />
		<c:set var="esisteBloccoFasi" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoCondizioniFasiAcquisizioniFunction",  pageContext, ngara, "-5")}'/>
				
		<c:set var="funzioneAbilitata" value='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIGARA.AttivaAperturaDomandePart")}'/>
		<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_TORN")}' />
		<c:if test='${!empty (filtroLivelloUtente)}'>
			<c:set var="autotizzatoModifica" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetAutorizzatoModificaFunction", pageContext, "CODGAR", codgar, "2")}'/>
		</c:if>
		<c:if test="${ !fn:startsWith(codgar, '$')}">
			<c:set var="esistonoDitteInGara" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteInGaraFunction", pageContext, "CODGAR5", codgar,"")}' />
		</c:if>
		
		<c:set var="modo" value="NUOVO" scope="request" />
		
		<gene:redefineInsert name="corpo">
			<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAperturaDomandePart">
				<gene:campoScheda>
					<td colSpan="2">
						<c:choose>
							<c:when test="${isSuperataDataTerminePresentazione == 'false'}">
								<br>
								Non &egrave; possibile procedere alla fase di apertura delle domande di partecipazione perch&egrave; non &egrave; ancora scaduto il termine di presentazione.
								<br>
								<br>
								Il termine per la presentazione delle domande di partecipazione scade il giorno <b>${dataScadenza}</b> alle ore <b>${oraScadenza}</b>.
								<br>
								<br>
							</c:when>
							<c:when test="${esistonoAcquisizioniOfferteDaElaborareFS10 eq 'true'}">
								<br>
								Non &egrave; possibile procedere alla fase di apertura delle domande di partecipazione perch&egrave; devono essere prima acquisite le domande di partecipazione da portale Appalti.
								<br>
								Attivare la funzione 'Acquisisci domande di partecipazione da portale Appalti' nella pagina corrente.
								<br>
								<br>
							</c:when>
							<c:when test="${esistonoDitteInGara eq 'false' and !fn:startsWith(codgar, '$')}">
								<br>
								Non &egrave; possibile procedere alla fase di apertura delle domande di partecipazione perch&egrave; non ci sono ditte in gara.
								<br>
								<br>
							</c:when>
							<c:when test="${ !funzioneAbilitata || esisteBloccoFasi eq 'true' || autotizzatoModifica eq 'false'}">
								<br>
								Non &egrave; possibile procedere alla fase di apertura delle domande di partecipazione perch&egrave; dati inconsistenti.
								<br>
								<br>
							</c:when>					
							<c:otherwise>
								<br>
								Mediante questa funzione &egrave; possibile procedere alla fase di apertura delle domande di partecipazione.
								<br><br>
								Confermi l'operazione ?
								<br>
								<br>
							</c:otherwise>
						</c:choose>
					</td>
				</gene:campoScheda>
				<input type="hidden" id="ngara" name="ngara" value="${ngara}" />
				<input type="hidden" id="codgar" name="codgar" value="${codgar}" />
			</gene:formScheda>
		</gene:redefineInsert>
		 <c:if test="${requestScope.operazioneEseguita eq 'ERRORI' || !funzioneAbilitata || esisteBloccoFasi eq 'true' || autotizzatoModifica eq 'false' 
		 	|| esistonoAcquisizioniOfferteDaElaborareFS10 eq 'true' || isSuperataDataTerminePresentazione == 'false' || esistonoDitteInGara == 'false'}" >
		  	<gene:redefineInsert name="buttons">
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
			</gene:redefineInsert>
		  </c:if>
		<gene:javaScript>
			function conferma() {
				document.forms[0].jspPathTo.value="gare/gare/gare-popup-attivaAperturaDomandePartecipazione.jsp";
				schedaConferma();
			}
			
			function annulla(){
				window.close();
			}	
		
		</gene:javaScript>	
	</gene:template>

</div>
</c:otherwise>
</c:choose>

