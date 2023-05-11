<%
/*
 * Created on: 04-05-2015
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
	
	<c:set var="riservatezzaAttiva" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.IsRiservatezzaAttivaFunction", pageContext, param.valoreChiaveRiservatezza, param.idconfi)}' scope="request"/>
	
			<table class="dettaglio-notab" id="datiProtocollo">
				<tr id="TitoloDatiDocumento">
					<td colspan="2">
						<br>
						<b>Dati dell'elemento documentale da protocollare</b>
					</td>				
				</tr>
				<div style="display: none;" class="error" id="protocollacomunicazionemessaggio"></div>
				<tr id="classificaDocumento">
					<td class="etichetta-dato">Classifica (*)</td>
					<td class="valore-dato">
						<select id="classificadocumento" name="classificadocumento"></select>
						<select id="classificadocumento_filtro" name="classificadocumento_filtro" style="display: none;"></select>
						<span id="classdoc" name="classdoc" style="display: none;"></span>
						<div style="display: none;" class="error" id="classificadocumentomessaggio"></div>
					</td>						
				</tr>
				<tr id="idTitolazione">
					<td class="etichetta-dato">Classifica (*)</td>
					<td class="valore-dato">
						<select id="idtitolazione" name="idtitolazione"></select>
					</td>						
				</tr>
				<tr id="codRegistroDocumento">
					<td class="etichetta-dato">Codice registro (*)</td>
					<td class="valore-dato">
						<select id="codiceregistrodocumento" name="codiceregistrodocumento"></select>
					</td>						
				</tr>
				<tr id="tipoDocumento">
					<td class="etichetta-dato">Tipo documento (*)</td>
					<td class="valore-dato">
						<select id="tipodocumento" name="tipodocumento"></select>
					</td>						
				</tr>
				<tr id="oggettoDocumento">
					<td class="etichetta-dato">Oggetto (*)</td>
					<td class="valore-dato">
						<textarea id="oggettodocumento" name="oggettodocumento" title="Oggetto" class="testo" rows="4" cols="45"></textarea>
					</td>						
				</tr>
				<tr id="mittenteInterno">
					<td class="etichetta-dato">Mittente interno (*)</td>
					<td class="valore-dato">
						<select id="mittenteinterno" name="mittenteinterno" style="max-width:450px"></select>
					</td>						
				</tr>
				<tr id="indirizzoMittente">
					<td class="etichetta-dato">Indirizzo mittente (*)</td>
					<td class="valore-dato">
						<select id="indirizzomittente" name="indirizzomittente" style="max-width:450px"></select>
						<span id="indirizzomittenteisualizza" name="indirizzomittenteisualizza" style="display: none;"></span>
					</td>						
				</tr>
				<tr style="display: none;" id="rigaSottotipo">
					<td class="etichetta-dato">Sottotipo (*)</td>
					<td class="valore-dato">
						<select id="sottotipo" name="sottotipo"></select>
					</td>						
				</tr>
				<tr style="display: none;" id="rigaTipoFirma">
					<td class="etichetta-dato">Tipo di firma (*)</td>
					<td class="valore-dato">
						<select id="tipofirma" name="tipofirma"></select>
					</td>						
				</tr>
				<tr id="mezzoInvio">
					<td class="etichetta-dato">Mezzo invio <span id="obblmezzoinvio">(*)</span></td>
					<td class="valore-dato">
						<select id="mezzoinvio" name="mezzoinvio"></select>
					</td>						
				</tr>
				<c:if test="${riservatezzaAttiva eq '1' or riservatezzaAttiva eq 'null'}">
				<tr id="sezionelivelloriservatezza">
					<td class="etichetta-dato">Livello riservatezza (*)</td>
					<td class="valore-dato">
						<select id="livelloriservatezza" name="livelloriservatezza"></select>
						<span id="livelloriservatezzavisualizza" name="livelloriservatezzavisualizza" style="display: true;"></span>
					</td>						
				</tr>
				</c:if>
				<tr id="trMezzo" style="display: none;">
					<td class="etichetta-dato">Mezzo (*)</td>
					<td class="valore-dato">
						<select id="mezzo" name="mezzo"></select>
					</td>						
				</tr>
				<tr id="trSupporto" style="display: none;">
					<td class="etichetta-dato">Supporto (*)</td>
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
				
				<tr id="idIndice">
					<td class="etichetta-dato">Indice (*)</td>
					<td class="valore-dato">
						<select id="idindice" name="idindice"></select>
					</td>						
				</tr>
				<tr id="idUnitaoperativaMittente">
					<td class="etichetta-dato">Unit&agrave; operativa mittente (*)</td>
					<td class="valore-dato">
						<select id="idunitaoperativamittente" name="idunitaoperativamittente"></select>
					</td>						
				</tr>				
				<tr id="sezionedatifascicolo">
					<td colspan="2">
						<b><br>Dati del fascicolo</b> <span style="float:right;"><a href="javascript:gestioneletturafascicolo();" id="linkleggiDatiFascicolo" class="linkLettura" style="display: none;">Rileggi dati fascicolo</a></span>
						<br>
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
					<td class="etichetta-dato">Anno fascicolo (*)</td>
					<td class="valore-dato"><input id="annofascicolo" name="annofascicolo" title="Anno fascicolo" class="testo" type="text" size="6" value="" maxlength="4">&nbsp;<a href="javascript:gestioneletturafascicoloPrisma();" id="linkleggifascicoloPrisma" style="display: none;">Leggi fascicolo</a></td>
				</tr>
				<tr>
					<td class="etichetta-dato">Numero fascicolo (*)</td>
					<td class="valore-dato"><input id="numerofascicolo" name="numerofascicolo" title="Numero fascicolo" class="testo" type="text" size="24" value="" maxlength="100"></td>
				</tr>
				<tr>
					<td class="etichetta-dato">Codice fascicolo (*)</td>
					<td class="valore-dato"><input id="codicefascicolo" name="codicefascicolo" title="Codice fascicolo" class="testo" type="text" size="47" value="" maxlength="100">&nbsp;<a href="javascript:gestioneletturafascicolo();" id="linkleggifascicolo" style="display: none;">Leggi fascicolo</a></td>
				</tr>
				<tr>
					<td class="etichetta-dato">Oggetto (*)</td>
					<td class="valore-dato"><span id="oggettofascicolo" name="oggettofascicolo"></span></td>
				</tr>
				<tr>
					<td class="etichetta-dato">Oggetto (*)</td>
					<td class="valore-dato">
						<textarea id="oggettofascicolonuovo" name="oggettofascicolonuovo" title="Oggetto fascicolo" class="testo" rows="4" cols="45"></textarea>
					</td>
				</tr>		
				<tr>
					<td class="etichetta-dato">Classifica <span id="obbligatorio" name="obbligatorio" >(*)</span></td>
					<td class="valore-dato"><span id="classificafascicolodescrizione" name="classificafascicolodescrizione" title="Classifica"></span>
					
					</td>
				</tr>
				<tr>
					<td class="etichetta-dato">Classifica <span id="classificaObbligatoriaNuovo" name="classificaObbligatoriaNuovo" >(*)</span></td>
					<td class="valore-dato">
						<select id="classificafascicolonuovo" name="classificafascicolonuovo"></select>
						<input id="classificafascicolonuovoPrisma" name="classificafascicolonuovoPrisma" title="Classifica fascicolo" class="testo" type="text" size="24" value="" maxlength="100" style="display: none;">
						<input id="classificafascicolonuovoItalprot" name="classificafascicolonuovoItalprot" title="Classifica fascicolo" class="testo" type="text" size="24" value="" maxlength="100" style="display: none;">
						<div style="display: none;" class="error" id="classificafascicolonuovomessaggio"></div>
						<input type="hidden" id="classificadescrizione"  name="classificadescrizione"/>
						<input type="hidden" id="voce"  name="voce"/>
					</td>
				</tr>
				<tr id="trtipofascicolo" style="display: none;">
					<td class="etichetta-dato">Tipo (*)</td>
					<td class="valore-dato">
						<select id="tipofascicolonuovo" name="tipofascicolonuovo"></select>
						<span id="tipofascicolo" name="tipofascicolo" style="display: none;" title="Tipo"></span>
					</td>
				</tr>
				<tr id="trricercafascicolo" style="display: none;">
					<td class="etichetta-dato">Fascicolo (*)</td>
					<td class="valore-dato">
					<select id="listafascicoli" name="listafascicoli" style="min-width:450px;max-width:450px">
					</select>
					<a href="javascript:gestioneletturafascicoliItalprot();" id="linkleggifascicoliItalprot" style="display: none;">Carica fascicoli</a>
					</td>
				</tr>
				<tr>
					<td class="etichetta-dato">Descrizione (*)</td>
					<td class="valore-dato"><span id="descrizionefascicolo" name="descrizionefascicolo"></span></td>
				</tr>
				<tr id="descFascicoloNuovo">
					<td class="etichetta-dato">Descrizione (*)</td>
					<td class="valore-dato">
						<textarea id="descrizionefascicolonuovo" name="descrizionefascicolonuovo" title="Descrizione fascicolo" class="testo" rows="4" cols="45"></textarea>
					</td>
				</tr>
				
				<!-- Campi per FOLIUM, la combinazione di questi campi genera il valore della classifica -->
				<tr style="display: none;">
					<td class="etichetta-dato" >Classifica (*)</td>
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
				
				<tr id="sezioneuocompetenza" style="display: none;">
					<td class="etichetta-dato">Unit&agrave; operativa di competenza (*)</td>
					<td class="valore-dato">
						<input id="uocompetenza" name="uocompetenza"  type="hidden" />
						<input id="uocompetenzadescrizione" name="uocompetenzadescrizione"  type="hidden" />
						<span id="uocompetenzaSpan" name="uocompetenzaSpan"></span>
						<textarea readonly id="uocompetenzaTxt" name="uocompetenzaTxt" title="Unit&agrave; operativa di competenza" class="testo" rows="4" cols="45"></textarea>
						&nbsp;
						<a href="javascript:apriListaUffici();" title="Seleziona unit&agrave; operativa" tabindex="1514" id="selezioneuocompetenza">
							Seleziona unit&agrave; operativa
						</a>
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
					<td class="etichetta-dato">Codice AOO (*)</td>
					<td class="valore-dato">
						<select id="codiceaoonuovo" name="codiceaoonuovo" ></select>
						<select id="codiceaoonuovo_filtro" name="codiceaoonuovo_filtro" style="display: none;"></select>
						<span id="codiceaoo" name="codiceaoo" style="display: none;"></span>
						<input type="hidden" name="codiceaoodes" id="codiceaoodes" value="" />
					</td>
				</tr>
				<tr id="sezionecodiceufficio" style="display: none;">
					<td class="etichetta-dato">Codice ufficio <span id="ufficioObbligatorioNuovo" name="ufficioObbligatorioNuovo" >(*)</span></td>
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
					<td class="etichetta-dato">Struttura (*)</td>
					<td class="valore-dato">
						<select id="strutturaonuovo" name="strutturaonuovo" style="max-width:450px"></select>
						<span id="struttura" name="struttura" style="display: none;"></span>
					</td>
				</tr>
			</table>
	<input id="idprofiloutente" type="hidden" value="${profiloUtente.id}"/>