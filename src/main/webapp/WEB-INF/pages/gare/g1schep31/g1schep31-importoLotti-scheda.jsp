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

<c:set var="ngara" value='${gene:getValCampo(param.key, "NGARA")}'/>
<c:set var="msgErroreValidazione" value="L'importo deve essere maggiore di 0"/>
<c:set var="msgErroreValidazioneIMPAPP" value="L'importo totale a base di gara deve essere maggiore o uguale dell'importo non soggetto a ribasso e della sicurezza"/>
<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, ngara)}' />
<c:set var="schedaP31InCorso" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.npa.CheckSchedaP31InCorsoFunction", pageContext, codgar)}' />
<c:set var="schedaCM1InCorso" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.npa.CheckSchedaCM1InCorsoFunction", pageContext, codgar)}' />
<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetTIPGENFunction" parametro='${codgar}' />
<c:if test="${tipoAppalto eq '1'}">
	<c:set var="msgErroreValidazioneIMPAPP" value="L'importo totale a base di gara deve essere maggiore o uguale dell'importo non soggetto a ribasso, della sicurezza e degli oneri di progettazione"/>
</c:if>
	
<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="G1SCHEP31-lotto-scheda">
<gene:setString name="titoloMaschera" value='Ridefinisci dati scheda P3_1 per il lotto ${ngara}' />

<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
	</gene:redefineInsert>
	
<gene:redefineInsert name="corpo">
<gene:formScheda entita="GARE" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreG1SCHEP31">
	
	<gene:campoScheda>
		<td colspan="3"><br>
		Mediante questa funzione è possibile ridefinire gli importi di gara ai fini dell'invio ad ANAC della scheda P3_1.<br>
		Sono ridefinibili solamente gli importi con valore originale valorizzato e maggiore di 0.00 &euro;.<br><br>
		Il valore massimo stimato è calcolato considerando gli importi ridefiniti. Se un particolare importo non è valorizzato, il calcolo considera il corrispondente importo originale.
		<br><br>
	</td>
	</gene:campoScheda>	
	<gene:campoScheda campo="NGARA" visibile="false"/>
	<gene:campoScheda campo="CODGAR" entita="TORN" where="TORN.CODGAR=GARE.CODGAR1" visibile="false"/>
	<gene:campoScheda campo="TIPGEN" entita="TORN" where="TORN.CODGAR=GARE.CODGAR1" visibile="false"/>
	<gene:campoScheda campo="ID" entita="G1SCHEP31" where="GARE.NGARA=G1SCHEP31.NGARA" visibile="false"/>
	<gene:campoScheda campo="CODGAR" entita="G1SCHEP31" where="GARE.NGARA=G1SCHEP31.NGARA" visibile="false"/>
	<gene:campoScheda campo="NGARA" entita="G1SCHEP31" where="GARE.NGARA=G1SCHEP31.NGARA" visibile="false"/>
	
	<gene:campoScheda>
		<td colspan="2"><b>Importo a base di gara, IVA esclusa</b></td>
	</gene:campoScheda>
	<gene:campoScheda campo="ONPRGE" visibile="false"/>
	<gene:campoScheda campo="ONPRGE" entita="G1SCHEP31" where="GARE.NGARA=G1SCHEP31.NGARA" visibile="${datiRiga.TORN_TIPGEN eq '1'}" modificabile="${not empty datiRiga.GARE_ONPRGE}" >
	<gene:checkCampoScheda funzione='checkGreater0("##")' obbligatorio="true" messaggio="${msgErroreValidazione}" onsubmit="false" />
		<td class="valore-dato old"><span>Valore originale: ${not empty datiRiga.GARE_ONPRGE? (datiRiga.GARE_ONPRGE.concat(' &euro;')): 'Non definito'}</span></td>
	</gene:campoScheda>
	<gene:campoScheda campo="IMPAPP" visibile="false"/>
	<gene:campoScheda campo="IMPAPP" entita="G1SCHEP31" where="GARE.NGARA=G1SCHEP31.NGARA" visibile="true" modificabile="${not empty datiRiga.GARE_IMPAPP}">
		<gene:checkCampoScheda funzione='checkGreater0("##")' obbligatorio="true" messaggio="${msgErroreValidazione}" onsubmit="false" />
		<td class="valore-dato old"><span>Valore originale: ${not empty datiRiga.GARE_IMPAPP? (datiRiga.GARE_IMPAPP.concat(' &euro;')): 'Non definito'}</span></td>
	</gene:campoScheda>
	
	<gene:fnJavaScriptScheda funzione="calcoloValmaxCustom('#GARE1_IMPRIN#','#GARE1_IMPSERV#','#GARE1_IMPPROR#','#GARE1_IMPALTRO#','#GARE_IMPAPP#','#G1SCHEP31_IMPRIN#','#G1SCHEP31_IMPSERV#','#G1SCHEP31_IMPPROR#','#G1SCHEP31_IMPALTRO#','#G1SCHEP31_IMPAPP#')" elencocampi="G1SCHEP31_IMPRIN;G1SCHEP31_IMPSERV;G1SCHEP31_IMPPROR;G1SCHEP31_IMPALTRO;G1SCHEP31_IMPAPP" esegui="true"/>

	<gene:campoScheda  campo="IMPAPP_RIB_ORIGINALE" campoFittizio="true" definizione="F15;0;;MONEY;" visibile="false">
		<gene:calcoloCampoScheda funzione='toMoney(toVal("#GARE_IMPAPP#") - toVal("#GARE_IMPNRL#") - toVal("#GARE_IMPSIC#") - toVal("#GARE_ONPRGE#"))' elencocampi="GARE_IMPAPP;GARE_IMPNRL;GARE_IMPSIC;GARE_ONPRGE" />
	</gene:campoScheda>
	<gene:campoScheda title="di cui soggetto a ribasso" campo="IMPAPP_RIB" campoFittizio="true" modificabile="false" definizione="F15;0;;MONEY;" visibile="true">
		<gene:calcoloCampoScheda funzione='calcolaImpappRib(toVal("#GARE_IMPAPP#"),toVal("#GARE_IMPNRL#"),toVal("#GARE_IMPSIC#"),("#GARE_ONPRGE#"),toVal("#G1SCHEP31_IMPAPP#"),toVal("#G1SCHEP31_IMPNRL#"),toVal("#G1SCHEP31_IMPSIC#"),("#G1SCHEP31_ONPRGE#"))' elencocampi="G1SCHEP31_IMPAPP;G1SCHEP31_IMPNRL;G1SCHEP31_IMPSIC;G1SCHEP31_ONPRGE" />
		<gene:checkCampoScheda funzione='checkGreater0("##")' obbligatorio="true" messaggio="${msgErroreValidazioneIMPAPP}" onsubmit="true" />
			<td class="valore-dato old"><span id="spanIMPAPP_RIB">calcolo in corso...</span></td>
	</gene:campoScheda>

	<gene:campoScheda campo="IMPNRL" visibile="false"/>
	<gene:campoScheda campo="IMPNRL" entita="G1SCHEP31" where="GARE.NGARA=G1SCHEP31.NGARA" visibile="true" modificabile="${datiRiga.GARE_IMPNRL gt 0.0}">
		<gene:checkCampoScheda funzione='checkGreater0("##")' obbligatorio="true" messaggio="${msgErroreValidazione}" onsubmit="false" />
		<td class="valore-dato old"><span>Valore originale: ${not empty datiRiga.GARE_IMPNRL? (datiRiga.GARE_IMPNRL.concat(' &euro;')): 'Non definito'}</span></td>
	</gene:campoScheda>
	<gene:campoScheda campo="IMPSIC" visibile="false"/>
	<gene:campoScheda campo="IMPSIC" entita="G1SCHEP31" where="GARE.NGARA=G1SCHEP31.NGARA" visibile="true" modificabile="${datiRiga.GARE_IMPSIC  gt 0.0}">
		<gene:checkCampoScheda funzione='checkGreater0("##")' obbligatorio="true" messaggio="${msgErroreValidazione}" onsubmit="false" />
		<td class="valore-dato old"><span>Valore originale: ${not empty datiRiga.GARE_IMPSIC? (datiRiga.GARE_IMPSIC.concat(' &euro;')): 'Non definito'}</span></td>
	</gene:campoScheda>
	<gene:campoScheda>
		<td colspan="2"><b>Opzioni e rinnovi</b></td>
	</gene:campoScheda>
	<gene:campoScheda campo="AMMRIN" entita="GARE1" where="GARE.NGARA=GARE1.NGARA" visibile="false"/>
	<gene:campoScheda campoFittizio="true" campo="AMMRINFITT" definizione="T2;0;;;G1AMMRING" value="${datiRiga.GARE1_AMMRIN eq '1'? 'Si' : 'No'}" visibile="true" modificabile="false">
		<td class="valore-dato old"><span></span></td>
	</gene:campoScheda>
	<gene:gruppoCampi visibile="${datiRiga.GARE1_AMMRIN eq '1'}">
		<gene:campoScheda campo="IMPRIN" entita="GARE1" where="GARE.NGARA=GARE1.NGARA" visibile="false"/>
		<gene:campoScheda campo="IMPRIN" entita="G1SCHEP31" where="GARE.NGARA=G1SCHEP31.NGARA" visibile="${datiRiga.GARE1_AMMRIN eq '1'}" modificabile="${not empty datiRiga.GARE1_IMPRIN}">
			<gene:checkCampoScheda funzione='checkGreater0("##")' obbligatorio="true" messaggio="${msgErroreValidazione}" onsubmit="false" />
			<td class="valore-dato old"><span>Valore originale: ${not empty datiRiga.GARE1_IMPRIN? (datiRiga.GARE1_IMPRIN.concat(' &euro;')): 'Non definito' }</span></td>
		</gene:campoScheda>
	</gene:gruppoCampi>
	<gene:campoScheda campo="AMMOPZ" entita="GARE1" where="GARE.NGARA=GARE1.NGARA" visibile="false"/>
	<gene:campoScheda campoFittizio="true" campo="AMMOPZFITT" definizione="T2;0;;;G1AMMOPZG" value="${datiRiga.GARE1_AMMOPZ eq '1'? 'Si' : 'No'}" visibile="true" modificabile="false">
			<td class="valore-dato old"><span></span></td>
		</gene:campoScheda>
	<gene:gruppoCampi visibile="${datiRiga.GARE1_AMMOPZ eq '1'}">
		<gene:campoScheda campo="IMPSERV" entita="GARE1" where="GARE.NGARA=GARE1.NGARA" visibile="false"/>
		<gene:campoScheda campo="IMPSERV" entita="G1SCHEP31" where="GARE.NGARA=G1SCHEP31.NGARA" visibile="true" modificabile="${not empty datiRiga.GARE1_IMPSERV}">
			<gene:checkCampoScheda funzione='checkGreater0("##")' obbligatorio="true" messaggio="${msgErroreValidazione}" onsubmit="false" />
			<td class="valore-dato old"><span>Valore originale: ${not empty datiRiga.GARE1_IMPSERV? (datiRiga.GARE1_IMPSERV.concat(' &euro;')): 'Non definito'}</span></td>
		</gene:campoScheda>
		<gene:campoScheda campo="IMPPROR" entita="GARE1" where="GARE.NGARA=GARE1.NGARA" visibile="false"/>
		<gene:campoScheda campo="IMPPROR" entita="G1SCHEP31" where="GARE.NGARA=G1SCHEP31.NGARA" visibile="true" modificabile="${not empty datiRiga.GARE1_IMPPROR}">
			<gene:checkCampoScheda funzione='checkGreater0("##")' obbligatorio="true" messaggio="${msgErroreValidazione}" onsubmit="false" />
			<td class="valore-dato old"><span>Valore originale: ${not empty datiRiga.GARE1_IMPPROR? (datiRiga.GARE1_IMPPROR.concat(' &euro;')): 'Non definito'}</span></td>
		</gene:campoScheda>
		<gene:campoScheda campo="IMPALTRO" entita="GARE1" where="GARE.NGARA=GARE1.NGARA" visibile="false"/>
		<gene:campoScheda campo="IMPALTRO" entita="G1SCHEP31" where="GARE.NGARA=G1SCHEP31.NGARA" visibile="true" modificabile="${not empty datiRiga.GARE1_IMPALTRO}">
			<gene:checkCampoScheda funzione='checkGreater0("##")' obbligatorio="true" messaggio="${msgErroreValidazione}" onsubmit="false" />
			<td class="valore-dato old"><span>Valore originale: ${not empty datiRiga.GARE1_IMPALTRO? (datiRiga.GARE1_IMPALTRO.concat(' &euro;')): 'Non definito'}</span></td>
		</gene:campoScheda>
	</gene:gruppoCampi>
	<gene:campoScheda campo="VALMAX" entita="V_GARE_IMPORTI" where="V_GARE_IMPORTI.NGARA=GARE.NGARA" visibile="false"/>
	<gene:campoScheda campo="VALMAX" entita="G1SCHEP31" where="GARE.NGARA=G1SCHEP31.NGARA" visibile="false"/>
	<gene:campoScheda campo="VALMAX_FIT" campoFittizio="true" title="Valore massimo stimato" definizione="F14.5;;;MONEY" value="${datiRiga.V_GARE_IMPORTI_VALMAX}" modificabile="false" visibile="true">
			<td class="valore-dato old"><span>Valore originale: ${not empty datiRiga.V_GARE_IMPORTI_VALMAX? (datiRiga.V_GARE_IMPORTI_VALMAX.concat(' &euro;')): 'Non definito'}</span></td>
	</gene:campoScheda>
	

	
	
	<gene:campoScheda>
		<tr>
		<td class="comandi-dettaglio" colSpan="3">
			<c:choose>
			<c:when test='${modo eq "MODIFICA"}'>
				<gene:insert name="pulsanteSalva">
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConfermaCustom()">
				</gene:insert>
				<gene:insert name="pulsanteAnnulla">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:schedaAnnulla()">
				</gene:insert>
			</c:when>
			<c:otherwise>
				<c:if test="${param.autorizzatoModifiche eq '1' && (schedaP31InCorso eq 'false' or (schedaP31InCorso eq 'true' and schedaCM1InCorso eq 'false'))}">
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
<gene:redefineInsert name="schedaConferma">		
		<tr>
			<td class="vocemenulaterale" id="">
				<a href="javascript:schedaConfermaCustom();" title="Salva" tabindex="1559">
					Salva</a>
			</td>
		</tr>
</gene:redefineInsert>
<gene:redefineInsert name="schedaModifica">
	<c:if test="${param.autorizzatoModifiche eq '1' && (schedaP31InCorso eq 'false' or (schedaP31InCorso eq 'true' and schedaCM1InCorso eq 'false'))}">
		<tr>
			<td class="vocemenulaterale">
				<a href="javascript:schedaModifica();" title="Modifica" tabindex="1501">Modifica</a>
			</td>
		</tr>
	</c:if>
</gene:redefineInsert>

	<input type="hidden" name="autorizzatoModifiche" value="${param.autorizzatoModifiche}"/>
</gene:formScheda>
<gene:javaScript>
	activeForm.calcola("IMPAPP_RIB_ORIGINALE");
	activeForm.calcola("IMPAPP_RIB");
	var impAppRibOrig=getValue("IMPAPP_RIB_ORIGINALE");
	$('#spanIMPAPP_RIB').text('Valore originale: '+(impAppRibOrig ? (impAppRibOrig+' \u20AC'): 'Non definito'));
	
	function calcoloValmaxCustom(imprin,impserv,imppror,impaltro,impapp,imprinNew,impservNew,impprorNew,impaltroNew,impappNew){
		if(imprinNew>0) 	imprin=imprinNew;
		if(impservNew>0)	impserv=impservNew;
		if(impprorNew>0)	imppror=impprorNew;
		if(impaltroNew>0)	impaltro=impaltroNew;
		if(impappNew>0)		impapp=impappNew;
		calcoloValmax(imprin,impserv,imppror,impaltro,impapp);
		var valMax = getValue('VALMAX_FIT');
		setValue('G1SCHEP31_VALMAX',valMax);
		<c:if test="${modo eq 'VISUALIZZA'}">
			$('#VALMAX_FITview').text(toVal(valMax).toLocaleString('it-IT', { style: 'currency', currency: 'EUR' }));
		</c:if>
	}
	
	function calcolaImpappRib(impapp,impnrl,impsic,onprge,impappNew,impnrlNew,impsicNew,onprgeNew){
		if(impappNew>0) 	impapp=impappNew;
		if(impnrlNew>0)		impnrl=impnrlNew;
		if(impsicNew>0)		impsic=impsicNew;
		if(onprgeNew>0)		onprge=onprgeNew;
		return toMoney(impapp - impnrl - impsic - onprge);
	}
	
	function checkGreater0(value){
		return value>0 || value==="";
	}

	function schedaConfermaCustom(){ //se non ho modificato valori, non setto neanche VALMAX
		if(!(getValue('G1SCHEP31_IMPAPP') 
			|| getValue('G1SCHEP31_IMPSERV') 
			|| getValue('G1SCHEP31_IMPPROR') 
			|| getValue('G1SCHEP31_IMPALTRO') 
			|| getValue('G1SCHEP31_ONPRGE') 
			|| getValue('G1SCHEP31_IMPNRL') 
			|| getValue('G1SCHEP31_IMPSIC'))){
				setValue('G1SCHEP31_VALMAX','');
			}
		schedaConferma();
	}		
</gene:javaScript>
</gene:redefineInsert>
</gene:template>