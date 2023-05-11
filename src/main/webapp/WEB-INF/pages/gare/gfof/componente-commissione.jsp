<%
/*
 * Created on: 24/11/2008
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

<c:set var="giudizioEspresso" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsGiudizioEspressoComponenteFunction", pageContext, item[14])}' scope="request"/>
<c:set var="isAlboCommissioneCollegato" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsAlboCommissioneCollegatoFunction", pageContext, key)}' scope="request"/>
<c:choose>
	<c:when test='${isAlboCommissioneCollegato eq "true"}'>
	<c:set var="isModificabile" value="false" />
	</c:when>
	<c:otherwise>
	<c:set var="isModificabile" value="true" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${item[16] eq "1"}'>
	<c:set var="bloccoCommicg" value="true" />
	</c:when>
	<c:otherwise>
	<c:set var="bloccoCommicg" value="false" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda campo="NGARA2_${param.contatore}" entita="GFOF" campoFittizio="true" visibile="false" definizione="T21;1;;;NGARA_FUN" value="${item[0]}" />
		<gene:archivio titolo="tecnici"
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.TECNI.CODTEC"), "gene/tecni/tecni-lista-popup.jsp", "")}'
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
			campi="TECNI.CODTEC;TECNI.NOMTEC"
			functionId="skip"
			chiave="GFOF_CODFOF_${param.contatore}"
			formName="formComponenteCommissione${param.contatore}"
			inseribile="${isCollegamentoAlbo}">
			<gene:campoScheda campo="CODFOF_${param.contatore}" entita="GFOF" campoFittizio="true" modificabile="${isModificabile and !giudizioEspresso and !bloccoCommicg}" obbligatorio="true" definizione="T10;1;;;CODFOF" value="${item[1]}" />
			<gene:campoScheda campo="NOMFOF_${param.contatore}" entita="GFOF" campoFittizio="true" modificabile="${isModificabile and !giudizioEspresso and !bloccoCommicg}" definizione="T61;0;;;NOMFOF" value="${item[2]}" /> 
		</gene:archivio>
		<gene:campoScheda campo="INCFOF_${param.contatore}" entita="GFOF" campoFittizio="true" modificabile="${isModificabile and !(param.obbligoPresidente eq '1' && bloccoCommicg eq 'true')}" definizione="N2;1;A1001;;INCFOF" value="${item[3]}" obbligatorio="true" />
		<gene:campoScheda campo="NUMCOMM_${param.contatore}" entita="GFOF" campoFittizio="true" modificabile="${isModificabile}" visibile="false" definizione="N3;0;;;NUMCOMMFOF" value="1" />
		<gene:campoScheda campo="INTFOF_${param.contatore}" entita="GFOF" campoFittizio="true" modificabile="${isModificabile}" definizione="T2;0;;SN;G1INTFOF" value="${item[4]}" obbligatorio="true" />
		<gene:campoScheda campo="IMPFOF_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="F15;0;;MONEY;G1IMPFOF" value="${item[5]}"  />
		<gene:campoScheda campo="IMPLIQ_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="F15;0;;MONEY;G1IMPLIQ_F" value="${item[6]}" />
		<gene:campoScheda campo="IMPSPE_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="F15;0;;MONEY;G1IMPSPE" value="${item[7]}" />
		<gene:campoScheda campo="DLIQSPE_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="T10;0;;DATA_ELDA;G1DLIQSPE" value="${item[8]}" />
		<gene:campoScheda campo="INDISPONIBILITA_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="T2;0;;SN;INDISPFOF" value="${item[9]}" visibile="${isAlboCommissioneCollegato eq 'true'}"/>
		<gene:campoScheda campo="MOTIVINDISP_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="T2000;0;;NOTE;MOTINDFOF" value="${item[10]}"  visibile="${isAlboCommissioneCollegato eq 'true'}"/>
		<gene:campoScheda campo="DATARICHIESTA_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="T10;0;;DATA_ELDA;DRICHFOF" value="${item[11]}" visibile="${isAlboCommissioneCollegato eq 'true'}"/>
		<gene:campoScheda campo="DATAACCETTAZIONE_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="T10;0;;DATA_ELDA;DACCETTFOF" value="${item[12]}" visibile="${isAlboCommissioneCollegato eq 'true'}"/>
		<gene:campoScheda campo="ESPGIU_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="T2;0;;SN;G1ESPGIU" value="${item[13]}" modificabile="${!giudizioEspresso and !bloccoCommicg}"/>
		<gene:campoScheda campo="COMMICG_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="T2;0;;SN;G1COMMICG" value="${item[16]}" modificabile="false" visibile="${param.integrazioneMEval eq '1'}"/>
		<gene:campoScheda campo="SEZALBO_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="N2;0;A1172;;G1SEZALBO" value="${item[15]}" />
		<gene:campoScheda campo="ID_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="N2;1;;;G1IDGFOF" value="${item[14]}" visibile="false" />
		<gene:fnJavaScriptScheda funzione='visualizzaImporti(${param.contatore})' elencocampi="GFOF_INTFOF_${param.contatore}" esegui="true" />
		<gene:fnJavaScriptScheda funzione="calcolaIMPCOM()" elencocampi="GFOF_IMPFOF_${param.contatore}" />
		<gene:fnJavaScriptScheda funzione="calcolaIMPLIQ()" elencocampi="GFOF_IMPLIQ_${param.contatore}" />		
		<gene:fnJavaScriptScheda funzione='visualizzaIndisponibilita(${param.contatore})' elencocampi="GFOF_INDISPONIBILITA_${param.contatore}" esegui="true"/>
		<c:if test='${(giudizioEspresso eq "true" || bloccoCommicg eq "true") and modo eq "MODIFICA"}'>
		<c:set var="non_cancellabile" value="true"/>
		<gene:campoScheda>
			<c:choose>
				<c:when test='${giudizioEspresso eq "true"}'>
				<c:set var="msgBlocco" value="Componente commissione con valutazioni sugli operatori in gara e pertanto non modificabile" />
				</c:when>
				<c:otherwise>
				<c:set var="msgBlocco" value="Componente commissione abilitato alla valutazione su M-Eval e pertanto non modificabile" />
				</c:otherwise>
			</c:choose>
			<tr>
			<td class="etichetta-dato"></td>
			<td>
				<span>&nbsp&nbspATTENZIONE: ${msgBlocco}</span>
			</td>
			</tr>
		</gene:campoScheda>
		</c:if>
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="NGARA2_${param.contatore}" entita="GFOF" campoFittizio="true" visibile="false" definizione="T21;1;;;NGARA_FUN" />
			<gene:archivio titolo="tecnici"
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.TECNI.CODTEC"), "gene/tecni/tecni-lista-popup.jsp", "")}'
				scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
				schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
				campi="TECNI.CODTEC;TECNI.NOMTEC"
				functionId="skip"
				chiave="GFOF_CODFOF_${param.contatore}"
				formName="formComponenteCommissione${param.contatore}"
				inseribile="true">
				<gene:campoScheda campo="CODFOF_${param.contatore}" entita="GFOF" modificabile="${isModificabile}" obbligatorio="true" campoFittizio="true" definizione="T10;1;;;CODFOF" />
				<gene:campoScheda campo="NOMFOF_${param.contatore}" entita="GFOF" modificabile="${isModificabile}" campoFittizio="true" definizione="T61;0;;;NOMFOF" /> 
			</gene:archivio>
		<gene:campoScheda campo="INCFOF_${param.contatore}" entita="GFOF" campoFittizio="true" modificabile="${isModificabile}" definizione="N2;1;A1001;;INCFOF" obbligatorio="true" />
		<gene:campoScheda campo="NUMCOMM_${param.contatore}" entita="GFOF" campoFittizio="true" modificabile="${isModificabile}" visibile="false" definizione="N3;0;;;NUMCOMMFOF" value="1" />
		<gene:campoScheda campo="INTFOF_${param.contatore}" entita="GFOF" campoFittizio="true" modificabile="${isModificabile}" definizione="T2;0;;SN;G1INTFOF" value="${gene:if(modoAperturaScheda eq 'VISUALIZZA','', '1')}" obbligatorio="true" />
		<gene:campoScheda campo="IMPFOF_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="F15;0;;MONEY;G1IMPFOF" />
		<gene:campoScheda campo="IMPLIQ_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="F15;0;;MONEY;G1IMPLIQ_F" />
		<gene:campoScheda campo="IMPSPE_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="F15;0;;MONEY;G1IMPSPE" />
		<gene:campoScheda campo="DLIQSPE_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="T10;0;;DATA_ELDA;G1DLIQSPE" />
		<gene:campoScheda campo="INDISPONIBILITA_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="T2;0;;SN;INDISPFOF" visibile="${isAlboCommissioneCollegato eq 'true'}"/>
		<gene:campoScheda campo="MOTIVINDISP_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="T2000;0;;NOTE;MOTINDFOF" visibile="${isAlboCommissioneCollegato eq 'true'}"/>
		<gene:campoScheda campo="DATARICHIESTA_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="T10;0;;DATA_ELDA;DRICHFOF" visibile="${isAlboCommissioneCollegato eq 'true'}"/>
		<gene:campoScheda campo="DATAACCETTAZIONE_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="T10;0;;DATA_ELDA;DACCETTFOF" visibile="${isAlboCommissioneCollegato eq 'true'}"/>
		<gene:campoScheda campo="ESPGIU_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="T2;0;;SN;G1ESPGIU" value="${gene:if(modoAperturaScheda eq 'VISUALIZZA','', '1')}" />
		<gene:campoScheda campo="COMMICG_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="T2;0;;SN;G1COMMICG" modificabile="false" visibile="${param.integrazioneMEval eq '1'}"/>
		<gene:campoScheda campo="SEZALBO_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="N2;0;A1172;;G1SEZALBO" />
		<gene:campoScheda campo="ID_${param.contatore}" entita="GFOF" campoFittizio="true" definizione="N2;1;;;G1IDGFOF" visibile="false" />
		<gene:fnJavaScriptScheda funzione='visualizzaImporti(${param.contatore})' elencocampi="GFOF_INTFOF_${param.contatore}" esegui="true" />
		<gene:fnJavaScriptScheda funzione="calcolaIMPCOM()" elencocampi="GFOF_IMPFOF_${param.contatore}" />
		<gene:fnJavaScriptScheda funzione="calcolaIMPLIQ()" elencocampi="GFOF_IMPLIQ_${param.contatore}" />
		<gene:fnJavaScriptScheda funzione='visualizzaIndisponibilita("${param.contatore}","${modo}")' elencocampi="GFOF_INDISPONIBILITA_${param.contatore}" esegui="true" />
	</c:otherwise>
</c:choose>

<gene:javaScript>

		<c:if test='${modoAperturaScheda ne "VISUALIZZA" and isAlboCommissioneCollegato eq "true"}'>
		 $('#GFOF_DATAACCETTAZIONE_${param.contatore}').change(function(e){
		 	var datrich =  $('#GFOF_DATARICHIESTA_${param.contatore}').val();
		 	if($(this).val() != null && (datrich == "" || datrich == null)){
		 		alert("Inserire prima la data richiesta indisponibilità.");
				$(this).val(null);
		 	}else{
				if($(this).val() != null){
					if(!confirm("Una volta impostata la data di accettazione dell'indisponibilità, non sarà più possibile modificare i dati del componente commissione.\nVuoi procedere ?")){
						$(this).val(null);
					}
				}
		 	}
     	});
		</c:if>

	function calcolaIMPCOM(){
		var importoCommissione = 0;
		for(var i=1; i < ${param.contatore}; i++)
			importoCommissione += toVal(getValue("GFOF_IMPFOF_" + i));
		
		setValue("GARE_IMPCOM", toMoney(importoCommissione));
	}

	function calcolaIMPLIQ(){
		var importoLiquidatoCommissione = 0;
		for(var i=1; i < ${param.contatore}; i++)
			importoLiquidatoCommissione += toVal(getValue("GFOF_IMPLIQ_" + i));

		setValue("GARE_IMPLIQ", toMoney(importoLiquidatoCommissione));
	}
	<c:if test='${non_cancellabile eq "true"}'>
	$("#tdTitoloDestra_${param.contatore}").html('');
	</c:if>
</gene:javaScript>
	
<c:if test='${param.addJs}' >

<gene:javaScript>
	// Customizzazione della funzione delElementoSchedaMultipla per effettuare il
	// ricalcolo dei campi GARE_IMPCOM e GARE_IMPLIQ
	function delComponente(id, label, tipo, campi){
		if(confirm("Procedere con l'eliminazione ?")){
			hideElementoSchedaMultipla(id, tipo, campi, false);
		  setValue(label + id, "1");
		  setValue("GFOF_IMPFOF_" + id, "");
		  setValue("GFOF_IMPLIQ_" + id, "");
		}
	}
</gene:javaScript>



</c:if>