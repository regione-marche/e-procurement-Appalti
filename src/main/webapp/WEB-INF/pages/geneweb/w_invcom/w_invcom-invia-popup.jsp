
<%
	/*
	 * Created on 04-Giu-2010
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

<c:choose>
	<c:when test='${not empty param.commodello}'>
		<c:set var="commodello" value="${param.commodello}"  />
	</c:when>
	<c:otherwise>
		<c:set var="commodello" value="${commodello}" />
	</c:otherwise>
</c:choose>

<gene:template file="popup-template.jsp">
	
	<c:choose>
		<c:when test='${!empty RISULTATO}'>
			<c:set var="idprg" value="${IDPRG}" />
			<c:set var="idcom" value="${IDCOM}" />
			<c:set var="compub" value="${COMPUB}" />
			<c:set var="descc" value="${DESCC}" />
		</c:when>
		<c:otherwise>
			<c:set var="idprg" value="${param.idprg}" />
			<c:set var="idcom" value="${param.idcom}" />
			<c:set var="compub" value="${param.compub}" />
			<c:set var="descc" value="${param.descc}" />
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value="Invia comunicazione" />

	<gene:redefineInsert name="corpo">
	
		<c:choose>
			<c:when test='${RISULTATO != null}'>
				<table class="lista">
					<tr>
						<td>
							<c:choose>
								<c:when test="${RISULTATO eq 'ERRORE.MARCATEMP'}">
									<br>
									<b>Il servizio di marcatura temporale non risponde o risponde in maniera errata, riprovare più tardi oppure contattare il servizio di assistenza.</b>
									<br><br>
								</c:when>
								<c:when test="${compub eq '1'}">
									<br>
									La comunicazione selezionata è stata inviata
									<br><br>
								</c:when>
								<c:otherwise>
									<br>
									La comunicazione selezionata è, ora, nello stato <b>"In uscita"</b>.
									<br>
									<br>
									Controllare in seguito la lista delle comunicazioni 
									e la lista dei soggetti destinatari per verificare l'esito dell'invio.
									<br>
									<br>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td class="comandi-dettaglio" colSpan="2">
							<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;&nbsp;
						</td>
					</tr>	
				</table>
			</c:when>
		
			<c:otherwise>
			
				<c:set var="esistonoSoggettiDestinatari" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsistonoSoggettiDestinatariFunction",pageContext,idprg,idcom)}' />
				<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.EsistonoAllegatiComunicazioneFunction" parametro="${idprg};${idcom}"/>
				<c:set var="esitoControlli" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.ControlliInvioComunicazioniFunction",pageContext,idprg,idcom,commodello)}' />	
				
				<!-- esistonoAllegatiComunicazione = ${esistonoAllegatiComunicazione} -->
								
				<c:set var="modo" value="MODIFICA" scope="request" />
				<table class="lista">
					<tr>
						<td>
							<c:choose>
								<c:when test="${esistonoSoggettiDestinatari eq 'FALSE' and compub ne '1'}">
									<br>
									Non è possibile inviare la comunicazione in quanto non è stato specificato alcun soggetto destinatario
									<br>
									<br>
								</c:when>
								<c:otherwise>
									<c:choose>
										<c:when test="${esistonoAllegatiComunicazione eq 'FALSE' and esitoControlli eq 'OK'}">
											
											<br>
											<b>ATTENZIONE!</b>
											<br><br>
											<b>Non è stato inserito alcun documento allegato.</b>
											<br><br>
											Si intende confermare comunque l'invio della comunicazione selezionata ?
											<br><br>
												
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${esistonoAllegatiFormatoNonValido eq 'TRUE'}">
													<br>
													<b>Documenti allegati con formato non valido.</b>
													<br><br>
													Non è possibile inviare la comunicazione in quanto ci sono degli allegati con un formato non valido.
													<br><br>
												</c:when>
												<c:when test="${esistonoAllegatiDaFirmare eq 'TRUE'}">
													<br>
													<b>Documenti allegati da firmare.</b>
													<br><br>
													Non è possibile inviare la comunicazione in quanto ci sono degli allegati in attesa di firma.
													<br><br>
												</c:when>
												<c:when test="${esitoControlli eq 'NO-TESTOCOM'}">
													<br>
													<b>Testo della comunicazione vuoto.</b>
													<br><br>
													Non è possibile inviare la comunicazione in quanto il testo e' vuoto.
													<br><br>
												</c:when>
												<c:when test="${esitoControlli eq 'NO-DATA'}">
													<br>
													<b>Termini di presentazione documentazione mancanti.</b>
													<br><br>
													Non è possibile inviare la comunicazione in quanto non è stata specificata la data e l'ora di termine presentazione documentazione.
													<br><br>
												</c:when>
												<c:when test="${esitoControlli eq 'NO-TERMINISCA'}">
													<br>
													<b>Termini di presentazione documentazione scaduti.</b>
													<br><br>
													Non è possibile inviare la comunicazione in quanto la data termine presentazione documentazione è precedente alla data corrente.
													<br><br>
												</c:when>
												<c:otherwise>
													<br>
													Si intende confermare l'invio della comunicazione selezionata ?
													<br>
													<br>
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									
									</c:choose>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
					<tr>
						<td>
							<gene:formScheda entita="W_INVCOM" gestisciProtezioni="true"
								gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreW_INVCOMInvia"
								where="W_INVCOM.IDPRG = '${idprg}' AND W_INVCOM.IDCOM = ${idcom}">
								<gene:campoScheda campo="IDPRG" visibile="false" defaultValue='${sessionScope.moduloAttivo}'/>
								<gene:campoScheda campo="IDCOM" visibile="false" />
								<gene:campoScheda campo="COMSTATO" visibile="false"/>
								<gene:campoScheda campo="COMPUB" visibile="false"/>
								<gene:campoScheda campo="COMKEY1" visibile="false"/>
								<gene:campoScheda campo="COMDATAPUB" visibile="false"/>
								<gene:campoScheda campo="IDCFG" visibile="false"/>
								<gene:campoScheda campo="IDCFG_NEW" visibile="false" campoFittizio="true" definizione="T10" value="${param.cenint}"/> 
								<gene:campoScheda campo="IDCOMRIS" visibile="false"/>
								<gene:campoScheda campo="IDPRGRIS" visibile="false"/>
								<gene:campoScheda campo="COMMSGOGG" visibile="false"/>
								<gene:campoScheda campo="COMMSGTES" visibile="false"/>
								<gene:campoScheda campo="COMENT" visibile="false"/>
								<input type="hidden" name="commodello" id="commodello" value="${commodello}" />
								<input type="hidden" name="descc" id="descc" value="${descc}">
							</gene:formScheda>
						</td>
					</tr>
					<tr>
						<td class="comandi-dettaglio" colSpan="2">
							<c:if test="${(compub eq '1' and esistonoAllegatiDaFirmare eq 'FALSE' and esistonoAllegatiFormatoNonValido eq 'FALSE') or (esistonoSoggettiDestinatari eq 'TRUE' 
									and esistonoAllegatiDaFirmare eq 'FALSE'
									and esistonoAllegatiFormatoNonValido eq 'FALSE') and esitoControlli eq 'OK'}">
								<INPUT type="button" class="bottone-azione" value="Invia" title="Invia" onclick="javascript:schedaConferma();">
							</c:if>
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
						</td>
					</tr>
				</table>			
			</c:otherwise>
		</c:choose>
	</gene:redefineInsert>

	<gene:javaScript> 
	
		document.forms[0].jspPathTo.value="geneweb/w_invcom/w_invcom-invia-popup.jsp";
		
		function chiudi(){
			window.opener.historyReload();
			window.close();
		}
		
		function annulla(){
			window.close();
		}
		

		
	</gene:javaScript>

</gene:template>	