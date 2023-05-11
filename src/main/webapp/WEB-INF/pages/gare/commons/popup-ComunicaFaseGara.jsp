<%
/*
 * Created on: 11-10-2012
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
		Finestra per la valorizzazione del campo ESINEG 
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty RISULTATO and (RISULTATO eq "CALCOLOESEGUITO")}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">

<c:choose>
	<c:when test="${param.isOffertaUnica eq 'Si' }">
		<c:set var="tipo" value="3"/>
	</c:when>
	<c:when test="${param.isOfferteDistinte eq 'Si' }">
		<c:set var="tipo" value="1"/>
	</c:when>
	<c:otherwise>
		<c:set var="tipo" value="2"/>
	</c:otherwise>
</c:choose>

<c:set var="msgConferma" value="Tale funzione permette di aggiornare fase della gara a partire dalla pubblicazione ."/>
<c:set var="msgAvviso" value="la gara risulta aggiudicata."/>
<c:set var="str_calcoloinfo" value="Aggiornamento della procedura ad ERP effettuato." />
<gene:setString name="titoloMaschera" value='Aggiorna procedura su ERP' />

<c:set var="msgBlocco" value="Non é possibile procedere perchè "/>

<c:if test='${RISULTATO eq "CALCOLOINFO"}'>
	<gene:redefineInsert name="buttons">
		<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
	</gene:redefineInsert>
</c:if>			


<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupComunicaFaseGara">
	
		<gene:campoScheda>
			<td colSpan="2">
				<c:choose>
					<c:when test="${1 eq 0}">
						<br>
						${msgBlocco} la gara risulta sospesa.<br>
						<br>
					</c:when>
					<c:when test='${RISULTATO eq "CALCOLOINFO"}'>
						<br>
						${str_calcoloinfo}<br>
						
						<br>
					</c:when>
					<c:otherwise>
							<br>
							${msgConferma}<br>
							<br>
					</c:otherwise>
				</c:choose>
			</td>
		</gene:campoScheda>

			<gene:campoScheda campo="NGARA" campoFittizio="true" defaultValue="${param.ngara}"  visibile="false" definizione="T20;0"/>
			<gene:campoScheda campo="CODGARA" campoFittizio="true" defaultValue="${param.codgar1}"  visibile="false" definizione="T21;0"/>
			<gene:campoScheda campo="ISLOTTOOFFDISTINTE" campoFittizio="true" defaultValue="${param.isLottoOffDistinte}"  visibile="false" definizione="T2;0"/>
			<gene:campoScheda campo="ISOFFERTAUNICA" campoFittizio="true" defaultValue="${param.isOffertaUnica}"  visibile="false" definizione="T2;0"/>
			<gene:campoScheda campo="ISOFFERTEDISTINTE" campoFittizio="true" defaultValue="${param.isOfferteDistinte}"  visibile="false" definizione="T2;0"/>
			<gene:campoScheda campo="ISLOTTOOFFUNICA" campoFittizio="true" defaultValue="${param.isLottoOffUnica}"  visibile="false" definizione="T2;0"/>
	</gene:formScheda>
  </gene:redefineInsert>

	<gene:javaScript>
		function conferma() {
			document.forms[0].jspPathTo.value="gare/commons/popup-ComunicaFaseGara.jsp";
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
