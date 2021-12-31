
<%
	/*
	 * Created on 18-june-2015
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
<c:set var="codgar" value="${param.codgar}" />
<c:set var="ngara" value="${param.ngara}" />
<c:set var="where" value="DITG.CODGAR5='${codgar}' AND DITG.NGARA5='${ngara}' AND (INVOFF is NULL or INVOFF='1') AND (FASGAR IS NULL OR FASGAR > 1)" />
<c:set var="whereDitgstati" value="DITG.CODGAR5=DITGSTATI.CODGAR AND DITG.NGARA5=DITGSTATI.NGARA AND DITG.DITTAO=DITGSTATI.DITTAO AND DITGSTATI.FASGAR=8" />


<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneListaCauzioneDitteFunction" parametro="${key}" />

<c:set var="idUtente" value="${sessionScope.profiloUtente.id}" />
						
<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="SVINCOLOCAUZPROVV-lista" >
	<gene:setString name="titoloMaschera" value="Comunicazione svincolo cauzione provvisoria" />
	<gene:setString name="entita" value="DITG" />

	<gene:redefineInsert name="corpo">
					
	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codgar}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>

		<table class="lista">
			<tr>
				<td><gene:formLista entita="DITG" pagesize="20" tableclass="datilista" gestisciProtezioni="false" sortColumn="7;8" where ="${where}" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreDITGSTATI">
					<gene:redefineInsert name="listaNuovo" />
					<gene:redefineInsert name="listaEliminaSelezione" />
					<gene:redefineInsert name="documentiAzioni" />					
					
										
					<gene:redefineInsert name="addToAzioni">
					<c:if test='${autorizzatoModifiche ne "2"}'>
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
							<c:if test="${datiRiga.rowCount > 0 and gene:checkProtFunz(pageContext,'MOD','LISTAMOD')}">
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
					</c:if>
					</gene:redefineInsert>
					<gene:campoLista campo="CODGAR5"  visibile="false"/>
					<gene:campoLista campo="NGARA5"  visibile="false"/>
					<gene:campoLista campo="DITTAO"  visibile="false"/>
					<gene:campoLista campo="CODGAR5"  edit="${(updateLista eq 1)}" visibile="false"/>
					<gene:campoLista campo="NGARA5"  edit="${(updateLista eq 1)}" visibile="false"/>
					<gene:campoLista campo="DITTAO"  edit="${(updateLista eq 1)}" visibile="false"/>
					<gene:campoLista campo="NUMORDPL" title="N.pl" />
					<gene:campoLista campo="NOMIMO" />
					<gene:campoLista entita="DITGSTATI" campo="FASGAR" where="${whereDitgstati}" visibile="false" />
					<c:choose>
						<c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.AutovieArchiflow")}'>
							<gene:campoLista entita="DITGSTATI" campo="NCOMSVIP" title="Numero protocollo" where="${whereDitgstati}" edit="${(updateLista eq 1)}" href="javascript:consultaDocumentiArchiflow('DITGSTATI_NCOMSVIP', '${datiRiga.DITGSTATI_NCOMSVIP}');"/>
						</c:when>
						<c:otherwise>
							<gene:campoLista entita="DITGSTATI" campo="NCOMSVIP" title="Numero protocollo" where="${whereDitgstati}" edit="${(updateLista eq 1)}"/>
						</c:otherwise>
					</c:choose>
					<gene:campoLista entita="DITGSTATI" campo="DCOMSVIP" title="Data" where="${whereDitgstati}" edit="${(updateLista eq 1)}"/>
					<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
					<input type="hidden" name="ngara"  id="ngara" value="${ngara}" />
					<input type="hidden" name="numeroDitte" id="numeroDitte" value="" />
				</gene:formLista></td>
			</tr>
			
			
		 <c:if test='${gene:checkProtFunz(pageContext,"MOD","LISTAMOD")}'>
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
						<c:choose>
						 <c:when test='${updateLista eq 1 }'>
							<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:listaConferma();">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla();">
						 </c:when>
						 <c:otherwise>
							<c:if test='${(autorizzatoModifiche ne "2") and datiRiga.rowCount > 0}'>
 								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:listaApriInModifica();">&nbsp;&nbsp;&nbsp;
							</c:if>
						 </c:otherwise>
						</c:choose>
				</td>
			</tr>
		</c:if>
			
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
	
	document.getElementById("numeroDitte").value = ${currentRow}+1;
	
	
	
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