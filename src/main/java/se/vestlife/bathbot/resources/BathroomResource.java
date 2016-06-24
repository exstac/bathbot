package se.vestlife.bathbot.resources;

import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.route.AsyncHandler;
import com.spotify.apollo.route.Route;

import se.vestlife.bathbot.model.Bathroom;
import se.vestlife.bathbot.model.BathroomState;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import okio.ByteString;

import static com.spotify.apollo.route.Middlewares.autoSerialize;

public class BathroomResource {

  private final Map<String, BathroomState> state;

  public BathroomResource() {
    state = new HashMap<>();
  }

  public Stream<Route<AsyncHandler<Response<ByteString>>>> routes() {
    return Stream.of(
        Route.async("PUT", "/bathroom/<floor>", ctx -> registerDevice(ctx.pathArgs().get("floor"), ctx)),
        Route.async("POST", "/bathroom/<id>", ctx -> update(ctx.pathArgs().get("id"))),
        Route.async("POST", "/bathrooms", autoSerialize(ctx -> listDevices()))
    );
  }

  private CompletionStage<Response<Map<String, BathroomState>>> listDevices() {
    return CompletableFuture.completedFuture(Response.forPayload(state));
  }

  private CompletionStage<Response<ByteString>> update(String deviceId) {
    return null;
  }

  private CompletionStage<Response<ByteString>> registerDevice(String deviceId, RequestContext ctx) {
    Optional<ByteString> payload = ctx.request().payload();
    return null;
  }
}
