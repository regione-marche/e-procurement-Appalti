
<%
	/*
	 * Created on 01-02-2012
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty RISULTATO and RISULTATO eq "OK"}' >
		<script type="text/javascript">
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
			window.close();
		</script>
	</c:when>
	<c:otherwise>
	

<c:choose>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.garaElenco}'>
		<c:set var="garaElenco" value="${param.garaElenco}"  />
	</c:when>
	<c:otherwise>
		<c:set var="garaElenco" value="${garaElenco}" />
	</c:otherwise>
</c:choose>

<c:choose>
     <c:when test='${not empty param.categoriaPrev}'>
             <c:set var="categoriaPrev" value="${param.categoriaPrev}"  />
     </c:when>
	<c:otherwise>
		<c:set var="categoriaPrev" value="${categoriaPrev}" />
	</c:otherwise>
</c:choose>

<c:choose>
     <c:when test='${not empty param.classifica}'>
             <c:set var="classifica" value="${param.classifica}"  />
     </c:when>
	<c:otherwise>
		<c:set var="classifica" value="${classifica}" />
	</c:otherwise>
</c:choose>

<c:choose>
     <c:when test='${not empty param.tipoGara}'>
             <c:set var="tipoGara" value="${param.tipoGara}"  />
     </c:when>
	<c:otherwise>
		<c:set var="tipoGara" value="${tipoGara}" />
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



<c:set var="modo" value="MODIFICA" scope="request" />

<gene:template file="popup-template.jsp" gestisciProtezioni="false">
	
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>		
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Selezione affidatario uscente da anagrafica" />
	
	<c:set var="where" value="TIPIMP IS NULL OR (TIPIMP IS NOT NULL AND TIPIMP <> 3 AND TIPIMP <> 10)" />
		
	<gene:redefineInsert name="corpo">
		<gene:formTrova entita="IMPR" filtro="${where}" gestisciProtezioni="true"
			lista="gare/gare/lista-affidatari-uscenti-popup.jsp">
			<tr>
				<td colspan="3">
					<br>Specificare l'operatore 'affidatario uscente' che deve essere escluso dalla selezione da elenco.
					<br>Impostare un criterio di filtro sui campi sottostanti per selezionare l'operatore dall'anagrafica.
					<br><br>
				</td>
				<gene:campoTrova campo="CODIMP"/>
				<gene:campoTrova campo="NOMEST"/>
				<gene:campoTrova campo="CFIMP"/>
				<gene:campoTrova campo="PIVIMP"/>
			</tr>
			
			<input type="hidden" name="garaElenco" value="${garaElenco}" />
			<input type="hidden" name="tipoGara" value="${tipoGara}" />
			<input type="hidden" name="where" id="where" value="${where}" />
			<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
            <input type="hidden" name="categoriaPrev" id="categoriaPrev" value="${categoriaPrev}" />
            <input type="hidden" name="classifica" id="classifica" value="${classifica}" />
			<input type="hidden" name="isGaraLottiConOffertaUnica" id="isGaraLottiConOffertaUnica" value="${isGaraLottiConOffertaUnica}" />
			
						
				
		</gene:formTrova>
		
		<gene:javaScript>
			
	function trovaEsegui(){
		// Funzione che esegue la trova sulla lista
		// Eseguo il submit delle form
		clearMsg();
		var continua = true;
		var numeroCampi = eval(getValue("campiCount"));
		var i=0;
		var nCampiValorizzati = 0;
		var nValLengthUp = 0;

		for(i; i < numeroCampi; i++){
		
			var campoVal = getValue("Campo" + i);
			if(campoVal != null && campoVal != ''){
				nCampiValorizzati = nCampiValorizzati +1;
				if(campoVal.length>2){
					nValLengthUp=nValLengthUp+1;
				}
			}
			
			if(getTipoCampo("Campo" + i) == "D"){
				var operatoreConfrontoData = getValue("Campo" + i + "_conf");
				if("<.<" == operatoreConfrontoData || "<=.<=" == operatoreConfrontoData){
					if((getValue("Campo" + i + "Da") == "" && getValue("Campo" + i) != "") || (getValue("Campo" + i + "Da") != "" && getValue("Campo" + i) == "")){
						if(getValue("Campo" + i + "Da") == ""){
							outMsg("<a href=\"javascript:selezionaCampo('Campo"+i+"Da');\"" + " title=\"Seleziona il campo\" style=\"color: #ff0000;\">Valorizzare il limite inferiore del filtro sulla data<"+"/a>","ERR");
							continua = false;
						} else if(getValue("Campo" + i) == ""){
							outMsg("<a href=\"javascript:selezionaCampo('Campo"+i+"');\"" + " title=\"Seleziona il campo\" style=\"color: #ff0000;\">Valorizzare il limite superiore del filtro sulla data<"+"/a>","ERR");
							continua = false;
						}
					} else {
						var dataFrom = toDate(getValue("Campo" + i + "Da"));
						var dataTo = toDate(getValue("Campo" + i));
						if(dataFrom > dataTo){
							outMsg("<a href=\"javascript:selezionaCampo('Campo"+i+"Da');\"" + " title=\"Seleziona il campo\" style=\"color: #ff0000;\">Intervallo temporale non valido<"+"/a>","ERR");
							continua = false;
						}
					}
				}
			}
		}
		
		if(nCampiValorizzati == 0){
			continua= false;
			alert('Valorizzare almeno un criterio di ricerca!');
		}else {
			if(nValLengthUp == 0){
				continua= false;
				alert('Valorizzare un criterio di ricerca con almeno 3 caratteri!');
			}else{
				if(continua){
					document.trova.metodo.value="trova";
					document.trova.submit();
				} else {
					onOffMsgFlag(true);
					alert("Si sono verificati degli errori durante i controlli sui campi");
				}
			
			}
		}
		
	}
			
		</gene:javaScript>
		
		
  	</gene:redefineInsert>

</gene:template>
</c:otherwise>
</c:choose>