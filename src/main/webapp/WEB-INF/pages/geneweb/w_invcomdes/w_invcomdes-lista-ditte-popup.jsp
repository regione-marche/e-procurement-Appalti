<%
/*
 * Created on: 04/06/2010
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
		Finestra che visualizza la conferma per l'inserimento delle pubblicazioni bando/esito predefinite
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>



<div style="width:97%;">

<gene:template file="popup-message-template.jsp">
	
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
	</gene:redefineInsert>
	
	
	<c:set var="idprg" value="${param.idprg}" />
	<c:set var="idcom" value="${param.idcom}" />
	<c:set var="comkey1" value="${param.comkey1}" />
	<c:set var="genereGara" value="${param.genereGara}" />
	<c:set var="idconfi" value="${param.idconfi}" />
	<c:set var="isInvioFax" value='${gene:callFunction("it.eldasoft.gene.tags.functions.IsInvioFaxFunction",pageContext)}' />
	<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, comkey1)}'/>
	<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codgar,idconfi)}'/>
	
	<c:if test="${integrazioneWSDM eq 1}" >
		<c:set var="abilitatoInvioMailDocumentale" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.AbilitatoInvioMailDocumentaleFunction", pageContext, idconfi)}'/>
	</c:if>

	<c:choose>
		<c:when test="${genereGara eq '10'}">
		<c:set var="testoTipoGara" value=" in elenco" />
		</c:when>
		<c:when test="${genereGara eq '20'}">
			<c:set var="testoTipoGara" value="in catalogo" />
		</c:when>
		<c:otherwise>
			<c:set var="testoTipoGara" value="in gara" />
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value='Selezione destinatari da ditte ${testoTipoGara}' />
		
	<gene:redefineInsert name="corpo">
	
		<gene:set name="titoloMenuPec">
			<a href='javascript:selezionaTutti(document.forms[0].keysPec);' Title='Seleziona tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>
			&nbsp;
			<a href='javascript:deselezionaTutti(document.forms[0].keysPec);' Title='Deseleziona tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>
		</gene:set>
		<gene:set name="titoloMenuEmail">
			<a href='javascript:selezionaTutti(document.forms[0].keysEmail);' Title='Seleziona tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>
			&nbsp;
			<a href='javascript:deselezionaTutti(document.forms[0].keysEmail);' Title='Deseleziona tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>
		</gene:set>
		<gene:set name="titoloMenuFax">
			<a href='javascript:selezionaTutti(document.forms[0].keysFax);' Title='Seleziona tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>
			&nbsp;
			<a href='javascript:deselezionaTutti(document.forms[0].keysFax);' Title='Deseleziona tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>
		</gene:set>

		<br>
		Selezionare dalla lista sottostante le ditte cui si intende inviare la comunicazione. Si sottolinea che nel caso di raggruppamento temporaneo viene considerata la ditta mandataria.
		<br>
		<br>	
	
		<gene:formLista entita="DITG" pagesize="20" sortColumn="6" tableclass="datilista" gestisciProtezioni="true">
			<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetMailTipoDittaFunction" parametro="${datiRiga.DITG_DITTAO};${idprg};${idcom};IMPR;SI" />
			<c:set var="nomimp" value="${fn:escapeXml(datiRiga.IMPR_NOMIMP)}"/>
			<gene:campoLista title="Utilizza PEC<br><center>${titoloMenuPec}</center>" width="50">
				<c:if test="${currentRow >= 0 && not empty requestScope.emailPec}">
					<c:choose>
						<c:when test="${requestScope.emailPecDisabled eq true or not requestScope.mandatariaPresente}">
						<!--  commento1 -->
							<input type="checkbox" name="keysPec" disabled="disabled" 
							   value="${datiRiga.DITG_DITTAO};${nomimp };${requestScope.emailPec}" />
						</c:when>
						<c:otherwise>
						<!--  commento2 -->
							<input type="checkbox" name="keysPec" 
							   value="${datiRiga.DITG_DITTAO};${nomimp };${requestScope.emailPec}" />
						</c:otherwise>
					</c:choose>
				</c:if>
			</gene:campoLista>
			<gene:campoLista title="Utilizza e-mail<br><center>${titoloMenuEmail}</center>" width="50" visibile="${abilitatoInvioMailDocumentale ne 'true' }">
				<c:if test="${currentRow >= 0 && not empty requestScope.email && abilitatoInvioMailDocumentale ne 'true'}">
					<c:choose>
						<c:when test="${requestScope.emailDisabled eq true or not requestScope.mandatariaPresente}">
							<input type="checkbox" name="keysEmail" class="email" disabled="disabled" 
							   value="${datiRiga.DITG_DITTAO};${nomimp };${requestScope.email}" />
						</c:when>
						<c:otherwise>
							<input type="checkbox" name="keysEmail" class="email"
							   value="${datiRiga.DITG_DITTAO};${nomimp };${requestScope.email}" />
						</c:otherwise>
					</c:choose>
				</c:if>
			</gene:campoLista>
			<c:if test="${isInvioFax eq 'true'}">
				<gene:campoLista title="Utilizza fax<br><center>${titoloMenuFax}</center>" width="50" visibile="${abilitatoInvioMailDocumentale ne 'true' }">
					<c:if test="${currentRow >= 0 && not empty requestScope.fax && abilitatoInvioMailDocumentale ne 'true'}">
						<c:choose>
							<c:when test="${requestScope.faxDisabled eq true or not requestScope.mandatariaPresente}">
								<input type="checkbox" name="keysFax" class="fax" disabled="disabled" 
								   value="${datiRiga.DITG_DITTAO};${nomimp };${requestScope.fax}" />
							</c:when>
							<c:otherwise>
								<input type="checkbox" name="keysFax" class="fax"
								   value="${datiRiga.DITG_DITTAO};${nomimp };${requestScope.fax}" />
							</c:otherwise>
						</c:choose>
					</c:if>
				</gene:campoLista>
			</c:if>
			<gene:campoLista campo="NGARA5" visibile="false"/>
			<gene:campoLista campo="DITTAO" visibile="false"/>
			<gene:campoLista campo="NOMIMP" entita="IMPR" where="DITG.DITTAO=IMPR.CODIMP"/>
			<c:choose>
				<c:when test="${genereGara ne '10' and genereGara ne '20'}">
					<gene:campoLista campo="INVGAR" width="70" />
					<gene:campoLista campo="INVOFF" width="70" />
					<gene:campoLista campo="AMMGAR" width="70" title="Ditta esclusa ?" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoSiNoInvertito"/>
					<gene:campoLista campo="FASGAR" />
				</c:when>
				<c:otherwise>
					<gene:campoLista campo="ABILITAZ" />
				</c:otherwise>
			</c:choose>
			<input type="hidden" name="idprg" value="${idprg}" />
			<input type="hidden" name="idcom" value="${idcom}" />
			<input type="hidden" name="comkey1" value="${comkey1}" />
			<input type="hidden" name="genereGara" value="${genereGara}" />
			<c:if test='${integrazioneWSDM =="1"}'>
				<input id="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
				<input id="idprg" type="hidden" value="${sessionScope.moduloAttivo}" />
			</c:if>
			
			<gene:redefineInsert name="buttons">
				<INPUT type="button" class="bottone-azione" value="Aggiungi destinatari selezionati" title="Aggiungi destinatari selezionati" onclick="javascript:aggiungi()">&nbsp;
				<INPUT type="button" class="bottone-azione" value="Torna alla ricerca" title="Torna alla ricerca" onclick="javascript:historyVaiA(0);">&nbsp;
				<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi()">&nbsp;
			</gene:redefineInsert>
			
		</gene:formLista>

  	</gene:redefineInsert>

	<gene:javaScript>

		function aggiungi(){
			var numeroOggettiPec = contaCheckSelezionati(document.forms[0].keysPec);
			var numeroOggettiEmail = contaCheckSelezionati(document.forms[0].keysEmail);
			var numeroOggettiFax = contaCheckSelezionati(document.forms[0].keysFax);
	  		if (numeroOggettiPec == 0 && numeroOggettiEmail == 0 && numeroOggettiFax == 0) {
	      		alert("Selezionare almeno un destinatario dalla lista");
	      	} else {
	      			      					    
	      		document.forms[0].action="${pageContext.request.contextPath}/pg/AggiungiDitteConcorrenti.do?"+csrfToken;
 				bloccaRichiesteServer();
				document.forms[0].submit();
 			}
		}

		
		function chiudi(){
			window.close();
		}
	</gene:javaScript>
	
	</gene:template>
</div>
