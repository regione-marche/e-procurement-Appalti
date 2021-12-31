<%
/*
 * Created on: 10-07-2017
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
		Popup per il calcolo dei punteggi tecnici o economici
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.calcoloEseguito and requestScope.calcoloEseguito eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:choose>
	<c:when test='${!empty param.chiave}'>
		<c:set var="chiave" value="${param.chiave}" />
	</c:when>
	<c:otherwise>
		<c:set var="chiave" value="${chiave}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.tipo}'>
		<c:set var="tipo" value="${param.tipo}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipo" value="${tipo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.AttivaValutazioneTec}'>
		<c:set var="AttivaValutazioneTec" value="${param.AttivaValutazioneTec}" />
	</c:when>
	<c:otherwise>
		<c:set var="AttivaValutazioneTec" value="${AttivaValutazioneTec}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${!empty param.isProceduraTelematica}'>
		<c:set var="isProceduraTelematica" value="${param.isProceduraTelematica}" />
	</c:when>
	<c:otherwise>
		<c:set var="isProceduraTelematica" value="${isProceduraTelematica}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${fn:contains(chiave,'GARE') }">
		<c:set var="ngara" value='${gene:getValCampo(chiave,"NGARA")}'  />
	</c:when>
	<c:when test="${fn:contains(chiave,'DITG') }">
		<c:set var="ngara" value='${gene:getValCampo(chiave,"NGARA5")}'  />
	</c:when>
</c:choose>

<c:choose>
	<c:when test='${!empty param.bustalotti}'>
		<c:set var="bustalotti" value="${param.bustalotti}" />
	</c:when>
	<c:otherwise>
		<c:set var="bustalotti" value="${bustalotti}" />
	</c:otherwise>
</c:choose>

<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, ngara)}' />

<c:if test="${isProceduraTelematica eq 'true' }">
	<c:choose>
		<c:when test='${bustalotti eq "2"}'>
			<c:set var="chiaveControlloBuste" value='${codgar}' />
		</c:when>
		<c:otherwise>
			<c:set var="chiaveControlloBuste" value="${ngara}" />
		</c:otherwise>
	</c:choose>
</c:if>

<c:choose>
	<c:when test='${!empty param.paginaAttivaWizard}'>
		<c:set var="paginaAttivaWizard" value="${param.paginaAttivaWizard}" />
	</c:when>
	<c:otherwise>
		<c:set var="paginaAttivaWizard" value="${paginaAttivaWizard}" />
	</c:otherwise>
</c:choose>

<c:set var="esistonoDitteConPunteggio" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePunteggioValorizzatoFunction", pageContext, ngara, tipo )}' />

<c:choose>
	<c:when test="${tipo eq '1' }">
		<c:set var="msgTitolo" value ="tecnica" />
		<c:if test="${esistonoDitteConPunteggio eq 'no' }">
			<c:set var="messaggioControlloTec" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlloPunteggioTecnicoEconomicoFunction", pageContext, ngara,"50", "calcoloPunteggi","true" )}' />
			<c:if test="${isProceduraTelematica eq 'true' }">
				<c:set var="esistonoAcquisizioniOfferteDaElaborareFS11B" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, chiaveControlloBuste, "FS11B" )}' />
				<c:if test="${AttivaValutazioneTec eq 'true' }">	
					<c:set var="esistonoDitteRequisitiMinimiNulli" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteRequisitiMinimiNulliFunction", pageContext, ngara, "false" )}' />
				</c:if>
			</c:if>
		</c:if>
	</c:when>
	<c:otherwise>
		<c:set var="msgTitolo" value ="economica" />
		<c:if test="${esistonoDitteConPunteggio eq 'no' }">
			<c:set var="messaggioControlloEco" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlloPunteggioTecnicoEconomicoFunction", pageContext, ngara,"60", "calcoloPunteggi","true" )}' />
			<c:if test="${isProceduraTelematica eq 'true' }">
				<c:set var="esistonoAcquisizioniOfferteDaElaborareFS11C" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, chiaveControlloBuste, "FS11C" )}' />
			</c:if>
			<c:set var="controlloImportiNulliDaCriteri" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.ControlloImportiNulliDaCriteriFunction", pageContext, ngara)}' />
		</c:if>
	</c:otherwise>
</c:choose>

<c:if test="${isProceduraTelematica eq 'true' and esistonoDitteConPunteggio eq 'no' }">
	<c:set var="esistonoDitteAmmissioneNulla" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteAmmissioneNullaFunction", pageContext, ngara,paginaAttivaWizard)}' />
	<c:set var="esistonoDitteAmmissioneSoccorso" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteAmmissioneSoccorsoIstruttorioFunction", pageContext, ngara, paginaAttivaWizard,"false" )}' />
</c:if>

<c:if test="${esistonoDitteConPunteggio eq 'no' }">
	<c:set var="esitoControlloG1cridef" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ControlloAllineamentoG1cridefG1crivalFunction", pageContext, ngara, tipo, "" )}' />
</c:if>

<c:choose>
	<c:when test='${param.lottoPlicoUnico}'>
		<c:set var="codice" value="${codgar}" />
	</c:when>
	<c:otherwise>
		<c:set var="codice" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:set var="isValutazioneCommissione" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsValutazioneCommissioneFunction",pageContext,codice)}' />
	
<c:if test="${esitoControlloG1cridef eq 'nok' and isValutazioneCommissione}">
	<c:set var="esitoControlloGiudiziCommissione" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlloGiudizioCommissioneFunction", pageContext, ngara,tipo,codgar, "" )}' />
	<c:if test="${esitoControlloGiudiziCommissione eq 'ok' or esitoControlloGiudiziCommissione eq 'ko'}">
		<c:set var="esitoControlloG1cridef" value='ok'/>
	</c:if>
</c:if>

<gene:setString name="titoloMaschera" value='Calcolo punteggi criteri di valutazione della busta ${msgTitolo }' />


<c:set var="modo" value="NUOVO" scope="request" />
	
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="DPUN" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupCalcoloPunteggi">
	
		<gene:campoScheda>
			<td>
			<br>
		<c:choose>
			<c:when test="${esistonoDitteConPunteggio eq 'si'}">
				<c:set var="blocco" value="true"/>
				I punteggi dei criteri di valutazione della busta ${msgTitolo} risultano già assegnati alle ditte.<br>
				Attivare la funzione 'Annulla calcolo punteggi' per rieseguire il calcolo.<br>
			</c:when>
			<c:when test="${!empty messaggioControlloTec}">
				<c:set var="blocco" value="true"/>
				${messaggioControlloTec }.<br>
			</c:when>
			<c:when test="${!empty messaggioControlloEco}">
				<c:set var="blocco" value="true"/>
				${messaggioControlloEco }.<br>
			</c:when>
			<c:when test="${esistonoAcquisizioniOfferteDaElaborareFS11B == 'true'}">
				<c:set var="blocco" value="true"/>
				Per procedere con il calcolo dei punteggi, deve essere prima acquisita la busta tecnica per ogni ditta nella lista.<br>
			</c:when>
			<c:when test="${esistonoAcquisizioniOfferteDaElaborareFS11C == 'true'}">
				<c:set var="blocco" value="true"/>
				Per procedere con il calcolo dei punteggi, deve essere prima acquisita la busta economica per ogni ditta nella lista.<br>
			</c:when>
			<c:when test="${esitoControlloG1cridef == 'nok'}">
				<c:set var="blocco" value="true"/>
				Per procedere con il calcolo dei punteggi, deve essere prima completata la compilazione del dettaglio valutazione per tutte le ditte in gara.<br><br>
				Verificare la ditta '${ragioneSocDitta }' o attivare la funzione 'Verifica dettaglio valutazione completato' per ottenere l'elenco completo delle ditte in gara con valutazione non completa.<br>
			</c:when>
			<c:when test="${esitoControlloG1cridef == 'nok-coeffNonValidi'}">
				<c:set var="blocco" value="true"/>
				Per procedere con il calcolo dei punteggi, i coefficenti assegnati nel dettaglio valutazione delle ditte in gara devono essere tutti inferiori o uguali a 1
				(verificare la ditta '${ragioneSocDitta }').<br>
			</c:when>
			<c:when test="${esitoControlloGiudiziCommissione == 'ko'}">
				<c:set var="blocco" value="true"/>
				Per procedere con il calcolo dei punteggi, deve essere prima completata la compilazione del dettaglio valutazione per tutte le ditte in gara.
				Compilare la valutazione di tutti i singoli commissari oppure quella complessiva.<br><br>
				Verificare la ditta '${dittaGiudizioCommissione }' o attivare la funzione 'Verifica dettaglio valutazione completato' per ottenere l'elenco completo delle ditte in gara con valutazione non completa.<br>
			</c:when>
			<c:when test="${esitoControlloGiudiziCommissione == 'ko-nonvalido'}">
				<c:set var="blocco" value="true"/>
				Per procedere con il calcolo dei punteggi, i coefficenti assegnati nel dettaglio valutazione delle ditte da parte dei singoli commissari devono essere tutti inferiori o uguali a 1
				(verificare la ditta '${dittaGiudizioCommissione }').<br>
			</c:when>
			<c:when test="${esistonoDitteRequisitiMinimiNulli == 'true'}">
				<c:set var="blocco" value="true"/>
				Per procedere con il calcolo dei punteggi, deve essere prima specificata la conformità ai requisiti minimi per ogni ditta in gara.<br>
			</c:when>
			<c:when test="${controlloImportiNulliDaCriteri == 'nok'}">
				<c:set var="blocco" value="true"/>
				Per procedere con il calcolo dei punteggi, deve essere prima specificato l'importo offerto per ogni ditta in gara.<br>
			</c:when>
			<c:when test="${esistonoDitteAmmissioneNulla == 'true'}">
				<c:set var="blocco" value="true"/>
				Per procedere con il calcolo dei punteggi, deve essere prima specificato lo stato di ammissione per ogni ditta nella lista.<br>
			</c:when>
			<c:when test="${esistonoDitteAmmissioneSoccorso == 'true'}">
				<c:set var="blocco" value="true"/>
				Per procedere con il calcolo dei punteggi, non ci devono essere ditte nella lista con soccorso istruttorio in corso.<br>
			</c:when>
			<c:otherwise>
				Confermi il calcolo dei punteggi dei criteri di valutazione della busta ${msgTitolo } per le ditte in gara?<br>
			</c:otherwise>
		</c:choose>
			<br>
			</td>
		</gene:campoScheda>

		<input type="hidden" name="chiave" value="${chiave}">
		<input type="hidden" name="isProceduraTelematica" value="${isProceduraTelematica}">
		<input type="hidden" name="tipo" value="${tipo}">
		<input type="hidden" name="AttivaValutazioneTec" value="${AttivaValutazioneTec}">
		<input type="hidden" name="ngara" value="${ngara}">
		<input type="hidden" name="codice" value="${codice}">
		<input type="hidden" name="bustalotti" value="${bustalotti}">
		<input type="hidden" name="paginaAttivaWizard" value="${paginaAttivaWizard}">
		
	</gene:formScheda>
		<c:if test="${blocco eq true || requestScope.calcoloEseguito eq '2'}">
			<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla()">&nbsp;
			</gene:redefineInsert>
		</c:if>
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/commons/popupCalcoloPunteggi.jsp";
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