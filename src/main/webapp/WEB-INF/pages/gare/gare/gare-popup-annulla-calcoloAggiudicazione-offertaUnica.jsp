<%
	/*
	 * Created on 24-nov-2009
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

<%//Popup per annullare il calcolo aggiudicazione per gare a lotti con offerta unica %>

<c:choose>
	<c:when test='${not empty param.bustalotti}'>
		<c:set var="bustalotti" value='${param.bustalotti}' />
	</c:when>
	<c:otherwise>
		<c:set var="bustalotti" value="${bustalotti}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${RISULTATO eq "CALCOLOESEGUITO"}' >
		<script type="text/javascript">
			<c:choose>
				<c:when test="${bustalotti ne '2' }">
					window.opener.listaVaiAPagina(window.opener.document.forms[0].pgCorrente.value);
					window.close();
				</c:when>
				<c:otherwise>
					window.opener.bloccaRichiesteServer();
					<c:choose>
						<c:when test="${gene:checkProt(pageContext, 'PAGE.VIS.GARE.GARE-scheda.FASIGARA') }">
							var pagina = eval(window.opener.document.pagineForm.activePage.value - 1);
						</c:when>
						<c:otherwise>
							var pagina = 0;
						</c:otherwise>
					</c:choose>
					window.opener.selezionaPagina(pagina);
					window.close();
				</c:otherwise>
			</c:choose>
		</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<c:choose>
		<c:when test='${not empty RISULTATO}'>
			<c:set var="codgar" value='${CODGAR}' />
		</c:when>
		<c:otherwise>
			<c:set var="codgar" value="${param.codgar}" />
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value="Annulla calcolo aggiudicazione" />
	
	<gene:redefineInsert name="corpo">
		<c:set var="chiave" value="${codgar}" />
		
		<c:choose>
			<c:when test='${RISULTATO eq "CALCOLOESEGUITO"}'>
				<c:set var="modo" value="APRI" scope="request" />	
			</c:when>
			<c:otherwise>
				<c:set var="modo" value="MODIFICA" scope="request" />
			</c:otherwise>
		</c:choose>
		
		<c:set var="esistonoLottiAggiudicati" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsistonoLottiAggiudicatiFunction", pageContext, codgar)}'/>
		
		<c:set var="esisteGestioneOffEco" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteGestioneOffertaUnicaFunction", pageContext, codgar, bustalotti)}'/>
		
		<c:choose>
			<c:when test="${esisteGestioneOffEco eq 'true' }">
				<c:set var="faseFinale" value="'Apertura offerte economiche'"/>
			</c:when>
			<c:otherwise>
				<c:set var="faseFinale" value="'Valutazione tecnica'"/>
			</c:otherwise>
		</c:choose>
		
		<gene:formScheda entita="TORN" where="TORN.CODGAR = '${chiave}'" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAnnullaCalcoloAggiudicazione">
			<gene:campoScheda>
				<td colSpan="2">
					<br>Viene annullato il calcolo della ditta aggiudicataria di tutti i lotti della gara
					ripristinando i dati alla fase di gara ${faseFinale } e
					riabilitando di conseguenza in tale fase la modifica dei dati.<br><br>
			<c:choose>
				<c:when test="${esistonoLottiAggiudicati eq 'true' }">
					<b>Non è possibile procedere poichè vi sono dei lotti aggiudicati in via definitiva</b><br><br>
				</c:when>
				<c:otherwise>
					Confermi l'operazione?<br><br>
				</c:otherwise>
			</c:choose>
					
				</td>
			</gene:campoScheda>

			<gene:campoScheda campo="CODGAR" visibile="false" />
			<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="true" />
			<input type="hidden" name="bustalotti" id="bustalotti" value="${bustalotti }" />
			<input type="hidden" name="esisteGestioneOffEco" id="esisteGestioneOffEco" value="${esisteGestioneOffEco }" />	
						
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
					<c:if test="${esistonoLottiAggiudicati ne 'true' }">
						<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">
					</c:if>
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
				</td>
			</gene:campoScheda>
				
		</gene:formScheda>
		
	</gene:redefineInsert>
	
	<gene:javaScript> 
	
		document.forms[0].jspPathTo.value="gare/gare/gare-popup-annulla-calcoloAggiudicazione-offertaUnica.jsp";
	
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