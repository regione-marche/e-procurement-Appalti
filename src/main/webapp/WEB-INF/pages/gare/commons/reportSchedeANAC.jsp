<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<gene:template file="scheda-template.jsp" gestisciProtezioni="false" >

    <gene:redefineInsert name="head" >
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.validate.min.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easytabs.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/reportSchedeANAC.js"></script>
    </gene:redefineInsert>

    <gene:redefineInsert name="modelliPredisposti"/>
    <gene:redefineInsert name="documentiAssociati"/>
    <gene:redefineInsert name="noteAvvisi"/>

    <gene:redefineInsert name="addToAzioni" >
        <tr>
            <td class="vocemenulaterale">
                <a href="javascript:exportExcel();" title="Esporta in excel" tabindex="1501">
                    Esporta in excel
                </a>
            </td>
        </tr>
    </gene:redefineInsert>

    <gene:setString name="titoloMaschera" value="Report schede ANAC" />

    <c:choose>
        <c:when test='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GARE.V_GARE_PROFILO-lista.ApriGare")}'>
            <c:set var="isTrovaProcedure" value='${true}' scope="request"/>
            <c:set var="uffintAbilitata" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata")}'/>
        </c:when>
        <c:otherwise>
            <c:set var="isTrovaProcedure" value='${false}' scope="request"/>
        </c:otherwise>
    </c:choose>

    <style>

        TABLE.schedagperm {
            margin-top: 5px;
            margin-bottom: 5px;
            padding: 0px;
            font-size: 11px;
            border-collapse: collapse !important;
            border-left: 1px solid #A0AABA;
            border-top: 1px solid #A0AABA;
            border-right: 1px solid #A0AABA;
            width: 100%;
        }

        TABLE.schedagperm TR.intestazione {
            background-color: #CCE0FF;
            border-bottom: 1px solid #A0AABA;
        }

        TABLE.dataTable tbody th {
            padding: 0;
        }

        TABLE.schedagperm TR.intestazione TD, TABLE.schedagperm TR.intestazione TH {
            padding: 5 2 5 2;
            text-align: center;
            font-weight: bold;
            border-left: 1px solid #A0AABA;
            border-right: 1px solid #A0AABA;
            border-top: 1px solid #A0AABA;
            border-bottom: 1px solid #A0AABA;
            border-collapse: collapse !important;
            height: 30px;
        }

        TABLE.schedagperm TR.sezione {
            background-color: #EFEFEF;
            border-bottom: 1px solid #A0AABA;
        }

        TABLE.schedagperm TR.sezione TD, TABLE.schedagperm TR.sezione TH {
            padding: 5 2 5 2;
            text-align: left;
            font-weight: bold;
            height: 25px;
        }

        TABLE.schedagperm TR {
            background-color: #FFFFFF;
        }

        TABLE.schedagperm TR TD {
            padding-left: 3px !important;
            padding-top: 1px !important;
            padding-bottom: 1px !important;
            padding-right: 3px !important;
            text-align: left;
            border-collapse: collapse !important;
            border-left: 1px solid #A0AABA;
            border-right: 1px solid #A0AABA;
            border-top: 1px solid #A0AABA;
            border-bottom: 1px solid #A0AABA;
            height: 25px;
            font: 11px Verdana, Arial, Helvetica, sans-serif;
        }

        TABLE.schedagperm TR.intestazione TH.codice, TABLE.schedagperm TR TD.codice {
            width: 20px;
        }

        TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.codfisc {
            width: 100px;
        }

        TABLE.schedagperm TR.intestazione TH.descr, TABLE.schedagperm TR TD.descr {
            word-break:break-all;
            width: 200px;
        }

        TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.datadescr {
            word-break:break-word;
            text-align: center;
        }
        TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.stato {
            width: 100px;
            text-align: center;
        }
        TABLE.schedagperm TR.intestazione TH.ck, TABLE.schedagperm TR TD.ck {
            width: 50px;
            text-align: center;
        }

        .dataTables_length, .dataTables_filter {
            padding-bottom: 5px;
        }

        .dataTables_empty {
            padding-top: 6px;
        }

        div.tooltip {
            width: 300px;
            margin-top: 3px;
            margin-bottom:3px;
            border: 1px solid #A0AABA;
            padding: 10px;
            display: none;
            position: absolute;
            z-index: 1000;
            background-color: #F4F4F4;
        }

        input.search {
            height: 16px;
            font: 11px Verdana, Arial, Helvetica, sans-serif;
            background-color: #FFFFFF;
            color: #000000;
            vertical-align: middle;
            border: 1px #366A9B solid;
            width: 98%;
            font-style: italic;
        }

        #add {
            float: right;
        }

        #add label select{
            width:105px;
        }

        div.schedeAttesaErrori {
            text-align: center;
            font: 10px Verdana, Arial, Helvetica, sans-serif;
            color: #FFFFFF;
            border: 1px solid #d30000;
            background-color: #d30000;
            padding: 3px;
            margin-bottom: 5px;
            margin-top: 5px;
            -moz-border-radius-topleft: 2px;
            -webkit-border-top-left-radius: 2px;
            -khtml-border-top-left-radius: 2px;
            border-top-left-radius: 2px;
            -moz-border-radius-topright: 2px;
            -webkit-border-top-right-radius: 2px;
            -khtml-border-top-right-radius: 2px;
            border-top-right-radius: 2px;
            -moz-border-radius-bottomleft: 2px;
            -webkit-border-bottom-left-radius: 2px;
            -khtml-border-bottom-left-radius: 2px;
            border-bottom-left-radius: 2px;
            -moz-border-radius-bottomright: 42px;
            -webkit-border-bottom-right-radius: 2px;
            -khtml-border-bottom-right-radius: 2px;
            border-bottom-right-radius: 2px;
        }

        div.schedeSuccesso {
            text-align: center;
            font: 10px Verdana, Arial, Helvetica, sans-serif;
            color: #FFFFFF;
            border: 1px solid #58cc00;
            background-color: #58cc00;
            padding: 3px;
            margin-bottom: 5px;
            margin-top: 5px;
            -moz-border-radius-topleft: 2px;
            -webkit-border-top-left-radius: 2px;
            -khtml-border-top-left-radius: 2px;
            border-top-left-radius: 2px;
            -moz-border-radius-topright: 2px;
            -webkit-border-top-right-radius: 2px;
            -khtml-border-top-right-radius: 2px;
            border-top-right-radius: 2px;
            -moz-border-radius-bottomleft: 2px;
            -webkit-border-bottom-left-radius: 2px;
            -khtml-border-bottom-left-radius: 2px;
            border-bottom-left-radius: 2px;
            -moz-border-radius-bottomright: 42px;
            -webkit-border-bottom-right-radius: 2px;
            -khtml-border-bottom-right-radius: 2px;
            border-bottom-right-radius: 2px;
        }

    </style>

    <gene:redefineInsert name="corpo">
        <br>
        <div id="gareContainer"></div>
    </gene:redefineInsert>

    <gene:javaScript>

        popolaTabella('${isTrovaProcedure}');

        // Visualizzazione del dettaglio
        function visualizzaGaraProfiloReport(codice,cenint,codprofilo,genere){
            var link = '${pageContext.request.contextPath}/pg/VisualizzaGareProfilo.do?codice=' + codice + '&profilo=' + codprofilo + '&genere=' + genere;
            if ("${uffintAbilitata}" == 1) {
                link =  link + '&uffint=' + cenint;
            }
            if(genere=='11'){
                link =  link + '&href=gare/gareavvisi/gareavvisi-lista.jsp';
            }else{
                link =  link + '&href=gare/v_gare_torn/v_gare_torn-lista.jsp';
            }
            document.location.href = link;
        }

        function apriDettaglio(codiceGara) {
            chiaveRiga= "V_GARE_TORN.CODGAR=T:" + codiceGara;
            if (chiaveRiga.indexOf('$')>0){
                //gara a lotto unico si va su gare
                formApriDettaglio.keyParent.value=chiaveRiga;
                formApriDettaglio.entita.value = "GARE";
            } else {
                formApriDettaglio.entita.value = "TORN";
            }
            formApriDettaglio.key.value=chiaveRiga;
            formApriDettaglio.metodo.value="apri";
            formApriDettaglio.activePage.value="0";
            formApriDettaglio.submit();
        }

        function exportExcel() {
            if (confirm("Si sta per scaricare (download) la lista delle gare estratte in formato excel.\nContinuare?")) {
                let nomeFile;
                _waitgperm();

                setTimeout(() => {
                    $.ajax({
                        type: "POST",
                        dataType: "json",
                        data: {
                            modalita: "exportExcel"
                        },
                        async: false,
                        url: "${contextPath}/pg/ReportSchedeANAC.do",
                        error: function(){
                            alert("Errore inaspettato durante l'operazione, controllare il log per i dettagli");
                        },
                        success: function(res){
                            nomeFile=res.data;
                        },
                        complete: function(jqXHR, textStatus) {
                            _nowaitgperm();
                            if (nomeFile != null) {
                                downloadExportForm.nomeTempFile.value = nomeFile;
                                downloadExportForm.submit();
                            }
                        }
                    });
                },50);
            }
        }

        function _waitgperm() {
            document.getElementById('bloccaScreen').style.visibility='visible';
            document.getElementById('wait').style.visibility='visible';
            $("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200});
        }
        function _nowaitgperm() {
            var timeout = null;
            timeout = setInterval(function() {
                document.getElementById('bloccaScreen').style.visibility='hidden';
                document.getElementById('wait').style.visibility='hidden';
                clearTimeout(timeout);
            }, 50);
        }

    </gene:javaScript>

</gene:template>

<form name="formApriDettaglio" action="${contextPath}/Lista.do" method="post" >
    <input type="hidden" name="key" value="" />
    <input type="hidden" name="keyParent" value="" />
    <input type="hidden" name="jspPath" value="" />
    <input type="hidden" name="jspPathTo" value="" />
    <input type="hidden" name="activePage" value="0" />
    <input type="hidden" name="isPopUp" value="0" />
    <input type="hidden" name="numeroPopUp" value="0" />
    <input type="hidden" name="metodo" value="" />
    <input type="hidden" name="entita" value="" />
    <input type="hidden" name="gestisciProtezioni" value="1" />
</form>

<form action="${contextPath}/DownloadTempFile.do" method="post" name="downloadExportForm" >
    <input type="hidden" name="nomeTempFile" value="" />
</form>
