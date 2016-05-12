import java.util.function.Consumer;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class Main {

	public static void main(String[] args) {

		VertxOptions options = new VertxOptions().setClustered(true).setClusterHost("10.69.65.58").setHAEnabled(true)
				.setHAGroup("dev");

		Vertx.clusteredVertx(options, res -> System.out.println(res.succeeded()));

		Consumer<Vertx> runner = vertx -> {
			try {
				vertx.deployVerticle(new ReceiverVerticle());
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
	}
}
