<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty RISULTATO and RISULTATO eq "OK"}' >
		<script type="text/javascript">
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			var jspPath = window.opener.document.forms[0].jspPath.value;
			jspPath+="?AGGIORNAMENTO=OK"; //Serve ad indicare che quando si è giunti a tale popup l'eventuale calcolo del numero ordine degli operatori è stato gia' eseguito
			window.opener.document.forms[0].jspPath.value=jspPath;
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
			window.close();
		</script>
	</c:when>
	<c:otherwise>

<c:set var="modo" value="MODIFICA" scope="request" />

<gene:template file="popup-template.jsp" gestisciProtezioni="false" schema="GARE">
	<gene:setString name="titoloMaschera" value="Imposta filtro su fascia di punteggio" />
	<gene:setString name="entita" value="${entita}" />
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		
		<br>

		<table class="dettaglio-notab">
			<tr>
				<td id="td_form"><gene:formScheda entita="GARE" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupFiltroElPremFasce" >

				<gene:campoScheda campo="NGARA"  visibile="false"/>

				<gene:campoScheda >
					&nbsp;Selezionare la fascia di punteggio su cui si vogliono filtrare gli operatori dell'elenco.
				</gene:campoScheda>

				<gene:campoScheda >
					<br><br>&nbsp;&nbsp;&nbsp;
					<input type="radio" name="filtroElPremFasce" value="" <c:if test='${empty sessionScope.filtroElPremFasce or sessionScope.filtroElPremFasce eq ""}'>checked="checked"</c:if>> Tutte le fasce&nbsp;&nbsp;&nbsp;
					<input type="radio" name="filtroElPremFasce" value="3" <c:if test='${sessionScope.filtroElPremFasce eq 3}'>checked="checked"</c:if>> Fascia 3&nbsp;&nbsp;&nbsp;
					<input type="radio" name="filtroElPremFasce" value="2" <c:if test='${sessionScope.filtroElPremFasce eq 2}'>checked="checked"</c:if>> Fascia 2&nbsp;&nbsp;&nbsp;
					<input type="radio" name="filtroElPremFasce" value="1" <c:if test='${sessionScope.filtroElPremFasce eq 1}'>checked="checked"</c:if>> Fascia 1
					<br><br>
				</gene:campoScheda>

				</gene:formScheda></td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<INPUT type="button"  class="bottone-azione" value='Imposta filtro' title='Imposta filtro' onclick="javascript:aggiungiFiltro();">&nbsp;&nbsp;&nbsp;
					<INPUT type="button"  class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:chiudi();">

				</td>
			</tr>
		</table>
	</gene:redefineInsert>
	<gene:javaScript>

		function aggiungiFiltro(){
			document.forms[0].jspPathTo.value="gare/gare/gare-popup-filtroElPremFasce.jsp";
			schedaConferma();
		}


		function chiudi(){
			window.close();
		}

	</gene:javaScript>
</gene:template>
	</c:otherwise>
</c:choose>
