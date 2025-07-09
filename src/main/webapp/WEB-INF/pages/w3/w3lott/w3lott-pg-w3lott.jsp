 <%
	/*
	 * Created on 20-Ott-2008
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
<c:if test="${not empty backToList || not empty erroreAggiornamentoMassivo}">
	<gene:javaScript>	
		<c:choose>
			<c:when test="${not empty erroreAggiornamentoMassivo}">
				alert("${erroreAggiornamentoMassivo}")
			</c:when>
			<c:otherwise>
				alert("Aggiornamento dei lotti concluso con successo")
			</c:otherwise>	
		</c:choose>
		historyVaiIndietroDi(1)
	</gene:javaScript>	
</c:if>
<c:if test="${empty backToList and empty erroreAggiornamentoMassivo}">
	<c:set var="isAggiornamentoMassivo" value="${param.aggiornaMassivo eq 'true'}"/>
	<c:set var="v3_04_9_Attiva" value='${gene:callFunction2("it.eldasoft.sil.w3.tags.funzioni.IsAttivaVx_xx_xFunction",pageContext,"7")}' />
	<c:set var="numgara" value='${gene:getValCampo(keyParent, "NUMGARA")}' />
	<c:if test="${empty numgara}">
		<c:set var="numgara" value='${gene:getValCampo(key, "NUMGARA")}' />
	</c:if>
	<c:if test="${not empty numgara}">
		<c:set var="cig_acc_quadro" value='${gene:callFunction2("it.eldasoft.sil.w3.tags.funzioni.GetCIGAccQuadroW3LottFunction",pageContext,numgara)}' />
	</c:if>

	<c:if test="${isAggiornamentoMassivo}">
				</br>
				<b>Numero lotti selezionati: ${param.numeroLotti}</b>
				<br><br>
				Mediante questa funzione è possibile aggiornare in modo massivo i dati dei lotti selezionati.<br>
				Per attivare la modifica di un campo impostare il corrispondente check nella colonna 'Attiva modifica'. I campi attivati e non valorizzati saranno sbiancati.<br>
				<br><br>
	</c:if>		
	<gene:formScheda entita="W3LOTT" gestisciProtezioni="true" 
		gestore="${isAggiornamentoMassivo? 'it.eldasoft.sil.w3.tags.gestori.submit.GestoreW3LOTTMASSIVO' : 'it.eldasoft.sil.w3.tags.gestori.submit.GestoreW3LOTT'}"
		plugin= "${isAggiornamentoMassivo? 'it.eldasoft.sil.w3.tags.gestori.plugin.GestoreW3LOTTMASSIVO' : 'it.eldasoft.sil.w3.tags.gestori.plugin.GestoreW3LOTT'}">
		
		<input type="hidden" name="aggiornaMassivo" value="${param.aggiornaMassivo}"/>
		<input type="hidden" name="lottiAggiornamento" value="${param.lottiAggiornamento}"/>
		
		<gene:campoScheda campo="NUMGARA" visibile="false" defaultValue='${gene:getValCampo(keyParent,"NUMGARA")}'/>
		<gene:campoScheda campo="NUMLOTT" visibile="false" />
	<c:if test="${not isAggiornamentoMassivo}">
		<gene:campoScheda campo="CIG" modificabile="false"/>
		<gene:campoScheda title="Stato del lotto" campo="STATO_SIMOG" modificabile="false" defaultValue="1"/>
		<gene:campoScheda entita="W3GARA" campo="STATO_SIMOG" visibile="false" where="W3GARA.NUMGARA = W3LOTT.NUMGARA"/>
		<gene:campoScheda campo="DATA_CREAZIONE_LOTTO" modificabile="false" />
		<gene:campoScheda campo="DATA_PUBBLICAZIONE" modificabile="false" visibile="${datiRiga.W3LOTT_STATO_SIMOG ne '1'}"/>
		<gene:campoScheda campo="DATA_CANCELLAZIONE_LOTTO" modificabile="false" visibile="${datiRiga.W3LOTT_STATO_SIMOG eq '6'}"/>
		<gene:campoScheda campo="ID_MOTIVAZIONE" modificabile="false" visibile="${datiRiga.W3LOTT_STATO_SIMOG eq '6'}"/>
		<gene:campoScheda campo="NOTE_CANC" modificabile="false" visibile="${datiRiga.W3LOTT_STATO_SIMOG eq '6'}"/>
		
		<gene:campoScheda>
			<td colspan="2"><b><br>SEZIONE I: DATI DEL LOTTO<br><br></b></td>
		</gene:campoScheda>
		<gene:campoScheda>
			<td colspan="2"><b>I.1) Dati generali</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="OGGETTO" obbligatorio="true"/>
		<gene:campoScheda campo="TIPO_CONTRATTO" obbligatorio="true"/>
		<gene:campoScheda campo="FLAG_USO_METODI_EDILIZIA" title="Uso di metodi e strumenti elettronici di modellazione per l'edilizia e le infrastrutture?"/>
			
		<gene:campoScheda addTr="false">
			<tr id="rowTipologiaLavoro" name ="W3LOTTIPI_TIPOLOGIALAVORO" >
				<td colspan="2"><b><br>Tipologia lavoro</b></td>
			</tr>
		</gene:campoScheda>
		<c:forEach items="${tabellatoTipologiaLavoro}" var="tabellato" varStatus="indice" >
			<gene:campoScheda campo="TIPOLOGIALAVORO${tabellato[1]}" entita="W3LOTTTIPI" value="${tabellato[2]}" 
				campoFittizio="true" definizione="T1;0;;SN" title='${tabellato[0]}' 
				visibile="${tabellato[3] eq '' or empty tabellato[3] or (tabellato[3] eq '1' and tabellato[2] eq '1')}">
			</gene:campoScheda>
		</c:forEach>
		
		<gene:campoScheda addTr="false">
			<tr id="rowTipologiaFornitura" name ="W3LOTTIPI_TIPOLOGIAFORNITURA" >
				<td colspan="2"><b><br>Tipologia fornitura</b></td>
			</tr>
		</gene:campoScheda>
		<c:forEach items="${tabellatoTipologiaFornitura}" var="tabellato" varStatus="indice" >
			<gene:campoScheda campo="TIPOLOGIAFORNITURA${tabellato[1]}" entita="W3LOTTTIPI" value="${tabellato[2]}" 
				campoFittizio="true" definizione="T1;0;;SN" title='${tabellato[0]}' 
				visibile="${tabellato[3] eq '' or empty tabellato[3] or (tabellato[3] eq '1' and tabellato[2] eq '1')}">
			</gene:campoScheda>
		</c:forEach>
	</c:if>
	<gene:campoScheda>
			<td colspan="2"><b><br>Ulteriori dati</b></td>
		</gene:campoScheda>
		<c:if test="${v3_04_9_Attiva}" >
			<gene:campoScheda campo="DEROGA_QUALIFICAZIONE_SA" obbligatorio="${not isAggiornamentoMassivo}"/>
		</c:if>
		<gene:campoScheda campo="FLAG_ESCLUSO" obbligatorio="${not isAggiornamentoMassivo}" defaultValue="2"/>
		<gene:campoScheda campo="ID_ESCLUSIONE" />
		<gene:campoScheda campo="FLAG_REGIME" obbligatorio="${not isAggiornamentoMassivo}" defaultValue="2"/>
		<gene:campoScheda campo="ART_REGIME" />

		<gene:campoScheda campo="CPV" title="Codice CPV" obbligatorio="${not isAggiornamentoMassivo}" href="#" speciale="true" >
			<gene:popupCampo titolo="Dettaglio CPV" href="#" />
		</gene:campoScheda>
		
		<gene:callFunction obj="it.eldasoft.sil.w3.tags.funzioni.GestioneW3CPVFunction" parametro="${gene:getValCampo(key, 'NUMGARA')};${gene:getValCampo(key, 'NUMLOTT')}" />
		<gene:campoScheda addTr="false" visibile="${isAggiornamentoMassivo}">
			<tr class="elimina_el_scheda_mult" id="rowEliminaCPVSecondari">
				<td style="text-align: center; background-color: #ddd;"><input type="checkbox" name="elimina_cpv" class="dt-checkboxes"></td><td></td><td><i>Rimuovi i CPV secondari esistenti</i></td>
			</tr>
		</gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='W3CPV'/>
			<jsp:param name="chiave" value='${key}'/>
			<jsp:param name="nomeAttributoLista" value='datiW3CPV' />
			<jsp:param name="idProtezioni" value="W3CPV" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/w3/w3cpv/w3cpv-interno-scheda.jsp" />
			<jsp:param name="arrayCampi" value="'W3CPV_CODGARA','W3CPV_CODLOTT', 'W3CPV_NUM_CPV_', 'W3CPV_CPV_'" />
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="sezioneListaVuota" value="false"/>
			<jsp:param name="titoloSezione" value="CPV Secondario" />
			<jsp:param name="titoloNuovaSezione" value="Nuovo CPV Secondario" />
			<jsp:param name="descEntitaVociLink" value="Descrizione CPV Secondario" />
			<jsp:param name="msgRaggiuntoMax" value="e Descrizione CPV Secondario" />
		</jsp:include>
		<gene:campoScheda>
			<td colspan="2"><b>&nbsp;</b></td>
		</gene:campoScheda>
		<gene:campoScheda title="Categoria merceologica oggetto della fornitura di cui al DPCM soggetti aggregatori" campo="CATEGORIA_MERC" obbligatorio="${not isAggiornamentoMassivo}" visibile="${ver_simog eq 4}" gestore="it.eldasoft.sil.w3.tags.gestori.decoratori.GestoreCategorieMerceologiche"/>
		<gene:campoScheda campo="ID_SCELTA_CONTRAENTE" obbligatorio="${not isAggiornamentoMassivo}"/>
		<gene:campoScheda campo="ID_AFF_RISERVATI"/>
		
		<gene:campoScheda addTr="false">
			<tr id="rowListaCOND" name ="W3LOTT_LISTACOND" >
				<td colspan="2"><br><b>Condizioni che giustificano il ricorso alla procedura negoziata</b></td>
			</tr>
		</gene:campoScheda>
		<gene:callFunction obj="it.eldasoft.sil.w3.tags.funzioni.GestioneW3CONDFunction" parametro="${gene:getValCampo(key, 'NUMGARA')};${gene:getValCampo(key, 'NUMLOTT')}" />
		<gene:campoScheda addTr="false" visibile="${isAggiornamentoMassivo}">
			<tr class="elimina_el_scheda_mult" id="rowEliminaCondizioni">
				<td style="text-align: center; background-color: #ddd;"><input type="checkbox" name="elimina_condizioni" id="elimina_condizioni" class="dt-checkboxes"></td><td></td><td><i>Rimuovi le condizioni esistenti</i></td>
			</tr>
		</gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='W3COND'/>
			<jsp:param name="chiave" value='${key}'/>
			<jsp:param name="nomeAttributoLista" value='datiW3COND' />
			<jsp:param name="idProtezioni" value="W3COND" />
			<jsp:param name="sezioneListaVuota" value="false" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/w3/w3cond/w3cond-interno-scheda.jsp"/>
			<jsp:param name="arrayCampi" value="'W3COND_NUMGARA_', 'W3COND_NUMLOTT_', 'W3COND_NUM_COND_', 'W3COND_ID_CONDIZIONE_' "/>
			<jsp:param name="arrayVisibilitaCampi" value="false,false,false,true" />
			<jsp:param name="titoloSezione" value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Condizione n. " />
			<jsp:param name="titoloNuovaSezione" value=" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Nuova condizione" />
			<jsp:param name="descEntitaVociLink" value="condizione" />
			<jsp:param name="msgRaggiuntoMax" value="e condizioni"/>
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="numMaxDettagliInseribili" value="5"/>
		</jsp:include>
		
		<gene:campoScheda>
			<td colspan="2"><br><b>I.2) Dati economici</b></td>
		</gene:campoScheda>
	<c:if test="${not isAggiornamentoMassivo}">	
		<gene:campoScheda campo="IMPORTO_LOTTO" obbligatorio="true"/>
		<gene:campoScheda campo="IMPORTO_OPZIONI" title="di cui per opzioni/ripetizioni"/>
		<gene:campoScheda campo="IMPORTO_ATTUAZIONE_SICUREZZA" title="di cui per attuazione della sicurezza"/>
		<gene:campoScheda campo="IMPORTO_SA" visibile="false" defaultValue="0"/>
		<gene:campoScheda campo="IMPORTO_IMPRESA" visibile="false" defaultValue="0"/>
	</c:if>	
		<gene:campoScheda campo="DURATA_AFFIDAMENTO"/>
		<gene:campoScheda campo="DURATA_RINNOVI"/>

		<gene:campoScheda>
			<td colspan="2"><br><b>I.3) Categoria prevalente ed ulteriori categorie</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="ID_CATEGORIA_PREVALENTE" obbligatorio="${not isAggiornamentoMassivo}"/>
		<gene:callFunction obj="it.eldasoft.sil.w3.tags.funzioni.GestioneW3LOTTCATEFunction" parametro="${gene:getValCampo(key, 'NUMGARA')};${gene:getValCampo(key, 'NUMLOTT')}" />
		<gene:campoScheda addTr="false" visibile="${isAggiornamentoMassivo}">
			<tr class="elimina_el_scheda_mult" id="rowEliminaUlterioriCategorie">
				<td style="text-align: center; background-color: #ddd;"><input type="checkbox" name="elimina_ulteriori_categorie" class="dt-checkboxes"></td><td></td><td><i>Rimuovi le ulteriori categorie esistenti</i></td>
			</tr>
		</gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='W3LOTTCATE'/>
			<jsp:param name="chiave" value='${key}'/>
			<jsp:param name="nomeAttributoLista" value='datiW3LOTTCATE' />
			<jsp:param name="idProtezioni" value="W3LOTTCATE" />
			<jsp:param name="sezioneListaVuota" value="false" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/w3/w3lottcate/w3lottcate-interno-scheda.jsp"/>
			<jsp:param name="arrayCampi" value="'W3LOTTCATE_NUMGARA_', 'W3LOTTCATE_NUMLOTT_', 'W3LOTTCATE_NUMCATE_', 'W3LOTTCATE_CATEGORIA_' "/>
			<jsp:param name="arrayVisibilitaCampi" value="false,false,false,true" />
			<jsp:param name="titoloSezione" value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ulteriore categoria n. " />
			<jsp:param name="titoloNuovaSezione" value=" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Altra categoria" />
			<jsp:param name="descEntitaVociLink" value="altra categoria" />
			<jsp:param name="msgRaggiuntoMax" value="e categorie"/>
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="numMaxDettagliInseribili" value="5"/>
		</jsp:include>
		
		<gene:campoScheda>
			<td colspan="2"><br><b>I.4) Luogo di esecuzione</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="PROVINCIA_ESECUZIONE" campoFittizio="true"	definizione="T2;0;Agx15;;;" title="Provincia" value="${descrizioneProvincia}" >
		<c:set var="functionId" value="default_${!empty datiRiga.GARE_PROSLA}" />
				<c:if test="${!empty datiRiga.GARE_PROSLA}">
					<c:set var="parametriWhere" value="T:${datiRiga.GARE_PROSLA}" />
				</c:if>
		</gene:campoScheda>
		<gene:archivio titolo="Comuni" 
			lista="gene/commons/istat-comuni-lista-popup.jsp" 
			scheda="" 
			schedaPopUp=""
			campi="G_COMUNI.PROVINCIA;G_COMUNI.DESCRI;G_COMUNI.CODISTAT" 
			functionId="${functionId}" 
			parametriWhere="${parametriWhere}"
			chiave=""
			formName="" 
			inseribile="false">
			<gene:campoScheda campoFittizio="true" campo="COM_PROLAV" definizione="T9" visibile="false" />
			<gene:campoScheda campo="COMUNE_ESECUZIONE" campoFittizio="true" definizione="T120;0;;;" title="Comune"	value="${descrizioneComune}" />
			<gene:campoScheda campo="LUOGO_ISTAT" />
		</gene:archivio>
		
		<gene:campoScheda campo="LUOGO_NUTS" href="#" modificabile="false" speciale="true" >
			<gene:popupCampo titolo="Dettaglio codice NUTS" href="#" />
		</gene:campoScheda>
		
		<gene:campoScheda>
			<td colspan="2"><br><b>I.6) Riferimento alla programmazione</b></td>
		</gene:campoScheda>	
		<gene:campoScheda campo="FLAG_DL50" obbligatorio="${not isAggiornamentoMassivo}" defaultValue="1"/>
		<gene:campoScheda campo="PRIMA_ANNUALITA"/>
		<gene:campoScheda campo="ANNUALE_CUI_MININF"/>
		
		<gene:campoScheda>
			<td colspan="2"><br><b>I.7) Ripetizioni</b></td>
		</gene:campoScheda>	
		<gene:campoScheda campo="FLAG_PREVEDE_RIP" title="L'appalto prevede ripetizioni o altre opzioni?" defaultValue="2" obbligatorio="${not isAggiornamentoMassivo}"/>
		<gene:campoScheda campo="MOTIVO_COLLEGAMENTO" obbligatorio="${not isAggiornamentoMassivo}"/>
		<gene:campoScheda campo="CIG_ORIGINE_RIP"/>
		<gene:campoScheda campo="FLAG_PNRR_PNC" obbligatorio="${not isAggiornamentoMassivo}"/>

		<gene:campoScheda>
			<td colspan="2"><br><b>I.8) CUP</b></td>
		</gene:campoScheda>	
		<gene:campoScheda campo="FLAG_CUP" obbligatorio="${not isAggiornamentoMassivo}"/>
		<gene:campoScheda addTr="false">
			<tr id="rowListaCUP" name ="W3LOTT_LISTACUP" >
				<td colspan="2"><b><br>Lista dei CUP associati al CIG</b></td>
			</tr>
		</gene:campoScheda>
		<gene:callFunction obj="it.eldasoft.sil.w3.tags.funzioni.GestioneW3LOTTCUPFunction" parametro="${gene:getValCampo(key, 'NUMGARA')};${gene:getValCampo(key, 'NUMLOTT')}" />
		<gene:campoScheda addTr="false" visibile="${isAggiornamentoMassivo}">
			<tr class="elimina_el_scheda_mult" id="rowEliminaCup">
				<td style="text-align: center; background-color: #ddd;"><input type="checkbox" name="elimina_cup" id="elimina_cup" class="dt-checkboxes"></td><td></td><td><i>Rimuovi i CUP esistenti</i></td>
			</tr>
		</gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='W3LOTTCUP'/>
			<jsp:param name="chiave" value='${key}'/>
			<jsp:param name="nomeAttributoLista" value='datiW3LOTTCUP' />
			<jsp:param name="idProtezioni" value="W3LOTTCUP" />
			<jsp:param name="sezioneListaVuota" value="false" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/w3/w3lottcup/w3lottcup-interno-scheda.jsp"/>
			<jsp:param name="arrayCampi" value="'W3LOTTCUP_NUMGARA_', 'W3LOTTCUP_NUMLOTT_', 'W3LOTTCUP_NUMCUP_', 'W3LOTTCUP_CUP_', 'W3LOTTCUP_DATI_DIPE_' "/>
			<jsp:param name="arrayVisibilitaCampi" value="false,false,false,true,true" />
			<jsp:param name="titoloSezione" value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CUP " />
			<jsp:param name="titoloNuovaSezione" value=" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CUP " />
			<jsp:param name="descEntitaVociLink" value="CUP" />
			<jsp:param name="msgRaggiuntoMax" value="e CUP"/>
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="numMaxDettagliInseribili" value="5"/>
		</jsp:include>
		
		
		<gene:campoScheda addTr="false">
			<tr id="sezioneTutelaParita" name ="sezioneTutelaParita" >
				<td colspan="2"><br><b>I.9) Tutela delle pari opportunit&#224; generazionali e di genere nonch&#233; per l'inclusione delle persone con disabilit&#224;</b></td>
			</tr>
		</gene:campoScheda>
		<c:if test="${not empty cig_acc_quadro}">		
			<gene:campoScheda campo="FLAG_DEROGA_ADESIONE" title="Deroga per adesione ad AQ? (valorizzare solo se AQ precedente al 30/12/2021)"/>
		</c:if>
		<gene:campoScheda campo="FLAG_PREVISIONE_QUOTA"/>
		<gene:campoScheda campo="QUOTA_FEMMINILE"/>
		<gene:campoScheda campo="QUOTA_GIOVANILE"/>
		<gene:campoScheda addTr="false">
			<tr id="rowListaDeroghe" name ="W3LOTT_LISTADEROGHE" >
				<td colspan="2"><b><br>Lista dei motivi deroga</b></td>
			</tr>
		</gene:campoScheda>
		<gene:callFunction obj="it.eldasoft.sil.w3.tags.funzioni.GestioneW3LOTTDEROGHEFunction" parametro="${gene:getValCampo(key, 'NUMGARA')};${gene:getValCampo(key, 'NUMLOTT')}" />
		<gene:campoScheda addTr="false" visibile="${isAggiornamentoMassivo}">
			<tr class="elimina_el_scheda_mult" id="rowEliminaMotiviDeroga">
				<td style="text-align: center; background-color: #ddd;"><input type="checkbox" name="elimina_motivi_deroga" id="elimina_motivi_deroga" class="dt-checkboxes"></td><td></td><td><i>Rimuovi i motivi deroga esistenti</i></td>
			</tr>
		</gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='W3LOTTDEROGHE'/>
			<jsp:param name="chiave" value='${key}'/>
			<jsp:param name="nomeAttributoLista" value='datiW3LOTTDEROGHE' />
			<jsp:param name="idProtezioni" value="W3LOTTDEROGHE" />
			<jsp:param name="sezioneListaVuota" value="false" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/w3/w3lottderoghe/w3lottderoghe-interno-scheda.jsp"/>
			<jsp:param name="arrayCampi" value="'W3LOTTDEROGHE_NUMGARA_', 'W3LOTTDEROGHE_NUMLOTT_', 'W3LOTTDEROGHE_IDDEROGA_', 'W3LOTTDEROGHE_CODDEROGA_' "/>
			<jsp:param name="arrayVisibilitaCampi" value="false,false,false,true" />
			<jsp:param name="titoloSezione" value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Motivo deroga " />
			<jsp:param name="titoloNuovaSezione" value=" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Motivo deroga " />
			<jsp:param name="descEntitaVociLink" value="motivo deroga" />
			<jsp:param name="msgRaggiuntoMax" value="i motivi deroga"/>
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="numMaxDettagliInseribili" value="5"/>
		</jsp:include>
		
		<gene:campoScheda>
			<td colspan="2">&nbsp;</td>
		</gene:campoScheda>
		
		<gene:campoScheda addTr="false">
			<tr id="rowListaMisure" name ="W3LOTT_LISTAMISURE" >
				<td colspan="2"><b><br>Misure premiali</b></td>
			</tr>
		</gene:campoScheda>
		<gene:campoScheda campo="FLAG_MISURE_PREMIALI"/>
		<gene:callFunction obj="it.eldasoft.sil.w3.tags.funzioni.GestioneW3MISPREMIALIFunction" parametro="${gene:getValCampo(key, 'NUMGARA')};${gene:getValCampo(key, 'NUMLOTT')}" />
	<gene:campoScheda addTr="false" visibile="${isAggiornamentoMassivo}">
			<tr class="elimina_el_scheda_mult"id="rowEliminaMisurePremiali">
				<td style="text-align: center; background-color: #ddd;"><input type="checkbox" name="elimina_misure_premiali" id="elimina_misure_premiali" class="dt-checkboxes"></td><td></td><td><i>Rimuovi le misure premiali esistenti</i></td>
			</tr>
		</gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
			<jsp:param name="entita" value='W3MISPREMIALI'/>
			<jsp:param name="chiave" value='${key}'/>
			<jsp:param name="nomeAttributoLista" value='datiW3MISPREMIALI' />
			<jsp:param name="idProtezioni" value="W3MISPREMIALI" />
			<jsp:param name="sezioneListaVuota" value="false" />
			<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/w3/w3mispremiali/w3mispremiali-interno-scheda.jsp"/>
			<jsp:param name="arrayCampi" value="'W3MISPREMIALI_NUMGARA_', 'W3MISPREMIALI_NUMLOTT_', 'W3MISPREMIALI_IDMISURA_', 'W3MISPREMIALI_CODMISURA_' "/>
			<jsp:param name="arrayVisibilitaCampi" value="false,false,false,true" />
			<jsp:param name="titoloSezione" value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Misura premiale " />
			<jsp:param name="titoloNuovaSezione" value=" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Misura premiale " />
			<jsp:param name="descEntitaVociLink" value="misura premiale" />
			<jsp:param name="msgRaggiuntoMax" value="e misure premiali"/>
			<jsp:param name="usaContatoreLista" value="true"/>
			<jsp:param name="numMaxDettagliInseribili" value="5"/>
		</jsp:include>
		
		<c:if test="${datiRiga.W3GARA_STATO_SIMOG eq '5'
			or datiRiga.W3GARA_STATO_SIMOG eq '6' or datiRiga.W3GARA_STATO_SIMOG eq '7'}">
			<gene:redefineInsert name="pulsanteNuovo" />
			<gene:redefineInsert name="schedaNuovo" />
		</c:if>

		<c:if test="${datiRiga.W3LOTT_STATO_SIMOG eq '5' 
			or datiRiga.W3LOTT_STATO_SIMOG eq '6'
			or datiRiga.W3LOTT_STATO_SIMOG eq '7'
			or datiRiga.W3GARA_STATO_SIMOG eq '5'
			or datiRiga.W3GARA_STATO_SIMOG eq '6'
			or datiRiga.W3GARA_STATO_SIMOG eq '7'}">
			<gene:redefineInsert name="pulsanteModifica" />
			<gene:redefineInsert name="schedaModifica" />
		</c:if>

		<gene:campoScheda>
			<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
		</gene:campoScheda>
		
		<gene:fnJavaScriptScheda funzione="gestioneFLAG_ESCLUSO('#W3LOTT_FLAG_ESCLUSO#')" elencocampi="W3LOTT_FLAG_ESCLUSO" esegui="true" />
		<gene:fnJavaScriptScheda funzione="gestioneFLAG_REGIME('#W3LOTT_FLAG_REGIME#')" elencocampi="W3LOTT_FLAG_REGIME" esegui="true" />
		<gene:fnJavaScriptScheda funzione="gestioneFLAG_DL50('#W3LOTT_FLAG_DL50#')" elencocampi="W3LOTT_FLAG_DL50" esegui="true" />
		<gene:fnJavaScriptScheda funzione="gestioneSCELTA_TIPO_CONTRATTO('#W3LOTT_TIPO_CONTRATTO#')" elencocampi="W3LOTT_TIPO_CONTRATTO" esegui="true" />
		<gene:fnJavaScriptScheda funzione="gestioneRESET_VALORI_TIPO_CONTRATTO()" elencocampi="W3LOTT_TIPO_CONTRATTO" esegui="false" />
		<gene:fnJavaScriptScheda funzione='gestioneOBBLIGATORIO_CUP("#W3LOTT_FLAG_CUP#")'	elencocampi="W3LOTT_FLAG_CUP" esegui="true" />
		<gene:fnJavaScriptScheda funzione="gestioneSCELTA_CONTRAENTE('#W3LOTT_ID_SCELTA_CONTRAENTE#')" elencocampi="W3LOTT_ID_SCELTA_CONTRAENTE" esegui="true" />
		<gene:fnJavaScriptScheda funzione="gestioneSCELTA_CONTRAENTE2('#W3LOTT_ID_SCELTA_CONTRAENTE#')" elencocampi="W3LOTT_ID_SCELTA_CONTRAENTE" esegui="false" />
		<gene:fnJavaScriptScheda funzione="gestioneFLAG_PREVEDE_RIP('#W3LOTT_FLAG_PREVEDE_RIP#')" elencocampi="W3LOTT_FLAG_PREVEDE_RIP" esegui="false" />
		<gene:fnJavaScriptScheda funzione='gestioneOBBLIGATORIO_MOTIVODEROGA("#W3LOTT_FLAG_PREVISIONE_QUOTA#")'	elencocampi="W3LOTT_FLAG_PREVISIONE_QUOTA" esegui="true" />
		<gene:fnJavaScriptScheda funzione='gestioneOBBLIGATORIO_MISURAPREMIALE("#W3LOTT_FLAG_MISURE_PREMIALI#")' elencocampi="W3LOTT_FLAG_MISURE_PREMIALI" esegui="true" />
		<gene:fnJavaScriptScheda funzione='gestioneTutelaPariOpportunita("#W3LOTT_FLAG_DEROGA_ADESIONE#","#W3LOTT_FLAG_PNRR_PNC#")' elencocampi="W3LOTT_FLAG_DEROGA_ADESIONE;W3LOTT_FLAG_PNRR_PNC" esegui="true" />
		
		
		 <c:if test='${modoAperturaScheda ne "VISUALIZZA"}' >
			<gene:fnJavaScriptScheda funzione='changeComune("#PROVINCIA_ESECUZIONE#", "COM_PROLAV")' elencocampi='PROVINCIA_ESECUZIONE' esegui="false"/>
			<gene:fnJavaScriptScheda funzione='setValueIfNotEmpty("PROVINCIA_ESECUZIONE", "#COM_PROLAV#")' elencocampi='COM_PROLAV' esegui="false"/>
		</c:if>
	<c:if test="${isAggiornamentoMassivo}">
		<gene:redefineInsert name="schedaConferma">		
				<tr>
					<td class="vocemenulaterale" id="">
						<a href="javascript:confermaCustom();" title="Conferma" tabindex="1559">
							Conferma</a>
					</td>
				</tr>
		</gene:redefineInsert>
		<gene:redefineInsert name="schedaAnnulla">		
				<tr>
					<td class="vocemenulaterale" id="">
						<a href="javascript:annullaCustom();" title="Annulla" tabindex="1600">
							Annulla</a>
					</td>
				</tr>
		</gene:redefineInsert>
		<gene:redefineInsert name="pulsanteSalva">
			<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:confermaCustom();">
		</gene:redefineInsert>
		<gene:redefineInsert name="pulsanteAnnulla">
				<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaCustom();">
			</gene:redefineInsert>
	</c:if>
	</gene:formScheda>

	<gene:javaScript>	
	var ready=false;
		$(window).ready(function () {
			_creaFinestraAlberoCpvVP();
			_creaLinkAlberoCpvVP($("#W3LOTT_CPV").parent(), "${modo}", $("#W3LOTT_CPV"), $("#W3LOTT_CPVview"));
			
			for (var i=1; i <= maxIdW3CPVVisualizzabile; i++) {
				var l1 = "#W3CPV_CPV_" + i;
				var l2 = "#W3CPV_CPV_" + i + "view";
				_creaLinkAlberoCpvVP($(l1).parent(), "${modo}", $(l1), $(l2));
			}
			
			$("input[name*='CPV']").attr('readonly','readonly');
			$("input[name*='CPV']").attr('tabindex','-1');
			$("input[name*='CPV']").css('border-color','#A3A6FF');
			$("input[name*='CPV']").css('border-width','1px');
			$("input[name*='CPV']").css('background-color','#E0E0E0');
			
			_creaFinestraAlberoNUTS();
			_creaLinkAlberoNUTS($("#W3LOTT_LUOGO_NUTS").parent(), "${modo}", $("#W3LOTT_LUOGO_NUTS"), $("#W9LOTT_LUOGO_NUTSview") );

			$("input[name^='W3LOTT_LUOGO_NUTS']").attr('readonly','readonly');
			$("input[name^='W3LOTT_LUOGO_NUTS']").attr('tabindex','-1');
			$("input[name^='W3LOTT_LUOGO_NUTS']").css('border-width','1px');
			$("input[name^='W3LOTT_LUOGO_NUTS']").css('background-color','#E0E0E0');
			
			if(${isAggiornamentoMassivo}){
				var tdCnt=0;
				var last_row= $('.dettaglio-notab tr').length;
				$('.dettaglio-notab tr').not(".elimina_el_scheda_mult").each(function(index){
					var td= $('<td/>');
					if($(this).find('td.etichetta-dato').length || $(this).find('td.etichetta-dato-archivio').length){
						if($(this).is("[id*='W3LOTT_']")){
						var ck = $('<input />', {
							type : 'checkbox',
							id : 'td' + tdCnt,
							class : 'dt-checkboxes',
							value : name,
							change: (event) => handleCheckbox(event.target.id)
						})
						td.append(ck);
						  tdCnt++; 
						td.css({"text-align": "center","background-color": "#ddd"});
						$(this).find('td.valore-dato input').not(':hidden').hide().val('');
						$(this).find('td.valore-dato input:hidden').each(function() {//eseguo backup della definizione, da ripristinare in caso di attivazione dell'input
							  var $t = $(this);
							  if($t.is("[id='W3LOTT_LUOGO_NUTS']")){
								  //il nuts ha una gestione particolare
							  }
							  else{
							  $t.attr({
								  backupvalue: $t.attr('value')+'000' //aggiungo dei numeri affinchè in update risulti una variazione del campo
								})
								.removeAttr('value');
							  }
							});
							
						$(this).find('td.valore-dato select').hide().val('');
						$(this).find('td.valore-dato a').css("pointer-events","none");
						}
						
					}
					if(index==0){
						var title = $('<b/>', {
							text: 'Attiva modifica'
						})
						td.css({"min-width":"90px","vertical-align": "bottom","text-align": "center"}).prepend(title);
						$(this).prepend(td);
					}else{
						if(index==last_row-1){
							td.css({"background-color": "#D3E4FF"});
						}
						$(this).prepend(td);
					}
				});
				$('#rowPROVINCIA_ESECUZIONE').find('td.valore-dato select').hide();
				$('#rowCOMUNE_ESECUZIONE').find('td.valore-dato input').hide();
				$('#W3LOTT_LUOGO_NUTS').val("");
				$('#W3LOTT_LUOGO_NUTSview').text("");
			}
			ready=true;
			
		});
		
		function handleCheckbox(id){
			if($('input#'+id).is(':checked')) {
				$('input#'+id).closest('tr').find('td.valore-dato input').show();
				$('input#'+id).closest('tr').find('td.valore-dato input:hidden').each(function() {//ripristino il value, necessario per triggerare l'update
					  var $t = $(this);
					  $t.attr({
						  value: $t.attr('backupvalue')
						})
						.removeAttr('backupvalue');
					});
				$('input#'+id).closest('tr').find('td.valore-dato select').show();
				$('input#'+id).closest('tr').find('td.valore-dato a').css("pointer-events","auto");	
				if($('input#'+id).closest('tr').is('#rowW3LOTT_LUOGO_ISTAT')){
					$('#rowPROVINCIA_ESECUZIONE').find('td.valore-dato select').show();
					$('#rowCOMUNE_ESECUZIONE').find('td.valore-dato input').show();
				}
			}else{
				$('input#'+id).closest('tr').find('td.valore-dato input').not(':hidden').hide().val('');
				$('input#'+id).closest('tr').find('td.valore-dato input:hidden').each(function() {//eseguo backup della definizione, da ripristinare in caso di attivazione dell'input
							  var $t = $(this);
							  $t.attr({
								  backupvalue: $t.attr('value')
								})
								.removeAttr('value');
							});
				$('input#'+id).closest('tr').find('td.valore-dato select').hide().val('').change();
				$('input#'+id).closest('tr').find('td.valore-dato a').css("pointer-events","none");	
				if($('input#'+id).closest('tr').is('#rowW3LOTT_LUOGO_ISTAT')){
					$('#rowPROVINCIA_ESECUZIONE').find('td.valore-dato select').hide().val('').change();
					$('#rowCOMUNE_ESECUZIONE').find('td.valore-dato input').hide();
				}
			}
		//queste funzioni mi servono per triggherare le funzioni associate alle schede multiple nascoste, come se avessi impostato un valore
		if($('input#'+id).closest('tr').is('#rowW3LOTT_FLAG_CUP'))
			$('#W3LOTT_FLAG_CUP').change();
		if($('input#'+id).closest('tr').is('#rowW3LOTT_ID_SCELTA_CONTRAENTE'))
			$('#W3LOTT_ID_SCELTA_CONTRAENTE').change();
		if($('input#'+id).closest('tr').is('#rowW3LOTT_FLAG_PREVISIONE_QUOTA'))
			$('#W3LOTT_FLAG_PREVISIONE_QUOTA').change();
		if($('input#'+id).closest('tr').is('#rowW3LOTT_FLAG_MISURE_PREMIALI'))
			$('#W3LOTT_FLAG_MISURE_PREMIALI').change();
		if($('input#'+id).closest('tr').is('#rowW3LOTT_FLAG_PNRR_PNC'))
			$('#W3LOTT_FLAG_PNRR_PNC').change();
		}
		
		

		function gestioneFLAG_PREVEDE_RIP(value){
			if(value=='1'){
				document.forms[0].W3LOTT_MOTIVO_COLLEGAMENTO.value='10';
			}
		}
		
		function gestioneFLAG_ESCLUSO(value){
			if (value == "1"){
				$("#rowW3LOTT_ID_ESCLUSIONE").show();
			} else{
				$("#rowW3LOTT_ID_ESCLUSIONE").hide().find('input[id^=td]').prop('checked', false);
				if(${isAggiornamentoMassivo})
					$('#W3LOTT_ID_ESCLUSIONE').hide();
				$('#W3LOTT_ID_ESCLUSIONE').val("");
			}
		}
		
		function gestioneFLAG_REGIME(value){
			if (value == "1"){
				$("#rowW3LOTT_ART_REGIME").show();
			} else{
				$("#rowW3LOTT_ART_REGIME").hide().find('input[id^=td]').prop('checked', false);
				if(${isAggiornamentoMassivo})
					$('#W3LOTT_ART_REGIME').hide();
				$('#W3LOTT_ART_REGIME').val("");
			}
		}
		
		function gestioneFLAG_DL50(value){
			if (value == "1"){
				$("#rowW3LOTT_PRIMA_ANNUALITA").show();
				$("#rowW3LOTT_ANNUALE_CUI_MININF").show();
			} else{
				$("#rowW3LOTT_PRIMA_ANNUALITA").hide().find('input[id^=td]').prop('checked', false);
				$("#rowW3LOTT_ANNUALE_CUI_MININF").hide().find('input[id^=td]').prop('checked', false);
				$('#W3LOTT_PRIMA_ANNUALITA').val("");
				$('#W3LOTT_ANNUALE_CUI_MININF').val("");
				if(${isAggiornamentoMassivo}){
					$('#W3LOTT_PRIMA_ANNUALITA').hide();
					$('#W3LOTT_ANNUALE_CUI_MININF').hide();
				}
			}
		}
		
		function gestioneOBBLIGATORIO_CUP(valore){
			$('#elimina_cup').prop('checked', false);
			if (valore == 1) {
				for (index = 1; index<= getValue("NUMERO_W3LOTTCUP"); index++) {
					setValue("W3LOTTCUP_DEL_W3LOTTCUP_" + index,null);
				}
				$("#rowListaCUP").show();
				$("#rowEliminaCup").show();
				$("[id^='rowW3LOTT_LISTACUP']").show();
				$("[id^='rowLinkAddW3LOTTCUP']").show();
				$("[id^='rowW3LOTTCUP_CUP_']").each(function () {
					if($(this).is('[show=true]'))
					$(this).show();
				})
				$("[id^='rowW3LOTTCUP_DATI_DIPE_']").each(function () {
					if($(this).is('[show=true]'))
					$(this).show();
				})
				$("[id^='rowtitoloW3LOTTCUP_']").each(function () {
					if($(this).is('[show=true]'))
					$(this).show();
				})
				
			} else {
				for (index = 1; index<= getValue("NUMERO_W3LOTTCUP"); index++) {
					setValue("W3LOTTCUP_DEL_W3LOTTCUP_" + index,"1");
				}
				$("#rowListaCUP").hide();
				$("#rowEliminaCup").hide();
				$("[id^='rowW3LOTT_LISTACUP']").hide();
				$("[id^='rowLinkAddW3LOTTCUP']").hide();
				$("[id^='rowW3LOTTCUP_CUP_']").each(function () {
					if($(this).is(':visible'))
						$(this).attr("show",true);
					$(this).hide();
				})
				$("[id^='rowW3LOTTCUP_DATI_DIPE_']").each(function () {
					if($(this).is(':visible'))
						$(this).attr("show",true);
					$(this).hide();
				})
				$("[id^='rowtitoloW3LOTTCUP_']").each(function () {
					if($(this).is(':visible'))
						$(this).attr("show",true);
					$(this).hide();
				})
				if(ready && $('#W3LOTT_FLAG_CUP').is(':visible')){
					$('#elimina_cup').prop('checked', true);
				}
			}
		}
		
		function gestioneTutelaPariOpportunita(valore,pnrr){
			$('#elimina_motivi_deroga').prop('checked', false);
			$('#elimina_misure_premiali').prop('checked', false);
			if(pnrr==1){
				$("#sezioneTutelaParita").show();
				if (valore != 1){
					$("#rowW3LOTT_FLAG_PREVISIONE_QUOTA").show();
					$("#rowW3LOTT_FLAG_MISURE_PREMIALI").show();
				} else{
					$("#rowW3LOTT_FLAG_PREVISIONE_QUOTA").hide().find('input[id^=td]').prop('checked', false);
					$("#rowW3LOTT_FLAG_MISURE_PREMIALI").hide().find('input[id^=td]').prop('checked', false);
					gestioneOBBLIGATORIO_MOTIVODEROGA("");
					gestioneOBBLIGATORIO_MISURAPREMIALE("");
					$('#W3LOTT_FLAG_PREVISIONE_QUOTA').val('').change();
					$('#W3LOTT_FLAG_MISURE_PREMIALI').val('').change();
					if(${isAggiornamentoMassivo}){
						$('#W3LOTT_FLAG_PREVISIONE_QUOTA').hide();
						$('#W3LOTT_FLAG_MISURE_PREMIALI').hide();
						if(ready && $('#W3LOTT_FLAG_PNRR_PNC').is(':visible')){
							$('#elimina_motivi_deroga').prop('checked', true);
							$('#elimina_misure_premiali').prop('checked', true);
						}
					}
				}
			}else  {
				if(!"${cig_acc_quadro}")
					$("#sezioneTutelaParita").hide();
				if (valore == 2){
					$("#rowW3LOTT_FLAG_PREVISIONE_QUOTA").show();
					$("#rowW3LOTT_FLAG_MISURE_PREMIALI").show();
				} else{
					$("#rowW3LOTT_FLAG_PREVISIONE_QUOTA").hide().find('input[id^=td]').prop('checked', false);
					$("#rowW3LOTT_FLAG_MISURE_PREMIALI").hide().find('input[id^=td]').prop('checked', false);
					gestioneOBBLIGATORIO_MOTIVODEROGA("");
					gestioneOBBLIGATORIO_MISURAPREMIALE("");
					$('#W3LOTT_FLAG_PREVISIONE_QUOTA').val('').change();
					$('#W3LOTT_FLAG_MISURE_PREMIALI').val('').change();
					if(${isAggiornamentoMassivo}){
						$('#W3LOTT_FLAG_PREVISIONE_QUOTA').hide();
						$('#W3LOTT_FLAG_MISURE_PREMIALI').hide();
						if(ready && $('#W3LOTT_FLAG_PNRR_PNC').is(':visible')){
							$('#elimina_motivi_deroga').prop('checked', true);
							$('#elimina_misure_premiali').prop('checked', true);
						}
					}
				}
			}
		}
		
		function gestioneOBBLIGATORIO_MOTIVODEROGA(valore){
			$('#elimina_motivi_deroga').prop('checked', false);
			if (valore == "Q" || valore == "N") {
				for (index = 1; index<= getValue("NUMERO_W3LOTTDEROGHE"); index++) {
					setValue("W3LOTTDEROGHE_DEL_W3LOTTDEROGHE_" + index,null);
				}
				$("#rowListaDeroghe").show();
				$("#rowEliminaMotiviDeroga").show();
				$("[id^='rowLinkAddW3LOTTDEROGHE']").show();
				$("[id^='rowW3LOTTDEROGHE_CODDEROGA_']").each(function () {
					if($(this).is('[show=true]'))
					$(this).show();
				})
				$("[id^='rowtitoloW3LOTTDEROGHE_']").each(function () {
					if($(this).is('[show=true]'))
					$(this).show();
				})
			} else {
				for (index = 1; index<= getValue("NUMERO_W3LOTTDEROGHE"); index++) {
					setValue("W3LOTTDEROGHE_DEL_W3LOTTDEROGHE_" + index,"1");
				}
				$("#rowListaDeroghe").hide();
				$("#rowEliminaMotiviDeroga").hide();
				$("[id^='rowLinkAddW3LOTTDEROGHE']").hide();
				$("[id^='rowW3LOTTDEROGHE_CODDEROGA_']").each(function () {
					if($(this).is(':visible'))
						$(this).attr("show",true);
					$(this).hide();
				})
				$("[id^='rowtitoloW3LOTTDEROGHE_']").each(function () {
					if($(this).is(':visible'))
						$(this).attr("show",true);
					$(this).hide();
				})
				if(ready && $('#W3LOTT_FLAG_PREVISIONE_QUOTA').is(':visible')){
					$('#elimina_motivi_deroga').prop('checked', true);
				}
			}
			if (valore == "Q"){
				$("#rowW3LOTT_QUOTA_FEMMINILE").show();
				$("#rowW3LOTT_QUOTA_GIOVANILE").show();
			} else{
				$("#rowW3LOTT_QUOTA_FEMMINILE").hide().find('input[id^=td]').prop('checked', false);
				$("#rowW3LOTT_QUOTA_GIOVANILE").hide().find('input[id^=td]').prop('checked', false);
				$('#W3LOTT_QUOTA_FEMMINILE').val("");
				$('#W3LOTT_QUOTA_GIOVANILE').val("");
				if(${isAggiornamentoMassivo}){
						$('#W3LOTT_QUOTA_FEMMINILE').hide();
						$('#W3LOTT_QUOTA_GIOVANILE').hide();
				}
			}
			
		}
		function gestioneOBBLIGATORIO_MISURAPREMIALE(valore){
			$('#elimina_misure_premiali').prop('checked', false);
			if (valore == 1) {
				for (index = 1; index<= getValue("NUMERO_W3MISPREMIALI"); index++) {
					setValue("W3MISPREMIALI_DEL_W3MISPREMIALI_" + index,null);
				}
				$("#rowListaMisure").show();
				$("#rowEliminaMisurePremiali").show();
				$("[id^='rowLinkAddW3MISPREMIALI']").show();
				$("[id^='rowW3MISPREMIALI_CODMISURA_']").each(function () {
					if($(this).is('[show=true]'))
					$(this).show();
				})
				$("[id^='rowtitoloW3MISPREMIALI_']").each(function () {
					if($(this).is('[show=true]'))
					$(this).show();
				})
			} else {
				for (index = 1; index<= getValue("NUMERO_W3MISPREMIALI"); index++) {
					setValue("W3MISPREMIALI_DEL_W3MISPREMIALI_" + index,"1");
				}
				$("#rowListaMisure").hide();
				$("#rowEliminaMisurePremiali").hide();
				$("[id^='rowLinkAddW3MISPREMIALI']").hide();
				$("[id^='rowMsgLastW3MISPREMIALI").hide();
				$("[id^='rowW3MISPREMIALI_CODMISURA_']").each(function () {
					if($(this).is(':visible'))
						$(this).attr("show",true);
					$(this).hide();
				})
				$("[id^='rowtitoloW3MISPREMIALI_']").each(function () {
					if($(this).is(':visible'))
						$(this).attr("show",true);
					$(this).hide();
				})
				if(ready && $('#W3LOTT_FLAG_MISURE_PREMIALI').is(':visible')){
					$('#elimina_misure_premiali').prop('checked', true);
				}
			}
		}
		
		function gestioneSCELTA_TIPO_CONTRATTO(tipo_contratto){
			if (tipo_contratto=='' || tipo_contratto=='S') {
				$("#rowTipologiaFornitura").hide();
				$("[id^='rowW3LOTTTIPI_TIPOLOGIAFORNITURA']").hide();
				$("#rowTipologiaLavoro").hide();
				$("[id^='rowW3LOTTTIPI_TIPOLOGIALAVORO']").hide();
				$("#rowW3LOTT_FLAG_USO_METODI_EDILIZIA").hide();
				$("#W3LOTT_FLAG_USO_METODI_EDILIZIA").val('').change();
			}
		
			if (tipo_contratto=='L') {
				$("#rowTipologiaFornitura").hide();
				$("[id^='rowW3LOTTTIPI_TIPOLOGIAFORNITURA']").hide();
				$("#rowTipologiaLavoro").show();
				$("#rowW3LOTT_FLAG_USO_METODI_EDILIZIA").show();
				$("[id^='rowW3LOTTTIPI_TIPOLOGIALAVORO']").show();
			} 
			
			if (tipo_contratto=='F') {
				$("#rowTipologiaFornitura").show();
				$("[id^='rowW3LOTTTIPI_TIPOLOGIAFORNITURA']").show();
				$("#rowTipologiaLavoro").hide();
				$("[id^='rowW3LOTTTIPI_TIPOLOGIALAVORO']").hide();
				$("#rowW3LOTT_FLAG_USO_METODI_EDILIZIA").hide();
				$("#W3LOTT_FLAG_USO_METODI_EDILIZIA").val('').change();
			} 
		}
		
		function gestioneSCELTA_CONTRAENTE(scelta_contraente){ 
			$('#elimina_condizioni').prop('checked', false);
			
			if (scelta_contraente == 4 || scelta_contraente == 10) {
				for (index = 1; index<= getValue("NUMERO_W3COND"); index++) {
					setValue("W3COND_DEL_W3COND_" + index,null);
				}
				$("#rowListaCOND").show();
				$("#rowEliminaCondizioni").show();
				$("[id^='rowW3LOTT_LISTACOND']").show();
				$("[id^='rowLinkAddW3COND']").show();
				$("[id^='rowW3COND_ID_CONDIZIONE_']").each(function () {
					if($(this).is('[show=true]'))
					$(this).show();
				})
				$("[id^='rowtitoloW3COND_']").each(function () {
					if($(this).is('[show=true]'))
					$(this).show();
				})
				
			} else {
				for (index = 1; index<= getValue("NUMERO_W3COND"); index++) {
					setValue("W3COND_DEL_W3COND_" + index,"1");
				}
				$("#rowListaCOND").hide();
				$("#rowEliminaCondizioni").hide();
				$("[id^='rowW3LOTT_LISTACOND']").hide();
				$("[id^='rowLinkAddW3COND']").hide();
				$("[id^='rowMsgLastW3COND").hide();
				$("[id^='rowW3COND_ID_CONDIZIONE_']").each(function () {
					if($(this).is(':visible'))
						$(this).attr("show",true);
					$(this).hide();
				})
				$("[id^='rowtitoloW3COND_']").each(function () {
					if($(this).is(':visible'))
						$(this).attr("show",true);
					$(this).hide();
				})
				if(ready && $('#W3LOTT_ID_SCELTA_CONTRAENTE').is(':visible')){
					$('#elimina_condizioni').prop('checked', true);
				}
			}

			var vis = (scelta_contraente != 32);
			if (vis) {
				setValue("W3LOTT_ID_AFF_RISERVATI","");
			}
			showObj("rowW3LOTT_ID_AFF_RISERVATI",!vis);
		}
		
		function gestioneSCELTA_CONTRAENTE2(scelta_contraente){
			if (scelta_contraente == 31) {
				document.forms[0].W3LOTT_MOTIVO_COLLEGAMENTO.value='8';
			}
		}
		
		function gestioneRESET_VALORI_TIPO_CONTRATTO(){
			$('[id^="rowW3LOTTTIPI_TIPOLOGIAFORNITURA"]').val("2");
			$('[id^="rowW3LOTTTIPI_TIPOLOGIALAVORO"]').val("2");
		}
		
		modifySelectOption($("#W3LOTT_ID_SCELTA_CONTRAENTE"));
		modifySelectOption($("#W3LOTT_ID_CATEGORIA_PREVALENTE"));
		
		function modifySelectOption(obj) {
			var obj_opt = obj.find("option");
			$.each(obj_opt, function( key, opt ) {
				var _title = opt.title;
				if (_title.length > 110) {
					 _title = _title.substring(0,110) + "...";
				}
				opt.text = _title;
			});
			obj.css("width","550px");	
		}
		
		function changeComune(provincia, nomeUnCampoInArchivio) {
			changeFiltroArchivioComuni(provincia, nomeUnCampoInArchivio);
			setValue("COMUNE_ESECUZIONE", "");
			setValue("W3LOTT_LUOGO_ISTAT", "");
		}

		
		function popupRiallineaCIG(numgara, numlott, codiceCIG) {
			openPopUpActionCustom("${pageContext.request.contextPath}/w3/ConfrontaCIG.do", "metodo=inizializza&numgara=" + numgara + "&numlott=" + numlott + "&cig=" + codiceCIG, "riallineaCig", 500, 450, "yes", "yes");
		}
		function annullaCustom(){
			document.forms[0].modo.value="NUOVO";
			schedaAnnulla();
		}
		
		function confermaCustom(){
			if($('input.dt-checkboxes:checkbox:checked').size()==0){
				alert("Nessuna modifica attivata!");
			}else{
				if(confirm("Si sta per aggiornare in modo massivo i dati dei lotti selezionati. Confermi l'operazione?"))
					schedaConferma();
			}
		}
	</gene:javaScript>
</c:if>