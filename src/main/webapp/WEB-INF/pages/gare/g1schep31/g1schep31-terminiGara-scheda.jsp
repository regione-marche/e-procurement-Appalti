	<%/*
       * Created on 22-mag-2024
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

<style>
.valore-dato.old{
	border-left: #A0AABA 1px solid;
	border-top: #A0AABA 1px solid;		
	background-color: #EFEFEF;
}
</style>

<gene:formScheda entita="TORN" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreG1SCHEP31">

	<gene:campoScheda>
		<td colspan="3"><br>
		Mediante questa funzione è possibile ridefinire i termini di gara ai fini dell'invio ad ANAC delle scheda P3_1.<br>
		Sono ridefinibili solamente i campi con valore originale valorizzato.<br><br>
	</td>
	</gene:campoScheda>				
	<gene:campoScheda campo="ITERGA" visibile="false"/>
	<gene:campoScheda campo="CODGAR" visibile="false"/>
	<gene:campoScheda campo="ID" entita="G1SCHEP31" where="TORN.CODGAR=G1SCHEP31.CODGAR and G1SCHEP31.NGARA is null" visibile="false"/>
	<gene:campoScheda campo="CODGAR" entita="G1SCHEP31" where="TORN.CODGAR=G1SCHEP31.CODGAR" visibile="false"/>
	<c:set var="iterga2or4" value="${datiRiga.TORN_ITERGA eq '2' || datiRiga.TORN_ITERGA eq '4'}"/>
	<c:set var="schedaP31InCorso" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.npa.CheckSchedaP31InCorsoFunction", pageContext, gene:getValCampo(param.key, "CODGAR"))}' />
	
	<gene:gruppoCampi visibile="${iterga2or4 eq 'true'}">
		<gene:campoScheda>
			<td colspan="2"><b>Termini per la presentazione della domanda di partecipazione</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DTEPAR" visibile="false"/>
		<gene:campoScheda campo="DTEPAR" entita="G1SCHEP31" where="TORN.CODGAR=G1SCHEP31.CODGAR" visibile="true" modificabile="${not empty datiRiga.TORN_DTEPAR}">
			<td class="valore-dato old"><span>Valore originale: ${not empty datiRiga.TORN_DTEPAR? datiRiga.TORN_DTEPAR: 'Non definito'}</span></td>
		</gene:campoScheda>
		<gene:campoScheda campo="OTEPAR" visibile="false"/>
		<gene:campoScheda campo="OTEPAR" entita="G1SCHEP31" where="TORN.CODGAR=G1SCHEP31.CODGAR" visibile="true" modificabile="${not empty datiRiga.TORN_OTEPAR}">
			<td class="valore-dato old"><span>Valore originale: ${not empty datiRiga.TORN_OTEPAR? datiRiga.TORN_OTEPAR: 'Non definito'}</span></td>
		</gene:campoScheda>
	</gene:gruppoCampi>
	<gene:gruppoCampi visibile="${iterga2or4 ne 'true'}">
		<gene:campoScheda>
			<td colspan="2"><b>Termini per la presentazione dell'offerta</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DTEOFF" visibile="false"/>
		<gene:campoScheda campo="DTEOFF" entita="G1SCHEP31" where="TORN.CODGAR=G1SCHEP31.CODGAR" visibile="true" modificabile="${not empty datiRiga.TORN_DTEOFF}">
			<td class="valore-dato old"><span>Valore originale: ${not empty datiRiga.TORN_DTEOFF? datiRiga.TORN_DTEOFF: 'Non definito'}</span></td>
		</gene:campoScheda>
		<gene:campoScheda campo="OTEOFF" visibile="false" modificabile="false"/>
		<gene:campoScheda campo="OTEOFF" entita="G1SCHEP31" where="TORN.CODGAR=G1SCHEP31.CODGAR" visibile="true" modificabile="${not empty datiRiga.TORN_OTEOFF}">
			<td class="valore-dato old"><span>Valore originale: ${not empty datiRiga.TORN_OTEOFF? datiRiga.TORN_OTEOFF: 'Non definito'}</span></td>
		</gene:campoScheda>
		<gene:campoScheda>
			<td colspan="2"><b>Apertura offerte</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="DESOFF" visibile="false"/>
		<gene:campoScheda campo="DESOFF" entita="G1SCHEP31" where="TORN.CODGAR=G1SCHEP31.CODGAR" visibile="true" modificabile="${not empty datiRiga.TORN_DESOFF}">
			<td class="valore-dato old"><span>Valore originale: ${not empty datiRiga.TORN_DESOFF? datiRiga.TORN_DESOFF: 'Non definito'}</span></td>
		</gene:campoScheda>
		<gene:campoScheda campo="OESOFF" visibile="false"/>
		<gene:campoScheda campo="OESOFF" entita="G1SCHEP31" where="TORN.CODGAR=G1SCHEP31.CODGAR" visibile="true" modificabile="${not empty datiRiga.TORN_OESOFF}">
			<td class="valore-dato old"><span>Valore originale: ${not empty datiRiga.TORN_OESOFF? datiRiga.TORN_OESOFF: 'Non definito'}</span></td>
		</gene:campoScheda>
	</gene:gruppoCampi >
	<gene:campoScheda>
		<tr>
		<td class="comandi-dettaglio" colSpan="3">
			<c:choose>
			<c:when test='${modo eq "MODIFICA"}'>
				<gene:insert name="pulsanteSalva">
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma()">
				</gene:insert>
				<gene:insert name="pulsanteAnnulla">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
				</gene:insert>
			</c:when>
			<c:otherwise>
				<c:if test="${param.autorizzatoModifiche eq '1' && schedaP31InCorso eq 'false'}">
					<INPUT type="button"  class="bottone-azione" value='Modifica' title='Modifica' onclick="javascript:schedaModifica();">
				</c:if>
			</c:otherwise>
			</c:choose>
			&nbsp;
		</td>
	</tr>
	</gene:campoScheda>

<gene:redefineInsert name="modelliPredisposti" />
<gene:redefineInsert name="documentiAssociati"/>
<gene:redefineInsert name="noteAvvisi" />
<gene:redefineInsert name="schedaNuovo" />
<gene:redefineInsert name="schedaModifica">
	<c:if test="${param.autorizzatoModifiche eq '1' && schedaP31InCorso eq 'false'}">
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:schedaModifica();" title="Modifica" tabindex="1501">Modifica</a>
			</td>
		</tr>
	</c:if>
</gene:redefineInsert>
<input type="hidden" name="autorizzatoModifiche" value="${param.autorizzatoModifiche}"/>
</gene:formScheda>