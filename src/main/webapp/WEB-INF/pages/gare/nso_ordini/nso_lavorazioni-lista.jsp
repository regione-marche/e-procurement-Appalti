
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:template file="scheda-template.jsp">

	<gene:redefineInsert name="head" >
		<script type="text/javascript">
			var _contextPath="${pageContext.request.contextPath}";
		</script>
	
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
		<script type="text/javascript" src="${contextPath}/js/jquery.validate.min.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.easytabs.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.nso.ordini.js?t=<%=System.currentTimeMillis()%>"></script>
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
	
	
	
	<c:set var="baseTitolo" value="Lista lavorazioni della gara "/>
	

	<gene:setString name="titoloMaschera" value="${baseTitolo} ${param.numeroGara}" />
		
	
	<gene:redefineInsert name="corpo">

			<input type="hidden" name="codiceGara" id="codiceGara" value="${param.codiceGara}" />
			<input type="hidden" name="numeroGara" id="numeroGara" value="${param.numeroGara}" />
			<input type="hidden" name="codiceDitta" id="codiceDitta" value="${param.codiceDitta}" />
			<input type="hidden" name="operazione" id="operazione" value="${param.operazione}" />
			<input type="hidden" name="idOrdine" id="idOrdine" value="${param.idOrdine}" />
			<input type="hidden" name="codice" id="codice" value="${param.codice}" />
			<input type="hidden" name="genere" id="genere" value="${param.genere}" />
			<input type="hidden" name="bustalotti" id="bustalotti" value="${param.bustalotti}" />
			<input type="hidden" id="contextPath" name="contextPath" value="${contextPath}" />
			<input type="hidden" name="uffint" id="uffint" value="${param.uffint}" />
			<input type="hidden" name="proceduraTelematica" id="proceduraTelematica" value="${param.proceduraTelematica}" />
			<input type="hidden" name="modalitaPresentazione" id="modalitaPresentazione" value="${param.modalitaPresentazione}" />
			<input type="hidden" name="messaggioControllo" id="messaggioControllo" value="${param.messaggioControllo}" />
			
			
			

		<table class="dettaglio-notab">
			<tr>
				<td>
					<br>
					<div id="lavNsoContainer" style="margin-left:8px; width: 98%"></div>
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
				<c:when test='${param.operazione eq "ADD"}'>
					<INPUT type="button" id="pulsannulla" class="bottone-azione" value="Annulla" title="Annulla" >&nbsp;
					<INPUT type="button" id="pulsaggiungi"   class="bottone-azione" value="Aggiungi" title="Aggiungi" >&nbsp;
				</c:when>
				<c:otherwise>
					<INPUT type="button" id="pulsannulla" class="bottone-azione" value="Annulla" title="Annulla" >&nbsp;
					<INPUT type="button" id="pulsindietro" class="bottone-azione" value="&lt; Indietro" title="Indietro" >&nbsp;
					<INPUT type="button" id="pulsavanti"   class="bottone-azione" value="Avanti &gt;" title="Avanti" >&nbsp;
				</c:otherwise>
				</c:choose>

					
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
	
		<c:choose>
		<c:when test='${param.operazione eq "ADD"}'>
		<tr>
			<td class="vocemenulaterale">
				<a href="#" id="menuaggiungi" title="Aggiungi" tabindex="1502">
					Aggiungi
				</a>
			</td>
		</tr>
		</c:when>
		<c:otherwise>
		<tr>
			<td class="vocemenulaterale">
				<a href="#" id="menuavanti" title="Avanti" tabindex="1502">
					Avanti &gt;
				</a>
			</td>
		</tr>
		<tr>
			<td class="vocemenulaterale">
				<a href="#" id="menuindietro" title="Indietro" tabindex="1503">
					&lt; Indietro
				</a>
			</td>
		</tr>
		</c:otherwise>
		</c:choose>

		<tr>
			<td class="vocemenulaterale">
				<a href="#" id="menuannulla" title="Annulla" tabindex="1504">
					Annulla
				</a>
			</td>
		</tr>	

	
	
	
		
	</gene:redefineInsert>

	
	<gene:redefineInsert name="noteAvvisi"/>
	<gene:redefineInsert name="documentiAssociati"/>

</gene:template>


	




