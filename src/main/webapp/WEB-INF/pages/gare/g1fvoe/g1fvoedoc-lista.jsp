<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<gene:setString name="titoloMaschera" value="Documenti FVOE della ditta '${nomimp}'"/>
<c:set var="filtroPagina" value=" and iddocumento is not null"/>
<c:if test="${activePage eq '1'}">
	<c:set var="filtroPagina" value=" and idrichiesta is not null"/>
</c:if>

		<%// Creo la lista filtrando per codgar%>
		<c:set var="where" value="CODGAR = ? AND CODIMP=? ${filtroPagina}"/>

		<c:set var="parametri" value="T:${codiceGara};T:${codiceDitta}"/>
		${gene:callFunction4("it.eldasoft.gene.tags.functions.ImpostazioneFiltroFunction", pageContext, "G1FVOEDOC", where, parametri)}

		<table class="dettaglio-tab-lista">
			<c:if test="${autorizzatoModifiche ne 2 && not empty accountSSO && accountSSO.hasLoa(3)}">
				<c:set var="cfdrp" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetCfDRPFunction",  pageContext, codiceGara)}'/>
				<c:if test="${fn:toUpperCase(cfdrp) eq fn:toUpperCase(profiloUtente.codiceFiscale)}">
					<c:set var="autorizzato" value="true"/>
				</c:if>
			</c:if>
			<c:if test="${autorizzato ne 'true'}">
				<tr>
					<td>
							<br>
							<span style="color: blue"><b>ATTENZIONE: </b>Le funzionalità di accesso alla documentazione del fascicolo FVOE sono disponibili solo per il soggetto incaricato degli invii ad ANAC autenticato con LoA 3 o superiore.</span>
					</td>
				</tr>
			</c:if>
			<tr>
				<td>
					<gene:formLista entita="G1FVOEDOC" gestisciProtezioni="true" pagesize="20" tableclass="datilista" sortColumn="${activePage eq '1'? '-2':'2'}">
						<gene:redefineInsert name="listaNuovo" />
						<gene:redefineInsert name="listaEliminaSelezione" />
						<gene:redefineInsert name="addToAzioni" >
						<c:if test="${autorizzato eq 'true' && autorizzatoModifiche ne 2}">
							<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.G1FVOE-DOC.DOCRICH.richiediDocumento')}" >
								<tr>
									<td class="vocemenulaterale">
										<a href="javascript:nuovoDoc();" title='Richiedi nuovo documento' tabindex="1503">
											Richiedi nuovo documento
										</a>
									</td>
								</tr>
							</c:if>
							<c:choose>
								<c:when test="${activePage eq '1'}">
									<c:if test='${autorizzato eq "true" and autorizzatoModifiche ne 2 && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.G1FVOE-DOC.DOCRICH.verificaRicDocumento")}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:verificaRichiestaDoc();" title='Verifica richieste documenti' tabindex="1504">
													Verifica richieste documenti
												</a>
											</td>
										</tr>
									</c:if>
								</c:when>
								<c:otherwise>
									<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.G1FVOE-DOC.DOCDISP.ricercaDocumenti')}" >
									<tr>
										<td class="vocemenulaterale">
											<a href="javascript:ricercaDocumenti();" title='Ricerca documenti' tabindex="1502">
												Ricerca documenti
											</a>
										</td>
									</tr>
									</c:if>
								</c:otherwise>
							</c:choose>
						</c:if>
						</gene:redefineInsert>
                        <gene:campoLista title="Opzioni" width="25" visibile="${autorizzato eq 'true' && activePage eq '1'}">
                            <c:if test='${autorizzato eq "true" and autorizzatoModifiche ne 2 && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.G1FVOE-DOC.DOCRICH.verificaRicDocumento")}'>
                                <c:if test="${activePage eq '1' && datiRiga.G1FVOEDOC_STATORICHIESTA ne '4' && datiRiga.G1FVOEDOC_STATORICHIESTA ne '5'}">
                                    <gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
                                            <gene:PopUpItem title="Verifica richiesta documento" href="verificaRichiestaDoc('${datiRiga.G1FVOEDOC_ID}')" />
                                    </gene:PopUp>
                                </c:if>
                            </c:if>
                        </gene:campoLista>
						<gene:campoLista campo="ID" visibile="false"/>
						<gene:campoLista campo="CODICEraw" campoFittizio="true" value="${datiRiga.G1FVOEDOC_CODICE}" title="Codice" definizione="T5"/>
						<gene:campoLista campo="CODICE" visibile="false"/>
						<gene:campoLista title="Tipologia documento" campo="TAB2D2" entita="TAB2" where="TAB2.TAB2COD='A1z30' AND G1FVOEDOC.CODICE=TAB2.TAB2TIP" visibile="true"/>
						<gene:campoLista title="Ente certificante" campo="TAB2D1" entita="TAB2" where="TAB2.TAB2COD='A1z30' AND G1FVOEDOC.CODICE=TAB2.TAB2TIP" visibile="true"/>
						<gene:campoLista campo="OGGETTO" visibile="${activePage ne '1'}"/>
						<gene:campoLista campo="IDDOCUMENTO" visibile="false"/>
						<gene:campoLista campo="IDRICHIESTA" visibile="false"/>
						<gene:campoLista campo="STATORICHIESTA" visibile="${activePage eq '1'}"/>
						<gene:campoLista campo="DTRICHIESTA" visibile="${activePage eq '1'}"/>
						<gene:campoLista campo="DATIRICHIESTA" visibile="false"/>
						<c:if test="${autorizzato eq 'true'}">
							<gene:campoLista title="Dati richiesta" width="20">
							<c:if test="${not empty datiRiga.G1FVOEDOC_DATIRICHIESTA && datiRiga.G1FVOEDOC_DATIRICHIESTA.indexOf('datiRichiesta')>0}">
								<span class="tooltipDati" title='${datiRiga.G1FVOEDOC_DATIRICHIESTA}'>[...]</span>
							</c:if>
							</gene:campoLista>
						</c:if>
						<c:if test="${autorizzato eq 'true' && activePage ne '1' && gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.G1FVOE-DOC.DOCDISP.downloadDocumento')}" >
							<gene:campoLista title="&nbsp;" width="20" visibile="${autorizzato eq 'true' && activePage ne '1'}">
								<a href="javascript:scaricaFile(${datiRiga.G1FVOEDOC_ID})" title="Download file" >
								<img width="24" height="24" title="Download file" alt="Download file" src="${contextPath}/img/visualizzafile.gif"></a>
							</gene:campoLista>
						</c:if>
						<input type="hidden" name="codiceGara" value="${codiceGara}"/>
						<input type="hidden" name="codiceDitta" value="${codiceDitta}"/>
						<input type="hidden" name="autorizzatoModifiche" value="${autorizzatoModifiche}" />
					</gene:formLista>
				</td>
			</tr>
			<c:if test="${autorizzato eq 'true' &&  autorizzatoModifiche ne 2}">
			<tr>
				<td class="comandi-dettaglio"  colSpan="2">
					<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.G1FVOE-DOC.DOCRICH.richiediDocumento')}" >
						<INPUT type="button"  class="bottone-azione" value='Richiedi nuovo documento' title='Richiedi nuovo documento' onclick="javascript:nuovoDoc();">
					</c:if>
					<c:choose>
						<c:when test="${activePage eq '1'}">
							<c:if test='${autorizzato eq "true" and autorizzatoModifiche ne 2 && gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.G1FVOE-DOC.DOCRICH.verificaRicDocumento")}'>
								<INPUT type="button"  class="bottone-azione" value='Verifica richieste documenti' title='Verifica richiesta documento' onclick="javascript:verificaRichiestaDoc();">
							</c:if>
						</c:when>
						<c:otherwise>
							<c:if test="${gene:checkProt(pageContext, 'FUNZ.VIS.ALT.GARE.G1FVOE-DOC.DOCDISP.ricercaDocumenti')}" >
								<INPUT type="button"  class="bottone-azione" value='Ricerca documenti' title='Ricerca documenti' onclick="javascript:ricercaDocumenti();">
							</c:if>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			</c:if>
		</table>

		<gene:javaScript>
			function ricercaDocumenti(){
				var accesso="${accessoAutorizzato}"
				if(accesso==='false'){
					alert("La richiesta d'accesso al fascicolo non risulta essere in stato 'Autorizzato'. Provare a rieseguire la funzione 'Verifica accesso FVOE'.")
					return;
				}
				if(!confirm("Tramite la seguente funzione verranno recuperati gli estremi di tutti i documenti disponibili nel fascicolo FVOE per la ditta.\n\nVuoi procedere?")){
					return;
				}
				var codiceGara= "${codiceGara}";
				var codiceDitta= "${codiceDitta}";
				bloccoPagina = true;
				setTimeout(() => {
					$.ajax({
						type: "POST",
						dataType: "json",
						url: "pg/ScambioDatiPCP.do?metodo=gestioneAppalto",
						data: {
							codiceGara: codiceGara.replace(/^\\$/, ''),
							codgar: codiceGara,
							servizio: "RICERCA_DOCUMENTI_FVOE",
							codimp: codiceDitta
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
							else
								alert("Operazione completata.\n\nDocumenti aggiunti: "+json.occorrenzeAggiunte+"\nDocumenti aggiornati: "+json.occorrenzeAggiornate);
							ricaricaPagina();
						}
					});
				},150);
			}

            function verificaRichiestaDoc(idfvoedoc){
				var accesso="${accessoAutorizzato}"
				if(accesso==='false'){
					alert("La richiesta d'accesso al fascicolo non risulta essere in stato 'Autorizzato'. Provare a rieseguire la funzione 'Verifica accesso FVOE'.")
					return;
				}
				var confirmMsg=idfvoedoc===undefined?"ogni documento":"il documento";
				if(!confirm("Tramite la seguente funzione è possibile verificare l'elaborazione della richiesta per "+confirmMsg+".\n\nVuoi procedere?")){
					return;
				}
                var codiceGara= "${codiceGara}";
				var codiceDitta= "${codiceDitta}";
                bloccoPagina = true;
                setTimeout(() => {
                    $.ajax({
                        type: "POST",
                        dataType: "json",
                        url: "pg/ScambioDatiPCP.do?metodo=gestioneAppalto",
                        data: {
                            codiceGara: codiceGara.replace(/^\\$/, ''),
                            codgar: codiceGara,
                            servizio: "VERIFICA_RICHIESTA_DOCUM_FVOE",
                            idfvoe: idfvoedoc,
							codimp: codiceDitta
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
							else
								alert("Operazione completata.\n\nRichieste aggiornate: "+json.occorrenzeProcessate+"\nRichieste non aggiornate: "+json.occorrenzeConErrori);
                            ricaricaPagina();
                        }
                    });
                },150);
            }

			function scaricaFile(idfvoedoc){
				var accesso="${accessoAutorizzato}"
				if(accesso==='false'){
					alert("La richiesta d'accesso al fascicolo non risulta essere in stato 'Autorizzato'. Provare a rieseguire la funzione 'Verifica accesso FVOE'.")
					return;
				}

				var codiceGara = "${codiceGara}";
				var url = "pg/ScambioDatiPCP.do?metodo=gestioneAppalto&codiceGara=" + codiceGara.replace(/^\\$/, '') +
				"&codgar=" + codiceGara + "&servizio=RECUPERA_DOCUMENTO_FVOE&idfvoe=" + idfvoedoc;

				document.getElementById('bloccaScreen').style.visibility='visible';
				document.getElementById('wait').style.visibility='visible';
				$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});

				fetch(url)
				.then(response => {
					document.getElementById('bloccaScreen').style.visibility='hidden';
					document.getElementById('wait').style.visibility='hidden';
					if (!response.ok) {
						// Gestione dell'errore: mostra un alert o un messaggio sulla pagina
						return response.text().then(text => {
							alert("Errore durante il download: " + text);
						});
					}
					const fileName = response.headers.get("X-Filename");

					// Se la risposta è ok, recupera il blob per scaricare il file
					return response.blob().then(blob => {
						const downloadUrl = window.URL.createObjectURL(blob);
						const a = document.createElement("a");
						a.href = downloadUrl;
						a.download = fileName;
						document.body.appendChild(a);
						a.click();
						a.remove();
						window.URL.revokeObjectURL(downloadUrl);
					});
				})
				.catch(error => {
				// Gestione di eventuali errori di connessione
					document.getElementById('bloccaScreen').style.visibility='hidden';
					document.getElementById('wait').style.visibility='hidden';
					alert("Errore di connessione: " + error.message);
				});
			}


			function ricaricaPagina(){
				listaVaiAPagina(document.forms[0].pgCorrente.value);
			}

			function nuovoDoc(){
				var accesso="${accessoAutorizzato}"
				if(accesso==='false'){
					alert("La richiesta d'accesso al fascicolo non risulta essere in stato 'Autorizzato'. Provare a rieseguire la funzione 'Verifica accesso FVOE'.")
					return;
				}
				var href = ("href=gare/g1fvoe/richiesta-doc.jsp&codiceGara=${codiceGara}&codiceDitta=${codiceDitta}&modo=NUOVO");
				openPopUpActionCustom(null, href, "schedaNuovoDocumento", 900, 500, "yes","yes");
			}

			$(document).ready(function() {
				$('.checkbox.disabled').change(function() {
					$(this).prop('checked', true);
				})
			})

			$(function() {
				$('.tooltipDati').tooltip({
					content: function(){
						var element = $( this );
						var json = JSON.parse(element.attr('title'))
						return generateTooltipText(json.datiRichiesta);
					}
				});
			});

			function generateTooltipText(obj, depth = 0) {
				let tooltipText = '';
				for (const [key, value] of Object.entries(obj)) {
					const indent = ' '.repeat(depth * 2);
					if (typeof value === 'object' && value !== null) {
						tooltipText +=  generateTooltipText(value, depth+1); // Chiamata ricorsiva
					} else {
						tooltipText += key+": "+value+"<br>"; // Aggiungi la coppia chiave-valore
					}
				}
				return tooltipText;
			}

		</gene:javaScript>
