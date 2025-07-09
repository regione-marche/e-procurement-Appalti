package it.eldasoft.sil.pg.npa.model;

public class ComunicazioneID<TipoCup> {

    private EnumNPA motivoRichiestaModifica;
    private TipoCup cup;

    public EnumNPA getMotivoRichiestaModifica() {
        return motivoRichiestaModifica;
    }

    public void setMotivoRichiestaModifica(EnumNPA motivoRichiestaModifica) {
        this.motivoRichiestaModifica = motivoRichiestaModifica;
    }

    public TipoCup getCup() {
        return cup;
    }

    public void setCup(TipoCup cup) {
        this.cup = cup;
    }

}
