
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<c:set var="OPGara" value="${fn:contains(listaOpzioniDisponibili, 'OP135#')}" scope="request"/>
<c:set var="OPElenco" value="${fn:contains(listaOpzioniDisponibili, 'OP136#')}" scope="request"/>

<gene:gruppoCampi idProtezioni="ALT">
	<gene:campoScheda>
		<td colspan="2"><b>Ulteriori dati</b> </td>
	</gene:campoScheda>
	<gene:campoScheda campo="GENERE" obbligatorio="true" gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoGenereQform" modificabile='${ modo eq "NUOVO" }'/>
	<gene:campoScheda campo="BUSTA" obbligatorio="true" />
	<gene:campoScheda campo="TIPLAV" />
	<gene:campoScheda campo="DAIMPORTO" />
	<gene:campoScheda campo="AIMPORTO" />
	<c:if test='${modo ne "MODIFICA" && modo ne "NUOVO" && datiRiga.QFORMLIB_STATO > 1}' >
		<gene:campoScheda addTr="false">
			<tr id="rowLinkGare">
				<td colspan="2" class="valore-dato" id="colonnaLinkGare">
				<a href="javascript:listaGare(${datiRiga.QFORMLIB_ID });" title="Lista gare ed elenchi che utilizzano il modello" >
					 Lista gare ed elenchi che utilizzano il modello
				</a>
			</tr>
		</gene:campoScheda>
	</c:if>
</gene:gruppoCampi>
			
			
			
			