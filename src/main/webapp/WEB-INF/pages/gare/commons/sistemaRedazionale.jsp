<!DOCTYPE HTML>
<html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
    <head>
        <style>

            #toggleFullscreenSistemaRedazionale {
                position: absolute;
                right: 17px;
                top: 17px;
                z-index: 10000;
                cursor: pointer;
            }
            .fullscreenSistemaRedazionale {
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: white;
                z-index: 999;
                overflow: hidden;
            }
            .sistemaRedazionaleEmbedded {
                width: 100%;
                height: 75vh;
                margin-top: 5px;
                margin-right: 12px;
            }
            .iframeSistemaRedazionale {
                width: 100%;
                height: 100%;
                overflow: auto;
                border: 1px solid #AAA;
            }
            .scaleSistemaRedazionale {
                transform: scale(0.8);
                transform-origin: top right;
                width: 130%;
            }
            .contenitoreSistemaRedazionale {
                display: flex;
            }

        </style>
    </head>
    <body>
    <div id="contenitoreSistemaRedazionale" class="scaleSistemaRedazionale contenitoreSistemaRedazionale">
    	<div id="paginaSistemaRedazionale" class="sistemaRedazionaleEmbedded">
                <div id="toggleFullscreenSistemaRedazionale">
                    <img width="20" height="20" src="${contextPath}/img/schermoIntero.png" title="Espandi" />
                    <img width="24" height="24" src="${contextPath}/img/riduciFinestra.png" title="Riduci" style="display: none;"/>
                </div>
            <iframe id="content" class="iframeSistemaRedazionale" allowfullscreen src="${param.urlPaginaSistemaRedazionale}"></iframe>

                <script>
                    $(document).ready(function() {
                        $('#toggleFullscreenSistemaRedazionale').click(function() {
                            $('#contenitoreSistemaRedazionale').toggleClass('scaleSistemaRedazionale');
                            $('#paginaSistemaRedazionale').toggleClass('fullscreenSistemaRedazionale').toggleClass('sistemaRedazionaleEmbedded');
                            $('#toggleFullscreenSistemaRedazionale').children().toggle();
                        });
                    });
                </script>
        </div>
    </div>
    </body>
</html>
