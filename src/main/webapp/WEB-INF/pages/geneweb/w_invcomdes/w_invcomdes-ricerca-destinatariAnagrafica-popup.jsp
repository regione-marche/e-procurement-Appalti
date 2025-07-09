<%
/*
 * Created on: 22/05/2018
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
		Finestra per impostare le condizioni di filtro per la ricerca da archivio dei destinatari da inserire
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div style="width:97%;">

<gene:template file="popup-template.jsp">
<c:set var="descc" value="${param.descc}" />
	
	<c:choose>
		<c:when test='${not empty param.idprg}'>
			<c:set var="idprg" value="${param.idprg}" />
		</c:when>
		<c:otherwise>
			<c:set var="idprg" value="${idprg}" />
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test='${not empty param.idcom}'>
			<c:set var="idcom" value="${param.idcom}" />
		</c:when>
		<c:otherwise>
			<c:set var="idcom" value="${idcom}" />
		</c:otherwise>
	</c:choose>
		
	<c:choose>
		<c:when test='${not empty param.coment}'>
			<c:set var="coment" value="${param.coment}" />
		</c:when>
		<c:otherwise>
			<c:set var="coment" value="${coment}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.filtroRadio}'>
			<c:set var="filtroRadio" value="${param.filtroRadio}" />
		</c:when>
		<c:otherwise>
			<c:set var="filtroRadio" value="${filtroRadio}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.idconfi}'>
			<c:set var="idconfi" value="${param.idconfi}" />
		</c:when>
		<c:otherwise>
			<c:set var="idconfi" value="${idconfi}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test="${filtroRadio eq '1'}">
			<c:set var="msgSelect" value="Visualizza imprese non inserite tra i destinatari"/>
			<c:set var="msgEtichetta" value="Visualizza solo imprese non ancora inserite tra i destinatari?"/>
			<c:set var="entitaRicerca" value="IMPR" />
			<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetFiltriDestinatariComunicazioniGenericheFunction" />
		</c:when>
		<c:when test="${filtroRadio eq '2'}">
			<c:set var="msgSelect" value="Visualizza tecnici non inseriti tra i destinatari"/>
			<c:set var="msgEtichetta" value="Visualizza solo tecnici non ancora inseriti tra i destinatari?"/>
			<c:set var="entitaRicerca" value="TECNI" />
		</c:when>
		<c:otherwise>
			<c:set var="msgSelect" value="Visualizza utenti/RUP non inseriti tra i destinatari"/>
			<c:set var="msgEtichetta" value="Visualizza solo utenti/RUP non ancora inseriti tra i destinatari?"/>
			<c:set var="entitaRicerca" value="TECNI" />
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value="Ricerca destinatari da anagrafica" />
			
	<gene:redefineInsert name="corpo">
		<gene:formTrova entita="${entitaRicerca }" gestisciProtezioni="true"
			lista="geneweb/w_invcomdes/w_invcomdes-lista.jsp"
			gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.trova.gestori.DestinatariAnagraficaGestoreTrova">
			
			<tr>
				<td colspan="3">
					Ricerca in archivio:
					<input type="radio" value="1" id="radioImpresa" name="filtroAnagrafica" <c:if test='${filtroRadio eq "1"}'>checked="checked"</c:if> onclick="javascript:cambiaEntita(1);" />Imprese
					&nbsp;
					<input type="radio" value="2" id="radioTecnici" name="filtroAnagrafica" <c:if test='${filtroRadio eq "2"}'>checked="checked"</c:if> onclick="javascript:cambiaEntita(2);"/>Tecnici
					<c:if test="${! empty sessionScope.uffint && coment ne 'HOMEPG'}">
						&nbsp;
						<input type="radio" value="3" id="radioUtentiRup" name="filtroAnagrafica" <c:if test='${filtroRadio eq "3"}'>checked="checked"</c:if> onclick="javascript:cambiaEntita(3);"/>Utenti/RUP delle Amministrazioni aderenti
					</c:if>
					<br>
					<br>
				</td>
			
			<c:choose>
				<c:when test="${filtroRadio eq '1'}">
					<c:if test="${coment eq 'HOMEPG' and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare') && filtroRadio eq '1'}" >
						<tr>
							<td colspan="3"><b>Dati generali</b></td>
						</tr>
					</c:if>
					<gene:campoTrova campo="CODIMP" />
					<gene:campoTrova campo="NOMEST" />
					<gene:campoTrova campo="CFIMP" />
					<gene:campoTrova campo="PIVIMP" />
					<gene:campoTrova campo="TIPIMP" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTipoImpresaNoRaggruppamento"/>
					<gene:campoTrova campo="ISMPMI" />
					<gene:campoTrova campo="EMAIIP" />
					<gene:campoTrova campo="EMAI2IP" />
				</c:when>
				<c:when test="${filtroRadio eq '2'}">
					<gene:campoTrova campo="CODTEC" />
					<gene:campoTrova campo="NOMTEC" />
					<gene:campoTrova campo="CFTEC" />
					<gene:campoTrova campo="PIVATEC" />
					<gene:campoTrova campo="EMATEC" />
					<gene:campoTrova campo="EMA2TEC" />
				</c:when>
				<c:otherwise>
					<gene:campoTrova campo="SYSUTE" entita="USRSYS" where="USRSYS.SYSCF = TECNI.CFTEC" />
					<gene:campoTrova campo="SYSCF" entita="USRSYS" where="USRSYS.SYSCF = TECNI.CFTEC" title="Codice fiscale utente"/>
					<gene:campoTrova campo="CODEIN" entita="USR_EIN" from="USRSYS" where="USR_EIN.SYSCON = USRSYS.SYSCON AND USRSYS.SYSCF = TECNI.CFTEC" title="Codice amministrazione"/>
					<gene:campoTrova campo="NOMEIN" entita="UFFINT" from="USRSYS,USR_EIN" where="UFFINT.CODEIN = USR_EIN.CODEIN AND USR_EIN.SYSCON = USRSYS.SYSCON AND USRSYS.SYSCF = TECNI.CFTEC"/>
					<gene:campoTrova campo="CFEIN" entita="UFFINT" from="USRSYS,USR_EIN" where="UFFINT.CODEIN = USR_EIN.CODEIN AND USR_EIN.SYSCON = USRSYS.SYSCON AND USRSYS.SYSCF = TECNI.CFTEC" title="Codice fiscale amministrazione"/>
					<gene:campoTrova campo="NOMTEC" title="Intestazione anagrafica tecnico" />
					<gene:campoTrova campo="EMATEC" />
					<gene:campoTrova campo="EMA2TEC" />
				</c:otherwise>
			</c:choose>
						

			<c:if test="${coment eq 'HOMEPG' and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare') && filtroRadio eq '1'}" >
				<tr>
					<td colspan="3"><b>Dati elenco operatori</b></td>
					</tr>
				<tr>
					<td class="etichetta-dato">Iscrizione a elenco operatori (considera l'anagrafica iscritta direttamente oppure come mandataria di RT)</td>
					<td class="operatore-trova"/>
					<td class="valore-dato-trova">
						<select id="CampoFittIscriz" name="CampoFittIscriz" title="Iscrizione a elenco operatori" onchange="javascript:showAndHideSezioneFiltriElenchi()">
							<option value=""></option>
							<option value="1" <c:if test="${param.campoFittIscriz eq 1}" >selected="selected"</c:if> >Iscritto ad almeno un elenco</option>
							<option value="2" <c:if test="${param.campoFittIscriz eq 2}" >selected="selected"</c:if>>Non iscritto ad alcun elenco</option>
						</select>
					</td>
				</tr>
				
				<tr id="sezioneFiltriElenchi" <c:if test="${param.campoFittIscriz ne 1}" >style="display: none;"</c:if>>
					<td class="etichetta-dato">Codice elenco</td>
					<td class="operatore-trova"/>
					<td class="valore-dato-trova" width="70px">
						<select name="CampoFittNgara" id="CampoFittNgara"">
							<option value="">&nbsp;</option>
								<c:if test='${!empty listaElenchi}'>
									<c:forEach items="${listaElenchi}" var="listaElenchi">
										<option value="${listaElenchi[0]}" <c:if test="${param.campoFittNgara eq listaElenchi[0]}" >selected="selected"</c:if> >${listaElenchi[0]}&nbsp;</option>
									</c:forEach>
								</c:if>
						</select>
					</td>
				</tr>
				<tr id="sezioneFiltriAbilitazioni" <c:if test="${param.campoFittIscriz ne 1}" >style="display: none;"</c:if>>
					<td class="etichetta-dato">Stato abilitazione (riferito allo specifico elenco, se indicato, altrimenti riferito a qualsiasi elenco a cui l'operatore è iscritto)</td>
					<td class="operatore-trova"/>
					<td class="valore-dato-trova" width="70px">
						<select name="CampoFittAbilitaz" id="CampoFittAbilitaz" >
							<option value="">&nbsp;</option>
								<c:if test='${!empty listaAbilitazioni}'>
									<c:forEach items="${listaAbilitazioni}" var="listaAbilitazioni">
										<option value="${listaAbilitazioni[0]}" <c:if test="${param.campoFittAbilitaz eq listaAbilitazioni[0]}" >selected="selected"</c:if> >${listaAbilitazioni[1]}&nbsp;</option>
									</c:forEach>
								</c:if>
						</select>
					</td>
				</tr>
				</c:if>
				<tr>
					<td class="etichetta-dato"/>
					<td class="operatore-trova"/>
					<td class="valore-dato-trova"/>
				</tr>
				<c:if test="${coment eq 'HOMEPG' and gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare') && filtroRadio eq '1'}" >
				<tr id="sezioneFiltriComponentiRT"  <c:if test="${param.campoFittIscriz ne 1}" >style="display: none;"</c:if>>
					<td class="etichetta-dato">Visualizza mandanti RT? (considera le componenti del RT di cui l'anagrafica estratta è mandataria, solo per lo specifico elenco se indicato, altrimenti per qualsiasi elenco)</td>
					<td class="operatore-trova"/>
					<td class="valore-dato-trova">
						<select id="CampoFittRT" name="CampoFittRT" title="Visualizza componenti RT">
							<option value="1" <c:if test="${param.campoFittRT eq 1}" >selected="selected"</c:if> >No</option>
							<option value="2" <c:if test="${param.campoFittRT eq 2}" >selected="selected"</c:if> >Si</option>
						</select>
					</td>
				</tr>
				</c:if>
				<tr>
					<td class="etichetta-dato">${msgEtichetta}</td>
					<td class="operatore-trova"/>
					<td class="valore-dato-trova">
						<select id="CampoFitt" name="CampoFitt" title="${msgSelect}">
							<option value="1" <c:if test="${param.campoFitt eq 1}" >selected="selected"</c:if>>Si</option>
							<option value="2" <c:if test="${param.campoFitt eq 2}" >selected="selected"</c:if>>No</option>
						</select>
					</td>
				</tr>
			
			<input type="hidden" name="idprg" value="${idprg}" />
			<input type="hidden" name="idcom" value="${idcom}" />
			<input type="hidden" name="idconfi" value="${idconfi}" />
			<input type="hidden" name="filtroRadio" value="${filtroRadio}" />
			<input type="hidden" name="entitaRicerca" value="${entitaRicerca}" />
			<input type="hidden" name="descc" value="${descc}" />
			<input type="hidden" name="coment" value="${coment}" />
						
				
		</gene:formTrova>
		
		<gene:javaScript>
			function cambiaEntita(valore){
				if(valore==1){
					document.trova.filtroRadio.value="1";
				}else if(valore==2){
					document.trova.filtroRadio.value="2";
				}else{
					document.trova.filtroRadio.value="3";
				}
				trovaClear();		
			}
			
			function showAndHideSezioneFiltriElenchi(){
					if($('#CampoFittIscriz').val() == "1"){
						$('#sezioneFiltriElenchi').show();
						$('#sezioneFiltriAbilitazioni').show();
						$('#sezioneFiltriComponentiRT').show();
					}
					else{
						$('#sezioneFiltriElenchi').hide();
						$('#CampoFittNgara').val('');
						$('#sezioneFiltriAbilitazioni').hide();
						$('#CampoFittAbilitaz').val('');
						$('#sezioneFiltriComponentiRT').hide();
						$('#CampoFittCompRT').val('');
					}
				}
			$(document).ready(function(){
				if('${coment}' =='HOMEPG' && ${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare')} && ${filtroRadio} == '1')
					window.resizeTo(950,700);
			})
			
		</gene:javaScript>
		
		
  	</gene:redefineInsert>
	
</gene:template>

</div>