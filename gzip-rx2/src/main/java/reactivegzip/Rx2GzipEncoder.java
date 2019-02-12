package reactivegzip;


import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import java.nio.ByteBuffer;

public class Rx2GzipEncoder {

	private final boolean syncFlush;

	public Rx2GzipEncoder() {
		this(false);
	}

	public Rx2GzipEncoder(boolean syncFlush) {
		this.syncFlush = syncFlush;
	}

	public Flowable<ByteBuffer> encode(Publisher<ByteBuffer> input) {
		NonblockingGzipEncoder encoder = new NonblockingGzipEncoder(syncFlush);
		return Flowable.concat(
				Flowable.fromPublisher(input).map(encoder::encode),
				Flowable.defer(() -> Flowable.just(encoder.endOfInput())));
	}
}
