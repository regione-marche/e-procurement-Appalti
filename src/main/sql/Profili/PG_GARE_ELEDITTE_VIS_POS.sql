--SET search_path = public, pg_catalog;

SET statement_timeout = 0;
-- decommentare la riga seguente in caso di lancio da interprete di comando direttamente da linux
--SET client_encoding = 'LATIN1';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

-- inserimento associazione (utente,profilo)
Insert into W_ACCPRO (ID_ACCOUNT,COD_PROFILO) select 48,'PG_GARE_ELEDITTE_VIS' from W_GENCHIAVI where TABELLA='W_GRUPPI' and not exists (select * from W_PROFILI where cod_profilo = 'PG_GARE_ELEDITTE_VIS');
Insert into W_ACCPRO (ID_ACCOUNT,COD_PROFILO) select 49,'PG_GARE_ELEDITTE_VIS' from W_GENCHIAVI where TABELLA='W_GRUPPI' and not exists (select * from W_PROFILI where cod_profilo = 'PG_GARE_ELEDITTE_VIS');
Insert into W_ACCPRO (ID_ACCOUNT,COD_PROFILO) select 50,'PG_GARE_ELEDITTE_VIS' from W_GENCHIAVI where TABELLA='W_GRUPPI' and not exists (select * from W_PROFILI where cod_profilo = 'PG_GARE_ELEDITTE_VIS');
-- inserimento eventuale gruppo
Update W_GENCHIAVI Set CHIAVE=CHIAVE+1 where TABELLA='W_GRUPPI' and not exists (select 1 from W_GRUPPI where cod_profilo='PG_GARE_ELEDITTE_VIS');
Insert into W_GRUPPI (ID_GRUPPO,NOME,DESCR,COD_PROFILO) Select (select CHIAVE from W_GENCHIAVI where TABELLA='W_GRUPPI'),'Elenchi operatori economici - sola consultazione','default per il profilo PG_GARE_ELEDITTE_VIS','PG_GARE_ELEDITTE_VIS' from W_GENCHIAVI where tabella='W_GRUPPI' and not exists (select * from W_GRUPPI where cod_profilo='PG_GARE_ELEDITTE_VIS');
-- inserimento associazione (utente,gruppo)
Insert into W_ACCGRP (ID_ACCOUNT,ID_GRUPPO,PRIORITA) select 48,ID_GRUPPO,0 from W_GRUPPI where COD_PROFILO='PG_GARE_ELEDITTE_VIS'  and not exists (select * from W_ACCGRP A inner join W_GRUPPI G on A.id_gruppo=G.id_gruppo where G.cod_profilo='PG_GARE_ELEDITTE_VIS' and A.id_account=48);
Insert into W_ACCGRP (ID_ACCOUNT,ID_GRUPPO,PRIORITA) select 49,ID_GRUPPO,0 from W_GRUPPI where COD_PROFILO='PG_GARE_ELEDITTE_VIS'  and not exists (select * from W_ACCGRP A inner join W_GRUPPI G on A.id_gruppo=G.id_gruppo where G.cod_profilo='PG_GARE_ELEDITTE_VIS' and A.id_account=49);
Insert into W_ACCGRP (ID_ACCOUNT,ID_GRUPPO,PRIORITA) select 50,ID_GRUPPO,0 from W_GRUPPI where COD_PROFILO='PG_GARE_ELEDITTE_VIS'  and not exists (select * from W_ACCGRP A inner join W_GRUPPI G on A.id_gruppo=G.id_gruppo where G.cod_profilo='PG_GARE_ELEDITTE_VIS' and A.id_account=50);

-- pulizia profilo eseguita sempre
Delete from W_PROAZI where COD_PROFILO='PG_GARE_ELEDITTE_VIS';
Delete from W_PROFILI where COD_PROFILO='PG_GARE_ELEDITTE_VIS';

-- inserimento (o reinserimento) testata e configurazione profilo
INSERT INTO W_PROFILI (COD_PROFILO,CODAPP,NOME,DESCRIZIONE,FLAG_INTERNO,DISCRIMINANTE,COD_CLIENTE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','PG','Elenchi operatori economici - sola consultazione','Sistema per l''iscrizione degli operatori economici agli elenchi per categorie SOA o merceologiche (o albi fornitori), qualificazione e selezione degli operatori - SOLA CONSULTAZIONE',1,null,0,3935871363);

INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','VIS','*',1,4179291998);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','MOD','GARE.DOCUMGARA.BUSTA',0,2610374576);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','VIS','GARE.DOCUMGARA.BUSTA',0,1326173024);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','MOD','GARE.DOCUMGARA.FASGAR',0,1179545152);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','VIS','GARE.DOCUMGARA.FASGAR',0,3226017912);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','VIS','GARE.GARE.CLAVOR',0,1810123361);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','VIS','GARE.GARE.CODCOM',0,2400001676);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','VIS','GARE.GARE.NUMERA',0,3701939251);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','VIS','GARE.GARSTR.*',0,32615009);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','MOD','GARE.PUBBTERM.DSORTEGGIO',0,2000460712);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','VIS','GARE.PUBBTERM.DSORTEGGIO',0,2916800558);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','MOD','GARE.PUBBTERM.NPUBAVVBAN',0,4127419470);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','VIS','GARE.PUBBTERM.NPUBAVVBAN',0,753217480);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','MOD','GARE.PUBBTERM.OSORTEGGIO',0,1411194092);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','VIS','GARE.PUBBTERM.OSORTEGGIO',0,2398838634);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','VIS','GARE.TORN.UFFDET',1,19799014);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','MOD','GARE.V_DITGAMMIS.FASGAR',0,3430308729);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','VIS','GARE.V_DITGAMMIS.FASGAR',0,1250757906);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','COLS','VIS','GARE.V_GARE_TORN.ISLOTTI',0,2877818399);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.AcquisiciIscrizioniElencoPortale',0,139231444);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.AcquisiciIscrizioniElencoPortaleMassivo',0,2136587416);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.ArchiviaDocumenti',0,1443767465);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.DITG-listaImportoAggiudicatoOperatori.ConteggioImporto',0,1519075212);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.ExportDocumenti',0,3772046739);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.FASIRICEZIONE.RiassegnaNumOrdine',0,4168003666);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.GARE-scheda.FASIISCRIZIONE.AbilitaOperatori',0,3018682710);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.GARE-scheda.FASIISCRIZIONE.MOD-FASE-3',0,455969580);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.GARE-scheda.FASIISCRIZIONE.MOD-FASE-4',0,1416371691);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.GARE-scheda.FASIISCRIZIONE.MOD-FASE-5',0,1299655850);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.GARE-scheda.FASIISCRIZIONE.RegistraImpresePortale',0,1245587950);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.GARE-scheda.FASIISCRIZIONE.VerificaOpertoriArt80',0,4219063667);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.GARE-scheda.FASIRICEZIONE.AssegnaNumOrdine',0,1727320959);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.GARE-scheda.PUBBANDO.InsertPredefiniti',0,1724237528);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.GARE-scheda.PUBBANDO.PubblicaSuPortale',1,46146152);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.GARE.AssociaGaraAppalto',0,3149509144);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.GARE.modassociazionecategorie',0,3327646157);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.GARE.RiceviComunicazioni',0,409919338);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.InviaAttiSCP',0,2734938593);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.InvioComunicazione',0,1858887225);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.ISCRIZCAT-lista.AssegnaPenalita',0,1638450506);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.ModificaOrdinamentoDocumentazione',0,3423983461);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.NotificaAggiornamentiCategorieElenco',0,1316632084);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.NotificaRinnoviIscrizioneElenco',0,3015745248);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.PubblicaSuPortale',0,4269460977);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.TORN-OFFUNICA-scheda.PUBBANDO.PubblicaSuPortale',1,106736653);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.TORN-scheda.PUBBANDO.PubblicaSuPortale',1,3659309325);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.TrasferisciAlDocumentale',0,1139392739);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.TrasferisciCos',0,2462609288);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.V_GARE_ELEDITTE-lista.ApriGare',1,1928126868);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.V_GARE_ELEDITTE-lista.Archivia-gara',0,2592379682);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.V_GARE_ELEDITTE-lista.Condividi-gara',0,1015202022);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GARE.V_GARE_ELEDITTE-lista.ElencoOpSorteggio',0,942521481);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GENE.ImprScheda.InviaMailAttivazioneSuPortale',0,3357538419);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','ALT.GENE.ImprScheda.SoggettoDelegaPortaleAppalti',0,2905001123);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','DEL.*',0,387120184);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','DEL.GARE.TORN-scheda.LOTTI.LISTADELSEL',0,530712220);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','INS.*',0,3559299390);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','INS.GARE.GARE-scheda.ALTRIDATI.INS-TRSTRADA',0,1103018212);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','INS.GARE.V_GARE_TORN-trova.TROVANUOVO',0,4178177032);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','FUNZ','VIS','MOD.*',0,3206127038);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','MASC','VIS','GARE.V_GARE_TORN-lista',0,3390890272);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','MENU','VIS','LAVORI',0,295892177);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','PAGE','VIS','GARE.GARE-scheda.CRITERIELEDITTE',1,2845314038);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','SEZ','VIS','GARE.GARE-scheda.ALTRIDATI.TRSTRADA',0,3349940915);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','SEZ','VIS','GARE.GARE-scheda.DATIGEN.RILA',0,41193655);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','SEZ','VIS','GARE.GARE-scheda.DOCUMGARA.DOCUMREQ',1,27187719);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','SUBMENU','VIS','ARCHIVI.Archivio-uffici-intestatari',1,3519115947);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','SUBMENU','VIS','ARCHIVI.Archivio-utenti',0,3100152466);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','SUBMENU','VIS','GARE.Trova-elenchi',1,802567953);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','SUBMENU','VIS','GARE.Trova-gare',0,2624564260);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','TABS','VIS','*',1,1947117961);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','TABS','VIS','GARE.GARSTR',0,718659715);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','TABS','VIS','GARE.V_DITTE_PRIT',0,2225114875);
INSERT INTO W_PROAZI (COD_PROFILO,TIPO,AZIONE,OGGETTO,VALORE,CRC) VALUES ('PG_GARE_ELEDITTE_VIS','TABS','VIS','GARE.V_GARE_NSCAD',0,762248012);
