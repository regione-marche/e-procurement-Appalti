
<%
	/*
	 * Created on 20-Ott-2008
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

<c:set var="archiviFiltrati" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata.archiviFiltrati")}'/>
<c:set var="filtroUffint" value="AND W9DELEGHE.CODEIN IS NULL"/> 
<c:if test="${!empty sessionScope.uffint}">
	<c:set var="filtroUffint" value="AND W9DELEGHE.CODEIN = '${sessionScope.uffint}'"/>
</c:if>


<gene:template file="lista-template.jsp" gestisciProtezioni="true"
	idMaschera="W3DELEGHE-lista" schema="W3">
	<gene:setString name="titoloMaschera" value="Lista collaboratori RUP" />
	<gene:setString name="entita" value="W9DELEGHE" />
	<gene:redefineInsert name="corpo">
	
		
		
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
			<tr>
				<td><gene:formLista entita="W9DELEGHE"  where="W9DELEGHE.CFRUP in (SELECT CFTEC FROM TECNI WHERE TECNI.CFTEC='${sessionScope.profiloUtente.codiceFiscale}' ) ${filtroUffint}" pagesize="20" sortColumn="4"
					tableclass="datilista" gestisciProtezioni="true" gestore="it.eldasoft.sil.w3.tags.gestori.submit.GestoreW3DELEGHE" pathScheda="w3/w3deleghe/w3deleghe-scheda.jsp">
					<c:if test='${param.updateLista ne 1}'>
						<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center><br>" width="50">
							<c:if test="${currentRow >= 0}">
								<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"
									onClick="chiaveRiga='${chiaveRigaJava}'">
									<gene:PopUpItemResource
										resource="popupmenu.tags.lista.visualizza"
										title="Visualizza collaborazione" />
									<gene:PopUpItemResource
										resource="popupmenu.tags.lista.modifica"
										title="Modifica collaborazione" />
										
									<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}'>
											<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina"
												title="Elimina collaborazione" />
									</c:if>
								</gene:PopUp>
								
								<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
									<input type="checkbox" name="keys" value="${chiaveRiga}" />
								</c:if>
								
							</c:if>
						</gene:campoLista>
					</c:if>
					
					<gene:campoLista campo="ID" visibile="false" edit="${param.updateLista eq 1 }"/>
					<gene:campoLista campo="SYSCON" entita="USRSYS" where="USRSYS.SYSCON=W9DELEGHE.ID_COLLABORATORE"/>
					<gene:campoLista campo="SYSUTE" entita="USRSYS" where="USRSYS.SYSCON=W9DELEGHE.ID_COLLABORATORE" title="Utente" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>
					<gene:campoLista campo="RUOLO" edit="${param.updateLista eq 1 }"/>
					<input type="hidden" name="numeroCollaboratori" id="numeroCollaboratori" value="" />
					
					<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
					<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
					<gene:redefineInsert name="addToAzioni">
						<c:choose>
							<c:when test='${param.updateLista eq 1}'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:listaConferma();" title="Salva modifiche" tabindex="1500">
										Salva
									</a>
								</td>
							</tr>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:listaAnnullaModifica();" title="Annulla modifiche" tabindex="1501">
										Annulla
									</a>
								</td>
							</tr>
							</c:when>
							<c:otherwise>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:aggiungi();" title="Nuovo" tabindex="1501">
									Nuovo</a>
								</td>
							</tr>
							<c:if test="${not (datiRiga.rowCount < 1)}">
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:listaApriInModifica();" title='Modifica ruoli' tabindex="1502">
											Modifica ruoli
										</a>
									</td>
								</tr>
							</c:if>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:listaEliminaSelezione();" title="Elimina" tabindex="1503">
									Elimina selezionati</a>
								</td>
							</tr>
							</c:otherwise>
						</c:choose>
						
					</gene:redefineInsert>
				</gene:formLista></td>
			</tr>
			<c:if test='${param.updateLista eq 1 }'>
				<gene:redefineInsert name="pulsanteListaInserisci"/>
				<gene:redefineInsert name="pulsanteListaEliminaSelezione"/>
				<gene:redefineInsert name="listaNuovo"/>
				<gene:redefineInsert name="listaEliminaSelezione"/>
				<c:set var="isNavigazioneDisabilitata" value="1" scope="request"/>
			</c:if>
	
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<c:choose>
						<c:when test='${param.updateLista eq 1 }'>
							<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="listaConferma();">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:listaAnnullaModifica();">
						</c:when>
						<c:otherwise>
							<INPUT type="button" class="bottone-azione" value="Nuovo" title="Nuovo" onclick="javascript:aggiungi();">&nbsp;&nbsp;
							<INPUT type="button" class="bottone-azione" value='Modifica ruoli' title='Modifica ruoli' onclick="javascript:listaApriInModifica();">&nbsp;
							<INPUT type="button" class="bottone-azione" value="Elimina selezionati" title="Elimina selezionati" onclick="javascript:listaEliminaSelezione();">&nbsp;&nbsp;	
						</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
	
	
	
	
	<gene:javaScript>
		document.forms[0].jspPathTo.value="w3/w3deleghe/w3deleghe-scheda.jsp";
		
		document.getElementById("numeroCollaboratori").value = ${currentRow}+1;	
		
		
		function aggiungi() {
			var rows = "${datiRiga.rowCount}";
			if(rows <1){
				openPopUpCustom("href=w3/commons/popup-login-simog.jsp", "loginSimog", 550, 400, "yes", "yes");
			}else{
				location.href = '${pageContext.request.contextPath}/ApriPagina.do?' + csrfToken + '&href=w3/w3deleghe/w3deleghe-scheda.jsp&modo=NUOVO';
			}
		}		
		
	</gene:javaScript>	
</gene:template>