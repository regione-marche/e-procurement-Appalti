
<%
  /*
   * Created on 22-10-2012
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

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<style type="text/css">
  	
 	TABLE.griglia {
		margin: 0;
		margin-top: 5;
		margin-bottom: 5;
		PADDING: 0px;
		width: 100%;
		FONT-SIZE: 11px;
		border-collapse: collapse;
		border-left: 1px solid #A0AABA;
		border-top: 1px solid #A0AABA;
		border-right: 1px solid #A0AABA;
	}

	TABLE.griglia TH {
		PADDING: 2 5 2 5;
		TEXT-ALIGN: center;
	}
	
		
	TABLE.griglia TH.totale {
		border-left: 1px solid #FFFFFF;
		width: 40px;
	}
	
	TABLE.griglia TH.numordine {
		width: 40px;
	}
	
	TABLE.griglia TH.icona {
		width: 20px;
	}

	TABLE.griglia THEAD TR {
		background-color: #A7BFD9;
	}
	
	TABLE.griglia TR.riepilogo {
		background-color: #C4D4E5;
	}
	
	TABLE.griglia TR.dettaglioCriterio {
		background-color: #E1E9F2;
	}
	
	TABLE.griglia TR.dettaglioSottoCriterio {
		background-color: #FFFFFF;
	}
	
	TABLE.griglia TR.titolo {
		background-color: #FFFFFF;
	}
	
	TABLE.griglia TR.vuota {
		background-color: #FFFFFF;
	}

	TABLE.griglia TD {
		PADDING: 2 2 2 2;
		TEXT-ALIGN: left;
		BORDER: #A0AABA 1px solid;
	}
	
	TABLE.griglia TR.titolo TD {
		PADDING: 10 10 4 0;
		font-weight: bold;
	}

	span.normale {
		padding-left: 19px;
	}
	
	span.collapsed {
		padding-left: 19px;
		background-image: url("${contextPath}/img/treelist-expand.png");
		background-repeat: no-repeat;
	}
	
	span.expanded {
		padding-left: 19px;
		background-image: url("${contextPath}/img/treelist-collapse.png");
		background-repeat: no-repeat;
	}

	TABLE.griglia TR.vuota TD {
		PADDING: 10 0 0 0;
	}
	
	TABLE.griglia TR.dettaglioSottoCriterio TD.info {
		
	}
	
	TABLE.griglia TR.riepilogo TD.normale
	{
		padding-left: 19px;
	}
	
	TABLE.griglia TR.riepilogo TD.collapsed
	{
		padding-left: 19px;
		background-image: url("${contextPath}/img/treelist-expand.png");
		background-repeat: no-repeat;
	}

	TABLE.griglia TR.riepilogo TD.expanded
	{
		padding-left: 19px;
		background-image: url("${contextPath}/img/treelist-collapse.png");
		background-repeat: no-repeat;
	}
	
li.advon
{
	background: url("${contextPath}/img/tick.png") 0 50%;
}
li.advoff
{
	background: url("${contextPath}/img/cross.png") 0 50%;
}
#targetall
{
	list-style: none;
	background-color: #eee;
}
#targetall li
{
	background-repeat: no-repeat;
	padding-left: 20px;
}
#thSelectColumn div.cmDiv
{
	display: inline;
	background: none;
	border: 0;
}
#thSelectColumn li.main
{
	padding: 0;
	background: none;
	width: 100%;
	text-align: right;
}
#thSelectColumn li.main li
{
	text-align: left;
	padding-right: 2px;
}
#thSelectColumn
{
	padding-right: 2px;
	width: 10px;
	border-left: 1px solid #FFFFFF;
}
#ulSelectColumn
{
	vertical-align: middle;
}
	
</style>

<div style="width:97%;">

	<gene:template file="popup-template.jsp">
		
		
		<gene:redefineInsert name="head">
			<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/clickmenu.css" />
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.clickmenu.js"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.columnmanager.js"></script>
			
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.cookie.js"></script>	
		</gene:redefineInsert>	
			
			
		
	
		<gene:redefineInsert name="addHistory" />
		<gene:redefineInsert name="gestioneHistory" />

		<c:set var="ngara" value='${param.ngara}' scope="request" />
		<c:set var="codgar" value='${param.codgar}' scope="request" />
		<c:set var="isOffertaUnica" value='${param.isOffertaUnica}' scope="request" />
		
		<c:set var="risultato" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetProspettoPunteggiFunction",pageContext,ngara,codgar,isOffertaUnica)}'/>
			
		<gene:setString name="titoloMaschera"  value='Prospetto complessivo dei punteggi delle ditte della gara ${ngara } ' />
	
		<gene:redefineInsert name="corpo">
		
			<table class="lista">
				<tr>
					<td>
						<table id="tableprospetto" class="griglia">
							<thead>
								<tr>
								 <!-- 1 -->   <th class="numordine">&nbsp;</th>
								 <!-- 2 -->   <th class="numordine">&nbsp;</th>
								 <!-- 3 -->   <th>&nbsp;</th>
								 <!-- 4 -->   <th class="totale"><span title="Punteggio">Punteggio</span></th>
								 <!-- 5 -->   <th class="totale"><span title="Punteggio riparametrato">Punt. riparam.</span></th>
								 <!-- 6 -->   <th id="thSelectColumn"><ul id="ulSelectColumn"><li><img src="${contextPath}/img/selectcol.png" alt="selezione colonne" title="selezione colonne" /><ul id="targetall"></ul></li></ul></th>
								 <!-- 7 -->   <th class="totale"><span title="Punteggio massimo">Punt. max</span></th>
								 <!-- 8 -->   <th class="totale"><span title="Punteggio minimo">Soglia min.</span></th>
								    
								    
								</tr>
							</thead>
							<tbody>
								<c:if test='${not empty listaPunteggiDitte}'>
								<c:forEach items="${listaPunteggiDitte}" var="punteggiDitta" varStatus="status" >
									<tr class="titolo"  onclick="javascript:togglegruppo('${status.index}');">
										<td colspan="3">
											<span id="titolo_${status.index}" class="collapsed"><b>${punteggiDitta[0]}</b></span>
										</td>
										<td >
											${punteggiDitta[3]}
										</td>
										<td >
											${punteggiDitta[13]}
										</td>
									</tr>
									<tr class="riepilogo" id="PUNTEC_${status.index}" onclick="javascript:toggledettaglio('PUNTEC_${status.index}','criterioTec');">
										<td colspan="3" class="collapsed" id="PUNTEC_${status.index}_td">
											Punteggio tecnico
										</td>
										<td >
											${punteggiDitta[1]}
										</td>
										<td >
											${punteggiDitta[11]}
										</td>
										<c:choose>
											<c:when test="${punteggiDitta[9] eq true}">
												<td style="text-align:center;"><img width="16" height="16" alt= "Punteggio ${gene:if(riptec eq '2', 'riparametrato ', '')}inferiore alla soglia minima" title="Punteggio ${gene:if(riptec eq '2', 'riparametrato ', '')}inferiore alla soglia minima" src="${pageContext.request.contextPath}/img/isquantimod.png"/></td>
											</c:when>
											<c:when test="${punteggiDitta[7] eq true}">
												<td style="text-align:center;"><img width="16" height="16" alt= "Punteggio superiore al punteggio massimo" title="Punteggio superiore al punteggio massimo" src="${pageContext.request.contextPath}/img/isquantimod.png"/></td>
											</c:when>
											<c:otherwise>
												<td ></td>
											</c:otherwise>
										</c:choose>
										<td>${punteggioTecMax }</td>
										<td>${mintec }</td>
										
									</tr>
									<c:forEach items="${punteggiDitta[5]}" var="criterioTecnico" varStatus="status1" >
										<c:choose>
											<c:when test='${criterioTecnico[5] eq 0 or criterioTecnico[5] eq 0.0}'>
												<tr class="dettaglioCriterio" id="PUNTEC_${status.index}_criterioTec_${status1.index}" >
											</c:when>
											<c:otherwise>
												<tr class="dettaglioSottoCriterio" id="PUNTEC_${status.index}_criterioTec_${status1.index}" >
											</c:otherwise>
										</c:choose>
											
											<c:set var="decimaliCriterio" value='${fn:split(criterioTecnico[4], ".")[1]}'/>
											<c:choose>
												<c:when test="${decimaliCriterio == 0}">
													<c:set var="numCriterio" value='${fn:split(criterioTecnico[4], ".")[0]}'/>
												</c:when>
												<c:otherwise>
													<c:set var="numCriterio" value='${criterioTecnico[4]}'/>
												</c:otherwise>
											</c:choose>
											
											<c:set var="decimaliSubCriterio" value='${fn:split(criterioTecnico[5], ".")[1]}'/>
											<c:choose>
												<c:when test="${decimaliSubCriterio == 0}">
													<c:set var="numSubCriterio" value='${fn:split(criterioTecnico[5], ".")[0]}'/>
												</c:when>
												<c:otherwise>
													<c:set var="numSubCriterio" value='${criterioTecnico[5]}'/>
												</c:otherwise>
											</c:choose>
																						
											<td >
												<c:if test='${criterioTecnico[5] eq 0 or criterioTecnico[5] eq 0.0}'>${numCriterio}</c:if>
											</td>
											<td <c:if test='${criterioTecnico[5] ne 0 and criterioTecnico[5] ne 0.0}'>class="info"</c:if>>
												<c:if test='${criterioTecnico[5] ne 0 and criterioTecnico[5] ne 0.0}'>${numSubCriterio}</c:if>
											</td>
											<td <c:if test='${criterioTecnico[5] ne 0 and criterioTecnico[5] ne 0.0}'>class="info"</c:if>>
												${criterioTecnico[0]}
											</td>
											<td >
												${criterioTecnico[7]}
											</td>
											<td >
												${criterioTecnico[8]}
											</td>
											<c:choose>
												<c:when test="${controlliSuPuntRiparamTecPerCriteri }">
													<c:set var="puntCriterioTec" value="${criterioTecnico[8] }"/>
												</c:when>
												<c:otherwise>
													<c:set var="puntCriterioTec" value="${criterioTecnico[7] }"/>
												</c:otherwise>
											</c:choose>
											<c:choose>
												<c:when test="${puntCriterioTec < criterioTecnico[3] && not empty puntCriterioTec and not empty criterioTecnico[3] and !saltareControlliSuPuntRiparamTecPerCriteri}">
													<td style="text-align:center;"><img width="16" height="16" alt= "Punteggio ${gene:if(controlliSuPuntRiparamTecPerCriteri, 'riparametrato ', '')}inferiore alla soglia minima" title="Punteggio ${gene:if(controlliSuPuntRiparamTecPerCriteri, 'riparametrato ', '')}inferiore alla soglia minima" src="${pageContext.request.contextPath}/img/isquantimod.png"/></td>
												</c:when>
												<c:when test="${criterioTecnico[7] > criterioTecnico[1]}">
													<td style="text-align:center;"><img width="16" height="16" alt= "Punteggio superiore al punteggio massimo" title="Punteggio superiore al punteggio massimo" src="${pageContext.request.contextPath}/img/isquantimod.png"/></td>
												</c:when>
												<c:otherwise>
													<td ></td>
												</c:otherwise>
											</c:choose>
											<td>${criterioTecnico[1]}</td>
											<td>${criterioTecnico[3]}</td>
											
										</tr>
									</c:forEach>
									<tr class="riepilogo" id="PUNECO_${status.index}" onclick="javascript:toggledettaglio('PUNECO_${status.index}','criterioEco');">
										<td colspan="3" class="collapsed" id="PUNECO_${status.index}_td">
											Punteggio economico
										</td>
										<td >
											${punteggiDitta[2]}
										</td>
										<td >
											${punteggiDitta[12]}
										</td>
										<c:choose>
											<c:when test="${punteggiDitta[10] eq true}">
												<td style="text-align:center;"><img width="16" height="16" alt= "Punteggio ${gene:if(ripeco eq '2', 'riparametrato ', '')}inferiore alla soglia minima" title="Punteggio  ${gene:if(ripeco eq '2', 'riparametrato ', '')}inferiore alla soglia minima" src="${pageContext.request.contextPath}/img/isquantimod.png"/></td>
											</c:when>
											<c:when test="${punteggiDitta[8] eq true}">
												<td style="text-align:center;"><img width="16" height="16" alt= "Punteggio superiore al punteggio massimo" title="Punteggio superiore al punteggio massimo" src="${pageContext.request.contextPath}/img/isquantimod.png"/></td>
											</c:when>
											<c:otherwise>
												<td ></td>
											</c:otherwise>
										</c:choose>
										<td>${punteggioEcoMax }</td>
										<td>${mineco }</td>
										
									</tr>
									
										<c:forEach items="${punteggiDitta[6]}" var="criterioEconomico" varStatus="status2" >
											<c:choose>
												<c:when test='${criterioEconomico[5] eq 0 or criterioEconomico[5] eq 0.0}'>
													<tr class="dettaglioCriterio" id="PUNECO_${status.index}_criterioEco_${status2.index}" >
												</c:when>
												<c:otherwise>
													<tr class="dettaglioSottoCriterio" id="PUNECO_${status.index}_criterioEco_${status2.index}" >
												</c:otherwise>
											</c:choose>
												
												<c:set var="decimaliCriterio" value='${fn:split(criterioEconomico[4], ".")[1]}'/>
												<c:choose>
													<c:when test="${decimaliCriterio == 0}">
														<c:set var="numCriterio" value='${fn:split(criterioEconomico[4], ".")[0]}'/>
													</c:when>
													<c:otherwise>
														<c:set var="numCriterio" value='${criterioEconomico[4]}'/>
													</c:otherwise>
												</c:choose>
												
												<c:set var="decimaliSubCriterio" value='${fn:split(criterioEconomico[5], ".")[1]}'/>
												<c:choose>
													<c:when test="${decimaliSubCriterio == 0}">
														<c:set var="numSubCriterio" value='${fn:split(criterioEconomico[5], ".")[0]}'/>
													</c:when>
													<c:otherwise>
														<c:set var="numSubCriterio" value='${criterioEconomico[5]}'/>
													</c:otherwise>
												</c:choose>
												
												<td >
													<c:if test='${criterioEconomico[5] eq 0 or criterioEconomico[5] eq 0.0}'>${numCriterio}</c:if>
												</td>
												<td <c:if test='${criterioEconomico[5] ne 0 and criterioEconomico[5] ne 0.0}'>class="info"</c:if>>
													<c:if test='${criterioEconomico[5] ne 0 and criterioEconomico[5] ne 0.0}'>${numSubCriterio}</c:if>
												</td>
												<td <c:if test='${criterioEconomico[5] ne 0 and criterioEconomico[5] ne 0.0}'>class="info"</c:if>>
													${criterioEconomico[0]}
												</td>
												<td >
													${criterioEconomico[7]}
												</td>
												<td >
													${criterioEconomico[8]}
												</td>
												<c:choose>
													<c:when test="${controlliSuPuntRiparamEcoPerCriteri }">
														<c:set var="puntCriterioEco" value="${criterioEconomico[8] }"/>
													</c:when>
													<c:otherwise>
														<c:set var="puntCriterioEco" value="${criterioEconomico[7] }"/>
													</c:otherwise>
												</c:choose>
												<c:choose>
													<c:when test="${puntCriterioEco < criterioEconomico[3] && not empty puntCriterioEco and not empty criterioEconomico[3] && ! saltareControlliSuPuntRiparamEcoPerCriteri}">
														<td style="text-align:center;"><img width="16" height="16" alt= "Punteggio ${gene:if(controlliSuPuntRiparamEcoPerCriteri, 'riparametrato ', '')}inferiore  alla soglia minima" title="Punteggio ${gene:if(controlliSuPuntRiparamEcoPerCriteri, 'riparametrato ', '')}inferiore alla soglia minima" src="${pageContext.request.contextPath}/img/isquantimod.png"/></td>
													</c:when>
													<c:when test="${criterioEconomico[7] > criterioEconomico[1]}">
														<td style="text-align:center;"><img width="16" height="16" alt= "Punteggio superiore al punteggio massimo" title="Punteggio superiore al punteggio massimo" src="${pageContext.request.contextPath}/img/isquantimod.png"/></td>
													</c:when>
													<c:otherwise>
														<td ></td>
													</c:otherwise>
												</c:choose>
												<td>${criterioEconomico[1]}</td>
												<td>${criterioEconomico[3]}</td>
												
											</tr>
									</c:forEach>
									
									
								</c:forEach>
								</c:if>
							</tbody>
						</table>
					</td>
				</tr>
				<tr>
					<td class="comandi-dettaglio" colSpan="2">
						<INPUT type="button" class="bottone-azione" value="Aggiorna" title="Aggiorna" onclick="javascript:aggiorna()">
						<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi()">&nbsp;
					</td>
				</tr>
			</table>
		</gene:redefineInsert>
	
		<gene:javaScript>
			$(document).ready(function (){
				function resettaCurrentPopup() {
					window.opener.currentPopUp=null;
				}

				window.opener.currentPopUp=null;
    			window.onfocus=resettaCurrentPopup;
				
				$(".dettaglioCriterio").hide();
				$(".dettaglioSottoCriterio").hide();
				
				//Plugin columnmanager
				//Vengono nascoste all'apertura della pagina le colonne 7 e 8.
				var riparametrizzazione = "${riparametrizzazione }";
				//Se non è prevista la riparametrizzazione si nasconde la colonna 5
				//Dal menu per la visualizzazione delle colonne nascoste vengono escluse le colonne 1,2,3,4,5,6 
				if(riparametrizzazione=="true")
					$('#tableprospetto').columnManager({ listTargetID:'targetall', onClass: 'advon', offClass: 'advoff',hideInList: [1,2,3,4,5,6]});
				else
					$('#tableprospetto').columnManager({ listTargetID:'targetall', onClass: 'advon', offClass: 'advoff',hideInList: [1,2,3,4,5,6], colsHidden: [5]});
				
				
				//Viene richiamato il plugin clickMenu
				$('#ulSelectColumn').clickMenu({onClick: function(){}});
				
						
			});
				
			function chiudi(){
				window.close();
			}
		
			function aggiorna(){
				window.location.reload();
			}
			
			function togglegruppo(riga) {
				var titolo = $("#titolo_" + riga);
				//var righeriepilogo = $('table.griglia tr.riepilogo td');
				var riepilogoTec =  $("#PUNTEC_" + riga + "_td");
				var riepilogoEco =  $("#PUNECO_" + riga + "_td");
								
				if (titolo.hasClass('expanded')) {
					titolo.removeClass("expanded");
					titolo.addClass("collapsed");
					if (riepilogoTec.hasClass('expanded')){
						toggledettaglio('PUNTEC_' +riga,'criterioTec');
					}
					if (riepilogoEco.hasClass('expanded')){
						toggledettaglio('PUNECO_' +riga,'criterioEco');
					}
					
				} else {
					titolo.addClass("expanded");
					titolo.removeClass("collapsed");
					if (!riepilogoTec.hasClass('expanded')){
						toggledettaglio('PUNTEC_' +riga,'criterioTec');
					}
					if (!riepilogoEco.hasClass('expanded')){
						toggledettaglio('PUNECO_' +riga,'criterioEco');
					}
					
				}
				
				
			}
			
			function toggledettaglio(idp,criterio) {
				
				var riepilogo =  $("#" + idp + "_td");
				var righedettaglio = $('table.griglia tr');
				var righedettagliofilter = righedettaglio.filter('[id^="' + idp + '_' + criterio + '_"]');
				if (riepilogo.hasClass('expanded')) {
					riepilogo.removeClass("expanded");
					riepilogo.addClass("collapsed");
					righedettagliofilter.hide("slow");
				} else {
					riepilogo.addClass("expanded");
					riepilogo.removeClass("collapsed");
					righedettagliofilter.show("slow");
				}
				
			}
			
		
		</gene:javaScript>
		
	</gene:template>

</div>

