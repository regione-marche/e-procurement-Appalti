<%
/*
 * Created on: 28/10/2008
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="ID_${param.contatore}" entita="GARATTIAGG" campoFittizio="true" visibile="false" definizione="N12;1;;;G1IDAA" value="${item[0]}" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="GARATTIAGG" campoFittizio="true" visibile="false" definizione="T21;1;;;G1NGARAAA" value="${item[1]}" />
		<gene:campoScheda campo="NCONT_${param.contatore}" entita="GARATTIAGG" campoFittizio="true" visibile="false" definizione="N3;1;;;G1NCONTAA" value="${item[2]}" />
		<gene:campoScheda campo="TIATTO_${param.contatore}" entita="GARATTIAGG" campoFittizio="true" definizione="N7;;A1124;;G1TIATTOAA" value="${item[6]}" />
		<gene:campoScheda campo="NREPAT_${param.contatore}" entita="GARATTIAGG" campoFittizio="true" definizione="T20;;;;G1NREPATAA" value="${item[3]}" />
		<gene:campoScheda campo="DAATTO_${param.contatore}" entita="GARATTIAGG" campoFittizio="true" definizione="D;0;;;G1DAATTOAA" value="${item[4]}" />
		<gene:campoScheda campo="NIMPCO_${param.contatore}" entita="GARATTIAGG" campoFittizio="true" definizione="F15;0;;MONEY;G1NIMPCOAA" value="${item[5]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="ID_${param.contatore}" entita="GARATTIAGG" campoFittizio="true" visibile="false" definizione="N12;1;;;G1IDAA" />
		<gene:campoScheda campo="NGARA_${param.contatore}" entita="GARATTIAGG" campoFittizio="true" visibile="false" definizione="T21;1;;;G1NGARAAA" value='${fn:substringBefore(param.chiave,";")}'/>
		<gene:campoScheda campo="NCONT_${param.contatore}" entita="GARATTIAGG" campoFittizio="true" visibile="false" definizione="N3;1;;;G1NCONTAA" value='${fn:substringAfter(param.chiave, ";")}'/>
		<gene:campoScheda campo="TIATTO_${param.contatore}" entita="GARATTIAGG" campoFittizio="true" definizione="N7;;A1124;;G1TIATTOAA" />
		<gene:campoScheda campo="NREPAT_${param.contatore}" entita="GARATTIAGG" campoFittizio="true" definizione="T20;;;;G1NREPATAA" />
		<gene:campoScheda campo="DAATTO_${param.contatore}" entita="GARATTIAGG" campoFittizio="true" definizione="D;0;;;G1DAATTOAA" />
		<gene:campoScheda campo="NIMPCO_${param.contatore}" entita="GARATTIAGG" campoFittizio="true" definizione="F15;0;;MONEY;G1NIMPCOAA" />
	</c:otherwise>
</c:choose>
<gene:fnJavaScriptScheda funzione='calcolaImportoTotale(${param.contatore })' elencocampi='GARATTIAGG_NIMPCO_${param.contatore}' esegui="false" />

<gene:javaScript>

	function calcolaImportoTotale(indice){
		var totImporto=0;
		for(var i=1; i < maxIdGARATTIAGGVisualizzabile ; i++){
			if(isObjShow("rowGARATTIAGG_NIMPCO_" + i)){
				var nimpco =  getValue("GARATTIAGG_NIMPCO_" + i);
				if(nimpco==null || nimpco=="")
					nimpco =0;
				totImporto += parseFloat(nimpco);	
			}
		}

		var iaggiu = 0;
		if(isObjShow("rowGARE_IAGGIU"))
			iaggiu = getValue("GARE_IAGGIU");
		if(iaggiu==null || iaggiu=="")
			iaggiu =0;
		iaggiu = parseFloat(iaggiu);	

		var impagg = 0;
		if(isObjShow("rowIMPAGG"))
			impagg = getValue("IMPAGG");
		if(impagg==null || impagg=="")
			impagg =0;
		impagg = parseFloat(impagg);	
		
		setValue("IMPNETTOCONT", round(iaggiu + + impagg+ totImporto,2));
	}
	
		//Customizzazione della funzione delElementoSchedaMultipla 
 	function delAttoAgg(id, label, tipo, campi){
		delElementoSchedaMultipla(id,label,tipo,campi);
		var totImporto=0;
		for(var i=1; i < maxIdGARATTIAGGVisualizzabile ; i++){
			if(isObjShow("rowGARATTIAGG_NIMPCO_" + i)){
				var nimpco =  getValue("GARATTIAGG_NIMPCO_" + i);
				if(nimpco==null || nimpco=="")
					nimpco =0;
				totImporto += parseFloat(nimpco);	
			}
		}
		
		var iaggiu = 0;
		if(isObjShow("rowGARE_IAGGIU"))
			iaggiu = getValue("GARE_IAGGIU");
		if(iaggiu==null || iaggiu=="")
			iaggiu =0;
		iaggiu = parseFloat(iaggiu);	

		var impagg = 0;
		if(isObjShow("rowIMPAGG"))
			impagg = getValue("IMPAGG");
		if(impagg==null || impagg=="")
			impagg =0;
		impagg = parseFloat(impagg);	
		
		setValue("IMPNETTOCONT", round(iaggiu + + impagg+ totImporto,2));

	}
	
	
</gene:javaScript>	