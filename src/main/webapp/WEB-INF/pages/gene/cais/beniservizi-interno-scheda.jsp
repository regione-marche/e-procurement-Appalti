<%
	/*
	 * Created on 29-Mar-2012
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>

<c:set var="descat" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.GetDescatCaisFunction", pageContext, param.chiave)}' />

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda addTr="false">
			<tr id="rowT_UBUY_BENISERVIZI_NUM_BS_${param.contatore}">
		</gene:campoScheda>
		<gene:campoScheda addTr="false" title="Categoria" entita="T_UBUY_BENISERVIZI" campo="CODCAT_${param.contatore}" campoFittizio="true" visibile="false" definizione="T30;1" value="${item[0]}"/>
		<gene:campoScheda addTr="false" title="Num. Bene/Servizio" entita="T_UBUY_BENISERVIZI" campo="NUM_BS_${param.contatore}" 
			campoFittizio="true" visibile="false" definizione="N12;1;;;" value="${item[1]}"/>
		<gene:campoScheda addTr="false" title="Bene/Servizio" entita="T_UBUY_BENISERVIZI" campo="COD_BS_${param.contatore}" 
			campoFittizio="true" visibile="true" definizione="T5;;;;" value="${item[2]}"/>
		<gene:campoScheda addTr="false" hideTitle ="true" title="Descrizione" entita="T_UBUY_BENISERVIZI" campo="DES_BS_${param.contatore}"  campoFittizio="true" visibile="true" definizione="T500;0;;" value="${item[3]}"/>
		<gene:campoScheda addTr="false" title="Descrizione cat" entita="T_UBUY_BENISERVIZI" campo="DESEST_CAT_${param.contatore}"  campoFittizio="true" visibile="false" definizione="T2500;0;;" value="${item[4]}"/>
		<gene:campoScheda addTr="false">
				<td id="elimina_${param.contatore}"></td>
			</tr>
		</gene:campoScheda>
	</c:when>
	<c:otherwise>
		<gene:campoScheda addTr="false">
			<tr id="rowT_UBUY_BENISERVIZI_NUM_BS_${param.contatore}">
		</gene:campoScheda>
		<gene:campoScheda addTr="false" title="Categoria" entita="T_UBUY_BENISERVIZI" campo="CODCAT_${param.contatore}" campoFittizio="true" visibile="false" definizione="T30;1" value="${param.chiave}"/>
		<gene:campoScheda addTr="false" title="Num. Bene/Servizio" entita="T_UBUY_BENISERVIZI" campo="NUM_BS_${param.contatore}" campoFittizio="true" visibile="false" definizione="N12;1;;;" />
		<gene:campoScheda addTr="false" title="Bene/Servizio" entita="T_UBUY_BENISERVIZI" campo="COD_BS_${param.contatore}" campoFittizio="true" visibile="true" definizione="T5;;;;" />
		<gene:campoScheda addTr="false" hideTitle ="true" title="Descrizione" entita="T_UBUY_BENISERVIZI" campo="DES_BS_${param.contatore}" campoFittizio="true" visibile="true" definizione="T500;0;;" />
		<gene:campoScheda addTr="false" title="Descrizione cat" entita="T_UBUY_BENISERVIZI" campo="DESEST_CAT_${param.contatore}" campoFittizio="true" visibile="false" definizione="T2500;0;;" value="${descat}"/>
		<gene:campoScheda addTr="false">
				<td id="elimina_${param.contatore}"></td>
			</tr>
		</gene:campoScheda>
	</c:otherwise>
</c:choose>

<gene:javaScript>

	
	<c:if test="${modo ne 'VISUALIZZA'}">
		$('#elimina_${param.contatore}').append($('#rowtitoloBENISERVIZI_${param.contatore}'));
	</c:if>
	
	
	$("#T_UBUY_BENISERVIZI_COD_BS_${param.contatore}").autocomplete({
		delay: 0,
	    autoFocus: true,
	    position: { 
	    	my : "left top",
	    	at: "left bottom"
	    },
		source: function( request, response ) {
			$("#T_UBUY_BENISERVIZI_DES_BS_${param.contatore}view").html("");
			var codbs = $("#T_UBUY_BENISERVIZI_COD_BS_${param.contatore}").val();
			$.ajax({
				async: false,
			    type: "GET",
                dataType: "json",
                beforeSend: function(x) {
	       			if(x && x.overrideMimeType) {
	           			x.overrideMimeType("application/json;charset=UTF-8");
				       }
	   			},
                url: "${pageContext.request.contextPath}/pg/GetListaBeniServizi.do",
                data: "codbs=" + codbs,
				success: function( data ) {
					if (!data) {
						response([]);
					} else {
						response( $.map( data, function( item ) {
							return {
								label: item[0].value + " (" + item[1].value + ") " ,
								value: item[0].value,
								valueDES_BS: item[1].value
							}
						}));
					} 
				},
                error: function(e){
                   alert("Codice bene/servizio: errore durante la lettura della lista dei beni/servizi ");
                }
			});
		},
		minLength: 1,
		select: function( event, ui ) {
			$("#T_UBUY_BENISERVIZI_DES_BS_${param.contatore}").val(ui.item.valueDES_BS);
		},
		change: function(event, ui) {
			var codbs = $("#T_UBUY_BENISERVIZI_COD_BS_${param.contatore}").val();
			$.ajax({
				async: false,
			    type: "GET",
                   dataType: "json",
                   beforeSend: function(x) {
       			if(x && x.overrideMimeType) {
           			x.overrideMimeType("application/json;charset=UTF-8");
			       }
   				},
                url: "${pageContext.request.contextPath}/pg/GetListaBeniServizi.do",
                data: "codbs=" + codbs,
				success: function( data ) {
					if (!data) {
						$("#T_UBUY_BENISERVIZI_COD_BS_${param.contatore}").val("");
						$("#T_UBUY_BENISERVIZI_DES_BS_${param.contatore}").html("");
					} 
				},
				error: function(e){
						$("#T_UBUY_BENISERVIZI_COD_BS_${param.contatore}").val("");
						$("#T_UBUY_BENISERVIZI_DES_BS_${param.contatore}").html("");
                }
			});
		}
	});
	
	
			

	
</gene:javaScript>
