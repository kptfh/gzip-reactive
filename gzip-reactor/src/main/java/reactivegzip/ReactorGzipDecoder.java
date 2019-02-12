package reactivegzip;


import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;

public class ReactorGzipDecoder {

	public Flux<ByteBuffer> decode(Publisher<ByteBuffer> input) {
		NonblockingGzipDecoder decoder = new NonblockingGzipDecoder();
		return Flux.from(input).map(decoder::decode)
				.doFinally(signalType -> decoder.destroy());
	}
}
