<%
	/*
	 * Created on 03-nov-2009
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

<%//Popup per annullare il calcolo aggiudicazione %>

<c:choose>
	<c:when test='${RISULTATO eq "CALCOLOESEGUITO"}' >
		<script type="text/javascript">
				window.opener.listaVaiAPagina(window.opener.document.forms[0].pgCorrente.value);
				window.close();
		</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<c:choose>
		<c:when test='${RISULTATO != null}'>
			<c:set var="ngara" value='${NGARA}' />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${fn:substringAfter(param.ngara,':')}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.bustalotti}'>
			<c:set var="bustalotti" value='${param.bustalotti}' />
		</c:when>
		<c:otherwise>
			<c:set var="bustalotti" value="${bustalotti}" />
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test='${not empty param.annullamentoLottoOffUnica}'>
			<c:set var="annullamentoLottoOffUnica" value='${param.annullamentoLottoOffUnica}' />
		</c:when>
		<c:otherwise>
			<c:set var="annullamentoLottoOffUnica" value="${annullamentoLottoOffUnica}" />
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test='${not empty param.ricastae}'>
			<c:set var="ricastae" value='${param.ricastae}' />
		</c:when>
		<c:otherwise>
			<c:set var="ricastae" value="${ricastae}" />
		</c:otherwise>
	</c:choose>
	
	<c:set var="esisteGestioneOffEco" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteGestioneOffertaUnicaFunction", pageContext, ngara, bustalotti)}'/>
	
	<c:choose>
		<c:when test="${esisteGestioneOffEco eq 'true' }">
			<c:set var="faseFinale" value="'Apertura offerte economiche'"/>
		</c:when>
		<c:otherwise>
			<c:set var="faseFinale" value="'Valutazione tecnica'"/>
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value="Annulla calcolo aggiudicazione" />
	
	<gene:redefineInsert name="corpo">
		<c:set var="chiave" value="${ngara}" />
		
		<c:choose>
			<c:when test='${RISULTATO eq "CALCOLOESEGUITO"}'>
				<c:set var="modo" value="APRI" scope="request" />	
			</c:when>
			<c:otherwise>
				<c:set var="modo" value="MODIFICA" scope="request" />
			</c:otherwise>
		</c:choose>

		<gene:formScheda entita="GARE" where="GARE.NGARA = '${chiave}'" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAnnullaCalcoloAggiudicazione">
			<c:choose>
				<c:when test='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePerAggiudicazioneFunction",pageContext,ngara) == "false"}'>
					<gene:campoScheda>
						<td colSpan="2"><br>Nessun calcolo della ditta aggiudicataria da annullare
							<br>&nbsp;<br>
						</td>
					</gene:campoScheda>
				</c:when>
				<c:otherwise>
					<gene:campoScheda>
						<c:choose>
							<c:when test="${annullamentoLottoOffUnica eq '1' }">
								<td colSpan="2"><br>Viene annullato il calcolo della ditta aggiudicataria.<br><br>
								Confermi l'operazione?<br><br>
								</td>
							</c:when>
							<c:when test="${ricastae eq '1' }">
								<td colSpan="2"><br>Viene annullato il calcolo della ditta aggiudicataria.<br><br>
								Confermi l'operazione?<br><br>
								</td>
							</c:when>
							<c:otherwise>
								<td colSpan="2"><br>Viene annullato il calcolo della ditta aggiudicataria ripristinando i dati 
								alla fase di gara ${faseFinale} e riabilitando di conseguenza in tale fase 
								la modifica dei dati.<br><br>
								Confermi l'operazione?<br><br>
								</td>
							</c:otherwise>
						</c:choose>
						
					</gene:campoScheda>
					
									
				</c:otherwise>
			</c:choose>

			<gene:campoScheda campo="CODGAR1" visibile="false" />
			<gene:campoScheda campo="NGARA" visibile="false" />
			<gene:campoScheda campo="TIPGARG" visibile="false" />
			<gene:campoScheda campo="MODLICG" visibile="false" />

			<gene:campoScheda campo="DITTAP" visibile="false" />
			<gene:campoScheda campo="RIBPRO" visibile="false" />
			<gene:campoScheda campo="IAGPRO" visibile="false" />
			<gene:campoScheda campo="IMPGAR" visibile="false" />
			<gene:campoScheda campo="DITTA" visibile="false" />
			<gene:campoScheda campo="NOMIMA" visibile="false" />
			<gene:campoScheda campo="RIBAGG" visibile="false" />
			<gene:campoScheda campo="IAGGIU" visibile="false" />
			<gene:campoScheda campo="FASGAR" visibile="false" />
			<gene:campoScheda campo="LIMMIN" visibile="false" />
			<gene:campoScheda campo="LIMMAX" visibile="false" />
			<gene:campoScheda campo="NOFVAL" visibile="false" />
			<gene:campoScheda campo="NOFMED" visibile="false" />
			<gene:campoScheda campo="MEDIA" visibile="false" />
							
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
					<c:if test='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoDittePerAggiudicazioneFunction",pageContext,ngara)}'>
						<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">
					</c:if>
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
			</gene:campoScheda>
			<input type="hidden" name="bustalotti" id="bustalotti" value="${bustalotti}" />
			<input type="hidden" name="annullamentoLottoOffUnica" id="annullamentoLottoOffUnica" value="${annullamentoLottoOffUnica}" />
			<input type="hidden" name="ricastae" id="ricastae" value="${ricastae}" />
			<input type="hidden" name="esisteGestioneOffEco" id="esisteGestioneOffEco" value="${esisteGestioneOffEco }" />	
		</gene:formScheda>
		
	</gene:redefineInsert>
	
	<gene:javaScript> 
	
		document.forms[0].jspPathTo.value="gare/gare/gare-popup-annulla-calcoloAggiudicazione.jsp";
	
	    function annulla(){
			window.close();
		}
		
		function conferma(){
			schedaConferma();
		}
		
	</gene:javaScript>
</gene:template>

</div>

</c:otherwise>
</c:choose>