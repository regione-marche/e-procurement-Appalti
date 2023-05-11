<%
/*
 * Created on: 23/05/2018
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
	
	<c:set var="descc" value="${param.descc}" />
	
	<c:choose>
		<c:when test='${not empty param.idprg}'>
			<c:set var="idprg" value="${param.idprg}" />
		</c:when>
		<c:otherwise>
			<c:set var="idprg" value="${idprg}" />
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test='${not empty param.idcom}'>
			<c:set var="idcom" value="${param.idcom}" />
		</c:when>
		<c:otherwise>
			<c:set var="idcom" value="${idcom}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.entitaRicerca}'>
			<c:set var="entitaRicerca" value="${param.entitaRicerca}" />
		</c:when>
		<c:otherwise>
			<c:set var="entitaRicerca" value="${entitaRicerca}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${not empty param.idconfi}'>
			<c:set var="idconfi" value="${param.idconfi}" />
		</c:when>
		<c:otherwise>
			<c:set var="idconfi" value="${idconfi}" />
		</c:otherwise>
	</c:choose>
	
	<c:set var="comkey1" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetCOMKEYGaraLottoFunction",pageContext,idprg,idcom)}' />
	<c:set var="isInvioFax" value='${gene:callFunction("it.eldasoft.gene.tags.functions.IsInvioFaxFunction",pageContext)}' />
	<c:set var="codgar" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCodgar1Function", pageContext, comkey1)}'/>
	<c:set var="integrazioneWSDM" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSDNFunction", pageContext, codgar, idconfi)}'/>
	
	<c:if test="${integrazioneWSDM eq 1}" >
		<c:set var="abilitatoInvioMailDocumentale" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.AbilitatoInvioMailDocumentaleFunction", pageContext,idconfi)}'/>
	</c:if>

	<gene:setString name="titoloMaschera" value='Selezione destinatari da anagrafica' />
		
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
		
		<br>
		Selezionare dalla lista sottostante i destinatari cui si intende inviare la comunicazione
		<br>
		<br>	
	
		<gene:formLista entita="${entitaRicerca}" pagesize="20" sortColumn="4" tableclass="datilista" gestisciProtezioni="true">
			<c:choose>
				<c:when test="${entitaRicerca eq 'IMPR' }">
					<c:set var="codiceDest" value="${fn:escapeXml(datiRiga.IMPR_CODIMP)}"/>
					<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetMailTipoDittaFunction" parametro="${codiceDest};${idprg};${idcom};IMPR;NO" />
					<c:set var="nomimp" value="${fn:escapeXml(datiRiga.IMPR_NOMIMP)}"/>
				</c:when>
				<c:otherwise>
					<c:set var="codiceDest" value="${fn:escapeXml(datiRiga.TECNI_CODTEC)}"/>
					<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetMailTipoDittaFunction" parametro="${codiceDest};${idprg};${idcom};TECNI;NO" />
					<c:set var="nomimp" value="${fn:escapeXml(datiRiga.TECNI_NOMTEC)}"/>
				</c:otherwise>
			</c:choose>
			
			
			<gene:campoLista title="Utilizza PEC<br><center>${titoloMenuPec}</center>" width="50">
				<c:if test="${currentRow >= 0 && not empty requestScope.emailPec}">
					<c:choose>
						<c:when test="${requestScope.emailPecDisabled eq true}">
						<!--  commento1 -->
							<input type="checkbox" name="keysPec" disabled="disabled" 
							   value="${codiceDest};${nomimp };${requestScope.emailPec}" />
						</c:when>
						<c:otherwise>
						<!--  commento2 -->
							<input type="checkbox" name="keysPec" 
							   value="${codiceDest};${nomimp };${requestScope.emailPec}" />
						</c:otherwise>
					</c:choose>
				</c:if>
			</gene:campoLista>
			<gene:campoLista title="Utilizza e-mail<br><center>${titoloMenuEmail}</center>" width="50" visibile="${abilitatoInvioMailDocumentale ne 'true' }">
				<c:if test="${currentRow >= 0 && not empty requestScope.email && abilitatoInvioMailDocumentale ne 'true'}">
					<c:choose>
						<c:when test="${requestScope.emailDisabled eq true}">
							<input type="checkbox" name="keysEmail" class="email" disabled="disabled" 
							   value="${codiceDest};${nomimp };${requestScope.email}" />
						</c:when>
						<c:otherwise>
							<input type="checkbox" name="keysEmail" class="email"
							   value="${codiceDest};${nomimp };${requestScope.email}" />
						</c:otherwise>
					</c:choose>
				</c:if>
			</gene:campoLista>
			<c:choose>
				<c:when test="${entitaRicerca eq 'IMPR'}">
					<gene:campoLista campo="CODIMP" visibile="false"/>
					<gene:campoLista campo="NOMIMP" />
					<gene:campoLista campo="CFIMP" />
					<gene:campoLista campo="PIVIMP" />
					<gene:campoLista campo="LOCIMP" />
					<gene:campoLista campo="EMAIIP" />
					<gene:campoLista campo="EMAI2IP" />
				</c:when>
				<c:otherwise>
					<gene:campoLista campo="CODTEC" visibile="false"/>
					<gene:campoLista campo="NOMTEC" />
					<gene:campoLista campo="CFTEC" />
					<gene:campoLista campo="PIVATEC" />
					<gene:campoLista campo="EMATEC" />
					<gene:campoLista campo="EMA2TEC" />
				</c:otherwise>
			</c:choose>
			
			
			
			<input type="hidden" name="idprg" value="${idprg}" />
			<input type="hidden" name="idcom" value="${idcom}" />
			<input type="hidden" name="comkey1" value="${comkey1}" />
			<input type="hidden" name="entitaRicerca" value="${entitaRicerca}" />
			<input type="hidden" name="descc" value="${descc}" />
			<c:if test='${integrazioneWSDM =="1"}'>
				<input id="servizio" type="hidden" value="FASCICOLOPROTOCOLLO" />
				<input id="idprg" type="hidden" value="${sessionScope.moduloAttivo}" />
				<input id="idconfi" type="hidden" value="${idconfi}" />
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
			if (numeroOggettiPec == 0 && numeroOggettiEmail == 0) {
	      		alert("Selezionare almeno un destinatario dalla lista");
	      	} else {
	      			      					    
	      		document.forms[0].action="${pageContext.request.contextPath}/pg/AggiungiDestinatariAnagrafica.do?"+csrfToken;
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
