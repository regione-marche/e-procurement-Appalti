<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/jquery.documenti.gara.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>

<c:choose>
	<c:when test='${!empty param.numeroGara}'>
		<c:set var="numeroGara" value="${param.numeroGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="numeroGara" value="${numeroGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.gruppo}'>
		<c:set var="gruppo" value="${param.gruppo}" />
	</c:when>
	<c:otherwise>
		<c:set var="gruppo" value="${gruppo}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.codiceGara}'>
		<c:set var="codiceGara" value="${param.codiceGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value="${codiceGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.autorizzatoModifiche}'>
		<c:set var="autorizzatoModifiche" value="${param.autorizzatoModifiche}" />
	</c:when>
	<c:otherwise>
		<c:set var="autorizzatoModifiche" value="${autorizzatoModifiche}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty numeroGara}'>
		<c:set var="chiaveRda" value="${numeroGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="chiaveRda" value="${codiceGara}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.genere}'>
		<c:set var="genere" value="${param.genere}" />
	</c:when>
	<c:otherwise>
		<c:set var="genere" value="${genere}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.gestioneQuestionariPreq}'>
		<c:set var="gestioneQuestionariPreq" value="${param.gestioneQuestionariPreq}" />
	</c:when>
	<c:otherwise>
		<c:set var="gestioneQuestionariPreq" value="${gestioneQuestionariPreq}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.gestioneQuestionariAmm}'>
		<c:set var="gestioneQuestionariAmm" value="${param.gestioneQuestionariAmm}" />
	</c:when>
	<c:otherwise>
		<c:set var="gestioneQuestionariAmm" value="${gestioneQuestionariAmm}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.gestioneQuestionariTec}'>
		<c:set var="gestioneQuestionariTec" value="${param.gestioneQuestionariTec}" />
	</c:when>
	<c:otherwise>
		<c:set var="gestioneQuestionariTec" value="${gestioneQuestionariTec}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.gestioneQuestionariEco}'>
		<c:set var="gestioneQuestionariEco" value="${param.gestioneQuestionariEco}" />
	</c:when>
	<c:otherwise>
		<c:set var="gestioneQuestionariEco" value="${gestioneQuestionariEco}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${!empty param.gestioneQuestionariIscriz}'>
		<c:set var="gestioneQuestionariIscriz" value="${param.gestioneQuestionariIscriz}" />
	</c:when>
	<c:otherwise>
		<c:set var="gestioneQuestionariIscriz" value="${gestioneQuestionariIscriz}" />
	</c:otherwise>
</c:choose>

<c:if test="${genereGara eq '10' or genereGara eq '20' }">
	<c:set var="garaElencoCatalogo" value='true'/>
</c:if>

<c:choose>
	<c:when test='${gruppo eq 3 and garaElencoCatalogo ne "true"}'>
		<c:set var="varQuestionari" value="${gestioneQuestionariPreq},${gestioneQuestionariAmm},${gestioneQuestionariTec},${gestioneQuestionariEco}" />
		<c:set var="dati" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetBusteDocumentiConcorrentiJsonFunction", pageContext,codiceGara,numeroGara,varQuestionari)}'></c:set>
	</c:when>
	<c:when test='${gruppo eq 3 and garaElencoCatalogo eq "true"}'>
		<c:set var="dati" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetFaseleDocumentiOperatoriJsonFunction", pageContext,codiceGara,numeroGara,gestioneQuestionariIscriz)}'></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="dati" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetTipologieDocumentiJsonFunction", pageContext,codiceGara,numeroGara)}'></c:set>
	</c:otherwise>
</c:choose>

<c:set var="integrazioneSCP" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "invioScp.ws.url")}'/>

<gene:redefineInsert name="schedaModifica"/>
<gene:redefineInsert name="pulsanteModifica"/>
<gene:redefineInsert name="addToAzioni" >
</gene:redefineInsert>
<gene:campoScheda>
	<c:if test="${dati eq '[]'}">
		<td colspan="2"><b>Non &egrave; possibile effettuare pubblicazioni</b></td>
	</c:if>
	<c:if test="${dati ne '[]'}">
		<td colspan="2"><b>&nbsp;</b>
		<br>
		<div id="jstree2" class="demo"></div></td>		
	</c:if>
	<c:if test='${autorizzatoModifiche ne "2" and isIntegrazionePortaleAlice eq "true"}'>
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.PubblicaSuPortale")}'>
			<tr class="comandi-dettaglio">
				<td colspan="2">
					<INPUT type="button"  class="bottone-azione" value='Pubblica su portale Appalti' title='Pubblica su portale Appalti' onclick="javascript:pubblicaSuPortaleAppalti();">
				</td>
			</tr>
		</c:if>
	<gene:redefineInsert name="addToAzioni" >
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.PubblicaSuPortale")}'>
			<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:pubblicaSuPortaleAppalti()" title="Pubblica su portale Appalti" tabindex="1505">
					Pubblica su portale Appalti
					</a>
				</td>
			</tr>
		</c:if>
		<c:if test='${gruppo ne 3 and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InviaAttiSCP") and not empty integrazioneSCP}'>
			<tr>
				<td class="vocemenulaterale" >
					<a href="javascript:inviaAttiScp()" title="Invia dati a SCP" tabindex="1505">
					Invia dati a SCP
					</a>
				</td>
			</tr>
		</c:if>
	</gene:redefineInsert>
	</c:if>
	<gene:redefineInsert name="addToDocumenti" >
		<c:if test='${autorizzatoModifiche ne "2" and modoAperturaScheda eq "VISUALIZZA" and (integrazioneWSERP eq "1" and (tipoWSERP eq "SMEUP" || tipoWSERP eq "UGOVPA" || tipoWSERP eq "AVM") and visAllegatiRda eq "true")}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:visAllegatiRda('${chiaveRda}');" title='Visualizza documenti allegati RdA' tabindex="1508">
						Visualizza documenti allegati RdA
					</a>
				</td>
			</tr>
		</c:if>
	</gene:redefineInsert>
</gene:campoScheda>

<gene:redefineInsert name="head">

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/themes/default/style.min.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jstree.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jstree.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.alphanum.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.character.js"></script>

<style type="text/css">
	#jstree2 .even > .jstree-wholerow { background:none;transition: 0.3s; } 
	#jstree2 .odd > .jstree-wholerow { background:none;transition: 0.3s; } 
	#jstree2 .even > .jstree-search{font-style:normal;color:black;font-weight:normal}
	#jstree2 .odd > .jstree-search{font-style:normal;color:black;font-weight:normal}
	#jstree2 .jstree-search{font-style:normal;color:black;font-weight:normal}
	
	#jstree2 .even.jstree-leaf{background:#E7F1FF;}
	#jstree2 .odd.jstree-leaf{background:#CEDAEB;}
	
	#jstree2 .even.jstree-leaf{padding-left:10px;}
	#jstree2 .odd.jstree-leaf{padding-left:10px;}
	
	#jstree2 .jstree-leaf{transition: 0.3s;}
	#jstree2 .jstree-leaf:hover{background:#7A91E6;}
	
	#jstree2 .even { background:none; }
	#jstree2 .odd  { background:none; }
	#jstree2 .even > .jstree-search{font-style:normal;color:black;font-weight:normal}
	#jstree2 .odd > .jstree-search{font-style:normal;color:black;font-weight:normal}
	
	#jstree2 .jstree-icon{width:0px;}
	#jstree2 .jstree-anchor img {padding:5px;padding-right:6px;}
	
	.jstree-default .jstree-anchor{
		line-height: 14px; 
		padding: 7 0 7 0;
		height: auto !important;
	}
	
	#jstree2 .jstree-anchor { height:auto !important; white-space:normal !important; width:100%; }
	
	#jstree2, .demo { max-width:100%; overflow:auto; box-shadow:0 0 5px #ccc; padding:10px; border-radius:5px; }
	
	SPAN.mcontatore {
	font: 10px Verdana, Arial, Helvetica, sans-serif;
	font-weight: bold;
	color: #FFFFFF;
	border: 1px solid #D30000;
	background-color: #D30000;
	padding-left: 2px;
	padding-right: 2px;
	float: right;
	-moz-border-radius-topleft: 2px; 
	-webkit-border-top-left-radius: 2px; 
	-khtml-border-top-left-radius: 2px; 
	border-top-left-radius: 2px; 
	-moz-border-radius-topright: 2px;
	-webkit-border-top-right-radius: 2px;
	-khtml-border-top-right-radius: 2px;
	border-top-right-radius: 2px;
	-moz-border-radius-bottomleft: 2px; 
	-webkit-border-bottom-left-radius: 2px; 
	-khtml-border-bottom-left-radius: 2px; 
	border-bottom-left-radius: 2px; 
	-moz-border-radius-bottomright: 42px;
	-webkit-border-bottom-right-radius: 2px;
	-khtml-border-bottom-right-radius: 2px;
	border-bottom-right-radius: 2px;
	}
</style>
	
</gene:redefineInsert>

<gene:javaScript>

document.forms[0].encoding="multipart/form-data";
				
$(function () {
	$('#jstree2').jstree({
		'plugins':["wholerow","search","ui"], 
		'core' : {
			"themes" : { "stripes" : true,
						 "icons" : false},
			'data' : ${dati}
				},
		"search": {
			'case_insensitive': true,
			'show_only_matches': true
			}
	});
	
	$('#radioTutte').click(function () {
		if ($(this).is(':checked')) {
			$('#jstree2').jstree(true).search('');
		}
	});
});

$("#jstree2").bind('ready.jstree', function(event, data) {
});

$('#jstree2').on('select_node.jstree', function (e, data) {
	data.instance.toggle_node(data.node);
});

$("#jstree2").bind("select_node.jstree", function (e, data) {
	 var href = data.node.a_attr.href;
	 document.location.href = href;
});

<c:choose>
	<c:when test="${genere eq 10 or genere eq 11 or genere eq 20}">
	function inviaAttiScp(){
		openPopUpCustom("href=gare/documgara/popupInviaDatiSCP.jsp?codiceGara=${codiceGara}&genere=${genere}&ngara=${numeroGara}", "inviaDatiSCP", 600, 350, "yes", "yes");
	}
	</c:when>
	<c:otherwise>
	function inviaAttiScp(){
		document.formInviaAttiSCP.submit();
	}
	</c:otherwise>
</c:choose>

</gene:javaScript>