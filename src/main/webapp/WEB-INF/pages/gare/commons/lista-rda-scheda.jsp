
<%
	/*
	 * Created on 20-Ott-2008
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

<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="documentiAssociatiDB" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.documentiAssociatiDB")}'/>

<gene:template file="scheda-template.jsp">

	<gene:redefineInsert name="head" >
		<script type="text/javascript">
			var _contextPath="${pageContext.request.contextPath}";
		</script>
	
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
		<script type="text/javascript" src="${contextPath}/js/jquery.validate.min.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.easytabs.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.wserpsupporto.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.wserp.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.cookie.js"></script>
				
		<style type="text/css">
		
			TABLE.dettaglio-notab TD.etichetta-search {
				width: 300px;
				PADDING-RIGHT: 10px;
				TEXT-ALIGN: right;
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
			
			TABLE.schedagperm TR.intestazione TH.codice, TABLE.schedagperm TR TD.desccenter {
				width: 20px;
				text-align: center;
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
	
	<c:set var="integrazioneWSERP" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSERPFunction", pageContext)}' />
	
	<c:choose>
		<c:when test='${requestScope.tipoWSERP eq "FNM"}'>
			<c:set var="baseTitolo" value="Lista dei procedimenti"/>
		</c:when>
		<c:otherwise>
			<c:set var="baseTitolo" value="Lista Rda"/>
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${param.genere eq "20"}'>
			<c:set var="genereTitolo" value="del catalogo"/>
		</c:when>
		<c:when test='${param.genere eq "10"}'>
			<c:set var="genereTitolo" value="dell' elenco"/>
		</c:when>
		<c:otherwise>
			<c:set var="genereTitolo" value="della gara"/>
		</c:otherwise>
	</c:choose>

	<gene:setString name="titoloMaschera" value="${baseTitolo} ${genereTitolo} ${param.codice}" />
		
	<c:if test='${requestScope.tipoWSERP eq "SMEUP"}'>
		<c:set var="carrelloAssociato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetTipoFornitureFunction", pageContext,param.codice)}' />
	</c:if>
	
	<gene:redefineInsert name="corpo">

			<input type="hidden" name="codgar" id="codgar" value="${param.codgar}" />
			<input type="hidden" name="codice" id="codice" value="${param.codice}" />
			<input type="hidden" name="genere" id="genere" value="${param.genere}" />
			<input type="hidden" name="tipoAppalto" id="tipoAppalto" value="${param.tipoAppalto}" />
			<input type="hidden" name="tipoProcedura" id="tipoProcedura" value="${param.tipoProcedura}" />
			<input type="hidden" name="bustalotti" id="bustalotti" value="${param.bustalotti}" />
			<input type="hidden" name="linkrda" id="linkrda" value="${param.linkrda}" />
			<input type="hidden" name="operation" id="operation" value="VISUALIZZA" />
			<input id="servizio" type="hidden" value="WSERP" />
			<input type="hidden" id="contextPath" name="contextPath" value="${contextPath}" />
			<input type="hidden" id="documentiAssociatiDB" name="documentiAssociatiDB" value="${documentiAssociatiDB}" />
			<input type="hidden" name="uffint" id="uffint" value="${param.uffint}" />
			<input type="hidden" name="proceduraTelematica" id="proceduraTelematica" value="${param.proceduraTelematica}" />
			<input type="hidden" name="modalitaPresentazione" id="modalitaPresentazione" value="${param.modalitaPresentazione}" />
			<input type="hidden" name="scProfilo" id="scProfilo" value="${param.scProfilo}" />
			<input type="hidden" name="messaggioControllo" id="messaggioControllo" value="${param.messaggioControllo}" />
			
			
			

		<table class="dettaglio-notab">
			<tr>
				<td>
					<br>
					<div id="rdaContainer" style="margin-left:8px; width: 98%"></div>
				    <br>
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<div class="error" id="rdamessaggio"></div>
				</td>
			</tr>
			<tr>	
				<td class="comandi-dettaglio">
					<c:choose>
						<c:when test='${isNavigazioneDisattiva ne "1"}'>
							<c:choose>
								<c:when test='${requestScope.tipoWSERP eq "FNM"}'>
										<c:choose>
										<c:when test='${param.tipoGara=="garaLottoUnico"}'>
											<INPUT type="button" id="pulsassprocedi" class="bottone-azione" value='Associa e prosegui' title='Associa e prosegui'>
										</c:when>
										<c:otherwise>
											<INPUT type="button" id="pulsasscarrello" class="bottone-azione" value='Associa procedimenti' title='Associa procedimenti'>
										</c:otherwise>
										</c:choose>
								</c:when>
								<c:otherwise>
										<INPUT type="button" id="pulsasscarrello" class="bottone-azione" value='Associa RdA' title='Associa RdA'>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
						</c:otherwise>
					</c:choose>
					&nbsp;
				</td>
			</tr>
		</table>
		
		<form name="listaNuovo" action="${contextPath}/Lista.do" method="post">
			<input type="hidden" name="jspPath" value="" /> 
			<input type="hidden" name="jspPathTo" value="" /> 
			<input type="hidden" name="activePage" value="0" /> 
			<input type="hidden" name="isPopUp" value="0" /> 
			<input type="hidden" name="numeroPopUp" value="0" /> 
			<input type="hidden" name="metodo" value="nuovo" /> 
			<input type="hidden" name="entita" value="" /> 
			<input type="hidden" name="gestisciProtezioni" value="1" />
		</form>
		
		
	</gene:redefineInsert>

	<gene:redefineInsert name="addToAzioni" >
		<c:if test='${requestScope.tipoWSERP eq "AVM"}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="#" id="menutrovarda" title="Trova RdA" tabindex="1511">Trova RdA</a>
				</td>
			</tr>
		</c:if>
		
		
		<c:choose>
			<c:when test='${requestScope.tipoWSERP eq "FNM"}'>
					<c:choose>
					<c:when test='${param.tipoGara=="garaLottoUnico"}'>
						<c:set var="menuFNM" value="Associa e prosegui"/>
					</c:when>
					<c:otherwise>
						<c:set var="menuFNM" value="Associa procedimenti"/>
					</c:otherwise>
					</c:choose>
				<tr>
			       	<tr>
			         <td class="vocemenulaterale">
			        	<c:if test='${isNavigazioneDisattiva eq isNavigazioneDisattiva}'>
							<a href="#" id="menuasscarrello" title=${menuFNM} tabindex="1512">${menuFNM}</a>
						</c:if>	
					 </td>
				   </tr>
				</tr>
			</c:when>
			<c:otherwise>
				<tr>
			       	<tr>
			         <td class="vocemenulaterale">
			        	<c:if test='${isNavigazioneDisattiva eq isNavigazioneDisattiva}'>
							<a href="#" id="menuasscarrello" title="Associa RdA" tabindex="1512">Associa RdA</a>
						</c:if>	
					 </td>
				   </tr>
				</tr>
			</c:otherwise>
		</c:choose>
		
	</gene:redefineInsert>

	
	<gene:redefineInsert name="noteAvvisi"/>
	<gene:redefineInsert name="documentiAssociati"/>

</gene:template>

	<div id="mascheraParametriLotto" title="Associa al lotto" style="display:none">
			<form id="richiestaLotto">
				<table class="dettaglio-notab">
				<tr>
					<td class="etichetta-dato">Numero lotto</td>
					<td class="valore-dato">
						<select id="lottosel" name="lotto"></select>
					</td>						
				</tr>
			</table>
		</form>
	</div>
	
	
	




