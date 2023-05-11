<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${contextPath}/js/jquery.documenti.gara.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/common-gare.js"></script>

<c:choose>
	<c:when test='${!empty param.codiceGara}'>
		<c:set var="codiceGara" value="${param.codiceGara}" />
	</c:when>
	<c:otherwise>
		<c:set var="codiceGara" value="${codiceGara}" />
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

<c:set var="key" value="TORN.CODGAR=T:${codiceGara}" scope="request"/>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="ANTICOR-scheda">
<gene:setString name="titoloMaschera" value='Invio dati a SCP' />
<gene:redefineInsert name="corpo">
	<gene:formScheda entita="TORN" >
	<c:set var="dati" value='${gene:callFunction3("it.eldasoft.sil.pg.tags.funzioni.GetJsonTipologieDaInviareSCPFunction", pageContext,codiceGara,genere)}'></c:set>
	<gene:redefineInsert name="addHistory">
		<c:if test="${modo eq 'VISUALIZZA'}" > 
			<gene:historyAdd titolo="Invio dati a SCP" id="Invio dati a SCP" />
		</c:if>
	</gene:redefineInsert>	
	
	<span id="didascaliaScp">
		<br>Mediante questa funzione si procede all'invio e pubblicazione degli atti di gara sul sito Servizio Contratti Pubblici.
		<br>Confermando l'operazione, verranno inviati a SCP tutti gli atti etichettati come 'da inviare'. 
	</span>
	
	<gene:redefineInsert name="schedaModifica"/>
	<gene:redefineInsert name="pulsanteModifica"/>
	<gene:redefineInsert name="schedaNuovo"/>
	<gene:redefineInsert name="addToAzioni" >
	</gene:redefineInsert>
	<gene:redefineInsert name="noteAvvisi"/>
	<gene:redefineInsert name="documentiAssociati"/>
	<gene:redefineInsert name="modelliPredisposti"/>
	<gene:campoScheda campo="CODGAR"  visibile="false" />
	<gene:campoScheda>
	<c:choose>
		<c:when test="${dati ne '[]'}">
			<td colspan="2"><b>&nbsp;</b>
				<br>
				<div id="jstree2" class="demo"></div>
			</td>		
		</c:when>
		<c:otherwise>
			<td colspan="2"><b>Non &egrave; possibile effettuare pubblicazioni</b></td>
		</c:otherwise>
	</c:choose>
	</gene:campoScheda>
	<gene:campoScheda>
		<tr class="comandi-dettaglio">
			<td colspan="2">
				<INPUT type="button"  class="bottone-azione" value="Procedi all'invio dati a SCP" title="Procedi all'invio dati a SCP" onclick="javascript:inviaAtti();">
			</td>
			<gene:redefineInsert name="addToAzioni" >
				<tr>
					<td class="vocemenulaterale" >
						<a href="javascript:inviaAtti()" title="Procedi all'invio dati a SCP" tabindex="1505">
						Procedi all'invio dati a SCP
					</td>
				</tr>
			</gene:redefineInsert>
		</tr>
	</gene:campoScheda>

	</gene:formScheda>
</gene:redefineInsert>
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
	#jstree2 .even.jstree-closed{background:#E7F1FF;}
	#jstree2 .odd.jstree-closed{background:#CEDAEB;}
	#jstree2 .even.jstree-open{background:#E7F1FF;}
	#jstree2 .odd.jstree-open{background:#CEDAEB;}
	
	#jstree2 .even{padding-left:10px;}
	#jstree2 .odd{padding-left:10px;}
	
	#jstree2 .jstree-leaf{transition: 0.3s;}
	#jstree2 .jstree-leaf:hover{background:#7A91E6;}
	
	#jstree2 .jstree-closed{transition: 0.3s;}
	#jstree2 .jstree-closed:hover{background:#7A91E6;}
	
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
	
	#jstree2 .child:hover{background:none;}
	#jstree2 .child a:hover{background:none;}
	#jstree2 .child .jstree-hovered{background:none;}
	#jstree2 .child .jstree-wholerow-hovered{background:none;}
	#jstree2 .child .jstree-wholerow-clicked{background:none;}
	
	#jstree2, .demo { max-width:100%; overflow:auto; box-shadow:0 0 5px #ccc; padding:10px; border-radius:5px; }
	
	#jstree2 .jstree-leaf a{cursor: default !important;}
	
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

function inviaAtti(){
	openPopUpCustom("href=gare/documgara/popupInviaDatiSCP.jsp?codiceGara=${codiceGara}&genere=${genere}&ngara=${param.ngara}", "inviaDatiSCP", 600, 350, "yes", "yes");
}

function doNothing(){
}

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
	 var href = data.node.a_attr.href + "&" + csrfToken;
	 document.location.href = href;
});
		
</gene:javaScript>
</gene:template>