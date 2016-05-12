import io.vertx.core.AbstractVerticle;

public class ReceiverVerticle extends AbstractVerticle {

	@Override
	public void start() throws Exception {
		vertx.eventBus().consumer("news.uk.sport", message -> {
            System.out.println("1 received message.body() = "
                + message.body());
        });
		
		vertx.createHttpServer().requestHandler(r -> {
			r.response().end("<h1> Hello World, from my first Vert.x 3.2.1 application</h1>");

		}).listen(8081, result -> {
			if (result.succeeded()) {
				System.out.println("Sucess!");
			} else {
				System.out.println("Failed!");
			}
		});
	}
}
