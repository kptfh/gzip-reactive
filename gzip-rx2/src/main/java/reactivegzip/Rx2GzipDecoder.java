package reactivegzip;


import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import java.nio.ByteBuffer;

public class Rx2GzipDecoder {

	public Flowable<ByteBuffer> decode(Publisher<ByteBuffer> input) {
		NonblockingGzipDecoder decoder = new NonblockingGzipDecoder();
		return Flowable.fromPublisher(input).map(decoder::decode)
				.doFinally(decoder::destroy);
	}

}
