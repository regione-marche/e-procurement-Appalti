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
		
		<c:choose>
			<c:when test='${not empty param.bustalotti}'>
				<c:set var="bustalotti" value="${param.bustalotti}" />
			</c:when>
			<c:otherwise>
				<c:set var="bustalotti" value="${bustalotti}" />
			</c:otherwise>
		</c:choose>
				
		<c:choose>
			<c:when test='${not empty param.sortinv}'>
				<c:set var="sortinv" value="${param.sortinv}" />
			</c:when>
			<c:otherwise>
				<c:set var="sortinv" value="${sortinv}" />
			</c:otherwise>
		</c:choose>


		<gene:redefineInsert name="addHistory" />
		<gene:redefineInsert name="gestioneHistory" />
		<c:choose>
			<c:when test="${sortinv eq '1'}">
				<gene:setString name="titoloMaschera" value='Attiva elenco ditte da invitare con selezione automatica degli invitati' />
			</c:when>
			<c:otherwise>
				<gene:setString name="titoloMaschera" value='Attiva elenco ditte da invitare' />
			</c:otherwise>
		</c:choose>
		
		
		<c:set var="esistonoAcquisizioniOfferteDaElaborareFS10A" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, ngara, "FS10A" )}' />
		<c:set var="esisteBloccoFasi" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoCondizioniFasiAcquisizioniFunction",  pageContext, ngara, "-4")}'/>
				
		<c:set var="funzioneAbilitata" value='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIGARA.AttivaDitteDaInvitare")}'/>
		<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_TORN")}' />
		<c:if test='${!empty (filtroLivelloUtente)}'>
			<c:set var="autotizzatoModifica" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetAutorizzatoModificaFunction", pageContext, "CODGAR", codgar, "2")}'/>
		</c:if>
		
		<c:set var="esistonoDitteAmmissioneNulla" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteAmmissioneNullaFunction", pageContext, ngara,"-40")}' />
		
		<c:if test="${bustalotti eq '2' or bustalotti eq '1' }">
			<c:set var="esistonoDittePartecipazioneNulla" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePartecipazioneNullaFunction", pageContext, ngara, "0" )}' />
		</c:if>
		
		<c:set var="esistonoDitteAmmissioneConRiserva" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteStatoAmmissioneSpecificoFunction", pageContext, codgar,"-40", "3")}' />
		
		<c:set var="esistonoDitteSoccorsoIstruttorioInCorso" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteStatoAmmissioneSpecificoFunction", pageContext, codgar,"-40", "10")}' />
		
		<c:if test="${sortinv eq '1' }">
			<c:set var="situazioneNOI" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetSituazioneNumOrdineInvitoFunction", pageContext, codgar,ngara )}' />
		</c:if>
		
	
		<c:set var="modo" value="NUOVO" scope="request" />
		
		<gene:redefineInsert name="corpo">
			<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAttivaElencoDitteDaInvitare">
				<gene:campoScheda>
					<td colSpan="2">
						<c:choose>
							<c:when test="${esistonoAcquisizioniOfferteDaElaborareFS10A eq 'true'}">
							<c:set var="blocco" value="true"/>	
							<br>
								Non &egrave; possibile procedere alla fase di elenco ditte da invitare perch&egrave; devono essere prima acquisite tutte le buste di prequalifica.
								<br>
								<br>
							</c:when>
							<c:when test="${sortinv eq 1 && !(situazioneNOI eq 1)}">
							<c:set var="blocco" value="true"/>	
								<br>
								Non &egrave; possibile procedere alla fase di elenco ditte da invitare perch&egrave; deve essere prima assegnato il numero ordine invito per ogni ditta in gara. 
								<br>
								Attivare la funzione 'Sorteggio ordine invito' nella pagina corrente.
								<br>
								<br>
							</c:when>
							<c:when test="${esistonoDitteAmmissioneNulla == 'true'}">
								<c:set var="blocco" value="true"/>
								<c:set var="msgStatoAmmissione" value="deve essere specificato lo stato di ammissione per ogni ditta in gara."/>
								<c:if test="${sortinv eq '1' }">
									<c:set var="msgStatoAmmissione" value="deve essere specificato lo stato di ammissione per ogni ditta in gara oppure devono risultare ammesse un numero di ditte pari almeno al numero di ditte da invitare. 
									In quest'ultimo caso lo stato ammissione deve essere specificato in maniera consecutiva secondo il numero ordine di invito assegnato alle ditte."/>
								</c:if>
								<br>
								Non &egrave; possibile procedere alla fase di elenco ditte da invitare perch&egrave; ${msgStatoAmmissione}
								<c:if test="${sortinv eq '1' }">
									<br><br>
									Numero ditte da invitare: <b>${numope}</b>
								</c:if>
								<br>
								<br>
							</c:when>
							<c:when test="${esistonoDitteAmmissioneConRiserva == 'true'}">
								<c:set var="blocco" value="true"/>
								<br>
								Non &egrave; possibile procedere alla fase di elenco ditte da invitare perch&egrave; esistono ditte ammesse con riserva.
								<br><br>
							</c:when>
							<c:when test="${esistonoDitteSoccorsoIstruttorioInCorso == 'true'}">
								<c:set var="blocco" value="true"/>
								<br>
								Non &egrave; possibile procedere alla fase di elenco ditte da invitare perch&egrave; ci sono delle ditte in gara con soccorso istruttorio in corso.
								<br><br>
							</c:when>
							<c:when test="${esistonoDittePartecipazioneNulla eq 'true'}">
							<c:set var="blocco" value="true"/>	
								<br>
								Non &egrave; possibile procedere alla fase di elenco ditte da invitare perch&egrave; deve essere dettagliata la partecipazione ai singoli lotti della gara per ogni ditta in gara. 
								<br>
								<br>
							</c:when>
							<c:when test="${ !funzioneAbilitata || esisteBloccoFasi eq 'true' || autotizzatoModifica eq 'false'}">
							<c:set var="blocco" value="true"/>	
							<br>
								Non &egrave; possibile procedere alla fase di elenco ditte da invitare perch&egrave; dati inconsistenti.
								<br>
								<br>
							</c:when>					
							<c:otherwise>
								<br>
								Mediante questa funzione &egrave; possibile procedere alla fase di elenco ditte da invitare.
								<c:if test="${sortinv eq '1' }">
									<br>
									Contestualmente si procede alla selezione automatica delle ditte da invitare, considerando le ditte ammesse secondo l'ordine di invito loro assegnato.
									<br>Le ditte per cui non è stato specificato lo stato ammissione vengono aggiornate allo stato 'Non verificata'.  
									<br><br> 
									Numero ditte da invitare: <b>${numope}</b>
								</c:if>
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
				<input type="hidden" id="sortinv" name="sortinv" value="${sortinv}" />
			</gene:formScheda>
		</gene:redefineInsert>
		 <c:if test="${blocco eq true}" >
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

