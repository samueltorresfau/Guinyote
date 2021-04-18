package manyosoft.guinyote.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.JsonToken;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class GuinyoteClienteJWT implements Serializable {
    static final String LOCALHOST = "http://10.0.2.2:9000/";
    static final String REMOTEHOST = "http://15.188.14.213:11050/";
    static final String HOST = LOCALHOST;


    // Usuarios
    static final String CREATE_USER = "api/v1/users/";
    static final String LOGIN = "api/v1/users/login/";
    static final String DELETE_USER = "api/v1/users/";
    static final String GET_USER = "api/v1/users/";
    static final String UPDATE_USER = "api/v1/users/";

    // Partidas
    static final String GET_PUBLICAS = "api/v1/games/";
    static final String GET_USER_GAMES = "api/v1/games/user/";
    static final String CREATE_GAME = "api/v1/games/";
    static final String GET_GAME = "api/v1/games/";

    // Players
    static final String JOIN_PAREJA = "api/v1/players/";

    private static GuinyoteClienteJWT instance = null;

    String token;

    protected GuinyoteClienteJWT() {
        this.token = null;
    }

    public static synchronized GuinyoteClienteJWT getInstance() {
        if (instance == null) {
            instance = new GuinyoteClienteJWT();
        }
        return instance;
    }

    public String getToken() {
        return "Bearer: " + token;
    }


    public String getTokenTesting() {
        return "Bearer: " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MX0.Qi0Tc-jTChzascHaZhl0e6rRaCvAS6OJ8RLsI8Y-R78";
    }

    public boolean loginUsuario(Context context, String username, String password) throws ExecutionException, InterruptedException {

        boolean error = true;
        final String usuarioJSON = "username";
        final String passwordJSON = "password";
        final String tokenJSON = "token";

        // Objeto JSON a pasar al backend
        JsonObject json = new JsonObject();
        json.addProperty(usuarioJSON, username);
        json.addProperty(passwordJSON, password);

        // Envío + Callback para la respuesta
        JsonObject result = Ion.with(context)
                .load("POST", HOST + LOGIN)
                .setJsonObjectBody(json)
                .asJsonObject()
                .get();
        if (result.get(tokenJSON) != null) {//comprobar que devuelve algo, si no devuelve es que no ha encontrado el usuario en la base
            token = result.get(tokenJSON).getAsString();
            error = false;
        }
        return error;

    }

    public boolean crearUsuario(Context context, String location, String usuario, String mail, String password) throws ExecutionException, InterruptedException {
        // Constantes para trabajar con JSON
        final String usuarioObjetoJSON = "user";
        final String usuarioJSON = "username";
        final String mailJSON = "email";
        final String locationJSON = "location";
        final String passwordJSON = "password";

        // Objeto JSON a pasar al backend
        JsonObject json = new JsonObject();
        json.addProperty(usuarioJSON, usuario);
        json.addProperty(mailJSON, mail);
        json.addProperty(locationJSON, location);
        json.addProperty(passwordJSON, password);

        // Envío + Callback para la respuesta
        JsonObject result = Ion.with(context)
                .load("POST", HOST + CREATE_USER)
                .setJsonObjectBody(json)
                .asJsonObject()
                .get();
        if (result.get(usuarioObjetoJSON) != null) {
            return false;
        }
        return true;
    }

    public Usuario getUsuario(Context context, String username) throws ExecutionException, InterruptedException {
        final String idUsuario = "id";
        final String usernameUsuario = "username";
        final String emailUsuario = "email";
        final String locationUsuario = "location";
        final String createdUsuario = "created_at";
        final String updatedUsuario = "updated_at";

        // Espera síncrona
        JsonObject respuesta = Ion.with(context)
                .load("GET", HOST + GET_USER + username)
                .setHeader("Authorization", getToken())  // Token de autorización
                .asJsonObject()
                .get();
        if (respuesta.get("user") != null) {
            return new Usuario(
                    respuesta.get("user").getAsJsonObject().get(idUsuario).getAsInt(),
                    respuesta.get("user").getAsJsonObject().get(usernameUsuario).getAsString(),
                    respuesta.get("user").getAsJsonObject().get(emailUsuario).getAsString(),
                    respuesta.get("user").getAsJsonObject().get(locationUsuario).getAsString(),
                    respuesta.get("user").getAsJsonObject().get(createdUsuario).getAsString(),
                    respuesta.get("user").getAsJsonObject().get(updatedUsuario).getAsString());
        } else {
            return null;
        }
    }

    public void updateUsuario(Context context, Integer id, String email, String location) {
        final String emailJSON = "email";
        final String locationJSON = "location";

        JsonObject json = new JsonObject();
        if (email != null) json.addProperty(emailJSON, email);
        if (location != null) json.addProperty(locationJSON, location);

        // Envía la petición PUT y no espera respuesta alguna
        Ion.with(context)
                .load("PUT", HOST + UPDATE_USER + id.toString())
                .setHeader("Authorization", getToken())  // Token de autorización
                .setJsonObjectBody(json);
    }

    public void deleteUsuario(Context context, Integer id) {
        // Envía la petición DELETE y no espera respuesta alguna
        Ion.with(context)
                .load("DELETE", HOST + DELETE_USER + id.toString())
                .setHeader("Authorization", getToken());  // Token de autorización
    }

    public ArrayList<Partida> getPartidasPublicas(Context context) throws ExecutionException, InterruptedException {
        ArrayList<Partida> partidasRecuperadas = new ArrayList<Partida>();

        // Espera síncrona
        JsonObject partidasJSON = Ion.with(context)
                .load("GET", HOST + GET_PUBLICAS)
                .setHeader("Authorization", getTokenTesting())  // Token de autorización
                .asJsonObject()
                .get();

        Log.d("Mensaje Partidas Recibido", partidasJSON.toString());

        JsonArray partidasJSONArray = partidasJSON.getAsJsonArray("games");
        if (partidasJSONArray == null) return partidasRecuperadas;
        else {
            for (JsonElement par : partidasJSONArray) {
                JsonObject parObj = par.getAsJsonObject();
                partidasRecuperadas.add(
                        new Partida(
                                parObj.get("id").getAsLong(),
                                parObj.get("name").getAsString(),
                                parObj.get("players_count").getAsInt(),
                                parObj.get("creation_date").getAsString(),
                                parObj.get("end_date").getAsString()
                        )
                );
            }
            // Devuelve el listado de partidas publicas recuperadas del backend
            return partidasRecuperadas;
        }
    }

    public Partida createAndJoinGame(Context context, Integer userID,String nombre,boolean publica) throws ExecutionException, InterruptedException {
        Partida creada = null;

        JsonObject json = new JsonObject();
        if (nombre != null) json.addProperty("name", nombre);
        json.addProperty("public", publica);

        // Espera síncrona
        JsonObject partidaJSON = Ion.with(context)
                .load("POST", HOST + CREATE_GAME+userID)
                .setHeader("Authorization", getTokenTesting())  // Token de autorización
                .setJsonObjectBody(json)
                .asJsonObject()
                .get();

        if (partidaJSON.get("game") != null) {
            creada = new Partida(
                    partidaJSON.getAsJsonObject("game").get("id").getAsLong(),
                    partidaJSON.getAsJsonObject("game").get("name").getAsString(),
                    partidaJSON.getAsJsonObject("game").get("players_count").getAsInt(),
                    partidaJSON.getAsJsonObject("game").get("creation_date").getAsString(),
                    partidaJSON.getAsJsonObject("game").get("end_date").getAsString()
            );
        }

        return creada;
    }

    public ArrayList<Partida> getPartidasByUser(Context context, int userId) throws ExecutionException, InterruptedException {
        ArrayList<Partida> partidasRecuperadas = new ArrayList<Partida>();

        // Espera síncrona
        JsonObject partidasJSON = Ion.with(context)
                .load("GET", HOST + GET_USER_GAMES + userId)
                .setHeader("Authorization", getTokenTesting())  // Token de autorización
                .asJsonObject()
                .get();

        Log.d("Partidas Usuario", partidasJSON.toString());

        JsonArray partidasJSONArray = partidasJSON.getAsJsonArray("games");
        if (partidasJSONArray != null) {
            for (JsonElement par : partidasJSONArray) {
                JsonObject parObj = par.getAsJsonObject();
                partidasRecuperadas.add(
                        new Partida(
                                parObj.get("id").getAsLong(),
                                parObj.get("name").getAsString(),
                                parObj.get("players_count").getAsInt(),
                                parObj.get("creation_date").getAsString(),
                                parObj.get("end_date").getAsString(),
                                parObj.get("points").getAsInt(),
                                parObj.get("winned").getAsBoolean()
                        )

                );
            }
            // Devuelve el listado de partidas publicas recuperadas del backend
        }
        return partidasRecuperadas;
    }
}

    public ArrayList<String> getJugadores(Context context, Long idPartida) throws ExecutionException, InterruptedException {
        ArrayList<String> jugadores = new ArrayList<>(6);

        Ion.getDefault(context).getConscryptMiddleware().enable(false);

        JsonObject game = Ion.with(context)
                .load("GET",HOST+GET_PUBLICAS)
                .setHeader("Authorization", getToken())  // Token de autorización
                .asJsonObject()
                .get();

        JsonArray parejasJsonArray = game.getAsJsonArray("pairs");
        JsonElement parejaElem1 = parejasJsonArray.get(0), parejaElem2 = parejasJsonArray.get(0);
        JsonObject pareja1, pareja2;
        pareja1 = parejaElem1.getAsJsonObject(); pareja2 = parejaElem2.getAsJsonObject();
        JsonArray jugadoresPareja1 = pareja1.getAsJsonArray("users");
        JsonArray jugadoresPareja2 = pareja2.getAsJsonArray("users");

        JsonElement jugador11, jugador12, jugador21, jugador22;
        try {
            jugador11 = jugadoresPareja1.get(0);
        } catch (IndexOutOfBoundsException e)   {
            jugador11 = null;
        }
        try {
            jugador12 = jugadoresPareja1.get(1);
        } catch (IndexOutOfBoundsException e)   {
            jugador12 = null;
        }
        try {
            jugador21 = jugadoresPareja2.get(0);
        } catch (IndexOutOfBoundsException e)   {
            jugador21 = null;
        }
        try {
            jugador22 = jugadoresPareja2.get(1);
        } catch (IndexOutOfBoundsException e)   {
            jugador22 = null;
        }

        if(jugador11 == null) jugadores.set(0, null);
        else                  jugadores.set(0, jugador11.getAsJsonObject().get("username").getAsString());
        if(jugador12 == null) jugadores.set(1, null);
        else                  jugadores.set(1, jugador12.getAsJsonObject().get("username").getAsString());
        if(jugador21 == null) jugadores.set(2, null);
        else                  jugadores.set(2, jugador21.getAsJsonObject().get("username").getAsString());
        if(jugador22 == null) jugadores.set(3, null);
        else                  jugadores.set(3, jugador22.getAsJsonObject().get("username").getAsString());

        if(pareja1 == null) jugadores.set(0, null);
        else                jugadores.set(0, ((Long)pareja1.get("id").getAsLong()).toString());
        if(pareja2 == null) jugadores.set(0, null);
        else                jugadores.set(0, ((Long)pareja2.get("id").getAsLong()).toString());

        return jugadores;
    }


    public Long joinGame(Context context, Long idJugador, Long idPareja)  {
        // Objeto JSON a pasar al backend
        JsonObject json = new JsonObject();
        json.addProperty("user_id", idJugador);
        json.addProperty("pair_id", idPareja);


        // Retorna el id del jugador (-1 si hay algun problema)
        Long id = null;
        try {
            Ion.getDefault(context).getConscryptMiddleware().enable(false);
            // Espera síncrona
            JsonObject respuesta = Ion.with(context)
                    .load("POST",HOST+JOIN_PAREJA)
                    .setHeader("Authorization", getToken())  // Token de autorización
                    .asJsonObject()
                    .get();

            id = respuesta.get("player").getAsJsonObject().get("id").getAsLong();
        } catch (Exception e)   {
            id = -1L;
        }

        return id;
    }
}
