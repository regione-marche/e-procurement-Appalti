<%
/*
 * Created on: 11-01-2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 /* Lista popup di selezione degli accordi quadro */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="tipo" value="${fn:substring(trovaAddWhere, fn:indexOf(trovaAddWhere, 'V_GARE_ACCORDIQUADRO.TIPGEN = ') + 30, fn:indexOf(trovaAddWhere, 'V_GARE_ACCORDIQUADRO.TIPGEN = ') + 31)}" />
<c:set var="descTipo" value='${gene:callFunction4("it.eldasoft.sil.pg.tags.funzioni.GetValoreTabellatoFunction", pageContext, "A1007", tipo, "")}'/>
<c:set var="descTipo" value='${fn:toLowerCase(descTipo) }'/>

<c:set var="cenintPresente" value="${fn:containsIgnoreCase(trovaAddWhere, 'cenint') }" />

<gene:template file="popup-template.jsp">
	<gene:setString name="titoloMaschera" value="Selezione dell'accordo quadro"/>
	<gene:redefineInsert name="corpo">
		<br/>
		Nella lista vengono riportati gli accordi quadro per ${descTipo } con data atto contrattuale e durata valorizzati.<br>
		<c:if test="${cenintPresente}">
		Inoltre vengono considerati solo gli accordi quadro per cui la stazione appaltante dell'adesione è qualificata a ricorrere 
		(la stazione appaltante è presente nell'elenco dei soggetti dell'accordo quadro oppure l'elenco dei soggetti non è definito).<br>
		</c:if>
		Non sono elencati gli accordi quadro archiviati.
		<br/><br/>
		<gene:formLista pagesize="25" tableclass="datilista" entita="V_GARE_ACCORDIQUADRO" sortColumn="-6" inserisciDaArchivio="false" gestisciProtezioni="true" >
			<% // Aggiungo gli item al menu contestuale di riga %>
			<gene:campoLista title="Opzioni" width="50">
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs},${currentRow  });"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<gene:campoLista campo="NGARA"  title="Codice" headerClass="sortable" href="javascript:archivioSeleziona(${datiArchivioArrayJs},${currentRow  });" />
			<gene:campoLista campo="CODCIG"  headerClass="sortable"/>
			<gene:campoLista campo="NOT_GAR"  headerClass="sortable"/>
			<gene:campoLista campo="DATAINIZIO" headerClass="sortable"/>
			<gene:campoLista campo="DATAFINE" headerClass="sortable"/>
			<c:if test="${not empty sessionScope.uffint}">
			<gene:campoLista campo="IMPIMP" entita="V_SPESE_ADESIONI" where="V_GARE_ACCORDIQUADRO.GARA=V_SPESE_ADESIONI.NGARA AND V_GARE_ACCORDIQUADRO.NCONT=V_SPESE_ADESIONI.NCONT AND V_SPESE_ADESIONI.CENINT='${sessionScope.uffint}'" edit="true" visibile = "false"/>
			<gene:campoLista campo="IMPAUT" entita="V_SPESE_ADESIONI" where="V_GARE_ACCORDIQUADRO.GARA=V_SPESE_ADESIONI.NGARA AND V_GARE_ACCORDIQUADRO.NCONT=V_SPESE_ADESIONI.NCONT AND V_SPESE_ADESIONI.CENINT='${sessionScope.uffint}'" edit="true" visibile = "false"/>
			<gene:campoLista campo="RESIDUO" title="Residuo da impegnare" campoFittizio="true" ordinabile = "false" edit="false" value="${datiRiga.V_SPESE_ADESIONI_IMPAUT - datiRiga.V_SPESE_ADESIONI_IMPIMP}" definizione="F24.5;0;;MONEY"  />
			
			</c:if>
		</gene:formLista>
		
  </gene:redefineInsert>
 
</gene:template>


