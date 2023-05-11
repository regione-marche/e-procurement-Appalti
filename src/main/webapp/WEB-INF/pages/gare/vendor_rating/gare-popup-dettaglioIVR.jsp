
<%
	/*
	 * Created on 30-10-2018
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<style type="text/css">
	
	.nascondi {
		display: none;
	}
	

</style>

<gene:template file="popup-template.jsp" gestisciProtezioni="false" schema="GARE">
<gene:setString name="titoloMaschera" value="Dettaglio Ivr ${param.ivr}" />
	<gene:redefineInsert name="corpo">
		<table class="lista">
			<tr>
				<td>
					<display:table name="listObjDettaglioIVR" id="IvrDettaglioForm" class="datilista" pagesize="25" sort="list" >
						
						<display:column title="id" style="display:none;" headerClass="nascondi">
								${IvrDettaglioForm[0]}
						</display:column>
						<display:column title="Indice" >
								${IvrDettaglioForm[1]}
						</display:column>
						<display:column title="Valore">
								${IvrDettaglioForm[2]}
						</display:column>
						<display:column title="Peso" style="display:none;" headerClass="nascondi">
								${IvrDettaglioForm[3]}
						</display:column>
					</display:table>
				</td>
			</tr>
			<tr>
			    <td class="comandi-dettaglio" colSpan="2">
			    	<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:window.close();" >&nbsp;
				</td>
		  </tr>
		</table>
	</gene:redefineInsert>
	
</gene:template>

