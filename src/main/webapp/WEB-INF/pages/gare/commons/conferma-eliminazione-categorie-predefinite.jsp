<%
/*
 * Created on: 30-ago-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra che visualizza la conferma per la cancellazione delle categorie
		d'iscrizione associate alla gara 
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty requestScope.categorieEliminate and requestScope.categorieEliminate eq "1"}' >
<script type="text/javascript">
		opener.historyReload();
		window.close();
</script>
	</c:when>
	<c:otherwise>

<div style="width:97%;">
<gene:template file="popup-message-template.jsp">
	<gene:setString name="titoloMaschera" value="Eliminazione categorie d'iscrizione" />
	
	<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="GARE" gestisciProtezioni="false" gestore="it.eldasoft.sil.pg.tags.gestori.submit.GestoreInsertDeleteCategoriePredefinite">
	
		<c:choose>
			<c:when test='${fn:length(param.tipoElenco) eq 1}'>
				<c:set var="tmpTIPOELE" value="00${param.tipoElenco}" />
			</c:when>
			<c:when test='${fn:length(param.tipoElenco) eq 2}'>
				<c:set var="tmpTIPOELE" value="0${param.tipoElenco}" />
			</c:when>
			<c:otherwise>
				<c:set var="tmpTIPOELE" value="${param.tipoElenco}" />
			</c:otherwise>
		</c:choose>
	
		<gene:campoScheda>
			<td>&nbsp;&nbsp;</td>
			<td>
				Scegliere la tipologia di categorie d'iscrizione da eliminare:&nbsp;
				<select name="tipoCategoria" id="tipoCategoria" onchange="javascript:changeTipoElenco();" >
				<c:if test='${fn:startsWith(tmpTIPOELE, "1")}'>
					<option value="1" <c:if test="${param.filtroTipoElenco eq 1}">selected=true</c:if> >Lavori</option>
				</c:if>
				<c:if test='${fn:startsWith(fn:substring(tmpTIPOELE, 1, 2), "1")}'>
					<option value="2" <c:if test="${param.filtroTipoElenco eq 2}">selected=true</c:if> >Forniture</option>
				</c:if>
				<c:if test='${fn:startsWith(fn:substring(tmpTIPOELE, 2, 3), "1")}'>
					<option value="3" <c:if test="${param.filtroTipoElenco eq 3}">selected=true</c:if> >Servizi</option>
				</c:if>
					<option value="4" <c:if test="${empty param.filtroTipoElenco}">selected=true</c:if> >Tutte le categorie</option>
				</select>
				<br><br>
				Confermi l'eliminazione delle categorie d'iscrizione predefinite ?
				<br><br>
			</td>
		</gene:campoScheda>

		<gene:campoScheda campo="CODGAR1" campoFittizio="true" defaultValue="${param.codgar}" visibile="false" definizione="T21;0"/>
		<gene:campoScheda campo="NGARA" campoFittizio="true" defaultValue="${param.ngara}" visibile="false" definizione="T20;0"/>
<c:choose>
	<c:when test="${empty param.filtroTipoElenco}">
		<gene:campoScheda campo="TIPO_CATEGORIA" campoFittizio="true" visibile="false" definizione="N1;0" defaultValue="4"/>
	</c:when>
	<c:otherwise>
		<gene:campoScheda campo="TIPO_CATEGORIA" campoFittizio="true" visibile="false" definizione="N1;0" defaultValue="${param.filtroTipoElenco}"/>
	</c:otherwise>
</c:choose>
		<gene:campoScheda campo="TIPO_OPERAZIONE" campoFittizio="true" visibile="false" definizione="T10;0" defaultValue="DELETE"/>
	</gene:formScheda>
  </gene:redefineInsert>

	<gene:javaScript>
		function changeTipoElenco(){
			var tipoCategoria = document.getElementById("tipoCategoria").value;
			setValue("TIPO_CATEGORIA", tipoCategoria);
		}

		function conferma() {
			document.forms[0].jspPathTo.value="gare/commons/conferma-eliminazione-categorie-predefinite.jsp";
			schedaConferma();
		}
		
		function annulla(){
			window.close();
		}

	</gene:javaScript>
</gene:template>
</div>

	</c:otherwise>
</c:choose>