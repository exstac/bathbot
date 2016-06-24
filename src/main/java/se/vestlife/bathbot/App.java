package se.vestlife.bathbot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotify.apollo.Response;
import com.spotify.apollo.httpservice.HttpService;
import com.spotify.apollo.httpservice.LoadingException;
import com.spotify.apollo.route.Route;

import se.vestlife.bathbot.resources.BathroomResource;

public final class App {

  public static void main(String[] args) throws LoadingException {
    HttpService.boot(environment -> {
      ObjectMapper objectMapper = new ObjectMapper();
      BathroomResource bathroomResource = new BathroomResource();

      environment.routingEngine()
          .registerRoutes(bathroomResource.routes())
          .registerRoute(Route.sync("GET", "/ping", rc -> Response.ok()));
    }, "bathbot", args);
  }

}