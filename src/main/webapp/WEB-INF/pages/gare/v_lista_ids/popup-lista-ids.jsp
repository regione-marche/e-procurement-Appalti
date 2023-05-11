<%
/*
 * Created on: 10-feb-2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 /* Lista popup di selezione degli ids collegati/scollegati alla gara */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="idUtente" value="${sessionScope.profiloUtente.id}" />
<c:set var="codiceGara" value="${param.codiceGara}" />
<c:set var="genereGara" value="${param.genereGara}" />
<c:set var="where" value="V_LISTA_IDS.ID_UTENTE = ${idUtente} AND V_LISTA_IDS.FLAG_RESPINGI = 2 AND V_LISTA_IDS.FLAG_EVADI = 2 AND V_LISTA_IDS.FLAG_ANNULLA = 2" />
<gene:template file="popup-template.jsp">
	<gene:setString name="titoloMaschera" value="Selezione ids"/>
	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenuCollega">
			<a href='javascript:selezionaTutti(document.forms[0].keysCollega);' Title='Seleziona tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>
			&nbsp;
			<a href='javascript:deselezionaTutti(document.forms[0].keysCollega);' Title='Deseleziona tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>
		</gene:set>
		<gene:set name="titoloMenuScollega">
			<a href='javascript:selezionaTutti(document.forms[0].keysScollega);' Title='Seleziona tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona tutti'></a>
			&nbsp;
			<a href='javascript:deselezionaTutti(document.forms[0].keysScollega);' Title='Deseleziona tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona tutti'></a>
		</gene:set>
		
		<table class="dettaglio-noBorderBottom">
			<tr><td colspan="2"><b>
				Nella lista sottostante sono elencati gli impegni di spesa collegati alla gara e quelli inevasi<br> 
				ordinati in base al numero protocollo
			</b></td></tr>
		</table>		
		<table class="lista">
		<tr>
		<td>
		<gene:formLista pagesize="25" sortColumn="6" tableclass="datilista" entita="V_LISTA_IDS" where ="${where}" gestisciProtezioni="true">
			<gene:campoLista campo="IDS_PROG" headerClass="sortable" visibile="false"/>
			<gene:campoLista campo="ID_UTENTE" visibile="false"/>
			<c:set var="livelloGareAssociate" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetLivelloGareAssociateIdsFunction", pageContext, datiRiga.V_LISTA_IDS_IDS_PROG,codiceGara)}' />
			<gene:campoLista  title="Collega Ids <br><center>${titoloMenuCollega}</center>" width="50">
				<c:if test="${currentRow >= 0 && not empty datiRiga.V_LISTA_IDS_IDS_PROG}">
					<c:choose>
						<c:when test="${livelloGareAssociate eq '0' || livelloGareAssociate eq '2'}">
						<!--  commento1 -->
							<input type="checkbox" name="keysCollega" 
							   value="${datiRiga.V_LISTA_IDS_IDS_PROG}" />
						</c:when>
						<c:otherwise>
						</c:otherwise>
					</c:choose>
				</c:if>
			</gene:campoLista>
			<gene:campoLista  title="Scollega Ids<br><center>${titoloMenuScollega}</center>" width="50">
				<c:if test="${currentRow >= 0 && not empty datiRiga.V_LISTA_IDS_IDS_PROG}">
					<c:choose>
						<c:when test="${livelloGareAssociate eq '1'}">
						<!--  commento1 -->
							<input type="checkbox" name="keysScollega" 
							   value="${datiRiga.V_LISTA_IDS_IDS_PROG}" />
						</c:when>
						<c:otherwise>
						</c:otherwise>
					</c:choose>
				</c:if>
			</gene:campoLista>

			<gene:campoLista campo="SIGLA_ENTITA_RICHIEDENTE"  headerClass="sortable"  />
			<gene:campoLista campo="NUMERO_PROTOCOLLO" href="javascript:consultaDocumentiArchiflow('V_LISTA_IDS_NUMERO_PROTOCOLLO', '${datiRiga.V_LISTA_IDS_NUMERO_PROTOCOLLO}');" />			<gene:campoLista campo="DATA_PROTOCOLLO" />
			<gene:campoLista campo="OGGETTO" />
			<gene:campoLista campo="STATO" title="Stato" />
<%/*			
			<gene:campoLista title="livello gare associate" campo="LIV_GARE_ASS" entita="V_LISTA_IDS" visibile="false" campoFittizio="true" definizione="N7" value="${livelloGareAssociate}" />
			<gene:campoLista title="" width="20">
				<c:choose>
					<c:when test='${livelloGareAssociate eq "0"}'>
						<span id="INFO_TOOLTIP${currentRow }" title="Ids privo di collegamenti a gare">
							<IMG SRC="${contextPath}/img/documentazione_elenco.png" >
						</span>
					</c:when>
					<c:otherwise>
						<span id="INFO_TOOLTIP${currentRow }" title="Ids con collegamenti a gare">
							<IMG SRC="${contextPath}/img/documentazione.png"  >
						</span>
					</c:otherwise>
				</c:choose>
			</gene:campoLista>
*/%>			
			<input type="hidden" name="codiceGara" id="codiceGara" value="${codiceGara}" />
			<input type="hidden" name="genereGara" id="genereGara" value="${genereGara}" />
			<input type="hidden" name="numeroIds" id="numeroIds" value="" />
		
		</gene:formLista>
		</td>
		</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<INPUT type="button"  class="bottone-azione" value='Conferma' title='Conferma' onclick="javascript:conferma();">&nbsp;
					<INPUT type="button"  class="bottone-azione" value='Annulla' title='Annulla' onclick="javascript:annulla();">&nbsp;
				</td>
			</tr>			
		</table>
  </gene:redefineInsert>

	<gene:javaScript>
		document.getElementById("numeroIds").value = ${currentRow}+1;
		
		function conferma() {
	      		document.forms[0].action="${pageContext.request.contextPath}/pg/AggiornaGaraIds.do?"+csrfToken;
 				bloccaRichiesteServer();
				document.forms[0].submit();
		}
		
		function annulla(){
			window.close();
		}

		function consultaDocumentiArchiflow(campoProtocollo,valoreProtocollo) {
			if(valoreProtocollo!=null){
				var par = "campoProtocollo=" + campoProtocollo;
				valoreProtocollo=$.trim(valoreProtocollo);
				par += "&valoreProtocollo=" + valoreProtocollo;
				openPopUpActionCustom(contextPath + "/pg/ConsultaDocumentiArchiflow.do", par, "ConsultaDocumentiArchiflow",700,700,"yes","yes");
			}
	}
		
		
		
	</gene:javaScript>

</gene:template>
