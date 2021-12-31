<%
/*
 * Created on: 02-11-2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 /*
  * Pagina a scheda relativa allo step 'Invito' del wizard Fasi gara per l'asta elettronica 
  *
  *
  */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="numeroGara" value='${gene:getValCampo(key, "NGARA")}'/>
<c:set var="codiceGara" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetCodiceGaraFunction", pageContext)}' />

<c:set var="meruolo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMERUOLOFunction",  pageContext, numeroGara)}'/>

<gene:redefineInsert name="head" >
	<script type="text/javascript" src="${contextPath}/js/jquery.formatCurrency-1.4.0.js"></script>
	<script type="text/javascript" src="${contextPath}/js/jquery.plugin.min.js"></script>
	<script type="text/javascript" src="${contextPath}/js/jquery.countdown.min.js"></script>
	<script type="text/javascript" src="${contextPath}/js/jquery.svolgimento.asta.js"></script>
	<script type="text/javascript" src="${contextPath}/js/common-gare.js"></script>
		
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/countdown/jquery.countdown.css" >
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/rilanci/jquery.rilanci.css" >
			
</gene:redefineInsert>


	<tr>
		<td ${stileDati} >
				<gene:formScheda entita="GARE" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreFasiGaraAstaElettronica_Invito">
					<gene:redefineInsert name="noteAvvisi" />
					<gene:redefineInsert name="documentiAssociati"/>
					<gene:redefineInsert name="modelliPredisposti" />
					<gene:redefineInsert name="schedaNuovo" />
					<gene:redefineInsert name="schedaModifica" >
						<c:if test="${stepgar eq step7_5Wizard and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.ConclusioneAstaElettronica')}">
							<td class="vocemenulaterale">
						      	<a href="javascript:concludiAsta('${numeroGara}');" title="Concludi asta" tabindex="1507">
								  Concludi asta
								</a>			  
							</td>
						</c:if>
						<c:if test='${autorizzatoModifiche ne "2" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ImpostaGaraNonAggiud") and param.paginaFasiGara eq "aperturaOffAggProvLottoOffUnica"}'>
						      <tr>
						      <td class="vocemenulaterale">
							      	<c:if test='${isNavigazioneDisattiva ne "1" }'>
											<a href="javascript:impostaGaraNonAggiudicata('${datiRiga.GARE_NGARA}','${datiRiga.GARE_CODGAR1}','${datiRiga.GARE_ESINEG}','${datiRiga.GARE_DATNEG}','${datiRiga.GARE1_NPANNREVAGG}');" title="Imposta lotto non aggiudicato" tabindex="1507">
									</c:if>
									  Imposta lotto non aggiudicato
									<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
								</td>
								</tr>
						</c:if>
						<tr>
							<td class="vocemenulaterale">
								<a href="javascript:indietro();" title='Fase precedente' tabindex="1503">
									< Fase precedente
								</a>
							</td>
						</tr>
						<c:if test="${stepgar > 65 }">
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:avanti();" title='Fase seguente' tabindex="1504">
										Fase seguente >
									</a>
								</td>
							</tr>
						</c:if>
					</gene:redefineInsert>
					<gene:redefineInsert name="addToAzioni">
						
					</gene:redefineInsert>				
					<gene:campoScheda campo="WIZARD_PAGINA_ATTIVA" visibile="false" campoFittizio="true" definizione="N1" value="${paginaAttivaWizard}" />
					
					<gene:campoScheda campo="NGARA" visibile="false" />
					<gene:campoScheda campo="CODGAR1" visibile="false" />
					<gene:campoScheda campo="ESINEG" visibile="false" />
					<gene:campoScheda campo="DATNEG" visibile="false" />
					<gene:campoScheda campo="NPANNREVAGG" entita="GARE1" where="GARE1.NGARA = GARE.NGARA" visibile="false" />
					
					<gene:campoScheda addTr="false">
						<tr>
							<td colspan="2">
							<table class="scheda" id="tabellaFase" style="width: 100%;">
								<tr>
									<td colspan="8">
										<br>
										<div style="display: none;" id="rigaRicaricaDati">
										<a id="ricaricaDitte" class="link-generico" title="Attiva rilettura dati" >Rilettura dati</a> tra <span id="tempoResiduo"></span> secondi
										</div>
										<br>
									</td>
								</tr>
								<tr>
									<td colspan="8" id="TIPO_FASE" class="grassetto"></td>
								</tr>
								<tr >
									<td class="etichetta">Numero fase</td>
									<td id="NUMFASE" class="valore" style="width: 40px;"></td>
									
									<td class="etichetta">Durata minima</td>
									<td id="DURMIN" class="valore"></td>
									
									<td class="etichetta">Durata massima</td>
									<td id="DURMAX" class="valore"></td>
									
									<td class="etichetta">Tempo base</td>
									<td id="TBASE" class="valore"></td>	
								</tr>
								<tr >
									<td class="etichetta">Apertura</td>
									<td id="DATAORAINI" colspan="3" class="valore"></td>
															
									<td class="etichetta">Chiusura</td>
									<td id="DATAORAFINE" colspan="3" class="valore"></td>	
								</tr>
								<tr id="RIGA_TEMPO_RIMANENTE" style="display: none;">
									<td class="etichetta">Tempo rimanente</td>
									<td id="TEMPO_RIMANENTE" colspan="7" ><div id="defaultCountdown" style="width: 200px;"></div></td>
								</tr>
							</table>
							</td>
						</tr>
						<tr>
							<td colspan="2">
							<br>
							<b>Ditte invitate all'asta elettronica</b><i>&nbsp;(selezionare una riga per consultare il dettaglio dei rilanci)</i>
							<br>
							<div id="dittecontainer"></div>
							
							<br>
							<div id="titolomessaggio" style="border-bottom: 1px solid #A0AABA; padding-bottom:3px; display: none;">Dettaglio rilanci della ditta <b><i>&nbsp;<span id="nomeDitta"></span></i></b></div>
							<div id="rilancicontainer"></div>
							</td>
						</tr>
					</gene:campoScheda>
					
					
					
					<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
					<input type="hidden" name="DIREZIONE_WIZARD" id="DIREZIONE_WIZARD" value="" />
					<input type="hidden" id="pgVaiA" name="pgVaiA" value="0" />
					<input type="hidden" id="pgSort" name="pgSort" value="" />
					<input type="hidden" id="pgLastSort" name="pgLastSort" value="" />
					<input type="hidden" id="pgLastValori" name="pgLastValori" value="" />
					<input type="hidden" id="ditteVincitrici_escluseDaAltriLotti" name="ditteVincitrici_escluseDaAltriLotti" value="" />
					<input type="hidden" id="pgAsta" name="pgAsta" value="${pgAsta }" />
					<input type="hidden" id="filtroDocumentazione" name="filtroDocumentazione" value="${tipoDoc }" />
					<input type="hidden" name="WIZARD_PAGINA_ATTIVA" value="${paginaAttivaWizard}" />						
					<input type="hidden" id="updateLista" name="updateLista" value="${updateLista}" />
					<input type="hidden" id="percorsoCartellaImg" name="percorsoCartellaImg" value="${contextPath}/img/" />
				</gene:formScheda>
			</td>
		</tr>
		
		<div id="dettaglioPrezziUnitari" title="Dettaglio prezzi unitari per la ditta" style="display: none;">
			<div id="titoloDettaglioPrezziUnitari" title="Lista dei prezzi unitari" class="testo"></div>
			<br>
			<div id="listaPrezziUnitaricontainer" title="Lista dei prezzi unitari" class="testo"></div>
		</div>
		