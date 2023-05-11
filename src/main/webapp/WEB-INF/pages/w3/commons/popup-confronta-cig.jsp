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

<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:setString name="titoloMaschera" value='Confronto dati' />
	
	<gene:redefineInsert name="corpo">
		<div>
			<br>
			<img src="\Appalti\img\collapse.gif" class="expandContent"/><b>&nbsp;Anagrafica gara</b> 
			<hr style="display:none">
			<div class="collapsable">
				<table class="dettaglio-notab">
					<tr>
						<td class="valore-dato" style="width:30%"><center><b>Descrizione del campo</b></center></td>
						<td class="valore-dato" style="width:35%"><center><b>Dati locali</b></center></td>
						<td class="valore-dato" style="width:35%"><center><b>Dati in SIMOG</b></center></td>
					</tr> 
				<c:if test='${not empty listaCampiAnagraficaGara}'>			
					<c:forEach var="rigaGara" items="${listaCampiAnagraficaGara}" varStatus="stato">
					<tr id="rowAnagraficaGara${stato.count}">
						<td class="etichetta-dato">
							<span style="padding-left:5px">${rigaGara[2]}</span>
						</td>
						<td class="valore-dato">
							<span style="padding-left:5px;float:left">${rigaGara[3]}</span>
						</td>
						<td class="valore-dato">
							<span style="padding-left:5px;float:left">${rigaGara[4]}</span>
						</td>
					</tr>
					</c:forEach>
				</c:if>
				<c:if test='${not empty listaCategorieMerceologicheVigilanza or not empty listaCategorieMerceologicheSIMOG}'>
					<tr id="rowAnagraficaGara_catMerc">
						<td class="etichetta-dato">
							<b>Categorie Merceologiche</b> 
						</td>
						<td class="valore-dato">
							<span style="padding-left:5px;float:left">
								<c:forEach var="riga" items="${listaCategorieMerceologicheVigilanza}" varStatus="stato">
									<c:if test='${stato.first}'><ul style="margin-top:5px;padding-left:20px" ></c:if>
									<li>${riga}</li>
									<c:if test='${stato.last}'></ul></c:if>
								</c:forEach>	
							</span>	
						</td>
						<td class="valore-dato">
							<span style="padding-left:5px;float:left">
								<c:forEach var="riga" items="${listaCategorieMerceologicheSIMOG}" varStatus="stato">
									<c:if test='${stato.first}'><ul style="margin-top:5px;padding-left:20px"></c:if>
									<li>${riga}</li>
									<c:if test='${stato.last}'></ul></c:if>
								</c:forEach>
							</span>
						</td>
					</tr>
				</c:if>
				</table>
			</div>
		</div>
		<div>
			<br>
			<img src="\Appalti\img\collapse.gif" class="expandContent"/><b>&nbsp;Anagrafica lotto</b>
			<hr style="display:none">
			<div class="collapsable">
				<table class="dettaglio-notab">
					<tr>
						<td class="valore-dato" style="width:30%"><center><b>Descrizione del campo</b></center></td>
						<td class="valore-dato" style="width:35%"><center><b>Dati locali</b></center></td>
						<td class="valore-dato" style="width:35%"><center><b>Dati in SIMOG</b></center></td>
					</tr> 
				<c:if test='${not empty listaCampiAnagraficaLotto}'>
					<c:forEach var="rigaLotto" items="${listaCampiAnagraficaLotto}" varStatus="stato">
					<tr id="rowAnagraficaLotto${stato.count}" >
						<td class="etichetta-dato">
							<span style="padding-left:5px">${rigaLotto[2]}</span>
						</td>
						<td class="valore-dato">
							<span style="padding-left:5px;float:left">${rigaLotto[3]}</span>
						</td>
						<td class="valore-dato">
							<span style="padding-left:5px;float:left">${rigaLotto[4]}</span>
						</td>
					</tr>
					</c:forEach>
				</c:if>
				<c:if test='${not empty listaUlterioriCategorieVigilanza or not empty listaUlterioriCategorieSIMOG}'>
					<tr id="rowAnagraficaLotto_ultCat">
						<td class="etichetta-dato">
							<b>Ulteriori categorie</b>
						</td>
						<td class="valore-dato">
							<span style="padding-left:5px;float:left">
								<c:forEach var="riga" items="${listaUlterioriCategorieVigilanza}" varStatus="stato">
									<c:if test='${stato.first}'><ul style="margin-top:5px;padding-left:20px"></c:if>
									<li>${riga}</li>
									<c:if test='${stato.last}'></ul></c:if>
								</c:forEach>
							</span>
						</td>
						<td class="valore-dato">
							<span style="padding-left:5px;float:left">
								<c:forEach var="riga" items="${listaUlterioriCategorieSIMOG}" varStatus="stato">
									<c:if test='${stato.first}'><ul style="margin-top:5px;padding-left:20px"></c:if>
									<li>${riga}</li>
									<c:if test='${stato.last}'></ul></c:if>
								</c:forEach>
							</span>
						</td>
					</tr>
				</c:if>
				<c:if test='${not empty listaTipiAppaltoVigilanza or not empty listaTipiAppaltoSIMOG}'>
					<tr id="rowAnagraficaLotto_tipoApp">
						<td class="etichetta-dato" >
							<b>Tipologia Appalto</b>
						</td>
						<td class="valore-dato">
							<span style="padding-left:5px;float:left">
								<c:forEach var="rigaVigilanza" items="${listaTipiAppaltoVigilanza}" varStatus="stato">
									<c:if test='${stato.first}'><ul style="margin-top:5px;padding-left:20px"></c:if>
									<li>${rigaVigilanza}</li>
									<c:if test='${stato.last}'></ul></c:if>
								</c:forEach>
							</span>					
						</td>
						<td class="valore-dato">
							<span style="padding-left:5px;float:left">
								<c:forEach var="rigaSIMOG" items="${listaTipiAppaltoSIMOG}" varStatus="stato">
									<c:if test='${stato.first}'><ul style="margin-top:5px;padding-left:20px"></c:if>
									<li>${rigaSIMOG}</li>
									<c:if test='${stato.last}'></ul></c:if>
								</c:forEach>
							</span>
						</td>
					</tr>
				</c:if>
				<c:if test='${not empty listaCondizioniVigilanza or not empty listaCondizioniSIMOG}'>
					<tr id="rowAnagraficaLotto_cond">
						<td class="etichetta-dato">
							<b>Condizioni</b>
						</td>
						<td class="valore-dato">
							<span style="padding-left:5px;float:left">
								<c:forEach var="rigaCondizione" items="${listaCondizioniVigilanza}" varStatus="stato">
									<c:if test='${stato.first}'><ul style="margin-top:5px;padding-left:20px"></c:if>
									<li>${rigaCondizione}</li>
									<c:if test='${stato.last}'></ul></c:if>
								</c:forEach>
							</span>					
						</td>
						<td class="valore-dato">
							<span style="padding-left:5px;float:left">
								<c:forEach var="rigaCondizione" items="${listaCondizioniSIMOG}" varStatus="stato">
									<c:if test='${stato.first}'><ul style="margin-top:5px;padding-left:20px"></c:if>
									<li>${rigaCondizione}</li>
									<c:if test='${stato.last}'></ul></c:if>
								</c:forEach>
							</span>
						</td>
					</tr>
				</c:if>
				<c:if test='${not empty listaCPVSecondariVigilanza or not empty listaCPVSecondariSIMOG}'>
					<tr id="rowAnagraficaLotto_cpvSec">
						<td class="etichetta-dato">
							<b>Cpv secondari</b>
						</td>
						<td class="valore-dato">
							<span style="padding-left:5px;float:left">
								<c:forEach var="rigacpvsecondari" items="${listaCPVSecondariVigilanza}" varStatus="stato">
									<c:if test='${stato.first}'><ul style="margin-top:5px;padding-left:20px"></c:if>
									<li>${rigacpvsecondari}</li>
									<c:if test='${stato.last}'></ul></c:if>
								</c:forEach>
							</span>		
						</td>
						<td class="valore-dato">
							<span style="padding-left:5px;float:left">
								<c:forEach var="rigacpvsecondari" items="${listaCPVSecondariSIMOG}" varStatus="stato">
									<c:if test='${stato.first}'><ul style="margin-top:5px;padding-left:20px"></c:if>
									<li>${rigacpvsecondari}</li>
									<c:if test='${stato.last}'></ul></c:if>
								</c:forEach>
							</span>
						</td>
					</tr>
				</c:if>
				<c:if test='${not empty listaMotiviDerogaVigilanza or not empty listaMotiviDerogaSIMOG}'>
					<tr id="rowAnagraficaLotto_cpvSec">
						<td class="etichetta-dato">
							<b>Lista dei motivi deroga</b>
						</td>
						<td class="valore-dato">
							<span style="padding-left:5px;float:left">
								<c:forEach var="rigamotivideroga" items="${listaMotiviDerogaVigilanza}" varStatus="stato">
									<c:if test='${stato.first}'><ul style="margin-top:5px;padding-left:20px"></c:if>
									<li>${rigamotivideroga}</li>
									<c:if test='${stato.last}'></ul></c:if>
								</c:forEach>
							</span>		
						</td>
						<td class="valore-dato">
							<span style="padding-left:5px;float:left">
								<c:forEach var="rigamotivideroga" items="${listaMotiviDerogaSIMOG}" varStatus="stato">
									<c:if test='${stato.first}'><ul style="margin-top:5px;padding-left:20px"></c:if>
									<li>${rigamotivideroga}</li>
									<c:if test='${stato.last}'></ul></c:if>
								</c:forEach>
							</span>
						</td>
					</tr>
				</c:if>
				<c:if test='${not empty listaMisurePremialiVigilanza or not empty listaMisurePremialiSIMOG}'>
					<tr id="rowAnagraficaLotto_cpvSec">
						<td class="etichetta-dato">
							<b>Lista misure premiali</b>
						</td>
						<td class="valore-dato">
							<span style="padding-left:5px;float:left">
								<c:forEach var="rigamisurepremiali" items="${listaMisurePremialiVigilanza}" varStatus="stato">
									<c:if test='${stato.first}'><ul style="margin-top:5px;padding-left:20px"></c:if>
									<li>${rigamisurepremiali}</li>
									<c:if test='${stato.last}'></ul></c:if>
								</c:forEach>
							</span>		
						</td>
						<td class="valore-dato">
							<span style="padding-left:5px;float:left">
								<c:forEach var="rigamisurepremiali" items="${listaMisurePremialiSIMOG}" varStatus="stato">
									<c:if test='${stato.first}'><ul style="margin-top:5px;padding-left:20px"></c:if>
									<li>${rigamisurepremiali}</li>
									<c:if test='${stato.last}'></ul></c:if>
								</c:forEach>
							</span>
						</td>
					</tr>
				</c:if>
				</table>
			</div>
		</div>
		<c:if test='${not empty listaConfrontoDatiRequisiti}'>
			<div>
				<br>
				<img src="\Appalti\img\collapse.gif" class="expandContent"/><b>&nbsp;Requisiti</b>
				<hr style="display:none">
				<div class="collapsable">
					<table class="dettaglio-notab">
						<tr>
							<td class="valore-dato" style="width:30%"><center><b>Descrizione del campo</b></center></td>
							<td class="valore-dato" style="width:35%"><center><b>Dati locali</b></center></td>
							<td class="valore-dato" style="width:35%"><center><b>Dati in SIMOG</b></center></td>
						</tr> 
					<c:forEach var="requisito" items="${listaConfrontoDatiRequisiti}" varStatus="stato">
						<c:forEach var="rigaRequisito" items="${requisito.value}" varStatus="row">
							<tr id="rowRequisito${row.count}_requisito${stato.count}">
								<td class="etichetta-dato">
									<span style="padding-left:5px;">${rigaRequisito[2]}</span>
								</td>
							<c:if test='${row.first}'>
								<td class="valore-dato">
									<c:if test="${not empty rigaRequisito[3]}"><span style="padding-left:5px;float:left">${requisito.key}</span></c:if>
								</td>
								<td class="valore-dato">
									<c:if test="${not empty rigaRequisito[4]}"><span style="padding-left:5px;float:left">${requisito.key}</span></c:if>
								</td>
							</c:if>
							<c:if test='${not row.first}'>
								<td class="valore-dato" >
									<span style="padding-left:5px;float:left">${rigaRequisito[3]}</span>
								</td>
								<td class="valore-dato" >
									<span style="padding-left:5px;float:left">${rigaRequisito[4]}</span>
								</td>
							</c:if>
							</tr>
						</c:forEach>
							<tr>
								<td class="valore-dato">&nbsp;</td>
								<td class="valore-dato">&nbsp;</td>
								<td class="valore-dato">&nbsp;</td>
							</tr>
					</c:forEach>
					</table>
				</div>
			</div>
		</c:if>

		
		<form action="${contextPath}/w3/ConfrontaCIG.do" method="post" name="formIDGARACIG">

			<input type="hidden" name="numgara" value="${param.numgara}" />
			<input type="hidden" name="numlott" value="${param.numlott}" />
			<input type="hidden" name="cig"    value="${param.cig}" />
			<input type="hidden" name="codrup" value="${param.codrup}" />
			<input type="hidden" name="metodo" value="riallineaDati" />
			
			<table class="dettaglio-notab">
				<tr>
					<td colspan="2" class="comandi-dettaglio">
						<INPUT type="button" class="bottone-azione" value="Riallinea dati a SIMOG" title="Riallinea dati a SIMOG" onclick="javascript:riallineaDati();">
						<INPUT type="button" class="bottone-azione" value="Annulla"	title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
				</tr>
			</table>
		</form>	

	</gene:redefineInsert>
	
	<gene:javaScript>
		document.forms[0].jspPathTo.value="w3/commons/popup-confronta-cig.jsp";
		
		function annulla() {
			window.close();
		}
		
		function riallineaDati() {
			document.formIDGARACIG.submit();
			bloccaRichiesteServer();
		}
		
		
		
	</gene:javaScript>	
</gene:template>
<script>
	$(document).ready(function(){
		window.moveTo(0, 0);
		window.resizeTo(screen.availWidth, screen.availHeight);
		
		$('tr[id*="row"]').filter(function(){
			return $(this).find("td:eq(1)").find('span').text().replace(/\n/g,'').replace(/\t/g,'').replace(/\s/g, '')!=$(this).find("td:eq(2)").find('span').text().replace(/\n/g,'').replace(/\t/g,'').replace(/\s/g, '');
		}).closest("tr").css({'background-color' : 'rgba(255, 0, 0, 0.4)'}).find(".etichetta-dato").closest('td').css({'background-color': 'rgba(255, 0, 0, 0.1)'});
		
	});
		
	$('.expandContent').click(function(){
		var img = $(this);
		var div = $(this).closest('div');
		div.find('.collapsable').slideToggle(300, function() {
		
			if (img.attr('src').indexOf("collapse.gif") > -1) {
				img.attr('src', img.attr('src').replace('collapse', 'expand'));
				div.find('hr').css({"display": "block","margin-top" : "18.1px"});
			}
			else{
				img.attr('src', img.attr('src').replace('expand', 'collapse'));
				div.find('hr').css("display", "none");
			}
		});
		
	});

</script>

</div>
