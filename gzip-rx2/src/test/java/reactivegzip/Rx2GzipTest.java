package reactivegzip;

import io.reactivex.Flowable;
import io.reactivex.Single;
import org.junit.Test;

import java.nio.ByteBuffer;

public class Rx2GzipTest {

	private final Rx2GzipEncoder encoder = new Rx2GzipEncoder();
	private final Rx2GzipDecoder decoder = new Rx2GzipDecoder();

	@Test
	public void shouldDecodeEncoded() {

		Flowable<ByteBuffer> data = getDataFlux();
		Flowable<ByteBuffer> decodedEncoded = decoder.decode(encoder.encode(data));

		concat(decodedEncoded)
				.test()
				.assertResult(concat(getDataFlux()).blockingGet());
	}

	private Flowable<ByteBuffer> getDataFlux() {
		return Flowable.range(0, 100)
				.map(i -> ByteBuffer.wrap((i+"text"+(i * i)).getBytes()));
	}

	private static Single<ByteBuffer> concat(Flowable<ByteBuffer> data){
		return data.reduce(ByteBuffer.allocate(0), (data1, data2) -> concat(data1, data2));
	}

	private static ByteBuffer concat(ByteBuffer data1, ByteBuffer data2){
		return (ByteBuffer)ByteBuffer.allocate(data1.limit() + data2.limit()).put(data1).put(data2).position(0);
	}

}
