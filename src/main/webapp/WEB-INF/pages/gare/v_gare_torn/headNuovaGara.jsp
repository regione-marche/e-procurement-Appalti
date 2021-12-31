<%
/*
 * Created on 28-ott-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI SELEZIONE 
 // DEL TIPO DI GARA DA CREARE CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="esisteIntegrazioneLavori" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneLavoriFunction", pageContext)}' />
<c:set var="integrazioneERPvsWSDM" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneERPvsWSDMFunction", pageContext,idconfi)}'/>
<c:set var="integrazioneWSERP" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSERPFunction", pageContext)}' />

<script type="text/javascript">
<!--

	// Azioni invocate dal menu contestuale

	function creaNuovaGara(){
    var tipoGara = null;
    var radioGara = document.formRadioBut.gara;
		if (radioGara != null) {
			for(var i = 0; i < radioGara.length; i++) { // uso radioGara.length per sapere quanti radio button ci sono
				if(radioGara[i].checked) { // scorre tutti i vari radio button
					tipoGara = radioGara[i].value; // valore radio scelto
					break; // esco dal cliclo
				}
			}
		}
		else {	//(SS140909) gestione caso in cui non è prevista da profilo la gestione delle gare divise in lotti
			<c:choose>
			 <c:when test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.GestioneGareLottiOffUnica")}'>
			  tipoGara = '3';
			 </c:when>
			 <c:otherwise>
			  tipoGara = '1';
			 </c:otherwise>
			</c:choose>
		}

    var tipoAppalto = null;
    var radioAppalto = document.formRadioBut.appalto;
		for(var i = 0; i < radioAppalto.length; i++) { // uso radioAppalto.length per sapere quanti radio button ci sono
			if(radioAppalto[i].checked) { // scorre tutti i vari radio button
				tipoAppalto = radioAppalto[i].value; // valore radio scelto
				break; // esco dal cliclo
			}
		}

		
		var proceduraTelematica = "";
		var radioProceduraTelematica = document.formRadioBut.proceduraTelematica;
		for(var i = 0; i < radioProceduraTelematica.length; i++) { 
			if(radioProceduraTelematica[i].checked) { 
				proceduraTelematica = radioProceduraTelematica[i].value; 
				break;
			}
		}
		
		var modalitaPresentazione = "";
		<c:choose>
			<c:when test='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.OFFTEL")}'>
				if(proceduraTelematica==1){
					var radioModalitaPresentazione = document.formRadioBut.modalitaPresentazione;
					for(var i = 0; i < radioModalitaPresentazione.length; i++) { 
						if(radioModalitaPresentazione[i].checked) { 
							modalitaPresentazione = radioModalitaPresentazione[i].value; 
							break;
						}
					}
				}
			</c:when>
			<c:otherwise>
				if(proceduraTelematica==1)
					modalitaPresentazione = 2;
			</c:otherwise>
		</c:choose>
		
		if(tipoGara == '1'){
				<c:choose>
					<c:when test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GARE.AssociaGaraAppalto") && esisteIntegrazioneLavori eq "TRUE"}'>
						bloccaRichiesteServer();
						document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/gare/trovaAppalto/associaAppalto.jsp&tipoGara=garaLottoUnico&modo=NUOVO&tipoAppalto="+tipoAppalto+"&proceduraTelematica="+proceduraTelematica+"&modalitaPresentazione="+modalitaPresentazione;
					</c:when>
					<c:when test='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.PRECED") }'>
						bloccaRichiesteServer();
						document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/gare/trovaGaraRilancio/associaGara.jsp&tipoGara=garaLottoUnico&modo=NUOVO&tipoAppalto="+tipoAppalto+"&proceduraTelematica="+proceduraTelematica+"&modalitaPresentazione="+modalitaPresentazione;
					</c:when>
					<c:otherwise>
						<c:choose>
						<c:when test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.inserimentoRdaSMAT")}'>
							bloccaRichiesteServer();
							document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/garerda/associaRda.jsp&tipoGara=garaLottoUnico&modo=NUOVO&tipoAppalto="+tipoAppalto+"&proceduraTelematica="+proceduraTelematica+"&modalitaPresentazione="+modalitaPresentazione;
						</c:when>
						<c:when test='${integrazioneERPvsWSDM eq "1"}'>
							bloccaRichiesteServer();
							document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/garerda/associa-rda-wsdm.jsp&tipoGara=garaLottoUnico&modo=NUOVO&tipoAppalto="+tipoAppalto+"&proceduraTelematica="+proceduraTelematica+"&modalitaPresentazione="+modalitaPresentazione+"&idconfi=${idconfi}";
						</c:when>
						<c:when test='${integrazioneWSERP eq "1" && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.GestioneUnicaERP")}'>
							<c:set var="scProfilo" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetSceltaContraenteProfiloFunction", pageContext)}' />
							bloccaRichiesteServer();
							document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/commons/lista-rda-scheda.jsp&tipoGara=garaLottoUnico&modo=NUOVO&tipoAppalto="+tipoAppalto+"&proceduraTelematica="+proceduraTelematica+"&modalitaPresentazione="+modalitaPresentazione+"&scProfilo=${scProfilo}";
						</c:when>
						<c:otherwise>
							listaNuovo.jspPathTo.value = "/WEB-INF/gare/gare/gare-scheda.jsp"
							listaNuovo.entita.value = "GARE";
							listaNuovo.action = document.listaNuovo.action + "&tipoGara=garaLottoUnico&tipoAppalto="+tipoAppalto+"&proceduraTelematica="+proceduraTelematica+"&modalitaPresentazione="+modalitaPresentazione;
							bloccaRichiesteServer();
							listaNuovo.submit();
						</c:otherwise>
						</c:choose>
 					</c:otherwise>
				</c:choose>
		}
		if(tipoGara == '2'){
			<c:choose>	
				<c:when test='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.PRECED") }'>
					bloccaRichiesteServer();
					document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/gare/trovaGaraRilancio/associaGara.jsp&tipoGara=garaLottoUnico&modo=NUOVO&tipoAppalto="+tipoAppalto+"&proceduraTelematica="+proceduraTelematica+"&modalitaPresentazione="+modalitaPresentazione;
				</c:when>
				<c:otherwise>	
					listaNuovo.jspPathTo.value = "/WEB-INF/gare/torn/torn-scheda.jsp"
					listaNuovo.entita.value = "TORN";
					listaNuovo.action = document.listaNuovo.action + "&tipoGara=garaDivisaLotti&tipoAppalto="+tipoAppalto+"&proceduraTelematica="+proceduraTelematica+"&modalitaPresentazione="+modalitaPresentazione;
					bloccaRichiesteServer();
					listaNuovo.submit();
				</c:otherwise>
			</c:choose>
		}
		if(tipoGara == '3'){
			<c:choose>
				<c:when test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.inserimentoRdaSMAT")}'>
					bloccaRichiesteServer();
					document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/garerda/associaRda.jsp&tipoGara=garaDivisaLottiOffUnica&modo=NUOVO&tipoAppalto="+tipoAppalto+"&proceduraTelematica="+proceduraTelematica+"&modalitaPresentazione="+modalitaPresentazione;
				</c:when>
				<c:when test='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.PRECED") }'>
					bloccaRichiesteServer();
					document.location.href = "${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gare/gare/trovaGaraRilancio/associaGara.jsp&tipoGara=garaLottoUnico&modo=NUOVO&tipoAppalto="+tipoAppalto+"&proceduraTelematica="+proceduraTelematica+"&modalitaPresentazione="+modalitaPresentazione;
				</c:when>
				<c:otherwise>
					listaNuovo.jspPathTo.value = "/WEB-INF/gare/torn/torn-scheda.jsp"
					listaNuovo.entita.value = "TORN";
					listaNuovo.action = document.listaNuovo.action + "&tipoGara=garaDivisaLottiOffUnica&tipoAppalto="+tipoAppalto+"&proceduraTelematica="+proceduraTelematica+"&modalitaPresentazione="+modalitaPresentazione;
					bloccaRichiesteServer();
					listaNuovo.submit();
				</c:otherwise>
			</c:choose>
			
		}
	}

	function annullaCreazione(){
		bloccaRichiesteServer();
		historyBack();
	}

	//(S.Santi 19.10.2015) Questo js non viene più richiamato
	function aggiornaRadioAppalto(tipoGara){
		var radioAppalto = document.formRadioBut.appalto;
		var radioProceduraTelematica = document.formRadioBut.proceduraTelematica;
		var radioModalitaPresentazione = document.formRadioBut.modalitaPresentazione;
		
		if (tipoGara == 1 || tipoGara == 2){
			//radioAppalto[0].checked = true;
			radioAppalto[0].disabled= false;
			<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.OFFTEL")}'>
			if(tipoGara == 1 && radioProceduraTelematica!=null && radioProceduraTelematica[0].checked == true && radioModalitaPresentazione!=null){
				<c:if test='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.OFFTEL")}'>
				radioModalitaPresentazione[0].disabled = false;
				radioModalitaPresentazione[1].disabled = false;
				</c:if>
			}
			</c:if>
		}else{
			//Tolto limitazione per plico unico - possono essere anche per lavori 
			//radioAppalto[0].disabled= true;
			//if (radioAppalto[0].checked == true ) radioAppalto[1].checked = true;
			<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.OFFTEL")}'>
			if (radioProceduraTelematica!=null && radioProceduraTelematica[0].checked == true && radioModalitaPresentazione!=null){
				radioModalitaPresentazione[0].checked = false;
				radioModalitaPresentazione[0].disabled = true;
				radioModalitaPresentazione[1].checked = true;
				radioModalitaPresentazione[1].disabled = true;
			}
			</c:if>
		}
	}
	
	function aggiornaTelematica(gartel) {
		var radioGara = document.formRadioBut.gara;
		var radioModalitaPresentazione = document.formRadioBut.modalitaPresentazione;
		if (gartel == 1) {
			<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareALotti")}'>
				//if (radioGara!=null && radioGara[2].checked)
				if(document.getElementById("radiogara2")!=null && document.getElementById("radiogara2").checked)
					radioGara[0].checked = true;
				var divLottiDistinti = document.getElementById("divLottiDistinti");
				divLottiDistinti.style.display = "none";
				//radioGara[2].checked = false;
				document.getElementById("radiogara2").checked;
			</c:if>
			//aggiornaRadioAppalto(1);
			<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.OFFTEL")}'>
			if(document.getElementById("presentazione")!=null)
				document.getElementById("presentazione").style.display="block";
			</c:if>
		} else {
			<c:if test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.GARE.GestioneGareALotti")}'>
				//radioGara[2].disabled = false;
				var divLottiDistinti = document.getElementById("divLottiDistinti");
				divLottiDistinti.style.display = "block";
			</c:if>
			<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.OFFTEL")}'>
			if(document.getElementById("presentazione")!=null)
				document.getElementById("presentazione").style.display="none";
			</c:if>
		} 
	}
		
-->
</script>