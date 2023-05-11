
<%
	/*
	 * Created on 10-feb-2015
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GetUtentiUfficioAppartenenzaFunction" parametro="${key}" />

<c:set var="idUtente" value="${sessionScope.profiloUtente.id}" />

<c:choose>
	<c:when test="${'A' eq abilitazioneGare}">
		<c:set var="where" value="V_LISTA_IDS.FLAG_RESPINGI = 2 AND V_LISTA_IDS.FLAG_EVADI = 2 AND V_LISTA_IDS.FLAG_ANNULLA = 2" />	
	</c:when>
	<c:otherwise>
		<c:set var="where" value="V_LISTA_IDS.FLAG_RESPINGI = 2 AND V_LISTA_IDS.FLAG_EVADI = 2 AND V_LISTA_IDS.FLAG_ANNULLA = 2 AND V_LISTA_IDS.ID_UTENTE = ${idUtente}" />
	</c:otherwise>
</c:choose>
						
<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<gene:template file="lista-template.jsp" gestisciProtezioni="false" >
	<gene:setString name="titoloMaschera" value="Consultazione ids" />
	<gene:setString name="entita" value="V_LISTA_IDS" />
	
		
	<gene:redefineInsert name="corpo">
					
		<table class="lista">
			<tr>
				<td><gene:formLista entita="V_LISTA_IDS" pagesize="20" tableclass="datilista" gestisciProtezioni="false" sortColumn="-6;-7" where ="${where}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreIds">
					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
					<gene:redefineInsert name="documentiAzioni" />					
					
										
					<gene:redefineInsert name="addToAzioni">
						<c:choose>
						<c:when test='${updateLista eq 1}'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:listaConferma();" title="Salva modifiche" tabindex="1500">
										${gene:resource("label.tags.template.dettaglio.schedaConferma")}
									</a>
								</td>
							</tr>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:annulla();" title="Annulla modifiche" tabindex="1501">
										${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}
									</a>
								</td>
							</tr>
						</c:when>
						<c:otherwise>
							<c:if test="${datiRiga.rowCount > 0}">
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:listaApriInModifica();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1505">
										${gene:resource("label.tags.template.dettaglio.schedaModifica")}
									</a>
								</td>
							</tr>
							</c:if>
						</c:otherwise>
						</c:choose>
					</gene:redefineInsert>
					
					<gene:campoLista campo="IDS_PROG" headerClass="sortable"  visibile="false"/>
					<gene:campoLista campo="IDS_PROG" edit="${updateLista eq 1}" visibile="false" />
					<gene:campoLista campo="SIGLA_ENTITA_RICHIEDENTE"  headerClass="sortable"  />
					<gene:campoLista campo="NUMERO_PROTOCOLLO" href="javascript:consultaDocumentiArchiflow('V_LISTA_IDS_NUMERO_PROTOCOLLO', '${datiRiga.V_LISTA_IDS_NUMERO_PROTOCOLLO}');" />
					<gene:campoLista campo="DATA_PROTOCOLLO" />
					<gene:campoLista campo="DATA_RICEZIONE" />
					<gene:campoLista campo="ORA_RICEZIONE" />
					<gene:campoLista campo="OGGETTO" />
					<c:choose>
					<c:when test="${'A' eq abilitazioneGare}">
						<gene:campoLista campo="ID_UTENTE" title="Affida a" edit="${(updateLista eq 1)}" >
							<gene:addValue value="" descr="" />
							<c:if test='${!empty listaUtentiUfficioAppartenenza}'>
								<c:forEach items="${listaUtentiUfficioAppartenenza}"
									var="valoriUtentiUfficioAppartenenza">
									<gene:addValue value="${valoriUtentiUfficioAppartenenza[0]}"
										descr="${valoriUtentiUfficioAppartenenza[2]}" />
								</c:forEach>
							</c:if>
						</gene:campoLista>
						<gene:campoLista campo="DATA_SCADENZA_EMISSIONE_RDO" title="Data scadenza emissione RDO" edit="${(updateLista eq 1)}" />
					</c:when>
					<c:otherwise>
						<gene:campoLista campo="ID_UTENTE" edit="${(updateLista eq 1)}" visibile="false"/>
						<gene:campoLista campo="DATA_SCADENZA_EMISSIONE_RDO" title="Data scadenza emissione RDO" edit="${(updateLista eq 1)}" />
					</c:otherwise>
					</c:choose>											

					<gene:campoLista campo="COLLEGAMENTI" visibile="false"/>
					<gene:campoLista campo="FLAG_RESPINGI" title = "Respingi?" edit="${(updateLista eq 1)  and ('A' eq abilitazioneGare) and (datiRiga.V_LISTA_IDS_COLLEGAMENTI eq '0')}" visibile="${'A' eq abilitazioneGare}" />
					<gene:campoLista campo="FLAG_RESPINGI" title = "Respingi?" edit="${updateLista eq 1}" visibile="false" />
					<gene:campoLista campo="STATO" title="Stato" edit="false" />
					<gene:campoLista campo="FLAG_EVADI" title = "Evadi?" edit="${(updateLista eq 1)}" />
					<gene:campoLista campo="FLAG_EVADI" title = "Evadi?" edit="${updateLista eq 1}" visibile="false" />
					<gene:campoLista campo="FLAG_ANNULLA" title = "Annulla?" edit="${(updateLista eq 1)}" />
					<gene:campoLista campo="FLAG_ANNULLA" title = "Annulla?" edit="${updateLista eq 1}" visibile="false" />
					<input type="hidden" name="numeroIds" id="numeroIds" value="" />
				</gene:formLista></td>
			</tr>
			
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					
						<c:choose>
						 <c:when test='${updateLista eq 1 }'>
							<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:listaConferma();">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla();">
						 </c:when>
						 <c:otherwise>
							<c:if test="${datiRiga.rowCount > 0}">
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:listaApriInModifica();">&nbsp;&nbsp;&nbsp;
							</c:if>
						 </c:otherwise>
						</c:choose>
				</td>
			</tr>
					
					
			
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
	
	document.getElementById("numeroIds").value = ${currentRow}+1;
	
	
	
	function annulla(){
			document.forms[0].updateLista.value = "0";
			listaAnnullaModifica();
	}

	function chiudi(){
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