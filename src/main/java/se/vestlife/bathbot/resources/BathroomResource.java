package se.vestlife.bathbot.resources;

import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.StatusType;
import com.spotify.apollo.route.AsyncHandler;
import com.spotify.apollo.route.Route;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import okio.ByteString;

import static com.spotify.apollo.route.Middlewares.autoSerialize;

public class BathroomResource {

  // Floor -> free bathrooms
  private final Map<Integer, Integer> state;

  public BathroomResource() {
    state = new HashMap<>();
    state.put(4, 2);
    state.put(5, 2);
    state.put(6, 2);
  }

  public Stream<Route<AsyncHandler<Response<ByteString>>>> routes() {
    return Stream.of(
        Route.async("PUT", "/bathroom", this::register),
        Route.async("POST", "/bathroom", this::update),
        Route.async("GET", "/bathrooms", autoSerialize(ctx -> listBathrooms()))
    );
  }

  private CompletionStage<Response<Map<Integer, Integer>>> listBathrooms() {
    return CompletableFuture.completedFuture(Response.forPayload(state));
  }

  private CompletionStage<Response<ByteString>> update(RequestContext ctx) {
    final Optional<ByteString> payload = ctx.request().payload();
    if (!payload.isPresent()) {
      System.out.println("POST /bathroom <no payload>");
      return CompletableFuture.completedFuture(Response.forStatus(Status.BAD_REQUEST));
    }
    Map<String, Integer> parameters = new HashMap<>();
    for (String parameter : payload.get().utf8().split("&")) {
      parameters.put(parameter.split("=")[0], Integer.valueOf(parameter.split("=")[1]));
    }
    if (parameters.get("busy") == 1) {
      Integer floor = parameters.get("floor");
      Integer freeBaths = state.get(floor);
      state.put(floor, freeBaths - 1);
      System.out.println("Bathroom on floor " + floor + " is busy. " + state.get(floor) + " unoccupied baths on the floor");
    } else if (parameters.get("busy") == 0) {
      Integer floor = parameters.get("floor");
      Integer freeBaths = state.get(floor);
      state.put(floor, freeBaths + 1);
      System.out.println("Bathroom on floor " + floor + " is free. " + state.get(floor) + " unoccupied baths on the floor");
    }

    return CompletableFuture.completedFuture(Response.ok());
  }

  private CompletionStage<Response<ByteString>> register(RequestContext ctx) {
    Request request = ctx.request();
    Optional<ByteString> payload = request.payload();
    if (!payload.isPresent()) {
      return CompletableFuture.completedFuture(Response.forStatus(Status.BAD_REQUEST));
    }
    return CompletableFuture.completedFuture(Response.ok());
  }
}
