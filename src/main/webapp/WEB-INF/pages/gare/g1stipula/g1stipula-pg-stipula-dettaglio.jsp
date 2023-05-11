
<%
/*
 * Created on: 17/05/2021
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


<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.RichiestaCIG") and not (isCigAbilitato eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GARE.InviaDatiCIG"))}'>
	<c:set var="codiceGaraSimog" value="${codgar}"/>
	<c:set var="esisteAnagraficaSimog" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.EsisteAnagraficaSimogFunction", pageContext, codiceGaraSimog)}'/>
</c:if>

<gene:callFunction obj="it.eldasoft.sil.pg.tags.funzioni.GestioneCPVFunction" parametro='${item[0]}' />

<gene:campoScheda title="Codice lotto" campo="NGARA_${param.contatore}" entita="GARE"
	obbligatorio="true" campoFittizio="true" definizione="T20;;;;NGARA"
	value="${item[0]}" />
<gene:campoScheda campo="CODIGA_${param.contatore}" entita="GARE"
	campoFittizio="true" definizione="T10;;;;CODIGA"
	value="${item[1]}" visibile="${!empty item[1] && (item[1] ne '')}" />
<gene:campoScheda campo="NUMAVCP_${param.contatore}" entita="TORN"
	campoFittizio="true" definizione="T20;;;;G1NUMAVCP"
	value="${item[2]}" visibile="${tipoSimog ne 'S'}"/>
<gene:campoScheda campo="CODCIG_${param.contatore}" entita="GARE"
	campoFittizio="true" definizione="T10;;;;G1CODCIG"
	value="${item[3]}" />
<gene:campoScheda campo="ESENTE_CIG_${param.contatore}" title="Esente CIG?" computed="true"
	campoFittizio="true" definizione="T10;;;SN;"
	value="2" visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.GARE.CODCIG") }'/>
<gene:campoScheda campo="STATO_SIMOG" title="Effettuata richiesta CIG?"
	campoFittizio="true" definizione="T30;"
	value="${tipoSimogDesc} - ${statoSimog }" visibile="${esisteAnagraficaSimog eq 'true'}"/>
<gene:campoScheda campo="NOTGAR_${param.contatore}" entita="GARE"
	campoFittizio="true" definizione="T2000;;;NOTE;OGGETA"
	value="${item[8]}" />
<gene:campoScheda campo="IAGGIU_${param.contatore}" entita="GARE"
	campoFittizio="true" definizione="F15;;;MONEY;IAGGIU"
	value="${item[5]}" />
<gene:campoScheda campo="IMPRIN_${param.contatore}" entita="GARE1"
	campoFittizio="true" definizione="F15;;;MONEY;G1IMPRIN"
	value="${item[6]}" />
<gene:campoScheda campo="IMPALTRO_${param.contatore}" entita="GARE1"
	campoFittizio="true" definizione="F15;;;MONEY;G1IMPALTRO"
	value="${item[7]}" />

<gene:javaScript>

	//Inserimento dei campi CPV, vengono inseriti dopo il campo IMPALTRO
	var html ='';
	var etichetta='Codice CPV principale';
	<c:choose>
		<c:when test="${not empty requestScope.cpv}">
			<c:forEach var="CPV" items="${requestScope.cpv}" varStatus="stato">
				<c:if test="${CPV[0] eq item[0] }">
					<c:if test="${CPV[3] ne '1' }">
						etichetta='Codice CPV complementare';
					</c:if>
					html += '<tr><td class="etichetta-dato">' + etichetta + '</td><td class="valore-dato">${CPV[2]}</td></tr>';
				</c:if>
			</c:forEach>
		</c:when>
		<c:otherwise>
			html += '<tr><td class="etichetta-dato">' + etichetta + '</td><td class="valore-dato"></td></tr>';
		</c:otherwise>
	</c:choose>
	if(html == '')
		html += '<tr><td class="etichetta-dato">' + etichetta + '</td><td class="valore-dato"></td></tr>';
		
		
	$('#rowGARE1_IMPALTRO_${param.contatore}').after(html);
	
	initEsenteCIG_CODCIG();
	
	initSMARTCIG();

	function initEsenteCIG_CODCIG() {
		var esenteCig = getValue("ESENTE_CIG_${param.contatore}");
		var codcig = getValue("GARE_CODCIG_${param.contatore}");
		//alert("esente CIG = " + esenteCig);
		//alert("Codice CIG = " + codcig);
		if ("" != codcig) {
			if (codcig.indexOf("#") == 0 || codcig.indexOf("$") == 0 || codcig.indexOf("NOCIG") == 0) {
				setValue("ESENTE_CIG_${param.contatore}", "Si", false);
				showObj("rowTORN_NUMAVCP_${param.contatore}", false);
			} else {
				setValue("ESENTE_CIG_${param.contatore}", "No", false);
				showObj("rowTORN_NUMAVCP_${param.contatore}", true);
			}
		} else {
			setValue("ESENTE_CIG_${param.contatore}", "No", false);
			showObj("rowTORN_NUMAVCP_${param.contatore}", true);
		}
	}
	
	function initSMARTCIG() {
		var codcig = getValue("GARE_CODCIG_${param.contatore}");
		if ("" != codcig) {
			if (codcig.indexOf("X") == 0 || codcig.indexOf("Y") == 0 || codcig.indexOf("Z") == 0) {
				showObj("rowTORN_NUMAVCP_${param.contatore}", false);
			}
		}
	}
	
</gene:javaScript>