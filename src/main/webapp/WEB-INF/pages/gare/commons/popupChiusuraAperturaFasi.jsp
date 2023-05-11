<%
/*
 * Created on: 21-10-2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 /*
  * La popup viene adoperata per l'apertura( o chiusura se non telematica) dello step 
  * delle offerte tecniche.
  * Nel caso di gare telematiche vengono eseguiti dei controlli preliminari bloccanti.
  * Alla fine del processo viene aggiornata la fase della gara.
  *
  */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>


<c:choose>
	<c:when test='${!empty param.operazione}'>
		<c:set var="operazione" value="${param.operazione}" />
	</c:when>
	<c:otherwise>
		<c:set var="operazione" value="${operazione}" />
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
	<c:when test='${not empty param.bustalotti}'>
		<c:set var="bustalotti" value="${param.bustalotti}" />
	</c:when>
	<c:otherwise>
		<c:set var="bustalotti" value="${bustalotti}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty requestScope.operazioneEseguita and requestScope.operazioneEseguita eq "OK"}' >
<script type="text/javascript">
		window.opener.bloccaRichiesteServer();
	<c:choose>
		<c:when test='${operazione eq "ATTIVA" && isGaraLottiConOffertaUnica eq "true" && bustalotti=="2" && requestScope.faseDaImpostare eq "TEC"}'>
			window.opener.avanti();;
		</c:when>	
		<c:when test='${operazione eq "ATTIVA" && (isGaraLottiConOffertaUnica ne "true" || (isGaraLottiConOffertaUnica eq "true" && bustalotti=="1"))}'>
			window.opener.attivaAperturaOfferte();
		</c:when>
		<c:when test='${operazione eq "ATTIVA" && isGaraLottiConOffertaUnica eq "true" && bustalotti=="2"}'>
			window.opener.avanza();
		</c:when>
		<c:otherwise>
			window.opener.disattivaAperturaOfferte();
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
		<c:when test='${not empty param.isProceduraTelematica}'>
			<c:set var="isProceduraTelematica" value="${param.isProceduraTelematica}" />
		</c:when>
		<c:otherwise>
			<c:set var="isProceduraTelematica" value="${isProceduraTelematica}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.paginaAttivaWizard}'>
			<c:set var="paginaAttivaWizard" value="${param.paginaAttivaWizard}" />
		</c:when>
		<c:otherwise>
			<c:set var="paginaAttivaWizard" value="${paginaAttivaWizard}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.isConcProg}'>
			<c:set var="isConcProg" value="${param.isConcProg}" />
		</c:when>
		<c:otherwise>
			<c:set var="isConcProg" value="${isConcProg}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.gestioneConcProg}'>
			<c:set var="gestioneConcProg" value="${param.gestioneConcProg}" />
		</c:when>
		<c:otherwise>
			<c:set var="gestioneConcProg" value="${gestioneConcProg}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${operazione eq "ATTIVA" && (isGaraLottiConOffertaUnica ne "true" || (isGaraLottiConOffertaUnica eq "true" && bustalotti=="1"))}'>
			<c:choose>
				<c:when test="${gestioneConcProg eq 'true' }">
					<gene:setString name="titoloMaschera" value='Procedi a inserimento punteggi' />
				</c:when>
				<c:otherwise>
					<gene:setString name="titoloMaschera" value='Attiva apertura offerte' />
				</c:otherwise>
			</c:choose>
			
		</c:when>
		<c:when test='${operazione eq "ATTIVA" && isGaraLottiConOffertaUnica eq "true" && bustalotti=="2" && (paginaAttivaWizard eq "45" || paginaAttivaWizard eq "35")}'>
			<gene:setString name="titoloMaschera" value='Attiva apertura offerte tecniche' />
		</c:when>
		<c:when test='${operazione eq "ATTIVA" && isGaraLottiConOffertaUnica eq "true" && bustalotti=="2"}'>
			<gene:setString name="titoloMaschera" value='Attiva calcolo aggiudicazione' />
			<c:set var="attivoCalcoloAggiudicazione" value="true" />
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value='Disattiva apertura offerte' />
		</c:otherwise>
	</c:choose>

	<c:if test="${isProceduraTelematica eq 'true' and attivoCalcoloAggiudicazione ne 'true'}">
		<c:set var="codiceGara" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, ngara )}' />
		<c:set var="garaInversa" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetInversaFunction", pageContext, codiceGara)}' />	
		<c:set var="compreq" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCompreqFunction", pageContext, codiceGara)}' />
		<c:set var="esisteBloccoFasi2" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoCondizioniFasiAcquisizioniFunction",  pageContext, ngara, "2")}'/>
		<c:set var="esisteBloccoFasi3" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoCondizioniFasiAcquisizioniFunction",  pageContext, ngara, "3")}'/>
		<c:set var="esisteBloccoFasi4" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteBloccoCondizioniFasiAcquisizioniFunction",  pageContext, ngara, "4")}'/>
		<c:if test="${esisteBloccoFasi2 eq 'true' && esisteBloccoFasi3 eq 'true' && esisteBloccoFasi4 eq 'true'}">
			<c:set var="esisteBloccoFasi" value='true'/>
		</c:if>
		
		<c:if test="${garaInversa ne '1' }">
			<c:set var="esistonoDitteAmmissioneNulla" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteAmmissioneNullaFunction", pageContext, ngara, paginaAttivaWizard )}' />
		</c:if>
		<c:choose>
			<c:when test="${bustalotti == '2' }">
				<c:set var="esistonoDitteAmmissioneSoccorso" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteAmmissioneSoccorsoIstruttorioFunction", pageContext, ngara, paginaAttivaWizard,"true" )}' />
			</c:when>
			<c:otherwise>
				<c:set var="esistonoDitteAmmissioneSoccorso" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteAmmissioneSoccorsoIstruttorioFunction", pageContext, ngara, paginaAttivaWizard,"false" )}' />
			</c:otherwise>
		</c:choose>
		
		
		<c:if test="${bustalotti eq '2' }">
			<c:set var="esistonoDittePartecipazioneNulla" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePartecipazioneNullaFunction", pageContext, ngara, "1" )}' />
		</c:if>
		
		<c:if test="${garaInversa ne '1' }">
			<c:set var="esistonoAcquisizioniOfferteDaElaborare" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, ngara, "FS11A" )}' />
		</c:if>
		
		<c:if test="${operazione eq 'ATTIVA'}">
			<c:set var="esistonoDitteInGara" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteInGaraFunction", pageContext, "NGARA5", ngara," and (fasgar > 2 or fasgar is null)")}' />
			<c:if test="${compreq eq '1' and garaInversa ne '1'  }">
				<c:set var="esistonoDitteEstimpValorizzato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteEstimpValorizzatoFunction", pageContext, ngara )}' />
			</c:if>
		</c:if>
		<c:if test="${isConcProg eq '1' || gestioneConcProg eq 'true'}">
			<c:set var="valoriStato" value="'5','7'"/>		
			<c:set var="esistonoAcquisizioniTecDaElaborare" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteInStatoDaElaborareFunction", pageContext, ngara, "FS11B" ,valoriStato)}' />
			<c:if test="${gestioneConcProg ne 'true'}">
				<c:set var="esistonoAcquisizioniTecDaElaborare" value="false"/>
			</c:if>
			<c:if test="${gestioneConcProg eq 'true'}">
				<c:set var="esistonoDitteInGaraSenzaIdAnonimo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteInGaraFunction", pageContext, "NGARA5", ngara," and (fasgar > 2 or fasgar is null) and idanonimo is null")}' />
			</c:if>
		</c:if>
	</c:if>

	<c:if test="${attivoCalcoloAggiudicazione eq 'true' }">
		<c:set var="esistonoAcquisizioniOfferteDaElaborareFS11C" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoAcquisizioniOfferteDaElaborareFunction", pageContext, ngara, "FS11C" )}' />
		<c:set var="esistonoDitteSenzaPunteggioEconomico" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePunteggiEconomiciNulliFunction", pageContext, ngara,"true","false")}' />
		<c:set var="messaggioControlloEco" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlloPunteggioTecnicoEconomicoFunction", pageContext, ngara,"60", "calcoloAggiudicazione","false" )}' />
		<c:set var="messaggioControlloImporti" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.ControlloImportiEconomiciFunction", pageContext, ngara, "true" )}' />
		<c:if test="${isProceduraTelematica ne 'true'}">
			<c:set var="esistonoDitteSenzaPunteggioTecnico" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePunteggiTecniciNulliFunction", pageContext, ngara,"true","false","false")}' />
			<c:set var="messaggioControlloTec" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlloPunteggioTecnicoEconomicoFunction", pageContext, ngara,"50", "calcoloAggiudicazione","false" )}' />
			<c:set var="messaggioControlloPunteggiLottiOEPV" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.ControlloPunteggiLottiOEPVFunction", pageContext, ngara)}' />
			<c:set var="esitocontrolloRiparametrazioneTec" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ControlloPresenzaRiparametrazioneApplicataFunction", pageContext, ngara, "1","tutti" )}' />
		</c:if>
		<c:if test="${esitocontrolloRiparametrazioneTec eq 'OK' or empty esitocontrolloRiparametrazioneTec}">
			<c:set var="esitocontrolloRiparametrazioneEco" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ControlloPresenzaRiparametrazioneApplicataFunction", pageContext, ngara, "2","tutti" )}' />
		</c:if>
		
		<c:set var="esistonoDitteAmmissioneSoccorso" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.EsistonoDitteAmmissioneSoccorsoIstruttorioFunction", pageContext, ngara, paginaAttivaWizard, "true" )}' />
	</c:if>
	
	<c:if test="${attivoCalcoloAggiudicazione ne 'true' and operazione eq 'ATTIVA'}">
		<c:set var="esitoControlloCommissione" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.ControlliComponentiCommissioneFunction", pageContext, ngara,"false", ngara,isGaraLottiConOffertaUnica)}' />
	</c:if>
	
	<c:set var="modo" value="NUOVO" scope="request" />

	<gene:redefineInsert name="corpo">
		
		<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupChiusuraAperturaDcoAmm">
			<gene:campoScheda>
				<td colSpan="2">
					<c:choose>
						<c:when test="${(isProceduraTelematica eq 'true' && (esisteBloccoFasi eq 'true' ||  esistonoDitteAmmissioneNulla eq 'true' || esistonoDittePartecipazioneNulla eq 'true' || esistonoAcquisizioniOfferteDaElaborare eq 'true'
							|| esistonoAcquisizioniOfferteDaElaborareFS11C eq 'true')) || esistonoDitteSenzaPunteggioEconomico eq 'true' || !empty messaggioControlloEco || !empty messaggioControlloTec || !empty messaggioControlloImporti 
							||  !empty messaggioControlloPunteggiLottiOEPV || esistonoDitteSenzaPunteggioTecnico eq 'true' || esitocontrolloRiparametrazioneEco eq 'NOK' || esitocontrolloRiparametrazioneTec eq 'NOK' || esistonoDitteInGara eq 'false'
							|| esistonoDitteEstimpValorizzato eq 'false' || esistonoDitteAmmissioneSoccorso eq 'true' || esistonoAcquisizioniTecDaElaborare eq 'true' || esistonoDitteInGaraSenzaIdAnonimo eq 'true'}">
							<c:set var="bloccoSalvataggio" value='true'/>
							<c:choose>
								<c:when test="${isProceduraTelematica eq 'true' && esisteBloccoFasi eq 'true'}">
									<br>
									Non &egrave; possibile procedere alla fase di apertura delle offerte perch&egrave; la fase corrente non &egrave; quella di apertura doc. amministrativa. 
									<br>
									<br>
								</c:when>
								<c:when test="${isProceduraTelematica eq 'true' && esistonoDitteAmmissioneNulla eq 'true'}">
									<br>
									Non &egrave; possibile procedere alla fase di apertura delle offerte perch&egrave; deve essere specificato lo stato di ammissione per ogni ditta in gara. 
									<br>
									<br>
								</c:when>
								<c:when test="${esistonoDitteAmmissioneSoccorso eq 'true'}">
									<c:choose>
										<c:when test="${attivoCalcoloAggiudicazione eq 'true' }">
											Non &egrave; possibile procedere all'attivazione del calcolo aggiudicazione perch&egrave; ci sono delle ditte in gara con soccorso istruttorio in corso.
										</c:when>
										<c:otherwise>
											Non &egrave; possibile procedere alla fase di apertura delle offerte perch&egrave; ci sono delle ditte in gara con soccorso istruttorio in corso.
										</c:otherwise>
									</c:choose>
									<br>
									 
									<br>
									<br>
								</c:when>
								<c:when test="${isProceduraTelematica eq 'true' && esistonoDittePartecipazioneNulla eq 'true'}">
									<br>
									Non &egrave; possibile procedere alla fase di apertura delle offerte perch&egrave; deve essere dettagliata la partecipazione ai singoli lotti della gara per ogni ditta in gara. 
									<br>
									<br>
								</c:when>
								<c:when test="${isProceduraTelematica eq 'true' && esistonoAcquisizioniOfferteDaElaborare eq 'true'}">
									<br>
									Non &egrave; possibile procedere alla fase di apertura delle offerte perch&egrave; devono essere prima acquisite tutte le buste amministrative. 
									<br>
									<br>
								</c:when>
								<c:when test="${isProceduraTelematica eq 'true' && esistonoDitteInGara eq 'false'}">
									<br>
									Non &egrave; possibile procedere alla fase di apertura delle offerte perch&egrave; non ci sono ditte in gara. 
									<br>
									<br>
								</c:when>
								<c:when test="${isProceduraTelematica eq 'true' && esistonoDitteEstimpValorizzato eq 'false'}">
									<br>
									Non &egrave; possibile procedere alla fase di apertura delle offerte perch&egrave; deve essere prima fatto il sorteggio sulle ditte in gara per la verifica requisiti. 
									<br>
									<br>
								</c:when>
								<c:when test="${isProceduraTelematica eq 'true' && esistonoAcquisizioniOfferteDaElaborareFS11C eq 'true'}">
									<br>
									Non &egrave; possibile procedere alla fase di calcolo dell'aggiudicazione perch&egrave; devono essere prima acquisite tutte le buste economiche. 
									<br>
									<br>
								</c:when>
								
								<c:when test="${esistonoDitteSenzaPunteggioTecnico eq 'true'}">
									<br>
									Non &egrave; possibile procedere alla fase di calcolo dell'aggiudicazione perch&egrave; ci sono delle ditte in gara per cui non &egrave; stato ancora valorizzato il punteggio tecnico.
									<br>
									<br>
								</c:when>
								
								<c:when test="${esistonoDitteSenzaPunteggioEconomico eq 'true'}">
									<br>
									Non &egrave; possibile procedere alla fase di calcolo dell'aggiudicazione perch&egrave; ci sono delle ditte in gara per cui non &egrave; stato ancora valorizzato il punteggio economico.
									<br>
									<br>
								</c:when>
								<c:when test="${esitocontrolloRiparametrazioneTec eq 'NOK'}">
									<br>
									Non &egrave; possibile procedere alla fase di calcolo dell'aggiudicazione perch&egrave; ci sono delle ditte in gara per cui non &egrave; stata ancora eseguita la riparametrazione dei punteggi tecnici 
									<c:choose>
										<c:when test="${fn:contains(elencoLottiNonRiparam,',') }">
											(verificare i lotti ${elencoLottiNonRiparam }).
										</c:when>
										<c:otherwise>
											(verificare il lotto ${elencoLottiNonRiparam }).
										</c:otherwise>
									</c:choose>
									<br>
									<br>
								</c:when>
								<c:when test="${esitocontrolloRiparametrazioneEco eq 'NOK'}">
									<br>
									Non &egrave; possibile procedere alla fase di calcolo dell'aggiudicazione perch&egrave; ci sono delle ditte in gara per cui non &egrave; stata ancora eseguita la riparametrazione dei punteggi economici 
									<c:choose>
										<c:when test="${fn:contains(elencoLottiNonRiparam,',') }">
											(verificare i lotti ${elencoLottiNonRiparam }).
										</c:when>
										<c:otherwise>
											(verificare il lotto ${elencoLottiNonRiparam }).
										</c:otherwise>
									</c:choose>
									<br>
									<br>
								</c:when>
								<c:when test="${!empty messaggioControlloTec}">
									<br>
									${messaggioControlloTec}
									<br>
									<br>
								</c:when>
								<c:when test="${!empty messaggioControlloEco}">
									<br>
									${messaggioControlloEco}
									<br>
									<br>
								</c:when>
								<c:when test="${!empty messaggioControlloImporti}">
									<br>
									Non &egrave; possibile procedere alla fase di calcolo dell'aggiudicazione perch&egrave; ci sono delle ditte in gara per cui non &egrave; stato ancora valorizzato ${messaggioControlloImporti}.
									<br>
									<br>
								</c:when>
								<c:when test="${!empty messaggioControlloPunteggiLottiOEPV}">
									<br>
									${messaggioControlloPunteggiLottiOEPV}
									<br>
									<br>
								</c:when>
								<c:when test="${esistonoAcquisizioniTecDaElaborare eq 'true'}">
									<br>
									Non &egrave; possibile procedere all'inserimento dei punteggi di valutazione tecnica perch&egrave; devono essere prima acquisite le buste tecniche.
									<br>Attivare la funzione "Acquisisci buste tecniche in forma anonima" nella pagina corrente. 
									<br>
									<br>
								</c:when>
								<c:when test="${esistonoDitteInGaraSenzaIdAnonimo eq 'true'}">
									<br>
									Non &egrave; possibile procedere all'inserimento dei punteggi di valutazione tecnica perch&egrave; deve essere prima fatto lo scarico su file zip dei documenti delle buste tecniche in forma anonima.
									<br>Attivare la funzione "Scarica zip buste tecniche anonime" nella pagina corrente.
									<br>
									<br>
								</c:when>		
							</c:choose>
						</c:when>
						<c:when test='${operazione eq "ATTIVA" && (isGaraLottiConOffertaUnica ne "true" || (isGaraLottiConOffertaUnica eq "true" && bustalotti=="1"))}'>		
							<br>
							<c:choose>
								<c:when test="${gestioneConcProg eq 'true' }">
								Mediante questa funzione &egrave; possibile procedere all'inserimento dei punteggi di valutazione tecnica assegnati in forma anonima.
								<br><br><b>ATTENZIONE:</b> Procedere solo dopo aver assegnato i punteggi.
								</c:when>
								<c:otherwise>
								Mediante questa funzione &egrave; possibile procedere alla fase di apertura delle offerte.
								</c:otherwise>
							</c:choose>
							<c:if test="${ esitoControlloCommissione eq 'NOK'}">
								${msgCommissione}
							</c:if>
							<br>
							<br>
							Confermi l'operazione ?
							<br>
							<br>
						</c:when>
						<c:when test='${operazione eq "ATTIVA" && isGaraLottiConOffertaUnica eq "true" && bustalotti=="2" && (paginaAttivaWizard eq "45" || paginaAttivaWizard eq "35")}'>
							<br>
							Mediante questa funzione &egrave; possibile procedere alla fase di apertura delle offerte tecniche.
							<c:if test="${ esitoControlloCommissione eq 'NOK'}">
								${msgCommissione}
							</c:if>
							<br>
							<br>
							Confermi l'operazione ?
							<br>
							<br>
						</c:when>
						<c:when test='${attivoCalcoloAggiudicazione eq "true"}'>
							<br>
							Mediante questa funzione &egrave; possibile procedere alla fase di calcolo dell'aggiudicazione.
							<br><br>
							Confermi l'operazione ?
							<br>
							<br>
						</c:when>
						<c:otherwise>
							<br>
							Mediante questa funzione &egrave; possibile disattivare la fase di apertura delle offerte.
							<br><br>
							Confermi l'operazione ?
							<br>
							<br>
						</c:otherwise>
					</c:choose>
				</td>
			</gene:campoScheda>
			<input type="hidden" name="operazione" id="operazione" value="${operazione}" />
			<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
			<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
			<input type="hidden" name="bustalotti" id="bustalotti" value="${bustalotti}" />	
			<input type="hidden" name="isProceduraTelematica" id="isProceduraTelematica" value="${isProceduraTelematica}" />
			<c:if test='${operazione eq "ATTIVA" && isGaraLottiConOffertaUnica eq "true" && bustalotti=="2" && (paginaAttivaWizard eq "45" || paginaAttivaWizard eq "35")}'>
				<input type="hidden" name="faseDaImpostare" id="faseDaImpostare" value="TEC" />		
			</c:if>
			<input type="hidden" name="isConcProg" id="isConcProg" value="${isConcProg}" />
			<input type="hidden" name="gestioneConcProg" id="gestioneConcProg" value="${gestioneConcProg}" />
		</gene:formScheda>
  </gene:redefineInsert>
  <c:if test='${requestScope.operazioneEseguita eq "ERRORI" or bloccoSalvataggio eq "true"}' >
  	<gene:redefineInsert name="buttons">
			<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;
	</gene:redefineInsert>
  </c:if>
  <gene:javaScript>

		function conferma() {
			document.forms[0].jspPathTo.value="gare/commons/popupChiusuraAperturaFasi.jsp";
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