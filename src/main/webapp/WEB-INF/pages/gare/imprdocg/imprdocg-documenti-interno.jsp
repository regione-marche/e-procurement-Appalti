<%
/*
 * Created on: 259/01/2018
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 /* La jsp contiene i campi comuni alle pagine  
 	gare-pg-contratto.jsp
 	gare-pg-aggiudicazione-efficace.jsp
 	gare-pg-stipula-accordo-quadro.jsp
 */
%>


<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>			

<gene:campoLista campo="CODGAR" visibile="false" edit="${updateLista eq 1}"/>
<gene:campoLista campo="CODIMP" visibile="false" edit="${updateLista eq 1}"/>
<gene:campoLista campo="NORDDOCI" visibile="false" edit="${updateLista eq 1}"/>
<gene:campoLista campo="NGARA"  visibile="false" edit="${updateLista eq 1}"/>
<gene:campoLista campo="BUSTAORD"  visibile="false" />
<gene:campoLista campo="PROVENI" visibile="false" edit="${updateLista eq 1}"/>

<gene:campoLista campo="DESCRIZIONE" ordinabile="${genereGara eq 10 ||  genereGara eq 20 ||  tipo == 'CONSULTAZIONE'}" edit="${updateLista eq 1 }" visibile="${updateLista eq 0}"/>
<gene:campoLista campo="DESCRIZIONE_FIT" campoFittizio="true" definizione="T2000;;;;G1DESCLIB_DD" value="${datiRiga.V_GARE_DOCDITTA_DESCRIZIONE}" visibile="${updateLista eq 1}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoNumeroProgressivoDITG"/>
<gene:campoLista campo="DIGNOMDOC" ordinabile="false" href='javascript:visualizzaFileAllegato("${datiRiga.V_GARE_DOCDITTA_IDPRG}",${datiRiga.V_GARE_DOCDITTA_IDDOCDG},${gene:string4Js(datiRiga.V_GARE_DOCDITTA_DIGNOMDOC)},"${datiRiga.V_GARE_DOCDITTA_DATARILASCIO}","${datiRiga.V_GARE_DOCDITTA_ORARILASCIO}" ,"${datiRiga.V_GARE_DOCDITTA_DOCTEL }",${currentRow}, "${datiRiga.IMPRDOCG_NORDDOCI}", "${datiRiga.IMPRDOCG_PROVENI}");'/>
<gene:campoLista campo="OBBLIGATORIO" title="Obbl.?" width="50" ordinabile="${genereGara eq 10 ||  genereGara eq 20 ||  tipo == 'CONSULTAZIONE'}" value="${gene:if(datiRiga.V_GARE_DOCDITTA_PROVENI eq 1, datiRiga.V_GARE_DOCDITTA_OBBLIGATORIO, '')}"/>


<gene:campoLista campo="DATARILASCIO" width="100" edit="${updateLista eq 1 and datiRiga.V_GARE_DOCDITTA_DOCTEL ne 1}" ordinabile="${genereGara eq 10 ||  genereGara eq 20 ||  tipo == 'CONSULTAZIONE'}"/>
<gene:campoLista campo="ORARILASCIO" width="100" edit="${updateLista eq 1 and datiRiga.V_GARE_DOCDITTA_DOCTEL ne 1}" ordinabile="${genereGara eq 10 ||  genereGara eq 20 ||  tipo == 'CONSULTAZIONE'}"/>
<gene:campoLista campo="DATASCADENZA" width="100" edit="${updateLista eq 1}" ordinabile="${genereGara eq 10 ||  genereGara eq 20 ||  tipo == 'CONSULTAZIONE'}"/>
<gene:campoLista campo="SITUAZDOCI" edit="${updateLista eq 1}" ordinabile="${genereGara eq 10 ||  genereGara eq 20 ||  tipo == 'CONSULTAZIONE'}"/>

<gene:campoLista campo="IDPRG" visibile="false" edit="${updateLista eq 1}"/>
<gene:campoLista campo="IDDOCDG" visibile="false" edit="${updateLista eq 1}"/>

<gene:campoLista title="&nbsp;" width="20" >
	<a href="javascript:chiaveRiga='${chiaveRigaJava}';ulterioriCampi(${currentRow}+1, '${chiaveRigaJava}');" title="Ulteriori dettagli del documento" >
		<img width="16" height="16" title="Ulteriori dettagli del documento" alt="Ulteriori dettagli del documento" src="${pageContext.request.contextPath}/img/opzioni.png"/>
	</a>
</gene:campoLista>

<gene:campoLista campo="DOCTEL" edit="${updateLista eq 1}" visibile="false"/>
<gene:campoLista campo="BUSTADESC" visibile="false"/>

<gene:campoLista campo="NUMORD" visibile="false"/>
<gene:campoLista campo="BUSTA" edit="true" visibile="false"/>

<c:set var="indiceRiga" value="${indiceRiga + 1}"/>

<%/* Questa parte di codice setta lo stile della riga in base che sia un titolo oppure una riga di dati */%>
<gene:campoLista visibile="false" >
	<th style="display:none">
		<c:if test="${oldTab1desc != newTab1desc}"><script type="text/javascript">
		var nomeForm = document.forms[0].name;
		var indice = ${indiceRiga};
		document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi } )].className =document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi }  ) - 1].className;
		document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi } ) - 1].className = "white";
		</script></c:if>
	</th>
</gene:campoLista>



<gene:campoLista campo="CODGAR" entita="IMPRDOCG" where="${param.whereImprdocg }" visibile="false" edit="${updateLista eq 1}"/>
<gene:campoLista campo="NGARA"  entita="IMPRDOCG" where="${param.whereImprdocg }" visibile="false" edit="${updateLista eq 1}"/>
<gene:campoLista campo="CODIMP" entita="IMPRDOCG" where="${param.whereImprdocg }" visibile="false" edit="${updateLista eq 1}"/>
<gene:campoLista campo="NORDDOCI" entita="IMPRDOCG" where="${param.whereImprdocg }" visibile="false" edit="${updateLista eq 1}"/>
<gene:campoLista campo="PROVENI" entita="IMPRDOCG" where="${param.whereImprdocg }" visibile="false" edit="${updateLista eq 1}"/>
<gene:campoLista campo="DESCRIZIONE" entita="IMPRDOCG" where="${param.whereImprdocg }" visibile="false" edit="${updateLista eq 1}"/>
<gene:campoLista campo="DATARILASCIO" entita="IMPRDOCG" where="${param.whereImprdocg }" visibile="false" edit="${updateLista eq 1}"/>
<gene:campoLista campo="ORARILASCIO" entita="IMPRDOCG" where="${param.whereImprdocg }" visibile="false" edit="${updateLista eq 1}"/>
<gene:campoLista campo="DATASCADENZA" entita="IMPRDOCG" where="${param.whereImprdocg }" visibile="false" edit="${updateLista eq 1}"/>
<gene:campoLista campo="SITUAZDOCI" entita="IMPRDOCG" where="${param.whereImprdocg }" visibile="false" edit="${updateLista eq 1}"/>
<gene:campoLista campo="NOTEDOCI" entita="IMPRDOCG" where="${param.whereImprdocg }" visibile="false" edit="${updateLista eq 1}" gestore="it.eldasoft.sil.pg.tags.gestori.decoratori.GestoreCampoTestoCodifcaDoppiApici"/>
<gene:campoLista campo="DATALETTURA" entita="IMPRDOCG" where="${param.whereImprdocg }" visibile="false" edit="${updateLista eq 1}"/>
<gene:campoLista campo="SYSCONLET" entita="IMPRDOCG" where="${param.whereImprdocg }" visibile="false" edit="${updateLista eq 1}"/>