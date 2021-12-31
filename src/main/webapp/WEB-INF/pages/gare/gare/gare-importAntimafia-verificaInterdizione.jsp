<%
/*
 * Created on: 21/02/2011
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
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GARE" idMaschera="GARE-scheda">

	<gene:setString name="titoloMaschera" value='Verifica interdizione ditte' />

	<gene:redefineInsert name="documentiAzioni"></gene:redefineInsert>

	<gene:redefineInsert name="addToAzioni" >
		<tr>
			<td class="vocemenulaterale" >
				<a href="javascript:indietro();" title="Indietro" tabindex="1503">
					Indietro
				</a>
			</td>
		</tr>
	
	</gene:redefineInsert>

	<gene:redefineInsert name="corpo">
		<table class="lista">
			<tr>
				<td>
					<display:table name="listaDitte" id="listaDitte" class="datilista" sort="list">
						<display:column property="nomimp" title="Ragione sociale" >  </display:column>
						<display:column property="in_archivio" title="Presente in archivio?" decorator="it.eldasoft.gene.commons.web.displaytag.IntBooleanDecorator" style="width:80px" >  </display:column>
						<display:column property="nomimp_db" title="Ragione sociale in archivio" >  </display:column>
						<display:column property="cfimp" title="Codice fiscale" >  </display:column>
						<display:column property="pivimp" title="Partita IVA" >  </display:column>
						<display:column property="locimp" title="Comune" >  </display:column>
						<display:column property="interdetta" title="Interdetta?"  decorator="it.eldasoft.gene.commons.web.displaytag.BooleanDecorator" >  </display:column>
					</display:table>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button"  class="bottone-azione" value='Indietro' title='Indietro' onclick="javascript:indietro()">
				</td>
			</tr>
		</table>
	</gene:redefineInsert>

<gene:javaScript>
	
	function indietro(){
		document.location.href = '${pageContext.request.contextPath}/ApriPagina.do?'+csrfToken+'&href=gare/gare/gare-importAntimafia.jsp';
	}	

</gene:javaScript>	

</gene:template>

