<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:set var="ngara" value='${gene:getValCampo(key,"NGARA")}' />

<c:choose>
	<c:when test="${not empty gene:getValCampo(key,'NGARA')}">
		<c:set var="ngara" value="${gene:getValCampo(key,'NGARA')}" />
		<c:set var="sql" value="select codgar1 from gare where ngara=?"/>
		<c:set var="codgar" value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, ngara)}'/>
	</c:when>
	<c:when test="${not empty gene:getValCampo(key,'CODGAR')}">
		<c:set var="ngara" value="${gene:getValCampo(key,'CODGAR')}" />
		<c:set var="codgar" value="${gene:getValCampo(key,'CODGAR')}" />
		<c:set var="key" value="GARE.NGARA=T:${gene:getValCampo(key,'CODGAR')}"/>
	</c:when>
</c:choose>

<c:set var="sql" value="select elencoe from gare where ngara=?"/>
<c:set var="elencoe" value ='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GetSafeDataFromDB", pageContext, sql, ngara)}'/>

<c:set var="delpremcont" value='${gene:callFunctionN("it.eldasoft.sil.pg.tags.funzioni.GetDelpremcontFromNgaraFunction", pageContext, ngara)}' />

<c:choose>
	<c:when test='${not empty param.tabStepSelezione}'>
		<c:set var="tabStepSelezione" value='${param.tabStepSelezione}' />
	</c:when>
	<c:otherwise>
		<c:set var="tabStepSelezione" value="${tabStepSelezione}" />
		<c:if test="${empty tabStepSelezione}">
			<c:set var="tabStepSelezione" value="0" />
		</c:if>
	</c:otherwise>
</c:choose>

<c:choose>
<c:when test="${tabStepSelezione eq 0}">

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

<c:if test='${updateLista eq 1}' >
	<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
</c:if>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="ELPREMGARE-lista">

	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>	
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/controlliFormali.js"></script>	
	</gene:redefineInsert>

	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codgar}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>

	<gene:setString name="titoloMaschera" value="Selezione da elenco elementi premiali della gara ${ngara}" />

	<c:choose>
	<c:when test="${updateLista eq 0 and (empty param.metodo or param.metodo ne 'updateLista')}">
	<gene:redefineInsert name="addHistory">
		<gene:historyAdd titolo='${gene:getString(pageContext,"titoloMaschera",gene:resource("label.tags.template.lista.titolo"))}' id="selezioneDaElencoELPREM" />
	</gene:redefineInsert>
	</c:when>
	<c:otherwise>
		<gene:redefineInsert name="addHistory"/>
	</c:otherwise>
	</c:choose>

	<gene:redefineInsert name="corpo">

		<table class="lista">
		<tr><td>
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>

			<div style="margin-top: 7px; margin-bottom: 7px">
				&nbsp;<input type="radio" value="0" name="tabStepSelezione" id="radioElementi" <c:if test='${tabStepSelezione eq 0}'>checked="checked"</c:if>
					<c:if test="${updateLista eq 1}">disabled="disabled"</c:if> />
			Elementi premiali
			&nbsp;&nbsp;&nbsp;&nbsp;

			<input type="radio" value="1" name="tabStepSelezione" id="radioFascePunteggio" <c:if test='${tabStepSelezione eq 1}'>checked="checked"</c:if>
				   <c:if test="${updateLista eq 1}">disabled="disabled"</c:if> onclick="javascript:cambiaPagina(1);" />
			Fasce di punteggio e altre opzioni
			&nbsp;&nbsp;&nbsp;&nbsp;
			</div>
		</td></tr>
		<tr><td>
			<c:set var="nonUsato" value='${gene:callFunctionN("it.eldasoft.sil.pg.tags.funzioni.GetListaElementiClasseMassimaFunction", pageContext, ngara)}' />

			<c:set var="where" value="ELPREMGARE.NGARA=? and (ELPREMGARE.IDOPZIONE is null or ELPREMGARE.IDOPZIONE <> 0)"/>
			<c:set var="parametri" value="T:${ngara}"/>
			${gene:callFunction4("it.eldasoft.gene.tags.functions.ImpostazioneFiltroFunction", pageContext, "ELPREMGARE", where, parametri)}
			<gene:formLista entita="ELPREMGARE" gestisciProtezioni="true" pagesize="-1" tableclass="datilista" sortColumn="4;2" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreELPREMGARE" >
				<gene:redefineInsert name="listaNuovo"/>
				<gene:redefineInsert name="listaEliminaSelezione" />

				<gene:redefineInsert name="addToAzioni">
					<c:choose>
						<c:when test='${updateLista eq 1}'>
							<tr>
								<td class="vocemenulaterale">
									<a href="javascript:conferma();" title="Salva" tabindex="1500">
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
							<c:if test='${autorizzatoModifiche ne 2 and empty ditteInseriteInElenco}'>
								<c:choose>
									<c:when test="${empty delpremcont}">
										<c:if test='${gene:checkProtFunz(pageContext, "INS","INS")}'>
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:apriModaleAggiungiElementiPremiali();" title="Aggiungi" tabindex="1501">
														Aggiungi
													</a>
												</td>
											</tr>
										</c:if>
										<c:if test='${gene:checkProtFunz(pageContext,"MOD","MOD")}'>
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:listaApriInModifica();" title="Modifica" tabindex="1502">
														Modifica
													</a>
												</td>
											</tr>
										</c:if>
										<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
											<tr>
												<td class="vocemenulaterale">
													<a href="javascript:listaEliminaSelezione();" title="Elimina selezionati" tabindex="1503">
														Elimina selezionati
													</a>
												</td>
											</tr>
										</c:if>
										<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ELPREMGARE.calcoloPunteggi")}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:calcoloPunteggi();" title="Conferma dati e calcolo punteggi" tabindex="1500">
													Conferma dati e calcolo punteggi
												</a>
											</td>
										</tr>
										</c:if>
									</c:when>
									<c:otherwise>
										<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ELPREMGARE.annullaCalcoloPunteggi")}'>
										<tr>
											<td class="vocemenulaterale">
												<a href="javascript:annullaCalcoloPunteggi();" title="Annulla calcolo punteggi" tabindex="1500">
													Annulla calcolo punteggi
												</a>
											</td>
										</tr>
										</c:if>
									</c:otherwise>
								</c:choose>

							</c:if>
						</c:otherwise>
					</c:choose>
				</gene:redefineInsert>

				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50" visibile="${updateLista eq 0 and autorizzatoModifiche ne 2 and empty ditteInseriteInElenco and empty delpremcont
				 and (gene:checkProtFunz(pageContext, 'DEL','DEL') or gene:checkProtFunz(pageContext,'DEL','LISTADELSEL'))}">
					<c:if test='${currentRow >= 0 and (empty updateLista or updateLista eq 0) and (gene:checkProtFunz(pageContext, "DEL","DEL") or gene:checkProtFunz(pageContext,"DEL","LISTADELSEL"))
					and autorizzatoModifiche ne 2 and empty ditteInseriteInElenco and empty delpremcont}'>
						<center>
							<span ${empty datiRiga.ELPREMALBO_TIPOQUESITO ? 'style="display: none;"' : ''}>
								<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
									<gene:PopUpItem title="Elimina elemento premiale" href="javascript:listaElimina();" />
								</gene:PopUp>
							</span>
							<input type="checkbox" name="keys" value="${chiaveRiga}" id="checkboxElimina_${datiRiga.ELPREMGARE_IDQUESITO}_${datiRiga.ELPREMGARE_IDOPZIONE}"
								${datiRiga.ELPREMALBO_TIPOQUESITO eq "premrating" ? 'onclick="javascript:selezionaAltriElementiDaEliminare(\''.concat(datiRiga.ELPREMGARE_IDQUESITO).concat('\')"') : ""}
								${empty datiRiga.ELPREMALBO_TIPOQUESITO ? 'disabled="disabled"' : ''}/>
						</center>
					</c:if>
				</gene:campoLista>

				<gene:campoLista campo="ID" visibile="false" edit="${updateLista eq 1}" />
				<gene:campoLista campo="NGARA" visibile="false" />
				<gene:campoLista campo="IDQUESITO" visibile="false" />
				<gene:campoLista campo="IDOPZIONE" visibile="false" />

				<gene:campoLista campo="IDQUESITOfitt"  width="20" campoFittizio="true" definizione="N12;;;;G1IDQUE_EPE" value="${not empty datiRiga.ELPREMALBO_TIPOQUESITO?datiRiga.ELPREMGARE_IDQUESITO:''}"/>

				<gene:campoLista campo="IDQUESITO" entita="ELPREMALBO" visibile="false" where="ELPREMALBO.IDQUESITO=ELPREMGARE.IDQUESITO AND ((ELPREMALBO.IDOPZIONE is null and ELPREMGARE.IDOPZIONE is null) or ELPREMALBO.IDOPZIONE=ELPREMGARE.IDOPZIONE) AND ELPREMALBO.NGARA='${elencoe}'" />
				<gene:campoLista campo="IDOPZIONE" entita="ELPREMALBO" visibile="false" />
				<gene:campoLista campo="TIPOQUESITO" entita="ELPREMALBO" visibile="false" />
				<gene:campoLista campo="NGARA" entita="ELPREMALBO" visibile="false" />
				<gene:campoLista campo="DESCQUESITO" entita="ELPREMALBO" visibile="false" />

				<gene:campoLista campo="TITOLO" entita="ELPREMALBO" visibile="false" />
				<gene:campoLista campo="TITOLOfitt" value="${not empty datiRiga.ELPREMALBO_TIPOQUESITO?datiRiga.ELPREMALBO_TITOLO:''}" campoFittizio="true" definizione="A500;;;;G1TITOLO_EPE" tooltip="${datiRiga.ELPREMALBO_DESCQUESITO}" />

				<gene:campoLista campo="IDOPZIONE" entita="ELPREMALBO" visibile="false"/>
				<gene:campoLista campo="DESCOPZIONE" entita="ELPREMALBO" visibile="false"/>
				<gene:campoLista title="Opzioni" definizione="T500;;;G1DEOPZ_EPE" campo="DESCOPZIONEfitt" campoFittizio="true" value="${empty datiRiga.ELPREMALBO_TIPOQUESITO ? datiRiga.ELPREMALBO_IDOPZIONE.concat(' - ').concat(datiRiga.ELPREMALBO_DESCOPZIONE):''}"/>
				<gene:campoLista campo="PESOPONMIN" entita="ELPREMALBO" ordinabile="false" visibile="false" />
				<gene:campoLista campo="PESOPONMAX" entita="ELPREMALBO" ordinabile="false" visibile="false" />
				<gene:campoLista title="Forcella" width="35" ordinabile="false" visibile="true" value="${not empty datiRiga.ELPREMALBO_PESOPONMIN and not empty datiRiga.ELPREMALBO_PESOPONMAX ? datiRiga.ELPREMALBO_PESOPONMIN.concat(' - ').concat(datiRiga.ELPREMALBO_PESOPONMAX) : ''}"/>

				<gene:campoLista visibile="false">
					<c:if test="${not empty datiRiga.ELPREMGARE_IDOPZIONE}">
					<script>
						$(document).ready(function(){
							$('#colELPREMGARE_PESOPON_${currentRow+1}').attr("data-idquesito","${datiRiga.ELPREMGARE_IDQUESITO}");
							<c:if test="${updateLista eq 1}">
								$('#ELPREMGARE_PESOPON_${currentRow+1}').attr("data-idquesito","${datiRiga.ELPREMGARE_IDQUESITO}");
								$('#ELPREMGARE_PESOPON_${currentRow+1}').attr("data-limmin","${datiRiga.ELPREMALBO_PESOPONMIN}");
								$('#ELPREMGARE_PESOPON_${currentRow+1}').attr("data-limmax","${datiRiga.ELPREMALBO_PESOPONMAX}");

								activeForm.getCampo("ELPREMGARE_PESOPON_${currentRow+1}").setFnValidazione(validazionePesoPonderale);
								$('#ELPREMGARE_PESOPON_${currentRow+1}').change(function(){
									if ($(this).val() == "") {
										ricalcoloPesi();
									}
								});
							</c:if>
						});
					</script>
					</c:if>
				</gene:campoLista>

				<gene:campoLista campo="PESOPON" width="100" ordinabile="false" edit="${updateLista eq 1 and not empty datiRiga.ELPREMALBO_IDOPZIONE}" />
				<gene:campoLista campo="pesoponarr" width="100" campoFittizio="true" title="Peso riparametrato" definizione="F13.9;;;;" ordinabile="false" edit="${updateLista eq 1 and not empty datiRiga.ELPREMALBO_IDOPZIONE}" visibile="${updateLista eq 1 or modoAperturaLista eq 'modifica'}" />

			<c:choose>
				<c:when test="${updateLista eq 1}">
					<gene:campoLista campo="pesoponr" edit="${not empty datiRiga.ELPREMALBO_IDOPZIONE}" width="100" ordinabile="false" />
				</c:when>
				<c:otherwise>
					<gene:campoLista campo="pesoponr" title="Peso riparametrato" width="100" ordinabile="false" />
				</c:otherwise>
			</c:choose>

				<input type="hidden" id="ngara" name="ngara" value="${ngara}"/>
				<input type="hidden" name="tabStepSelezione" value="${tabStepSelezione}"/>
				<input type="hidden" name="numeroRighe" id="numeroRighe" value=""/>
			</gene:formLista>
		</td></tr>
			<c:if test='${updateLista eq 0}' >
				<jsp:include page="/WEB-INF/pages/gare/elpremgare/modaleAggiungiElementiPremiali.jsp">
					<jsp:param value="${elencoe}" name="elencoe"/>
					<jsp:param value="${ngara}" name="ngara"/>
				</jsp:include>
			</c:if>
		<tr>
			<td class="comandi-dettaglio"  colSpan="2">

				<c:choose>
					<c:when test="${updateLista eq 1}">
						<INPUT type="button"  class="bottone-azione" value='Salva' title='Salva' onclick="javascript:conferma();">&nbsp;
						<INPUT type="button"  class="bottone-azione" value='Annulla' title='Annulla' onclick="javascript:listaAnnullaModifica();">&nbsp;
					</c:when>
					<c:otherwise>
						<c:if test='${autorizzatoModifiche ne 2 and empty ditteInseriteInElenco}'>
							<c:choose>
								<c:when test="${empty delpremcont}">
									<c:if test='${gene:checkProtFunz(pageContext, "INS","INS")}'>
										<INPUT type="button"  class="bottone-azione" value='Aggiungi' title='Aggiungi' onclick="javascript:apriModaleAggiungiElementiPremiali();">&nbsp;
									</c:if>
									<c:if test='${gene:checkProtFunz(pageContext,"MOD","MOD")}'>
										<INPUT type="button"  class="bottone-azione" value='Modifica' title='Modifica' onclick="javascript:listaApriInModifica();">&nbsp;
									</c:if>
									<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
										<INPUT type="button"  class="bottone-azione" value='Elimina selezionati' title='Elimina selezionati' onclick="javascript:listaEliminaSelezione()">&nbsp;
									</c:if>
									<c:if test='${gene:checkProtFunz(pageContext, "INS","INS") || gene:checkProtFunz(pageContext,"MOD","MOD") || gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
									<br><br>
									</c:if>
									<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ELPREMGARE.calcoloPunteggi")}'>
										<input type="button" class="bottone-azione" value="Conferma dati e calcolo punteggi" title="Conferma dati e calcolo punteggi" onclick="javascript:calcoloPunteggi();">&nbsp;
										<br><br>
									</c:if>
								</c:when>
								<c:otherwise>
									<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ELPREMGARE.annullaCalcoloPunteggi")}'>
										<input type="button" class="bottone-azione" value="Annulla calcolo punteggi" title="Annulla calcolo punteggi" onclick="javascript:annullaCalcoloPunteggi();">&nbsp;
										<br><br>
									</c:if>
								</c:otherwise>
							</c:choose>
						</c:if>
						<INPUT type="button"  class="bottone-azione" value='Torna a elenco ditte' title='Torna a elenco ditte' onclick="javascript:historyVaiIndietroDi(1);">&nbsp;
					</c:otherwise>
				</c:choose>

			</td>
		</tr>

		</table>
		<style>

			span.pagebanner {
				margin-top: 0;
			}

			tr.rigaTotale > td {
				font-weight: bold;
			}

			td.cellaCorsivo > span {
				font-style: italic;
			}

			.valoreNon100 {
				color: red;
				font-weight: bold;
			}

		</style>

	</gene:redefineInsert>
	<gene:javaScript>

		var selezionaTuttiOld = selezionaTutti;
		selezionaTutti = function(keys) {
			$("input[id^='checkboxElimina_']").each(function(){
				if($(this).attr('disabled') == 'disabled' && $(this).prop('checked') != 'true') {
					let disabled = $(this).attr('disabled');
					$(this).attr('disabled',null);
					$(this).prop('checked','true');
					$(this).attr('disabled',disabled);
				}
			});
			selezionaTuttiOld(keys);
		}

		function selezionaAltriElementiDaEliminare(idQuesito) {
			$("input[id^='checkboxElimina_" + idQuesito + "_']").each(function(){
				if($(this).attr('id') != ('checkboxElimina_'+idQuesito+'_')) {
					let disabled = $(this).attr('disabled');
					$(this).attr('disabled',null);
					$(this).click();
					$(this).attr('disabled',disabled);
				}
			});
		}

		function inizializzaLista(){
			for(var t=1; t <= ${currentRow}+1; t++){
				$("#PESOPONARR_" + t).attr('disabled','disabled');
				$("#PESOPONARR_" + t).attr('tabindex','-1');
				$("#PESOPONARR_" + t).css('border-color','#A3A6FF');
				$("#PESOPONARR_" + t).css('border-width','1px');
				$("#PESOPONARR_" + t).css('background-color','#E0E0E0');

				$("#ELPREMGARE_PESOPONR_" + t).attr('disabled','disabled');
				$("#ELPREMGARE_PESOPONR_" + t).attr('tabindex','-1');
				$("#ELPREMGARE_PESOPONR_" + t).css('border-color','#A3A6FF');
				$("#ELPREMGARE_PESOPONR_" + t).css('border-width','1px');
				$("#ELPREMGARE_PESOPONR_" + t).css('background-color','#E0E0E0');
			}
		}

		function conferma(){
			// Riabilitazione prima del salvataggio degli input disabilitati all'utente
			//ricalcoloPesi();
			if (Number($('#pesoRiparametratoArrotondatoTotale').text()) != 100) {
				if (confirm('La somma dei pesi riparametrati non è 100, procedere comunque con il salvataggio?')) {
				} else {
					return;
				}
			}
			for(var i=1; i <= ${currentRow}+1; i++){
				$("#ELPREMGARE_PESOPONR_" + i).attr('disabled',null);
			}
			listaConferma();
		}

		function assegnaAttributoIsMax(isUpdateMode) {
			let mappaValori = new Map();
			let idColonna = isUpdateMode ? 'ELPREMGARE_PESOPON_' : 'colELPREMGARE_PESOPON_';

			$("[id^="+idColonna+"]").each(function(){
				let idQuesito = $(this).attr("data-idquesito");
				if (idQuesito != undefined) {

					let valore;
					if (isUpdateMode) {
						valore = $(this).val();
					} else {
						let valoreText = $(this).children().first().text().replace(',','.');
						valore = valoreText;
					}
					valore = Number(valore);

					if (!mappaValori.has(idQuesito)) {
						mappaValori.set(idQuesito, valore);
					} else {
						let valoreMax = mappaValori.get(idQuesito);
						if (valoreMax === undefined) {
							mappaValori.set(idQuesito, valore);
						} else {
							valoreMax = Number(valoreMax);
							if (valore > valoreMax) {
								mappaValori.set(idQuesito, valore);
							}
						}
					}
				}
			});

			$("[id^="+idColonna+"]").each(function(){
				let valore;
				if (isUpdateMode) {
					valore = $(this).val();
				} else {
					let valoreText = $(this).children().first().text().replace(',','.');
					valore = valoreText;
				}
				valore = Number(valore);
				let idQuesito = $(this).attr("data-idquesito");
				let valoreMax = mappaValori.get(idQuesito);
				if (valoreMax === undefined) {
					$(this).attr("data-ismax", "false");
				} else {
					valoreMax = Number(valoreMax);
					if (valoreMax === valore) {
						$(this).attr("data-ismax", "true");
						mappaValori.delete(idQuesito);
					} else {
						$(this).attr("data-ismax", "false");
					}
				}
			});
		}

		function calcolaSommaPunteggi(idColonna, isUpdateMode) {
			let sommaPunteggi = Number(0);
			$("[id^="+idColonna+"]").each(function(){
				let valore = getValore($(this), isUpdateMode);
				if ($(this).attr("data-ismax") == "true") {
					sommaPunteggi += Number(valore);
				}
			});
			return roundToDecimal(sommaPunteggi, 9);
        }

        function getValore($elemento, isUpdateMode) {
			let valore;
			if (isUpdateMode) {
				valore = $elemento.val();
			} else {
				let valoreText = $elemento.children().first().text().replace(',','.');
				valore = valoreText;
			}
			if (valore === '')
				return valore
			else
				return Number(valore);
        }

        function calcolaValoriRiparametrati(idColonna, sommaPunteggi, isUpdateMode) {
			let somme = {
				riparametrati: Number(0),
				riparametratiArrotondati: Number(0)
        	};

			$("[id^="+idColonna+"]").each(function(){
				let $elemento = $(this);
				let numeroRiga = $elemento.attr('id').substring($elemento.attr('id').lastIndexOf('_') + 1);
				let valore = getValore($elemento, isUpdateMode);
				let idQuesito = $elemento.attr('data-idquesito');

				let valoriCalcolati = calcolaValoriSingola(valore, sommaPunteggi);

				let ismax = $elemento.attr("data-ismax") == "true"
				if (ismax) {
					if (isUpdateMode) {
						somme.riparametrati += valoriCalcolati.riparametrato;
						somme.riparametratiArrotondati += valoriCalcolati.riparametratoArrotondato;
					} else {
						let riparametratoDaSommare = $('#colELPREMGARE_PESOPONR_' + numeroRiga).children().first().text();
						somme.riparametratiArrotondati += Number(riparametratoDaSommare.replace(/\,/g, '.'));
					}
				}
				if (isUpdateMode) {
					if (valore !== '' && (ismax || isClasseMassimaValorizzata(idColonna, idQuesito))) {
						aggiornaValoriRiga(numeroRiga, valoriCalcolati);
					}
					else if (valore === '' || (!ismax && !isClasseMassimaValorizzata(idColonna, idQuesito))) {
						aggiornaValoriRiga(numeroRiga, {riparametrato: '', riparametratoArrotondato: ''});
					}
				}
			});

			return {
				riparametrati: roundToDecimal(somme.riparametrati, 9),
				riparametratiArrotondati: roundToDecimal(somme.riparametratiArrotondati, 2)
			};
        }

		function isClasseMassimaValorizzata(idColonna, idQuesito) {
			return Number($("[id^="+idColonna+"][data-ismax='true'][data-idquesito="+idQuesito+"]").val()) > 0;
		}

        function calcolaValoriSingola(valore, sommaPunteggi) {
			let valoreCalcolato = valore / sommaPunteggi * 100;
			return {
				riparametrato: roundToDecimal(valoreCalcolato, 9),
				riparametratoArrotondato: roundToDecimal(valoreCalcolato, 2)
			};
        }

		function sbiancaTutto(idColonna) {
			$("[id^="+idColonna+"]").each(function(){
				let $elemento = $(this);
				let numeroRiga = $elemento.attr('id').substring($elemento.attr('id').lastIndexOf('_') + 1);
				aggiornaValoriRiga(numeroRiga, {riparametrato: '', riparametratoArrotondato: ''})
			});
		}

        function aggiornaValoriRiga(numeroRiga, valori) {
			let riparametrato = valori.riparametrato;
			let riparametratoArrotondato = valori.riparametratoArrotondato;
			$('#PESOPONARR_' + numeroRiga).val(riparametrato);
			$('#ELPREMGARE_PESOPONR_' + numeroRiga).val(riparametratoArrotondato);
        }

        function ricalcoloPesi() {
			let isUpdateMode = ${updateLista eq 1};
			assegnaAttributoIsMax(isUpdateMode);
			let idColonna = isUpdateMode ? 'ELPREMGARE_PESOPON_' : 'colELPREMGARE_PESOPON_';

			let sommaPunteggi = calcolaSommaPunteggi(idColonna, isUpdateMode);
			let testoSomma = "" + sommaPunteggi;
			if (${updateLista eq 0})
				testoSomma = testoSomma.replace(/\./g, ',');
			$('#pesoPonderaleTotale').text(testoSomma);

			if (sommaPunteggi === 0) {
				sbiancaTutto(idColonna);
				$("#pesoRiparametratoArrotondatoTotale").removeClass("valoreNon100");
			} else {
				let somme = calcolaValoriRiparametrati(idColonna, sommaPunteggi, isUpdateMode);
				let testoSommaRiparametrata = "" + somme.riparametratiArrotondati;
				if (${updateLista eq 0})
					testoSommaRiparametrata = testoSommaRiparametrata.replace(/\./g, ',');
				$("#pesoRiparametratoArrotondatoTotale").text(testoSommaRiparametrata);
				$("#pesoRiparametratoArrotondatoTotale").removeClass("valoreNon100");
				if (somme.riparametratiArrotondati != 100) {
					$("#pesoRiparametratoArrotondatoTotale").addClass("valoreNon100");
				}
			}
        }

		function roundToDecimal(number, decimalPlaces) {
			const factor = Math.pow(10, decimalPlaces);
			return Math.round(number * factor) / factor;
		}

		function validazionePesoPonderale(refVal, msg, obj){
			let result = false;
			let messaggio = "";
			if(checkFloat(refVal, msg, obj)){
				let numeroCifreDecimali = 9;
				let tmp = this.getValue();
				if(this.getValue().indexOf(".") >= 0){
					if(tmp.substr(tmp.indexOf(".") + 1).length > numeroCifreDecimali){
						messaggio = "Per il peso ponderale è possibile indicare al piu' "+numeroCifreDecimali+" cifre decimali";
						sbiancaCampi = false;
					}
				}
				if (tmp<0) {
					if (messaggio != "")
						messaggio += "\r\n";
					messaggio += "Il peso ponderale inserito deve essere maggiore o uguale a 0"
				}
				let pesoMinimoForcella = this.obj.getAttribute("data-limmin");
				let pesoMassimoForcella = this.obj.getAttribute("data-limmax");
				if (pesoMinimoForcella != "" && pesoMassimoForcella != "" && (Number(tmp) > Number(pesoMassimoForcella) || Number(tmp) < Number(pesoMinimoForcella))) {
					if (messaggio != "")
						messaggio += "\r\n";
					messaggio += "Il peso ponderale inserito deve essere compreso nella forcella.";
				}
			} else {
				return false;
			}
			if (messaggio != "") {
				msg.setValue(messaggio);
			} else {
				ricalcoloPesi();
				result = true;
			}
			return result;
		}

		function aggiungiRigaTotale() {
			tableId = 'tab' + activeForm.getFormName();
			let row = $('<tr>');
			if (${updateLista ne 1 and autorizzatoModifiche ne 2 and empty ditteInseriteInElenco and empty delpremcont}) {
				row.append($('<td>').text(null));
			}
			row.append($('<td>').text(null));
			row.append($('<td>').text(null));
			row.append($('<td>').html('<b>TOTALE</b>'));
			row.append($('<td>').text(null));
			row.append($('<td>').attr("id",'pesoPonderaleTotale').text(null));
			if (${updateLista eq 1}) {
				row.append($('<td>').attr("id",'pesoRiparametratoTotale').text(null));
			}
			row.append($('<td>').attr("id",'pesoRiparametratoArrotondatoTotale').text(null));
			$(`#` + tableId).children().last().append($('<br>'));
			$(`#` + tableId).children().last().append(row);
		}

		function cambiaStileRigaTotale() {
			$("#pesoPonderaleTotale").css("text-align", "right");
			$("#pesoRiparametratoArrotondatoTotale").css("text-align", "right");
		}

		function cambiaPagina(numeroPagina) {
			formSelezioneElementiPremiali.tabStepSelezione.value = numeroPagina;
			formSelezioneElementiPremiali.submit();
		}

		function calcoloPunteggi() {
			var ngara = "${ngara}";
			var href = "href=gare/elprempun/popupCalcoloELPREMPUN.jsp?ngara="+ngara;
			var dim1 = 650;
			var dim2 = 300;
			openPopUpCustom(href, "calcoloELPREMPUN", dim1, dim2, "no", "no");
		}

		function annullaCalcoloPunteggi() {
			var ngara = "${ngara}";
			var href = "href=gare/elprempun/popupCalcoloELPREMPUN.jsp?annullaCalcolo=VERO&ngara="+ngara;
			var dim1 = 650;
			var dim2 = 300;
			openPopUpCustom(href, "calcoloELPREMPUN", dim1, dim2, "no", "no");
		}

		$(document).ready(function(){
			aggiungiRigaTotale();
			if (${updateLista eq 1}) {
				inizializzaLista();
			}
			ricalcoloPesi();
			document.getElementById("numeroRighe").value = ${currentRow}+1;
			if (${updateLista eq 1}) {
				cambiaStileRigaTotale();
			}
		});

	</gene:javaScript>
</gene:template>
</c:when>
<c:otherwise>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="ELPREMGARE-fasce">

	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/controlliFormali.js"></script>
	</gene:redefineInsert>

	<jsp:include page="/WEB-INF/pages/commons/bloccaModifica-pg-lista-scheda.jsp">
		<jsp:param name="entita" value="V_GARE_TORN"/>
		<jsp:param name="inputFiltro" value="CODGAR=T:${codgar}"/>
		<jsp:param name="filtroCampoEntita" value="codgar=#CODGAR#"/>
	</jsp:include>

	<gene:setString name="titoloMaschera" value="Selezione da elenco elementi premiali della gara ${ngara}" />

	<c:if test="${empty modo}">
		<c:set var="modo" value="VISUALIZZA"/>
	</c:if>
	<c:choose>
	<c:when test="${modo eq 'VISUALIZZA' and (empty param.metodo or param.metodo ne 'update')}">
	<gene:redefineInsert name="addHistory">
		<gene:historyAdd titolo='${gene:getString(pageContext,"titoloMaschera",gene:resource("label.tags.template.lista.titolo"))}' id="selezioneDaElencoELPREM" />
	</gene:redefineInsert>
	</c:when>
	<c:otherwise>
		<gene:redefineInsert name="addHistory"/>
	</c:otherwise>
	</c:choose>

	<gene:redefineInsert name="schedaNuovo"/>
	<gene:redefineInsert name="schedaConferma"/>
	<gene:redefineInsert name="schedaAnnulla"/>
	<gene:redefineInsert name="modelliPredisposti"/>
	<gene:redefineInsert name="documentiAssociati"/>
	<gene:redefineInsert name="noteAvvisi"/>
	<c:if test="${not empty delpremcont or autorizzatoModifiche eq '2'}">
		<gene:redefineInsert name="schedaModifica"/>
	</c:if>

	<gene:redefineInsert name="addToAzioni">
		<c:if test='${modo eq "VISUALIZZA" and autorizzatoModifiche ne "2" and empty ditteInseriteInElenco}' >
			<c:choose>
				<c:when test="${empty delpremcont}">
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ELPREMGARE.calcoloPunteggi")}'>
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:calcoloPunteggi();" title="Conferma dati e calcolo punteggi" tabindex="1500">
								Conferma dati e calcolo punteggi
							</a>
						</td>
					</tr>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ELPREMGARE.annullaCalcoloPunteggi")}'>
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:annullaCalcoloPunteggi();" title="Annulla calcolo punteggi" tabindex="1500">
								Annulla calcolo punteggi
							</a>
						</td>
					</tr>
					</c:if>
				</c:otherwise>
			</c:choose>
		</c:if>
		<c:if test='${modo eq "MODIFICA"}' >
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:salvaSoglie();" title="Salva" tabindex="1500">
						Salva
					</a>
				</td>
			</tr>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:schedaAnnulla();" title="Annulla" tabindex="1501">
						Annulla
					</a>
				</td>
			</tr>
		</c:if>
	</gene:redefineInsert>

	<gene:redefineInsert name="corpo">

		<table class="dettaglio-notab">
		<tr><td style="border: 0px">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>

			<div style="margin-top: 7px; margin-bottom: 7px">
				&nbsp;<input type="radio" value="0" name="tabStepSelezione" id="radioElementi" <c:if test='${tabStepSelezione eq 0}'>checked="checked"</c:if>
						<c:if test="${modo eq 'MODIFICA'}">disabled="disabled"</c:if> onclick="javascript:cambiaPagina(0);" />
			Elementi premiali
			&nbsp;&nbsp;&nbsp;&nbsp;

			<input type="radio" value="1" name="tabStepSelezione" id="radioFascePunteggio" <c:if test='${tabStepSelezione eq 1}'>checked="checked"</c:if>
				   <c:if test="${modo eq 'MODIFICA'}">disabled="disabled"</c:if> />
			Fasce di punteggio e altre opzioni
			&nbsp;&nbsp;&nbsp;&nbsp;
			</div>
		</td></tr>
		<tr><td>
		<gene:formScheda entita="GARE" gestisciProtezioni="true" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreGARE">
			<gene:campoScheda>
				<td colspan="2"><b>Fasce di punteggio</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="NGARA" visibile="false" />
			<gene:campoScheda campo="CODGAR1" visibile="false" />
			<gene:campoScheda campo="NGARA" visibile="false"  entita="GARE1" where="GARE.NGARA=GARE1.NGARA" />
			<gene:campoScheda campo="ELPREMSINF" entita="GARE1" />
			<gene:campoScheda campo="ELPREMSINT" entita="GARE1" />
			<gene:campoScheda campo="ELPREMSMAX" title="Punteggio massimo" campoFittizio="true" modificabile="false" definizione="N3;;;;" value="100" />
			<gene:campoScheda>
				<td colspan="2"><b>Altre opzioni</b></td>
			</gene:campoScheda>
			<gene:campoScheda campo="ELPREMVAL" entita="GARE1" obbligatorio="true" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoSenzaNull" />

			<input type="hidden" id="ngara" name="ngara" value="${ngara}"/>
			<input type="hidden" name="tabStepSelezione" value="${tabStepSelezione}"/>
		</gene:formScheda>
		</td></tr>
			<tr>
				<td class="comandi-dettaglio" colspan="2">
				<c:choose>
					<c:when test="${modo eq 'MODIFICA'}">
						<INPUT type="button"  class="bottone-azione" value='Salva' title='Salva' onclick="javascript:salvaSoglie();">&nbsp;
						<INPUT type="button"  class="bottone-azione" value='Annulla' title='Annulla' onclick="javascript:schedaAnnulla();">&nbsp;
					</c:when>
					<c:otherwise>
						<c:if test='${autorizzatoModifiche ne 2 and empty ditteInseriteInElenco}'>
						<c:choose>
							<c:when test="${empty delpremcont}">
								<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD")}'>
									<input type="button" class="bottone-azione" value="Modifica" title="Modifica" onclick="javascript:schedaModifica();">&nbsp;
									<br><br>
								</c:if>
								<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ELPREMGARE.calcoloPunteggi")}'>
									<input type="button" class="bottone-azione" value="Conferma dati e calcolo punteggi" title="Conferma dati e calcolo punteggi" onclick="javascript:calcoloPunteggi();">&nbsp;
									<br><br>
								</c:if>
							</c:when>
							<c:otherwise>
								<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.ELPREMGARE.annullaCalcoloPunteggi")}'>
									<input type="button" class="bottone-azione" value="Annulla calcolo punteggi" title="Annulla calcolo punteggi" onclick="javascript:annullaCalcoloPunteggi();">&nbsp;
									<br><br>
								</c:if>
							</c:otherwise>
						</c:choose>
						</c:if>
						<INPUT type="button"  class="bottone-azione" value='Torna a elenco ditte' title='Torna a elenco ditte' onclick="javascript:historyVaiIndietroDi(1);">&nbsp;
					</c:otherwise>
				</c:choose>
				</td>
			</tr>
		</table>
	</gene:redefineInsert>

	<gene:javaScript>
		function cambiaPagina(numeroPagina) {
			formSelezioneElementiPremiali.tabStepSelezione.value = numeroPagina;
			formSelezioneElementiPremiali.submit();
		}

		function salvaSoglie() {
			sogliaInferiore = getValue('GARE1_ELPREMSINF');
			sogliaIntermedia = getValue('GARE1_ELPREMSINT');
			if ((sogliaInferiore != "" && sogliaIntermedia == "") || (sogliaInferiore == "" && sogliaIntermedia != "")) {
				alert('Inserire i valori di entrambe le soglie o nessuna');
				return;
			}
			if (sogliaInferiore != "" && sogliaIntermedia != "") {
				sogliaInferiore = Number(sogliaInferiore);
				sogliaIntermedia = Number(sogliaIntermedia);
				if (sogliaInferiore > 100 || sogliaInferiore < 0 || sogliaIntermedia > 100 || sogliaIntermedia < 0) {
					alert('I valori delle soglie devono essere compresi tra 0 e 100');
					return;
				}
				if (sogliaInferiore >= sogliaIntermedia) {
					alert('Il valore della soglia inferiore deve essere minore della soglia intermedia');
					return;
				}
			}
			schedaConferma();
		}

		function calcoloPunteggi() {
			var ngara = "${ngara}";
			var href = "href=gare/elprempun/popupCalcoloELPREMPUN.jsp?ngara="+ngara;
			var dim1 = 650;
			var dim2 = 300;
			openPopUpCustom(href, "calcoloELPREMPUN", dim1, dim2, "no", "no");
		}

		function annullaCalcoloPunteggi() {
			var ngara = "${ngara}";
			var href = "href=gare/elprempun/popupCalcoloELPREMPUN.jsp?annullaCalcolo=VERO&ngara="+ngara;
			var dim1 = 650;
			var dim2 = 300;
			openPopUpCustom(href, "calcoloELPREMPUN", dim1, dim2, "no", "no");
		}
	</gene:javaScript>

</gene:template>

</c:otherwise>
</c:choose>

<form name="formSelezioneElementiPremiali" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
	<input type="hidden" name="href" value="gare/elpremgare/elpremgare-lista.jsp" />
	<input type="hidden" name="tabStepSelezione" value="${tabStepSelezione}" />
	<input type="hidden" name="key" value="${key}" />
</form>
