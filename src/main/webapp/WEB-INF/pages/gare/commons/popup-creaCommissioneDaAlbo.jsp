<%
/*
 * Created on: 01/07/2015
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
		Finestra pop-up per la creazione della commissione di gara 
		a partire dall'albo dei nominativi
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


	
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
		<c:when test='${not empty param.codgar}'>
			<c:set var="codgar" value="${param.codgar}" />
		</c:when>
		<c:otherwise>
			<c:set var="codgar" value="${codgar}" />
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test='${not empty param.garaLottiConOffertaUnica}'>
			<c:set var="garaLottiConOffertaUnica" value="${param.garaLottiConOffertaUnica}" />
		</c:when>
		<c:otherwise>
			<c:set var="garaLottiConOffertaUnica" value="${garaLottiConOffertaUnica}" />
		</c:otherwise>
	</c:choose>
	
	
	<gene:setString name="titoloMaschera" value="Estrazione componenti commissione da elenco per la gara ${ngara}" />
	
	<c:set var="modo" value="MODIFICA" scope="request" />
	<c:set var="where" value="NGARA='${ngara}'" scope="request" />
	
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="GARE" where="${where}" gestisciProtezioni="false" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreControlliPreliminariCreaCommissione" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupCreaCommissione">
		<gene:campoScheda campo="CODGAR1"  visibile="false"/>
		<gene:campoScheda campo="NGARA"  visibile="false"/>
		
		<br>
		Mediante questa funzione vengono designati i componenti della commissione di gara
		estraendoli tra quelli abilitati in elenco. I nominativi vengono estratti 
		in modo casuale con rotazione sul numero di presenze in commissione. 
				
		<gene:campoScheda>
		<td colSpan="2">
			<br>
			<c:choose>
				<c:when test='${requestScope.ERRORI eq "SI"}'>
					&nbsp;<br><b>Operazione non completata.</b>
				</c:when>
				<c:when test='${requestScope.commissioneCreata eq "1"}'>
					&nbsp;<br><b>Operazione completata.</b>
				</c:when>
				
				<c:when test='${statoCommissione eq "NORUPSA"}'>
					&nbsp;<br><b>Per procedere all'estrazione dei componenti commissione da elenco specificare
					 la stazione appaltante e il responsabile unico procedimento della gara.</b> 
				</c:when>
				<c:when test='${statoCommissione eq "NOCONF"}'>
					&nbsp;<br><b>Non risulta definita la configurazione della commissione per la tipologia di gara corrente. 
					<br>Non è pertanto possibile procedere all'estrazione.</b>
				</c:when>
				<c:when test='${statoCommissione eq "INCOMPLETA"}'>
					&nbsp;<br><b>La commissione presente risulta incompleta.
					<br>Non risulta pertanto possibile procedere. </b>
				</c:when>
				<c:when test='${statoCommissione eq "ATTESA"}'>
					&nbsp;<br><b>Esistono soggetti indisponibili all'incarico assegnato
					<br>ma in attesa di accettazione.
					<br>Non risulta pertanto possibile procedere all'integrazione.</b> 
				</c:when>
				<c:when test='${statoCommissione eq "SOSTITUZIONE"  }'>
					&nbsp;<br><b>La commissione di gara non è completa.
					<br>Confermi l'estrazione da elenco dei nominativi per integrare la commissione di gara 
					con i componenti mancanti?</b>
				</c:when>
				<c:when test='${statoCommissione eq "COMPLETA"}'>
					&nbsp;<br><b>La commissione risulta completa.
					<br>Non si procede pertanto a nessuna estrazione.</b>
				</c:when>
				<c:when test='${statoCommissione eq "VUOTA" }'>
					&nbsp;<br><b>Confermi l'estrazione da elenco dei componenti della commissione?</b>
				</c:when>
				
				<c:otherwise>
					&nbsp;<b>Ci sono errori di composizione
				</c:otherwise>
			</c:choose>
			
			<br>&nbsp;
			<br>&nbsp;
		</td>
		</gene:campoScheda>
		
		
		
		
		
		
		<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		<input type="hidden" name="garaLottiConOffertaUnica" id="garaLottiConOffertaUnica" value="${garaLottiConOffertaUnica}" />
		<input type="hidden" name="statoCommissione" id="statoCommissione" value="${statoCommissione}" />
		
		</gene:formScheda>
  	</gene:redefineInsert>
	<gene:redefineInsert name="buttons">
	<c:if test='${(statoCommissione eq "VUOTA" || statoCommissione eq "SOSTITUZIONE" ) && empty requestScope.ERRORI && requestScope.ERRORI ne "SI" && requestScope.commissioneCreata ne "1"}'>
		<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:creaCommissione()">&nbsp;
	</c:if>
		<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi()">&nbsp;
	</gene:redefineInsert>
  	
  	<gene:javaScript>

  	function creaCommissione(){
			document.forms[0].jspPathTo.value="gare/commons/popup-creaCommissioneDaAlbo.jsp";
			schedaConferma();
		}

		function chiudi(){
			window.opener.historyReload();
			window.close();
		}
		
 	</gene:javaScript>
</gene:template>

</div>

