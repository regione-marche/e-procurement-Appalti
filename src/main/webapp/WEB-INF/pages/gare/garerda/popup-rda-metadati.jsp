<%
	/*
	 * Created on 03-nov-2009
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

<%//Popup per visualizzare i metadati di una rda%>



<div style="width:97%;">

<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="head" >
		<script type="text/javascript">
			var _contextPath="${pageContext.request.contextPath}";
		</script>
	
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
		<script type="text/javascript" src="${contextPath}/js/jquery.validate.min.js"></script>
		<script type="text/javascript" src="${contextPath}/js/jquery.easytabs.js"></script>
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
			
			
			TABLE.rda {
				margin-top: 5px;
				margin-bottom: 5px;
				padding: 0px;
				font-size: 11px;
				border-collapse: collapse;
				border-left: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
			}
	
			TABLE.rda TR.intestazione {
				background-color: #EFEFEF;
				border-bottom: 1px solid #A0AABA;
			}
			
			TABLE.rda TR.intestazione TH {
				padding: 2 15 2 5;
				text-align: center;
				font-weight: bold;
				border-left: 1px solid #A0AABA;
				border-right: 1px solid #A0AABA;
				border-top: 1px solid #A0AABA;
				border-bottom: 1px solid #A0AABA;
				height: 25px;
			}
	
			
			TABLE.rda TR TD.center {
				text-align: center;
			}
		
		
			TABLE.rda TR {
				background-color: #FFFFFF;
			}
	
			TABLE.rda TR TD {
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
			
			TABLE.rda TR TD.error {
				color: #D30000;
				font-weight: bold;
				padding: 10 10 10 10;
			}
	
			
			TABLE.rda TR TD.codice {
				width: 80px;
			}
			
				
		</style>
		
	</gene:redefineInsert>
	
	<c:choose>
		<c:when test='${!empty param.ngara}'>
			<c:set var="ngara" value="${param.ngara}" />
		</c:when>
		<c:otherwise>
			<c:set var="ngara" value="${ngara}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.numeroRda}'>
			<c:set var="numeroRda" value="${param.numeroRda}" />
		</c:when>
		<c:otherwise>
			<c:set var="numeroRda" value="${numeroRda}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.esercizio}'>
			<c:set var="esercizio" value="${param.esercizio}" />
		</c:when>
		<c:otherwise>
			<c:set var="esercizio" value="${esercizio}" />
		</c:otherwise>
	</c:choose>
	
	<c:choose>
		<c:when test='${!empty param.tipoWSERP}'>
			<c:set var="tipoWSERP" value="${param.tipoWSERP}" />
		</c:when>
		<c:otherwise>
			<c:set var="tipoWSERP" value="${tipoWSERP}" />
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

	<c:set var="key" value="GARE.NGARA=T:${ngara}" scope="request" />
	
	<c:set var="integrazioneWSERP" value='${gene:callFunction("it.eldasoft.sil.pg.tags.funzioni.EsisteIntegrazioneWSERPFunction", pageContext)}' scope="request" />
	<c:choose>
		<c:when test='${integrazioneWSERP eq "1"}'>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wserpsupporto.js"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>	
			<c:set var="servizio" value="FASCICOLOPROTOCOLLO" />
		</c:when>
		<c:otherwise>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmsupporto.js?v=${sessionScope.versioneModuloAttivo}"></script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wsdmfascicoloprotocollo.js?v=${sessionScope.versioneModuloAttivo}"></script>
			<c:set var="servizio" value="DOCUMENTALE" />
		</c:otherwise>
	</c:choose>
	
	<gene:setString name="titoloMaschera" value="Elenco metadati" />
	
	<gene:redefineInsert name="corpo">
			<table class="dettaglio-notab" id="datiLogin" style="display: none;">
				<jsp:include page="/WEB-INF/pages/gare/wsdm/wsdm-login.jsp"/>
			</table>

			<input type="hidden" name="step" id="step" value="${step}" />
			<input type="hidden" id="servizio"  value="${servizio}" />
			<input type="hidden" id="syscon" value="${profiloUtente.id}" /> 
			<input type="hidden" id="tiposistemaremoto" value="" />
			<input id="tabellatiInDB" type="hidden" value="" />
			<input type="hidden" id="entita"  value="GARE" /> 
			<input type="hidden" id="idprg"  value="PG" />
			<input type="hidden" id="key1"  name="key1" value="${ngara }" />
			<input type="hidden" id="key2"  name="key2" value="" /> 
			<input type="hidden" id="key3"  name="key3" value="" /> 
			<input type="hidden" id="key4"  name="key4" value="" /> 
			<input type="hidden" id="chiaveOriginale" value="${ngara }" />
			<input type="hidden" id="idconfi" value="${idconfi }" />
			<input id="abilitazioneGare" type="hidden" value="${profiloUtente.abilitazioneGare}" />


		

			<table class="dettaglio-notab">

			<tr>
					<td>
						<br>
						<div id="datiperscontainer" style="margin-left:8px; width: 98%"></div>
						
						<div id="finestraSchedaRda"></div>
						<br>
					</td>
			</tr>
			<tr>
					<td class="comandi-dettaglio" colSpan="2">
						<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:annulla();">&nbsp;&nbsp;
					</td>
			
				
			</tr>
			</table>
				

										
				

			
		
	</gene:redefineInsert>
	
	<gene:javaScript>


	
		
		/*
	     * Gestione utente ed attributi per il collegamento remoto
	     */
	     if(${tipoWSERP eq "FNM" || tipoWSERP eq "CAV" || tipoWSERP eq "AMIU" || tipoWSERP eq "RAIWAY"}){
	      if(${tipoWSERP eq "FNM"}){
	     	_getWSERPRda('${numeroRda}','${esercizio}');
	      }
	      
	      if(${tipoWSERP eq "CAV"}){
				_creaPopolaSchedaRda('${numeroRda}','${esercizio}');
	      }
	      
	      if(${tipoWSERP eq "AMIU"}){
				_creaPopolaRdaAmiu('${numeroRda}');
	      }

	      if(${tipoWSERP eq "RAIWAY"}){
				_creaPopolaRdaRaiway('${numeroRda}');
	      }
	     	
	     }else{
			$("#datiLogin").show();
			_getWSTipoSistemaRemoto();
			_popolaTabellato("ruolo","ruolo");
			_getWSLogin();
			_gestioneWSLogin();
			
			$("#datiLogin").hide();
	
			_getWSDMDocumento('${numeroRda}');
	     }
	     

	    function annulla(){
			window.close();
		}
		
	
	
	</gene:javaScript>
	
</gene:template>

</div>
