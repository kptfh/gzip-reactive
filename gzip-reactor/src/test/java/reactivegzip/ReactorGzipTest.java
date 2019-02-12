package reactivegzip;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class ReactorGzipTest {

	private final ReactorGzipEncoder encoder = new ReactorGzipEncoder();
	private final ReactorGzipDecoder decoder = new ReactorGzipDecoder();

	@Test
	public void shouldDecodeEncoded() {

		Flux<ByteBuffer> data = getDataFlux();
		Flux<ByteBuffer> decodedEncoded = decoder.decode(encoder.encode(data));

		StepVerifier.create(concat(decodedEncoded))
				.expectNext(concat(getDataFlux()).block())
				.verifyComplete();
	}

	private Flux<ByteBuffer> getDataFlux() {
		return Flux.fromStream(IntStream.range(0, 100)
				.mapToObj(i -> ByteBuffer.wrap((i+"text"+(i * i)).getBytes())));
	}

	private static Mono<ByteBuffer> concat(Flux<ByteBuffer> data){
		return data.reduce(ByteBuffer.allocate(0), (data1, data2) -> concat(data1, data2));
	}

	private static ByteBuffer concat(ByteBuffer data1, ByteBuffer data2){
		return (ByteBuffer)ByteBuffer.allocate(data1.limit() + data2.limit()).put(data1).put(data2).position(0);
	}

}
