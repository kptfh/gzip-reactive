package reactivegzip;


import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;

public class ReactorGzipEncoder {

	private final boolean syncFlush;

	public ReactorGzipEncoder() {
		this(false);
	}

	public ReactorGzipEncoder(boolean syncFlush) {
		this.syncFlush = syncFlush;
	}

	public Flux<ByteBuffer> encode(Publisher<ByteBuffer> input) {
		NonblockingGzipEncoder encoder = new NonblockingGzipEncoder(syncFlush);
		return Flux.concat(
				Flux.from(input).map(encoder::encode),
				Flux.defer(() -> Flux.just(encoder.endOfInput())));
	}
}
