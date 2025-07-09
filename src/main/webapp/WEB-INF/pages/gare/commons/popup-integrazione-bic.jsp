<%
	/*
	 *
	 * Copyright (c) Maggioli S.p.A.
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

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<div style="display: none">
	<div class="contenitore-errori-custom" >
		<div class="errori-javascript-titolo">
			<div><img src="${pageContext.request.contextPath}/img/jsMsgOn.gif" alt="Messaggi">Messaggi</div>
			<div class="errori-javascript-dettaglio">

			</div>
		</div>
	</div>
</div>

<div style="width:97%;">

	<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:setString name="titoloMaschera" value='Inserimento documenti BIC da commessa' />

	<gene:redefineInsert name="corpo">
		<c:choose>
			<c:when test="${RISULTATO ne 'OK'}">
				<c:set var="tmp" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.GetSAConfigurateBICFunction", pageContext)}' scope="request"/>
				<c:if test="${param.garaLotti ne 1}">
					<c:set var="tmp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetAssociazioneGaraLavoroFunction", pageContext, param.ngara)}' />
				</c:if>
				<form action="${contextPath}/pg/LeggiDocumentiBIC.do" method="post" name="formRecuperaDocBic">
					<table class="dettaglio-notab">
						<tr>
							<td colspan="2">
								Mediante questa funzione è possibile inserire come documenti di gara le URL dei documenti degli edifici BIC di una commessa/appalto.
								<br>E’ possibile filtrare la selezione indicando la commessa (inserire il codice esatto) e l’appalto.
								<br><br>
							</td>
						</tr>
						<c:if test="${not empty listaSAConfigurate}">
							<tr>
								<td class="etichetta-dato">Stazione appaltante</td>
								<td class="valore-dato">
									<select name="sa" id="sa">
										<c:forEach items="${listaSAConfigurate}" var="item">
											<option value="${item[0]}">${item[0]} - ${item[1]} - ${item[2]}</option>
										</c:forEach>
									</select>
								</td>
							</tr>
						</c:if>
						<tr>
							<td class="etichetta-dato">Codice commessa</td>
							<td class="valore-dato">
								<input type="text" id="codiceCommessa" name="codiceCommessa" value="${clavor}"/>
							</td>
						</tr>
						<tr>
							<td class="etichetta-dato">Numero appalto</td>
							<td class="valore-dato">
								<input type="text" id="numeroAppalto" name="numeroAppalto" value="${numera}"/>
							</td>
						</tr>
						<tr><td><br></td></tr>
						<tr>
							<td colspan="2" class="comandi-dettaglio">
								<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:recuperaDocBic();">
								<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
							</td>
						</tr>
					</table>
					<input type="hidden" name="metodo" value="getLista"/>
					<input type="hidden" name="gruppo" value="${param.gruppo}"/>
					<input type="hidden" name="tipologia" value="${param.tipologia}"/>
					<input type="hidden" name="codgar" value="${param.codgar}"/>
					<input type="hidden" name="ngara" value="${param.ngara}"/>
				</form>
			</c:when>
			<c:when test="${COMPLETED ne 'OK' and RISULTATO eq 'OK'}">
				<gene:redefineInsert name="head" >
					<script type="text/javascript" src="${contextPath}/js/jquery.integrazionebic.js?v=${sessionScope.versioneModuloAttivo}"></script>

					<style>
						#mascheraCopiaDocumenti {
							display: none;
							padding: 20px;
							font-size: 11px;
							font: 11px Verdana, Arial, Helvetica, sans-serif;
						}

						TABLE.schedagperm {
							margin-top: 5px;
							margin-bottom: 5px;
							padding: 0px;
							font-size: 11px;
							border-collapse: collapse;
							border-left: 1px solid #A0AABA;
							border-top: 1px solid #A0AABA;
							border-right: 1px solid #A0AABA;
							width: 100%;
						}

						TABLE.schedagperm TR.intestazione {
							background-color: #CCE0FF;
							border-bottom: 1px solid #A0AABA;
						}

						TABLE.schedagperm TR.intestazione TD, TABLE.schedagperm TR.intestazione TH {
							padding: 5 2 5 2;
							text-align: center;
							font-weight: bold;
							border-left: 1px solid #A0AABA;
							border-right: 1px solid #A0AABA;
							border-top: 1px solid #A0AABA;
							border-bottom: 1px solid #A0AABA;
							height: 30px;
						}

						TABLE.schedagperm TR.sezione {
							background-color: #EFEFEF;
							border-bottom: 1px solid #A0AABA;
						}

						TABLE.schedagperm TR.sezione TD, TABLE.schedagperm TR.sezione TH {
							padding: 5 2 5 2;
							text-align: left;
							font-weight: bold;
							height: 25px;
						}

						TABLE.schedagperm TR {
							background-color: #FFFFFF;
						}

						TABLE.schedagperm TR TD {
							padding-left: 3px;
							padding-top: 1px;
							padding-bottom: 1px;
							padding-right: 3px;
							text-align: left;
							border-left: 1px solid #A0AABA;
							border-right: 1px solid #A0AABA;
							border-top: 1px solid #A0AABA;
							border-bottom: 1px solid #A0AABA;
							height: 25px;
							font: 11px Verdana, Arial, Helvetica, sans-serif;
						}

						TABLE.schedagperm TR.intestazione TH.codice, TABLE.schedagperm TR TD.codice {
							width: 20px;
						}

						TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.codfisc {
							width: 100px;
						}

						TABLE.schedagperm TR.intestazione TH.descr, TABLE.schedagperm TR TD.descr {
							word-break:break-all;
							width: 200px;
						}

						TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.datadescr {
							word-break:break-word;
							text-align: center;
						}
						TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.stato {
							width: 100px;
							text-align: center;
						}
						TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.ck {
							width: 50px;
							text-align: center;
						}


						img.img_titolo {
							padding-left: 8px;
							padding-right: 8px;
							width: 24px;
							height: 24px;
							vertical-align: middle;
						}

						.dataTables_length, .dataTables_filter {
							padding-bottom: 5px;
						}

						.dataTables_empty {
							padding-top: 6px;
						}

						div.tooltip {
							width: 300px;
							margin-top: 3px;
							margin-bottom:3px;
							border: 1px solid #A0AABA;
							padding: 10px;
							display: none;
							position: absolute;
							z-index: 1000;
							background-color: #F4F4F4;
						}


						input.search {
							height: 16px;
							font: 11px Verdana, Arial, Helvetica, sans-serif;
							background-color: #FFFFFF;
							color: #000000;
							vertical-align: middle;
							border: 1px #366A9B solid;
							width: 98%;
							font-style: italic;
						}

						#add {
							float: right;
						}

						#add label select{
							width:105px;
						}

					</style>
				</gene:redefineInsert>
				<form action="${contextPath}/pg/LeggiDocumentiBIC.do" method="post" name="formRecuperaDocBic">
					<table class="dettaglio-notab">
							<td><br>
								Selezionare le URL dei documenti degli edifici BIC che si vogliono rendere disponibili nei documenti di gara.
								<br><br><br>
								<div id="documentiContainer" style="margin-left:8px; width: 98%"></div>
								<br>
							</td>
						</tr>
						<tr>
							<td colspan="2" class="comandi-dettaglio">
								<c:if test="${not empty documenti.data}">
									<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:copiaSelezionati();">
								</c:if>
								<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
							</td>
						</tr>
					</table>
					<input type="hidden" name="metodo" value="setDocumgara"/>
					<input type="hidden" name="gruppo" value="${param.gruppo}"/>
					<input type="hidden" name="tipologia" value="${param.tipologia}"/>
					<input type="hidden" name="codgar" value="${param.codgar}"/>
					<input type="hidden" name="ngara" value="${param.ngara}"/>
					<input type="hidden" name="codiceCommessa" value="${param.codiceCommessa}"/>
					<input type="hidden" name="numeroAppalto" value="${param.numeroAppalto}"/>
					<input type="hidden" name="sa" value="${param.sa}"/>
				</form>

			</c:when>
			<c:when test="${COMPLETED eq 'OK'}">
				<script type="text/javascript">
					window.opener.historyReload();
					window.close();
				</script>
			</c:when>
		</c:choose>
	</gene:redefineInsert>
	<gene:javaScript>
	function recuperaDocBic() {
		document.formRecuperaDocBic.submit();
		bloccaRichiesteServer();
	}

	function annulla() {
		window.close();
	}

	var _contextPath = "${contextPath}"
	<c:if test="${not empty documenti}">
	var json = ${documenti};
	if(json.esito === false){
		$('.errori-javascript-dettaglio').append($("<ul>").html($("<li>").addClass('errori-javascript-err').text('ERRORE: ').append($("<small>").text(json.messaggio))));
		$('.errori-javascript-dettaglio').append($("<br>"));
		var $divErr = $('.contenitore-errori-custom')
		$('.contenitore-errori-arealavoro').first().html($divErr);
	}
	if(json.data){
		apriModaleLeggiDocumentiBIC(json.data)
	}
	</c:if>

	</gene:javaScript>
	</gene:template>

</div>
