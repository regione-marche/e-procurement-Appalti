<%
/*
 * Created on 03-06-2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 //PAGINA CHE CONTIENE LA PERSONALIZZAZIONE DEI JAVASCRIPT.
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${fn:containsIgnoreCase(campoChiave, "CODGAR")}'>
		<c:set var="isProceduraTelematica" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsProceduraTelematicaFunction", pageContext, valoreChiave)}'/>
	</c:when>
	<c:otherwise>
		<c:set var="isProceduraTelematica" value='false'/>
	</c:otherwise>
</c:choose>
<c:if test='${fn:containsIgnoreCase(campoChiave, "IDMERIC") || isProceduraTelematica eq "true"}'>
	<c:set var="valoreTabA1137" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1137","1","true")}'/>
</c:if>

<script type="text/javascript">
<c:choose>
<c:when test='${fn:containsIgnoreCase(campoChiave, "IDMERIC") || isProceduraTelematica eq "true"}'>
	var controlloRiga_Default = controlloRiga;
	
	function controlloRiga_Custom(idRiga){
		controlloRiga_Default(idRiga);
		var idRuolo = "ruoloSelezionato" + idRiga;
		var idAssocia = "associa" + idRiga;
		var objCheckAssocia = document.getElementById("associa"+idRiga);
		if(objCheckAssocia.checked){
			var indice = document.getElementById(idRuolo).selectedIndex;
			if(indice==0){
				document.getElementById(idRuolo).disabled = false;
				//document.getElementById("autoriz" + idRiga).disabled = true;
			}else{
				var ruoloUsrsys = document.getElementById("meruoloUsrsys" + idRiga).value;
				if(ruoloUsrsys!= 1)
					document.getElementById(idRuolo).disabled = true;
			}
			setRuolo(idRiga);
		}else {
			document.getElementById(idRuolo).disabled = true;
		}
	}
	
	controlloRiga =   controlloRiga_Custom;
	
	function setRuolo(idRiga){
		var idRuolo = "ruoloSelezionato" + idRiga;
		var indice = document.getElementById(idRuolo).selectedIndex;
		if(indice==0){
			var objAutorizzazione = document.getElementById("autorizzazione" + idRiga);
			objAutorizzazione.value = 1;
			document.getElementById("autoriz" + idRiga).disabled = true;
			var objAutoriz = document.getElementById("autoriz" + idRiga);
			objAutoriz.value=1;
		}else{
			document.getElementById("autoriz" + idRiga).disabled = false;
		}
		var objRuoloSelezionato = document.getElementById(idRuolo);
		var objRuolo = document.getElementById("ruolo" + idRiga);
		objRuolo.value = objRuoloSelezionato.options[objRuoloSelezionato.selectedIndex].value;
	}
	
	function salvaModifiche(){
		//Si deve controllare che vi sia un solo ruolo "Punto ordinante"
		var len = document.permessiAccountEntitaForm.associa.length;
		var puntiOrdinante=0;
		var applicazioneControllo = "${valoreTabA1137}";
		if(applicazioneControllo=="1"){
			for(var i=0; i < len; i++){
				var objCheckAssocia = document.getElementById("associa"+i);
				if(objCheckAssocia.checked){
					var indice = document.getElementById("ruoloSelezionato" + i).selectedIndex;
					if(indice==0)
						puntiOrdinante++;
				}
			}
		}
		if(puntiOrdinante>1){
			alert("Il ruolo di 'Punto ordinante' può essere assegnato a un solo utente");
		}else{
			bloccaRichiesteServer();
			document.permessiAccountEntitaForm.submit();	
		}
		
  	}
	
	function init(){
		var len = document.permessiAccountEntitaForm.associa.length;
		for(var i=0; i < len; i++){
			controlloRiga(i);
		}
	}
	</c:when>
	<c:otherwise>
		function init(){
		
		}
	</c:otherwise>
</c:choose>
	
	
	
	
</script>



