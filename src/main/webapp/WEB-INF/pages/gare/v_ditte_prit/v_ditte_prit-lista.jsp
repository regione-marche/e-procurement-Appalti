<%/*
   * Created on 10-apr-2009
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneOperazioniRitiroFunction" parametro="${key}" />

<%
//<gene:set name="titoloMenu">
//	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
//</gene:set> //
%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="filtroFaseOpRitiro" value="${filtroFaseOpRitiro}" scope="request" />

<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_DITTE_PRIT-lista">
	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>
	</gene:redefineInsert>
	<gene:setString name="titoloMaschera" value="Operazioni di ritiro plichi"/>
	<gene:redefineInsert name="corpo">
		<table class="lista">
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td style="background-color:#EEEEEE; border: 1px dotted #999999; padding: 2px 0px 2px 2px; display: block;" >
					<c:set var="strPagineVisitate" value=""/>
					<c:set var="strPagineDaVisitare" value=""/>
					<c:forEach items="${pagineVisitate}" var="pagina" >
						<c:set var="strPagineVisitate" value="${strPagineVisitate} -> ${pagina}"/>	
					</c:forEach>
					<c:set var="strPagineVisitate" value="${fn:substring(strPagineVisitate, 4, fn:length(strPagineVisitate))}" />
					<c:if test='${fn:length(pagineDaVisitare) > 0}' >
						<c:forEach items="${pagineDaVisitare}" var="pagina" >
							<c:set var="strPagineDaVisitare" value="${strPagineDaVisitare} -> ${pagina}" />
						</c:forEach>
						<c:set var="strPagineDaVisitare" value="${fn:substring(strPagineDaVisitare, 0, fn:length(strPagineDaVisitare))}" />
					</c:if>
					<span class="avanzamento-paginevisitate"><c:out value="${fn:trim(strPagineVisitate)}" escapeXml="true" /></span><span class="avanzamento-paginedavisitare"><c:out value="${strPagineDaVisitare}" escapeXml="true" /></span>
				</td>
			</tr>
			<tr>
				<td>
					<gene:formLista entita="V_DITTE_PRIT" where='${filtroFaseOpRitiro}' tableclass="datilista" sortColumn="1;2;3" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreOperazioniRitiro">
						<gene:redefineInsert name="listaNuovo" />
						<gene:redefineInsert name="listaEliminaSelezione" />
						<gene:redefineInsert name="addToAzioni" >
							<c:choose>
								<c:when test='${updateLista eq 1}'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:conferma();" title="Salva modifiche" tabindex="1500">
												${gene:resource("label.tags.template.dettaglio.schedaConferma")}
											</a>
										</td>
									</tr>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:listaAnnullaModifica();" title="Annulla modifiche" tabindex="1501">
												${gene:resource("label.tags.template.dettaglio.schedaAnnulla")}
											</a>
										</td>
									</tr>
								</c:when>
								<c:otherwise>
									<c:if test='${datiRiga.rowCount > 0}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:listaApriInModifica();" title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' tabindex="1504">
													${gene:resource("label.tags.template.dettaglio.schedaModifica")}
												</a>
											</td>
										</tr>
									</c:if>
									<c:if test='${paginaAttivaWizard > 1}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:indietro();" title='Fase precedente' tabindex="1505">
													< Fase precedente
												</a>
											</td>
										</tr>
									</c:if>
									<c:if test='${paginaAttivaWizard < 2}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:avanti();" title='Fase seguente' tabindex="1507">
													Fase seguente >
												</a>
											</td>
										</tr>
									</c:if>
									<c:if test='${paginaAttivaWizard > 1 and datiRiga.rowCount > 0}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:apriPopupStampa();" title='Stampa per ritiro definitivo' tabindex="1506">
													Stampa per ritiro definitivo
												</a>
											</td>
										</tr>
									</c:if>
								</c:otherwise>
							</c:choose>
						</gene:redefineInsert>
						<gene:campoLista campo="NGARA" headerClass="sortable" />
						<gene:campoLista campo="TIPPROT" headerClass="sortable" />
						<gene:campoLista campo="NPROGG" title="N." headerClass="sortable" />
						<gene:campoLista campo="DITTAO" headerClass="sortable" visibile="false" edit="${updateLista eq 1}"/>
						<c:set var="link" value='javascript:archivioImpresa("${datiRiga.V_DITTE_PRIT_DITTAO}");' />
						<gene:campoLista campo="NOMIMO" headerClass="sortable" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda"), link, "")}' />
						<gene:campoLista campo="PROTOCOLLO" headerClass="sortable" />
						<gene:campoLista campo="DATAP" headerClass="sortable" />
						<gene:campoLista campo="ORAP" headerClass="sortable" />
						<gene:campoLista campo="CODGAR" headerClass="sortable" visibile='false'/>
						<gene:campoLista campo="RITIRO" entita="V_DITTE_PRIT" value="${datiRiga.V_DITTE_PRIT_RITIRO}" visibile="false" />

				<c:choose>
					<c:when test='${updateLista eq 0}'>
						<gene:campoLista campo="RITIRO_FASE1" campoFittizio="true" entita="V_DITTE_PRIT" definizione="N1;0;;SN" title="Ritiro?" edit="${updateLista eq 1}" value="${gene:if(empty datiRiga.V_DITTE_PRIT_RITIRO or datiRiga.V_DITTE_PRIT_RITIRO eq 0,0,1)}" visibile="${paginaAttivaWizard eq 1 }" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoSenzaNull"/>
						<gene:campoLista campo="RITIRO_FASE2" campoFittizio="true" entita="V_DITTE_PRIT" definizione="N1;0;;SN" title="Ritiro?" edit="${updateLista eq 1}" value="${gene:if(datiRiga.V_DITTE_PRIT_RITIRO eq 1,1,0)}" visibile="${paginaAttivaWizard eq 2 }" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoSenzaNull"/>
					</c:when>
					<c:otherwise>
						<gene:campoLista campo="RITIRO_FASE1" campoFittizio="true" entita="V_DITTE_PRIT" definizione="N1;0;;SN" title="Ritiro?" edit="${updateLista eq 1}" value="${gene:if(empty datiRiga.V_DITTE_PRIT_RITIRO or datiRiga.V_DITTE_PRIT_RITIRO eq 0,0,1)}" visibile="false" />
						<gene:campoLista campo="RITIRO_FASE2" campoFittizio="true" entita="V_DITTE_PRIT" definizione="N1;0;;SN" title="Ritiro?" edit="${updateLista eq 1}" value="${gene:if(datiRiga.V_DITTE_PRIT_RITIRO eq 1,1,0)}" visibile="false" />
						<gene:campoLista title="Ritiro<center>  <a href='javascript:selTuttiRit(document.forms[0].RITIRO_FASE);' Title='Seleziona Tutti'> <img src='${pageContext.request.contextPath}/img/ico_check.gif' height='15' width='15' alt='Seleziona Tutti'></a>&nbsp;<a href='javascript:deselTuttiRit(document.forms[0].RITIRO_FASE);' Title='Deseleziona Tutti'><img src='${pageContext.request.contextPath}/img/ico_uncheck.gif' height='15' width='15' alt='Deseleziona Tutti'></a></center>" width="50" >

							<c:if test="${paginaAttivaWizard eq 1}">
								<INPUT type="checkbox" name="RITIRO_FASE" id="RITIRO_FASE1_${currentRow+1}" value="" onchange="javascript:aggiornaRitiro(${currentRow+1});"/>
							</c:if>
							<c:if test="${paginaAttivaWizard eq 2}">
								<INPUT type="checkbox" name="RITIRO_FASE" id="RITIRO_FASE2_${currentRow+1}" value="" <c:if test='${datiRiga.V_DITTE_PRIT_RITIRO eq "1"}' >checked="checked" </c:if> onchange="javascript:aggiornaRitiro(${currentRow+1});"/>
							</c:if>
					  	</gene:campoLista>
					</c:otherwise>
				</c:choose>
				
						<gene:campoLista campo="campofittizioNGARA" campoFittizio="true" entita="V_DITTE_PRIT" definizione="T20" visibile="false" edit="${updateLista eq 1}" value="${datiRiga.V_DITTE_PRIT_NGARA}"/>
						<gene:campoLista campo="campofittizioTIPPROT" campoFittizio="true" entita="V_DITTE_PRIT" definizione="N2" visibile="false" edit="${updateLista eq 1}" value="${datiRiga.V_DITTE_PRIT_TIPPROT}"/>
						<gene:campoLista campo="campofittizioCODGAR" campoFittizio="true" entita="V_DITTE_PRIT" definizione="T21" visibile="false" edit="${updateLista eq 1}" value="${datiRiga.V_DITTE_PRIT_CODGAR}"/>
						<input type="hidden" name="WIZARD_PAGINA_ATTIVA" value="${paginaAttivaWizard}" />
						<input type="hidden" name="numeroDitte" id="numeroDitte" value="" />
						<input type="hidden" name="numeroDitteTotali" id="numeroDitteTotali" value="" />
					</gene:formLista>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<c:choose>
						<c:when test='${updateLista eq 1}'>
							<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:listaConferma();">
							<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:listaAnnullaModifica();">
						</c:when>
						<c:otherwise>
							<c:if test='${datiRiga.rowCount > 0}'>
								<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' title='${gene:resource("label.tags.template.dettaglio.schedaModifica")}' onclick="javascript:listaApriInModifica();">&nbsp;&nbsp;&nbsp;
							</c:if>
							<c:if test='${paginaAttivaWizard > 1}'>
								<INPUT type="button"  class="bottone-azione" value='< Fase precedente' title='Fase precedente' onclick="javascript:indietro();">
							</c:if>
							<c:if test='${paginaAttivaWizard < 2 }'>
								<INPUT type="button"  class="bottone-azione" value='Fase seguente >' title='Fase seguente' onclick="javascript:avanti();">
							</c:if>
							<c:if test='${paginaAttivaWizard > 1}'>
								<c:if test='${datiRiga.rowCount > 0}'>
									<INPUT type="button"  class="bottone-azione" value='Stampa per ritiro definitivo' title='Stampa per ritiro definitivo' onclick="javascript:apriPopupStampa();">
								</c:if>
							</c:if>
						</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</tr>
		</table>
	</gene:redefineInsert>
<gene:javaScript>
	  
	  document.getElementById("numeroDitte").value = ${currentRow}+1;
	  document.getElementById("numeroDitteTotali").value = ${datiRiga.rowCount};
	  
	  function archivioImpresa(codiceImpresa){
	<c:choose>
		<c:when test='${updateLista eq 1}' >
			var href = ("href=gene/impr/impr-scheda-popup.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
			openPopUp(href, "schedaImpresa");
		</c:when>
		<c:otherwise>
			var href = ("href=gene/impr/impr-scheda.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
			document.location.href = contextPath + "/ApriPagina.do?"+csrfToken+"&" + href + "&key=IMPR.CODIMP=T:" + codiceImpresa;
		</c:otherwise>
	</c:choose>
		}
		
	<c:if test='${paginaAttivaWizard < 2}'>
		function avanti(){
			setValue("WIZARD_PAGINA_ATTIVA", ${paginaAttivaWizard + 1});
			document.forms[0].pgSort.value = "";
			document.forms[0].pgLastSort.value = "";
			document.forms[0].pgLastValori.value = "";
			listaVaiAPagina(0);
		}
	</c:if>
		
	<c:if test='${paginaAttivaWizard > 1}'>
		function indietro(){
			if(document.forms[0].action.indexOf("Scheda.do") >= 0){
				document.forms[0].action = "${pageContext.request.contextPath}/Lista.do?"+csrfToken;
				document.forms[0].keyParent.value = document.forms[0].key.value;
			} else {
				document.forms[0].pgSort.value = "";
				document.forms[0].pgLastSort.value = "";
				document.forms[0].pgLastValori.value = "";
			}
			setValue("WIZARD_PAGINA_ATTIVA", ${paginaAttivaWizard - 1});
			listaVaiAPagina(0);
		}
	</c:if>
	
	<c:if test='${updateLista eq 1}'>
		function conferma(){
			listaConferma();
		}
		
		function aggiornaRitiro(numeroRiga){
	<c:choose>
		<c:when test="${paginaAttivaWizard eq 1}">
			var isChecked = document.getElementById("RITIRO_FASE1_" + numeroRiga).checked;
			var ritiro = getValue("V_DITTE_PRIT_RITIRO_FASE1_" + numeroRiga);
			if(isChecked)
				setValue("V_DITTE_PRIT_RITIRO_FASE1_" + numeroRiga, 1);
			else
				setValue("V_DITTE_PRIT_RITIRO_FASE1_" + numeroRiga, getOriginalValue("V_DITTE_PRIT_RITIRO_FASE1_" + numeroRiga));
		</c:when>
		<c:when test="${paginaAttivaWizard eq 2}">
			var isChecked = document.getElementById("RITIRO_FASE2_" + numeroRiga).checked;
			var ritiro = getValue("V_DITTE_PRIT_RITIRO_FASE2_" + numeroRiga);
			if(isChecked)
				setValue("V_DITTE_PRIT_RITIRO_FASE2_" + numeroRiga, 1);
			else
				setValue("V_DITTE_PRIT_RITIRO_FASE2_" + numeroRiga, 0);				
		</c:when>
	</c:choose>
		}
		
		 function selTuttiRit(achkArrayCheckBox) {
 			if (achkArrayCheckBox) {
			  var arrayLen = "" + achkArrayCheckBox.length;
			  if(arrayLen != 'undefined') {
			    for (var i = 0; i < achkArrayCheckBox.length; i++) {
			    	if(! achkArrayCheckBox[i].disabled){
				      achkArrayCheckBox[i].checked = true;
				      setValue("V_DITTE_PRIT_RITIRO_FASE${paginaAttivaWizard}_" + (i+1), 1);
				    }
			    }
			  } else {
			  	if(! achkArrayCheckBox.disabled){
			      achkArrayCheckBox.checked = true;
			      setValue("V_DITTE_PRIT_RITIRO_FASE${paginaAttivaWizard}_" + (i+1), 1);
			    }
			  }
    		}
  		}
		  function deselTuttiRit(achkArrayCheckBox) {
		    if (achkArrayCheckBox) {
		      var arrayLen = "" + achkArrayCheckBox.length;
			  if(arrayLen != 'undefined') {
		  	  for (var i = 0; i < achkArrayCheckBox.length; i++) {
		  	      achkArrayCheckBox[i].checked = false;
		  	      setValue("V_DITTE_PRIT_RITIRO_FASE${paginaAttivaWizard}_" + (i+1), 0);
		    	  }
		      } else {
		        if (achkArrayCheckBox){
		          achkArrayCheckBox.checked = false;
		          setValue("V_DITTE_PRIT_RITIRO_FASE${paginaAttivaWizard}_" + (i+1), 0);
		        }
		      }
		    }
		  }
				
	</c:if>	
	
	function apriPopupStampa(){
		var trovaAddWhere = "" + document.forms[0].trovaAddWhere.value;
		var trovaParameter = "" + document.forms[0].trovaParameter.value;
			
		var tipprot = "";
		var datap = "";
		var operatoreDatap = "";

		var numeroCondizioniWhere = 0;
		if(trovaAddWhere != ""){
			numeroCondizioniWhere = 1;
			if(trovaAddWhere.indexOf("and") > 0)
				numeroCondizioniWhere = 2;
		}
		
		if(trovaAddWhere.indexOf("V_DITTE_PRIT.TIPPROT") >= 0){
			if(trovaAddWhere.indexOf("V_DITTE_PRIT.TIPPROT") == 0)
				tipprot = trovaParameter.substring(2, trovaParameter.indexOf(";"));
			else
				tipprot = trovaParameter.substring(trovaParameter.indexOf(";") +1 + 2);
		}
		if(trovaAddWhere.indexOf("V_DITTE_PRIT.DATAP") >= 0){
			if(trovaAddWhere.indexOf("V_DITTE_PRIT.DATAP") == 0){
				if(trovaParameter.indexOf(";") > 0)
					datap = trovaParameter.substring(2, trovaParameter.indexOf(";"));
				else
					datap = trovaParameter.substring(2);
				operatoreDatap = trimStringa(trovaAddWhere.substring(trovaAddWhere.indexOf("V_DITTE_PRIT.DATAP") + 18 + 1, trovaAddWhere.indexOf("?")));
			} else {
				datap = trovaParameter.substring(trovaParameter.indexOf(";") + 1 + 2);
				operatoreDatap = trimStringa(trovaAddWhere.substring(trovaAddWhere.indexOf("V_DITTE_PRIT.DATAP") + 18 + 1, trovaAddWhere.lastIndexOf("?")));
			}
		}
			
		//alert("tipprot = '" + tipprot + "'\ndatap = '" + datap + "'\noperatoreDatap = '" + operatoreDatap + "'");
		var href = "href=gare/v_ditte_prit/popupConfermaStampaLista.jsp&tipprot=" + tipprot + "&datap=" + datap + "&operatoreDatap=" + operatoreDatap;
		openPopUpCustom(href, "confermaStampa", 540, 230, "yes", "yes");
	}

	function rileggi(){
		historyReload();
	}

  </gene:javaScript>
</gene:template>