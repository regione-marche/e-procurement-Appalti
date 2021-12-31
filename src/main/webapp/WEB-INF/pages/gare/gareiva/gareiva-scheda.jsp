<%
/*
 * Created on: 19/12/2013
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda addTr="false">
				<tr id="rowGAREIVA_ID_${param.contatore}">
			</gene:campoScheda>
		<gene:campoScheda addTr="false" campo="ID_${param.contatore}" entita="GAREIVA" campoFittizio="true" visibile="false" definizione="N12;1" value="${item[0]}" />
		<gene:campoScheda addTr="false" campo="NGARA_${param.contatore}" entita="GAREIVA" campoFittizio="true" visibile="false" definizione="T20;0" value="${item[1]}" />
		<gene:campoScheda addTr="false" campo="NCONT_${param.contatore}" entita="GAREIVA" campoFittizio="true"  visibile="false" definizione="N3;0" value="${item[2]}" />
		<gene:campoScheda addTr="false" campo="PERCIVA_${param.contatore}" entita="GAREIVA" hideTitle="true" campoFittizio="true" obbligatorio="true" definizione="N7;0;G_055;;G1PERCIVA" value="${item[3]}" />
		<gene:campoScheda addTr="false" campo="IMPONIB_${param.contatore}" entita="GAREIVA" hideTitle="true" campoFittizio="true" obbligatorio="true"  definizione="F25.5;0;;MONEY;G1IMPONIB" value="${item[4]}" />
		<gene:campoScheda addTr="false" campo="IMPIVA_${param.contatore}" entita="GAREIVA" hideTitle="true" campoFittizio="true"   definizione="F24.5;0;;MONEY;G1IMPIVA" value="${item[5]}"/>
		<gene:campoScheda addTr="false">
				<td id="elimina_${param.contatore}"></td>
			</tr>
		</gene:campoScheda>
	</c:when>
	<c:otherwise>
		<gene:campoScheda addTr="false">
				<tr id="rowGAREIVA_ID_${param.contatore}">
			</gene:campoScheda>
		<gene:campoScheda addTr="false" campo="ID_${param.contatore}" entita="GAREIVA" campoFittizio="true" visibile="false" definizione="N12;1"  />
		<gene:campoScheda addTr="false" campo="NGARA_${param.contatore}" entita="GAREIVA" campoFittizio="true" visibile="false" definizione="T20;0"  value="${param.chiave }"/>
		<gene:campoScheda addTr="false" campo="NCONT_${param.contatore}" entita="GAREIVA" campoFittizio="true"  visibile="false" definizione="N3;0"  value="1"/>
		<gene:campoScheda addTr="false" campo="PERCIVA_${param.contatore}" entita="GAREIVA" hideTitle="true" campoFittizio="true"  obbligatorio="true" definizione="N7;0;G_055;;G1PERCIVA"  />
		<gene:campoScheda addTr="false" campo="IMPONIB_${param.contatore}" entita="GAREIVA" hideTitle="true" campoFittizio="true" obbligatorio="true"  definizione="F25.5;0;;MONEY;G1IMPONIB"  />
		<gene:campoScheda addTr="false" campo="IMPIVA_${param.contatore}" entita="GAREIVA" hideTitle="true" campoFittizio="true"   definizione="F24.5;0;;MONEY;G1IMPIVA"  />
		<gene:campoScheda addTr="false">
				<td id="elimina_${param.contatore}"></td>
			</tr>
		</gene:campoScheda>
	</c:otherwise>
</c:choose>
<gene:fnJavaScriptScheda funzione='impostaImporto("#GAREIVA_PERCIVA_${param.contatore}#","#GAREIVA_IMPONIB_${param.contatore}#",${param.contatore })' elencocampi='GAREIVA_PERCIVA_${param.contatore};GAREIVA_IMPONIB_${param.contatore}' esegui="false" />

<gene:javaScript>

	<c:if test="${modo ne 'VISUALIZZA'}">
		$('#elimina_${param.contatore}').append($('#rowtitoloGAREIVA_${param.contatore}'));
	</c:if>
	
	function impostaImporto(perciva,imponib,indice){
		if(perciva==null || perciva=="") 
			perciva=0;
		if(imponib==null || imponib=="") 
			imponib=0;
		if(perciva!=0){
			var valoreIva = $('#GAREIVA_PERCIVA_' + indice + '>option:selected').text();
         	perciva = parseFloat(valoreIva);
        }
		var importo= imponib * perciva /100;
		importo = round(importo,2)
		setValue("GAREIVA_IMPIVA_" + indice, importo);
		
		var totIva=0;
		var totImporto=0;
		for(var i=1; i < maxIdGAREIVAVisualizzabile ; i++){
			if(isObjShow("rowGAREIVA_ID_" + i)){
				var impiva =  getValue("GAREIVA_IMPIVA_" + i);
				if(impiva==null || impiva=="")
					impiva =0;
				totIva += parseFloat(impiva);	
			}
		}
		setValue("GARECONT_IMPIVA", round(totIva,2));
		var iaggiu = getValue("GARE_IAGGIU");
		if(iaggiu==null || iaggiu=="")
			iaggiu =0;
		iaggiu = parseFloat(iaggiu);	
		setValue("GARECONT_IMPTOT", round(iaggiu + totIva,2));
	}
	
	//Customizzazione della funzione delElementoSchedaMultipla 
 	function delIva(id, label, tipo, campi){
		delElementoSchedaMultipla(id,label,tipo,campi);
		var totIva=0;
		var totImporto=0;
		for(var i=1; i < maxIdGAREIVAVisualizzabile ; i++){
			if(isObjShow("rowtitoloGAREIVA_" + i)){
				var impiva =  getValue("GAREIVA_IMPIVA_" + i);
				if(impiva==null || impiva=="")
					impiva =0;
				totIva += parseFloat(impiva);
			}
		}
		setValue("GARECONT_IMPIVA", round(totIva,2));
		var iaggiu = getValue("GARE_IAGGIU");
		if(iaggiu==null || iaggiu=="")
			iaggiu =0;
		iaggiu = parseFloat(iaggiu);	
		setValue("GARECONT_IMPTOT", round(iaggiu + totIva,2));
	}
	
	<c:if test='${!(modo eq "VISUALIZZA")}'>
		$('[id^="GAREIVA_IMPIVA_"]').attr('disabled',true);
		$('[id^="GAREIVA_IMPIVA_"]').css('background-color','#ECECEC');
		
		/*
		$('$[id^="GAREIVA_IMPONIB_"]').attr('size','12');
		$('$[id^="GAREIVA_IMPIVA_"]').attr('size','12');
		//Diminuisco la larghezza delle classi="etichetta-dato"
		$('$[id^="rowGAREIVA_ID_"]').find( "td.etichetta-dato" ).css('width', '120');		
		*/
		
		
    </c:if>
</gene:javaScript>



