<%
/*
 * Created on: 30/07/2012
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
		Finestra pop-up per l'import dei prodotti da OLIAMM
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<c:choose>
	<c:when test='${not empty requestScope.importEseguito and requestScope.importEseguito eq "OK"}' >
<script type="text/javascript">
		opener.historyReload();
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
		<c:when test='${not empty param.garaLottiConOffertaUnica}'>
			<c:set var="garaLottiConOffertaUnica" value="${param.garaLottiConOffertaUnica}" />
		</c:when>
		<c:otherwise>
			<c:set var="garaLottiConOffertaUnica" value="${garaLottiConOffertaUnica}" />
		</c:otherwise>
	</c:choose>
	
	
	<c:choose>
		<c:when test='${not empty param.isCodificaAutomatica}'>
			<c:set var="isCodificaAutomatica" value="${param.isCodificaAutomatica}" />
		</c:when>
		<c:otherwise>
			<c:set var="isCodificaAutomatica" value="${isCodificaAutomatica}" />
		</c:otherwise>
	</c:choose>	
	
	<c:set var="riferimentoOliamm" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetRiferimentoOliammFunction",  pageContext,ngara)}' />
	
	<gene:setString name="titoloMaschera" value="Importazione da OLIAMM della lista prodotti della gara ${ngara}" />
	
	<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="V_GARE_OUT" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupImportOliamm">
		<br>Mediante questa funzione è possibile importare la lista dei prodotti della gara corrente dalla gara OLIAMM associata '${riferimentoOliamm }'
		<br>&nbsp;<br>&nbsp;
		<br><b>ATTENZIONE: durante l'operazione di import verranno cancellati i prodotti esistenti <c:if test='${garaLottiConOffertaUnica eq 1 }'> e tutti i lotti della gara</c:if>.</b>
		<br>&nbsp;
		
		<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
		<input type="hidden" name="garaLottiConOffertaUnica" id="garaLottiConOffertaUnica" value="${garaLottiConOffertaUnica}" />
		<input type="hidden" name="isCodificaAutomatica" id="isCodificaAutomatica" value="${isCodificaAutomatica}" />
		
		</gene:formScheda>
  	</gene:redefineInsert>
	<gene:redefineInsert name="buttons">
		<c:if test='${isCodificaAutomatica ne "2" }'>
			<INPUT type="button" class="bottone-azione" value="Importa" title="Importa" onclick="javascript:importa()">&nbsp;
		</c:if>
		<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi()">&nbsp;
	</gene:redefineInsert>
  	
  	<gene:javaScript>

  	function importa(){
			document.forms[0].jspPathTo.value="gare/commons/popup-importOliamm.jsp";
			var garaLottiConOffertaUnica ="${ garaLottiConOffertaUnica}";
			var isCodificaAutomatica ="${isCodificaAutomatica }";
			
			//Per le gare ad offerta unica deve essere attiva la codifica automatica
			if(garaLottiConOffertaUnica == 1 && isCodificaAutomatica == 2){
				outMsg("Deve essere attiva la codifica automatica", "ERR");
				onOffMsg();
			}else{
				schedaConferma();
			}
		}

		function chiudi(){
			window.close();
		}
		
		<c:if test='${isCodificaAutomatica eq "2" }'>
			outMsg("Non è possibile procedere con l'importazione perchè non risulta configurata la codifica automatica per le gare", "ERR");
			onOffMsg();
		</c:if>
 	</gene:javaScript>
</gene:template>

</div>

</c:otherwise>
</c:choose>