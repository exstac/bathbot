package se.vestlife.bathbot.resources;

import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.route.AsyncHandler;
import com.spotify.apollo.route.Route;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import okio.ByteString;

import static com.spotify.apollo.route.Middlewares.autoSerialize;

public class BathroomResource {

  private final Map<Integer, BathroomState> state;

  public BathroomResource() {
    state = new HashMap<>();
  }

  public Stream<Route<AsyncHandler<Response<ByteString>>>> routes() {
    return Stream.of(
        Route.async("PUT", "/bathroom", this::register),
        Route.async("POST", "/bathroom", this::update),
        Route.async("POST", "/bathrooms", autoSerialize(ctx -> listBathrooms()))
    );
  }

  private CompletionStage<Response<Map<Integer, BathroomState>>> listBathrooms() {
    return CompletableFuture.completedFuture(Response.forPayload(state));
  }

  private CompletionStage<Response<ByteString>> update(RequestContext ctx) {
    final Optional<ByteString> payload = ctx.request().payload();
    final String parameter = payload.isPresent() ? payload.get().utf8() : "no payload";
    System.out.println("POST /bathroom " + parameter);
    return CompletableFuture.completedFuture(Response.ok());
  }

  private CompletionStage<Response<ByteString>> register(RequestContext ctx) {
    Request request = ctx.request();
    Optional<ByteString> payload = request.payload();

    return CompletableFuture.completedFuture(Response.ok());
  }
}
