function creaTabella() {

    var headerRow = $('<tr/>', {"class": "intestazione"});
    headerRow.append('<th/>');
    headerRow.append('<th/>');
    headerRow.append('<th/>');
    headerRow.append('<th/>');
    headerRow.append('<th/>');
    headerRow.append('<th/>');
    headerRow.append('<th/>');
    headerRow.append('<th/>');
    headerRow.append('<th/>');
    headerRow.append('<th/>');
    headerRow.append('<th/>');
    headerRow.append('<th/>');

    var thead = $('<thead>');
    thead.append(headerRow);

    var tbody = $('<tbody>');

    var _tr2 = $('<tr/>', {"class": "intestazione"});
    _tr2.append('<td/>');
    _tr2.append('<td/>');
    _tr2.append('<td/>');
    _tr2.append('<td/>');
    _tr2.append('<td/>');
    _tr2.append('<td/>');
    _tr2.append('<td/>');
    _tr2.append('<td/>');
    _tr2.append('<td/>');
    _tr2.append('<td/>');
    _tr2.append('<td/>');
    _tr2.append('<td/>');

    var _tfoot = $('<tfoot/>');
    _tfoot.append(_tr2);

    var tabella = $('<table>', {"id": "gare", "class": "schedagperm", "cellspacing": "0"});
    tabella.append(thead);
    tabella.append(tbody);
    tabella.append(_tfoot);

    $('#gareContainer').append(tabella);
}

function popolaTabella(isTrovaProcedure) {

    $('#gareContainer').empty();
    creaTabella();
    _tableGare = $('#gare').removeAttr('width').DataTable({

        "ajax": {
            "url": "pg/ReportSchedeANAC.do",
            "async": false,
        },
        searching: true,
        order: [[0,'desc'],[1,'asc']],
        "ordering": true,
        "bAutoWidth": false,
        "columnDefs": [
            {
                "searchable": true,
                "data": "CODICEGARA.value",
                "visible": true,
                "sTitle": "Cod. gara",
                "class": "datadescr",
                "width": "6%",
                "targets": [ 0 ],
                "render": function (data, type, full) {
                    var _div = $("<div/>");
                    if (isTrovaProcedure !== "true") {
                        href = 'javascript:apriDettaglio("' + full.CODGAR.value + '")';
                        var linkApriGara = $('<a>', {
                            text : full.CODICEGARA.value,
                            style : "color: #000000; text-decoration: underline;",
                            title: "Codice gara",
                            href : href
                        });
                        _div.append(linkApriGara);
                    } else {
                        var spanCodiceGara = $('<span>', {
                            text: full.CODICEGARA.value,
                            title: "Codice gara"
                        })
                        _div.append(spanCodiceGara);
                    }
                    return _div.html();
                }
            },
            {
                "searchable": true,
                "data": "CODIGA.value",
                "visible": true,
                "class": "datadescr",
                "sTitle": "Lot",
                "width": "5%",
                "targets": [ 1 ]
            },
            {
                "searchable": true,
                "data": "CODCIG.value",
                "visible": true,
                "sTitle": "CIG",
                "width": "3%",
                "targets": [ 2 ]
            },
            {
                "searchable": true,
                "data": "NOT_GAR.value",
                "visible": true,
                "sTitle": "Oggetto",
                "targets": [ 3 ],
                "render": function (data, type, full) {
                    var _div = $("<div/>");
                    oggettoTroncato = full.NOT_GAR.value;
                    if(oggettoTroncato != null && oggettoTroncato.length > 200)
                        oggettoTroncato = oggettoTroncato.substring(0,195) + " [..]";
                    var spanOggetto = $('<span>', {
                        text: oggettoTroncato,
                        title: full.NOT_GAR.value
                    })
                    _div.append(spanOggetto);
                    return _div.html();
                }
            },
            {
                "searchable": true,
                "data": "NOMERP.value",
                "visible": true,
                "sTitle": "Nome RUP",
                "width": (isTrovaProcedure !== "true" ? "8%": "5%"),
                "targets": [ 4 ]
            },
            {
                "searchable": true,
                "data": "STATOESITO.value",
                "visible": true,
                "sTitle": "Stato esito",
                "width": (isTrovaProcedure !== "true" ? "12%": "8%"),
                "class": "datadescr",
                "targets": [ 5 ]
            },
            {
                "searchable": true,
                "data": "SCHEDECOMPILAZIONE.value",
                "visible": true,
                "sTitle": "Schede in compilazione",
                "width": "9%",
                "class": "datadescr",
                "targets": [ 6 ]
            },
            {
                "searchable": true,
                "data": "SCHEDEATTESAERRORI.value",
                "visible": true,
                "sTitle": "Schede inviate in attesa o con errori",
                "width": "9%",
                "targets": [ 7 ],
                createdCell: function (td, cellData, rowData, row, col) {
                    if (cellData && cellData !== null && cellData !== '') {
                        var $div = $('<div>', {
                            class: 'schedeAttesaErrori',
                            text: cellData
                        });
                        $(td).html($div);
                    } else {
                        $(td).text(''); // Clear the cell content if data is empty or null
                    }
                }
            },
            {
                "searchable": true,
                "data": "SCHEDESUCCESSO.value",
                "visible": true,
                "sTitle": "Schede inviate con successo",
                "width": "9%",
                "targets": [ 8 ],
                createdCell: function (td, cellData, rowData, row, col) {
                    if (cellData && cellData !== null && cellData !== '') {
                        var $div = $('<div>', {
                            class: 'schedeSuccesso',
                            text: cellData
                        });
                        $(td).html($div);
                    } else {
                        $(td).text(''); // Clear the cell content if data is empty or null
                    }
                }
            },
            {
                "searchable": true,
                "visible": (isTrovaProcedure === "true"),
                "sTitle": "Profilo",
                "width": "10%",
                "class": "datadescr",
                "targets": [ 9 ],
                "render": function (data, type, full) {
                    var _div = $("<div/>");
                    if (isTrovaProcedure === "true") {
                        var linkApriGara = $('<span>', {
                            text : full.CODPROFILO.value
                        });
                        _div.append(linkApriGara);
                    }
                    return _div.html();
                }
            },
            {
                "searchable": false,
                "visible": (isTrovaProcedure === "true"),
                "sTitle": "",
                "width": "1%",
                "targets": [ 11 ],
                "render": function (data, type, full) {
                    var _div = $("<div/>");
                    if (isTrovaProcedure === "true") {
                        let href = 'javascript:visualizzaGaraProfiloReport("' + full.CODICEGARA.value + '","' + full.CENINT.value + '","' + full.CODPROFILO.value + '","' + full.GENERE.value + '");';
                        var linkApriGara = $('<a>', {
                            href: href
                        });
                        var immagineVaiProfilo = $('<img>', {
                            width: '16',
                            height: '16',
                            title: 'Vai al profilo',
                            alt: 'Vai al profilo',
                            src: 'img/accediGaraProfilo.png'
                        });
                        linkApriGara.append(immagineVaiProfilo);
                        _div.append(linkApriGara);
                    }
                    return _div.html();
                }
            },
            {
                "searchable": false,
                "visible": true,
                "sTitle": "",
                "width": "1%",
                "targets": [ 10 ],
                "render": function (data, type, full) {
                    var _div = $("<div/>");
                    if (full.GARATERMINATA.value !== null && full.GARATERMINATA.value === false && (full.DITTA.value != null || full.ESINEG.value != null)) {
                        var immagineAlert = $('<img>', {
                            width: '16',
                            height: '16',
                            title: 'Gara aggiudicata o annullata senza scheda esito inviata con successo',
                            alt: 'Gara aggiudicata o annullata senza scheda esito inviata con successo',
                            src: 'img/isquantimod.png'
                        });
                        _div.append(immagineAlert);
                    }
                    return _div.html();
                }
            }
        ],
        "aoColumns": [
            { "bSortable": true, "bSearchable": true },
            { "bSortable": true, "bSearchable": true },
            { "bSortable": true, "bSearchable": true },
            { "bSortable": true, "bSearchable": true },
            { "bSortable": true, "bSearchable": true },
            { "bSortable": true, "bSearchable": true },
            { "bSortable": false, "bSearchable": true },
            { "bSortable": false, "bSearchable": true },
            { "bSortable": false, "bSearchable": true },
            { "bSortable": true, "bSearchable": true },
            { "bSortable": false, "bSearchable": false },
            { "bSortable": false, "bSearchable": false }
        ],
        language: {
            "sEmptyTable":     "Non ci sono gare da visualizzare",
            "sInfo":           "Visualizzazione da _START_ a _END_ di _TOTAL_ gare",
            "sInfoEmpty":      "Non ci sono gare da visualizzare",
            "sInfoFiltered":   "",
            "sInfoPostFix":    "",
            "sInfoThousands":  ",",
            "sLengthMenu":     "Visualizza _MENU_",
            "sLoadingRecords": "",
            "sProcessing":     "Elaborazione...",
            "sSearch":         "",
            "sZeroRecords":    "Non ci sono gare da visualizzare",
            "oPaginate": {
                "sFirst":      "<<",
                "sPrevious":   "<",
                "sNext":       ">",
                "sLast":       ">>"
            }
        },
        "initComplete": function (oSettings, jso) {
            var _iTotalRecords = oSettings.fnRecordsTotal();
            if (_iTotalRecords == 0) {
                $("#gare tfoot").hide();
                $("#gare_info").hide();
                $("#gare_paginate").hide();
            }
        },
        "pagingType": "full_numbers",
        "lengthMenu": [[10, 25, 50, -1], ["10 gare", "25 gare", "50 gare", "Tutte le gare"]],
        "dom" : 'l<"#add">frtip'
    });

    let tableResultXpage= $('#gare').DataTable().page.info()?.length;
    let page= $('#gare').DataTable().page();
    $('#gare').DataTable().page.len(-1).draw();
    $('#gare').DataTable().page.len(tableResultXpage)?.draw();
    $('#gare').DataTable().page(page).draw(false);

    let columnNo = 0;
    $('#gare tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca gara"/>' );
    $('#gare tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca lotto"/>' );
    $('#gare tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca CIG"/>' );
    $('#gare tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca oggetto"/>' );
    $('#gare tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca RP"/>' );
    $('#gare tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca stato esito"/>' );
    $('#gare tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca schede"/>' );
    $('#gare tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca schede"/>' );
    $('#gare tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca schede"/>' );

    if (isTrovaProcedure === 'true') {
        $('#gare tfoot td').eq(columnNo++).html( '<input class="search" size="10" type="text" placeholder="Ricerca profilo"/>' );
    }

    _tableGare.columns().eq(0).each( function (colIdx) {
        $('input', _tableGare.column(colIdx).footer()).on( 'keyup change', function () {
            _tableGare.column(colIdx).search(this.value).draw();
        });
    });

    $("#gare_filter").hide();
}
