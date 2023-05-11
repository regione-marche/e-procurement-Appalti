
<%
	/* 
	 * Created on 04-Nov-2008
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */

	// Scheda degli intestatari della concessione stradale
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/funzioniIDGARACIG.js"></script>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="W3SMARTCIG-Scheda" schema="W3">
	<c:set var="entita" value="W3SMARTCIG" />
	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.sil.w3.tags.funzioni.GetTitleFunction",pageContext,entita)}'/>

	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="W3SMARTCIG" gestisciProtezioni="true" gestore="it.eldasoft.sil.w3.tags.gestori.submit.GestoreW3SMARTCIG" 
		 plugin="it.eldasoft.sil.w3.tags.gestori.plugin.GestoreW3SMARTCIG" >

			<c:set var="archiviFiltrati" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata.archiviFiltrati")}'/>
			<c:set var="filtroUffint" value=""/>
			<c:set var="filtroUffintTecniParam" value="" />
			<c:set var="filtroUffintTecniParam2" value="T:null;" />
			<c:set var="filtroUffintDeleghe" value="T:null;" />
			<c:if test="${!empty sessionScope.uffint}">
				<c:set var="filtroUffintDeleghe" value="T:${sessionScope.uffint};" />
				<c:if test="${fn:contains(archiviFiltrati,'TECNI')}">
					<c:set var="filtroUffintTecniParam" value="T:${sessionScope.uffint};" />
				</c:if>
			</c:if>
			<c:if test="${!empty sessionScope.profiloUtente.codiceFiscale}">
				<c:set var="filtroUffintTecniParam2" value="T:${sessionScope.profiloUtente.codiceFiscale};" />
			</c:if>

			<c:set var="codrich" value='${gene:getValCampo(key,"CODRICH")}' scope="request" />
	
			<gene:campoScheda campo="CIG" modificabile="false"/>
			<gene:campoScheda title="Stato della gara" campo="STATO" modificabile="false" defaultValue="1" />
			<gene:campoScheda campo="DATA_OPERAZIONE" modificabile="false" visibile="${datiRiga.W3SMARTCIG_STATO ne '1'}"/>
			<gene:campoScheda campo="CODEIN" visibile="false" defaultValue="${sessionScope.uffint}" />
			
			<gene:campoScheda>
				<td colspan="2"><b><br>SEZIONE I: RUP e COLLABORAZIONE (AMMINISTRAZIONE/STAZIONE APPALTANTE)<br><br></b></td>
			</gene:campoScheda>
			<gene:campoScheda>
				<td colspan="2"><b>I.1) Responsabile unico del procedimento</b></td>
			</gene:campoScheda>
			<c:choose>
				<c:when test='${fromGare ne "1"}' >
					<gene:archivio titolo="Tecnici" inseribile="false"
						lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.W3.W3SMARTCIG.RUP"),"gene/tecni/tecni-lista-popup.jsp","")}'
						scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
						functionId="w3"
						parametriWhere="${filtroUffintTecniParam}T:${sessionScope.profiloUtente.id};${filtroUffintDeleghe}${filtroUffintTecniParam2}"
						schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
						campi="TECNI.CODTEC;TECNI.NOMTEC;TECNI.CFTEC" chiave="W3GARA_RUP" >
						<gene:campoScheda campo="RUP" visibile="false" defaultValue="${rup}"/>
						<gene:campoScheda title="Denominazione" entita="TECNI" campo="NOMTEC" where="TECNI.CODTEC=W3SMARTCIG.RUP" defaultValue="${nomtec}" obbligatorio="true"/>
						<gene:campoScheda title="Codice fiscale" entita="TECNI" campo="CFTEC" defaultValue="${cftec}"/>
					</gene:archivio>
					</c:when>
					<c:otherwise>
						<gene:campoScheda campo="RUP" visibile="false" defaultValue="${rup}"/>
						<gene:campoScheda title="Denominazione" entita="TECNI" campo="NOMTEC" where="TECNI.CODTEC=W3SMARTCIG.RUP" defaultValue="${nomtec}" modificabile="false"/>
						<gene:campoScheda title="Codice fiscale" entita="TECNI" campo="CFTEC" defaultValue="${cftec}" modificabile="false"/>
					</c:otherwise>
					</c:choose>
				<c:if test='${fromGare ne "1"}' >
					<gene:callFunction obj="it.eldasoft.sil.w3.tags.funzioni.GetListaCollaboratoriFunction" />
				</c:if>
			<gene:campoScheda>
				<td colspan="2"><b><br>I.2) Centri di Costo / Amministrazione</b></td>
			</gene:campoScheda>
			<gene:campoScheda entita="UFFINT" campo="NOMEIN" where="UFFINT.CODEIN=W3SMARTCIG.CODEIN" title="Denominazione amministrazione" modificabile="false" defaultValue="${azienda_denom}"/>
			<gene:campoScheda entita="UFFINT" campo="CFEIN" where="UFFINT.CODEIN=W3SMARTCIG.CODEIN" title="Codice fiscale amministrazione" modificabile="false" defaultValue="${azienda_cf}"/>
			<gene:campoScheda entita="W3SMARTCIG" campo="IDCC" where="CENTRICOSTO.IDCENTRO=W3SMARTCIG.IDCC" visibile="false" modificabile="false"/>
			<gene:campoScheda entita="CENTRICOSTO" campo="CODCENTRO" where="CENTRICOSTO.IDCENTRO=W3SMARTCIG.IDCC"  modificabile="false"/>
			<c:choose>
				<c:when test="${modo eq 'MODIFICA' || modo eq 'NUOVO'}">
					
					
					<c:choose>
						<c:when test='${!empty listaRUP || fromGare eq "1"}'>
							<gene:campoScheda entita="CENTRICOSTO" campo="DENOMCENTRO" where="CENTRICOSTO.IDCENTRO=W3SMARTCIG.IDCC" speciale="true">
						<gene:popupCampo titolo="Seleziona centro di costo" href="javascript:valorizzaCC()" />
					</gene:campoScheda>
						</c:when> 
						<c:otherwise>
							<gene:campoScheda entita="CENTRICOSTO" campo="DENOMCENTRO" where="CENTRICOSTO.IDCENTRO=W3SMARTCIG.IDCC" modificabile="false"/>
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
				
					
					<gene:campoScheda entita="CENTRICOSTO" campo="DENOMCENTRO" where="CENTRICOSTO.IDCENTRO=W3SMARTCIG.IDCC" modificabile="false"/>
				</c:otherwise>
			</c:choose>
	
			<gene:campoScheda>
				<td colspan="2"><b><br>SEZIONE II: RICHIESTA CIG</b><br><br></td>
			</gene:campoScheda>
			<gene:campoScheda>
				<td colspan="2"><b>II.1) Dati generali</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="CODRICH" visibile="false"/>
			<gene:campoScheda campo="OGGETTO" obbligatorio="true"/>
			<gene:campoScheda campo="FATTISPECIE" obbligatorio="true"/>
			<gene:campoScheda campo="IMPORTO" obbligatorio="true"/>
			<gene:campoScheda campo="ID_SCELTA_CONTRAENTE" obbligatorio="true"/>
			<gene:campoScheda campo="TIPO_CONTRATTO" obbligatorio="true"/>
			
			<gene:campoScheda>
				<td colspan="2"><b><br>II.2) Riferimento ad un accordo quadro</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="CIG_ACC_QUADRO" />
			<gene:campoScheda>
				<td colspan="2"><b><br>II.3) CUP</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="CUP" />
			<gene:campoScheda>
				<td colspan="2"><b><br>II.4) Motivazioni per la richiesta CIG e categorie merceologiche</b></td>
			</gene:campoScheda>	
			<gene:campoScheda campo="M_RICH_CIG"/>
			<gene:campoScheda campo="M_RICH_CIG_COMUNI" />
			<gene:callFunction obj="it.eldasoft.sil.w3.tags.funzioni.GestioneW3SMARTCIGMERCFunction" parametro="${codrich}" />
			<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
				<jsp:param name="entita" value='W3SMARTCIGMERC'/>
				<jsp:param name="chiave" value='${codrich}'/>
				<jsp:param name="nomeAttributoLista" value='datiW3SMARTCIGMERC' />
				<jsp:param name="idProtezioni" value="W3SMARTCIGMERC" />
				<jsp:param name="sezioneListaVuota" value="true" />
				<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/w3/w3smartcigmerc/w3smartcigmerc-interno-scheda.jsp"/>
				<jsp:param name="arrayCampi" value="'W3SMARTCIGMERC_CODRICH_','W3SMARTCIGMERC_NUMMERC_','W3SMARTCIGMERC_CATEGORIA_'"/>
				<jsp:param name="titoloSezione" value="<br>Categoria merceologica n. " />
				<jsp:param name="titoloNuovaSezione" value="<br>Nuova categoria merceologica" />
				<jsp:param name="descEntitaVociLink" value="categoria merceologica" />
				<jsp:param name="msgRaggiuntoMax" value="e categorie merceologiche"/>
				<jsp:param name="usaContatoreLista" value="true"/>
				<jsp:param name="numMaxDettagliInseribili" value="5"/>
				<jsp:param name="sezioneInseribile" value="true"/>
				<jsp:param name="sezioneEliminabile" value="true"/>
			</jsp:include>
	
			<gene:redefineInsert name="pulsanteNuovo" />
			<gene:redefineInsert name="schedaNuovo" />
	
			<c:if test="${datiRiga.W3SMARTCIG_STATO eq '99'}">
				<gene:redefineInsert name="pulsanteModifica" />
				<gene:redefineInsert name="schedaModifica" />
			</c:if>
		
			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
			
			<gene:fnJavaScriptScheda funzione="gestioneRUP_CODTEC('#W3SMARTCIG_RUP#')" elencocampi="W3SMARTCIG_RUP" esegui="false" />
			
		</gene:formScheda>
		
		<gene:redefineInsert name="addToAzioni" >
			<jsp:include page="/WEB-INF/pages/w3/commons/addtodocumenti-idgaracig.jsp" >
				<jsp:param name="entita" value='W3SMARTCIG'/>
				<jsp:param name="stato" value='${datiRiga.W3SMARTCIG_STATO}'/>
			</jsp:include>
		</gene:redefineInsert>

		<gene:javaScript>
			function modifySelectOption(obj) {
				var obj_opt = obj.find("option");
				$.each(obj_opt, function( key, opt ) {
				var _title = opt.title;
				if (_title.length > 110) {
				 	_title = _title.substring(0,110) + "...";
				}
				opt.text = _title;
				});
				obj.css("width","550px");	
			}
		
		function gestioneRUP_CODTEC(value) {
		
		if (isValueChanged("W3SMARTCIG_RUP")) {
			document.forms[0].W3SMARTCIG_IDCC.value='';
			document.forms[0].CENTRICOSTO_DENOMCENTRO.value='';
			if(${empty sessionScope.uffint}){
				document.getElementById("UFFINT_CFEINview").innerText = "";
				document.getElementById("UFFINT_NOMEINview").innerText = "";
			}
			document.getElementById("CENTRICOSTO_CODCENTROview").innerText = "";
		}
		
		
	}
	
	function valorizzaCC(){
		if(isAbilitato()){
			openPopUpCustom("href=w3/commons/popup-valorizza-centrocosto.jsp?codrup="+document.forms[0].W3SMARTCIG_RUP.value, "valorizzaCC", 550, 300, "yes", "yes");
		}else{
			if(document.forms[0].W3SMARTCIG_RUP.value){
				alert("Non è possibile procedere.\nL'utente corrente non \u00E8 il RUP della gara n\u00E9 un suo collaboratore.");
			}else{
				alert("Per valorizzare il centro di costo, selezionare il responsabile.");
			}
		}
	}
	
	function valorizzaCC(){
		if(isAbilitato()){
			var href = "href=w3/commons/popup-valorizza-centrocosto.jsp?codrup="+document.forms[0].W3SMARTCIG_RUP.value;
			if(${fromGare} == "1"){
				href += "&codeinFromGare="+document.forms[0].W3SMARTCIG_CODEIN.value;
			}
			openPopUpCustom(href, "valorizzaCC", 550, 300, "yes", "yes");
		}else{
			if(document.forms[0].W3SMARTCIG_RUP.value){
				alert("Non è possibile procedere.\nL'utente corrente non \u00E8 il RUP della gara n\u00E9 un suo collaboratore.");
			}else{
				alert("Per valorizzare il centro di costo, selezionare il responsabile.");
			}
		}
	}
	
	function isAbilitato(){
		if(!document.forms[0].W3SMARTCIG_RUP.value){
			return false;
		}
		var codrup = '['+document.forms[0].W3SMARTCIG_RUP.value;
		var lsRup = '${listaRUP}';
		var abilitato=false;
		if(lsRup.indexOf(codrup)>-1){
			abilitato = true;
		}
		if(${fromGare} == "1"){
			abilitato = true;
		}
		return abilitato;
	}
	
	$(document).ready(function(){
		$("input[name*='DENOMCENTRO']").attr('readonly','readonly');
		$("input[name*='DENOMCENTRO']").attr('tabindex','-1');
		$("input[name*='DENOMCENTRO']").css('border-color','#A3A6FF');
		$("input[name*='DENOMCENTRO']").css('border-width','1px');
		$("input[name*='DENOMCENTRO']").css('background-color','#E0E0E0');
	});
	
		</gene:javaScript>
	</gene:redefineInsert>

</gene:template>
