<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="codiceGara" value="${param.codiceGara}" />
<c:set var="autorizzatoModifiche" value="${param.autorizzatoModifiche}" />
<c:set var="idconfi" value="${param.idconfi}" />
<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.InizializzaUpdateListaFunction" parametro="" />
<c:set var="modelloNotificaFVOE" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsW_CONFCOMPopolataFunction",pageContext,"61")}' />
<c:set var="abilitatoInvioMailDocumentale" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.AbilitatoInvioMailDocumentaleFunction", pageContext, idconfi)}'/>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="G1FVOE-lista">

	<gene:redefineInsert name="addHistory">
		<gene:historyAdd titolo='Lista ditte per verifiche FVOE' id="listaDitteFVOE" />
	</gene:redefineInsert>

	<gene:redefineInsert name="head" >
		<script type="text/javascript">
			var _contextPath="${pageContext.request.contextPath}";
		</script>

		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
		<script type="text/javascript" src="${contextPath}/js/jquery.validate.min.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.easytabs.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.integrazionefvoe.js"></script>

		<style type="text/css">

			TABLE.dettaglio-notab TD.etichetta-search {
				width: 300px;
				PADDING-RIGHT: 10px;
				TEXT-ALIGN: right;
			}

			TABLE.dettaglio-notab INPUT.search-input {
				margin-left: 10px;
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
				width: 50px;
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

		</style>

	</gene:redefineInsert>

	<gene:setString name="titoloMaschera" value="Lista ditte per verifiche FVOE"/>

	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">

		<%// Creo la lista filtrando per codgar%>
		<c:set var="where" value="CODGAR = ? AND ISARCHI is null AND ISATTIVO='1'" />
		<c:set var="parametri" value="T:${codiceGara}"/>
		${gene:callFunction4("it.eldasoft.gene.tags.functions.ImpostazioneFiltroFunction", pageContext, "G1FVOE", where, parametri)}

		<table class="lista">
			<tr>
				<td>
					<c:if test="${autorizzatoModifiche ne 2 && not empty accountSSO && accountSSO.hasLoa(3)}">
						<c:set var="cfdrp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCfDRPFunction",  pageContext, codiceGara)}'/>
						<c:if test="${fn:toUpperCase(cfdrp) eq fn:toUpperCase(profiloUtente.codiceFiscale)}">
							<c:set var="autorizzato" value="true"/>
						</c:if>
					</c:if>
					<c:if test="${autorizzato ne 'true'}">
						<br>
						<span style="color: blue"><b>ATTENZIONE: </b>Le funzionalità di accesso al fascicolo FVOE sono disponibili solo per il soggetto incaricato degli invii ad ANAC autenticato con LoA 3 o superiore.</span>
					</c:if>
				</td>
			</tr>
			<tr>
				<td>
					<gene:formLista entita="G1FVOE" gestisciProtezioni="true" pagesize="20" tableclass="datilista" sortColumn="3;2" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreG1FVOE">
						<gene:redefineInsert name="listaNuovo" />
						<gene:redefineInsert name="listaEliminaSelezione" />
						<gene:redefineInsert name="addToAzioni" >
							<c:choose>
								<c:when test='${updateLista eq 1 }'>
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:listaConferma();" title="Salva modifiche" tabindex="1500">
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
										<c:if test="${autorizzatoModifiche ne 2 && gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.G1FVOE-lista.aggiungiDitte')}">
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:aggiungiDitte();" title='Aggiungi ditte' tabindex="1500">
														Aggiungi ditte
													</a>
												</td>
											</tr>
										</c:if>
										<c:if test="${autorizzatoModifiche ne 2 && autorizzato eq 'true'}">
											<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.G1FVOE-lista.modificaStatoVerifiche')}">
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:listaApriInModifica();" title='Modifica stato verifiche' tabindex="1501">
														Modifica stato verifiche
													</a>
												</td>
											</tr>
											</c:if>
											<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.G1FVOE-lista.richiediAccesso')}">
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:richiediAccesso();" title='Richiedi accesso FVOE' tabindex="1502">
														Richiedi accesso FVOE
													</a>
												</td>
											</tr>
											</c:if>
											<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.G1FVOE-lista.verificaAccesso')}">
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:verificaAccesso();" title='Verifica accesso FVOE' tabindex="1503">
														Verifica accesso FVOE
													</a>
												</td>
											</tr>
											</c:if>
										</c:if>
									</c:otherwise>
								</c:choose>
							</gene:redefineInsert>
						<gene:campoLista title="Opzioni" width="50">
							<c:if test='${updateLista ne 1 and autorizzatoModifiche ne 2}'>
								<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
									<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.G1FVOE-lista.visualizzaDocumenti')}" >
										<gene:PopUpItem title="Visualizza documenti" href="visualizzaDocumentiOE('${datiRiga.G1FVOE_CODGAR}','${datiRiga.G1FVOE_CODIMP}')" />
									</c:if>
									<c:if test="${autorizzatoModifiche ne 2 && gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.G1FVOE-lista.eliminaDitta') && false}" >
										<gene:PopUpItem title="Elimina ditta" href="ruolo=${datiRiga.G1FVOE_RUOLO};nomimo=`${datiRiga.DITG_NOMIMO}`;eliminaDitta()" />
									</c:if>
									<c:if test="${datiRiga.G1FVOE_STATORICHIESTA eq '1'}" >
										<c:set var="chiave" value="G1FVOE.CODGAR:${datiRiga.G1FVOE_CODGAR};G1FVOE.CODIMP:${datiRiga.G1FVOE_CODIMP};"/>
										<c:set var="inviaComunicazioneAbilitato" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetAbilitazioneInvioComunicazioneFunction",  pageContext, chiave, abilitatoInvioMailDocumentale)}' />
										<gene:PopUpItem title="Invia notifica richiesta accesso" href="inviaComunicazioneFVOE('${datiRiga.G1FVOE_CODGAR}','${datiRiga.G1FVOE_CODIMP}','${modelloNotificaFVOE}','${numeroModello}','${idconfi}','${inviaComunicazioneAbilitato}')" />
									</c:if>
									<c:if test="${autorizzato eq 'true' and (datiRiga.G1FVOE_STATORICHIESTA ne '1' && datiRiga.G1FVOE_STATORICHIESTA ne '2') and datiRiga.G1FVOE_ISATTIVO eq '1' && gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.G1FVOE-lista.richiediAccesso')}">
										<gene:PopUpItem title="Richiedi accesso FVOE" href="richiediAccesso('${datiRiga.G1FVOE_ID}')" />
									</c:if>
									<c:if test="${autorizzato eq 'true' and not empty datiRiga.G1FVOE_STATORICHIESTA  and datiRiga.G1FVOE_ISATTIVO eq '1' && gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.G1FVOE-lista.verificaAccesso')}">
										<gene:PopUpItem title="Verifica accesso FVOE" href="verificaAccesso('${datiRiga.G1FVOE_ID}')" />
									</c:if>
								</gene:PopUp>
							</c:if>
						</gene:campoLista>
						<% // Campi veri e propri %>

						<gene:campoLista campo="ID" visibile="false"/>
						<gene:campoLista campo="DITTAO" visibile="false" headerClass="sortable" />
						<gene:campoLista campo="NOMIMO" entita="DITG" where="DITG.DITTAO=G1FVOE.DITTAO" visibile="false" headerClass="sortable" />
						<gene:campoLista campo="CODIMP" visibile="false"/>
						<c:set var="link" value='javascript:archivioImpresa("${datiRiga.G1FVOE_CODIMP}");' />
						<gene:campoLista campo="NOMEST" entita="IMPR" where="IMPR.CODIMP=G1FVOE.CODIMP" headerClass="sortable" href='${gene:if(gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda") && updateLista ne 1, link, "")}'/>
						<gene:campoLista campo="CFIMP" entita="IMPR" where="IMPR.CODIMP=G1FVOE.CODIMP" headerClass="sortable"/>
						<gene:campoLista campo="RUOLI" entita="V_G1FVOE_RUOLI" where="V_G1FVOE_RUOLI.CODIMP=G1FVOE.CODIMP AND V_G1FVOE_RUOLI.CODGAR=G1FVOE.CODGAR" headerClass="sortable"/>
						<gene:campoLista campo="STATORICHIESTA" headerClass="sortable"/>
						<gene:campoLista campo="ISATTIVO" visibile="false"/>
						<gene:campoLista campo="STATOVERIFICHE" headerClass="sortable" edit="${updateLista eq 1 && datiRiga.G1FVOE_ISATTIVO eq '1'}"><c:if test="${datiRiga.G1FVOE_ISATTIVO ne '1'}">&nbsp;(ditta duplicata)</c:if></gene:campoLista>
						<gene:campoLista campo="LOTTI" title="Lotti" campoFittizio="true" definizione="T15" width="50" value="TODO" visibile="false"/>

						<gene:campoLista campo="CODGAR" visibile="false" edit="${updateLista eq 1 && datiRiga.G1FVOE_ISATTIVO eq '1'}" />
						<gene:campoLista campo="CODIMP"  visibile="false" edit="${updateLista eq 1 && datiRiga.G1FVOE_ISATTIVO eq '1'}" />

						<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.G1FVOE-lista.visualizzaDocumenti')}">
							<gene:campoLista title="&nbsp;" width="20" >
								<a href="javascript:chiaveRiga='${chiaveRigaJava}';visualizzaDocumentiOE('${datiRiga.G1FVOE_CODGAR}','${datiRiga.G1FVOE_CODIMP}')" title="Verifica documenti richiesti" >
									<img width="16" height="16" title="Visualizza documenti" alt="Visualizza documenti" src="${pageContext.request.contextPath}/img/documentazione.png"/></a>
							</gene:campoLista>
						</c:if>
						<input type="hidden" name="codiceGara" value="${codiceGara}"/>
						<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche}" />
						<input type="hidden" id="idconfi" name="idconfi" value="${idconfi}"/>
					 	<input type="hidden" id="numeroDitte" name="numeroDitte" value=""/>
					</gene:formLista>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<c:choose>
						<c:when test="${updateLista eq '1'}">
							<INPUT type="button"  class="bottone-azione" value='Salva' title='Salva' onclick="javascript:listaConferma();">
							<INPUT type="button"  class="bottone-azione" value='Annulla' title='Annulla' onclick="javascript:listaAnnullaModifica();">
							&nbsp;
						</c:when>
						<c:otherwise>
							<c:if test="${autorizzato eq 'true' && autorizzatoModifiche ne 2 && gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.G1FVOE-lista.modificaStatoVerifiche')}">
								<INPUT type="button"  class="bottone-azione" value='Modifica stato verifiche' title='Modifica stato verifiche' onclick="javascript:listaApriInModifica();">
							</c:if>
							<c:if test="${autorizzatoModifiche ne 2 && gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.G1FVOE-lista.aggiungiDitte')}">
								<INPUT type="button"  class="bottone-azione" value='Aggiungi ditte' title='Aggiungi ditte' onclick="javascript:aggiungiDitte();">
							</c:if>
						</c:otherwise>
					</c:choose>

				</td>
			</tr>
		</table>

		<gene:javaScript>
			var ruolo = "";
			var nomimo="";
			document.getElementById("numeroDitte").value = ${currentRow}+1;
			var inizializzata = false;

			function aggiungiDitte(){
				var codiceGara = "${codiceGara}";
				if(!inizializzata){
					_creaFinestraSelDitte(codiceGara);
					_creaContainerListaDitte(codiceGara);
				}
				inizializzata=true;
				$("#finestraselditte").dialog("open");
			};

			/*function eliminaDitta(){
				if(ruolo===2 || ruolo===11 || ruolo===4)
					alert(`Non è possibile procedere all'eliminazione.\nLa ditta selezionata non partecipa alla gara direttamente ma come mandante, consorziata esecutrice o ausiliaria di una ditta in gara. Per eliminarla deve essere eliminata la ditta '`+nomimo+`' a cui è legata.`)
				else if(confirm("Verranno eliminate anche le sue eventuali mandanti, consorziate esecutrici o ausiliarie.\n\nProcedere con l'eliminazione?")){
					listaEliminaPopUp();
				}
			}*/

			function richiediAccesso(idfvoe){
				var confirmMsg=idfvoe===undefined?"per tutte le ditte che non hanno ancora una richiesta aperta":"per la ditta";
				if(!confirm("Tramite la seguente funzione si richiederà l'accesso al fascicolo FVOE "+confirmMsg+".\n\nVuoi procedere?")){
					return;
				}
				var codiceGara= "${codiceGara}";
				bloccoPagina = true;
				setTimeout(() => {
					$.ajax({
						type: "POST",
						dataType: "json",
						url: "pg/ScambioDatiPCP.do?metodo=gestioneAppalto",
						data: {
							codiceGara: codiceGara.replace(/^\\$/, ''),
							codgar: codiceGara,
							servizio: "RICHIESTA_ACCESSO_FVOE",
							idfvoe: idfvoe
						},
						async: false,
						beforeSend: function() {
							bloccaRichiesteServer();
						},
						success: function(json) {
							if(json.done!=='Y'){
								if(json.anacErrors)
									alert("Errore "+json.anacErrors[0].codice+" - "+json.anacErrors[0].dettaglio+"\n\nGovwayTransactionId: "+json.govwayTransactionId);
								else if(json.internalErrors)
									alert("Errore "+json.internalErrors[0]);
								else
									alert("Errore: "+json.pgErrors);
							}
							else{
								var msg = "Operazione completata.\n\nRichieste di accesso inviate: "+json.occorrenzeProcessate;
								if(json.occorrenzeProcessate===0)
									msg="Nessuna ditta per cui richiedere l'autorizzazione";
								alert(msg);
							}
							ricaricaPagina();
						}
					});
				},150);
			}

			function verificaAccesso(idfvoe){
				if(!confirm("Tramite la seguente funzione si può verificare lo stato di accesso al fascicolo FVOE.\n\nVuoi procedere?")){
					return;
				}
				var codiceGara= "${codiceGara}";
				bloccoPagina = true;
				setTimeout(() => {
					$.ajax({
						type: "POST",
						dataType: "json",
						url: "pg/ScambioDatiPCP.do?metodo=gestioneAppalto",
						data: {
							codiceGara: codiceGara.replace(/^\\$/, ''),
							codgar: codiceGara,
							servizio: "VERIFICA_RICHIESTA_ACCESSO_FVOE",
							idfvoe: idfvoe
						},
						async: false,
						beforeSend: function() {
							bloccaRichiesteServer();
						},
						success: function(json) {
							if(json.done!=='Y'){
								if(json.anacErrors)
									alert("Errore "+json.anacErrors[0].codice+" - "+json.anacErrors[0].dettaglio+"\n\nGovwayTransactionId: "+json.govwayTransactionId);
								else if(json.internalErrors)
									alert("Errore "+json.internalErrors[0]);
								else
									alert("Errore: "+json.pgErrors);
							}
							else{
								var msg = "Operazione completata.\n\nRichieste di accesso aggiornate: "+json.occorrenzeProcessate+"\nRichieste di accesso non aggiornate: "+json.occorrenzeConErrori
								if(json.occorrenzeProcessate===0)
								msg="Nessuna richiesta da aggiornare";
								alert(msg);
							}
							ricaricaPagina();
						}
					});
				},150);
			}

			function visualizzaDocumentiOE(codiceGara, codiceDitta){
				document.formVisualizzaFVOEDOC.codiceGara.value=codiceGara;
				document.formVisualizzaFVOEDOC.codiceDitta.value=codiceDitta;
				document.formVisualizzaFVOEDOC.submit();
			}


			function ricaricaPagina(){
				listaVaiAPagina(document.forms[0].pgCorrente.value);
			}

			function archivioImpresa(codiceImpresa){
				var href = ("href=gene/impr/impr-scheda.jsp&key=IMPR.CODIMP=T:" + codiceImpresa);
				document.location.href = contextPath + "/ApriPagina.do?" + href;
			}

			function inviaComunicazioneFVOE(codiceGara,codiceDitta,modelloNotificaFVOE,numeroModello,idconfi,inviaComunicazioneAbilitato){
				if(modelloNotificaFVOE != "true"){
					alert("Non e' configurato il modello per la notifica di richiesta d'accesso ad FVOE");
					return;
				}else if(inviaComunicazioneAbilitato=="NoPec"){
						alert("Non e' possibile procedere.\nLa ditta selezionata non ha un indirizzo PEC specificato in anagrafica");
						return;
				}else if(inviaComunicazioneAbilitato=="NoMail"){
					alert("Non e' possibile procedere.\nLa ditta selezionata non ha un indirizzo PEC o E-mail specificato in anagrafica");
					return;
				}else{
					var numeroGara=codiceGara.indexOf('$')===0?codiceGara.substring(1):codiceGara;
					var href = contextPath + "/Lista.do?numModello=" + numeroModello + "&keyAdd=W_INVCOM.COMKEY1=T:" + numeroGara ;
					href += "&keyParent=GARE.NGARA=T:" + numeroGara;
					href += "&ditta=" + codiceDitta+ "&metodo=nuovo&entita=W_INVCOM&jspPathTo=/WEB-INF/geneweb/w_invcom/w_invcom-scheda.jsp";
					var entitaWSDM="GARE";
					var chiaveWSDM=numeroGara;
					href+="&entitaWSDM=" + entitaWSDM + "&chiaveWSDM=" + chiaveWSDM;
					if(idconfi){
						href+="&idconfi=" + idconfi;
					}
					document.location.href = href;
				}
			}

		</gene:javaScript>

		<form name="formVisualizzaFVOEDOC" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
			<input type="hidden" name="href" value="gare/g1fvoe/g1fvoedoc-scheda.jsp" />
			<input type="hidden" name="codiceGara" value="" />
			<input type="hidden" name="codiceDitta" value="" />
			<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche }" />
		</form>
	</gene:redefineInsert>
</gene:template>
