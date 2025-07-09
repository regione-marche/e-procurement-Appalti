<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:choose>
	<c:when test='${not empty gene:getValCampo(key, "GARE.NGARA")}'>
		<c:set var="ngara" value='${gene:getValCampo(key, "GARE.NGARA")}' />
	</c:when>
	<c:when test='${not empty param.ngara}'>
		<c:set var="ngara" value="${param.ngara}" />
	</c:when>
	<c:otherwise>
		<c:set var="ngara" value="${ngara}" />
	</c:otherwise>
</c:choose>

<c:set var="ngara1" value="${ngara}" />
<c:set var="key" value="GARE.NGARA=T:${ngara}" />
<c:set var="genere" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction", pageContext, key)}' scope="request"/>

<c:set var="ngarapa" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select ngara from gare,torn where ngarapa=? and codgar=codgar1 and torn.iterga=10", ngara1)}' />
<c:choose>
	<c:when test="${not empty ngarapa}">
		<c:set var="ngara1"  value="${ngarapa}"/>
	</c:when>
	<c:otherwise>
		<c:set var="ngara1" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, "select ngara from gare where ngara = ?", ngara1)}' />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.codgar}'>
		<c:set var="codgar" value="${param.codgar}" />
	</c:when>
	<c:otherwise>
		<c:set var="codgar" value="${codgar}" />
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

<c:choose>
	<c:when test='${not empty param.updateLista}'>
		<c:set var="updateLista" value='${param.updateLista}' />
	</c:when>
	<c:otherwise>
		<c:set var="updateLista" value="${updateLista}" />
		<c:if test="${empty updateLista}">
			<c:set var="updateLista" value="0" />
		</c:if>
	</c:otherwise>
</c:choose>

<c:set var="genere" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipologiaGaraFunction", pageContext, key)}' scope="request" />

<c:set var="nonusato" value='${gene:callFunctionN("it.eldasoft.sil.pg.tags.funzioni.GetDpubattiFromNgaraFunction", pageContext, ngara)}' />
<c:set var="isNotPubblicato" value="${empty dpubatti}"/>

<c:set var="dicituraModifica" value="Modifica" />
<c:choose>
	<c:when test="${isNotPubblicato}">
		<c:set var="dicituraPubblicaPortale" value="Pubblica su portale Appalti" />
	</c:when>
	<c:otherwise>
		<c:set var="dicituraPubblicaPortale" value="Integra su portale Appalti" />
	</c:otherwise>
</c:choose>

<c:set var="dicituraAnnullaPubblicazioneAtti" value="Annulla pubblicazione" />

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_DITGPUB-lista">

	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codgar}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>

	<c:choose>
		<c:when test="${genere eq 1 or genere eq 3}">
			<gene:setString name="titoloMaschera" value="Pubblicazione atti e offerte del lotto ${ngara}" />
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value="Pubblicazione atti e offerte della gara ${ngara}" />
		</c:otherwise>
	</c:choose>

	<c:choose>
		<c:when test="${updateLista eq 0 and (empty param.metodo or param.metodo ne 'updateLista')}">
			<gene:redefineInsert name="addHistory">
				<gene:historyAdd titolo='${gene:getString(pageContext,"titoloMaschera",gene:resource("label.tags.template.lista.titolo"))}' id="ditgpub-lista" />
			</gene:redefineInsert>
		</c:when>
		<c:otherwise>
			<gene:redefineInsert name="addHistory"/>
		</c:otherwise>
	</c:choose>

	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easytabs.js"></script>
	</gene:redefineInsert>
	<gene:redefineInsert name="corpo">

	<c:choose>
		<c:when test="${fn:startsWith(codgar,'$')}">
			<c:set var="meruolo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMERUOLOFunction", pageContext, ngara)}' scope="request"/>
		</c:when>
		<c:otherwise>
			<c:set var="meruolo" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMERUOLOFunction", pageContext, codgar)}' scope="request"/>
		</c:otherwise>
	</c:choose>

	<c:set var="modlicg" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetMODLICGFunction",pageContext,ngara)}'/>

	<table class="dettaglio-tab-lista" style="border-top: 0px;">
	<tr>
		<td>

		<c:set var="where" value="V_DITGPUB.NGARA=?"/>
		<c:set var="parametri" value="T:${ngara1}"/>
		${gene:callFunction4("it.eldasoft.gene.tags.functions.ImpostazioneFiltroFunction", pageContext, "V_DITGPUB", where, parametri)}
		<gene:formLista entita="V_DITGPUB" gestisciProtezioni="true" pagesize="20" tableclass="datilista" sortColumn="5;7;8;12;${modlicg eq 6 or modlicg eq 17 ? '-' : ''}13;14" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreDitgPub">

			<gene:redefineInsert name="listaNuovo"/>
			<gene:redefineInsert name="listaEliminaSelezione" />

			<gene:redefineInsert name="addToAzioni">
				<c:choose>
					<c:when test='${updateLista eq 1}'>
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:listaConferma();" title="Salva" tabindex="1500">
								Salva
							</a>
						</td>
					</tr>
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:listaAnnullaModifica();" title="Annulla" tabindex="1501">
								Annulla
							</a>
						</td>
					</tr>
					</c:when>
					<c:otherwise>
					<c:if test="${autorizzatoModifiche ne '2'}">
						<c:if test='${isNotPubblicato and gene:checkProtFunz(pageContext,"MOD","MOD")}'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:listaApriInModifica();" title="${dicituraModifica}" tabindex="1500">
											${dicituraModifica}
									</a>
								</td>
							</tr>
						</c:if>
						<c:if test="${(meruolo eq '1' or meruolo eq '3')}">
							<c:if test="${gene:checkProtFunz(pageContext, 'ALT','pubblicaSuPortale')}">
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:pubblicaSuPortale();" title="${dicituraPubblicaPortale}" tabindex="1501">
											${dicituraPubblicaPortale}
										</a>
									</td>
								</tr>
							</c:if>
							<c:if test="${not isNotPubblicato and gene:checkProtFunz(pageContext, 'ALT','annullaPubblicazione')}">
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:annullaPubblicazioneAtti();" title="${dicituraAnnullaPubblicazioneAtti}" tabindex="1501">
											${dicituraAnnullaPubblicazioneAtti}
									</a>
								</td>
							</tr>
							</c:if>
						</c:if>
					</c:if>
					</c:otherwise>
				</c:choose>
			</gene:redefineInsert>

			<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
				<c:if test="${currentRow >= 0}">
					<center>
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<gene:PopUpItem title="Visualizza documenti" href="javascript:apriListaDocumenti('${datiRiga.V_DITGPUB_DITTAO}');"/>
					</gene:PopUp>
					</center>
				</c:if>
			</gene:campoLista>
			<gene:campoLista campo="ngara" visibile="false" edit="${datiRiga.V_DITGPUB_TIPORIGA ne 1 and updateLista eq 1}" />
			<gene:campoLista campo="codgar" visibile="false" edit="${datiRiga.V_DITGPUB_TIPORIGA ne 1 and updateLista eq 1}" />
			<gene:campoLista campo="dittao" visibile="false" edit="${datiRiga.V_DITGPUB_TIPORIGA ne 1 and updateLista eq 1}" />
			<gene:campoLista campo="tiporiga" visibile="false" />
			<gene:campoLista campo="descrizione" ordinabile="true" />
			<gene:campoLista campo="isaggi" width="60" visibile="true" />
			<gene:campoLista campo="STATODIT" width="90" ordinabile="true" />
			<gene:campoLista campo="LIVPUBATTI" edit="${datiRiga.V_DITGPUB_TIPORIGA ne 1 and updateLista eq 1}" ordinabile="true" />
			<gene:campoLista campo="numdoc" width="70" ordinabile="true" value="" >
				<c:if test="${datiRiga.V_DITGPUB_TIPORIGA eq 1 or datiRiga.V_DITGPUB_LIVPUBATTI eq 1}">
					${datiRiga.V_DITGPUB_NUMDOC}
				</c:if>
			</gene:campoLista>
			<gene:campoLista campo="id" visibile="false" />
			<gene:campoLista campo="STAGGI"  visibile="false" />
			<gene:campoLista campo="RIBAUO" visibile="false" />
			<gene:campoLista campo="NUMORDPL" visibile="false" />
			<gene:campoLista title="" width="20" visibile="${!(updateLista eq 1)}">
				<c:if test="${!(updateLista eq 1) and (datiRiga.V_DITGPUB_LIVPUBATTI eq 1 or datiRiga.V_DITGPUB_TIPORIGA eq 1)}">
					<a href="javascript:apriListaDocumenti('${datiRiga.V_DITGPUB_DITTAO}');">
						<img width="16" height="16" title="Documenti" alt="Documenti" src="/Appalti/img/documentazione.png">
					</a>
				</c:if>
			</gene:campoLista>
		<gene:campoLista visibile="false">
			<c:if test="${updateLista eq 1 and empty datiRiga.V_DITGPUB_LIVPUBATTI and currentRow > 0}">
				<script>
					$(document).ready(function(){
						if ($('#V_DITGPUB_LIVPUBATTI_${currentRow+1}').val() == '') {
							if (${datiRiga.V_DITGPUB_STATODIT eq 3}) {
								$('#V_DITGPUB_LIVPUBATTI_${currentRow+1}').val(3);
							} else {
								$('#V_DITGPUB_LIVPUBATTI_${currentRow+1}').val(2);
							}
						}
					});
				</script>
			</c:if>
		</gene:campoLista>
			<input type="hidden" id="ngara" name="ngara" value="${ngara}" />
			<input type="hidden" id="codgar" name="codgar" value="${codgar}" />
			<input type="hidden" name="idconfi" id="idconfi" value="${idconfi}"/>
			<input type="hidden" name="numeroRighe" id="numeroRighe" value=""/>
		</gene:formLista>

		</td>
	</tr>
	<c:if test="${autorizzatoModifiche ne '2' and (isNotPubblicato or meruolo eq '1' or meruolo eq '3')}">
		<tr>
			<td class="comandi-dettaglio" colSpan="2">
				<c:choose>
				<c:when test="${updateLista eq 1}">
					<INPUT type="button"  class="bottone-azione" value='Salva' title='Salva' onclick="javascript:listaConferma();">&nbsp;
					<INPUT type="button"  class="bottone-azione" value='Annulla' title='Annulla' onclick="javascript:listaAnnullaModifica();">&nbsp;
				</c:when>
				<c:otherwise>
				<c:if test='${isNotPubblicato and gene:checkProtFunz(pageContext, "ALT","insDaGraduatoria")}'>
					<INPUT type="button"  class="bottone-azione" value='${dicituraModifica}' title='${dicituraModifica}' onclick="javascript:listaApriInModifica()">&nbsp;
				</c:if>
				<c:if test="${(meruolo eq '1' or meruolo eq '3')}">
					<c:if test="${gene:checkProtFunz(pageContext, 'ALT','pubblicaSuPortale')}">
						<INPUT type="button"  class="bottone-azione" value='${dicituraPubblicaPortale}' title='${dicituraPubblicaPortale}' onclick="javascript:pubblicaSuPortale();">&nbsp;
					</c:if>
					<c:if test="${not isNotPubblicato and gene:checkProtFunz(pageContext, 'ALT','annullaPubblicazione')}">
						<INPUT type="button"  class="bottone-azione" value='${dicituraAnnullaPubblicazioneAtti}'
							   title='${dicituraAnnullaPubblicazioneAtti}' onclick="javascript:annullaPubblicazioneAtti();">&nbsp;
					</c:if>
				</c:if>
				</c:otherwise>
				</c:choose>
			</td>
		</tr>
	</c:if>
	</table>
	</gene:redefineInsert>

	<gene:javaScript>

		$(document).ready(function(){
			document.getElementById("numeroRighe").value = ${currentRow}+1;
			$("[id^='V_DITGPUB_LIVPUBATTI_']").each( function() {
				$(this).children().first().remove();
			});
		});

		function apriListaDocumenti(dittao) {
			var href = "href=gare/g1documatti/g1documatti-lista.jsp&dittao=" + dittao + "&ngara=${ngara}";
			document.location.href = contextPath + "/ApriPagina.do?" + href;
		}

		function pubblicaSuPortale() {
			var codgar = "${codgar}";
			var ngara = "${ngara}";
			var idconfi = "${idconfi}"; //"";
			var integrazioneWSDM = "${integrazioneWSDM}";
			var href = "href=gare/commons/popupPubblicaAttiSuPortale.jsp?codgar="+codgar+"&ngara="+ngara+"&idconfi="+idconfi+"&step=1";
			var dim1 = 650;
			var dim2 = 300;
			if(integrazioneWSDM =='1'){
				var entita="GARE";
				if(codgar.indexOf('$')!=0 )
					entita="TORN";
				href += "&entita=" + entita;
			}
			openPopUpCustom(href, "pubblicaSuPortale", dim1, dim2, "no", "yes");
		}

		function annullaPubblicazioneAtti() {
			var codgar = "${codgar}";
			var ngara = "${ngara}";
			var href = "href=gare/commons/popupAnnullaPubblicazioneAtti.jsp?codgar="+codgar+"&ngara="+ngara;
			var dim1 = 650;
			var dim2 = 300;
			openPopUpCustom(href, "pubblicaSuPortale", dim1, dim2, "no", "yes");
		}
	</gene:javaScript>
</gene:template>

