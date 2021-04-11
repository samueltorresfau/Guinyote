package manyosoft.guinyote.util;

public class Partida {
    private Long id;
    private String nombre;
    private Integer jugadores;
    private String created, end;

    /**
     *  Resto de elementos necesarios para controlar la partida
     */

    public Partida(Long id, String name, Integer players)   {
        if(id != null && name != null && players != null)   {
            this.id = id;
            this.nombre = name;
            this.jugadores = players;
        }
    }


    public Partida(Long id, String name, Integer players, String created, String end)   {
        if(id != null && name != null && players != null)   {
            this.id = id;
            this.nombre = name;
            this.jugadores = players;
            this.created = created;
            this.end = end;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getJugadores() {
        return jugadores;
    }

    public void setJugadores(Integer jugadores) {
        this.jugadores = jugadores;
    }

    public String  getCreated(){return created;}

    public void setCreated(String created) {this.created = created;}

    public String getEnd() { return end;}

    public void setEnd(String end) {this.end = end;}
}