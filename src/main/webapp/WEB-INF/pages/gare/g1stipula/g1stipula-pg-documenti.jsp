<%
/*
 * Created on: 02/12/2009
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<c:set var="idStipula" value='${gene:getValCampo(key, "G1STIPULA.ID")}' />
<c:set var="abilitazioneGare" value="${sessionScope.profiloUtente.abilitazioneGare}" />
<c:set var="utente" value="${sessionScope.profiloUtente.id}" />
<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDatiStipulaFunction", pageContext, idStipula)}'/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="digitalSignatureUrlCheck" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-check-url")}'/>
<c:set var="digitalSignatureProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>
<c:choose>
	<c:when test="${!empty digitalSignatureUrlCheck && !empty digitalSignatureProvider && (digitalSignatureProvider eq 1 || digitalSignatureProvider eq 2)}">
		<c:set var="digitalSignatureWsCheck" value='1'/>
	</c:when>
	<c:otherwise>
		<c:set var="digitalSignatureWsCheck" value='0'/>
	</c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
	<jsp:param name="entita" value="V_GARE_STIPULA"/>
	<jsp:param name="inputFiltro" value="${key}"/>
	<jsp:param name="filtroCampoEntita" value="idstipula=${idStipula}"/>
</jsp:include>

<table class="dettaglio-tab-lista">
	<tr>
		<td ${stileDati}>
			<gene:formLista entita="G1DOCSTIPULA" where="G1DOCSTIPULA.IDSTIPULA=${idStipula}" tableclass="datilista" sortColumn="5;6" pagesize="25" >
				<gene:redefineInsert name="addToAzioni" >
					<c:if test='${autorizzatoModifiche ne "2" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.G1STIPULA-scheda.DOCSTIP.InserisciDocumentiPredefiniti")}'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:insDocPredefiniti();" title="Inserisci documenti predefiniti" tabindex="1510">
										Inserisci documenti predefiniti
									</a>
								</td>
							</tr>
					</c:if>
					<c:if test='${autorizzatoModifiche ne "2" and gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.G1STIPULA-scheda.DOCSTIP.ModificaOrdinamentoDocStipula")}'>
						<tr>
							<td class="vocemenulaterale" >
								<a href="javascript:modificaOrdinam()" title='Modifica disposizione documenti' tabindex="1511">
								Modifica disposizione documenti
							</td>
						</tr>
					</c:if>
					<c:if test='${(abilitazioneGare eq "A" || utente eq requestScope.creatore || utente eq requestScope.assegnatario) and (gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.G1STIPULA-scheda.DOCSTIP.InviaContrEPubblicaSuAppalti"))}'>
							<tr>
								<td class="vocemenulaterale">
									<a href='javascript:inviaAggiudicatarioContraente();' title='Invia a contraente e pubblica su portale Appalti' tabindex="1512">
										Invia a contraente e pubblica su portale Appalti
									</a>
								</td>
							</tr>
					</c:if>
					<c:if test='${(autorizzatoModifiche ne "2") && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.G1STIPULA-scheda.DOCSTIP.RiportaInCompilazione")}'>
							<tr>
								<td class="vocemenulaterale">
									<a href='javascript:riportaInCompilazione();' title='Riporta in compilazione' tabindex="1513">
										Riporta in compilazione
									</a>
								</td>
							</tr>
					</c:if>
				</gene:redefineInsert>	
				
					<c:set var="oldTab1desc" value="${newTab1desc}"/>
					<c:set var="newTab1desc" value="${datiRiga.G1DOCSTIPULA_FASE}"/>
					<gene:campoLista campoFittizio="true" visibile="false">
						<%/* Nel caso in cui siano diversi inframezzo il titolo */%>
						<c:if test="${!empty newTab1desc && (newTab1desc != oldTab1desc)}">
								<gene:sqlSelect nome="descrFase" parametri="${param.inputFiltro}" tipoOut="VectorString" >
									select tab1desc from tab1 where tab1cod='A1181' and tab1tip= ${newTab1desc}
								</gene:sqlSelect>
							<td colspan="9" >
								<b>${descrFase[0]}</b> 
							</td>
						</tr>
						<tr class="odd">
						</c:if>
					</gene:campoLista>
				
				
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
							<gene:PopUpItem title="Visualizza documento" href="javascript:listaVisualizza()" />
						<c:if test='${(autorizzatoModifiche ne "2") && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
							<gene:PopUpItem title="Modifica documento" href="javascript:listaModifica()" />
						</c:if>
						<c:if test='${(autorizzatoModifiche ne "2") && gene:checkProtFunz(pageContext, "DEL","DEL")}' >
							<gene:PopUpItem title="Elimina documento" href="javascript:listaElimina()" />
						</c:if>
					</gene:PopUp>
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
						<input type="checkbox" name="keys" value="${chiaveRiga}"  />
					</c:if>
				</gene:campoLista>
				<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza('${chiaveRigaJava}');" />
				<gene:campoLista campo="ID" visibile="false"/>
				<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDatiDocumentiStipulaFunction", pageContext, datiRiga.G1DOCSTIPULA_ID)}'/>
				<gene:campoLista campo="IDSTIPULA" visibile="false"/>
				<gene:campoLista campo="FASE" visibile="false" />
				<gene:campoLista campo="NUMORD" visibile="false" />
				
				<gene:campoLista campo="TITOLO" href="${gene:if(visualizzaLink, link, '')}" ordinabile="false"/>
				<gene:campoLista campo="DESCRIZIONE" ordinabile="false"/>
				<gene:campoLista campo="IDPRG" visibile="false" entita="V_GARE_DOCSTIPULA" where="V_GARE_DOCSTIPULA.ID=G1DOCSTIPULA.ID" />
				<gene:campoLista campo="IDDOCDIG" visibile="false" entita="V_GARE_DOCSTIPULA" where="V_GARE_DOCSTIPULA.ID=G1DOCSTIPULA.ID" />
				<gene:campoLista campo="FIRMACHECK" ordinabile="false" visibile="false" entita="V_GARE_DOCSTIPULA" where="V_GARE_DOCSTIPULA.ID=G1DOCSTIPULA.ID" />
				<gene:campoLista campo="FIRMACHECKTS" ordinabile="false" visibile="false" entita="V_GARE_DOCSTIPULA" where="V_GARE_DOCSTIPULA.ID=G1DOCSTIPULA.ID" />
				<gene:campoLista campo="DIGNOMDOC" visibile="true" entita="V_GARE_DOCSTIPULA" where="V_GARE_DOCSTIPULA.ID=G1DOCSTIPULA.ID" ordinabile="false"
				 href="javascript: 	visualizzaFileAllegato('${datiRiga.V_GARE_DOCSTIPULA_IDPRG}','${datiRiga.V_GARE_DOCSTIPULA_IDDOCDIG}', '${datiRiga.V_GARE_DOCSTIPULA_DIGNOMDOC}');" >
				 </gene:campoLista>
				<gene:campoLista campo="FIRMA_FIT" campoFittizio="true"  title ="" width="24" definizione="T2;;;;G1_DIGFIRMACHECK_DD" >
					<c:if test="${not empty datiRiga.V_GARE_DOCSTIPULA_FIRMACHECK and datiRiga.V_GARE_DOCSTIPULA_FIRMACHECK=='1'}">
						&nbsp;<img src="${pageContext.request.contextPath}/img/firmaRemota-valid.png" title="Verifica automatica firma digitale riuscita (data verifica ${datiRiga.V_GARE_DOCSTIPULA_FIRMACHECKTS})" alt="Verifica automatica firma digitale riuscita" width="16" height="16">
						<%-- Firma digitale verificata al ${datiRiga.V_GARE_DOCSTIPULA_FIRMACHECKTS} 
						Verifica automatica firma digitale riuscita (data verifica ${datiRiga.V_GARE_DOCSTIPULA_FIRMACHECKTS})--%>
					</c:if>	
					<c:if test="${not empty datiRiga.V_GARE_DOCSTIPULA_FIRMACHECK and datiRiga.V_GARE_DOCSTIPULA_FIRMACHECK=='2'}">
						&nbsp;<img src="${pageContext.request.contextPath}/img/firmaRemota-notvalid.png" title="Verifica automatica firma digitale NON riuscita (data verifica ${datiRiga.V_GARE_DOCSTIPULA_FIRMACHECKTS})" alt="Verifica automatica firma digitale NON riuscita" width="16" height="16">
						<%-- Firma digitale non verificabile al ${datiRiga.V_GARE_DOCSTIPULA_FIRMACHECKTS}
						Verifica automatica firma digitale NON riuscita (data verifica ${datiRiga.V_GARE_DOCSTIPULA_FIRMACHECKTS}) --%>
					</c:if>
				</gene:campoLista>
				<gene:campoLista campo="NOTE" ordinabile="false"/>
				<gene:campoLista campo="VISIBILITA" ordinabile="false"/>
				<gene:campoLista campo="OBBLIGATORIO" title="Obbl." ordinabile="false"/>
				<gene:campoLista campo="STATODOC" visibile="false"/>
				<gene:campoLista title="Stato" width="20" ordinabile="false">
				<c:choose>
					<c:when test='${datiRiga.G1DOCSTIPULA_STATODOC eq "1"}'>
						<span id="INFO_TOOLTIP${currentRow }" title="In compilazione"></span>
					</c:when>
					<c:when test='${datiRiga.G1DOCSTIPULA_STATODOC eq "2"}'>
						<span id="INFO_TOOLTIP${currentRow }" title="Pubblicato">
							<IMG SRC="${contextPath}/img/stipPubblicato.png"> </span>
					</c:when>
					<c:when test='${datiRiga.G1DOCSTIPULA_STATODOC eq "3"}'>
						<span id="INFO_TOOLTIP${currentRow }" title="Richiesta in corso">
							<IMG SRC="${contextPath}/img/stipRichiestaInCorso.png"> </span>
					</c:when>
					<c:when test='${datiRiga.G1DOCSTIPULA_STATODOC eq "4"}'>
						<span id="INFO_TOOLTIP${currentRow }" title="Ricevuto">
							<IMG SRC="${contextPath}/img/stipRicevuto.png"> </span>
					</c:when>
					<c:when test='${datiRiga.G1DOCSTIPULA_STATODOC eq "5"}'>
						<span id="INFO_TOOLTIP${currentRow }" title="Completo">
							<IMG SRC="${contextPath}/img/stipCompleto.png"> </span>
					</c:when>
					</c:choose>
				</gene:campoLista>

				
				<gene:campoLista campo="OBBLIGATORIO" visibile="false" />
			</gene:formLista >
		</td>
	</tr>
	<tr>
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<gene:insert name="pulsanteListaInserisci">
				<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
					<INPUT type="button"  class="bottone-azione" value='Aggiungi' title='Aggiungi' onclick="javascript:listaNuovo()">
				</c:if>
			</gene:insert>
			<gene:insert name="pulsanteListaEliminaSelezione">
				<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
					<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione()">
				</c:if>
			</gene:insert>
		
			&nbsp;
		</td>
	</tr>
	
	<form name="formVisFirmaDigitale" id="formVisFirmaDigitale" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
		<input type="hidden" name="href" value="gene/system/firmadigitale/verifica-firmadigitale.jsp" />
		<input type="hidden" name="idprg" id="idprg" value="" />
		<input type="hidden" name="iddocdig" id="iddocdig" value="" />
	</form>
	
	

	
</table>
<gene:javaScript>

		function visualizzaFileAllegato(idprg,iddocdig,dignomdoc) {
			var vet = dignomdoc.split(".");
			var ext = vet[vet.length-1];
			ext = ext.toUpperCase();
			<c:choose>
				<c:when test="${digitalSignatureWsCheck eq 0}">
					if(ext=='P7M' || ext=='TSD'){
						document.formVisFirmaDigitale.idprg.value = idprg;
						document.formVisFirmaDigitale.iddocdig.value = iddocdig;
						document.formVisFirmaDigitale.submit();
					}else{
						var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
						document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
					}
				</c:when>
				<c:otherwise>
					if(ext=='P7M' || ext=='TSD' || ext=='XML' || ext=='PDF'){
						document.formVisFirmaDigitale.idprg.value = idprg;
						document.formVisFirmaDigitale.iddocdig.value = iddocdig;
						document.formVisFirmaDigitale.submit();
					}else{
						var href = "${pageContext.request.contextPath}/pg/VisualizzaFileAllegato.do";
						document.location.href=href+"?"+csrfToken+"&idprg=" + idprg + "&iddocdig=" + iddocdig + "&dignomdoc=" + encodeURIComponent(dignomdoc);
					}
				</c:otherwise>
			</c:choose>
		}
	
		function insDocPredefiniti(){
			var idStipula = "${idStipula}";
			var href = "href=gare/g1stipula/g1stipula-importaDocPredefiniti.jsp?idStipula=" + idStipula;
			openPopUpCustom(href, "importaDocPredefiniti", 700, 450, "yes", "yes");
		}	
	
	
		//Apertura popup per la pubblicazione su Area Riservata portale Appalti
		function inviaAggiudicatarioContraente() {
			var idStipula = "${idStipula}";
			var idconfi = "${idconfi}";
			var href = "href=gare/g1stipula/g1stipula-PubblicaDocumenti.jsp?idStipula="+idStipula+"&idconfi="+idconfi;
			dim1 = 800;
			dim2 = 500;
			openPopUpCustom(href, "pubblicaDocumenti", dim1, dim2, "no", "yes");
		}
		
		function riportaInCompilazione() {
			var idStipula = "${idStipula}";
			var href = "href=gare/g1stipula/g1stipula-RiportaInCompilazioneDoc.jsp?idStipula="+idStipula;
				dim1 = 850;
				dim2 = 550;
			openPopUpCustom(href, "riportaInCompilazioneDoc", dim1, dim2, "no", "yes");
		}
		
		function riportaInCompilazioneOld(idDocStipula) {
			var msgConferma = "Confermi la ricompilazione del documento di stipula?";
			if(confirm(msgConferma)){
				var idStipula = "${idStipula}";
				var href = "${pageContext.request.contextPath}/pg/SetStatoDocumentoStipula.do";
				document.location.href=href+"?"+csrfToken+"&idStipula=" + idStipula + "&idDocStipula=" + idDocStipula + "&statoDocStipula=1";
			
			}else{
			  //historyVaiIndietroDi(0);
			}		
		}
		
		function modificaOrdinam(){
			var idStipula = "${idStipula}";
			var href = "href=gare/commons/popup-ModificaOrdinamentoDocStipula.jsp";
			href+="?idStipula=" + idStipula;
			openPopUpCustom(href, "modificaOrdinamG1docstipula", 850, 550, "yes", "yes");		
		}

</gene:javaScript>