<%
/*
 * Created on: 03-02-2016
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
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<c:choose>
	<c:when test='${not empty param.visOffertaEco}'>
		<c:set var="visOffertaEco" value="${param.visOffertaEco}" />
	</c:when>
	<c:otherwise>
		<c:set var="visOffertaEco" value="${visOffertaEco}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty requestScope.operazioneEseguita and requestScope.operazioneEseguita eq "OK"}' >
<script type="text/javascript">
		window.opener.bloccaRichiesteServer();
		<c:choose>
			<c:when test='${visOffertaEco ne true}'>
				window.opener.avanza();
			</c:when>
			<c:otherwise>
				window.opener.avanti();
			</c:otherwise>
		</c:choose>
		
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
			<c:when test='${not empty param.modlicg}'>
				<c:set var="modlicg" value="${param.modlicg}" />
			</c:when>
			<c:otherwise>
				<c:set var="modlicg" value="${modlicg}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.isGaraLottiConOffertaUnica}'>
				<c:set var="isGaraLottiConOffertaUnica" value="${param.isGaraLottiConOffertaUnica}" />
			</c:when>
			<c:otherwise>
				<c:set var="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
			</c:otherwise>
		</c:choose>
		
		<c:choose>
			<c:when test='${not empty param.fase}'>
				<c:set var="fase" value="${param.fase}" />
			</c:when>
			<c:otherwise>
				<c:set var="fase" value="${fase}" />
			</c:otherwise>
		</c:choose>
		
		
		<c:choose>
			<c:when test='${not empty param.attivaValutazioneTec}'>
				<c:set var="attivaValutazioneTec" value="${param.attivaValutazioneTec}" />
			</c:when>
			<c:otherwise>
				<c:set var="attivaValutazioneTec" value="${attivaValutazioneTec}" />
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
			<c:when test='${not empty param.codgar}'>
				<c:set var="codgar" value="${param.codgar}" />
			</c:when>
			<c:otherwise>
				<c:set var="codgar" value="${codgar}" />
			</c:otherwise>
		</c:choose>
		
		
			
		<gene:redefineInsert name="addHistory" />
		<gene:redefineInsert name="gestioneHistory" />
		
		<c:choose>
			<c:when test="${visOffertaEco ne true}">
				<gene:setString name="titoloMaschera" value='Attiva calcolo aggiudicazione' />
			</c:when>
			<c:otherwise>
				<gene:setString name="titoloMaschera" value='Attiva apertura offerte economiche' />
			</c:otherwise>
		</c:choose>
		
		<c:set var="controlloAmmissione" value='si'/>
		<c:set var="isProceduraTelematica" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsProceduraTelematicaFunction",  pageContext, codgar)}'/>
		<c:if test="${isProceduraTelematica eq 'true' and bustalotti eq '2' }">
			<c:set var="garaInversa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetInversaFunction", pageContext, codgar)}' />
			<c:if test="${garaInversa eq '1' }">
				<c:set var="controlloAmmissione" value='no'/>
			</c:if>
		</c:if>
		
		<c:if test="${controlloAmmissione eq 'si' }">
			<c:set var="esistonoDitteAmmissioneNulla" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteAmmissioneNullaFunction", pageContext, ngara, fase )}' />
		</c:if>
		
		<c:choose>
			<c:when test="${bustalotti eq '2' }">
				<c:set var="valore" value="${codgar}" />
			</c:when>
			<c:otherwise>
				<c:set var="valore" value="${ngara}" />
			</c:otherwise>	
		</c:choose>
		<c:set var="esistonoAcquisizioniOfferteDaElaborareFS11B" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, valore, "FS11B" )}' />
		
		<c:if test="${bustalotti eq '2' }">
			<c:set var="esistonoDitteInviatoOffertaNulla" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteInviatoOffertaNullaFunction", pageContext, ngara, "1" )}' />
		</c:if>
		
		<c:if test="${attivaValutazioneTec eq 'true' }">
			<c:set var="esistonoDitteSenzaReqmin" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteRequisitiMinimiNulliFunction", pageContext, ngara,isGaraLottiConOffertaUnica)}' />
		</c:if>
		
		<c:if test="${modlicg eq 6}">
			<c:set var="esistonoDitteSenzaPunteggio" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePunteggiTecniciNulliFunction", pageContext, ngara,isGaraLottiConOffertaUnica,"false","false")}' />
		</c:if>
		
		<c:choose>
			<c:when test="${visOffertaEco ne true}">
				<c:set var="tipoMessaggi" value="calcoloAggiudicazione"/>
				<c:set var="incipitMessaggi" value="Non &egrave; possibile procedere alla fase di calcolo di aggiudicazione perch&egrave;"/>
				<c:set var="msgConferma" value="Mediante questa funzione &egrave; possibile procedere alla fase calcolo di aggiudicazione."/>
			</c:when>
			<c:otherwise>
				<c:set var="tipoMessaggi" value="offerteEconomiche"/>
				<c:set var="incipitMessaggi" value="Non &egrave; possibile procedere alla fase di apertura delle offerte economiche perch&egrave;"/>
				<c:set var="msgConferma" value="Mediante questa funzione &egrave; possibile procedere alla fase di apertura delle offerte economiche."/>
			</c:otherwise>
		</c:choose>
		
		
		
		<c:if test="${isProceduraTelematica eq 'true'}">
			<c:choose>
				<c:when test="${bustalotti eq '2' }">
					<c:set var="esitocontrolloRiparametrazioneTec" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ControlloPresenzaRiparametrazioneApplicataFunction", pageContext, codgar, "1", "tutti" )}' />
					<c:set var="esistonoDitteAmmissioneSoccorso" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteAmmissioneSoccorsoIstruttorioFunction", pageContext, ngara, fase,"true" )}' />
				</c:when>
				<c:when test="${bustalotti ne '2' and modlicg eq 6}">
					<c:set var="esitocontrolloRiparametrazioneTec" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ControlloPresenzaRiparametrazioneApplicataFunction", pageContext, ngara, "1", "singolo" )}' />
					<c:if test="${bustalotti eq '1' }">
						<c:set var="esistonoDitteAmmissioneSoccorso" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteAmmissioneSoccorsoIstruttorioFunction", pageContext, ngara, fase,"false" )}' />
					</c:if>
				</c:when>
			</c:choose>
			
			
		</c:if>
		
		<c:set var="messaggioControllo" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlloPunteggioTecnicoEconomicoFunction", pageContext, ngara,fase,tipoMessaggi,"false" )}' />
		
		<c:if test="${esistonoAcquisizioniOfferteDaElaborareFS11B ne 'true' && esistonoDitteAmmissioneNulla ne 'true' && esistonoDitteSenzaPunteggio ne 'true' && esistonoDitteSenzaReqmin ne 'true' && empty messaggioControllo && esistonoDitteInviatoOffertaNulla ne 'true'
		    && esitocontrolloRiparametrazioneTec ne 'NOK' && esistonoDitteAmmissioneSoccorso ne 'true'}">
			
			
			<c:choose>
				<c:when test="${bustalotti eq '1' }">
					<c:set var="controlloPresenzaOffertaTecnica" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlloPresenzaOffertaTecnicaFunction",  pageContext, ngara, "false")}'/>
				</c:when>
				<c:otherwise>
					<c:set var="controlloPresenzaOffertaTecnica" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlloPresenzaOffertaTecnicaFunction",  pageContext, ngara, isGaraLottiConOffertaUnica)}'/>
				</c:otherwise>
			</c:choose>
			
			<c:choose>
				<c:when test="${controlloPresenzaOffertaTecnica eq true }">
					<c:set var="esisteBloccoFasi" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoCondizioniFasiAcquisizioniFunction",  pageContext, ngara, "5")}'/>
				</c:when>
				<c:otherwise>
					<c:set var="esisteBloccoFasi2" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoCondizioniFasiAcquisizioniFunction",  pageContext, ngara, "2")}'/>
					<c:set var="esisteBloccoFasi3" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoCondizioniFasiAcquisizioniFunction",  pageContext, ngara, "3")}'/>
					<c:set var="esisteBloccoFasi4" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoCondizioniFasiAcquisizioniFunction",  pageContext, ngara, "4")}'/>
					<c:choose>
						<c:when test="${ bustalotti eq '1'}">
							<c:set var="fasgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetFASGARFunction", pageContext, ngara)}' />
							<c:set var="esisteBloccoFasi" value='${esisteBloccoFasi2 eq "true" && esisteBloccoFasi3 eq "true" && esisteBloccoFasi4 eq "true" && !empty fasgar}'/>
						</c:when>
						<c:otherwise>
							
							<c:set var="esisteBloccoFasi" value='${esisteBloccoFasi2 eq "true" && esisteBloccoFasi3 eq "true" && esisteBloccoFasi4 eq "true"}'/>
						</c:otherwise>
					</c:choose>
					
				</c:otherwise>
			</c:choose>
			
			<c:set var="funzioneAbilitata" value='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.FASIGARA.AttivaOfferteEconomiche")}'/>
			<c:set var="filtroLivelloUtente" value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_TORN")}' />
			<c:if test='${!empty (filtroLivelloUtente)}'>
				<c:set var="autotizzatoModifica" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetAutorizzatoModificaFunction", pageContext, "CODGAR", codgar, "2")}'/>
			</c:if>
		</c:if>
		
		<c:set var="modo" value="NUOVO" scope="request" />
		
		<gene:redefineInsert name="corpo">
			<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupChiusuraAperturaDcoAmm">
				<gene:campoScheda>
					<td colSpan="2">
						<c:choose>
							<c:when test="${esistonoAcquisizioniOfferteDaElaborareFS11B eq 'true'}">
								<br>
								${incipitMessaggi } ci sono delle ditte in gara per cui non &egrave; stata acquisita la busta tecnica.
								<br>
								<br>
							</c:when>
							<c:when test="${esistonoDitteAmmissioneNulla eq 'true'}">
								<br>
								${incipitMessaggi } ci sono delle ditte in gara per cui non &egrave; stato ancora valorizzato il campo 'Ammissione'.
								<br>
								<br>
							</c:when>
							<c:when test="${esistonoDitteAmmissioneSoccorso eq 'true'}">
								<br>
								${incipitMessaggi } ci sono delle ditte in gara per cui il campo 'Ammissione' &egrave; pari a 'soccorso istruttorio in corso'.
								<br>
								<br>
							</c:when>
							<c:when test="${esistonoDitteSenzaReqmin eq 'true'}">
								<br>
								${incipitMessaggi } ci sono delle ditte in gara per cui non &egrave; stato ancora valorizzato il campo 'Conforme ai requisiti minimi'.
								<br>
								<br>
							</c:when>
							<c:when test="${esistonoDitteSenzaPunteggio eq 'true'}">
								<br>
								${incipitMessaggi } ci sono delle ditte in gara per cui non &egrave; stato ancora valorizzato il punteggio tecnico.
								<br>
								<br>
							</c:when>
							<c:when test="${esistonoDitteInviatoOffertaNulla eq 'true'}">
								<br>
								${incipitMessaggi } ci sono delle ditte in gara per cui non &egrave; stato ancora valorizzato il campo 'Inviato offerta'.
								<br>
								<br>
							</c:when>
							<c:when test="${esitocontrolloRiparametrazioneTec eq 'NOK'}">
								<br>
								${incipitMessaggi } ci sono delle ditte in gara per cui non &egrave; stata ancora eseguita la riparametrazione dei punteggi tecnici
								<c:if test="${not empty elencoLottiNonRiparam and elencoLottiNonRiparam ne '' }">
									<c:choose>
										<c:when test="${fn:contains(elencoLottiNonRiparam,',') }">
											(verificare i lotti ${elencoLottiNonRiparam }).
										</c:when>
										<c:otherwise>
											(verificare il lotto ${elencoLottiNonRiparam }).
										</c:otherwise>
									</c:choose>
									
								</c:if>
								<br>
								<br>
							</c:when>
							<c:when test="${!empty messaggioControllo}">
								<br>
								${messaggioControllo}
								<br>
								<br>
							</c:when>		
							<c:when test="${isProceduraTelematica == 'false' || !funzioneAbilitata || esisteBloccoFasi eq 'true' || autotizzatoModifica eq 'false'}">
								<br>
								${incipitMessaggi } dati inconsistenti.
								<br>
								<br>
							</c:when>					
							<c:otherwise>
								<br>
								${msgConferma }
								<c:if test="${visOffertaEco eq true}">
									<c:set var ="parGaraOffUnica" value="${isGaraLottiConOffertaUnica}"/>
									<c:set var ="parCodiceGara" value="${ngara}"/>
									<c:if test="${ bustalotti eq '1'}">
										<c:set var ="parGaraOffUnica" value="bustalotti=1"/>
										<c:set var ="parCodiceGara" value="${codgar}"/>
									</c:if>
									<c:set var="esitoControlloCommissione" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlliComponentiCommissioneFunction", pageContext, parCodiceGara,"false", ngara,parGaraOffUnica)}' />
									<c:if test="${ esitoControlloCommissione eq 'NOK'}">
										${msgCommissione}
									</c:if>
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
				<input type="hidden" id="bustalotti" name="bustalotti" value="${bustalotti}" />
				<input type="hidden" id="codgar" name="codgar" value="${codgar}" />
				<input type="hidden" id="attivaValutazioneTec" name="attivaValutazioneTec" value="${attivaValutazioneTec}" />
				<input type="hidden" id="faseDaImpostare" name="faseDaImpostare" value="${gene:if(visOffertaEco ne true,'CALCOLO','ECO') }" />
				<input type="hidden" id="visOffertaEco" name="visOffertaEco" value="${visOffertaEco}" />
			</gene:formScheda>
		</gene:redefineInsert>
		 <c:if test="${requestScope.operazioneEseguita eq 'ERRORI' or esistonoDitteAmmissioneNulla eq 'true' or esistonoDitteSenzaPunteggio eq 'true' or esistonoDitteSenzaReqmin eq 'true'
		 	or !empty messaggioControllo or isProceduraTelematica == 'false' || !funzioneAbilitata || esisteBloccoFasi eq 'true' || autotizzatoModifica eq 'false' || esistonoDitteInviatoOffertaNulla eq 'true'
		 	|| esistonoAcquisizioniOfferteDaElaborareFS11B eq 'true' || esitocontrolloRiparametrazioneTec eq 'NOK' || esistonoDitteAmmissioneSoccorso eq 'true'}" >
		  	<gene:redefineInsert name="buttons">
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
			</gene:redefineInsert>
		  </c:if>
		<gene:javaScript>
			function conferma() {
				document.forms[0].jspPathTo.value="gare/gare/gare-popup-attivaAperturaEconomiche.jsp";
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

