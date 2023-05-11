
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="codgar" value="${param.codgar}"/>
<c:set var="ngara" value="${param.ngara}"/>
<c:set var="solaLettura" value="${param.autorizzatoModifiche eq '2'}" />
<c:set var="pubblicata" value="${param.bloccoModificatiDati || false}" />
<c:if test="${empty backToScollega}">
<c:choose>
	<c:when test="${not empty param.handleRda && param.handleRda eq 'scollega'}">
		<c:set var="titolo" value="Lista RdA/RdI della gara ${codgar.replace('$','')}" />
		<c:set var="conteggioRDARDI" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.GetRDARDIFunction", pageContext, "rdaCollegate",codgar,null,"true")}' />
		<c:set var="handle" value="scollega" />
	</c:when>
	<c:when test="${not empty param.handleRda && param.handleRda eq 'scollegalotto'}">
		<c:set var="titolo" value="Lista RdA/RdI del lotto ${ngara}" />
		<c:set var="conteggioRDARDI" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.GetRDARDIFunction", pageContext, "rdaCollegate",codgar,ngara,null)}' />
		<c:set var="handle" value="scollegalotto" />
	</c:when>
	<c:otherwise>
		<c:set var="titolo" value="Lista RdA/RdI da collegare" />
		<c:set var="conteggioRDARDI" value='${gene:callFunction5("it.eldasoft.sil.pg.tags.funzioni.GetRDARDIFunction", pageContext, "codiceApplicativoNonCollegato",null,null,null)}' />
		<c:set var="handle" value="" />
	</c:otherwise>
</c:choose>
</c:if>
<gene:template file="scheda-template.jsp">

  <gene:redefineInsert name="head" >
    <script type="text/javascript">
      var _contextPath="${pageContext.request.contextPath}";
    </script>

    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/wsdm/jquery.wsdm.css" >
    <script type="text/javascript" src="${contextPath}/js/jquery.validate.min.js"></script>
    <script type="text/javascript" src="${contextPath}/js/jquery.easytabs.js"></script>
    <script type="text/javascript" src="${contextPath}/js/jquery.integrazioneprogrammazione.js"></script>

    <style type="text/css">
		
		.div_importo{
			float: left;
			width: 48%;
			font: 11px Verdana, Arial, Helvetica, sans-serif;
			margin: 8px 1%;
		}
		.importo_collegato div b{
			margin-right: 10px;
		}
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



  <gene:setString name="titoloMaschera" value="${titolo}" />

  <gene:redefineInsert name="corpo">
  <form name="formListaRdaRdi" action="${pageContext.request.contextPath}/pg/CollegaScollegaRda.do" method="post">
	<input type="hidden" name="handleRda" id="handleRda" value="${handle}" />
	<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
	<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
	<input type="hidden" name="autorizzatoModifiche" id="autorizzatoModifiche" value="${param.autorizzatoModifiche}" />
	<input type="hidden" name="bloccoModificatiDati" id="bloccoModificatiDati" value="${param.bloccoModificatiDati}"/>
	</form> 

	<form action="${pageContext.request.contextPath}/pg/CalcolaImportoRdA.do" method="post" name="formCalcolaImporto" >
	<input type="hidden" name="handleRda" id="handleRda" value="${handle}" />
	<input type="hidden" name="codgar" id="codgar" value="${codgar}" />
	<input type="hidden" name="ngara" id="ngara" value="${ngara}" />
	<input type="hidden" name="autorizzatoModifiche" id="autorizzatoModifiche" value="${param.autorizzatoModifiche}" />
	<input type="hidden" name="bloccoModificatiDati" id="bloccoModificatiDati" value="${param.bloccoModificatiDati}"/>
	<input type="hidden" name="importo" id="importo" value="0"/>
	</form>

	<c:set var="container" value="listarda" />
    <table class="dettaglio-notab">
	 <c:if test="${not empty param.handleRda && (codgar.indexOf('$')==0 || not empty ngara)}">
	 <c:set var="parametroNgara" value="${not empty ngara? ngara : codgar.replace('$','')}" />
	  ${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetImportoGaraFunction", pageContext,parametroNgara,"false")}
		
		  <tr>
			<td>
			  <div class="importo_collegato" id="importo_collegato">
			  <div class="div_importo" style="color:blue"><b>Totale importo a base di gara:</b><span class="importo"><c:if test="${importoGara>0}"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${importoGara}" /> &euro;</c:if></span></div>
			  </div>
			</td>
		  </tr>
	  </c:if>
      <tr>
        <td>
          <br>
			<div id="container_${container}" style="margin-left:8px; width: 98%"></div>
          <br>
        </td>
      </tr>
	 
      <tr>
        <td class="comandi-dettaglio">
           <c:if test='${isNavigazioneDisattiva ne "1" && !solaLettura}'>
				
			<c:choose>
				<c:when test="${not empty param.handleRda}">
					<INPUT type="button" onclick="javascript:trovaRda()" class="bottone-azione" value="Aggiungi" title="Aggiungi">&nbsp;
					<INPUT type="button" id="pulselimina" class="bottone-azione" value="Elimina selezionati" title="Elimina selezionati">&nbsp;
				</c:when>
				<c:otherwise>
					<INPUT type="button" id="pulscrea" class="bottone-azione" value="Crea nuova gara >" title="Crea nuova gara">&nbsp;
				</c:otherwise>
			</c:choose>
			 </c:if>
			 <br><br>
			 <c:choose>
			<c:when test="${not empty param.handleRda && param.handleRda eq 'scollega'}">
				<INPUT type="button" class="bottone-azione" value="Torna al dettaglio gara" title="Torna al dettaglio gara" onclick="javascript:historyVaiIndietroDi(1);">&nbsp;
				  &nbsp;
			</c:when>
			<c:when test="${not empty param.handleRda && param.handleRda eq 'scollegalotto'}">
				<INPUT type="button" class="bottone-azione" value="Torna al dettaglio lotto" title="Torna al dettaglio lotto" onclick="javascript:historyVaiIndietroDi(1);">&nbsp;
				  &nbsp;
			</c:when>
			<c:otherwise>
				<INPUT type="button" class="bottone-azione" value="Indietro" title="Indietro" onclick="javascript:historyVaiIndietroDi(1);">&nbsp;
				  &nbsp;
			</c:otherwise>
		</c:choose>
        </td>
      </tr>
    </table>
  </gene:redefineInsert>
  
  <gene:redefineInsert name="addToAzioni" >
		 <c:if test='${isNavigazioneDisattiva ne "1" && !solaLettura}'>
			<c:choose>
				<c:when test="${not empty param.handleRda}">
					<td class="vocemenulaterale">
						<a href="javascript:trovaRda();" title='Aggiungi' tabindex="1511">Aggiungi</a>
					</td>
				</tr>
				<tr>
					<td class="vocemenulaterale" >
						<a href="#" id="menuelimina" title="Elimina selezionati" tabindex="1512">Elimina selezionati</a>
					</td>
				</tr>
				 <c:if test="${!pubblicata && (codgar.indexOf('$')==0 || not empty ngara)}">
				<tr>
					<td class="vocemenulaterale" >
						<a href="javascript:calcolaImporto();" title='Calcola importo di gara da RdA' tabindex="1513">Calcola importo a base di gara da RdA/RdI</a>
					</td>
				</tr>
				</c:if>
				</c:when>
				<c:otherwise>
				<tr>
					<td class="vocemenulaterale">
						<a href="#" id="menucrea" title="Crea nuova gara" tabindex="1512">Crea nuova gara</a>
					</td>
				</tr>
				</c:otherwise>
			</c:choose>
          </c:if>
      </td>
  </gene:redefineInsert>

  <gene:redefineInsert name="noteAvvisi"/>
  <gene:redefineInsert name="documentiAssociati"/>
  
	<gene:javaScript>
	var ngara = "${ngara}";
	var codgar = "${codgar}";
	var back = "${backToScollega}";
	var url= document.location.href;
	var inizializzata = false;
	var isLotto;
	var codgarParam;
	var ngaraParam;
	var lottoUnico;
	var isNotHomepage = false;
	var bloccoSolaLettura = ${solaLettura};
	var bloccoPubblicata = ${pubblicata};
	$(document).ready(function () {
		 isLotto = document.getElementById("handleRda")?.value=='scollegalotto';
		 codgarParam = document.getElementById("codgar")?.value;
		 ngaraParam = document.getElementById("ngara")?.value;
		 if(codgarParam.length>0){
			  lottoUnico = codgarParam.includes('$');
			  isNotHomepage = true;
		}
		if(back == 'true'){
			bloccaRichiesteServer();
			if(ngaraParam){
				formListaRdaRdi.handleRda.value = "scollegalotto";
			}else{
				formListaRdaRdi.handleRda.value = "scollega";
			}
			formListaRdaRdi.action = formListaRdaRdi.action.includes("CSRFToken")? formListaRdaRdi.action : formListaRdaRdi.action + "?" +csrfToken
			formListaRdaRdi.submit();
		}else{
			var data = ${requestScope.listaRDARDI}
			_generaTabella(data,codgar,"listarda",ngara,bloccoSolaLettura,bloccoPubblicata);
		}
	});
	
	function trovaRda(){
		if(!inizializzata){
			_creaFinestraSelRda(codgar,ngara);
			_creaContainerListaRda(codgar,ngara);
		}
		inizializzata=true;
		$("#finestraselrda").dialog("open");
	};
	
	function calcolaImporto(){
		_creaFinestraConferma();
	}
	
</gene:javaScript>
</gene:template>
