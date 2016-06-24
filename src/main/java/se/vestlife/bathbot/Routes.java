package se.vestlife.bathbot;

import com.spotify.apollo.Environment;
import com.spotify.apollo.route.Route;

class Routes {
  static void register(Environment environment) {
    environment.routingEngine().registerAutoRoute (
        Route.sync("GET", "/", rc -> "hello world")
    );
  }
}
