//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

public class Processo {
    private Integer chegada;
    private Integer pico;

    public Processo(Integer chegada, Integer pico) {
        this.chegada = chegada;
        this.pico = pico;
    }

    public Integer getChegada() {
        return this.chegada;
    }

    public void setChegada(Integer chegada) {
        this.chegada = chegada;
    }

    public Integer getPico() {
        return this.pico;
    }

    public void setPico(Integer pico) {
        this.pico = pico;
    }

    public String toString() {
        return "chegada = " + this.chegada + ", pico = " + this.pico;
    }
}
