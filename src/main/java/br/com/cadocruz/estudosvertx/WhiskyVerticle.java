package br.com.cadocruz.estudosvertx;

import java.util.function.Consumer;

import com.google.inject.Guice;
import com.google.inject.Inject;

import br.com.cadocruz.estudosvertx.rest.WhiskyResource;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class WhiskyVerticle extends AbstractVerticle {

	@Inject
	private WhiskyResource whiskyController;

	@Override
	public void start(Future<Void> fut) throws Exception {

		Guice.createInjector().injectMembers(this);
		
		whiskyController.createSomeData();

		// Criando um Router
		Router router = Router.router(vertx);

		router.route("/").handler(rc -> {
			HttpServerResponse response = rc.response();
			response.putHeader("content-type", "text/html").end("<h1>Hello World!!!</h1>");
		});
		
		// Serve static resources from the /assets directory
		router.route("/assets/*").handler(StaticHandler.create("assets"));

		router.get("/api/whiskies").handler(whiskyController::getAll);
		router.route("/api/whiskies*").handler(BodyHandler.create());
		router.post("/api/whiskies").handler(whiskyController::addOne);
		router.delete("/api/whiskies/:id").handler(whiskyController::deleteOne);
		router.get("/api/whiskies/:id").handler(whiskyController::getOne);
		router.put("/api/whiskies/:id").handler(whiskyController::updateOne);

		vertx.createHttpServer()
			.requestHandler(router::accept)
			.listen(
				// Retrieve the port from the configuration,
				// default to 8080.
				config().getInteger("http.port", 8080), result -> {
					if (result.succeeded()) {
						fut.complete();
					} else {
						fut.fail(result.cause());
					}
				});
	}

	public static void main(String[] args) {
		Consumer<Vertx> runner = vertx -> {
			try {
				vertx.deployVerticle(new WhiskyVerticle());
			} catch (Throwable t) {
				t.printStackTrace();
			}
		};
		Vertx.clusteredVertx(new VertxOptions().setClustered(true), res -> {
			if (res.succeeded()) {
				Vertx vertx = res.result();
				runner.accept(vertx);
			} else {
				res.cause().printStackTrace();
			}
		});
		//vertx.deployVerticle(WhiskyVerticle.class.getName());
	}
}
