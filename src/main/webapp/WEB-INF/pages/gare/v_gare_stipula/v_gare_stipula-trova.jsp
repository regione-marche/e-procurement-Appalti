<%/*
       * Created on 15-apr-2021
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
<% // i filtri applicati sono il filtro sul livello utente ed il filtro sul codice profilo %>
<c:set var="filtroLivelloUtente"
	value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "V_GARE_STIPULA")}' />


<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="V_GARE_STIPULA-trova">
	<gene:setString name="titoloMaschera" value="Ricerca stipule contratti"/>
	
	<gene:redefineInsert name="trovaCreaNuovo">
	</gene:redefineInsert>
	<% // Ridefinisco il corpo della ricerca %>
	<gene:redefineInsert name="corpo">
  	<% // Creo la form di trova con i campi dell'entità %>
  	<gene:formTrova entita="V_GARE_STIPULA" filtro="${filtroLivelloUtente}" gestisciProtezioni="true" >
  		<gene:campoTrova campo="CODSTIPULA"/>
  		<gene:campoTrova campo="NGARA"/>
  		<gene:campoTrova campo="CODCIG"/>
  		<gene:campoTrova campo="OGGETTO"/>
  		<gene:campoTrova campo="NREPAT"/>
  		<gene:campoTrova campo="DAATTO"/>
  		<gene:campoTrova campo="CODIMP"/>
  		<gene:campoTrova campo="NOMEST"/>
  		<gene:campoTrova campo="STATO"/>
		<gene:campoTrova campo="NOME_CREATORE" title="Creato da"/>
		<gene:campoTrova campo="NOME_CONTRACT" />
		<gene:campoTrova campo="IMPSTIPULA"/>
		<gene:campoTrova id="G1STIPULA_CARTELLA" entita="G1STIPULA" campo="CARTELLA" where="G1STIPULA.ID=V_GARE_STIPULA.ID"/>
		<gene:campoTrova campo="ISARCHI" defaultValue="2"/>
	</gene:formTrova>    
  </gene:redefineInsert>
  
<gene:javaScript>

	$("#G1STIPULA_CARTELLA").keyup(function () {  
		$(this).val($(this).val().toUpperCase());  
	});

	$("#G1STIPULA_CARTELLA").autocomplete({

		delay: 0,
	    autoFocus: true,
	    position: { 
	    	my : "left top",
	    	at: "left bottom"
	    },
		source: function( request, response ) {
			var folder = $("#G1STIPULA_CARTELLA").val().toUpperCase();
			$.ajax({
				async: false,
			    type: "GET",
                dataType: "json",
                beforeSend: function(x) {
	       			if(x && x.overrideMimeType) {
	           			x.overrideMimeType("application/json;charset=UTF-8");
				       }
	   			},
                url: "${pageContext.request.contextPath}/pg/GetListaCartelleStipula.do",
                data: "folder=" + folder,
				success: function( data ) {
					if (!data) {
						response([]);
					} else {
						response( $.map( data, function( item ) {
							return {
								label: item[0].value,
								value: item[0].value,
							}
						}));
					} 
				},
                error: function(e){
                   alert("Cartella di archiviazione: errore durante la lettura della lista delle cartelle attuali");
                }
			});
		},
		minLength: 1,
		select: function( event, ui ) {
			$("#G1STIPULA_CARTELLA").val(ui.item.value);
		},
		change: function(event, ui) {
			var folder = $("#G1STIPULA_CARTELLA").val();
			$.ajax({
				async: false,
			    type: "GET",
                   dataType: "json",
                   beforeSend: function(x) {
       			if(x && x.overrideMimeType) {
           			x.overrideMimeType("application/json;charset=UTF-8");
			       }
   				},
                url: "${pageContext.request.contextPath}/pg/GetListaCartelleStipula.do",
                data: "folder=" + folder,
				success: function( data ) {
					if (!data) {
						$("#G1STIPULA_CARTELLA").val("");
						$("#G1STIPULA_CARTELLA").html("");
					} 
				},
				error: function(e){
						$("#G1STIPULA_CARTELLA").val("");
						$("#G1STIPULA_CARTELLA").html("");
                }
			});
		}
	});
	
</gene:javaScript>  
  
</gene:template>

