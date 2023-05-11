
<%
	/*
	 * Created on 04-10-2022
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

<jsp:include page="/WEB-INF/pages/commons/defCostantiAppalti.jsp" />

<c:choose>
	<c:when test='${not empty RISULTATO and (RISULTATO eq "OK" || RISULTATO eq "OK-NOP")}' >
<gene:template file="popup-template.jsp" gestisciProtezioni="false" >
	<gene:setString name="titoloMaschera" value="Modifica abilitazione valutazione su M-Eval " />
	<gene:redefineInsert name="corpo">
		<table class="dettaglio-notab">
			<tr>
				<td class="valore-dato" colspan="2">
					<br>
					L'operazione si è conclusa con il seguente esito: <br><br>
					<c:choose>
					<c:when test="${RISULTATO eq 'OK' }">
					<c:if test="${numAbilitatiOk ne 0 }">
					- commissari abilitati: ${numAbilitatiOk }<br>
					</c:if>
					<c:if test="${numAbilitatiNOk ne 0 }">
					- commissari non abilitati in seguito a errori: ${numAbilitatiNOk }<br>
					</c:if>
					<c:if test="${numDisabilitatiOk ne 0 }">
					- commissari disabilitati: ${numDisabilitatiOk }<br>
					</c:if>
					<c:if test="${numDisabilitatiNOk ne 0 }">
					- commissari non disabilitati in seguito a errori: ${numDisabilitatiNOk }<br>
					</c:if>
					<c:if test="${not empty lottiInteressati }">
					&nbsp;&nbsp;Lotti interessati : ${lottiInteressati }<br>
					</c:if>
					</c:when>
					<c:otherwise>
					- nessuna operazione apportata<br>
					</c:otherwise>
					</c:choose>
					<br>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="comandi-dettaglio">
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:ricarica();">&nbsp;&nbsp;
				</td>
			</tr>			
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
		function ricarica(){
			opener.historyReload();
			window.close();
		}
	</gene:javaScript>
</gene:template>		
	</c:when>
	<c:otherwise>
<c:choose>
	<c:when test='${not empty param.codgar}'>
		<c:set var="codgar" value="${param.codgar}" />
	</c:when>
	<c:otherwise>
		<c:set var="codgar" value="${codgar}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.lotto}'>
		<c:set var="lotto" value="${param.lotto}" />
	</c:when>
	<c:otherwise>
		<c:set var="lotto" value="${lotto}" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${not empty param.genere}'>
		<c:set var="genere" value="${param.genere}" />
	</c:when>
	<c:otherwise>
		<c:set var="genere" value="${genere}" />
	</c:otherwise>
</c:choose>

<c:if test='${not empty codgar and gene:matches(codgar, regExpresValidazStringhe, true)}' />
<c:if test='${not empty lotto and gene:matches(lotto, regExpresValidazStringhe, true)}' />

<c:set var="modo" value="MODIFICA" scope="request" />

<c:set var="propertyurlAppaltiMs" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction",  urlAppaltiMs)}' scope="request"/>
<c:if test="${not empty propertyurlAppaltiMs && propertyurlAppaltiMs ne ''}">
	<c:set var="controlli" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.ControlliAbilitazioneValutazioneMEvalFunction", pageContext, codgar)}' />
</c:if>


<gene:template file="popup-template.jsp" gestisciProtezioni="false" >
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>	
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/controlliFormali.js"></script>	
	</gene:redefineInsert>
	
	<gene:setString name="titoloMaschera" value="Modifica abilitazione valutazione su M-Eval " />
	<gene:redefineInsert name="corpo">
		<c:choose>
			<c:when test="${(controlli ne 'OK' and controlli ne 'OK-NOP') or RISULTATO eq 'NOK' }">
				<table class="dettaglio-notab">
					<tr>
						<td class="valore-dato" colspan="2">
							<c:choose>
								<c:when test="${empty propertyurlAppaltiMs || propertyurlAppaltiMs eq ''}">
									<br>
									La configurazione dell'integrazione con M-Eval non &egrave; completa. Valorizzare la property della sezione 'Integrazione appalti-ms'.
									<br><br>
								</c:when>
								<c:when test="${controlli eq 'NOK-1'}">
									<br>
									Non &egrave; possibile procedere alla modifica dell'abilitazione su M-Eval in quanto la valutazione tecnica su M-Eval &egrave; gi&agrave; conclusa.
									<br><br>
								</c:when>
								<c:when test="${controlli eq 'NOK-2'}">
									<br>
									Non &egrave; possibile procedere alla modifica dell'abilitazione su M-Eval in quanto la fase di "Valutazione tecnica" della gara &egrave; gi&agrave; chiusa.
									<br><br>
								</c:when>
								<c:when test="${controlli eq 'NOK-3'}">
									<br>
									Non &egrave; possibile procedere alla modifica dell'abilitazione su M-Eval in quanto non sono definiti criteri di valutazione per la busta tecnica con valutazione manuale da parte della commissione<c:if test="${genere eq '3'}"> in nessun lotto della gara</c:if>.  
									<br><br>
								</c:when>
								<c:when test="${RISULTATO eq 'NOK'}">
									<br>
									Non &egrave; possibile procedere alla modifica dell'abilitazione su M-Eval.
									${msg }
									<br><br>
								</c:when>
							</c:choose>
							
						</td>
					</tr>
					<tr >
					<td colspan="2" class="comandi-dettaglio">
						<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;&nbsp;
					</td>
				</tr>
				</table>
			</c:when>
			<c:otherwise >
			
			<gene:set name="titoloMenu">
				<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
			</gene:set>
			
			<c:choose>
				<c:when test="${BLOCCO eq '1'}">
					<br>
					Non &egrave; possibile procedere alla modifica dell'abilitazione su M-Eval in quanto la valutazione tecnica su M-Eval &egrave; gi&agrave; conclusa.
				</c:when>
				<c:when test="${BLOCCO eq '2'}">
					<br>
					Non &egrave; possibile procedere alla modifica dell'abilitazione su M-Eval in quanto la fase di "Valutazione tecnica" della gara &egrave; gi&agrave; chiusa.
				</c:when>
				<c:otherwise>
				</c:otherwise>
			</c:choose>
			<br/>
			Nella lista sottostante sono riportati i componenti della commissione che esprimono giudizio, ovvero che possono essere abilitati alla valutazione tecnica delle offerte su M-Eval.
			<br>Mediante il check è possibile abilitare o disabilitare la valutazione su M-Eval dei singoli componenti. 
			<br/>
			
			<table class="lista">
				<tr>
					<td><gene:formLista entita="GFOF" tableclass="datilista" gestisciProtezioni="false" sortColumn="8" plugin="it.eldasoft.sil.pg.tags.gestori.plugin.GestoreGFOFLista" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestorePopupAbilitaValutazMEval">
						<gene:campoLista title="Abilitato? <br><center>${titoloMenu}</center>"	width="50">
							<center>
							<c:if test="${currentRow >= 0}">
								<input type="checkbox" name="keys" id="${currentRow + 1 }" value="${datiRiga.GFOF_ID};${datiRiga.GFOF_COMMICG}"  <c:if test="${datiRiga.GFOF_COMMICG eq 1 }" >checked="checked"</c:if> />
							</c:if>
							</center>
						</gene:campoLista>
											
						<gene:campoLista campo="CODFOF" ordinabile="true" title="Cod."/>
						<gene:campoLista campo="NOMFOF" ordinabile="true" title="Nome componente commissione"/>
						<gene:campoLista campo="INCFOF" ordinabile="true"/>
						<gene:campoLista campo="COMMICG" visibile="false" edit="true"/>
						<gene:campoLista campo="NGARA2" visibile="false"/>
						<gene:campoLista campo="NUMCOMM" visibile="false"/>
						<gene:campoLista campo="ID" visibile="false" edit="true"/>
																	
						<input type="hidden" name="lotto" id="lotto" value="${lotto}" />
	                    <input type="hidden" name="codgar" id="codgar" value="${codgar}" />
	                    <input type="hidden" name="genere" id="genere" value="${genere}" />
	                    <input type="hidden" name="idDisabilitati" id="idDisabilitati" value="" />
	                    <input type="hidden" name="commTuttiDis" id="commTuttiDis" value="0" />
	                </gene:formLista></td>
				</tr>
				<tr>
					<td class="comandi-dettaglio"  colSpan="2">
						<c:if test="${datiRiga.rowCount > 0}">
							<INPUT type="button"  id="Aggiungi" class="bottone-azione" value='Conferma modifica abilitazione' title='Conferma modifica abilitazione' onclick="javascript:confermaAbilitati();">&nbsp;&nbsp;&nbsp;
						</c:if>
						<INPUT type="button"  id="Chiudi" class="bottone-azione" value='Chiudi' title='Chiudi' onclick="javascript:chiudi();">&nbsp;
						
					</td>
				</tr>			
			</table>
			</c:otherwise>
		</c:choose>
	</gene:redefineInsert>
	<gene:javaScript>
		
		var numElementiNonSelezionati=0;				
		$("#numComm").val("${ datiRiga.rowCount}");
				
		function chiudi(){
			window.close();
		}
		
			
		function datiCheckNonSelezionati(objArrayCheckBox) {
			var idNonSelezionati = '';
			
			if (objArrayCheckBox) {
			  var arrayLen = "" + objArrayCheckBox.length;
				if (arrayLen != 'undefined') {
				  for (var i = 0; i < objArrayCheckBox.length; i++) {
					if (!objArrayCheckBox[i].checked){
						var indiceRiga= objArrayCheckBox[i].id;
						var commicg=getValue("GFOF_COMMICG_" + indiceRiga);
						if(commicg==1){
							var id=getValue("GFOF_ID_" + indiceRiga);
							idNonSelezionati+=id + ",";
						}
						numElementiNonSelezionati++;
					}
					  
				  }
				  if(idNonSelezionati.length>0){
					  idNonSelezionati = idNonSelezionati.substr(0,idNonSelezionati.length-1);
				  }
				} else {
				  if (!objArrayCheckBox.checked) {
				  	var indiceRiga= objArrayCheckBox.id;
				  	var commicg=getValue("GFOF_COMMICG_" + indiceRiga);
					if(commicg==1){
						var id=getValue("GFOF_ID_" + indiceRiga);
						idNonSelezionati+=id;
					}
					numElementiNonSelezionati++;
				  }
				}	
			}
			return idNonSelezionati;
		  }
		
		function confermaAbilitati(){
			var idNonSelezionati = datiCheckNonSelezionati(document.forms[0].keys);
			$("#idDisabilitati").val(idNonSelezionati);
			var numTotOccorrenze="${ datiRiga.rowCount}";
			if(numTotOccorrenze==numElementiNonSelezionati)
				$("#commTuttiDis").val("1");
			else
				$("#commTuttiDis").val("0");
			listaConferma();
		}
	</gene:javaScript>
</gene:template>
</c:otherwise>
</c:choose>