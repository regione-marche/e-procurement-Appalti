<%
/*
 * Created on: 17-feb-2015
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

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENEWEB" idMaschera="W_INVCOM-schedaWSDM">

	<gene:redefineInsert name="head" >
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
				
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easytabs.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.comunicazione.js"></script>
				
		<style type="text/css">
			.dataTables_filter {
		     	display: none;
			}
			
			.dataTables_length {
				padding-top: 5px;
				padding-bottom: 5px;
			}
			
			.dataTables_length label {
				vertical-align: bottom;
			}
			
			.dataTables_paginate {
				padding-bottom: 5px;
			}
		
			.etabs {
				margin-bottom: 5px;
			}
			
			
			
		</style>
		
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Comunicazione del ${param.comdatins }" />
	
	<c:set var="key1" value="${param.key1}"/>
	<c:set var="riservatezzaAttiva" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsRiservatezzaAttivaFunction", pageContext, key1, param.idconfi)}' scope="request"/>
	
	<gene:redefineInsert name="corpo">
		<gene:redefineInsert name="documentiAssociati"/>
		<gene:redefineInsert name="noteAvvisi"/>
		<input id="idconfi" type="hidden" value="${param.idconfi}" /> 
		<form id="richiestawslogin">
			<table class="dettaglio-notab">
				<input id="syscon" type="hidden" value="${profiloUtente.id}" /> 
				<input id="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
				<input id="tiposistemaremoto" type="hidden" value="" />
				<input id="tabellatiInDB" type="hidden" value="" />
				<input id="modoapertura" type="hidden" value="MODIFICA" /> 
				<input id="entita" type="hidden" value="${param.entita}" /> 
				<input id="key1" type="hidden" value="${param.key1}" /> 
				<input id="key2" type="hidden" value="${param.key2}" />
				<input id="key3" type="hidden" value="${param.key3}" />
				<input id="key4" type="hidden" value="${param.key4}" />
				
				<input id="idprg" type="hidden" value="${param.idprg}" />
				<input id="idcom" type="hidden" value="${param.idcom}"/>
				<input id="idcfg" type="hidden" value="${param.idcfg}"/>
				<input id="tipoPagina" type="hidden" name="tipoPagina" value="COMUNICAZIONE" />
				<input id="chiaveOriginale" type="hidden" value="${param.chiaveOriginale}" />
				<input id="abilitazioneGare" type="hidden" value="${profiloUtente.abilitazioneGare}" />
				<input id="idprofiloutente" type="hidden" value="${profiloUtente.id}"/>  
				<jsp:include page="wsdm-login.jsp"></jsp:include>			
			</table>
		</form>
			
			
		<form id="richiestainserimentoprotocollo">
			<table class="dettaglio-notab">
				<tr>
					<td colspan="2">
						<br>
						<b>Dati dell'elemento documentale da protocollare</b>
					</td>				
				</tr>
				<div style="display: none;" class="error" id="protocollacomunicazionemessaggio"></div>
				<tr>
					<td class="etichetta-dato">Classifica</td>
					<td class="valore-dato">
						<select id="classificadocumento" name="classificadocumento"></select>
					</td>						
				</tr>
				<tr>
					<td class="etichetta-dato">Classifica</td>
					<td class="valore-dato">
						<select id="idtitolazione" name="idtitolazione"></select>
					</td>						
				</tr>
				<tr>
					<td class="etichetta-dato">Codice registro</td>
					<td class="valore-dato">
						<select id="codiceregistrodocumento" name="codiceregistrodocumento"></select>
					</td>						
				</tr>
				<tr>
					<td class="etichetta-dato">Tipo documento</td>
					<td class="valore-dato">
						<select id="tipodocumento" name="tipodocumento"></select>
					</td>						
				</tr>
				<tr>
					<td class="etichetta-dato">Oggetto</td>
					<td class="valore-dato">
						<textarea id="oggettodocumento" name="oggettodocumento" title="Oggetto" class="testo" rows="4" cols="45"></textarea>
					</td>						
				</tr>
				<tr>
					<td class="etichetta-dato">Mittente interno</td>
					<td class="valore-dato">
						<select id="mittenteinterno" name="mittenteinterno"></select>
					</td>						
				</tr>
				<tr>
					<td class="etichetta-dato">Indirizzo mittente</td>
					<td class="valore-dato">
						<select id="indirizzomittente" name="indirizzomittente"></select>
						<span id="indirizzomittenteisualizza" name="indirizzomittenteisualizza" style="display: none;"></span>
					</td>						
				</tr>
				<tr style="display: none;" id="rigaSottotipo">
					<td class="etichetta-dato">Sottotipo</td>
					<td class="valore-dato">
						<select id="sottotipo" name="sottotipo"></select>
					</td>						
				</tr>
				<tr>
					<td class="etichetta-dato">Mezzo invio</td>
					<td class="valore-dato">
						<select id="mezzoinvio" name="mezzoinvio"></select>
					</td>						
				</tr>
				<c:if test="${riservatezzaAttiva eq '1' or riservatezzaAttiva eq 'null' }">
					<tr>
						<td class="etichetta-dato">Livello riservatezza</td>
						<td class="valore-dato">
							<select id="livelloriservatezza" name="livelloriservatezza"></select>
							<span id="livelloriservatezzavisualizza" name="livelloriservatezzavisualizza" ></span>
						</td>						
					</tr>
				</c:if>
				<input id="isRiservatezzaAttiva" type="hidden" value="${riservatezzaAttiva}" />
				<tr style="display: none;">
					<td class="etichetta-dato">Mezzo</td>
					<td class="valore-dato">
						<select id="mezzo" name="mezzo"></select>
					</td>						
				</tr>
				<tr id="trSupporto" style="display: none;">
					<td class="etichetta-dato">Supporto</td>
					<td class="valore-dato">
						<select id="supporto" name="supporto"></select>
					</td>						
				</tr>
				<tr style="display: none;">
					<td class="etichetta-dato" style="display: none;">Ingresso/uscita</td>
					<td class="valore-dato" style="display: none;" >
						<select id="inout" name="inout">
							<option value="OUT">Uscita</option>
						</select>
					</td>						
				</tr>
					
				<tr style="display: none;">
					<td class="etichetta-dato" style="display: none;">Societa</td>
					<td class="valore-dato" style="display: none;" >
						<input id="societa" name="societa"/>
					</td>						
				</tr>
				<tr style="display: none;">
					<td class="etichetta-dato" style="display: none;">RUP</td>
					<td class="valore-dato" style="display: none;" >
						<input id="RUP" name="RUP"/>
					</td>						
				</tr>
				<tr style="display: none;">
					<td class="etichetta-dato" style="display: none;">CodiceGaraLotto</td>
					<td class="valore-dato" style="display: none;" >
						<input id="codicegaralotto" name="codicegaralotto"/>
					</td>						
				</tr>
				<tr style="display: none;">
					<td class="etichetta-dato" style="display: none;">Cig</td>
					<td class="valore-dato" style="display: none;" >
						<input id="cig" name="cig"/>
					</td>						
				</tr>
				<tr style="display: none;">
					<td class="etichetta-dato" style="display: none;">DestinatarioPrincipale</td>
					<td class="valore-dato" style="display: none;" >
						<input id="destinatarioprincipale" name="destinatarioprincipale"/>
					</td>						
				</tr>
				
				<tr>
					<td class="etichetta-dato">Indice</td>
					<td class="valore-dato">
						<select id="idindice" name="idindice"></select>
					</td>						
				</tr>
				<tr>
					<td class="etichetta-dato">Unit&agrave; operativa mittente</td>
					<td class="valore-dato">
						<select id="idunitaoperativamittente" name="idunitaoperativamittente"></select>
					</td>						
				</tr>				
				<tr>
					<td class="etichetta-dato">Numero destinatari</td>
					<td class="valore-dato">
						<input class="readonly" id="numerodestinatari" name="numerodestinatari" title="Numero destinatari" class="testo" type="text" size="5" value="" maxlength="5"/>
					</td>						
				</tr>
				<tr id="sezionenumerodestinatarinocf" style="display: none;">
					<td class="etichetta-dato">Numero destinatari privi di codice fiscale (obbligatorio per l'invio dei dati al protocollo)</td>
					<td class="valore-dato">
						<input class="readonly" id="numerodestinatarinocf" name="numerodestinatarinocf" title="Numero destinatari privi di codice fiscale" class="testo" type="text" size="5" value="" maxlength="5"/>
					</td>						
				</tr>
				<tr id="sezionenumerodestinatarinopec" style="display: none;">
					<td class="etichetta-dato">Numero destinatari privi di pec</td>
					<td class="valore-dato">
						<input class="readonly" id="numerodestinatarinopec" name="numerodestinatarinopec" title="Numero destinatari privi di pec" class="testo" type="text" size="5" value="" maxlength="5"/>
					</td>						
				</tr>
				<tr id="sezionedestinataricomunicazione" style="display: none;">
					<td colspan="2">
						<br>
						<b>Lista dei destinatari</b>
						<div id="destinataricomunicazionecontainer" style="padding-bottom: 10px;"></div>
					</td>
				</tr>
				<tr>
					<td class="etichetta-dato">Numero documenti secondari</td>
					<td class="valore-dato">
						<input class="readonly" id="numeroallegati" name="numeroallegati" title="Numero allegati" class="testo" type="text" size="5" value="" maxlength="5"/>
					</td>						
				</tr>
				<tr id="sezionenumeroallegatinodescr" style="display: none;">
					<td class="etichetta-dato">Numero allegati privi di descrizione (obbligatorio per l'invio dei dati al protocollo)</td>
					<td class="valore-dato">
						<input class="readonly" id="numeroallegatinodescr" name="numeroallegatinodescr" title="Numero allegati privi di descrizione" class="testo" type="text" size="5" value="" maxlength="5"/>
					</td>						
				</tr>
				<tr id="sezionenumeroallegatiattesafirma" style="display: none;">
					<td class="etichetta-dato">Numero allegati in attesa di firma</td>
					<td class="valore-dato">
						<input class="readonly" id="numeroallegatiattesafirma" name="numeroallegatiattesafirma" title="Numero allegati in attesa di firma" class="testo" type="text" size="5" value="" maxlength="5"/>
					</td>						
				</tr>
				<tr id="sezionePosizioneAllegato" style="display: none;">
					<td class="etichetta-dato">Testo della comunicazione come documento principale?	</td>
					<td class="valore-dato" >
						<select id="posAllegato" name="posAllegato">
							<option value="1">Si</option>
							<option value="2" selected>No</option>
						</select>
					</td>
				</tr>
				
				<tr id="sezionedatifascicolo">
					<td colspan="2">
						<b><br>Dati del fascicolo</b>
						<div style="display: none;" class="error" id="documentifascicolomessaggio"></div>
					</td>
				</tr>
				<tr style="display: none;">
					<td class="etichetta-dato">Inserire il nuovo elemento documentale in un fascicolo ?</td>
					<td class="valore-dato">
						<select id="inserimentoinfascicolo" name="inserimentoinfascicolo">
							<option value=""></option>
							<option value="NO">No, non inserire</option>
							<option value="SI_FASCICOLO_ESISTENTE">Si, inserire nel fascicolo gi&agrave; associato</option>
							<option value="SI_FASCICOLO_NUOVO">Si, inserire in un nuovo fascicolo</option>
						</select>
					</td>						
				</tr>
				<tr>
					<td class="etichetta-dato">Anno fascicolo</td>
					<td class="valore-dato"><input id="annofascicolo" name="annofascicolo" title="Anno fascicolo" class="testo" type="text" size="6" value="" maxlength="4">&nbsp;<a href="javascript:gestioneletturafascicoloPrisma();" id="linkleggifascicoloPrisma" style="display: none;">Leggi fascicolo</a></td>
				</tr>
				<tr>
					<td class="etichetta-dato">Numero fascicolo</td>
					<td class="valore-dato"><input id="numerofascicolo" name="numerofascicolo" title="Numero fascicolo" class="testo" type="text" size="24" value="" maxlength="100"></td>
				</tr>
				<tr>
					<td class="etichetta-dato">Codice fascicolo</td>
					<td class="valore-dato"><input id="codicefascicolo" name="codicefascicolo" title="Codice fascicolo" class="testo" type="text" size="47" value="" maxlength="100">&nbsp;<a href="javascript:gestioneletturafascicolo();" id="linkleggifascicolo" style="display: none;">Leggi fascicolo</a></td>
				</tr>
				<tr>
					<td class="etichetta-dato">Oggetto</td>
					<td class="valore-dato"><span id="oggettofascicolo" name="oggettofascicolo"></span></td>
				</tr>
				<tr>
					<td class="etichetta-dato">Oggetto</td>
					<td class="valore-dato">
						<textarea id="oggettofascicolonuovo" name="oggettofascicolonuovo" title="Oggetto fascicolo" class="testo" rows="4" cols="45"></textarea>
					</td>
				</tr>		
				<tr>
					<td class="etichetta-dato">Classifica</td>
					<td class="valore-dato"><span id="classificafascicolodescrizione" name="classificafascicolodescrizione" title="Classifica"></span></td>
				</tr>
				<tr>
					<td class="etichetta-dato">Classifica</td>
					<td class="valore-dato">
						<select id="classificafascicolonuovo" name="classificafascicolonuovo"></select>
						<input id="classificafascicolonuovoPrisma" name="classificafascicolonuovoPrisma" title="Classifica fascicolo" class="testo" type="text" size="24" value="" maxlength="100" style="display: none;">
						<div style="display: none;" class="error" id="classificafascicolonuovomessaggio"></div>
						<input type="hidden" id="classificadescrizione"  name="classificadescrizione"/>
						<input type="hidden" id="voce"  name="voce"/>
					</td>
				</tr>
				<tr id="trtipofascicolo" style="display: none;">
					<td class="etichetta-dato">Tipo</td>
					<td class="valore-dato">
						<select id="tipofascicolonuovo" name="tipofascicolonuovo"></select>
						<span id="tipofascicolo" name="tipofascicolo" style="display: none;" title="Tipo"></span>
					</td>
				</tr>
				<tr>
					<td class="etichetta-dato">Descrizione</td>
					<td class="valore-dato"><span id="descrizionefascicolo" name="descrizionefascicolo"></span></td>
				</tr>
				<tr>
					<td class="etichetta-dato">Descrizione</td>
					<td class="valore-dato">
						<textarea id="descrizionefascicolonuovo" name="descrizionefascicolonuovo" title="Descrizione fascicolo" class="testo" rows="4" cols="45"></textarea>
					</td>
				</tr>
				
				<!-- Campi per FOLIUM, la combinazione di questi campi genera il valore della classifica -->
				<tr style="display: none;">
					<td class="etichetta-dato" >Classifica</td>
					<td class="valore-dato"  >
						<input id="categoria" name="categoria" size="6" maxlength="5" title="categoria"/>.<input id="classe" name="classe" size="6" maxlength="5" title="classe"/>.<input id="sottoclasse" name="sottoclasse" size="6" maxlength="5" title="sottoclasse"/>.<input id="sotto-sottoclasse" name="sotto-sottoclasse" size="6" maxlength="5" title="sotto-sottoclasse"/>.<input id="fascicoloFolium" name="fascicoloFolium" size="6" maxlength="5" title="fascicolo"/>.<input id="titolare" name="titolare" size="6" maxlength="5" title="titolare"/>
					</td>						
				</tr>
				<!-- Campi per FOLIUM -->
												
				<!-- Campi per JDOC -->
				<tr style="display: none;" id="rigaAcronimoRup">
					<td class="etichetta-dato" >Acronimo RUP </td>
					<td class="valore-dato"  >
						<span id="acronimoRup" name="acronimoRup"></span>
					</td>						
				</tr>
				<tr style="display: none;" id = "rigaNomeRup">
					<td class="etichetta-dato" >Nome Rup  </td>
					<td class="valore-dato"  >
						<span id="nomeRup" name="nomeRup"></span>
					</td>						
				</tr>
				
				<!-- Campi per JDOC -->
				
				<tr id="sezioneamministrazioneorganizzativa" style="display: none;">
					<td colspan="2">
						<b><br>Amministrazione organizzativa</b>
						<div style="display: none;" class="error" id="amministrazioneorganizzativamessaggio"></div>
					</td>
				</tr>
				<tr id="sezionecodiceaoo" style="display: none;">
					<td class="etichetta-dato">Codice AOO</td>
					<td class="valore-dato">
						<select id="codiceaoonuovo" name="codiceaoonuovo"></select>
						<select id="codiceaoonuovo_filtro" name="codiceaoonuovo_filtro" style="display: none;"></select>
						<span id="codiceaoo" name="codiceaoo" style="display: none;"></span>
						<input type="hidden" name="codiceaoodes" id="codiceaoodes" value="" />
					</td>
				</tr>
				<tr id="sezionecodiceufficio" style="display: none;">
					<td class="etichetta-dato">Codice ufficio</td>
					<td class="valore-dato">
						<select id="codiceufficionuovo" name="codiceufficionuovo" style="max-width:450px"></select>
						<select id="codiceufficionuovo_filtro" name="codiceufficionuovo_filtro" style="display: none;"></select>
						<span id="codiceufficio" name="codiceufficio" style="display: none;"></span>
						<input type="hidden" name="codiceufficiodes" id="codiceufficiodes" value="" />
					</td>
				</tr>
				<tr id="sezionestrutturacompetente" style="display: none;">
					<td colspan="2">
						<b><br>Struttura competente</b>
						<div style="display: none;" class="error" id="strutturacompetentemessaggio"></div>
					</td>
				</tr>
				<tr id="sezionestruttura" style="display: none;">
					<td class="etichetta-dato">Struttura</td>
					<td class="valore-dato">
						<select id="strutturaonuovo" name="strutturaonuovo" style="max-width:450px"></select>
						<span id="struttura" name="struttura" style="display: none;"></span>
					</td>
				</tr>
				<tr>
					<td class="comandi-dettaglio" colspan="2">
						<INPUT type="button" id="wsdmprotocollapulsante" class="bottone-azione" value="Protocolla ed invia la comunicazione" title="Protocolla ed invia la comunicazione"/>
						&nbsp;
					</td>
				</tr>
				
			</table>
		</form>
		
		<div id="info" style="display: none;">
			<table class="dettaglio-notab">
				<tr>
					<td colspan="2">
						<br>
						<b>Comunicazione protocollata con successo</b>
						<br>
						<br>
						<div id="messageinfo"></div>
						<div id="messageinfoMailStandard">
							<br>
							Inoltre la stessa comunicazione &egrave;, ora, nello stato 'In uscita'.
							<br>
							<br>
							Controllare in seguito la lista delle comunicazioni e la lista dei soggetti destinatari per verificare l'esito dell'invio.
							<br>
							<br>
						</div>
						<div id="messageinfoMailDocumentale">
							<br>
							Inoltre la stessa comunicazione &egrave; stata presa in carico dal documentale <span id="messageinfoMailDocumentaleErr">con messaggi di errore</span>
							<br>
							<br>
						</div>
					</td>
				</tr>
				<tr>
					<td class="comandi-dettaglio" colspan="2">
						<INPUT type="button" id="wsdmritornapulsante" class="bottone-azione" value="Ritorna alla scheda della comunicazione" title="Ritorna alla scheda della comunicazioni"/>
						&nbsp;
					</td>
				</tr>
			</table>
		</div>
	</gene:redefineInsert>
</gene:template>
