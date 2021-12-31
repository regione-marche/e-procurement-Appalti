<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

	<c:choose>
		<c:when test='${fn:contains(sessionScope.profiloAttivo, "AVVISI")}'>
			<c:set var="titoloMaschera" scope="request" value="Condivisione e protezione dell'avviso ${fn:replace(codgar, '$', '')}" />
		</c:when>
		<c:when test='${genereGara eq "10" }'>
			<c:set var="titoloMaschera" scope="request" value="Condivisione e protezione dell'elenco ${fn:replace(codgar, '$', '')}" />
		</c:when>
		<c:when test='${genereGara eq "20" }'>
			<c:set var="titoloMaschera" scope="request" value="Condivisione e protezione del catalogo ${fn:replace(codgar, '$', '')}" />
		</c:when>
		<c:otherwise>
			<c:set var="isProceduraTelematica" value='${gene:callFunction2("it.eldasoft.sil.pg.tags.funzioni.IsProceduraTelematicaFunction", pageContext, codgar)}'/>
			<c:choose>
				<c:when test='${isProceduraTelematica eq "true" and ! fn:contains(sessionScope.profiloAttivo, "ODA")}'>
					<c:set var="titoloMaschera" scope="request" value="Punto ordinante e punto istruttore della gara ${fn:replace(codgar, '$', '')}" />
				</c:when>
				<c:when test='${isProceduraTelematica eq "true" and fn:contains(sessionScope.profiloAttivo, "ODA")}'>
					<c:set var="titoloMaschera" scope="request" value="Punto ordinante e punto istruttore della ricerca di mercato ${fn:replace(codgar, '$', '')}" />
				</c:when>
				<c:otherwise>
					<c:set var="titoloMaschera" scope="request" value="Condivisione e protezione della gara ${fn:replace(codgar, '$', '')}" />
				</c:otherwise>
			</c:choose>

		</c:otherwise>
	</c:choose>

	
